package cn.visolink.system.approval.controller;


import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.approval.bo.ApprovalProcessConfigBO;
import cn.visolink.system.approval.bo.ApprovalProcessNodeBO;
import cn.visolink.system.approval.bo.ApprovalProcessQueryBO;
import cn.visolink.system.approval.service.ApprovalProcessConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批流程配置Controller
 */
@RestController
@RequestMapping("/approval/process")
@Api(tags = "审批流程配置")
public class ApprovalProcessConfigController {

    @Autowired
    private ApprovalProcessConfigService approvalProcessConfigService;

    @Log("保存审批流程配置")
    @PostMapping("/save")
    @ApiOperation(value = "保存审批流程配置", notes = "保存审批流程配置")
    public ResultBody save(@RequestBody ApprovalProcessConfigBO config) {
        return approvalProcessConfigService.save(config);
    }

    @Log("更新审批流程配置")
    @PostMapping("/update")
    @ApiOperation(value = "更新审批流程配置", notes = "更新审批流程配置")
    public ResultBody update(@RequestBody ApprovalProcessConfigBO config) {
        return approvalProcessConfigService.update(config);
    }

    @Log("查询审批流程配置")
    @PostMapping("/getById")
    @ApiOperation(value = "根据ID查询审批流程配置", notes = "根据ID查询审批流程配置")
    public ResultBody getById(@RequestParam String id) {
        return approvalProcessConfigService.getById(id);
    }

    @Log("查询审批流程配置列表")
    @PostMapping("/select")
    @ApiOperation(value = "查询审批流程配置列表", notes = "查询审批流程配置列表")
    public ResultBody select(@RequestBody ApprovalProcessQueryBO query) {
        return approvalProcessConfigService.select(query);
    }

    @Log("删除审批流程配置")
    @PostMapping("/delete")
    @ApiOperation(value = "删除审批流程配置", notes = "删除审批流程配置")
    public ResultBody delete(@RequestParam String id) {
        return approvalProcessConfigService.deleteById(id);
    }

    @Log("批量删除审批流程配置")
    @PostMapping("/batchDelete")
    @ApiOperation(value = "批量删除审批流程配置", notes = "批量删除审批流程配置")
    public ResultBody batchDelete(@RequestBody List<String> ids) {
        return approvalProcessConfigService.batchDelete(ids);
    }

    @Log("启用/禁用审批流程")
    @PostMapping("/toggleEnabled")
    @ApiOperation(value = "启用/禁用审批流程", notes = "启用/禁用审批流程")
    public ResultBody toggleEnabled(@RequestParam String id, @RequestParam Integer isEnabled) {
        return approvalProcessConfigService.toggleEnabled(id, isEnabled);
    }

    @Log("复制审批流程配置")
    @PostMapping("/copy")
    @ApiOperation(value = "复制审批流程配置", notes = "复制审批流程配置")
    public ResultBody copy(@RequestParam String sourceId,
                          @RequestParam String targetProcessType,
                          @RequestParam String targetLevelType,
                          @RequestParam(required = false) String targetRegionId,
                          @RequestParam(required = false) String targetProjectId) {
        return approvalProcessConfigService.copy(sourceId, targetProcessType, targetLevelType, targetRegionId, targetProjectId);
    }

    @Log("清空所有流程配置")
    @PostMapping("/clearAll")
    @ApiOperation(value = "清空所有流程配置", notes = "清空所有流程配置")
    public ResultBody clearAll() {
        return approvalProcessConfigService.clearAll();
    }

    @Log("保存审批流程节点")
    @PostMapping("/saveNode")
    @ApiOperation(value = "保存审批流程节点", notes = "保存审批流程节点")
    public ResultBody saveNode(@RequestBody ApprovalProcessNodeBO node) {
        return approvalProcessConfigService.saveNode(node);
    }

    @Log("更新审批流程节点")
    @PostMapping("/updateNode")
    @ApiOperation(value = "更新审批流程节点", notes = "更新审批流程节点")
    public ResultBody updateNode(@RequestBody ApprovalProcessNodeBO node) {
        return approvalProcessConfigService.updateNode(node);
    }

    @Log("删除审批流程节点")
    @PostMapping("/deleteNode")
    @ApiOperation(value = "删除审批流程节点", notes = "删除审批流程节点")
    public ResultBody deleteNode(@RequestParam String id) {
        return approvalProcessConfigService.deleteNode(id);
    }

    @Log("查询审批流程节点")
    @PostMapping("/getNodesByConfigId")
    @ApiOperation(value = "根据流程配置ID查询节点列表", notes = "根据流程配置ID查询节点列表")
    public ResultBody getNodesByConfigId(@RequestParam String processConfigId) {
        return approvalProcessConfigService.getNodesByConfigId(processConfigId);
    }
}
