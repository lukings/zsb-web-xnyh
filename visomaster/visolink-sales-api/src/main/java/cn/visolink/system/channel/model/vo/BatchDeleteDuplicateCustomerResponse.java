package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除重复客户响应
 */
@Data
@ApiModel(value = "批量删除重复客户响应", description = "批量删除重复客户响应结果")
public class BatchDeleteDuplicateCustomerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "总删除数量")
    private Integer totalCount;

    @ApiModelProperty(value = "成功删除数量")
    private Integer successCount;

    @ApiModelProperty(value = "失败删除数量")
    private Integer failCount;

    @ApiModelProperty(value = "删除结果详情")
    private List<DeleteResult> deleteResults;

    /**
     * 删除结果详情
     */
    @Data
    @ApiModel(value = "删除结果详情", description = "单个客户删除结果")
    public static class DeleteResult implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "客户ID")
        private String customerId;

        @ApiModelProperty(value = "是否删除成功")
        private Boolean success;

        @ApiModelProperty(value = "删除消息")
        private String message;

        @ApiModelProperty(value = "删除的线索数量")
        private Integer deletedCluesCount;

        @ApiModelProperty(value = "删除的跟进记录数量")
        private Integer deletedFollowupCount;
    }
}
