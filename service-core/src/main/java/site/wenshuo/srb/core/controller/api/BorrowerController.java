package site.wenshuo.srb.core.controller.api;


import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.base.util.JwtUtils;
import site.wenshuo.srb.core.pojo.vo.BorrowerVO;
import site.wenshuo.srb.core.service.BorrowerService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@RequestMapping("/api/core/borrower")
public class BorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @PostMapping("/auth/save")
    public R save(@RequestBody BorrowerVO borrowerVO, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowerService.saveBorrowerVOByUserId(borrowerVO,userId);
        return R.ok().message("save successful");
    }

    @GetMapping("/auth/getBorrowerStatus")
    public R getBorrowerStatus (HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowerService.getBorrowerStatus(userId);
        return R.ok().data("status",status);
    }

}

