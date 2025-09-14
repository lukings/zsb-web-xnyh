package cn.visolink.system.openQuotation.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName OpenRoom
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/7/29 11:27
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="房间", description="房间")
public class OpenRoom implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "项目id")
    private String projectid;

    @ApiModelProperty(value = "项目名称")
    private String projectname;

    @ApiModelProperty(value = "分期id")
    private String projectidFq;

    @ApiModelProperty(value = "分期名称")
    private String projectnameFq;

    @ApiModelProperty(value = "明源楼栋ID")
    private String buildguid;

    @ApiModelProperty(value = "楼栋名称")
    private String buildname;

    @ApiModelProperty(value = "房间ID")
    private String roomguid;

    @ApiModelProperty(value = "房间名称")
    private String roomname;

    @ApiModelProperty(value = "房间全称")
    private String roominfo;

    @ApiModelProperty(value = "房间编号")
    private int roomno;

    @ApiModelProperty(value = "单元")
    private String unitno;

    @ApiModelProperty(value = "楼层")
    private String floor;

    @ApiModelProperty(value = "楼层号")
    private int floorno;

    @ApiModelProperty(value = "销售状态(1:待售 2:已售 3:销控)")
    private String saleStatus;

    @ApiModelProperty(value = "房间类型")
    private String roomType;

    @ApiModelProperty(value = "朝向")
    private String exposure;

    @ApiModelProperty(value = "户型图")
    private String exposureImg;

    @ApiModelProperty(value = "户型")
    private String houseType;

    @ApiModelProperty(value = "户型Id")
    private String houseTypeId;

    @ApiModelProperty(value = "建筑面积")
    private String floorArea;

    @ApiModelProperty(value = "套内面积")
    private String insideArea;

    @ApiModelProperty(value = "建筑单价")
    private String floorPrice;

    @ApiModelProperty(value = "套内单价")
    private String insidePrice;

    @ApiModelProperty(value = "总价")
    private String totalPrice;

    @ApiModelProperty(value = "排序")
    private String orderby;

    private String isChecked;//是否选择
}
