package site.wenshuo.srb.core.pojo.vo;

import lombok.Data;

@Data
public class BorrowerApprovalVO {
    private Long borrowerId;
    private Integer status;
    private Boolean isIdCardOk;
    private Boolean isHouseOk;
    private Boolean isCarOk;
    private Integer infoIntegral;
}
