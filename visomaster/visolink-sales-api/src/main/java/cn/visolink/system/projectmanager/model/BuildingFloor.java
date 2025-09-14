package cn.visolink.system.projectmanager.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/18 21:15
 * @description
 * @Version 1.0
 */
@ToString
@Data
@Accessors(chain = true)
public class BuildingFloor implements Serializable {

    @ApiModelProperty(name = "floorId", value = "楼层id")
    private String floorId;

    @ApiModelProperty(name = "buildId", value = "楼栋id")
    private String buildId;

    @ApiModelProperty(name = "floorName", value = "楼层名称")
    private String floorName;

    @ApiModelProperty(name = "floorNo", value = "楼层序号")
    private Integer floorNo;

    @ApiModelProperty(name = "creator", value = "创建人")
    private String creator;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private String createTime;

    @ApiModelProperty(name = "editorTime", value = "修改时间")
    private String editorTime;

    @ApiModelProperty(name = "editor", value = "修改人")
    private String editor;

    @ApiModelProperty(name = "isDelete", value = "是否删除 0：正常；1：删除")
    private Integer isDelete;

    @ApiModelProperty(name = "status", value = "状态 0:禁用；1：启用")
    private Integer status;

}

