package site.wenshuo.srb.core.pojo.query;

import lombok.Data;

@Data
public class UserInfoQuery {
    private String mobile;
    private Integer status;
    private Integer userType;
}
