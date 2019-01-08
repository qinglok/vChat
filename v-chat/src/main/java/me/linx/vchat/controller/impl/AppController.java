package me.linx.vchat.controller.impl;

import com.alibaba.fastjson.JSONObject;
import me.linx.vchat.bean.User;
import me.linx.vchat.bean.UserProfile;
import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.controller.BaseController;
import me.linx.vchat.model.ResultEntity;
import me.linx.vchat.repository.UserRepository;
import me.linx.vchat.utils.JwtUtils;
import me.linx.vchat.utils.PasswordUtils;
import me.linx.vchat.utils.StringUtils;
import me.linx.vchat.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/app")
public class AppController extends BaseController {
    private final UserRepository userRepository;

    @Autowired
    public AppController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResultEntity register(@ModelAttribute User user, String deviceId) {
        try {
            Thread.sleep(1000);
            ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(user);

            if (validationResult.hasErrors()) {
                return failure(CodeMap.ErrorParameter.value, validationResult.errorFormatMsg());
            }

            synchronized (this) {
                //检查邮箱是否已被注册
                if (userRepository.countByEmail(user.getEmail()) > 0) {
                    return failure(CodeMap.ErrorEmailWasUsed);
                }
            }

            UserProfile userProfile = new UserProfile();

            //密码盐加密
            userProfile.setPasswordEncode(PasswordUtils.generate(user.getPassword(), PasswordUtils.generateSalt()));

            user.setUserProfile(userProfile);
            //不保存未加密的原始密码,填充假数据
            user.setPassword("xxxxxxxx");

            //保存到数据库
            userRepository.save(user);

            //返回信息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bizId", user.getId());
            jsonObject.put("token" ,JwtUtils.sign(user.getId(), deviceId));
            jsonObject.put("createTime", user.getCreateTime().getTime());
            jsonObject.put("updateTime", user.getUpdateTime().getTime());

            return success(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return failure(CodeMap.ErrorUnknown);
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultEntity login(@ModelAttribute User user, String deviceId) {
        try {
            Thread.sleep(1000);
            ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(user);

            if (validationResult.hasErrors()) {
                return failure(CodeMap.ErrorParameter.value, validationResult.errorFormatMsg());
            }

            User u = userRepository.findByEmail(user.getEmail());

            if (u == null){
                return failure(CodeMap.ErrorEmailUnUsed);
            }

            UserProfile userProfile = u.getUserProfile();
            if (userProfile == null || StringUtils.isTrimEmpty(userProfile.getPasswordEncode())){
                return failure(CodeMap.ErrorEmailUnUsed);
            }

            if (!PasswordUtils.check(user.getPassword(), userProfile.getPasswordEncode())){
                return failure(CodeMap.ErrorPassword);
            }

            //返回信息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bizId", u.getId());
            jsonObject.put("email", u.getEmail());
            jsonObject.put("token" ,JwtUtils.sign(u.getId(), deviceId));
            jsonObject.put("createTime", u.getCreateTime().getTime());
            jsonObject.put("updateTime", u.getUpdateTime().getTime());

            return success(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return failure(CodeMap.ErrorUnknown);
        }
    }
}
