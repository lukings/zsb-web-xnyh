package cn.visolink.system.channel.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户等级记录表
 * @TableName b_customer_level_record
 */
@Data
public class CustomerLevelRecordVo implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 客户ID
     */
    private String opportunityCueId;

    /**
     * 线索客户ID
     */
    private String projectClueId;

    /**
     * 客户等级
     */
    private String customerLevel;

    /**
     * 案场归属人ID
     */
    private String salesAttributionId;

    /**
     * 案场归属人名称
     */
    private String salesAttributionName;

    /**
     * 案场归属团队ID
     */
    private String salesAttributionTeamId;

    /**
     * 案场归属团队名称
     */
    private String salesAttributionTeamName;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 区域ID
     */
    private String areaId;

    /**
     * 案场归属时间
     */
    private String salesAttributionTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新人
     */
    private String updator;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否删除
     */
    private Integer isDel;

    private static final long serialVersionUID = 1L;
}