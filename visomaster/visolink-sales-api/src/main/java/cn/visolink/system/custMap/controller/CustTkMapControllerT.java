package cn.visolink.system.custMap.controller;

import cn.visolink.common.ClueAcquisitionStatusEnum;
import cn.visolink.common.ClueStatusEnum;
import cn.visolink.common.permission.RequiresPermission;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.channel.dao.DataStatisticDao;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.dao.TaskDao;
import cn.visolink.system.channel.model.form.UserOrgRelForm;
import cn.visolink.system.companyQw.util.DateUtils;
import cn.visolink.system.custMap.bo.ZsMapPermissionsBO;
import cn.visolink.system.custMap.bo.ZsMapResBO;
import cn.visolink.system.custMap.dao.CustMapDao;
import cn.visolink.system.custMap.dao.ZsMapPermissionsDao;
import cn.visolink.system.custMap.service.ZsMapService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.homepage.service.WorkbenchService;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.system.userQuery.service.UserQueryConditionsService;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName CustMapController
 * @Author wanggang
 * @Description //客户地图
 * @Date 2022/9/1 17:49
 **/
@Slf4j
@RestController
@RequestMapping("/custMap")
@Api(tags = "拓客地图功能T")
public class CustTkMapControllerT {

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
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private ProjectCluesDao projectCluesDao;
    
    @Autowired
    private UserQueryConditionsService userQueryConditionsService;


    /**
     * 权限类型权重，1最大，5最小
     */
    private int getPermissionWeight(String type) {
        if ("1".equals(type)) return 5;
        if ("2".equals(type)) return 4;
        if ("3".equals(type)) return 3;
        if ("4".equals(type)) return 2;
        if ("5".equals(type)) return 1;
        return 0;
    }

