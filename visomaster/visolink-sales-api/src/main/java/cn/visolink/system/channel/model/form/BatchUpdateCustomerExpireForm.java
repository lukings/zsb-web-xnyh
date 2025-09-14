package cn.visolink.system.channel.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 批量调整客户过保及预警时间请求表单
 */
@Data
@ApiModel(value = "批量调整客户过保及预警时间请求", description = "批量调整客户过保及预警时间请求参数")
public class BatchUpdateCustomerExpireForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户机会列表", required = true)
    private List<CustomerExpireInfo> opportunityList;

    @ApiModelProperty(value = "用户ID", required = true)
    private String userId;

    @ApiModelProperty(value = "调整原因", required = true)
    private String reason;

    @ApiModelProperty(value = "附件列表")
    private List<Map<String, String>> fileList;

    /**
     * 客户过保信息
     */
    @Data
    @ApiModel(value = "客户过保信息", description = "客户过保信息")
    public static class CustomerExpireInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "机会ID", required = true)
        private String id;

        @ApiModelProperty(value = "过保时间", required = true)
        private String salesFollowExpireDate;

        @ApiModelProperty(value = "预警时间", required = true)
        private String salesFollowExpireWarningDate;
    }
}
