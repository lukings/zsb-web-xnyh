package cn.visolink.system.custMap.controller;

import cn.visolink.common.ClueStatusEnum;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.channel.dao.DataStatisticDao;
import cn.visolink.system.channel.model.vo.ProjectCluesNew;
import cn.visolink.system.companyQw.util.DateUtils;
import cn.visolink.system.custMap.bo.*;
import cn.visolink.system.custMap.dao.CustMapDao;
import cn.visolink.system.custMap.dao.ZsMapPermissionsDao;
import cn.visolink.system.custMap.service.ZsMapService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.homepage.service.WorkbenchService;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
public class CustMapController {

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

    /*
     * 获取园区
     * */
    @PostMapping("/getAllPark")
    public ResultBody getAllPark(@RequestBody Map paramMap){
        Map map = new HashMap();
        map.put("UserName", SecurityUtils.getUsername());
//        map.put("UserName", "wg-test");
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        paramMap.put("UserId",SecurityUtils.getUserId());
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(paramMap);
        //获取登陆人项目权限
        List<String> fullpath = projectMapper.findFullPathAllHasUser(map);
        List<ProjectVO> projectList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList)){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
        }else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            map.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(map);
        }
        if(!CollectionUtils.isEmpty(qyProjectList)){
            for(int i = 0;i<qyProjectList.size();i++){
                if(!projectList.contains(qyProjectList.get(i))){
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
//        if (projectList==null || projectList.size()==0){
//            throw new BadRequestException(-10_0000,"用户无项目权限！");
//        }
        String jobCode = paramMap.get("jobCode")+"";
        if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            projectList = new ArrayList<>();
            ProjectVO pro = new ProjectVO();
            pro.setProjectId(paramMap.get("projectId")+"");
            projectList.add(pro);
        }
        map.put("projectList",projectList);
        return ResultBody.success(custMapDao.getAllPark(map));
    }

    /*
     * 获取标签
     * */
    @PostMapping("/getAllLabel")
    public ResultBody getAllLabel(@RequestBody Map paramMap){
        Map map = new HashMap();
        map.put("UserName", SecurityUtils.getUsername());
//        map.put("UserName", "wg-test");
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        paramMap.put("UserId",SecurityUtils.getUserId());
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(paramMap);
        //获取登陆人项目权限
        List<String> fullpath = projectMapper.findFullPathAllHasUser(map);
        List<ProjectVO> projectList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList)){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
        }else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            map.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(map);
        }
        if(!CollectionUtils.isEmpty(qyProjectList)){
            for(int i = 0;i<qyProjectList.size();i++){
                if(!projectList.contains(qyProjectList.get(i))){
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
//        if (projectList==null || projectList.size()==0){
//            throw new BadRequestException(-10_0000,"用户无项目权限！");
//        }
        String jobCode = paramMap.get("jobCode")+"";
        if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            projectList = new ArrayList<>();
            ProjectVO pro = new ProjectVO();
            pro.setProjectId(paramMap.get("projectId")+"");
            projectList.add(pro);
        }
        map.put("projectList",projectList);
        map.put("userLabel",paramMap.get("userLabel").toString());
        return ResultBody.success(custMapDao.getAllLabel(map));
    }


    /*
     * 获取客户行业
     * */
    @GetMapping("/getCustIndustry")
    public ResultBody getCustIndustry(){
        List<Map> list = custMapDao.getCstIndustryOne();
        return ResultBody.success(list);
    }

    /**
     * @Author wanggang
     * @Description //获取招商地图
     * @Date 11:20 2022/8/30
     * @Param [city, tokerTime, type]
     * @return cn.visolink.exception.ResultBody
     **/
    @PostMapping("/zsMap")
    public ResultBody zsMap(@RequestBody Map paramMap){
        List<String> areaList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<List<String>> areaDataList = (List<List<String>>) paramMap.get("areaData");
        List<String> provinceList = (List<String>) paramMap.get("provinceArray");
        List<String> cityList = (List<String>) paramMap.get("cityArray");
        List<String> countList = (List<String>) paramMap.get("countArray");
        String customerName = (String) paramMap.get("customerName");
        String tagLabel = (String) paramMap.get("tagLabel");
      if (customerName != null && !"".equals(customerName)) {
          //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
          if (customerName.matches("[0-9]+")) {
              paramMap.put("customerMobile", customerName);
              paramMap.put("customerName", null);
          }
      }
        /*if (!CollectionUtils.isEmpty(provinceList)) {
            if (cityList == null)
                cityList = new ArrayList<>();
            if (provinceList.contains("北京市"))
                cityList.add("北京市");
            if (provinceList.contains("天津市"))
                cityList.add("天津市");
            if (provinceList.contains("上海市"))
                cityList.add("上海市");
            if (provinceList.contains("重庆市"))
                cityList.add("重庆市");
            areaList = provinceList;
        }
        if (!CollectionUtils.isEmpty(cityList)) {
            areaList = cityList;
        }
        if (!CollectionUtils.isEmpty(countList)) {
            areaList = countList;
        }*/
        if (!CollectionUtils.isEmpty(areaDataList)) {
            List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
                Stream.of(SecurityUtils.getUserId()).collect(Collectors.toList()));
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
                areaDataList = areaDataList.stream().filter(a-> dbAreaDataList.contains(a)).collect(Collectors.toList());
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
        paramMap.put("UserName", SecurityUtils.getUsername());
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        paramMap.put("UserId",SecurityUtils.getUserId());
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(paramMap);
        //查询地图授权
        List<String> fullpath = new ArrayList<>();
        List<ProjectVO> projectList = new ArrayList<>();

        //获取登陆人项目权限
        List<String> fullpath1 = projectMapper.findFullPathAllHasUser(paramMap);
        fullpath.addAll(fullpath1);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList)){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
        }else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            paramMap.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(paramMap);
        }
        if(!CollectionUtils.isEmpty(qyProjectList)){
            for(int i = 0;i<qyProjectList.size();i++){
                if(!projectList.contains(qyProjectList.get(i))){
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
//        if (projectList==null || projectList.size()==0){
//            throw new BadRequestException(-10_0000,"用户无项目权限！");
//        }
        String jobCode = paramMap.get("jobCode")+"";
        //获取人员最高权限-可以根据人员岗位的组织层级
        List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("zygw")){
                    if(level<= 1){
                        jobCode = "zygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyzygw")){
                    if(level<= 1){
                        jobCode = "qyzygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("xsjl")
                		||String.valueOf(userList.get(i).get("JobCode")).equals("yxjl")
                		||String.valueOf(userList.get(i).get("JobCode")).equals("zszj")){
                    if(level<= 2){
                        jobCode =String.valueOf(userList.get(i).get("JobCode"));
                        level = 2;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyxsjl")
                		||String.valueOf(userList.get(i).get("JobCode")).equals("qyyxjl")
                		||String.valueOf(userList.get(i).get("JobCode")).equals("qyzszj")){
                    if(level<= 2){
                    	jobCode =String.valueOf(userList.get(i).get("JobCode"));
                        level = 2;
                    }
                }else {
                    jobCode = userList.get(i).get("JobCode")+"";
                    level = 3;
                }
            }
        }
        if (paramMap.get("projectIds") != null) {
            List<String> projectIds = (List<String>) paramMap.get("projectIds");
            projectList = new ArrayList<>();
            for (String projectId : projectIds) {
                ProjectVO pro = new ProjectVO();
                pro.setProjectId(projectId);
                projectList.add(pro);
            }
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
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                    projectIds);
            paramMap.put("orgIds",orgIds);
        }

        //获取团队人员Id
        List<String> userIds = new ArrayList<>();
        if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode) 
            ||"yxjl".equals(jobCode) || "qyyxjl".equals(jobCode)
        	||"zszj".equals(jobCode) || "qyzszj".equals(jobCode)){
            String userId = SecurityUtils.getUserId();
            //获取用户此岗位下所有组织
            List<String> orgs = custMapDao.getUserOrgs(userId,jobCode);
            String userJobCode = "";
            if ("xsjl".equals(jobCode)){
                userJobCode = "zygw";
            }else{
                userJobCode = "qyzygw";
            }
            for (String orgId:orgs) {
                List<String> userIds1 = custMapDao.getTeamUser(orgId,userJobCode);
                userIds.addAll(userIds1);
            }
        }else if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            userIds.add(SecurityUtils.getUserId());
        }
        paramMap.put("userIds",userIds);
        Map map = new HashMap();
        String type = paramMap.get("type")+"";
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
                if(flagType.indexOf("2")!=-1){
                    isPublic = true;
                }
                if ("3".equals(flagType)) {
                	isMap = true;
                }
            }
            if (isSales){
                if (clueStatusList.contains("1") || clueStatusList.contains("2")
                    || clueStatusList.contains("3") || clueStatusList.contains("4")
                    || clueStatusList.contains("8")){
                    List<ZsMapResBO> opportunitys = custMapDao.getOpportunity(paramMap);
                    List<ZsMapResBO> reports = opportunitys.stream()
                        .filter(a -> "1".equals(a.getType())).collect(Collectors.toList());
                    List<ZsMapResBO> visits = opportunitys.stream()
                        .filter(a -> "2".equals(a.getType())).collect(Collectors.toList());
                    List<ZsMapResBO> threeOnes = opportunitys.stream()
                        .filter(a -> "3".equals(a.getType())).collect(Collectors.toList());
                    List<ZsMapResBO> sees = opportunitys.stream()
                        .filter(a -> "4".equals(a.getType())).collect(Collectors.toList());
                    List<ZsMapResBO> deals = opportunitys.stream()
                        .filter(a -> "8".equals(a.getType())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(reports) && reports.size() > 0 && clueStatusList.contains("1") ) {
                        str.append(ClueStatusEnum.getNameByStatus("1") + "的客户有"
                            + reports.size() + "组;");
                        zsMapResBOList.addAll(reports);
                    }
                    if (!CollectionUtils.isEmpty(visits) && visits.size() > 0 && clueStatusList.contains("2") ) {
                        str.append(ClueStatusEnum.getNameByStatus("2") + "的客户有"
                            + visits.size() + "组;");
                        zsMapResBOList.addAll(visits);
                    }
                    if (!CollectionUtils.isEmpty(threeOnes) && threeOnes.size() > 0 && clueStatusList.contains("3")) {
                        str.append(ClueStatusEnum.getNameByStatus("3") + "的客户有"
                            + threeOnes.size() + "组;");
                        zsMapResBOList.addAll(threeOnes);
                    }
                    if (!CollectionUtils.isEmpty(sees) && sees.size() > 0 && clueStatusList.contains("4") ) {
                        str.append(ClueStatusEnum.getNameByStatus("4") + "的客户有"
                            + sees.size() + "组;");
                        zsMapResBOList.addAll(sees);
                    }
                    if (!CollectionUtils.isEmpty(deals) && deals.size() > 0 && clueStatusList.contains("8") ) {
                        str.append(ClueStatusEnum.getNameByStatus("8") + "的客户有"
                            + deals.size() + "组;");
                        zsMapResBOList.addAll(deals);
                    }
                }
                if (clueStatusList.contains("6") || clueStatusList.contains("7")){
                    List<ZsMapResBO> clues = custMapDao.getClues(paramMap);
                    List<ZsMapResBO> clue6 = clues.stream()
                        .filter(a -> "6".equals(a.getType())).collect(Collectors.toList());
                    List<ZsMapResBO> errs = clues.stream()
                        .filter(a -> "7".equals(a.getType())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(clue6) && clue6.size() > 0 && clueStatusList.contains("6") ) {
                        str.append(ClueStatusEnum.getNameByStatus("6") + "的客户有"
                            + clue6.size() + "组;");
                        zsMapResBOList.addAll(clue6);
                    }
                    if (!CollectionUtils.isEmpty(errs) && errs.size() > 0 && clueStatusList.contains("7") ) {
                        str.append(ClueStatusEnum.getNameByStatus("7") + "的客户有"
                            + errs.size() + "组;");
                        zsMapResBOList.addAll(errs);
                    }
                }
            }
            if (isPublic){
            	//改造后（管理者）0-项目全部客户；1-在保客户；21-项目公客池；22-区域公客池客户；23-全国公客池客户 3-地图导入客户 
            	paramMap.put("poolType",flagType.substring(1));
                List<ZsMapResBO> publicPools = custMapDao.getPublicPool(paramMap);
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
            	 List<ZsMapResBO> clues = custMapDao.getClues(paramMap);
                 List<ZsMapResBO> clueMap = clues.stream()
                     .filter(a -> "招商地图导入".equals(a.getSourceMode())).collect(Collectors.toList());

                 if (!CollectionUtils.isEmpty(clueMap) && clueMap.size() > 0) {
                     zsMapResBOList.addAll(clueMap);
                 }
            }
        }
        result.put("zsMapResBOList", zsMapResBOList);
        result.put("message", str.toString());
        return ResultBody.success(result);
    }

    private List<String> initClueStatus(String clueStatus, String taskName, String flagType) {
        List<String> clueStatusList = Stream.of("1","2","3","4","6","7","8").collect(Collectors.toList());
        //线索客户和公共池都不查询
        if (StringUtils.isNotBlank(taskName)) {
            clueStatusList = Stream.of("1","2","3","4","8").collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(flagType)){
            if ("1".equals(flagType)){//原拉式在保-
                clueStatusList = Stream.of("1","2","3","4","8").collect(Collectors.toList());
            }
        }

        if (StringUtils.isNotBlank(clueStatus)) {
            clueStatusList = Stream.of(clueStatus).collect(Collectors.toList());
        }
        return clueStatusList;
    }

    /**
     * @Author wanggang
     * @Description //获取热力图
     * @Date 11:20 2022/8/30
     * @Param [city, tokerTime, type]
     * @return cn.visolink.exception.ResultBody
     **/
    @PostMapping("/zsHeatMap")
    public ResultBody zsHeatMap(@RequestBody Map paramMap){
        paramMap.put("UserName", SecurityUtils.getUsername());
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        paramMap.put("UserId",SecurityUtils.getUserId());
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(paramMap);
        //获取登陆人项目权限
        List<String> fullpath = projectMapper.findFullPathAllHasUser(paramMap);
        List<ProjectVO> projectList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList)){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
        }else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            paramMap.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(paramMap);
        }
        if(!CollectionUtils.isEmpty(qyProjectList)){
            for(int i = 0;i<qyProjectList.size();i++){
                if(!projectList.contains(qyProjectList.get(i))){
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
//        if (projectList==null || projectList.size()==0){
//            throw new BadRequestException(-10_0000,"用户无项目权限！");
//        }
        String jobCode = paramMap.get("jobCode")+"";
        //获取人员最高权限
        List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("zygw")){
                    if(level<= 1){
                        jobCode = "zygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyzygw")){
                    if(level<= 1){
                        jobCode = "qyzygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("xsjl")){
                    if(level<= 2){
                        jobCode = "xsjl";
                        level = 2;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyxsjl")){
                    if(level<= 2){
                        jobCode = "qyxsjl";
                        level = 2;
                    }
                }else {
                    jobCode = userList.get(i).get("JobCode")+"";
                    level = 3;
                }
            }
        }
        if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            projectList = new ArrayList<>();
            ProjectVO pro = new ProjectVO();
            pro.setProjectId(paramMap.get("projectId")+"");
            projectList.add(pro);
        }
        paramMap.put("projectList",projectList);
        //获取团队人员Id
        List<String> userIds = new ArrayList<>();
        if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)){
            String userId = SecurityUtils.getUserId();
            //获取用户此岗位下所有组织
            List<String> orgs = custMapDao.getUserOrgs(userId,jobCode);
            String userJobCode = "";
            if ("xsjl".equals(jobCode)){
                userJobCode = "zygw";
            }else{
                userJobCode = "qyzygw";
            }
            for (String orgId:orgs) {
                List<String> userIds1 = custMapDao.getTeamUser(orgId,userJobCode);
                userIds.addAll(userIds1);
            }
        }else if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            userIds.add(SecurityUtils.getUserId());
        }
        paramMap.put("userIds",userIds);
        Map map = new HashMap();
        String type = paramMap.get("type")+"";
        if ("1".equals(type)){
            map.put("tokerNum",custMapDao.tokerNumNewHeat(paramMap));
        }else if ("2".equals(type)){
            map.put("reportNum",custMapDao.reportNumHeat(paramMap));
        }else if ("3".equals(type)){
            map.put("visitNum",custMapDao.visitNumHeat(paramMap));
        }else if ("4".equals(type)){
            map.put("signNum",custMapDao.signNumNewHeat(paramMap));
        }else {
            map.put("tokerNum", custMapDao.tokerNumNewHeat(paramMap));
            map.put("reportNum", custMapDao.reportNumHeat(paramMap));
            map.put("visitNum", custMapDao.visitNumHeat(paramMap));
            map.put("signNum", custMapDao.signNumNewHeat(paramMap));
        }
        return ResultBody.success(map);
    }


    /**
     * @Author wanggang
     * @Description //获取热力图
     * @Date 11:20 2022/8/30
     * @Param [city, tokerTime, type]
     * @return cn.visolink.exception.ResultBody
     **/
    @PostMapping("/zsHeatMapT")
    public ResultBody zsHeatMapT(@RequestBody Map paramMap){
        paramMap.put("UserName", SecurityUtils.getUsername());
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        paramMap.put("UserId",SecurityUtils.getUserId());
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(paramMap);
        //获取登陆人项目权限
        List<String> fullpath = projectMapper.findFullPathAllHasUser(paramMap);
        List<ProjectVO> projectList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList)){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
        }else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            paramMap.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(paramMap);
        }
        if(!CollectionUtils.isEmpty(qyProjectList)){
            for(int i = 0;i<qyProjectList.size();i++){
                if(!projectList.contains(qyProjectList.get(i))){
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
//        if (projectList==null || projectList.size()==0){
//            throw new BadRequestException(-10_0000,"用户无项目权限！");
//        }
        String jobCode = paramMap.get("jobCode")+"";
        //获取人员最高权限
        List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("zygw")){
                    if(level<= 1){
                        jobCode = "zygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyzygw")){
                    if(level<= 1){
                        jobCode = "qyzygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("xsjl")){
                    if(level<= 2){
                        jobCode = "xsjl";
                        level = 2;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyxsjl")){
                    if(level<= 2){
                        jobCode = "qyxsjl";
                        level = 2;
                    }
                }else {
                    jobCode = userList.get(i).get("JobCode")+"";
                    level = 3;
                }
            }
        }
        if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            projectList = new ArrayList<>();
            ProjectVO pro = new ProjectVO();
            pro.setProjectId(paramMap.get("projectId")+"");
            projectList.add(pro);
        }
        paramMap.put("projectList",projectList);
        //获取团队人员Id
        List<String> userIds = new ArrayList<>();
        if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)){
            String userId = SecurityUtils.getUserId();
            //获取用户此岗位下所有组织
            List<String> orgs = custMapDao.getUserOrgs(userId,jobCode);
            String userJobCode = "";
            if ("xsjl".equals(jobCode)){
                userJobCode = "zygw";
            }else{
                userJobCode = "qyzygw";
            }
            for (String orgId:orgs) {
                List<String> userIds1 = custMapDao.getTeamUser(orgId,userJobCode);
                userIds.addAll(userIds1);
            }
        }else if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            userIds.add(SecurityUtils.getUserId());
        }
        paramMap.put("userIds",userIds);
        Map map = new HashMap();
        String type = paramMap.get("type")+"";
        if ("1".equals(type)){
            map.put("tokerNum",custMapDao.tokerNumNewHeat(paramMap));
        }else if ("2".equals(type)){
            map.put("reportNum",custMapDao.reportNumHeat(paramMap));
        }else if ("3".equals(type)){
            map.put("visitNum",custMapDao.visitNumHeat(paramMap));
        }else if ("4".equals(type)){
            map.put("signNum",custMapDao.signNumNewHeat(paramMap));
        }else {
            map.put("tokerNum", custMapDao.tokerNumNewHeat(paramMap));
            map.put("reportNum", custMapDao.reportNumHeat(paramMap));
            map.put("visitNum", custMapDao.visitNumHeat(paramMap));
            map.put("signNum", custMapDao.signNumNewHeat(paramMap));
        }
        return ResultBody.success(map);
    }

    @Log("招商地图导出")
    @ApiOperation(value = "招商地图导出", notes = "招商地图导出")
    @RequestMapping(value = "/zsMapExport")
    public void zsMapExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        List<List<String>> areaDataList = (List<List<String>>) paramMap.get("areaData");
        List<String> areaList = new ArrayList<>();
        List<String> provinceList = (List<String>) paramMap.get("provinceArray");
        List<String> cityList = (List<String>) paramMap.get("cityArray");
        List<String> countList = (List<String>) paramMap.get("countArray");
        String customerName = (String) paramMap.get("customerName");
        String tagLabel = (String) paramMap.get("tagLabel");
        if (customerName != null && !"".equals(customerName)) {
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (customerName.matches("[0-9]+")) {
                paramMap.put("customerMobile", customerName);
                paramMap.put("customerName", null);
            }
        }
        /*if (!CollectionUtils.isEmpty(provinceList)) {
            if (cityList == null)
                cityList = new ArrayList<>();
            if (provinceList.contains("北京市"))
                cityList.add("北京市");
            if (provinceList.contains("天津市"))
                cityList.add("天津市");
            if (provinceList.contains("上海市"))
                cityList.add("上海市");
            if (provinceList.contains("重庆市"))
                cityList.add("重庆市");
            areaList = provinceList;
        }
        if (!CollectionUtils.isEmpty(cityList)) {
            areaList = cityList;
        }
        if (!CollectionUtils.isEmpty(countList)) {
            areaList = countList;
        }*/
        if (!CollectionUtils.isEmpty(areaDataList)) {
            List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
                Stream.of(SecurityUtils.getUserId()).collect(Collectors.toList()));
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
                areaDataList = areaDataList.stream().filter(a-> dbAreaDataList.contains(a)).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(areaDataList) || areaDataList.size() == 0) {
                    return;
                }
                areaDataList.forEach(areaData -> {
                    String string = StringUtils.join(areaData, "");
                    string = string.replaceAll(",", "");
                    areaList.add(string);
                });
                paramMap.put("areaList", areaList);
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
        paramMap.put("UserName", SecurityUtils.getUsername());
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        paramMap.put("UserId",SecurityUtils.getUserId());
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(paramMap);
        //查询地图授权
        List<String> fullpath = new ArrayList<>();
        List<ProjectVO> projectList = new ArrayList<>();
        //获取登陆人项目权限
        List<String> fullpath1 = projectMapper.findFullPathAllHasUser(paramMap);
        fullpath.addAll(fullpath1);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList)){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
        }else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            paramMap.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(paramMap);
        }
        if(!CollectionUtils.isEmpty(qyProjectList)){
            for(int i = 0;i<qyProjectList.size();i++){
                if(!projectList.contains(qyProjectList.get(i))){
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
//        if (projectList==null || projectList.size()==0){
//            throw new BadRequestException(-10_0000,"用户无项目权限！");
//        }
        String jobCode = paramMap.get("jobCode")+"";
        //获取人员最高权限
        List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("zygw")){
                    if(level<= 1){
                        jobCode = "zygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyzygw")){
                    if(level<= 1){
                        jobCode = "qyzygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("xsjl")){
                    if(level<= 2){
                        jobCode = "xsjl";
                        level = 2;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyxsjl")){
                    if(level<= 2){
                        jobCode = "qyxsjl";
                        level = 2;
                    }
                }else {
                    jobCode = userList.get(i).get("JobCode")+"";
                    level = 3;
                }
            }
        }
        if (paramMap.get("projectIds") != null) {
            List<String> projectIds = (List<String>) paramMap.get("projectIds");
            projectList = new ArrayList<>();
            for (String projectId : projectIds) {
                ProjectVO pro = new ProjectVO();
                pro.setProjectId(projectId);
                projectList.add(pro);
            }
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
        }

        //获取团队人员Id
        List<String> userIds = new ArrayList<>();
        if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)){
            String userId = SecurityUtils.getUserId();
            //获取用户此岗位下所有组织
            List<String> orgs = custMapDao.getUserOrgs(userId,jobCode);
            String userJobCode = "";
            if ("xsjl".equals(jobCode)){
                userJobCode = "zygw";
            }else{
                userJobCode = "qyzygw";
            }
            for (String orgId:orgs) {
                List<String> userIds1 = custMapDao.getTeamUser(orgId,userJobCode);
                userIds.addAll(userIds1);
            }
        }else if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            userIds.add(SecurityUtils.getUserId());
        }
        paramMap.put("userIds",userIds);
        String type = paramMap.get("type")+"";
        //导出的文档下面的名字
        String excelName = "招商地图";
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

        List<ProjectCluesNew> projectCluesNewList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(clueStatusList)) {
            boolean isPublic = false;
            boolean isSales = false;
            boolean isMap = false;
            //flagType 
            //原始0-全部；1-在保；2-公客池
            //改造后（管理者）0-项目全部客户；1-在保客户；21-项目公客池；22-区域公客池客户；23-全国公客池客户 3-地图导入客户 
            //改造后（专员）  0-项目全部客户；1-线索客户（自己录入的+公客池分配的+任务下发的客户）；21-项目公客池客户；22区域公客池客户；23全国公客池客户 
            if (StringUtils.isEmpty(flagType) || "0".equals(flagType)) {
                isPublic = true;
                isSales = true;
            } else {
                if ("1".equals(flagType)) {
                    isSales = true;
                }
                if(flagType.indexOf("2")>0) {
                    isPublic = true;
                }
                if ("3".equals(flagType)) {
                	isMap = true;
                }
            }
            if(isSales) {
                if (clueStatusList.contains("1") || clueStatusList.contains("2")
                    || clueStatusList.contains("3") || clueStatusList.contains("4")
                    || clueStatusList.contains("8")) {
                    List<ProjectCluesNew> opportunityCustomers = custMapDao.getOpportunityCustomer(
                        paramMap);
                    List<ProjectCluesNew> reports = opportunityCustomers.stream()
                        .filter(a -> "1".equals(a.getType())).collect(Collectors.toList());
                    List<ProjectCluesNew> visits = opportunityCustomers.stream()
                        .filter(a -> "2".equals(a.getType())).collect(Collectors.toList());
                    List<ProjectCluesNew> threeOnes = opportunityCustomers.stream()
                        .filter(a -> "3".equals(a.getType())).collect(Collectors.toList());
                    List<ProjectCluesNew> sees = opportunityCustomers.stream()
                        .filter(a -> "4".equals(a.getType())).collect(Collectors.toList());
                    List<ProjectCluesNew> deals = opportunityCustomers.stream()
                        .filter(a -> "8".equals(a.getType())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(reports) && reports.size() > 0) {
                        projectCluesNewList.addAll(reports);
                    }
                    if (!CollectionUtils.isEmpty(visits) && visits.size() > 0) {
                        projectCluesNewList.addAll(visits);
                    }
                    if (!CollectionUtils.isEmpty(threeOnes) && threeOnes.size() > 0) {
                        projectCluesNewList.addAll(threeOnes);
                    }
                    if (!CollectionUtils.isEmpty(sees) && sees.size() > 0) {
                        projectCluesNewList.addAll(sees);
                    }
                    if (!CollectionUtils.isEmpty(deals) && deals.size() > 0) {
                        projectCluesNewList.addAll(deals);
                    }
                }
                if (clueStatusList.contains("6") || clueStatusList.contains("7")){
                    List<ProjectCluesNew> clues = custMapDao.getCluesCustomer(paramMap);
                    List<ProjectCluesNew> clue6 = clues.stream()
                        .filter(a -> "6".equals(a.getType())).collect(Collectors.toList());
                    List<ProjectCluesNew> errs = clues.stream()
                        .filter(a -> "7".equals(a.getType())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(clue6) && clue6.size() > 0) {
                        projectCluesNewList.addAll(clue6);
                    }
                    if (!CollectionUtils.isEmpty(errs) && errs.size() > 0) {
                        projectCluesNewList.addAll(errs);
                    }
                }
            }
            if (isPublic) {
                //公共池
                projectCluesNewList.addAll(custMapDao.getPublicCustomer(paramMap));
            }
            //新增-招商地图导入
            if (isMap) {
                List<ProjectCluesNew> clues = custMapDao.getCluesCustomer(paramMap);
                List<ProjectCluesNew> clueMap = clues.stream()
                    .filter(a -> "招商地图导入".equals(a.getSourceMode())).collect(Collectors.toList());
               
                if (!CollectionUtils.isEmpty(clueMap) && clueMap.size() > 0) {
                    projectCluesNewList.addAll(clueMap);
                }
            }
        }
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setCreator(SecurityUtils.getUserId());
        if (!CollectionUtils.isEmpty(projectList)) {
            Map proMap = custMapDao.getAreaNameAndProNames(projectList);
            //获取项目集合数据（事业部，项目Id,项目名称）
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(SecurityUtils.getUserId());
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (projectCluesNewList != null && projectCluesNewList.size() > 0){
            String isAllStr = paramMap.get("isAll") + "";
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
            if ("6".equals(clueStatus) || "7".equals(clueStatus)){
                headers = projectCluesNewList.get(0).courtCaseTitle1;
                int rowNum = 1;
                for (ProjectCluesNew model : projectCluesNewList) {
                    model.setRownum(rowNum);
                    Object[] oArray = model.toOldData1(isAll);
                    dataset.add(oArray);
                    rowNum++;
                }
            }else {
                headers = projectCluesNewList.get(0).courtCaseTitle2;
                int rowNum = 1;
                for (ProjectCluesNew model : projectCluesNewList) {
                    model.setRownum(rowNum);
                    Object[] oArray = model.toOldData2(isAll);
                    dataset.add(oArray);
                    rowNum++;
                }
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Log("招商地图导出")
    @ApiOperation(value = "招商地图导出", notes = "招商地图导出")
    @RequestMapping(value = "/zsMapExportNew")
    public String zsMapExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        String userId = SecurityUtils.getUserId();
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        List<String> areaList = new ArrayList<>();
        List<String> provinceList = (List<String>) paramMap.get("provinceArray");
        List<String> cityList = (List<String>) paramMap.get("cityArray");
        List<String> countList = (List<String>) paramMap.get("countArray");
        if (!CollectionUtils.isEmpty(provinceList)) {
            if (cityList == null)
                cityList = new ArrayList<>();
            if (provinceList.contains("北京市"))
                cityList.add("北京市");
            if (provinceList.contains("天津市"))
                cityList.add("天津市");
            if (provinceList.contains("上海市"))
                cityList.add("上海市");
            if (provinceList.contains("重庆市"))
                cityList.add("重庆市");
            areaList = provinceList;
        }
        if (!CollectionUtils.isEmpty(cityList)) {
            areaList = cityList;
        }
        if (!CollectionUtils.isEmpty(countList)) {
            areaList = countList;
        }
        List<String> dbAreas = new ArrayList<>();
        List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(Stream.of(SecurityUtils.getUserId()).collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(mapPermissions)) {
            for (ZsMapPermissionsBO zsMapPermissionsBO : mapPermissions) {
                if (StringUtils.isNotBlank(zsMapPermissionsBO.getAreaPermissions())
                    && zsMapPermissionsBO.getAreaEndDate().compareTo(DateUtils.getDateAfterDays(new Date(), -1)) > 0) {
                    List<String> areaPermissionsList = JSON.parseArray(zsMapPermissionsBO.getAreaPermissions(), String.class);
                    for (String areaPerm : areaPermissionsList) {
                        List<String> strings = JSON.parseArray(areaPerm, String.class);
                        dbAreas.add(strings.get(strings.size() - 1));
                    }
                }
            }
        }
        areaList = areaList.stream().filter(a-> dbAreas.contains(a)).collect(Collectors.toList());
        paramMap.put("areaList", areaList);
        paramMap.put("UserName", SecurityUtils.getUsername());
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        paramMap.put("UserId",SecurityUtils.getUserId());
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(paramMap);
        //获取登陆人项目权限
        List<String> fullpath = projectMapper.findFullPathAllHasUser(paramMap);
        List<ProjectVO> projectList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList)){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
        }else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            paramMap.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(paramMap);
        }
        if(!CollectionUtils.isEmpty(qyProjectList)){
            for(int i = 0;i<qyProjectList.size();i++){
                if(!projectList.contains(qyProjectList.get(i))){
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
        String jobCode = paramMap.get("jobCode")+"";
        //获取人员最高权限
        List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("zygw")){
                    if(level<= 1){
                        jobCode = "zygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyzygw")){
                    if(level<= 1){
                        jobCode = "qyzygw";
                        level = 1;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("xsjl")){
                    if(level<= 2){
                        jobCode = "xsjl";
                        level = 2;
                    }
                }else if (String.valueOf(userList.get(i).get("JobCode")).equals("qyxsjl")){
                    if(level<= 2){
                        jobCode = "qyxsjl";
                        level = 2;
                    }
                }else {
                    jobCode = userList.get(i).get("JobCode")+"";
                    level = 3;
                }
            }
        }
        if (paramMap.get("projectIds") != null) {
            List<String> projectIds = (List<String>) paramMap.get("projectIds");
            projectList = new ArrayList<>();
            for (String projectId : projectIds) {
                ProjectVO pro = new ProjectVO();
                pro.setProjectId(projectId);
                projectList.add(pro);
            }
        }
        if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            projectList = new ArrayList<>();
            ProjectVO pro = new ProjectVO();
            pro.setProjectId(paramMap.get("projectId")+"");
            projectList.add(pro);
        }
        paramMap.put("projectList",projectList);

        //获取团队人员Id
        List<String> userIds = new ArrayList<>();
        if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)){
            //获取用户此岗位下所有组织
            List<String> orgs = custMapDao.getUserOrgs(userId,jobCode);
            String userJobCode = "";
            if ("xsjl".equals(jobCode)){
                userJobCode = "zygw";
            }else{
                userJobCode = "qyzygw";
            }
            for (String orgId:orgs) {
                List<String> userIds1 = custMapDao.getTeamUser(orgId,userJobCode);
                userIds.addAll(userIds1);
            }
        }else if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)){
            userIds.add(SecurityUtils.getUserId());
        }
        paramMap.put("userIds",userIds);
        String type = paramMap.get("type")+"";
        //导出的文档下面的名字
        String excelName = "招商地图";
        if ("1".equals(type)){
            excelName = "招商地图-走访客户";
        }else if ("2".equals(type)){
            excelName = "招商地图-报备客户";
        }else if ("3".equals(type)){
            excelName = "招商地图-到访客户";
        }else if ("4".equals(type)){
            excelName = "招商地图-成交客户";
        }else {
            excelName = "招商地图";
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("ZM1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType(String.valueOf(paramMap.get("isAll")));//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(projectList)) {
            Map proMap = custMapDao.getAreaNameAndProNames(projectList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(paramMap));
        try{
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
            //放入redis
            redisUtil.lPush("downLoad",id);
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "任务创建发生异常！";
        }
        return "下载任务创建成功，请关注右上角下载任务状态";
    }

//    @Log("招商地图导出")
//    @ApiOperation(value = "招商地图导出", notes = "招商地图导出")
//    @RequestMapping(value = "/zsMapExport")
//    public void zsMapExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
//        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
//        String type = paramMap.get("type")+"";
//        if("0".equals(type)){
//            return;
//        }
//        List<String> areaList = new ArrayList<>();
//        List<String> provinceList = (List<String>) paramMap.get("provinceArray");
//        List<String> cityList = (List<String>) paramMap.get("cityArray");
//        List<String> countList = (List<String>) paramMap.get("countArray");
//        if (!CollectionUtils.isEmpty(provinceList)) {
//            if (cityList == null)
//                cityList = new ArrayList<>();
//            if (provinceList.contains("北京市"))
//                cityList.add("北京市");
//            if (provinceList.contains("天津市"))
//                cityList.add("天津市");
//            if (provinceList.contains("上海市"))
//                cityList.add("上海市");
//            if (provinceList.contains("重庆市"))
//                cityList.add("重庆市");
//            areaList = provinceList;
//        }
//        if (!CollectionUtils.isEmpty(cityList)) {
//            areaList = cityList;
//        }
//        if (!CollectionUtils.isEmpty(countList)) {
//            areaList = countList;
//        }
//        paramMap.put("areaList", areaList);
//        String userId = SecurityUtils.getUserId();
//        paramMap.put("UserName",SecurityUtils.getUsername());
//        //集团维度 1 个人 2 项目 3 区域 4 集团
//        String groupLatType = paramMap.get("groupLatType")+"";
//        List<ProjectVO> projectList = new ArrayList<>();
//        List<String> userIds = new ArrayList<>();
//        if("1".equals(groupLatType)){
//            userIds.add(userId);
//        }else if("2".equals(groupLatType)){
//            List<String> fullpath = projectMapper.findFullPath(paramMap);
//            StringBuffer sb = new StringBuffer();
//            if (fullpath==null || fullpath.size()==0){
//                throw new BadRequestException(-10_0000,"用户无项目权限！");
//            }
//            for (int i = 0; i < fullpath.size(); i++) {
//                if (i==0){
//                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
//                }else{
//                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
//                }
//            }
//            paramMap.put("where", sb.toString());
//            projectList = projectMapper.getProjectListByUserName(paramMap);
//            projectList.addAll(projectList);
//        }else if("3".equals(groupLatType)){
//            List<String> fullpath = projectMapper.findFullPath(paramMap);
//            StringBuffer sb = new StringBuffer();
//            if (fullpath==null || fullpath.size()==0){
//                throw new BadRequestException(-10_0000,"用户无区域权限！");
//            }
//            for (int i = 0; i < fullpath.size(); i++) {
//                if (i==0){
//                    sb.append(" and org.FullPath LIKE '"+fullpath.get(i)+"%'");
//                }else{
//                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
//                }
//            }
//            paramMap.put("where", sb.toString());
//            List<ProjectVO> regionList = custMapDao.getProList(paramMap);
//            projectList.addAll(regionList);
//        }else if("4".equals(groupLatType)){
//            paramMap.put("JobCode","10001");
//            projectList = projectMapper.getProjectListByUserName(paramMap);
//            projectList.addAll(projectList);
//        }else {
//            return;
//        }
//        paramMap.put("userIds",userIds);
//        paramMap.put("projectList",projectList);
//        //导出的文档下面的名字
//        String excelName = "招商地图";
//        List<ProjectCluesNew> projectCluesNewList = new ArrayList<>();
//        if ("1".equals(type)){
//            excelName = "招商地图-走访客户";
//            projectCluesNewList = custMapDao.getChannelTokerCustomer(paramMap);
//        }else if ("2".equals(type)){
//            excelName = "招商地图-报备客户";
//            projectCluesNewList = custMapDao.getSalesReportCustomer(paramMap);
//        }else if ("3".equals(type)){
//            excelName = "招商地图-到访客户";
//            projectCluesNewList = custMapDao.getSalesVisitCustomer(paramMap);
//        }else if ("4".equals(type)){
//            excelName = "招商地图-成交客户";
//            projectCluesNewList = custMapDao.getSalesSignCustomer(paramMap);
//        }
//        ArrayList<Object[]> dataset = new ArrayList<>();
//        String[] headers = null;
//        //保存导出日志
//        ExcelExportLog excelExportLog = new ExcelExportLog();
//        String id = UUID.randomUUID().toString();
//        excelExportLog.setId(id);
//        excelExportLog.setMainTypeDesc(excelName);
//        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
//        excelExportLog.setCreator(userId);
//        if (!CollectionUtils.isEmpty(projectList)) {
//            Map proMap = custMapDao.getAreaNameAndProNames(projectList);
//            //获取项目集合数据（事业部，项目Id,项目名称）
//            excelExportLog.setAreaName(proMap.get("areaName")+"");
//            excelExportLog.setProjectId(proMap.get("projectId")+"");
//            excelExportLog.setProjectName(proMap.get("projectName")+"");
//        }
//        excelExportLog.setCreator(userId);
//        excelExportLog.setExportStatus("2");
//        excelExportLog.setDoSql(excelForm);
//        //保存任务表
//        excelImportMapper.addExcelExportLog(excelExportLog);
//        if (projectCluesNewList != null && projectCluesNewList.size() > 0){
////            String isAllStr = paramMap.get("isAll") + "";
//            boolean isAll = true;
////            if ("1".equals(isAllStr)) isAll = false;
//            if ("1".equals(type)){
//                headers = projectCluesNewList.get(0).courtCaseTitle1;
//                int rowNum = 1;
//                for (ProjectCluesNew model : projectCluesNewList) {
//                    model.setRownum(rowNum);
//                    Object[] oArray = model.toData1(isAll);
//                    dataset.add(oArray);
//                    rowNum++;
//                }
//            }else {
//                headers = projectCluesNewList.get(0).courtCaseTitle2;
//                int rowNum = 1;
//                for (ProjectCluesNew model : projectCluesNewList) {
//                    model.setRownum(rowNum);
//                    Object[] oArray = model.toData2(isAll);
//                    dataset.add(oArray);
//                    rowNum++;
//                }
//            }
//            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
//            try {
//                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Log("招商地图授权查询")
    @ApiOperation(value = "招商地图授权查询", notes = "招商地图授权查询")
    @PostMapping(value = "/getZsMapPermissions")
    public ResultBody getZsMapPermissions(@RequestBody ZsMapPermissionsQueryBO zsMapPermissionsQueryBO) {
        return ResultBody.success(zsMapService.getZsMapPermissions(zsMapPermissionsQueryBO));
    }

    @Log("招商地图授权详情")
    @ApiOperation(value = "招商地图授权详情", notes = "招商地图授权详情")
    @PostMapping(value = "/getZsMapPermissionsDetail")
    public ResultBody getZsMapPermissionsDetail(@RequestBody ZsMapPermissionsQueryBO zsMapPermissionsQueryBO) {
        return ResultBody.success(zsMapService.getZsMapPermissionsDetail(zsMapPermissionsQueryBO));
    }
    @Log("招商地图授权日志查询")
    @ApiOperation(value = "招商地图授权轨迹查询", notes = "招商地图授权日志查询")
    @PostMapping(value = "/getZsMapPermissionsRecord")
    public ResultBody getZsMapPermissionsRecord(@RequestBody PermissionsRecordQueryBO permissionsRecordQueryBO) {
        return ResultBody.success(zsMapService.getZsMapPermissionsRecord(permissionsRecordQueryBO));
    }
    @Log("招商地图授权保存")
    @ApiOperation(value = "招商地图授权保存", notes = "招商地图授权保存")
    @PostMapping(value = "/saveZsMapPermissions")
    public ResultBody saveZsMapPermissions(@RequestBody List<ZsMapPermissionsVO> zsMapPermissionsVOs) {
        zsMapService.saveZsMapPermissions(zsMapPermissionsVOs);
        return ResultBody.success(true);
    }

    @Log("招商地图授权导出")
    @ApiOperation(value = "招商地图授权导出", notes = "招商地图授权导出")
    @PostMapping(value = "/exportZsMapPermissions")
    public ResultBody exportZsMapPermissions(HttpServletResponse response, @RequestBody ZsMapPermissionsQueryBO zsMapPermissionsQueryBO) {
        zsMapService.exportZsMapPermissions(response,zsMapPermissionsQueryBO);
        return ResultBody.success(true);
    }

    @Log("招商地图授权删除")
    @ApiOperation(value = "招商地图授权删除", notes = "招商地图授权删除")
    @PostMapping(value = "/deleteZsMapPermissions")
    public ResultBody deleteZsMapPermissions(@RequestBody ZsMapPermissionsVO zsMapPermissionsVO) {
        return zsMapService.deleteZsMapPermissions(zsMapPermissionsVO);
    }

    @Log("地图去重功能")
    @ApiOperation(value = "地图去重功能", notes = "地图去重功能")
    @PostMapping(value = "/zsMapDistict")
    public ResultBody zsMapDistict(@RequestBody Map paramMap) {
        String customerName = paramMap.get("customerNameSwitch") + "";
        String customerMobile = paramMap.get("customerMobileSwitch") + "";
        String lineDistance = paramMap.get("lineDistanceSwitch") + "";
        ResultBody resultBody = this.zsMap(paramMap);
        Map<String, Object> map = (Map) resultBody.getData();
        return ResultBody.success(zsMapService.zsMapDistict(customerName, customerMobile, lineDistance, (List<ZsMapResBO>)map.get("zsMapResBOList")));
    }

    @Log("位置异常")
    @ApiOperation(value = "位置异常", notes = "位置异常")
    @GetMapping(value = "/locatiomError")
    public ResultBody locatiomError(@RequestParam("businessId") String businessId) {
        zsMapService.locatiomError(businessId);
        return ResultBody.success(true);
    }

    @ApiOperation(value = "获取客户是否重复")
    @PostMapping("/getCustomerIsRepeat")
    public ResultBody getCustomerIsRepeat(@RequestBody Map map) {
        return zsMapService.getCustomerIsRepeat(map);
    }
    @Log("地图绘制保存")
    @ApiOperation(value = "地图绘制保存", notes = "地图绘制保存")
    @PostMapping(value = "/saveZsMapDraw")
    public ResultBody saveZsMapDraw(@RequestBody ZsMapDrawDTO zsMapDrawDTO) {
        if (!CollectionUtils.isEmpty(zsMapDrawDTO.getEnclosures())) {
            zsMapDrawDTO.setUrl(StringUtils.join(zsMapDrawDTO.getEnclosures(), ";"));
        }
//        zsMapService.zsMapDraw(zsMapDrawDTO);
//        return ResultBody.success(true);
        return zsMapService.saveZsMapDraw(zsMapDrawDTO);
    }

    @Log("地图绘制查询")
    @ApiOperation(value = "地图绘制查询", notes = "地图绘制查询")
    @PostMapping(value = "/getZsMapDraw")
    public ResultBody getZsMapDraw(HttpServletRequest request, HttpServletResponse response,@RequestBody ZsMapDrawQueryBO zsMapDrawQueryBO) {
        String userId = request.getHeader("userid");
        if(StringUtils.isEmpty(userId)){
            userId = SecurityUtils.getUserId();
        }
        zsMapDrawQueryBO.setCreateUser(userId);
        return ResultBody.success(zsMapService.getZsMapDraw(zsMapDrawQueryBO));
    }

    @Log("地图绘制删除")
    @ApiOperation(value = "地图绘制删除", notes = "地图绘制删除")
    @PostMapping(value = "/deleteZsMapDraw")
    public ResultBody deleteZsMapDraw(@RequestBody ZsMapDrawQueryBO zsMapDrawQueryBO) {
        return zsMapService.deleteZsMapDraw(zsMapDrawQueryBO);
    }

    @Log("新建文件夹")
    @ApiOperation(value = "新建文件夹", notes = "新建文件夹")
    @PostMapping(value = "/makeFolder")
    public ResultBody makeFolder(@RequestBody ZsMapDrawFolderBO zsMapDrawFolderBO) {
        return zsMapService.makeFolder(zsMapDrawFolderBO);
    }

    @Log("查询文件夹列表")
    @ApiOperation(value = "查询文件夹列表", notes = "查询文件夹列表")
    @PostMapping(value = "/getFolderList")
    public ResultBody getFolderList(@RequestBody ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO) {
        return zsMapService.getFolderList(zsMapDrawFolderQueryBO);
    }

    @Log("修改文件夹名称")
    @ApiOperation(value = "修改文件夹名称", notes = "修改文件夹名称")
    @PostMapping(value = "/updateFolder")
    public ResultBody updateFolder(@RequestBody ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO) {
        return zsMapService.updateFolder(zsMapDrawFolderQueryBO);
    }

    @Log("查询客户详情列表")
    @ApiOperation(value = "查询客户详情列表", notes = "查询客户详情列表")
    @PostMapping(value = "/getCustomerList")
    public ResultBody getCustomerList(@RequestBody List<BatchQueryCustomerBO> batchQueryCustomerBOList) {
        return zsMapService.getCustomerList(batchQueryCustomerBOList);
    }

    @Log("查询客户详情列表")
    @ApiOperation(value = "查询客户详情列表", notes = "查询客户详情列表")
    @PostMapping(value = "/getCustomerListT")
    public ResultBody getCustomerListT(@RequestBody List<BatchQueryCustomerBO> batchQueryCustomerBOList) {
        return zsMapService.getCustomerList(batchQueryCustomerBOList);
    }

    @Log("修改地图绘制信息")
    @ApiOperation(value = "修改地图绘制信息", notes = "修改地图绘制信息")
    @PostMapping(value = "/updateZsmapDraw")
    public ResultBody updateZsmapDraw(@RequestBody ZsMapDrawBO zsMapDrawBO) {
        zsMapService.updateZsmapDraw(zsMapDrawBO);
        return ResultBody.success(true);
    }

    @Log("网格分布统计")
    @ApiOperation(value = "网格分布统计", notes = "根据开始时间和结束时间统计各省市地图绘制分布情况")
    @PostMapping(value = "/getGridDistribution")
    public ResultBody getGridDistribution(HttpServletRequest request, HttpServletResponse response, @RequestBody GridDistributionQueryBO gridDistributionQueryBO) {
        String userId = request.getHeader("userid");
        if(StringUtils.isEmpty(userId)){
            userId = SecurityUtils.getUserId();
        }
        gridDistributionQueryBO.setCreateUser(userId);
        return ResultBody.success(zsMapService.getGridDistribution(gridDistributionQueryBO));
    }

    @Log("导出网格分布统计Word报告")
    @ApiOperation(value = "导出网格分布统计Word报告", notes = "根据开始时间和结束时间导出网格分布统计Word报告")
    @PostMapping(value = "/exportGridDistributionWord")
    public void exportGridDistributionWord(HttpServletRequest request, HttpServletResponse response, @RequestBody GridDistributionQueryBO gridDistributionQueryBO) {
        String userId = request.getHeader("userid");
        if(StringUtils.isEmpty(userId)){
            userId = SecurityUtils.getUserId();
        }
        gridDistributionQueryBO.setCreateUser(userId);
        zsMapService.exportGridDistributionWord(response, gridDistributionQueryBO);
    }
}
