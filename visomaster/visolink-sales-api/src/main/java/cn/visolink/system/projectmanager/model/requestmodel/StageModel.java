package cn.visolink.system.projectmanager.model.requestmodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Mr.Yu
 * @String: 2021/11/9 22:56
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class StageModel extends BaseModel implements Serializable {

    @ApiModelProperty(name = "stageId", value = "分期id")
    private String stageId;

    @ApiModelProperty(name = "stageCode", value = "分期编号")
    private String stageCode;

    @ApiModelProperty(name = "stageName", value = "分期名称")
    private String stageName;

    @ApiModelProperty(name = "stageRecordName", value = "分期备案名称")
    private String stageRecordName;

    @ApiModelProperty(name = "projectId", value = "项目id")
    private String projectId;

    @ApiModelProperty(name = "startTime", value = "开始时间")
    private String startTime;

    @ApiModelProperty(name = "endTime", value = "结束时间")
    private String endTime;

    @ApiModelProperty(name = "totallandArea", value = "总用地面积")
    private BigDecimal totallandArea;

    @ApiModelProperty(name = "buildlandArea", value = "建设用地面积")
    private BigDecimal buildlandArea;

    @ApiModelProperty(name = "totalbuildArea", value = "总建设面积")
    private BigDecimal totalbuildArea;

    @ApiModelProperty(name = "onBuildArea", value = "地上建筑面积")
    private BigDecimal onBuildArea;

    @ApiModelProperty(name = "upBuildArea", value = "地下建筑面积")
    private BigDecimal upBuildArea;

    @ApiModelProperty(name = "buildVolumeArea", value = "计容建筑面积")
    private BigDecimal buildVolumeArea;

    @ApiModelProperty(name = "buildSaleArea", value = "建筑可售面积")
    private BigDecimal buildSaleArea;

    @ApiModelProperty(name = "plotRatio", value = "容积率")
    private BigDecimal plotRatio;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

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

    @ApiModelProperty(name = "orderCode", value = "序号 排序使用")
    private Integer orderCode;

    private List<DesignBuildModel> designBuildModelList;

}

