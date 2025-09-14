package cn.visolink.system.visitandcallexcel.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.BadRequestException;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.visitandcallexcel.dao.ProjectProtectRuleDao;
import cn.visolink.system.visitandcallexcel.dao.VisitAndCallExcelDao;
import cn.visolink.system.visitandcallexcel.model.ExcelModelVisitAndCall;
import cn.visolink.system.visitandcallexcel.model.ProjectProtectRule;
import cn.visolink.system.visitandcallexcel.service.VisitAndCallExcelService;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.models.auth.In;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Struct;
import java.util.*;


/**
 * 客户来访，来电，去电台账导出
 *
 * @author ligengying
 * @date 20191119
 */
@Service
public class VisitAndCallExcelServiceImpl implements VisitAndCallExcelService {

    @Autowired
    private VisitAndCallExcelDao excelMapper;

    @Autowired
    private ProjectProtectRuleDao ruleDao;

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private AuthMapper authMapper;

    /**
     * 导出来访，来电，去电台账
     *
     * @param params
     */
    @Override
    public void getDownloadVisitAndCallInfo(HttpServletRequest request, HttpServletResponse response, String params) {
        String authCompanyId = request.getHeader("AuthCompanyID");
//        JSONObject param= JSON.parseObject(params);
        Map map = JSONObject.parseObject(params, Map.class);
        String timeCode = "";
        if (!String.valueOf(map.get("beginTime")).equals("1949-01-01")) {
            timeCode = String.valueOf(map.get("beginTime"));
            if (!String.valueOf(map.get("beginTime")).equals(String.valueOf(map.get("endTime")))) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(DateUtil.format(DateUtil.parse(String.valueOf(map.get("beginTime"))), "yyyyMMdd")).append("-").append(DateUtil.format(DateUtil.parse(String.valueOf(map.get("endTime"))), "yyyyMMdd"));
                timeCode = stringBuilder.toString();
            }
        }
        map.put("beginTime", DateUtil.beginOfDay(DateUtil.parse(String.valueOf(map.get("beginTime")))));
        map.put("endTime", DateUtil.endOfDay(DateUtil.parse(String.valueOf(map.get("endTime")))));

