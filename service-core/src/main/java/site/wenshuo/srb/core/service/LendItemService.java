package site.wenshuo.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.wenshuo.srb.core.pojo.entity.LendItem;
import site.wenshuo.srb.core.pojo.vo.InvestVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface LendItemService extends IService<LendItem> {

    String commitInvest(InvestVo investVO);

    void notify(Map<String, Object> map);

    List<LendItem> selectByLendId(Long lendId, Integer status);

    List<LendItem> selectByLendId(Long id);
}
