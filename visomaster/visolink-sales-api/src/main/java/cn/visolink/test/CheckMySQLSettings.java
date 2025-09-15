package cn.visolink.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 检查 MySQL 服务器的大小写设置
 */
public class CheckMySQLSettings {
    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.46.167:54319/wyzsb?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true&connectTimeout=10000&socketTimeout=30000&lowerCaseTableNames=true&allowPublicKeyRetrieval=true&useInformationSchema=true";
        String username = "example_wyjt";
        String password = "Example2025!@#";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== 检查 MySQL 服务器设置 ===");
            
            // 检查 lower_case_table_names 设置
            ResultSet rs = stmt.executeQuery("SHOW VARIABLES LIKE 'lower_case_table_names'");
            while (rs.next()) {
                System.out.println("lower_case_table_names = " + rs.getString(2));
            }
            
            // 检查数据库名称
            rs = stmt.executeQuery("SELECT DATABASE()");
            while (rs.next()) {
                System.out.println("当前数据库: " + rs.getString(1));
            }
            
            // 测试表名大小写查询
            System.out.println("\n=== 测试表名大小写查询 ===");
            
            String[] testTables = {
                "S_JobsUserRel",      // 应该映射到 s_jobsuserrel
                "s_jobsuserrel",      // 直接查询小写
                "S_Jobs",             // 应该映射到 s_jobs
                "s_jobs",             // 直接查询小写
                "S_Organization",     // 应该映射到 s_organization
                "s_organization"      // 直接查询小写
            };
            
            for (String tableName : testTables) {
                try {
                    System.out.print("查询表 '" + tableName + "': ");
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
                    if (rs.next()) {
                        System.out.println("✅ 成功 (记录数: " + rs.getInt(1) + ")");
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
