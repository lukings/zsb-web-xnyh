package cn.visolink.system.channel.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName IntermediaryStores
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/17 14:40
 **/
@Data
@ApiModel(value="中介门店对象", description="中介门店")
public class IntermediaryStores implements Serializable {

    @ApiModelProperty(value = "门店ID")
    private String companyId;
    @ApiModelProperty(value = "项目ID")
    private String projectId;
    @ApiModelProperty(value = "门店新增方式 1:新增 2：引入")
    private String addType;
    @ApiModelProperty(value = "门店实际名称")
    private String companyName;
    @ApiModelProperty(value = "总公司ID")
    private String headquartersId;
    @ApiModelProperty(value = "总公司名称")
    private String headquartersName;
    @ApiModelProperty(value = "门店组织ID")
    private String orgId;
    @ApiModelProperty(value = "门店展示名称")
    private String orgName;
    @ApiModelProperty(value = "有效开始时间")
    private String startTime;
    @ApiModelProperty(value = "有效结束时间")
    private String endTime;
    @ApiModelProperty(value = "防截客时间(小时)")
    private String cutGuestDrainage;
    @ApiModelProperty(value = "报备保护期(小时)")
    private String reportExpireDays;
    @ApiModelProperty(value = "报备预警(小时)")
    private String reportDaysWarning;
    @ApiModelProperty(value = "渠道保护期(天)")
    private String channelProtectionPeriod;
    @ApiModelProperty(value = "渠道预警(天)")
    private String channelProtectionPeriodWarning;
    @ApiModelProperty(value = "是否启用")
    private String orgStatus = "1";
    @ApiModelProperty(value = "是否删除")
    private String isDel = "0";
    @ApiModelProperty(value = "组织排序")
    private String listIndex;
    @ApiModelProperty(value = "报备模式 0 隐号报备  1 全号报备")
    private String standbyMode;
    @ApiModelProperty(value = "中介报备全号是否需要验证 1 验证，0 不验证")
    private String idyVerification;

}
