package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OppTradeVo
 * @Author wanggang
 * @Description //交易辅助类
 * @Date 2021/11/20 10:40
 **/
@Data
public class OppTradeVo implements Serializable {

    private String id;

    private String opportunityClueId;//机会ID

    private String projectClueId;//线索ID

    private String tradeGUID;//交易ID

    private String intentionID;//机会关联ID

    private String contractGuid;//签约ID

    private String roomID;//房间ID

    private String roomName;//房间

    private String isClosed;//是否关闭

    private String isDeleted;//是否作废

    private String contractDate;//签约时间

    private String createTime;//创建时间

    private String updateTime;//更新时间

    private String cstName;//客户姓名

    private String cstMobile;//客户手机号

    private String cjBldArea;//成交面积

    private String cjBldPrice;//成交单价

    private String prjectGUID;//项目ID
    private String projectId;//项目ID

    private String projectFQGUID;//分期ID

    private String orderGUID;//认购ID

    private String orderDate;//认购时间

    private String orderYwgsDate;//认购业绩归属时间

    private String contractYwGsDate;//签约业绩归属时间

    private String clueStatus;//机会状态

    private String sourceType;//成交类型

    private String tradeStatus;//交易状态

    private String closeReason;//关闭原因

    private String tnArea;//套内面积

    private String buyerAllCardIds;//买房证件号

    private String htTotal;//合同总价

    private String zygw;//置业顾问

    private String cjTnPrice;//成交套内单价

    private String remarks;//备注

    private String creator;

    private String updator;

    private String salesAttributionTeamId;//招商团队

    private String salesAttributionId;//招商专员

}
