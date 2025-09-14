package cn.visolink.system.builddynamic.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.BaseResultCodeEnum;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.builddynamic.mapper.BuildDynamicMapper;
import cn.visolink.system.builddynamic.model.BuildBookDynamic;
import cn.visolink.system.builddynamic.service.BuildDynamicService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.CessException;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/12/18
 */
@Service
public class BuildDynamicServiceImpl implements BuildDynamicService {

    @Autowired
    private BuildDynamicMapper buildDynamicMapper;

    @Autowired
    private ExcelImportMapper excelImportMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private AuthMapper authMapper;

    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public ResultBody saveBuildBookDynamic(BuildBookDynamic buildBookDynamic) {
        if(StringUtils.isBlank(buildBookDynamic.getBuildDynamicName()) ||
           StringUtils.isBlank(buildBookDynamic.getBuildDynamicDesc()) ||
           StringUtils.isBlank(buildBookDynamic.getProjectId()) ||
           StringUtils.isBlank(buildBookDynamic.getBuildBookId()) ||
           StringUtils.isBlank(buildBookDynamic.getDynamicCode())){
           return ResultBody.error(2003,"参数错误");
        }
        if("1".equals(buildBookDynamic.getIsJump())){
            if("1".equals(buildBookDynamic.getJumpType())){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("newsId",buildBookDynamic.getJumpPageId());
                buildBookDynamic.setJumpParam(jsonObject.toJSONString());
            }else if("2".equals(buildBookDynamic.getJumpType())){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("houseTypeId",buildBookDynamic.getJumpPageId());
                jsonObject.put("buildBookID",buildBookDynamic.getBuildBookId());
                buildBookDynamic.setJumpParam(jsonObject.toJSONString());
            }else if("3".equals(buildBookDynamic.getJumpType())){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("actId",buildBookDynamic.getJumpPageId());
                buildBookDynamic.setJumpParam(jsonObject.toJSONString());
            }
        }
        buildBookDynamic.setCreateTime(this.getNewDate());
        buildBookDynamic.setIsDel("0");
        buildBookDynamic.setStatus("1");
        buildBookDynamic.setReleaseStatus("2");
        buildDynamicMapper.saveBuildBookDynamic(buildBookDynamic);
        return ResultBody.success("成功");
    }

    @Override
    public ResultBody getBuildBookDynamicList(Map map) {
        int pageIndex = MapUtils.getInteger(map,"pageIndex",0);
        int pageSize = MapUtils.getInteger(map,"pageSize",10);
        String statusStr = MapUtils.getString(map,"statusList","");
        if(StringUtils.isNoneBlank(statusStr)){
            String[] strList = statusStr.split(",");
            List<String> releaseStatusList = new ArrayList<>();
            List<String> statusList = new ArrayList<>();
            for (String str : strList){
                if("3".equals(str)){
                    statusList.add("0");
                }else{
                    releaseStatusList.add(str);
                }
            }
            map.put("statusList",statusList);
            map.put("releaseStatusList",releaseStatusList);
        }
        PageHelper.startPage(pageIndex, pageSize);
        List list = buildDynamicMapper.getBuildBookDynamicList(map);
        PageInfo<Object> pageInfo = new PageInfo<>(list);
        return ResultBody.success(pageInfo);
    }

    @Override
    public ResultBody editBuildBookDynamic(BuildBookDynamic buildBookDynamic) {
        if(StringUtils.isBlank(buildBookDynamic.getId())){
            return ResultBody.error(2003,"参数错误");
        }
        if("1".equals(buildBookDynamic.getIsJump())){
            if("1".equals(buildBookDynamic.getJumpType())){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("newsId",buildBookDynamic.getJumpPageId());
                buildBookDynamic.setJumpParam(jsonObject.toJSONString());
            }else if("2".equals(buildBookDynamic.getJumpType())){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("houseTypeId",buildBookDynamic.getJumpPageId());
                jsonObject.put("buildBookID",buildBookDynamic.getBuildBookId());
                buildBookDynamic.setJumpParam(jsonObject.toJSONString());
            }else if("3".equals(buildBookDynamic.getJumpType())){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("actId",buildBookDynamic.getJumpPageId());
                buildBookDynamic.setJumpParam(jsonObject.toJSONString());
            }
        }
        if("0".equals(buildBookDynamic.getStatus())){
            buildBookDynamic.setDisableTime(this.getNewDate());
        }
        buildBookDynamic.setEditTime(this.getNewDate());
        return ResultBody.success(buildDynamicMapper.editBuildBookDynamic(buildBookDynamic));
    }

    @Override
    public ResultBody getBuildListByPojId(String projectId) {
        if (projectId!=null){
            String[] ids = projectId.split(",");
            return ResultBody.success(buildDynamicMapper.getBuildListByPojId(Arrays.asList(ids)));
        }else{
            return ResultBody.error(-130000,"必传参数未传！");
        }


    }

