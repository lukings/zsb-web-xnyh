package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName QuitUser
 * @Author wanggang
 * @Description //离职成员
 * @Date 2022/1/18 10:16
 **/
@Data
@ApiModel(value = "离职成员", description = "离职成员")
public class QuitUser {

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "成员id")
    private String userId;

    @ApiModelProperty(value = "成员名称")
    private String userName;

    @ApiModelProperty(value = "成员手机号")
    private String userMobile;

    @ApiModelProperty(value = "所有客户数")
    private Integer allCstCount = 0;

    @ApiModelProperty(value = "待分配客户数")
    private Integer unassignedCount = 0;

    @ApiModelProperty(value = "分配中客户数")
    private Integer distributionCount = 0;

    @ApiModelProperty(value = "继承成功客户数")
    private Integer inheritOk = 0;

    @ApiModelProperty(value = "继承失败客户数")
    private Integer inheritNotOk = 0;


}
