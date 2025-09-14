package cn.visolink.system.custMap.controller;

import cn.visolink.common.ClueStatusEnum;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.channel.dao.DataStatisticDao;
import cn.visolink.system.companyQw.util.DateUtils;
import cn.visolink.system.custMap.bo.*;
import cn.visolink.system.custMap.dao.CustMapDao;
import cn.visolink.system.custMap.dao.ZsMapPermissionsDao;
import cn.visolink.system.custMap.service.ZsMapService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.homepage.service.WorkbenchService;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.utils.SecurityUtils;
import com.alibaba.fastjson.JSON;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
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
public class CustZsMapController {

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
    private static final Logger logger = LoggerFactory.getLogger(CustZsMapControllerT.class);

    @Log("更新招商地图客户阶段缓存数据")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/updateCustomerStatusCache", method = RequestMethod.GET)
    public String updateCustomerStatusCache(){
        long startTime = System.currentTimeMillis();
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

        // 处理招商地图
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

        long endTime2 = System.currentTimeMillis();
        logger.info("更新线索缓存表耗时：" + (endTime2 - endTime)/1000 + "s");

        return "调用成功！！";
    }

    /**
     * @return cn.visolink.exception.ResultBody
     * @Author wanggan
     * @Description //获取招商地图
     * @Date 11:20 2022/8/30
     * @Param [city, tokerTime, type]
     **/
    @PostMapping("/zsMapA")
    public ResultBody zsMapA(@RequestBody Map paramMap) {
        long startTime = System.currentTimeMillis();
        long stepTime = startTime;
        long currentTime;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String userId=request.getHeader("userid");
        String userName=request.getHeader("username");
        if(StringUtils.isEmpty(userId)){
            userId=SecurityUtils.getUserId();
        }
        if(StringUtils.isEmpty(userName)){
            userName=SecurityUtils.getUsername();
        }
        String mapType = (String) paramMap.get("mapType");  // 1-客户地图 2-拓客地图
        String dataRange = (String) paramMap.get("dataRange");  // 1-项目 2-区域 3-集团
        // 设置默认值
        if (StringUtils.isEmpty(mapType)) {
            mapType = "1";  // 默认为客户地图
        }
        if (StringUtils.isEmpty(dataRange)) {
            dataRange = "1";  // 默认为项目范围
        }
        // 将参数传递给DAO层
        paramMap.put("mapType", mapType);
        paramMap.put("dataRange", dataRange);
        // 创建一个Map来存储各个步骤的执行时间
        Map<String, Object> timeStatistics = new LinkedHashMap<>();
        // 创建一个Map来存储DAO查询信息
        Map<String, Object> daoStatistics = new LinkedHashMap<>();

        List<String> areaList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<List<String>> areaDataList = (List<List<String>>) paramMap.get("areaData");
        List<String> provinceList = (List<String>) paramMap.get("provinceArray");
        List<String> cityList = (List<String>) paramMap.get("cityArray");
        List<String> countList = (List<String>) paramMap.get("countArray");
        String customerName = (String) paramMap.get("customerName");
        String tagLabel = (String) paramMap.get("tagLabel");

        String latLon = (String) paramMap.get("latLon");//区域经纬度集合 [{\"lng\":113.655672,\"lat\":34.810713},{\"lng\":113.667745,\"lat\":34.811425},{\"lng\":113.679746,\"lat\":34.811721,\"pf\":\"inner\"},{\"lng\":113.662858,\"lat\":34.792158}]
        String custNature = (String) paramMap.get("custNature");//1-仅看专员手动录入的客户、2-仅看下发的客户、3-仅看该任务客户（下发+手动录入的客户）、4-仅看其他客户标注（若有权限）、5-该网格内全部客户（若有权限）
        String isTaskCreate = (String) paramMap.get("isTaskCreate");//0-正常查询 1-创建任务选择客户查询

        if (customerName != null && !"".equals(customerName)) {
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (customerName.matches("[0-9]+")) {
                paramMap.put("customerMobile", customerName);
                paramMap.put("customerName", null);
            }
        }
        if (!CollectionUtils.isEmpty(areaDataList)) {
            currentTime = System.currentTimeMillis();
            timeStatistics.put("参数处理耗时", currentTime - stepTime);
            stepTime = currentTime;

            List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
                    Stream.of(userId).collect(Collectors.toList()));

            // 记录DAO查询信息
            Map<String, Object> daoInfo = new HashMap<>();
            daoInfo.put("dao名称", "zsMapPermissionsDao.getMapPermissions");
            daoInfo.put("记录数", mapPermissions != null ? mapPermissions.size() : 0);
            daoStatistics.put("获取地图权限", daoInfo);

            currentTime = System.currentTimeMillis();
            timeStatistics.put("获取地图权限耗时", currentTime - stepTime);
            stepTime = currentTime;

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
                areaDataList = areaDataList.stream().filter(a -> dbAreaDataList.contains(a)).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(areaDataList) || areaDataList.size() == 0) {
                    result.put("message", "");
                    result.put("zsMapResBOList", new ArrayList<>());
                    return ResultBody.success(result);
                }
                areaDataList.forEach(areaData -> {
                    String string = StringUtils.join(areaData, "");
                    string = string.replaceAll(",", "");
                    areaList.add(string);
                });
                paramMap.put("areaList", areaList);

