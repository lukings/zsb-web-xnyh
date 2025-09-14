package cn.visolink.system.openQuotation.model;

import cn.visolink.system.householdregistration.model.IntentionPlaceMaterial;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName OpenActivity
 * @Author wanggang
 * @Description //开盘活动
 * @Date 2020/12/31 15:12
 **/
@Data
public class OpenActivity  implements Serializable {

    private String rownum;//编号

    private String id;

    private String areaId;//区域ID

    private String areaName;//区域名称

    private String projectId;//项目ID

    private String projectName;//项目名称

    private String chooseRoomType;//选房类型 1顺销选房2集中开盘

    private String choosePattern = "1";//选房模式 1：销控图模式 2：精选模式

    private String sxChooseRoomId;//顺销选房编号

    private String sxActivityName;//顺销选房活动名称

    private String sxReleaseTime;//顺销活动发布时间

    private String sxActivityBegintime;//顺销活动开始时间

    private String sxActivityEndtime;//顺销活动结束时间

    private String sxActivityDesc;//活动规则说明

    private String isOnlinePay;//是否支持线上支付 0否1是

    private String bankName;//商户收款账户银行名称

    private String bankCardId;//商户收款账户号

    private String failMinute;//支付过期时间（分钟）

    private String orderSignType;//认购协议签署方式 1线上签署 2线下签署

    private String houseAreaShowType;//房源面积展示方式 1建筑面积2套内面积

    private String delStatus;//删除状态

    private String actStatus;//活动状态

    private String status;//是否启用

    private String disableor;//禁用人

    private String disabletime;//禁用时间

    private String creator;//创建人

    private String createTime;//创建时间

    private List<OpenActivityBuild> buildings;//开盘活动楼栋

    private List<OpenBuildBook> buildBooks;//开盘活动关联楼盘

    private List<OpenActivityRoom> roomList;//开盘活动精选房间

    private List<OpenNotRoom> rooms;//开盘活动不展示房间

    private List<OpenBuildSite> buildSites;//开盘活动楼栋概览图坐标

    private List<IntentionPlaceMaterial> materialList;//开盘活动素材

    private List<OpenDiscount> activityDiscount;//开盘活动折扣

    private List<OpenActivityBank> activityBanks;//开盘活动商户

    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toActivityData(){
        return new Object[]{
                getRownum(),getAreaName(),getProjectName(),
                getSxChooseRoomId(),getSxActivityName(),
                getSxActivityBegintime(),getSxActivityEndtime(),getSxReleaseTime(),
                getIsOnlinePay(),getBankCardId(),getActStatus(),
                getCreator(),getCreateTime(),getDisableor(),getDisabletime()
        };
    }

    public String[]  activityTitle =  new String[]{
            "序号","区域","项目","开盘活动编号","开盘活动名称","开盘开始时间","开盘结束时间",
            "发布时间","是否支持线上支付","收款账户","状态","创建人","创建时间","禁用人","禁用时间"};

}
