package cn.visolink.common.redis.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 删除用户Redis键请求
 */
@Data
@ApiModel(value = "删除用户Redis键请求", description = "删除用户Redis键请求参数")
public class DeleteUserRedisKeyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户账户列表", required = true)
    private List<String> userAccounts;

    @ApiModelProperty(value = "是否删除APP端键", required = true)
    private Boolean deleteAppKeys = true;

    @ApiModelProperty(value = "是否删除PC端键", required = true)
    private Boolean deletePcKeys = true;
}
