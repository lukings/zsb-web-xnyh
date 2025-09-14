package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName QwUserVo
 * @Author wanggang
 * @Description //企微成员
 * @Date 2022/1/4 19:17
 **/
@Data
@ApiModel(value = "企微成员", description = "企微成员")
public class QwUserVo implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private String userid;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "部门列表")
    private List<String> department;

    @ApiModelProperty(value = "性别。1表示男性，2表示女性")
    private String gender;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "启用/禁用成员。1表示启用成员，0表示禁用成员")
    private String enable;

    @ApiModelProperty(value = "主部门")
    private String mainDepartment;

    @ApiModelProperty(value = "聚客宝账号ID")
    private String accountId;
}
