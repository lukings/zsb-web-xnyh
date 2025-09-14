package cn.visolink.system.projectmanager.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: wang gang
 * @Date: 2019/9/9 15:28
 */
@Data
@ApiModel(value="房间信息")
public class RoomVo {
    @ApiModelProperty(value = "建筑单价")
    private String BldPrice;
    @ApiModelProperty(value = "套内单价")
    private String TnPrice;
    @ApiModelProperty(value = "楼层名称")
    private String FloorName;
    @ApiModelProperty(value = "楼层编号")
    private int FloorNo;
    @ApiModelProperty(value = "建筑面积")
    private String BldArea;
    @ApiModelProperty(value = "房间全称")
    private String RoomInfo;
    @ApiModelProperty(value = "房间简称")
    private String Room;
    @ApiModelProperty(value = "房间编号")
    private int No;
    @ApiModelProperty(value = "房间ID")
    private String RoomGUID;
    @ApiModelProperty(value = "套内面积")
    private String TnArea;
    @ApiModelProperty(value = "房间总价")
    private String Total;
    @ApiModelProperty(value = "房屋结构")
    private String RoomStru;
    @ApiModelProperty(value = "房间状态枚举")
    private String StatusEnum;
    @ApiModelProperty(value = "户型名称")
    private String HxName;
    @ApiModelProperty(value = "房间状态")
    private String Status;
    @ApiModelProperty(value = "预售证号")
    private String XxCode;
    @ApiModelProperty(value = "置业顾问")
    private String zygw;
    @ApiModelProperty(value = "客户姓名")
    private String CustomerName;
    @ApiModelProperty(value = "客户电话")
    private String CustomerTel;
    @ApiModelProperty(value = "成交总价")
    private String HtTotal;
    @ApiModelProperty(value = "建筑成交单价")
    private String CjBldPrice;
    @ApiModelProperty(value = "套内成交单价")
    private String CjTnPrice;
    @ApiModelProperty(value = "签署日期")
    private String ContractDate;
    @ApiModelProperty(value = "项目分期id")
    private String ProjectIdFq;
    @ApiModelProperty(value = "户型Id")
    private String HxId;
    @ApiModelProperty(value = "客户线索ID")
    private String ProjectClueId;
    @ApiModelProperty(value = "客户机会ID")
    private String OpportunityClueId;
    @ApiModelProperty(value = "交易id")
    private String TradeGUID;
    @ApiModelProperty(value = "交易备注")
    private String remarks;
    @ApiModelProperty(value = "计价方式")
    private String calMode;
    @ApiModelProperty(value = "面积状态")
    private String areaStatus;
}
