package site.wenshuo.srb.core.controller.admin;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.core.pojo.entity.Lend;
import site.wenshuo.srb.core.service.LendService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@Slf4j
@RequestMapping("/admin/core/lend")
public class AdminLendController {

    @Resource
    private LendService lendService;

    @GetMapping("/list")
    public R list(){
        List<Lend> list = lendService.selectList();
        return R.ok().data("list",list);
    }

    @GetMapping("/show/{id}")
    public R show(@PathVariable String id){
        Map<String, Object> map= lendService.show(id);
        return R.ok().data("lendDetail",map);
    }

    @GetMapping("/makeLoan/{id}")
    public R makeLoan(@PathVariable Long id){
        lendService.makeLoan(id);
        return R.ok().message("Transaction Done");
    }

}

