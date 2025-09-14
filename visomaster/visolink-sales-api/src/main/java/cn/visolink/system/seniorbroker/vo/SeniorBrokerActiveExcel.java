package cn.visolink.system.seniorbroker.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * seniorBrokerActive对象
 * </p>
 *
 * @author autoJob
 * @since 2020-10-20
 */
@Data
@ApiModel(value="seniorBrokerActive对象", description="二级经纪人台账")
public class SeniorBrokerActiveExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ProjectId;

    private String KindeeProjectID;

    private String AreaName;

    private String ProjectName;

    private String AccountName;

    private String AccountId;

    private String AccountMobileAll;

    private String AccountMobile;

    private String BrokerId;

    private String BrokerName;

    private String BrokerMobile;

    private String BrokerMobileAll;

    private String CreateTime;

    private String ActiveGive;

    private int report = 0;

    private int arrive = 0;

    private int cntrtCnt = 0;

    private int orderCnt = 0;
    /**
     * 根据条件，或者全号，或引号数据
     * @param isAllPhone 是否获取全号
     * @return
     */
    private String getAccountMobile(boolean isAllPhone) {
        String accountMobile = getAccountMobile();
        if (isAllPhone) {
            accountMobile = getAccountMobileAll();
        }
        return accountMobile;
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
        String accountMobile = getAccountMobile(isAllPhone);
        String brokerMobile = getBrokerMobile(isAllPhone);
        return new Object[]{
                getAreaName(),getProjectName(),getAccountName(),accountMobile,
                getBrokerName(),brokerMobile,getCreateTime(),getActiveGive(),
                getReport(),getArrive(),
                getCntrtCnt(),
                getOrderCnt(),
        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "区域","项目","大客户经理姓名","大客户经理电话",
            "二级经纪人姓名","二级经纪人手机号","加入时间","参与过的任务",
            "报备","到访","认购","签约",};
}
