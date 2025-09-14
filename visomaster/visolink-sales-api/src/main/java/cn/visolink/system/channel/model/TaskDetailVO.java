package cn.visolink.system.channel.model;

import cn.visolink.common.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class TaskDetailVO extends PageQuery {

    @ApiModelProperty("区域")
    private String areaName;

    @ApiModelProperty("项目")
    private String projectName;

    @ApiModelProperty("团队")
    private String teamName;

    @ApiModelProperty("姓名")
    private String userName;

    @ApiModelProperty("账号")
    private String accountName;

    @ApiModelProperty("报备指标量")
    private Integer reportNum;

    @ApiModelProperty("报备完成量")
    private Integer reportCompleteNum;

    @ApiModelProperty("拜访指标量")
    private Integer visitNum;

    @ApiModelProperty("拜访完成量")
    private Integer visitCompleteNum;

    @ApiModelProperty("到访指标量")
    private Integer arriveNum;

    @ApiModelProperty("到访完成量")
    private Integer arriveCompleteNum;

    @ApiModelProperty("三个一指标量")
    private Integer threeOneNum;

    @ApiModelProperty("三个一完成量")
    private Integer threeOneCompleteNum;
    
    @ApiModelProperty("成交指标量")
    private Integer dealNum;

    @ApiModelProperty("成交完成量")
    private Integer dealCompleteNum;
    
    @ApiModelProperty("首访指标量")
    private Integer firstVisitNum;

    @ApiModelProperty("首访指标完成量")
    private Integer firstVisitCompleteNum;
    
    @ApiModelProperty("复方指标量")
    private Integer repeatVisitNum;

    @ApiModelProperty("复访指标完成量")
    private Integer repeatVisitCompleteNum;
    
    
}
