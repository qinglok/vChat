package me.linx.vchat.controller.app;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.model.validation.SignModel;
import me.linx.vchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/app")
public class AppController {


    private final UserService userService;

    @Autowired
    public AppController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult login(@ModelAttribute SignModel signModel, String deviceId) {
        try {
            Thread.sleep(500);

            return userService.handleLogin(signModel, deviceId);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult register(@ModelAttribute SignModel signModel, String deviceId) {
        try {
            Thread.sleep(500);

            return userService.handleRegister(signModel, deviceId);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

}
