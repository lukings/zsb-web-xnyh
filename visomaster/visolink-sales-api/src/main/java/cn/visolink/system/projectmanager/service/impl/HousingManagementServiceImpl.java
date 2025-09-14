package cn.visolink.system.projectmanager.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.common.security.domain.MenuResult;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.openQuotation.model.OppTradeVo;
import cn.visolink.system.openQuotation.model.OrderNodeRecord;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.projectmanager.dao.HousingManagementDao;
import cn.visolink.system.projectmanager.model.*;
import cn.visolink.system.projectmanager.model.Vo.*;
import cn.visolink.system.projectmanager.service.HousingManagementService;
import cn.visolink.utils.*;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName HousingManagementServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/11/24 14:48
 **/
@Service
public class HousingManagementServiceImpl implements HousingManagementService {

    @Autowired
    private HousingManagementDao housingManagementDao;

    @Value("${AppMenuUrl}")
    private String appMenuUrl;

    @Autowired
    private RedisUtil redisUtil;
    /**
     * 获取验证码接口
     */
    @Value("${CODE_PATH}")
    private String codeUrl;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private ExcelImportMapper excelImportMapper;

    @Override
    public PageInfo<TradeCstData> getTradeCstList(TradeCstForm tradeCstForm) {
        // 分页数据设置
        PageHelper.startPage((int) tradeCstForm.getCurrent(), (int) tradeCstForm.getSize());
        List<TradeCstData> list = housingManagementDao.getTradeCstList(tradeCstForm);
        return new PageInfo<>(list);
    }

