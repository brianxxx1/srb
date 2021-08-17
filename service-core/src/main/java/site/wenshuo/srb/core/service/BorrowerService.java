package site.wenshuo.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import site.wenshuo.srb.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import site.wenshuo.srb.core.pojo.vo.BorrowerApprovalVO;
import site.wenshuo.srb.core.pojo.vo.BorrowerDetailVO;
import site.wenshuo.srb.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface BorrowerService extends IService<Borrower> {

    void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId);

    Integer getBorrowerStatus(Long userId);

    IPage<Borrower> listPage(Page<Borrower> borrowerPage, String keyword);

    BorrowerDetailVO getBorrowerDetailVoById(Long id);

    void approval(BorrowerApprovalVO borrowerApprovalVO);
}
