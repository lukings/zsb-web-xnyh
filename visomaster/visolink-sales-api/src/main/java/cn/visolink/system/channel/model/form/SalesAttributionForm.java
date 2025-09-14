package cn.visolink.system.channel.model.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/9/9
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "SalesAttributionForm对象", description = "分配置业顾问数据映射类")
public class SalesAttributionForm {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "案场归属人id")
    private String salesAttributionId;

    @ApiModelProperty(value = "案场归属人名称")
    private String salesAttributionName;

    @ApiModelProperty(value = "案场归属人组id ")
    private String salesAttributionGroupId;

    @ApiModelProperty(value = "案场归属人组名称")
    private String salesAttributionGroupName;

    @ApiModelProperty(value = "案场归属人队id")
    private String salesAttributionTeamId;

    @ApiModelProperty(value = "案场归属人队名称")
    private String salesAttributionTeamName;

    @ApiModelProperty(value = "分配人ID")
    private String disPerson;

    @ApiModelProperty(value = "分配人姓名")
    private String disPersonName;

    @ApiModelProperty(value = "线索id")
    private String projectClueId;

    @ApiModelProperty(value = "机会ID")
    private String opportunityClueId;

    @ApiModelProperty(value = "分组类型")
    private String groupType;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "公共池id")
    private String publicPoolId;

    @ApiModelProperty(value = "层级")
    private String levels;

    @ApiModelProperty(value = "岗位编码")
    private String jobCode;

    @ApiModelProperty(value = "父级组织id")
    private String parentId;

    @ApiModelProperty(value = "到访逾期时间")
    private String salesVisitExpireDate;

    @ApiModelProperty(value = "到访预警时间")
    private String salesVisitExpireWarningDate;

    @ApiModelProperty(value = "实际案场跟进逾期时间")
    private String salesFollowExpireDate;

    @ApiModelProperty(value = "实际案场跟进预警时间")
    private String salesFollowExpireWarningDate;

    @ApiModelProperty(value = "线索集合")
    private List<String> projectClueList;

    @ApiModelProperty(value = "原线索集合")
    private List<String> oldProjectClueList;

    private List<String> allSelectList;

    private List<String> allSelectGwList;

    private Boolean isAllSelect;

    @ApiModelProperty(value = "搜索条件（姓名/手机号/置业顾问）置业顾问精确查询")
    private String searchName;

    @ApiModelProperty(value = "客户手机号")
    private String customerMobile;

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "置业顾问")
    private String adviserName;

    @ApiModelProperty(value = "客户状态")
    private List<String> clueStatusList;

    @ApiModelProperty(value = "意向级别")
    private List<String> levelList;

    @ApiModelProperty(value = "sql拼接")
    private String sqlStr;

    @ApiModelProperty(value = "首访问卷信息完成率")
    private String dataCompleteRate;

    @ApiModelProperty(value = "首访问卷通用信息完成率")
    private String dataCompleteAttachRate;

    @ApiModelProperty(value = "首访时间")
    private String startSendTime;

    @ApiModelProperty(value = "首访时间")
    private String endSendTime;

    @ApiModelProperty(value = "首访时间sql")
    private String dateSql;

    @ApiModelProperty(value = "进入公共池开始时间")
    private String publicStartTime;

    @ApiModelProperty(value = "进入公共池结束时间")
    private String publicEndTime;

    @ApiModelProperty(value = "报备来源集合")
    private List<String> reportSourceList;

    @ApiModelProperty(value = "是否分配置业顾问集合")
    private List<String> isAllocationList;

    @ApiModelProperty(value = "是否分配置业顾问")
    private String isAllocation;

    private String labelStr;

    private String overdueType;

    @ApiModelProperty(value = "来访次数")
    private String visitNumber;

    private List<String> isOverdueList;

    private String isActivation;

    private List<String> isActivationList;

    private String type;

    private String userId;

    private String employeeName;

    private String poolType;

    @ApiModelProperty(value = "客户重分配信息集合")
    private List<CustomerDistributionInfo> customerDistributionList;

    @ApiModelProperty(value = "ID集合")
    private List<String> ids;

}
