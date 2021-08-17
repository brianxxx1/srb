package site.wenshuo.srb.core.mapper;

import site.wenshuo.srb.core.pojo.dto.excelDictDTO;
import site.wenshuo.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(List<excelDictDTO> list);
}
