package me.linx.vchat.controller.app;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.model.validation.LoginAndVerifySecretModel;
import me.linx.vchat.model.validation.LoginModel;
import me.linx.vchat.model.validation.RegisterModel;
import me.linx.vchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AppController {

    private final UserService userService;

    @Autowired
    public AppController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String index() {
        return "/index";
    }

    @RequestMapping(value = "app/login", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult login(@ModelAttribute LoginModel loginModel, String deviceId, HttpServletRequest request) {
        try {
            return userService.handleLogin(loginModel, deviceId, request);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "app/loginAndVerifySecret", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult loginAndVerifySecret(HttpServletRequest request,@ModelAttribute LoginAndVerifySecretModel model) {
        try {
            return userService.loginAndVerifySecret(model);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "app/register", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult register(@ModelAttribute RegisterModel registerModel, String deviceId, HttpServletRequest request) {
        try {
            return userService.handleRegister(registerModel, deviceId, request);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

}
