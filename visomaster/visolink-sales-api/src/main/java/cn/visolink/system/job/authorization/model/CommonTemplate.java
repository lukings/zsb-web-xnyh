package cn.visolink.system.job.authorization.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/11/11
 */
@Data
public class CommonTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "pid")
    private String pId;

    @ApiModelProperty(value = "岗位code")
    private String jobCode;

    @ApiModelProperty(value = "分组code")
    private String groupCode;

    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @ApiModelProperty(value = "分组value")
    private String groupValue;

    @ApiModelProperty(value = "分组描述")
    private String groupDesc;

    @ApiModelProperty(value = "类型：1 旭客汇看板  2旭客汇消息")
    private String type;

    @ApiModelProperty(value = "是否删除 0 否 1 是")
    private String isDel;

    @ApiModelProperty(value = "是否选中")
    private Boolean isChecked = false;

    @ApiModelProperty(value = "子集")
    private List<CommonTemplate> childDataList;

    @ApiModelProperty(value = "级别")
    private Integer level;

    @ApiModelProperty(value = "排序")
    private Integer listIndex;

    private Integer messageNum;

    private Long customerNum;

    private String userTemplateId;

}
