package cn.visolink.system.parameter.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/12/3 15:27
 */
@Data
@ApiModel(value = "自定义CoDe", description = "自定义字段")
public class ProjectDiyCode {

    @ApiModelProperty("项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目集合")
    private List<String> projectList;

    private Integer pageSize;

    private Integer pageNum;

}
