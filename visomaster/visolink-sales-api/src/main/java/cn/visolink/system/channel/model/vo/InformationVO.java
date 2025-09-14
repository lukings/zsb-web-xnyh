package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/12/17
 */
@Data
@ApiModel(value = "InformationVO对象", description = "首访问卷详细信息")
public class InformationVO {

    @ApiModelProperty(value = "线索id")
    private String projectClueId;

    @ApiModelProperty(value = "客户来源（1：招商邀约 2：老客户介绍 3：自然到访 4:万企通）")
    private String sourceMode;

    @ApiModelProperty(value = "机会id")
    private String opportunityClueId;

    @ApiModelProperty(value = "企业名称")
    private String customerName;

    @ApiModelProperty(value = "企业名称（隐号）")
    private String customerNameIns;

    @ApiModelProperty(value = "联系人手机号")
    private String customerMobile;
    @ApiModelProperty(value = "联系人手机号")
    private String customerMobileIns;

    @ApiModelProperty(value = "客户性别")
    private String customerGender;
    private String customerGenderDesc;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "证件类型")
    private String cardType;

    @ApiModelProperty(value = "证件类型描述")
    private String cardTypeName;

    @ApiModelProperty(value="证件号码")
    private String cardNum;

    @ApiModelProperty(value = "职位描述")
    private String position;

    @ApiModelProperty(value = "是否园区（1：是 0：否）")
    private String isPark;
    @ApiModelProperty(value = "园区名称")
    private String parkName;
    @ApiModelProperty(value = "园区地址")
    private String parkAddress;
    @ApiModelProperty(value = "客户地址")
    private String customerAddress;

    @ApiModelProperty(value="意向等级")
    private String level;

    @ApiModelProperty(value = "客户等级")
    private String customerLevel;

    @ApiModelProperty(value = "客户等级")
    private String customerLevelDesc;

    @ApiModelProperty(value = "报备人角色（1：项目招商专员 2：区域招商专员）")
    private String reportUserRole;

    @ApiModelProperty(value = "主营产品")
    private String mainProducts;
    @ApiModelProperty(value = "所在楼层")
    private String floor;
    @ApiModelProperty(value = "厂房类型（1：独栋 2：双拼 3：高层 4：钢构）")
    private String plantType;
    @ApiModelProperty(value = "租售类型（1：租赁 2：购买 3：自建）")
    private String rentAndSaleType;
    @ApiModelProperty(value = "租售价格")
    private String rentalPrice;
    @ApiModelProperty(value = "园区楼层")
    private String parkFloor;
    @ApiModelProperty(value = "经度")
    private String longitude;
    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "意向面积")
    private String intentionalAreaDesc;
    @ApiModelProperty(value = "意向楼层")
    private String intentionalFloorDesc;
    @ApiModelProperty(value = "客户难点及疑虑")
    private String resistanceDesc;

    @ApiModelProperty(value = "意向区域")
    private String yxArea;

    @ApiModelProperty(value = "意向区域描述")
    private String yxAreaDesc;

    @ApiModelProperty(value = "意向购买原因")
    private String zyreason;
    @ApiModelProperty(value = "意向购买原因")
    private String zyreasonDesc;

    @ApiModelProperty(value="所属行业")
    private String belongIndustrise;

    @ApiModelProperty(value="所属行业描述")
    private String belongIndustriseDesc;


    @ApiModelProperty(value = "所属行业子类")
    private String belongIndustriseTwo;
    private String belongIndustriseTwoDesc;
    @ApiModelProperty(value = "所属行业子子类")
    private String belongIndustriseThree;
    private String belongIndustriseThreeDesc;
    @ApiModelProperty(value = "所属行业四子类")
    private String belongIndustriseFour;
    private String belongIndustriseFourDesc;

    @ApiModelProperty(value = "意向分类")
    private String intentionClass;
    @ApiModelProperty(value = "意向分类描述")
    private String intentionClassDesc;
    @ApiModelProperty(value = "意向类型")
    private String intentionType;
    @ApiModelProperty(value = "意向类型描述")
    private String intentionTypeDesc;
    @ApiModelProperty(value = "意向签约年月")
    private String yxSignTime;
    @ApiModelProperty(value = "特殊意向需求备注")
    private String demandRemarks;
    @ApiModelProperty(value = "厂房类型")
    private String plantTypeDesc;
    @ApiModelProperty(value = "租售类型")
    private String rentAndSaleTypeDesc;

    @ApiModelProperty(value = "意向单价")
    private String intentionalPrice;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "纳税人识别号")
    private String taxpayerNum;

    @ApiModelProperty(value = "所在区域")
    private String area;

    @ApiModelProperty(value = "详细地址")
    private String detailedAddress;

    @ApiModelProperty(value = "工艺流程")
    private String technologicalProcess;

    @ApiModelProperty(value = "主要经营产品")
    private String businessProducts;

    @ApiModelProperty(value = "主要设备")
    private String majorEquipment;
    @ApiModelProperty(value = "主要原材料")
    private String mainRawMaterials;

    @ApiModelProperty(value = "企业污染、能耗重点情况说明")
    private String importantDescription;
    @ApiModelProperty(value = "企业年度用电（KW）")
    private String electricityYear;

    @ApiModelProperty(value = "企业年度用水（万吨）")
    private String waterYear;
    @ApiModelProperty(value = "企业年产值（千万）")
    private String annualOutputValue;
    @ApiModelProperty(value = "企业年度纳税额（百万）")
    private String taxAmountYear;
    @ApiModelProperty(value = "现有厂房类型(1:租赁 2:自建厂房 3:他建购买)")
    private String workShopType;
    @ApiModelProperty(value = "现有厂房类型描述")
    private String workShopTypeDesc;
    @ApiModelProperty(value = "企业现有员工数")
    private String peopleNum;
    @ApiModelProperty(value = "现有厂房面积m²")
    private String existingPlantArea;


    @ApiModelProperty(value = "关联客户")
    private List<Map> relCustomerList;

    @ApiModelProperty(value = "购房意向")
    private String tradeLevel;

    @ApiModelProperty(value = "状态")
    private String clueStatus;

    @ApiModelProperty(value = "客户手机号加星号")
    private String reCustomerMobile;

    @ApiModelProperty(value = "企业名称加星号")
    private String reCustomerName;

    @ApiModelProperty(value = "邮政编码")
    private String postCode;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "行政区编号")
    private String adminVisionCode;

    @ApiModelProperty(value = "行政区描述")
    private String adminVisionDesc;

    @ApiModelProperty(value = "录入时间")
    private String reportCreateTime;

    @ApiModelProperty(value = "报备人")
    private String reportUserName;

    @ApiModelProperty(value = "明源机会ID")
    private String intentionID;

    @ApiModelProperty(value = "是否推送明源")
    private String isToMy;

    @ApiModelProperty(value = "附件")
    private List<String> enclosures;

    @ApiModelProperty(value = "是否不可编辑")
    private String isEditOk = "0";

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "组织ID")
    private String orgId;

    @ApiModelProperty(value = "组织")
    private String orgName;

    @ApiModelProperty(value = "接待人ID")
    private String salesAttributionId;

    @ApiModelProperty(value = "接待人")
    private String salesAttributionName;

    @ApiModelProperty(value = "推送成交项目ID")
    private String orderProId;

    @ApiModelProperty(value = "推送成交项目")
    private String orderProName;

    @ApiModelProperty(value = "客户来源名称")
    private String sourceModeCode;

    @ApiModelProperty(value = "客户性质 0:企业 1:个人")
    private String customerType;
    private String customerTypeDesc;

    @ApiModelProperty(value = "法人")
    private String legalPerson;

    @ApiModelProperty(value = "法人联系电话")
    private String legalPersonPhone;

    @ApiModelProperty(value = "企业性质")
    private String enterpriseType ;

    @ApiModelProperty(value = "企业性质描述")
    private String enterpriseTypeDesc;

    @ApiModelProperty(value = "证件类型")
    private String customerCardType;

    @ApiModelProperty(value = "证件类型描述")
    private String customerCardTypeDesc;

    @ApiModelProperty(value = "证件号码")
    private String customerCardNum;

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

    @ApiModelProperty(value = "是否本人")
    private String isSelf;

    @ApiModelProperty(value = "客户类型")
    private String type;

    @ApiModelProperty(value = "团队ID")
    private String salesAttributionTeamId;

    @ApiModelProperty("扩展信息")
    private String extendInfo;
}
