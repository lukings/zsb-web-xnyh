package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CustomerDistributionRecords
 * @Author wanggang
 * @Description //重分配记录
 * @Date 2021/1/12 11:34
 **/
@Data
@ApiModel(value = "CustomerDistributionRecords", description = "重分配记录")
public class CustomerDistributionRecords implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "重分配批次")
    private String redistributionBatchId;

    @ApiModelProperty(value = "项目iD")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "操作人员姓名")
    private String employeeName;

    @ApiModelProperty(value = "操作时间")
    private String confirmTime;

    @ApiModelProperty(value = "分配原因")
    private String reason;

    @ApiModelProperty(value = "发起入口")
    private String entrance;

    @ApiModelProperty(value = "客户姓名")
    private String cstName;

    @ApiModelProperty(value = "客户手机号(隐号)")
    private String cstPhone;

    @ApiModelProperty(value = "客户手机号(全号)")
    private String cstPhoneAll;

    @ApiModelProperty(value = "客户状态")
    private String cstStatus;

    @ApiModelProperty(value = "原销售人员")
    private String oldSalesName;

    @ApiModelProperty(value = "销售人员")
    private String salesName;

    @ApiModelProperty(value = "销售人员归属时间")
    private String salesAttributionTime;

    @ApiModelProperty(value = "销售人员归属时间")
    private String oldSalesAttributionTime;

    private String distributionMode;

    @ApiModelProperty(value = "企业名称")
    private String customerName;

    @ApiModelProperty(value = "原报备人")
    private String reportUserName;

    private String customerMobile;

    private String search;

    private String startTime;

    private String endTime;

    private String date;

    private String pageNum;

    private String pageSize;

    private List<String> projectList;

    private Integer reportUserRole;

    private String isAll;

    private List<Map> fileds;


    private String getPhone(String isAll){
        if ("1".equals(isAll)){
            return getCstPhoneAll();
        }else{
            return getCstPhone();
        }
    }
    /**
     * 获取分析数据
     * @param
     * @return
     */
    public Object[] toExproData(String isAll){
        return new Object[]{
                getRedistributionBatchId(),getEmployeeName(),getConfirmTime(),getEntrance()
                ,getReason(),getDistributionMode(),getCstName(),this.getPhone(isAll),getProjectName(),getCstStatus(),
                getSalesName(),getConfirmTime(),getOldSalesName(),getOldSalesAttributionTime()
        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "重分配批次","操作人","操作时间","重分配入口","分配原因","分配模式",
            "客户姓名","客户手机号","项目",
            "客户状态","归属人","归属时间","原归属人"
            ,"原归属时间"};

}
