package cn.visolink.system.channel.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.02
 */
@Data
@ApiModel(value = "跟进记录", description = "跟进记录")
public class FollowUpRecordVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "跟进id")
    private String followRecordId;

    @ApiModelProperty(value = "跟进方式")
    private String followUpWay;

    @ApiModelProperty(value = "跟进方式描述")
    private String followUpWayDesc;

    @ApiModelProperty(value = "跟进内容")
    private String communicationContent;

    @ApiModelProperty(value = "跟进详情")
    private String followUpDetail;

    @ApiModelProperty(value = "跟进人姓名")
    private String employeeName;

    @ApiModelProperty(value = "跟进时间")
    private String followUpDate;
    private String followUpStartTime;
    private String followUpEndTime;

    @ApiModelProperty(value = "下次跟进时间")
    private String nextFollowUpDate;

    @ApiModelProperty(value = "岗位")
    private String orgName;

    @ApiModelProperty(value = "下次跟进提醒内容")
    private String nextFollowUpDetail;

    @ApiModelProperty(value = "其他附件路径")
    private String enclosures;

    @ApiModelProperty(value = "三个一附件")
    private String threeOnesUrls;

    @ApiModelProperty(value = "图纸报价附件")
    private String drawingQuotationUrls;

    @ApiModelProperty(value = "其他附件路径")
    private List<String> enclosure;

    @ApiModelProperty(value = "三个一附件")
    private List<String> threeOnesUrl;

    @ApiModelProperty(value = "图纸报价附件")
    private List<String> drawingQuotationUrl;

    @ApiModelProperty(value = "审核状态")
    private String status;

    @ApiModelProperty(value = "跟进ID")
    private String id;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "方式")
    private String opportunityClueId;

    @ApiModelProperty(value = "机会状态")
    private String clueStatus;

    @ApiModelProperty(value = "审核时间")
    private String approvalTime;
    private String approvalStartTime;
    private String approvalEndTime;

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "客户手机号")
    private String customerMobile;

    @ApiModelProperty(value = "客户姓名")
    private String reCustomerName;

    @ApiModelProperty(value = "客户手机号")
    private String reCustomerMobile;

    @ApiModelProperty(value = "报备时间")
    private String reportCreateTime;

    @ApiModelProperty(value = "地址")
    private String customerAddress;

    @ApiModelProperty(value = "详细地址")
    private String detailedAddress;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "跟进人ID")
    private String userId;

    @ApiModelProperty(value = "跟进人名称")
    private String userName;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;

    @ApiModelProperty(value = "审核状态描述")
    private String statusDesc;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "审批人")
    private String approveUser;

    @ApiModelProperty(value = "是否满足三个一")
    private String isThreeOnesStatus;
    private String isThreeOnesStatusDesc;

    @ApiModelProperty(value = "客户来源")
    private String sourceMode;

    private String childProjectName;

    private String childProjectId;

    private Integer followUpUserRole;

    private String search;

    private String pageIndex;
    private String pageNum;

    private String pageSize;

    private List<Map> fileds;

    private List<String> projectList;

    private String isAll;

    private Integer rownum;

    private String projectClueId;

    private String mainApprovalUser;

    private String followUpAddress;

    private List<String> orgIds;

    @ApiModelProperty(value = "整改状态(0 无需整改 1 待整改 2 待复核 3 已整改")
    private String rectificationStatus;

    @ApiModelProperty(value = "核验结果(0 未核验 1 核验合格 2 核验不合格)")
    private String verificationStatus;
    private List<String> verificationStatusList;

    @ApiModelProperty(value = "核验人")
    private String verificationUerName;

    @ApiModelProperty(value = "核验时间")
    private String verificationTime;
    private String verificationStartTime;
    private String verificationEndTime;

    @ApiModelProperty(value = "跟进类型")
    private String dataSource;

    /**
     * 获取数据线索
     * @param
     * @return
     */
    public Object[] toData1(boolean isAll,List<String> fileds){
        String mobile = getReCustomerMobile();
        String customerName = getReCustomerName();
        if (isAll){
            mobile = getCustomerMobile();
            customerName = getCustomerName();
        }
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("followUpWayDesc")){
            met.add(getFollowUpWayDesc());
        }
        if(fileds.contains("areaName")){
            met.add(getAreaName());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("childProjectName")){
            met.add(getChildProjectName());
        }
        if(fileds.contains("followUpAddress")){
            met.add(getFollowUpAddress());
        }
        if(fileds.contains("customerName")){
            met.add(customerName);
        }
        if(fileds.contains("customerAddress")){
            met.add(getCustomerAddress());
        }
        if(fileds.contains("detailedAddress")){
            met.add(getDetailedAddress());
        }
        if(fileds.contains("sourceMode")){
            met.add(getSourceMode());
        }
        if(fileds.contains("contacts")){
            met.add(getContacts());
        }
        if(fileds.contains("customerMobile")){
            met.add(mobile);
        }
        if(fileds.contains("userName")){
            met.add(getUserName());
        }
        if(fileds.contains("followUpDate")){
            met.add(getFollowUpDate());
        }
        if(fileds.contains("followUpDetail")){
            met.add(getFollowUpDetail());
        }
        if(fileds.contains("nextFollowUpDate")){
            met.add(getNextFollowUpDate());
        }
        if(fileds.contains("nextFollowUpDetail")){
            met.add(getNextFollowUpDetail());
        }
        if(fileds.contains("statusDesc")){
            met.add(getStatusDesc());
        }
        if(fileds.contains("rejectReason")){
            met.add(getRejectReason());
        }
        if(fileds.contains("approveUser")){
            met.add(getApproveUser());
        }
        if(fileds.contains("approvalTime")){
            met.add(getApprovalTime());
        }
        if(fileds.contains("isThreeOnesStatusDesc")){
            met.add(getIsThreeOnesStatusDesc());
        }
        if(fileds.contains("verificationStatus")){
            met.add(getVerificationStatus());
        }
        Object[] objects = met.toArray();
        return objects;
    }

    /**
     * 获取数据线索
     * @param
     * @return
     */
    public Object[] toData2(boolean isAll,List<String> fileds){
        String mobile = getReCustomerMobile();
        String customerName = getReCustomerName();
        if (isAll){
            mobile = getCustomerMobile();
            customerName = getCustomerName();
        }
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("followUpWayDesc")){
            met.add(getFollowUpWayDesc());
        }
        if(fileds.contains("areaName")){
            met.add(getAreaName());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("childProjectName")){
            met.add(getChildProjectName());
        }
        if(fileds.contains("followUpAddress")){
            met.add(getFollowUpAddress());
        }
        if(fileds.contains("customerName")){
            met.add(customerName);
        }
        if(fileds.contains("customerAddress")){
            met.add(getCustomerAddress());
        }
        if(fileds.contains("detailedAddress")){
            met.add(getDetailedAddress());
        }
        if(fileds.contains("sourceMode")){
            met.add(getSourceMode());
        }
        if(fileds.contains("contacts")){
            met.add(getContacts());
        }
        if(fileds.contains("customerMobile")){
            met.add(mobile);
        }
        if(fileds.contains("userName")){
            met.add(getUserName());
        }
        if(fileds.contains("followUpDate")){
            met.add(getFollowUpDate());
        }
        if(fileds.contains("followUpDetail")){
            met.add(getFollowUpDetail());
        }
        if(fileds.contains("nextFollowUpDate")){
            met.add(getNextFollowUpDate());
        }
        if(fileds.contains("nextFollowUpDetail")){
            met.add(getNextFollowUpDetail());
        }
        if(fileds.contains("statusDesc")){
            met.add(getStatusDesc());
        }
        if(fileds.contains("rejectReason")){
            met.add(getRejectReason());
        }
        if(fileds.contains("approveUser")){
            met.add(getApproveUser());
        }
        if(fileds.contains("approvalTime")){
            met.add(getApprovalTime());
        }
        if(fileds.contains("isThreeOnesStatusDesc")){
            met.add(getIsThreeOnesStatusDesc());
        }
        if(fileds.contains("verificationUerName")){
            met.add(getVerificationUerName());
        }
        if(fileds.contains("verificationTime")){
            met.add(getVerificationTime());
        }
        if(fileds.contains("verificationStatus")){
            met.add(getVerificationStatus());
        }
        if(fileds.contains("rectificationStatus")){
            met.add(getRectificationStatus());
        }
        Object[] objects = met.toArray();
        return objects;
    }
}
