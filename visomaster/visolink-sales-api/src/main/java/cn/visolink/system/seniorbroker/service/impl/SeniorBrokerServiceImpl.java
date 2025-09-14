package cn.visolink.system.seniorbroker.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.BaseResultCodeEnum;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.allpeople.examine.model.RetDataZT;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.job.authorization.model.BrokerAccountRecordsBatch;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.seniorbroker.vo.*;
import cn.visolink.message.model.form.MessageClueRelation;
import cn.visolink.system.seniorbroker.mapper.SeniorBrokerMapper;
import cn.visolink.system.seniorbroker.service.SeniorBrokerService;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.CessException;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.soap.Addressing;
import java.text.DecimalFormat;
import java.util.*;

import static com.alibaba.fastjson.JSONObject.*;

/**
 * @Author: BaoQiangLer
 * @ProjectName: visolink
 * @Description:  地图选房业务接口实现
 * @Date: Created in 2020/10/12
 */

@Service
public class SeniorBrokerServiceImpl implements SeniorBrokerService {

    @Value("${ZTJJRHDURL}")
    private String ZTJJRHDURL;

    @Value("${ZTDKHURL}")
    private String ZTDKHURL;

    private final SeniorBrokerMapper seniorBrokerMapper;

    private final RedisUtil redisUtil;

    private final ExcelImportMapper excelImportMapper;

    private final ProjectMapper projectMapper;

    private final AuthMapper authMapper;

    public SeniorBrokerServiceImpl(SeniorBrokerMapper seniorBrokerMapper, RedisUtil redisUtil, ExcelImportMapper excelImportMapper, ProjectMapper projectMapper, AuthMapper authMapper) {
        this.seniorBrokerMapper = seniorBrokerMapper;
        this.redisUtil = redisUtil;
        this.excelImportMapper = excelImportMapper;
        this.projectMapper = projectMapper;
        this.authMapper = authMapper;
    }

    private DecimalFormat df = new DecimalFormat("#0.00");

