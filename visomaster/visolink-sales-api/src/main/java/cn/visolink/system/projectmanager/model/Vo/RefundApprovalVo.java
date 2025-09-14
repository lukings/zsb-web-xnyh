package cn.visolink.system.projectmanager.model.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName RefundApprovalVo
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/12/6 18:05
 **/
@Data
@ApiModel(value="退款审核")
public class RefundApprovalVo {

    @ApiModelProperty(value = "序号")
    private String rownum;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目")
    private String projectName;

    @ApiModelProperty(value = "分期ID")
    private String projectFid;

    @ApiModelProperty(value = "创建人ID")
    private String creator;

    @ApiModelProperty(value = "创建人时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String updator;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;

    @ApiModelProperty(value = "(1:发起 2：通过 3：驳回)")
    private String status;

    @ApiModelProperty(value = "款项类型")
    private String itemType;

    @ApiModelProperty(value = "款项名称")
    private String itemName;

    @ApiModelProperty(value = "房间名称")
    private String roomName;

    @ApiModelProperty(value = "客户姓名")
    private String cstName;

    @ApiModelProperty(value = "客户手机号")
    private String cstMobile;

    @ApiModelProperty(value = "退款状态")
    private String refundApprovalStatus;

    @ApiModelProperty(value = "退款状态描述")
    private String refundApprovalStatusDesc;

    @ApiModelProperty(value = "支付状态")
    private String receivableStatus;

    @ApiModelProperty(value = "交易金额")
    private String ysAmount;

    @ApiModelProperty(value = "退款手续费")
    private String serviceCharge;

    @ApiModelProperty(value = "应收日期")
    private String ysDate;

    @ApiModelProperty(value = "实收日期")
    private String payDate;

    @ApiModelProperty(value = "银行类型")
    private String collBank;

    @ApiModelProperty(value = "银行卡号")
    private String bankNo;

    @ApiModelProperty(value = "银行卡用户姓名")
    private String bankCstName;

    @ApiModelProperty(value = "银行卡用户预留手机号")
    private String bankCstMobile;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "驳回原因")
    private String rejectionReasons;


    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toRefundApprovalData() {
        return new Object[]{
                getRownum(),getProjectName(),getItemType(),getItemName(),getRoomName(),
                getCstName(),getCstMobile(),getRefundApprovalStatusDesc(),getReceivableStatus(),
                getYsAmount(),getServiceCharge(),getYsDate(),getPayDate(),
                getCollBank(),getBankNo(),getBankCstMobile(),getCreator(),getCreateTime(),getRemarks(),getRejectionReasons()
        };
    }

    public String[] refundApprovalTitle = new String[]{
            "序号","项目","款项类型","款项名称","房间名称","客户姓名",
            "客户手机号","退款状态","支付状态","交易金额","退款手续费","应收日期","实收日期",
            "银行类型","银行卡号","银行预留手机号","操作人","操作时间","备注","驳回原因"};
}
