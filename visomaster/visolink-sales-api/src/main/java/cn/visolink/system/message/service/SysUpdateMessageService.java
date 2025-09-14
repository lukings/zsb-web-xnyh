package cn.visolink.system.message.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.message.entity.SysUpdateMessage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 系统更新消息服务接口
 */
public interface SysUpdateMessageService extends IService<SysUpdateMessage> {

    /**
     * 分页查询系统更新消息
     */
    ResultBody<IPage<SysUpdateMessage>> getMessagePage(Page<SysUpdateMessage> page, Map<String, Object> params);

    /**
     * 创建系统更新消息
     */
    ResultBody<String> createMessage(SysUpdateMessage message);

    /**
     * 更新系统更新消息
     */
    ResultBody<String> updateMessage(SysUpdateMessage message);

    /**
     * 删除系统更新消息
     */
    ResultBody<String> deleteMessage(String messageId);

    /**
     * 批量删除系统更新消息
     */
    ResultBody<String> batchDeleteMessages(List<String> messageIds);

    /**
     * 获取用户未读的系统更新消息
     */
    ResultBody<List<SysUpdateMessage>> getUnreadMessagesByUserId(String userId);

    /**
     * 获取需要弹框提醒的消息
     */
    ResultBody<List<SysUpdateMessage>> getPopupMessagesByUserId(String userId);

    /**
     * 标记消息为已读
     */
    ResultBody<String> markMessageAsRead(String messageId, String userId);

    /**
     * 批量标记消息为已读
     */
    ResultBody<String> batchMarkMessagesAsRead(List<String> messageIds, String userId);

    /**
     * 获取消息的已读用户列表
     */
    ResultBody<List<Map<String, Object>>> getMessageReadUsers(String messageId);

    /**
     * 获取消息的未读用户列表
     */
    ResultBody<List<Map<String, Object>>> getMessageUnreadUsers(String messageId);

    /**
     * 统计消息的已读/未读数量
     */
    ResultBody<Map<String, Object>> getMessageReadStatus(String messageId);

    /**
     * 批量下发消息给指定用户
     */
    ResultBody<String> batchSendMessageToUsers(String messageId, List<String> userIds);


    /**
     * 获取消息详情
     */
    ResultBody<SysUpdateMessage> getMessageById(String messageId);
}
