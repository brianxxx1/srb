package site.wenshuo.srb.core.controller.admin;


import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.wenshuo.common.exception.BusinessException;
import site.wenshuo.common.result.R;
import site.wenshuo.common.result.ResponseEnum;
import site.wenshuo.srb.core.pojo.dto.excelDictDTO;
import site.wenshuo.srb.core.pojo.entity.Dict;
import site.wenshuo.srb.core.service.DictService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@RequestMapping("/admin/core/dict")
@Slf4j
//@CrossOrigin
public class AdminDictController {

    @Resource
    private DictService dictService;

    @PostMapping("/import")
    public R batchImport(@RequestParam("file") MultipartFile file){
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            dictService.importData(inputStream);
            return R.ok().message("Batch Import Successful");
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }

    @GetMapping("/export")
    public void download(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition","attachment;filename*=utf-8" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), excelDictDTO.class).sheet("Info Dict").doWrite(dictService.listDictData());
    }

    @GetMapping("/listByParentId/{parentId}")
    public R listByParentId(@PathVariable("parentId") Long parentId){
        List<Dict> dicts = dictService.listByParentId(parentId);
        return R.ok().data("list",dicts);
    }
}