    /**
     * 获取大客户活动数据
     *
     * @param param 大客户经理人账号
     * @return result
     * */
    @Override
    public PageInfo<AccountActiveExcel> getAccountActiveList(Map<String, Object> param) {
        PageHelper.startPage((Integer) param.get("pageNum"), (Integer)param.get("pageSize"));
//        List<String> sList = (List<String>) param.get("Status");
//        if(sList!=null && sList.size()>0){
//            for(int i=0;i<sList.size();i++){
//                if(sList.get(i).equals("6")){
//                    param.put("endStatus","6");
//                    sList.remove(i);
//                }
//            }
//        }
        List<AccountActiveExcel> list = seniorBrokerMapper.getAccountActiveList(param);
        JSONArray cstList  = null;
        //注释调用中台
//        cstList  =  this.getZtTransactionDate("_ALL_",ZTJJRHDURL,null,null);
        boolean b = cstList != null && cstList.size()>0;
        if(b){
            for(AccountActiveExcel account : list){
                for (int i = 0; i < cstList.size(); i++) {
                    Map retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()), Map.class);
                    if (account.getActiveId().equalsIgnoreCase(String.valueOf(retData.get("actyId")))) {
                        account.setCntrtCnt(account.getCntrtCnt() + MapUtils.getIntValue(retData, "cntrtCnt", 0));
                        account.setOrderCnt(account.getOrderCnt() + MapUtils.getIntValue(retData, "orderCnt", 0));
                    }
                }
            }
        }
        return new PageInfo<>(list);
    }

    /**
     * 获取大客户活动数据
     *
     * @param param 大客户经理人账号
     * @return result
     * */
    @Override
    public Map<String,Object> getAccountActiveById(Map<String, Object> param) {
        String  activeId = MapUtils.getString(param,"ActiveId");
        Map<String,Object> result = new HashMap<>();
        if ( StrUtil.isBlank(activeId)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        Map<String,Object> active = seniorBrokerMapper.getAccountActiveById(activeId);
        JSONArray cstList  = this.getZtTransactionDate(activeId,ZTJJRHDURL,null,null);
        boolean b = cstList != null && cstList.size()>0;
        if(b){
            for (int i = 0; i < cstList.size(); i++) {
                Map retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()), Map.class);
                if (activeId.equalsIgnoreCase(String.valueOf(retData.get("actyId")))) {
                    active.put("cntrtCnt",MapUtils.getIntValue(active,"cntrtCnt",0) + MapUtils.getIntValue(retData, "cntrtCnt", 0));
                    active.put("orderCnt",MapUtils.getIntValue(active,"orderCnt",0) + MapUtils.getIntValue(retData, "orderCnt", 0));
                }
            }
        }else{
            active.put("cntrtCnt", MapUtils.getIntValue(active, "cntrtCnt", 0));
            active.put("orderCnt", MapUtils.getIntValue(active, "orderCnt", 0));
        }
        result.put("active",active);
        return result;
    }

    /**
     * 大客户活动数据导出
     *
     * @param request request
     * @param response response
     * @param param 大客户经理人账号
     * */
    @Override
    public void getAccountActiveExport(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param) {
        String userName = MapUtils.getString(param, "UserName");
        String userId = MapUtils.getString(param, "UserId");
        //获取导出是否全号
        int exportType = MapUtils.getInteger(param, "exportType");
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
        excelExportLog.setExportType(exportType+"");

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
        String excelName = "大客户经理任务台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<AccountActiveExcel> list;
        try{
            //查询数据
//            List<String> sList = (List<String>) param.get("Status");
//            if(sList!=null && sList.size()>0){
//                for(int i=0;i<sList.size();i++){
//                    if(sList.get(i).equals("6")){
//                        param.put("endStatus","6");
//                        sList.remove(i);
//                    }
//                }
//            }
            list = seniorBrokerMapper.getAccountActiveList(param);
            String []  headers = new AccountActiveExcel().courtCaseTitle;
            //查询项目名称
            JSONArray cstList  = this.getZtTransactionDate("_ALL_",ZTJJRHDURL,null,null);
            boolean b = cstList != null && cstList.size()>0;
            for(AccountActiveExcel model : list){
                if(b){
                    for (int i = 0; i < cstList.size(); i++) {
                        Map retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()), Map.class);
                        if (model.getActiveId().equalsIgnoreCase(String.valueOf(retData.get("actyId")))) {
                            model.setCntrtCnt(model.getCntrtCnt() + MapUtils.getIntValue(retData, "cntrtCnt", 0));
                            model.setOrderCnt(model.getOrderCnt() + MapUtils.getIntValue(retData, "orderCnt", 0));
                        }
                    }
                }
                Object[] oArray = model.toPublicData(exportType == 2);
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



    /**
     * 获取活动下的二级经纪人导出
     *
     * @param request request
     * @param response response
     * @param param 大客户经理人账号
     * */
    @Override
    public void getBrokerByActiveIdExcel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param) {
        String userName = MapUtils.getString(param, "UserName");
        String userId = MapUtils.getString(param, "UserId");
        String activeId = MapUtils.getString(param,"ActiveId");
        //获取导出是否全号
        int exportType = MapUtils.getInteger(param, "exportType");
        if ( StrUtil.isBlank(userName) || StrUtil.isBlank(userId) || StrUtil.isBlank(activeId) ) {
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
        excelExportLog.setSubType("Q4");
        excelExportLog.setSubTypeDesc("经纪人参与情况导出");
        excelExportLog.setIsAsyn("0");
        excelExportLog.setExportType(exportType+"");
        excelExportLog.setAreaName("/");
        excelExportLog.setProjectId("/");
        excelExportLog.setProjectName("/");

        //导出的文档下面的名字
        String excelName = "经纪人参与情况";
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<BrokerActiveExcel> list;
        try{
            //查询数据
            list = seniorBrokerMapper.getBrokerByActiveIdExcel(param);
            String []  headers = new BrokerActiveExcel().courtCaseTitle;
            //中台获取认购签约数据
            JSONArray cstList  = this.getZtTransactionDate(activeId,ZTJJRHDURL,null,null);
            boolean b = cstList != null && cstList.size()>0;
            for(BrokerActiveExcel model : list){
                if(b){
                    for (int i = 0; i < cstList.size(); i++) {
                        Map retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()), Map.class);
                        if (activeId.equalsIgnoreCase(String.valueOf(retData.get("actyId")))
                                && model.getBrokerId().equalsIgnoreCase(String.valueOf(retData.get("brokerId")))
                        ) {
                            model.setCntrtCnt(model.getCntrtCnt() + MapUtils.getIntValue(retData, "cntrtCnt", 0));
                            model.setOrderCnt(model.getOrderCnt() + MapUtils.getIntValue(retData, "orderCnt", 0));
                        }
                    }
                }
                //查询项目名称
                Object[] oArray = model.toPublicData(exportType == 2);
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


    /**
     * 获取活动下的客户导出
     *
     * @param request request
     * @param response response
     * @param param 大客户经理人账号
     * */
    @Override
    public void getCustomerByActiveIdExcel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param) {
        String userName = MapUtils.getString(param, "UserName");
        String userId = MapUtils.getString(param, "UserId");
        String activeId = MapUtils.getString(param,"ActiveId");
        //获取导出是否全号
        int exportType = MapUtils.getInteger(param, "exportType");
        if ( StrUtil.isBlank(userName) || StrUtil.isBlank(userId) || StrUtil.isBlank(activeId) ) {
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
        excelExportLog.setSubType("Q5");
        excelExportLog.setSubTypeDesc("活动报备台账");
        excelExportLog.setIsAsyn("0");
        excelExportLog.setExportType(exportType+"");
        excelExportLog.setAreaName("/");
        excelExportLog.setProjectId("/");
        excelExportLog.setProjectName("/");
        //导出的文档下面的名字
        String excelName = "经纪人参与情况";
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<CustomerActiveExcel> list;
        try{
            //查询数据
            list = seniorBrokerMapper.getCustomerByActiveIdExcel(param);
            String []  headers = new CustomerActiveExcel().courtCaseTitle;
            //查询项目名称
            for(CustomerActiveExcel model : list){
                Object[] oArray = model.toPublicData(exportType == 2);
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
    public String getSeniorBrokerExcelNew(Map param) {
        String companycode = "";
        if (param.get("companycode")!=null){
            companycode = param.get("companycode")+"";
        }
        String userName = MapUtils.getString(param, "UserName");
        String userId = MapUtils.getString(param, "UserId");
        String ids = MapUtils.getString(param, "proIds");
        //获取导出是否全号
        int exportType = MapUtils.getInteger(param, "exportType");
        if ( StrUtil.isBlank(userName) || StrUtil.isBlank(userId)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        //记录导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setCreator(userId);
        excelExportLog.setId(id);
        excelExportLog.setMainType("3");
        excelExportLog.setMainTypeDesc("经纪人管理");
        excelExportLog.setSubType("J2");
        excelExportLog.setSubTypeDesc("金牌合伙人台账");
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportType(exportType+"");

        List<String> projectIdList = new ArrayList<>();
        if ("".equals(ids)){
            Map map = new HashMap();
            map.put("UserName", userName);
            Map userInfoMap = authMapper.mGetUserInfo(map);
            List<String> fullpath = projectMapper.findFullPath(map);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(userName, "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList != null && mapList.size() > 0) {
                for (Map proMap : mapList) {
                    projectIdList.add(proMap.get("projectId") + "");
                }
            }
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(projectIdList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }else{
            String[] idss = ids.split(",");
            projectIdList = Arrays.asList(idss);
        }
        String pro = "";//关联项目
        if (projectIdList.size()>0){
            pro = " and ba.ProjectID in ('"+StringUtils.join(projectIdList.toArray(), "','")+"')";
        }
        //sql拼接
        StringBuffer sb = new StringBuffer();
        sb.append("select ba.id, p.id as ProjectId,p.KindeeProjectID, p.AreaName, p.projectName as ProjectName, a.id as AccountId, a.EmployeeName as AccountName, a.Mobile as AccountMobileAll, concat(left(a.Mobile,3),'****',RIGHT(a.Mobile,4)) as AccountMobile, bu.id as BrokerId, bu.name as BrokerName, bu.Mobile as BrokerMobileAll, concat(left(bu.Mobile,3),'****',RIGHT(bu.Mobile,4)) as BrokerMobile, ifnull(date_format(ba.CreateTime,'%Y-%m-%d %H:%i:%s'),'') as CreateTime, count(distinct ag.id) as ActiveGive, sum(case when pc.ClueStatus < 9 then 1 else 0 end) as Report, sum(case when pc.ClueStatus < 9 and pc.ClueStatus > 1 then 1 else 0 end) as Arrive from a_broker_account ba inner join a_broker_user bu on ba.brokerid = bu.id inner join a_user_role ur on ba.brokerid = ur.brokerid and ur.isdel = 0 left join b_account a on ba.accountid = a.id and a.isdel = 0 and a.status = 1 left join b_project p on ba.projectid = p.id and p.isdel = 0 left join a_accountactive_give ag on ba.brokerid = ag.brokerid and ba.accountid = ag.accountid and ba.projectid = ag.projectid left join a_account_performance ap on ba.brokerid = ap.brokerid and ba.accountid = ap.accountid and ba.projectid = ap.projectid and ap.isdel = 0 left join b_project_clues pc on ap.ProjectClueId = pc.ProjectClueId and pc.isdel = 0 and((pc.ClueStatus = 1 and a.status = 1) or (pc.ClueStatus > 1 and pc.ClueStatus < 9)) where ba.isdel = 0"+pro);
        if (param.get("RoleNameList")!=null && !"".equals(param.get("RoleNameList"))){
            String[] roles = param.get("RoleNameList").toString().split(",");
            sb.append(" and ur.RoleName in ('"+StringUtils.join(roles, "','")+"')");
        }
        if (param.get("AccountName")!=null && !"".equals(param.get("AccountName")+"")){
            sb.append(" and (a.EmployeeName like concat('%','"+param.get("AccountName")+"','%') or a.Mobile like concat('%','"+param.get("AccountName")+"','%'))");
        }
        if (param.get("BrokerName")!=null && !"".equals(param.get("BrokerName")+"")){
            sb.append(" and (bu.name like concat('%','"+param.get("BrokerName")+"','%') or bu.Mobile like concat('%','"+param.get("BrokerName")+"','%'))");
        }
        String beginTime = "19700101";
        String endTime = DateUtil.format(new Date(), "yyyyMMdd");
        if (param.get("startDate")!=null && param.get("endDate")!=null && !"".equals(param.get("startDate")+"") && !"".equals(param.get("endDate")+"")){
            if (param.get("key")!=null){
                if ("1".equals(param.get("key")+"")){
                    sb.append(" and ba.CreateTime BETWEEN '"+param.get("startDate")+"' AND '"+param.get("endDate")+"'");
                }else if("2".equals(param.get("key")+"")){
                    sb.append(" and pc.CreateTime BETWEEN '"+param.get("startDate")+"' AND '"+param.get("endDate")+"'");
                    beginTime = DateUtil.format(DateUtil.parse(String.valueOf(param.get("startDate"))), "yyyyMMdd");
                    endTime = DateUtil.format(DateUtil.parse(String.valueOf(param.get("endDate"))), "yyyyMMdd");
                }
            }
        }
        excelExportLog.setFileName(beginTime+","+endTime);
        sb.append(" GROUP BY ba.id");
        excelExportLog.setDoSql(sb.toString());
        try{
            //记录导出日志
            excelExportLog.setExportStatus("1");
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
            if (StringUtils.isEmpty(companycode)){
                //放入redis
                redisUtil.lPush("downLoad",id);
            }else{
                //放入redis
                redisUtil.lPush("downLoad",id+","+companycode);
            }
        }catch (Exception e){
            e.printStackTrace();
            excelExportLog.setExportStatus("3");
            excelExportLog.setExceptionMessage(e.getMessage());
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
            return "任务创建发生异常！";
        }
        return "导出任务创建成功，请关注右上角下载任务状态";
    }

    /**
     * 二级经纪人数据导出
     *
     * @param request request
     * @param response response
     * @param param 大客户经理人账号
     * */
    @Override
    public void getSeniorBrokerExcel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param) {
        String userName = MapUtils.getString(param, "UserName");
        String userId = MapUtils.getString(param, "UserId");
        String ids = MapUtils.getString(param, "proIds");
        if (param.get("RoleNameList")!=null && !"".equals(param.get("RoleNameList"))){
            String[] roles = param.get("RoleNameList").toString().split(",");
            param.put("RoleNameList",Arrays.asList(roles));
        }else{
            param.put("RoleNameList",null);
        }
        System.out.println("开始导出------------》");
        //获取导出是否全号
        int exportType = MapUtils.getInteger(param, "exportType");
        if ( StrUtil.isBlank(userName) || StrUtil.isBlank(userId)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        //记录导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = System.currentTimeMillis();
        excelExportLog.setCreator(userId);
        excelExportLog.setId(id);
        excelExportLog.setMainType("3");
        excelExportLog.setMainTypeDesc("经纪人管理");
        excelExportLog.setSubType("J2");
        excelExportLog.setSubTypeDesc("金牌合伙人台账");
        excelExportLog.setIsAsyn("0");
        excelExportLog.setExportType(exportType+"");

        List<String> projectIdList = new ArrayList<>();
        if ("".equals(ids)){
            Map map = new HashMap();
            map.put("UserName", userName);
            Map userInfoMap = authMapper.mGetUserInfo(map);
            List<String> fullpath = projectMapper.findFullPath(map);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(userName, "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList != null && mapList.size() > 0) {
                for (Map proMap : mapList) {
                    projectIdList.add(proMap.get("projectId") + "");
                }
            }
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(projectIdList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }else{
            String[] idss = ids.split(",");
            projectIdList = Arrays.asList(idss);
        }
        param.put("projectIdList",projectIdList);
        //导出的文档下面的名字
        String excelName = "金牌合伙人台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<SeniorBrokerActiveExcel> list;
        try{
            System.out.println("开始查询二级经纪人数据------------》");
            //查询数据
            list = seniorBrokerMapper.getSeniorBrokerExcel(param);
            if (list!=null && list.size()>0){
                System.out.println("二级经纪人数据查询完毕------------》查询到"+list.size()+"条");
                String []  headers = new SeniorBrokerActiveExcel().courtCaseTitle;
                String beginTime = "19700101";
                String endTime = DateUtil.format(new Date(), "yyyyMMdd");
                if (param.get("startDate")!=null && !"".equals(param.get("startDate")+"") && param.get("endDate")!=null && !"".equals(param.get("endDate")+"")
                        && param.get("key")!=null && "2".equals(param.get("key")+"")){
                    try{
                        beginTime = DateUtil.format(DateUtil.parse(String.valueOf(param.get("startDate"))), "yyyyMMdd");
                        endTime = DateUtil.format(DateUtil.parse(String.valueOf(param.get("endDate"))), "yyyyMMdd");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                System.out.println("开始查询中台------------》");
                JSONArray cstList  = this.getZtTransactionDate("_ALL_",ZTDKHURL,beginTime,endTime);
                System.out.println("中台查询完毕------------》查询到数据  "+cstList.size()+"条");
                boolean b = cstList != null && cstList.size()>0;
                for(SeniorBrokerActiveExcel model : list){
                    if(b){
                        for (int i = 0; i < cstList.size(); i++) {
                            Map retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()), Map.class);
                            if (model.getAccountId()==null){
                                if (model.getBrokerId().equalsIgnoreCase(String.valueOf(retData.get("brokerId")))
                                        && model.getKindeeProjectID().equalsIgnoreCase(String.valueOf(retData.get("projId")))){
                                    model.setCntrtCnt(model.getCntrtCnt() + MapUtils.getIntValue(retData, "cntrtCnt", 0));
                                    model.setOrderCnt(model.getOrderCnt() + MapUtils.getIntValue(retData, "orderCnt", 0));
                                }
                            }else{
                                if (model.getAccountId().equalsIgnoreCase(String.valueOf(retData.get("acctId")))
                                        && model.getBrokerId().equalsIgnoreCase(String.valueOf(retData.get("brokerId")))
                                        && model.getKindeeProjectID().equalsIgnoreCase(String.valueOf(retData.get("projId")))
                                ) {
                                    model.setCntrtCnt(model.getCntrtCnt() + MapUtils.getIntValue(retData, "cntrtCnt", 0));
                                    model.setOrderCnt(model.getOrderCnt() + MapUtils.getIntValue(retData, "orderCnt", 0));
                                }
                            }

                        }
                    }
                    //查询项目名称
                    Object[] oArray = model.toPublicData(exportType == 2);
                    dataset.add(oArray);
                }
                System.out.println("开始导出Excel------------》");
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(excelName,headers,dataset,excelName,response,null);
                System.out.println("开始导出Excel完成!------------》");
            }
            //记录导出日志
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            excelExportLog.setIsDown("1");
            Long export = System.currentTimeMillis();
            Long exporttime = export-nowtime;
            String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
            excelExportLog.setWaitTime("0");
            excelExportLog.setExportTime(exportTime);
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
            System.out.println("导出日志添加成功！------------》");
        }catch (Exception e){
            e.printStackTrace();
            excelExportLog.setExportStatus("3");
            excelExportLog.setExceptionMessage(e.getMessage());
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }

    }



    /**
     * 获取活动下的二级经纪人
     *
     * @param param 大客户经理人账号
     * @return result
     * */
    @Override
    public PageInfo<Map<String,Object>> getBrokerByActiveId(Map<String, Object> param) {
        PageHelper.startPage((Integer) param.get("pageNum"), (Integer)param.get("pageSize"));
        String  activeId = MapUtils.getString(param,"ActiveId");
        if ( StrUtil.isBlank(activeId)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        List<Map<String,Object>> list = seniorBrokerMapper.getBrokerByActiveId(param);
        JSONArray cstList  = this.getZtTransactionDate(activeId,ZTJJRHDURL,null,null);
        boolean b = cstList != null && cstList.size()>0;
        for(Map<String,Object> map : list){
            if(b){
                for (Object o : cstList) {
                    Map retData = JSONObject.toJavaObject(JSON.parseObject(o.toString()), Map.class);
                    if (activeId.equalsIgnoreCase(String.valueOf(retData.get("actyId")))
                            && MapUtils.getString(map, "BrokerId").equalsIgnoreCase(String.valueOf(retData.get("brokerId")))
                    ) {
                        map.put("cntrtCnt", MapUtils.getIntValue(retData, "cntrtCnt", 0) + MapUtils.getIntValue(retData, "cntrtCnt", 0));
                        map.put("orderCnt", MapUtils.getIntValue(retData, "orderCnt", 0) + MapUtils.getIntValue(retData, "orderCnt", 0));
                    }
                }
            }else{
                map.put("cntrtCnt", MapUtils.getIntValue(map, "cntrtCnt", 0));
                map.put("orderCnt", MapUtils.getIntValue(map, "orderCnt", 0));
            }
        }
        return new PageInfo<>(list);
    }

    /**
     * 获取活动下的客户
     *
     * @param param 大客户经理人账号
     * @return result
     * */
    @Override
    public PageInfo<Map<String,Object>> getCustomerByActiveId(Map<String, Object> param) {
        PageHelper.startPage((Integer) param.get("pageNum"), (Integer)param.get("pageSize"));
        String  activeId = MapUtils.getString(param,"ActiveId");
        if ( StrUtil.isBlank(activeId)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        return new PageInfo<>(seniorBrokerMapper.getCustomerByActiveId(param));
    }


    /**
     * 获取二级经纪人数据
     *
     * @param param 大客户经理人账号
     * @return result
     * */
    @Override
    public PageInfo<Map<String,Object>> getSeniorBroker(Map<String, Object> param) {
        String ids = MapUtils.getString(param, "proIds");
        List<String> projectIdList = new ArrayList<>();
        if ("".equals(ids)){
            Map map = new HashMap();
            map.put("UserName", SecurityUtils.getUsername());
            Map userInfoMap = authMapper.mGetUserInfo(map);
            List<String> fullpath = projectMapper.findFullPath(map);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(SecurityUtils.getUsername(), "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList != null && mapList.size() > 0) {
                for (Map proMap : mapList) {
                    projectIdList.add(proMap.get("projectId") + "");
                }
            }
        }else{
            String[] idss = ids.split(",");
            projectIdList = Arrays.asList(idss);
        }
        String beginTime = "19700101";
        String endTime = DateUtil.format(new Date(), "yyyyMMdd");
        if (param.get("startDate")!=null && !"".equals(param.get("startDate")+"") && param.get("endDate")!=null && !"".equals(param.get("endDate")+"")
        && param.get("key")!=null && "2".equals(param.get("key")+"")){
            try{
                beginTime = DateUtil.format(DateUtil.parse(String.valueOf(param.get("startDate"))), "yyyyMMdd");
                endTime = DateUtil.format(DateUtil.parse(String.valueOf(param.get("endDate"))), "yyyyMMdd");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        param.put("projectIdList",projectIdList);
        PageHelper.startPage((Integer) param.get("pageNum"), (Integer)param.get("pageSize"));
        List<Map<String,Object>> list = seniorBrokerMapper.getSeniorBroker(param);
        JSONArray cstList  = null;
        //注释调用中台
//        cstList  =  this.getZtTransactionDate("_ALL_",ZTDKHURL,beginTime,endTime);
        boolean b = cstList != null && cstList.size()>0;
        for(Map<String,Object> map : list){
            map.put("cntrtCnt", 0);
            map.put("orderCnt", 0);
            if(b){
                for (int i = 0; i < cstList.size(); i++) {
                    Map retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()), Map.class);
                    if (MapUtils.getString(map,"AccountId") != null && MapUtils.getString(map,"AccountId").equalsIgnoreCase(String.valueOf(retData.get("acctId"))) &&
                        MapUtils.getString(map,"BrokerId") != null  && MapUtils.getString(map,"BrokerId").equalsIgnoreCase(String.valueOf(retData.get("brokerId"))) &&
                        MapUtils.getString(map,"KindeeProjectID") != null  && MapUtils.getString(map,"KindeeProjectID").equalsIgnoreCase(String.valueOf(retData.get("projId")))
                    ) {
                        map.put("cntrtCnt",MapUtils.getIntValue(map, "cntrtCnt", 0) + MapUtils.getIntValue(retData, "cntrtCnt", 0));
                        map.put("orderCnt",MapUtils.getIntValue(map, "orderCnt", 0) + MapUtils.getIntValue(retData, "orderCnt", 0));
                    }
                }
            }
        }
        return new PageInfo<>(list);
    }

    /**
     * 获取项目的大客户
     *
     * @param param 大客户经理人账号
     * @return result
     * */
    @Override
    public ResultBody getAccountProject(Map<String, Object> param) {
        String  brokerId = MapUtils.getString(param,"projectId");
        if ( StrUtil.isBlank(brokerId)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        List<Map<String,Object>> list = seniorBrokerMapper.getAccountProject(brokerId);
        return ResultBody.success(list);
    }


    /**
     * 获取二级经纪人活动数据
     *
     * @param param 大客户经理人账号
     * @return result
     * */
    @Override
    public PageInfo<Map<String,Object>> getSeniorBrokerActive(Map<String, Object> param) {
        String  brokerId = MapUtils.getString(param,"BrokerId");
        if ( StrUtil.isBlank(brokerId)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        PageHelper.startPage((Integer) param.get("pageNum"), (Integer)param.get("pageSize"));
        List<Map<String,Object>> list = seniorBrokerMapper.getSeniorBrokerActive(param);
        JSONArray cstList  = null;
        //注释查询中台
//        cstList  = this.getZtTransactionDate("_ALL_",ZTJJRHDURL,null,null);
        boolean b = cstList != null && cstList.size()>0;
        for(Map<String,Object> map : list){
            if(b){
                for (int i = 0; i < cstList.size(); i++) {
                    Map retData = JSONObject.toJavaObject(JSON.parseObject(cstList.get(i).toString()), Map.class);
                    if (brokerId.equalsIgnoreCase(String.valueOf(retData.get("brokerId")))
                            && MapUtils.getString(map,"ActiveId").equalsIgnoreCase(String.valueOf(retData.get("acctId")))
                    ) {
                        map.put("cntrtCnt",MapUtils.getIntValue(map, "cntrtCnt", 0) + MapUtils.getIntValue(retData, "cntrtCnt", 0));
                        map.put("orderCnt",MapUtils.getIntValue(map, "orderCnt", 0) + MapUtils.getIntValue(retData, "orderCnt", 0));
                    }
                }
            }else{
                map.put("cntrtCnt", MapUtils.getIntValue(map, "cntrtCnt", 0));
                map.put("orderCnt", MapUtils.getIntValue(map, "orderCnt", 0));
            }
        }
        return new PageInfo<>(list);
    }

    /**
     * 获取二级经纪人分配历史
     *
     * @param param 大客户经理人账号
     * @return result
     * */
    @Override
    public PageInfo<Map<String,Object>> getBrokerAccountRecords(Map<String, Object> param) {
        PageHelper.startPage((Integer) param.get("pageNum"), (Integer)param.get("pageSize"));
        List<Map<String,Object>> list = seniorBrokerMapper.getBrokerAccountRecords(param);
        return new PageInfo<>(list);
    }

    /**
     * 获取二级经纪人分配历史
     *
     * @return result
     * */
    @Override
    public List<Map<String,Object>> getDkhCfpType() {
        return seniorBrokerMapper.getDkhCfpType();
    }


    /**
     * 二级经纪人分配
     *
     * */
    @Override
    @Transactional
    public ResultBody redistributionAccountManager(BrokerAccountForm brokerAccountForm) {
        if(StringUtils.isBlank(brokerAccountForm.getProjectId()) ||
                brokerAccountForm.getBrokerActIdList() == null || brokerAccountForm.getBrokerActIdList().size() == 0){
            return  ResultBody.error(2001,"参数错误");
        }

        //查询分配经纪人客户业绩
        List<AccountPerformance> accountPerformanceList = seniorBrokerMapper.getAccountPerformanceList(brokerAccountForm.getBrokerActIdList());
        if (accountPerformanceList.size() > 0) {
            //查询大客户经理活动id
            List<String> activityIdList = seniorBrokerMapper.getActivityByAccountId(brokerAccountForm.getUserId(), brokerAccountForm.getProjectId());
            List<String> idList = new ArrayList<>();
            List<String> idListTwo = new ArrayList<>();
            for (AccountPerformance accountPerformance : accountPerformanceList) {
                if (StringUtils.isNotBlank(accountPerformance.getActiveId())
                        && !activityIdList.contains(accountPerformance.getActiveId())
                        && "1".equals(accountPerformance.getClueStatus())) {

                    idList.add(accountPerformance.getId());
                }else{
                    if("1".equals(accountPerformance.getClueStatus())){
                        idListTwo.add(accountPerformance.getId());
                    }
                }
            }
            //更新业绩归属
            if(idList.size() > 0){
                seniorBrokerMapper.updateAccountPerformance(idList,brokerAccountForm.getUserId());
            }
            //更新大客户经理
            if(idListTwo.size() > 0){
                seniorBrokerMapper.updateAccountPerformanceTwo(idListTwo,brokerAccountForm.getUserId());
            }
        }
        //保存分配记录
        //批次id
        String batchId = UUID.randomUUID().toString();
        BrokerAccountRecordsBatch brokerAccountRecordsBatch = new BrokerAccountRecordsBatch();
        brokerAccountRecordsBatch.setId(batchId);
        brokerAccountRecordsBatch.setAccountId(brokerAccountForm.getUserId());
        brokerAccountRecordsBatch.setCountNumber(brokerAccountForm.getBrokerActIdList().size() + "");
        brokerAccountRecordsBatch.setCreateUser(SecurityUtils.getUserId());
        brokerAccountRecordsBatch.setEntrance("后台重分配");
        brokerAccountRecordsBatch.setReason("后台重分配");
        brokerAccountRecordsBatch.setProjectId(brokerAccountForm.getProjectId());
        seniorBrokerMapper.saveBrokerAccountRecordsBatch(brokerAccountRecordsBatch);
        //1.新增分配记录
        List<BrokerAccountRecords> brokerAccountRecordsList = this.addBrokerAccountRecords(brokerAccountForm,batchId);
        //2.新增分配记录
        seniorBrokerMapper.saveBrokerAccountRecords(brokerAccountRecordsList);
        //3.删除大客户经理二级经纪人关联表数据
        //seniorBrokerMapper.deleteBrokerAccount(brokerAccountForm.getBrokerActIdList());
        //4.保存大客户经理二级经纪人关联数据查询业绩归属表数据
        //List<BrokerAccount> brokerAccountList = this.addBrokerAccount(brokerAccountForm);
        //seniorBrokerMapper.saveBrokerAccount(brokerAccountList);
        //更新大客户经理
        seniorBrokerMapper.updateBrokerAccountId(brokerAccountForm.getBrokerActIdList(),brokerAccountForm.getUserId());
        //5.添加经纪人分配记录
        List<Map> mapLists = seniorBrokerMapper.getOldAccountManagerL(brokerAccountForm.getBrokerActIdList());
        List<Map> mapList = this.addUpdateUserLog(brokerAccountForm,mapLists);
        seniorBrokerMapper.recordUpdateUserLog(mapList);
        //发送消息
        List<String> list = new ArrayList<>();
        String content = "";
        String id = UUID.randomUUID().toString();
        Message message = new Message();
        if(mapLists.size() > 1){
            message.setExt2("1");
            content = brokerAccountForm.getUserName() + "_渠道经理"+brokerAccountForm.getEmployeeName()+"，为您分配「"+mapLists.get(0).get("brokerName")+"」等"+brokerAccountForm.getBrokerActIdList().size()+"个二级经纪人，点击查看详情。";
        }else{
            message.setExt2(null);
            content = brokerAccountForm.getUserName() + "_渠道经理"+brokerAccountForm.getEmployeeName()+"为您分配了二级经纪人「"+mapLists.get(0).get("brokerName")+"」，点击查看详情。";
        }
        message.setSubject("变更大客户经理提醒");
        message.setContent(content);
        message.setSender(brokerAccountForm.getUserId());
        message.setMessageType(2003);
        message.setIsDel(0);
        message.setReceiver(brokerAccountForm.getAccountId());
        message.setIsRead(0);
        message.setIsPush(2);
        message.setIsNeedPush(2);
        message.setProjectId(brokerAccountForm.getProjectId());
        message.setProjectClueId(null);
        message.setId(id);
        //保存消息
        seniorBrokerMapper.saveMessageInfo(message);
        //保存关联关系
        if(brokerAccountForm.getBrokerActIdList().size() > 0){
            List<MessageClueRelation> messageClueRelationList = new ArrayList<>();
            for (String str : brokerAccountForm.getBrokerActIdList()){
                MessageClueRelation messageClueRelation = new MessageClueRelation();
                messageClueRelation.setMessageId(id);
                messageClueRelation.setBrokerId(str);
                messageClueRelationList.add(messageClueRelation);
            }
            seniorBrokerMapper.saveMessageClueRelation(messageClueRelationList);
        }
        list.add(content);
        redisUtil.lPush("pushMessage", list);
        //经纪人消息
        if(mapLists.size() > 0) {
            List<Message> messageList = new ArrayList<>();
            //查询所有楼盘
            List<String> buildNameList = seniorBrokerMapper.getAllBuildBook(null, brokerAccountForm.getProjectId());
            String allBuildName = "";
            if (buildNameList.size() > 0) {
                allBuildName = String.join("、", buildNameList);
            }
            for (Map map : mapLists) {
                Message messageTwo = new Message();
                messageTwo.setSubject("变更大客户经理提醒");
                messageTwo.setContent("我们已将您在" + allBuildName + "的大客户经理,由" + map.get("accountName") + "变更为" + brokerAccountForm.getEmployeeName() + "，点击查看详情。");
                messageTwo.setSender(brokerAccountForm.getUserId());
                messageTwo.setMessageType(2203);
                messageTwo.setIsDel(0);
                messageTwo.setReceiver(String.valueOf(map.get("brokerId")));
                messageTwo.setIsRead(0);
                messageTwo.setIsPush(2);
                messageTwo.setIsNeedPush(2);
                messageTwo.setProjectId(brokerAccountForm.getProjectId());
                messageTwo.setProjectClueId(null);
                messageTwo.setId(UUID.randomUUID().toString());
                messageTwo.setExt3(String.valueOf(map.get("brokerId")));
                messageList.add(messageTwo);
            }
            if (messageList.size() > 0) {
                seniorBrokerMapper.insertMessageList(messageList);
            }
        }
        return ResultBody.success("分配成功");
    }




    /**
     * 新增分配记录表
     *
     * @param brokerAccountForm
     * @return
     */
    private List<BrokerAccountRecords> addBrokerAccountRecords(BrokerAccountForm brokerAccountForm,String batchId) {
        List<BrokerAccountRecords> brokerAccountRecordsList = new ArrayList<>();
        List<Map> mapList = seniorBrokerMapper.getOldAccountManagerL(brokerAccountForm.getBrokerActIdList());
        if (mapList.size() > 0) {
            for (Map map : mapList) {
                BrokerAccountRecords brokerAccountRecords = new BrokerAccountRecords();
                brokerAccountRecords.setAccountId(brokerAccountForm.getUserId());
                brokerAccountRecords.setAccountIdOld(MapUtils.getString(map,"accountId",null));
                brokerAccountRecords.setAccountMobile(brokerAccountForm.getUserMobile());
                brokerAccountRecords.setAccountMobileOld(MapUtils.getString(map,"accountMobile",null));
                brokerAccountRecords.setAccountName(brokerAccountForm.getEmployeeName());
                brokerAccountRecords.setAccountNameOld(MapUtils.getString(map,"accountName",null));
                brokerAccountRecords.setBrokerId(MapUtils.getString(map,"brokerId",null));
                brokerAccountRecords.setBrokerMobile(MapUtils.getString(map,"brokerMobile",null));
                brokerAccountRecords.setBrokerName(MapUtils.getString(map,"brokerName",null));
                brokerAccountRecords.setBrokerOpenId(MapUtils.getString(map,"openId",null));
                brokerAccountRecords.setProjectId(brokerAccountForm.getProjectId());
                brokerAccountRecords.setProjectIdOld(MapUtils.getString(map,"projectid",null));
                brokerAccountRecords.setProjectName(brokerAccountForm.getProjectName());
                brokerAccountRecords.setProjectNameOld(MapUtils.getString(map,"projectname",null));
                brokerAccountRecords.setRemarks(brokerAccountForm.getRemarks());
                brokerAccountRecords.setReason(brokerAccountForm.getReason());
                brokerAccountRecords.setEntrance("后台重分配");
                brokerAccountRecords.setCreator(SecurityUtils.getUserId());
                brokerAccountRecords.setBatchId(batchId);
                brokerAccountRecordsList.add(brokerAccountRecords);
            }
        }
        return brokerAccountRecordsList;
    }

    /***
     *
     * @param brokerAccountForm
     *@return {}
     *@throws
     *@Description: 添加经纪人分配日志
     *@author FuYong
     *@date 2020/10/23 15:27
     */
    private List<Map> addUpdateUserLog(BrokerAccountForm brokerAccountForm,List<Map> mapLists){
        List<Map> mapList = new ArrayList<>();
        if (mapLists.size() > 0) {
            for (Map map : mapLists) {
                Map maps = new HashMap();
                maps.put("brokerId",MapUtils.getString(map,"brokerId",null));
                maps.put("brokerName",MapUtils.getString(map,"brokerName",null));
                maps.put("beforeChange",MapUtils.getString(map,"accountName",null) +"-"+ MapUtils.getString(map,"projectname",null));
                maps.put("afterAlteration",MapUtils.getString(map,"brokerId",null));
                maps.put("userId",brokerAccountForm.getUserName() + "-" + brokerAccountForm.getProjectName());
                maps.put("creator",brokerAccountForm.getCreator());
                mapList.add(maps);
            }
        }
        return mapList;
    }


    /**
     * 查询中台获取经纪人活动数据
     *
     * @param param 参数
     * @param url 路径
     * @return JSONArray
     * */
    public JSONArray getZtTransactionDate(String param,String url,String beginTime,String endTime){
        //查询中台获取经纪人活动数据
        StringBuilder sb = new StringBuilder();
        if (beginTime==null){
            beginTime = "19700101";
            endTime = DateUtil.format(new Date(), "yyyyMMdd");
        }
        sb.append(beginTime).append(",").append(endTime).append(",").append(param);
        Map<String, String> reqMap;
        reqMap = new HashMap<>(1);
        reqMap.put("params", sb.toString());
        //调用中台获取转化率数据
        String result1 = HttpClientUtil.sendGet(url, reqMap);
        Map resMaps = JSONObject.parseObject(result1, Map.class);
        JSONArray dataList = null;
        //判断如果存在数据放入集合
        if (ObjectUtil.isNotNull(resMaps.get("retData"))) {
            dataList = JSONObject.parseArray(String.valueOf(resMaps.get("retData")));
        }
        return dataList;
    }

}
