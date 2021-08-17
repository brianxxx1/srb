package site.wenshuo.srb.core.pojo.vo;

import lombok.Data;

@Data
public class LoginVO {
    private Integer userType;
    private String mobile;
    private String password;
}
