package cn.visolink.system.pubilcPool.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2021/5/21
 */
@Data
@ApiModel(value = "记录批次对象", description = "记录批次对象")
public class PublicPoolBatchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "记录类型 (1 放弃 2 淘客)")
    private String recordType;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "操作时间")
    private String operationTime;

    @ApiModelProperty(value = "数量")
    private String countNumber;

}
