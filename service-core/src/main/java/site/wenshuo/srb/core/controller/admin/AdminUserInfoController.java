package site.wenshuo.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.core.pojo.entity.UserInfo;
import site.wenshuo.srb.core.pojo.query.UserInfoQuery;
import site.wenshuo.srb.core.service.UserInfoService;

import javax.annotation.Resource;

/**

 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@RequestMapping("/admin/core/userInfo")
@Slf4j
//@CrossOrigin
public class AdminUserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/listPage/{list}/{limit}")
    public R listPage(@ApiParam(value = "queryObject", required = false) UserInfoQuery userInfoQuery,
                      @PathVariable("list") Integer page,
                      @PathVariable("limit") Integer limit){
        Page<UserInfo> userInfoPage = new Page<>(page,limit);
        IPage<UserInfo> pageModel =  userInfoService.listPage(userInfoPage,userInfoQuery);
        return R.ok().data("pageModel",pageModel);
    }

    @PutMapping("/lock/{id}/{status}")
    public R lock(@PathVariable Long id, @PathVariable Integer status){
        userInfoService.lock(id,status);
        return R.ok().message(status==1?"Unlock Successful":"Lock Successful");
    }



}

