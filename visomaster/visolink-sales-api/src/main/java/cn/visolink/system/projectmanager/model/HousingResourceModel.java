package cn.visolink.system.projectmanager.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/18 10:18
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HousingResourceModel implements Serializable {

    @ApiModelProperty(name = "floors", value = "楼层数")
    private Integer floors;

    @ApiModelProperty(name = "unitNum", value = "单元数")
    private Integer unitNum;

    @ApiModelProperty(name = "bldType", value = "类型 普通住址 商业房间 车位 别墅")
    private String bldType;

    @ApiModelProperty(name = "buildId", value = "楼栋id")
    private String buildId;

    @ApiModelProperty(name = "projectId", value = "项目id")
    private String projectId;

    @ApiModelProperty(name = "productBuildName", value = "楼栋名称")
    private String productBuildName;

    @ApiModelProperty(name = "stageId", value = "分期id")
    private String stageId;

    @ApiModelProperty(name = "stageName", value = "分期名称")
    private String stageName;

    /**
     *   单元对象
     */
    private List<BuildUnit> buildUnitList;

    /**
     *   楼层对象
     */
    List<BuildingFloor> buildingFloorList;
}

