package cn.visolink.system.allpeople.examine.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.allpeople.examine.dao.ExamineDao;
import cn.visolink.system.allpeople.examine.model.*;
import cn.visolink.system.allpeople.examine.service.ExamineService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.seniorbroker.mapper.SeniorBrokerMapper;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName ExamineServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/1/14 19:21
 **/
@Service
public class ExamineServiceImpl implements ExamineService {
    @Autowired
    private ExamineDao examineDao;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SeniorBrokerMapper seniorBrokerMapper;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("#0.00");
    @Value("${ZTJJRURL}")
    private String ZTJJRURL;
    @Override
    public PageInfo<Examine> getExamineList(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex, pageSize);
        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime")+"")));
                map.put("endTime",sf.format(sf.parse(map.get("endTime")+"")));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        List<Examine> list = examineDao.getExamineList(map);
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void updatePeople(Map map) {
        if (map.get("ids")!=null){
            String ids = map.get("ids")+"";
            String[] idss = ids.split(",");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < idss.length; i++) {
                if (i==idss.length-1){
                    sb.append("'"+idss[i]+"'");
                }else{
                    sb.append("'"+idss[i]+"',");
                }
            }
            map.put("ids",sb.toString());
            examineDao.updatePeople(map);
        }
    }

    @Override
    public List<Map> getCitys() {

        return examineDao.getCitys();
    }

    @Override
    public Map getBrokerUserList(Map paramMap) {
        Map resultMap = new HashMap();
        List<Examine> list = new ArrayList<>();
        if (paramMap.get("search")!=null && !"".equals(paramMap.get("search"))){
            String search = paramMap.get("search").toString();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                paramMap.put("Mobile",search);
            } else {
                paramMap.put("Name",search);
            }
        }

        int pageIndex = 1;
        int pageSize = 10;
        int Sum = 0;//总数
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"") && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
            try{
                paramMap.put("beginTime",sf.format(sf.parse(paramMap.get("startTime")+"")));
                paramMap.put("endTime",sf.format(sf.parse(paramMap.get("endTime")+"")));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        //获取总数
        Sum = examineDao.getBrokerUserListCount(paramMap);
        //计算跳过的数量
        if (Sum>0){
            pageIndex = (pageIndex-1)*pageSize;
            paramMap.put("pageIndex",pageIndex);
            paramMap.put("pageSize",pageSize);
            //查询数据
            list = examineDao.getBrokerUserList(paramMap);
        }

        resultMap.put("total",Sum);
        resultMap.put("list",list);
        return resultMap;
    }

    @Override
    public Examine getBrokerUser(Map paramMap) {
        Examine examine = examineDao.getBrokerUser(paramMap);
        return examine;
    }

    @Override
    public PageInfo<Customer> getBrokerUserCustomer(Map paramMap) {
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageIndex")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex, pageSize);
        if (paramMap.get("search")!=null){
            String search = paramMap.get("search").toString();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                paramMap.put("CustomerMobile",search);
            } else {
                paramMap.put("CustomerName",search);
            }
        }
        List<Customer> list = examineDao.getBrokerUserCustomer(paramMap);
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public PageInfo<UserEdit> getBrokerUserEditLog(Map paramMap) {
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageIndex")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex, pageSize);
        List<UserEdit> list = examineDao.getBrokerUserEditLog(paramMap);
        for (UserEdit u:list) {
            if ("经纪人银行卡信息".equals(u.getEditField())){
                if (u.getBeforeChange()!=null && !"".equals(u.getBeforeChange())){
                    String[] s = u.getBeforeChange().split("、");
                    if (s.length>1){
                        int len = s[1].length();
                        if (len>5){
                            s[1] = "************"+s[1].substring(len-4);
                        }
                    }
                    String re = StringUtils.join(s,'、');
                    u.setBeforeChange(re);
                }

                if (u.getAfterAlteration()!=null && !"".equals(u.getAfterAlteration())){
                    String[] s = u.getAfterAlteration().split("、");
                    if (s.length>1){
                        int len = s[1].length();
                        if (len>5){
                            s[1] = "************"+s[1].substring(len-4);
                        }
                    }
                    String re = StringUtils.join(s,'、');
                    u.setAfterAlteration(re);
                }

            }
        }
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public static void main(String[] args){
        String ss = "沈铭燕、6217002430023250880、中国建设银行、是";
        String[] s = ss.split("、");
        System.out.println(JSON.toJSONString(s));
        String re = StringUtils.join(s,'、');
        System.out.println(re);
    }
    @Override
    public List<Map> getProjectList(Map paramMap) {
        List<Map> list = examineDao.getProjectList(paramMap);
        return list;
    }

    @Override
    public List<Map> getAllProject() {
        return examineDao.getAllProject();
    }

    @Override
    public void brokerUserExport(HttpServletRequest request, HttpServletResponse response, String exportVoMap) {
        ExportVo exportVo = JSONObject.parseObject(exportVoMap,ExportVo.class);
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        Map paramMap = new HashMap();
        //获取经纪人数据
        List<Examine> list = new ArrayList<>();
        if (exportVo.getSearch()!=null && !"".equals(exportVo.getSearch())){
            String search = exportVo.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                paramMap.put("Mobile",search);
            } else {
                paramMap.put("Name",search);
            }
        }
        if (exportVo.getRoleName()!=null && !"".equals(exportVo.getRoleName())){
            paramMap.put("role",exportVo.getRoleName());
        }
        if (exportVo.getCompanyName()!=null && !"".equals(exportVo.getCompanyName())){
            paramMap.put("companyName",exportVo.getCompanyName());
        }

        if (exportVo.getBeginTime()!=null && !"".equals(exportVo.getBeginTime()) && exportVo.getEndTime()!=null && !"".equals(exportVo.getEndTime())){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                paramMap.put("beginTime",sf.format(sf.parse(exportVo.getBeginTime())));
                paramMap.put("endTime",sf.format(sf.parse(exportVo.getEndTime())));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        list = examineDao.getBrokerUserListExport(paramMap);
        if(list.size()>0){
            try{
                excelName = "渠道人员明细表";
                int num = 0;
                String[] headers = list.get(0).getCourtCaseTitle();
                for(Examine model : list){
                    num++;
                    boolean isAllPhone = true;
                    //判断是否全号导出
                    if ("1".equals(exportVo.getIsAllPhone()) || "".equals(exportVo.getIsAllPhone())){
                        isAllPhone = false;
                    }
                    model.setNum(num);
                    Object[] oArray = model.toPublicData(isAllPhone);
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("渠道人员明细表",headers,dataset,excelName,response,null);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public String brokerUserExportNew(String exportVoMap) {
        ExportVo exportVo = JSONObject.parseObject(exportVoMap,ExportVo.class);
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();

        Map map=new HashMap();
        map.put("UserName", exportVo.getUserName());
        Map userInfoMap = authMapper.mGetUserInfo(map);
        List<String> fullpath = projectMapper.findFullPath(map);
        StringBuffer sbs = new StringBuffer();
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sbs.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sbs.append("or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        List<Map> mapList = projectMapper.findProjectListByUserName( exportVo.getUserName()+"","",userInfoMap.get("JobCode").toString(),sbs.toString());
        List<String> proIdList = new ArrayList<>();
        if (mapList!=null && mapList.size()>0){
            for (Map proMap:mapList) {
                proIdList.add(proMap.get("projectId")+"");
            }
        }
        excelExportLog.setCreator(exportVo.getUserId());
        excelExportLog.setId(id);
        excelExportLog.setMainType("3");
        excelExportLog.setMainTypeDesc("经纪人管理");
        excelExportLog.setSubType("J1");
        excelExportLog.setSubTypeDesc("经纪人列表");
        excelExportLog.setIsAsyn("1");
        if ("1".equals(exportVo.getIsAllPhone()) || "".equals(exportVo.getIsAllPhone())){
            excelExportLog.setExportType("1");
        }else{
            excelExportLog.setExportType("2");
        }
        excelExportLog.setExportStatus("1");
        //sql拼接
        StringBuffer sbAll = new StringBuffer();
        StringBuffer sbParam = new StringBuffer();
        if (exportVo.getStatus()!=null && !"".equals(exportVo.getStatus())){
            sbParam.append(" and ro.Status = '"+exportVo.getStatus()+"'");
        }
        if (exportVo.getAuthenticationStatus()!=null && !"".equals(exportVo.getAuthenticationStatus())){
            sbParam.append(" and u.AuthenticationStatus =  '"+exportVo.getAuthenticationStatus()+"'");
        }
        if (exportVo.getIsOA()!=null && !"".equals(exportVo.getIsOA())){
            if ("1".equals(exportVo.getIsOA())){
                sbParam.append(" and u.AccountId is not null and u.AccountId !=''");
            }else{
                sbParam.append(" and (u.AccountId is null or u.AccountId ='')");
            }

        }
        if (exportVo.getSearch()!=null && !"".equals(exportVo.getSearch())){
            String search = exportVo.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                sbParam.append(" and (u.Mobile like '%"+search+"%' or ro.IDCard like '%"+search+"%')");
            } else {
                sbParam.append(" and u.Name like '%"+search+"%'");
            }
        }
        if (exportVo.getBeginTime()!=null && !"".equals(exportVo.getBeginTime()) && exportVo.getEndTime()!=null && !"".equals(exportVo.getEndTime())){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                sbParam.append(" and ro.CreateTime between '"+sf.format(sf.parse(exportVo.getBeginTime()))+"' and '"+sf.format(sf.parse(exportVo.getEndTime()))+"'");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //获取经纪人角色查询条件
        if (exportVo.getRoleName()!=null && !"".equals(exportVo.getRoleName())){
            String[] roleNames = exportVo.getRoleName().split(",");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < roleNames.length; i++) {
                if (i==roleNames.length-1){
                    sb.append("'"+roleNames[i]+"'");
                }else{
                    sb.append("'"+roleNames[i]+"',");
                }
            }
            sbParam.append(" and ro.RoleName in ("+sb.toString()+")");
        }
        //获取经纪人注册来源查询条件
        if (exportVo.getRegistFroms()!=null && !"".equals(exportVo.getRegistFroms())){
            String[] RegistFroms = exportVo.getRegistFroms().split(",");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < RegistFroms.length; i++) {
                if (i==RegistFroms.length-1){
                    sb.append("'"+RegistFroms[i]+"'");
                }else{
                    sb.append("'"+RegistFroms[i]+"',");
                }
            }
            sbParam.append(" and u.RegistFrom in ("+sb.toString()+")");
        }
        if (exportVo.getRecommend()!=null && !"".equals(exportVo.getRecommend())){
            sbParam.append(" and(u.id in (select a.id from a_broker_user a left JOIN a_broker_user b on a.RegistRefcommend = b.OpenId where ((a.RegistFrom = '小程序分享' or a.RegistFrom = '小程序首页分享' or a.RegistFrom = '小程序新闻分享' or a.RegistFrom = '小程序户型分享' or a.RegistFrom = '小程序楼盘分享' or a.RegistFrom = '小程序活动分享' or a.RegistFrom = '小程序大客户活动分享' or a.a.RegistFrom = '小程序楼盘动态分享' or a.RegistFrom = '小程序名片分享' or a.RegistFrom = '海报分享' or a.RegistFrom = '海报楼盘分享' or a.RegistFrom = '海报活动分享' or a.RegistFrom = '海报名片分享') and b.Name like concat('%','"+exportVo.getRecommend()+"','%')) or ((a.RegistFrom = 'APP推荐' or a.RegistFrom = 'APP小程序名片分享' or a.RegistFrom = 'APP海报名片分享') and a.RegistRefcommend like concat('%','"+exportVo.getRecommend()+"','%'))))");
        }

        if (exportVo.getWeChatUserName()!=null && !"".equals(exportVo.getWeChatUserName())){
            sbParam.append(" and(u.id in (select a.id from a_broker_user a left JOIN a_broker_user b on a.RegistRefcommend = b.OpenId where (a.RegistFrom = '小程序分享' or a.RegistFrom = '小程序首页分享' or a.RegistFrom = '小程序新闻分享' or a.RegistFrom = '小程序楼盘分享' or a.RegistFrom = '小程序户型分享' or a.RegistFrom = '小程序活动分享' or a.RegistFrom = '小程序名片分享' or a.RegistFrom = '小程序大客户活动分享' or a.RegistFrom = '小程序楼盘动态分享' or a.RegistFrom = '海报分享' or a.RegistFrom = '海报楼盘分享' or a.RegistFrom = '海报活动分享' or a.RegistFrom = '海报名片分享') and b.WeChatUserName like concat('%','"+exportVo.getWeChatUserName()+"','%')))");
        }

        if (exportVo.getBrokerLevel()!=null && !"".equals(exportVo.getBrokerLevel())){
            sbParam.append(" and u.BrokerLevel = '"+exportVo.getBrokerLevel()+"'");
        }

        //判断是否查询无注册项目人员
        if (exportVo.getNoReg()!=null && "0".equals(exportVo.getNoReg())){
            sbAll.append("SELECT distinct ro.BrokerId as ID, ro.CityName, ro.RoleName, u.Mobile MobileAll, concat(left(u.Mobile,3),'****',right(u.Mobile,4)) Mobile,(case when (u.AccountId is null or u.AccountId= '') then '未绑定' when (u.AccountId is not null and u.AccountId!= '') then '已绑定' else '' end) OAAccount, ro.IDCard, (case ro.Status when '0' then '禁用' when '1' then '启用' when '2' then '待审核' when '3' then '审核驳回' end) Status,(case u.BrokerLevel when '1' then '项目级' when '2' then '区域级' when '3' then '集团级' end) BrokerLevel, u.RegistFrom, (case when aex.ExtenActivityName is not null then aex.ExtenActivityName else u.RegistRefcommend end) as RegistRefcommend, '' RegistProject, u.Name, u.RealName, DATE_FORMAT(ro.CreateTime,'%Y-%m-%d %H:%i:%s') as CreateTime, 0 CustomerCnt, 0 VisitCnt, 0 InvalidCnt FROM a_broker_user u inner join a_user_role ro on ro.BrokerId =u.ID left join a_extension aex on aex.ID = u.RegistRefcommend and (u.RegistFrom = '策划推广' or u.RegistFrom = '旭客家首页推广码' or u.RegistFrom = '旭客家楼盘推广码' or u.RegistFrom = '旭客家户型推广码' or u.RegistFrom = '旭客家活动推广码' or u.RegistFrom = '旭客家新闻推广码') WHERE ro.isDel = 0 and u.RegistProject is null"+sbParam.toString()+" order by ro.CreateTime desc");
            excelExportLog.setAreaName("/");
            excelExportLog.setProjectId("/");
            excelExportLog.setProjectName("/");
        }else{
            String[] regProjectIDs = exportVo.getRegProjectID().split(",");
            //判断是否有权限查询这些项目
            List<String> arrayToList1= Arrays.asList(regProjectIDs);
            List<String> arrayList1=new ArrayList<String>(arrayToList1);//转换为ArrayLsit调用相关的remove方法
            //将无权限的删除
            for (int f = 0;f <arrayList1.size();f++) {
                if (!proIdList.contains(arrayList1.get(f))){
                    arrayList1.remove(f);
                    f--;
                }
            }
            //根据项目查询区域ID  将区域和集团都放入项目ID集合
            List<String> orgIds = examineDao.getOrgIdsByPros(arrayList1);
            if (orgIds.size()>0){
                arrayList1.addAll(orgIds);
            }
            arrayList1.add("00000001");
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(arrayList1);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
            String[] strings1 = new String[arrayList1.size()];
            arrayList1.toArray(strings1);
            String regProjectID = "'"+StringUtils.join(strings1,"','")+"'";
            sbAll.append("select t.*, s.CustomerCnt, s.VisitCnt, s.InvalidCnt from( SELECT distinct ro.BrokerId as ID, ro.CityName, ro.RoleName, concat(left(u.Mobile,3),'****',right(u.Mobile,4)) Mobile, u.Mobile MobileAll, (case when (u.AccountId is null or u.AccountId= '') then '未绑定' when (u.AccountId is not null and u.AccountId!= '') then '已绑定' else '' end) OAAccount, ro.IDCard, (case ro.Status when '0' then '禁用' when '1' then '启用' when '2' then '待审核' when '3' then '审核驳回' end) Status,(case u.BrokerLevel when '1' then '项目级' when '2' then '区域级' when '3' then '集团级' end) BrokerLevel, u.RegistFrom, (case when aex.ExtenActivityName is not null then aex.ExtenActivityName else u.RegistRefcommend end) as RegistRefcommend, (case u.BrokerLevel when '1' then bp.ProjectName else org.OrgName end) RegistProject, u.Name, u.RealName, DATE_FORMAT(ro.CreateTime,'%Y-%m-%d %H:%i:%s') as CreateTime FROM a_broker_user u inner join a_user_role ro on ro.BrokerId =u.ID INNER JOIN b_project_clues c on ro.BrokerId = c.ReportUserID and c.SourceType = 4 and c.projectId in ("+regProjectID+") LEFT JOIN b_project bp on bp.ID = u.RegistProject LEFT JOIN s_organization org on org.ID = u.RegistProject left join a_extension aex on aex.ID = u.RegistRefcommend and (u.RegistFrom = '策划推广' or u.RegistFrom = '旭客家首页推广码' or u.RegistFrom = '旭客家楼盘推广码' or u.RegistFrom = '旭客家户型推广码' or u.RegistFrom = '旭客家活动推广码' or u.RegistFrom = '旭客家新闻推广码') WHERE ro.isDel = 0"+sbParam.toString());
            sbAll.append(" UNION SELECT distinct ro.BrokerId as ID, ro.CityName, ro.RoleName, concat(left(u.Mobile,3),'****',right(u.Mobile,4)) Mobile, u.Mobile MobileAll,(case when (u.AccountId is null or u.AccountId= '') then '未绑定' when (u.AccountId is not null and u.AccountId!= '') then '已绑定' else '' end) OAAccount, ro.IDCard, (case ro.Status when '0' then '禁用' when '1' then '启用' when '2' then '待审核' when '3' then '审核驳回' end) Status,(case u.BrokerLevel when '1' then '项目级' when '2' then '区域级' when '3' then '集团级' end) BrokerLevel, u.RegistFrom, (case when aex.ExtenActivityName is not null then aex.ExtenActivityName else u.RegistRefcommend end) as RegistRefcommend, (case u.BrokerLevel when '1' then bp.ProjectName else org.OrgName end) RegistProject, u.Name, u.RealName, DATE_FORMAT(ro.CreateTime,'%Y-%m-%d %H:%i:%s') as CreateTime FROM a_broker_user u inner join a_user_role ro on ro.BrokerId =u.ID LEFT JOIN b_project bp on bp.ID = u.RegistProject LEFT JOIN s_organization org on org.ID = u.RegistProject left join a_extension aex on aex.ID = u.RegistRefcommend and (u.RegistFrom = '策划推广' or u.RegistFrom = '旭客家首页推广码' or u.RegistFrom = '旭客家楼盘推广码' or u.RegistFrom = '旭客家户型推广码' or u.RegistFrom = '旭客家活动推广码' or u.RegistFrom = '旭客家新闻推广码') WHERE ro.isDel = 0 and u.RegistProject in ("+regProjectID+")"+sbParam.toString());
            sbAll.append(") t LEFT JOIN( SELECT ReportUserID, sum( CASE WHEN ClueStatus < 9 THEN 1 ELSE 0 END) AS CustomerCnt, sum( CASE WHEN ClueStatus < 9 AND ClueStatus > 1 THEN 1 ELSE 0 END ) AS VisitCnt, sum( CASE WHEN now() > BrokerCustomerExpiryDate and ClueStatus <8 OR ClueStatus = 9 THEN 1 ELSE 0 END ) AS InvalidCnt FROM b_project_clues clue WHERE clue.SourceType = 4 and EXISTS ( SELECT ID FROM a_broker_user abu where abu.ID = clue.ReportUserID ) GROUP BY ReportUserID ) s ON t.ID = s.ReportUserID order by t.CreateTime desc");
        }
        excelExportLog.setDoSql(sbAll.toString());
        try{
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
        return "您当前导出数据多于5000条，请关注右上角下载任务状态";
    }

    @Override
    public ResultBody channelRegistration(Map paramMap) {
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<ChannelRegistration> list = examineDao.channelRegistration(paramMap);
        if (list==null){
            list = new ArrayList<>();
        }
        return ResultBody.success(new PageInfo<>(list));
    }

    @Override
    public ChannelRegistration channelDetail(Map paramMap) {
        ChannelRegistration channelRegistration = new ChannelRegistration();
        String type = paramMap.get("type")+"";
        //判断查询类型 1:渠道商 2：申请渠道商
        if ("1".equals(type)){
            channelRegistration = examineDao.channelDetail1(paramMap);
            //查询合同信息
            List<ChannelContract> channelContracts = examineDao.getChannelContracts(paramMap);
            channelRegistration.setChannelContracts(channelContracts);
        }else{
            channelRegistration = examineDao.channelDetail2(paramMap);
        }
        return channelRegistration;
    }

    @Override
    public ResultBody channelManagement(Map paramMap) {
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<ChannelRegistration> list = examineDao.channelManagement(paramMap);
        if (list==null){
            list = new ArrayList<>();
        }
        return ResultBody.success(new PageInfo<>(list));
    }

    @Override
    public String addblacklist(String id,String type) {
        String result = "";
        if ("1".equals(type)){
            result = "加入黑名单成功！！";
        }else{
            result = "移除黑名单成功！！";
        }
        examineDao.addblacklist(id,type);
        return result;
    }

    @Override
    public String channelAudit(Map paramMap) {
        paramMap.put("examineUser",SecurityUtils.getUserId());
        examineDao.channelAudit(paramMap);
        return "审批完成！";
    }

    @Override
    public void channelRegistrationExport(HttpServletRequest request, HttpServletResponse response, String exportVo) {
        Map paramMap = JSONObject.parseObject(exportVo,Map.class);
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<ChannelRegistration> list = examineDao.channelManagement(paramMap);
        if (list!=null){
            try{
                excelName = "申请渠道商列表";
                int num = 0;
                String[] headers = list.get(0).getCourtCaseTitle2();
                for(ChannelRegistration model : list){
                    num++;
                    model.setRownum(num+"");
                    Object[] oArray = model.toData2();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("申请渠道商列表",headers,dataset,excelName,response,null);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelManagementExport(HttpServletRequest request, HttpServletResponse response, String exportVo) {
        Map paramMap = JSONObject.parseObject(exportVo,Map.class);
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<ChannelRegistration> list = examineDao.channelManagement(paramMap);
        if (list!=null){
            try{
                excelName = "渠道商列表";
                int num = 0;
                String[] headers = list.get(0).getCourtCaseTitle1();
                for(ChannelRegistration model : list){
                    num++;
                    model.setRownum(num+"");
                    Object[] oArray = model.toData1();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("渠道商列表",headers,dataset,excelName,response,null);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
