package cn.visolink.system.project.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/11/8
 */
@Data
@ApiModel(value = "ProjectVO对象", description = "项目")
public class ProjectVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "组织id")
    private String orgId;

    @ApiModelProperty(value ="用户账号")
    private String userName;

    @ApiModelProperty(value ="区域名称")
    private String areaName;

    @ApiModelProperty(value ="区域id")
    private String areaId;

    @ApiModelProperty(value ="区域id")
    private String comguid;


    @ApiModelProperty(value ="媒体子类")
    private List<ProjectVO> projectList;

    @ApiModelProperty(value ="联动类型（0未联动1与此项目同一批次的联动2不同批次但已联动项目）")
    private int type;

    @ApiModelProperty(value ="项目招商类型编码")
    private String investmentTypeCode;
}
