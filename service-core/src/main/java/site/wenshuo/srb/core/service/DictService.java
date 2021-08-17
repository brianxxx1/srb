package site.wenshuo.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.wenshuo.srb.core.pojo.dto.excelDictDTO;
import site.wenshuo.srb.core.pojo.entity.Dict;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface DictService extends IService<Dict> {
    void importData(InputStream inputStream);

    List<excelDictDTO> listDictData();

    List<Dict> listByParentId(Long parentId);

    List<Dict> findByDictCode(String dictCode);

    String getNameByParentDictCodeAndValue(String parentDictCode, Integer value);
}
