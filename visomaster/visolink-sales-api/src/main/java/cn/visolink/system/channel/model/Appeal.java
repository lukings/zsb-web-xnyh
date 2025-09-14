package cn.visolink.system.channel.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 招商专员申诉表
 * @TableName b_user_appeal
 */
@Data
public class Appeal implements Serializable {
    /**
     *
     */
    private String id;

    /**
     * 申诉人ID
     */
    private String userId;

    /**
     * 申诉人
     */
    private String username;

    /**
     * 问题描述
     */
    private String problemDesc;

    /**
     * 问题截图链接
     */
    private String imgUrl;

    /**
     * 处理人ID
     */
    private String handleUserId;

    /**
     * 处理时间
     */
    private String handleTime;
    private String handleStartTime;
    private String handleEndTime;

    /**
     * 处理说明
     */
    private String handleDesc;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;
    private String createStartTime;
    private String createEndTime;

    /**
     * 状态0 申请中 1 已通过 2 被驳回
     */
    private Integer status;

    /**
     * 是否删除 0未删除 1 删除
     */
    private Integer isDel;

    /**
     * 修改人
     */
    private String editor;

    /**
     * 修改时间
     */
    private String editTime;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 流程ID
     */
    private String processId;

    private String search;

    private String mobile;

    private String pageNum;

    private String pageSize;

    private List<String> projectList;

    private String jobCode;

    private String isAll;

    private  List<Map> fileds;

    private static final long serialVersionUID = 1L;
}
