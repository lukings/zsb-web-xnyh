package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * ProjectCluesVO对象
 * </p>
 *
 * @author 吴要光
 * @since 2019-08-27
 */
@Data
@ApiModel(value = "CustomerDistributionRecords对象", description = "重分配表")
public class CustomerDistributionRecordsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String rownum;

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
    private String confirmTime;

    @ApiModelProperty(value = "是否是最新")
    private Boolean isNew;

    @ApiModelProperty(value = "分配原因")
    private String reason;

    @ApiModelProperty(value = "发起入口")
    private String entrance;

    @ApiModelProperty(value = "分配记录表id")
    private String redistributionBatchId;

    @ApiModelProperty(value = "渠道原归属时间")
    private String oldTokerAttributionTime;

    @ApiModelProperty(value = "案场原归属时间")
    private String oldSalesAttributionTime;

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

    @ApiModelProperty(value = "销售人员归属时间")
    private String salesAttributionTime;

    @ApiModelProperty(value = "案场机会id")
    private String opportunityClueId;

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "客户手机号")
    private String customerMobile;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "类别(1:项目负责人分配 2：放弃客户 3：淘客)")
    private String type;

    @ApiModelProperty(value = "放弃时间")
    private String applyDatetime;

    @ApiModelProperty(value = "客户手机全号")
    private String oldCustomerMobile;

    @ApiModelProperty(value = "成交类型")
    private String sourceType;

    @ApiModelProperty(value = "客户状态")
    private String clueStatus;

    @ApiModelProperty(value = "原报备人")
    private String reportUserName;

    @ApiModelProperty(value = "原报备时间")
    private String reportCreateTime;

    @ApiModelProperty(value = "公司全称")
    private String oldCustomerName;

    @ApiModelProperty(value = "原归属项目")
    private String oldProjectName;

    @ApiModelProperty(value = "接受项目")
    private String newProjectName;

    @ApiModelProperty(value = "接受人")
    private String salesAttributionNames;

    @ApiModelProperty(value = "分配批次编码")
    private String batchCode;

    @ApiModelProperty(value = "分配客户概述")
    private String redistributionResult;
    private String batchInfo;

    /**
     * 获取数据线索
     * @param
     * @return
     */
    public Object[] toData1(boolean isAll, List<String> fileds){
        String mobile = getCustomerMobile();
        String customerName = getCustomerName();
        if (isAll) {
            mobile = getOldCustomerMobile();
            customerName = getOldCustomerName();
        }
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("oldSalesName")){
            met.add(getOldSalesName());
        }
        if(fileds.contains("applyDatetime")){
            met.add(getApplyDatetime());
        }
        if(fileds.contains("customerName")){
            met.add(customerName);
        }
        if(fileds.contains("customerMobile")){
            met.add(mobile);
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("clueStatus")){
            met.add(getClueStatus());
        }
        if(fileds.contains("reportUserName")){
            met.add(getReportUserName());
        }
        if(fileds.contains("reportCreateTime")){
            met.add(getReportCreateTime());
        }

        Object[] objects = met.toArray();
        return objects;

//        return new Object[]{
//                getRownum(), getOldSalesName(), getApplyDatetime(), customerName, mobile, getProjectName(),
//                getClueStatus(), getReportUserName(), getReportCreateTime()
//        };
    }

    public Object[] toData2(boolean isAll, List<String> fileds){
        String mobile = getCustomerMobile();
        String customerName = getCustomerName();
        if (isAll) {
            mobile = getOldCustomerMobile();
            customerName = getOldCustomerName();
        }
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("confirmPersonName")){
            met.add(getConfirmPersonName());
        }
        if(fileds.contains("confirmTime")){
            met.add(getConfirmTime());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("customerName")){
            met.add(customerName);
        }
        if(fileds.contains("customerMobile")){
            met.add(mobile);
        }
        if(fileds.contains("oldSalesName")){
            met.add(getOldSalesName());
        }
        if(fileds.contains("oldProjectName")){
            met.add(getOldProjectName());
        }
        if(fileds.contains("salesName")){
            met.add(getSalesName());
        }
        if(fileds.contains("newProjectName")){
            met.add(getNewProjectName());
        }

        Object[] objects = met.toArray();
        return objects;
    }
    public String[]  courtCaseTitle1 =  new String[]{
            "序号", "放弃人" ,"放弃时间", "企业名称", "客户电话", "项目",
            "客户状态", "原报备人","原报备时间"};

}
