package cn.visolink.system.projectmanager.model.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName RoomListVo
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/11/30 10:52
 **/
@Data
@ApiModel(value="房间信息")
public class RoomListVo {

    @ApiModelProperty(value = "房间ID")
    private String roomGUID;

    @ApiModelProperty(value = "房间全称")
    private String roomInfo;

    @ApiModelProperty(value = "房间简称")
    private String room;

    @ApiModelProperty(value = "房间编号")
    private String roomNo;

    @ApiModelProperty(value = "房间序号")
    private String no;

    @ApiModelProperty(value = "楼层名称")
    private String floorName;

    @ApiModelProperty(value = "楼层编号")
    private String floorNo;

    @ApiModelProperty(value = "单元名称")
    private String unitName;

    @ApiModelProperty(value = "单元编号")
    private String unitNo;

    @ApiModelProperty(value = "房间状态")
    private String statusEnum;

}
