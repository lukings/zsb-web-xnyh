package cn.visolink.system.openQuotation.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2021/1/12
 */
@Data
public class OrderNodeRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "节点类型 (1 生成订单 2 发起支付 3 支付成功 4 支付失败 5订单完成 6 签署认购书 7 订单关闭 )")
    private String nodeType;

    @ApiModelProperty(value = "节点类型名称")
    private String nodeTypeName;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty(value = "记录时间")
    private String recordTime;

    @ApiModelProperty(value = "是否删除")
    private String isDel;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "修改时间")
    private String editTime;

    @ApiModelProperty(value = "绑定人")
    private String bindPerson;

    @ApiModelProperty(value = "解绑人")
    private String unbindPerson;

    @ApiModelProperty(value = "解绑原因")
    private String unbindReason;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "接收人")
    private String receivePerson;
}
