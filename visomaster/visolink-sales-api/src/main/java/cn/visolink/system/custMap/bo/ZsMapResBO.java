package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ZsMapResBO {

  @ApiModelProperty("客户姓名")
  private String customerName;

  @ApiModelProperty("经度")
  private String longitude;

  @ApiModelProperty("纬度")
  private String latitude;

  @ApiModelProperty("客户类型")
  private String type;

  @ApiModelProperty("原始客户名称")
  private String oldCustomerName;

  @ApiModelProperty("业务ID")
  private String businessId;

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

  @ApiModelProperty("厂房类型描述")
  private String plantTypeDesc;

  @ApiModelProperty("报备人招商团队")
  private String reportTeamName;

  @ApiModelProperty("首访时间")
  private Date theFirstVisitDate;

  @ApiModelProperty("复访时间")
  private Date visitDate;

  @ApiModelProperty("是否满足三个一")
  private Integer isThreeOnes;

  @ApiModelProperty("意向类型描述")
  private String intentionTypeDesc;

  @ApiModelProperty("意向面积描述")
  private String intentionalAreaDesc;

  @ApiModelProperty("意向单价描述")
  private String acceptPriceDesc;

  @ApiModelProperty("意向楼层描述")
  private String intentionalFloorDesc;

  @ApiModelProperty("客户等级")
  private String customerLevel;

  @ApiModelProperty("联系方式")
  private String customerMobile;


  @ApiModelProperty("原始联系方式")
  private String oldCustomerMobile;

  @ApiModelProperty("联系人")
  private String contacts;

  @ApiModelProperty("公客池类型")
  private String poolType;

  @ApiModelProperty("去重集合")
  private List<ZsMapResBO> zsMapResBOList;

  @ApiModelProperty("集合数量")
  private Integer size = 1;

  @ApiModelProperty("公客池id")
  private String poolId;
  
  @ApiModelProperty("客户来源")
  private String sourceMode;

  @ApiModelProperty("客户类型")
  private String customerType;//1-下发客户3、新增客户


  @Override
  public String toString() {
    return customerName;
  }


  // 新增字段
  @ApiModelProperty("地图类型")
  private String mapType;    // 地图类型：1-客户地图 2-拓客地图
  // 新增字段
  @ApiModelProperty("数据范围")
  private String dataRange;  // 数据范围：1-项目 2-区域 3-集团

  /**
   * 序号
   */
  private String serialNumber;


  /**
   * 联系人方式
   */
  private String contactWay;

  /**
   * 企业地址
   */
  private String companyAddress;

  /**
   * 详细地址
   */
  private String detailAddress;

  /**
   * 行业分类
   */
  private String industryCategory;

  /**
   * 二级分类
   */
  private String secondaryCategory;

  /**
   * 主营产品
   */
  private String mainProduct;

  /**
   * 意向项目
   */
  private String intentionProject;

  /**
   * 录入时间
   */
  private Date entryTime;

  /**
   * 录入人
   */
  private String entryPerson;

  /**
   * 录入人身份
   */
  private String entryPersonIdentity;

  /**
   * 是否园区
   */
  private String isPark;

  /**
   * 园区地址
   */
  private String parkAddress;

  /**
   * 园区层数
   */
  private String parkFloors;

  /**
   * 园区名称
   */
  private String parkName;

  /**
   * 客户状态
   */
  private String clueStatus;

  /**
   * 客户状态中文
   */
  private String clueStatusCh;

  /**
   * 是否捞取客户
   */
  private String isTaoGuest;


  /**
   * 客户状态
   */
  private String reason;

  private String label;
  private String level;
  private String tokerTheLatestFollowDate;
  private String disTime;
  private String salesFollowExpireDate;
  private String salesAttributionId;
  private String salesAttributionName;

  private String salesAttributionTeamId;
  private String salesAttributionTeamName;
  private Date salesAttributionTime;


  private String projectId;
  private String projectClueId;

  private String isReferralOk;
  private String wqtRefereeTime;
  private String wqtRefereeName;
  private String salesTheLatestFollowDate;
  private String isEditOk;
  private String isSelf;

  private String permissionLevel;

}
