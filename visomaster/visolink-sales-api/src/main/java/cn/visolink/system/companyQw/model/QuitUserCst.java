package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName QuitUserCst
 * @Author wanggang
 * @Description //离职成员客户
 * @Date 2022/1/18 10:29
 **/
@Data
@ApiModel(value = "离职成员客户", description = "离职成员客户")
public class QuitUserCst {

    @ApiModelProperty(value = "原成员id")
    private String useridOld;

    @ApiModelProperty(value = "原成员名称")
    private String userNameOld;

    @ApiModelProperty(value = "客户ID")
    private String externalUserid;

    @ApiModelProperty(value = "客户姓名")
    private String cstName;

    @ApiModelProperty(value = "客户状态（1：待分配 2：等待继承 3：分配成功 4：客户拒绝 5：成员客户达上限）")
    private String status;

    @ApiModelProperty(value = "客户状态（1：待分配 2：等待继承 3：分配成功 4：客户拒绝 5：成员客户达上限）")
    private String statusDesc;

    @ApiModelProperty(value = "继承员工ID")
    private String useridNew;

    @ApiModelProperty(value = "继承员工姓名")
    private String userNameNew;

    @ApiModelProperty(value = "分配时间")
    private String distTime;

}
