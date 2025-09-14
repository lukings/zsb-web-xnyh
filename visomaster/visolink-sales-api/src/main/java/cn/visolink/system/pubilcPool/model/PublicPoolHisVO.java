package cn.visolink.system.pubilcPool.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2021/5/21
 */
@Data
@ApiModel(value = "公共池记录对象", description = "公共池记录对象")
public class PublicPoolHisVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rowNum;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "公共池id")
    private String publicPoolId;

    @ApiModelProperty(value = "批次id")
    private String batchId;

    @ApiModelProperty(value = "现池类型( 1 公共池 2 淘客池)")
    private String poolType;

    @ApiModelProperty(value = "源池类型")
    private String oldPoolType;

    @ApiModelProperty(value = "加入类型")
    private String addType;

    @ApiModelProperty(value = "加入类型描述")
    private String addTypeDesc;

    @ApiModelProperty(value = "激活原因类型")
    private String activateReasonType;

    @ApiModelProperty(value = "激活原因描述")
    private String activateReasonDesc;

    @ApiModelProperty(value = "记录类型 (1 放弃 2 淘客)")
    private String recordType;

    @ApiModelProperty(value = "记录时间")
    private String recordTime;

    @ApiModelProperty(value = "放弃时间")
    private String discardTime;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "淘客时间")
    private String taoGuestTime;

    @ApiModelProperty(value = "放弃人名称")
    private String operatorName;

    @ApiModelProperty(value = "放弃原因")
    private String discardReason;

    @ApiModelProperty(value = "评价")
    private String evaluateDesc;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "客户id")
    private String customerBasicId;

    @ApiModelProperty(value = "线索id")
    private String projectClueId;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "客户手机号(隐号)")
    private String customerMobile;

    @ApiModelProperty(value = "客户手机号(全号)")
    private String customerMobileAll;

    @ApiModelProperty(value = "客户性别")
    private String customerGender;

    @ApiModelProperty(value = "客储等级 1正常报备  1.5   巡展报备，外展报备  2     来访客户  2.5  小卡，大卡  3    认购  4    签约、退房、退订等")
    private String customerLevel;

    @ApiModelProperty(value = "意向等级")
    private String level;

    @ApiModelProperty(value = "渠道标签")
    private String channelLabel;

    @ApiModelProperty(value = "线索状态 1：未到访 2：已到访 3：排小卡 4：排大卡 5：订房 6：认筹 7：认购 8：签约 9：作废")
    private String clueStatus;

    @ApiModelProperty(value = "成交渠道 1 中介 2 自渠 3 自然到访 4 全民经纪人5 历史数据")
    private String sourceType;

    @ApiModelProperty(value = "成交渠道描述")
    private String sourceTypeDesc;

    @ApiModelProperty(value = "媒体大类id")
    private String mainMediaId;

    @ApiModelProperty(value = "媒体大类名称")
    private String mainMediaName;

    @ApiModelProperty(value = "媒体子类id")
    private String subMediaId;

    @ApiModelProperty(value = "媒体子类名称")
    private String subMediaName;

    @ApiModelProperty(value = "报备人id")
    private String reportUserId;

    @ApiModelProperty(value = "报备人名称")
    private String reportUserName;

    @ApiModelProperty(value = "原报备人id")
    private String oldReportUserId;

    @ApiModelProperty(value = "原报备人名称")
    private String oldReportUserName;

    @ApiModelProperty(value = "报备时间")
    private String reportTime;

    @ApiModelProperty(value = "置业顾问id")
    private String salesId;

    @ApiModelProperty(value = "置业顾问名称")
    private String salesName;

    @ApiModelProperty(value = "首访时间")
    private String theFirstVisitDate;

    @ApiModelProperty(value = "最近跟进人")
    private String latelyFollowUpPerson;

    @ApiModelProperty(value = "最近跟进人手机号")
    private String latelyFollowUpMobile;

    @ApiModelProperty(value = "最近跟进时间")
    private String latelyFollowUpTime;

    @ApiModelProperty(value = "主键id")
    private String latelyFollowUpContent;

    @ApiModelProperty(value = "认购时间")
    private String subscribeTime;

    @ApiModelProperty(value = "加入淘客池时间")
    private String addTaoTime;

    @ApiModelProperty(value = "分配类型（1：固定分配 2：进入淘客池）")
    private String allocationType;

    //放弃记录
    public Object[] toGiveUpData(String isAll) {
        String mobile = "";
        if ("1".equals(isAll)){
            mobile = getCustomerMobileAll();
        }else{
            mobile = getCustomerMobile();
        }
        return new Object[]{
                getRowNum(),getOperatorName(),getDiscardTime(),getCustomerName(),mobile,getSourceTypeDesc(),getProjectName(),
                getClueStatus(),getDiscardReason(),getOldReportUserName(),getReportTime()
        };
    }

    //放弃记录
    public String[] courtGiveUpTitle = new String[]{
            "序号","放弃人","放弃时间","客户姓名","客户电话","成交类型","项目","客户状态","放弃原因",
            "原报备人","原报备时间"};

    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toTaoData(String isAll) {
        String mobile = "";
        if ("1".equals(isAll)){
            mobile = getCustomerMobileAll();
        }else{
            mobile = getCustomerMobile();
        }
        return new Object[]{
                getRowNum(),getOperatorName(),getRecordTime(),getCustomerName(),mobile,getProjectName(),
                getSourceTypeDesc(),getAddTypeDesc(),getActivateReasonDesc(),
                getAddTaoTime(),getOldReportUserName(),getReportTime(),getLatelyFollowUpPerson(),getTheFirstVisitDate(),getSubscribeTime()
        };
    }



    //淘客记录
    public String[] courtTaoTitle = new String[]{
            "序号","淘客人","淘客时间","客户姓名","客户电话","项目","成交类型",
            "客户类型","激活原因","加入淘客池时间","原报备人","原报备时间","最近跟进人","首访时间","认购时间"};

}
