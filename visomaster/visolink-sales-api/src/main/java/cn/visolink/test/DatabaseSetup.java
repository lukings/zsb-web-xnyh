package cn.visolink.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 数据库设置工具类
 * 用于创建 wyzsb 数据库和必要的表结构
 */
public class DatabaseSetup {
    
    private static final String[] SERVERS = {
        "192.168.46.167:54319",
        "192.168.46.168:54319", 
        "192.168.46.169:54319"
    };
    
    private static final String USERNAME = "example_wyjt";
    private static final String PASSWORD = "Example2025!@#";
    
    public static void main(String[] args) {
        System.out.println("开始创建数据库和表结构...");
        
        for (String server : SERVERS) {
            try {
                setupDatabase(server);
                System.out.println("✅ 服务器 " + server + " 设置完成");
            } catch (Exception e) {
                System.err.println("❌ 服务器 " + server + " 设置失败: " + e.getMessage());
            }
        }
        
        System.out.println("数据库设置完成！");
    }
    
    private static void setupDatabase(String server) throws Exception {
        // 连接到 MySQL 服务器（不指定数据库）
        String url = "jdbc:mysql://" + server + "/?useSSL=false&serverTimezone=Asia/Shanghai";
        
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // 创建数据库
            stmt.execute("CREATE DATABASE IF NOT EXISTS wyzsb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("  创建数据库 wyzsb");
            
            // 使用数据库
            stmt.execute("USE wyzsb");
            
            // 创建主表
            stmt.execute("CREATE TABLE IF NOT EXISTS b_project_clues (" +
                "ProjectClueId VARCHAR(36) PRIMARY KEY, " +
                "CreateTime DATETIME NOT NULL, " +
                "INDEX idx_create_time (CreateTime)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("  创建主表 b_project_clues");
            
            // 创建分表（2024年）
            String[] months2024 = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            for (String month : months2024) {
                stmt.execute("CREATE TABLE IF NOT EXISTS b_project_clues_2024" + month + " LIKE b_project_clues");
            }
            System.out.println("  创建2024年分表");
            
            // 创建分表（2025年）
            for (String month : months2024) {
                stmt.execute("CREATE TABLE IF NOT EXISTS b_project_clues_2025" + month + " LIKE b_project_clues");
            }
            System.out.println("  创建2025年分表");
            
            // 验证创建结果
            java.sql.ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'b_project_clues%'");
            int tableCount = 0;
            while (rs.next()) {
                tableCount++;
            }
            System.out.println("  共创建了 " + tableCount + " 个表");
            
        }
    }
}
