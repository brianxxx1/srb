package site.wenshuo.srb.core.controller.api;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.base.util.JwtUtils;
import site.wenshuo.srb.core.pojo.entity.LendItemReturn;
import site.wenshuo.srb.core.service.LendItemReturnService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@RequestMapping("/api/core/lendItemReturn")
public class LendItemReturnController {
    @Resource
    private LendItemReturnService lendItemReturnService;

    @GetMapping("/auth/list/{id}")
    public R selectByLendId(@PathVariable Long id, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        List<LendItemReturn> list = lendItemReturnService.selectByLendId(id,userId);
        return R.ok().data("list",list);
    }

}

