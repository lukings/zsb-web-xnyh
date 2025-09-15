package cn.visolink.common.security.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/8/31 17:02
 */


/**
 * <p>使用步骤：</p>
 * <blockquote><pre>
 *     DynamicDataSource.dataSourcesMap.put(dataSourceKey, druidDataSource);
 *     DynamicDataSource.setDataSource(dataSourceKey);
 *     调用业务代码</i>
 *     DynamicDataSource.clear();
 * </pre></blockquote>
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> dataSourceKey = ThreadLocal.withInitial(() -> "defaultDataSource");

    public static Map<Object, Object> dataSourcesMap = new ConcurrentHashMap<>(10);

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSource.dataSourceKey.get();
    }

    public static void setDataSource(String dataSource) {
        DynamicDataSource.dataSourceKey.set(dataSource);
        DynamicDataSource dynamicDataSource = (DynamicDataSource) SpringUtils.getBean("dataSource");
        dynamicDataSource.afterPropertiesSet();
    }

    public static String getDataSource() {
        return DynamicDataSource.dataSourceKey.get();
    }

    public static void clear() {
        DynamicDataSource.dataSourceKey.remove();
    }
    
    /**
     * 初始化动态数据源配置
     * 这个配置类将在Spring容器启动时初始化动态数据源
     */
    @Configuration
    public static class DynamicDataSourceInitializer implements InitializingBean {
        
        @Autowired
        private Environment environment;
        
        @Autowired(required = false)
        private DataSource defaultDataSource;
        
        @Override
        public void afterPropertiesSet() {
            // 检查是否启用了sharding配置
            String activeProfile = environment.getProperty("spring.profiles.active", "");
            if (!activeProfile.contains("sharding") && defaultDataSource != null) {
                // 仅在非sharding模式且defaultDataSource不为null时注册默认数据源
                dataSourcesMap.put("defaultDataSource", defaultDataSource);
            }
        }
    }
}
