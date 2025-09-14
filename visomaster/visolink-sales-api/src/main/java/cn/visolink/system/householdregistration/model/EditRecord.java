package cn.visolink.system.householdregistration.model;

import lombok.Data;

/**
 * @ClassName EditRecord
 * @Author wanggang
 * @Description // 调整记录
 * @Date 2021/2/18 14:36
 **/
@Data
public class EditRecord {

    private String rownum;

    private String customerName;//客户姓名

    private String customerMobileAll;//客户手机号全号

    private String customerMobile;//客户手机号隐号

    private String customerStatus;//客户状态

    private String salesName;//置业顾问姓名

    private String cardName;//卡号

    private String batchNoName;//排卡批次

    private String cardGroupingName;//排卡分组

    private String activityName;//活动名

    private String areaName;//区域

    private String projectName;//项目

    private String intentionLevelOld;//原意向级别（1一选，2二选，3三选，4四选，5五选）

    private String intentionLevel;//现意向级别

    private String roomNameOld;//原房间名

    private String roomName;//现房间名

    private String editStatus;//调整状态（1:未处理 2：通过 3：拒绝）

    /**
     * 获取数据
     * @param isAll(0:隐号 1：全号)
     * @return
     */
    public Object[] toActivityData(String isAll){
        String custMobile = getCustomerMobile();
        if ("1".equals(isAll)){
            custMobile = getCustomerMobileAll();
        }
        return new Object[]{
                getRownum(),getCustomerName(),custMobile,getSalesName(),
                getCardName(),getBatchNoName(),
                getCardGroupingName(),getActivityName(),getAreaName(),getProjectName(),
                getIntentionLevelOld(),getIntentionLevel(),getRoomNameOld(),
                getRoomName(),getEditStatus()
        };
    }

    public String[]  activityTitle =  new String[]{
            "序号","客户姓名","客户电话","置业顾问",
            "卡号","开盘批次","排卡分组","活动名称",
            "区域","项目","原意向级别","调整意向级别","原房间","调整房间","调整状态"};
}
