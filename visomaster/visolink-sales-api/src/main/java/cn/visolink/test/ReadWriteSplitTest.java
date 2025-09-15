package cn.visolink.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 读写分离测试类
 * 用于验证 ShardingSphere 读写分离功能
 */
@Component
public class ReadWriteSplitTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void testReadWriteSplit() {
        System.out.println("=== 开始测试读写分离功能 ===");
        
        try {
            // 测试写操作（应该路由到主库）
            System.out.println("1. 测试写操作（INSERT）...");
            String insertSql = "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, NOW())";
            String projectClueId = "test_" + System.currentTimeMillis();
            int insertResult = jdbcTemplate.update(insertSql, projectClueId);
            System.out.println("   写操作结果: " + insertResult + " 行受影响");
            
            // 测试读操作（应该路由到从库）
            System.out.println("2. 测试读操作（SELECT）...");
            String selectSql = "SELECT COUNT(*) as total FROM b_project_clues";
            Integer count = jdbcTemplate.queryForObject(selectSql, Integer.class);
            System.out.println("   读操作结果: 总记录数 = " + count);
            
            // 测试查询最新记录
            System.out.println("3. 测试查询最新记录...");
            String latestSql = "SELECT ProjectClueId, CreateTime FROM b_project_clues ORDER BY CreateTime DESC LIMIT 5";
            List<Map<String, Object>> latestRecords = jdbcTemplate.queryForList(latestSql);
            System.out.println("   最新记录数: " + latestRecords.size());
            for (Map<String, Object> record : latestRecords) {
                System.out.println("   - ID: " + record.get("ProjectClueId") + ", 时间: " + record.get("CreateTime"));
            }
            
            // 测试更新操作（应该路由到主库）
            System.out.println("4. 测试更新操作（UPDATE）...");
            String updateSql = "UPDATE b_project_clues SET CreateTime = NOW() WHERE ProjectClueId = ?";
            int updateResult = jdbcTemplate.update(updateSql, latestRecords.get(0).get("ProjectClueId"));
            System.out.println("   更新操作结果: " + updateResult + " 行受影响");
            
            // 测试删除操作（应该路由到主库）
            System.out.println("5. 测试删除操作（DELETE）...");
            String deleteSql = "DELETE FROM b_project_clues WHERE ProjectClueId = ?";
            int deleteResult = jdbcTemplate.update(deleteSql, latestRecords.get(0).get("ProjectClueId"));
            System.out.println("   删除操作结果: " + deleteResult + " 行受影响");
            
            System.out.println("=== 读写分离测试完成 ===");
            
        } catch (Exception e) {
            System.err.println("读写分离测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
