package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ProjectCluesVO对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-27
 */
@Data
@ApiModel(value = "ProjectClues对象", description = "线索表")
public class ProjectCluesVO implements Serializable {

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

    @ApiModelProperty(value = "主数据ID")
    private String custId;

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

    @ApiModelProperty(value = "预计到访时间")
    private String expectedVisitDate;

    @ApiModelProperty(value = "预计到访人数")
    private Integer expectedVisitCount;

    @ApiModelProperty(value = "实际到访人数")
    private Integer actualVisitsCount;

    @ApiModelProperty(value = "是否关注 0:非关注 1：已关注")
    private Boolean isFollow;

    @ApiModelProperty(value = "客户标签")
    private String label;

    @ApiModelProperty(value = "案场标签")
    private String caseLabel;

    @ApiModelProperty(value = "渠道标签")
    private String channelLabel;

    @ApiModelProperty(value = "成交渠道 1 中介 2 自渠 3 自然到访 4 全民经纪人5 历史数据 ")
    private String sourceType;

    @ApiModelProperty(value = "成交渠道描述")
    private String sourceTypeDesc;

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

    @ApiModelProperty(value = "首访问卷填写日期")
    private String questionnaireDate;

    @ApiModelProperty(value = "首访时间")
    private String theFirstVisitDate;

    @ApiModelProperty(value = "分配类别")
    private Integer disType;

    @ApiModelProperty(value = "顾问是否确认")
    private Integer isConfirm;

    @ApiModelProperty(value = "分配人ID")
    private String disPerson;

    @ApiModelProperty(value = "分配人姓名")
    private String disPersonName;

    @ApiModelProperty(value = "分配时间")
    private String disTime;

    @ApiModelProperty(value = "接手时间")
    private String catchTime;

    @ApiModelProperty(value = "分接审核人id")
    private String auditUserId;

    @ApiModelProperty(value = "分接审核人")
    private String auditUserName;

    @ApiModelProperty(value = "分接审核状态")
    private Integer disStatus;

