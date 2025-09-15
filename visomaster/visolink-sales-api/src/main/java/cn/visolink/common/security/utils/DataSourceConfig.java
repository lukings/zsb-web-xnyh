package cn.visolink.common.security.utils;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Autowired
    private Environment environment;
    
    // 仅在非sharding模式下创建默认数据源
    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    @ConditionalOnExpression("!'${spring.profiles.active}'.contains('sharding')")
    public DataSource defaultDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    // 仅在非sharding模式下创建动态数据源
    @Bean
    @Primary
    @DependsOn({"springUtils"})
    @ConditionalOnExpression("!'${spring.profiles.active}'.contains('sharding')")
    public DynamicDataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(DynamicDataSource.dataSourcesMap);
        return dynamicDataSource;
    }
}
