package cn.visolink.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.ReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.rule.ReadwriteSplittingDataSourceRuleConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * ShardingSphere配置类
 * 用于替换原有的DynamicDataSource配置
 * 
 * @author system
 * @since 2024-01-01
 */
@Configuration
@ConditionalOnProperty(name = "sharding.enabled", havingValue = "true", matchIfMissing = false)
public class ShardingSphereConfig {
    
    @Value("${spring.shardingsphere.datasource.master.url}")
    private String masterUrl;
    
    @Value("${spring.shardingsphere.datasource.master.username}")
    private String masterUsername;
    
    @Value("${spring.shardingsphere.datasource.master.password}")
    private String masterPassword;
    
    @Value("${spring.shardingsphere.datasource.master.driver-class-name}")
    private String masterDriverClassName;
    
    @Value("${spring.shardingsphere.datasource.slave2.url}")
    private String slave2Url;
    
    @Value("${spring.shardingsphere.datasource.slave2.username}")
    private String slave2Username;
    
    @Value("${spring.shardingsphere.datasource.slave2.password}")
    private String slave2Password;
    
    @Value("${spring.shardingsphere.datasource.slave2.driver-class-name}")
    private String slave2DriverClassName;
    
    /**
     * 手动创建 ShardingSphere 数据源
     */
    @Bean("dataSource")
    @Primary
    public DataSource dataSource() throws SQLException {
        // 创建实际的数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        
        // 主库数据源
        DruidDataSource masterDataSource = new DruidDataSource();
        masterDataSource.setUrl(masterUrl);
        masterDataSource.setUsername(masterUsername);
        masterDataSource.setPassword(masterPassword);
        masterDataSource.setDriverClassName(masterDriverClassName);
        masterDataSource.setInitialSize(1);
        masterDataSource.setMinIdle(1);
        masterDataSource.setMaxActive(5);
        masterDataSource.setMaxWait(10000);
        masterDataSource.setTimeBetweenEvictionRunsMillis(90000);
        masterDataSource.setMinEvictableIdleTimeMillis(1800000);
        masterDataSource.setTestWhileIdle(true);
        masterDataSource.setTestOnBorrow(false);
        masterDataSource.setTestOnReturn(false);
        masterDataSource.setValidationQuery("select 1");
        dataSourceMap.put("master", masterDataSource);
        
        // 从库数据源
        DruidDataSource slave2DataSource = new DruidDataSource();
        slave2DataSource.setUrl(slave2Url);
        slave2DataSource.setUsername(slave2Username);
        slave2DataSource.setPassword(slave2Password);
        slave2DataSource.setDriverClassName(slave2DriverClassName);
        slave2DataSource.setInitialSize(1);
        slave2DataSource.setMinIdle(1);
        slave2DataSource.setMaxActive(5);
        slave2DataSource.setMaxWait(10000);
        slave2DataSource.setTimeBetweenEvictionRunsMillis(90000);
        slave2DataSource.setMinEvictableIdleTimeMillis(1800000);
        slave2DataSource.setTestWhileIdle(true);
        slave2DataSource.setTestOnBorrow(false);
        slave2DataSource.setTestOnReturn(false);
        slave2DataSource.setValidationQuery("select 1");
        dataSourceMap.put("slave2", slave2DataSource);
        
        // 配置读写分离规则 - 使用 Java 8 兼容的方式
        Properties props = new Properties();
        props.setProperty("write-data-source-name", "master");
        props.setProperty("read-data-source-names", "slave2");
        
        ReadwriteSplittingDataSourceRuleConfiguration dataSourceRuleConfig = 
            new ReadwriteSplittingDataSourceRuleConfiguration("readwrite_ds", 
                "Static", 
                props, 
                "round_robin");
        
        Map<String, ShardingSphereAlgorithmConfiguration> loadBalancers = new HashMap<>();
        loadBalancers.put("round_robin", new ShardingSphereAlgorithmConfiguration("ROUND_ROBIN", new Properties()));
        
        ReadwriteSplittingRuleConfiguration readwriteSplittingRuleConfig = 
            new ReadwriteSplittingRuleConfiguration(Collections.singletonList(dataSourceRuleConfig), 
                loadBalancers);
        
        // 创建 ShardingSphere 数据源
        Properties shardingProps = new Properties();
        shardingProps.setProperty("sql-show", "true");
        shardingProps.setProperty("sql-simple", "false");
        shardingProps.setProperty("sql-show-detail", "true");
        
        return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, 
            Collections.singletonList(readwriteSplittingRuleConfig), 
            shardingProps);
    }
    
    /**
     * 主数据源，用于 TableAliasInitializer
     */
    @Bean("masterDataSource")
    public DataSource masterDataSource() {
        return createDataSource(masterUrl, masterUsername, masterPassword, masterDriverClassName);
    }
    
    private DataSource createDataSource(String url, String username, String password, String driverClassName) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(5);
        dataSource.setMaxWait(10000);
        dataSource.setTimeBetweenEvictionRunsMillis(90000);
        dataSource.setMinEvictableIdleTimeMillis(1800000);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setValidationQuery("select 1");
        return dataSource;
    }
}
