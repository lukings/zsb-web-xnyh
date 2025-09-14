package cn.visolink.system.custMap.task;

import cn.visolink.system.custMap.dao.CustMapDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户状态缓存定时任务
 * 每30分钟执行一次，预计算客户状态并存入缓存表
 */
@Component
@Profile({"uat", "prod"})
public class CustomerStatusCacheTask {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerStatusCacheTask.class);
    
    @Autowired
    private CustMapDao custMapDao;
    
    private static final Map<String, Integer> STATUS_TYPE_WEIGHT_MAPTYPE2 = new HashMap<>();
    private static final Map<String, Integer> STATUS_TYPE_WEIGHT_MAPTYPE1 = new HashMap<>();
    static {
        // mapType=2
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("1", 9);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("2", 8);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("7", 7);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("6", 6);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("4", 5);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("3", 4);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("5", 3);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("9", 2);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("8", 1);
        // mapType=1
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("1", 9);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("2", 8);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("3", 7);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("4", 6);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("5", 5);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("9", 4);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("8", 3);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("7", 2);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("6", 1);
    }

    /**
     * 每30分钟执行一次客户状态缓存更新
     */
    @Scheduled(cron = "0 10 8,13,20 * * ?")
    @Transactional(rollbackFor = Exception.class)
    @ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = false)
    public void updateCustomerStatusCache() {
        logger.info("开始执行客户状态缓存更新任务");
        long startTime = System.currentTimeMillis();
        
        try {
            // 处理报备数据
            custMapDao.clearOpportunityCacheStaging();
            
            // 处理招商地图
            custMapDao.updateOpportunityCacheStagingXM();
            custMapDao.updateOpportunityCacheStagingQY();
            custMapDao.updateOpportunityCacheStagingJT();

            // 处理拓客地图
            custMapDao.updateOpportunityCacheStagingXM_Tkdt();
            custMapDao.updateOpportunityCacheStagingQY_Tkdt();
            custMapDao.updateOpportunityCacheStagingJT_Tkdt();

            //中间表到查询表
            custMapDao.clearOpportunityCache();
            custMapDao.insertOpportunityCache();

            long endTime = System.currentTimeMillis();
            logger.info("更新机会缓存表耗时：" + (endTime - startTime)/1000 + "s");

            //处理线索数据
            custMapDao.clearCluesCacheStaging();

            // 处理客户地图
            custMapDao.updateCluesCacheStagingXM_Khdt();
            custMapDao.updateCluesCacheStagingXM_KhdtMapHis();


            custMapDao.updateCluesCacheStagingQY_Khdt();
            custMapDao.updateCluesCacheStagingJT_Khdt();



            // 处理拓客地图
            custMapDao.updateCluesCacheStagingXM_Tkdt();
            custMapDao.updateCluesCacheStagingXM_TkdtMapHis();
            custMapDao.updateCluesCacheStagingQY_Tkdt();
            custMapDao.updateCluesCacheStagingJT_Tkdt();

            //中间表到查询表
            custMapDao.clearCluesCache();
            custMapDao.insertCluesCache();

            //处理是否按该项目已有重叠客户标注逻辑
            //this.updateCustomerStatusCacheRecent();

            long endTime2 = System.currentTimeMillis();
            logger.info("更新线索缓存表耗时：" + (endTime2 - endTime)/1000 + "s");


        } catch (Exception e) {
            logger.error("客户状态缓存更新失败", e);
            throw e;
        }
    }

    /**
     * 每30分钟执行一次客户状态缓存更新-处理项目上的客户取最新标注
     */
