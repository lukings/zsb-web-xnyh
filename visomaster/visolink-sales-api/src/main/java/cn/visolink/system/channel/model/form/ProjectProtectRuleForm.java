package cn.visolink.system.channel.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * ProjectProtectRuleForm对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "ProjectProtectRule对象", description = "")
public class ProjectProtectRuleForm extends Page {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目组织ID")
    private String projectOrgId;

    @ApiModelProperty(value = "项目组织类别 1、自拓团队  2 代理团队")
    private Integer projectOrgCategory;

    @ApiModelProperty(value = "单位小时（作废）")
    private Integer protectDays;

    @ApiModelProperty(value = "是否允许重复报备")
    private Integer isAllowRepeatReport;

    @ApiModelProperty(value = "是否强制判客 0 不强制 1强制 ")
    private Integer isForceCustomer;

    @ApiModelProperty(value = "是否报备业主")
    private Integer isReportOwner;

    @ApiModelProperty(value = " 逾期后是否强制回收客户 0 不强制回收  1强制回收")
    private Integer isFouceRecycle;

    @ApiModelProperty(value = "跟进逾期后是否强制回收客户  0 否  1-是")
    private Integer isFollowFouceRecycle;

    @ApiModelProperty(value = "（暂不启用）")
    private Integer isVisitAudit;

    @ApiModelProperty(value = "报备后到访逾期")
    private Integer reportExpireDays;

    @ApiModelProperty(value = "到访后认购逾期")
    private Integer visitExpireDays;

    @ApiModelProperty(value = "首访后跟进逾期")
    private Integer theFirstVisitFollowupExpireDays;

    @ApiModelProperty(value = "下次的跟进逾期")
    private Integer theNextVisitFollowupExpireDays;

    @ApiModelProperty(value = "贝壳树报备逾期")
    private Integer treeReportExpireDays;

    @ApiModelProperty(value = "贝壳树到昂逾期")
    private Integer treeVisitExpireDays;

    @ApiModelProperty(value = "系统类别 :1 -拓客  2 案场 3 贝壳树   ")
    private Integer sourceType;

    @ApiModelProperty(value = "自渠防截客")
    private Double cutGuestInvite;

    @ApiModelProperty(value = "外渠防截客")
    private Double cutGuestDrainage;

    @ApiModelProperty(value = "0 隐号报备  1 全号报备")
    private Integer standbyMode;

    @ApiModelProperty(value = "渠道保护期")
    private Integer channelProtectionPeriod;

    @ApiModelProperty(value = "渠道保护期预警")
    private Integer channelProtectionPeriodWarning;

    @ApiModelProperty(value = "贝壳树经纪人类型 : 1  集团员工  2 项目员工  3 拓客  4 案场  5 中介  6 业主  7  普通用户 8 大客户  ")
    private Integer treeType;

    private String editTime;

    private String editor;
    @ApiModelProperty(value = "报备预警")
    private Integer reportDaysWarning;

    @ApiModelProperty(value = "跟进预警")
    private Integer followupExpireDaysWarning;

    @ApiModelProperty(value = "到访预警")
    private Integer visitingWarning;

    @ApiModelProperty(value = "签约有效期")
    private Integer validityOfContract;

    @ApiModelProperty(value = "签约预警")
    private Integer validimessatyOfWarning;

    @ApiModelProperty(value = "回款预警")
    private Integer remittanceWarning;

    @ApiModelProperty(value = "老业主是否可以再次报备 1 可以，0 不可以  （已经签约的老业主，在不同的项目是否可以重复报备）")
    private Integer oldOwnerReport;

    @ApiModelProperty(value = "客户再次报备 1 可以 0 不可以  （同一个客户，在不同项目，是否可以再次报备）")
    private Integer ownerAgain;

    @ApiModelProperty(value = "状态")
    private String modifyType;

    @ApiModelProperty(value = "操作人")
    private String userId;

    @ApiModelProperty(value = "报备模式")
    private Integer standbyModeStandbyMode;

    @ApiModelProperty(value = "签约预警")
    private Integer validityOfWarning;

    @ApiModelProperty(value = "是否是代理")
    private String entrance;

    @ApiModelProperty(value = "组织ID")
    private String orgId;

    @ApiModelProperty(value = "门店名称")
    private String companyName;

    @ApiModelProperty(value = "门店ID")
    private String companyId;

    @ApiModelProperty(value = "门店编码")
    private String companyCode;

    @ApiModelProperty(value = "门店展示名称")
    private String orgName;

    @ApiModelProperty(value = "门店组织状态")
    private String orgStatus;

    @ApiModelProperty(value = "总公司ID")
    private String headquartersId;
    @ApiModelProperty(value = "总公司名称")
    private String headquartersName;

    @ApiModelProperty(value = "门店创建时间")
    private String createTime;

    @ApiModelProperty(value = "门店有效开始时间")
    private String startTime;
    @ApiModelProperty(value = "门店有效结束时间")
    private String endTime;

    @ApiModelProperty(value = "中介报备是否需要验证")
    private Integer idyVerification;

    @ApiModelProperty(value = "自渠报备提示归属人")
    private Integer promptAttribution;

    @ApiModelProperty(value = "跟进逾期是否进入公共池")
    private Integer isEnterPublicPool;

    @ApiModelProperty(value = "合作公司ID")
    private String AuthCompanyID;
    @ApiModelProperty(value = "允许报备客户数")
    private Integer channelReportMax;

    @ApiModelProperty(value = "是否淘客 (0 否 1 是)")
    private Integer isTaoGuest;

    @ApiModelProperty(value = "淘客数量")
    private Integer taoGuestNumber;

    @ApiModelProperty(value = "淘客数量分类")
    private Integer taoGuestNumberType;

    @ApiModelProperty(value = "是否开启案场淘客 (0 否 1 是)")
    private Integer isCaseTaoGuest;

    @ApiModelProperty(value = "案场淘客数量")
    private Integer caseTaoGuestNumber;

    @ApiModelProperty(value = "允许淘客数量分类（1：每天 2：总量）")
    private Integer caseGuestNumberType;

}
