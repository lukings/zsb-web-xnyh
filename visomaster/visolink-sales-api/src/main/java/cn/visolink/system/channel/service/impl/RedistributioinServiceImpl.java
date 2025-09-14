package cn.visolink.system.channel.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.message.model.form.MessageClueRelation;
import cn.visolink.message.model.form.MessageForm;
import cn.visolink.system.activity.model.vo.ActivityInfoVO;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.dao.PublicpoolDao;
import cn.visolink.system.channel.model.ClueOpportunityExtend;
import cn.visolink.system.channel.model.CustomerDistributionRecords;
import cn.visolink.system.channel.model.DistributionInfo;
import cn.visolink.system.channel.model.vo.CustomerDistributionRecordsVO;
import cn.visolink.system.channel.model.vo.ProjectProtectRuleVO;
import cn.visolink.system.channel.service.RedistributionService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.utils.AverageDataUtil;
import cn.visolink.utils.HttpRequestUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class RedistributioinServiceImpl implements RedistributionService {

    @Autowired
    private PublicpoolDao publicpoolDao;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ProjectCluesDao projectCluesDao;

    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${RedistributioinURL}")
    private String RedisbuURL;

    @Value("${MediaTypeURL}")
    private String mediaURL;

    @Value("${PerformanceURL}")
    private String performanceURL;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public PageInfo ProjectClueDeriveMustAcByToker(Map map) {
        PageHelper.startPage((Integer) map.get("pageNum"), (Integer) map.get("pageSize"));

        List<Map> list = publicpoolDao.ProjectClueDeriveMustAcByToker(map);

        PageInfo<Map> page = new PageInfo<>(list);
//        for (Map res : list) {
//            if (res.get("RedistributionType") == null) {
//                continue;
//            } else {
//                res.put("RedistributionType", publicpoolDao.getRelson(res.get("RedistributionType").toString()));
//            }
//
//        }
        return page;
    }

    @Override
    public PageInfo queryRedistributionRecord(Map map) {
//        if(CollectionUtils.isEmpty((Collection<?>) map.get("projectList")) && CollectionUtils.isEmpty((Collection<?>) map.get("newProjectList"))){
//            return new PageInfo<>();
//        }
        //获取用户权限 如果存在管理员可以查看所有 不是管理员只能查看自己操作的
        int isManger = projectCluesDao.getUserJobHsxt(SecurityUtils.getUserId());
        if (isManger == 0){
            map.put("userId",SecurityUtils.getUserId());
        }
        PageHelper.startPage((Integer) map.get("pageNum"), (Integer) map.get("pageSize"));
        List<CustomerDistributionRecordsVO> list = publicpoolDao.queryRedistributionRecord(map);
        PageInfo<CustomerDistributionRecordsVO> page = new PageInfo<>(list);
        return page;
    }

    @Override
    public void cluesRedistributionRecordExport(HttpServletRequest request, HttpServletResponse response, Map map) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("1");
        excelExportLog.setMainTypeDesc("渠道管理");
        excelExportLog.setSubType("Q6");
        excelExportLog.setSubTypeDesc("渠道重分配记录");
        if ("1".equals(map.get("isAll").toString())){
            excelExportLog.setExportType("2");
        }else{
            excelExportLog.setExportType("1");
        }
        excelExportLog.setIsAsyn("0");
        try{
            List<String> proIdList = new ArrayList<>();
            String[] ids = map.get("projectList").toString().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
            map.put("projectList",proIdList);
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setCreator(map.get("userId").toString());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
            //导出的文档下面的名字
            List<CustomerDistributionRecords> list = publicpoolDao.getCustomerDistributionClues(map);
            if (list.size()>0){
                String[] headers = list.get(0).getCourtCaseTitle();
                for (int i = 0; i < list.size(); i++) {
                    CustomerDistributionRecords customerDistributionRecords = list.get(i);
                    Object[] oArray = customerDistributionRecords.toExproData(map.get("isAll").toString());
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("渠道重分配明细", headers,dataset, "渠道重分配明细", response,null);
            }
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
    public String cluesRedistributionRecordExportNew(Map map) {
        String companycode = "";
        if (map.get("companycode")!=null && !"".equals(map.get("companycode")+"")){
            companycode = map.get("companycode")+"";
        }
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("1");
        excelExportLog.setMainTypeDesc("渠道管理");
        excelExportLog.setSubType("Q6");
        excelExportLog.setSubTypeDesc("渠道重分配记录");
        if ("1".equals(map.get("isAll").toString())){
            excelExportLog.setExportType("2");
        }else{
            excelExportLog.setExportType("1");
        }
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        List<String> proIdList = new ArrayList<>();
        String[] ids = map.get("projectList").toString().split(",");
        for (String proid : ids) {
            proIdList.add(proid);
        }
        map.put("projectList",proIdList);
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(map.get("userId").toString());
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        //sql拼接
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT brb.id redistributionBatchId, ba.EmployeeName as employeeName, DATE_FORMAT(bc.ConfirmTime,'%Y-%m-%d %H:%i:%s') confirmTime,(case brb.RedistributionGenre when 1 then '渠道台账' when 2 then '案场台账' when 3 then 'app' when 4 then '公共池' else '' end) as entrance, dict.DictName reason, bpc.CustomerName cstName, concat(left(bpc.CustomerMobile,3),'****',right(bpc.CustomerMobile,4)) as cstPhone, bpc.CustomerMobile as cstPhoneAll, (case bpc.ClueStatus when 1 then '报备' when 2 then '到访' when 3 then '排小卡' when 4 then '排大卡' when 5 then '订房' when 6 then '认筹' when 7 then '认购' when 8 then '签约' when 9 then '放弃' else '' end) as cstStatus, bc.SalesName salesName, DATE_FORMAT(bc.SalesAttributionTime,'%Y-%m-%d %H:%i:%s') salesAttributionTime, bc.OldSalesName oldSalesName, DATE_FORMAT(bc.OldTokerAttributionTime,'%Y-%m-%d %H:%i:%s') oldSalesAttributionTime, bpc.ProjectName projectName FROM b_redistribution_batch brb JOIN B_CustomerDistributionRecords bc ON brb.id = bc.RedistributionBatchId left join b_project_clues bpc on bpc.ProjectClueId = bc.ProjectClueId left join b_account ba on ba.id = brb.createUser left join (select DictCode,DictName from s_dictionary where pid = ( SELECT id FROM s_dictionary WHERE pid = -1 and DictCode = '1007') and IsDel = 0) dict on dict.DictCode = brb.RedistributionType WHERE (brb.RedistributionGenre=1 or (brb.RedistributionGenre=3 and (bc.Type = 9 or bc.Type = 10)))");
        String projectIds = "'"+StringUtils.join(proIdList.toArray(), "','")+"'";
        sb.append(" and brb.projectId in ("+projectIds+")");
        if (map.get("name")!=null && !"".equals(map.get("name")+"")){
            sb.append(" and bpc.CustomerName LIKE concat('%','"+map.get("name")+"','%')");
        }
        if (map.get("mobile")!=null && !"".equals(map.get("mobile")+"")){
            sb.append(" and bpc.CustomerMobile LIKE concat('%','"+map.get("mobile")+"','%')");
        }
        if (map.get("activeTab")!=null && "2".equals(map.get("activeTab")+"")){
            sb.append(" and brb.RedistributionGenre in ('1','3','9','10','11','12','13','14','29','31')");
        }
        if (map.get("activeTab")!=null && "3".equals(map.get("activeTab")+"")){
            sb.append(" and brb.RedistributionGenre in ('30','24','23','5')");
        }
        if (map.get("activeTab")==null || "0".equals(map.get("activeTab")+"")){
            sb.append(" and brb.RedistributionGenre in ('1','3','9','10','11','12','13','14','29','31','30','24','23','5')");
        }
        sb.append(" order by bc.ConfirmTime desc");
        excelExportLog.setDoSql(sb.toString());
        try{
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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "任务创建发生异常！";
        }
        return "您当前导出数据多于8000条，请关注右上角下载任务状态";
    }

    @Override
    public void oppRedistributionRecordExport(HttpServletRequest request, HttpServletResponse response, Map map) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        try{
            List<Map> fileds = (List<Map>) map.get("fileds");
            fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

            List<String> filedCodes = new ArrayList<>();
            List<String> filedNames = new ArrayList<>();

            for (Map filed : fileds) {
                filedCodes.add(filed.get("filedCode")+"");
                filedNames.add(filed.get("filedName")+"");
            }
            List<String> proIdList = new ArrayList<>();
            if (map.get("projectList") != null && !"".equals(map.get("projectList") + "")){
                proIdList = (List<String>) map.get("projectList");
                map.put("projectList",proIdList);
            }
            String userId = request.getHeader("userId");
            //导出的文档下面的名字
            String excelName = "机会重分配明细";
            String[] headers = null;
            List<CustomerDistributionRecordsVO> list = publicpoolDao.queryRedistributionRecord(map);
            //保存导出日志
            ExcelExportLog excelExportLog = new ExcelExportLog();
            String id = UUID.randomUUID().toString();
            excelExportLog.setId(id);
            excelExportLog.setMainTypeDesc(excelName);
            excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
            excelExportLog.setCreator(userId);
            if (!CollectionUtils.isEmpty(proIdList)) {
                Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
                //获取项目集合数据（事业部，项目Id,项目名称）
                excelExportLog.setCreator(userId);
                excelExportLog.setAreaName(proMap.get("areaName")+"");
                excelExportLog.setProjectId(proMap.get("projectId")+"");
                excelExportLog.setProjectName(proMap.get("projectName")+"");
                excelExportLog.setExportStatus("2");
                excelExportLog.setDoSql(JSON.toJSONString(map));
                //保存任务表
                excelImportMapper.addExcelExportLog(excelExportLog);
            }
            if (list.size()>0){
                boolean isAll = true;
                String isAllStr = map.get("isAll") + "";
                if ("1".equals(isAllStr)) isAll = false;
                headers = filedNames.toArray(new String[0]);
                int rowNum = 1;
                for (int i = 0; i < list.size(); i++) {
                    CustomerDistributionRecordsVO customerDistributionRecords = list.get(i);
                    customerDistributionRecords.setRownum(rowNum + "");
                    Object[] oArray = customerDistributionRecords.toData2(isAll,filedCodes);
                    dataset.add(oArray);
                    rowNum++;
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("机会重分配明细", headers,dataset, "机会重分配明细", response,null);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String oppRedistributionRecordExportNew(Map map) {
//        String companycode = "";
//        if (map.get("companycode")!=null && !"".equals(map.get("companycode")+"")){
//            companycode = map.get("companycode")+"";
//        }
//        ExcelExportLog excelExportLog = new ExcelExportLog();
//        String id = UUID.randomUUID().toString();
//        excelExportLog.setId(id);
//        excelExportLog.setMainType("2");
//        excelExportLog.setMainTypeDesc("案场管理");
//        excelExportLog.setSubType("A8");
//        excelExportLog.setSubTypeDesc("案场重分配记录");
//        if ("1".equals(map.get("isAll").toString())){
//            excelExportLog.setExportType("2");
//        }else{
//            excelExportLog.setExportType("1");
//        }
//        excelExportLog.setIsAsyn("1");
//        excelExportLog.setExportStatus("1");
//        List<String> proIdList = new ArrayList<>();
//        String[] ids = map.get("projectList").toString().split(",");
//        for (String proid : ids) {
//            proIdList.add(proid);
//        }
//        map.put("projectList",proIdList);
//        //获取项目集合数据（事业部，项目Id,项目名称）
//        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
//        excelExportLog.setCreator(map.get("userId").toString());
//        excelExportLog.setAreaName(proMap.get("areaName")+"");
//        excelExportLog.setProjectId(proMap.get("projectId")+"");
//        excelExportLog.setProjectName(proMap.get("projectName")+"");
//        //sql拼接
//        StringBuffer sb = new StringBuffer();
//        sb.append("SELECT brb.id redistributionBatchId, ba.EmployeeName as employeeName, DATE_FORMAT(bc.ConfirmTime,'%Y-%m-%d %H:%i:%s') confirmTime,(case brb.RedistributionGenre when 1 then '渠道台账' when 2 then '案场台账' when 3 then 'app' when 4 then '公共池' else '' end) as entrance, dict.DictName reason, bpc.CustomerName cstName, concat(left(bpc.CustomerMobile,3),'****',right(bpc.CustomerMobile,4)) as cstPhone, bpc.CustomerMobile as cstPhoneAll, (case bpc.ClueStatus when 1 then '报备' when 2 then '到访' when 3 then '排小卡' when 4 then '排大卡' when 5 then '订房' when 6 then '认筹' when 7 then '认购' when 8 then '签约' when 9 then '放弃' else '' end) as cstStatus, bc.SalesName salesName, DATE_FORMAT(bc.SalesAttributionTime,'%Y-%m-%d %H:%i:%s') salesAttributionTime, bc.OldSalesName oldSalesName, DATE_FORMAT(bc.OldSalesAttributionTime,'%Y-%m-%d %H:%i:%s') oldSalesAttributionTime, bpc.ProjectName projectName,(case bc.distribution_mode when 1 then '固定分配' when 2 then '进入淘客池'  when 3 then '淘客池回收' else '' end ) as distributionMode FROM b_redistribution_batch brb JOIN B_CustomerDistributionRecords bc ON brb.id = bc.RedistributionBatchId inner join b_project_opportunity bpc on bpc.ProjectClueId = bc.ProjectClueId left join b_account ba on ba.id = brb.createUser left join (select DictCode,DictName from s_dictionary where pid = ( SELECT id FROM s_dictionary WHERE pid = -1 and DictCode = '1007') and IsDel = 0) dict on dict.DictCode = brb.RedistributionType WHERE (brb.RedistributionGenre=2 or brb.RedistributionGenre=4 or (brb.RedistributionGenre=3 and bc.Type !=9 and bc.Type !=10))");
//        String projectIds = "'"+StringUtils.join(proIdList.toArray(), "','")+"'";
//        sb.append(" and brb.projectId in ("+projectIds+")");
//        if (map.get("name")!=null && !"".equals(map.get("name")+"")){
//            sb.append(" and bpc.CustomerName LIKE concat('%','"+map.get("name")+"','%')");
//        }
//        if (map.get("mobile")!=null && !"".equals(map.get("mobile")+"")){
//            sb.append(" and bpc.CustomerMobile LIKE concat('%','"+map.get("mobile")+"','%')");
//        }
//        if (map.get("redistributionGenre")!=null && !"".equals(map.get("redistributionGenre")+"")){
//            sb.append(" and brb.RedistributionGenre = '"+map.get("redistributionGenre")+"'");
//        }
//        sb.append(" order by bc.ConfirmTime desc");
//        excelExportLog.setDoSql(sb.toString());
//        try{
//            //保存任务表
//            excelImportMapper.addExcelExportLog(excelExportLog);
//            if (StringUtils.isEmpty(companycode)){
//                //放入redis
//                redisUtil.lPush("downLoad",id);
//            }else{
//                //放入redis
//                redisUtil.lPush("downLoad",id+","+companycode);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            return "任务创建发生异常！";
//        }
//        return "您当前导出数据多于8000条，请关注右上角下载任务状态";
        List<String> proIdList = new ArrayList<>();
        if (map.get("projectList") != null && !"".equals(map.get("projectList") + "")){
            proIdList = (List<String>) map.get("projectList");
        }
        String userId = SecurityUtils.getUserId();
        //导出的文档下面的名字
        String excelName = "案场重分配明细";
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("RR1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType(String.valueOf(map.get("isAll")));//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        if (!CollectionUtils.isEmpty(proIdList)) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            //获取项目集合数据（事业部，项目Id,项目名称）
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(map));
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

    /*
     * 重分配接口
     * */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public Map redistribution(Map map) {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        List<String> keyList = new ArrayList<>();
        int num = 0;
        try {
            List<DistributionInfo> distributionList = this.getDistribution(map);
            if(distributionList.size() == 0){
                returnMap.put("errmsg", "重分配失败，分配置业顾问小于客户数量！");
                return returnMap;
            }
            for (DistributionInfo distributionInfo : distributionList) {
                //传过来的参数
                //自定参数  tuokeOrAc  --  2：案场公共池  3：渠道台账  4：案场台账
                String tuokeOrAc = map.get("tuokeOrAc") + "";
                String SaleID = distributionInfo.getDistributionUserId(); // 指派人员ID
                String accoutName = distributionInfo.getDistributionUserName(); // 指派人员姓名
                String ClueClueID = distributionInfo.getProjectClueId(); // 被分配的客户(对应线索ID，或者机会ID)
                String ProjectID = map.get("ProjectID") + ""; // 项目id
                String UserID = map.get("UserID") + ""; // 当前登录人ID  （操作人员ID）
                String userName = map.get("userName") + ""; // 当前登录人姓名  （操作人员姓名）
                String select_user_parent = distributionInfo.getSelectUserParent(); // 分配人的父级（岗位）ID
                String select_user_parent_name = distributionInfo.getSelectUserParentName(); // 分配人的父级（岗位）名称
                String channel = map.get("channel") + ""; // 成交渠道

                String SourceTypeDesc = "";
                String achievementId = SaleID;// 业绩归属人id
                String achievementName = accoutName;// 业绩归属人姓名
                if (channel.equals("3")) {
                    SourceTypeDesc = "案场成交";
                    achievementId = "";
                    achievementName = "";
                }

                // 生成批次ID的方式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss"); // 生成日期，到秒
                Random random = new Random();
                int ends = random.nextInt(99); // 生成两位随机数
                String hendnum = String.format("%02d", ends);//如果不足两位，前面补0
                String endnum = simpleDateFormat.format(new Date()) + hendnum;
                map.put("uuid", endnum); // 随机数，作为批次的ID

                // 将每个人分开放进数组中 =
                String[] result = ClueClueID.split(",");
                if (result.length == 0) {
                    returnMap.put("errmsg", "重分配失败，没有重分配关键信息！");
                    throw new RuntimeException("");
                }
                //被重分配人数
                String countNumber = result.length + "";
                // 1.字段tuokeOrAc是为区分调用重分配的入口
                // 2.规则查询（只有自渠和案场的规则）
                List<ProjectProtectRuleVO> listRule = new ArrayList<>();
                if ("" != tuokeOrAc && null != tuokeOrAc) {
                    // 批次类型  4 为公共池（（1.拓客台账 2.案场台账 3.app 4.公共池））
                    if ("1".equals(tuokeOrAc) || "2".equals(tuokeOrAc)) {
                        map.put("redistributionGenre", "4");
                        map.put("SourceTypeSourceType", 2);
                        listRule = publicpoolDao.selectRuleByType(map);//查询规则
                        // 拓客台账
                    } else if ("3".equals(tuokeOrAc)) {
                        map.put("redistributionGenre", "1");
                        map.put("ProjectOrgCategory", 1);
                        map.put("SourceTypeSourceType", 1);
                        listRule = publicpoolDao.selectRuleByType(map);
                        // 案场台账
                    } else {
                        map.put("SourceTypeSourceType", 2);
                        map.put("redistributionGenre", "2");
                        listRule = publicpoolDao.selectRuleByType(map);
                    }
                    //重分配批次详情类型（3为销售经理分配）
                    String redisType = "6";
                    map.put("Type", redisType);
                    if (listRule.size() > 1) {
                        returnMap.put("errmsg", "同一个项目查找到多条规则导致重分配失败！");
                        throw new RuntimeException("");
                    }
                    // 获取系统时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    map.put("accoutName", accoutName);
                    map.put("countNumber", countNumber);
                    map.put("clueIdList", Arrays.asList(result));   // 循环出来的单个的各人ID（线索或者机会）
                    List<Map> list = null;
                    if ("4".equals(tuokeOrAc) || "2".equals(tuokeOrAc)) {  // 案场台账
                        // 此参数作为删除公共池参数使用
                        map.put("cusetomerSource", "2");
                        list = publicpoolDao.QueryOnePeople(map);

                    } else if ("3".equals(tuokeOrAc) || "1".equals(tuokeOrAc)) {
                        map.put("cusetomerSource", "1");
                        list = publicpoolDao.QueryOnePeopleClue(map);
                    }

                    /**
                     *如果没找到个人信息，则报错回滚
                     * */
                    if (list.size() == 0 || list.size() < result.length) {
                        returnMap.put("errmsg", "重分配失败，重分配时个人信息查找失败！");
                        throw new RuntimeException("");
                    }
                    log.info("开始查找登录人信息");
                    Map mapLogin = publicpoolDao.selectUserJob(map);
                    if (mapLogin == null) {
                        returnMap.put("errmsg", "重分配查找重要信息失败！");
                        throw new RuntimeException("");
                    }
                    List<String> cluesList = new ArrayList<>();
                    //判断是否渠道台账重分配
                    if ("3".equals(tuokeOrAc)){
                        //把作废的线索删除
                        for (int i = 0; i < list.size(); i++) {
                            Map cst = list.get(i);
                            if ("4".equals(cst.get("clueValidity")+"")){
                                list.remove(i);
                                num++;
                                i--;
                            }else{
                                //把锁定的删除
                                String key = cst.get("projectId").toString() + cst.get("CustomerMobile").toString();
                                if (redisUtil.setIfNull(key,true,1000)){
                                    keyList.add(key);
                                    cluesList.add(cst.get("ProjectClueId")+"");
                                }else{
                                    num++;
                                    list.remove(i);
                                    i--;
                                }
                            }
                        }
                    }
                    if (list.size()==0){
                        returnMap.put("errmsg", "客户信息已发生改变请刷新重试！");
                        throw new RuntimeException("");
                    }else{
                        List<String> clueIdList = new ArrayList<>();
                        for (Map mapCst:list) {
                            clueIdList.add(mapCst.get("ProjectClueId")+"");
                        }
                        map.put("countNumber", list.size());
                        map.put("clueIdList", clueIdList);
                    }
                    //声明保存list
                    List<Map> mapList = new ArrayList<>();
                    List<ClueOpportunityExtend> extendList = new ArrayList<>();
                    List<String> clueIdList = new ArrayList<>();
                    // 循环处理各个需要重分配的人
                    for (int a = 0; a < list.size(); a++) {
                        clueIdList.add(list.get(a).get("ProjectClueId") + "");
                        Map maps = new HashMap();
                        //如果成交渠道（channel）为空，则不修改成就渠道
                        if (!StringUtils.isBlank(channel)) {
                            //原成交渠道
                            String SourceTypeOld = list.get(a).get("SourceType") + "";
                            //原成交渠道描述
                            String SourceTypeOldDesc = list.get(a).get("SourceTypeDesc") + "";
                            maps.put("SourceType", channel);
                            maps.put("SourceTypeDesc", SourceTypeDesc);
                            maps.put("SourceTypeOld", SourceTypeOld);
                            maps.put("SourceTypeOldDesc", SourceTypeOldDesc);
                        }
                        maps.put("Type", redisType);
                        maps.put("SaleID", SaleID);
                        maps.put("accoutName", accoutName);
                        maps.put("achievementId", achievementId);
                        maps.put("achievementName", achievementName);
                        maps.put("UserID", UserID);
                        maps.put("userName", userName);
                        maps.put("ProjectID", ProjectID);
                        maps.put("uuid", endnum); // 随机数，作为批次的ID
                        //客户姓名
                        String CustomerName = "";
                        if (list.get(a).get("CustomerName") + "" != ""
                                && list.get(a).get("CustomerName") != null) {
                            CustomerName = list.get(a).get("CustomerName").toString();
                        }
                        maps.put("CustomerName", CustomerName);
                        // 客户轨迹表需要的标题
                        String title = "";
                        if (null != CustomerName && "" != CustomerName) {
                            title = userName + "将" + CustomerName + "重分配给" + accoutName;
                        }
                        /*存放批次详情中需要的信息*/
                        if ("2".equals(tuokeOrAc) || "4".equals(tuokeOrAc)) {
                            // 机会id
                            if ("" != list.get(a).get("OpportunityClueId") + ""
                                    && null != list.get(a).get("OpportunityClueId")) {
                                maps.put("OpportunityClueId", list.get(a).get("OpportunityClueId").toString());
                            } else {
                                maps.put("OpportunityClueId", "");
                            }

                        }
                        // 线索id
                        if ("" != list.get(a).get("ProjectClueId") + ""
                                && null != list.get(a).get("ProjectClueId")) {
                            maps.put("projectClueId", list.get(a).get("ProjectClueId").toString());
                        } else {
                            maps.put("projectClueId", "");
                        }
                        //用户ID
                        if ("" != list.get(a).get("BasicCustomerId") + ""
                                && null != list.get(a).get("BasicCustomerId")) {
                            maps.put("BasicCustomerId", list.get(a).get("BasicCustomerId").toString());
                        } else {
                            maps.put("BasicCustomerId", "");
                        }
                        //用户手机
                        if ("" != list.get(a).get("CustomerMobile") + ""
                                && null != list.get(a).get("CustomerMobile")) {
                            maps.put("CustomerMobile", list.get(a).get("CustomerMobile").toString());
                        } else {
                            maps.put("CustomerMobile", "");
                        }
                        //用户性别
                        if ("" != list.get(a).get("CustomerGender") + ""
                                && list.get(a).get("CustomerGender") != null) {
                            maps.put("CustomerGender", list.get(a).get("CustomerGender").toString());
                        } else {
                            maps.put("CustomerGender", "");
                        }
                        //用户年龄
                        if (!"".equals(list.get(a).get("CustomerAge") + "")
                                && list.get(a).get("CustomerAge") != null) {
                            maps.put("CustomerAge", list.get(a).get("CustomerAge").toString());
                        } else {
                            maps.put("CustomerAge", "");
                        }
                        // 项目名称
                        if ("" != list.get(a).get("ProjectName") + ""
                                && list.get(a).get("ProjectName") != null) {
                            maps.put("ProjectName", list.get(a).get("ProjectName").toString());
                        } else {
                            maps.put("ProjectName", "");
                        }
                        // 分配日期
                        maps.put("ConfirmTime", df.format(new Date()));
                        // 拓客原归属时间
                        if ("" != list.get(a).get("TokerAttributionTime") + ""
                                && null != list.get(a).get("TokerAttributionTime")) {
                            maps.put("TokerAttributionTime", list.get(a).get("TokerAttributionTime").toString());
                        } else {
                            maps.put("TokerAttributionTime", null);
                        }
                        // 案场原归属时间
                        if ("" != list.get(a).get("SalesAttributionTime") + ""
                                && list.get(a).get("SalesAttributionTime") != null) {
                            maps.put("OriginalSalesAttributionTime", list.get(a).get("SalesAttributionTime").toString());
                        } else {
                            maps.put("OriginalSalesAttributionTime", null);

                        }
                        //原销售人员姓名
                        if (tuokeOrAc.equals("3")) {
                            if ("" != list.get(a).get("ReportUserName") + ""
                                    && null != list.get(a).get("ReportUserName")) {
                                maps.put("OldSalesAttributionName", list.get(a).get("ReportUserName").toString());
                            }
                            if ("" != list.get(a).get("ReportUserID") + ""
                                    && null != list.get(a).get("ReportUserID")) {
                                maps.put("OldSalesAttributionId", list.get(a).get("ReportUserID").toString());
                            }
                            //原销售人员团队名称
                            if ("" != list.get(a).get("ReportTeamName") + ""
                                    && null != list.get(a).get("ReportTeamName")) {
                                maps.put("OldSalesAttributionTeamName", list.get(a).get("ReportTeamName").toString());
                            } else {
                                maps.put("OldSalesAttributionTeamName", "");
                            }
                            //原销售人员团队id
                            if ("" != list.get(a).get("ReportTeamID") + ""
                                    && null != list.get(a).get("ReportTeamID")) {
                                maps.put("OldSalesAttributionTeamId", list.get(a).get("ReportTeamID").toString());
                            } else {
                                maps.put("OldSalesAttributionTeamId", "");
                            }
                            maps.put("childType", 1);
                        } else {
                            ClueOpportunityExtend extend = new ClueOpportunityExtend();
                            extend.setProjectClueId(list.get(a).get("ProjectClueId").toString());
                            // 原销售人员ID
                            if ("" != list.get(a).get("SalesAttributionName") + ""
                                    && null != list.get(a).get("SalesAttributionName")) {
                                maps.put("OldSalesAttributionName", list.get(a).get("SalesAttributionName").toString());
                                extend.setOldSalesName(list.get(a).get("SalesAttributionName").toString());
                            }
                            if ("" != list.get(a).get("SalesAttributionId") + ""
                                    && null != list.get(a).get("SalesAttributionId")) {
                                maps.put("OldSalesAttributionId", list.get(a).get("SalesAttributionId").toString());
                                extend.setOldSalesId(list.get(a).get("SalesAttributionId").toString());
                            }
                            //原销售人员团队名称
                            if ("" != list.get(a).get("SalesAttributionTeamName") + ""
                                    && null != list.get(a).get("SalesAttributionTeamName")) {
                                maps.put("OldSalesAttributionTeamName", list.get(a).get("SalesAttributionTeamName").toString());
                                extend.setOldTeamName(list.get(a).get("SalesAttributionTeamName").toString());
                            } else {
                                maps.put("OldSalesAttributionTeamName", "");
                            }
                            //原销售人员团队id
                            if ("" != list.get(a).get("SalesAttributionTeamId") + ""
                                    && null != list.get(a).get("SalesAttributionTeamId")) {
                                maps.put("OldSalesAttributionTeamId", list.get(a).get("SalesAttributionTeamId").toString());
                                extend.setOldTeamId(list.get(a).get("SalesAttributionTeamId").toString());
                            } else {
                                maps.put("OldSalesAttributionTeamId", "");
                            }
                            maps.put("childType", 0);
                            if (extend.getOldSalesId()!=null){
                                extendList.add(extend);
                            }
                        }
                        //原业绩归属人id
                        if ("" != list.get(a).get("PerformanceAttributorID") + ""
                                && null != list.get(a).get("PerformanceAttributorID")) {
                            maps.put("OldPerformanceAttributorID", list.get(a).get("PerformanceAttributorID").toString());
                        } else {
                            maps.put("OldPerformanceAttributorID", "");
                        }
                        //原业绩归属人姓名
                        if ("" != list.get(a).get("PerformanceAttributor") + ""
                                && null != list.get(a).get("PerformanceAttributor")) {
                            maps.put("OldPerformanceAttributor", list.get(a).get("PerformanceAttributor").toString());
                        } else {
                            maps.put("OldPerformanceAttributor", "");
                        }
                        // 销售人员归属时间
                        maps.put("SalesAttributionTime", df.format(new Date()));
                        log.info("开始添加客户轨迹表");
                        maps.put("title", title);
                        maps.put("trajectoryType", "2");
                        // 操作人
                        maps.put("createUserName", UserID);
                        // 操作人岗位
                        if ("" != mapLogin.get("JobName") + "" && null != mapLogin.get("JobName").toString()) {
                            maps.put("createUserOgrName", mapLogin.get("JobName").toString());
                        } else {
                            maps.put("createUserOgrName", "");
                        }
                        // 操作人岗位ID
                        if ("" != mapLogin.get("JobCode") + ""
                                && null != mapLogin.get("JobCode")) {
                            maps.put("createUserOgrId", mapLogin.get("JobCode").toString());
                        } else {
                            maps.put("createUserOgrId", "");
                        }
                        // 客户电话
                        if ("" != list.get(a).get("CustomerMobile") + ""
                                && null != list.get(a).get("CustomerMobile")) {
                            maps.put("CustomerMobile", list.get(a).get("CustomerMobile").toString());
                        }
                        // 归属人姓名
                        maps.put("guishurenName", accoutName);
                        // 客户ID
                        if ("" != list.get(a).get("BasicCustomerId") + ""
                                && null != list.get(a).get("BasicCustomerId")) {
                            maps.put("SaleCustomerId", list.get(a).get("BasicCustomerId").toString());
                        }
                        log.info("开始查询规则和指派人员的信息");
                        Date todayDate = new Date();
                        String custormTeamName = "";
                        //组ID
                        String AttributionTeamId = "";
                        if (select_user_parent != "" && select_user_parent != null) {
                            AttributionTeamId = select_user_parent;
                        }
                        //队名称
                        String AttributionTeamName = "";
                        if (select_user_parent_name != "" && select_user_parent_name != null) {
                            AttributionTeamName = select_user_parent_name;
                            custormTeamName = AttributionTeamName;
                        }
                        if (null != AttributionTeamId && "" != AttributionTeamId) {
                            maps.put("SalesAttributionTeamId", AttributionTeamId);
                        }
                        if (null != AttributionTeamName && "" != AttributionTeamName) {
                            maps.put("SalesAttributionTeamName", AttributionTeamName);
                        }
                        //如果没有查到规则，则默认所有需要修改的时间为null
                        if (listRule.size() == 0) {
                            //实际案场报备逾期时间
                            maps.put("ReportExpireDate", null);
                            //实际案场报备预警时间
                            maps.put("ReportExpireWarningDate", null);
                            maps.put("VisitExpireDays", null);
                            maps.put("VisitingWarning", null);
                            maps.put("TheNextVisitFollowupExpireDays", null);
                            maps.put("followupExpireDaysWarning", null);
                        } else {
                            //报备逾期时间
                            String ReportExpireDate = listRule.get(0).getReportExpireDays() + "";
                            //报备预警时间
                            String ReportExpireWarningDate = listRule.get(0).getReportDaysWarning() + "";
                            //时间处理
                            if (null != listRule.get(0).getReportExpireDays() && "" != ReportExpireDate
                                    && !ReportExpireDate.equals("0") && !ReportExpireDate.equals("null")) {
                                Integer days = Integer.valueOf(ReportExpireDate);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(todayDate);
                                cal.add(Calendar.HOUR_OF_DAY, days);
                                //实际案场报备逾期时间
                                maps.put("ReportExpireDate", df.format(cal.getTime()));

                                if (null != ReportExpireWarningDate && "" != ReportExpireWarningDate
                                        && !ReportExpireWarningDate.equals("0") && !ReportExpireWarningDate.equals("null")) {
                                    Integer days2 = Integer.valueOf(ReportExpireWarningDate);
                                    Calendar cal2 = Calendar.getInstance();
                                    cal2.setTime(cal.getTime());
                                    cal2.add(Calendar.HOUR_OF_DAY, -days2);
                                    //实际案场报备预警时间
                                    maps.put("ReportExpireWarningDate", df.format(cal2.getTime()));
                                } else {
                                    maps.put("ReportExpireWarningDate", null);
                                }
                            } else {
                                //实际案场报备逾期时间
                                maps.put("ReportExpireDate", null);
                                //实际案场报备预警时间
                                maps.put("ReportExpireWarningDate", null);
                            }
                            //渠道未认购逾期
                            String ChannelProtectionPeriod = listRule.get(0).getChannelProtectionPeriod() + "";
                            //渠道为认购预警
                            String ChannelProtectionPeriodWarning = listRule.get(0).getChannelProtectionPeriodWarning() + "";
                            //时间处理
                            if (null != listRule.get(0).getChannelProtectionPeriod() && "" != ChannelProtectionPeriod
                                    && !ChannelProtectionPeriod.equals("0") && !ChannelProtectionPeriod.equals("null")) {
                                Integer days = Integer.valueOf(ChannelProtectionPeriod);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(todayDate);
                                cal.add(Calendar.DATE, days);
                                //实际渠道到访逾期时间
                                maps.put("ChannelProtectionPeriod", df.format(cal.getTime()));

                                if (null != listRule.get(0).getChannelProtectionPeriodWarning() && "" != ChannelProtectionPeriodWarning
                                        && ChannelProtectionPeriodWarning != "0" && !ChannelProtectionPeriodWarning.equals("null")) {
                                    Integer daysw = Integer.valueOf(ChannelProtectionPeriodWarning);
                                    Calendar calw = Calendar.getInstance();
                                    calw.setTime(cal.getTime());
                                    calw.add(Calendar.DATE, -daysw);
                                    //实际案场到访预警时间
                                    maps.put("ChannelProtectionPeriodWarning", df.format(calw.getTime()));
                                } else {
                                    maps.put("ChannelProtectionPeriodWarning", null);
                                }
                            } else {
                                maps.put("ChannelProtectionPeriod", null);
                                maps.put("ChannelProtectionPeriodWarning", null);
                            }
                            //到访未认购逾期
                            String VisitExpireDays = listRule.get(0).getVisitExpireDays() + "";
                            //到访未认购预警
                            String visitingWarning = listRule.get(0).getVisitingWarning() + "";
                            //时间处理
                            if (null != listRule.get(0).getVisitExpireDays() && "" != VisitExpireDays
                                    && !VisitExpireDays.equals("0") && !VisitExpireDays.equals("null")) {
                                Integer days = Integer.valueOf(VisitExpireDays);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(todayDate);
                                cal.add(Calendar.DATE, days);
                                //实际案场到访逾期时间
                                maps.put("VisitExpireDays", df.format(cal.getTime()));
                                if (null != listRule.get(0).getVisitingWarning() && "" != visitingWarning
                                        && visitingWarning != "0" && !visitingWarning.equals("null")) {
                                    Integer daysw = Integer.valueOf(visitingWarning);
                                    Calendar calw = Calendar.getInstance();
                                    calw.setTime(cal.getTime());
                                    calw.add(Calendar.DATE, -daysw);
                                    //实际案场到访预警时间
                                    maps.put("VisitingWarning", df.format(calw.getTime()));
                                } else {
                                    maps.put("VisitingWarning", null);
                                }
                            } else {
                                maps.put("VisitExpireDays", null);
                                maps.put("VisitingWarning", null);
                            }
                            //跟进逾期  ----- 只有案场人员有跟进逾期时间
                            if (tuokeOrAc.equals("4") || tuokeOrAc.equals("2")) {
                                String TheNextVisitFollowupExpireDays = listRule.get(0).getTheNextVisitFollowupExpireDays() + "";
                                //跟进预警
                                String followupExpireDaysWarning = listRule.get(0).getFollowupExpireDaysWarning() + "";
                                //时间处理
                                if (null != listRule.get(0).getTheNextVisitFollowupExpireDays() && "" != TheNextVisitFollowupExpireDays
                                        && !TheNextVisitFollowupExpireDays.equals("0") && !TheNextVisitFollowupExpireDays.equals("null")) {
                                    Integer days = Integer.valueOf(TheNextVisitFollowupExpireDays);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(todayDate);
                                    cal.add(Calendar.DATE, days);
                                    //实际案场跟进逾期时间SalesFollowExpireDate
                                    maps.put("TheNextVisitFollowupExpireDays", df.format(cal.getTime()));
                                    if (followupExpireDaysWarning != "" && null != listRule.get(0).getFollowupExpireDaysWarning()
                                            && !followupExpireDaysWarning.equals("0") && !followupExpireDaysWarning.equals("null")) {
                                        Integer daysw = Integer.valueOf(followupExpireDaysWarning);
                                        Calendar calw = Calendar.getInstance();
                                        calw.setTime(cal.getTime());
                                        calw.add(Calendar.DATE, -daysw);
                                        //实际案场报备预警时间
                                        maps.put("followupExpireDaysWarning", df.format(calw.getTime()));
                                    } else {
                                        maps.put("followupExpireDaysWarning", null);
                                    }
                                } else {
                                    maps.put("TheNextVisitFollowupExpireDays", null);
                                    maps.put("followupExpireDaysWarning", null);
                                }
                            }
                            if ("3".equals(tuokeOrAc)) {
                                maps.put("childType", 1);
                                maps.put("bianCan", "1");
                            }
                            maps.put("suoShuTeam", custormTeamName); // 所属人员团队
                        }
                        DistributionInfo distributionInfo1 = new DistributionInfo();
                        //客户重分配后更新客户行程
                        if (maps.get("projectClueId") != null && !"".equals(maps.get("projectClueId")) && SaleID != null && !"".equals(SaleID)) {
                            Map mapp = new HashMap();
                            mapp.put("ClueId",maps.get("projectClueId"));
                            mapp.put("newId",SaleID);
                            List<String> listc = projectCluesDao.getTaxiId(mapp.get("ClueId").toString());
                            distributionInfo1  = projectCluesDao.getAccountUserById(mapp.get("newId").toString());
                            if (listc != null && listc.size() > 0 && distributionInfo1 != null && distributionInfo1.getSalesAttributionId() != null)  {
                                distributionInfo1.setList(listc);
                                distributionInfo1.setUserId(UserID);
                                projectCluesDao.updateSalesAttribution(distributionInfo1);
                            }
                        }
                        mapList.add(maps);
                    }
                    //更新机会扩展表数据
                    if (extendList.size()>0){
                        projectCluesDao.updateClueOpportunityExtend(extendList);
                    }
                    if(clueIdList != null && clueIdList.size() > 0){
                        if(tuokeOrAc.equals("4") || tuokeOrAc.equals("2")){
                            projectCluesDao.editClueExtendValidity("2",clueIdList);
                        }else{
                            projectCluesDao.editClueExtendValidity("1",clueIdList);
                        }
                        projectCluesDao.updateTaoPublic(clueIdList);
                    }
                    log.info("开始添加批次表");
                    Integer in = publicpoolDao.insertNewBatch(map);
                    if (in != 1) {
                        returnMap.put("errmsg", "重分配插入人员记录失败！");
                        throw new RuntimeException("");
                    }
                    // 修改批次详情表中的信息，将“是否是最新”  改为 否（不管重分配线索还是机会，都一定存在线索，故以线索为条件）
                    log.info("开始修改批次详情表");
//                    publicpoolDao.UpdatePiCiDetail(map);
                    log.info("开始添加批次详情表");
                    publicpoolDao.InsertPICIDetail(mapList);

                    log.info("开始修改线索表和机会表");
                    if ("3".equals(tuokeOrAc)) {
                        //增加业绩归属人记录表信息
                        publicpoolDao.insertDistributionRecords(mapList);
                        // 修改渠道相关
                        publicpoolDao.UpdateXianSuoChance(mapList);
                        //删除消息
                        List<String> messageTypeList = Stream.of("1101", "1001").collect(Collectors.toList());
                        publicpoolDao.updateMessageByClueId(Arrays.asList(result), messageTypeList);
                    } else if ("2".equals(tuokeOrAc) || "4".equals(tuokeOrAc)) {
                        publicpoolDao.UpdateChanceInformation(mapList);
                        //删除消息
                        List<String> messageTypeList = Stream.of("2002", "2102", "1002", "1102", "2003", "2103").collect(Collectors.toList());
                        publicpoolDao.updateMessageByClueId(Arrays.asList(result), messageTypeList);
                    }

                    log.info("开始添加客户历史人员表");
                    Integer inCH = publicpoolDao.insertCustomerHistory(mapList);
                    if (inCH <= 0) {
                        returnMap.put("errmsg", "添加客户历史人员失败！");
                        throw new RuntimeException("");
                    }
                    log.info("开始删除公共池中的数据");
                    //publicpoolDao.deletePool(Arrays.asList(result));
                    projectCluesDao.editCustomerPublicPoolByOppId(Arrays.asList(result));
                    if(list.size() > 0){
                        log.info("开始往消息表中存放消息");
                        String customerName = list.get(0).get("CustomerName") + "";
                        String content = "【分配通知】销售经理已将客户[" + customerName + "]等"+countNumber+"个客户分配给您，由您做后续跟进，请知悉";
                        MessageForm messageForm = new MessageForm();
                        messageForm.setId(UUID.randomUUID().toString());
                        messageForm.setSubject("分配通知");
                        messageForm.setContent(content);
                        messageForm.setProjectId(ProjectID);
                        messageForm.setProjectClueId(null);
                        messageForm.setOpportunityClueId(null);
                        messageForm.setReceiver(SaleID);
                        messageForm.setSender(UserID);
                        messageForm.setMessageType(2106);
                        messageForm.setExt2("1");
                        List<MessageClueRelation> messageClueRelationList = new ArrayList<>();
                        for (String str : result){
                            MessageClueRelation messageClueRelation = new MessageClueRelation();
                            messageClueRelation.setMessageId(messageForm.getId());
                            messageClueRelation.setProjectClueId(str);
                            messageClueRelationList.add(messageClueRelation);
                        }
                        //保存消息
                        messageMapper.saveMessageInfo(messageForm);
                        //保存关联关系
                        messageMapper.saveMessageClueRelation(messageClueRelationList);
                    }else{
                        log.info("开始往消息表中存放消息");
                        String content = "【分配通知】销售经理已将客户[" + list.get(0).get("CustomerName") + "]分配给您，由您做后续跟进，请知悉。";
                        MessageForm messageForm = new MessageForm();
                        messageForm.setId(UUID.randomUUID().toString());
                        messageForm.setSubject("分配通知");
                        messageForm.setContent(content);
                        messageForm.setProjectId(ProjectID);
                        messageForm.setProjectClueId(list.get(0).get("ProjectClueId") + "");
                        messageForm.setOpportunityClueId(list.get(0).get("OpportunityClueId")+ "");
                        messageForm.setReceiver(SaleID);
                        messageForm.setSender(UserID);
                        messageForm.setMessageType(2106);
                        messageForm.setExt2("0");
                        //保存消息
                        messageMapper.saveMessageInfo(messageForm);
                    }

                    if (tuokeOrAc.equals("2") || tuokeOrAc.equals("4")) {
                        Map jsonMap = new HashMap();
                        jsonMap.put("projectClueIds", ClueClueID);
                        jsonMap.put("userId", SaleID);
                        jsonMap.put("userName", accoutName);
                        HttpRequestUtil.httpPost(RedisbuURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
                    }else{
                        Map jsonMap = new HashMap();
                        jsonMap.put("projectClueIds", ClueClueID);
                        jsonMap.put("userId", SaleID);
                        jsonMap.put("userName", accoutName);
                        HttpRequestUtil.httpPost(performanceURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
                    }
                }
            }
            returnMap.put("code", "200");
            returnMap.put("errmsg", "重分配成功！");
            returnMap.put("data", null);
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            returnMap.put("code", "-1");
        }finally {
            if (keyList.size()>0){
                for (String key:keyList
                ) {
                    redisUtil.del(key);
                }
            }
        }
        return returnMap;
    }

    @Override
    public List<Map> selectMan(Map map) {
        List<Map> map2 = publicpoolDao.selectMan(map);
        if (map2 != null && map2.size() > 0) {
            String pid = map2.get(0).get("PID") + "";
            map2.get(0).put("PID2", pid);
            map2.get(0).put("PID", -1);
            for (int i = 0; i < map2.size(); i++) {
                String pid2 = map2.get(i).get("PID") + "";
                //如果当前的数据的等级=第一条数据的等级，或者它的父级name为0，则认为其是第一级
                if (map2.get(i).get("Levels").equals(map2.get(0).get("Levels")) || map2.get(i).get("Pname").toString().equals("0")) {
                    map2.get(i).put("PID2", pid2);
                    map2.get(i).put("PID", -1);
                }
            }
        }

        return map2;
    }

    @Override
    public Map selectDetailedInformation(Map map) {
        return publicpoolDao.selectDetailedInformation(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map updateDetaileReport(Map map) {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        String key = "";
        boolean flag = false;
        try {
            String sourceType = map.get("sourceType") + "";
            String reportTeamId = map.get("reportTeamId") + "";
            String UserID = map.get("UserID") + ""; // 当前登录人ID  （操作人员ID）
            String userName = map.get("userName") + ""; // 当前登录人姓名  （操作人员姓名）
            String clueStatus = map.get("ClueStatus") + "";
            List<String> clueIds = new ArrayList<>();
            clueIds.add(map.get("projectClueId")+"");
            //查询信息
            map.put("clueIdList", clueIds );
            map.put("ClueID", map.get("projectClueId"));
            List<Map> list = publicpoolDao.QueryOnePeopleClue(map);
            /**
             *如果没找到个人信息，则报错回滚
             * */
            if (list.size() == 0) {
                returnMap.put("errmsg", "分配失败，分配时个人信息查找失败！");
                throw new RuntimeException("");
            }
            //如果是全民经纪人暂时只更新媒体类别
            if ("4".equals(sourceType)) {
                //修改报备人详情信息
                map.put("updateuuid", UUID.randomUUID().toString());
                //修改报备人日志
                publicpoolDao.update_DetaileReport_log(map);
                if (!String.valueOf(list.get(0).get("MainMediaGUID")).equals(String.valueOf(map.get("MainMediaGUID"))) ||
                        !String.valueOf(list.get(0).get("SubMediaGUID")).equals(String.valueOf(map.get("SubMediaGUID")))) {
                    map.put("OldMainMediaGUID", list.get(0).get("MainMediaGUID"));
                    map.put("OldMainMediaName", list.get(0).get("MainMediaName"));
                    map.put("OldSubMediaGUID", list.get(0).get("SubMediaGUID"));
                    map.put("OldSubMediaName", list.get(0).get("SubMediaName"));
                } else {
                    map.put("OldMainMediaGUID", null);
                    map.put("OldMainMediaName", null);
                    map.put("OldSubMediaGUID", null);
                    map.put("OldSubMediaName", null);
                }
                map.put("OldReportUserName", list.get(0).get("ReportUserName"));
                //修改报备人详情
                publicpoolDao.insertModificationDetails(map);
                Map param = new HashMap();
                param.put("projectClueId", map.get("projectClueId"));
                param.put("MainMediaGUID", map.get("MainMediaGUID"));
                param.put("MainMediaName", map.get("MainMediaName"));
                param.put("SubMediaGUID", map.get("SubMediaGUID"));
                param.put("SubMediaName", map.get("SubMediaName"));
                //修改线索表和机会表数据
                publicpoolDao.updateDetaileReport(param);

                if (!"1".equals(String.valueOf(list.get(0).get("ClueStatus")))) {
                    Map jsonMap = new HashMap();
                    jsonMap.put("IntentionID", map.get("IntentionID"));
                    jsonMap.put("MainMediaGUID", map.get("MainMediaGUID"));
                    jsonMap.put("MainMediaName", map.get("MainMediaName"));
                    jsonMap.put("SubMediaGUID", map.get("SubMediaGUID"));
                    jsonMap.put("SubMediaName", map.get("SubMediaName"));
                    jsonMap.put("SourceTypeDesc", map.get("sourceTypeDesc"));
                    jsonMap.put("PerformanceAttributor", map.get("reportUserName"));
//                    HttpRequestUtil.httpPost(mediaURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
                }

                returnMap.put("code", "200");
                returnMap.put("errmsg", "修改成功！");
                returnMap.put("data", null);
                return returnMap;
            }
            if ("1".equals(String.valueOf(list.get(0).get("ClueStatus")))){
                String clueValidity = list.get(0).get("clueValidity") + "";
                //判断报备客户是否作废状态  作废状态不能重分配
                if ("4".equals(clueValidity)){
                    returnMap.put("code", "-1");
                    returnMap.put("errmsg", "客户为作废状态不可重分配报备人！");
                    returnMap.put("data", null);
                    return returnMap;
                }
            }

            // 项目名称
            if ("" != list.get(0).get("ProjectName") + ""
                    && list.get(0).get("ProjectName") != null) {
                map.put("ProjectName", list.get(0).get("ProjectName").toString());
            } else {
                map.put("ProjectName", "");
            }
            Date todayDate = new Date();
            // 获取系统时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 分配日期
            map.put("ConfirmTime", df.format(new Date()));
            //如果是中介公司 业绩归属人存中介公司名称
            if ("1".equals(String.valueOf(map.get("sourceType")))) {
                Map companyMap = publicpoolDao.getCompanyInfo(String.valueOf(map.get("reportUserId")));
                if (companyMap != null) {
                    map.put("performanceName", companyMap.get("companyName"));
                    map.put("performanceId", companyMap.get("companyId"));
                } else {
                    map.put("performanceId", null);
                    map.put("performanceName", null);
                }
            } else if ("2".equals(String.valueOf(map.get("sourceType")))) {
                map.put("performanceName", map.get("reportUserName"));
                map.put("performanceId", map.get("reportUserId"));
            } else if ("3".equals(String.valueOf(map.get("sourceType")))) {
                map.put("performanceName", null);
                map.put("performanceId", null);
            }

            if (!String.valueOf(list.get(0).get("ReportUserID")).equals(String.valueOf(map.get("reportUserId"))) ||
                    !String.valueOf(list.get(0).get("SourceType")).equals(String.valueOf(map.get("sourceType")))) {
                if ("1".equals(String.valueOf(list.get(0).get("ClueStatus")))){
                    key = list.get(0).get("projectId").toString() + list.get(0).get("CustomerMobile").toString();
                    if(redisUtil.setIfNull(key,true,1000)) {
                        flag = true;
                    }else{
                        returnMap.put("code", "-1");
                        returnMap.put("errmsg", "该客户正在被其他渠道分配请稍后重试");
                        return returnMap;
                    }
                }
                //查询规则
                List<ProjectProtectRuleVO> listRule = new ArrayList<>();
                if ("1".equals(sourceType)) {
                    map.put("ProjectOrgCategory", 2);
                    map.put("SourceTypeSourceType", 1);
                    map.put("ProjectOrgId", reportTeamId);
                    listRule = publicpoolDao.selectRuleByType(map);//查询规则
                } else if ("2".equals(sourceType)) {
                    map.put("ProjectOrgCategory", 1);
                    map.put("SourceTypeSourceType", 1);
                    listRule = publicpoolDao.selectRuleByType(map);
                } else if ("3".equals(sourceType)) {
                    map.put("SourceTypeSourceType", 2);
                    listRule = publicpoolDao.selectRuleByType(map);
                }
                if (listRule.size() > 1) {
                    returnMap.put("errmsg", "同一个项目查找到多条规则导致保存失败！");
                    throw new RuntimeException("");
                }
                if (listRule.size() == 0) {
                    //实际案场报备逾期时间
                    map.put("ReportExpireDate", null);
                    //实际案场报备预警时间
                    map.put("ReportExpireWarningDate", null);
                } else {
                    //判断状态走不同的分支
                    if ("1".equals(clueStatus)) {
                        //报备逾期时间
                        String ReportExpireDate = listRule.get(0).getReportExpireDays() + "";
                        //报备预警时间
                        String ReportExpireWarningDate = listRule.get(0).getReportDaysWarning() + "";
                        //时间处理
                        if (null != listRule.get(0).getReportExpireDays() && "" != ReportExpireDate
                                && !ReportExpireDate.equals("0") && !ReportExpireDate.equals("null")) {
                            Integer days = Integer.valueOf(ReportExpireDate);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(todayDate);
                            cal.add(Calendar.HOUR_OF_DAY, days);
                            //实际报备逾期时间
                            map.put("ReportExpireDate", df.format(cal.getTime()));

                            if (null != ReportExpireWarningDate && "" != ReportExpireWarningDate
                                    && !ReportExpireWarningDate.equals("0") && !ReportExpireWarningDate.equals("null")) {
                                Integer days2 = Integer.valueOf(ReportExpireWarningDate);
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(cal.getTime());
                                cal2.add(Calendar.HOUR_OF_DAY, -days2);
                                //实际报备预警时间
                                map.put("ReportExpireWarningDate", df.format(cal2.getTime()));
                            } else {
                                map.put("ReportExpireWarningDate", null);
                            }
                        } else {
                            //实际报备逾期时间
                            map.put("ReportExpireDate", null);
                            //实际报备预警时间
                            map.put("ReportExpireWarningDate", null);
                        }
                    } else {
                        //渠道逾期时间
                        String channelProtectionPeriodDays = listRule.get(0).getChannelProtectionPeriod() + "";
                        //渠道预警时间
                        String channelProtectionPeriodWarningDays = listRule.get(0).getChannelProtectionPeriodWarning() + "";
                        //时间处理
                        if (null != channelProtectionPeriodDays && "" != channelProtectionPeriodDays
                                && !channelProtectionPeriodDays.equals("0") && !channelProtectionPeriodDays.equals("null")) {
                            Integer days = Integer.valueOf(channelProtectionPeriodDays);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(todayDate);
                            cal.add(Calendar.DATE, days);
                            //实际渠道逾期时间
                            map.put("TokerVisitExpireDate", df.format(cal.getTime()));

                            if (null != channelProtectionPeriodWarningDays && "" != channelProtectionPeriodWarningDays
                                    && !channelProtectionPeriodWarningDays.equals("0") && !channelProtectionPeriodWarningDays.equals("null")) {
                                Integer days2 = Integer.valueOf(channelProtectionPeriodWarningDays);
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(cal.getTime());
                                cal2.add(Calendar.DATE, -days2);
                                //实际渠道预警时间
                                map.put("TokerVisitExpireWarningDate", df.format(cal2.getTime()));
                            } else {
                                map.put("TokerVisitExpireWarningDate", null);
                            }
                        } else {
                            //实际渠道逾期时间
                            map.put("TokerVisitExpireDate", null);
                            //实际渠道预警时间
                            map.put("TokerVisitExpireWarningDate", null);
                        }
                    }
                }
                // 拓客原归属时间
                if ("" != list.get(0).get("TokerAttributionTime") + ""
                        && null != list.get(0).get("TokerAttributionTime")) {
                    map.put("TokerAttributionTime", list.get(0).get("TokerAttributionTime").toString());
                } else {
                    map.put("TokerAttributionTime", null);
                }
                // 案场原归属时间
                if ("" != list.get(0).get("SalesAttributionTime") + ""
                        && list.get(0).get("SalesAttributionTime") != null) {
                    map.put("OriginalSalesAttributionTime", list.get(0).get("SalesAttributionTime").toString());
                } else {
                    map.put("OriginalSalesAttributionTime", null);

                }
                if ("" != list.get(0).get("ReportUserName") + ""
                        && null != list.get(0).get("ReportUserName")) {
                    map.put("OldSalesAttributionName", list.get(0).get("ReportUserName").toString());
                }
                if ("" != list.get(0).get("ReportUserID") + ""
                        && null != list.get(0).get("ReportUserID")) {
                    map.put("OldSalesAttributionId", list.get(0).get("ReportUserID").toString());
                }
                //原销售人员团队名称
                if ("" != list.get(0).get("ReportTeamName") + ""
                        && null != list.get(0).get("ReportTeamName")) {
                    map.put("OldSalesAttributionTeamName", list.get(0).get("ReportTeamName").toString());
                } else {
                    map.put("OldSalesAttributionTeamName", "");
                }
                //原销售人员团队id
                if ("" != list.get(0).get("ReportTeamID") + ""
                        && null != list.get(0).get("ReportTeamID")) {
                    map.put("OldSalesAttributionTeamId", list.get(0).get("ReportTeamID").toString());
                } else {
                    map.put("OldSalesAttributionTeamId", "");
                }
                //原业绩归属人id
                if ("" != list.get(0).get("PerformanceAttributorID") + ""
                        && null != list.get(0).get("PerformanceAttributorID")) {
                    map.put("OldPerformanceAttributorID", list.get(0).get("PerformanceAttributorID").toString());
                } else {
                    map.put("OldPerformanceAttributorID", "");
                }
                //原业绩归属人姓名
                if ("" != list.get(0).get("PerformanceAttributor") + ""
                        && null != list.get(0).get("PerformanceAttributor")) {
                    map.put("OldPerformanceAttributor", list.get(0).get("PerformanceAttributor").toString());
                } else {
                    map.put("OldPerformanceAttributor", "");
                }
                map.put("OldReportUserId", list.get(0).get("ReportUserID"));
                map.put("OldReportUserName", list.get(0).get("ReportUserName"));
                map.put("OldReportTeamId", list.get(0).get("ReportTeamID"));
                map.put("OldReportTeamName", list.get(0).get("ReportTeamName"));
                map.put("OldSourceType", list.get(0).get("SourceType"));
                map.put("OldSourceTypeDesc", list.get(0).get("SourceTypeDesc"));
                map.put("OldOldSourceType", list.get(0).get("SourceTypeOld"));
                map.put("OldOldSourceTypeDesc", list.get(0).get("SourceTypeOldDesc"));
                map.put("OldPerformanceAttributorOld", list.get(0).get("PerformanceAttributorOld"));
                map.put("OldPerformanceAttributorOldID", list.get(0).get("PerformanceAttributorOldID"));
                if ("1".equals(clueStatus)) {
                    map.put("update", "1");
                } else {
                    map.put("update", "2");
                }
                map.put("PerformanceAttributors", map.get("performanceName"));
                map.put("PerformanceAttributorIDs", map.get("performanceId"));

                // 生成批次ID的方式
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss"); // 生成日期，到秒
                Random random = new Random();
                int ends = random.nextInt(99); // 生成两位随机数
                String hendnum = String.format("%02d", ends);//如果不足两位，前面补0
                String endnum = simpleDateFormat.format(new Date()) + hendnum;
                map.put("uuid", endnum); // 随机数，作为批次的ID
                map.put("Reason", 1);
                map.put("countNumber", 1);
                map.put("DoDesc", map.get("reasonsRevision"));
                map.put("redistributionGenre", 1);
                Integer in = publicpoolDao.insertNewBatch(map);
                if (in != 1) {
                    returnMap.put("errmsg", "分配插入人员记录失败！");
                    throw new RuntimeException("");
                }
                //新增批次详情
                map.put("accoutName", map.get("reportUserName"));
                map.put("SaleID", map.get("reportUserId"));
                map.put("SalesAttributionTeamName", map.get("reportTeamName"));
                map.put("SalesAttributionTeamId", map.get("reportTeamId"));
                map.put("Type", 5);
                map.put("childType", 1);
                map.put("Reason", "分配");
                List<Map> mapList = new ArrayList<>();
                mapList.add(map);
                publicpoolDao.InsertPICIDetail(mapList);
                //新增渠道变更信息
                publicpoolDao.insertDistributionRecords(mapList);
            } else {
                map.put("OldReportUserId", null);
                map.put("OldReportUserName", null);
                map.put("OldReportTeamId", null);
                map.put("OldReportTeamName", null);
                map.put("reportUserId", null);
                map.put("reportUserName", null);
                map.put("reportTeamId", null);
                map.put("reportTeamName", null);
                map.put("OldSourceType", null);
                map.put("OldSourceTypeDesc", null);
                map.put("OldOldSourceType", null);
                map.put("OldOldSourceTypeDesc", null);
                map.put("sourceType", null);
                map.put("sourceTypeDesc", null);
                //  map.put("sourceType",null);
                //map.put("sourceTypeDesc",null);
                map.put("OldPerformanceAttributor", null);
                map.put("OldPerformanceAttributorID", null);
                map.put("OldPerformanceAttributorOld", null);
                map.put("OldPerformanceAttributorOldID", null);
            }

            if (!String.valueOf(list.get(0).get("MainMediaGUID")).equals(String.valueOf(map.get("MainMediaGUID"))) ||
                    !String.valueOf(list.get(0).get("SubMediaGUID")).equals(String.valueOf(map.get("SubMediaGUID")))) {
                map.put("OldMainMediaGUID", list.get(0).get("MainMediaGUID"));
                map.put("OldMainMediaName", list.get(0).get("MainMediaName"));
                map.put("OldSubMediaGUID", list.get(0).get("SubMediaGUID"));
                map.put("OldSubMediaName", list.get(0).get("SubMediaName"));
            } else {
                map.put("OldMainMediaGUID", null);
                map.put("OldMainMediaName", null);
                map.put("OldSubMediaGUID", null);
                map.put("OldSubMediaName", null);
            }
            //修改报备人详情信息
            map.put("updateuuid", UUID.randomUUID().toString());
            //修改线索表和机会表数据
            publicpoolDao.updateDetaileReport(map);
            //修改报备人日志
            publicpoolDao.update_DetaileReport_log(map);
            //修改报备人详情
            publicpoolDao.insertModificationDetails(map);
            //删除大客户经理业绩
            if(!"4".equals(sourceType)){
                publicpoolDao.delAccountPerformance(map.get("projectClueId")+"");
            }
            if ("1".equals(String.valueOf(map.get("update")))) {
                //删除消息
                List<String> messageTypeList = Stream.of("1101", "1001").collect(Collectors.toList());
                List<String> clueIdList = Stream.of(String.valueOf(map.get("projectClueId"))).collect(Collectors.toList());
                publicpoolDao.updateMessageByClueId(clueIdList, messageTypeList);
            } else {
                //删除消息
                List<String> messageTypeList = Stream.of("2002", "2102").collect(Collectors.toList());
                List<String> clueIdList = Stream.of(String.valueOf(map.get("projectClueId"))).collect(Collectors.toList());
                publicpoolDao.updateMessageByClueId(clueIdList, messageTypeList);
            }
            if (!"1".equals(String.valueOf(list.get(0).get("ClueStatus")))) {
                Map jsonMap = new HashMap();
                jsonMap.put("IntentionID", map.get("IntentionID"));
                jsonMap.put("MainMediaGUID", map.get("MainMediaGUID"));
                jsonMap.put("MainMediaName", map.get("MainMediaName"));
                jsonMap.put("SubMediaGUID", map.get("SubMediaGUID"));
                jsonMap.put("SubMediaName", map.get("SubMediaName"));
                jsonMap.put("SourceTypeDesc", map.get("sourceTypeDesc"));
                if ("3".equals(String.valueOf(map.get("sourceType")))) {
                    jsonMap.put("PerformanceAttributor", null);
                }
                if ("2".equals(String.valueOf(map.get("sourceType")))) {
                    jsonMap.put("PerformanceAttributor", map.get("reportUserName"));
                }
                if ("1".equals(String.valueOf(map.get("sourceType")))) {
                    jsonMap.put("PerformanceAttributor", map.get("reportTeamName"));
                }
//                HttpRequestUtil.httpPost(mediaURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
            }
            returnMap.put("code", "200");
            returnMap.put("errmsg", "修改成功！");
            returnMap.put("data", null);
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            returnMap.put("code", "-1");
        }finally {
            if (flag){
                redisUtil.del(key);
            }
        }
        return returnMap;
    }

    @Override
    public List<Map> getModification(String projectClueId) {
        List<Map> mapList = publicpoolDao.getModification(projectClueId);
        if (mapList.size() > 0) {
            for (int i = 0; i < mapList.size(); i++) {
                String updateActionName = "";
                if (mapList.get(i).get("updateAction") != null) {
                    String updateAction = String.valueOf(mapList.get(i).get("updateAction"));
                    if (updateAction.indexOf("1") != -1) {
                        updateActionName += "报备人" + ",";
                    }
                    if (updateAction.indexOf("2") != -1) {
                        updateActionName += "成交类型" + ",";
                    }
                    if (updateAction.indexOf("3") != -1) {
                        updateActionName += "认知途径" + ",";
                    }
                    if (updateAction.indexOf("4") != -1) {
                        updateActionName += "置业顾问" + ",";
                    }
                    if (updateAction.indexOf("5") != -1) {
                        updateActionName += "其他" + ",";
                    }
                    if (updateActionName.length() > 0) {
                        updateActionName = updateActionName.substring(0, updateActionName.length() - 1);
                    }
                }
                mapList.get(i).put("updateActionName", updateActionName);
            }
        }
        return mapList;
    }

    @Override
    public List<Map> getModificationDetails(String updateLogId) {
        Map map = publicpoolDao.getModificationDetails(updateLogId);
        List<Map> mapList = new ArrayList<>();
        if (map != null) {
            if (map.get("ReportUserName") != null) {
                Map map1 = new HashMap();
                map1.put("Modifyfields", "报备人");
                map1.put("Bmodification", map.get("OldReportUserName"));
                map1.put("Amodification", map.get("ReportUserName"));
                mapList.add(map1);
            }
            if (map.get("ReportTeamName") != null) {
                Map map2 = new HashMap();
                map2.put("Modifyfields", "报备人团队");
                map2.put("Bmodification", map.get("OldReportTeamName"));
                map2.put("Amodification", map.get("ReportTeamName"));
                mapList.add(map2);
            }
            /*if(map.get("OldSourceTypeDesc") != null ) {
                Map map3 = new HashMap();
                map3.put("Modifyfields", "成交渠道");
                map3.put("Bmodification", map.get("OldSourceTypeDesc"));
                map3.put("Amodification", map.get("SourceTypeDesc"));
                mapList.add(map3);
            }*/
            if (map.get("SourceTypeDesc") != null) {
                Map map4 = new HashMap();
                map4.put("Modifyfields", "原成交渠道");
                map4.put("Bmodification", map.get("OldOldSourceTypeDesc"));
                map4.put("Amodification", map.get("NowOldSourceTypeDesc"));
                mapList.add(map4);
            }
            if (map.get("MainMediaName") != null) {
                Map map5 = new HashMap();
                map5.put("Modifyfields", "认知途径");
                map5.put("Bmodification", String.valueOf(map.get("OldMainMediaName")) + "/" + String.valueOf(map.get("OldMainMediaName")));
                map5.put("Amodification", String.valueOf(map.get("MainMediaName")) + "/" + String.valueOf(map.get("SubMediaName")));
                mapList.add(map5);
            }
            /*if(map.get("OldPerformanceAttributor") != null ) {
                Map map6 = new HashMap();
                map6.put("Modifyfields", "业绩归属人");
                map6.put("Bmodification", map.get("OldPerformanceAttributor"));
                map6.put("Amodification", map.get("PerformanceAttributor"));
                mapList.add(map6);
            }*/
            if (map.get("PerformanceAttributorOld") != null) {
                Map map7 = new HashMap();
                map7.put("Modifyfields", "原业绩归属人");
                map7.put("Bmodification", map.get("OldPerformanceAttributorOld"));
                map7.put("Amodification", map.get("PerformanceAttributorOld"));
                mapList.add(map7);
            }
        }
        return mapList;
    }


    private List<DistributionInfo> getDistribution(Map map) {
        //声明返回集合
        List<DistributionInfo> distributionInfos = new ArrayList<>();
        //String 转list
        String tuokeOrAc = map.get("tuokeOrAc") + "";
        String projectId = map.get("ProjectID") + "";
        String ClueClueID = map.get("ClueID") + "";
        String allocationUserStr = map.get("allocationUserStr") + "";
        List<String> projectClueIdList = Arrays.asList(ClueClueID.split(","));
        List<String> allocationUserList = Arrays.asList(allocationUserStr.split(","));
        if (allocationUserList.size() == 1) {
            Map salesInfo = publicpoolDao.getDistributionUserInfo(projectId, allocationUserList.get(0), tuokeOrAc);
            DistributionInfo distributionInfo = new DistributionInfo();
            distributionInfo.setDistributionUserId(String.valueOf(salesInfo.get("ID")));
            distributionInfo.setDistributionUserName(String.valueOf(salesInfo.get("EmployeeName")));
            distributionInfo.setSelectUserParent(String.valueOf(salesInfo.get("PID")));
            distributionInfo.setSelectUserParentName(String.valueOf(salesInfo.get("Pname")));
            distributionInfo.setProjectClueId(String.join(",", projectClueIdList));
            distributionInfos.add(distributionInfo);
        } else {
            if(projectClueIdList.size() < allocationUserList.size()){
                return distributionInfos;
            }
            //平均分配客户
            List<Map> mapList = AverageDataUtil.averageData(projectClueIdList, allocationUserList);
            for (Map maps : mapList) {
                //查询置业顾问信息
                Iterator<String> iter = maps.keySet().iterator();
                while (iter.hasNext()) {
                    //获取key
                    String key = iter.next();
                    List<String> value = (List) maps.get(key);
                    System.out.println("********* => " + key + " " + value);
                    Map salesInfo = publicpoolDao.getDistributionUserInfo(projectId, key, tuokeOrAc);
                    DistributionInfo distributionInfo = new DistributionInfo();
                    distributionInfo.setDistributionUserId(String.valueOf(salesInfo.get("ID")));
                    distributionInfo.setDistributionUserName(String.valueOf(salesInfo.get("EmployeeName")));
                    distributionInfo.setSelectUserParent(String.valueOf(salesInfo.get("PID")));
                    distributionInfo.setSelectUserParentName(String.valueOf(salesInfo.get("Pname")));
                    distributionInfo.setProjectClueId(String.join(",", value));
                    distributionInfos.add(distributionInfo);
                }
            }
        }
        return distributionInfos;
    }
}
