package site.wenshuo.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import site.wenshuo.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import site.wenshuo.srb.core.pojo.query.UserInfoQuery;
import site.wenshuo.srb.core.pojo.vo.LoginVO;
import site.wenshuo.srb.core.pojo.vo.RegisterVO;
import site.wenshuo.srb.core.pojo.vo.UserIndexVO;
import site.wenshuo.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO registerVO);

    UserInfoVO login(LoginVO loginVo, String ip);

    IPage<UserInfo> listPage(Page<UserInfo> userInfoPage, UserInfoQuery userInfoQuery);

    void lock(Long id, Integer status);

    boolean checkMobile(String mobile);

    UserIndexVO getUserInfoById(Long userId);
}
