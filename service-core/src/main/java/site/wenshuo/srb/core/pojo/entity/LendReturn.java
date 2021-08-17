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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * returned money
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="LendReturn object", description="returned money")
public class LendReturn implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "lendId")
    private Long lendId;

    @ApiModelProperty(value = "borrowInfoId")
    private Long borrowInfoId;

    @ApiModelProperty(value = "returnNo")
    private String returnNo;

    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "amount")
    private BigDecimal amount;

    @ApiModelProperty(value = "baseAmount")
    private BigDecimal baseAmount;

    @ApiModelProperty(value = "currentPeriod")
    private Integer currentPeriod;

    @ApiModelProperty(value = "lendYearRate")
    private BigDecimal lendYearRate;

    @ApiModelProperty(value = "returnMethod 1-equal installments 2-equal principal 3-one time principle interest per month 4-one time principal")
    private Integer returnMethod;

    @ApiModelProperty(value = "principal")
    private BigDecimal principal;

    @ApiModelProperty(value = "interest")
    private BigDecimal interest;

    @ApiModelProperty(value = "total")
    private BigDecimal total;

    @ApiModelProperty(value = "fee")
    private BigDecimal fee;

    @ApiModelProperty(value = "returnDate")
    private LocalDate returnDate;

    @ApiModelProperty(value = "realReturnTime")
    private LocalDateTime realReturnTime;

    @ApiModelProperty(value = "overdue")
    @TableField("is_overdue")
    private Boolean overdue;

    @ApiModelProperty(value = "overdueTotal")
    private BigDecimal overdueTotal;

    @ApiModelProperty(value = "last")
    @TableField("is_last")
    private Boolean last;

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
