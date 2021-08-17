package site.wenshuo.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.wenshuo.common.result.R;
import site.wenshuo.srb.base.util.JwtUtils;
import site.wenshuo.srb.core.hfb.RequestHelper;
import site.wenshuo.srb.core.pojo.vo.UserBindVo;
import site.wenshuo.srb.core.service.UserBindService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**

 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@RequestMapping("api/core/userBind")
@Slf4j
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @PostMapping("/auth/bind")
    public R bind (@RequestBody UserBindVo userBindVo, HttpServletRequest request){

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        String formStr = userBindService.commitBindUser(userBindVo,userId);
        return R.ok().data("formStr",formStr);
    }

    @PostMapping("/notify")
    public String notify(HttpServletRequest request){
        Map<String, Object> map = RequestHelper.switchMap(request.getParameterMap());
        if (!RequestHelper.isSignEquals(map)){
            log.error("signature is not authenticated"+ JSON.toJSONString(map));
            return "error";
        }else{
            userBindService.notify(map);
            return "success";
        }
        }
}