    @Override
    public ResultBody getBuildDynamicDictList(Map map) {
        if(StringUtils.isNoneBlank(MapUtils.getString(map,"childCodeStr",""))){
            String childCodeStr = MapUtils.getString(map,"childCodeStr");
            String[] childCodeList = childCodeStr.split(",");
            map.put("childCodeList", Arrays.asList(childCodeList));
        }
        return ResultBody.success(buildDynamicMapper.getBuildDynamicDictList(map));
    }

    @Override
    public ResultBody getJumpTypeDataList(Map map) {
        if(StringUtils.isBlank(MapUtils.getString(map,"type","")) ||
           StringUtils.isBlank(MapUtils.getString(map,"jumpId",""))){
            return ResultBody.error(2003,"参数错误");
        }
        String type = MapUtils.getString(map,"type");
        String jumpId = MapUtils.getString(map,"jumpId");
        List<Map> mapList = new ArrayList<>();
        if("1".equals(type)){
            mapList = buildDynamicMapper.getNewsListByCityId(jumpId);
        }else if("2".equals(type)){
            mapList = buildDynamicMapper.getHouseListByBookId(jumpId);
        }else if("3".equals(type)){
            mapList = buildDynamicMapper.getActivityListByPojId(jumpId);
        }
        return ResultBody.success(mapList);
    }

    @Override
    public ResultBody getBuildDynamicById(String id) {
        BuildBookDynamic buildBookDynamic = buildDynamicMapper.getBuildBookDynamicById(id);
        if(buildBookDynamic != null){
            List<String> projectList = new ArrayList<>();
            projectList.add(buildBookDynamic.getProjectAreaId());
            projectList.add(buildBookDynamic.getProjectId());
            buildBookDynamic.setProjectIdList(projectList);
        }
        return ResultBody.success(buildBookDynamic);
    }

    @Override
    public void getBuildDynamicExcel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param) {
        String userName = MapUtils.getString(param, "UserName");
        String userId = MapUtils.getString(param, "UserId");
        if ( StrUtil.isBlank(userName) || StrUtil.isBlank(userId) ) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        //记录导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = System.currentTimeMillis();
        excelExportLog.setCreator(userId);
        excelExportLog.setId(id);
        excelExportLog.setMainType("1");
        excelExportLog.setMainTypeDesc("渠道管理");
        excelExportLog.setSubType("Q3");
        excelExportLog.setSubTypeDesc("大客户经理活动台账");
        excelExportLog.setIsAsyn("0");
        excelExportLog.setExportType("3");

        List<String> projectIdList = (List<String>) param.get("projectIdList");
        if(projectIdList != null && projectIdList.size()>0){
            Map proMap = excelImportMapper.getAreaNameAndProNames(projectIdList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }else{
            Map userInfoMap = authMapper.mGetUserInfo(param);
            List<String> fullpath = projectMapper.findFullPath(param);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sbs.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sbs.append("or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName( userName+"","",userInfoMap.get("JobCode").toString(),sbs.toString());
            List<String> proIdList = new ArrayList<>();
            if (mapList!=null && mapList.size()>0){
                for (Map proMap:mapList) {
                    proIdList.add(proMap.get("projectId")+"");
                }
            }
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        //导出的文档下面的名字
        String excelName = "楼盘动态台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<BuildBookDynamic> list;
        try {
            String statusStr = MapUtils.getString(param, "statusList", "");
            if (StringUtils.isNoneBlank(statusStr)) {
                String[] strList = statusStr.split(",");
                List<String> releaseStatusList = new ArrayList<>();
                List<String> statusList = new ArrayList<>();
                for (String str : strList) {
                    if ("3".equals(str)) {
                        statusList.add("0");
                    } else {
                        releaseStatusList.add(str);
                    }
                }
                param.put("statusList", statusList);
                param.put("releaseStatusList", releaseStatusList);
            }
            list = buildDynamicMapper.getBuildBookDynamicList(param);
            String []  headers = new BuildBookDynamic().courtCaseTitle;
            for (BuildBookDynamic model : list) {
                Object[] oArray = model.toBuildBookDynamicData();
                dataset.add(oArray);
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            excelExportUtil.exportExcel(excelName,headers,dataset,excelName,response,null);
            //记录导出日志
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            excelExportLog.setIsDown("1");
            Long export = System.currentTimeMillis();
            Long exporttime = export-nowtime;
            String exportTime = df.format(Double.valueOf(exporttime+"")/1000);
            excelExportLog.setWaitTime("0");
            excelExportLog.setExportTime(exportTime);
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }catch (Exception e){
            e.printStackTrace();
            excelExportLog.setExportStatus("3");
            excelExportLog.setExceptionMessage(e.getMessage());
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }
    }

    /***
     *
     * @param
     *@return {}
     *@throws
     *@Description: 获取当前时间
     *@author FuYong
     *@date 2020/12/14 11:43
     */
    public String getNewDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
