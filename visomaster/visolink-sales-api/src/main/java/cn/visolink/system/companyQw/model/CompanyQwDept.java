package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName CompanyQwDept
 * @Author wanggang
 * @Description //企微部门
 * @Date 2022/1/17 15:26
 **/
@Data
@ApiModel(value = "企微部门", description = "企微部门")
public class CompanyQwDept implements Serializable {

    @ApiModelProperty(value = "部门id")
    private String id;

    @ApiModelProperty(value = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "父部门id")
    private String parentid;

    @ApiModelProperty(value = "父部门名称")
    private String parentDeptName;

    @ApiModelProperty(value = "组织ID")
    private String orgId;

    @ApiModelProperty(value = "组织名称")
    private String orgName;

    @ApiModelProperty(value = "组织全路径")
    private String fullpath;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "是否绑定（1：绑定 2：未绑定）")
    private String status;

}
