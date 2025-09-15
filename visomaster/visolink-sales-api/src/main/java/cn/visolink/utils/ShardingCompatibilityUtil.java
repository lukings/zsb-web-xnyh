package cn.visolink.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ShardingSphere兼容性工具类
 * 处理分表模式下的特殊查询需求
 * 
 * @author system
 * @since 2024-01-01
 */
@Component
public class ShardingCompatibilityUtil {
    
    @Autowired
    private DataSourceSwitchUtil dataSourceSwitchUtil;
    
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    
    /**
     * 根据时间范围生成可能涉及的表名列表
     * 用于跨月查询时的表名生成
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 表名列表
     */
    public List<String> generateTableNamesByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<String> tableNames = new ArrayList<>();
        
        if (!dataSourceSwitchUtil.isShardingEnabled()) {
            // 非分表模式，返回原始表名
            tableNames.add("b_project_clues");
            return tableNames;
        }
        
        if (startTime == null || endTime == null) {
            // 如果时间范围不明确，返回当前月份的表
            String currentMonth = LocalDateTime.now().format(MONTH_FORMATTER);
            tableNames.add("b_project_clues_" + currentMonth);
            return tableNames;
        }
        
        // 生成时间范围内的所有月份表名
        LocalDateTime current = startTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = endTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        while (!current.isAfter(end)) {
            String tableSuffix = current.format(MONTH_FORMATTER);
            tableNames.add("b_project_clues_" + tableSuffix);
            current = current.plusMonths(1);
        }
        
        return tableNames;
    }
    
    /**
     * 根据单个时间点生成表名
     * 
     * @param createTime 创建时间
     * @return 表名
     */
    public String generateTableNameByTime(LocalDateTime createTime) {
        if (!dataSourceSwitchUtil.isShardingEnabled()) {
            return "b_project_clues";
        }
        
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        
        String tableSuffix = createTime.format(MONTH_FORMATTER);
        return "b_project_clues_" + tableSuffix;
    }
    
    /**
     * 检查查询是否需要跨表处理
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return true-需要跨表查询，false-单表查询
     */
    public boolean isCrossTableQuery(LocalDateTime startTime, LocalDateTime endTime) {
        if (!dataSourceSwitchUtil.isShardingEnabled()) {
            return false;
        }
        
        if (startTime == null || endTime == null) {
            return false;
        }
        
        // 如果开始和结束时间不在同一个月，则需要跨表查询
        String startMonth = startTime.format(MONTH_FORMATTER);
        String endMonth = endTime.format(MONTH_FORMATTER);
        
        return !startMonth.equals(endMonth);
    }
    
    /**
     * 生成分表模式下的SQL提示信息
     * 用于日志记录和调试
     */
    public String generateShardingInfo(String operation, LocalDateTime startTime, LocalDateTime endTime) {
        if (!dataSourceSwitchUtil.isShardingEnabled()) {
            return String.format("[Traditional] %s on b_project_clues", operation);
        }
        
        List<String> tableNames = generateTableNamesByTimeRange(startTime, endTime);
        return String.format("[Sharding] %s on tables: %s", operation, String.join(", ", tableNames));
    }
}
