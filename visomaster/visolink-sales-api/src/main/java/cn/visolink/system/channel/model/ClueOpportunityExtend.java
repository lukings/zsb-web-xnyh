package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/6/10
 */
@Data
public class ClueOpportunityExtend implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "线索id")
    private String projectClueId;

    @ApiModelProperty(value = "报备来源")
    private Integer reportSource;

    @ApiModelProperty(value = "报备来源描述")
    private String reportSourceDesc;

    @ApiModelProperty(value = "报备来源分类")
    private String childReportSource;

    @ApiModelProperty(value = "报备来源分类信息")
    private String childReportSourceDesc;

    @ApiModelProperty(value = "报备推荐信息")
    private String reportRefcommend;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "报名id")
    private String signUpId;

    @ApiModelProperty(value = "报名id")
    private String interceptionParam;

    @ApiModelProperty(value = "报名id")
    private String interceptionTime;

    @ApiModelProperty(value = "首访置业顾问ID")
    private String firstVisitSalesId;

    @ApiModelProperty(value = "首访置业顾问名称")
    private String firstVisitSalesName;

    @ApiModelProperty(value = "首访团队ID")
    private String firstVisitTeamId;

    @ApiModelProperty(value = "首访团队名称")
    private String firstVisitTeamName;

    @ApiModelProperty(value = "上次置业顾问ID")
    private String oldSalesId;

    @ApiModelProperty(value = "上次置业顾问名称")
    private String oldSalesName;

    @ApiModelProperty(value = "上次团队ID")
    private String oldTeamId;

    @ApiModelProperty(value = "上次团队名称")
    private String oldTeamName;



}
