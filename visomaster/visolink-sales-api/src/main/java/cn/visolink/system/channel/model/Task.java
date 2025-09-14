package cn.visolink.system.channel.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 任务表
 * @TableName b_task
 */
@Data
public class Task implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型id
     */
    private String taskTypeId;

    /**
     * 任务类型名称
     */
    private String taskTypeName;

    /**
     * 任务开始时间
     */
    private String startTime;

    /**
     * 任务结束时间
     */
    private String endTime;

    /**
     * 任务地点
     */
    private String taskArea;

    /**
     * 任务地点
     */
    private String taskAreaName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县
     */
    private String county;

    /**
     * 街道
     */
    private String street;

    /**
     * 报备指标
     */
    private Integer reportNum;

    /**
     * 拜访指标
     */
    private Integer visitNum;

    /**
     * 到访指标
     */
    private Integer arriveNum;

    /**
     * 成交指标
     */
    private Integer dealNum;

    /**
     * 团队id
     */
    private String teamId;

    /**
     * 团队名称
     */
    private String teamName;

    /**
     * 所属项目
     */
    private String projectId;

    /**
     * 所属组织
     */
    private String orgId;

    /**
     * 是否删除：0 未删除、1 已删除
     */
    private Integer isDel;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private String modifyTime;

    /**
     * 任务报备数
     */
    private Integer taskReport;

    /**
     * 媒体大类ID
     */
    private String mainMediaId;

    /**
     * 媒体大类描述
     */
    private String mainMediaName;

    /**
     * 媒体子类ID
     */
    private String subMediaId;

    /**
     * 媒体子类描述
     */
    private String subMediaName;

    /**
     * 任务描述
     */
    private String taskDesc;

    private String pageNum;

    private String pageSize;

    private String search;

    private String mobile;

    private String oldMobile;

    private List<String> projectList;

    private String taskStatus;

    private String date1;

    private String date2;

    private String isAll;

    private List<Map> fileds;

    private List<String> orgIds;

    private String parentId;

    private String status;

    private List<String> userIds;

    private String jobCode;

    /**
     * 任务创建人姓名
     */
    private String createUserName;

    /**
     * 任务执行人姓名
     */
    private String memberUserName;


    private List<String> userIdsCreate;
    private List<String> userIdsMember;

    private List<String> taskIds;

    private List<String> taskAreaIds;

    private String isForDraw;

    private static final long serialVersionUID = 1L;
}
