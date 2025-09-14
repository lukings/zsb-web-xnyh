package cn.visolink.system.project.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/11/8
 */
@Data
@ApiModel(value = "ResultProjectVO对象", description = "返回数据")
public class ResultProjectVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value ="区域名称")
    private String label;

    @ApiModelProperty(value ="下标")
    private String value;

    @ApiModelProperty(value ="项目招商类型编码")
    private String investmentTypeCode;

    @ApiModelProperty(value ="媒体子类")
    private List<ResultProjectVO> children;
}
