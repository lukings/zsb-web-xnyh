package cn.visolink.system.approval.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("审批流程查询BO")
public class ApprovalProcessQueryBO {

    @ApiModelProperty("流程类型")
    private String processType;

    @ApiModelProperty("级别类型（1：集团级 2：区域级 3：项目级）")
    private String levelType;

    @ApiModelProperty("区域ID")
    private String regionId;

    @ApiModelProperty("项目ID")
    private String projectId;

    @ApiModelProperty("是否启用（0：禁用 1：启用）")
    private Integer isEnabled;

    @ApiModelProperty("流程名称（模糊查询）")
    private String processName;

    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("每页大小")
    private Integer pageSize = 10;
}
