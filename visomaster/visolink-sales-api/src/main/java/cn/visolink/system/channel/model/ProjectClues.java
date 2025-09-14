package cn.visolink.system.channel.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 线索表
 * </p>
 *
 * @author autoJob
 * @since 2019-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_project_clues")
@ApiModel(value = "ProjectClues对象", description = "线索表")
public class ProjectClues implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "线索id")
    @TableId(value = "ProjectClueId", type = IdType.AUTO)
    private String projectClueId;

    @ApiModelProperty(value = "销售系统顾客ID")
    @TableField("CustomerID")
    private String customerID;

    @ApiModelProperty(value = "销售系统机会ID")
    @TableField("IntentionID")
    private String intentionID;

    @ApiModelProperty(value = "案场客户表ID")
    @TableField("BasicCustomerId")
    private String basicCustomerId;

    @ApiModelProperty(value = "主数据ID")
    @TableField("Cust_ID")
    private String custId;

    @ApiModelProperty(value = "客储等级 1正常报备  1.5   巡展报备，外展报备  2     来访客户  2.5  小卡，大卡  3    认购  4    签约、退房、退订等")
    @TableField("CustomerLevel")
    private String customerLevel;

    @ApiModelProperty(value = "意向等级")
    @TableField("Level")
    private String level;

    @ApiModelProperty(value = "客户姓名")
    @TableField("CustomerName")
    private String customerName;

    @ApiModelProperty(value = "客户手机号")
    @TableField("CustomerMobile")
    private String customerMobile;

    @ApiModelProperty(value = "客户性别")
    @TableField("CustomerGender")
    private Integer customerGender;

    @ApiModelProperty(value = "客户年龄")
    @TableField("CustomerAge")
    private String customerAge;

    @ApiModelProperty(value = "预计到访时间")
    @TableField("ExpectedVisitDate")
    private Date expectedVisitDate;

    @ApiModelProperty(value = "预计到访人数")
    @TableField("ExpectedVisitCount")
    private Integer expectedVisitCount;

    @ApiModelProperty(value = "实际到访人数")
    @TableField("ActualVisitsCount")
    private Integer actualVisitsCount;

    @ApiModelProperty(value = "是否关注 0:非关注 1：已关注")
    @TableField("IsFollow")
    private Boolean isFollow;

    @ApiModelProperty(value = "客户标签")
    @TableField("Label")
    private String label;

    @ApiModelProperty(value = "成交渠道 1 中介 2 自渠 3 自然到访 4 全民经纪人5 历史数据 ")
    @TableField("SourceType")
    private Integer sourceType;

    @ApiModelProperty(value = "成交渠道描述")
    @TableField("SourceTypeDesc")
    private String sourceTypeDesc;

    @ApiModelProperty(value = "媒体大类ID")
    @TableField("MainMediaGUID")
    private String mainMediaGUID;

    @ApiModelProperty(value = "媒体大类描述")
    @TableField("MainMediaName")
    private String mainMediaName;

    @ApiModelProperty(value = "媒体子类ID")
    @TableField("SubMediaGUID")
    private String subMediaGUID;

    @ApiModelProperty(value = "媒体子类描述")
    @TableField("SubMediaName")
    private String subMediaName;

    @ApiModelProperty(value = "客户证件类型")
    @TableField("CustomerCardType")
    private String customerCardType;

    @ApiModelProperty(value = "客户证件类型描述")
    @TableField("CustomerCardTypeDesc")
    private String customerCardTypeDesc;

    @ApiModelProperty(value = "客户证件号码")
    @TableField("CustomerCardNum")
    private String customerCardNum;

    @ApiModelProperty(value = "线索状态 1：未到访 2：已到访 3：排小卡 4：排大卡 5：订房 6：认筹 7：认购 8：签约 9：作废")
    @TableField("ClueStatus")
    private Integer clueStatus;

    @ApiModelProperty(value = "首访问卷是否填写")
    @TableField("IsFirstTable")
    private Integer isFirstTable;

    @ApiModelProperty(value = "首访问卷填写日期")
    @TableField("QuestionnaireDate")
    private Date questionnaireDate;

    @ApiModelProperty(value = "首访时间")
    @TableField("TheFirstVisitDate")
    private Date theFirstVisitDate;

    @ApiModelProperty(value = "分配类别")
    @TableField("DisType")
    private Integer disType;

    @ApiModelProperty(value = "顾问是否确认")
    @TableField("IsConfirm")
    private Integer isConfirm;

    @ApiModelProperty(value = "分配人ID")
    @TableField("DisPerson")
    private String disPerson;

    @ApiModelProperty(value = "分配人姓名")
    @TableField("DisPersonName")
    private String disPersonName;

    @ApiModelProperty(value = "分配时间")
    @TableField("DisTime")
    private Date disTime;

    @ApiModelProperty(value = "接手时间")
    @TableField("CatchTime")
    private Date catchTime;

    @ApiModelProperty(value = "分接审核人id")
    @TableField("AuditUserId")
    private String auditUserId;

    @ApiModelProperty(value = "分接审核人")
    @TableField("AuditUserName")
    private String auditUserName;

    @ApiModelProperty(value = "分接审核状态")
    @TableField("DisStatus")
    private Integer disStatus;

    @ApiModelProperty(value = "分接审核日期")
    @TableField("DisAuditDate")
    private Date disAuditDate;

    @ApiModelProperty(value = "项目id")
    @TableField("projectId")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    @TableField("ProjectName")
    private String projectName;

    @ApiModelProperty(value = "报备人员ID")
    @TableField("ReportUserID")
    private String reportUserID;

    @ApiModelProperty(value = "报备人姓名")
    @TableField("ReportUserName")
    private String reportUserName;

    @ApiModelProperty(value = "报备人团队ID")
    @TableField("ReportTeamID")
    private String reportTeamID;

    @ApiModelProperty(value = "报备时间")
    @TableField("ReportCreateTime")
    private Date reportCreateTime;

    @ApiModelProperty(value = "报备人团队名称")
    @TableField("ReportTeamName")
    private String reportTeamName;

    @ApiModelProperty(value = "渠道归属人id")
    @TableField("TokerAttributionId")
    private String tokerAttributionId;

    @ApiModelProperty(value = "渠道归属人")
    @TableField("TokerAttributionName")
    private String tokerAttributionName;

    @ApiModelProperty(value = "渠道归属人组id")
    @TableField("TokerAttributionGroupId")
    private String tokerAttributionGroupId;

    @ApiModelProperty(value = "渠道归属人组名")
    @TableField("TokerAttributionGroupName")
    private String tokerAttributionGroupName;

    @ApiModelProperty(value = "渠道归属人队id")
    @TableField("TokerAttributionTeamId")
    private String tokerAttributionTeamId;

    @ApiModelProperty(value = "渠道归属人队名")
    @TableField("TokerAttributionTeamName")
    private String tokerAttributionTeamName;

    @ApiModelProperty(value = "渠道归属时间")
    @TableField("TokerAttributionTime")
    private Date tokerAttributionTime;

    @ApiModelProperty(value = "案场归属人id")
    @TableField("SalesAttributionId")
    private String salesAttributionId;

    @ApiModelProperty(value = "案场归属人名称")
    @TableField("SalesAttributionName")
    private String salesAttributionName;

    @ApiModelProperty(value = "案场归属人组id")
    @TableField("SalesAttributionGroupId")
    private String salesAttributionGroupId;

    @ApiModelProperty(value = "案场归属人组名称")
    @TableField("SalesAttributionGroupName")
    private String salesAttributionGroupName;

    @ApiModelProperty(value = "案场归属人队id")
    @TableField("SalesAttributionTeamId")
    private String salesAttributionTeamId;

    @ApiModelProperty(value = "案场归属人队名称")
    @TableField("SalesAttributionTeamName")
    private String salesAttributionTeamName;

    @ApiModelProperty(value = "案场归属时间")
    @TableField("SalesAttributionTime")
    private Date salesAttributionTime;

    @ApiModelProperty(value = "房间号")
    @TableField("RoomID")
    private String roomID;

    @ApiModelProperty(value = "房间名称")
    @TableField("RoomName")
    private String roomName;

    @ApiModelProperty(value = "认筹时间")
    @TableField("BookingDate")
    private Date bookingDate;

    @ApiModelProperty(value = "认筹金额")
    @TableField("BookingMoney")
    private String bookingMoney;

    @ApiModelProperty(value = "认购时间")
    @TableField("SubscribingDate")
    private Date subscribingDate;

    @ApiModelProperty(value = "认购金额")
    @TableField("SubscribingMoney")
    private String subscribingMoney;

    @ApiModelProperty(value = "签约金额")
    @TableField("ContractAmount")
    private Double contractAmount;

    @ApiModelProperty(value = "签约时间")
    @TableField("ContractDate")
    private Date contractDate;

    @ApiModelProperty(value = "付款方式大类")
    @TableField("PaymentMethodName")
    private String paymentMethodName;

    @ApiModelProperty(value = "付款方式小类")
    @TableField("PaymentMethodNameMin")
    private String paymentMethodNameMin;

    @ApiModelProperty(value = "户型")
    @TableField("BuiltPriceName")
    private String builtPriceName;

    @ApiModelProperty(value = "面积")
    @TableField("BuiltArea")
    private Double builtArea;

    @ApiModelProperty(value = "单价")
    @TableField("BuiltPrice")
    private Double builtPrice;

    @ApiModelProperty(value = "销售系统项目ID")
    @TableField("SaleProjectID")
    private String saleProjectID;

    @ApiModelProperty(value = "刷新数据标识")
    private String flag;

    @ApiModelProperty(value = "客户地址")
    @TableField("CustomerAddress")
    private String customerAddress;

    @ApiModelProperty(value = "备注")
    @TableField("Remarks")
    private String remarks;

    @ApiModelProperty(value = "Longitude")
    @TableField("Longitude")
    private String longitude;

    @ApiModelProperty(value = "Latitude")
    @TableField("Latitude")
    private String latitude;

    @ApiModelProperty(value = "city")
    private String city;

    @ApiModelProperty(value = "area")
    private String area;

    @ApiModelProperty(value = "是否报备逾期")
    @TableField("IsReportExpire")
    private Integer isReportExpire;

    @ApiModelProperty(value = "实际报备逾期时间")
    @TableField("ReportExpireDate")
    private Date reportExpireDate;

    @ApiModelProperty(value = "是否渠道到访逾期")
    @TableField("IsTokerVisitExpire")
    private Integer isTokerVisitExpire;

    @ApiModelProperty(value = "实际渠道到访逾期时间")
    @TableField("TokerVisitExpireDate")
    private Date tokerVisitExpireDate;

    @ApiModelProperty(value = "是否渠道跟进逾期")
    @TableField("IsTokerFollowExpire")
    private Integer isTokerFollowExpire;

    @ApiModelProperty(value = "实际渠道跟进逾期时间")
    @TableField("TokerFollowExpireDate")
    private Date tokerFollowExpireDate;

    @ApiModelProperty(value = "渠道最近跟近日期")
    @TableField("TokerTheLatestFollowDate")
    private Date tokerTheLatestFollowDate;

    @ApiModelProperty(value = "是否案场到访逾期")
    @TableField("IsSalesVisitExpire")
    private Integer isSalesVisitExpire;

    @ApiModelProperty(value = "实际案场到访逾期时间")
    @TableField("SalesVisitExpireDate")
    private Date salesVisitExpireDate;

    @ApiModelProperty(value = "案场是否跟进逾期")
    @TableField("IsSalesFollowExpire")
    private Integer isSalesFollowExpire;

    @ApiModelProperty(value = "实际案场跟进逾期时间")
    @TableField("SalesFollowExpireDate")
    private Date salesFollowExpireDate;

    @ApiModelProperty(value = "案场最近跟近日期")
    @TableField("SalesTheLatestFollowDate")
    private Date salesTheLatestFollowDate;

    @ApiModelProperty(value = "修改时间")
    @TableField("EditorTime")
    private Date editorTime;

    @ApiModelProperty(value = " 1 -中介  2- 自渠  3-自然到访")
    @TableField("AccountAttr")
    private Integer accountAttr;

    @ApiModelProperty(value = "是否删除 0 否  1是")
    @TableField("IsDel")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    @TableField("CreateTime")
    private Date createTime;

    @ApiModelProperty(value = "创建人id")
    @TableField("CreateUserId")
    private String createUserId;

    @ApiModelProperty(value = "修改人ID")
    @TableField("EditUserId")
    private String editUserId;
}