    @Override
    public void tradeCstExport(HttpServletRequest request, HttpServletResponse response, String params) {
        TradeCstForm param = JSONObject.parseObject(params,TradeCstForm.class);
        ArrayList<Object[]> dataset = new ArrayList<>();
        try {
            //查询数据
            List<TradeCstData> list = housingManagementDao.getTradeCstList(param);
            if (list.size()>0){
                String[] headers = list.get(0).getTradeTitle();
                for (int i = 0; i < list.size(); i++) {
                    TradeCstData activityInfoVO = list.get(i);
                    activityInfoVO.setRownum(i + 1);
                    Object[] oArray = activityInfoVO.toTradeData(param.getIsAll());
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("交易明细表", headers,dataset, "交易明细表", response,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultBody getRoomList(HttpServletRequest request, Map param) {
        String companycode = request.getHeader("companycode");
        if (param.get("bldguidList") == null){
            return ResultBody.error(-1200002,"楼栋ID未传！");
        }
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
                return ResultBody.error(-1200001,"获取是否自销系统失败！查询有误");
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
        try{
            //楼栋ID
            String bldguidList = param.get("bldguidList").toString();
            String[] bldIds = bldguidList.split(",");
            List<BuildingVo> buildingVoList = new ArrayList<>();
            for (String bldId:bldIds) {
                BuildingVo buildingVo = new BuildingVo();
                String bldNameSql = "";//查询楼栋名称
                List<Map<String, Object>> bldingList = null;
                //获取楼栋数据
                if (flag){
                    bldNameSql = "select DISTINCT CONCAT_WS('-',bp.ProjectName,(case when bld.x_productType is not null and bld.x_productType !='' then bld.x_productType else '住宅' end),bld.BldName) name from vs_xk_s_building bld" +
                            " INNER JOIN b_project bp on bp.ID = bld.PrjectGUID " +
                            "where bld.BldGUID = '"+bldId+"'";
                    bldingList = housingManagementDao.getDataList(bldNameSql);
                }else{
                    bldNameSql = "select DISTINCT bp.ProjName +'-'+ (case when bld.x_productType is not null and bld.x_productType !='' then bld.x_productType else '住宅' end) +'-'+ bld.BldName name " +
                            "from vs_xk_s_building bld " +
                            "INNER JOIN p_Project bp on bp.p_projectId = bld.PrjectGUID" +
                            " where bld.BldGUID = '"+bldId+"'";
                    bldingList = DbTest.getObjects(bldNameSql);
                }
                buildingVo.setBldName(bldingList.get(0).get("name")+"");

                //根据楼栋获取单元信息
                List<Map<String, Object>> result = null;
                String sql = "SELECT BldUnitGUID,UnitName,UnitNo FROM VS_XK_S_BUILDING " +
                        "WHERE BldGUID = '" + bldId + "' order by UnitNo";
                //判断是否自销调用
                if (flag){
                    result = housingManagementDao.getDataList(sql);
                }else{
                    result = DbTest.getObjects(sql);
                }
                List<UnitVo> unitVoList = new ArrayList<>();
                for (Map<String, Object> map : result) {
                    if (map.get("UnitNo")!=null){
                        UnitVo buildingUnitVO = new UnitVo();
                        buildingUnitVO.setUnitNo(map.get("UnitNo") + "");
                        buildingUnitVO.setUnitName(map.get("UnitName") + "");
                        //获取楼层房间最大数
                        int maxCount = 0;
                        //根据楼栋单元获取房间SQL语句
                        String roomSqlSum = "";
                        List<Map<String, Object>> countMap = null;
                        //判断是否自销调用
                        if (flag){
                            roomSqlSum = "select FloorNo,max(RoomNo) count from VS_XK_S_ROOM where BldGUID = '" + bldId + "' AND UnitNo = '" + map.get("UnitNo") + "' and FloorNo  >-1 group by FloorNo order by count desc limit 1";
                            countMap = housingManagementDao.getDataList(roomSqlSum);
                        }else{
                            roomSqlSum = "select top 1 FloorNo,max(cast(RoomNo as int)) count from VS_XK_S_ROOM where BldGUID = '" + bldId + "' AND UnitNo = '" + map.get("UnitNo") + "' and cast(FloorNo as int) >-1 group by FloorNo order by count desc";
                            countMap = DbTest.getObjects(roomSqlSum);
                        }
                        if (null != countMap && countMap.size()>0) {
                            Map<String, Object> cMap = countMap.get(0);
                            if (null!=cMap.get("count") && !"".equals(cMap.get("count"))){
                                if (flag){
                                    maxCount = Integer.valueOf(cMap.get("count")+"");
                                }else {
                                    maxCount = Integer.valueOf(cMap.get("count")+"")+1;
                                }
                            }
                        }
                        buildingUnitVO.setRoomMax(maxCount+"");
                        unitVoList.add(buildingUnitVO);
                    }
                }
                buildingVo.setUnitVoList(unitVoList);
                //根据楼栋获取楼层信息
                List<Map<String, Object>> floors = null;
                String floorSql = "select FloorNo,FloorName from vs_xk_s_room " +
                        "where BldGUID = '"+bldId+"' GROUP BY FloorNo ORDER BY FloorNo";
                //判断是否自销调用
                if (flag){
                    floors = housingManagementDao.getDataList(floorSql);
                }else{
                    floors = DbTest.getObjects(floorSql);
                }

                //获取预销控房间信息
                List<String> housingSalesRoomList = housingManagementDao.getHousingSalesRoom(bldId);
                //根据楼栋获取房间信息
                List<Map<String, Object>> rooms = null;
                String roomSql = "";
                if (flag){
                    roomSql = "select FloorNo,FloorName,RoomGUID,RoomInfo,RoomNo,no,Room,UnitNo,UnitName,StatusEnum from vs_xk_s_room where BldGUID = '"+bldId+"' ORDER BY UnitNo,FloorNo,no";
                    rooms = housingManagementDao.getDataList(roomSql);
                }else{
                    roomSql = "select FloorNo,FloorName,RoomGUID,RoomInfo,RoomNo,RoomNo no,Room,UnitNo,UnitName,StatusEnum from vs_xk_s_room where BldGUID = '"+bldId+"' ORDER BY UnitNo,FloorNo,RoomNo";
                    rooms = DbTest.getObjects(roomSql);
                }
                List<FloorVo> floorVoList = new ArrayList<>();
                for (Map<String, Object> map : floors) {
                    String floorNo = map.get("FloorNo")+"";
                    FloorVo floorVo = new FloorVo();
                    floorVo.setFloorNo(floorNo);
                    floorVo.setFloorName(map.get("FloorName")+"");
                    List<RoomListVo> roomListVoList = new ArrayList<>();
                    for (Map<String, Object> maproom :rooms) {
                        if (floorNo.equals(maproom.get("FloorNo")+"")){
                            RoomListVo roomListVo = new RoomListVo();
                            roomListVo.setFloorNo(floorNo);
                            roomListVo.setFloorName(maproom.get("FloorName")+"");
                            roomListVo.setRoom(maproom.get("Room")+"");
                            roomListVo.setRoomGUID(maproom.get("RoomGUID")+"");
                            roomListVo.setRoomInfo(maproom.get("RoomInfo")+"");
                            roomListVo.setRoomNo(maproom.get("RoomNo")+"");
                            roomListVo.setNo(maproom.get("no")+"");
                            if (flag){
                                roomListVo.setNo(maproom.get("no")+"");
                            }else {
                                roomListVo.setNo((Integer.parseInt(maproom.get("no")+"")+1)+"");
                            }
                            roomListVo.setUnitNo(maproom.get("UnitNo")+"");
                            roomListVo.setUnitName(maproom.get("UnitName")+"");
                            String StatusEnum = maproom.get("StatusEnum")+"";
                            if (housingSalesRoomList!=null && housingSalesRoomList.size()>0
                            && "1".equals(StatusEnum) && housingSalesRoomList.contains(maproom.get("RoomGUID")+"")){
                                StatusEnum = "6";
                            }
                            roomListVo.setStatusEnum(StatusEnum);
                            roomListVoList.add(roomListVo);
                        }
                    }
                    floorVo.setRoomList(roomListVoList);
                    floorVoList.add(floorVo);
                }
                buildingVo.setFloorVoList(floorVoList);
                //查询楼栋房间状态
                String bldRoomSumSql = "select sum(case when StatusEnum = 1 then 1 else 0 end) dsSum," +
                        "sum(case when StatusEnum = 2 then 1 else 0 end) rgSum," +
                        "sum(case when StatusEnum = 3 then 1 else 0 end) xkSum," +
                        "sum(case when StatusEnum = 4 then 1 else 0 end) qySum" +
                        " from vs_xk_s_room where BldGUID = '"+bldId+"'";
                List<Map<String, Object>> roomSums = null;
                if (flag){
                    roomSums = housingManagementDao.getDataList(bldRoomSumSql);
                }else{
                    roomSums = DbTest.getObjects(bldRoomSumSql);
                }
                Map<String, Object> roomSumMap = roomSums.get(0);
                String dsSum = roomSumMap.get("dsSum")+"";
                String rgSum = roomSumMap.get("rgSum")+"";
                String xkSum = roomSumMap.get("xkSum")+"";
                String qySum = roomSumMap.get("qySum")+"";
                String yxkSum = "0";

                if (housingSalesRoomList!=null && housingSalesRoomList.size()>0){
                    yxkSum = housingSalesRoomList.size()+"";
                    int ds = Integer.parseInt(dsSum) - housingSalesRoomList.size();
                    dsSum = ds+"";
                }
                buildingVo.setDsSum(dsSum);
                buildingVo.setQySum(qySum);
                buildingVo.setRgSum(rgSum);
                buildingVo.setXkSum(xkSum);
                buildingVo.setYxkSum(yxkSum);
                buildingVoList.add(buildingVo);
            }
            return ResultBody.success(buildingVoList);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-120002,"获取房间数据异常！");
        }

    }

    @Override
    public ResultBody getRoomById(HttpServletRequest request, String roomGUID) {
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
                return ResultBody.error(-1200001,"获取是否自销系统失败！查询有误");
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
        ResultBody pubRes = new ResultBody();
        try{
            RoomVo roomVo = new RoomVo();
            String roomSql = "";
            List<Map<String, Object>> roomMaps = null;
            //判断是否自销调用
            if (flag){
                roomSql = "SELECT r.BldGUID,r.Price BldPrice,r.TnPrice,r.BldArea,r.RoomInfo,r.Room,r.RoomGUID,r.TnArea,r.Total,r.RoomStru,r.StatusEnum,r.HxName,r.calMode,r.areaStatus,c.CustomerTel,c.Zygw,c.CustomerName,c.CjBldPrice,c.CjTnPrice,c.ContractDate,c.OrderDate,c.HtTotal,c.ContractGUID,c.OrderGUID,c.OppGUID,c.TradeGUID,c.remarks,s.PermitNum,r.PrjectFGUID,r.HxGUID FROM VS_XK_S_ROOM r LEFT JOIN VS_XK_S_BUILDING s on r.BldGUID = s.BldGUID LEFT JOIN VS_XK_S_CONTRACT c on r.RoomGUID = c.RoomGUID  and c.TradeStatus = '激活' WHERE r.RoomGUID = '"+roomGUID+"' limit 1";
                roomMaps = housingManagementDao.getDataList(roomSql);
            }else{
                roomSql = "SELECT top 1 r.BldGUID,r.Price BldPrice,r.TnPrice,r.BldArea,r.RoomInfo,r.Room,r.RoomGUID,r.TnArea,r.Total,r.RoomStru,r.StatusEnum,r.HxName,'' calMode,'' areaStatus,c.CustomerTel,c.Zygw,c.CustomerName,c.CjBldPrice,c.CjTnPrice,c.ContractDate,c.OrderDate,c.HtTotal,c.ContractGUID,c.OrderGUID,c.OppGUID,s.PermitNum,r.PrjectFGUID,r.HxGUID FROM VS_XK_S_ROOM r LEFT JOIN VS_XK_S_BUILDING s on r.BldGUID = s.BldGUID LEFT JOIN VS_XK_S_CONTRACT c on r.RoomGUID = c.RoomGUID  and c.TradeStatus = '激活' WHERE r.RoomGUID = '"+roomGUID+"'";
                roomMaps = DbTest.getObjects(roomSql);
            }
            if (null != roomMaps) {
                for (Map<String, Object> roomMap : roomMaps) {
                    //获取预销控房间信息
                    List<String> housingSalesRoomList = null;
                    String bldId = "";
                    if (roomMap.get("BldGUID") != null && !"".equals(roomMap.get("BldGUID")+"")){
                        bldId = roomMap.get("BldGUID")+"";
                    }
                    if (!StringUtils.isEmpty(bldId)){
                        housingSalesRoomList = housingManagementDao.getHousingSalesRoom(bldId);
                    }
                    //格式化数字
                    DecimalFormat df = new DecimalFormat(".00");
                    String BldPrice = "";
                    if (roomMap.get("BldPrice") != null) {
                        double d = Double.valueOf(roomMap.get("BldPrice") + "");
                        BldPrice = df.format(d);
                    }
                    roomVo.setBldPrice(BldPrice);
                    String XxCode = "";
                    if (roomMap.get("PermitNum") != null) {
                        XxCode = roomMap.get("PermitNum").toString();
                    }

                    roomVo.setXxCode(XxCode);

                    String IntentionID = "";
                    if (roomMap.get("OppGUID") != null) {
                        IntentionID = roomMap.get("OppGUID").toString();
                    }
                    String TradeGUID = "";
                    if (roomMap.get("TradeGUID") != null) {
                        TradeGUID = roomMap.get("TradeGUID").toString();
                    }
                    roomVo.setTradeGUID(TradeGUID);
                    String remarks = "";
                    if (roomMap.get("remarks") != null) {
                        remarks = roomMap.get("remarks").toString();
                    }
                    roomVo.setRemarks(remarks);

                    String TnPrice = "";
                    if (roomMap.get("TnPrice") != null) {
                        double d = Double.valueOf(roomMap.get("TnPrice") + "");
                        TnPrice = df.format(d);
                    }
                    roomVo.setTnPrice(TnPrice);
                    String BldArea = "";
                    if (roomMap.get("BldArea") != null) {
                        double d = Double.valueOf(roomMap.get("BldArea") + "");
                        BldArea = df.format(d);
                    }
                    roomVo.setBldArea(BldArea);
                    String HxName ="";
                    if (roomMap.get("HxName")!=null){
                        HxName = roomMap.get("HxName") + "";
                    }
                    roomVo.setHxName(HxName);
                    if(roomMap.get("PrjectFGUID")!=null){
                        roomVo.setProjectIdFq(roomMap.get("PrjectFGUID") + "");
                    }
                    if(roomMap.get("HxGUID")!=null){
                        roomVo.setHxId(roomMap.get("HxGUID") + "");
                    }
                    if(roomMap.get("calMode")!=null){
                        roomVo.setCalMode(roomMap.get("calMode") + "");
                    }
                    if(roomMap.get("areaStatus")!=null){
                        roomVo.setAreaStatus(roomMap.get("areaStatus") + "");
                    }
                    if ((roomMap.get("OrderGUID")!=null && !"".equals(roomMap.get("OrderGUID"))) || (roomMap.get("ContractGUID")!=null && !"".equals(roomMap.get("ContractGUID")))){
                        StringBuffer Zygw = new StringBuffer();
                        if (roomMap.get("Zygw")!=null && !"".equals(roomMap.get("Zygw"))){
                            String[] gws = (roomMap.get("Zygw")+"").split(";");
                            for (String gw:gws) {
                                //根据用户账号查询用户名称
                                String userName = housingManagementDao.getUserNameByUserCode(gw);
                                if(userName!=null && !"".equals(userName)){
                                    Zygw.append(userName+";");
                                }
                            }

                        }
                        if (Zygw.toString().length()>0){
                            roomVo.setZygw(Zygw.toString().substring(0,Zygw.toString().length()-1));
                        }
                        String CjBldPrice = "";
                        if (roomMap.get("CjBldPrice") != null) {
                            double d = Double.valueOf(roomMap.get("CjBldPrice") + "");
                            CjBldPrice = df.format(d);
                        }
                        roomVo.setCjBldPrice(CjBldPrice);
                        String CjTnPrice = "";
                        if (roomMap.get("CjTnPrice") != null) {
                            double d = Double.valueOf(roomMap.get("CjTnPrice") + "");
                            CjTnPrice = df.format(d);
                        }
                        roomVo.setCjTnPrice(CjTnPrice);
                        String HtTotal = "";
                        if (roomMap.get("HtTotal") != null) {
                            double d = Double.valueOf(roomMap.get("HtTotal") + "");
                            HtTotal = df.format(d);
                        }
                        roomVo.setHtTotal(HtTotal);
                        String CustomerName = "";
                        if (roomMap.get("CustomerName") != null && !"".equals(roomMap.get("CustomerName"))){
                            CustomerName = roomMap.get("CustomerName")+"";
                        }
                        roomVo.setCustomerName(CustomerName);

                        String CustomerTel = "";
                        if (roomMap.get("CustomerTel") != null && !"".equals(roomMap.get("CustomerTel"))){
                            CustomerTel = roomMap.get("CustomerTel")+"";
                        }
                        roomVo.setCustomerTel(CustomerTel);

                        String ContractDate = "";
                        if (roomMap.get("ContractGUID")!=null && !"".equals(roomMap.get("ContractGUID"))){
                            if (roomMap.get("ContractDate") != null && !"".equals(roomMap.get("ContractDate"))){
                                ContractDate = sf.format((java.sql.Timestamp)roomMap.get("ContractDate"));
                            }
                        }else{
                            if (roomMap.get("OrderDate") != null && !"".equals(roomMap.get("OrderDate"))){
                                ContractDate = sf.format((java.sql.Timestamp)roomMap.get("OrderDate"));
                            }
                        }
                        roomVo.setContractDate(ContractDate);
                    }
                    roomVo.setRoom(roomMap.get("Room") + "");
                    roomVo.setRoomGUID(roomGUID);
                    roomVo.setRoomInfo(roomMap.get("RoomInfo") + "");
                    roomVo.setRoomStru(roomMap.get("RoomStru") + "");
                    String StatusEnum = roomMap.get("StatusEnum") + "";
                    //判断房间状态
                    if ("1".equals(StatusEnum)){
                        if (housingSalesRoomList!=null && housingSalesRoomList.size()>0
                        && housingSalesRoomList.contains(roomGUID)){
                            StatusEnum = "预销控";
                        }else{
                            StatusEnum = "待售";
                        }
                    }else if ("2".equals(StatusEnum)){
                        StatusEnum = "认购";
                    }else if ("3".equals(StatusEnum)){
                        StatusEnum = "销控";
                    }else if ("4".equals(StatusEnum)){
                        StatusEnum = "签约";
                    }
                    roomVo.setStatusEnum(StatusEnum);
                    String Total = "";
                    if (roomMap.get("Total") != null) {
                        double d = Double.valueOf(roomMap.get("Total") + "");
                        Total = df.format(d);
                    }
                    roomVo.setTotal(Total);
                    String TnArea = "";
                    if (roomMap.get("TnArea") != null) {
                        double d = Double.valueOf(roomMap.get("TnArea") + "");
                        TnArea = df.format(d);
                    }
                    roomVo.setTnArea(TnArea);
                }
            }
            pubRes.setData(roomVo);
        }catch (Exception e){
            e.printStackTrace();
            pubRes.setCode(203);
            pubRes.setMessages("发生异常！！");
        }
        return pubRes;
    }

    @Override
    public ResultBody getBldByProId(HttpServletRequest request, String projectId) {
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
                return ResultBody.error(-1200001,"获取是否自销系统失败！查询有误");
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
        String roomSql = "SELECT DISTINCT BldGUID bldGUID,BldName bldName FROM vs_xk_s_building where PrjectGUID = '"+projectId+"'";
        List<Map<String, Object>> roomMaps = null;
        //判断是否自销调用
        if (flag){
            roomMaps = housingManagementDao.getDataList(roomSql);
        }else{
            roomMaps = DbTest.getObjects(roomSql);
        }
        return ResultBody.success(roomMaps);
    }

    @Override
    public ResultBody addOrEditColl(ProCollAccountVo param) {
        //校验验证码
//        if (StringUtils.isEmpty(param.getXCode())
//                || redisUtil.get(param.getBankCstMobile()) == null
//                || !param.getXCode().equals(redisUtil.get(param.getBankCstMobile()).toString())){
//            return ResultBody.error(-1200002,"验证码有误！！");
//        }
        //判断是否编辑
        if ("1".equals(param.getAddOrEdit())){
            param.setCreator(SecurityUtils.getUserId());
            //新增
            housingManagementDao.addProColl(param);
        }else{
            //查询原数据
            ProCollAccountVo old = housingManagementDao.getProColl(param.getId());
            if (old==null){
                return ResultBody.error(-1200002,"原账户未查询到！");
            }
            param.setUpdator(SecurityUtils.getUserId());
            housingManagementDao.updateProColl(param);
            param.setOldAccountType(old.getAccountType());
            param.setOldBankCstMobile(old.getBankCstMobile());
            param.setOldBankCstName(old.getBankCstName());
            param.setOldBankNo(old.getBankNo());
            param.setOldCollBank(old.getCollBank());
            param.setOldProCollAccount(old.getProCollAccount());
            param.setOldSigningTime(old.getSigningTime());
            housingManagementDao.addProCollEditLog(param);
        }
        return ResultBody.success("保存成功！！");
    }

    @Override
    public ResultBody getXcollection(Map param) {
        int pageNum = 1;
        int pageSize = 10;
        if (param.get("pageNum")!=null && !"".equals(param.get("pageNum")+"")){
            pageNum = Integer.parseInt(param.get("pageNum")+"");
        }
        if (param.get("pageSize")!=null && !"".equals(param.get("pageSize")+"")){
            pageSize = Integer.parseInt(param.get("pageSize")+"");
        }
        String[] ids = param.get("projectIds").toString().split(",");
        param.put("list",ids);
        PageHelper.startPage(pageNum,pageSize);
        List<ProCollAccountVo> proCollList = housingManagementDao.getXcollection(param);
        return ResultBody.success(new PageInfo<>(proCollList));
    }

    @Override
    public ResultBody getProCollEditLog(Map param) {
        int pageNum = 1;
        int pageSize = 10;
        String id = param.get("id")+"";
        if (param.get("pageNum")!=null && !"".equals(param.get("pageNum")+"")){
            pageNum = Integer.parseInt(param.get("pageNum")+"");
        }
        if (param.get("pageSize")!=null && !"".equals(param.get("pageSize")+"")){
            pageSize = Integer.parseInt(param.get("pageSize")+"");
        }
        PageHelper.startPage(pageNum,pageSize);
        List<ProCollAccountVo> editList = housingManagementDao.getProCollEdit(id);
        return ResultBody.success(new PageInfo<>(editList));
    }

    @Override
    public void xcollectionExport(HttpServletRequest request, HttpServletResponse response, Map param) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] ids = param.get("projectIds").toString().split(",");
        param.put("list",ids);
        try {
            //导出的数据
            List<ProCollAccountVo> proCollList = housingManagementDao.getXcollection(param);
            if (proCollList.size()>0){
                String[] headers = proCollList.get(0).getProCollTitle();
                for (int i = 0; i < proCollList.size(); i++) {
                    ProCollAccountVo activityInfoVO = proCollList.get(i);

                    activityInfoVO.setRownum((i + 1) + "");
                    Object[] oArray = activityInfoVO.toProCollData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("项目收款银行明细", headers,dataset, "项目收款银行明细", response,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultBody getRefundApproval(Map param) {
        int pageNum = 1;
        int pageSize = 10;
        String id = param.get("id")+"";
        if (param.get("pageNum")!=null && !"".equals(param.get("pageNum")+"")){
            pageNum = Integer.parseInt(param.get("pageNum")+"");
        }
        if (param.get("pageSize")!=null && !"".equals(param.get("pageSize")+"")){
            pageSize = Integer.parseInt(param.get("pageSize")+"");
        }
        String[] ids = param.get("projectIds").toString().split(",");
        param.put("list",ids);
        PageHelper.startPage(pageNum,pageSize);
        List<RefundApprovalVo> refundApprovalVos = housingManagementDao.getRefundApproval(param);
        return ResultBody.success(new PageInfo<>(refundApprovalVos));
    }

    @Override
    public void refundApprovalExport(HttpServletRequest request, HttpServletResponse response, Map param) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] ids = param.get("projectIds").toString().split(",");
        param.put("list",ids);
        try {
            //导出的数据
            List<RefundApprovalVo> reCollList = housingManagementDao.getRefundApproval(param);
            if (reCollList.size()>0){
                String[] headers = reCollList.get(0).getRefundApprovalTitle();
                for (int i = 0; i < reCollList.size(); i++) {
                    RefundApprovalVo activityInfoVO = reCollList.get(i);

                    activityInfoVO.setRownum((i + 1) + "");
                    Object[] oArray = activityInfoVO.toRefundApprovalData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("退款明细", headers,dataset, "退款明细", response,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultBody getReturnedMoney(Map param) {
        int pageNum = 1;
        int pageSize = 10;
        String id = param.get("id")+"";
        if (param.get("pageNum")!=null && !"".equals(param.get("pageNum")+"")){
            pageNum = Integer.parseInt(param.get("pageNum")+"");
        }
        if (param.get("pageSize")!=null && !"".equals(param.get("pageSize")+"")){
            pageSize = Integer.parseInt(param.get("pageSize")+"");
        }
        String[] ids = param.get("projectIds").toString().split(",");
        param.put("list",ids);
        PageHelper.startPage(pageNum,pageSize);
        List<FeeVo> feeVos = housingManagementDao.getReturnedMoney(param);
        return ResultBody.success(new PageInfo<>(feeVos));
    }

    @Override
    public void returnedMoneyExport(HttpServletRequest request, HttpServletResponse response, Map param) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        String[] ids = param.get("projectIds").toString().split(",");
        param.put("list",ids);
        try {
            //导出的数据
            List<FeeVo> feeList = housingManagementDao.getReturnedMoney(param);
            if (feeList.size()>0){
                String[] headers = feeList.get(0).getRefundApprovalTitle();
                for (int i = 0; i < feeList.size(); i++) {
                    FeeVo activityInfoVO = feeList.get(i);

                    activityInfoVO.setRownum((i + 1) + "");
                    Object[] oArray = activityInfoVO.toRefundApprovalData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("回款明细", headers,dataset, "回款明细", response,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody refundApproval(Map param) {
        try {
            String id = param.get("id") + "";
            String status = param.get("status") + "";
            // 获取 HttpServletRequest 对象
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String user_id = request.getHeader("userid");
            param.put("approver", user_id);
            RefundApprovalVo refundApprovalVo = housingManagementDao.getRefundApprovalById(id);
            if (!"1".equals(refundApprovalVo.getStatus())){
                return ResultBody.error(-120002,"此单审批状态已更新请刷新重试！");
            }
            // 通过userId查询用户名称
            // String userName = housingManagementDao.getNameByUserId(user_id);
            //判断是否驳回
            if ("3".equals(status)) {
                param.put("refundApprovalStatus", "5");
                param.put("approver", user_id);
                //更新退款申请为驳回
                housingManagementDao.updateRefundApproval(param);
                this.addRefundApprovalMessage(refundApprovalVo,param.get("rejectionReasons")==null?"":param.get("rejectionReasons")+"");
            }else{
                // 通过
                // 查询订单信息
                // 调用第三方退款接口 发起退款
                // 更新状态为退款中
                param.put("refundApprovalStatus", "2");
                param.put("approver", user_id);
                housingManagementDao.updateRefundApproval(param);
                this.addRefundApprovalMessage(refundApprovalVo,null);
//            // 退款成功更新订单状态
//            if (true){
//                // 更新机会状态  交易状态
//
//                // 插入订单节点
//
//
//            }else{
//                // 退款失败更新退款记录
//                param.put("refundApprovalStatus","4");
//                //失败原因 接口返回
//                String failureReason = "";
//                param.put("failureReason",failureReason);
//                housingManagementDao.updateRefundApproval(param);
//            }


                // 根据退款审批表的id查询交易id，再根据交易id查询订单信息
                Map orderMap = housingManagementDao.getOrderInfoById(id);
                if (null == orderMap) {
                    return ResultBody.error(-20012, "根据退款审批表的id没有查询到订单信息！");
                }
                // 根据项目id查询银行信息
                Map bankMap = housingManagementDao.getBankInfoByProjectId((String) orderMap.get("project_id"));
                if (null == bankMap) {
                    return ResultBody.error(-20012, "根据项目id没有查询到银行信息！");
                }
                // TODO 这里需要调用第三方支付接口，根据返回的状态判断是否退款
                boolean returnValue = true;
                if (returnValue) {
                    // 更新机会状态
                    this.editCheckout((String) orderMap.get("trade_id"), sf1.format(new Date()), "2");

                    // 交易状态
                    Map dataMap = new HashMap(8);
                    dataMap.put("IsClosed", "1");
                    dataMap.put("TradeStatus", "关闭");
                    dataMap.put("CloseReason", "撤销认购");
                    dataMap.put("ClueStatus", "撤销认购");
                    dataMap.put("TradeGUID", orderMap.get("trade_id"));
                    housingManagementDao.updateOppTradeVo(dataMap);

                    // 房间状态 房间状态(1:待售2:认购3:销控4:签约5：预留)
                    housingManagementDao.updateRoomStatus((String) orderMap.get("room_id"), "1", 0);

                    // 订单状态
                    Map orderMap1 = new HashMap();
                    orderMap1.put("TradeStatus", "认购");
                    orderMap1.put("status", "3");
                    orderMap1.put("closeReason", "撤销认购");
                    orderMap1.put("electronicStatus", "0");
                    orderMap1.put("payStatus", "2");
                    orderMap1.put("isLockRoom", 0);
                    orderMap1.put("tradeGuid", orderMap.get("trade_id"));
                    housingManagementDao.updateOrder(orderMap1);

                    // 插入订单节点 生成订单节点记录
                    OrderNodeRecord orderNodeRecord = this.getOrderNodeRecord(SecurityUtils.getUserId(), (String) orderMap.get("order_no"), "8", null, null);
                    housingManagementDao.saveOrderNodeRecord(orderNodeRecord);

                    // 更新银行卡信息 b_returned_money
                    Map bankInfoMap = new HashMap();
                    bankInfoMap.put("bank_no", bankMap.get("bank_no"));
                    bankInfoMap.put("coll_bank", bankMap.get("coll_bank"));
                    bankInfoMap.put("TradeGUID", orderMap.get("trade_id"));
                    bankInfoMap.put("bank_cst_mobile", bankMap.get("bank_cst_mobile"));
                    housingManagementDao.updateBankInfo(bankInfoMap);

                    // 更新实收金额根据交易id
                /*Map map1 = new HashMap();
                // 交易id
                map1.put("trade_guid", orderMap.get("trade_id"));
                // 跟新实收时间，金额
                housingManagementDao.updateReturnedMoneyByTradeGUID(map1);
*/
                    // 更新状态为已退款 b_refund_approval 退款状态（1：待审核 2：退款中 3：已退款 4：退款失败 5:驳回退款）
                    Map map = new HashMap();
                    map.put("refundApprovalStatus", "3");
                    map.put("id", id);
                    housingManagementDao.updateRefundApproval(map);
                }
                else {
                    // 退款失败更新退款记录
                    param.put("refundApprovalStatus","4");
                    //失败原因 接口返回
                    String failureReason = "";
                    param.put("failureReason",failureReason);
                    param.put("id", id);
                    housingManagementDao.updateRefundApproval(param);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResultBody.success("退款审批成功！");
    }

    /**
     * @Author wanggang
     * @Description //退房审批消息 小程序消息
     * @Date 16:40 2021/11/20
     * @Param [oppMap, reason]
     * @return void
     **/
    private void addRefundApprovalMessage(RefundApprovalVo refundApprovalVo,String reason){
        List<Map> messageList = new ArrayList<>();
        String subject = "";
        String messageType = "71112";
        String content = "";
        String isPush = "1";
        String isNeedPush = "1";
        //判断是否审批通过 reason为null即通过
        if (!StringUtils.isEmpty(reason)){
            subject = "退房审批驳回通知";
            content = "您申请的"+refundApprovalVo.getRoomName()+"房间退房审批被驳回，原因："+reason+",请知悉。";
        }else{
            subject = "退房审批通过通知";
            content = "您申请的"+refundApprovalVo.getRoomName()+"房间退房审批通过，请知悉。";
        }
        Map smassage = new HashMap();
        smassage.put("subject", subject);
        smassage.put("content", content);
        smassage.put("messageType", messageType);
        smassage.put("receiver", refundApprovalVo.getCreator());
        smassage.put("isPush", isPush);
        smassage.put("isNeedPush", isNeedPush);
        messageList.add(smassage);
        //保存消息
        if (messageList.size() > 0) {
            housingManagementDao.insertMessage(messageList);
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
            orderNodeRecord.setReason("收款商户：" + bankName + ":" + bankId);
        } else if ("4".equals(type)) {
            orderNodeRecord.setNodeType("4");
            orderNodeRecord.setNodeTypeName("支付失败");
            orderNodeRecord.setReason("收款商户：" + bankName + ":" + bankId);
        } else if ("5".equals(type)) {
            orderNodeRecord.setNodeType("5");
            orderNodeRecord.setNodeTypeName("订单完成(线上支付)");
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

    //退房辅助方法
    private String editCheckout(String TradeGUID,String time,String type) {
        //根据交易ID查询数据
        OppTradeVo oppTradeVo = housingManagementDao.getOppTradeVo(TradeGUID);
        //查询机会数据
        Map oppMap = housingManagementDao.getOppData(oppTradeVo.getOpportunityClueId());
        String ClueStatus = "";
        //原机会状态
        String oldClueStatus = oppMap.get("ClueStatus") + "";
        String oldTClueStatus = oppTradeVo.getClueStatus();
        int ss = Integer.parseInt(oldClueStatus);
        if ("认购".equals(oldTClueStatus)) {
            oldTClueStatus = "7";
        } else {
            oldTClueStatus = "8";
        }
        int tt = Integer.parseInt(oldTClueStatus);
        //判断机会状态是否需要更新
        if (ss > tt) {

        } else {
            //查询是否存在其他交易
            List<OppTradeVo> oppTradeVoList = housingManagementDao.getOldOppTradeVoList(oppTradeVo.getOpportunityClueId(), TradeGUID);
            if (oppTradeVoList != null && oppTradeVoList.size() > 0) {
                boolean flag = false;
                for (OppTradeVo oppvo : oppTradeVoList) {
                    if ("签约".equals(oppvo.getClueStatus())) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    ClueStatus = "8";
                } else {
                    ClueStatus = "7";
                }
            } else {
                //查询是否有排卡
                String cardType = housingManagementDao.getCardType(oppTradeVo.getOpportunityClueId());
                if (!StringUtils.isEmpty(cardType)) {
                    int cc = Integer.parseInt(cardType);
                    //判断是否小卡
                    if (cc == 1) {
                        ClueStatus = "3";
                    } else {
                        ClueStatus = "4";
                    }
                } else {
                    ClueStatus = "2";
                }

            }
            //更新机会状态
            housingManagementDao.updateOppTStatus(oppMap.get("ProjectClueId") + "", ClueStatus);
        }

        //插入节点记录（退认购、退签约）
        String followUpWay = "";
        if ("认购".equals(oppTradeVo.getClueStatus())){
            followUpWay = "退认购";
        }else{
            followUpWay = "退签约";
        }
        oppMap.put("followUpWay",followUpWay);
        oppMap.put("RoomName",oppTradeVo.getRoomName());
        oppMap.put("followUpDate",time);
        this.addFollowUp(oppMap);
        if ("1".equals(type)){
            //给置业顾问发消息退房审批通过
            this.addChackoutMessage(oppMap,null);
        }
        return "1";
    }

    /**
     * @Author wanggang
     * @Description //退房审批消息
     * @Date 16:40 2021/11/20
     * @Param [oppMap, reason]
     * @return void
     **/
    private void addChackoutMessage(Map oppMap,String reason){
        List<Map> messageList = new ArrayList<>();
        String subject = "";
        String messageType = "2301";
        String content = "";
        String isPush = "1";
        String isNeedPush = "1";
        //判断是否审批通过 reason为null即通过
        if (reason!=null){
            subject = "退房审批驳回通知";
            content = "您申请的"+oppMap.get("RoomName")+"房间退房审批被驳回，原因："+reason+",请知悉。";
        }else{
            subject = "退房审批通过通知";
            content = "您申请的"+oppMap.get("RoomName")+"房间退房审批通过，请知悉。";
        }
        Map smassage = new HashMap();
        smassage.put("subject", subject);
        smassage.put("content", content);
        smassage.put("messageType", messageType);
        smassage.put("receiver", oppMap.get("SalesAttributionId"));
        smassage.put("isPush", isPush);
        smassage.put("isNeedPush", isNeedPush);
        smassage.put("projectClueId", oppMap.get("ProjectClueId"));
        messageList.add(smassage);
        //保存消息
        if (messageList.size() > 0) {
            housingManagementDao.insertMessage(messageList);
        }
    }

    @Override
    public ResultBody returnedMoney(Map param) {
        String id = param.get("id")+"";
        //查询交易数据
        Map tradeMap = housingManagementDao.getTradeMapByRemId(id);
        //更新回款为已支付
        housingManagementDao.updateReturnedMoney(id);
        //更新机会状态为认购
        String ProjectClueId = tradeMap.get("ProjectClueId")+"";
        housingManagementDao.updateOppStatus(ProjectClueId);
        //更新订单为完成
        String orderNo = tradeMap.get("orderNo")+"";
        housingManagementDao.updateXorder(orderNo);
        //插入订单节点(已支付，订单完成)
        Map node = new HashMap();
        node.put("orderNo",orderNo);
        node.put("node_type","3");
        node.put("node_type_name","支付成功");
        node.put("creator",SecurityUtils.getUserId());
        housingManagementDao.addOrderNode(node);
        node.put("node_type","5");
        node.put("node_type_name","订单完成");
        housingManagementDao.addOrderNode(node);
        //更新房间状态 为认购
        housingManagementDao.updateRoomStatus(tradeMap.get("RoomID")+"","2", 0);
        //删除销控数据
        housingManagementDao.updateHousingSalesControl(tradeMap.get("RoomID")+"");
        //发送消息
        this.addMessage(tradeMap);
        //插入节点记录
        String followUpWay = "认购";
        tradeMap.put("followUpWay",followUpWay);
        tradeMap.put("followUpDate",new Date());
        this.addFollowUp(tradeMap);
        return ResultBody.success("确认支付成功！");
    }

    /**
     * 保存消息
     */
    public void addMessage(Map oppMap) {
        List<Map> messageList = new ArrayList<>();
        String tradeDate = "";
        String subject = "";
        String tradeType = "";
        String messageType = "";
        Map smessage = new HashMap();
        smessage.put("ProjectClueId", oppMap.get("ProjectClueId"));
        List<Map> messageOld = housingManagementDao.selectMessageALL(smessage);

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
        subject = "认购通知";
        tradeType = "认购";
        tradeDate = sf.format(new Date());
        //置业顾问消息
        if (!isOrder) {
            messageType = "2111";
            Map smassage = new HashMap();
            smassage.put("subject", subject);
            smassage.put("content", "客户【" + oppMap.get("CustomerName") + "】已完成" + tradeType + " 房间号：" + oppMap.get("RoomName") + " " + tradeType + "日期：" + tradeDate + " 请知悉。");
            smassage.put("messageType", messageType);
            smassage.put("receiver", oppMap.get("SalesAttributionId"));
            smassage.put("isPush", isPush);
            smassage.put("isNeedPush", isNeedPush);
            smassage.put("projectClueId", oppMap.get("ProjectClueId"));
            messageList.add(smassage);
        }
        //销售经理消息
        if (!isOrder4) {
            List<Map> xsjl = housingManagementDao.findXSJL(oppMap.get("projectId") + "");
            if (xsjl != null && xsjl.size() > 0) {
                messageType = "21114";
                for (Map map : xsjl) {
                    Map smassage = new HashMap();
                    smassage.put("subject", subject);
                    smassage.put("content", "客户【" + oppMap.get("CustomerName") + "】已完成" + tradeType + " 房间号：" + oppMap.get("RoomName") + " " + tradeType + "日期：" + tradeDate + " 请知悉。");
                    smassage.put("messageType", messageType);
                    smassage.put("receiver", map.get("id"));
                    smassage.put("isPush", isPush);
                    smassage.put("isNeedPush", isNeedPush);
                    smassage.put("projectClueId", oppMap.get("ProjectClueId"));
                    messageList.add(smassage);
                }
            }
        }
        //保存消息
        if (messageList.size() > 0) {
            housingManagementDao.insertMessage(messageList);
        }

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
            housingManagementDao.savaFollowupRecord(nodeMap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 签约协议模板列表查询
     *
     * @param orderTemplate
     * @return
     */
    @Override
    public ResultBody selectSignProtocolTemplates(OrderTemplate orderTemplate) {
        if (null == orderTemplate) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "签约协议模板列表查询请求参数为空");
        }
        PageHelper.startPage(orderTemplate.getPageIndex(), orderTemplate.getPageSize());

        // 判断是否传入项目id
        if (StringUtils.isNotEmpty(orderTemplate.getProjectId())) {
            String[] strings = orderTemplate.getProjectId().split(",");
            if (strings.length > 1) {
                StringBuilder builder = new StringBuilder("(");
                for (int i = 0; i < strings.length; i++) {
                    builder.append("'" + strings[i] + "',");
                }
                String s = builder.toString();
                System.out.println(s.substring(0, s.length() - 1));
                // 去掉最后的逗号
                String s1 = s.substring(0, s.length() - 1) + ")";

                // 重新把项目id复制给projectId
                orderTemplate.setProjectId(s1);
            }
            else {
                orderTemplate.setProjectId("('" + orderTemplate.getProjectId() + "')");
            }
        }
        // 签约协议模板列表查询
        return ResultBody.success(new PageInfo<>(housingManagementDao.selectSignProtocolTemplates(orderTemplate)));
    }

    /**
     * 签约协议模板列表启用或者禁用
     *
     * @param id
     * @param statusFlag 0禁用 1启用
     * @return
     */
    @Override
    public ResultBody updateSignProtocolTemplatesStatus(Integer id, Integer statusFlag) {
        if (null == id) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "请求参数模板id为空");
        }
        int isSucceed = 0;
        if (statusFlag == 0) {
            isSucceed = housingManagementDao.updateSignProtocolTemplatesStatus(id, 0);
            return isSucceed > 0 ? ResultBody.success("禁用成功") : ResultBody.success("禁用失败");
        }
        else if (statusFlag == 1) {
            isSucceed = housingManagementDao.updateSignProtocolTemplatesStatus(id, 1);
        }
        return isSucceed > 0 ? ResultBody.success("启用成功") : ResultBody.success("启用失败");
    }

    /**
     * 保存签约模板
     *
     * @param orderTemplate
     * @param request
     * @return
     */
    @Override
    public ResultBody saveOrderTemplate(OrderTemplate orderTemplate, HttpServletRequest request) {
        if (null == orderTemplate) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "保存签约模板请求参数为空");
        }
        orderTemplate.setCreator(request.getHeader("username"));
        // 查询最大的主键id
        Integer manCount = housingManagementDao.getMaxId();

        // 根据是否传入主键id进行更新
        if (orderTemplate.getId() != null) {
            this.updateOrderTemplate(orderTemplate, request);
        }
        else {
            // 判断是否已经存在签约模板通过项目id
            Integer integer = housingManagementDao.getCountByProjectId(orderTemplate.getProjectId());
            if (integer > 0) {
                return ResultBody.error(-200001, "项目模板已经存在，请重新选择！");
            }
            // 保存新增签约模板
            orderTemplate.setId(manCount + 1);
            housingManagementDao.saveOrderTemplate(orderTemplate);
        }

        return  ResultBody.success(orderTemplate);
    }

    /**
     * 查询签约模板详情通过id
     *
     * @param id
     * @return
     */
    @Override
    public ResultBody getOrderTemplate(Integer id) {
        if (null == id) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "请求参数模板id为空");
        }
        OrderTemplate orderTemplate = housingManagementDao.getOrderTemplate(id);
        if (null != orderTemplate && org.apache.commons.lang3.StringUtils.isNotEmpty(orderTemplate.getSubTemplateUrl())) {
            String[] strings = orderTemplate.getSubTemplateUrl().split(",");
            System.out.println("路径集合为：" + Arrays.asList(strings));
            orderTemplate.setSubTemplateUrlList(Arrays.asList(strings));
        }

        return ResultBody.success(orderTemplate);
    }

    /**
     * 更新签约模板通过id
     *
     * @param orderTemplate
     * @param request
     * @return
     */
    @Override
    public ResultBody updateOrderTemplate(OrderTemplate orderTemplate, HttpServletRequest request) {
        if (null == orderTemplate) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "保存签约模板请求参数为空");
        }
        orderTemplate.setUpdator(request.getHeader("username"));
        return housingManagementDao.updateOrderTemplate(orderTemplate) > 0
                ? ResultBody.success("更新签约模板成功") : ResultBody.success("更新签约模板失败");
    }

    /**
     * 集团签署认购协议文件列表
     *
     * @return
     */
    @Override
    public ResultBody selectGroupOrderTemplate() {
        return ResultBody.success(housingManagementDao.selectGroupOrderTemplate());
    }

    @Override
    public ResultBody getXcode(Map param) {
        String mobile = "";
        if (param==null || param.get("mobile")==null || "".equals(param.get("mobile")+"")){
            return ResultBody.error(-1000200,"手机号未传！");
        }
        mobile = param.get("mobile")+"";
        try {
            String verificationCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
            Map map = new HashMap(2);
            map.put("mobile", mobile);
            map.put("code", verificationCode);
            JSONObject result = JSONObject.parseObject(HttpClientUtil.doPost(codeUrl, JSONObject.toJSONString(map)));
            //把验证码放在redis做校验  并存入数据库做记录
            redisUtil.set(mobile, verificationCode, 600);
            System.out.println("验证码" + redisUtil.get(mobile));
            return ResultBody.success("验证码获取成功");
        } catch (Exception e) {
            return ResultBody.error(-1000200,"验证码获取失败！");
        }
    }

    @Override
    public PageInfo<DealCstData> getDealCstData(DealCstForm dealCstForm) {
        // 分页数据设置
        PageHelper.startPage((int) dealCstForm.getCurrent(), (int) dealCstForm.getSize());
        List<DealCstData> list = housingManagementDao.getDealCstList(dealCstForm);
        return new PageInfo<DealCstData>(list);
    }

    @Override
    public void dealCstExport(HttpServletRequest request, HttpServletResponse response, String params) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        DealCstForm dealCstForm = JSONObject.parseObject(params,DealCstForm.class);
        try {
            //查询数据
            List<DealCstData> list = housingManagementDao.getDealCstList(dealCstForm);
            if (list.size()>0){
                String[] headers = list.get(0).getDealCstTitle();
                for (int i = 0; i < list.size(); i++) {
                    DealCstData activityInfoVO = list.get(i);
                    Object[] oArray = activityInfoVO.toDealCstData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("成交客户表", headers,dataset, "成交客户表", response,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dealRelationCstExport(HttpServletRequest request, HttpServletResponse response, String param) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        DealCstForm dealCstForm = JSONObject.parseObject(param,DealCstForm.class);
        try {
            //查询主客户数据
            List<String> mainIds = housingManagementDao.getDealCstIds(dealCstForm);
            if (mainIds!=null && mainIds.size()>0){
                //查询关联客户数据
                List<DealCstData> list = housingManagementDao.getDealRelationCst(mainIds);
                if (list.size()>0){
                    String[] headers = list.get(0).getDealCstTitle();
                    for (int i = 0; i < list.size(); i++) {
                        DealCstData activityInfoVO = list.get(i);
                        Object[] oArray = activityInfoVO.toDealCstData();
                        dataset.add(oArray);
                    }
                    ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                    excelExportUtil.exportExcel("成交关联客户表", headers,dataset, "成交关联客户表", response,null);
                }else{
                    int i = 10/0;
                }
            }else{
                int i = 10/0;
            }
        } catch (Exception e) {
            throw new RuntimeException("无联名客户信息！");
        }
    }
}
