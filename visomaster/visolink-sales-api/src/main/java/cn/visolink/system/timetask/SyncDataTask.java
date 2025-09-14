package cn.visolink.system.timetask;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.utils.DynamicDataSource;
import cn.visolink.common.task.MultiDataSourceTimedTaskHandle;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.message.model.SysLog;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 定时从视图同步项目、组织、用户数据
 *
 * @author yangjie
 * @date 2020-9-16
 */
@Component
@EnableScheduling
public class SyncDataTask {

    private final MessageMapper messageMapper;
    private final SyncDataTaskMapper syncDataTaskMapper;
    private final MultiDataSourceTimedTaskHandle multiDataSourceTimedTaskHandle;

    private final PlatformTransactionManager platformTransactionManager;
    private final TransactionDefinition transactionDefinition;
    private final RedisUtil redisUtil;

    @Autowired
    public SyncDataTask(MessageMapper messageMapper, SyncDataTaskMapper syncDataTaskMapper, MultiDataSourceTimedTaskHandle multiDataSourceTimedTaskHandle, PlatformTransactionManager platformTransactionManager, TransactionDefinition transactionDefinition, RedisUtil redisUtil) {
        this.messageMapper = messageMapper;
        this.syncDataTaskMapper = syncDataTaskMapper;
        this.multiDataSourceTimedTaskHandle = multiDataSourceTimedTaskHandle;
        this.platformTransactionManager = platformTransactionManager;
        this.transactionDefinition = transactionDefinition;
        this.redisUtil = redisUtil;
    }

    /**
     * 同步项目
     * 每天凌晨二点
     */
//    @Scheduled(cron = "0 0 2 * * ?")
//    @Scheduled(cron = "0 0/5 * * * *")
    public void synProjectTask() {
        multiDataSourceTimedTaskHandle.externalTaskHandle(this::synProjectTaskFun);
    }

    /**
     * 同步项目代码
     */
    private void synProjectTaskFun(Map<String, Object> map) {
        TransactionStatus transactionStatus = null;
        String databaseName = map.get("DataBaseUrl").toString();
        saveLog("数据库'" + databaseName + "'：同步项目开始");
        long time1 = System.currentTimeMillis();
        try {
            // 切换外部数据源
            switchExternalDataSource(map);
            // 获取外部项目同步数据
            List<Map<String, Object>> externalProjectList = syncDataTaskMapper.getExternalProjectList();
            DynamicDataSource.clear();
            // 切换内部数据源
            switchInsideDataSource(map);
            // 获取内部项目数据
            List<Map<String, Object>> insideProjectList = syncDataTaskMapper.getInsideProjectList();

            // 封装新增、更新项目
            Map<Object, Object> insideProjectMap = insideProjectList.stream().collect(Collectors.toMap(x -> x.get("id"), x -> ""));
            List<Map> insertProjectList = new ArrayList<>();
            List<Map> updateProjectList = new ArrayList<>();
            if (CollUtil.isNotEmpty(externalProjectList)) {
                for (Map<String, Object> projectMap : externalProjectList) {
                    //根据项目ID查询项目表不存在即插入，存在即更新
                    if (insideProjectMap.get(projectMap.get("project_code").toString()) == null) {
                        insertProjectList.add(projectMap);
                    } else {
                        updateProjectList.add(projectMap);
                    }
                }
            }

            // 检验项目数量
            String companycode = map.get("CompanyCode").toString();
            Object obj = redisUtil.get(companycode + "zhyx");
            JSONObject jsonObject = JSONObject.parseObject(obj.toString());
            Integer projectNum = (Integer) jsonObject.get("projectNum");
            Long notDeletedProjectListNum = syncDataTaskMapper.getNotDeletedInsideProjectList();

            // 同步
            // 开启事务
            transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
            if (CollUtil.isNotEmpty(updateProjectList)) {
                syncDataTaskMapper.updateBatchProject(updateProjectList);
            }
            if (CollUtil.isNotEmpty(insertProjectList) && (notDeletedProjectListNum + insertProjectList.size() <= projectNum)) {
                syncDataTaskMapper.insertBatchProject(insertProjectList);
            }
            // 提交事务
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            platformTransactionManager.rollback(transactionStatus);
            DynamicDataSource.clear();
            saveLog("数据库'" + databaseName + "'：同步项目异常", e.getMessage());
            return;
        }

        DynamicDataSource.clear();
        long time2 = System.currentTimeMillis();
        System.out.println("====================:" + "Time: " + (time2 - time1));
        saveLog("数据库'" + databaseName + "'：同步项目成功");
    }

