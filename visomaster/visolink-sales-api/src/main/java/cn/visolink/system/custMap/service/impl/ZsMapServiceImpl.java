package cn.visolink.system.custMap.service.impl;

import cn.visolink.common.DrawTypeEnum;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.model.ProjectRuleDetail;
import cn.visolink.system.channel.model.Task;
import cn.visolink.system.channel.model.vo.InformationVO;
import cn.visolink.system.channel.model.vo.OppInformation;
import cn.visolink.system.channel.model.vo.TaskVo;
import cn.visolink.system.channel.service.ProjectCluesService;
import cn.visolink.system.channel.service.TaskService;
import cn.visolink.system.custMap.bo.BatchQueryCustomerBO;
import cn.visolink.system.custMap.bo.PermissionsRecordQueryBO;
import cn.visolink.system.custMap.bo.ZsMapDrawBO;
import cn.visolink.system.custMap.bo.ZsMapDrawFolderBO;
import cn.visolink.system.custMap.bo.ZsMapDrawFolderQueryBO;
import cn.visolink.system.custMap.bo.ZsMapDrawFolderResBO;
import cn.visolink.system.custMap.bo.ZsMapDrawQueryBO;
import cn.visolink.system.custMap.bo.ZsMapDrawResBO;
import cn.visolink.system.custMap.bo.ZsMapPermissionsBO;
import cn.visolink.system.custMap.bo.ZsMapPermissionsExcelBO;
import cn.visolink.system.custMap.bo.ZsMapPermissionsQueryBO;
import cn.visolink.system.custMap.bo.ZsMapPermissionsRecordBO;
import cn.visolink.system.custMap.bo.ZsMapPermissionsResBO;
import cn.visolink.system.custMap.bo.ZsMapPermissionsVO;
import cn.visolink.system.custMap.bo.ZsMapRecordBO;
import cn.visolink.system.custMap.bo.GridDistributionQueryBO;
import cn.visolink.system.custMap.bo.GridDistributionResBO;
import cn.visolink.system.custMap.bo.ZsMapResBO;
import cn.visolink.system.custMap.dao.CustMapDao;
import cn.visolink.system.custMap.dao.ZsMapDrawDao;
import cn.visolink.system.custMap.dao.ZsMapDrawFolderDao;
import cn.visolink.system.custMap.dao.ZsMapPermissionsDao;
import cn.visolink.system.custMap.dao.ZsMapRecordDao;
import cn.visolink.system.custMap.service.ZsMapService;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.system.excel.util.AMapUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ZsMapServiceImpl implements ZsMapService {

  private static final Logger log = LoggerFactory.getLogger(ZsMapServiceImpl.class);
  @Autowired
  private ProjectMapper projectMapper;
  @Autowired
  private CustMapDao custMapDao;
  @Autowired
  private ZsMapPermissionsDao zsMapPermissionsDao;
  @Autowired
  private ZsMapRecordDao zsMapRecordDao;
  @Autowired
  private ProjectCluesDao projectCluesDao;
  @Autowired
  private ZsMapDrawDao zsMapDrawDao;
  @Autowired
  private ZsMapDrawFolderDao zsMapDrawFolderDao;
  @Autowired
  private ProjectCluesService projectCluesService;
  @Autowired
  private TaskService taskService;

  @Override
  public PageInfo<ZsMapPermissionsResBO> getZsMapPermissions(
      ZsMapPermissionsQueryBO zsMapPermissionsQueryBO) {
    getFulls(zsMapPermissionsQueryBO);
    PageHelper.startPage(zsMapPermissionsQueryBO.getPageIndex(), zsMapPermissionsQueryBO.getPageSize());
    List<ZsMapPermissionsResBO> zsMapPermissionsResBOList = custMapDao.getMapPermissions(
        zsMapPermissionsQueryBO);
    initPermissionsList(zsMapPermissionsResBOList);
    return new PageInfo<>(zsMapPermissionsResBOList);
  }

  private void initPermissionsList(List<ZsMapPermissionsResBO> zsMapPermissionsResBOList) {
    if (CollectionUtils.isNotEmpty(zsMapPermissionsResBOList)) {
      List<String> projPermissionsList = zsMapPermissionsResBOList.stream().filter(a-> !StringUtils.isEmpty(a.getProjPermissions())).map(a-> a.getProjPermissions()).collect(
          Collectors.toList());
      Map<String, ProjectVO> projectVOMap = null;
      if (CollectionUtils.isNotEmpty(projPermissionsList)) {
        List<ProjectVO> projectVOs = getProjectNames(projPermissionsList);
        projectVOMap = projectVOs.stream().collect(Collectors.toMap(ProjectVO::getProjectId, u -> u,(t1,t2) -> t1));
      }
      for (ZsMapPermissionsResBO zsMapPermissionsResBO : zsMapPermissionsResBOList) {
        if (!StringUtils.isEmpty(zsMapPermissionsResBO.getProjPermissions())) {
          List<String> collect = initProjectNames(zsMapPermissionsResBO.getProjPermissions(), projectVOMap);
          zsMapPermissionsResBO.setProjPermissions(String.join(",", collect));
        }
        if (StringUtils.isNotEmpty(zsMapPermissionsResBO.getAreaPermissions())) {
          List<String> projList = JSON.parseArray(zsMapPermissionsResBO.getAreaPermissions(), String.class);
          zsMapPermissionsResBO.setAreaPermissions(String.join(",", projList));
        }
      }
    }
  }

  private void getFulls(ZsMapPermissionsQueryBO zsMapPermissionsQueryBO) {
    Map paramMap = new HashMap<>();
    StringBuffer sb = new StringBuffer();
    paramMap.put("UserName", SecurityUtils.getUsername());
    //获取登陆人项目权限
    List<String> fullpaths = projectMapper.findFullPathAllHasUser(paramMap);
    if (CollectionUtils.isEmpty(fullpaths)) {
      throw new BadRequestException(-10_0000, "用户无项目权限！");
    }

    for (int i = 0; i < fullpaths.size(); i++) {
      if (i == 0) {
        sb.append("(org.FullPath LIKE '" + fullpaths.get(i) + "%'");
      } else {
        sb.append(" or org.FullPath LIKE '" + fullpaths.get(i) + "%'");
      }
    }
    sb.append(")");
    zsMapPermissionsQueryBO.setFullPaths(sb.toString());
  }


  @Override
  public ResultBody deleteZsMapPermissions(ZsMapPermissionsVO permissionsVO) {
    if (StringUtils.isEmpty(permissionsVO.getPermissionsId())) {
      return ResultBody.error(-10_000, "删除业务主键不可为空");
    }
    permissionsVO.setUpdateTime(new Date());
    permissionsVO.setUpdateBy(SecurityUtils.getUserId());
    return ResultBody.success(zsMapPermissionsDao.deletePermissionsById(permissionsVO));
  }

  @Override
  @Transactional
  public void saveZsMapPermissions(List<ZsMapPermissionsVO> zsMapPermissionsVOs) {
    // 兼容前端传permissionsTypeList的多选批量分配
    List<ZsMapPermissionsVO> flatList = new ArrayList<>();
    for (ZsMapPermissionsVO vo : zsMapPermissionsVOs) {
      if (vo.getPermissionsTypeList() != null && !vo.getPermissionsTypeList().isEmpty()) {
        for (String type : vo.getPermissionsTypeList()) {
          ZsMapPermissionsVO clone = new ZsMapPermissionsVO();
          BeanUtils.copyProperties(vo, clone);
          clone.setPermissionsType(type);
          clone.setPermissionsTypeList(null); // 防止后续重复处理
          flatList.add(clone);
        }
      } else {
        flatList.add(vo);
      }
    }
    zsMapPermissionsVOs = flatList;
    if (CollectionUtils.isEmpty(zsMapPermissionsVOs)) {
      throw new BadRequestException(-10_0000, "参数错误！");
    }
    //判断是否批量
    boolean flag = false;
    if (zsMapPermissionsVOs.size() > 1) {
      flag = true;
    }
    Date date = new Date();
    
    // 使用 accountId + permissionsType 作为唯一键，而不是只用 accountId
    Map<String, ZsMapPermissionsVO> permissionsBOMap = zsMapPermissionsVOs.stream()
        .collect(Collectors.toMap(
            a -> a.getAccountId() + "_" + a.getPermissionsType(), 
            Function.identity(), 
            (n1, n2) -> n1));
    
    List<String> accountIds = zsMapPermissionsVOs.stream()
        .map(ZsMapPermissionsVO::getAccountId).distinct().collect(Collectors.toList());
    List<ZsMapPermissionsBO> dbPermissions = zsMapPermissionsDao.getMapPermissions(accountIds);
    Map<String, ZsMapPermissionsBO> dbPermissionsBOMap = dbPermissions.stream()
        .collect(Collectors.toMap(
            a -> a.getAccountId() + "_" + a.getPermissionsType(), 
            Function.identity(), 
            (n1, n2) -> n1));
    
    for (String key : permissionsBOMap.keySet()) {
      ZsMapPermissionsVO permissionsVO = permissionsBOMap.get(key);
      if (CollectionUtils.isNotEmpty(permissionsVO.getAreaPermissionsList()) && permissionsVO.getAreaStartDate() == null) {
          throw new BadRequestException(-10_0001, "地区权限和地区有效期不可同时为空！");
       }
      if (CollectionUtils.isEmpty(permissionsVO.getAreaPermissionsList()) && permissionsVO.getAreaStartDate() != null) {
        throw new BadRequestException(-10_0002, "地区权限和地区有效期不可同时为空！");
      }
      if (CollectionUtils.isNotEmpty(permissionsVO.getProjPermissionsList()) && permissionsVO.getProjStartDate() == null) {
        throw new BadRequestException(-10_0003, "项目权限和项目有效期不可同时为空！");
      }
      if (CollectionUtils.isEmpty(permissionsVO.getProjPermissionsList()) && permissionsVO.getProjStartDate() != null) {
        throw new BadRequestException(-10_0004, "项目权限和项目有效期不可同时为空！");
      }
      if (CollectionUtils.isNotEmpty(permissionsVO.getAreaPermissionsList())) {
        permissionsVO.setAreaPermissions(JSON.toJSONString(permissionsVO.getAreaPermissionsList()));
      }
      if (CollectionUtils.isNotEmpty(permissionsVO.getProjPermissionsList())) {
        permissionsVO.setProjPermissions(JSON.toJSONString(permissionsVO.getProjPermissionsList()));
      }
      ZsMapPermissionsBO dbZsMapPermissionsBO = dbPermissionsBOMap.get(key);
      if (dbZsMapPermissionsBO == null) {
        //新增授权
        permissionsVO.setPermissionsId(UUID.randomUUID().toString());
        permissionsVO.setCreateBy(SecurityUtils.getUserId());
        permissionsVO.setCreateTime(date);
        permissionsVO.setUpdateBy(SecurityUtils.getUserId());
        permissionsVO.setUpdateTime(date);
        permissionsVO.setIsDel(0);
        zsMapPermissionsDao.save(permissionsVO);
      } else {
        permissionsVO.setPermissionsId(dbZsMapPermissionsBO.getPermissionsId());
        //修改授权
        permissionsVO.setUpdateTime(date);
        permissionsVO.setUpdateBy(SecurityUtils.getUserId());
        zsMapPermissionsDao.update(permissionsVO);
      }
      //单条授权保存日志
      if (!flag) {
        ZsMapRecordBO zsMapRecordBO = initZsMapRecordBO(permissionsVO, flag, permissionsVO.getUserName());
        zsMapRecordDao.save(zsMapRecordBO);
      }
    }
    //批量授权保存日志
    if (flag) {
      List<String> userNames = zsMapPermissionsVOs.stream()
          .map(ZsMapPermissionsVO::getUserName).distinct().collect(Collectors.toList());
      String userName = StringUtils.join(userNames, ",");
      ZsMapRecordBO zsMapRecordBO = initZsMapRecordBO(zsMapPermissionsVOs.get(0), flag, userName);
      zsMapRecordDao.save(zsMapRecordBO);
    }
  }

  @Override
  public void exportZsMapPermissions(HttpServletResponse response,
      ZsMapPermissionsQueryBO zsMapPermissionsQueryBO) {
    List<ZsMapPermissionsResBO> zsMapPermissionsList = this.getMapPermissionsResBOList(zsMapPermissionsQueryBO);
    String fileName = "地图授权信息导出";
    ArrayList<Object[]> dataset = new ArrayList<>();
    String[] headers = null;
    if (CollectionUtils.isEmpty(zsMapPermissionsList)) {
      throw new BadRequestException(-10_0000, "无授权信息！");
    }

    List<ZsMapPermissionsExcelBO> zsMapPermissionsExcelBOList = new ArrayList<>();
    zsMapPermissionsList.forEach(a -> {
      ZsMapPermissionsExcelBO zsMapPermissionsExcelBO = new ZsMapPermissionsExcelBO();
      BeanUtils.copyProperties(a, zsMapPermissionsExcelBO);
      zsMapPermissionsExcelBOList.add(zsMapPermissionsExcelBO);
    });
    headers = zsMapPermissionsExcelBOList.get(0).courtCaseTitle;
    for (ZsMapPermissionsExcelBO zsMapPermissionsExcelBO : zsMapPermissionsExcelBOList) {
      Object[] oArray = zsMapPermissionsExcelBO.toOldData();
      dataset.add(oArray);
    }
    ExcelExportUtil excelExportUtil = new ExcelExportUtil();
    try {
      excelExportUtil.exportExcel(fileName, headers, dataset, fileName, response,null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<ZsMapPermissionsResBO> getMapPermissionsResBOList(ZsMapPermissionsQueryBO zsMapPermissionsQueryBO) {
    getFulls(zsMapPermissionsQueryBO);
    List<ZsMapPermissionsResBO> zsMapPermissionsResBOList = custMapDao.getMapPermissions(
        zsMapPermissionsQueryBO);
    initPermissionsList(zsMapPermissionsResBOList);
    return zsMapPermissionsResBOList;
  }

  @Override
  public ZsMapPermissionsVO getZsMapPermissionsDetail(
      ZsMapPermissionsQueryBO zsMapPermissionsQueryBO) {
    if (StringUtils.isEmpty(zsMapPermissionsQueryBO.getPermissionsId())) {
      return null;
    }
    ZsMapPermissionsBO zsMapPermissionsBO = zsMapPermissionsDao.query(
        zsMapPermissionsQueryBO.getPermissionsId());
    if (zsMapPermissionsBO == null) {
      return null;
    }
    ZsMapPermissionsVO zsMapPermissionsVO = new ZsMapPermissionsVO();
    BeanUtils.copyProperties(zsMapPermissionsBO, zsMapPermissionsVO);
    if (StringUtils.isNotEmpty(zsMapPermissionsVO.getAreaPermissions())) {
      List<List<String>> areaPermissionsList = new ArrayList<>();
      List<String> areaList = JSON.parseArray(zsMapPermissionsBO.getAreaPermissions(), String.class);
      for (String area : areaList) {
        areaPermissionsList.add(JSON.parseArray(area, String.class));
      }
      zsMapPermissionsVO.setAreaPermissionsList(areaPermissionsList);
    }
    if (StringUtils.isNotEmpty(zsMapPermissionsVO.getProjPermissions())) {
      List<List<String>> projList = new ArrayList<>();
      List<String> projs = JSON.parseArray(zsMapPermissionsBO.getProjPermissions(), String.class);
      for (String proj : projs) {
        projList.add(JSON.parseArray(proj, String.class));
      }
      zsMapPermissionsVO.setProjPermissionsList(projList);
    }
    if (zsMapPermissionsVO.getAreaStartDate() != null && zsMapPermissionsVO.getAreaEndDate() != null) {
      List<String> areaDateTime = new ArrayList<>();
      areaDateTime.add((new SimpleDateFormat("yyyy-MM-dd").format(zsMapPermissionsVO.getAreaStartDate())));
      areaDateTime.add((new SimpleDateFormat("yyyy-MM-dd").format(zsMapPermissionsVO.getAreaEndDate())));
      zsMapPermissionsVO.setAreaDateTime(areaDateTime);
    }
    if (zsMapPermissionsVO.getProjStartDate() != null && zsMapPermissionsVO.getProjEndDate() != null) {
      List<String> projDateTime = new ArrayList<>();
      projDateTime.add((new SimpleDateFormat("yyyy-MM-dd").format(zsMapPermissionsVO.getProjStartDate())));
      projDateTime.add((new SimpleDateFormat("yyyy-MM-dd").format(zsMapPermissionsVO.getProjEndDate())));
      zsMapPermissionsVO.setProjDateTime(projDateTime);
    }
    return zsMapPermissionsVO;
  }

  @Override
  public List<ZsMapResBO> zsMapDistict(String customerName, String customerMobile, String lineDistance,
      List<ZsMapResBO> zsMapResBOList) {
    Map<String, List<ZsMapResBO>> map = new HashMap<>();
    List<ZsMapResBO> zsMapResBOList1 = new ArrayList<>();
    if (CollectionUtils.isEmpty(zsMapResBOList)) {
      return null;
    }

    //客户姓名
    if (StringUtils.isNotEmpty(customerName)
        && StringUtils.isEmpty(customerMobile)
        && StringUtils.isEmpty(lineDistance)) {
      map = zsMapResBOList.stream().collect(Collectors.groupingBy(ZsMapResBO::getOldCustomerName));
    }
    //联系方式
    if (StringUtils.isEmpty(customerName)
        && StringUtils.isNotEmpty(customerMobile)
        && StringUtils.isEmpty(lineDistance)) {
      map = zsMapResBOList.stream().collect(Collectors.groupingBy(ZsMapResBO::getOldCustomerMobile));
    }
    //直线距离
    if (StringUtils.isEmpty(customerName)
        && StringUtils.isEmpty(customerMobile)
        && StringUtils.isNotEmpty(lineDistance)) {
      map = getLineDistances(zsMapResBOList);
    }
    //客户姓名+联系方式
    if (StringUtils.isNotEmpty(customerName)
        && StringUtils.isNotEmpty(customerMobile)
        && StringUtils.isEmpty(lineDistance)) {
      map = zsMapResBOList.stream()
          .collect(Collectors.groupingBy(a -> a.getOldCustomerName() + a.getOldCustomerMobile()));
    }
    Map<String, List<ZsMapResBO>> lineDistances = getLineDistances(zsMapResBOList);
    //直线距离+客户姓名
    if (StringUtils.isNotEmpty(customerName)
        && StringUtils.isEmpty(customerMobile)
        && StringUtils.isNotEmpty(lineDistance)) {
      for (String key : lineDistances.keySet()) {
        Map<String, List<ZsMapResBO>> collect = lineDistances.get(key).stream()
            .collect(Collectors.groupingBy(a-> a.getOldCustomerName() + key));
        map.putAll(collect);
      }
    }
    //直线距离+联系方式
    if (StringUtils.isEmpty(customerName)
        && StringUtils.isNotEmpty(customerMobile)
        && StringUtils.isNotEmpty(lineDistance)) {
      for (String key : lineDistances.keySet()) {
        Map<String, List<ZsMapResBO>> collect = lineDistances.get(key).stream()
            .collect(Collectors.groupingBy(a-> a.getOldCustomerMobile() + key));
        map.putAll(collect);
      }
    }
    //客户姓名+联系方式+直线距离
    if (StringUtils.isNotEmpty(customerName)
        && StringUtils.isNotEmpty(customerMobile)
        && StringUtils.isNotEmpty(lineDistance)) {
      for (String key : lineDistances.keySet()) {
        Map<String, List<ZsMapResBO>> collect = lineDistances.get(key).stream()
            .collect(Collectors.groupingBy(a-> a.getOldCustomerName() + a.getOldCustomerMobile() + key));
        map.putAll(collect);
      }
    }
    //循环map，将多个key的value合并
    for (String key : map.keySet()) {
      List<ZsMapResBO> collect = map.get(key);
      if (collect.size() > 1) {
        ZsMapResBO zsMapResBO = new ZsMapResBO();
        BeanUtils.copyProperties(collect.get(0), zsMapResBO);;
        zsMapResBO.setZsMapResBOList(collect);
        zsMapResBO.setSize(collect.size());
        zsMapResBOList1.add(zsMapResBO);
      } else {
        zsMapResBOList1.add(collect.get(0));
      }
    }
    return zsMapResBOList1;
  }

  @Override
  public void locatiomError(String businessId) {
    projectCluesDao.updateLocationError(businessId);
  }

  @Override
  public void zsMapDraw(ZsMapDrawBO zsMapDrawBO) {
    if (zsMapDrawBO == null) {
      throw new BadRequestException(-10_0000, "参数错误！");
    }
    if (zsMapDrawBO.getType() == null || StringUtils.isEmpty(DrawTypeEnum.getNameByCode(zsMapDrawBO.getType()))) {
      throw new BadRequestException(-10_0000, "绘制类型错误！");
    }
    if (StringUtils.isNotEmpty(zsMapDrawBO.getFolderId())) {
      ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO = new ZsMapDrawFolderQueryBO();
      zsMapDrawFolderQueryBO.setFolderId(zsMapDrawBO.getFolderId());
      List<ZsMapDrawFolderResBO> zsMapDrawFolderResBOS = zsMapDrawFolderDao.select(
          zsMapDrawFolderQueryBO);
      if (CollectionUtils.isEmpty(zsMapDrawFolderResBOS)) {
        throw new BadRequestException(-10_0000, "文件夹不存在！");
      }
      zsMapDrawBO.setFolder(zsMapDrawFolderResBOS.get(0).getTitle());
    }
    zsMapDrawBO.setDrawId(UUID.randomUUID().toString());
    zsMapDrawBO.setCreateBy(SecurityUtils.getUserId());
    zsMapDrawBO.setCreateTime(new Date());
    zsMapDrawBO.setUpdateBy(SecurityUtils.getUserId());
    zsMapDrawBO.setUpdateTime(new Date());
    zsMapDrawBO.setIsDel(0);
    zsMapDrawDao.save(zsMapDrawBO);
  }

  @Override
  public ResultBody saveZsMapDraw(ZsMapDrawBO zsMapDrawBO) {
    if (zsMapDrawBO == null) {
      return ResultBody.error(-10_0000, "参数错误！");
    }
    if (zsMapDrawBO.getType() == null || StringUtils.isEmpty(DrawTypeEnum.getNameByCode(zsMapDrawBO.getType()))) {
      return ResultBody.error(-10_0000, "绘制类型错误！");
    }
    
    // 如果是面标记（type=3）或多区域标记（type=4），需要检查经纬度重叠
//    if (zsMapDrawBO.getType() == 3 || zsMapDrawBO.getType() == 4) {
//      // 检查经纬度参数
//      if (StringUtils.isEmpty(zsMapDrawBO.getLatLon())) {
//        return ResultBody.error(-10_0000, "面标记必须包含经纬度坐标！");
//      }
//
//      // 检查是否与项目下已存在的面标记重叠
//      ResultBody overlapCheckResult = checkPolygonOverlap(zsMapDrawBO);
//      if (overlapCheckResult.getCode() != 200) {
//        return overlapCheckResult;
//      }
//    }

    if (StringUtils.isNotEmpty(zsMapDrawBO.getFolderId())) {
      ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO = new ZsMapDrawFolderQueryBO();
      zsMapDrawFolderQueryBO.setFolderId(zsMapDrawBO.getFolderId());
      List<ZsMapDrawFolderResBO> zsMapDrawFolderResBOS = zsMapDrawFolderDao.select(
              zsMapDrawFolderQueryBO);
      if (CollectionUtils.isEmpty(zsMapDrawFolderResBOS)) {
        return ResultBody.error(-10_0000, "文件夹不存在！");
      }
      zsMapDrawBO.setFolder(zsMapDrawFolderResBOS.get(0).getTitle());
    }
    
    zsMapDrawBO.setDrawId(UUID.randomUUID().toString());
    zsMapDrawBO.setCreateBy(SecurityUtils.getUserId());
    zsMapDrawBO.setCreateTime(new Date());
    zsMapDrawBO.setUpdateBy(SecurityUtils.getUserId());
    zsMapDrawBO.setUpdateTime(new Date());
    zsMapDrawBO.setIsDel(0);
    zsMapDrawDao.save(zsMapDrawBO);
    return ResultBody.success("新增成功");
  }

  @Override
  public List<ZsMapDrawResBO> getZsMapDraw(ZsMapDrawQueryBO zsMapDrawQueryBO) {
    //根据用户ID或取本项目的用户ID
     String   userid=zsMapDrawQueryBO.getCreateUser();
    List<String> userIds = new ArrayList<>();
    userIds.add(userid);
    // 获取项目招商经理ID
    Map paramMap = new HashMap<>();
    paramMap.put("UserId", userid);
    List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
    if (!org.springframework.util.CollectionUtils.isEmpty(userList)) {
      for (Map user : userList) {
        String jobCode = String.valueOf(user.get("JobCode"));
        if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode) ||"yxjl".equals(jobCode) || "qyyxjl".equals(jobCode)
                || "zszj".equals(jobCode) || "qyzszj".equals(jobCode)
                || "xmz".equals(jobCode) || "qyfz".equals(jobCode) || "qyz".equals(jobCode) || "10001".equals(jobCode)
                || "jtsjg".equals(jobCode)) {
          String orgPath = String.valueOf(user.get("FullPath"));
          if("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)){
              int lastSlashIndex = orgPath.lastIndexOf("/");
              orgPath=orgPath.substring(0, lastSlashIndex);
          }
          String projectID = String.valueOf(user.get("ProjectID"));
          //List<String> teamUserIds = custMapDao.getProjectUserXsjl(orgPath);
//          List<String> teamUserIds = custMapDao.getProjectUserGlc(projectID);//除了zygw之外的角色
//          userIds.addAll(teamUserIds);
          //List<String> teamUserIds = custMapDao.getProjectUserGlc(projectID);//除了zygw之外的角色
          List<String> teamUserIds = custMapDao.getProjectUserGlz(orgPath);
          userIds.addAll(teamUserIds);
        }
      }
    }
    zsMapDrawQueryBO.setCreateUserS(userIds);
    List<ZsMapDrawResBO> zsMapDrawResBOList = zsMapDrawDao.select(zsMapDrawQueryBO);
    if("1".equals(zsMapDrawQueryBO.getIsTaskCreate())){
      return zsMapDrawResBOList;
    }else{
    if (CollectionUtils.isNotEmpty(zsMapDrawResBOList)) {
      List<String> ids = zsMapDrawResBOList.stream().map(ZsMapDrawResBO::getDrawId).collect(Collectors.toList());
      //List<TaskVo> taskVos = taskService.getListByIds(ids);
      Task task = new Task();
      task.setTaskAreaIds(ids);
      task.setTaskTypeId("2");
      task.setIsForDraw("1");
      task.setPageNum("1");
      task.setPageSize("99999");
      List<TaskVo> taskVos = projectCluesService.getTaskAccount(task);
      Map<String, List<TaskVo>> taskMap;
      if (CollectionUtils.isNotEmpty(taskVos)) {
        taskMap = taskVos.stream()
            .collect(Collectors.groupingBy(TaskVo::getTaskArea));
      } else {
        taskMap = Collections.emptyMap();
      }
      zsMapDrawResBOList.forEach(a-> {
        if (a.getType() != null) {
          a.setTypeName(DrawTypeEnum.getNameByCode(a.getType()));
        }
        if (StringUtils.isNotEmpty(a.getUrl())) {
          a.setEnclosures(Arrays.asList(a.getUrl().split(";")));
        }
        a.setTaskList(taskMap.get(a.getDrawId()));
      });
    }
    return zsMapDrawResBOList;
    }
  }

  @Override
  public ResultBody deleteZsMapDraw(ZsMapDrawQueryBO zsMapDrawQueryBO) {
    if (StringUtils.isEmpty(zsMapDrawQueryBO.getDrawId())) {
      return ResultBody.error(-10_000, "删除业务主键不可为空");
    }
//    List<TaskVo> taskVos = taskService.getListByIds(Arrays.asList(zsMapDrawQueryBO.getDrawId()));
//    if (CollectionUtils.isNotEmpty(taskVos)) {
//      return ResultBody.error(-10_000, "已创建任务的面标记不可删除");
//    }
    //逻辑删除
    return ResultBody.success(zsMapDrawDao.update(zsMapDrawQueryBO.getDrawId()));
  }

  @Override
  public ResultBody getCustomerIsRepeat(Map map) {
    String keyWord = String.valueOf(map.get("keyWord"));
    if(StringUtils.isEmpty(keyWord)){
      return ResultBody.error(-1001,"参数不能为空！");
    }
    map.put("customerMobile",keyWord);
    map.put("customerName",keyWord);
    //处理项目联动
    String projectId = String.valueOf(map.get("projectId"));
    List<String> proList = new ArrayList<>();
    String proIds = projectCluesDao.getTranslateProIds(projectId);
    if(StringUtils.isNotEmpty(proIds)){
      proList = new ArrayList(Arrays.asList(proIds.split(",")));
    }
    //不管有无联动项目 保证原项目存在
    proList.add(projectId);
    map.put("proList",proList);
    //判断系统配置规则
    ProjectRuleDetail projectRuleDetailXt = projectCluesDao.selectProjectRuleZs("-1","2");
    map.put("judgeNoPool",projectRuleDetailXt.getJudgeNoPool());
    map.put("judgeNoRegion",projectRuleDetailXt.getJudgeNoRegion());
    //查询是否存在机会
    List<Map> opps = new ArrayList<>();
    opps = projectCluesDao.getCstIsOkReferral(map);

    boolean flag = false;
    for (Map m:opps) {
      int cout = Integer.parseInt(m.get("count")+"");
      if (cout>0){
        //获取重复客户 判断客户对应类型的规则 万企通走万企通配置 转介走转介保护期
        map.put("type",m.get("type")+"");
        List<Map> cusOpps = projectCluesDao.getCstIsOkRepeat(map);
        for (Map cusOpp: cusOpps) {
          Map queryMap = new HashMap();
          List<String> proList1 = new ArrayList<>();
          proList1.add(cusOpp.get("projectId")+"");
          queryMap.put("proList",proList1);
          queryMap.put("customerMobile",cusOpp.get("customerMobile")+"");
          queryMap.put("customerName",cusOpp.get("customerName")+"");
          String sourceMode = cusOpp.get("sourceMode")+"";
          if("1".equals(sourceMode)){//万企通客户
            queryMap.put("judgeNoPool",projectRuleDetailXt.getWqtJudgeNoPool());
            queryMap.put("judgeNoRegion",projectRuleDetailXt.getWqtJudgeNoRegion());
            //查询是否存在机会
            List<Map> opps1 = new ArrayList<>();
            if("0".equals(projectRuleDetailXt.getWqtJudgeStage())){
              opps1 = projectCluesDao.getCstIsOkReferral(queryMap);
            }else if("1".equals(projectRuleDetailXt.getWqtJudgeStage())){
              opps1 = projectCluesDao.getCstIsOkComeVisit(queryMap);
            }else if("2".equals(projectRuleDetailXt.getWqtJudgeStage())){
              opps1 = projectCluesDao.getCstIsOkTrade(queryMap);
            }else {
              return ResultBody.error(-10002,"系统配置异常");
            }
            for (Map m1:opps1) {
              int cout1 = Integer.parseInt(m1.get("count")+"");
              if (cout1>0){
                flag = true;
                break;
              }
            }
          }else if("2".equals(sourceMode)){//转介客户
            queryMap.put("judgeNoPool",projectRuleDetailXt.getReferralJudgeNoPool());
            queryMap.put("judgeNoRegion",projectRuleDetailXt.getReferralJudgeNoRegion());
            //查询是否存在机会
            List<Map> opps2 = new ArrayList<>();
            if("0".equals(projectRuleDetailXt.getReferralJudgeStage())){
              opps2 = projectCluesDao.getCstIsOkReferral(queryMap);
            }else if("1".equals(projectRuleDetailXt.getReferralJudgeStage())){
              opps2 = projectCluesDao.getCstIsOkComeVisit(queryMap);
            }else if("2".equals(projectRuleDetailXt.getReferralJudgeStage())){
              opps2 = projectCluesDao.getCstIsOkTrade(queryMap);
            }else {
              return ResultBody.error(-10002,"系统配置异常");
            }
            for (Map m2:opps2) {
              int cout2 = Integer.parseInt(m2.get("count")+"");
              if (cout2>0){
                flag = true;
                break;
              }
            }
          }else if("3".equals(sourceMode)){//案场客户
            flag = true;
            break;
          }
        }
      }
    }
    Map reMap =new HashMap<>();
    reMap.put("customer",keyWord);
    List<Map> custList = projectCluesDao.getCustIsOkInfo(map);
    if (!org.springframework.util.CollectionUtils.isEmpty(custList)) {
      reMap.put("custList", custList);
      reMap.put("reportCount", projectCluesDao.getKsReportCount(custList));
      reMap.put("visitCount", projectCluesDao.getKsVisitCount(custList));
      reMap.put("orderCount", projectCluesDao.getKsOrderCount(custList));
    } else{
      reMap.put("reportCount", 0);
      reMap.put("visitCount", 0);
      reMap.put("orderCount", 0);
    }
    if (flag){
      reMap.put("reportStatus","不允许报备");
      return ResultBody.success(reMap);
    }
    reMap.put("reportStatus","允许报备");
    return ResultBody.success(reMap);
  }

  @Override
  public PageInfo<ZsMapPermissionsRecordBO> getZsMapPermissionsRecord(
      PermissionsRecordQueryBO permissionsRecordQueryBO) {
    if (!StringUtils.isEmpty(permissionsRecordQueryBO.getOperator())) {
      //通过名字查询用户id
      List<String> accountIds = zsMapRecordDao.getAccountIds(permissionsRecordQueryBO.getOperator());
      permissionsRecordQueryBO.setAccountIds(accountIds);
    }
    PageHelper.startPage(permissionsRecordQueryBO.getPageIndex(), permissionsRecordQueryBO.getPageSize());
    List<ZsMapPermissionsRecordBO> zsMapPermissionsRecord = zsMapRecordDao.getZsMapPermissionsRecord(
        permissionsRecordQueryBO);
    if (CollectionUtils.isNotEmpty(zsMapPermissionsRecord)) {
      List<String> projPermissionsList = zsMapPermissionsRecord.stream().filter(a-> !StringUtils.isEmpty(a.getProjPermissions())).map(a-> a.getProjPermissions()).collect(
          Collectors.toList());
      List<ProjectVO> projectVOs = getProjectNames(projPermissionsList);
      Map<String, ProjectVO> projectVOMap = projectVOs.stream().collect(Collectors.toMap(ProjectVO::getProjectId, u -> u,(t1,t2) -> t1));
      for (ZsMapPermissionsRecordBO zsMapPermissionsRecordBO : zsMapPermissionsRecord) {
        if (!StringUtils.isEmpty(zsMapPermissionsRecordBO.getProjPermissions())) {
          List<String> collect = initProjectNames(zsMapPermissionsRecordBO.getProjPermissions(), projectVOMap);
          zsMapPermissionsRecordBO.setProjPermissions(String.join(",", collect));
        }
      }
    }
    return new PageInfo<>(zsMapPermissionsRecord);
  }

  @Override
  public ResultBody makeFolder(ZsMapDrawFolderBO zsMapDrawFolderBO) {
    if (StringUtils.isEmpty(zsMapDrawFolderBO.getTitle())) {
      ResultBody.error(-10002, "请输入文件夹名称");
    }
    ZsMapDrawFolderQueryBO titleQueryBO = new ZsMapDrawFolderQueryBO();
    titleQueryBO.setTitle(zsMapDrawFolderBO.getTitle());
    List<ZsMapDrawFolderResBO> titleResBOS = zsMapDrawFolderDao.select(
        titleQueryBO);
    if (CollectionUtils.isNotEmpty(titleResBOS)) {
      return ResultBody.error(-10002, "文件夹名称已存在");
    }
    if (StringUtils.isNotEmpty(zsMapDrawFolderBO.getParentId())) {
      ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO = new ZsMapDrawFolderQueryBO();
      zsMapDrawFolderQueryBO.setFolderId(zsMapDrawFolderBO.getParentId());
      List<ZsMapDrawFolderResBO> zsMapDrawFolderResBOS = zsMapDrawFolderDao.select(
          zsMapDrawFolderQueryBO);
      if (CollectionUtils.isEmpty(zsMapDrawFolderResBOS)) {
        return ResultBody.error(-10002, "父级文件夹不存在");
      }
      zsMapDrawFolderBO.setLevel(zsMapDrawFolderResBOS.get(0).getLevel() + 1);
    } else {
      zsMapDrawFolderBO.setLevel(1);
    }
    zsMapDrawFolderBO.setFolderId(UUID.randomUUID().toString());
    zsMapDrawFolderBO.setCreateBy(SecurityUtils.getUserId());
    zsMapDrawFolderBO.setUpdateBy(SecurityUtils.getUserId());
    zsMapDrawFolderDao.save(zsMapDrawFolderBO);
    return ResultBody.success(true);
  }

  @Override
  public ResultBody getFolderList(ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO) {
    zsMapDrawFolderQueryBO.setCreateBy(SecurityUtils.getUserId());
    List<ZsMapDrawFolderResBO> zsMapDrawFolderResBOS = zsMapDrawFolderDao.select(zsMapDrawFolderQueryBO);
    Map<Integer, List<ZsMapDrawFolderResBO>> collect = zsMapDrawFolderResBOS.stream()
        .collect(Collectors.groupingBy(ZsMapDrawFolderResBO::getLevel));
    List<ZsMapDrawFolderResBO> resBOS = collect.get(collect.size());
    List<ZsMapDrawFolderResBO> resBOList = getList(collect, collect.size() - 1, resBOS);
    return ResultBody.success(resBOList);
  }

  private List<ZsMapDrawFolderResBO> getList(Map<Integer, List<ZsMapDrawFolderResBO>> collect,
      Integer index, List<ZsMapDrawFolderResBO> resBOS) {
    if (index < 1) {
      return resBOS;
    }
    List<ZsMapDrawFolderResBO> zsMapDrawFolderResBOS = collect.get(index);
    if (CollectionUtils.isNotEmpty(zsMapDrawFolderResBOS)) {
      for (ZsMapDrawFolderResBO zsMapDrawFolderResBO : zsMapDrawFolderResBOS) {
        if (CollectionUtils.isEmpty(zsMapDrawFolderResBO.getChildren())) {
          zsMapDrawFolderResBO.setChildren(new ArrayList<>());
        }
        for (ZsMapDrawFolderResBO resBO : resBOS) {
          if (zsMapDrawFolderResBO.getFolderId().equals(resBO.getParentId())) {
            zsMapDrawFolderResBO.getChildren().add(resBO);
          }
        }
      }
    }
    return getList(collect, index - 1, zsMapDrawFolderResBOS);
  }


  @Override
  public ResultBody updateFolder(ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO) {
    if (StringUtils.isEmpty(zsMapDrawFolderQueryBO.getFolderId()) || StringUtils.isEmpty(zsMapDrawFolderQueryBO.getTitle())) {
      return ResultBody.error(-10002, "参数错误");
    }
    return ResultBody.success(zsMapDrawFolderDao.update(zsMapDrawFolderQueryBO));
  }

  private List<String> initProjectNames(String projPermissions, Map<String, ProjectVO> projectVOMap) {
    List<String> collect = new ArrayList<>();
    if (projectVOMap == null) {
      return collect;
    }
    List<String> projectIds = new ArrayList<>();
    List<String> projList = JSON.parseArray(projPermissions, String.class);
    for (String proj : projList) {
      List<String> projIds = JSON.parseArray(proj, String.class);
      projectIds.add(projIds.get(projIds.size() - 1));
    }

    for (String projectId : projectIds) {
      collect.add(projectVOMap.get(projectId).getProjectName());
    }
    return collect;
  }

  @Override
  public List<ProjectVO> getProjectNames(List<String> projPermissionsList) {
    if(CollectionUtils.isEmpty(projPermissionsList)) {
      return new ArrayList<>();
    }
    List<ProjectVO> projectVOList = new ArrayList<>();
    List<String> projectIds = new ArrayList<>();
    for (String proj : projPermissionsList) {
      List<String> projList = JSON.parseArray(proj, String.class);
      for (String projs : projList) {
        List<String> lastOne = new ArrayList<>();
        List<String> projIds = JSON.parseArray(projs, String.class);
        //只查询最底层区域
        lastOne.add(projIds.get(projIds.size() - 1));
        projectIds.addAll(lastOne);
      }
    }
    projectIds = projectIds.stream().distinct().collect(Collectors.toList());
    List<String> groupBatchQuery = new ArrayList<>();
    int limitCount = 100;
    for (int i = 1; i <= projectIds.size(); i++) {
      groupBatchQuery.add(projectIds.get(i - 1));
      if (i % limitCount == 0) {
        projectVOList.addAll(projectMapper.getProjectListByIds(groupBatchQuery));
        groupBatchQuery.clear();
      }
      if (i == projectIds.size() && CollectionUtils.isNotEmpty(groupBatchQuery)) {
        projectVOList.addAll(projectMapper.getProjectListByIds(groupBatchQuery));
      }
    }
    return projectVOList;
  }

  @Override
  public ResultBody getCustomerList(List<BatchQueryCustomerBO> batchQueryCustomerBOList) {
    if (CollectionUtils.isEmpty(batchQueryCustomerBOList)) {
      return ResultBody.success(new ArrayList<>());
    }
    List<Map> list = new ArrayList<>();
    for (BatchQueryCustomerBO batchQueryCustomerBO : batchQueryCustomerBOList) {
      if ("clue".equals(batchQueryCustomerBO.getDataSource())) {
        Map map = new HashMap();
        map.put("projectClueId", batchQueryCustomerBO.getBusinessId());
        InformationVO informationVO = projectCluesService.cluesInformation(map);
        informationVO.setType(batchQueryCustomerBO.getType());
        list.add(toMap(informationVO));
      } else {
        Map map = new HashMap();
        map.put("opportunityClueId", batchQueryCustomerBO.getBusinessId());
        OppInformation projectCluesVO = projectCluesService.oppInformation(map);
        projectCluesVO.setType(batchQueryCustomerBO.getType());
        projectCluesVO.setPoolId(batchQueryCustomerBO.getPoolId());
        projectCluesVO.setPoolType(batchQueryCustomerBO.getPoolType());
        list.add(toMap(projectCluesVO));
      }
    }
    return ResultBody.success(list);
  }
  public Map<String, Object> toMap(Object dto) {
    Map<String, Object> map = new HashMap<>();
    Class<?> clazz = dto.getClass();
    try {
      for (Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        map.put(field.getName(), field.get(dto));
      }
    } catch (Exception e) {
      log.error("map转换异常");
    }
    return map;
  }
  private Map<String, List<ZsMapResBO>> getLineDistances(List<ZsMapResBO> zsMapResBOList) {
    Map<String, List<ZsMapResBO>> map = new HashMap<>();
    while (CollectionUtils.isNotEmpty(zsMapResBOList)) {
      Iterator<ZsMapResBO> iterator = zsMapResBOList.iterator();
      List<ZsMapResBO> resList = new ArrayList<>();
      ZsMapResBO zsMapResBO1 = iterator.next();
      resList.add(zsMapResBO1);
      iterator.remove();
      while (iterator.hasNext()) {
        ZsMapResBO zsMapResBO2 = iterator.next();
        double distance = getDistance(zsMapResBO1.getLatitude(), zsMapResBO1.getLongitude(),
            zsMapResBO2.getLatitude(), zsMapResBO2.getLongitude());
        if (distance < Double.parseDouble("100")) {
          resList.add(zsMapResBO2);
          iterator.remove();
        }
      }
      map.put(zsMapResBO1.getLatitude(), resList);
    }
    return map;
  }

  double getDistance(String lata, String lona, String latb, String lonb) {
    double lat1 = Double.parseDouble(lata);
    double lon1 = Double.parseDouble(lona);
    double lat2 = Double.parseDouble(latb);
    double lon2 = Double.parseDouble(lonb);
    double degToRad = Math.PI / 180.0;
    lat1 *= degToRad;
    lon1 *= degToRad;
    lat2 *= degToRad;
    lon2 *= degToRad;

    // 使用Haversine公式计算距离
    double dlon = lon2 - lon1;
    double dlat = lat2 - lat1;
    double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(
        Math.sin(dlon / 2), 2);
    double c = 2 * Math.asin(Math.sqrt(a));
    double radius = 6371; // 地球半径，单位为公里
    return radius * c;
  }

  private ZsMapRecordBO initZsMapRecordBO(ZsMapPermissionsBO permissionsBO, boolean flag,String userNames) {
    ZsMapRecordBO zsMapRecordBO = new ZsMapRecordBO();
    zsMapRecordBO.setRecordId(UUID.randomUUID().toString());
    Integer type = 1;
    if (flag) {
      type = 2;
    }
    zsMapRecordBO.setType(type);
    zsMapRecordBO.setUserNames(userNames);
    zsMapRecordBO.setPermissionsId(permissionsBO.getPermissionsId());
    zsMapRecordBO.setAreaPermissions(permissionsBO.getAreaPermissions());
    zsMapRecordBO.setAreaStartDate(permissionsBO.getAreaStartDate());
    zsMapRecordBO.setAreaEndDate(permissionsBO.getAreaEndDate());
    zsMapRecordBO.setProjPermissions(permissionsBO.getProjPermissions());
    zsMapRecordBO.setProjStartDate(permissionsBO.getProjStartDate());
    zsMapRecordBO.setProjEndDate(permissionsBO.getProjEndDate());
    zsMapRecordBO.setCreateBy(SecurityUtils.getUserId());
    zsMapRecordBO.setCreateTime(new Date());
    zsMapRecordBO.setUpdateBy(SecurityUtils.getUserId());
    zsMapRecordBO.setUpdateTime(new Date());
    zsMapRecordBO.setIsDel(0);
    return zsMapRecordBO;
  }

  @Override
  public ResultBody updateZsmapDraw(ZsMapDrawBO zsMapDrawBO) {
    if (StringUtils.isEmpty(zsMapDrawBO.getName()) || StringUtils.isEmpty(zsMapDrawBO.getColor()) || StringUtils.isEmpty(zsMapDrawBO.getDrawId())) {
      return ResultBody.error(-10002, "参数错误");
    }
    zsMapDrawBO.setUpdateBy(SecurityUtils.getUserId());
    zsMapDrawBO.setUpdateTime(new Date());
    return ResultBody.success(zsMapDrawDao.updateZsmapDraw(zsMapDrawBO));
  }

  /**
   * 检查新增的面标记是否与项目下已存在的面标记重叠
   * @param newPolygon 新增的面标记
   * @return ResultBody，code=200表示不重叠，code!=200表示重叠
   */
  private ResultBody checkPolygonOverlap(ZsMapDrawBO newPolygon) {
    try {
      // 查询项目下所有已存在的面标记（type=3和type=4）
      ZsMapDrawQueryBO queryBO = new ZsMapDrawQueryBO();
      List<String> projectIds = new ArrayList<>();
      projectIds.add(newPolygon.getProjectId());
      queryBO.setProjectIdS(projectIds);
      
      // 查询type=3和type=4的记录
      List<String> types = new ArrayList<>();
      types.add("3");
      types.add("4");
      queryBO.setTypes(types);
      
      List<ZsMapDrawResBO> existingPolygons = zsMapDrawDao.select(queryBO);
      
      if (CollectionUtils.isEmpty(existingPolygons)) {
        return ResultBody.success("无重叠"); // 没有已存在的面标记，不重叠
      }
      
      // 根据新增标记的类型解析坐标
      List<List<Point>> newPolygonGroups = parseLatLonByType(newPolygon.getLatLon(), newPolygon.getType());
      if (CollectionUtils.isEmpty(newPolygonGroups)) {
        return ResultBody.error(-10_0000, "经纬度坐标格式错误！");
      }
      
      // 检查与每个已存在面标记是否重叠
      for (ZsMapDrawResBO existingPolygon : existingPolygons) {
        if (StringUtils.isNotEmpty(existingPolygon.getLatLon())) {
          // 解析已存在标记的坐标（根据其类型）
          List<List<Point>> existingPolygonGroups = parseLatLonByType(existingPolygon.getLatLon(), existingPolygon.getType());
          if (!CollectionUtils.isEmpty(existingPolygonGroups)) {
            // 检查新增标记的每个区域与已存在标记的每个区域是否重叠
            for (List<Point> newGroup : newPolygonGroups) {
              for (List<Point> existingGroup : existingPolygonGroups) {
                if (checkPolygonOverlap(newGroup, existingGroup)) {
                  return ResultBody.error(-10_0000, "新增的面标记与已存在的面标记范围重叠，请调整坐标范围！");
                }
              }
            }
          }
        }
      }
      
      return ResultBody.success("无重叠"); // 没有重叠
    } catch (Exception e) {
      // 如果检查过程中出现异常，记录日志但不阻止保存
      log.error("检查面标记重叠时发生异常", e);
      return ResultBody.success("检查异常，允许保存");
    }
  }

  /**
   * 根据类型解析经纬度字符串为坐标点组列表
   * @param latLon 经纬度字符串
   * @param type 类型：3=单区域，4=多区域（多个type=3构成的数组）
   * @return 坐标点组列表，每个组是一个区域的坐标点列表
   */
  private List<List<Point>> parseLatLonByType(String latLon, Integer type) {
    List<List<Point>> result = new ArrayList<>();
    
    if (type == 3) {
      // type=3：单区域，直接解析为一个区域
      // 格式：[{"lng":116.123,"lat":39.456},{"lng":116.124,"lat":39.457}]
      List<Point> points = parseLatLonToPointsNew(latLon);
      if (!CollectionUtils.isEmpty(points)) {
        result.add(points);
      }
    } else if (type == 4) {
      // type=4：多区域，是多个type=3构成的数组
      // 格式：[[{"lng":116.123,"lat":39.456},{"lng":116.124,"lat":39.457}],[{"lng":116.125,"lat":39.458},{"lng":116.126,"lat":39.459}]]
      result = parseMultiRegionLatLonNew(latLon);
    }
    
    return result;
  }

  /**
   * 解析多区域经纬度字符串（type=4格式）
   * @param latLon 多区域经纬度字符串，格式：[[{"lng":116.123,"lat":39.456},{"lng":116.124,"lat":39.457}],[{"lng":116.125,"lat":39.458},{"lng":116.126,"lat":39.459}]]
   * @return 坐标点组列表
   */
  private List<List<Point>> parseMultiRegionLatLonNew(String latLon) {
    List<List<Point>> result = new ArrayList<>();
    
    if (StringUtils.isEmpty(latLon)) {
      return result;
    }
    
    try {
      // 解析外层JSON数组
      JSONArray outerArray = JSON.parseArray(latLon);
      if (outerArray != null) {
        for (int i = 0; i < outerArray.size(); i++) {
          // 每个元素是一个type=3格式的JSON数组
          String regionJson = outerArray.getString(i);
          if (StringUtils.isNotEmpty(regionJson)) {
            List<Point> points = parseLatLonToPointsNew(regionJson);
            if (!CollectionUtils.isEmpty(points)) {
              result.add(points);
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("解析多区域经纬度失败", e);
    }
    
    return result;
  }

  /**
   * 解析经纬度字符串为坐标点列表
   * @param latLon 经纬度字符串，格式如："116.123,39.456;116.124,39.457;116.125,39.458"
   * @return 坐标点列表
   */
  private List<Point> parseLatLonToPoints(String latLon) {
    List<Point> points = new ArrayList<>();
    
    if (StringUtils.isEmpty(latLon)) {
      return points;
    }
    
    try {
      // 假设经纬度格式为：经度,纬度;经度,纬度;经度,纬度
      String[] coordinatePairs = latLon.split(";");
      
      for (String pair : coordinatePairs) {
        if (StringUtils.isNotEmpty(pair)) {
          String[] coordinates = pair.trim().split(",");
          if (coordinates.length == 2) {
            double longitude = Double.parseDouble(coordinates[0].trim());
            double latitude = Double.parseDouble(coordinates[1].trim());
            points.add(new Point(longitude, latitude));
          }
        }
      }
    } catch (Exception e) {
      log.error("解析经纬度坐标失败: {}", latLon, e);
    }
    
    return points;
  }

  /**
   * 检查两个多边形是否重叠
   * 使用简化的重叠检测算法：检查边界框是否重叠，然后检查是否有交点
   * @param polygon1 多边形1的坐标点列表
   * @param polygon2 多边形2的坐标点列表
   * @return true表示重叠，false表示不重叠
   */
  private boolean checkPolygonOverlap(List<Point> polygon1, List<Point> polygon2) {
    if (CollectionUtils.isEmpty(polygon1) || CollectionUtils.isEmpty(polygon2)) {
      return false;
    }
    
    // 1. 检查边界框是否重叠（快速预检）
    if (!checkBoundingBoxOverlap(polygon1, polygon2)) {
      return false;
    }
    
    // 2. 检查是否有交点（简化算法）
    return checkPolygonIntersection(polygon1, polygon2);
  }

  /**
   * 检查两个多边形的边界框是否重叠
   */
  private boolean checkBoundingBoxOverlap(List<Point> polygon1, List<Point> polygon2) {
    if (CollectionUtils.isEmpty(polygon1) || CollectionUtils.isEmpty(polygon2)) {
      return false;
    }
    
    // 计算边界框
    double minX1 = polygon1.stream().mapToDouble(p -> p.x).min().orElse(0);
    double maxX1 = polygon1.stream().mapToDouble(p -> p.x).max().orElse(0);
    double minY1 = polygon1.stream().mapToDouble(p -> p.y).min().orElse(0);
    double maxY1 = polygon1.stream().mapToDouble(p -> p.y).max().orElse(0);
    
    double minX2 = polygon2.stream().mapToDouble(p -> p.x).min().orElse(0);
    double maxX2 = polygon2.stream().mapToDouble(p -> p.x).max().orElse(0);
    double minY2 = polygon2.stream().mapToDouble(p -> p.y).min().orElse(0);
    double maxY2 = polygon2.stream().mapToDouble(p -> p.y).max().orElse(0);
    
    // 检查边界框是否重叠
    return !(maxX1 < minX2 || maxX2 < minX1 || maxY1 < minY2 || maxY2 < minY1);
  }

  /**
   * 检查两个多边形是否有交点（简化算法）
   * 这里使用一个简化的检测方法：检查一个多边形的点是否在另一个多边形内部
   */
  private boolean checkPolygonIntersection(List<Point> polygon1, List<Point> polygon2) {
    // 检查polygon1的点是否在polygon2内部
    for (Point point : polygon1) {
      if (isPointInPolygon(point, polygon2)) {
        return true;
      }
    }
    
    // 检查polygon2的点是否在polygon1内部
    for (Point point : polygon2) {
      if (isPointInPolygon(point, polygon1)) {
        return true;
      }
    }
    
    // 检查边是否相交（简化版本）
    return checkEdgesIntersect(polygon1, polygon2);
  }

  /**
   * 检查点是否在多边形内部（射线法）
   */
  private boolean isPointInPolygon(Point point, List<Point> polygon) {
    if (CollectionUtils.isEmpty(polygon) || polygon.size() < 3) {
      return false;
    }
    
    boolean inside = false;
    int j = polygon.size() - 1;
    
    for (int i = 0; i < polygon.size(); i++) {
      if (((polygon.get(i).y > point.y) != (polygon.get(j).y > point.y)) &&
          (point.x < (polygon.get(j).x - polygon.get(i).x) * (point.y - polygon.get(i).y) / 
           (polygon.get(j).y - polygon.get(i).y) + polygon.get(i).x)) {
        inside = !inside;
      }
      j = i;
    }
    
    return inside;
  }

  /**
   * 检查两个多边形的边是否相交（简化版本）
   */
  private boolean checkEdgesIntersect(List<Point> polygon1, List<Point> polygon2) {
    // 这里可以实现更复杂的边相交检测算法
    // 为了简化，我们只检查边界框重叠
    return true; // 如果边界框重叠，认为可能相交
  }

  /**
   * 解析新的经纬度格式：JSON字符串包含lng和lat字段
   * 格式示例：[{"lng":107.668525,"lat":35.455211},{"lng":107.475354,"lat":34.390711}]
   * @param latLon 经纬度JSON字符串
   * @return 坐标点列表
   */
  private List<Point> parseLatLonToPointsNew(String latLon) {
    List<Point> points = new ArrayList<>();
    
    if (StringUtils.isEmpty(latLon)) {
      return points;
    }
    
    try {
      // 首先尝试解析为JSON数组
      Object parsed = JSON.parse(latLon);
      
      if (parsed instanceof JSONArray) {
        // 如果是JSON数组，直接处理
        JSONArray jsonArray = (JSONArray) parsed;
        for (int i = 0; i < jsonArray.size(); i++) {
          Object item = jsonArray.get(i);
          if (item instanceof JSONObject) {
            JSONObject coordinate = (JSONObject) item;
            Double lng = coordinate.getDouble("lng");
            Double lat = coordinate.getDouble("lat");
            if (lng != null && lat != null) {
              points.add(new Point(lng, lat));
            }
          }
        }
      } else if (parsed instanceof JSONObject) {
        // 如果是单个JSON对象，包装成数组处理
        JSONObject coordinate = (JSONObject) parsed;
        Double lng = coordinate.getDouble("lng");
        Double lat = coordinate.getDouble("lat");
        if (lng != null && lat != null) {
          points.add(new Point(lng, lat));
        }
      }
    } catch (Exception e) {
      log.error("解析新格式经纬度坐标失败: {}", latLon, e);
    }
    
    return points;
  }

  /**
   * 坐标点类
   */
  private static class Point {
    double x; // 经度
    double y; // 纬度
    
    Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  @Override
  public GridDistributionResBO getGridDistribution(GridDistributionQueryBO gridDistributionQueryBO) {
    try {
      // 1. 构建查询条件，只查询type为3和4的地图绘制
      ZsMapDrawQueryBO queryBO = new ZsMapDrawQueryBO();
      queryBO.setStartTime(gridDistributionQueryBO.getStartTime());
      queryBO.setEndTime(gridDistributionQueryBO.getEndTime());
      //queryBO.setCreateUser(gridDistributionQueryBO.getCreateUser());
      
      // 设置查询type为3和4
      List<String> types = new ArrayList<>();
      types.add("3");
      types.add("4");
      queryBO.setTypes(types);
      
      // 2. 查询地图绘制数据
      //List<ZsMapDrawResBO> drawList = getZsMapDraw(queryBO);
      List<ZsMapDrawResBO> drawList = zsMapDrawDao.select(queryBO);
      if (CollectionUtils.isEmpty(drawList)) {
        GridDistributionResBO emptyResult = new GridDistributionResBO();
        emptyResult.setProvinceCount(0);
        emptyResult.setCityCount(0);
        emptyResult.setTotalGridCount(0);
        emptyResult.setProvinces(new ArrayList<>());
        return emptyResult;
      }
      
      // 3. 统计各省市的绘制数量
      Map<String, Map<String, Integer>> provinceCityCountMap = new HashMap<>();
      
      for (ZsMapDrawResBO draw : drawList) {
        if (StringUtils.isNotEmpty(draw.getLatLon())) {
          String address = draw.getAddress();
          String[] locationInfo = null;
          
          // 先判断address是否有值，如果有值就直接使用，如果没有值才走计算中心点位的逻辑
          if (StringUtils.isNotEmpty(address)) {
            // 直接使用已有的address
            locationInfo = parseAddress(address);
          } else {
            // 计算区域中心点
            Point centerPoint = calculateCenterPoint(draw.getLatLon(), draw.getType());
            if (centerPoint != null) {
              // 通过中心点获取省市区信息
              address = getAddressFromCoordinates(centerPoint.y, centerPoint.x);
              if (StringUtils.isNotEmpty(address)) {
                locationInfo = parseAddress(address);
                
                // 更新draw的address字段，节省下次查询时间
                draw.setAddress(address);
                // 将计算出的address保存到数据库，节省下次查询时间
                try {
                  zsMapDrawDao.updateAddress(draw.getDrawId(), address);
                } catch (Exception e) {
                  log.warn("更新绘制区域地址失败，drawId: {}, address: {}", draw.getDrawId(), address, e);
                }
              }
            }
          }
          
          // 统计省份和城市数量
          if (locationInfo != null && locationInfo.length >= 2) {
            String province = locationInfo[0];
            String city = locationInfo[1];
            
            provinceCityCountMap.computeIfAbsent(province, k -> new HashMap<>());
            Map<String, Integer> cityCountMap = provinceCityCountMap.get(province);
            cityCountMap.put(city, cityCountMap.getOrDefault(city, 0) + 1);
          }
        }
      }
      
      // 4. 构建返回结果
      List<GridDistributionResBO.ProvinceInfo> provinceList = new ArrayList<>();
      int totalCityCount = 0;
      
      for (Map.Entry<String, Map<String, Integer>> provinceEntry : provinceCityCountMap.entrySet()) {
        GridDistributionResBO.ProvinceInfo provinceInfo = new GridDistributionResBO.ProvinceInfo();
        provinceInfo.setProvince(provinceEntry.getKey());
        
        List<GridDistributionResBO.CityInfo> cityList = new ArrayList<>();
        int provinceTotal = 0;
        
        for (Map.Entry<String, Integer> cityEntry : provinceEntry.getValue().entrySet()) {
          GridDistributionResBO.CityInfo cityInfo = new GridDistributionResBO.CityInfo();
          cityInfo.setCityName(cityEntry.getKey());
          cityInfo.setCount(cityEntry.getValue());
          cityList.add(cityInfo);
          provinceTotal += cityEntry.getValue();
          totalCityCount++; // 统计地级市总数（网格覆盖的城市数量）
        }
        
        provinceInfo.setCities(cityList);
        provinceInfo.setTotalCount(provinceTotal);
        provinceList.add(provinceInfo);
      }
      
      // 按省份总数量降序排序
      provinceList.sort((a, b) -> b.getTotalCount().compareTo(a.getTotalCount()));
      
      // 构建最终返回结果
      GridDistributionResBO result = new GridDistributionResBO();
      result.setProvinceCount(provinceCityCountMap.size()); // 省份总数（网格覆盖的省份数量）
      result.setCityCount(totalCityCount); // 地级市总数（网格覆盖的城市数量）
      result.setTotalGridCount(drawList.size()); // 总网格数量 - 直接使用查询结果的size
      result.setProvinces(provinceList); // 省份分布列表
      
      return result;
      
    } catch (Exception e) {
      log.error("获取网格分布统计失败", e);
      GridDistributionResBO errorResult = new GridDistributionResBO();
      errorResult.setProvinceCount(0);
      errorResult.setCityCount(0);
      errorResult.setTotalGridCount(0);
      errorResult.setProvinces(new ArrayList<>());
      return errorResult;
    }
  }

  @Override
  public void exportGridDistributionWord(HttpServletResponse response, GridDistributionQueryBO gridDistributionQueryBO) {
    try {
      // 1. 获取网格分布统计数据
      GridDistributionResBO gridData = getGridDistribution(gridDistributionQueryBO);
      
      // 2. 生成Word文档
      generateGridDistributionWord(response, gridData, gridDistributionQueryBO);
      
    } catch (Exception e) {
      log.error("导出网格分布统计Word报告失败", e);
      throw new RuntimeException("导出Word报告失败：" + e.getMessage());
    }
  }

  /**
   * 生成网格分布统计Word文档
   */
  private void generateGridDistributionWord(HttpServletResponse response, GridDistributionResBO gridData, GridDistributionQueryBO queryBO) {
    try {
      // 设置响应头
      String fileName = "网格分布统计报告_" + queryBO.getStartTime() + "_" + queryBO.getEndTime() + ".docx";
      response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
      response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
      response.setCharacterEncoding("UTF-8");
      
      // 创建Word文档内容
      StringBuilder wordContent = new StringBuilder();
      
      // 标题
      wordContent.append("网格分布统计\n\n");
      
      // 统计时间范围和总体统计
      wordContent.append(queryBO.getStartTime()).append(" - ").append(queryBO.getEndTime())
                 .append(" 绘制产业地图").append(gridData.getTotalGridCount()).append("份，覆盖")
                 .append(gridData.getProvinceCount()).append("个省份，").append(gridData.getCityCount()).append("个地级市。\n\n");
      
      // 详细分布
      wordContent.append("具体如下：\n");
      
      for (GridDistributionResBO.ProvinceInfo province : gridData.getProvinces()) {
        wordContent.append(province.getProvince()).append("（").append(province.getTotalCount()).append("）：");
        
        List<String> cityNames = new ArrayList<>();
        for (GridDistributionResBO.CityInfo city : province.getCities()) {
          cityNames.add(city.getCityName());
        }
        wordContent.append(String.join("、", cityNames)).append("。\n");
      }
      
      // 将内容写入响应流
      byte[] contentBytes = wordContent.toString().getBytes("UTF-8");
      response.setContentLength(contentBytes.length);
      response.getOutputStream().write(contentBytes);
      response.getOutputStream().flush();
      response.getOutputStream().close();
      
    } catch (Exception e) {
      log.error("生成Word文档失败", e);
      throw new RuntimeException("生成Word文档失败：" + e.getMessage());
    }
  }
  
  /**
   * 计算区域中心点
   * @param latLon 经纬度字符串
   * @param type 绘制类型（3-单区域，4-多区域）
   * @return 中心点坐标
   */
  private Point calculateCenterPoint(String latLon, Integer type) {
    try {
      List<Point> points = new ArrayList<>();
      
      if (type == 3) {
        // 单区域：解析一组经纬度坐标
        points = parseLatLonToPointsNew(latLon);
      } else if (type == 4) {
        // 多区域：解析多组经纬度坐标，计算所有区域的中心点
        List<List<Point>> multiRegionPoints = parseMultiRegionLatLonNew(latLon);
        if (!CollectionUtils.isEmpty(multiRegionPoints)) {
          // 将所有区域的点合并到一个列表中
          for (List<Point> regionPoints : multiRegionPoints) {
            points.addAll(regionPoints);
          }
        }
      }
      
      if (CollectionUtils.isEmpty(points)) {
        return null;
      }
      
      // 计算中心点（简单平均）
      double sumX = 0, sumY = 0;
      for (Point point : points) {
        sumX += point.x;
        sumY += point.y;
      }
      
      return new Point(sumX / points.size(), sumY / points.size());
      
    } catch (Exception e) {
      log.error("计算中心点失败: {}", latLon, e);
      return null;
    }
  }
  
  /**
   * 通过经纬度获取地址信息
   * @param lat 纬度
   * @param lng 经度
   * @return 地址字符串
   */
  private String getAddressFromCoordinates(double lat, double lng) {
    try {
      // 使用百度地图API进行逆地理编码
      return AMapUtils.longitudeToAddress((float) lat, (float) lng);
    } catch (Exception e) {
      log.error("获取地址信息失败: lat={}, lng={}", lat, lng, e);
      return null;
    }
  }
  
  /**
   * 解析地址字符串，提取省市区信息
   * @param address 完整地址
   * @return [省份, 城市, 区县]
   */
  private String[] parseAddress(String address) {
    if (StringUtils.isEmpty(address)) {
      return new String[0];
    }
    
    try {
      // 使用现有的地址解析方法
      String regex = "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(address);
      
      if (matcher.find()) {
        String province = matcher.group("province");
        String city = matcher.group("city");
        String county = matcher.group("county");
        
        // 处理直辖市
        if ("北京市".equals(province) || "天津市".equals(province) || 
            "上海市".equals(province) || "重庆市".equals(province)) {
          return new String[]{province, province, county};
        }
        
        return new String[]{province, city, county};
      }
    } catch (Exception e) {
      log.error("解析地址失败: {}", address, e);
    }
    
    return new String[0];
  }
}
