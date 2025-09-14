package cn.visolink.system.seniorbroker.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * <p>
 * PublicpoolVO对象
 * </p>
 *
 * @author autoJob
 * @since 2020-10-20
 */
@Data
@ApiModel(value="accountActive对象", description="大客户活动")
public class AccountActiveExcel  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountId;

    private String brokerId;

    private String ProjectId;

    private String AreaName;

    private String ProjectName;

    private String ActiveId;

    private String ActiveName;

    private String Status;

    private String StatusName;

    private String ReleaseTime;

    private String BeginTime;

    private String EndTime;

    private String DisableTime;

    private String AccountName;

    private String Mobile;

    private String MobileAll;

    private String participants;

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
                getAreaName(),getProjectName(),getActiveName(),
                getStatusName(),getReleaseTime(),
                getBeginTime(),getEndTime(),getAccountName(),
                phone,getParticipants(),
                getReport(),getArrive(),
                getCntrtCnt(),
                getOrderCnt(),
                getDisableTime()
        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "区域","项目","活动名称","活动状态","活动发布时间",
            "活动开始时间","活动结束时间","大客户经理","大客户经理手机号",
            "参与人数","报备","到访","认购","签约","活动禁用时间"};
}
