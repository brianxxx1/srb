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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * borrow info
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="BorrowInfo object", description="borrowInfo")
public class BorrowInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String mobile;

    @TableField(exist = false)
    private Map<String, Object> param = new HashMap<>();

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "amount")
    private BigDecimal amount;

    @ApiModelProperty(value = "period")
    private Integer period;

    @ApiModelProperty(value = "borrowYearRate")
    private BigDecimal borrowYearRate;

    @ApiModelProperty(value = "returnMethod 1-equal installments 2-equal principal 3-one time principle interest per month 4-one time principal")
    private Integer returnMethod;

    @ApiModelProperty(value = "moneyUse")
    private Integer moneyUse;

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
