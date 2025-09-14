package cn.visolink.system.openQuotation.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OpenActivityRoom
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/1/29 11:32
 **/
@Data
public class OpenActivityRoom implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "分期id")
    private String projectFid;

    @ApiModelProperty(value = "明源楼栋ID")
    private String buildId;

    @ApiModelProperty(value = "楼栋名称")
    private String buildName;

    @ApiModelProperty(value = "房间ID")
    private String roomId;

    @ApiModelProperty(value = "房间全称")
    private String roomInfo;

    @ApiModelProperty(value = "房间编号")
    private String roomNo;

    @ApiModelProperty(value = "单元")
    private String unitName;

    @ApiModelProperty(value = "单元编号")
    private String unitNo;

    @ApiModelProperty(value = "楼层")
    private String floor;

    @ApiModelProperty(value = "楼层号")
    private String floorNo;

    @ApiModelProperty(value = "房间状态")
    private String roomStatus;

    @ApiModelProperty(value = "房间类型")
    private String roomType;

    @ApiModelProperty(value = "房间分类（0：正常房间 1：已预收款 2：已集中选房）")
    private String roomClassification;

    @ApiModelProperty(value = "朝向")
    private String exposure;

    @ApiModelProperty(value = "户型图名称")
    private String hxPhotoName;

    @ApiModelProperty(value = "户型图路径")
    private String hxPhotoUrl;

    @ApiModelProperty(value = "户型")
    private String hxName;

    @ApiModelProperty(value = "户型Id")
    private String hxId;

    @ApiModelProperty(value = "建筑面积")
    private String bldArea;

    @ApiModelProperty(value = "套内面积")
    private String tnArea;

    @ApiModelProperty(value = "建筑单价")
    private String price;

    @ApiModelProperty(value = "套内单价")
    private String tnPrice;

    @ApiModelProperty(value = "总价")
    private String total;

    @ApiModelProperty(value = "是否删除")
    private String isDel;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "选房原因")
    private String chooseReason;

    @ApiModelProperty(value = "房间状态描述")
    private String roomStatusdesc;

}