    private void fillPermissionLevel(List<ZsMapResBO> resList, List<ZsMapPermissionsBO> mapPermissions, Map paramMap, List<UserOrgRelForm>  dataViewPremissionApproveStatusList) {
        if (resList == null || mapPermissions == null) return;
        if ("1".equals(paramMap.get("permissionType").toString())) {
            // 项目权限
            Map<String, String> projectIdToPermissionsType = new HashMap<>();
            for (ZsMapPermissionsBO bo : mapPermissions) {
                String permissionsType = bo.getPermissionsType();
                String projPermissions = bo.getProjPermissions();
                if (cn.visolink.utils.StringUtils.isNotBlank(projPermissions)) {
                    try {
                        List<Object> projPermissArray = JSON.parseArray(projPermissions, Object.class);
                        if (projPermissArray != null) {
                            for (Object item : projPermissArray) {
                                if (item instanceof List) {
                                    List<?> itemList = (List<?>) item;
                                    if (itemList.size() >= 2 && itemList.get(1) instanceof String) {
                                        String projectId = (String) itemList.get(1);
                                        // 权重逻辑
                                        if (projectIdToPermissionsType.containsKey(projectId)) {
                                            String oldType = projectIdToPermissionsType.get(projectId);
                                            if (getPermissionWeight(permissionsType) > getPermissionWeight(oldType)) {
                                                projectIdToPermissionsType.put(projectId, permissionsType);
                                            }
                                        } else {
                                            projectIdToPermissionsType.put(projectId, permissionsType);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 解析失败，跳过
                    }
                }
            }
            for (ZsMapResBO bo : resList) {
                String projectId = bo.getProjectId();
                String permissionsType = projectIdToPermissionsType.get(projectId);
                if (StringUtils.isNotBlank(permissionsType)) {
                    bo.setPermissionLevel(permissionsType);
                }else{
                    //IsSelf为0：招商地图权限客户 IsSelf为1：本人客户权限 IsSelf为2：管理者全号数据权限 IsSelf为3：管理者隐号数据权限

                    if("0".equals(bo.getIsSelf()) && "3".equals(paramMap.get("isSelf").toString())){
                        bo.setIsSelf("3");

                        // ===== 权限属性处理逻辑 =====
                        // 从paramMap中获取myProjectIds
                        Object myProjectIdsObj = paramMap.get("myProjectIds");
                        List<String> myProjectIds = null;
                        if (myProjectIdsObj instanceof List) {
                            myProjectIds = (List<String>) myProjectIdsObj;
                        }

                        if (dataViewPremissionApproveStatusList != null && !dataViewPremissionApproveStatusList.isEmpty()) {
                            // 将权限配置按projectId分组
                            Map<String, List<UserOrgRelForm>> projectIdToAuthList = new HashMap<>();
                            for (UserOrgRelForm form : dataViewPremissionApproveStatusList) {
                                if (form != null && form.getProjectId() != null) {
                                    projectIdToAuthList
                                            .computeIfAbsent(form.getProjectId(), k -> new ArrayList<>())
                                            .add(form);
                                }
                            }

                            // 检查当前projectId是否在myProjectIds中
                            String currentProjectId = bo.getProjectId();
                            if (myProjectIds != null && !myProjectIds.isEmpty() && myProjectIds.contains(currentProjectId)) {
                                List<UserOrgRelForm> authList = projectIdToAuthList.get(currentProjectId);
                                if (authList != null && !authList.isEmpty()) {
                                    for (UserOrgRelForm auth : authList) {
                                        // 处理团队权限
//                                        if (auth.getOrgId() != null) {
//                                            List<String> orgIds = Arrays.asList(auth.getOrgId().split(","));
//                                            if (orgIds.contains(bo.getSalesAttributionTeamId())) {
//                                                bo.setIsSelf("3");
//                                            }
//                                        }
                                        // 处理姓名脱敏
                                        if (1 == auth.getIsNameShow()) {
                                            bo.setCustomerName(bo.getOldCustomerName());
                                        }
                                        // 处理手机号脱敏
                                        if (1 == auth.getIsMobileShow()) {
                                            bo.setCustomerMobile(bo.getOldCustomerMobile());
                                        }
                                        if(1 == auth.getIsNameShow() && 1==auth.getIsMobileShow()){
                                            bo.setIsSelf("2");
                                        }

                                        // 如果只需要处理一条授权配置，可以 break;
                                    }
                                }
                            }
                        }
                        // ===== 权限属性处理逻辑 END =====
                    }
                }
                //处理客户点位
                if("5".equals(permissionsType)){
                    if("1".equals(paramMap.get("mapType").toString())){
                        bo.setType("6");//未拜访-未报备
                    }else if("2".equals(paramMap.get("mapType").toString())){
                        bo.setType("8");//目标企业
                    }
                }
            }
        } else  if ("2".equals(paramMap.get("permissionType").toString())) {
            // 区域权限
            Map<String, String> areaToPermissionsType = new HashMap<>();
            for (ZsMapPermissionsBO bo : mapPermissions) {
                String permissionsType = bo.getPermissionsType();
                String areaPermissions = bo.getAreaPermissions();
                if (cn.visolink.utils.StringUtils.isNotBlank(areaPermissions)) {
                    try {
                        List<Object> areaList = JSON.parseArray(areaPermissions, Object.class);
                        for (Object areaObj : areaList) {
                            if (areaObj instanceof List) {
                                List<?> area = (List<?>) areaObj;
                                String areaKey = org.apache.commons.lang3.StringUtils.join(area, "");
                                areaKey = areaKey.replaceAll(",", "");
                                // 权重逻辑
                                if (areaToPermissionsType.containsKey(areaKey)) {
                                    String oldType = areaToPermissionsType.get(areaKey);
                                    if (getPermissionWeight(permissionsType) > getPermissionWeight(oldType)) {
                                        areaToPermissionsType.put(areaKey, permissionsType);
                                    }
                                } else {
                                    areaToPermissionsType.put(areaKey, permissionsType);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 解析失败，跳过
                    }
                }
            }
            for (ZsMapResBO bo : resList) {
                String areaKey = bo.getCompanyAddress();
                String permissionsType = areaToPermissionsType.get(areaKey);
                bo.setPermissionLevel(permissionsType);
                //处理客户点位
                if("5".equals(permissionsType)){
                    if("1".equals(paramMap.get("mapType").toString())){
                        bo.setType("6");//未拜访-未报备
                    }else if("2".equals(paramMap.get("mapType").toString())){
                        bo.setType("8");//目标企业
                    }
                }
            }
        }
    }

    @RequiresPermission(value = "", description = "拓客地图查询",required = true)
    @Log("拓客地图查询")
    @PostMapping("/tkMapT")
    @ApiOperation(value = "拓客地图查询", notes = "拓客地图查询")
    public ResultBody tkMapT(@RequestBody Map<String, Object> paramMap) {
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
        //保存查询参数
        userQueryConditionsService.saveUserQueryConditions(userId, "/custMap/tkMapT", paramMap);
        // 2. 设置默认值和通用参数
        paramMap.put("UserName", userName);
        paramMap.put("UserId", userId);

        String permissionType = (String) paramMap.get("permissionType");
        if(StringUtils.isBlank(permissionType)){
            paramMap.put("permissionType", "1");
            permissionType = "1";
        }
        // 3. 处理区域权限
        Map<String, Object> result = new HashMap<>();

        handleAreaPermissions(paramMap);
        // 4. 处理团队和项目信息
        handleTeamAndProjectInfo(paramMap);

        // 5. 执行查询并构建消息
        performAllQueries(paramMap, result);

        // ===== 权限类型赋值逻辑 =====
        List<ZsMapResBO> zsMapResBOList = (List<ZsMapResBO>) result.get("zsMapResBOList");
        List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
            Stream.of((String)paramMap.get("UserId")).collect(Collectors.toList()));

        UserOrgRelForm  userOrgRelForm = new UserOrgRelForm();
        userOrgRelForm.setUserId(userId);
        List<UserOrgRelForm> dataViewPremissionApproveStatusList = projectCluesDao.getDataViewPremissionApprove(
                userOrgRelForm);
        fillPermissionLevel(zsMapResBOList, mapPermissions, paramMap,dataViewPremissionApproveStatusList);
        // ===== 权限类型赋值逻辑 END =====

        // 查热力图功能
        String isHeart = String.valueOf(paramMap.get("isHeart"));
        if ("1".equals(isHeart)) {
            // 分组统计
            zsMapResBOList = (List<ZsMapResBO>) result.get("zsMapResBOList");
            Map<String, List<ZsMapResBO>> groupMap = zsMapResBOList.stream()
                .filter(item -> item.getLongitude() != null && item.getLatitude() != null)
                .collect(Collectors.groupingBy(item -> item.getLongitude() + "_" + item.getLatitude()));

            List<Map<String, Object>> reportNum = new ArrayList<>();
            for (Map.Entry<String, List<ZsMapResBO>> entry : groupMap.entrySet()) {
                String[] arr = entry.getKey().split("_");
                Map<String, Object> map = new HashMap<>();
                map.put("count", entry.getValue().size());
                map.put("Longitude", arr[0]);
                map.put("Latitude", arr[1]);
                reportNum.add(map);
            }
            result.put("reportNum", reportNum);
        }

        // 去重功能
        String isDistict = paramMap.get("isDistict") == null ? null : paramMap.get("isDistict").toString();
        if ("1".equals(isDistict)) {
            zsMapResBOList = (List<ZsMapResBO>) result.get("zsMapResBOList");
            String customerName = paramMap.get("customerNameSwitch") + "";
            String customerMobile = paramMap.get("customerMobileSwitch") + "";
            String lineDistance = paramMap.get("lineDistanceSwitch") + "";
            return ResultBody.success(zsMapService.zsMapDistict(customerName, customerMobile, lineDistance,zsMapResBOList));
        }

        //处理任务客户下发客户数和已拓客户数
        String taskId = String.valueOf(paramMap.get("taskId"));
        if(StringUtils.isNotBlank(taskId) && !"null".equals(taskId)){
            //处理下发客户数及已拓客户数
            handleTaskYtkh(paramMap, result);
            //处理任务指标标记量
            //handleTaskBjl(paramMap, result);
        }

        return ResultBody.success(result);
    }

    private void handleTaskYtkh(Map<String, Object> paramMap, Map<String, Object> result) {
        // 查询任务客户信息
        List<Map<String, Object>> tc = taskDao.selectTaskCustomerByTaskId(paramMap.get("taskId").toString());
        // 获取地图客户信息
        List<ZsMapResBO> zsMapResBOList = (List<ZsMapResBO>) result.get("zsMapResBOList");
        String orderType = paramMap.get("orderType") == null ? null : paramMap.get("orderType").toString();
        if ("1".equals(orderType)) {
            // 由近及远（最新在前）
            zsMapResBOList.sort(
                Comparator.comparing(ZsMapResBO::getEntryTime, Comparator.nullsLast(Date::compareTo)).reversed()
            );
        } else if ("3".equals(orderType)) {
            // 由远及近（最早在前）
            zsMapResBOList.sort(
                Comparator.comparing(ZsMapResBO::getEntryTime, Comparator.nullsLast(Date::compareTo))
            );
        } else {
            zsMapResBOList.sort(
                Comparator.comparing(ZsMapResBO::getEntryTime, Comparator.nullsLast(Date::compareTo)).reversed()
            );
        }
        result.put("zsMapResBOList", zsMapResBOList);

        // 1. 提取tc中customerType=1的客户下发客户
        Set<String> tcType1KeySet = tc.stream()
            .filter(e -> !"3".equals(String.valueOf(e.get("customerType"))))
            .map(e -> e.get("customerMobile") + "_" + e.get("customerName"))
            .collect(Collectors.toSet());

        // 2. 筛选ZsMapResBO中type=8且在tcType1KeySet中的客户
        List<ZsMapResBO> wtkhList = zsMapResBOList.stream()
            .filter(e -> "8".equals(String.valueOf(e.getType()))
                && tcType1KeySet.contains(e.getOldCustomerMobile() + "_" + e.getOldCustomerName()))
            .collect(Collectors.toList());

        int xfkh=zsMapResBOList.size();
        int wtkh= wtkhList.size();
        int ytkh=xfkh-wtkh;
        // 3. 统计数量或做后续处理
        result.put("ytkh", ytkh);
        result.put("xfkh", xfkh);
    }

    //方法作废-改为单挑数据去处理
    private void handleTaskBjl(Map<String, Object> paramMap, Map<String, Object> result) {
        // 查询任务客户信息
        List<Map<String, Object>> tc = taskDao.selectTaskCustomerByTaskId(paramMap.get("taskId").toString());
        // 获取地图客户信息
        List<ZsMapResBO> zsMapResBOList = (List<ZsMapResBO>) result.get("zsMapResBOList");


        //统计完成标记量的客户-阶段是 3、4、5、9
        List<ZsMapResBO> bjlList = zsMapResBOList.stream()
                .filter(e -> "3".equals(String.valueOf(e.getType())) || "4".equals(String.valueOf(e.getType()))
                        || "5".equals(String.valueOf(e.getType())) || "9".equals(String.valueOf(e.getType())))
                .collect(Collectors.toList());

        int bjl=bjlList.size();

        taskDao.updateTaskBjl(paramMap.get("taskId").toString(), bjl);
    }

    @Log("拓客地图导出")
    @PostMapping("/tkMapExportT")
    @ApiOperation(value = "拓客地图导出", notes = "拓客地图导出")
    public void tkMapExportT(HttpServletResponse response, @RequestBody String excelForm) throws IOException {
        // 1. 初始化基础参数
        Map<String, Object> paramMap = JSONObject.parseObject(excelForm, Map.class);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userId = request.getHeader("userid");
        String userName = request.getHeader("username");
        if(StringUtils.isEmpty(userId)){
            userId = SecurityUtils.getUserId();
        }
        if(StringUtils.isEmpty(userName)){
            userName = SecurityUtils.getUsername();
        }
        paramMap.put("UserName", userName);
        paramMap.put("UserId", userId);
        String permissionType = (String) paramMap.get("permissionType");
        if(StringUtils.isBlank(permissionType)){
            paramMap.put("permissionType", "1");
            permissionType = "1";
        }
        // 2. 处理区域和项目权限

        handleAreaPermissions(paramMap);

        handleTeamAndProjectInfo(paramMap);

        // ===== 新增：查出projectId到permissionsType的映射 =====
        List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
            Stream.of((String)paramMap.get("UserId")).collect(Collectors.toList()));
        List<ZsMapResBO> finalResults = executeQueriesForExport(paramMap);

        UserOrgRelForm  userOrgRelForm = new UserOrgRelForm();
        userOrgRelForm.setUserId(userId);
        List<UserOrgRelForm> dataViewPremissionApproveStatusList = projectCluesDao.getDataViewPremissionApprove(
                userOrgRelForm);
        fillPermissionLevel(finalResults, mapPermissions, paramMap,dataViewPremissionApproveStatusList);

        // ===== 新增 END =====

        // 4. 生成Excel文件
        String mapType = (String) paramMap.get("mapType");
        String excelName = "1".equals(mapType) ? "招商地图导出" : "拓客地图导出";
        
        if (finalResults != null && !finalResults.isEmpty()) {
            // 1. 准备表头, 调整列顺序
            List<String> headerList = new ArrayList<>(Arrays.asList(this.courtCaseTitle2));
            String[] headers = headerList.toArray(new String[0]);
            ArrayList<Object[]> dataset = new ArrayList<>();
            // isAll 用于控制手机号是否脱敏
            String isAllStr = paramMap.get("isAll") != null ? paramMap.get("isAll").toString() : "";
            boolean showAllDigits = !"1".equals(isAllStr);
            int rowNum = 1;
            
            for (ZsMapResBO model : finalResults) {
                Object[] oArray = new Object[headers.length];
                
                // --- 按新表头顺序填充数据 ---
                oArray[0] = rowNum++;
                oArray[1] = model.getIntentionProject();
                //oArray[2] = showAllDigits?model.getOldCustomerName():model.getCustomerName();
                oArray[2] = model.getCustomerName();
                oArray[3] = model.getCompanyAddress();
                oArray[4] = model.getDetailAddress();
                oArray[5] = model.getLongitude();
                oArray[6] = model.getLatitude();
                oArray[7] = model.getSourceMode();
                oArray[8] = model.getIndustryCategory();
                oArray[9] = model.getSecondaryCategory();
                oArray[10] = model.getMainProduct();
                oArray[11] = model.getMainRawMaterials();
                oArray[12] = model.getPeopleNum();
                oArray[13] = model.getExistingPlantArea();
                oArray[14] = model.getAnnualOutputValue();
                oArray[15] = model.getTaxAmountYear();
                oArray[16] = model.getPlantTypeDesc();
                oArray[17] = model.getContacts();
                //oArray[18] = showAllDigits?model.getOldCustomerMobile():model.getCustomerMobile();
                oArray[18] = model.getCustomerMobile();
                oArray[19] = formatDate(model.getEntryTime());
                oArray[20] = model.getEntryPerson();

                oArray[21] = model.getSalesAttributionTeamName();
                oArray[22] = model.getEntryPersonIdentity();
                oArray[23] = model.getSalesAttributionName();
                oArray[24] = formatDate(model.getSalesAttributionTime());
                oArray[25] = model.getSalesTheLatestFollowDate();
                oArray[26] = model.getSalesFollowExpireDate();
                oArray[27] = model.getDisTime();
                oArray[28] = formatDate(model.getTheFirstVisitDate());
                oArray[29] = formatDate(model.getVisitDate());
                oArray[30] = model.getCustomerLevel();
                oArray[31] = model.getIsTaoGuest();
                oArray[32] = model.getClueStatusCh();
                oArray[33] = model.getIsThreeOnes() != null && model.getIsThreeOnes() == 1 ? "是" : "否";
                oArray[34] = model.getIntentionTypeDesc();
                oArray[35] = model.getIntentionalAreaDesc();
                oArray[36] = model.getAcceptPriceDesc();
                oArray[37] = model.getIntentionalFloorDesc();
                oArray[38] = model.getIsPark() != null && model.getIsPark().equals("1") ? "是" : "否";
                oArray[39] = model.getParkAddress();
                oArray[40] = model.getParkFloors();
                oArray[41] = model.getParkName();
                
                // 新增的最后一列: 客户阶段
                String statusName = "1".equals(mapType)
                        ? ClueStatusEnum.getNameByStatus(model.getType())
                        : ClueAcquisitionStatusEnum.getNameByStatus(model.getType());
                oArray[42] = statusName;
                oArray[43] = model.getLabel();

                dataset.add(oArray);
            }

            // 5. 记录导出日志
            logExportAction(excelForm, excelName, paramMap);

            // 6. 导出Excel
            if (!dataset.isEmpty()) {
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response, null);
            }
        }
    }

    private List<ZsMapResBO> executeQueriesForExport(Map<String, Object> paramMap) {
        String mapType = (String) paramMap.getOrDefault("mapType", "2");
        String dataRange = (String) paramMap.getOrDefault("dataRange", "1");
        String flagType = (String) paramMap.get("flagType");
        String clueStatus = (String) paramMap.get("clueStatus");
        paramMap.put("mapType", mapType);
        paramMap.put("dataRange", dataRange);

        // 处理多选flagTypes参数
        List<String> flagTypesList = new ArrayList<>();
        Object flagTypesObj = paramMap.get("flagTypes");
        if (flagTypesObj instanceof List) {
            List<?> rawList = (List<?>) flagTypesObj;
            flagTypesList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
            paramMap.put("flagTypes", flagTypesList);
        } else {
            // 如果没有多选参数，使用原来的单个参数
            if (StringUtils.isNotBlank(flagType)) {
                flagTypesList.add(flagType);
                paramMap.put("flagTypes", flagTypesList);
            }
        }

        // 处理多选clueStatuss参数
        List<String> clueStatussList = new ArrayList<>();
        Object clueStatussObj = paramMap.get("clueStatuss");
        if (clueStatussObj instanceof List) {
            List<?> rawList = (List<?>) clueStatussObj;
            clueStatussList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
            paramMap.put("clueStatuss", clueStatussList);
        } else {
            // 如果没有多选参数，使用原来的单个参数
            if (StringUtils.isNotBlank(clueStatus)) {
                clueStatussList.add(clueStatus);
                paramMap.put("clueStatuss", clueStatussList);
            }
        }

        List<String> typeList =  new ArrayList<>();
        Object typesObj = paramMap.get("types");
        if (typesObj instanceof List) {
            List<?> rawList = (List<?>) typesObj;
            typeList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
            paramMap.put("types", typeList);
        } else {
            paramMap.remove("types");
        }
        //归属人数组
        List<String> salesAttributionNameList =  new ArrayList<>();
        Object salesAttributionNameSObj = paramMap.get("salesAttributionNameS");
        if (salesAttributionNameSObj instanceof List) {
            List<?> rawList = (List<?>) salesAttributionNameSObj;
            salesAttributionNameList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
            paramMap.put("salesAttributionNameS", salesAttributionNameList);
        } else {
            paramMap.remove("salesAttributionNameS");
        }
        List<String> clueStatusList = initClueStatus(typeList, flagTypesList, mapType);

        List<ZsMapResBO> zsMapResBOList = new ArrayList<>();

        // 1. 在保客户（原商机客户）
        if (shouldQueryOpportunity(flagTypesList) && hasOpportunityStatus(clueStatusList, mapType)) {
            zsMapResBOList.addAll(queryOpportunityCustomers(paramMap, clueStatusList));
        }
        // 2. 线索客户
        if (shouldQueryClue(flagTypesList) && hasClueStatus(clueStatusList, mapType)) {
            paramMap.put("isMapImport", "0");
            zsMapResBOList.addAll(queryClueCustomers(paramMap, clueStatusList));
        }
        // 3. 公客池客户
        if (shouldQueryPublicPool(flagTypesList)) {
            // 处理公客池类型，优先使用多选参数中的第一个包含"2"的值
            String poolType = "1"; // 默认值
            if (flagTypesList != null && !flagTypesList.isEmpty()) {
                for (String ft : flagTypesList) {
                    if (ft.contains("2")) {
                        poolType = ft.substring(1, 2);
                        break;
                    }
                }
            }
            paramMap.put("poolType", poolType);
            zsMapResBOList.addAll(queryPublicPoolCustomers(paramMap));
        }
        // 4. 地图导入客户
        if (shouldQueryMapImport(flagTypesList)) {
            paramMap.put("isMapImport", "1");
            zsMapResBOList.addAll(queryMapImportCustomers(paramMap, clueStatusList));
        }
        // 5. 一键拓客客户
        if (shouldQueryYjtk(flagTypesList)) {
            paramMap.put("isYjtk", "1");
            zsMapResBOList.addAll(queryYjtkCustomers(paramMap, clueStatusList));
        }

        // 统一去重并过滤type为null
        return zsMapResBOList.stream()
                .filter(item -> item.getType() != null)
                .distinct()
                .collect(Collectors.toList());
    }

    private void logExportAction(String excelForm, String excelName, Map<String, Object> paramMap) {
        ExcelExportLog excelExportLog = new ExcelExportLog();
        excelExportLog.setId(UUID.randomUUID().toString());
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3"); // 导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setCreator(paramMap.get("UserId").toString());
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);

        Object projectListObj = paramMap.get("projectList");
        if (projectListObj instanceof List && !((List<?>) projectListObj).isEmpty()) {
            List<String> projectIds = (List<String>) projectListObj;
            List<ProjectVO> projectVOs = projectIds.stream().map(id -> {
                ProjectVO vo = new ProjectVO();
                vo.setProjectId(id);
                return vo;
            }).collect(Collectors.toList());
            Map<String, String> proMap = custMapDao.getAreaNameAndProNames(projectVOs);
            if (proMap != null) {
                excelExportLog.setAreaName(proMap.get("areaName"));
                excelExportLog.setProjectId(proMap.get("projectId"));
                excelExportLog.setProjectName(proMap.get("projectName"));
            }
        }
        excelImportMapper.addExcelExportLog(excelExportLog);
    }

    /**
     * 统一的查询主方法，根据mapType动态执行逻辑
     */
    private void performAllQueries(Map<String, Object> paramMap, Map<String, Object> result) {
        String mapType = (String) paramMap.get("mapType");
        String flagType = (String) paramMap.get("flagType");
        String clueStatus = (String) paramMap.get("clueStatus");//客户状态
        String statusType = (String) paramMap.get("type");//客户阶段

        // 处理多选flagTypes参数
        List<String> flagTypesList = new ArrayList<>();
        Object flagTypesObj = paramMap.get("flagTypes");
        if (flagTypesObj instanceof List) {
            List<?> rawList = (List<?>) flagTypesObj;
            flagTypesList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
            paramMap.put("flagTypes", flagTypesList);
        } else {
            // 如果没有多选参数，使用原来的单个参数
            if (StringUtils.isNotBlank(flagType)) {
                flagTypesList.add(flagType);
                paramMap.put("flagTypes", flagTypesList);
            }
        }

        // 处理多选clueStatuss参数
        List<String> clueStatussList = new ArrayList<>();
        Object clueStatussObj = paramMap.get("clueStatuss");
        if (clueStatussObj instanceof List) {
            List<?> rawList = (List<?>) clueStatussObj;
            clueStatussList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
            paramMap.put("clueStatuss", clueStatussList);
        } else {
            // 如果没有多选参数，使用原来的单个参数
            if (StringUtils.isNotBlank(clueStatus)) {
                clueStatussList.add(clueStatus);
                paramMap.put("clueStatuss", clueStatussList);
            }
        }

        List<String> typeList =  new ArrayList<>();
        Object typesObj = paramMap.get("types");
        if (typesObj instanceof List) {
            List<?> rawList = (List<?>) typesObj;
            typeList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
            paramMap.put("types", typeList);
        } else {
            paramMap.remove("types");
        }

        //归属人数组
        List<String> salesAttributionNameList =  new ArrayList<>();
        Object salesAttributionNameSObj = paramMap.get("salesAttributionNameS");
        if (salesAttributionNameSObj instanceof List) {
            List<?> rawList = (List<?>) salesAttributionNameSObj;
            salesAttributionNameList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
            paramMap.put("salesAttributionNameS", salesAttributionNameList);
        } else {
            paramMap.remove("salesAttributionNameS");
        }

        //标签
        List<String> tagLabelList =  new ArrayList<>();
        Object tagLabelObj = paramMap.get("tagLabel");
        if (tagLabelObj instanceof List) {
            List<?> rawList = (List<?>) tagLabelObj;
            tagLabelList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
            paramMap.put("tagLabelS", tagLabelList);
        } else {
            paramMap.remove("tagLabel");
        }

        List<String> clueStatusList = initClueStatus(typeList, flagTypesList, mapType);

        // 结果集和提示信息分开收集
        List<ZsMapResBO> zsMapResBOList = new ArrayList<>();
        StringBuilder messageBuilder = new StringBuilder();

        // 各子查询结果和提示信息
        List<ZsMapResBO> opportunityResults = new ArrayList<>();
        List<ZsMapResBO> clueResults = new ArrayList<>();
        List<ZsMapResBO> publicPoolResults = new ArrayList<>();
        List<ZsMapResBO> mapImportResults = new ArrayList<>();
        List<ZsMapResBO> yjtkResults = new ArrayList<>();

        // 1. 在保客户（原商机客户）
        if (shouldQueryOpportunity(flagTypesList) && hasOpportunityStatus(clueStatusList, mapType)) {
            opportunityResults = queryOpportunityCustomers(paramMap, clueStatusList);
            zsMapResBOList.addAll(opportunityResults);
            messageBuilder.append("【在保客户】");
            appendStatusMessage(messageBuilder, opportunityResults, clueStatusList, mapType);
        }
        // 2. 线索客户
        if (shouldQueryClue(flagTypesList) && hasClueStatus(clueStatusList, mapType)) {
            paramMap.put("isMapImport", "0");
            clueResults = queryClueCustomers(paramMap, clueStatusList);
            zsMapResBOList.addAll(clueResults);
            messageBuilder.append("【线索客户】");
            appendStatusMessage(messageBuilder, clueResults, clueStatusList, mapType);
        }
        // 3. 公客池客户
        if (shouldQueryPublicPool(flagTypesList)) {
            // 处理公客池类型，优先使用多选参数中的第一个包含"2"的值
            String poolType = "1"; // 默认值
            if (flagTypesList != null && !flagTypesList.isEmpty()) {
                for (String ft : flagTypesList) {
                    if (ft.contains("2")) {
                        poolType = ft.substring(1, 2);
                        break;
                    }
                }
            }
            paramMap.put("poolType", poolType);
            publicPoolResults = queryPublicPoolCustomers(paramMap);
            zsMapResBOList.addAll(publicPoolResults);
            messageBuilder.append("【公客池客户】");
            appendPublicPoolMessage(messageBuilder, publicPoolResults);
        }
        // 4. 地图导入客户
        if (shouldQueryMapImport(flagTypesList)) {
            paramMap.put("isMapImport", "1");
            mapImportResults = queryMapImportCustomers(paramMap, clueStatusList);
            zsMapResBOList.addAll(mapImportResults);
            messageBuilder.append("【地图导入客户】");
            appendMapImportMessage(messageBuilder, mapImportResults, clueStatusList, mapType);
        }
        // 5. 一键拓客客户
        if (shouldQueryYjtk(flagTypesList)) {
            paramMap.put("isYjtk", "1");
            yjtkResults=queryYjtkCustomers(paramMap, clueStatusList);
            zsMapResBOList.addAll(yjtkResults);
            messageBuilder.append("【一键拓客客户】");
            appendYjtkMessage(messageBuilder, yjtkResults, clueStatusList, mapType);
        }

        // 统一去重并过滤type为null
        List<ZsMapResBO> finalList = zsMapResBOList.stream()
                .filter(item -> item.getType() != null)
                .distinct()
                .collect(Collectors.toList());
        result.put("zsMapResBOList", finalList);

        // 1. 各子查询带标签的统计信息
        result.put("messageDetail", messageBuilder.toString());

        // 2. 合并最终结果集的统计信息（无条件输出）
        Map<String, Long> typeCount = finalList.stream()
                .collect(Collectors.groupingBy(ZsMapResBO::getType, Collectors.counting()));
        StringBuilder totalMsg = new StringBuilder();
        for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
            String typeName = ("1".equals(mapType))
                    ? ClueStatusEnum.getNameByStatus(entry.getKey())
                    : ClueAcquisitionStatusEnum.getNameByStatus(entry.getKey());
            totalMsg.append(typeName).append("客户").append(entry.getValue()).append("组；");
        }
        result.put("message", totalMsg.toString());
        result.put("dataSize", finalList.size());
    }

    // 处理区域权限
    private void handleAreaPermissions(Map paramMap) {
        List<List<String>> areaDataList = (List<List<String>>) paramMap.get("areaData");

                List<String> areaList = new ArrayList<>();
                areaDataList.forEach(areaData -> {
                    String string = StringUtils.join(areaData, "");
                    string = string.replaceAll(",", "");
                    areaList.add(string);
                });
                paramMap.put("areaList", areaList);
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
        Object projectIdsObj = paramMap.get("projectIds");

        List<String> myProjectIds = new ArrayList<>();
        if (projectIdsObj instanceof List && !((List<?>) projectIdsObj).isEmpty()) {
            List<String> projectIds = (List<String>) paramMap.get("projectIds");
            projectList = new ArrayList<>();
            for (String projectId : projectIds) {
                ProjectVO pro = new ProjectVO();
                pro.setProjectId(projectId);
                projectList.add(pro);
            }
            //从所选项目中过滤出地图授权的项目
            List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
                    Stream.of((String)paramMap.get("UserId")).collect(Collectors.toList()));

            if (!CollectionUtils.isEmpty(mapPermissions)) {
                List<String> projPermissList = mapPermissions.stream().filter(zsMapPermissionsBO-> cn.visolink.utils.StringUtils.isNotBlank(zsMapPermissionsBO.getProjPermissions())
                        && zsMapPermissionsBO.getProjEndDate().compareTo(
                        DateUtils.getDateAfterDays(new Date(), -1)) > 0).map(ZsMapPermissionsBO::getProjPermissions).collect(
                        Collectors.toList());
                
                // 从projPermissList中提取项目ID部分，然后与projectIds做交集
                Set<String> authorizedProjectIds = new HashSet<>();
                for (String projPermiss : projPermissList) {
                    try {
                        List<Object> projPermissArray = JSON.parseArray(projPermiss, Object.class);
                        if (projPermissArray != null) {
                            for (Object item : projPermissArray) {
                                if (item instanceof List) {
                                    List<?> itemList = (List<?>) item;
                                    if (itemList.size() >= 2 && itemList.get(1) instanceof String) {
                                        authorizedProjectIds.add((String) itemList.get(1)); // 取第二个元素作为项目ID
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 解析失败，跳过
                    }
                }

                //地图授权中包含的projectId
                List<String> filteredProjectIds = projectIds.stream()
                        .filter(authorizedProjectIds::contains)
                        .collect(Collectors.toList());


                //地图授权中不包含的projectId
                myProjectIds = projectIds.stream()
                        .filter(id -> !authorizedProjectIds.contains(id))
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(filteredProjectIds)) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < filteredProjectIds.size(); i++) {
                        if (i == 0) {
                            stringBuffer.append("'" + filteredProjectIds.get(i) + "'");
                        } else {
                            stringBuffer.append("," + "'" + filteredProjectIds.get(i) + "'");
                        }
                    }
                    paramMap.put("projPermissList", stringBuffer.toString());
                }else{
                    paramMap.put("projPermissList", "");
                }
            }else{
                paramMap.put("projPermissList", "");
                myProjectIds=projectIds;
            }

        } else {
            projectList = new ArrayList<>();
            ProjectVO pro = new ProjectVO();
            if("1".equals(paramMap.get("permissionType"))){
                pro.setProjectId("not exist projectId");
                projectList.add(pro);
            }
            //防止后台报错
            paramMap.put("projPermissList", "");

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

        //获取上级创建任务人员
        List<String> userIdsCreate = new ArrayList<>();
        userIds.add((String)paramMap.get("UserId"));
        List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
        paramMap.put("isSelf","");//给个默认值
        if (!CollectionUtils.isEmpty(userList)) {
            for (Map user : userList) {
                String jobCode = String.valueOf(user.get("JobCode"));
                if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)
                        || "yxjl".equals(jobCode) || "qyyxjl".equals(jobCode)
                        || "zszj".equals(jobCode) || "qyzszj".equals(jobCode)|| "jtsjg".equals(jobCode)
                        || "xmz".equals(jobCode) || "qyfz".equals(jobCode) || "qyz".equals(jobCode) || "10001".equals(jobCode)) {
                    String orgPath = String.valueOf(user.get("FullPath"));
                    List<String> teamUserIds = custMapDao.getTeamUserNew(orgPath);
                    userIds.addAll(teamUserIds);

                    //根据项目查询项目下下营销经理和招商总监-管理者
                    if(myProjectIds!=null && myProjectIds.size()>0){
                        userIdsCreate=taskDao.getProjectYxjlAndZszj(myProjectIds);
                    }

                    paramMap.put("isSelf","3");//如果有这些岗位贼判断为管理者隐号权限
                }
            }
        }
        paramMap.put("myProjectIds", myProjectIds);
        paramMap.put("userIds", userIds);
    }
    
    /**
     * 查询团队用户信息
     */
    @Log("查询团队用户信息")
    @PostMapping("/queryTeamUserInfo")
    @ResponseBody
    @ApiOperation(value = "查询团队用户信息", notes = "根据项目信息查询团队用户信息（包含ID、姓名、账号）")
    public ResultBody queryTeamUserInfo(@RequestBody Map<String, Object> paramMap) {
        try {
            // 处理销售归属人姓名参数
//            List<String> salesAttributionNameList = new ArrayList<>();
//            Object salesAttributionNameSObj = paramMap.get("salesAttributionNameS");
//            if (salesAttributionNameSObj instanceof List) {
//                List<?> rawList = (List<?>) salesAttributionNameSObj;
//                salesAttributionNameList = rawList.stream().map(String::valueOf).collect(Collectors.toList());
//                paramMap.put("salesAttributionNameS", salesAttributionNameList);
//            } else {
//                paramMap.remove("salesAttributionNameS");
//            }
            
            List<Map> teamUserInfo = new ArrayList<>();
            List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
            if (!CollectionUtils.isEmpty(userList)) {
                for (Map user : userList) {
                    String jobCode = String.valueOf(user.get("JobCode"));
                    if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)
                            || "yxjl".equals(jobCode) || "qyyxjl".equals(jobCode)
                            || "zszj".equals(jobCode) || "qyzszj".equals(jobCode) ||"jtsjg".equals(jobCode)
                            || "xmz".equals(jobCode) || "qyfz".equals(jobCode) || "qyz".equals(jobCode) || "10001".equals(jobCode)) {
                        String orgPath = String.valueOf(user.get("FullPath"));
                        List<Map> teamUserS = custMapDao.getTeamUserInfo(orgPath);
                        teamUserInfo.addAll(teamUserS);
                    }
                }
            }

            if (CollectionUtils.isEmpty(teamUserInfo)) {
                return ResultBody.error(-10002, "未找到用户信息");
            }
            
            // 通过userId进行去重处理
            Map<String, Map> uniqueUsers = new HashMap<>();
            for (Map user : teamUserInfo) {
                String userId = String.valueOf(user.get("userId"));
                if (StringUtils.isNotBlank(userId)) {
                    // 如果已存在相同userId，保留第一个（或者可以根据需要保留最新的）
                    if (!uniqueUsers.containsKey(userId)) {
                        uniqueUsers.put(userId, user);
                    }
                }
            }
            
            // 转换回List
            List<Map> uniqueUserList = new ArrayList<>(uniqueUsers.values());
            
            return ResultBody.success(uniqueUserList);
        } catch (Exception e) {
            return ResultBody.error(-10003, "查询团队用户信息失败：" + e.getMessage());
        }
    }

