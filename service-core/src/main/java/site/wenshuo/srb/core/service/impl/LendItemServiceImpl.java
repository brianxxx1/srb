package site.wenshuo.srb.core.service.impl;

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
import site.wenshuo.srb.core.mapper.LendItemMapper;
import site.wenshuo.srb.core.mapper.LendMapper;
import site.wenshuo.srb.core.mapper.UserAccountMapper;
import site.wenshuo.srb.core.mapper.UserBindMapper;
import site.wenshuo.srb.core.pojo.bo.TransFlowBO;
import site.wenshuo.srb.core.pojo.entity.Lend;
import site.wenshuo.srb.core.pojo.entity.LendItem;
import site.wenshuo.srb.core.pojo.entity.UserBind;
import site.wenshuo.srb.core.pojo.vo.InvestVo;
import site.wenshuo.srb.core.service.LendItemService;
import site.wenshuo.srb.core.service.LendService;
import site.wenshuo.srb.core.service.TransFlowService;
import site.wenshuo.srb.core.service.UserAccountService;
import site.wenshuo.srb.core.util.LendNoUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

    @Resource
    private LendMapper lendMapper;

    @Resource
    private TransFlowService transFlowService;

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserBindMapper userBindMapper;

    @Resource
    private LendService lendService;

    @Override
    public String commitInvest(InvestVo investVO) {
        Long lendId = investVO.getLendId();
        Lend lend = lendMapper.selectById(lendId);
        BigDecimal balance = userAccountService.getAccount(investVO.getInvestUserId());

        Assert.isTrue(lend.getStatus() == LendStatusEnum.INVEST_RUN.getStatus().intValue(), ResponseEnum.LEND_INVEST_ERROR);
        Assert.isTrue(lend.getInvestAmount().add(new BigDecimal(investVO.getInvestAmount())).doubleValue() <= lend.getAmount().doubleValue(), ResponseEnum.LEND_FULL_SCALE_ERROR);
        Assert.isTrue(balance.doubleValue() >= new BigDecimal(investVO.getInvestAmount()).doubleValue(), ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        LendItem lendItem = new LendItem();
        lendItem.setInvestUserId(investVO.getInvestUserId());
        lendItem.setInvestName(investVO.getInvestName());
        String lendItemNo = LendNoUtils.getLendItemNo();
        lendItem.setLendItemNo(lendItemNo);
        lendItem.setLendId(investVO.getLendId());
        lendItem.setInvestAmount(new BigDecimal(investVO.getInvestAmount()));
        lendItem.setLendYearRate(lend.getLendYearRate());
        lendItem.setInvestTime(LocalDateTime.now());
        lendItem.setLendStartDate(lend.getLendStartDate());
        lendItem.setLendEndDate(lend.getLendEndDate());

        BigDecimal expectAmount = lendService.getInterestCount(
                lendItem.getInvestAmount(),
                lendItem.getLendYearRate(),
                lend.getPeriod(),
                lend.getReturnMethod());
        lendItem.setExpectAmount(expectAmount);

        lendItem.setRealAmount(new BigDecimal(0));
        lendItem.setStatus(0);
        baseMapper.insert(lendItem);

        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", investVO.getInvestUserId());
        UserBind userBind = userBindMapper.selectOne(userBindQueryWrapper);
        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", lend.getUserId());
        UserBind userBind1 = userBindMapper.selectOne(userBindQueryWrapper);

        HashMap<String, Object> map = new HashMap<>();
        map.put("agentId", HfbConst.AGENT_ID);
        map.put("voteBindCode", userBind.getBindCode());
        map.put("benefitBindCode", userBind1.getBindCode());
        map.put("agentProjectCode", lend.getLendNo());
        map.put("agentProjectName", lend.getTitle());
        map.put("agentBillNo", lendItemNo);
        map.put("voteAmt", investVO.getInvestAmount());
        map.put("votePrizeAmt", 0);
        map.put("voteFreeAmt", 0);
        map.put("projectAmt", lend.getAmount());
        map.put("note", "");
        map.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL);
        map.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        map.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(map);
        map.put("sign", sign);

        return FormHelper.buildForm(HfbConst.INVEST_URL, map);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> map) {
        String agentBillNo = (String)map.get("agentBillNo");
        boolean transFlowExisted = userAccountService.isTransFlowExisted(agentBillNo);
        if (!transFlowExisted){
            userAccountMapper.updateAccount((String)map.get("voteBindCode"),new BigDecimal("-"+(String)map.get("voteAmt")),new BigDecimal((String)map.get("voteAmt")));
            QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
            lendItemQueryWrapper.eq("lend_item_no",agentBillNo);
            LendItem lendItem = baseMapper.selectOne(lendItemQueryWrapper);
            lendItem.setStatus(1);
            baseMapper.updateById(lendItem);
            Long lendId = lendItem.getLendId();
            Lend lend = lendMapper.selectById(lendId);
            lend.setInvestNum(lend.getInvestNum()+1);
            lend.setInvestAmount(lend.getInvestAmount().add(new BigDecimal((String) map.get("voteAmt"))));
            lendMapper.updateById(lend);

            TransFlowBO transFlowBO = new TransFlowBO(agentBillNo, (String)map.get("voteBindCode"), new BigDecimal((String)map.get("voteAmt")),
                    TransTypeEnum.INVEST_LOCK, lend.getLendNo());
            transFlowService.saveTransFlow(transFlowBO);

        }
    }

    @Override
    public List<LendItem> selectByLendId(Long lendId, Integer status) {
        QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
        lendItemQueryWrapper.eq("lend_id",lendId).eq("status",status);
        return baseMapper.selectList(lendItemQueryWrapper);
    }

    @Override
    public List<LendItem> selectByLendId(Long id) {
        QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
        lendItemQueryWrapper.eq("lend_id",id);
        List<LendItem> lendItems = baseMapper.selectList(lendItemQueryWrapper);
        return lendItems;
    }

}
