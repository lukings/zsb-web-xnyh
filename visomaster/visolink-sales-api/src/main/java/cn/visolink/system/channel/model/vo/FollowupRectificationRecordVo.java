package cn.visolink.system.channel.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 跟进记录整改表
 * @TableName b_followup_rectification_record
 */
@Data
public class FollowupRectificationRecordVo implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 跟进记录表
     */
    private String followRecordId;

    /**
     * 线索客户ID
     */
    private String projectClueId;

    /**
     * 机会客户ID
     */
    private String opportunityClueId;

    /**
     * 跟进核验记录表
     */
    private String followVerificationRecordId;

    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 跟进描述
     */
    private String description;

    /**
     * 审批意见
     */
    private String approvalReason;

    /**
     * 状态 0 禁用 1启用
     */
    private String status;

    /**
     * 是否删除 0否1是
     */
    private String isDel;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 修改人
     */
    private String updator;

    /**
     * 附件
     */
    private List<String> enclosures;

    /**
     * 三个一图片路径
     */
    private List<String> threeOnesUrl;

    /**
     * 图纸报价路径
     */
    private List<String> drawingQuotationUrl;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 整改是否通过 1 通过 2 驳回
     */
    private String type;

    /**
     * 审核状态
     */
    private String approvalStatus;

    /**
     * 审核时间
     */
    private String approvalTime;

    /**
     * 人员名称
     */
    private String employeeName;

    /**
     * 核验状态
     */
    private String verificationStatus;

    /**
     * 整改状态
     */
    private String rectificationStatus;

    /**
     * 核验审核意见
     */
    private String verificationApprovalReason;

    /**
     * 核验发起时间
     */
    private String verificationCreateTime;

    /**
     * 核验人员
     */
    private String verificationmUserName;

    private static final long serialVersionUID = 1L;
}