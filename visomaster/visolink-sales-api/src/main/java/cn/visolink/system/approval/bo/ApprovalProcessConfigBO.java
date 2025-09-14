package cn.visolink.system.approval.bo;

import cn.visolink.common.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("审批流程配置BO")
public class ApprovalProcessConfigBO extends BaseBO {

    @ApiModelProperty("主键ID")
    private String id;

    @ApiModelProperty("流程名称")
    private String processName;

    @ApiModelProperty("流程类型（1：相似客户审批 2：客户信息变更审批 3：电话/微信跟进审批 4：电话/微信且满足三个一的跟进审批 5：上门拜访跟进审批 6：上门拜访且满足三个一的跟进审批 7：邀约到访跟进审批 8：邀约到访且满足三个一的跟进审批 9：自然来访跟进审批 10：自然来访且满足三个一的跟进审批 11：其他跟进审批 12：其他跟进且满足三个一的审批）")
    private String processType;

    @ApiModelProperty("级别类型（1：集团级 2：区域级 3：项目级）")
    private String levelType;

    @ApiModelProperty("区域ID（区域级时必填）")
    private String regionId;

    @ApiModelProperty("项目ID（项目级时必填）")
    private String projectId;

    @ApiModelProperty("是否启用（0：禁用 1：启用）")
    private Integer isEnabled;

    @ApiModelProperty("是否强制（0：否 1：是，集团级专用）")
    private Integer isForce;

    @ApiModelProperty("流程节点列表")
    private List<ApprovalProcessNodeBO> nodes;

    @ApiModelProperty("区域名称")
    private String regionName;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("流程类型名称")
    private String processTypeName;

    @ApiModelProperty("级别类型名称")
    private String levelTypeName;
}
