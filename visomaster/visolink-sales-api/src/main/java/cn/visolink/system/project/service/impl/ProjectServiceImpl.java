package cn.visolink.system.project.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.allpeople.examine.model.ProjectList;
import cn.visolink.system.companyQw.util.DateUtils;
import cn.visolink.system.custMap.bo.ZsMapPermissionsBO;
import cn.visolink.system.custMap.dao.ZsMapPermissionsDao;
import cn.visolink.system.custMap.service.ZsMapService;
import cn.visolink.system.job.authorization.mapper.JobMapper;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import cn.visolink.system.project.model.vo.TranslateProjectVo;
import cn.visolink.system.project.service.ProjectService;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/9/17 10:10 上午
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectServiceImpl implements ProjectService {

    public static final String GROUPNUM = "00000001";

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private ZsMapPermissionsDao zsMapPermissionsDao;
    @Autowired
    private ZsMapService zsMapService;
    @Override
    public List<Map> findProjectListByUserId(String UserName,String projectName,String authCompanyID) {
        if(StrUtil.isEmpty(UserName)){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        Map map=new HashMap();
        map.put("UserName", UserName);
        Map userInfoMap = authMapper.mGetUserInfo(map);
        List<String> fullpath = projectMapper.findFullPathHasUser(map);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        List<Map> mapList = projectMapper.findProjectListByUserName( UserName,projectName,userInfoMap.get("JobCode").toString(),sb.toString());
        return mapList;
    }

    @Override
    public List<Map> getRegionByUserName() {
        Map map = new HashMap();
        map.put("UserName",SecurityUtils.getUsername());
        List<String> fullpath = projectMapper.findFullPathAll(map);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无区域权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        map.put("where", sb.toString());
        List<Map> projectList = projectMapper.getRegionByUserName(map);
        return projectList;
    }

    /**
     * 查询项目
     * @param map
     * @return
     */
    @Override
    public List<ResultProjectVO> getProjectListByUserId(Map<String,Object> map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        List<String> fullpath = projectMapper.findFullPath(map);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        map.put("where", sb.toString());
        String jobCode = map.get("JobCode")+"";
        //获取人员最高权限
        map.put("UserId",SecurityUtils.getUserId());
        List<Map> userList = projectMapper.findFullPathAllInsZs(map);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("10001")){
                    if(level<= 1){
                        jobCode = "10001";
                        level = 3;
                    }
                }
            }
        }
        map.put("JobCode",jobCode);
        List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map);
        if(projectList.size() > 0){
            List<String> strings = projectList.stream().map(ProjectVO::getComguid).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String comguid = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(comguid);
                List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getComguid())).collect(Collectors.toList());
                if(projectVOList1.size() > 0){
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                        resultProjectVO2.setInvestmentTypeCode(projectVOList1.get(j).getInvestmentTypeCode());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;
    }

    @Override
    public List<ResultProjectVO> getProjectAllList(Map<String,Object> map) {
        List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map);
        if(projectList.size() > 0){
            List<String> strings = projectList.stream().map(ProjectVO::getComguid).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String comguid = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(comguid);
                List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getComguid())).collect(Collectors.toList());
                if(projectVOList1.size() > 0){
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;
    }

    @Override
    public List<ResultProjectVO> getProjectAllListByUserName(Map<String,Object> map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        List<String> fullpath = projectMapper.findFullPathHasUser(map);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        map.put("where", sb.toString());
        String jobCode = map.get("JobCode")+"";
        //获取人员最高权限
        map.put("UserId",SecurityUtils.getUserId());
        List<Map> userList = projectMapper.findFullPathAllInsZs(map);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("10001")){
                    if(level<= 1){
                        jobCode = "10001";
                        level = 3;
                    }
                }
            }
        }
        map.put("JobCode",jobCode);
        List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map);
        if(projectList.size() > 0){
            List<String> strings = projectList.stream().map(ProjectVO::getComguid).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String comguid = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(comguid);
                List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getComguid())).collect(Collectors.toList());
                if(projectVOList1.size() > 0){
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;
    }

    @Override
    public List<ResultProjectVO> getProjectListByUserIdSmds(Map<String, Object> map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        List<String> fullpath = projectMapper.findFullPathHasUser(map);
        StringBuffer sb = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                sb2.append("org.FullPath = '"+fullpath.get(i)+"'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                sb2.append(" or org.FullPath = '"+fullpath.get(i)+"'");
            }
        }
        map.put("where", sb.toString());
        map.put("where2", sb2.toString());
        String jobCode = map.get("JobCode")+"";
        //获取人员最高权限
        map.put("UserId",SecurityUtils.getUserId());
        List<Map> userList = projectMapper.findFullPathAllInsZs(map);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("10001")){
                    if(level<= 1){
                        jobCode = "10001";
                        level = 3;
                    }
                }
            }
        }
        map.put("JobCode",jobCode);
        List<ProjectVO> projectList = projectMapper.getProjectListByUserNameSmds(map);
        if(level == 3){
            if(projectList.size() > 0){
                List<String> strings = projectList.stream().map(ProjectVO::getComguid).filter(x -> x != null).distinct().collect(Collectors.toList());
                List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
                for (int i = 0; i < strings.size(); i++) {
                    String comguid = strings.get(i);
                    ResultProjectVO resultProjectVO = new ResultProjectVO();
                    resultProjectVO.setValue(comguid);
                    List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getComguid())).collect(Collectors.toList());
                    if(projectVOList1.size() > 0){
                        List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                        for (int j = 0; j < projectVOList1.size(); j++) {
                            resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                            ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                            resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                            resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                            resultProjectVOList2.add(resultProjectVO2);
                        }
                        resultProjectVO.setChildren(resultProjectVOList2);
                    }
                    resultProjectVOList.add(resultProjectVO);
                }
                return resultProjectVOList;
            }
        }else {
            if(projectList.size() > 0){
                List<String> strings = new ArrayList<>();
                for (int i = 0; i < projectList.size(); i++) {
                    if(Objects.equals(projectList.get(i).getProjectId(), projectList.get(i).getOrgId())){
                        strings.add(projectList.get(i).getProjectId());
                    }
                }
                List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
                for (int i = 0; i < strings.size(); i++) {
                    String comguid = strings.get(i);
                    ResultProjectVO resultProjectVO = new ResultProjectVO();
                    resultProjectVO.setValue(comguid);
                    List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getOrgId())).collect(Collectors.toList());
                    if(projectVOList1.size() > 0){
                        List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                        for (int j = 0; j < projectVOList1.size(); j++) {
                            resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                            ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                            if(!projectVOList1.get(j).getProjectId().equals(projectVOList1.get(j).getOrgId())){
                                resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                                resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                                resultProjectVOList2.add(resultProjectVO2);
                            }
                        }
                        resultProjectVO.setChildren(resultProjectVOList2);
                    }
                    resultProjectVOList.add(resultProjectVO);
                    projectList.removeAll(projectVOList1);
                }
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                for (int j = 0; j < projectList.size(); j++) {
                    ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                    resultProjectVO2.setValue(projectList.get(j).getProjectId());
                    resultProjectVO2.setLabel(projectList.get(j).getProjectName());
                    resultProjectVOList2.add(resultProjectVO2);
                }
                resultProjectVO.setChildren(resultProjectVOList2);
                resultProjectVOList.add(resultProjectVO);
                return resultProjectVOList;
            }
        }
        return null;
    }

    /**
     * 查询项目
     * @param map
     * @return
     */
    @Override
    public List<ResultProjectVO> getProjectListByUserNameAndSqx(Map<String,Object> map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(map);
        List<String> fullpath = projectMapper.findFullPathSQX(map);
        List<ProjectVO> projectList = new ArrayList<>();
        String jobCode = map.get("JobCode")+"";
        //查询招商地图授权
        if (map.get("isQueryPermissions") != null) {
            List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
                Stream.of((String)map.get("userId")).collect(Collectors.toList()));

            // 获取permissionLevel参数，并对mapPermissions进行过滤
            Object permissionLevelObj = map.get("permissionLevel");
            if (permissionLevelObj != null && mapPermissions != null) {
                String permissionLevel = String.valueOf(permissionLevelObj);
                if ("1".equals(permissionLevel)) {
                    mapPermissions = mapPermissions.stream()
                            .filter(bo -> "1".equals(bo.getPermissionsType()) || "2".equals(bo.getPermissionsType()) || "4".equals(bo.getPermissionsType()))
                            .collect(Collectors.toList());
                } else if ("3".equals(permissionLevel)) {
                    mapPermissions = mapPermissions.stream()
                            .filter(bo -> "3".equals(bo.getPermissionsType()))
                            .collect(Collectors.toList());
                }
            }
            if (!CollectionUtils.isEmpty(mapPermissions)) {
                List<String> projPermissList = mapPermissions.stream().filter(zsMapPermissionsBO-> StringUtils.isNotBlank(zsMapPermissionsBO.getProjPermissions())
                    && zsMapPermissionsBO.getProjEndDate().compareTo(
                    DateUtils.getDateAfterDays(new Date(), -1)) > 0).map(ZsMapPermissionsBO::getProjPermissions).collect(
                    Collectors.toList());
                List<ProjectVO> permissList = zsMapService.getProjectNames(projPermissList);
                projectList.addAll(permissList);
            }
        }
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList) && CollectionUtils.isEmpty(projectList)){
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
            //获取人员最高权限
            //map.put("UserId",SecurityUtils.getUserId());
            List<Map> userList = projectMapper.findFullPathAllInsZs(map);
            int level = 0;
            if(!CollectionUtils.isEmpty(userList)){
                for(int i = 0;i<userList.size();i++){
                    if(String.valueOf(userList.get(i).get("JobCode")).equals("10001")){
                        if(level<= 1){
                            jobCode = "10001";
                            level = 3;
                        }
                    }
                }
            }
            map.put("JobCode",jobCode);
            List<ProjectVO> projects = projectMapper.getProjectListByUserName(map);
            if (!CollectionUtils.isEmpty(projects)) {
                projectList.addAll(projects);
            }
        }
        if(!CollectionUtils.isEmpty(qyProjectList) && jobCode != "10001"){
           for(int i = 0;i<qyProjectList.size();i++){
               if(!projectList.contains(qyProjectList.get(i))){
                   projectList.add(qyProjectList.get(i));
               }
           }
        }
        if(projectList.size() > 0){
             projectList = projectList.stream()
                    .collect(Collectors.toMap(ProjectVO::getProjectId, obj -> obj, (existing, replacement) -> existing))
                    .values()
                    .stream()
                    .collect(Collectors.toList());
            projectList.stream().forEach(a-> a.setOrgId(null));
            projectList = projectList.stream().distinct().collect(Collectors.toList());
            List<String> strings = projectList.stream().map(ProjectVO::getComguid).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String comguid = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(comguid);
                List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getComguid())).collect(Collectors.toList());
                if(projectVOList1.size() > 0){
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                        resultProjectVO2.setInvestmentTypeCode(projectVOList1.get(j).getInvestmentTypeCode());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;
    }

    /**
     * 查询区域
     * @param map
     * @return
     */
    @Override
    public List<Map> getRegionListByUserNameAndSqx(Map<String,Object> map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
                Stream.of((String)map.get("userId")).collect(Collectors.toList()));

        // 获取permissionLevel参数，并对mapPermissions进行过滤
        Object permissionLevelObj = map.get("permissionLevel");
        if (permissionLevelObj != null && mapPermissions != null) {
            String permissionLevel = String.valueOf(permissionLevelObj);
            if ("1".equals(permissionLevel)) {
                mapPermissions = mapPermissions.stream()
                        .filter(bo -> "1".equals(bo.getPermissionsType()) || "2".equals(bo.getPermissionsType()) || "4".equals(bo.getPermissionsType()))
                        .collect(Collectors.toList());
            } else if ("3".equals(permissionLevel)) {
                mapPermissions = mapPermissions.stream()
                        .filter(bo -> "3".equals(bo.getPermissionsType()))
                        .collect(Collectors.toList());
            }
        }

        if (!CollectionUtils.isEmpty(mapPermissions)) {
            List<List<String>> dbAreaDataList = new ArrayList<>();
            for (ZsMapPermissionsBO zsMapPermissionsBO : mapPermissions) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(zsMapPermissionsBO.getAreaPermissions())
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
            // --- 新增：转为树形结构 ---
            Map<String, Map<String, Object>> provinceMap = new LinkedHashMap<>();
            for (List<String> area : dbAreaDataList) {
                if (area == null || area.isEmpty()) continue;
                if (area.size() == 2) {
                    // 直辖市
                    String city = area.get(0);
                    String district = area.get(1);
                    Map<String, Object> cityNode = provinceMap.computeIfAbsent(city, k -> {
                        Map<String, Object> node = new LinkedHashMap<>();
                        node.put("value", city);
                        node.put("label", city);
                        node.put("children", new ArrayList<Map<String, Object>>());
                        return node;
                    });
                    List<Map<String, Object>> children = (List<Map<String, Object>>) cityNode.get("children");
                    Map<String, Object> districtNode = new LinkedHashMap<>();
                    districtNode.put("value", district);
                    districtNode.put("label", district);
                    children.add(districtNode);
                } else if (area.size() == 3) {
                    // 省-市-区
                    String province = area.get(0);
                    String city = area.get(1);
                    String district = area.get(2);
                    Map<String, Object> provinceNode = provinceMap.computeIfAbsent(province, k -> {
                        Map<String, Object> node = new LinkedHashMap<>();
                        node.put("value", province);
                        node.put("label", province);
                        node.put("children", new ArrayList<Map<String, Object>>());
                        return node;
                    });
                    List<Map<String, Object>> cityList = (List<Map<String, Object>>) provinceNode.get("children");
                    // 查找或新建 cityNode
                    Map<String, Object> cityNode = null;
                    for (Map<String, Object> c : cityList) {
                        if (city.equals(c.get("value"))) {
                            cityNode = c;
                            break;
                        }
                    }
                    if (cityNode == null) {
                        cityNode = new LinkedHashMap<>();
                        cityNode.put("value", city);
                        cityNode.put("label", city);
                        cityNode.put("children", new ArrayList<Map<String, Object>>());
                        cityList.add(cityNode);
                    }
                    List<Map<String, Object>> districtList = (List<Map<String, Object>>) cityNode.get("children");
                    Map<String, Object> districtNode = new LinkedHashMap<>();
                    districtNode.put("value", district);
                    districtNode.put("label", district);
                    districtList.add(districtNode);
                }
            }
            // 组装最终结构
            List<Map> result = new ArrayList<>(provinceMap.values());

            return result;
        }
        return null;
    }

    // 递归过滤方法
    @SuppressWarnings("unchecked")
    private List<Map> filterByPermissionsType(List<Map> nodes, List<Integer> allowedTypes) {
        List<Map> filtered = new ArrayList<>();
        for (Map node : nodes) {
            Object typeObj = node.get("permissionsType");
            Integer type = null;
            if (typeObj instanceof Integer) {
                type = (Integer) typeObj;
            } else if (typeObj != null) {
                try {
                    type = Integer.parseInt(typeObj.toString());
                } catch (Exception ignored) {}
            }
            boolean typeMatch = type != null && allowedTypes.contains(type);
            // 递归过滤children
            List<Map> children = (List<Map>) node.get("children");
            if (children != null && !children.isEmpty()) {
                children = filterByPermissionsType(children, allowedTypes);
                node.put("children", children);
            }
            // 只要自己或子节点有匹配就保留
            if (typeMatch || (children != null && !children.isEmpty())) {
                filtered.add(node);
            }
        }
        return filtered;
    }

    /**
     * 查询项目
     * @param map
     * @return
     */
    @Override
    public List<ResultProjectVO> getProjectListByOwnerUserAndSqx(Map<String,Object> map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        List<String> fullpath = projectMapper.findFullPathByOwnerUser(map);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }

        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        map.put("where", sb.toString());
        List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map);

        //查询招商地图授权
        if (map.get("isQueryPermissions") != null) {
            List<ZsMapPermissionsBO> mapPermissions = zsMapPermissionsDao.getMapPermissions(
                    Stream.of((String)map.get("userId")).collect(Collectors.toList()));

            // 获取permissionLevel参数，并对mapPermissions进行过滤
            Object permissionLevelObj = map.get("permissionLevel");
            if (permissionLevelObj != null && mapPermissions != null) {
                String permissionLevel = String.valueOf(permissionLevelObj);
                if ("1".equals(permissionLevel)) {
                    mapPermissions = mapPermissions.stream()
                            .filter(bo -> "1".equals(bo.getPermissionsType()) || "2".equals(bo.getPermissionsType()) || "4".equals(bo.getPermissionsType()))
                            .collect(Collectors.toList());
                } else if ("3".equals(permissionLevel)) {
                    mapPermissions = mapPermissions.stream()
                            .filter(bo -> "3".equals(bo.getPermissionsType()))
                            .collect(Collectors.toList());
                }
            }

            if (!CollectionUtils.isEmpty(mapPermissions)) {
                List<String> projPermissList = mapPermissions.stream().filter(zsMapPermissionsBO-> StringUtils.isNotBlank(zsMapPermissionsBO.getProjPermissions())
                        && zsMapPermissionsBO.getProjEndDate().compareTo(
                        DateUtils.getDateAfterDays(new Date(), -1)) > 0).map(ZsMapPermissionsBO::getProjPermissions).collect(
                        Collectors.toList());
                List<ProjectVO> permissList = zsMapService.getProjectNames(projPermissList);
                projectList.addAll(permissList);
            }
        }
        if(projectList.size() > 0){
            List<String> strings = projectList.stream().map(ProjectVO::getComguid).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String comguid = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(comguid);
                List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getComguid())).collect(Collectors.toList());
                if(projectVOList1.size() > 0){
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                        resultProjectVO2.setInvestmentTypeCode(projectVOList1.get(j).getInvestmentTypeCode());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    // 去重逻辑，根据 label 和 value 判断
                    resultProjectVOList2 = resultProjectVOList2.stream()
                            .filter(distinctByKey(vo -> vo.getLabel() + "|" + vo.getValue()))
                            .collect(Collectors.toList());
                    resultProjectVO.setChildren(resultProjectVOList2);
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;
    }

    // 辅助方法，用于 Stream 的 distinct 操作
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    /**
     * 查询项目
     * @param map
     * @return
     */
    @Override
    public List<ResultProjectVO> getProjectListByOwnerUser(Map<String,Object> map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        List<String> fullpath = projectMapper.findFullPathByOwnerUser(map);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        map.put("where", sb.toString());
        List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map);
        if(projectList.size() > 0){
            List<String> strings = projectList.stream().map(ProjectVO::getComguid).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String comguid = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(comguid);
                List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getComguid())).collect(Collectors.toList());
                if(projectVOList1.size() > 0){
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                        resultProjectVO2.setInvestmentTypeCode(projectVOList1.get(j).getInvestmentTypeCode());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;
    }

    @Override
    public ResultBody getCityListByUser(String username) {
        if (StringUtils.isEmpty(username)) {
            return ResultBody.error(500, "未识别到当前用户，请刷新后重试");
        }

        List<String> orgList = projectMapper.getOrgListByUser(username);
        if (CollectionUtils.isEmpty(orgList)) {
            return ResultBody.error(500, "当前用户未配置岗位信息！");
        }
        if (orgList.contains(GROUPNUM)) {
            List<Map> cityList = projectMapper.getCityList(null);
            return ResultBody.success(cityList);
        }
        StringBuffer orgStr = new StringBuffer();
        for (int i = 0; i < orgList.size(); i++) {
            if (i == orgList.size() - 1) {
                orgStr.append("'").append(orgList.get(i)).append("'");
            } else {
                orgStr.append("'").append(orgList.get(i)).append("',");
            }
        }
//        List<Map> cityListByOrgId = projectMapper.getCityListByOrgId(orgStr.toString());
        List<Map> cityList = projectMapper.getCityList(orgStr.toString());
        return ResultBody.success(cityList);
    }

    @Override
    public ResultBody getProList(Map map) {
        PageHelper.startPage(Integer.parseInt(map.get("pageNum")+""),Integer.parseInt(map.get("pageSize")+""));
        List<ProjectList> projectLists = projectMapper.getProList(map);
        return ResultBody.success(new PageInfo<>(projectLists));
    }

    @Override
    public ResultBody getProDetail(Map map) {
        ProjectList projectList = projectMapper.getProDetail(map);
        return ResultBody.success(projectList);
    }

    @Override
    public ResultBody editPro(Map map) {
        if ("1".equals(map.get("status")+"")){
            map.put("isSyn",1);
        }else{
            map.put("isSyn",0);
        }
        map.put("Editor",SecurityUtils.getUserId());
        projectMapper.updateProject(map);
        String fullPath=jobMapper.getFullPath(map.get("orgID").toString());
        jobMapper.updateOrg(map.get("projectId").toString(),fullPath);
        //查询岗位
        // GZZS  广州招商业务员
        //zygw  项目业务员
        //SZZS  深圳招商业务员
        //BJZS  北京招商业务员
        //SHZS  上海招商业务员
        //BJZSJL  北京招商负责人
        //GZZSJL  广州招商负责人
        //SZZSJL  深圳招商负责人
        //SHZSJL  上海招商负责人
        //xsjl  项目负责人
        //xsjl  项目负责人
//        String param = "'zszj','yxjl'";
//        //查询通用岗信息
//        List<Map> comJobs = jobMapper.getComJobs(param);
//        String orgID = map.get("orgID").toString();
//        //新增岗位
//        List<Map> proMaps = new ArrayList<>();
//        for (Map com:comJobs) {
//            String comJobCode = com.get("JobCode")+"";
//            String comJobId = com.get("ID")+"";
//            Map proMap = new HashMap();
//            if ("zszj".equals(comJobCode)){
//                proMap.put("JobCode","zszj");
//                proMap.put("JobName","招商总监（项目）");
//                proMap.put("JobDesc","招商总监（项目）");
//            }else if ("yxjl".equals(comJobCode)){
//                proMap.put("JobCode","yxjl");
//                proMap.put("JobName","营销经理（项目）");
//                proMap.put("JobDesc","营销经理（项目）");
//            }
//            proMap.put("CommonJobID",comJobId);
//            proMap.put("JobOrgID",orgID);
//            proMap.put("AuthCompanyID","ede1b679-3546-11e7-a3f8-5254007b6f02");
//            proMap.put("ProductID","ee3b2466-3546-11e7-a3f8-5254007b6f02");
//            proMaps.add(proMap);
//        }
//        jobMapper.addProJobs(proMaps);
        return ResultBody.success("绑定成功！！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveTranslatePro(Map map) {
        String projectId = String.valueOf(map.get("transProjectIds"));
        String transProjectIds = String.valueOf(map.get("transProjectIds"));
        if(StringUtils.isEmpty(projectId) || StringUtils.isEmpty(transProjectIds)){
            return ResultBody.error(-10002, "参数不能为空！");
        }
        //处理多个关联项目
        List<String> proIds = Arrays.asList(transProjectIds.split(","));
        //删除已存在联动
        projectMapper.updateTranslatePro(map);
        //保存新联动关系
        List<Map> list = new ArrayList<>();
        proIds.stream().forEach(x->{
            map.put("transProjectId",x);
            list.add(map);
        });
        projectMapper.saveTranslatePro(list);
        return ResultBody.success("联动成功！");
    }

    @Override
    public ResultBody getTranslatePro(Map map) {
        if(StringUtils.isEmpty(String.valueOf(map.get("projectId")))){
            return ResultBody.error(-10002, "参数不能为空！");
        }
        return ResultBody.success(projectMapper.getTranslatePro(map));
    }

    @Override
    public List<ResultProjectVO> getZyProject(Map map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        List<String> fullpath = projectMapper.getZyProject(map.get("UserName")+"");
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        map.put("where", sb.toString());
        List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map);
        if(projectList.size() > 0){
            List<String> strings = projectList.stream().map(ProjectVO::getComguid).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String comguid = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(comguid);
                List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getComguid())).collect(Collectors.toList());
                if(projectVOList1.size() > 0){
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;
    }

    @Override
    public List<ResultProjectVO> getZygwProject(Map<String, Object> map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        List<String> fullpath = projectMapper.getZygwProject(map.get("UserName")+"");
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        map.put("where", sb.toString());
        List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map);
        if(projectList.size() > 0){
            List<String> strings = projectList.stream().map(ProjectVO::getComguid).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String comguid = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(comguid);
                List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> comguid.equals(pro.getComguid())).collect(Collectors.toList());
                if(projectVOList1.size() > 0){
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;
    }

    @Override
    public ResultBody getTranslateProList(Map<String, Object> map) {
        if(map == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        String projectId = String.valueOf(map.get("projectId"));
        if(StringUtils.isEmpty(projectId)){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        List<ProjectVO> projectList = projectMapper.getTranslateProListByAreaId(map);
        return ResultBody.success(projectList);
    }

    /**
     * @Author luqianqian
     * @Description //联动项目
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    @Override
    public ResultBody saveTranslateProject(TranslateProjectVo translateProjectVo) {
        if(translateProjectVo == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        String areaId = translateProjectVo.getAreaId();
        if(StringUtils.isEmpty(areaId)){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        if(StringUtils.isEmpty(translateProjectVo.getTransProjectId())){
            throw new BadRequestException(-10_0001,"未选择联动项目");
        }
//        //判断当前项目是否联动
//        TranslateProjectVo transPro = projectMapper.getTranslateProjectByAreaId(areaId);
//
        int i;
        if(translateProjectVo.getId() == null || translateProjectVo.getId().equals("")){
            translateProjectVo.setUserId(SecurityUtils.getUserId());
            i = projectMapper.saveTranslateProject(translateProjectVo);
        }else {
            translateProjectVo.setId(translateProjectVo.getId());
            i = projectMapper.updateTranslateProject(translateProjectVo);
        }
        return i >0 ? ResultBody.success("联动成功") : ResultBody.error(-10001,"联动失败");
    }

    /**
     * @Author luqianqian
     * @Description //获取联动项目详情
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    @Override
    public ResultBody getTranslateProjectInfo(TranslateProjectVo translateProjectVo) {
        if(translateProjectVo == null){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        String id = translateProjectVo.getId();
        if(StringUtils.isEmpty(id)){
            throw new BadRequestException(-10_0000,"参数不能为空");
        }
        Map reMap = new HashMap();
        Map map = new HashMap<>();
        map.put("id",id);
        List<ProjectVO> translateProListByAreaId = projectMapper.getTranslateProListByAreaId(map);
        reMap.put("noTransProList",translateProListByAreaId);
        //获取当前区域下所有联动项目
        List<TranslateProjectVo> transPro = projectMapper.getTranslateProjectByAreaIdInsStatus(id);
        if(transPro.size() != 0){
            List<List<String>> lists = new ArrayList<>();
            for (TranslateProjectVo t:transPro){
                List<String> transProIds = Arrays.asList(t.getTransProjectId().split(","));
                Boolean bolean = false;
                for (String s:transProIds){
                    if (s.equals(id)){
                        bolean = true;
                    }
                }
                if (bolean){
                    //获取当前区域下所有项目
                    map.put("list",transProIds);
                    map.put("transType","2");//已联动
                    reMap.put("transProList",projectMapper.getTranslateProListByAreaId(map));
                    reMap.put("translateId",t.getId());
                    reMap.put("startTime",t.getStartTime());
                    reMap.put("endTime",t.getEndTime());
                    reMap.put("endTime",t.getEndTime());
                    reMap.put("status",t.getStatus());
                }else{
                    lists.add(transProIds);
                }
            }

            for (ProjectVO p :translateProListByAreaId){
                if (map.get("list") != null){
                    List<String> list = (List<String>) map.get("list" );
                    for (String i:list){
                        if (p.getProjectId().equals(i)){
                            p.setType(1);
                        }
                    }
                }

                if (lists.size() != 0){
                    for (List<String> l: lists){
                        for (String n : l){
                            if (n.equals(p.getProjectId())){
                                p.setType(2);
                            }
                        }
                    }
                }
            }
        }
        reMap.put("areaId",translateProListByAreaId.get(0).getAreaId());
        reMap.put("areaName",translateProListByAreaId.get(0).getAreaName());
        return ResultBody.success(reMap);
    }

    @Override
    public ResultBody getProIsRegion(Map map) {
        String projectId = String.valueOf(map.get("projectId"));
        if(StringUtils.isEmpty(projectId)){
            return ResultBody.error(-1001,"参数不能为空！");
        }
        return ResultBody.success(projectMapper.getProIsRegion(projectId));
    }

    /**
     * @Author luqianqian
     * @Description //获取全部区域
     * @Date 16:31 2023/10/18
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    @Override
    public ResultBody getAllRegionList(Map map) {
        List<Map> regions = projectMapper.getAllRegionNew2(map);
        return ResultBody.success(regions);
    }

    @Override
    public ResultBody findFullPathAllInsZs() {
        Map map = new HashMap<>();
        map.put("UserId", SecurityUtils.getUserId());
        return ResultBody.success(projectMapper.findFullPathAllInsZs(map));
    }
}
