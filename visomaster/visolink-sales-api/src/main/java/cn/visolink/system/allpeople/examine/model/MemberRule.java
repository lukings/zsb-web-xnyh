package cn.visolink.system.allpeople.examine.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName MemberRule
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/2 17:15
 **/
@ApiModel(value="会员规则", description="会员规则")
@Data
public class MemberRule {

    @ApiModelProperty(value = "积分")
    private String integral;
    @ApiModelProperty(value = "规则分类(1：获取积分 2：消耗积分)")
    private String ruleType;
    @ApiModelProperty(value = "规则名称")
    private String ruleName;
    @ApiModelProperty(value = "规则ID")
    private String ruleId;
    @ApiModelProperty(value = "1:重复任务 2：一次任务")
    private String isRepeat;
    @ApiModelProperty(value = "1:每天重置  2：客户重置")
    private String repeatType;
    @ApiModelProperty(value = "可完成数量 -1无限制")
    private String repeatNumber;
    @ApiModelProperty(value = "规则描述")
    private String ruleDesc;
    @ApiModelProperty(value = "状态（1启用 0禁用）")
    private String status;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
