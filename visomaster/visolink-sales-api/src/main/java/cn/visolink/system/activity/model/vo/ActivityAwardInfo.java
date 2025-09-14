package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/10/21 11:46
 */

@Data
@ApiModel(value="ActivityAwardInfo对象", description="奖项表")
public class ActivityAwardInfo {

    private String id;

    @ApiModelProperty(value = "活动ID")
    private String activityId;

    @ApiModelProperty(value = "奖项名称")
    private String award_name;

    @ApiModelProperty(value = "奖项名额")
    private int award_num;

    @ApiModelProperty(value = "优惠券iD")
    private String coupon_id;

    @ApiModelProperty(value = "虚拟人数")
    private Integer virtual_num;

    private String  award_id;
}
