package cn.visolink.system.channel.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "excel文件导出", description = "")
public class ExcelForm  extends Page {

    private String search;

    private String isAll;

    private String reportUserRole;

    private String startTime;

    private String startTimeTwo;

    private String pageNum;
    private String pageIndex;

    private String pageSize;

    private String projectId;

    private String acOrQd;

    private String numbers;

    private String clueStatus;

    private String customerName;

    private String customerMobile;

    private String reportUserName;

    private String level;

    private String sourceType;

    private String reportTime;

    private String date1;

    private String date2;

    private String salesAttributionName;

    private String isRepurchase;

    private String FeedBackUserName;

    private String FeedBackUserMobile;

    private String Status;

    private String FeedBackUserRole;

    private String beginTime;

    private String endTime;

    private String endTimeTwo;

    private List<String> brokerList;

    private List<String> relatedList;

    private List<String> isReportList;

    private List<String> projectList;

    private List<String> mainMediaList;

    private List<String> FeedbackModuleList;

    private List<String> clueValidityList;

    private String searchName;

    private String salesName;

    private String salesMobile;

    private String brokerStr;

    private String relatedStr;

    private String isReportStr;

    private String tradeLevel;

    private String mainMediaStr;

    private List<String> tradeLevelList;

    private String userName;//当前登录账号

    private String userId;//当前登录账号ID

    private String isSecondBroker;//是否二级经纪人

    @ApiModelProperty(value = "关联客户姓名")
    private String glName;

    @ApiModelProperty(value = "关联客户电话")
    private String glPhone;

    private String companycode;//公司编码

    private String sourceMode;

    private String customerLevel;

    private List<String> opportunityList;

    private List<Map> fileds;

    private String followStartTime;

    private String followEndTime;

    private String ownerUserId;

    private List<String> relaCustomerMainIds;

    /**
     * 过保开始时间
     * */
    private String expireStartTime;

    /**
     * 过保结束时间
     * */
    private String expireEndTime;

    @ApiModelProperty(value = "到访数")
    private String visitCount;
    private String visitStartCount;
    private String visitEndCount;

    @ApiModelProperty(value = "三个一前拜访数")
    private String threeOnesBeforeCount;
    private String threeOnesBeforeStartCount;
    private String threeOnesBeforeEndCount;

    @ApiModelProperty(value = "三个一后拜访数")
    private String threeOnesAfterCount;
    private String threeOnesAfterStartCount;
    private String threeOnesAfterEndCount;

    @ApiModelProperty(value = "拜访数")
    private String comeVisitCount;
    private String comeVisitStartCount;
    private String comeVisitEndCount;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    private List<String> orgIds;

    private String tagLabel;

    private Integer type = 0;

    @ApiModelProperty(value = "过保时间")
    private String salesFollowExpireDate;

    @ApiModelProperty(value = "预警时间")
    private String salesFollowExpireWarningDate;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty(value = "附件")
    private List<Map> fileList;

    @ApiModelProperty(value = "最大跟进次数")
    private String maxFollowUp;

}
