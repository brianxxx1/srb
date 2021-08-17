package site.wenshuo.srb.core.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.core.pojo.entity.LendItem;
import site.wenshuo.srb.core.service.LendItemService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/core/lendItem")
public class AdminLendItemController {

    @Resource
    private LendItemService lendItemService;
    @GetMapping("/list/{id}")
    public R list(@PathVariable Long id){
        List<LendItem> lendItems = lendItemService.selectByLendId(id);
        return R.ok().data("list",lendItems);
    }
}