    /**
     * 同步组织
     * 每天凌晨三点
     */
//    @Scheduled(cron = "0 0 3 * * ?")
//    @Scheduled(cron = "0 0/5 * * * *")
    public void synOrganizationTask() {
        multiDataSourceTimedTaskHandle.externalTaskHandle(this::synOrganizationTaskFun);
    }

    /**
     * 同步组织代码
     */
    private void synOrganizationTaskFun(Map<String, Object> map) {
        TransactionStatus transactionStatus = null;
        String databaseName = map.get("DataBaseUrl").toString();
        saveLog("数据库'" + databaseName + "'：同步组织开始");
        long time1 = System.currentTimeMillis();
        try {
            // 切换外部数据源
            switchExternalDataSource(map);
            // 获取外部组织同步数据
            List<Map<String, Object>> externalOrganizationList = syncDataTaskMapper.getExternalOrganizationList();
            DynamicDataSource.clear();
            // 切换内部数据源
            switchInsideDataSource(map);
            // 获取内部组织数据
            List<Map<String, Object>> insideOrganizationList = syncDataTaskMapper.getInsideOrganizationList();

            // 封装新增、更新组织
            Map<Object, Object> insideOrganizationMap = insideOrganizationList.stream().collect(Collectors.toMap(x -> x.get("ID"), x -> ""));
            List<Map> insertOrganizationList = new ArrayList<>();
            List<Map> updateOrganizationList = new ArrayList<>();
            if (CollUtil.isNotEmpty(externalOrganizationList)) {
                for (Map<String, Object> organizationMap : externalOrganizationList) {
                    //根据组织ID查询组织表不存在即插入，存在即更新
                    if (insideOrganizationMap.get(organizationMap.get("organization_code").toString()) == null) {
                        insertOrganizationList.add(organizationMap);
                    } else {
                        updateOrganizationList.add(organizationMap);
                    }
                }
            }

            // 同步
            // 开启事务
            transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
            if (CollUtil.isNotEmpty(updateOrganizationList)) {
                syncDataTaskMapper.updateBatchOrganization(updateOrganizationList);
            }
            if (CollUtil.isNotEmpty(insertOrganizationList)) {
                syncDataTaskMapper.insertBatchOrganization(insertOrganizationList);
            }

            // 自动生成全路径
            organizationFullPathDataCleaning();

            // 提交事务
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            platformTransactionManager.rollback(transactionStatus);
            DynamicDataSource.clear();
            saveLog("数据库'" + databaseName + "'：同步组织异常", e.getMessage());
            return;
        }

        DynamicDataSource.clear();
        long time2 = System.currentTimeMillis();
        System.out.println("====================:" + "Time: " + (time2 - time1));
        saveLog("数据库'" + databaseName + "'：同步组织成功");
    }

    /**
     * 同步用户
     * 每天凌晨四点
     */
//    @Scheduled(cron = "0 0 4 * * ?")
//    @Scheduled(cron = "0 0/5 * * * *")
    public void synUserTask() {
        multiDataSourceTimedTaskHandle.externalTaskHandle(this::synUserTaskFun);
    }

