package site.wenshuo.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import site.wenshuo.srb.core.pojo.entity.BorrowerAttach;
import site.wenshuo.srb.core.mapper.BorrowerAttachMapper;
import site.wenshuo.srb.core.pojo.vo.BorrowerAttachVO;
import site.wenshuo.srb.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public List<BorrowerAttachVO> selectBorrowerAttachVOList(Long id) {
        QueryWrapper<BorrowerAttach> borrowerAttachQueryWrapper = new QueryWrapper<>();
        borrowerAttachQueryWrapper.eq("borrower_id",id);
        List<BorrowerAttach> borrowerAttaches = baseMapper.selectList(borrowerAttachQueryWrapper);
        ArrayList<BorrowerAttachVO> borrowerAttachVOList = new ArrayList<>();
        for (BorrowerAttach borrowerAttach : borrowerAttaches) {
            borrowerAttachVOList.add(new BorrowerAttachVO(borrowerAttach.getImageType(),borrowerAttach.getImageUrl()));
        }
        return borrowerAttachVOList;
    }
}
