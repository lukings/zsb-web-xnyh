package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ProjectRule
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/6 11:47
 **/
@ApiModel(value="项目规则", description="项目规则")
@Data
public class ProjectRuleDetail {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "项目ID")
    private String projectID;
    @ApiModelProperty(value = "系统类别 :1 -渠道  2 项目业务员 3  上海招商 4北京招商 5广州招商 6深圳招商")
    private String sourceType;
    @ApiModelProperty(value = "掉入公共池类型（1：业务员公池 2：招商公池）")
    private String publicPoolType;
    @ApiModelProperty(value = "转介确认时间（小时）")
    private String referralConfirmationTime;
    @ApiModelProperty(value = "跟进预警（天）")
    private String followupExpireDaysWarning;
    @ApiModelProperty(value = "跟进逾期（天）")
    private String theNextVisitFollowupExpireDays;
    @ApiModelProperty(value = "跟进审批时间（小时）")
    private String followUpConfirmationTime;
    @ApiModelProperty(value = "公客池捞取缓冲期（小时）")
    private String obtainCstConfirmationTime;
    @ApiModelProperty(value = "公客池分配客户保护期（天）")
    private String assignPoolsExpireDays;
    @ApiModelProperty(value = "分配客户保护期（天）")
    private String assignExpireDays;
    @ApiModelProperty(value = "已交房客户保护期（小时）")
    private String deliveryCustomerProtectTime;
    @ApiModelProperty(value = "允许报备客户数")
    private String channelReportMax;
    @ApiModelProperty(value = "每月最大保留客户数")
    private String monthReportMax;
    @ApiModelProperty(value = "项目（招商）客户池保留时间（天）")
    private String projectPoolRetentionTime;
    @ApiModelProperty(value = "区域（总招商）客户池保留时间（天）")
    private String regionPoolRetentionTime;
    @ApiModelProperty(value = "允许淘客数量分类（1：每天 2：总量）")
    private String caseGuestNumberType;
    @ApiModelProperty(value = "案场淘客数量")
    private String caseTaoGuestNumber;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "修改时间")
    private String editTime;
    @ApiModelProperty(value = "修改人")
    private String editor;
    @ApiModelProperty(value = "报备保护期（天）")
    private String reportExpireDays;
    @ApiModelProperty(value = "到访保护期（天）")
    private String visitExpireDays;
    @ApiModelProperty(value = "最大跟进次数（次）")
    private String maxFollowUp;
    @ApiModelProperty(value = "到访预警（天）")
    private String visitingWarning;
    @ApiModelProperty(value = "报备预警（天）")
    private String reportDaysWarning;
    @ApiModelProperty(value = "附件")
    private List<Map> enclosures;
    @ApiModelProperty(value = "修改批次ID")
    private String editBatchId;

    @ApiModelProperty(value = "最大到访次数（次）")
    private String maxVisit;
    @ApiModelProperty(value = "最大拜访次数（次）")
    private String maxComeVisit;
    @ApiModelProperty(value = "拜访保护期（天）")
    private String comeVisitExpireDays;
    @ApiModelProperty(value = "拜访预警")
    private String comeVisitingWarning;

    @ApiModelProperty(value = "转介保护期（天）")
    private String referralExpireDays;
    @ApiModelProperty(value = "转介预警")
    private String referralWarning;
    @ApiModelProperty(value = "转介跟进预警（天）")
    private String referralFollowupExpireDaysWarning;
    @ApiModelProperty(value = "转介跟进逾期（天）")
    private String referralTheNextVisitFollowupExpireDays;
    @ApiModelProperty(value = "转介最大跟进次数（次）")
    private String referralMaxFollowUp;
    @ApiModelProperty(value = "转介到访保护期（天）")
    private String referralVisitExpireDays;
    @ApiModelProperty(value = "转介到访预警（天）")
    private String referralVisitingWarning;
    @ApiModelProperty(value = "转介最大到访次数（次）")
    private String referralMaxVisit;
    @ApiModelProperty(value = "转介拜访保护期（天）")
    private String referralComeVisitExpireDays;
    @ApiModelProperty(value = "转介拜访预警")
    private String referralComeVisitingWarning;
    @ApiModelProperty(value = "转介最大拜访次数（次）")
    private String referralMaxComeVisit;
    @ApiModelProperty(value = "转介区域验证")
    private String referralJudgeNoRegion;
    @ApiModelProperty(value = "转介公海池验证")
    private String referralJudgeNoPool;
    @ApiModelProperty(value = "转介判客阶段")
    private String referralJudgeStage;
    @ApiModelProperty(value = "直接转介保护期（天）")
    private String directReferralExpireDays;
    @ApiModelProperty(value = "直接转介到访保护期（天）")
    private String directReferralVisitExpireDays;
    @ApiModelProperty(value = "直接转介到访预警（天）")
    private String directReferralVisitingWarning;
    @ApiModelProperty(value = "直接转介最大到访次数（次）")
    private String directReferralMaxVisit;

    @ApiModelProperty(value = "万企通保护期（天）")
    private String wqtReportExpireDays;
    @ApiModelProperty(value = "万企通预警（天）")
    private String wqtReportDaysWarning;
    @ApiModelProperty(value = "万企通跟进预警（天）")
    private String wqtFollowupExpireDaysWarning;
    @ApiModelProperty(value = "万企通跟进逾期（天）")
    private String wqtTheNextVisitFollowupExpireDays;
    @ApiModelProperty(value = "万企通最大跟进次数（次）")
    private String wqtMaxFollowUp;
    @ApiModelProperty(value = "万企通到访保护期（天）")
    private String wqtVisitExpireDays;
    @ApiModelProperty(value = "万企通到访预警（天）")
    private String wqtVisitingWarning;
    @ApiModelProperty(value = "万企通最大到访次数（次）")
    private String wqtMaxVisit;
    @ApiModelProperty(value = "万企通拜访保护期（天）")
    private String wqtComeVisitExpireDays;
    @ApiModelProperty(value = "万企通拜访预警（天）")
    private String wqtComeVisitingWarning;
    @ApiModelProperty(value = "万企通最大拜访次数（次）")
    private String wqtMaxComeVisit;

    @ApiModelProperty(value = "区域验证")
    private String judgeNoRegion;
    @ApiModelProperty(value = "公海池验证")
    private String judgeNoPool;
    @ApiModelProperty(value = "判客阶段")
    private String judgeStage;

    @ApiModelProperty(value = "万企通区域验证")
    private String wqtJudgeNoRegion;
    @ApiModelProperty(value = "万企通公海池验证")
    private String wqtJudgeNoPool;
    @ApiModelProperty(value = "万企通判客阶段")
    private String wqtJudgeStage;
    @ApiModelProperty(value = "规则级别")
    private String ruleLevel;
    @ApiModelProperty(value = "项目名称")
    private String entryName;

    @ApiModelProperty(value = "计算三个一")
    private String countThreeOnes;

    @ApiModelProperty(value = "案场最长保护期（天）")
    private String maxFollowUpDays;
    @ApiModelProperty(value = "万企通最长保护期（天）")
    private String wqtMaxFollowUpDays;
    @ApiModelProperty(value = "转介最长保护期（天）")
    private String referralMaxFollowUpDays;

    @ApiModelProperty(value = "线索客户分配客户上限数")
    private String allocationCustomerMax;

    @ApiModelProperty(value = "企业数据库客户保护时效")
    private String enterpriseDatabaseCustomerProtectionDays;

    @ApiModelProperty(value = "未拨打客户保护期（天）")
    private String callNotDialCustomerProtectDays;

    @ApiModelProperty(value = "未接通客户保护期（天）")
    private String callNotConnCustomerProtectDays;

    @ApiModelProperty(value = "已接通客户保护期（天）")
    private String callHasConnCustomerProtectDays;

    @ApiModelProperty(value = "打卡范围（米）")
    private String checkInRange;


    public String[]  excelTitle =  new String[]{
            "规则编码",
            "规则级别",
            "项目名称",
            "案场",
            "报备预警（天）",
            "报备保护期（天）",
            "案场最长保护期（天）",
            "跟进预警（天）",
            "跟进过保（天）",
            "最多延保次数",
            "拜访预警（天）",
            "拜访保护期（天）",
            "拜访最多延保次数",
            "到访预警（天）",
            "到访保护期（天）",
            "到访最多延保次数",
            "项目客户池保留时间（天）",
            "区域客户池保留时间（天）",
            "报备客户上限数",
            "线索客户分配客户上限数",
            "跟进审批时间（小时）",
            "公客池捞取缓冲期（小时）",
            "公客池分配客户保护期（天）",
            "分配客户保护期（天）",
            "已交房客户保护期（小时）",
            "万企通",
            "万企通报备预警（天）",
            "万企通报备保护期（天）",
            "万企通最长保护期（天）",
            "跟进预警（天）",
            "跟进过保（天）",
            "最多延保次数",
            "拜访预警（天）",
            "拜访保护期（天）",
            "拜访最多延保次数",
            "到访预警（天）",
            "到访保护期（天）",
            "到访最多延保次数",
            "转介客户",
            "转介确认时间（小时）",
            "转介预警（天）",
            "转介保护期（天）",
            "转介最长保护期（天）",
            "跟进预警（天）",
            "跟进过保（天）",
            "最多延保次数",
            "拜访预警（天）",
            "拜访保护期（天）",
            "拜访最多延保次数",
            "到访预警（天）",
            "到访保护期（天）",
            "到访最多延保次数",
            "直接转介保护期（天）",
            "到访预警（天）",
            "到访保护期（天）",
            "到访最多延保次数",
            "判客规则",
            "判客阶段",
            "区域与项目报备客户判重",
            "项目公客池判重",
            "万企通判客规则",
            "判客阶段",
            "区域与项目报备客户判重",
            "项目公客池判重",
            "转介客户判客规则",
            "判客阶段",
            "区域与项目报备客户判重",
            "项目公客池判重",
            "计算三个一",
            "企业数据库",
            "企业数据库客户保护时效",
            "话单客户",
            "未拨打客户保护期（天）",
            "未接通客户保护期（天）",
            "已接通客户保护期（天）",
            "打卡范围（米）",

    };

    //设置导出列的数据
    public Object[] toData(){
        return new Object[]{
                getId(),
                getRuleLevel(),
                getEntryName(),
                "案场",
                getReportDaysWarning(),
                getReportExpireDays(),
                getMaxFollowUpDays(),
                getFollowupExpireDaysWarning(),
                getTheNextVisitFollowupExpireDays(),
                getMaxFollowUp(),
                getComeVisitingWarning(),
                getComeVisitExpireDays(),
                getMaxComeVisit(),
                getVisitingWarning(),
                getVisitExpireDays(),
                getMaxVisit(),
                getProjectPoolRetentionTime(),
                getRegionPoolRetentionTime(),
                getChannelReportMax(),
                getAllocationCustomerMax(),
                getFollowUpConfirmationTime(),
                getObtainCstConfirmationTime(),
                getAssignPoolsExpireDays(),
                getAssignExpireDays(),
                getDeliveryCustomerProtectTime(),
                "万企通",
                getWqtReportDaysWarning(),
                getWqtReportExpireDays(),
                getWqtMaxFollowUpDays(),
                getWqtFollowupExpireDaysWarning(),
                getWqtTheNextVisitFollowupExpireDays(),
                getWqtMaxFollowUp(),
                getWqtComeVisitingWarning(),
                getWqtComeVisitExpireDays(),
                getWqtMaxComeVisit(),
                getWqtVisitingWarning(),
                getWqtVisitExpireDays(),
                getWqtMaxVisit(),
                "转介客户",
                getReferralConfirmationTime(),
                getReferralWarning(),
                getReferralExpireDays(),
                getReferralMaxFollowUpDays(),
                getReferralFollowupExpireDaysWarning(),
                getReferralTheNextVisitFollowupExpireDays(),
                getReferralMaxFollowUp(),
                getReferralComeVisitingWarning(),
                getReferralComeVisitExpireDays(),
                getReferralMaxComeVisit(),
                getReferralVisitingWarning(),
                getReferralVisitExpireDays(),
                getReferralMaxVisit(),
                getDirectReferralExpireDays(),
                getDirectReferralVisitingWarning(),
                getDirectReferralVisitExpireDays(),
                getDirectReferralMaxVisit(),
                "判客规则",
                getJudgeStage(),
                getJudgeNoRegion(),
                getJudgeNoPool(),
                "万企通判客规则",
                getWqtJudgeStage(),
                getWqtJudgeNoRegion(),
                getWqtJudgeNoPool(),
                "转介客户判客规则",
                getReferralJudgeStage(),
                getReferralJudgeNoRegion(),
                getReferralJudgeNoPool(),
                getCountThreeOnes(),
                "企业数据库",
                getEnterpriseDatabaseCustomerProtectionDays(),
                "话单客户",
                getCallNotDialCustomerProtectDays(),
                getCallNotConnCustomerProtectDays(),
                getCallHasConnCustomerProtectDays(),
                getCheckInRange()

        };
    }
}
