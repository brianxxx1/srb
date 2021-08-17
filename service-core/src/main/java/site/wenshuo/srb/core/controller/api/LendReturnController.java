package site.wenshuo.srb.core.controller.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.base.util.JwtUtils;
import site.wenshuo.srb.core.hfb.RequestHelper;
import site.wenshuo.srb.core.pojo.entity.LendReturn;
import site.wenshuo.srb.core.service.LendReturnService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@Slf4j
@RequestMapping("/api/core/lendReturn")
public class LendReturnController {
    @Resource
    private LendReturnService lendReturnService;
    @GetMapping("/list/{lendId}")
    public R list(@PathVariable Long lendId) {
        List<LendReturn> list = lendReturnService.selectByLendId(lendId);
        return R.ok().data("list", list);
    }

    @PostMapping("/auth/commitReturn/{id}")
    public R commitReturn(@PathVariable Long id, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String formStr = lendReturnService.commitReturn(id,userId);
        return R.ok().data("formStr",formStr);
    }

    @PostMapping("/notifyUrl")
    public String notifyUrl(HttpServletRequest request){
        Map<String, Object> map = RequestHelper.switchMap(request.getParameterMap());
        if (RequestHelper.isSignEquals(map)) {
            if ("0001".equals(map.get("resultCode"))){
                lendReturnService.notify(map);
                return "success";
            }else{
                log.warn("Result Code Wrong");
                return "success";
            }

        }else{
            log.warn("sign not correct");
            return "fail";
        }
    }
}

