package cn.visolink.system.userQuery.service.impl;

import cn.visolink.system.userQuery.entity.UserQueryConditions;
import cn.visolink.system.userQuery.mapper.UserQueryConditionsMapper;
import cn.visolink.system.userQuery.service.UserQueryConditionsService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户查询条件Service实现类
 * @author system
 * @date 2025/1/27
 */
@Service
@Slf4j
public class UserQueryConditionsServiceImpl implements UserQueryConditionsService {
    
    @Autowired
    private UserQueryConditionsMapper userQueryConditionsMapper;
    
    @Override
    public void saveUserQueryConditions(String userId, String interfaceName, Map<String, Object> queryParams) {
        try {
            // 转换为JSON字符串
            String queryParamsJson = JSON.toJSONString(queryParams);
            
            // 手动生成UUID
            String id = UUID.randomUUID().toString();
            
            // 保存或更新
            int result = userQueryConditionsMapper.saveOrUpdate(id, userId, interfaceName, queryParamsJson);
            
            if (result > 0) {
                log.info("用户{}的接口{}查询条件保存成功，ID: {}", userId, interfaceName, id);
            } else {
                log.warn("用户{}的接口{}查询条件保存失败", userId, interfaceName);
            }
        } catch (Exception e) {
            log.error("保存用户查询条件失败: userId={}, interfaceName={}", userId, interfaceName, e);
        }
    }
    
    @Override
    public Map<String, Object> getUserQueryConditions(String userId, String interfaceName) {
        try {
            UserQueryConditions conditions = userQueryConditionsMapper.getLatestByUserAndInterface(userId, interfaceName);
            
            if (conditions != null && StringUtils.isNotBlank(conditions.getQueryParams())) {
                // 解析JSON字符串为Map
                return JSON.parseObject(conditions.getQueryParams(), Map.class);
            }
        } catch (Exception e) {
            log.error("获取用户查询条件失败: userId={}, interfaceName={}", userId, interfaceName, e);
        }
        
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getUserQueryConditionsWithFilter(String userId, String interfaceName, String dataRange, String mapType) {
        try {
            UserQueryConditions conditions = userQueryConditionsMapper.getLatestByUserAndInterfaceWithFilter(userId, interfaceName, dataRange, mapType);
            
            if (conditions != null && StringUtils.isNotBlank(conditions.getQueryParams())) {
                // 解析JSON字符串为Map
                return JSON.parseObject(conditions.getQueryParams(), Map.class);
            }
        } catch (Exception e) {
            log.error("获取用户查询条件失败: userId={}, interfaceName={}, dataRange={}, mapType={}", userId, interfaceName, dataRange, mapType, e);
        }
        
        return new HashMap<>();
    }
    
    @Override
    public void clearUserQueryConditions(String userId, String interfaceName) {
        try {
            int result = userQueryConditionsMapper.deleteByUserAndInterface(userId, interfaceName);
            
            if (result > 0) {
                log.info("用户{}的接口{}查询条件清除成功", userId, interfaceName);
            }
        } catch (Exception e) {
            log.error("清除用户查询条件失败: userId={}, interfaceName={}", userId, interfaceName, e);
        }
    }
    
    @Override
    public UserQueryConditions getById(String id) {
        try {
            return userQueryConditionsMapper.getById(id);
        } catch (Exception e) {
            log.error("根据ID查询记录失败: id={}", id, e);
            return null;
        }
    }
}
