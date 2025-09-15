package cn.visolink.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 数据源切换工具类
 * 用于在ShardingSphere和传统数据源之间切换
 * 
 * @author system
 * @since 2024-01-01
 */
@Component
public class DataSourceSwitchUtil {
    
    @Value("${spring.profiles.active:uat}")
    private String activeProfile;
    
    /**
     * 判断是否启用ShardingSphere
     * @return true-启用分库分表，false-使用传统数据源
     */
    public boolean isShardingEnabled() {
        return "sharding".equals(activeProfile) || "sharding-simple".equals(activeProfile);
    }
    
    /**
     * 获取当前数据源模式
     * @return 数据源模式描述
     */
    public String getDataSourceMode() {
        return isShardingEnabled() ? "ShardingSphere-JDBC" : "Traditional-Druid";
    }
    
    /**
     * 检查是否需要特殊处理
     * 在分表模式下，某些跨表查询需要特殊处理
     */
    public boolean needSpecialHandling() {
        return isShardingEnabled();
    }
}
