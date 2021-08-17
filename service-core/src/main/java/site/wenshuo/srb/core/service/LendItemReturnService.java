package site.wenshuo.srb.core.service;

import site.wenshuo.srb.core.pojo.entity.LendItemReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface LendItemReturnService extends IService<LendItemReturn> {

    List<LendItemReturn> selectByLendId(Long id, Long userId);

    List<Map<String, Object>> addReturnDetail(Long lendReturnId);
}
