package cn.visolink.system.channel.model.form;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @TableName s_user_org_rel
 */
@Data
public class UserOrgRelForm implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 申请人id
     */
    private String userId;

    /**
     * 申请人名称
     */
    private String userName;

    /**
     * 权限集合
     */
    private String orgId;
    private List<String> orgIdList;

    /**
     * 权限集合名称
     */
    private String orgName;
    private List<String> orgNameList;

    /**
     * 岗位编码
     */
    private String jobCode;

    /**
     * 岗位名称
     */
    private String jobName;

    /**
     * 有效期开始时间
     */
    private String startTime;

    /**
     * 有效期结束时间
     */
    private String endTime;

    /**
     * 有效期类型（0 有效期 0 不限时间 ）
     */
    private Integer isNoTime;

    /**
     * 全号/隐号 展示客户名称（0 隐号 1 全号）
     */
    private Integer isNameShow;

    /**
     * 全号/隐号 展示联系方式（0 隐号 1 全号）
     */
    private Integer isMobileShow;

    /**
     * 审批状态（0 草稿 1 审批中 2 审批通过 4 审批驳回）
     */
    private String approveStatus;

    /**
     * 审批时间
     */
    private String approveTime;

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
     * 是否删除
     */
    private Integer isdel;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 外部流程系统id
     */
    private String processId;

    /**
     * 审批结果
     */
    private String approveDesc;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 申请原因
     */
    private String applyReason;

    private String comGUID;

    private String userMobile;

    private List<String> orgList;
    private List<String> proList;

    private String type;

    private static final long serialVersionUID = 1L;
}