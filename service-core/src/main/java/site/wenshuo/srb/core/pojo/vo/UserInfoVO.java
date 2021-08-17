package site.wenshuo.srb.core.pojo.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private String name;
    private String nickName;
    private String headImg;
    private String mobile;
    private Integer userType;
    private String token;
}
