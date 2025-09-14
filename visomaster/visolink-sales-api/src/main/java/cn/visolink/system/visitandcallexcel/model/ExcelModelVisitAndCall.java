package cn.visolink.system.visitandcallexcel.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Description:
 *
 * @author: wanggang
 * @date: 2020.01.13
 */
@Data
public class ExcelModelVisitAndCall {

    private String rownum;//
    private String opportunityClueId;//机会ID
    private String areaName;//区域
    private String projectName;//项目名称
    private String customerName;//客户姓名
    private String customerMobile;//客户手机
    private String salesAttributionName;//当前置业顾问
    private String clueStatus;//状态
    private String customerLB;//来访类别
    private String number;//累计来访次数
    private String visitDate;//到访时间
    private String sourceType;//成交类型
    private String sourceTypeDesc;//成交类型
    private String performanceAttributor;//业绩归属人
    private String actualVisitsCount;//来访人数
    private String mainMediaName;//认知途径
    private String subMediaName;//认知渠道
    private String saleName;//当前置业顾问
    private String followUpDate;//跟进时间
    private String followUpWay;//跟进方式
    private String reportUserID;//报备人 id
    private String reportUserName;//报备人
    private String reportTeamId;//报备人组织ID
    private String reportCreateTime;//报备时间
    private String theFirstVisitDate;//首访时间
    private String lastRefTime;//最后一次刷新有效期时间
    private String reportExpireDate;//报备有效期截止时间
    private String guestTime;//防截客截止时间
    private String tokerVisitExpireDate;//渠道有效期截至时间
    private String resultCode;//判客结果
    private String invalidReason;//判客失败原因
    private String invalidDate;//判客失败原因

    public String getRownum(){
        return StringUtils.isBlank(rownum) ? "" : rownum.replace(".0","");
    }

    public String[]  toVisitTitle =  new String[]{
            "序号","区域","项目","姓名",
            "联系方式","当前置业顾问","当前客户状态",
            "来访类别","累计来访次数","到访时间","成交类型",
            "业绩归属人","来访人数","认知途径","认知渠道"};

    /**
     * 获取来访的数据
     * @param
     * @return
     */
    public Object[] toVisitData(){
        return new Object[]{
                getRownum(),getAreaName(),getProjectName(),getCustomerName(),getCustomerMobile(),
                getSalesAttributionName(),getClueStatus(),getCustomerLB(),getNumber(),getVisitDate(),
                getSourceTypeDesc(),getPerformanceAttributor(),getActualVisitsCount(),getMainMediaName(),getSubMediaName()
        };
    }

    public String[]  toCallTitle =  new String[]{
            "序号","区域","项目","姓名",
            "联系方式","当前置业顾问","当前客户状态",
            "客户类别","跟进时间","跟进方式","认知途径","认知渠道"};

    /**
     * 获取来电的数据
     * @param
     * @return
     */
    public Object[] toCallData(){
        return new Object[]{
                getRownum(),getAreaName(),getProjectName(),getCustomerName(),getCustomerMobile(),
                getSaleName(),getClueStatus(),getCustomerLB(),getFollowUpDate(),getFollowUpWay(),
                getMainMediaName(),getSubMediaName()
        };
    }

    public String[]  toReportVisitInfoTitle =  new String[]{
            "序号","项目","姓名","联系方式",
            "报备人","成交类型","当前客户状态","当前置业顾问",
            "报备时间","首访时间","最后一次刷新有效期时间","报备有效期截止时间",
            "防截客截止时间","渠道有效期截至时间","判客结果","判客失败原因","作废时间"};
    /**
     * 获取判客的数据
     * @param
     * @return
     */
    public Object[] toReportVisitInfoData(){
        return new Object[]{
                getRownum(),getProjectName(),getCustomerName(),getCustomerMobile(),
                getReportUserName(),getSourceTypeDesc(),getClueStatus(),getSalesAttributionName(),
                getReportCreateTime(),getTheFirstVisitDate(),getLastRefTime(),getReportExpireDate(),
                getGuestTime(),getTokerVisitExpireDate(),getResultCode(),getInvalidReason(),getInvalidDate()
        };
    }


}
