package cn.visolink.system.channel.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.dao.PublicpoolDao;
import cn.visolink.system.channel.model.Publicpool;
import cn.visolink.system.channel.model.form.CustomerDistributionRecordsForm;
import cn.visolink.system.channel.model.form.PublicpoolForm;
import cn.visolink.system.channel.model.form.RedistributionBatchForm;
import cn.visolink.system.channel.model.vo.CustomerDistributionRecordsVO;
import cn.visolink.system.channel.model.vo.ProjectCluesVO;
import cn.visolink.system.channel.model.vo.PublicpoolVO;
import cn.visolink.system.channel.model.vo.RedistributionBatchVO;
import cn.visolink.system.channel.service.PublicpoolService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.wicket.core.dbhelper.sql.DBSQLServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * Publicpool服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2019-09-02
 */
@Service
public class PublicpoolServiceImpl extends ServiceImpl<PublicpoolDao, Publicpool> implements PublicpoolService {

        @Autowired
        DBSQLServiceImpl dbsqlService;
        @Autowired
        private PublicpoolDao publicpoolDao;
        @Autowired
        private ProjectCluesDao projectCluesDao;

        @Autowired
        private ExcelImportMapper excelImportMapper;

        private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private DecimalFormat df = new DecimalFormat("#0.00");

        @Autowired
        private RedisUtil redisUtil;

        @Override
        public PageInfo<PublicpoolVO> selectAllPublic(PublicpoolForm publicpoolForm) {
                PageHelper.startPage(Integer.parseInt(publicpoolForm.getPageNum()),Integer.parseInt(publicpoolForm.getPageSize()));
                com.github.pagehelper.Page<PublicpoolVO> list = publicpoolDao.queryPublicPool(publicpoolForm);
                return new PageInfo<PublicpoolVO>(list);
        }

        @Override
        public List<Map> getClueResetCause(Map map) {
                List<Map> list = publicpoolDao.getClueResetCause(map);
                return list;
        }

