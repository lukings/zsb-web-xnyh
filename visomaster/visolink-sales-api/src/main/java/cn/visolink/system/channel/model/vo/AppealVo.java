package cn.visolink.system.channel.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 招商专员申诉表
 * @TableName b_user_appeal
 */
@Data
public class AppealVo implements Serializable {
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
    private String imgUrlStr;
    private List<String> imgUrl;

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

    private String mobile;

    private String oldMobile;

    private String rownum;

    private String rejectReason;

    private String appealStatus;

    private static final long serialVersionUID = 1L;

    /**
     * 申诉记录
     * @param
     * @return
     */
    public Object[] toData1(boolean isAll, List<String> fileds) {
        String mobile = getMobile();
        if (isAll) {
            mobile = getOldMobile();
        }
        List met = new ArrayList();
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("username")){
            met.add(getUsername());
        }
        if(fileds.contains("mobile")){
            met.add(mobile);
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
        if(fileds.contains("handleTime")){
            met.add(getHandleTime());
        }
        if(fileds.contains("appealStatus")){
            met.add(getAppealStatus());
        }
        if(fileds.contains("rejectReason")){
            met.add(getRejectReason());
        }
        Object[] objects = met.toArray();
        return objects;
//        return new Object[] {
//                getRownum(), getUsername(), mobile, getProjectName(), getProblemDesc(), getImgUrl()
//        };
    }
    public String[] courtCaseTitle1 = new String[] {
            "序号", "申诉人", "联系方式", "项目", "申诉原因", "附件"
    };
}
