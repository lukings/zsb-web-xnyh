package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客户判重检查请求对象
 */
@Data
@ApiModel(value = "客户判重检查请求", description = "客户判重检查请求参数")
public class CustomerDuplicateCheckRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目ID", required = true)
    private String projectId;

    @ApiModelProperty(value = "客户列表", required = true)
    private List<CustomerInfo> customerList;

    /**
     * 客户信息
     */
    @Data
    @ApiModel(value = "客户信息", description = "客户基本信息")
    public static class CustomerInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "客户ID")
        private String customerId;

        @ApiModelProperty(value = "客户名称", required = true)
        private String customerName;

        @ApiModelProperty(value = "客户手机号", required = true)
        private String customerMobile;

        @ApiModelProperty(value = "客户名称隐号")
        private String customerNameIns;

        @ApiModelProperty(value = "客户手机号隐号")
        private String customerMobileIns;

        @ApiModelProperty(value = "标志类型")
        private String flagType;
    }
}
