package site.wenshuo.srb.core.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class excelDictDTO {
    @ExcelProperty("id")
    private Long id;
    @ExcelProperty("parent_id")
    private Long parentId;
    @ExcelProperty("name")
    private String name;
    @ExcelProperty("value")
    private Integer value;
    @ExcelProperty("dict_code")
    private String dictCode;
}
