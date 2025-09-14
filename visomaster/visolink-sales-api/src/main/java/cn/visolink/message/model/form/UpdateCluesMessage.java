package cn.visolink.message.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "ProjectClues对象", description = "线索表")
public class UpdateCluesMessage extends Page {

    private String projectClueId;

    private String opportunityClueId;

    private String customerId;

    private String intentionId;

    private String basicCustomerId;

    private String custId;

    private String customerName;

    private String expectedVisitDate;

    private Integer sourceType;

    private String sourceTypeDesc;

    private String mainMediaGuId;

    private String mainMediaName;

    private String subMediaGuId;

    private String subMediaName;

    private String customerCardType;

    @ApiModelProperty(value = "客户证件类型描述")
    private String customerCardTypeDesc;

    @ApiModelProperty(value = "客户证件号码")
    private String customerCardNum;

    @ApiModelProperty(value = "线索状态 1：未到访 2：已到访 3：排小卡 4：排大卡 5：订房 6：认筹 7：认购 8：签约 9：作废")
    private Integer clueStatus;

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

    @ApiModelProperty(value = "Longitude")
    private String longitude;

    @ApiModelProperty(value = "Latitude")
    private String latitude;

    @ApiModelProperty(value = "是否报备逾期")
    private Integer isReportExpire;

    @ApiModelProperty(value = "实际报备逾期时间")
    private String reportExpireDate;

    @ApiModelProperty(value = "是否渠道到访逾期")
    private Integer isTokerVisitExpire;

    @ApiModelProperty(value = "实际渠道到访逾期时间")
    private String tokerVisitExpireDate;

    @ApiModelProperty(value = "是否渠道跟进逾期")
    private Integer isTokerFollowExpire;

    @ApiModelProperty(value = "实际渠道跟进逾期时间")
    private String tokerFollowExpireDate;

    @ApiModelProperty(value = "渠道最近跟近日期")
    private String tokerTheLatestFollowDate;

    @ApiModelProperty(value = "是否案场到访逾期")
    private Integer isSalesVisitExpire;

    @ApiModelProperty(value = "实际案场到访逾期时间")
    private String salesVisitExpireDate;

    @ApiModelProperty(value = "案场是否跟进逾期")
    private Integer isSalesFollowExpire;

    @ApiModelProperty(value = "实际案场跟进逾期时间")
    private String salesFollowExpireDate;

    @ApiModelProperty(value = "案场最近跟近日期")
    private String salesTheLatestFollowDate;

    @ApiModelProperty(value = "修改时间")
    private String editorTime;

    private String message;

    private String reportExpireWarningDate;

    private String tokerVisitExpireWarningDate;

    private String salesVisitExpireWarningDate;

    private String salesFollowExpireWarningDate;


}
