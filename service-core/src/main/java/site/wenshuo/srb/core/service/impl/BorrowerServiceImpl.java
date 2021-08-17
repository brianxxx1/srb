package site.wenshuo.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.wenshuo.srb.core.enums.BorrowerStatusEnum;
import site.wenshuo.srb.core.enums.IntegralEnum;
import site.wenshuo.srb.core.mapper.BorrowerAttachMapper;
import site.wenshuo.srb.core.mapper.BorrowerMapper;
import site.wenshuo.srb.core.mapper.UserInfoMapper;
import site.wenshuo.srb.core.mapper.UserIntegralMapper;
import site.wenshuo.srb.core.pojo.entity.Borrower;
import site.wenshuo.srb.core.pojo.entity.BorrowerAttach;
import site.wenshuo.srb.core.pojo.entity.UserInfo;
import site.wenshuo.srb.core.pojo.entity.UserIntegral;
import site.wenshuo.srb.core.pojo.vo.BorrowerApprovalVO;
import site.wenshuo.srb.core.pojo.vo.BorrowerDetailVO;
import site.wenshuo.srb.core.pojo.vo.BorrowerVO;
import site.wenshuo.srb.core.service.BorrowerAttachService;
import site.wenshuo.srb.core.service.BorrowerService;
import site.wenshuo.srb.core.service.DictService;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerAttachService borrowerAttachService;

    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        baseMapper.insert(borrower);

        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        for (BorrowerAttach borrowerAttach : borrowerAttachList) {
            borrowerAttach.setBorrowerId(userId);
            borrowerAttachMapper.insert(borrowerAttach);
        }

        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getBorrowerStatus(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id", userId);
        Borrower borrower = baseMapper.selectOne(borrowerQueryWrapper);

        if (borrower == null) {
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        } else {
            return borrower.getStatus();

        }

    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> borrowerPage, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return baseMapper.selectPage(borrowerPage, null);
        } else {
            QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
            borrowerQueryWrapper.like("name", keyword).or().like("id_card", keyword).or().like("mobile", keyword).orderByDesc("update_time");
            return baseMapper.selectPage(borrowerPage, borrowerQueryWrapper);
        }
    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVoById(Long id) {
        Borrower borrower = baseMapper.selectById(id);
        QueryWrapper<BorrowerAttach> borrowerAttachQueryWrapper = new QueryWrapper<>();
        borrowerAttachQueryWrapper.eq("borrower_id", id);
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();

        BeanUtils.copyProperties(borrower, borrowerDetailVO);
        borrowerDetailVO.setMarry(borrower.getMarry() ? "married" : "not married");
        borrowerDetailVO.setSex(borrower.getSex() == 1 ? "Male" : "Female");

        borrowerDetailVO.setEducation(dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation()));
        borrowerDetailVO.setIndustry(dictService.getNameByParentDictCodeAndValue("industry", borrower.getIndustry()));
        borrowerDetailVO.setIncome(dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome()));
        borrowerDetailVO.setReturnSource(dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource()));
        borrowerDetailVO.setContactsRelation(dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation()));
        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus()));

        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachService.selectBorrowerAttachVOList(borrower.getUserId()));

        return borrowerDetailVO;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        Borrower borrower = baseMapper.selectById(borrowerApprovalVO.getBorrowerId());
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);

        UserInfo userInfo = userInfoMapper.selectById(borrower.getUserId());
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        Integer infoIntegral = borrowerApprovalVO.getInfoIntegral();
        Integer total = userInfo.getIntegral();
        total += infoIntegral;

        Long userId = borrower.getUserId();
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(infoIntegral);
        userIntegral.setContent("Borrower Base Info");
        userIntegralMapper.insert(userIntegral);

        if (borrowerApprovalVO.getIsCarOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            total += IntegralEnum.BORROWER_IDCARD.getIntegral();
        }

        if (borrowerApprovalVO.getIsHouseOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            total += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }

        if (borrowerApprovalVO.getIsCarOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            total += IntegralEnum.BORROWER_CAR.getIntegral();
        }
        userInfo.setIntegral(total);
        userInfoMapper.updateById(userInfo);
    }

}
