package site.wenshuo.srb.core.service;

import site.wenshuo.srb.core.pojo.bo.TransFlowBO;
import site.wenshuo.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface TransFlowService extends IService<TransFlow> {

    void saveTransFlow(TransFlowBO transFlowBO);

    List<TransFlow> selectByUserId(Long userId);
}
