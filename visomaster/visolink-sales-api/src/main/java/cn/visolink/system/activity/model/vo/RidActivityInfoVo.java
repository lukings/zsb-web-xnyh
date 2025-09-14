package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "乘车活动类")
public class RidActivityInfoVo implements Serializable {

    @ApiModelProperty(value = "序号")
    private String rowNo;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "乘车活动编号")
    private String ridActivityNo;

    @ApiModelProperty("乘车活动名称")
    private String ridActivityName;

    @ApiModelProperty("活动类型")
    private String activityType;

    @ApiModelProperty(value = "活动规则说明")
    private String activityNote;

    @ApiModelProperty(value = "活动开始时间")
    private String activityBegintime;

    @ApiModelProperty(value = "活动结束时间")
    private String activityEndtime;

    @ApiModelProperty(value = "活动禁用时间")
    private String disabletime;

    @ApiModelProperty(value = "活动创建时间")
    private String createtime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "是否开启预约 1 开启 0 不开启")
    private String isSubscribe;

//    @ApiModelProperty(value = "参与活动的用户身份")
//    private String ridAllowCustomerStatus;

    @ApiModelProperty(value = "接待人员配置")
    private String brokerConfig;

    @ApiModelProperty(value = "城市id")
    private String cityId;

    @ApiModelProperty(value = "城市名称")
    private String cityName;

    @ApiModelProperty(value = "乘车活动项目id")
    private String projectId;

    @ApiModelProperty(value = "乘车活动项目名称")
    private String projectName;

    @ApiModelProperty(value = "楼盘id")
    private String bookId;

    @ApiModelProperty(value = "楼盘名称")
    private String bookName;

    @ApiModelProperty(value = "楼盘地址")
    private String activityAddress;

    @ApiModelProperty(value = "活动状态 1 已启用 2 已禁用 3 已开始 4 已结束")
    private String status;

    @ApiModelProperty(value = "活动的点击次数")
    private String clickCount;

    public String[] getRidActTitle = new String[]{
            "序号","活动编号","活动名称","项目名称","楼盘名称","城市",
            "活动开始时间","活动结束时间","活动创建时间","创建人","禁用时间",
            "活动状态","是否启用预约"};


    public Object[] toRidActData() {
        return new Object[]{
                getRowNo(),getRidActivityNo(),getRidActivityName(),getProjectName(),getBookName(),getCityName(),
                getActivityBegintime(),getActivityEndtime(),getCreatetime(),getCreator(),getDisabletime(),getStatus(),getIsSubscribe()
        };
    }
}