//    @Scheduled(cron = "0 18 * * * ?")
//    @Transactional(rollbackFor = Exception.class)
//    @ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = false)
    public void updateCustomerStatusCacheRecent() {
        logger.info("开始执行客户状态缓存更新任务");
        long startTime = System.currentTimeMillis();
        
        try {
            // 查询 flag 以 '_1' 结尾的数据
            List<Map> flagEndWith1List = custMapDao.selectFlagEndWith1();
            logger.info("flag 以 '_1' 结尾的数据条数: {}", flagEndWith1List.size());

            for (Map item : flagEndWith1List) {
                Object customerName = item.get("CustomerName");
                Object projectId = item.get("projectId");
                if (customerName == null || projectId == null) continue;
                Map<String, Object> param = new java.util.HashMap<>();
                param.put("customerName", customerName);
                param.put("projectId", projectId);
                List<Map> listOpportunityCache = custMapDao.selectOpportunityCacheByNameAndProjectId(param);
                List<Map> listCluesCache = custMapDao.selectCluesCacheByNameAndProjectId(param);
                logger.info("CustomerName: {}, projectId: {}, opportunityCache size: {}, cluesCache size: {}", customerName, projectId, listOpportunityCache.size(), listCluesCache.size());

                Map<String, List<Map>> oppCacheByMapType = listOpportunityCache.stream()
                    .filter(e -> e.get("mapType") != null)
                    .collect(Collectors.groupingBy(e -> String.valueOf(e.get("mapType"))));
                Map<String, List<Map>> cluesCacheByMapType = listCluesCache.stream()
                    .filter(e -> e.get("mapType") != null)
                    .collect(Collectors.groupingBy(e -> String.valueOf(e.get("mapType"))));

                Object opportunityClueId = item.get("OpportunityClueId");
                Object projectClueId = item.get("ProjectClueId");

                for (String mapType : new String[]{"1", "2"}) {
                    List<Map> oppListForType = oppCacheByMapType.get(mapType);
                    List<Map> cluesListForType = cluesCacheByMapType.get(mapType);
                    Map<String, Integer> weightMap = "1".equals(mapType) ? STATUS_TYPE_WEIGHT_MAPTYPE1 : STATUS_TYPE_WEIGHT_MAPTYPE2;

                    // 机会缓存表
                    String maxStatusType = null;
                    int maxWeight = Integer.MIN_VALUE;
                    String currentStatusType = null;
                    if (opportunityClueId != null && oppListForType != null && !oppListForType.isEmpty()) {
                        for (Map cacheItem : oppListForType) {
                            String statusType = String.valueOf(cacheItem.get("StatusType"));
                            int weight = weightMap.getOrDefault(statusType, 0);
                            if (weight > maxWeight) {
                                maxWeight = weight;
                                maxStatusType = statusType;
                            }
                            if (opportunityClueId.equals(cacheItem.get("OpportunityClueId"))) {
                                currentStatusType = statusType;
                            }
                        }
                        if (maxStatusType != null && currentStatusType != null && !maxStatusType.equals(currentStatusType)) {
                            custMapDao.updateOpportunityCacheStatusTypeByIdAndMapType(String.valueOf(opportunityClueId), maxStatusType, mapType);
                            logger.info("已更新StatusType: {}，mapType: {}，OpportunityClueId: {}，ProjectClueId: {}", maxStatusType, mapType, opportunityClueId, projectClueId);
                        }
                    }

                    // 线索缓存表
                    maxStatusType = null;
                    maxWeight = Integer.MIN_VALUE;
                    currentStatusType = null;
                    if (projectClueId != null && cluesListForType != null && !cluesListForType.isEmpty()) {
                        for (Map cacheItem : cluesListForType) {
                            String statusType = String.valueOf(cacheItem.get("StatusType"));
                            int weight = weightMap.getOrDefault(statusType, 0);
                            if (weight > maxWeight) {
                                maxWeight = weight;
                                maxStatusType = statusType;
                            }
                            if (projectClueId.equals(cacheItem.get("ProjectClueId"))) {
                                currentStatusType = statusType;
                            }
                        }
                        if (maxStatusType != null && currentStatusType != null && !maxStatusType.equals(currentStatusType)) {
                            custMapDao.updateCluesCacheStatusTypeByIdAndMapType(String.valueOf(projectClueId), maxStatusType, mapType);
                            logger.info("已更新StatusType: {}，mapType: {}，OpportunityClueId: {}，ProjectClueId: {}", maxStatusType, mapType, opportunityClueId, projectClueId);
                        }
                    }
                }
            }

            long endTime2 = System.currentTimeMillis();
            logger.info("更新线索缓存表耗时：" + (endTime2 - endTime2)/1000 + "s");


        } catch (Exception e) {
            logger.error("客户状态缓存更新失败", e);
            throw e;
        }
    }


    /**
     * 每天23:50执行一次客户状态缓存更新
     */
    @Scheduled(cron = "0 50 23 * * ?")
    @Transactional(rollbackFor = Exception.class)
    @ConditionalOnProperty(name = "scheduler.enabled", havingValue = "true", matchIfMissing = false)
    public void updateCustomerStatusCacheSnapshot() {
        logger.info("开始执行客户状态缓存更新任务");
        long startTime = System.currentTimeMillis();
        
        try {

        } catch (Exception e) {
            logger.error("客户状态缓存更新失败", e);
            throw e;
        }
    }
}