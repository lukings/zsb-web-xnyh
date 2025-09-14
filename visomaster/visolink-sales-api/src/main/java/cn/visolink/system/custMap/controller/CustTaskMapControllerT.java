package cn.visolink.system.custMap.controller;

import cn.visolink.common.ClueAcquisitionStatusEnum;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.channel.dao.DataStatisticDao;
import cn.visolink.system.companyQw.util.DateUtils;
import cn.visolink.system.custMap.bo.ZsMapPermissionsBO;
import cn.visolink.system.custMap.bo.ZsMapResBO;
import cn.visolink.system.custMap.dao.CustMapDao;
import cn.visolink.system.custMap.dao.ZsMapPermissionsDao;
import cn.visolink.system.custMap.service.ZsMapService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.homepage.service.WorkbenchService;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName CustMapController
 * @Author wanggang
 * @Description //客户地图
 * @Date 2022/9/1 17:49
 **/
@RestController
@RequestMapping("/custMap")
public class CustTaskMapControllerT {

    @Autowired
    private CustMapDao custMapDao;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private DataStatisticDao dataStatisticDao;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private ZsMapService zsMapService;
    @Autowired
    private ZsMapPermissionsDao zsMapPermissionsDao;
    @Autowired
    private WorkbenchService workbenchService;

    @PostMapping("/taskMapT")
    public ResultBody taskMapT(@RequestBody Map<String, Object> paramMap) {
        // 1. 初始化基础参数
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userId = request.getHeader("userid");
        String userName = request.getHeader("username");
        if(StringUtils.isEmpty(userId)){
            userId = SecurityUtils.getUserId();
        }
        if(StringUtils.isEmpty(userName)){
            userName = SecurityUtils.getUsername();
        }

        // 2. 设置默认值和通用参数
        paramMap.putIfAbsent("mapType", "2");
        paramMap.putIfAbsent("dataRange", "1");
        paramMap.put("UserName", userName);
        paramMap.put("UserId", userId);

        // 3. 处理区域权限
        Map<String, Object> result = new HashMap<>();
        if (!handleAreaPermissions(paramMap, result)) {
            return ResultBody.success(result);
        }

        // 4. 处理团队和项目信息
        handleTeamAndProjectInfo(paramMap);

        // 5. 执行查询并构建消息
        performAllQueries(paramMap, result);

        return ResultBody.success(result);
    }

    private void performAllQueries(Map<String, Object> paramMap, Map<String, Object> result) {
        // 1. 获取查询类型和状态
        String flagType = (String) paramMap.get("flagType");
        String clueStatus = (String) paramMap.get("clueStatus");
        String taskName = (String) paramMap.get("taskName");
        List<String> clueStatusList = initClueStatus(clueStatus, taskName, flagType);

        // 2. 执行查询
        List<ZsMapResBO> zsMapResBOList = new ArrayList<>();
        StringBuilder messageBuilder = new StringBuilder();

        // 2.1 查询商机
        if (shouldQueryOpportunity(flagType) && hasOpportunityStatus(clueStatusList)) {
            List<ZsMapResBO> opportunityResults = queryOpportunityCustomers(paramMap, clueStatusList);
            zsMapResBOList.addAll(opportunityResults);
            appendStatusMessage(messageBuilder, opportunityResults, clueStatusList);
        }

        // 2.2 查询线索
        if (shouldQueryClue(flagType) && hasClueStatus(clueStatusList)) {
            paramMap.put("isMapImport",'0');
            List<ZsMapResBO> clueResults = queryClueCustomers(paramMap, clueStatusList);
            zsMapResBOList.addAll(clueResults);
            appendStatusMessage(messageBuilder, clueResults, clueStatusList);
        }

        // 2.3 查询公客池
        if (shouldQueryPublicPool(flagType)) {
            List<ZsMapResBO> publicPoolResults = queryPublicPoolCustomers(paramMap);
            zsMapResBOList.addAll(publicPoolResults);
            appendPublicPoolMessage(messageBuilder, publicPoolResults);
        }

        // 2.4 查询地图导入
        if (shouldQueryMapImport(flagType)) {
            paramMap.put("isMapImport",'1');
            List<ZsMapResBO> mapImportResults = queryMapImportCustomers(paramMap, clueStatusList);
            zsMapResBOList.addAll(mapImportResults);
            appendMapImportMessage(messageBuilder, mapImportResults, clueStatusList);
        }

        // 3. 设置返回结果
        result.put("zsMapResBOList", zsMapResBOList);
        result.put("message", messageBuilder.toString());
    }

