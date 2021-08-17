package site.wenshuo.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.wenshuo.srb.core.listener.ExcelDictDTOListener;
import site.wenshuo.srb.core.mapper.DictMapper;
import site.wenshuo.srb.core.pojo.dto.excelDictDTO;
import site.wenshuo.srb.core.pojo.entity.Dict;
import site.wenshuo.srb.core.service.DictService;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    private RedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, excelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
    }

    @Override
    public List<excelDictDTO> listDictData() {
        List<Dict> dicts = baseMapper.selectList(null);
        ArrayList<excelDictDTO> excelDictDTOS = new ArrayList<>(dicts.size());
        for (Dict dict : dicts) {
            excelDictDTO excelDictDTO = new excelDictDTO();
            BeanUtils.copyProperties(dict, excelDictDTO);
            excelDictDTOS.add(excelDictDTO);
        }
        return excelDictDTOS;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {

        try {

            List<Dict> dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList" + parentId);
            if (dictList != null) {
                log.info("Getting Value from Redis");
                return dictList;
            }
        } catch (Exception e) {
            log.error("Redis Server Down" + ExceptionUtils.getStackTrace(e));
        }

        log.info("Getting Value from MySQL");
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id", parentId);
        List<Dict> dicts = baseMapper.selectList(dictQueryWrapper);
        dicts.forEach(dict -> {
            dict.setHasChildren(hasChildren(dict.getId()));
        });

        try {
            log.info("Storing Value to Redis");
            redisTemplate.opsForValue().set("srb:core:dictList" + parentId, dicts, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis Server Down" + ExceptionUtils.getStackTrace(e));
        }
        return dicts;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);

        List<Dict> dictList = listByParentId(dict.getId());
        return dictList;
    }


    private boolean hasChildren(Long id) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Dict> parent_id = dictQueryWrapper.eq("parent_id", id);
        Integer integer = baseMapper.selectCount(parent_id);
        return integer > 0;
    }

    @Override
    public String getNameByParentDictCodeAndValue(String parentDictCode, Integer value) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code", parentDictCode);
        Dict parentDict = baseMapper.selectOne(dictQueryWrapper);
        if (parentDict == null) {
            return "";
        }
        dictQueryWrapper = new QueryWrapper<Dict>();

        dictQueryWrapper.eq("parent_id", parentDict.getId())
                .eq("value", value);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        if (dict == null) {
            return "";
        }else {
            return dict.getName();
        }


    }
}
