package cn.visolink.system.approval.dao;

import cn.visolink.system.approval.bo.ApprovalProcessConfigBO;
import cn.visolink.system.approval.bo.ApprovalProcessNodeBO;
import cn.visolink.system.approval.bo.ApprovalProcessQueryBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 审批流程配置DAO
 */
public interface ApprovalProcessConfigDao {

    /**
     * 保存审批流程配置
     */
    void save(ApprovalProcessConfigBO config);

    /**
     * 更新审批流程配置
     */
    void update(ApprovalProcessConfigBO config);

    /**
     * 根据ID查询审批流程配置
     */
    ApprovalProcessConfigBO getById(@Param("id") String id);

    /**
     * 查询审批流程配置列表
     */
    List<ApprovalProcessConfigBO> select(ApprovalProcessQueryBO query);

    /**
     * 根据流程类型和级别查询审批流程配置
     */
    ApprovalProcessConfigBO getByProcessTypeAndLevel(@Param("processType") String processType,
                                                     @Param("levelType") String levelType,
                                                     @Param("regionId") String regionId,
                                                     @Param("projectId") String projectId);

    /**
     * 删除审批流程配置
     */
    void deleteById(@Param("id") String id);

    /**
     * 批量删除审批流程配置
     */
    void batchDelete(@Param("ids") List<String> ids);

    /**
     * 保存审批流程节点
     */
    void saveNode(ApprovalProcessNodeBO node);

    /**
     * 更新审批流程节点
     */
    void updateNode(ApprovalProcessNodeBO node);

    /**
     * 根据流程配置ID查询节点列表
     */
    List<ApprovalProcessNodeBO> getNodesByConfigId(@Param("processConfigId") String processConfigId);

    /**
     * 删除流程配置下的所有节点
     */
    void deleteNodesByConfigId(@Param("processConfigId") String processConfigId);

    /**
     * 根据ID查询节点
     */
    ApprovalProcessNodeBO getNodeById(@Param("id") String id);

    /**
     * 删除节点
     */
    void deleteNodeById(@Param("id") String id);

    /**
     * 检查流程配置是否存在
     */
    int checkExists(@Param("processType") String processType,
                    @Param("levelType") String levelType,
                    @Param("regionId") String regionId,
                    @Param("projectId") String projectId,
                    @Param("excludeId") String excludeId);
}
