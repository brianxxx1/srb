package site.wenshuo.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.wenshuo.common.exception.Assert;
import site.wenshuo.common.result.ResponseEnum;
import site.wenshuo.srb.core.enums.BorrowInfoStatusEnum;
import site.wenshuo.srb.core.enums.BorrowerStatusEnum;
import site.wenshuo.srb.core.enums.UserBindEnum;
import site.wenshuo.srb.core.mapper.BorrowInfoMapper;
import site.wenshuo.srb.core.mapper.BorrowerMapper;
import site.wenshuo.srb.core.mapper.IntegralGradeMapper;
import site.wenshuo.srb.core.mapper.UserInfoMapper;
import site.wenshuo.srb.core.pojo.entity.*;
import site.wenshuo.srb.core.pojo.vo.BorrowInfoApprovalVO;
import site.wenshuo.srb.core.pojo.vo.BorrowerDetailVO;
import site.wenshuo.srb.core.service.BorrowInfoService;
import site.wenshuo.srb.core.service.BorrowerService;
import site.wenshuo.srb.core.service.DictService;
import site.wenshuo.srb.core.service.LendService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private LendService lendService;

    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        Integer integral = userInfo.getIntegral();


        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper.le("integral_start",integral).gt("integral_end",integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);
        if (integralGrade == null){
            return new BigDecimal(0);
        }
        return integralGrade.getBorrowAmount();
    }

    @Override
    public Integer getBorrowStatus(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.eq("user_id",userId);
        BorrowInfo borrowInfo = baseMapper.selectOne(borrowInfoQueryWrapper);
        if (borrowInfo == null){
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        return borrowInfo.getStatus();
    }

    @Override
    public List<BorrowInfo> selectList() {
        List<BorrowInfo> borrowInfos = baseMapper.selectBorrowInfoList();
        for (BorrowInfo borrowInfo : borrowInfos) {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
            borrowInfo.getParam().put("returnMethod",returnMethod);
            String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
            borrowInfo.getParam().put("moneyUse",moneyUse);
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
            borrowInfo.getParam().put("status",status);
        }
        return borrowInfos;
    }

    @Override
    public Map<String, Object> getBorrowInfoDetail(Integer id) {
        HashMap<String, Object> map = new HashMap<>();

        BorrowInfo borrowInfo = baseMapper.selectById(id);

        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        borrowInfo.getParam().put("returnMethod",returnMethod);
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        borrowInfo.getParam().put("moneyUse",moneyUse);
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("status",status);

        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id",borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVoById(borrower.getId());


        map.put("borrowInfo",borrowInfo);
        map.put("borrower",borrowerDetailVO);

        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoApprovalVO.getId());
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(borrowInfo);
        if (borrowInfoApprovalVO.getStatus().intValue() == BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()){
            lendService.createLend(borrowInfoApprovalVO,borrowInfo);
        }

    }


    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.isTrue(userInfo.getBindStatus().intValue() == UserBindEnum.BIND_OK.getStatus().intValue(),ResponseEnum.USER_NO_BIND_ERROR);
        Assert.isTrue(userInfo.getBorrowAuthStatus().intValue() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),ResponseEnum.USER_NO_AMOUNT_ERROR);
        BigDecimal borrowAmount = getBorrowAmount(userId);
        Assert.isTrue(borrowInfo.getAmount().doubleValue()<=borrowAmount.doubleValue(),ResponseEnum.USER_AMOUNT_LESS_ERROR);
        borrowInfo.setUserId(userId);
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);
    }
}
