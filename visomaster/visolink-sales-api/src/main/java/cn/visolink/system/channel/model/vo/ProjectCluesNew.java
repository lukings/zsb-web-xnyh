package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ProjectClues
 * </p>
 *
 * @author autoJob
 * @since 2019-08-27
 */
@Data
@ApiModel(value = "ProjectClues对象", description = "线索表")
public class ProjectCluesNew implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "机会id")
    private String opportunityClueId;

    @ApiModelProperty(value = "线索id")
    private String projectClueId;

    @ApiModelProperty(value = "销售系统顾客ID")
    private String customerId;

    @ApiModelProperty(value = "销售系统机会ID")
    private String intentionId;

    @ApiModelProperty(value = "案场客户表ID")
    private String basicCustomerId;

    @ApiModelProperty(value = "客储等级 1正常报备  1.5   巡展报备，外展报备  2     来访客户  2.5  小卡，大卡  3    认购  4    签约、退房、退订等")
    private String customerLevel;

    @ApiModelProperty(value = "意向等级")
    private String level;

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "客户手机号")
    private String customerMobile;

    @ApiModelProperty(value = "客户性别")
    private String customerGender;

    @ApiModelProperty(value = "客户年龄")
    private String customerAge;

    @ApiModelProperty(value = "客户标签")
    private String label;

    @ApiModelProperty(value = "案场标签")
    private String caseLabel;

    @ApiModelProperty(value = "渠道标签")
    private String channelLabel;

    @ApiModelProperty(value = "报备人角色（1：业务员 2：四强业务员 3：个人渠道商 4：公司渠道商）")
    private String reportUserRole;

    @ApiModelProperty(value = "媒体大类ID")
    private String mainMediaGuId;

    @ApiModelProperty(value = "媒体大类描述")
    private String mainMediaName;

    @ApiModelProperty(value = "媒体子类ID")
    private String subMediaGuId;

    @ApiModelProperty(value = "媒体子类描述")
    private String subMediaName;

    @ApiModelProperty(value = "客户证件类型")
    private String customerCardType;

    @ApiModelProperty(value = "客户证件类型描述")
    private String customerCardTypeDesc;

    @ApiModelProperty(value = "客户证件号码")
    private String customerCardNum;

    @ApiModelProperty(value = "线索状态 1：未到访 2：已到访 3：排小卡 4：排大卡 5：订房 6：认筹 7：认购 8：签约 9：作废")
    private String clueStatus;

    @ApiModelProperty(value = "首访问卷是否填写")
    private Integer isFirstTable;

    @ApiModelProperty(value = "复访时间")
    private String visitDate;

    @ApiModelProperty(value = "接手时间")
    private String catchTime;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "报备人员ID")
    private String reportUserId;

    @ApiModelProperty(value = "报备人姓名")
    private String reportUserName;

    @ApiModelProperty(value = "报备人团队ID")
    private String reportTeamId;

    @ApiModelProperty(value = "报备时间")
    private String reportCreateTime;

    @ApiModelProperty(value = "报备人团队名称")
    private String reportTeamName;

    @ApiModelProperty(value = "案场归属人id")
    private String salesAttributionId;

    @ApiModelProperty(value = "案场归属人名称")
    private String salesAttributionName;

    @ApiModelProperty(value = "案场归属人队id")
    private String salesAttributionTeamId;

    @ApiModelProperty(value = "案场归属人队名称")
    private String salesAttributionTeamName;

    @ApiModelProperty(value = "案场归属时间")
    private String salesAttributionTime;

    @ApiModelProperty(value = "刷新数据标识")
    private String flag;

    @ApiModelProperty(value = "客户地址")
    private String customerAddress;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "Longitude")
    private String longitude;

    @ApiModelProperty(value = "Latitude")
    private String latitude;

    @ApiModelProperty(value = "渠道最近跟近日期")
    private String tokerTheLatestFollowDate;

    @ApiModelProperty(value = "案场是否跟进逾期")
    private String isSalesFollowExpire;

    @ApiModelProperty(value = "过保时间")
    private String salesFollowExpireDate;

    @ApiModelProperty(value = "剩余天数")
    private String remainingDays;

    @ApiModelProperty(value = "案场最近跟近日期")
    private String salesTheLatestFollowDate;

    @ApiModelProperty(value = "修改时间")
    private String editorTime;

    @ApiModelProperty(value = "是否删除 0 否  1是")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "创建人id")
    private String createUserId;
    private String createUserName;

    @ApiModelProperty(value = "修改人ID")
    private String editUserId;

    private Integer rownum;

    @ApiModelProperty(value = "案场意向等级")
    private String tradeLevel;

    @ApiModelProperty(value = "置业顾问电话")
    private String salesAttReportMobile;

    @ApiModelProperty(value = "客户手机号全号")
    private String oldCustomerMobile;

    @ApiModelProperty(value = "线索主题")
    private String theme;

    @ApiModelProperty(value = "线索来源（1：电话 2：微信 3: 拜访）")
    private String sourceMode;

    @ApiModelProperty(value = "备选号码")
    private String alternativeTelephone;

    @ApiModelProperty(value = "家庭电话")
    private String homePhone;

    @ApiModelProperty(value = "公司电话")
    private String companyPhone;

    @ApiModelProperty(value = "所属行业描述")
    private String belongIndustriseDesc;

    @ApiModelProperty(value = "报备人手机号")
    private String reportUserMobile;

    @ApiModelProperty(value = "是否园区")
    private String isPark;

    @ApiModelProperty(value = "园区地址")
    private String parkAddress;

    @ApiModelProperty(value = "园区名称")
    private String parkName;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "所属行业子类")
    private String belongIndustriseTwoDesc;

    @ApiModelProperty(value = "园区层数")
    private String parkFloor;

    @ApiModelProperty(value = "公司全称")
    private String oldCustomerName;

    @ApiModelProperty(value = "首访时间")
    private String theFirstVisitDate;

    @ApiModelProperty(value = "是否捞取客户")
    private String isTaoGuest;

    @ApiModelProperty(value = "企业地址")
    private String detailedAddress;

    @ApiModelProperty("二级分类")
    private String industryDirectoryChildDesc;

    @ApiModelProperty("主营产品")
    private String businessProducts;

    @ApiModelProperty("主要原材料")
    private String mainRawMaterials;

    @ApiModelProperty("企业现有员工数")
    private String peopleNum;

    @ApiModelProperty("现有厂房面积")
    private String existingPlantArea;

    @ApiModelProperty("企业年产值")
    private String annualOutputValue;

    @ApiModelProperty("企业年度纳税额")
    private String taxAmountYear;

    @ApiModelProperty("现有厂房类型")
    private String workShopType;
    @ApiModelProperty("现有厂房类型描述")
    private String workShopTypeDesc;

    @ApiModelProperty("厂房类型")
    private String plantType;
    @ApiModelProperty("厂房类型描述")
    private String plantTypeDesc;

    @ApiModelProperty("意向类型")
    private String intentionTypeDesc;

    @ApiModelProperty("意向面积")
    private String intentionalAreaDesc;

    @ApiModelProperty("意向单价")
    private String acceptPriceDesc;

    @ApiModelProperty("意向楼层")
    private String intentionalFloorDesc;

    @ApiModelProperty(value = "是否转介中")
    private String isEditOk;

    @ApiModelProperty(value = "是否允许跟进 0 允许跟进 1 存在待审批跟进 2 存在审核驳回跟进")
    private String isFollUpOk;
    private String isFollOrder;

    @ApiModelProperty(value = "到访数")
    private String visitCount;

    @ApiModelProperty(value = "三个一前拜访数")
    private String threeOnesBeforeCount;

    @ApiModelProperty(value = "三个一后拜访数")
    private String threeOnesAfterCount;

    @ApiModelProperty(value = "拜访数")
    private String comeVisitCount;

    @ApiModelProperty(value = "分配时间")
    private String disTime;

    @ApiModelProperty(value = "客户性质 0:企业 1:个人")
    private String customerType;

    @ApiModelProperty(value="所属行业")
    private String belongIndustrise;

    @ApiModelProperty(value = "所属行业子类")

    private String belongIndustriseTwo;
    @ApiModelProperty(value = "所属行业子子类")

    private String belongIndustriseThree;
    @ApiModelProperty(value = "所属行业四子类")

    private String belongIndustriseFour;

    @ApiModelProperty(value = "主营产品")
    private String mainProducts;

    @ApiModelProperty(value = "职位描述")
    private String position;

    @ApiModelProperty(value = "法人")
    private String legalPerson;

    @ApiModelProperty(value = "法人联系电话")
    private String legalPersonPhone;

    @ApiModelProperty(value = "法人证件号码")
    private String legalPersonCardNum;

    @ApiModelProperty(value = "企业性质")
    private String enterpriseType ;

    @ApiModelProperty(value = "邮政编码")
    private String postCode;

    @ApiModelProperty(value = "所在楼层")
    private String floor;

    @ApiModelProperty(value = "租售类型（1：租赁 2：购买 3：自建）")
    private String rentAndSaleType;

    @ApiModelProperty(value = "租售价格")
    private String rentalPrice;

    @ApiModelProperty(value = "意向区域")
    private String yxArea;

    @ApiModelProperty(value = "意向分类")
    private String intentionClass;

    @ApiModelProperty(value = "意向类型")
    private String intentionType;

    @ApiModelProperty(value = "意向单价")
    private String intentionalPrice;

    @ApiModelProperty(value = "现有租金（元/㎡）")
    private String nowRent;

    @ApiModelProperty(value = "现有办公面积")
    private String nowOfficeSpace;

    @ApiModelProperty(value = "污染物排放（0:污水,1:废气,2:污水废气)")
    private String pollutantDischarge;

    @ApiModelProperty(value = "意向购买原因")
    private String zyreason;

    @ApiModelProperty(value = "客户难点及疑虑")
    private String resistanceDesc;

    @ApiModelProperty(value = "工艺流程")
    private String technologicalProcess;

    @ApiModelProperty(value = "主要设备")
    private String majorEquipment;

    @ApiModelProperty(value = "企业污染、能耗重点情况说明")
    private String importantDescription;

    @ApiModelProperty(value = "企业年度用电（KW）")
    private String electricityYear;

    @ApiModelProperty(value = "企业年度用水（万吨）")
    private String waterYear;

    @ApiModelProperty(value = "附件")
    private String enclosures;

    @ApiModelProperty(value = "客户完整度")
    private String perfectionProgress;

    @ApiModelProperty(value = "子级")
    private String childrenStr;
    private List<Map> children;

    @ApiModelProperty(value = "交房时间")
    private String deliveryDate;

    @ApiModelProperty(value = "相似客户审批状态")
    private String similarCustomerApproveStatus;

    @ApiModelProperty(value = "是否满足三个一")
    private String isThreeOnes;

    @ApiModelProperty("状态类型")
    private String type;

    /**
     * 获取数据线索
     * @param
     * @return
     */
    public Object[] toData1(boolean isAll,List<String> fileds){
        String mobile = getCustomerMobile();
        String customerName = getCustomerName();
        if (isAll){
            mobile = getOldCustomerMobile();
            customerName = getOldCustomerName();
        }
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("customerName")){
            met.add(customerName);
        }
        if(fileds.contains("customerMobile")){
            met.add(mobile);
        }
        if(fileds.contains("oldCustomerName")){
            met.add(customerName);
        }
        if(fileds.contains("oldCustomerMobile")){
            met.add(mobile);
        }


        if(fileds.contains("customerAddress")){
            met.add(getCustomerAddress());
        }
        if(fileds.contains("detailedAddress")){
            met.add(getDetailedAddress());
        }
        if(fileds.contains("longitude")){
            met.add(getLongitude());
        }
        if(fileds.contains("latitude")){
            met.add(getLatitude());
        }
        if(fileds.contains("belongIndustriseDesc")){
            met.add(getBelongIndustriseDesc());
        }
        if(fileds.contains("belongIndustriseTwoDesc")){
            met.add(getBelongIndustriseTwoDesc());
        }
        if(fileds.contains("businessProducts")){
            met.add(getBusinessProducts());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("clueStatus")){
            met.add(getClueStatus());
        }
        if(fileds.contains("disTime")){
            met.add(getDisTime());
        }
        if(fileds.contains("salesAttributionName")){
            met.add(getSalesAttributionName());
        }
        if(fileds.contains("reportCreateTime")){
            met.add(getReportCreateTime());
        }
        if(fileds.contains("reportUserName")){
            met.add(getReportUserName());
        }
        if(fileds.contains("reportUserRole")){
            met.add(getReportUserRole());
        }
        if(fileds.contains("salesFollowExpireDate")){
            met.add(getSalesFollowExpireDate());
        }
        if(fileds.contains("remainingDays")){
            met.add(getRemainingDays());
        }
        if(fileds.contains("isPark")){
            met.add(getIsPark());
        }
        if(fileds.contains("parkAddress")){
            met.add(getParkAddress());
        }
        if(fileds.contains("parkFloor")){
            met.add(getParkFloor());
        }
        if(fileds.contains("parkName")){
            met.add(getParkName());
        }
        if(fileds.contains("label")){
            met.add(getLabel());
        }
        Object[] objects = met.toArray();
        return objects;
