package cn.visolink.system.homepage.service;

import cn.visolink.exception.ResultBody;

import java.util.List;
import java.util.Map;

public interface WorkbenchService {
    /**
     * 查询我的待办
     * */
    ResultBody getPendingList(Map map);
    ResultBody getSendToMessage(Map map);

    ResultBody getMessageSize(Map map);

    ResultBody updMessIsRead(Map map);

    /***
     *@Description: 更新未读消息
     */
    ResultBody updateMessage(Map map);
    /**
     * 获取申请项目权限
     * @param userId
     * @param proList
     * @return
     */
    List<String> linkOrgIds(String userId, List<String> proList);
}
