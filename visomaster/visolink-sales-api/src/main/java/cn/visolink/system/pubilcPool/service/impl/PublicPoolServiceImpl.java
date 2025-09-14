package cn.visolink.system.pubilcPool.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.message.model.form.MessageClueRelation;
import cn.visolink.message.model.form.MessageForm;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.dao.PublicpoolDao;
import cn.visolink.system.channel.model.ClueOpportunityExtend;
import cn.visolink.system.channel.model.DistributionInfo;
import cn.visolink.system.channel.model.vo.ProjectProtectRuleVO;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.pubilcPool.dao.PublicPoolDao;
import cn.visolink.system.pubilcPool.model.CasePublicPoolVO;
import cn.visolink.system.pubilcPool.model.CluesModel;
import cn.visolink.system.pubilcPool.model.PublicPoolHisVO;
import cn.visolink.system.pubilcPool.model.PublicPoolVO;
import cn.visolink.system.pubilcPool.model.form.CustomerDistributionRecords;
import cn.visolink.system.pubilcPool.model.form.PublicPoolListSearch;
import cn.visolink.system.pubilcPool.model.form.RecoveryEdit;
import cn.visolink.system.pubilcPool.model.form.RedistributionBatch;
import cn.visolink.system.pubilcPool.service.PublicPoolService;
import cn.visolink.utils.*;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName PublicPoolServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/26 16:05
 **/
@Service
public class PublicPoolServiceImpl implements PublicPoolService {

    @Autowired
    private PublicPoolDao publicPoolDao;
    @Autowired
    private PublicpoolDao publicpoolDao;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ProjectCluesDao projectCluesDao;
    @Autowired
    private RedisUtil redisUtil;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public PageInfo<PublicPoolVO> getPublicPoolList(PublicPoolListSearch paramMap) {
        PageHelper.startPage((int) paramMap.getCurrent(), (int) paramMap.getSize());
        List<PublicPoolVO> list = publicPoolDao.getPublicPoolList(paramMap);
        return new PageInfo<PublicPoolVO>(list);
    }

    @Override
    public PageInfo<PublicPoolHisVO> getPublicPoolHisList(PublicPoolListSearch paramMap) {
        PageHelper.startPage((int) paramMap.getCurrent(), (int) paramMap.getSize());
        List<PublicPoolHisVO> list = publicPoolDao.getPublicPoolHisList(paramMap);
        return new PageInfo<PublicPoolHisVO>(list);
    }

