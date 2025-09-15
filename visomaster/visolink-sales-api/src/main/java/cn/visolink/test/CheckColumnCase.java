package cn.visolink.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 检查字段名大小写问题
 */
public class CheckColumnCase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.46.167:54319/wyzsb?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true&connectTimeout=10000&socketTimeout=30000&lowerCaseTableNames=true&allowPublicKeyRetrieval=true&useInformationSchema=true";
        String username = "example_wyjt";
        String password = "Example2025!@#";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== 检查字段名大小写问题 ===");
            
            // 检查 s_jobsuserrel 表的字段
            System.out.println("\n1. s_jobsuserrel 表的字段:");
            ResultSet rs = stmt.executeQuery("DESCRIBE s_jobsuserrel");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1) + " " + rs.getString(2));
            }
            
            // 检查 s_jobs 表的字段
            System.out.println("\n2. s_jobs 表的字段:");
            rs = stmt.executeQuery("DESCRIBE s_jobs");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1) + " " + rs.getString(2));
            }
            
            // 检查 s_organization 表的字段
            System.out.println("\n3. s_organization 表的字段:");
            rs = stmt.executeQuery("DESCRIBE s_organization");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1) + " " + rs.getString(2));
            }
            
            // 测试字段名大小写查询
            System.out.println("\n=== 测试字段名大小写查询 ===");
            
            String[] testQueries = {
                "SELECT ID FROM s_jobsuserrel LIMIT 1",           // 大写字段名
                "SELECT id FROM s_jobsuserrel LIMIT 1",            // 小写字段名
                "SELECT AccountID FROM s_jobsuserrel LIMIT 1",     // 大写字段名
                "SELECT accountid FROM s_jobsuserrel LIMIT 1",     // 小写字段名
                "SELECT JobID FROM s_jobsuserrel LIMIT 1",         // 大写字段名
                "SELECT jobid FROM s_jobsuserrel LIMIT 1"          // 小写字段名
            };
            
            for (String query : testQueries) {
                try {
                    System.out.print("查询: " + query + " -> ");
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        System.out.println("✅ 成功");
                    }
                } catch (Exception e) {
                    System.out.println("❌ 失败: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ 连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
