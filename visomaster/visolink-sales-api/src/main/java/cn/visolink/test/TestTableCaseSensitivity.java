package cn.visolink.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 测试表名大小写敏感问题
 */
public class TestTableCaseSensitivity {
    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.46.167:54319/wyzsb?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true&connectTimeout=10000&socketTimeout=30000&lowerCaseTableNames=true&allowPublicKeyRetrieval=true";
        String username = "example_wyjt";
        String password = "Example2025!@#";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== 测试表名大小写敏感问题 ===");
            System.out.println("JDBC URL: " + url);
            System.out.println();
            
            // 1. 查看所有表名
            System.out.println("1. 查看所有表名:");
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            while (rs.next()) {
                System.out.println("   - " + rs.getString(1));
            }
            
            // 2. 测试不同大小写的表名查询
            System.out.println("\n2. 测试表名大小写查询:");
            
            String[] tableNames = {
                "S_JobsUserRel",      // 原始大小写
                "s_jobsuserrel",      // 全小写
                "s_jobs_user_rel",    // 下划线分隔
                "S_JOBSUSERREL"       // 全大写
            };
            
            for (String tableName : tableNames) {
                try {
                    System.out.print("   查询表 '" + tableName + "': ");
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
                    if (rs.next()) {
                        System.out.println("✅ 成功 (记录数: " + rs.getInt(1) + ")");
                    }
                } catch (Exception e) {
                    System.out.println("❌ 失败: " + e.getMessage());
                }
            }
            
            // 3. 测试具体的 S_JobsUserRel 表
            System.out.println("\n3. 测试 S_JobsUserRel 表结构:");
            try {
                rs = stmt.executeQuery("DESCRIBE S_JobsUserRel");
                System.out.println("   表结构:");
                while (rs.next()) {
                    System.out.println("     - " + rs.getString(1) + " " + rs.getString(2));
                }
            } catch (Exception e) {
                System.out.println("   ❌ 无法获取表结构: " + e.getMessage());
            }
            
            // 4. 测试插入操作
            System.out.println("\n4. 测试插入操作:");
            try {
                String testId = "test_" + System.currentTimeMillis();
                stmt.executeUpdate("INSERT INTO S_JobsUserRel (id, name) VALUES ('" + testId + "', 'test')");
                System.out.println("   ✅ 插入成功");
                
                // 清理测试数据
                stmt.executeUpdate("DELETE FROM S_JobsUserRel WHERE id = '" + testId + "'");
                System.out.println("   ✅ 清理测试数据成功");
            } catch (Exception e) {
                System.out.println("   ❌ 插入失败: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ 连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
