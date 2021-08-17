package site.wenshuo.srb.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.wenshuo.common.exception.BusinessException;
import site.wenshuo.common.result.R;
import site.wenshuo.common.result.ResponseEnum;
import site.wenshuo.srb.service.FileService;

import javax.annotation.Resource;

@RestController
//@CrossOrigin
@RequestMapping("/api/oss/file")
@Slf4j
public class FileController {
    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile multipartFile, @RequestParam("module") String module){

        String URL = null;
        try {
            URL = fileService.upload(multipartFile.getInputStream(), module, multipartFile.getOriginalFilename());
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
        return R.ok().message("Upload Successful").data("URL",URL);
    }

    @DeleteMapping("/remove")
    public R remove(@RequestParam("url") String url){
        fileService.remove(url);
        return R.ok().message("Deletion Successful");
    }
}