    // 查询商机客户
    private List<ZsMapResBO> queryOpportunityCustomers(Map paramMap, List<String> clueStatusList) {
        List<ZsMapResBO> opportunitys = custMapDao.getOpportunityFromCache(paramMap);

        String latLonStr = (String) paramMap.get("latLon");
        if (StringUtils.isNotEmpty(latLonStr)) { // 仅当latLon非空时处理
            JSONArray jsonArray = JSONArray.parseArray(latLonStr);
            if (!jsonArray.isEmpty()) { // 数组非空才判断维度
                boolean isTwoDimensional = jsonArray.get(0) instanceof JSONArray;

                if (isTwoDimensional) {
                    // 二维数组：循环内层数组，逐个调用一维过滤方法
                    for (Object innerObj : jsonArray) {
                        if (innerObj instanceof JSONArray) {
                            String oneDimLatLonStr = ((JSONArray) innerObj).toJSONString();
                            opportunitys = filterByLatLon(opportunitys, oneDimLatLonStr);
                        }
                    }
                } else {
                    // 一维数组：直接调用过滤方法
                    opportunitys = filterByLatLon(opportunitys, latLonStr);
                }
            }
        }

        return opportunitys.stream()
                .filter(item -> clueStatusList.contains(item.getType()))
                .collect(Collectors.toList());
    }

