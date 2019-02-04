package me.linx.vchat.service;

import com.alibaba.fastjson.JSONObject;
import me.linx.vchat.aop.UploadAction;
import me.linx.vchat.bean.FileWrapper;
import me.linx.vchat.bean.User;
import me.linx.vchat.bean.UserProfile;
import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.model.validation.LoginAndVerifySecretModel;
import me.linx.vchat.model.validation.NickNameModel;
import me.linx.vchat.model.validation.LoginModel;
import me.linx.vchat.model.validation.RegisterModel;
import me.linx.vchat.repository.FileWrapperRepository;
import me.linx.vchat.repository.UserRepository;
import me.linx.vchat.utils.JwtUtils;
import me.linx.vchat.utils.PasswordUtils;
import me.linx.vchat.utils.StringUtils;
import me.linx.vchat.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final FileWrapperRepository fileWrapperRepository;
    private final TokenRecordService tokenRecordService;

    @Autowired
    public UserService(UserRepository userRepository, FileWrapperRepository fileWrapperRepository, TokenRecordService tokenRecordService) {
        this.userRepository = userRepository;
        this.fileWrapperRepository = fileWrapperRepository;
        this.tokenRecordService = tokenRecordService;
    }

    /**
     * 修改头像
     *
     * @param fileName 文件名
     * @param userId   userId
     * @return 结果
     */
    @UploadAction(action = "editHeadImage")
    public JsonResult editHeadImage(String fileName, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserProfile userProfile = user.getUserProfile();

            FileWrapper fileWrapper = fileWrapperRepository.findByName(fileName);
            userProfile.setHeadImg(fileWrapper);
            userRepository.save(user);
        }
        return JsonResult.success(fileName);
    }

    /**
     * 修改昵称
     *
     * @param model  参数
     * @param userId userId
     * @return 结果
     */
    public JsonResult editNickName(NickNameModel model, Long userId) {
        // 验证参数
        ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(model);
        if (validationResult.hasErrors()) {
            return JsonResult.failure(CodeMap.ErrorParameter.value, validationResult.errorFormatMsg());
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.getUserProfile().setNickName(model.getNickName());
            userRepository.save(user);
            return JsonResult.success();
        }
        return JsonResult.failure(CodeMap.ErrorSys);
    }

    /**
     * 注册处理
     *
     * @param registerModel 注册参数模型
     * @param deviceId      设备ID
     * @return 注册结果
     */
    public JsonResult handleRegister(@NotNull RegisterModel registerModel, @NotNull String deviceId, HttpServletRequest request) {
        // 验证参数
        ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(registerModel);
        if (validationResult.hasErrors()) {
            return JsonResult.failure(CodeMap.ErrorParameter.value, validationResult.errorFormatMsg());
        }

        String email = registerModel.getEmail();

        //检查邮箱是否已被注册
        if (checkEmail(email)) {
            return JsonResult.failure(CodeMap.ErrorEmailWasUsed);
        }

        // 创建用户
        User user = new User();
        UserProfile userProfile = new UserProfile();

        userProfile.setNickName(email.substring(0, email.indexOf("@")));
        userProfile.setSecretQuestion(registerModel.getSecretQuestion());
        userProfile.setSecretAnswer(registerModel.getSecretAnswer());

        user.setEmail(email);
        user.setPasswordEncode(PasswordUtils.generate(registerModel.getPassword(), PasswordUtils.generateSalt()));  // 密码盐加密
        user.setUserProfile(userProfile);

        //保存
        userRepository.save(user);

        // 根据userId， 设备标识生成token
        String token = JwtUtils.sign(user.getId(), deviceId);
        // 保存登录信息
        tokenRecordService.save(user, token, deviceId);

        JSONObject jsonObject = createJsonObject(user, userProfile.getNickName(), deviceId);
        jsonObject.put("token", token);

        //返回信息
        return JsonResult.success(jsonObject);
    }

    /**
     * 登录处理
     *
     * @param loginModel 注册参数模型
     * @param deviceId   设备ID
     * @return 注册结果
     */
    public JsonResult handleLogin(@NotNull LoginModel loginModel, @NotNull String deviceId, HttpServletRequest request) {
        // 验证参数
        ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(loginModel);
        if (validationResult.hasErrors()) {
            return JsonResult.failure(CodeMap.ErrorParameter.value, validationResult.errorFormatMsg());
        }

        User user = userRepository.findByEmail(loginModel.getEmail());

        // 用户不存在
        if (user == null) {
            return JsonResult.failure(CodeMap.ErrorEmailUnUsed);
        }

        // 加密后密码
        String passwordEncode = user.getPasswordEncode();

        // 密码异常
        if (StringUtils.isTrimEmpty(passwordEncode)) {
            return JsonResult.failure(CodeMap.ErrorEmailUnUsed);
        }

        // 校验密码
        if (!PasswordUtils.check(loginModel.getPassword(), passwordEncode)) {
            return JsonResult.failure(CodeMap.ErrorPassword);
        }

        Boolean passDevice = tokenRecordService.verifyDevice(user);
        // 已经在其他设备登陆过
        if (!passDevice) {
            // 已经设置密保，不允许登录
            if (StringUtils.isNotTrimEmpty(user.getUserProfile().getSecretQuestion())) {
                return JsonResult.failure(CodeMap.ErrorLoggedOther.value, user.getUserProfile().getSecretQuestion());
            } else {
                // 未设置密保，放行，并清除原有登录信息
                tokenRecordService.clearLoggedInfo(user);
            }
        }

        // 用户附属信息
        UserProfile userProfile = user.getUserProfile();

        String headImgPath = ""; // 头像
        FileWrapper headImgFile = userProfile.getHeadImg();
        if (headImgFile != null) {
            headImgPath = headImgFile.getName();
        }

        // 根据userId， 设备标识生成token
        String token = JwtUtils.sign(user.getId(), deviceId);
        // 保存登录信息
        tokenRecordService.save(user, token, deviceId);

        //返回信息
        JSONObject jsonObject = createJsonObject(user, userProfile.getNickName(), deviceId);
        jsonObject.put("email", user.getEmail());
        jsonObject.put("headImg", headImgPath);
        jsonObject.put("token", token);

        return JsonResult.success(jsonObject);
    }

    private JSONObject createJsonObject(User user, String nickName, String deviceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bizId", user.getId());
        jsonObject.put("nickName", nickName);
        jsonObject.put("createTime", user.getCreateTime().getTime());
        jsonObject.put("updateTime", user.getUpdateTime().getTime());
        return jsonObject;
    }

    private synchronized boolean checkEmail(@NotNull String email) {
        return StringUtils.isNotTrimEmpty(email) && userRepository.countByEmail(email) > 0;
    }

    public JsonResult handleLogout(Long currentUserId) {
        // 清除登录信息
        tokenRecordService.clearLoggedInfo(currentUserId);
        return JsonResult.success();
    }

    /**
     *  处理登录，并验证密保
     * @param model 参数
     * @return {@link JsonResult}
     */
    public JsonResult loginAndVerifySecret(LoginAndVerifySecretModel model) {
        // 验证参数
        ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(model);
        if (validationResult.hasErrors()) {
            return JsonResult.failure(CodeMap.ErrorParameter.value, validationResult.errorFormatMsg());
        }

        User user = userRepository.findByEmail(model.getEmail());

        // 用户不存在
        if (user == null) {
            return JsonResult.failure(CodeMap.ErrorEmailUnUsed);
        }

        // 加密后密码
        String passwordEncode = user.getPasswordEncode();

        // 密码异常
        if (StringUtils.isTrimEmpty(passwordEncode)) {
            return JsonResult.failure(CodeMap.ErrorEmailUnUsed);
        }

        // 校验密码
        if (!PasswordUtils.check(model.getPassword(), passwordEncode)) {
            return JsonResult.failure(CodeMap.ErrorPassword);
        }

        // 用户附属信息
        UserProfile userProfile = user.getUserProfile();

        // 验证密保
        if (!model.getAnswer().equals(userProfile.getSecretAnswer()))
            return JsonResult.failure(CodeMap.ErrorVerifySecret);
        // 清除旧的登录记录
        tokenRecordService.clearLoggedInfo(user.getId());

        String headImgPath = ""; // 头像
        FileWrapper headImgFile = userProfile.getHeadImg();
        if (headImgFile != null) {
            headImgPath = headImgFile.getName();
        }

        // 根据userId， 设备标识生成token
        String token = JwtUtils.sign(user.getId(), model.getDeviceId());
        // 保存登录信息
        tokenRecordService.save(user, token, model.getDeviceId());

        //返回信息
        JSONObject jsonObject = createJsonObject(user, userProfile.getNickName(), model.getDeviceId());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("headImg", headImgPath);
        jsonObject.put("token", token);

        return JsonResult.success(jsonObject);
    }
}
