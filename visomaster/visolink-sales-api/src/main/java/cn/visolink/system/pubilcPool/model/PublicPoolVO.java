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
@ApiModel(value = "公共池对象", description = "公共池对象")
public class PublicPoolVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rowNum;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "池类型( 1 公共池 2 淘客池)")
    private String poolType;//池类型( 1 公共池 2 淘客池)

    @ApiModelProperty(value = "原池类型( 1 公共池 2 淘客池)")
    private String oldPoolType;//池类型( 1 公共池 2 淘客池)

    @ApiModelProperty(value = "操作时间")
    private String addTime;//操作时间

    @ApiModelProperty(value = "加入类型")
    private String addType;

    @ApiModelProperty(value = "加入类型描述")
    private String addTypeDesc;

    @ApiModelProperty(value = "评价")
    private String evaluateDesc;

    @ApiModelProperty(value = "加入原因")
    private String addReasonType;

    @ApiModelProperty(value = "加入原因描述")
    private String addReasonDesc;

    @ApiModelProperty(value = "激活原因类型")
    private String activateReasonType;

    @ApiModelProperty(value = "激活原因描述")
    private String activateReasonDesc;

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
    private String channelLabel;//操作时间

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

    @ApiModelProperty(value = "最近跟进时间")
    private String latelyFollowUpTime;

    @ApiModelProperty(value = "最近跟进人手机号")
    private String latelyFollowUpMobile;

    @ApiModelProperty(value = "最近跟进内容")
    private String latelyFollowUpContent;

    @ApiModelProperty(value = "加入次数")
    private String addNumber;

    @ApiModelProperty(value = "分配类型")
    private String allocationType;

    @ApiModelProperty(value = "记录类型")
    private String recordType;

    @ApiModelProperty(value = "是否删除（0 否 1 是）")
    private String isDel;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "操作人")
    private String creator;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "业态")
    private String intentionBusiness;

    @ApiModelProperty(value = "浏览数量")
    private String browseNumber;

    @ApiModelProperty(value = "浏览时间")
    private String browseTime;

    @ApiModelProperty(value = "浏览描述")
    private String browseDesc;

    @ApiModelProperty(value = "放弃时间")
    private String discardTime;

    @ApiModelProperty(value = "加入淘客池时间")
    private String addTaoTime;

    private String opportunityClueId;

    private String dataCompleteRate;

    private String dataCompleteAttachRate;

    private String salesTheLatestFollowDate;



    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toPublicData(String isAll) {
        String mobile = "";
        if ("1".equals(isAll)){
            mobile = getCustomerMobileAll();
        }else{
            mobile = getCustomerMobile();
        }
        return new Object[]{
                getRowNum(),getCustomerName(),mobile,getProjectName(),getAddTime(),
                getAddReasonDesc(),getAddTypeDesc(),getActivateReasonDesc(),
                getSourceTypeDesc(),getReportUserName(),getReportTime(),getLatelyFollowUpPerson()
        };
    }
    //公共池
    public String[] courtPublicTitle = new String[]{
            "序号","客户姓名","客户电话","项目","加入公共池时间","加入公共池原因",
            "客户类型","激活原因","成交类型","原报备人","原报备时间","最近跟进人"};
    //淘客池
    public String[] courtTaoTitle = new String[]{
            "序号","客户姓名","客户电话","项目","加入淘客池时间","加入淘客池原因",
            "客户类型","激活原因","成交类型","原报备人","原报备时间","最近跟进人"};

}
