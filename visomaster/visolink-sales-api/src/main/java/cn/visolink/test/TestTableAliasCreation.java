package cn.visolink.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 测试表别名创建，避免字符编码问题
 */
public class TestTableAliasCreation {
    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.46.167:54319/wyzsb?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true&connectTimeout=10000&socketTimeout=30000&lowerCaseTableNames=true&allowPublicKeyRetrieval=true";
        String username = "example_wyjt";
        String password = "Example2025!@#";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("=== 测试表别名创建 ===");
            
            // 测试创建 s_menus 别名
            try {
                String createView = "CREATE OR REPLACE VIEW s_menus AS SELECT * FROM S_Menus";
                stmt.execute(createView);
                System.out.println("✅ 成功创建 s_menus 别名");
            } catch (Exception e) {
                System.out.println("❌ 创建 s_menus 别名失败: " + e.getMessage());
            }
            
            // 测试查询
            try {
                String testQuery = "SELECT COUNT(*) FROM s_menus";
                stmt.executeQuery(testQuery);
                System.out.println("✅ s_menus 查询成功");
            } catch (Exception e) {
                System.out.println("❌ s_menus 查询失败: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ 连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
