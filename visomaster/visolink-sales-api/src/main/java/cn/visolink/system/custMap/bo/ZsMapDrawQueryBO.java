package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ZsMapDrawQueryBO {

  @ApiModelProperty("主键")
  private String drawId;

  @ApiModelProperty("绘制类型;1-点、2-线、3-面")
  private Integer type;

  @ApiModelProperty("绘制名称")
  private String name;

  @ApiModelProperty("绘制人")
  private String createUser;

  @ApiModelProperty("绘制开始时间")
  private String startTime;

  @ApiModelProperty("绘制结束时间")
  private String endTime;

  @ApiModelProperty("本项目的绘制人")
  private List<String> createUserS;

  @ApiModelProperty("绘制类型;1-点、2-线、3-面4、行政区域")
  private List<String> types;

  @ApiModelProperty("绘制人姓名")
  private String createUserName;

  @ApiModelProperty("是否任务创建")
  private String isTaskCreate;

  @ApiModelProperty("项目ID")
  private List<String> projectIdS;
}
