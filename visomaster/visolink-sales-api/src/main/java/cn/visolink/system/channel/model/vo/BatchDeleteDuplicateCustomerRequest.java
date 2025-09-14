package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除重复客户请求
 */
@Data
@ApiModel(value = "批量删除重复客户请求", description = "批量删除重复客户请求参数")
public class BatchDeleteDuplicateCustomerRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目ID", required = true)
    private String projectId;

    @ApiModelProperty(value = "客户ID列表", required = true)
    private List<String> customerIds;

    @ApiModelProperty(value = "操作人ID", required = true)
    private String operatorId;

    @ApiModelProperty(value = "操作人姓名", required = true)
    private String operatorName;
}
