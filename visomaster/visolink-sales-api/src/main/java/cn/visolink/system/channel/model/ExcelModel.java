package cn.visolink.system.channel.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Description:
 *
 * @author: wanggang
 * @date: 2020.01.13
 */
@Data
public class ExcelModel {

    private String rownum;//
    private String OpportunityClueId;//机会ID
    private String ProjectClueId;//线索ID
    private String CustomerID;//客户ID
    private String CustomerName;//客户姓名
    private String BasicCustomerId;//客户ID
    private String CustomerMobileAll;//全号
    private String CustomerMobile;//客户手机隐号
    private String CustomerGender;//客户性别
    private String CustomerCardTypeDesc;//证件类型
    private String CustomerCardType;//证件类型
    private String CustomerCardNum;//证件号码
    private String Label;//客户标签
    private String caseLabel;//客户标签
    private String channelLabel;//渠道标签
    private String Level;//意向等级
    private String ReportUserName;//报备人
    private String ClueStatus;//状态
    private String SourceType;//成交渠道
    private String SourceTypeDesc;//成交渠道
    private String CustomerLevel;//客储等级
    private String MainMediaName;//媒体大类
    private String SubMediaName;//媒体子类
    private String ProjectName;//项目名称
    private String AreaName;//区域
    private String TokerAttributionTeamName;//
    private String TokerAttributionName;//
    private String TokerAttributionTime;//
    private String SalesAttributionTeamName;//销售团队
    private String SalesAttributionName;//案场归属人
    private String SalesAttributionTime;//案场归属时间
    private String ReportCreateTime;//报备时间
    private String LastRefreshReportExpireDate;//最新报备时间
    private String TheFirstVisitDate;//到访时间（首访）
    private String BookingDate;//认筹时间
    private String SubscribingDate;//认购时间
    private String ContractDate;//签约时间
    private String ExpectedVisitDate;//预计到访时间
    private String TokerVisitExpireDate;//渠道失效时间
    private String ReportExpireDate;//报备失效时间
    private String SalesTheLatestFollowDate;//案场最近跟进时间
    private String FollowUpDate;//最近跟进时间
    private String FollowUpDetail;//最近跟进内容
    private String FollowUpWay;//最近跟进方式
    private String CustomerAddress;//客户地址
    private String AgeGroupDesc;//年龄段
    private String WorkAreaDesc;//工作区域
    private String BelongIndustriseDesc;//所属行业
    private String LifeAreaDesc;//生活区域
    private String FamilyStructureDesc;//家庭结构
    private String FamilyIncomeDesc;//家庭收入
    private String PurchasePurposeDesc;//置业目的
    private String HomeNumDesc;//置业次数
    private String CurrentHouseTypeDesc;//现居住房型
    private String IntentionalAreaDesc;//意向面积
    private String IntentionalFloorDesc;//意向楼层
    private String AcceptPriceDesc;//接受单价
    private String AcceptTotalPriceDesc;//接受总价
    private String BuyPointDesc;//买点
    private String ResistanceDesc;//抗性
    private String Description;//备注
    private String Qualifications;//是否具备购房资格
    private String NoEligibilityReasonDesc;//无购房资格原因
    private String PropertyTypeDesc;//物业类型
    private String EligibilitySolveDesc;//购房资格能否解决
    private String PurchaseFundsSourceDesc;//购房资金来源
    private String HousingNumberDesc;//持房套数
    private String IsTemplateRoomDesc;//是否样板房
    private String IsAddWeChatDesc;//是否加微信
    private String ReceptionDurationDesc;//接待时长
    private String DecisionMakerDesc;//决策人
    private String HobbyDesc;//兴趣爱好
    private String VehicleInformationDesc;//车辆信息
    private String MajorCompetitorsDesc;//主要竞品
    private String MinorCompetitionDesc;//次要竞品
    private String ResidentialAreaDesc;//居住小区
    private String EmployerDesc;//工作单位
    private String WorkJobsDesc;//工作岗位
    private String IsBuyFitUpPackageDesc;//是否购买装修包
    private String DataCompleteRate;//关键信息完成度
    private String DataCompleteAttachRate;//附加信息完成度
    private String IsRepurchase;//是否是联名客户
    private String TransactionType;//卡片类型
    private String CardLockTypeDesc;//卡锁定方式描述
    private String FirstScale;//首付比例
    private String CardLockMoney;//卡锁定金额
    private String CreditSubmission;//是否提交征信
    private String HousingQualification;//是否具备购房资格
    private String ReportUserRole;//全民经纪人推荐客户时的身份
    private String ReportSourceDesc;
    private String TradeLevel;
    private String Remarks;//渠道备注
    private String AccountManager;//大客户经理姓名
    private String IsSenior;//是否为二级经纪人
    private String ActiveName;//活动名称
    private String clueValidity;//客户有效性
    private String invalidReason;//作废原因
    private int isSecondBroker;

