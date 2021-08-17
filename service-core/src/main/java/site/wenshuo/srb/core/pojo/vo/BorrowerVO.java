package site.wenshuo.srb.core.pojo.vo;

import lombok.Data;
import lombok.ToString;
import site.wenshuo.srb.core.pojo.entity.BorrowerAttach;

import java.util.List;

@Data
@ToString
public class BorrowerVO {
    private Integer age;
    private Integer sex;
    private Boolean marry;
    private Integer education;
    private Integer industry;
    private Integer income;
    private Integer returnSource;
    private String contactsName;
    private String contactsMobile;
    private Integer contactsRelation;
    private List<BorrowerAttach> borrowerAttachList;
}
