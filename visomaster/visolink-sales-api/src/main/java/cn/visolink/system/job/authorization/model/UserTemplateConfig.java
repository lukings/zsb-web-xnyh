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
public class UserTemplateConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "岗位code")
    private String jobCode;

    @ApiModelProperty(value = "模板id")
    private String templateId;

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

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "排序")
    private Integer listIndex;

    @ApiModelProperty(value = "级别")
    private Integer level;

    @ApiModelProperty(value = "父级id")
    private String pid;

    private String isDel;

    private Integer messageNum;

    private String newMessageContent;

    private String newMessageTime;

    private String isRead;

    List<UserTemplateConfig> childDataList;
}
