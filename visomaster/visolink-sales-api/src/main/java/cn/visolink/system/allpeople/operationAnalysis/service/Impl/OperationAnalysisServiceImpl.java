package cn.visolink.system.allpeople.operationAnalysis.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.system.activity.dao.ActivityInfoDao;
import cn.visolink.system.allpeople.contentManagement.dao.ContentDao;
import cn.visolink.system.allpeople.operationAnalysis.dao.OperationAnalysisDao;
import cn.visolink.system.allpeople.operationAnalysis.model.*;
import cn.visolink.system.allpeople.operationAnalysis.service.OperationAnalysisService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName OperationAnalysisServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/4/13 20:41
 **/
@Service
public class OperationAnalysisServiceImpl implements OperationAnalysisService {

    @Autowired
    private OperationAnalysisDao operationAnalysisDao;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ActivityInfoDao activityInfoDao;
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private ContentDao contentDao;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private RedisUtil redisUtil;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("#0.00");
    @Value("${ZTJUrl}")
    private String ZTJUrl;
    @Value("${ZTACURL}")
    private String ZTACURL;


    @Override
    public PageInfo<OperationAnalysis> getOperationAnalysisList(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        String beginTime = "";
        String endTime = "";
        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime")+"")));
                map.put("endTime",sf.format(sf.parse(map.get("endTime")+"")));
                beginTime = DateUtil.format(DateUtil.parse(String.valueOf(map.get("beginTime"))), "yyyyMMdd");
                endTime = DateUtil.format(DateUtil.parse(String.valueOf(map.get("endTime"))), "yyyyMMdd");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            beginTime = "19700101";
            endTime = DateUtil.format(new Date(), "yyyyMMdd");
        }
        //获取项目ID
        if (map.get("projectIds")!=null && !"".equals(map.get("projectIds")+"")){
            String[] proIdss = map.get("projectIds").toString().split(",");
            String proIds = "'"+ StringUtils.join(proIdss,"','")+"'";
            map.put("proIds",proIds);
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", SecurityUtils.getUsername());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(SecurityUtils.getUsername(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < mapList.size(); i++) {
                    if (i==mapList.size()-1){
                        sb.append("'"+mapList.get(i).get("projectId")+"'");
                    }else{
                        sb.append("'"+mapList.get(i).get("projectId")+"',");
                    }
                }
                map.put("proIds",sb.toString());
            }
        }
        if (map.get("cityIds")!=null && !"".equals(map.get("cityIds")+"")){
            String[] cityIdss = map.get("cityIds").toString().split(",");
            String cityIds = "'"+ StringUtils.join(cityIdss,"','")+"'";
            map.put("cityIds",cityIds);
        }
        PageHelper.startPage(pageIndex, pageSize);
        List<OperationAnalysis> list = operationAnalysisDao.getOperationAnalysisList(map);
        //获取数据湖数据
        if (list!=null && list.size()>0){
            for (OperationAnalysis oper:list) {
                int recCount = 0;
                int visitCount = 0;
                int orderCount = 0;
                int contractCount = 0;
                double orderAMT = 0;
                double contractAMT = 0;
                List<Map> proList = operationAnalysisDao.getMergeProjectID(oper.getProjectId());
                for (Map mapPro:proList) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(beginTime).append(",").append(endTime).append(",").append(mapPro.get("KindeeProjectID"));
                    Map<String, String> reqMap = new HashMap<>(1);
                    reqMap.put("params", sb.toString());
                    //调用中台获取转化率数据
                    String result = HttpClientUtil.sendGet(ZTJUrl, reqMap);
                    Map resMaps = JSONObject.parseObject(result, Map.class);
                    //判断如果存在数据放入集合
                    if (ObjectUtil.isNotNull(resMaps.get("retData"))) {
                        JSONArray cstList = JSONObject.parseArray(String.valueOf(resMaps.get("retData")));
                        for (int i = 0; i < cstList.size(); i++) {
                            RetData retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()),RetData.class);
                            recCount+=retData.getAgentRptCnt();
                            recCount+=retData.getOwnerRptCnt();
                            recCount+=retData.getEmployeeRptCnt();
                            recCount+=retData.getParterRptCnt();
                            visitCount+=retData.getAgentVisitCnt();
                            visitCount += retData.getOwnerVisitCnt();//业主推荐到访量
                            visitCount += retData.getEmployeeVisitCnt();//员工推荐到访量
                            visitCount += retData.getParterVisitCnt();//合作方推荐到访量
                            orderCount += retData.getAgentOrderCnt();//普通经纪人推荐认购套数
                            orderCount += retData.getOwnerOrderCnt();//业主推荐认购套数
                            orderCount += retData.getEmployeeOrderCnt();//员工推荐认购套数
                            orderCount += retData.getParterOrderCnt();//合作方推荐认购套数
                            orderAMT += retData.getAgentOrderAmt();
                            orderAMT += retData.getOwnerOrderAmt();
                            orderAMT += retData.getEmployeeOrderAmt();
                            orderAMT += retData.getParterOrderAmt();
                            contractCount += retData.getAgentCntrtCnt();//普通经纪人推荐签约套数
                            contractCount += retData.getOwnerCntrtCnt();//业主推荐签约套数
                            contractCount += retData.getEmployeeCntrtCnt();//员工推荐签约套数
                            contractCount += retData.getParterCntrtCnt();//合作方推荐签约套数
                            contractAMT += retData.getAgentCntrtAmt();
                            contractAMT += retData.getOwnerCntrtAmt();
                            contractAMT += retData.getEmployeeCntrtAmt();
                            contractAMT += retData.getParterCntrtAmt();
                        }
                    }
                }
                oper.setRecCount(recCount+"");
                oper.setVisitCount(visitCount+"");
                oper.setOrderCount(orderCount+"");
                oper.setContractCount(contractCount+"");
                DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
                oper.setOrderAMT(df2.format(orderAMT/10000));
                oper.setContractAMT(df2.format(contractAMT/10000));
            }
        }
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public PageInfo<OperationAnalysisDetail> getOperationAnalysisDetailList(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        String beginTime = "";
        String endTime = "";
        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime")+"")));
                map.put("endTime",sf.format(sf.parse(map.get("endTime")+"")));
                beginTime = DateUtil.format(DateUtil.parse(String.valueOf(map.get("beginTime"))), "yyyyMMdd");
                endTime = DateUtil.format(DateUtil.parse(String.valueOf(map.get("endTime"))), "yyyyMMdd");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            beginTime = "19700101";
            endTime = DateUtil.format(new Date(), "yyyyMMdd");
        }
        //获取项目ID
        if (map.get("projectIds")!=null && !"".equals(map.get("projectIds")+"")){
            String[] proIdss = map.get("projectIds").toString().split(",");
            String proIds = "'"+ StringUtils.join(proIdss,"','")+"'";
            map.put("proIds",proIds);
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", SecurityUtils.getUsername());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(SecurityUtils.getUsername(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < mapList.size(); i++) {
                    if (i==mapList.size()-1){
                        sb.append("'"+mapList.get(i).get("projectId")+"'");
                    }else{
                        sb.append("'"+mapList.get(i).get("projectId")+"',");
                    }
                }
                map.put("proIds",sb.toString());
            }
        }
        if (map.get("cityIds")!=null && !"".equals(map.get("cityIds")+"")){
            String[] cityIdss = map.get("cityIds").toString().split(",");
            String cityIds = "'"+ StringUtils.join(cityIdss,"','")+"'";
            map.put("cityIds",cityIds);
        }
        PageHelper.startPage(pageIndex, pageSize);
        List<OperationAnalysisDetail> list = operationAnalysisDao.getOperationAnalysisDetailList(map);
        //获取数据湖数据
        if (list!=null && list.size()>0){
            for (OperationAnalysisDetail oper:list) {
                List<Map> proList = operationAnalysisDao.getMergeProjectID(oper.getProjectId());
                int ptRecCount = 0;//普通经纪人推荐量
                int yzRecCount = 0;//业主推荐量
                int ygRecCount = 0;//员工推荐量
                int hzfRecCount = 0;//合作方推荐量

                int ptVisitCount = 0;//普通经纪人推荐到访量
                int yzVisitCount = 0;//业主推荐到访量
                int ygVisitCount = 0;//员工推荐到访量
                int hzfVisitCount = 0;//合作方推荐到访量

                int ptOrderCount = 0;//普通经纪人推荐认购套数
                int yzOrderCount = 0;//业主推荐认购套数
                int ygOrderCount = 0;//员工推荐认购套数
                int hzfOrderCount = 0;//合作方推荐认购套数

                int ptContractCount = 0;//普通经纪人推荐签约套数
                int yzContractCount = 0;//业主推荐签约套数
                int ygContractCount = 0;//员工推荐签约套数
                int hzfContractCount = 0;//合作方推荐签约套数

                int recCount = 0;
                int visitCount = 0;
                int orderCount = 0;
                int contractCount = 0;
                double orderAMT = 0;
                double contractAMT = 0;
                for (Map mapPro:proList) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(beginTime).append(",").append(endTime).append(",").append(mapPro.get("KindeeProjectID"));
                    Map<String, String> reqMap = new HashMap<>(1);
                    reqMap.put("params", sb.toString());
                    //调用中台获取转化率数据
                    String result = HttpClientUtil.sendGet(ZTJUrl, reqMap);
                    Map resMaps = JSONObject.parseObject(result, Map.class);
                    //判断如果存在数据放入集合
                    if (ObjectUtil.isNotNull(resMaps.get("retData"))) {
                        JSONArray cstList = JSONObject.parseArray(String.valueOf(resMaps.get("retData")));
                        for (int i = 0; i < cstList.size(); i++) {
                            RetData retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()),RetData.class);
                            ptRecCount+=retData.getAgentRptCnt();
                            yzRecCount+=retData.getOwnerRptCnt();
                            ygRecCount+=retData.getEmployeeRptCnt();
                            hzfRecCount+=retData.getParterRptCnt();
                            ptVisitCount+=retData.getAgentVisitCnt();
                            yzVisitCount += retData.getOwnerVisitCnt();//业主推荐到访量
                            ygVisitCount += retData.getEmployeeVisitCnt();//员工推荐到访量
                            hzfVisitCount += retData.getParterVisitCnt();//合作方推荐到访量
                            ptOrderCount += retData.getAgentOrderCnt();//普通经纪人推荐认购套数
                            yzOrderCount += retData.getOwnerOrderCnt();//业主推荐认购套数
                            ygOrderCount += retData.getEmployeeOrderCnt();//员工推荐认购套数
                            hzfOrderCount += retData.getParterOrderCnt();//合作方推荐认购套数
                            ptContractCount += retData.getAgentCntrtCnt();//普通经纪人推荐签约套数
                            yzContractCount += retData.getOwnerCntrtCnt();//业主推荐签约套数
                            ygContractCount += retData.getEmployeeCntrtCnt();//员工推荐签约套数
                            hzfContractCount += retData.getParterCntrtCnt();//合作方推荐签约套数

                            recCount+=retData.getAgentRptCnt();
                            recCount+=retData.getOwnerRptCnt();
                            recCount+=retData.getEmployeeRptCnt();
                            recCount+=retData.getParterRptCnt();
                            visitCount += retData.getAgentVisitCnt();
                            visitCount += retData.getOwnerVisitCnt();//业主推荐到访量
                            visitCount += retData.getEmployeeVisitCnt();//员工推荐到访量
                            visitCount += retData.getParterVisitCnt();//合作方推荐到访量
                            orderCount += retData.getAgentOrderCnt();//普通经纪人推荐认购套数
                            orderCount += retData.getOwnerOrderCnt();//业主推荐认购套数
                            orderCount += retData.getEmployeeOrderCnt();//员工推荐认购套数
                            orderCount += retData.getParterOrderCnt();//合作方推荐认购套数
                            orderAMT += retData.getAgentOrderAmt();
                            orderAMT += retData.getOwnerOrderAmt();
                            orderAMT += retData.getEmployeeOrderAmt();
                            orderAMT += retData.getParterOrderAmt();
                            contractCount += retData.getAgentCntrtCnt();//普通经纪人推荐签约套数
                            contractCount += retData.getOwnerCntrtCnt();//业主推荐签约套数
                            contractCount += retData.getEmployeeCntrtCnt();//员工推荐签约套数
                            contractCount += retData.getParterCntrtCnt();//合作方推荐签约套数
                            contractAMT += retData.getAgentCntrtAmt();
                            contractAMT += retData.getOwnerCntrtAmt();
                            contractAMT += retData.getEmployeeCntrtAmt();
                            contractAMT += retData.getParterCntrtAmt();
                        }
                    }
                }
                oper.setPtRecCount(ptRecCount+"");
                oper.setYzRecCount(yzRecCount+"");
                oper.setYgRecCount(ygRecCount+"");
                oper.setHzfRecCount(hzfRecCount+"");

                oper.setPtVisitCount(ptVisitCount+"");
                oper.setYzVisitCount(yzVisitCount+"");
                oper.setYgVisitCount(ygVisitCount+"");
                oper.setHzfVisitCount(hzfVisitCount+"");

                oper.setPtOrderCount(ptOrderCount+"");
                oper.setYzOrderCount(yzOrderCount+"");
                oper.setYgOrderCount(ygOrderCount+"");
                oper.setHzfOrderCount(hzfOrderCount+"");

                oper.setPtContractCount(ptContractCount+"");
                oper.setYzContractCount(yzContractCount+"");
                oper.setYgContractCount(ygContractCount+"");
                oper.setHzfContractCount(hzfContractCount+"");
                oper.setRecCount(recCount+"");
                oper.setVisitCount(visitCount+"");
                oper.setOrderCount(orderCount+"");
                oper.setContractCount(contractCount+"");
                DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
                oper.setOrderAMT(df2.format(orderAMT/10000));
                oper.setContractAMT(df2.format(contractAMT/10000));
            }
        }
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void operationAnalysisExport(HttpServletRequest request, HttpServletResponse response, String param) {
        ExportVo map = JSONObject.parseObject(param,ExportVo.class);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("7");
        excelExportLog.setMainTypeDesc("运营分析表");
        excelExportLog.setSubType("Y1");
        excelExportLog.setSubTypeDesc("分析列表");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        //导出的文档下面的名字
        String excelName = null;
        String basePath = "templates";
        String templatePath = "";
        ArrayList<Object[]> dataset = new ArrayList<>();
        //获取数据
        List<OperationAnalysis> list = new ArrayList<>();
        Map paramMap=new HashMap();
        String beginTime = "";
        String endTime = "";
        if (map.getBeginTime()!=null && !"".equals(map.getBeginTime()) && map.getEndTime()!=null && !"".equals(map.getEndTime())){
            try{
                paramMap.put("beginTime",sf.format(sf.parse(map.getBeginTime())));
                paramMap.put("endTime",sf.format(sf.parse(map.getEndTime())));
                beginTime = DateUtil.format(DateUtil.parse(map.getBeginTime()), "yyyyMMdd");
                endTime = DateUtil.format(DateUtil.parse(map.getEndTime()), "yyyyMMdd");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            beginTime = "19700101";
            endTime = DateUtil.format(new Date(), "yyyyMMdd");
        }
        List<String> proIdList = new ArrayList<>();
        //获取项目ID
        if (map.getProjectIds()!=null && !"".equals(map.getProjectIds())){
            String[] ids = map.getProjectIds().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", map.getUserName());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(map.getUserName(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (int i = 0; i < mapList.size(); i++) {
                    proIdList.add(mapList.get(i).get("projectId")+"");
                }
            }
        }
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(map.getUserId());
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        String projectIds = "'"+StringUtils.join(proIdList.toArray(), "','")+"'";
        paramMap.put("proIds",projectIds);
        if (map.getCityIds()!=null && !"".equals(map.getCityIds())){
            String[] cityIdss = map.getCityIds().split(",");
            String cityIds = "'"+ StringUtils.join(cityIdss,"','")+"'";
            paramMap.put("cityIds",cityIds);
        }else{
            //查询城市
            //修改为按照账号查询所有岗位城市
            String userId = map.getUserId();
            //查询所有岗位
            List<Map> jobMap = contentDao.getAllJobs(userId);
            if (jobMap!=null && jobMap.size()>0){
                List<Map> citys = new ArrayList<>();
                //查询岗位是否是系统管理员
                boolean isAdmin = false;
                StringBuffer sb = new StringBuffer();
                for (Map map1:jobMap) {
                    if ("10001".equals(map1.get("JobCode")) || "系统管理员".equals(map1.get("JobCode"))){
                        isAdmin = true;
                    }
                    sb.append("'"+map1.get("id")+"',");
                }
                if (isAdmin){
                    citys = contentDao.getAllCitys();
                }else{
                    String JobIDs = sb.toString().substring(0,sb.toString().length()-1);
                    citys = contentDao.getCityByJobId(JobIDs);
                }
                if (citys!=null && citys.size()>0){
                    StringBuffer sb1 = new StringBuffer();
                    for (int j = 0; j < citys.size(); j++) {
                        if (j==citys.size()-1){
                            sb1.append("'"+citys.get(j).get("CityID")+"'");
                        }else{
                            sb1.append("'"+citys.get(j).get("CityID")+"',");
                        }
                    }
                    paramMap.put("cityIds",sb1.toString());
                }
            }
        }

        list = operationAnalysisDao.getOperationAnalysisList(paramMap);
        //获取数据湖数据
        if (list!=null && list.size()>0){
            StringBuilder sb = new StringBuilder();
            sb.append(beginTime).append(",").append(endTime).append(",").append("_ALL_");
            Map<String, String> reqMap = new HashMap<>(1);
            reqMap.put("params", sb.toString());
            //调用中台获取转化率数据
            String result = HttpClientUtil.sendGet(ZTJUrl, reqMap);
            Map resMaps = JSONObject.parseObject(result, Map.class);
            JSONArray cstList = null;
            //判断如果存在数据放入集合
            if (ObjectUtil.isNotNull(resMaps.get("retData"))) {
                cstList = JSONObject.parseArray(String.valueOf(resMaps.get("retData")));
            }
            for (OperationAnalysis oper:list) {
                int recCount = 0;
                int visitCount = 0;
                int orderCount = 0;
                int contractCount = 0;
                double orderAMT = 0;
                double contractAMT = 0;
                List<Map> proList = operationAnalysisDao.getMergeProjectID(oper.getProjectId());
                for (Map mapPro:proList) {
                    if (cstList!=null && cstList.size()>0){
                        for (int i = 0; i < cstList.size(); i++) {
                            RetData retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()),RetData.class);
                            if (retData.getProjId().equals(mapPro.get("KindeeProjectID"))){
                                recCount+=retData.getAgentRptCnt();
                                recCount+=retData.getOwnerRptCnt();
                                recCount+=retData.getEmployeeRptCnt();
                                recCount+=retData.getParterRptCnt();
                                visitCount+=retData.getAgentVisitCnt();
                                visitCount += retData.getOwnerVisitCnt();//业主推荐到访量
                                visitCount += retData.getEmployeeVisitCnt();//员工推荐到访量
                                visitCount += retData.getParterVisitCnt();//合作方推荐到访量
                                orderCount += retData.getAgentOrderCnt();//普通经纪人推荐认购套数
                                orderCount += retData.getOwnerOrderCnt();//业主推荐认购套数
                                orderCount += retData.getEmployeeOrderCnt();//员工推荐认购套数
                                orderCount += retData.getParterOrderCnt();//合作方推荐认购套数
                                orderAMT += retData.getAgentOrderAmt();
                                orderAMT += retData.getOwnerOrderAmt();
                                orderAMT += retData.getEmployeeOrderAmt();
                                orderAMT += retData.getParterOrderAmt();
                                contractCount += retData.getAgentCntrtCnt();//普通经纪人推荐签约套数
                                contractCount += retData.getOwnerCntrtCnt();//业主推荐签约套数
                                contractCount += retData.getEmployeeCntrtCnt();//员工推荐签约套数
                                contractCount += retData.getParterCntrtCnt();//合作方推荐签约套数
                                contractAMT += retData.getAgentCntrtAmt();
                                contractAMT += retData.getOwnerCntrtAmt();
                                contractAMT += retData.getEmployeeCntrtAmt();
                                contractAMT += retData.getParterCntrtAmt();
                            }
                        }
                    }
                }
                oper.setRecCount(recCount+"");
                oper.setVisitCount(visitCount+"");
                oper.setOrderCount(orderCount+"");
                oper.setContractCount(contractCount+"");
                oper.setOrderAMT(orderAMT+"");
                oper.setContractAMT(contractAMT+"");
                DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
                oper.setOrderAMT(df2.format(orderAMT/10000));
                oper.setContractAMT(df2.format(contractAMT/10000));
            }
        }
        try{
            excelName = "运营分析表";
            templatePath = basePath + File.separator + "brokerAnalysis.xlsx";
            for(OperationAnalysis model : list){
                Object[] oArray = model.toExproData();
                dataset.add(oArray);
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            excelExportUtil.exportExcelTemplate(templatePath,dataset,excelName,response);
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(sf.format(new Date()));
            excelExportLog.setIsDown("1");
            Long export = new Date().getTime();
            Long exporttime = export-nowtime;
            String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
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

    @Override
    public String operationAnalysisExportNew(ExportVo param) {
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        List<String> proIdList = new ArrayList<>();
        //获取项目ID
        if (param.getProjectIds()!=null && !"".equals(param.getProjectIds())){
            String[] ids = param.getProjectIds().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", param.getUserName());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(param.getUserName(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (int i = 0; i < mapList.size(); i++) {
                    proIdList.add(mapList.get(i).get("projectId")+"");
                }
            }
        }
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(param.getUserId());
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setId(id);
        excelExportLog.setMainType("7");
        excelExportLog.setMainTypeDesc("运营分析表");
        excelExportLog.setSubType("Y1");
        excelExportLog.setSubTypeDesc("分析列表");
        excelExportLog.setExportType("3");
        excelExportLog.setExportStatus("1");
        excelExportLog.setIsAsyn("1");
        //sql拼接
        StringBuffer sb = new StringBuffer();
        String timeParam = "";
        if (param.getBeginTime()!=null && !"".equals(param.getBeginTime()) && param.getEndTime()!=null && !"".equals(param.getEndTime())){
            try{
                String beginTime = sf.format(sf.parse(param.getBeginTime()));
                String endTime = sf.format(sf.parse(param.getEndTime()));
                timeParam = "sum(case when a.RegistTime > '"+beginTime+"' and a.RegistTime < '"+endTime+"' then 1 else 0 end) SectionRegCstCount, sum(case when a.RegistTime > '"+beginTime+"' and a.RegistTime < '"+endTime+"' and a.CurrentRole = '普通经纪人' then 1 else 0 end) SectionPRegCstCount, sum(case when a.RegistTime > '"+beginTime+"' and a.RegistTime < '"+endTime+"' and a.CurrentRole = '业主' then 1 else 0 end) SectionYRegCstCount, sum(case when a.RegistTime > '"+beginTime+"' and a.RegistTime < '"+endTime+"' and a.CurrentRole = '公司员工' then 1 else 0 end) SectionGRegCstCount, sum(case when a.RegistTime > '"+beginTime+"' and a.RegistTime < '"+endTime+"' and a.CurrentRole = '合作方' then 1 else 0 end) SectionHRegCstCount,";
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            timeParam = "count(1) SectionRegCstCount, sum(case when a.CurrentRole = '普通经纪人' then 1 else 0 end) SectionPRegCstCount, sum(case when a.CurrentRole = '业主' then 1 else 0 end) SectionYRegCstCount, sum(case when a.CurrentRole = '公司员工' then 1 else 0 end) SectionGRegCstCount, sum(case when a.CurrentRole = '合作方' then 1 else 0 end) SectionHRegCstCount,";
        }
        sb.append("SELECT p.id ProjectId, p.AreaName, p.ProjectName, p.ProjectNum, ct.CityName, IFNULL( sum( b.OnlookersNum), 0 ) BookAllVisitsCount, IFNULL( t.PRegCstCount, 0 ) PRegCstCount, IFNULL( t.YRegCstCount, 0 ) YRegCstCount, IFNULL( t.GRegCstCount, 0 ) GRegCstCount, IFNULL( t.HRegCstCount, 0 ) HRegCstCount, IFNULL( t.SectionRegCstCount, 0 ) SectionRegCstCount, IFNULL( t.SectionPRegCstCount, 0 ) SectionPRegCstCount, IFNULL( t.SectionYRegCstCount, 0 ) SectionYRegCstCount, IFNULL( t.SectionGRegCstCount, 0 ) SectionGRegCstCount, IFNULL( t.SectionHRegCstCount, 0 ) SectionHRegCstCount, IFNULL( t.RegCstCount, 0 ) RegCstCount FROM b_project p LEFT JOIN a_build_book b ON p.id = b.ProjectID INNER JOIN a_city_project c ON p.id = c.ProjectID INNER JOIN a_city ct ON c.CityID = ct.id INNER JOIN( SELECT count( 1 ) RegCstCount, sum(case when a.CurrentRole = '普通经纪人' then 1 else 0 end) PRegCstCount, sum(case when a.CurrentRole = '业主' then 1 else 0 end) YRegCstCount, sum(case when a.CurrentRole = '公司员工' then 1 else 0 end) GRegCstCount, sum(case when a.CurrentRole = '合作方' then 1 else 0 end) HRegCstCount,"+timeParam+"p.id FROM b_project p INNER JOIN a_broker_user a ON a.RegistProject = p.id and a.CurrentRole is not null GROUP BY p.id) t ON p.id = t.id where p.isDel = 0 and p.Status = 1 and p.pid is null and p.orgId is not null");
        String projectIds = "'"+StringUtils.join(proIdList.toArray(), "','")+"'";
        sb.append(" and p.id in ("+projectIds+")");
        if (param.getCityIds()!=null && !"".equals(param.getCityIds())){
            String[] cityIdss = param.getCityIds().split(",");
            String cityIds = "'"+ StringUtils.join(cityIdss,"','")+"'";
            sb.append(" and ct.id in ("+cityIds+")");
        }else{
            //查询城市
            //修改为按照账号查询所有岗位城市
            String userId = param.getUserId();
            //查询所有岗位
            List<Map> jobMap = contentDao.getAllJobs(userId);
            if (jobMap!=null && jobMap.size()>0){
                List<Map> citys = new ArrayList<>();
                //查询岗位是否是系统管理员
                boolean isAdmin = false;
                StringBuffer sb2 = new StringBuffer();
                for (Map map1:jobMap) {
                    if ("10001".equals(map1.get("JobCode")) || "系统管理员".equals(map1.get("JobCode"))){
                        isAdmin = true;
                    }
                    sb2.append("'"+map1.get("id")+"',");
                }
                if (isAdmin){
                    citys = contentDao.getAllCitys();
                }else{
                    String JobIDs = sb2.toString().substring(0,sb2.toString().length()-1);
                    citys = contentDao.getCityByJobId(JobIDs);
                }
                if (citys!=null && citys.size()>0){
                    StringBuffer sb1 = new StringBuffer();
                    for (int j = 0; j < citys.size(); j++) {
                        if (j==citys.size()-1){
                            sb1.append("'"+citys.get(j).get("CityID")+"'");
                        }else{
                            sb1.append("'"+citys.get(j).get("CityID")+"',");
                        }
                    }
                    sb.append(" and ct.id in ("+sb1.toString()+")");
                }
            }
        }
        sb.append(" and p.orgId != '' GROUP BY p.id ORDER BY BookAllVisitsCount DESC, RegCstCount DESC");
        excelExportLog.setDoSql(sb.toString());
        try{
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
            if (StringUtils.isEmpty(param.getCompanycode())){
                //放入redis
                redisUtil.lPush("downLoad",id);
            }else{
                //放入redis
                redisUtil.lPush("downLoad",id+","+param.getCompanycode());
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "任务创建发生异常！";
        }
        return "任务创建成功，请关注右上角下载任务状态";
    }

    @Override
    public String activityStatementExportNew(ExportVo exportVo) {
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        List<String> ids = new ArrayList<>();
        //获取项目ID
        if (exportVo.getProjectIds()!=null && !"".equals(exportVo.getProjectIds())){
            String[] proIdss = exportVo.getProjectIds().split(",");
            ids = Arrays.asList(proIdss);
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", exportVo.getUserName());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(exportVo.getUserName(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (int i = 0; i < mapList.size(); i++) {
                    ids.add(mapList.get(i).get("projectId")+"");
                }
            }
        }

        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(ids);
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setCreator(exportVo.getUserId());
        excelExportLog.setId(id);
        excelExportLog.setMainType("7");
        excelExportLog.setMainTypeDesc("运营分析表");
        excelExportLog.setSubType("Y4");
        excelExportLog.setSubTypeDesc("活动效果统计");
        excelExportLog.setExportType("3");
        excelExportLog.setExportStatus("1");
        excelExportLog.setIsAsyn("1");
        //sql拼接
        StringBuffer sb = new StringBuffer();
        sb.append("select info.id ActivityID,info.activity_name ActivityName,ppp.AreaNames,info.activity_projectnames ProjectName,ppp1.ProjectNums,DATE_FORMAT(info.activity_begintime,'%Y-%m-%d %H:%i:%s') ActivityBeginTime, DATE_FORMAT(info.activity_endtime,'%Y-%m-%d %H:%i:%s') ActivityEndTime, DATE_FORMAT(info.createtime,'%Y-%m-%d %H:%i:%s') CreateTime, DATE_FORMAT(info.release_time,'%Y-%m-%d %H:%i:%s') ReleaseTime,(case when info.`status` = 0 then '已禁用' when info.activity_endtime <= now() and info.act_status = 2 and info.`status` = 1 then '已结束' when info.activity_begintime <= now() and info.activity_endtime > now() and info.act_status = 2 and info.`status` = 1 then '已开始' when info.act_status = 2 and info.release_time <= now() and info.`status` = 1 then '已发布' when info.act_status = 2 and info.release_time > now() and info.`status` = 1 then '未发布' when info.act_status = 1 then '草稿' else '' end) Status, info.activity_type ActivityType, ifnull(s.count,0) CouponCollected, ifnull(sup.count,0) SignUpCount, ifnull(sin.count,0) SignInCount, ifnull(he.count,0) NeedHelpCount, ifnull(hedetail.count,0) HelpCount,ifnull(avd.count,0) vowCount from a_activity_info info LEFT JOIN (select sum(coupon_collected) count,activity_id from a_coupon_info GROUP BY activity_id) s on s.activity_id = info.id LEFT JOIN (select count(1) count,activity_id from a_activity_signup GROUP BY activity_id) sup on info.id = sup.activity_id LEFT JOIN (select count(1) count,cc.activity_id from ( select activity_id,signin_id from a_activity_signin GROUP BY activity_id,signin_id) cc group by cc.activity_id) sin on info.id = sin.activity_id LEFT JOIN (select count(1) count,activity_id from a_activity_help GROUP BY activity_id) he on info.id = he.activity_id LEFT JOIN (select count(1) count,activity_id from a_activity_vow_detail GROUP BY activity_id ) avd on avd.activity_id=info.id LEFT JOIN (select count(1) count,sss.activity_id from ( select activity_id,friend_id from a_activity_helpdetail GROUP BY activity_id,friend_id) sss group by sss.activity_id) hedetail on info.id = hedetail.activity_id INNER JOIN(select GROUP_CONCAT(t.AreaName) AreaNames,t.actity_id activityId from (select DISTINCT ap.actity_id,bp.AreaName from a_activity_projects ap INNER JOIN b_project bp on ap.project_id = bp.id where ap.isdel = 0) t GROUP BY t.actity_id) ppp on ppp.activityId = info.id INNER JOIN (select GROUP_CONCAT(t.ProjectNum) ProjectNums,t.actity_id activityId from (select DISTINCT ap.actity_id,bp.ProjectNum from a_activity_projects ap INNER JOIN b_project bp on ap.project_id = bp.id where ap.isdel = 0) t GROUP BY t.actity_id ) ppp1 on ppp1.activityId = info.id where info.is_del = 0");
        String projectIds = "'"+StringUtils.join(ids.toArray(), "','")+"'";
        sb.append(" and info.id in (select DISTINCT actity_id from a_activity_projects where isdel = 0 and project_id in ("+projectIds+"))");

        try{
            if (exportVo.getBeginTime()!=null && exportVo.getEndTime()!=null && !"".equals(exportVo.getBeginTime()) && !"".equals(exportVo.getEndTime())){
                String beginTime = sf.format(sf.parse(exportVo.getBeginTime()));
                String endTime = sf.format(sf.parse(exportVo.getEndTime()));
                if (exportVo.getReportTime()!=null){
                    if ("1".equals(exportVo.getReportTime())){
                        sb.append(" and info.release_time BETWEEN '"+beginTime+"' AND '"+endTime+"'");
                    }else if("2".equals(exportVo.getReportTime())){
                        sb.append(" and info.activity_begintime BETWEEN '"+beginTime+"' AND '"+endTime+"'");
                    }
                }
            }
            sb.append(" order by info.createtime desc");
            excelExportLog.setDoSql(sb.toString());
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
            if (StringUtils.isEmpty(exportVo.getCompanycode())){
                //放入redis
                redisUtil.lPush("downLoad",id);
            }else{
                //放入redis
                redisUtil.lPush("downLoad",id+","+exportVo.getCompanycode());
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "任务创建发生异常！";
        }
        return "任务创建成功，请关注右上角下载任务状态";
    }

    @Override
    public String proStatementExportNew(ExportVo exportVo) {
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        List<String> ids = new ArrayList<>();
        //获取项目ID
        if (exportVo.getProjectIds()!=null && !"".equals(exportVo.getProjectIds())){
            String[] proIdss = exportVo.getProjectIds().split(",");
            ids = Arrays.asList(proIdss);
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", exportVo.getUserName());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(exportVo.getUserName(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (int i = 0; i < mapList.size(); i++) {
                    ids.add(mapList.get(i).get("projectId")+"");
                }
            }
        }

        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(ids);
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setCreator(exportVo.getUserId());
        excelExportLog.setId(id);
        excelExportLog.setMainType("7");
        excelExportLog.setMainTypeDesc("运营分析表");
        excelExportLog.setSubType("Y5");
        excelExportLog.setSubTypeDesc("项目内容运维");
        excelExportLog.setExportType("3");
        excelExportLog.setExportStatus("1");
        excelExportLog.setIsAsyn("1");
        //sql拼接
        StringBuffer sb = new StringBuffer();
        try{
            String proTime = "";
            String recTime = "";
            String time = "";
            String beginTime = "";
            String endTime = "";
            boolean flag = false;
            if (exportVo.getBeginTime()!=null && exportVo.getEndTime()!=null && !"".equals(exportVo.getBeginTime()) && !"".equals(exportVo.getEndTime())){
                beginTime = exportVo.getBeginTime();
                endTime = exportVo.getEndTime();
                time = DateUtil.format(DateUtil.parse(exportVo.getBeginTime()), "yyyyMMdd")+","+DateUtil.format(DateUtil.parse(exportVo.getEndTime()), "yyyyMMdd")+"#"+beginTime+","+endTime;
                proTime = " and CreateTime BETWEEN '"+beginTime+"' AND '"+endTime+"'";
                recTime = " and create_time BETWEEN '"+beginTime+"' AND '"+endTime+"'";
                flag = true;
            }else{
                time = "19700101,"+DateUtil.format(new Date(), "yyyyMMdd");
                flag = false;
            }
            //将中台查询参数用文件名称字段保存下来
            excelExportLog.setFileName(time);
            sb.append("select bp.AreaName,bp.ProjectName,bp.ProjectNum,bp.id ProjectId, ifnull(ab.PhotoCount,0) PhotoCount,ifnull(ab.VideoCount,0) VideoCount, ifnull(ab.VrCount,0) VrCount,ifnull(ab.HouseImgCount,0) HouseImgCount, ifnull(ap.PosterCount,0) PosterCount,ifnull(ap.CreatePosterCount,0) CreatePosterCount, ifnull(ap1.RangePosterCount,0) RangePosterCount, ifnull(c.ConsultingCount,0) ConsultingCount, ifnull(c1.ConsultingCstCount,0) ConsultingCstCount, ifnull(cc.CallCount,0) CallCount, ifnull(c2.CallCstCount,0) CallCstCount,ifnull(tt.releaseCnt,0) dynamicReleaseCnt,ifnull(tt.allCnt,0) dynamicAllCnt from b_project bp LEFT JOIN(select a.ProjectID,sum(ifnull(t.PhotoCount,0)) PhotoCount,sum(ifnull(t.VideoCount,0)) VideoCount, sum(ifnull(t1.VrCount,0))+sum(ifnull(t.BookVRCount,0)) VrCount,sum(ifnull(t1.HouseImgCount,0)) HouseImgCount from a_build_book a left JOIN( select sum(case when TypeName = '2' then 1 else 0 end) PhotoCount, sum(case when TypeName = '1' then 1 else 0 end) BookVRCount, sum(case when TypeName = '3' then 1 else 0 end) VideoCount,BuildBookID from a_build_book_photo where TypeName in ('1','2','3') and IsDel = 0 and `Status` = 1 GROUP BY BuildBookID) t on a.id = t.BuildBookID left join ( select BuildBookID,sum(case when IsHaveVR = 1 and VRLookRoom is not null then 1 else 0 end) VrCount, count(1) HouseImgCount from a_build_book_warehouse where IsDel = 0 and `Status` = 1 GROUP BY BuildBookID) t1 on a.id = t1.BuildBookID where a.IsDel = 0 and a.`Status` = 1 GROUP BY a.ProjectID) ab on bp.id = ab.ProjectID LEFT JOIN ( select bb.ProjectId,count(1) PosterCount,sum(abp.CreatePosterCount) CreatePosterCount from b_build_poster bb LEFT JOIN ( select count(1) CreatePosterCount,PosterId from a_book_poster where PosterId is not null GROUP BY PosterId ) abp on abp.PosterId = bb.id where bb.IsDel = 0 and bb.`Status` = 1 GROUP BY bb.ProjectId ) ap on bp.id = ap.ProjectID LEFT JOIN ( select bb.ProjectId,count(1) RangePosterCount from b_build_poster bb where bb.IsDel = 0"+proTime);
            sb.append(" GROUP BY bb.ProjectId) ap1 on bp.id = ap1.ProjectID LEFT JOIN( select count(1) ConsultingCount,pp.project_id from ( select project_id,open_id,create_date from c_project_consult_records where is_del = 0"+recTime);
            sb.append(" GROUP BY project_id,open_id,create_date) pp group by pp.project_id) c on bp.id = c.project_id LEFT JOIN( select count(1) CallCount,pp1.project_id from ( select project_id,open_id,create_date from c_project_consult_records where is_del = 0 and consult_type = 1"+recTime);
            sb.append(" GROUP BY project_id,open_id,create_date) pp1 group by pp1.project_id) cc on bp.id = cc.project_id LEFT JOIN( select count(1) ConsultingCstCount,project_id from ( select project_id,open_id from c_project_consult_records where is_del = 0"+recTime);
            sb.append(" GROUP BY project_id,open_id) cc GROUP BY cc.project_id) c1 on bp.id = c1.project_id LEFT JOIN( select count(1) CallCstCount,project_id from ( select project_id,open_id from c_project_consult_records where is_del = 0 and consult_type = 1"+recTime+" GROUP BY project_id,open_id) cc GROUP BY cc.project_id) c2 on bp.id = c2.project_id");
            if(flag) {
                sb.append(" LEFT JOIN( SELECT project_id,sum(case  when release_time >= '"+beginTime+"' and release_time <= '"+endTime+"' then 1 else 0 end ) releaseCnt,count(1) as allCnt FROM a_build_book_dynamic GROUP BY project_id ) tt on tt.project_id = bp.id");
            }else{
                sb.append(" LEFT JOIN( SELECT project_id,count(1) releaseCnt,count(1) as allCnt FROM a_build_book_dynamic GROUP BY project_id ) tt on tt.project_id = bp.id");
            }
            sb.append(" where bp.IsSyn = 1 and bp.IsDel = 0");
            String projectIds = "'"+StringUtils.join(ids.toArray(), "','")+"'";
            sb.append(" and bp.id IN ("+projectIds+")");
            excelExportLog.setDoSql(sb.toString());
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
            if (StringUtils.isEmpty(exportVo.getCompanycode())){
                //放入redis
                redisUtil.lPush("downLoad",id);
            }else{
                //放入redis
                redisUtil.lPush("downLoad",id+","+exportVo.getCompanycode());
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "任务创建发生异常！";
        }
        return "任务创建成功，请关注右上角下载任务状态";
    }

    @Override
    public void operationAnalysisDetailExport(HttpServletRequest request, HttpServletResponse response, String param) {
        ExportVo paramMap = JSONObject.parseObject(param,ExportVo.class);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("7");
        excelExportLog.setMainTypeDesc("运营分析表");
        excelExportLog.setSubType("Y2");
        excelExportLog.setSubTypeDesc("推荐明细表");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        //导出的文档下面的名字
        String excelName = null;
        String basePath = "templates";
        String templatePath = "";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String beginTime = "";
        String endTime = "";
        Map map = new HashMap();
        if (paramMap.getBeginTime()!=null && !"".equals(paramMap.getBeginTime()) && paramMap.getEndTime()!=null && !"".equals(paramMap.getEndTime())){
            try{
                map.put("beginTime",sf.format(sf.parse(paramMap.getBeginTime())));
                map.put("endTime",sf.format(sf.parse(paramMap.getEndTime())));
                beginTime = DateUtil.format(DateUtil.parse(paramMap.getBeginTime()), "yyyyMMdd");
                endTime = DateUtil.format(DateUtil.parse(paramMap.getEndTime()), "yyyyMMdd");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            beginTime = "19700101";
            endTime = DateUtil.format(new Date(), "yyyyMMdd");
        }
        List<String> proIdList = new ArrayList<>();
        //获取项目ID
        if (paramMap.getProjectIds()!=null && !"".equals(paramMap.getProjectIds())){
            String[] ids = paramMap.getProjectIds().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", paramMap.getUserName());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(paramMap.getUserName(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (int i = 0; i < mapList.size(); i++) {
                    proIdList.add(mapList.get(i).get("projectId")+"");
                }
            }
        }
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(paramMap.getUserId());
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        String projectIds = "'"+StringUtils.join(proIdList.toArray(), "','")+"'";
        map.put("proIds",projectIds);
        if (paramMap.getCityIds()!=null && !"".equals(paramMap.getCityIds())){
            String[] cityIdss = paramMap.getCityIds().split(",");
            String cityIds = "'"+ StringUtils.join(cityIdss,"','")+"'";
            map.put("cityIds",cityIds);
        }
        List<OperationAnalysisDetail> list = operationAnalysisDao.getOperationAnalysisDetailList(map);
        //获取数据湖数据
        if (list!=null && list.size()>0){
            StringBuilder sb = new StringBuilder();
            sb.append(beginTime).append(",").append(endTime).append(",").append("_ALL_");
            Map<String, String> reqMap = new HashMap<>(1);
            reqMap.put("params", sb.toString());
            //调用中台获取转化率数据
            String result = HttpClientUtil.sendGet(ZTJUrl, reqMap);
            Map resMaps = JSONObject.parseObject(result, Map.class);
            JSONArray cstList = null;
            //判断如果存在数据放入集合
            if (ObjectUtil.isNotNull(resMaps.get("retData"))) {
                cstList = JSONObject.parseArray(String.valueOf(resMaps.get("retData")));
            }
            for (OperationAnalysisDetail oper:list) {
                List<Map> proList = operationAnalysisDao.getMergeProjectID(oper.getProjectId());
                int ptRecCount = 0;//普通经纪人推荐量
                int yzRecCount = 0;//业主推荐量
                int ygRecCount = 0;//员工推荐量
                int hzfRecCount = 0;//合作方推荐量

                int ptVisitCount = 0;//普通经纪人推荐到访量
                int yzVisitCount = 0;//业主推荐到访量
                int ygVisitCount = 0;//员工推荐到访量
                int hzfVisitCount = 0;//合作方推荐到访量

                int ptOrderCount = 0;//普通经纪人推荐认购套数
                int yzOrderCount = 0;//业主推荐认购套数
                int ygOrderCount = 0;//员工推荐认购套数
                int hzfOrderCount = 0;//合作方推荐认购套数

                int ptContractCount = 0;//普通经纪人推荐签约套数
                int yzContractCount = 0;//业主推荐签约套数
                int ygContractCount = 0;//员工推荐签约套数
                int hzfContractCount = 0;//合作方推荐签约套数

                int recCount = 0;
                int visitCount = 0;
                int orderCount = 0;
                int contractCount = 0;
                double orderAMT = 0;
                double contractAMT = 0;
                for (Map mapPro:proList) {
                    if (cstList!=null && cstList.size()>0){
                        for (int i = 0; i < cstList.size(); i++) {
                            RetData retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()),RetData.class);
                            if (retData.getProjId().equals(mapPro.get("KindeeProjectID"))){
                                ptRecCount+=retData.getAgentRptCnt();
                                yzRecCount+=retData.getOwnerRptCnt();
                                ygRecCount+=retData.getEmployeeRptCnt();
                                hzfRecCount+=retData.getParterRptCnt();
                                ptVisitCount+=retData.getAgentVisitCnt();
                                yzVisitCount += retData.getOwnerVisitCnt();//业主推荐到访量
                                ygVisitCount += retData.getEmployeeVisitCnt();//员工推荐到访量
                                hzfVisitCount += retData.getParterVisitCnt();//合作方推荐到访量
                                ptOrderCount += retData.getAgentOrderCnt();//普通经纪人推荐认购套数
                                yzOrderCount += retData.getOwnerOrderCnt();//业主推荐认购套数
                                ygOrderCount += retData.getEmployeeOrderCnt();//员工推荐认购套数
                                hzfOrderCount += retData.getParterOrderCnt();//合作方推荐认购套数
                                ptContractCount += retData.getAgentCntrtCnt();//普通经纪人推荐签约套数
                                yzContractCount += retData.getOwnerCntrtCnt();//业主推荐签约套数
                                ygContractCount += retData.getEmployeeCntrtCnt();//员工推荐签约套数
                                hzfContractCount += retData.getParterCntrtCnt();//合作方推荐签约套数

                                recCount+=retData.getAgentRptCnt();
                                recCount+=retData.getOwnerRptCnt();
                                recCount+=retData.getEmployeeRptCnt();
                                recCount+=retData.getParterRptCnt();
                                visitCount+=retData.getAgentVisitCnt();
                                visitCount += retData.getOwnerVisitCnt();//业主推荐到访量
                                visitCount += retData.getEmployeeVisitCnt();//员工推荐到访量
                                visitCount += retData.getParterVisitCnt();//合作方推荐到访量
                                orderCount += retData.getAgentOrderCnt();//普通经纪人推荐认购套数
                                orderCount += retData.getOwnerOrderCnt();//业主推荐认购套数
                                orderCount += retData.getEmployeeOrderCnt();//员工推荐认购套数
                                orderCount += retData.getParterOrderCnt();//合作方推荐认购套数
                                orderAMT += retData.getAgentOrderAmt();
                                orderAMT += retData.getOwnerOrderAmt();
                                orderAMT += retData.getEmployeeOrderAmt();
                                orderAMT += retData.getParterOrderAmt();
                                contractCount += retData.getAgentCntrtCnt();//普通经纪人推荐签约套数
                                contractCount += retData.getOwnerCntrtCnt();//业主推荐签约套数
                                contractCount += retData.getEmployeeCntrtCnt();//员工推荐签约套数
                                contractCount += retData.getParterCntrtCnt();//合作方推荐签约套数
                                contractAMT += retData.getAgentCntrtAmt();
                                contractAMT += retData.getOwnerCntrtAmt();
                                contractAMT += retData.getEmployeeCntrtAmt();
                                contractAMT += retData.getParterCntrtAmt();
                            }
                        }
                    }
                }
                oper.setPtRecCount(ptRecCount+"");
                oper.setYzRecCount(yzRecCount+"");
                oper.setYgRecCount(ygRecCount+"");
                oper.setHzfRecCount(hzfRecCount+"");

                oper.setPtVisitCount(ptVisitCount+"");
                oper.setYzVisitCount(yzVisitCount+"");
                oper.setYgVisitCount(ygVisitCount+"");
                oper.setHzfVisitCount(hzfVisitCount+"");

                oper.setPtOrderCount(ptOrderCount+"");
                oper.setYzOrderCount(yzOrderCount+"");
                oper.setYgOrderCount(ygOrderCount+"");
                oper.setHzfOrderCount(hzfOrderCount+"");

                oper.setPtContractCount(ptContractCount+"");
                oper.setYzContractCount(yzContractCount+"");
                oper.setYgContractCount(ygContractCount+"");
                oper.setHzfContractCount(hzfContractCount+"");

                oper.setRecCount(recCount+"");
                oper.setVisitCount(visitCount+"");
                oper.setOrderCount(orderCount+"");
                oper.setContractCount(contractCount+"");

                DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
                oper.setOrderAMT(df2.format(orderAMT/10000));
                oper.setContractAMT(df2.format(contractAMT/10000));
            }
        }

        try{
            excelName = "推荐明细表";
            templatePath = basePath + File.separator + "brokerAnalysisDetail.xlsx";
            for(OperationAnalysisDetail model : list){
                Object[] oArray = model.toExproData();
                dataset.add(oArray);
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            excelExportUtil.exportExcelTemplate(templatePath,dataset,excelName,response);
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(sf.format(new Date()));
            excelExportLog.setIsDown("1");
            Long export = new Date().getTime();
            Long exporttime = export-nowtime;
            String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
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

    @Override
    public PageInfo<CityStatement> getCityStatement(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime")+"")));
                map.put("endTime",sf.format(sf.parse(map.get("endTime")+"")));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        List<String> ids = new ArrayList<>();
        if (map.get("cityIds")!=null && !"".equals(map.get("cityIds")+"")){
            String[] cityIdss = map.get("cityIds").toString().split(",");
            ids = Arrays.asList(cityIdss);
        }else{
            //查询城市
            //修改为按照账号查询所有岗位城市
            List<Map> jobMap = contentDao.getAllJobs(SecurityUtils.getUserId());
            if (jobMap!=null && jobMap.size()>0){
                List<Map> citys = new ArrayList<>();
                //查询岗位是否是系统管理员
                boolean isAdmin = false;
                StringBuffer sb = new StringBuffer();
                for (Map map1:jobMap) {
                    if ("10001".equals(map1.get("JobCode")) || "系统管理员".equals(map1.get("JobCode"))){
                        isAdmin = true;
                    }
                    sb.append("'"+map1.get("id")+"',");
                }
                if (isAdmin){
                    citys = contentDao.getAllCitys();
                }else{
                    String JobIDs = sb.toString().substring(0,sb.toString().length()-1);
                    citys = contentDao.getCityByJobId(JobIDs);
                }
                if (citys!=null && citys.size()>0){
                    for (int j = 0; j < citys.size(); j++) {
                        ids.add(citys.get(j).get("CityID")+"");
                    }
                }
            }
        }
        map.put("cityList",ids);
        PageHelper.startPage(pageIndex,pageSize);
        List<CityStatement> list = operationAnalysisDao.getCityStatement(map);
        for (int i=0;i< list.size();i++) {
            CityStatement city = list.get(i);
            if ("0".equals(city.getBuildCount()) && "0".equals(city.getPhotoCount()) && "0".equals(city.getNewsCount())){
                list.remove(city);
                i--;
                continue;
            }
            DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
            Map cityMap = new HashMap();
            cityMap.put("cityId",city.getCityID());
            cityMap.put("beginTime",map.get("beginTime"));
            cityMap.put("endTime",map.get("endTime"));
            //查询当前城市新闻发布时间（11条）
            List<String> newsTimes = operationAnalysisDao.getNewsAddTimes(cityMap);
            if (newsTimes!=null && newsTimes.size()>1){
               try{
                   Date d1=sf.parse(newsTimes.get(0));
                   Date d2=sf.parse(newsTimes.get(newsTimes.size()-1));
                   Calendar  calendar =Calendar.getInstance();//日历类
                   calendar.setTime(d2);
                   double now_date =calendar.getTimeInMillis();
                   calendar.setTime(d1);
                   double overdue_time=calendar.getTimeInMillis();
                   double overdueDate=(now_date-overdue_time)/(1000*3600*24)/(newsTimes.size()-1);
                   city.setCreateNewsMeanTime(df2.format(overdueDate));
               } catch (Exception e){
                   e.printStackTrace();
               }
            }else{
                city.setCreateNewsMeanTime("--");
            }
            //查询当前城市活动发布时间（6条）
            List<String> activityTimes = operationAnalysisDao.getActivityAddTimes(cityMap);
            if (activityTimes!=null && activityTimes.size()>1){
                try{
                    Date d1=sf.parse(activityTimes.get(0));
                    Date d2=sf.parse(activityTimes.get(activityTimes.size()-1));
                    Calendar  calendar =Calendar.getInstance();//日历类
                    calendar.setTime(d2);
                    double now_date =calendar.getTimeInMillis();
                    calendar.setTime(d1);
                    double overdue_time=calendar.getTimeInMillis();
                    double overdueDate=(now_date-overdue_time)/(1000*3600*24)/(activityTimes.size()-1);
                    city.setReleaseActivityMeanTime(df2.format(overdueDate));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                city.setReleaseActivityMeanTime("--");
            }
        }
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void cityStatementExport(HttpServletRequest request, HttpServletResponse response, String param) {
        Map result = new HashMap();
        ExportVo exportVo = JSONObject.parseObject(param,ExportVo.class);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("7");
        excelExportLog.setMainTypeDesc("运营分析表");
        excelExportLog.setSubType("Y3");
        excelExportLog.setSubTypeDesc("城市内容运维");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        excelExportLog.setCreator(exportVo.getUserId());
        excelExportLog.setAreaName("/");
        excelExportLog.setProjectId("/");
        excelExportLog.setProjectName("/");
        if (exportVo.getBeginTime()!=null && !"".equals(exportVo.getBeginTime()) && exportVo.getEndTime()!=null && !"".equals(exportVo.getEndTime())){
            try{
                result.put("beginTime",sf.format(sf.parse(exportVo.getBeginTime())));
                result.put("endTime",sf.format(sf.parse(exportVo.getEndTime())));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        List<String> ids = new ArrayList<>();
        if (exportVo.getCityIds()!=null && !"".equals(exportVo.getCityIds())){
            String[] cityIdss = exportVo.getCityIds().split(",");
            ids = Arrays.asList(cityIdss);
        }else{
            //查询城市
            //修改为按照账号查询所有岗位城市
            List<Map> jobMap = contentDao.getAllJobs(exportVo.getUserId());
            if (jobMap!=null && jobMap.size()>0){
                List<Map> citys = new ArrayList<>();
                //查询岗位是否是系统管理员
                boolean isAdmin = false;
                StringBuffer sb = new StringBuffer();
                for (Map map1:jobMap) {
                    if ("10001".equals(map1.get("JobCode")) || "系统管理员".equals(map1.get("JobCode"))){
                        isAdmin = true;
                    }
                    sb.append("'"+map1.get("id")+"',");
                }
                if (isAdmin){
                    citys = contentDao.getAllCitys();
                }else{
                    String JobIDs = sb.toString().substring(0,sb.toString().length()-1);
                    citys = contentDao.getCityByJobId(JobIDs);
                }
                if (citys!=null && citys.size()>0){
                    for (int j = 0; j < citys.size(); j++) {
                        ids.add(citys.get(j).get("CityID")+"");
                    }
                }
            }
        }
        result.put("cityList",ids);
        List<CityStatement> list = operationAnalysisDao.getCityStatement(result);
        if (list!=null && list.size()>0){
            String[] headers = list.get(0).getCityTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();
            int num = 1;
            for(CityStatement model : list){
                DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
                Map cityMap = new HashMap();
                cityMap.put("cityId",model.getCityID());
                cityMap.put("beginTime",result.get("beginTime"));
                cityMap.put("endTime",result.get("endTime"));
                //查询当前城市新闻发布时间（11条）
                List<String> newsTimes = operationAnalysisDao.getNewsAddTimes(cityMap);
                if (newsTimes!=null && newsTimes.size()>1){
                    try{
                        Date d1=sf.parse(newsTimes.get(0));
                        Date d2=sf.parse(newsTimes.get(newsTimes.size()-1));
                        Calendar  calendar =Calendar.getInstance();//日历类
                        calendar.setTime(d2);
                        double now_date =calendar.getTimeInMillis();
                        calendar.setTime(d1);
                        double overdue_time=calendar.getTimeInMillis();
                        double overdueDate=(now_date-overdue_time)/(1000*3600*24)/(newsTimes.size()-1);
                        model.setCreateNewsMeanTime(df2.format(overdueDate));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    model.setCreateNewsMeanTime("--");
                }
                //查询当前城市活动发布时间（6条）
                List<String> activityTimes = operationAnalysisDao.getActivityAddTimes(cityMap);
                if (activityTimes!=null && activityTimes.size()>1){
                    try{
                        Date d1=sf.parse(activityTimes.get(0));
                        Date d2=sf.parse(activityTimes.get(activityTimes.size()-1));
                        Calendar  calendar =Calendar.getInstance();//日历类
                        calendar.setTime(d2);
                        double now_date =calendar.getTimeInMillis();
                        calendar.setTime(d1);
                        double overdue_time=calendar.getTimeInMillis();
                        double overdueDate=(now_date-overdue_time)/(1000*3600*24)/(activityTimes.size()-1);
                        model.setReleaseActivityMeanTime(df2.format(overdueDate));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    model.setReleaseActivityMeanTime("--");
                }

                model.setRownum(num+"");
                Object[] oArray = model.tocityData();
                dataset.add(oArray);
                num++;
            }
            try{
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("城市内容运维",headers,dataset,"城市内容运维",response,null);
                excelExportLog.setExportStatus("2");
                excelExportLog.setDownLoadTime(sf.format(new Date()));
                excelExportLog.setIsDown("1");
                Long export = new Date().getTime();
                Long exporttime = export-nowtime;
                String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
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
    }

    @Override
    public PageInfo<ActivityStatement> getActivityStatement(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime")+"")));
                map.put("endTime",sf.format(sf.parse(map.get("endTime")+"")));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        List<String> ids = new ArrayList<>();
        //获取项目ID
        if (map.get("projectIds")!=null && !"".equals(map.get("projectIds")+"")){
            String[] proIdss = map.get("projectIds").toString().split(",");
            ids = Arrays.asList(proIdss);
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", SecurityUtils.getUsername());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(SecurityUtils.getUsername(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (int i = 0; i < mapList.size(); i++) {
                    ids.add(mapList.get(i).get("projectId")+"");
                }
            }
        }

        map.put("projectList",ids);
        PageHelper.startPage(pageIndex, pageSize);

        List<ActivityStatement> list = operationAnalysisDao.getActivityStatement(map);

        if (list!=null && list.size()>0){
           String beginTime = "19700101";
           String endTime = DateUtil.format(new Date(), "yyyyMMdd");
            StringBuilder sbp = new StringBuilder();
            sbp.append(beginTime).append(",").append(endTime).append(",").append("_ALL_");
            Map<String, String> reqMap = new HashMap<>(1);
            reqMap.put("params", sbp.toString());
            //调用中台获取转化率数据
            String result = HttpClientUtil.sendGet(ZTACURL, reqMap);
//            result = "{\"retCode\":0,\"retMsg\":null,\"retData\":[{\"orderCnt\":1,\"orderArea\":97.69,\"orderAmt\":1454463,\"actyId\":\"18084517075b4accb8217641f6c98eeb\",\"cntrtArea\":97.69,\"cntrtAmt\":1454463,\"cntrtCnt\":1},{\"orderCnt\":3,\"orderArea\":326.92,\"orderAmt\":4756095,\"actyId\":\"0402bc580b8a473ab6daec76fd558fb7\",\"cntrtArea\":326.92,\"cntrtAmt\":4411258,\"cntrtCnt\":3},{\"orderCnt\":1,\"orderArea\":118.64,\"orderAmt\":2346314,\"actyId\":\"fad6bcf77eb9421db282047e8eddaeef\",\"cntrtArea\":118.64,\"cntrtAmt\":2228998,\"cntrtCnt\":1},{\"orderCnt\":1,\"orderArea\":143.43,\"orderAmt\":3287647,\"actyId\":\"850e89772db94d5daa581a3748390d7b\",\"cntrtArea\":143.43,\"cntrtAmt\":2335874,\"cntrtCnt\":1},{\"orderCnt\":1,\"orderArea\":89.95,\"orderAmt\":952996,\"actyId\":\"b18ea052856644b5959a345c9f336477\",\"cntrtArea\":89.95,\"cntrtAmt\":915444,\"cntrtCnt\":1},{\"orderCnt\":1,\"orderArea\":46.68,\"orderAmt\":445853,\"actyId\":\"89cdedd937a44827b14ef9fc8f5f3de5\",\"cntrtArea\":46.68,\"cntrtAmt\":405726,\"cntrtCnt\":1},{\"orderCnt\":1,\"orderArea\":122.53,\"orderAmt\":1181130,\"actyId\":\"f81751e9ac2b4385b8d8be81a74284e6\",\"cntrtArea\":122.53,\"cntrtAmt\":1089659,\"cntrtCnt\":1}],\"pageNum\":0,\"pageSize\":0,\"total\":0}";
            Map resMaps = JSONObject.parseObject(result, Map.class);
            JSONArray acList = null;
            //判断如果存在数据放入集合
            if (ObjectUtil.isNotNull(resMaps.get("retData"))) {
                acList = JSONObject.parseArray(String.valueOf(resMaps.get("retData")));
            }
            List<ActivityData> retDatas = new ArrayList<>();
            if (acList!=null && acList.size()>0){
                for (int i = 0; i < acList.size(); i++) {
                    ActivityData retData = JSONObject.toJavaObject(JSON.parseObject(acList.get(i).toString()),ActivityData.class);
                    retDatas.add(retData);
                }
            }
            for (ActivityStatement ac:list) {
                if (ac.getActivityType()!=null && !"".equals(ac.getActivityType())){
                    String[] types = ac.getActivityType().split(",");
                    StringBuffer sb = new StringBuffer();
                    for (String type:types) {
                        if ("1".equals(type)){
                            sb.append("报名,");
                        }else if ("2".equals(type)){
                            sb.append("助力,");
                        }else if ("3".equals(type)){
                            sb.append("优惠券,");
                        }else if ("4".equals(type)){
                            sb.append("许愿抽奖,");
                        }else if ("5".equals(type)) {
                            sb.append("乘车活动,");
                        }
                    }
                    String activityType = sb.toString().substring(0,sb.toString().length()-1);
                    ac.setActivityType(activityType);
                }
                if ("草稿".equals(ac.getStatus())){
                    ac.setNewVisitCount("0");
                    ac.setReVisitCount("0");
                    ac.setVisitCount("0");
                    ac.setOrderCount("0");
                    ac.setContractCount("0");
                }else{
                    if (ac.getActivityBeginTime()!=null && ac.getActivityEndTime()!=null
                            && !"".equals(ac.getActivityBeginTime())
                            && !"".equals(ac.getActivityEndTime())){
                        List<String> proIds = activityInfoDao.getActivityPro(ac.getActivityID());
                        Map time = new HashMap();
                        time.put("beginTime",ac.getActivityBeginTime());
                        time.put("endTime",ac.getActivityEndTime());
                        time.put("proIds",proIds);
                        time.put("activityId",ac.getActivityID());
                        List<String> timeList = operationAnalysisDao.getVisitCount(time);
                        ac.setNewVisitCount(timeList.get(0));
                        ac.setReVisitCount(timeList.get(1));
                        ac.setVisitCount(timeList.get(2));
                    }else{
                        ac.setNewVisitCount("0");
                        ac.setReVisitCount("0");
                        ac.setVisitCount("0");
                    }
                    if (retDatas.size()>0){
                        for (ActivityData ad:retDatas) {
                            if (ad.getActyId().equals(ac.getActivityID())){
                                ac.setOrderCount(ad.getOrderCnt()+"");
                                ac.setContractCount(ad.getCntrtCnt()+"");
                                break;
                            }
                        }
                    }

                }
            }
        }

        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void activityStatementExport(HttpServletRequest request, HttpServletResponse response, String param) {
        Map result = new HashMap();
        ExportVo exportVo = JSONObject.parseObject(param,ExportVo.class);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("7");
        excelExportLog.setMainTypeDesc("运营分析表");
        excelExportLog.setSubType("Y4");
        excelExportLog.setSubTypeDesc("活动效果统计");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        excelExportLog.setCreator(exportVo.getUserId());

        if (exportVo.getBeginTime()!=null && !"".equals(exportVo.getBeginTime()) && exportVo.getEndTime()!=null && !"".equals(exportVo.getEndTime())){
            try{
                result.put("beginTime",sf.format(sf.parse(exportVo.getBeginTime())));
                result.put("endTime",sf.format(sf.parse(exportVo.getEndTime())));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        List<String> ids = new ArrayList<>();
        //获取项目ID
        if (exportVo.getProjectIds()!=null && !"".equals(exportVo.getProjectIds())){
            String[] proIdss = exportVo.getProjectIds().split(",");
            ids = Arrays.asList(proIdss);
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", exportVo.getUserName());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(exportVo.getUserName(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (int i = 0; i < mapList.size(); i++) {
                    ids.add(mapList.get(i).get("projectId")+"");
                }
            }
        }

        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(ids);
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");

        result.put("projectList",ids);
        List<ActivityStatement> list = operationAnalysisDao.getActivityStatement(result);
        if (list!=null && list.size()>0){
            String beginTime = "19700101";
            String endTime = DateUtil.format(new Date(), "yyyyMMdd");
            StringBuilder sbp = new StringBuilder();
            sbp.append(beginTime).append(",").append(endTime).append(",").append("_ALL_");
            Map<String, String> reqMap = new HashMap<>(1);
            reqMap.put("params", sbp.toString());
            //调用中台获取数据
            String result1 = HttpClientUtil.sendGet(ZTACURL, reqMap);
//            result1 = "{\"retCode\":0,\"retMsg\":null,\"retData\":[{\"orderCnt\":1,\"orderArea\":97.69,\"orderAmt\":1454463,\"actyId\":\"18084517075b4accb8217641f6c98eeb\",\"cntrtArea\":97.69,\"cntrtAmt\":1454463,\"cntrtCnt\":1},{\"orderCnt\":3,\"orderArea\":326.92,\"orderAmt\":4756095,\"actyId\":\"0402bc580b8a473ab6daec76fd558fb7\",\"cntrtArea\":326.92,\"cntrtAmt\":4411258,\"cntrtCnt\":3},{\"orderCnt\":1,\"orderArea\":118.64,\"orderAmt\":2346314,\"actyId\":\"fad6bcf77eb9421db282047e8eddaeef\",\"cntrtArea\":118.64,\"cntrtAmt\":2228998,\"cntrtCnt\":1},{\"orderCnt\":1,\"orderArea\":143.43,\"orderAmt\":3287647,\"actyId\":\"850e89772db94d5daa581a3748390d7b\",\"cntrtArea\":143.43,\"cntrtAmt\":2335874,\"cntrtCnt\":1},{\"orderCnt\":1,\"orderArea\":89.95,\"orderAmt\":952996,\"actyId\":\"b18ea052856644b5959a345c9f336477\",\"cntrtArea\":89.95,\"cntrtAmt\":915444,\"cntrtCnt\":1},{\"orderCnt\":1,\"orderArea\":46.68,\"orderAmt\":445853,\"actyId\":\"89cdedd937a44827b14ef9fc8f5f3de5\",\"cntrtArea\":46.68,\"cntrtAmt\":405726,\"cntrtCnt\":1},{\"orderCnt\":1,\"orderArea\":122.53,\"orderAmt\":1181130,\"actyId\":\"f81751e9ac2b4385b8d8be81a74284e6\",\"cntrtArea\":122.53,\"cntrtAmt\":1089659,\"cntrtCnt\":1}],\"pageNum\":0,\"pageSize\":0,\"total\":0}";
            Map resMaps = JSONObject.parseObject(result1, Map.class);
            JSONArray acList = null;
            //判断如果存在数据放入集合
            if (ObjectUtil.isNotNull(resMaps.get("retData"))) {
                acList = JSONObject.parseArray(String.valueOf(resMaps.get("retData")));
            }
            List<ActivityData> retDatas = new ArrayList<>();
            if (acList!=null && acList.size()>0){
                for (int i = 0; i < acList.size(); i++) {
                    ActivityData retData = JSONObject.toJavaObject(JSON.parseObject(acList.get(i).toString()),ActivityData.class);
                    retDatas.add(retData);
                }
            }

            String[] headers = list.get(0).getActivityTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();
            int num = 1;
            for(ActivityStatement model : list){
                if (model.getActivityType()!=null && !"".equals(model.getActivityType())){
                    String[] types = model.getActivityType().split(",");
                    StringBuffer sb = new StringBuffer();
                    for (String type:types) {
                        if ("1".equals(type)){
                            sb.append("报名,");
                        }else if ("2".equals(type)){
                            sb.append("助力,");
                        }else if ("3".equals(type)){
                            sb.append("优惠券,");
                        }else if ("4".equals(type)){
                            sb.append("许愿抽奖,");
                        }else if ("5".equals(type)) {
                            sb.append("乘车活动,");
                        }
                    }
                    String activityType = sb.toString().substring(0,sb.toString().length()-1);
                    model.setActivityType(activityType);
                }
                if ("草稿".equals(model.getStatus())){
                    model.setNewVisitCount("0");
                    model.setReVisitCount("0");
                    model.setVisitCount("0");
                    model.setOrderCount("0");
                    model.setContractCount("0");
                }else{

                    if (model.getActivityBeginTime()!=null && model.getActivityEndTime()!=null
                            && !"".equals(model.getActivityBeginTime())
                            && !"".equals(model.getActivityEndTime())){
                        List<String> proIds = activityInfoDao.getActivityPro(model.getActivityID());
                        Map time = new HashMap();
                        time.put("beginTime",model.getActivityBeginTime());
                        time.put("endTime",model.getActivityEndTime());
                        time.put("proIds",proIds);
                        time.put("activityId",model.getActivityID());
                        List<String> timeList = operationAnalysisDao.getVisitCount(time);
                        model.setNewVisitCount(timeList.get(0));
                        model.setReVisitCount(timeList.get(1));
                        model.setVisitCount(timeList.get(2));
                    }else{
                        model.setNewVisitCount("0");
                        model.setReVisitCount("0");
                        model.setVisitCount("0");
                    }
                    if (retDatas.size()>0){
                        for (ActivityData ad:retDatas) {
                            if (ad.getActyId().equals(model.getActivityID())){
                                model.setOrderCount(ad.getOrderCnt()+"");
                                model.setContractCount(ad.getCntrtCnt()+"");
                                break;
                            }
                        }
                    }
                }
                model.setRownum(num+"");
                Object[] oArray = model.toActivityData();
                dataset.add(oArray);
                num++;
            }
            try{
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("活动效果统计",headers,dataset,"活动效果统计",response,null);
                excelExportLog.setExportStatus("2");
                excelExportLog.setDownLoadTime(sf.format(new Date()));
                excelExportLog.setIsDown("1");
                Long export = new Date().getTime();
                Long exporttime = export-nowtime;
                String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
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

    }

    @Override
    public PageInfo<ProjectStatement> getProStatement(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        String beginTime = "";
        String endTime = "";
        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            try{
                map.put("beginTime",map.get("beginTime")+"");
                map.put("endTime",map.get("endTime")+"");
                beginTime = DateUtil.format(DateUtil.parse(map.get("beginTime")+""), "yyyyMMdd");
                endTime = DateUtil.format(DateUtil.parse(map.get("endTime")+""), "yyyyMMdd");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            beginTime = "19700101";
            endTime = DateUtil.format(new Date(), "yyyyMMdd");
        }
        List<String> ids = new ArrayList<>();
        //获取项目ID
        if (map.get("projectIds")!=null && !"".equals(map.get("projectIds")+"")){
            String[] proIdss = map.get("projectIds").toString().split(",");
            ids = Arrays.asList(proIdss);
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", SecurityUtils.getUsername());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(SecurityUtils.getUsername(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (int i = 0; i < mapList.size(); i++) {
                    ids.add(mapList.get(i).get("projectId")+"");
                }
            }
        }
        map.put("projectList",ids);
        PageHelper.startPage(pageIndex, pageSize);
        List<ProjectStatement> list = operationAnalysisDao.getProjectStatement(map);
        if (list!=null && list.size()>0){
            DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
            StringBuilder sb = new StringBuilder();
            sb.append(beginTime).append(",").append(endTime).append(",").append("_ALL_");
            Map<String, String> reqMap = new HashMap<>(1);
            reqMap.put("params", sb.toString());
            //调用中台获取转化率数据
            String result = HttpClientUtil.sendGet(ZTJUrl, reqMap);
            Map resMaps = JSONObject.parseObject(result, Map.class);
            JSONArray dataList = null;
            //判断如果存在数据放入集合
            if (ObjectUtil.isNotNull(resMaps.get("retData"))) {
                dataList = JSONObject.parseArray(String.valueOf(resMaps.get("retData")));
            }
            //获取置业顾问数据
            List<Map> sales = operationAnalysisDao.getSales();
            for (ProjectStatement pro:list) {
                String proId = pro.getProjectId();
                map.put("projectId",proId);
                int visitCount = operationAnalysisDao.getConsultingVisitCount(map);
                pro.setConsultingVisitCount(visitCount+"");
                if (visitCount==0 || "0".equals(pro.getConsultingCstCount())){
                    pro.setVisitRate(0.00+"");
                }else{
                    pro.setVisitRate(df2.format(visitCount/Double.valueOf(pro.getConsultingCstCount())*100));
                }
                //根据项目ID查询金蝶ID
                String JDproId = operationAnalysisDao.getJDproId(proId);
                if (JDproId!=null && !"".equals(JDproId)){
                    int ConsultingTurnoverCount = 0;
                    String TurnoverRate = "0.00";
                    if (dataList!=null && dataList.size()>0){
                        for (int i = 0; i < dataList.size(); i++) {
                            RetData retData = JSONObject.toJavaObject(JSON.parseObject(dataList.get(i).toString()),RetData.class);
                            if (retData.getProjId().equals(JDproId)){
                                ConsultingTurnoverCount = retData.getCardOrderCnt();
                                if (ConsultingTurnoverCount!=0 && visitCount!=0){
                                    TurnoverRate = df2.format(ConsultingTurnoverCount/Double.valueOf(visitCount)*100);
                                }
                                break;
                            }
                        }
                    }
                    pro.setTurnoverRate(TurnoverRate);
                    pro.setConsultingTurnoverCount(ConsultingTurnoverCount+"");
                }else{
                    pro.setTurnoverRate(0.00+"");
                    pro.setConsultingTurnoverCount(0+"");
                }

                //获取当前项目下置业顾问ID
                List<String> salesId = new ArrayList<>();
                for (Map saleMap:sales) {
                    if (proId.equals(saleMap.get("projectId")+"")){
                        salesId.add(saleMap.get("saleId")+"");
                    }
                }
                if(salesId.size()>0){
                    map.put("salesId",salesId);
                    //获取关注数据
                    Map salesGZ = operationAnalysisDao.getGZdesc(map);
                    if (salesGZ!=null && !"0".equals(salesGZ.get("CardCount")+"")){
                        pro.setCardCount(salesGZ.get("CardCount")+"");
                        pro.setCardCollectCount(salesGZ.get("CardCollectCount")+"");
                        pro.setCardCollectCstCount(salesGZ.get("CardCollectCstCount")+"");
                        pro.setCardCancelCollectCount(salesGZ.get("CardCancelCollectCount")+"");
                        pro.setCardCancelCollectCstCount(salesGZ.get("CardCancelCollectCstCount")+"");
                    }else{
                        pro.setCardCount("0");
                        pro.setCardCollectCount("0");
                        pro.setCardCollectCstCount("0");
                        pro.setCardCancelCollectCount("0");
                        pro.setCardCancelCollectCstCount("0");
                    }
                }else{
                    pro.setCardCount("0");
                    pro.setCardCollectCount("0");
                    pro.setCardCollectCstCount("0");
                    pro.setCardCancelCollectCount("0");
                    pro.setCardCancelCollectCstCount("0");
                }
                //楼盘动态发布时长
                Map dynamicMap = new HashMap();
                dynamicMap.put("projectId",proId);
                dynamicMap.put("beginTime",map.get("beginTime"));
                dynamicMap.put("endTime",map.get("endTime"));
                List<String> dynamicTime = operationAnalysisDao.getDynamicAddTimes(dynamicMap);
                if (dynamicTime!=null && dynamicTime.size()>1){
                    try{
                        Date d1=sf.parse(dynamicTime.get(0));
                        Date d2=sf.parse(dynamicTime.get(dynamicTime.size()-1));
                        Calendar  calendar =Calendar.getInstance();//日历类
                        calendar.setTime(d2);
                        double now_date =calendar.getTimeInMillis();
                        calendar.setTime(d1);
                        double overdue_time=calendar.getTimeInMillis();
                        double overdueDate=(now_date-overdue_time)/(1000*3600*24)/(dynamicTime.size()-1);
                        pro.setDynamicReleaseTime(df2.format(overdueDate));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    pro.setDynamicReleaseTime("--");
                }
                if(StringUtils.isBlank(pro.getDynamicReleaseCnt())){
                    pro.setDynamicReleaseCnt("0");
                }
                if(StringUtils.isBlank(pro.getDynamicAllCnt())){
                    pro.setDynamicAllCnt("0");
                }
            }
        }
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void proStatementExport(HttpServletRequest request, HttpServletResponse response, String param) {
        Map result = new HashMap();
        ExportVo exportVo = JSONObject.parseObject(param,ExportVo.class);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("7");
        excelExportLog.setMainTypeDesc("运营分析表");
        excelExportLog.setSubType("Y5");
        excelExportLog.setSubTypeDesc("项目内容运维");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        excelExportLog.setCreator(exportVo.getUserId());

        String beginTime = "";
        String endTime = "";
        if (exportVo.getBeginTime()!=null && !"".equals(exportVo.getBeginTime()) && exportVo.getEndTime()!=null && !"".equals(exportVo.getEndTime())){
            try{
                result.put("beginTime",sf.format(sf.parse(exportVo.getBeginTime())));
                result.put("endTime",sf.format(sf.parse(exportVo.getEndTime())));
                beginTime = DateUtil.format(DateUtil.parse(exportVo.getBeginTime()), "yyyyMMdd");
                endTime = DateUtil.format(DateUtil.parse(exportVo.getEndTime()), "yyyyMMdd");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            beginTime = "19700101";
            endTime = DateUtil.format(new Date(), "yyyyMMdd");
        }
        List<String> ids = new ArrayList<>();
        //获取项目ID
        if (exportVo.getProjectIds()!=null && !"".equals(exportVo.getProjectIds())){
            String[] proIdss = exportVo.getProjectIds().split(",");
            ids = Arrays.asList(proIdss);
        }else {
            //如果未传参数则查询当前用户有权限的项目
            Map parammap = new HashMap();
            parammap.put("UserName", exportVo.getUserName());
            Map userInfoMap = authMapper.mGetUserInfo(parammap);
            List<String> fullpath = projectMapper.findFullPath(parammap);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(exportVo.getUserName(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (int i = 0; i < mapList.size(); i++) {
                    ids.add(mapList.get(i).get("projectId")+"");
                }
            }
        }
        result.put("projectList",ids);
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(ids);
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");

        List<ProjectStatement> list = operationAnalysisDao.getProjectStatement(result);
        if (list!=null && list.size()>0){
            DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
            StringBuilder sb = new StringBuilder();
            sb.append(beginTime).append(",").append(endTime).append(",").append("_ALL_");
            Map<String, String> reqMap = new HashMap<>(1);
            reqMap.put("params", sb.toString());
            //调用中台获取转化率数据
            String result1 = HttpClientUtil.sendGet(ZTJUrl, reqMap);
            Map resMaps = JSONObject.parseObject(result1, Map.class);
            JSONArray dataList = null;
            //判断如果存在数据放入集合
            if (ObjectUtil.isNotNull(resMaps.get("retData"))) {
                dataList = JSONObject.parseArray(String.valueOf(resMaps.get("retData")));
            }
            String[] headers = list.get(0).getProTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();
            //获取置业顾问数据
            List<Map> sales = operationAnalysisDao.getSales();
            int num = 1;
            for (ProjectStatement pro:list) {
                pro.setRownum(num+"");
                String proId = pro.getProjectId();
                result.put("projectId",proId);
                int visitCount = operationAnalysisDao.getConsultingVisitCount(result);
                pro.setConsultingVisitCount(visitCount+"");
                if (visitCount==0 || "0".equals(pro.getConsultingCstCount())){
                    pro.setVisitRate(0.00+"");
                }else{
                    pro.setVisitRate(df2.format(visitCount/Double.valueOf(pro.getConsultingCstCount())*100));
                }
                //根据项目ID查询金蝶ID
                String JDproId = operationAnalysisDao.getJDproId(proId);
                if (JDproId!=null && !"".equals(JDproId)){
                    int ConsultingTurnoverCount = 0;
                    String TurnoverRate = "0.00";
                    if (dataList!=null && dataList.size()>0){
                        for (int i = 0; i < dataList.size(); i++) {
                            RetData retData = JSONObject.toJavaObject(JSON.parseObject(dataList.get(i).toString()),RetData.class);
                            if (retData.getProjId().equals(JDproId)){
                                ConsultingTurnoverCount = retData.getCardOrderCnt();
                                if (ConsultingTurnoverCount!=0 && visitCount!=0){
                                    TurnoverRate = df2.format(ConsultingTurnoverCount/Double.valueOf(visitCount)*100);
                                }
                                break;
                            }
                        }
                    }
                    pro.setTurnoverRate(TurnoverRate);
                    pro.setConsultingTurnoverCount(ConsultingTurnoverCount+"");
                }else{
                    pro.setTurnoverRate(0.00+"");
                    pro.setConsultingTurnoverCount(0+"");
                }
                //获取当前项目下置业顾问ID
                List<String> salesId = new ArrayList<>();
                for (Map saleMap:sales) {
                    if (proId.equals(saleMap.get("projectId")+"")){
                        salesId.add(saleMap.get("saleId")+"");
                    }
                }
                if(salesId.size()>0){
                    result.put("salesId",salesId);
                    //获取关注数据
                    Map salesGZ = operationAnalysisDao.getGZdesc(result);
                    if (salesGZ!=null && !"0".equals(salesGZ.get("CardCount")+"")){
                        pro.setCardCount(salesGZ.get("CardCount")+"");
                        pro.setCardCollectCount(salesGZ.get("CardCollectCount")+"");
                        pro.setCardCollectCstCount(salesGZ.get("CardCollectCstCount")+"");
                        pro.setCardCancelCollectCount(salesGZ.get("CardCancelCollectCount")+"");
                        pro.setCardCancelCollectCstCount(salesGZ.get("CardCancelCollectCstCount")+"");
                    }else{
                        pro.setCardCount("0");
                        pro.setCardCollectCount("0");
                        pro.setCardCollectCstCount("0");
                        pro.setCardCancelCollectCount("0");
                        pro.setCardCancelCollectCstCount("0");
                    }
                }else{
                    pro.setCardCount("0");
                    pro.setCardCollectCount("0");
                    pro.setCardCollectCstCount("0");
                    pro.setCardCancelCollectCount("0");
                    pro.setCardCancelCollectCstCount("0");
                }
                //楼盘动态发布时长
                Map dynamicMap = new HashMap();
                dynamicMap.put("projectId",proId);
                dynamicMap.put("beginTime",result.get("beginTime"));
                dynamicMap.put("endTime",result.get("endTime"));
                List<String> dynamicTime = operationAnalysisDao.getDynamicAddTimes(dynamicMap);
                if (dynamicTime!=null && dynamicTime.size()>1){
                    try{
                        Date d1=sf.parse(dynamicTime.get(0));
                        Date d2=sf.parse(dynamicTime.get(dynamicTime.size()-1));
                        Calendar  calendar =Calendar.getInstance();//日历类
                        calendar.setTime(d2);
                        double now_date =calendar.getTimeInMillis();
                        calendar.setTime(d1);
                        double overdue_time=calendar.getTimeInMillis();
                        double overdueDate=(now_date-overdue_time)/(1000*3600*24)/(dynamicTime.size()-1);
                        pro.setDynamicReleaseTime(df2.format(overdueDate));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    pro.setDynamicReleaseTime("--");
                }
                if(StringUtils.isBlank(pro.getDynamicReleaseCnt())){
                    pro.setDynamicReleaseCnt("0");
                }
                if(StringUtils.isBlank(pro.getDynamicAllCnt())){
                    pro.setDynamicAllCnt("0");
                }
                Object[] oArray = pro.toProData();
                dataset.add(oArray);
                num++;
            }
            try{
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("项目内容运维",headers,dataset,"项目内容运维",response,null);
                excelExportLog.setExportStatus("2");
                excelExportLog.setDownLoadTime(sf.format(new Date()));
                excelExportLog.setIsDown("1");
                Long export = new Date().getTime();
                Long exporttime = export-nowtime;
                String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
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
    }


}