    // 查询线索客户
    private List<ZsMapResBO> queryClueCustomers(Map paramMap, List<String> clueStatusList) {
        List<ZsMapResBO> clues = custMapDao.getCluesFromCache(paramMap);

//        //任务选择用户需要过滤掉已经下发的线索
//        if("1".equals(paramMap.get("isTaskCreate"))){
//            List<String> taskClueIds = custMapDao.selectActiveTaskCustomerIds(paramMap.get("projectId").toString());
//            Set<String> taskClueIdSet = new HashSet<>(taskClueIds);
//            clues = clues.stream()
//                    .filter(clue -> !taskClueIdSet.contains(clue.getProjectClueId()))
//                    .collect(Collectors.toList());
//        }
//        if (StringUtils.isNotEmpty((String) paramMap.get("latLon"))) {
//            clues = filterByLatLon(clues, (String) paramMap.get("latLon"));
//        }

        String latLonStr = (String) paramMap.get("latLon");
        if (StringUtils.isNotEmpty(latLonStr)) { // 仅当latLon非空时处理
            JSONArray jsonArray = JSONArray.parseArray(latLonStr);
            if (!jsonArray.isEmpty()) { // 数组非空才判断维度
                boolean isTwoDimensional = jsonArray.get(0) instanceof JSONArray;

                if (isTwoDimensional) {
                    // 二维数组：循环内层数组，逐个调用一维过滤方法
                    for (Object innerObj : jsonArray) {
                        if (innerObj instanceof JSONArray) {
                            String oneDimLatLonStr = ((JSONArray) innerObj).toJSONString();
                            clues = filterByLatLon(clues, oneDimLatLonStr);
                        }
                    }
                } else {
                    // 一维数组：直接调用过滤方法
                    clues = filterByLatLon(clues, latLonStr);
                }
            }
        }

        return clues.stream()
                .filter(item -> clueStatusList.contains(item.getType()))
                .collect(Collectors.toList());
    }

