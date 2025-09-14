package cn.visolink.system.approval.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.approval.bo.ApprovalProcessConfigBO;
import cn.visolink.system.approval.bo.ApprovalProcessNodeBO;
import cn.visolink.system.approval.bo.ApprovalProcessQueryBO;

import java.util.List;

/**
 * 审批流程配置Service
 */
public interface ApprovalProcessConfigService {

    /**
     * 保存审批流程配置
     */
    ResultBody save(ApprovalProcessConfigBO config);

    /**
     * 更新审批流程配置
     */
    ResultBody update(ApprovalProcessConfigBO config);

    /**
     * 根据ID查询审批流程配置
     */
    ResultBody getById(String id);

    /**
     * 查询审批流程配置列表
     */
    ResultBody select(ApprovalProcessQueryBO query);

    /**
     * 删除审批流程配置
     */
    ResultBody deleteById(String id);

    /**
     * 批量删除审批流程配置
     */
    ResultBody batchDelete(List<String> ids);

    /**
     * 启用/禁用审批流程
     */
    ResultBody toggleEnabled(String id, Integer isEnabled);

    /**
     * 复制审批流程配置
     */
    ResultBody copy(String sourceId, String targetProcessType, String targetLevelType, String targetRegionId, String targetProjectId);

    /**
     * 清空所有流程配置
     */
    ResultBody clearAll();

    /**
     * 根据流程类型和级别获取审批流程配置
     */
    ApprovalProcessConfigBO getByProcessTypeAndLevel(String processType, String levelType, String regionId, String projectId);

    /**
     * 保存审批流程节点
     */
    ResultBody saveNode(ApprovalProcessNodeBO node);

    /**
     * 更新审批流程节点
     */
    ResultBody updateNode(ApprovalProcessNodeBO node);

    /**
     * 删除审批流程节点
     */
    ResultBody deleteNode(String id);

    /**
     * 根据流程配置ID查询节点列表
     */
    ResultBody getNodesByConfigId(String processConfigId);
}
