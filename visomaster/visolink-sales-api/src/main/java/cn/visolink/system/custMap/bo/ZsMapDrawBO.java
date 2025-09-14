package cn.visolink.system.custMap.bo;

import cn.visolink.common.BaseBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ZsMapDrawBO extends BaseBO {

  @ApiModelProperty("主键")
  private String drawId;

  @ApiModelProperty("绘制类型;1-点、2-线、3-面")
  private Integer type;

  @ApiModelProperty("绘制名称")
  private String name;

  @ApiModelProperty("绘制名称")
  private String address;

  @ApiModelProperty("绘制长度")
  private BigDecimal length;

  @ApiModelProperty("绘制面积")
  private BigDecimal acreage;

  @ApiModelProperty("文件夹")
  private String folderId;

  @ApiModelProperty("文件夹名称")
  private String folder;

  @ApiModelProperty("坐标")
  private String latLon;

  @ApiModelProperty("颜色")
  private String color;

  @ApiModelProperty("照片地址")
  private String url;

  @ApiModelProperty("项目ID")
  private String projectId;

  @ApiModelProperty("项目名称")
  private String projectName;

  @ApiModelProperty("绘制人角色（1：项目招商专员 2：区域招商专员 3：万企通 4：中介 5：项目招商经理 6：项目营销经理 7：项目招商总监）")
  private Integer reportUserRole;

  @ApiModelProperty("创建人岗位ID")
  private String createJobId;

  @ApiModelProperty("创建人团队ID")
  private String createTeamID;

  @ApiModelProperty("创建人团队名称")
  private String createTeamName;

}
