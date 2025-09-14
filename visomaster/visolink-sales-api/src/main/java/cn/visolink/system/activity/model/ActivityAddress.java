package cn.visolink.system.activity.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/6/28
 */
@Data
@ApiModel(value="ActivityAddress对象", description="活动报名地址表")
public class ActivityAddress {

    private String id;

    @ApiModelProperty(value = "活动ID")
    private String activityId;

    @ApiModelProperty(value = "活动ID")
    private String address;

    @ApiModelProperty(value = "活动ID")
    private String longitude;

    @ApiModelProperty(value = "活动ID")
    private String latitude;
}