        //查询场景：来访，来电,去电
        String selectCode = String.valueOf(map.get("selectCode"));
        //隐号查询，全号查询
        String statusCode = String.valueOf(map.get("statusCode"));
        if (StrUtil.isBlank(statusCode)) {
            throw new BadRequestException(-11_0001, "请选择导出的手机号类型！");
        }
        //项目id
        String projectId = String.valueOf(map.get("projectId"));
        //来访查询新访，复访，全部
        String code = String.valueOf(map.get("code"));
        if (StrUtil.isBlank(projectId)) {
            throw new BadRequestException(-11_0002, "请选择项目!");
        }
        Map mapUser=new HashMap();
        mapUser.put("UserName", map.get("userName"));
        Map userInfoMap = authMapper.mGetUserInfo(mapUser);
        List<String> fullpath = projectMapper.findFullPath(mapUser);
        StringBuffer sbs = new StringBuffer();
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sbs.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sbs.append("or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        //获取有权限的项目
        List<Map> mapLists = projectMapper.findProjectListByUserName( map.get("userName")+"","",userInfoMap.get("JobCode").toString(),sbs.toString());
        List<String> proIdList = new ArrayList<>();
        if (mapLists!=null && mapLists.size()>0){
            for (Map proMap:mapLists) {
                proIdList.add(proMap.get("projectId")+"");
            }
        }

        String[] split = projectId.split(",");
        //判断是否有权限查询这些项目
        List<String> arrayToList= Arrays.asList(split);
        List<String> arrayList=new ArrayList<String>(arrayToList);//转换为ArrayLsit调用相关的remove方法
        //将无权限的删除
        for (int f = 0;f <arrayList.size();f++) {
            if (!proIdList.contains(arrayList.get(f))){
                arrayList.remove(f);
                f--;
            }
        }
        String[] strings = new String[arrayList.size()];
        arrayList.toArray(strings);
        String proIds = "'"+ StringUtils.join(strings,"','")+"'";
        map.put("projectId", proIds);

        String projectName = "";
        String basePath = "templates";
        String templatePath = "";
        ArrayList<Object[]> dataset = new ArrayList<>();

        try {
            //导出的文档下面的名字
            String excelName = null;
            List<ExcelModelVisitAndCall> mapList = new ArrayList<>();

            //项目名称
            List<Map> projectNameList = excelMapper.getProjectNameList(proIds);
            if (projectNameList.size() == 1) {
                Map map1 = projectNameList.get(0);
                projectName = String.valueOf(map1.get("projectName"));
            } else {
                boolean isFlag = true;
                Map map1 = projectNameList.get(0);
                String areaName = String.valueOf(map1.get("areaName"));
                //是否存在多个区域
                for (int i = 1; i <= projectNameList.size() - 1; i++) {

                    Map map2 = projectNameList.get(i);
                    String areaName1 = String.valueOf(map2.get("areaName"));
                    if (!areaName.equals(areaName1)) {
                        isFlag = false;
                    }
                }
                //多个项目都是一个区域
                if (isFlag) {
                    projectName = String.valueOf(projectNameList.get(0).get("areaName"));
                }
            }
            String countInfo = "";
            if ("visit".equals(selectCode)) {
                if ((Integer.parseInt(String.valueOf(map.get("max"))) == Integer.parseInt(String.valueOf(map.get("min")))) && "0".equals(String.valueOf(map.get("min")))) {
                    map.put("max", null);
                    map.put("min", null);
                }
                if ("newVisit".equals(code)) {

                    excelName = projectName + "新访台账明细";
                    mapList = excelMapper.getExcelNewVisitList(map);
                    countInfo = "新访统计" + mapList.size() + "组";
                } else if ("oldVisit".equals(code)) {

                    excelName = projectName + "复访台账明细";
                    mapList = excelMapper.getExcelOldVisitList(map);
                    countInfo = "复访统计" + mapList.size() + "组";
                } else {

                    excelName = projectName + "来访台账明细";
                    mapList = excelMapper.getExcelAllVisitList(map);
                    int newVisitCount = 0;
                    for (ExcelModelVisitAndCall map1 : mapList) {
                        String customerLB = map1.getCustomerLB();
                        if ("新访".equals(customerLB)) {
                            newVisitCount++;
                        }
                    }
                    countInfo = "来访共计" + mapList.size() + "组，新访" + newVisitCount + "组，复访" + (mapList.size() - newVisitCount) + "组";
                }
            } else if ("comeCall".equals(selectCode)) {

                excelName = projectName + "新客来电台账明细";
                mapList = excelMapper.getExcelComeCallList(map);
                countInfo = "来电新客统计" + mapList.size() + "组";
            } else if ("goCall".equals(selectCode)) {

                excelName = projectName + "新客去电台账明细";
                mapList = excelMapper.getExcelGoCallList(map);
                countInfo = "去电新客统计" + mapList.size() + "组";
            }
            //循环遍历所有数据
            for (int z = 0; z < mapList.size(); z++) {
                ExcelModelVisitAndCall map1 = mapList.get(z);
                //添加序号
                map1.setRownum((z+1)+"");
                if ("visit".equals(selectCode)){
                    Object[] oArray = map1.toVisitData();
                    dataset.add(oArray);
                }else{
                    Object[] oArray = map1.toCallData();
                    dataset.add(oArray);
                }

            }
            if (dataset.size()>0){
                String[] headers = null;
                if ("visit".equals(selectCode)){
                    headers = mapList.get(0).toVisitTitle;
                }else {
                    headers = mapList.get(0).toCallTitle;
                }

                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(excelName,headers,dataset,excelName,response,countInfo);
            }

        }  catch (UnsupportedEncodingException e) {
            System.out.print("中文字符转换异常");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询来访，来电，去电台账
     *
     * @param map
     * @return
     */
    @Override
    public Map getVisitAndCallInfo(Map map) {

        map.put("beginTime", DateUtil.beginOfDay(DateUtil.parse(String.valueOf(map.get("beginTime")))));
        map.put("endTime", DateUtil.endOfDay(DateUtil.parse(String.valueOf(map.get("endTime")))));
        Map hashMap = new HashMap<>(4);
        //查询场景：来访，来电,去电
        String selectCode = String.valueOf(map.get("selectCode"));
        //隐号查询，全号查询
        String statusCode = String.valueOf(map.get("statusCode"));
        if (StrUtil.isBlank(statusCode)) {
            throw new BadRequestException(-11_0001, "请选择查询手机号类型！");
        }
        //项目id
        String projectId = String.valueOf(map.get("projectId"));
        //来访查询新访，复访，全部
        if (StrUtil.isBlank(projectId)) {
            throw new BadRequestException(-11_0002, "请选择项目!");
        }

        Map mapUser=new HashMap();
        mapUser.put("UserName", SecurityUtils.getUsername());
        Map userInfoMap = authMapper.mGetUserInfo(mapUser);
        List<String> fullpath = projectMapper.findFullPath(mapUser);
        StringBuffer sbs = new StringBuffer();
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sbs.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sbs.append("or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        //获取有权限的项目
        List<Map> mapLists = projectMapper.findProjectListByUserName( SecurityUtils.getUsername(),"",userInfoMap.get("JobCode").toString(),sbs.toString());
        List<String> proIdList = new ArrayList<>();
        if (mapLists!=null && mapLists.size()>0){
            for (Map proMap:mapLists) {
                proIdList.add(proMap.get("projectId")+"");
            }
        }


        String[] split = projectId.split(",");
        //判断是否有权限查询这些项目
        List<String> arrayToList= Arrays.asList(split);
        List<String> arrayList=new ArrayList<String>(arrayToList);//转换为ArrayLsit调用相关的remove方法
        //将无权限的删除
        for (int f = 0;f <arrayList.size();f++) {
            if (!proIdList.contains(arrayList.get(f))){
                arrayList.remove(f);
                f--;
            }
        }
        String[] strings = new String[arrayList.size()];
        arrayList.toArray(strings);
        String proIds = "'"+ StringUtils.join(strings,"','")+"'";
        map.put("projectId", proIds);
        if (map.get("customerMobile")!=null && !"".equals(map.get("customerMobile")+"")){
            //判断手机号是否输入全号
            if (String.valueOf(map.get("customerMobile")).length()==11){
                map.put("customerMobile"," = '"+map.get("customerMobile")+"'");
            }else{
                map.put("customerMobile"," like '%"+map.get("customerMobile")+"%'");
            }
        }
        map.put("index", (Integer.parseInt(String.valueOf(map.get("index"))) - 1) * Integer.parseInt(String.valueOf(map.get("size"))));
        try {
            List<Map> mapList = new ArrayList<>();
            int count = 0;
            if ("visit".equals(selectCode)) {
                if ((Integer.parseInt(String.valueOf(map.get("max"))) == Integer.parseInt(String.valueOf(map.get("min")))) && "0".equals(String.valueOf(map.get("min")))) {
                    map.put("max", null);
                    map.put("min", null);
                }
                String code = String.valueOf(map.get("code"));
                if ("newVisit".equals(code)) {
                    mapList = excelMapper.getNewVisitList(map);
                    count = excelMapper.getNewVisitCount(map);
                    hashMap.put("newCount", count);
                    hashMap.put("oldCount", 0);
                } else if ("oldVisit".equals(code)) {

                    mapList = excelMapper.getOldVisitList(map);
                    count = excelMapper.getOldVisitCount(map);
                    hashMap.put("newCount", 0);
                    hashMap.put("oldCount", count);
                } else {

                    mapList = excelMapper.getAllVisitList(map);
                    int newCount = excelMapper.getNewVisitCount(map);
                    count = excelMapper.getAllVisitCount(map);

                    hashMap.put("newCount", newCount);
                    hashMap.put("oldCount", count - newCount);
                }
            } else if ("comeCall".equals(selectCode)) {

                mapList = excelMapper.getComeCallList(map);
                count = excelMapper.getComeCallCount(map);
            } else if ("goCall".equals(selectCode)) {

                mapList = excelMapper.getGoCallList(map);
                count = excelMapper.getGoCallCount(map);
            }

            hashMap.put("mapList", mapList);
            hashMap.put("count", count);
            return hashMap;
        } catch (Exception e) {
            throw new BadRequestException(-11_0003, "查询台账失败", e);
        }

    }

    /**
     * 查询接待大使判客台账
     *
     * @param map
     * @return
     */
    @Override
    public Map getReceptionCustomerInfo(Map map) {
        int index = (Integer.parseInt(String.valueOf(map.get("index"))) - 1) * Integer.parseInt(String.valueOf(map.get("size")));
        map.put("index", index);

        if (StrUtil.isNotBlank(String.valueOf(map.get("beginTime"))) && StrUtil.isNotBlank(String.valueOf(map.get("endTime")))) {
            Date beginTime = DateUtil.beginOfDay(DateUtil.parse(String.valueOf(map.get("beginTime"))));
            Date endTime = DateUtil.endOfDay(DateUtil.parse(String.valueOf(map.get("endTime"))));
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
        }
        List<ExcelModelVisitAndCall> resList = excelMapper.getReceptionCustomerInfoList(map);
        /*
        //中介获取项目保护规则
        QueryWrapper<ProjectProtectRule> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2
                .eq("projectID", map.get("projectId"))
                .eq("sourceType", 1)
                .eq("projectOrgCategory ", 2);
        List<ProjectProtectRule> oneProtectRuleList = ruleDao.selectList(queryWrapper2);
        //自渠获取项目保护规则
        QueryWrapper<ProjectProtectRule> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("projectID", map.get("projectId"))
                .eq("sourceType", 1)
                .eq("projectOrgCategory ", 1);
        ProjectProtectRule twoProtectRule = ruleDao.selectOne(queryWrapper);
        //案场获取项目保护规则
        QueryWrapper<ProjectProtectRule> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1
                .eq("projectID", map.get("projectId"))
                .eq("sourceType", 2);
        ProjectProtectRule threeProtectRule = ruleDao.selectOne(queryWrapper1);
        for (ExcelModelVisitAndCall map1 : resList) {
            String sourceType = map1.getSourceType();
            String reportCreateTime = map1.getReportCreateTime();
            map1.setGuestTime("");
            if (StrUtil.isNotBlank(sourceType) && StrUtil.isNotBlank(reportCreateTime) && !"null".equals(sourceType) && !"null".equals(reportCreateTime)) {
                if (Integer.parseInt(sourceType) == 1) {
                    if (StrUtil.isNotBlank(map1.getReportUserID())) {
                        //获取组织ID
                        String reportTeamId = map1.getReportTeamId();
                        if (StrUtil.isNotBlank(reportTeamId) && !"null".equals(reportCreateTime)) {
                            for (ProjectProtectRule oneProtectRule : oneProtectRuleList) {
                                if (oneProtectRule != null && oneProtectRule.getCutGuestDrainage() != null && oneProtectRule.getCutGuestDrainage() > 0) {
                                    double number = Double.parseDouble(String.valueOf(oneProtectRule.getCutGuestDrainage()));
                                    //计算防截客的结束时间
                                    Double time = number * 60;
                                    if (StrUtil.isNotBlank(reportCreateTime)) {
                                        Date dateTime = DateUtil.offsetMinute(DateUtil.parse(reportCreateTime), time.intValue());
                                        map1.setGuestTime(DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss"));
                                    }
                                }
                            }
                        }
                    }
                } else if (Integer.parseInt(sourceType) == 2) {
                    if (twoProtectRule != null && twoProtectRule.getCutGuestInvite() != null && twoProtectRule.getCutGuestInvite() > 0) {
                        double number = Double.parseDouble(String.valueOf(twoProtectRule.getCutGuestInvite()));
                        //计算防截客的结束时间
                        Double time = number * 60;
                        if (StrUtil.isNotBlank(reportCreateTime)) {
                            Date dateTime = DateUtil.offsetMinute(DateUtil.parse(reportCreateTime), time.intValue());
                            map1.setGuestTime(DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                } else if (Integer.parseInt(sourceType) == 3) {
                    if (threeProtectRule != null && threeProtectRule.getCutGuestInvite() != null && threeProtectRule.getCutGuestInvite() > 0) {
                        if(twoProtectRule==null){
                            twoProtectRule=new ProjectProtectRule();
                        }
                        double number = Double.parseDouble(String.valueOf(twoProtectRule.getCutGuestInvite()));
                        //计算防截客的结束时间
                        Double time = number * 60;
                        if (StrUtil.isNotBlank(reportCreateTime)) {
                            Date dateTime = DateUtil.offsetMinute(DateUtil.parse(reportCreateTime), time.intValue());
                            map1.setGuestTime(DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                }
            }
        }*/
        int count = excelMapper.getVisitCount(map);
        Map resMap = new HashMap<>();
        resMap.put("count", count);
        resMap.put("resList", resList);
        return resMap;
    }

    /**
     * 导出接待大使判客台账
     *
     * @param request
     * @param response
     * @param param
     */
    @Override
    public void getDownloadReceptionCustomerExcel(HttpServletRequest request, HttpServletResponse response, String param) {
        Map map = JSONObject.parseObject(param, Map.class);
        if (StrUtil.isNotBlank(String.valueOf(map.get("beginTime"))) && StrUtil.isNotBlank(String.valueOf(map.get("endTime")))) {
            Date beginTime = DateUtil.beginOfDay(DateUtil.parse(String.valueOf(map.get("beginTime"))));
            Date endTime = DateUtil.endOfDay(DateUtil.parse(String.valueOf(map.get("endTime"))));
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
        }
        //中介获取项目保护规则
        QueryWrapper<ProjectProtectRule> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2
                .eq("projectID", map.get("projectId"))
                .eq("sourceType", 1)
                .eq("projectOrgCategory ", 2);
        List<ProjectProtectRule> oneProtectRuleList = ruleDao.selectList(queryWrapper2);
        //自渠获取项目保护规则
        QueryWrapper<ProjectProtectRule> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("projectID", map.get("projectId"))
                .eq("sourceType", 1)
                .eq("projectOrgCategory ", 1);
        ProjectProtectRule twoProtectRule = ruleDao.selectOne(queryWrapper);
        //案场获取项目保护规则
        QueryWrapper<ProjectProtectRule> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1
                .eq("projectID", map.get("projectId"))
                .eq("sourceType", 2);
        ProjectProtectRule threeProtectRule = ruleDao.selectOne(queryWrapper1);

        ArrayList<Object[]> dataset = new ArrayList<>();
        try {
            //导出的文档下面的名字
            String excelName = "判客台账明细";
            List<ExcelModelVisitAndCall> mapList = excelMapper.getReceptionCustomerInfoList(map);
            //循环遍历所有数据
            for (int z = 0; z < mapList.size(); z++) {
                ExcelModelVisitAndCall map1 = mapList.get(z);
                String sourceType = map1.getSourceType();
                String reportCreateTime = map1.getReportCreateTime();
                map1.setGuestTime("");
                if (StrUtil.isNotBlank(sourceType) && StrUtil.isNotBlank(reportCreateTime) && !"null".equals(sourceType) && !"null".equals(reportCreateTime)) {
                    if (Integer.parseInt(sourceType) == 1) {
                        if (StrUtil.isNotBlank(map1.getReportUserID())) {
                            //获取组织ID
                            String reportTeamId = map1.getReportTeamId();
                            if (StrUtil.isNotBlank(reportTeamId) && !"null".equals(reportCreateTime)) {
                                for (ProjectProtectRule oneProtectRule : oneProtectRuleList) {
                                    if (oneProtectRule != null && oneProtectRule.getCutGuestDrainage() != null && oneProtectRule.getCutGuestDrainage() > 0) {
                                        double number = Double.parseDouble(String.valueOf(oneProtectRule.getCutGuestDrainage()));
                                        //计算防截客的结束时间
                                        Double time = number * 60;
                                        if (StrUtil.isNotBlank(reportCreateTime)) {
                                            Date dateTime = DateUtil.offsetMinute(DateUtil.parse(reportCreateTime), time.intValue());
                                            map1.setGuestTime(DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss"));
                                        }
                                    }
                                }
                            }
                        }
                    } else if (Integer.parseInt(sourceType) == 2) {
                        if (twoProtectRule != null && twoProtectRule.getCutGuestInvite() != null && twoProtectRule.getCutGuestInvite() > 0) {
                            double number = Double.parseDouble(String.valueOf(twoProtectRule.getCutGuestInvite()));
                            //计算防截客的结束时间
                            Double time = number * 60;
                            if (StrUtil.isNotBlank(reportCreateTime)) {
                                Date dateTime = DateUtil.offsetMinute(DateUtil.parse(reportCreateTime), time.intValue());
                                map1.setGuestTime(DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss"));
                            }
                        }
                    } else if (Integer.parseInt(sourceType) == 3) {
                        if (threeProtectRule != null && threeProtectRule.getCutGuestInvite() != null && threeProtectRule.getCutGuestInvite() > 0) {
                            if(twoProtectRule==null){
                                twoProtectRule=new ProjectProtectRule();
                            }
                            double number = Double.parseDouble(String.valueOf(twoProtectRule.getCutGuestInvite()));
                            //计算防截客的结束时间
                            Double time = number * 60;
                            if (StrUtil.isNotBlank(reportCreateTime)) {
                                Date dateTime = DateUtil.offsetMinute(DateUtil.parse(reportCreateTime), time.intValue());
                                map1.setGuestTime(DateUtil.format(dateTime, "yyyy-MM-dd HH:mm:ss"));
                            }
                        }
                    }
                }
                //添加序号
                map1.setRownum((z+1)+"");
                Object[] oArray = map1.toReportVisitInfoData();
                dataset.add(oArray);
            }
            if (dataset.size()>0){
                String[] headers = mapList.get(0).toReportVisitInfoTitle;

                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(excelName,headers,dataset,excelName,response,null);
            }

        }  catch (UnsupportedEncodingException e) {
            System.out.print("中文字符转换异常");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDataVisitAndCallCell(Workbook targetWorkBook, XSSFRow positionRow, Map jobrow, CellStyle style, Map map, int i) {

        CellStyle cs = targetWorkBook.createCellStyle();
        cs.cloneStyleFrom(style);
        cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        CellStyle cc = cs;

        String selectCode = String.valueOf(map.get("selectCode"));
        //来访
        if ("visit".equals(selectCode)) {
            XSSFCell cell1 = positionRow.createCell(0);
            cell1.setCellStyle(cc);
            cell1.setCellValue(Double.valueOf(i + 1).intValue());
            XSSFCell cell2 = positionRow.createCell(1);
            cell2.setCellStyle(cc);
            cell2.setCellValue(((String) jobrow.get("areaName")));
            XSSFCell cell3 = positionRow.createCell(2);
            cell3.setCellStyle(cc);
            cell3.setCellValue(((String) jobrow.get("projectName")));
            XSSFCell cell4 = positionRow.createCell(3);
            cell4.setCellStyle(cc);
            cell4.setCellValue(((String) jobrow.get("customerName")));
            XSSFCell cell5 = positionRow.createCell(4);
            cell5.setCellStyle(cc);
            cell5.setCellValue((String) jobrow.get("customerMobile"));
            XSSFCell cell6 = positionRow.createCell(5);
            cell6.setCellStyle(cc);
            cell6.setCellValue(((String) jobrow.get("salesAttributionName")));
            XSSFCell cell7 = positionRow.createCell(6);
            cell7.setCellStyle(cc);
            cell7.setCellValue(((String) jobrow.get("clueStatus")));
            XSSFCell cell8 = positionRow.createCell(7);
            cell8.setCellStyle(cc);
            cell8.setCellValue(((String) jobrow.get("customerLB")));
            XSSFCell cell9 = positionRow.createCell(8);
            cell9.setCellStyle(cc);
            cell9.setCellValue((Long) jobrow.get("number"));
            XSSFCell cell10 = positionRow.createCell(9);
            cell10.setCellStyle(cc);
            cell10.setCellValue((String) jobrow.get("visitDate"));
            XSSFCell cell11 = positionRow.createCell(10);
            cell11.setCellStyle(cc);
            cell11.setCellValue(((String) jobrow.get("sourceTypeDesc")));
            XSSFCell cell12 = positionRow.createCell(11);
            cell12.setCellStyle(cc);
            cell12.setCellValue(((String) jobrow.get("performanceAttributor")));
            XSSFCell cell13 = positionRow.createCell(12);
            cell13.setCellStyle(cc);
            cell13.setCellValue(jobrow.get("actualVisitsCount")+"");
            XSSFCell cell14 = positionRow.createCell(13);
            cell14.setCellStyle(cc);
            cell14.setCellValue((String) jobrow.get("mainMediaName"));
            XSSFCell cell15 = positionRow.createCell(14);
            cell15.setCellStyle(cc);
            cell15.setCellValue((String) jobrow.get("subMediaName"));
        } else if ("comeCall".equals(selectCode) || "goCall".equals(selectCode)) {
            XSSFCell cell1 = positionRow.createCell(0);
            cell1.setCellStyle(cc);
            cell1.setCellValue(Double.valueOf(i + 1).intValue());
            XSSFCell cell2 = positionRow.createCell(1);
            cell2.setCellStyle(cc);
            cell2.setCellValue(((String) jobrow.get("areaName")));
            XSSFCell cell3 = positionRow.createCell(2);
            cell3.setCellStyle(cc);
            cell3.setCellValue(((String) jobrow.get("projectName")));
            XSSFCell cell4 = positionRow.createCell(3);
            cell4.setCellStyle(cc);
            cell4.setCellValue(((String) jobrow.get("customerName")));
            XSSFCell cell5 = positionRow.createCell(4);
            cell5.setCellStyle(cc);
            cell5.setCellValue((String) jobrow.get("customerMobile"));
            XSSFCell cell6 = positionRow.createCell(5);
            cell6.setCellStyle(cc);
            cell6.setCellValue(((String) jobrow.get("saleName")));
            XSSFCell cell7 = positionRow.createCell(6);
            cell7.setCellStyle(cc);
            cell7.setCellValue(((String) jobrow.get("clueStatus")));
            XSSFCell cell8 = positionRow.createCell(7);
            cell8.setCellStyle(cc);
            cell8.setCellValue(((String) jobrow.get("customerLB")));
            XSSFCell cell9 = positionRow.createCell(8);
            cell9.setCellStyle(cc);
            cell9.setCellValue((String) jobrow.get("followUpDate"));
            XSSFCell cell10 = positionRow.createCell(9);
            cell10.setCellStyle(cc);
            cell10.setCellValue((String) jobrow.get("followUpWay"));
            XSSFCell cell11 = positionRow.createCell(10);
            cell11.setCellStyle(cc);
            cell11.setCellValue((String) jobrow.get("mainMediaName"));
            XSSFCell cell12 = positionRow.createCell(11);
            cell12.setCellStyle(cc);
            cell12.setCellValue((String) jobrow.get("subMediaName"));
        }else if ("reportVisitInfo".equals(selectCode)){
            XSSFCell cell1 = positionRow.createCell(0);
            cell1.setCellStyle(cc);
            cell1.setCellValue(Double.valueOf(i + 1).intValue());
            XSSFCell cell2 = positionRow.createCell(1);
            cell2.setCellStyle(cc);
            cell2.setCellValue(((String) jobrow.get("projectName")));
            XSSFCell cell3 = positionRow.createCell(2);
            cell3.setCellStyle(cc);
            cell3.setCellValue(((String) jobrow.get("customerName")));
            XSSFCell cell4 = positionRow.createCell(3);
            cell4.setCellStyle(cc);
            cell4.setCellValue(((String) jobrow.get("customerMobile")));
            XSSFCell cell5 = positionRow.createCell(4);
            cell5.setCellStyle(cc);
            cell5.setCellValue((String) jobrow.get("reportUserName"));
            XSSFCell cell6 = positionRow.createCell(5);
            cell6.setCellStyle(cc);
            cell6.setCellValue(((String) jobrow.get("sourceTypeDesc")));
            XSSFCell cell7 = positionRow.createCell(6);
            cell7.setCellStyle(cc);
            cell7.setCellValue(((String) jobrow.get("clueStatus")));
            XSSFCell cell8 = positionRow.createCell(7);
            cell8.setCellStyle(cc);
            cell8.setCellValue(((String) jobrow.get("salesAttributionName")));
            XSSFCell cell9 = positionRow.createCell(8);
            cell9.setCellStyle(cc);
            cell9.setCellValue((String) jobrow.get("reportCreateTime"));
            XSSFCell cell10 = positionRow.createCell(9);
            cell10.setCellStyle(cc);
            cell10.setCellValue((String) jobrow.get("theFirstVisitDate"));
            XSSFCell cell11 = positionRow.createCell(10);
            cell11.setCellStyle(cc);
            cell11.setCellValue(((String) jobrow.get("lastRefTime")));
            XSSFCell cell12 = positionRow.createCell(11);
            cell12.setCellStyle(cc);
            cell12.setCellValue(((String) jobrow.get("reportExpireDate")));
            XSSFCell cell13 = positionRow.createCell(12);
            cell13.setCellStyle(cc);
            cell13.setCellValue((String) jobrow.get("guestTime"));
            XSSFCell cell14 = positionRow.createCell(13);
            cell14.setCellStyle(cc);
            cell14.setCellValue((String) jobrow.get("tokerVisitExpireDate"));
            XSSFCell cell15 = positionRow.createCell(14);
            cell15.setCellStyle(cc);
            cell15.setCellValue((String) jobrow.get("resultCode"));
            XSSFCell cell16 = positionRow.createCell(15);
            cell16.setCellStyle(cc);
            cell16.setCellValue((String) jobrow.get("invalidReason"));
        }
    }
}
