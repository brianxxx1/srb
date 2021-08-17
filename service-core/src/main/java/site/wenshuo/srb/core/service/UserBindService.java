package site.wenshuo.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.wenshuo.srb.core.pojo.entity.UserBind;
import site.wenshuo.srb.core.pojo.vo.UserBindVo;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface UserBindService extends IService<UserBind> {
    
    String commitBindUser(UserBindVo userBindVo, Long userId);

    void notify(Map<String, Object> map);
}
