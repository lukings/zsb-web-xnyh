package cn.visolink.system.visitandcallexcel.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_project_protect_rule")
@ApiModel(value = "ProjectProtectRule对象", description = "")
public class ProjectProtectRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "项目ID")
    @TableField("ProjectID")
    private String projectID;

    @ApiModelProperty(value = "项目组织ID")
    @TableField("ProjectOrgID")
    private String projectOrgID;

    @ApiModelProperty(value = "项目组织类别 1、自拓团队  2 代理团队")
    @TableField("ProjectOrgCategory")
    private Boolean projectOrgCategory;

    @ApiModelProperty(value = "单位小时（作废）")
    @TableField("ProtectDays")
    private Boolean protectDays;

    @ApiModelProperty(value = "是否允许重复报备")
    @TableField("IsAllowRepeatReport")
    private Boolean isAllowRepeatReport;

    @ApiModelProperty(value = "是否强制判客 0 不强制 1强制 ")
    @TableField("IsForceCustomer")
    private Boolean isForceCustomer;

    @ApiModelProperty(value = "是否报备业主")
    @TableField("IsReportOwner")
    private Integer isReportOwner;

    @ApiModelProperty(value = " 逾期后是否强制回收客户 0 不强制回收  1强制回收")
    @TableField("IsFouceRecycle")
    private Boolean isFouceRecycle;

    @ApiModelProperty(value = "跟进逾期后是否强制回收客户  0 否  1-是")
    @TableField("IsFollowFouceRecycle")
    private Integer isFollowFouceRecycle;

    @ApiModelProperty(value = "（暂不启用）")
    @TableField("IsVisitAudit")
    private Integer isVisitAudit;

    @ApiModelProperty(value = "报备后到访逾期")
    @TableField("ReportExpireDays")
    private Double reportExpireDays;

    @ApiModelProperty(value = "到访后认购逾期")
    @TableField("VisitExpireDays")
    private Integer visitExpireDays;

    @ApiModelProperty(value = "首访后跟进逾期")
    @TableField("TheFirstVisitFollowupExpireDays")
    private Integer theFirstVisitFollowupExpireDays;

    @ApiModelProperty(value = "下次的跟进逾期")
    @TableField("TheNextVisitFollowupExpireDays")
    private Integer theNextVisitFollowupExpireDays;

    @ApiModelProperty(value = "贝壳树报备逾期")
    @TableField("TreeReportExpireDays")
    private Integer treeReportExpireDays;

    @ApiModelProperty(value = "贝壳树到昂逾期")
    @TableField("TreeVisitExpireDays")
    private Integer treeVisitExpireDays;

    @ApiModelProperty(value = "系统类别 :1 -拓客  2 案场 3 贝壳树   ")
    @TableField("SourceType")
    private Integer sourceType;

    @ApiModelProperty(value = "自渠防截客")
    @TableField("CutGuestInvite")
    private Double cutGuestInvite;

    @ApiModelProperty(value = "外渠防截客")
    @TableField("CutGuestDrainage")
    private Double cutGuestDrainage;

    @ApiModelProperty(value = "0 隐号报备  1 全号报备")
    @TableField("StandbyMode")
    private Integer standbyMode;

    @ApiModelProperty(value = "渠道保护期")
    @TableField("ChannelProtectionPeriod")
    private Double channelProtectionPeriod;

    @ApiModelProperty(value = "渠道保护期预警（天）")
    @TableField("ChannelProtectionPeriodWarning")
    private Integer channelProtectionPeriodWarning;

    @ApiModelProperty(value = "贝壳树经纪人类型 : 1  集团员工  2 项目员工  3 拓客  4 案场  5 中介  6 业主  7  普通用户 8 大客户  ")
    @TableField("TreeType")
    private Integer treeType;

    @TableField("EditTime")
    private Date editTime;

    @TableField("Editor")
    private String editor;

    @ApiModelProperty(value = "跟进预警")
    @TableField("FollowupExpireDaysWarning")
    private Integer followupExpireDaysWarning;

    @ApiModelProperty(value = "到访预警")
    @TableField("VisitingWarning")
    private Integer visitingWarning;

    @ApiModelProperty(value = "签约有效期")
    @TableField("ValidityOfContract")
    private Integer validityOfContract;

    @ApiModelProperty(value = "签约预警")
    @TableField("ValidityOfWarning")
    private Integer validityOfWarning;

    @ApiModelProperty(value = "回款预警")
    @TableField("RemittanceWarning")
    private Integer remittanceWarning;

    @ApiModelProperty(value = "老业主是否可以再次报备 1 可以，0 不可以  （已经签约的老业主，在不同的项目是否可以重复报备）")
    @TableField("OldOwnerReport")
    private Integer oldOwnerReport;

    @ApiModelProperty(value = "客户再次报备 1 可以 0 不可以  （同一个客户，在不同项目，是否可以再次报备）")
    @TableField("OwnerAgain")
    private Integer ownerAgain;


}
