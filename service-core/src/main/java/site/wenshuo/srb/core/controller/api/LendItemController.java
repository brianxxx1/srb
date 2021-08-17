package site.wenshuo.srb.core.controller.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.base.util.JwtUtils;
import site.wenshuo.srb.core.hfb.RequestHelper;
import site.wenshuo.srb.core.pojo.entity.LendItem;
import site.wenshuo.srb.core.pojo.vo.InvestVo;
import site.wenshuo.srb.core.service.LendItemService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@Slf4j
@RequestMapping("/api/core/lendItem")
public class LendItemController {
    @Resource
    private LendItemService lendItemService;

    @PostMapping("/auth/commitInvest")
    public R commitInvest(@RequestBody InvestVo investVO, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String userName = JwtUtils.getUserName(token);
        investVO.setInvestUserId(userId);
        investVO.setInvestName(userName);

        String formStr = lendItemService.commitInvest(investVO);
        return R.ok().data("formStr", formStr);
    }

    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> map = RequestHelper.switchMap(request.getParameterMap());
        if (RequestHelper.isSignEquals(map)) {
            if ("0001".equals(map.get("resultCode"))) {
                lendItemService.notify(map);
                return "success";
            } else {
                log.error("HFB call failed with a returnCode not being 0001");
                return "fail";
            }
        } else {
            log.error("Signature Authentication failed");
            return "fail";
        }
    }
    @GetMapping("/list/{id}")
    public R list(@PathVariable Long id){
        List<LendItem> lendItems = lendItemService.selectByLendId(id);
        return R.ok().data("list",lendItems);
    }
}