    /**
     * 同步用户代码
     */
    private void synUserTaskFun(Map<String, Object> map) {
        TransactionStatus transactionStatus = null;
        String databaseName = map.get("DataBaseUrl").toString();
        saveLog("数据库'" + databaseName + "'：同步用户开始");
        long time1 = System.currentTimeMillis();
        try {
            // 切换外部数据源
            switchExternalDataSource(map);
            // 获取外部用户同步数据
            List<Map<String, Object>> externalUserList = syncDataTaskMapper.getExternalUserList();
            DynamicDataSource.clear();
            // 切换内部数据源
            switchInsideDataSource(map);
            // 获取内部用户数据
            List<Map<String, Object>> insideUserList = syncDataTaskMapper.getInsideUserList();

            // 封装新增、更新用户
            Map<Object, Object> insideUserMap = insideUserList.stream().collect(Collectors.toMap(x -> x.get("ID"), x -> ""));
            List<Map> insertUserList = new ArrayList<>();
            List<Map> updateUserList = new ArrayList<>();
            if (CollUtil.isNotEmpty(externalUserList)) {
                for (Map<String, Object> userMap : externalUserList) {
                    //根据用户ID查询用户表不存在即插入，存在即更新
                    if (insideUserMap.get(userMap.get("user_code").toString()) == null) {
                        insertUserList.add(userMap);
                    } else {
                        updateUserList.add(userMap);
                    }
                }
            }

            // 同步
            // 开启事务
            transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
            if (CollUtil.isNotEmpty(updateUserList)) {
                syncDataTaskMapper.updateBatchUser(updateUserList);
            }
            if (CollUtil.isNotEmpty(insertUserList)) {
                syncDataTaskMapper.insertBatchUser(insertUserList);
            }
            // 提交事务
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            platformTransactionManager.rollback(transactionStatus);
            DynamicDataSource.clear();
            saveLog("数据库'" + databaseName + "'：同步用户异常", e.getMessage());
            return;
        }

        DynamicDataSource.clear();
        long time2 = System.currentTimeMillis();
        System.out.println("====================:" + "Time: " + (time2 - time1));
        saveLog("数据库'" + databaseName + "'：同步用户成功");
    }

    /**
     * 同步字典
     * 每天凌晨五点
     */
//    @Scheduled(cron = "0 0 5 * * ?")
//    @Scheduled(cron = "0 0/5 * * * *")
    public void synDictionaryTask() {
        multiDataSourceTimedTaskHandle.externalTaskHandle(this::synDictionaryTaskFun);
    }

    /**
     * 同步字典代码
     */
    private void synDictionaryTaskFun(Map<String, Object> map) {
        TransactionStatus transactionStatus = null;
        String databaseName = map.get("DataBaseUrl").toString();
        saveLog("数据库'" + databaseName + "'：同步字典开始");
        long time1 = System.currentTimeMillis();
        try {
            // 切换外部数据源
            switchExternalDataSource(map);
            // 获取外部字典同步数据
            List<Map<String, Object>> externalDictionaryList = syncDataTaskMapper.getExternalDictionaryList();
            DynamicDataSource.clear();
            // 切换内部数据源
            switchInsideDataSource(map);
            // 获取内部字典数据
            List<Map<String, Object>> insideDictionaryList = syncDataTaskMapper.getInsideDictionaryList();

            // 封装新增、更新字典
            Map<Object, Object> insideDictionaryMap = insideDictionaryList.stream().collect(Collectors.toMap(x -> x.get("ID"), x -> ""));
            List<Map> insertDictionaryList = new ArrayList<>();
            List<Map> updateDictionaryList = new ArrayList<>();
            if (CollUtil.isNotEmpty(externalDictionaryList)) {
                for (Map<String, Object> dictionaryMap : externalDictionaryList) {
                    //根据字典ID查询字典表不存在即插入，存在即更新
                    if (insideDictionaryMap.get(dictionaryMap.get("dictionary_code").toString()) == null) {
                        insertDictionaryList.add(dictionaryMap);
                    } else {
                        updateDictionaryList.add(dictionaryMap);
                    }
                }
            }

            // 同步
            // 开启事务
            transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
            if (CollUtil.isNotEmpty(updateDictionaryList)) {
                syncDataTaskMapper.updateBatchDictionary(updateDictionaryList);
            }
            if (CollUtil.isNotEmpty(insertDictionaryList)) {
                syncDataTaskMapper.insertBatchDictionary(insertDictionaryList);
            }
            // 提交事务
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            platformTransactionManager.rollback(transactionStatus);
            DynamicDataSource.clear();
            saveLog("数据库'" + databaseName + "'：同步字典异常", e.getMessage());
            return;
        }

        DynamicDataSource.clear();
        long time2 = System.currentTimeMillis();
        System.out.println("====================:" + "Time: " + (time2 - time1));
        saveLog("数据库'" + databaseName + "'：同步字典成功");
    }


