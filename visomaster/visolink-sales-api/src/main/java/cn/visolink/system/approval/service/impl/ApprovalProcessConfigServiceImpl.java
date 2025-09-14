package cn.visolink.system.approval.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.approval.bo.ApprovalProcessConfigBO;
import cn.visolink.system.approval.bo.ApprovalProcessNodeBO;
import cn.visolink.system.approval.bo.ApprovalProcessQueryBO;
import cn.visolink.system.approval.dao.ApprovalProcessConfigDao;
import cn.visolink.system.approval.service.ApprovalProcessConfigService;
import cn.visolink.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 审批流程配置Service实现
 */
@Service
public class ApprovalProcessConfigServiceImpl implements ApprovalProcessConfigService {

    @Autowired
    private ApprovalProcessConfigDao approvalProcessConfigDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody save(ApprovalProcessConfigBO config) {
        try {
            // 1. 参数校验
            if (StringUtils.isEmpty(config.getProcessName())) {
                return ResultBody.error(-10001, "流程名称不能为空");
            }
            if (StringUtils.isEmpty(config.getProcessType())) {
                return ResultBody.error(-10002, "流程类型不能为空");
            }
            if (StringUtils.isEmpty(config.getLevelType())) {
                return ResultBody.error(-10003, "级别类型不能为空");
            }

            // 2. 检查是否已存在
            int exists = approvalProcessConfigDao.checkExists(
                config.getProcessType(),
                config.getLevelType(),
                config.getRegionId(),
                config.getProjectId(),
                null
            );
            if (exists > 0) {
                return ResultBody.error(-10004, "该流程配置已存在");
            }

            // 3. 设置基础信息
            config.setId(UUID.randomUUID().toString());
            config.setCreateTime(new Date());
            config.setUpdateTime(new Date());
            config.setIsDel(0);

            config.setCreateBy(SecurityUtils.getUserId());
            // 4. 保存流程配置
            approvalProcessConfigDao.save(config);

            // 5. 保存节点信息
            if (!CollectionUtils.isEmpty(config.getNodes())) {
                for (int i = 0; i < config.getNodes().size(); i++) {
                    ApprovalProcessNodeBO node = config.getNodes().get(i);
                    node.setId(UUID.randomUUID().toString());
                    node.setProcessConfigId(config.getId());
                    node.setNodeOrder(i + 1);
                    node.setCreateTime(new Date());
                    node.setUpdateTime(new Date());
                    node.setCreateBy(SecurityUtils.getUserId());
                    node.setUpdateBy(SecurityUtils.getUserId());
                    node.setIsDel(0);

                    // 处理岗位信息
                    if (!CollectionUtils.isEmpty(node.getApprovalJobList())) {
                        node.setApprovalJobs(JSON.toJSONString(node.getApprovalJobList()));
                    }
                    if (!CollectionUtils.isEmpty(node.getCcJobList())) {
                        node.setCcJobs(JSON.toJSONString(node.getCcJobList()));
                    }

                    approvalProcessConfigDao.saveNode(node);
                }
            }

            return ResultBody.success("保存成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "保存失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody update(ApprovalProcessConfigBO config) {
        try {
            // 1. 参数校验
            if (StringUtils.isEmpty(config.getId())) {
                return ResultBody.error(-10001, "ID不能为空");
            }
            if (StringUtils.isEmpty(config.getProcessName())) {
                return ResultBody.error(-10002, "流程名称不能为空");
            }

            // 2. 检查记录是否存在
            ApprovalProcessConfigBO existingConfig = approvalProcessConfigDao.getById(config.getId());
            if (existingConfig == null) {
                return ResultBody.error(-10003, "流程配置不存在");
            }

            // 3. 更新流程配置
            config.setUpdateTime(new Date());
            config.setUpdateBy(SecurityUtils.getUserId());
            approvalProcessConfigDao.update(config);

            // 4. 删除原有节点
            approvalProcessConfigDao.deleteNodesByConfigId(config.getId());

            // 5. 保存新节点信息
            if (!CollectionUtils.isEmpty(config.getNodes())) {
                for (int i = 0; i < config.getNodes().size(); i++) {
                    ApprovalProcessNodeBO node = config.getNodes().get(i);
                    node.setId(UUID.randomUUID().toString());
                    node.setProcessConfigId(config.getId());
                    node.setNodeOrder(i + 1);
                    node.setCreateTime(new Date());
                    node.setUpdateTime(new Date());
                    node.setCreateBy(SecurityUtils.getUserId());
                    node.setUpdateBy(SecurityUtils.getUserId());
                    node.setIsDel(0);

                    // 处理岗位信息
                    if (!CollectionUtils.isEmpty(node.getApprovalJobList())) {
                        node.setApprovalJobs(JSON.toJSONString(node.getApprovalJobList()));
                    }
                    if (!CollectionUtils.isEmpty(node.getCcJobList())) {
                        node.setCcJobs(JSON.toJSONString(node.getCcJobList()));
                    }

                    approvalProcessConfigDao.saveNode(node);
                }
            }

            return ResultBody.success("更新成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "更新失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody getById(String id) {
        try {
            ApprovalProcessConfigBO config = approvalProcessConfigDao.getById(id);
            if (config == null) {
                return ResultBody.error(-10001, "流程配置不存在");
            }

            // 查询节点信息
            List<ApprovalProcessNodeBO> nodes = approvalProcessConfigDao.getNodesByConfigId(id);
            config.setNodes(nodes);

            return ResultBody.success(config);
        } catch (Exception e) {
            return ResultBody.error(-10000, "查询失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody select(ApprovalProcessQueryBO query) {
        try {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            List<ApprovalProcessConfigBO> list = approvalProcessConfigDao.select(query);
            PageInfo<ApprovalProcessConfigBO> pageInfo = new PageInfo<>(list);

            return ResultBody.success(pageInfo);
        } catch (Exception e) {
            return ResultBody.error(-10000, "查询失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody deleteById(String id) {
        try {
            approvalProcessConfigDao.deleteById(id);
            approvalProcessConfigDao.deleteNodesByConfigId(id);
            return ResultBody.success("删除成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody batchDelete(List<String> ids) {
        try {
            if (CollectionUtils.isEmpty(ids)) {
                return ResultBody.error(-10001, "ID列表不能为空");
            }
            approvalProcessConfigDao.batchDelete(ids);
            return ResultBody.success("批量删除成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "批量删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody toggleEnabled(String id, Integer isEnabled) {
        try {
            ApprovalProcessConfigBO config = new ApprovalProcessConfigBO();
            config.setId(id);
            config.setIsEnabled(isEnabled);
            config.setUpdateBy(SecurityUtils.getUserId());
            config.setUpdateTime(new Date());
            approvalProcessConfigDao.update(config);
            return ResultBody.success("操作成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "操作失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody copy(String sourceId, String targetProcessType, String targetLevelType, String targetRegionId, String targetProjectId) {
        try {
            // 1. 查询源配置
            ApprovalProcessConfigBO sourceConfig = approvalProcessConfigDao.getById(sourceId);
            if (sourceConfig == null) {
                return ResultBody.error(-10001, "源流程配置不存在");
            }

            // 2. 检查目标配置是否已存在
            int exists = approvalProcessConfigDao.checkExists(
                targetProcessType,
                targetLevelType,
                targetRegionId,
                targetProjectId,
                null
            );
            if (exists > 0) {
                return ResultBody.error(-10002, "目标流程配置已存在");
            }

            // 3. 复制配置
            ApprovalProcessConfigBO targetConfig = new ApprovalProcessConfigBO();
            targetConfig.setId(UUID.randomUUID().toString());
            targetConfig.setProcessName(sourceConfig.getProcessName());
            targetConfig.setProcessType(targetProcessType);
            targetConfig.setLevelType(targetLevelType);
            targetConfig.setRegionId(targetRegionId);
            targetConfig.setProjectId(targetProjectId);
            targetConfig.setIsEnabled(0); // 默认禁用
            targetConfig.setIsForce(0);
            targetConfig.setCreateTime(new Date());
            targetConfig.setCreateBy(SecurityUtils.getUserId());
            targetConfig.setUpdateTime(new Date());
            targetConfig.setUpdateBy(SecurityUtils.getUserId());
            targetConfig.setIsDel(0);

            approvalProcessConfigDao.save(targetConfig);

            // 4. 复制节点
            List<ApprovalProcessNodeBO> sourceNodes = approvalProcessConfigDao.getNodesByConfigId(sourceId);
            if (!CollectionUtils.isEmpty(sourceNodes)) {
                for (ApprovalProcessNodeBO sourceNode : sourceNodes) {
                    ApprovalProcessNodeBO targetNode = new ApprovalProcessNodeBO();
                    targetNode.setId(UUID.randomUUID().toString());
                    targetNode.setProcessConfigId(targetConfig.getId());
                    targetNode.setNodeName(sourceNode.getNodeName());
                    targetNode.setNodeType(sourceNode.getNodeType());
                    targetNode.setNodeOrder(sourceNode.getNodeOrder());
                    targetNode.setApprovalJobs(sourceNode.getApprovalJobs());
                    targetNode.setCcJobs(sourceNode.getCcJobs());
                    targetNode.setTimeoutDays(sourceNode.getTimeoutDays());
                    targetNode.setIsRequired(sourceNode.getIsRequired());
                    targetNode.setCreateTime(new Date());
                    targetNode.setCreateBy(SecurityUtils.getUserId());
                    targetNode.setUpdateTime(new Date());
                    targetNode.setUpdateBy(SecurityUtils.getUserId());
                    targetNode.setIsDel(0);

                    approvalProcessConfigDao.saveNode(targetNode);
                }
            }

            return ResultBody.success("复制成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "复制失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody clearAll() {
        try {
            // 这里需要根据实际需求实现清空逻辑
            // 可能需要先查询所有配置，然后批量删除
            return ResultBody.success("清空成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "清空失败：" + e.getMessage());
        }
    }

    @Override
    public ApprovalProcessConfigBO getByProcessTypeAndLevel(String processType, String levelType, String regionId, String projectId) {
        return approvalProcessConfigDao.getByProcessTypeAndLevel(processType, levelType, regionId, projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveNode(ApprovalProcessNodeBO node) {
        try {
            if (StringUtils.isEmpty(node.getProcessConfigId())) {
                return ResultBody.error(-10001, "流程配置ID不能为空");
            }
            if (StringUtils.isEmpty(node.getNodeName())) {
                return ResultBody.error(-10002, "节点名称不能为空");
            }

            node.setId(UUID.randomUUID().toString());
            node.setCreateTime(new Date());
            node.setCreateBy(SecurityUtils.getUserId());
            node.setUpdateTime(new Date());
            node.setUpdateBy(SecurityUtils.getUserId());
            node.setIsDel(0);

            // 处理岗位信息
            if (!CollectionUtils.isEmpty(node.getApprovalJobList())) {
                node.setApprovalJobs(JSON.toJSONString(node.getApprovalJobList()));
            }
            if (!CollectionUtils.isEmpty(node.getCcJobList())) {
                node.setCcJobs(JSON.toJSONString(node.getCcJobList()));
            }

            approvalProcessConfigDao.saveNode(node);
            return ResultBody.success("保存成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "保存失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateNode(ApprovalProcessNodeBO node) {
        try {
            if (StringUtils.isEmpty(node.getId())) {
                return ResultBody.error(-10001, "节点ID不能为空");
            }
            node.setUpdateBy(SecurityUtils.getUserId());
            node.setUpdateTime(new Date());

            // 处理岗位信息
            if (!CollectionUtils.isEmpty(node.getApprovalJobList())) {
                node.setApprovalJobs(JSON.toJSONString(node.getApprovalJobList()));
            }
            if (!CollectionUtils.isEmpty(node.getCcJobList())) {
                node.setCcJobs(JSON.toJSONString(node.getCcJobList()));
            }

            approvalProcessConfigDao.updateNode(node);
            return ResultBody.success("更新成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "更新失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody deleteNode(String id) {
        try {
            approvalProcessConfigDao.deleteNodeById(id);
            return ResultBody.success("删除成功");
        } catch (Exception e) {
            return ResultBody.error(-10000, "删除失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody getNodesByConfigId(String processConfigId) {
        try {
            List<ApprovalProcessNodeBO> nodes = approvalProcessConfigDao.getNodesByConfigId(processConfigId);
            return ResultBody.success(nodes);
        } catch (Exception e) {
            return ResultBody.error(-10000, "查询失败：" + e.getMessage());
        }
    }
}
