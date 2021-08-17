package site.wenshuo.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.wenshuo.common.exception.Assert;
import site.wenshuo.common.result.ResponseEnum;
import site.wenshuo.srb.base.dto.SmsDTO;
import site.wenshuo.srb.core.enums.TransTypeEnum;
import site.wenshuo.srb.core.hfb.FormHelper;
import site.wenshuo.srb.core.hfb.HfbConst;
import site.wenshuo.srb.core.hfb.RequestHelper;
import site.wenshuo.srb.core.mapper.TransFlowMapper;
import site.wenshuo.srb.core.mapper.UserAccountMapper;
import site.wenshuo.srb.core.mapper.UserBindMapper;
import site.wenshuo.srb.core.mapper.UserInfoMapper;
import site.wenshuo.srb.core.pojo.bo.TransFlowBO;
import site.wenshuo.srb.core.pojo.entity.TransFlow;
import site.wenshuo.srb.core.pojo.entity.UserAccount;
import site.wenshuo.srb.core.pojo.entity.UserBind;
import site.wenshuo.srb.core.pojo.entity.UserInfo;
import site.wenshuo.srb.core.service.TransFlowService;
import site.wenshuo.srb.core.service.UserAccountService;
import site.wenshuo.srb.core.util.LendNoUtils;
import site.wenshuo.srb.rabbitmq.constant.MQConst;
import site.wenshuo.srb.rabbitmq.service.MQService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Resource
    private UserBindMapper userBindMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private TransFlowService transFlowService;

    @Resource
    private TransFlowMapper transFlowMapper;

    @Resource
    private MQService mqService;

    @Override
    public boolean isTransFlowExisted(String agentBillNo) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("trans_no", agentBillNo);
        Integer integer = transFlowMapper.selectCount(transFlowQueryWrapper);
        return integer > 0;

    }

    @Override
    public BigDecimal getAccount(Long userId) {
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);
        return userAccount.getAmount();
    }

    @Override
    public String commitWithdraw(BigDecimal fetchAmt, Long userId) {
        BigDecimal amt = getAccount(userId);
        Assert.isTrue(amt.doubleValue() >= fetchAmt.doubleValue(), ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        HashMap<String, Object> map = new HashMap<>();
        map.put("agentId", HfbConst.AGENT_ID);
        map.put("agentBillNo", LendNoUtils.getWithdrawNo());
        map.put("bindCode", bindCode);
        map.put("fetchAmt", fetchAmt);
        map.put("feeAmt", new BigDecimal(0));
        map.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        map.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        map.put("timeStamp", RequestHelper.getTimestamp());
        map.put("sign", RequestHelper.getSign(map));

        return FormHelper.buildForm(HfbConst.WITHDRAW_URL, map);
    }

    @Override
    public void notifyWithdraw(Map<String, Object> map) {
        String agentBillNo = (String) map.get("agentBillNo");
        boolean bool = isTransFlowExisted(agentBillNo);
        if (!bool) {
            String bindCode = (String) map.get("bindCode");
            baseMapper.updateAccount(bindCode, new BigDecimal((String) map.get("fetchAmt")).negate(), new BigDecimal(0));

            TransFlowBO transFlowBO = new TransFlowBO(agentBillNo, bindCode, new BigDecimal((String) map.get("fetchAmt")), TransTypeEnum.WITHDRAW, "Withdraw");
            transFlowService.saveTransFlow(transFlowBO);
        } else {
            log.warn("agentBillNo already existed");
        }
    }

    @Override
    public String getMobileByBindCode(String bindCode) {
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("bind_code",bindCode);
        UserBind userBind = userBindMapper.selectOne(userBindQueryWrapper);
        return userBind.getMobile();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> map) {
        String agentBillNo = (String) map.get("agentBillNo");
        if (!isTransFlowExisted(agentBillNo)) {
            String chargeAmt = (String) map.get("chargeAmt");
            String bindCode = (String) map.get("bindCode");
            baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmt), new BigDecimal(0));
            TransFlowBO transFlowBO = new TransFlowBO(agentBillNo, (String) map.get("bindCode"), new BigDecimal(chargeAmt),
                    TransTypeEnum.RECHARGE, "top-up");
            transFlowService.saveTransFlow(transFlowBO);

            SmsDTO smsDTO = new SmsDTO();
            smsDTO.setMobile(getMobileByBindCode(bindCode));
            smsDTO.setMessage("1234");
            mqService.sendMessage(MQConst.EXCHANGE_TOPIC_SMS,MQConst.ROUTING_SMS_ITEM,smsDTO);
            return "success";
        } else {
            log.warn("Record is already in the database");
            return "success";
        }
    }

    @Override
    public String commitCharge(Long userId, BigDecimal chargeAmt) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();

        //判断账户绑定状态
        Assert.notEmpty(bindCode, ResponseEnum.USER_NO_BIND_ERROR);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", "CHARGE" + LendNoUtils.getNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);//检查常量是否正确
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);
        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return formStr;
    }

}
