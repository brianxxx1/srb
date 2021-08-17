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
 * Borrower
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Borrower object", description="Borrower")
public class Borrower implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "user id")
    private Long userId;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "id card")
    private String idCard;

    @ApiModelProperty(value = "cellphone")
    private String mobile;

    @ApiModelProperty(value = "sex（1：male 0：female）")
    private Integer sex;

    @ApiModelProperty(value = "age")
    private Integer age;

    @ApiModelProperty(value = "education")
    private Integer education;

    @ApiModelProperty(value = "is_marry（1：yes 0：no）")
    @TableField("is_marry")
    private Boolean marry;

    @ApiModelProperty(value = "industry")
    private Integer industry;

    @ApiModelProperty(value = "income")
    private Integer income;

    @ApiModelProperty(value = "returnSource")
    private Integer returnSource;

    @ApiModelProperty(value = "contactsName")
    private String contactsName;

    @ApiModelProperty(value = "contactsMobile")
    private String contactsMobile;

    @ApiModelProperty(value = "contactsRelation")
    private Integer contactsRelation;

    @ApiModelProperty(value = "status（0：unconfirmed，1：confirming， 2：confirmed， -1：failed）")
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
