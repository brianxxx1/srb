package site.wenshuo.srb.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.core.pojo.entity.Borrower;
import site.wenshuo.srb.core.pojo.vo.BorrowerApprovalVO;
import site.wenshuo.srb.core.pojo.vo.BorrowerDetailVO;
import site.wenshuo.srb.core.service.BorrowerService;

import javax.annotation.Resource;

@SuppressWarnings("ALL")
@RestController
@Slf4j
@RequestMapping("/admin/core/borrower")

public class AdminBorrowerController {
    @Resource
    private BorrowerService borrowerService;

    @GetMapping("/list/{page}/{limit}")
    public R listPage(@PathVariable("page") Long page, @PathVariable("limit") Long limit, @RequestParam String keyword){
        Page<Borrower> borrowerPage = new Page<>(page,limit);
        IPage<Borrower> borrowerIPage = borrowerService.listPage(borrowerPage,keyword);
        return R.ok().data("pageModel",borrowerIPage);
    }

    @GetMapping("/show/{id}")
    public R show(@PathVariable("id") Long id){
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVoById(id);
        return R.ok().data("borrowerDetailVO", borrowerDetailVO);
    }

    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO){
        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("Verification Successful");
    }
}
