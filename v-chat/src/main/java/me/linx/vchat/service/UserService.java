package me.linx.vchat.service;

import com.alibaba.fastjson.JSONObject;
import me.linx.vchat.aop.UploadAction;
import me.linx.vchat.bean.FileWrapper;
import me.linx.vchat.bean.TokenRecord;
import me.linx.vchat.bean.User;
import me.linx.vchat.bean.UserProfile;
import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.model.validation.LoginAndVerifySecretModel;
import me.linx.vchat.model.validation.LoginModel;
import me.linx.vchat.model.validation.NicknameModel;
import me.linx.vchat.model.validation.RegisterModel;
import me.linx.vchat.netty.session.IMDispatcher;
import me.linx.vchat.repository.FileWrapperRepository;
import me.linx.vchat.repository.UserRepository;
import me.linx.vchat.utils.PasswordUtils;
import me.linx.vchat.utils.StringUtils;
import me.linx.vchat.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;
    private FileWrapperRepository fileWrapperRepository;
    private TokenRecordService tokenRecordService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setFileWrapperRepository(FileWrapperRepository fileWrapperRepository) {
        this.fileWrapperRepository = fileWrapperRepository;
    }

    @Autowired
    public void setTokenRecordService(TokenRecordService tokenRecordService) {
        this.tokenRecordService = tokenRecordService;
    }

    /**
     * 修改头像
     *
     * @param fileName 文件名
     * @param user     {@link User}
     * @return {@link JsonResult}
     */
    @SuppressWarnings("unused")
    @UploadAction(action = "editHeadImage")
    JsonResult editHeadImage(String fileName, User user) {
        FileWrapper fileWrapper = fileWrapperRepository.findByName(fileName);
        user.getUserProfile().setAvatar(fileWrapper);
        userRepository.save(user);
        return JsonResult.success(fileName);
    }

    /**
     * 修改昵称
     *
     * @param model 参数模型
     * @param user  {@link User}
     * @return {@link JsonResult}
     */
    public JsonResult editNickname(NicknameModel model, User user) {
        // 验证参数
        ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(model);
        if (validationResult.hasErrors()) {
            return JsonResult.failure(CodeMap.ErrorParameter.value, validationResult.errorFormatMsg());
        }

        user.getUserProfile().setNickname(model.getNickname());
        userRepository.save(user);
        return JsonResult.success();
    }

    /**
     * 注册处理
     *
     * @param registerModel 注册参数模型
     * @param deviceId      设备ID
     * @return 注册结果
     */
    public JsonResult handleRegister(@NotNull RegisterModel registerModel, @NotNull String deviceId) {
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

        userProfile.setNickname(email.substring(0, email.indexOf("@")));
        userProfile.setSecretQuestion(registerModel.getSecretQuestion());
        userProfile.setSecretAnswer(registerModel.getSecretAnswer());

        user.setEmail(email);
        user.setPasswordEncode(PasswordUtils.generate(registerModel.getPassword(), PasswordUtils.generateSalt()));  // 密码盐加密
        user.setUserProfile(userProfile);

        //保存
        userRepository.save(user);

        // 根据userId， 设备标识生成token
//        String token = JwtUtils.sign(user.getId(), deviceId);
        // 保存登录信息
        TokenRecord tokenRecord = tokenRecordService.save(user, deviceId);

        JSONObject jsonObject = createJsonObject(user.getId(), userProfile);
        jsonObject.put("token", tokenRecord.getToken());

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
    public JsonResult handleLogin(@NotNull LoginModel loginModel, @NotNull String deviceId) {
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
                // 移除IM通道，通知异地登录
                IMDispatcher.unBindWithLoggedOther(user.getId());
                // 未设置密保，放行，并清除原有登录信息
                tokenRecordService.clearLoggedInfo(user);
            }
        }

        // 用户附属信息
        UserProfile userProfile = user.getUserProfile();

        String avatarPath = ""; // 头像
        FileWrapper avatarFile = userProfile.getAvatar();
        if (avatarFile != null) {
            avatarPath = avatarFile.getName();
        }

        // 根据userId， 设备标识生成token
