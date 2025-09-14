package cn.visolink.system.projectmanager.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/18 15:25
 * @description
 * @Version 1.0
 */
@Data
@ToString
public class HXDto implements Serializable {

    @ApiModelProperty(name = "hxId", value = "户型Id")
    private String hxId;

    @ApiModelProperty(name = "hxName", value = "户型名称")
    private String hxName;

    @ApiModelProperty(name = "updateUser", value = "修改人账号")
    private String updateUser;
}

