package site.wenshuo.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * UserBind
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="UserBind object", description="UserBind")
public class UserBind implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "idCard")
    private String idCard;

    @ApiModelProperty(value = "bankNo")
    private String bankNo;

    @ApiModelProperty(value = "bankType")
    private String bankType;

    @ApiModelProperty(value = "mobile")
    private String mobile;

    @ApiModelProperty(value = "bindCode")
    private String bindCode;

    @ApiModelProperty(value = "status")
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
