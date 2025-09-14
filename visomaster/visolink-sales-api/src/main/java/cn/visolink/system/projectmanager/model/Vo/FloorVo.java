package cn.visolink.system.projectmanager.model.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FloorVo
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/11/30 10:51
 **/
@Data
@ApiModel(value="楼层信息")
public class FloorVo {

    @ApiModelProperty(value = "楼层名称")
    private String floorName;

    @ApiModelProperty(value = "楼层编号")
    private String floorNo;

    @ApiModelProperty(value = "房间")
    private List<RoomListVo> roomList;

}
