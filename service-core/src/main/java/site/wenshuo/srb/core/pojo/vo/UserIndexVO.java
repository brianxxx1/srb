package site.wenshuo.srb.core.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data

public class UserIndexVO {
    private Long userId;
    private String name;
    private String nickName;
    private Integer userType;
    private String headImg;
    private Integer bindStatus;
    private BigDecimal amount;
    private BigDecimal freezeAmount;
    private LocalDateTime lastLoginTime;
}
