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
public class CustomerCallRecordVoCount implements Serializable {
    private static final long serialVersionUID = 1L;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String projectName;

    private String channel;

    private String originalPhone;
    private String phone;
    private String phoneCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date callTime;

    private String callDate;



    public Object[] toActivityHelpData(String isAll){
        if ("1".equals(isAll)){
            phone = getPhone();
        }else{
            if(phone!=null&&phone.length()>=7) {
                phone = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
            }
        }
        if(callTime!=null){
            callDate = sf.format(callTime);
        }


        return new Object[]{
                getProjectName(), getChannel(),getOriginalPhone(),getPhone(),getPhoneCount(),getCallDate(),
        };
        //项目名称、客户号码、客户姓名、是否电商、通话方式、是否接通、通话时间、通话时长、通话类别、置业顾问、电商名称、认知通道、认知渠道、意向等级、无效原因、备注
    }
    public String[]  activityHelpTitle =  new String[]{
            "项目名称","电商名称",
            "电商号码","真实号码","来电次数",
            "通话时间"
    };

}
