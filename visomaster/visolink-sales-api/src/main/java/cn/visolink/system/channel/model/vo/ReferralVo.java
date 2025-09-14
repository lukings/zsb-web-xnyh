package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ReferralVo
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/11 19:48
 **/
@Data
@ApiModel(value="转介客户信息", description="转介客户信息")
public class ReferralVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "机会ID")
    private String opportunityClueId;

    @ApiModelProperty(value = "线索ID")
    private String projectClueId;

    @ApiModelProperty(value = "转介项目Id")
    private String referralProjectId;

    @ApiModelProperty(value = "接收项目Id")
    private String receiverProjectId;

    @ApiModelProperty(value = "转介人ID")
    private String referralUserID;

    @ApiModelProperty(value = "接收人ID")
    private String receiverUserID;

    @ApiModelProperty(value = "发起时间")
    private String createTime;

    @ApiModelProperty(value = "状态（1：发起申请 2：同意  3：拒绝  4：区域同意驳回 5：区域拒绝驳回 6：自动驳回）")
    private String status;

    @ApiModelProperty(value = "接收或拒绝时间")
    private String endTime;

    @ApiModelProperty(value = "拒绝原因")
    private String rejectionReason;

    @ApiModelProperty(value = "转介类型（1：渠道转介 2：业务员转机会 3:招商转介）")
    private String referralType;

    @ApiModelProperty(value = "操作人（业务员（渠道）或项目负责人或区域负责人）")
    private String operator;

    @ApiModelProperty(value = "失效时间")
    private String invalidTime;

    @ApiModelProperty(value = "佣金占比")
    private String commissionRate;

    @ApiModelProperty(value = "业绩占比")
    private String achievementRate;

    @ApiModelProperty(value = "转介项目")
    private String referralProjectName;

    @ApiModelProperty(value = "接收项目")
    private String receiverProjectName;

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "客户手机号")
    private String customerMobile;

    @ApiModelProperty(value = "客户性别")
    private String customerGender;

    @ApiModelProperty(value = "意向等级")
    private String tradeLevel;

    @ApiModelProperty(value = "录入时间")
    private String reportCreateTime;

    @ApiModelProperty(value = "接收人姓名")
    private String receiverUserName;

    @ApiModelProperty(value = "客户证件号")
    private String customerCardNum;

    @ApiModelProperty(value = "客户性质 0:个人 1:公司")
    private String customerType;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "案场归属人")
    private String salesAttributionId;

    @ApiModelProperty(value = "项目Id")
    private String projectId;

    @ApiModelProperty(value = "二级审批项目Id")
    private String childProjectId;

    @ApiModelProperty(value = "跟进角色")
    private String followUpUserRole;

    @ApiModelProperty(value = "客户等级")
    private String customerLevel;

    @ApiModelProperty(value = "是否三个一前跟进(0 三个一前 1 三个一后)")
    private String isThreeOnesAfterStatus;

    @ApiModelProperty(value = "过保时间")
    private String salesFollowExpireDate;

    @ApiModelProperty(value = "过保预警时间")
    private String salesFollowExpireWarningDate;

}
