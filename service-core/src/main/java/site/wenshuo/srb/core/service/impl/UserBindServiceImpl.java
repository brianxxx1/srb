package site.wenshuo.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.wenshuo.common.exception.Assert;
import site.wenshuo.common.result.ResponseEnum;
import site.wenshuo.srb.core.enums.UserBindEnum;
import site.wenshuo.srb.core.hfb.FormHelper;
import site.wenshuo.srb.core.hfb.HfbConst;
import site.wenshuo.srb.core.hfb.RequestHelper;
import site.wenshuo.srb.core.mapper.UserBindMapper;
import site.wenshuo.srb.core.mapper.UserInfoMapper;
import site.wenshuo.srb.core.pojo.entity.UserBind;
import site.wenshuo.srb.core.pojo.entity.UserInfo;
import site.wenshuo.srb.core.pojo.vo.UserBindVo;
import site.wenshuo.srb.core.service.UserBindService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public String commitBindUser(UserBindVo userBindVo, Long userId) {
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("id_card",userBindVo.getIdCard()).ne("user_id",userId);
        UserBind userBind2 = baseMapper.selectOne(userBindQueryWrapper);
        Assert.isNull(userBind2, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", userId);
        UserBind userBind1 = baseMapper.selectOne(userBindQueryWrapper);

        if (userBind1 == null){
            UserBind userBind = new UserBind();
            BeanUtils.copyProperties(userBindVo,userBind);

            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        }else{
            BeanUtils.copyProperties(userBindVo,userBind1);
            baseMapper.updateById(userBind1);
        }



        HashMap<String, Object> map = new HashMap<>();
        map.put("agentId",HfbConst.AGENT_ID);
        map.put("agentUserId",userId);
        map.put("idCard",userBindVo.getIdCard());
        map.put("personalName",userBindVo.getName());
        map.put("bankType",userBindVo.getBankType());
        map.put("bankNo",userBindVo.getBankNo());
        map.put("mobile", userBindVo.getMobile());
        map.put("returnUrl",HfbConst.USERBIND_RETURN_URL);
        map.put("notifyUrl",HfbConst.USERBIND_NOTIFY_URL);
        map.put("timestamp", RequestHelper.getTimestamp());
        map.put("sign",RequestHelper.getSign(map));
        return FormHelper.buildForm(HfbConst.USERBIND_URL, map);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> map) {

        String bindCode = (String) map.get("bindCode");
        String agentUserId = (String) map.get("agentUserId");
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",agentUserId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        UserInfo userInfo = userInfoMapper.selectById(agentUserId);
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);
    }
}
