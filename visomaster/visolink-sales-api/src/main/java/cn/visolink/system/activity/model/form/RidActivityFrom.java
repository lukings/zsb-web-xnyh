package cn.visolink.system.activity.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("乘车活动信息主体")
public class RidActivityFrom {

    @ApiModelProperty(value = "活动id")
    private String id;

    @ApiModelProperty(value = "乘车活动项目id")
    private String projectId;

    @ApiModelProperty(value = "乘车活动项目名称")
    private String projectName;

    @ApiModelProperty(value = "乘车活动楼盘id")
    private String buildBookId;

    @ApiModelProperty(value = "乘车活动楼盘名称")
    private String buildBookName;

    @ApiModelProperty(value = "活动编号")
    private String ridActivityNo;

    @ApiModelProperty(value = "活动名称")
    private String ridActivityName;

    @ApiModelProperty(value = "活动开始时间")
    private String activityBegintime;

    @ApiModelProperty(value = "活动结束时间")
    private String activityEndtime;

    @ApiModelProperty(value = "乘车活动是否开启预约")
    private String isSubscribe;

//    @ApiModelProperty(value = "参与活动的用户身份")
//    private String ridAllowCustomerStatus;

    @ApiModelProperty(value = "接待人员配置")
    private String brokerConfig;

    @ApiModelProperty(value = "活动规则说明")
    private String activityNote;

    @ApiModelProperty(value = "乘车活动状态 1 已开始 2已结束 3 已启用 4 已禁用")
    private String status;

    @ApiModelProperty(value = "当前用户id")
    private String userId;
}
