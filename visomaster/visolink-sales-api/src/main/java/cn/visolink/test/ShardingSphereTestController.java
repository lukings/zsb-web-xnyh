package cn.visolink.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ShardingSphere 测试控制器
 * 提供 REST API 来测试读写分离和分表功能
 */
@RestController
@RequestMapping("/test/sharding")
public class ShardingSphereTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试读写分离功能
     */
    @GetMapping("/readwrite")
    public Map<String, Object> testReadWriteSplit() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试写操作
            System.out.println("=== 执行写操作（应该路由到主库）===");
            String insertSql = "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, NOW())";
            String projectClueId = "test_" + System.currentTimeMillis();
            int insertResult = jdbcTemplate.update(insertSql, projectClueId);
            result.put("insertResult", insertResult);
            result.put("insertMessage", "写操作成功，影响 " + insertResult + " 行");
            
            // 测试读操作
            System.out.println("=== 执行读操作（应该路由到从库）===");
            String countSql = "SELECT COUNT(*) FROM b_project_clues";
            Integer totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);
            result.put("totalCount", totalCount);
            result.put("readMessage", "读操作成功，总记录数: " + totalCount);
            
            // 测试查询最新记录
            String latestSql = "SELECT ProjectClueId, CreateTime FROM b_project_clues ORDER BY CreateTime DESC LIMIT 3";
            List<Map<String, Object>> latestRecords = jdbcTemplate.queryForList(latestSql);
            result.put("latestRecords", latestRecords);
            result.put("latestMessage", "查询到 " + latestRecords.size() + " 条最新记录");
            
            result.put("success", true);
            result.put("message", "读写分离测试完成");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 测试分表功能
     */
    @GetMapping("/sharding")
    public Map<String, Object> testSharding() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 插入不同月份的数据来测试分表
            System.out.println("=== 测试分表功能 ===");
            
            // 插入2024年1月的数据
            String insert202401 = "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, '2024-01-15 10:00:00')";
            int result202401 = jdbcTemplate.update(insert202401, "test_202401_" + System.currentTimeMillis());
            result.put("insert202401", result202401);
            
            // 插入2024年6月的数据
            String insert202406 = "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, '2024-06-15 10:00:00')";
            int result202406 = jdbcTemplate.update(insert202406, "test_202406_" + System.currentTimeMillis());
            result.put("insert202406", result202406);
            
            // 插入2024年12月的数据
            String insert202412 = "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, '2024-12-15 10:00:00')";
            int result202412 = jdbcTemplate.update(insert202412, "test_202412_" + System.currentTimeMillis());
            result.put("insert202412", result202412);
            
            // 插入2025年1月的数据
            String insert202501 = "INSERT INTO b_project_clues (ProjectClueId, CreateTime) VALUES (?, '2025-01-15 10:00:00')";
            int result202501 = jdbcTemplate.update(insert202501, "test_202501_" + System.currentTimeMillis());
            result.put("insert202501", result202501);
            
            // 查询所有数据
            String selectAll = "SELECT ProjectClueId, CreateTime FROM b_project_clues ORDER BY CreateTime";
            List<Map<String, Object>> allRecords = jdbcTemplate.queryForList(selectAll);
            result.put("allRecords", allRecords);
            result.put("totalRecords", allRecords.size());
            
            result.put("success", true);
            result.put("message", "分表测试完成，数据已插入到不同的分表中");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取数据库连接信息
     */
    @GetMapping("/connection")
    public Map<String, Object> getConnectionInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查询当前连接信息
            String connectionSql = "SELECT CONNECTION_ID() as connectionId, USER() as currentUser, DATABASE() as currentDatabase";
            Map<String, Object> connectionInfo = jdbcTemplate.queryForMap(connectionSql);
            
            result.put("connectionInfo", connectionInfo);
            result.put("success", true);
            result.put("message", "连接信息获取成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 清理测试数据
     */
    @GetMapping("/cleanup")
    public Map<String, Object> cleanupTestData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 删除测试数据
            String deleteSql = "DELETE FROM b_project_clues WHERE CreateTime >= '2024-01-01'";
            int deletedRows = jdbcTemplate.update(deleteSql);
            
            result.put("deletedRows", deletedRows);
            result.put("success", true);
            result.put("message", "测试数据清理完成，删除了 " + deletedRows + " 行数据");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}