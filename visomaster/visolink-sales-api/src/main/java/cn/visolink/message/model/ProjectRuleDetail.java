package cn.visolink.message.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    @ApiModelProperty(value = "到访预警")
    private String visitingWarning;
    @ApiModelProperty(value = "报备预警")
    private String reportDaysWarning;

}
