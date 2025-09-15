package cn.visolink.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 清理测试数据
 * 解决主键冲突问题
 */
public class CleanupTestData {

    private static final String DB_USER = "example_wyjt";
    private static final String DB_PASSWORD = "Example2025!@#";
    private static final String[] DB_HOSTS = {
        "192.168.46.167:54319",
        "192.168.46.168:54319",
        "192.168.46.169:54319"
    };

    public static void main(String[] args) {
        System.out.println("开始清理测试数据...");
        for (String host : DB_HOSTS) {
            try {
                cleanupDatabase(host);
                System.out.println("✅ 服务器 " + host + " 清理完成");
            } catch (SQLException e) {
                System.err.println("❌ 服务器 " + host + " 清理失败: " + e.getMessage());
            }
        }
        System.out.println("数据清理完成！");
    }

    private static void cleanupDatabase(String host) throws SQLException {
        String url = "jdbc:mysql://" + host + "/wyzsb?useSSL=false&serverTimezone=Asia/Shanghai";
        try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 清理主表数据
            stmt.execute("DELETE FROM b_project_clues");
            System.out.println("  清理主表 b_project_clues");

            // 清理所有分表数据
            String[] months2024 = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            for (String month : months2024) {
                try {
                    stmt.execute("DELETE FROM b_project_clues_2024" + month);
                } catch (SQLException e) {
                    // 表可能不存在，忽略错误
                }
            }
            System.out.println("  清理2024年分表数据");

            String[] months2025 = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            for (String month : months2025) {
                try {
                    stmt.execute("DELETE FROM b_project_clues_2025" + month);
                } catch (SQLException e) {
                    // 表可能不存在，忽略错误
                }
            }
            System.out.println("  清理2025年分表数据");

            // 重置自增主键
            stmt.execute("ALTER TABLE b_project_clues AUTO_INCREMENT = 1");
            System.out.println("  重置主表自增主键");

            // 验证清理结果
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM b_project_clues");
            if (rs.next()) {
                System.out.println("  主表记录数: " + rs.getInt("count"));
            }

        }
    }
}
