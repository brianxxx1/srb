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

 * @author Wenshuo
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="BorrowerAttach object", description="borrower uploaded resource")
public class BorrowerAttach implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "borrowerId")
    private Long borrowerId;

    @ApiModelProperty(value = "imageType（idCard1：id card front，idCard2：id card back，house,card）")
    private String imageType;

    @ApiModelProperty(value = "imageUrl")
    private String imageUrl;

    @ApiModelProperty(value = "imageName")
    private String imageName;

    @ApiModelProperty(value = "createTime")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "updateTime")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "logically deleted(1:yes，0:no)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;


}