//        String token = JwtUtils.sign(user.getId(), deviceId);
        // 保存登录信息
        TokenRecord tokenRecord = tokenRecordService.save(user, deviceId);

        //返回信息
        return getJsonResult(user, userProfile, avatarPath, tokenRecord.getToken());
    }

    private synchronized boolean checkEmail(@NotNull String email) {
        return StringUtils.isNotTrimEmpty(email) && userRepository.countByEmail(email) > 0;
    }

    public JsonResult handleLogout(User user) {
        // 移除IM通道
        IMDispatcher.unBind(user.getId());
        // 清除登录信息
        tokenRecordService.clearLoggedInfo(user);
        return JsonResult.success();
    }

    /**
     * 处理登录，并验证密保
     *
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

        // 移除IM通道，通知异地登录
        IMDispatcher.unBindWithLoggedOther(user.getId());
        // 清除旧的登录记录
        tokenRecordService.clearLoggedInfo(user.getId());

        String avatarPath = ""; // 头像
        FileWrapper avatarFile = userProfile.getAvatar();
        if (avatarFile != null) {
            avatarPath = avatarFile.getName();
        }

        // 根据userId， 设备标识生成token
//        String token = JwtUtils.sign(user.getId(), model.getDeviceId());
        // 保存登录信息
        TokenRecord tokenRecord = tokenRecordService.save(user, model.getDeviceId());

        //返回信息
        return getJsonResult(user, userProfile, avatarPath, tokenRecord.getToken());
    }

    /**
     * 获取当前用户信息
     *
     * @param user       {@link User}
     * @param updateTime 客户端更新时间
     * @return {@link JsonResult}
     */
    public JsonResult getUserProfile(User user, Long updateTime) {
        return getJsonResult(updateTime, user);
    }

    /**
     * 获取其他用户信息
     *
     * @param userId     {@link User} ID
     * @param updateTime @link User} updateTime
     * @return {@link JsonResult}
     */
    public JsonResult getUserProfile(Long userId, Long updateTime) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return getJsonResult(updateTime, user);
        }

        return JsonResult.failure(CodeMap.ErrorUserNotFound);
    }

    /**
     *  获取在线用户
     * @return @link JsonResult}
     */
    public JsonResult getActiveUserProfile(User currentUser) {
        List<JSONObject> list = new ArrayList<>();
        for (User user : IMDispatcher.getActiveUser()) {
            if (currentUser.getId() ==  user.getId())
                continue;

            UserProfile userProfile = user.getUserProfile();
            list.add(getJsonObject(userProfile));
        }
        return JsonResult.success(list);
    }

    private JsonResult getJsonResult(Long updateTime, User user) {
        UserProfile userProfile = user.getUserProfile();

        if (updateTime != null) {
            if (user.getUserProfile().getUpdateTime().getTime() > updateTime) {
                return getJsonResult(userProfile);
            } else {
                return JsonResult.success();
            }
        } else {
            return getJsonResult(userProfile);
        }
    }

    private JSONObject getJsonObject(UserProfile userProfile) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bizId", userProfile.getId());
        jsonObject.put("nickname", userProfile.getNickname());
        jsonObject.put("avatar", userProfile.getAvatar() != null ? userProfile.getAvatar().getName() : "");
        jsonObject.put("updateTime", userProfile.getUpdateTime().getTime());
        return jsonObject;
    }

    private JsonResult getJsonResult(UserProfile userProfile) {
        return JsonResult.success(getJsonObject(userProfile));
    }

    private JSONObject createJsonObject(Long userId, UserProfile userProfile) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bizId", userId);
        jsonObject.put("nickname", userProfile.getNickname());
        jsonObject.put("updateTime", userProfile.getUpdateTime().getTime());
        return jsonObject;
    }

    private JsonResult getJsonResult(User user, UserProfile userProfile, String avatarPath, String token) {
        JSONObject jsonObject = createJsonObject(user.getId(), userProfile);
        jsonObject.put("avatar", avatarPath);
        jsonObject.put("token", token);

        return JsonResult.success(jsonObject);
    }
}
