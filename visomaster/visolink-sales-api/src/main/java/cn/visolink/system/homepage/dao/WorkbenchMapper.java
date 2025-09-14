package cn.visolink.system.homepage.dao;

import cn.visolink.message.model.form.MessageForm;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WorkbenchMapper {
    /**
     * 获取登录人的岗位权限
     * */
    List<Map> findUserJobAllInfo(Map map);

    /**
     * 获取所有待办
     * */
    List<Map> getPendingList(Map map);

    /**
     * 获取所有待办-线索跟进
     * */
    List<Map> getPendingListClue(Map map);

    /**
     * 获取所有已办
     * */
    List<Map> getPendingOkList(Map map);

    /**
     * 获取所有已办-线索跟进
     * */
    List<Map> getPendingOkListClue(Map map);

    List<MessageForm> getMessageList(Map map);

    Integer getUserMessageNum(String userId);

    Integer updMessIsRead(Map map);

    /***
     *@Description: 修改已读
     */
    int updateMessage(Map map);
}
