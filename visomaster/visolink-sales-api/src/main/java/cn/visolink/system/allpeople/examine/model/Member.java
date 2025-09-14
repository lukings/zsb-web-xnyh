package cn.visolink.system.allpeople.examine.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName Member
 * @Author wanggang
 * @Description //会员
 * @Date 2022/4/2 16:43
 **/
@ApiModel(value="会员", description="会员")
@Data
public class Member {

    @ApiModelProperty(value = "会员id")
    private String brokerId;
    @ApiModelProperty(value = "会员姓名")
    private String name;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "积分")
    private String integral;
    @ApiModelProperty(value = "账号等级")
    private String level;
    @ApiModelProperty(value = "注册时间")
    private String registTime;

}
