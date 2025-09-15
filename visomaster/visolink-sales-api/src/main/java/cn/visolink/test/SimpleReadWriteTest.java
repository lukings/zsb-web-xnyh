package cn.visolink.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * 简单的读写分离测试
 * 通过日志观察 SQL 路由
 */
@Component
public class SimpleReadWriteTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void testReadWrite() {
        System.out.println("\n=== 开始读写分离测试 ===");
        
        try {
            // 写操作 - 应该路由到主库
            System.out.println("执行写操作（INSERT）...");
            String projectClueId = UUID.randomUUID().toString();
            jdbcTemplate.update("INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, NOW())", projectClueId);
            System.out.println("写操作完成，主键: " + projectClueId);
            
            // 读操作 - 应该路由到从库
            System.out.println("执行读操作（SELECT）...");
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM b_project_clues", Integer.class);
            System.out.println("读操作完成，总记录数: " + count);
            
            // 再次读操作 - 应该路由到另一个从库
            System.out.println("执行第二次读操作（SELECT）...");
            Integer count2 = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM b_project_clues", Integer.class);
            System.out.println("第二次读操作完成，总记录数: " + count2);
            
            System.out.println("=== 读写分离测试完成 ===\n");
            
        } catch (Exception e) {
            System.err.println("读写分离测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
