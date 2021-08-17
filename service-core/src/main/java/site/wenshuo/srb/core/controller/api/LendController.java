package site.wenshuo.srb.core.controller.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.core.pojo.entity.Lend;
import site.wenshuo.srb.core.service.LendService;
import site.wenshuo.srb.core.service.UserAccountService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@Slf4j
@RequestMapping("/api/core/lend")
public class LendController {

    @Resource
    private LendService lendService;

    @Resource
    private UserAccountService userAccountService;


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
    @GetMapping("/getInterestCount/{invest}/{yearRate}/{totalmonth}/{returnMethod}")
    public R getInterestCount(@PathVariable BigDecimal invest, @PathVariable Integer returnMethod, @PathVariable Integer totalmonth, @PathVariable BigDecimal yearRate){
        BigDecimal  interestCount = lendService.getInterestCount(invest, yearRate, totalmonth, returnMethod);
        return R.ok().data("interestCount", interestCount);
    }





}

