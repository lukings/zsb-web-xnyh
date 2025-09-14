package cn.visolink.system.openQuotation.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.common.security.domain.MenuResult;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.householdregistration.dao.IntentionPlaceDao;
import cn.visolink.system.householdregistration.model.IntentionPlaceMaterial;
import cn.visolink.system.openQuotation.dao.OpenQuotationDao;
import cn.visolink.system.openQuotation.model.*;
import cn.visolink.system.openQuotation.service.OpenQuotationService;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.projectmanager.dao.HousingManagementDao;
import cn.visolink.utils.DbTest;
import cn.visolink.utils.HttpRequestUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName OpenQuotationServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/1/4 15:04
 **/
@Service
public class OpenQuotationServiceImpl implements OpenQuotationService {

    @Autowired
    private OpenQuotationDao openQuotationDao;

    @Autowired
    private IntentionPlaceDao intentionPlaceDao;

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private HousingManagementDao housingManagementDao;

    @Value("${FOREIGNURL}")
    private String foreignurl;

    @Value("${AppMenuUrl}")
    private String appMenuUrl;


    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat de = new DecimalFormat("0.00");
    @Override
    public ResultBody getOpenActivitys(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.valueOf(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.valueOf(map.get("pageSize")+"");
        }
        List<String> proIdList = new ArrayList<>();
        if (map.get("projectIdss") != null && !"".equals(map.get("projectIdss")+"")) {
            String[] ids = map.get("projectIdss").toString().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
        } else {
            Map mapf = new HashMap();
            mapf.put("UserName", map.get("userName"));
            Map userInfoMap = authMapper.mGetUserInfo(mapf);
            List<String> fullpath = projectMapper.findFullPath(mapf);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(map.get("userName") + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList != null && mapList.size() > 0) {
                for (Map proMap : mapList) {
                    proIdList.add(proMap.get("projectId") + "");
                }
            }
        }
        map.put("projectIds",proIdList);
        PageHelper.startPage(pageIndex,pageSize);
        List<OpenActivity> list = openQuotationDao.getOpenActivityList(map);
        return ResultBody.success(new PageInfo<OpenActivity>(list));

    }

    @Override
    public ResultBody releaseActivity(Map map) {
        map.put("release","1");
        map.put("editor",SecurityUtils.getUserId());
        openQuotationDao.updateOpenActivityStatus(map);
        return ResultBody.success("发布成功！");
    }

    @Override
    public ResultBody disableActivity(Map map) {
        map.put("status","0");
        map.put("editor",SecurityUtils.getUserId());
        map.put("disableor",SecurityUtils.getUserId());
        openQuotationDao.updateOpenActivityStatus(map);
        return ResultBody.success("禁用成功！");
    }

    @Override
    public ResultBody delActivity(Map map) {
        map.put("delStatus","1");
        map.put("editor",SecurityUtils.getUserId());
        openQuotationDao.updateOpenActivityStatus(map);
        return ResultBody.success("删除成功！");
    }

    @Override
    public ResultBody getOpenActivityBuild(Map map) {
        map.put("id",map.get("activityId"));
        return ResultBody.success(openQuotationDao.getActivityBuilds(map));
    }

    @Override
    public ResultBody getOpenActivityPhoto(Map map) {
        Map result = new HashMap();
        String materialAddress = intentionPlaceDao.selectActivityPhoto(map.get("activityId")+"");
        List<OpenBuildSite> openBuildSites = openQuotationDao.getOpenBuildSite(map.get("activityId")+"");
        result.put("materialAddress",materialAddress);
        result.put("buildSites",openBuildSites);
        return ResultBody.success(result);
    }

    @Override
    public String queryOpenActivityStatus(Map map) {
        return openQuotationDao.queryOpenActivityStatus(map);
    }



    @Override
    public ResultBody getProBook(Map map) {
        return ResultBody.success(openQuotationDao.getProBuild(map.get("projectId")+""));
    }

