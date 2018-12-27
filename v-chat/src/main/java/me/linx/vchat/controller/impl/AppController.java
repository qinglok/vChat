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
import me.linx.vchat.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/app")
public class AppController extends BaseController {
    private final UserRepository userRepository;

    @Autowired
    public AppController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @RequestMapping(value = "/register")
    @ResponseBody
    public ResultEntity register(@ModelAttribute User user, String deviceId) {
        try {
            ValidationUtils.ValidationResult validationResult = ValidationUtils.validateEntity(user);

            if (validationResult.hasErrors()) {
                return failure(CodeMap.ERROR_PARAMETER, validationResult.errorFormatMsg());
            }

            synchronized (this) {
                //检查邮箱是否已被注册
                if (userRepository.countByEmail(user.getEmail()) > 0) {
                    return failure(CodeMap.ERROR_REGISTER_EMAIL_WAS_USED);
                }
            }

            //密码盐加密
            UserProfile userProfile = new UserProfile();
            userProfile.setPasswordEncode(PasswordUtils.generate(user.getPassword()));

            user.setUserProfile(userProfile);
            //不保存未加密的原始密码,填充假数据
            user.setPassword("xxxxxxxx");

            //保存到数据库
            userRepository.save(user);

            //返回信息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bid", user.getId());
            //签发Token
            jsonObject.put("token", JwtUtils.sign(user.getId(), deviceId));
            jsonObject.put("createTime", user.getCreateTime().getTime());
            jsonObject.put("updateTime", user.getUpdateTime().getTime());

            ResultEntity resultEntity = new ResultEntity();
            resultEntity.setData(jsonObject);

            return success(resultEntity);
        }catch (Exception e){
            e.printStackTrace();
            return failure(CodeMap.ERROR_UNKNOWN);
        }
    }
}
