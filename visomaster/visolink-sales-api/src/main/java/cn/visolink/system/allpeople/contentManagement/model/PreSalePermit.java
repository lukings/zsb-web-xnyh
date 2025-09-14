package cn.visolink.system.allpeople.contentManagement.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName PreSalePermit
 * @Author wanggang
 * @Description //TODO 预售证
 * @Date 2020/4/21 15:32
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("a_build_book_presalepermit")
@ApiModel(value = "预售证对象", description = "预售证表")
public class PreSalePermit {

    @ApiModelProperty(value = "id")
    @TableField("id")
    private String id;
    @ApiModelProperty(value = "楼书ID")
    @TableField("BuildBookID")
    private String BuildBookID;
    @ApiModelProperty(value = "预售证号")
    @TableField("PreSalePermit")
    private String PreSalePermit;
    @ApiModelProperty(value = "发证时间")
    @TableField("CertificationTime")
    private String CertificationTime;
    @ApiModelProperty(value = "绑定楼栋")
    @TableField("BindingBuild")
    private String BindingBuild;
    @ApiModelProperty(value = "创建人")
    @TableField("Creator")
    private String Creator;
    @ApiModelProperty(value = "创建时间")
    @TableField("CreateTime")
    private String CreateTime;

}
