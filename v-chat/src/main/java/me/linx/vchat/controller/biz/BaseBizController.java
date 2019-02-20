package me.linx.vchat.controller.biz;

import me.linx.vchat.bean.User;
import me.linx.vchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public abstract class BaseBizController {

    UserService userService;
    private HttpSession session;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setSession(HttpSession session) {
        this.session = session;
    }

    public void setCurrentUser(User user) {
        session.setAttribute("currentUser", user);
    }

    public User getCurrentUser() {
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null){
            return null;
        }

        if (currentUser instanceof User){
            return (User) currentUser;
        }

        return null;
    }
}