    public String getRownum(){
        return StringUtils.isBlank(rownum) ? "" : rownum.replace(".0","");
    }
    /**
     * 根据条件，或者全号，或引号数据
     * @param isAllPhone 是否获取全号
     * @return
     */
    private String getCustomerMobile(boolean isAllPhone) {
        String phone = getCustomerMobile();
        if (isAllPhone) {
            phone = getCustomerMobileAll();
        }
        return phone;
    }


    /**
     * 格式化跟进记录
     * @return
     */
    private String formatFollowUpInfo() {
        String followUpInfo = "";
        if (getFollowUpWay() != null && getFollowUpDetail() != null) {
            followUpInfo = getFollowUpWay() + "--" + getFollowUpDetail();
        } else if (getFollowUpWay() != null && getFollowUpDetail() == null) {
            followUpInfo = getFollowUpWay();
        } else if (getFollowUpWay() == null && getFollowUpDetail() != null) {
            followUpInfo = getFollowUpDetail();
        }
        return followUpInfo;
    }

    /**
     * 获取渠道的数据
     * @param isAllPhone
     * @return
     */
    public Object[] toChannelData(boolean isAllPhone){
        String phone = getCustomerMobile(isAllPhone);
        return new Object[]{
                getRownum(),getAreaName(),getProjectName(),getCustomerName(),getCustomerGender(),
                phone,getSalesAttributionName(),getReportUserName(),
                getClueStatus(),getReportSourceDesc(),getCustomerLevel(),
                getClueValidity(),getMainMediaName(),getSubMediaName(),getLevel(),getTradeLevel(),getCaseLabel() ,getChannelLabel() ,
                getReportCreateTime(),getExpectedVisitDate(),
                getTheFirstVisitDate(),getFollowUpDate(),
                getLastRefreshReportExpireDate(),getReportExpireDate(),getTokerVisitExpireDate(),getRemarks()
        };
    }

    public String[]  channelTitle =  new String[]{
            "序号","区域/事业部","项目","客户姓名","性别",
            "客户电话","置业顾问","报备人","客户状态","报备来源","客储等级","客户有效性",
            "媒体大类","媒体子类","渠道意向等级","案场意向等级","案场标签","渠道标签","报备时间","预计到访时间",
            "首访时间","最近跟进时间","最新报备时间",
            "报备失效时间","渠道失效时间","备注"};

