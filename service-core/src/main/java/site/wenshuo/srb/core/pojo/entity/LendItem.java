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
 * confirmed bid
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="LendItem object", description="confirmed bid")
public class LendItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "lendItemNo")
    private String lendItemNo;

    @ApiModelProperty(value = "lendId")
    private Long lendId;

    @ApiModelProperty(value = "investUserId")
    private Long investUserId;

    @ApiModelProperty(value = "investName")
    private String investName;

    @ApiModelProperty(value = "investAmount")
    private BigDecimal investAmount;

    @ApiModelProperty(value = "lendYearRate")
    private BigDecimal lendYearRate;

    @ApiModelProperty(value = "investTime")
    private LocalDateTime investTime;

    @ApiModelProperty(value = "lendStartDate")
    private LocalDate lendStartDate;

    @ApiModelProperty(value = "lendEndDate")
    private LocalDate lendEndDate;

    @ApiModelProperty(value = "expectAmount")
    private BigDecimal expectAmount;

    @ApiModelProperty(value = "realAmount")
    private BigDecimal realAmount;

    @ApiModelProperty(value = "status（0：default 1：paid 2：returned）")
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
