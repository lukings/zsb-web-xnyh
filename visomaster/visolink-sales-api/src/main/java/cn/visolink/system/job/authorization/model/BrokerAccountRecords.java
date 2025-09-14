package cn.visolink.system.job.authorization.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/10/16
 */
@Data
public class BrokerAccountRecords implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "二级经纪人ID")
    private String brokerId;

    @ApiModelProperty(value = "二级经纪人Openid")
    private String brokerOpenId;

    @ApiModelProperty(value = "二级经纪人姓名")
    private String brokerName;

    @ApiModelProperty(value = "二级经纪人手机号")
    private String brokerMobile;

    @ApiModelProperty(value = "大客户经理ID")
    private String accountId;

    @ApiModelProperty(value = "大客户经理姓名")
    private String accountName;

    @ApiModelProperty(value = "大客户经理电话")
    private String accountMobile;

    @ApiModelProperty(value = "归属项目ID")
    private String projectId;

    @ApiModelProperty(value = "归属项目名称")
    private String projectName;

    @ApiModelProperty(value = "原大客户经理ID")
    private String accountIdOld;

    @ApiModelProperty(value = "原大客户经理姓名")
    private String accountNameOld;

    @ApiModelProperty(value = "原大客户经理电话")
    private String accountMobileOld;

    @ApiModelProperty(value = "原归属项目ID")
    private String projectIdOld;

    @ApiModelProperty(value = "原归属项目名称")
    private String projectNameOld;

    @ApiModelProperty(value = "是否删除1删除，0未删除")
    private String isDel;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "编辑时间")
    private String editTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "备注")
    private String remarks;

    private String reason;

    private String entrance;

    private String batchId;
}
