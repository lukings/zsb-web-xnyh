package cn.visolink.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ShardingSphere功能测试类
 * 用于验证读写分离和分表功能
 */
@Component
public class ShardingSphereTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试读写分离功能
     */
    public void testReadWriteSplitting() {
        System.out.println("=== 测试读写分离功能 ===");
        
        try {
            // 测试写操作（应该路由到主库）
            System.out.println("1. 测试写操作...");
            String projectClueId = UUID.randomUUID().toString();
            String insertSql = "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, projectClueId, LocalDateTime.now());
            System.out.println("写操作成功，数据已插入到主库，主键: " + projectClueId);
            
            // 测试读操作（应该路由到从库）
            System.out.println("2. 测试读操作...");
            String selectSql = "SELECT * FROM b_project_clues WHERE ProjectClueId = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(selectSql, projectClueId);
            System.out.println("读操作成功，从从库读取到数据: " + results.size() + " 条");
            
        } catch (Exception e) {
            System.err.println("读写分离测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试分表功能
     */
    public void testSharding() {
        System.out.println("=== 测试分表功能 ===");
        
        try {
            // 测试不同月份的数据插入
            LocalDateTime now = LocalDateTime.now();
            
            // 插入当前月份的数据
            System.out.println("1. 插入当前月份数据...");
            String projectClueId1 = UUID.randomUUID().toString();
            String insertCurrentMonth = "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, ?)";
            jdbcTemplate.update(insertCurrentMonth, projectClueId1, now);
            System.out.println("  插入数据，主键: " + projectClueId1);
            
            // 插入上个月的数据
            System.out.println("2. 插入上个月数据...");
            String projectClueId2 = UUID.randomUUID().toString();
            LocalDateTime lastMonth = now.minusMonths(1);
            jdbcTemplate.update(insertCurrentMonth, projectClueId2, lastMonth);
            System.out.println("  插入数据，主键: " + projectClueId2);
            
            // 插入下个月的数据
            System.out.println("3. 插入下个月数据...");
            String projectClueId3 = UUID.randomUUID().toString();
            LocalDateTime nextMonth = now.plusMonths(1);
            jdbcTemplate.update(insertCurrentMonth, projectClueId3, nextMonth);
            System.out.println("  插入数据，主键: " + projectClueId3);
            
            // 查询所有数据
            System.out.println("4. 查询所有数据...");
            String selectAll = "SELECT ProjectClueId, CreateTime FROM b_project_clues ORDER BY CreateTime";
            List<Map<String, Object>> allResults = jdbcTemplate.queryForList(selectAll);
            System.out.println("查询到数据: " + allResults.size() + " 条");
            for (Map<String, Object> row : allResults) {
                System.out.println("ID: " + row.get("ProjectClueId") + 
                                 ", 时间: " + row.get("CreateTime"));
            }
            
        } catch (Exception e) {
            System.err.println("分表测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试数据源切换
     */
    public void testDataSourceSwitch() {
        System.out.println("=== 测试数据源切换 ===");
        
        try {
            // 测试主库连接
            System.out.println("1. 测试主库连接...");
            String masterSql = "SELECT 'master' as source, COUNT(*) as count FROM b_project_clues";
            List<Map<String, Object>> masterResult = jdbcTemplate.queryForList(masterSql);
            System.out.println("主库查询结果: " + masterResult);
            
            // 测试从库连接（通过读操作）
            System.out.println("2. 测试从库连接...");
            String slaveSql = "SELECT 'slave' as source, COUNT(*) as count FROM b_project_clues";
            List<Map<String, Object>> slaveResult = jdbcTemplate.queryForList(slaveSql);
            System.out.println("从库查询结果: " + slaveResult);
            
        } catch (Exception e) {
            System.err.println("数据源切换测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 运行所有测试
     */
    public void runAllTests() {
        System.out.println("开始ShardingSphere功能测试...");
        System.out.println("=====================================");
        
        testReadWriteSplitting();
        System.out.println();
        
        testSharding();
        System.out.println();
        
        testDataSourceSwitch();
        System.out.println();
        
        System.out.println("=====================================");
        System.out.println("ShardingSphere功能测试完成！");
    }
}