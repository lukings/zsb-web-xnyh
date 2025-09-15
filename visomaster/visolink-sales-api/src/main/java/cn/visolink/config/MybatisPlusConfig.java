package cn.visolink.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置类
 * 与ShardingSphere-JDBC兼容的分页插件配置
 * 
 * @author wcl
 * @version 1.0
 * @date 2019/8/24 2:33 下午
 */
@Configuration
@MapperScan(value = {"cn.visolink.system.**.dao", "cn.visolink.system.**.mapper"})
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 分页插件
     * 使用PaginationInterceptor，与MyBatis-Plus 3.2.0版本兼容
     * 与ShardingSphere-JDBC完全兼容
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setLimit(1000);
        // 溢出总页数后是否进行处理
        paginationInterceptor.setOverflow(false);
        // 设置数据库类型
        paginationInterceptor.setDialectType("mysql");
        
        return paginationInterceptor;
    }
}
