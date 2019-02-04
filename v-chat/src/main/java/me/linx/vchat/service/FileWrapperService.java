package me.linx.vchat.service;

import me.linx.vchat.bean.FileWrapper;
import me.linx.vchat.constants.CodeMap;
import me.linx.vchat.model.JsonResult;
import me.linx.vchat.repository.FileWrapperRepository;
import me.linx.vchat.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Transactional
public class FileWrapperService {
    @Value("${upload-dir}")
    private String uploadDir;

    private final FileWrapperRepository fileWrapperRepository;

    @Autowired
    public FileWrapperService(FileWrapperRepository fileWrapperRepository) {
        this.fileWrapperRepository = fileWrapperRepository;
    }

    public JsonResult handleFile(MultipartFile file) {
        if (file.isEmpty()) {
            return JsonResult.failure(CodeMap.ErrorFileEmpty);
        }
        try{
            // 通过校验MD5检查文件是否已经存在，已经存在就返回已经存在的文件信息
            String md5Hex = DigestUtils.md5Hex(file.getInputStream());
            FileWrapper fileWrapper = fileWrapperRepository.findByMd5(md5Hex);
            if (fileWrapper != null){
                return JsonResult.success(fileWrapper.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorFileUploadFailure);
        }

        // 原版文件名
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isTrimEmpty(originalFilename)){
            originalFilename = "";
        }
        // 保存路径
        String path = uploadDir;
        // 后缀名
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 保存文件名
        String fileName = UUID.randomUUID().toString() + suffixName;

        File dest = new File(path, fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            dest.getParentFile().mkdirs();// 新建文件夹
        }
        try {
            file.transferTo(dest);// 文件写入

            FileWrapper fileWrapper = new FileWrapper();
            String md5Hex = DigestUtils.md5Hex(file.getInputStream());

            fileWrapper.setName(fileName);
            fileWrapper.setMd5(md5Hex);
            fileWrapperRepository.save(fileWrapper);

            return JsonResult.success(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return JsonResult.failure(CodeMap.ErrorFileUploadFailure);
        }
    }
}
