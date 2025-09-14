package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName OppInformation
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/16 21:03
 **/
@Data
@ApiModel(value = "机会信息", description = "机会信息")
public class OppInformation {

    @ApiModelProperty(value = "机会id")
    private String opportunityClueId;
    @ApiModelProperty(value = "线索id")
    private String projectClueId;
    @ApiModelProperty(value = "客户类别")
    private String customerType;
    @ApiModelProperty(value = "客户姓名")
    private String customerName;
    @ApiModelProperty(value = "客户姓名(隐号)")
    private String customerNameIns;
    @ApiModelProperty(value = "法人")
    private String legalPerson;
    @ApiModelProperty(value = "客户手机号")
    private String customerMobile;
    @ApiModelProperty(value = "客户手机号")
    private String customerMobileIns;
    @ApiModelProperty(value = "职位")
    private String position;
    @ApiModelProperty(value = "证件号码")
    private String customerCardNum;
    @ApiModelProperty(value = "证件类型")
    private String customerCardTypeDesc;
    @ApiModelProperty(value = "性别")
    private String customerGender;
    @ApiModelProperty(value = "地址")
    private String customerAddress;
    @ApiModelProperty(value = "备注")
    private String remarks;
    @ApiModelProperty(value = "媒体大类")
    private String mainMediaName;
    @ApiModelProperty(value = "媒体子类")
    private String subMediaName;
    @ApiModelProperty(value = "意向等级")
    private String tradeLevel;
    @ApiModelProperty(value = "生产产品")
    private String products;
    @ApiModelProperty(value = "意向面积")
    private String demandArea;
    @ApiModelProperty(value = "意向楼层")
    private String demandFloor;
    @ApiModelProperty(value = "现有厂房面积")
    private String existingPlantArea;
    @ApiModelProperty(value = "年产值")
    private String annualOutputValue;
    @ApiModelProperty(value = "公司人数")
    private String peopleNum;
    @ApiModelProperty(value = "录入人")
    private String reportUserName;
    @ApiModelProperty(value = "录入时间")
    private String reportCreateTime;
    @ApiModelProperty(value = "录入人角色")
    private String reportUserRole;
    @ApiModelProperty(value = "录入人手机号")
    private String reportUserMobile;
    @ApiModelProperty(value = "接待人")
    private String salesAttributionName;
    @ApiModelProperty(value = "接待人ID")
    private String salesAttributionId;
    @ApiModelProperty(value = "接待时间")
    private String salesAttributionTime;
    @ApiModelProperty(value = "录入人最近跟进时间")
    private String tokerTheLatestFollowDate;
    @ApiModelProperty(value = "接待人最近跟进时间")
    private String salesTheLatestFollowDate;
    @ApiModelProperty(value = "是否捞取客户")
    private String isTaoGuest;
    @ApiModelProperty(value = "状态")
    private String clueStatus;
    @ApiModelProperty(value = "购房用途")
    private String purchasePurposeDesc;
    @ApiModelProperty(value = "所属行业")
    private String belongIndustrise;
    private String belongIndustriseDesc;
    @ApiModelProperty(value = "产业目录一级")
    private String industryDirectoryDesc;
    @ApiModelProperty(value = "产业目录二级")
    private String industryDirectoryChildDesc;
    @ApiModelProperty(value = "产品类型")
    private String productTypeDesc;
    @ApiModelProperty(value = "关注因素")
    private String gzysDesc;
    @ApiModelProperty(value = "主要抗性")
    private String resistanceDesc;
    @ApiModelProperty(value = "工作区域")
    private String workAreaDesc;
    @ApiModelProperty(value = "生活区域")
    private String lifeAreaDesc;
    @ApiModelProperty(value = "意向区域")
    private String yxArea;
    private String yxAreaDesc;
    @ApiModelProperty(value = "置业原因")
    private String zyreason;
    private String zyreasonDesc;
    @ApiModelProperty(value = "婚姻状况")
    private String maritalStatusDesc;
    @ApiModelProperty(value = "家庭结构")
    private String familyStructureDesc;
    @ApiModelProperty(value = "附件")
    private List<String> enclosures;

