package site.wenshuo.srb.core.service;

import site.wenshuo.srb.core.pojo.entity.BorrowInfo;
import site.wenshuo.srb.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import site.wenshuo.srb.core.pojo.entity.LendItem;
import site.wenshuo.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface LendService extends IService<Lend> {

    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    List<Lend> selectList();

    Map<String, Object> show(String id);

    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod);

    void makeLoan(Long id);


}