    @ApiModelProperty(value = "分接审核日期")
    private String disAuditDate;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "报备人员ID")
    private String reportUserId;

    @ApiModelProperty(value = "报备人姓名")
    private String reportUserName;

    @ApiModelProperty(value = "报备人团队ID")
    private String reportTeamId;

    @ApiModelProperty(value = "报备时间")
    private String reportCreateTime;

    @ApiModelProperty(value = "最新报备时间")
    private String lastRefreshReportExpireDate;

    @ApiModelProperty(value = "报备人团队名称")
    private String reportTeamName;

    @ApiModelProperty(value = "渠道归属人id")
    private String tokerAttributionId;

    @ApiModelProperty(value = "渠道归属人")
    private String tokerAttributionName;

    @ApiModelProperty(value = "渠道归属人组id")
    private String tokerAttributionGroupId;

    @ApiModelProperty(value = "渠道归属人组名")
    private String tokerAttributionGroupName;

    @ApiModelProperty(value = "渠道归属人队id")
    private String tokerAttributionTeamId;

    @ApiModelProperty(value = "渠道归属人队名")
    private String tokerAttributionTeamName;

    @ApiModelProperty(value = "渠道归属时间")
    private String tokerAttributionTime;

    @ApiModelProperty(value = "案场归属人id")
    private String salesAttributionId;

    @ApiModelProperty(value = "案场归属人名称")
    private String salesAttributionName;

    @ApiModelProperty(value = "案场归属人组id")
    private String salesAttributionGroupId;

    @ApiModelProperty(value = "案场归属人组名称")
    private String salesAttributionGroupName;

    @ApiModelProperty(value = "案场归属人队id")
    private String salesAttributionTeamId;

    @ApiModelProperty(value = "案场归属人队名称")
    private String salesAttributionTeamName;

    @ApiModelProperty(value = "案场归属时间")
    private String salesAttributionTime;

    @ApiModelProperty(value = "房间号")
    private String roomId;

    @ApiModelProperty(value = "房间名称")
    private String roomName;

    @ApiModelProperty(value = "认筹时间")
    private String bookingDate;

    @ApiModelProperty(value = "认筹金额")
    private String bookingMoney;

    @ApiModelProperty(value = "认购时间")
    private String subscribingDate;

    @ApiModelProperty(value = "认购金额")
    private String subscribingMoney;

    @ApiModelProperty(value = "签约金额")
    private Double contractAmount;

    @ApiModelProperty(value = "签约时间")
    private String contractDate;

    @ApiModelProperty(value = "付款方式大类")
    private String paymentMethodName;

    @ApiModelProperty(value = "付款方式小类")
    private String paymentMethodNameMin;

    @ApiModelProperty(value = "户型")
    private String builtPriceName;

    @ApiModelProperty(value = "面积")
    private Double builtArea;

    @ApiModelProperty(value = "单价")
    private Double builtPrice;

    @ApiModelProperty(value = "销售系统项目ID")
    private String saleProjectId;

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

    @ApiModelProperty(value = "city")
    private String city;

    @ApiModelProperty(value = "area")
    private String area;

    @ApiModelProperty(value = "是否报备逾期")
    private String isReportExpire;

    @ApiModelProperty(value = "实际报备逾期时间")
    private String reportExpireDate;

    @ApiModelProperty(value = "是否渠道到访逾期")
    private String isTokerVisitExpire;

    @ApiModelProperty(value = "实际渠道到访逾期时间")
    private String tokerVisitExpireDate;

    @ApiModelProperty(value = "是否渠道跟进逾期")
    private String isTokerFollowExpire;

    @ApiModelProperty(value = "实际渠道跟进逾期时间")
    private String tokerFollowExpireDate;

    @ApiModelProperty(value = "渠道最近跟近日期")
    private String tokerTheLatestFollowDate;

    @ApiModelProperty(value = "是否案场到访逾期")
    private String isSalesVisitExpire;

    @ApiModelProperty(value = "实际案场到访逾期时间")
    private String salesVisitExpireDate;

    @ApiModelProperty(value = "案场是否跟进逾期")
    private String isSalesFollowExpire;

    @ApiModelProperty(value = "实际案场跟进逾期时间")
    private String salesFollowExpireDate;

    @ApiModelProperty(value = "案场最近跟近日期")
    private String salesTheLatestFollowDate;

    @ApiModelProperty(value = "修改时间")
    private String editorTime;

    @ApiModelProperty(value = " 1 -中介  2- 自渠  3-自然到访")
    private String accountAttr;

    @ApiModelProperty(value = "是否删除 0 否  1是")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "创建人id")
    private String createUserId;

    @ApiModelProperty(value = "修改人ID")
    private String editUserId;

    @ApiModelProperty(value = "菜单按钮集合")
    private List buttonList;

    private Integer rownum;

    private String reportMobile;

    private String isRepurchase;

    @ApiModelProperty(value = "案场意向等级")
    private String tradeLevel;

    @ApiModelProperty(value = "置业顾问电话")
    private String salesAttReportMobile;

    @ApiModelProperty(value = "客户手机号全号")
    private String oldCustomerMobile;

    private String reportUserRole;

    private String activityName;//活动名称

    private String ruleEnableDate;//活动开启时间

    private String ruleEndDate;//活动结束时间

    private String failReason;//全民经纪人失效原因

    private String brokerageRule;//佣金比例

    private String ruleValidity;//全民经纪人有效规则

    private String reportSourceDesc;//报备来源描述

    private String reportSource;//报备来源大类

    private String childReportSource;//报备来源小类

    private String reportRefcommend;//报备来源信息

    private String performanceAttributorOldID;//原业绩归属人ID

    private int isSeniorBroker; //是否二级经纪人

    private String activeName;//活动名称

    private String accountName; //大客户名称

    private String accountMobile;//大客户手机号

    private String activeId;//活动id

    private String clueValidity;//客户有效性

    private String invalidReason;//作废原因

    @ApiModelProperty(value = "联名客户来源")
    private String sourceCust;

    @ApiModelProperty(value = "联名客户线索id")
    private String sourceClueId;

    @ApiModelProperty(value = "联名客户机会线索id")
    private String sourceOppClueId;

    private String acClueValidity;

    public String getSourceOppClueId() {
        return sourceOppClueId;
    }
}
