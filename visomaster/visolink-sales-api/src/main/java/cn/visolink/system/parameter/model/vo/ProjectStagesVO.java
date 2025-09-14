package cn.visolink.system.parameter.model.vo;/**
 * @className: ProjectStagesVO
 * @description: TODO
 * @author: yhx
 * @date: 2021/1/11 15:04
 * @version: 1.0
 **/

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2021/1/11 15:04
 */
@Data
@ApiModel(value = "项目分期",description = "根据项目ID取分期")
public class ProjectStagesVO {

    @ApiModelProperty("项目ID")
    private String projectId;

    @ApiModelProperty("项目名称")
    private String projectName;
}
