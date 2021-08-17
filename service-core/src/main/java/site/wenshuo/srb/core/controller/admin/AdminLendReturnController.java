package site.wenshuo.srb.core.controller.admin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.core.pojo.entity.LendReturn;
import site.wenshuo.srb.core.service.LendReturnService;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@Slf4j
@RequestMapping("/admin/core/lendReturn")
public class AdminLendReturnController {
    @Resource
    private LendReturnService lendReturnService;

    @GetMapping("/list/{id}")
    public R list(@PathVariable Long id){
        List<LendReturn> list = lendReturnService.selectByLendId(id);
        return R.ok().data("list",list);
    }

}

