package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderList
 * @Author wanggang
 * @Description //订单列表
 * @Date 2021/1/7 19:02
 **/
@Data
public class OrderList implements Serializable {

    private String rownum;//编号

    private String id;

    private String projectName;//项目名称

    private String orderNo;//订单编号

    private String cstName;//客户姓名

    private String cstPhone;//客户手机号

    private String cstPhoneAll;//客户手机号(全号)

    private String createTime;//创建时间

    private String failTime;//支付超时时间

    private String actualPayType;//支付方式

    private String payStatus;//支付状态

    private String electronicSealStatus;//是否签署认购协议

    private String amountPayable;//应付金额

    private String realityPayTime;//支付时间

    private String tradeCloseTime;//交易关闭时间

    private String tradeCloseReason;//交易关闭原因

    private String orderType;//订单类型（1 旭客家 2 旭客汇）

    private String openId;

    private String unbindReason; //解除绑定原因

    private String getPhone(String isAll){
        if ("1".equals(isAll)){
            return getCstPhoneAll();
        }else{
            return getCstPhone();
        }
    }
    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toOrderData(String isAll){
        String phone = this.getPhone(isAll);
        return new Object[]{
                getRownum(),getProjectName(),getOrderNo(),getOrderType(),
                getCstName(),phone,
                getCreateTime(),getFailTime(),getActualPayType(),
                getAmountPayable(),getPayStatus(),getRealityPayTime(),
                getTradeCloseTime(),getTradeCloseReason(),getElectronicSealStatus(),getUnbindReason()
        };
    }

    public String[]  orderTitle =  new String[]{
            "序号","项目","订单编号","订单来源","客户姓名","客户手机号","创建时间","支付超时时间",
            "支付方式","应付金额","支付状态","支付时间","交易关闭时间","交易关闭原因","是否签署认购协议","解除绑定原因"};

}
