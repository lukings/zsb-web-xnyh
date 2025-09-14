package cn.visolink.system.excel.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 亮
 * @Description:
 * @date 2024/8/30 14:13
 */
@Data
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value="客户信息", description="客户表")
public class FollowUpRecordForm {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "跟进ID")
    private String FollowRecordId;

    @ApiModelProperty(value = "跟进方式")
    private String followUpWay;

    @ApiModelProperty(value = "跟进内容")
    private String communicationContent;

    @ApiModelProperty(value = "跟进详情")
    private String followUpDetail;

    @ApiModelProperty(value = "用户Id")
    private String userId;

    @ApiModelProperty(value = "跟进状态")
    private String status;

    @ApiModelProperty(value = "意向等级")
    private String level;

    @ApiModelProperty(value = "线索Id")
    private String projectClueId;

    @ApiModelProperty(value = "客户Id")
    private String saleCustomerId;

    @ApiModelProperty(value = "项目ID")
    private String projectId;
    private String childProjectId;
    private String projectName;

    @ApiModelProperty(value = "跟进人姓名")
    private String employeeName;

    @ApiModelProperty(value = "岗位Id")
    private String jobOrgId;

    @ApiModelProperty(value = "跟进时间")
    private String followUpDate;

    @ApiModelProperty(value = "岗位")
    private String jobName;

    @ApiModelProperty(value = "附件")
    private List<String> enclosures;

    @ApiModelProperty(value = "三个一图片路径")
    private List<String> threeOnesUrl;

    @ApiModelProperty(value = "图纸报价路径")
    private List<String> drawingQuotationUrl;

    @ApiModelProperty(value = "机会ID")
    private String opportunityClueId;

    @ApiModelProperty(value = "状态")
    private String clueStatus;

    @ApiModelProperty(value = "下次跟进提醒内容")
    private String nextFollowUpDetail;

    @ApiModelProperty(value = "地址")
    private String customerAddress;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "下次跟进时间")
    private String nextFollowUpDate;

    @ApiModelProperty(value = "跟进方式描述")
    private String followUpWayDesc;

    @ApiModelProperty(value = "跟进人角色（1：项目招商专员 2：区域招商专员）")
    private String followUpUserRole;

    @ApiModelProperty(value = "是否业务员首次跟进（1：是 0：否）")
    private String isFirstFollowUp;

    @ApiModelProperty(value = "主跟进项目id(实际跟进项目 普通项目为项目自身 区域项目为到访项目)")
    private String mainFollowProjectId;

    @ApiModelProperty(value = "失效时间")
    private String invalidTime ;

    @ApiModelProperty(value = "是否三个一拜访")
    private String isThreeOnesStatus;

    @ApiModelProperty(value = "id")
    private String id;

}