//        return new Object[]{
//                getRownum(), customerName, mobile,getCustomerAddress(),getDetailedAddress(),getBelongIndustriseDesc(),getBelongIndustriseTwoDesc(),
//                getBusinessProducts(),getProjectName(), getReportCreateTime(),
//                getReportUserName(), getReportUserRole(), getIsPark(), getParkAddress(),
//                getParkFloor(), getParkName()
//        };
    }
    public Object[] toOldData1(boolean isAll){
        String mobile = getCustomerMobile();
        String customerName = getCustomerName();
        if (isAll){
            mobile = getOldCustomerMobile();
            customerName = getOldCustomerName();
        }
        return new Object[]{
                getRownum(), customerName, mobile,getCustomerAddress(), getDetailedAddress(),getLongitude(), getLatitude(),getBelongIndustriseDesc(),getBelongIndustriseTwoDesc(),
                getBusinessProducts(),getProjectName(), getReportCreateTime(),
                getReportUserName(), getReportUserRole(), getIsPark(), getParkAddress(),
                getParkFloor(), getParkName()
        };
    }
    public String[]  courtCaseTitle1 =  new String[]{
            "序号", "客户姓名", "联系人方式","企业地址","详细地址","经度","纬度","行业分类","二级分类","主营产品", "意向项目", "录入时间", "录入人",
            "录入人身份", "是否园区", "园区地址", "园区层数", "园区名称"};

    /**
     * 获取数据机会
     * @param
     * @return
     */
    public Object[] toData2(boolean isAll,List<String> fileds,Boolean isHasChild){
        String mobile = getCustomerMobile();
        String customerName = getCustomerName();
        if (isAll){
            mobile = getOldCustomerMobile();
            customerName = getOldCustomerName();
        }
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("areaName")){
            met.add(getAreaName());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("customerName")){
            met.add(customerName);
        }
        if(fileds.contains("oldCustomerName")){
            met.add(customerName);
        }
        if(fileds.contains("customerAddress")){
            met.add(getCustomerAddress());
        }
        if(fileds.contains("detailedAddress")){
            met.add(getDetailedAddress());
        }
        if(fileds.contains("longitude")){
            met.add(getLongitude());
        }
        if(fileds.contains("latitude")){
            met.add(getLatitude());
        }
        if(fileds.contains("sourceMode")){
            met.add(getSourceMode());
        }
        if(fileds.contains("belongIndustriseDesc")){
            met.add(getBelongIndustriseDesc());
        }
        if(fileds.contains("belongIndustriseTwoDesc")){
            met.add(getBelongIndustriseTwoDesc());
        }
        if(fileds.contains("businessProducts")){
            met.add(getBusinessProducts());
        }
        if(fileds.contains("label")){
            met.add(getLabel());
        }
        if(fileds.contains("mainRawMaterials")){
            met.add(getMainRawMaterials());
        }
        if(fileds.contains("peopleNum")){
            met.add(getPeopleNum());
        }
        if(fileds.contains("existingPlantArea")){
            met.add(getExistingPlantArea());
        }
        if(fileds.contains("annualOutputValue")){
            met.add(getAnnualOutputValue());
        }
        if(fileds.contains("taxAmountYear")){
            met.add(getTaxAmountYear());
        }
        if(fileds.contains("plantTypeDesc")){
            met.add(getPlantTypeDesc());
        }
        if(fileds.contains("perfectionProgress")){
            met.add(getPerfectionProgress());
        }
        if(fileds.contains("contacts")){
            met.add(getContacts());
        }
        if(fileds.contains("customerMobile")){
            met.add(mobile);
        }
        if(fileds.contains("oldCustomerMobile")){
            met.add(mobile);
        }
        if(fileds.contains("createTime")){
            met.add(getCreateTime());
        }
        if(fileds.contains("createUserName")){
            met.add(getCreateUserName());
        }
        if(fileds.contains("deliveryDate")){
            met.add(getDeliveryDate());
        }
        if(fileds.contains("reportCreateTime")){
            met.add(getReportCreateTime());
        }
        if(fileds.contains("reportUserName")){
            met.add(getReportUserName());
        }
        if(fileds.contains("salesAttributionName")){
            met.add(getSalesAttributionName());
        }
        if(fileds.contains("reportTeamName")){
            met.add(getReportTeamName());
        }
        if(fileds.contains("reportUserRole")){
            met.add(getReportUserRole());
        }
        if(fileds.contains("salesTheLatestFollowDate")){
            met.add(getSalesTheLatestFollowDate());
        }
        if(fileds.contains("salesFollowExpireDate")){
            met.add(getSalesFollowExpireDate());
        }
        if(fileds.contains("remainingDays")){
            met.add(getRemainingDays());
        }
        if(fileds.contains("visitCount")){
            met.add(getVisitCount());
        }
        if(fileds.contains("threeOnesBeforeCount")){
            met.add(getThreeOnesBeforeCount());
        }
        if(fileds.contains("threeOnesAfterCount")){
            met.add(getThreeOnesAfterCount());
        }
        if(fileds.contains("comeVisitCount")){
            met.add(getComeVisitCount());
        }
        if(fileds.contains("theFirstVisitDate")){
            met.add(getTheFirstVisitDate());
        }
        if(fileds.contains("visitDate")){
            met.add(getVisitDate());
        }
        if(fileds.contains("customerLevel")){
            met.add(getCustomerLevel());
        }
        if(fileds.contains("isTaoGuest")){
            met.add(getIsTaoGuest());
        }
        if(fileds.contains("clueStatus")){
            met.add(getClueStatus());
        }
        if(fileds.contains("isThreeOnes")){
            met.add(getIsThreeOnes());
        }
        if(fileds.contains("intentionTypeDesc")){
            met.add(getIntentionTypeDesc());
        }
        if(fileds.contains("intentionalAreaDesc")){
            met.add(getIntentionalAreaDesc());
        }
        if(fileds.contains("acceptPriceDesc")){
            met.add(getAcceptPriceDesc());
        }
        if(fileds.contains("intentionalFloorDesc")){
            met.add(getIntentionalFloorDesc());
        }
        if(fileds.contains("isPark")){
            met.add(getIsPark());
        }
        if(fileds.contains("parkAddress")){
            met.add(getParkAddress());
        }
        if(fileds.contains("parkFloor")){
            met.add(getParkFloor());
        }
        if(fileds.contains("parkName")){
            met.add(getParkName());
        }
        if(isHasChild){
            met.add(getChildren());
        }
        Object[] objects = met.toArray();
        return objects;
        // Object[] objects = new Object[];
