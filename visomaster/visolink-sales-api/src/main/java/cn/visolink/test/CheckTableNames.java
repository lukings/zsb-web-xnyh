package cn.visolink.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 检查数据库中的实际表名
 */
public class CheckTableNames {
    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.46.167:54319/wyzsb?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true&connectTimeout=10000&socketTimeout=30000&lowerCaseTableNames=true&allowPublicKeyRetrieval=true";
        String username = "example_wyjt";
        String password = "Example2025!@#";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== 检查数据库中的表名 ===");
            
            // 查看所有表名
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("数据库中的所有表:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
            
            // 查找包含 jobs 的表
            System.out.println("\n包含 'jobs' 的表:");
            rs = stmt.executeQuery("SHOW TABLES LIKE '%jobs%'");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
            
            // 查找包含 Jobs 的表
            System.out.println("\n包含 'Jobs' 的表:");
            rs = stmt.executeQuery("SHOW TABLES LIKE '%Jobs%'");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
            
            // 查找包含 user 的表
            System.out.println("\n包含 'user' 的表:");
            rs = stmt.executeQuery("SHOW TABLES LIKE '%user%'");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
            
            // 查找包含 User 的表
            System.out.println("\n包含 'User' 的表:");
            rs = stmt.executeQuery("SHOW TABLES LIKE '%User%'");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
            
        } catch (Exception e) {
            System.out.println("❌ 连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}