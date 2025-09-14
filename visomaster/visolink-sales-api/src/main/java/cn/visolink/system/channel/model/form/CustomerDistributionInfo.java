package cn.visolink.system.channel.model.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luqianqian
 * @Description: 客户重分配信息
 * @date 2025/1/23 14:30
 */
@Data
public class CustomerDistributionInfo {
    @ApiModelProperty(value = "岗位编码")
    private String jobCode;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "人员ID")
    private String salesAttributionId;

    @ApiModelProperty(value = "人员名称")
    private String salesAttributionName;
}
