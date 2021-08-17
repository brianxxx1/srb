package site.wenshuo.srb.core.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BorrowInfoApprovalVO {
    private Long id;
    private Integer status;
    private String content;
    private String title;
    private BigDecimal lendYearRate;
    private BigDecimal serviceRate;
    private String lendStartDate;
    private String lendInfo;
}
