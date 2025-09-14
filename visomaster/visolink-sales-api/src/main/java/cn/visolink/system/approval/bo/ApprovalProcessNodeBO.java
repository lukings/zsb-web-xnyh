package cn.visolink.system.approval.bo;

import cn.visolink.common.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("审批流程节点BO")
public class ApprovalProcessNodeBO extends BaseBO {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("流程配置ID")
    private String processConfigId;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("节点类型（1：审批节点 2：抄送节点）")
    private String nodeType;

    @ApiModelProperty("节点顺序")
    private Integer nodeOrder;

    @ApiModelProperty("审批岗位（JSON格式，多个岗位用逗号分隔）")
    private String approvalJobs;

    @ApiModelProperty("抄送岗位（JSON格式，多个岗位用逗号分隔）")
    private String ccJobs;

    @ApiModelProperty("超时天数（0：不超时）")
    private Integer timeoutDays;

    @ApiModelProperty("是否必填（0：否 1：是）")
    private Integer isRequired;

    @ApiModelProperty("审批岗位列表")
    private List<String> approvalJobList;

    @ApiModelProperty("抄送岗位列表")
    private List<String> ccJobList;

    @ApiModelProperty("节点类型名称")
    private String nodeTypeName;
}