        @Override
        public void publicExport(HttpServletRequest request, HttpServletResponse response, PublicpoolForm publicpoolForm) {
                List<Map> fileds = publicpoolForm.getFileds();
                fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

                List<String> filedCodes = new ArrayList<>();
                List<String> filedNames = new ArrayList<>();
                for (Map filed : fileds) {
                        filedCodes.add(filed.get("filedCode")+"");
                        filedNames.add(filed.get("filedName")+"");
                }


                String userId = request.getHeader("userId");
                List<PublicpoolVO> publicPoolVOList = new ArrayList<>();
                if(!StringUtils.isBlank(publicpoolForm.getRedistributionId())){
                        publicpoolForm.setRedistributionIdList(Arrays.asList(publicpoolForm.getRedistributionId().split(",")));
                }
                //处理参数（逗号分割的装换为sql查询所需格式）
                if (!StringUtils.isBlank(publicpoolForm.getClueStatus())){
                        String[] ids = publicpoolForm.getClueStatus().split(",");
                        String ClueStatus ="'" + StringUtils.join(ids,"','")+"'";
                        publicpoolForm.setClueStatus(ClueStatus);
                }
                if (!StringUtils.isBlank(publicpoolForm.getReason())){
                        String[] ids = publicpoolForm.getReason().split(",");
                        String Reason ="'" + StringUtils.join(ids,"','")+"'";
                        publicpoolForm.setReason(Reason);
                }
                if (publicpoolForm.getSearch() != null && !"".equals(publicpoolForm.getSearch())){
                        String search = publicpoolForm.getSearch();
                        //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
                        if (search.matches("[0-9]+")) {
                                publicpoolForm.setCustomerMobile(search);
                        } else {
                                publicpoolForm.setCustomerName(search);
                        }
                }
                List<String> projectList = new ArrayList<>();
                if (publicpoolForm.getProjectId() != null && publicpoolForm.getProjectId() != "") {
                        projectList = Arrays.asList(String.valueOf(publicpoolForm.getProjectId()).split(","));
                        publicpoolForm.setProjectList(projectList);
                }
                if("2".equals(publicpoolForm.getPoolType())){
                        projectList = publicpoolDao.getProListAllRegions(publicpoolForm.getAreaList());
                        publicpoolForm.setProjectList(projectList);
                }
                //处理客户行业
                List<String> belongIndustriseList = new ArrayList<>();
                List<String> belongIndustriseTwoList = new ArrayList<>();
                List<String> belongIndustriseThreeList = new ArrayList<>();
                List<String> belongIndustriseFourList = new ArrayList<>();
                if(!CollectionUtils.isEmpty(publicpoolForm.getCustomerIndustryArr())){
                        publicpoolForm.getCustomerIndustryArr().stream().forEach(x->{
                                List<String> arr = x;
                                final int[] i = {1};
                                arr.stream().forEach(y->{
                                        if(i[0] == 1){
                                                if(!belongIndustriseList.contains(y)){
                                                        belongIndustriseList.add(y);
                                                }
                                                i[0]++;
                                        }else if (i[0] == 2){
                                                if(!belongIndustriseTwoList.contains(y)){
                                                        belongIndustriseTwoList.add(y);
                                                }
                                                i[0]++;
                                        }else if(i[0] == 3){
                                                if(!belongIndustriseThreeList.contains(y)){
                                                        belongIndustriseThreeList.add(y);
                                                }
                                                i[0]++;
                                        }else if(i[0] == 4){
                                                if(!belongIndustriseFourList.contains(y)){
                                                        belongIndustriseFourList.add(y);
                                                }
                                                i[0]++;
                                        }
                                });
                        });
                }
                publicpoolForm.setBelongIndustriseList(belongIndustriseList);
                publicpoolForm.setBelongIndustriseTwoList(belongIndustriseTwoList);
                publicpoolForm.setBelongIndustriseThreeList(belongIndustriseThreeList);
                publicpoolForm.setBelongIndustriseFourList(belongIndustriseFourList);
                //导出的文档下面的名字
                String excelName = "公共池记录";
                ArrayList<Object[]> dataset = new ArrayList<>();
                String[] headers = null;
                publicPoolVOList = publicpoolDao.queryPublicPool(publicpoolForm);
                //判断时间查询条件是否存在
                //保存导出日志
                ExcelExportLog excelExportLog = new ExcelExportLog();
                String id = UUID.randomUUID().toString();
                excelExportLog.setId(id);
                excelExportLog.setMainTypeDesc(excelName);
                excelExportLog.setExportType("3");//导出类型（1：隐号 2：全号 3：无限制）
                excelExportLog.setCreator(userId);
                excelExportLog.setExportStatus("2");
                excelExportLog.setDoSql(JSON.toJSONString(publicpoolForm));
                //保存任务表
                excelImportMapper.addExcelExportLog(excelExportLog);

                if (publicPoolVOList != null && publicPoolVOList.size() > 0){
                        String isAllStr = publicpoolForm.getIsAll();
                        boolean isAll = true;
                        if ("1".equals(isAllStr)) isAll = false;
//                        headers = publicPoolVOList.get(0).courtCaseTitle;
                        headers = filedNames.toArray(new String[0]);

                        int rowNum = 1;
                        for (PublicpoolVO model : publicPoolVOList) {
                                model.setRownum(rowNum);
                                Object[] oArray = model.toPublicData(isAll,filedCodes);
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
        public String publicExportNew(HttpServletRequest request, HttpServletResponse response, PublicpoolForm publicpoolForm) {
                String userId = SecurityUtils.getUserId();
                //导出的文档下面的名字
                String excelName = "公客池列表";
                //保存导出日志
                ExcelExportLog excelExportLog = new ExcelExportLog();
                String id = UUID.randomUUID().toString();
                excelExportLog.setId(id);
                excelExportLog.setMainType("11");
                excelExportLog.setSubType("GC1");
                excelExportLog.setMainTypeDesc(excelName);
                excelExportLog.setSubTypeDesc(excelName);
                excelExportLog.setExportType(publicpoolForm.getIsAll());//导出类型（1：隐号 2：全号 3：无限制）
                excelExportLog.setIsAsyn("1");
                excelExportLog.setExportStatus("1");
                //获取项目集合数据（事业部，项目Id,项目名称）
                if (!CollectionUtils.isEmpty(publicpoolForm.getProjectList())) {
                        Map proMap = excelImportMapper.getAreaNameAndProNames(publicpoolForm.getProjectList());
                        excelExportLog.setAreaName(proMap.get("areaName")+"");
                        excelExportLog.setProjectId(proMap.get("projectId")+"");
                        excelExportLog.setProjectName(proMap.get("projectName")+"");
                }
                excelExportLog.setCreator(userId);
                excelExportLog.setDoSql(JSON.toJSONString(publicpoolForm));
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
        public List<PublicpoolVO> getAllPublicList(PublicpoolForm publicpoolForm) {
                if(!StringUtils.isBlank(publicpoolForm.getRedistributionId())){
                        publicpoolForm.setRedistributionIdList(Arrays.asList(publicpoolForm.getRedistributionId().split(",")));
                }
                //处理参数（逗号分割的装换为sql查询所需格式）
                if (!StringUtils.isBlank(publicpoolForm.getClueStatus())){
                        String[] ids = publicpoolForm.getClueStatus().split(",");
                        String ClueStatus ="'" + StringUtils.join(ids,"','")+"'";
                        publicpoolForm.setClueStatus(ClueStatus);
                }
                if (!StringUtils.isBlank(publicpoolForm.getReason())){
                        String[] ids = publicpoolForm.getReason().split(",");
                        String Reason ="'" + StringUtils.join(ids,"','")+"'";
                        publicpoolForm.setReason(Reason);
                }
                if (publicpoolForm.getSearch() != null && !"".equals(publicpoolForm.getSearch())){
                        String search = publicpoolForm.getSearch();
                        //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
                        if (search.matches("[0-9]+")) {
                                publicpoolForm.setCustomerMobile(search);
                        } else {
                                publicpoolForm.setCustomerName(search);
                        }
                }
                List<String> projectList = new ArrayList<>();
                if (publicpoolForm.getProjectId() != null && publicpoolForm.getProjectId() != "") {
                        projectList = Arrays.asList(String.valueOf(publicpoolForm.getProjectId()).split(","));
                        publicpoolForm.setProjectList(projectList);
                }
                if("1".equals(publicpoolForm.getPoolType()) && CollectionUtils.isEmpty(publicpoolForm.getProjectList())){
                    return new ArrayList<>();
                }
                if("2".equals(publicpoolForm.getPoolType()) && CollectionUtils.isEmpty(publicpoolForm.getAreaList())){
                    return new ArrayList<>();
                }
                if("2".equals(publicpoolForm.getPoolType())){
                        projectList = publicpoolDao.getProListAllRegions(publicpoolForm.getAreaList());
                        publicpoolForm.setProjectList(projectList);
                }
                //处理客户行业
                List<String> belongIndustriseList = new ArrayList<>();
                List<String> belongIndustriseTwoList = new ArrayList<>();
                List<String> belongIndustriseThreeList = new ArrayList<>();
                List<String> belongIndustriseFourList = new ArrayList<>();
                if(!CollectionUtils.isEmpty(publicpoolForm.getCustomerIndustryArr())){
                        publicpoolForm.getCustomerIndustryArr().stream().forEach(x->{
                                List<String> arr = x;
                                final int[] i = {1};
                                arr.stream().forEach(y->{
                                        if(i[0] == 1){
                                                if(!belongIndustriseList.contains(y)){
                                                        belongIndustriseList.add(y);
                                                }
                                                i[0]++;
                                        }else if (i[0] == 2){
                                                if(!belongIndustriseTwoList.contains(y)){
                                                        belongIndustriseTwoList.add(y);
                                                }
                                                i[0]++;
                                        }else if(i[0] == 3){
                                                if(!belongIndustriseThreeList.contains(y)){
                                                        belongIndustriseThreeList.add(y);
                                                }
                                                i[0]++;
                                        }else if(i[0] == 4){
                                                if(!belongIndustriseFourList.contains(y)){
                                                        belongIndustriseFourList.add(y);
                                                }
                                                i[0]++;
                                        }
                                });
                        });
                }
                publicpoolForm.setBelongIndustriseList(belongIndustriseList);
                publicpoolForm.setBelongIndustriseTwoList(belongIndustriseTwoList);
                publicpoolForm.setBelongIndustriseThreeList(belongIndustriseThreeList);
                publicpoolForm.setBelongIndustriseFourList(belongIndustriseFourList);
                PageHelper.startPage(Integer.parseInt(publicpoolForm.getPageNum()),Integer.parseInt(publicpoolForm.getPageSize()));
                return  publicpoolDao.queryPublicPool(publicpoolForm);

        }

        @Override
        public ResultBody getFunctionObtainZs(Map map) {
                //获取当前登录人的总监身份
                List<Map> zjList = publicpoolDao.getUserZjJobOrgInfo(map);
                if(CollectionUtils.isEmpty(zjList)){
                        return ResultBody.error(400,"没有找到当前登录人的总监身份");
                }else {
                        map.put("zjList",zjList);
                        //根据总监身份对应项目 获取项目下专员身份和对应岗位信息 权限信息
                        return ResultBody.success(publicpoolDao.getJobsAppFunctionRel(map));
                }
        }

        @Override
        public ResultBody saveFunctionObtainZs(Map map) {
                String type = map.get("type")+"";
                int i = 0;
                if("0".equals(type)){
                        i = publicpoolDao.delJobsAppFunctionRel(map);
                }else if("1".equals(type)){
                        i = publicpoolDao.saveJobsAppFunctionRel(map);
                }
                return i > 0 ? ResultBody.success("授权成功") : ResultBody.error(400,"授权失败");
        }

        @Override
        public ResultBody getProjectListHasObtainCst(Map map) {
                //根据公客池类型判断可选择项目
                String poolType = map.get("poolType")+"";
                if("1".equals(poolType)){
                    //项目池  当前项目和联动项目
                    List<String> proList = new ArrayList<>();
                    proList.add(map.get("projectId")+"");
                    map.put("proList",proList);
                }else if("2".equals(poolType)){
                    //区域池 当前区域下项目
                    List<String> proList =  publicpoolDao.getProListAll(map.get("projectId")+"");
                    map.put("proList", proList);
                }
                List<Map> list = publicpoolDao.getProjectListHasObtainCst(map);
                return CollectionUtils.isEmpty(list) ? ResultBody.error(400,"暂无捞取权限 请联系总监！") : ResultBody.success(list);
        }


}
