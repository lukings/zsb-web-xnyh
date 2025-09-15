package cn.visolink.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 测试数据源配置是否正确
 */
@SpringBootTest(properties = "sharding.enabled=true")
public class DatasourceTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Test
    public void testBeansExist() {
        System.out.println("=== 测试Spring容器中Bean是否存在 ===");
        // 检查masterDataSource是否存在
        boolean masterDataSourceExists = context.containsBean("masterDataSource");
        System.out.println("masterDataSource Bean是否存在: " + masterDataSourceExists);
        
        // 检查dataSource是否存在
        boolean dataSourceExists = context.containsBean("dataSource");
        System.out.println("dataSource Bean是否存在: " + dataSourceExists);
        
        // 打印所有可用的Bean名称
        System.out.println("\n=== 可用的Bean列表（部分） ===");
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            if (beanName.contains("DataSource") || beanName.contains("dataSource")) {
                System.out.println("- " + beanName + " : " + context.getType(beanName).getSimpleName());
            }
        }
        
        assert masterDataSourceExists : "masterDataSource Bean不存在";
    }

    @Test
    public void testMasterDataSourceConnection() throws SQLException {
        System.out.println("=== 测试masterDataSource连接 ===");
        try (Connection conn = masterDataSource.getConnection()) {
            System.out.println("连接成功: " + conn.isValid(10));
            System.out.println("数据库产品名称: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("数据库版本: " + conn.getMetaData().getDatabaseProductVersion());
        }
    }
}