package cn.visolink.config;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * b_project_clues表月度分表算法
 * 根据CreateTime字段按yyyyMM格式进行分表
 * 
 * @author system
 * @since 2024-01-01
 */
public class ProjectCluesMonthlyShardingAlgorithm implements StandardShardingAlgorithm<LocalDateTime> {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<LocalDateTime> shardingValue) {
        LocalDateTime createTime = shardingValue.getValue();
        if (createTime == null) {
            // 如果CreateTime为空，路由到当前月份的表
            createTime = LocalDateTime.now();
        }
        
        String tableSuffix = createTime.format(MONTH_FORMATTER);
        String targetTableName = "b_project_clues_" + tableSuffix;
        
        // 检查目标表是否存在，如果不存在则使用默认表
        if (availableTargetNames.contains(targetTableName)) {
            return targetTableName;
        } else {
            // 如果目标表不存在，返回原始表名（用于兼容历史数据）
            return "b_project_clues";
        }
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<LocalDateTime> shardingValue) {
        Collection<String> result = new LinkedHashSet<>();
        
        LocalDateTime lowerBound = shardingValue.getValueRange().lowerEndpoint();
        LocalDateTime upperBound = shardingValue.getValueRange().upperEndpoint();
        
        // 如果范围查询，返回所有可能涉及的表
        if (lowerBound != null && upperBound != null) {
            LocalDateTime current = lowerBound.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = upperBound.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            
            while (!current.isAfter(end)) {
                String tableSuffix = current.format(MONTH_FORMATTER);
                String targetTableName = "b_project_clues_" + tableSuffix;
                
                if (availableTargetNames.contains(targetTableName)) {
                    result.add(targetTableName);
                }
                
                // 移动到下个月
                current = current.plusMonths(1);
            }
        }
        
        // 如果范围查询没有找到任何表，或者没有边界条件，返回原始表
        if (result.isEmpty()) {
            result.add("b_project_clues");
        }
        
        return result;
    }

    @Override
    public void init(Properties props) {
        // 初始化配置，可以在这里读取自定义参数
    }

    @Override
    public String getType() {
        return "PROJECT_CLUES_MONTHLY";
    }

    @Override
    public Properties getProps() {
        return new Properties();
    }
}
