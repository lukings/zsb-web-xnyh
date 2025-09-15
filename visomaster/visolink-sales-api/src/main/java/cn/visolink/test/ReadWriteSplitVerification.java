package cn.visolink.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 验证读写分离是否生效
 */
@Component
public class ReadWriteSplitVerification {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void verifyReadWriteSplit() {
        System.out.println("=== 验证读写分离是否生效 ===");
        
        try {
            // 1. 测试写操作 - 应该路由到主库
            System.out.println("1. 执行写操作（INSERT）...");
            String testId = "rw_test_" + System.currentTimeMillis();
            int insertResult = jdbcTemplate.update(
                "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, NOW())", 
                testId
            );
            System.out.println("   写操作结果: " + insertResult + " 行受影响");
            
            // 2. 测试读操作 - 应该路由到从库
            System.out.println("2. 执行读操作（SELECT）...");
            List<Map<String, Object>> readResult = jdbcTemplate.queryForList(
                "SELECT ProjectClueId, CreateTime FROM b_project_clues WHERE ProjectClueId = ?", 
                testId
            );
            System.out.println("   读操作结果: " + readResult.size() + " 条记录");
            
            // 3. 测试更新操作 - 应该路由到主库
            System.out.println("3. 执行更新操作（UPDATE）...");
            int updateResult = jdbcTemplate.update(
                "UPDATE b_project_clues SET CreateTime = NOW() WHERE ProjectClueId = ?", 
                testId
            );
            System.out.println("   更新操作结果: " + updateResult + " 行受影响");
            
            // 4. 测试删除操作 - 应该路由到主库
            System.out.println("4. 执行删除操作（DELETE）...");
            int deleteResult = jdbcTemplate.update(
                "DELETE FROM b_project_clues WHERE ProjectClueId = ?", 
                testId
            );
            System.out.println("   删除操作结果: " + deleteResult + " 行受影响");
            
            System.out.println("=== 读写分离验证完成 ===");
            System.out.println("注意：如果看到 ShardingSphere-SQL 日志，说明读写分离生效");
            System.out.println("如果没有看到路由日志，说明读写分离可能没有生效");
            
        } catch (Exception e) {
            System.out.println("验证失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
