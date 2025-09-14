package cn.visolink.system.pubilcPool.model.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PublicPoolListSearch
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/26 16:29
 **/
@NoArgsConstructor
@Data
public class PublicPoolListSearch  implements Serializable {

    @ApiModelProperty(value = "客户姓名")
    private String customerName;
    @ApiModelProperty(value = "客户手机号")
    private String customerMobile;
    @ApiModelProperty(value = "最后一次跟进人")
    private String latelyFollowUpPerson;
    @ApiModelProperty(value = "报备人")
    private String reportUserName;
    @ApiModelProperty(value = "加入公共池分类")
    private List<String> addType;
    @ApiModelProperty(value = "池类型( 1 公共池 2 淘客池)")
    private String poolType;
    @ApiModelProperty(value = "记录类型 (1 放弃 2 淘客 3回收 4重分配)")
    private String recordType;
    @ApiModelProperty(value = "成交渠道 1 中介 2 自渠 3 案场 4 全民经纪人5 历史数据")
    private String sourceType;
    @ApiModelProperty(value = "加入公池原因")
    private List<String> addReasonType;
    @ApiModelProperty(value = "是否激活 1：是 0：否")
    private String activateReasonType;
    @ApiModelProperty(value = "时间类型 1：进入公共池时间 2：报备时间")
    private String selectTime;
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    @ApiModelProperty(value = "项目ID集合")
    private List<String> projectIds;
    @ApiModelProperty(value = "每页行数")
    private long size = 10;
    @ApiModelProperty(value = "当前页")
    private long current = 1;
    @ApiModelProperty(value = "操作人")
    private String operatorName;
    @ApiModelProperty(value = "导出类型 1:全号 0:隐号")
    private String isAll;
    @ApiModelProperty(value = "当前用户ID")
    private String userId;
    @ApiModelProperty(value = "客户ID集合")
    private List<String> cstIds;
    @ApiModelProperty(value = "客户状态")
    private String clueStatus;
    @ApiModelProperty(value = "客户状态")
    private List<String> clueStatusList;
}
