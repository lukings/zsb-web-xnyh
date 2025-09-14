package cn.visolink.system.channel.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 无效报备明细记录
 * @TableName b_customer_repeat_report_detail
 */
@Data
public class ReportFail implements Serializable {
    /**
     * 主键id
     */
    private String id;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 客户来源 1 招商专员 2 区域招商专员  4 万企通
     */
    private Integer sourceType;

    /**
     * 手机号类型 1 隐号 2 全号
     */
    private Integer mobileType;

    /**
     * 客户手机号
     */
    private String customerMobile;

    /**
     * 企业名称
     */
    private String customerName;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 岗位
     */
    private String jobCode;

    /**
     * 报备人id
     */
    private String reportUserId;

    /**
     * 原因
     */
    private String reason;

    /**
     * 报备时间
     */
    private String reportTime;

    /**
     * 创建时间
     */
    private String createTime;

    private String pageNum;

    private String pageSize;

    private List<String> projectList;

    private String search;

    private String reportName;

    private String date1;

    private String date2;

    private String isAll;

    /**
    * 管理员权限控制
    * */
    private Integer type;
    private List<Map> fileds;

    private static final long serialVersionUID = 1L;
}
