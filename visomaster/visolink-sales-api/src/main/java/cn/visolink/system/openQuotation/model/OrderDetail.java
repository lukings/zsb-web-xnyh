package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OrderDetail
 * @Author wanggang
 * @Description //订单详情
 * @Date 2021/1/8 15:01
 **/
@Data
public class OrderDetail implements Serializable {

    private String id;

    private String mainOppId;//主客户机会ID

    private String oppId;//明源机会ID

    private String roomId;//房间ID

    private String orderNo;//订单编号

    private String cstName;//客户姓名

    private String cstPhone;//客户手机号

    private String cstCardTypeName;//客户证件类型

    private String cstCardId;//客户证件号

    private String address;//客户地址

    private String sxChooseRoomId;//开盘活动编号

    private String sxActivityName;//开盘活动名称

    private String status;//订单状态（1：激活 2：关闭）

    private String tradeCloseReason;//交易关闭原因

    private String roomName;//房间名称

    private String electronicSealStatus;//是否签署认购协议

    private String salesName;//置业顾问名称

    private String createTime;//创建时间

    private String failTime;//支付超时时间

    private String actualPayType;//支付方式

    private String payStatus;//支付状态

    private String amountMoney;//定金总额

    private String amountPayable;//应付金额

    private String realityPayTime;//支付时间

    private String cardAccount;//卡抵扣金额

    private String relCardId;//排卡ID

    private String relCardName;//排卡名

    private List<Buyers> relationBuyers;//关联购房人

    private List<Map> cards;//主客户排卡

    private List<OrderNode> orderNodes;//订单节点

    private String brokerName;

    private String salesMobile;

    private String createName;

    private String bindingMobile;

    private String orderType;

    private String creator;

}
