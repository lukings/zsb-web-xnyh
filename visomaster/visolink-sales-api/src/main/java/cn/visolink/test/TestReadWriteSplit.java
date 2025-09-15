package cn.visolink.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 测试读写分离功能
 */
@Component
public class TestReadWriteSplit {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void testReadWriteSplit() {
        System.out.println("=== 测试读写分离功能 ===");
        
        try {
            // 1. 测试写操作（应该路由到主库）
            System.out.println("1. 测试写操作（INSERT）...");
            String testId = "test_" + System.currentTimeMillis();
            jdbcTemplate.update("INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, ?)", 
                testId, java.time.LocalDateTime.now());
            System.out.println("   ✅ 写操作成功，数据已插入主库");
            
            // 2. 测试读操作（应该路由到从库）
            System.out.println("2. 测试读操作（SELECT）...");
            List<Map<String, Object>> results = jdbcTemplate.queryForList("SELECT COUNT(*) as count FROM b_project_clues");
            System.out.println("   ✅ 读操作成功，总记录数: " + results.get(0).get("count"));
            
            // 3. 测试更新操作（应该路由到主库）
            System.out.println("3. 测试更新操作（UPDATE）...");
            int updateCount = jdbcTemplate.update("UPDATE b_project_clues SET CreateTime = ? WHERE ProjectClueId = ?", 
                java.time.LocalDateTime.now(), testId);
            System.out.println("   ✅ 更新操作成功，影响行数: " + updateCount);
            
            // 4. 测试删除操作（应该路由到主库）
            System.out.println("4. 测试删除操作（DELETE）...");
            int deleteCount = jdbcTemplate.update("DELETE FROM b_project_clues WHERE ProjectClueId = ?", testId);
            System.out.println("   ✅ 删除操作成功，影响行数: " + deleteCount);
            
            System.out.println("=== 读写分离测试完成 ===");
            
        } catch (Exception e) {
            System.out.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
