package cn.visolink.system.pubilcPool.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2021/6/21
 */
@Data
@ApiModel(value = "公共池对象", description = "公共池对象")
public class CasePublicPoolVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String addType;

    private String operationTime;

    private String theFirstVisitDate;

    private String reportUserName;

    private String reportTime;

    private String saleId;

    private String saleName;

    private String expireTag;

    private String clueStatus;

    private String projectId;

    private String projectName;

    private String projectClueId;

    private String salesAttributionName;

    private String salesAttributionId;

    private String customerName;

    private String customerMobile;

    private String customerGender;

    private String customerAge;

    private String sourceType;

    private String sourceTypeDesc;

    private String mainMediaGuId;

    private String mainMediaName;

    private String subMediaGuId;

    private String subMediaName;

    private String customerLevel;

    private String level;

    private String tradeLevel;

    private String label;

    private String latelyFollowUpPerson;

    private String latelyFollowUpMobile;

    private String latelyFollowUpTime;

    private String latelyFollowUpContent;

    private String browseNumber;

    private String browseTime;

    private String browseDesc;

    private String addReasonType;

    private String addReasonDesc;

    private String addNumber;

    private String activateReasonType;

    private String activateReasonDesc;

    private String basicCustomerId;

    private String reportUserId;

    private String intentionBusiness;

    private String opportunityClueId;

    private String dataCompleteRate;

    private String dataCompleteAttachRate;

    private String salesAttributionTeamId;

    private String salesAttributionTeamName;

    private String salesTheLatestFollowDate;

}
