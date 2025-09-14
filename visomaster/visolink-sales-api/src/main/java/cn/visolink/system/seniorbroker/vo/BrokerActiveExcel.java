package cn.visolink.system.seniorbroker.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * PublicpoolVO对象
 * </p>
 *
 * @author autoJob
 * @since 2020-10-20
 */
@Data
@ApiModel(value="brokerActive对象", description="经纪人参与情况导出")
public class BrokerActiveExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String brokerId;

    private String brokerName;

    private String mobile;

    private String mobileAll;

    private String createTime;

    private int report = 0;

    private int arrive = 0;

    private int cntrtCnt = 0;

    private int orderCnt = 0;
    /**
     * 根据条件，或者全号，或引号数据
     * @param isAllPhone 是否获取全号
     * @return
     */

    private String getCustomerMobile(boolean isAllPhone) {
        String phone = getMobile();
        if (isAllPhone) {
            phone = getMobileAll();
        }
        return phone;
    }
    /**
     * 获取公共池的数据
     * @param isAllPhone
     * @return
     */
    public Object[] toPublicData(boolean isAllPhone){
        String phone = getCustomerMobile(isAllPhone);
        return new Object[]{
                getBrokerName(),phone,getCreateTime(),
                getReport(),
                getArrive(),
                getCntrtCnt(),
                getOrderCnt()
        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "二级经纪人姓名","二级经纪人手机号","参与活动时间","报备","到访","认购","签约"};
}
