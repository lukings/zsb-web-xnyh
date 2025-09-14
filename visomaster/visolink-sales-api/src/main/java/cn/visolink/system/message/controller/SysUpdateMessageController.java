package cn.visolink.system.message.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.message.entity.SysUpdateMessage;
import cn.visolink.system.message.service.SysUpdateMessageService;
import cn.visolink.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统更新消息控制器
 */
@RestController
@RequestMapping("/api/sysUpdateMessage")
@Api(tags = "系统更新消息管理")
public class SysUpdateMessageController {

    @Autowired
    private SysUpdateMessageService sysUpdateMessageService;

    /**
     * 分页查询系统更新消息
     */
    @Log("分页查询系统更新消息")
    @ApiOperation(value = "分页查询系统更新消息")
    @GetMapping("/page")
    public ResultBody<IPage<SysUpdateMessage>> getMessagePage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("消息标题") @RequestParam(required = false) String title,
            @ApiParam("消息类型") @RequestParam(required = false) Integer messageType,
            @ApiParam("目标类型") @RequestParam(required = false) Integer targetType,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("开始时间") @RequestParam(required = false) String startTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {
        
        Page<SysUpdateMessage> page = new Page<>(pageNum, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("title", title != null ? title : "");
        params.put("messageType", messageType != null ? messageType.toString() : "");
        params.put("targetType", targetType != null ? targetType.toString() : "");
        params.put("status", status != null ? status.toString() : "");
        params.put("startTime", startTime != null ? startTime : "");
        params.put("endTime", endTime != null ? endTime : "");
        
        return sysUpdateMessageService.getMessagePage(page, params);
    }

    /**
     * 创建系统更新消息
     */
    @Log("创建系统更新消息")
    @ApiOperation(value = "创建系统更新消息")
    @PostMapping("/create")
    public ResultBody<String> createMessage(@RequestBody SysUpdateMessage message) {
        return sysUpdateMessageService.createMessage(message);
    }

    /**
     * 更新系统更新消息
     */
    @Log("更新系统更新消息")
    @ApiOperation(value = "更新系统更新消息")
    @PutMapping("/update")
    public ResultBody<String> updateMessage(@RequestBody SysUpdateMessage message) {
        return sysUpdateMessageService.updateMessage(message);
    }

    /**
     * 删除系统更新消息
     */
    @Log("删除系统更新消息")
    @ApiOperation(value = "删除系统更新消息")
    @DeleteMapping("/delete/{messageId}")
    public ResultBody<String> deleteMessage(@ApiParam("消息ID") @PathVariable String messageId) {
        return sysUpdateMessageService.deleteMessage(messageId);
    }

    /**
     * 批量删除系统更新消息
     */
    @Log("批量删除系统更新消息")
    @ApiOperation(value = "批量删除系统更新消息")
    @DeleteMapping("/batchDelete")
    public ResultBody<String> batchDeleteMessages(@RequestBody List<String> messageIds) {
        return sysUpdateMessageService.batchDeleteMessages(messageIds);
    }

    /**
     * 获取用户未读的系统更新消息
     */
    @Log("获取用户未读的系统更新消息")
    @ApiOperation(value = "获取用户未读的系统更新消息")
    @GetMapping("/unread")
    public ResultBody<List<SysUpdateMessage>> getUnreadMessages() {
        String userId = SecurityUtils.getUserId();
        return sysUpdateMessageService.getUnreadMessagesByUserId(userId);
    }

    /**
     * 获取需要弹框提醒的消息
     */
    @Log("获取需要弹框提醒的消息")
    @ApiOperation(value = "获取需要弹框提醒的消息")
    @GetMapping("/popup")
    public ResultBody<List<SysUpdateMessage>> getPopupMessages() {
        String userId = SecurityUtils.getUserId();
        return sysUpdateMessageService.getPopupMessagesByUserId(userId);
    }

    /**
     * 标记消息为已读
     */
    @Log("标记消息为已读")
    @ApiOperation(value = "标记消息为已读")
    @PutMapping("/markRead/{messageId}")
    public ResultBody<String> markMessageAsRead(@ApiParam("消息ID") @PathVariable String messageId) {
        String userId = SecurityUtils.getUserId();
        return sysUpdateMessageService.markMessageAsRead(messageId, userId);
    }

    /**
     * 批量标记消息为已读
     */
    @Log("批量标记消息为已读")
    @ApiOperation(value = "批量标记消息为已读")
    @PutMapping("/batchMarkRead")
    public ResultBody<String> batchMarkMessagesAsRead(@RequestBody List<String> messageIds) {
        String userId = SecurityUtils.getUserId();
        return sysUpdateMessageService.batchMarkMessagesAsRead(messageIds, userId);
    }

    /**
     * 获取消息的已读用户列表
     */
    @Log("获取消息的已读用户列表")
    @ApiOperation(value = "获取消息的已读用户列表")
    @GetMapping("/readUsers/{messageId}")
    public ResultBody<List<Map<String, Object>>> getMessageReadUsers(@ApiParam("消息ID") @PathVariable String messageId) {
        return sysUpdateMessageService.getMessageReadUsers(messageId);
    }

    /**
     * 获取消息的未读用户列表
     */
    @Log("获取消息的未读用户列表")
    @ApiOperation(value = "获取消息的未读用户列表")
    @GetMapping("/unreadUsers/{messageId}")
    public ResultBody<List<Map<String, Object>>> getMessageUnreadUsers(@ApiParam("消息ID") @PathVariable String messageId) {
        return sysUpdateMessageService.getMessageUnreadUsers(messageId);
    }

    /**
     * 统计消息的已读/未读数量
     */
    @Log("统计消息的已读/未读数量")
    @ApiOperation(value = "统计消息的已读/未读数量")
    @GetMapping("/readStatus/{messageId}")
    public ResultBody<Map<String, Object>> getMessageReadStatus(@ApiParam("消息ID") @PathVariable String messageId) {
        return sysUpdateMessageService.getMessageReadStatus(messageId);
    }

    /**
     * 批量下发消息给指定用户
     */
    @Log("批量下发消息给指定用户")
    @ApiOperation(value = "批量下发消息给指定用户")
    @PostMapping("/batchSendToUsers")
    public ResultBody<String> batchSendMessageToUsers(
            @ApiParam("消息ID") @RequestParam String messageId,
            @RequestBody List<String> userIds) {
        return sysUpdateMessageService.batchSendMessageToUsers(messageId, userIds);
    }


    /**
     * 获取消息详情
     */
    @Log("获取消息详情")
    @ApiOperation(value = "获取消息详情")
    @GetMapping("/detail/{messageId}")
    public ResultBody<SysUpdateMessage> getMessageById(@ApiParam("消息ID") @PathVariable String messageId) {
        return sysUpdateMessageService.getMessageById(messageId);
    }
}
