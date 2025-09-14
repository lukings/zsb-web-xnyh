package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ClueReferralVo
 * @Author wanggang
 * @Description //线索转介记录
 * @Date 2022/3/30 9:23
 **/
@Data
@ApiModel(value = "ClueReferralVo对象", description = "线索转介记录")
public class ClueReferralVo {

    @ApiModelProperty(value = "序号")
    private String rownum;
    @ApiModelProperty(value = "客户姓名")
    private String customerName;
    @ApiModelProperty(value = "客户手机号隐号")
    private String customerMobile;
    @ApiModelProperty(value = "客户手机号全号")
    private String customerMobileAll;
    @ApiModelProperty(value = "项目ID")
    private String projectId;
    @ApiModelProperty(value = "项目")
    private String projectName;
    @ApiModelProperty(value = "发起人ID")
    private String referralUserID;
    @ApiModelProperty(value = "发起人")
    private String referralUser;
    @ApiModelProperty(value = "接收人ID（或审批人）")
    private String receiverUserID;
    @ApiModelProperty(value = "接收人（或审批人）")
    private String receiverUser;
    @ApiModelProperty(value = "线索ID")
    private String ProjectClueId;
    @ApiModelProperty(value = "状态（1：发起申请 2：同意  3：拒绝 ）")
    private String status;
    @ApiModelProperty(value = "发起时间")
    private String createTime;
    @ApiModelProperty(value = "操作时间")
    private String endTime;
    @ApiModelProperty(value = "拒绝原因")
    private String rejectionReason;

}
