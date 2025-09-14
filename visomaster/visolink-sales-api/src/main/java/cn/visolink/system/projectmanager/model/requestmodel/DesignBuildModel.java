package cn.visolink.system.projectmanager.model.requestmodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Mr.Yu
 * @String: 2021/11/9 23:13
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class DesignBuildModel extends BaseModel implements Serializable {

    @ApiModelProperty(name = "buildId", value = "楼栋id")
    private String buildId;

    @ApiModelProperty(name = "kingdeeBuildId", value = "楼栋id（金蝶）")
    private String kingdeeBuildId;

    @ApiModelProperty(name = "myBuildId", value = "楼栋id（明源）")
    private String myBuildId;

    @ApiModelProperty(name = "productBuildCode", value = "业态楼栋编码")
    private String productBuildCode;

    @ApiModelProperty(name = "productBuildName", value = "业态楼栋名称")
    private String productBuildName;

    @ApiModelProperty(name = "buildingCode", value = "物理楼栋编码")
    private String buildingCode;

    @ApiModelProperty(name = "buildingName", value = "物理楼栋名称")
    private String buildingName;

    @ApiModelProperty(name = "stageId", value = "项目分期id")
    private String stageId;

    @ApiModelProperty(name = "stageName", value = "分期名称")
    private String stageName;

    @ApiModelProperty(name = "stageCode", value = "分期编号")
    private String stageCode;

    @ApiModelProperty(name = "groupId", value = "组团id")
    private String groupId;

    @ApiModelProperty(name = "groupName", value = "组团名称")
    private String groupName;

    @ApiModelProperty(name = "productCode", value = "产品构成编码")
    private String productCode;

    @ApiModelProperty(name = "productName", value = "产品构成名称")
    private String productName;

    @ApiModelProperty(name = "freeTypeCode", value = "装修类型（例如： JZ, MP）")
    private String freeTypeCode;

    @ApiModelProperty(name = "freeTypeName", value = "装修类型名称（例如：精装、毛坯）")
    private String freeTypeName;

    @ApiModelProperty(name = "proTypeCode", value = "产品属性编码")
    private String proTypeCode;

    @ApiModelProperty(name = "proTypeName", value = "产品属性名称")
    private String proTypeName;

    @ApiModelProperty(name = "totalSaleArea", value = "总可售面积")
    private Float totalSaleArea;

    @ApiModelProperty(name = "totalBuildArea", value = "总建筑面积")
    private Float totalBuildArea;

    @ApiModelProperty(name = "upBuildArea", value = "地上建筑面积")
    private Float upBuildArea;

    @ApiModelProperty(name = "underBuildArea", value = "地下建筑面积")
    private Float underBuildArea;

    @ApiModelProperty(name = "upSaleArea", value = "地上可租售面积")
    private Float upSaleArea;

    @ApiModelProperty(name = "underSaleArea", value = "地下可租售面积")
    private Float underSaleArea;

    @ApiModelProperty(name = "totalComplimentaryArea", value = "总赠送面积")
    private Float totalComplimentaryArea;

    @ApiModelProperty(name = "upGiftArea", value = "地上赠送面积")
    private Float upGiftArea;

    @ApiModelProperty(name = "underGiftArea", value = "地下赠送面积")
    private Float underGiftArea;

    @ApiModelProperty(name = "totalLandArea", value = "总用地面积")
    private Float totalLandArea;

    @ApiModelProperty(name = "landQzArea", value = "土地证取证面积")
    private Float landQzArea;

    @ApiModelProperty(name = "landQzDate", value = "土地证取证时间")
    private String landQzDate;

    @ApiModelProperty(name = "planningQzDate", value = "规划证取证时间")
    private String planningQzDate;

    @ApiModelProperty(name = "planningQzArea", value = "规划证取证面积")
    private Float planningQzArea;

    @ApiModelProperty(name = "constructionQzDate", value = "施工证取证时间")
    private String constructionQzDate;

    @ApiModelProperty(name = "constructionQzArea", value = "施工证取证面积")
    private Float constructionQzArea;

    @ApiModelProperty(name = "presaleQzDate", value = "预售证取证时间")
    private String presaleQzDate;

    @ApiModelProperty(name = "presaleQzArea", value = "预售证取证面积")
    private Float presaleQzArea;

    @ApiModelProperty(name = "completioncerDate", value = "竣备证取证时间")
    private String completioncerDate;

    @ApiModelProperty(name = "completioncerArea", value = "竣备证取证面积")
    private Float completioncerArea;

    @ApiModelProperty(name = "floors", value = "层数")
    private Integer floors;

    @ApiModelProperty(name = "unitNum", value = "单元数")
    private Integer unitNum;

    @ApiModelProperty(name = "roomHigh", value = "屋面高度")
    private Float roomHigh;

    @ApiModelProperty(name = "floorHigh", value = "层高")
    private Float floorHigh;

    @ApiModelProperty(name = "liftNum", value = "电梯数")
    private Integer liftNum;

    @ApiModelProperty(name = "roomNum", value = "户数（总套数）")
    private Integer roomNum;

    @ApiModelProperty(name = "structureArea", value = "结构面积")
    private Float structureArea;

    @ApiModelProperty(name = "parkingRatio", value = "车位配比")
    private Float parkingRatio;

    @ApiModelProperty(name = "isRefined", value = "是否精装修0：否；1：是")
    private Integer isRefined;

    @ApiModelProperty(name = "isAssembling", value = "是否装配式0：否；1：是")
    private Integer isAssembling;

    @ApiModelProperty(name = "presaleImage", value = "预售形象描述")
    private String presaleImage;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private String createTime;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private String updateTime;

    @ApiModelProperty(name = "createUser", value = "创建人")
    private String createUser;

    @ApiModelProperty(name = "updateUser", value = "修改人")
    private String updateUser;

    @ApiModelProperty(name = "isDelete", value = "0: 正常 1： 删除")
    private Integer isDelete;

    @ApiModelProperty(name = "status", value = "状态 1：启用 0： 禁用")
    private Integer status;

    @ApiModelProperty(name = "projectId", value = "项目id")
    private String projectId;

    @ApiModelProperty(name = "projectName", value = "项目名称")
    private String projectName;

    @ApiModelProperty(name = "startTime", value = "开始时间")
    private String startTime;

    @ApiModelProperty(name = "endTime", value = "结束时间")
    private String endTime;

    @ApiModelProperty(name = "bldType", value = "业态类型")
    private String bldType;

    @ApiModelProperty(name = "ytTypeId", value = "业态类型id")
    private String ytTypeId;

    @ApiModelProperty(name = "orderCode", value = "楼栋排序")
    private Integer orderCode;

    @ApiModelProperty(name = "permitNum", value = "预售证编号")
    private String permitNum;

    @ApiModelProperty(name = "genHouseFlag", value = "房源生成标识 1: 生成房源")
    private Integer genHouseFlag;

    @ApiModelProperty(name = "deposit", value = "定金")
    private BigDecimal deposit;




}

