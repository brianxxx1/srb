package site.wenshuo.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * UserInfo
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value="UserInfo object", description="UserInfo")
public class UserInfo implements Serializable {

    public static final Integer STATUS_NORMAL = 1;
    public static final Integer STATUS_LOCKED = 0;
    public static final String USER_AVATAR = "https://srb-file-wenshuo.oss-cn-beijing.aliyuncs.com/ins/2021/08/01/5570ba6b-dc53-4b84-96b7-534db801325a.jpg";

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "1：lender 2：borrower")
    private Integer userType;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "password")
    private String password;

    @ApiModelProperty(value = "nickName")
    private String nickName;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "idCard")
    private String idCard;

    @ApiModelProperty(value = "email")
    private String email;

    @ApiModelProperty(value = "openid")
    private String openid;

    @ApiModelProperty(value = "headImg")
    private String headImg;

    @ApiModelProperty(value = "bindStatus（0：not bound，1：bound -1：bound failed）")
    private Integer bindStatus;

    @ApiModelProperty(value = "borrowAuthStatus（0：not verified 1：verifying 2：verified -1：failed）")
    private Integer borrowAuthStatus;

    @ApiModelProperty(value = "bindCode")
    private String bindCode;

    @ApiModelProperty(value = "integral")
    private Integer integral;

    @ApiModelProperty(value = "status（0：locked 1：ok）")
    private Integer status;

    @ApiModelProperty(value = "createTime")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "updateTime")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "logically deleted(1:deleted，0:not deleted)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
