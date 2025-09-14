package cn.visolink.system.projectmanager.model.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BuildingVo
 * @Author wanggang
 * @Description //楼栋
 * @Date 2021/11/30 10:50
 **/
@Data
@ApiModel(value="楼栋信息")
public class BuildingVo {

    @ApiModelProperty(value = "楼栋名称")
    private String bldName;
    @ApiModelProperty(value = "单元列表")
    private List<UnitVo> unitVoList;
    @ApiModelProperty(value = "楼层列表")
    private List<FloorVo> floorVoList;
    @ApiModelProperty(value = "销控数量")
    private String xkSum;
    @ApiModelProperty(value = "待售数量")
    private String dsSum;
    @ApiModelProperty(value = "认购数量")
    private String rgSum;
    @ApiModelProperty(value = "签约数量")
    private String qySum;
    @ApiModelProperty(value = "预销控数量")
    private String yxkSum;
}
