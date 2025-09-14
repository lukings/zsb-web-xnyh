package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ReferralRecordClue
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/6 10:36
 **/
@ApiModel(value="转介记录", description="转介记录")
@Data
public class ReferralRecord {

    @ApiModelProperty(value = "序号")
    private String rownum;
    @ApiModelProperty(value = "转介人ID")
    private String referralUserID;
    @ApiModelProperty(value = "转介人")
    private String referralUser;
    @ApiModelProperty(value = "接收人ID")
    private String receiverUserID;
    @ApiModelProperty(value = "接收人")
    private String receiverUser;
    @ApiModelProperty(value = "发起时间")
    private String createTime;
    private String createStartTime;
    private String createEndTime;
    @ApiModelProperty(value = "客户姓名")
    private String cstName;
    @ApiModelProperty(value = "客户手机号")
    private String cstMobile;
    @ApiModelProperty(value = "客户手机号(全号)")
    private String cstMobileAll;
    @ApiModelProperty(value = "项目")
    private String projectName;
    @ApiModelProperty(value = "状态（1：发起申请 2：同意  3：拒绝  4：区域同意驳回 5：区域拒绝驳回 6：自动驳回 7:撤销")
    private String status;
    @ApiModelProperty(value = "接收或拒绝时间")
    private String endTime;
    private String endStartTime;
    private String endEndTime;
    @ApiModelProperty(value = "转介类型（1：渠道转介 2：业务员转机会 3:招商转介4:业务员机会转介）")
    private String referralType;
    @ApiModelProperty(value = "审批人")
    private String operator;
    @ApiModelProperty(value = "佣金占比")
    private String commissionRate;
    @ApiModelProperty(value = "业绩占比")
    private String achievementRate;
    @ApiModelProperty(value = "拒绝原因")
    private String rejectionReason;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "公司全称")
    private String oldCstName;

    @ApiModelProperty(value = "接受项目")
    private String receiverProjectName;

    @ApiModelProperty(value = "客户来源")
    private String sourceMode;

    @ApiModelProperty(value = "是否全号")
    private String isShowReferral;

    @ApiModelProperty(value = "过保时间")
    private String salesFollowExpireDate;

    @ApiModelProperty(value = "机会id")
    private String opportunityClueId;

    @ApiModelProperty(value = "线索id")
    private String projectClueId;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    /**
     * 获取数据线索渠道转介
     * @param
     * @return
     */
    public Object[] toData1(boolean isAll, List<String> fileds){
        String mobile = getCstMobile();
        String cstName = getCstName();
        if (isAll){
            mobile = getCstMobileAll();
            cstName = getOldCstName();
        }
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("referralUser")){
            met.add(getReferralUser());
        }
        if(fileds.contains("createTime")){
            met.add(getCreateTime());
        }
        if(fileds.contains("cstName")){
            met.add(cstName);
        }
        if (fileds.contains("sourceMode")){
            met.add(getSourceMode());
        }
        if(fileds.contains("cstMobile")){
            met.add(mobile);
        }
        if(fileds.contains("contacts")){
            met.add(getContacts());
        }
        if(fileds.contains("salesFollowExpireDate")){
            met.add(getSalesFollowExpireDate());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("receiverUser")){
            met.add(getReceiverUser());
        }
        if(fileds.contains("receiverProjectName")){
            met.add(getReceiverProjectName());
        }
        if(fileds.contains("endTime")){
            met.add(getEndTime());
        }
        if(fileds.contains("status")){
            met.add(getStatus());
        }
        if(fileds.contains("rejectionReason")){
            met.add(getRejectionReason());
        }
        if(fileds.contains("isShowReferral")){
            met.add(getIsShowReferral());
        }
        if(fileds.contains("achievementRate")){
            met.add(getAchievementRate());
        }
        if(fileds.contains("commissionRate")){
            met.add(getCommissionRate());
        }
        Object[] objects = met.toArray();
        return objects;

//        return new Object[]{
//                getRownum(), getReferralUser(), getCreateTime(), cstName, mobile, getContacts(),
//                getProjectName(), getReceiverUser(), getReceiverProjectName(), getEndTime(),
//                getStatus(), getAchievementRate(), getCommissionRate()
//        };
    }
    public String[]  courtCaseTitle1 =  new String[]{
            "序号", "转介人", "转介时间", "企业名称", "客户手机号", "联系人", "转介项目",
            "接收人", "接收项目", "处理转介时间", "转介状态", "业绩占比", "佣金占比"};

    /**
     * 获取数据线索业务员转介
     * @param
     * @return
     */
    public Object[] toData2(boolean isAll){
        String mobile = getCstMobile();
        if (isAll){
            mobile = getCstMobileAll();
        }
        return new Object[]{
                getRownum(),getReferralUser(),getCreateTime(),getCstName(),mobile,getProjectName(),
                getOperator(),getEndTime(),getStatus(),getRejectionReason()
        };
    }
    public String[]  courtCaseTitle2 =  new String[]{
            "序号","发起人","发起时间","客户姓名","客户手机号","项目",
            "审批人","审批时间","线索状态","拒绝原因"};

    /**
     * 获取数据机会转介
     * @param
     * @return
     */
    public Object[] toData3(boolean isAll){
        String mobile = getCstMobile();
        if (isAll){
            mobile = getCstMobileAll();
        }
        return new Object[]{
                getRownum(),getReferralUser(),getCreateTime(),getCstName(),mobile,getProjectName(),
                getReceiverUser(),getEndTime(),getStatus(),getAchievementRate(),getCommissionRate()
        };
    }
    public String[]  courtCaseTitle3 =  new String[]{
            "序号","转介人","转介时间","客户姓名","客户手机号","转介项目",
            "接收人","处理转介时间","转介状态","业绩占比","佣金比例"};

}
