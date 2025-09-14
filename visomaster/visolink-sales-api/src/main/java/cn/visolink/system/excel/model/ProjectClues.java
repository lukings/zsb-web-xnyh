package cn.visolink.system.excel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Data;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>
 * ProjectCluesForm对象
 * </p>
 *
 * @author autoJob
 * @since 2019-09-26
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "ProjectClues对象", description = "线索表")
public class ProjectClues {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "线索id")
    private String ProjectClueId;

    @ApiModelProperty(value = "销售系统顾客ID")
    private String CustomerID;

    @ApiModelProperty(value = "销售系统机会ID")
    private String IntentionID;

    @ApiModelProperty(value = "案场客户表ID")
    private String BasicCustomerId;

    @ApiModelProperty(value = "主数据ID")
    private String custId;

    @ApiModelProperty(value = "客储等级 1正常报备  1.5   巡展报备，外展报备  2     来访客户  2.5  小卡，大卡  3    认购  4    签约、退房、退订等")
    private String CustomerLevel;

    @ApiModelProperty(value = "意向等级")
    private String Level;

    @ApiModelProperty(value = "购房意向")
    private String TradeLevel;

    @ApiModelProperty(value = "客户姓名")
    private String CustomerName;

    @ApiModelProperty(value = "客户手机号")
    private String CustomerMobile;

    @ApiModelProperty(value = "客户性别")
    private Integer CustomerGender;

    @ApiModelProperty(value = "客户年龄")
    private String CustomerAge;

    @ApiModelProperty(value = "预计到访时间")
    private String ExpectedVisitDate;

    @ApiModelProperty(value = "预计到访人数")
    private Integer ExpectedVisitCount;

    @ApiModelProperty(value = "实际到访人数")
    private Integer ActualVisitsCount;

    @ApiModelProperty(value = "是否关注 0:非关注 1：已关注")
    private Boolean IsFollow;

    @ApiModelProperty(value = "客户标签")
    private String Label;

    @ApiModelProperty(value = "成交渠道 1 中介 2 自渠 3 自然到访 4 私营媒介5 历史数据 ")
    private Integer SourceType;

    @ApiModelProperty(value = "成交渠道描述")
    private String SourceTypeDesc;

    @ApiModelProperty(value = "媒体大类ID")
    private String MainMediaGUID;

    @ApiModelProperty(value = "媒体大类描述")
    private String MainMediaName;

    @ApiModelProperty(value = "媒体子类ID")
    private String SubMediaGUID;

    @ApiModelProperty(value = "媒体子类描述")
    private String SubMediaName;

    @ApiModelProperty(value = "业绩归属人")
    private String PerformanceAttributor;

    @ApiModelProperty(value = "原成交渠道 1 中介 2 自渠 3 自然到访 4 全民经纪人5 历史数据 ")
    private Integer SourceTypeOld;

    @ApiModelProperty(value = "原成交渠道描述")
    private String SourceTypeOldDesc;

    @ApiModelProperty(value = "原业绩归属人")
    private String PerformanceAttributorOld;

    @ApiModelProperty(value = "客户证件类型")
    private String CustomerCardType;

    @ApiModelProperty(value = "客户证件类型描述")
    private String CustomerCardTypeDesc;

    @ApiModelProperty(value = "客户证件号码")
    private String CustomerCardNum;

    @ApiModelProperty(value = "线索状态 1：未到访 2：已到访 3：排小卡 4：排大卡 5：订房 6：认筹 7：认购 8：签约 9：作废")
    private Integer ClueStatus;

    @ApiModelProperty(value = "首访问卷是否填写")
    private Integer IsFirstTable;

    @ApiModelProperty(value = "首访问卷填写日期")
    private String QuestionnaireDate;

    @ApiModelProperty(value = "首访时间")
    private String TheFirstVisitDate;

    @ApiModelProperty(value = "分配类别")
    private Integer DisType;

    @ApiModelProperty(value = "顾问是否确认")
    private Integer IsConfirm;

    @ApiModelProperty(value = "分配人ID")
    private String DisPerson;

    @ApiModelProperty(value = "分配人姓名")
    private String DisPersonName;

    @ApiModelProperty(value = "分配时间")
    private String DisTime;

    @ApiModelProperty(value = "接手时间")
    private String CatchTime;

    @ApiModelProperty(value = "分接审核人id")
    private String AuditUserId;

    @ApiModelProperty(value = "分接审核人")
    private String AuditUserName;

    @ApiModelProperty(value = "分接审核状态")
    private Integer DisStatus;

    @ApiModelProperty(value = "分接审核日期")
    private String DisAuditDate;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String ProjectName;

    @ApiModelProperty(value = "报备人员ID")
    private String ReportUserID;

    @ApiModelProperty(value = "报备人姓名")
    private String ReportUserName;

    @ApiModelProperty(value = "报备人团队ID")
    private String ReportTeamID;

    @ApiModelProperty(value = "报备时间")
    private String ReportCreateTime;

    @ApiModelProperty(value = "报备人团队名称")
    private String ReportTeamName;

    @ApiModelProperty(value = "渠道归属人id")
    private String TokerAttributionId;

    @ApiModelProperty(value = "渠道归属人")
    private String TokerAttributionName;

    @ApiModelProperty(value = "渠道归属人组id")
    private String TokerAttributionGroupId;

    @ApiModelProperty(value = "渠道归属人组名")
    private String TokerAttributionGroupName;

    @ApiModelProperty(value = "渠道归属人队id")
    private String TokerAttributionTeamId;

    @ApiModelProperty(value = "渠道归属人队名")
    private String TokerAttributionTeamName;

    @ApiModelProperty(value = "渠道归属时间")
    private String TokerAttributionTime;

    @ApiModelProperty(value = "案场归属人id")
    private String SalesAttributionId;

    @ApiModelProperty(value = "案场归属人名称")
    private String SalesAttributionName;

    @ApiModelProperty(value = "案场归属人组id")
    private String SalesAttributionGroupId;

    @ApiModelProperty(value = "案场归属人组名称")
    private String SalesAttributionGroupName;

    @ApiModelProperty(value = "案场归属人队id")
    private String SalesAttributionTeamId;

    @ApiModelProperty(value = "案场归属人队名称")
    private String SalesAttributionTeamName;

    @ApiModelProperty(value = "案场归属时间")
    private String SalesAttributionTime;

    @ApiModelProperty(value = "房间号")
    private String RoomID;

    @ApiModelProperty(value = "房间名称")
    private String RoomName;

    @ApiModelProperty(value = "认筹时间")
    private String BookingDate;

    @ApiModelProperty(value = "认筹金额")
    private String BookingMoney;

    @ApiModelProperty(value = "认购时间")
    private String SubscribingDate;

    @ApiModelProperty(value = "认购金额")
    private String SubscribingMoney;

    @ApiModelProperty(value = "签约金额")
    private Double ContractAmount;

    @ApiModelProperty(value = "签约时间")
    private String ContractDate;

    @ApiModelProperty(value = "付款方式大类")
    private String PaymentMethodName;

    @ApiModelProperty(value = "付款方式小类")
    private String PaymentMethodNameMin;

    @ApiModelProperty(value = "户型")
    private String BuiltPriceName;

    @ApiModelProperty(value = "面积")
    private Double BuiltArea;

    @ApiModelProperty(value = "单价")
    private Double BuiltPrice;

    @ApiModelProperty(value = "销售系统项目ID")
    private String SaleProjectID;

    @ApiModelProperty(value = "刷新数据标识")
    private String flag;

    @ApiModelProperty(value = "客户地址")
    private String CustomerAddress;

    @ApiModelProperty(value = "备注")
    private String Remarks;

    @ApiModelProperty(value = "Longitude")
    private String Longitude;

    @ApiModelProperty(value = "Latitude")
    private String Latitude;

    @ApiModelProperty(value = "city")
    private String city;

    @ApiModelProperty(value = "area")
    private String area;

    @ApiModelProperty(value = "是否报备逾期")
    private Integer IsReportExpire;

    @ApiModelProperty(value = "实际报备逾期时间")
    private String ReportExpireDate;

    @ApiModelProperty(value = "实际报备预警时间")
    private String ReportExpireWarningDate;

    @ApiModelProperty(value = "是否渠道到访逾期")
    private Integer IsTokerVisitExpire;

    @ApiModelProperty(value = "实际渠道到访逾期时间")
    private String TokerVisitExpireDate;

    @ApiModelProperty(value = "实际渠道到访预警时间")
    private String TokerVisitExpireWarningDate;

    @ApiModelProperty(value = "是否渠道跟进逾期")
    private Integer IsTokerFollowExpire;

    @ApiModelProperty(value = "实际渠道跟进逾期时间")
    private String TokerFollowExpireDate;

    @ApiModelProperty(value = "渠道最近跟进日期")
    private String TokerTheLatestFollowDate;

    @ApiModelProperty(value = "是否案场到访逾期")
    private Integer IsSalesVisitExpire;

    @ApiModelProperty(value = "实际案场到访逾期时间")
    private String SalesVisitExpireDate;

    @ApiModelProperty(value = "实际案场到访预警时间")
    private String SalesVisitExpireWarningDate;

    @ApiModelProperty(value = "案场是否跟进逾期")
    private Integer IsSalesFollowExpire;

    @ApiModelProperty(value = "实际案场跟进逾期时间")
    private String SalesFollowExpireDate;

    @ApiModelProperty(value = "实际案场跟进预警时间")
    private String SalesFollowExpireWarningDate;

    @ApiModelProperty(value = "案场最近跟进日期")
    private String SalesTheLatestFollowDate;

    @ApiModelProperty(value = "签约逾期")
    private Integer ContractExpire;

    @ApiModelProperty(value = "签约逾期时间")
    private String ContractExpireDate;

    @ApiModelProperty(value = "修改时间")
    private String EditorTime;

    @ApiModelProperty(value = "1-中介  2- 自渠  3-自然到访")
    private Integer AccountAttr;

    @ApiModelProperty(value = "是否删除 0 否  1是")
    private Boolean IsDel;

    @ApiModelProperty(value = "创建时间")
    private String CreateTime;

    @ApiModelProperty(value = "创建人id")
    private String CreateUserId;

    @ApiModelProperty(value = "修改人ID")
    private String EditUserId;

    @ApiModelProperty(value = "意向业态")
    private String IntentionBusiness;

    @ApiModelProperty(value = "到访时和首访时间相同，以后复访更新此字段")
    private String VisitDate;

    @ApiModelProperty(value = "是否重复认购/联名客户")
    private Boolean IsRepurchase;


}