    @Override
    public void publicPoolExport(HttpServletRequest request, HttpServletResponse response, String param) {
        PublicPoolListSearch publicPoolListSearch = JSONObject.parseObject(param,PublicPoolListSearch.class);
        ArrayList<Object[]> dataset = new ArrayList<>();
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("1");
        excelExportLog.setMainTypeDesc("渠道管理");
        String SubType = "";
        String SubTypeDesc = "";
        String ExportType = "";
        if ("1".equals(publicPoolListSearch.getPoolType())){
            SubType = "Q8";
            SubTypeDesc = "渠道公共池";
        }else{
            SubType = "Q7";
            SubTypeDesc = "渠道淘客池";
        }
        //判断是否全号
        if ("1".equals(publicPoolListSearch.getIsAll())){
            ExportType = "2";
        }else{
            ExportType = "1";
        }
        excelExportLog.setSubType(SubType);
        excelExportLog.setSubTypeDesc(SubTypeDesc);
        excelExportLog.setExportType(ExportType);
        excelExportLog.setIsAsyn("0");

        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(publicPoolListSearch.getProjectIds());
        excelExportLog.setCreator(publicPoolListSearch.getUserId());
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        try{
            List<PublicPoolVO> list = publicPoolDao.getPublicPoolList(publicPoolListSearch);
            if (list!=null && list.size()>0){
                String[] headers = null;
                if ("1".equals(publicPoolListSearch.getPoolType())){
                    headers = list.get(0).getCourtPublicTitle();
                }else{
                    headers = list.get(0).getCourtTaoTitle();
                }
                for (int i = 0; i < list.size(); i++) {
                    PublicPoolVO publicPoolVO = list.get(i);
                    publicPoolVO.setRowNum((i+1)+"");
                    Object[] oArray = publicPoolVO.toPublicData(publicPoolListSearch.getIsAll());
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(SubTypeDesc, headers,dataset, SubTypeDesc, response,null);
            }
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(sf.format(new Date()));
            excelExportLog.setIsDown("1");
            Long export = new Date().getTime();
            Long exporttime = export-nowtime;
            String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
            excelExportLog.setWaitTime("0");
            excelExportLog.setExportTime(exportTime);
            //保存导出记录表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }catch (Exception e){
            e.printStackTrace();
            excelExportLog.setExportStatus("3");
            excelExportLog.setExceptionMessage(e.getMessage());
            //保存导出记录表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }
    }

    @Override
    public void publicPoolHisExport(HttpServletRequest request, HttpServletResponse response, String param) {
        PublicPoolListSearch publicPoolListSearch = JSONObject.parseObject(param,PublicPoolListSearch.class);

        ArrayList<Object[]> dataset = new ArrayList<>();
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("1");
        excelExportLog.setMainTypeDesc("渠道管理");
        String SubType = "";
        String SubTypeDesc = "";
        String ExportType = "";
        if ("1".equals(publicPoolListSearch.getRecordType())){
            SubType = "Q10";
            SubTypeDesc = "放弃记录";
        }else{
            SubType = "Q9";
            SubTypeDesc = "淘客记录";
        }
        //判断是否全号
        if ("1".equals(publicPoolListSearch.getIsAll())){
            ExportType = "2";
        }else{
            ExportType = "1";
        }
        excelExportLog.setSubType(SubType);
        excelExportLog.setSubTypeDesc(SubTypeDesc);
        excelExportLog.setExportType(ExportType);
        excelExportLog.setIsAsyn("0");

        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(publicPoolListSearch.getProjectIds());
        excelExportLog.setCreator(publicPoolListSearch.getUserId());
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        try{
            List<PublicPoolHisVO> list = publicPoolDao.getPublicPoolHisList(publicPoolListSearch);
            if (list!=null && list.size()>0){
                String[] headers = null;
                if ("1".equals(publicPoolListSearch.getRecordType())){
                    headers = list.get(0).getCourtGiveUpTitle();
                }else{
                    headers = list.get(0).getCourtTaoTitle();
                }
                for (int i = 0; i < list.size(); i++) {
                    PublicPoolHisVO publicPoolVO = list.get(i);
                    publicPoolVO.setRowNum((i+1)+"");
                    Object[] oArray = null;
                    if ("1".equals(publicPoolListSearch.getRecordType())){
                        oArray = publicPoolVO.toGiveUpData(publicPoolListSearch.getIsAll());
                    }else{
                        oArray = publicPoolVO.toTaoData(publicPoolListSearch.getIsAll());
                    }
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(SubTypeDesc, headers,dataset, SubTypeDesc, response,null);
            }
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(sf.format(new Date()));
            excelExportLog.setIsDown("1");
            Long export = new Date().getTime();
            Long exporttime = export-nowtime;
            String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
            excelExportLog.setWaitTime("0");
            excelExportLog.setExportTime(exportTime);
            //保存导出记录表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }catch (Exception e){
            e.printStackTrace();
            excelExportLog.setExportStatus("3");
            excelExportLog.setExceptionMessage(e.getMessage());
            //保存导出记录表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addTao(RecoveryEdit params) {
        //判断参数
        if (params==null || StringUtils.isEmpty(params.getProjectId())
                || params.getCstId()==null || params.getCstId().size()==0){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }
        String result = "";
        List<String> keyList = new ArrayList<>();
        try{
            String userId = SecurityUtils.getUserId();
            //获取操作人信息
            String userName = publicPoolDao.getUserName(userId);
            params.setEditor(userId);
            List<String> endCstList = new ArrayList<>();
            if("1".equals(params.getClueStatus())) {
                PublicPoolListSearch publicPoolListSearch = new PublicPoolListSearch();
                publicPoolListSearch.setCstIds(params.getCstId());
                List<String> proIds = new ArrayList<>();
                proIds.add(params.getProjectId());
                publicPoolListSearch.setProjectIds(proIds);
                publicPoolListSearch.setPoolType("1");
                publicPoolListSearch.setSourceType(params.getSourceType());
                //查询客户数据
                List<PublicPoolVO> list = publicPoolDao.getPublicPoolList(publicPoolListSearch);
                int num = 0;
                for (int i = 0; i < list.size(); i++) {
                    PublicPoolVO publicPoolVO = list.get(i);
                    String key = publicPoolVO.getProjectId() + publicPoolVO.getCustomerMobileAll();
                    if (redisUtil.setIfNull(key, true, 1000)) {
                        keyList.add(key);
                        endCstList.add(publicPoolVO.getCustomerBasicId());
                    } else {
                        num++;
                        list.remove(i);
                        i--;
                    }
                }
                if (endCstList.size() > 0) {
                    params.setCstId(endCstList);
                    params.setPoolType("1");
                    //更新公共池数据
                    publicPoolDao.updatePublicPool(params);
                    //删除淘客池数据
                    publicPoolDao.delPublicPool(params.getProjectId(),"2",endCstList);

                    String reason = "";
                    String redistributionGenre = "";
                    String poolType = "2";
                    String oldPoolType = "1";
                    String allocationType = "2";
                    String recordType = "2";
                    String redistributionType = "90008";
                    //渠道
                    if ("2".equals(params.getSourceType())) {
                        reason = "渠道分配淘客池";
                        redistributionGenre = "8";
                    } else {
                        reason = "案场分配淘客池";
                        redistributionGenre = "30";
                    }
                    //生成批次id 保存批次表数据
                    String batchId = SnowFlakeUtil.generateId() + "";
                    String addTaoTime = sf.format(new Date());
                    for (PublicPoolVO v : list) {
                        //用是否删除字段保存ID
                        v.setIsDel(SnowFlakeUtil.generateId() + "");
                        v.setBatchId(batchId);
                        v.setEditor(SecurityUtils.getUserId());
                        //用创建人字段保存操作人姓名
                        v.setCreator(userName);
                        v.setPoolType(poolType);
                        v.setOldPoolType(oldPoolType);
                        v.setAllocationType(allocationType);
                        v.setRecordType(recordType);
                        v.setAddTaoTime(addTaoTime);
                    }
                    Map batch = new HashMap();
                    batch.put("id", batchId);
                    batch.put("record_type", "2");
                    batch.put("reason", reason);
                    batch.put("project_id", params.getProjectId());
                    batch.put("count_number", list.size());
                    batch.put("creator", SecurityUtils.getUserId());
                    publicPoolDao.addPublicPoolBatch(batch);
                    //保存变更记录
                    publicPoolDao.addPublicPoolHis(list);
                    for (PublicPoolVO v : list) {
                        v.setId(SnowFlakeUtil.generateId() + "");
                    }
                    //插入淘客池
                    publicPoolDao.addPublicPoolList(list);
                    String id = this.getRedistributionBatchId();
                    //保存重分配记录
                    this.addRedistributionBatch(params.getProjectId(), id, list.size(), redistributionGenre, redistributionType);
                    this.addCustomerDistributionRecords(list, id, userName, redistributionType, allocationType, redistributionGenre);
                    if (num > 0) {
                        result = "分配成功" + list.size() + "个客户，剩余" + num + "个客户信息发生变动请刷新重试！";
                    } else {
                        result = "分配成功";
                    }
                } else {
                    result = "客户信息发生变动请刷新重试！";
                }
            }else{
                //查询客户数据
                List<CasePublicPoolVO> customerList = publicPoolDao.getCasePublicPoolList(params.getClueIds());
                int num = 0;
                for (int i = 0; i < customerList.size(); i++) {
                    CasePublicPoolVO casePublicPoolVO = customerList.get(i);
                    String key = casePublicPoolVO.getProjectId() + casePublicPoolVO.getCustomerMobile();
                    if (redisUtil.setIfNull(key, true, 1000)) {
                        keyList.add(key);
                        endCstList.add(casePublicPoolVO.getBasicCustomerId());
                    } else {
                        num++;
                        customerList.remove(i);
                        i--;
                    }
                }
                if (endCstList.size() > 0) {
//                    params.setCstId(endCstList);
//                    params.setPoolType("1");
//                    //更新公共池数据
//                    publicPoolDao.updatePublicPool(params);
                    //删除淘客池数据
                    publicPoolDao.delPublicPool(params.getProjectId(),"2",endCstList);
                    String reason = "案场分配淘客池";
                    String redistributionGenre = "6";
                    String poolType = "2";
                    String oldPoolType = "1";
                    String allocationType = "2";
                    String recordType = "2";
                    String redistributionType = "90008";
                    //生成批次id 保存批次表数据
                    String batchId = SnowFlakeUtil.generateId() + "";
                    String addTaoTime = sf.format(new Date());
                    List<PublicPoolVO> list = new ArrayList<>();
                    List<String> projectClueIdList = new ArrayList<>();
                    for (CasePublicPoolVO v : customerList) {
                        projectClueIdList.add(v.getProjectClueId());
                        PublicPoolVO publicPoolVO = new PublicPoolVO();
                        //用是否删除字段保存ID
                        publicPoolVO.setIsDel(SnowFlakeUtil.generateId() + "");
                        publicPoolVO.setBatchId(batchId);
                        publicPoolVO.setEditor(SecurityUtils.getUserId());
                        //用创建人字段保存操作人姓名
                        publicPoolVO.setCreator(userName);
                        publicPoolVO.setPoolType(poolType);
                        publicPoolVO.setOldPoolType(oldPoolType);
                        publicPoolVO.setAllocationType(allocationType);
                        publicPoolVO.setRecordType(recordType);
                        publicPoolVO.setAddTaoTime(addTaoTime);
                        publicPoolVO.setAddTime(addTaoTime);
                        publicPoolVO.setAddType(v.getAddType());
                        publicPoolVO.setAddTypeDesc(v.getExpireTag());
                        publicPoolVO.setEvaluateDesc(v.getExpireTag());
                        publicPoolVO.setAddReasonType(v.getAddReasonType());
                        publicPoolVO.setAddReasonDesc(v.getAddReasonDesc());
                        publicPoolVO.setActivateReasonType(v.getActivateReasonType());
                        publicPoolVO.setActivateReasonDesc(v.getActivateReasonDesc());
                        publicPoolVO.setProjectId(v.getProjectId());
                        publicPoolVO.setProjectName(v.getProjectName());
                        publicPoolVO.setCustomerBasicId(v.getBasicCustomerId());
                        publicPoolVO.setProjectClueId(v.getProjectClueId());
                        publicPoolVO.setCustomerName(v.getCustomerName());
                        publicPoolVO.setCustomerMobile(v.getCustomerMobile());
                        publicPoolVO.setCustomerMobileAll(v.getCustomerMobile());
                        publicPoolVO.setCustomerGender(v.getCustomerGender());
                        publicPoolVO.setCustomerLevel(v.getCustomerLevel());
                        publicPoolVO.setLevel(v.getLevel());
                        publicPoolVO.setChannelLabel(v.getLabel());
                        publicPoolVO.setClueStatus(v.getClueStatus());
                        publicPoolVO.setSourceType(v.getSourceType());
                        publicPoolVO.setSourceTypeDesc(v.getSourceTypeDesc());
                        publicPoolVO.setMainMediaId(v.getMainMediaGuId());
                        publicPoolVO.setMainMediaName(v.getMainMediaName());
                        publicPoolVO.setSubMediaId(v.getSubMediaGuId());
                        publicPoolVO.setSubMediaName(v.getSubMediaName());
                        publicPoolVO.setReportUserId(v.getReportUserId());
                        publicPoolVO.setReportUserName(v.getReportUserName());
                        publicPoolVO.setReportTime(v.getReportTime());
                        publicPoolVO.setSalesId(v.getSalesAttributionId());
                        publicPoolVO.setSalesName(v.getSalesAttributionName());
                        publicPoolVO.setTheFirstVisitDate(v.getTheFirstVisitDate());
                        publicPoolVO.setLatelyFollowUpPerson(v.getLatelyFollowUpPerson());
                        publicPoolVO.setLatelyFollowUpTime(v.getLatelyFollowUpTime());
                        publicPoolVO.setLatelyFollowUpMobile(v.getLatelyFollowUpMobile());
                        publicPoolVO.setLatelyFollowUpContent(v.getLatelyFollowUpContent());
                        publicPoolVO.setAddNumber(v.getAddNumber());
                        publicPoolVO.setIntentionBusiness(v.getIntentionBusiness());
                        publicPoolVO.setBrowseNumber(v.getBrowseNumber());
                        publicPoolVO.setBrowseTime(v.getBrowseTime());
                        publicPoolVO.setBrowseDesc(v.getBrowseDesc());
                        publicPoolVO.setDiscardTime(v.getOperationTime());
                        list.add(publicPoolVO);
                    }
                    //删除案场公共池
                    projectCluesDao.updateCustomerPublicPool(projectClueIdList);
                    Map batch = new HashMap();
                    batch.put("id", batchId);
                    batch.put("record_type", "2");
                    batch.put("reason", reason);
                    batch.put("project_id", params.getProjectId());
                    batch.put("count_number", list.size());
                    batch.put("creator", SecurityUtils.getUserId());
                    publicPoolDao.addPublicPoolBatch(batch);
                    //保存变更记录
                    publicPoolDao.addPublicPoolHis(list);
                    for (PublicPoolVO v : list) {
                        v.setId(SnowFlakeUtil.generateId() + "");
                    }
                    //插入淘客池
                    publicPoolDao.addPublicPoolList(list);
                    String id = this.getRedistributionBatchId();
                    //保存重分配记录
                    this.addRedistributionBatch(params.getProjectId(), id, list.size(), redistributionGenre, redistributionType);
                    this.addCustomerDistributionRecords(list, id, userName, redistributionType, allocationType, redistributionGenre);
                    if (num > 0) {
                        result = "分配成功" + list.size() + "个客户，剩余" + num + "个客户信息发生变动请刷新重试！";
                    } else {
                        result = "分配成功";
                    }
                } else {
                    result = "客户信息发生变动请刷新重试！";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0006,"发生异常！");
        }finally {
            if (keyList.size()>0){
                for (String key:keyList) {
                    redisUtil.del(key);
                }
            }
        }
        return ResultBody.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody taoRecovery(RecoveryEdit params) {
        //判断参数
        if (params==null || StringUtils.isEmpty(params.getProjectId())
        || params.getCstId()==null || params.getCstId().size()==0
        || StringUtils.isEmpty(params.getSourceType())){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }
        String result = "";
        List<String> keyList = new ArrayList<>();
        try{
            String userId = SecurityUtils.getUserId();
            //获取操作人信息
            String userName = publicPoolDao.getUserName(userId);
            params.setEditor(userId);
            PublicPoolListSearch publicPoolListSearch = new PublicPoolListSearch();
            publicPoolListSearch.setCstIds(params.getCstId());
            List<String> proIds = new ArrayList<>();
            proIds.add(params.getProjectId());
            publicPoolListSearch.setProjectIds(proIds);
            publicPoolListSearch.setPoolType("2");
            publicPoolListSearch.setSourceType(params.getSourceType());
            List<String> endCstList = new ArrayList<>();
            //查询客户数据
            if("2".equals(params.getClueStatus())){
                publicPoolListSearch.setSourceType(null);
            }
            List<PublicPoolVO> list = publicPoolDao.getPublicPoolList(publicPoolListSearch);
            int num = 0;
            for (int i = 0; i < list.size(); i++) {
                PublicPoolVO publicPoolVO = list.get(i);
                if("0".equals(publicPoolVO.getIsDel())) {
                    String key = publicPoolVO.getProjectId() + publicPoolVO.getCustomerMobileAll();
                    if (redisUtil.setIfNull(key, true, 1000)) {
                        keyList.add(key);
                        endCstList.add(publicPoolVO.getCustomerBasicId());
                    } else {
                        num++;
                        list.remove(i);
                        i--;
                    }
                }else{
                    num++;
                    list.remove(i);
                    i--;
                }
            }
            if (endCstList.size() > 0) {
                if("1".equals(params.getClueStatus())) {
                    params.setCstId(endCstList);
                    //更新公共池数据
                    publicPoolDao.updatePublicPoolVO(params);
                    //生成批次id 保存批次表数据
                    String batchId = SnowFlakeUtil.generateId() + "";
                    String reason = "";
                    String redistributionGenre = "";
                    String poolType = "1";
                    String oldPoolType = "2";
                    String allocationType = "3";
                    String recordType = "3";
                    String redistributionType = "90007";
                    //渠道
                    if ("2".equals(params.getSourceType())) {
                        reason = "渠道淘客池回收";
                        redistributionGenre = "29";
                    } else {
                        reason = "案场渠道淘客池回收";
                        redistributionGenre = "5";
                    }

                    for (PublicPoolVO v : list) {
                        //用是否删除字段保存ID
                        v.setIsDel(SnowFlakeUtil.generateId() + "");
                        v.setBatchId(batchId);
                        v.setEditor(SecurityUtils.getUserId());
                        //用创建人字段保存操作人姓名
                        v.setCreator(userName);
                        v.setPoolType(poolType);
                        v.setOldPoolType(oldPoolType);
                        v.setAllocationType(allocationType);
                        v.setAddReasonType("3");
                        v.setAddReasonDesc("淘客池回收");
                        v.setRecordType(recordType);
                    }

                    Map batch = new HashMap();
                    batch.put("id", batchId);
                    batch.put("record_type", "3");
                    batch.put("reason", reason);
                    batch.put("project_id", params.getProjectId());
                    batch.put("count_number", list.size());
                    batch.put("creator", SecurityUtils.getUserId());
                    publicPoolDao.addPublicPoolBatch(batch);
                    //保存变更记录
                    publicPoolDao.addPublicPoolHis(list);
                    String id = this.getRedistributionBatchId();
                    //保存重分配记录
                    this.addRedistributionBatch(params.getProjectId(), id, list.size(), redistributionGenre, redistributionType);
                    this.addCustomerDistributionRecords(list, id, userName, redistributionType, allocationType, redistributionGenre);
                    if (num > 0) {
                        result = "回收成功" + list.size() + "个客户，剩余" + num + "个客户信息发生变动请刷新重试！";
                    } else {
                        result = "回收成功";
                    }
                }else {
                    params.setCstId(endCstList);
//                    //更新公共池数据
                    publicPoolDao.updateAcPublicPoolVO(params);
                    //生成批次id 保存批次表数据
                    String batchId = SnowFlakeUtil.generateId() + "";
                    String reason = "案场到访淘客池回收";
                    String redistributionGenre = "35";
                    String poolType = "1";
                    String oldPoolType = "2";
                    String allocationType = "3";
                    String recordType = "3";
                    String redistributionType = "90007";
                    List<CasePublicPoolVO> casePublicPoolVOList = new ArrayList<>();
                    for (PublicPoolVO v : list) {
                        //用是否删除字段保存ID
                        v.setIsDel(SnowFlakeUtil.generateId() + "");
                        v.setBatchId(batchId);
                        v.setEditor(SecurityUtils.getUserId());
                        //用创建人字段保存操作人姓名
                        v.setCreator(userName);
                        v.setPoolType(poolType);
                        v.setOldPoolType(oldPoolType);
                        v.setAllocationType(allocationType);
                        v.setAddReasonType("3");
                        v.setAddReasonDesc("淘客池回收");
                        v.setRecordType(recordType);

                        CasePublicPoolVO casePublicPoolVO = new CasePublicPoolVO();
                        casePublicPoolVO.setAddType(v.getAddType());
                        casePublicPoolVO.setTheFirstVisitDate(v.getTheFirstVisitDate());
                        casePublicPoolVO.setReportUserName(v.getReportUserName());
                        casePublicPoolVO.setReportTime(v.getReportTime());
                        casePublicPoolVO.setSaleId(v.getSalesId());
                        casePublicPoolVO.setSaleName(v.getSalesName());
                        casePublicPoolVO.setExpireTag(v.getAddTypeDesc());
                        casePublicPoolVO.setClueStatus(v.getClueStatus());
                        casePublicPoolVO.setProjectId(v.getProjectId());
                        casePublicPoolVO.setProjectName(v.getProjectName());
                        casePublicPoolVO.setProjectClueId(v.getProjectClueId());
                        casePublicPoolVO.setSalesAttributionName(v.getSalesName());
                        casePublicPoolVO.setSalesAttributionId(v.getSalesId());
                        casePublicPoolVO.setCustomerName(v.getCustomerName());
                        casePublicPoolVO.setCustomerMobile(v.getCustomerMobile());
                        casePublicPoolVO.setCustomerGender(v.getCustomerGender());
                        casePublicPoolVO.setSourceType(v.getSourceType());
                        casePublicPoolVO.setSourceTypeDesc(v.getSourceTypeDesc());
                        casePublicPoolVO.setMainMediaGuId(v.getMainMediaId());
                        casePublicPoolVO.setMainMediaName(v.getMainMediaName());
                        casePublicPoolVO.setSubMediaGuId(v.getSubMediaId());
                        casePublicPoolVO.setSubMediaName(v.getSubMediaName());
                        casePublicPoolVO.setCustomerLevel(v.getCustomerLevel());
                        casePublicPoolVO.setLevel(v.getLevel());
                        casePublicPoolVO.setTradeLevel(v.getLevel());
                        casePublicPoolVO.setLabel(v.getChannelLabel());
                        casePublicPoolVO.setLatelyFollowUpPerson(v.getLatelyFollowUpPerson());
                        casePublicPoolVO.setLatelyFollowUpMobile(v.getLatelyFollowUpMobile());
                        casePublicPoolVO.setLatelyFollowUpTime(v.getLatelyFollowUpTime());
                        casePublicPoolVO.setLatelyFollowUpContent(v.getLatelyFollowUpContent());
                        casePublicPoolVO.setBrowseNumber(v.getBrowseNumber());
                        casePublicPoolVO.setBrowseTime(v.getBrowseTime());
                        casePublicPoolVO.setBrowseDesc(v.getBrowseDesc());
                        casePublicPoolVO.setAddReasonType(v.getAddReasonType());
                        casePublicPoolVO.setAddReasonDesc(v.getAddReasonDesc());
                        casePublicPoolVO.setAddNumber(v.getAddNumber());
                        casePublicPoolVO.setActivateReasonType("8");
                        casePublicPoolVO.setActivateReasonDesc("淘客池回收");
                        casePublicPoolVO.setBasicCustomerId(v.getCustomerBasicId());
                        casePublicPoolVO.setReportUserId(v.getReportUserId());
                        casePublicPoolVO.setIntentionBusiness(v.getIntentionBusiness());
                        casePublicPoolVO.setOpportunityClueId(v.getOpportunityClueId());
                        casePublicPoolVO.setDataCompleteAttachRate(v.getDataCompleteAttachRate());
                        casePublicPoolVO.setDataCompleteRate(v.getDataCompleteRate());
                        casePublicPoolVO.setSalesTheLatestFollowDate(v.getSalesTheLatestFollowDate());
                        casePublicPoolVOList.add(casePublicPoolVO);
                    }
                    //新增公共池数据
                    if(casePublicPoolVOList != null && casePublicPoolVOList.size() > 0) {
                        publicPoolDao.editPublic(casePublicPoolVOList);
                    }

                    Map batch = new HashMap();
                    batch.put("id", batchId);
                    batch.put("record_type", "3");
                    batch.put("reason", reason);
                    batch.put("project_id", params.getProjectId());
                    batch.put("count_number", list.size());
                    batch.put("creator", SecurityUtils.getUserId());
                    publicPoolDao.addPublicPoolBatch(batch);
                    //保存变更记录
                    publicPoolDao.addPublicPoolHis(list);
                    String id = this.getRedistributionBatchId();
                    //保存重分配记录
                    this.addRedistributionBatch(params.getProjectId(), id, list.size(), redistributionGenre, redistributionType);
                    this.addCustomerDistributionRecords(list, id, userName, redistributionType, allocationType, redistributionGenre);
                    if (num > 0) {
                        result = "回收成功" + list.size() + "个客户，剩余" + num + "个客户信息发生变动请刷新重试！";
                    } else {
                        result = "回收成功";
                    }
                }
            } else {
                result = "客户信息发生变动请刷新重试！";
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0006,"发生异常！");
        }finally {
            if (keyList.size()>0){
                for (String key:keyList) {
                    redisUtil.del(key);
                }
            }
        }
        return ResultBody.success(result);
    }
    /**
     * @Author wanggang
     * @Description //重分配报备人
     * @Date 13:42 2021/6/1
     * @Param [params]
     * @return cn.visolink.exception.ResultBody
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody channelPoolRedistribution(RecoveryEdit params) {
        //判断参数
        if (params==null || StringUtils.isEmpty(params.getProjectId())
                || params.getClueIds()==null || params.getClueIds().size()==0
                || params.getMobiles()==null || params.getMobiles().size()==0
                || params.getCstId()==null || params.getCstId().size()==0
                || StringUtils.isEmpty(params.getPoolType())
                || StringUtils.isEmpty(params.getAllocationUserStr())
                || StringUtils.isEmpty(params.getReason())
                || StringUtils.isEmpty(params.getDoDesc())){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }
        String result = "";
        //rediskey集合
        List<String> keyList = new ArrayList<>();
        try{
            int num = 0;
            List<String> mobileAll = params.getMobiles();
            //需要删除的失效手机号
            List<String> delMobiles = new ArrayList<>();
            if("1".equals(params.getClueStatus())) {
                //查询是否存在有效线索
                List<String> mobiles = publicPoolDao.getCluesMobile(params);
                //去除已经有效的
                if (mobiles != null && mobiles.size() > 0) {
                    for (int i = 0; i < mobileAll.size(); i++) {
                        if (mobiles.contains(mobileAll.get(i))) {
                            delMobiles.add(mobileAll.get(i));
                            mobileAll.remove(mobileAll.get(i));
                            i--;
                        }
                    }
                }
            }
            //校验redis
            for (int i = 0; i < mobileAll.size(); i++) {
                String key = params.getProjectId() + mobileAll.get(i);
                if(redisUtil.setIfNull(key,true,1000)) {
                    keyList.add(key);
                }else{
                    num++;
                    mobileAll.remove(mobileAll.get(i));
                    i--;
                }
            }
            //查询所有公池数据
            PublicPoolListSearch publicPoolListSearch = new PublicPoolListSearch();
            List<String> proIds = new ArrayList<>();
            proIds.add(params.getProjectId());
            publicPoolListSearch.setProjectIds(proIds);
            publicPoolListSearch.setSourceType(params.getSourceType());
            publicPoolListSearch.setPoolType(params.getPoolType());
            publicPoolListSearch.setClueStatus(params.getClueStatus());
            //查询失效的客户
            if (delMobiles.size()>0){
                publicPoolListSearch.setCstIds(delMobiles);
                List<PublicPoolVO> delPublic = publicPoolDao.getPublicPoolListByMobile(publicPoolListSearch);
                //查询所有线索
                List<CluesModel> delClues = new ArrayList<>();//需要删除的线索
                for (PublicPoolVO publicPoolVO:delPublic) {
                    CluesModel cluesModel = new CluesModel();
                    cluesModel.setCustomerMobile(publicPoolVO.getCustomerMobileAll());
                    cluesModel.setProjectClueId(publicPoolVO.getProjectClueId());
                    delClues.add(cluesModel);
                }
                //更新线索为作废
                if (delClues!=null && delClues.size()>0){
                    publicPoolDao.updateCluesStatusDel(delClues);
                }
            }
            if (mobileAll.size()>0){
                publicPoolListSearch.setCstIds(mobileAll);
                List<PublicPoolVO> publicPoolVOS = publicPoolDao.getPublicPoolListByMobile(publicPoolListSearch);

                List<String> cluesIds = new ArrayList<>();
                for (PublicPoolVO publicPoolVO:publicPoolVOS) {
                    cluesIds.add(publicPoolVO.getProjectClueId());
                }
                if (publicPoolVOS.size()>0){
                    params.setClueIds(cluesIds);
                    List<CluesModel> cluesModels = publicPoolDao.getAllClues(params);
                    //调用重分配接口
                    if (cluesModels!=null && cluesModels.size()>0){
                        Map param = new HashMap();
                        param.put("UserID",SecurityUtils.getUserId());
                        param.put("DoDesc",params.getDoDesc());
                        param.put("ProjectID",params.getProjectId());
                        param.put("Reason",params.getReason());
                        param.put("userName",params.getUserName());
                        param.put("allocationUserStr",params.getAllocationUserStr());
                        String Entrance = "";
                        String tuokeOrAc = "";
                        if("2".equals(params.getClueStatus())){
                            Entrance = "35";
                            tuokeOrAc = "4";
                        }else{
                            //判断是否自渠
                            if ("2".equals(params.getSourceType())){
                                //判断池类型 （1 公共池 2淘客池）
                                if ("1".equals(params.getPoolType())){
                                    Entrance = "8";
                                }else{
                                    Entrance = "29";
                                }
                                tuokeOrAc = "3";
                            }else{
                                //判断池类型 （1 公共池 2淘客池）
                                if ("1".equals(params.getPoolType())){
                                    Entrance = "30";
                                }else{
                                    Entrance = "5";
                                }
                                tuokeOrAc = "2";
                            }
                        }
                        List<String> cluesList = new ArrayList<>();
                        for (CluesModel cluesModel:cluesModels) {
                            cluesList.add(cluesModel.getProjectClueId());
                        }
                        String ClueID = StringUtils.join(cluesList,",");
                        param.put("ClueID",ClueID);
                        param.put("Entrance",Entrance);
                        param.put("tuokeOrAc",tuokeOrAc);
                        Map resultMap = this.redistribution(param);
                        //调用成功 添加公共池分配记录 更新公共池状态  更新线索状态
                        if ("200".equals(resultMap.get("code")+"")){

                            //生成批次id 保存批次表数据
                            String batchId = SnowFlakeUtil.generateId()+"";
                            List<String> endCstList = new ArrayList<>();
                            for (PublicPoolVO v:publicPoolVOS) {
                                //用是否删除字段保存ID
                                v.setIsDel(SnowFlakeUtil.generateId()+"");
                                v.setBatchId(batchId);
                                v.setEditor(SecurityUtils.getUserId());
                                //用创建人字段保存操作人姓名
                                v.setCreator(params.getUserName());
                                v.setPoolType(params.getPoolType());
                                v.setOldPoolType(params.getPoolType());
                                v.setRecordType("4");
                                endCstList.add(v.getCustomerBasicId());
                            }
                            params.setCstId(endCstList);
                            //更新公共池数据
                            publicPoolDao.updatePublicPool(params);
                            String reason = "";
                            if("2".equals(params.getClueStatus())) {
                                reason = "案场淘客池分配";
                            }else {
                                //渠道
                                if ("2".equals(params.getSourceType())) {
                                    //判断池类型 （1 公共池 2淘客池）
                                    if ("1".equals(params.getPoolType())) {
                                        reason = "渠道公共池分配";
                                    } else {
                                        reason = "渠道淘客池分配";
                                    }

                                } else {
                                    //判断池类型 （1 公共池 2淘客池）
                                    if ("1".equals(params.getPoolType())) {
                                        reason = "案场渠道公共池分配";
                                    } else {
                                        reason = "案场渠道淘客池分配";
                                    }
                                }
                            }
                            Map batch = new HashMap();
                            batch.put("id",batchId);
                            batch.put("record_type","4");
                            batch.put("reason",reason);
                            batch.put("project_id",params.getProjectId());
                            batch.put("count_number",publicPoolVOS.size());
                            batch.put("creator",SecurityUtils.getUserId());
                            publicPoolDao.addPublicPoolBatch(batch);
                            //保存变更记录
                            publicPoolDao.addPublicPoolHis(publicPoolVOS);
                            String id = UUID.randomUUID().toString();
                            if (num>0){
                                result = "重分配成功"+publicPoolVOS.size()+"个客户，剩余"+num+"个客户信息发生变动请刷新重试！";
                            }else{
                                result = "重分配成功";
                            }

                        }else{
                            result = "重分配错误！";
                        }

                    }else{
                        result = "客户信息发生变动请刷新重试！";
                    }
                }else{
                    result = "客户信息发生变动请刷新重试！";
                }

            }else{
                result = "客户信息发生变动请刷新重试！";
            }

        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0006,"发生异常！");
        }finally {
            if (keyList.size()>0){
                for (String key:keyList) {
                    redisUtil.del(key);
                }
            }
        }
        return ResultBody.success(result);
    }
    /**
     * @Author wanggang
     * @Description //重分配报备人
     * @Date 17:58 2021/6/1
     * @Param [map]
     * @return java.util.Map
     **/
    @Transactional(rollbackFor = Exception.class)
    public Map redistribution(Map map){
        Map<String, Object> returnMap = new LinkedHashMap<>();
        try {
            List<DistributionInfo> distributionList = this.getDistribution(map);
            if(distributionList.size() == 0){
                returnMap.put("code","-1");
                returnMap.put("errmsg", "重分配失败，分配顾问小于客户数量！");
                return returnMap;
            }
            String tuokeOrAc = map.get("tuokeOrAc") + "";
            for (DistributionInfo distributionInfo : distributionList) {
                //传过来的参数
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
                //重分配批次详情类型（6为销售经理分配）
                String redisType = "9";
                // 批次类型  4 为公共池（（1.拓客台账 2.案场台账 3.app 4.公共池））
                if ("1".equals(tuokeOrAc) || "2".equals(tuokeOrAc)) {
                    map.put("redistributionGenre", "4");
                    map.put("SourceTypeSourceType", 2);
                    listRule = publicpoolDao.selectRuleByType(map);//查询规则
                    redisType = "6";
                    // 拓客台账
                } else if ("3".equals(tuokeOrAc)) {
                    map.put("redistributionGenre", "1");
                    map.put("ProjectOrgCategory", 1);
                    map.put("SourceTypeSourceType", 1);
                    listRule = publicpoolDao.selectRuleByType(map);
                    redisType = "9";
                    // 案场台账
                } else {
                    map.put("SourceTypeSourceType", 2);
                    map.put("redistributionGenre", "2");
                    listRule = publicpoolDao.selectRuleByType(map);
                }


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
                map.put("cusetomerSource", "1");
                list = publicpoolDao.QueryOnePeopleClue(map);

                /**
                 *如果没找到个人信息，则报错回滚
                 * */
                if (list.size() == 0 || list.size() < result.length) {
                    returnMap.put("errmsg", "重分配失败，重分配时个人信息查找失败！");
                    throw new RuntimeException("");
                }
                Map mapLogin = publicpoolDao.selectUserJob(map);
                if (mapLogin == null) {
                    returnMap.put("errmsg", "重分配查找重要信息失败！");
                    throw new RuntimeException("");
                }
                //声明保存list
                List<Map> mapList = new ArrayList<>();
                List<ClueOpportunityExtend> extendList = new ArrayList<>();
                List<String> clueIdList = new ArrayList<>();
                // 循环处理各个需要重分配的人
                for (int a = 0; a < list.size(); a++) {
                    clueIdList.add(list.get(a).get("ProjectClueId") + "");
                    ClueOpportunityExtend clueOpportunityExtend = new ClueOpportunityExtend();
                    clueOpportunityExtend.setProjectClueId(list.get(a).get("ProjectClueId").toString());
                    extendList.add(clueOpportunityExtend);
                    Map maps = new HashMap();
                    maps.put("childType", "1");
                    maps.put("Entrance", map.get("Entrance"));
                    maps.put("DoDesc",map.get("DoDesc"));
                    maps.put("Reason",map.get("Reason"));
                    maps.put("SourceType", list.get(a).get("SourceType"));
                    //如果成交渠道（channel）为空，则不修改成就渠道
                    if (StringUtils.isNotBlank(MapUtils.getString(map,"channel",null))) {
                        //原成交渠道
                        String SourceTypeOld = list.get(a).get("SourceType") + "";
                        //原成交渠道描述
                        String SourceTypeOldDesc = list.get(a).get("SourceTypeDesc") + "";
                        maps.put("SourceType", null);
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
                        maps.put("bianCan", "1");
                        maps.put("suoShuTeam", custormTeamName); // 所属人员团队

                        //跟进逾期  ----- 只有案场人员有跟进逾期时间
                        if (tuokeOrAc.equals("4")) {
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
                if(clueIdList != null && clueIdList.size() > 0){
                    if(tuokeOrAc.equals("4") ){
                        projectCluesDao.editClueExtendValidity("2",clueIdList);
                    }else{
                        projectCluesDao.editClueExtendValidity("1",clueIdList);
                    }
                }

                Integer in = publicpoolDao.insertNewBatch(map);
                if (in != 1) {
                    returnMap.put("code", "-1");
                    returnMap.put("errmsg", "重分配插入人员记录失败！");
                    return returnMap;
                }
                // 修改批次详情表中的信息，将“是否是最新”  改为 否（不管重分配线索还是机会，都一定存在线索，故以线索为条件）
//                publicpoolDao.UpdatePiCiDetail(map);
                publicpoolDao.InsertPICIDetailTao(mapList);

                //增加业绩归属人记录表信息
                publicpoolDao.insertDistributionRecords(mapList);
                // 修改渠道相关
                if(tuokeOrAc.equals("4") ){
                    publicpoolDao.UpdateChanceInformation(mapList);
                    //删除消息
                    List<String> messageTypeList = Stream.of("2002", "2102", "1002", "1102", "2003", "2103").collect(Collectors.toList());
                    publicpoolDao.updateMessageByClueId(Arrays.asList(result), messageTypeList);
                }else {
                    publicpoolDao.updateXianSuo(mapList);
                    //删除消息
                    List<String> messageTypeList = Stream.of("1101", "1001").collect(Collectors.toList());
                    publicpoolDao.updateMessageByClueId(Arrays.asList(result), messageTypeList);
                }
//                Integer inCH = publicpoolDao.insertCustomerHistory(mapList);
//                if (inCH <= 0) {
//                    returnMap.put("code", "-1");
//                    returnMap.put("errmsg", "添加客户历史人员失败！");
//                    return returnMap;
//                }
//                publicpoolDao.deletePool(Arrays.asList(result));

                if(list.size() > 0){
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
            }
            returnMap.put("code", "200");
            returnMap.put("errmsg", "重分配成功！");
            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            returnMap.put("code", "-1");
            return returnMap;
        }
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


    /**
     * 新增分配批次表
     * @param
     * @param id
     * @return
     */
    public void addRedistributionBatch(String projectId, String id, int number,String redistributionGenre,String redistributionType){
        RedistributionBatch redistributionBatch = new RedistributionBatch();
        redistributionBatch.setId(id);
        redistributionBatch.setCountNumber(number);
        redistributionBatch.setCreateUser(SecurityUtils.getUserId());
        redistributionBatch.setProjectId(projectId);
        redistributionBatch.setIsDel(0);
        redistributionBatch.setRedistributionGenre(redistributionGenre);
        redistributionBatch.setRedistributionType(redistributionType);
        //保存批次表
        publicPoolDao.insertRedistributionBatch(redistributionBatch);
    }

    /**
     * 新增分配记录表
     * @param
     * @param id
     * @return
     */
    public void addCustomerDistributionRecords(List<PublicPoolVO> list, String id,String userName,String redistributionType,String allocationType,String redistributionGenre) {
        List<CustomerDistributionRecords> customerDistributionRecordsList = new ArrayList<>();
        CustomerDistributionRecords customerDistributionRecords = null;
        Integer type = 7;

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                PublicPoolVO publicPoolVO = list.get(i);
                customerDistributionRecords = new CustomerDistributionRecords();
                customerDistributionRecords.setProjectClueId(publicPoolVO.getProjectClueId());
                customerDistributionRecords.setProjectId(publicPoolVO.getProjectId());
                customerDistributionRecords.setProjectName(publicPoolVO.getProjectName());
                customerDistributionRecords.setConfirmID(SecurityUtils.getUserId());
                customerDistributionRecords.setConfirmPersonName(userName);
                customerDistributionRecords.setIsNew(0);
                customerDistributionRecords.setReason(redistributionType);
                customerDistributionRecords.setEntrance(Integer.valueOf(redistributionGenre));
                customerDistributionRecords.setRedistributionBatchId(id);
                customerDistributionRecords.setType(type);
                if("6".equals(redistributionGenre) || "35".equals(redistributionGenre)){
                    customerDistributionRecords.setChildType(0);
                }else {
                    customerDistributionRecords.setChildType(1);
                }
                customerDistributionRecords.setDistributionMode(allocationType);
                customerDistributionRecordsList.add(customerDistributionRecords);
            }
        }
        //保存重分配记录
        publicPoolDao.insertCustomerDistributionRecords(customerDistributionRecordsList);
    }

    /**
     * 生成分配批次id
     * @return
     */
    private String getRedistributionBatchId(){
        // 生成批次ID的方式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss"); // 生成日期，到秒
        Random random = new Random();
        int ends = random.nextInt(9999); // 生成两位随机数
//        String hendnum = String.format("%02d", ends);//如果不足两位，前面补0
        String endnum = simpleDateFormat.format(new Date()) + ends;
        return endnum; // 随机数，作为批次的ID
    }
}
