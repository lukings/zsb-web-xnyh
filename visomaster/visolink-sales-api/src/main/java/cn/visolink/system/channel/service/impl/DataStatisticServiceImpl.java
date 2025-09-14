package cn.visolink.system.channel.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.system.channel.dao.DataStatisticDao;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.model.*;
import cn.visolink.system.channel.model.vo.*;
import cn.visolink.system.channel.model.vo.PunchInStatisticsVO;
import cn.visolink.system.channel.service.DataStatisticService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.homepage.service.WorkbenchService;
import cn.visolink.system.job.authorization.mapper.JobMapper;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @ClassName DataStatisticServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/19 13:53
 **/
@Service
public class DataStatisticServiceImpl implements DataStatisticService {

    @Autowired
    private DataStatisticDao dataStatisticDao;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectCluesDao projectCluesDao;
    @Autowired
    private WorkbenchService workbenchService;

    @Autowired
    private RedisUtil redisUtil;

//    @Autowired
//    private RestHighLevelClient client;//连接elasticsearch的客户端，在配置文件中已经注入
//    private static final String indexName = "test-wy-customer_add_log"; //索引名称
//    private static final String documentId = "customer_add_log_01"; //文档ID

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DecimalFormat df = new DecimalFormat("#0.00");

    /**
     * 安全地获取Map中的整数值
     * @param map 数据Map
     * @param key 键名
     * @return 整数值，如果为空或转换失败则返回0
     */
    private Integer getIntegerValue(Map map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 安全地获取Map中的字符串值
     * @param map 数据Map
     * @param key 键名
     * @return 字符串值，如果为空则返回空字符串
     */
    private String getStringValue(Map map, String key) {
        Object value = map.get(key);
        return value == null ? "" : value.toString();
    }

    @Override
    public ResultBody punchInStatistics(Map paramMap) {
        try {
            // 处理时间参数
            if (paramMap.get("startTime") != null && !"".equals(paramMap.get("startTime") + "")
                    && paramMap.get("endTime") != null && !"".equals(paramMap.get("endTime") + "")) {
                // 时间参数已存在，直接使用
            } else {
                String type = paramMap.get("type") + "";
                Date today = new Date();
                Date beginTime = null;
                Date endTime = null;
                // 不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
                if ("".equals(type) || "null".equals(type) || "1".equals(type)) {
                    // 未传时间参数默认取当天的数据
                    beginTime = DateUtil.beginOfDay(today);
                } else if ("2".equals(type)) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(today);
                    calendar.add(calendar.DATE, -7);
                    beginTime = DateUtil.beginOfDay(calendar.getTime());
                } else if ("3".equals(type)) {
                    beginTime = DateUtil.beginOfMonth(today);
                } else if ("4".equals(type)) {
                    beginTime = DateUtil.beginOfQuarter(today);
                } else {
                    beginTime = DateUtil.beginOfDay(today);
                }
                endTime = DateUtil.endOfDay(today);
                paramMap.put("startTime", sf.format(beginTime));
                paramMap.put("endTime", sf.format(endTime));
            }

            // 处理分页参数
            int pageIndex = 1;
            int pageSize = 10;
            if (paramMap.get("pageNum") != null) {
                pageIndex = Integer.parseInt(paramMap.get("pageNum") + "");
            }
            if (paramMap.get("pageSize") != null) {
                pageSize = Integer.parseInt(paramMap.get("pageSize") + "");
            }
            int i = (pageIndex - 1) * pageSize;
            paramMap.put("pageIndex", i);
            paramMap.put("pageSize", pageSize);

            // 获取打卡统计汇总数据
            List<Map> summaryList = dataStatisticDao.getPunchInStatisticsSummary(paramMap);

            // 构建返回结果
            PunchInStatisticsVO resultVO = new PunchInStatisticsVO();
            resultVO.setPageNum(pageIndex);
            resultVO.setPageSize(pageSize);
            
            // 转换汇总数据
            List<PunchInStatisticsVO.PunchInSummaryVO> summaryVOList = new ArrayList<>();
            
            // 添加合计行（第一行）
            PunchInStatisticsVO.PunchInSummaryVO totalRow = new PunchInStatisticsVO.PunchInSummaryVO();
            totalRow.setOrgId("total");
            totalRow.setOrgName("合计");
            totalRow.setProjectId("total");
            totalRow.setProjectName("合计");
            totalRow.setPunchInUserCount(0);
            totalRow.setPunchInCustomerCount(0);
            totalRow.setPunchInCount(0);
            summaryVOList.add(totalRow);
            
            if (!CollectionUtils.isEmpty(summaryList)) {
                for (Map summary : summaryList) {
                    PunchInStatisticsVO.PunchInSummaryVO summaryVO = new PunchInStatisticsVO.PunchInSummaryVO();
                    summaryVO.setOrgId(getStringValue(summary, "OrgId"));
                    summaryVO.setOrgName(getStringValue(summary, "OrgName"));
                    summaryVO.setProjectId(getStringValue(summary, "ProjectId"));
                    summaryVO.setProjectName(getStringValue(summary, "ProjectName"));
                    summaryVO.setPunchInUserCount(getIntegerValue(summary, "PunchInUserCount"));
                    summaryVO.setPunchInCustomerCount(getIntegerValue(summary, "PunchInCustomerCount"));
                    summaryVO.setPunchInCount(getIntegerValue(summary, "PunchInCount"));
                    
                    // 累加合计值
                    totalRow.setPunchInUserCount(totalRow.getPunchInUserCount() + summaryVO.getPunchInUserCount());
                    totalRow.setPunchInCustomerCount(totalRow.getPunchInCustomerCount() + summaryVO.getPunchInCustomerCount());
                    totalRow.setPunchInCount(totalRow.getPunchInCount() + summaryVO.getPunchInCount());
                    
                    summaryVOList.add(summaryVO);
                }
            }
            resultVO.setSummaryList(summaryVOList);
            return ResultBody.success(resultVO);
        } catch (Exception e) {
            return ResultBody.error(-100001,"打卡统计查询失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody punchInProjectDetail(Map paramMap) {
        try {
            // 处理时间参数
            if (paramMap.get("startTime") != null && !"".equals(paramMap.get("startTime") + "")
                    && paramMap.get("endTime") != null && !"".equals(paramMap.get("endTime") + "")) {
                // 时间参数已存在，直接使用
            } else {
                String type = paramMap.get("type") + "";
                Date today = new Date();
                Date beginTime = null;
                Date endTime = null;
                // 不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
                if ("".equals(type) || "null".equals(type) || "1".equals(type)) {
                    // 未传时间参数默认取当天的数据
                    beginTime = DateUtil.beginOfDay(today);
                } else if ("2".equals(type)) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(today);
                    calendar.add(calendar.DATE, -7);
                    beginTime = DateUtil.beginOfDay(calendar.getTime());
                } else if ("3".equals(type)) {
                    beginTime = DateUtil.beginOfMonth(today);
                } else if ("4".equals(type)) {
                    beginTime = DateUtil.beginOfQuarter(today);
                } else {
                    beginTime = DateUtil.beginOfDay(today);
                }
                endTime = DateUtil.endOfDay(today);
                paramMap.put("startTime", sf.format(beginTime));
                paramMap.put("endTime", sf.format(endTime));
            }
            // 处理分页参数
            int pageIndex = 1;
            int pageSize = 10;
            if (paramMap.get("pageNum") != null) {
                pageIndex = Integer.parseInt(paramMap.get("pageNum") + "");
            }
            if (paramMap.get("pageSize") != null) {
                pageSize = Integer.parseInt(paramMap.get("pageSize") + "");
            }
            int i = (pageIndex - 1) * pageSize;
            paramMap.put("pageIndex", String.valueOf(i));
            paramMap.put("pageSize", pageSize);

            // 获取项目明细数据（按团队）
            List<Map> projectDetailList = dataStatisticDao.getPunchInProjectDetail(paramMap);

            // 构建返回结果，添加合计行
            List<Map> resultList = new ArrayList<>();
            
            // 添加合计行（第一行）
            Map<String, Object> totalRow = new HashMap<>();
            totalRow.put("OrgId", "total");
            totalRow.put("OrgName", "合计");
            totalRow.put("ProjectId", "total");
            totalRow.put("ProjectName", "合计");
            totalRow.put("SalesAttributionTeamId", "total");
            totalRow.put("SalesAttributionTeamName", "合计");
            totalRow.put("PunchInUserCount", 0);
            totalRow.put("PunchInCustomerCount", 0);
            totalRow.put("PunchInCount", 0);
            resultList.add(totalRow);
            
            if (!CollectionUtils.isEmpty(projectDetailList)) {
                for (Map detail : projectDetailList) {
                    // 累加合计值
                    Integer userCount = getIntegerValue(detail, "PunchInUserCount");
                    Integer customerCount = getIntegerValue(detail, "PunchInCustomerCount");
                    Integer punchCount = getIntegerValue(detail, "PunchInCount");
                    
                    totalRow.put("PunchInUserCount", getIntegerValue(totalRow, "PunchInUserCount") + userCount);
                    totalRow.put("PunchInCustomerCount", getIntegerValue(totalRow, "PunchInCustomerCount") + customerCount);
                    totalRow.put("PunchInCount", getIntegerValue(totalRow, "PunchInCount") + punchCount);
                    
                    resultList.add(detail);
                }
            }

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("projectDetailList", resultList);
            resultMap.put("total", resultList.size());
            resultMap.put("pageNum", pageIndex);
            resultMap.put("pageSize", pageSize);
            return ResultBody.success(resultMap);
        } catch (Exception e) {
            return ResultBody.error(-100002, "打卡统计项目明细查询失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody punchInCustomerDetail(Map paramMap) {
        try {
            // 处理时间参数
            if (paramMap.get("startTime") != null && !"".equals(paramMap.get("startTime") + "")
                    && paramMap.get("endTime") != null && !"".equals(paramMap.get("endTime") + "")) {
                // 时间参数已存在，直接使用
            } else {
                String type = paramMap.get("type") + "";
                Date today = new Date();
                Date beginTime = null;
                Date endTime = null;
                // 不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
                if ("".equals(type) || "null".equals(type) || "1".equals(type)) {
                    // 未传时间参数默认取当天的数据
                    beginTime = DateUtil.beginOfDay(today);
                } else if ("2".equals(type)) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(today);
                    calendar.add(calendar.DATE, -7);
                    beginTime = DateUtil.beginOfDay(calendar.getTime());
                } else if ("3".equals(type)) {
                    beginTime = DateUtil.beginOfMonth(today);
                } else if ("4".equals(type)) {
                    beginTime = DateUtil.beginOfQuarter(today);
                } else {
                    beginTime = DateUtil.beginOfDay(today);
                }
                endTime = DateUtil.endOfDay(today);
                paramMap.put("startTime", sf.format(beginTime));
                paramMap.put("endTime", sf.format(endTime));
            }

            // 处理分页参数
            int pageIndex = 1;
            int pageSize = 10;
            if (paramMap.get("pageNum") != null) {
                pageIndex = Integer.parseInt(paramMap.get("pageNum") + "");
            }
            if (paramMap.get("pageSize") != null) {
                pageSize = Integer.parseInt(paramMap.get("pageSize") + "");
            }
            int i = (pageIndex - 1) * pageSize;
            paramMap.put("pageIndex", String.valueOf(i));
            paramMap.put("pageSize", pageSize);

            // 获取客户明细数据
            List<Map> customerDetailList = dataStatisticDao.getPunchInCustomerDetail(paramMap);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("customerDetailList", customerDetailList);
            resultMap.put("pageNum", pageIndex);
            resultMap.put("pageSize", pageSize);
            resultMap.put("total", customerDetailList != null ? customerDetailList.size() : 0);

            return ResultBody.success(resultMap);
        } catch (Exception e) {
            return ResultBody.error(-100003, "打卡统计客户明细查询失败：" + e.getMessage());
        }
    }

    @Override
    public ResultBody projectDataStatistics(Map paramMap) {
        //判断是项目统计还是业务员统计 查询类型（1：项目 2：业务员）
        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        int i = (pageIndex - 1) * pageSize;
        paramMap.put("pageIndex",String.valueOf(i));
        paramMap.put("pageSize",pageSize);
        String total = "";
        Map reMap = new HashMap<>();
        String projectIds = paramMap.get("projectIds")+"";
        String projectId = paramMap.get("projectId")+"";

        List<String> proIds = Collections.singletonList(projectId);
        List<String> userIds = dataStatisticDao.getUserAscInsPro(proIds);
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        //限制经理、总监、营销经理
        List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                proIds);
        paramMap.put("orgIds",orgIds);
//        PageHelper.startPage(pageIndex, pageSize);
        if ("1".equals(searchType)){//项目数据统计主表格查询
            if ("".equals(projectIds) || "null".equals(projectIds)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            proIds = Arrays.asList(projectIds.split(","));
            paramMap.put("proIds",proIds);
            paramMap.put("pageNum","");
            proDataStatistics = dataStatisticDao.getProDataStatistics(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics1("pro",proDataStatistics));//计算合计值
            reMap.put("list",proDataStatistics);
            reMap.put("total",proIds.size());
            return ResultBody.success(reMap);
        }else if ("2".equals(searchType)){//数据查看详情查询
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            String teams = paramMap.get("teams")+"";
            if (!"".equals(teams) && !"null".equals(teams)){
                List<String> teamIds = Arrays.asList(teams.split(","));
                paramMap.put("teamIds",teamIds);
            }
            if (CollectionUtils.isEmpty(userIds)){
                return ResultBody.success(new PageInfo<>(proDataStatistics));
            }
            paramMap.put("userIds",userIds);
            PageHelper.startPage(pageIndex, pageSize);
            proDataStatistics = dataStatisticDao.getUserDataStatistics(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatistics));
        }else if ("3".equals(searchType)){//蓝色字体点击跟进客户台账查询
            if ("".equals(projectIds) || "null".equals(projectIds)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            proIds = Arrays.asList(projectIds.split(","));
            paramMap.put("proIds",proIds);
            PageHelper.startPage(pageIndex, pageSize);
            List<FollowUpRecordVO> proDataStatisticsGather = dataStatisticDao.getProDataStatisticsGather(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGather));
        }else if ("4".equals(searchType)) {//数据查看详情查询弹出来表格里面蓝色字体点击查询
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            String teams = paramMap.get("teams")+"";
            if (!"".equals(teams) && !"null".equals(teams)){
                List<String> teamIds = Arrays.asList(teams.split(","));
                paramMap.put("teamIds",teamIds);
            }

            paramMap.put("userIds",userIds);
            PageHelper.startPage(pageIndex, pageSize);
            List<FollowUpRecordVO> proDataStatisticsGather = dataStatisticDao.getUserDataStatisticsGather(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGather));
        }else if ("5".equals(searchType)) {//蓝色字体点击报备客户台账查询
            if ("".equals(projectIds) || "null".equals(projectIds)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            proIds = Arrays.asList(projectIds.split(","));
            paramMap.put("proIds",proIds);
            PageHelper.startPage(pageIndex, pageSize);
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getProDataStatisticsGatherCc(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("6".equals(searchType)) {//数据查看详情查询弹出来表格里面蓝色字体点击报备客户台账查询
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            String teams = paramMap.get("teams")+"";
            if (!"".equals(teams) && !"null".equals(teams)){
                List<String> teamIds = Arrays.asList(teams.split(","));
                paramMap.put("teamIds",teamIds);
            }

            paramMap.put("userIds",userIds);
            PageHelper.startPage(pageIndex, pageSize);
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getUserDataStatisticsGatherCc(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("7".equals(searchType)) {//蓝色字体点击报备客户台账查询
            if ("".equals(projectIds) || "null".equals(projectIds)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            proIds = Arrays.asList(projectIds.split(","));
            paramMap.put("proIds",proIds);
            PageHelper.startPage(pageIndex, pageSize);
            paramMap.put("gsType","pro");
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getDataStatisticsGatherAt(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("8".equals(searchType)) {//数据查看详情查询弹出来表格里面蓝色字体点击报备客户台账查询
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            String teams = paramMap.get("teams")+"";
            if (!"".equals(teams) && !"null".equals(teams)){
                List<String> teamIds = Arrays.asList(teams.split(","));
                paramMap.put("teamIds",teamIds);
            }

            paramMap.put("userIds",userIds);
            PageHelper.startPage(pageIndex, pageSize);
            paramMap.put("gsType","proUser");
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getDataStatisticsGatherAt(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }
        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    @Override
    public ResultBody userDataStatistics(Map paramMap) {
        //判断是专员统计还是个人统计 查询类型（1：个人 2：专员）
        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        List<FollowUpRecordVO> proDataStatisticsGather = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        String projectIds = paramMap.get("projectIds")+"";
        String projectId = paramMap.get("projectId")+"";
        String userId = paramMap.get("userId")+"";
        List<String> proIds = Arrays.asList(projectIds.split(","));
        List<String> userIds = dataStatisticDao.getUserAscInsProHasLz(proIds);
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        PageHelper.startPage(pageIndex, pageSize);
        if ("1".equals(searchType)){
            if ("".equals(projectIds) || "null".equals(projectIds)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            proIds = Arrays.asList(projectIds.split(","));
            paramMap.put("proIds",proIds);
            proDataStatistics = dataStatisticDao.getGrUserDataStatistics(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics1("gr",proDataStatistics));//计算合计值
            return ResultBody.success(new PageInfo<>(proDataStatistics));
        }else if ("2".equals(searchType)){
            if ("".equals(projectIds) || "null".equals(projectIds)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            paramMap.put("proIds",proIds);
            if (CollectionUtils.isEmpty(userIds)){
                return ResultBody.success(new PageInfo<>(proDataStatistics));
            }
            paramMap.put("userIds",userIds);
            proDataStatistics = dataStatisticDao.getZyUserDataStatistics(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics1("zy",proDataStatistics));//计算合计值
            return ResultBody.success(new PageInfo<>(proDataStatistics));
        }else if ("3".equals(searchType)){
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            proDataStatisticsGather = dataStatisticDao.getGrUserDataStatisticsGather(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGather));
        }else if ("4".equals(searchType)){
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            if ("".equals(userId) || "null".equals(userId)){
                return ResultBody.error(-1200002,"专员ID未传！！");
            }
            //限制经理、总监、营销经理
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                    proIds);
            paramMap.put("orgIds",orgIds);
            proDataStatisticsGather = dataStatisticDao.getZyUserDataStatisticsGather(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGather));
        }else if ("5".equals(searchType)){
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getGrUserDataStatisticsGatherCc(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("6".equals(searchType)){
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            if ("".equals(userId) || "null".equals(userId)){
                return ResultBody.error(-1200002,"专员ID未传！！");
            }
            //限制经理、总监、营销经理
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                    proIds);
            paramMap.put("orgIds",orgIds);
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getZyUserDataStatisticsGatherCc(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("7".equals(searchType)){
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            paramMap.put("gsType","grUser");
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getDataStatisticsGatherAt(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("8".equals(searchType)){
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            if ("".equals(userId) || "null".equals(userId)){
                return ResultBody.error(-1200002,"专员ID未传！！");
            }
            paramMap.put("gsType","zyUser");
            //限制经理、总监、营销经理
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                    proIds);
            paramMap.put("orgIds",orgIds);
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getDataStatisticsGatherAt(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }
        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    @Override
    public ResultBody getTeamListByProId(Map paramMap){
        return ResultBody.success(dataStatisticDao.getTeamListByProId(paramMap));
    }

    @Override
    public ResultBody getAllRegion() {
        Map mapUser=new HashMap();
        mapUser.put("UserName", SecurityUtils.getUsername());
        List<String> fullpath = projectMapper.findFullPath(mapUser);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无区域权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append(" and org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        mapUser.put("where", sb.toString());
        List<Map> regions = dataStatisticDao.getAllRegionNew(mapUser);
        return ResultBody.success(regions);
    }

    @Override
    public ResultBody regionDataStatistics(Map paramMap) {
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        List<FollowUpRecordVO> proDataStatisticsGather = new ArrayList<>();
        //判断是项目统计还是业务员统计 查询类型（1：项目 2：业务员）
        String searchType = paramMap.get("searchType")+"";
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        if ("1".equals(searchType)){
            String projectIds = paramMap.get("regionIds")+"";
            if ("".equals(projectIds) || "null".equals(projectIds)){
                return ResultBody.error(-1200002,"区域ID未传！！");
            }
            if (paramMap.get("regionIds")!=null && !"".equals(paramMap.get("regionIds")+"")){
                String regionIds = paramMap.get("regionIds")+"";
                List<String> regionList = Arrays.asList(regionIds.split(","));
                regionList = dataStatisticDao.getProList(regionList);
                paramMap.put("proIds",regionList);
            }
            PageHelper.startPage(pageIndex, pageSize);
            proDataStatistics = dataStatisticDao.getQyDataStatistics(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics1("qy",proDataStatistics));//计算合计值
            return ResultBody.success(new PageInfo<>(proDataStatistics));
        }else if ("2".equals(searchType)){
            String projectId = paramMap.get("regionId")+"";
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"区域ID未传！！");
            }
            List<String> userIds = dataStatisticDao.getUserAscInsPro(dataStatisticDao.getProList(Collections.singletonList(projectId)));
            if (CollectionUtils.isEmpty(userIds)){
                return ResultBody.success(new PageInfo<>(proDataStatistics));
            }
            paramMap.put("userIds",userIds);
            PageHelper.startPage(pageIndex, pageSize);
            paramMap.put("projectId",projectId);
            String teams = paramMap.get("teams")+"";
            if (!"".equals(teams) && !"null".equals(teams)){
                List<String> teamIds = Arrays.asList(teams.split(","));
                paramMap.put("teamIds",teamIds);
            }
            proDataStatistics = dataStatisticDao.getUserQyDataStatistics(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatistics));
        }else if ("3".equals(searchType)){
            String regionId = paramMap.get("regionId")+"";
            if ("".equals(regionId) || "null".equals(regionId)){
                return ResultBody.error(-1200002,"区域ID未传！！");
            }
            if (paramMap.get("regionId")!=null && !"".equals(paramMap.get("regionId")+"")){
                String regionIds = paramMap.get("regionId")+"";
                List<String> regionList = Arrays.asList(regionIds.split(","));
                regionList = dataStatisticDao.getProList(regionList);
                paramMap.put("proIds",regionList);
                //限制经理、总监、营销经理
                List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                        regionList);
                paramMap.put("orgIds",orgIds);
            }
            PageHelper.startPage(pageIndex, pageSize);
            proDataStatisticsGather = dataStatisticDao.getQyDataStatisticsGather(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGather));
        }else if ("4".equals(searchType)){
            String projectId = paramMap.get("regionId")+"";
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"区域ID未传！！");
            }
            String userId = paramMap.get("userId")+"";
            if ("".equals(userId) || "null".equals(userId)){
                return ResultBody.error(-1200002,"专员ID未传！！");
            }
            PageHelper.startPage(pageIndex, pageSize);
            paramMap.put("projectId",projectId);
            //限制经理、总监、营销经理
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                    projectMapper.getProInsRegion(projectId));
            paramMap.put("orgIds",orgIds);
            proDataStatisticsGather = dataStatisticDao.getUserQyDataStatisticsGather(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGather));
        }else if ("5".equals(searchType)){
            String regionId = paramMap.get("regionId")+"";
            if ("".equals(regionId) || "null".equals(regionId)){
                return ResultBody.error(-1200002,"区域ID未传！！");
            }
            if (paramMap.get("regionId")!=null && !"".equals(paramMap.get("regionId")+"")){
                String regionIds = paramMap.get("regionId")+"";
                List<String> regionList = Arrays.asList(regionIds.split(","));
                regionList = dataStatisticDao.getProList(regionList);
                paramMap.put("proIds",regionList);
                //限制经理、总监、营销经理
                List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                        regionList);
                paramMap.put("orgIds",orgIds);
            }
            PageHelper.startPage(pageIndex, pageSize);
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getQyDataStatisticsGatherCc(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("6".equals(searchType)){
            String projectId = paramMap.get("regionId")+"";
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"区域ID未传！！");
            }
            String userId = paramMap.get("userId")+"";
            if ("".equals(userId) || "null".equals(userId)){
                return ResultBody.error(-1200002,"专员ID未传！！");
            }
            PageHelper.startPage(pageIndex, pageSize);
            paramMap.put("projectId",projectId);
            //限制经理、总监、营销经理
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                    projectMapper.getProInsRegion(projectId));
            paramMap.put("orgIds",orgIds);
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getUserQyDataStatisticsGatherCc(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("7".equals(searchType)){
            String regionId = paramMap.get("regionId")+"";
            if ("".equals(regionId) || "null".equals(regionId)){
                return ResultBody.error(-1200002,"区域ID未传！！");
            }
            if (paramMap.get("regionId")!=null && !"".equals(paramMap.get("regionId")+"")){
                String regionIds = paramMap.get("regionId")+"";
                List<String> regionList = Arrays.asList(regionIds.split(","));
                regionList = dataStatisticDao.getProList(regionList);
                paramMap.put("proIds",regionList);
                //限制经理、总监、营销经理
                List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                        regionList);
                paramMap.put("orgIds",orgIds);
            }
            PageHelper.startPage(pageIndex, pageSize);
            paramMap.put("gsType","qy");
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getDataStatisticsGatherAt(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("8".equals(searchType)){
            String projectId = paramMap.get("regionId")+"";
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"区域ID未传！！");
            }
            String userId = paramMap.get("userId")+"";
            if ("".equals(userId) || "null".equals(userId)){
                return ResultBody.error(-1200002,"专员ID未传！！");
            }
            PageHelper.startPage(pageIndex, pageSize);
            paramMap.put("projectId",projectId);
            //限制经理、总监、营销经理
            List<String> orgIds = workbenchService.linkOrgIds(SecurityUtils.getUserId(),
                    projectMapper.getProInsRegion(projectId));
            paramMap.put("orgIds",orgIds);
            paramMap.put("gsType","qyUser");
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getDataStatisticsGatherAt(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }

        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    @Override
    public void projectDataExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {

        Map paramMap = JSONObject.parseObject(excelForm, Map.class);

        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }
        List<Map> filedsMX = (List<Map>) paramMap.get("filedsMX");
        filedsMX = filedsMX.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodesMX = new ArrayList<>();
        List<String> filedNamesMX = new ArrayList<>();
        for (Map filed : filedsMX) {
            filedCodesMX.add(filed.get("filedCode")+"");
            filedNamesMX.add(filed.get("filedName")+"");
        }

        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        List<String> proIdList = new ArrayList<>();
        if ("1".equals(searchType)){
            excelName = "项目数据统计";
            String projectIds = paramMap.get("projectIds")+"";
            List<String> proIds = Arrays.asList(projectIds.split(","));
            proIdList = proIds;
            paramMap.put("proIds",proIds);
            proDataStatistics = dataStatisticDao.getProDataStatistics(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics1("pro",proDataStatistics));//计算合计值
        }else{
            excelName = "项目成员数据统计";
            proIdList.add(paramMap.get("projectId")+"");
            String teams = paramMap.get("teams")+"";
            if (!"".equals(teams) && !"null".equals(teams)){
                List<String> teamIds = Arrays.asList(teams.split(","));
                paramMap.put("teamIds",teamIds);
            }
            List<String> userIds = dataStatisticDao.getUserAscInsPro(proIdList);
            paramMap.put("userIds",userIds);
            proDataStatistics = dataStatisticDao.getUserDataStatistics(paramMap);
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(paramMap.get("userId")+"");
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            int rowNum = 1;
            if ("1".equals(searchType)){
//                headers = proDataStatistics.get(0).cardTitle1;
                headers = filedNames.toArray(new String[0]);

                for (ProDataStatistics model : proDataStatistics) {
                    model.setRownum(rowNum+"");
                    Object[] oArray = model.toData1(filedCodes);
                    dataset.add(oArray);
                    rowNum++;
                }
            }else{
//                headers = proDataStatistics.get(0).cardTitle2;
                headers = filedNamesMX.toArray(new String[0]);
                for (ProDataStatistics model : proDataStatistics) {
                    model.setRownum(rowNum+"");
                    Object[] oArray = model.toData2(filedCodesMX);
                    dataset.add(oArray);
                    rowNum++;
                }
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                        && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,paramMap.get("startTime")+"-"+paramMap.get("endTime"));
                }else{
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String projectDataExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        String userId = SecurityUtils.getUserId();
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        String searchType = paramMap.get("searchType")+"";
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        List<String> proIdList = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = "";
        if ("1".equals(searchType)){
            excelName = "项目数据统计";
            String projectIds = paramMap.get("projectIds")+"";
            List<String> proIds = Arrays.asList(projectIds.split(","));
            proIdList = proIds;
            paramMap.put("proIds",proIds);
        }else{
            excelName = "项目成员数据统计";
            proIdList.add(paramMap.get("projectId")+"");
            String teams = paramMap.get("teams")+"";
            if (!"".equals(teams) && !"null".equals(teams)){
                List<String> teamIds = Arrays.asList(teams.split(","));
                paramMap.put("teamIds",teamIds);
            }
            List<String> userIds = dataStatisticDao.getUserAscInsPro(proIdList);
            paramMap.put("userIds",userIds);
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("PDS1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(paramMap));
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
    public void userDataExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);

        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }
//        List<Map> filedsMX = (List<Map>) paramMap.get("filedsMX");
//        filedsMX = filedsMX.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

//        List<String> filedCodesMX = new ArrayList<>();
//        List<String> filedNamesMX = new ArrayList<>();
//        for (Map filed : filedsMX) {
//            filedCodesMX.add(filed.get("filedCode")+"");
//            filedNamesMX.add(filed.get("filedName")+"");
//        }

        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        List<String> proIdList = new ArrayList<>();
        if ("1".equals(searchType)){
            excelName = "个人数据统计";
            String projectIds = paramMap.get("projectIds")+"";
            List<String> proIds = Arrays.asList(projectIds.split(","));
            proIdList = proIds;
            paramMap.put("proIds",proIds);
            proDataStatistics = dataStatisticDao.getGrUserDataStatistics(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics1("gr",proDataStatistics));//计算合计值
        }else{
            excelName = "专员数据统计";
            String projectIds = paramMap.get("projectIds")+"";
            List<String> proIds = Arrays.asList(projectIds.split(","));
            proIdList = proIds;
            paramMap.put("proIds",proIds);
            List<String> userIds = dataStatisticDao.getUserAscInsProHasLz(proIds);
            paramMap.put("userIds",userIds);
            proDataStatistics = dataStatisticDao.getZyUserDataStatistics(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics1("zy",proDataStatistics));//计算合计值
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(paramMap.get("userId")+"");
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            int rowNum = 1;
            if ("1".equals(searchType)){
//                headers = proDataStatistics.get(0).cardTitle1;
                headers = filedNames.toArray(new String[0]);

                for (ProDataStatistics model : proDataStatistics) {
                    model.setRownum(rowNum+"");
                    Object[] oArray = model.toData1(filedCodes);
                    dataset.add(oArray);
                    rowNum++;
                }
            }else{
//                headers = proDataStatistics.get(0).cardTitle2;
                headers = filedNames.toArray(new String[0]);
                for (ProDataStatistics model : proDataStatistics) {
                    model.setRownum(rowNum+"");
                    Object[] oArray = model.toData2(filedCodes);
                    dataset.add(oArray);
                    rowNum++;
                }
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                        && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,paramMap.get("startTime")+"-"+paramMap.get("endTime"));
                }else{
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String userDataExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        String userId = SecurityUtils.getUserId();
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        String searchType = paramMap.get("searchType")+"";
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        List<String> proIdList = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = null;
        if ("1".equals(searchType)){
            excelName = "个人数据统计";
            String projectIds = paramMap.get("projectIds")+"";
            List<String> proIds = Arrays.asList(projectIds.split(","));
            proIdList = proIds;
            paramMap.put("proIds",proIds);
        }else{
            excelName = "专员数据统计";
            String projectIds = paramMap.get("projectIds")+"";
            List<String> proIds = Arrays.asList(projectIds.split(","));
            proIdList = proIds;
            paramMap.put("proIds",proIds);
            List<String> userIds = dataStatisticDao.getUserAscInsPro(proIds);
            paramMap.put("userIds",userIds);
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("UDS1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(paramMap.get("userId")+"");
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(paramMap));
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
    public void regionDataExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);


        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }
        List<Map> filedsMX = (List<Map>) paramMap.get("filedsMX");
        filedsMX = filedsMX.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodesMX = new ArrayList<>();
        List<String> filedNamesMX = new ArrayList<>();
        for (Map filed : filedsMX) {
            filedCodesMX.add(filed.get("filedCode")+"");
            filedNamesMX.add(filed.get("filedName")+"");
        }

        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = "区域数据统计";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        List<String> proIdList = new ArrayList<>();
        if ("1".equals(searchType)){
            excelName = "区域数据统计";
            String projectIds = paramMap.get("regionIds")+"";
            List<String> proIds = Arrays.asList(projectIds.split(","));
            proIdList.addAll(proIds);
            proIds  = dataStatisticDao.getProList(proIds);
            paramMap.put("proIds",proIds);
            proDataStatistics = dataStatisticDao.getQyDataStatistics(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics1("qy",proDataStatistics));//计算合计值
        }else{
            excelName = "区域成员数据统计";
            String projectId = paramMap.get("regionId")+"";
            paramMap.put("projectId",projectId);
            proIdList.add(projectId);
            String teams = paramMap.get("teams")+"";
            if (!"".equals(teams) && !"null".equals(teams)){
                List<String> teamIds = Arrays.asList(teams.split(","));
                paramMap.put("teamIds",teamIds);
            }
            List<String> userIds = dataStatisticDao.getUserAscInsPro(dataStatisticDao.getProList(Collections.singletonList(projectId)));
            paramMap.put("userIds",userIds);
            proDataStatistics = dataStatisticDao.getUserQyDataStatistics(paramMap);
        }

        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        String areaName = excelImportMapper.getAreaNames(proIdList);
        excelExportLog.setCreator(paramMap.get("userId")+"");
        excelExportLog.setAreaName(areaName);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);

        if (proDataStatistics!=null && proDataStatistics.size()>0){
            int rowNum = 1;
            if ("1".equals(searchType)){
//                headers = proDataStatistics.get(0).cardTitle3;
                headers = filedNames.toArray(new String[0]);

                for (ProDataStatistics model : proDataStatistics) {
                    model.setRownum(rowNum+"");
                    Object[] oArray = model.toData3(filedCodes);
                    dataset.add(oArray);
                    rowNum++;
                }
            }else{
//                headers = proDataStatistics.get(0).cardTitle4;
                headers = filedNamesMX.toArray(new String[0]);

                for (ProDataStatistics model : proDataStatistics) {
                    model.setRownum(rowNum+"");
                    Object[] oArray = model.toData4(filedCodesMX);
                    dataset.add(oArray);
                    rowNum++;
                }
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                        && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,paramMap.get("startTime")+"-"+paramMap.get("endTime"));
                }else{
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String regionDataExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        String userId = SecurityUtils.getUserId();
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        String searchType = paramMap.get("searchType")+"";
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        List<String> proIdList = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = "区域数据统计";
        if ("1".equals(searchType)){
            excelName = "区域数据统计";
            String projectIds = paramMap.get("regionIds")+"";
            List<String> proIds = Arrays.asList(projectIds.split(","));
            proIdList.addAll(proIds);
            proIds  = dataStatisticDao.getProList(proIds);
            paramMap.put("proIds",proIds);
        }else{
            excelName = "区域成员数据统计";
            String projectId = paramMap.get("regionId")+"";
            paramMap.put("projectId",projectId);
            proIdList.add(projectId);
            String teams = paramMap.get("teams")+"";
            if (!"".equals(teams) && !"null".equals(teams)){
                List<String> teamIds = Arrays.asList(teams.split(","));
                paramMap.put("teamIds",teamIds);
            }
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("RDS1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        String areaName = excelImportMapper.getAreaNames(proIdList);
        excelExportLog.setCreator(paramMap.get("userId")+"");
        excelExportLog.setAreaName(areaName);
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(paramMap));
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
    public ResultBody dealStatistics(Map paramMap) {
        //判断是项目统计还是业务员统计 查询类型（1：项目 2：业务员）
        String searchType = paramMap.get("searchType")+"";
        List<DealStatistics> proDataStatistics = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }

        if ("1".equals(searchType)){
            String projectIds = paramMap.get("projectIds")+"";
            if ("".equals(projectIds) || "null".equals(projectIds)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            List<String> proIds = Arrays.asList(projectIds.split(","));
            paramMap.put("proIds",proIds);
            PageHelper.startPage(pageIndex,pageSize);
            proDataStatistics = dataStatisticDao.getProDealStatisticsNew(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics2("proDeal",proDataStatistics));//计算合计值
        }else{
            String projectId = paramMap.get("projectId")+"";
            if ("".equals(projectId) || "null".equals(projectId)){
                return ResultBody.error(-1200002,"项目ID未传！！");
            }
            //查询项目是否区域项目
            String isRegion = messageMapper.getIsRegionByPro(projectId);
            List<String> proIds = new ArrayList<>();
            List<String> userCodes = new ArrayList<>();
            if ("1".equals(isRegion)){
                //获取项目ID
                proIds = dataStatisticDao.getPros(projectId);
                //获取项目下人员
                userCodes = dataStatisticDao.getProUsers(projectId,"qyzygw");
            }else{
                proIds.add(projectId);
                //获取项目下人员
                userCodes = dataStatisticDao.getProUsers(projectId,"zygw");
            }
            paramMap.put("userCode",userCodes);
            paramMap.put("proIds",proIds);
            PageHelper.startPage(pageIndex,pageSize);
            proDataStatistics = dataStatisticDao.getUserDealStatisticsNew(paramMap);
        }
        for (DealStatistics d:proDataStatistics) {
            d.setDealCount(d.getDealCount().replace(".0",""));
        }
        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    @Override
    public ResultBody regionDealStatistics(Map paramMap) {
        List<DealStatistics> proDataStatistics = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        if (paramMap.get("regionIds")!=null && !"".equals(paramMap.get("regionIds")+"")){
            String regionIds = paramMap.get("regionIds")+"";
            List<String> regionList = Arrays.asList(regionIds.split(","));
            paramMap.put("regionList",regionList);
        }
        PageHelper.startPage(pageIndex, pageSize);
        proDataStatistics = dataStatisticDao.getRegionDealStatisticsNew(paramMap);
        proDataStatistics.add(0, this.tlDataStatistics2("qyDeal",proDataStatistics));//计算合计值
        for (DealStatistics d:proDataStatistics) {
            d.setDealCount(d.getDealCount().replace(".0",""));
        }
        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    @Override
    public void dealStatisticsExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }


        String searchType = paramMap.get("searchType")+"";
        String userId = request.getHeader("userId");
        List<DealStatistics> proDataStatistics = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        List<String> proIds = new ArrayList<>();
        if ("1".equals(searchType)){
            excelName = "项目成交数据统计";
            String projectIds = paramMap.get("projectIds")+"";
            proIds = Arrays.asList(projectIds.split(","));
            paramMap.put("proIds",proIds);
            proDataStatistics = dataStatisticDao.getProDealStatisticsNew(paramMap);
            proDataStatistics.add(0, this.tlDataStatistics2("proDeal",proDataStatistics));//计算合计值
        }else{
            excelName = "项目成员成交数据统计";
            String projectId = paramMap.get("projectId")+"";
            //查询项目是否区域项目
            String isRegion = messageMapper.getIsRegionByPro(projectId);

            List<String> userCodes = new ArrayList<>();
            if ("1".equals(isRegion)){
                //获取项目ID
                proIds = dataStatisticDao.getPros(projectId);
                //获取项目下人员
                userCodes = dataStatisticDao.getProUsers(projectId,"qyzygw");
            }else{
                proIds.add(projectId);
                //获取项目下人员
                userCodes = dataStatisticDao.getProUsers(projectId,"zygw");
            }
            paramMap.put("userCode",userCodes);
            paramMap.put("proIds",proIds);
            proDataStatistics = dataStatisticDao.getUserDealStatisticsNew(paramMap);
        }
        for (DealStatistics d:proDataStatistics) {
            d.setDealCount(d.getDealCount().replace(".0",""));
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIds);
        excelExportLog.setCreator(userId);
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);

        if (proDataStatistics!=null && proDataStatistics.size()>0){
            if ("1".equals(searchType)){
//                headers = proDataStatistics.get(0).cardTitle1;
                headers = filedNames.toArray(new String[0]);
            }else{
                headers = proDataStatistics.get(0).cardTitle2;
            }
            int rowNum = 1;
            for (DealStatistics model : proDataStatistics) {
                model.setRownum(rowNum+"");
                Object[] oArray = model.toData1(filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void regionDealExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {

        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }
        List<DealStatistics> proDataStatistics = new ArrayList<>();
        String userId = request.getHeader("userId");
        //导出的文档下面的名字
        String excelName = "区域成交数据统计";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        List<String> regionList = new ArrayList<>();
        if (paramMap.get("regionIds")!=null && !"".equals(paramMap.get("regionIds")+"")){
            String regionIds = paramMap.get("regionIds")+"";
            regionList = Arrays.asList(regionIds.split(","));
            paramMap.put("regionList",regionList);
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        String areaName = "/";
        if (regionList.size()>0){
            areaName = excelImportMapper.getAreaNames(regionList);
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setAreaName(areaName);
        excelExportLog.setProjectId("/");
        excelExportLog.setProjectName("/");
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);

        proDataStatistics = dataStatisticDao.getRegionDealStatisticsNew(paramMap);
        proDataStatistics.add(0, this.tlDataStatistics2("qyDeal",proDataStatistics));//计算合计值
        for (DealStatistics d:proDataStatistics) {
            d.setDealCount(d.getDealCount().replace(".0",""));
        }
        if (proDataStatistics!=null && proDataStatistics.size()>0){
//            headers = proDataStatistics.get(0).cardTitle3;
            headers = filedNames.toArray(new String[0]);

            int rowNum = 1;
            for (DealStatistics model : proDataStatistics) {
                model.setRownum(rowNum+"");
                Object[] oArray = model.toData1(filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResultBody followUpStatistics(Map paramMap) {
        List<FollowUpStatistics> proDataStatistics = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex, pageSize);
        String projectIds = paramMap.get("projectIds")+"";
        if ("".equals(projectIds) || "null".equals(projectIds)){
            return ResultBody.error(-1200002,"项目ID未传！！");
        }
        List<String> proIds = Arrays.asList(projectIds.split(","));
        paramMap.put("proIds",proIds);
        proDataStatistics = dataStatisticDao.getFollowUpStatistics(paramMap);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            for (FollowUpStatistics followUpStatistics:proDataStatistics) {
                String oppCount = followUpStatistics.getOppCount();
                String followUpCount = followUpStatistics.getOppCount();
                String visitCount = followUpStatistics.getOppCount();
                String firstVisitCount = followUpStatistics.getOppCount();
                if ("0".equals(oppCount)){
                    followUpStatistics.setFirstVisitRate("0.00%");
                    followUpStatistics.setVisitRate("0.00%");
                    followUpStatistics.setFollowUpRate("0.00%");
                }else{
                    Double sum = Double.valueOf(oppCount);
                    Double followUp = Double.valueOf(followUpCount);
                    Double visit = Double.valueOf(visitCount);
                    Double firstVisit = Double.valueOf(firstVisitCount);
                    Double FollowUpRate = followUp/sum*100;
                    Double VisitRate = visit/sum*100;
                    Double FirstVisitRate = firstVisit/sum*100;
                    followUpStatistics.setFirstVisitRate(df.format(FirstVisitRate)+"%");
                    followUpStatistics.setVisitRate(df.format(VisitRate)+"%");
                    followUpStatistics.setFollowUpRate(df.format(FollowUpRate)+"%");
                }

            }
        }
        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    @Override
    public void followUpExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        List<FollowUpStatistics> proDataStatistics = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = "跟进数据统计";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        String projectIds = paramMap.get("projectIds")+"";
        List<String> proIds = Arrays.asList(projectIds.split(","));
        paramMap.put("proIds",proIds);
        proDataStatistics = dataStatisticDao.getFollowUpStatistics(paramMap);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            headers = proDataStatistics.get(0).cardTitle1;
            int rowNum = 1;
            for (FollowUpStatistics followUpStatistics:proDataStatistics) {
                String oppCount = followUpStatistics.getOppCount();
                String followUpCount = followUpStatistics.getOppCount();
                String visitCount = followUpStatistics.getOppCount();
                String firstVisitCount = followUpStatistics.getOppCount();
                if ("0".equals(oppCount)){
                    followUpStatistics.setFirstVisitRate("0.00%");
                    followUpStatistics.setVisitRate("0.00%");
                    followUpStatistics.setFollowUpRate("0.00%");
                }else{
                    Double sum = Double.valueOf(oppCount);
                    Double followUp = Double.valueOf(followUpCount);
                    Double visit = Double.valueOf(visitCount);
                    Double firstVisit = Double.valueOf(firstVisitCount);
                    Double FollowUpRate = followUp/sum*100;
                    Double VisitRate = visit/sum*100;
                    Double FirstVisitRate = firstVisit/sum*100;
                    followUpStatistics.setFirstVisitRate(df.format(FirstVisitRate)+"%");
                    followUpStatistics.setVisitRate(df.format(VisitRate)+"%");
                    followUpStatistics.setFollowUpRate(df.format(FollowUpRate)+"%");
                }
                followUpStatistics.setRownum(rowNum+"");
                Object[] oArray = followUpStatistics.toData1();
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResultBody proConversionRateStatistics(Map paramMap) {
        List<ProConversionRate> proDataStatistics = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        if (paramMap.get("projectIds")!=null && !"".equals(paramMap.get("projectIds")+"")){
            String regionIds = paramMap.get("projectIds")+"";
            List<String> regionList = Arrays.asList(regionIds.split(","));
            paramMap.put("projectIds",regionList);
        }
        PageHelper.startPage(pageIndex, pageSize);
        proDataStatistics = dataStatisticDao.proConversionRateStatistics(paramMap);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            for (ProConversionRate followUpStatistics:proDataStatistics) {
                String oppSum = followUpStatistics.getOppSum();
                String followCount = followUpStatistics.getFollowCount();
                String visitCount = followUpStatistics.getVisitCount();
                String threeOnesCount = followUpStatistics.getThreeOnesCount();
                String orderSum = followUpStatistics.getOrderSum();
                if ("0".equals(oppSum)){
                    followUpStatistics.setFollowRate("0.00%");
                    followUpStatistics.setVisitRate("0.00%");
                    followUpStatistics.setThreeOnesRate("0.00%");
                    followUpStatistics.setOrderRate("0.00%");
                }else{
                    Double sum = Double.valueOf(oppSum);
                    Double followUp = Double.valueOf(followCount);
                    Double visit = Double.valueOf(visitCount);
                    Double threeOnes = Double.valueOf(threeOnesCount);
                    Double order = Double.valueOf(orderSum);

                    Double FollowUpRate = followUp/sum*100;
                    Double VisitRate = visit/sum*100;
                    Double ThreeOnesRate = threeOnes/sum*100;
                    Double OrderRate = order/sum*100;
                    followUpStatistics.setFollowRate(df.format(FollowUpRate)+"%");
                    followUpStatistics.setVisitRate(df.format(VisitRate)+"%");
                    followUpStatistics.setThreeOnesRate(df.format(ThreeOnesRate)+"%");
                    followUpStatistics.setOrderRate(df.format(OrderRate)+"%");
                }

            }
        }
        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    public List<CustomerAddLogVo> getAllCustomerAddLogListSqlQuery(Map paramMap,List<Map> xcPro,String type) {
        if("1".equals(type)){
            paramMap.put("proIds",xcPro);
        }else if ("2".equals(type)){
            paramMap.put("regionIds",xcPro);
        }else {
            paramMap.put("proIds",xcPro);
        }
        List<CustomerAddLogVo> list = dataStatisticDao.getAllCustomerAddLogList(paramMap);
        return list;
    }

    @Override
    public ResultBody proConversionRateStatisticsNew(Map paramMap) {
        String searchType = paramMap.get("searchType")+"";
        List<ProConversionRate> proDataStatistics = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        if (paramMap.get("deadTime")!=null && !"".equals(paramMap.get("deadTime")+"")){
        }else {
            paramMap.put("deadTime",paramMap.get("endTime"));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        paramMap.put("pageNum","");
        paramMap.put("pageSize","");
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        int total = 0;
        List<Map> proIds;
        List<CustomerAddLogVo> customerAddLogList;
        List<CustomerFodLogVo> customerFodLogList = new ArrayList<>();
        if("1".equals(searchType)) {//项目转化率统计
            customerAddLogList = new ArrayList<>();
            //执行分页条件 获取每页的数据和总条数
            if (paramMap.get("projectIds") != null && !"".equals(paramMap.get("projectIds") + "")) {
                String regionIds = paramMap.get("projectIds") + "";
                List<String> regionList = Arrays.asList(regionIds.split(","));
                total = regionList.size();
                paramMap.put("projectIds", regionList);
            }
            //对项目查询结果分页 获取分页内的项目ID集合
            PageHelper.startPage(pageIndex, pageSize);
            proIds = dataStatisticDao.getProjectIds(paramMap);
            paramMap.put("proIds",proIds);
        }else if ("2".equals(searchType)) {//区域转化率统计
            customerAddLogList = new ArrayList<>();
            //执行分页条件 获取每页的数据和总条数
            if (paramMap.get("regionIds")!=null && !"".equals(paramMap.get("regionIds")+"")){
                String regionIds = paramMap.get("regionIds")+"";
                List<String> regionList = Arrays.asList(regionIds.split(","));
//                regionList = dataStatisticDao.getProList(regionList);
                total = regionList.size();
                paramMap.put("regionList",regionList);
            }
            //对区域查询结果分页 获取分页内的区域ID集合
            PageHelper.startPage(pageIndex, pageSize);
            proIds = dataStatisticDao.getRegionIds(paramMap);
            paramMap.put("regionIds",proIds);
        } else {
            customerAddLogList = new ArrayList<>();
            if (paramMap.get("projectIds") != null && !"".equals(paramMap.get("projectIds") + "")) {
                String regionIds = paramMap.get("projectIds") + "";
                List<String> regionList = Arrays.asList(regionIds.split(","));
                paramMap.put("projectIds", regionList);
            }
            PageHelper.startPage(1, 50);
            proIds = dataStatisticDao.getProjectIdsCs(paramMap);
            paramMap.put("proIds",proIds);
//            try {
//                GetRequest getRequest = new GetRequest(indexName, "_doc", documentId); // 指定 "_doc" 类型
//                GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
//                String string = getResponse.getSourceAsString();
//                customerAddLogList = Collections.singletonList(JSON.parseObject(string, CustomerAddLogVo.class));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            List<CustomerAddLogVo> filteredList = new ArrayList<>();
//            for (CustomerAddLogVo log : customerAddLogList) {
//                String customerAddress = paramMap.get("customerAddress")+"";
//                if ((customerAddress != null && !customerAddress.isEmpty()) &&
//                        log.getCustomerAddress() != null && log.getCustomerAddress().contains(customerAddress)) {
//                    filteredList.add(log);
//                }
//
//                if (source != null && !source.isEmpty() &&
//                        log.getSourceMode() != null && source.contains(log.getSourceMode())) {
//                    filteredList.add(log);
//                }
//
//                if (belongIndustriseList != null && !belongIndustriseList.isEmpty() &&
//                        log.getBelongIndustrise() != null && belongIndustriseList.contains(log.getBelongIndustrise())) {
//                    filteredList.add(log);
//                }
//
//                if (belongIndustriseTwoList != null && !belongIndustriseTwoList.isEmpty() &&
//                        log.getBelongIndustrise() != null && belongIndustriseTwoList.contains(log.getBelongIndustrise())) {
//                    filteredList.add(log);
//                }
//
//                if (belongIndustriseThreeList != null && !belongIndustriseThreeList.isEmpty() &&
//                        log.getBelongIndustrise() != null && belongIndustriseThreeList.contains(log.getBelongIndustrise())) {
//                    filteredList.add(log);
//                }
//
//                if (belongIndustriseFourList != null && !belongIndustriseFourList.isEmpty() &&
//                        log.getBelongIndustrise() != null && belongIndustriseFourList.contains(log.getBelongIndustrise())) {
//                    filteredList.add(log);
//                }
//            }
        }
        if (CollectionUtils.isEmpty(customerAddLogList)){
            //根据客户条件获取符合条件的客户
            customerAddLogList = dataStatisticDao.getAllCustomerAddLogList(paramMap);
//            List<List<Map>> xcPro = new ArrayList<>();
//            int len = 10;
//            if (proIds.size() > 0){
//                int size = proIds.size();
//                //len个线程每次查询多少条
//                int count = 0;
//                if(proIds.size() < len){
//                    len = proIds.size();
//                    count = 1;
//                }else {
//                    count = size / len;
//                }
//                for (int s = 0; s < len; s++) {
//                    if (s == len -1) {
//                        List<Map> fformList = proIds.subList(s * count, size);
//                        xcPro.add(s, fformList);
//                    } else {
//                        List<Map> fformList = proIds.subList(s * count, (s + 1) * count);
//                        xcPro.add(s, fformList);
//                    }
//                }
//                //将客户查询处理成线程
//                ExecutorService executor1 = Executors.newFixedThreadPool(len);
//                List<Future<List<CustomerAddLogVo>>> results1 = new ArrayList<>();
//                try {
//                    for (int i = 0; i < len; i++) {
//                        int finalI = i;
//                        Callable<List<CustomerAddLogVo>> task = () -> {
//                            // 在每个线程中执行需要的查询操作
//                            return getAllCustomerAddLogListSqlQuery(paramMap,xcPro.get(finalI),searchType);
//                        };
//                        Future<List<CustomerAddLogVo>> result = executor1.submit(task);
//                        results1.add(result);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    executor1.shutdown(); // 关闭线程池
//                    try {
//                        if (!executor1.awaitTermination(60, TimeUnit.SECONDS)) {
//                            executor1.shutdownNow();
//                        }
//                    } catch (InterruptedException ex) {
//                        executor1.shutdownNow();
//                    }
//                }
//                // 结果合并
//                for (Future<List<CustomerAddLogVo>> result : results1) {
//                    try {
//                        customerAddLogList.addAll(result.get());
//                    } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            if (!CollectionUtils.isEmpty(customerAddLogList)){
                //获取客户集合ID
                List<String> ids = customerAddLogList.stream().map(CustomerAddLogVo::getOpportunityClueId).distinct().collect(Collectors.toList());
                customerFodLogList = dataStatisticDao.getAllCustomerFodLogList(ids);
                //根据客户集合ID 查询对应的跟进交易记录
//            List<List<String>> xcIds = new ArrayList<>();
//            int len2 = 10;
//            if (ids.size() > 0){
//                int size = ids.size();
//                //len2个线程每次查询多少条
//                int count = 0;
//                if(ids.size() < len2){
//                    len2 = ids.size();
//                    count = 1;
//                }else {
//                    count = size / len2;
//                }
//                for (int s = 0; s < len2; s++) {
//                    if (s == len2 - 1) {
//                        List<String> fformList = ids.subList(s * count, size);
//                        xcIds.add(s, fformList);
//                    } else {
//                        List<String> fformList = ids.subList(s * count, (s + 1) * count);
//                        xcIds.add(s, fformList);
//                    }
//                }
//                //将客户跟进交易记录查询处理成线程
//                ExecutorService executor2 = Executors.newFixedThreadPool(len2);
//                List<Future<List<CustomerFodLogVo>>> results2 = new ArrayList<>();
//                try {
//                    for (int i = 0; i < len2; i++) {
//                        int finalI = i;
//                        Callable<List<CustomerFodLogVo>> task = () -> {
//                            // 在每个线程中执行需要的查询操作
//                            return dataStatisticDao.getAllCustomerFodLogList(xcIds.get(finalI));
//                        };
//                        Future<List<CustomerFodLogVo>> result = executor2.submit(task);
//                        results2.add(result);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    executor2.shutdown(); // 关闭线程池
//                    try {
//                        if (!executor2.awaitTermination(60, TimeUnit.SECONDS)) {
//                            executor2.shutdownNow();
//                        }
//                    } catch (InterruptedException ex) {
//                        executor2.shutdownNow();
//                    }
//                }
//                // 结果合并
//                for (Future<List<CustomerFodLogVo>> result : results2) {
//                    try {
//                        customerFodLogList.addAll(result.get());
//                    } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
                //将跟进交易记录按客户维度分组
                Map<String, List<CustomerFodLogVo>> recordMap = customerFodLogList.stream().collect(Collectors.groupingBy(CustomerFodLogVo::getOpportunityClueId));
                //循环客户集合 封装客户跟进交易数据
                List<CustomerFodLogVo> cc = new ArrayList<>();
                CustomerFodLogVo customerFodLogVo = new CustomerFodLogVo();
                customerFodLogVo.setBusinessId("visolink123");
                customerFodLogVo.setBusinessType("visolink123");
                customerFodLogVo.setBusinessTime(sf.format(DateUtil.date()));
                customerFodLogVo.setBusinessProjectId("visolink123");
                customerFodLogVo.setMainVisitProjectId("visolink123");
                customerFodLogVo.setIsThreeOnesStatus("visolink123");
                customerFodLogVo.setIsFirstComeVisitStatus("visolink123");
                customerFodLogVo.setIsFirstVisitStatus("visolink123");
                customerFodLogVo.setIsSignAfterVisitStatus("visolink123");
                customerFodLogVo.setIsStatistics("visolink123");
                cc.add(customerFodLogVo);
                customerAddLogList.stream().forEach(x->{
                    x.setCustomerFodLogList(recordMap.get(x.getOpportunityClueId()));
                    //塞一个不满足条件的空集合数据 防止无数据异常
                    if(CollectionUtils.isEmpty(x.getCustomerFodLogList())){
                        cc.get(0).setOpportunityClueId(x.getOpportunityClueId());
                        x.setCustomerFodLogList(cc);
                    }
                });
            }
        }
        Map reMap = new HashMap<>();
        List<ProConversionRate> reList = new ArrayList<>();
        if("1".equals(searchType)){//项目转化率统计
            if (CollectionUtils.isEmpty(customerAddLogList)){
                proIds.stream().forEach(x->{
                    ProConversionRate proConversionRate = new ProConversionRate();
                    proConversionRate.setName(String.valueOf(x.get("projectName")));
                    proConversionRate.setAddToComeVisitRate("0.00%");//新增转拜访率
                    proConversionRate.setAddToThreeOnesRate("0.00%");//新增转三个一率
                    proConversionRate.setAddToFollowVisitRate("0.00%");//新增转来访率
                    proConversionRate.setComeVisitToFollowVisitRate("0.00%");//拜访转来访率
                    proConversionRate.setThreeOnesToFollowVisitRate("0.00%");//三个一转来访率
                    proConversionRate.setFirstFollowVisitToReFollowVisitRate("0.00%");//首访转复访率
                    proConversionRate.setReFollowVisitToTradeRate("0.00%");//复访转成交率
                    proConversionRate.setFollowVisitToTradeRate("0.00%");//来访转成交率
                    proConversionRate.setReTradeRate("0.00%");//累计复购率
                    reList.add(proConversionRate);
                });
                reMap.put("list",reList);
                reMap.put("total",total);
                return ResultBody.success(reMap);
            }
            //将客户按照项目分组
            Map<String, List<CustomerAddLogVo>> oppMap = customerAddLogList.stream().collect(Collectors.groupingBy(CustomerAddLogVo :: getProjectId));
            //将项目分成多个线程执行 最后合并查询结果
            //int numThreads = proIds.size(); // 定义要启动的线程数
            int numThreads = 10; // 定义要启动的线程数
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Future<ProConversionRate>> results = new ArrayList<>();
            try {
                for (int i = 0; i < numThreads; i++) {
                    int finalI = i;
                    List<CustomerAddLogVo> finalCustomerAddLogList = customerAddLogList;
                    Callable<ProConversionRate> task = () -> {
                        // 在每个线程中执行需要的查询操作
                        return performQueryAndReturnResult(searchType,proIds.get(finalI),oppMap,paramMap, finalCustomerAddLogList);
                    };
                    Future<ProConversionRate> result = executor.submit(task);
                    results.add(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown(); // 关闭线程池
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                }
            }
            // 结果合并
            for (Future<ProConversionRate> result : results) {
                try {
                    reList.add(result.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            reMap.put("list",reList);
            reMap.put("total",total);
            return ResultBody.success(reMap);
        }else if ("2".equals(searchType)){//区域转化率
            if (CollectionUtils.isEmpty(customerAddLogList)){
                proIds.stream().forEach(region->{
                    ProConversionRate proConversionRate = new ProConversionRate();
                    proConversionRate.setName(String.valueOf(region.get("areaName")));
                    proConversionRate.setAddToComeVisitRate("0.00%");//新增转拜访率
                    proConversionRate.setAddToThreeOnesRate("0.00%");//新增转三个一率
                    proConversionRate.setAddToFollowVisitRate("0.00%");//新增转来访率
                    proConversionRate.setComeVisitToFollowVisitRate("0.00%");//拜访转来访率
                    proConversionRate.setThreeOnesToFollowVisitRate("0.00%");//三个一转来访率
                    proConversionRate.setFirstFollowVisitToReFollowVisitRate("0.00%");//首访转复访率
                    proConversionRate.setReFollowVisitToTradeRate("0.00%");//复访转成交率
                    proConversionRate.setFollowVisitToTradeRate("0.00%");//来访转成交率
                    proConversionRate.setReTradeRate("0.00%");//累计复购率
                    reList.add(proConversionRate);
                });
                reMap.put("list",reList);
                reMap.put("total",total);
                return ResultBody.success(reMap);
            }
            //将客户按照项目分组
            Map<String, List<CustomerAddLogVo>> oppMap = customerAddLogList.stream().filter(a -> a.getAreaId() != null && a.getAreaId() != "").collect(Collectors.groupingBy(CustomerAddLogVo::getAreaId));
            //将区域分成多个线程执行 最后合并查询结果
            //int numThreads = proIds.size(); // 定义要启动的线程数
            int numThreads = 10;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Future<ProConversionRate>> results = new ArrayList<>();
            try {
                for (int i = 0; i < numThreads; i++) {
                    int finalI = i;
                    List<CustomerAddLogVo> finalCustomerAddLogList1 = customerAddLogList;
                    Callable<ProConversionRate> task = () -> {
                        // 在每个线程中执行需要的查询操作
                        return performQueryAndReturnResult(searchType,proIds.get(finalI),oppMap,paramMap, finalCustomerAddLogList1);
                    };
                    Future<ProConversionRate> result = executor.submit(task);
                    results.add(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown(); // 关闭线程池
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                }
            }
            // 结果合并
            for (Future<ProConversionRate> result : results) {
                try {
                    reList.add(result.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            reMap.put("list",reList);
            reMap.put("total",total);
            return ResultBody.success(reMap);
        }else if ("3".equals(searchType)){//集团转化率
            if (CollectionUtils.isEmpty(customerAddLogList)) {
                ProConversionRate proConversionRate = new ProConversionRate();
                proConversionRate.setName("万洋集团");
                proConversionRate.setAddToComeVisitRate("0.00%");//新增转拜访率
                proConversionRate.setAddToThreeOnesRate("0.00%");//新增转三个一率
                proConversionRate.setAddToFollowVisitRate("0.00%");//新增转来访率
                proConversionRate.setComeVisitToFollowVisitRate("0.00%");//拜访转来访率
                proConversionRate.setThreeOnesToFollowVisitRate("0.00%");//三个一转来访率
                proConversionRate.setFirstFollowVisitToReFollowVisitRate("0.00%");//首访转复访率
                proConversionRate.setReFollowVisitToTradeRate("0.00%");//复访转成交率
                proConversionRate.setFollowVisitToTradeRate("0.00%");//来访转成交率
                proConversionRate.setReTradeRate("0.00%");//累计复购率
                reList.add(proConversionRate);
            }else {
                //新增转拜访客户 新增的客户里（指数据统计里的新增客户数），有多少客户已经拜访过了； 新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转拜访客户 日志表左连接客户跟进交易记录表 增加筛选项 跟进方式为上门拜访 在对满足条件的客户ID 去重
                List<CustomerAddLogVo> addCount = customerAddLogList.stream().filter(a -> a.getIsAdd().equals("1") &&
                        a.getReportCreateTime().compareTo(paramMap.get("startTime")+"") >= 0 && a.getReportCreateTime().compareTo(paramMap.get("endTime")+"") <= 0
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> addToCVisitCount = new ArrayList<>();
                if (addCount.size()>0){
                    addToCVisitCount = customerAddLogList.stream().filter(a-> addCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                                    b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //新增转三个一率 新增的客户里（指数据统计里的新增客户数），有多少客户已经达成了实际三个一； 新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转三个一客户 查询客户报备日志表 查询创建方式为手动录入且是否完成三个一为是的客户 针对客户ID 去重
                List<CustomerAddLogVo> addToThreeOnesCount = new ArrayList<>();
                if (addCount.size()>0){
                    addToThreeOnesCount = customerAddLogList.stream().filter(a-> addCount.contains(a) &&
                                    a.getCustomerFodLogList().stream().anyMatch(b -> Optional.ofNullable(b.getIsThreeOnesStatus()).map(date -> date.equals("1")).orElse(false)
                                            && b.getIsStatistics().equals("1") && b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //新增转来访客户 新增的客户里（指数据统计里的新增客户数），有多少客户已经来访过了；新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转来访客户 左连接客户跟进交易记录表 查询业务类型为邀约到访和自然来访的记录 针对客户ID 去重 联动招商来访客户为满足上述来访条件 但是项目和主项目不同的客户
                //正常新增到访客户
                List<CustomerAddLogVo> addToVisitCount = new ArrayList<>();
                if (addCount.size()>0){
                    addToVisitCount = customerAddLogList.stream().filter(a-> addCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsStatistics().equals("1") &&
                                    b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //拜访转首访率 已拜访过的客户里，有多少客户完成了首访；拜访客户 查询客户报备日志表左连接客户跟进交易记录表 查询业务类型为上门拜访的客户 针对客户ID去重 拜访转来访查询业务类型同时包含上门拜访和（y邀约拜访 自然来访）的客户 针对客户ID去重
                List<CustomerAddLogVo> cVisitCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> cVisitTofVisitCount = new ArrayList<>();
                if (cVisitCount.size()>0){
                    cVisitTofVisitCount = customerAddLogList.stream().filter(a ->cVisitCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                    b.getBusinessTime().compareTo(String.valueOf(cVisitCount.stream().filter(c ->
                                            c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c ->
                                            c.getCustomerFodLogList().stream().filter(d ->
                                                    d.getBusinessType().equals("2") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())
                                    ) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //三个一转首访率 实际达成三个一的客户里，有多少客户完成了首访； 三个一客户查询客户报备记录表 是否三个一客户是是的客户 针对客户ID去重 三个一转来访客户 左连接客户跟进交易记录表 查询 日志表是否三个一客户是是的客户且跟进交易记录表业务类型为邀约到访和自然来访的记录 针对客户ID 去重
                List<CustomerAddLogVo> threeOnesCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> Optional.ofNullable(b.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false) && b.getIsStatistics().equals("1") &&
                        Optional.ofNullable(b.getBusinessTime()).map(date -> date.compareTo(paramMap.get("startTime") + "") >= 0 && date.compareTo(paramMap.get("endTime") + "") <= 0).orElse(false))
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> threeOnesTofVisitCount = new ArrayList<>();
                if (threeOnesCount.size()>0){
                    threeOnesTofVisitCount = customerAddLogList.stream().filter(a ->threeOnesCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                    b.getBusinessTime().compareTo(threeOnesCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> Optional.ofNullable(d.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false) && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get()) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //首访转复访率 完成过首访的客户里，有多少客户完成了第二及以上次来访；首访客户 查询客户报备日志表左连接客户跟进交易记录表 查询业务类型为到访 且是否首访状态为是是的客户 针对客户ID 去重 首访转复访查询业务类型为到访 且是否首访状态为否的客户 针对客户ID 去重
                List<CustomerAddLogVo> firstVisitCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                        && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("1") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> firstVisitToReVisitCount = new ArrayList<>();
                if (firstVisitCount.size()>0){
                    firstVisitToReVisitCount = customerAddLogList.stream().filter(a -> firstVisitCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                                    && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("0") &&
                                    b.getBusinessTime().compareTo(String.valueOf(firstVisitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsFirstVisitStatus().equals("1") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //复访转成交率 完成两次及以上来访的客户里，有多少客户已经成交； 复访客户查询业务类型为到访 且是否首访状态为否的客户 针对客户ID 去重 复访转成交 查询业务类型为到访 且是否首访状态为否的客户 客户是否成交为是的客户 针对客户ID 去重
                List<CustomerAddLogVo> reVisitCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                        && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("0") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> reVisitToTradeCount = new ArrayList<>();
                if (reVisitCount.size()>0){
                    reVisitToTradeCount = customerAddLogList.stream().filter(a -> reVisitCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                                    b.getBusinessTime().compareTo(String.valueOf(reVisitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsFirstVisitStatus().equals("0") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //来访转成交率 来访过的客户里，有多少客户已经成交；来访客户查询业务类型为到访的客户 针对客户ID 去重 来访转成交 查询业务类型为到访 且客户是否成交为是的客户 针对客户ID 去重
                List<CustomerAddLogVo> visitCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsStatistics().equals("1") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> visitToTradeCount = new ArrayList<>();
                if (visitCount.size()>0) {
                    visitToTradeCount = customerAddLogList.stream().filter(a -> visitCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                                    b.getBusinessTime().compareTo(String.valueOf(visitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //累计复购率 已经成交的厂房客户里，有多少客户重复购买； 查询客户报备记录表是否签约状态为是的客户 复购客户查询是否复购为是的客户
                List<CustomerAddLogVo> tradeCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> tradeToReCount = new ArrayList<>();
                if (tradeCount.size() > 0) {
                    tradeToReCount = customerAddLogList.stream().filter(a -> tradeCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") && b.getBusinessTime().compareTo(
                                    String.valueOf(LocalDateTime.parse(String.valueOf(tradeCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> d.getBusinessType().equals("6")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get()), dtf).plusDays(2))
                            ) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //针对查询结果的客户ID 去重
                //新增
                int xzCount = addCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double xzSum = Double.valueOf(xzCount);
                //新增转拜访率
                int xzTcvCount = addToCVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double xzTcvSum = Double.valueOf(xzTcvCount);
                Double xzTcvRate;
                if (0 == xzSum) {
                    xzTcvRate = 0.0;
                }else {
                    xzTcvRate = xzTcvSum/xzSum*100;
                }
                //新增转三个一率
                int xzTtoCount = addToThreeOnesCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double xzTtoSum = Double.valueOf(xzTtoCount);
                Double xzTtoRate;
                if (0 == xzSum) {
                    xzTtoRate = 0.0;
                }else {
                    xzTtoRate = xzTtoSum/xzSum*100;
                }
                //新增转来访率
                int xzTvCount = addToVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double xzTvSum = Double.valueOf(xzTvCount);
                Double xzTvRate;
                if (0 == xzSum) {
                    xzTvRate = 0.0;
                }else {
                    xzTvRate = xzTvSum/xzSum*100;
                }
                //拜访
                int cvCount = cVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double cvSum = Double.valueOf(cvCount);
                //拜访转来访率
                int cvTvCount = cVisitTofVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double cvTvSum = Double.valueOf(cvTvCount);
                Double cvTvRate;
                if (0 == cvSum) {
                    cvTvRate = 0.0;
                }else {
                    cvTvRate = cvTvSum/cvSum*100;
                }
                //三个一
                int toCount = threeOnesCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double toSum = Double.valueOf(toCount);
                //三个一转来访率
                int toTvCount = threeOnesTofVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double toTvSum = Double.valueOf(toTvCount);
                Double toTvRate;
                if (0 == toSum) {
                    toTvRate = 0.0;
                }else {
                    toTvRate = toTvSum/toSum*100;
                }
                //首访
                int fvCount = firstVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double fvSum = Double.valueOf(fvCount);
                //首访转复访率
                int fvTrvCount = firstVisitToReVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double fvTrvSum = Double.valueOf(fvTrvCount);
                Double fvTrvRate;
                if (0 == fvSum) {
                    fvTrvRate = 0.0;
                }else {
                    fvTrvRate = fvTrvSum/fvSum*100;
                }
                //复访
                int rvCount = reVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double rvSum = Double.valueOf(rvCount);
                //复访转成交率
                int rvTtdCount = reVisitToTradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double rvTtdSum = Double.valueOf(rvTtdCount);
                Double rvTtdRate;
                if (0 == rvSum) {
                    rvTtdRate = 0.0;
                }else {
                    rvTtdRate = rvTtdSum/rvSum*100;
                }
                //来访
                int vCount = visitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                //来访转成交率
                int vTtdCount = visitToTradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double vTtdSum = Double.valueOf(vTtdCount);
                Double vTtdRate;
                if (0 == vCount) {
                    vTtdRate = 0.0;
                }else {
                    vTtdRate = vTtdSum/vCount*100;
                }
                //累计复购率
                int tdCount = tradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double tdSum = Double.valueOf(tdCount);
                int reTtdCount = tradeToReCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double reTtdSum = Double.valueOf(reTtdCount);
                Double reTtdRate;
                if (0 == tdSum) {
                    reTtdRate = 0.0;
                }else {
                    reTtdRate = reTtdSum/tdSum*100;
                }
                ProConversionRate proConversionRate = new ProConversionRate();
                proConversionRate.setName("万洋集团");
                proConversionRate.setAddToComeVisitRate(df.format(xzTcvRate)+"%");//新增转拜访率
                proConversionRate.setAddToThreeOnesRate(df.format(xzTtoRate)+"%");//新增转三个一率
                proConversionRate.setAddToFollowVisitRate(df.format(xzTvRate)+"%");//新增转来访率
                proConversionRate.setComeVisitToFollowVisitRate(df.format(cvTvRate)+"%");//拜访转来访率
                proConversionRate.setThreeOnesToFollowVisitRate(df.format(toTvRate)+"%");//三个一转来访率
                proConversionRate.setFirstFollowVisitToReFollowVisitRate(df.format(fvTrvRate)+"%");//首访转复访率
                proConversionRate.setReFollowVisitToTradeRate(df.format(rvTtdRate)+"%");//复访转成交率
                proConversionRate.setFollowVisitToTradeRate(df.format(vTtdRate)+"%");//来访转成交率
                proConversionRate.setReTradeRate(df.format(reTtdRate)+"%");//累计复购率
                reList.add(proConversionRate);
                reMap.put("list",reList);
                reMap.put("total",1);
            }
        }
        return ResultBody.success(reMap);
    }
    public String convertUtcToCst(String utcTime) {
        // 解析UTC时间字符串（包含时区信息）
        ZonedDateTime utcZoned = ZonedDateTime.parse(utcTime);
        // 转换为中国标准时间（UTC+8）
        ZonedDateTime cstZoned = utcZoned.withZoneSameInstant(java.time.ZoneId.of("Asia/Shanghai"));
        // 格式化为目标字符串
        return cstZoned.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    @Transactional
    @Override
    public ResultBody proConversionRateStatisticsNewPL(Map paramMap) {
        String searchType = paramMap.get("searchType")+"";
        List<ProConversionRate> proDataStatistics = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        if (paramMap.get("deadTime")!=null && !"".equals(paramMap.get("deadTime")+"")){
            paramMap.put("deadTime",convertUtcToCst(paramMap.get("deadTime")+""));
        }else {
            paramMap.put("deadTime",DateUtil.format(DateUtil.beginOfDay(new Date()), "yyyy-MM-dd")+" 23:59:59");
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        paramMap.put("pageNum","");
        paramMap.put("pageSize","");
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        int total = 0;
        List<Map> proIds = new ArrayList<>();
//        List<CustomerAddLogVo> customerAddLogList = new ArrayList<>();
        List<CustomerFodLogVo> customerFodLogList = new ArrayList<>();
        if("1".equals(searchType)) {//项目转化率统计
            //执行分页条件 获取每页的数据和总条数
            if (paramMap.get("projectIds") != null && !"".equals(paramMap.get("projectIds") + "")) {
                String regionIds = paramMap.get("projectIds") + "";
                List<String> regionList = Arrays.asList(regionIds.split(","));
                total = regionList.size();
                paramMap.put("projectIds", regionList);
            }
            //对项目查询结果分页 获取分页内的项目ID集合
            if(!"all".equals(paramMap.get("pagination"))){
                PageHelper.startPage(pageIndex, pageSize);
            }

            proIds = dataStatisticDao.getProjectIds(paramMap);
            paramMap.put("proIds",proIds);
        }else if ("2".equals(searchType)) {//区域转化率统计
            //执行分页条件 获取每页的数据和总条数
            if (paramMap.get("regionIds")!=null && !"".equals(paramMap.get("regionIds")+"")){
                String regionIds = paramMap.get("regionIds")+"";
                List<String> regionList = Arrays.asList(regionIds.split(","));
//                regionList = dataStatisticDao.getProList(regionList);
                total = regionList.size();
                paramMap.put("regionList",regionList);
            }
            //对区域查询结果分页 获取分页内的区域ID集合
            PageHelper.startPage(pageIndex, pageSize);
            proIds = dataStatisticDao.getRegionIds(paramMap);
            paramMap.put("regionIds",proIds);
        } else {
            if (paramMap.get("projectIds") != null && !"".equals(paramMap.get("projectIds") + "")) {
                String regionIds = paramMap.get("projectIds") + "";
                List<String> regionList = Arrays.asList(regionIds.split(","));
                paramMap.put("projectIds", regionList);
            }
            proIds = dataStatisticDao.getProjectIdsCs(paramMap);
            paramMap.put("proIds",proIds);

        }
            //根据条件查询新增客户分母数据
                //新增分母
                List<Map> customerAddLogList = dataStatisticDao.getAllCustomerAddLogListPL(paramMap);
                    // 使用Stream API提取opportunityClueId字段
                    List<String> opportunityClueIds = customerAddLogList
                    .stream()
                    // 提取每个对象的opportunityClueId字段
                    .map(map -> (String) map.get("opportunityClueId"))
                    // 过滤可能的null值（如果需要）
                    .filter(Objects::nonNull)
                    // 转换为List
                    .collect(Collectors.toList());

                       //通过分母id查分子
                       paramMap.put("oppoIds",opportunityClueIds);
                        //拜访分子（新增转拜访率）
                        List<Map> addVisit = dataStatisticDao.getAllCustomervisitMolecule(paramMap);
                        //三个一分子（新增转三个率）
                        List<Map> addThreeOne = dataStatisticDao.getAllCustomerThreeOneMolecule(paramMap);
                        //来访分子（新增转来访率）
                        List<Map> addDaoFang = dataStatisticDao.getAllCustomerDaoFangMolecule(paramMap);
                //拜访分母
                List<Map> allCustomervisit = dataStatisticDao.getAllCustomervisit(paramMap);
                    // 使用Stream API提取opportunityClueId字段
                    List<String> allCustomervisitIds = allCustomervisit
                    .stream()
                    // 提取每个对象的opportunityClueId字段
                    .map(map -> (String) map.get("opportunityClueId"))
                    // 过滤可能的null值（如果需要）
                    .filter(Objects::nonNull)
                    // 转换为List
                    .collect(Collectors.toList());
                       //通过分母id查分子
                       paramMap.put("oppoIds",allCustomervisitIds);
                        //首访分子（拜访转首访率）
                        List<Map> visitShouFang = dataStatisticDao.getAllCustomershouFangMolecule(paramMap);


                //三个一分母
                List<Map> allCustomerThreeOne = dataStatisticDao.getAllCustomerThreeOne(paramMap);
                    // 使用Stream API提取opportunityClueId字段
                    List<String> allCustomerThreeOneIds = allCustomerThreeOne
                    .stream()
                    // 提取每个对象的opportunityClueId字段
                    .map(map -> (String) map.get("opportunityClueId"))
                    // 过滤可能的null值（如果需要）
                    .filter(Objects::nonNull)
                    // 转换为List
                    .collect(Collectors.toList());
                       //通过分母id查分子
                       paramMap.put("oppoIds",allCustomerThreeOneIds);
                        //首访分子（三个一转首访率）
                        List<Map> threeOneShoufang = dataStatisticDao.getAllCustomershouFangMolecule(paramMap);


                //来访分母
                List<Map> allCustomerDaoFang = dataStatisticDao.getAllCustomerDaoFang(paramMap);
                        // 使用Stream API提取opportunityClueId字段
                        List<String> allCustomerDaoFangIds = allCustomerDaoFang
                        .stream()
                        // 提取每个对象的opportunityClueId字段
                        .map(map -> (String) map.get("opportunityClueId"))
                        // 过滤可能的null值（如果需要）
                        .filter(Objects::nonNull)
                        // 转换为List
                        .collect(Collectors.toList());
                           //通过分母id查分子
                           paramMap.put("oppoIds",allCustomerDaoFangIds);
                            //成交分子（来访转成交率）
                            List<Map> daoFangChengjiao = dataStatisticDao.getAllCustomerchengjiaoMolecule(paramMap);


                //首访分母
                List<Map> allCustomershouFang = dataStatisticDao.getAllCustomershouFang(paramMap);
                        // 使用Stream API提取opportunityClueId字段
                        List<String> allCustomershouFangIds = allCustomershouFang
                        .stream()
                        // 提取每个对象的opportunityClueId字段
                        .map(map -> (String) map.get("opportunityClueId"))
                        // 过滤可能的null值（如果需要）
                        .filter(Objects::nonNull)
                        // 转换为List
                        .collect(Collectors.toList());
                           //通过分母id查分子
                           paramMap.put("oppoIds",allCustomershouFangIds);
                            //复访分子（首访转复访率）
                            List<Map> shoufangFufang = dataStatisticDao.getAllCustomerfuFangMolecule(paramMap);


                //复访分母
                List<Map> allCustomerfuFang = dataStatisticDao.getAllCustomerfuFang(paramMap);
                        // 使用Stream API提取opportunityClueId字段
                        List<String> allCustomerfuFangIds = allCustomerfuFang
                        .stream()
                        // 提取每个对象的opportunityClueId字段
                        .map(map -> (String) map.get("opportunityClueId"))
                        // 过滤可能的null值（如果需要）
                        .filter(Objects::nonNull)
                        // 转换为List
                        .collect(Collectors.toList());
                           //通过分母id查分子
                           paramMap.put("oppoIds",allCustomerfuFangIds);
                            //成交分子（复访转成交率）
                            List<Map> fufangChengjiao = dataStatisticDao.getAllCustomerchengjiaoMolecule(paramMap);



                //成交分母
                List<Map> allCustomerchengjiao = dataStatisticDao.getAllCustomerchengjiao(paramMap);
                        // 使用Stream API提取opportunityClueId字段
                        List<String> allCustomerchengjiaoIds = allCustomerchengjiao
                        .stream()
                        // 提取每个对象的opportunityClueId字段
                        .map(map -> (String) map.get("opportunityClueId"))
                        // 过滤可能的null值（如果需要）
                        .filter(Objects::nonNull)
                        // 转换为List
                        .collect(Collectors.toList());
                           //通过分母id查分子
                           paramMap.put("oppoIds",allCustomerchengjiaoIds);
                            //复购分子（成交转复购率）
                            List<Map> chengjiaoFugou = dataStatisticDao.getAllCustomerFugouMolecule(paramMap);



        Map reMap = new HashMap<>();
        List<ProConversionRate> reList = new ArrayList<>();
        if("1".equals(searchType)){
            //项目转化率统计
            //将客户按照项目分组并存储到类中
            NumeratorAndDenominator numeratorAndDenominator = new NumeratorAndDenominator();
            numeratorAndDenominator.setCustomerAddLogList(itemGrouping(customerAddLogList));
            numeratorAndDenominator.setAddVisit(itemGrouping(addVisit));
            numeratorAndDenominator.setAddThreeOne(itemGrouping(addThreeOne));
            numeratorAndDenominator.setAddDaoFang(itemGrouping(addDaoFang));
            numeratorAndDenominator.setAllCustomervisit(itemGrouping(allCustomervisit));
            numeratorAndDenominator.setVisitShouFang(itemGrouping(visitShouFang));
            numeratorAndDenominator.setAllCustomerThreeOne(itemGrouping(allCustomerThreeOne));
            numeratorAndDenominator.setThreeOneShoufang(itemGrouping(threeOneShoufang));
            numeratorAndDenominator.setAllCustomerDaoFang(itemGrouping(allCustomerDaoFang));
            numeratorAndDenominator.setDaoFangChengjiao(itemGrouping(daoFangChengjiao));
            numeratorAndDenominator.setAllCustomershouFang(itemGrouping(allCustomershouFang));
            numeratorAndDenominator.setShoufangFufang(itemGrouping(shoufangFufang));
            numeratorAndDenominator.setAllCustomerfuFang(itemGrouping(allCustomerfuFang));
            numeratorAndDenominator.setFufangChengjiao(itemGrouping(fufangChengjiao));
            numeratorAndDenominator.setAllCustomerchengjiao(itemGrouping(allCustomerchengjiao));
            numeratorAndDenominator.setChengjiaoFugou(itemGrouping(chengjiaoFugou));
            //将项目分成多个线程执行 最后合并查询结果
            int threadCount = Math.min(proIds.size(), 10);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<List<ProConversionRate>>> results = new ArrayList<>();
            try {
                // 计算每个线程应处理的项目数量
                int totalProjects = proIds.size();
                int baseProjectsPerThread = totalProjects / threadCount;
                int remainingProjects = totalProjects % threadCount;

                int currentIndex = 0;
                for (int i = 0; i < threadCount; i++) {
                    // 计算当前线程应处理的项目数量（前remainingProjects个线程多处理1个）
                    int projectsForThread = baseProjectsPerThread + (i < remainingProjects ? 1 : 0);

                    // 截取当前线程负责的项目子集
                    int endIndex = currentIndex + projectsForThread;
                    List<Map> subProIds = proIds.subList(currentIndex, endIndex);
                    currentIndex = endIndex;

                    // 创建任务并提交到线程池
                    Callable<List<ProConversionRate>> task = () ->
                            performQueryAndReturnResultPL(searchType, subProIds,numeratorAndDenominator);

                    Future<List<ProConversionRate>> future = executor.submit(task);
                    results.add(future);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 优雅关闭线程池
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt(); // 恢复中断状态
                }
            }
            // 结果合并
            for (Future<List<ProConversionRate>> result : results) {
                try {
                    reList.addAll(result.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            reMap.put("list",reList);
            reMap.put("total",total);
            return ResultBody.success(reMap);
        }else if ("2".equals(searchType)){//区域转化率

            //将客户按照区域分组
            NumeratorAndDenominator numeratorAndDenominator = new NumeratorAndDenominator();
            numeratorAndDenominator.setCustomerAddLogList(regionGrouping(customerAddLogList));
            numeratorAndDenominator.setAddVisit(regionGrouping(addVisit));
            numeratorAndDenominator.setAddThreeOne(regionGrouping(addThreeOne));
            numeratorAndDenominator.setAddDaoFang(regionGrouping(addDaoFang));
            numeratorAndDenominator.setAllCustomervisit(regionGrouping(allCustomervisit));
            numeratorAndDenominator.setVisitShouFang(regionGrouping(visitShouFang));
            numeratorAndDenominator.setAllCustomerThreeOne(regionGrouping(allCustomerThreeOne));
            numeratorAndDenominator.setThreeOneShoufang(regionGrouping(threeOneShoufang));
            numeratorAndDenominator.setAllCustomerDaoFang(regionGrouping(allCustomerDaoFang));
            numeratorAndDenominator.setDaoFangChengjiao(regionGrouping(daoFangChengjiao));
            numeratorAndDenominator.setAllCustomershouFang(regionGrouping(allCustomershouFang));
            numeratorAndDenominator.setShoufangFufang(regionGrouping(shoufangFufang));
            numeratorAndDenominator.setAllCustomerfuFang(regionGrouping(allCustomerfuFang));
            numeratorAndDenominator.setFufangChengjiao(regionGrouping(fufangChengjiao));
            numeratorAndDenominator.setAllCustomerchengjiao(regionGrouping(allCustomerchengjiao));
            numeratorAndDenominator.setChengjiaoFugou(regionGrouping(chengjiaoFugou));
            //将区域分成多个线程执行 最后合并查询结果
            int threadCount = Math.min(proIds.size(), 10);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<List<ProConversionRate>>> results = new ArrayList<>();
            try {
                // 计算每个线程应处理的项目数量
                int totalProjects = proIds.size();
                int baseProjectsPerThread = totalProjects / threadCount;
                int remainingProjects = totalProjects % threadCount;

                int currentIndex = 0;
                for (int i = 0; i < threadCount; i++) {
                    // 计算当前线程应处理的项目数量（前remainingProjects个线程多处理1个）
                    int projectsForThread = baseProjectsPerThread + (i < remainingProjects ? 1 : 0);

                    // 截取当前线程负责的项目子集
                    int endIndex = currentIndex + projectsForThread;
                    List<Map> subProIds = proIds.subList(currentIndex, endIndex);
                    currentIndex = endIndex;

                    // 创建任务并提交到线程池
                    Callable<List<ProConversionRate>> task = () ->
                            performQueryAndReturnResultPL(searchType, subProIds,numeratorAndDenominator);

                    Future<List<ProConversionRate>> future = executor.submit(task);
                    results.add(future);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 优雅关闭线程池
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt(); // 恢复中断状态
                }
            }
            // 结果合并
            for (Future<List<ProConversionRate>> result : results) {
                try {
                    reList.addAll(result.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            reMap.put("list",reList);
            reMap.put("total",total);
            return ResultBody.success(reMap);
        }else if ("3".equals(searchType)){//集团转化率
            ///新增分母（基础分母，用于“新增转XX率”系列计算）

            // 1. 拜访分子（新增转拜访率）：计算符合条件的分子数（ID匹配+分母完成时间晚于分子）
            long addVisitingMolecules = calculateMatchCount(customerAddLogList, addVisit);

            // 2. 三个一分子（新增转三个率）：计算符合条件的分子数
            long addThreeOneMolecules = calculateMatchCount(customerAddLogList, addThreeOne);

            // 3. 来访分子（新增转来访率）：计算符合条件的分子数
            long addDaoFangMolecules = calculateMatchCount(customerAddLogList, addDaoFang);
            // 计算分子有但分母没有的客户ID数量（使用Stream API）
            long addDaoFangMoleculesDifference = calculateMissingCustomerCountWithStream(customerAddLogList, addDaoFang);

            // 4. 首访分子（拜访转首访率）：先获取“拜访分母”，再计算符合条件的首访分子数
            long visitToShouFangMolecules = calculateMatchCount(allCustomervisit, visitShouFang);
            // 计算分子有但分母没有的客户ID数量（使用Stream API）
            long visitToShouFangMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomervisit, visitShouFang);

            // 5. 首访分子（三个一转首访率）：先获取“三个一分母”，再计算符合条件的首访分子数
            long threeOneToShouFangMolecules = calculateMatchCount(allCustomerThreeOne, threeOneShoufang);
            // 计算分子有但分母没有的客户ID数量（使用Stream API）
            long threeOneToShouFangMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomerThreeOne, threeOneShoufang);

            // 6. 成交分子（来访转成交率）：先获取“来访分母”，再计算符合条件的成交分子数
            long daoFangToChengjiaoMolecules = calculateMatchCount(allCustomerDaoFang, daoFangChengjiao);
            long daoFangToChengjiaoMoleculesDifference = 0;
//                long daoFangToChengjiaoMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomerDaoFangList, daoFangChengjiaoList);

            // 7. 复访分子（首访转复访率）：先获取“首访分母”，再计算符合条件的复访分子数
            long shouFangToFuFangMolecules = calculateMatchCount(allCustomershouFang, shoufangFufang);
            long shouFangToFuFangMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomershouFang, shoufangFufang);

            // 8. 成交分子（复访转成交率）：先获取“复访分母”，再计算符合条件的成交分子数
            long fuFangToChengjiaoMolecules = calculateMatchCount(allCustomerfuFang, fufangChengjiao);
            long fuFangToChengjiaoMoleculesDifference = 0;
//                long fuFangToChengjiaoMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomerfuFangList, fufangChengjiaoList);

            // 9. 复购分子（成交转复购率）：先获取“成交分母”，再计算符合条件的复购分子数
            long chengJiaoToFuGouMolecules = calculateMatchCount(allCustomerchengjiao, chengjiaoFugou);
            // -------------------------- 新增：转化率计算（完全对齐老代码逻辑）--------------------------
            // 注意：分母用size()（已处理去重），分子用你已算的long型变量转Double
            int xzCount = (customerAddLogList != null ? customerAddLogList.size() : 0); // 新增分母总数（已去重）
            Double xzSum = Double.valueOf(xzCount);

            int cvCount = (allCustomervisit != null ? allCustomervisit.size() : 0); // 拜访分母总数（已去重）
            Double cvSum = Double.valueOf(cvCount);

            int toCount = (allCustomerThreeOne != null ? allCustomerThreeOne.size() : 0); // 三个一分母总数（已去重）
            Double toSum = Double.valueOf(toCount);

            int vCount = (allCustomerDaoFang != null ? allCustomerDaoFang.size() : 0); // 来访分母总数（已去重）
            Double vSum = Double.valueOf(vCount);

            int fvCount = (allCustomershouFang != null ? allCustomershouFang.size() : 0); // 首访分母总数（已去重）
            Double fvSum = Double.valueOf(fvCount);

            int rvCount = (allCustomerfuFang != null ? allCustomerfuFang.size() : 0); // 复访分母总数（已去重）
            Double rvSum = Double.valueOf(rvCount);

            int tdCount = (allCustomerchengjiao != null ? allCustomerchengjiao.size() : 0); // 成交分母总数（已去重）
            Double tdSum = Double.valueOf(tdCount);


            // 1. 新增转拜访率（对齐老代码 xzTcvRate）
            Double addVisitingSum = Double.valueOf(addVisitingMolecules);
            Double xzTcvRate = (0 == xzSum) ? 0.0 : (addVisitingSum / xzSum) * 100;

            // 2. 新增转三个一率（对齐老代码 xzTtoRate）
            Double addThreeOneSum = Double.valueOf(addThreeOneMolecules);
            Double xzTtoRate = (0 == xzSum) ? 0.0 : (addThreeOneSum / xzSum) * 100;

            // 3. 新增转来访率（对齐老代码 xzTvRate，按老代码特殊逻辑，若需叠加其他分子可调整）
            Double addDaoFangSum = Double.valueOf(addDaoFangMolecules);
            Double addDaoFangDifferenceSum = Double.valueOf(addDaoFangMoleculesDifference);
            Double xzTvRate = (0 == (xzSum + addDaoFangDifferenceSum)) ? 0.0 :
                    ((addDaoFangSum + addDaoFangDifferenceSum) / (xzSum + addDaoFangDifferenceSum)) * 100;

            // 4. 拜访转来访率（即拜访转首访率，对齐老代码 cvTvRate）
            Double visitToShouFangSum = Double.valueOf(visitToShouFangMolecules);
            Double visitToShouFangDifference = Double.valueOf(visitToShouFangMoleculesDifference);
            Double cvTvRate = (0 == (cvSum + visitToShouFangDifference)) ? 0.0 :
                    ((visitToShouFangSum + visitToShouFangDifference) / (cvSum + visitToShouFangDifference)) * 100;

            // 5. 三个一转来访率（即三个一转首访率，对齐老代码 toTvRate）
            Double threeOneToShouFangSum = Double.valueOf(threeOneToShouFangMolecules);
            Double threeOneToShouFangDifference= Double.valueOf(threeOneToShouFangMoleculesDifference);
            Double toTvRate = (0 == (toSum + threeOneToShouFangDifference)) ? 0.0 :
                    ((threeOneToShouFangSum + threeOneToShouFangDifference) / (toSum + threeOneToShouFangDifference)) * 100;

            // 6. 首访转复访率（对齐老代码 fvTrvRate）
            Double shouFangToFuFangSum = Double.valueOf(shouFangToFuFangMolecules);
            Double shouFangToFuFangDifference = Double.valueOf(shouFangToFuFangMoleculesDifference);
            Double fvTrvRate = (0 == (fvSum + shouFangToFuFangDifference)) ? 0.0 :
                    ((shouFangToFuFangSum + shouFangToFuFangDifference) / (fvSum + shouFangToFuFangDifference)) * 100;

            // 7. 复访转成交率
            Double fuFangToChengjiaoSum = Double.valueOf(fuFangToChengjiaoMolecules);
            Double fuFangToChengjiaoDifferenceSum = Double.valueOf(fuFangToChengjiaoMoleculesDifference);
            Double rvTtdRate = (0 == (rvSum + fuFangToChengjiaoDifferenceSum)) ? 0.0 :
                    ((fuFangToChengjiaoSum + fuFangToChengjiaoDifferenceSum) / (rvSum + fuFangToChengjiaoDifferenceSum)) * 100;

            // 8. 来访转成交率
            Double daoFangToChengjiaoSum = Double.valueOf(daoFangToChengjiaoMolecules);
            Double daoFangToChengjiaoDifferenceSum = Double.valueOf(daoFangToChengjiaoMoleculesDifference);
            Double vTtdRate = (0 == (vSum + daoFangToChengjiaoDifferenceSum)) ? 0.0 :
                    ((daoFangToChengjiaoSum + daoFangToChengjiaoDifferenceSum) / (vSum + daoFangToChengjiaoDifferenceSum)) * 100;

            // 9. 累计复购率（成交转复购率，对齐老代码 reTtdRate）
            Double chengJiaoToFuGouSum = Double.valueOf(chengJiaoToFuGouMolecules);
            Double reTtdRate = (0 == tdSum) ? 0.0 : (chengJiaoToFuGouSum / tdSum) * 100;


            // -------------------------- 新增：封装结果（与老代码返回类型完全一致）--------------------------
            ProConversionRate proConversionRate = new ProConversionRate();
            proConversionRate.setName("万洋集团"); // 项目名称
            proConversionRate.setAddToComeVisitRate(df.format(xzTcvRate) + "%"); // 新增转拜访率
            proConversionRate.setAddToThreeOnesRate(df.format(xzTtoRate) + "%"); // 新增转三个一率
            proConversionRate.setAddToFollowVisitRate(df.format(xzTvRate) + "%"); // 新增转来访率
            proConversionRate.setComeVisitToFollowVisitRate(df.format(cvTvRate) + "%"); // 拜访转来访率
            proConversionRate.setThreeOnesToFollowVisitRate(df.format(toTvRate) + "%"); // 三个一转来访率
            proConversionRate.setFirstFollowVisitToReFollowVisitRate(df.format(fvTrvRate) + "%"); // 首访转复访率
            proConversionRate.setReFollowVisitToTradeRate(df.format(rvTtdRate) + "%"); // 复访转成交率
            proConversionRate.setFollowVisitToTradeRate(df.format(vTtdRate) + "%"); // 来访转成交率
            proConversionRate.setReTradeRate(df.format(reTtdRate) + "%"); // 累计复购率
            reList.add(proConversionRate);
            reMap.put("list",reList);
            reMap.put("total",1);
        }
        return ResultBody.success(reMap);
    }
    public Map<String, List<Map>> itemGrouping(List<Map> customer){
        // 假设Map中存储projectId的键名为"projectId"（根据实际键名修改）
        Map<String, List<Map>> oppMap = customer.stream()
                // 从每个Map中获取"projectId"的值作为分组键
                .collect(Collectors.groupingBy(
                        map -> (String) map.get("projectId") // 强转为String类型（根据实际类型调整）
                ));
        return oppMap;
    }
    public Map<String, List<Map>> regionGrouping(List<Map> customer){
        // 假设Map中存储projectId的键名为"projectId"（根据实际键名修改）
        Map<String, List<Map>> oppMap = customer.stream()
                // 从每个Map中获取"projectId"的值作为分组键
                .collect(Collectors.groupingBy(
                        map -> (String) map.get("areaId") // 强转为String类型（根据实际类型调整）
                ));
        return oppMap;
    }
    @Override
    public void proConversionRateStatisticsExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        String searchType = paramMap.get("searchType")+"";

        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        if (paramMap.get("deadTime")!=null && !"".equals(paramMap.get("deadTime")+"")){
        }else {
            paramMap.put("deadTime",paramMap.get("endTime"));
        }
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);

        //导出的文档下面的名字
        String excelName = "项目转化率统计";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        List<Map> proIds;
        List<String> regionList = new ArrayList<>();
        List<CustomerAddLogVo> customerAddLogList;
        List<CustomerFodLogVo> customerFodLogList = new ArrayList<>();
        if("1".equals(searchType)) {//项目转化率统计
            customerAddLogList = new ArrayList<>();
            excelName = "项目转化率统计";
            if (paramMap.get("projectIds") != null && !"".equals(paramMap.get("projectIds") + "")) {
                String regionIds = paramMap.get("projectIds") + "";
                regionList = Arrays.asList(regionIds.split(","));
                paramMap.put("projectIds", regionList);
            }
            proIds = dataStatisticDao.getProjectIds(paramMap);
            paramMap.put("proIds",proIds);
        }else if("2".equals(searchType)) {//区域转化率统计
            customerAddLogList = new ArrayList<>();
            excelName = "区域转化率统计";
            if (paramMap.get("regionIds")!=null && !"".equals(paramMap.get("regionIds")+"")){
                String regionIds = paramMap.get("regionIds")+"";
                regionList = Arrays.asList(regionIds.split(","));
                paramMap.put("regionList",regionList);
            }
            proIds = dataStatisticDao.getRegionIds(paramMap);
            paramMap.put("regionIds",proIds);
        }else {
            customerAddLogList = new ArrayList<>();
            if (paramMap.get("projectIds") != null && !"".equals(paramMap.get("projectIds") + "")) {
                String regionIds = paramMap.get("projectIds") + "";
                regionList = Arrays.asList(regionIds.split(","));
                paramMap.put("projectIds", regionList);
            }
            PageHelper.startPage(1, 50);
            proIds = dataStatisticDao.getProjectIdsCs(paramMap);
            paramMap.put("proIds",proIds);
            //            try {
//                GetRequest getRequest = new GetRequest(indexName, "_doc", documentId); // 指定 "_doc" 类型
//                GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
//                String string = getResponse.getSourceAsString();
//                customerAddLogList = Collections.singletonList(JSON.parseObject(string, CustomerAddLogVo.class));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            List<CustomerAddLogVo> filteredList = new ArrayList<>();
//            for (CustomerAddLogVo log : customerAddLogList) {
//                String customerAddress = paramMap.get("customerAddress")+"";
//                if ((customerAddress != null && !customerAddress.isEmpty()) &&
//                        log.getCustomerAddress() != null && log.getCustomerAddress().contains(customerAddress)) {
//                    filteredList.add(log);
//                }
//
//                if (source != null && !source.isEmpty() &&
//                        log.getSourceMode() != null && source.contains(log.getSourceMode())) {
//                    filteredList.add(log);
//                }
//
//                if (belongIndustriseList != null && !belongIndustriseList.isEmpty() &&
//                        log.getBelongIndustrise() != null && belongIndustriseList.contains(log.getBelongIndustrise())) {
//                    filteredList.add(log);
//                }
//
//                if (belongIndustriseTwoList != null && !belongIndustriseTwoList.isEmpty() &&
//                        log.getBelongIndustrise() != null && belongIndustriseTwoList.contains(log.getBelongIndustrise())) {
//                    filteredList.add(log);
//                }
//
//                if (belongIndustriseThreeList != null && !belongIndustriseThreeList.isEmpty() &&
//                        log.getBelongIndustrise() != null && belongIndustriseThreeList.contains(log.getBelongIndustrise())) {
//                    filteredList.add(log);
//                }
//
//                if (belongIndustriseFourList != null && !belongIndustriseFourList.isEmpty() &&
//                        log.getBelongIndustrise() != null && belongIndustriseFourList.contains(log.getBelongIndustrise())) {
//                    filteredList.add(log);
//                }
//            }
            if("3".equals(searchType)) {//集团转化率统计
                excelName = "集团转化率统计";
            }
        }

        if (CollectionUtils.isEmpty(customerAddLogList)){
            //根据客户条件获取符合条件的客户
            customerAddLogList = dataStatisticDao.getAllCustomerAddLogList(paramMap);
//            List<List<Map>> xcPro = new ArrayList<>();
//            int len = 10;
//            if (proIds.size() > 0){
//                int size = proIds.size();
//                //len个线程每次查询多少条
//                int count = 0;
//                if(proIds.size() < len){
//                    len = proIds.size();
//                    count = 1;
//                }else {
//                    count = size / len;
//                }
//                for (int s = 0; s < len; s++) {
//                    if (s == len -1) {
//                        List<Map> fformList = proIds.subList(s * count, size);
//                        xcPro.add(s, fformList);
//                    } else {
//                        List<Map> fformList = proIds.subList(s * count, (s + 1) * count);
//                        xcPro.add(s, fformList);
//                    }
//                }
//                //将客户查询处理成线程
//                ExecutorService executor1 = Executors.newFixedThreadPool(len);
//                List<Future<List<CustomerAddLogVo>>> results1 = new ArrayList<>();
//                try {
//                    for (int i = 0; i < len; i++) {
//                        int finalI = i;
//                        Callable<List<CustomerAddLogVo>> task = () -> {
//                            // 在每个线程中执行需要的查询操作
//                            return getAllCustomerAddLogListSqlQuery(paramMap,xcPro.get(finalI),searchType);
//                        };
//                        Future<List<CustomerAddLogVo>> result = executor1.submit(task);
//                        results1.add(result);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    executor1.shutdown(); // 关闭线程池
//                    try {
//                        if (!executor1.awaitTermination(60, TimeUnit.SECONDS)) {
//                            executor1.shutdownNow();
//                        }
//                    } catch (InterruptedException ex) {
//                        executor1.shutdownNow();
//                    }
//                }
//                // 结果合并
//                for (Future<List<CustomerAddLogVo>> result : results1) {
//                    try {
//                        customerAddLogList.addAll(result.get());
//                    } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            if(!CollectionUtils.isEmpty(customerAddLogList)){
                //获取客户集合ID
                List<String> ids = customerAddLogList.stream().map(CustomerAddLogVo::getOpportunityClueId).distinct().collect(Collectors.toList());
                //根据客户集合ID 查询对应的跟进交易记录
                customerFodLogList = dataStatisticDao.getAllCustomerFodLogList(ids);
//            List<List<String>> xcIds = new ArrayList<>();
//            int len2 = 10;
//            if (ids.size() > 0){
//                int size = ids.size();
//                //len2个线程每次查询多少条
//                int count = 0;
//                if(ids.size() < len2){
//                    len2 = ids.size();
//                    count = 1;
//                }else {
//                    count = size / len2;
//                }
//                for (int s = 0; s < len2; s++) {
//                    if (s == len2 - 1) {
//                        List<String> fformList = ids.subList(s * count, size);
//                        xcIds.add(s, fformList);
//                    } else {
//                        List<String> fformList = ids.subList(s * count, (s + 1) * count);
//                        xcIds.add(s, fformList);
//                    }
//                }
//                //将客户跟进交易记录查询处理成线程
//                ExecutorService executor2 = Executors.newFixedThreadPool(len2);
//                List<Future<List<CustomerFodLogVo>>> results2 = new ArrayList<>();
//                try {
//                    for (int i = 0; i < len2; i++) {
//                        int finalI = i;
//                        Callable<List<CustomerFodLogVo>> task = () -> {
//                            // 在每个线程中执行需要的查询操作
//                            return dataStatisticDao.getAllCustomerFodLogList(xcIds.get(finalI));
//                        };
//                        Future<List<CustomerFodLogVo>> result = executor2.submit(task);
//                        results2.add(result);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    executor2.shutdown(); // 关闭线程池
//                    try {
//                        if (!executor2.awaitTermination(60, TimeUnit.SECONDS)) {
//                            executor2.shutdownNow();
//                        }
//                    } catch (InterruptedException ex) {
//                        executor2.shutdownNow();
//                    }
//                }
//                // 结果合并
//                for (Future<List<CustomerFodLogVo>> result : results2) {
//                    try {
//                        customerFodLogList.addAll(result.get());
//                    } catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
                //将跟进交易记录按客户维度分组
                Map<String, List<CustomerFodLogVo>> recordMap = customerFodLogList.stream().collect(Collectors.groupingBy(CustomerFodLogVo::getOpportunityClueId));
                //循环客户集合 封装客户跟进交易数据
                List<CustomerFodLogVo> cc = new ArrayList<>();
                CustomerFodLogVo customerFodLogVo = new CustomerFodLogVo();
                customerFodLogVo.setBusinessId("visolink123");
                customerFodLogVo.setBusinessType("visolink123");
                customerFodLogVo.setBusinessTime(sf.format(DateUtil.date()));
                customerFodLogVo.setBusinessProjectId("visolink123");
                customerFodLogVo.setMainVisitProjectId("visolink123");
                customerFodLogVo.setIsThreeOnesStatus("visolink123");
                customerFodLogVo.setIsFirstComeVisitStatus("visolink123");
                customerFodLogVo.setIsFirstVisitStatus("visolink123");
                customerFodLogVo.setIsSignAfterVisitStatus("visolink123");
                customerFodLogVo.setIsStatistics("visolink123");
                cc.add(customerFodLogVo);
                customerAddLogList.stream().forEach(x->{
                    x.setCustomerFodLogList(recordMap.get(x.getOpportunityClueId()));
                    //塞一个不满足条件的空集合数据 防止无数据异常
                    if(CollectionUtils.isEmpty(x.getCustomerFodLogList())){
                        cc.get(0).setOpportunityClueId(x.getOpportunityClueId());
                        x.setCustomerFodLogList(cc);
                    }
                });
            }
        }
        List<ProConversionRate> reList = new ArrayList<>();
        if("1".equals(searchType)){//项目转化率统计
            if (CollectionUtils.isEmpty(customerAddLogList)){
                proIds.stream().forEach(x->{
                    ProConversionRate proConversionRate = new ProConversionRate();
                    proConversionRate.setName(String.valueOf(x.get("projectName")));
                    proConversionRate.setAddToComeVisitRate("0.00%");//新增转拜访率
                    proConversionRate.setAddToThreeOnesRate("0.00%");//新增转三个一率
                    proConversionRate.setAddToFollowVisitRate("0.00%");//新增转来访率
                    proConversionRate.setComeVisitToFollowVisitRate("0.00%");//拜访转来访率
                    proConversionRate.setThreeOnesToFollowVisitRate("0.00%");//三个一转来访率
                    proConversionRate.setFirstFollowVisitToReFollowVisitRate("0.00%");//首访转复访率
                    proConversionRate.setReFollowVisitToTradeRate("0.00%");//复访转成交率
                    proConversionRate.setFollowVisitToTradeRate("0.00%");//来访转成交率
                    proConversionRate.setReTradeRate("0.00%");//累计复购率
                    reList.add(proConversionRate);
                });
            }else {
                //将客户按照项目分组
                Map<String, List<CustomerAddLogVo>> oppMap = customerAddLogList.stream().collect(Collectors.groupingBy(CustomerAddLogVo :: getProjectId));
                //将项目分成多个线程执行 最后合并查询结果
                //int numThreads = proIds.size(); // 定义要启动的线程数
                int numThreads = 10;
                ExecutorService executor = Executors.newFixedThreadPool(numThreads);
                List<Future<ProConversionRate>> results = new ArrayList<>();
                try {
                    for (int i = 0; i < numThreads; i++) {
                        int finalI = i;
                        List<CustomerAddLogVo> finalCustomerAddLogList = customerAddLogList;
                        Callable<ProConversionRate> task = () -> {
                            // 在每个线程中执行需要的查询操作
                            return performQueryAndReturnResult(searchType,proIds.get(finalI),oppMap,paramMap, finalCustomerAddLogList);
                        };
                        Future<ProConversionRate> result = executor.submit(task);
                        results.add(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    executor.shutdown(); // 关闭线程池
                    try {
                        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException ex) {
                        executor.shutdownNow();
                    }
                }
                // 结果合并
                for (Future<ProConversionRate> result : results) {
                    try {
                        reList.add(result.get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if ("2".equals(searchType)){//区域转化率
            if (CollectionUtils.isEmpty(customerAddLogList)){
                proIds.stream().forEach(region->{
                    ProConversionRate proConversionRate = new ProConversionRate();
                    proConversionRate.setName(String.valueOf(region.get("areaName")));
                    proConversionRate.setAddToComeVisitRate("0.00%");//新增转拜访率
                    proConversionRate.setAddToThreeOnesRate("0.00%");//新增转三个一率
                    proConversionRate.setAddToFollowVisitRate("0.00%");//新增转来访率
                    proConversionRate.setComeVisitToFollowVisitRate("0.00%");//拜访转来访率
                    proConversionRate.setThreeOnesToFollowVisitRate("0.00%");//三个一转来访率
                    proConversionRate.setFirstFollowVisitToReFollowVisitRate("0.00%");//首访转复访率
                    proConversionRate.setReFollowVisitToTradeRate("0.00%");//复访转成交率
                    proConversionRate.setFollowVisitToTradeRate("0.00%");//来访转成交率
                    proConversionRate.setReTradeRate("0.00%");//累计复购率
                    reList.add(proConversionRate);
                });
            }else {
                //将客户按照项目分组
                Map<String, List<CustomerAddLogVo>> oppMap = customerAddLogList.stream().filter(a -> a.getAreaId() != null && a.getAreaId() != "").collect(Collectors.groupingBy(CustomerAddLogVo::getAreaId));
                //将区域分成多个线程执行 最后合并查询结果
                //int numThreads = proIds.size(); // 定义要启动的线程数
                int numThreads = 10; // 定义要启动的线程数
                ExecutorService executor = Executors.newFixedThreadPool(numThreads);
                List<Future<ProConversionRate>> results = new ArrayList<>();
                try {
                    for (int i = 0; i < numThreads; i++) {
                        int finalI = i;
                        List<CustomerAddLogVo> finalCustomerAddLogList1 = customerAddLogList;
                        Callable<ProConversionRate> task = () -> {
                            // 在每个线程中执行需要的查询操作
                            return performQueryAndReturnResult(searchType,proIds.get(finalI),oppMap,paramMap, finalCustomerAddLogList1);
                        };
                        Future<ProConversionRate> result = executor.submit(task);
                        results.add(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    executor.shutdown(); // 关闭线程池
                    try {
                        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException ex) {
                        executor.shutdownNow();
                    }
                }
                // 结果合并
                for (Future<ProConversionRate> result : results) {
                    try {
                        reList.add(result.get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if ("3".equals(searchType)){//集团转化率
            if (CollectionUtils.isEmpty(customerAddLogList)) {
                ProConversionRate proConversionRate = new ProConversionRate();
                proConversionRate.setName("万洋集团");
                proConversionRate.setAddToComeVisitRate("0.00%");//新增转拜访率
                proConversionRate.setAddToThreeOnesRate("0.00%");//新增转三个一率
                proConversionRate.setAddToFollowVisitRate("0.00%");//新增转来访率
                proConversionRate.setComeVisitToFollowVisitRate("0.00%");//拜访转来访率
                proConversionRate.setThreeOnesToFollowVisitRate("0.00%");//三个一转来访率
                proConversionRate.setFirstFollowVisitToReFollowVisitRate("0.00%");//首访转复访率
                proConversionRate.setReFollowVisitToTradeRate("0.00%");//复访转成交率
                proConversionRate.setFollowVisitToTradeRate("0.00%");//来访转成交率
                proConversionRate.setReTradeRate("0.00%");//累计复购率
                reList.add(proConversionRate);
            }else {
                //新增转拜访客户 新增的客户里（指数据统计里的新增客户数），有多少客户已经拜访过了； 新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转拜访客户 日志表左连接客户跟进交易记录表 增加筛选项 跟进方式为上门拜访 在对满足条件的客户ID 去重
                List<CustomerAddLogVo> addCount = customerAddLogList.stream().filter(a -> a.getIsAdd().equals("1") &&
                        a.getReportCreateTime().compareTo(paramMap.get("startTime")+"") >= 0 && a.getReportCreateTime().compareTo(paramMap.get("endTime")+"") <= 0
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> addToCVisitCount = new ArrayList<>();
                if (addCount.size()>0){
                    addToCVisitCount = customerAddLogList.stream().filter(a-> addCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                                    b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //新增转三个一率 新增的客户里（指数据统计里的新增客户数），有多少客户已经达成了实际三个一； 新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转三个一客户 查询客户报备日志表 查询创建方式为手动录入且是否完成三个一为是的客户 针对客户ID 去重
                List<CustomerAddLogVo> addToThreeOnesCount = new ArrayList<>();
                if (addCount.size()>0){
                    addToThreeOnesCount = customerAddLogList.stream().filter(a-> addCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> Optional.ofNullable(b.getIsThreeOnesStatus()).map(date -> date.equals("1")).orElse(false)
                                    && b.getIsStatistics().equals("1") && b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //新增转来访客户 新增的客户里（指数据统计里的新增客户数），有多少客户已经来访过了；新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转来访客户 左连接客户跟进交易记录表 查询业务类型为邀约到访和自然来访的记录 针对客户ID 去重 联动招商来访客户为满足上述来访条件 但是项目和主项目不同的客户
                //正常新增到访客户
                List<CustomerAddLogVo> addToVisitCount = new ArrayList<>();
                if (addCount.size()>0){
                    addToVisitCount = customerAddLogList.stream().filter(a-> addCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsStatistics().equals("1") &&
                                    b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //拜访转首访率 已拜访过的客户里，有多少客户完成了首访；拜访客户 查询客户报备日志表左连接客户跟进交易记录表 查询业务类型为上门拜访的客户 针对客户ID去重 拜访转来访查询业务类型同时包含上门拜访和（y邀约拜访 自然来访）的客户 针对客户ID去重
                List<CustomerAddLogVo> cVisitCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> cVisitTofVisitCount = new ArrayList<>();
                if (cVisitCount.size()>0){
                    cVisitTofVisitCount = customerAddLogList.stream().filter(a ->cVisitCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                    b.getBusinessTime().compareTo(String.valueOf(cVisitCount.stream().filter(c ->
                                            c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c ->
                                            c.getCustomerFodLogList().stream().filter(d ->
                                                    d.getBusinessType().equals("2") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())
                                    ) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //三个一转首访率 实际达成三个一的客户里，有多少客户完成了首访； 三个一客户查询客户报备记录表 是否三个一客户是是的客户 针对客户ID去重 三个一转来访客户 左连接客户跟进交易记录表 查询 日志表是否三个一客户是是的客户且跟进交易记录表业务类型为邀约到访和自然来访的记录 针对客户ID 去重
                List<CustomerAddLogVo> threeOnesCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> Optional.ofNullable(b.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false) &&
                        Optional.ofNullable(b.getBusinessTime()).map(date -> date.compareTo(paramMap.get("startTime") + "") >= 0 && date.compareTo(paramMap.get("endTime") + "") <= 0).orElse(false))
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> threeOnesTofVisitCount = new ArrayList<>();
                if (threeOnesCount.size()>0){
                    threeOnesTofVisitCount = customerAddLogList.stream().filter(a ->threeOnesCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                    b.getBusinessTime().compareTo(String.valueOf(threeOnesCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> Optional.ofNullable(d.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false)).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //首访转复访率 完成过首访的客户里，有多少客户完成了第二及以上次来访；首访客户 查询客户报备日志表左连接客户跟进交易记录表 查询业务类型为到访 且是否首访状态为是是的客户 针对客户ID 去重 首访转复访查询业务类型为到访 且是否首访状态为否的客户 针对客户ID 去重
                List<CustomerAddLogVo> firstVisitCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                        && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("1") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> firstVisitToReVisitCount = new ArrayList<>();
                if (firstVisitCount.size()>0){
                    firstVisitToReVisitCount = customerAddLogList.stream().filter(a -> firstVisitCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                                    && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("0") &&
                                    b.getBusinessTime().compareTo(String.valueOf(firstVisitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsFirstVisitStatus().equals("1") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //复访转成交率 完成两次及以上来访的客户里，有多少客户已经成交； 复访客户查询业务类型为到访 且是否首访状态为否的客户 针对客户ID 去重 复访转成交 查询业务类型为到访 且是否首访状态为否的客户 客户是否成交为是的客户 针对客户ID 去重
                List<CustomerAddLogVo> reVisitCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                        && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("0") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> reVisitToTradeCount = new ArrayList<>();
                if (reVisitCount.size()>0){
                    reVisitToTradeCount = customerAddLogList.stream().filter(a -> reVisitCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                                    b.getBusinessTime().compareTo(String.valueOf(reVisitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsFirstVisitStatus().equals("0") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //来访转成交率 来访过的客户里，有多少客户已经成交；来访客户查询业务类型为到访的客户 针对客户ID 去重 来访转成交 查询业务类型为到访 且客户是否成交为是的客户 针对客户ID 去重
                List<CustomerAddLogVo> visitCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsStatistics().equals("1") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> visitToTradeCount = new ArrayList<>();
                if (visitCount.size()>0) {
                    visitToTradeCount = customerAddLogList.stream().filter(a -> visitCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                                    b.getBusinessTime().compareTo(String.valueOf(visitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //累计复购率 已经成交的厂房客户里，有多少客户重复购买； 查询客户报备记录表是否签约状态为是的客户 复购客户查询是否复购为是的客户
                List<CustomerAddLogVo> tradeCount = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                ).collect(Collectors.toList());
                List<CustomerAddLogVo> tradeToReCount = new ArrayList<>();
                if (tradeCount.size() > 0) {
                    tradeToReCount = customerAddLogList.stream().filter(a -> tradeCount.contains(a) &&
                            a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") && b.getBusinessTime().compareTo(
                                    String.valueOf(LocalDateTime.parse(String.valueOf(tradeCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> d.getBusinessType().equals("6")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get()), dtf).plusDays(2))
                            ) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                    ).collect(Collectors.toList());
                }
                //针对查询结果的客户ID 去重
                //新增
                int xzCount = addCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double xzSum = Double.valueOf(xzCount);
                //新增转拜访率
                int xzTcvCount = addToCVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double xzTcvSum = Double.valueOf(xzTcvCount);
                Double xzTcvRate;
                if (0 == xzSum) {
                    xzTcvRate = 0.0;
                }else {
                    xzTcvRate = xzTcvSum/xzSum*100;
                }
                //新增转三个一率
                int xzTtoCount = addToThreeOnesCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double xzTtoSum = Double.valueOf(xzTtoCount);
                Double xzTtoRate;
                if (0 == xzSum) {
                    xzTtoRate = 0.0;
                }else {
                    xzTtoRate = xzTtoSum/xzSum*100;
                }
                //新增转来访率
                int xzTvCount = addToVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double xzTvSum = Double.valueOf(xzTvCount);
                Double xzTvRate;
                if (0 == xzSum) {
                    xzTvRate = 0.0;
                }else {
                    xzTvRate = xzTvSum/xzSum*100;
                }
                //拜访
                int cvCount = cVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double cvSum = Double.valueOf(cvCount);
                //拜访转来访率
                int cvTvCount = cVisitTofVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double cvTvSum = Double.valueOf(cvTvCount);
                Double cvTvRate;
                if (0 == cvSum) {
                    cvTvRate = 0.0;
                }else {
                    cvTvRate = cvTvSum/cvSum*100;
                }
                //三个一
                int toCount = threeOnesCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double toSum = Double.valueOf(toCount);
                //三个一转来访率
                int toTvCount = threeOnesTofVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double toTvSum = Double.valueOf(toTvCount);
                Double toTvRate;
                if (0 == toSum) {
                    toTvRate = 0.0;
                }else {
                    toTvRate = toTvSum/toSum*100;
                }
                //首访
                int fvCount = firstVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double fvSum = Double.valueOf(fvCount);
                //首访转复访率
                int fvTrvCount = firstVisitToReVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double fvTrvSum = Double.valueOf(fvTrvCount);
                Double fvTrvRate;
                if (0 == fvSum) {
                    fvTrvRate = 0.0;
                }else {
                    fvTrvRate = fvTrvSum/fvSum*100;
                }
                //复访
                int rvCount = reVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double rvSum = Double.valueOf(rvCount);
                //复访转成交率
                int rvTtdCount = reVisitToTradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double rvTtdSum = Double.valueOf(rvTtdCount);
                Double rvTtdRate;
                if (0 == rvSum) {
                    rvTtdRate = 0.0;
                }else {
                    rvTtdRate = rvTtdSum/rvSum*100;
                }
                //来访
                int vCount = visitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                //来访转成交率
                int vTtdCount = visitToTradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double vTtdSum = Double.valueOf(vTtdCount);
                Double vTtdRate;
                if (0 == vCount) {
                    vTtdRate = 0.0;
                }else {
                    vTtdRate = vTtdSum/vCount*100;
                }
                //累计复购率
                int tdCount = tradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double tdSum = Double.valueOf(tdCount);
                int reTtdCount = tradeToReCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                Double reTtdSum = Double.valueOf(reTtdCount);
                Double reTtdRate;
                if (0 == tdSum) {
                    reTtdRate = 0.0;
                }else {
                    reTtdRate = reTtdSum/tdSum*100;
                }
                ProConversionRate proConversionRate = new ProConversionRate();
                proConversionRate.setName("万洋集团");
                proConversionRate.setAddToComeVisitRate(df.format(xzTcvRate)+"%");//新增转拜访率
                proConversionRate.setAddToThreeOnesRate(df.format(xzTtoRate)+"%");//新增转三个一率
                proConversionRate.setAddToFollowVisitRate(df.format(xzTvRate)+"%");//新增转来访率
                proConversionRate.setComeVisitToFollowVisitRate(df.format(cvTvRate)+"%");//拜访转来访率
                proConversionRate.setThreeOnesToFollowVisitRate(df.format(toTvRate)+"%");//三个一转来访率
                proConversionRate.setFirstFollowVisitToReFollowVisitRate(df.format(fvTrvRate)+"%");//首访转复访率
                proConversionRate.setReFollowVisitToTradeRate(df.format(rvTtdRate)+"%");//复访转成交率
                proConversionRate.setFollowVisitToTradeRate(df.format(vTtdRate)+"%");//来访转成交率
                proConversionRate.setReTradeRate(df.format(reTtdRate)+"%");//累计复购率
                reList.add(proConversionRate);
            }
        }

        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if ("1".equals(searchType)){
            Map proMap = excelImportMapper.getAreaNameAndProNames(regionList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }else if ("2".equals(searchType)){
            Map proMap = excelImportMapper.getAreaNameAndProNames(projectMapper.getAllProInsRegion(regionList));
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }else if ("3".equals(searchType)){
            excelExportLog.setAreaName("万洋集团");
            excelExportLog.setProjectId("670869647114347");
            excelExportLog.setProjectName("万洋集团");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);

        if (!CollectionUtils.isEmpty(reList)){
//            headers = proDataStatistics.get(0).cardTitle1;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (ProConversionRate model : reList) {
                model.setRownum(rowNum+"");
                Object[] oArray = model.toData2(filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void proConversionRateStatisticsExportNewPL(HttpServletRequest request, HttpServletResponse response, String excelForm) {

        Map paramMap = JSONObject.parseObject(excelForm, Map.class);

        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        String searchType = paramMap.get("searchType")+"";


        //导出的文档下面的名字
        String excelName = "项目转化率统计";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        List<Map> proIds;
        List<String> regionList = new ArrayList<>();
        List<CustomerAddLogVo> customerAddLogList;
        List<CustomerFodLogVo> customerFodLogList = new ArrayList<>();
        if("1".equals(searchType)) {//项目转化率统计
            if (paramMap.get("projectIds") != null && !"".equals(paramMap.get("projectIds") + "")) {
                String regionIds = paramMap.get("projectIds") + "";
                regionList = Arrays.asList(regionIds.split(","));
            }
            excelName = "项目转化率统计";

        }else if("2".equals(searchType)) {//区域转化率统计
            if (paramMap.get("regionIds")!=null && !"".equals(paramMap.get("regionIds")+"")){
                String regionIds = paramMap.get("regionIds")+"";
                regionList = Arrays.asList(regionIds.split(","));
            }
            excelName = "区域转化率统计";

        }else {
            if (paramMap.get("projectIds") != null && !"".equals(paramMap.get("projectIds") + "")) {
                String regionIds = paramMap.get("projectIds") + "";
                regionList = Arrays.asList(regionIds.split(","));
            }
                excelName = "集团转化率统计";
        }


        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        if ("1".equals(searchType)){
            Map proMap = excelImportMapper.getAreaNameAndProNames(regionList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }else if ("2".equals(searchType)){
            Map proMap = excelImportMapper.getAreaNameAndProNames(projectMapper.getAllProInsRegion(regionList));
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }else if ("3".equals(searchType)){
            excelExportLog.setAreaName("万洋集团");
            excelExportLog.setProjectId("670869647114347");
            excelExportLog.setProjectName("万洋集团");
        }
        excelExportLog.setCreator(userId);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);

        paramMap.put("pagination","all");
        List<ProConversionRate> reList = new ArrayList<>();
        if ("3".equals(searchType)){
            //集团导出
        ResultBody resultBody = this.proConversionRateStatisticsNewPL(paramMap);
        Map resMap = (Map) resultBody.getData();
         reList = (List<ProConversionRate>) resMap.get("list");

        }else{
            //调用转化率查询获取要导出的数据(区域和项目导出)
            reList = batchQueryProConversionRate(paramMap);
        }


        if (!CollectionUtils.isEmpty(reList)){
//            headers = proDataStatistics.get(0).cardTitle1;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (ProConversionRate model : reList) {
                model.setRownum(rowNum+"");
                Object[] oArray = model.toData2(filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // 分批查询并汇总结果的方法
    public List<ProConversionRate> batchQueryProConversionRate(Map<String, Object> paramMap) {
        // 1. 初始化全局汇总列表（存储所有批次的结果）
        List<ProConversionRate> totalReList = new ArrayList<>();

        // 2. 从paramMap中获取projectIds字符串，并拆分为List<String>
        String projectIdsStr = (String) paramMap.get("projectIds");
        String regionIdsStr = (String) paramMap.get("regionIds");

        if("1".equals(paramMap.get("searchType"))){


            if (projectIdsStr == null || projectIdsStr.trim().isEmpty()) {
                return totalReList;
            }
            // 处理项目ID列表（去除空格并拆分）
            List<String> proIds = Arrays.asList(projectIdsStr.replaceAll("\\s+", "").split(","));
            if (proIds.isEmpty() || (proIds.size() == 1 && proIds.get(0).isEmpty())) {
                return Collections.emptyList();
            }

            int totalProjects = proIds.size();
            // 3. 定义每个线程最多处理50个项目，计算所需线程数
            int maxProjectsPerThread = 50;
            int threadCount = (totalProjects + maxProjectsPerThread - 1) / maxProjectsPerThread; // 向上取整
            System.out.println("总项目数: " + totalProjects + ", 线程数: " + threadCount);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<List<ProConversionRate>>> results = new ArrayList<>();

            try {
                // 4. 为每个线程分配项目
                int currentIndex = 0;
                for (int i = 0; i < threadCount; i++) {
                    // 计算当前线程应处理的项目数量（最后一个线程可能少于50个）
                    int remaining = totalProjects - currentIndex;
                    int projectsForThread = Math.min(remaining, maxProjectsPerThread);

                    // 截取当前线程负责的项目子集
                    int endIndex = currentIndex + projectsForThread;
                    List<String> subProIds = proIds.subList(currentIndex, endIndex);
                    currentIndex = endIndex;

                    // 创建参数副本，避免线程安全问题
                    Map<String, Object> threadParamMap = new HashMap<>(paramMap);

                    // 提交任务到线程池
                    Callable<List<ProConversionRate>> task = () ->
                            processSingleThreadBatch(subProIds, threadParamMap);

                    Future<List<ProConversionRate>> future = executor.submit(task);
                    results.add(future);
                }

                // 5. 收集所有线程的结果
                for (Future<List<ProConversionRate>> future : results) {
                    try {
                        List<ProConversionRate> threadResult = future.get();
                        if (threadResult != null && !threadResult.isEmpty()) {
                            totalReList.addAll(threadResult);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("处理线程结果时发生错误: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 6. 优雅关闭线程池
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }else{


            if (regionIdsStr == null || regionIdsStr.trim().isEmpty()) {
                return totalReList;
            }
            // 处理项目ID列表（去除空格并拆分）
            List<String> proIds = Arrays.asList(regionIdsStr.replaceAll("\\s+", "").split(","));
            if (proIds.isEmpty() || (proIds.size() == 1 && proIds.get(0).isEmpty())) {
                return Collections.emptyList();
            }

            int totalProjects = proIds.size();
            // 3. 定义每个线程最多处理50个项目，计算所需线程数
            int maxProjectsPerThread = 2;
            int threadCount = (totalProjects + maxProjectsPerThread - 1) / maxProjectsPerThread; // 向上取整
            System.out.println("总区域数: " + totalProjects + ", 线程数: " + threadCount);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<List<ProConversionRate>>> results = new ArrayList<>();

            try {
                // 4. 为每个线程分配项目
                int currentIndex = 0;
                for (int i = 0; i < threadCount; i++) {
                    // 计算当前线程应处理的项目数量（最后一个线程可能少于50个）
                    int remaining = totalProjects - currentIndex;
                    int projectsForThread = Math.min(remaining, maxProjectsPerThread);

                    // 截取当前线程负责的项目子集
                    int endIndex = currentIndex + projectsForThread;
                    List<String> subProIds = proIds.subList(currentIndex, endIndex);
                    currentIndex = endIndex;

                    // 创建参数副本，避免线程安全问题
                    Map<String, Object> threadParamMap = new HashMap<>(paramMap);

                    // 提交任务到线程池
                    Callable<List<ProConversionRate>> task = () ->
                            processSingleThreadBatch(subProIds, threadParamMap);

                    Future<List<ProConversionRate>> future = executor.submit(task);
                    results.add(future);
                }

                // 5. 收集所有线程的结果
                for (Future<List<ProConversionRate>> future : results) {
                    try {
                        List<ProConversionRate> threadResult = future.get();
                        if (threadResult != null && !threadResult.isEmpty()) {
                            totalReList.addAll(threadResult);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("处理线程结果时发生错误: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 6. 优雅关闭线程池
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }


        return totalReList;
    }
    private List<ProConversionRate> processSingleThreadBatch(List<String> subProIds, Map<String, Object> paramMap) {
        List<ProConversionRate> batchResult = new ArrayList<>();

        try {
            // 将当前线程负责的项目ID拼接为字符串
            String currentProjectIdsStr = String.join(",", subProIds);
            if("1".equals(paramMap.get("searchType"))){
                paramMap.put("projectIds", currentProjectIdsStr);
            }else{
                paramMap.put("regionIds", currentProjectIdsStr);
            }


            // 调用查询方法
            ResultBody resultBody = this.proConversionRateStatisticsNewPL(paramMap);

            // 解析结果
            if (resultBody != null && resultBody.getData() != null) {
                Map<?, ?> resMap = (Map<?, ?>) resultBody.getData();
                List<ProConversionRate> currentReList = (List<ProConversionRate>) resMap.get("list");

                if (currentReList != null && !currentReList.isEmpty()) {
                    batchResult.addAll(currentReList);
                }
            }
        } catch (Exception e) {
            System.err.println("线程处理区域失败，项目数量: " + subProIds.size() + ", 区域IDs: " + String.join(",", subProIds));
            e.printStackTrace();
        }

        return batchResult;
    }

    // 工具方法：将批次ID列表拼接为逗号分隔字符串
    private String joinBatchIds(List<String> batchIds) {
        StringJoiner joiner = new StringJoiner(",");
        for (String id : batchIds) {
            joiner.add(id);
        }
        return joiner.toString();
    }
    private List<ProConversionRate> performQueryAndReturnResultPL(String searchType,List<Map> prMap, NumeratorAndDenominator numeratorAndDenominator){
        DecimalFormat df = new DecimalFormat("0.00");
        if("1".equals(searchType)) {
            List<ProConversionRate> proConversionRates = new ArrayList<>();
            prMap.stream().forEach(pro->{
                //项目
                String projectId = String.valueOf(pro.get("projectId"));
                String projectName = String.valueOf(pro.get("projectName"));
                // 3. 优化：numeratorAndDenominator 子属性非空链判断（父对象为null时，子列表默认为空集合）
                // 新增分母：getCustomerAddLogList() 为 null 时，get(x) 也为 null，最终转空集合
                List<Map> customerAddLogList = Optional.ofNullable(numeratorAndDenominator.getCustomerAddLogList())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());

                // -------------------------- 1. 新增转拜访率 --------------------------
                List<Map> addVisitList = Optional.ofNullable(numeratorAndDenominator.getAddVisit())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                // 调用前非空校验：空集合直接返回 0，无需进入子方法
                long addVisitingMolecules = calculateMatchCount(customerAddLogList, addVisitList);

                // -------------------------- 2. 新增转三个一率 --------------------------
                List<Map> addThreeOneList = Optional.ofNullable(numeratorAndDenominator.getAddThreeOne())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long addThreeOneMolecules = calculateMatchCount(customerAddLogList, addThreeOneList);

                // -------------------------- 3. 新增转来访率 --------------------------
                List<Map> addDaoFangList = Optional.ofNullable(numeratorAndDenominator.getAddDaoFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long addDaoFangMolecules = calculateMatchCount(customerAddLogList, addDaoFangList);
                long addDaoFangMoleculesDifference = calculateMissingCustomerCountWithStream(customerAddLogList, addDaoFangList);

                // -------------------------- 4. 拜访转首访率 --------------------------
                List<Map> allCustomervisitList = Optional.ofNullable(numeratorAndDenominator.getAllCustomervisit())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> visitShouFangList = Optional.ofNullable(numeratorAndDenominator.getVisitShouFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long visitToShouFangMolecules = calculateMatchCount(allCustomervisitList, visitShouFangList);
                long visitToShouFangMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomervisitList, visitShouFangList);

                // -------------------------- 5. 三个一转首访率 --------------------------
                List<Map> allCustomerThreeOneList = Optional.ofNullable(numeratorAndDenominator.getAllCustomerThreeOne())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> threeOneShoufangList = Optional.ofNullable(numeratorAndDenominator.getThreeOneShoufang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long threeOneToShouFangMolecules = calculateMatchCount(allCustomerThreeOneList, threeOneShoufangList);
                long threeOneToShouFangMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomerThreeOneList, threeOneShoufangList);

                // -------------------------- 6. 来访转成交率 --------------------------
                List<Map> allCustomerDaoFangList = Optional.ofNullable(numeratorAndDenominator.getAllCustomerDaoFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> daoFangChengjiaoList = Optional.ofNullable(numeratorAndDenominator.getDaoFangChengjiao())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long daoFangToChengjiaoMolecules = calculateMatchCount(allCustomerDaoFangList, daoFangChengjiaoList);
                long daoFangToChengjiaoMoleculesDifference = 0;
//                long daoFangToChengjiaoMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomerDaoFangList, daoFangChengjiaoList);
                // -------------------------- 7. 首访转复访率 --------------------------
                List<Map> allCustomershouFangList = Optional.ofNullable(numeratorAndDenominator.getAllCustomershouFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> shoufangFufangList = Optional.ofNullable(numeratorAndDenominator.getShoufangFufang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long shouFangToFuFangMolecules = calculateMatchCount(allCustomershouFangList, shoufangFufangList);
                long shouFangToFuFangMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomershouFangList, shoufangFufangList);

                // -------------------------- 8. 复访转成交率 --------------------------
                List<Map> allCustomerfuFangList = Optional.ofNullable(numeratorAndDenominator.getAllCustomerfuFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> fufangChengjiaoList = Optional.ofNullable(numeratorAndDenominator.getFufangChengjiao())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long fuFangToChengjiaoMolecules = calculateMatchCount(allCustomerfuFangList, fufangChengjiaoList);
                long fuFangToChengjiaoMoleculesDifference = 0;
//                long fuFangToChengjiaoMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomerfuFangList, fufangChengjiaoList);
                // -------------------------- 9. 成交转复购率（解决空列表问题） --------------------------
                List<Map> allCustomerchengjiaoList = Optional.ofNullable(numeratorAndDenominator.getAllCustomerchengjiao())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> chengjiaoFugouList = Optional.ofNullable(numeratorAndDenominator.getChengjiaoFugou())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                // 关键：即使 chengjiaoFugouList 为空，也会安全处理（返回 0）
                long chengJiaoToFuGouMolecules = calculateMatchCount(allCustomerchengjiaoList, chengjiaoFugouList);

                // -------------------------- 转化率计算（逻辑不变，保留原逻辑） --------------------------
                int xzCount = customerAddLogList.size();
                Double xzSum = Double.valueOf(xzCount);

                int cvCount = allCustomervisitList.size();
                Double cvSum = Double.valueOf(cvCount);

                int toCount = allCustomerThreeOneList.size();
                Double toSum = Double.valueOf(toCount);

                int vCount = allCustomerDaoFangList.size();
                Double vSum = Double.valueOf(vCount);

                int fvCount = allCustomershouFangList.size();
                Double fvSum = Double.valueOf(fvCount);

                int rvCount = allCustomerfuFangList.size();
                Double rvSum = Double.valueOf(rvCount);

                int tdCount = allCustomerchengjiaoList.size();
                Double tdSum = Double.valueOf(tdCount);

                // 1. 新增转拜访率
                Double addVisitingSum = Double.valueOf(addVisitingMolecules);
                Double xzTcvRate = (0 == xzSum) ? 0.0 : (addVisitingSum / xzSum) * 100;

                // 2. 新增转三个一率
                Double addThreeOneSum = Double.valueOf(addThreeOneMolecules);
                Double xzTtoRate = (0 == xzSum) ? 0.0 : (addThreeOneSum / xzSum) * 100;

                // 3. 新增转来访率
                Double addDaoFangSum = Double.valueOf(addDaoFangMolecules);
                Double addDaoFangDifferenceSum = Double.valueOf(addDaoFangMoleculesDifference);
                Double xzTvRate = (0 == (xzSum + addDaoFangDifferenceSum)) ? 0.0 :
                        ((addDaoFangSum + addDaoFangDifferenceSum) / (xzSum + addDaoFangDifferenceSum)) * 100;

                // 4. 拜访转来访率
                Double visitToShouFangSum = Double.valueOf(visitToShouFangMolecules);
                Double visitToShouFangDifference = Double.valueOf(visitToShouFangMoleculesDifference);
                Double cvTvRate = (0 == (cvSum + visitToShouFangDifference)) ? 0.0 :
                        ((visitToShouFangSum + visitToShouFangDifference) / (cvSum + visitToShouFangDifference)) * 100;

                // 5. 三个一转来访率
                Double threeOneToShouFangSum = Double.valueOf(threeOneToShouFangMolecules);
                Double threeOneToShouFangDifference = Double.valueOf(threeOneToShouFangMoleculesDifference);
                Double toTvRate = (0 == (toSum + threeOneToShouFangDifference)) ? 0.0 :
                        ((threeOneToShouFangSum + threeOneToShouFangDifference) / (toSum + threeOneToShouFangDifference)) * 100;

                // 6. 首访转复访率
                Double shouFangToFuFangSum = Double.valueOf(shouFangToFuFangMolecules);
                Double shouFangToFuFangDifference = Double.valueOf(shouFangToFuFangMoleculesDifference);
                Double fvTrvRate = (0 == (fvSum + shouFangToFuFangDifference)) ? 0.0 :
                        ((shouFangToFuFangSum + shouFangToFuFangDifference) / (fvSum + shouFangToFuFangDifference)) * 100;

                // 7. 复访转成交率
                Double fuFangToChengjiaoSum = Double.valueOf(fuFangToChengjiaoMolecules);
                Double fuFangToChengjiaoDifferenceSum = Double.valueOf(fuFangToChengjiaoMoleculesDifference);
                Double rvTtdRate = (0 == (rvSum + fuFangToChengjiaoDifferenceSum)) ? 0.0 :
                        ((fuFangToChengjiaoSum + fuFangToChengjiaoDifferenceSum) / (rvSum + fuFangToChengjiaoDifferenceSum)) * 100;

                // 8. 来访转成交率
                Double daoFangToChengjiaoSum = Double.valueOf(daoFangToChengjiaoMolecules);
                Double daoFangToChengjiaoDifferenceSum = Double.valueOf(daoFangToChengjiaoMoleculesDifference);
                Double vTtdRate = (0 == (vSum + daoFangToChengjiaoDifferenceSum)) ? 0.0 :
                        ((daoFangToChengjiaoSum + daoFangToChengjiaoDifferenceSum) / (vSum + daoFangToChengjiaoDifferenceSum)) * 100;

                // 9. 累计复购率
                Double chengJiaoToFuGouSum = Double.valueOf(chengJiaoToFuGouMolecules);
                Double reTtdRate = (0 == tdSum) ? 0.0 : (chengJiaoToFuGouSum / tdSum) * 100;

                // -------------------------- 封装结果 --------------------------
                ProConversionRate proConversionRate = new ProConversionRate();
                proConversionRate.setName(projectName);
                proConversionRate.setAddToComeVisitRate(df.format(xzTcvRate) + "%");
                proConversionRate.setAddToThreeOnesRate(df.format(xzTtoRate) + "%");
                proConversionRate.setAddToFollowVisitRate(df.format(xzTvRate) + "%");
                proConversionRate.setComeVisitToFollowVisitRate(df.format(cvTvRate) + "%");
                proConversionRate.setThreeOnesToFollowVisitRate(df.format(toTvRate) + "%");
                proConversionRate.setFirstFollowVisitToReFollowVisitRate(df.format(fvTrvRate) + "%");
                proConversionRate.setReFollowVisitToTradeRate(df.format(rvTtdRate) + "%");
                proConversionRate.setFollowVisitToTradeRate(df.format(vTtdRate) + "%");
                proConversionRate.setReTradeRate(df.format(reTtdRate) + "%");

                proConversionRates.add(proConversionRate);
            });
            return proConversionRates; // 返回封装好的转化率结果（与老代码返回类型一致）
        }else{
            List<ProConversionRate> proConversionRates = new ArrayList<>();
            prMap.stream().forEach(pro->{
                //项目
                String projectId = String.valueOf(pro.get("areaId"));
                String areaName = String.valueOf(pro.get("areaName"));
                // 3. 优化：numeratorAndDenominator 子属性非空链判断（父对象为null时，子列表默认为空集合）
                // 新增分母：getCustomerAddLogList() 为 null 时，get(x) 也为 null，最终转空集合
                List<Map> customerAddLogList = Optional.ofNullable(numeratorAndDenominator.getCustomerAddLogList())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());

                // -------------------------- 1. 新增转拜访率 --------------------------
                List<Map> addVisitList = Optional.ofNullable(numeratorAndDenominator.getAddVisit())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                // 调用前非空校验：空集合直接返回 0，无需进入子方法
                long addVisitingMolecules = calculateMatchCount(customerAddLogList, addVisitList);

                // -------------------------- 2. 新增转三个一率 --------------------------
                List<Map> addThreeOneList = Optional.ofNullable(numeratorAndDenominator.getAddThreeOne())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long addThreeOneMolecules = calculateMatchCount(customerAddLogList, addThreeOneList);

                // -------------------------- 3. 新增转来访率 --------------------------
                List<Map> addDaoFangList = Optional.ofNullable(numeratorAndDenominator.getAddDaoFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long addDaoFangMolecules = calculateMatchCount(customerAddLogList, addDaoFangList);
                long addDaoFangMoleculesDifference = calculateMissingCustomerCountWithStream(customerAddLogList, addDaoFangList);

                // -------------------------- 4. 拜访转首访率 --------------------------
                List<Map> allCustomervisitList = Optional.ofNullable(numeratorAndDenominator.getAllCustomervisit())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> visitShouFangList = Optional.ofNullable(numeratorAndDenominator.getVisitShouFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long visitToShouFangMolecules = calculateMatchCount(allCustomervisitList, visitShouFangList);
                long visitToShouFangMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomervisitList, visitShouFangList);

                // -------------------------- 5. 三个一转首访率 --------------------------
                List<Map> allCustomerThreeOneList = Optional.ofNullable(numeratorAndDenominator.getAllCustomerThreeOne())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> threeOneShoufangList = Optional.ofNullable(numeratorAndDenominator.getThreeOneShoufang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long threeOneToShouFangMolecules = calculateMatchCount(allCustomerThreeOneList, threeOneShoufangList);
                long threeOneToShouFangMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomerThreeOneList, threeOneShoufangList);

                // -------------------------- 6. 来访转成交率 --------------------------
                List<Map> allCustomerDaoFangList = Optional.ofNullable(numeratorAndDenominator.getAllCustomerDaoFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> daoFangChengjiaoList = Optional.ofNullable(numeratorAndDenominator.getDaoFangChengjiao())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long daoFangToChengjiaoMolecules = calculateMatchCount(allCustomerDaoFangList, daoFangChengjiaoList);
                long daoFangToChengjiaoMoleculesDifference = 0;
//                long daoFangToChengjiaoMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomerDaoFangList, daoFangChengjiaoList);

                // -------------------------- 7. 首访转复访率 --------------------------
                List<Map> allCustomershouFangList = Optional.ofNullable(numeratorAndDenominator.getAllCustomershouFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> shoufangFufangList = Optional.ofNullable(numeratorAndDenominator.getShoufangFufang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long shouFangToFuFangMolecules = calculateMatchCount(allCustomershouFangList, shoufangFufangList);
                long shouFangToFuFangMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomershouFangList, shoufangFufangList);

                // -------------------------- 8. 复访转成交率 --------------------------
                List<Map> allCustomerfuFangList = Optional.ofNullable(numeratorAndDenominator.getAllCustomerfuFang())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> fufangChengjiaoList = Optional.ofNullable(numeratorAndDenominator.getFufangChengjiao())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                long fuFangToChengjiaoMolecules = calculateMatchCount(allCustomerfuFangList, fufangChengjiaoList);
                long fuFangToChengjiaoMoleculesDifference = 0;
//                long fuFangToChengjiaoMoleculesDifference = calculateMissingCustomerCountWithStream(allCustomerfuFangList, fufangChengjiaoList);

                // -------------------------- 9. 成交转复购率（解决空列表问题） --------------------------
                List<Map> allCustomerchengjiaoList = Optional.ofNullable(numeratorAndDenominator.getAllCustomerchengjiao())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                List<Map> chengjiaoFugouList = Optional.ofNullable(numeratorAndDenominator.getChengjiaoFugou())
                        .map(map -> map.get(projectId))
                        .orElse(new ArrayList<>());
                // 关键：即使 chengjiaoFugouList 为空，也会安全处理（返回 0）
                long chengJiaoToFuGouMolecules = calculateMatchCount(allCustomerchengjiaoList, chengjiaoFugouList);

                // -------------------------- 转化率计算（逻辑不变，保留原逻辑） --------------------------
                int xzCount = customerAddLogList.size();
                Double xzSum = Double.valueOf(xzCount);

                int cvCount = allCustomervisitList.size();
                Double cvSum = Double.valueOf(cvCount);

                int toCount = allCustomerThreeOneList.size();
                Double toSum = Double.valueOf(toCount);

                int vCount = allCustomerDaoFangList.size();
                Double vSum = Double.valueOf(vCount);

                int fvCount = allCustomershouFangList.size();
                Double fvSum = Double.valueOf(fvCount);

                int rvCount = allCustomerfuFangList.size();
                Double rvSum = Double.valueOf(rvCount);

                int tdCount = allCustomerchengjiaoList.size();
                Double tdSum = Double.valueOf(tdCount);

                // 1. 新增转拜访率
                Double addVisitingSum = Double.valueOf(addVisitingMolecules);
                Double xzTcvRate = (0 == xzSum) ? 0.0 : (addVisitingSum / xzSum) * 100;

                // 2. 新增转三个一率
                Double addThreeOneSum = Double.valueOf(addThreeOneMolecules);
                Double xzTtoRate = (0 == xzSum) ? 0.0 : (addThreeOneSum / xzSum) * 100;

                // 3. 新增转来访率
                Double addDaoFangSum = Double.valueOf(addDaoFangMolecules);
                Double addDaoFangDifferenceSum = Double.valueOf(addDaoFangMoleculesDifference);
                Double xzTvRate = (0 == (xzSum + addDaoFangDifferenceSum)) ? 0.0 :
                        ((addDaoFangSum + addDaoFangDifferenceSum) / (xzSum + addDaoFangDifferenceSum)) * 100;

                // 4. 拜访转来访率
                Double visitToShouFangSum = Double.valueOf(visitToShouFangMolecules);
                Double visitToShouFangDifference = Double.valueOf(visitToShouFangMoleculesDifference);
                Double cvTvRate = (0 == (cvSum + visitToShouFangDifference)) ? 0.0 :
                        ((visitToShouFangSum + visitToShouFangDifference) / (cvSum + visitToShouFangDifference)) * 100;

                // 5. 三个一转来访率
                Double threeOneToShouFangSum = Double.valueOf(threeOneToShouFangMolecules);
                Double threeOneToShouFangDifference = Double.valueOf(threeOneToShouFangMoleculesDifference);
                Double toTvRate = (0 == (toSum + threeOneToShouFangDifference)) ? 0.0 :
                        ((threeOneToShouFangSum + threeOneToShouFangDifference) / (toSum + threeOneToShouFangDifference)) * 100;

                // 6. 首访转复访率
                Double shouFangToFuFangSum = Double.valueOf(shouFangToFuFangMolecules);
                Double shouFangToFuFangDifference = Double.valueOf(shouFangToFuFangMoleculesDifference);
                Double fvTrvRate = (0 == (fvSum + shouFangToFuFangDifference)) ? 0.0 :
                        ((shouFangToFuFangSum + shouFangToFuFangDifference) / (fvSum + shouFangToFuFangDifference)) * 100;

                // 7. 复访转成交率
                Double fuFangToChengjiaoSum = Double.valueOf(fuFangToChengjiaoMolecules);
                Double fuFangToChengjiaoDifferenceSum = Double.valueOf(fuFangToChengjiaoMoleculesDifference);
                Double rvTtdRate = (0 == (rvSum + fuFangToChengjiaoDifferenceSum)) ? 0.0 :
                        ((fuFangToChengjiaoSum + fuFangToChengjiaoDifferenceSum) / (rvSum + fuFangToChengjiaoDifferenceSum)) * 100;

                // 8. 来访转成交率
                Double daoFangToChengjiaoSum = Double.valueOf(daoFangToChengjiaoMolecules);
                Double daoFangToChengjiaoDifferenceSum = Double.valueOf(daoFangToChengjiaoMoleculesDifference);
                Double vTtdRate = (0 == (vSum + daoFangToChengjiaoDifferenceSum)) ? 0.0 :
                        ((daoFangToChengjiaoSum + daoFangToChengjiaoDifferenceSum) / (vSum + daoFangToChengjiaoDifferenceSum)) * 100;

                // 9. 累计复购率
                Double chengJiaoToFuGouSum = Double.valueOf(chengJiaoToFuGouMolecules);
                Double reTtdRate = (0 == tdSum) ? 0.0 : (chengJiaoToFuGouSum / tdSum) * 100;

                // -------------------------- 封装结果 --------------------------
                ProConversionRate proConversionRate = new ProConversionRate();
                proConversionRate.setName(areaName);
                proConversionRate.setAddToComeVisitRate(df.format(xzTcvRate) + "%");
                proConversionRate.setAddToThreeOnesRate(df.format(xzTtoRate) + "%");
                proConversionRate.setAddToFollowVisitRate(df.format(xzTvRate) + "%");
                proConversionRate.setComeVisitToFollowVisitRate(df.format(cvTvRate) + "%");
                proConversionRate.setThreeOnesToFollowVisitRate(df.format(toTvRate) + "%");
                proConversionRate.setFirstFollowVisitToReFollowVisitRate(df.format(fvTrvRate) + "%");
                proConversionRate.setReFollowVisitToTradeRate(df.format(rvTtdRate) + "%");
                proConversionRate.setFollowVisitToTradeRate(df.format(vTtdRate) + "%");
                proConversionRate.setReTradeRate(df.format(reTtdRate) + "%");

                proConversionRates.add(proConversionRate);


            });
            return proConversionRates; // 返回封装好的转化率结果（与老代码返回类型一致）
        }

    }
    /**
     * 使用JDK8 Stream API计算分子中存在但分母中不存在的客户ID数量
     */
    private long calculateMissingCustomerCountWithStream(List<Map> denominatorList, List<Map> numeratorList) {
        // 1. 提取分母中所有非空客户ID并去重（转为Set）
        // JDK8中用Collections.emptyList()替代List.of()
        Set<Object> denominatorIds = (denominatorList == null ? Collections.<Map>emptyList() : denominatorList)
                .stream()
                .map(map -> map.get("opportunityClueId")) // 提取客户ID字段（根据实际字段名调整）
                .filter(Objects::nonNull) // 过滤null值
                .collect(Collectors.toSet()); // 去重并收集为Set

        // 2. 提取分子中所有非空客户ID并去重，然后过滤出不在分母中的ID
        return (numeratorList == null ? Collections.<Map>emptyList() : numeratorList)
                .stream()
                // 单次操作完成过滤和映射：不符合条件的返回null，后续统一过滤
                .map(map -> {
                    Object projectId = map.get("projectId");
                    Object bproId = map.get("bproId");
                    // 先判断projectId和bproId是否需要过滤
                    if (projectId != null && bproId != null && projectId.equals(bproId)) {
                        return null; // 需要过滤的记录直接返回null
                    }
                    // 提取opportunityClueId（自动包含了null判断）
                    return map.get("opportunityClueId");
                })
                .filter(Objects::nonNull) // 同时过滤掉：被排除的记录(null)和无效ID(null)
                .filter(id -> !denominatorIds.contains(id))
                .count();
    }
    // 定义一个通用方法：计算单个分子列表与分母列表的匹配数量
    private long calculateMatchCount(List<Map> denominatorList, List<Map> numeratorList) {
        // 1. 将当前分母列表转为 ID -> 完成时间的映射
        Map<Object, Date> numeratorTimeMap = denominatorList.stream()
                .filter(num -> num.get("opportunityClueId") != null && num.get("businessTime") instanceof Date) // 过滤无效数据
                .collect(Collectors.toMap(
                        num -> num.get("opportunityClueId"),
                        num -> (Date) num.get("businessTime"),
                        (existing, replacement) -> existing // 处理重复ID，保留第一个
                ));

        // 2. 分子列表过滤并计数
        return numeratorList.stream()
                .filter(denominator -> {
                    Object denoId = denominator.get("opportunityClueId");
                    Date denoTime = (denominator.get("businessTime") instanceof Date) ?
                            (Date) denominator.get("businessTime") : null;

                    // 校验：ID不为空 + 分母时间不为空 + 存在相同ID的分子 + 分子时间晚于分母时间
                    return denoId != null
                            && denoTime != null
                            && numeratorTimeMap.containsKey(denoId)
                            && denoTime.after(numeratorTimeMap.get(denoId));
                })
                .count();
    }
    private ProConversionRate performQueryAndReturnResult(String searchType,Map prMap,Map<String, List<CustomerAddLogVo>> oppMap,Map paramMap,List<CustomerAddLogVo> customerAddLogList) {
        if("1".equals(searchType)){
//            ids.stream().forEach(pro->{
                String x = String.valueOf(prMap.get("projectId"));
                //查询项目内符合条件的客户
                List<CustomerAddLogVo> oppList = oppMap.get(x);
                if (CollectionUtils.isEmpty(oppList)){
                    ProConversionRate proConversionRate = new ProConversionRate();
                    proConversionRate.setName(String.valueOf(prMap.get("projectName")));
                    proConversionRate.setAddToComeVisitRate("0.00%");//新增转拜访率
                    proConversionRate.setAddToThreeOnesRate("0.00%");//新增转三个一率
                    proConversionRate.setAddToFollowVisitRate("0.00%");//新增转来访率
                    proConversionRate.setComeVisitToFollowVisitRate("0.00%");//拜访转来访率
                    proConversionRate.setThreeOnesToFollowVisitRate("0.00%");//三个一转来访率
                    proConversionRate.setFirstFollowVisitToReFollowVisitRate("0.00%");//首访转复访率
                    proConversionRate.setReFollowVisitToTradeRate("0.00%");//复访转成交率
                    proConversionRate.setFollowVisitToTradeRate("0.00%");//来访转成交率
                    proConversionRate.setReTradeRate("0.00%");//累计复购率
                    return proConversionRate;
                }else {
                    //新增转拜访客户 新增的客户里（指数据统计里的新增客户数），有多少客户已经拜访过了； 新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转拜访客户 日志表左连接客户跟进交易记录表 增加筛选项 跟进方式为上门拜访 在对满足条件的客户ID 去重
                    List<CustomerAddLogVo> addCount = oppList.stream().filter(a -> a.getIsAdd().equals("1") &&
                            a.getReportCreateTime().compareTo(paramMap.get("startTime")+"") >= 0 && a.getReportCreateTime().compareTo(paramMap.get("endTime")+"") <= 0
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> addToCVisitCount = new ArrayList<>();
                    if (addCount.size()>0){
                        addToCVisitCount = oppList.stream().filter(a-> addCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //新增转三个一率 新增的客户里（指数据统计里的新增客户数），有多少客户已经达成了实际三个一； 新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转三个一客户 查询客户报备日志表 查询创建方式为手动录入且是否完成三个一为是的客户 针对客户ID 去重
                    List<CustomerAddLogVo> addToThreeOnesCount = new ArrayList<>();
                    if (addCount.size()>0){
                        addToThreeOnesCount = oppList.stream().filter(a-> addCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> Optional.ofNullable(b.getIsThreeOnesStatus()).map(date -> date.equals("1")).orElse(false)
                                        && b.getIsStatistics().equals("1") && b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //新增转来访客户 新增的客户里（指数据统计里的新增客户数），有多少客户已经来访过了；新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转来访客户 左连接客户跟进交易记录表 查询业务类型为邀约到访和自然来访的记录 针对客户ID 去重 联动招商来访客户为满足上述来访条件 但是项目和主项目不同的客户
                    //正常新增到访客户
                    List<CustomerAddLogVo> addToVisitCount1 = new ArrayList<>();
                    if (addCount.size()>0){
                        addToVisitCount1 = oppList.stream().filter(a-> addCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsStatistics().equals("1") &&
                                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0 &&
                                        b.getBusinessProjectId().equals(x))
                        ).collect(Collectors.toList());
                    }
                    //联动 到访客户（需满足分母条件 例如三个一转来访 这里的客户也需要限制完成三个一的 如果不满足分子分母都不计入）
                    List<CustomerAddLogVo> addToVisitCount2 = customerAddLogList.stream().filter(a-> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsStatistics().equals("1") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0 &&
                            b.getMainVisitProjectId().equals(x) && !b.getBusinessProjectId().equals(x))
                    ).collect(Collectors.toList());
                    //拜访转首访率 已拜访过的客户里，有多少客户完成了首访；拜访客户 查询客户报备日志表左连接客户跟进交易记录表 查询业务类型为上门拜访的客户 针对客户ID去重 拜访转来访查询业务类型同时包含上门拜访和（y邀约拜访 自然来访）的客户 针对客户ID去重
                    List<CustomerAddLogVo> cVisitCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> cVisitTofVisitCount1 = new ArrayList<>();
                    if (cVisitCount.size()>0){
                        cVisitTofVisitCount1 = oppList.stream().filter(a ->cVisitCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                        b.getBusinessTime().compareTo(String.valueOf(cVisitCount.stream().filter(c ->
                                                c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c ->
                                                c.getCustomerFodLogList().stream().filter(d ->
                                                        d.getBusinessType().equals("2") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())
                                        ) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0
                                        && b.getBusinessProjectId().equals(x))
                        ).collect(Collectors.toList());
                    }
                    List<CustomerAddLogVo> cVisitCount2 = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> cVisitTofVisitCount2 = new ArrayList<>();
                    if (cVisitCount2.size()>0){
                        cVisitTofVisitCount2 = cVisitCount2.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                b.getBusinessTime().compareTo(String.valueOf(cVisitCount2.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> d.getBusinessType().equals("2") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0
                                && b.getMainVisitProjectId().equals(x) && !b.getBusinessProjectId().equals(x))
                        ).collect(Collectors.toList());
                    }
                    //三个一转首访率 实际达成三个一的客户里，有多少客户完成了首访； 三个一客户查询客户报备记录表 是否三个一客户是是的客户 针对客户ID去重 三个一转来访客户 左连接客户跟进交易记录表 查询 日志表是否三个一客户是是的客户且跟进交易记录表业务类型为邀约到访和自然来访的记录 针对客户ID 去重
                    List<CustomerAddLogVo> threeOnesCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> Optional.ofNullable(b.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false)
                            && b.getIsStatistics().equals("1") &&
                            Optional.ofNullable(b.getBusinessTime()).map(date -> date.compareTo(paramMap.get("startTime") + "") >= 0 && date.compareTo(paramMap.get("endTime") + "") <= 0).orElse(false))
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> threeOnesTofVisitCount1 = new ArrayList<>();
                    if (threeOnesCount.size()>0){
                        threeOnesTofVisitCount1 = oppList.stream().filter(a ->threeOnesCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                        b.getBusinessTime().compareTo(String.valueOf(threeOnesCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> Optional.ofNullable(d.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false) && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0
                                        && b.getBusinessProjectId().equals(x))
                        ).collect(Collectors.toList());
                    }
                    List<CustomerAddLogVo> threeOnesCount2 = customerAddLogList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> Optional.ofNullable(b.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false) && b.getIsStatistics().equals("1") &&
                            Optional.ofNullable(b.getBusinessTime()).map(date -> date.compareTo(paramMap.get("startTime") + "") >= 0 && date.compareTo(paramMap.get("endTime") + "") <= 0).orElse(false))
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> threeOnesTofVisitCount2 = new ArrayList<>();
                    if (threeOnesCount2.size()>0){
                        threeOnesTofVisitCount2 = threeOnesCount2.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                                b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                        b.getBusinessTime().compareTo(String.valueOf(threeOnesCount2.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> Optional.ofNullable(d.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false) && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0
                                        && b.getMainVisitProjectId().equals(x) && !b.getBusinessProjectId().equals(x))
                        ).collect(Collectors.toList());
                    }
                    //首访转复访率 完成过首访的客户里，有多少客户完成了第二及以上次来访；首访客户 查询客户报备日志表左连接客户跟进交易记录表 查询业务类型为到访 且是否首访状态为是是的客户 针对客户ID 去重 首访转复访查询业务类型为到访 且是否首访状态为否的客户 针对客户ID 去重
                    List<CustomerAddLogVo> firstVisitCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                            && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("1") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0
                            && b.getMainVisitProjectId().equals(x))
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> firstVisitToReVisitCount = new ArrayList<>();
                    if (firstVisitCount.size()>0){
                        firstVisitToReVisitCount = oppList.stream().filter(a -> firstVisitCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                                        && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("0") &&
                                        b.getBusinessTime().compareTo(String.valueOf(firstVisitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsFirstVisitStatus().equals("1") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //复访转成交率 完成两次及以上来访的客户里，有多少客户已经成交； 复访客户查询业务类型为到访 且是否首访状态为否的客户 针对客户ID 去重 复访转成交 查询业务类型为到访 且是否首访状态为否的客户 客户是否成交为是的客户 针对客户ID 去重
                    List<CustomerAddLogVo> reVisitCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                            && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("0") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0
                            && b.getMainVisitProjectId().equals(x))
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> reVisitToTradeCount = new ArrayList<>();
                    if (reVisitCount.size()>0){
                        reVisitToTradeCount = oppList.stream().filter(a -> reVisitCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                                        b.getBusinessTime().compareTo(String.valueOf(reVisitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsStatistics().equals("1") && d.getIsFirstVisitStatus().equals("0")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //来访转成交率 来访过的客户里，有多少客户已经成交；来访客户查询业务类型为到访的客户 针对客户ID 去重 来访转成交 查询业务类型为到访 且客户是否成交为是的客户 针对客户ID 去重
                    List<CustomerAddLogVo> visitCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsStatistics().equals("1") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0
                            && b.getMainVisitProjectId().equals(x))
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> visitToTradeCount = new ArrayList<>();
                    if (visitCount.size()>0){
                        visitToTradeCount = oppList.stream().filter(a -> visitCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                                        b.getBusinessTime().compareTo(String.valueOf(visitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //累计复购率 已经成交的厂房客户里，有多少客户重复购买； 查询客户报备记录表是否签约状态为是的客户 复购客户查询是否复购为是的客户
                    List<CustomerAddLogVo> tradeCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> tradeToReCount = new ArrayList<>();
                    if (tradeCount.size() > 0) {
                        tradeToReCount = oppList.stream().filter(a -> tradeCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") && b.getBusinessTime().compareTo(
                                        String.valueOf(LocalDateTime.parse(String.valueOf(tradeCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> d.getBusinessType().equals("6")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get()), dtf).plusDays(2))
                                ) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //针对查询结果的客户ID 去重
                    //新增
                    int xzCount = addCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double xzSum = Double.valueOf(xzCount);
                    //新增转拜访率
                    int xzTcvCount = addToCVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double xzTcvSum = Double.valueOf(xzTcvCount);
                    Double xzTcvRate;
                    if (0 == xzSum) {
                        xzTcvRate = 0.0;
                    }else {
                        xzTcvRate = xzTcvSum/xzSum*100;
                    }
                    //新增转三个一率
                    int xzTtoCount = addToThreeOnesCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double xzTtoSum = Double.valueOf(xzTtoCount);
                    Double xzTtoRate;
                    if (0 == xzSum) {
                        xzTtoRate = 0.0;
                    }else {
                        xzTtoRate = xzTtoSum/xzSum*100;
                    }
                    //新增转来访率
                    int xzTvCount1 = addToVisitCount1.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    int xzTvCount2 = addToVisitCount2.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double xzTvSum1 = Double.valueOf(xzTvCount1);
                    Double xzTvSum2 = Double.valueOf(xzTvCount2);
                    Double xzTvRate;
                    if (0 == xzSum+xzTvSum2) {
                        xzTvRate = 0.0;
                    }else {
                        xzTvRate = ((xzTvSum1+xzTvSum2)/(xzSum+xzTvSum2))*100;
                    }
                    //拜访
                    int cvCount = cVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double cvSum = Double.valueOf(cvCount);
                    //拜访转来访率
                    int cvTvCount1 = cVisitTofVisitCount1.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    int cvTvCount2 = cVisitTofVisitCount2.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double cvTvSum1 = Double.valueOf(cvTvCount1);
                    Double cvTvSum2 = Double.valueOf(cvTvCount2);
                    Double cvTvRate;
                    if (0 == cvSum+cvTvSum2) {
                        cvTvRate = 0.0;
                    }else {
                        cvTvRate = ((cvTvSum1+cvTvSum2)/(cvSum+cvTvSum2))*100;
                    }
                    //三个一
                    int toCount = threeOnesCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double toSum = Double.valueOf(toCount);
                    //三个一转来访率
                    int toTvCount1 = threeOnesTofVisitCount1.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    int toTvCount2 = threeOnesTofVisitCount2.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double toTvSum1 = Double.valueOf(toTvCount1);
                    Double toTvSum2 = Double.valueOf(toTvCount2);
                    Double toTvRate;
                    if (0 == toSum+toTvSum2) {
                        toTvRate = 0.0;
                    }else {
                        toTvRate = ((toTvSum1+toTvSum2)/(toSum+toTvSum2))*100;
                    }
                    //首访
                    int fvCount = firstVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double fvSum = Double.valueOf(fvCount);
                    //首访转复访率
                    int fvTrvCount = firstVisitToReVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double fvTrvSum = Double.valueOf(fvTrvCount);
                    Double fvTrvRate;
                    if (0 == fvSum) {
                        fvTrvRate = 0.0;
                    }else {
                        fvTrvRate = fvTrvSum/fvSum*100;
                    }
                    //复访
                    int rvCount = reVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double rvSum = Double.valueOf(rvCount);
                    //复访转成交率
                    int rvTtdCount = reVisitToTradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double rvTtdSum = Double.valueOf(rvTtdCount);
                    Double rvTtdRate;
                    if (0 == rvSum) {
                        rvTtdRate = 0.0;
                    }else {
                        rvTtdRate = rvTtdSum/rvSum*100;
                    }
                    //来访
                    int vCount = visitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    //来访转成交率
                    int vTtdCount = visitToTradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double vTtdSum = Double.valueOf(vTtdCount);
                    Double vTtdRate;
                    if (0 == vCount) {
                        vTtdRate = 0.0;
                    }else {
                        vTtdRate = vTtdSum/vCount*100;
                    }
                    //累计复购率
                    int tdCount = tradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double tdSum = Double.valueOf(tdCount);
                    int reTtdCount = tradeToReCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double reTtdSum = Double.valueOf(reTtdCount);
                    Double reTtdRate;
                    if (0 == tdSum) {
                        reTtdRate = 0.0;
                    }else {
                        reTtdRate = reTtdSum/tdSum*100;
                    }
                    ProConversionRate proConversionRate = new ProConversionRate();
                    proConversionRate.setName(String.valueOf(prMap.get("projectName")));
                    proConversionRate.setAddToComeVisitRate(df.format(xzTcvRate)+"%");//新增转拜访率
                    proConversionRate.setAddToThreeOnesRate(df.format(xzTtoRate)+"%");//新增转三个一率
                    proConversionRate.setAddToFollowVisitRate(df.format(xzTvRate)+"%");//新增转来访率
                    proConversionRate.setComeVisitToFollowVisitRate(df.format(cvTvRate)+"%");//拜访转来访率
                    proConversionRate.setThreeOnesToFollowVisitRate(df.format(toTvRate)+"%");//三个一转来访率
                    proConversionRate.setFirstFollowVisitToReFollowVisitRate(df.format(fvTrvRate)+"%");//首访转复访率
                    proConversionRate.setReFollowVisitToTradeRate(df.format(rvTtdRate)+"%");//复访转成交率
                    proConversionRate.setFollowVisitToTradeRate(df.format(vTtdRate)+"%");//来访转成交率
                    proConversionRate.setReTradeRate(df.format(reTtdRate)+"%");//累计复购率
                    return proConversionRate;
                }
//            });
        }else {
//            ids.stream().forEach(region->{
                String x = String.valueOf(prMap.get("areaId"));
                //获取区域下项目
                List<String> proList = dataStatisticDao.getProList(Arrays.asList(x));
                //查询项目内符合条件的客户
                List<CustomerAddLogVo> oppList = oppMap.get(x);
                if (CollectionUtils.isEmpty(oppList)){
                    ProConversionRate proConversionRate = new ProConversionRate();
                    proConversionRate.setName(String.valueOf(prMap.get("areaName")));
                    proConversionRate.setAddToComeVisitRate("0.00%");//新增转拜访率
                    proConversionRate.setAddToThreeOnesRate("0.00%");//新增转三个一率
                    proConversionRate.setAddToFollowVisitRate("0.00%");//新增转来访率
                    proConversionRate.setComeVisitToFollowVisitRate("0.00%");//拜访转来访率
                    proConversionRate.setThreeOnesToFollowVisitRate("0.00%");//三个一转来访率
                    proConversionRate.setFirstFollowVisitToReFollowVisitRate("0.00%");//首访转复访率
                    proConversionRate.setReFollowVisitToTradeRate("0.00%");//复访转成交率
                    proConversionRate.setFollowVisitToTradeRate("0.00%");//来访转成交率
                    proConversionRate.setReTradeRate("0.00%");//累计复购率
                    return proConversionRate;
                }else {
                    //新增转拜访客户 新增的客户里（指数据统计里的新增客户数），有多少客户已经拜访过了； 新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转拜访客户 日志表左连接客户跟进交易记录表 增加筛选项 跟进方式为上门拜访 在对满足条件的客户ID 去重
                    List<CustomerAddLogVo> addCount = oppList.stream().filter(a -> a.getIsAdd().equals("1") &&
                            a.getReportCreateTime().compareTo(paramMap.get("startTime")+"") >= 0 && a.getReportCreateTime().compareTo(paramMap.get("endTime")+"") <= 0
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> addToCVisitCount = new ArrayList<>();
                    if (addCount.size()>0){
                        addToCVisitCount = oppList.stream().filter(a-> addCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //新增转三个一率 新增的客户里（指数据统计里的新增客户数），有多少客户已经达成了实际三个一； 新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转三个一客户 查询客户报备日志表 查询创建方式为手动录入且是否完成三个一为是的客户 针对客户ID 去重
                    List<CustomerAddLogVo> addToThreeOnesCount = new ArrayList<>();
                    if (addCount.size()>0){
                        addToThreeOnesCount = oppList.stream().filter(a-> addCount.contains(a) &&
                                a.getIsThreeOnes().equals("1") && Optional.ofNullable(a.getIsThreeOnesDate()).map(date -> date.compareTo(paramMap.get("startTime") + "") >= 0 && date.compareTo(paramMap.get("deadTime") + "") <= 0).orElse(false)
                        ).collect(Collectors.toList());
                    }
                    //新增转来访客户 新增的客户里（指数据统计里的新增客户数），有多少客户已经来访过了；新增客户查询客户报备日志表中是否新增为是的客户 针对客户ID 去重 新增转来访客户 左连接客户跟进交易记录表 查询业务类型为邀约到访和自然来访的记录 针对客户ID 去重 联动招商来访客户为满足上述来访条件 但是项目和主项目不同的客户
                    //正常新增到访客户
                    List<CustomerAddLogVo> addToVisitCount = new ArrayList<>();
                    if (addCount.size()>0){
                        addToVisitCount = oppList.stream().filter(a-> addCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsStatistics().equals("1") &&
                                        b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0 &&
                                        proList.contains(b.getMainVisitProjectId()))
                        ).collect(Collectors.toList());
                    }
                    //拜访转首访率 已拜访过的客户里，有多少客户完成了首访；拜访客户 查询客户报备日志表左连接客户跟进交易记录表 查询业务类型为上门拜访的客户 针对客户ID去重 拜访转来访查询业务类型同时包含上门拜访和（y邀约拜访 自然来访）的客户 针对客户ID去重
                    List<CustomerAddLogVo> cVisitCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("2") && b.getIsStatistics().equals("1") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> cVisitTofVisitCount = new ArrayList<>();
                    if (cVisitCount.size()>0){
                        cVisitTofVisitCount = oppList.stream().filter(a ->cVisitCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                        b.getBusinessTime().compareTo(String.valueOf(cVisitCount.stream().filter(c ->
                                                c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c ->
                                                c.getCustomerFodLogList().stream().filter(d ->
                                                        d.getBusinessType().equals("2") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())
                                        ) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0
                                        && proList.contains(b.getMainVisitProjectId()))
                        ).collect(Collectors.toList());
                    }
                    //三个一转首访率 实际达成三个一的客户里，有多少客户完成了首访； 三个一客户查询客户报备记录表 是否三个一客户是是的客户 针对客户ID去重 三个一转来访客户 左连接客户跟进交易记录表 查询 日志表是否三个一客户是是的客户且跟进交易记录表业务类型为邀约到访和自然来访的记录 针对客户ID 去重
                    List<CustomerAddLogVo> threeOnesCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> Optional.ofNullable(b.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false) && b.getIsStatistics().equals("1") &&
                            Optional.ofNullable(b.getBusinessTime()).map(date -> date.compareTo(paramMap.get("startTime") + "") >= 0 && date.compareTo(paramMap.get("endTime") + "") <= 0).orElse(false))
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> threeOnesTofVisitCount = new ArrayList<>();
                    if (threeOnesCount.size()>0){
                        threeOnesTofVisitCount = oppList.stream().filter(a ->threeOnesCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) && b.getIsFirstVisitStatus().equals("1") && b.getIsStatistics().equals("1") &&
                                        b.getBusinessTime().compareTo(String.valueOf(threeOnesCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> Optional.ofNullable(d.getIsThreeOnesStatus()).map(status -> status.equals("1")).orElse(false) && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0
                                        && proList.contains(b.getMainVisitProjectId()))
                        ).collect(Collectors.toList());
                    }
                    //首访转复访率 完成过首访的客户里，有多少客户完成了第二及以上次来访；首访客户 查询客户报备日志表左连接客户跟进交易记录表 查询业务类型为到访 且是否首访状态为是是的客户 针对客户ID 去重 首访转复访查询业务类型为到访 且是否首访状态为否的客户 针对客户ID 去重
                    List<CustomerAddLogVo> firstVisitCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                            && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("1") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0
                            && proList.contains(b.getMainVisitProjectId()))
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> firstVisitToReVisitCount = new ArrayList<>();
                    if (firstVisitCount.size()>0){
                        firstVisitToReVisitCount = oppList.stream().filter(a -> firstVisitCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                                        && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("0") &&
                                        b.getBusinessTime().compareTo(String.valueOf(firstVisitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsFirstVisitStatus().equals("1") && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //复访转成交率 完成两次及以上来访的客户里，有多少客户已经成交； 复访客户查询业务类型为到访 且是否首访状态为否的客户 针对客户ID 去重 复访转成交 查询业务类型为到访 且是否首访状态为否的客户 客户是否成交为是的客户 针对客户ID 去重
                    List<CustomerAddLogVo> reVisitCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4"))
                            && b.getIsStatistics().equals("1") && b.getIsFirstVisitStatus().equals("0") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0
                            && proList.contains(b.getMainVisitProjectId()))
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> reVisitToTradeCount = new ArrayList<>();
                    if (reVisitCount.size()>0){
                        reVisitToTradeCount = oppList.stream().filter(a -> reVisitCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                                        b.getBusinessTime().compareTo(String.valueOf(reVisitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4") && d.getIsStatistics().equals("1")) && d.getIsFirstVisitStatus().equals("0")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //来访转成交率 来访过的客户里，有多少客户已经成交；来访客户查询业务类型为到访的客户 针对客户ID 去重 来访转成交 查询业务类型为到访 且客户是否成交为是的客户 针对客户ID 去重
                    List<CustomerAddLogVo> visitCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> (b.getBusinessType().equals("3") || b.getBusinessType().equals("4")) &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0
                            && proList.contains(b.getMainVisitProjectId()))
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> visitToTradeCount = new ArrayList<>();
                    if (visitCount.size() > 0) {
                        visitToTradeCount = oppList.stream().filter(a -> visitCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                                        b.getBusinessTime().compareTo(String.valueOf(visitCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> (d.getBusinessType().equals("3") || d.getBusinessType().equals("4")) && d.getIsStatistics().equals("1")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get())) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //累计复购率 已经成交的厂房客户里，有多少客户重复购买； 查询客户报备记录表是否签约状态为是的客户 复购客户查询是否复购为是的客户
                    List<CustomerAddLogVo> tradeCount = oppList.stream().filter(a -> a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") &&
                            b.getBusinessTime().compareTo(paramMap.get("startTime") + "") >= 0 && b.getBusinessTime().compareTo(paramMap.get("endTime") + "") <= 0)
                    ).collect(Collectors.toList());
                    List<CustomerAddLogVo> tradeToReCount = new ArrayList<>();
                    if (tradeCount.size() > 0) {
                        tradeToReCount = oppList.stream().filter(a -> tradeCount.contains(a) &&
                                a.getCustomerFodLogList().stream().anyMatch(b -> b.getBusinessType().equals("6") && b.getBusinessTime().compareTo(
                                        String.valueOf(LocalDateTime.parse(String.valueOf(tradeCount.stream().filter(c -> c.getOpportunityClueId().equals(a.getOpportunityClueId())).flatMap(c -> c.getCustomerFodLogList().stream().filter(d -> d.getBusinessType().equals("6")).map(CustomerFodLogVo::getBusinessTime)).min(Comparator.naturalOrder()).get()), dtf).plusDays(2))
                                ) >= 0 && b.getBusinessTime().compareTo(paramMap.get("deadTime") + "") <= 0)
                        ).collect(Collectors.toList());
                    }
                    //针对查询结果的客户ID 去重
                    //新增
                    int xzCount = addCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double xzSum = Double.valueOf(xzCount);
                    //新增转拜访率
                    int xzTcvCount = addToCVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double xzTcvSum = Double.valueOf(xzTcvCount);
                    Double xzTcvRate;
                    if (0 == xzSum) {
                        xzTcvRate = 0.0;
                    }else {
                        xzTcvRate = xzTcvSum/xzSum*100;
                    }
                    //新增转三个一率
                    int xzTtoCount = addToThreeOnesCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double xzTtoSum = Double.valueOf(xzTtoCount);
                    Double xzTtoRate;
                    if (0 == xzSum) {
                        xzTtoRate = 0.0;
                    }else {
                        xzTtoRate = xzTtoSum/xzSum*100;
                    }
                    //新增转来访率
                    int xzTvCount = addToVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double xzTvSum = Double.valueOf(xzTvCount);
                    Double xzTvRate;
                    if (0 == xzSum) {
                        xzTvRate = 0.0;
                    }else {
                        xzTvRate = xzTvSum/xzSum*100;
                    }
                    //拜访
                    int cvCount = cVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double cvSum = Double.valueOf(cvCount);
                    //拜访转来访率
                    int cvTvCount = cVisitTofVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double cvTvSum = Double.valueOf(cvTvCount);
                    Double cvTvRate;
                    if (0 == cvSum) {
                        cvTvRate = 0.0;
                    }else {
                        cvTvRate = cvTvSum/cvSum*100;
                    }
                    //三个一
                    int toCount = threeOnesCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double toSum = Double.valueOf(toCount);
                    //三个一转来访率
                    int toTvCount = threeOnesTofVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double toTvSum = Double.valueOf(toTvCount);
                    Double toTvRate;
                    if (0 == toSum) {
                        toTvRate = 0.0;
                    }else {
                        toTvRate = toTvSum/toSum*100;
                    }
                    //首访
                    int fvCount = firstVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double fvSum = Double.valueOf(fvCount);
                    //首访转复访率
                    int fvTrvCount = firstVisitToReVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double fvTrvSum = Double.valueOf(fvTrvCount);
                    Double fvTrvRate;
                    if (0 == fvSum) {
                        fvTrvRate = 0.0;
                    }else {
                        fvTrvRate = fvTrvSum/fvSum*100;
                    }
                    //复访
                    int rvCount = reVisitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double rvSum = Double.valueOf(rvCount);
                    //复访转成交率
                    int rvTtdCount = reVisitToTradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double rvTtdSum = Double.valueOf(rvTtdCount);
                    Double rvTtdRate;
                    if (0 == rvSum) {
                        rvTtdRate = 0.0;
                    }else {
                        rvTtdRate = rvTtdSum/rvSum*100;
                    }
                    //来访
                    int vCount = visitCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    //来访转成交率
                    int vTtdCount = visitToTradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double vTtdSum = Double.valueOf(vTtdCount);
                    Double vTtdRate;
                    if (0 == vCount) {
                        vTtdRate = 0.0;
                    }else {
                        vTtdRate = vTtdSum/vCount*100;
                    }
                    //累计复购率
                    int tdCount = tradeCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double tdSum = Double.valueOf(tdCount);
                    int reTtdCount = tradeToReCount.stream().filter(distinctByKey(CustomerAddLogVo::getOpportunityClueId)).collect(Collectors.toList()).size();
                    Double reTtdSum = Double.valueOf(reTtdCount);
                    Double reTtdRate;
                    if (0 == tdSum) {
                        reTtdRate = 0.0;
                    }else {
                        reTtdRate = reTtdSum/tdSum*100;
                    }
                    ProConversionRate proConversionRate = new ProConversionRate();
                    proConversionRate.setName(String.valueOf(prMap.get("areaName")));
                    proConversionRate.setAddToComeVisitRate(df.format(xzTcvRate)+"%");//新增转拜访率
                    proConversionRate.setAddToThreeOnesRate(df.format(xzTtoRate)+"%");//新增转三个一率
                    proConversionRate.setAddToFollowVisitRate(df.format(xzTvRate)+"%");//新增转来访率
                    proConversionRate.setComeVisitToFollowVisitRate(df.format(cvTvRate)+"%");//拜访转来访率
                    proConversionRate.setThreeOnesToFollowVisitRate(df.format(toTvRate)+"%");//三个一转来访率
                    proConversionRate.setFirstFollowVisitToReFollowVisitRate(df.format(fvTrvRate)+"%");//首访转复访率
                    proConversionRate.setReFollowVisitToTradeRate(df.format(rvTtdRate)+"%");//复访转成交率
                    proConversionRate.setFollowVisitToTradeRate(df.format(vTtdRate)+"%");//来访转成交率
                    proConversionRate.setReTradeRate(df.format(reTtdRate)+"%");//累计复购率
                    return proConversionRate;
                }
//            });
        }
    }

    @Override
    public ResultBody regionConversionRateStatistics(Map paramMap) {
        List<ProConversionRate> proDataStatistics = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        if (paramMap.get("regionIds")!=null && !"".equals(paramMap.get("regionIds")+"")){
            String regionIds = paramMap.get("regionIds")+"";
            List<String> regionList = Arrays.asList(regionIds.split(","));
            regionList = dataStatisticDao.getProList(regionList);
            paramMap.put("regionList",regionList);
        }
        PageHelper.startPage(pageIndex, pageSize);
        proDataStatistics = dataStatisticDao.regionConversionRateStatistics(paramMap);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            for (ProConversionRate followUpStatistics:proDataStatistics) {
                String oppSum = followUpStatistics.getOppSum();
                String followCount = followUpStatistics.getFollowCount();
                String visitCount = followUpStatistics.getVisitCount();
                String threeOnesCount = followUpStatistics.getThreeOnesCount();
                String orderSum = followUpStatistics.getOrderSum();
                if ("0".equals(oppSum)){
                    followUpStatistics.setFollowRate("0.00%");
                    followUpStatistics.setVisitRate("0.00%");
                    followUpStatistics.setThreeOnesRate("0.00%");
                    followUpStatistics.setOrderRate("0.00%");
                }else{
                    Double sum = Double.valueOf(oppSum);
                    Double followUp = Double.valueOf(followCount);
                    Double visit = Double.valueOf(visitCount);
                    Double threeOnes = Double.valueOf(threeOnesCount);
                    Double order = Double.valueOf(orderSum);

                    Double FollowUpRate = followUp/sum*100;
                    Double VisitRate = visit/sum*100;
                    Double ThreeOnesRate = threeOnes/sum*100;
                    Double OrderRate = order/sum*100;
                    followUpStatistics.setFollowRate(df.format(FollowUpRate)+"%");
                    followUpStatistics.setVisitRate(df.format(VisitRate)+"%");
                    followUpStatistics.setThreeOnesRate(df.format(ThreeOnesRate)+"%");
                    followUpStatistics.setOrderRate(df.format(OrderRate)+"%");
                }

            }
        }
        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    @Override
    public void proConversionRateExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String userId = request.getHeader("userId");
        List<ProConversionRate> proDataStatistics = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = "项目转化率统计";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        List<String> proIdList = new ArrayList<>();
        if (paramMap.get("projectIds")!=null && !"".equals(paramMap.get("projectIds")+"")){
            String regionIds = paramMap.get("projectIds")+"";
            proIdList = Arrays.asList(regionIds.split(","));
            paramMap.put("projectIds",proIdList);
        }
        proDataStatistics = dataStatisticDao.proConversionRateStatistics(paramMap);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            for (ProConversionRate followUpStatistics:proDataStatistics) {
                String oppSum = followUpStatistics.getOppSum();
                String followCount = followUpStatistics.getFollowCount();
                String visitCount = followUpStatistics.getVisitCount();
                String threeOnesCount = followUpStatistics.getThreeOnesCount();
                String orderSum = followUpStatistics.getOrderSum();
                if ("0".equals(oppSum)){
                    followUpStatistics.setFollowRate("0.00%");
                    followUpStatistics.setVisitRate("0.00%");
                    followUpStatistics.setThreeOnesRate("0.00%");
                    followUpStatistics.setOrderRate("0.00%");
                }else{
                    Double sum = Double.valueOf(oppSum);
                    Double followUp = Double.valueOf(followCount);
                    Double visit = Double.valueOf(visitCount);
                    Double threeOnes = Double.valueOf(threeOnesCount);
                    Double order = Double.valueOf(orderSum);

                    Double FollowUpRate = followUp/sum*100;
                    Double VisitRate = visit/sum*100;
                    Double ThreeOnesRate = threeOnes/sum*100;
                    Double OrderRate = order/sum*100;
                    followUpStatistics.setFollowRate(df.format(FollowUpRate)+"%");
                    followUpStatistics.setVisitRate(df.format(VisitRate)+"%");
                    followUpStatistics.setThreeOnesRate(df.format(ThreeOnesRate)+"%");
                    followUpStatistics.setOrderRate(df.format(OrderRate)+"%");
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
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(userId);
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);

        if (proDataStatistics!=null && proDataStatistics.size()>0){
//            headers = proDataStatistics.get(0).cardTitle1;
            headers = filedNames.toArray(new String[0]);
            int rowNum = 1;
            for (ProConversionRate model : proDataStatistics) {
                model.setRownum(rowNum+"");
                Object[] oArray = model.toData1(filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void regionConversionRateExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);

        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }
        String userId = request.getHeader("userId");
        List<ProConversionRate> proDataStatistics = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = "区域转化率统计";
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        List<String> proIdList = new ArrayList<>();
        String projectIds = paramMap.get("regionIds")+"";
        List<String> proIdList1 = Arrays.asList(projectIds.split(","));
        proIdList = dataStatisticDao.getProList(proIdList1);
        paramMap.put("regionList",proIdList);
        proDataStatistics = dataStatisticDao.regionConversionRateStatistics(paramMap);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            for (ProConversionRate followUpStatistics:proDataStatistics) {
                String oppSum = followUpStatistics.getOppSum();
                String followCount = followUpStatistics.getFollowCount();
                String visitCount = followUpStatistics.getVisitCount();
                String threeOnesCount = followUpStatistics.getThreeOnesCount();
                String orderSum = followUpStatistics.getOrderSum();
                if ("0".equals(oppSum)){
                    followUpStatistics.setFollowRate("0.00%");
                    followUpStatistics.setVisitRate("0.00%");
                    followUpStatistics.setThreeOnesRate("0.00%");
                    followUpStatistics.setOrderRate("0.00%");
                }else{
                    Double sum = Double.valueOf(oppSum);
                    Double followUp = Double.valueOf(followCount);
                    Double visit = Double.valueOf(visitCount);
                    Double threeOnes = Double.valueOf(threeOnesCount);
                    Double order = Double.valueOf(orderSum);

                    Double FollowUpRate = followUp/sum*100;
                    Double VisitRate = visit/sum*100;
                    Double ThreeOnesRate = threeOnes/sum*100;
                    Double OrderRate = order/sum*100;
                    followUpStatistics.setFollowRate(df.format(FollowUpRate)+"%");
                    followUpStatistics.setVisitRate(df.format(VisitRate)+"%");
                    followUpStatistics.setThreeOnesRate(df.format(ThreeOnesRate)+"%");
                    followUpStatistics.setOrderRate(df.format(OrderRate)+"%");
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
        String areaName = excelImportMapper.getAreaNames(proIdList1);
        excelExportLog.setCreator(userId);
        excelExportLog.setAreaName(areaName);
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);

        if (proDataStatistics!=null && proDataStatistics.size()>0){
//            headers = proDataStatistics.get(0).cardTitle3;
            headers = filedNames.toArray(new String[0]);

            int rowNum = 1;
            for (ProConversionRate model : proDataStatistics) {
                model.setRownum(rowNum+"");
                Object[] oArray = model.toData1(filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResultBody getAllRegionNew() {
        Map mapUser=new HashMap();
        mapUser.put("UserName", SecurityUtils.getUsername());
        List<String> fullpath = projectMapper.findFullPath(mapUser);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无区域权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append(" and org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        mapUser.put("where", sb.toString());
        List<Map> regions = dataStatisticDao.getAllRegionNew(mapUser);
        return ResultBody.success(regions);
    }

    @Override
    public ResultBody sourceModeDataStatistics(Map paramMap) {
        List<SourceModeDataStatistics> proDataStatistics = new ArrayList<>();
        List<SourceModeDataStatistics> proDataStatisticsXt = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
//        int pageIndex = 1;
//        int pageSize = 10;
//        if (paramMap.get("pageNum")!=null){
//            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
//        }
//        if (paramMap.get("pageSize")!=null){
//            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
//        }
        String projectIds = paramMap.get("projectIds")+"";
        String regionIds = paramMap.get("regionIds")+"";
        List<String> proIds = Arrays.asList(projectIds.split(","));
        List<String> qyIds = Arrays.asList(regionIds.split(","));
//        PageHelper.startPage(pageIndex, pageSize);
        if ("".equals(projectIds) || "null".equals(projectIds)){
            return ResultBody.error(-1200002,"项目ID未传！！");
        }
        proIds = Arrays.asList(projectIds.split(",")).stream().distinct().collect(Collectors.toList());
        qyIds = Arrays.asList(regionIds.split(",")).stream().distinct().collect(Collectors.toList());
        //获取人员最高权限
        Map map = new HashMap<>();
        String jobCode = "";
        map.put("UserId",SecurityUtils.getUserId());
        List<Map> userList = projectMapper.findFullPathAllInsZs(map);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("10001") || String.valueOf(userList.get(i).get("JobCode")).equals("jtsjg")){
                    if(level<= 1){
                        jobCode = "10001";
                        level = 3;
                    }
                }
            }
        }
        if(!jobCode.equals("10001")){
            map.put("UserName",SecurityUtils.getUsername());
            List<String> orgProjectIds = projectMapper.findOrgProjectId(map);
            // 复制一份原始集合用于操作
            List<String> finalProIds = new ArrayList<>(proIds);
            finalProIds.removeIf(x -> !orgProjectIds.contains(x)); // 使用 removeIf 方法来筛选保留满足条件的元素
            List<String> finalQyIds = new ArrayList<>(qyIds);
            finalQyIds.removeIf(x -> !orgProjectIds.contains(x));
            finalQyIds.stream().forEach(x->{
                finalProIds.addAll(projectCluesDao.getProListAll(x));
            });
            //为空异常处理
            finalProIds.add("");
            finalQyIds.add("");
            paramMap.put("proIds",finalProIds.stream().distinct().collect(Collectors.toList()));
            paramMap.put("qyIds",finalQyIds);
        }else {
            paramMap.put("proIds",proIds);
            paramMap.put("qyIds",qyIds);
        }
        proDataStatistics = dataStatisticDao.getSourceModeDataStatistics(paramMap);
        if(proDataStatistics.size() > 0){
//            List<String> strings = proDataStatistics.stream().map(SourceModeDataStatistics::getProjectId).filter(x -> x != null).distinct().collect(Collectors.toList());
//            List<SourceModeDataStatistics> resultVoList = new ArrayList<>();
//            for (int i = 0; i < strings.size(); i++) {
//                String projectId = strings.get(i);
//                List<SourceModeDataStatistics> projectVoList = proDataStatistics.stream().filter(pro -> projectId.equals(pro.getProjectId())).collect(Collectors.toList());
//                if(projectVoList.size() > 0){
//                    SourceModeDataStatistics projectVo = new SourceModeDataStatistics();
//                    BeanUtils.copyProperties(projectVoList.get(0),projectVo);
//                    for (int j = 0; j < projectVoList.size(); j++) {
//                        if(projectVoList.get(j).getCustomerType().equals("报备客户数")){
//                            projectVo.setReportChildren(projectVoList.get(j));
//                        }else if(projectVoList.get(j).getCustomerType().equals("新增客户数")){
//                            projectVo.setAddChildren(projectVoList.get(j));
//                        }else if(projectVoList.get(j).getCustomerType().equals("来访客户数")){
//                            projectVo.setVisitChildren(projectVoList.get(j));
//                        }else if(projectVoList.get(j).getCustomerType().equals("成交客户数")){
//                            projectVo.setSignChildren(projectVoList.get(j));
//                        }
//                    }
//                    resultVoList.add(projectVo);
//                }
//            }
            List<SourceModeDataStatistics> reTrees = CollUtil.newArrayList();
            List<SourceModeDataStatistics> trees = CollUtil.newArrayList();
            List<SourceModeDataStatistics> reMoveProData = new ArrayList<>();
            for (SourceModeDataStatistics vo : proDataStatistics) {
                System.out.println(vo.getName());
                if ("-1".equals(vo.getComguid())) {
                    trees.add(vo);
                    reMoveProData.add(vo);
                }
                for (SourceModeDataStatistics organizationVO : proDataStatistics) {
                    if (organizationVO.getComguid() != null && vo.getProjectId() != null) {
                        if (organizationVO.getComguid().equals(vo.getProjectId())) {
                            System.out.println(vo);
                            if(vo.getCustomerType().equals("报备客户数")){
                                vo.getChildren().add(organizationVO);
                                reMoveProData.add(organizationVO);
                            }
                        }
                    }
                }
            }
            //处理单独项目权限
            proDataStatistics.removeAll(reMoveProData);
            for (SourceModeDataStatistics vo : proDataStatistics) {
                trees.get(0).getChildren().add(vo);
            }
            //获取用户权限 如果存在管理员可以查看系统 其他权限只能查看当前专员
            int type = projectCluesDao.getUserJobHsjt(SecurityUtils.getUserId());
            if(type > 0){
                proDataStatisticsXt = dataStatisticDao.getSourceModeDataStatisticsXt(paramMap);
                if(!CollectionUtils.isEmpty(proDataStatisticsXt)){
                    reTrees.addAll(proDataStatisticsXt);
                }
            }
            reTrees.addAll(trees.get(0).getChildren());
            Map reMap = new HashMap<>();
            reMap.put("list",reTrees);
            reMap.put("total",proDataStatistics != null ? proDataStatistics.size() + proDataStatisticsXt.size() : 0);
            return ResultBody.success(reMap);
        }
        Map reMap = new HashMap<>();
        reMap.put("list",new ArrayList<>());
        reMap.put("total",0);
        return ResultBody.success(reMap);
    }

    @Override
    public void sourceModeDataStatisticsExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);

        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        filedCodes.add("NAMET");
        filedNames.add("项目名称");
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        List<SourceModeDataStatistics> proDataStatistics = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        String projectIds = paramMap.get("projectIds")+"";
        String regionIds = paramMap.get("regionIds")+"";
        List<String> proIds = Arrays.asList(projectIds.split(","));
        List<String> qyIds = Arrays.asList(regionIds.split(","));
//        PageHelper.startPage(pageIndex, pageSize);
        proIds = Arrays.asList(projectIds.split(",")).stream().distinct().collect(Collectors.toList());
        qyIds = Arrays.asList(regionIds.split(",")).stream().distinct().collect(Collectors.toList());
        //获取人员最高权限
        Map map = new HashMap<>();
        String jobCode = "";
        map.put("UserId",SecurityUtils.getUserId());
        List<Map> userList = projectMapper.findFullPathAllInsZs(map);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("10001") || String.valueOf(userList.get(i).get("JobCode")).equals("jtsjg")){
                    if(level<= 1){
                        jobCode = "10001";
                        level = 3;
                    }
                }
            }
        }
        if(!jobCode.equals("10001")){
            map.put("UserName",SecurityUtils.getUsername());
            List<String> orgProjectIds = projectMapper.findOrgProjectId(map);
            // 复制一份原始集合用于操作
            List<String> finalProIds = new ArrayList<>(proIds);
            finalProIds.removeIf(x -> !orgProjectIds.contains(x)); // 使用 removeIf 方法来筛选保留满足条件的元素
            List<String> finalQyIds = new ArrayList<>(qyIds);
            finalQyIds.removeIf(x -> !orgProjectIds.contains(x));
            finalQyIds.stream().forEach(x->{
                finalProIds.addAll(projectCluesDao.getProListAll(x));
            });
            //为空异常处理
            finalProIds.add("");
            finalQyIds.add("");
            paramMap.put("proIds",finalProIds.stream().distinct().collect(Collectors.toList()));
            paramMap.put("qyIds",finalQyIds);
        }else {
            paramMap.put("proIds",proIds);
            paramMap.put("qyIds",qyIds);
        }
        //获取用户权限 如果存在管理员可以查看系统 其他权限只能查看当前专员
        int type = projectCluesDao.getUserJobHsjt(SecurityUtils.getUserId());
        if(type > 0){
            proDataStatistics.addAll(dataStatisticDao.getSourceModeDataStatisticsXt(paramMap));
        }
        proDataStatistics.addAll(dataStatisticDao.getSourceModeDataStatistics(paramMap).stream().filter(s -> !"-1".equals(s.getComguid())).collect(Collectors.toList()));
        List<String> proIdList = new ArrayList<>();
        excelName = "客户来源数据统计";
        proIdList = proIds;
        paramMap.put("proIds",proIds);
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(paramMap.get("userId")+"");
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            int rowNum = 1;
            headers = filedNames.toArray(new String[0]);

            for (SourceModeDataStatistics model : proDataStatistics) {
                model.setRownum(rowNum+"");
                Object[] oArray = model.toData1(filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                        && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,paramMap.get("startTime")+"-"+paramMap.get("endTime"));
                }else{
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String sourceModeDataStatisticsExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        String userId = SecurityUtils.getUserId();
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        String projectIds = paramMap.get("projectIds")+"";
        String regionIds = paramMap.get("regionIds")+"";
        List<String> proIds = Arrays.asList(projectIds.split(","));
        List<String> qyIds = Arrays.asList(regionIds.split(","));
        proIds = Arrays.asList(projectIds.split(","));
        qyIds = Arrays.asList(regionIds.split(","));
        paramMap.put("proIds",proIds);
        paramMap.put("qyIds",qyIds);
        //导出的文档下面的名字
        String excelName = "客户来源数据统计";
        List<String> proIdList = new ArrayList<>();
        proIdList = proIds;
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("11");
        excelExportLog.setSubType("SDS1");
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setSubTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(paramMap.get("userId")+"");
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setCreator(userId);
        excelExportLog.setDoSql(JSON.toJSONString(paramMap));
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
    public ResultBody groupDataStatistics(Map paramMap) {
        //判断是项目统计还是业务员统计 查询类型（1：项目 2：业务员）
        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        List<FollowUpRecordVO> proDataStatisticsGather = new ArrayList<>();
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        int i = (pageIndex - 1) * pageSize;
        paramMap.put("pageIndex",String.valueOf(i));
        paramMap.put("pageSize",pageSize);
        String total = "";
        Map reMap = new HashMap<>();

        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
//        PageHelper.startPage(pageIndex, pageSize);
        if ("1".equals(searchType)){//集团数据统计主表格查询
            paramMap.put("pageNum","");
            proDataStatistics = dataStatisticDao.getGroupDataStatistics(paramMap);
            reMap.put("list",proDataStatistics);
            reMap.put("total",'1');
            return ResultBody.success(reMap);
        }else if ("2".equals(searchType)){//数据查看详情查询
            List<String> userIds = dataStatisticDao.getUserAscInsGroup();
            if (CollectionUtils.isEmpty(userIds)){
                return ResultBody.success(new PageInfo<>(proDataStatistics));
            }
            paramMap.put("userIds",userIds);
            PageHelper.startPage(pageIndex, pageSize);
            proDataStatistics = dataStatisticDao.getGroupUserDataStatistics(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatistics));
        }else if ("3".equals(searchType)){//蓝色字体点击跟进客户台账查询
            PageHelper.startPage(pageIndex, pageSize);
            proDataStatisticsGather = dataStatisticDao.getGroupDataStatisticsGather(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGather));
        }else if ("4".equals(searchType)) {//数据查看详情查询弹出来表格里面蓝色字体点击查询
            PageHelper.startPage(pageIndex, pageSize);
            proDataStatisticsGather = dataStatisticDao.getGroupUserDataStatisticsGather(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGather));
        }else if ("5".equals(searchType)) {//蓝色字体点击报备客户台账查询
            PageHelper.startPage(pageIndex, pageSize);
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getGroupDataStatisticsGatherCc(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("6".equals(searchType)) {//数据查看详情查询弹出来表格里面蓝色字体点击查询
            PageHelper.startPage(pageIndex, pageSize);
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getGroupUserDataStatisticsGatherCc(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("7".equals(searchType)) {//蓝色字体点击报备客户台账查询
            PageHelper.startPage(pageIndex, pageSize);
            paramMap.put("gsType","group");
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getDataStatisticsGatherAt(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }else if ("8".equals(searchType)) {//数据查看详情查询弹出来表格里面蓝色字体点击查询
            PageHelper.startPage(pageIndex, pageSize);
            paramMap.put("gsType","groupUser");
            List<ProjectCluesNew> proDataStatisticsGatherCc = dataStatisticDao.getDataStatisticsGatherAt(paramMap);
            return ResultBody.success(new PageInfo<>(proDataStatisticsGatherCc));
        }
        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    @Override
    public void groupDataExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);

        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }
        List<Map> filedsMX = (List<Map>) paramMap.get("filedsMX");
        filedsMX = filedsMX.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodesMX = new ArrayList<>();
        List<String> filedNamesMX = new ArrayList<>();
        for (Map filed : filedsMX) {
            filedCodesMX.add(filed.get("filedCode")+"");
            filedNamesMX.add(filed.get("filedName")+"");
        }

        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //判断时间查询条件是否存在
        if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
        }else{
            String type = paramMap.get("type")+"";
            Date today = new Date();
            Date beginTime = null;
            Date endTime = null;
            //不存在计算时间  查询时间分类（1：当天 2：近7天 3：本月 4：本季度）
            if ("".equals(type) || "null".equals(type) || "1".equals(type)){
                //未传时间参数默认取当天的数据
                beginTime = DateUtil.beginOfDay(today);
            }else if ("2".equals(type)){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(today);
                calendar.add(calendar.DATE, -7);
                beginTime = DateUtil.beginOfDay(calendar.getTime());
            }else if ("3".equals(type)){
                beginTime = DateUtil.beginOfMonth(today);
            }else if ("4".equals(type)){
                beginTime = DateUtil.beginOfQuarter(today);
            }else{
                beginTime = DateUtil.beginOfDay(today);
            }
            endTime = DateUtil.endOfDay(today);
            paramMap.put("startTime",sf.format(beginTime));
            paramMap.put("endTime",sf.format(endTime));
        }
        //查询项目规则获取计算三个一
        ProjectRuleDetail projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        paramMap.put("countThreeOnes",projectRuleDetail.getCountThreeOnes());
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        if ("1".equals(searchType)){
            excelName = "集团数据统计";
            proDataStatistics = dataStatisticDao.getGroupDataStatistics(paramMap);
        }else{
            excelName = "集团成员数据统计";
            List<String> userIds = dataStatisticDao.getUserAscInsGroup();
            paramMap.put("userIds",userIds);
            proDataStatistics = dataStatisticDao.getGroupUserDataStatistics(paramMap);
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        excelExportLog.setCreator(paramMap.get("userId")+"");
        excelExportLog.setAreaName("万洋集团");
        excelExportLog.setProjectId("");
        excelExportLog.setProjectName("万洋集团");
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            int rowNum = 1;
            if ("1".equals(searchType)){
//                headers = proDataStatistics.get(0).cardTitle1;
                headers = filedNames.toArray(new String[0]);

                for (ProDataStatistics model : proDataStatistics) {
                    model.setRownum(rowNum+"");
                    Object[] oArray = model.toData1(filedCodes);
                    dataset.add(oArray);
                    rowNum++;
                }
            }else{
//                headers = proDataStatistics.get(0).cardTitle2;
                headers = filedNamesMX.toArray(new String[0]);
                for (ProDataStatistics model : proDataStatistics) {
                    model.setRownum(rowNum+"");
                    Object[] oArray = model.toData2(filedCodesMX);
                    dataset.add(oArray);
                    rowNum++;
                }
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                if (paramMap.get("startTime")!=null && !"".equals(paramMap.get("startTime")+"")
                        && paramMap.get("endTime")!=null && !"".equals(paramMap.get("endTime")+"")){
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,paramMap.get("startTime")+"-"+paramMap.get("endTime"));
                }else{
                    excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResultBody customerTradeCycleDataStatistics(Map paramMap) {
        //判断是周期查询类型（1：集团 2：区域 3：项目 4：招商团队 5：招商专员）
        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        String projectIds = paramMap.get("projectIds")+"";
        String regionIds = paramMap.get("regionIds")+"";
        if ("".equals(projectIds) || "null".equals(projectIds)){
            return ResultBody.error(-1200002,"项目ID未传！！");
        }
        List<String> proIds = Arrays.asList(projectIds.split(",")).stream().distinct().collect(Collectors.toList());
        List<String> yProIds = new ArrayList<>();
        List<String> qyIds = Arrays.asList(regionIds.split(",")).stream().distinct().collect(Collectors.toList());
//        if("2".equals(searchType)){
//            proIds = projectMapper.getAllProInsRegion(qyIds);
//        }else
        if ("3".equals(searchType)){
            String projectIdc = String.valueOf(paramMap.get("id"));
            List<String> proIdc = Arrays.asList(projectIdc.split("@@"));
            proIds = Collections.singletonList(proIdc.get(1));
            yProIds.add(proIdc.get(0));
        }
        //获取人员最高权限
        Boolean isManager = false;
        Map map = new HashMap<>();
        String jobCode = "";
        map.put("UserId",SecurityUtils.getUserId());
        List<Map> userList = projectMapper.findFullPathAllInsZs(map);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("10001") || String.valueOf(userList.get(i).get("JobCode")).equals("jtsjg")){
                    if(level<= 1){
                        jobCode = "10001";
                        level = 3;
                    }
                }
            }
        }
        if(!jobCode.equals("10001")){
            map.put("UserName",SecurityUtils.getUsername());
            List<String> orgProjectIds = projectMapper.findOrgProjectId(map);
            // 复制一份原始集合用于操作
            List<String> finalProIds = new ArrayList<>(proIds);
            finalProIds.removeIf(x -> !orgProjectIds.contains(x)); // 使用 removeIf 方法来筛选保留满足条件的元素
            List<String> finalQyIds = new ArrayList<>(qyIds);
            finalQyIds.removeIf(x -> !orgProjectIds.contains(x));
            finalQyIds.stream().forEach(x->{
                finalProIds.addAll(projectCluesDao.getProListAll(x));
            });
            //为空异常处理
            finalProIds.add("");
            finalQyIds.add("");
            paramMap.put("proIds",finalProIds.stream().distinct().collect(Collectors.toList()));
            paramMap.put("qyIds",finalQyIds);
        }else {
            isManager = true;
            paramMap.put("proIds",proIds);
            paramMap.put("qyIds",qyIds);
        }
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        List<ProDataStatistics> reList = new ArrayList<>();
        if ("1".equals(searchType)){//集团成交周期 查询集团和集团下区域
            paramMap.put("searchType","2");
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatistics(paramMap);
            if(!CollectionUtils.isEmpty(proDataStatistics)){
                reList.addAll(proDataStatistics);
            }
            paramMap.put("searchType","1");
            if(isManager){
                proDataStatistics = dataStatisticDao.customerTradeCycleDataStatistics(paramMap);
                if(!CollectionUtils.isEmpty(proDataStatistics)){
                    reList.addAll(proDataStatistics);
                }
            }
        }else if ("2".equals(searchType)){//区域成交周期 查询区域和区域下项目
            paramMap.put("searchType","3");
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatistics(paramMap);
            if(!CollectionUtils.isEmpty(proDataStatistics)){
                reList.addAll(proDataStatistics);
            }
            paramMap.put("searchType","2");
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatistics(paramMap);
            if(!CollectionUtils.isEmpty(proDataStatistics)){
                reList.addAll(proDataStatistics);
            }
        }else if ("3".equals(searchType)){//项目成交周期 查询项目和项目下招商团队
            paramMap.put("searchType","4");
            if(isManager){
                //查询招商团队
                List<String> teamIds = projectMapper.getTeamIdsByProject(paramMap);
                paramMap.put("teamIds",teamIds);
            }else {
                Map neMap = new HashMap<>();
                neMap.put("UserName",SecurityUtils.getUsername());
                List<String> fullpath = projectMapper.findFullPathNotApply(neMap);
                List<ProjectVO> projectList = new ArrayList<>();
                StringBuffer sb = new StringBuffer();
                if (fullpath==null || fullpath.size()==0){
                    return ResultBody.success("400");
                }else {
                    for (int i = 0; i < fullpath.size(); i++) {
                        if (i == 0) {
                            sb.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                        } else {
                            sb.append(" or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                        }
                    }
                    neMap.put("where", sb.toString());
                    projectList = projectMapper.getProjectListByUserName(neMap);
                }
                //查询招商团队
                neMap.put("proIds",projectList);
                List<String> teamIds = projectMapper.getTeamIdsByProject(neMap);
                String teamIdsStr = projectMapper.getTeamIdsByQxProject(neMap);
                if(StringUtils.isEmpty(teamIdsStr)){
                    teamIds.addAll(Arrays.asList(teamIdsStr.split(",")));
                }
                paramMap.put("teamIds",teamIds);
            }
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatistics(paramMap);
            if(!CollectionUtils.isEmpty(proDataStatistics)){
                reList.addAll(proDataStatistics);
            }
            paramMap.put("searchType","3");
            paramMap.put("yProIds",yProIds);
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatistics(paramMap);
            if(!CollectionUtils.isEmpty(proDataStatistics)){
                reList.addAll(proDataStatistics);
            }
        }else if ("4".equals(searchType)) {//招商组成交周期 查询招商团队和招商团队下招商专员
            paramMap.put("searchType","5");
            //查询招商专员
            List<String> userIds = projectCluesDao.getTeamUser(String.valueOf(paramMap.get("id")));
            paramMap.put("userIds",userIds);
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatistics(paramMap);
            if(!CollectionUtils.isEmpty(proDataStatistics)){
                reList.addAll(proDataStatistics);
            }
            paramMap.put("searchType","4");
            List<String> teamIds = new ArrayList<>();
            teamIds.add(String.valueOf(paramMap.get("id")));
            paramMap.put("teamIds", teamIds);
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatistics(paramMap);
            if(!CollectionUtils.isEmpty(proDataStatistics)){
                reList.addAll(proDataStatistics);
            }
        }
        return ResultBody.success(reList);
    }

    @Override
    public ResultBody customerTradeCycleDataStatisticsGather(Map paramMap) {
        //判断是周期查询类型（1：集团 2：区域 3：项目 4：招商组 5：招商专员）
        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        int pageIndex = 1;
        int pageSize = 10;
        if (paramMap.get("pageNum")!=null){
            pageIndex = Integer.parseInt(paramMap.get("pageNum")+"");
        }
        if (paramMap.get("pageSize")!=null){
            pageSize = Integer.parseInt(paramMap.get("pageSize")+"");
        }
        String projectIds = paramMap.get("projectIds")+"";
        String regionIds = paramMap.get("regionIds")+"";
        if ("".equals(projectIds) || "null".equals(projectIds)){
            return ResultBody.error(-1200002,"项目ID未传！！");
        }
        List<String> proIds = Arrays.asList(projectIds.split(",")).stream().distinct().collect(Collectors.toList());
        List<String> qyIds = Arrays.asList(regionIds.split(",")).stream().distinct().collect(Collectors.toList());
//        if("2".equals(searchType)){
//            proIds = projectMapper.getAllProInsRegion(qyIds);
//        }
        //获取人员最高权限
        Map map = new HashMap<>();
        String jobCode = "";
        map.put("UserId",SecurityUtils.getUserId());
        List<Map> userList = projectMapper.findFullPathAllInsZs(map);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("10001") || String.valueOf(userList.get(i).get("JobCode")).equals("jtsjg")){
                    if(level<= 1){
                        jobCode = "10001";
                        level = 3;
                    }
                }
            }
        }
        if(!jobCode.equals("10001")){
            map.put("UserName",SecurityUtils.getUsername());
            List<String> orgProjectIds = projectMapper.findOrgProjectId(map);
            // 复制一份原始集合用于操作
            List<String> finalProIds = new ArrayList<>(proIds);
            finalProIds.removeIf(x -> !orgProjectIds.contains(x)); // 使用 removeIf 方法来筛选保留满足条件的元素
            List<String> finalQyIds = new ArrayList<>(qyIds);
            finalQyIds.removeIf(x -> !orgProjectIds.contains(x));
            finalQyIds.stream().forEach(x->{
                finalProIds.addAll(projectCluesDao.getProListAll(x));
            });
            //为空异常处理
            finalProIds.add("");
            finalQyIds.add("");
            paramMap.put("proIds",finalProIds.stream().distinct().collect(Collectors.toList()));
            paramMap.put("qyIds",finalQyIds);
        }else {
            paramMap.put("proIds",proIds);
            paramMap.put("qyIds",qyIds);
        }
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        PageHelper.startPage(pageIndex, pageSize);
        if ("1".equals(searchType)){//集团成交周期
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatisticsGather(paramMap);
            proDataStatistics.stream().forEach(x->{
                //获取集团最早报备时间
                String reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(x.getOpportunityClueId(),"1",null);
                //获取集团最早成交时间
                String contractDate = messageMapper.getCustomerFirstContractDate(x.getOpportunityClueId(),"1",null);
                //获取客户时间范围内的到访次数
                x.setVisitCount(dataStatisticDao.getCustomerFollowVisitCount(x.getOpportunityClueId(),"1",reportCreateTime,contractDate,null));
            });
        }else if ("2".equals(searchType)){//区域成交周期
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatisticsGather(paramMap);
            proDataStatistics.stream().forEach(x->{
                //获取集团最早报备时间
                String reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(x.getOpportunityClueId(),"2",paramMap.get("id")+"");
                //获取集团最早成交时间
                String contractDate = messageMapper.getCustomerFirstContractDate(x.getOpportunityClueId(),"2",paramMap.get("id")+"");
                //获取客户时间范围内的到访次数
                x.setVisitCount(dataStatisticDao.getCustomerFollowVisitCount(x.getOpportunityClueId(),"2",reportCreateTime,contractDate,paramMap.get("id")+""));
            });
        }else if ("3".equals(searchType)){//项目成交周期
            String projectIdc = String.valueOf(paramMap.get("id"));
            List<String> proIdc = Arrays.asList(projectIdc.split("@@"));
            paramMap.put("oppProId",proIdc.get(0));
            paramMap.put("tradeProId",proIdc.get(1));
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatisticsGather(paramMap);
            proDataStatistics.stream().forEach(x->{
                //获取集团最早报备时间
                String reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(x.getOpportunityClueId(),"3",paramMap.get("id")+"");
                //获取集团最早成交时间
                String contractDate = messageMapper.getCustomerFirstContractDate(x.getOpportunityClueId(),"3",paramMap.get("id")+"");
                //获取客户时间范围内的到访次数
                x.setVisitCount(dataStatisticDao.getCustomerFollowVisitCount(x.getOpportunityClueId(),"3",reportCreateTime,contractDate,paramMap.get("id")+""));
            });
        }else if ("4".equals(searchType)) {//招商组成交周期
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatisticsGather(paramMap);
            proDataStatistics.stream().forEach(x->{
                //获取集团最早报备时间
                String reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(x.getOpportunityClueId(),"4",paramMap.get("id")+"");
                //获取集团最早成交时间
                String contractDate = messageMapper.getCustomerFirstContractDate(x.getOpportunityClueId(),"4",paramMap.get("id")+"");
                //获取客户时间范围内的到访次数
                x.setVisitCount(dataStatisticDao.getCustomerFollowVisitCount(x.getOpportunityClueId(),"4",reportCreateTime,contractDate,paramMap.get("id")+""));
            });
        }
        return ResultBody.success(new PageInfo<>(proDataStatistics));
    }

    @Override
    public void customerTradeCycleDataStatisticsGatherExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        Map paramMap = JSONObject.parseObject(excelForm, Map.class);

        List<Map> fileds = (List<Map>) paramMap.get("fileds");
        fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

        List<String> filedCodes = new ArrayList<>();
        List<String> filedNames = new ArrayList<>();
        for (Map filed : fileds) {
            filedCodes.add(filed.get("filedCode")+"");
            filedNames.add(filed.get("filedName")+"");
        }

        String searchType = paramMap.get("searchType")+"";
        List<ProDataStatistics> proDataStatistics = new ArrayList<>();
        String projectIds = paramMap.get("projectIds")+"";
        String regionIds = paramMap.get("regionIds")+"";
        if ("".equals(projectIds) || "null".equals(projectIds)){
            return ;
        }
        List<String> proIds = Arrays.asList(projectIds.split(",")).stream().distinct().collect(Collectors.toList());
        List<String> qyIds = Arrays.asList(regionIds.split(",")).stream().distinct().collect(Collectors.toList());
//        if("2".equals(searchType)){
//            proIds = projectMapper.getAllProInsRegion(qyIds);
//        }
        //获取人员最高权限
        Map map = new HashMap<>();
        String jobCode = "";
        map.put("UserId",SecurityUtils.getUserId());
        List<Map> userList = projectMapper.findFullPathAllInsZs(map);
        int level = 0;
        if(!CollectionUtils.isEmpty(userList)){
            for(int i = 0;i<userList.size();i++){
                if(String.valueOf(userList.get(i).get("JobCode")).equals("10001") || String.valueOf(userList.get(i).get("JobCode")).equals("jtsjg")){
                    if(level<= 1){
                        jobCode = "10001";
                        level = 3;
                    }
                }
            }
        }
        if(!jobCode.equals("10001")){
            map.put("UserName",SecurityUtils.getUsername());
            List<String> orgProjectIds = projectMapper.findOrgProjectId(map);
            // 复制一份原始集合用于操作
            List<String> finalProIds = new ArrayList<>(proIds);
            finalProIds.removeIf(x -> !orgProjectIds.contains(x)); // 使用 removeIf 方法来筛选保留满足条件的元素
            List<String> finalQyIds = new ArrayList<>(qyIds);
            finalQyIds.removeIf(x -> !orgProjectIds.contains(x));
            finalQyIds.stream().forEach(x->{
                finalProIds.addAll(projectCluesDao.getProListAll(x));
            });
            //为空异常处理
            finalProIds.add("");
            finalQyIds.add("");
            paramMap.put("proIds",finalProIds.stream().distinct().collect(Collectors.toList()));
            paramMap.put("qyIds",finalQyIds);
        }else {
            paramMap.put("proIds",proIds);
            paramMap.put("qyIds",qyIds);
        }
        //导出的文档下面的名字
        String excelName = null;
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] headers = null;
        //处理客户行业
        List<String> belongIndustriseList = new ArrayList<>();
        List<String> belongIndustriseTwoList = new ArrayList<>();
        List<String> belongIndustriseThreeList = new ArrayList<>();
        List<String> belongIndustriseFourList = new ArrayList<>();
        List<List<String>> customerIndustryArr = (List<List<String>>) paramMap.get("customerIndustryArr");
        if(!CollectionUtils.isEmpty(customerIndustryArr)){
            customerIndustryArr.stream().forEach(x->{
                List<String> arr = x;
                final int[] j = {1};
                arr.stream().forEach(y->{
                    if(j[0] == 1){
                        if(!belongIndustriseList.contains(y)){
                            belongIndustriseList.add(y);
                        }
                        j[0]++;
                    }else if (j[0] == 2){
                        if(!belongIndustriseTwoList.contains(y)){
                            belongIndustriseTwoList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 3){
                        if(!belongIndustriseThreeList.contains(y)){
                            belongIndustriseThreeList.add(y);
                        }
                        j[0]++;
                    }else if(j[0] == 4){
                        if(!belongIndustriseFourList.contains(y)){
                            belongIndustriseFourList.add(y);
                        }
                        j[0]++;
                    }
                });
            });
        }
        paramMap.put("belongIndustriseList",belongIndustriseList);
        paramMap.put("BelongIndustriseTwoList",belongIndustriseTwoList);
        paramMap.put("BelongIndustriseThreeList",belongIndustriseThreeList);
        paramMap.put("BelongIndustriseFourList",belongIndustriseFourList);
        List<String> source = (List<String>) paramMap.get("source");
        paramMap.put("source",source);
        excelName = "项目成交明细";
        if ("1".equals(searchType)){//集团成交周期
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatisticsGather(paramMap);
            proDataStatistics.stream().forEach(x->{
                //获取集团最早报备时间
                String reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(x.getOpportunityClueId(),"1",null);
                //获取集团最早成交时间
                String contractDate = messageMapper.getCustomerFirstContractDate(x.getOpportunityClueId(),"1",null);
                //获取客户时间范围内的到访次数
                x.setVisitCount(dataStatisticDao.getCustomerFollowVisitCount(x.getOpportunityClueId(),"1",reportCreateTime,contractDate,null));
            });
        }else if ("2".equals(searchType)){//区域成交周期
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatisticsGather(paramMap);
            proDataStatistics.stream().forEach(x->{
                //获取集团最早报备时间
                String reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(x.getOpportunityClueId(),"2",paramMap.get("id")+"");
                //获取集团最早成交时间
                String contractDate = messageMapper.getCustomerFirstContractDate(x.getOpportunityClueId(),"2",paramMap.get("id")+"");
                //获取客户时间范围内的到访次数
                x.setVisitCount(dataStatisticDao.getCustomerFollowVisitCount(x.getOpportunityClueId(),"2",reportCreateTime,contractDate,paramMap.get("id")+""));
            });
        }else if ("3".equals(searchType)){//项目成交周期
            String projectIdc = String.valueOf(paramMap.get("id"));
            List<String> proIdc = Arrays.asList(projectIdc.split("@@"));
            paramMap.put("oppProId",proIdc.get(0));
            paramMap.put("tradeProId",proIdc.get(1));
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatisticsGather(paramMap);
            proDataStatistics.stream().forEach(x->{
                //获取集团最早报备时间
                String reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(x.getOpportunityClueId(),"3",paramMap.get("id")+"");
                //获取集团最早成交时间
                String contractDate = messageMapper.getCustomerFirstContractDate(x.getOpportunityClueId(),"3",paramMap.get("id")+"");
                //获取客户时间范围内的到访次数
                x.setVisitCount(dataStatisticDao.getCustomerFollowVisitCount(x.getOpportunityClueId(),"3",reportCreateTime,contractDate,paramMap.get("id")+""));
            });
        }else if ("4".equals(searchType)) {//招商组成交周期
            proDataStatistics = dataStatisticDao.customerTradeCycleDataStatisticsGather(paramMap);
            proDataStatistics.stream().forEach(x->{
                //获取集团最早报备时间
                String reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(x.getOpportunityClueId(),"4",paramMap.get("id")+"");
                //获取集团最早成交时间
                String contractDate = messageMapper.getCustomerFirstContractDate(x.getOpportunityClueId(),"4",paramMap.get("id")+"");
                //获取客户时间范围内的到访次数
                x.setVisitCount(dataStatisticDao.getCustomerFollowVisitCount(x.getOpportunityClueId(),"4",reportCreateTime,contractDate,paramMap.get("id")+""));
            });
        }
        //保存导出日志
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainTypeDesc(excelName);
        excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames((List<String>) paramMap.get("proIds"));
        excelExportLog.setCreator(paramMap.get("userId")+"");
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setExportStatus("2");
        excelExportLog.setDoSql(excelForm);
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
        if (proDataStatistics!=null && proDataStatistics.size()>0){
            int rowNum = 1;
            headers = filedNames.toArray(new String[0]);

            for (ProDataStatistics model : proDataStatistics) {
                model.setRownum(rowNum+"");
                Object[] oArray = model.toData5(filedCodes);
                dataset.add(oArray);
                rowNum++;
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel(excelName, headers,dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ProDataStatistics tlDataStatistics1(String type,List<ProDataStatistics> proDataStatistics){
        ProDataStatistics tlMap = new ProDataStatistics();
        if(type.equals("pro") || type.equals("qy") || type.equals("gr") || type.equals("zy")){
            int totalOppSum = 0;
            int totalAddSum = 0;
            int totalOrderSum = 0;
            int totalCallCount = 0;
            int totalFirstComeVisitCount = 0;
            int totalThreeOnesBeforeReComeVisitCount = 0;
            int totalThreeOnesAfterReComeVisitCount = 0;
            int totalSjThreeOnesCount = 0;
            Double totalJsThreeOnesCount = 0.0;
            int totalFirstVisitCount = 0;
            int totalReVisitCount = 0;
            int totalFollowVisitCount = 0;
            int totalACount = 0;
            int totalBCount = 0;
            int totalCCount = 0;
            int totalDCount = 0;

            for (ProDataStatistics data : proDataStatistics) {
                totalOppSum += Integer.parseInt(data.getOppSum());
                totalAddSum += Integer.parseInt(data.getAddSum());
                totalOrderSum += Integer.parseInt(data.getOrderSum());
                totalCallCount += Integer.parseInt(data.getCallCount());
                totalFirstComeVisitCount += Integer.parseInt(data.getFirstComeVisitCount());
                totalThreeOnesBeforeReComeVisitCount += Integer.parseInt(data.getThreeOnesBeforeReComeVisitCount());
                totalThreeOnesAfterReComeVisitCount += Integer.parseInt(data.getThreeOnesAfterReComeVisitCount());
                totalSjThreeOnesCount += Integer.parseInt(data.getSjThreeOnesCount());
                totalJsThreeOnesCount += Double.parseDouble(data.getJsThreeOnesCount());
                totalFirstVisitCount += Integer.parseInt(data.getFirstVisitCount());
                totalReVisitCount += Integer.parseInt(data.getReVisitCount());
                totalFollowVisitCount += Integer.parseInt(data.getFollowVisitCount());
                totalACount += Integer.parseInt(data.getACount());
                totalBCount += Integer.parseInt(data.getBCount());
                totalCCount += Integer.parseInt(data.getCCount());
                totalDCount += Integer.parseInt(data.getDCount());
            }

            tlMap.setProjectId("");
            tlMap.setName("总计");
            tlMap.setAreaName("");
            tlMap.setOppSum(String.valueOf(totalOppSum));
            tlMap.setAddSum(String.valueOf(totalAddSum));
            tlMap.setOrderSum(String.valueOf(totalOrderSum));
            tlMap.setCallCount(String.valueOf(totalCallCount));
            tlMap.setFirstComeVisitCount(String.valueOf(totalFirstComeVisitCount));
            tlMap.setThreeOnesBeforeReComeVisitCount(String.valueOf(totalThreeOnesBeforeReComeVisitCount));
            tlMap.setThreeOnesAfterReComeVisitCount(String.valueOf(totalThreeOnesAfterReComeVisitCount));
            tlMap.setSjThreeOnesCount(String.valueOf(totalSjThreeOnesCount));
            tlMap.setJsThreeOnesCount(df.format(totalJsThreeOnesCount));
            tlMap.setFirstVisitCount(String.valueOf(totalFirstVisitCount));
            tlMap.setReVisitCount(String.valueOf(totalReVisitCount));
            tlMap.setFollowVisitCount(String.valueOf(totalFollowVisitCount));
            tlMap.setACount(String.valueOf(totalACount));
            tlMap.setBCount(String.valueOf(totalBCount));
            tlMap.setCCount(String.valueOf(totalCCount));
            tlMap.setDCount(String.valueOf(totalDCount));

        }
        return tlMap;
    }

    public DealStatistics tlDataStatistics2(String type,List<DealStatistics> dealStatistics){
        DealStatistics tlMap = new DealStatistics();
        if (type.equals("proDeal") || type.equals("qyDeal")){
            Double dealCount = 0.0;
            Double dealAmount = 0.0;
            for (DealStatistics data : dealStatistics) {
                dealCount += Double.parseDouble(data.getDealCount());
                dealAmount += Double.parseDouble(data.getDealAmount().replace(",",""));
            }
            tlMap.setProjectId("");
            tlMap.setName("总计");
            tlMap.setDealCount(String.valueOf(dealCount));
            tlMap.setDealAmount(String.format("%,.2f", dealAmount));// 将计算后的值以带逗号的形式设置回去

        }
        return tlMap;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
