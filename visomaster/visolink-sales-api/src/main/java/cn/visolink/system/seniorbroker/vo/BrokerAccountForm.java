package cn.visolink.system.seniorbroker.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/10/20
 */
@Data
public class BrokerAccountForm extends BrokerAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> brokerActIdList;

    @ApiModelProperty(value = "二级经纪人Openid")
    private String brokerOpenId;

    @ApiModelProperty(value = "二级经纪人姓名")
    private String brokerName;

    @ApiModelProperty(value = "二级经纪人手机号")
    private String brokerMobile;

    @ApiModelProperty(value = "大客户经理姓名")
    private String accountName;

    @ApiModelProperty(value = "大客户经理电话")
    private String accountMobile;

    private String userId;

    private String userName;

    private String userMobile;

    private String employeeName;

    private List<String> accountIdList;

    private String creator;

    private String remarks;

    private String reason;

    private String entrance;

}
