package cn.visolink.system.activity.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ActivityMaterial
 * @Author wanggang
 * @Description //活动素材
 * @Date 2020/5/26 11:20
 **/
@Data
@ApiModel(value="ActivityMaterial对象", description="活动素材")
public class ActivityMaterial {

    private String id;

    @ApiModelProperty(value = "活动ID")
    private String activityId;

    @ApiModelProperty(value = "1 活动图文说明 2 活动分享海报 3活动中心封面 4项目活动展示封面 5分享小程序封面")
    private String materialType;

    @ApiModelProperty(value = "活动素材路径")
    private String materialAddress;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "是否首页推荐")
    private String isHomePageHot;

    @ApiModelProperty(value = "首页热推图片")
    private String hotImageUrl;

    @ApiModelProperty(value = "热推开始时间")
    private String hotStartTime;

    @ApiModelProperty(value = "热推结束时间")
    private String hotEndTime;

    @ApiModelProperty(value = "热推城市id")
    private String hotCityId;

    @ApiModelProperty(value = "热推城市名称")
    private String hotCityName;

}
