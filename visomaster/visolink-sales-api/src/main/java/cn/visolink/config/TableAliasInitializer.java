package cn.visolink.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用启动时创建表别名，解决大小写问题
 */
@Component
public class TableAliasInitializer implements CommandLineRunner {
    
    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;
    
    // 定义需要创建别名的表映射关系
    private static final Map<String, String> TABLE_ALIASES = new HashMap<>();
    
    static {
        // 大写表名 -> 小写表名
        TABLE_ALIASES.put("S_JobsUserRel", "s_jobsuserrel");
        TABLE_ALIASES.put("S_Jobs", "s_jobs");
        TABLE_ALIASES.put("S_Organization", "s_organization");
        TABLE_ALIASES.put("S_CommonJobs", "s_commonjobs");
        TABLE_ALIASES.put("S_CommonJobsFunctionsRel", "s_commonjobsfunctionsrel");
        TABLE_ALIASES.put("S_CommonJobsMenuRel", "s_commonjobsmenurel");
        TABLE_ALIASES.put("S_Dictionary", "s_dictionary");
        TABLE_ALIASES.put("S_Functions", "s_functions");
        TABLE_ALIASES.put("S_JobsFunctionsRel", "s_jobsfunctionsrel");
        TABLE_ALIASES.put("S_JobsMenuRel", "s_jobsmenurel");
        TABLE_ALIASES.put("S_Menus", "s_menus");
        TABLE_ALIASES.put("S_Product", "s_product");
        TABLE_ALIASES.put("S_ProductMenuRel", "s_productmenurel");
        TABLE_ALIASES.put("S_SystemLogs", "s_systemlogs");
        TABLE_ALIASES.put("S_AuthCompany", "s_authcompany");
        TABLE_ALIASES.put("S_AuthCompanyOrderDetail", "s_authcompanyorderdetail");
        
        // 添加更多可能需要的表别名
        TABLE_ALIASES.put("s_menus", "S_Menus");  // 小写 -> 大写
        TABLE_ALIASES.put("s_jobsuserrel", "S_JobsUserRel");
        TABLE_ALIASES.put("s_jobs", "S_Jobs");
        TABLE_ALIASES.put("s_organization", "S_Organization");
        
        // 添加 b_project 相关表别名
        TABLE_ALIASES.put("b_project", "B_Project");
        TABLE_ALIASES.put("B_Project", "b_project");
        TABLE_ALIASES.put("b_project_clues", "B_Project_Clues");
        TABLE_ALIASES.put("B_Project_Clues", "b_project_clues");
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 开始创建表别名，解决大小写问题 ===");
        
        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;
        
        for (Map.Entry<String, String> entry : TABLE_ALIASES.entrySet()) {
            String aliasName = entry.getKey();
            String realTableName = entry.getValue();
            
            try (Connection conn = masterDataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                // 先检查表是否存在
                String checkTable = "SELECT COUNT(*) FROM " + realTableName + " LIMIT 1";
                try {
                    stmt.executeQuery(checkTable);
                } catch (Exception checkE) {
                    System.out.println("⚠️  目标表不存在，跳过: " + realTableName);
                    skipCount++;
                    continue;
                }
                
                // 创建视图，使用反引号包围表名和字段名，避免关键字冲突
                String createView = "CREATE OR REPLACE VIEW `" + aliasName + "` AS SELECT * FROM `" + realTableName + "`";
                stmt.execute(createView);
                System.out.println("✅ 已创建表别名: " + aliasName + " -> " + realTableName);
                successCount++;
                
            } catch (Exception e) {
                String errorMsg = e.getMessage();
                if (errorMsg.contains("already exists") || errorMsg.contains("已存在")) {
                    System.out.println("ℹ️  表别名已存在: " + aliasName);
                    skipCount++;
                } else if (errorMsg.contains("doesn't exist") || errorMsg.contains("不存在")) {
                    System.out.println("⚠️  目标表不存在，跳过: " + realTableName);
                    skipCount++;
                } else {
                    System.out.println("❌ 创建表别名失败: " + aliasName + " -> " + realTableName + " 错误: " + errorMsg);
                    errorCount++;
                }
            }
        }
        
        System.out.println("=== 表别名创建完成 ===");
        System.out.println("✅ 成功创建: " + successCount + " 个");
        System.out.println("ℹ️  跳过: " + skipCount + " 个");
        System.out.println("❌ 失败: " + errorCount + " 个");
        
        if (errorCount > 0) {
            System.out.println("⚠️  部分表别名创建失败，但不影响应用启动");
        }
    }
}
