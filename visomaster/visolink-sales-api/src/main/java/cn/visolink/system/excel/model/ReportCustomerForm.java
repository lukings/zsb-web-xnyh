package cn.visolink.system.excel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.05
 */

@Data
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value="报备客户信息", description="客户表")
public class ReportCustomerForm {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String employeeName;

    @ApiModelProperty(value = "岗位ID")
    private String jobId;

    @ApiModelProperty(value = "岗位名")
    private String jobName;

    @ApiModelProperty(value = "组织ID")
    private String orgId;

    @ApiModelProperty(value = "组织名")
    private String orgName;

    @ApiModelProperty(value = "企业名称")
    private String customerName;

    @ApiModelProperty(value = "联系人手机号")
    private String customerMobile;

    @ApiModelProperty(value = "联系人手机号多个")
    private String customerMobileS;

    @ApiModelProperty(value = "性别")
    private String customerGender = "1";

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "客户证件号")
    private String cardNum;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "预计到访时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expectedVisitDate;

    @ApiModelProperty(value= "媒体大类ID")
    private String mainMediaGuId;

    @ApiModelProperty(value = "媒体大类")
    private String mainMediaName;

    @ApiModelProperty(value = "媒体小类ID")
    private String subMediaGuId;

    @ApiModelProperty(value = "媒体小类")
    private String subMediaName;

    @ApiModelProperty(value = "意向等级")
    private String tradeLevel;

    @ApiModelProperty(value = "客户UUID")
    private String customerUuid;

    @ApiModelProperty(value = "线索UUID")
    private String projectClueUuid;

    @ApiModelProperty(value = "线索ID")
    private String projectClueId;

    @ApiModelProperty(value = "实际报备失效时间 规则ReportExpireDays")
    private String reportExpireDate;

    @ApiModelProperty(value = "渠道到访预期时间 规则VisitExpireDays")
    private String tokerVisitExpireDate;

    @ApiModelProperty(value = "意向业态")
    private String intentionBusiness;

    @ApiModelProperty(value = "跟进方式")
    private String followUpWay;

    @ApiModelProperty(value="报备预警")
    private String reportExpireWarningDate;

    @ApiModelProperty(value="跟进预警")
    private String salesFollowExpireWarningDate;

    @ApiModelProperty(value="跟进逾期")
    private String salesFollowExpireDate;

    @ApiModelProperty(value="手机区号")
    private String areaCode;

    @ApiModelProperty(value = "跟进描述")
    private String followUpDetail;

    private String createDate;

    private String communicationContent;

    private String flag;
    @ApiModelProperty(value = "职位描述")
    private String position;
    @ApiModelProperty(value = "客户来源")
    private String sourceMode;
    @ApiModelProperty(value = "客户来源Id")
    private String sourceModeId;
    @ApiModelProperty(value = "是否园区（1：是 0：否）")
    private String isPark;
    @ApiModelProperty(value = "园区名称")
    private String parkName;
    @ApiModelProperty(value = "园区地址")
    private String parkAddress;
    @ApiModelProperty(value = "标签")
    private String label;
    @ApiModelProperty(value = "客户地址")
    private String customerAddress;
    @ApiModelProperty(value = "所属行业")
    private String belongIndustrise;
    @ApiModelProperty(value = "所属行业描述")
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
    @ApiModelProperty(value = "客户性质 0:企业、1:个人")
    private String customerType;
    @ApiModelProperty(value = "附件路径")
    private List<String> enclosures;
    @ApiModelProperty(value = "报备人角色（1：项目招商专员 2：区域招商专员）")
    private String reportUserRole;
    @ApiModelProperty(value = "机会ID")
    private String opportunityClueId;
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
    private String zyreasonDesc;
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
    @ApiModelProperty(value = "标注分类")
    private String dimensionType;

    @ApiModelProperty(value = "厂房类型")
    private String plantTypeDesc;
    @ApiModelProperty(value = "租售类型")
    private String rentAndSaleTypeDesc;

    @ApiModelProperty(value = "意向单价")
    private String intentionalPrice;
    @ApiModelProperty(value = "客户来源code")
    private String sourceModeCode;

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
    @ApiModelProperty(value = "年产值")
    private String annualOutputValue;

    @ApiModelProperty(value = "园区所在省份")
    private String parkProvince;
    @ApiModelProperty(value = "园区所在城市")
    private String parkCity;
    @ApiModelProperty(value = "园区所在区")
    private String parkCounty;
    @ApiModelProperty(value = "园区所在街道")
    private String parkStreet;
    @ApiModelProperty(value = "园区经度")
    private String ParkLongitude;
    @ApiModelProperty(value = "园区纬度")
    private String ParkLatitude;

    @ApiModelProperty(value = "页数")
    private String pageIndex;
    @ApiModelProperty(value = "条数")
    private String pageSize;

    @ApiModelProperty(value = "详细地址")
    private String detailedAddress;
    @ApiModelProperty(value = "省")
    private String province;
    @ApiModelProperty(value = "市")
    private String city;
    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "区")
    private String status;

    @ApiModelProperty(value = "客户状态 0：走访1：报备 2：到访 3：交房 4：相似客户审批中  7：认购 8：签约 9：丢失 10：作废 11：分配 12：相似客户审批驳回")
    private String clueStatus;

    @ApiModelProperty(value = "分配时间")
    private String disTime;

    @ApiModelProperty(value = "分配人id")
    private String disPerson;

    @ApiModelProperty(value = "分配人名称")
    private String disPersonName;

    @ApiModelProperty(value = "分配客户id")
    private String disOpportunityClueId;

    @ApiModelProperty(value = "动态隐藏的客户名称")
    private String diyHideCustomerName;

    @ApiModelProperty(value = "线索客户等级A,B,C,D,E")
    private String CustomerLevel;

    @ApiModelProperty(value = "外呼跟进记录")
    private List<FollowUpRecordForm> callFollowUp;



    @ApiModelProperty(value = "行号")
    private int rowNum;

    @ApiModelProperty(value = "API返回的客户地址")
    private String apiCustomerAddress;
    @ApiModelProperty(value = "API返回的经度")
    private String apiLongitude;
    @ApiModelProperty(value = "API返回的纬度")
    private String apiLatitude;

    @ApiModelProperty(value = "业务数据ID")
    private String businessId;
}
