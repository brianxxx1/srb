package site.wenshuo.srb.core.controller.admin;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.exception.Assert;
import site.wenshuo.common.result.R;
import site.wenshuo.common.result.ResponseEnum;
import site.wenshuo.srb.core.pojo.entity.IntegralGrade;
import site.wenshuo.srb.core.service.IntegralGradeService;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
//@CrossOrigin
@RequestMapping("/admin/core/IntegralGrade")
public class AdminIntegralGradeController {
    @Resource
     private IntegralGradeService integralGradeService;

    @GetMapping("/list")
    public R listAll (){
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list).message("List Acquired Successful");
    }

    @DeleteMapping("/remove/{id}")
    public R removeById(@PathVariable Long id){
        boolean b = integralGradeService.removeById(id);
        if (b){
            return R.ok().message("Deletion Successful");
        }else {
            return R.error().message("Deletion Failed");
        }
    }

    @ApiOperation("new a credit level")
    @PostMapping("/save")
    public R save(@RequestBody IntegralGrade integralGrade)
    {
        boolean save = integralGradeService.save(integralGrade);
        Assert.notNull(integralGrade.getBorrowAmount(),ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        if (save) {
            return R.ok().message("Save Successful");
        }else{
            return R.error().message("Save Failed");
        }
    }

    @ApiOperation("get a credit level by id")
    @GetMapping("/get/{id}")
    public R getById(@PathVariable Long id){
        IntegralGrade byId = integralGradeService.getById(id);
        if (byId != null){
            return R.ok().data("record",byId);
        }else {
            return R.error().message("Data Retrieval Failed");
        }
    }

    @ApiOperation("update a credit level by id")
    @PutMapping("/update")
    public R updateById(@RequestBody IntegralGrade integralGrade){
        boolean b = integralGradeService.updateById(integralGrade);
        if (b){
            return R.ok().message("Update Success");
        }else {
            return R.error().message("Update Failed");
        }
    }


}
