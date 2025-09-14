package cn.visolink.system.seniorbroker.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * customerActive对象
 * </p>
 *
 * @author autoJob
 * @since 2020-10-20
 */
@Data
@ApiModel(value="customerActive对象", description="活动报备台账")
public class CustomerActiveExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String customerName;

    private String customerMobile;

    private String customerMobileAll;

    private String reportCreateTime;

    private String clueStatus;

    private String brokerName;

    private String brokerMobile;

    private String brokerMobileAll;

    private String theFirstVisitDate;

    private String subscribingDate;

    private String contractDate;

    private String expireDate;


    /**
     * 根据条件，或者全号，或引号数据
     * @param isAllPhone 是否获取全号
     * @return
     */

    private String getCustomerMobile(boolean isAllPhone) {
        String customerMobile = getCustomerMobile();
        if (isAllPhone) {
            customerMobile = getCustomerMobileAll();
        }
        return customerMobile;
    }

    private String getBrokerMobile(boolean isAllPhone) {
        String brokerMobile = getBrokerMobile();
        if (isAllPhone) {
            brokerMobile = getBrokerMobileAll();
        }
        return brokerMobile;
    }
    /**
     * 获取公共池的数据
     * @param isAllPhone
     * @return
     */
    public Object[] toPublicData(boolean isAllPhone){
        String customerMobile = getCustomerMobile(isAllPhone);
        String brokerMobile = getBrokerMobile(isAllPhone);
        return new Object[]{
                getCustomerName(),customerMobile,
                getReportCreateTime(),getClueStatus(),
                getBrokerName(),brokerMobile,
                getTheFirstVisitDate(),
                getSubscribingDate(),
                getContractDate(),
                getExpireDate()
        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "客户姓名","客户手机号",
            "报备时间","状态",
            "经纪人姓名","经纪人手机号",
            "到访时间","认购时间","签约时间","失效时间"};
}
