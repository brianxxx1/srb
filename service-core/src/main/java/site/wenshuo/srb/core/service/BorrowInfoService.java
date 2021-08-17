package site.wenshuo.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.wenshuo.srb.core.pojo.entity.BorrowInfo;
import site.wenshuo.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    BigDecimal getBorrowAmount(Long userId);

    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    Integer getBorrowStatus(Long userId);

    List<BorrowInfo> selectList();

    Map<String, Object> getBorrowInfoDetail(Integer id);

    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
