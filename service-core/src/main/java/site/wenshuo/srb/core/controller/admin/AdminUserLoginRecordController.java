package site.wenshuo.srb.core.controller.admin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.core.pojo.entity.UserLoginRecord;
import site.wenshuo.srb.core.service.UserLoginRecordService;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
//@CrossOrigin
@Slf4j
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController {
    @Resource
    private UserLoginRecordService userLoginRecordService;

    @GetMapping("/listTop50/{id}")
    public R listTop50(@PathVariable Long id){
        List<UserLoginRecord> list = userLoginRecordService.listTop50(id);
        return R.ok().data("list",list);
    }
}

