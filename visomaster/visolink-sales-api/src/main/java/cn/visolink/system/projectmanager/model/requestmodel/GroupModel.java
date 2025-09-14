package cn.visolink.system.projectmanager.model.requestmodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Yu
 * @String: 2021/11/9 23:12
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class GroupModel extends BaseModel implements Serializable {

    @ApiModelProperty(name = "groupId", value = "组团id")
    private String groupId;

    @ApiModelProperty(name = "groupCode", value = "组团编码")
    private String groupCode;

    @ApiModelProperty(name = "groupName", value = "组团名称")
    private String groupName;

    @ApiModelProperty(name = "kingdeeProjectId", value = "金蝶项目id")
    private String kingdeeProjectId;

    @ApiModelProperty(name = "projectId", value = "项目id")
    private String projectId;

    @ApiModelProperty(name = "projectCode", value = "项目编码")
    private String projectCode;

    @ApiModelProperty(name = "projectName", value = "项目名称")
    private String projectName;

    @ApiModelProperty(name = "kingdeeStageId", value = "金蝶分期id")
    private String kingdeeStageId;

    @ApiModelProperty(name = "stageId", value = "分期id")
    private String stageId;

    @ApiModelProperty(name = "stageCode", value = "分期编码")
    private String stageCode;

    @ApiModelProperty(name = "stageName", value = "分期名称")
    private String stageName;

    @ApiModelProperty(name = "totalBuildArea", value = "总建筑面积")
    private Float totalBuildArea;

    @ApiModelProperty(name = "businessType", value = "经营方式")
    private String businessType;

    @ApiModelProperty(name = "freeType", value = "装修方式")
    private String freeType;

    @ApiModelProperty(name = "versionStage", value = "版本阶段")
    private String versionStage;

    @ApiModelProperty(name = "totalSaleArea", value = "总可售面积")
    private Float totalSaleArea;

    @ApiModelProperty(name = "roomNum", value = "户数（总套数）")
    private Integer roomNum;

    @ApiModelProperty(name = "approvalTime", value = "审批日期")
    private String approvalTime;

    @ApiModelProperty(name = "startTime", value = "开始时间")
    private String startTime;

    @ApiModelProperty(name = "endTime", value = "结束时间")
    private String endTime;

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

    private List<DesignBuildModel> designBuildModelList;

}

