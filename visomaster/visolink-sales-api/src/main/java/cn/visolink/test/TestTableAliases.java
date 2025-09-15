package cn.visolink.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 测试表别名是否工作正常
 */
@Component
public class TestTableAliases {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void testTableAliases() {
        System.out.println("=== 测试表别名功能 ===");
        
        // 测试的表名
        String[] testTables = {
            "S_JobsUserRel",
            "S_Jobs", 
            "S_Organization",
            "S_CommonJobs",
            "S_Dictionary",
            "S_Menus"
        };
        
        for (String tableName : testTables) {
            try {
                System.out.print("测试表 '" + tableName + "': ");
                List<Map<String, Object>> results = jdbcTemplate.queryForList("SELECT COUNT(*) as count FROM " + tableName);
                if (!results.isEmpty()) {
                    System.out.println("✅ 成功 (记录数: " + results.get(0).get("count") + ")");
                }
            } catch (Exception e) {
                System.out.println("❌ 失败: " + e.getMessage());
            }
        }
        
        System.out.println("=== 表别名测试完成 ===");
    }
}
