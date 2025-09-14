package cn.visolink.system.channel.task;

import cn.visolink.system.channel.dao.TaskDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户状态缓存定时任务
 * 每30分钟执行一次，预计算客户状态并存入缓存表
 */
@Component
@Profile({"uat", "prod"})
public class MapTask {
    
    private static final Logger logger = LoggerFactory.getLogger(MapTask.class);
    
    @Autowired
    private TaskDao taskDao;
    
    /**
     * 零晨0点10分执行一次
     */
    @Scheduled(cron = "0 10 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    @ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = false)
    public void updateMapTask() {
        logger.info("开始执行客户状态缓存更新任务");

        try {
            //1、更新任务的状态字段
            taskDao.updateTaskStatusByTime();

            // 2. 查出所有需要回收的客户
            List<Map<String, Object>> toRecycle = taskDao.selectTaskCustomerToRecycle();

            // 3. 批量更新project_clues归属信息，每100个一批
            int batchSize = 100;
            List<String> customerIdsBatch = new ArrayList<>();



            for (int i = 0; i < toRecycle.size(); i++) {
                Map<String, Object> row = toRecycle.get(i);
                String projectClueId = (String) row.get("customerId");
                customerIdsBatch.add(projectClueId);

                // 每满100个或最后一批时，执行一次批量更新
                if (customerIdsBatch.size() == batchSize || i == toRecycle.size() - 1) {
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("ClueStatus", 0);
                    taskDao.batchUpdateByProjectClueIds(customerIdsBatch, updateMap);
                    customerIdsBatch.clear();
                }
            }

            //4、任务结束回收任务客户
            taskDao.recycleTaskCustomerByTaskStatusAndNotReported();
        } catch (Exception e) {
            logger.error("客户状态缓存更新失败", e);
            throw e;
        }
    }


}