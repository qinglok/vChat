package me.linx.vchat.controller.biz;

import me.linx.vchat.service.UserService;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

@Controller
public abstract class BaseBizController {
    protected final UserService userService;
    protected final HttpSession session;

    public BaseBizController(UserService userService, HttpSession session) {
        this.userService = userService;
        this.session = session;
    }

    public void setCurrentUserId(Long id) {
        session.setAttribute("currentUserId", id);
    }

    public Long getCurrentUserId() {
        Object currentUserId = session.getAttribute("currentUserId");
        return session.getAttribute("currentUserId") == null ? 0L : Long.parseLong(currentUserId.toString());
    }
}
