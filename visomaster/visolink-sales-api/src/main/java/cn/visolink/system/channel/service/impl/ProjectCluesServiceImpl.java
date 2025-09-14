package cn.visolink.system.channel.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.visolink.common.TaskStatusEnum;
import cn.visolink.common.TaskTypeEnum;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.domain.MenuResult;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.message.model.SysLog;
import cn.visolink.message.model.form.MessageForm;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.dao.TaskDao;
import cn.visolink.system.channel.model.Appeal;
import cn.visolink.system.channel.model.CardStandingBook;
import cn.visolink.system.channel.model.CustomerDistributionRecords;
import cn.visolink.system.channel.model.FeedBackEc;
import cn.visolink.system.channel.model.ProjectClues;
import cn.visolink.system.channel.model.ProjectRuleDetail;
import cn.visolink.system.channel.model.ReferralRecord;
import cn.visolink.system.channel.model.ReportFail;
import cn.visolink.system.channel.model.Task;
import cn.visolink.system.channel.model.TaskCustomer;
import cn.visolink.system.channel.model.TaskDetailVO;
import cn.visolink.system.channel.model.TaskMember;
import cn.visolink.system.channel.model.TaskQueryVO;
import cn.visolink.system.channel.model.UserAuthority;
import cn.visolink.system.channel.model.form.*;
import cn.visolink.system.channel.model.vo.*;
import cn.visolink.system.channel.service.ProjectCluesService;
import cn.visolink.system.company.service.CompanyService;
import cn.visolink.system.custMap.dao.CustMapDao;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.excel.model.FollowUpRecordForm;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.homepage.dao.WorkbenchMapper;
import cn.visolink.system.homepage.service.WorkbenchService;
import cn.visolink.system.org.model.form.OrganizationForm;
import cn.visolink.system.org.service.impl.OrganizationServiceImpl;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import cn.visolink.system.ruleEditLog.dao.RuleEditDao;
import cn.visolink.system.ruleEditLog.model.RuleEditLogBatch;
import cn.visolink.system.ruleEditLog.model.RuleEditLogDetail;
import cn.visolink.system.seniorbroker.vo.Message;
import cn.visolink.utils.*;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Collections;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.apache.wicket.core.dbhelper.sql.DBSQLServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import com.google.gson.Gson;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * <p>
 * ProjectClues服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-30
 */
@Service
public class ProjectCluesServiceImpl extends ServiceImpl<ProjectCluesDao, ProjectClues> implements ProjectCluesService {

    @Autowired
    DBSQLServiceImpl dbsqlService;
    @Autowired
    private CustMapDao custMapDao;

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ProjectCluesDao projectCluesDao;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private RuleEditDao ruleEditDao;
    @Autowired
    private WorkbenchMapper workbenchMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${AppMenuUrl}")
    private String appMenuUrl;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private OrganizationServiceImpl organizationService;
    @Value("${druidDataSource.Url}")
    private String druidDataSourceUrl;
    @Value("${druidDataSource.Username}")
    private String druidDataSourceUsername;
    @Value("${druidDataSource.Password}")
    private String druidDataSourcePassword;
    @Value("${CompanyDataUrl}")
    private String companyDataUrl;
    @Autowired
    private ProjectMapper projectMapper;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sfr = new SimpleDateFormat("yyyyMMddHHmmss");

    private SimpleDateFormat sfDa = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sfTi = new SimpleDateFormat("HH:mm:ss");

    private DecimalFormat df = new DecimalFormat("#0.00");

    @Value("${SystemUrl}")
    private String SystemUrl;

    @Value("${StartProcess}")
    private String StartProcess;
    @Autowired
    private TaskDao taskDao;

    @Autowired
    private WorkbenchService workbenchService;

    private final List<String> limitJobs = Arrays.asList("qyzszj","qyxsjl","qyyxjl","zszj","xsjl","yxjl");
    @Value("${YDACSENDOAMESSAGEURL}")
    private String sendOAMessageUrl;
    @Value("${YDACSENDOAMESSAGEAPPCODE}")
    private String sendOAMessageAppCode;
    @Value("${isSendOAMessage}")
    private int isSendOAMessage;

    @Value("${param.interface.redisKey}")
    private String redisKey;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    //查询渠道台账信息
    @Override
    public PageInfo<ProjectCluesNew> channelProjectClues(Map projectCluesForm) {
        int pageIndex = 1;
        int pageSize = 10;
        if (projectCluesForm.get("pageNum") != null){
            pageIndex = Integer.parseInt(projectCluesForm.get("pageNum") + "");
        }
        if (projectCluesForm.get("pageSize")!=null){
            pageSize = Integer.parseInt(projectCluesForm.get("pageSize") + "");
        }

        if (projectCluesForm.get("search") != null && !"".equals(projectCluesForm.get("search") + "")){
            String search = projectCluesForm.get("search") + "";
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                projectCluesForm.put("customerMobile",search);
            } else {
                projectCluesForm.put("customerName",search);
            }
        }
        List<String> projectList = new ArrayList<>();
        if (projectCluesForm.get("projectList") != null) {
            projectList = Arrays.asList(String.valueOf(projectCluesForm.get("projectList")).split(","));
            projectCluesForm.put("projectIdList", projectList);
        }
        //获取用户权限 如果存在管理员支持模糊查询
        projectCluesForm.put("type",projectCluesDao.getUserJobHsxt(SecurityUtils.getUserId()));
        //限制经理、总监、营销经理
        List<String> jobs = projectCluesDao.getJobsByUserId(SecurityUtils.getUserId());
        Boolean flag = checkJobs(jobs);
        if(!projectCluesForm.containsKey("ownerUserId") && projectCluesForm.get("ownerUserId") == null && flag){
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(), projectList);
            projectCluesForm.put("orgIds", orgIds);
        }
        PageHelper.startPage(pageIndex, pageSize);
        List<ProjectCluesNew> list = projectCluesDao.channelProjectClues(projectCluesForm);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time1 = format.format(new Date());
        if (!CollectionUtils.isEmpty(list)) {
            for (ProjectCluesNew projectCluesNew : list) {
                //剩余天数需要计算
                if (StringUtils.isEmpty(projectCluesNew.getSalesFollowExpireDate()) || "签约".equals(projectCluesNew.getClueStatus())) {
                    projectCluesNew.setSalesFollowExpireDate("无");
                    projectCluesNew.setRemainingDays("永久");
                } else {
                    String time2 = projectCluesNew.getSalesFollowExpireDate();
                    Integer remainingDays = null;
                    try {
                        remainingDays = this.dateDifference(time1, time2);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    projectCluesNew.setRemainingDays(String.valueOf(remainingDays));
                }
            }
        }
        return new PageInfo<>(list);
    }

    //查询渠道台账信息
    @Override
    public PageInfo<ProjectCluesNew> channelProjectCluesByUser(Map projectCluesForm) {
        int pageIndex = 1;
        int pageSize = 10;
        if (projectCluesForm.get("pageNum") != null){
            pageIndex = Integer.parseInt(projectCluesForm.get("pageNum") + "");
        }
        if (projectCluesForm.get("pageSize")!=null){
            pageSize = Integer.parseInt(projectCluesForm.get("pageSize") + "");
        }

        if (projectCluesForm.get("search") != null && !"".equals(projectCluesForm.get("search") + "")){
            String search = projectCluesForm.get("search") + "";
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                projectCluesForm.put("customerMobile",search);
            } else {
                projectCluesForm.put("customerName",search);
            }
        }
        List<String> projectList = new ArrayList<>();
        if (projectCluesForm.get("projectList") != null) {
            projectList = Arrays.asList(String.valueOf(projectCluesForm.get("projectList")).split(","));
            projectCluesForm.put("projectIdList", projectList);
        }
        projectCluesForm.put("ownerUserId",SecurityUtils.getUserId());
        PageHelper.startPage(pageIndex, pageSize);
        List<ProjectCluesNew> list = projectCluesDao.channelProjectClues(projectCluesForm);
        return new PageInfo<>(list);
    }

//    渠道信息导出
    @Override
    public void channelProjectCluesExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);

        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();

        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        List<ProjectCluesNew> projectCluesNewList = new ArrayList<>();
        List<String> proIdList = new ArrayList<>();
        if (paramMap.get("projectList") != null && !"".equals(paramMap.get("projectList") + "")){
            String regionIds = paramMap.get("projectList") + "";
            proIdList = Arrays.asList(regionIds.split(","));
            paramMap.put("projectIdList",proIdList);
        }
        if (paramMap.get("search") != null && !"".equals(paramMap.get("search") + "")){
            String search = paramMap.get("search") + "";
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                paramMap.put("customerMobile",search);
            } else {
                paramMap.put("customerName",search);
            }
        }
        String userId = request.getHeader("userId");
        //导出的文档下面的名字
        String excelName = "走访客户台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        projectCluesNewList = projectCluesDao.channelProjectClues(paramMap);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time1 = format.format(new Date());
        if (!CollectionUtils.isEmpty(projectCluesNewList)) {
            for (ProjectCluesNew projectCluesNew : projectCluesNewList) {
                //剩余天数需要计算
                if (StringUtils.isEmpty(projectCluesNew.getSalesFollowExpireDate()) || "签约".equals(projectCluesNew.getClueStatus())) {
                    projectCluesNew.setSalesFollowExpireDate("无");
                    projectCluesNew.setRemainingDays("永久");
                } else {
                    String time2 = projectCluesNew.getSalesFollowExpireDate();
                    Integer remainingDays = null;
                    try {
                        remainingDays = this.dateDifference(time1, time2);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    projectCluesNew.setRemainingDays(String.valueOf(remainingDays));
                }
            }
        }
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
            excelExportLog.setDoSql(excelForm);
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }

        if (projectCluesNewList != null && projectCluesNewList.size() > 0){
            String isAllStr = paramMap.get("isAll") + "";
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
         //   headers = projectCluesNewList.get(0).courtCaseTitle1;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (ProjectCluesNew model : projectCluesNewList) {
                model.setRownum(rowNum);
                Object[] oArray = model.toData1(isAll,filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String channelProjectCluesExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        List<String> proIdList = new ArrayList<>();
        if (paramMap.get("projectList") != null && !"".equals(paramMap.get("projectList") + "")){
            String regionIds = paramMap.get("projectList") + "";
            proIdList = Arrays.asList(regionIds.split(","));
            paramMap.put("projectList",proIdList);
        }
        String userId = SecurityUtils.getUserId();
        //导出的文档下面的名字
        String excelName = "走访客户台账";
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("CC1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType(String.valueOf(paramMap.get("isAll")));//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        if (!CollectionUtils.isEmpty(proIdList)) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            //获取项目集合数据（事业部，项目Id,项目名称）
            excelExportLog.setCreator(userId);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(excelForm);
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

    //    渠道信息导出
    @Override
    public void channelProjectCluesByUserExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);

        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();

        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        List<ProjectCluesNew> projectCluesNewList = new ArrayList<>();
        List<String> proIdList = new ArrayList<>();
        if (paramMap.get("projectList") != null && !"".equals(paramMap.get("projectList") + "")){
            String regionIds = paramMap.get("projectList") + "";
            proIdList = Arrays.asList(regionIds.split(","));
            paramMap.put("projectList",proIdList);
        }
        if (paramMap.get("search") != null && !"".equals(paramMap.get("search") + "")){
            String search = paramMap.get("search") + "";
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                paramMap.put("customerMobile",search);
            } else {
                paramMap.put("customerName",search);
            }
        }
        String userId = request.getHeader("userId");
        //导出的文档下面的名字
        String excelName = "走访客户台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        paramMap.put("ownerUserId",userId);
        projectCluesNewList = projectCluesDao.channelProjectClues(paramMap);
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
            excelExportLog.setDoSql(excelForm);
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }

        if (projectCluesNewList != null && projectCluesNewList.size() > 0){
            String isAllStr = paramMap.get("isAll") + "";
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
            //   headers = projectCluesNewList.get(0).courtCaseTitle1;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (ProjectCluesNew model : projectCluesNewList) {
                model.setRownum(rowNum);
                Object[] oArray = model.toData1(isAll,filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //    查询案场信息
    @Override
    public PageInfo<ProjectCluesNew> courtCase(ExcelForm projectCluesForm) throws ParseException {
        if (projectCluesForm.getSearch() != null && !"".equals(projectCluesForm.getSearch())){
            String search = projectCluesForm.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                projectCluesForm.setCustomerMobile(search);
            } else {
                projectCluesForm.setCustomerName(search);
            }
        }
        //获取用户权限 如果存在管理员支持模糊查询
        projectCluesForm.setType(projectCluesDao.getUserJobHsxt(SecurityUtils.getUserId()));
        //判断是否存在关联企业
        if(StringUtils.isNotEmpty(projectCluesForm.getCustomerName()) || StringUtils.isNotEmpty(projectCluesForm.getCustomerMobile())){
            projectCluesForm.setRelaCustomerMainIds(projectCluesDao.getRelaCustomerMainId(projectCluesForm));
        }
        //排除申请权限配置项目的未配置组
        List<String> jobs = projectCluesDao.getJobsByUserId(SecurityUtils.getUserId());
        Boolean flag = checkJobs(jobs);
        if(StringUtils.isEmpty(projectCluesForm.getOwnerUserId()) && flag){
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(), projectCluesForm.getProjectList());
            projectCluesForm.setOrgIds(orgIds);
        }
        int pageNum = 1;
        int pageSize = 10;
        if (projectCluesForm.getPageNum() != null && projectCluesForm.getPageNum() != "" ){
            pageNum = Integer.parseInt(projectCluesForm.getPageNum());
        }
        if (projectCluesForm.getPageSize() != null && projectCluesForm.getPageSize() != ""){
            pageSize = Integer.parseInt(projectCluesForm.getPageSize());
        }
        if(CollectionUtils.isEmpty(projectCluesForm.getProjectList())){
            List<String> proList = new ArrayList<>();
            proList.add("");
            projectCluesForm.setProjectList(proList);
        }
        int i = (pageNum - 1) * pageSize;
        projectCluesForm.setPageIndex(String.valueOf(i));
        projectCluesForm.setPageNum(null);
        List<ProjectCluesNew> list = projectCluesDao.courtCase(projectCluesForm);
        String total = projectCluesDao.courtCaseCount(projectCluesForm);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time1 = format.format(new Date());
        if (!CollectionUtils.isEmpty(list)) {
            //报备链路
            List<Map> rpList = new ArrayList<>();
            if(StringUtils.isEmpty(projectCluesForm.getOwnerUserId())){
                rpList = projectCluesDao.getReportList(list);
            }

            for (ProjectCluesNew projectCluesNew : list) {
                //剩余天数需要计算
                if(StringUtils.isEmpty(projectCluesNew.getSalesFollowExpireDate()) || "签约".equals(projectCluesNew.getClueStatus())){
                    projectCluesNew.setSalesFollowExpireDate("无");
                    projectCluesNew.setRemainingDays("永久");
                }else {
                    String time2 = projectCluesNew.getSalesFollowExpireDate();
                    Integer remainingDays = this.dateDifference(time1, time2);
                    projectCluesNew.setRemainingDays(String.valueOf(remainingDays));
                }
                //信息完善度
                projectCluesNew.setPerfectionProgress(this.getPerfectionProgress(projectCluesNew));
                //报备链路
                if(!CollectionUtils.isEmpty(rpList)){
                    List<Map> cList = rpList.stream().filter(x->x.get("opportunityClueId").equals(projectCluesNew.getOpportunityClueId())).collect(Collectors.toList());
                    List<Map> reList = new ArrayList<>();
                    final Boolean[] ff = {false};
                    if(!CollectionUtils.isEmpty(cList)){
                        cList.stream().forEach(x->{
                            if(ff[0]){
                                Map reMap = new HashMap<>();
                                reMap.put("salesAttributionName",x.get("reportUserName"));
                                reMap.put("reportCreateTime",x.get("reportCreateTime"));
                                reMap.put("opportunityClueId",UUID.randomUUID().toString());
                                reMap.put("rowKey",x.get("opportunityClueId"));
                                reMap.put("type","ret");
                                reList.add(reMap);
                            }
                            ff[0] = true;
                        });
                    }
                    projectCluesNew.setChildren(reList);
                }

            }
        }
        PageInfo<ProjectCluesNew> pageInfo = new PageInfo<>();
        pageInfo.setList(list);
        pageInfo.setTotal(Integer.parseInt(total));
        return pageInfo;
    }

    /**
     * 校验当前用户是否需要限制权限查询
     * @param jobs
     * @return
     */
    @Override
    public Boolean checkJobs(List<String> jobs) {
        Boolean flag = false;
        if (CollectionUtils.isEmpty(jobs)) {
            return flag;
        }
        for (String job : jobs) {
            if (limitJobs.contains(job)) {
                flag = true ;
                break;
            }
        }
        return flag;
    }


    private Boolean checkJobsSjg(List<String> jobs) {
        List<String> limitJobsm = Arrays.asList("qyzszj","qyxsjl","qyyxjl","zszj","xsjl","yxjl","zygw","qyzygw");
        Boolean flag = false;
        if (CollectionUtils.isEmpty(jobs)) {
            return flag;
        }
        for (String job : jobs) {
            if (!limitJobsm.contains(job)) {
                flag = true ;
                break;
            }
        }
        return flag;
    }


    public String getPerfectionProgress(ProjectCluesNew informationVO){
        int count =0;
        int num =0;
        count++;
        if (!StringUtils.isEmpty(informationVO.getCustomerType())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getSourceMode())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getCustomerName())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getBelongIndustrise())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getBelongIndustriseTwo())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getBelongIndustriseThree())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getBelongIndustriseFour())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getMainProducts())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getContacts())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getCustomerGender())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getCustomerMobile())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getCustomerCardType())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getCustomerCardNum())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getPosition())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getLegalPerson())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getLegalPersonPhone())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getLegalPersonCardNum())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getEnterpriseType())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getCustomerAddress())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getDetailedAddress())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getPostCode())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getFloor())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getRentAndSaleType())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getRentalPrice())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getEnclosures())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getIsPark())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getYxArea())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getIntentionClass())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getIntentionType())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getIntentionalAreaDesc())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getIntentionalPrice())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getIntentionalFloorDesc())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getNowRent())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getNowOfficeSpace())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getPollutantDischarge())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getZyreason())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getResistanceDesc())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getTechnologicalProcess())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getBusinessProducts())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getMajorEquipment())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getMainRawMaterials())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getImportantDescription())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getPeopleNum())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getExistingPlantArea())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getElectricityYear())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getWaterYear())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getAnnualOutputValue())){
            num++;
        }
        count++;
        if (!StringUtils.isEmpty(informationVO.getTaxAmountYear())){
            num++;
        }
        double percent = (double) num / count * 100;
        String tail = new DecimalFormat("0.00").format(percent) + "%";
        return tail;
    }

    //    查询案场信息
    @Override
    public PageInfo<ProjectCluesNew> courtCaseByUser(ExcelForm projectCluesForm) throws ParseException {
        if (projectCluesForm.getSearch() != null && !"".equals(projectCluesForm.getSearch())){
            String search = projectCluesForm.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                projectCluesForm.setCustomerMobile(search);
            } else {
                projectCluesForm.setCustomerName(search);
            }
        }
        int pageNum = 1;
        int pageSize = 10;
        if (projectCluesForm.getPageNum() != null && projectCluesForm.getPageNum() != "" ){
            pageNum = Integer.parseInt(projectCluesForm.getPageNum());
        }
        if (projectCluesForm.getPageSize() != null && projectCluesForm.getPageSize() != ""){
            pageSize = Integer.parseInt(projectCluesForm.getPageSize());
        }
        if(CollectionUtils.isEmpty(projectCluesForm.getProjectList())){
            List<String> proList = new ArrayList<>();
            proList.add("");
            projectCluesForm.setProjectList(proList);
        }
        projectCluesForm.setOwnerUserId(SecurityUtils.getUserId());
        PageHelper.startPage(pageNum, pageSize);
        List<ProjectCluesNew> list = projectCluesDao.courtCase(projectCluesForm);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time1 = format.format(new Date());
        if (!CollectionUtils.isEmpty(list)) {
            for (ProjectCluesNew projectCluesNew : list) {
                //剩余天数需要计算
                if(StringUtils.isEmpty(projectCluesNew.getSalesFollowExpireDate()) || "签约".equals(projectCluesNew.getClueStatus())){
                    projectCluesNew.setSalesFollowExpireDate("无");
                    projectCluesNew.setRemainingDays("永久");
                }else {
                    String time2 = projectCluesNew.getSalesFollowExpireDate();
                    Integer remainingDays = this.dateDifference(time1, time2);
                    projectCluesNew.setRemainingDays(String.valueOf(remainingDays));
                }
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    public ResultBody getCourtCaseIsExport(ExcelForm projectCluesForm) throws ParseException {
        //获取用户权限 如果存在管理员支持模糊查询
        projectCluesForm.setType(projectCluesDao.getUserJobHsxt(SecurityUtils.getUserId()));
        //判断是否存在关联企业
        if(StringUtils.isNotEmpty(projectCluesForm.getCustomerName()) || StringUtils.isNotEmpty(projectCluesForm.getCustomerMobile())){
            projectCluesForm.setRelaCustomerMainIds(projectCluesDao.getRelaCustomerMainId(projectCluesForm));
        }
        //排除申请权限配置项目的未配置组
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(), projectCluesForm.getProjectList());
        projectCluesForm.setOrgIds(orgIds);
        String total = projectCluesDao.courtCaseCount(projectCluesForm);
        if(Integer.parseInt(total) <= 100000){
            return ResultBody.success("您当前导出数据在100000条以内，文件将被直接下载");
        }else {
            return ResultBody.error(201,"下载任务创建成功，请关注右上角下载任务状态");
        }
    }

    //    案场信息导出
    @Override
    public void courtCaseExport(HttpServletRequest request, HttpServletResponse response,
                                ExcelForm projectCluesForm) throws ParseException {


        List<Map> fileds = projectCluesForm.getFileds();
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        List<ProjectCluesNew> projectCluesNewList = new ArrayList<>();
        if (projectCluesForm.getSearch() != null && !"".equals(projectCluesForm.getSearch())){
            String search = projectCluesForm.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                projectCluesForm.setCustomerMobile(search);
            } else {
                projectCluesForm.setCustomerName(search);
            }
        }
        if(CollectionUtils.isEmpty(projectCluesForm.getProjectList())){
            List<String> proList = new ArrayList<>();
            proList.add("");
            projectCluesForm.setProjectList(proList);
        }
        //导出的文档下面的名字
        String excelName = "报备客户台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        projectCluesNewList = projectCluesDao.courtCase(projectCluesForm);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time1 = format.format(new Date());
        if (!CollectionUtils.isEmpty(projectCluesNewList)) {
            //报备链路
            List<Map> rpList = new ArrayList<>();
            if(StringUtils.isEmpty(projectCluesForm.getOwnerUserId())){
                rpList = projectCluesDao.getReportList(projectCluesNewList);
            }
            for (ProjectCluesNew projectCluesNew : projectCluesNewList) {
                //剩余天数需要计算
                if(StringUtils.isEmpty(projectCluesNew.getSalesFollowExpireDate()) || "签约".equals(projectCluesNew.getClueStatus())){
                    projectCluesNew.setSalesFollowExpireDate("无");
                    projectCluesNew.setRemainingDays("永久");
                }else {
                    String time2 = projectCluesNew.getSalesFollowExpireDate();
                    Integer remainingDays = this.dateDifference(time1, time2);
                    projectCluesNew.setRemainingDays(String.valueOf(remainingDays));
                }
                //信息完善度
                projectCluesNew.setPerfectionProgress(this.getPerfectionProgress(projectCluesNew));
                //报备链路
                if(!CollectionUtils.isEmpty(rpList)){
                    List<Map> cList = rpList.stream().filter(x->x.get("opportunityClueId").equals(projectCluesNew.getOpportunityClueId())).collect(Collectors.toList());
                    List<Map> reList = new ArrayList<>();
                    final Boolean[] ff = {false};
                    if(!CollectionUtils.isEmpty(cList)){
                        if (cList.size() > 100) {
                            cList = cList.subList(0, 99);
                        }
                        cList.stream().forEach(x->{
                            if(ff[0]){
                                Map reMap = new HashMap<>();
                                reMap.put("salesAttributionName",x.get("reportUserName"));
                                reMap.put("reportCreateTime",x.get("reportCreateTime"));
                                reMap.put("opportunityClueId",UUID.randomUUID().toString());
                                reMap.put("rowKey",x.get("opportunityClueId"));
                                reMap.put("type","ret");
                                reList.add(reMap);
                            }
                            ff[0] = true;
                        });
                    }
                    projectCluesNew.setChildren(reList);
                }
            }
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(projectCluesForm.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(projectCluesForm.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(projectCluesForm));
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (projectCluesNewList != null && projectCluesNewList.size() > 0){
            String isAllStr = projectCluesForm.getIsAll();
            boolean isAll = true;
            boolean isHasChild = true;
            if ("1".equals(isAllStr)) isAll = false;
//            headers = projectCluesNewList.get(0).courtCaseTitle2;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (ProjectCluesNew model : projectCluesNewList) {
                model.setRownum(rowNum);
                if(CollectionUtils.isEmpty(model.getChildren())){
                    isHasChild = false;
                }else {
                    isHasChild = true;
                }
                Object[] oArray = model.toData2(isAll,filedCodes,isHasChild);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel2(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//  案场信息导出
  @Override
  public void courtCaseExportNoLink(HttpServletRequest request, HttpServletResponse response,
                              ExcelForm projectCluesForm) throws ParseException {


      List<Map> fileds = projectCluesForm.getFileds();
      fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

      List<String> filedCodes = new ArrayList<>();
      List<String> filedNames = new ArrayList<>();
      for (Map filed : fileds) {
          filedCodes.add(filed.get("filedCode")+"");
          filedNames.add(filed.get("filedName")+"");
      }

      String userId = request.getHeader("userId");
      List<ProjectCluesNew> projectCluesNewList = new ArrayList<>();
      if (projectCluesForm.getSearch() != null && !"".equals(projectCluesForm.getSearch())){
          String search = projectCluesForm.getSearch();
          //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
          if (search.matches("[0-9]+")) {
              projectCluesForm.setCustomerMobile(search);
          } else {
              projectCluesForm.setCustomerName(search);
          }
      }
      if(CollectionUtils.isEmpty(projectCluesForm.getProjectList())){
          List<String> proList = new ArrayList<>();
          proList.add("");
          projectCluesForm.setProjectList(proList);
      }
      //导出的文档下面的名字
      String excelName = "报备客户台账";
      ArrayList<Object[]> dataset = new ArrayList<>();
      String[] headers = null;
      projectCluesNewList = projectCluesDao.courtCase(projectCluesForm);
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String time1 = format.format(new Date());
      //保存导出日志
      ExcelExportLog excelExportLog = new ExcelExportLog();
      String id = UUID.randomUUID().toString();
      excelExportLog.setId(id);
      excelExportLog.setMainTypeDesc(excelName);
      excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
      //获取项目集合数据（事业部，项目Id,项目名称）
      if (!CollectionUtils.isEmpty(projectCluesForm.getProjectList())) {
          Map proMap = excelImportMapper.getAreaNameAndProNames(projectCluesForm.getProjectList());
          excelExportLog.setAreaName(proMap.get("areaName")+"");
          excelExportLog.setProjectId(proMap.get("projectId")+"");
          excelExportLog.setProjectName(proMap.get("projectName")+"");
      }
      excelExportLog.setCreator(userId);
      excelExportLog.setExportStatus("2");
      excelExportLog.setDoSql(JSON.toJSONString(projectCluesForm));
      //保存任务表
      excelImportMapper.addExcelExportLog(excelExportLog);
      if (projectCluesNewList != null && projectCluesNewList.size() > 0){
          String isAllStr = projectCluesForm.getIsAll();
          boolean isAll = true;
          boolean isHasChild = true;
          if ("1".equals(isAllStr)) isAll = false;
//          headers = projectCluesNewList.get(0).courtCaseTitle2;
          headers = filedNames.toArray(new String[0]);
          int rowNum = 1;
          for (ProjectCluesNew model : projectCluesNewList) {
              model.setRownum(rowNum);
              if(CollectionUtils.isEmpty(model.getChildren())){
                  isHasChild = false;
              }else {
                  isHasChild = true;
              }
              Object[] oArray = model.toData2(isAll,filedCodes,isHasChild);
              dataset.add(oArray);
              rowNum++;
          }
          ExcelExportUtil excelExportUtil = new ExcelExportUtil();
          try {
              excelExportUtil.exportExcel2(excelName, headers, dataset, excelName, response,null);
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String courtCaseExportNew(HttpServletRequest request, HttpServletResponse response,
                                     ExcelForm projectCluesForm) throws ParseException {
        String userId = SecurityUtils.getUserId();
        //导出的文档下面的名字
        String excelName = "报备客户台账";
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("CE1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType(projectCluesForm.getIsAll());//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(projectCluesForm.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(projectCluesForm.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(projectCluesForm));
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

    //    案场信息导出
    @Override
    public void courtCaseByUserExport(HttpServletRequest request, HttpServletResponse response,
                                ExcelForm projectCluesForm) throws ParseException {


        List<Map> fileds = projectCluesForm.getFileds();
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        List<ProjectCluesNew> projectCluesNewList = new ArrayList<>();
        if (projectCluesForm.getSearch() != null && !"".equals(projectCluesForm.getSearch())){
            String search = projectCluesForm.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                projectCluesForm.setCustomerMobile(search);
            } else {
                projectCluesForm.setCustomerName(search);
            }
        }
        if(CollectionUtils.isEmpty(projectCluesForm.getProjectList())){
            List<String> proList = new ArrayList<>();
            proList.add("");
            projectCluesForm.setProjectList(proList);
        }
        //导出的文档下面的名字
        String excelName = "报备客户台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        projectCluesForm.setOwnerUserId(userId);
        projectCluesNewList = projectCluesDao.courtCase(projectCluesForm);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time1 = format.format(new Date());
        if (!CollectionUtils.isEmpty(projectCluesNewList)) {
            for (ProjectCluesNew projectCluesNew : projectCluesNewList) {
                //剩余天数需要计算
                if(StringUtils.isEmpty(projectCluesNew.getSalesFollowExpireDate()) || "签约".equals(projectCluesNew.getClueStatus())){
                    projectCluesNew.setSalesFollowExpireDate("无");
                    projectCluesNew.setRemainingDays("永久");
                }else {
                    String time2 = projectCluesNew.getSalesFollowExpireDate();
                    Integer remainingDays = this.dateDifference(time1, time2);
                    projectCluesNew.setRemainingDays(String.valueOf(remainingDays));
                }
            }
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(projectCluesForm.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(projectCluesForm.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(projectCluesForm));
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (projectCluesNewList != null && projectCluesNewList.size() > 0){
            String isAllStr = projectCluesForm.getIsAll();
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
//            headers = projectCluesNewList.get(0).courtCaseTitle2;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (ProjectCluesNew model : projectCluesNewList) {
                model.setRownum(rowNum);
                Object[] oArray = model.toData2(isAll,filedCodes,false);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //    获取放弃记录
    @Override
    public PageInfo<CustomerDistributionRecordsVO> getAbandonRecord(CustomerDistributionRecords customerDistributionRecords) {
        if(CollectionUtils.isEmpty(customerDistributionRecords.getProjectList())){
            return new PageInfo<>();
        }
        if (customerDistributionRecords.getSearch() != null && !"".equals(customerDistributionRecords.getSearch())){
            String search = customerDistributionRecords.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                customerDistributionRecords.setCustomerMobile(search);
            } else {
                customerDistributionRecords.setCustomerName(search);
            }
        }
        int pageNum = 1;
        int pageSize = 10;
        if (customerDistributionRecords.getPageNum() != null){
            pageNum = Integer.parseInt(customerDistributionRecords.getPageNum());
        }
        if (customerDistributionRecords.getPageSize() != null){
            pageSize = Integer.parseInt(customerDistributionRecords.getPageSize());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<CustomerDistributionRecordsVO> list = projectCluesDao.getAbandonRecord(customerDistributionRecords);
        return new PageInfo<>(list);
    }

//    放弃记录导出
    @Override
    public void abandonRecordExport(HttpServletRequest request, HttpServletResponse response,
                                    CustomerDistributionRecords customerDistributionRecords) {

        List<Map> fileds = customerDistributionRecords.getFileds();
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        List<CustomerDistributionRecordsVO> customerDistributionRecordsVOList = new ArrayList<>();
        if (customerDistributionRecords.getSearch() != null && !"".equals(customerDistributionRecords.getSearch())){
            String search = customerDistributionRecords.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                customerDistributionRecords.setCustomerMobile(search);
            } else {
                customerDistributionRecords.setCustomerName(search);
            }
        }
        //导出的文档下面的名字
        String excelName = "放弃记录";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        customerDistributionRecordsVOList = projectCluesDao.getAbandonRecord(customerDistributionRecords);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(customerDistributionRecords.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(customerDistributionRecords.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(customerDistributionRecords));
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);

        if (customerDistributionRecordsVOList != null && customerDistributionRecordsVOList.size() > 0){
            String isAllStr = customerDistributionRecords.getIsAll();
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
//            headers = customerDistributionRecordsVOList.get(0).courtCaseTitle1;
            headers = filedNames.toArray(new String[0]);;
            int rowNum = 1;
            for (CustomerDistributionRecordsVO model : customerDistributionRecordsVOList) {
                model.setRownum(rowNum + "");
                Object[] oArray = model.toData1(isAll,filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String abandonRecordExportNew(HttpServletRequest request, HttpServletResponse response, CustomerDistributionRecords customerDistributionRecords) {
        String userId = SecurityUtils.getUserId();
        //导出的文档下面的名字
        String excelName = "全部放弃记录";
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("AR1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType(customerDistributionRecords.getIsAll());//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(customerDistributionRecords.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(customerDistributionRecords.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(customerDistributionRecords));
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

    @Override
    public ProjectProtectRuleVO selectRuleInfo(ProjectProtectRuleForm projectProtectRuleForm) {
        ProjectProtectRuleVO projectProtectRuleVO = projectCluesDao.ProjectProtectRuleInfo_Select(projectProtectRuleForm);
        if (projectProtectRuleVO != null) {
            String IsPrintStatus = projectCluesDao.getIsPrintStatus(projectProtectRuleForm.getProjectId());
            if (IsPrintStatus == null || "".equals(IsPrintStatus)) {
                projectProtectRuleVO.setIsPrintStatus(0);
            } else {
                projectProtectRuleVO.setIsPrintStatus(Integer.valueOf(IsPrintStatus));
            }
        }
        return projectProtectRuleVO;
    }

    @Override
    public List<ProjectProtectRuleVO> selectRuleCompany(ProjectProtectRuleForm projectProtectRuleForm) {
        Map map = new HashMap();
        //查询外销公司规则
        map.put("projectId", projectProtectRuleForm.getProjectId());
        map.put("orgCategory", projectProtectRuleForm.getProjectOrgCategory());
        List<ProjectProtectRuleVO> list = messageMapper.ProjectProtectProxyRule_Select(map);
        return list;
    }

    /**
     * 修改规则处理
     *
     * @return ProjectProtectRuleForm projectProtectRuleForm
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map updateChannelRule(RuleList te) {
        List<ProjectProtectRuleVO> ruleLogsMap = new ArrayList<>();
        Map<String, Object> returnMap = new LinkedHashMap<>();
        List<String> editParams = new ArrayList<>();
        try {
            String modifyType = te.getModifyType();
            String entrance = te.getEntrance();
            String IsPrintStatus = "";
            ProjectProtectRuleVO projectProtectRuleVOOld = null;
            boolean flag = false;
            //更新项目打印配置
            if ("site".equals(entrance)) {
                if (modifyType.equals("modify")) {
                    IsPrintStatus = projectCluesDao.getIsPrintStatus(te.getProjectId());
                    if (IsPrintStatus == null || "".equals(IsPrintStatus) || !IsPrintStatus.equals(te.getIsPrintStatus())) {
                        IsPrintStatus = "0";
                        editParams.add("是否启用打印");
                        flag = true;
                    }
                }
                Map proMap = new HashMap();
                proMap.put("projectId", te.getProjectId());
                proMap.put("isPrintStatus", te.getIsPrintStatus());
                projectCluesDao.updateIsPrintStatus(proMap);
            }
            if (modifyType.equals("modify")) {

                ProjectProtectRuleForm projectProtectRuleForm = new ProjectProtectRuleForm();
                projectProtectRuleForm.setProjectId(te.getOne().getProjectId());
                projectProtectRuleForm.setSourceType(te.getOne().getSourceType());
                if ("toker".equals(entrance)) {
                    projectProtectRuleForm.setProjectOrgCategory(te.getOne().getProjectOrgCategory());
                    te.getOne().setIdyVerification(te.getIdyVerification());
                    te.getOne().setStandbyModeStandbyMode(te.getStandbyModeStandbyMode());
                }
                projectProtectRuleVOOld = projectCluesDao.ProjectProtectRuleInfo_Select(projectProtectRuleForm);
                if (projectProtectRuleVOOld != null && projectProtectRuleVOOld.getSourceType()!=null) {
                    if ("toker".equals(entrance)) {
                        //判断自渠团队参数是否有修改（有修改则保存修改记录）
                        List<String> zqParams = this.isZQEdit(projectProtectRuleVOOld, te.getOne());
                        if (zqParams != null && zqParams.size() > 0) {
                            //保存修改批次
                            RuleEditLogBatch ruleEditLogBatch = new RuleEditLogBatch();
                            String batchId = UUID.randomUUID().toString();
                            ruleEditLogBatch.setId(batchId);
                            ruleEditLogBatch.setCreator(SecurityUtils.getUserId());
                            ruleEditLogBatch.setProjectId(te.getOne().getProjectId());
                            ruleEditLogBatch.setRuleType("自渠团队");
                            ruleEditLogBatch.setRuleTypeCode("自渠团队");
                            //判断类型（渠道/案场）
                            ruleEditLogBatch.setEditType(te.getOne().getSourceType().toString());
                            String[] str2 = zqParams.toArray(new String[zqParams.size()]);
                            String edit_params = StringUtils.join(str2, ",");
                            ruleEditLogBatch.setEditParams(edit_params);
                            ruleEditDao.addRuleEditBatch(ruleEditLogBatch);
                            //保存修改详情
                            List<RuleEditLogDetail> list = this.addZQParam(projectProtectRuleVOOld, te.getOne(), batchId, te.getOne().getProjectId());
                            ruleEditDao.addRuleEditLogBetails(list);
                        }
                        Map map = new HashMap();
                        //查询外销公司规则
                        map.put("projectId", te.getOne().getProjectId());
                        map.put("orgCategory", "7");
                        List<ProjectProtectRuleVO> oldList = messageMapper.ProjectProtectProxyRule_Select(map);
                        //判断中介团队参数是否有修改（有修改则保存修改记录）
                        if (oldList != null && oldList.size() > 0) {
                            ProjectProtectRuleVO xz = null;
                            for (ProjectProtectRuleVO pp : oldList) {
                                if (pp.getId() != null && pp.getStandbyMode() != null) {
                                    xz = pp;
                                    break;
                                }
                            }
                            if (xz == null) {
                                xz = oldList.get(0);
                                xz.setStandbyMode(0);
                                xz.setIdyVerification(0);
                            }
                            List<String> dlParam = this.isDlEdit(xz, te);
                            if (dlParam != null && dlParam.size() > 0) {
                                //保存修改批次
                                RuleEditLogBatch ruleEditLogBatch = new RuleEditLogBatch();
                                String batchId = UUID.randomUUID().toString();
                                ruleEditLogBatch.setId(batchId);
                                ruleEditLogBatch.setCreator(SecurityUtils.getUserId());
                                ruleEditLogBatch.setProjectId(te.getOne().getProjectId());
                                ruleEditLogBatch.setRuleType("中介团队");
                                ruleEditLogBatch.setRuleTypeCode("中介团队");
                                //判断类型（渠道/案场）
                                ruleEditLogBatch.setEditType(te.getOne().getSourceType().toString());
                                String[] str2 = dlParam.toArray(new String[dlParam.size()]);
                                String edit_params = StringUtils.join(str2, ",");
                                ruleEditLogBatch.setEditParams(edit_params);
                                ruleEditDao.addRuleEditBatch(ruleEditLogBatch);
                                //保存修改详情
                                List<RuleEditLogDetail> list = this.addDlParam(xz, te, batchId, te.getOne().getProjectId());
                                ruleEditDao.addRuleEditLogBetails(list);
                            }
                        }
                    } else {
                        List<String> params = this.isEdit(projectProtectRuleVOOld, te.getOne());
                        if (params.size() > 0) {
                            editParams.addAll(params);
                        }
                    }
                }
            }
            if (modifyType.equals("addNew")) {
                // 添加规则前删除规则
                if (te.getOne().getProjectId() == null || te.getOne().getProjectId() + "" == ""
                        || te.getOne().getSourceType() == null || te.getOne().getSourceType() + "" == "") {
                    returnMap.put("errmsg", "未获取项目级参数，请联系管理员!");
                    throw new RuntimeException("");
                }
                Map map = new HashMap();
                map.put("projectId", te.getOne().getProjectId());
                map.put("sourceType", te.getOne().getSourceType());
                map.put("projectOrgCategory", te.getOne().getProjectOrgCategory());
                messageMapper.deleteRule(map);

                //第一次新增则需要添加项目保护规则； 渠道自渠、案场自销公共部分方法
                messageMapper.ProjectProtectRuleInfo_Inset(te.getOne());
            } else {
                //编辑
                messageMapper.ProjectProtectRule_Update(te.getOne());
            }

            switch (entrance) {
                case "toker":
                    //获取分销规则
                    te.getTwo().forEach(rule -> {
                        rule.setIsAllowRepeatReport(te.getIsRep());
                        rule.setStandbyModeStandbyMode(te.getStandbyModeStandbyMode());
                        rule.setProjectOrgCategory(2);
                        rule.setSourceType(1);
                        rule.setProjectId(te.getProjectId());
                        rule.setIdyVerification(te.getIdyVerification());
                        ProjectProtectRuleVO projectProtectRuleVO = messageMapper.ProjectProtectProxyRuleIsExist_Select(rule);
                        //更新中介公司组织名称
                        Map comMap = new HashMap();
                        comMap.put("id", rule.getOrgId());
                        comMap.put("orgName", rule.getOrgName());
                        messageMapper.updateCompName(comMap);
                        if (projectProtectRuleVO != null && projectProtectRuleVO.getId() != null) { //存在即修改
                            //判断是否修改了参数（修改即保存修改记录）
                            List<String> zjParams = this.isZJEdit(projectProtectRuleVO, rule);
                            if (zjParams != null && zjParams.size() > 0) {
                                //保存修改批次
                                RuleEditLogBatch ruleEditLogBatch = new RuleEditLogBatch();
                                String batchId = UUID.randomUUID().toString();
                                ruleEditLogBatch.setId(batchId);
                                ruleEditLogBatch.setCreator(SecurityUtils.getUserId());
                                ruleEditLogBatch.setProjectId(te.getOne().getProjectId());
                                ruleEditLogBatch.setRuleType(rule.getCompanyName());
                                ruleEditLogBatch.setRuleTypeCode(rule.getOrgId());
                                ruleEditLogBatch.setEditType(te.getOne().getSourceType().toString());
                                String[] str2 = zjParams.toArray(new String[zjParams.size()]);
                                String edit_params = StringUtils.join(str2, ",");
                                ruleEditLogBatch.setEditParams(edit_params);
                                ruleEditDao.addRuleEditBatch(ruleEditLogBatch);
                                //保存修改详情
                                List<RuleEditLogDetail> list = this.addZJParam(projectProtectRuleVO, rule, batchId, te.getOne().getProjectId());
                                ruleEditDao.addRuleEditLogBetails(list);
                                if (zjParams.contains("关联公司") || zjParams.contains("有效开始时间") || zjParams.contains("有效结束时间")){
                                    //更新中介门店信息
                                    rule.setStartTime(rule.getStartTime());
                                    rule.setEndTime(rule.getEndTime());
                                    rule.setEditor(SecurityUtils.getUserId());
                                    projectCluesDao.updateCompDesc(rule);
                                }
                            }
                            rule.setId(projectProtectRuleVO.getId());
                            messageMapper.ProjectProtectRule_Update(rule);
                            ruleLogsMap.add(projectProtectRuleVO);
                        } else {//新增
                            messageMapper.ProjectProtectRuleInfo_Inset(rule);
                        }
                    });
                    break;

                default:

            }
            //如果有修改保存修改记录(案场参数)
            if (modifyType.equals("modify") && editParams.size() > 0 && "site".equals(entrance)) {
                //保存修改批次
                RuleEditLogBatch ruleEditLogBatch = new RuleEditLogBatch();
                String batchId = UUID.randomUUID().toString();
                ruleEditLogBatch.setId(batchId);
                ruleEditLogBatch.setCreator(SecurityUtils.getUserId());
                ruleEditLogBatch.setProjectId(te.getOne().getProjectId());
                //判断类型（渠道/案场）
                ruleEditLogBatch.setEditType(te.getOne().getSourceType().toString());
                String[] str2 = editParams.toArray(new String[editParams.size()]);
                String edit_params = StringUtils.join(str2, ",");
                ruleEditLogBatch.setEditParams(edit_params);
                ruleEditDao.addRuleEditBatch(ruleEditLogBatch);
                //保存修改详情
                List<RuleEditLogDetail> list = this.addParam(projectProtectRuleVOOld, te.getOne(), batchId, te.getOne().getProjectId());
                if (flag) {
                    RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
                    ruleEditLogDetail.setBatchId(batchId);
                    ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
                    ruleEditLogDetail.setParam("是否启用打印");
                    ruleEditLogDetail.setProjectId(te.getOne().getProjectId());
                    ruleEditLogDetail.setBeforeEdit(IsPrintStatus.equals("1") ? "是" : "否");
                    ruleEditLogDetail.setAfterEdit(te.getIsPrintStatus().equals("1") ? "是" : "否");
                    list.add(ruleEditLogDetail);
                }
                ruleEditDao.addRuleEditLogBetails(list);
            }


            //添加记录
            String ruleID = UUID.randomUUID().toString();
            System.out.println(ruleLogsMap);
            Map<String, String> ruleMaps = new HashMap<>();
            ruleMaps.put("ruleLogID", ruleID);
            ruleMaps.put("ProjectID", te.getProjectId());
            ruleMaps.put("userID", te.getUserId());
            messageMapper.ProjectProtectRuleLogs_Inset(ruleMaps);
            ruleLogsMap.forEach(ruleMap -> {
                        ruleMap.setRuleLogId(ruleID);
                        messageMapper.ProjectProtectRuleLogsDetails_Inset(ruleMap);
                    }
            );


            returnMap.put("code", "0");
            returnMap.put("errmsg", "保护规则修改成功");
        } catch (Exception e) {
            log.error("保护规则接口错误：" + e.getMessage(), e);
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            returnMap.put("code", "-1");
            returnMap.put("errmsg", "保护规则修改失败");

        }
        return returnMap;
    }

    public List<String> isZJEdit(ProjectProtectRuleVO projectProtectRuleVO, ProjectProtectRuleForm one) {
        List<String> params = new ArrayList<>();
        if (!(projectProtectRuleVO.getOrgName()).equals(one.getOrgName())) {
            params.add("门店展示名称");
        }
        if (!(projectProtectRuleVO.getReportExpireDays().toString()).equals(one.getReportExpireDays().toString())) {
            params.add("报备保护期（小时）");
        }
        if (!(projectProtectRuleVO.getReportDaysWarning().toString()).equals(one.getReportDaysWarning().toString())) {
            params.add("报备预警（小时）");
        }
        if (projectProtectRuleVO.getCutGuestDrainage().doubleValue() == one.getCutGuestDrainage().doubleValue()) {
        } else {
            params.add("防截客（分钟）");
        }
        if (!(projectProtectRuleVO.getChannelProtectionPeriod().toString()).equals(one.getChannelProtectionPeriod().toString())) {
            params.add("渠道保护期（天）");
        }
        if (!(projectProtectRuleVO.getChannelProtectionPeriodWarning().toString()).equals(one.getChannelProtectionPeriodWarning().toString())) {
            params.add("渠道保护期预警（天）");
        }
        if (one.getHeadquartersId()!=null && !"".equals(one.getHeadquartersId()) && (projectProtectRuleVO.getHeadquartersId()==null || "".equals(projectProtectRuleVO.getHeadquartersId()) || !one.getHeadquartersId().equals(projectProtectRuleVO.getHeadquartersId()))) {
            params.add("关联公司");
        }else if((StringUtils.isEmpty(one.getHeadquartersId())) && projectProtectRuleVO.getHeadquartersId()!=null && !"".equals(projectProtectRuleVO.getHeadquartersId())){
            params.add("关联公司");
        }
        if (!(one.getStartTime()).equals(projectProtectRuleVO.getStartTime())) {
            params.add("有效开始时间");
        }
        if (!(one.getEndTime()).equals(projectProtectRuleVO.getEndTime())) {
            params.add("有效结束时间");
        }
        return params;
    }

    public List<RuleEditLogDetail> addZJParam(ProjectProtectRuleVO projectProtectRuleVO, ProjectProtectRuleForm one, String batchId, String ProjectId) {
        List<RuleEditLogDetail> list = new ArrayList<>();
        RuleEditLogDetail ruleEditLogDetail0 = new RuleEditLogDetail();
        ruleEditLogDetail0.setBatchId(batchId);
        ruleEditLogDetail0.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail0.setParam("门店展示名称");
        ruleEditLogDetail0.setProjectId(ProjectId);
        ruleEditLogDetail0.setBeforeEdit(projectProtectRuleVO.getOrgName());
        ruleEditLogDetail0.setAfterEdit(one.getOrgName());
        list.add(ruleEditLogDetail0);

        RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
        ruleEditLogDetail.setBatchId(batchId);
        ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail.setParam("报备保护期（小时）");
        ruleEditLogDetail.setProjectId(ProjectId);
        ruleEditLogDetail.setBeforeEdit(projectProtectRuleVO.getReportExpireDays().toString());
        ruleEditLogDetail.setAfterEdit(one.getReportExpireDays().toString());
        list.add(ruleEditLogDetail);

        RuleEditLogDetail ruleEditLogDetail1 = new RuleEditLogDetail();
        ruleEditLogDetail1.setBatchId(batchId);
        ruleEditLogDetail1.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail1.setParam("报备预警（小时）");
        ruleEditLogDetail1.setProjectId(ProjectId);
        ruleEditLogDetail1.setBeforeEdit(projectProtectRuleVO.getReportDaysWarning().toString());
        ruleEditLogDetail1.setAfterEdit(one.getReportDaysWarning().toString());
        list.add(ruleEditLogDetail1);

        RuleEditLogDetail ruleEditLogDetail2 = new RuleEditLogDetail();
        ruleEditLogDetail2.setBatchId(batchId);
        ruleEditLogDetail2.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail2.setParam("防截客（分钟）");
        ruleEditLogDetail2.setProjectId(ProjectId);
        String be = df.format(projectProtectRuleVO.getCutGuestDrainage());
        String af = df.format(one.getCutGuestDrainage());
        if (be.contains(".00")) {
            be = be.replace(".00", "");
        }
        if (af.contains(".00")) {
            af = af.replace(".00", "");
        }
        ruleEditLogDetail2.setBeforeEdit(be);
        ruleEditLogDetail2.setAfterEdit(af);
        list.add(ruleEditLogDetail2);

        RuleEditLogDetail ruleEditLogDetail3 = new RuleEditLogDetail();
        ruleEditLogDetail3.setBatchId(batchId);
        ruleEditLogDetail3.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail3.setParam("渠道保护期（天）");
        ruleEditLogDetail3.setProjectId(ProjectId);
        ruleEditLogDetail3.setBeforeEdit(projectProtectRuleVO.getChannelProtectionPeriod().toString());
        ruleEditLogDetail3.setAfterEdit(one.getChannelProtectionPeriod().toString());
        list.add(ruleEditLogDetail3);

        RuleEditLogDetail ruleEditLogDetail4 = new RuleEditLogDetail();
        ruleEditLogDetail4.setBatchId(batchId);
        ruleEditLogDetail4.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail4.setParam("渠道保护期预警（天）");
        ruleEditLogDetail4.setProjectId(ProjectId);
        ruleEditLogDetail4.setBeforeEdit(projectProtectRuleVO.getChannelProtectionPeriodWarning().toString());
        ruleEditLogDetail4.setAfterEdit(one.getChannelProtectionPeriodWarning().toString());
        list.add(ruleEditLogDetail4);

        if (one.getHeadquartersId()!=null && !"".equals(one.getHeadquartersId()) && (projectProtectRuleVO.getHeadquartersId()==null || "".equals(projectProtectRuleVO.getHeadquartersId()) || !one.getHeadquartersId().equals(projectProtectRuleVO.getHeadquartersId()))) {
            RuleEditLogDetail ruleEditLogDetail5 = new RuleEditLogDetail();
            ruleEditLogDetail5.setBatchId(batchId);
            ruleEditLogDetail5.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail5.setParam("关联公司");
            ruleEditLogDetail5.setProjectId(ProjectId);
            String old = "";
            if (projectProtectRuleVO.getHeadquartersId()==null || "".equals(projectProtectRuleVO.getHeadquartersId())){
            }else{
                old = projectProtectRuleVO.getHeadquartersName();
            }
            ruleEditLogDetail5.setBeforeEdit(old);
            ruleEditLogDetail5.setAfterEdit(one.getHeadquartersName());
            list.add(ruleEditLogDetail5);
        }else if((StringUtils.isEmpty(one.getHeadquartersId())) && projectProtectRuleVO.getHeadquartersId()!=null && !"".equals(projectProtectRuleVO.getHeadquartersId())){
            RuleEditLogDetail ruleEditLogDetail5 = new RuleEditLogDetail();
            ruleEditLogDetail5.setBatchId(batchId);
            ruleEditLogDetail5.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail5.setParam("关联公司");
            ruleEditLogDetail5.setProjectId(ProjectId);
            String old = projectProtectRuleVO.getHeadquartersName();
            ruleEditLogDetail5.setBeforeEdit(old);
            ruleEditLogDetail5.setAfterEdit("");
            list.add(ruleEditLogDetail5);
        }

        RuleEditLogDetail ruleEditLogDetail6 = new RuleEditLogDetail();
        ruleEditLogDetail6.setBatchId(batchId);
        ruleEditLogDetail6.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail6.setParam("有效开始时间");
        ruleEditLogDetail6.setProjectId(ProjectId);
        ruleEditLogDetail6.setBeforeEdit(projectProtectRuleVO.getStartTime());
        ruleEditLogDetail6.setAfterEdit(one.getStartTime());
        list.add(ruleEditLogDetail6);

        RuleEditLogDetail ruleEditLogDetail7 = new RuleEditLogDetail();
        ruleEditLogDetail7.setBatchId(batchId);
        ruleEditLogDetail7.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail7.setParam("有效结束时间");
        ruleEditLogDetail7.setProjectId(ProjectId);
        ruleEditLogDetail7.setBeforeEdit(projectProtectRuleVO.getEndTime());
        ruleEditLogDetail7.setAfterEdit(one.getEndTime());
        list.add(ruleEditLogDetail7);

        return list;
    }

    public List<String> isDlEdit(ProjectProtectRuleVO projectProtectRuleVO, RuleList one) {
        List<String> params = new ArrayList<>();
        if (!(projectProtectRuleVO.getStandbyMode().toString()).equals(one.getStandbyModeStandbyMode().toString())) {
            params.add("报备模式");
        }
        if (!(projectProtectRuleVO.getIdyVerification().toString()).equals(one.getIdyVerification().toString())) {
            params.add("中介报备验证");
        }
        return params;
    }

    public List<RuleEditLogDetail> addDlParam(ProjectProtectRuleVO projectProtectRuleVO, RuleList one, String batchId, String ProjectId) {
        List<RuleEditLogDetail> list = new ArrayList<>();
        RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
        ruleEditLogDetail.setBatchId(batchId);
        ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail.setParam("报备模式");
        ruleEditLogDetail.setProjectId(ProjectId);
        ruleEditLogDetail.setBeforeEdit(projectProtectRuleVO.getStandbyMode().toString().equals("1") ? "全号" : "隐号");
        ruleEditLogDetail.setAfterEdit(one.getStandbyModeStandbyMode().toString().equals("1") ? "全号" : "隐号");
        list.add(ruleEditLogDetail);

        RuleEditLogDetail ruleEditLogDetail1 = new RuleEditLogDetail();
        ruleEditLogDetail1.setBatchId(batchId);
        ruleEditLogDetail1.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail1.setParam("中介报备验证");
        ruleEditLogDetail1.setProjectId(ProjectId);
        ruleEditLogDetail1.setBeforeEdit(projectProtectRuleVO.getIdyVerification().toString().equals("1") ? "是" : "否");
        ruleEditLogDetail1.setAfterEdit(one.getIdyVerification().toString().equals("1") ? "是" : "否");
        list.add(ruleEditLogDetail1);
        return list;
    }

    public List<String> isZQEdit(ProjectProtectRuleVO projectProtectRuleVO, ProjectProtectRuleForm one) {
        List<String> params = new ArrayList<>();
        if (!(projectProtectRuleVO.getReportExpireDays().toString()).equals(one.getReportExpireDays().toString())) {
            params.add("报备保护期（小时）");
        }
        if (!(projectProtectRuleVO.getReportDaysWarning().toString()).equals(one.getReportDaysWarning().toString())) {
            params.add("报备预警（小时）");
        }
        if (projectProtectRuleVO.getCutGuestInvite().doubleValue() == one.getCutGuestInvite().doubleValue()) {
        } else {
            params.add("防截客（分钟）");
        }
        if (!(projectProtectRuleVO.getChannelProtectionPeriod().toString()).equals(one.getChannelProtectionPeriod().toString())) {
            params.add("渠道保护期（天）");
        }
        if (!(projectProtectRuleVO.getChannelProtectionPeriodWarning().toString()).equals(one.getChannelProtectionPeriodWarning().toString())) {
            params.add("渠道保护期预警（天）");
        }
        if (!(projectProtectRuleVO.getPromptAttribution().toString()).equals(one.getPromptAttribution().toString())) {
            params.add("提示客户归属");
        }
        if (projectProtectRuleVO.getChannelReportMax()==null || !(projectProtectRuleVO.getChannelReportMax().toString()).equals(one.getChannelReportMax().toString())) {
            params.add("允许报备客户数");
        }
        if (projectProtectRuleVO.getIsTaoGuest()==null || !(projectProtectRuleVO.getIsTaoGuest().toString()).equals(one.getIsTaoGuest().toString())) {
            params.add("是否开启淘客");
        }
        if (projectProtectRuleVO.getTaoGuestNumber()==null || !(projectProtectRuleVO.getTaoGuestNumber().toString()).equals(one.getTaoGuestNumber().toString())) {
            params.add("允许淘客数量");
        }
        if (projectProtectRuleVO.getTaoGuestNumberType()==null || !(projectProtectRuleVO.getTaoGuestNumberType().toString()).equals(one.getTaoGuestNumberType().toString())) {
            params.add("允许淘客数量分类");
        }
        return params;
    }

    public List<RuleEditLogDetail> addZQParam(ProjectProtectRuleVO projectProtectRuleVO, ProjectProtectRuleForm one, String batchId, String ProjectId) {
        List<RuleEditLogDetail> list = new ArrayList<>();
        RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
        ruleEditLogDetail.setBatchId(batchId);
        ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail.setParam("报备保护期（小时）");
        ruleEditLogDetail.setProjectId(ProjectId);
        ruleEditLogDetail.setBeforeEdit(projectProtectRuleVO.getReportExpireDays().toString());
        ruleEditLogDetail.setAfterEdit(one.getReportExpireDays().toString());
        list.add(ruleEditLogDetail);

        RuleEditLogDetail ruleEditLogDetail1 = new RuleEditLogDetail();
        ruleEditLogDetail1.setBatchId(batchId);
        ruleEditLogDetail1.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail1.setParam("报备预警（小时）");
        ruleEditLogDetail1.setProjectId(ProjectId);
        ruleEditLogDetail1.setBeforeEdit(projectProtectRuleVO.getReportDaysWarning().toString());
        ruleEditLogDetail1.setAfterEdit(one.getReportDaysWarning().toString());
        list.add(ruleEditLogDetail1);

        RuleEditLogDetail ruleEditLogDetail2 = new RuleEditLogDetail();
        ruleEditLogDetail2.setBatchId(batchId);
        ruleEditLogDetail2.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail2.setParam("防截客（分钟）");
        ruleEditLogDetail2.setProjectId(ProjectId);
        String be = df.format(projectProtectRuleVO.getCutGuestInvite());
        String af = df.format(one.getCutGuestInvite());
        if (be.contains(".00")) {
            be = be.replace(".00", "");
        }
        if (af.contains(".00")) {
            af = af.replace(".00", "");
        }
        ruleEditLogDetail2.setBeforeEdit(be);
        ruleEditLogDetail2.setAfterEdit(af);
        list.add(ruleEditLogDetail2);

        RuleEditLogDetail ruleEditLogDetail3 = new RuleEditLogDetail();
        ruleEditLogDetail3.setBatchId(batchId);
        ruleEditLogDetail3.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail3.setParam("渠道保护期（天）");
        ruleEditLogDetail3.setProjectId(ProjectId);
        ruleEditLogDetail3.setBeforeEdit(projectProtectRuleVO.getChannelProtectionPeriod().toString());
        ruleEditLogDetail3.setAfterEdit(one.getChannelProtectionPeriod().toString());
        list.add(ruleEditLogDetail3);

        RuleEditLogDetail ruleEditLogDetail4 = new RuleEditLogDetail();
        ruleEditLogDetail4.setBatchId(batchId);
        ruleEditLogDetail4.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail4.setParam("渠道保护期预警（天）");
        ruleEditLogDetail4.setProjectId(ProjectId);
        ruleEditLogDetail4.setBeforeEdit(projectProtectRuleVO.getChannelProtectionPeriodWarning().toString());
        ruleEditLogDetail4.setAfterEdit(one.getChannelProtectionPeriodWarning().toString());
        list.add(ruleEditLogDetail4);

        RuleEditLogDetail ruleEditLogDetail8 = new RuleEditLogDetail();
        ruleEditLogDetail8.setBatchId(batchId);
        ruleEditLogDetail8.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail8.setParam("提示客户归属");
        ruleEditLogDetail8.setProjectId(ProjectId);
        ruleEditLogDetail8.setBeforeEdit(projectProtectRuleVO.getPromptAttribution()==null?"否":projectProtectRuleVO.getPromptAttribution().toString().equals("1") ? "是" : "否");
        ruleEditLogDetail8.setAfterEdit(one.getPromptAttribution().toString().equals("1") ? "是" : "否");
        list.add(ruleEditLogDetail8);

        RuleEditLogDetail ruleEditLogDetail9 = new RuleEditLogDetail();
        ruleEditLogDetail9.setBatchId(batchId);
        ruleEditLogDetail9.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail9.setParam("允许报备客户数");
        ruleEditLogDetail9.setProjectId(ProjectId);
        ruleEditLogDetail9.setBeforeEdit(projectProtectRuleVO.getChannelReportMax()==null?"0":projectProtectRuleVO.getChannelReportMax().toString());
        ruleEditLogDetail9.setAfterEdit(one.getChannelReportMax().toString());
        list.add(ruleEditLogDetail9);

        RuleEditLogDetail ruleEditLogDetail10 = new RuleEditLogDetail();
        ruleEditLogDetail10.setBatchId(batchId);
        ruleEditLogDetail10.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail10.setParam("是否开启淘客");
        ruleEditLogDetail10.setProjectId(ProjectId);
        ruleEditLogDetail10.setBeforeEdit(projectProtectRuleVO.getIsTaoGuest()==null?"否":projectProtectRuleVO.getIsTaoGuest().toString().equals("1")?"是":"否");
        ruleEditLogDetail10.setAfterEdit(one.getIsTaoGuest().toString().equals("1")?"是":"否");
        list.add(ruleEditLogDetail10);

        RuleEditLogDetail ruleEditLogDetail11 = new RuleEditLogDetail();
        ruleEditLogDetail11.setBatchId(batchId);
        ruleEditLogDetail11.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail11.setParam("允许淘客数量");
        ruleEditLogDetail11.setProjectId(ProjectId);
        ruleEditLogDetail11.setBeforeEdit(projectProtectRuleVO.getTaoGuestNumber()==null?"0":projectProtectRuleVO.getTaoGuestNumber().toString());
        ruleEditLogDetail11.setAfterEdit(one.getTaoGuestNumber().toString());
        list.add(ruleEditLogDetail11);

        RuleEditLogDetail ruleEditLogDetail12 = new RuleEditLogDetail();
        ruleEditLogDetail12.setBatchId(batchId);
        ruleEditLogDetail12.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail12.setParam("允许淘客数量分类");
        ruleEditLogDetail12.setProjectId(ProjectId);
        ruleEditLogDetail12.setBeforeEdit(projectProtectRuleVO.getTaoGuestNumberType()==null?"总量":projectProtectRuleVO.getTaoGuestNumberType().toString().equals("1")?"每天":"总量");
        ruleEditLogDetail12.setAfterEdit(one.getTaoGuestNumberType().toString().equals("1")?"每天":"总量");
        list.add(ruleEditLogDetail12);

        return list;
    }

    public List<RuleEditLogDetail> addParam(ProjectProtectRuleVO projectProtectRuleVO, ProjectProtectRuleForm one, String batchId, String ProjectId) {
        List<RuleEditLogDetail> list = new ArrayList<>();
        RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
        ruleEditLogDetail.setBatchId(batchId);
        ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail.setParam("报备保护期（小时）");
        ruleEditLogDetail.setProjectId(ProjectId);
        ruleEditLogDetail.setBeforeEdit(projectProtectRuleVO.getReportExpireDays().toString());
        ruleEditLogDetail.setAfterEdit(one.getReportExpireDays().toString());
        list.add(ruleEditLogDetail);

        RuleEditLogDetail ruleEditLogDetail1 = new RuleEditLogDetail();
        ruleEditLogDetail1.setBatchId(batchId);
        ruleEditLogDetail1.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail1.setParam("报备预警（小时）");
        ruleEditLogDetail1.setProjectId(ProjectId);
        ruleEditLogDetail1.setBeforeEdit(projectProtectRuleVO.getReportDaysWarning().toString());
        ruleEditLogDetail1.setAfterEdit(one.getReportDaysWarning().toString());
        list.add(ruleEditLogDetail1);

        RuleEditLogDetail ruleEditLogDetail2 = new RuleEditLogDetail();
        ruleEditLogDetail2.setBatchId(batchId);
        ruleEditLogDetail2.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail2.setParam("认购逾期（天）");
        ruleEditLogDetail2.setProjectId(ProjectId);
        ruleEditLogDetail2.setBeforeEdit(projectProtectRuleVO.getVisitExpireDays().toString());
        ruleEditLogDetail2.setAfterEdit(one.getVisitExpireDays().toString());
        list.add(ruleEditLogDetail2);

        RuleEditLogDetail ruleEditLogDetail3 = new RuleEditLogDetail();
        ruleEditLogDetail3.setBatchId(batchId);
        ruleEditLogDetail3.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail3.setParam("认购预警（天）");
        ruleEditLogDetail3.setProjectId(ProjectId);
        ruleEditLogDetail3.setBeforeEdit(projectProtectRuleVO.getVisitingWarning().toString());
        ruleEditLogDetail3.setAfterEdit(one.getVisitingWarning().toString());
        list.add(ruleEditLogDetail3);

        RuleEditLogDetail ruleEditLogDetail4 = new RuleEditLogDetail();
        ruleEditLogDetail4.setBatchId(batchId);
        ruleEditLogDetail4.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail4.setParam("签约预警（天）");
        ruleEditLogDetail4.setProjectId(ProjectId);
        ruleEditLogDetail4.setBeforeEdit(projectProtectRuleVO.getValidityOfWarning().toString());
        ruleEditLogDetail4.setAfterEdit(one.getValidityOfWarning().toString());
        list.add(ruleEditLogDetail4);

        RuleEditLogDetail ruleEditLogDetail5 = new RuleEditLogDetail();
        ruleEditLogDetail5.setBatchId(batchId);
        ruleEditLogDetail5.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail5.setParam("回款预警（天）");
        ruleEditLogDetail5.setProjectId(ProjectId);
        ruleEditLogDetail5.setBeforeEdit(projectProtectRuleVO.getRemittanceWarning().toString());
        ruleEditLogDetail5.setAfterEdit(one.getRemittanceWarning().toString());
        list.add(ruleEditLogDetail5);

        RuleEditLogDetail ruleEditLogDetail6 = new RuleEditLogDetail();
        ruleEditLogDetail6.setBatchId(batchId);
        ruleEditLogDetail6.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail6.setParam("跟进逾期（天）");
        ruleEditLogDetail6.setProjectId(ProjectId);
        ruleEditLogDetail6.setBeforeEdit(projectProtectRuleVO.getTheNextVisitFollowupExpireDays().toString());
        ruleEditLogDetail6.setAfterEdit(one.getTheNextVisitFollowupExpireDays().toString());
        list.add(ruleEditLogDetail6);

        RuleEditLogDetail ruleEditLogDetail7 = new RuleEditLogDetail();
        ruleEditLogDetail7.setBatchId(batchId);
        ruleEditLogDetail7.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail7.setParam("跟进预警（天）");
        ruleEditLogDetail7.setProjectId(ProjectId);
        ruleEditLogDetail7.setBeforeEdit(projectProtectRuleVO.getFollowupExpireDaysWarning().toString());
        ruleEditLogDetail7.setAfterEdit(one.getFollowupExpireDaysWarning().toString());
        list.add(ruleEditLogDetail7);

        RuleEditLogDetail ruleEditLogDetail8 = new RuleEditLogDetail();
        ruleEditLogDetail8.setBatchId(batchId);
        ruleEditLogDetail8.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail8.setParam("提示客户归属");
        ruleEditLogDetail8.setProjectId(ProjectId);
        if (projectProtectRuleVO.getPromptAttribution()!=null){
            ruleEditLogDetail8.setBeforeEdit(projectProtectRuleVO.getPromptAttribution().toString().equals("1") ? "是" : "否");
        }else{
            ruleEditLogDetail8.setBeforeEdit("否");
        }
        ruleEditLogDetail8.setAfterEdit(one.getPromptAttribution().toString().equals("1") ? "是" : "否");
        list.add(ruleEditLogDetail8);

        RuleEditLogDetail ruleEditLogDetail9 = new RuleEditLogDetail();
        ruleEditLogDetail9.setBatchId(batchId);
        ruleEditLogDetail9.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail9.setParam("跟进逾期后是否掉入公共池");
        ruleEditLogDetail9.setProjectId(ProjectId);
        ruleEditLogDetail9.setBeforeEdit(projectProtectRuleVO.getIsEnterPublicPool()==null?"否":projectProtectRuleVO.getIsEnterPublicPool().toString().equals("1") ? "是" : "否");
        ruleEditLogDetail9.setAfterEdit(one.getIsEnterPublicPool().toString().equals("1") ? "是" : "否");
        list.add(ruleEditLogDetail9);

//        RuleEditLogDetail ruleEditLogDetail10 = new RuleEditLogDetail();
//        ruleEditLogDetail10.setBatchId(batchId);
//        ruleEditLogDetail10.setCreator(SecurityUtils.getUserId());
//        ruleEditLogDetail10.setParam("是否开启淘客");
//        ruleEditLogDetail10.setProjectId(ProjectId);
//        ruleEditLogDetail10.setBeforeEdit(projectProtectRuleVO.getIsTaoGuest()==null?"否":projectProtectRuleVO.getIsTaoGuest().toString().equals("1")?"是":"否");
//        ruleEditLogDetail10.setAfterEdit(one.getIsTaoGuest().toString().equals("1")?"是":"否");
//        list.add(ruleEditLogDetail10);
//
//        RuleEditLogDetail ruleEditLogDetail11 = new RuleEditLogDetail();
//        ruleEditLogDetail11.setBatchId(batchId);
//        ruleEditLogDetail11.setCreator(SecurityUtils.getUserId());
//        ruleEditLogDetail11.setParam("允许淘客数量");
//        ruleEditLogDetail11.setProjectId(ProjectId);
//        ruleEditLogDetail11.setBeforeEdit(projectProtectRuleVO.getTaoGuestNumber()==null?"0":projectProtectRuleVO.getTaoGuestNumber().toString());
//        ruleEditLogDetail11.setAfterEdit(one.getTaoGuestNumber().toString());
//        list.add(ruleEditLogDetail11);
//
//        RuleEditLogDetail ruleEditLogDetail12 = new RuleEditLogDetail();
//        ruleEditLogDetail12.setBatchId(batchId);
//        ruleEditLogDetail12.setCreator(SecurityUtils.getUserId());
//        ruleEditLogDetail12.setParam("允许淘客数量分类");
//        ruleEditLogDetail12.setProjectId(ProjectId);
//        ruleEditLogDetail12.setBeforeEdit(projectProtectRuleVO.getTaoGuestNumberType()==null?"总量":projectProtectRuleVO.getTaoGuestNumberType().toString().equals("1")?"每天":"总量");
//        ruleEditLogDetail12.setAfterEdit(one.getTaoGuestNumberType().toString().equals("1")?"每天":"总量");
//        list.add(ruleEditLogDetail12);
//
//        RuleEditLogDetail ruleEditLogDetail13 = new RuleEditLogDetail();
//        ruleEditLogDetail13.setBatchId(batchId);
//        ruleEditLogDetail13.setCreator(SecurityUtils.getUserId());
//        ruleEditLogDetail13.setParam("是否开启案场淘客");
//        ruleEditLogDetail13.setProjectId(ProjectId);
//        ruleEditLogDetail13.setBeforeEdit(projectProtectRuleVO.getIsCaseTaoGuest()==null?"否":projectProtectRuleVO.getIsCaseTaoGuest().toString().equals("1")?"是":"否");
//        ruleEditLogDetail13.setAfterEdit(one.getIsCaseTaoGuest().toString().equals("1")?"是":"否");
//        list.add(ruleEditLogDetail13);
//
//        RuleEditLogDetail ruleEditLogDetail14 = new RuleEditLogDetail();
//        ruleEditLogDetail14.setBatchId(batchId);
//        ruleEditLogDetail14.setCreator(SecurityUtils.getUserId());
//        ruleEditLogDetail14.setParam("允许案场淘客数量");
//        ruleEditLogDetail14.setProjectId(ProjectId);
//        ruleEditLogDetail14.setBeforeEdit(projectProtectRuleVO.getCaseTaoGuestNumber()==null?"0":projectProtectRuleVO.getCaseTaoGuestNumber().toString());
//        ruleEditLogDetail14.setAfterEdit(one.getCaseTaoGuestNumber().toString());
//        list.add(ruleEditLogDetail14);
//
//        RuleEditLogDetail ruleEditLogDetail15 = new RuleEditLogDetail();
//        ruleEditLogDetail15.setBatchId(batchId);
//        ruleEditLogDetail15.setCreator(SecurityUtils.getUserId());
//        ruleEditLogDetail15.setParam("允许案场淘客数量分类");
//        ruleEditLogDetail15.setProjectId(ProjectId);
//        ruleEditLogDetail15.setBeforeEdit(projectProtectRuleVO.getCaseGuestNumberType()==null?"总量":projectProtectRuleVO.getCaseGuestNumberType().toString().equals("1")?"每天":"总量");
//        ruleEditLogDetail15.setAfterEdit(one.getCaseGuestNumberType().toString().equals("1")?"每天":"总量");
//        list.add(ruleEditLogDetail15);
        return list;
    }

    public List<String> isEdit(ProjectProtectRuleVO projectProtectRuleVO, ProjectProtectRuleForm one) {
        List<String> params = new ArrayList<>();
        if (!(projectProtectRuleVO.getReportExpireDays().toString()).equals(one.getReportExpireDays().toString())) {
            params.add("报备保护期（小时）");
        }
        if (!(projectProtectRuleVO.getReportDaysWarning().toString()).equals(one.getReportDaysWarning().toString())) {
            params.add("报备预警（小时）");
        }
        if (!(projectProtectRuleVO.getVisitExpireDays().toString()).equals(one.getVisitExpireDays().toString())) {
            params.add("认购逾期（天）");
        }
        if (!(projectProtectRuleVO.getVisitingWarning().toString()).equals(one.getVisitingWarning().toString())) {
            params.add("认购预警（天）");
        }
        if (!(projectProtectRuleVO.getValidityOfWarning().toString()).equals(one.getValidityOfWarning().toString())) {
            params.add("签约预警（天）");
        }
        if (!(projectProtectRuleVO.getRemittanceWarning().toString()).equals(one.getRemittanceWarning().toString())) {
            params.add("回款预警（天）");
        }
        if (!(projectProtectRuleVO.getTheNextVisitFollowupExpireDays().toString()).equals(one.getTheNextVisitFollowupExpireDays().toString())) {
            params.add("跟进逾期（天）");
        }
        if (!(projectProtectRuleVO.getFollowupExpireDaysWarning().toString()).equals(one.getFollowupExpireDaysWarning().toString())) {
            params.add("跟进预警（天）");
        }

        if (projectProtectRuleVO.getPromptAttribution() == null || !(projectProtectRuleVO.getPromptAttribution().toString()).equals(one.getPromptAttribution().toString())) {
            params.add("提示客户归属");
        }
        if (projectProtectRuleVO.getIsEnterPublicPool() ==null || !(projectProtectRuleVO.getIsEnterPublicPool().toString()).equals(one.getIsEnterPublicPool().toString())) {
            params.add("跟进逾期后是否掉入公共池");
        }
//        if (projectProtectRuleVO.getIsTaoGuest() == null || !(projectProtectRuleVO.getIsTaoGuest().toString()).equals(one.getIsTaoGuest().toString())) {
//            params.add("是否开启淘客");
//        }
//        if (projectProtectRuleVO.getTaoGuestNumber() == null || !(projectProtectRuleVO.getTaoGuestNumber().toString()).equals(one.getTaoGuestNumber().toString())) {
//            params.add("允许淘客数量");
//        }
//        if (projectProtectRuleVO.getTaoGuestNumberType() == null || !(projectProtectRuleVO.getTaoGuestNumberType().toString()).equals(one.getTaoGuestNumberType().toString())) {
//            params.add("允许淘客数量分类");
//        }
        return params;
    }

//    基本信息
    @Override
    public ProjectCluesNew essentialInformation(Map map) {
        ProjectCluesNew projectCluesVO = projectCluesDao.essentialInformation(map);
        return projectCluesVO;
    }


    public static String ListToString(List list) {
        if (!CollectionUtils.isEmpty(list)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i)).append('|');
            }
            return list.isEmpty() ? "" : sb.toString().substring(0, sb.toString().length() - 1);
        }
        return null;
    }

    @Override
    public List<Map> associatedCustomers(Map map) {
        if (map.get("type") != null && "1".equals(map.get("type") + "") && map.get("projectClueId") != null) {
            String OpportunityClueId = projectCluesDao.getOppIdBYCluesId(map.get("projectClueId") + "");
            map.put("opportunityClueId", OpportunityClueId);
        }
        return projectCluesDao.associatedCustomers(map);
    }

    @Override
    public List<Map> nodeRecord(Map map) {
        //获取用户权限 如果存在管理员可以查看所以 其他权限只能查看当前专员
//        map.put("type",projectCluesDao.getUserJobHsxt(SecurityUtils.getUserId()));
        List<Map> list = projectCluesDao.nodeRecord(map);
        List<Map> followList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (Map maps : list) {
                String enclosures = String.valueOf(maps.get("enclosures"));
//                String threeOnesUrls = String.valueOf(maps.get("threeOnesUrls"));
                String drawingQuotationUrls = String.valueOf(maps.get("drawingQuotationUrls"));
                if (!StringUtils.isEmpty(enclosures) && !"null".equals(enclosures)) {
                    String[] ss = enclosures.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    maps.put("enclosures", enclosure);
                }else {
                    List<String> enclosure = new ArrayList<>();
                    maps.put("enclosures", enclosure);
                }
//                if (!StringUtils.isEmpty(threeOnesUrls)&& !"null".equals(threeOnesUrls)) {
//                    String[] ss = threeOnesUrls.split(",");
//                    List<String> enclosure = Arrays.asList(ss);
//                    maps.put("threeOnesUrls", enclosure);
//                }else {
//                    List<String> enclosure = new ArrayList<>();
//                    maps.put("threeOnesUrls", enclosure);
//                }
                if (!StringUtils.isEmpty(drawingQuotationUrls) && !"null".equals(drawingQuotationUrls)) {
                    String[] ss = drawingQuotationUrls.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    maps.put("drawingQuotationUrls", enclosure);
                }else {
                    List<String> enclosure = new ArrayList<>();
                    maps.put("drawingQuotationUrls", enclosure);
                }
                if(String.valueOf(maps.get("status")).equals("1") || String.valueOf(maps.get("status")).equals("4") || String.valueOf(maps.get("status")).equals("5")){
                    //节点移交人
                    List<Map> nodeMap = projectCluesDao.getFirstNodeMoveInfo(String.valueOf(maps.get("id")));
                    if(!CollectionUtils.isEmpty(nodeMap)){
                        nodeMap.stream().forEach(x->{
                            maps.put("approveUser",Arrays.asList(String.valueOf(maps.get("approveUser")).split(",")).stream().filter(user -> !user.equals(x.get("oldSalesName").toString().concat("(待审核)"))).collect(Collectors.joining(",")));
                        });
                    }
                    //移交审批人
                    List<Map> mainNodeMap = projectCluesDao.getMainNodeMoveInfo(String.valueOf(maps.get("id")));
                    if(!CollectionUtils.isEmpty(mainNodeMap)){
                        if(StringUtils.isEmpty(String.valueOf(maps.get("approveUser")))) {
                            mainNodeMap.stream().forEach(x->{
                                maps.put("approveUser",(String.valueOf(maps.get("approveUser")).concat(x.get("salesName")+"(待审核)")));
                            });
                        }else {
                            mainNodeMap.stream().forEach(x->{
                                maps.put("approveUser",(String.valueOf(maps.get("approveUser")).concat(","+ x.get("salesName")+"(待审核)")));
                            });
                        }
                    }
                }
            }
            followList.addAll(list);
        }
        return followList;
    }

    @Override
    public List<Map> dealRecord(Map map) {
        String oppoId = map.get("opportunityClueId")+"";
        List<String> oppoList = new ArrayList<>();
        oppoList.add(oppoId);
        //判断是否有转介客户
        List<String> referralOrderCustomerByOppId = projectCluesDao.getReferralOrderCustomerByOppId(oppoId);
        if(referralOrderCustomerByOppId != null && !referralOrderCustomerByOppId.isEmpty()){
            oppoList.addAll(referralOrderCustomerByOppId);//转介客户的id添加到集合整体查出交易信息
        }
        List<Map> list = projectCluesDao.dealRecord(oppoList);
        return list;
    }

    @Override
    public List<Map> clueNodeRecord(Map map) {
        List<Map> list = projectCluesDao.clueNodeRecord(map);
        return list;
    }

    @Override
    public List<Map> followUpRecord(Map map) {
        return projectCluesDao.followUpRecord(map);
    }

    @Override
    public List<Map> followUpOppRecord(Map map) {
        return projectCluesDao.followUpOppRecord(map);
    }

    @Override
    public OppInformation oppInformation(Map map) {
        //map.put("userId", SecurityUtils.getUserId());
        OppInformation oppInformation = projectCluesDao.oppInformation(map);
        String oCustomerName=oppInformation.getCustomerName();
        String oCustomerMobile=oppInformation.getCustomerMobile();

        UserOrgRelForm userOrgRelForm = new UserOrgRelForm();
        //userOrgRelForm.setUserId(SecurityUtils.getUserId());
        userOrgRelForm.setUserId(map.get("userId").toString());
        userOrgRelForm.setProjectId(oppInformation.getProjectId());

        //排除申请权限配置项目的未配置组
        List<String> jobs = projectCluesDao.getJobsByUserId(map.get("userId").toString());
        Boolean flag = checkJobsSjg(jobs);
        if(flag){
            oppInformation.setIsSelf("3");//管理者非限制引号权限
        }

        //取一个改为去所有的授权集合判断
        List<UserOrgRelForm> dataViewPremissionApproveStatusList = projectCluesDao.getDataViewPremissionApprove(
                userOrgRelForm);
        if (dataViewPremissionApproveStatusList != null && !dataViewPremissionApproveStatusList.isEmpty()) {
            for (UserOrgRelForm dataViewPremissionApproveStatus : dataViewPremissionApproveStatusList) {
                if (dataViewPremissionApproveStatus != null && dataViewPremissionApproveStatus.getOrgId() != null) {
                    List<String> orgIds = Arrays.asList(dataViewPremissionApproveStatus.getOrgId().split(","));

                    if (orgIds.contains(oppInformation.getSalesAttributionTeamId())) {
                        oppInformation.setIsSelf("3");
                    }
                }

                if (dataViewPremissionApproveStatus != null && 0 == dataViewPremissionApproveStatus.getIsNameShow()) {
                    oppInformation.setCustomerName(oppInformation.getCustomerNameIns());
                }
                if (dataViewPremissionApproveStatus != null && 0 == dataViewPremissionApproveStatus.getIsMobileShow()) {
                    oppInformation.setCustomerMobile(oppInformation.getCustomerMobileIns());
                    oppInformation.setLegalPersonPhone(oppInformation.getLegalPersonPhoneIns());
                }
            }
        }

        if (oppInformation!=null){
//            String company = projectCluesDao.selectRelationCompany(oppInformation.getOpportunityClueId() != null ?
//                    oppInformation.getOpportunityClueId() : null);
            List<Map> company = projectCluesDao.getCusRelateList(map);
            if (company!=null && company.size()>0){
                oppInformation.setRelCustomerList(company);
            }else{
                oppInformation.setRelCustomerList(new ArrayList<>());
            }

            List<String> enclosures = projectCluesDao.getOppEnclosures(map);
            oppInformation.setEnclosures(enclosures);
            //IsSelf为0：招商地图权限客户 IsSelf为1：本人客户权限  IsSelf为3：管理者数据权限
            if ("0".equals(oppInformation.getIsSelf())) {
                oppInformation.setCustomerName(oppInformation.getCustomerNameIns());
            }
            // 添加扩展信息
            Map<String, String> extendInfo = new HashMap<>();
            String clueStatus = oppInformation.getClueStatus();

            //接口传参客户阶段
            String type =String.valueOf(map.get("type"));
            List<Map<String, Object>> resultList = new ArrayList<>();
            // 根据客户状态添加不同的扩展信息
            if (type != null) {
                // 获取客户ID
                String opportunityClueId = oppInformation.getOpportunityClueId();
                if (opportunityClueId != null) {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("opportunityClueId", opportunityClueId);

                    // 已来访客户：展示首访时间、最新来访时间和来访项目
                    if (type.equals("2")) {
                        Map<String, String> visitInfo = projectCluesDao.getCustomerVisitInfo(paramMap);
                        if (visitInfo != null) {
                            addKeyValuePair(resultList, "首访时间", visitInfo.get("firstVisitTime"));
                            addKeyValuePair(resultList, "最新来访时间", visitInfo.get("lastVisitTime"));
                            addKeyValuePair(resultList, "来访项目", visitInfo.get("visitProject"));
                        }
                    }
                    // 已成交客户：展示首次成交时间、最近一次成交时间、成交项目，成交厂房类型（若有）、成交面积
                    if (type.equals("1")) {
                        Map<String, String> dealInfo = projectCluesDao.getCustomerDealInfo(paramMap);
                        if (dealInfo != null) {

                            addKeyValuePair(resultList, "首次成交时间", dealInfo.get("firstDealTime"));
                            addKeyValuePair(resultList, "最近一次成交时间", dealInfo.get("lastDealTime"));
                            addKeyValuePair(resultList, "成交项目", dealInfo.get("dealProject"));

                            // 成交厂房类型（若有）
                            String factoryType = dealInfo.get("factoryType");
                            if (factoryType != null && !factoryType.isEmpty()) {
                                addKeyValuePair(resultList, "成交厂房类型", dealInfo.get("factoryType"));
                            }

                            // 成交面积
                            String dealArea = dealInfo.get("dealArea");
                            if (dealArea != null && !dealArea.isEmpty()) {
                                addKeyValuePair(resultList, "成交面积", dealInfo.get("dealArea"));
                            }
                        }
                    }

                    // 已完成三个一客户：完成三个一的时间
                    if (type.equals("3")) {
                        Map<String, String> threeOneInfo = projectCluesDao.getCustomerThreeOneInfo(paramMap);
                        if (threeOneInfo != null) {
                            addKeyValuePair(resultList, "完成三个一的时间", threeOneInfo.get("threeOneCompleteTime"));
                        }
                    }
                    // 成交在其他项目
                    if (type.equals("2")) {
                            addKeyValuePair(resultList, "本项目所属阶段", "见到老板（未完成三个一）");
                    }

                }
            }

            //全号权限
            if(map.get("permissionLevel") !=null && "1".equals(map.get("permissionLevel").toString())){
                oppInformation.setCustomerName(oCustomerName);
                oppInformation.setCustomerMobile(oCustomerMobile);
            }
            // 设置扩展信息
            Gson gson = new Gson();
            String jsonString = gson.toJson(resultList);
            oppInformation.setExtendInfo(jsonString);
        }else{
            oppInformation = new OppInformation();
            oppInformation.setExtendInfo("");
        }
        //oppInformation.setCustomerNameIns(null);
        //oppInformation.setCustomerMobileIns(null);
        //oppInformation.setLegalPersonPhoneIns(null);
        return oppInformation;
    }

    // 提取公共方法，用于添加键值对到结果列表
    private void addKeyValuePair(List<Map<String, Object>> resultList, String key, Object value) {
        if (value != null) { // 可选：过滤空值
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("key", key);
            itemMap.put("value", value);
            resultList.add(itemMap);
        }
    }
    @Override
    public InformationVO cluesInformation(Map map) {
        //map.put("userId", SecurityUtils.getUserId());
        InformationVO informationVO = projectCluesDao.selectProjectClues(map);

        String oCustomerName=informationVO.getCustomerName();
        String oCustomerMobile=informationVO.getCustomerMobile();

        if (informationVO!=null){
            List<String> enclosures = projectCluesDao.getClueEnclosures(map);
            informationVO.setEnclosures(enclosures);
        }else{
            informationVO = new InformationVO();
        }
        UserOrgRelForm userOrgRelForm = new UserOrgRelForm();
        //userOrgRelForm.setUserId(SecurityUtils.getUserId());
        userOrgRelForm.setUserId(map.get("userId").toString());
        userOrgRelForm.setProjectId(informationVO.getProjectId());
        UserOrgRelForm dataViewPremissionApproveStatus = projectCluesDao.getDataViewPremissionApproveStatus(
            userOrgRelForm);
        if (dataViewPremissionApproveStatus != null && !"1".equals(informationVO.getIsSelf())) {
            List<String> orgIds = Arrays.asList(dataViewPremissionApproveStatus.getOrgId().split(","));
            if(orgIds.contains(informationVO.getSalesAttributionTeamId())){
                informationVO.setIsSelf("3");
            }
            if (0 == dataViewPremissionApproveStatus.getIsNameShow()) {
                informationVO.setCustomerName(informationVO.getCustomerNameIns());
            }
            if (1 == dataViewPremissionApproveStatus.getIsMobileShow()) {
                informationVO.setCustomerMobile(informationVO.getCustomerMobileIns());
            }
        }
        //IsSelf为0：招商地图权限客户 IsSelf为1：本人客户权限 IsSelf为2：管理者全号数据权限 IsSelf为3：管理者隐号数据权限
        if ("0".equals(informationVO.getIsSelf())) {
            informationVO.setCustomerName(informationVO.getCustomerNameIns());
        }
        //接口传参客户阶段
        String type =String.valueOf(map.get("type"));
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 已拜访客户：最新拜访时间
        if (type.equals("7")||type.equals("8")||type.equals("9")) {
            Map<String, String> visitInfo = projectCluesDao.getClueVisitInfo(map);
            if (visitInfo != null) {
                addKeyValuePair(resultList, "最新拜访时间", visitInfo.get("lastVisitTime"));
            }
        }
        // 设置扩展信息
        String jsonString="";
        Gson gson = new Gson();
        jsonString = gson.toJson(resultList);
        informationVO.setExtendInfo(jsonString);
//        informationVO.setCustomerNameIns(null);
//        informationVO.setCustomerMobileIns(null);

        //全号权限
        if(map.get("permissionLevel") != null && "1".equals(map.get("permissionLevel").toString())){
            informationVO.setCustomerName(oCustomerName);
            informationVO.setCustomerMobile(oCustomerMobile);
        }
        return informationVO;
    }

    //    编辑客户等级
    @Override
    public ResultBody updateCustomerGrade(ExcelForm projectCluesForm) {
        //判断客户等级是否变化
        List<String> opportunityList = projectCluesForm.getOpportunityList();
        opportunityList.stream().forEach(x->{
            //获取客户信息 判断客户等级是否变化
            Map oppMap = projectCluesDao.getCustomerInfoInsCl(x);
            //客户等级变化记录 客户等级日志表
            if (!"A".equals(String.valueOf(oppMap.get("customerLevel"))) && !"E".equals(String.valueOf(oppMap.get("customerLevel")))) {
                CustomerLevelRecordVo clMap = new CustomerLevelRecordVo();
                clMap.setOpportunityCueId(String.valueOf(oppMap.get("opportunityClueId")));
                clMap.setProjectClueId(String.valueOf(oppMap.get("projectClueId")));
                clMap.setCustomerLevel("A");
                clMap.setSalesAttributionId(String.valueOf(oppMap.get("salesAttributionId")));
                clMap.setSalesAttributionName(String.valueOf(oppMap.get("salesAttributionName")));
                clMap.setSalesAttributionTeamId(String.valueOf(oppMap.get("salesAttributionTeamId")));
                clMap.setSalesAttributionTeamName(String.valueOf(oppMap.get("salesAttributionTeamName")));
                clMap.setProjectId(String.valueOf(oppMap.get("projectId")));
                clMap.setAreaId(String.valueOf(oppMap.get("areaId")));
                clMap.setCreator(projectCluesForm.getUserId());
                projectCluesDao.insertCustomerLevelChangeLog(clMap);
            }
        });

        int i = projectCluesDao.updateCustomerGrade(projectCluesForm);
        return i > 0 ? ResultBody.success("更新成功") : ResultBody.success("没有需要更新的数据");
    }

//    申诉记录
    @Override
    public ResultBody getAppealRecord(Appeal appeal) {
        if (appeal.getSearch() != null && !"".equals(appeal.getSearch())) {
            String search = appeal.getSearch();
//            判断是手机号还是人名
            if (search.matches("[0-9]+")) {
                appeal.setMobile(search);
            } else {
                appeal.setUsername(search);
            }
        }
        int pageNum = 1;
        int pageSize = 10;
        if (appeal.getPageNum() != null && !"".equals(appeal.getPageNum())) {
            pageNum = Integer.parseInt(appeal.getPageNum());
        }
        if (appeal.getPageSize() != null && !"".equals(appeal.getPageSize())) {
            pageSize = Integer.parseInt(appeal.getPageSize());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<AppealVo> appealRecord = projectCluesDao.getAppealRecord(appeal);
        List<AppealVo> apList = new ArrayList<>();
        if(appealRecord != null && appealRecord.size() > 0){
            for (AppealVo map : appealRecord){
                String enclosures = map.getImgUrlStr();
                if (!StringUtils.isEmpty(enclosures)){
                    String[] ss = enclosures.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    map.setImgUrl(enclosure);
                }
            }
            apList.addAll(appealRecord);
        }
        return ResultBody.success(new PageInfo<>(apList));
    }

//    申诉记录导出
    @Override
    public void AppealRecordExport(HttpServletRequest request, HttpServletResponse response, Appeal appeal) {

        List<Map> fileds = appeal.getFileds();
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        List<AppealVo> appealVoList = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = "申诉记录";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        if (appeal.getSearch() != null && !"".equals(appeal.getSearch())) {
            String search = appeal.getSearch();
//            判断是手机号还是人名
            if (search.matches("[0-9]+")) {
                appeal.setMobile(search);
            } else {
                appeal.setUsername(search);
            }
        }
        appealVoList = projectCluesDao.getAppealRecord(appeal);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(appeal.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(appeal.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName") + "");
            excelExportLog.setProjectId(proMap.get("projectId") + "");
            excelExportLog.setProjectName(proMap.get("projectName") + "");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(appeal));
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (appealVoList != null && appealVoList.size() > 0){
            String isAllStr = appeal.getIsAll();
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
//            headers = appealVoList.get(0).courtCaseTitle1;
            headers = filedNames.toArray(new String[0]);

            int rowNum = 1;
            for (AppealVo model : appealVoList) {
                model.setRownum(rowNum + "");
                Object[] oArray = model.toData1(isAll,filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        //    任务管理台账
        // ... existing code ...
        @Override
        public List<TaskVo> getTaskAccount(Task task) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            String userId = request.getHeader("userid");
            String userName = request.getHeader("username");
            if (org.apache.commons.lang3.StringUtils.isEmpty(userId)) {
                userId = SecurityUtils.getUserId();
            }
            if (org.apache.commons.lang3.StringUtils.isEmpty(userName)) {
                userName = SecurityUtils.getUsername();
            }
            int pageNum = 1;
            int pageSize = 10;
            if (StringUtils.isEmpty(task.getTaskTypeId()) || StringUtils.isEmpty(TaskTypeEnum.getNameByType(task.getTaskTypeId()))) {
                task.setTaskTypeId("1");
            }
            if (task.getPageNum() != null && !"".equals(task.getPageNum())) {
                pageNum = Integer.parseInt(task.getPageNum());
            }
            if (task.getPageSize() != null && !"".equals(task.getPageSize())) {
                pageSize = Integer.parseInt(task.getPageSize());
            }
            if (CollectionUtils.isEmpty(task.getProjectList())) {
                List<String> proList = new ArrayList<>();
                proList.add("1");
                task.setProjectList(proList);
            }
            //限制经理、总监、营销经理
            List<String> orgIds = workbenchService.linkOrgIds(userId, task.getProjectList());
            task.setOrgIds(orgIds);
            task.setCreateBy(userId);
            List<String> userIdsCreate = new ArrayList<>();
            List<String> userIdsMember = new ArrayList<>();
            // 获取创建人ID
            if (StringUtils.isNotBlank(task.getCreateUserName())) {
                processUserNames(task.getCreateUserName(), userIdsCreate);
            }
            // 获取执行人ID
            if (StringUtils.isNotBlank(task.getMemberUserName())) {
                processUserNames(task.getMemberUserName(), userIdsMember);
            }
            Map paramMap = new HashMap();
            paramMap.put("UserId", userId);
            List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
            List<String> userIds = new ArrayList<>();
            userIds.add(userId);
            if (!CollectionUtils.isEmpty(userList)) {
                for (int i = 0; i < userList.size(); i++) {
                    String jobCode = String.valueOf(userList.get(i).get("JobCode"));
                    if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)
                            || "yxjl".equals(jobCode) || "qyyxjl".equals(jobCode)
                            || "zszj".equals(jobCode) || "qyzszj".equals(jobCode) ||"jtsjg".equals(jobCode)
                            || "xmz".equals(jobCode) || "qyfz".equals(jobCode) || "qyz".equals(jobCode) || "10001".equals(jobCode)) {
                        String orgPath = String.valueOf(userList.get(i).get("FullPath"));
                        List<String> userIds1 = custMapDao.getTeamUserNew(orgPath);
                        userIds.addAll(userIds1);
                    }
                }
            }
            List<String> userIdsC = new ArrayList<>();
            if (StringUtils.isNotBlank(task.getCreateUserName())) {
                userIdsC = userIds.stream().filter(uid -> userIdsCreate.contains(uid)).collect(Collectors.toList());
                userIdsC = userIdsC.isEmpty() ? Collections.singletonList("noUserId") : userIdsC;
            } else {
                userIdsC.addAll(userIds);
            }
            List<String> userIdsM = new ArrayList<>();
            if (StringUtils.isNotBlank(task.getMemberUserName())) {
                userIdsM = userIds.stream().filter(uid -> userIdsMember.contains(uid)).collect(Collectors.toList());
                userIdsM = userIdsM.isEmpty() ? Collections.singletonList("noUserId") : userIdsM;
            } else {
                userIdsM.addAll(userIds);
            }
            userIdsCreate.clear();
            userIdsCreate.addAll(userIdsC);
            userIdsMember.clear();
            userIdsMember.addAll(userIdsM);
            task.setUserIdsCreate(userIdsCreate);
            task.setUserIdsMember(userIdsMember);
            // 1. 分页查所有任务（父、子、单独任务）
            PageHelper.startPage(pageNum, pageSize);
            List<TaskVo> taskVoList =null;
            if("1".equals(task.getIsForDraw())){
                taskVoList=projectCluesDao.getTaskAccountForDraw(task);
            }else{
                taskVoList=projectCluesDao.getTaskAccount(task);
            }
            if (!CollectionUtils.isEmpty(taskVoList)) {
                // 2. 查这些任务的所有子任务
                List<String> taskIds = taskVoList.stream().map(TaskVo::getId).collect(Collectors.toList());
                String parentIdsStr = taskIds.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","));
                List<TaskVo> childTasks = projectCluesDao.getChildTasksByParentIds(parentIdsStr);
                Map<String, List<TaskVo>> childTaskMap = childTasks.stream().collect(Collectors.groupingBy(TaskVo::getParentId));
                // 3. 查所有任务（本页+子任务）的成员、客户
                List<String> allTaskIds = new ArrayList<>(taskIds);
                allTaskIds.addAll(childTasks.stream().map(TaskVo::getId).collect(Collectors.toList()));
                String allTaskIdsStr = allTaskIds.stream().collect(Collectors.joining("','", "'", "'"));
                List<TaskMember> allMembers = projectCluesDao.getUserByTaskId(allTaskIdsStr);
                Map<String, List<TaskMember>> memberMap = allMembers.stream().collect(Collectors.groupingBy(TaskMember::getTaskId));
                List<TaskCustomer> allCustomers = projectCluesDao.getCustomerByTaskId(allTaskIdsStr);
                Map<String, List<TaskCustomer>> customerMap = allCustomers.stream().collect(Collectors.groupingBy(TaskCustomer::getTaskId));
                // 4. 统计子任务的指标
                for (TaskVo child : childTasks) {
                    processTaskCompletionStats(child, memberMap.get(child.getId()), customerMap.get(child.getId()), null);
                }
                // 5. 统计本页任务
                // 先计算项目下所有任务的指标汇总（用于计算CompleteProportion）
                Map<String, Integer> projectSummary = calculateProjectTaskSummary(task.getProjectList().get(0), taskVoList);
                
                for (TaskVo task1 : taskVoList) {
                    initTaskCompletionData(task1);
                    task1.setTaskStatusDesc(TaskStatusEnum.getNameByType(task1.getTaskStatus()));
                    task1.setMemberList(memberMap.get(task1.getId()));
                    // 处理父任务的子任务信息
                    List<TaskVo> subTasks = childTaskMap.get(task1.getId());
                    if (StringUtils.isBlank(task1.getParentId())) {
                        // 父任务或单独任务
                        if (!CollectionUtils.isEmpty(subTasks)) {
                            task1.setSubTaskCount(subTasks.size());
                            task1.setSubTaskList(subTasks);
                            calculateParentTaskStats(task1, subTasks, projectSummary);
                        } else {
                            task1.setSubTaskCount(0);
                            // 单独任务，统计自己
                            processTaskCompletionStats(task1, memberMap.get(task1.getId()), customerMap.get(task1.getId()), projectSummary);
                        }
                        task1.setCustomerList(customerMap.get(task1.getId()));
                    } else {
                        // 子任务，统计自己
                        processTaskCompletionStats(task1, memberMap.get(task1.getId()), customerMap.get(task1.getId()), projectSummary);
                        task1.setCustomerList(customerMap.get(task1.getId()));
                    }
                    List<TaskVo> taskVoListZrw = null;
                    if (StringUtils.isNotEmpty(task.getId())) {
                        //如果是任务详情页面
                        taskVoListZrw = this.getTaskAccountZrw(task);
                    }
                    task1.setTaskVoListZrw(taskVoListZrw);
                }
            }
            return taskVoList;
        }

// 提取公共方法处理用户名查询
    private void processUserNames(String userName, List<String> targetList) {
        List<Map<String, Object>> userMaps = excelImportMapper.getUserInfoSByUserName(userName);

        // 使用 Spring CollectionUtils.isEmpty() 并取反
        if (userMaps != null && !CollectionUtils.isEmpty(userMaps)) {
            // 转换userId并添加到结果列表（处理null值）
            userMaps.forEach(user -> {
                Object userId = user.get("userId");
                targetList.add(userId != null ? userId.toString() : "null");
            });
        } else {
            // 未找到用户时添加特殊标记
            targetList.add("noUserId");
        }
    }
        /**
         * 初始化任务指标完成数据
         * @param task 任务对象
         */
        private void initTaskCompletionData(TaskVo task) {
            task.setReportCompleteNum(0);
            task.setReportCompleteRate("0.00%");
            task.setReportCompleteProportion("0.00%");
            task.setVisitCompleteNum(0);
            task.setVisitCompleteRate("0.00%");
            task.setVisitCompleteProportion("0.00%");
            task.setArriveCompleteNum(0);
            task.setThreeOneCompleteNum(0);
            task.setThreeOneCompleteRate("0.00%");
            task.setThreeOneCompleteProportion("0.00%");

            task.setDealCompleteNum(0);
            task.setDealCompleteRate("0.00%");
            task.setDealCompleteProportion("0.00%");
            task.setFirstVisitCompleteNum(0);
            task.setFirstVisitCompleteRate("0.00%");
            task.setFirstVisitCompleteProportion("0.00%");
            task.setRepeatVisitCompleteNum(0);
            task.setRepeatVisitCompleteRate("0.00%");
            task.setRepeatVisitCompleteProportion("0.00%");
            task.setTagCompleteNum(0);
            task.setTagCompleteRate("0.00%");
            task.setTagCompleteProportion("0.00%");
        }

        /**
         * 处理任务的指标完成统计
         * @param task 任务对象
         * @param taskMembers 任务成员列表
         * @param taskCustomers 任务客户列表
         */
        private void processTaskCompletionStats(TaskVo task, List<TaskMember> taskMembers, List<TaskCustomer> taskCustomers, Map<String, Integer> projectSummary) {
            if (CollectionUtils.isEmpty(taskMembers)) {
                return;
            }

            // 从成员统计指标完成情况
            task.setReportCompleteNum(taskMembers.stream().filter(a -> a.getReportCount() != null).mapToInt(a -> a.getReportCount()).sum());
            if (task.getReportNum()==null || task.getReportNum() == 0) {
                task.setReportCompleteRate("0.00%");
            } else {
                task.setReportCompleteRate(df.format(Double.valueOf(task.getReportCompleteNum()) / Double.valueOf(task.getReportNum()) * 100) + "%");
            }

            task.setType("report");
            // 使用项目汇总数据计算占比，而不是查询数据库
            Integer reportProportion = projectSummary != null ? projectSummary.get("reportNum") : 0;
            if (reportProportion == 0) {
                task.setReportCompleteProportion("0.00%");
            } else {
                task.setReportCompleteProportion(df.format(Double.valueOf(task.getReportCompleteNum()) / Double.valueOf(reportProportion) * 100) + "%");
            }

            task.setVisitCompleteNum(taskMembers.stream().filter(a -> a.getVisitCount() != null).mapToInt(a -> a.getVisitCount()).sum());
            if (task.getVisitNum()==null || task.getVisitNum() == 0) {
                task.setVisitCompleteRate("0.00%");
            } else {
                task.setVisitCompleteRate(df.format(Double.valueOf(task.getVisitCompleteNum()) / Double.valueOf(task.getVisitNum()) * 100) + "%");
            }

            task.setType("visit");
            // 使用项目汇总数据计算占比，而不是查询数据库
            Integer visitProportion = projectSummary != null ? projectSummary.get("visitNum") : 0;
            if (visitProportion == 0) {
                task.setVisitCompleteProportion("0.00%");
            } else {
                task.setVisitCompleteProportion(df.format(Double.valueOf(task.getVisitCompleteNum()) / Double.valueOf(visitProportion) * 100) + "%");
            }

            task.setArriveCompleteNum(taskMembers.stream().filter(a -> a.getArriveCount() != null).mapToInt(a -> a.getArriveCount()).sum());
            task.setThreeOneCompleteNum(taskMembers.stream().filter(a -> a.getThreeOneCount() != null).mapToInt(a -> a.getThreeOneCount()).sum());
            if (task.getThreeOneNum()  == null ||task.getThreeOneNum() == 0) {
                task.setThreeOneCompleteRate("0.00%");
            } else {
                task.setThreeOneCompleteRate(df.format(Double.valueOf(task.getThreeOneCompleteNum()) / Double.valueOf(task.getThreeOneNum()) * 100) + "%");
            }

            task.setType("threeOne");
            // 使用项目汇总数据计算占比，而不是查询数据库
            Integer threeOneProportion = projectSummary != null ? projectSummary.get("threeOneNum") : 0;
            if (threeOneProportion == 0) {
                task.setThreeOneCompleteProportion("0.00%");
            } else {
                task.setThreeOneCompleteProportion(df.format(Double.valueOf(task.getThreeOneCompleteNum()) / Double.valueOf(threeOneProportion) * 100) + "%");
            }

            //成交数据
            task.setDealCompleteNum(taskMembers.stream().filter(a -> a.getDealCount() != null).mapToInt(a -> a.getDealCount()).sum());
            if (task.getDealNum()==null||task.getDealNum() == 0) {
                task.setDealCompleteRate("0.00%");
            } else {
                task.setDealCompleteRate(df.format(Double.valueOf(task.getDealCompleteNum()) / Double.valueOf(task.getDealNum()) * 100) + "%");
            }

            task.setType("deal");
            // 使用项目汇总数据计算占比，而不是查询数据库
            Integer DealProportion = projectSummary != null ? projectSummary.get("dealNum") : 0;
            if (DealProportion == 0) {
                task.setDealCompleteProportion("0.00%");
            } else {
                task.setDealCompleteProportion(df.format(Double.valueOf(task.getDealCompleteNum()) / Double.valueOf(DealProportion) * 100) + "%");
            }

            //首访数据
            task.setFirstVisitCompleteNum(taskMembers.stream().filter(a -> a.getFirstVisitCount() != null).mapToInt(a -> a.getFirstVisitCount()).sum());
            if (task.getFirstVisitNum()==null || task.getFirstVisitNum() == 0) {
                task.setFirstVisitCompleteRate("0.00%");
            } else {
                task.setFirstVisitCompleteRate(df.format(Double.valueOf(task.getFirstVisitCompleteNum()) / Double.valueOf(task.getFirstVisitNum()) * 100) + "%");
            }

            task.setType("firstVisit");
            // 使用项目汇总数据计算占比，而不是查询数据库
            Integer FirstVisitProportion = projectSummary != null ? projectSummary.get("firstVisitNum") : 0;
            if (FirstVisitProportion == 0) {
                task.setFirstVisitCompleteProportion("0.00%");
            } else {
                task.setFirstVisitCompleteProportion(df.format(Double.valueOf(task.getFirstVisitCompleteNum()) / Double.valueOf(FirstVisitProportion) * 100) + "%");
            }

            //复访数据
            task.setRepeatVisitCompleteNum(taskMembers.stream().filter(a -> a.getRepeatVisitCount() != null).mapToInt(a -> a.getRepeatVisitCount()).sum());
            if (task.getRepeatVisitNum()==null || task.getRepeatVisitNum() == 0) {
                task.setRepeatVisitCompleteRate("0.00%");
            } else {
                task.setRepeatVisitCompleteRate(df.format(Double.valueOf(task.getRepeatVisitCompleteNum()) / Double.valueOf(task.getRepeatVisitNum()) * 100) + "%");
            }

            task.setType("repeatVisit");
            // 使用项目汇总数据计算占比，而不是查询数据库
            Integer RepeatVisitProportion = projectSummary != null ? projectSummary.get("repeatVisitNum") : 0;
            if (RepeatVisitProportion == 0) {
                task.setRepeatVisitCompleteProportion("0.00%");
            } else {
                task.setRepeatVisitCompleteProportion(df.format(Double.valueOf(task.getRepeatVisitCompleteNum()) / Double.valueOf(RepeatVisitProportion) * 100) + "%");
            }

            //标记数据
            task.setTagCompleteNum(taskMembers.stream().filter(a -> a.getTagCount() != null).mapToInt(a -> a.getTagCount()).sum());
            if (task.getTagNum()==null || task.getTagNum() == 0) {
                task.setTagCompleteRate("0.00%");
            } else {
                task.setTagCompleteRate(df.format(Double.valueOf(task.getTagCompleteNum()) / Double.valueOf(task.getTagNum()) * 100) + "%");
            }

            task.setType("tag");
            // 使用项目汇总数据计算占比，而不是查询数据库
            Integer TagProportion = projectSummary != null ? projectSummary.get("tagNum") : 0;
            if (TagProportion == 0) {
                task.setTagCompleteProportion("0.00%");
            } else {
                task.setTagCompleteProportion(df.format(Double.valueOf(task.getTagCompleteNum()) / Double.valueOf(TagProportion) * 100) + "%");
            }
        }

        /**
         * 计算父任务的指标完成情况（从子任务汇总）
         * @param parentTask 父任务
         * @param subTasks 子任务列表
         * @param projectSummary 项目指标汇总
         */
        private void calculateParentTaskStats(TaskVo parentTask, List<TaskVo> subTasks, Map<String, Integer> projectSummary) {
            // 汇总子任务的指标完成数
            if (CollectionUtils.isEmpty(subTasks)) {
                return;
            }

            // 汇总子任务的指标完成数，将 null 视为 0
            int reportCompleteNum = subTasks.stream()
                    .filter(Objects::nonNull) // 过滤 null 元素
                    .mapToInt(task -> Optional.ofNullable(task.getReportCompleteNum()).orElse(0))
                    .sum();

            int visitCompleteNum = subTasks.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(task -> Optional.ofNullable(task.getVisitCompleteNum()).orElse(0))
                    .sum();

            int arriveCompleteNum = subTasks.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(task -> Optional.ofNullable(task.getArriveCompleteNum()).orElse(0))
                    .sum();

            int threeOneCompleteNum = subTasks.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(task -> Optional.ofNullable(task.getThreeOneCompleteNum()).orElse(0))
                    .sum();

            int dealCompleteNum = subTasks.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(task -> Optional.ofNullable(task.getDealCompleteNum()).orElse(0))
                    .sum();

            int firstVisitCompleteNum = subTasks.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(task -> Optional.ofNullable(task.getFirstVisitCompleteNum()).orElse(0))
                    .sum();

            int repeatVisitCompleteNum = subTasks.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(task -> Optional.ofNullable(task.getRepeatVisitCompleteNum()).orElse(0))
                    .sum();

            int tagCompleteNum = subTasks.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(task -> Optional.ofNullable(task.getTagCompleteNum()).orElse(0))
                    .sum();

            // 设置父任务的指标完成数
            parentTask.setReportCompleteNum(reportCompleteNum);
            parentTask.setVisitCompleteNum(visitCompleteNum);
            parentTask.setArriveCompleteNum(arriveCompleteNum);
            parentTask.setThreeOneCompleteNum(threeOneCompleteNum);
            parentTask.setDealCompleteNum(dealCompleteNum);
            parentTask.setFirstVisitCompleteNum(firstVisitCompleteNum);
            parentTask.setRepeatVisitCompleteNum(repeatVisitCompleteNum);
            parentTask.setTagCompleteNum(tagCompleteNum);

            // 计算完成率
            if (parentTask.getReportNum() > 0) {
                parentTask.setReportCompleteRate(df.format(Double.valueOf(reportCompleteNum) / Double.valueOf(parentTask.getReportNum()) * 100) + "%");
            }

            if (parentTask.getVisitNum() > 0) {
                parentTask.setVisitCompleteRate(df.format(Double.valueOf(visitCompleteNum) / Double.valueOf(parentTask.getVisitNum()) * 100) + "%");
            }

            if (parentTask.getThreeOneNum() > 0) {
                parentTask.setThreeOneCompleteRate(df.format(Double.valueOf(threeOneCompleteNum) / Double.valueOf(parentTask.getThreeOneNum()) * 100) + "%");
            }

            if (parentTask.getDealNum() > 0) {
                parentTask.setDealCompleteRate(df.format(Double.valueOf(dealCompleteNum) / Double.valueOf(parentTask.getDealNum()) * 100) + "%");
            }

            if (parentTask.getFirstVisitNum() > 0) {
                parentTask.setFirstVisitCompleteRate(df.format(Double.valueOf(firstVisitCompleteNum) / Double.valueOf(parentTask.getFirstVisitNum()) * 100) + "%");
            }

            if (parentTask.getRepeatVisitNum() > 0) {
                parentTask.setRepeatVisitCompleteRate(df.format(Double.valueOf(repeatVisitCompleteNum) / Double.valueOf(parentTask.getRepeatVisitNum()) * 100) + "%");
            }

            if (parentTask.getTagNum() > 0) {
                parentTask.setTagCompleteRate(df.format(Double.valueOf(tagCompleteNum) / Double.valueOf(parentTask.getTagNum()) * 100) + "%");
            }

            // 计算占比（使用项目汇总数据，而不是查询数据库）
            if (projectSummary != null) {
                Integer reportProportion = projectSummary.get("reportNum");
                if (reportProportion != null && reportProportion > 0) {
                    parentTask.setReportCompleteProportion(df.format(Double.valueOf(reportCompleteNum) / Double.valueOf(reportProportion) * 100) + "%");
                }

                Integer visitProportion = projectSummary.get("visitNum");
                if (visitProportion != null && visitProportion > 0) {
                    parentTask.setVisitCompleteProportion(df.format(Double.valueOf(visitCompleteNum) / Double.valueOf(visitProportion) * 100) + "%");
                }

                Integer threeOneProportion = projectSummary.get("threeOneNum");
                if (threeOneProportion != null && threeOneProportion > 0) {
                    parentTask.setThreeOneCompleteProportion(df.format(Double.valueOf(threeOneCompleteNum) / Double.valueOf(threeOneProportion) * 100) + "%");
                }

                Integer dealProportion = projectSummary.get("dealNum");
                if (dealProportion != null && dealProportion > 0) {
                    parentTask.setDealCompleteProportion(df.format(Double.valueOf(dealCompleteNum) / Double.valueOf(dealProportion) * 100) + "%");
                }

                Integer firstVisitProportion = projectSummary.get("firstVisitNum");
                if (firstVisitProportion != null && firstVisitProportion > 0) {
                    parentTask.setFirstVisitCompleteProportion(df.format(Double.valueOf(firstVisitCompleteNum) / Double.valueOf(firstVisitProportion) * 100) + "%");
                }

                Integer repeatVisitProportion = projectSummary.get("repeatVisitNum");
                if (repeatVisitProportion != null && repeatVisitProportion > 0) {
                    parentTask.setRepeatVisitCompleteProportion(df.format(Double.valueOf(repeatVisitCompleteNum) / Double.valueOf(repeatVisitProportion) * 100) + "%");
                }

                Integer tagProportion = projectSummary.get("tagNum");
                if (tagProportion != null && tagProportion > 0) {
                    parentTask.setTagCompleteProportion(df.format(Double.valueOf(tagCompleteNum) / Double.valueOf(tagProportion) * 100) + "%");
                }
            }
        }


    private List<TaskVo> getTaskAccountZrw(Task task) {
        List<TaskVo>  taskVoList=  null;
        return taskVoList;
    }

//    任务管理台账导出
    @Override
    public void taskAccountExport(HttpServletRequest request, HttpServletResponse response, Task task) throws ParseException {

        List<Map> fileds = task.getFileds();
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }
        String userId = request.getHeader("userid");
        if (org.apache.commons.lang3.StringUtils.isEmpty(userId)) {
            userId = SecurityUtils.getUserId();
        }
        //导出的文档下面的名字
        String excelName = "任务管理台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        if(CollectionUtils.isEmpty(task.getProjectList())){
            List<String> proList = new ArrayList<>();
            proList.add("");
            task.setProjectList(proList);
        }
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(userId, task.getProjectList());
        task.setOrgIds(orgIds);
        
        // 设置创建人
        task.setCreateBy(userId);
        
        // 处理创建人和执行人用户名
        List<String> userIdsCreate = new ArrayList<>();
        List<String> userIdsMember = new ArrayList<>();
        // 获取创建人ID
        if (StringUtils.isNotBlank(task.getCreateUserName())) {
            processUserNames(task.getCreateUserName(), userIdsCreate);
        }
        // 获取执行人ID
        if (StringUtils.isNotBlank(task.getMemberUserName())) {
            processUserNames(task.getMemberUserName(), userIdsMember);
        }
        
        // 获取用户权限范围内的所有用户ID
        Map paramMap = new HashMap();
        paramMap.put("UserId", userId);
        List<Map> userList = projectMapper.findFullPathAllInsZs(paramMap);
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        if (!CollectionUtils.isEmpty(userList)) {
            for (int i = 0; i < userList.size(); i++) {
                String jobCode = String.valueOf(userList.get(i).get("JobCode"));
                if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)
                        || "yxjl".equals(jobCode) || "qyyxjl".equals(jobCode)
                        || "zszj".equals(jobCode) || "qyzszj".equals(jobCode) || "jtsjg".equals(jobCode)
                        || "xmz".equals(jobCode) || "qyfz".equals(jobCode) || "qyz".equals(jobCode) || "10001".equals(jobCode)) {
                    String orgPath = String.valueOf(userList.get(i).get("FullPath"));
                    List<String> userIds1 = custMapDao.getTeamUserNew(orgPath);
                    userIds.addAll(userIds1);
                }
            }
        }
        
        // 设置创建人ID列表
        List<String> userIdsC = new ArrayList<>();
        if (StringUtils.isNotBlank(task.getCreateUserName())) {
            userIdsC = userIds.stream().filter(uid -> userIdsCreate.contains(uid)).collect(Collectors.toList());
            userIdsC = userIdsC.isEmpty() ? Collections.singletonList("noUserId") : userIdsC;
        } else {
            userIdsC.addAll(userIds);
        }
        
        // 设置执行人ID列表
        List<String> userIdsM = new ArrayList<>();
        if (StringUtils.isNotBlank(task.getMemberUserName())) {
            userIdsM = userIds.stream().filter(uid -> userIdsMember.contains(uid)).collect(Collectors.toList());
            userIdsM = userIdsM.isEmpty() ? Collections.singletonList("noUserId") : userIdsM;
        } else {
            userIdsM.addAll(userIds);
        }
        
        userIdsCreate.clear();
        userIdsCreate.addAll(userIdsC);
        userIdsMember.clear();
        userIdsMember.addAll(userIdsM);
        task.setUserIdsCreate(userIdsCreate);
        task.setUserIdsMember(userIdsMember);
        
        // 获取任务数据，与查询方法保持一致
        List<TaskVo>  taskVoList = projectCluesDao.getTaskAccount(task);
        
        if (!CollectionUtils.isEmpty(taskVoList)) {
            // 1. 查这些任务的所有子任务
            List<String> taskIds = taskVoList.stream().map(TaskVo::getId).collect(Collectors.toList());
            String parentIdsStr = taskIds.stream().map(id -> "'" + id + "'").collect(Collectors.joining(","));
            List<TaskVo> childTasks = projectCluesDao.getChildTasksByParentIds(parentIdsStr);
            Map<String, List<TaskVo>> childTaskMap = childTasks.stream().collect(Collectors.groupingBy(TaskVo::getParentId));
            
            // 2. 查所有任务（本页+子任务）的成员、客户
            List<String> allTaskIds = new ArrayList<>(taskIds);
            allTaskIds.addAll(childTasks.stream().map(TaskVo::getId).collect(Collectors.toList()));
            String allTaskIdsStr = allTaskIds.stream().collect(Collectors.joining("','", "'", "'"));
            List<TaskMember> allMembers = projectCluesDao.getUserByTaskId(allTaskIdsStr);
            Map<String, List<TaskMember>> memberMap = allMembers.stream().collect(Collectors.groupingBy(TaskMember::getTaskId));
            List<TaskCustomer> allCustomers = projectCluesDao.getCustomerByTaskId(allTaskIdsStr);
            Map<String, List<TaskCustomer>> customerMap = allCustomers.stream().collect(Collectors.groupingBy(TaskCustomer::getTaskId));
            
            // 3. 统计子任务的指标
            for (TaskVo child : childTasks) {
                processTaskCompletionStats(child, memberMap.get(child.getId()), customerMap.get(child.getId()), null);
            }
            
                    // 4. 统计本页任务
        // 先计算项目下所有任务的指标汇总（用于计算CompleteProportion）
        Map<String, Integer> projectSummary = calculateProjectTaskSummary(task.getProjectList().get(0), taskVoList);
        
        for (TaskVo task1 : taskVoList) {
            initTaskCompletionData(task1);
            task1.setTaskStatusDesc(TaskStatusEnum.getNameByType(task1.getTaskStatus()));
            task1.setMemberList(memberMap.get(task1.getId()));
            
            // 设置memberName（取memberList中第一个成员的姓名）
            List<TaskMember> members = memberMap.get(task1.getId());
            if (!CollectionUtils.isEmpty(members) && StringUtils.isNotBlank(members.get(0).getMemberName())) {
                task1.setMemberName(members.get(0).getMemberName());
            }
            
            // 处理父任务的子任务信息
            List<TaskVo> subTasks = childTaskMap.get(task1.getId());
            if (StringUtils.isBlank(task1.getParentId())) {
                // 父任务或单独任务
                if (!CollectionUtils.isEmpty(subTasks)) {
                    task1.setSubTaskCount(subTasks.size());
                    task1.setSubTaskList(subTasks);
                    calculateParentTaskStats(task1, subTasks, projectSummary);
                } else {
                    task1.setSubTaskCount(0);
                    // 单独任务，统计自己
                    processTaskCompletionStats(task1, memberMap.get(task1.getId()), customerMap.get(task1.getId()), projectSummary);
                }
                task1.setCustomerList(customerMap.get(task1.getId()));
            } else {
                // 子任务，统计自己
                processTaskCompletionStats(task1, memberMap.get(task1.getId()), customerMap.get(task1.getId()), projectSummary);
                task1.setCustomerList(customerMap.get(task1.getId()));
            }
            
            List<TaskVo> taskVoListZrw = null;
            if (StringUtils.isNotEmpty(task.getId())) {
                // 如果是任务详情页面
                taskVoListZrw = this.getTaskAccountZrw(task);
            }
            task1.setTaskVoListZrw(taskVoListZrw);
        }
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(task.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(task.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName") + "");
            excelExportLog.setProjectId(proMap.get("projectId") + "");
            excelExportLog.setProjectName(proMap.get("projectName") + "");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(task));
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (taskVoList != null && taskVoList.size() > 0){
            String isAllStr = task.getIsAll();
//            headers = taskVoList.get(0).courtCaseTitle1;
            headers = filedNames.toArray(new String[0]);

            int rowNum = 1;
            for (TaskVo model : taskVoList) {
                model.setRownum(rowNum + "");
                Object[] oArray = model.toData1(filedCodes, model.getTaskTypeId());
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String taskAccountExportNew(HttpServletRequest request, HttpServletResponse response, Task task) throws ParseException {
        String userId = SecurityUtils.getUserId();
        //导出的文档下面的名字
        String excelName = "任务管理台账";
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("TA1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType(task.getIsAll());//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(task.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(task.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName") + "");
            excelExportLog.setProjectId(proMap.get("projectId") + "");
            excelExportLog.setProjectName(proMap.get("projectName") + "");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(task));
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

    //    报备失败台账
    @Override
    public ResultBody getReportFailAccount(ReportFail reportFail) {
        if (reportFail.getSearch() != null && !"".equals(reportFail.getSearch())) {
            String search = reportFail.getSearch();
//            判断是手机号还是人名
            if (search.matches("[0-9]+")) {
                reportFail.setCustomerMobile(search);
            } else {
                reportFail.setCustomerName(search);
            }
        }
        int pageNum = 1;
        int pageSize = 10;
        if (reportFail.getPageNum() != null && !"".equals(reportFail.getPageNum())) {
            pageNum = Integer.parseInt(reportFail.getPageNum());
        }
        if (reportFail.getPageSize() != null && !"".equals(reportFail.getPageSize())) {
            pageSize = Integer.parseInt(reportFail.getPageSize());
        }
        //判断当前登录人是否有管理员权限
        //reportFail.setType(projectCluesDao.getUserJobHsxt(SecurityUtils.getUserId()));
        reportFail.setType(0);
        if(CollectionUtils.isEmpty(reportFail.getProjectList())){
            List<String> proList = new ArrayList<>();
            proList.add("");
            reportFail.setProjectList(proList);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<ReportFailVo> taskAccount = projectCluesDao.getReportFailAccount(reportFail);
        return ResultBody.success(new PageInfo<>(taskAccount));
    }

//    报备失败台账导出
    @Override
    public void reportFailAccountExport(HttpServletRequest request, HttpServletResponse response, ReportFail reportFail) {

        List<Map> fileds = reportFail.getFileds();
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }
        String userId = request.getHeader("userId");
        List<ReportFailVo> reportFailVoList = new ArrayList<>();
        if (reportFail.getSearch() != null && !"".equals(reportFail.getSearch())) {
            String search = reportFail.getSearch();
//            判断是手机号还是人名
            if (search.matches("[0-9]+")) {
                reportFail.setCustomerMobile(search);
            } else {
                reportFail.setCustomerName(search);
            }
        }
        //导出的文档下面的名字
        String excelName = "报备失败台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        reportFailVoList = projectCluesDao.getReportFailAccount(reportFail);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(reportFail.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(reportFail.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName") + "");
            excelExportLog.setProjectId(proMap.get("projectId") + "");
            excelExportLog.setProjectName(proMap.get("projectName") + "");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(reportFail));
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (reportFailVoList != null && reportFailVoList.size() > 0){
            String isAllStr = reportFail.getIsAll();
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
//            headers = reportFailVoList.get(0).courtCaseTitle1;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (ReportFailVo model : reportFailVoList) {
                model.setRownum(rowNum + "");
                Object[] oArray = model.toData1(isAll,filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String reportFailAccountExportNew(HttpServletRequest request, HttpServletResponse response, ReportFail reportFail) {
        String userId = SecurityUtils.getUserId();
        //导出的文档下面的名字
        String excelName = "报备失败台账";
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("RF1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType(reportFail.getIsAll());//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(reportFail.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(reportFail.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName") + "");
            excelExportLog.setProjectId(proMap.get("projectId") + "");
            excelExportLog.setProjectName(proMap.get("projectName") + "");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(reportFail));
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

    @Override
    public List<Map> toMoveRecord(Map map) {
        return projectCluesDao.toMoveRecord(map);
    }

    @Override
    public Map firstInterviewQuestionnaire(Map map) {
        if (map.get("type") != null && "1".equals(map.get("type") + "") && map.get("projectClueId") != null) {
            String OpportunityClueId = projectCluesDao.getOppIdBYCluesId(map.get("projectClueId") + "");
            map.put("opportunityClueId", OpportunityClueId);
        }
        return projectCluesDao.firstInterviewQuestionnaire(map);
    }

    @Override
    public Map currProjectInfoSelect(String projectId) {
        return projectCluesDao.currProjectInfoSelect(projectId);
    }

    @Override
    public Map SelectClueConditionClue(Map map) {
        Map resultMap = new HashMap();
        List<String> projectClueIdList = null;
        if (map.get("clueSelects") != null) {
            projectClueIdList = Arrays.asList(String.valueOf(map.get("clueSelects")).split(","));
        }
        map.put("projectClueIdList", projectClueIdList);
        List<String> projectClueIdLists = projectCluesDao.SelectClueConditionClue(map);
        int countOne = 0;
        if (projectClueIdLists.size() > 0) {
            countOne = projectClueIdList.size() - projectClueIdLists.size();
            resultMap.put("projectClueIds",StringUtils.join(projectClueIdLists, ","));
        }else{
            countOne = projectClueIdList.size();
            resultMap.put("projectClueIds",null);
        }
        resultMap.put("countOne", countOne);
        return resultMap;
    }

    @Override
    public Map SelectClueConditionAC(Map map) {
        Map resultMap = new HashMap();
        List<String> projectClueIdList = null;
        if (map.get("clueSelects") != null) {
            projectClueIdList = Arrays.asList(String.valueOf(map.get("clueSelects")).split(","));
        }
        map.put("opportunityClueIdList", projectClueIdList);
        List<String> oppClueIdList = projectCluesDao.SelectClueConditionAC(map);
        int countOne = 0;
        if (oppClueIdList.size() > 0) {
            countOne = projectClueIdList.size() - oppClueIdList.size();
            resultMap.put("oppClueIds",StringUtils.join(oppClueIdList, ","));
        }else{
            countOne = projectClueIdList.size();
            resultMap.put("oppClueIds",null);
        }
        resultMap.put("countOne", countOne);
        return resultMap;
    }

    @Override
    public List<Map> transactionInformation(Map map) {
//        List<Map> list = projectCluesDao.transactionInformation(map);

        List<Map> list = projectCluesDao.getDealRecordGroup(map.get("projectClueId")+"");
        return list;
    }

    @Override
    public String channelExportNew(String param) {
        ExcelForm excelForm = JSONObject.parseObject(param, ExcelForm.class);
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        String numbers = excelForm.getNumbers() + "";
        String AcOrQd = excelForm.getAcOrQd() + "";
        try {
            String projectId = excelForm.getProjectId();
            List<String> projectList = excelForm.getProjectList();
            List<String> proIdList = new ArrayList<>();
            if (projectList != null && projectList.size() > 0) {
                proIdList.addAll(projectList);
            } else {
                proIdList.add(projectId);
            }
            StringBuffer sbParam = new StringBuffer();
            if (!StringUtils.isBlank(excelForm.getClueStatus())) {
                String[] clues = excelForm.getClueStatus().split(",");
                String ClueStatus = "'" + StringUtils.join(clues, "','") + "'";
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" and bpc.ClueStatus in (" + ClueStatus + ")");
                } else {
                    sbParam.append(" and bp.ClueStatus in (" + ClueStatus + ")");
                }

            }
            if (!StringUtils.isBlank(excelForm.getLevel())) {
                String[] clues = excelForm.getLevel().split(",");
                String levelList = "'" + StringUtils.join(clues, "','") + "'";
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" and bpc.`Level` in (" + levelList + ")");
                } else {
                    sbParam.append(" and bp.TradeLevel in (" + levelList + ")");
                }
            }
            if (!StringUtils.isBlank(excelForm.getSourceType())) {
                String[] clues = excelForm.getSourceType().split(",");
                String sourceTypeList = "'" + StringUtils.join(clues, "','") + "'";
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" and bpc.SourceTypeOld in (" + sourceTypeList + ")");
                } else {
                    sbParam.append(" and bp.SourceTypeOld in (" + sourceTypeList + ")");
                }
            }
            if (excelForm.getClueValidityList()!=null && excelForm.getClueValidityList().size()>0) {
                String sourceTypeList = "'" + StringUtils.join(excelForm.getClueValidityList(), "','") + "'";
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" and coe.clue_validity in (" + sourceTypeList + ")");
                } else {

                }
            }
            if (!StringUtils.isBlank(excelForm.getIsRepurchase())) {
                String[] clues = excelForm.getIsRepurchase().split(",");
                String isRepurchaseList = "'" + StringUtils.join(clues, "','") + "'";
                if ("channel".equals(AcOrQd)) {

                } else {
                    sbParam.append(" and bp.IsRepurchase in (" + isRepurchaseList + ")");
                }
            }
            if (!StringUtils.isBlank(excelForm.getMainMediaStr())) {
                String[] clues = excelForm.getMainMediaStr().split(",");
                String mainMediaList = "'" + StringUtils.join(clues, "','") + "'";
                if ("channel".equals(AcOrQd)) {

                } else {
                    sbParam.append(" and bp.MainMediaGUID in (" + mainMediaList + ")");
                }
            }
            if (!StringUtils.isBlank(excelForm.getBrokerStr())) {
                String[] clues = excelForm.getBrokerStr().split(",");
                String brokerList = "'" + StringUtils.join(clues, "','") + "'";
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" and bpc.ReportUserRole in (" + brokerList + ")");
                }
            }
            if (!StringUtils.isBlank(excelForm.getRelatedStr())) {
                String[] clues = excelForm.getRelatedStr().split(",");
                String relatedList = "'" + StringUtils.join(clues, "','") + "'";
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" and bpc.IsRepurchase in (" + relatedList + ")");
                }
            }
            if (!StringUtils.isBlank(excelForm.getIsReportStr())) {
                String[] clues = excelForm.getIsReportStr().split(",");
                String isReportList = "'" + StringUtils.join(clues, "','") + "'";
                sbParam.append(" and coe.ReportSource in (" + isReportList + ")");
            }
            if (!StringUtils.isBlank(excelForm.getTradeLevel())) {
                String[] clues = excelForm.getTradeLevel().split(",");
                String tradeLevelList = "'" + StringUtils.join(clues, "','") + "'";
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" and bpc.TradeLevel in (" + tradeLevelList + ")");
                }
            }
            if (!StringUtils.isBlank(excelForm.getCustomerName())) {
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" AND bpc.CustomerName LIKE '%" + excelForm.getCustomerName() + "%'");
                } else {
                    sbParam.append(" AND bp.CustomerName LIKE '%" + excelForm.getCustomerName() + "%'");
                }

            }
            if (!StringUtils.isBlank(excelForm.getCustomerMobile())) {
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" AND bpc.CustomerMobile LIKE '%" + excelForm.getCustomerMobile() + "%'");
                } else {
                    sbParam.append(" AND bp.CustomerMobile LIKE '%" + excelForm.getCustomerMobile() + "%'");
                }

            }
            if (!StringUtils.isBlank(excelForm.getReportUserName())) {
                if ("channel".equals(AcOrQd)) {
                    sbParam.append(" AND (bpc.ReportUserName LIKE '%" + excelForm.getReportUserName() + "%' or bpc.PerformanceAttributor like '%" + excelForm.getReportUserName() + "%')");
                } else {
                    sbParam.append(" AND bp.ReportUserName LIKE (" + excelForm.getReportUserName() + ")");
                }

            }
            if (!StringUtils.isBlank(excelForm.getSalesAttributionName())) {
                if ("channel".equals(AcOrQd)) {
                } else {
                    sbParam.append(" AND bp.SalesAttributionName LIKE '%" + excelForm.getSalesAttributionName() + "%'");
                }

            }

            if (!StringUtils.isBlank(excelForm.getIsSecondBroker())) {
                String isSecondBroker = excelForm.getIsSecondBroker();
                if (isSecondBroker == "1") {
                    sbParam.append(" and ap.brokerId is not null ");
                } else if (isSecondBroker == "0") {
                    sbParam.append(" and ap.brokerId is null ");
                }
            }
            if (excelForm.getDate1() != null && !"".equals(excelForm.getDate1())
                    && null != excelForm.getDate2() && !"".equals(excelForm.getDate2())) {

                if ("1".equals(excelForm.getReportTime())) {
                    if ("channel".equals(AcOrQd)) {
                        sbParam.append(" and bpc.ReportCreateTime BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    } else {
                        sbParam.append(" and bp.TheFirstVisitDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    }
                } else if ("2".equals(excelForm.getReportTime())) {
                    if ("channel".equals(AcOrQd)) {
                        sbParam.append(" and bpc.ReportExpireDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    } else {
                        sbParam.append(" and bp.SalesTheLatestFollowDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    }
                } else if ("3".equals(excelForm.getReportTime())) {
                    if ("channel".equals(AcOrQd)) {
                        sbParam.append(" and bpc.ExpectedVisitDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    } else {
                        sbParam.append(" and bp.SalesVisitExpireDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    }
                } else if ("4".equals(excelForm.getReportTime())) {
                    if ("channel".equals(AcOrQd)) {
                        sbParam.append(" and bpc.TokerVisitExpireDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    } else {
                        sbParam.append(" and bp.ReportCreateTime BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    }
                } else if ("5".equals(excelForm.getReportTime())) {
                    if ("channel".equals(AcOrQd)) {
                        sbParam.append(" and bpc.TheFirstVisitDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    } else {
                        sbParam.append(" and ot.SubscribingDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    }
                } else if ("6".equals(excelForm.getReportTime())) {
                    if ("channel".equals(AcOrQd)) {
                        sbParam.append(" and bpc.LastRefreshReportExpireDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    } else {
                        sbParam.append(" and ot.ContractDate BETWEEN '" + excelForm.getDate1() + "' and '" + excelForm.getDate2() + "'");
                    }
                }
            }
            if ("channel".equals(AcOrQd)) {
                sbParam.append(" order by bpc.ReportCreateTime desc");
            } else {
                sbParam.append(" order by bp.TheFirstVisitDate desc");
            }


            String mainType = "";
            String mainTypeDesc = "";
            String subType = "";
            String subTypeDesc = "";
            if ("channel".equals(AcOrQd)) {
                mainType = "1";
                mainTypeDesc = "渠道管理";
                subType = "Q1";
                subTypeDesc = "渠道台账";
            } else {
                mainType = "2";
                mainTypeDesc = "案场管理";
                subType = "A5";
                subTypeDesc = "机会台账";
            }
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setCreator(excelForm.getUserId());
            excelExportLog.setAreaName(proMap.get("areaName") + "");
            excelExportLog.setProjectId(proMap.get("projectId") + "");
            excelExportLog.setProjectName(proMap.get("projectName") + "");
            excelExportLog.setId(id);
            excelExportLog.setMainType(mainType);
            excelExportLog.setMainTypeDesc(mainTypeDesc);
            excelExportLog.setSubType(subType);
            excelExportLog.setSubTypeDesc(subTypeDesc);
            excelExportLog.setIsAsyn("1");
            if ("1".equals(numbers) || "".equals(numbers)) {
                excelExportLog.setExportType("1");
            } else {
                excelExportLog.setExportType("2");
            }
            excelExportLog.setExportStatus("1");
            //sql拼接
            StringBuffer sbAll = new StringBuffer();
            String[] strings1 = new String[proIdList.size()];
            proIdList.toArray(strings1);
            String projectID = "'" + StringUtils.join(strings1, "','") + "'";
            if ("channel".equals(AcOrQd)) {
                sbAll.append("SELECT bpc.ProjectClueId,bpc.Label caseLabel,coe.channel_label channelLabel,bpc.CustomerName, bpc.BasicCustomerId, bpc.CustomerMobile as CustomerMobileAll, concat(left(bpc.CustomerMobile,3),'****',right(bpc.CustomerMobile,4)) CustomerMobile,(case bpc.CustomerGender when 1 then '男' when 2 then '女' else '' end) as CustomerGender, bpc.Label, bpc.CustomerCardTypeDesc as CustomerCardType, bpc.CustomerCardNum, bpc.Level, dic.DictName TradeLevel, (case bpc.SourceTypeOld when 1 then bpc.ReportTeamName else bpc.ReportUserName end ) as ReportUserName, (case bpc.ClueStatus when 1 then '报备' when 2 then '到访' when 3 then '排小卡' when 4 then '排大卡' when 5 then '订房' when 6 then '认筹' when 7 then '认购' when 8 then '签约' when 9 then '放弃' else '' end) as ClueStatus, (case bpc.SourceTypeOld when 1 then '中介成交' when 2 then '自渠成交' when 3 then '案场成交' when 4 then '全民经纪人成交' else '' end ) as SourceType, bpc.SourceTypeOldDesc as SourceTypeDesc, bpc.CustomerLevel, bpc.MainMediaName, bpc.SubMediaName, bpc.ProjectName, p.AreaName as AreaName, bpc.TokerAttributionTeamName, bpc.TokerAttributionName, DATE_FORMAT(bpc.TokerAttributionTime,'%Y-%m-%d %H:%i:%s')as TokerAttributionTime, bpc.SalesAttributionTeamName, bpc.SalesAttributionName, DATE_FORMAT(bpc.SalesAttributionTime,'%Y-%m-%d %H:%i:%s')as SalesAttributionTime, DATE_FORMAT(bpc.ReportCreateTime,'%Y-%m-%d %H:%i:%s')as ReportCreateTime, DATE_FORMAT(bpc.LastRefreshReportExpireDate,'%Y-%m-%d %H:%i:%s')as LastRefreshReportExpireDate, DATE_FORMAT(bpc.TheFirstVisitDate,'%Y-%m-%d %H:%i:%s')as TheFirstVisitDate, DATE_FORMAT(bpc.BookingDate,'%Y-%m-%d %H:%i:%s')as BookingDate, DATE_FORMAT(bpc.SubscribingDate,'%Y-%m-%d %H:%i:%s')as SubscribingDate, DATE_FORMAT(bpc.ContractDate,'%Y-%m-%d %H:%i:%s')as ContractDate, DATE_FORMAT(bpc.ExpectedVisitDate,'%Y-%m-%d %H:%i:%s')as ExpectedVisitDate, DATE_FORMAT(bpc.TokerVisitExpireDate,'%Y-%m-%d %H:%i:%s')as TokerVisitExpireDate, DATE_FORMAT(bpc.ReportExpireDate,'%Y-%m-%d %H:%i:%s')as ReportExpireDate, DATE_FORMAT((case when bpc.SalesTheLatestFollowDate is not null then bpc.SalesTheLatestFollowDate else bpc.TokerTheLatestFollowDate end),'%Y-%m-%d %H:%i:%s') as FollowUpDate, (case bpc.IsRepurchase when 1 then '是' when 0 then '否' else '' end ) as IsRepurchase, bpc.ReportUserRole, (case when coe.ChildReportSourceDesc is not null and coe.ChildReportSourceDesc !='' and coe.ChildReportSourceDesc !='null' then concat(coe.ReportSourceDesc,'-',coe.ChildReportSourceDesc) else coe.ReportSourceDesc end) ReportSourceDesc,bpc.Remarks,ifnull(ba.EmployeeName,'') AccountManager,bpc.InvalidReason invalidReason,(case when coe.clue_validity = 1 then '有效' when coe.clue_validity = 2 then '主动放弃' when coe.clue_validity = 3 then '报备逾期' when coe.clue_validity = 4 then '作废' when coe.clue_validity = 5 then '渠道逾期' else '有效' end) clueValidity,(case when ap.brokerId is null then '否' when ap.brokerId is not null then '是' else '否' end)as IsSenior, aa.activename as ActiveName  FROM b_project_clues bpc LEFT JOIN b_project p on p.id = bpc.projectId left join (select sd.DictName,sd.DictCode from s_dictionary sd where sd.PID=(select ID from s_dictionary where DictCode='s_gfyx') ) dic on dic.DictCode = bpc.TradeLevel left join b_clue_opportunity_extend coe on coe.ProjectClueId = bpc.ProjectClueId  left join a_account_performance ap on bpc.ProjectClueId = ap.ProjectClueId and ap.isdel = 0 left join a_accountactive aa on ap.activeid = aa.id and aa.isdel = 0 left join b_account ba on ap.accountid = ba.ID where bpc.projectId in (" + projectID + ")" + sbParam.toString());
            } else {
//                sbAll.append("SELECT bp.OpportunityClueId, bp.Label caseLabel,coe.channel_label channelLabel, bp.ProjectClueId, bp.CustomerID, bp.BasicCustomerId, bp.CustomerName, bp.CustomerMobile as CustomerMobileAll, concat(left(bp.CustomerMobile,3),'****',right(bp.CustomerMobile,4)) CustomerMobile, bp.Label, bp.CustomerCardTypeDesc, bp.CustomerCardNum,(case bp.CustomerGender when 1 then '男' when 2 then '女' else '' end) as CustomerGender, (case bp.ClueStatus when 1 then '报备' when 2 then '到访' when 3 then '排小卡' when 4 then '排大卡' when 5 then '订房' when 6 then '认筹' when 7 then '认购' when 8 then '签约' when 9 then '放弃' else '' end) as ClueStatus, (case bp.SourceTypeOld when 1 then '中介成交' when 2 then '自渠成交' when 3 then '案场成交' when 4 then '全民经纪人成交' else '' end ) as SourceType, bp.SourceTypeOldDesc as SourceTypeDesc, dict.DictName as Level, bp.CustomerLevel, bp.MainMediaName, bp.SubMediaName, bp.ProjectName, p.AreaName as AreaName, bp.TokerAttributionName, bp.TokerAttributionTime, bp.SalesAttributionName, bp.SalesAttributionTime, DATE_FORMAT(bp.ReportCreateTime,'%Y-%m-%d %H:%i:%s') as ReportCreateTime, DATE_FORMAT(bp.TheFirstVisitDate,'%Y-%m-%d %H:%i:%s') as TheFirstVisitDate, DATE_FORMAT(bp.SalesTheLatestFollowDate,'%Y-%m-%d %H:%i:%s') as SalesTheLatestFollowDate, DATE_FORMAT(bp.BookingDate,'%Y-%m-%d %H:%i:%s') as BookingDate, DATE_FORMAT( ot.SubscribingDate ,'%Y-%m-%d %H:%i:%s') as SubscribingDate, DATE_FORMAT( ot.ContractDate ,'%Y-%m-%d %H:%i:%s') as ContractDate, (case bp.SourceTypeOld when 1 then bp.ReportTeamName else bp.ReportUserName end ) as ReportUserName, DATE_FORMAT(bp.ExpectedVisitDate,'%Y-%m-%d %H:%i:%s')as ExpectedVisitDate, DATE_FORMAT((case when bp.SalesTheLatestFollowDate is not null then bp.SalesTheLatestFollowDate else bp.TokerTheLatestFollowDate end),'%Y-%m-%d %H:%i:%s') as FollowUpDate, bp.CustomerAddress, bi.AgeGroupDesc, bi.WorkAreaDesc, bi.BelongIndustriseDesc, bi.LifeAreaDesc, bi.FamilyStructureDesc, bi.FamilyIncomeDesc, bi.PurchasePurposeDesc, bi.HomeNumDesc, bi.CurrentHouseTypeDesc, bi.IntentionalAreaDesc, bi.IntentionalFloorDesc, bi.AcceptPriceDesc, bi.AcceptTotalPriceDesc, bi.BuyPointDesc, bi.ResistanceDesc, bi.Description, (case bi.Qualifications when '0' then '否' when '1' then '是' when '2' then '审核中' else '' end ) as Qualifications, bi.NoEligibilityReasonDesc, bi.PropertyTypeDesc, bi.EligibilitySolveDesc, bi.PurchaseFundsSourceDesc, bi.HousingNumberDesc, bi.IsTemplateRoomDesc, bi.IsAddWeChatDesc, bi.ReceptionDurationDesc, bi.DecisionMakerDesc, bi.HobbyDesc, bi.VehicleInformationDesc, bi.MajorCompetitorsDesc, bi.MinorCompetitionDesc, bi.ResidentialAreaDesc, bi.EmployerDesc, bi.WorkJobsDesc, bi.IsBuyFitUpPackageDesc, bp.DataCompleteRate, bp.DataCompleteAttachRate, (case bp.IsRepurchase when 1 then '是' when 0 then '否' else '' end ) as IsRepurchase, (case when coe.ChildReportSourceDesc is not null and coe.ChildReportSourceDesc !='' and coe.ChildReportSourceDesc !='null' then concat(coe.ReportSourceDesc,'-',coe.ChildReportSourceDesc) else coe.ReportSourceDesc end) ReportSourceDesc FROM b_project_opportunity bp left join b_information bi on bp.OpportunityClueId = bi.OpportunityClueId LEFT JOIN b_project p on p.id = bp.projectId LEFT JOIN s_dictionary dict on dict.DictCode = bp.TradeLevel and dict.PID=(select ID from s_dictionary where DictCode='s_gfyx') LEFT JOIN b_project_clues bpc ON bp.ProjectClueId = bpc.ProjectClueId left join b_clue_opportunity_extend coe on coe.ProjectClueId = bp.ProjectClueId LEFT JOIN ( SELECT OpportunityClueId, MAX(case when ClueStatus = '认购' then bot.OrderYwgsDate ELSE NULL END) SubscribingDate, MAX(case when ClueStatus = '签约' then bot.ContractYwGsDate ELSE NULL END) ContractDate FROM b_opportunity_trade bot WHERE bot.ClueStatus IN('签约','认购') AND bot.ProjectID = '" + projectId + "' AND TradeStatus = '激活' GROUP BY OpportunityClueId ) ot ON ot.OpportunityClueId = bp.OpportunityClueId WHERE bp.projectId='" + projectId + "'" + sbParam.toString());
                sbAll.append("SELECT bp.OpportunityClueId, bp.Label caseLabel,coe.channel_label channelLabel, bp.ProjectClueId, bp.CustomerID, bp.BasicCustomerId, bp.CustomerName, bp.CustomerMobile as CustomerMobileAll, concat(left(bp.CustomerMobile,3),'****',right(bp.CustomerMobile,4)) CustomerMobile, bp.Label, bp.CustomerCardTypeDesc, bp.CustomerCardNum,(case bp.CustomerGender when 1 then '男' when 2 then '女' else '' end) as CustomerGender, (case bp.ClueStatus when 1 then '报备' when 2 then '到访' when 3 then '排小卡' when 4 then '排大卡' when 5 then '订房' when 6 then '认筹' when 7 then '认购' when 8 then '签约' when 9 then '放弃' else '' end) as ClueStatus, (case bp.SourceTypeOld when 1 then '中介成交' when 2 then '自渠成交' when 3 then '案场成交' when 4 then '全民经纪人成交' else '' end ) as SourceType, bp.SourceTypeOldDesc as SourceTypeDesc, dict.DictName as Level, bp.CustomerLevel, bp.MainMediaName, bp.SubMediaName, bp.ProjectName, p.AreaName as AreaName, bp.TokerAttributionName, bp.TokerAttributionTime, bp.SalesAttributionName, bp.SalesAttributionTime,bp.SalesAttributionTeamName, DATE_FORMAT(bp.ReportCreateTime,'%Y-%m-%d %H:%i:%s') as ReportCreateTime, DATE_FORMAT(bp.TheFirstVisitDate,'%Y-%m-%d %H:%i:%s') as TheFirstVisitDate, DATE_FORMAT(bp.SalesTheLatestFollowDate,'%Y-%m-%d %H:%i:%s') as SalesTheLatestFollowDate, DATE_FORMAT(bp.BookingDate,'%Y-%m-%d %H:%i:%s') as BookingDate, DATE_FORMAT( ot.SubscribingDate ,'%Y-%m-%d %H:%i:%s') as SubscribingDate, DATE_FORMAT( ot.ContractDate ,'%Y-%m-%d %H:%i:%s') as ContractDate, (case bp.SourceTypeOld when 1 then bp.ReportTeamName else bp.ReportUserName end ) as ReportUserName, DATE_FORMAT(bp.ExpectedVisitDate,'%Y-%m-%d %H:%i:%s')as ExpectedVisitDate, DATE_FORMAT((case when bp.SalesTheLatestFollowDate is not null then bp.SalesTheLatestFollowDate else bp.TokerTheLatestFollowDate end),'%Y-%m-%d %H:%i:%s') as FollowUpDate, bp.CustomerAddress, bi.AgeGroupDesc, bi.WorkAreaDesc, bi.BelongIndustriseDesc, bi.LifeAreaDesc, bi.FamilyStructureDesc, bi.FamilyIncomeDesc, bi.PurchasePurposeDesc, bi.HomeNumDesc, bi.CurrentHouseTypeDesc, bi.IntentionalAreaDesc, bi.IntentionalFloorDesc, bi.AcceptPriceDesc, bi.AcceptTotalPriceDesc, bi.BuyPointDesc, bi.ResistanceDesc, bi.Description, (case bi.Qualifications when '0' then '否' when '1' then '是' when '2' then '审核中' else '' end ) as Qualifications, bi.NoEligibilityReasonDesc, bi.PropertyTypeDesc, bi.EligibilitySolveDesc, bi.PurchaseFundsSourceDesc, bi.HousingNumberDesc, bi.IsTemplateRoomDesc, bi.IsAddWeChatDesc, bi.ReceptionDurationDesc, bi.DecisionMakerDesc, bi.HobbyDesc, bi.VehicleInformationDesc, bi.MajorCompetitorsDesc, bi.MinorCompetitionDesc, bi.ResidentialAreaDesc, bi.EmployerDesc, bi.WorkJobsDesc, bi.IsBuyFitUpPackageDesc, bp.DataCompleteRate, bp.DataCompleteAttachRate, (case when coe.ext1 = 1 then '有效' when coe.ext1 = 2 then '主动放弃' when coe.ext1 = 3 then '跟进逾期未进入公共池' when coe.ext1 = 4 then '跟进逾期进入公共池' when coe.ext1 = 5 then '认购逾期' else '有效' end) clueValidity, (case bp.IsRepurchase when 1 then '是' when 0 then '否' else '' end ) as IsRepurchase, (case when coe.ChildReportSourceDesc is not null and coe.ChildReportSourceDesc !='' and coe.ChildReportSourceDesc !='null' then concat(coe.ReportSourceDesc,'-',coe.ChildReportSourceDesc) else coe.ReportSourceDesc end) ReportSourceDesc FROM b_project_opportunity bp left join b_information bi on bp.OpportunityClueId = bi.OpportunityClueId LEFT JOIN b_project p on p.id = bp.projectId LEFT JOIN s_dictionary dict on dict.DictCode = bp.TradeLevel and dict.PID=(select ID from s_dictionary where DictCode='s_gfyx') LEFT JOIN b_project_clues bpc ON bp.ProjectClueId = bpc.ProjectClueId left join b_clue_opportunity_extend coe on coe.ProjectClueId = bp.ProjectClueId LEFT JOIN ( SELECT OpportunityClueId, MAX(case when ClueStatus = '认购' then bot.OrderYwgsDate ELSE NULL END) SubscribingDate, MAX(case when ClueStatus = '签约' then bot.ContractYwGsDate ELSE NULL END) ContractDate FROM b_opportunity_trade bot WHERE bot.ClueStatus IN('签约','认购') AND bot.ProjectID = '" + projectId + "' AND TradeStatus = '激活' GROUP BY OpportunityClueId ) ot ON ot.OpportunityClueId = bp.OpportunityClueId WHERE bp.projectId='" + projectId + "'" + sbParam.toString());
            }
            excelExportLog.setDoSql(sbAll.toString());
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
            if (StringUtils.isEmpty(excelForm.getCompanycode())){
                redisUtil.lPush("downLoad", id);
            }else{
                //放入redis
                redisUtil.lPush("downLoad", id+","+excelForm.getCompanycode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "任务创建发生异常！";
        }
        return "您当前导出数据多于8000条，请关注右上角下载任务状态";
    }

    @Override
    public List<ProjectCluesVO> getAllChannel(ProjectCluesForm projectCluesForm) {
        if (!StringUtils.isBlank(projectCluesForm.getRedistributionId())) {
            projectCluesForm.setRedistributionIdList(Arrays.asList(projectCluesForm.getRedistributionId().split(",")));
        }
        return projectCluesDao.queryAllCustmerByOther(projectCluesForm);
    }

    @Override
    public List<ProjectCluesVO> getAllCase(ProjectCluesForm projectCluesForm) {
        if (!StringUtils.isBlank(projectCluesForm.getRedistributionId())) {
            projectCluesForm.setRedistributionIdList(Arrays.asList(projectCluesForm.getRedistributionId().split(",")));
        }
        return projectCluesDao.queryAllAnChangByOther(projectCluesForm);
    }

    @Override
    public Map getDealRecord(HttpServletRequest request,Map map) {
        String companycode = request.getHeader("companycode");
        //获取是否开启自销系统
        boolean flag = false;
        if (redisUtil.get("ISZXOPEN_"+companycode)!=null){
            if ("1".equals(redisUtil.get("ISZXOPEN_"+companycode).toString())){
                flag = true;
            }else{
                flag = false;
            }
        }else{
            //获取公司聚客汇菜单
            List<String> companyMenuList = new ArrayList<>();
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("companycode",companycode);
            String res = "";
            try {
                res = HttpRequestUtil.httpPost(appMenuUrl, JSONObject.parseObject(JSONObject.toJSONString(paramMap)), false).toJSONString();
                MenuResult menuResult = JSONObject.toJavaObject(JSON.parseObject(res), MenuResult.class);
                if (menuResult!=null && menuResult.getData()!=null){
                    companyMenuList = menuResult.getData();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if (companyMenuList!=null && companyMenuList.size()>0){
                if (companyMenuList.contains("appmenu5-10")){
                    //保存缓存是否开启自销为是
                    redisUtil.set("ISZXOPEN_"+companycode,"1",3600);
                    flag = true;
                }else{
                    flag = false;
                    redisUtil.set("ISZXOPEN_"+companycode,"2",3600);
                }
            }else{
                flag = false;
                //保存缓存是否开启自销为否
                redisUtil.set("ISZXOPEN_"+companycode,"2",3600);
            }
        }

        String projectClueId = map.get("projectClueId")+"";
        String roomID = map.get("roomID")+"";
        Map result = new HashMap();
        //获取交易基本信息
        //查询明源机会ID
        //先注释掉查询明源
        String oppguid = projectCluesDao.getIntensionId(projectClueId);
        if (!StringUtils.isEmpty(oppguid)){
            //判断是否自销系统
            if (flag){
                //查询交易信息
                Map result1 = projectCluesDao.getTradeByOppId(projectClueId,roomID);
                if (result1!=null){
                    result.put("area",result1.get("CjBldArea"));
                    result.put("cj",0);
                    result.put("cstName",result1.get("CstName"));
                    if ("认购".equals(result1.get("ClueStatus")+"")){
                        result.put("orderAmount",result1.get("HtTotal"));
                        try {
                            String orderDate = sf.format(sf.parse(result1.get("OrderDate")+""));
                            result.put("orderDate",orderDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        if (result1.get("ContractDate")!=null){
                            try {
                                String contractDate = sf.format(sf.parse(result1.get("ContractDate")+""));
                                result.put("contractDate",contractDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        result.put("contractAmount",result1.get("HtTotal"));
                        result.put("contractSum",result1.get("HtTotal"));
                    }

                }
            }else {
                //根据明源机会ID房间ID查询交易信息
                String sql = "select top 1 xx.CstNames cstName,xx.CjRoomTotal orderAmount,xx.BcTotal cj,xx.CJRMBTOTAL contractAmount,xx.BLDAREA area,(case when (bu.RoomStatus = '认购' or bu.RoomStatus = '签约') and bu.OrderYwgsDate is not null then bu.OrderYwgsDate when (bu.RoomStatus = '认购' or bu.RoomStatus = '签约') and bu.OrderYwgsDate is null then bu.OrderDate else '' end) orderDate, (case when bu.RoomStatus = '签约' and bu.ContractYwGsDate is not null then bu.ContractYwGsDate when bu.RoomStatus = '签约' and bu.ContractYwGsDate is null then bu.ContractDate else '' end) contractDate from VS_XSGL_TRADE xx left join (select * from VS_XK_S_CONTRACT where OppGUID = '" + oppguid + "' and RoomGUID = '" + roomID + "' and TradeStatus = '激活') bu on xx.TRADEGUID = bu.TradeGUID where xx.OppGUID = '" + oppguid + "' and xx.RoomGUID = '" + roomID + "' and xx.STATUS = '激活'";
                result = DbTest.getObject(sql);
                if (result != null) {
                    //计算总价
                    if (result.get("cj") != null && !"".equals(result.get("cj") + "")
                            && result.get("contractAmount") != null && !"".equals(result.get("contractAmount") + "")) {
                        double sum = Double.valueOf(result.get("contractAmount") + "") + Double.valueOf(result.get("cj") + "");
                        String contractSum = df.format(sum);
                        result.put("contractSum", contractSum);
                    }
                    if (result.get("orderDate") != null && !"".equals(result.get("orderDate") + "")) {
                        try {
                            String orderDate = sf.format(sf.parse(result.get("orderDate") + ""));
                            result.put("orderDate", orderDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (result.get("contractDate") != null && !"".equals(result.get("contractDate") + "")) {
                        try {
                            String contractDate = sf.format(sf.parse(result.get("contractDate") + ""));
                            result.put("contractDate", contractDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    result = new HashMap();
                }
            }
        }
        List<Map> list = projectCluesDao.getDealRecord(projectClueId,roomID);
        result.put("list",list);
        return result;
    }

    @Override
    public List<Supplier> getSupplierList() {

        return projectCluesDao.getSupplierList();
    }

    @Override
    public ResultBody addIntermediaryStores(IntermediaryStores map) {

        try{
            String companyId = "";
            //判断是新增、引入
            if ("1".equals(map.getAddType())){
                //新增
                Map addMap = new HashMap();
                String startTime = map.getStartTime();
                if (startTime != null && !"".equals(startTime)) {
                    addMap.put("startTime", startTime);
                    map.setStartTime(startTime);
                }

                String endTime = map.getEndTime();
                if (map.getEndTime() != null && !"".equals(endTime)) {
                    addMap.put("endTime", endTime);
                    map.setEndTime(endTime);
                }
                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateNowStr = sdf.format(d);
                int i = (int) (Math.random() * 900 + 100);
                String myStr = Integer.toString(i);
                String code = dateNowStr.substring(2, 4) + dateNowStr.substring(5, 7) + dateNowStr.substring(8, 10) + myStr;
                //去数据库查一下如果存在就重新生成
                String companyCode= companyService.isValidByOrgCode(code);
                while (companyCode!=null){
                    i=(int) (Math.random() * 900 + 100);
                    myStr=Integer.toString(i);
                    code=dateNowStr.substring(2, 4) + dateNowStr.substring(5, 7) + dateNowStr.substring(8, 10) + myStr;
                    companyCode=companyService.isValidByOrgCode(code);
                }
                addMap.put("companyCode", code);
                addMap.put("companyName",map.getCompanyName());
                addMap.put("userId",SecurityUtils.getUserId());
                addMap.put("Status","1");
                addMap.put("companyAttr","2");
                addMap.put("headquartersId",map.getHeadquartersId());
                addMap.put("headquartersName",map.getHeadquartersName());
                int a = companyService.insertCompany(addMap);
                //获取中介ID
                companyId = projectCluesDao.getCompnayID(code);
            }else{
                //引入
                companyId = map.getCompanyId();
                if (map.getHeadquartersId()!=null){
                    //更新中介公司
                    projectCluesDao.updateCompany(map);
                }
            }
            map.setCompanyId(companyId);
            //查询项目下中介部门
            OrganizationForm recordPid = projectCluesDao.getZJBM(map.getProjectId());
            //保存组织
            OrganizationForm record = new OrganizationForm();
            record.setPid(recordPid.getId());
            record.setFullPath(recordPid.getFullPath());
            record.setLevels(2);
            record.setOrgCompanyId(companyId);
            record.setListIndex(Integer.valueOf(map.getListIndex()));
            record.setOrgName(map.getOrgName());
            record.setOrgShortName(map.getOrgName());
            record.setOrgCategory(7);
            record.setStatus(Integer.valueOf(map.getOrgStatus()));
            organizationService.save(record);
            //获取组织ID
            String projectOrgId = projectCluesDao.getOrgId(recordPid.getId(),companyId);
            map.setOrgId(projectOrgId);
            //保存参数
            ProjectProtectRuleForm projectProtectRuleForm = new ProjectProtectRuleForm();
            projectProtectRuleForm.setStandbyMode(Integer.valueOf(map.getStandbyMode()));
            projectProtectRuleForm.setIdyVerification(Integer.valueOf(map.getIdyVerification()));
            projectProtectRuleForm.setProjectOrgCategory(2);
            projectProtectRuleForm.setIsAllowRepeatReport(0);
            projectProtectRuleForm.setSourceType(1);
            projectProtectRuleForm.setOrgId(projectOrgId);
            projectProtectRuleForm.setProjectId(map.getProjectId());
            projectProtectRuleForm.setCutGuestDrainage(Double.valueOf(map.getCutGuestDrainage()));
            projectProtectRuleForm.setReportExpireDays(Integer.valueOf(map.getReportExpireDays()));
            projectProtectRuleForm.setReportDaysWarning(Integer.valueOf(map.getReportDaysWarning()));
            projectProtectRuleForm.setChannelProtectionPeriod(Integer.valueOf(map.getChannelProtectionPeriod()));
            projectProtectRuleForm.setChannelProtectionPeriodWarning(Integer.valueOf(map.getChannelProtectionPeriodWarning()));
            messageMapper.ProjectProtectRuleInfo_Inset(projectProtectRuleForm);

            //保存修改批次
            RuleEditLogBatch ruleEditLogBatch = new RuleEditLogBatch();
            String batchId = UUID.randomUUID().toString();
            ruleEditLogBatch.setId(batchId);
            ruleEditLogBatch.setCreator(SecurityUtils.getUserId());
            ruleEditLogBatch.setProjectId(map.getProjectId());
            ruleEditLogBatch.setEditType("1");
            ruleEditLogBatch.setRuleType(map.getCompanyName());
            ruleEditLogBatch.setRuleTypeCode(projectOrgId);
            //是否存在关联公司
            boolean flag = false;
            if (map.getHeadquartersId()!=null && !"".equals(map.getHeadquartersId())){
                ruleEditLogBatch.setEditParams("门店实际名称,门店展示名称,有效开始时间,有效结束时间,防截客时间(分钟),报备保护期(小时),报备预警(小时),渠道保护期(天),渠道预警(天),中介门店状态,关联公司");
                flag = true;
            }else{
                ruleEditLogBatch.setEditParams("门店实际名称,门店展示名称,有效开始时间,有效结束时间,防截客时间(分钟),报备保护期(小时),报备预警(小时),渠道保护期(天),渠道预警(天),中介门店状态");
            }
            ruleEditDao.addRuleEditBatch(ruleEditLogBatch);
            List<RuleEditLogDetail> list = this.addAZJ(map,batchId);
            if (flag){
                RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
                ruleEditLogDetail.setBatchId(batchId);
                ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
                ruleEditLogDetail.setParam("关联公司");
                ruleEditLogDetail.setProjectId(map.getProjectId());
                ruleEditLogDetail.setBeforeEdit("");
                ruleEditLogDetail.setAfterEdit(map.getHeadquartersName());
                list.add(ruleEditLogDetail);
            }
            ruleEditDao.addRuleEditLogBetails(list);

        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-100022,"发生异常！");
        }
        return ResultBody.success("成功！！");
    }

    private List<RuleEditLogDetail> addAZJ(IntermediaryStores map,String batchId) {
        //保存修改详情
        List<RuleEditLogDetail> list = new ArrayList<>();

        RuleEditLogDetail ruleEditLogDetail1 = new RuleEditLogDetail();
        ruleEditLogDetail1.setBatchId(batchId);
        ruleEditLogDetail1.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail1.setParam("门店实际名称");
        ruleEditLogDetail1.setProjectId(map.getProjectId());
        ruleEditLogDetail1.setBeforeEdit("");
        ruleEditLogDetail1.setAfterEdit(map.getCompanyName());
        list.add(ruleEditLogDetail1);

        RuleEditLogDetail ruleEditLogDetail2 = new RuleEditLogDetail();
        ruleEditLogDetail2.setBatchId(batchId);
        ruleEditLogDetail2.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail2.setParam("门店展示名称");
        ruleEditLogDetail2.setProjectId(map.getProjectId());
        ruleEditLogDetail2.setBeforeEdit("");
        ruleEditLogDetail2.setAfterEdit(map.getOrgName());
        list.add(ruleEditLogDetail2);

        RuleEditLogDetail ruleEditLogDetail3 = new RuleEditLogDetail();
        ruleEditLogDetail3.setBatchId(batchId);
        ruleEditLogDetail3.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail3.setParam("有效开始时间");
        ruleEditLogDetail3.setProjectId(map.getProjectId());
        ruleEditLogDetail3.setBeforeEdit("");
        ruleEditLogDetail3.setAfterEdit(map.getStartTime());
        list.add(ruleEditLogDetail3);

        RuleEditLogDetail ruleEditLogDetail4 = new RuleEditLogDetail();
        ruleEditLogDetail4.setBatchId(batchId);
        ruleEditLogDetail4.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail4.setParam("有效结束时间");
        ruleEditLogDetail4.setProjectId(map.getProjectId());
        ruleEditLogDetail4.setBeforeEdit("");
        ruleEditLogDetail4.setAfterEdit(map.getEndTime());
        list.add(ruleEditLogDetail4);

        RuleEditLogDetail ruleEditLogDetail5 = new RuleEditLogDetail();
        ruleEditLogDetail5.setBatchId(batchId);
        ruleEditLogDetail5.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail5.setParam("防截客时间(分钟)");
        ruleEditLogDetail5.setProjectId(map.getProjectId());
        ruleEditLogDetail5.setBeforeEdit("");
        ruleEditLogDetail5.setAfterEdit(map.getCutGuestDrainage());
        list.add(ruleEditLogDetail5);

        RuleEditLogDetail ruleEditLogDetail6 = new RuleEditLogDetail();
        ruleEditLogDetail6.setBatchId(batchId);
        ruleEditLogDetail6.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail6.setParam("报备保护期(小时)");
        ruleEditLogDetail6.setProjectId(map.getProjectId());
        ruleEditLogDetail6.setBeforeEdit("");
        ruleEditLogDetail6.setAfterEdit(map.getReportExpireDays());
        list.add(ruleEditLogDetail6);

        RuleEditLogDetail ruleEditLogDetail7 = new RuleEditLogDetail();
        ruleEditLogDetail7.setBatchId(batchId);
        ruleEditLogDetail7.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail7.setParam("报备预警(小时)");
        ruleEditLogDetail7.setProjectId(map.getProjectId());
        ruleEditLogDetail7.setBeforeEdit("");
        ruleEditLogDetail7.setAfterEdit(map.getReportDaysWarning());
        list.add(ruleEditLogDetail7);

        RuleEditLogDetail ruleEditLogDetail8 = new RuleEditLogDetail();
        ruleEditLogDetail8.setBatchId(batchId);
        ruleEditLogDetail8.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail8.setParam("渠道保护期(天)");
        ruleEditLogDetail8.setProjectId(map.getProjectId());
        ruleEditLogDetail8.setBeforeEdit("");
        ruleEditLogDetail8.setAfterEdit(map.getChannelProtectionPeriod());
        list.add(ruleEditLogDetail8);

        RuleEditLogDetail ruleEditLogDetail9 = new RuleEditLogDetail();
        ruleEditLogDetail9.setBatchId(batchId);
        ruleEditLogDetail9.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail9.setParam("渠道预警(天)");
        ruleEditLogDetail9.setProjectId(map.getProjectId());
        ruleEditLogDetail9.setBeforeEdit("");
        ruleEditLogDetail9.setAfterEdit(map.getChannelProtectionPeriodWarning());
        list.add(ruleEditLogDetail9);

        RuleEditLogDetail ruleEditLogDetail10 = new RuleEditLogDetail();
        ruleEditLogDetail10.setBatchId(batchId);
        ruleEditLogDetail10.setCreator(SecurityUtils.getUserId());
        ruleEditLogDetail10.setParam("中介门店状态");
        ruleEditLogDetail10.setProjectId(map.getProjectId());
        ruleEditLogDetail10.setBeforeEdit("");
        ruleEditLogDetail10.setAfterEdit(map.getOrgStatus().equals("1") ? "启用" : "禁用");
        list.add(ruleEditLogDetail10);

        return list;

    }


    @Override
    public ResultBody updateIntermediaryRule(IntermediaryStores map) {
        try{
            projectCluesDao.updateIntermediaryRule(map);
            //保存修改批次
            RuleEditLogBatch ruleEditLogBatch = new RuleEditLogBatch();
            String batchId = UUID.randomUUID().toString();
            ruleEditLogBatch.setId(batchId);
            ruleEditLogBatch.setCreator(SecurityUtils.getUserId());
            ruleEditLogBatch.setProjectId(map.getProjectId());
            ruleEditLogBatch.setEditType("1");
            ruleEditLogBatch.setRuleType(map.getCompanyName());
            ruleEditLogBatch.setRuleTypeCode(map.getOrgId());
            ruleEditLogBatch.setEditParams("中介门店状态");
            ruleEditDao.addRuleEditBatch(ruleEditLogBatch);
            //保存修改详情
            List<RuleEditLogDetail> list = new ArrayList<>();
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("中介门店状态");
            ruleEditLogDetail.setProjectId(map.getProjectId());
            ruleEditLogDetail.setBeforeEdit(map.getOrgStatus().equals("1") ? "禁用" : "启用");
            ruleEditLogDetail.setAfterEdit(map.getOrgStatus().equals("1") ? "启用" : "禁用");
            list.add(ruleEditLogDetail);
            ruleEditDao.addRuleEditLogBetails(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-100003,"发生异常！");
        }
        return ResultBody.success("更新成功！");
    }

    @Override
    public ResultBody getIntermediaryList() {
        return ResultBody.success(projectCluesDao.getIntermediaryList());
    }

    @Override
    public ResultBody getClueReferral(ClueReferralForm map) {
        PageHelper.startPage(Integer.parseInt(map.getCurrent()),Integer.parseInt(map.getSize()));
        List<ClueReferralVo> clueReferralVos = projectCluesDao.getClueReferral(map);
        return ResultBody.success(new PageInfo<>(clueReferralVos));
    }

    @Override
    public ResultBody selectProjectRule(Map map) {
        List<ProjectRuleDetail> projectRuleDetails = new ArrayList<>();
        projectRuleDetails = projectCluesDao.selectProjectRule(map);
        if (projectRuleDetails!=null && projectRuleDetails.size()>0){
            ProjectRuleDetail p = projectRuleDetails.get(0);
            List<Map> enclosures = projectCluesDao.getProRuleEnclosures(p.getId());
            p.setEnclosures(enclosures);
            return ResultBody.success(p);
        }
        return ResultBody.success(null);
    }
    @Override
    public ResultBody selectProjectRuleByProjectId(Map map) {
        // 不要覆盖参数map
        String projectId = String.valueOf(map.get("projectId"));
        String comGUID = projectCluesDao.getComGUIDByProject(projectId); // 区域ID

        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs(projectId, "2");
        if (projectRuleDetail != null) {
            return ResultBody.success(projectRuleDetail);
        }
        projectRuleDetail = projectCluesDao.selectProjectRuleZs(comGUID, "2");
        if (projectRuleDetail != null) {
            return ResultBody.success(projectRuleDetail);
        }
        projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1", "2");
        return ResultBody.success(projectRuleDetail); // 可能为null
    }

    @Override
    public ResultBody addOrEditProjectRule(ProjectRuleDetail map) {
        //需要新增的规则
        List<ProjectRuleDetail> addList = new ArrayList<>();
        //需要更新的规则
        List<ProjectRuleDetail> updateList = new ArrayList<>();
        //判断是否新增
        if (StringUtils.isEmpty(map.getId())){
            map.setId(UUID.randomUUID().toString().replaceAll("-",""));
            map.setCreator(SecurityUtils.getUserId());
            addList.add(map);
        }else{
            map.setEditor(SecurityUtils.getUserId());
            map.setCreator(SecurityUtils.getUserId());
            updateList.add(map);
            projectCluesDao.delProRuleEnclosures(map);
            //获取原规则数据
            Map ruleMap = new HashMap();
            ruleMap.put("projectId",map.getProjectID());
            List<ProjectRuleDetail> projectRuleDetails = new ArrayList<>();
            projectRuleDetails = projectCluesDao.selectProjectRule(ruleMap);
            ProjectRuleDetail p = projectRuleDetails.get(0);
            //保存修改日志
            String batchId = this.addRuleEditLog(p,map);
            map.setEditBatchId(batchId);
        }
        if (map.getEnclosures().size()>0){
            //保存附件
            projectCluesDao.addProRuleEnclosures(map);
        }
        if (addList.size()>0){
            projectCluesDao.addProjectRule(addList);
        }
        if (updateList.size()>0){
            projectCluesDao.updateProjectRule(updateList);
        }
        return ResultBody.success("编辑成功！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody deleteProjectRule(ProjectRuleDetail map) {
        if(StringUtils.isEmpty(map.getId())){
            return ResultBody.error(400,"规则id不能为空");
        }
        int i = projectCluesDao.delProjectRule(map);
        if(i > 1){
            throw new RuntimeException("删除失败");
        }
        if(i == 1){
            //保存修改批次
            RuleEditLogBatch ruleEditLogBatch = new RuleEditLogBatch();
            String batchId = UUID.randomUUID().toString();
            ruleEditLogBatch.setId(batchId);
            ruleEditLogBatch.setCreator(SecurityUtils.getUserId());
            ruleEditLogBatch.setProjectId(map.getProjectID());
            ruleEditLogBatch.setEditType("2");
            ruleEditLogBatch.setEditParams("删除规则配置");
            ruleEditDao.addRuleEditBatch(ruleEditLogBatch);
//            //保存附件
//            map.setEditBatchId(batchId);
//            if (map.getEnclosures().size()>0){
//                projectCluesDao.addProRuleEnclosures(map);
//            }
        }
        return i == 0 ? ResultBody.error(400,"删除失败") : ResultBody.success("删除成功");
    }

    private String addRuleEditLog(ProjectRuleDetail old,ProjectRuleDetail ne){
        StringBuffer sb = new StringBuffer();
        RuleEditLogBatch ruleEditLogBatch = new RuleEditLogBatch();
        String batchId = UUID.randomUUID().toString();
        List<RuleEditLogDetail> list = new ArrayList<>();
        //判断修改字段
        if (old.getChannelReportMax()!=ne.getChannelReportMax()){
            sb.append("报备客户上限数,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("报备客户上限数");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getChannelReportMax());
            ruleEditLogDetail.setAfterEdit(ne.getChannelReportMax());
            list.add(ruleEditLogDetail);
        }
        if (old.getAllocationCustomerMax()!=ne.getAllocationCustomerMax()){
            sb.append("线索客户分配客户上限数,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("线索客户分配客户上限数");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getAllocationCustomerMax());
            ruleEditLogDetail.setAfterEdit(ne.getAllocationCustomerMax());
            list.add(ruleEditLogDetail);
        }
        if (old.getReportExpireDays()!=ne.getReportExpireDays()){
            sb.append("报备保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("报备保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReportExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getReportExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getReportDaysWarning()!=ne.getReportDaysWarning()){
            sb.append("报备预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("报备预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReportDaysWarning());
            ruleEditLogDetail.setAfterEdit(ne.getReportDaysWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getMaxFollowUpDays()!=ne.getMaxFollowUpDays()){
            sb.append("案场最长保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("案场最长保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getMaxFollowUpDays());
            ruleEditLogDetail.setAfterEdit(ne.getMaxFollowUpDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getVisitingWarning()!=ne.getVisitingWarning()){
            sb.append("到访预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("到访预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getVisitingWarning());
            ruleEditLogDetail.setAfterEdit(ne.getVisitingWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getVisitExpireDays()!=ne.getVisitExpireDays()){
            sb.append("到访保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("到访保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getVisitExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getVisitExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getMaxVisit()!=ne.getMaxVisit()){
            sb.append("最大到访次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("最大到访次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getMaxVisit());
            ruleEditLogDetail.setAfterEdit(ne.getMaxVisit());
            list.add(ruleEditLogDetail);
        }
        if (old.getTheNextVisitFollowupExpireDays()!=ne.getTheNextVisitFollowupExpireDays()){
            sb.append("跟进保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("跟进保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getTheNextVisitFollowupExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getTheNextVisitFollowupExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getFollowupExpireDaysWarning()!=ne.getFollowupExpireDaysWarning()){
            sb.append("跟进预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("跟进预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getFollowupExpireDaysWarning());
            ruleEditLogDetail.setAfterEdit(ne.getFollowupExpireDaysWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getMaxFollowUp()!=ne.getMaxFollowUp()){
            sb.append("最大跟进次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("最大跟进次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getMaxFollowUp());
            ruleEditLogDetail.setAfterEdit(ne.getMaxFollowUp());
            list.add(ruleEditLogDetail);
        }
        if (old.getComeVisitingWarning()!=ne.getComeVisitingWarning()){
            sb.append("拜访预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("拜访预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getComeVisitingWarning());
            ruleEditLogDetail.setAfterEdit(ne.getComeVisitingWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getComeVisitExpireDays()!=ne.getComeVisitExpireDays()){
            sb.append("拜访保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("拜访保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getComeVisitExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getComeVisitExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getMaxComeVisit()!=ne.getMaxComeVisit()){
            sb.append("最大拜访次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("最大拜访次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getMaxComeVisit());
            ruleEditLogDetail.setAfterEdit(ne.getMaxComeVisit());
            list.add(ruleEditLogDetail);
        }
        if (old.getFollowUpConfirmationTime()!=ne.getFollowUpConfirmationTime()){
            sb.append("跟进审批时间（小时）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("跟进审批时间（小时）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getFollowUpConfirmationTime());
            ruleEditLogDetail.setAfterEdit(ne.getFollowUpConfirmationTime());
            list.add(ruleEditLogDetail);
        }
        if (old.getObtainCstConfirmationTime()!=ne.getObtainCstConfirmationTime()){
            sb.append("公客池捞取缓冲期（小时）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("公客池捞取缓冲期（小时）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getObtainCstConfirmationTime());
            ruleEditLogDetail.setAfterEdit(ne.getObtainCstConfirmationTime());
            list.add(ruleEditLogDetail);
        }
        if (old.getAssignPoolsExpireDays()!=ne.getAssignPoolsExpireDays()){
            sb.append("公客池分配客户保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("公客池分配客户保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getAssignPoolsExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getAssignPoolsExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getAssignExpireDays()!=ne.getAssignExpireDays()){
            sb.append("分配客户保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("分配客户保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getAssignExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getAssignExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getDeliveryCustomerProtectTime()!=ne.getDeliveryCustomerProtectTime()){
            sb.append("已交房客户保护期（小时）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("已交房客户保护期（小时）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getDeliveryCustomerProtectTime());
            ruleEditLogDetail.setAfterEdit(ne.getDeliveryCustomerProtectTime());
            list.add(ruleEditLogDetail);
        }
        if (old.getProjectPoolRetentionTime()!=ne.getProjectPoolRetentionTime()){
            sb.append("项目客户池保留时间（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("项目客户池保留时间（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getProjectPoolRetentionTime());
            ruleEditLogDetail.setAfterEdit(ne.getProjectPoolRetentionTime());
            list.add(ruleEditLogDetail);
        }
        if (old.getRegionPoolRetentionTime()!=ne.getRegionPoolRetentionTime()){
            sb.append("区域客户池保留时间（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("区域客户池保留时间（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getRegionPoolRetentionTime());
            ruleEditLogDetail.setAfterEdit(ne.getRegionPoolRetentionTime());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralConfirmationTime()!=ne.getReferralConfirmationTime()){
            sb.append("转介确认时间（小时）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介确认时间（小时）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralConfirmationTime());
            ruleEditLogDetail.setAfterEdit(ne.getReferralConfirmationTime());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralWarning()!=ne.getReferralWarning()){
            sb.append("转介预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralWarning());
            ruleEditLogDetail.setAfterEdit(ne.getReferralWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralExpireDays()!=ne.getReferralExpireDays()){
            sb.append("转介保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getReferralExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralVisitingWarning()!=ne.getReferralVisitingWarning()){
            sb.append("转介到访预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介到访预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralVisitingWarning());
            ruleEditLogDetail.setAfterEdit(ne.getReferralVisitingWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralVisitExpireDays()!=ne.getReferralVisitExpireDays()){
            sb.append("转介到访保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介到访保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralVisitExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getReferralVisitExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralMaxVisit()!=ne.getReferralMaxVisit()){
            sb.append("转介最大到访次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介最大到访次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralMaxVisit());
            ruleEditLogDetail.setAfterEdit(ne.getReferralMaxVisit());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralTheNextVisitFollowupExpireDays()!=ne.getReferralTheNextVisitFollowupExpireDays()){
            sb.append("转介跟进保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介跟进保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralTheNextVisitFollowupExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getReferralTheNextVisitFollowupExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralFollowupExpireDaysWarning()!=ne.getReferralFollowupExpireDaysWarning()){
            sb.append("转介跟进预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介跟进预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralFollowupExpireDaysWarning());
            ruleEditLogDetail.setAfterEdit(ne.getReferralFollowupExpireDaysWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralMaxFollowUp()!=ne.getReferralMaxFollowUp()){
            sb.append("转介最大跟进次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介最大跟进次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralMaxFollowUp());
            ruleEditLogDetail.setAfterEdit(ne.getReferralMaxFollowUp());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralMaxFollowUpDays()!=ne.getReferralMaxFollowUpDays()){
            sb.append("转介最长保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介最长保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralMaxFollowUpDays());
            ruleEditLogDetail.setAfterEdit(ne.getReferralMaxFollowUpDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralComeVisitingWarning()!=ne.getReferralComeVisitingWarning()){
            sb.append("转介拜访预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介拜访预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralComeVisitingWarning());
            ruleEditLogDetail.setAfterEdit(ne.getReferralComeVisitingWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralComeVisitExpireDays()!=ne.getReferralComeVisitExpireDays()){
            sb.append("转介拜访保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介拜访保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralComeVisitExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getReferralComeVisitExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralMaxComeVisit()!=ne.getReferralMaxComeVisit()){
            sb.append("转介最大拜访次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介最大拜访次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralMaxComeVisit());
            ruleEditLogDetail.setAfterEdit(ne.getReferralMaxComeVisit());
            list.add(ruleEditLogDetail);
        }
        if (old.getDirectReferralExpireDays()!=ne.getDirectReferralExpireDays()){
            sb.append("直接转介保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("直接转介保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getDirectReferralExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getDirectReferralExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getDirectReferralVisitingWarning()!=ne.getDirectReferralVisitingWarning()){
            sb.append("直接转介到访预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("直接转介到访预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getDirectReferralVisitingWarning());
            ruleEditLogDetail.setAfterEdit(ne.getDirectReferralVisitingWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getDirectReferralVisitExpireDays()!=ne.getDirectReferralVisitExpireDays()){
            sb.append("直接转介到访保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("直接转介到访保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getDirectReferralVisitExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getDirectReferralVisitExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getDirectReferralMaxVisit()!=ne.getDirectReferralMaxVisit()){
            sb.append("直接转介最大到访次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("直接转介最大到访次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getDirectReferralMaxVisit());
            ruleEditLogDetail.setAfterEdit(ne.getDirectReferralMaxVisit());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtReportDaysWarning()!=ne.getWqtReportDaysWarning()){
            sb.append("万企通预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtReportDaysWarning());
            ruleEditLogDetail.setAfterEdit(ne.getWqtReportDaysWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtReportExpireDays()!=ne.getWqtReportExpireDays()){
            sb.append("万企通保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtReportExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getWqtReportExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtMaxFollowUpDays()!=ne.getWqtMaxFollowUpDays()){
            sb.append("万企通最长保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通最长保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtMaxFollowUpDays());
            ruleEditLogDetail.setAfterEdit(ne.getWqtMaxFollowUpDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtVisitingWarning()!=ne.getWqtVisitingWarning()){
            sb.append("万企通到访预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通到访预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtVisitingWarning());
            ruleEditLogDetail.setAfterEdit(ne.getWqtVisitingWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtVisitExpireDays()!=ne.getWqtVisitExpireDays()){
            sb.append("万企通到访保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通到访保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtVisitExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getWqtVisitExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtMaxVisit()!=ne.getWqtMaxVisit()){
            sb.append("万企通最大到访次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通最大到访次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtMaxVisit());
            ruleEditLogDetail.setAfterEdit(ne.getWqtMaxVisit());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtTheNextVisitFollowupExpireDays()!=ne.getWqtTheNextVisitFollowupExpireDays()){
            sb.append("万企通跟进保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通跟进保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtTheNextVisitFollowupExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getWqtTheNextVisitFollowupExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtFollowupExpireDaysWarning()!=ne.getWqtFollowupExpireDaysWarning()){
            sb.append("万企通跟进预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通跟进预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtFollowupExpireDaysWarning());
            ruleEditLogDetail.setAfterEdit(ne.getWqtFollowupExpireDaysWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtMaxFollowUp()!=ne.getWqtMaxFollowUp()){
            sb.append("万企通最大跟进次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通最大跟进次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtMaxFollowUp());
            ruleEditLogDetail.setAfterEdit(ne.getWqtMaxFollowUp());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtComeVisitingWarning()!=ne.getWqtComeVisitingWarning()){
            sb.append("万企通拜访预警（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通拜访预警（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtComeVisitingWarning());
            ruleEditLogDetail.setAfterEdit(ne.getWqtComeVisitingWarning());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtComeVisitExpireDays()!=ne.getWqtComeVisitExpireDays()){
            sb.append("万企通拜访保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通拜访保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtComeVisitExpireDays());
            ruleEditLogDetail.setAfterEdit(ne.getWqtComeVisitExpireDays());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtMaxComeVisit()!=ne.getWqtMaxComeVisit()){
            sb.append("万企通最大拜访次数（次）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通最大拜访次数（次）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtMaxComeVisit());
            ruleEditLogDetail.setAfterEdit(ne.getWqtMaxComeVisit());
            list.add(ruleEditLogDetail);
        }
        if (old.getJudgeStage()!=ne.getJudgeStage()){
            sb.append("判客阶段,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("判客阶段");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getJudgeStage());
            ruleEditLogDetail.setAfterEdit(ne.getJudgeStage());
            list.add(ruleEditLogDetail);
        }
        if (old.getJudgeNoRegion()!=ne.getJudgeNoRegion()){
            sb.append("判客区域验证,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("判客区域验证");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getJudgeNoRegion());
            ruleEditLogDetail.setAfterEdit(ne.getJudgeNoRegion());
            list.add(ruleEditLogDetail);
        }
        if (old.getJudgeNoPool()!=ne.getJudgeNoPool()){
            sb.append("判客公海池验证,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("判客公海池验证");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getJudgeNoPool());
            ruleEditLogDetail.setAfterEdit(ne.getJudgeNoPool());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtJudgeStage()!=ne.getWqtJudgeStage()){
            sb.append("万企通判客阶段,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通判客阶段");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtJudgeStage());
            ruleEditLogDetail.setAfterEdit(ne.getWqtJudgeStage());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtJudgeNoRegion()!=ne.getWqtJudgeNoRegion()){
            sb.append("万企通判客区域验证,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通判客区域验证");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtJudgeNoRegion());
            ruleEditLogDetail.setAfterEdit(ne.getWqtJudgeNoRegion());
            list.add(ruleEditLogDetail);
        }
        if (old.getWqtJudgeNoPool()!=ne.getWqtJudgeNoPool()){
            sb.append("万企通判客公海池验证,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("万企通判客公海池验证");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getWqtJudgeNoPool());
            ruleEditLogDetail.setAfterEdit(ne.getWqtJudgeNoPool());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralJudgeStage()!=ne.getReferralJudgeStage()){
            sb.append("转介客户判客阶段,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介客户判客阶段");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralJudgeStage());
            ruleEditLogDetail.setAfterEdit(ne.getReferralJudgeStage());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralJudgeNoRegion()!=ne.getReferralJudgeNoRegion()){
            sb.append("转介客户判客区域验证,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介客户判客区域验证");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralJudgeNoRegion());
            ruleEditLogDetail.setAfterEdit(ne.getReferralJudgeNoRegion());
            list.add(ruleEditLogDetail);
        }
        if (old.getReferralJudgeNoPool()!=ne.getReferralJudgeNoPool()){
            sb.append("转介客户判客公海池验证,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("转介客户判客公海池验证");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getReferralJudgeNoPool());
            ruleEditLogDetail.setAfterEdit(ne.getReferralJudgeNoPool());
            list.add(ruleEditLogDetail);
        }
        if(old.getCountThreeOnes()!=ne.getCountThreeOnes()){
            sb.append("计算三个一,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("计算三个一");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getCountThreeOnes());
            ruleEditLogDetail.setAfterEdit(ne.getCountThreeOnes());
            list.add(ruleEditLogDetail);
        }
        if(old.getEnterpriseDatabaseCustomerProtectionDays()!=ne.getEnterpriseDatabaseCustomerProtectionDays()){
            sb.append("企业数据库客户保护时效,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("企业数据库客户保护时效");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getEnterpriseDatabaseCustomerProtectionDays());
            ruleEditLogDetail.setAfterEdit(ne.getEnterpriseDatabaseCustomerProtectionDays());
            list.add(ruleEditLogDetail);
        }
        if(old.getCallNotDialCustomerProtectDays()!=ne.getCallNotDialCustomerProtectDays()){
            sb.append("未拨打客户保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("未拨打客户保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getCallNotDialCustomerProtectDays());
            ruleEditLogDetail.setAfterEdit(ne.getCallNotDialCustomerProtectDays());
            list.add(ruleEditLogDetail);
        }
        if(old.getCallNotConnCustomerProtectDays()!=ne.getCallNotConnCustomerProtectDays()){
            sb.append("未接通客户保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("未接通客户保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getCallNotConnCustomerProtectDays());
            ruleEditLogDetail.setAfterEdit(ne.getCallNotConnCustomerProtectDays());
            list.add(ruleEditLogDetail);
        }
        if(old.getCallHasConnCustomerProtectDays()!=ne.getCallHasConnCustomerProtectDays()){
            sb.append("已接通客户保护期（天）,");
            RuleEditLogDetail ruleEditLogDetail = new RuleEditLogDetail();
            ruleEditLogDetail.setBatchId(batchId);
            ruleEditLogDetail.setCreator(SecurityUtils.getUserId());
            ruleEditLogDetail.setParam("已接通客户保护期（天）");
            ruleEditLogDetail.setProjectId(old.getProjectID());
            ruleEditLogDetail.setBeforeEdit(old.getCallHasConnCustomerProtectDays());
            ruleEditLogDetail.setAfterEdit(ne.getCallHasConnCustomerProtectDays());
            list.add(ruleEditLogDetail);
        }

        if (list.size()>0){
            //保存修改批次
            ruleEditLogBatch.setId(batchId);
            ruleEditLogBatch.setCreator(SecurityUtils.getUserId());
            ruleEditLogBatch.setProjectId(old.getProjectID());
            ruleEditLogBatch.setEditType("2");
            ruleEditLogBatch.setEditParams(sb.toString().substring(0,sb.toString().length()-1));
            ruleEditDao.addRuleEditBatch(ruleEditLogBatch);
            //保存修改详情
            ruleEditDao.addRuleEditLogBetails(list);
            return batchId;
        }else{
            return null;
        }
    }

    @Override
    public ResultBody getAppealDetail(Map map) {
        AppealForm appral = projectCluesDao.getAppealDetail(map);
        List<Map> imgUrls = projectCluesDao.getAppealFj(map);
        if (imgUrls!=null && imgUrls.size()>0){
            appral.setImgUrls(imgUrls);
        }
        return ResultBody.success(appral);
    }

    @Override
    public ResultBody getCstSource() {
        List<Map> list = projectCluesDao.getCstSource();
        return ResultBody.success(list);
    }

    @Override
    public ResultBody getCstIndustryOneNew() {
        List<Map> object = (List<Map>) redisUtil.get(VisolinkConstant.REDIS_KEY+".PcSshyD");
        if (null != object) {
            System.out.println("进入redis查询！");
            return ResultBody.success(object);
        }else {
            List<Map> dictList = projectCluesDao.getCstIndustryOneNew();
            for (Map d:dictList) {
                List<Map> c = projectCluesDao.getCstIndustryTwo(d);
                if (dictList!=null && dictList.size()>0){
                    for (Map mm:dictList) {
                        List<Map> children = projectCluesDao.getCstIndustryTwo(mm);
                        if (children!=null && children.size()>0){
                            for (Map ss:children) {
                                List<Map> children1 = projectCluesDao.getCstIndustryTwo(ss);
                                if (children1!=null && children1.size()>0){
                                    for (Map ff:children1) {
                                        List<Map> children2 = projectCluesDao.getCstIndustryTwo(ff);
                                        if (children2!=null && children2.size()>0){
                                            ff.put("children",children2);
                                        }
                                    }
                                    ss.put("children",children1);
                                }
                            }
                            mm.put("children",children);
                        }
                    }
                }
                if(c!=null && c.size()>0){
                    d.put("children",c);
                }
            }
            redisUtil.set(VisolinkConstant.REDIS_KEY+".PcSshyD",dictList,60*60*12);
            return ResultBody.success(dictList);
        }
    }

    @Override
    public ResultBody toOppMoveRecord(Map map) {
        String proIds = map.get("projectIds").toString();
        if(StringUtils.isEmpty(proIds)){
            return ResultBody.success(new PageInfo<>());
        }
        map.put("projectIds", "".equals(proIds) ? null : Arrays.asList(proIds.split(",")));
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageNum")!=null && !"".equals(map.get("pageNum")+"")){
            pageIndex = Integer.parseInt(map.get("pageNum")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                Arrays.asList(proIds.split(",")));
        map.put("orgIds",orgIds);
//        map.put("type","4");//机会转介
        PageHelper.startPage(pageIndex, pageSize);
        List<ReferralRecord> referralRecords = projectCluesDao.selectReferralClue(map);
        return ResultBody.success(new PageInfo<>(referralRecords));
    }

    @Override
    public ResultBody selectReferralClue(Map map) {
        String proIds = map.get("projectIds").toString();
        String rproIds = map.get("receiverProjectIds").toString();
        List<String> projectIds = Arrays.asList(proIds.split(","));
        List<String> receiverProjectIds = Arrays.asList(rproIds.split(","));
        map.put("projectIds",projectIds);
        map.put("receiverProjectIds",receiverProjectIds);
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageNum")!=null && !"".equals(map.get("pageNum")+"")){
            pageIndex = Integer.parseInt(map.get("pageNum")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                Arrays.asList(proIds.split(",")));
        map.put("orgIds",orgIds);
        PageHelper.startPage(pageIndex, pageSize);
        List<ReferralRecord> referralRecords = projectCluesDao.selectReferralClue(map);
        return ResultBody.success(new PageInfo<>(referralRecords));
    }

    @Override
    public void toOppMoveRecordExport(HttpServletRequest request, HttpServletResponse response, Map map) {
        List<Map> fileds = (List<Map>) map.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        List<ReferralRecord> referralRecordList = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = "转介记录";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        List<String> proIdList = new ArrayList<>();
        if (map.get("projectIds") != null && !"".equals(map.get("projectIds") + "")){
            String regionIds = map.get("projectIds") + "";
            proIdList = Arrays.asList(regionIds.split(","));
            map.put("projectIds", proIdList);
        }
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                proIdList);
        map.put("orgIds",orgIds);
        referralRecordList = projectCluesDao.selectReferralClue(map);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(proIdList)) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setAreaName(proMap.get("areaName") + "");
            excelExportLog.setProjectId(proMap.get("projectId") + "");
            excelExportLog.setProjectName(proMap.get("projectName") + "");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(map));
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (referralRecordList != null && referralRecordList.size() > 0){
            String isAllStr = map.get("isAll") + "";
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
//            headers = referralRecordList.get(0).courtCaseTitle1;
            headers = filedNames.toArray(new String[0]);

            int rowNum = 1;
            for (ReferralRecord model : referralRecordList) {
                model.setRownum(rowNum + "");
                Object[] oArray = model.toData1(isAll,filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String toOppMoveRecordExportNew(HttpServletRequest request, HttpServletResponse response, Map map) {
        String userId = SecurityUtils.getUserId();
        //导出的文档下面的名字
        String excelName = "转介记录";
        List<String> proIdList = new ArrayList<>();
        if (map.get("projectIds") != null && !"".equals(map.get("projectIds") + "")){
            String regionIds = map.get("projectIds") + "";
            proIdList = Arrays.asList(regionIds.split(","));
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("MR1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType(String.valueOf(map.get("isAll")));//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(proIdList)) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setAreaName(proMap.get("areaName") + "");
            excelExportLog.setProjectId(proMap.get("projectId") + "");
            excelExportLog.setProjectName(proMap.get("projectName") + "");
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

    @Override
    public void selectReferralClueExport(HttpServletRequest request, HttpServletResponse response, Map map) {
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        String proIds = map.get("projectIds").toString();
        List<String> projectIds = Arrays.asList(proIds.split(","));
        map.put("projectIds",projectIds);
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                projectIds);
        map.put("orgIds",orgIds);
        List<ReferralRecord> list = projectCluesDao.selectReferralClue(map);
        if (list!=null){
            try{
                String isAllStr = map.get("isAll")+"";
                boolean isAll = false;
                //判断是否全号导出
                if ("2".equals(isAllStr)){
                    isAll = true;
                }
                //类型 查询类型（1：渠道商 2：业务员）
                String type = map.get("type")+"";
                excelName = "线索转介列表";
                int num = 0;
                String[] headers = null;
                if ("1".equals(type)){
                    headers = list.get(0).getCourtCaseTitle1();
                }else{
                    headers = list.get(0).getCourtCaseTitle2();
                }
                for(ReferralRecord model : list){
                    num++;
                    model.setRownum(num+"");
                    Object[] oArray = null;
                    if ("1".equals(type)){
                        oArray = model.toData1(isAll,null);
                    }else{
                        oArray = model.toData2(isAll);
                    }
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("线索转介列表",headers,dataset,excelName,response,null);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 数据导出公共接口
     */
    @Override
    public void channelExport(HttpServletRequest request, HttpServletResponse response, String param) {
        ExcelForm excelForm = JSONObject.parseObject(param, ExcelForm.class);
        String AcOrQd = excelForm.getAcOrQd() + "";
        try {
            //导出的文档下面的名字
            String excelName = null;
            ArrayList<Object[]> dataset = new ArrayList<>();
            String[] headers = null;
            if ("channel".equals(AcOrQd)) {
                excelName = "线索客户表";
                Map temMap = new HashMap();
                if (excelForm.getSearch()!=null && !"".equals(excelForm.getSearch())){
                    String search = excelForm.getSearch();
                    //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
                    if (search.matches("[0-9]+")) {
                        temMap.put("mobile",search);
                    } else {
                        temMap.put("name",search);
                    }
                }
                temMap.put("reportUserName", excelForm.getReportUserName());
                temMap.put("reportUserRole", excelForm.getReportUserRole());
                temMap.put("startTime", excelForm.getStartTime());
                temMap.put("endTime", excelForm.getEndTime());
                List<ProjectCluesNew> list = projectCluesDao.channelProjectClues(temMap);
                int rowNum = 1;
                for (ProjectCluesNew model : list) {
                    headers = model.getCourtCaseTitle1();
                    model.setRownum(rowNum);
                    boolean isAllPhone = true;
                    if (excelForm.getIsAll().equals("1")) {
                        isAllPhone = false;
                    }
                    Object[] oArray = model.toData1(isAllPhone,null);
                    dataset.add(oArray);
                    rowNum++;
                }
            } else {
                excelName = "机会客户表";
                if (excelForm.getSearch()!=null && !"".equals(excelForm.getSearch())){
                    String search = excelForm.getSearch();
                    //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
                    if (search.matches("[0-9]+")) {
                        excelForm.setCustomerMobile(search);
                    } else {
                        excelForm.setCustomerName(search);
                    }
                }
                List<ProjectCluesNew> list = projectCluesDao.courtCase(excelForm);
                int rowNum = 1;
                for (ProjectCluesNew model : list) {
                    headers = model.getCourtCaseTitle2();
                    model.setRownum(rowNum);
                    boolean isAllPhone = true;
                    if (excelForm.getIsAll().equals("1")) {
                        isAllPhone = false;
                    }
                    Object[] oArray = model.toData2(isAllPhone,null,false);
                    dataset.add(oArray);
                    rowNum++;
                }
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 客户明细台账
    @Override
    public PageInfo<ProjectCluesVO> queryAllCustmerDetails(ProjectCluesForm projectCluesForm) {
        PageHelper.startPage((int) projectCluesForm.getCurrent(), (int) projectCluesForm.getSize());
               /* if(!StringUtils.isBlank(projectCluesForm.getProjectId())){
                     projectCluesForm.setProjectList(Arrays.asList(projectCluesForm.getProjectId().split(",")));
                }*/
        com.github.pagehelper.Page<ProjectCluesVO> list = projectCluesDao.queryAllCustmerDetails(projectCluesForm);
        return new PageInfo<ProjectCluesVO>(list);
    }

    @Override
    public ResultBody getProZsSales(Map map) {
        if(StringUtils.isEmpty(map.get("projectId")+"")){
            return ResultBody.error(-10001,"项目不能为空！");
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageNum") != null){
            pageIndex = Integer.parseInt(map.get("pageNum") + "");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize") + "");
        }
        map.put("userId",SecurityUtils.getUserId());
        map.put("pageNum","");
        map.put("pageSize","");
        String jobCode = projectMapper.getZyProjectGw(map);
        if(StringUtils.isEmpty(jobCode)){
            return ResultBody.error(-10001,"岗位设置错误 请联系综管!");
        }
        List<Map> list = new ArrayList<>();
        if ("zszj".equals(jobCode)){
            map.put("jobCode","zygw");
        }else {
            map.put("jobCode","qyzygw");
        }
        PageHelper.startPage(pageIndex, pageSize);
        list = projectCluesDao.getProZsSales(map);
        return ResultBody.success(new PageInfo(list));
    }

    /**
     * 分配客户
     *
     * @param salesAttributionForm
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody allocationPropertyConsultant(SalesAttributionForm salesAttributionForm) {
//        if (StringUtils.isBlank(salesAttributionForm.getProjectClueId())) {
//            return ResultBody.error(2001, "参数异常！");
//        }
        try {
            if (StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId()) && (salesAttributionForm.getProjectClueList()==null || salesAttributionForm.getProjectClueList().size()==0)){
                return ResultBody.error(2001, "请选择需分配的客户！");
            }
            List<String> oppIds = new ArrayList<>();
            if (!StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId())){
                oppIds.add(salesAttributionForm.getOpportunityClueId());
            }else{
                oppIds = salesAttributionForm.getProjectClueList();
            }
            int noAllowCount = 0;
            if (oppIds.size()>0){
                Map gwMap = new HashMap<>();
                gwMap.put("userId",SecurityUtils.getUserId());
                gwMap.put("projectId",salesAttributionForm.getProjectId());
                String jobCode = projectMapper.getZyProjectGw(gwMap);
                if(StringUtils.isEmpty(jobCode)){
                    return ResultBody.error(-10001,"岗位设置错误 请联系综管!");
                }
                String projectId = salesAttributionForm.getProjectId();
                String salesAttributionId = salesAttributionForm.getSalesAttributionId();
                String salesAttributionName = salesAttributionForm.getSalesAttributionName();

                Map param = new HashMap();
                param.put("userId", salesAttributionId);
                param.put("projectId", projectId);

                //获取业务员组织
                if ("zszj".equals(jobCode)){
                    jobCode = "zygw";
                }
                if ("qyzszj".equals(jobCode)){
                    jobCode = "qyzygw";
                }
                param.put("jobCode",jobCode);
                Map orgMap = projectCluesDao.getUserOrg(param);
                String reportUserRole = "";
                if ("zygw".equals(jobCode)){
                    reportUserRole = "1";
                }
                if ("qyzygw".equals(jobCode)){
                    reportUserRole = "2";
                }
                Map map = new HashMap();
                //查询项目规则
                String ComGUID = projectCluesDao.getComGUIDByProject(projectId);// 区域ID
                //查询规则计算报备逾期及预警时间
                ProjectRuleDetail projectRuleDetail = null;
                if ("1".equals(reportUserRole)){
                    projectRuleDetail = projectCluesDao.selectProjectRuleZs(projectId,"2");
                    //项目没有配置规则 查询区域的
                    if (projectRuleDetail==null){
                        projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
                    }
                }else if ("2".equals(reportUserRole)){
                    projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
                }
                //没有区域的查询集团的
                if (projectRuleDetail==null){
                    projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");

                }
                String reportExpireDaysStr = "";
                int reportExpireDays = 0;
                String reportDaysWarningStr = "";
                int reportDaysWarning = 0;
                if (projectRuleDetail!=null) {
                    reportExpireDaysStr = projectRuleDetail.getReportExpireDays();
                    if (StringUtils.isNotEmpty(reportExpireDaysStr)) {
                        reportExpireDays = Integer.parseInt(reportExpireDaysStr);
                    }
                    reportDaysWarningStr = projectRuleDetail.getReportDaysWarning();
                    if (StringUtils.isNotEmpty(reportDaysWarningStr)) {
                        reportDaysWarning = Integer.parseInt(reportDaysWarningStr);
                    }
                }
                //查询规则计算跟进逾期及预警时间
                if (StringUtils.isNotEmpty(reportExpireDaysStr)){
                    Date dBefore = new Date();
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(dBefore);//把当前时间赋给日历
                    calendar.add(Calendar.DAY_OF_MONTH, reportExpireDays);
                    dBefore = calendar.getTime();
                    map.put("salesFollowExpireDate",sf.format(dBefore));
                }else{
                    map.put("salesFollowExpireDate",null);
                }
                if (StringUtils.isNotEmpty(reportDaysWarningStr)){
                    Date dBefore = new Date();
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(dBefore);//把当前时间赋给日历
                    calendar.add(Calendar.DAY_OF_MONTH, reportDaysWarning);
                    dBefore = calendar.getTime();
                    map.put("salesFollowExpireWarningDate",sf.format(dBefore));
                }else{
                    map.put("salesFollowExpireWarningDate",null);
                }
                for (String OpportunityClueId:oppIds) {
//                    String OpportunityClueId = salesAttributionForm.getOpportunityClueId();
                    //获取机会信息
                    Map oldMap = projectCluesDao.getOpportunityById(OpportunityClueId);
                    if(String.valueOf(oldMap.get("SalesAttributionId")).equals(salesAttributionId)){//分配和所属人相同
                        noAllowCount++;
                    }else {
                        oldMap.put("ConfirmID",SecurityUtils.getUserId());
                        oldMap.put("ConfirmTime",new Date());
                        oldMap.put("ApplyDatetime",new Date());
                        oldMap.put("OldSalesId",oldMap.get("SalesAttributionId"));
                        oldMap.put("OldSalesName",oldMap.get("SalesAttributionName"));
                        oldMap.put("SalesId",salesAttributionId);
                        oldMap.put("SalesName",salesAttributionName);
                        oldMap.put("Type","1");
                        oldMap.put("mainProjectId",projectId);
                        oldMap.put("mainProjectName",projectMapper.getProjectNameByProjectId(projectId));
                        //保存分配记录
                        projectCluesDao.addRelCustomerRecord(oldMap);

                        //生成消息
                        //获取客户原信息
                        ReferralVo referralVo = projectCluesDao.getOldOpportunityClueInfo(OpportunityClueId);
                        //发起人
                        Message message = new Message();
                        message.setSubject("【分配客户通知】");
                        message.setContent("【分配客户通知】您的客户：【" + referralVo.getCustomerName() + "】已重新分配，请知悉。");
                        message.setSender("");
                        message.setMessageType(2106);
                        message.setIsDel(0);
                        message.setReceiver(referralVo.getSalesAttributionId());
                        message.setIsRead(0);
                        message.setIsPush(2);
                        message.setIsNeedPush(2);
                        message.setProjectClueId(oldMap.get("ProjectClueId")+"");
                        message.setOpportunityClueId(OpportunityClueId);
                        message.setProjectId(salesAttributionForm.getProjectId());
                        projectCluesDao.insertOneMessage(message);
                        //接收人
                        message.setSubject("【分配客户通知】");
                        message.setContent("【分配客户通知】 您已被分配新的客户:【" + referralVo.getCustomerName() + "】，请知悉。");
                        message.setSender("");
                        message.setMessageType(2106);
                        message.setIsDel(0);
                        message.setReceiver(salesAttributionId);
                        message.setIsRead(0);
                        message.setIsPush(2);
                        message.setIsNeedPush(2);
                        message.setProjectClueId(oldMap.get("ProjectClueId")+"");
                        message.setOpportunityClueId(OpportunityClueId);
                        message.setProjectId(salesAttributionForm.getProjectId());
                        projectCluesDao.insertOneMessage(message);

                        map.put("OpportunityClueId",OpportunityClueId);
                        map.put("salesAttributionId",salesAttributionId);
                        map.put("salesAttributionName",salesAttributionName);
                        map.put("salesAttributionTeamId",orgMap.get("orgId"));
                        map.put("salesAttributionTeamName",orgMap.get("orgName"));
                        map.put("ProjectClueId",oldMap.get("ProjectClueId"));
                        if(!"8".equals(oldMap.get("ClueStatus")+"")){
                            map.put("ClueStatus","1");
                            map.put("salesTheFirstFollowDate","null");
                            //新逻辑 字段改为首次三个一时间 不重置
//                            map.put("isThreeOnesDate","null");
                        }else {
                            map.put("ClueStatus","");
                            map.put("salesTheFirstFollowDate","");
                            //新逻辑 字段改为首次三个一时间 不重置
//                            map.put("isThreeOnesDate","");
                        }
                        map.put("ReportCreateTime","1");
                        projectCluesDao.updateOppCst(map);
                    }
                }
            }
            if(noAllowCount > 0){
                return ResultBody.success("部分分配成功,"+noAllowCount+"条无需分配");
            }else {
                return ResultBody.success("分配成功");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BadRequestException(-11_1058, "分配失败！", e);
        }
    }

    @Override
    public ResultBody getDataViewPremission(UserOrgRelForm map) {
        //获取人员权限 判断是否存在非经理 总监 营销经理权限
//        List<Map> orgList = projectCluesDao.getDataViewPremissionNoOrg(map);
        Map neMap = new HashMap<>();
        neMap.put("UserName",SecurityUtils.getUsername());
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        neMap.put("UserId",SecurityUtils.getUserId());
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(neMap);
        List<String> fullpath = projectMapper.findFullPathSQX(neMap);
        List<ProjectVO> projectList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList)){
                return ResultBody.success("400");
            }
        }else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            neMap.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(neMap);
        }
        if(!CollectionUtils.isEmpty(qyProjectList)){
            for(int i = 0;i<qyProjectList.size();i++){
                if(!projectList.contains(qyProjectList.get(i))){
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
        if(CollectionUtils.isEmpty(projectList)){
//            UserOrgRelForm dataViewPremission = projectCluesDao.getDataViewPremissionApproveStatus(map);
//            if(dataViewPremission == null){
                return ResultBody.success("400");
//            }
        }
        return ResultBody.success("ok");
    }

    @Override
    public ResultBody getDataViewPremissionZs(UserOrgRelForm map) {
        //获取人员权限 判断是否存在非经理 总监 营销经理权限
//        List<Map> orgList = projectCluesDao.getDataViewPremissionNoOrg(map);
        Map neMap = new HashMap<>();
        neMap.put("UserName",SecurityUtils.getUsername());
        //区域营销经理配置在公司层级 可以申请公司下所有项目 根据区域id无法定位数据 单独处理下区域营销经理单独申请的项目
        neMap.put("UserId",SecurityUtils.getUserId());
        List<ProjectVO> qyProjectList = projectMapper.getProjectListByUserIdAndQy(neMap);
        List<String> fullpath = projectMapper.findFullPathAllHasUser(neMap);
        List<ProjectVO> projectList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            if(CollectionUtils.isEmpty(qyProjectList)){
                return ResultBody.success("400");
            }
        }else {
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            neMap.put("where", sb.toString());
            projectList = projectMapper.getProjectListByUserName(neMap);
        }
        if(!CollectionUtils.isEmpty(qyProjectList)){
            for(int i = 0;i<qyProjectList.size();i++){
                if(!projectList.contains(qyProjectList.get(i))){
                    projectList.add(qyProjectList.get(i));
                }
            }
        }
        if(CollectionUtils.isEmpty(projectList)){
//            UserOrgRelForm dataViewPremission = projectCluesDao.getDataViewPremissionApproveStatus(map);
//            if(dataViewPremission == null){
            return ResultBody.success("400");
//            }
        }
        return ResultBody.success("ok");
    }

    @Override
    public ResultBody adminSetDataViewPremission(List<UserOrgRelForm> list) {
        try{
            //禁用当前用户所有权限 以管理员设置的为准
            projectCluesDao.updateAdminDateViewPremissionStatus(list.get(0).getUserId());
            list.stream().forEach(x->{
                String id = UUID.randomUUID().toString();
                x.setId(id);
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String creatTime = simpleDateFormat2.format(new Date());
                SysLog sysLog1 = new SysLog();
                sysLog1.setExecutTime(creatTime);
                sysLog1.setTaskName("管理员授权数据权限查看");
                sysLog1.setNote("管理员授权数据权限查看参数为：" + JSONObject.toJSONString(x));
                projectCluesDao.insertLogs(sysLog1);
                //判断是否存在历史权限审批 以最新的操作权限
                List<UserOrgRelForm> uu = projectCluesDao.getAdminDataViewPremissionApprove(x);
                if(!CollectionUtils.isEmpty(uu)){
                    String mainId = uu.get(0).getId();
                    x.setId(mainId);
                    projectCluesDao.updateAdminDataViewPremission(x);
                }else {
                    projectCluesDao.addAdminDataViewPremission(x);
                }
            });
            return ResultBody.success("管理员授权成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-12232,"管理员授权失败！");
        }
    }

    @Override
    public ResultBody getAllDataViewPremissionInfo(UserOrgRelForm map) {
        List<UserOrgRelForm> allInfo = projectCluesDao.getAllDataViewPremissionInfo(map);
        List<UserOrgRelForm> reList = new ArrayList<>();
        allInfo.stream().forEach(x->{
            x.setOrgIdList(Arrays.asList(x.getOrgId().split(",")));
            x.setOrgNameList(Arrays.asList(x.getOrgName().split(",")));
            reList.add(x);
        });
        return ResultBody.success(reList);
    }

    @Override
    public ResultBody getFollowUpRecordList(FollowUpRecordVO followUpRecordVO) throws ParseException {
        if (followUpRecordVO.getSearch() != null && !"".equals(followUpRecordVO.getSearch())){
            String search = followUpRecordVO.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                followUpRecordVO.setCustomerMobile(search);
            } else {
                followUpRecordVO.setCustomerName(search);
            }
        }
        //处理核验状态
        if(StringUtils.isNotEmpty(followUpRecordVO.getVerificationStatus())){
            if("0".equals(followUpRecordVO.getVerificationStatus())){//全部
                followUpRecordVO.setVerificationStatusList(Arrays.asList("1","2","0"));
            }else if("1".equals(followUpRecordVO.getVerificationStatus())){//未核验
                followUpRecordVO.setVerificationStatusList(Arrays.asList("0"));
            }else if("2".equals(followUpRecordVO.getVerificationStatus())){//已核验合格
                followUpRecordVO.setVerificationStatusList(Arrays.asList("1"));
            }else if("3".equals(followUpRecordVO.getVerificationStatus())){//已核验不合格
                followUpRecordVO.setVerificationStatusList(Arrays.asList("2"));
            }
        }
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
            followUpRecordVO.getProjectList());
        followUpRecordVO.setOrgIds(orgIds);
        int pageNum = 1;
        int pageSize = 10;
        if (followUpRecordVO.getPageNum() != null && followUpRecordVO.getPageNum() != "" ){
            pageNum = Integer.parseInt(followUpRecordVO.getPageNum());
        }
        if (followUpRecordVO.getPageSize() != null && followUpRecordVO.getPageSize() != ""){
            pageSize = Integer.parseInt(followUpRecordVO.getPageSize());
        }
        if(CollectionUtils.isEmpty(followUpRecordVO.getProjectList())){
            List<String> proList = new ArrayList<>();
            proList.add("");
            followUpRecordVO.setProjectList(proList);
        }
//        PageHelper.startPage(pageNum, pageSize);
        followUpRecordVO.setPageNum("");
        int i = (pageNum - 1) * pageSize;
        followUpRecordVO.setPageIndex(String.valueOf(i));
        followUpRecordVO.setPageSize(String.valueOf(pageSize));
        int total = 0;
        List<FollowUpRecordVO> list = null;

         if("clue".equals(followUpRecordVO.getDataSource())){
             total=projectCluesDao.getFollowUpRecordCountClue(followUpRecordVO);
             list=projectCluesDao.getFollowUpRecordListClue(followUpRecordVO);
         } else{
             total=projectCluesDao.getFollowUpRecordCount(followUpRecordVO);
             list=projectCluesDao.getFollowUpRecordList(followUpRecordVO);
         }
        if (list!=null && list.size()>0){
            for (FollowUpRecordVO map : list) {
                String enclosures = map.getEnclosures();
                String threeOnesUrls = map.getThreeOnesUrls();
                String drawingQuotationUrls = map.getDrawingQuotationUrls();
                if (!StringUtils.isEmpty(enclosures)){
                    String[] ss = enclosures.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    map.setEnclosure(enclosure);
                }
//                if (!StringUtils.isEmpty(threeOnesUrls)){
//                    String[] ss = threeOnesUrls.split(",");
//                    List<String> enclosure = Arrays.asList(ss);
//                    map.setThreeOnesUrl(enclosure);
//                }
                if (!StringUtils.isEmpty(drawingQuotationUrls)){
                    String[] ss = drawingQuotationUrls.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    map.setDrawingQuotationUrl(enclosure);
                }
                if(map.getStatus().equals("1") || map.getStatus().equals("4") || map.getStatus().equals("5")){
                    //节点移交人
                    List<Map> nodeMap = projectCluesDao.getFirstNodeMoveInfo(map.getFollowRecordId());
                    if(!CollectionUtils.isEmpty(nodeMap)){
                        nodeMap.stream().forEach(x->{
                            map.setApproveUser(Arrays.asList(map.getApproveUser().split(",")).stream().filter(user -> !user.equals(x.get("oldSalesName").toString().concat("(待审核)"))).collect(Collectors.joining(",")));
                        });
                    }
                    //移交审批人
                    List<Map> mainNodeMap = projectCluesDao.getMainNodeMoveInfo(map.getFollowRecordId());
                    if(!CollectionUtils.isEmpty(mainNodeMap)){
                        if(StringUtils.isEmpty(map.getApproveUser())) {
                            mainNodeMap.stream().forEach(x->{
                                map.setApproveUser(map.getApproveUser().concat(x.get("salesName")+"(待审核)"));
                            });
                        }else {
                            mainNodeMap.stream().forEach(x->{
                                map.setApproveUser(map.getApproveUser().concat(","+ x.get("salesName")+"(待审核)"));
                            });
                        }
                    }
                }
            }
        }
        Map resultMap = new HashMap<>();
        resultMap.put("list",list);
        resultMap.put("total",total);
        return ResultBody.success(resultMap);
//        return ResultBody.success(new PageInfo<>(list));
    }

    @Override
    public void getFollowUpRecordListExport(HttpServletRequest request, HttpServletResponse response, FollowUpRecordVO followUpRecordVO) throws ParseException {
        List<Map> fileds = followUpRecordVO.getFileds();
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        List<FollowUpRecordVO> followUpRecordList = new ArrayList<>();
        if (followUpRecordVO.getSearch() != null && !"".equals(followUpRecordVO.getSearch())){
            String search = followUpRecordVO.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                followUpRecordVO.setCustomerMobile(search);
            } else {
                followUpRecordVO.setCustomerName(search);
            }
        }
        //处理核验状态
        if(StringUtils.isNotEmpty(followUpRecordVO.getVerificationStatus())){
            if("0".equals(followUpRecordVO.getVerificationStatus())){//全部
                followUpRecordVO.setVerificationStatusList(Arrays.asList("1","2","0"));
            }else if("1".equals(followUpRecordVO.getVerificationStatus())){//未核验
                followUpRecordVO.setVerificationStatusList(Arrays.asList("0"));
            }else if("2".equals(followUpRecordVO.getVerificationStatus())){//已核验合格
                followUpRecordVO.setVerificationStatusList(Arrays.asList("1"));
            }else if("3".equals(followUpRecordVO.getVerificationStatus())){//已核验不合格
                followUpRecordVO.setVerificationStatusList(Arrays.asList("2"));
            }
        }
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(userId,
                followUpRecordVO.getProjectList());
        followUpRecordVO.setOrgIds(orgIds);
        //导出的文档下面的名字
        String excelName = "跟进记录台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;

        if("clue".equals(followUpRecordVO.getDataSource())){
            followUpRecordList = projectCluesDao.getFollowUpRecordListClue(followUpRecordVO);
        } else{
            followUpRecordList = projectCluesDao.getFollowUpRecordList(followUpRecordVO);
        }

        if (followUpRecordList!=null && followUpRecordList.size()>0){
            for (FollowUpRecordVO map : followUpRecordList) {
                String enclosures = map.getEnclosures();
                String threeOnesUrls = map.getThreeOnesUrls();
                String drawingQuotationUrls = map.getDrawingQuotationUrls();
                if (!StringUtils.isEmpty(enclosures)){
                    String[] ss = enclosures.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    map.setEnclosure(enclosure);
                }
//                if (!StringUtils.isEmpty(threeOnesUrls)){
//                    String[] ss = threeOnesUrls.split(",");
//                    List<String> enclosure = Arrays.asList(ss);
//                    map.setThreeOnesUrl(enclosure);
//                }
                if (!StringUtils.isEmpty(drawingQuotationUrls)){
                    String[] ss = drawingQuotationUrls.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    map.setDrawingQuotationUrl(enclosure);
                }
                if(map.getStatus().equals("1") || map.getStatus().equals("4") || map.getStatus().equals("5")){
                    //节点移交人
                    List<Map> nodeMap = projectCluesDao.getFirstNodeMoveInfo(map.getFollowRecordId());
                    if(!CollectionUtils.isEmpty(nodeMap)){
                        nodeMap.stream().forEach(x->{
                            map.setApproveUser(Arrays.asList(map.getApproveUser().split(",")).stream().filter(user -> !user.equals(x.get("oldSalesName").toString().concat("(待审核)"))).collect(Collectors.joining(",")));
                        });
                    }
                    //移交审批人
                    List<Map> mainNodeMap = projectCluesDao.getMainNodeMoveInfo(map.getFollowRecordId());
                    if(!CollectionUtils.isEmpty(mainNodeMap)){
                        if(StringUtils.isEmpty(map.getApproveUser())) {
                            mainNodeMap.stream().forEach(x->{
                                map.setApproveUser(map.getApproveUser().concat(x.get("salesName")+"(待审核)"));
                            });
                        }else {
                            mainNodeMap.stream().forEach(x->{
                                map.setApproveUser(map.getApproveUser().concat(","+ x.get("salesName")+"(待审核)"));
                            });
                        }
                    }
                }
            }
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(followUpRecordVO.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(followUpRecordVO.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(followUpRecordVO));
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (followUpRecordList != null && followUpRecordList.size() > 0){
            String isAllStr = followUpRecordVO.getIsAll();
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (FollowUpRecordVO model : followUpRecordList) {
                model.setRownum(rowNum);
                Object[] oArray = model.toData1(isAll,filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getFollowUpRecordListExportNew(HttpServletRequest request, HttpServletResponse response, FollowUpRecordVO followUpRecordVO) throws ParseException {
        String userId = SecurityUtils.getUserId();
        //处理核验状态
        if(StringUtils.isNotEmpty(followUpRecordVO.getVerificationStatus())){
            if("0".equals(followUpRecordVO.getVerificationStatus())){//全部
                followUpRecordVO.setVerificationStatusList(Arrays.asList("1","2","0"));
            }else if("1".equals(followUpRecordVO.getVerificationStatus())){//未核验
                followUpRecordVO.setVerificationStatusList(Arrays.asList("0"));
            }else if("2".equals(followUpRecordVO.getVerificationStatus())){//已核验合格
                followUpRecordVO.setVerificationStatusList(Arrays.asList("1"));
            }else if("3".equals(followUpRecordVO.getVerificationStatus())){//已核验不合格
                followUpRecordVO.setVerificationStatusList(Arrays.asList("2"));
            }
        }
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(userId,
                followUpRecordVO.getProjectList());
        followUpRecordVO.setOrgIds(orgIds);
        //导出的文档下面的名字
        String excelName = "跟进记录台账";
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("FU1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType(followUpRecordVO.getIsAll());//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(followUpRecordVO.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(followUpRecordVO.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(followUpRecordVO));
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

    @Override
    public ResultBody getCommonDict(String parentCode) {
        List<Map> list = projectCluesDao.getCommonDict(parentCode);
        return ResultBody.success(list);
    }

    @Override
    public ResultBody getMaxProJobInsFollowUper(Map map) {
        final String[] jobCode = {""};
        final String[] followUpUserRole = {""};
        final String[] orgId = {""};
        final String[] orgName = {""};
        final String[] isPost = {""};
        map.put("userName",SecurityUtils.getUsername());
        map.put("folPprojectId",map.get("projectId"));
        //获取登录人在当前项目下的岗位
        List<Map> jobList = workbenchMapper.findUserJobAllInfo(map);
        if(CollectionUtils.isEmpty(jobList)){
            return ResultBody.error(-10001,"岗位配置异常 无法发起跟进");
        }else {
            jobList.stream().forEach(x->{
                String newJobCode = x.get("jobCode")+"";
               if("zygw".equals(newJobCode) && (StringUtils.isEmpty(jobCode[0]) || jobCode[0].equals("zygw"))){
                   jobCode[0] = x.get("jobCode")+"";
                   followUpUserRole[0] = "1";
                   orgId[0] = x.get("orgId")+"";
                   orgName[0] = x.get("orgName")+"";
                   isPost[0] = x.get("isPost")+"";
               }else if("qyzygw".equals(newJobCode) && (StringUtils.isEmpty(jobCode[0]) || jobCode[0].equals("qyzygw"))){
                   jobCode[0] = x.get("jobCode")+"";
                   followUpUserRole[0] = "2";
                   orgId[0] = x.get("orgId")+"";
                   orgName[0] = x.get("orgName")+"";
                   isPost[0] = x.get("isPost")+"";
               }else if("xsjl".equals(newJobCode) && (StringUtils.isEmpty(jobCode[0]) || jobCode[0].equals("zygw") || jobCode[0].equals("qyzygw"))){
                   jobCode[0] = x.get("jobCode")+"";
                   followUpUserRole[0] = "5";
                   orgId[0] = x.get("orgId")+"";
                   orgName[0] = x.get("orgName")+"";
                   isPost[0] = x.get("isPost")+"";
               }else if("yxjl".equals(newJobCode) && (StringUtils.isEmpty(jobCode[0]) || jobCode[0].equals("zygw") || jobCode[0].equals("qyzygw") || jobCode[0].equals("xsjl"))){
                   jobCode[0] = x.get("jobCode")+"";
                   followUpUserRole[0] = "6";
                   isPost[0] = x.get("isPost")+"";
               }else if("zszj".equals(newJobCode) && (StringUtils.isEmpty(jobCode[0]) || jobCode[0].equals("zygw") || jobCode[0].equals("qyzygw") || jobCode[0].equals("xsjl") || jobCode[0].equals("yxjl"))){
                   jobCode[0] = x.get("jobCode")+"";
                   followUpUserRole[0] = "7";
                   orgId[0] = x.get("orgId")+"";
                   orgName[0] = x.get("orgName")+"";
                   isPost[0] = x.get("isPost")+"";
               }
            });
        }
        map.put("followUpUserRole", followUpUserRole[0]);
        map.put("jobCode", jobCode[0]);
        map.put("jobOrgID", orgId[0]);
        map.put("orgName", orgName[0]);
        map.put("isPost", isPost[0]);
        map.put("userId", SecurityUtils.getUserId());
        map.put("employeeName", SecurityUtils.getEmployeeName());
        return StringUtils.isEmpty(followUpUserRole[0]) ? ResultBody.error(-10001,"岗位配置异常 无法发起跟进") : ResultBody.success(map);
    }

    /**
     * 获取登录人权限内可分配客户的置业顾问
     * */
    @Override
    public ResultBody getGlAllocationPropertyConsultantZygw(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageNum")!=null){
            pageIndex = Integer.parseInt(map.get("pageNum")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        map.put("isManager", "0");
        //获取当前登录人是否存在可分配客户的权限
        List<Map> list = projectCluesDao.getAllocationAllCustomerUserOrgInfo(SecurityUtils.getUserId());
        if(CollectionUtils.isEmpty(list)){
            return ResultBody.error(-1000_01,"暂无分配客户权限！");
        }
        List<String> proList = new ArrayList<>();//项目总监
        List<String> qyProList = new ArrayList<>();//区域总监
        //判断是否存在管理员权限
        final boolean[] isManager = {false};
        list.stream().forEach(x->{
            if("10001".equals(x.get("jobCode"))){
                isManager[0] = true;
            }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                proList.add(x.get("projectId")+"");
            }else if("qyzszj".equals(x.get("jobCode"))){
                qyProList.add(x.get("projectId")+"");
            }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                String comGUID = x.get("areaId")+"";
                proList.addAll(projectCluesDao.getProListD(comGUID));
                qyProList.addAll(projectCluesDao.getProList(comGUID));
            }
        });
        //获取权限内的权限专员 按区域 项目 团队 专员 分组
        if(isManager[0]){//管理员 可分配全系统人员
            map.put("isManager", "1");
        }else {//按权限查询
            map.put("proList", proList);
            map.put("qyProList", qyProList);
            //限制经理、总监、营销经理
            List<String> proIds = new ArrayList<>();
            proIds.addAll(proList);
            proIds.addAll(qyProList);
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                    proIds);
            map.put("orgIds",orgIds);
        }
        PageHelper.startPage(pageIndex, pageSize);
        List<Map> reList = projectCluesDao.getGlAllocationPropertyConsultantZygw(map);
        return ResultBody.success(new PageInfo<>(reList));
    }

    /**
     * 分配置业顾问新
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody allocationPropertyConsultantNew(SalesAttributionForm salesAttributionForm) {
        try {
            if (StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId()) && (salesAttributionForm.getProjectClueList()==null || salesAttributionForm.getProjectClueList().size()==0)){
                return ResultBody.error(2001, "请选择需分配的客户！");
            }
            List<String> oppIds = new ArrayList<>();
            if (!StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId())){
                oppIds.add(salesAttributionForm.getOpportunityClueId());
            }else{
                oppIds = salesAttributionForm.getProjectClueList();
            }
            String sendTime = "";
            String sendTimeStr = "";
            String sendCustomerName = "";
            String sendCustomerOppId = "";
            String sendCustomerClueId = "";
            String result = "分配成功";
            boolean resFlag = false;
            if (oppIds.size()>0){
                String jobCode = salesAttributionForm.getJobCode();
                String projectId = salesAttributionForm.getProjectId();
                String projectName = salesAttributionForm.getProjectName();
                String salesAttributionId = salesAttributionForm.getSalesAttributionId();
                String salesAttributionName = salesAttributionForm.getSalesAttributionName();

                Map param = new HashMap();
                param.put("userId", salesAttributionId);
                param.put("projectId", projectId);

                param.put("jobCode",jobCode);
                Map orgMap = projectCluesDao.getUserOrg(param);
                String reportUserRole = "";
                if ("zygw".equals(jobCode)){
                    reportUserRole = "1";
                }
                if ("qyzygw".equals(jobCode)){
                    reportUserRole = "2";
                }
                //判断系统配置规则
                ProjectRuleDetail projectRuleDetailXt = projectCluesDao.selectProjectRuleZs("-1","2");
                Map map = new HashMap();
                //查询项目规则
                String ComGUID = projectCluesDao.getComGUIDByProject(projectId);// 区域ID
                //查询规则计算报备逾期及预警时间
                ProjectRuleDetail projectRuleDetail = null;
                if ("1".equals(reportUserRole)){
                    projectRuleDetail = projectCluesDao.selectProjectRuleZs(projectId,"2");
                    //项目没有配置规则 查询区域的
                    if (projectRuleDetail==null){
                        projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
                    }
                }else if ("2".equals(reportUserRole)){
                    projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
                }
                //没有区域的查询集团的
                if (projectRuleDetail==null){
                    projectRuleDetail = projectRuleDetailXt;
                }
//                String reportExpireDaysStr = "";
//                int reportExpireDays = 0;
//                String reportDaysWarningStr = "";
//                int reportDaysWarning = 0;
//                if (projectRuleDetail!=null){
//                    reportExpireDaysStr = projectRuleDetail.getReportExpireDays();
//                    if (StringUtils.isNotEmpty(reportExpireDaysStr)){
//                        reportExpireDays = Integer.parseInt(reportExpireDaysStr);
//                    }
//                    reportDaysWarningStr = projectRuleDetail.getReportDaysWarning();
//                    if (StringUtils.isNotEmpty(reportDaysWarningStr)){
//                        reportDaysWarning = Integer.parseInt(reportDaysWarningStr);
//                    }
//                }
                //分配保护时间
                String assignsExpireDaysStr = "";
                int assignsExpireDays = 0;
                if (projectRuleDetailXt!=null) {
                    assignsExpireDaysStr = projectRuleDetailXt.getAssignExpireDays();
                    if (StringUtils.isNotEmpty(assignsExpireDaysStr)){
                        assignsExpireDays = Integer.parseInt(assignsExpireDaysStr);
                    }
                }
                //查询规则计算跟进逾期及预警时间
//                if (StringUtils.isNotEmpty(reportExpireDaysStr)){
//                    Date dBefore = new Date();
//                    Calendar calendar = Calendar.getInstance(); //得到日历
//                    calendar.setTime(dBefore);//把当前时间赋给日历
//                    calendar.add(Calendar.DAY_OF_MONTH, reportExpireDays);
//                    dBefore = calendar.getTime();
//                    map.put("salesFollowExpireDate",sf.format(dBefore));
//                }else{
//                    map.put("salesFollowExpireDate",null);
//                }
//                if (StringUtils.isNotEmpty(reportDaysWarningStr)){
//                    Date dBefore = new Date();
//                    Calendar calendar = Calendar.getInstance(); //得到日历
//                    calendar.setTime(dBefore);//把当前时间赋给日历
//                    calendar.add(Calendar.DAY_OF_MONTH, reportDaysWarning);
//                    dBefore = calendar.getTime();
//                    map.put("salesFollowExpireWarningDate",sf.format(dBefore));
//                }else{
//                    map.put("salesFollowExpireWarningDate",null);
//                }
                if (StringUtils.isNotEmpty(assignsExpireDaysStr)){
                    Date today = new Date();
                    Date dBefore = new Date();
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(today);//把当前时间赋给日历
                    calendar.add(Calendar.DAY_OF_MONTH, assignsExpireDays);
                    dBefore = calendar.getTime();
                    map.put("salesFollowExpireDate", sf.format(dBefore));
                    sendTime = sf.format(dBefore);
                    sendTimeStr = assignsExpireDaysStr;
                }else{
                    map.put("salesFollowExpireDate", null);
                    sendTime = "无";
                    sendTimeStr = "永久";
                }
                //查询有分配权限的项目
                List<String> qxProList = new ArrayList<>();
                List<Map> list = projectCluesDao.getAllocationAllCustomerUserOrgInfo(SecurityUtils.getUserId());
                //判断是否存在管理员权限
                final boolean[] isManager = {false};
                list.stream().forEach(x->{
                    if("10001".equals(x.get("jobCode"))){
                        isManager[0] = true;
                    }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                        qxProList.add(x.get("projectId")+"");
                    }else if("qyzszj".equals(x.get("jobCode"))){
                        qxProList.add(x.get("projectId")+"");
                    }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                        String comGUID = x.get("areaId")+"";
                        qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                    }
                });
                //可分配的机会ID
                List<String> okOppIds = new ArrayList<>();
                List<String> notOppIds = new ArrayList<>();
                //不可分配的机会ID 无分配权限
                List<String> notQxOppIds = new ArrayList<>();
                //不可分配的机会ID 客户规则判重
                List<String> notPcProOppIds = new ArrayList<>();
                List<String> notPcRegionOppIds = new ArrayList<>();
                List<String> notPcPoolOppIds = new ArrayList<>();
                List<String> notPcProRelaOppIds = new ArrayList<>();
                List<String> notPcRegionRelaOppIds = new ArrayList<>();
                //不可分配的机会ID 无需分配
                List<String> notNeedOppIds = new ArrayList<>();
                for (String OpportunityClueId:oppIds) {
                    //获取机会信息
                    Map oldMap = projectCluesDao.getOpportunityById(OpportunityClueId);
                    //判断客户是否超过了过保时间
                    try {
                        if(!"8".equals(String.valueOf(oldMap.get("ClueStatus"))) && ObjectUtils.isNotEmpty(oldMap.get("SalesFollowExpireDate")) && sf.parse(String.valueOf(oldMap.get("SalesFollowExpireDate"))).before(new Date())){
                            notOppIds.add(OpportunityClueId);
                            continue;
                        }
                        //判断客户是否过保/作废
                        if ("9".equals(String.valueOf(oldMap.get("ClueStatus"))) || "10".equals(String.valueOf(oldMap.get("ClueStatus")))){
                            notOppIds.add(OpportunityClueId);
                            continue;
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    //判断是否存在分配权限
                    if (!isManager[0] && !qxProList.contains(oldMap.get("projectId")+"")){
                        notQxOppIds.add(OpportunityClueId);
                        continue;
                    }
                    //判断是否本项目分配客户
                    boolean isNotOK = false;
                    String notPCType = "";
                    if(projectId.equals(String.valueOf(oldMap.get("projectId")))){
                        //相同项目判断是不是相同专员 相同专员无需分配 不同专员内部调整 无需判重
                        if(salesAttributionId.equals(String.valueOf(oldMap.get("SalesAttributionId")))){
                            notNeedOppIds.add(OpportunityClueId);
                            continue;
                        }
                    }else {
                        //判断是否可分配
                        Map map1 = new HashMap();
                        map1.put("projectId",projectId);
                        map1.put("customerMobile",oldMap.get("CustomerMobile"));
                        map1.put("customerName",oldMap.get("CustomerName"));
                        map1.put("opportunityClueId",oldMap.get("OpportunityClueId"));
                        //原机会的项目ID
                        String oldProjectId = oldMap.get("projectId")+"";
                        //处理项目联动
                        List<String> proList = new ArrayList<>();
                        String proIds = projectCluesDao.getTranslateProIds(projectId);
                        if(StringUtils.isNotEmpty(proIds)){
                            proList = new ArrayList(Arrays.asList(proIds.split(",")));
                        }
                        //不管有无联动项目 保证原项目存在
                        proList.add(projectId);
                        map1.put("proList",proList);
                        map1.put("judgeNoPool",projectRuleDetailXt.getJudgeNoPool());
                        map1.put("judgeNoRegion",projectRuleDetailXt.getJudgeNoRegion());
                        //查询是否存在机会
                        List<Map> opps = new ArrayList<>();
                        if("0".equals(projectRuleDetailXt.getJudgeStage())){
                            opps = projectCluesDao.getCstIsOkReferral(map1);
                        }else if("1".equals(projectRuleDetailXt.getJudgeStage())){
                            opps = projectCluesDao.getCstIsOkComeVisit(map1);
                        }else if("2".equals(projectRuleDetailXt.getJudgeStage())){
                            opps = projectCluesDao.getCstIsOkTrade(map1);
                        }else {
                            return ResultBody.error(-10002,"系统配置异常");
                        }
                        String type = "";
                        boolean flag1 = false;
                        for (Map m:opps) {
                            int cout = Integer.parseInt(m.get("count")+"");
                            if (cout>0){
                                type = m.get("type")+"";
                                //获取重复客户 判断客户对应类型的规则 万企通走万企通配置 转介走转介保护期
                                map1.put("type",type);
                                List<Map> cusOpps = projectCluesDao.getCstIsOkRepeat(map1);
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
                                                type = m1.get("type")+"";
                                                flag1 = true;
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
                                                type = m2.get("type")+"";
                                                flag1 = true;
                                                break;
                                            }
                                        }
                                    }else if("3".equals(sourceMode)){//案场客户
                                        flag1 = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (flag1){
                            if ("1".equals(type)){
                                if (!oldProjectId.equals(projectId)){
                                    isNotOK = true;
                                    notPCType = type;
                                }
                            }else if ("pro".equals(type)){
                                isNotOK = true;
                                notPCType = type;
                            }else if ("region".equals(type)){
                                isNotOK = true;
                                notPCType = type;
                            }else if ("proRelate".equals(type)){
                                isNotOK = true;
                                notPCType = type;
                            }else if ("regionRelate".equals(type)){
                                isNotOK = true;
                                notPCType = type;
                            }
                        }
                    }
                    if (isNotOK){
                        if ("1".equals(notPCType)){
                            notPcPoolOppIds.add(OpportunityClueId);
                        }else if ("pro".equals(notPCType)){
                            notPcProOppIds.add(OpportunityClueId);
                        }else if ("region".equals(notPCType)){
                            notPcRegionOppIds.add(OpportunityClueId);
                        }else if ("proRelate".equals(notPCType)){
                            notPcProRelaOppIds.add(OpportunityClueId);
                        }else if ("regionRelate".equals(notPCType)){
                            notPcRegionRelaOppIds.add(OpportunityClueId);
                        }
                        continue;
                    }else{
                        okOppIds.add(OpportunityClueId);
                    }
                    oldMap.put("ConfirmID",SecurityUtils.getUserId());
                    oldMap.put("ConfirmTime",new Date());
                    oldMap.put("ApplyDatetime",new Date());
                    oldMap.put("OldSalesId",oldMap.get("SalesAttributionId"));
                    oldMap.put("OldSalesName",oldMap.get("SalesAttributionName"));
                    oldMap.put("SalesId",salesAttributionId);
                    oldMap.put("SalesName",salesAttributionName);
                    oldMap.put("Type","1");
                    oldMap.put("mainProjectId",projectId);
                    oldMap.put("mainProjectName",projectName);
                    //保存分配记录
                    projectCluesDao.addRelCustomerRecord(oldMap);

                    //生成消息
                    //获取客户原信息
                    ReferralVo referralVo = projectCluesDao.getOldOpportunityClueInfo(OpportunityClueId);
                    //发起人
                    Message message = new Message();
                    message.setSubject("【分配客户通知】");
                    message.setContent("【分配客户通知】您的客户：【" + referralVo.getCustomerName() + "】已重新分配，请知悉。");
                    message.setSender("");
                    message.setMessageType(2106);
                    message.setIsDel(0);
                    message.setReceiver(referralVo.getSalesAttributionId());
                    message.setIsRead(0);
                    message.setIsPush(2);
                    message.setIsNeedPush(2);
                    message.setProjectClueId(oldMap.get("ProjectClueId")+"");
                    message.setOpportunityClueId(OpportunityClueId);
                    message.setProjectId(salesAttributionForm.getProjectId());
                    projectCluesDao.insertOneMessage(message);

                    map.put("OpportunityClueId",OpportunityClueId);
                    map.put("salesAttributionId",salesAttributionId);
                    map.put("salesAttributionName",salesAttributionName);
                    map.put("salesAttributionTeamId",orgMap.get("orgId"));
                    map.put("salesAttributionTeamName",orgMap.get("orgName"));
                    map.put("ProjectClueId",oldMap.get("ProjectClueId"));
                    map.put("projectId",projectId);
                    map.put("projectName",projectName);
                    if(!"8".equals(oldMap.get("ClueStatus")+"")){
                        map.put("ClueStatus","1");
                        map.put("salesTheFirstFollowDate","null");
                        //新逻辑 字段改为首次三个一时间 不重置
//                        map.put("isThreeOnesDate","null");
                    }else {
                        map.put("ClueStatus","");
                        map.put("salesTheFirstFollowDate","");
                        //新逻辑 字段改为首次三个一时间 不重置
//                        map.put("isThreeOnesDate","");
                    }
                    map.put("ReportCreateTime","1");
                    projectCluesDao.updateOppCst(map);

                    //新增客户报备日志记录
                    CustomerAddLogVo customerAddLogVo = new CustomerAddLogVo();
                    customerAddLogVo.setAreaId(projectCluesDao.getComGUIDByProject(map.get("projectId")+""));
                    customerAddLogVo.setProjectId(map.get("projectId")+"");
                    customerAddLogVo.setOpportunityClueId(map.get("OpportunityClueId")+"");
                    customerAddLogVo.setProjectClueId(map.get("ProjectClueId")+"");
                    customerAddLogVo.setCustomerName(oldMap.get("CustomerName")+"");
                    customerAddLogVo.setCustomerMobile(oldMap.get("CustomerMobile")+"");
                    customerAddLogVo.setSalesAttributionId(map.get("salesAttributionId")+"");
                    customerAddLogVo.setSalesAttributionName(map.get("salesAttributionName")+"");
                    customerAddLogVo.setSalesAttributionTeamId(map.get("salesAttributionTeamId")+"");
                    customerAddLogVo.setSalesAttributionTeamName(map.get("salesAttributionTeamName")+"");
                    //获取历史该客户信息
                    customerAddLogVo.setIsThreeOnes(oldMap.get("IsThreeOnes")+"");
                    customerAddLogVo.setIsThreeOnesDate(oldMap.get("IsThreeOnesDate")+"");
                    customerAddLogVo.setAddType("5");
                    //判断客户是否报备过该项目
                    String isAdd = projectCluesDao.getCustomerAddLogToIsAdd(customerAddLogVo);
                    customerAddLogVo.setIsAdd(isAdd);
                    customerAddLogVo.setReportCreateTime(sf.format(DateUtil.date()));
                    customerAddLogVo.setIsEffective("1");
                    //设置历史该客户状态
                    projectCluesDao.disableCutomerAddLog(customerAddLogVo);
                    //保存客户报备日志
                    projectCluesDao.saveCustomerAddLog(customerAddLogVo);

                    //保存成功分配客户名称
                    sendCustomerName = referralVo.getCustomerName();
                    sendCustomerOppId = referralVo.getOpportunityClueId();
                    sendCustomerClueId = referralVo.getProjectClueId();
                }
                if (okOppIds.size()>0) {
                    if (okOppIds.size() == 1) {
                        sendCustomerName = "新的客户【" + sendCustomerName + "】";
                    } else {
                        sendCustomerName = okOppIds.size() + "位新的客户";
                        sendCustomerOppId = "";
                        sendCustomerClueId = "";
                    }
                    //接收人
                    Message message = new Message();
                    message.setSubject("【分配客户通知】");
                    String content;
                    if("无".equals(sendTime)){
                        content = "【分配客户通知】您已被分配"+sendCustomerName+"，客户为永久保护，请及时跟进并审核完成！";
                        message.setContent("【分配客户通知】 您已被分配"+sendCustomerName+"，客户为永久保护，请及时跟进并审核完成！");
                    }else {
                        content = "【分配客户通知】您已被分配"+sendCustomerName+"，请在"+sendTime+"天内及时跟进并审核完成，否则将自动掉回公客池，过保时间为："+sendTime+"。";
                        message.setContent("【分配客户通知】 您已被分配" + sendCustomerName + "，请在"+sendTimeStr+"天内及时跟进并审核完成，否则将自动掉回公客池，过保时间为："+sendTime+"。");
                    }
                    message.setSender("");
                    message.setMessageType(2106);
                    message.setIsDel(0);
                    message.setReceiver(salesAttributionId);
                    message.setIsRead(0);
                    message.setIsPush(2);
                    message.setIsNeedPush(2);
                    message.setProjectClueId(sendCustomerClueId);
                    message.setOpportunityClueId(sendCustomerOppId);
                    message.setProjectId(salesAttributionForm.getProjectId());
                    projectCluesDao.insertOneMessage(message);
                    //查询手机号
                    Map mobileMap = messageMapper.getUserMobile(salesAttributionId);
                    if (mobileMap != null && mobileMap.get("Mobile") != null && !StringUtils.isEmpty(sendTime)){
                        //发送短信
                        try {
                            if(!"无".equals(sendTime)){
                                String sss = URLEncoder.encode(sendCustomerName+"|"+sendTimeStr+"|"+sfDa.format(sf.parse(sendTime))+"|"+sfTi.format(sf.parse(sendTime)),"UTF-8");
                                String userName = mobileMap.get("UserName")+"";
                                //发送短信改为发送OA
                                content = content.replaceAll(" ","_");
                                if(isSendOAMessage==1){
                                    HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url=&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                                }
//                                HttpRequestUtil.httpGet("http://esb.vanyang.com.cn/sms/sendSMSMessageV1?sys_code=ydac&login_name=ydac&password=123456&modeId=000044&vars="+sss+"&mobile="+mobile+"&sendTime&notifyUrl=http://www.baidu.com&sysName=移动案场系统",false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (notNeedOppIds.size()>0 || notQxOppIds.size()>0 || notPcPoolOppIds.size()>0 || notPcProOppIds.size()>0 || notPcRegionOppIds.size()>0 || notPcProRelaOppIds.size()>0 || notPcRegionRelaOppIds.size()>0 || notOppIds.size()>0){
                    resFlag = true;
                    result = result.concat(okOppIds.size()+"条,其中");
                }
                if (notNeedOppIds.size()>0){
                    result = result.concat(notNeedOppIds.size()+"个客户无需分配,");
                }
                if (notQxOppIds.size()>0){
                    result = result.concat(notQxOppIds.size()+"个客户无权限分配,");
                }
                if (notPcPoolOppIds.size()>0){
                    result = result.concat(notPcPoolOppIds.size()+"个客户公客池已存在无法分配,");
                }
                if (notPcProOppIds.size()>0){
                    result = result.concat(notPcProOppIds.size()+"个客户项目上已存在无法分配,");
                }
                if (notPcRegionOppIds.size()>0){
                    result = result.concat(notPcRegionOppIds.size()+"个客户区域上已存在无法分配,");
                }
                if (notPcProRelaOppIds.size()>0){
                    result = result.concat(notPcProRelaOppIds.size()+"个客户项目已关联无法分配,");
                }
                if (notPcRegionRelaOppIds.size()>0){
                    result = result.concat(notPcRegionRelaOppIds.size()+"个客户区域已关联无法分配,");
                }
                if (notOppIds.size()>0){
                    result = result.concat(notOppIds.size()+"个客户超出过保时间无法分配,");
                }
            }
            if(resFlag){
                result = result.substring(0,result.length()-1).concat("！");
            }else {
                result = result.concat("！");
            }
            return ResultBody.success(result);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BadRequestException(-11_1058, "分配失败！", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody transferPropertyConsultant(SalesAttributionForm salesAttributionForm) {
        try {
            if (StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId()) && (salesAttributionForm.getProjectClueList()==null || salesAttributionForm.getProjectClueList().size()==0)){
                return ResultBody.error(2001, "请选择需分配的客户！");
            }
            List<String> oppIds = new ArrayList<>();
            if (!StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId())){
                oppIds.add(salesAttributionForm.getOpportunityClueId());
            }else{
                oppIds = salesAttributionForm.getProjectClueList();
            }
            String sendTime = "";
            String sendTimeStr = "";
            String sendCustomerName = "";
            String sendCustomerOppId = "";
            String sendCustomerClueId = "";
            String result = "分配成功";
            boolean resFlag = false;
            if (oppIds.size()>0){
                String jobCode = salesAttributionForm.getJobCode();
                String projectId = salesAttributionForm.getProjectId();
                String projectName = salesAttributionForm.getProjectName();
                String salesAttributionId = salesAttributionForm.getSalesAttributionId();
                String salesAttributionName = salesAttributionForm.getSalesAttributionName();

                Map param = new HashMap();
                param.put("userId", salesAttributionId);
                param.put("projectId", projectId);

                param.put("jobCode",jobCode);
                Map orgMap = projectCluesDao.getUserOrg(param);
                //判断系统配置规则
                ProjectRuleDetail projectRuleDetailXt = projectCluesDao.selectProjectRuleZs("-1","2");
                Map map = new HashMap();
                //查询有分配权限的项目
                List<String> qxProList = new ArrayList<>();
                List<Map> list = projectCluesDao.getAllocationAllCustomerUserOrgInfo(SecurityUtils.getUserId());
                //判断是否存在管理员权限
                final boolean[] isManager = {false};
                list.stream().forEach(x->{
                    if("10001".equals(x.get("jobCode"))){
                        isManager[0] = true;
                    }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                        qxProList.add(x.get("projectId")+"");
                    }else if("qyzszj".equals(x.get("jobCode"))){
                        qxProList.add(x.get("projectId")+"");
                    }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                        String comGUID = x.get("areaId")+"";
                        qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                    }
                });
                //可分配的机会ID
                List<String> okOppIds = new ArrayList<>();
                List<String> notOppIds = new ArrayList<>();
                //不可分配的机会ID 无分配权限
                List<String> notQxOppIds = new ArrayList<>();
                //不可分配的机会ID 客户规则判重
                List<String> notPcProOppIds = new ArrayList<>();
                List<String> notPcRegionOppIds = new ArrayList<>();
                List<String> notPcPoolOppIds = new ArrayList<>();
                List<String> notPcProRelaOppIds = new ArrayList<>();
                List<String> notPcRegionRelaOppIds = new ArrayList<>();
                //不可分配的机会ID 无需分配
                List<String> notNeedOppIds = new ArrayList<>();
                for (String OpportunityClueId:oppIds) {
                    //获取机会信息
                    Map oldMap = projectCluesDao.getOpportunityById(OpportunityClueId);
                    //判断客户是否超过了过保时间
                    try {
                        if(!"8".equals(String.valueOf(oldMap.get("ClueStatus"))) && ObjectUtils.isNotEmpty(oldMap.get("SalesFollowExpireDate")) && sf.parse(String.valueOf(oldMap.get("SalesFollowExpireDate"))).before(new Date())){
                            notOppIds.add(OpportunityClueId);
                            continue;
                        }
                        //判断客户是否过保/作废
                        if ("9".equals(String.valueOf(oldMap.get("ClueStatus"))) || "10".equals(String.valueOf(oldMap.get("ClueStatus")))){
                            notOppIds.add(OpportunityClueId);
                            continue;
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    //判断是否存在分配权限
                    if (!isManager[0] && !qxProList.contains(oldMap.get("projectId")+"")){
                        notQxOppIds.add(OpportunityClueId);
                        continue;
                    }
                    //判断是否本项目分配客户
                    boolean isNotOK = false;
                    String notPCType = "";
                    if(projectId.equals(String.valueOf(oldMap.get("projectId")))){
                        //相同项目判断是不是相同专员 相同专员无需分配 不同专员内部调整 无需判重
                        if(salesAttributionId.equals(String.valueOf(oldMap.get("SalesAttributionId")))){
                            notNeedOppIds.add(OpportunityClueId);
                            continue;
                        }
                    }else {
                        //判断是否可分配
                        Map map1 = new HashMap();
                        map1.put("projectId",projectId);
                        map1.put("customerMobile",oldMap.get("CustomerMobile"));
                        map1.put("customerName",oldMap.get("CustomerName"));
                        map1.put("opportunityClueId",oldMap.get("OpportunityClueId"));
                        //原机会的项目ID
                        String oldProjectId = oldMap.get("projectId")+"";
                        //处理项目联动
                        List<String> proList = new ArrayList<>();
                        String proIds = projectCluesDao.getTranslateProIds(projectId);
                        if(StringUtils.isNotEmpty(proIds)){
                            proList = new ArrayList(Arrays.asList(proIds.split(",")));
                        }
                        //不管有无联动项目 保证原项目存在
                        proList.add(projectId);
                        map1.put("proList",proList);
                        map1.put("judgeNoPool",projectRuleDetailXt.getJudgeNoPool());
                        map1.put("judgeNoRegion",projectRuleDetailXt.getJudgeNoRegion());
                        //查询是否存在机会
                        List<Map> opps = new ArrayList<>();
                        if("0".equals(projectRuleDetailXt.getJudgeStage())){
                            opps = projectCluesDao.getCstIsOkReferral(map1);
                        }else if("1".equals(projectRuleDetailXt.getJudgeStage())){
                            opps = projectCluesDao.getCstIsOkComeVisit(map1);
                        }else if("2".equals(projectRuleDetailXt.getJudgeStage())){
                            opps = projectCluesDao.getCstIsOkTrade(map1);
                        }else {
                            return ResultBody.error(-10002,"系统配置异常");
                        }
                        String type = "";
                        boolean flag1 = false;
                        for (Map m:opps) {
                            int cout = Integer.parseInt(m.get("count")+"");
                            if (cout>0){
                                type = m.get("type")+"";
                                //获取重复客户 判断客户对应类型的规则 万企通走万企通配置 转介走转介保护期
                                map1.put("type",type);
                                List<Map> cusOpps = projectCluesDao.getCstIsOkRepeat(map1);
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
                                                type = m1.get("type")+"";
                                                flag1 = true;
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
                                                type = m2.get("type")+"";
                                                flag1 = true;
                                                break;
                                            }
                                        }
                                    }else if("3".equals(sourceMode)){//案场客户
                                        flag1 = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (flag1){
                            if ("1".equals(type)){
                                if (!oldProjectId.equals(projectId)){
                                    isNotOK = true;
                                    notPCType = type;
                                }
                            }else if ("pro".equals(type)){
                                isNotOK = true;
                                notPCType = type;
                            }else if ("region".equals(type)){
                                isNotOK = true;
                                notPCType = type;
                            }else if ("proRelate".equals(type)){
                                isNotOK = true;
                                notPCType = type;
                            }else if ("regionRelate".equals(type)){
                                isNotOK = true;
                                notPCType = type;
                            }
                        }
                    }
                    if (isNotOK){
                        if ("1".equals(notPCType)){
                            notPcPoolOppIds.add(OpportunityClueId);
                        }else if ("pro".equals(notPCType)){
                            notPcProOppIds.add(OpportunityClueId);
                        }else if ("region".equals(notPCType)){
                            notPcRegionOppIds.add(OpportunityClueId);
                        }else if ("proRelate".equals(notPCType)){
                            notPcProRelaOppIds.add(OpportunityClueId);
                        }else if ("regionRelate".equals(notPCType)){
                            notPcRegionRelaOppIds.add(OpportunityClueId);
                        }
                        continue;
                    }else{
                        okOppIds.add(OpportunityClueId);
                    }
                    oldMap.put("ConfirmID",SecurityUtils.getUserId());
                    oldMap.put("ConfirmTime",new Date());
                    oldMap.put("ApplyDatetime",new Date());
                    oldMap.put("OldSalesId",oldMap.get("SalesAttributionId"));
                    oldMap.put("OldSalesName",oldMap.get("SalesAttributionName"));
                    oldMap.put("SalesId",salesAttributionId);
                    oldMap.put("SalesName",salesAttributionName);
                    oldMap.put("Type","1");
                    oldMap.put("mainProjectId",projectId);
                    oldMap.put("mainProjectName",projectName);
                    //保存分配记录
                    projectCluesDao.addRelCustomerRecord(oldMap);

                    //生成消息
                    //获取客户原信息
                    ReferralVo referralVo = projectCluesDao.getOldOpportunityClueInfo(OpportunityClueId);
                    //发起人
                    Message message = new Message();
                    message.setSubject("【分配客户通知】");
                    message.setContent("【分配客户通知】您的客户：【" + referralVo.getCustomerName() + "】已重新分配，请知悉。");
                    message.setSender("");
                    message.setMessageType(2106);
                    message.setIsDel(0);
                    message.setReceiver(referralVo.getSalesAttributionId());
                    message.setIsRead(0);
                    message.setIsPush(2);
                    message.setIsNeedPush(2);
                    message.setProjectClueId(oldMap.get("ProjectClueId")+"");
                    message.setOpportunityClueId(OpportunityClueId);
                    message.setProjectId(salesAttributionForm.getProjectId());
                    projectCluesDao.insertOneMessage(message);

                    map.put("OpportunityClueId",OpportunityClueId);
                    map.put("salesAttributionId",salesAttributionId);
                    map.put("salesAttributionName",salesAttributionName);
                    map.put("salesAttributionTeamId",orgMap.get("orgId"));
                    map.put("salesAttributionTeamName",orgMap.get("orgName"));
                    map.put("ProjectClueId",oldMap.get("ProjectClueId"));
                    map.put("projectId",projectId);
                    map.put("projectName",projectName);
                    if(!"8".equals(oldMap.get("ClueStatus")+"")){
                        map.put("ClueStatus",null);
                        map.put("salesTheFirstFollowDate","null");
                        //新逻辑 字段改为首次三个一时间 不重置
//                        map.put("isThreeOnesDate","null");
                    }else {
                        map.put("ClueStatus",null);
                        map.put("salesTheFirstFollowDate","");
                        //新逻辑 字段改为首次三个一时间 不重置
//                        map.put("isThreeOnesDate","");
                    }
                    map.put("ReportCreateTime","1");
                    map.put("salesFollowExpireDate",oldMap.get("SalesFollowExpireDate"));
                    map.put("salesFollowExpireWarningDate",oldMap.get("SalesFollowExpireDate"));
                    if (ObjectUtils.isNotEmpty(oldMap.get("SalesFollowExpireDate"))){
                        sendTime = String.valueOf(oldMap.get("SalesFollowExpireDate"));
                        // 定义日期时间格式，包含毫秒部分
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                        // 解析字符串为 LocalDateTime 对象
                        LocalDateTime historicalDateTime = LocalDateTime.parse(sendTime, formatter);
                        // 从 LocalDateTime 中提取 LocalDate
                        LocalDate historicalDate = historicalDateTime.toLocalDate();
                        // 获取当前日期
                        LocalDate currentDate = LocalDate.now();
                        sendTimeStr = String.valueOf(ChronoUnit.DAYS.between(currentDate,historicalDate));
                    }else {
                        sendTime = "无";
                        sendTimeStr = "永久";
                    }
                    projectCluesDao.updateOppCst(map);

                    //新增客户报备日志记录
                    CustomerAddLogVo customerAddLogVo = new CustomerAddLogVo();
                    customerAddLogVo.setAreaId(projectCluesDao.getComGUIDByProject(map.get("projectId")+""));
                    customerAddLogVo.setProjectId(map.get("projectId")+"");
                    customerAddLogVo.setOpportunityClueId(map.get("OpportunityClueId")+"");
                    customerAddLogVo.setProjectClueId(map.get("ProjectClueId")+"");
                    customerAddLogVo.setCustomerName(oldMap.get("CustomerName")+"");
                    customerAddLogVo.setCustomerMobile(oldMap.get("CustomerMobile")+"");
                    customerAddLogVo.setSalesAttributionId(map.get("salesAttributionId")+"");
                    customerAddLogVo.setSalesAttributionName(map.get("salesAttributionName")+"");
                    customerAddLogVo.setSalesAttributionTeamId(map.get("salesAttributionTeamId")+"");
                    customerAddLogVo.setSalesAttributionTeamName(map.get("salesAttributionTeamName")+"");
                    //获取历史该客户信息
                    customerAddLogVo.setIsThreeOnes(oldMap.get("IsThreeOnes")+"");
                    customerAddLogVo.setIsThreeOnesDate(oldMap.get("IsThreeOnesDate")+"");
                    customerAddLogVo.setAddType("5");
                    //判断客户是否报备过该项目
                    String isAdd = projectCluesDao.getCustomerAddLogToIsAdd(customerAddLogVo);
                    customerAddLogVo.setIsAdd(isAdd);
                    customerAddLogVo.setReportCreateTime(sf.format(DateUtil.date()));
                    customerAddLogVo.setIsEffective("1");
                    //设置历史该客户状态
                    projectCluesDao.disableCutomerAddLog(customerAddLogVo);
                    //保存客户报备日志
                    projectCluesDao.saveCustomerAddLog(customerAddLogVo);

                    //保存成功分配客户名称
                    sendCustomerName = referralVo.getCustomerName();
                    sendCustomerOppId = referralVo.getOpportunityClueId();
                    sendCustomerClueId = referralVo.getProjectClueId();
                }
                if (okOppIds.size()>0) {
                    if (okOppIds.size() == 1) {
                        sendCustomerName = "新的客户【" + sendCustomerName + "】";
                    } else {
                        sendCustomerName = okOppIds.size() + "位新的客户";
                        sendCustomerOppId = "";
                        sendCustomerClueId = "";
                    }
                    //接收人
                    Message message = new Message();
                    message.setSubject("【分配客户通知】");
                    String content;
                    if("无".equals(sendTime)){
                        content = "【分配客户通知】您已被分配"+sendCustomerName+"，客户为永久保护，请及时跟进并审核完成！";
                        message.setContent("【分配客户通知】 您已被分配"+sendCustomerName+"，客户为永久保护，请及时跟进并审核完成！");
                    }else {
                        content = "【分配客户通知】您已被分配"+sendCustomerName+"，请在"+sendTime+"天内及时跟进并审核完成，否则将自动掉回公客池，过保时间为："+sendTime+"。";
                        message.setContent("【分配客户通知】 您已被分配" + sendCustomerName + "，请在"+sendTimeStr+"天内及时跟进并审核完成，否则将自动掉回公客池，过保时间为："+sendTime+"。");
                    }
                    message.setSender("");
                    message.setMessageType(2106);
                    message.setIsDel(0);
                    message.setReceiver(salesAttributionId);
                    message.setIsRead(0);
                    message.setIsPush(2);
                    message.setIsNeedPush(2);
                    message.setProjectClueId(sendCustomerClueId);
                    message.setOpportunityClueId(sendCustomerOppId);
                    message.setProjectId(salesAttributionForm.getProjectId());
                    projectCluesDao.insertOneMessage(message);
                    //查询手机号
                    Map mobileMap = messageMapper.getUserMobile(salesAttributionId);
                    if (mobileMap != null && mobileMap.get("Mobile") != null && !StringUtils.isEmpty(sendTime)){
                        //发送短信
                        try {
                            if(!"无".equals(sendTime)){
                                String sss = URLEncoder.encode(sendCustomerName+"|"+sendTimeStr+"|"+sfDa.format(sf.parse(sendTime))+"|"+sfTi.format(sf.parse(sendTime)),"UTF-8");
                                String userName = mobileMap.get("UserName")+"";
                                //发送短信改为发送OA
                                content = content.replaceAll(" ","_");
                                if(isSendOAMessage==1){
                                    HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url=&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                                }
//                                HttpRequestUtil.httpGet("http://esb.vanyang.com.cn/sms/sendSMSMessageV1?sys_code=ydac&login_name=ydac&password=123456&modeId=000044&vars="+sss+"&mobile="+mobile+"&sendTime&notifyUrl=http://www.baidu.com&sysName=移动案场系统",false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (notNeedOppIds.size()>0 || notQxOppIds.size()>0 || notPcPoolOppIds.size()>0 || notPcProOppIds.size()>0 || notPcRegionOppIds.size()>0 || notPcProRelaOppIds.size()>0 || notPcRegionRelaOppIds.size()>0 || notOppIds.size()>0){
                    resFlag = true;
                    result = result.concat(okOppIds.size()+"条,其中");
                }
                if (notNeedOppIds.size()>0){
                    result = result.concat(notNeedOppIds.size()+"个客户无需分配,");
                }
                if (notQxOppIds.size()>0){
                    result = result.concat(notQxOppIds.size()+"个客户无权限分配,");
                }
                if (notPcPoolOppIds.size()>0){
                    result = result.concat(notPcPoolOppIds.size()+"个客户公客池已存在无法分配,");
                }
                if (notPcProOppIds.size()>0){
                    result = result.concat(notPcProOppIds.size()+"个客户项目上已存在无法分配,");
                }
                if (notPcRegionOppIds.size()>0){
                    result = result.concat(notPcRegionOppIds.size()+"个客户区域上已存在无法分配,");
                }
                if (notPcProRelaOppIds.size()>0){
                    result = result.concat(notPcProRelaOppIds.size()+"个客户项目已关联无法分配,");
                }
                if (notPcRegionRelaOppIds.size()>0){
                    result = result.concat(notPcRegionRelaOppIds.size()+"个客户区域已关联无法分配,");
                }
                if (notOppIds.size()>0){
                    result = result.concat(notOppIds.size()+"个客户超出过保时间无法分配,");
                }
            }
            if(resFlag){
                result = result.substring(0,result.length()-1).concat("！");
            }else {
                result = result.concat("！");
            }
            return ResultBody.success(result);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BadRequestException(-11_1058, "分配失败！", e);
        }
    }

    public ResultBody getAllocationClueCustomerIsMax(String projectId,String userId,String jobCode,int dCount){
        String reportUserRole = "";
        if ("zygw".equals(jobCode)){
            reportUserRole = "1";
        }
        if ("qyzygw".equals(jobCode)){
            reportUserRole = "2";
        }
        //查询项目规则
        String ComGUID = projectCluesDao.getComGUIDByProject(projectId);// 区域ID
        //查询规则计算报备逾期及预警时间
        ProjectRuleDetail projectRuleDetail = null;
        if ("1".equals(reportUserRole)){
            projectRuleDetail = projectCluesDao.selectProjectRuleZs(projectId,"2");
            //项目没有配置规则 查询区域的
            if (projectRuleDetail==null){
                projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
            }
        }else if ("2".equals(reportUserRole)){
            projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
        }
        //没有区域的查询集团的
        if (projectRuleDetail==null){
            projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");

        }
        //获取客户分配上限
        if(StringUtils.isNotEmpty(projectRuleDetail.getAllocationCustomerMax())){
            int maxCount = Integer.parseInt(projectRuleDetail.getAllocationCustomerMax());
            //判断当天分配是否达到上限
            int lCount = projectCluesDao.getAllowedClueCount(userId,projectId);
            if(lCount >= maxCount){
                return ResultBody.error(2001, "该专员可接受分配客户数已达到上限 "+maxCount+" 个，无法继续分配客户！");
            }
            if(dCount + lCount > maxCount){
                return ResultBody.error(2002, "本次分配客户已达专员可接受分配客户数上限 "+maxCount+" 个，历史已分配"+lCount+"个，请重新选择客户再分配该专员！");
            }
        }
        return ResultBody.success("");
    }

    /**
     * 公池重分配新
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody publicPoolDistributionNew(SalesAttributionForm salesAttributionForm) {
        try {
            if (StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId()) && (salesAttributionForm.getProjectClueList()==null || salesAttributionForm.getProjectClueList().size()==0)){
                return ResultBody.error(2001, "请选择需分配的客户！");
            }
            List<String> oppIds = new ArrayList<>();
            if (!StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId())){
                oppIds.add(salesAttributionForm.getOpportunityClueId());
            }else{
                oppIds = salesAttributionForm.getProjectClueList();
            }
            //判断当天分配是否达到上限 20000
            int dCount = oppIds.size();
            int lCount = projectCluesDao.getAllowedCount(SecurityUtils.getUserId());
            if(lCount >= 20000){
                return ResultBody.error(2001, "当天分配客户已达到上限 20000 个，请明天再来！");
            }
            if(dCount + lCount > 20000){
                return ResultBody.error(2002, "本次分配客户已达当天分配客户数上限 20000 个，当天已分配"+lCount+"，请重新选择或明日再来！");
            }
            ResultBody res = this.getAllocationClueCustomerIsMax(salesAttributionForm.getProjectId(),salesAttributionForm.getSalesAttributionId(),salesAttributionForm.getJobCode(),oppIds.size());
            if (res.getCode() != 200){
                return res;
            }
            String sendTime = "";
            String sendTimeStr = "";
            String sendCustomerName = "";
            String sendCustomerOppId = "";
            String sendCustomerClueId = "";
            String result = "分配成功";
            boolean resFlag = false;
            if (oppIds.size()>0){
                String jobCode = salesAttributionForm.getJobCode();
                String projectId = salesAttributionForm.getProjectId();
                String projectName = salesAttributionForm.getProjectName();
                String salesAttributionId = salesAttributionForm.getSalesAttributionId();
                String salesAttributionName = salesAttributionForm.getSalesAttributionName();
                String poolType = salesAttributionForm.getPoolType();

                Map param = new HashMap();
                param.put("userId", salesAttributionId);
                param.put("projectId", projectId);

                param.put("jobCode",jobCode);
                Map orgMap = projectCluesDao.getUserOrg(param);
                String reportUserRole = "";
                if ("zygw".equals(jobCode)){
                    reportUserRole = "1";
                }
                if ("qyzygw".equals(jobCode)){
                    reportUserRole = "2";
                }
                //判断系统配置规则
                ProjectRuleDetail projectRuleDetailXt = projectCluesDao.selectProjectRuleZs("-1","2");
                Map map = new HashMap();
                //查询项目规则
                String ComGUID = projectCluesDao.getComGUIDByProject(projectId);// 区域ID
                //查询规则计算报备逾期及预警时间
                ProjectRuleDetail projectRuleDetail = null;
                if ("1".equals(reportUserRole)){
                    projectRuleDetail = projectCluesDao.selectProjectRuleZs(projectId,"2");
                    //项目没有配置规则 查询区域的
                    if (projectRuleDetail==null){
                        projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
                    }
                }else if ("2".equals(reportUserRole)){
                    projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
                }
                //没有区域的查询集团的
                if (projectRuleDetail==null){
                    projectRuleDetail = projectRuleDetailXt;

                }
//                String reportExpireDaysStr = "";
//                int reportExpireDays = 0;
//                String reportDaysWarningStr = "";
//                int reportDaysWarning = 0;
//                if (projectRuleDetail!=null){
//                    reportExpireDaysStr = projectRuleDetail.getReportExpireDays();
//                    if (StringUtils.isNotEmpty(reportExpireDaysStr)){
//                        reportExpireDays = Integer.parseInt(reportExpireDaysStr);
//                    }
//                    reportDaysWarningStr = projectRuleDetail.getReportDaysWarning();
//                    if (StringUtils.isNotEmpty(reportDaysWarningStr)){
//                        reportDaysWarning = Integer.parseInt(reportDaysWarningStr);
//                    }
//                }
                //公客池分配保护时间
                String assignPoolsExpireDaysStr = "";
                int assignPoolsExpireDays = 0;
                if (projectRuleDetailXt!=null) {
                    assignPoolsExpireDaysStr = projectRuleDetailXt.getAssignPoolsExpireDays();
                    if (StringUtils.isNotEmpty(assignPoolsExpireDaysStr)){
                        assignPoolsExpireDays = Integer.parseInt(assignPoolsExpireDaysStr);
                    }
                }
                //查询规则计算跟进逾期及预警时间
//                if (StringUtils.isNotEmpty(reportExpireDaysStr)){
//                    Date dBefore = new Date();
//                    Calendar calendar = Calendar.getInstance(); //得到日历
//                    calendar.setTime(dBefore);//把当前时间赋给日历
//                    calendar.add(Calendar.DAY_OF_MONTH, reportExpireDays);
//                    dBefore = calendar.getTime();
//                    map.put("salesFollowExpireDate",sf.format(dBefore));
//                }else{
//                    map.put("salesFollowExpireDate",null);
//                }
//                if (StringUtils.isNotEmpty(reportDaysWarningStr)){
//                    Date dBefore = new Date();
//                    Calendar calendar = Calendar.getInstance(); //得到日历
//                    calendar.setTime(dBefore);//把当前时间赋给日历
//                    calendar.add(Calendar.DAY_OF_MONTH, reportDaysWarning);
//                    dBefore = calendar.getTime();
//                    map.put("salesFollowExpireWarningDate",sf.format(dBefore));
//                }else{
//                    map.put("salesFollowExpireWarningDate",null);
//                }
                if (StringUtils.isNotEmpty(assignPoolsExpireDaysStr)){
                    Date today = new Date();
                    Date dBefore = new Date();
                    Calendar calendar = Calendar.getInstance(); //得到日历
                    calendar.setTime(today);//把当前时间赋给日历
                    calendar.add(Calendar.DAY_OF_MONTH, assignPoolsExpireDays);
                    dBefore = calendar.getTime();
                    map.put("salesFollowExpireDate", sf.format(dBefore));
                    sendTime = sf.format(dBefore);
                    sendTimeStr = assignPoolsExpireDaysStr;
                }else{
                    map.put("salesFollowExpireDate", null);
                    sendTime = "无";
                    sendTimeStr = "永久";
                }
                //查询有分配权限的项目
                List<String> qxProList = new ArrayList<>();
                List<Map> list = projectCluesDao.getAllocationAllCustomerUserOrgInfo(SecurityUtils.getUserId());
                //判断是否存在管理员权限
                final boolean[] isManager = {false};
                list.stream().forEach(x->{
                    if("3".equals(poolType)){//全国池 看有没有分配权限
                        if("10001".equals(x.get("jobCode"))){
                            isManager[0] = true;
                        }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                            isManager[0] = true;
                        }else if("qyzszj".equals(x.get("jobCode"))){
                            isManager[0] = true;
                        }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                            isManager[0] = true;
                        }
                    }else if("2".equals(poolType)){//区域池 看岗位所在组织对应的区域id下的所有项目
                        if("10001".equals(x.get("jobCode"))){
                            isManager[0] = true;
                        }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                            String comGUID = x.get("areaId")+"";
                            qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                        }else if("qyzszj".equals(x.get("jobCode"))){
                            String comGUID = x.get("areaId")+"";
                            qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                        }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                            String comGUID = x.get("areaId")+"";
                            qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                        }
                    }else if("1".equals(poolType)){//区域池 看岗位所在组织对应的项目
                        if("10001".equals(x.get("jobCode"))){
                            isManager[0] = true;
                        }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                            qxProList.add(x.get("projectId")+"");
                        }else if("qyzszj".equals(x.get("jobCode"))){
                            qxProList.add(x.get("projectId")+"");
                        }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                            String comGUID = x.get("areaId")+"";
                            qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                        }
                    }
                });
                //可分配的机会ID
                List<String> okOppIds = new ArrayList<>();
                //不可分配的机会ID 无分配权限
                List<String> notQxOppIds = new ArrayList<>();
                //不可分配的机会ID 客户规则判重
                List<String> notPcProOppIds = new ArrayList<>();
                List<String> notPcRegionOppIds = new ArrayList<>();
                List<String> notPcPoolOppIds = new ArrayList<>();
                List<String> notPcProRelaOppIds = new ArrayList<>();
                List<String> notPcRegionRelaOppIds = new ArrayList<>();
                for (String OpportunityClueId:oppIds) {
                    //获取机会信息
                    Map oldMap = projectCluesDao.getOpportunityById(OpportunityClueId);
                    //判断是否存在分配权限
                    if (!isManager[0] && !qxProList.contains(oldMap.get("projectId")+"")){
                        notQxOppIds.add(OpportunityClueId);
                        continue;
                    }
                    //判断是否可分配
                    Map map1 = new HashMap();
                    map1.put("projectId",projectId);
                    map1.put("customerMobile",oldMap.get("CustomerMobile"));
                    map1.put("customerName",oldMap.get("CustomerName"));
                    map1.put("opportunityClueId",oldMap.get("OpportunityClueId"));
                    //原机会的项目ID
//                    String oldProjectId = oldMap.get("projectId")+"";
                    //处理项目联动
                    List<String> proList = new ArrayList<>();
//                    String proIds = projectCluesDao.getTranslateProIds(projectId);
//                    if(StringUtils.isNotEmpty(proIds)){
//                        proList = new ArrayList(Arrays.asList(proIds.split(",")));
//                    }
                    //不管有无联动项目 保证原项目存在
                    proList.add(projectId);
                    map1.put("proList",proList);
//                    map1.put("judgeNoPool",projectRuleDetailXt.getJudgeNoPool());
//                    map1.put("judgeNoRegion",projectRuleDetailXt.getJudgeNoRegion());
                    //查询是否存在机会
//                    List<Map> opps = new ArrayList<>();
//                    if("0".equals(projectRuleDetailXt.getJudgeStage())){
//                        opps = projectCluesDao.getCstIsOkReferral(map1);
//                    }else if("1".equals(projectRuleDetailXt.getJudgeStage())){
//                        opps = projectCluesDao.getCstIsOkComeVisit(map1);
//                    }else if("2".equals(projectRuleDetailXt.getJudgeStage())){
//                        opps = projectCluesDao.getCstIsOkTrade(map1);
//                    }else {
//                        return ResultBody.error(-10002,"系统配置异常");
//                    }
                    String type = "";
                    boolean flag1 = false;
//                    for (Map m:opps) {
//                        int cout = Integer.parseInt(m.get("count")+"");
//                        if (cout>0){
//                            type = m.get("type")+"";
//                            //获取重复客户 判断客户对应类型的规则 万企通走万企通配置 转介走转介保护期
//                            map1.put("type",type);
//                            List<Map> cusOpps = projectCluesDao.getCstIsOkRepeat(map1);
//                            for (Map cusOpp: cusOpps) {
//                                Map queryMap = new HashMap();
//                                List<String> proList1 = new ArrayList<>();
//                                proList1.add(cusOpp.get("projectId")+"");
//                                queryMap.put("proList",proList1);
//                                queryMap.put("customerMobile",cusOpp.get("customerMobile")+"");
//                                queryMap.put("customerName",cusOpp.get("customerName")+"");
//                                String sourceMode = cusOpp.get("sourceMode")+"";
//                                if("1".equals(sourceMode)){//万企通客户
//                                    queryMap.put("judgeNoPool",projectRuleDetailXt.getWqtJudgeNoPool());
//                                    queryMap.put("judgeNoRegion",projectRuleDetailXt.getWqtJudgeNoRegion());
//                                    //查询是否存在机会
//                                    List<Map> opps1 = new ArrayList<>();
//                                    if("0".equals(projectRuleDetailXt.getWqtJudgeStage())){
//                                        opps1 = projectCluesDao.getCstIsOkReferral(queryMap);
//                                    }else if("1".equals(projectRuleDetailXt.getWqtJudgeStage())){
//                                        opps1 = projectCluesDao.getCstIsOkComeVisit(queryMap);
//                                    }else if("2".equals(projectRuleDetailXt.getWqtJudgeStage())){
//                                        opps1 = projectCluesDao.getCstIsOkTrade(queryMap);
//                                    }else {
//                                        return ResultBody.error(-10002,"系统配置异常");
//                                    }
//                                    for (Map m1:opps1) {
//                                        int cout1 = Integer.parseInt(m1.get("count")+"");
//                                        if (cout1>0){
//                                            type = m1.get("type")+"";
//                                            flag1 = true;
//                                            break;
//                                        }
//                                    }
//                                }else if("2".equals(sourceMode)){//转介客户
//                                    queryMap.put("judgeNoPool",projectRuleDetailXt.getReferralJudgeNoPool());
//                                    queryMap.put("judgeNoRegion",projectRuleDetailXt.getReferralJudgeNoRegion());
//                                    //查询是否存在机会
//                                    List<Map> opps2 = new ArrayList<>();
//                                    if("0".equals(projectRuleDetailXt.getReferralJudgeStage())){
//                                        opps2 = projectCluesDao.getCstIsOkReferral(queryMap);
//                                    }else if("1".equals(projectRuleDetailXt.getReferralJudgeStage())){
//                                        opps2 = projectCluesDao.getCstIsOkComeVisit(queryMap);
//                                    }else if("2".equals(projectRuleDetailXt.getReferralJudgeStage())){
//                                        opps2 = projectCluesDao.getCstIsOkTrade(queryMap);
//                                    }else {
//                                        return ResultBody.error(-10002,"系统配置异常");
//                                    }
//                                    for (Map m2:opps2) {
//                                        int cout2 = Integer.parseInt(m2.get("count")+"");
//                                        if (cout2>0){
//                                            type = m2.get("type")+"";
//                                            flag1 = true;
//                                            break;
//                                        }
//                                    }
//                                }else if("3".equals(sourceMode)){//案场客户
//                                    flag1 = true;
//                                    break;
//                                }
//                            }
//                        }
//                    }
                    //查询是否存在分配状态客户
                    List<Map> disClus = projectCluesDao.getCstIsOkDisClues(map1);
                    for (Map m:disClus) {
                        int cout = Integer.parseInt(m.get("count")+"");
                        if (cout>0){
                            type = m.get("type")+"";
                            flag1 = true;
                            break;
                        }
                    }
                    boolean isNotOK = false;
//                    String notPCType = "";
                    if (flag1){
//                        if ("1".equals(type)){
//                            if (!oldProjectId.equals(projectId)){
//                                isNotOK = true;
//                                notPCType = type;
//                            }
//                        }else if ("pro".equals(type)){
                            isNotOK = true;
//                            notPCType = type;
//                        }else if ("region".equals(type)){
//                            isNotOK = true;
//                            notPCType = type;
//                        }else if ("proRelate".equals(type)){
//                            isNotOK = true;
//                            notPCType = type;
//                        }else if ("regionRelate".equals(type)){
//                            isNotOK = true;
//                            notPCType = type;
//                        }
                    }
                    if (isNotOK){
//                        if ("1".equals(notPCType)){
//                            notPcPoolOppIds.add(OpportunityClueId);
//                        }else if ("pro".equals(notPCType)){
                            notPcProOppIds.add(OpportunityClueId);
//                        }else if ("region".equals(notPCType)){
//                            notPcRegionOppIds.add(OpportunityClueId);
//                        }else if ("proRelate".equals(notPCType)){
//                            notPcProRelaOppIds.add(OpportunityClueId);
//                        }else if ("regionRelate".equals(notPCType)){
//                            notPcRegionRelaOppIds.add(OpportunityClueId);
//                        }
                        continue;
                    }else{
                        okOppIds.add(OpportunityClueId);
                    }
                    oldMap.put("ConfirmID",SecurityUtils.getUserId());
                    oldMap.put("ConfirmTime",new Date());
                    oldMap.put("ApplyDatetime",new Date());
                    oldMap.put("OldSalesId",oldMap.get("SalesAttributionId"));
                    oldMap.put("OldSalesName",oldMap.get("SalesAttributionName"));
                    oldMap.put("SalesId",salesAttributionId);
                    oldMap.put("SalesName",salesAttributionName);
                    oldMap.put("Type","1");
                    oldMap.put("mainProjectId",projectId);
                    oldMap.put("mainProjectName",projectName);
                    //保存分配记录
                    projectCluesDao.addRelCustomerRecord(oldMap);

                    //生成消息
                    //获取客户原信息
                    ReferralVo referralVo = projectCluesDao.getOldOpportunityClueInfo(OpportunityClueId);

                    //判断线索客户是否存在
                    String ProjectClueId = String.valueOf(oldMap.get("ProjectClueId"));
                    ReportCustomerForm reportCustomerForm = projectCluesDao.getProjectClue(ProjectClueId);
                    if(reportCustomerForm == null){
                        //获取客户原信息
                        reportCustomerForm = new ReportCustomerForm();
                        InformationVO informationVO = projectCluesDao.getInformationInfo(ProjectClueId,"zygw","1","1",null,null);
                        BeanUtils.copyProperties(informationVO,reportCustomerForm);
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        reportCustomerForm.setClueStatus("11");
                        reportCustomerForm.setUserId(salesAttributionId);
                        reportCustomerForm.setEmployeeName(salesAttributionName);
                        reportCustomerForm.setOrgId(String.valueOf(orgMap.get("orgId")));
                        reportCustomerForm.setOrgName(String.valueOf(orgMap.get("orgName")));
                        reportCustomerForm.setProjectId(projectId);
                        reportCustomerForm.setProjectName(projectName);
                        reportCustomerForm.setDisTime(sf.format(DateUtil.date()));
                        reportCustomerForm.setDisPerson(SecurityUtils.getUserId());
                        reportCustomerForm.setDisPersonName(SecurityUtils.getEmployeeName());
                        reportCustomerForm.setDisOpportunityClueId(OpportunityClueId);
                        //设置保护期
                        reportCustomerForm.setSalesFollowExpireDate(map.get("salesFollowExpireDate")+"");
                        reportCustomerForm.setCustomerLevel("E");
                        //判断客户分配前的客户等级 若为A/B/ 则需要更新为D级
                        if (informationVO.getCustomerLevel().equals("A") || informationVO.getCustomerLevel().equals("B") || informationVO.getCustomerLevel().equals("C")){
                            reportCustomerForm.setCustomerLevel("D");

                            //获取客户信息
                            Map oppMap = projectCluesDao.getCustomerInfoInsCl(OpportunityClueId);
                            //客户等级变化记录 客户等级日志表
                            CustomerLevelRecordVo clMap = new CustomerLevelRecordVo();
                            clMap.setOpportunityCueId(String.valueOf(oppMap.get("opportunityClueId")));
                            clMap.setProjectClueId(String.valueOf(oppMap.get("projectClueId")));
                            clMap.setCustomerLevel("D");
                            clMap.setSalesAttributionId(String.valueOf(oppMap.get("salesAttributionId")));
                            clMap.setSalesAttributionName(String.valueOf(oppMap.get("salesAttributionName")));
                            clMap.setSalesAttributionTeamId(String.valueOf(oppMap.get("salesAttributionTeamId")));
                            clMap.setSalesAttributionTeamName(String.valueOf(oppMap.get("salesAttributionTeamName")));
                            clMap.setProjectId(String.valueOf(oppMap.get("projectId")));
                            clMap.setAreaId(String.valueOf(oppMap.get("areaId")));
                            clMap.setCreator(String.valueOf(oppMap.get("userId")));
                            projectCluesDao.insertCustomerLevelChangeLog(clMap);
                        }
                        //线索表新增
                        projectCluesDao.insertProjectClues(reportCustomerForm);
                        //更新报备客户信息
                        Map oppMap = new HashMap<>();
                        oppMap.put("OpportunityClueId",OpportunityClueId);
                        oppMap.put("projectId",reportCustomerForm.getProjectId());
                        oppMap.put("projectName",reportCustomerForm.getProjectName());
                        oppMap.put("salesAttributionId",reportCustomerForm.getUserId());
                        oppMap.put("salesAttributionName",reportCustomerForm.getEmployeeName());
                        oppMap.put("salesAttributionTeamId",reportCustomerForm.getOrgId());
                        oppMap.put("salesAttributionTeamName",reportCustomerForm.getOrgName());
                        oppMap.put("CustomerLevel",reportCustomerForm.getCustomerLevel());
                        oppMap.put("salesFollowExpireDate",referralVo.getSalesFollowExpireDate());
                        oppMap.put("salesFollowExpireWarningDate",referralVo.getSalesFollowExpireDate());
                        projectCluesDao.updateOppCst(oppMap);
                        //查询附件
                        List<String> enclosures = projectCluesDao.getOppEnclosures2(OpportunityClueId);
                        //新增附件表
                        if (enclosures!=null && enclosures.size()>0){
                            Map param1 = new HashMap();
                            param1.put("list",enclosures);
                            param1.put("ProjectClueId",ProjectClueId);
                            param1.put("enclosureType","1");
                            projectCluesDao.addCluesEnclosures(param1);
                        }
                    }else {
                        map.put("disOpportunityClueId",OpportunityClueId);
                        map.put("salesAttributionId",salesAttributionId);
                        map.put("salesAttributionName",salesAttributionName);
                        map.put("salesAttributionTeamId",orgMap.get("orgId"));
                        map.put("salesAttributionTeamName",orgMap.get("orgName"));
                        map.put("ProjectClueId",oldMap.get("ProjectClueId"));
                        map.put("clueStatus","11");
                        map.put("disPerson",SecurityUtils.getUserId());
                        map.put("disPersonName",SecurityUtils.getEmployeeName());
                        map.put("projectId",projectId);
                        map.put("ProjectName",projectName);
                        map.put("salesFollowExpireDate",map.get("salesFollowExpireDate"));
                        //判断客户分配前的客户等级 若为A/B/ 则需要更新为D级
                        if (referralVo.getCustomerLevel().equals("A") || referralVo.getCustomerLevel().equals("B") || referralVo.getCustomerLevel().equals("C")){
                            map.put("CustomerLevel","D");

                            //获取客户信息
                            Map oppMap = projectCluesDao.getCustomerInfoInsCl(OpportunityClueId);
                            //客户等级变化记录 客户等级日志表
                            CustomerLevelRecordVo clMap = new CustomerLevelRecordVo();
                            clMap.setOpportunityCueId(String.valueOf(oppMap.get("opportunityClueId")));
                            clMap.setProjectClueId(String.valueOf(oppMap.get("projectClueId")));
                            clMap.setCustomerLevel("D");
                            clMap.setSalesAttributionId(String.valueOf(oppMap.get("salesAttributionId")));
                            clMap.setSalesAttributionName(String.valueOf(oppMap.get("salesAttributionName")));
                            clMap.setSalesAttributionTeamId(String.valueOf(oppMap.get("salesAttributionTeamId")));
                            clMap.setSalesAttributionTeamName(String.valueOf(oppMap.get("salesAttributionTeamName")));
                            clMap.setProjectId(String.valueOf(oppMap.get("projectId")));
                            clMap.setAreaId(String.valueOf(oppMap.get("areaId")));
                            clMap.setCreator(String.valueOf(oppMap.get("userId")));
                            projectCluesDao.insertCustomerLevelChangeLog(clMap);
                        }else {
                            map.put("CustomerLevel",referralVo.getCustomerLevel());
                        }
                        projectCluesDao.updateClueCst(map);
                        //更新报备客户信息
                        Map oppMap = new HashMap<>();
                        oppMap.put("OpportunityClueId",OpportunityClueId);
                        oppMap.put("projectId",projectId);
                        oppMap.put("projectName",projectName);
                        oppMap.put("salesAttributionId",salesAttributionId);
                        oppMap.put("salesAttributionName",salesAttributionName);
                        oppMap.put("salesAttributionTeamId",orgMap.get("orgId"));
                        oppMap.put("salesAttributionTeamName",orgMap.get("orgName"));
                        oppMap.put("CustomerLevel", String.valueOf(map.get("CustomerLevel")));
                        oppMap.put("salesFollowExpireDate",referralVo.getSalesFollowExpireDate());
                        oppMap.put("salesFollowExpireWarningDate",referralVo.getSalesFollowExpireDate());
                        projectCluesDao.updateOppCst(oppMap);
                    }
                    //新增客户报备日志记录
                    CustomerAddLogVo customerAddLogVo = new CustomerAddLogVo();
                    customerAddLogVo.setAreaId(projectCluesDao.getComGUIDByProject(projectId));
                    customerAddLogVo.setProjectId(projectId);
                    customerAddLogVo.setOpportunityClueId(oldMap.get("OpportunityClueId")+"");
                    customerAddLogVo.setProjectClueId(oldMap.get("ProjectClueId")+"");
                    customerAddLogVo.setCustomerName(oldMap.get("CustomerName")+"");
                    customerAddLogVo.setCustomerMobile(oldMap.get("CustomerMobile")+"");
                    customerAddLogVo.setSalesAttributionId(salesAttributionId);
                    customerAddLogVo.setSalesAttributionName(salesAttributionName);
                    customerAddLogVo.setSalesAttributionTeamId(orgMap.get("orgId")+"");
                    customerAddLogVo.setSalesAttributionTeamName(orgMap.get("orgName")+"");
                    //获取历史该客户信息
                    customerAddLogVo.setIsThreeOnes(oldMap.get("IsThreeOnes")+"");
                    customerAddLogVo.setIsThreeOnesDate(oldMap.get("IsThreeOnesDate")+"");
                    customerAddLogVo.setAddType("5");
                    //判断客户是否报备过该项目
                    String isAdd = projectCluesDao.getCustomerAddLogToIsAdd(customerAddLogVo);
                    customerAddLogVo.setIsAdd(isAdd);
                    customerAddLogVo.setReportCreateTime(sf.format(DateUtil.date()));
                    customerAddLogVo.setIsEffective("1");
                    //设置历史该客户状态
                    projectCluesDao.disableCutomerAddLog(customerAddLogVo);
                    //保存客户报备日志
                    projectCluesDao.saveCustomerAddLog(customerAddLogVo);

                    //保存成功分配客户名称
                    sendCustomerName = referralVo.getCustomerName();
                    sendCustomerOppId = referralVo.getOpportunityClueId();
                    sendCustomerClueId = referralVo.getProjectClueId();
                }
                //可分配的机会存在 更新公池数据为删除
                if (okOppIds.size()>0){
                    projectCluesDao.delPublicOpps(okOppIds);
                    if(okOppIds.size() == 1){
                        sendCustomerName = "新的客户【"+sendCustomerName+"】";
                    }else {
                        sendCustomerName = okOppIds.size()+"个新的客户";
                        sendCustomerOppId = "";
                        sendCustomerClueId = "";
                    }
                    //发起人
                    Message message = new Message();
                    //接收人
                    message.setSubject("【公客池分配客户通知】");
                    String content;
                    if("无".equals(sendTime)){
                        content = "【公客池分配客户通知】您已被分配"+sendCustomerName+"至线索客户列表，客户为永久保护，请及时转报备！";
                        message.setContent("【公客池分配客户通知】 您已被分配"+sendCustomerName+"至线索客户列表，客户为永久保护，请及时转报备！");
                    }else {
                        content = "【公客池分配客户通知】您已被分配"+sendCustomerName+"至线索客户列表，客户为保护"+sendTime+"天，请在"+sendTimeStr+"天内及时转报备，否则将自动掉回公客池，过保时间为："+sendTime+"。";
                        message.setContent("【公客池分配客户通知】 您已被分配"+sendCustomerName+"至线索客户列表，请在"+sendTimeStr+"天内及时转报备，否则将自动掉回公客池，过保时间为："+sendTime+"。");
                    }
                    message.setSender("");
                    message.setMessageType(2106);
                    message.setIsDel(0);
                    message.setReceiver(salesAttributionId);
                    message.setIsRead(0);
                    message.setIsPush(2);
                    message.setIsNeedPush(2);
                    message.setProjectClueId(sendCustomerClueId);
                    message.setOpportunityClueId(sendCustomerOppId);
                    message.setProjectId(salesAttributionForm.getProjectId());
                    projectCluesDao.insertOneMessage(message);
                    //查询手机号
                    Map mobileMap = messageMapper.getUserMobile(salesAttributionId);
                    if (mobileMap != null && !StringUtils.isEmpty(mobileMap.get("Mobile") + "") && !StringUtils.isEmpty(sendTime)){
                        //发送短信
                        try {
                            if(!"无".equals(sendTime)){
                                String sss = URLEncoder.encode(sendCustomerName+"|"+sendTimeStr+"|"+sfDa.format(sf.parse(sendTime))+"|"+sfTi.format(sf.parse(sendTime)),"UTF-8");
                                String userName = mobileMap.get("UserName")+"";
                                content = content.replaceAll(" ","");
                                //发送短信改为发送OA
                                if(isSendOAMessage==1) {
                                    HttpRequestUtil.httpGet(sendOAMessageUrl + "?method=unmsg&content=" + content + "&url=&h5url=&noneBindingReceiver=" + userName + "&appcode=" + sendOAMessageAppCode + "&sysName=移动案场系统", false);
                                }
//                                HttpRequestUtil.httpGet("http://esb.vanyang.com.cn/sms/sendSMSMessageV1?sys_code=ydac&login_name=ydac&password=123456&modeId=000045&vars="+sss+"&mobile="+mobile+"&sendTime&notifyUrl=http://www.baidu.com&sysName=移动案场系统",false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (notQxOppIds.size()>0 || notPcPoolOppIds.size()>0 || notPcProOppIds.size()>0 || notPcRegionOppIds.size()>0 || notPcProRelaOppIds.size()>0 || notPcRegionRelaOppIds.size()>0){
                    resFlag = true;
                    result = result.concat(okOppIds.size()+"条,其中");
                }
                if (notQxOppIds.size()>0){
                    result = result.concat(notQxOppIds.size()+"个公池客户无权限分配,");
                }
                if (notPcPoolOppIds.size()>0){
                    result = result.concat(notPcPoolOppIds.size()+"个公池客户公客池已存在无法分配,");
                }
                if (notPcProOppIds.size()>0){
                    result = result.concat(notPcProOppIds.size()+"个公池客户项目上已存在无法分配,");
                }
                if (notPcRegionOppIds.size()>0){
                    result = result.concat(notPcRegionOppIds.size()+"个公池客户区域上已存在无法分配,");
                }
                if (notPcProRelaOppIds.size()>0){
                    result = result.concat(notPcProRelaOppIds.size()+"个公池客户项目已关联无法分配,");
                }
                if (notPcRegionRelaOppIds.size()>0){
                    result = result.concat(notPcRegionRelaOppIds.size()+"个公池客户区域已关联无法分配,");
                }
            }
            if(resFlag){
                result = result.substring(0,result.length()-1).concat("！");
            }else {
                result = result.concat("！");
            }
            return ResultBody.success(result);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BadRequestException(-11_1058, "分配失败！", e);
        }
    }

    /**
     * 批量调整客户状态
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateBatchCustomerStatus(ExcelForm projectCluesForm) {
        if (CollectionUtils.isEmpty(projectCluesForm.getOpportunityList())){
            return ResultBody.error(2001, "请选择需调整的客户！");
        }
        //判断当前登录人是否有管理员调整客户状态权限
        List<Map> glQx = projectCluesDao.getGlBatchUpdateCustomerStatusQx(SecurityUtils.getUserId());
        if(CollectionUtils.isEmpty(glQx)){
            return ResultBody.error(2001, "暂无管理员批量调整客户状态权限 请调整！");
        }
        String clueStatus = projectCluesForm.getClueStatus();
        Map<String,ProjectRuleDetail> mainProjectRule = new HashMap<>();
        final ProjectRuleDetail[] projectRuleDetail = {null};
        //保存集团规则
        projectRuleDetail[0] = projectCluesDao.selectProjectRuleZs("-1","2");
        mainProjectRule.put("-1", projectRuleDetail[0]);
        List<String> proList = new ArrayList<>();
        List<String> areaList = new ArrayList<>();
        List<String> oppIds = projectCluesForm.getOpportunityList();
        //需要更新的数据
        List<Map> udpMap = new ArrayList<>();
        oppIds.stream().forEach(x->{
            //获取客户信息
            Map oldCustMap = projectCluesDao.getCustomerProAndAreaAllInfo(x);
            if(oldCustMap != null && !oldCustMap.isEmpty()){
                if(!clueStatus.equals(String.valueOf(oldCustMap.get("clueStatus")))){//相同状态不调整
                    //需要更新数据的内容
                    Map map = new HashMap<>();
                    String projectId = oldCustMap.get("projectId")+"";
                    String comGuId = oldCustMap.get("comGuId")+"";
                    //判断项目集合和区域集合 是否查询过
                    if(!proList.contains(projectId) && !areaList.contains(comGuId)){
                        //查询保护规则
                        projectRuleDetail[0] = projectCluesDao.selectProjectRuleZs(projectId,"2");
                        proList.add(projectId);
                        //查询到项目规则 保存项目规则记录 如果项目没有配置规则 查询区域的
                        if (projectRuleDetail[0] !=null){
                            mainProjectRule.put(projectId, projectRuleDetail[0]);
                        }else {
                            projectRuleDetail[0] = projectCluesDao.selectProjectRuleZs(comGuId,"2");
                            areaList.add(comGuId);
                            //查询到区域规则 保存区域规则记录 如果没有区域的默认取集团规则
                            if (projectRuleDetail[0] !=null){
                                mainProjectRule.put(comGuId, projectRuleDetail[0]);
                            }else {
                                projectRuleDetail[0] = mainProjectRule.get("-1");
                            }
                        }
                    }else {
                        if(proList.contains(projectId)){
                            projectRuleDetail[0] = mainProjectRule.get(projectId);
                        }
                        if(projectRuleDetail[0] == null){
                            if(areaList.contains(comGuId)){
                                projectRuleDetail[0] = mainProjectRule.get(comGuId);
                            }
                        }
                        if(projectRuleDetail[0] == null){
                            projectRuleDetail[0] = mainProjectRule.get("-1");
                        }
                    }
                    String reportExpireDaysStr = "";
                    int reportExpireDays = 0;
                    String reportDaysWarningStr = "";
                    int reportDaysWarning = 0;
                    if (projectRuleDetail[0] !=null){
                        if("1".equals(clueStatus)){//报备
                            reportExpireDaysStr = projectRuleDetail[0].getReportExpireDays();
                            reportDaysWarningStr = projectRuleDetail[0].getReportDaysWarning();
                            map.put("clueStatus","1");
                        }else if("2".equals(clueStatus)){//到访
                            reportExpireDaysStr = projectRuleDetail[0].getVisitExpireDays();
                            reportDaysWarningStr = projectRuleDetail[0].getVisitingWarning();
                            map.put("clueStatus","2");
                        }else if ("8".equals(clueStatus)){//成交
                            map.put("clueStatus","8");
                        }
                        if (StringUtils.isNotEmpty(reportExpireDaysStr)){
                            reportExpireDays = Integer.parseInt(reportExpireDaysStr);
                        }
                        if (StringUtils.isNotEmpty(reportDaysWarningStr)){
                            reportDaysWarning = Integer.parseInt(reportDaysWarningStr);
                        }
                    }
                    //查询规则计算跟进逾期及预警时间
                    if (StringUtils.isNotEmpty(reportExpireDaysStr)){
                        Date dBefore = new Date();
                        Calendar calendar = Calendar.getInstance(); //得到日历
                        calendar.setTime(dBefore);//把当前时间赋给日历
                        calendar.add(Calendar.DAY_OF_MONTH, reportExpireDays);
                        dBefore = calendar.getTime();
                        map.put("salesFollowExpireDate",sf.format(dBefore));
                    }else{
                        map.put("salesFollowExpireDate",null);
                    }
                    if (StringUtils.isNotEmpty(reportDaysWarningStr)){
                        Date dBefore = new Date();
                        Calendar calendar = Calendar.getInstance(); //得到日历
                        calendar.setTime(dBefore);//把当前时间赋给日历
                        calendar.add(Calendar.DAY_OF_MONTH, reportDaysWarning);
                        dBefore = calendar.getTime();
                        map.put("salesFollowExpireWarningDate",sf.format(dBefore));
                    }else{
                        map.put("salesFollowExpireWarningDate",null);
                    }
                    oldCustMap.put("projectClueId",oldCustMap.get("ProjectClueId"));
                    oldCustMap.put("opportunityClueId",oldCustMap.get("OpportunityClueId"));
                    oldCustMap.put("projectId",oldCustMap.get("projectId"));
                    oldCustMap.put("projectName",oldCustMap.get("ProjectName"));
                    oldCustMap.put("confirmId",SecurityUtils.getUserId());
                    oldCustMap.put("confirmPersonName",SecurityUtils.getEmployeeName());
                    oldCustMap.put("confirmTime",new Date());
                    oldCustMap.put("applyDateTime",new Date());
                    oldCustMap.put("salesId",oldCustMap.get("SalesAttributionId"));
                    oldCustMap.put("salesName",oldCustMap.get("SalesAttributionName"));
                    oldCustMap.put("note","将客户状态由"+this.clueStatusToCn(String.valueOf(oldCustMap.get("clueStatus")))+"调整为"+this.clueStatusToCn(clueStatus));
                    oldCustMap.put("type","1");
                    oldCustMap.put("creator",SecurityUtils.getUserId());
                    oldCustMap.put("createTime",new Date());
                    oldCustMap.put("updator",SecurityUtils.getUserId());
                    oldCustMap.put("updateTime",new Date());
                    //保存客户变更节点记录
                    projectCluesDao.addCustomerChangeNodeRecord(oldCustMap);

                    map.put("opportunityClueId",oldCustMap.get("opportunityClueId")+"");
                    udpMap.add(map);
                }
            }
        });
        if (udpMap.size() > 0) {
            int size = udpMap.size();
            //每次更新100条
            int count = size % 100 == 0 ? size / 100 : size / 100 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<Map> fformList = udpMap.subList(s * 100, size);
                    projectCluesDao.updateBatchCustomerStatus(fformList);
                } else {
                    List<Map> fformList = udpMap.subList(s * 100, (s + 1) * 100);
                    projectCluesDao.updateBatchCustomerStatus(fformList);
                }
            }
        }
        return ResultBody.success("操作成功！");
    }

    @Override
    public ResultBody getDataViewPremissionOrgTeam(UserOrgRelForm map) {
//        String type= map.getType();
        List<Map> list = new ArrayList<>();
        List<String> orgList = new ArrayList<>();
        List<String> proList = new ArrayList<>();
        List<Map> orgInfo = projectCluesDao.getDataViewPremissionOrg(map);
        if(CollectionUtils.isEmpty(orgInfo)){
            return ResultBody.success(list);
        }
        List<Map> reList = new ArrayList<>();
//        if("1".equals(type)){
//            orgInfo.stream().forEach(x->{
//                String areaId = x.get("areaId")+"";
//                String areaName = x.get("areaName")+"";
//                Map reMap = new HashMap();
//                reMap.put("label",areaName);
//                reMap.put("value",areaId);
//                if(!reList.contains(reMap)){
//                    reList.add(reMap);
//                }
//            });
//        }else if("2".equals(type)){
            orgInfo.stream().forEach(x->{
                String jobCode = x.get("jobCode")+"";
                String projectId = x.get("projectId")+"";
                String orgId = x.get("orgId")+"";
                //项目ID集合
                if ("xsjl".equals(jobCode) || "qyxsjl".equals(jobCode)){
                    orgList.add(orgId);
                }else if ("zszj".equals(jobCode) || "qyzszj".equals(jobCode) || "yxjl".equals(jobCode)){
                    proList.add(projectId);
                }else if ("qyyxjl".equals(jobCode)){
                    //获取区域下项目ID
                    List<String> proList1 =  projectCluesDao.getComProList(orgId);
                    proList.addAll(proList1);
                }
            });
            map.setProList(proList);
            map.setOrgList(orgList);
            list = projectCluesDao.getDataViewPremissionOrgTeam(map);
            if(CollectionUtils.isEmpty(list)){
                return ResultBody.success(new ArrayList<>());
            }
            Map<String,List<Map>> newMaps= list.stream().collect(Collectors.groupingBy(x->x.get("projectId")+"!"+x.get("projectName")));
            for (String bKey : newMaps.keySet()){
                List<Map> bList=newMaps.get(bKey);
                Map reMap = new HashMap();
                String[] str = bKey.split("!");
                reMap.put("label",str[1]);
                reMap.put("value",str[0]);
                reMap.put("children",bList);
                reList.add(reMap);
            }
//        }
        return ResultBody.success(reList);
    }

    @Override
    public ResultBody getOrgTeam(UserOrgRelForm map) {
        List<Map> viewPremissionOrgTeam = projectCluesDao.getDataViewPremissionOrgTeam(map);
        List<ResultProjectVO> projectList = new ArrayList<>();
        Map<Object, List<Map>> projectIdMap = null;
        if (!CollectionUtils.isEmpty(viewPremissionOrgTeam)) {
            projectIdMap = viewPremissionOrgTeam.stream()
                .collect(Collectors.groupingBy(x -> x.get("projectId")));
        }
        if (projectIdMap == null) {
            return ResultBody.success(true);
        }
        List<String> strings = projectIdMap.keySet().stream().map(x -> x + "").collect(Collectors.toList());
        for (int i = 0; i < strings.size(); i++) {
            String projectId = strings.get(i);
            ResultProjectVO resultProjectVO = new ResultProjectVO();
            resultProjectVO.setValue(projectId);
            List<Map> projectVOList = projectIdMap.get(projectId);
            if(projectVOList.size() > 0){
                List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                for (int j = 0; j < projectVOList.size(); j++) {
                    resultProjectVO.setLabel(projectVOList.get(0).get("projectName") + "");
                    ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                    resultProjectVO2.setValue(projectVOList.get(j).get("orgId") + "");
                    resultProjectVO2.setLabel(projectVOList.get(j).get("orgName") + "");
                    resultProjectVOList2.add(resultProjectVO2);
                }
                resultProjectVO.setChildren(resultProjectVOList2);
            }
            projectList.add(resultProjectVO);
        }
        return ResultBody.success(projectList);
    }

    @Override
    public ResultBody startDataViewPremission(List<UserOrgRelForm> list) {
        final int[] i = {0};
        final int[] j = {0};
        final String[] reMsg = {""};
        try{
            list.stream().forEach(map->{
                //判断流程是否变更 是否需要推送到OA审批
                Boolean isProcessChanged = checkProcessChange(map);
                if (isProcessChanged) {
                    String userName = SecurityUtils.getUsername();
                    String id = UUID.randomUUID().toString();
                    map.setId(id);

                    //推送 OA系统 获取流程ID
                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String creatTime = simpleDateFormat2.format(new Date());
                    map.setCreateTime(creatTime);
                    // 推送流程参数
                    Map requestMap = new HashMap();
                    // 流程编码
                    requestMap.put("sysname", "YDAC");
//            String flowTitle = map.getProjectName()+"项目"+map.getJobName()+map.getUserName()+"的数据查看权限审批";
                    String flowTitle = map.getUserName()+"的数据查看权限审批";
                    // 系统身份唯一标识
                    requestMap.put("templeteCode", "YDAC_002");
//                requestMap.put("billUrl",SystemUrl+"detailsRepresentations?id="+id);
                    requestMap.put("billName",flowTitle);
//                requestMap.put("title-p1","查看原单据");
//                requestMap.put("url-p1",SystemUrl+"detailsRepresentations?id="+id);
                    StringBuffer titlep2 = new StringBuffer();
                    StringBuffer urlp2 = new StringBuffer();
                    String title2 = "";
                    String url2 = "";
                    requestMap.put("title-p2",title2);
                    requestMap.put("url-p2",url2);
                    requestMap.put("senderId",map.getUserId());
                    //获取申诉人对应OA主岗 所属组织信息
                    requestMap.put("senderName",SecurityUtils.getUserId());
                    Map oaMap = projectCluesDao.getUserOAInfo(map.getUserId());
                    if(oaMap != null && (!oaMap.isEmpty())){
                        requestMap.put("senderPost",oaMap.get("senderPost"));
                        requestMap.put("senderGroup",oaMap.get("senderOrg"));
                    }
                    requestMap.put("sendTime",creatTime);//时间
                    requestMap.put("company",projectCluesDao.getProOrgParentLevel(map.getProjectId()));
                    requestMap.put("projectName",map.getProjectName());
                    requestMap.put("mobile",projectCluesDao.getUserMobile(map.getUserId()));
//            requestMap.put("title",flowTitle);
                    requestMap.put("orgIds",map.getOrgName());
                    requestMap.put("isNoTime",map.getIsNoTime());
                    if("0".equals(map.getIsNoTime()+"")){
//                requestMap.put("vaidTime",map.getStartTime()+"至"+map.getEndTime());
                        requestMap.put("vaidTimeS",map.getStartTime());
                        requestMap.put("vaidTimeE",map.getEndTime());
                    }
                    requestMap.put("isNameShow",map.getIsNameShow());
                    requestMap.put("isMobileShow",map.getIsMobileShow());
                    requestMap.put("applyReason",map.getApplyReason());
                    try {
                        flowTitle= URLEncoder.encode(flowTitle,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    JSONObject result = HttpRequestUtil.httpPost(StartProcess+"?sysName=YDAC&loginName="+userName+"&title="+flowTitle+"&templateCode="+"YDAC_002"+"", (JSONObject) JSONObject.toJSON(requestMap),false);

                    Map resultInfo = (Map) result.get("data");

                    SysLog sysLog1 = new SysLog();
                    sysLog1.setExecutTime(creatTime);
                    sysLog1.setTaskName("流程发起接口");
                    sysLog1.setNote("流程发起接口接口参数为：" + JSONObject.toJSONString(requestMap) + " OA返回："+ JSONObject.toJSONString(resultInfo));
                    projectCluesDao.insertLogs(sysLog1);
                    if ("0".equals(result.get("code"))) {
                        map.setProcessId(resultInfo.get("summaryId")+"");
                        //保存记录
                        projectCluesDao.addDataViewPremissionRecord(map);
                        //判断是否存在历史权限审批 保留最新的 进行更新 其他逻辑删除
                        List<UserOrgRelForm> uu = projectCluesDao.getDataViewPremissionApprove(map);
                        if(!CollectionUtils.isEmpty(uu)){
                            String mainId = uu.get(0).getId();
                            projectCluesDao.updateDateViewPremissionStatus(uu);
                            map.setId(mainId);
                            projectCluesDao.updateDataViewPremission(map);
                        }else {
                            projectCluesDao.addDataViewPremission(map);
                        }
                    }else{
                        JSONObject error = JSONObject.parseObject(JSONObject.parseObject(result.get("data").toString()).get("error").toString());
                        String msg =error.get("message").toString();
                        reMsg[0] = reMsg[0].concat(msg).concat(",");
                        i[0]++;
                    }
                }else {
                    reMsg[0] = reMsg[0].concat("数据权限无变化 无需发起审批").concat(",");
                    j[0]++;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-12232,"申请提交失败！");
        }
        if(i[0] > 0 || j[0] > 0){
            String reMessage = "发起成功";
            if(j[0] > 0){
                reMessage = reMessage.concat("，其中"+j[0]+"条记录数据权限无变化，无需发起审批");
            }
            if(i[0] > 0){
                reMessage = reMessage.concat("，其中"+i[0]+"条记录发起失败 原因是："+reMsg[0]);
            }
            return ResultBody.success(reMessage);
        }else {
            return ResultBody.success("发起成功");
        }
    }

    public Boolean checkProcessChange(UserOrgRelForm now){
        //获取原权限信息
        UserOrgRelForm old = projectCluesDao.getDataViewPremissionMainInfo(now);
        //判断数据权限是否变化
        if(old == null){
            return true;
        }
        //项目
        if(!old.getProjectId().equals(now.getProjectId())){
            return true;
        }
        //组织
        List<String> oldOrgList = Arrays.asList(old.getOrgId().split(","));
        List<String> nowOrgList = Arrays.asList(now.getOrgId().split(","));
        if(!compareCollections(oldOrgList,nowOrgList)){
            return true;
        }
        //人员
        if(!old.getUserId().equals(now.getUserId())){
            return true;
        }
        //联系方式查看
        if(!old.getIsMobileShow().equals(now.getIsMobileShow())){
            return true;
        }
        //客户名称查看
        if(!old.getIsNameShow().equals(now.getIsNameShow())){
            return true;
        }
        //生效时间查看
        if(!old.getIsNoTime().equals(now.getIsNoTime())){
            return true;
        }else {
            if(old.getIsNoTime() == 0){// 0 有效期 1 不限时间
                //判断时间范围是否变化
                if(!old.getStartTime().equals(now.getStartTime())){
                    return true;
                }
                if(!old.getEndTime().equals(now.getEndTime())){
                    return true;
                }
            }
        }
        return false;
    }

    // 比较两个List<String>集合是否相同的方法
    public static boolean compareCollections(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        List<String> temp = new ArrayList<>(list1);
        temp.retainAll(list2);

        return temp.size() == list1.size();
    }


    @Override
    public ResultBody getDataViewPremissionList(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageNum")!=null){
            pageIndex = Integer.parseInt(map.get("pageNum")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        List<String> projectList = new ArrayList<>();
        if (map.get("projectList") != null) {
            projectList = Arrays.asList(String.valueOf(map.get("projectList")).split(","));
            map.put("projectIdList", projectList);
        }
        map.put("type",projectCluesDao.getUserJobHsxt(SecurityUtils.getUserId()));
        PageHelper.startPage(pageIndex, pageSize);
        //获取用户权限 如果存在管理员可以查看所以 其他权限只能查看当前专员
        List<UserOrgRelForm> list = projectCluesDao.getDataViewPremissionList(map);
        return ResultBody.success(new PageInfo<>(list));
    }

    @Override
    public ResultBody getDataViewPremissionDetail(Map map) {
        UserOrgRelForm dataViewPremission = projectCluesDao.getDataViewPremissionDetail(map);
        return ResultBody.success(dataViewPremission);
    }

    /**
     * 查询认知渠道
     *
     * @param map
     * @return
     */
    @Override
    public List<ResultProjectVO> getMainMediaList(Map map) {
        if (map == null) {
            throw new BadRequestException(-10_0000, "参数不能为空");
        }
        List<Map> mainMediaList = projectCluesDao.getMainMediaList(map);
        if (mainMediaList.size() > 0) {
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (Map mainMedia : mainMediaList) {
                if (mainMedia != null) {
                    ResultProjectVO resultProjectVO = new ResultProjectVO();
                    resultProjectVO.setValue(String.valueOf(mainMedia.get("DictCode")));
                    resultProjectVO.setLabel(String.valueOf(mainMedia.get("DictName")));
                    Map map1 = new HashMap();
                    map1.put("parentCode", mainMedia.get("DictCode"));
                    map1.put("projectId", map.get("projectId"));
                    map1.put("childCode", "0");
                    List<Map> subMediaList = projectCluesDao.getMainMediaList(map1);
                    if (subMediaList.size() > 0) {
                        List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                        for (Map subMedia : subMediaList) {
                            if (subMedia != null) {
                                ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                                resultProjectVO2.setValue(String.valueOf(subMedia.get("DictCode")));
                                resultProjectVO2.setLabel(String.valueOf(subMedia.get("DictName")));
                                resultProjectVOList2.add(resultProjectVO2);
                            }
                        }
                        resultProjectVO.setChildren(resultProjectVOList2);
                    }
                    resultProjectVOList.add(resultProjectVO);
                }

            }
            return resultProjectVOList;
        }
        return null;
    }

    /**
     * 查询认知渠道
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> getMainList(Map map) {
        if (map == null) {
            throw new BadRequestException(-10_0000, "参数不能为空");
        }
        return projectCluesDao.getMainMediaList(map);
    }

    /**
     * 置业顾问名片台账
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody getCardStandingBookList(Map map) {
//        List<Map> salesList = projectCluesDao.getSalesAttributionList(map);
//        map.put("salesList",salesList);
        PageHelper.startPage((int) map.get("current"), (int) map.get("size"));
        Page<CardStandingBook> list = projectCluesDao.getCardStandingBookList(map);
        return ResultBody.success(new PageInfo<CardStandingBook>(list));
    }

    /**
     * 导出置业顾问名片台账
     *
     * @param request
     * @param response
     * @param param
     */
    @Override
    public void cardStandingBookExport(HttpServletRequest request, HttpServletResponse response, String param) {
        ExcelForm paramMap = JSONObject.parseObject(param, ExcelForm.class);
        //声明变量
        String excelName = null;
        String basePath = "templates";
        String templatePath = "";
        ArrayList<Object[]> dataset = new ArrayList<>();
        //转化参数
        Map map = new HashMap();
        map.put("projectList", Arrays.asList(paramMap.getProjectId().split(",")));
        map.put("salesName", paramMap.getSalesName());
        map.put("salesMobile", paramMap.getSalesMobile());
        map.put("beginTime", paramMap.getBeginTime());
        map.put("endTime", paramMap.getEndTime());
//        //查询置业顾问
//        List<Map> salesList = projectCluesDao.getSalesAttributionList(map);
//        map.put("salesList",salesList);
        List<CardStandingBook> cardStandingBookList = projectCluesDao.getCardStandingBookList(map);
        try {
            excelName = "置业顾问名片台账";
            templatePath = basePath + File.separator + "cardStandingBook.xlsx";
            int num = 0;
            for (CardStandingBook model : cardStandingBookList) {
                num++;
                model.setRownum(num);
                Object[] oArray = model.toExproData();
                dataset.add(oArray);

            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            excelExportUtil.exportExcelTemplate(templatePath, dataset, excelName, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 、
     * 判断是否有无报备保护期内客户
     *
     * @param map
     * @return
     */
    @Override
    public Map getIsReport(Map map) {
        if (map.get("projectId") == null) {
            throw new BadRequestException(-10_0000, "校验是否有渠道报备失败，无项目id");
        }
        if (map.get("customerMobile") == null) {
            throw new BadRequestException(-10_0000, "校验是否有渠道报备失败，无客户手机号");
        }
        Map resultMap = new HashMap();
        String key = map.get("projectId").toString() + map.get("customerMobile").toString();
        boolean flag = false;
        if(redisUtil.setIfNull(key,true,1000)) {
            flag = true;
        }else{
            resultMap.put("code", "-1");
            resultMap.put("errmsg", "该客户正在被其他渠道分配请稍后重试");
            return resultMap;
        }
        try{
            //判断是否报备过
            List<Map> customMap = projectCluesDao.getCustomerMobile(map);
            if (customMap.size() > 0) {
                for (int i = 0; i < customMap.size(); i++) {
                    if ("2".equals(String.valueOf(customMap.get(i).get("ClueStatus")))) {
                        resultMap.put("code", "-1");
                        resultMap.put("errmsg", "该客户已到访");
                        return resultMap;
                    }
                }

                for (int i = 0; i < customMap.size(); i++) {
                    String reportUserName = String.valueOf(customMap.get(i).get("ReportUserName"));
                    if ("0".equals(String.valueOf(customMap.get(i).get("IsReportExpire")))) {
                        if (!String.valueOf(map.get("reportUserId")).equals(String.valueOf(customMap.get(i).get("ReportUserID")))
                                || !String.valueOf(map.get("projectClueId")).equals(String.valueOf(customMap.get(i).get("ProjectClueId")))) {
                            resultMap.put("code", "-1");
                            resultMap.put("errmsg", "该客户已被" + reportUserName + "有效报备，不可修改");
                            return resultMap;
                        }
                    }
                }
                resultMap.put("code", "0");
                return resultMap;
            } else {
                resultMap.put("code", "0");
                return resultMap;
            }
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("code", "-1");
            return resultMap;
        }finally {
            if (flag){
                redisUtil.del(key);
            }
        }

    }

    @Override
    public Map getIsReportList(Map map) {
        Map resultMap = new HashMap();
        //拿到线索id集合
        if (map.get("ids") == null || "".equals(map.get("ids"))) {
            throw new BadRequestException(-10_0000, "线索ID未传");
        }
        //查询所有线索
        String[] ids = map.get("ids").toString().split(",");
        List<String> clueIdList = new ArrayList<>();
        if (ids.length > 0) {
            clueIdList = Arrays.asList(ids);
        }
        String symbolb = StringUtils.join(ids, "','");
        symbolb = "'" + symbolb + "'";
        List<Map> cluesList = projectCluesDao.getCluesByIds(symbolb);
        int count = 0;
        //最终可以分配的ID
        List<String> endIds = new ArrayList<>();
        //重复手机号线索
        List<String> repeatList = new ArrayList<>();
        for (Map clueMap : cluesList) {
            //判断是否报备过
            List<Map> customMap = projectCluesDao.getCustomerMobile(clueMap);
            if (customMap.size() > 0) {
                boolean flag = true;
                for (int i = 0; i < customMap.size(); i++) {
                    if ("0".equals(String.valueOf(customMap.get(i).get("IsReportExpire")))
                            && (!String.valueOf(clueMap.get("reportUserId")).equals(String.valueOf(customMap.get(i).get("ReportUserID")))
                            || !String.valueOf(clueMap.get("ProjectClueId")).equals(String.valueOf(customMap.get(i).get("ProjectClueId"))))) {
                        count++;
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    if (!repeatList.contains(String.valueOf(clueMap.get("ProjectClueId")))) {
                        if (customMap.size() > 1) {
                            for (int i = 0; i < customMap.size(); i++) {
                                if (!String.valueOf(clueMap.get("ProjectClueId")).equals(String.valueOf(customMap.get(i).get("ProjectClueId")))
                                        && !"0".equals(String.valueOf(customMap.get(i).get("IsReportExpire")))) {
                                    if (clueIdList.contains(String.valueOf(customMap.get(i).get("ProjectClueId")))) {
                                        repeatList.add(String.valueOf(customMap.get(i).get("ProjectClueId")));
                                    }
                                }
                            }
                        }
                        if (!repeatList.contains(String.valueOf(clueMap.get("ProjectClueId")))) {
                            endIds.add(String.valueOf(clueMap.get("ProjectClueId")));
                        }
                    } else {
                        count++;
                    }
                }
            } else {
                endIds.add(clueMap.get("ProjectClueId") + "");
            }
        }
        if (count > 0) {
            String symbol = StringUtils.join(endIds.toArray(), ",");
            resultMap.put("count", count);
            resultMap.put("ids", symbol);
        } else {
            resultMap.put("ids", map.get("ids"));
            resultMap.put("count", 0);
        }
        return resultMap;
    }

    /*计算两日期之间的天数差*/
    public Integer dateDifference(String time1, String time2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(time1);
        Date date2 = format.parse(time2);
        int days = (int) ((date2.getTime() - date.getTime()) / (1000*3600*24));
        return days;
    }

    /**
     * 导出规则配置
     * */
    @Override
    public void ruleConfigurationExport(HttpServletResponse response) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<ProjectRuleDetail>  projectRuleDetails = projectCluesDao.selectProjectRulexport();

        if(projectRuleDetails!=null && projectRuleDetails.size()>0){
            ProjectRuleDetail projectRuleDetail = projectRuleDetails.get(0);
            String[] excelTitle = projectRuleDetail.getExcelTitle();
            for (ProjectRuleDetail p : projectRuleDetails){

                if (p.getProjectID().equals("-1")){

                    p.setRuleLevel("集团级");
                    p.setEntryName("系统");

                    if (p.getJudgeStage().equals("0")){
                        p.setJudgeStage("报备为准");
                    }else if(p.getJudgeStage().equals("1")){
                        p.setJudgeStage("到访为准");
                    }else if(p.getJudgeStage().equals("2")){
                        p.setJudgeStage("签约为准");
                    }
                    if (p.getJudgeNoRegion().equals("0")){
                        p.setJudgeNoRegion("启用");
                    }else{
                        p.setJudgeNoRegion("禁用");
                    }
                    if (p.getJudgeNoPool().equals("0")){
                        p.setJudgeNoPool("启用");
                    }else{
                        p.setJudgeNoPool("禁用");
                    }


                    if (p.getWqtJudgeStage().equals("0")){
                        p.setWqtJudgeStage("报备为准");
                    }else if (p.getWqtJudgeStage().equals("1")){
                        p.setWqtJudgeStage("到访为准");
                    }else if (p.getWqtJudgeStage().equals("2")){
                        p.setWqtJudgeStage("签约为准");
                    }
                    if (p.getWqtJudgeNoRegion().equals("0")){
                        p.setWqtJudgeNoRegion("启用");
                    }else{
                        p.setWqtJudgeNoRegion("禁用");
                    }
                    if (p.getWqtJudgeNoPool().equals("0")){
                        p.setWqtJudgeNoPool("启用");
                    }else{
                        p.setWqtJudgeNoPool("禁用");
                    }


                    if (p.getReferralJudgeStage().equals("0")){
                        p.setReferralJudgeStage("报备为准");
                    }else if (p.getReferralJudgeStage().equals("1")){
                        p.setReferralJudgeStage("到访为准");
                    }else if (p.getReferralJudgeStage().equals("2")){
                        p.setReferralJudgeStage("签约为准");
                    }
                    if (p.getReferralJudgeNoRegion().equals("0")){
                        p.setReferralJudgeNoRegion("启用");
                    }else{
                        p.setReferralJudgeNoRegion("禁用");
                    }
                    if (p.getReferralJudgeNoPool().equals("0")){
                        p.setReferralJudgeNoPool("启用");
                    }else{
                        p.setReferralJudgeNoPool("禁用");
                    }


                }else{
                    p.setFollowUpConfirmationTime("");
                    p.setJudgeStage("");
                    p.setJudgeNoRegion("");
                    p.setJudgeNoPool("");
                    p.setWqtJudgeStage("");
                    p.setWqtJudgeNoRegion("");
                    p.setWqtJudgeNoPool("");
                    p.setReferralJudgeStage("");
                    p.setReferralJudgeNoRegion("");
                    p.setReferralJudgeNoPool("");
                    if (p.getEntryName() != null){
                        if (p.getEntryName().contains("-")){
                            p.setRuleLevel("项目级");
                        }else{
                            p.setRuleLevel("区域级");
                        }
                    }

                }

                Object[] oArray = p.toData();
                dataset.add(oArray);
            }


            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel("规则配置", excelTitle, dataset, "规则配置", response, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void ExportUserAuthorityAll(HttpServletResponse response) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<UserAuthority> usersAndPositions = projectCluesDao.getUserAuthorityAll();

        if(usersAndPositions!=null && usersAndPositions.size()>0){
            UserAuthority projectRuleDetail = usersAndPositions.get(0);
            String[] excelTitle = projectRuleDetail.getExcelTitle();
            for (UserAuthority u : usersAndPositions){
                if(u.getIsNoTime() != null){
                    if (u.getIsNoTime().equals("1")){
                        u.setIsNoTime("不限时间");
                    }else if (u.getIsNoTime().equals("0")){
                        u.setIsNoTime("限制时间");
                    }
                }
                if (u.getIsNameShow() != null){
                    if (u.getIsNameShow().equals("1")){
                        u.setIsNameShow("全号");
                    }else if (u.getIsNameShow().equals("0")){
                        u.setIsNameShow("隐号");
                    }
                }
                if (u.getIsMobileShow() != null){
                    if (u.getIsMobileShow().equals("1")){
                        u.setIsMobileShow("全号");
                    }else if (u.getIsMobileShow().equals("0")){
                        u.setIsMobileShow("隐号");
                    }
                }

                Object[] oArray = u.toData();
                dataset.add(oArray);
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel("用户权限", excelTitle, dataset, "用户权限", response, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ResultBody getFeedAskCjList(FeedBackEc feedBackEc) {
        String type = feedBackEc.getType();
        if("0".equals(type)){
            List<FeedBackEc> list = projectCluesDao.getFeedAskCjList(feedBackEc);
            return ResultBody.success(list);
        }else if("1".equals(type)){
            int pageIndex = 1;
            int pageSize = 10;
            if (StringUtils.isNotEmpty(feedBackEc.getPageNum())){
                pageIndex = Integer.parseInt(feedBackEc.getPageNum());
            }
            if (StringUtils.isNotEmpty(feedBackEc.getPageSize())){
                pageSize = Integer.parseInt(feedBackEc.getPageSize());
            }
            if(CollectionUtils.isEmpty(feedBackEc.getProjectList())){
                List<String> proList = new ArrayList<>();
                proList.add("");
                feedBackEc.setProjectList(proList);
            }
            PageHelper.startPage(pageIndex, pageSize);
            List<FeedBackEc> list = projectCluesDao.getFeedBackEdList(feedBackEc);
            if (list!=null && list.size()>0){
                for (FeedBackEc rr:list) {
                    if(rr.getEnclosureList()!=null){
                        String[] ee = rr.getEnclosureList().split(",");
                        rr.setEnclosures(Arrays.asList(ee));
                    }
                }
            }
            return ResultBody.success(new PageInfo<>(list));
        }else {
            return null;
        }
    }

    @Override
    public ResultBody getFeedAskCjDetail(FeedBackEc feedBackEc) {
        FeedBackEc feedback = projectCluesDao.getFeedAskCjDetail(feedBackEc);
        return ResultBody.success(feedback);
    }

    @Override
    public ResultBody addOrEditFeedAskCj(FeedBackEc feedBackEc) {
        int i = 0;
        if(StringUtils.isEmpty(feedBackEc.getId())){
            feedBackEc.setType("0");
            feedBackEc.setCreator(SecurityUtils.getUserId());
            feedBackEc.setFeedBackUserId(SecurityUtils.getUserId());
            feedBackEc.setFeedBackUserName(SecurityUtils.getEmployeeName());
            feedBackEc.setFeedBackUserMobile(projectCluesDao.getUserMobile(SecurityUtils.getUserId()));
            i = projectCluesDao.addFeedAskCj(feedBackEc);
        }else {
            feedBackEc.setEditor(SecurityUtils.getUserId());
            i = projectCluesDao.updateFeedAskCj(feedBackEc);
        }
        return i == 1 ? ResultBody.success("操作成功！") :  ResultBody.error(400,"操作失败！");
    }

    @Override
    public void feedBackEdExcel(HttpServletRequest request, HttpServletResponse response,FeedBackEc feedBackEc) {
        List<Map> fileds = feedBackEc.getFileds();
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        List<FeedBackEc> projectCluesNewList = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = "问题反馈台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        projectCluesNewList = projectCluesDao.getFeedBackEdList(feedBackEc);;
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(feedBackEc));
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(feedBackEc.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(feedBackEc.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (projectCluesNewList != null && projectCluesNewList.size() > 0){
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (FeedBackEc model : projectCluesNewList) {
                model.setRownum(rowNum);
                Object[] oArray = model.toData(true,filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void operation() {
        List<TaskVo> taskVoList = taskDao.selectMothTask();
        if (!CollectionUtils.isEmpty(taskVoList)) {
            // 根据任务id,获取成员
            String taskIds = taskVoList.stream().map(e -> e.getId().toString()).
                collect(Collectors.joining("','", "'", "'"));
            List<TaskMember> userList = projectCluesDao.getUserByTaskId(taskIds);
            if(CollectionUtils.isEmpty(userList)) {
                return;
            }
            Map<String, List<TaskMember>> userMap = userList.stream().filter(a-> a.getReportCount() == null).collect(Collectors.groupingBy(TaskMember::getTaskId));
            //封装任务
            for (TaskVo task1 : taskVoList) {
                String taskId = task1.getId();
                for (TaskMember taskMember : userMap.get(taskId)) {
                    List<String> userIds = new ArrayList<>();
                    userIds.add(taskMember.getMemberId());
                    Map quMap = new HashMap();
                    quMap.put("projectId",task1.getProjectId());
                    quMap.put("taskId",taskId);
                    quMap.put("userIds",userIds);
                    quMap.put("startTime",task1.getStartTime());
                    quMap.put("endTime",task1.getEndTime());
                    Map complete = projectCluesDao.getTaskComplete(quMap);
                    taskMember.setReportCount(Integer.parseInt(complete.get("reportCompleteNum").toString()));
                    taskMember.setVisitCount(Integer.parseInt(complete.get("visitCompleteNum").toString()));
                    taskMember.setArriveCount(Integer.parseInt(complete.get("arriveCompleteNum").toString()));
                    taskMember.setRepeatVisitCount(Integer.parseInt(complete.get("repeatVisitCompleteNum").toString()));
                    taskMember.setDealCount(Integer.parseInt(complete.get("dealCompleteNum").toString()));
                    taskMember.setFirstVisitCount(Integer.parseInt(complete.get("firstVisitCompleteNum").toString()));
                    taskDao.updateTaskMember(taskMember);
                }
            }
        }
    }

    @Override
    public ResultBody callTurnTheClue(ReportCustomerForm reportCustomerForm) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ProjectClueId = null;
        try {
            if (reportCustomerForm.getCustomerName() == null){
                return ResultBody.error(400,"公司名称为空！");
            }
            if (reportCustomerForm.getEmployeeName() == null){
                return ResultBody.error(400,"用户姓名为空！");
            }
            if (reportCustomerForm.getUserId() == null){
                return ResultBody.error(400,"用户ID为空！");
            }
            if (reportCustomerForm.getOrgId() == null){
                return ResultBody.error(400,"用户部门ID为空！！");
            }
            if (reportCustomerForm.getOrgName() == null){
                return ResultBody.error(400,"用户部门名称为空！");
            }
            if (reportCustomerForm.getLongitude() == null){
                return ResultBody.error(400,"客户经度为空！");
            }
            if (reportCustomerForm.getLatitude() == null){
                return ResultBody.error(400,"客户纬度为空！");
            }
            if (reportCustomerForm.getSourceMode() == null){
                return ResultBody.error(400,"客户来源为空！");
            }
            if (reportCustomerForm.getCustomerAddress() == null) {
                return ResultBody.error(400,"客户地址为空！");
            }
            Map projectByOrgId = projectCluesDao.getProjectByOrgId(reportCustomerForm.getOrgId());
            reportCustomerForm.setProjectId(projectByOrgId.get("projectId")+"");
            reportCustomerForm.setProjectName(projectByOrgId.get("projectName")+"");
             ProjectClueId = UUID.randomUUID().toString();
            reportCustomerForm.setProjectClueId(ProjectClueId);
            reportCustomerForm.setClueStatus("0");
            //判断是区域专员还是项目专员
            String isRegion = projectByOrgId.get("isRegion")+"";
            reportCustomerForm.setReportUserRole("1".equals(isRegion)?"2":"1");
            reportCustomerForm.setCustomerLevel("E");
            //线索表新增
            projectCluesDao.insertProjectCluesCall(reportCustomerForm);

            //新增标注信息
            reportCustomerForm.setDimensionType("1");
            projectCluesDao.addDimensionCall(reportCustomerForm);
            //保存详细信息
            projectCluesDao.saveInformationZCall(reportCustomerForm);
        } catch (Exception e) {
//            throw new BadRequestException(-11_0006, e);
            return ResultBody.error(500,e.toString());
        }
        return ResultBody.success(ProjectClueId);
    }

    @Override
    public ResultBody callTurnTheClueRobot(List<Map> reportCustomerForm) {

            for (Map m : reportCustomerForm){
                //通过专员所属组织id查询项目ID
                Map projectByOrgId = projectCluesDao.getProjectByOrgId(m.get("sid")+"");
                m.put("projectId",projectByOrgId.get("projectId")+"");
                m.put("projectName",projectByOrgId.get("projectName")+"");
                String ProjectClueId = UUID.randomUUID().toString();
                m.put("projectClueId",ProjectClueId);
                m.put("clueStatus",0);
                //判断是区域专员还是项目专员
                String isRegion = projectByOrgId.get("isRegion")+"";
                m.put("reportUserRole","1".equals(isRegion)?"2":"1");
                //线索表新增
                projectCluesDao.insertProjectCluesCallTwo(m);
                Map userAll = projectCluesDao.getUserNameMobile(m.get("uid") + "");
                String userName = userAll.get("UserName")+"";
                String mobile = userAll.get("Mobile")+"";

                //发送钉钉
                String flowTitle="机器人给您分配了个客户，注意查收，客户名称："+m.get("customerName");
                try {
                    flowTitle = URLEncoder.encode(flowTitle,"UTF-8");
                    String resultD = HttpClientUtil.doPostSd("http://esb.vanyang.com.cn/dingding/corpconversation?content="+flowTitle+"&loginName="+userName+"&agentId=1942757632&title=短信验证码&sender=数字案场系统&sysName=数字案场系统&billUrl=", null);
                    System.out.println("钉钉发送="+resultD);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                //发送OA消息
                if(isSendOAMessage==1) {
                    HttpRequestUtil.httpGet(sendOAMessageUrl + "?method=unmsg&content=" + flowTitle + "&url=&h5url=&noneBindingReceiver=" + userName + "&appcode=" + sendOAMessageAppCode + "&sysName=移动案场系统", false);
                }
            }


        return ResultBody.success("添加成功");
    }

    @Override
    public PageInfo<TaskDetailVO> getTaskAccountDetail(TaskQueryVO taskQueryVO) {
        PageHelper.startPage(taskQueryVO.getPageIndex(), taskQueryVO.getPageSize());
        List<TaskDetailVO> taskDetailVOS = projectCluesDao.getTaskAccountDetail(taskQueryVO);
        return new PageInfo<>(taskDetailVOS);
    }

    @Override
    public ResultBody getGlAllocationPropertyConsultantZygwCall(Map map) {

        map.put("isManager", "0");
        //获取当前登录人是否存在可分配客户的权限
        List<Map> list = projectCluesDao.getAllocationAllCustomerUserOrgInfoCall(map.get("userId")+"");
        if(CollectionUtils.isEmpty(list)){
            return ResultBody.error(-1000_01,"暂无分配客户权限！");
        }
        List<String> proList = new ArrayList<>();//项目总监
        List<String> qyProList = new ArrayList<>();//区域总监
        //判断是否存在管理员权限
        final boolean[] isManager = {false};
        list.stream().forEach(x->{
            if("10001".equals(x.get("jobCode"))){
                isManager[0] = true;
            }else if("zszj".equals(x.get("jobCode"))){
                proList.add(x.get("projectId")+"");
            }else if("qyzszj".equals(x.get("jobCode"))){
                qyProList.add(x.get("projectId")+"");
            }else if ("qyfz".equals(x.get("jobCode"))){
                String comGUID = x.get("areaId")+"";
                proList.addAll(projectCluesDao.getProListD(comGUID));
                qyProList.addAll(projectCluesDao.getProList(comGUID));
            }
        });
        //获取权限内的权限专员 按区域 项目 团队 专员 分组
        if(isManager[0]){//管理员 可分配全系统人员
            map.put("isManager", "1");
        }else {//按权限查询
            map.put("proList", proList);
            map.put("qyProList", qyProList);
        }
        List<Map> reList = projectCluesDao.getGlAllocationPropertyConsultantZygwCall(map);
        return ResultBody.success(reList);
    }

    @Override
    public ResultBody isRobotPermissions(Map map) {

        List<Map> robotPermissions = projectCluesDao.isRobotPermissions(map);

        return ResultBody.success(robotPermissions.size()>0?1:0);
    }


    private String clueStatusToCn(String clueStatus){
        if("1".equals(clueStatus)){
            return "报备";
        }else if("2".equals(clueStatus)){
            return "到访";
        }else if("8".equals(clueStatus)){
            return "签约";
        }else{
            return "";
        }
    }

    /**
     * 保存跟进核验记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveFollowupVerificationRecord(FollowupVerificationRecordVo verificationVo) {
        //防止同时进入核验
        if (redisUtil.setIfNull("followupVerification"+verificationVo.getFollowRecordId(),"111",300)) {
            //根据跟进记录的整改状态判断当前跟进是否已发起核验
            FollowUpRecordVO f = projectCluesDao.getFollowUpRecordById(verificationVo.getFollowRecordId());
            if("1".equals(f.getRectificationStatus()) || "2".equals(f.getRectificationStatus())){
                return ResultBody.error(400,"该跟进记录核验中，请等待！");
            }
            //判断发起人是否已核验过此跟进
            FollowupVerificationRecordVo oldVerificationRecord = projectCluesDao.getFollowupVerificationRecord(verificationVo);
            if(oldVerificationRecord != null) {
                return ResultBody.error(400, "该跟进记录你已核验过，请勿重复核验!");
            }
            if(StringUtils.isEmpty(verificationVo.getApprovalReason())){
                return ResultBody.error(500,"核验意见不能为空！");
            }
            //保存核验记录
            String type = verificationVo.getType();
            if ("2".equals(type)){
                verificationVo.setVerificationStatus("0");//核验状态 不合格
                verificationVo.setRectificationStatus("1");//整改状态 待整改
                verificationVo.setVerificationResult("2");//核验结果 不合格
                //修改客户跟进交易日志表是否统计状态为否
                projectCluesDao.updateCustomerFodRecordIsStatistics(verificationVo.getFollowRecordId(),"0");
                //发送系统消息+OA消息+钉钉消息 提示专员跟进记录核验不合格 请及时整改
                //发送系统消息
                Message message = new Message();
                message.setSubject("【跟进核验通知】");
                message.setContent("【跟进核验通知】您的客户【"+f.getCustomerName()+"】跟进核验不合格，请及时整改。");
                message.setSender("");
                message.setMessageType(2120);
                message.setIsDel(0);
                message.setReceiver(f.getUserId());
                message.setIsRead(0);
                message.setIsPush(2);
                message.setIsNeedPush(2);
                message.setProjectClueId(f.getProjectClueId());
                message.setOpportunityClueId(f.getOpportunityClueId());
                message.setProjectId(f.getProjectId());
                projectCluesDao.insertOneMessage(message);
                String content = "您的客户【"+f.getCustomerName()+"】跟进核验不合格，请及时整改。";
                String userName = f.getUserName();
                //发送OA
                content = content.replaceAll(" ","_");
                if(isSendOAMessage==1){
                    HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url=&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                }
                //发送钉钉
                String flowTitle="您的客户【"+f.getCustomerName()+"】跟进核验不合格，请及时整改。";
                try {
                    flowTitle = URLEncoder.encode(flowTitle,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                String resultD = HttpClientUtil.doPostSd("http://esb.vanyang.com.cn/dingding/corpconversation?content="+flowTitle+"&loginName="+userName+"&agentId=1942757632&title=短信验证码&sender=数字案场系统&sysName=数字案场系统&billUrl=", null);
                System.out.println("钉钉发送="+resultD);
            }else {
                verificationVo.setVerificationStatus("1");//核验状态 合格
                verificationVo.setRectificationStatus("0");//整改状态 无需整改
                verificationVo.setVerificationResult("1");//核验结果 合格
            }
            verificationVo.setVersionNum(String.valueOf(projectCluesDao.getFollowupVerificationVersionNum(verificationVo) + 1));
            //保存核验记录
            int a = projectCluesDao.saveFollowupVerificationRecord(verificationVo);
            //更新跟进记录
            int b = projectCluesDao.updateFollowUpRecordInsVrf(verificationVo);
            return a > 0 && b > 0 ? ResultBody.success("核验成功!") : ResultBody.error(500, "核验失败，请重试！");
        }else {
            return ResultBody.error(-10002,"有人正在核验待办不可操作！");
        }
    }

    @Override
    public ResultBody getFollowupVerificationRecordList(FollowUpRecordVO followUpRecordVO) {
        if (followUpRecordVO.getSearch() != null && !"".equals(followUpRecordVO.getSearch())){
            String search = followUpRecordVO.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                followUpRecordVO.setCustomerMobile(search);
            } else {
                followUpRecordVO.setCustomerName(search);
            }
        }
        int pageNum = 1;
        int pageSize = 10;
        if (followUpRecordVO.getPageNum() != null && followUpRecordVO.getPageNum() != "" ){
            pageNum = Integer.parseInt(followUpRecordVO.getPageNum());
        }
        if (followUpRecordVO.getPageSize() != null && followUpRecordVO.getPageSize() != ""){
            pageSize = Integer.parseInt(followUpRecordVO.getPageSize());
        }
        if(CollectionUtils.isEmpty(followUpRecordVO.getProjectList())){
            List<String> proList = new ArrayList<>();
            proList.add("");
            followUpRecordVO.setProjectList(proList);
        }
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                followUpRecordVO.getProjectList());
        followUpRecordVO.setOrgIds(orgIds);
        followUpRecordVO.setPageNum("");
        int i = (pageNum - 1) * pageSize;
        followUpRecordVO.setPageIndex(String.valueOf(i));
        followUpRecordVO.setPageSize(String.valueOf(pageSize));
        int total = projectCluesDao.getFollowUpRecordVerificationCount(followUpRecordVO);
        List<FollowUpRecordVO> list = projectCluesDao.getFollowUpVerificationRecordList(followUpRecordVO);
        if (list!=null && list.size()>0){
            for (FollowUpRecordVO map : list) {
                String enclosures = map.getEnclosures();
                String drawingQuotationUrls = map.getDrawingQuotationUrls();
                if (!StringUtils.isEmpty(enclosures)){
                    String[] ss = enclosures.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    map.setEnclosure(enclosure);
                }
                if (!StringUtils.isEmpty(drawingQuotationUrls)){
                    String[] ss = drawingQuotationUrls.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    map.setDrawingQuotationUrl(enclosure);
                }
            }
        }
        Map resultMap = new HashMap<>();
        resultMap.put("list",list);
        resultMap.put("total",total);
        return ResultBody.success(resultMap);
    }

    @Override
    public void getFollowupVerificationRecordListExport(HttpServletRequest request, HttpServletResponse response, FollowUpRecordVO followUpRecordVO) {
        List<Map> fileds = followUpRecordVO.getFileds();
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        List<FollowUpRecordVO> followUpRecordList = new ArrayList<>();
        if (followUpRecordVO.getSearch() != null && !"".equals(followUpRecordVO.getSearch())){
            String search = followUpRecordVO.getSearch();
            //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
            if (search.matches("[0-9]+")) {
                followUpRecordVO.setCustomerMobile(search);
            } else {
                followUpRecordVO.setCustomerName(search);
            }
        }
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                followUpRecordVO.getProjectList());
        followUpRecordVO.setOrgIds(orgIds);
        //导出的文档下面的名字
        String excelName = "跟进核验记录台账";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        followUpRecordList = projectCluesDao.getFollowUpVerificationRecordList(followUpRecordVO);
        if (followUpRecordList!=null && followUpRecordList.size()>0){
            for (FollowUpRecordVO map : followUpRecordList) {
                String enclosures = map.getEnclosures();
                String threeOnesUrls = map.getThreeOnesUrls();
                String drawingQuotationUrls = map.getDrawingQuotationUrls();
                if (!StringUtils.isEmpty(enclosures)){
                    String[] ss = enclosures.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    map.setEnclosure(enclosure);
                }
                if (!StringUtils.isEmpty(drawingQuotationUrls)){
                    String[] ss = drawingQuotationUrls.split(",");
                    List<String> enclosure = Arrays.asList(ss);
                    map.setDrawingQuotationUrl(enclosure);
                }
            }
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if (!CollectionUtils.isEmpty(followUpRecordVO.getProjectList())) {
            Map proMap = excelImportMapper.getAreaNameAndProNames(followUpRecordVO.getProjectList());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(JSON.toJSONString(followUpRecordVO));
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (followUpRecordList != null && followUpRecordList.size() > 0){
            String isAllStr = followUpRecordVO.getIsAll();
            boolean isAll = true;
            if ("1".equals(isAllStr)) isAll = false;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (FollowUpRecordVO model : followUpRecordList) {
                model.setRownum(rowNum);
                Object[] oArray = model.toData2(isAll,filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResultBody getFollowupVerificationRecordOnTab(String followRecordId) {
        if(StringUtils.isEmpty(followRecordId)){
            return ResultBody.error(-100003, "跟进记录id不能为空");
        }
        //查询跟进所有的核验记录
        List<FollowupVerificationRecordVo> followupVerificationRecordList = projectCluesDao.getFollowupVerificationRecordByFollowRecordId(followRecordId);
        if(followupVerificationRecordList == null || followupVerificationRecordList.size() == 0){
            return ResultBody.error(-100004,"暂无数据");
        }
        //获取核验记录id集合
        List<String> ids = followupVerificationRecordList.stream().map(FollowupVerificationRecordVo::getId).collect(Collectors.toList());
        //根据id集合获取所有的整改记录
        List<FollowupRectificationRecordVo> followupRectificationRecordList = projectCluesDao.getFollowupRectificationRecordByFollowupVerificationRecordIds(ids);
        //循环分组处理整改数据 保存进入核验记录中
        if(followupRectificationRecordList != null && followupRectificationRecordList.size() > 0){
            Map<String,List<FollowupRectificationRecordVo>> verificationRecordMap = followupRectificationRecordList.stream().collect(Collectors.groupingBy(FollowupRectificationRecordVo::getFollowVerificationRecordId));
            followupVerificationRecordList.forEach(x -> {
                x.setFollowupRectificationRecordVoList(verificationRecordMap.get(x.getId()));
            });
        }
        return ResultBody.success(followupVerificationRecordList);

    }

    @Override
    public ResultBody getFollowupRectificationRecordOnTab(String followVerificationRecordId) {
        if(StringUtils.isEmpty(followVerificationRecordId)){
            return ResultBody.error(-100003, "跟进核验记录id不能为空");
        }
        //根据跟进核验的所有的整改记录
        Map reMap = new HashMap<>();
        List<Map> reList = new ArrayList<>();
        List<FollowupRectificationRecordVo> followupRectificationRecordList = projectCluesDao.getFollowupVerificationAllRecordList(followVerificationRecordId);
        reMap.put("verificationCreateTime",followupRectificationRecordList.get(0).getVerificationCreateTime());
        reMap.put("verificationmUserName",followupRectificationRecordList.get(0).getVerificationmUserName());
        reMap.put("verificationStatus",followupRectificationRecordList.get(0).getVerificationStatus());
        reMap.put("rectificationStatus",followupRectificationRecordList.get(0).getRectificationStatus());
        reMap.put("verificationApprovalReason",followupRectificationRecordList.get(0).getVerificationApprovalReason());
        reMap.put("followupRectificationRecordList",reList);
        return ResultBody.success(reMap);
    }

    /**
     * 批量调整客户过保及预警时间
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateBatchCustomerExpireDate(ExcelForm projectCluesForm) {
        if (CollectionUtils.isEmpty(projectCluesForm.getOpportunityList())){
            return ResultBody.error(2001, "请选择需调整的客户！");
        }
        //判断当前登录人是否有管理员调整客户过保及预警时间权限
        List<Map> glQx = projectCluesDao.getGlBatchUpdateCustomerStatusQx(SecurityUtils.getUserId());
        if(CollectionUtils.isEmpty(glQx)){
            return ResultBody.error(2001, "暂无管理员批量调整客户过保及预警时间权限 请调整！");
        }
        String salesFollowExpireDate = projectCluesForm.getSalesFollowExpireDate();
        String salesFollowExpireWarningDate = projectCluesForm.getSalesFollowExpireWarningDate();
        String reason = projectCluesForm.getReason();
        StringBuffer enclosurseUrl = new StringBuffer();
        for (int i = 0; i < projectCluesForm.getFileList().size(); i++) {
            Map<String, String> x = projectCluesForm.getFileList().get(i);
            enclosurseUrl.append(x.get("enclosureUrl"));

            // 如果不是最后一个元素，则追加逗号
            if (i < projectCluesForm.getFileList().size() - 1) {
                enclosurseUrl.append(",");
            }
        }
        String customerChangeNodeBatchId = UUID.randomUUID().toString();
        List<String> oppIds = projectCluesForm.getOpportunityList();
        //需要更新的数据
        List<Map> udpMap = new ArrayList<>();
        oppIds.stream().forEach(x->{
            //获取客户信息
            Map oldCustMap = projectCluesDao.getCustomerProAndAreaAllInfo(x);
            oldCustMap.put("projectClueId",oldCustMap.get("ProjectClueId"));
            oldCustMap.put("opportunityClueId",oldCustMap.get("OpportunityClueId"));
            oldCustMap.put("projectId",oldCustMap.get("projectId"));
            oldCustMap.put("projectName",oldCustMap.get("ProjectName"));
            oldCustMap.put("confirmId",SecurityUtils.getUserId());
            oldCustMap.put("confirmPersonName",SecurityUtils.getEmployeeName());
            oldCustMap.put("confirmTime",new Date());
            oldCustMap.put("reason",reason);
            oldCustMap.put("applyDateTime",new Date());
            oldCustMap.put("salesId",oldCustMap.get("SalesAttributionId"));
            oldCustMap.put("salesName",oldCustMap.get("SalesAttributionName"));
            String note = "将客户过保时间由";
            if(ObjectUtils.isNotEmpty(oldCustMap.get("SalesFollowExpireDate"))){
                note += oldCustMap.get("SalesFollowExpireDate");
            }else{
                note += "空";
            }
            note += "调整为"+salesFollowExpireDate+",将客户过保预警时间由";
            if (ObjectUtils.isNotEmpty(oldCustMap.get("SalesFollowExpireWarningDate"))){
                note += oldCustMap.get("SalesFollowExpireWarningDate");
            }else {
                note += "空";
            }
            note += "调整为"+salesFollowExpireWarningDate;
            oldCustMap.put("note",note);
            oldCustMap.put("type","2");
            oldCustMap.put("enclosurseUrl",String.valueOf(enclosurseUrl));
            oldCustMap.put("customerChangeNodeBatchId",customerChangeNodeBatchId);
            oldCustMap.put("creator",SecurityUtils.getUserId());
            oldCustMap.put("createTime",new Date());
            oldCustMap.put("updator",SecurityUtils.getUserId());
            oldCustMap.put("updateTime",new Date());
            //保存客户变更节点记录
            projectCluesDao.addCustomerChangeNodeRecord(oldCustMap);

            Map map = new HashMap<>();
            map.put("opportunityClueId",oldCustMap.get("OpportunityClueId")+"");
            map.put("salesFollowExpireDate",salesFollowExpireDate);
            map.put("salesFollowExpireWarningDate",salesFollowExpireWarningDate);
            udpMap.add(map);
        });
        if (udpMap.size() > 0) {
            int size = udpMap.size();
            //每次更新100条
            int count = size % 100 == 0 ? size / 100 : size / 100 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<Map> fformList = udpMap.subList(s * 100, size);
                    projectCluesDao.updateBatchCustomerExpireDate(fformList);
                } else {
                    List<Map> fformList = udpMap.subList(s * 100, (s + 1) * 100);
                    projectCluesDao.updateBatchCustomerExpireDate(fformList);
                }
            }
        }
        return ResultBody.success("操作成功！");
    }

    /**
     * 批量设置客户最大跟进次数
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateBatchCustomerMaxFollowUp(ExcelForm projectCluesForm) {
        if (CollectionUtils.isEmpty(projectCluesForm.getOpportunityList())){
            return ResultBody.error(2001, "请选择需调整的客户！");
        }
        //判断当前登录人是否有管理员调整客户最大跟进次数权限
        List<Map> glQx = projectCluesDao.getGlBatchUpdateCustomerStatusQx(SecurityUtils.getUserId());
        if(CollectionUtils.isEmpty(glQx)){
            return ResultBody.error(2001, "暂无管理员批量调整客户最大跟进次数权限 请调整！");
        }
        String maxFollowUp = projectCluesForm.getMaxFollowUp();
        String reason = projectCluesForm.getReason();
        StringBuffer enclosurseUrl = new StringBuffer();
        for (int i = 0; i < projectCluesForm.getFileList().size(); i++) {
            Map<String, String> x = projectCluesForm.getFileList().get(i);
            enclosurseUrl.append(x.get("enclosureUrl"));

            // 如果不是最后一个元素，则追加逗号
            if (i < projectCluesForm.getFileList().size() - 1) {
                enclosurseUrl.append(",");
            }
        }
        String customerChangeNodeBatchId = UUID.randomUUID().toString();
        List<String> oppIds = projectCluesForm.getOpportunityList();
        //需要更新的数据
        List<Map> udpMap = new ArrayList<>();
        oppIds.stream().forEach(x->{
            //获取客户信息
            Map oldCustMap = projectCluesDao.getCustomerProAndAreaAllInfo(x);
            oldCustMap.put("projectClueId",oldCustMap.get("ProjectClueId"));
            oldCustMap.put("opportunityClueId",oldCustMap.get("OpportunityClueId"));
            oldCustMap.put("projectId",oldCustMap.get("projectId"));
            oldCustMap.put("projectName",oldCustMap.get("ProjectName"));
            oldCustMap.put("confirmId",SecurityUtils.getUserId());
            oldCustMap.put("confirmPersonName",SecurityUtils.getEmployeeName());
            oldCustMap.put("confirmTime",new Date());
            oldCustMap.put("reason",reason);
            oldCustMap.put("applyDateTime",new Date());
            oldCustMap.put("salesId",oldCustMap.get("SalesAttributionId"));
            oldCustMap.put("salesName",oldCustMap.get("SalesAttributionName"));
            String note = "将客户最大跟进次数由";
            if(ObjectUtils.isNotEmpty(oldCustMap.get("maxFollowUp"))){
                note += oldCustMap.get("maxFollowUp");
            }else{
                note += "空";
            }
            note += "调整为"+maxFollowUp;
            oldCustMap.put("note",note);
            oldCustMap.put("type","3"); // 类型3表示最大跟进次数调整
            oldCustMap.put("enclosurseUrl",String.valueOf(enclosurseUrl));
            oldCustMap.put("customerChangeNodeBatchId",customerChangeNodeBatchId);
            oldCustMap.put("creator",SecurityUtils.getUserId());
            oldCustMap.put("createTime",new Date());
            oldCustMap.put("updator",SecurityUtils.getUserId());
            oldCustMap.put("updateTime",new Date());
            //保存客户变更节点记录
            projectCluesDao.addCustomerChangeNodeRecord(oldCustMap);

            Map map = new HashMap<>();
            map.put("opportunityClueId",oldCustMap.get("OpportunityClueId")+"");
            map.put("maxFollowUp",maxFollowUp);
            udpMap.add(map);
        });
        if (udpMap.size() > 0) {
            int size = udpMap.size();
            //每次更新100条
            int count = size % 100 == 0 ? size / 100 : size / 100 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<Map> fformList = udpMap.subList(s * 100, size);
                    projectCluesDao.updateBatchCustomerMaxFollowUp(fformList);
                } else {
                    List<Map> fformList = udpMap.subList(s * 100, (s + 1) * 100);
                    projectCluesDao.updateBatchCustomerMaxFollowUp(fformList);
                }
            }
        }
        return ResultBody.success("操作成功！");
    }

    /**
     * 批量调整客户过保及预警时间（支持每个客户单独设置）
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateBatchCustomerExpireDateByDays(BatchUpdateCustomerExpireForm batchUpdateCustomerExpireForm) {
        if (CollectionUtils.isEmpty(batchUpdateCustomerExpireForm.getOpportunityList())){
            return ResultBody.error(2001, "请选择需调整的客户！");
        }
        //判断当前登录人是否有管理员调整客户过保及预警时间权限
        List<Map> glQx = projectCluesDao.getGlBatchUpdateCustomerStatusQx(SecurityUtils.getUserId());
        if(CollectionUtils.isEmpty(glQx)){
            return ResultBody.error(2001, "暂无管理员批量调整客户过保及预警时间权限 请调整！");
        }
        
        String reason = batchUpdateCustomerExpireForm.getReason();
        StringBuffer enclosurseUrl = new StringBuffer();
        
        // 处理附件URL
        if (batchUpdateCustomerExpireForm.getFileList() != null && !batchUpdateCustomerExpireForm.getFileList().isEmpty()) {
            for (int i = 0; i < batchUpdateCustomerExpireForm.getFileList().size(); i++) {
                Map<String, String> x = batchUpdateCustomerExpireForm.getFileList().get(i);
                enclosurseUrl.append(x.get("enclosureUrl"));

                // 如果不是最后一个元素，则追加逗号
                if (i < batchUpdateCustomerExpireForm.getFileList().size() - 1) {
                    enclosurseUrl.append(",");
                }
            }
        }
        
        String customerChangeNodeBatchId = UUID.randomUUID().toString();
        List<BatchUpdateCustomerExpireForm.CustomerExpireInfo> opportunityList = batchUpdateCustomerExpireForm.getOpportunityList();
        
        //需要更新的数据
        List<Map> udpMap = new ArrayList<>();
        opportunityList.stream().forEach(customerExpireInfo -> {
            String opportunityId = customerExpireInfo.getId();
            String salesFollowExpireDate = customerExpireInfo.getSalesFollowExpireDate();
            String salesFollowExpireWarningDate = customerExpireInfo.getSalesFollowExpireWarningDate();
            
            //获取客户信息
            Map oldCustMap = projectCluesDao.getCustomerProAndAreaAllInfo(opportunityId);
            oldCustMap.put("projectClueId",oldCustMap.get("ProjectClueId"));
            oldCustMap.put("opportunityClueId",oldCustMap.get("OpportunityClueId"));
            oldCustMap.put("projectId",oldCustMap.get("projectId"));
            oldCustMap.put("projectName",oldCustMap.get("ProjectName"));
            oldCustMap.put("confirmId",SecurityUtils.getUserId());
            oldCustMap.put("confirmPersonName",SecurityUtils.getEmployeeName());
            oldCustMap.put("confirmTime",new Date());
            oldCustMap.put("reason",reason);
            oldCustMap.put("applyDateTime",new Date());
            oldCustMap.put("salesId",oldCustMap.get("SalesAttributionId"));
            oldCustMap.put("salesName",oldCustMap.get("SalesAttributionName"));
            
            String note = "将客户过保时间由";
            if(ObjectUtils.isNotEmpty(oldCustMap.get("SalesFollowExpireDate"))){
                note += oldCustMap.get("SalesFollowExpireDate");
            }else{
                note += "空";
            }
            note += "调整为"+salesFollowExpireDate+",将客户过保预警时间由";
            if (ObjectUtils.isNotEmpty(oldCustMap.get("SalesFollowExpireWarningDate"))){
                note += oldCustMap.get("SalesFollowExpireWarningDate");
            }else {
                note += "空";
            }
            note += "调整为"+salesFollowExpireWarningDate;
            
            oldCustMap.put("note",note);
            oldCustMap.put("type","2");
            oldCustMap.put("enclosurseUrl",String.valueOf(enclosurseUrl));
            oldCustMap.put("customerChangeNodeBatchId",customerChangeNodeBatchId);
            oldCustMap.put("creator",SecurityUtils.getUserId());
            oldCustMap.put("createTime",new Date());
            oldCustMap.put("updator",SecurityUtils.getUserId());
            oldCustMap.put("updateTime",new Date());
            //保存客户变更节点记录
            projectCluesDao.addCustomerChangeNodeRecord(oldCustMap);

            Map map = new HashMap<>();
            map.put("opportunityClueId",oldCustMap.get("OpportunityClueId")+"");
            map.put("salesFollowExpireDate",salesFollowExpireDate);
            map.put("salesFollowExpireWarningDate",salesFollowExpireWarningDate);
            udpMap.add(map);
        });
        
        if (udpMap.size() > 0) {
            int size = udpMap.size();
            //每次更新100条
            int count = size % 100 == 0 ? size / 100 : size / 100 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<Map> fformList = udpMap.subList(s * 100, size);
                    projectCluesDao.updateBatchCustomerExpireDate(fformList);
                } else {
                    List<Map> fformList = udpMap.subList(s * 100, (s + 1) * 100);
                    projectCluesDao.updateBatchCustomerExpireDate(fformList);
                }
            }
        }
        return ResultBody.success("操作成功！");
    }

    @Override
    public ResultBody unlockInterfaceLimit(String userName) {
        if(StringUtils.isEmpty(userName)){
            return ResultBody.error(400,"用户名不能为空");
        }
        String key = redisKey + ":" + userName;
        String date = DateUtil.format(new Date(), "yyyy-MM-dd");
        //判断用户是否存在且到达上线
        Object value = redisTemplate.opsForHash().get(key, date);
        if(ObjectUtils.isNotEmpty(value)){
            redisTemplate.opsForHash().put(key,date,0);
            return ResultBody.success("解锁成功");
        }
        return ResultBody.error(400,"未获取到该用户信息");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody publicPoolAverageDistribution(SalesAttributionForm salesAttributionForm) {
        List<String> delIds = new ArrayList<>();
        try {
            if (StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId()) && (salesAttributionForm.getProjectClueList()==null || salesAttributionForm.getProjectClueList().size()==0)){
                return ResultBody.error(2001, "请选择需分配的客户！");
            }
            List<String> oppIds = new ArrayList<>();
            if (!StringUtils.isEmpty(salesAttributionForm.getOpportunityClueId())){
                oppIds.add(salesAttributionForm.getOpportunityClueId());
            }else{
                oppIds = salesAttributionForm.getProjectClueList();
            }
            //针对选择的公池客户增加redis锁 限制其他操作 如果客户已被锁定 移除该客户 加入有人正在操作此客户 不可重复操作集合
            List<String> ids = salesAttributionForm.getIds();
            for (String id : ids){
                if (redisUtil.setIfNull(id,"111",300)){
                    delIds.add(id);
                }else {
                    return ResultBody.error(-10002,"有人正在捞取客户不可操作！");
                }
            }
            List<CustomerDistributionInfo> customerDistributionList = salesAttributionForm.getCustomerDistributionList();
            if (CollectionUtils.isEmpty(customerDistributionList)){
                return ResultBody.error(2001, "请选择需分配到的专员！");
            }
            if (customerDistributionList.size() > 50){
                return ResultBody.error(2001, "分配人员选择超出上限！");
            }
            if (customerDistributionList.size() > oppIds.size()){
                return ResultBody.error(2001, "选择的专员数需小于勾选的客户数！");
            }
            //可分配的机会客户
            List<String> okOppIds = new ArrayList<>();
            //不可分配的机会ID 无分配权限
            List<String> notQxOppIds = new ArrayList<>();
            //不可分配的机会ID 客户规则判重
            List<String> notPcProOppIds = new ArrayList<>();
            //不可分配的机会ID 历史分配不可再分配
            List<String> notHistoryOppIds = new ArrayList<>();
            //不可分配的机会ID 客户已达上限无法分配
            List<String> notMaxOppIds = new ArrayList<>();
            //不可分配的机会ID 客户状态已变更不可再分配
            List<String> notChangeOppIds = new ArrayList<>();
            //判断当天分配是否达到上限 20000
            int dCount = oppIds.size();
            int lCount = projectCluesDao.getAllowedCount(salesAttributionForm.getUserId());
            if(lCount >= 20000){
                return ResultBody.error(2001, "当天分配客户已达到上限 20000 个，请明天再来！");
            }
            if(dCount + lCount > 20000){
                return ResultBody.error(2002, "本次分配客户已达当天分配客户数上限 20000 个，当天已分配"+lCount+"，请重新选择或明日再来！");
            }
            //根据分配客户集合获取全部客户信息
            List<Map> oldList = projectCluesDao.getOpportunityByIds(oppIds);
            //根据分配客户集合获取全部客户公客池信息
            List<Map> pcList = projectCluesDao.getPublicPoolOpportunityByIds(oppIds);
            //将客户公客池信息按照客户维度分组 便于后面根据客户ID获取客户 进行分配客户集合处理
            Map<String, List<Map>> pcMap = pcList.stream().collect(Collectors.groupingBy(x -> x.get("OpportunityClueId") + ""));
            //查询有分配权限的项目
            List<String> qxProList = new ArrayList<>();
            List<Map> list = projectCluesDao.getAllocationAllCustomerUserOrgInfo(salesAttributionForm.getUserId());
            //判断是否存在管理员权限
            String poolType = salesAttributionForm.getPoolType();
            final boolean[] isManager = {false};
            list.stream().forEach(x->{
                if("3".equals(poolType)){//全国池 看有没有分配权限
                    if("10001".equals(x.get("jobCode"))){
                        isManager[0] = true;
                    }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                        isManager[0] = true;
                    }else if("qyzszj".equals(x.get("jobCode"))){
                        isManager[0] = true;
                    }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                        isManager[0] = true;
                    }
                }else if("2".equals(poolType)){//区域池 看岗位所在组织对应的区域id下的所有项目
                    if("10001".equals(x.get("jobCode"))){
                        isManager[0] = true;
                    }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                        String comGUID = x.get("areaId")+"";
                        qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                    }else if("qyzszj".equals(x.get("jobCode"))){
                        String comGUID = x.get("areaId")+"";
                        qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                    }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                        String comGUID = x.get("areaId")+"";
                        qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                    }
                }else if("1".equals(poolType)){//项目池 看岗位所在组织对应的项目
                    if("10001".equals(x.get("jobCode"))){
                        isManager[0] = true;
                    }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                        qxProList.add(x.get("projectId")+"");
                    }else if("qyzszj".equals(x.get("jobCode"))){
                        qxProList.add(x.get("projectId")+"");
                    }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                        String comGUID = x.get("areaId")+"";
                        qxProList.addAll(projectCluesDao.getProListAll(comGUID));
                    }
                }
            });
            //判重客户 获取所有分配专员名下的客户
            Map map1 = new HashMap<>();
            map1.put("userIds",customerDistributionList);
            map1.put("projectId",customerDistributionList.get(0).getProjectId());
            List<Map> disClus = projectCluesDao.getClueCstNMInfo(map1);
            //获取客户名称集合和客户联系方式集合 这两个集合用于循环客户信息集合 去除客户名称或者联系方式在我这个集合内的客户
            List<String> cstNList = new ArrayList<>();
            List<String> cstMList = new ArrayList<>();
            disClus.stream().forEach(x->{
                cstNList.add(x.get("customerName")+"");
                cstMList.add(x.get("customerMobile")+"");
            });
            //循环客户信息集合 首先判重是否有分配权限 如果没有 移除该客户信息 如果有 判断该客户的客户名称或者客户联系方式是否在客户名称或者联系方式集合内 如果在 移除该客户信息
            List<Map> newList = oldList.stream().filter(x->{
                List<Map> pool = pcMap.get(x.get("OpportunityClueId")+"");
                if (pool != null && pool.size() > 1){
                    throw new RuntimeException("公客池客户【"+x.get("CustomerName")+"】数据异常 请联系管理员！");
                }
                if (!isManager[0] && !qxProList.contains(x.get("projectId")+"")){//无权限分配
                    notQxOppIds.add(x.get("OpportunityClueId")+"");
                    return false;
                }else if (pool == null || pool.size() == 0){//公客池捞取状态是否变更
                    notChangeOppIds.add(x.get("OpportunityClueId")+"");
                    return false;
                }else {
                    if (cstNList.contains(x.get("CustomerName")+"") || cstMList.contains(x.get("CustomerMobile")+"")){//客户判重
                        notPcProOppIds.add(x.get("OpportunityClueId")+"");
                        return false;
                    }else{
                        return true;
                    }
                }
            }).collect(Collectors.toList());
            //获取线索客户ID集合
            List<String> clueIds = newList.stream().map(x->x.get("ProjectClueId")+"").collect(Collectors.toList());
            //获取客户线索信息
            List<ReportCustomerForm> reportCustomerFormList = projectCluesDao.getProjectClueByIds(clueIds);
            Map<String,ReportCustomerForm> reportCustomerFormMap = reportCustomerFormList.stream().collect(Collectors.toMap(x->x.getProjectClueId(), x->x));
            //获取客户全部详细信息
            List<InformationVO> informationVOList = projectCluesDao.getInformationInfoByIds(clueIds, "zygw", "1", "1", null, null);
            Map<String,InformationVO> informationVOMap = informationVOList.stream().collect(Collectors.toMap(x->x.getProjectClueId(), x->x));
            //查询客户附件信息
            List<Map> enclosuresList = projectCluesDao.getOppEnclosuresByIds(oppIds);
            Map<String, List<String>> enclosuresMap = enclosuresList.stream().collect(Collectors.groupingBy(x -> x.get("OpportunityClueId").toString(),
                            Collectors.mapping( x -> Optional.ofNullable(x.get("enclosureUrl")).orElse("").toString(),Collectors.toList())
                    ));
            //获取公客池客户分配上限和每个专员的已存在分配客户数
            String reportUserRole = "";
            if ("zygw".equals(customerDistributionList.get(0).getJobCode())){
                reportUserRole = "1";
            }
            if ("qyzygw".equals(customerDistributionList.get(0).getJobCode())){
                reportUserRole = "2";
            }
            //判断系统配置规则
            ProjectRuleDetail projectRuleDetailXt = projectCluesDao.selectProjectRuleZs("-1","2");
            //查询项目规则
            String ComGUID = projectCluesDao.getComGUIDByProject(customerDistributionList.get(0).getProjectId());// 区域ID
            //查询规则计算报备逾期及预警时间
            ProjectRuleDetail projectRuleDetail = null;
            if ("1".equals(reportUserRole)){
                projectRuleDetail = projectCluesDao.selectProjectRuleZs(customerDistributionList.get(0).getProjectId(),"2");
                //项目没有配置规则 查询区域的
                if (projectRuleDetail==null){
                    projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
                }
            }else if ("2".equals(reportUserRole)){
                projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
            }
            //没有区域的查询集团的
            if (projectRuleDetail==null){
                projectRuleDetail = projectRuleDetailXt;
            }
            //公客池分配保护时间
            String salesFollowExpireDate = "";
            String sendTime = "";
            String sendTimeStr = "";
            String assignPoolsExpireDaysStr = "";
            int assignPoolsExpireDays = 0;
            if (projectRuleDetailXt!=null) {
                assignPoolsExpireDaysStr = projectRuleDetailXt.getAssignPoolsExpireDays();
                if (StringUtils.isNotEmpty(assignPoolsExpireDaysStr)){
                    assignPoolsExpireDays = Integer.parseInt(assignPoolsExpireDaysStr);
                }
            }
            if (StringUtils.isNotEmpty(assignPoolsExpireDaysStr)){
                Date today = new Date();
                Date dBefore = new Date();
                Calendar calendar = Calendar.getInstance(); //得到日历
                calendar.setTime(today);//把当前时间赋给日历
                calendar.add(Calendar.DAY_OF_MONTH, assignPoolsExpireDays);
                dBefore = calendar.getTime();
                salesFollowExpireDate = sf.format(dBefore);
                sendTime = sf.format(dBefore);
                sendTimeStr = assignPoolsExpireDaysStr;
            }else{
                salesFollowExpireDate = null;
                sendTime = "无";
                sendTimeStr = "永久";
            }
            //公客池客户分配上限
            int maxCount = Integer.parseInt(projectRuleDetail.getAllocationCustomerMax());
            //每个分配人员的已拥有分配客户数
            List<Map> userAllowedClueCount = projectCluesDao.getUserAllowedClueCount(customerDistributionList,customerDistributionList.get(0).getProjectId());
            //将上述集合处理成客户根据客户ID获取的形式
            Map<String,Integer> userAllowedClueCountMap = new HashMap<>();
            userAllowedClueCount.stream().forEach(x->{
                userAllowedClueCountMap.put(x.get("userId")+"",Integer.parseInt(x.get("count")+""));
            });
            final String[] sendCustomerName = {""};
            final String[] sendCustomerOppId = {""};
            final String[] sendCustomerClueId = {""};
            String result = "分配成功";
            boolean resFlag = false;
            //处理用户组织信息集合 前端限制只能选择一个项目 按照客户ID 分组处理即可
            Map<String,Map> userOrgMap = new HashMap<>();
            Map param = new HashMap();
            String projectId = customerDistributionList.get(0).getProjectId();
            String projectName = customerDistributionList.get(0).getProjectName();
            String jobCode = customerDistributionList.get(0).getJobCode();
            param.put("ids", customerDistributionList.stream().map(CustomerDistributionInfo::getSalesAttributionId).collect(Collectors.toList()));
            param.put("projectId", projectId);
            param.put("jobCode",jobCode);
            List<Map> orgList = projectCluesDao.getUserOrgByIds(param);
            userOrgMap = orgList.stream().collect(Collectors.toMap(x->x.get("userId")+"", x->x));
            //分配批次信息
            Map customerDistributionBatch = new HashMap();
            String redistributionBatchId = UUID.randomUUID().toString();
            //分配批次编码 默认生成规则 日期+四位随机数
            String redistributionBatchCode = sfr.format(DateUtil.date())+ RandomUtil.randomNumbers(4);
            int countNumber = 0;
//            StringBuffer customerFullNames = new StringBuffer();
//            StringBuffer customerHideNames = new StringBuffer();
//            StringBuffer customerFullMobiles = new StringBuffer();
//            StringBuffer customerHideMobiles = new StringBuffer();
            StringBuffer projectIds = new StringBuffer();
            StringBuffer projectNames = new StringBuffer();
            StringBuffer opportunityIds = new StringBuffer();
            StringBuffer salesAttributionNames   = new StringBuffer();
            //分配记录信息
            List<Map> customerDistributionRecords = new ArrayList<>();
            //需要新增的线索客户信息
            List<ReportCustomerForm> addClueList = new ArrayList<>();
            List<Map> cluesEnclosures = new ArrayList<>();
            //需要更新的线索客户信息
            List<Map> udpClueList = new ArrayList<>();
            //需要更新的报备客户信息
            List<Map> udpOppList = new ArrayList<>();
            //需要保存的客户等级变化记录
            List<CustomerLevelRecordVo> clList = new ArrayList<>();
            //需要保存的客户报备日志记录
            List<CustomerAddLogVo> caLogList = new ArrayList<>();
            //保存需要发送消息的记录
            List<Map> sendMsgList = new ArrayList<>();
            //处理历史归属客户分配情况和超出客户上限无法分配情况
            Map isHistory = new HashMap<>();//历史归属客户
            int index = 0;//超出客户上限无法分配客户
            //如果客户列表有数据 进行分配
//            Collections.shuffle(newList); // 打乱客户顺序
            while (!CollectionUtils.isEmpty(newList)) {
                if (!isHistory.isEmpty()) {
                    // 使用迭代器查找并删除 isHistory
                    Iterator<Map> iterator = newList.iterator();
                    while (iterator.hasNext()) {
                        Map<String, Object> item = iterator.next();
                        if (item.equals(isHistory)) {
                            notHistoryOppIds.add(isHistory.get("OpportunityClueId") + "");
                            iterator.remove();
                            isHistory = new HashMap<>();
                            break;
                        }
                    }
                }
                if (index == customerDistributionList.size()) {
                    if (!newList.isEmpty()) {
                        // 使用迭代器删除第一个元素
                        Iterator<Map> iterator = newList.iterator();
                        if (iterator.hasNext()) {
                            Map<String, Object> firstItem = iterator.next();
                            notMaxOppIds.add(firstItem.get("OpportunityClueId") + "");
                            iterator.remove();
                        }
                    }
                    index = 0;
                }
                //循环分配人员 判断客户归属
                Iterator<CustomerDistributionInfo> customerIterator = customerDistributionList.iterator();
                while (customerIterator.hasNext()) {
                    CustomerDistributionInfo customerDistributionInfo = customerIterator.next();
                    //判断是否可分配
                    String salesAttributionId = customerDistributionInfo.getSalesAttributionId();
                    String salesAttributionName = customerDistributionInfo.getSalesAttributionName();
                    //按照筛选后符合条件的客户进行分配
                    Iterator<Map> newListIterator = newList.iterator();
                    while (newListIterator.hasNext()) {
                        index++;
                        Map oldMap = newListIterator.next();
                        //获取机会信息
                        String OpportunityClueId = oldMap.get("OpportunityClueId") + "";
                        //根据客户的原归属 判断历史归属人员和当前人员是否是同一人
                        if (salesAttributionId.equals(oldMap.get("SalesAttributionId") + "")) {//如果是 跳过该客户 判断下一个客户
                            if (isHistory.isEmpty()) {
                                isHistory = oldMap;
                            }
                        } else {
                            //判断专员是否到达分配上限 如果没有到达 正常分配 如果到达分配上限 结束客户循环 跳过该专员
                            if (userAllowedClueCountMap.get(salesAttributionId) >= maxCount) {
                                break;
                            }
                            //保存分配记录
                            oldMap.put("ConfirmID", salesAttributionForm.getUserId());
                            oldMap.put("ConfirmTime", new Date());
                            oldMap.put("ApplyDatetime", new Date());
                            oldMap.put("OldSalesId", oldMap.get("SalesAttributionId"));
                            oldMap.put("OldSalesName", oldMap.get("SalesAttributionName"));
                            oldMap.put("SalesId", salesAttributionId);
                            oldMap.put("SalesName", salesAttributionName);
                            oldMap.put("Type", "1");
                            oldMap.put("mainProjectId", projectId);
                            oldMap.put("mainProjectName", projectName);
                            oldMap.put("RedistributionBatchId", redistributionBatchId);
                            oldMap.put("RedistributionBatchCode", redistributionBatchCode);
                            customerDistributionRecords.add(oldMap);

                            //获取分配客户组织信息
                            Map orgMap = userOrgMap.get(salesAttributionId);

                            //判断线索客户是否存在
                            String ProjectClueId = String.valueOf(oldMap.get("ProjectClueId"));
                            ReportCustomerForm reportCustomerForm = reportCustomerFormMap.get(ProjectClueId);
                            if (reportCustomerForm == null) {
                                //获取客户原信息
                                reportCustomerForm = new ReportCustomerForm();
                                InformationVO informationVO = informationVOMap.get(ProjectClueId);
                                BeanUtils.copyProperties(informationVO, reportCustomerForm);
                                reportCustomerForm.setProjectClueUuid(ProjectClueId);
                                reportCustomerForm.setProjectClueId(ProjectClueId);
                                reportCustomerForm.setClueStatus("11");
                                reportCustomerForm.setUserId(salesAttributionId);
                                reportCustomerForm.setEmployeeName(salesAttributionName);
                                reportCustomerForm.setOrgId(String.valueOf(orgMap.get("orgId")));
                                reportCustomerForm.setOrgName(String.valueOf(orgMap.get("orgName")));
                                reportCustomerForm.setProjectId(projectId);
                                reportCustomerForm.setProjectName(projectName);
                                reportCustomerForm.setDisTime(sf.format(DateUtil.date()));
                                reportCustomerForm.setDisPerson(salesAttributionForm.getUserId());
                                reportCustomerForm.setDisPersonName(salesAttributionForm.getEmployeeName());
                                reportCustomerForm.setDisOpportunityClueId(OpportunityClueId);
                                //设置保护期
                                reportCustomerForm.setSalesFollowExpireDate(salesFollowExpireDate);
                                reportCustomerForm.setCustomerLevel("E");
                                //判断客户分配前的客户等级 若为A/B/ 则需要更新为D级
                                if (informationVO.getCustomerLevel().equals("A") || informationVO.getCustomerLevel().equals("B") || informationVO.getCustomerLevel().equals("C")) {
                                    reportCustomerForm.setCustomerLevel("D");

                                    //客户等级变化记录 客户等级日志表
                                    CustomerLevelRecordVo clMap = new CustomerLevelRecordVo();
                                    clMap.setOpportunityCueId(String.valueOf(oldMap.get("OpportunityClueId")));
                                    clMap.setProjectClueId(String.valueOf(oldMap.get("ProjectClueId")));
                                    clMap.setCustomerLevel("D");
                                    clMap.setSalesAttributionId(String.valueOf(oldMap.get("SalesAttributionId")));
                                    clMap.setSalesAttributionName(String.valueOf(oldMap.get("SalesAttributionName")));
                                    clMap.setSalesAttributionTeamId(String.valueOf(oldMap.get("SalesAttributionTeamId")));
                                    clMap.setSalesAttributionTeamName(String.valueOf(oldMap.get("SalesAttributionTeamName")));
                                    clMap.setProjectId(String.valueOf(oldMap.get("projectId")));
                                    clMap.setAreaId(ComGUID);
                                    clMap.setCreator(salesAttributionForm.getUserId());
                                    clList.add(clMap);
                                }
                                //线索表新增
                                addClueList.add(reportCustomerForm);
                                //更新报备客户信息
                                Map oppMap = new HashMap<>();
                                oppMap.put("OpportunityClueId", OpportunityClueId);
                                oppMap.put("projectId", reportCustomerForm.getProjectId());
                                oppMap.put("projectName", reportCustomerForm.getProjectName());
                                oppMap.put("salesAttributionId", reportCustomerForm.getUserId());
                                oppMap.put("salesAttributionName", reportCustomerForm.getEmployeeName());
                                oppMap.put("salesAttributionTeamId", reportCustomerForm.getOrgId());
                                oppMap.put("salesAttributionTeamName", reportCustomerForm.getOrgName());
                                oppMap.put("CustomerLevel", reportCustomerForm.getCustomerLevel());
                                oppMap.put("salesFollowExpireDate", oldMap.get("SalesFollowExpireDate"));
                                oppMap.put("salesFollowExpireWarningDate", oldMap.get("SalesFollowExpireWarningDate"));
                                udpOppList.add(oppMap);
                                //查询附件
                                List<String> enclosures = enclosuresMap.get(OpportunityClueId);
                                //新增附件表
                                if (enclosures != null && enclosures.size() > 0) {
                                    Map param1 = new HashMap();
                                    param1.put("enclosureUrl", enclosures);
                                    param1.put("ProjectClueId", ProjectClueId);
                                    param1.put("enclosureType", "1");
                                    cluesEnclosures.add(param1);
                                }
                            } else {
                                Map map = new HashMap();
                                map.put("salesFollowExpireDate", salesFollowExpireDate);
                                map.put("disOpportunityClueId", OpportunityClueId);
                                map.put("salesAttributionId", salesAttributionId);
                                map.put("salesAttributionName", salesAttributionName);
                                map.put("salesAttributionTeamId", orgMap.get("orgId"));
                                map.put("salesAttributionTeamName", orgMap.get("orgName"));
                                map.put("ProjectClueId", oldMap.get("ProjectClueId"));
                                map.put("clueStatus", "11");
                                map.put("disPerson", salesAttributionForm.getUserId());
                                map.put("disPersonName", salesAttributionForm.getEmployeeName());
                                map.put("projectId", projectId);
                                map.put("ProjectName", projectName);
                                //判断客户分配前的客户等级 若为A/B/ 则需要更新为D级
                                if (String.valueOf(oldMap.get("CustomerLevel")).equals("A") || String.valueOf(oldMap.get("CustomerLevel")).equals("B") || String.valueOf(oldMap.get("CustomerLevel")).equals("C")) {
                                    map.put("CustomerLevel", "D");

                                    //客户等级变化记录 客户等级日志表
                                    CustomerLevelRecordVo clMap = new CustomerLevelRecordVo();
                                    clMap.setOpportunityCueId(String.valueOf(oldMap.get("OpportunityClueId")));
                                    clMap.setProjectClueId(String.valueOf(oldMap.get("ProjectClueId")));
                                    clMap.setCustomerLevel("D");
                                    clMap.setSalesAttributionId(String.valueOf(oldMap.get("SalesAttributionId")));
                                    clMap.setSalesAttributionName(String.valueOf(oldMap.get("SalesAttributionName")));
                                    clMap.setSalesAttributionTeamId(String.valueOf(oldMap.get("SalesAttributionTeamId")));
                                    clMap.setSalesAttributionTeamName(String.valueOf(oldMap.get("SalesAttributionTeamName")));
                                    clMap.setProjectId(String.valueOf(oldMap.get("projectId")));
                                    clMap.setAreaId(ComGUID);
                                    clMap.setCreator(salesAttributionForm.getUserId());
                                    clList.add(clMap);
                                } else {
                                    map.put("CustomerLevel", String.valueOf(oldMap.get("CustomerLevel")));
                                }
                                udpClueList.add(map);
                                //更新报备客户信息
                                Map oppMap = new HashMap<>();
                                oppMap.put("OpportunityClueId", OpportunityClueId);
                                oppMap.put("projectId", projectId);
                                oppMap.put("projectName", projectName);
                                oppMap.put("salesAttributionId", salesAttributionId);
                                oppMap.put("salesAttributionName", salesAttributionName);
                                oppMap.put("salesAttributionTeamId", orgMap.get("orgId"));
                                oppMap.put("salesAttributionTeamName", orgMap.get("orgName"));
                                oppMap.put("CustomerLevel", String.valueOf(map.get("CustomerLevel")));
                                oppMap.put("salesFollowExpireDate", oldMap.get("SalesFollowExpireDate"));
                                oppMap.put("salesFollowExpireWarningDate", oldMap.get("SalesFollowExpireWarningDate"));
                                udpOppList.add(oppMap);
                            }
                            //新增客户报备日志记录
                            CustomerAddLogVo customerAddLogVo = new CustomerAddLogVo();
                            customerAddLogVo.setAreaId(ComGUID);
                            customerAddLogVo.setProjectId(projectId);
                            customerAddLogVo.setOpportunityClueId(oldMap.get("OpportunityClueId") + "");
                            customerAddLogVo.setProjectClueId(oldMap.get("ProjectClueId") + "");
                            customerAddLogVo.setCustomerName(oldMap.get("CustomerName") + "");
                            customerAddLogVo.setCustomerMobile(oldMap.get("CustomerMobile") + "");
                            customerAddLogVo.setSalesAttributionId(salesAttributionId);
                            customerAddLogVo.setSalesAttributionName(salesAttributionName);
                            customerAddLogVo.setSalesAttributionTeamId(orgMap.get("orgId") + "");
                            customerAddLogVo.setSalesAttributionTeamName(orgMap.get("orgName") + "");
                            //获取历史该客户信息
                            customerAddLogVo.setIsThreeOnes(oldMap.get("IsThreeOnes") + "");
                            customerAddLogVo.setIsThreeOnesDate(oldMap.get("IsThreeOnesDate") + "");
                            customerAddLogVo.setAddType("5");
                            //判断客户是否报备过该项目
                            String isAdd = projectCluesDao.getCustomerAddLogToIsAdd(customerAddLogVo);
                            customerAddLogVo.setIsAdd(isAdd);
                            customerAddLogVo.setReportCreateTime(sf.format(DateUtil.date()));
                            customerAddLogVo.setIsEffective("1");
                            caLogList.add(customerAddLogVo);

                            //保存发送消息的数据
                            Map message = new HashMap();
                            message.put("sendCustomerName", oldMap.get("CustomerName"));
                            message.put("sendCustomerOppId", oldMap.get("OpportunityClueId"));
                            message.put("sendCustomerClueId", oldMap.get("ProjectClueId"));
                            message.put("salesAttributionId", salesAttributionId);
                            sendMsgList.add(message);

                            //将客户加入可分配集合
                            okOppIds.add(OpportunityClueId);
                            //该专员分配客户数量+1
                            userAllowedClueCountMap.put(salesAttributionId, userAllowedClueCountMap.get(salesAttributionId) + 1);
                            //删除已分配客户
                            newListIterator.remove();
                            //分配成功处理批次数据
//                            customerFullNames.append(oldMap.get("CustomerName") + ",");
//                            customerHideNames.append(oldMap.get("reCustomerName") + ",");
//                            customerHideMobiles.append(oldMap.get("CustomerMobile") + ",");
//                            customerFullMobiles.append(oldMap.get("reCustomerMobile") + ",");
                            opportunityIds.append(oldMap.get("OpportunityClueId") + ",");
                            //如果项目ID没有 就添加进入
                            if (!projectIds.toString().contains(oldMap.get("projectId") + "")) {
                                projectIds.append(oldMap.get("projectId") + ",");
                                projectNames.append(projectMapper.getProjectNameByProjectId(oldMap.get("projectId") + "") + ",");
                            }
                            //如果人员没有 就添加进入
                            if (!salesAttributionNames.toString().contains(salesAttributionName)) {
                                salesAttributionNames.append(salesAttributionName + ",");
                            }

                            countNumber++;

                            index = 0;

                            isHistory = new HashMap<>();

                            break;//跳出循环
                        }
                    }
                }
            }
            //平均分配归属完成 开始处理数据
            if (okOppIds.size()>0){
                //保存分配批次
                customerDistributionBatch.put("id",redistributionBatchId);
                customerDistributionBatch.put("batch_code",redistributionBatchCode);
                customerDistributionBatch.put("createUser",salesAttributionForm.getUserId());
                customerDistributionBatch.put("createTime",DateUtil.date());
                customerDistributionBatch.put("countNumber",countNumber);
                customerDistributionBatch.put("type","2");
//                customerDistributionBatch.put("customer_full_names",customerFullNames.substring(0,customerFullNames.length()-1));
//                customerDistributionBatch.put("customer_hide_names",customerHideNames.substring(0,customerHideNames.length()-1));
//                customerDistributionBatch.put("customer_full_mobiles",customerFullMobiles.substring(0,customerFullMobiles.length()-1));
//                customerDistributionBatch.put("customer_hide_mobiles",customerHideMobiles.substring(0,customerHideMobiles.length()-1));
                customerDistributionBatch.put("project_ids",projectIds.substring(0,projectIds.length()-1));
                customerDistributionBatch.put("project_names",projectNames.substring(0,projectNames.length()-1));
                customerDistributionBatch.put("sales_attribution_names  ",salesAttributionNames.substring(0,projectNames.length()-1));
                customerDistributionBatch.put("projectId",customerDistributionList.get(0).getProjectId());
                // 将对象转换为 JSON 字符串
                Gson gson = new Gson();
                customerDistributionBatch.put("sales_attribution_info",gson.toJson(customerDistributionList));//保存参数
                //批量保存分配记录
                projectCluesDao.addRelCustomerRecordBatch(customerDistributionRecords);
                //处理客户信息 需要新增的和需要编辑的
                if(!CollectionUtils.isEmpty(addClueList)){
                    projectCluesDao.insertProjectCluesBatch(addClueList);
                }
                if (!CollectionUtils.isEmpty(cluesEnclosures))
                    projectCluesDao.addCluesEnclosuresBatch(cluesEnclosures);

                if (!CollectionUtils.isEmpty(udpClueList)){
                    projectCluesDao.updateClueCstBatch(udpClueList);
                }
                projectCluesDao.updateOppCstBatch(udpOppList);
                //批量保存客户等级变化记录
                if (!CollectionUtils.isEmpty(clList)){
                    projectCluesDao.insertCustomerLevelChangeLogBatch(clList);
                }
                //批量保存客户报备日志记录
                projectCluesDao.disableCutomerAddLogBatch(caLogList);//设置历史该客户状态
                projectCluesDao.saveCustomerAddLogBatch(caLogList);//保存客户报备日志
                //批量更新公池数据
                projectCluesDao.delPublicOpps(okOppIds);
                //处理消息发送
                List<MessageForm> messageList = new ArrayList<>();
                List<Map> oaMsgList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(sendMsgList)){
                    //将发送消息记录按人员分组
                    Map<String, List<Map>> sendMsgMap = sendMsgList.stream().collect(Collectors.groupingBy(m -> m.get("salesAttributionId")+""));
                    //循环每个人员 处理需要发送的消息
                    String finalSendTime = sendTime;
                    String finalSendTimeStr = sendTimeStr;
                    sendMsgMap.forEach((k, v)->{
                        if(v.size() == 1){
                            sendCustomerName[0] = "新的客户【"+v.get(0).get("sendCustomerName")+"】";
                            sendCustomerOppId[0] = String.valueOf(v.get(0).get("sendCustomerOppId"));
                            sendCustomerClueId[0] = String.valueOf(v.get(0).get("sendCustomerClueId"));
                        }else {
                            sendCustomerName[0] = v.size()+"个新的客户";
                            sendCustomerOppId[0] = "";
                            sendCustomerClueId[0] = "";
                        }
                        //系统消息
                        MessageForm message = new MessageForm();
                        message.setSubject("【公客池分配客户通知】");
                        String content;
                        if("无".equals(finalSendTime)){
                            content = "【公客池分配客户通知】您已被分配"+ sendCustomerName[0] +"至线索客户列表，客户为永久保护，请及时转报备！";
                            message.setContent("【公客池分配客户通知】 您已被分配"+ sendCustomerName[0] +"至线索客户列表，客户为永久保护，请及时转报备！");
                        }else {
                            content = "【公客池分配客户通知】您已被分配"+ sendCustomerName[0] +"至线索客户列表，客户为保护"+ finalSendTime +"天，请在"+ finalSendTimeStr +"天内及时转报备，否则将自动掉回公客池，过保时间为："+finalSendTime+"。";
                            message.setContent("【公客池分配客户通知】 您已被分配"+ sendCustomerName[0] +"至线索客户列表，请在"+ finalSendTimeStr +"天内及时转报备，否则将自动掉回公客池，过保时间为："+ finalSendTime +"。");
                        }
                        message.setSender("");
                        message.setMessageType(2106);
                        message.setIsDel(0);
                        message.setReceiver(k);
                        message.setIsRead("0");
                        message.setIsPush("2");
                        message.setIsNeedPush(2);
                        message.setProjectClueId(sendCustomerClueId[0]);
                        message.setOpportunityClueId(sendCustomerOppId[0]);
                        message.setProjectId(salesAttributionForm.getProjectId());
                        messageList.add(message);
                        //OA消息
                        Map oaMessage = new HashMap<>();
                        Map mobileMap = messageMapper.getUserMobile(k);
                        if (mobileMap != null && !StringUtils.isEmpty(mobileMap.get("Mobile") + "") && !StringUtils.isEmpty(finalSendTime)){
                            //发送短信
                            if(!"无".equals(finalSendTime)){
                                String userName = mobileMap.get("UserName")+"";
                                content = content.replaceAll(" ","");
                                oaMessage.put("content",content);
                                oaMessage.put("userName",userName);
                                oaMsgList.add(oaMessage);
                            }
                        }
                    });
                }
                //批量发送系统消息
                messageMapper.insertMessage(messageList);
                //批量发送OA消息
                if (!CollectionUtils.isEmpty(oaMsgList)){
                    oaMsgList.stream().forEach(x->{
                        //发送短信改为发送OA
                        if(isSendOAMessage==1){
                            HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+x.get("content")+"&url=&h5url=&noneBindingReceiver="+x.get("userName")+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                        }
                    });
                }
            }
            if (notQxOppIds.size()>0 || notPcProOppIds.size()>0 || notHistoryOppIds.size()>0 || notMaxOppIds.size()>0 || notChangeOppIds.size()>0){
                resFlag = true;
                result = result.concat(okOppIds.size()+"条,其中");
            }
            if (notQxOppIds.size()>0){
                result = result.concat(notQxOppIds.size()+"个公池客户无权限分配,");
            }
            if (notPcProOppIds.size()>0){
                result = result.concat(notPcProOppIds.size()+"个公池客户项目上已存在无法分配,");
            }
            if (notHistoryOppIds.size()>0){
                result = result.concat(notHistoryOppIds.size()+"个公池客户历史分配过无法分配,");
            }
            if (notMaxOppIds.size()>0){
                result = result.concat(notMaxOppIds.size()+"个公池客户超过专员接收上限无法分配,");
            }
            if (notChangeOppIds.size()>0){
                result = result.concat(notChangeOppIds.size()+"个公池客户状态已变更无法分配,");
            }
            if(resFlag){
                result = result.substring(0,result.length()-1).concat("！");
            }else {
                result = result.concat("！");
            }
            customerDistributionBatch.put("redistributionResult",result);
            projectCluesDao.addRelCustomerBatch(customerDistributionBatch);
            return ResultBody.success(result);
        }catch (Exception e){
            for (String id : delIds){
                redisUtil.del(id);
            }
            throw new RuntimeException("分配公客池客户异常！");
        }finally {
            //针对选择的客户 解除redis锁访问限制
            for (String id : delIds){
                redisUtil.del(id);
            }
        }
    }

    @Override
    public PageInfo queryPublicPoolAverageRedistributionRecord(Map map) {
//        if(CollectionUtils.isEmpty((Collection<?>) map.get("projectList"))){
//            return new PageInfo<>();
//        }
        String type = map.get("type")+"";
        List<CustomerDistributionRecordsVO> list = new ArrayList<>();
        if ("1".equals(type)){//批次记录
            //获取用户权限 如果存在管理员可以查看所有 不是管理员只能查看自己操作的
            int isManger = projectCluesDao.getUserJobHsxt(SecurityUtils.getUserId());
            if (isManger == 0){
                map.put("userId",SecurityUtils.getUserId());
            }
            PageHelper.startPage((Integer) map.get("pageNum"), (Integer) map.get("pageSize"));
            list = projectCluesDao.queryPublicPoolAverageRedistributionRecord(map);
        }else {//详情记录
            //获取批次记录信息
            String batchInfo = projectCluesDao.queryPublicPoolAverageRedistributionInfo(map);
            map.put("batchInfo",batchInfo);
            PageHelper.startPage((Integer) map.get("pageNum"), (Integer) map.get("pageSize"));
            list = projectCluesDao.queryPublicPoolAverageRedistributionDetailRecord(map);
        }
        PageInfo<CustomerDistributionRecordsVO> page = new PageInfo<>(list);
        return page;
    }

    /**
     * 获取登录人权限内可分配客户的项目
     * */
    @Override
    public ResultBody getGlAllocationPropertyConsultantPro(Map map) {
        map.put("isManager", "0");
        //获取当前登录人是否存在可分配客户的权限
        List<Map> list = projectCluesDao.getAllocationAllCustomerUserOrgInfo(SecurityUtils.getUserId());
        if(CollectionUtils.isEmpty(list)){
            return ResultBody.error(-1000_01,"暂无分配客户权限！");
        }
        List<String> proList = new ArrayList<>();//项目总监
        List<String> qyProList = new ArrayList<>();//区域总监
        //判断是否存在管理员权限
        final boolean[] isManager = {false};
        list.stream().forEach(x->{
            if("10001".equals(x.get("jobCode"))){
                isManager[0] = true;
            }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                proList.add(x.get("projectId")+"");
            }else if("qyzszj".equals(x.get("jobCode"))){
                qyProList.add(x.get("projectId")+"");
            }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                String comGUID = x.get("areaId")+"";
                proList.addAll(projectCluesDao.getProListD(comGUID));
                qyProList.addAll(projectCluesDao.getProList(comGUID));
            }
        });
        //获取权限内的权限专员 按区域 项目 团队 专员 分组
        if(isManager[0]){//管理员 可分配全系统人员
            map.put("isManager", "1");
        }else {//按权限查询
            List<String> pList = new ArrayList<>();
            pList.addAll(proList);
            pList.addAll(qyProList);
            map.put("pList", pList);
        }
        List<Map> reList = projectCluesDao.getGlAllocationPropertyConsultantPro(map);
        return ResultBody.success(new PageInfo<>(reList));
    }

    /**
     * 获取登录人权限内可分配客户的项目下的置业顾问
     * */
    @Override
    public ResultBody getGlAllocationPropertyConsultantProZygw(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageNum")!=null){
            pageIndex = Integer.parseInt(map.get("pageNum")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        List<String> proList = new ArrayList<>();//项目总监
        List<String> qyProList = new ArrayList<>();//区域总监
        map.put("isManager", "0");
        String projectId = Collections.singletonList(String.valueOf(map.get("projectId"))).get(0);
        //判断项目是普通项目还是区域项目
        String isRegino = projectMapper.getProIsRegion(projectId);
        if ("1".equals(isRegino)){
            qyProList.add(projectId);
        }else {
            proList.add(projectId);
        }
        //获取权限内的权限专员 按区域 项目 团队 专员 分组
        map.put("proList", proList);
        map.put("qyProList", qyProList);
        //限制经理、总监、营销经理
        List<String> proIds = new ArrayList<>();
        proIds.addAll(proList);
        proIds.addAll(qyProList);
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                proIds);
        map.put("orgIds",orgIds);
        PageHelper.startPage(pageIndex, pageSize);
        List<Map> reList = projectCluesDao.getGlAllocationPropertyConsultantZygw(map);
        return ResultBody.success(new PageInfo<>(reList));
    }

    @Override
    public ResultBody getCallProjectRule(Map map) {
        if (map.get("orgId") == null){
            return ResultBody.error(400,"用户部门ID为空！！");
        }
        Map projectByOrgId = projectCluesDao.getProjectByOrgId(map.get("orgId")+"");
        if (projectByOrgId == null){
            return ResultBody.error(400,"用户部门ID不存在对应的项目！！");
        }
        String projectId = projectByOrgId.get("projectId")+"";
        //查询项目规则
        String ComGUID = projectCluesDao.getComGUIDByProject(projectId);// 区域ID
        //查询规则获取保护期
        ProjectRuleDetail projectRuleDetail = null;
        //查询项目保护期
        projectRuleDetail = projectCluesDao.selectProjectRuleZs(projectId,"2");
        //项目没有配置规则 查询区域的
        if (projectRuleDetail==null){
            projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
        }
        //没有区域的查询集团的
        if (projectRuleDetail==null){
            projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");

        }
        if (projectRuleDetail!=null) {
            Map reMap = new HashMap();
            reMap.put("callNotDialCustomerProtectDays",projectRuleDetail.getCallNotDialCustomerProtectDays());
            reMap.put("callNotConnCustomerProtectDays",projectRuleDetail.getCallNotConnCustomerProtectDays());
            reMap.put("callHasConnCustomerProtectDays",projectRuleDetail.getCallHasConnCustomerProtectDays());
            return ResultBody.success(reMap);
        }else {
            return ResultBody.error(400,"获取项目保护规则异常");
        }
    }
    
    /**
     * 计算项目下各任务的指标汇总（用于计算CompleteProportion）
     * @param projectId 项目ID
     * @param taskList 任务列表
     * @return Map包含各指标的汇总值
     */
    private Map<String, Integer> calculateProjectTaskSummary(String projectId, List<TaskVo> taskList) {
        Map<String, Integer> summary = new HashMap<>();
        
        // 初始化汇总值
        summary.put("reportNum", 0);
        summary.put("visitNum", 0);
        summary.put("arriveNum", 0);
        summary.put("threeOneNum", 0);
        summary.put("dealNum", 0);
        summary.put("firstVisitNum", 0);
        summary.put("repeatVisitNum", 0);
        summary.put("tagNum", 0);
        
        if (CollectionUtils.isEmpty(taskList)) {
            return summary;
        }
        
        // 汇总项目下所有任务的指标量
        for (TaskVo task : taskList) {
            if (task.getProjectId() != null && task.getProjectId().equals(projectId)) {
                // 报备指标汇总
                if (task.getReportNum() != null) {
                    summary.put("reportNum", summary.get("reportNum") + task.getReportNum());
                }
                
                // 拜访指标汇总
                if (task.getVisitNum() != null) {
                    summary.put("visitNum", summary.get("visitNum") + task.getVisitNum());
                }
                
                // 到访指标汇总
                if (task.getArriveNum() != null) {
                    summary.put("arriveNum", summary.get("arriveNum") + task.getArriveNum());
                }
                
                // 三个一指标汇总
                if (task.getThreeOneNum() != null) {
                    summary.put("threeOneNum", summary.get("threeOneNum") + task.getThreeOneNum());
                }
                
                // 成交指标汇总
                if (task.getDealNum() != null) {
                    summary.put("dealNum", summary.get("dealNum") + task.getDealNum());
                }
                
                // 首访指标汇总
                if (task.getFirstVisitNum() != null) {
                    summary.put("firstVisitNum", summary.get("firstVisitNum") + task.getFirstVisitNum());
                }
                
                // 复访指标汇总
                if (task.getRepeatVisitNum() != null) {
                    summary.put("repeatVisitNum", summary.get("repeatVisitNum") + task.getRepeatVisitNum());
                }
                
                // 标记指标汇总
                if (task.getTagNum() != null) {
                    summary.put("tagNum", summary.get("tagNum") + task.getTagNum());
                }
            }
        }
        
        return summary;
    }
}
