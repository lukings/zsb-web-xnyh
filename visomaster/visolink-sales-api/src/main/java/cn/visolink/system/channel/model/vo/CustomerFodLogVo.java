package cn.visolink.system.channel.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户跟进交易日志表
 * @TableName b_customer_fod_log
 */
@Data
public class CustomerFodLogVo implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 客户新增日志表ID
     */
    private String addLogId;

    /**
     * 客户ID
     */
    private String opportunityClueId;

    /**
     * 线索客户ID
     */
    private String projectClueId;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 业务类型(1 电话微信 2上门拜访 3 邀约到访 4自然来访 5其他 6 签约 7退房)
     */
    private String businessType;

    /**
     * 业务时间
     */
    private String businessTime;

    /**
     * 周期类型 (1 成交周期 2 复购周期)
     */
    private String cycleType;

    /**
     * 集团周期
     */
    private Double groupCycle;

    /**
     * 区域周期
     */
    private Double areaCycle;

    /**
     * 项目周期
     */
    private Double projectCycle;

    /**
     * 团队周期
     */
    private Double teamCycle;

    /**
     * 专员周期
     */
    private Double userCycle;

    /**
     * 业务发生项目
     */
    private String businessProjectId;

    /**
     * 招商最终到访项目
     */
    private String mainVisitProjectId;

    /**
     * 是否三个一跟进
     */
    private String isThreeOnesStatus;

    /**
     * 是否三个一前跟进(0 三个一前 1 三个一后)
     */
    private String isThreeOnesAfterStatus;

    /**
     * 是否首访
     */
    private String isFirstVisitStatus;

    /**
     * 是否首拜
     */
    private String isFirstComeVisitStatus;

    /**
     * 是否签约后到访(0 否 1 是)
     */
    private String isSignAfterVisitStatus;

    /**
     * 是否统计(0 否 1 是)
     */
    private String isStatistics;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 更新人
     */
    private String updator;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否删除
     */
    private String isDel;

    private static final long serialVersionUID = 1L;
}