package site.wenshuo.srb.core.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.core.pojo.entity.Dict;
import site.wenshuo.srb.core.service.DictService;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/core/dict")
public class DictController {

    @Resource
    private DictService dictService;

    @GetMapping("/findByDictCode/{dictCode}")
    public R findByDictCode(@PathVariable String dictCode){

        List<Dict> list = dictService.findByDictCode(dictCode);
        return R.ok().data("list",list);
    }
}
