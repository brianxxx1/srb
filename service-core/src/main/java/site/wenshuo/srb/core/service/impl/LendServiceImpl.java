package site.wenshuo.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.wenshuo.common.exception.BusinessException;
import site.wenshuo.srb.core.enums.LendStatusEnum;
import site.wenshuo.srb.core.enums.ReturnMethodEnum;
import site.wenshuo.srb.core.enums.TransTypeEnum;
import site.wenshuo.srb.core.hfb.HfbConst;
import site.wenshuo.srb.core.hfb.RequestHelper;
import site.wenshuo.srb.core.mapper.BorrowerMapper;
import site.wenshuo.srb.core.mapper.LendMapper;
import site.wenshuo.srb.core.mapper.UserAccountMapper;
import site.wenshuo.srb.core.mapper.UserInfoMapper;
import site.wenshuo.srb.core.pojo.bo.TransFlowBO;
import site.wenshuo.srb.core.pojo.entity.*;
import site.wenshuo.srb.core.pojo.vo.BorrowInfoApprovalVO;
import site.wenshuo.srb.core.pojo.vo.BorrowerDetailVO;
import site.wenshuo.srb.core.service.*;
import site.wenshuo.srb.core.util.LendNoUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
@Slf4j
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Resource
    private LendItemReturnService lendItemReturnService;


    @Resource
    private LendReturnService lendReturnService;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private LendItemService lendItemService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private TransFlowService transFlowService;

    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        Lend lend = new Lend();
        lend.setUserId(borrowInfo.getUserId());
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setLendNo(LendNoUtils.getLendNo());
        lend.setTitle(borrowInfoApprovalVO.getTitle());
        lend.setAmount(borrowInfo.getAmount());
        lend.setPeriod(borrowInfo.getPeriod());
        lend.setLendYearRate(borrowInfoApprovalVO.getLendYearRate().divide(new BigDecimal(100)));
        lend.setServiceRate(borrowInfoApprovalVO.getServiceRate().divide(new BigDecimal(100)));
        lend.setReturnMethod(borrowInfo.getReturnMethod());
        lend.setLowestAmount(new BigDecimal(100));
        lend.setInvestAmount(new BigDecimal(0));
        lend.setInvestNum(0);
        lend.setPublishDate(LocalDateTime.now());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(), dateTimeFormatter);
        lend.setLendStartDate(start);
        LocalDate end = start.plusMonths(borrowInfo.getPeriod());
        lend.setLendEndDate(end);
        lend.setLendInfo(borrowInfoApprovalVO.getLendInfo());
        lend.setExpectAmount(lend.getServiceRate().divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(lend.getPeriod())).multiply(lend.getAmount()));
        lend.setRealAmount(new BigDecimal(0));
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());
        lend.setCheckTime(LocalDateTime.now());
        lend.setCheckAdminId(1L);
        baseMapper.insert(lend);
    }

    @Override
    public List<Lend> selectList() {
        List<Lend> list = baseMapper.selectList(null);
        for (Lend lend : list) {
            Map<String, Object> params = lend.getParams();
            params.put("returnMethod", dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod()));
            params.put("status", LendStatusEnum.getMsgByStatus(lend.getStatus()));
        }
        return list;
    }

    @Override
    public Map<String, Object> show(String id) {
        HashMap<String, Object> result = new HashMap<>();
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        Lend lend = baseMapper.selectById(id);

        Map<String, Object> params = lend.getParams();
        params.put("returnMethod", dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod()));
        params.put("status", LendStatusEnum.getMsgByStatus(lend.getStatus()));

        borrowerQueryWrapper.eq("user_id", lend.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVoById(borrower.getId());

        result.put("borrower", borrowerDetailVO);
        result.put("lend", lend);
        return result;
    }

    @Override
    public BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod) {
        BigDecimal interestCount;
        if (returnMethod.intValue() == ReturnMethodEnum.ONE.getMethod()) {
            interestCount = com.atguigu.srb.core.util.Amount1Helper.getInterestCount(invest, yearRate, totalmonth);
        } else if (returnMethod.intValue() == ReturnMethodEnum.TWO.getMethod()) {
            interestCount = com.atguigu.srb.core.util.Amount2Helper.getInterestCount(invest, yearRate, totalmonth);
        } else if (returnMethod.intValue() == ReturnMethodEnum.THREE.getMethod()) {
            interestCount = com.atguigu.srb.core.util.Amount3Helper.getInterestCount(invest, yearRate, totalmonth);
        } else {
            interestCount = com.atguigu.srb.core.util.Amount4Helper.getInterestCount(invest, yearRate, totalmonth);
        }
        return interestCount;
    }

    @Override
    public void makeLoan(Long id) {
        Lend lend = baseMapper.selectById(id);
        HashMap<String, Object> map = new HashMap<>();
        map.put("agentId", HfbConst.AGENT_ID);
        map.put("agentProjectCode", lend.getLendNo());
        map.put("agentBillNo", LendNoUtils.getLoanNo());
        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal(12), 8, RoundingMode.DOWN);
        BigDecimal fee = lend.getInvestAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));
        map.put("mchFee", fee);
        map.put("timeStamp", RequestHelper.getTimestamp());
        map.put("sign", RequestHelper.getSign(map));
        JSONObject result = RequestHelper.sendRequest(map, HfbConst.MAKE_LOAD_URL);

        if ("0000".equals(result.getString("resultCode"))) {
            lend.setRealAmount(fee);
            lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
            lend.setPaymentTime(LocalDateTime.now());
            baseMapper.updateById(lend);

            Long borrowerId = lend.getUserId();
            UserInfo borrowerUserInfo = userInfoMapper.selectById(borrowerId);
            userAccountMapper.updateAccount(borrowerUserInfo.getBindCode(), new BigDecimal(result.getString("voteAmt")), new BigDecimal(0));
            TransFlowBO transFlowBO = new TransFlowBO(result.getString("agentBillNo"), borrowerUserInfo.getBindCode(), new BigDecimal(result.getString("voteAmt")),
                    TransTypeEnum.BORROW_BACK, "LEND NO:" + lend.getLendNo());
            transFlowService.saveTransFlow(transFlowBO);

            List<LendItem> lendItems = lendItemService.selectByLendId(lend.getId(), 1);
            for (LendItem lendItem : lendItems) {
                UserInfo userInfo = userInfoMapper.selectById(lendItem.getInvestUserId());
                String investBindCode = userInfo.getBindCode();
                userAccountMapper.updateAccount(investBindCode, new BigDecimal(0), lendItem.getInvestAmount().negate());

                TransFlowBO transFlow = new TransFlowBO(LendNoUtils.getTransNo(), investBindCode, lendItem.getInvestAmount(), TransTypeEnum.INVEST_UNLOCK, "Frozen Money UNLOCK: " + lend.getLendNo());
                transFlowService.saveTransFlow(transFlow);
            }
            repaymentPlan(lend);
        } else {
            throw new BusinessException(result.getString("resultMsg"));
        }
    }

    private void repaymentPlan(Lend lend){
        ArrayList<LendReturn> lendReturns = new ArrayList<>();
        Integer len = lend.getPeriod();
        for (Integer i = 1; i <= len; i++) {
            LendReturn lendReturn = new LendReturn();
            lendReturn.setReturnNo(LendNoUtils.getReturnNo());
            lendReturn.setLendId(lend.getId());
            lendReturn.setBorrowInfoId(lend.getBorrowInfoId());
            lendReturn.setUserId(lend.getUserId());
            lendReturn.setAmount(lend.getAmount());
            lendReturn.setBaseAmount(lend.getInvestAmount());
            lendReturn.setCurrentPeriod(i);
            lendReturn.setLendYearRate(lend.getLendYearRate());
            lendReturn.setReturnMethod(lend.getReturnMethod());
            lendReturn.setFee(new BigDecimal(0));
            if (i.equals(len)){
                lendReturn.setLast(true);
            }else {
                lendReturn.setLast(false);
            }
            lendReturn.setReturnDate(LocalDate.now().plusMonths(i));
            lendReturn.setStatus(0);
            lendReturn.setOverdue(false);
            lendReturns.add(lendReturn);
        }
        lendReturnService.saveBatch(lendReturns);

        Map<Integer, Long> periodLendReturnIdMap = lendReturns.stream().collect(
                Collectors.toMap(LendReturn::getCurrentPeriod, LendReturn::getId)
        );
        ArrayList<LendItemReturn> allLendItemReturns = new ArrayList<>();

        List<LendItem> lendItems = lendItemService.selectByLendId(lend.getId(), 1);

        for (LendItem lendItem : lendItems) {
            List<LendItemReturn> lendItemReturns = payBackPlan(lendItem.getId(), periodLendReturnIdMap, lend);
            allLendItemReturns.addAll(lendItemReturns);
        }

        for (LendReturn lendReturn : lendReturns) {
            BigDecimal principal = allLendItemReturns.stream().filter(item ->
                    item.getLendReturnId().equals(lendReturn.getId())
            ).map(LendItemReturn::getPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal interest = allLendItemReturns.stream().filter(item ->
                    item.getLendReturnId().equals(lendReturn.getId())
            ).map(LendItemReturn::getInterest).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal total = allLendItemReturns.stream().filter(item ->
                    item.getLendReturnId().equals(lendReturn.getId())
            ).map(LendItemReturn::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

            lendReturn.setPrincipal(principal);
            lendReturn.setInterest(interest);
            lendReturn.setTotal(total);
        }
        lendReturnService.updateBatchById(lendReturns);


    }

    private List<LendItemReturn> payBackPlan(Long lendItemId, Map<Integer, Long> periodLendReturnIdMap, Lend lend){

        LendItem lendItem = lendItemService.getById(lendItemId);
        BigDecimal amount = lendItem.getInvestAmount();
        BigDecimal yearRate = lendItem.getLendYearRate();
        Integer totalMonth = lend.getPeriod();
        Map<Integer, BigDecimal> interestMap = null;
        Map<Integer, BigDecimal> principalMap = null;
        Integer returnMethod = lend.getReturnMethod();

        if (returnMethod.intValue() == ReturnMethodEnum.ONE.getMethod()) {
            interestMap = com.atguigu.srb.core.util.Amount1Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            principalMap = com.atguigu.srb.core.util.Amount1Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else if (lend.getReturnMethod().intValue() == ReturnMethodEnum.TWO.getMethod()) {
            interestMap = com.atguigu.srb.core.util.Amount2Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            principalMap = com.atguigu.srb.core.util.Amount2Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else if (lend.getReturnMethod().intValue() == ReturnMethodEnum.THREE.getMethod()) {
            interestMap = com.atguigu.srb.core.util.Amount3Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            principalMap = com.atguigu.srb.core.util.Amount3Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        } else {
            interestMap = com.atguigu.srb.core.util.Amount4Helper.getPerMonthInterest(amount, yearRate, totalMonth);
            principalMap = com.atguigu.srb.core.util.Amount4Helper.getPerMonthPrincipal(amount, yearRate, totalMonth);
        }
        ArrayList<LendItemReturn> lendItemReturns = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> interestEntry : interestMap.entrySet()) {
            LendItemReturn lendItemReturn = new LendItemReturn();
            Integer currentPeriod = interestEntry.getKey();
            Long lendReturnId = periodLendReturnIdMap.get(currentPeriod);
            lendItemReturn.setLendReturnId(lendReturnId);
            lendItemReturn.setLendId(lend.getId());
            lendItemReturn.setLendItemId(lendItemId);
            lendItemReturn.setLendYearRate(lend.getLendYearRate());
            lendItemReturn.setCurrentPeriod(currentPeriod);
            lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
            lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
            lendItemReturn.setReturnMethod(returnMethod);
            if (currentPeriod.equals(lend.getPeriod())){
                BigDecimal principleSum = lendItemReturns.stream().map(
                        LendItemReturn::getPrincipal
                ).reduce(
                        BigDecimal.ZERO, BigDecimal::add
                );
                BigDecimal principal = lendItem.getInvestAmount().subtract(principleSum);
                lendItemReturn.setPrincipal(principal);


                BigDecimal interestSum = lendItemReturns.stream().map(
                        LendItemReturn::getInterest
                ).reduce(
                        BigDecimal.ZERO, BigDecimal::add
                );
                BigDecimal interest = lendItem.getExpectAmount().subtract(interestSum);
                lendItemReturn.setInterest(interest);
            }else{
                lendItemReturn.setInterest(interestMap.get(currentPeriod));
                lendItemReturn.setPrincipal(principalMap.get(currentPeriod));
            }

            lendItemReturn.setTotal(lendItemReturn.getInterest().add(lendItemReturn.getPrincipal()));
            lendItemReturns.add(lendItemReturn);
            lendItemReturn.setFee(new BigDecimal(0));
            lendItemReturn.setReturnDate(LocalDate.now().plusMonths(currentPeriod));
            lendItemReturn.setOverdue(false);
            lendItemReturn.setStatus(0);
        }
        lendItemReturnService.saveBatch(lendItemReturns);
        return lendItemReturns;
    }
}
