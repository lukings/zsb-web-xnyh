package cn.visolink.system.activity.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.activity.dao.ActivityInfoDao;
import cn.visolink.system.activity.dao.CouponInfoDao;
import cn.visolink.system.activity.model.CouponInfo;
import cn.visolink.system.activity.model.form.CouponDetailForm;
import cn.visolink.system.activity.model.form.CouponInfoForm;
import cn.visolink.system.activity.model.vo.CouponDetailVO;
import cn.visolink.system.activity.model.vo.CouponInfoVO;
import cn.visolink.system.activity.service.CouponInfoService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>
 * CouponInfo服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2020-05-27
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoDao, CouponInfo> implements CouponInfoService {

    @Autowired
    private CouponInfoDao couponInfoDao;
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

    @Autowired
    private ActivityInfoDao activityInfoDao;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addCouponInfo(CouponInfoForm param) {
        //优惠券ID
        String id = UUID.randomUUID().toString().replaceAll("-","");
        try{
            param.setId(id);
            String createtime = sf.format(new Date());
            String creator = SecurityUtils.getUserId();
            param.setCreatetime(createtime);
            param.setCreator(creator);
            //保存优惠券关联项目
            List<String> proIds = param.getProjectList();
            if (proIds!=null && proIds.size()>0){
                for (String projectId:proIds) {
                    String projectName = couponInfoDao.getProNameById(projectId);
                    Map proMap = new HashMap();
                    proMap.put("couponId",id);
                    proMap.put("projectId",projectId);
                    proMap.put("projectName",projectName);
                    couponInfoDao.insertCouponPro(proMap);
                }
            }
            //保存优惠券
            param.setStockSurplus(param.getStockNo());
            couponInfoDao.insertCouponInfo(param);
        }catch (Exception e){
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003,"添加优惠券异常！！");
        }

        return ResultBody.success(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateCouponInfoById(CouponInfoForm param) {
        try{
            //优惠券ID
            String id = param.getId();
            String creator = SecurityUtils.getUserId();
            param.setCreator(creator);
            //更新优惠券关联项目
            if (param.getProjectList()!=null && param.getProjectList().size()>0){
                //查询原关联项目
                List<String> oldProIds = couponInfoDao.getCouponPro(id);
                //本次操作的关联项目ID
                List<String> proIds = param.getProjectList();
                for (String proId:oldProIds) {
                    //删除解除关联的项目
                    Map del = new HashMap();
                    del.put("couponId",id);
                    del.put("projectId",proId);
                    couponInfoDao.deleteCouponPro(del);
                }
                if (proIds.size()>0){
                    for (String proId:proIds) {
                        String projectName = couponInfoDao.getProNameById(proId);
                        //添加关联项目
                        Map proMap = new HashMap();
                        proMap.put("couponId",id);
                        proMap.put("projectId",proId);
                        proMap.put("projectName",projectName);
                        couponInfoDao.insertCouponPro(proMap);
                    }
                }
            }
            //更新优惠券
            couponInfoDao.updateCouponInfo(param);

        }catch (Exception e){
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003,"更新优惠券异常！！");
        }

        return ResultBody.success("更新优惠券成功！！");
    }

    @Override
    public CouponInfoVO getCouponInfoById(String id) {
        CouponInfoVO couponInfoVO = couponInfoDao.getCouponInfoById(id);
        if (couponInfoVO!=null){
            List<String> ids = couponInfoDao.getCouponPro(id);
            couponInfoVO.setProjectList(ids);
        }
        Integer helpCount =  activityInfoDao.getCountHelp(couponInfoVO.getActivityId());
        if (helpCount > 0){
            couponInfoVO.setHelpStatus(1);
        }else {
            couponInfoVO.setHelpStatus(0);
        }
        return couponInfoVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateCouponStatusById(Map map) {
        try{
            if (map!=null){
                if (map.get("id")==null || "".equals(map.get("id")+"")){
                    return  ResultBody.error(-21_0006,"必传参数未传！！");
                }
                if ((map.get("isDel")==null || "".equals(map.get("isDel")+""))
                        && (map.get("couponStatus")==null || "".equals(map.get("couponStatus")+""))
                        && (map.get("status")==null || "".equals(map.get("status")+""))){
                    return  ResultBody.error(-21_0006,"参数未传！！");
                }else{
                    map.put("userId",SecurityUtils.getUserId());
                    couponInfoDao.updateCouponInfoStatus(map);
                }
            }else{
                return  ResultBody.error(-21_0006,"参数异常！！");
            }
        }catch (Exception e){
            e.printStackTrace();
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003,"更新优惠券状态异常！！");
        }
        return ResultBody.success("更新优惠券状态成功！！");
    }

    @Override
    public PageInfo<CouponInfoVO> getCouponInfoPage(CouponInfoForm param) {
        // 分页数据设置
        List<String> proIdList = new ArrayList<>();
        if (param.getProjectList()!=null && param.getProjectList().size()>0){

        }else {
            Map map=new HashMap();
            map.put("UserName", param.getUserName());
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
            List<Map> mapList = projectMapper.findProjectListByUserName( param.getUserName()+"","",userInfoMap.get("JobCode").toString(),sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (Map proMap:mapList) {
                    proIdList.add(proMap.get("projectId")+"");
                }
            }
            param.setProjectList(proIdList);
        }
        PageHelper.startPage((int) param.getCurrent(), (int) param.getSize());
        List<CouponInfoVO> list = couponInfoDao.getAllCouponInfoVO(param);
        if (list!=null && list.size()>0){
            for (CouponInfoVO c:list) {
                if(null != c.getHelpStatus() && 1== c.getHelpStatus()){
                    c.setLockNo(couponInfoDao.queryLockNo(c.getId(),1));
                    String s = couponInfoDao.queryLockNo(c.getId(), 3).toString();
                    c.setCouponCollected(s);
                    c.setCollectionCstCount(s);
                    c.setCollectionCount(s);
                }

                List<Map> proMap = couponInfoDao.getProNameAndAreaName(c.getId());
                if (proMap!=null && proMap.size()>0){
                    Set<String> aName = new HashSet<>();
                    StringBuffer sb = new StringBuffer();
                    StringBuffer sb1 = new StringBuffer();
                    for (int i = 0; i < proMap.size(); i++) {
                        aName.add(proMap.get(i).get("AreaName")+"");
                        //项目
                        if (i==proMap.size()-1){
                            sb1.append(proMap.get(i).get("ProjectName")+"");
                        }else{
                            sb1.append(proMap.get(i).get("ProjectName")+",");
                        }
                    }
                    //区域
                    List<String> names = new ArrayList<>(aName);
                    for (int i = 0; i < names.size(); i++) {
                        if (i==names.size()-1){
                            sb.append(names.get(i));
                        }else{
                            sb.append(names.get(i)+",");
                        }
                    }
                    c.setAreaNameNames(sb.toString());
                    c.setProjectNames(sb1.toString());
                }
            }
        }
        return new PageInfo<CouponInfoVO>(list);
    }


    @Override
    public String couponInfoExportNew(CouponInfoForm param) {
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        List<String> proIdList = new ArrayList<>();
        if (param.getProjectIds() != null && !"".equals(param.getProjectIds())) {
            String[] ids = param.getProjectIds().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
        } else {
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
            List<Map> mapList = projectMapper.findProjectListByUserName(SecurityUtils.getUsername() + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList != null && mapList.size() > 0) {
                for (Map proMap : mapList) {
                    proIdList.add(proMap.get("projectId") + "");
                }
            }
        }
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(SecurityUtils.getUserId());
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setId(id);
        excelExportLog.setMainType("4");
        excelExportLog.setMainTypeDesc("活动管理");
        excelExportLog.setSubType("H4");
        excelExportLog.setSubTypeDesc("优惠券列表");
        excelExportLog.setExportType("3");
        excelExportLog.setExportStatus("1");
        excelExportLog.setIsAsyn("1");
        //sql拼接
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT " +
                "info.id, " +
                "info.coupon_no couponNo，" +
                "info.coupon_name couponName, " +
                "aa.activity_no activityNo," +
                "aa.activity_name activityName, " +
                "DATE_FORMAT(info.publish_time,'%Y-%m-%d %H:%i:%s') publishTime, " +
                "DATE_FORMAT(info.begintime,'%Y-%m-%d %H:%i:%s') begintime, " +
                "DATE_FORMAT(info.endtime,'%Y-%m-%d %H:%i:%s') endtime, DATE_FORMAT(info.createtime,'%Y-%m-%d %H:%i:%s') createtime," +
                "(case when info.disabletime is null then '/' else DATE_FORMAT(info.disabletime,'%Y-%m-%d %H:%i:%s') end) disabletime, " +
                "ba.EmployeeName creator, " +
                "( CASE WHEN info.coupon_type = '1' THEN '折扣券' WHEN info.coupon_type = '2' THEN '代金券' WHEN info.coupon_type = '3' THEN '礼品券' ELSE '' END) couponType, " +
                "info.coupon_value couponValue, " +
                "info.stock_no stockNo, " +
                "info.stock_surplus stockSurplus, " +
                "info.coupon_collected couponCollected, " +
                "info.coupon_closure couponClosure, " +
                "( CASE WHEN info.`status` = 0 THEN '已禁用' WHEN info.endtime <= now( ) " +
                "AND info.coupon_status = 2 AND info.`status` = 1 THEN '已结束' " +
                "WHEN (info.begintime <= now( ) AND info.endtime > now( ) or info.is_vow_award = 1) " +
                "AND info.coupon_status = 2 AND info.`status` = 1 THEN '已开始' " +
                "WHEN info.coupon_status = 2 AND info.publish_time <= now( ) " +
                "AND info.`status` = 1 THEN '已发布' " +
                "WHEN info.coupon_status = 2 " +
                "AND info.publish_time > now( ) " +
                "AND info.`status` = 1 " +
                "THEN '未发布' " +
                "WHEN info.coupon_status = 1 THEN '草稿' ELSE '' END ) couponStatus, " +
                "ifnull(t.cstCount,0) collectionCstCount, " +
                "(select count(DISTINCT collection_openid) " +
                "from a_coupon_detail where coupon_id = info.id and closure_id is not null) closureCstCount, " +
                "ifnull(t.collectionCount,0) collectionCount, " +
                "ifnull(t.closureCount,0) closureCount " +
                "FROM a_coupon_info info " +
                "LEFT JOIN b_account ba ON ba.id = info.creator " +
                "LEFT JOIN a_activity_info aa ON aa.id = info.activity_id " +
                "LEFT JOIN (select coupon_id,count(1) collectionCount,count( DISTINCT collection_openid) cstCount, " +
                "sum(case when closure_id is not null then 1 else 0 end) closureCount " +
                "from a_coupon_detail " +
                "where isdel = 0 GROUP BY coupon_id) t on info.id = t.coupon_id " +
                "where info.isdel = 0");
        String projectIds = "'"+StringUtils.join(proIdList.toArray(), "','")+"'";
        sb.append(" (select DISTINCT coupon_id from a_coupon_project where project_id in ("+projectIds+"))");
        if (param.getCreator()!=null && !"".equals(param.getCreator())){
            sb.append(" and ba.EmployeeName like concat('%','"+param.getCreator()+"','%')");
        }
        if (param.getActivityName()!=null && !"".equals(param.getActivityName())){
            sb.append(" and aa.activity_name like concat('%','"+param.getActivityName()+"','%')");
        }
        if (param.getCouponName()!=null && !"".equals(param.getCouponName())){
            sb.append(" and info.coupon_name like concat('%','"+param.getCouponName()+"','%')");
        }
        if (param.getCouponNo()!=null && !"".equals(param.getCouponNo())){
            sb.append(" and info.coupon_no like concat('%','"+param.getCouponNo()+"','%')");
        }
        if (param.getActivityNo()!=null && !"".equals(param.getActivityNo())){
            sb.append(" and info.activity_no like concat('%','"+param.getActivityNo()+"','%')");
        }
        if (param.getCouponStatus()!=null && !"".equals(param.getCouponStatus())){
            if ("1".equals(param.getCouponStatus())){
                sb.append(" and info.coupon_status = '"+param.getCouponStatus()+"'");
            }else if ("2".equals(param.getCouponStatus())){
                sb.append(" and info.coupon_status = 2 and info.publish_time <= now() and info.begintime > now() and info.`status` = 1");
            }else if ("3".equals(param.getCouponStatus())){
                sb.append(" and (info.begintime <= now( ) AND info.endtime > now( ) or info.is_vow_award = 1) and info.coupon_status = 2 and info.`status` = 1");
            }else if ("0".equals(param.getCouponStatus())){
                sb.append(" and info.`status` = 0");
            }else if ("4".equals(param.getCouponStatus())){
                sb.append(" and info.endtime <= now() and info.coupon_status = 2 and info.`status` = 1");
            }else if ("5".equals(param.getCouponStatus())){
                sb.append(" and info.publish_time > now() and info.coupon_status = 2 and info.`status` = 1");
            }
        }
        if (param.getCouponTypes()!=null && !"".equals(param.getCouponTypes())){
            String typs = "'"+StringUtils.join(param.getCouponTypes().split(","), "','")+"'";
            sb.append(" and info.coupon_type in ("+typs+")");
        }
        if (param.getDate1()!=null && param.getDate2()!=null && !"".equals(param.getDate1()) && !"".equals(param.getDate2())){
            if (param.getReportTime()!=null){
                if ("1".equals(param.getReportTime())){
                    sb.append(" and info.publish_time BETWEEN '"+param.getDate1()+"' AND '"+param.getDate2()+"'");
                }else if("2".equals(param.getReportTime())){
                    sb.append(" and info.begintime BETWEEN '"+param.getDate1()+"' AND '"+param.getDate2()+"'");
                }else if("3".equals(param.getReportTime())){
                    sb.append(" and info.endtime BETWEEN '"+param.getDate1()+"' AND '"+param.getDate2()+"'");
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
    public String getCouponInfoStatus(String id) {
        return couponInfoDao.getCouponInfoStatus(id);
    }

    @Override
    public void updateCouNo() {
        List<String> ids = couponInfoDao.getActCount();
        String actNoPre = "COUP";
        for (int i = 0; i < ids.size(); i++) {
            int randomFour = (int) ((Math.random() * 9 + 1) * 1000);
            Date day1=new Date();
            SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = df1.format(day1);
            String couno = actNoPre + "" + format + randomFour;
            couponInfoDao.updateCouNo(couno,ids.get(i));
        }
    }

    @Override
    public void couponInfoExport(HttpServletRequest request, HttpServletResponse response, String params) {
        CouponInfoForm param = JSONObject.parseObject(params, CouponInfoForm.class);
        ArrayList<Object[]> dataset = new ArrayList<>();
        ExcelExportLog excelExportLog = new ExcelExportLog();
        Long nowtime = new Date().getTime();
        String id = UUID.randomUUID().toString();
        excelExportLog.setId(id);
        excelExportLog.setMainType("4");
        excelExportLog.setMainTypeDesc("活动管理");
        excelExportLog.setSubType("H4");
        excelExportLog.setSubTypeDesc("优惠券列表");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        try{
            List<String> couponTypeList = new ArrayList<>();
            if (param.getCouponTypes()!=null && !"".equals(param.getCouponTypes())){
                String[] ids =  param.getCouponTypes().split(",");
                for (String cid:ids) {
                    couponTypeList.add(cid);
                }
            }
            param.setCouponTypeList(couponTypeList);
            List<String> proIdList = new ArrayList<>();
            if (param.getProjectIds()!=null && !"".equals(param.getProjectIds())){
                String[] ids =   param.getProjectIds().split(",");
                for (String proid:ids) {
                    proIdList.add(proid);
                }
            }else {
                Map map=new HashMap();
                map.put("UserName", param.getUserName());
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
                List<Map> mapList = projectMapper.findProjectListByUserName( param.getUserName()+"","",userInfoMap.get("JobCode").toString(),sbs.toString());
                if (mapList!=null && mapList.size()>0){
                    for (Map proMap:mapList) {
                        proIdList.add(proMap.get("projectId")+"");
                    }
                }
            }
            param.setProjectList(proIdList);
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMaps = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setCreator(param.getUserId());
            excelExportLog.setAreaName(proMaps.get("areaName")+"");
            excelExportLog.setProjectId(proMaps.get("projectId")+"");
            excelExportLog.setProjectName(proMaps.get("projectName")+"");

            //导出的文档下面的名字
            String excelName = "优惠券明细表";
            List<CouponInfoVO> list = couponInfoDao.getAllCouponInfoVO(param);
            if (list!=null && list.size()>0){
                String[] headers = list.get(0).getCourtCaseTitle();
                for (CouponInfoVO c:list) {
                    List<Map> proMap = couponInfoDao.getProNameAndAreaName(c.getId());
                    if (proMap!=null && proMap.size()>0){
                        Set<String> aName = new HashSet<>();
                        StringBuffer sb = new StringBuffer();
                        StringBuffer sb1 = new StringBuffer();
                        for (int i = 0; i < proMap.size(); i++) {
                            aName.add(proMap.get(i).get("AreaName")+"");
                            //项目
                            if (i==proMap.size()-1){
                                sb1.append(proMap.get(i).get("ProjectName")+"");
                            }else{
                                sb1.append(proMap.get(i).get("ProjectName")+",");
                            }
                        }
                        //区域
                        List<String> names = new ArrayList<>(aName);
                        for (int i = 0; i < names.size(); i++) {
                            if (i==names.size()-1){
                                sb.append(names.get(i));
                            }else{
                                sb.append(names.get(i)+",");
                            }
                        }
                        c.setAreaNameNames(sb.toString());
                        c.setProjectNames(sb1.toString());
                    }
                }

                for (int i = 0; i < list.size(); i++) {
                    CouponInfoVO activityInfoVO = list.get(i);
                    activityInfoVO.setRownum((i+1)+"");
                    Object[] oArray = activityInfoVO.toActivityData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("优惠券明细表",headers,dataset,excelName,response,null);
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
    public PageInfo<CouponDetailVO> getCouponDetail(CouponDetailForm param) {
        // 分页数据设置
        PageHelper.startPage((int) param.getCurrent(), (int) param.getSize());
        if(param.getCouponType()!=null && !"".equals(param.getCouponType())){
            String[] couponList = param.getCouponType().split(",");
            List<String> couponTypeList = new ArrayList(Arrays.asList(couponList));
            param.setCouponTypeList(couponTypeList);
        }
        List<CouponDetailVO> list = couponInfoDao.getCouponDetailList(param);
        if (list!=null && list.size()>0){
            for (CouponDetailVO c:list) {
                List<Map> proMap = couponInfoDao.getProNameAndAreaName(c.getCouponId());
                if (proMap!=null && proMap.size()>0){
                    StringBuffer sb1 = new StringBuffer();
                    for (int i = 0; i < proMap.size(); i++) {
                        //项目
                        if (i==proMap.size()-1){
                            sb1.append(proMap.get(i).get("ProjectName")+"");
                        }else{
                            sb1.append(proMap.get(i).get("ProjectName")+",");
                        }
                    }
                    c.setCouponProjectnames(sb1.toString());
                }
            }
        }
        return new PageInfo<CouponDetailVO>(list);
    }

    @Override
    public void couponDetailExport(HttpServletRequest request, HttpServletResponse response, CouponDetailForm param) {
        String basePath = "templates";
        String templatePath = basePath + File.separator + "couponDetail.xlsx";
        ArrayList<Object[]> dataset = new ArrayList<>();
        try{
//            List<String> status = new ArrayList<>();
//            if (param.getStatuss()!=null && !"".equals(param.getStatuss())){
//                String[] ids =  param.getStatuss().split(",");
//                for (String id:ids) {
//                    status.add(id);
//                }
//            }
//            param.setStatus(status);
            List<String> giveType = new ArrayList<>();
            if (param.getGiveTypes()!=null && !"".equals(param.getGiveTypes())){
                String[] ids =  param.getGiveTypes().split(",");
                for (String id:ids) {
                    giveType.add(id);
                }
            }

            param.setGiveType(giveType);
            if(param.getCouponType()!=null && !"".equals(param.getCouponType())){
                String[] couponList = param.getCouponType().split(",");
                List<String> couponTypeList = new ArrayList(Arrays.asList(couponList));
                param.setCouponTypeList(couponTypeList);
            }
            List<String> proIdList = new ArrayList<>();
            if (param.getProjectIds()!=null && !"".equals(param.getProjectIds())){
                String[] ids =   param.getProjectIds().split(",");
                for (String id:ids) {
                    proIdList.add(id);
                }
            }else {
                Map map=new HashMap();
                map.put("UserName", param.getUserName());
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
                List<Map> mapList = projectMapper.findProjectListByUserName( param.getUserName()+"","",userInfoMap.get("JobCode").toString(),sbs.toString());
                if (mapList!=null && mapList.size()>0){
                    for (Map proMap:mapList) {
                        proIdList.add(proMap.get("projectId")+"");
                    }
                }
            }
            param.setProjectList(proIdList);
            if ("".equals(param.getIsAll())){
                param.setIsAll(null);
            }
            //导出的文档下面的名字
            String excelName = "优惠券领取明细表";
            List<CouponDetailVO> list = couponInfoDao.getCouponDetailList(param);
            if (list!=null && list.size()>0){
                String[] headers = list.get(0).getCourtCaseTitle();
                for (CouponDetailVO c:list) {
                    List<Map> proMap = couponInfoDao.getProNameAndAreaName(c.getCouponId());
                    if (proMap!=null && proMap.size()>0){
                        StringBuffer sb1 = new StringBuffer();
                        for (int i = 0; i < proMap.size(); i++) {
                            //项目
                            if (i==proMap.size()-1){
                                sb1.append(proMap.get(i).get("ProjectName")+"");
                            }else{
                                sb1.append(proMap.get(i).get("ProjectName")+",");
                            }
                        }
                        c.setCouponProjectnames(sb1.toString());
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    CouponDetailVO activityInfoVO = list.get(i);
                    activityInfoVO.setRownum((i+1)+"");
                    Object[] oArray = activityInfoVO.toActivityData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("优惠券领取明细表",headers,dataset,excelName,response,null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }




}
