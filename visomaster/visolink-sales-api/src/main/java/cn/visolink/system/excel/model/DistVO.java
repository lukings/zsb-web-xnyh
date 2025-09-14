package cn.visolink.system.excel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DistVOVO
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/8/6 17:11
 **/
@Data
@ApiModel(value = "DistVO对象", description = "字典数据")
public class DistVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value ="名称")
    private String label;

    @ApiModelProperty(value ="下标")
    private String value;

    @ApiModelProperty(value ="子类")
    private List<DistVO> children;

}
