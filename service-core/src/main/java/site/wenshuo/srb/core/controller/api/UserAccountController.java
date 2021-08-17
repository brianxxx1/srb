package site.wenshuo.srb.core.controller.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.base.util.JwtUtils;
import site.wenshuo.srb.core.hfb.RequestHelper;
import site.wenshuo.srb.core.service.UserAccountService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@Slf4j
@RequestMapping("/api/core/userAccount")
public class UserAccountController {

    @Resource
    private UserAccountService userAccountService;

    @PostMapping("/auth/commitCharge/{chargeAmt}")
    public R commitCharge(HttpServletRequest request, @PathVariable BigDecimal chargeAmt) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String formStr = userAccountService.commitCharge(userId, chargeAmt);
        return R.ok().data("formStr", formStr);
    }

    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> map = RequestHelper.switchMap(request.getParameterMap());
        if (RequestHelper.isSignEquals(map)) {
            if ("0001".equals(map.get("resultCode"))) {
                return userAccountService.notify(map);
            } else {
                return "success";
            }
        } else {
            return "fail";
        }
    }

    @GetMapping("/auth/getAccount")
    public R getAccount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal account = userAccountService.getAccount(userId);
        return R.ok().data("account", account);
    }

    @PostMapping("/auth/commitWithdraw/{fetchAmt}")
    public R commitWithdraw(@PathVariable BigDecimal fetchAmt, HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String formStr = userAccountService.commitWithdraw(fetchAmt, userId);
        return R.ok().data("formStr", formStr);
    }

    @PostMapping("/notifyWithdraw")
    public String notifyWithdraw(HttpServletRequest request) {
        Map<String, Object> map = RequestHelper.switchMap(request.getParameterMap());
        if (RequestHelper.isSignEquals(map)) {
            if ("0001".equals(map.get("resultCode"))) {
                userAccountService.notifyWithdraw(map);
                return "success";
            } else {
                log.info("Result Code Wrong");
                return "success";
            }
        } else {
            log.info("sign wrong");
            return "fail";
        }
    }
}

