package cn.visolink.system.phone.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/12/22 14:23
 */
@Data
public class CustomerCallRecordVo  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 一小时的秒数
     */
    private static final int HOUR_SECOND = 60 * 60;

    /**
     * 一分钟的秒数
     */
    private static final int MINUTE_SECOND = 60;

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String projectName;

    private String phone;

    private String name;

    private String channel;

    private String isChannel;


    private String callStatus;

    private String duration;

    private String isDuration;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date callTime;

    private String callDate;

    private String callType;

    private String newCustomer;

    private String salerName;

    private String childChannel;

    private String parentChannel;

    private String intentionLevel;

    private String invalidReason;

    private String note;

    public Object[] toActivityHelpData(String isAll){
        if ("1".equals(isAll)){
            phone = getPhone();
        }else{
            if(phone!=null&&phone.length()>=7) {
                phone = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
            }
        }
        if(getChannel()!=null){
            isChannel="是";
        }else {
            isChannel="否";
        }
        if(callStatus!=null){
        if(callStatus.equals(0)||callStatus.equals(2)){
            callStatus="来电";
        } else{
            callStatus="去电";
        }}else{
            callStatus="来电";
        }
        if(newCustomer!=null){
        if(newCustomer.equals("true")){
            callType="新客";
        }else{
            callType="老客";
        }
        }else{
            callType="新客";
        }
        if(duration!=null&&!duration.equals("0")){
            isDuration="是";
        }else{
            isDuration="否";
        }
        if(callTime!=null){
            callDate = sf.format(callTime);
        }
        duration = getTimeStrBySecond(Integer.parseInt(duration));

        return new Object[]{
                getProjectName(),getPhone(),getName(),getIsChannel(),getCallStatus(),getIsDuration(),getCallDate(),getDuration(),getCallType(),getSalerName(),
                getChannel(),getChildChannel(),getParentChannel(),getIntentionLevel(),getInvalidReason(),getNote()
        };
        //项目名称、客户号码、客户姓名、是否电商、通话方式、是否接通、通话时间、通话时长、通话类别、置业顾问、电商名称、认知通道、认知渠道、意向等级、无效原因、备注
    }
    public String[]  activityHelpTitle =  new String[]{
            "项目名称","客户号码",
            "客户姓名","是否电商","通话方式","是否接通",
            "通话时间","通话时长","通话类别","置业顾问","电商名称","认知通道","认知渠道",
            "意向等级","无效原因","备注"
    };



    public static String getTimeStrBySecond(int second) {
        if (second <= 0) {

            return "00:00:00";
        }

        StringBuilder sb = new StringBuilder();
        int hours = second / HOUR_SECOND;
        if (hours > 0) {

            second -= hours * HOUR_SECOND;
        }

        int minutes = second / MINUTE_SECOND;
        if (minutes > 0) {

            second -= minutes * MINUTE_SECOND;
        }

        return (hours >= 10 ? (hours + "")
                : ("0" + hours) + ":" + (minutes >= 10 ? (minutes + "") : ("0" + minutes)) + ":"
                + (second >= 10 ? (second + "") : ("0" + second)));
    }

}
