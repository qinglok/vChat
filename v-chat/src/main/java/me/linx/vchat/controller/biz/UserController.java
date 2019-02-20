package me.linx.vchat.controller.biz;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.model.validation.NicknameModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController extends BaseBizController{

    @RequestMapping(value = "/editNickname", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult editNickname(@SuppressWarnings("unused") HttpServletRequest request, @ModelAttribute NicknameModel model) {
        try {
            return userService.editNickname(model, getCurrentUser());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "/getUserProfile", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getUserProfile(@SuppressWarnings("unused") HttpServletRequest request, Long updateTime) {
        try {
            return userService.getUserProfile(getCurrentUser(), updateTime);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "/getUserProfileById", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getUserProfileById(@SuppressWarnings("unused") HttpServletRequest request, Long userId, Long updateTime) {
        try {
            return userService.getUserProfile(userId, updateTime);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "/getActiveUserProfile", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getActiveUserProfile(@SuppressWarnings("unused") HttpServletRequest request) {
        try {
            return userService.getActiveUserProfile(getCurrentUser());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult logout(@SuppressWarnings("unused") HttpServletRequest request) {
        try {
            return userService.handleLogout(getCurrentUser());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorSys);
        }
    }

    @RequestMapping(value = "/loginTimeoutTest", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult loginTimeoutTest(@ModelAttribute NicknameModel model) {
        return JsonResult.failure(CodeMap.ErrorTokenFailed);
    }

}
