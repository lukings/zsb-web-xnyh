package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liming
 * <p>
 * created at 2021/3/1 15:20
 */
@Data
@ApiModel(value="助力优惠券", description="助力优惠券")
public class HelpCoupon {

    @ApiModelProperty(value = "优惠券ID")
    private String couponId;

    @ApiModelProperty(value = "层级")
    private Integer level;

    @ApiModelProperty(value = "邀请人数目标")
    private Integer targetNumber;

    @ApiModelProperty(value = "助力礼品url")
    private String giftImageUrl;

    private String activityId;

    @ApiModelProperty(value = "优惠券编号(展示)")
    private String couponNo;

    @ApiModelProperty(value = "优惠券名称(展示)")
    private String couponName;

    @ApiModelProperty(value = "优惠券面额(展示)")
    private String couponValue;

    @ApiModelProperty(value = "有效时间(展示)")
    private String validityTime;

    @ApiModelProperty(value = "总库存(展示)")
    private Integer stockNo;

    @ApiModelProperty(value = "剩余库存(展示)")
    private Integer stockSurplus;

    @ApiModelProperty(value = "优惠券类型(展示)")
    private String couponType;

    @ApiModelProperty(value = "领取时间(展示)")
    private String time;

    @ApiModelProperty(value = "关联的项目(展示)")
    private String projectName;

}