    // 查询公客池客户
    private List<ZsMapResBO> queryPublicPoolCustomers(Map paramMap) {
        if(paramMap.get("flagType").toString().equals("0")){
            paramMap.put("poolType","1");//全部客户查项目公客池
        }
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

//            if (StringUtils.isNotEmpty((String) paramMap.get("latLon"))) {
//                batchList = filterByLatLon(batchList, (String) paramMap.get("latLon"));
//            }
            String latLonStr = (String) paramMap.get("latLon");
            if (StringUtils.isNotEmpty(latLonStr)) { // 仅当latLon非空时处理
                JSONArray jsonArray = JSONArray.parseArray(latLonStr);
                if (!jsonArray.isEmpty()) { // 数组非空才判断维度
                    boolean isTwoDimensional = jsonArray.get(0) instanceof JSONArray;

                    if (isTwoDimensional) {
                        // 二维数组：循环内层数组，逐个调用一维过滤方法
                        for (Object innerObj : jsonArray) {
                            if (innerObj instanceof JSONArray) {
                                String oneDimLatLonStr = ((JSONArray) innerObj).toJSONString();
                                batchList = filterByLatLon(batchList, oneDimLatLonStr);
                            }
                        }
                    } else {
                        // 一维数组：直接调用过滤方法
                        batchList = filterByLatLon(batchList, latLonStr);
                    }
                }
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
        String pTaskId = paramMap.get("pTaskId") == null ? null : paramMap.get("pTaskId").toString();
        if(StringUtils.isNotEmpty(pTaskId)){
            Map<String, Object> ptask= taskDao.selectTaskByTaskId(pTaskId).get(0);
            String pTaskCreateBy=ptask.get("createBy").toString();
            paramMap.put("pTaskCreateBy",pTaskCreateBy);
        }
        //根据当前用户查询上级人员
        List<ZsMapResBO> clues = custMapDao.getCluesFromCache(paramMap);
        List<String> taskClueIds=null;
        //任务选择用户需要过滤掉已经下发的线索
        if("1".equals(paramMap.get("isTaskCreate"))){
            taskClueIds = custMapDao.selectActiveTaskCustomerIds(paramMap.get("projectId").toString());
            Set<String> taskClueIdSet = new HashSet<>(taskClueIds);
            clues = clues.stream()
                    .filter(clue -> !taskClueIdSet.contains(clue.getProjectClueId()))
                    .collect(Collectors.toList());
        }

//        if (StringUtils.isNotEmpty((String) paramMap.get("latLon"))) {
//            clues = filterByLatLon(clues, (String) paramMap.get("latLon"));
//        }

        String latLonStr = (String) paramMap.get("latLon");
        if (StringUtils.isNotEmpty(latLonStr)) { // 仅当latLon非空时处理
            JSONArray jsonArray = JSONArray.parseArray(latLonStr);
            if (!jsonArray.isEmpty()) { // 数组非空才判断维度
                boolean isTwoDimensional = jsonArray.get(0) instanceof JSONArray;

                if (isTwoDimensional) {
                    // 二维数组：循环内层数组，逐个调用一维过滤方法
                    for (Object innerObj : jsonArray) {
                        if (innerObj instanceof JSONArray) {
                            String oneDimLatLonStr = ((JSONArray) innerObj).toJSONString();
                            clues = filterByLatLon(clues, oneDimLatLonStr);
                        }
                    }
                } else {
                    // 一维数组：直接调用过滤方法
                    clues = filterByLatLon(clues, latLonStr);
                }
            }
        }


        //任务下发只下发目标企业客户
        if ("1".equals(paramMap.get("isTaskCreate"))) {
            clues = filterTargetEnterprises(clues);
        }

        return clues;
    }

    // 查询一键拓客客户
    private List<ZsMapResBO> queryYjtkCustomers(Map paramMap, List<String> clueStatusList) {
        String pTaskId = paramMap.get("pTaskId") == null ? null : paramMap.get("pTaskId").toString();
        if(StringUtils.isNotEmpty(pTaskId)){
            Map<String, Object> ptask= taskDao.selectTaskByTaskId(pTaskId).get(0);
            String pTaskCreateBy=ptask.get("createBy").toString();
            paramMap.put("pTaskCreateBy",pTaskCreateBy);
        }
        //根据当前用户查询上级人员
        List<ZsMapResBO> clues = custMapDao.getCluesFromCache(paramMap);
        List<String> taskClueIds=null;
        //任务选择用户需要过滤掉已经下发的线索
        if("1".equals(paramMap.get("isTaskCreate"))){
            taskClueIds = custMapDao.selectActiveTaskCustomerIds(paramMap.get("projectId").toString());
            Set<String> taskClueIdSet = new HashSet<>(taskClueIds);
            clues = clues.stream()
                    .filter(clue -> !taskClueIdSet.contains(clue.getProjectClueId()))
                    .collect(Collectors.toList());
        }

//        if (StringUtils.isNotEmpty((String) paramMap.get("latLon"))) {
//            clues = filterByLatLon(clues, (String) paramMap.get("latLon"));
//        }

        String latLonStr = (String) paramMap.get("latLon");
        if (StringUtils.isNotEmpty(latLonStr)) { // 仅当latLon非空时处理
            JSONArray jsonArray = JSONArray.parseArray(latLonStr);
            if (!jsonArray.isEmpty()) { // 数组非空才判断维度
                boolean isTwoDimensional = jsonArray.get(0) instanceof JSONArray;

                if (isTwoDimensional) {
                    // 二维数组：循环内层数组，逐个调用一维过滤方法
                    for (Object innerObj : jsonArray) {
                        if (innerObj instanceof JSONArray) {
                            String oneDimLatLonStr = ((JSONArray) innerObj).toJSONString();
                            clues = filterByLatLon(clues, oneDimLatLonStr);
                        }
                    }
                } else {
                    // 一维数组：直接调用过滤方法
                    clues = filterByLatLon(clues, latLonStr);
                }
            }
        }


        //任务下发只下发目标企业客户
        if ("1".equals(paramMap.get("isTaskCreate"))) {
            clues = filterTargetEnterprises(clues);
        }

        return clues;
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

    // 辅助方法：判断是否应该查询商机客户（多选版本）
    private boolean shouldQueryOpportunity(List<String> flagTypes) {
        if (flagTypes == null || flagTypes.isEmpty()) {
            return true; // 没有指定时查询所有
        }
        return flagTypes.contains("0") || flagTypes.contains("1");
    }

    // 辅助方法：判断是否应该查询线索客户
    private boolean shouldQueryClue(String flagType) {
        return StringUtils.isEmpty(flagType) || "0".equals(flagType) || "4".equals(flagType);
    }

    // 辅助方法：判断是否应该查询线索客户（多选版本）
    private boolean shouldQueryClue(List<String> flagTypes) {
        if (flagTypes == null || flagTypes.isEmpty()) {
            return true; // 没有指定时查询所有
        }
        return flagTypes.contains("0") || flagTypes.contains("4");
    }

    // 辅助方法：判断是否应该查询公客池
    private boolean shouldQueryPublicPool(String flagType) {
        return StringUtils.isEmpty(flagType) || "0".equals(flagType) || flagType.indexOf("2") != -1;
    }

    // 辅助方法：判断是否应该查询公客池（多选版本）
    private boolean shouldQueryPublicPool(List<String> flagTypes) {
        if (flagTypes == null || flagTypes.isEmpty()) {
            return true; // 没有指定时查询所有
        }
        return flagTypes.contains("0") || flagTypes.stream().anyMatch(ft -> ft.contains("2"));
    }

    // 辅助方法：判断是否应该查询地图导入
    private boolean shouldQueryMapImport(String flagType) {
        return StringUtils.isEmpty(flagType) || "0".equals(flagType) || "3".equals(flagType);
    }

    // 辅助方法：判断是否应该查询地图导入（多选版本）
    private boolean shouldQueryMapImport(List<String> flagTypes) {
        if (flagTypes == null || flagTypes.isEmpty()) {
            return true; // 没有指定时查询所有
        }
        return flagTypes.contains("0") || flagTypes.contains("3");
    }

    // 辅助方法：判断是否应该查询一键拓客客户
    private boolean shouldQueryYjtk(String flagType) {
        return StringUtils.isEmpty(flagType) || "0".equals(flagType) || "5".equals(flagType);
    }

    // 辅助方法：判断是否应该查询一键拓客客户（多选版本）
    private boolean shouldQueryYjtk(List<String> flagTypes) {
        if (flagTypes == null || flagTypes.isEmpty()) {
            return true; // 没有指定时查询所有
        }
        return flagTypes.contains("0") || flagTypes.contains("5");
    }

    // 辅助方法：判断是否包含商机状态
    private boolean hasOpportunityStatus(List<String> clueStatusList, String mapType) {
        if ("1".equals(mapType)) { // 招商地图的商机状态
            return clueStatusList.stream().anyMatch(s -> "12345".contains(s));
        } else { // 拓客地图的商机状态
            return clueStatusList.contains("1") || clueStatusList.contains("2") ||
                    clueStatusList.contains("7") || clueStatusList.contains("6") ||
                    clueStatusList.contains("4") || clueStatusList.contains("8");
        }
    }

    // 辅助方法：判断是否包含线索状态
    private boolean hasClueStatus(List<String> clueStatusList, String mapType) {
        if ("1".equals(mapType)) { // 招商地图的线索状态
            return clueStatusList.stream().anyMatch(s -> "6789".contains(s));
        } else { // 拓客地图的线索状态
            return clueStatusList.contains("3") || clueStatusList.contains("5") ||
                    clueStatusList.contains("9") || clueStatusList.contains("8");
        }
    }

    // 辅助方法：添加状态消息
    private void appendStatusMessage(StringBuilder messageBuilder, List<ZsMapResBO> results, List<String> clueStatusList, String mapType) {
        for (String status : clueStatusList) {
            List<ZsMapResBO> filteredResults = results.stream()
                    .filter(item -> status.equals(item.getType()))
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(filteredResults)) {
                String statusName;
                if ("1".equals(mapType)) { // 招商地图
                    statusName = ClueStatusEnum.getNameByStatus(status);
                } else { // 拓客地图
                    statusName = ClueAcquisitionStatusEnum.getNameByStatus(status);
                }

                if (statusName != null) {
                    messageBuilder.append(statusName)
                            .append("的客户有")
                            .append(filteredResults.size())
                            .append("组;");
                }
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

    // 辅助方法：一键拓客消息
    private void appendYjtkMessage(StringBuilder messageBuilder, List<ZsMapResBO> results, List<String> clueStatusList, String mapType) {
        appendStatusMessage(messageBuilder, results, clueStatusList, mapType);
    }
    // 辅助方法：添加地图导入消息
    private void appendMapImportMessage(StringBuilder messageBuilder, List<ZsMapResBO> results, List<String> clueStatusList, String mapType) {
        appendStatusMessage(messageBuilder, results, clueStatusList, mapType);
    }



    private List<String> initClueStatus(List<String> types, List<String> flagTypes, String mapType) {
        // 如果指定了具体的状态，则优先使用
        if (types != null && types.size() > 0) {
            return types;
        }

        // 如果没有指定flagTypes，返回所有状态
        if (flagTypes == null || flagTypes.isEmpty()) {
            return Stream.of("1", "2", "3", "4", "5", "6", "7", "8", "9").collect(Collectors.toList());
        }

        Set<String> resultStatusSet = new HashSet<>();

        if ("1".equals(mapType)) { // 招商地图逻辑
            for (String flagType : flagTypes) {
                switch (flagType) {
                    case "1": // 在保客户
                        resultStatusSet.addAll(Stream.of("1", "2", "3", "4", "5").collect(Collectors.toList()));
                        break;
                    case "4": // 线索客户(只查询线索相关状态)
                        resultStatusSet.addAll(Stream.of("6", "7", "8", "9").collect(Collectors.toList()));
                        break;
                    case "3": // 线索客户/地图导入客户
                        resultStatusSet.addAll(Stream.of("6", "7", "8", "9", "1").collect(Collectors.toList()));
                        break;
                    default:
                        break;
                }
            }
        } else { // 拓客地图逻辑
            for (String flagType : flagTypes) {
                switch (flagType) {
                    case "1": // 在保客户 (只查询商机相关状态)
                        resultStatusSet.addAll(Stream.of("1", "2", "4", "6", "7", "3", "5", "8", "9").collect(Collectors.toList()));
                        break;
                    case "4": // 线索客户 (只查询线索相关状态)
                        resultStatusSet.addAll(Stream.of("3", "5", "8", "9").collect(Collectors.toList()));
                        break;
                    case "3": // 地图导入客户 (也属于线索范畴)
                        resultStatusSet.addAll(Stream.of("3", "5", "8", "9", "1", "2").collect(Collectors.toList()));
                        break;
                    default:
                        break;
                }
            }
        }

        // 如果没有任何匹配的状态，返回所有状态
        if (resultStatusSet.isEmpty()) {
            return Stream.of("1", "2", "3", "4", "5", "6", "7", "8", "9").collect(Collectors.toList());
        }

        return new ArrayList<>(resultStatusSet);
    }

    /**
     * 格式化日期为 "yyyy-MM-dd HH:mm:ss"
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 数据脱敏处理
     */
    private String maskData(String data, boolean showAll, String type) {
        if ("custname".equals(type)) {
            int len = data.length();
            if (len == 0) return data;
            int maskCount = (int) Math.ceil(len / 3.0);
            Set<Integer> maskIndexes = new HashSet<>();
            Random random = new Random();
            while (maskIndexes.size() < maskCount) {
                maskIndexes.add(random.nextInt(len));
            }
            char[] chars = data.toCharArray();
            for (int idx : maskIndexes) {
                chars[idx] = '*';
            }
            return new String(chars);
        }
        if ("phone".equals(type) && data.length() == 11) {
            return data.substring(0, 3) + "****" + data.substring(7);
        }
        return data;
    }

    public String[]  courtCaseTitle2 =  new String[]{
            "序号", "项目", "企业名称","企业地址","详细地址", "经度","纬度", "客户来源", "客户行业", "二级分类","主营产品","主要原材料","企业现有员工数","现有厂房面积",
            "企业年产值","企业年度纳税额","厂房类型",
            "联系人", "联系人方式", "首次录入时间", "录入人", "报备人团队","报备人身份",
            "报备人","报备时间", "接待最新跟进时间", "过保有效时间", "剩余天数", "首访时间", "复访时间", "客户等级","是否捞取客户", "当前状态","是否满足三个一","意向类型",
            "意向面积","意向单价","意向楼层","是否园区", "园区地址", "园区层数", "园区名称", "客户阶段", "标签"
    };

    @Log("获取面标记面积")
    @PostMapping("/getDrawAcreage")
    @ApiOperation(value = "获取面标记面积", notes = "获取面标记面积")
    public ResultBody getDrawAcreage(@RequestBody Map<String, Object> paramMap) {
        try {
            Object latLonObj = paramMap.get("latLon");

            // 参数校验
            if (latLonObj == null) {
                return ResultBody.error(-10000,"经纬度参数不能为空");
            }

            List<double[]> coordinates;

            // 支持两种格式：JSON数组格式和字符串格式
            if (latLonObj instanceof String) {
                String latLonStr = (String) latLonObj;
                if (latLonStr.trim().startsWith("[")) {
                    // 以 [ 开头，直接当 JSON 数组处理
                    coordinates = parseJsonCoordinates(latLonStr);
                } else {
                    // 否则按原有字符串格式处理
                    coordinates = parseStringCoordinates(latLonStr);
                }
            } else {
                // 兼容老的 JSON 对象传参
                coordinates = parseJsonCoordinates(JSON.toJSONString(latLonObj));
            }

            // 计算多边形面积
            double area = calculatePolygonAreaFromCoordinates(coordinates);

            Map<String, Object> result = new HashMap<>();
            result.put("area", Math.round(area * 100.0) / 100.0); // 面积，单位：平方米，保留2位小数
            result.put("areaKm2", Math.round(area / 1000000 * 10000.0) / 10000.0); // 面积，单位：平方公里，保留4位小数
            result.put("areaHectare", Math.round(area / 10000 * 100.0) / 100.0); // 面积，单位：公顷，保留2位小数
            result.put("areaMu", Math.round(area / 666.67 * 100.0) / 100.0); // 面积，单位：亩，保留2位小数

            return ResultBody.success(result);
        } catch (Exception e) {
            return ResultBody.error(-10002,"计算面积失败：" + e.getMessage());
        }
    }
    
    @Log("一键拓客查询企业")
    @PostMapping("/queryCompanyYjtk")
    @ApiOperation(value = "一键拓客查询企业", notes = "通过百度地图API查询企业信息")
    public ResultBody queryCompanyYjtk(@RequestBody Map<String, Object> paramMap) {
        try {
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
            paramMap.put("UserName", userName);
            paramMap.put("UserId", userId);

            // 3. 获取查询参数
            String query = (String) paramMap.get("query"); // 查询关键词
            String region = (String) paramMap.get("region"); // 查询区域
            String type = (String) paramMap.get("type"); // 查询类型
            String ak = "krHOOUhCZ23jxq5GdKKYuDTve6EaFy52"; // 百度地图API密钥
            
            // 4. 获取分页参数
            Integer pageNum = (Integer) paramMap.get("page_num");
            Integer pageSize = (Integer) paramMap.get("page_size");
            
            // 设置默认值
            if (pageNum == null || pageNum < 0) {
                pageNum = 0; // 百度地图API从0开始
            }
            if (pageSize == null || pageSize <= 0 || pageSize > 20) {
                pageSize = 20; // 默认20条，最大20条
            }
            
            // 5. 参数校验
            if (StringUtils.isEmpty(query)) {
                return ResultBody.error(-10001, "查询关键词不能为空");
            }
            
            // 6. 构建请求URL - 使用place/v3/search接口进行关键词搜索
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://api.map.baidu.com/place/v3/region?");
            urlBuilder.append("query=").append(encodeUrl(query));
            if (StringUtils.isNotEmpty(region)) {
                urlBuilder.append("&region=").append(encodeUrl(region));
            }
            if (StringUtils.isNotEmpty(type)) {
                urlBuilder.append("&type=").append(encodeUrl(type));
            }
            urlBuilder.append("&ak=").append(ak);
            urlBuilder.append("&output=json");
            urlBuilder.append("&page_size=").append(pageSize);
            urlBuilder.append("&page_num=").append(pageNum);
            // 添加其他可选参数
            urlBuilder.append("&scope=2"); // 返回详细信息
            urlBuilder.append("&coord_type=3"); // 百度坐标
            
            String url = urlBuilder.toString();
            
            // 7. 发送HTTP请求
            String response = sendHttpGetRequest(url);
            
            // 8. 解析响应结果
            Map<String, Object> result = parseBaiduMapResponse(response, pageNum, pageSize);
            
            return ResultBody.success(result);
            
        } catch (Exception e) {
            return ResultBody.error(-10002, "查询企业信息失败：" + e.getMessage());
        }
    }

    @Log("一键拓客查询企业-圆形区域")
    @PostMapping("/queryCompanyYjtkAround")
    @ApiOperation(value = "一键拓客查询企业-圆形区域", notes = "通过百度地图API查询企业信息-圆形区域搜索")
    public ResultBody queryCompanyYjtkAround(@RequestBody Map<String, Object> paramMap) {
        try {
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
            paramMap.put("UserName", userName);
            paramMap.put("UserId", userId);

            // 3. 获取查询参数
            String query = (String) paramMap.get("query"); // 查询关键词
            String location = (String) paramMap.get("location"); // 中心点坐标 "lng,lat"
            String radius = (String) paramMap.get("radius"); // 搜索半径(米)
            String ak = "krHOOUhCZ23jxq5GdKKYuDTve6EaFy52"; // 百度地图API密钥
            
            // 4. 获取分页参数
            Integer pageNum = (Integer) paramMap.get("page_num");
            Integer pageSize = (Integer) paramMap.get("page_size");
            
            // 设置默认值
            if (pageNum == null || pageNum < 0) {
                pageNum = 0; // 百度地图API从0开始
            }
            if (pageSize == null || pageSize <= 0 || pageSize > 20) {
                pageSize = 20; // 默认20条，最大20条
            }
            if (StringUtils.isEmpty(radius)) {
                radius = "1000"; // 默认1000米
            }
            
            // 5. 参数校验
            if (StringUtils.isEmpty(query)) {
                return ResultBody.error(-10001, "查询关键词不能为空");
            }
            if (StringUtils.isEmpty(location)) {
                return ResultBody.error(-10003, "中心点坐标不能为空，格式：lng,lat");
            }
            
            // 验证坐标格式
            String[] coords = location.split(",");
            if (coords.length != 2) {
                return ResultBody.error(-10004, "中心点坐标格式错误，应为：lng,lat");
            }
            
            try {
                Double.parseDouble(coords[0]); // 经度
                Double.parseDouble(coords[1]); // 纬度
            } catch (NumberFormatException e) {
                return ResultBody.error(-10005, "中心点坐标数值格式错误");
            }
            
            // 6. 构建请求URL - 使用place/v3/search接口进行圆形区域搜索
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://api.map.baidu.com/place/v3/around?");
            urlBuilder.append("query=").append(encodeUrl(query));
            urlBuilder.append("&location=").append(location);
            urlBuilder.append("&radius=").append(radius);
            urlBuilder.append("&ak=").append(ak);
            urlBuilder.append("&output=json");
            urlBuilder.append("&page_size=").append(pageSize);
            urlBuilder.append("&page_num=").append(pageNum);
            // 添加其他可选参数
            urlBuilder.append("&scope=2"); // 返回详细信息
            urlBuilder.append("&coord_type=3"); // 百度坐标
            urlBuilder.append("&radius_limit=true"); // 严格限制在半径范围内
            
            String url = urlBuilder.toString();
            
            // 7. 发送HTTP请求
            String response = sendHttpGetRequest(url);
            
            // 8. 解析响应结果
            Map<String, Object> result = parseBaiduMapResponse(response, pageNum, pageSize);
            
            // 9. 添加圆形区域搜索的额外信息
            result.put("search_type", "circular_area");
            result.put("location", location);
            result.put("radius", radius);
            result.put("radius_unit", "meters");
            
            return ResultBody.success(result);
            
        } catch (Exception e) {
            return ResultBody.error(-10002, "查询企业信息失败：" + e.getMessage());
        }
    }
    
    
    /**
     * 发送HTTP GET请求
     */
    private String sendHttpGetRequest(String url) throws Exception {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                throw new Exception("HTTP请求失败，响应码：" + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * URL编码
     */
    private String encodeUrl(String str) throws Exception {
        return URLEncoder.encode(str, "UTF-8");
    }
    
    /**
     * 解析百度地图API响应
     */
    private Map<String, Object> parseBaiduMapResponse(String response, Integer pageNum, Integer pageSize) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 使用JSONObject解析响应
            JSONObject jsonResponse = JSONObject.parseObject(response);
            
            // 检查响应状态
            Integer status = jsonResponse.getInteger("status");
            if (status != null && status == 0) {
                // 成功响应
                JSONArray results = jsonResponse.getJSONArray("results");
                List<Map<String, Object>> companies = new ArrayList<>();
                
                if (results != null) {
                    for (int i = 0; i < results.size(); i++) {
                        JSONObject company = results.getJSONObject(i);
                        Map<String, Object> companyInfo = new HashMap<>();
                        
                        // 基本信息
                        companyInfo.put("uid", company.getString("uid"));
                        companyInfo.put("name", company.getString("name"));
                        companyInfo.put("address", company.getString("address"));
                        
                        // 位置信息
                        JSONObject location = company.getJSONObject("location");
                        if (location != null) {
                            companyInfo.put("lng", location.getDouble("lng"));
                            companyInfo.put("lat", location.getDouble("lat"));
                        }
                        
                        // 行政区划信息
                        companyInfo.put("province", company.getString("province"));
                        companyInfo.put("city", company.getString("city"));
                        companyInfo.put("area", company.getString("area"));
                        companyInfo.put("town", company.getString("town"));
                        companyInfo.put("adcode", company.getInteger("adcode"));
                        
                        // 联系信息
                        companyInfo.put("telephone", company.getString("telephone"));
                        
                        // 其他信息
                        companyInfo.put("street_id", company.getString("street_id"));
                        companyInfo.put("detail", company.getInteger("detail"));
                        companyInfo.put("tag", company.getString("tag"));
                        companyInfo.put("type", company.getString("type"));
                        companyInfo.put("distance", company.getInteger("distance"));
                        
                        // 营业状态
                        companyInfo.put("status", company.getString("status"));
                        
                        // 详细信息（如果有）
                        JSONObject detailInfo = company.getJSONObject("detail_info");
                        if (detailInfo != null) {
                            companyInfo.put("overall_rating", detailInfo.getDouble("overall_rating"));
                            companyInfo.put("price", detailInfo.getDouble("price"));
                            companyInfo.put("shop_hours", detailInfo.getString("shop_hours"));
                            companyInfo.put("comment_num", detailInfo.getInteger("comment_num"));
                            companyInfo.put("image_num", detailInfo.getInteger("image_num"));
                            companyInfo.put("detail_url", detailInfo.getString("detail_url"));
                        }
                        
                        companies.add(companyInfo);
                    }
                }
                
                // 获取总数
                Integer total = jsonResponse.getInteger("total");
                if (total == null) {
                    total = companies.size(); // 如果没有total字段，使用当前结果数量
                }
                
                result.put("status", 0);
                result.put("message", "查询成功");
                result.put("total", total);
                result.put("companies", companies);
                result.put("result_type", jsonResponse.getString("result_type"));
                result.put("query_type", jsonResponse.getString("query_type"));
                
                // 分页信息
                result.put("page_num", pageNum);
                result.put("page_size", pageSize);
                result.put("has_more", companies.size() == pageSize && (pageNum + 1) * pageSize < total);
                
            } else {
                // 失败响应
                String message = jsonResponse.getString("message");
                if (StringUtils.isEmpty(message)) {
                    message = "查询失败，状态码：" + status;
                }
                
                result.put("status", status != null ? status : -1);
                result.put("message", message);
                result.put("companies", new ArrayList<>());
                result.put("total", 0);
                result.put("page_num", pageNum);
                result.put("page_size", pageSize);
                result.put("has_more", false);
            }
            
        } catch (Exception e) {
            // 解析失败
            result.put("status", -1);
            result.put("message", "响应解析失败：" + e.getMessage());
            result.put("companies", new ArrayList<>());
            result.put("total", 0);
            result.put("page_num", pageNum);
            result.put("page_size", pageSize);
            result.put("has_more", false);
        }
        
        return result;
    }

    /**
     * 解析字符串格式的经纬度坐标
     * @param latLonStr 经纬度字符串，格式："lng1,lat1;lng2,lat2;lng3,lat3;..."
     * @return 坐标列表
     */
    private List<double[]> parseStringCoordinates(String latLonStr) {
        if (StringUtils.isEmpty(latLonStr)) {
            throw new IllegalArgumentException("经纬度参数不能为空");
        }

        String[] points = latLonStr.split(";");
        if (points.length < 3) {
            throw new IllegalArgumentException("至少需要3个点才能构成多边形");
        }

        List<double[]> coordinates = new ArrayList<>();
        for (String point : points) {
            String[] lonLat = point.split(",");
            if (lonLat.length != 2) {
                throw new IllegalArgumentException("经纬度格式错误：" + point);
            }
            try {
                double lon = Double.parseDouble(lonLat[0]);
                double lat = Double.parseDouble(lonLat[1]);
                coordinates.add(new double[]{lon, lat});
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("经纬度数值格式错误：" + point);
            }
        }

        return coordinates;
    }

    /**
     * 解析JSON格式的经纬度坐标
     * @param jsonStr JSON字符串，格式："[{\"lng\":115.458686,\"lat\":22.956984},...]"
     * @return 坐标列表
     */
    private List<double[]> parseJsonCoordinates(String jsonStr) {
        if (StringUtils.isEmpty(jsonStr)) {
            throw new IllegalArgumentException("经纬度参数不能为空");
        }
        try {
            // 用 fastjson 直接解析
            List<Map<String, Object>> list = JSON.parseObject(jsonStr, List.class);
            List<double[]> coordinates = new ArrayList<>();
            for (Map<String, Object> obj : list) {
                double lng = Double.parseDouble(obj.get("lng").toString());
                double lat = Double.parseDouble(obj.get("lat").toString());
                coordinates.add(new double[]{lng, lat});
            }
            if (coordinates.size() < 3) {
                throw new IllegalArgumentException("至少需要3个点才能构成多边形");
            }
            return coordinates;
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON格式解析错误：" + e.getMessage());
        }
    }

    /**
     * 根据坐标列表计算多边形面积
     * @param coordinates 坐标列表
     * @return 面积（平方米）
     */
    private double calculatePolygonAreaFromCoordinates(List<double[]> coordinates) {
        if (coordinates == null || coordinates.size() < 3) {
            return 0.0;
        }

        // 确保多边形闭合
        if (!Arrays.equals(coordinates.get(0), coordinates.get(coordinates.size() - 1))) {
            coordinates.add(coordinates.get(0));
        }

        return calculateSphericalPolygonArea(coordinates);
    }

    /**
     * 使用球面几何算法计算多边形面积
     * @param coordinates 坐标列表，每个元素为[经度, 纬度]
     * @return 面积（平方米）
     */
    private double calculateSphericalPolygonArea(List<double[]> coordinates) {
        final double EARTH_RADIUS = 6378137.0; // 地球半径（米）

        if (coordinates.size() < 4) { // 至少需要4个点（包括闭合点）
            return 0.0;
        }

        double area = 0.0;
        int n = coordinates.size() - 1; // 排除闭合点

        for (int i = 0; i < n; i++) {
            double[] p1 = coordinates.get(i);
            double[] p2 = coordinates.get((i + 1) % n);

            double lon1 = Math.toRadians(p1[0]);
            double lat1 = Math.toRadians(p1[1]);
            double lon2 = Math.toRadians(p2[0]);
            double lat2 = Math.toRadians(p2[1]);

            // 使用球面三角形面积公式
            double deltaLon = lon2 - lon1;
            double E = 2 * Math.atan2(Math.tan(deltaLon / 2) * (Math.sin(lat1) + Math.sin(lat2)),
                    Math.tan(deltaLon / 2) * (Math.sin(lat1) - Math.sin(lat2)) + 2);
            area += E;
        }

        area = Math.abs(area) * EARTH_RADIUS * EARTH_RADIUS / 2;
        return area;
    }

    /**
     * 使用Shoelace公式计算多边形面积（平面几何，适用于小范围区域）
     * @param coordinates 坐标列表，每个元素为[经度, 纬度]
     * @return 面积（平方米）
     */
    private double calculatePlanarPolygonArea(List<double[]> coordinates) {
        if (coordinates.size() < 4) {
            return 0.0;
        }

        double area = 0.0;
        int n = coordinates.size() - 1; // 排除闭合点

        for (int i = 0; i < n; i++) {
            double[] p1 = coordinates.get(i);
            double[] p2 = coordinates.get((i + 1) % n);

            // 将经纬度转换为米（近似计算）
            double x1 = p1[0] * 111320 * Math.cos(Math.toRadians(p1[1]));
            double y1 = p1[1] * 110540;
            double x2 = p2[0] * 111320 * Math.cos(Math.toRadians(p2[1]));
            double y2 = p2[1] * 110540;

            area += (x1 * y2 - x2 * y1);
        }

        return Math.abs(area) / 2.0;
    }

    @Log("一键拓客查询企业-矩形区域")
    @PostMapping("/queryCompanyYjtkRect")
    @ApiOperation(value = "一键拓客查询企业-矩形区域", notes = "通过百度地图API查询企业信息-矩形区域搜索")
    public ResultBody queryCompanyYjtkRect(@RequestBody Map<String, Object> paramMap) {
        try {
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
            paramMap.put("UserName", userName);
            paramMap.put("UserId", userId);

            // 3. 获取查询参数
            String query = (String) paramMap.get("query"); // 查询关键词
            String bounds = (String) paramMap.get("bounds"); // 矩形区域边界 "lng1,lat1,lng2,lat2"
            String type = (String) paramMap.get("type"); // 查询类型
            String ak = "krHOOUhCZ23jxq5GdKKYuDTve6EaFy52"; // 百度地图API密钥
            
            // 4. 获取分页参数
            Integer pageNum = (Integer) paramMap.get("page_num");
            Integer pageSize = (Integer) paramMap.get("page_size");
            
            // 设置默认值
            if (pageNum == null || pageNum < 0) {
                pageNum = 0; // 百度地图API从0开始
            }
            if (pageSize == null || pageSize <= 0 || pageSize > 20) {
                pageSize = 20; // 默认20条，最大20条
            }
            
            // 5. 参数校验
            if (StringUtils.isEmpty(query)) {
                return ResultBody.error(-10001, "查询关键词不能为空");
            }
            if (StringUtils.isEmpty(bounds)) {
                return ResultBody.error(-10006, "矩形区域边界不能为空，格式：lng1,lat1,lng2,lat2");
            }
            
//            // 验证边界格式
//            String[] coords = bounds.split(",");
//            if (coords.length != 4) {
//                return ResultBody.error(-10007, "矩形区域边界格式错误，应为：lng1,lat1,lng2,lat2");
//            }
//
//            try {
//                Double.parseDouble(coords[0]); // 经度1
//                Double.parseDouble(coords[1]); // 纬度1
//                Double.parseDouble(coords[2]); // 经度2
//                Double.parseDouble(coords[3]); // 纬度2
//            } catch (NumberFormatException e) {
//                return ResultBody.error(-10008, "矩形区域边界数值格式错误");
//            }
            
            // 6. 构建请求URL - 使用place/v3/search接口进行矩形区域搜索
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://api.map.baidu.com/place/v3/polygon?");
            urlBuilder.append("query=").append(encodeUrl(query));
            urlBuilder.append("&bounds=").append(bounds);
            urlBuilder.append("&ak=").append(ak);
            urlBuilder.append("&output=json");
            urlBuilder.append("&page_size=").append(pageSize);
            urlBuilder.append("&page_num=").append(pageNum);
            // 添加其他可选参数
            urlBuilder.append("&scope=2"); // 返回详细信息
            urlBuilder.append("&coord_type=3"); // 百度坐标
            if (StringUtils.isNotEmpty(type)) {
                urlBuilder.append("&type=").append(encodeUrl(type));
            }
            
            String url = urlBuilder.toString();
            
            // 7. 发送HTTP请求
            String response = sendHttpGetRequest(url);
            
            // 8. 解析响应结果
            Map<String, Object> result = parseBaiduMapResponse(response, pageNum, pageSize);
            
            // 9. 添加矩形区域搜索的额外信息
            result.put("search_type", "rectangular_area");
            result.put("bounds", bounds);
            
            return ResultBody.success(result);
            
        } catch (Exception e) {
            return ResultBody.error(-10002, "查询企业信息失败：" + e.getMessage());
        }
    }
    
    @Log("一键拓客查询企业-圆形区域-一次性返回所有结果")
    @PostMapping("/queryCompanyYjtkAroundAll")
    @ApiOperation(value = "一键拓客查询企业-圆形区域-一次性返回所有结果", notes = "通过百度地图API查询企业信息-圆形区域搜索，一次性返回所有结果")
    public ResultBody queryCompanyYjtkAroundAll(@RequestBody Map<String, Object> paramMap) {
        try {
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
            paramMap.put("UserName", userName);
            paramMap.put("UserId", userId);

            // 3. 获取查询参数
            String query = (String) paramMap.get("query"); // 查询关键词
            String location = (String) paramMap.get("location"); // 中心点坐标 "lng,lat"
            String radius = (String) paramMap.get("radius"); // 搜索半径(米)
            String ak = "krHOOUhCZ23jxq5GdKKYuDTve6EaFy52"; // 百度地图API密钥
            
            // 4. 参数校验
            if (StringUtils.isEmpty(query)) {
                return ResultBody.error(-10001, "查询关键词不能为空");
            }
            if (StringUtils.isEmpty(location)) {
                return ResultBody.error(-10003, "中心点坐标不能为空，格式：lng,lat");
            }
            
            // 验证坐标格式
            String[] coords = location.split(",");
            if (coords.length != 2) {
                return ResultBody.error(-10004, "中心点坐标格式错误，应为：lng,lat");
            }
            
            try {
                Double.parseDouble(coords[0]); // 经度
                Double.parseDouble(coords[1]); // 纬度
            } catch (NumberFormatException e) {
                return ResultBody.error(-10005, "中心点坐标数值格式错误");
            }
            
            if (StringUtils.isEmpty(radius)) {
                radius = "1000"; // 默认1000米
            }
            
            // 5. 先查询第一页获取总记录数
            Map<String, Object> firstPageResult = queryBaiduMapPage(query, location, radius, ak, 0, 20);
            
            if (firstPageResult == null || !Integer.valueOf(0).equals(firstPageResult.get("status"))) {
                return ResultBody.error(-10006, "百度地图API查询失败：" + (firstPageResult != null ? firstPageResult.get("message") : "未知错误"));
            }
            
            // 6. 获取总记录数
            Integer total = (Integer) firstPageResult.get("total");
            if (total == null || total <= 0) {
                return ResultBody.success(firstPageResult);
            }
            
            // 7. 计算需要查询的页数（百度最多150条，每页20条，最多8页）
            int maxPages = Math.min((total + 19) / 20, 8); // 向上取整，但不超过8页
            List<Map<String, Object>> allResults = new ArrayList<>();
            
            // 8. 添加第一页结果
            List<Map<String, Object>> firstPageData = (List<Map<String, Object>>) firstPageResult.get("companies");
            if (firstPageData != null) {
                allResults.addAll(firstPageData);
            }
            
            // 9. 查询剩余页数
            for (int pageNum = 1; pageNum < maxPages; pageNum++) {
                Map<String, Object> pageResult = queryBaiduMapPage(query, location, radius, ak, pageNum, 20);
                if (pageResult != null && Integer.valueOf(0).equals(pageResult.get("status"))) {
                    List<Map<String, Object>> pageData = (List<Map<String, Object>>) pageResult.get("companies");
                    if (pageData != null) {
                        allResults.addAll(pageData);
                    }
                }
                
                // 添加延迟避免请求过快
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // 10. 构建最终返回结果
            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("status", "0");
            finalResult.put("message", "success");
            finalResult.put("total", allResults.size());
            finalResult.put("results", allResults);
            finalResult.put("search_type", "circular_area");
            finalResult.put("location", location);
            finalResult.put("radius", radius);
            finalResult.put("radius_unit", "meters");
            finalResult.put("pages_queried", maxPages);
            finalResult.put("max_records_limit", "150");
            
            return ResultBody.success(finalResult);
            
        } catch (Exception e) {
            return ResultBody.error(-10002, "查询企业信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询百度地图指定页数据
     */
    private Map<String, Object> queryBaiduMapPage(String query, String location, String radius, String ak, int pageNum, int pageSize) {
        try {
            // 构建请求URL
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://api.map.baidu.com/place/v3/around?");
            urlBuilder.append("query=").append(encodeUrl(query));
            urlBuilder.append("&location=").append(location);
            urlBuilder.append("&radius=").append(radius);
            urlBuilder.append("&ak=").append(ak);
            urlBuilder.append("&output=json");
            urlBuilder.append("&page_size=").append(pageSize);
            urlBuilder.append("&page_num=").append(pageNum);
            urlBuilder.append("&scope=2"); // 返回详细信息
            urlBuilder.append("&coord_type=3"); // 百度坐标
            urlBuilder.append("&radius_limit=true"); // 严格限制在半径范围内
            
            String url = urlBuilder.toString();
            
            // 发送HTTP请求
            String response = sendHttpGetRequest(url);
            
            // 解析响应结果
            return parseBaiduMapResponse(response, pageNum, pageSize);
            
        } catch (Exception e) {
            //log.error("查询百度地图第{}页失败", pageNum, e);
            return null;
        }
    }
    
    @Log("一键拓客查询企业-圆形区域-导出Excel")
    @PostMapping("/exportCompanyYjtkAroundAll")
    @ApiOperation(value = "一键拓客查询企业-圆形区域-导出Excel", notes = "通过百度地图API查询企业信息并导出Excel")
    public void exportCompanyYjtkAroundAll(@RequestBody Map<String, Object> paramMap, HttpServletResponse response) {
        try {
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
            paramMap.put("UserName", userName);
            paramMap.put("UserId", userId);

            // 3. 获取查询参数
            String query = (String) paramMap.get("query"); // 查询关键词
            String location = (String) paramMap.get("location"); // 中心点坐标 "lng,lat"
            String radius = (String) paramMap.get("radius"); // 搜索半径(米)
            String ak = "krHOOUhCZ23jxq5GdKKYuDTve6EaFy52"; // 百度地图API密钥
            
            // 4. 参数校验
            if (StringUtils.isEmpty(query)) {
                throw new IllegalArgumentException("查询关键词不能为空");
            }
            if (StringUtils.isEmpty(location)) {
                throw new IllegalArgumentException("中心点坐标不能为空，格式：lng,lat");
            }
            
            // 验证坐标格式
            String[] coords = location.split(",");
            if (coords.length != 2) {
                throw new IllegalArgumentException("中心点坐标格式错误，应为：lng,lat");
            }
            
            try {
                Double.parseDouble(coords[0]); // 经度
                Double.parseDouble(coords[1]); // 纬度
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("中心点坐标数值格式错误");
            }
            
            if (StringUtils.isEmpty(radius)) {
                radius = "1000"; // 默认1000米
            }
            
            // 5. 先查询第一页获取总记录数
            Map<String, Object> firstPageResult = queryBaiduMapPage(query, location, radius, ak, 0, 20);
            
            if (firstPageResult == null || !Integer.valueOf(0).equals(firstPageResult.get("status"))) {
                throw new RuntimeException("百度地图API查询失败：" + (firstPageResult != null ? firstPageResult.get("message") : "未知错误"));
            }
            
            // 6. 获取总记录数
            Integer total = (Integer) firstPageResult.get("total");
            if (total == null || total <= 0) {
                throw new RuntimeException("未查询到相关企业信息");
            }
            
            // 7. 计算需要查询的页数（百度最多150条，每页20条，最多8页）
            int maxPages = Math.min((total + 19) / 20, 8); // 向上取整，但不超过8页
            List<Map<String, Object>> allResults = new ArrayList<>();
            
            // 8. 添加第一页结果
            List<Map<String, Object>> firstPageData = (List<Map<String, Object>>) firstPageResult.get("companies");
            if (firstPageData != null) {
                allResults.addAll(firstPageData);
            }
            
            // 9. 查询剩余页数
            for (int pageNum = 1; pageNum < maxPages; pageNum++) {
                Map<String, Object> pageResult = queryBaiduMapPage(query, location, radius, ak, pageNum, 20);
                if (pageResult != null && Integer.valueOf(0).equals(pageResult.get("status"))) {
                    List<Map<String, Object>> pageData = (List<Map<String, Object>>) pageResult.get("companies");
                    if (pageData != null) {
                        allResults.addAll(pageData);
                    }
                }
                
                // 添加延迟避免请求过快
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // 10. 生成Excel文件
            generateCompanyExcel(allResults, query, location, radius, response);
            
        } catch (Exception e) {
            log.error("导出失败", e);
            try {
                // 如果还没有开始写入响应，则返回错误信息
                if (!response.isCommitted()) {
                    response.setContentType("text/html;charset=utf-8");
                    response.getWriter().write("导出失败：" + e.getMessage());
                }
            } catch (Exception ex) {
                log.error("无法返回错误信息", ex);
            }
        }
    }
    
    /**
     * 生成企业信息Excel文件
     */
    private void generateCompanyExcel(List<Map<String, Object>> companies, String query, String location, String radius, HttpServletResponse response) throws Exception {
        // 参数验证
        if (companies == null) {
            companies = new ArrayList<>();
        }
        if (query == null) {
            query = "";
        }
        if (location == null) {
            location = "";
        }
        if (radius == null) {
            radius = "";
        }
        
        log.info("开始生成Excel文件，企业数量：{}，查询关键词：{}，坐标：{}，半径：{}", companies.size(), query, location, radius);
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("企业查询结果_" + query + "_" + System.currentTimeMillis() + ".xlsx", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("企业信息");
        
        // 创建标题行样式
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 定义表头
        String[] headers = {
            "序号", "企业名称", "地址", "经度", "纬度", "省份", "城市", "区县", "街道", 
            "联系电话", "营业时间", "评分", "评论数", "图片数", "详情链接", "距离(米)", "标签", "类型"
        };
        
        // 创建标题行
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 填充数据
        for (int i = 0; i < companies.size(); i++) {
            Map<String, Object> company = companies.get(i);
            if (company == null) {
                log.warn("第{}行企业数据为null，跳过", i + 1);
                continue;
            }
            
            try {
                XSSFRow row = sheet.createRow(i + 1);
                
                int colIndex = 0;
                row.createCell(colIndex++).setCellValue(i + 1); // 序号
                row.createCell(colIndex++).setCellValue(getStringValue(company, "name")); // 企业名称
                row.createCell(colIndex++).setCellValue(getStringValue(company, "address")); // 地址
                row.createCell(colIndex++).setCellValue(getDoubleValue(company, "lng")); // 经度
                row.createCell(colIndex++).setCellValue(getDoubleValue(company, "lat")); // 纬度
                row.createCell(colIndex++).setCellValue(getStringValue(company, "province")); // 省份
                row.createCell(colIndex++).setCellValue(getStringValue(company, "city")); // 城市
                row.createCell(colIndex++).setCellValue(getStringValue(company, "area")); // 区县
                row.createCell(colIndex++).setCellValue(getStringValue(company, "town")); // 街道
                row.createCell(colIndex++).setCellValue(getStringValue(company, "telephone")); // 联系电话
                row.createCell(colIndex++).setCellValue(getStringValue(company, "shop_hours")); // 营业时间
                row.createCell(colIndex++).setCellValue(getDoubleValue(company, "overall_rating")); // 评分
                row.createCell(colIndex++).setCellValue(getIntegerValue(company, "comment_num")); // 评论数
                row.createCell(colIndex++).setCellValue(getIntegerValue(company, "image_num")); // 图片数
                row.createCell(colIndex++).setCellValue(getStringValue(company, "detail_url")); // 详情链接
                row.createCell(colIndex++).setCellValue(getIntegerValue(company, "distance")); // 距离
                row.createCell(colIndex++).setCellValue(getStringValue(company, "tag")); // 标签
                row.createCell(colIndex++).setCellValue(getStringValue(company, "type")); // 类型
            } catch (Exception e) {
                log.error("处理第{}行企业数据时出错：{}", i + 1, e.getMessage());
                // 继续处理下一行，不中断整个流程
                continue;
            }
        }
        
        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // 添加查询条件信息
        int startRow = Math.max(companies.size() + 2, 2); // 确保至少有2行
        XSSFRow infoRow1 = sheet.createRow(startRow);
        XSSFRow infoRow2 = sheet.createRow(startRow + 1);
        XSSFRow infoRow3 = sheet.createRow(startRow + 2);
        
        infoRow1.createCell(0).setCellValue("查询条件信息：");
        infoRow2.createCell(0).setCellValue("查询关键词：" + query);
        infoRow3.createCell(0).setCellValue("中心坐标：" + location + "，搜索半径：" + radius + "米");
        
        log.info("Excel文件生成完成，准备输出到响应流");
        
        // 输出Excel文件
        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
        workbook.close();
        
        log.info("Excel文件输出完成");
    }
    
    /**
     * 安全获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        if (map == null) return "";
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    /**
     * 安全获取整数值
     */
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        if (map == null) return 0;
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0; // 返回0而不是null
    }
    
    /**
     * 安全获取双精度值
     */
    private Double getDoubleValue(Map<String, Object> map, String key) {
        if (map == null) return 0.0;
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0; // 返回0.0而不是null
    }
}