    @Override
    public ResultBody getProBank(Map map) {
        List<Map> result = new ArrayList<>();
        Map map1 = new HashMap();
        map1.put("bankCardId","6222020200030567712");
        map1.put("bankName","中国工商银行六道口支行1");
        result.add(map1);
        Map map2 = new HashMap();
        map2.put("bankCardId","6222020200030567711");
        map2.put("bankName","中国工商银行六道口支行");
        result.add(map2);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody getProDiscount(Map map) {
        Map endMap = new HashMap();
        List<Map> resultEnd = new ArrayList<>();
        int pageIndex = 1;
        int pageSize = 10;
        if(map==null || map.get("projectId")==null || "".equals(map.get("projectId")+"")){
            return ResultBody.error(-21_0006,"必传参数未传");
        }
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex"))){
            pageIndex = Integer.valueOf(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize"))){
            pageSize = Integer.valueOf(map.get("pageSize")+"");
        }
        endMap.put("pageIndex",pageIndex);
        endMap.put("pageSize",pageSize);
        String discountName = "";
        String nameCode = "";
        if (map.get("discountName")!=null && !"".equals(map.get("discountName")+"")){
            discountName = map.get("discountName")+"";
            nameCode = DigestUtils.md5DigestAsHex(discountName.getBytes());
        }
        String projectId = map.get("projectId")+"";
        String key = projectId+nameCode+"_ZKCX";
        int begin = (pageIndex-1)*pageSize;
        int end = pageIndex*pageSize;
        int total = 0;
        //判断redis里是否存在
        if (redisUtil.hasKey(key)){
            String datas = String.valueOf(redisUtil.get(key));
            List<Map> ts = JSONArray.parseArray(datas, Map.class);
            if (end>ts.size()){
                end = ts.size();
            }
            total = ts.size();
            resultEnd = ts.subList(begin,end);
        }else{
            String url = foreignurl+"getDiscount";
            Map<String,Object> requestMap = new HashMap<>();
            requestMap.put("projectId",projectId);
            if (!"".equals(discountName)){
                requestMap.put("discountName",discountName);
            }
            String postDataJson = JSONObject.toJSONString(requestMap, SerializerFeature.WriteMapNullValue);
            //获取折扣数据
            JSONObject result = HttpRequestUtil.httpPost(url,JSONObject.parseObject(postDataJson), false);
            if (result!=null && result.getJSONObject("result")!=null && "S".equals(result.getJSONObject("result").getString("code"))){
                JSONArray dataJsonArray = result.getJSONObject("result").getJSONArray("data");
                if (dataJsonArray!=null && dataJsonArray.size()>0){
                    redisUtil.set(key,JSONObject.toJSONString(dataJsonArray),1800);
                    List<Map> ts1 = dataJsonArray.toJavaList(Map.class);
                    if (end>ts1.size()){
                        end = ts1.size();
                    }
                    total = ts1.size();
                    resultEnd = ts1.subList(begin,end);
                }
            }
        }
        if (resultEnd.size()>0){
            for (Map e:resultEnd) {
                String expirationDate = "";
                String effectiveDate = "";
                if (e.get("expirationDate")!=null){
                    Long time = Long.valueOf(e.get("expirationDate")+"");
                    expirationDate = sf.format(new Date(time));
                }
                if (e.get("effectiveDate")!=null){
                    Long time = Long.valueOf(e.get("effectiveDate")+"");
                    effectiveDate = sf.format(new Date(time));
                }
                if (!"".equals(effectiveDate) && !"".equals(expirationDate)){
                    e.put("effectiveDate",effectiveDate+"~~"+expirationDate);
                }else{
                    e.put("effectiveDate",effectiveDate);
                }
                //判断是否存在指定房间，存在查询房间名称
                if (e.get("designatedRoom")!=null && !"".equals(e.get("designatedRoom")+"")){
                    String designatedRoomIds = e.get("designatedRoom")+"";
                    String[] ids = designatedRoomIds.split(",");
                    String idss = "'"+StringUtils.join(ids,"','")+"'";
                    String sql = "select STUFF((select ','+RoomInfo from VS_XK_S_ROOM nolock where RoomGUID in("+idss+") FOR xml path('')), 1, 1, '') roomName";
                    Map<String,Object> room = DbTest.getObject(sql);
                    if (room!=null){
                        e.put("designatedRoom",room.get("roomName"));
                    }
                }
            }
        }

        endMap.put("total",total);
        endMap.put("list",resultEnd);
        return ResultBody.success(endMap);
    }

    @Override
    public OpenBuild getBldingRoomList(String bldId, String activityId, String isResult) {
        if (activityId==null || "".equals(activityId)){
            return this.getBldRoom(bldId);
        }else{
            return this.getBldOldRoom(bldId,activityId,isResult);
        }

    }
    public OpenBuild getBldOldRoom(String bldId, String activityId, String isResult){
        // 获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String companycode = request.getHeader("companycode");

        //获取是否开启自销系统
        boolean flag = false;
        if (redisUtil.get("ISZXOPEN_" + companycode) != null) {
            if ("1".equals(redisUtil.get("ISZXOPEN_" + companycode).toString())) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            //获取公司聚客汇菜单
            List<String> companyMenuList = new ArrayList<>();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("companycode", companycode);
            String res = "";
            try {
                res = HttpRequestUtil.httpPost(appMenuUrl, JSONObject.parseObject(JSONObject.toJSONString(paramMap)), false).toJSONString();
                MenuResult menuResult = JSONObject.toJavaObject(JSON.parseObject(res), MenuResult.class);
                if (menuResult != null && menuResult.getData() != null) {
                    companyMenuList = menuResult.getData();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new OpenBuild();
            }
            if (companyMenuList != null && companyMenuList.size() > 0) {
                if (companyMenuList.contains("appmenu5-10")) {
                    //保存缓存是否开启自销为是
                    redisUtil.set("ISZXOPEN_" + companycode, "1", 3600);
                    flag = true;
                } else {
                    flag = false;
                    redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
                }
            } else {
                flag = false;
                //保存缓存是否开启自销为否
                redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
            }
        }

        OpenBuild openBuild = new OpenBuild();
        //获取楼栋信息
        Map map1 = new HashMap();
        map1.put("id",activityId);
        map1.put("bldId",bldId);
        List<OpenActivityBuild> openBuilds = openQuotationDao.getActivityBuilds(map1);
        if (openBuilds!=null && openBuilds.size()>0){
            OpenActivityBuild openActivityBuild = openBuilds.get(0);
            openBuild.setProjectid(openActivityBuild.getProjectId());
            openBuild.setProjectname(openActivityBuild.getProjectName());
            openBuild.setActivityId(activityId);
            openBuild.setBuildguid(openActivityBuild.getBuildId());
            openBuild.setBuildname(openActivityBuild.getBuildName());
            List<Map<String, Object>> result = null;
           // 判断是查询自销系统还是明源系统 true: 自销系统 false: 明源系统
            if (flag) {
                //根据楼栋获取单元信息
                String sql1 = "select DISTINCT BldGUID,UnitNo,UnitName,BldUnitGUID,PrjectGUID from VS_XK_S_BUILDING  where BldGUID in ('" + bldId + "') and UnitNo is not null order by UnitNo";

                result = housingManagementDao.getDataList(sql1);
            }
            else {
                //根据楼栋获取单元信息
                String sql1 = "select DISTINCT BldGUID,UnitNo,UnitName,BldUnitGUID,PrjectGUID from VS_XK_S_BUILDING  where BldGUID in ('" + bldId + "') and UnitNo is not null order by cast(UnitNo as int)";

                result = DbTest.getObjects(sql1);
            }

           List<Map<String, Object>> roomMaps = null;

            // 判断是查询自销系统还是明源系统 true: 自销系统 false: 明源系统
            if (flag) {
                //根据楼栋单元获取房间SQL语句
                String roomSql = "SELECT room.FloorNo,room.FloorName,room.Room,room.RoomInfo,room.RoomNo as No,room.RoomGUID,room.RoomStru,room.StatusEnum,room.UnitNo,room.BldGUID,room.BldName,room.HxName,room.HxGUID,room.BldArea,room.Price,room.TnArea,room.TnPrice,room.Total,room.PrjectGUID,room.PrjectFGUID,room.RoomType,Concat(room.KindeeProjectNAME,room.KindeeProjectFNAME) ProjectFNAME FROM VS_XK_S_ROOM room  WHERE room.BldGUID in ('" + bldId + "') order by room.UnitNo,room.FloorNo desc,room.RoomNo";
                roomMaps = housingManagementDao.getDataList(roomSql);
            }
            else {
                //根据楼栋单元获取房间SQL语句
                String roomSql = "SELECT CAST ( room.FloorNo AS INT ) FloorNo,room.FloorName,room.Room,room.RoomInfo,room.RoomNo as No,room.RoomGUID,room.RoomStru,room.StatusEnum,room.UnitNo,room.BldGUID,room.BldName,room.HxName,room.HxGUID,room.BldArea,room.Price,room.TnArea,room.TnPrice,room.Total,room.PrjectGUID,room.PrjectFGUID,room.RoomType,Concat(room.KindeeProjectNAME,room.KindeeProjectFNAME) ProjectFNAME FROM VS_XK_S_ROOM room  WHERE room.BldGUID in ('" + bldId + "') order by room.UnitNo,CAST ( room.FloorNo AS INT ) desc,room.RoomNo";
                roomMaps = DbTest.getObjects(roomSql);
            }
            List<OpenUnit> units = new ArrayList<>();

            for (Map<String, Object> map : result) {
                //获取楼层房间最大数
                int maxCount = 0;
                Map<String, Object> countMap = null;

                if (flag) {
                    String floorSql = "select FloorNo,max(cast(RoomNo as UNSIGNED)) count from VS_XK_S_ROOM where BldGUID = '" + openBuild.getBuildguid() + "' AND UnitNo = '" + map.get("UnitNo") + "' group by FloorNo order by count desc limit 1";
                    countMap = housingManagementDao.getDataList(floorSql).get(0);
                }
                else {
                    String floorSql = "select top 1 FloorNo,max(cast(RoomNo as int)) count from VS_XK_S_ROOM where BldGUID = '" + openBuild.getBuildguid() + "' AND UnitNo = '" + map.get("UnitNo") + "' group by FloorNo order by count desc";
                    countMap = DbTest.getObject(floorSql);
                }
                if (null != countMap) {
                    if (null != countMap.get("count") && !"".equals(countMap.get("count"))) {
                        maxCount = Integer.valueOf(countMap.get("count") + "") + 1;
                    }
                }
                OpenUnit bldUnit = new OpenUnit();
                bldUnit.setBuildguid(map.get("BldGUID") + "");
                bldUnit.setBldunitguid(map.get("BldUnitGUID") + "");
                bldUnit.setUnitname(map.get("UnitName") + "");
                bldUnit.setUnitno(map.get("UnitNo") + "");
                bldUnit.setMaxroomcount(maxCount);
                List<OpenRoom> roomList = new ArrayList<>();

                //获取销售经理销控房间数据
                List<String> roomIds = openQuotationDao.getControl(map.get("BldGUID") + "");
                List<String> openOrderIds = new ArrayList<>();//正在认购
                List<String> openOrderOkIds = new ArrayList<>();//已经完成认购
                List<String> roomNotIds = null;
                //判断是否选房结果查询
                if ("1".equals(isResult)){
                    //查询正在认购或已经完成认购的房间
                    openOrderOkIds = openQuotationDao.getOrderOkRoomIds(bldId);
                    openOrderIds = openQuotationDao.getOrderRoomIds(bldId);
                    roomNotIds = openQuotationDao.getNotShowRoomID(activityId,bldId);
                }
                for (Map<String, Object> map2 : roomMaps) {
                    if (openBuild.getBuildguid().equals(map2.get("BldGUID") + "") && bldUnit.getUnitno().equals(map2.get("UnitNo") + "")) {
                        OpenRoom intentionPlaceRoom = new OpenRoom();
                        intentionPlaceRoom.setBuildguid(map2.get("BldGUID") + "");
                        intentionPlaceRoom.setBuildname(map2.get("BldName") + "");
                        intentionPlaceRoom.setFloor(map2.get("FloorName") + "");
                        String floorNoS = map2.get("FloorNo") + "";
                        int floorNo = 0;
                        String noS = map2.get("No") + "";
                        int no = 0;
                        if (null != floorNoS && !"".equals(floorNoS)) {
                            if ("0".equals(floorNoS)){
                                floorNo = 1;
                            }else{
                                floorNo = Integer.parseInt(floorNoS);
                            }
                        }
                        if (null != noS && !"".equals(noS)) {
                            no = Integer.parseInt(noS)+1;
                        }
                        intentionPlaceRoom.setFloorno(floorNo);
                        intentionPlaceRoom.setFloorArea(map2.get("BldArea") + "");
                        intentionPlaceRoom.setFloorPrice(map2.get("Price") + "");
                        intentionPlaceRoom.setInsideArea(map2.get("TnArea") + "");
                        intentionPlaceRoom.setInsidePrice(map2.get("TnPrice") + "");
                        intentionPlaceRoom.setOrderby(no + "");
                        intentionPlaceRoom.setProjectidFq(map2.get("PrjectFGUID") + "");
                        intentionPlaceRoom.setProjectnameFq(map2.get("ProjectFNAME") + "");
                        intentionPlaceRoom.setRoomguid(map2.get("RoomGUID") + "");
                        intentionPlaceRoom.setRoominfo(map2.get("RoomInfo") + "");
                        intentionPlaceRoom.setRoomname(map2.get("Room") + "");
                        intentionPlaceRoom.setRoomno(no);
                        String roomType = "";
                        if (map2.get("RoomStru")!=null){
                            roomType = map2.get("RoomStru")+"";
                        }
                        intentionPlaceRoom.setRoomType(roomType);
                        intentionPlaceRoom.setUnitno(map2.get("UnitNo") + "");
                        intentionPlaceRoom.setHouseType(map2.get("HxName") + "");
                        intentionPlaceRoom.setHouseTypeId(map2.get("HxGUID") + "");
                        String salesStatus = "";
                        //(1:待售2:认购3:销控4:签约5：预留)-->(1:可售 2:已售 3:销控 4:已预收款 5:已集中选房 6:正在认购 7:开盘已售 8:非开盘已售 9:其它原因不可售)
                        if ("1".equals(map2.get("StatusEnum") + "")) {
                            if ("1".equals(map2.get("RoomType") + "")){
                                salesStatus = "4";
                            }else if ("2".equals(map2.get("RoomType") + "")){
                                salesStatus = "5";
                            }else if ("0".equals(map2.get("RoomType") + "")){
                                //判断是否被销售经理销控
                                if (roomIds!=null && roomIds.contains(intentionPlaceRoom.getRoomguid())){
                                    salesStatus = "3";
                                }else{
                                    salesStatus = "1";
                                }
                            }else{
                                salesStatus = "9";
                            }
                        } else if ("2".equals(map2.get("StatusEnum") + "") || "4".equals(map2.get("StatusEnum") + "")) {
                            salesStatus = "2";
                        } else if ("3".equals(map2.get("StatusEnum") + "") || "5".equals(map2.get("StatusEnum") + "")) {
                            salesStatus = "3";
                        }

                        if ("1".equals(isResult)){
                            if (openOrderIds.size()>0 && openOrderIds.contains(intentionPlaceRoom.getRoomguid())){
                                salesStatus = "6";
                            }
                            if ("2".equals(salesStatus)){
                                if (openOrderOkIds.size()>0 && openOrderOkIds.contains(intentionPlaceRoom.getRoomguid())){
                                    salesStatus = "7";
                                }else{
                                    salesStatus = "8";
                                }
                            }

                        }
                        //判断房间状态如果不展示则默认为选中
                        if (roomNotIds!=null && roomNotIds.size()>0 && roomNotIds.contains(intentionPlaceRoom.getRoomguid())){
                            intentionPlaceRoom.setIsChecked("1");
                        }
                        intentionPlaceRoom.setSaleStatus(salesStatus);
                        intentionPlaceRoom.setTotalPrice(map2.get("Total") + "");
                        roomList.add(intentionPlaceRoom);
                    }
                }
                bldUnit.setRoomList(roomList);
                units.add(bldUnit);
            }
            openBuild.setUnitList(units);
        }else{
            openBuild = this.getBldRoom(bldId);
        }
        return openBuild;
    }



    public OpenBuild getBldRoom(String bldId){
        // 获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String companycode = request.getHeader("companycode");

        //获取是否开启自销系统
        boolean flag = false;
        if (redisUtil.get("ISZXOPEN_" + companycode) != null) {
            if ("1".equals(redisUtil.get("ISZXOPEN_" + companycode).toString())) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            //获取公司聚客汇菜单
            List<String> companyMenuList = new ArrayList<>();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("companycode", companycode);
            String res = "";
            try {
                res = HttpRequestUtil.httpPost(appMenuUrl, JSONObject.parseObject(JSONObject.toJSONString(paramMap)), false).toJSONString();
                MenuResult menuResult = JSONObject.toJavaObject(JSON.parseObject(res), MenuResult.class);
                if (menuResult != null && menuResult.getData() != null) {
                    companyMenuList = menuResult.getData();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new OpenBuild();
            }
            if (companyMenuList != null && companyMenuList.size() > 0) {
                if (companyMenuList.contains("appmenu5-10")) {
                    //保存缓存是否开启自销为是
                    redisUtil.set("ISZXOPEN_" + companycode, "1", 3600);
                    flag = true;
                } else {
                    flag = false;
                    redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
                }
            } else {
                flag = false;
                //保存缓存是否开启自销为否
                redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
            }
        }


        OpenBuild openBuild = new OpenBuild();
        //新增时查询明源
        String buildIds = "'" + bldId + "'";
        //获取楼栋数据
        String sql = "select DISTINCT BldGUID,BldName,PrjectGUID,PrjectFGUID,Concat(KindeeProjectNAME,KindeeProjectFNAME) ProjectFNAME,BldType,OrderCode from VS_XK_S_BUILDING  where BldGUID in (" + buildIds + ") ORDER BY OrderCode";
        List<Map<String, Object>> bldMaps = null;
        // 判断是查询自销系统还是明源系统 true: 自销系统 false: 明源系统
        if (flag) {
            bldMaps = housingManagementDao.getDataList(sql);
        }
        else {
            bldMaps = DbTest.getObjects(sql);
        }

        if (bldMaps!=null && bldMaps.size()>0){
            Map<String, Object> bldMap = bldMaps.get(0);
            openBuild.setBuildguid(bldMap.get("BldGUID") + "");
            openBuild.setBuildname(bldMap.get("BldName") + "");
            openBuild.setOrderby(bldMap.get("OrderCode") + "");
           List<Map<String, Object>> result = null;
            // 判断是查询自销系统还是明源系统 true: 自销系统 false: 明源系统
            if (flag) {
                //根据楼栋获取单元信息
                String sql1 = "select DISTINCT BldGUID,UnitNo,UnitName,BldUnitGUID,PrjectGUID from VS_XK_S_BUILDING  where BldGUID in (" + buildIds + ") and UnitNo is not null order by UnitNo";

                result = housingManagementDao.getDataList(sql1);
            }
            else {
                //根据楼栋获取单元信息
                String sql1 = "select DISTINCT BldGUID,UnitNo,UnitName,BldUnitGUID,PrjectGUID from VS_XK_S_BUILDING  where BldGUID in (" + buildIds + ") and UnitNo is not null order by cast(UnitNo as int)";

                result = DbTest.getObjects(sql1);
            }

            List<Map<String, Object>> roomMaps = null;
            // 判断是查询自销系统还是明源系统 true: 自销系统 false: 明源系统
            if (flag) {
                //根据楼栋单元获取房间SQL语句
                String roomSql = "SELECT room.FloorNo,room.FloorName,room.Room,room.RoomInfo,room.RoomNo as No,room.RoomGUID,room.RoomStru,room.StatusEnum,room.UnitNo,room.BldGUID,room.BldName,room.HxName,room.HxGUID,room.BldArea,room.Price,room.TnArea,room.TnPrice,room.Total,room.PrjectGUID,room.PrjectFGUID,room.RoomType,Concat(room.KindeeProjectNAME,room.KindeeProjectFNAME) ProjectFNAME FROM VS_XK_S_ROOM room  WHERE room.BldGUID in (" + buildIds + ") order by room.UnitNo,room.FloorNo desc,room.RoomNo";
                roomMaps = housingManagementDao.getDataList(roomSql);
            }
            else {
                //根据楼栋单元获取房间SQL语句
                String roomSql = "SELECT CAST ( room.FloorNo AS INT ) FloorNo,room.FloorName,room.Room,room.RoomInfo,room.RoomNo as No,room.RoomGUID,room.RoomStru,room.StatusEnum,room.UnitNo,room.BldGUID,room.BldName,room.HxName,room.HxGUID,room.BldArea,room.Price,room.TnArea,room.TnPrice,room.Total,room.PrjectGUID,room.PrjectFGUID,room.RoomType,Concat(room.KindeeProjectNAME,room.KindeeProjectFNAME) ProjectFNAME FROM VS_XK_S_ROOM room  WHERE room.BldGUID in (" + buildIds + ") order by room.UnitNo,CAST ( room.FloorNo AS INT ) desc,room.RoomNo";
                roomMaps = DbTest.getObjects(roomSql);
            }

            List<OpenUnit> units = new ArrayList<>();
            for (Map<String, Object> map : result) {
                //获取楼层房间最大数
                int maxCount = 0;
                Map<String, Object> countMap = null;
                // 判断是查询自销系统还是明源系统 true: 自销系统 false: 明源系统
                if (flag) {
                    String floorSql = "select FloorNo,max(cast(RoomNo as UNSIGNED)) count from VS_XK_S_ROOM where BldGUID = '" + openBuild.getBuildguid() + "' AND UnitNo = '" + map.get("UnitNo") + "' group by FloorNo order by count desc limit 1";
                    countMap = housingManagementDao.getDataList(floorSql).get(0);
                }
                else {
                    String floorSql = "select top 1 FloorNo,max(cast(RoomNo as int)) count from VS_XK_S_ROOM where BldGUID = '" + openBuild.getBuildguid() + "' AND UnitNo = '" + map.get("UnitNo") + "' group by FloorNo order by count desc";
                    countMap = DbTest.getObject(floorSql);
                }


                if (null != countMap) {
                    if (null != countMap.get("count") && !"".equals(countMap.get("count"))) {
                        maxCount = Integer.valueOf(countMap.get("count") + "");
                    }
                }
                OpenUnit bldUnit = new OpenUnit();
                bldUnit.setBuildguid(map.get("BldGUID") + "");
                bldUnit.setBldunitguid(map.get("BldUnitGUID") + "");
                bldUnit.setUnitname(map.get("UnitName") + "");
                bldUnit.setUnitno(map.get("UnitNo") + "");
                bldUnit.setMaxroomcount(maxCount);
                List<OpenRoom> roomList = new ArrayList<>();

                //获取销售经理销控房间数据
                List<String> roomIds = openQuotationDao.getControl(map.get("BldGUID") + "");

                for (Map<String, Object> map2 : roomMaps) {
                    if (openBuild.getBuildguid().equals(map2.get("BldGUID") + "") && bldUnit.getUnitno().equals(map2.get("UnitNo") + "")) {
                        OpenRoom intentionPlaceRoom = new OpenRoom();
                        intentionPlaceRoom.setBuildguid(map2.get("BldGUID") + "");
                        intentionPlaceRoom.setBuildname(map2.get("BldName") + "");
                        intentionPlaceRoom.setFloor(map2.get("FloorName") + "");
                        String floorNoS = map2.get("FloorNo") + "";
                        int floorNo = 0;
                        String noS = map2.get("No") + "";
                        int no = 0;
                        if (null != floorNoS && !"".equals(floorNoS)) {
                            if ("0".equals(floorNoS)){
                                floorNo = 1;
                            }else{
                                floorNo = Integer.parseInt(floorNoS);
                            }
                        }
                        if (null != noS && !"".equals(noS)) {
                            no = Integer.parseInt(noS);
                        }
                        intentionPlaceRoom.setFloorno(floorNo);
                        intentionPlaceRoom.setFloorArea(map2.get("BldArea") + "");
                        intentionPlaceRoom.setFloorPrice(map2.get("Price") + "");
                        intentionPlaceRoom.setInsideArea(map2.get("TnArea") + "");
                        intentionPlaceRoom.setInsidePrice(map2.get("TnPrice") + "");
                        intentionPlaceRoom.setOrderby(no + "");
                        intentionPlaceRoom.setProjectidFq(map2.get("PrjectFGUID") + "");
                        intentionPlaceRoom.setProjectnameFq(map2.get("ProjectFNAME") + "");
                        intentionPlaceRoom.setRoomguid(map2.get("RoomGUID") + "");
                        intentionPlaceRoom.setRoominfo(map2.get("RoomInfo") + "");
                        intentionPlaceRoom.setRoomname(map2.get("Room") + "");
                        intentionPlaceRoom.setRoomno(no);
                        String roomType = "";
                        if (map2.get("RoomStru")!=null){
                            roomType = map2.get("RoomStru")+"";
                        }
                        intentionPlaceRoom.setRoomType(roomType);
                        intentionPlaceRoom.setUnitno(map2.get("UnitNo") + "");
                        intentionPlaceRoom.setHouseType(map2.get("HxName") + "");
                        intentionPlaceRoom.setHouseTypeId(map2.get("HxGUID") + "");
                        String salesStatus = "";
                        //(1:待售2:认购3:销控4:签约5：预留)-->(1:可售 2:已售 3:销控 4:已预收款 5:已集中选房 6:正在认购 7:其它原因不可售)
                        if ("1".equals(map2.get("StatusEnum") + "")) {
                            if ("1".equals(map2.get("RoomType") + "")){
                                salesStatus = "4";
                            }else if ("2".equals(map2.get("RoomType") + "")){
                                salesStatus = "5";
                            }else if ("0".equals(map2.get("RoomType") + "")){
                                //判断是否被销售经理销控
                                if (roomIds!=null && roomIds.contains(intentionPlaceRoom.getRoomguid())){
                                    salesStatus = "3";
                                }else{
                                    salesStatus = "1";
                                }
                            }else{
                                salesStatus = "7";
                            }

                        } else if ("2".equals(map2.get("StatusEnum") + "") || "4".equals(map2.get("StatusEnum") + "")) {
                            salesStatus = "2";
                        } else if ("3".equals(map2.get("StatusEnum") + "") || "5".equals(map2.get("StatusEnum") + "")) {
                            salesStatus = "3";
                        }
                        intentionPlaceRoom.setSaleStatus(salesStatus);
                        intentionPlaceRoom.setTotalPrice(map2.get("Total") + "");
                        roomList.add(intentionPlaceRoom);
                    }
                }
                bldUnit.setRoomList(roomList);
                units.add(bldUnit);
            }
            openBuild.setUnitList(units);
        }
        return openBuild;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addOpenActivity(OpenActivity param) {

        try{
            String creator = SecurityUtils.getUserId();
            //生成活动ID
            String activityId = UUID.randomUUID().toString();
            //生成活动编号
            String sxChooseRoomId = this.getAcNo();
            param.setSxChooseRoomId(sxChooseRoomId);
            param.setId(activityId);
            param.setCreator(creator);
            StringBuffer sb = new StringBuffer();
            String bookIds = "";
            //保存活动关联楼盘
            List<OpenBuildBook> books = param.getBuildBooks();
            for (OpenBuildBook book:books) {
                book.setCreator(creator);
                book.setActivityId(activityId);
                book.setProjectId(param.getProjectId());
                sb.append("'"+book.getBookId()+"',");
            }
            if (books!=null && books.size()>0){
                bookIds = sb.toString().substring(0,sb.toString().length()-1);
                if (!"1".equals(param.getActStatus())){
                    //判断时间是否可以使用
                    Map map = new HashMap();
                    map.put("bookIds",bookIds);
                    map.put("sxReleaseTime",param.getSxReleaseTime());
                    map.put("sxActivityEndtime",param.getSxActivityEndtime());
                    int count = openQuotationDao.getIsTimeOk(map);
                    if (count>0){
                        return ResultBody.error(-233333,"配置的时间已存在其他活动！");
                    }
                }
                openQuotationDao.addOpenBooks(books);
            }
            openQuotationDao.addOpenActivity(param);
            //保存活动楼栋
            List<OpenActivityBuild> builds = param.getBuildings();
            if (builds!=null && builds.size()>0){
                for (OpenActivityBuild build:builds) {
                    build.setActivityId(activityId);
                    build.setCreator(creator);
                    build.setProjectId(param.getProjectId());
                    build.setProjectName(param.getProjectName());
                }
                openQuotationDao.addOpenBuilds(builds);
            }
            //判断选房模式
            if ("2".equals(param.getChoosePattern())){
                List<OpenActivityRoom> rooms = param.getRoomList();
                if (rooms!=null && rooms.size()>0){
                    for (OpenActivityRoom room:rooms) {
                        room.setActivityId(activityId);
                        room.setProjectId(param.getProjectId());
                        room.setCreator(creator);
                    }
                    openQuotationDao.addOpenActivityRoom(rooms);
                }
            }else{
                //保存活动不可设置房间
                List<OpenNotRoom> rooms = param.getRooms();
                if (rooms!=null && rooms.size()>0){
                    for (OpenNotRoom room:rooms) {
                        room.setActivityId(activityId);
                        room.setProjectId(param.getProjectId());
                        room.setProjectName(param.getProjectName());
                        room.setCreator(creator);
                    }
                    openQuotationDao.addOpenNotRoom(rooms);
                }
            }

            //保存活动素材
            String materialid = "";//概览图素材ID
            List<IntentionPlaceMaterial> materials = param.getMaterialList();
            if (materials!=null && materials.size()>0){
                for (IntentionPlaceMaterial inten:materials) {
                    String mId = UUID.randomUUID().toString();
                    inten.setId(mId);
                    inten.setActivityId(activityId);
                    inten.setCreator(creator);
                    if ("2".equals(inten.getMaterialType())){
                        materialid = mId;
                    }
                }
                intentionPlaceDao.addIntentionPlaceMaterial(materials);
            }

            //保存活动概览图坐标
            List<OpenBuildSite> buildSites = param.getBuildSites();
            if (buildSites!=null && buildSites.size()>0){
                for (OpenBuildSite buildSite:buildSites) {
                    buildSite.setActivityId(activityId);
                    buildSite.setMaterialid(materialid);
                    buildSite.setProjectid(param.getProjectId());
                    buildSite.setProjectname(param.getProjectName());
                }
                openQuotationDao.addOpenBuildSite(buildSites);
            }
            //保存折扣信息
            List<OpenDiscount> openDiscounts = param.getActivityDiscount();
            if (openDiscounts!=null && openDiscounts.size()>0){
                for (OpenDiscount dis:openDiscounts) {
                    dis.setActivityId(activityId);
                    dis.setCreator(creator);
                    dis.setProjectId(param.getProjectId());
                    dis.setProjectName(param.getProjectName());
                }
                openQuotationDao.addOpenDiscount(openDiscounts);
            }

            //保存商户信息
//            List<OpenActivityBank> openActivityBanks = param.getActivityBanks();
//            if (openActivityBanks!=null && openActivityBanks.size()>0){
//                for (OpenActivityBank open:openActivityBanks) {
//                    open.setOpenActivityId(activityId);
//                    open.setCreator(creator);
//                }
//                openQuotationDao.addActivityBank(openActivityBanks);
//            }

        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0006,"添加开盘活动异常！");
        }
        return ResultBody.success("添加开盘活动成功！");
    }

    private String getAcNo(){
         SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        //获取当前时间
        String date = sd.format(new Date());
        //获取4位随机数
        int ran = (int)((Math.random()*9+1)*1000);
        return "BOL"+date+ran;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateOpenActivity(OpenActivity param) {
        try{
            String creator = SecurityUtils.getUserId();
            String activityId = param.getId();
            param.setCreator(creator);
            StringBuffer sb = new StringBuffer();
            String bookIds = "";
            //保存活动关联楼盘
            List<OpenBuildBook> books = param.getBuildBooks();
            for (OpenBuildBook book:books) {
                book.setCreator(creator);
                book.setActivityId(activityId);
                book.setProjectId(param.getProjectId());
                sb.append("'"+book.getBookId()+"',");
            }
            if (books!=null && books.size()>0){
                bookIds = sb.toString().substring(0,sb.toString().length()-1);
                if (!"1".equals(param.getActStatus())){
                    //判断时间是否可以使用
                    Map map = new HashMap();
                    map.put("bookIds",bookIds);
                    map.put("activityId",activityId);
                    map.put("sxReleaseTime",param.getSxReleaseTime());
                    map.put("sxActivityEndtime",param.getSxActivityEndtime());
                    int count = openQuotationDao.getIsTimeOk(map);
                    if (count>0){
                        return ResultBody.error(-233333,"配置的时间已存在其他活动！");
                    }
                }
                //先删除关联楼盘
                openQuotationDao.delOpenBooks(activityId);
                openQuotationDao.addOpenBooks(books);
            }

            //更新基本信息
            openQuotationDao.updateOpenActivity(param);
            //判断选房模式
            if ("2".equals(param.getChoosePattern())){
                openQuotationDao.delOpenActivityRoom(activityId);
                List<OpenActivityRoom> rooms = param.getRoomList();
                if (rooms!=null && rooms.size()>0){
                    for (OpenActivityRoom room:rooms) {
                        room.setActivityId(activityId);
                        room.setProjectId(param.getProjectId());
                        room.setCreator(creator);
                    }
                    openQuotationDao.addOpenActivityRoom(rooms);
                }
                //删除原来选择的楼栋
                Map map = new HashMap();
                map.put("id",activityId);
                openQuotationDao.delOpenBuilds(map);
                //保存活动楼栋
                List<OpenActivityBuild> builds = param.getBuildings();
                if (builds!=null && builds.size()>0){
                    for (OpenActivityBuild build:builds) {
                        build.setActivityId(activityId);
                        build.setCreator(creator);
                        build.setProjectId(param.getProjectId());
                        build.setProjectName(param.getProjectName());
                    }
                    openQuotationDao.addOpenBuilds(builds);
                }
                //删除原来不可设置房间
                openQuotationDao.delOpenNotRoom(map);
            }else{
                //删除原来选择的楼栋
                Map map = new HashMap();
                map.put("id",activityId);
                openQuotationDao.delOpenBuilds(map);
                //保存活动楼栋
                List<OpenActivityBuild> builds = param.getBuildings();
                if (builds!=null && builds.size()>0){
                    for (OpenActivityBuild build:builds) {
                        build.setActivityId(activityId);
                        build.setCreator(creator);
                        build.setProjectId(param.getProjectId());
                        build.setProjectName(param.getProjectName());
                    }
                    openQuotationDao.addOpenBuilds(builds);
                }
                //删除原来不可设置房间
                openQuotationDao.delOpenNotRoom(map);
                //保存活动不可设置房间
                List<OpenNotRoom> rooms = param.getRooms();
                if (rooms!=null && rooms.size()>0){
                    for (OpenNotRoom room:rooms) {
                        room.setActivityId(activityId);
                        room.setProjectId(param.getProjectId());
                        room.setProjectName(param.getProjectName());
                        room.setCreator(creator);
                    }
                    openQuotationDao.addOpenNotRoom(rooms);
                }
            }
            //更新活动素材
            String materialid = "";//概览图素材ID
            List<IntentionPlaceMaterial> materials = param.getMaterialList();
            if (materials!=null && materials.size()>0){
                for (IntentionPlaceMaterial inten:materials) {
                    inten.setEditor(creator);
                    if ("2".equals(inten.getMaterialType())){
                        materialid = inten.getId();
                    }
                }
                intentionPlaceDao.updateIntentionPlaceMaterial(materials);
            }
            //删除活动概览图坐标
            openQuotationDao.delOpenBuildSite(activityId);
            //保存活动概览图坐标
            List<OpenBuildSite> buildSites = param.getBuildSites();
            if (buildSites!=null && buildSites.size()>0){
                for (OpenBuildSite buildSite:buildSites) {
                    buildSite.setActivityId(activityId);
                    buildSite.setMaterialid(materialid);
                    buildSite.setProjectid(param.getProjectId());
                    buildSite.setProjectname(param.getProjectName());
                }
                openQuotationDao.addOpenBuildSite(buildSites);
            }
            openQuotationDao.delOpenDiscount(activityId);
            //保存折扣信息
            List<OpenDiscount> openDiscounts = param.getActivityDiscount();
            if (openDiscounts!=null && openDiscounts.size()>0){
                for (OpenDiscount dis:openDiscounts) {
                    dis.setActivityId(activityId);
                    dis.setCreator(creator);
                    dis.setProjectId(param.getProjectId());
                    dis.setProjectName(param.getProjectName());
                }
                openQuotationDao.addOpenDiscount(openDiscounts);
            }
            openQuotationDao.delActivityBank(activityId);
            //保存商户信息
//            List<OpenActivityBank> openActivityBanks = param.getActivityBanks();
//            if (openActivityBanks!=null && openActivityBanks.size()>0){
//                for (OpenActivityBank open:openActivityBanks) {
//                    open.setOpenActivityId(activityId);
//                    open.setCreator(creator);
//                }
//                openQuotationDao.addActivityBank(openActivityBanks);
//            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0006,"更新开盘活动异常！");
        }
        return ResultBody.success("更新开盘活动成功！");
    }

    @Override
    public void openActivityExport(HttpServletRequest request, HttpServletResponse response, Map map) {
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("8");
        excelExportLog.setMainTypeDesc("在线开盘管理");
        excelExportLog.setSubType("O1");
        excelExportLog.setSubTypeDesc("开盘活动列表");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        try{
            List<String> proIdList = new ArrayList<>();
            if (map.get("projectIdss") != null && !"".equals(map.get("projectIdss")+"")) {
                String[] ids = map.get("projectIdss").toString().split(",");
                for (String proid : ids) {
                    proIdList.add(proid);
                }
            } else {
                Map mapf = new HashMap();
                mapf.put("UserName", map.get("userName"));
                Map userInfoMap = authMapper.mGetUserInfo(mapf);
                List<String> fullpath = projectMapper.findFullPath(mapf);
                StringBuffer sbs = new StringBuffer();
                for (int i = 0; i < fullpath.size(); i++) {
                    if (i == 0) {
                        sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                    } else {
                        sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                    }
                }
                List<Map> mapList = projectMapper.findProjectListByUserName(map.get("userName") + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
                if (mapList != null && mapList.size() > 0) {
                    for (Map proMap : mapList) {
                        proIdList.add(proMap.get("projectId") + "");
                    }
                }
            }
            map.put("projectIds",proIdList);
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setCreator(map.get("userId")+"");
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");

            List<OpenActivity> list = openQuotationDao.getOpenActivityExport(map);
            if (list!=null && list.size()>0){
                String[] headers = list.get(0).getActivityTitle();
                ArrayList<Object[]> dataset = new ArrayList<>();
                int num = 0;
                for (OpenActivity ac:list) {
                    num++;
                    ac.setRownum(num+"");
                    Object[] oArray = ac.toActivityData();
                    dataset.add(oArray);
                }

                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("开盘活动列表",headers,dataset,"开盘活动列表",response,null);

            }
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(sf.format(new Date()));
            excelExportLog.setIsDown("1");
            Long export = new Date().getTime();
            Long exporttime = export-nowtime;
            String exportTime =sf.format(Double.valueOf(exporttime+"")/1000);
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
    @Transactional(rollbackFor = Exception.class)
    public String openActivityExportNew(Map map) {
        String companycode = "";
        if (map.get("companycode")!=null && !"".equals(map.get("companycode")+"")){
            companycode = map.get("companycode")+"";
        }
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        List<String> proIdList = new ArrayList<>();
        if (map.get("projectIdss") != null && !"".equals(map.get("projectIdss")+"")) {
            String[] ids = map.get("projectIdss").toString().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
        } else {
            Map mapf = new HashMap();
            mapf.put("UserName", map.get("userName"));
            Map userInfoMap = authMapper.mGetUserInfo(mapf);
            List<String> fullpath = projectMapper.findFullPath(mapf);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(map.get("userName") + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList != null && mapList.size() > 0) {
                for (Map proMap : mapList) {
                    proIdList.add(proMap.get("projectId") + "");
                }
            }
        }
        //获取项目集合数据（事业部，项目Id,项目名称）
        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
        excelExportLog.setCreator(map.get("userId")+"");
        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");
        excelExportLog.setId(id);
        excelExportLog.setMainType("8");
        excelExportLog.setMainTypeDesc("在线开盘管理");
        excelExportLog.setSubType("O1");
        excelExportLog.setSubTypeDesc("开盘活动列表");
        excelExportLog.setExportType("3");
        excelExportLog.setExportStatus("1");
        excelExportLog.setIsAsyn("1");
        //sql拼接
        StringBuffer sb = new StringBuffer();
        sb.append("select bp.AreaName areaName,op.project_name projectName,op.id, op.sx_choose_room_id sxChooseRoomId, op.sx_activity_name sxActivityName, DATE_FORMAT(op.sx_activity_begintime,'%Y-%m-%d %H:%i:%s') sxActivityBegintime, DATE_FORMAT(op.sx_activity_endtime,'%Y-%m-%d %H:%i:%s') sxActivityEndtime, DATE_FORMAT(op.sx_release_time,'%Y-%m-%d %H:%i:%s') sxReleaseTime, op.bank_card_id bankCardId, ba.EmployeeName creator, DATE_FORMAT(op.create_time,'%Y-%m-%d %H:%i:%s') createTime, ba1.EmployeeName disableor, DATE_FORMAT(op.disabletime,'%Y-%m-%d %H:%i:%s') disabletime,(case when op.act_status = 1 then '草稿' when op.`status`= 0 then '已禁用' when op.act_status = 2 and op.`status` = 1 and now()< op.sx_release_time then '未发布' when op.act_status = 2 and op.`status` = 1 and now()>= op.sx_release_time and now()< op.sx_activity_begintime then '已发布' when op.act_status = 2 and op.`status` = 1 and now()>= op.sx_activity_begintime and now()<op.sx_activity_endtime then '已开始' when op.act_status = 2 and op.`status` = 1 and now()>= op.sx_activity_endtime then '已结束' end) actStatus, (case when op.is_online_pay = 1 then '是' else '否' end) isOnlinePay from x_open_activity op INNER JOIN b_project bp on bp.ID = op.project_id INNER JOIN b_account ba on ba.ID = op.creator left join b_account ba1 on ba1.ID = op.disableor where op.del_status = 0");
        String projectIds = "'"+ StringUtils.join(proIdList.toArray(), "','")+"'";
        sb.append(" and op.project_id in ("+projectIds+")");
        if (map.get("search")!=null && !"".equals(map.get("search")+"")){
            sb.append(" and (op.sx_activity_name like concat('%','"+map.get("search")+"','%') or op.sx_choose_room_id like concat('%','"+map.get("search")+"','%') )");
        }
        if (map.get("isOnlinePay")!=null && !"".equals(map.get("isOnlinePay")+"")){
            sb.append(" and op.is_online_pay = '"+map.get("isOnlinePay")+"'");
        }
        if (map.get("actStatus")!=null && !"".equals(map.get("actStatus")+"")){
            if ("1".equals(map.get("actStatus")+"")){
                sb.append(" and op.act_status = '"+map.get("actStatus")+"'");
            }else if ("2".equals(map.get("actStatus")+"")){
                sb.append(" and op.act_status = 2 and op.sx_release_time <= now() and op.sx_activity_begintime > now() and op.`status` = 1");
            }else if ("3".equals(map.get("actStatus")+"")){
                sb.append(" and op.sx_activity_begintime <= now() and op.sx_activity_endtime > now() and op.act_status = 2 and op.`status` = 1");
            }else if ("0".equals(map.get("actStatus")+"")){
                sb.append(" and op.`status` = 0");
            }else if ("4".equals(map.get("actStatus")+"")){
                sb.append(" and op.sx_activity_endtime <= now() and op.act_status = 2 and op.`status` = 1");
            }else if ("5".equals(map.get("actStatus")+"")){
                sb.append(" and op.sx_release_time > now() and op.act_status = 2 and op.`status` = 1");
            }
        }
        sb.append(" order by op.create_time desc");
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
        return "任务创建成功,请关注右上角下载任务状态";
    }

    @Override
    public ResultBody getOpenActivityResult(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.valueOf(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.valueOf(map.get("pageSize")+"");
        }
        List<String> proIdList = new ArrayList<>();
        if (map.get("projectIdss") != null && !"".equals(map.get("projectIdss")+"")) {
            String[] ids = map.get("projectIdss").toString().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
        } else {
            Map mapf = new HashMap();
            mapf.put("UserName", map.get("userName"));
            Map userInfoMap = authMapper.mGetUserInfo(mapf);
            List<String> fullpath = projectMapper.findFullPath(mapf);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(map.get("userName") + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList != null && mapList.size() > 0) {
                for (Map proMap : mapList) {
                    proIdList.add(proMap.get("projectId") + "");
                }
            }
        }
        map.put("projectIds",proIdList);
        PageHelper.startPage(pageIndex,pageSize);
        List<OpenActivityResult> list = openQuotationDao.getOpenActivityResult(map);
        return ResultBody.success(new PageInfo<OpenActivityResult>(list));
    }

    @Override
    public ResultBody getOpenOrderList(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.valueOf(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.valueOf(map.get("pageSize")+"");
        }
        List<String> proIdList = new ArrayList<>();
        if (map.get("projectIdss") != null && !"".equals(map.get("projectIdss")+"")) {
            String[] ids = map.get("projectIdss").toString().split(",");
            for (String proid : ids) {
                proIdList.add(proid);
            }
        } else {
            Map mapf = new HashMap();
            mapf.put("UserName", map.get("userName"));
            Map userInfoMap = authMapper.mGetUserInfo(mapf);
            List<String> fullpath = projectMapper.findFullPath(mapf);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i == 0) {
                    sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                } else {
                    sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                }
            }
            List<Map> mapList = projectMapper.findProjectListByUserName(map.get("userName") + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
            if (mapList != null && mapList.size() > 0) {
                for (Map proMap : mapList) {
                    proIdList.add(proMap.get("projectId") + "");
                }
            }
        }
        map.put("projectIds",proIdList);
        PageHelper.startPage(pageIndex,pageSize);
        List<OrderList> list = openQuotationDao.getOrderList(map);
        return ResultBody.success(new PageInfo<OrderList>(list));
    }

    @Override
    public void openOrderListExport(HttpServletRequest request, HttpServletResponse response, Map map) {
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("8");
        excelExportLog.setMainTypeDesc("在线开盘管理");
        excelExportLog.setSubType("O2");
        excelExportLog.setSubTypeDesc("开盘订单明细");
        String isAll = "";
        if (map.get("isAll") != null && !"".equals(map.get("isAll")+"") && "1".equals(map.get("isAll")+"")){
            excelExportLog.setExportType("2");
            isAll = "1";
        }else{
            excelExportLog.setExportType("1");
        }
        excelExportLog.setIsAsyn("0");
        try{
            List<String> proIdList = new ArrayList<>();
            if (map.get("projectIdss") != null && !"".equals(map.get("projectIdss")+"")) {
                String[] ids = map.get("projectIdss").toString().split(",");
                for (String proid : ids) {
                    proIdList.add(proid);
                }
            } else {
                Map mapf = new HashMap();
                mapf.put("UserName", map.get("userName"));
                Map userInfoMap = authMapper.mGetUserInfo(mapf);
                List<String> fullpath = projectMapper.findFullPath(mapf);
                StringBuffer sbs = new StringBuffer();
                for (int i = 0; i < fullpath.size(); i++) {
                    if (i == 0) {
                        sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                    } else {
                        sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                    }
                }
                List<Map> mapList = projectMapper.findProjectListByUserName(map.get("userName") + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
                if (mapList != null && mapList.size() > 0) {
                    for (Map proMap : mapList) {
                        proIdList.add(proMap.get("projectId") + "");
                    }
                }
            }
            map.put("projectIds",proIdList);
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setCreator(map.get("userId")+"");
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");

            List<OrderList> list = openQuotationDao.getOrderList(map);
            if (list!=null && list.size()>0){
                String[] headers = list.get(0).getOrderTitle();
                ArrayList<Object[]> dataset = new ArrayList<>();
                int num = 0;
                for (OrderList ac:list) {
                    num++;
                    ac.setRownum(num+"");
                    Object[] oArray = ac.toOrderData(isAll);
                    dataset.add(oArray);
                }

                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("开盘订单明细",headers,dataset,"开盘订单明细",response,null);

            }
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(sf.format(new Date()));
            excelExportLog.setIsDown("1");
            Long export = new Date().getTime();
            Long exporttime = export-nowtime;
            String exportTime =sf.format(Double.valueOf(exporttime+"")/1000);
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
    public String queryBuildAccount(Map map) {
        String buildId = map.get("buildId")+"";
        String url = foreignurl+"getBuildDeposit";
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("buildGuid",buildId);
        String postDataJson = JSONObject.toJSONString(requestMap, SerializerFeature.WriteMapNullValue);
        //获取明源排卡数据
        JSONObject result = HttpRequestUtil.httpPost(url,JSONObject.parseObject(postDataJson), false);
        if (result!=null && result.getJSONObject("result")!=null && "S".equals(result.getJSONObject("result").getString("code"))){
            JSONObject dataJson = result.getJSONObject("result").getJSONObject("data");
            String orderDeposit = dataJson.getString("orderDeposit");
            if (orderDeposit!=null && !"".equals(orderDeposit)){
                Double dd = Double.valueOf(orderDeposit);
                if (dd<=0){
                    return "0";
                }
            }else{
                return "0";
            }
        }else {
            return "0";
        }
        return "1";
    }

    @Override
    public int queryActivityIsOkPublish(Map map) {
        String activityId = map.get("activityId")+"";
        //根据活动查询结束时间
        OpenActivity openActivity = openQuotationDao.getOpenActivity(activityId);
        //查询关联楼盘
        List<OpenBuildBook> openBuildBooks = openQuotationDao.getOpenBooks(activityId);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < openBuildBooks.size(); i++) {
            OpenBuildBook book = openBuildBooks.get(i);
            if (i==openBuildBooks.size()-1){
                sb.append("'"+book.getBookId()+"'");
            }else{
                sb.append("'"+book.getBookId()+"',");
            }
        }
        //判断是否可发布
        Map timeMap = new HashMap();
        timeMap.put("bookIds",sb.toString());
        timeMap.put("activityId",activityId);
        timeMap.put("sxReleaseTime",sf.format(new Date()));
        timeMap.put("sxActivityEndtime",openActivity.getSxActivityEndtime());
        int isOk = openQuotationDao.getIsTimeOk(timeMap);
        return isOk;
    }

    @Override
    public ResultBody getBldingRoomsList(Map map) {

        // 获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String companycode = request.getHeader("companycode");

        //获取是否开启自销系统
        boolean flag = false;
        if (redisUtil.get("ISZXOPEN_" + companycode) != null) {
            if ("1".equals(redisUtil.get("ISZXOPEN_" + companycode).toString())) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            //获取公司聚客汇菜单
            List<String> companyMenuList = new ArrayList<>();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("companycode", companycode);
            String res = "";
            try {
                res = HttpRequestUtil.httpPost(appMenuUrl, JSONObject.parseObject(JSONObject.toJSONString(paramMap)), false).toJSONString();
                MenuResult menuResult = JSONObject.toJavaObject(JSON.parseObject(res), MenuResult.class);
                if (menuResult != null && menuResult.getData() != null) {
                    companyMenuList = menuResult.getData();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResultBody.error(-20006, "获取是否自销系统失败！查询有误");
            }
            if (companyMenuList != null && companyMenuList.size() > 0) {
                if (companyMenuList.contains("appmenu5-10")) {
                    //保存缓存是否开启自销为是
                    redisUtil.set("ISZXOPEN_" + companycode, "1", 3600);
                    flag = true;
                } else {
                    flag = false;
                    redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
                }
            } else {
                flag = false;
                //保存缓存是否开启自销为否
                redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
            }
        }

        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.valueOf(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.valueOf(map.get("pageSize")+"");
        }
        String blds = map.get("bldIds")+"";
        String[] ids = blds.split(",");
        String buildIds = "'"+StringUtils.join(ids,"','")+"'";

        String param = "";
        if (map.get("room")!=null && !"".equals(map.get("room")+"")){
            param = " and RoomInfo like '%"+map.get("room")+"%'";
        }
        Map<String,Object> sumMap = null;
        //查询总数
        String sql = "select count(1) count from VS_XK_S_ROOM where BldGUID in ("+buildIds+")"+param;
        // 判断是查询自销系统还是明源系统 true: 自销系统 false: 明源系统
        if (flag) {
            sumMap = housingManagementDao.getDataList(sql).get(0);
        }
        else {
            sumMap = DbTest.getObject(sql);
        }


        int total = Integer.valueOf(sumMap.get("count")+"");
        PageInfo page = new PageInfo<OpenActivityRoom>();
        page.setPageNum(pageIndex);
        page.setPageSize(pageSize);
        page.setTotal(total);
        if (total==0){
            return ResultBody.success(page);
        }else{
            //获取销售经理销控房间数据
            List<String> roomIds = openQuotationDao.getControls(buildIds);
            //分页查询数据
            int skip = (pageIndex-1)*pageSize;
            // int pages = pageIndex*pageSize>total?total:pageIndex*pageSize;
            List<Map<String,Object>> rooms = null;

            // 判断是查询自销系统还是明源系统 true: 自销系统 false: 明源系统
            if (flag) {
                String sql1 = "SELECT * FROM VS_XK_S_ROOM s WHERE BldGUID IN (" + buildIds + ") " + param + " ORDER BY s.BldGUID limit " + skip + "," + pageSize;
                rooms = housingManagementDao.getDataList(sql1);
            }
            else {
                int pages = pageIndex * pageSize > total ? total : pageIndex * pageSize;
                String sql1 = "select * from(select *,ROW_NUMBER() over(order by s.BldGUID) ROW_ID from (SELECT * FROM VS_XK_S_ROOM where BldGUID in ("+buildIds+")"+param+") s) t where t.ROW_ID between "+skip+" and "+pages;
                rooms = DbTest.getObjects(sql1);
            }

            List<OpenActivityRoom> list = new ArrayList<>();
            for (Map<String,Object> roomInfo:rooms) {
                OpenActivityRoom openRoom = new OpenActivityRoom();
                openRoom.setProjectId(roomInfo.get("PrjectGUID")+"");
                openRoom.setProjectFid(roomInfo.get("PrjectFGUID")+"");
                openRoom.setRoomId(roomInfo.get("RoomGUID")+"");
                openRoom.setRoomInfo(roomInfo.get("RoomInfo")+"");
                openRoom.setRoomNo(roomInfo.get("No")+"");
                String roomType = "";
                if (roomInfo.get("RoomStru")!=null){
                    roomType = roomInfo.get("RoomStru")+"";
                }
                openRoom.setRoomType(roomType);

                String salesStatus = "";
                String salesStatusDesc = "";
                //(1:待售2:认购3:销控4:签约5：预留)-->(1:可售 2:已售 3:销控 4:已预收款 5:已集中选房 6:正在认购 7:其它原因不可售)
                if ("1".equals(roomInfo.get("StatusEnum") + "")) {
                    if ("1".equals(roomInfo.get("RoomType") + "")){
                        salesStatus = "4";
                        salesStatusDesc = "已预收款";
                    }else if ("2".equals(roomInfo.get("RoomType") + "")){
                        salesStatusDesc = "已集中选房";
                        salesStatus = "5";
                    }else if ("0".equals(roomInfo.get("RoomType") + "")){
                        //判断是否被销售经理销控
                        if (roomIds!=null && roomIds.contains(openRoom.getRoomId())){
                            salesStatusDesc = "销控";
                            salesStatus = "3";
                        }else{
                            salesStatusDesc = "可售";
                            salesStatus = "1";
                        }
                    }else{
                        salesStatusDesc = "其它原因不可售";
                        salesStatus = "7";
                    }

                } else if ("2".equals(roomInfo.get("StatusEnum") + "") || "4".equals(roomInfo.get("StatusEnum") + "")) {
                    salesStatusDesc = "已售";
                    salesStatus = "2";
                } else if ("3".equals(roomInfo.get("StatusEnum") + "") || "5".equals(roomInfo.get("StatusEnum") + "")) {
                    salesStatusDesc = "销控";
                    salesStatus = "3";
                }
                openRoom.setRoomStatusdesc(salesStatusDesc);
                openRoom.setRoomStatus(salesStatus);
                openRoom.setRoomClassification(roomInfo.get("RoomType")+"");
                openRoom.setBuildId(roomInfo.get("BldGUID")+"");
                openRoom.setBuildName(roomInfo.get("BldName")+"");
                openRoom.setUnitNo(roomInfo.get("UnitNo")+"");
                openRoom.setUnitName(roomInfo.get("UnitName")+"");
                openRoom.setFloorNo(roomInfo.get("FloorNo")+"");
                openRoom.setFloor(roomInfo.get("FloorName")+"");
                openRoom.setBldArea(roomInfo.get("BldArea")+"");
                openRoom.setPrice(roomInfo.get("Price")+"");
                openRoom.setTnArea(roomInfo.get("TnArea")+"");
                openRoom.setTnPrice(roomInfo.get("TnPrice")+"");
                openRoom.setTotal(roomInfo.get("Total")+"");
                openRoom.setExposure("");
                if (roomInfo.get("HxGUID")!=null){
                    openRoom.setHxId(roomInfo.get("HxGUID")+"");
                }
                if (roomInfo.get("HxName")!=null){
                    openRoom.setHxName(roomInfo.get("HxName")+"");
                }
                if (roomInfo.get("x_HxImgName")!=null){
                    openRoom.setHxPhotoName(roomInfo.get("x_HxImgName")+"");
                }
                if (roomInfo.get("x_HxImgUrl")!=null){
                    openRoom.setHxPhotoUrl(roomInfo.get("x_HxImgUrl")+"");
                }
                list.add(openRoom);
            }
            page.setList(list);
            page.setTotal(total);
            return ResultBody.success(page);
        }

    }

    @Override
    public int queryRoomDelOk(Map map) {
        return openQuotationDao.queryRoomIsDel(map.get("roomId")+"");
    }

    @Override
    public ResultBody getProjectRule(String projectId) {
        return ResultBody.success(openQuotationDao.getProjectRuleByPojId(projectId));
    }

    @Override
    public ResultBody saveProjectRule(Map map) {
        if(StringUtils.isBlank(MapUtils.getString(map,"projectId",null))){
            return ResultBody.error(-21_0006,"必传参数未传");
        }
        //查询是否有规则
        Map ruleMap = openQuotationDao.getProjectRuleByPojId(MapUtils.getString(map,"projectId"));
        if(ruleMap != null){
            openQuotationDao.editProjectRule(map);
        }else{
            openQuotationDao.saveProjectRule(map);
        }
        return ResultBody.success("保存成功");
    }

    @Override
    public ResultBody getIsCollBank(String projectId) {
        return ResultBody.success(openQuotationDao.getIsCollBank(projectId));
    }

    @Override
    public ResultBody editOrder(Map map) {
        if(StringUtils.isBlank(MapUtils.getString(map,"orderNo",null))){
            return ResultBody.error(-21_0006,"订单编号不能为空");
        }
        if(StringUtils.isBlank(MapUtils.getString(map,"unbindReason",null))){
            return ResultBody.error(-21_0006,"解除原因不能为空");
        }
        //保存节点记录
        map.put("nodeType","8");
        map.put("nodeTypeName","订单解绑");
        openQuotationDao.saveOrderNode(map);
        return ResultBody.success(openQuotationDao.editOrder(map));
    }

    @Override
    public ResultBody getOrderDetail(Map map) {
        if(map.get("orderNo")==null || "".equals(map.get("orderNo")+"")){
            return ResultBody.error(-21_0006,"必传参数（订单编号）未传");
        }

        // 获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String companycode = request.getHeader("companycode");

        //获取是否开启自销系统
        boolean flag = false;
        if (redisUtil.get("ISZXOPEN_" + companycode) != null) {
            if ("1".equals(redisUtil.get("ISZXOPEN_" + companycode).toString())) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            //获取公司聚客汇菜单
            List<String> companyMenuList = new ArrayList<>();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("companycode", companycode);
            String res = "";
            try {
                res = HttpRequestUtil.httpPost(appMenuUrl, JSONObject.parseObject(JSONObject.toJSONString(paramMap)), false).toJSONString();
                MenuResult menuResult = JSONObject.toJavaObject(JSON.parseObject(res), MenuResult.class);
                if (menuResult != null && menuResult.getData() != null) {
                    companyMenuList = menuResult.getData();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResultBody.error(-20006, "获取是否自销系统失败！查询有误");
            }
            if (companyMenuList != null && companyMenuList.size() > 0) {
                if (companyMenuList.contains("appmenu5-10")) {
                    //保存缓存是否开启自销为是
                    redisUtil.set("ISZXOPEN_" + companycode, "1", 3600);
                    flag = true;
                } else {
                    flag = false;
                    redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
                }
            } else {
                flag = false;
                //保存缓存是否开启自销为否
                redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
            }
        }
        OrderDetail orderDetail = openQuotationDao.getOrderDetail(map.get("orderNo")+"");
        //获取购房人信息
        List<Buyers> buyers = openQuotationDao.getRelationBuyers(map.get("orderNo")+"");
        String roomId = orderDetail.getRoomId();
        String oppId = orderDetail.getOppId();
        if("1".equals(orderDetail.getOrderType())){
            orderDetail.setCreateName(openQuotationDao.getBrokerName(orderDetail.getOppId()));
        }else{
            orderDetail.setCreateName(openQuotationDao.getAccountName(orderDetail.getCreator()));
        }
        List<Map<String,Object>> roomPro = null;
        //根据房间获取项目ID
        String sql = "select PrjectGUID from vs_xk_s_room  where RoomGUID = '"+roomId+"'";
        if(flag){
            roomPro = housingManagementDao.getDataList(sql);
        }else{
            roomPro = DbTest.getObjects(sql);
        }
        Map proMap = null;
        if (roomPro!=null && roomPro.size()>0){
            proMap = roomPro.get(0);
        }
        if (proMap!=null && proMap.get("PrjectGUID")!=null){
            String projectId = proMap.get("PrjectGUID")+"";
            String url = foreignurl+"getCstCard";
            Map<String,Object> requestMap = new HashMap<>();
            requestMap.put("oppGuid",oppId);
            requestMap.put("projectId",projectId);
            String postDataJson = JSONObject.toJSONString(requestMap, SerializerFeature.WriteMapNullValue);
            String cardId = orderDetail.getRelCardId();
            //获取明源排卡数据  暂时不获取
//            JSONObject result = HttpRequestUtil.httpPost(url,JSONObject.parseObject(postDataJson), false);
//            if (result!=null && result.getJSONObject("result")!=null && "S".equals(result.getJSONObject("result").getString("code"))){
//                JSONArray dataJsonArray = result.getJSONObject("result").getJSONArray("data");
//                if(dataJsonArray != null && dataJsonArray.size() > 0){
//                    List<Map> cards = new ArrayList<>();
//                    for (int i = 0; i < dataJsonArray.size(); i++ ){
//                        Map cardMap = new HashMap();
//                        JSONObject dataJson = dataJsonArray.getJSONObject(i);
//                        String amountPaid = dataJson.getString("amountPaid");
//                        if(StringUtils.isBlank(amountPaid) || Double.valueOf(amountPaid)<=0){
//                            continue;
//                        }else{
//                            cardMap.put("amountPaid",de.format(Double.valueOf(amountPaid)));
//                        }
//                        String cardName = "";
//                        if(StringUtils.isNotBlank(dataJson.getString("projectFname"))){
//                            cardName = dataJson.getString("projectFname");
//                        }
//                        if(StringUtils.isNotBlank(dataJson.getString("cardType"))){
//                            if(StringUtils.isNotBlank(cardName)) {
//                                cardName =cardName+ "-" + dataJson.getString("cardType");
//                            }else{
//                                cardName = dataJson.getString("cardType");
//                            }
//                        }
//                        cardMap.put("cardName",cardName);
//                        String createTime = "";
//                        if(StringUtils.isNotBlank(dataJson.getString("createTime"))){
//                            createTime = dataJson.getString("createTime");
//                        }
//                        cardMap.put("createTime",createTime);
//                        if (StringUtils.isNotBlank(cardId) && cardId.equals(dataJson.getString("cardId"))){
//                            cardMap.put("isCheck","1");
//                        }else{
//                            cardMap.put("isCheck","0");
//                        }
//                        cards.add(cardMap);
//                    }
//                    orderDetail.setCards(cards);
//                }
//            }
        }
        if (buyers!=null){
            orderDetail.setRelationBuyers(buyers);
        }
        //获取订单节点记录
        List<OrderNode> nodes = openQuotationDao.getOrderNodes(map.get("orderNo")+"");
        if (nodes!=null){
            orderDetail.setOrderNodes(nodes);
        }
        return ResultBody.success(orderDetail);
    }

    @Override
    public String getRoomOrder(Map map) {
        return openQuotationDao.getRoomOrder(map);
    }

    @Override
    public ResultBody queryOpenActivityBanks(Map map) {
        if (map==null || map.get("buildIds")==null){
            return ResultBody.error(-21_0006, "必传参数未传！！");
        }
        List<OpenActivityBank> openActivityBanks = new ArrayList<>();

        String[] buildIds = map.get("buildIds").toString().split(",");
        String bids = StringUtils.join(buildIds,"','");
        //根据楼栋ID查询分期项目ID
        String sql = "select DISTINCT PrjectFGUID,Concat(KindeeProjectNAME,KindeeProjectFNAME) ProjectFNAME from VS_XK_S_BUILDING where BldGUID in ('"+bids+"');";
        List<Map<String,Object>> proMap = DbTest.getObjects(sql);
        if (proMap!=null && proMap.size()>0){
            List<OpenActivityBank> openActivityBankOld = new ArrayList<>();
            if (map.get("activityId")!=null && !"".equals(map.get("activityId")+"")){
                //查询活动原商户信息
                openActivityBankOld = openQuotationDao.getActivityBank(map.get("activityId")+"");
            }
            for (Map<String,Object> pMap:proMap) {
                OpenActivityBank openActivityBank = new OpenActivityBank();
                openActivityBank.setProjectFid(pMap.get("PrjectFGUID")+"");
                openActivityBank.setProjectFname(pMap.get("ProjectFNAME")+"");
                //将原来选择的商户赋值
                if (openActivityBankOld!=null && openActivityBankOld.size()>0){
                    for (OpenActivityBank old:openActivityBankOld) {
                        if (openActivityBank.getProjectFid().equals(old.getProjectFid())){
                            openActivityBank.setBankId(old.getBankId());
                        }
                    }
                }
                //查询商户信息
                List<ProBank> proBanks = openQuotationDao.getBankByPros(pMap.get("PrjectFGUID")+"");
                openActivityBank.setBanks(proBanks);
                openActivityBanks.add(openActivityBank);
            }
        }
        return ResultBody.success(openActivityBanks);
    }



    @Override
    public void openActivityResultExport(HttpServletRequest request, HttpServletResponse response, Map map) {
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("8");
        excelExportLog.setMainTypeDesc("在线开盘管理");
        excelExportLog.setSubType("O1");
        excelExportLog.setSubTypeDesc("开盘活动明细");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        try{
            List<String> proIdList = new ArrayList<>();
            if (map.get("projectIdss") != null && !"".equals(map.get("projectIdss")+"")) {
                String[] ids = map.get("projectIdss").toString().split(",");
                for (String proid : ids) {
                    proIdList.add(proid);
                }
            } else {
                Map mapf = new HashMap();
                mapf.put("UserName", map.get("userName"));
                Map userInfoMap = authMapper.mGetUserInfo(mapf);
                List<String> fullpath = projectMapper.findFullPath(mapf);
                StringBuffer sbs = new StringBuffer();
                for (int i = 0; i < fullpath.size(); i++) {
                    if (i == 0) {
                        sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                    } else {
                        sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                    }
                }
                List<Map> mapList = projectMapper.findProjectListByUserName(map.get("userName") + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
                if (mapList != null && mapList.size() > 0) {
                    for (Map proMap : mapList) {
                        proIdList.add(proMap.get("projectId") + "");
                    }
                }
            }
            map.put("projectIds",proIdList);
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setCreator(map.get("userId")+"");
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");

            List<OpenActivityResult> list = openQuotationDao.getOpenActivityResult(map);
            if (list!=null && list.size()>0){
                String[] headers = list.get(0).getActivityTitle();
                ArrayList<Object[]> dataset = new ArrayList<>();
                int num = 0;
                for (OpenActivityResult ac:list) {
                    num++;
                    ac.setRownum(num+"");
                    Object[] oArray = ac.toActivityData();
                    dataset.add(oArray);
                }

                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("开盘活动明细",headers,dataset,"开盘活动明细",response,null);

            }
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(sf.format(new Date()));
            excelExportLog.setIsDown("1");
            Long export = new Date().getTime();
            Long exporttime = export-nowtime;
            String exportTime =sf.format(Double.valueOf(exporttime+"")/1000);
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
    public int queryBuildDelOk(Map map) {
        return openQuotationDao.getBuildOrderCount(map);
    }

    @Override
    public OpenActivity getOpenActivityDetail(Map map) {
        String activityId = map.get("activityId")+"";
        //获取开盘活动基本信息
        OpenActivity openActivity = openQuotationDao.getOpenActivity(activityId);
        //获取活动素材
        List<IntentionPlaceMaterial> materialList = intentionPlaceDao.getIntentionPlaceMaterial(activityId);
        //获取活动关联楼盘
        List<OpenBuildBook> openBooks = openQuotationDao.getOpenBooks(activityId);
        //获取活动楼栋
        map.put("id",activityId);
        List<OpenActivityBuild> activityBuilds = openQuotationDao.getActivityBuilds(map);
        //获取活动折扣信息
        List<OpenDiscount> openDiscount = openQuotationDao.getOpenDiscount(activityId);
        //获取活动概览图坐标
        List<OpenBuildSite> openBuildSites = openQuotationDao.getOpenBuildSite(activityId);
        //获取开盘活动商户信息
        List<OpenActivityBank> openActivityBanks = openQuotationDao.getActivityBank(activityId);
        if (openActivityBanks!=null && openActivityBanks.size()>0){
            for (OpenActivityBank open:openActivityBanks) {
                List<ProBank> proBanks = openQuotationDao.getBankByPros(open.getProjectFid());
                if (proBanks!=null){
                    open.setBanks(proBanks);
                }
            }
        }
        openActivity.setActivityBanks(openActivityBanks);
        //获取不可展示房间
        List<OpenNotRoom> rooms = openQuotationDao.getNotShowRoom(activityId);
        openActivity.setRooms(rooms);
        openActivity.setBuildBooks(openBooks);
        openActivity.setBuildings(activityBuilds);
        if (openBuildSites!=null && openBuildSites.size()>0){
            openActivity.setBuildSites(openBuildSites);
        }
        //获取精选房间
        List<OpenActivityRoom> roomList = openQuotationDao.getOpenActivityRoom(activityId);
        openActivity.setRoomList(roomList);
        openActivity.setMaterialList(materialList);
        openActivity.setActivityDiscount(openDiscount);
        return openActivity;
    }


    /**
     * 线下支付凭证审核列表
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody offlinePayCheckList(Map map) {
        if (null == map) {
            return ResultBody.error(-2001, "请求参数为空！");
        }
        // 1. 取出所有的项目id
        String project_ids = (String) map.get("project_id");
        // 2. 使用split进行分割
        String[] strings = project_ids.split(",");
        // 进行分割后的数组大小大于1, 说明传讯的是多个项目id, 需要组装数据库认识的sql语句
        if (strings.length > 1) {
            StringBuilder builder = new StringBuilder("(");
            // 3. 判断strings不为空进行遍历，组装数据库认识的sql语句
            //if (strings.length > 0) {
                for (int i = 0; i < strings.length; i++) {
                    builder.append("'" + strings[i] + "',");
                }
                // 4. 去掉最后的逗号
                String s = builder.toString();
                System.out.println(s);
                // 5. 项目id字符串后面拼接)完成sql语句的封闭
                String s1 = s.substring(0, s.length() - 1) + ")";
                // 6. 重新赋值给项目id
                map.put("project_id", s1);
            //}
        }
        else {
            map.put("project_id", "('" + project_ids + "')");
        }

        PageHelper.startPage((Integer) map.get("pageIndex"), (Integer) map.get("pageSize"));
        return ResultBody.success(new PageInfo<>(openQuotationDao.offlinePayCheckList(map)));
    }

    /**
     * 审批凭证
     *  status: 审批状态（1：待审核 2：审批通过, 3: 驳回）
     * @param map
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody checkCertificate(Map map) {
        if (null == map) {
            return ResultBody.error(-2001, "请求参数为空！");
        }
        // 获取 HttpServletRequest 对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = request.getHeader("userid");
        String username = request.getHeader("username");
        // 根据username查询名字
        String name = openQuotationDao.getNameByusername(username);
        OfflineCertificate offlinePay = openQuotationDao.getOfflinePay(map);
        if (offlinePay.getStatus()!=1){
            return ResultBody.error(-1200002,"此单审批状态已改变请刷新重试！");
        }

        // 返回值
        Integer returnValue = 0;
        // 1. checkFlag 不为空则走通过分支， 否，驳回分支
        if (StringUtils.isNotEmpty((String) map.get("checkFlag"))) {
            // 审批状态（1：待审核 2：审批通过, 3: 驳回）
            map.put("status", 2);
            map.put("status_name", "审批通过");
            map.put("approve_id", userId);
            map.put("approve_name", name);
            // 更新订单，交易，房间，机会状态，添加记录
            this.updateStatus(map);

            // 通过
            returnValue = openQuotationDao.checkCertificate(map);
            this.addCheckCertificateMessage(offlinePay,null);
            return returnValue > 0 ? ResultBody.success("通过成功") : ResultBody.success("通过失败");
        }
        else {
            // 审批状态（1：待审核 2：审批通过, 3: 驳回）
            map.put("status", 3);
            map.put("status_name", "驳回");
            map.put("approve_id", userId);
            map.put("approve_name", name);
            String rejectionReason = map.get("rejection_reason")+"";
            // 驳回
            returnValue = openQuotationDao.checkCertificate(map);
            this.addCheckCertificateMessage(offlinePay,rejectionReason);
        }
        return returnValue > 0 ? ResultBody.success("驳回成功") : ResultBody.success("驳回失败");
    }

    /**
     * @Author wanggang
     * @Description //线下支付凭证审批消息 小程序消息
     * @Date 16:40 2021/11/20
     * @Param [oppMap, reason]
     * @return void
     **/
    private void addCheckCertificateMessage(OfflineCertificate offlinePay,String reason){
        List<Map> messageList = new ArrayList<>();
        String subject = "";
        String messageType = "71111";
        String content = "";
        String isPush = "1";
        String isNeedPush = "1";
        //判断是否审批通过 reason为null即通过
        if (!StringUtils.isEmpty(reason)){
            subject = "支付凭证审核通知";
            content = "您申请的"+offlinePay.getRoom_name()+"房间支付凭证审批被驳回，原因："+reason+",请知悉。";
        }else{
            subject = "支付凭证审核通知";
            content = "您申请的"+offlinePay.getRoom_name()+"房间支付凭证审批通过，请知悉。";
        }
        Map smassage = new HashMap();
        smassage.put("subject", subject);
        smassage.put("content", content);
        smassage.put("messageType", messageType);
        smassage.put("receiver", offlinePay.getSubmit());
        smassage.put("isPush", isPush);
        smassage.put("isNeedPush", isNeedPush);
        messageList.add(smassage);
        //保存消息
        if (messageList.size() > 0) {
            housingManagementDao.insertMessage(messageList);
        }
    }

    /**
     * 更新订单，交易，房间，机会状态，添加记录
     *
     * @param map
     */
    public void updateStatus(Map map) {
        // 根据id查询线下支付审核凭证
        Map payMap = openQuotationDao.getRecordById(map);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 1. 更新订单
        Map orderMap = new HashMap(8);
        orderMap.put("TradeStatus", "认购");
        // 订单状态 1 未完成 2 已完成 3 作废
        orderMap.put("status", "2");
        // 支付状态 1 未支付 2 已支付 3支付失败 4 取消支付,5 支付中
        orderMap.put("payStatus", "2");
        //
        orderMap.put("pay_type", 0);
        orderMap.put("isLockRoom", 0);
        orderMap.put("tradeGuid", payMap.get("trade_guid"));
        // 取消订单
        openQuotationDao.updateOrder(orderMap);

        // 根据交易id查询订单信息
        Map orderInfo = openQuotationDao.getOrderInfoByTradeGuid(payMap.get("trade_guid") + "");
        // 添加订单记录
        OrderNodeRecord orderNodeRecord = this.getOrderNodeRecord((String) orderInfo.get("creator"), (String) orderInfo.get("order_no"), "3", null, null);
        openQuotationDao.saveOrderNodeRecord(orderNodeRecord);
        // 添加订单记录
        OrderNodeRecord orderNodeRecord1 = this.getOrderNodeRecord((String) orderInfo.get("creator"), (String) orderInfo.get("order_no"), "5", null, null);
        openQuotationDao.saveOrderNodeRecord(orderNodeRecord1);

        // 3. 房间 更新房间状态为待售 房间状态(1:待售2:认购3:销控4:签约5：预留)
        housingManagementDao.updateRoomStatus((String) payMap.get("room_id"),"2", 0);

        // 4. 机会状态 更新主客户机会
        //根据交易ID查询数据
        OppTradeVo oppTradeVo = openQuotationDao.getOppTradeVo(payMap.get("trade_guid") + "");
        //查询机会数据
        Map oppMap = openQuotationDao.getOppData(oppTradeVo.getOpportunityClueId());
        System.out.println("机会数据为： " + oppMap);
        String ClueStatus = "7";
        //查询是否存在其他交易
        List<OppTradeVo> oppTradeVoList = openQuotationDao.getOldOppTradeVoList(oppTradeVo.getOpportunityClueId(), payMap.get("trade_guid") + "");
        if (CollectionUtils.isNotEmpty(oppTradeVoList)){
            boolean flag = false;
            for (OppTradeVo oppvo : oppTradeVoList) {
                if ("签约".equals(oppvo.getClueStatus())){
                    flag = true;
                    break;
                }
            }
            if (flag){
                ClueStatus = "8";
            }else{
                ClueStatus = "7";
            }
        }
        //更新机会状态
        openQuotationDao.updateOppTStatus(oppMap.get("ProjectClueId") + "", ClueStatus);

        // 2. 交易
        Map dataMap = new HashMap(8);
        dataMap.put("UpdateTime", sf.format(new Date()));
        // 交易id
        dataMap.put("TradeGUID", payMap.get("trade_guid"));
        // 认购业务归属日期
        dataMap.put("OrderYwgsDate", sf.format(new Date()));
        openQuotationDao.updateOppTradeVo(dataMap);


        // 更新实收金额根据交易id
        Map map1 = new HashMap();
        // 交易id
        map1.put("trade_guid", payMap.get("trade_guid"));
        map1.put("client_mobile", payMap.get("client_mobile"));
        map1.put("updator", request.getHeader("username"));
        // 跟新实收时间，金额
        openQuotationDao.updateReturnedMoneyByTradeGUID(map1);

        //删除销控数据
        openQuotationDao.updateHousingSalesControl((String) orderInfo.get("room_id"));

        // 插入节点记录
        oppMap.put("followUpWay", "认购");
        oppMap.put("RoomName", orderInfo.get("room_name"));
        oppMap.put("followUpDate", sf.format(new Date()));
        this.addFollowUp(oppMap);


        //发送消息
        this.addMessage(oppMap,"1",oppTradeVo);
        // 5. 添加记录
    }

    /**
     * 添加节点记录
     */
    public void addFollowUp(Map oppMap){
        //插入节点记录
        HashMap<String, Object> nodeMap = new HashMap<>();
        try{
            nodeMap.put("projectId", oppMap.get("projectId"));
            nodeMap.put("followUpWay",oppMap.get("followUpWay"));
            nodeMap.put("CommunicationContent","");
            nodeMap.put("FollowUpDetail","客户" + oppMap.get("followUpWay") + oppMap.get("RoomName"));
            nodeMap.put("userId",oppMap.get("SalesAttributionId"));
            nodeMap.put("clueId",oppMap.get("ProjectClueId"));
            nodeMap.put("JobOrgID",oppMap.get("SalesAttributionTeamId"));
            nodeMap.put("EmployeeName",oppMap.get("SalesAttributionName"));
            nodeMap.put("orgName","置业顾问");
            nodeMap.put("oppoId",oppMap.get("OpportunityClueId"));
            nodeMap.put("customerId",oppMap.get("BasicCustomerId"));
            nodeMap.put("followUpDate",oppMap.get("followUpDate"));
            openQuotationDao.savaFollowupRecord(nodeMap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 保存消息
     */
    public void addMessage(Map oppMap, String type, OppTradeVo trade) {
        List<Map> messageList = new ArrayList<>();
        String tradeDate = "";
        String subject = "";
        String tradeType = "";
        String messageType = "";
        Map smessage = new HashMap();
        smessage.put("ProjectClueId", oppMap.get("ProjectClueId"));
        List<Map> messageOld = openQuotationDao.selectMessageALL(smessage);

        Boolean isOrder = false;//是否存在置业顾问认购消息
        Boolean isOrder1 = false;//是否存在渠道顾问认购消息
        Boolean isOrder2 = false;//是否存在渠道组长认购消息
        Boolean isOrder3 = false;//是否存在渠道经理认购消息
        Boolean isOrder4 = false;//是否存在销售经理认购消息
        Boolean isOrder5 = false;//是否存在销售组长认购消息
        Boolean isCont = false;//是否存在置业顾问签约消息
        Boolean isCont1 = false;//是否存在渠道顾问签约消息
        Boolean isCont2 = false;//是否存在渠道组长签约消息
        Boolean isCont3 = false;//是否存在渠道经理签约消息
        Boolean isCont4 = false;//是否存在销售经理签约消息
        Boolean isCont5 = false;//是否存在销售组长签约消息
        if (messageOld != null && messageOld.size() > 0) {
            for (Map mMap : messageOld) {
                if ("2110".equals(mMap.get("MessageType") + "")) {
                    isCont = true;
                } else if ("21101".equals(mMap.get("MessageType") + "")) {
                    isCont1 = true;
                } else if ("21102".equals(mMap.get("MessageType") + "")) {
                    isCont2 = true;
                } else if ("21103".equals(mMap.get("MessageType") + "")) {
                    isCont3 = true;
                } else if ("21104".equals(mMap.get("MessageType") + "")) {
                    isCont4 = true;
                } else if ("21105".equals(mMap.get("MessageType") + "")) {
                    isCont5 = true;
                } else if ("2111".equals(mMap.get("MessageType") + "")) {
                    isOrder = true;
                } else if ("21111".equals(mMap.get("MessageType") + "")) {
                    isOrder1 = true;
                } else if ("21112".equals(mMap.get("MessageType") + "")) {
                    isOrder2 = true;
                } else if ("21113".equals(mMap.get("MessageType") + "")) {
                    isOrder3 = true;
                } else if ("21114".equals(mMap.get("MessageType") + "")) {
                    isOrder4 = true;
                } else if ("21115".equals(mMap.get("MessageType") + "")) {
                    isOrder5 = true;
                }
            }
        }
        String isPush = "1";
        String isNeedPush = "1";
        //判断需要发送的消息类型(1：认购 2：签约)
        if ("1".equals(type)) {
            subject = "认购通知";
            tradeType = "认购";
            tradeDate = trade.getOrderDate();
            //置业顾问消息
            if (!isOrder) {
                messageType = "2111";
                Map smassage = new HashMap();
                smassage.put("subject", subject);
                smassage.put("content", "客户【" + oppMap.get("CustomerName") + "】已完成" + tradeType + " 房间号：" + trade.getRoomName() + " " + tradeType + "日期：" + tradeDate + " 请知悉。");
                smassage.put("messageType", messageType);
                smassage.put("receiver", oppMap.get("SalesAttributionId"));
                smassage.put("isPush", isPush);
                smassage.put("isNeedPush", isNeedPush);
                smassage.put("projectClueId", oppMap.get("ProjectClueId"));
                messageList.add(smassage);
            }
            //销售经理消息
            if (!isOrder4) {
                List<Map> xsjl = openQuotationDao.findXSJL(oppMap.get("projectId") + "");
                if (xsjl != null && xsjl.size() > 0) {
                    messageType = "21114";
                    for (Map map : xsjl) {
                        Map smassage = new HashMap();
                        smassage.put("subject", subject);
                        smassage.put("content", "客户【" + oppMap.get("CustomerName") + "】已完成" + tradeType + " 房间号：" + trade.getRoomName() + " " + tradeType + "日期：" + tradeDate + " 请知悉。");
                        smassage.put("messageType", messageType);
                        smassage.put("receiver", map.get("id"));
                        smassage.put("isPush", isPush);
                        smassage.put("isNeedPush", isNeedPush);
                        smassage.put("projectClueId", oppMap.get("ProjectClueId"));
                        messageList.add(smassage);
                    }
                }
            }
        } else if ("2".equals(type)) {
            subject = "签约通知";
            tradeType = "签约";
            tradeDate = trade.getContractDate();
            //置业顾问消息
            if (!isCont) {
                messageType = "2110";
                Map smassage = new HashMap();
                smassage.put("subject", subject);
                smassage.put("content", "客户【" + oppMap.get("CustomerName") + "】已完成" + tradeType + " 房间号：" + trade.getRoomName() + " " + tradeType + "日期：" + tradeDate + " 请知悉。");
                smassage.put("messageType", messageType);
                smassage.put("receiver", oppMap.get("SalesAttributionId"));
                smassage.put("isPush", isPush);
                smassage.put("isNeedPush", isNeedPush);
                smassage.put("projectClueId", oppMap.get("ProjectClueId"));
                messageList.add(smassage);
            }
            //销售经理消息
            if (!isCont4) {
                List<Map> xsjl = openQuotationDao.findXSJL(oppMap.get("projectId") + "");
                if (xsjl != null && xsjl.size() > 0) {
                    messageType = "21104";
                    for (Map map : xsjl) {
                        Map smassage = new HashMap();
                        smassage.put("subject", subject);
                        smassage.put("content", "客户【" + oppMap.get("CustomerName") + "】已完成" + tradeType + " 房间号：" + trade.getRoomName() + " " + tradeType + "日期：" + tradeDate + " 请知悉。");
                        smassage.put("messageType", messageType);
                        smassage.put("receiver", map.get("id"));
                        smassage.put("isPush", isPush);
                        smassage.put("isNeedPush", isNeedPush);
                        smassage.put("projectClueId", oppMap.get("ProjectClueId"));
                        messageList.add(smassage);
                    }
                }
            }
        }
        //保存消息
        if (messageList.size() > 0) {
            openQuotationDao.insertMessage(messageList);
        }

    }

    /***
     *
     * @param userId
     * @param orderNo
     * @param type
     * @param bankName
     * @param bankId
     *@return {}
     *@throws
     *@Description: 认购节点记录
     *@author FuYong
     *@date 2021/1/12 18:26
     */
    public OrderNodeRecord getOrderNodeRecord(String userId, String orderNo, String type, String bankName, String bankId) {
        OrderNodeRecord orderNodeRecord = new OrderNodeRecord();
        if ("1".equals(type)) {
            orderNodeRecord.setNodeType("1");
            orderNodeRecord.setNodeTypeName("订单生成");
            orderNodeRecord.setReason(null);
        } else if ("2".equals(type)) {
            orderNodeRecord.setNodeType("2");
            orderNodeRecord.setNodeTypeName("发起支付");
            orderNodeRecord.setReason("收款商户：" + bankName + ":" + bankId);
        } else if ("3".equals(type)) {
            orderNodeRecord.setNodeType("3");
            orderNodeRecord.setNodeTypeName("支付成功");
            orderNodeRecord.setReason(null);
        } else if ("4".equals(type)) {
            orderNodeRecord.setNodeType("4");
            orderNodeRecord.setNodeTypeName("支付失败");
            orderNodeRecord.setReason("收款商户：" + bankName + ":" + bankId);
        } else if ("5".equals(type)) {
            orderNodeRecord.setNodeType("5");
            orderNodeRecord.setNodeTypeName("订单完成");
            orderNodeRecord.setReason(null);
        } else if ("6".equals(type)) {
            orderNodeRecord.setNodeType("6");
            orderNodeRecord.setNodeTypeName("签署认购书");
            orderNodeRecord.setReason(null);
        } else if ("7".equals(type)) {
            orderNodeRecord.setNodeType("7");
            orderNodeRecord.setNodeTypeName("订单关闭");
            orderNodeRecord.setReason("关闭原因：取消订单");
        } else if ("8".equals(type)) {
            orderNodeRecord.setNodeType("7");
            orderNodeRecord.setNodeTypeName("订单关闭");
            orderNodeRecord.setReason("退房");
        } else if ("9".equals(type)) {
            orderNodeRecord.setNodeType("7");
            orderNodeRecord.setNodeTypeName("订单关闭");
            orderNodeRecord.setReason("换房");
        } else if ("10".equals(type)) {
            orderNodeRecord.setNodeType("7");
            orderNodeRecord.setNodeTypeName("订单关闭");
            orderNodeRecord.setReason("关撤销认购");
        } else if ("11".equals(type)) {
            orderNodeRecord.setNodeType("7");
            orderNodeRecord.setNodeTypeName("订单关闭");
            orderNodeRecord.setReason("退认购");
        } else if ("12".equals(type)) {
            orderNodeRecord.setNodeType("7");
            orderNodeRecord.setNodeTypeName("订单关闭");
            orderNodeRecord.setReason("支付超时取消订单");
        }
        orderNodeRecord.setCreator(userId);
        orderNodeRecord.setOrderNo(orderNo);
        return orderNodeRecord;
    }

    /**
     * 查看驳回记录通过主键id
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody getRecordById(Map map) {
        if (null == map) {
            return ResultBody.error(-2001, "请求参数为空！");
        }
        return ResultBody.success(openQuotationDao.getRecordById(map));
    }

    /**
     * 导出审核列表数据通过项目id
     *
     * @param map
     * @param request
     * @param response
     * @return
     */
    @Override
    public void exportCheckListData(Map map, HttpServletRequest request, HttpServletResponse response) {
        // 查询支付审核凭证列表数据
        List<OfflineCertificate> list = openQuotationDao.selectCertificateList(map);
        if (CollectionUtils.isNotEmpty(list)) {
            String[] headers = list.get(0).getCertificateTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();
            int num = 0;
            for (OfflineCertificate oc : list) {
                num++;
                oc.setRownum(num + "");
                Object[] oArray = oc.toCertificateData();
                dataset.add(oArray);
            }
            try {
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("线下支付审核凭证列表", headers, dataset, "线下支付审核凭证列表", response, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
