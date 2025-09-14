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
 * @author 孙林
 * @date:2019-9-10
 * */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BuildUnit implements Serializable {

    @ApiModelProperty(name = "unitId", value = "单元id")
    private String unitId;

    @ApiModelProperty(name = "unitName", value = "单元名称")
    private String unitName;

    @ApiModelProperty(name = "unitNo", value = "单元编号")
    private Integer unitNo;

    @ApiModelProperty(name = "buildId", value = "楼栋ID(计划系统楼栋ID)")
    private String buildId;

    @ApiModelProperty(name = "kingdeeBuildId", value = "楼栋ID（金蝶）")
    private String kingdeeBuildId;

    @ApiModelProperty(name = "myBuildId", value = "楼栋ID(明源)")
    private String myBuildId;

    @ApiModelProperty(name = "stageId", value = "项目分期ID")
    private String stageId;

    @ApiModelProperty(name = "stageCode", value = "分期编号")
    private String stageCode;

    @ApiModelProperty(name = "stageName", value = "分期名称")
    private String stageName;

    @ApiModelProperty(name = "groupId", value = "组团ID")
    private String groupId;

    @ApiModelProperty(name = "groupName", value = "组团名称")
    private String groupName;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private String createTime;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private String updateTime;

    @ApiModelProperty(name = "createUser", value = "创建人账号")
    private String createUser;

    @ApiModelProperty(name = "updateUser", value = "修改人账号")
    private String updateUser;

    @ApiModelProperty(name = "isDelete", value = "是否删除 0：正常；1：删除")
    private Integer isDelete;

    @ApiModelProperty(name = "status", value = "状态 0:禁用；1：启用")
    private Integer status;

    @ApiModelProperty(name = "projectId", value = "项目id")
    private String projectId;

    @ApiModelProperty(name = "projectName", value = "项目名称")
    private String projectName;

    @ApiModelProperty(name = "householdsNum", value = "户数")
    private String householdsNum;

    @ApiModelProperty(name = "totalHouseholdsNum", value = "总户数")
    private Integer totalHouseholdsNum;

    @ApiModelProperty(name = "floorsName", value = "楼层名称")
    private String floorsName;

    /**
     *   房间对象
     */
    private List<BuildRoom> buildRoomList;
}