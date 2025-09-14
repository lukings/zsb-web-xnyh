package cn.visolink.common.redis.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 删除用户Redis键响应
 */
@Data
@ApiModel(value = "删除用户Redis键响应", description = "删除用户Redis键响应结果")
public class DeleteUserRedisKeyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否删除成功")
    private Boolean success;

    @ApiModelProperty(value = "删除消息")
    private String message;

    @ApiModelProperty(value = "删除的键列表")
    private List<String> deletedKeys;

    @ApiModelProperty(value = "删除的键数量")
    private Integer deletedCount;

    @ApiModelProperty(value = "用户账户列表")
    private List<String> userAccounts;

    @ApiModelProperty(value = "每个用户的删除结果详情")
    private Map<String, UserDeleteResult> userResults;

    @ApiModelProperty(value = "成功删除的用户数量")
    private Integer successUserCount;

    @ApiModelProperty(value = "失败删除的用户数量")
    private Integer failedUserCount;

    /**
     * 单个用户删除结果
     */
    @Data
    @ApiModel(value = "用户删除结果", description = "单个用户的删除结果详情")
    public static class UserDeleteResult implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "是否删除成功")
        private Boolean success;

        @ApiModelProperty(value = "删除消息")
        private String message;

        @ApiModelProperty(value = "删除的键列表")
        private List<String> deletedKeys;

        @ApiModelProperty(value = "删除的键数量")
        private Integer deletedCount;
    }
}
