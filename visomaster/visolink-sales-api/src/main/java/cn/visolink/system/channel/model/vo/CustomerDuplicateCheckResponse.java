package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客户判重检查响应对象
 */
@Data
@ApiModel(value = "客户判重检查响应", description = "客户判重检查结果")
public class CustomerDuplicateCheckResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "总客户数量")
    private Integer totalCount;

    @ApiModelProperty(value = "重复客户数量")
    private Integer duplicateCount;

    @ApiModelProperty(value = "非重复客户数量")
    private Integer nonDuplicateCount;

    @ApiModelProperty(value = "客户判重结果列表")
    private List<CustomerDuplicateResult> customerResults;

    /**
     * 客户判重结果
     */
    @Data
    @ApiModel(value = "客户判重结果", description = "单个客户的判重结果")
    public static class CustomerDuplicateResult implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "客户ID")
        private String customerId;

        @ApiModelProperty(value = "客户名称")
        private String customerName;

        @ApiModelProperty(value = "客户手机号")
        private String customerMobile;

        @ApiModelProperty(value = "客户名称隐号")
        private String customerNameIns;

        @ApiModelProperty(value = "客户手机号隐号")
        private String customerMobileIns;

        @ApiModelProperty(value = "是否重复：0-不重复，1-重复")
        private Integer isDuplicate;

        @ApiModelProperty(value = "重复原因描述")
        private String duplicateReason;

        @ApiModelProperty(value = "重复的客户信息列表")
        private List<DuplicateCustomerInfo> duplicateCustomers;

        @ApiModelProperty(value = "标志类型")
        private String flagType;

        /**
         * 重复客户信息
         */
        @Data
        @ApiModel(value = "重复客户信息", description = "重复的客户详细信息")
        public static class DuplicateCustomerInfo implements Serializable {

            private static final long serialVersionUID = 1L;

            @ApiModelProperty(value = "客户ID")
            private String customerId;

            @ApiModelProperty(value = "客户名称")
            private String customerName;

            @ApiModelProperty(value = "客户手机号")
            private String customerMobile;

            @ApiModelProperty(value = "客户来源：1-万企通，2-转介，3-案场")
            private String sourceMode;

            @ApiModelProperty(value = "客户来源描述")
            private String sourceModeDesc;

            @ApiModelProperty(value = "项目ID")
            private String projectId;

            @ApiModelProperty(value = "项目名称")
            private String projectName;

            @ApiModelProperty(value = "创建时间")
            private String createTime;
        }
    }
}