//        return new Object[]{
//                getRownum(), getProjectName(), customerName,getCustomerAddress(),getDetailedAddress(), getSourceMode(), getBelongIndustriseDesc(),
//                getBelongIndustriseTwoDesc(),getBusinessProducts(),getMainRawMaterials(),getPeopleNum(),getExistingPlantArea(),
//                getAnnualOutputValue(),getTaxAmountYear(),getWorkShopTypeDesc(),
//                getContacts(), mobile, getReportCreateTime(), getReportUserName(), getReportTeamName(),getReportUserRole(),
//                getSalesAttributionName(),getSalesAttributionTime(), getSalesFollowExpireDate(),
//                getRemainingDays(), getTheFirstVisitDate(), getVisitDate(), getCustomerLevel(), getIsTaoGuest(),getClueStatus(),
//                getIntentionTypeDesc(),getIntentionalAreaDesc(),
//                getAcceptPriceDesc(),getIntentionalFloorDesc(), getIsPark(), getParkAddress(),
//                getParkFloor(), getParkName()
//        };
    }
    public Object[] toOldData2(boolean isAll){
        String mobile = getCustomerMobile();
        String customerName = getCustomerName();
        if (isAll){
            mobile = getOldCustomerMobile();
            customerName = getOldCustomerName();
        }
        return new Object[]{
                getRownum(), getProjectName(), customerName,getCustomerAddress(),getDetailedAddress(), getLongitude(), getLatitude(), getSourceMode(), getBelongIndustriseDesc(),
                getBelongIndustriseTwoDesc(),getBusinessProducts(),getMainRawMaterials(),getPeopleNum(),getExistingPlantArea(),
                getAnnualOutputValue(),getTaxAmountYear(),getWorkShopTypeDesc(),
                getContacts(), mobile, getReportCreateTime(), getReportUserName(), getReportTeamName(),getReportUserRole(),
                getSalesAttributionName(),getSalesAttributionTime(), getSalesFollowExpireDate(),
                getRemainingDays(), getTheFirstVisitDate(), getVisitDate(), getCustomerLevel(), getIsTaoGuest(),getClueStatus(),getIsThreeOnes(),
                getIntentionTypeDesc(),getIntentionalAreaDesc(),
                getAcceptPriceDesc(),getIntentionalFloorDesc(), getIsPark(), getParkAddress(),
                getParkFloor(), getParkName()
        };
    }
    public String[]  courtCaseTitle2 =  new String[]{
            "序号", "项目", "企业名称","企业地址","详细地址", "经度","纬度", "客户来源", "客户行业", "二级分类","主营产品","主要原材料","企业现有员工数","现有厂房面积",
            "企业年产值","企业年度纳税额","厂房类型",
            "联系人", "联系人方式", "报备时间", "报备人", "报备人招商团队","报备人身份",
            "接待人", "接待最新跟进时间", "过保有效时间", "剩余天数", "首访时间", "复访时间", "客户等级","是否捞取客户", "当前状态","是否满足三个一","意向类型",
            "意向面积","意向单价","意向楼层","是否园区", "园区地址", "园区层数", "园区名称"
    };
}
