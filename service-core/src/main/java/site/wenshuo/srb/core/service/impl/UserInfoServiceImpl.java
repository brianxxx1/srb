package site.wenshuo.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.wenshuo.common.exception.Assert;
import site.wenshuo.common.result.ResponseEnum;
import site.wenshuo.common.util.MD5;
import site.wenshuo.srb.base.util.JwtUtils;
import site.wenshuo.srb.core.mapper.UserAccountMapper;
import site.wenshuo.srb.core.mapper.UserInfoMapper;
import site.wenshuo.srb.core.mapper.UserLoginRecordMapper;
import site.wenshuo.srb.core.pojo.entity.UserAccount;
import site.wenshuo.srb.core.pojo.entity.UserInfo;
import site.wenshuo.srb.core.pojo.entity.UserLoginRecord;
import site.wenshuo.srb.core.pojo.query.UserInfoQuery;
import site.wenshuo.srb.core.pojo.vo.LoginVO;
import site.wenshuo.srb.core.pojo.vo.RegisterVO;
import site.wenshuo.srb.core.pojo.vo.UserIndexVO;
import site.wenshuo.srb.core.pojo.vo.UserInfoVO;
import site.wenshuo.srb.core.service.UserInfoService;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile", registerVO.getMobile());
        Integer integer = baseMapper.selectCount(userInfoQueryWrapper);
        Assert.isTrue(integer == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        UserInfo userInfo = new UserInfo();
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        userInfo.setHeadImg(UserInfo.USER_AVATAR);
        baseMapper.insert(userInfo);

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoVO login(LoginVO loginVo, String ip) {
        String mobile = loginVo.getMobile();
        Integer userType = loginVo.getUserType();
        String password = loginVo.getPassword();
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile", mobile).eq("user_type", userType);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);

        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);

        Assert.equals(MD5.encrypt(password), userInfo.getPassword(), ResponseEnum.LOGIN_PASSWORD_ERROR);

        Assert.equals(userInfo.getStatus(), UserInfo.STATUS_NORMAL, ResponseEnum.LOGIN_LOCKED_ERROR);

        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setIp(ip);
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecordMapper.insert(userLoginRecord);

        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setToken(token);
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setUserType(userInfo.getUserType());
        userInfoVO.setMobile(userInfo.getMobile());
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        userInfoVO.setNickName(userInfo.getNickName());

        return userInfoVO;
    }

    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> userInfoPage, UserInfoQuery userInfoQuery) {
        if (userInfoQuery == null) {
            return baseMapper.selectPage(userInfoPage, null);
        } else {
            Integer userType = userInfoQuery.getUserType();
            Integer status = userInfoQuery.getStatus();
            String mobile = userInfoQuery.getMobile();

            QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
            userInfoQueryWrapper.eq(StringUtils.isNotBlank(mobile), "mobile", mobile).eq(status != null, "status", status).eq(userType != null, "user_type", userType);
            return baseMapper.selectPage(userInfoPage, userInfoQueryWrapper);
        }
    }

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public boolean checkMobile(String mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile);
        Integer integer = baseMapper.selectCount(userInfoQueryWrapper);
        return integer > 0;
    }

    @Override
    public UserIndexVO getUserInfoById(Long userId) {
        UserIndexVO userIndexVO = new UserIndexVO();
        userIndexVO.setUserId(userId);
        UserInfo userInfo = userInfoMapper.selectById(userId);
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id",userId);
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);
        userIndexVO.setName(userInfo.getName());
        userIndexVO.setNickName(userInfo.getNickName());
        userIndexVO.setUserType(userInfo.getUserType());
        userIndexVO.setHeadImg(userInfo.getHeadImg());
        userIndexVO.setBindStatus(userInfo.getBindStatus());

        userIndexVO.setAmount(userAccount.getAmount());
        userIndexVO.setFreezeAmount(userAccount.getFreezeAmount());

        QueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new QueryWrapper<>();
        userLoginRecordQueryWrapper.eq("user_id",userId).orderByDesc("create_time").last("limit 1");
        UserLoginRecord userLoginRecord = userLoginRecordMapper.selectOne(userLoginRecordQueryWrapper);

        userIndexVO.setLastLoginTime(userLoginRecord.getCreateTime());


        return userIndexVO;
    }
}
