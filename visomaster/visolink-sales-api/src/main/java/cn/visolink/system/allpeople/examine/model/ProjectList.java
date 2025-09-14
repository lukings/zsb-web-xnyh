package cn.visolink.system.allpeople.examine.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ProjectList
 * @Author wanggang
 * @Description //项目列表
 * @Date 2022/4/6 16:56
 **/
@ApiModel(value="项目列表", description="项目列表")
@Data
public class ProjectList {

    @ApiModelProperty(value = "项目ID")
    private String projectId;
    @ApiModelProperty(value = "项目")
    private String projectName;
    @ApiModelProperty(value = "项目状态")
    private String projectStatus;
    @ApiModelProperty(value = "状态(1:启用 0：禁用)")
    private String status;
    @ApiModelProperty(value = "组织")
    private String orgName;
    @ApiModelProperty(value = "区域")
    private String areaName;
    @ApiModelProperty(value = "组织Id")
    private String orgID;
    @ApiModelProperty(value = "城市")
    private String cityName;
    @ApiModelProperty(value = "是否区域")
    private String isRegion;
    @ApiModelProperty(value = "联动项目id")
    private String transProjectId;
    @ApiModelProperty(value = "联动项目名称")
    private String transProjectName;
    @ApiModelProperty(value = "有效期开始时间")
    private String startTime;
    @ApiModelProperty(value = "有效期结束时间")
    private String endTime;
    @ApiModelProperty(value = "联动状态")
    private String transProstatus;
    @ApiModelProperty(value = "联动状态")
    private String transStatus;
    @ApiModelProperty(value = "项目招商类型编码")
    private String investmentTypeCode;
    @ApiModelProperty(value = "项目招商类型名称")
    private String investmentTypeName;

}
