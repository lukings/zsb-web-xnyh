package cn.visolink.system.message.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.message.dao.SysUpdateMessageMapper;
import cn.visolink.system.message.entity.SysUpdateMessage;
import cn.visolink.system.message.service.SysUpdateMessageService;
import cn.visolink.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 系统更新消息服务实现类
 */
@Service
public class SysUpdateMessageServiceImpl extends ServiceImpl<SysUpdateMessageMapper, SysUpdateMessage> implements SysUpdateMessageService {

    @Autowired
    private SysUpdateMessageMapper sysUpdateMessageMapper;

    @Override
    public ResultBody<IPage<SysUpdateMessage>> getMessagePage(Page<SysUpdateMessage> page, Map<String, Object> params) {
        try {
            IPage<SysUpdateMessage> result = sysUpdateMessageMapper.selectMessagePage(page, params);
            return ResultBody.success(result);
        } catch (Exception e) {
            return ResultBody.error(500, "查询系统更新消息失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody<String> createMessage(SysUpdateMessage message) {
        try {
            // 参数校验
            if (!StringUtils.hasText(message.getTitle())) {
                return ResultBody.error(400, "消息标题不能为空");
            }
            if (!StringUtils.hasText(message.getContent())) {
                return ResultBody.error(400, "消息内容不能为空");
            }

            // 设置创建信息
            message.setId(UUID.randomUUID().toString());
            message.setCreator(SecurityUtils.getUserId());
            message.setCreatorName(SecurityUtils.getEmployeeName());
            message.setCreateTime(new Date());
            message.setStatus(1);
            message.setIsDeleted(0);

            // 保存消息
            this.save(message);

            return ResultBody.success("创建系统更新消息成功");
        } catch (Exception e) {
            return ResultBody.error(500, "创建系统更新消息失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody<String> updateMessage(SysUpdateMessage message) {
        try {
            // 参数校验
            if (!StringUtils.hasText(message.getId())) {
                return ResultBody.error(400, "消息ID不能为空");
            }

            // 设置更新信息
            message.setUpdator(SecurityUtils.getUserId());
            message.setUpdatorName(SecurityUtils.getEmployeeName());
            message.setUpdateTime(new Date());

            // 更新消息
            this.updateById(message);

            return ResultBody.success("更新系统更新消息成功");
        } catch (Exception e) {
            return ResultBody.error(500, "更新系统更新消息失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody<String> deleteMessage(String messageId) {
        try {
            if (!StringUtils.hasText(messageId)) {
                return ResultBody.error(400, "消息ID不能为空");
            }

            // 逻辑删除
            SysUpdateMessage message = new SysUpdateMessage();
            message.setId(messageId);
            message.setIsDeleted(1);
            message.setUpdator(SecurityUtils.getUserId());
            message.setUpdatorName(SecurityUtils.getEmployeeName());
            message.setUpdateTime(new Date());

            this.updateById(message);

            return ResultBody.success("删除系统更新消息成功");
        } catch (Exception e) {
            return ResultBody.error(500, "删除系统更新消息失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody<String> batchDeleteMessages(List<String> messageIds) {
        try {
            if (CollectionUtils.isEmpty(messageIds)) {
                return ResultBody.error(400, "消息ID列表不能为空");
            }

            // 批量逻辑删除
            List<SysUpdateMessage> messages = new ArrayList<>();
            for (String messageId : messageIds) {
                SysUpdateMessage message = new SysUpdateMessage();
                message.setId(messageId);
                message.setIsDeleted(1);
                message.setUpdator(SecurityUtils.getUserId());
                message.setUpdatorName(SecurityUtils.getEmployeeName());
                message.setUpdateTime(new Date());
                messages.add(message);
            }

            this.updateBatchById(messages);

            return ResultBody.success("批量删除系统更新消息成功");
        } catch (Exception e) {
            return ResultBody.error(500, "批量删除系统更新消息失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody<List<SysUpdateMessage>> getUnreadMessagesByUserId(String userId) {
        try {
            if (!StringUtils.hasText(userId)) {
                return ResultBody.error(400, "用户ID不能为空");
            }

            List<SysUpdateMessage> messages = sysUpdateMessageMapper.selectUnreadMessagesByUserId(userId);
            return ResultBody.success(messages);
        } catch (Exception e) {
            return ResultBody.error(500, "获取用户未读消息失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody<List<SysUpdateMessage>> getPopupMessagesByUserId(String userId) {
        try {
            if (!StringUtils.hasText(userId)) {
                return ResultBody.error(400, "用户ID不能为空");
            }

            List<SysUpdateMessage> messages = sysUpdateMessageMapper.selectPopupMessagesByUserId(userId);
            return ResultBody.success(messages);
        } catch (Exception e) {
            return ResultBody.error(500, "获取弹框消息失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody<String> markMessageAsRead(String messageId, String userId) {
        try {
            if (!StringUtils.hasText(messageId) || !StringUtils.hasText(userId)) {
                return ResultBody.error(400, "消息ID和用户ID不能为空");
            }

            // 更新已读状态
            int result = sysUpdateMessageMapper.updateMessageReadStatus(messageId, userId);
            if (result == 0) {
                // 如果没有记录，则插入新记录
                List<Map<String, Object>> readRecords = new ArrayList<>();
                Map<String, Object> record = new HashMap<>();
                record.put("id", UUID.randomUUID().toString());
                record.put("messageId", messageId);
                record.put("userId", userId);
                record.put("userName", SecurityUtils.getEmployeeName());
                record.put("isRead", 1);
                record.put("readTime", new Date());
                readRecords.add(record);
                sysUpdateMessageMapper.batchInsertMessageRead(readRecords);
            }

            return ResultBody.success("标记消息为已读成功");
        } catch (Exception e) {
            return ResultBody.error(500, "标记消息为已读失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody<String> batchMarkMessagesAsRead(List<String> messageIds, String userId) {
        try {
            if (CollectionUtils.isEmpty(messageIds) || !StringUtils.hasText(userId)) {
                return ResultBody.error(400, "消息ID列表和用户ID不能为空");
            }

            for (String messageId : messageIds) {
                markMessageAsRead(messageId, userId);
            }

            return ResultBody.success("批量标记消息为已读成功");
        } catch (Exception e) {
            return ResultBody.error(500, "批量标记消息为已读失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody<List<Map<String, Object>>> getMessageReadUsers(String messageId) {
        try {
            if (!StringUtils.hasText(messageId)) {
                return ResultBody.error(400, "消息ID不能为空");
            }

            List<Map<String, Object>> users = sysUpdateMessageMapper.selectMessageReadUsers(messageId);
            return ResultBody.success(users);
        } catch (Exception e) {
            return ResultBody.error(500, "获取已读用户列表失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody<List<Map<String, Object>>> getMessageUnreadUsers(String messageId) {
        try {
            if (!StringUtils.hasText(messageId)) {
                return ResultBody.error(400, "消息ID不能为空");
            }

            List<Map<String, Object>> users = sysUpdateMessageMapper.selectMessageUnreadUsers(messageId);
            return ResultBody.success(users);
        } catch (Exception e) {
            return ResultBody.error(500, "获取未读用户列表失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody<Map<String, Object>> getMessageReadStatus(String messageId) {
        try {
            if (!StringUtils.hasText(messageId)) {
                return ResultBody.error(400, "消息ID不能为空");
            }

            Map<String, Object> status = sysUpdateMessageMapper.countMessageReadStatus(messageId);
            return ResultBody.success(status);
        } catch (Exception e) {
            return ResultBody.error(500, "获取消息阅读状态失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody<String> batchSendMessageToUsers(String messageId, List<String> userIds) {
        try {
            if (!StringUtils.hasText(messageId) || CollectionUtils.isEmpty(userIds)) {
                return ResultBody.error(400, "消息ID和用户ID列表不能为空");
            }

            // 获取消息信息
            SysUpdateMessage message = this.getById(messageId);
            if (message == null) {
                return ResultBody.error(404, "消息不存在");
            }

            // 更新目标用户
            String targetUsers = String.join(",", userIds);
            message.setTargetType(2);
            message.setTargetUsers(targetUsers);
            message.setUpdator(SecurityUtils.getUserId());
            message.setUpdatorName(SecurityUtils.getEmployeeName());
            message.setUpdateTime(new Date());

            this.updateById(message);

            return ResultBody.success("批量下发消息给指定用户成功");
        } catch (Exception e) {
            return ResultBody.error(500, "批量下发消息失败：" + e.getMessage());
        }
    }


    @Override
    public ResultBody<SysUpdateMessage> getMessageById(String messageId) {
        try {
            if (!StringUtils.hasText(messageId)) {
                return ResultBody.error(400, "消息ID不能为空");
            }

            SysUpdateMessage message = this.getById(messageId);
            if (message == null) {
                return ResultBody.error(404, "消息不存在");
            }

            return ResultBody.success(message);
        } catch (Exception e) {
            return ResultBody.error(500, "获取消息详情失败：" + e.getMessage());
        }
    }
}
