package cn.visolink.system.activity.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.BaseResultCodeEnum;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.activity.dao.ActivityInfoDao;
import cn.visolink.system.activity.model.ActivityAddress;
import cn.visolink.system.activity.model.ActivityInfo;
import cn.visolink.system.activity.model.ActivityMaterial;
import cn.visolink.system.activity.model.form.ActivityInfoForm;
import cn.visolink.system.activity.model.form.RidActivityFrom;
import cn.visolink.system.activity.model.vo.*;
import cn.visolink.system.activity.service.ActivityInfoService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import cn.visolink.utils.*;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.CessException;
import io.cess.util.PropertyUtil;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * <p>
 * ActivityInfo服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2020-05-25
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoDao, ActivityInfo> implements ActivityInfoService {
    @Autowired
    private ActivityInfoDao activityInfoDao;

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${BROKERURL}")
    private String url;

    @Value("${UPDATEREDISURL}")
    private String updateRedisUrl;


    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("#0.00");
    @Override
    public ActivityInfoVO selectById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        ActivityInfo data = baseMapper.selectById(id);
        ActivityInfoVO result = PropertyUtil.copy(data, ActivityInfoVO.class);
        return result;
    }

    @Override
    public List<ActivityInfoVO> selectAll(ActivityInfoForm record) {
        QueryWrapper<ActivityInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(PropertyUtil.copy(record, ActivityInfo.class));
        List<ActivityInfo> list = baseMapper.selectList(queryWrapper);
        return this.convert(list);
    }

    @Override
    public PageInfo<ActivityInfoVO> queryActivityListPage(ActivityInfoForm param) {
        // 分页数据设置
        PageHelper.startPage((int) param.getCurrent(), (int) param.getSize());
        List<ActivityInfoVO> list = activityInfoDao.getActivityInfoVOList(param);
        for (ActivityInfoVO avo:list) {
            List<HelpCoupon> helpCoupons = activityInfoDao.getHelpCoupons(avo.getId());
            if (null !=  avo.getActivityType() && avo.getActivityType().contains("2")){
                avo.setLevel1No(null != avo.getHelpMax()? Integer.valueOf(avo.getHelpMax()) : 0);
            }
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(helpCoupons)){
                helpCoupons.forEach(coupon -> {
                    if (null != coupon.getLevel() && 1== coupon.getLevel()){
                        avo.setLevel1No(coupon.getStockNo());
                    }
                    if (null != coupon.getLevel() && 2== coupon.getLevel()){
                        avo.setLevel2No(coupon.getStockNo());
                    }
                    if (null != coupon.getLevel() && 3== coupon.getLevel()){
                        avo.setLevel3No(coupon.getStockNo());
                    }
                });

            }
            if (avo.getActivityType()!=null && !"".equals(avo.getActivityType())){
                String[] types = avo.getActivityType().split(",");
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
                    }else if ("5".equals(type)){
                        sb.append("乘车活动,");
                    }
                }
                String activityType = sb.toString().substring(0,sb.toString().length()-1);
                avo.setActivityType(activityType);
            }
        }
        return new PageInfo<ActivityInfoVO>(list);
    }

    @Override
    public ActivityInfoVO getActivitySumCountById(String id) {
        return activityInfoDao.getActivitySumCountById(id);
    }


    @Override
    public PageInfo<ActivityInfoVO> selectPage(ActivityInfoForm record) {
            // 分页数据设置
            PageHelper.startPage((int) record.getCurrent(), (int) record.getSize());
            String queryType = "1";
            String books = "";
            if (record.getBookIds()!=null && record.getBookIds().size()>0){
                books = " and book_id in ('"+StringUtils.join(record.getBookIds().toArray(new String[record.getBookIds().size()]),"','")+"')";
            }
            if (record.getProjectList()!=null && record.getProjectList().size()>0){
                if (!"".equals(books)){
                    queryType = "3";
                }else{
                    queryType = "2";
                }
            }
            record.setQueryType(queryType);
            record.setBooks(books);
            List<ActivityInfoVO> list = activityInfoDao.getAllActivityInfoVO(record);
            for (ActivityInfoVO avo:list) {
                List<HelpCoupon> helpCoupons = activityInfoDao.getHelpCoupons(avo.getId());
                if(null != avo.getActivityType() && avo.getActivityType().contains("2")){
                    avo.setLevel1No(null != avo.getHelpMax()? Integer.valueOf(avo.getHelpMax()) : 0);
                    avo.setLevel1User(null != avo.getHelpOkCount()? Integer.valueOf(avo.getHelpOkCount()) : 0);
                }
                if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(helpCoupons)){
                    Integer receiveNo = activityInfoDao.getCountReceive(avo.getId());
                    avo.setCouponCollected(null != receiveNo ? receiveNo.toString() : "0");
                    helpCoupons.forEach(coupon -> {
                        if (null != coupon.getLevel() && 1== coupon.getLevel()){
                            avo.setLevel1No(coupon.getStockNo());
                            avo.setLevel1User(activityInfoDao.getSuccessHelpNo(avo.getId(),coupon.getTargetNumber()));

                        }
                        if (null != coupon.getLevel() && 2== coupon.getLevel()){
                            avo.setLevel2No(coupon.getStockNo());
                            avo.setLevel2User(activityInfoDao.getSuccessHelpNo(avo.getId(),coupon.getTargetNumber()));
                        }
                        if (null != coupon.getLevel() && 3== coupon.getLevel()){
                            avo.setLevel3No(coupon.getStockNo());
                            avo.setLevel3User(activityInfoDao.getSuccessHelpNo(avo.getId(),coupon.getTargetNumber()));
                        }
                    });

                }

                if (avo.getActivityType()!=null && !"".equals(avo.getActivityType())){
                    String[] types = avo.getActivityType().split(",");
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
                        } else if ("5".equals(type)) {
                            sb.append("乘车活动,");
                        }
                    }
                    String activityType = sb.toString().substring(0,sb.toString().length()-1);
                    avo.setActivityType(activityType);
                }
                if (avo.getActivityType().equals("乘车活动") && org.apache.commons.lang3.StringUtils.isNotBlank(avo.getId())) {
                    // 根据id查询统计数据
                    ActDataStatistics actDataStatistics = activityInfoDao.getActDataStatistics(avo.getId());
                    if (!ObjectUtils.isEmpty(actDataStatistics)) {
                        BeanUtils.copyProperties(actDataStatistics,avo);
                    }
                    // 查询乘车活动楼盘名称
                    String bookName = activityInfoDao.getBookNameByActId(avo.getId());
                    avo.setBookName(bookName);
                }
            }
        System.out.println("list = " + list);
            return new PageInfo<ActivityInfoVO>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateActivityInfo(Map map) {
        try {
            if (map != null) {
                if (map.get("id") == null || "".equals(map.get("id") + "")) {
                    return ResultBody.error(-21_0006, "必传参数未传！！");
                }
                if ((map.get("isDel") == null || "".equals(map.get("isDel") + ""))
                        && (map.get("actStatus") == null || "".equals(map.get("actStatus") + ""))
                        && (map.get("status") == null || "".equals(map.get("status") + ""))) {
                    return ResultBody.error(-21_0006, "参数未传！！");
                } else {
                    if ((map.get("status") != null && "0".equals(map.get("status") + "") || (map.get("isDel") != null && "1".equals(map.get("isDel") + "")))) {
                        //禁用时更新其关联的轮播图和弹窗
                        activityInfoDao.updatePhotoAndPopup(map.get("id") + "");
                    }
                    map.put("userId", SecurityUtils.getUserId());
                    activityInfoDao.updateActivityInfo(map);
                }
            } else {
                return ResultBody.error(-21_0006, "参数异常！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003, "更新异常！！");
        }
        return ResultBody.success("更新成功！！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String activityExportNew(String param1) {
        ActivityInfoForm param = JSONObject.parseObject(param1,ActivityInfoForm.class);
        ExcelExportLog excelExportLog = new ExcelExportLog();

        String queryType = "1";
        String books = "";
        if (param.getBookIds()!=null && param.getBookIds().size()>0){
            books = " and book_id in ('"+StringUtils.join(param.getBookIds().toArray(new String[param.getBookIds().size()]),"','")+"')";
        }
        String id = UUID.randomUUID().toString();
        List<String> proIdList = new ArrayList<>();
        if (param.getProjectIds() != null && !"".equals(param.getProjectIds())) {
            String[] ids = param.getProjectIds().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
            if (!"".equals(books)){
                queryType = "3";
            }else{
                queryType = "2";
            }

        } else {
            Map map = new HashMap();
            map.put("UserName", param.getUserName());
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
            List<Map> mapList = projectMapper.findProjectListByUserName(param.getUserName() + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList != null && mapList.size() > 0) {
                for (Map proMap : mapList) {
                    proIdList.add(proMap.get("projectId") + "");
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
        excelExportLog.setMainType("4");
        excelExportLog.setMainTypeDesc("活动管理");
        excelExportLog.setSubType("H3");
        excelExportLog.setSubTypeDesc("活动列表");
        excelExportLog.setExportType("3");
        excelExportLog.setExportStatus("1");
        excelExportLog.setIsAsyn("1");
        //sql拼接
        StringBuffer sb = new StringBuffer();
        sb.append("select info.id,info.activity_name activityName,info.activity_projectnames activityProjectnames,DATE_FORMAT(info.activity_begintime,'%Y-%m-%d %H:%i:%s') activityBegintime,DATE_FORMAT(info.activity_endtime,'%Y-%m-%d %H:%i:%s') activityEndtime,DATE_FORMAT(info.createtime,'%Y-%m-%d %H:%i:%s') createtime,ba.EmployeeName creator,DATE_FORMAT(info.release_time,'%Y-%m-%d %H:%i:%s') releaseTime,(case when info.disabletime is null then '/' else DATE_FORMAT(info.disabletime,'%Y-%m-%d %H:%i:%s') end) disabletime,(case when info.`status` = 0 then '已禁用' when info.activity_endtime <= now() and info.act_status = 2 and info.`status` = 1 then '已结束' when info.activity_begintime <= now() and info.activity_endtime > now() and info.act_status = 2 and info.`status` = 1 then '已开始' when info.act_status = 2 and info.release_time <= now() and info.`status` = 1 then '已发布' when info.act_status = 2 and info.release_time > now() and info.`status` = 1 then '未发布' when info.act_status = 1 then '草稿' else '' end) actStatus, info.activity_type activityType, ifnull(s.count,0) couponCollected, ifnull(sup.count,0) signUpCount, ifnull(sin.count,0) signInCount, ifnull(he.count,0) needHelpCount, ifnull(he.okCount,0) helpOkCount,ifnull(hedetail.count,0) helpCount,info.signup_max signupMax,info.help_max helpMax,ifnull(avd.count,0) vowCount,(CASE WHEN info.vow_open_time > now() THEN '未开奖' when info.vow_open_time <=now() THEN '已开奖' ELSE '/' END) openAwardStatus from a_activity_info info LEFT JOIN b_account ba on ba.id = info.creator LEFT JOIN (select sum(coupon_collected) count,activity_id " +
                "from a_coupon_info GROUP BY activity_id) s on s.activity_id = info.id LEFT JOIN (select count(1) count,activity_id from a_activity_signup GROUP BY activity_id) sup on info.id = sup.activity_id LEFT JOIN (select count(1) count,cc.activity_id from ( select activity_id,signin_id from a_activity_signin GROUP BY activity_id,signin_id) cc group by cc.activity_id) sin on info.id = sin.activity_id LEFT JOIN (select count(1) count,sum(case when help_status = 2 then 1 else 0 end) okCount,activity_id from a_activity_help GROUP BY activity_id) he on info.id = he.activity_id LEFT JOIN (select count(1) count,sss.activity_id from ( select activity_id,friend_id from a_activity_helpdetail GROUP BY activity_id,friend_id) sss group by sss.activity_id) hedetail on info.id = hedetail.activity_id LEFT JOIN (select count(1) count,activity_id from a_activity_vow_detail GROUP BY activity_id ) avd on avd.activity_id=info.id where info.is_del = 0 and info.third_party_type = 0");
        String projectIds = "'"+StringUtils.join(proIdList.toArray(), "','")+"'";
        if ("3".equals(queryType)){
            sb.append(" and info.id in (select DISTINCT actity_id from a_activity_projects where isdel = 0 and project_id in ("+projectIds+")"+books+")");
        }else{
            sb.append(" and info.id in (select DISTINCT actity_id from a_activity_projects where isdel = 0 and project_id in ("+projectIds+"))");
        }

        if (param.getCreator()!=null && !"".equals(param.getCreator())){
            sb.append(" and ba.EmployeeName like concat('%','"+param.getCreator()+"','%')");
        }
        if (param.getOpenAwardtStatus()!=null && "1".equals(param.getOpenAwardtStatus())){
            sb.append(" and info.vow_open_time <=now()");
        }
        if (param.getOpenAwardtStatus()!=null && "2".equals(param.getOpenAwardtStatus())){
            sb.append(" and info.vow_open_time > now()");
        }
        if (param.getActivityName()!=null && !"".equals(param.getActivityName())){
            sb.append(" and info.activity_name like concat('%','"+param.getActivityName()+"','%')");
        }
        if (param.getActivityType()!=null && !"".equals(param.getActivityType())){
            sb.append(" and info.activity_type like concat('%','"+param.getActivityType()+"','%')");
        }
        if (param.getActivityNo()!=null && !"".equals(param.getActivityNo())){
            sb.append(" and info.activity_no like concat('%','" + param.getActivityNo() +"','%')");
        }
        if (param.getActStatus()!=null && !"".equals(param.getActStatus())){
            if ("1".equals(param.getActStatus())){
                sb.append(" and info.act_status = '"+param.getActStatus()+"'");
            }else if ("2".equals(param.getActStatus())){
                sb.append(" and info.act_status = 2 and info.release_time <= now() and info.activity_begintime > now() and info.`status` = 1");
            }else if ("3".equals(param.getActStatus())){
                sb.append(" and info.activity_begintime <= now() and info.activity_endtime > now() and info.act_status = 2 and info.`status` = 1");
            }else if ("0".equals(param.getActStatus())){
                sb.append(" and info.`status` = 0");
            }else if ("4".equals(param.getActStatus())){
                sb.append(" and info.activity_endtime <= now() and info.act_status = 2 and info.`status` = 1");
            }else if ("5".equals(param.getActStatus())){
                sb.append(" and info.release_time > now() and info.act_status = 2 and info.`status` = 1");
            }
        }
        if (param.getDate1()!=null && param.getDate2()!=null && !"".equals(param.getDate1()) && !"".equals(param.getDate2())){
            if (param.getReportTime()!=null){
                if ("1".equals(param.getReportTime())){
                    sb.append(" and info.release_time BETWEEN '"+param.getDate1()+"' AND '"+param.getDate2()+"'");
                }else if("2".equals(param.getReportTime())){
                    sb.append(" and info.activity_begintime BETWEEN '"+param.getDate1()+"' AND '"+param.getDate2()+"'");
                }else if("3".equals(param.getReportTime())){
                    sb.append(" and info.activity_endtime BETWEEN '"+param.getDate1()+"' AND '"+param.getDate2()+"'");
                }
            }
        }
        sb.append(" order by info.createtime desc");
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
        return "您当前导出数据多于8000条，请关注右上角下载任务状态";
    }

    @Override
    public ResultBody getHelpAndSingUpCount(Map param) {
        if (param.get("id") == null || "".equals(param.get("id") + "")) {
            return ResultBody.error(-21_0006, "必传参数未传！！");
        }
        return ResultBody.success(activityInfoDao.getHelpAndSingUpCount(param.get("id")+""));
    }

    @Override
    public ResultBody getActivityOrder(Map map) {
        return ResultBody.success(activityInfoDao.getActivityOrder(map));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addActivityOrder(Map map) {
        try{
            //先删掉
            activityInfoDao.delActivityOrder(map);
            //保存排序
            activityInfoDao.addActivityOrder(map);
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0007,"保存排序异常！");
        }
        return ResultBody.success("保存排序成功！");
    }



    @Override
    public void activityExport(HttpServletRequest request, HttpServletResponse response, String params) {
        ActivityInfoForm param = JSONObject.parseObject(params,ActivityInfoForm.class);
        String queryType = "1";
        String books = "";
        if (param.getBookIds()!=null && param.getBookIds().size()>0){
            books = " and book_id in ('"+StringUtils.join(param.getBookIds().toArray(new String[param.getBookIds().size()]),"','")+"')";
        }
        param.setQueryType(queryType);
        param.setBooks(books);
        ArrayList<Object[]> dataset = new ArrayList<>();
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("4");
        excelExportLog.setMainTypeDesc("活动管理");
        excelExportLog.setSubType("H3");
        excelExportLog.setSubTypeDesc("活动列表");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        try {
            List<String> proIdList = new ArrayList<>();
            if (param.getProjectIds() != null && !"".equals(param.getProjectIds())) {
                String[] ids = param.getProjectIds().split(",");
                for (String proid : ids) {
                    proIdList.add(proid);
                }

            } else {
                Map map = new HashMap();
                map.put("UserName", param.getUserName());
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
                List<Map> mapList = projectMapper.findProjectListByUserName(param.getUserName() + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
                if (mapList != null && mapList.size() > 0) {
                    for (Map proMap : mapList) {
                        proIdList.add(proMap.get("projectId") + "");
                    }
                }
            }
            param.setProjectList(proIdList);
            if (!"".equals(books)){
                queryType = "3";
            }else{
                queryType = "2";
            }
            param.setQueryType(queryType);
            param.setBooks(books);
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setCreator(param.getUserId());
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
            //导出的文档下面的名字
            List<ActivityInfoVO> list = activityInfoDao.getAllActivityInfoVO(param);
            if (list.size()>0){
                String[] headers = list.get(0).getCourtCaseTitle();
                for (int i = 0; i < list.size(); i++) {
                    ActivityInfoVO activityInfoVO = list.get(i);
                    List<HelpCoupon> helpCoupons = activityInfoDao.getHelpCoupons(activityInfoVO.getId());
                    if(null != activityInfoVO.getActivityType() && activityInfoVO.getActivityType().contains("2")){
                        activityInfoVO.setLevel1No(null != activityInfoVO.getHelpMax()? Integer.valueOf(activityInfoVO.getHelpMax()) : 0);
                        activityInfoVO.setLevel1User(null != activityInfoVO.getHelpOkCount()? Integer.valueOf(activityInfoVO.getHelpOkCount()) : 0);
                    }
                    if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(helpCoupons)){
                        Integer receiveNo = activityInfoDao.getCountReceive(activityInfoVO.getId());
                        activityInfoVO.setCouponCollected(null != receiveNo ? receiveNo.toString() : "0");
                        helpCoupons.forEach(coupon -> {
                            if (null != coupon.getLevel() && 1== coupon.getLevel()){
                                activityInfoVO.setLevel1No(coupon.getStockNo());
                                activityInfoVO.setLevel1User(activityInfoDao.getSuccessHelpNo(activityInfoVO.getId(),coupon.getTargetNumber()));

                            }
                            if (null != coupon.getLevel() && 2== coupon.getLevel()){
                                activityInfoVO.setLevel2No(coupon.getStockNo());
                                activityInfoVO.setLevel2User(activityInfoDao.getSuccessHelpNo(activityInfoVO.getId(),coupon.getTargetNumber()));
                            }
                            if (null != coupon.getLevel() && 3== coupon.getLevel()){
                                activityInfoVO.setLevel3No(coupon.getStockNo());
                                activityInfoVO.setLevel3User(activityInfoDao.getSuccessHelpNo(activityInfoVO.getId(),coupon.getTargetNumber()));
                            }
                        });
                    }

                    if (activityInfoVO.getActivityType()!=null && !"".equals(activityInfoVO.getActivityType())){
                        String[] types = activityInfoVO.getActivityType().split(",");
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
                            } else if ("5".equals(type)) {
                                sb.append("乘车活动,");
                            }
                        }
                        String activityType = sb.toString().substring(0,sb.toString().length()-1);
                        activityInfoVO.setActivityType(activityType);
                    }
                    if (activityInfoVO.getActivityType().equals("乘车活动") && org.apache.commons.lang3.StringUtils.isNotBlank(activityInfoVO.getId())) {
                        // 根据id查询统计数据
                        ActDataStatistics actDataStatistics = activityInfoDao.getActDataStatistics(activityInfoVO.getId());
                        BeanUtils.copyProperties(actDataStatistics,activityInfoVO);

                        // 查询乘车活动楼盘名称
                        String bookName = activityInfoDao.getBookNameByActId(activityInfoVO.getId());
                        activityInfoVO.setBookName(bookName);
                        activityInfoVO.setActivityProjectnames(activityInfoVO.getProjectName());
                    }
                    activityInfoVO.setRownum((i + 1) + "");
                    Object[] oArray = activityInfoVO.toActivityData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("活动明细表", headers,dataset, "活动明细表", response,null);
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
        } catch (Exception e) {
            e.printStackTrace();
            excelExportLog.setExportStatus("3");
            excelExportLog.setExceptionMessage(e.getMessage());
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addActivityInfo(ActivityInfoForm param) {
        JSONArray jsonArray = JSONUtil.parseArray(param.getSignup_diy_code());
        for (Object object : jsonArray) {
            JSONObject jsonObject = JSONObject.parseObject(object.toString());
            if(jsonObject.get("codeId")!=null){
            excelImportMapper.updateDiyCode(jsonObject.get("codeId")+"");
            }
        }
        //活动ID
        String id = UUID.randomUUID().toString().replaceAll("-", "");

        try {
            if (param == null || param.getProjectList() == null || param.getProjectList().size() == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultBody.error(-21_0006, "必传参数未传！！");
            }
            param.setId(id);
            String createtime = sf.format(new Date());
            String creator = SecurityUtils.getUserId();
            List<String> proIds = param.getProjectList();
            if (proIds != null && proIds.size() > 0) {
//                StringBuffer proNames = new StringBuffer();
                List<String> proNames = new ArrayList<>();
                for (String proid : proIds) {
                    //查询项目关联城市，楼盘
                    List<Map> maps = activityInfoDao.getProBookCity(proid);
//                    proNames.append(maps.get(0).get("ProjectName") + ",");
                    for (Map m : maps) {
                        if (!proNames.contains(m.get("ProjectName")+"")){
                            proNames.add(m.get("ProjectName")+"");
                        }
                        //保存活动关联项目
                        m.put("activityId", id);
                        m.put("projectId", proid);
                        m.put("createtime", createtime);
                        m.put("creator", creator);
                        activityInfoDao.addActivityPro(m);
                    }
                }
                String activityProjectnames = StringUtils.join(proNames,",");
                param.setActivityProjectnames(activityProjectnames);
//                String activityProjectnames = proNames.toString().substring(0, proNames.toString().length() - 1);
//                param.setActivityProjectnames(activityProjectnames);
            }

            for (ActivityMaterial a : param.getMaterials()) {
                //保存素材
                Map map = new HashMap();
                map.put("activity_id", id);
                map.put("material_type", a.getMaterialType());
                map.put("material_address", a.getMaterialAddress());
                map.put("creator", creator);
                map.put("createtime", createtime);
                activityInfoDao.addActivityPhoto(map);
            }
            //保存活动地址 先全部删除 在新增
            activityInfoDao.delActivityAddressByActivityId(param.getId());
            if (param.getAddressList().size() > 0) {
                for (int i = 0; i < param.getAddressList().size(); i++) {
                    param.getAddressList().get(i).setActivityId(id);
                }
                activityInfoDao.saveActivityAddress(param.getAddressList());
            }
            param.setCreatetime(createtime);
            param.setCreator(creator);
            activityInfoDao.addActivityInfo(param);
            StringBuffer sb = new StringBuffer();
            if (param.getCouponIdList() != null && param.getCouponIdList().size() > 0) {
                //更新优惠卷
                for (int i = 0; i < param.getCouponIdList().size(); i++) {
                    if (i == param.getCouponIdList().size() - 1) {
                        sb.append("'" + param.getCouponIdList().get(i) + "'");
                    } else {
                        sb.append("'" + param.getCouponIdList().get(i) + "',");
                    }
                }
                Map kMap = new HashMap();
                kMap.put("activityId", id);
                kMap.put("ids", sb.toString());
                activityInfoDao.updateCoupon(kMap);
            }
            //保存奖项信息
            //解除优惠券绑定活动
            //活动绑定优惠券
            if(null != param.getAwardInfo() && param.getAwardInfo().size()>0){
                for (int i = 0; i < param.getAwardInfo().size(); i++) {
                    String awardId = UUID.randomUUID().toString().replaceAll("-", "");
                    param.getAwardInfo().get(i).setId(awardId);
                    param.getAwardInfo().get(i).setActivityId(id);
                }
                if(param.getAwardInfo().size()>0) {
                    activityInfoDao.saveCouponRel(id, param.getAwardInfo());
                    activityInfoDao.saveAwardInfo(param.getAwardInfo());
                }
            }
            //助力优惠券处理
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(param.getHelpCoupons())){
                param.getHelpCoupons().forEach(item -> {
                    activityInfoDao.updateHelpCoupons(id, item.getCouponId(), item.getLevel(), item.getTargetNumber(), item.getGiftImageUrl(),1);
                });
            }

            // 只有勾选是时 保存首页热推信息 保证同时间只有一个活动有首页热推
            if ("0".equals(param.getIsHomePageHot())) {
                String hotEndTime = param.getHotEndTime();
                String hotStartTime = param.getHotStartTime();
                if (org.apache.commons.lang3.StringUtils.isBlank(hotEndTime) ||
                        org.apache.commons.lang3.StringUtils.isBlank(hotStartTime) ||
                        org.apache.commons.lang3.StringUtils.isBlank(param.getHotImageUrl())) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultBody.error(120001,"请完善热推信息!");
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(hotStartTime) ||
                        org.apache.commons.lang3.StringUtils.isBlank(hotEndTime)
                ) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultBody.error(108889,"请选择首页热推时间！");
                }
                List<String> hotCityIds = param.getHotCityId();
                for (String hotCityId : hotCityIds) {
                    Integer count = activityInfoDao.checkHotHomePage(hotCityId, hotStartTime, hotEndTime, "", "");
                    if (count > 0) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        String cityName = activityInfoDao.getCityNameById(hotCityId);
                        return ResultBody.error(108888,"当前"+cityName+"已有活动热推,请重新选择时间或重选城市！");
                    }
                }
                String hotCityId = ListToString(hotCityIds);
                // 保存首页热推信息
                Map<String, Object> hotMap = new HashMap<>();
                hotMap.put("cityId", hotCityId);
                hotMap.put("cityName",param.getHotCityName());
                hotMap.put("imageUrl",param.getHotImageUrl());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                hotMap.put("startTime", dateFormat.parse(hotStartTime));
                hotMap.put("endTime", dateFormat.parse(hotEndTime));
                hotMap.put("createTime",dateFormat.format(new Date()));
                hotMap.put("creator",param.getCreator());
                // 活动id
                hotMap.put("activityId",id);
                activityInfoDao.addActivityHotHomePage(hotMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003, "添加活动异常！！");
        }
        return ResultBody.success(id);
    }

    // 集合转字符串方法
    public static String ListToString(List list) {
        if (!CollectionUtils.isEmpty(list)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i)).append(',');
            }
            return list.isEmpty() ? "" : sb.toString().substring(0, sb.toString().length() - 1);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateActivityById(ActivityInfoForm param) {

        try {
            if (param == null || param.getProjectList() == null || param.getProjectList().size() == 0) {
                return ResultBody.error(-21_0006, "必传参数未传！！");
            }
            String id = param.getId();
            String creator = SecurityUtils.getUserId();
            param.setCreator(creator);
            List<String> pros = param.getProjectList();
            if (pros != null && pros.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (String p : pros) {
                    sb.append("'" + p + "',");
                }
                String pids = sb.toString().substring(0, sb.toString().length() - 1);
                //查询项目名称
                List<String> names = activityInfoDao.getProNames(pids);
                StringBuffer sb1 = new StringBuffer();
                for (String p : names) {
                    sb1.append(p + ",");
                }
                String activityProjectnames = sb1.toString().substring(0, sb1.toString().length() - 1);
                param.setActivityProjectnames(activityProjectnames);
                List<String> oldPros = activityInfoDao.getActivityPro(id);
                //更新关联项目
                for (String proId : oldPros) {
                    //逻辑删除解除绑定的项目
                    Map kMap = new HashMap();
                    kMap.put("id", id);
                    kMap.put("proId", proId);
                    kMap.put("creator", creator);
                    activityInfoDao.updateActivityProById(kMap);
                }
                if (pros.size() > 0) {
                    for (String proid : pros) {
                        //查询项目关联城市，楼盘
                        List<Map> maps = activityInfoDao.getProBookCity(proid);
                        for (Map m : maps) {
                            //保存活动关联项目
                            m.put("activityId", id);
                            m.put("projectId", proid);
                            m.put("createtime", sf.format(new Date()));
                            m.put("creator", creator);
                            activityInfoDao.addActivityPro(m);
                        }
                    }
                }
            }

            //保存活动地址 先全部删除 在新增
            activityInfoDao.delActivityAddressByActivityId(param.getId());
            if (param.getAddressList().size() > 0) {
                for (int i = 0; i < param.getAddressList().size(); i++) {
                    param.getAddressList().get(i).setActivityId(param.getId());
                }
                activityInfoDao.saveActivityAddress(param.getAddressList());
            }
            //更新活动信息
            activityInfoDao.updateActivityById(param);

            //更新奖项
            List<ActivityAwardInfo> activityAwardInfoList = param.getAwardInfo();

            if(activityAwardInfoList != null && activityAwardInfoList.size()>0){
                for (ActivityAwardInfo activityAwardInfo : activityAwardInfoList) {
                            if(activityAwardInfo.getId()==null||StringUtils.isEmpty(activityAwardInfo.getId())){
                                String awardId = UUID.randomUUID().toString().replaceAll("-", "");
                                activityAwardInfo.setId(awardId);
                            }
                    activityAwardInfo.setActivityId(id);
                }
                //解除优惠券绑定活动
                activityInfoDao.delCouponRel(id);
                //活动绑定优惠券
                activityInfoDao.saveCouponRel(id,activityAwardInfoList);
                //删除奖项
                activityInfoDao.delAwardInfoByActiviId(id);
                //新增
                activityInfoDao.saveAwardInfo(activityAwardInfoList);
            }
            //更新活动素材
            List<ActivityMaterial> materials = param.getMaterials();
            if (materials != null && materials.size() > 0) {
                //删除素材
                activityInfoDao.delActivityPhoto(id);
                for (ActivityMaterial a : materials) {
                    Map map = new HashMap();
                    map.put("activity_id", id);
                    map.put("material_type", a.getMaterialType());
                    map.put("material_address", a.getMaterialAddress());
                    map.put("creator", creator);
                    map.put("createtime", sf.format(new Date()));
                    activityInfoDao.addActivityPhoto(map);
                }
            }
            //查询原来绑定的优惠券
            List<String> yhIDs = activityInfoDao.selectActivityCoupon(id);
            //获取现在绑定的优惠券
            List<String> xIds = param.getCouponIdList();
            StringBuffer ss = new StringBuffer();
            if (yhIDs != null && yhIDs.size() > 0) {
                for (String idsss : yhIDs) {
                    ss.append("'" + idsss + "',");
                }
                String ids = ss.toString().substring(0, ss.toString().length() - 1);
                Map kMap = new HashMap();
                kMap.put("activityId", null);
                kMap.put("ids", ids);
                activityInfoDao.updateCoupon(kMap);
            }

            StringBuffer ss1 = new StringBuffer();
            if (xIds != null && xIds.size() > 0) {
                for (int i = 0; i < xIds.size(); i++) {
                    if (i == xIds.size() - 1) {
                        ss1.append("'" + xIds.get(i) + "'");
                    } else {
                        ss1.append("'" + xIds.get(i) + "',");
                    }
                }
                Map kMap = new HashMap();
                kMap.put("activityId", id);
                kMap.put("ids", ss1.toString());
                activityInfoDao.updateCoupon(kMap);
            }

            //助力优惠券处理(需先解绑之前活动的优惠券)
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(yhIDs)){
                List<String> zhuliIdList = new ArrayList<>();
                if(xIds != null && xIds.size() > 0){
                    for(String str : yhIDs){
                        if(!xIds.contains(str)){
                            zhuliIdList.add(str);
                        }
                    }
                }
                if(zhuliIdList != null && zhuliIdList.size() > 0) {
                    zhuliIdList.forEach(item -> {
                        activityInfoDao.updateHelpCoupons(null, item, null, null, null, null);
                    });
                }
            }
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(param.getHelpCoupons())){
                param.getHelpCoupons().forEach(item -> {
                    activityInfoDao.updateHelpCoupons(id, item.getCouponId(), item.getLevel(), item.getTargetNumber(), item.getGiftImageUrl(),1);
                });
            }

            // 只有勾选并且首页热推为启用状态时 判断热推信息 保证同时间只有一个活动有首页热推
            String hotId = param.getHotId();
            if ("0".equals(param.getIsHomePageHot())) {
                String hotEndTime = param.getHotEndTime();
                String hotStartTime = param.getHotStartTime();
                String flag = "1";
                List<String> hotCityIds = param.getHotCityId();
                for (String hotCityId : hotCityIds) {
                    Integer count = activityInfoDao.checkHotHomePage(hotCityId, hotStartTime, hotEndTime, flag, hotId);
                    if (count > 0) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        String cityName = activityInfoDao.getCityNameById(hotCityId);
                        return ResultBody.error(108888,"当前"+cityName+"已有活动热推,请重新选择时间或重选城市！");
                    }
                }
                // 更新首页热推信息
                Map<String, Object> hotMap = new HashMap<>();
                String s = ListToString(param.getHotCityId());
                hotMap.put("cityId", s);
                hotMap.put("cityName", param.getHotCityName());
                hotMap.put("imageUrl", param.getHotImageUrl());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                hotMap.put("startTime", hotStartTime);
                hotMap.put("endTime", hotEndTime);
                // 活动id
                hotMap.put("activityId", param.getId());
                if (org.apache.commons.lang3.StringUtils.isNotBlank(param.getHotId())) {
                    hotMap.put("id",param.getHotId());
                    hotMap.put("editTime", dateFormat.format(new Date()));
                    hotMap.put("editor", param.getEditor());
                    String status = param.getIsHomePageHot();
                    hotMap.put("status", status);
                    activityInfoDao.updateActivityHotHomePage(hotMap);
                } else {
                    hotMap.put("createTime", dateFormat.format(new Date()));
                    hotMap.put("creator", param.getCreator());
                    activityInfoDao.addActivityHotHomePage(hotMap);
                }
            } else {
                // 活动不热推 热推信息禁用
                activityInfoDao.updateHotPageStatusById(hotId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003, "更新活动异常！！");
        }
        return ResultBody.success("更新活动成功！！");
    }

    @Override
    public ResultBody updateRedisById(Map param) {
        String url = this.url + "?id=" + param.get("id") + "&type=" + param.get("type");
        try {
            if ("2".equals(param.get("type") + "") && param.get("editStatus") != null && "1".equals(param.get("editStatus"))) {
                //更新优惠券更新库存字段为0
                activityInfoDao.updateCouponUCount(param.get("id") + "");
            }
            HttpRequestUtil.httpGet(url, false);
        } catch (Exception e) {
            e.printStackTrace();
            Map map = new HashMap();
            map.put("node", e.getMessage());
            if ("1".equals(param.get("type") + "")) {
                map.put("activityId", param.get("id"));
            } else {
                map.put("couponId", param.get("id"));
            }
            activityInfoDao.addErrorEdit(map);
        }
        return ResultBody.success("成功！！");
    }
    /**
     * @Author wanggang
     * @Description //更新许愿图片
     * @Date 19:37 2021/2/5
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    @Override
    public ResultBody updateRedisXy() {
        String url = this.updateRedisUrl;
        try {
            System.out.println("开始更新！！");
            String re = HttpRequestUtil.httpGet(url, false);
            System.out.println("开始更新！！"+re);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBody.success("成功！！");
    }

    @Override
    public List<ResultProjectVO> getProjectListByUserId(Map<String, Object> map) {
        if (map == null) {
            throw new BadRequestException(-10_0000, "参数不能为空");
        }
        List<String> fullpath = projectMapper.findFullPath(map);
        StringBuffer sb = new StringBuffer();
        if (fullpath == null || fullpath.size() == 0) {
            throw new BadRequestException(-10_0000, "用户无项目权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i == 0) {
                sb.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
            } else {
                sb.append(" or org.FullPath LIKE '" + fullpath.get(i) + "%'");
            }
        }
        map.put("where", sb.toString());
        List<ProjectVO> projectList = projectMapper.getBookProjectListByUserName(map);
        if (projectList.size() > 0) {
            List<String> strings = projectList.stream().map(ProjectVO::getAreaId).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String areaId = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(String.valueOf(i));
                List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> areaId.equals(pro.getAreaId())).collect(Collectors.toList());
                if (projectVOList1.size() > 0) {
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getAreaName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getProjectId());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectName());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;
    }

        @Override
        public List<Map> getLatitudeAndLongitudeList(Map map) {
            return activityInfoDao.getLatitudeAndLongitudeList(map);
        }

    @Override
    public ResultBody getActivitySignUpDetailedList(Map map) {
        PageHelper.startPage((int) map.get("pageNum"), (int) map.get("pageSize"));
        Page<ActivitySignUpVO> list = activityInfoDao.getActivitySignUpDetailedList(map);
        return ResultBody.success(new PageInfo<ActivitySignUpVO>(list));
    }

    @Override
    public ResultBody getActivityHelpDetailedList(Map map) {
        PageHelper.startPage((int) map.get("pageNum"), (int) map.get("pageSize"));
        //处理助力状态
        handleStuats(map);
        Page<ActivityHelpVO> list = activityInfoDao.getActivityHelpDetailedList(map);
        if (CollectionUtils.isEmpty(list)){
            return ResultBody.success(new PageInfo<ActivityHelpVO>(list));
        }

        list.forEach(item -> {
            item.setLeve1No(null != item.getHelpNum()? Integer.valueOf(item.getHelpNum()) : 0);
            if (null != item.getHelpStatus() && 2==item.getHelpStatus()){
                item.setReceiveCouponName(item.getGiftName());
                item.setHeXiaoCouponName(item.getGiftName());
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(item.getActivityId())){
                List<HelpCoupon> helpCoupons = activityInfoDao.getHelpCoupons(item.getActivityId());
                if (!CollectionUtils.isEmpty(helpCoupons)){
                    helpCoupons.forEach(coupon -> {
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(item.getShareOpenId())){
                            List<CouponLock> couponLocks = activityInfoDao.queryIsLock(coupon.getCouponId(), item.getShareOpenId());
                            if (!CollectionUtils.isEmpty(couponLocks)){
                                Integer isLock = couponLocks.get(0).getIsLock();
                                if(null != isLock && (1 == isLock || isLock == 3)){
                                    item.setLockCouponNo(coupon.getCouponNo());
                                    item.setLockCouponId(coupon.getCouponId());
                                    item.setLockCouponName(coupon.getCouponName());
                                }
                                if(null != isLock && isLock == 3){
                                    item.setReceiveCouponNo(coupon.getCouponNo());
                                    item.setReceiveCouponId(coupon.getCouponId());
                                    item.setReceiveCouponName(coupon.getCouponName());
                                }
                            }

                        }
                        if (null != coupon.getLevel() && 1== coupon.getLevel()){
                            item.setLeve1No(coupon.getTargetNumber());
                        }
                        if (null != coupon.getLevel() && 2== coupon.getLevel()){
                            item.setLeve2No(coupon.getTargetNumber());
                        }
                        if (null != coupon.getLevel() && 3== coupon.getLevel()){
                            item.setLeve3No(coupon.getTargetNumber());
                        }
                    });
                    if (null != item.getHelpStatus() && 2==item.getHelpStatus()){
                        item.setHeXiaoCouponId(item.getReceiveCouponId());
                        item.setHeXiaoCouponName(item.getReceiveCouponName());
                    }
                }
            }

        });


        return ResultBody.success(new PageInfo<ActivityHelpVO>(list));
    }

    private void handleStuats(Map map) {
        List<String> statusAll = null != map.get("helpStatusList")? (List<String>) map.get("helpStatusList") : new ArrayList<>();
        List<String> helpStatusList = new ArrayList<>();
        List<String> levels = new ArrayList<>();
        for (String status : statusAll) {
            switch (status) {
                case "1":
                    helpStatusList.add("1");
                    break;
                case "2":
                    helpStatusList.add("2");
                    break;
                case "3":
                    levels.add("1");
                    break;
                case "4":
                    levels.add("2");
                    break;
                case "5":
                    levels.add("3");
                    break;
                default:

            }

        }
        map.put("helpStatusList",helpStatusList);
        map.put("levels",levels);
    }

    @Override
    public String activitySignUpDetailedExportNew(Map param) {
        String companycode = "";
        if (param.get("companycode")!=null){
            companycode = param.get("companycode")+"";
        }
        String isAll = param.get("isAll")+"";
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("4");
        excelExportLog.setMainTypeDesc("活动管理");
        excelExportLog.setSubType("H1");
        excelExportLog.setSubTypeDesc("活动报名明细");
        excelExportLog.setCreator(param.get("userId")+"");
        if ("1".equals(isAll)){
            excelExportLog.setExportType("2");
        }else{
            excelExportLog.setExportType("1");
        }
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        String ids = param.get("projectStr")+"";
        List<String> proIdList= new ArrayList<>();
        if ("".equals(ids)){
            excelExportLog.setAreaName("/");
            excelExportLog.setProjectId("/");
            excelExportLog.setProjectName("/");
        }else{
            String[] arr = ids.split(",");
            proIdList= new ArrayList<>(Arrays.asList(arr));
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        String pro1 = "";//报名项目
        String pro2 = "";//签到项目
        String pro3 = "";//活动关联项目
        if (param.get("projectType")!=null && proIdList.size()>0){
            if ("1".equals(param.get("projectType")+"")){
                pro1 = " and F_LableIsExist(aas.signup_projectid,'"+param.get("projectStr")+"') = 1";
            }else if ("2".equals(param.get("projectType")+"")){
                pro2 = " and aasi.signin_projectid in ('"+StringUtils.join(proIdList.toArray(), "','")+"')";
            }else if ("3".equals(param.get("projectType")+"")){
                pro3 = " and aap.project_id in ('"+StringUtils.join(proIdList.toArray(), "','")+"')";
            }
        }

        //sql拼接
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT info.activity_no activityNo ," +
                "aas.activity_name activityName," +
                "(case when aasi.is_first_signin = 1 then '' else DATE_FORMAT(aas.signup_time,'%Y-%m-%d %H:%i:%s') end) signUpTime, " +
                "aas.signup_id signUpId, aas.signup_name customerName, concat(left(aas.signup_mobile,3),'****',right(aas.signup_mobile,4)) customerMobile, " +
                "aas.signup_mobile customerMobileAll, DATE_FORMAT(aasi.signin_time,'%Y-%m-%d %H:%i:%s') signInTime, " +
                "DATE_FORMAT(aasi.signin_time_auto,'%Y-%m-%d %H:%i:%s') signInTimeAuto, " +
                "aasi.signin_time_autoname signInTimeAutoName, " +
                "aasi.signin_type signInType, aasi.signin_projectname signInProjectName, " +
                "(case when info.`status` = 0 then '已禁用' when info.activity_endtime <= now() and info.act_status = 2 and info.`status` = 1 then '已结束' when info.activity_begintime <= now() and info.activity_endtime > now() and info.act_status = 2 and info.`status` = 1 then '已开始' when info.act_status = 2 and info.release_time <= now() and info.`status` = 1 then '已发布' when info.act_status = 2 and info.release_time > now() and info.`status` = 1 then '未发布' when info.act_status = 1 then '草稿' else '' end) actStatus, " +
                "(case aasi.signin_type when 1 then 'APP二维码签到' when 2 then 'PC端二维码签到' when 3 then '确访自动签到' else '' end) signinTypeDesc, " +
                "aas.signup_projectname signUpProjectName, aai.relationProject FROM a_activity_signup aas " +
                "LEFT JOIN a_activity_info info on aas.activity_id = info.id " +
                "INNER JOIN ( SELECT aap.actity_id, GROUP_CONCAT(distinct aap.project_name) as relationProject " +
                "FROM a_activity_projects aap " +
                "WHERE aap.isdel = 0 AND aap.`status` = 1 "+pro3+" group by aap.actity_id ) aai ON aai.actity_id = aas.activity_id " +
                "LEFT JOIN a_activity_signin aasi ON aasi.activity_id = aas.activity_id AND aasi.signup_id = aas.id");
        sb.append(" where 1=1").append(pro1).append(pro2);
        if (param.get("activityId")!=null && !"".equals(param.get("activityId")+"")){
            sb.append(" and aas.activity_id ='"+param.get("activityId")+"'");
        }

        if (param.get("signInTypeList")!=null && !"".equals(param.get("signInTypeList")+"")){
            String[] arr = param.get("signInTypeList").toString().split(",");
            sb.append(" and aasi.signin_type in ('"+StringUtils.join(arr, "','")+"')");
        }
        //activityNo
        if (param.get("activityNo")!=null && !"".equals(param.get("activityNo")+"")){
            sb.append(" and info.activity_no like concat('%','"+param.get("activityNo")+"','%')");
        }

        if (param.get("activityName")!=null && !"".equals(param.get("activityName")+"")){
            sb.append(" and aas.activity_name like concat('%','"+param.get("activityName")+"','%')");
        }
        if (param.get("customerName")!=null && !"".equals(param.get("customerName")+"")){
            sb.append(" and aas.signup_name like concat('%','"+param.get("customerName")+"','%')");
        }
        if (param.get("customerMobile")!=null && !"".equals(param.get("customerMobile")+"")){
            sb.append(" and aas.signup_mobile like concat('%','"+param.get("customerMobile")+"','%')");
        }
        if (param.get("actStatus")!=null && !"".equals(param.get("actStatus")+"")){
            if  ("2".equals(param.get("actStatus")+"")){
                sb.append(" and info.act_status = 2 and info.release_time <= now() and info.activity_begintime > now() and info.`status` = 1");
            }else if ("3".equals(param.get("actStatus")+"")){
                sb.append(" and info.activity_begintime <= now() and info.activity_endtime > now() and info.act_status = 2 and info.`status` = 1");
            }else if ("0".equals(param.get("actStatus")+"")){
                sb.append(" and info.`status` = 0");
            }else if ("4".equals(param.get("actStatus")+"")){
                sb.append(" and info.activity_endtime <= now() and info.act_status = 2 and info.`status` = 1");
            }
        }
        if (param.get("date1")!=null && param.get("date2")!=null && !"".equals(param.get("date1")+"") && !"".equals(param.get("date2")+"")){
            if (param.get("TimeType")!=null){
                if ("1".equals(param.get("TimeType")+"")){
                    sb.append(" and aasi.signin_time BETWEEN '"+param.get("date1")+"' AND '"+param.get("date2")+"'");
                }else if("2".equals(param.get("TimeType")+"")){
                    sb.append(" and aasi.signin_time_auto BETWEEN '"+param.get("date1")+"' AND '"+param.get("date2")+"'");
                }else if("3".equals(param.get("TimeType")+"")){
                    sb.append(" and aas.signup_time BETWEEN '"+param.get("date1")+"' AND '"+param.get("date2")+"'");
                }
            }
        }
        sb.append(" order by aas.signup_time desc");
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
    public String activityHelpDetailedExportNew(Map param) {
        String companycode = "";
        if (param.get("companycode")!=null){
            companycode = param.get("companycode")+"";
        }
        String isAll = param.get("isAll")+"";
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("4");
        excelExportLog.setMainTypeDesc("活动管理");
        excelExportLog.setSubType("H2");
        excelExportLog.setSubTypeDesc("活动助力明细");
        excelExportLog.setCreator(param.get("userId")+"");
        if ("1".equals(isAll)){
            excelExportLog.setExportType("2");
        }else{
            excelExportLog.setExportType("1");
        }
        excelExportLog.setIsAsyn("1");
        excelExportLog.setExportStatus("1");
        String ids = param.get("projectStr")+"";
        List<String> proIdList= new ArrayList<>();
        if ("".equals(ids)){
            excelExportLog.setAreaName("/");
            excelExportLog.setProjectId("/");
            excelExportLog.setProjectName("/");
        }else{
            String[] arr = ids.split(",");
            proIdList= new ArrayList<>(Arrays.asList(arr));
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        String pro = "";//关联项目
        if (proIdList.size()>0){
            pro = " and aap.project_id in ('"+StringUtils.join(proIdList.toArray(), "','")+"')";
        }

        //sql拼接
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT aah.id, " +
                "aai.activity_name activityName," +
                "aai.activity_no activityNo," +
                "aah.activity_id as activityId," +
                "aah.share_openid shareOpenid," +
                "aah.level,( CASE aai.help_type WHEN 1 THEN '邀请好友助力' WHEN 2 THEN '邀请好友注册' ELSE '' END) helpType, " +
                "DATE_FORMAT( aai.help_begintime, '%Y-%m-%d %H:%i:%s' ) helpBeginTime, DATE_FORMAT( aai.help_endtime, '%Y-%m-%d %H:%i:%s' ) helpEndTime," +
                " aai.gift_name giftName, aai.sharefriend_no shareFriendNo, aah.share_id shareId, aah.share_name shareName, " +
                "concat( LEFT ( aah.share_mobile, 3 ), '****', RIGHT ( aah.share_mobile, 4 ) ) shareMobile, aah.share_mobile shareMobileAll, " +
                "DATE_FORMAT( aah.share_time, '%Y-%m-%d %H:%i:%s' ) shareTime, aah.closure_id closureId, aah.closure_name closureName, " +
                "DATE_FORMAT( aah.closure_time, '%Y-%m-%d %H:%i:%s' ) closureTime, ifnull( aahl.helpNum, 0 ) helpNum, " +
                "aap.relationProject,aah.help_status helpStatus  " +
                "FROM a_activity_help aah " +
                "LEFT JOIN a_activity_info aai ON aah.activity_id = aai.id " +
                "LEFT JOIN ( SELECT aahl.help_id, count( 1) AS helpNum FROM a_activity_helpdetail aahl GROUP BY aahl.help_id ) aahl ON aahl.help_id = aah.id " +
                "inner JOIN ( SELECT aap.actity_id, GROUP_CONCAT( DISTINCT aap.project_name ) as relationProject " +
                "FROM a_activity_projects aap " +
                "WHERE aap.isdel = 0 AND aap.`status` = 1 "+pro+" GROUP BY aap.actity_id ) aap ON aap.actity_id = aai.id");
        sb.append(" where 1=1");
        if (param.get("activityId")!=null && !"".equals(param.get("activityId")+"")){
            sb.append(" and aah.activity_id ='"+param.get("activityId")+"'");
        }
        if (param.get("activityNo")!=null && !"".equals(param.get("activityNo")+"")){
            sb.append(" and aai.activity_no like concat('%','"+param.get("activityNo")+"','%')");
        }
        if (param.get("activityName")!=null && !"".equals(param.get("activityName")+"")){
            sb.append(" and aah.activity_name like concat('%','"+param.get("activityName")+"','%')");
        }
        if (param.get("shareName")!=null && !"".equals(param.get("shareName")+"")){
            sb.append(" and aah.share_name like concat('%','"+param.get("shareName")+"','%')");
        }
        if (param.get("shareMobile")!=null && !"".equals(param.get("shareMobile")+"")){
            sb.append(" and aah.share_mobile like concat('%','"+param.get("shareMobile")+"','%')");
        }
        if (param.get("closureName")!=null && !"".equals(param.get("closureName")+"")){
            sb.append(" and aah.closure_name like concat('%','"+param.get("closureName")+"','%')");
        }

        if (param.get("helpStatusList")!=null && !"".equals(param.get("helpStatusList")+"")){
            String[] arr = param.get("helpStatusList").toString().split(",");
            sb.append(" and aah.help_status in ('"+StringUtils.join(arr, "','")+"')");
        }
        if (param.get("helpTypeList")!=null && !"".equals(param.get("helpTypeList")+"")){
            String[] arr = param.get("helpTypeList").toString().split(",");
            sb.append(" and aai.help_type in ('"+StringUtils.join(arr, "','")+"')");
        }
        if (param.get("closureStatusList")!=null && !"".equals(param.get("closureStatusList")+"")){
            String[] arr = param.get("closureStatusList").toString().split(",");
            sb.append(" and aah.closure_status in ('"+StringUtils.join(arr, "','")+"')");
        }
        if (param.get("date1")!=null && param.get("date2")!=null && !"".equals(param.get("date1")+"") && !"".equals(param.get("date2")+"")){
            if (param.get("TimeType")!=null){
                if ("1".equals(param.get("TimeType")+"")){
                    sb.append(" and aah.share_time BETWEEN '"+param.get("date1")+"' AND '"+param.get("date2")+"'");
                }else if("2".equals(param.get("TimeType")+"")){
                    sb.append(" and aah.closure_time BETWEEN '"+param.get("date1")+"' AND '"+param.get("date2")+"'");
                }else if("3".equals(param.get("TimeType")+"")){
                    sb.append(" and aai.help_begintime BETWEEN '"+param.get("date1")+"' AND '"+param.get("date2")+"'");
                }else if("4".equals(param.get("TimeType")+"")){
                    sb.append(" and aai.help_endtime BETWEEN '"+param.get("date1")+"' AND '"+param.get("date2")+"'");
                }
            }
        }
        sb.append(" order by aah.share_time desc");
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
    public ResultBody getActivityStatus(Map map) {

        return ResultBody.success(activityInfoDao.getActivityStatus(map));
    }


    /**
     * 活动报名明细活动列表
     * */
    @Override
    public List<Map> getSignUpActivityList(Map map) {
        return activityInfoDao.getSignUpActivityList(map);
    }


    /**
     * 导出
     * */
    @Override
    public ResultBody getExcel(HttpServletRequest request, HttpServletResponse response, String activity_id,String exportType) {
        //根据活动查询一下动态字段
        String signUpDiyCode = activityInfoDao.getActivityDiyCode(activity_id);
        List<Map> withdrawVos = activityInfoDao.getSignUpDetailByActivityId(activity_id);



        // 创建工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建表
        HSSFSheet sheet = workbook.createSheet("报名明细");
        // 创建行
        HSSFRow row = sheet.createRow(0);
        // 创建单元格样式
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 14);//设置字体大小
        cellStyle.setFont(font);
        // 表头
        List headList = new ArrayList();
        headList.add("序号");
        headList.add("客户姓名");
        headList.add("客户手机号");
        headList.add("报名项目");
        headList.add("签到项目");
        headList.add("报名时间");
        headList.add("扫码签到时间");
        headList.add("签到方式");
        headList.add("确访签到时间");
        headList.add("确访操作人");
        headList.add("活动编号");
        headList.add("活动名称");
        headList.add("关联项目");
        headList.add("活动状态");
        sheet.setDefaultRowHeight((short) (20 * 20));
        sheet.setDefaultColumnWidth((short)20);
        JSONArray jsonArray = JSONUtil.parseArray(signUpDiyCode);
        for (Object object : jsonArray) {
            JSONObject jsonObject = JSONObject.parseObject(object.toString());
            System.out.println(jsonObject.get("codeName"));
            headList.add(jsonObject.get("codeName"));
        }
        HSSFCell cell;
        // 设置表头
        for (int iHead = 0; iHead < headList.size(); iHead++) {
            cell = row.createCell(iHead);
            cell.setCellValue(headList.get(iHead).toString());
            cell.setCellStyle(cellStyle);
        }
        // 设置表格内容
        for (int iBody = 0; iBody < withdrawVos.size(); iBody++) {
            row = sheet.createRow(iBody + 1);
            Map res = withdrawVos.get(iBody);
            String[] userArray = new String[110];
            userArray[0] = res.get("rownum")+"";
            userArray[1] = res.get("customerName")+"";
            if ("1".equals(exportType)){
                userArray[2] = res.get("customerMobile")+"";
            }
            if ("2".equals(exportType)){
                userArray[2] = res.get("customerMobileAll")+"";
            }

            userArray[3] = res.get("signup_projectname")+"";
            userArray[4] = res.get("signinProjectName")+"";
            userArray[5] = res.get("signup_time")+ "";
            userArray[6] = res.get("signin_time") + "";
            userArray[7] = res.get("signinTypeDesc")+"";
            userArray[8] = res.get("signin_time_auto") + "";
            userArray[9] = res.get("signin_time_autoname") + "";
            userArray[10] = res.get("activity_no")+"" ;
            userArray[11] = res.get("activity_name")+"" ;
            userArray[12] = res.get("relationProject")+"" ;
            userArray[13] = res.get("actStatus")+"" ;
            if(res.get("signup_diy_code")!=null &&!res.get("signup_diy_code").equals("")&&!res.get("signup_diy_code").equals(" ")){
                JSONArray jsonArrayTwo = JSONUtil.parseArray(res.get("signup_diy_code")+"");
                for (int i = 0; i < jsonArrayTwo.size(); i++) {
                    JSONObject jsonObject = JSONObject.parseObject(jsonArrayTwo.get(i).toString());
                    userArray[i+14] = jsonObject.get("codeValue")+"";
                }
            }
            for (int iArray = 0; iArray < userArray.length; iArray++) {
                row.createCell(iArray).setCellValue(userArray[iArray]);
            }
        }
        // 生成Excel文件
        FileCommonUtil.createFile(response, workbook);
        return null;
    }

    @Override
    public ResultBody getActivityHelpCount(Map map) {

        return ResultBody.success(activityInfoDao.getActivityHelpCount(map));
    }



    @Override
    public void activitySignUpDetailedExport(HttpServletRequest request, HttpServletResponse response, String param) {
        Map result = new HashMap();
        result = JSONObject.parseObject(param,Map.class);
        String isAll = result.get("isAll")+"";

        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("4");
        excelExportLog.setMainTypeDesc("活动管理");
        excelExportLog.setSubType("H1");
        excelExportLog.setSubTypeDesc("活动报名明细");
        excelExportLog.setCreator(result.get("userId")+"");
        if ("1".equals(isAll)){
            excelExportLog.setExportType("2");
        }else{
            excelExportLog.setExportType("1");
        }
        excelExportLog.setIsAsyn("0");
        String ids = result.get("projectStr")+"";
        if ("".equals(ids)){
            excelExportLog.setAreaName("/");
            excelExportLog.setProjectId("/");
            excelExportLog.setProjectName("/");
        }else{
            String[] arr = ids.split(",");
            List<String> proIdList= new ArrayList<>(Arrays.asList(arr));
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        List<ActivitySignUpVO> list = activityInfoDao.getActivitySignUpDetailedList(result);
        if (list!=null && list.size()>0){
            String[] headers = list.get(0).getActivityTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();

            Integer i=1;
            for (ActivitySignUpVO ac:list) {
                ac.setRownum(i.toString());
                Object[] oArray = ac.toActivityData(isAll);
                dataset.add(oArray);
                i++;
            }
            try{
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("活动报名明细",headers,dataset,"活动报名明细",response,null);
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
    public void activityHelpDetailedExport(HttpServletRequest request, HttpServletResponse response, String param) {
        Map result = new HashMap();
        result = JSONObject.parseObject(param,Map.class);
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("4");
        excelExportLog.setMainTypeDesc("活动管理");
        excelExportLog.setSubType("H2");
        excelExportLog.setSubTypeDesc("活动助力明细");
        excelExportLog.setCreator(result.get("userId")+"");
        String isAll = result.get("isAll")+"";
        if ("1".equals(isAll)){
            excelExportLog.setExportType("2");
        }else{
            excelExportLog.setExportType("1");
        }
        excelExportLog.setIsAsyn("0");

        String ids = result.get("projectStr")+"";
        if ("".equals(ids)){
            excelExportLog.setAreaName("/");
            excelExportLog.setProjectId("/");
            excelExportLog.setProjectName("/");
        }else{
            String[] arr = ids.split(",");
            List<String> proIdList= new ArrayList<>(Arrays.asList(arr));
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        List<ActivityHelpVO> list = activityInfoDao.getActivityHelpDetailedList(result);
        if (list!=null && list.size()>0){
            String[] headers = list.get(0).getActivityHelpTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();
            list.forEach(item -> {
                // 判断当前助力状态
                Integer status = item.getHelpStatus();
                switch (status) {
                    case 1:{
                        item.setHelpStatusName("未完成");
                        break;
                    }
                    case 2 : {
                        item.setHelpStatusName("已完成");
                        break;
                    }
                }
                item.setLeve1No(null != item.getHelpNum()? Integer.valueOf(item.getHelpNum()) : 0);
                if (org.apache.commons.lang3.StringUtils.isNotBlank(item.getActivityId())){
                    List<HelpCoupon> helpCoupons = activityInfoDao.getHelpCoupons(item.getActivityId());
                    if (!CollectionUtils.isEmpty(helpCoupons)){
                        helpCoupons.forEach(coupon -> {
                            if (org.apache.commons.lang3.StringUtils.isNotBlank(item.getShareOpenId())){
                                List<CouponLock> couponLocks = activityInfoDao.queryIsLock(coupon.getCouponId(), item.getShareOpenId());
                                if (!CollectionUtils.isEmpty(couponLocks)){
                                    Integer isLock = couponLocks.get(0).getIsLock();
                                    if(null != isLock && (1 == isLock || isLock == 3)){
                                        item.setLockCouponNo(coupon.getCouponNo());
                                        item.setLockCouponId(coupon.getCouponId());
                                        item.setLockCouponName(coupon.getCouponName());
                                    }
                                    if(null != isLock && isLock == 3){
                                        item.setReceiveCouponNo(coupon.getCouponNo());
                                        item.setReceiveCouponId(coupon.getCouponId());
                                        item.setReceiveCouponName(coupon.getCouponName());
                                    }
                                }

                            }
                            if (null != coupon.getLevel() && 1== coupon.getLevel()){
                                item.setLeve1No(coupon.getTargetNumber());
                            }
                            if (null != coupon.getLevel() && 2== coupon.getLevel()){
                                item.setLeve2No(coupon.getTargetNumber());
                            }
                            if (null != coupon.getLevel() && 3== coupon.getLevel()){
                                item.setLeve3No(coupon.getTargetNumber());
                            }
                        });
                    }
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(item.getClosureId())){
                    item.setHeXiaoCouponId(item.getReceiveCouponId());
                    item.setHeXiaoCouponName(item.getReceiveCouponName());
                }
            });
            for (ActivityHelpVO ac:list) {
                Object[] oArray = ac.toActivityHelpData(isAll);
                dataset.add(oArray);
            }
            try{
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("活动助力明细",headers,dataset,"活动助力明细",response,null);
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
    public List<Map> getHelpDetail(Map map) {
        return activityInfoDao.getHelpDetail(map);
    }

    @Override
    public ResultBody getActivityCensusDetail(Map map) {
        PageHelper.startPage((int) map.get("pageNum"), (int) map.get("pageSize"));
        Page<Map> list = null;
        if("1".equals(String.valueOf(map.get("type")))){
            list = activityInfoDao.getActivitySignUpListByActId(String.valueOf(map.get("activityId")));
        }else if("2".equals(String.valueOf(map.get("type")))){
            list = activityInfoDao.getActivitySignInListByActId(String.valueOf(map.get("activityId")));
        }else if("3".equals(String.valueOf(map.get("type")))){
            list = activityInfoDao.getActivityHelpListByActId(String.valueOf(map.get("activityId")));
        }else if("4".equals(String.valueOf(map.get("type")))){
            list = activityInfoDao.getActivityHelpDetailListByActId(String.valueOf(map.get("activityId")));
        }else if("5".equals(String.valueOf(map.get("type")))){
            list = activityInfoDao.getActivityCouponDetailListByActId(String.valueOf(map.get("activityId")));
        }
        return ResultBody.success(new PageInfo<Map>(list));
    }



    @Override
    public List<Map> getActivityPro(Map map1) {
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
        List<Map> pro = activityInfoDao.getActivityProName(map1);
        if (mapList != null && mapList.size() > 0 && pro!=null && pro.size()>0) {
            List<String> proList = new ArrayList<>();
            for (Map mainPro:mapList) {
                proList.add(mainPro.get("projectId")+"");
            }
            for (int i=0;i< pro.size();i++) {
                Map proMap = pro.get(i);
                if (!proList.contains(proMap.get("projectId")+"")){
                    pro.remove(proMap);
                    i--;
                }
            }
            return pro;
        }else{
            return new ArrayList<>();
        }

    }

    @Override
    public ResultBody createQr(Map map) {
        if (map==null || map.get("activityId")==null
                || map.get("activityName")==null
                || map.get("projectId")==null
                || map.get("projectName")==null){
            return ResultBody.error(-21_0006,"必传参数未传！！");
        }
        JSONObject josn = new JSONObject();
        josn.put("activityId",map.get("activityId"));
        josn.put("activityName",map.get("activityName"));
        josn.put("projectId",map.get("projectId"));
        josn.put("projectName",map.get("projectName"));
        josn.put("type","2");
        return ResultBody.success(QrCodeUtils.creatRrCode(josn.toString(),1280,1280));
    }



    @Override
    public ResultBody selectCityByPro(Map param) {
        if (param == null || param.get("ids") == null || "".equals(param.get("ids"))) {
            return ResultBody.error(-21_0006, "必传参数未传！！");
        }
        String name = "";
        StringBuffer sb = new StringBuffer();
        if (param.get("ids") instanceof ArrayList<?>) {
            for (Object o : (List<?>) param.get("ids")) {
                String id = String.class.cast(o);
                sb.append("'" + id + "',");
            }
        }
        String ids = sb.toString().substring(0, sb.toString().length() - 1);
        List<String> names = activityInfoDao.selectCityByPro(ids);
        if (names.size() > 0) {
            StringBuffer sb1 = new StringBuffer();
            for (int i = 0; i < names.size(); i++) {
                if (i == names.size() - 1) {
                    sb1.append(names.get(i));
                } else {
                    sb1.append(names.get(i) + ",");
                }
            }
            name = sb1.toString();
        }
        return ResultBody.success(name);
    }

    @Override
    public ResultBody selectCoupon(Map param) {
        if (param == null || param.get("ids") == null || "".equals(param.get("ids"))) {
            return ResultBody.error(-21_0006, "必传参数未传！！");
        }
        StringBuffer sb = new StringBuffer();
        if (param.get("ids") instanceof ArrayList<?>) {
            for (Object o : (List<?>) param.get("ids")) {
                String id = String.class.cast(o);
                sb.append("'" + id + "',");
            }
        }
        String proIds = sb.toString().substring(0, sb.toString().length() - 1);
        String couponName = "";
        if (param.get("couponName") != null && !"".equals(param.get("couponName"))) {
            couponName = param.get("couponName") + "";
        }
        //获取优惠券信息
        List<Map> coupons = activityInfoDao.selectCoupon(param.get("id") + "", proIds, couponName);
        if (coupons != null && coupons.size() > 0) {
            for (Map map : coupons) {
                //获取优惠券项目
                List<String> proNames = activityInfoDao.getCouponPro(map.get("id") + "");
                StringBuffer sbss = new StringBuffer();
                for (int i = 0; i < proNames.size(); i++) {
                    if (i == proNames.size() - 1) {
                        sbss.append(proNames.get(i));
                    } else {
                        sbss.append(proNames.get(i) + ",");
                    }
                }
                map.put("projectName", sbss.toString());
                if(null !=  map.get("helpStatus") && map.get("helpStatus").toString().equals("1")){
                    map.put("activityId",null);
                }
            }
        }
        return ResultBody.success(coupons);
    }

    @Override
    public ResultBody selectAwardCoupon(Map param){
        List<Map> coupons = activityInfoDao.selectAwardCoupon(param);
        return ResultBody.success(coupons);
    }

    @Override
    public ResultBody selectCouponByProjectId(Map param) {
        if (null == param.get("projectIds") ||  CollectionUtils.isEmpty((List<String>) param.get("projectIds"))){
            return ResultBody.success(null);
        }
        List<Map> coupons = activityInfoDao.selectCouponByProjectId(param);
        return ResultBody.success(coupons);
    }

    @Override
    public void updateCouNo() {
        List<String> ids = activityInfoDao.getActCount();
        String actNoPre = "ACT";
        for (int i = 0; i < ids.size(); i++) {
            int randomFour = (int) ((Math.random() * 9 + 1) * 1000);
            Date day1=new Date();
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = df1.format(day1);
            String actNo = actNoPre + "" + format + randomFour;
            activityInfoDao.updateCouNo(actNo,ids.get(i));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addRidActivity(RidActivityFrom ridActivityFrom) {
        try {
            if (org.apache.commons.lang3.StringUtils.isBlank(ridActivityFrom.getProjectId()))  {
                return ResultBody.error(992204,"项目不能为空");
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(ridActivityFrom.getBuildBookId())) {
                return ResultBody.error(992205,"楼盘不能为空");
            }

            if (org.apache.commons.lang3.StringUtils.isBlank(ridActivityFrom.getActivityBegintime())) {
                return ResultBody.error(992210,"活动开始时间不能为空");
            }

            if (org.apache.commons.lang3.StringUtils.isBlank(ridActivityFrom.getActivityEndtime())) {
                return ResultBody.error(992211,"活动结束时间不能为空");
            }

            if (org.apache.commons.lang3.StringUtils.isBlank(ridActivityFrom.getActivityNote())) {
                return ResultBody.error(992212,"活动规则不能为空");
            }

            if (org.apache.commons.lang3.StringUtils.isBlank(ridActivityFrom.getIsSubscribe())) {
                return ResultBody.error(992211,"预约状态不能为空");
            }

            String id = ridActivityFrom.getUserId();
            if (org.apache.commons.lang3.StringUtils.isBlank(id)) {
                return ResultBody.error(992206,"用户信息参数未传！！");
            }
            // 查询所有的乘车活动楼盘id集合
            List<String> ridActBuildBookIds = activityInfoDao.getAllRidActBuildBookIds(ridActivityFrom.getProjectId());
            if (ridActBuildBookIds.contains(ridActivityFrom.getBuildBookId())) {
                return ResultBody.error(992200,"当前楼盘已存在乘车活动,不可重复添加!");
            }
            // 生成活动主键id
            String ridActId = UUID.randomUUID().toString().replace("-","");

            // 获取项目名称
            String proName = activityInfoDao.getProjectName(ridActivityFrom.getProjectId());
            ridActivityFrom.setProjectName(proName);

            // 保存活动主体信息
            ridActivityFrom.setId(ridActId);
            activityInfoDao.addRidActivity(ridActivityFrom);

            // 查询对应的楼盘信息
            Map map =  activityInfoDao.getBuildBookInfoById(ridActivityFrom.getBuildBookId());
            // 保存创建者信息
            map.put("userId",id);
            // 保存活动id
            map.put("activityId",ridActId);
            // 生成楼盘活动主键
            String uuid = UUID.randomUUID().toString().replace("-","");
            map.put("id",uuid);
            // 保存活动对应的楼盘信息
            activityInfoDao.addRidActivityBuildBookInfo(map);
            return ResultBody.success(ridActId);
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003, "添加乘车活动异常！！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateRidActivity(RidActivityFrom ridActivityFrom) {
        try {
            if (org.apache.commons.lang3.StringUtils.isBlank(ridActivityFrom.getProjectId()))  {
                return ResultBody.error(992204,"项目所需参数未传,新增失败!!!");
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(ridActivityFrom.getBuildBookId())) {
                return ResultBody.error(992205,"楼盘所需参数未传,新增失败!!!");
            }

            String id = ridActivityFrom.getUserId();
            if (org.apache.commons.lang3.StringUtils.isBlank(id)) {
                return ResultBody.error(992206,"用户信息参数未传！！");
            }

            // 获取项目名称
            String proName = activityInfoDao.getProjectName(ridActivityFrom.getProjectId());
            ridActivityFrom.setProjectName(proName);

            activityInfoDao.updateRidActivity(ridActivityFrom);
            // 查询对应的楼盘信息
            Map map =  activityInfoDao.getBuildBookInfoById(ridActivityFrom.getBuildBookId());
            // 保存更新者信息
            map.put("userId",id);
            // 获取活动主键
            String activityFromId = ridActivityFrom.getId();
            map.put("activityId",activityFromId);
            // 保存活动对应的楼盘信息
            activityInfoDao.updateRidActivityBuildBookInfo(map);
            return ResultBody.success("更新乘车活动成功");
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(10001, "更新乘车活动异常！！");
        }
    }

    @Override
    public ResultBody getRidActivityInfo(String ridActivityId) {
        RidActivityInfoVo ridActivityInfo = activityInfoDao.getRidActivityInfo(ridActivityId);
        return ResultBody.success(ridActivityInfo);
    }

    @Override
    public ResultBody changeRidActStatus(ChangeStatusVo changeStatusVo) {

        try {
            activityInfoDao.changeStatus(changeStatusVo);
            return ResultBody.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(992201, "修改乘车活动状态异常！！");
        }
    }

    @Override
    public List<RidActivityInfoVo> queryAllRidActivity() {
        return activityInfoDao.queryAllRidActivity();
    }

    @Override
    public ResultBody<PageInfo<RidActivityInfoVo>> getRidActsByConditions(RidActConditionsVo ridActConditionsVo) {
        // 分页设置
        PageHelper.startPage((int)ridActConditionsVo.getCurrent(),(int)ridActConditionsVo.getSize());
        // 全国查询全部
        if (ridActConditionsVo.getCityId().equals("00000000-0000-0000-0000-000000000000")) {
            ridActConditionsVo.setCityId("");
        }
        List<RidActivityInfoVo> list = activityInfoDao.getRidActsByConditions(ridActConditionsVo);

        // 查询出全部点击次数
        List<ActDataStatisticsVo> allClickCount = activityInfoDao.getAllClickCount();

        int num = 1;
        for (RidActivityInfoVo ridActivityInfoVo : list) {
            // 设置行号
            ridActivityInfoVo.setRowNo(num + "");
            num++;
            for (ActDataStatisticsVo actDataStatisticsVo : allClickCount) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(ridActivityInfoVo.getActivityType())
                        && ridActivityInfoVo.getActivityType() .equals("5")
                        && ridActivityInfoVo.getActivityId().equals(actDataStatisticsVo.getActivityId())) {
                    ridActivityInfoVo.setClickCount(actDataStatisticsVo.getDataCount() + "");
                }
            }
            if (null == ridActivityInfoVo.getClickCount()) {
                ridActivityInfoVo.setClickCount(0+"");
            }
        }
        PageInfo<RidActivityInfoVo> lists = new PageInfo<RidActivityInfoVo>(list);
        return ResultBody.success(lists);
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static String  preKey = "CLICK_COUNT_PRE:";

    @Override
    public ResultBody delRidActById(String ridActId) {
        try {
            String countKey = preKey + ":" + ridActId;
            // 删除缓存中的对应的数据
            stringRedisTemplate.delete(countKey);
            // 删除活动统计中对应的数据
            activityInfoDao.delRidStatisticsById(ridActId);
            // 删除活动表中数据
            activityInfoDao.delRidActById(ridActId);
            return ResultBody.success("乘车活动删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(992203, "删除乘车活动异常！！");
        }
    }

    @Override
    public void ridActExport(HttpServletRequest request,HttpServletResponse response,RidActConditionsVo ridActConditionsVo)  {
        // 创建导出记录对象
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("9");
        excelExportLog.setMainTypeDesc("乘车活动管理");
        excelExportLog.setSubType("CAR1");
        excelExportLog.setSubTypeDesc("乘车活动列表");
        // 无限制
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        List<String> ids = ridActConditionsVo.getProjectIds();
        if (CollectionUtils.isEmpty(ids)){
            excelExportLog.setAreaName("/");
            excelExportLog.setProjectId("/");
            excelExportLog.setProjectName("/");
        }else{
            Map proMap = excelImportMapper.getAreaNameAndProNames(ids);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }

        // 全国查询全部
        if (ridActConditionsVo.getCityId().equals("00000000-0000-0000-0000-000000000000")) {
            ridActConditionsVo.setCityId("");
        }
        // 根据条件查询数据
        List<RidActivityInfoVo> list = activityInfoDao.getRidActsByConditions(ridActConditionsVo);
        if (!CollectionUtils.isEmpty(list)) {
            String[] headers = list.get(0).getRidActTitle;
            ArrayList<Object[]> dataset = new ArrayList<>();
            AtomicInteger rowNum = new AtomicInteger(1);
            list.forEach(item -> {
                item.setRowNo(rowNum + "");
                // 判断当前活动状态
                String status = item.getStatus();
                switch (status) {
                    case "0": {
                        item.setStatus("未启用");
                        break;
                    }
                    case "1": {
                        item.setStatus("已启用");
                        break;
                    }
                    case "2": {
                        item.setStatus("已开始");
                        break;
                    }
                    case "3": {
                        item.setStatus("已结束");
                        break;
                    }
                }
                // 判断是否可以开启预约
                String isSubscribe = item.getIsSubscribe();
                switch (isSubscribe) {
                    case "1": {
                        item.setIsSubscribe("已启用预约");
                        break;
                    }
                    case "0": {
                        item.setIsSubscribe("未启用预约");
                        break;
                    }
                }
                rowNum.getAndIncrement();
            });
            for (RidActivityInfoVo ridActivityInfoVo:list) {
                Object[] oArray = ridActivityInfoVo.toRidActData();
                dataset.add(oArray);
            }
            try{
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("乘车活动",headers,dataset,"乘车活动",response,null);
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
    public ResultBody<PageInfo<RidActDetailsVo>> getRidActTripDetails(RidActConditionsVo ridActConditionsVo) {
        // 设置开启分页
        PageHelper.startPage((int)ridActConditionsVo.getCurrent(),(int)ridActConditionsVo.getSize());
        // 全国查询全部
        if (ridActConditionsVo.getCityId().equals("00000000-0000-0000-0000-000000000000")) {
            ridActConditionsVo.setCityId("");
        }
        // 条件查询行程详情列表
        List<RidActDetailsVo> ridActDetailsVoList = activityInfoDao.getRidActTripDetails(ridActConditionsVo);
        AtomicInteger num = new AtomicInteger(1);
        ridActDetailsVoList.forEach(ridActDetailsVo -> {
            ridActDetailsVo.setRowNo(num + "");
            num.getAndIncrement();
        });
        PageInfo<RidActDetailsVo> pageInfo = new PageInfo<>(ridActDetailsVoList);
        return ResultBody.success(pageInfo);
    }

    @Override
    public void ridActDetailsExport(HttpServletRequest request, HttpServletResponse response, RidActConditionsVo ridActConditionsVo) {
        // 创建导出记录对象
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("9");
        excelExportLog.setMainTypeDesc("乘车活动管理");
        excelExportLog.setSubType("CAR2");
        excelExportLog.setSubTypeDesc("乘车活动明细");
        // 设置导出记录导出类型
        if (ridActConditionsVo.getExportType() == 1) {
            excelExportLog.setExportType("2");
        }
        excelExportLog.setExportType("1");
        excelExportLog.setIsAsyn("0");
        List<String> ids = ridActConditionsVo.getProjectIds();
        if (CollectionUtils.isEmpty(ids)){
            excelExportLog.setAreaName("/");
            excelExportLog.setProjectId("/");
            excelExportLog.setProjectName("/");
        }else{
            Map proMap = excelImportMapper.getAreaNameAndProNames(ids);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }

        // 全国查询全部
        if (ridActConditionsVo.getCityId().equals("00000000-0000-0000-0000-000000000000")) {
            ridActConditionsVo.setCityId("");
        }
        // 根据条件查询数据
        List<RidActDetailsVo> list = activityInfoDao.getRidActTripDetails(ridActConditionsVo);
        if (!CollectionUtils.isEmpty(list)) {
            Object[] oArray = null;
            String[] headers = list.get(0).getRidActTitle;
            ArrayList<Object[]> dataset = new ArrayList<>();
            AtomicInteger rowNum = new AtomicInteger(1);
            if (!CollectionUtils.isEmpty(list)) {
                for (RidActDetailsVo ridActDetailsVo:list) {
                    ridActDetailsVo.setRowNo(rowNum +"");
                    String status = ridActDetailsVo.getTripStatus();
                    switch (status) {
                        case "1": {
                            ridActDetailsVo.setTripStatus("行程正常");
                            break;
                        }
                        case "2": {
                            ridActDetailsVo.setTripStatus("行程取消");
                            break;
                        }
                    }
                    rowNum.getAndIncrement();
                    // 全号导出
                    if (ridActConditionsVo.getExportType() == 1) {
                        oArray = ridActDetailsVo.toRidActData();
                    }
                    // 引号导出
                    if (ridActConditionsVo.getExportType() == 2) {
                        oArray = ridActDetailsVo.roRidActSecData();
                    }
                    dataset.add(oArray);
                }
            }
            try{
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("乘车活动明细",headers,dataset,"乘车活动明细",response,null);
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
    public ResultBody selectActivityById(Map param) {
        if (param == null || param.get("id") == null || "".equals(param.get("id"))) {
           return ResultBody.error(-21_0006, "必传参数未传！！");
        }
        String id = param.get("id") + "";
        //查询活动基本信息
        ActivityInfoVO activityInfoVO = activityInfoDao.getActivityById(id);
        //查询活动素材
        List<ActivityMaterial> materials = activityInfoDao.selectActivityPhoto(id);
        //查询活动关联项目
        List<String> proS = activityInfoDao.getActivityPro(id);
        //查询奖项信息
        List<Map> list = activityInfoDao.getAwardInfoList(id);
        //查詢活動地址
        List<ActivityAddress> activityAddressList = activityInfoDao.getActivityAddressList(id);
        //查询优惠券
        List<HelpCoupon> helpCoupons =  activityInfoDao.getHelpCoupons(id);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(helpCoupons)){
            helpCoupons.forEach(item -> {
                List<String> proNames = activityInfoDao.getCouponPro(item.getCouponId());
                if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(proNames)){
                    item.setProjectName( StringUtils.join(proNames, ","));;
                }
            });
        }
        Integer helpCount =  activityInfoDao.getCountHelp(id);
        if (helpCount > 0){
            activityInfoVO.setHelpStatus(1);
        }else {
            activityInfoVO.setHelpStatus(0);
        }
        activityInfoVO.setMaterials(materials);
        activityInfoVO.setProjectList(proS);
        activityInfoVO.setAddressList(activityAddressList);
        activityInfoVO.setAwardInfoList(list);
        activityInfoVO.setHelpCoupons(helpCoupons);
        Map map =  null;
//        if (activityInfoVO.getIsHomePageHot().equals("0")) {
            // 查询首页热推图
            map =  activityInfoDao.getHotPageInfo(id);
            if (map != null) {
                String cityIds = map.get("city_id") + "";
                String[] split = cityIds.split(",");
                List<String> cityIdss = Arrays.asList(split);
                map.put("city_id",cityIdss);
            }
            activityInfoVO.setMap(map);
//        }
        return ResultBody.success(activityInfoVO);
    }


    /**
     * Do -> VO
     *
     * @param list 对象
     * @return VO对象
     */
    private List<ActivityInfoVO> convert(List<ActivityInfo> list) {
        List<ActivityInfoVO> activityInfoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return activityInfoList;
        }
        for (ActivityInfo source : list) {
            ActivityInfoVO target = new ActivityInfoVO();
            BeanUtils.copyProperties(source, target);
            activityInfoList.add(target);
        }
        return activityInfoList;
    }

}
