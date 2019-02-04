package me.linx.vchat.controller.biz;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.model.validation.NickNameModel;
import me.linx.vchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController extends BaseBizController{

    @Autowired
    public UserController(UserService userService, HttpSession session) {
        super(userService, session);
    }

    @RequestMapping(value = "/editNickName", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult editNickName(@SuppressWarnings("unused") HttpServletRequest request, @ModelAttribute NickNameModel model) {
        try {
            return userService.editNickName(model, getCurrentUserId());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "/getUserProfile", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getUserProfile(@SuppressWarnings("unused") HttpServletRequest request, Long updateTime) {
        try {
            return userService.getUserProfile(getCurrentUserId(), updateTime);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult logout(@SuppressWarnings("unused") HttpServletRequest request) {
        try {
            return userService.handleLogout(getCurrentUserId());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "/loginTimeoutTest", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult loginTimeoutTest(@ModelAttribute NickNameModel model) {
        return JsonResult.failure(CodeMap.ErrorTokenFailed);
    }

}
