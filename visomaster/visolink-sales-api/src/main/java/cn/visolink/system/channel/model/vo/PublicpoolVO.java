package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * PublicpoolVO对象
 * </p>
 *
 * @author autoJob
 * @since 2019-09-02
 */
@Data
@ApiModel(value="Publicpool对象", description="公共池表")
public class PublicpoolVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String poolType;//公共池类型（1：项目 2：区域 3：全国 4: 招商 5：总招商）

    private String addDate;//进入公共池时间

    private String clueId;

    private String name;

    private String gender;

    private String level;

    private String operationTime;

    private String theFirstVisitDate;

    private String clueSource;

    private String reportUserName;

    private String reportTime;

    private String saleId;

    private String saleName;

    private String expireTag;

    private Date catchTime;

    private String catchWay;

    private String clueStatus;

    private String mobile;

    private String projectId;

    private String projectName;

    private String sourceType;

    @ApiModelProperty(value = "1 报备过期2 跟进过期3 到访过期4 丢弃5 顾问离职6 报备人离职")
    private String reason;

    private String customerName;

    private String customerMobile;

    private String customerMobileAll;

    private String cusetomerSource;

    private String projectClueId;

    private String reportCreateTime;

    private String tokerAttributionName;

    private String tokerAttributionTime;

    private String salesAttributionId;

    private String salesAttributionName;

    private String salesAttributionTime;

    private String salesAttributionTeamName;

    private String salesTheLatestFollowDate;//最近跟进时间

    private String genjinshijan;

    private String opportunityClueId;

    private Integer rownum;

    @ApiModelProperty(value = "加入类型")
    private String addType;

    @ApiModelProperty(value = "加入类型描述")
    private String addTypeDesc;

    @ApiModelProperty(value = "加入原因")
    private String addReasonType;

    @ApiModelProperty(value = "加入原因描述")
    private String addReasonDesc;

    private String activateReasonDesc;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "公司全称")
    private String oldCustomerName;

    @ApiModelProperty(value = "企业地址")
    private String customerAddress;

    @ApiModelProperty(value = "详细地址")
    private String detailedAddress;

    @ApiModelProperty(value = "客户来源")
    private String sourceMode;

    @ApiModelProperty(value = "主营产品")
    private String mainProducts;

    @ApiModelProperty(value = "是否园区")
    private String isPark;

    @ApiModelProperty(value = "园区地址")
    private String parkAddress;

    @ApiModelProperty(value = "园区名称")
    private String parkName;

    @ApiModelProperty(value = "园区层数")
    private String parkFloor;

    @ApiModelProperty(value = "客户行业")
    private String belongIndustriseDesc;

    @ApiModelProperty(value = "二级分类")
    private String belongIndustriseTwoDesc;

    @ApiModelProperty(value = "主要原材料")
    private String mainRawMaterials;

    @ApiModelProperty(value = "企业现有员工数")
    private String peopleNum;

    @ApiModelProperty(value = "企业年产值")
    private String annualOutputValue;

    @ApiModelProperty(value = "意向面积")
    private String intentionalAreaDesc;

    @ApiModelProperty(value = "意向单价")
    private String intentionalPrice;

    @ApiModelProperty(value = "企业年度纳税额")
    private String taxAmountYear;

    @ApiModelProperty(value = "现有厂房类型")
    private String workShopTypeDesc;

    @ApiModelProperty(value = "厂房类型")
    private String plantTypeDesc;

    @ApiModelProperty(value = "意向类型")
    private String intentionTypeDesc;

    @ApiModelProperty(value = "意向楼层")
    private String intentionalFloorDesc;

    @ApiModelProperty(value = "现有厂房面积")
    private String existingPlantArea;

    @ApiModelProperty(value = "客户逾期标签")
    private String customerDateLabel;

    @ApiModelProperty(value = "到访数")
    private String visitCount;

    @ApiModelProperty(value = "三个一前拜访数")
    private String threeOnesBeforeCount;

    @ApiModelProperty(value = "三个一后拜访数")
    private String threeOnesAfterCount;

    @ApiModelProperty(value = "拜访数")
    private String comeVisitCount;

    /**
     * 根据条件，或者全号，或引号数据
     * @param isAllPhone 是否获取全号
     * @return
     */
    private String getCustomerMobile(boolean isAllPhone) {
        String phone = getCustomerMobile();
        String customerName = getCustomerName();
        if (isAllPhone) {
            phone = getCustomerMobileAll();
            customerName = getOldCustomerName();
        }
        return phone;
    }
    private String getCustomerName(boolean isAllPhone) {
        String customerName = getCustomerName();
        if (isAllPhone) {
            customerName = getOldCustomerName();
        }
        return customerName;
    }
    /**
     * 获取公共池的数据
     * @param isAllPhone
     * @return
     */
    public Object[] toPublicData(boolean isAllPhone, List<String> fileds){
        String phone = getCustomerMobile(isAllPhone);
        String customerName = getCustomerName(isAllPhone);

        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }

        if(fileds.contains("customerName")){
            met.add(customerName);
        }
        if(fileds.contains("customerAddress")){
            met.add(getCustomerAddress());
        }
        if(fileds.contains("detailedAddress")){
            met.add(getDetailedAddress());
        }
        if(fileds.contains("sourceMode")){
            met.add(getSourceMode());
        }
        if(fileds.contains("customerDateLabel")){
            met.add(getCustomerDateLabel());
        }
        if(fileds.contains("belongIndustriseDesc")){
            met.add(getBelongIndustriseDesc());
        }
        if(fileds.contains("belongIndustriseTwoDesc")){
            met.add(getBelongIndustriseTwoDesc());
        }
        if(fileds.contains("businessProducts")){
            met.add(getMainProducts());
        }
        if(fileds.contains("mainProducts")){
            met.add(getMainProducts());
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

        if(fileds.contains("customerMobile")){
            met.add(phone);
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("contacts")){
            met.add(getContacts());
        }
        if(fileds.contains("clueStatus")){
            met.add(getClueStatus());
        }
        if(fileds.contains("addDate")){
            met.add(getAddDate());
        }
        if(fileds.contains("addTypeDesc")){
            met.add(getAddTypeDesc());
        }
        if(fileds.contains("poolType")){
            met.add(getPoolType());
        }
        if(fileds.contains("salesAttributionName")){
            met.add(getSalesAttributionName());
        }

        if(fileds.contains("reportUserName")){
            met.add(getReportUserName());
        }
        if(fileds.contains("reportTime")){
            met.add(getReportTime());
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

        if(fileds.contains("intentionTypeDesc")){
            met.add(getIntentionTypeDesc());
        }

        if(fileds.contains("intentionalAreaDesc")){
            met.add(getIntentionalAreaDesc());
        }
        if(fileds.contains("intentionalPrice")){
            met.add(getIntentionalPrice());
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

        Object[] objects = met.toArray();
        return objects;

//        return new Object[]{
//                getRownum(), customerName,getCustomerAddress(),getDetailedAddress(),getSourceMode(),getBelongIndustriseDesc(),getBelongIndustriseTwoDesc(),getMainProducts(),
//                getMainRawMaterials(),getPeopleNum(),getExistingPlantArea(),getAnnualOutputValue(),getTaxAmountYear(),getWorkShopTypeDesc(),
//                phone, getProjectName(), getContacts(), getClueStatus(), getAddDate(), getAddTypeDesc(), getPoolType(), getSalesAttributionName(),
//                getReportUserName(), getReportTime(),getIntentionTypeDesc(),getIntentionalAreaDesc(),getIntentionalPrice(),getIntentionalFloorDesc(),
//                getIsPark(),getParkAddress(),getParkFloor(),getParkName()
//        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "序号", "企业名称","企业地址","详细地址","客户来源", "客户行业","二级分类","主营产品","主要原材料","企业现有员工数","现有厂房面积","企业年产值","企业年度纳税额","厂房类型",
            "联系人手机号", "项目", "联系人", "客户状态","加入公共池时间","进入客户池原因", "客户池类型",
            "原归属人", "报备人员", "报备时间","意向类型", "意向面积", "意向单价","意向楼层", "是否园区", "园区地址","园区层数", "园区名称"};
}
