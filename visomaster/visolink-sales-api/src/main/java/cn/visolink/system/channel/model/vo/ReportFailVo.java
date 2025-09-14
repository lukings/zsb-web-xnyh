package cn.visolink.system.channel.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 无效报备明细记录
 * @TableName b_customer_repeat_report_detail
 */
@Data
public class ReportFailVo implements Serializable {
    /**
     * 主键id
     */
    private String id;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 客户来源 1 招商专员 2 区域招商专员  4 万企通
     */
    private Integer sourceType;

    /**
     * 手机号类型 1 隐号 2 全号
     */
    private Integer mobileType;

    /**
     * 客户手机隐号
     */
    private String customerMobile;

    /**
     * 客户手机全号
     */
    private String oldCustomerMobile;

    /**
     * 企业名称
     */
    private String customerName;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 岗位
     */
    private String jobCode;

    /**
     * 报备人id
     */
    private String reportUserId;

    /**
     * 原因
     */
    private String reason;

    /**
     * 报备时间
     */
    private String reportTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 报备人
     */
    private String reportName;

    /**
     * 客户来源 1 招商专员 2 区域招商专员  4 万企通
     */
    private String sourceTypeDesc;

    private String rownum;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公司全称")
    private String oldCustomerName;

    /**
     * 申诉记录
     * @param
     * @return
     */
    public Object[] toData1(boolean isAll, List<String> fileds) {
        String mobile = getCustomerMobile();
        String customerName = getCustomerName();
        if (isAll) {
            mobile = getOldCustomerMobile();
            customerName = getOldCustomerName();
        }

        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("reason")){
            met.add(getReason());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("customerName")){
            met.add(customerName);
        }
        if(fileds.contains("customerMobile")){
            met.add(mobile);
        }
        if(fileds.contains("reportTime")){
            met.add(getReportTime());
        }
        if(fileds.contains("reportName")){
            met.add(getReportName());
        }
        if(fileds.contains("sourceTypeDesc")){
            met.add(getSourceTypeDesc());
        }

        Object[] objects = met.toArray();
        return objects;


//        return new Object[] {
//                getRownum(),getReason(), getProjectName(), customerName, getContacts(), mobile,
//                getReportTime(), getReportName(), getSourceTypeDesc()
//        };
    }
    public String[] courtCaseTitle1 = new String[] {
            "序号","报备失败原因", "项目", "企业名称", "联系人", "联系人方式", "报备时间", "报备人", "报备人身份"
    };
}