    @ApiModelProperty(value = "客户来源")
    private String sourceMode;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "是否园区")
    private Integer isPark;

    @ApiModelProperty(value = "园区名称")
    private String parkName;

    @ApiModelProperty(value = "园区地址")
    private String parkAddress;

    @ApiModelProperty(value = "意向分类")
    private String intentionClass;
    private String intentionClassDesc;

    @ApiModelProperty(value = "意向类型")
    private String intentionType;
    private String intentionTypeDesc;

    @ApiModelProperty(value = "意向签约年月")
    private String yxSignTime;

    @ApiModelProperty(value = "意向面积")
    private String intentionalArea;
    private String intentionalAreaDesc;

    @ApiModelProperty(value = "意向楼栋")
    private String intentionalFloor;
    private String intentionalFloorDesc;

    @ApiModelProperty(value = "意向单价")
    private String intentionalPrice;

    @ApiModelProperty(value = "特殊意向需求备注")
    private String demandRemarks;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "工艺流程")
    private String technologicalProcess;

    @ApiModelProperty(value = "主要原材料")
    private String mainRawMaterials;

    @ApiModelProperty(value = "企业年度用电")
    private String electricityYear;

    @ApiModelProperty(value = "纳税人识别号")
    private String taxpayerNum;

    @ApiModelProperty(value = "企业关联公司的id")
    private String relationOpportunityClueId;

    @ApiModelProperty(value = "企业关联公司")
    private String relationCompany;

    @ApiModelProperty(value = "企业污染、能耗重点情况说明")
    private String importantDescription;

    @ApiModelProperty(value = "企业年度用水")
    private String waterYear;

    @ApiModelProperty(value = "主要经营产品")
    private String businessProducts;

    @ApiModelProperty(value = "所在区域")
    private String area;

    @ApiModelProperty(value = "主要设备")
    private String majorEquipment;

    @ApiModelProperty(value = "企业年度纳税额")
    private String taxAmountYear;

    @ApiModelProperty(value = "现有厂房类型")
    private String workShopTypeDesc;

    @ApiModelProperty(value = "客户行业子类")
    private String belongIndustriseTwo;
    private String belongIndustriseTwoDesc;
    private String belongIndustriseThree;
    private String belongIndustriseThreeDesc;
    private String belongIndustriseFour;
    private String belongIndustriseFourDesc;

    @ApiModelProperty(value = "主营产品")
    private String mainProducts;

    @ApiModelProperty(value = "所处楼层")
    private String floor;

    @ApiModelProperty(value = "厂房类型")
    private String plantType;
    private String plantTypeDesc;

    @ApiModelProperty(value = "租售类型")
    private String rentAndSaleType;
    private String rentAndSaleTypeDesc;

    @ApiModelProperty(value = "租售价格")
    private String rentalPrice;

    @ApiModelProperty(value = "园区层数")
    private String parkFloor;

    @ApiModelProperty(value = "法人联系电话")
    private String legalPersonPhone;
    @ApiModelProperty(value = "法人联系电话")
    private String legalPersonPhoneIns;

    @ApiModelProperty(value = "企业性质")
    private String enterpriseType ;
    @ApiModelProperty(value = "企业性质描述")
    private String enterpriseTypeDesc;

    @ApiModelProperty(value = "证件类型")
    private String customerCardType;

    @ApiModelProperty(value = "邮政编码")
    private String postCode;

    @ApiModelProperty(value = "污染物排放（0:污水,1:废气,2:污水废气)")
    private String pollutantDischarge;
    @ApiModelProperty(value = "污染物排放描述")
    private String pollutantDischargeDesc;
    @ApiModelProperty(value = "宿舍需求(床位)")
    private String needs;
    @ApiModelProperty(value = "现有租金（元/㎡）")
    private String nowRent;
    @ApiModelProperty(value = "现有办公面积")
    private String nowOfficeSpace;

    @ApiModelProperty(value = "详细地址")
    private String detailedAddress;

    @ApiModelProperty(value = "关联客户")
    private List<Map> relCustomerList;

    @ApiModelProperty(value = "是否本人")
    private String isSelf;

    @ApiModelProperty(value = "客户类型")
    private String type;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty("公客池id")
    private String poolId;

    @ApiModelProperty("公客池类型")
    private String poolType;

    @ApiModelProperty("团队ID")
    private String salesAttributionTeamId;


    /**
     * 扩展信息，根据客户状态返回不同的信息
     */
//    private Map<String, String> extendInfo;
//
//    public Map<String, String> getExtendInfo() {
//        return extendInfo;
//    }
//
//    public void setExtendInfo(Map<String, String> extendInfo) {
//        this.extendInfo = extendInfo;
//    }

    @ApiModelProperty("扩展信息")
    private String extendInfo;
}
