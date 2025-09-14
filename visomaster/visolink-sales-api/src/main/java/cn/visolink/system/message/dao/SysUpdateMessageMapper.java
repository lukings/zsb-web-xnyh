package cn.visolink.system.message.dao;

import cn.visolink.system.message.entity.SysUpdateMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 系统更新消息Mapper接口
 */
@Mapper
public interface SysUpdateMessageMapper extends BaseMapper<SysUpdateMessage> {

    /**
     * 分页查询系统更新消息
     */
    IPage<SysUpdateMessage> selectMessagePage(Page<SysUpdateMessage> page, @Param("params") Map<String, Object> params);

    /**
     * 查询用户未读的系统更新消息
     */
    List<SysUpdateMessage> selectUnreadMessagesByUserId(@Param("userId") String userId);

    /**
     * 查询需要弹框提醒的消息
     */
    List<SysUpdateMessage> selectPopupMessagesByUserId(@Param("userId") String userId);

    /**
     * 批量插入消息已读记录
     */
    int batchInsertMessageRead(@Param("list") List<Map<String, Object>> list);

    /**
     * 更新消息已读状态
     */
    int updateMessageReadStatus(@Param("messageId") String messageId, @Param("userId") String userId);

    /**
     * 查询消息的已读用户列表
     */
    List<Map<String, Object>> selectMessageReadUsers(@Param("messageId") String messageId);

    /**
     * 查询消息的未读用户列表
     */
    List<Map<String, Object>> selectMessageUnreadUsers(@Param("messageId") String messageId);

    /**
     * 统计消息的已读/未读数量
     */
    Map<String, Object> countMessageReadStatus(@Param("messageId") String messageId);
}
