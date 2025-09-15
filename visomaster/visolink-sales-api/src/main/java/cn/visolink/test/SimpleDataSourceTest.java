package cn.visolink.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 简单的数据源测试
 */
@Component
public class SimpleDataSourceTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void testDataSource() {
        System.out.println("=== 测试数据源路由 ===");
        
        try {
            // 执行一个简单的查询
            System.out.println("执行查询: SELECT 1");
            List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT 1 as test");
            System.out.println("查询结果: " + result);
            
            // 执行一个写操作
            System.out.println("执行写操作: INSERT INTO b_project_clues");
            String projectClueId = "test_routing_" + System.currentTimeMillis();
            int insertResult = jdbcTemplate.update(
                "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, NOW())", 
                projectClueId
            );
            System.out.println("插入结果: " + insertResult + " 行");
            
            // 执行读操作
            System.out.println("执行读操作: SELECT COUNT(*) FROM b_project_clues");
            List<Map<String, Object>> countResult = jdbcTemplate.queryForList("SELECT COUNT(*) as count FROM b_project_clues");
            System.out.println("查询结果: " + countResult);
            
            // 清理测试数据
            jdbcTemplate.update("DELETE FROM b_project_clues WHERE ProjectClueId = ?", projectClueId);
            System.out.println("清理测试数据完成");
            
        } catch (Exception e) {
            System.out.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
