package me.linx.vchat.controller.biz;

import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.service.FileWrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/biz")
public class UploadController {

    private final FileWrapperService fileWrapperService;

    @Autowired
    public UploadController(FileWrapperService fileWrapperService) {
        this.fileWrapperService = fileWrapperService;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult editHeadImg(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            return fileWrapperService.handleFile(file);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorFileUploadFailure);
        }
    }
}
