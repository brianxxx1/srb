package site.wenshuo.srb.core.service;

import site.wenshuo.srb.core.pojo.entity.LendReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface LendReturnService extends IService<LendReturn> {

    List<LendReturn> selectByLendId(Long id);

    String commitReturn(Long id, Long userId);

    void notify(Map<String, Object> map);
}
