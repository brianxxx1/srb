package site.wenshuo.srb.core.controller.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import site.wenshuo.common.exception.Assert;
import site.wenshuo.common.result.R;
import site.wenshuo.common.result.ResponseEnum;
import site.wenshuo.common.util.RegexValidateUtils;
import site.wenshuo.srb.base.util.JwtUtils;
import site.wenshuo.srb.core.pojo.vo.LoginVO;
import site.wenshuo.srb.core.pojo.vo.RegisterVO;
import site.wenshuo.srb.core.pojo.vo.UserIndexVO;
import site.wenshuo.srb.core.pojo.vo.UserInfoVO;
import site.wenshuo.srb.core.service.UserInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**

 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@RestController
@RequestMapping("/api/core/userInfo")
@Slf4j
//@CrossOrigin
public class UserInfoController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/checkMobile/{mobile}")
    public Boolean checkMobile(@PathVariable String mobile){
        return userInfoService.checkMobile(mobile);
    }



    @PostMapping("/register")
    public R register (@RequestBody RegisterVO registerVO){
        String password = registerVO.getPassword();
        String mobile = registerVO.getMobile();
        String code1 = registerVO.getCode();


        Assert.notEmpty(mobile,ResponseEnum.MOBILE_ERROR);
        Assert.notEmpty(password,ResponseEnum.PASSWORD_NULL_ERROR);
        Assert.notEmpty(code1,ResponseEnum.CODE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(registerVO.getMobile()),ResponseEnum.MOBILE_ERROR);

        String code = (String) redisTemplate.opsForValue().get("srb:sms:code" + registerVO.getMobile());
        Assert.equals(code,registerVO.getCode(), ResponseEnum.CODE_ERROR);

        userInfoService.register(registerVO);
        return R.ok().message("Register Successful");
    }

    @PostMapping("/login")
    public R login(@RequestBody LoginVO loginVo, HttpServletRequest httpServletRequest){
        String ip = httpServletRequest.getRemoteAddr();

        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        Assert.notNull(mobile,ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notNull(password,ResponseEnum.PASSWORD_NULL_ERROR);

        UserInfoVO userInfoVO = userInfoService.login(loginVo,ip);

        return R.ok().message("Login OK").data("userInfo",userInfoVO);
    }

    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request){
        String token = request.getHeader("token");
        boolean b = JwtUtils.checkToken(token);
        if (!b){
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }else{
            return R.ok();
        }
    }

    @GetMapping("/auth/getIndexUserInfo")
    public R getIndexUserInfo(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        UserIndexVO userIndexVO = userInfoService.getUserInfoById(userId);
        return R.ok().data("userIndexVO",userIndexVO);
    }


}

