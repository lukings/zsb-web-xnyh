package cn.visolink.system.userQuery.service;

import cn.visolink.system.userQuery.entity.UserQueryConditions;

import java.util.Map;

/**
 * 用户查询条件Service接口
 * @author system
 * @date 2025/1/27
 */
public interface UserQueryConditionsService {
    
    /**
     * 保存用户查询条件
     */
    void saveUserQueryConditions(String userId, String interfaceName, Map<String, Object> queryParams);
    
    /**
     * 获取用户指定接口的最近一次查询条件
     */
    Map<String, Object> getUserQueryConditions(String userId, String interfaceName);
    
    /**
     * 清除用户指定接口的查询条件
     */
    void clearUserQueryConditions(String userId, String interfaceName);
    
    /**
     * 根据ID查询记录
     */
    UserQueryConditions getById(String id);

    /**
     * 获取用户指定接口的最近一次查询条件（带过滤条件）
     */
    Map<String, Object> getUserQueryConditionsWithFilter(String userId, String interfaceName, String dataRange, String mapType);
}
