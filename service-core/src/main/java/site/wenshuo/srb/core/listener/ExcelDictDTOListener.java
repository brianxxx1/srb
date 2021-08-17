package site.wenshuo.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.wenshuo.srb.core.mapper.DictMapper;
import site.wenshuo.srb.core.pojo.dto.excelDictDTO;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<excelDictDTO> {

    private List<excelDictDTO> list = new ArrayList<>();

    private static final Integer BATCH_COUNT = 5;


    private DictMapper dictMapper;

    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(excelDictDTO exceLDictDTO, AnalysisContext analysisContext) {
        list.add(exceLDictDTO);
        if (list.size()>=BATCH_COUNT){
            log.info("adding");
            dictMapper.insertBatch(list);
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

        if (list.size()!=0){
            log.info("adding");
            dictMapper.insertBatch(list);
        }
    }
}
