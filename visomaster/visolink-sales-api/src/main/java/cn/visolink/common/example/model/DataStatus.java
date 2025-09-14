package cn.visolink.common.example.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author YHX
 * @date 2021年11月23日 16:18
 */
@Data
@ApiModel(value = "数据状态", description = "数据状态")
public class DataStatus {

    @ApiModelProperty(value = "数据id")
    private String dataId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @ApiModelProperty(value = "表名")
    private String tableName;

    @ApiModelProperty(value = "用户名称")
    private String userId;
}
