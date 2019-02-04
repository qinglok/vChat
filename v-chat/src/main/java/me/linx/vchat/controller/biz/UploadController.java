package me.linx.vchat.controller.biz;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.service.FileWrapperService;
import me.linx.vchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/biz")
public class UploadController extends BaseBizController{

    private final FileWrapperService fileWrapperService;

    @Autowired
    public UploadController(UserService userService, HttpSession session, FileWrapperService fileWrapperService) {
        super(userService, session);
        this.fileWrapperService = fileWrapperService;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult upload(@SuppressWarnings("unused") HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            return fileWrapperService.handleFile(file);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorFileUploadFailure);
        }
    }
}