                currentTime = System.currentTimeMillis();
                timeStatistics.put("处理地图权限耗时", currentTime - stepTime);
                stepTime = currentTime;
            }
        }

        if (paramMap.get("teamIds") != null) {
            List<String> teamIdList = new ArrayList<>();
            List<List<String>> teamIds = (List<List<String>>) paramMap.get("teamIds");
            for (List<String> list : teamIds) {
                teamIdList.add(list.get(list.size() - 1));
            }
            paramMap.put("teamIds", teamIdList);
        }
        paramMap.put("UserName", userName);
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        paramMap.put("UserId", userId);
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(paramMap);

        // 记录DAO查询信息
        Map<String, Object> daoInfo = new HashMap<>();
        daoInfo.put("dao名称", "projectMapper.getProjectListByUserIdAndQy");
        daoInfo.put("记录数", qyProjectList != null ? qyProjectList.size() : 0);
        daoStatistics.put("获取区域项目列表", daoInfo);

        currentTime = System.currentTimeMillis();
        timeStatistics.put("获取区域项目列表耗时", currentTime - stepTime);
        stepTime = currentTime;

        //查询地图授权
        List<String> fullpath = new ArrayList<>();
        List<ProjectVO> projectList = new ArrayList<>();

        //获取登陆人项目权限
        List<String> fullpath1 = projectMapper.findFullPathAllHasUser(paramMap);

        daoInfo = new HashMap<>();
        daoInfo.put("dao名称", "projectMapper.findFullPathAllHasUser");
        daoInfo.put("记录数", fullpath1 != null ? fullpath1.size() : 0);
        daoStatistics.put("获取用户项目权限", daoInfo);

        currentTime = System.currentTimeMillis();
        timeStatistics.put("获取用户项目权限耗时", currentTime - stepTime);
        stepTime = currentTime;

        fullpath.addAll(fullpath1);
        StringBuffer sb = new StringBuffer();
        if (fullpath == null || fullpath.size() == 0) {
            if (CollectionUtils.isEmpty(qyProjectList)) {
                throw new BadRequestException(-10_0000, "用户无项目权限！");
            }
        } else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sb.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sb.append(" or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            paramMap.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(paramMap);

            // 记录DAO查询信息
            daoInfo = new HashMap<>();
            daoInfo.put("dao名称", "projectMapper.getProjectListByUserName");
            daoInfo.put("记录数", projectList != null ? projectList.size() : 0);
            daoStatistics.put("获取项目列表", daoInfo);


            currentTime = System.currentTimeMillis();
            timeStatistics.put("获取项目列表耗时", currentTime - stepTime);
            stepTime = currentTime;
        }
        if (!CollectionUtils.isEmpty(qyProjectList)) {
            for (int i = 0; i < qyProjectList.size(); i++) {
                if (!projectList.contains(qyProjectList.get(i))) {
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
//        if (projectList==null || projectList.size()==0){
//            throw new BadRequestException(-10_0000,"用户无项目权限！");
//        }
        String jobCode = paramMap.get("jobCode") + "";
        //获取人员最高权限-可以根据人员岗位的组织层级
        List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);

        // 记录DAO查询信息
        daoInfo = new HashMap<>();
        daoInfo.put("dao名称", "projectMapper.findFullPathAllInsZs");
        daoInfo.put("记录数", userList != null ? userList.size() : 0);
        daoStatistics.put("获取人员权限", daoInfo);

        currentTime = System.currentTimeMillis();
        timeStatistics.put("获取人员权限耗时", currentTime - stepTime);
        stepTime = currentTime;

        int level = 0;

        if (paramMap.get("projectIds") != null) {
            List<String> projectIds = (List<String>) paramMap.get("projectIds");
            projectList = new ArrayList<>();//没选按权限查，选了按中的查
            for (String projectId : projectIds) {
                ProjectVO pro = new ProjectVO();
                pro.setProjectId(projectId);
                projectList.add(pro);
            }
        }else{
            projectList = new ArrayList<>();//没选按权限查，选了按中的查
                ProjectVO pro = new ProjectVO();
                pro.setProjectId("not exist projectId");
                projectList.add(pro);
        }
        /*if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            projectList = new ArrayList<>();
            ProjectVO pro = new ProjectVO();
            pro.setProjectId(paramMap.get("projectId")+"");
            projectList.add(pro);
        }*/
        List<String> projectIds = projectList.stream().map(ProjectVO::getProjectId).distinct().collect(Collectors.toList());
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
            //限制经理、总监、营销经理
            List<String> orgIds = workbenchService.linkOrgIds(userId,
                    projectIds);

            // 记录DAO查询信息
            daoInfo = new HashMap<>();
            daoInfo.put("dao名称", "workbenchService.linkOrgIds");
            daoInfo.put("记录数", orgIds != null ? orgIds.size() : 0);
            daoStatistics.put("获取组织ID", daoInfo);


            currentTime = System.currentTimeMillis();
            timeStatistics.put("获取组织ID耗时", currentTime - stepTime);
            stepTime = currentTime;

            paramMap.put("orgIds", orgIds);
        }

        //获取团队人员Id
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);//自己
        if (!CollectionUtils.isEmpty(userList)){
                for (int i = 0; i < userList.size(); i++) {
                jobCode=String.valueOf(userList.get(i).get("JobCode"));
                if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)
                        || "yxjl".equals(jobCode) || "qyyxjl".equals(jobCode)
                        || "zszj".equals(jobCode) || "qyzszj".equals(jobCode)
                        || "xmz".equals(jobCode) || "qyfz".equals(jobCode) ||"qyz".equals(jobCode)) {

                    String orgPath=String.valueOf(userList.get(i).get("FullPath"));

                    //查询置业顾问
                    List<String> userIds1 = custMapDao.getTeamUserNew(orgPath);
                    userIds.addAll(userIds1);
                }
            }
        }
        paramMap.put("userIds", userIds);
        Map map = new HashMap();
        String type = paramMap.get("type") + "";
        //标记类型
        String flagType = "";
        if (paramMap.get("flagType") != null) {
            flagType = paramMap.get("flagType") + "";
        }
        //任务名称
        String taskName = "";
        if (paramMap.get("taskName") != null) {
            taskName = paramMap.get("taskName") + "";
        }
        //客户状态
        String clueStatus = "";
        if (paramMap.get("clueStatus") != null) {
            clueStatus = paramMap.get("clueStatus") + "";
        }

        List<String> clueStatusList = initClueStatus(clueStatus, taskName, flagType);
        StringBuffer str = new StringBuffer();
        List<ZsMapResBO> zsMapResBOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(clueStatusList)) {
            boolean isPublic = false;
            boolean isSales = false;
            boolean isMap = false;
            if (StringUtils.isEmpty(flagType) || "0".equals(flagType)) {
                isPublic = true;
                isSales = true;
                isMap = true;
            } else {
                if ("1".equals(flagType)) {
                    isSales = true;
                }
                //flagType
                //原始0-全部；1-在保；2-公客池
                //改造后（管理者）0-项目全部客户；1-在保客户；21-项目公客池；22-区域公客池客户；23-全国公客池客户 3-地图导入客户
                //改造后（专员）  0-项目全部客户；1-线索客户（自己录入的+公客池分配的+任务下发的客户）；21-项目公客池客户；22区域公客池客户；23全国公客池客户
                if (flagType.indexOf("2") != -1) {
                    isPublic = true;
                }
                if ("3".equals(flagType)) {
                    isMap = true;
                }
            }
            // 在zsMapT方法中，将原来的查询替换为缓存查询
            if (isSales) {
                if (clueStatusList.contains("1") || clueStatusList.contains("2")
                        || clueStatusList.contains("3") || clueStatusList.contains("4")
                        || clueStatusList.contains("5")) {
                    // 使用缓存表查询替代原来的直接查询
                    List<ZsMapResBO> opportunitys = custMapDao.getOpportunityFromCache(paramMap);

                    // 如果经纬度参数不为空，过滤掉不在经纬度范围内的数据
                    if (StringUtils.isNotEmpty(latLon)) {
                        opportunitys = opportunitys.stream()
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

                    // 记录DAO查询信息
                    daoInfo = new HashMap<>();
                    daoInfo.put("dao名称", "custMapDao.getOpportunityFromCache");
                    daoInfo.put("记录数", opportunitys != null ? opportunitys.size() : 0);
                    daoStatistics.put("获取商机客户", daoInfo);

                    currentTime = System.currentTimeMillis();
                    timeStatistics.put("获取商机客户耗时", currentTime - stepTime);
                    stepTime = currentTime;


                    // 后续处理逻辑保持不变
                    List<ZsMapResBO> s1 = opportunitys.stream()
                            .filter(a -> "1".equals(a.getType())).collect(Collectors.toList());
                    List<ZsMapResBO> s2 = opportunitys.stream()
                            .filter(a -> "2".equals(a.getType())).collect(Collectors.toList());
                    List<ZsMapResBO> s3 = opportunitys.stream()
                            .filter(a -> "3".equals(a.getType())).collect(Collectors.toList());
                    List<ZsMapResBO> s4 = opportunitys.stream()
                            .filter(a -> "4".equals(a.getType())).collect(Collectors.toList());
                    //已成交1-8
                    List<ZsMapResBO> s5 = opportunitys.stream()
                            .filter(a -> "5".equals(a.getType())).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(s1) && s1.size() > 0 && clueStatusList.contains("1")) {
                        str.append(ClueStatusEnum.getNameByStatus("1") + "的客户有"
                                + s1.size() + "组;");
                        zsMapResBOList.addAll(s1);
                    }
                    if (!CollectionUtils.isEmpty(s2) && s2.size() > 0 && clueStatusList.contains("2")) {
                        str.append(ClueStatusEnum.getNameByStatus("2") + "的客户有"
                                + s2.size() + "组;");
                        zsMapResBOList.addAll(s2);
                    }
                    if (!CollectionUtils.isEmpty(s3) && s3.size() > 0 && clueStatusList.contains("3")) {
                        str.append(ClueStatusEnum.getNameByStatus("3") + "的客户有"
                                + s3.size() + "组;");
                        zsMapResBOList.addAll(s3);
                    }
                    if (!CollectionUtils.isEmpty(s4) && s4.size() > 0 && clueStatusList.contains("4")) {
                        str.append(ClueStatusEnum.getNameByStatus("4") + "的客户有"
                                + s4.size() + "组;");
                        zsMapResBOList.addAll(s4);
                    }
                    if (!CollectionUtils.isEmpty(s5) && s5.size() > 0 && clueStatusList.contains("5")) {
                        str.append(ClueStatusEnum.getNameByStatus("5") + "的客户有"
                                + s5.size() + "组;");
                        zsMapResBOList.addAll(s5);
                    }
                }
                // 6789查线索
                if (clueStatusList.contains("6") || clueStatusList.contains("7") || clueStatusList.contains("8")||clueStatusList.contains("9")) {
                    //List<ZsMapResBO> clues = custMapDao.getClues(paramMap);
                    List<ZsMapResBO> clues = custMapDao.getCluesFromCache(paramMap);

                    // 记录DAO查询信息
                    daoInfo = new HashMap<>();
                    daoInfo.put("dao名称", "custMapDao.getClues");
                    daoInfo.put("记录数", clues != null ? clues.size() : 0);
                    daoStatistics.put("获取线索客户", daoInfo);


                    currentTime = System.currentTimeMillis();
                    timeStatistics.put("获取线索客户耗时", currentTime - stepTime);
                    stepTime = currentTime;

                    List<ZsMapResBO> clue6 = clues.stream()
                            .filter(a -> "6".equals(a.getType())).collect(Collectors.toList());

                    List<ZsMapResBO> clue7 = clues.stream()
                            .filter(a -> "7".equals(a.getType())).collect(Collectors.toList());


                    List<ZsMapResBO> clue8 = clues.stream()
                            .filter(a -> "8".equals(a.getType())).collect(Collectors.toList());

                    List<ZsMapResBO> clue9 = clues.stream()
                            .filter(a -> "9".equals(a.getType())).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(clue6) && clue6.size() > 0 && clueStatusList.contains("6")) {

                        str.append(ClueStatusEnum.getNameByStatus("6") + "的客户有"
                                + clue6.size() + "组;");
                        zsMapResBOList.addAll(clue6);
                    }
                    if (!CollectionUtils.isEmpty(clue7) && clue7.size() > 0 && clueStatusList.contains("7")) {

                        str.append(ClueStatusEnum.getNameByStatus("7") + "的客户有"
                                + clue7.size() + "组;");
                        zsMapResBOList.addAll(clue7);
                    }
                    if (!CollectionUtils.isEmpty(clue8) && clue8.size() > 0 && clueStatusList.contains("8")) {

                        str.append(ClueStatusEnum.getNameByStatus("8") + "的客户有"
                                + clue8.size() + "组;");
                        zsMapResBOList.addAll(clue8);
                    }
                    //位置异常
                    if (!CollectionUtils.isEmpty(clue9) && clue9.size() > 0 && clueStatusList.contains("9")) {
                        str.append(ClueStatusEnum.getNameByStatus("9") + "的客户有"
                                + clue9.size() + "组;");
                        zsMapResBOList.addAll(clue9);
                    }
                }
            }
            //公客池
            if (isPublic) {
                //改造后（管理者）0-项目全部客户；1-在保客户；21-项目公客池；22-区域公客池客户；23-全国公客池客户 3-地图导入客户
                if (!StringUtils.isEmpty(flagType) && !"0".equals(flagType)) {
                    paramMap.put("poolType", flagType.substring(1));
                }
                //List<ZsMapResBO> publicPools = custMapDao.getPublicPool(paramMap);
                // 分批处理公客池数据
                List<ZsMapResBO> publicPools = new ArrayList<>();
                int pageSize = 500000; // 每批处理的记录数
                int pageIndex = 0;
                boolean hasMoreData = true;
                while (hasMoreData) {
                    paramMap.put("pageSize", pageSize);
                    paramMap.put("pageIndex", pageIndex * pageSize);

                    List<ZsMapResBO> batchList = custMapDao.getPublicPoolPage(paramMap);

                    // 如果经纬度参数不为空，过滤掉不在经纬度范围内的数据
                    if (StringUtils.isNotEmpty(latLon)) {
                        batchList = batchList.stream()
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

                    if (batchList != null && !batchList.isEmpty()) {
                        publicPools.addAll(batchList);
                        pageIndex++;

                        // 如果返回的记录数小于pageSize，说明没有更多数据了
                        if (batchList.size() < pageSize) {
                            hasMoreData = false;
                        }
                    } else {
                        hasMoreData = false;
                    }
                }



                // 记录DAO查询信息
                daoInfo = new HashMap<>();
                daoInfo.put("dao名称", "custMapDao.getPublicPool");
                daoInfo.put("记录数", publicPools != null ? publicPools.size() : 0);
                daoStatistics.put("获取公客池客户", daoInfo);

                currentTime = System.currentTimeMillis();
                timeStatistics.put("获取公客池客户耗时", currentTime - stepTime);
                stepTime = currentTime;
                if (!CollectionUtils.isEmpty(publicPools) && publicPools.size() > 0) {
                    str.append("公客池的客户有" + publicPools.size() + "组;");
                    if (publicPools.size() >= 99999) {
                        result.put("isOver", true);
                    }
                }
                zsMapResBOList.addAll(publicPools);
                zsMapResBOList = zsMapResBOList.stream().distinct().collect(Collectors.toList());
            }
            //新增-招商地图导入
            if (isMap) {
                List<ZsMapResBO> clues = custMapDao.getCluesFromCache(paramMap);
                List<ZsMapResBO> cluesHis = custMapDao.getCluesKhdtHis(paramMap);

                // 如果经纬度参数不为空，过滤掉不在经纬度范围内的数据
                if (StringUtils.isNotEmpty(latLon)) {
                    clues = clues.stream()
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
                // 记录DAO查询信息
                daoInfo = new HashMap<>();
                daoInfo.put("dao名称", "custMapDao.getClues");
                daoInfo.put("记录数", clues != null ? clues.size() : 0);
                daoStatistics.put("获取地图导入客户", daoInfo);

                currentTime = System.currentTimeMillis();
                timeStatistics.put("获取地图导入客户耗时", currentTime - stepTime);
                stepTime = currentTime;

                // 后续处理逻辑保持不变
                List<ZsMapResBO> s1 = cluesHis.stream()
                        .filter(a -> "1".equals(a.getType())).collect(Collectors.toList());
                List<ZsMapResBO> s3 = cluesHis.stream()
                        .filter(a -> "3".equals(a.getType())).collect(Collectors.toList());

                //已成交1-8
                List<ZsMapResBO> s6 = cluesHis.stream()
                        .filter(a -> "6".equals(a.getType())).collect(Collectors.toList());

                List<ZsMapResBO> s7 = cluesHis.stream()
                        .filter(a -> "7".equals(a.getType())).collect(Collectors.toList());

                List<ZsMapResBO> s9 = cluesHis.stream()
                        .filter(a -> "9".equals(a.getType())).collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(s1) && s1.size() > 0 && clueStatusList.contains("1")) {
                    str.append(ClueStatusEnum.getNameByStatus("1") + "的客户有"
                            + s1.size() + "组;");
                    zsMapResBOList.addAll(s1);
                }
                if (!CollectionUtils.isEmpty(s3) && s3.size() > 0 && clueStatusList.contains("3")) {
                    str.append(ClueStatusEnum.getNameByStatus("3") + "的客户有"
                            + s3.size() + "组;");
                    zsMapResBOList.addAll(s3);
                }
                Set<String> zsMapResBOListS6 = zsMapResBOList.stream()
                        .map(ZsMapResBO::getProjectClueId)
                        .filter(Objects::nonNull)  // 可选：过滤空值
                        .collect(Collectors.toSet());
                List<ZsMapResBO> clueMap = clues.stream()
                        .filter(a -> "招商地图导入".equals(a.getSourceMode()))
                        .filter(a -> "6".equals(a.getType()))
                        .filter(a -> !zsMapResBOListS6.contains(a.getProjectClueId()))
                        .collect(Collectors.toList());


                //历史数据中包含未拜访未报备
                if (!CollectionUtils.isEmpty(s6) && s6.size() > 0 && clueStatusList.contains("6") && !CollectionUtils.isEmpty(clueMap) && clueMap.size() > 0) {
                    str.append(ClueStatusEnum.getNameByStatus("6") + "的客户有"
                            + (s6.size()+clueMap.size()) + "组;");
                    zsMapResBOList.addAll(s6);
                    zsMapResBOList.addAll(clueMap);
                }else{
                    if (!CollectionUtils.isEmpty(s6) && s6.size() > 0) {
                        str.append(ClueStatusEnum.getNameByStatus("6") + "的客户有"
                                + s6.size() + "组;");
                        zsMapResBOList.addAll(s6);
                    } else if (!CollectionUtils.isEmpty(clueMap) && clueMap.size() > 0) {
                        str.append(ClueStatusEnum.getNameByStatus("6") + "的客户有"
                                + clueMap.size() + "组;");
                        zsMapResBOList.addAll(clueMap);
                    }
                }

                if (!CollectionUtils.isEmpty(s7) && s7.size() > 0 && clueStatusList.contains("7")) {
                    str.append(ClueStatusEnum.getNameByStatus("7") + "的客户有"
                            + s7.size() + "组;");
                    zsMapResBOList.addAll(s7);
                }

                if (!CollectionUtils.isEmpty(s9) && s9.size() > 0 && clueStatusList.contains("9")) {
                    str.append(ClueStatusEnum.getNameByStatus("9") + "的客户有"
                            + s9.size() + "组;");
                    zsMapResBOList.addAll(s9);
                }

            }
        }
        result.put("zsMapResBOList", zsMapResBOList);
        result.put("message", str.toString());

        currentTime = System.currentTimeMillis();
        timeStatistics.put("总耗时", currentTime - startTime);

        // 统一输出所有步骤的执行时间
        // 统一输出所有步骤的执行时间和DAO查询信息
        System.out.println("===== zsMap接口执行时间统计 =====");
        for (Map.Entry<String, Object> entry : timeStatistics.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "ms");
        }
        System.out.println("\n===== zsMap接口DAO查询统计 =====");
        for (Map.Entry<String, Object> entry : daoStatistics.entrySet()) {
            Map<String, Object> info = (Map<String, Object>) entry.getValue();
            System.out.println(entry.getKey() + ": " +
                    "DAO=" + info.get("dao名称") + ", " +
                    "记录数=" + info.get("记录数"));
        }
        System.out.println("================================");

        return ResultBody.success(result);
    }

    private List<String> initClueStatus(String clueStatus, String taskName, String flagType) {
        List<String> clueStatusList = Stream.of("1", "2", "3", "4", "5", "6", "7","8", "9").collect(Collectors.toList());
        //线索客户和公共池都不查询
        if (StringUtils.isNotBlank(taskName)) {
            //clueStatusList = Stream.of("1", "2", "3", "4", "8").collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(flagType)) {
            if ("1".equals(flagType)) {//1在保客户
                clueStatusList = Stream.of("1", "2", "3", "4", "5").collect(Collectors.toList());
            }
            if ("3".equals(flagType)) {//3-线索客户 4-地图导入客户
                clueStatusList = Stream.of("6", "7", "8","9").collect(Collectors.toList());
            }
        }

        if (StringUtils.isNotBlank(clueStatus)) {
            clueStatusList = Stream.of(clueStatus).collect(Collectors.toList());
        }
        //先全部返回不处理逻辑
        //clueStatusList = Stream.of("1", "2", "3", "4", "5", "6", "7","8", "9").collect(Collectors.toList());
        return clueStatusList;

    }

}
