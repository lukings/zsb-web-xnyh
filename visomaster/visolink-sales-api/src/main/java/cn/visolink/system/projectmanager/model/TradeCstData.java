package cn.visolink.system.projectmanager.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName TradeCstData
 * @Author wanggang
 * @Description //交易客户信息
 * @Date 2021/11/24 13:58
 **/
@Data
@ApiModel(value = "交易客户对象", description = "")
public class TradeCstData {

    private int rownum;//序号

    @ApiModelProperty(name = "roomId", value = "房间ID")
    private String roomId;

    @ApiModelProperty(name = "roomInfo", value = "房间全称")
    private String roomInfo;

    @ApiModelProperty(name = "cstName", value = "客户姓名")
    private String cstName;

    @ApiModelProperty(name = "cstMobile", value = "客户手机号（隐号）")
    private String cstMobile;

    @ApiModelProperty(name = "cstMobileAll", value = "客户手机号(全号)")
    private String cstMobileAll;

    @ApiModelProperty(name = "projectName", value = "项目")
    private String projectName;

    @ApiModelProperty(name = "clueStatus", value = "客户状态")
    private String clueStatus;

    @ApiModelProperty(name = "sourceType", value = "成交类型")
    private String sourceType;

    @ApiModelProperty(name = "zygw", value = "置业顾问")
    private String zygw;

    @ApiModelProperty(name = "projectId", value = "项目ID")
    private String projectId;

    @ApiModelProperty(name = "htTotal", value = "交易金额")
    private String htTotal;

    @ApiModelProperty(name = "tradeDate", value = "交易时间")
    private String tradeDate;

    @ApiModelProperty(name = "tradeRemarks", value = "交易备注")
    private String tradeRemarks;

    @ApiModelProperty(name = "checkOutCreator", value = "退房申请人")
    private String checkOutCreator;

    @ApiModelProperty(name = "checkOutCreateTime", value = "退房申请时间")
    private String checkOutCreateTime;

    @ApiModelProperty(name = "approver", value = "退房审批人")
    private String approver;

    @ApiModelProperty(name = "approvalTime", value = "退房审批时间")
    private String approvalTime;

    @ApiModelProperty(name = "checkOutRemarks", value = "退房备注")
    private String checkOutRemarks;

    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toTradeData(String isAll) {
        String mobile = getCstMobile();
        if ("1".equals(isAll)){
            mobile = getCstMobileAll();
        }
        return new Object[]{
                getRownum(),getRoomInfo(),getCstName(),mobile,getProjectName(),
                getClueStatus(),getSourceType(),getZygw(),
                getHtTotal(),getTradeDate(),getTradeRemarks(),getCheckOutCreator(),
                getCheckOutCreateTime(),getApprover(),getApprovalTime(),getCheckOutRemarks()
        };
    }

    public String[] tradeTitle = new String[]{
            "序号","房间号","客户姓名","客户电话","项目","客户状态",
            "成交类型","置业顾问","交易金额","交易时间","交易备注",
            "退房申请人","退房申请时间","退房审批人","审批时间","退房备注"};
}
