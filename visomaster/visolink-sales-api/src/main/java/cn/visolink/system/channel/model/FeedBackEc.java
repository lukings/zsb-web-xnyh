package cn.visolink.system.channel.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户反馈记录表
 * @TableName a_user_feedback
 */
@Data
public class FeedBackEc implements Serializable {
    /**
     * 主键ID
     */
    private String id;

    /**
     * 反馈人ID
     */
    private String feedBackUserId;

    /**
     * 反馈人名称
     */
    private String feedBackUserName;

    /**
     * 反馈人手机号
     */
    private String feedBackUserMobile;

    /**
     * 问题描述
     */
    private String problemDesc;

    /**
     * 反馈人角色
     */
    private String feedBackUserRole;

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

    /**
     * 状态0未处理 1 已处理
     */
    private String status;

    /**
     * 是否删除 0未删除 1 删除
     */
    private String isDel;

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
     * 问题类型 0 系统常见问题 1 问题反馈
     */
    private String type;

    /**
     * 关键词
     */
    private String keyWord;

    private String pageNum;

    private String pageSize;

    private List<String> enclosures;//附件

    private String enclosureList;//附件逗号分隔

    private List<Map> fileds;

    private Integer rownum;

    private String startTime;

    private String endTime;

    private List<String> projectList;

    /**
     * 获取数据机会
     * @param
     * @return
     */
    public Object[] toData(boolean isAll,List<String> fileds){
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("feedBackUserName")){
            met.add(getFeedBackUserName());
        }
        if(fileds.contains("feedBackUserMobile")){
            met.add(getFeedBackUserMobile());
        }
        if(fileds.contains("createTime")){
            met.add(getCreateTime());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("problemDesc")){
            met.add(getProblemDesc());
        }
        Object[] objects = met.toArray();
        return objects;
    }

    private static final long serialVersionUID = 1L;
}