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
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * bid
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Lend object", description="bid")
public class Lend implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "borrowInfoId")
    private Long borrowInfoId;

    @ApiModelProperty(value = "lendNo")
    private String lendNo;

    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "amount")
    private BigDecimal amount;

    @ApiModelProperty(value = "period")
    private Integer period;

    @ApiModelProperty(value = "lendYearRate")
    private BigDecimal lendYearRate;

    @ApiModelProperty(value = "serviceRate")
    private BigDecimal serviceRate;

    @ApiModelProperty(value = "returnMethod")
    private Integer returnMethod;

    @ApiModelProperty(value = "lowestAmount")
    private BigDecimal lowestAmount;

    @ApiModelProperty(value = "investAmount")
    private BigDecimal investAmount;

    @ApiModelProperty(value = "investNum")
    private Integer investNum;

    @ApiModelProperty(value = "publishDate")
    private LocalDateTime publishDate;

    @ApiModelProperty(value = "lendStartDate")
    private LocalDate lendStartDate;

    @ApiModelProperty(value = "lendEndDate")
    private LocalDate lendEndDate;

    @ApiModelProperty(value = "lendInfo")
    private String lendInfo;

    @ApiModelProperty(value = "expectAmount")
    private BigDecimal expectAmount;

    @ApiModelProperty(value = "realAmount")
    private BigDecimal realAmount;

    @ApiModelProperty(value = "status")
    private Integer status;

    @ApiModelProperty(value = "checkTime")
    private LocalDateTime checkTime;

    @ApiModelProperty(value = "checkAdminId")
    private Long checkAdminId;

    @ApiModelProperty(value = "paymentTime")
    private LocalDateTime paymentTime;

    @ApiModelProperty(value = "paymentAdminId")
    private LocalDateTime paymentAdminId;

    @ApiModelProperty(value = "createTime")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "updateTime")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "logically deleted(1:deleted，0:not deleted)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();

}
