package cn.visolink.system.channel.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 跟进记录核验表
 * @TableName b_followup_verification_record
 */
@Data
public class FollowupVerificationRecordVo implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 跟进记录表
     */
    private String followRecordId;

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
     * 整改状态 0 无需整改 1 待整改 2 待复核 3 已整改
     */
    private String rectificationStatus;

    /**
     * 核验状态 0 不合格 1 合格
     */
    private String verificationStatus;

    /**
     * 核验结果(0 未核验 1 核验合格 2 核验不合格)
     */
    private String verificationResult;

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
     * 发起人
     */
    private String userId;

    /**
     * 核验合格类型
     */
    private String type;

    /**
     * 版本号
     */
    private String versionNum;

    /**
     * 人员名称
     */
    private String employeeName;

    /**
     * 跟进核验整改记录
     */
    private List<FollowupRectificationRecordVo> followupRectificationRecordVoList;

    private static final long serialVersionUID = 1L;
}