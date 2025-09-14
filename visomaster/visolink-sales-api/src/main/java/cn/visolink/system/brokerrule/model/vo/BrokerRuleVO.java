package cn.visolink.system.brokerrule.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/2/1
 */
@Data
@ApiModel(value = "BrokerRuleVO对象", description = "")
public class BrokerRuleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int num;//序号

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "规则表id")
    private String ruleID;

    @ApiModelProperty(value = "全民经纪人身份类型")
    private String brokerType;

    @ApiModelProperty(value = "活动名称")
    private String activityId;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "项目区域")
    private String projectArea;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "佣金规则")
    private String brokerageRule;

    @ApiModelProperty(value = "是否启用")
    private String isEnable;

    @ApiModelProperty(value = "是否启用描述")
    private String isEnableName;

    @ApiModelProperty(value = "规则启动时间")
    private String enableDate;

    @ApiModelProperty(value = "规则截止时间")
    private String endDate;

    @ApiModelProperty(value = "防截客时间")
    private Double preventIntercept;

    @ApiModelProperty(value = "报备逾期保护期（小时）")
    private String reportExpireDays;

    @ApiModelProperty(value = "报备逾期保护期（小时）")
    private String reportDaysWarning;

    @ApiModelProperty(value = "渠道逾期保护期（天）")
    private String channelProtectionPeriod;

    @ApiModelProperty(value = "渠道预警保护期（天）")
    private String channelProtectionPeriodWarning;

    @ApiModelProperty(value = "允许报备客户数")
    private String reportMax;

    @ApiModelProperty(value = "创建时间")
    private String createDate;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "创建人姓名")
    private String createUserName;

    @ApiModelProperty(value = "修改时间")
    private String editDate;

    @ApiModelProperty(value = "修改人")
    private String editUser;

    @ApiModelProperty(value = "规则详情")
    private String ruleDesc;

    @ApiModelProperty(value = "是否禁用")
    private String isDisabled;

    @ApiModelProperty(value = "活动有效性规则")
    private String ruleValidity;

    @ApiModelProperty(value = "是否禁用描述")
    private String isDisabledName;

    @ApiModelProperty(value = "禁用时间")
    private String disabledTime;

    @ApiModelProperty(value = "禁用人ID")
    private String disUser;

    @ApiModelProperty(value = "禁用人姓名")
    private String disUserName;

    @ApiModelProperty(value = "项目咨询电话")
    private String hotLine;


}
