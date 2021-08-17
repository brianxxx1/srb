package site.wenshuo.srb.core.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.core.pojo.entity.BorrowInfo;
import site.wenshuo.srb.core.pojo.vo.BorrowInfoApprovalVO;
import site.wenshuo.srb.core.service.BorrowInfoService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin/core/borrowInfo")
@RestController
@Slf4j
public class AdminBorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @GetMapping("/list")
    public R list(){
        List<BorrowInfo> list = borrowInfoService.selectList();
        return R.ok().data("list",list);
    }

    @GetMapping("/show/{id}")
    public R show(@PathVariable Integer id){
        Map<String,Object> map = borrowInfoService.getBorrowInfoDetail(id);
        return R.ok().data("map", map);
    }

    @PostMapping("/approval")
    public R approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO){
        borrowInfoService.approval(borrowInfoApprovalVO);
        return R.ok().message("success");
    }
}
