package cn.visolink.system.pubilcPool.model.form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/9/7
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_customerdistributionrecords")
@ApiModel(value = "CustomerDistributionRecords对象", description = "分配记录表")
public class CustomerDistributionRecords implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "线索ID")
    @TableId(value = "ProjectClueId")
    private String projectClueId;

    @ApiModelProperty(value = "项目ID")
    @TableId(value = "projectId")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    @TableId(value = "ProjectName")
    private String projectName;

    @ApiModelProperty(value = "分配人员ID")
    @TableId(value = "ConfirmID")
    private String confirmID;

    @ApiModelProperty(value = "分配人员姓名")
    @TableId(value = "ConfirmPersonName")
    private String confirmPersonName;

    @ApiModelProperty(value = "分配日期")
    @TableId(value = "ConfirmTime")
    private Date confirmTime;

    @ApiModelProperty(value = "是否是最新")
    @TableId(value = "IsNew")
    private Integer isNew;

    @ApiModelProperty(value = "分配原因")
    @TableId(value = "Reason")
    private String reason;

    @ApiModelProperty(value = "发起入口")
    @TableId(value = "Entrance")
    private Integer entrance;

    @ApiModelProperty(value = "分配记录表id")
    @TableId(value = "RedistributionBatchId")
    private String redistributionBatchId;

    @ApiModelProperty(value = "渠道原归属时间")
    @TableId(value = "OldTokerAttributionTime")
    private Date oldTokerAttributionTime;

    @ApiModelProperty(value = "案场原归属时间")
    @TableId(value = "OldSalesAttributionTime")
    private Date oldSalesAttributionTime;

    @ApiModelProperty(value = "原销售人员")
    @TableId(value = "OldSalesName")
    private String oldSalesName;

    @ApiModelProperty(value = "原销售人员id")
    @TableId(value = "OldSalesId")
    private String oldSalesId;

    @ApiModelProperty(value = "原销售人员所属团队")
    @TableId(value = "OldSalesAttributionTeamName")
    private String oldSalesAttributionTeamName;

    @ApiModelProperty(value = "原销售人员所属团队id")
    @TableId(value = "OldSalesAttributionTeamId")
    private String oldSalesAttributionTeamId;

    @ApiModelProperty(value = "销售人员")
    @TableId(value = "SalesName")
    private String salesName;

    @ApiModelProperty(value = "销售人员id")
    @TableId(value = "SalesId")
    private String salesId;

    @ApiModelProperty(value = "销售人员团队")
    @TableId(value = "SalesAttributionTeamName")
    private String salesAttributionTeamName;

    @ApiModelProperty(value = "销售人员团队id")
    @TableId(value = "SalesAttributionTeamId")
    private String salesAttributionTeamId;

    @ApiModelProperty(value = "销售人员所属组ID")
    @TableId(value = "SalesAttributionGroupId")
    private String salesAttributionGroupId;

    @ApiModelProperty(value = "销售人员所属组名称")
    @TableId(value = "SalesAttributionGroupName")
    private String salesAttributionGroupName;

    @ApiModelProperty(value = "原销售人员所属组ID")
    @TableId(value = "OldSalesAttributionGroupId")
    private String oldSalesAttributionGroupId;

    @ApiModelProperty(value = "原销售人员所属组名称")
    @TableId(value = "OldSalesAttributionGroupName")
    private String oldSalesAttributionGroupName;

    @ApiModelProperty(value = "销售人员归属时间")
    @TableId(value = "SalesAttributionTime")
    private Date salesAttributionTime;

    @ApiModelProperty(value = "案场机会id")
    @TableId(value = "OpportunityClueId")
    private String opportunityClueId;

    @ApiModelProperty(value = "类别")
    @TableId(value = "Type")
    private Integer type;

    @ApiModelProperty(value = "备注")
    @TableId(value = "note")
    private String note;

    @ApiModelProperty(value = "0 修改置业顾问 1 修改报备人")
    @TableId(value = "ChildType")
    private Integer childType;

    @ApiModelProperty(value = "分配模式")
    private String distributionMode;

}
