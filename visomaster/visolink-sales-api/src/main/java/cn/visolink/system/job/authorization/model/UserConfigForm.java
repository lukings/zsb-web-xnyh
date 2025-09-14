package cn.visolink.system.job.authorization.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/11/11
 */
@Data
public class UserConfigForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "岗位code")
    private String jobCode;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "模块id")
    private String templateId;

}
