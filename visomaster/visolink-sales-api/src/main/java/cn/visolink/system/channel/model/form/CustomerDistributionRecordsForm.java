package cn.visolink.system.channel.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * CustomerDistributionRecords对象
 * </p>
 *
 * @author 吴要光
 * @since 2019-09-02
 */

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "CustomerDistributionRecords对象", description = "重分配表")
public class CustomerDistributionRecordsForm extends Page {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "线索ID")
    private String projectClueId;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "分配人员ID")
    private String confirmId;

    @ApiModelProperty(value = "分配人员姓名")
    private String confirmPersonName;

    @ApiModelProperty(value = "分配日期")
    private Date confirmTime;

    @ApiModelProperty(value = "是否是最新")
    private Boolean isNew;

    @ApiModelProperty(value = "分配原因")
    private String reason;

    @ApiModelProperty(value = "发起入口")
    private Integer entrance;

    @ApiModelProperty(value = "分配记录表id")
    private String redistributionBatchId;

    @ApiModelProperty(value = "渠道原归属时间")
    private Date oldTokerAttributionTime;

    @ApiModelProperty(value = "案场原归属时间")
    private Date oldSalesAttributionTime;

    @ApiModelProperty(value = "原销售人员")
    private String oldSalesName;

    @ApiModelProperty(value = "原销售人员id")
    private String oldSalesId;

    @ApiModelProperty(value = "原销售人员所属团队")
    private String oldSalesAttributionTeamName;

    @ApiModelProperty(value = "原销售人员所属团队id")
    private String oldSalesAttributionTeamId;

    @ApiModelProperty(value = "销售人员")
    private String salesName;

    @ApiModelProperty(value = "销售人员id")
    private String salesId;

    @ApiModelProperty(value = "销售人员团队")
    private String salesAttributionTeamName;

    @ApiModelProperty(value = "销售人员团队id")
    private String salesAttributionTeamId;

    @ApiModelProperty(value = "销售人员所属组ID")
    private String salesAttributionGroupId;

    @ApiModelProperty(value = "销售人员所属组名称")
    private String salesAttributionGroupName;

    private String oldSalesAttributionGroupId;

    @ApiModelProperty(value = "原销售人员所属组名称")
    private String oldSalesAttributionGroupName;

    @ApiModelProperty(value = "销售人员归属时间")
    private Date salesAttributionTime;

    @ApiModelProperty(value = "案场机会id")
    private String opportunityClueId;

    @ApiModelProperty(value = "申请顾问id")
    private String applySalesId;

    @ApiModelProperty(value = "申请时间")
    private Date applyDatetime;

    @ApiModelProperty(value = "审核状态")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核时间")
    private Date auditDatetime;

    @ApiModelProperty(value = "审核人")
    private String auditId;

    @ApiModelProperty(value = "驳回原因")
    private String rejected;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "类别")
    private Boolean type;
}
