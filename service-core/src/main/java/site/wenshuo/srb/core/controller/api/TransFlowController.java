package site.wenshuo.srb.core.controller.api;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.base.util.JwtUtils;
import site.wenshuo.srb.core.pojo.entity.TransFlow;
import site.wenshuo.srb.core.service.TransFlowService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@RequestMapping("/api/core/transFlow")
public class TransFlowController {

    @Resource
    private TransFlowService transFlowService;

    @GetMapping("/list")
    public R list(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<TransFlow> list =transFlowService.selectByUserId(userId);
        return R.ok().data("list",list);
    }
}

