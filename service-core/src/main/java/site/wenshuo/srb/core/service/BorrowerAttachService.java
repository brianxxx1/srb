package site.wenshuo.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.wenshuo.srb.core.pojo.entity.BorrowerAttach;
import site.wenshuo.srb.core.pojo.vo.BorrowerAttachVO;

import java.util.List;

/**
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    List<BorrowerAttachVO> selectBorrowerAttachVOList(Long id);

}
