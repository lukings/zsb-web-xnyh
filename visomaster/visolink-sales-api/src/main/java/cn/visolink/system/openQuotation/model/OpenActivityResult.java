package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OpenActivityResult
 * @Author wanggang
 * @Description //开盘活动明细
 * @Date 2021/1/7 16:18
 **/
@Data
public class OpenActivityResult implements Serializable {

    private String rownum;//编号

    private String areaName;//区域名称

    private String projectName;//项目名称

    private String sxChooseRoomId;//顺销选房编号

    private String sxActivityName;//顺销选房活动名称

    private String sxReleaseTime;//顺销活动发布时间

    private String sxActivityBegintime;//顺销活动开始时间

    private String sxActivityEndtime;//顺销活动结束时间

    private String isOnlinePay;//是否支持线上支付 0否1是

    private String actStatus;//活动状态

    private String disableor;//禁用人

    private String disabletime;//禁用时间

    private String creator;//创建人

    private String createTime;//创建时间

    private String notPayCount;//未支付订单数

    private String payCount;//已支付订单数

    private String okCount;//已完成订单数

    private String closeCount;//关闭订单数

    private String signCount;//签署认购协议数


    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toActivityData(){
        return new Object[]{
                getRownum(),getAreaName(),getProjectName(),
                getSxChooseRoomId(),getSxActivityName(),getSxReleaseTime(),
                getSxActivityBegintime(),getSxActivityEndtime(),
                getIsOnlinePay(),getActStatus(),getCreateTime(),getCreator(),
                getDisabletime(),getDisableor(),
                getNotPayCount(),getPayCount(),getOkCount(),getCloseCount(),
                getSignCount()
        };
    }

    public String[]  activityTitle =  new String[]{
            "序号","区域","项目","开盘活动编号","开盘活动名称","开盘发布时间","开盘开始时间","开盘结束时间",
            "是否支持线上支付","活动状态","创建时间","创建人","禁用时间","禁用人","未支付订单数","已支付订单数","已完成订单数","关闭订单数","签署认购协议数"};

}
