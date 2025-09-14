package cn.visolink.system.allpeople.examine.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName IntegralLogs
 * @Author wanggang
 * @Description //积分明细
 * @Date 2022/4/2 16:16
 **/
@ApiModel(value="积分明细", description="积分明细")
@Data
public class IntegralLogs {

    @ApiModelProperty(value = "会员id")
    private String brokerId;
    @ApiModelProperty(value = "会员姓名")
    private String name;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "积分")
    private String integral;
    @ApiModelProperty(value = "记录类型（1：收入 2：支出）")
    private String logType;
    @ApiModelProperty(value = "规则名称")
    private String ruleName;
    @ApiModelProperty(value = "规则ID")
    private String ruleId;
    @ApiModelProperty(value = "记录时间")
    private String createTime;
}