    /**
     * 切换外部数据源
     *
     * @param map map
     */
    private void switchExternalDataSource(Map<String, Object> map) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(map.get("url").toString());
        druidDataSource.setUsername(map.get("username").toString());
        druidDataSource.setPassword(map.get("password").toString());
        String databaseName = UUID.randomUUID().toString();
        DynamicDataSource.dataSourcesMap.put(databaseName, druidDataSource);
        DynamicDataSource.setDataSource(databaseName);
    }

    /**
     * 切换内部数据源
     *
     * @param map map
     */
    private void switchInsideDataSource(Map<String, Object> map) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(map.get("insideUrl").toString());
        druidDataSource.setUsername(map.get("insideUsername").toString());
        druidDataSource.setPassword(map.get("insidePassword").toString());
        DynamicDataSource.dataSourcesMap.put(map.get("DataBaseUrl").toString(), druidDataSource);
        DynamicDataSource.setDataSource(map.get("DataBaseUrl").toString());
    }

    /**
     * 保存日志
     *
     * @param taskName taskName
     */
    private void saveLog(String taskName) {
        saveLog(taskName, null);
    }

    /**
     * 保存日志
     *
     * @param taskName taskName
     * @param note     note
     */
    private void saveLog(String taskName, String note) {
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(DateUtil.now());
        sysLog.setTaskName(taskName);
        sysLog.setNote(note);
        messageMapper.insertLogs(sysLog);
    }

    /**
     * 组织全路径数据清洗
     */
    public void organizationFullPathDataCleaning() {
        // 获取组织信息
        List<Map<String, Object>> organizationList = syncDataTaskMapper.getOrganizationFullPath();
        if (CollUtil.isEmpty(organizationList)) {
            return;
        }

        // 生成组织全路径
        List<Map<String, Object>> newOrganizationList = generateOrganizationFullPath(organizationList);

        // 批量更新组织全路径
        if (CollUtil.isNotEmpty(newOrganizationList)) {
            syncDataTaskMapper.updateBatchOrganizationFullPath(newOrganizationList);
        }
    }

    /**
     * 生成组织全路径
     *
     * @param organizationList organizationList
     * @return return
     */
    private List<Map<String, Object>> generateOrganizationFullPath(List<Map<String, Object>> organizationList) {
        // 封装根组织
        Map<String, Map<String, Object>> newOrganizationMap = new HashMap<>(organizationList.size() * 2);
        Iterator<Map<String, Object>> organizationIterator = organizationList.iterator();
        while (organizationIterator.hasNext()) {
            Map<String, Object> organization = organizationIterator.next();
            if (ObjectUtil.equal("-1", organization.get("PID")) || ObjectUtil.isNull(organization.get("PID")) || StrUtil.isBlank(organization.get("PID").toString())) {
                if (!ObjectUtil.equal(organization.get("OrgName"), organization.get("FullPath"))) {
                    organization.put("FullPath", organization.get("OrgName"));
                    newOrganizationMap.put(organization.get("ID").toString(), organization);
                }
                organizationIterator.remove();
            }
        }

        // 封装子组织
        packageSubOrganization(newOrganizationMap, organizationList);

        return new ArrayList<>(newOrganizationMap.values());
    }

    /**
     * 封装子组织
     *
     * @param newOrganizationMap newOrganizationMap
     * @param organizationList   organizationList
     */
    private void packageSubOrganization(Map<String, Map<String, Object>> newOrganizationMap, List<Map<String, Object>> organizationList) {
        boolean boo = false;
        Map<String, Map<String, Object>> newOrganizationMap2 = new HashMap<>((newOrganizationMap.size() + organizationList.size()) * 2);
        Iterator<Map<String, Object>> organizationIterator = organizationList.iterator();
        while (organizationIterator.hasNext()) {
            Map<String, Object> organization = organizationIterator.next();
            Map<String, Object> pOrganization = newOrganizationMap.get(organization.get("PID").toString());
            if (ObjectUtil.isNotNull(pOrganization)) {
                String fullPath = pOrganization.get("FullPath").toString() + "/" + organization.get("OrgName");
                if (!ObjectUtil.equal(fullPath, organization.get("FullPath"))) {
                    organization.put("FullPath", fullPath);
                    newOrganizationMap2.put(organization.get("ID").toString(), organization);
                }
                organizationIterator.remove();
                boo = true;
            }
        }

        newOrganizationMap.putAll(newOrganizationMap2);

        // 迭代
        if (CollUtil.isNotEmpty(organizationList) && boo) {
            packageSubOrganization(newOrganizationMap, organizationList);
        }
    }
}