    // 处理区域权限
    private boolean handleAreaPermissions(Map paramMap, Map<String, Object> result) {
        List<List<String>> areaDataList = (List<List<String>>) paramMap.get("areaData");
        if (!CollectionUtils.isEmpty(areaDataList)) {
            List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
                    Stream.of((String)paramMap.get("UserId")).collect(Collectors.toList()));

            if (!CollectionUtils.isEmpty(mapPermissions)) {
                List<List<String>> dbAreaDataList = new ArrayList<>();
                for (ZsMapPermissionsBO zsMapPermissionsBO : mapPermissions) {
                    if (StringUtils.isNotBlank(zsMapPermissionsBO.getAreaPermissions())
                            && zsMapPermissionsBO.getAreaEndDate()
                            .compareTo(DateUtils.getDateAfterDays(new Date(), -1)) > 0) {
                        List<String> areaPermissionsList = JSON.parseArray(
                                zsMapPermissionsBO.getAreaPermissions(), String.class);
                        for (String areaPerm : areaPermissionsList) {
                            List<String> strings = JSON.parseArray(areaPerm, String.class);
                            dbAreaDataList.add(strings);
                        }
                    }
                }
                areaDataList = areaDataList.stream()
                        .filter(a -> dbAreaDataList.contains(a))
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(areaDataList)) {
                    result.put("message", "");
                    result.put("zsMapResBOList", new ArrayList<>());
                    return false;
                }
                List<String> areaList = new ArrayList<>();
                areaDataList.forEach(areaData -> {
                    String string = StringUtils.join(areaData, "");
                    string = string.replaceAll(",", "");
                    areaList.add(string);
                });
                paramMap.put("areaList", areaList);
            }
        }
        return true;
    }

    // 处理团队和项目信息
    private void handleTeamAndProjectInfo(Map paramMap) {
        // 处理团队ID
        if (paramMap.get("teamIds") != null) {
            List<String> teamIdList = new ArrayList<>();
            List<List<String>> teamIds = (List<List<String>>) paramMap.get("teamIds");
            for (List<String> list : teamIds) {
                teamIdList.add(list.get(list.size() - 1));
            }
            paramMap.put("teamIds", teamIdList);
        }

        // 获取项目列表
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(paramMap);
        List<String> fullpath = projectMapper.findFullPathAllHasUser(paramMap);
        List<ProjectVO> projectList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(fullpath)) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sb.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sb.append(" or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            paramMap.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(paramMap);
        }

        if (!CollectionUtils.isEmpty(qyProjectList)) {
            for (ProjectVO project : qyProjectList) {
                if (!projectList.contains(project)) {
                    projectList.add(project);
                }
            }
        }

        // 处理项目ID
        if (paramMap.get("projectIds") != null) {
            List<String> projectIds = (List<String>) paramMap.get("projectIds");
            projectList = new ArrayList<>();
            for (String projectId : projectIds) {
                ProjectVO pro = new ProjectVO();
                pro.setProjectId(projectId);
                projectList.add(pro);
            }
        } else {
            projectList = new ArrayList<>();
            ProjectVO pro = new ProjectVO();
            pro.setProjectId("not exist projectId");
            projectList.add(pro);
        }

        // 设置项目ID列表
        List<String> projectIds = projectList.stream()
                .map(ProjectVO::getProjectId)
                .distinct()
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(projectIds)) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < projectIds.size(); i++) {
                if (i == 0) {
                    stringBuffer.append("'" + projectIds.get(i) + "'");
                } else {
                    stringBuffer.append("," + "'" + projectIds.get(i) + "'");
                }
            }
            paramMap.put("projectList", stringBuffer.toString());

            // 获取组织ID
            List<String> orgIds = workbenchService.linkOrgIds((String)paramMap.get("UserId"), projectIds);
            paramMap.put("orgIds", orgIds);
        }

        // 获取团队人员ID
        List<String> userIds = new ArrayList<>();
        userIds.add((String)paramMap.get("UserId"));
        List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
        if (!CollectionUtils.isEmpty(userList)) {
            for (Map user : userList) {
                String jobCode = String.valueOf(user.get("JobCode"));
                if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)
                        || "yxjl".equals(jobCode) || "qyyxjl".equals(jobCode)
                        || "zszj".equals(jobCode) || "qyzszj".equals(jobCode)
                        || "xmz".equals(jobCode) || "qyfz".equals(jobCode) || "qyz".equals(jobCode)) {
                    String orgPath = String.valueOf(user.get("FullPath"));
                    List<String> teamUserIds = custMapDao.getTeamUserNew(orgPath);
                    userIds.addAll(teamUserIds);
                }
            }
        }
        paramMap.put("userIds", userIds);
    }

    // 查询商机客户
    private List<ZsMapResBO> queryOpportunityCustomers(Map paramMap, List<String> clueStatusList) {
        List<ZsMapResBO> opportunitys = custMapDao.getOpportunityFromCache(paramMap);

        if (StringUtils.isNotEmpty((String) paramMap.get("latLon"))) {
            opportunitys = filterByLatLon(opportunitys, (String) paramMap.get("latLon"));
        }

        return opportunitys.stream()
                .filter(item -> clueStatusList.contains(item.getType()))
                .collect(Collectors.toList());
    }

    // 查询线索客户
    private List<ZsMapResBO> queryClueCustomers(Map paramMap, List<String> clueStatusList) {
        List<ZsMapResBO> clues = custMapDao.getCluesFromCache(paramMap);

        if (StringUtils.isNotEmpty((String) paramMap.get("latLon"))) {
            clues = filterByLatLon(clues, (String) paramMap.get("latLon"));
        }

        return clues.stream()
                .filter(item -> clueStatusList.contains(item.getType()))
                .collect(Collectors.toList());
    }

    // 查询公客池客户
    private List<ZsMapResBO> queryPublicPoolCustomers(Map paramMap) {
        List<ZsMapResBO> publicPools = new ArrayList<>();
        int pageSize = 500000;
        int pageIndex = 0;

        while (true) {
            paramMap.put("pageSize", pageSize);
            paramMap.put("pageIndex", pageIndex * pageSize);

            List<ZsMapResBO> batchList = custMapDao.getPublicPoolPage(paramMap);
            if (batchList == null || batchList.isEmpty()) {
                break;
            }

            if (StringUtils.isNotEmpty((String) paramMap.get("latLon"))) {
                batchList = filterByLatLon(batchList, (String) paramMap.get("latLon"));
            }

            publicPools.addAll(batchList);

            if (batchList.size() < pageSize) {
                break;
            }
            pageIndex++;
        }

        return publicPools;
    }

    // 查询地图导入客户
    private List<ZsMapResBO> queryMapImportCustomers(Map paramMap, List<String> clueStatusList) {
        List<ZsMapResBO> clues = custMapDao.getCluesFromCache(paramMap);
        List<ZsMapResBO> cluesHis = custMapDao.getCluesTkdtHis(paramMap);

        if (StringUtils.isNotEmpty((String) paramMap.get("latLon"))) {
            clues = filterByLatLon(clues, (String) paramMap.get("latLon"));
            cluesHis = filterByLatLon(cluesHis, (String) paramMap.get("latLon"));
        }

        if ("1".equals(paramMap.get("isTaskCreate"))) {
            clues = filterTargetEnterprises(clues);
            cluesHis = filterTargetEnterprises(cluesHis);
        }

        return combineMapImportResults(clues, cluesHis, clueStatusList);
    }

    // 辅助方法：过滤经纬度范围内的数据
    private List<ZsMapResBO> filterByLatLon(List<ZsMapResBO> dataList, String latLon) {
        return dataList.stream()
                .filter(item -> {
                    if (StringUtils.isNotEmpty(item.getLongitude()) && StringUtils.isNotEmpty(item.getLatitude())) {
                        try {
                            double lng = Double.parseDouble(item.getLongitude());
                            double lat = Double.parseDouble(item.getLatitude());
                            return GeoUtils.checkPointInArea(latLon, lng, lat) == 1;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    // 辅助方法：过滤目标企业
    private List<ZsMapResBO> filterTargetEnterprises(List<ZsMapResBO> dataList) {
        return dataList.stream()
                .filter(a -> "8".equals(a.getType()))
                .collect(Collectors.toList());
    }

    // 辅助方法：判断是否应该查询商机客户
    private boolean shouldQueryOpportunity(String flagType) {
        return StringUtils.isEmpty(flagType) || "0".equals(flagType) || "1".equals(flagType);
    }

    // 辅助方法：判断是否应该查询线索客户
    private boolean shouldQueryClue(String flagType) {
        return StringUtils.isEmpty(flagType) || "0".equals(flagType) || "4".equals(flagType) || "3".equals(flagType);
    }

    // 辅助方法：判断是否应该查询公客池
    private boolean shouldQueryPublicPool(String flagType) {
        return StringUtils.isEmpty(flagType) || "0".equals(flagType) || flagType.indexOf("2") != -1;
    }

    // 辅助方法：判断是否应该查询地图导入
    private boolean shouldQueryMapImport(String flagType) {
        return StringUtils.isEmpty(flagType) || "0".equals(flagType) || "3".equals(flagType);
    }

    // 辅助方法：判断是否包含商机状态
    private boolean hasOpportunityStatus(List<String> clueStatusList) {
        return clueStatusList.contains("1") || clueStatusList.contains("2") ||
                clueStatusList.contains("7") || clueStatusList.contains("6") ||
                clueStatusList.contains("4") || clueStatusList.contains("8");
    }

    // 辅助方法：判断是否包含线索状态
    private boolean hasClueStatus(List<String> clueStatusList) {
        return clueStatusList.contains("3") || clueStatusList.contains("5") ||
                clueStatusList.contains("9") || clueStatusList.contains("8");
    }

    // 辅助方法：添加状态消息
    private void appendStatusMessage(StringBuilder messageBuilder, List<ZsMapResBO> results, List<String> clueStatusList) {
        for (String status : clueStatusList) {
            List<ZsMapResBO> filteredResults = results.stream()
                    .filter(item -> status.equals(item.getType()))
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(filteredResults)) {
                messageBuilder.append(ClueAcquisitionStatusEnum.getNameByStatus(status))
                        .append("的客户有")
                        .append(filteredResults.size())
                        .append("组;");
            }
        }
    }

    // 辅助方法：添加公客池消息
    private void appendPublicPoolMessage(StringBuilder messageBuilder, List<ZsMapResBO> results) {
        if (!CollectionUtils.isEmpty(results)) {
            messageBuilder.append("公客池的客户有")
                    .append(results.size())
                    .append("组;");
        }
    }

    // 辅助方法：添加地图导入消息
    private void appendMapImportMessage(StringBuilder messageBuilder, List<ZsMapResBO> results, List<String> clueStatusList) {
        appendStatusMessage(messageBuilder, results, clueStatusList);
    }

    // 辅助方法：合并地图导入结果
    private List<ZsMapResBO> combineMapImportResults(List<ZsMapResBO> clues, List<ZsMapResBO> cluesHis, List<String> clueStatusList) {
        List<ZsMapResBO> results = new ArrayList<>();

        // 处理历史数据
        for (String status : clueStatusList) {
            List<ZsMapResBO> filteredHis = cluesHis.stream()
                    .filter(item -> status.equals(item.getType()))
                    .collect(Collectors.toList());
            results.addAll(filteredHis);
        }

        // 处理当前数据
        List<ZsMapResBO> clueMap = clues.stream()
                .filter(a -> "招商地图导入".equals(a.getSourceMode()))
                .filter(a -> "8".equals(a.getType()))
                .collect(Collectors.toList());

        // 过滤掉已存在的目标企业
        Set<String> existingIds = results.stream()
                .map(ZsMapResBO::getProjectClueId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        clueMap = clueMap.stream()
                .filter(a -> !existingIds.contains(a.getProjectClueId()))
                .collect(Collectors.toList());

        results.addAll(clueMap);

        return results;
    }

    private List<String> initClueStatus(String clueStatus, String taskName, String flagType) {
        // 如果指定了具体的状态，则优先使用
        if (StringUtils.isNotBlank(clueStatus)) {
            return Stream.of(clueStatus).collect(Collectors.toList());
        }

        // 根据flagType决定查询的状态列表
        if (StringUtils.isNotBlank(flagType)) {
            switch (flagType) {
                case "1": // 在保客户 (只查询商机相关状态)
                    return Stream.of("1", "2", "4", "6", "7", "8").collect(Collectors.toList());
                case "4": // 线索客户 (只查询线索相关状态)
                    return Stream.of("3", "5", "8", "9").collect(Collectors.toList());
                case "3": // 地图导入客户 (也属于线索范畴)
                    return Stream.of("3", "5", "8", "9").collect(Collectors.toList());
                default:
                    // 其他如 "0", "2" 或未明确定义的flagType，查询所有
                    break;
            }
        }

        // 任务名称的逻辑，当前被注释
        if (StringUtils.isNotBlank(taskName)) {
            // clueStatusList = Stream.of("1", "2", "3", "4", "8").collect(Collectors.toList());
        }

        // 默认查询所有状态
        return Stream.of("1", "2", "3", "4", "5", "6", "7", "8", "9").collect(Collectors.toList());
    }

}
