package site.wenshuo.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.wenshuo.srb.core.mapper.TransFlowMapper;
import site.wenshuo.srb.core.mapper.UserBindMapper;
import site.wenshuo.srb.core.pojo.bo.TransFlowBO;
import site.wenshuo.srb.core.pojo.entity.TransFlow;
import site.wenshuo.srb.core.pojo.entity.UserBind;
import site.wenshuo.srb.core.service.TransFlowService;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Resource
    private UserBindMapper userBindMapper;

    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {

        String bindCode = transFlowBO.getBindCode();
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("bind_code", bindCode);
        UserBind userBind = userBindMapper.selectOne(userBindQueryWrapper);
        TransFlow transFlow = new TransFlow();
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setMemo(transFlowBO.getMemo());
        transFlow.setTransType(transFlowBO.getTransTypeEnum().getTransType());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setTransNo(transFlowBO.getAgentBillNo());
        transFlow.setUserId(userBind.getUserId());
        transFlow.setUserName(userBind.getName());
        System.out.println(transFlow);
        baseMapper.insert(transFlow);
    }

    @Override
    public List<TransFlow> selectByUserId(Long userId) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("user_id",userId).orderByDesc("create_time");
        return baseMapper.selectList(transFlowQueryWrapper);
    }
}
