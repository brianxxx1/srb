package site.wenshuo.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.wenshuo.common.exception.Assert;
import site.wenshuo.common.result.ResponseEnum;
import site.wenshuo.srb.core.enums.LendStatusEnum;
import site.wenshuo.srb.core.enums.TransTypeEnum;
import site.wenshuo.srb.core.hfb.FormHelper;
import site.wenshuo.srb.core.hfb.HfbConst;
import site.wenshuo.srb.core.hfb.RequestHelper;
import site.wenshuo.srb.core.mapper.*;
import site.wenshuo.srb.core.pojo.bo.TransFlowBO;
import site.wenshuo.srb.core.pojo.entity.*;
import site.wenshuo.srb.core.service.*;
import site.wenshuo.srb.core.util.LendNoUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class LendReturnServiceImpl extends ServiceImpl<LendReturnMapper, LendReturn> implements LendReturnService {
    @Resource
    private LendItemMapper lendItemMapper;

    @Resource
    private LendItemReturnMapper lendItemReturnMapper;

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private LendService lendService;

    @Resource
    private LendItemReturnService lendItemReturnService;

    @Resource
    private LendMapper lendMapper;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private TransFlowService transFlowService;


    @Override
    public List<LendReturn> selectByLendId(Long id) {
        QueryWrapper<LendReturn> lendReturnQueryWrapper = new QueryWrapper<>();
        lendReturnQueryWrapper.eq("lend_id",id);
        return baseMapper.selectList(lendReturnQueryWrapper);
    }

    @Override
    public String commitReturn(Long id, Long userId) {
        LendReturn lendReturn = baseMapper.selectById(id);
        Lend lend = lendService.getById(lendReturn.getLendId());
        BigDecimal amount = userAccountService.getAccount(userId);
        UserInfo borrower = userInfoMapper.selectById(userId);
        Assert.isTrue(amount.doubleValue() >= lendReturn.getTotal().doubleValue(), ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);
        HashMap<String, Object> map = new HashMap<>();
        map.put("agentId", HfbConst.AGENT_ID);
        map.put("agentGoodsName",lend.getTitle());
        map.put("agentBatchNo",lendReturn.getReturnNo());
        map.put("fromBindCode", borrower.getBindCode());
        map.put("note", "");
        map.put("totalAmt", lendReturn.getTotal());
        map.put("voteFeeAmt", new BigDecimal(0));
        map.put("notifyUrl", HfbConst.BORROW_RETURN_NOTIFY_URL);
        map.put("returnUrl", HfbConst.BORROW_RETURN_RETURN_URL);
        map.put("timeStamp", RequestHelper.getTimestamp());
        List<Map<String, Object>> list = lendItemReturnService.addReturnDetail(id);
        map.put("data", JSONObject.toJSONString(list));
        map.put("sign",RequestHelper.getSign(map));
        return FormHelper.buildForm(HfbConst.BORROW_RETURN_URL,map);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> map) {
        String agentBatchNo = (String) map.get("agentBatchNo");
        if (userAccountService.isTransFlowExisted(agentBatchNo)){
            return;
        }else{
            QueryWrapper<LendReturn> lendReturnQueryWrapper = new QueryWrapper<>();
            lendReturnQueryWrapper.eq("return_no",agentBatchNo);
            LendReturn lendReturn = baseMapper.selectOne(lendReturnQueryWrapper);
            lendReturn.setStatus(1);
            lendReturn.setFee(new BigDecimal((String) map.get("voteFeeAmt")));
            lendReturn.setRealReturnTime(LocalDateTime.now());
            baseMapper.updateById(lendReturn);

            Lend lend = lendMapper.selectById(lendReturn.getLendId());
            if (lendReturn.getLast()){
                lend.setStatus(LendStatusEnum.PAY_OK.getStatus());
                lendMapper.updateById(lend);
            }
            UserInfo userInfo = userInfoMapper.selectById(lendReturn.getUserId());
            userAccountMapper.updateAccount(userInfo.getBindCode(),new BigDecimal((String) map.get("totalAmt")).negate(),new BigDecimal(0));


            TransFlowBO transFlowBO = new TransFlowBO(agentBatchNo,userInfo.getBindCode(),new BigDecimal((String) map.get("totalAmt")), TransTypeEnum.RETURN_DOWN,"Project Code: "+lend.getLendNo());
            transFlowService.saveTransFlow(transFlowBO);

            QueryWrapper<LendItemReturn> lendItemReturnQueryWrapper = new QueryWrapper<>();
            lendItemReturnQueryWrapper.eq("lend_return_id",lendReturn.getId());
            List<LendItemReturn> lendItemReturns = lendItemReturnMapper.selectList(lendItemReturnQueryWrapper);

            for (LendItemReturn lendItemReturn : lendItemReturns) {
                LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());
                lendItem.setRealAmount(lendItem.getRealAmount().add(lendItemReturn.getInterest()));
                lendItemMapper.updateById(lendItem);
                lendItemReturn.setStatus(1);
                lendItemReturn.setRealReturnTime(LocalDateTime.now());
                lendItemReturnMapper.updateById(lendItemReturn);
                UserInfo lender = userInfoMapper.selectById(lendItemReturn.getInvestUserId());
                userAccountMapper.updateAccount(lender.getBindCode(),lendItemReturn.getTotal(),new BigDecimal(0));
                TransFlowBO transFlowBO1 = new TransFlowBO(LendNoUtils.getReturnItemNo(),lender.getBindCode(),lendItemReturn.getTotal(), TransTypeEnum.INVEST_BACK,"Money back to lender -- Project Code: "+lend.getLendNo());
                transFlowService.saveTransFlow(transFlowBO1);
            }


        }
    }
}
