package cn.visolink.system.projectmanager.model.requestmodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/9 16:57
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class ProjectQueryRequest extends BaseModel {

    @ApiModelProperty(name = "orgId", value = "组织id")
    private String orgId;

    @ApiModelProperty(name = "projectName", value = "项目名称，查询条件")
    private String projectName;

    @ApiModelProperty(name = "areaName", value = "公司名称，查询条件")
    private String areaName;

    @ApiModelProperty(name = "projectId", value = "项目id")
    private String projectId;

    @ApiModelProperty(name = "fullPath", value = "全路径")
    private String fullPath;

}

