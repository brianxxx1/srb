package site.wenshuo.srb.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import site.wenshuo.srb.core.pojo.entity.BorrowInfo;

import java.util.List;

/**
 * <p>
 * 借款信息表 Mapper 接口
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface BorrowInfoMapper extends BaseMapper<BorrowInfo> {

    List<BorrowInfo> selectBorrowInfoList();

}
