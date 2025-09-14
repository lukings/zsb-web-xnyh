package cn.visolink.system.householdregistration.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ProBatchVO
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/8/6 17:11
 **/
@Data
@ApiModel(value = "ProBatchVO对象", description = "返回数据")
public class ProBatchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value ="区域名称")
    private String label;

    @ApiModelProperty(value ="下标")
    private String value;

    @ApiModelProperty(value ="是否选中")
    private String isChecked = "0";

    @ApiModelProperty(value ="媒体子类")
    private List<ProBatchVO> children;

}
