package cn.visolink.system.approval.service;

import cn.visolink.exception.ResultBody;

/**
 * 审批流程执行服务
 */
public interface ApprovalProcessExecuteService {

    /**
     * 启动审批流程
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param processType 流程类型
     * @param applyUserId 申请人ID
     * @param applyUserName 申请人姓名
     * @param applyUserJob 申请人岗位
     * @return 审批流程记录ID
     */
    ResultBody startApprovalProcess(String businessId, String businessType, String processType,
                                   String applyUserId, String applyUserName, String applyUserJob);

    /**
     * 审批通过
     * @param processRecordId 流程记录ID
     * @param nodeRecordId 节点记录ID
     * @param approveUserId 审批人ID
     * @param approveUserName 审批人姓名
     * @param approveOpinion 审批意见
     * @return 结果
     */
    ResultBody approve(String processRecordId, String nodeRecordId, String approveUserId,
                      String approveUserName, String approveOpinion);

    /**
     * 审批驳回
     * @param processRecordId 流程记录ID
     * @param nodeRecordId 节点记录ID
     * @param approveUserId 审批人ID
     * @param approveUserName 审批人姓名
     * @param approveOpinion 审批意见
     * @return 结果
     */
    ResultBody reject(String processRecordId, String nodeRecordId, String approveUserId,
                     String approveUserName, String approveOpinion);

    /**
     * 获取待审批列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 待审批列表
     */
    ResultBody getPendingApprovalList(String userId, Integer pageNum, Integer pageSize);

    /**
     * 获取已审批列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 已审批列表
     */
    ResultBody getApprovedList(String userId, Integer pageNum, Integer pageSize);
}