    /**
     * 获取案场的数据
     * @param isAllPhone
     * @return
     */
    public Object[] toCourtCaseData(boolean isAllPhone){
        String phone = getCustomerMobile(isAllPhone);
        return new Object[]{
                getRownum(),getAreaName(),getProjectName(),getCustomerName(),getCustomerGender(),
                phone,getSalesAttributionName(),getSalesAttributionTeamName(),getReportUserName(),getClueStatus(),getReportSourceDesc(),getCustomerLevel(),getClueValidity(),
                getMainMediaName(),getSubMediaName(),getLevel(),getCaseLabel(),getChannelLabel(),getReportCreateTime(),getExpectedVisitDate(),
                getTheFirstVisitDate(),getSubscribingDate(),getContractDate(),getFollowUpDate(),
                getCustomerCardTypeDesc(),getCustomerCardNum(),getCustomerAddress(),getAgeGroupDesc(),getWorkAreaDesc(),
                getBelongIndustriseDesc(),getLifeAreaDesc(),getFamilyStructureDesc(),getFamilyIncomeDesc(),getPurchasePurposeDesc(),
                getHomeNumDesc(),getCurrentHouseTypeDesc(),getIntentionalAreaDesc(),getIntentionalFloorDesc(),getAcceptPriceDesc(),
                getAcceptTotalPriceDesc(),getBuyPointDesc(),getResistanceDesc(),getQualifications(),getNoEligibilityReasonDesc(),
                getIsRepurchase(),getDescription(),getDataCompleteRate(),getPropertyTypeDesc(),getEligibilitySolveDesc(),
                getPurchaseFundsSourceDesc(),getHousingNumberDesc(),getIsTemplateRoomDesc(),getIsAddWeChatDesc(),getReceptionDurationDesc(),
                getDecisionMakerDesc(),getHobbyDesc(),getVehicleInformationDesc(),getMajorCompetitorsDesc(),getMinorCompetitionDesc(),
                getResidentialAreaDesc(),getEmployerDesc(),getWorkJobsDesc(),getIsBuyFitUpPackageDesc(),getDataCompleteAttachRate()
        };
    }

    public String[]  courtCaseTitle =  new String[]{
            "序号","区域/事业部","项目","客户姓名","性别",
            "客户电话","置业顾问","案场团队","报备人","客户状态","报备来源","客储等级","客户有效性",
            "媒体大类","媒体子类","意向等级","案场标签","渠道标签","报备时间","预计到访时间",
            "首访时间","认购时间","签约时间","最近跟进时间",
            "证件类型","证件号码","联系地址","年龄段","工作区域",
            "所属行业","生活区域","家庭结构","家庭收入","置业目的",
            "置业次数","现居住房型","意向面积","意向楼层","接受单价",
            "接受总价","买点","抗性","是否具备购房资格","无购房资格原因",
            "是否联名客户","备注","关键信息完成度","物业类型","购房资格能否解决",
            "购房资金来源","持房套数","是否样板房","是否加微信","接待时长",
            "决策人","兴趣爱好","车辆信息","主要竞品","次要竞品","居住小区",
            "工作单位", "工作岗位","是否购买装修包","附加信息完成度"};


    /**
     * 获取排卡的数据
     * @param isAllPhone
     * @return
     */
    public Object[] toCardData(boolean isAllPhone) {
        String phone = getCustomerMobile(isAllPhone);
        String followUpInfo = formatFollowUpInfo();
        return new Object[]{
                getRownum(),getAreaName(),getProjectName(),getCustomerName(),getCustomerGender(),
                phone,getSalesAttributionName(),getReportUserName(),getClueStatus(),getCustomerLevel(),
                getMainMediaName(),getSubMediaName(),getLevel(),
                getTheFirstVisitDate(),getFollowUpDate(),
                getTransactionType(),getPropertyTypeDesc(),getFirstScale(),getCardLockTypeDesc(),String.valueOf(getCardLockMoney()),
                getCreditSubmission(),getHousingQualification()
        };
    }

    public String[]  cardTitle =  new String[]{
            "序号","区域/事业部","项目","客户姓名","性别",
            "客户电话","置业顾问","报备人","客户状态","客储等级",
            "媒体大类","媒体子类","渠道意向等级","首访时间","最近跟进时间","排卡类型","分类",
            "首付比例","锁定方式","锁定金额","是否提交征信","是否具备购房资格"};

}
