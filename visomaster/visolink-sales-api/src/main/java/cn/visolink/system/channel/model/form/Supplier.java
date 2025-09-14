package cn.visolink.system.channel.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName Supplier
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/17 9:36
 **/
@NoArgsConstructor
@Data
@ApiModel(value="供应商对象", description="供应商表")
public class Supplier implements Serializable {

    @ApiModelProperty(value = "入库时间")
    private String rktime;
    @ApiModelProperty(value = "入库状态")
    private int rkstatus;
    @ApiModelProperty(value = "是否删除")
    private int isDelete;
    @ApiModelProperty(value = "办公地址")
    private String bgarea;
    @ApiModelProperty(value = "更新时间")
    private String updateTime;
    @ApiModelProperty(value = "注册类型")
    private String type;
    @ApiModelProperty(value = "供方编号（全局唯一）")
    private String businessCode;
    @ApiModelProperty(value = "统一社会信用代码")
    private String tyxydb;
    @ApiModelProperty(value = "供方名称")
    private String name;
    @ApiModelProperty(value = "营业执照号")
    private String yyzz;
    @ApiModelProperty(value = "库内状态")
    private int status;
}
