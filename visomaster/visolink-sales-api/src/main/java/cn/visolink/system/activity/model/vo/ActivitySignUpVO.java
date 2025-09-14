package cn.visolink.system.activity.model.vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/6/29
 */
@Data
public class ActivitySignUpVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rownum;

    private String id;

    private String projectClueId;

    private String customerName;

    private String customerGender;

    private String customerMobile;

    private String customerMobileAll;

    private String sourceType;

    private String sourceTypeDesc;

    private String mainMediaGUID;

    private String mainMediaName;

    private String subMediaGUID;

    private String subMediaName;

    private String sourceTypeOld;

    private String sourceTypeOldDesc;

    private String clueStatus;

    private String projectId;

    private String projectName;

    private String reportSource;

    private String reportSourceDesc;

    private String activityId;

    private String signUpId;

    private String activityName;

    private String activityNo;

    private String signUpTime;

    private String actBeginTime;

    private String actEndTime;

    private String signUpOpenId;

    private String signUpName;

    private String signUpMobile;

    private String buildBookName;

    private String buildBookIds;

    private String signUpProjectId;

    private String signUpProjectName;

    private String signInTimeOnline;

    private String signInTime;

    private String signInTimeAuto;

    private String signInTimeAutoId;

    private String signInTimeAutoName;

    private String signinProjectName;

    private String signInType;

    private String signinTypeDesc;

    private String relationProject;

    private String actStatus;
    private String signup_diy_code;

    public String getRownum(){
        return StringUtils.isBlank(rownum) ? "" : rownum.replace(".0","");
    }
    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toActivityData(String isAll){
        String customerMobile = "";
        if ("1".equals(isAll)){
            customerMobile = getCustomerMobileAll();
        }else{
            customerMobile = getCustomerMobile();
        }
        return new Object[]{
                getRownum(),getCustomerName(),customerMobile,getSignUpProjectName(),
                getSigninProjectName(),getSignUpTime(),getSignInTime(),
                getSigninTypeDesc(),getSignInTimeAuto(),getSignInTimeAutoName(),
                getActivityNo(),getActivityName(),getRelationProject(),getActStatus()
        };
    }

    public String[]  activityTitle =  new String[]{
            "序号","客户姓名","客户手机号","报名项目","签到项目",
            "报名时间","扫码签到时间","签到方式","确访签到时间",
            "确访操作人","活动编号","活动名称","关联项目","活动状态"};

}
