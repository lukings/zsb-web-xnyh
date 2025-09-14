package cn.visolink.system.channel.model.vo;

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
 * @Author:
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/9/7
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_redistribution_batch")
@ApiModel(value = "RedistributionBatch对象", description = "重分配批次")
public class RedistributionBatchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "分配原因")
    @TableId(value = "RedistributionType")
    private String redistributionType;

    @ApiModelProperty(value = "操作人")
    @TableId(value = "createUser")
    private String createUser;

    @ApiModelProperty(value = "操作时间")
    @TableId(value = "createTime")
    private String createTime;

    @ApiModelProperty(value = "涉及数量")
    @TableId(value = "countNumber")
    private String countNumber;

    @ApiModelProperty(value = "项目id")
    @TableId(value = "projectId")
    private String projectId;

    @ApiModelProperty(value = "备注")
    @TableId(value = "note")
    private String note;

    @ApiModelProperty(value = "重分配类型 （1.拓客台账 2.案场台账 3.app 4.公共池）")
    @TableId(value = "RedistributionGenre")
    private String redistributionGenre;

    @ApiModelProperty(value = "分配详情ID")
    private String xqDd;

    @ApiModelProperty(value = "线索ID")
    private String projectClueId;

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

    @ApiModelProperty(value = "类别")
    private Boolean type;

}
