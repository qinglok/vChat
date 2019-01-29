package me.linx.vchat.service;

import com.alibaba.fastjson.JSONObject;
import me.linx.vchat.aop.UploadAction;
import me.linx.vchat.bean.FileWrapper;
import me.linx.vchat.bean.User;
import me.linx.vchat.bean.UserProfile;
import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.model.validation.SignModel;
import me.linx.vchat.repository.FileWrapperRepository;
import me.linx.vchat.repository.UserRepository;
import me.linx.vchat.utils.JwtUtils;
import me.linx.vchat.utils.PasswordUtils;
import me.linx.vchat.utils.StringUtils;
import me.linx.vchat.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FileWrapperRepository fileWrapperRepository;


    @Autowired
    public UserService(UserRepository userRepository, FileWrapperRepository fileWrapperRepository) {
        this.userRepository = userRepository;
        this.fileWrapperRepository = fileWrapperRepository;
    }

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
     * 注册处理
     *
     * @param signModel 注册参数模型
     * @param deviceId  设备ID
     * @return 注册结果
     */
    public JsonResult handleRegister(@NotNull SignModel signModel, @NotNull String deviceId) {
        // 验证参数
        ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(signModel);
        if (validationResult.hasErrors()) {
            return JsonResult.failure(CodeMap.ErrorParameter.value, validationResult.errorFormatMsg());
        }

        String email = signModel.getEmail();

        //检查邮箱是否已被注册
        if (checkEmail(email)) {
            return JsonResult.failure(CodeMap.ErrorEmailWasUsed);
        }

        // 创建用户
        User user = new User();
        UserProfile userProfile = new UserProfile();

        userProfile.setNickName(email.substring(0, email.indexOf("@")));
        user.setEmail(email);
        user.setPasswordEncode(PasswordUtils.generate(signModel.getPassword(), PasswordUtils.generateSalt()));  // 密码盐加密
        user.setUserProfile(userProfile);

        //保存
        userRepository.save(user);

        //返回信息
        return JsonResult.success(createJsonObject(user, userProfile.getNickName(), deviceId));
    }

    public JsonResult handleLogin(@NotNull SignModel signModel, @NotNull String deviceId) {
        // 验证参数
        ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(signModel);
        if (validationResult.hasErrors()) {
            return JsonResult.failure(CodeMap.ErrorParameter.value, validationResult.errorFormatMsg());
        }

        User user = userRepository.findByEmail(signModel.getEmail());

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
        if (!PasswordUtils.check(signModel.getPassword(), passwordEncode)) {
            return JsonResult.failure(CodeMap.ErrorPassword);
        }

        // 用户附属信息
        UserProfile userProfile = user.getUserProfile();

        String headImgPath = ""; // 头像
        FileWrapper headImgFile = userProfile.getHeadImg();
        if (headImgFile != null) {
            headImgPath = headImgFile.getName();
        }

        //返回信息
        JSONObject jsonObject = createJsonObject(user, userProfile.getNickName(), deviceId);
        jsonObject.put("email", user.getEmail());
        jsonObject.put("headImg", headImgPath);

        return JsonResult.success(jsonObject);
    }

    private JSONObject createJsonObject(User user, String nickName, String deviceId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bizId", user.getId());
        jsonObject.put("token", JwtUtils.sign(user.getId(), deviceId));
        jsonObject.put("nickName", nickName);
        jsonObject.put("createTime", user.getCreateTime().getTime());
        jsonObject.put("updateTime", user.getUpdateTime().getTime());
        return jsonObject;
    }

    private synchronized boolean checkEmail(@NotNull String email) {
        return StringUtils.isNotTrimEmpty(email) && userRepository.countByEmail(email) > 0;
    }
}
