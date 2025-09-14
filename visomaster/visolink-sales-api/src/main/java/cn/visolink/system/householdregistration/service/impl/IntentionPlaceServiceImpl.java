package cn.visolink.system.householdregistration.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.domain.MenuResult;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.message.model.form.MessageForm;
import cn.visolink.system.householdregistration.dao.IntentionPlaceDao;
import cn.visolink.system.householdregistration.model.*;
import cn.visolink.system.householdregistration.model.form.IntentionPlaceForm;
import cn.visolink.system.householdregistration.model.vo.IntentionPlaceVO;
import cn.visolink.system.householdregistration.service.IntentionPlaceService;
import cn.visolink.system.projectmanager.dao.HousingManagementDao;
import cn.visolink.utils.*;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * IntentionPlace服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2020-07-29
 */
@Service
public class IntentionPlaceServiceImpl implements IntentionPlaceService {


    @Autowired
    private IntentionPlaceDao intentionPlaceDao;
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private HousingManagementDao housingManagementDao;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${IMAGE_ACTIVITY_URL}")
    private String path;

    @Value("${AppMenuUrl}")
    private String appMenuUrl;

    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    @Override
    public PageInfo<IntentionPlaceVO> selectPage(IntentionPlaceForm param) {
        int pageSize = Integer.valueOf(param.getSize() + "");
        int pageIndex = Integer.valueOf(param.getCurrent() + "");
        PageHelper.startPage(pageIndex, pageSize);
        List<IntentionPlaceVO> list = intentionPlaceDao.getIntentionPlacePage(param);
        for (IntentionPlaceVO intentionPlaceVO : list) {
            int count = this.getNeedIntentionPlace(intentionPlaceVO);
            intentionPlaceVO.setNeedPlaceCount(count + "");
        }
        return new PageInfo<IntentionPlaceVO>(list);
    }

    //获取待装户数量
    public int getNeedIntentionPlace(IntentionPlaceVO intentionPlaceVO) {
        //明源排卡数
//        List<Map<String, Object>> rowCardNumList = new ArrayList<>();
//        int count = 0;
//        //判断是否到访以后
//        if ("1".equals(intentionPlaceVO.getCustomerType())) {
//            //获取到访后的机会数
//            count = intentionPlaceDao.getAllCount(intentionPlaceVO);
//            //查询排卡数（过滤掉只有1张卡的数据）
//            rowCardNumList = getRowCardNum(intentionPlaceVO.getId(), intentionPlaceVO.getProjectid(), null, intentionPlaceVO.getActivityEndtime());
//        } else {
//            //查询排卡数（过滤掉只有1张卡的数据）
//            rowCardNumList = getRowCardNum(intentionPlaceVO.getId(), intentionPlaceVO.getProjectid(), intentionPlaceVO.getCardType(), intentionPlaceVO.getActivityEndtime());
//            //查询排卡范围
//            String[] cardTypes = intentionPlaceVO.getCardType().split(",");
//            //声明明源机会ID集合
//            List<String> intentionIds = new ArrayList<>();
//            if (cardTypes.length == 1) {
//                //如果是小卡
//                if ("1".equals(cardTypes[0])) {
//                    List<String> proIds = intentionPlaceDao.getSmallCard(intentionPlaceVO.getId(), intentionPlaceVO.getProjectid());
//                    if (proIds != null && proIds.size() > 0) {
//                        String proId = "'" + StringUtils.join(proIds.toArray(), "','") + "'";
//                        String sql = "select DISTINCT OppGUID from VS_XK_Booking2Prefix where status = '激活' and kpstate = '小卡' and p_projectfId in (" + proId + ") and CreatedTime < '" + intentionPlaceVO.getActivityEndtime() + "'";
//                        List<Map<String, Object>> ids = DbTest.getObjects(sql);
//                        for (Map<String, Object> mapMy : ids) {
//                            intentionIds.add(mapMy.get("OppGUID") + "");
//                        }
//                    }
//                } else {
//                    StringBuffer sb = new StringBuffer();
//                    String sql = "select DISTINCT OppGUID from VS_XK_Booking2Prefix where status = '激活' and kpstate = '大卡'";
//                    sb.append(sql);
//                    //如果是大卡
//                    List<Map> dMap = intentionPlaceDao.getBigCard(intentionPlaceVO.getId(), intentionPlaceVO.getProjectid());
//                    if (dMap != null && dMap.size() > 0) {
//                        sb.append(" and (");
//                        for (int i = 0; i < dMap.size(); i++) {
//                            if (i == dMap.size() - 1) {
//                                if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
//                                    sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "')");
//                                } else {
//                                    sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "')");
//                                }
//                            } else {
//                                if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
//                                    sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "') or ");
//                                } else {
//                                    sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "') or ");
//                                }
//                            }
//
//                        }
//                        sb.append(") and CreatedTime < '" + intentionPlaceVO.getActivityEndtime() + "'");
//                        List<Map<String, Object>> ids = DbTest.getObjects(sb.toString());
//                        for (Map<String, Object> mapMy : ids) {
//                            intentionIds.add(mapMy.get("OppGUID") + "");
//                        }
//                    }
//                }
//            } else {
//                //查询明源机会ID
//                //查询小卡范围
//                List<String> proIds = intentionPlaceDao.getSmallCard(intentionPlaceVO.getId(), intentionPlaceVO.getProjectid());
//                //查询大卡范围
//                List<Map> dMap = intentionPlaceDao.getBigCard(intentionPlaceVO.getId(), intentionPlaceVO.getProjectid());
//                StringBuffer sb = new StringBuffer();
//                String sql = "select DISTINCT OppGUID from VS_XK_Booking2Prefix where status = '激活'";
//                sb.append(sql);
//                sb.append(" and (");
//                if (proIds != null && proIds.size() > 0) {
//                    String proId = "'" + StringUtils.join(proIds.toArray(), "','") + "'";
//                    if (dMap != null && dMap.size() > 0) {
//                        sb.append("(kpstate = '小卡' and p_projectfId in (" + proId + ")) or ");
//                    } else {
//                        sb.append("(kpstate = '小卡' and p_projectfId in (" + proId + "))");
//                    }
//                }
//                if (dMap != null && dMap.size() > 0) {
//                    for (int i = 0; i < dMap.size(); i++) {
//                        if (i == dMap.size() - 1) {
//                            if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
//                                sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "')");
//                            } else {
//                                sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "')");
//                            }
//                        } else {
//                            if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
//                                sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "') or ");
//                            } else {
//                                sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "') or ");
//                            }
//                        }
//
//                    }
//                }
//                sb.append(") and CreatedTime < '" + intentionPlaceVO.getActivityEndtime() + "'");
//                List<Map<String, Object>> ids = DbTest.getObjects(sb.toString());
//                for (Map<String, Object> mapMy : ids) {
//                    intentionIds.add(mapMy.get("OppGUID") + "");
//                }
//            }
//            //查询待装户客户数（存在置业顾问的）
//            if (intentionIds.size() > 0) {
//                CardOppID cardOppID = new CardOppID();
//                cardOppID.setProjectId(intentionPlaceVO.getProjectid());
//                cardOppID.setIntentionIDs(intentionIds);
//                count = intentionPlaceDao.getCardOpp(cardOppID);
//            }
//        }
//        if (rowCardNumList.size() > 0) {
//            for (Map map1 : rowCardNumList) {
//                count += Integer.parseInt(String.valueOf(map1.get("counts")));
//            }
//        return count;
        return 10;
    }

    /***
     *
     * @param activityId
     * @param projectId
     * @param cardType
     *@return {}
     *@throws
     *@Description: 查询明源排卡机会id
     *@author FuYong
     *@date 2020/8/11 17:00
     */
    public List<Map<String,Object>> getRowCardNum(String activityId,String projectId,String cardType,String activityEndTime){
        List<Map<String,Object>> resultList = new ArrayList<>();
            List<String> proIds = intentionPlaceDao.getFqIdByProId(projectId);
            if (proIds != null && proIds.size() > 0) {
                String proId = "'" + StringUtils.join(proIds.toArray(), "','") + "'";
                String sql = "select OppGUID,(count(OppGUID) - 1) as counts from VS_XK_Booking2Prefix where status = '激活' and CreatedTime < '" + activityEndTime + "' and p_projectfId in (" + proId + ") group by OppGUID HAVING  (count(OppGUID) - 1) > 0";
                resultList = DbTest.getObjects(sql);
            }
else {
            String[] cardTypes = cardType.split(",");
            if (cardTypes.length == 1) {
                //如果是小卡
                if ("1".equals(cardTypes[0])) {
                    proIds = intentionPlaceDao.getSmallCard(activityId, projectId);
                    if (proIds != null && proIds.size() > 0) {
                        String proId = "'" + StringUtils.join(proIds.toArray(), "','") + "'";
                        String sql = "select OppGUID,(count(OppGUID) - 1) as counts from VS_XK_Booking2Prefix where status = '激活' and CreatedTime < '" + activityEndTime + "' and kpstate = '小卡' and p_projectfId in (" + proId + ") group by OppGUID HAVING  (count(OppGUID) - 1) > 0";
                        resultList = DbTest.getObjects(sql);
                    }
                } else {
                    StringBuffer sb = new StringBuffer();
                    String sql = "select OppGUID,(count(OppGUID) - 1) as counts from VS_XK_Booking2Prefix where status = '激活' and CreatedTime < '" + activityEndTime + "' and kpstate = '大卡'";
                    sb.append(sql);
                    //如果是大卡
                    List<Map> dMap = intentionPlaceDao.getBigCard(activityId, projectId);
                    if (dMap != null && dMap.size() > 0) {
                        sb.append(" and (");
                        for (int i = 0; i < dMap.size(); i++) {
                            if (i == dMap.size() - 1) {
                                if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
                                    sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "')");
                                } else {
                                    sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "')");
                                }
                            } else {
                                if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
                                    sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "') or ");
                                } else {
                                    sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "') or ");
                                }
                            }

                        }
                        sb.append(")");
                        sb.append(" group by OppGUID HAVING  (count(OppGUID) - 1) > 0");
                        resultList = DbTest.getObjects(sb.toString());
                    }
                }
            } else {
                //查询明源机会ID
                //查询小卡范围
              proIds = intentionPlaceDao.getSmallCard(activityId, projectId);
                //查询大卡范围
                List<Map> dMap = intentionPlaceDao.getBigCard(activityId, projectId);
                StringBuffer sb = new StringBuffer();
                String sql = "select OppGUID,(count(OppGUID) - 1) as counts from VS_XK_Booking2Prefix where status = '激活' and CreatedTime < '" + activityEndTime + "'";
                sb.append(sql);
                if ((proIds != null && proIds.size() > 0) || (dMap != null && dMap.size() > 0)) {
                    sb.append(" and (");
                    if (proIds != null && proIds.size() > 0) {
                        String proId = "'" + StringUtils.join(proIds.toArray(), "','") + "'";
                        if (dMap != null && dMap.size() > 0) {
                            sb.append("(kpstate = '小卡' and p_projectfId in (" + proId + ")) or ");
                        } else {
                            sb.append("(kpstate = '小卡' and p_projectfId in (" + proId + "))");
                        }
                    }
                    if (dMap != null && dMap.size() > 0) {
                        for (int i = 0; i < dMap.size(); i++) {
                            if (i == dMap.size() - 1) {
                                if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
                                    sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "')");
                                } else {
                                    sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "')");
                                }
                            } else {
                                if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
                                    sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "') or ");
                                } else {
                                    sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "') or ");
                                }
                            }

                        }
                    }
                    sb.append(")");
                }
                sb.append(" group by OppGUID HAVING  (count(OppGUID) - 1) > 0");
                resultList = DbTest.getObjects(sb.toString());
            }
        }
        return resultList;
    }


    @Override
    public void intentionPlaceExport(HttpServletRequest request, HttpServletResponse response, String param) {
        IntentionPlaceForm placeForm = JSONObject.parseObject(param, IntentionPlaceForm.class);
        List<IntentionPlaceVO> list = intentionPlaceDao.getIntentionPlacePage(placeForm);
        if (list != null && list.size() > 0) {
            String[] headers = list.get(0).getActivityTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();
            int num = 0;
            for (IntentionPlaceVO ac : list) {
                int count = this.getNeedIntentionPlace(ac);
                ac.setNeedPlaceCount(count + "");
                num++;
                ac.setRownum(num + "");
                Object[] oArray = ac.toActivityData();
                dataset.add(oArray);
            }
            try {
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("装户活动明细", headers, dataset, "装户活动明细", response, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public PageInfo<IntentionPlaceResult> getIntentionPlaceResultPage(IntentionPlaceResult param) {
        int pageSize = Integer.valueOf(param.getSize() + "");
        int pageIndex = Integer.valueOf(param.getCurrent() + "");
        PageHelper.startPage(pageIndex, pageSize);
        List<IntentionPlaceResult> list = new ArrayList<>();
        //判断活动类型(1:旭客家 2：置业顾问)
        if ("1".equals(param.getLoadingType())) {
            list = intentionPlaceDao.getIntentionPlaceResultPage(param);
        } else {
            list = intentionPlaceDao.getIntentionPlaceResultPage2(param);
        }

        return new PageInfo<IntentionPlaceResult>(list);
    }

    @Override
    public void intentionPlaceResultExport(HttpServletRequest request, HttpServletResponse response, String param) {
        IntentionPlaceResult placeForm = JSONObject.parseObject(param, IntentionPlaceResult.class);
        List<IntentionPlaceResult> list = new ArrayList<>();
        //判断活动类型(1:旭客家 2：置业顾问)
        if ("1".equals(placeForm.getLoadingType())) {
            list = intentionPlaceDao.getIntentionPlaceResultPage(placeForm);
        } else {
            list = intentionPlaceDao.getIntentionPlaceResultPage2(placeForm);
        }
        if (list != null && list.size() > 0) {
            String[] headers = list.get(0).getActivityResultTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();
            int num = 0;
            for (IntentionPlaceResult ac : list) {
                num++;
                ac.setRownum(num);
                Object[] oArray = ac.toActivityResultData(placeForm.getIsAll());
                dataset.add(oArray);
            }
            try {
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("装户结果明细", headers, dataset, "装户结果明细", response, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateIntentionPlaceIsShow(Map map) {

        try {
            String editor = SecurityUtils.getUserId();
            map.put("editor", editor);
            intentionPlaceDao.updateIntentionPlaceResult(map);
            return ResultBody.success("更新成功！！");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003, "更新异常！！");
        }
    }

    @Override
    public ResultBody updateEditResult(Map map) {
        List<IntentionPlaceRoom> list = JSONObject.parseArray(JSONObject.toJSONString(map.get("roomList")), IntentionPlaceRoom.class);
        String editor = SecurityUtils.getUserId();
        if (list != null) {
            //批量更新时需处理数据
            if (list != null && list.size() > 1) {
                for (IntentionPlaceRoom room : list) {
                    room.setEditor(editor);
                }
            } else if (list != null && list.size() > 0) {
                list.get(0).setEditor(editor);
            } else {
                return ResultBody.error(-21_0003, "更新异常,参数未传！");
            }
            intentionPlaceDao.updateEditResultSome(list);
            return ResultBody.success("更新成功！！");
        } else {
            return ResultBody.error(-21_0003, "更新异常,参数未传！");
        }
    }

    @Override
    public ResultBody selectMoveRoomResult(Map map) {
        if (map.get("edition") == null || "".equals(map.get("edition") + "")) {
            return ResultBody.error(-12300, "版本未传！");
        }
        String edition = map.get("edition") + "";
        String activityId = map.get("activityId") + "";
        List<String> buildguids = new ArrayList<>();
        if (map.get("buildIds") != null && !"".equals(map.get("buildIds") + "")) {
            String[] ids = map.get("buildIds").toString().split(",");
            buildguids = Arrays.asList(ids);
        } else {
            return ResultBody.error(-12300, "楼栋ID未传！");
        }
        List<IntentionPlaceBuild> builds = new ArrayList<>();
        Map param = new HashMap();
        param.put("id", activityId);
        param.put("buildguid", "");
        param.put("list", buildguids);
        builds = intentionPlaceDao.getIntentionPlaceBuilds(param);
        List<BldUnit> units = new ArrayList<>();
        units = intentionPlaceDao.getIntentionPlaceUnit(param);
        List<IntentionPlaceRoom> roomList = new ArrayList<>();
        param.put("edition", edition);
        roomList = intentionPlaceDao.selectMoveRoomResult(param);
        if (builds.size() > 0) {
            if (units.size() > 0) {
                for (IntentionPlaceBuild build : builds) {
                    List<BldUnit> units1 = new ArrayList<>();
                    for (BldUnit unit : units) {
                        if (unit.getBuildguid().equals(build.getBuildguid())) {
                            List<IntentionPlaceRoom> roomList1 = new ArrayList<>();
                            for (IntentionPlaceRoom room : roomList) {
                                if (room.getUnitno().equals(unit.getUnitno()) && room.getBuildguid().equals(unit.getBuildguid())) {
                                    roomList1.add(room);
                                }
                            }
                            unit.setRoomList(roomList1);
                            units1.add(unit);
                        }
                    }
                    build.setUnitList(units1);
                }
            }

        }
        return ResultBody.success(builds);
    }

    @Override
    public ResultBody selectRoomResult(Map map) {
        String activityId = map.get("id") + "";
        String buildguid = "";
        if (map.get("buildguid") != null) {
            buildguid = map.get("buildguid") + "";
        }
        List<IntentionPlaceBuild> builds = new ArrayList<>();
        Map param = new HashMap();
        param.put("id", activityId);
        param.put("buildguid", buildguid);
        builds = intentionPlaceDao.getIntentionPlaceBuild(param);
        List<BldUnit> units = new ArrayList<>();
        units = intentionPlaceDao.getIntentionPlaceUnit(param);
        List<IntentionPlaceRoom> roomList = new ArrayList<>();
        roomList = intentionPlaceDao.selectRoomResult(activityId, buildguid);
        if (builds.size() > 0) {
            if (units.size() > 0) {
                for (IntentionPlaceBuild build : builds) {
                    List<BldUnit> units1 = new ArrayList<>();
                    for (BldUnit unit : units) {
                        if (unit.getBuildguid().equals(build.getBuildguid())) {
                            List<IntentionPlaceRoom> roomList1 = new ArrayList<>();
                            for (IntentionPlaceRoom room : roomList) {
                                if (room.getUnitno().equals(unit.getUnitno()) && room.getBuildguid().equals(unit.getBuildguid())) {
                                    roomList1.add(room);
                                }
                            }
                            unit.setRoomList(roomList1);
                            units1.add(unit);
                        }
                    }
                    build.setUnitList(units1);
                }
            }

        }
        return ResultBody.success(builds);
    }

    @Override
    public ResultBody getRoomResultDetail(Map map) {
         List<Map> roomResultDetail = intentionPlaceDao.getRoomResultDetail(map);
        return ResultBody.success(roomResultDetail);
    }

    @Override
    public ResultBody selectActivityBuild(Map map) {
        return ResultBody.success(intentionPlaceDao.getActivityBuild(map.get("id") + ""));
    }

    @Override
    public ResultBody selectActivityPhoto(Map map) {
            Map result = new HashMap();
            String materialAddress = intentionPlaceDao.selectActivityPhoto(map.get("id")+"");
            List<Map> buildSites = intentionPlaceDao.getBuildSite(map.get("id")+"");
            result.put("materialAddress",materialAddress);
            result.put("buildSites",buildSites);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody getActivityBuildResult(Map map) {
        return ResultBody.success(intentionPlaceDao.getActivityBuildResult(map.get("id") + ""));
    }

    @Override
    public PageInfo<EditRecord> getEditRecordList(Map map) {
        int pageSize = Integer.valueOf(map.get("pageSize") + "");
        int pageIndex = Integer.valueOf(map.get("pageIndex") + "");
        PageHelper.startPage(pageIndex, pageSize);
        List<EditRecord> list = intentionPlaceDao.getEditList(map);
        return new PageInfo<EditRecord>(list);
    }

    @Override
    public void editRecordExport(HttpServletRequest request, HttpServletResponse response, String param) {
        Map map = JSONObject.parseObject(param, Map.class);
        List<EditRecord> list = intentionPlaceDao.getEditList(map);
        if (list != null && list.size() > 0) {
            String[] headers = list.get(0).getActivityTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();
            int num = 0;
            for (EditRecord ac : list) {
                num++;
                ac.setRownum(num + "");
                Object[] oArray = ac.toActivityData(map.get("isAll") + "");
                dataset.add(oArray);
            }
            try {
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("装户调整明细", headers, dataset, "装户调整明细", response, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Map> getProActivitys(Map map) {
        return intentionPlaceDao.getProActivitys(map);
    }


    @Override
    public ResultBody getIntentionPlaceDetail(Map map) {
        if (map == null || map.get("id") == null) {
            return ResultBody.error(-21_0006, "必传参数未传！！");
        }
        //查询装户详情及素材所选楼栋,如选择了排卡则返回排卡分组
        IntentionPlaceForm intentionPlaceForm = intentionPlaceDao.getIntentionPlaceDetail(map.get("id") + "");
        List<IntentionPlaceMaterial> intentionPlaceMaterials = intentionPlaceDao.getIntentionPlaceMaterial(map.get("id") + "");
        intentionPlaceForm.setMaterialList(intentionPlaceMaterials);
        List<Map> builds = intentionPlaceDao.getBldList(map.get("id") + "");
        List<Map> buildSites = intentionPlaceDao.getBuildSite(map.get("id") + "");
        intentionPlaceForm.setBuilds(builds);
        intentionPlaceForm.setBuildSites(buildSites);
        //查询是否选择了小卡分期
        List<String> fprojectList = intentionPlaceDao.getIntentionPlaceCardGroupX(map.get("id") + "");
        if (fprojectList != null && fprojectList.size() > 0) {
            intentionPlaceForm.setFprojectList(fprojectList);
        }
        List<IntentionPlaceBuild> buildList = this.getOldRoomList(map.get("id") + "", null);
        intentionPlaceForm.setBuildList(buildList);
        //查询是否选择了大卡分期
        List<ProBatchVO> batchList = intentionPlaceDao.getIntentionPlaceCardGroupDF(map.get("id") + "");
        if (batchList != null && batchList.size() > 0) {
            //获取所有开盘批次排卡分组
            List<Map> allBatch = this.getFProjectCardGroup(intentionPlaceForm.getProjectid());
            List<ProBatchVO> batchListAll = JSONObject.parseArray(JSONObject.toJSONString(allBatch), ProBatchVO.class);
            for (ProBatchVO vo : batchListAll) {
                for (ProBatchVO pro : batchList) {
                    if (vo.getValue().equals(pro.getValue())) {
                        vo.setIsChecked("1");
                    }
                }
                if (null != vo.getChildren() && vo.getChildren().size() > 0) {
                    List<ProBatchVO> batchsList = intentionPlaceDao.getIntentionPlaceCardGroupDP(map.get("id") + "", vo.getValue());
                    if (batchsList != null && batchsList.size() > 0) {
                        for (ProBatchVO batch : vo.getChildren()) {
                            for (ProBatchVO batchIs : batchsList) {
                                if (batchIs.getValue().equals(batch.getValue())) {
                                    batch.setIsChecked("1");
                                }
                            }
                            List<ProBatchVO> cardList = intentionPlaceDao.getIntentionPlaceCardGroupDZ(map.get("id") + "", vo.getValue(), batch.getValue());
                            if (cardList != null && cardList.size() > 0) {
                                for (ProBatchVO card : batch.getChildren()) {
                                    for (ProBatchVO card1 : cardList) {
                                        if (card.getValue().equals(card1.getValue())) {
                                            card.setIsChecked("1");
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
//                        for (ProBatchVO re:batchList) {
//                                List<ProBatchVO> batchsList =  intentionPlaceDao.getIntentionPlaceCardGroupDP(map.get("id")+"",re.getValue());
//                                if (batchsList!=null && batchsList.size()>0){
//                                        for (ProBatchVO res:batchsList) {
//                                                List<ProBatchVO> cardList =  intentionPlaceDao.getIntentionPlaceCardGroupDZ(map.get("id")+"",re.getValue(),res.getValue());
//                                                if (cardList!=null && cardList.size()>0){
//                                                        res.setChildren(cardList);
//                                                }
//                                        }
//                                        re.setChildren(batchsList);
//                                }
//
//                        }
                        intentionPlaceForm.setBatchList(batchListAll);
                }

                return ResultBody.success(intentionPlaceForm);
        }








        @Override
        public List<IntentionPlaceBuild> getBldingRoomList(String bldIds,String activityId) {
                List<IntentionPlaceBuild> builds = new ArrayList<>();
                //如果是查看活动（根据活动ID查询房间数据）
                if (activityId!=null && !"".equals(activityId) && (bldIds==null || "".equals(bldIds))){
                        builds = this.getOldRoomList(activityId,null);
                }else if (activityId!=null && !"".equals(activityId) && bldIds!=null && !"".equals(bldIds)){
                       //编辑时
                       //先查询原有楼栋
                    //模拟数据 空map
                        List<IntentionPlaceBuild> buildsOld = intentionPlaceDao.getIntentionPlaceBuild(new HashMap());
                        List<String> oldIds = new ArrayList<>();
                        String[] ids = bldIds.split(",");
                        List<String> newIds = new ArrayList<>();
                        //拿到原有楼栋ID
                        for (IntentionPlaceBuild build:buildsOld) {
                                oldIds.add(build.getBuildguid());
                        }
                        //判断新旧楼栋差异
                        for (String id:ids) {
                             if (oldIds.contains(id)){
                                     oldIds.remove(id);
                             }  else{
                                     newIds.add(id);
                             }
                        }
                        //如果无新增
                        if (newIds.size()==0){
                                //如果无减少
                              if (oldIds.size()==0){
                                      //返回原楼栋数据
                                      builds = this.getOldRoomList(activityId,null);
                              }else{
                                      //删除减少的楼栋房间
                                      for (String id:oldIds) {
                                              for (int i = 0; i < buildsOld.size(); i++) {
                                                      IntentionPlaceBuild build = buildsOld.get(i);
                                                      if (build.getBuildguid().equals(id)){
                                                              buildsOld.remove(build);
                                                              i--;
                                                      }
                                              }

                                      }
                                      //返回留下的楼栋房间
                                      for (IntentionPlaceBuild build:buildsOld) {
                                              builds.add(this.getOldRoomList(activityId,build.getBuildguid()).get(0));
                                      }

                              }
                        }else{
                                //如果有新增无减少
                                if(oldIds.size()==0){
                                        //先查询原有数据
                                        builds = this.getOldRoomList(activityId,null);

                                }else{
                                        //删除减少的楼栋房间
                                        for (String id:oldIds) {
                                                for (int i = 0; i < buildsOld.size(); i++) {
                                                        IntentionPlaceBuild build = buildsOld.get(i);
                                                        if (build.getBuildguid().equals(id)){
                                                                buildsOld.remove(build);
                                                                i--;
                                                        }
                                                }

                                        }
                                        //返回留下的楼栋房间
                                        for (IntentionPlaceBuild build:buildsOld) {
                                                builds.add(this.getOldRoomList(activityId,build.getBuildguid()).get(0));
                                        }
                                }
                                //将新增的楼栋添加进去
                                StringBuffer sb = new StringBuffer();
                                for (String id:newIds) {
                                        sb.append(id+",");
                                }
                                String bldIdsNew = sb.toString().substring(0,sb.toString().length()-1);
                                List<IntentionPlaceBuild> buildsNew = this.getNewRoom(bldIdsNew);
                                if (buildsNew!=null && buildsNew.size()>0){
                                        builds.addAll(buildsNew);
                                }

                        }
                }else {
                    List<IntentionPlaceBuild> buildsNew = this.getNewRoom(bldIds);
                    if (buildsNew!=null && buildsNew.size()>0){
                        builds.addAll(buildsNew);
                    }

//                    String str ="[{\"id\":null,\"activityId\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"buildprice\":null,\"orderby\":\"00000001\",\"status\":null,\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"unitList\":[{\"projectid\":null,\"activityId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"bldunitguid\":\"644973FC-1C17-EA11-80BE-005056A21B76\",\"unitno\":\"0\",\"unitname\":\"1\",\"maxroomcount\":2,\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"roomList\":[{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"924973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1701\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1701\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"17\",\"floorno\":16,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"14200.0000\",\"insidePrice\":\"19012.7700\",\"totalPrice\":\"1563420.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"934973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1702\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1702\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"17\",\"floorno\":16,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"14400.0000\",\"insidePrice\":\"19279.8900\",\"totalPrice\":\"1795536.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"944973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1601\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1601\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"16\",\"floorno\":15,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"15200.0000\",\"insidePrice\":\"20351.7000\",\"totalPrice\":\"1673520.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"954973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1602\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1602\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"16\",\"floorno\":15,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15400.0000\",\"insidePrice\":\"20618.7700\",\"totalPrice\":\"1920226.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"964973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1501\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1501\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"15\",\"floorno\":14,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"15200.0000\",\"insidePrice\":\"20351.7000\",\"totalPrice\":\"1673520.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"974973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1502\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1502\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"15\",\"floorno\":14,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15400.0000\",\"insidePrice\":\"20618.7700\",\"totalPrice\":\"1920226.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"984973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1401\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1401\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"14\",\"floorno\":13,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"15150.0000\",\"insidePrice\":\"20284.7500\",\"totalPrice\":\"1668015.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"994973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1402\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1402\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"14\",\"floorno\":13,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15350.0000\",\"insidePrice\":\"20551.8200\",\"totalPrice\":\"1913991.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"9A4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1301\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1301\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"13\",\"floorno\":12,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"15150.0000\",\"insidePrice\":\"20284.7500\",\"totalPrice\":\"1668015.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"9B4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1302\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1302\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"13\",\"floorno\":12,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15350.0000\",\"insidePrice\":\"20551.8200\",\"totalPrice\":\"1913991.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"9C4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1201\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1201\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"12\",\"floorno\":11,\"saleStatus\":\"2\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"15150.0000\",\"insidePrice\":\"20284.7500\",\"totalPrice\":\"1668015.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"9D4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1202\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1202\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"12\",\"floorno\":11,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15350.0000\",\"insidePrice\":\"20551.8200\",\"totalPrice\":\"1913991.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"9E4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1101\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1101\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"11\",\"floorno\":10,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"15100.0000\",\"insidePrice\":\"20217.8000\",\"totalPrice\":\"1662510.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"9F4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1102\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1102\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"11\",\"floorno\":10,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15300.0000\",\"insidePrice\":\"20484.8800\",\"totalPrice\":\"1907757.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A04973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1001\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1001\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"10\",\"floorno\":9,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"15050.0000\",\"insidePrice\":\"20150.8600\",\"totalPrice\":\"1657005.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A14973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"1002\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-1002\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"10\",\"floorno\":9,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15250.0000\",\"insidePrice\":\"20417.9300\",\"totalPrice\":\"1901522.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A24973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"901\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-901\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"9\",\"floorno\":8,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"14995.9900\",\"insidePrice\":\"20078.5500\",\"totalPrice\":\"1651059.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A34973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"902\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-902\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"9\",\"floorno\":8,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15196.0000\",\"insidePrice\":\"20345.6400\",\"totalPrice\":\"1894789.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A44973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"801\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-801\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"8\",\"floorno\":7,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"14940.0000\",\"insidePrice\":\"20003.5800\",\"totalPrice\":\"1644894.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A54973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"802\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-802\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"8\",\"floorno\":7,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15140.0000\",\"insidePrice\":\"20270.6500\",\"totalPrice\":\"1887806.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A64973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"701\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-701\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"7\",\"floorno\":6,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"14890.0000\",\"insidePrice\":\"19936.6300\",\"totalPrice\":\"1639389.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A74973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"702\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-702\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"7\",\"floorno\":6,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15090.0000\",\"insidePrice\":\"20203.7200\",\"totalPrice\":\"1881572.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A84973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"601\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-601\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"6\",\"floorno\":5,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"14825.0000\",\"insidePrice\":\"19849.5900\",\"totalPrice\":\"1632232.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"A94973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"602\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-602\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"6\",\"floorno\":5,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15025.0000\",\"insidePrice\":\"20116.6900\",\"totalPrice\":\"1873467.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"AA4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"501\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-501\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"5\",\"floorno\":4,\"saleStatus\":\"2\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"14800.0000\",\"insidePrice\":\"19816.1300\",\"totalPrice\":\"1629480.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"AB4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"502\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-502\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"5\",\"floorno\":4,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"15000.0000\",\"insidePrice\":\"20083.2200\",\"totalPrice\":\"1870350.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"AC4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"401\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-401\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"4\",\"floorno\":3,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"14750.0000\",\"insidePrice\":\"19749.1800\",\"totalPrice\":\"1623975.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"AD4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"402\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-402\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"4\",\"floorno\":3,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"14950.0000\",\"insidePrice\":\"20016.2700\",\"totalPrice\":\"1864115.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"AE4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"301\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-301\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"3\",\"floorno\":2,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"14700.0000\",\"insidePrice\":\"19682.2300\",\"totalPrice\":\"1618470.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"AF4973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"302\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-302\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"3\",\"floorno\":2,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"14900.0000\",\"insidePrice\":\"19949.3300\",\"totalPrice\":\"1857881.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"B04973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"201\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-201\",\"roomno\":1,\"unitno\":\"0\",\"floor\":\"2\",\"floorno\":1,\"saleStatus\":\"2\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"110\",\"houseTypeId\":\"80787FF0-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"110.1000\",\"insideArea\":\"82.2300\",\"floorPrice\":\"14200.0000\",\"insidePrice\":\"19012.7700\",\"totalPrice\":\"1563420.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"1\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null},{\"id\":null,\"activityId\":null,\"activityName\":null,\"projectid\":null,\"projectname\":null,\"projectidFq\":\"1A28C405-34EE-E711-80BB-005056A27FB0\",\"projectnameFq\":\"天津公元大道一期\",\"placebuildId\":null,\"buildguid\":\"311CC4DD-15A2-E911-80BE-005056A27FB0\",\"buildname\":\"1\",\"roomguid\":\"B14973FC-1C17-EA11-80BE-005056A21B76\",\"roomname\":\"202\",\"roominfo\":\"旭辉公元大道（天津）-旭辉公元大道一期（天津）-1-1-202\",\"roomno\":2,\"unitno\":\"0\",\"floor\":\"2\",\"floorno\":1,\"saleStatus\":\"1\",\"roomType\":\"住宅\",\"exposure\":null,\"exposureImg\":null,\"houseType\":\"125\",\"houseTypeId\":\"60B70BF7-5F99-E911-80BE-005056A27FB0\",\"floorArea\":\"124.6900\",\"insideArea\":\"93.1300\",\"floorPrice\":\"14400.0000\",\"insidePrice\":\"19279.8900\",\"totalPrice\":\"1795536.0000\",\"configureTotalPrice\":null,\"totalCount\":null,\"totalDiffCount\":null,\"totalEditCount\":null,\"oneCount\":null,\"oneDiffCount\":null,\"oneEditCount\":null,\"twoCount\":null,\"twoDiffCount\":null,\"twoEditCount\":null,\"threeCount\":null,\"threeDiffCount\":null,\"threeEditCount\":null,\"fourCount\":null,\"fourDiffCount\":null,\"fourEditCount\":null,\"fiveCount\":null,\"fiveDiffCount\":null,\"fiveEditCount\":null,\"orderby\":\"2\",\"status\":\"1\",\"isdel\":null,\"createtime\":null,\"creator\":null,\"edittime\":null,\"editor\":null,\"isChecked\":null}],\"checkArr\":null}],\"checkFloor\":null}]";
//                        builds = JSONArray.parseArray(str,IntentionPlaceBuild.class);
                }
                return builds;
        }

        @Override
        public List<Map> getFProject(String projectId) {
                return intentionPlaceDao.getFProject(projectId);
        }

        @Override
        public List<Map> getFProjectCardGroup(String projectId) {
                List<Map> proMaps = new ArrayList<>();
                //获取分期项目ID
                List<Map> fproIds = intentionPlaceDao.getFProject(projectId);
//                for (Map map:fproIds) {
//                        Map proMap = new HashMap();
//                        proMap.put("value",map.get("id"));
//                        proMap.put("label",map.get("projectName"));
//                        proMap.put("isChecked","0");
//                        //根据分期项目ID获取排卡批次
//                        List<Map<String,Object>> openRoomBatch = DbTest.getObjects("select DISTINCT OpenRoomBatchGUID value,OpenRoomBatchName label,'0' isChecked from VS_XK_ProjPrefix where ProjectID = '"+map.get("id")+"' and IsDisabled = 0");
//                        if (openRoomBatch!=null && openRoomBatch.size()>0){
//                                for (Map<String,Object> fix:openRoomBatch) {
//                                        //根据分期项目ID获取排卡批次
//                                        List<Map<String,Object>> proPrefixMap = DbTest.getObjects("select ProjPrefixCode value,ProjPrefix label,'0' isChecked from VS_XK_ProjPrefix where OpenRoomBatchGUID = '"+fix.get("value")+"' and IsDisabled = 0");
//                                        fix.put("children",proPrefixMap);
//                                }
//                                proMap.put("children",openRoomBatch);
//                        }
//                        proMaps.add(proMap);
//                }
                return proMaps;
        }
    @Override
    public ResultBody selectRoomResultCstList(Map map) {
        if (map == null || map.get("activityId") == null) {
            return ResultBody.error(-21_0006, "必传参数未传！！");
        }
        List<Map> result = intentionPlaceDao.selectRoomResultCst(map);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody updateRoomResult(Map map) {
        if (map.get("list") == null) {
            return ResultBody.error(-120005, "必传参数未传！");
        }
        List<Map> list = JSONObject.parseArray(JSONObject.toJSONString(map.get("list")), Map.class);
        if (list.size() > 0) {
            for (Map editMap : list) {
                String id = UUID.randomUUID().toString();
                editMap.put("id", id);
            }
        }
        map.put("list", list);
        //添加更新记录
        intentionPlaceDao.addEditRecords(map);
        //更新装户信息
        intentionPlaceDao.updateRoomResult(map);
        return ResultBody.success("修改成功！！");
    }

    @Override
    public ResultBody delRoomResult(Map map) {
        if (map == null || map.get("id") == null || "".equals(map.get("id") + "")
                || map.get("activityId") == null || "".equals(map.get("activityId") + "")) {
            return ResultBody.error(-120005, "必传参数未传！");
        }
        String rId = map.get("id") + "";

        String activityId = map.get("activityId") + "";
        //查询装户记录
        Map result = intentionPlaceDao.getDelIntentionDesc(map);
        String editId = UUID.randomUUID().toString();
        intentionPlaceDao.delRoomResult(rId,editId);
        List<Map> list = new ArrayList<>();
        //添加调整记录
        result.put("id",editId);
        result.put("roomguidEdit",null);
        result.put("roomnameEdit",null);
        result.put("intentionLevelEdit",null);
        list.add(result);
        map.put("list",list);
        //添加更新记录
        intentionPlaceDao.addEditRecords(map);
        //更新活动为已调整
        intentionPlaceDao.updateActivityEdit(activityId);
        return ResultBody.success("删除成功！！");
    }

    @Override
    public List<IntentionCst> getProActivityCsts(Map map) {
        // 获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String companycode = request.getHeader("companycode");

        //获取是否开启自销系统
        boolean flag1 = false;
        if (redisUtil.get("ISZXOPEN_" + companycode) != null) {
            if ("1".equals(redisUtil.get("ISZXOPEN_" + companycode).toString())) {
                flag1 = true;
            } else {
                flag1 = false;
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
                return new ArrayList<>();
            }
            if (companyMenuList != null && companyMenuList.size() > 0) {
                if (companyMenuList.contains("appmenu5-10")) {
                    //保存缓存是否开启自销为是
                    redisUtil.set("ISZXOPEN_" + companycode, "1", 3600);
                    flag1 = true;
                } else {
                    flag1 = false;
                    redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
                }
            } else {
                flag1 = false;
                //保存缓存是否开启自销为否
                redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
            }
        }
        List<IntentionCst> intentionCstList = new ArrayList<>();
        //活动ID
        String id = map.get("id") + "";
        //置业顾问ID
        String salesId = map.get("salesId") + "";//1 为查询无置业顾问客户
        //房间ID
        String roomId = map.get("roomId") + "";
        //查询活动数据
        IntentionPlaceForm intentionPlaceForm = intentionPlaceDao.getIntentionPlaceDetail(id);
        //意向房源个数
        int intentionCount = Integer.valueOf(intentionPlaceForm.getIntentionCount());

        //判断是否为数字（为数字匹配电话号码，不是匹配客户姓名）
        if (map.get("searchName") != null && !"".equals(map.get("searchName") + "")) {
            String searchName = map.get("searchName") + "";
            if (searchName.matches("[0-9]+")) {
                map.put("customerMobile", searchName);
            } else {
                map.put("userName", searchName);
            }
        }
        map.put("type","1");
        map.put("salesId",salesId);
        //查询是否存在客户范围数据
        List<IntentionCst> intentionCstList1 = intentionPlaceDao.getIntentionPlaceCstList(map);
        if (intentionCstList1!=null && intentionCstList1.size()>0){
            intentionCstList = intentionCstList1;
        }else{
            //查询机会数据
            map.put("activityEndTime", intentionPlaceForm.getActivityEndtime());
            map.put("projectId", intentionPlaceForm.getProjectid());
            List<IntentionCst> csts = intentionPlaceDao.getIntentionPlaceUserList(map);
            //查询活动排卡范围
            String cardType = "";
            if ("1".equals(intentionPlaceForm.getCustomerType())) {
                //到访及以后 查询项目下所有排卡
            } else {
                cardType = intentionPlaceForm.getCardType();
            }
            //查询排卡数据
            List<Map<String, Object>> rowcards = this.getRowCardLists(intentionPlaceForm.getId(), intentionPlaceForm.getProjectid(), cardType, intentionPlaceForm.getActivityEndtime(),null,flag1);

            //拼接装户客户数据
            if (!"".equals(cardType)){
                //仅排卡
                if (rowcards!=null && rowcards.size()>0 && csts!=null && csts.size()>0){
                    for (int i = 0; i < csts.size(); i++) {
                        IntentionCst cst = csts.get(i);
                        int count = 1;
                        for (Map cardMap:rowcards) {
                            String oppid = "";
                            if (flag1){
                                oppid = cst.getOpportunityClueId();
                            }else{
                                oppid = cst.getIntentionId();
                            }
                            if (oppid !=null && oppid.equals(cardMap.get("OppGUID")+"")){
                                IntentionCst cst1 = new IntentionCst();
                                cst1.setProjectid(cst.getProjectid());
                                cst1.setOpportunityClueId(cst.getOpportunityClueId());
                                cst1.setProjectClueId(cst.getProjectClueId());
                                cst1.setIntentionId(cst.getIntentionId());
                                cst1.setCustomerName(cst.getCustomerName());
                                cst1.setCustomerMobile(cst.getCustomerMobile());
                                cst1.setProjectname(cst.getProjectname());
                                cst1.setSalesId(cst.getSalesId());
                                cst1.setSalesName(cst.getSalesName());
                                cst1.setActivityId(intentionPlaceForm.getId());
                                cst1.setActivityName(intentionPlaceForm.getActivityName());
                                cst1.setCardId(cardMap.get("BookingGUID")+"");
                                cst1.setCardName("卡"+NumberUtil.int2chineseNum(count));
                                cst1.setProjectidFq(cardMap.get("p_projectfId")+"");
                                if (cardMap.get("x_OpenRoomBatchGUID")!=null){
                                    cst1.setBatchNo(cardMap.get("x_OpenRoomBatchGUID")+"");
                                    cst1.setBatchNoName(cardMap.get("x_OpenRoomBatchName")+"");
                                    cst1.setCardGrouping(cardMap.get("ProjPrefixCode")+"");
                                    cst1.setCardGroupingName(cardMap.get("ProjPrefix")+"");
                                }
                                if ("小卡".equals(cardMap.get("kpstate")+"")){
                                    cst1.setCardType("1");
                                }else{
                                    cst1.setCardType("2");
                                }
                                intentionCstList.add(cst1);
                                count++;
                            }
                        }
                    }
                }
            }else{
                if (csts.size()>0){
                    //到访及以后
                    if (rowcards!=null && rowcards.size()>0){
                        for (int i = 0; i < csts.size(); i++) {
                            IntentionCst cst = csts.get(i);
                            cst.setActivityId(intentionPlaceForm.getId());
                            cst.setActivityName(intentionPlaceForm.getActivityName());
                            boolean flag = false;
                            int count = 1;
                            for (Map cardMap:rowcards) {
                                String oppid = "";
                                if (flag1){
                                    oppid = cst.getOpportunityClueId();
                                }else{
                                    oppid = cst.getIntentionId();
                                }
                                if (oppid !=null && oppid.equals(cardMap.get("OppGUID")+"")){
                                    flag = true;
                                    IntentionCst cst1 = new IntentionCst();
                                    cst1.setProjectid(cst.getProjectid());
                                    cst1.setOpportunityClueId(cst.getOpportunityClueId());
                                    cst1.setProjectClueId(cst.getProjectClueId());
                                    cst1.setIntentionId(cst.getIntentionId());
                                    cst1.setCustomerName(cst.getCustomerName());
                                    cst1.setCustomerMobile(cst.getCustomerMobile());
                                    cst1.setProjectname(cst.getProjectname());
                                    cst1.setSalesId(cst.getSalesId());
                                    cst1.setSalesName(cst.getSalesName());
                                    cst1.setActivityId(intentionPlaceForm.getId());
                                    cst1.setActivityName(intentionPlaceForm.getActivityName());
                                    cst1.setCardId(cardMap.get("BookingGUID")+"");
                                    cst1.setCardName("卡"+NumberUtil.int2chineseNum(count));
                                    cst1.setProjectidFq(cardMap.get("p_projectfId")+"");
                                    if (cardMap.get("x_OpenRoomBatchGUID")!=null){
                                        cst1.setBatchNo(cardMap.get("x_OpenRoomBatchGUID")+"");
                                        cst1.setBatchNoName(cardMap.get("x_OpenRoomBatchName")+"");
                                        cst1.setCardGrouping(cardMap.get("ProjPrefixCode")+"");
                                        cst1.setCardGroupingName(cardMap.get("ProjPrefix")+"");
                                    }
                                    if ("小卡".equals(cardMap.get("kpstate")+"")){
                                        cst1.setCardType("1");
                                    }else{
                                        cst1.setCardType("2");
                                    }
                                    intentionCstList.add(cst1);
                                    count++;
                                }
                            }
                            if (flag){
                                csts.remove(i);
                                i--;
                            }
                        }
                        if (csts.size()>0){
                            intentionCstList.addAll(csts);
                        }
                    }else{
                        for (IntentionCst cst:csts) {
                            cst.setActivityId(intentionPlaceForm.getId());
                            cst.setActivityName(intentionPlaceForm.getActivityName());
                        }
                        intentionCstList.addAll(csts);
                    }
                }
            }
        }
        //查询已经装户客户数据
        map.put("activityId", id);
        map.put("edition", "2");
        map.put("roomId", "");
        List<Map> cstResult = intentionPlaceDao.selectRoomResultCst(map);
        map.put("roomId", roomId);
        if (intentionCstList.size()>0){
            for (int i = 0; i < intentionCstList.size(); i++) {
                IntentionCst cst = intentionCstList.get(i);
                List<String> intentionLevelStr = new ArrayList<>();
                List<Map> intentionLevels = new ArrayList<>();
                for (int j = 1; j <= intentionCount; j++) {
                    intentionLevelStr.add(j+"");
                }
                boolean ff = true;
                if (cstResult!=null && cstResult.size()>0){
                    for (Map resultMap:cstResult){
                        if (resultMap.get("roomguidOld")!=null && roomId.equals(resultMap.get("roomguidOld")+"")
                                && cst.getOpportunityClueId().equalsIgnoreCase(resultMap.get("opportunityClueId")+"")){
                            intentionCstList.remove(i);
                            i--;
                            ff = false;
                            break;
                        }
                    }
                    if (ff){
                        for (Map resultMap:cstResult) {
                            if (resultMap.get("cardId")!=null && !"".equals(resultMap.get("cardId")+"")){
                                if (cst.getCardId()!=null && !"".equals(cst.getCardId())
                                        && cst.getCardId().equals(resultMap.get("cardId")+"") && resultMap.get("roomguidOld")!=null && !"".equals(resultMap.get("roomguidOld")+"")){
                                    intentionLevelStr.remove(resultMap.get("intentionLevelOld")+"");
                                }
                            }else {
                                if ((cst.getCardId()==null || "".equals(cst.getCardId()))
                                        && cst.getOpportunityClueId().equalsIgnoreCase(resultMap.get("opportunityClueId")+"")
                                        && resultMap.get("roomguidOld")!=null && !"".equals(resultMap.get("roomguidOld")+"")){
                                    intentionLevelStr.remove(resultMap.get("intentionLevelOld")+"");
                                }
                            }
                        }
                    }

                }
                if (ff){
                    if (intentionLevelStr.size()==0){
                        intentionCstList.remove(i);
                        i--;
                    }else{
                        for (String ss:intentionLevelStr) {
                            Map levelMap = new HashMap();
                            levelMap.put("id",ss);
                            if (ss.equals("1")){
                                levelMap.put("value","首选");
                            }else if (ss.equals("2")){
                                levelMap.put("value","二选");
                            }else if (ss.equals("3")){
                                levelMap.put("value","三选");
                            }else if (ss.equals("4")){
                                levelMap.put("value","四选");
                            }else if (ss.equals("5")){
                                levelMap.put("value","五选");
                            }
                            intentionLevels.add(levelMap);
                        }
                        cst.setIntentionLevels(intentionLevels);
                    }
                }
            }
        }
        return intentionCstList;
    }

    /***
     *
     * @param activityId
     * @param projectId
     * @param cardType
     *@return {}
     *@throws
     *@Description: 查询明源排卡机会id
     *@author FuYong
     *@date 2020/8/11 17:00
     */
    public List<Map<String, Object>> getRowCardLists(String activityId, String projectId, String cardType, String activityEndTime,String OppGUID,boolean flag) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //判断是否自销
        if (flag){
            String[] cardTypes = cardType.split(",");
            String oppid = "";
            if (OppGUID!=null && !"".equals(OppGUID)){
                oppid = " and OpportunityClueId = '"+OppGUID+"'";
            }
            String card = "";
            if (cardTypes.length==1){
                if (cardTypes[0].equals("1")){
                    card = " and TransactionType = '1'";
                }else{
                    card = " and TransactionType in (2,4)";
                }

            }
            List<String> proIds = null;
            proIds =  intentionPlaceDao.getFqIdByProId(projectId);
            if (proIds!=null){
                projectId = "'"+StringUtils.join(proIds,"','")+"'";
            }else{
                projectId = "'"+projectId+"'";
            }
            //查询自己的排卡数据
            String sql = "select DISTINCT projectId,projectId p_projectfId,'' as x_kingdeeProjectID,'' as x_kingdeeProjectfID,RowCardID BookingGUID,OpportunityClueId OppGUID," +
                    "OpenRoomBatchGUID x_OpenRoomBatchGUID,OpenRoomBatchName x_OpenRoomBatchName,ProjPrefixCode,ProjPrefix,(case when TransactionType = '1' then '小卡' else '大卡' end) kpstate,'' ProjNum,'激活' as status,CreateTime " +
                    "from b_customer_row_card where CreateTime < '" + activityEndTime + "' and projectId in (" + projectId + ")"+oppid+card+" and IsDel = 0 order by CreateTime";
            resultList = housingManagementDao.getDataList(sql);
        }else{
            String oppid = "";
            if (OppGUID!=null && !"".equals(OppGUID)){
                oppid = " and OppGUID = '"+OppGUID+"'";
            }
            if ("".equals(cardType)){
                List<String> proIds = intentionPlaceDao.getFqIdByProId(projectId);
                if (proIds != null && proIds.size() > 0) {
                    String proId = "'" + StringUtils.join(proIds.toArray(), "','") + "'";
                    String sql = "select DISTINCT projectId,p_projectfId,x_kingdeeProjectID,x_kingdeeProjectfID,BookingGUID,OppGUID," +
                            "x_OpenRoomBatchGUID,x_OpenRoomBatchName,ProjPrefixCode,ProjPrefix,kpstate,ProjNum,status,CreatedTime " +
                            "from VS_XK_Booking2Prefix where status = '激活' " +
                            "and CreatedTime < '" + activityEndTime + "' and p_projectfId in (" + proId + ")"+oppid+" order by CreatedTime";
                    resultList = DbTest.getObjects(sql);
                }
            }else{
                String[] cardTypes = cardType.split(",");
                if (cardTypes.length == 1) {
                    //如果是小卡
                    if ("1".equals(cardTypes[0])) {
                        List<String> proIds = intentionPlaceDao.getSmallCard(activityId, projectId);
                        if (proIds != null && proIds.size() > 0) {
                            String proId = "'" + StringUtils.join(proIds.toArray(), "','") + "'";
                            String sql = "select DISTINCT projectId,p_projectfId,x_kingdeeProjectID,x_kingdeeProjectfID,BookingGUID,OppGUID," +
                                    "x_OpenRoomBatchGUID,x_OpenRoomBatchName,ProjPrefixCode,ProjPrefix,kpstate,ProjNum,status,CreatedTime " +
                                    "from VS_XK_Booking2Prefix where status = '激活' " +
                                    "and CreatedTime < '" + activityEndTime + "' and kpstate = '小卡' and p_projectfId in (" + proId + ")"+oppid+" order by CreatedTime";
                            resultList = DbTest.getObjects(sql);
                        }
                    } else {
                        StringBuffer sb = new StringBuffer();
                        String sql = "select DISTINCT projectId,p_projectfId,x_kingdeeProjectID,x_kingdeeProjectfID,BookingGUID,OppGUID," +
                                "x_OpenRoomBatchGUID,x_OpenRoomBatchName,ProjPrefixCode,ProjPrefix,kpstate,ProjNum,status,CreatedTime " +
                                "from VS_XK_Booking2Prefix where status = '激活' " +
                                "and CreatedTime < '" + activityEndTime + "' and kpstate = '大卡'";
                        sb.append(sql);
                        //如果是大卡
                        List<Map> dMap = intentionPlaceDao.getBigCard(activityId, projectId);
                        if (dMap != null && dMap.size() > 0) {
                            sb.append(" and (");
                            for (int i = 0; i < dMap.size(); i++) {
                                if (i == dMap.size() - 1) {
                                    if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
                                        sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "')");
                                    } else {
                                        sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "')");
                                    }
                                } else {
                                    if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
                                        sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "') or ");
                                    } else {
                                        sb.append("(p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "') or ");
                                    }
                                }

                            }
                            sb.append(")"+oppid+" order by CreatedTime");
                        }
                        resultList = DbTest.getObjects(sb.toString());
                    }
                } else {
                    //查询明源机会ID
                    //查询小卡范围
                    List<String> proIds = intentionPlaceDao.getSmallCard(activityId, projectId);
                    //查询大卡范围
                    List<Map> dMap = intentionPlaceDao.getBigCard(activityId, projectId);
                    StringBuffer sb = new StringBuffer();
                    String sql = "select DISTINCT projectId,p_projectfId,x_kingdeeProjectID,x_kingdeeProjectfID,BookingGUID,OppGUID," +
                            "x_OpenRoomBatchGUID,x_OpenRoomBatchName,ProjPrefixCode,ProjPrefix,kpstate,ProjNum,status,CreatedTime " +
                            "from VS_XK_Booking2Prefix where status = '激活' " +
                            "and CreatedTime < '" + activityEndTime + "'";
                    sb.append(sql);
                    if ((proIds != null && proIds.size() > 0) || (dMap != null && dMap.size() > 0)) {
                        sb.append(" and (");
                        if (proIds != null && proIds.size() > 0) {
                            String proId = "'" + StringUtils.join(proIds.toArray(), "','") + "'";
                            if (dMap != null && dMap.size() > 0) {
                                sb.append("(kpstate = '小卡' and p_projectfId in (" + proId + ")) or ");
                            } else {
                                sb.append("(kpstate = '小卡' and p_projectfId in (" + proId + "))");
                            }
                        }
                        if (dMap != null && dMap.size() > 0) {
                            for (int i = 0; i < dMap.size(); i++) {
                                if (i == dMap.size() - 1) {
                                    if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
                                        sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "')");
                                    } else {
                                        sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "')");
                                    }
                                } else {
                                    if (dMap.get(i).get("openingBatch") == null || "".equals(dMap.get(i).get("openingBatch") + "")) {
                                        sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "') or ");
                                    } else {
                                        sb.append("(kpstate = '大卡' and p_projectfId = '" + dMap.get(i).get("fid") + "' and x_OpenRoomBatchGUID = '" + dMap.get(i).get("openingBatch") + "' and ProjPrefixCode = '" + dMap.get(i).get("cardGrouping") + "') or ");
                                    }
                                }
                            }
                        }
                        sb.append(")"+oppid+" order by CreatedTime");
                    }
                    resultList = DbTest.getObjects(sb.toString());
                }
            }
        }

        return resultList;
    }

    @Override
    public void addRoomResultList(Map map) {
        List<Map> list = JSONObject.parseArray(JSONObject.toJSONString(map.get("list")),Map.class);
        List<Map> updateMap = new ArrayList<>();
        List<Map> addMap = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String id = UUID.randomUUID().toString();
            list.get(i).put("id",id);
            if (list.get(i).get("roomguidOld")==null || "".equals(list.get(i).get("roomguidOld")+"")){
                //判断此意向是否原来存在
                int count = intentionPlaceDao.queryIsResultExit(list.get(i));
                if (count>0){
                    list.get(i).put("intentionLevel",list.get(i).get("intentionLevelEdit"));
                    updateMap.add(list.get(i));
                }else{
                    addMap.add(list.get(i));
                }
            }else{
                updateMap.add(list.get(i));
            }
        }
        if (addMap.size()>0){
            map.put("list",addMap);
            intentionPlaceDao.addRoomResultList(map);
            intentionPlaceDao.addEditRecords(map);
        }
        if (updateMap.size()>0){
            map.put("list",updateMap);
            intentionPlaceDao.updateRoomResult(map);
            intentionPlaceDao.addEditRecords(map);
        }
        if (list!=null && list.size()>0){
            String activityId = list.get(0).get("activityId")+"";
            if (activityId!=null && !"".equals(activityId)){
                //更新活动为已调整
                intentionPlaceDao.updateActivityEdit(activityId);
            }
        }
    }

    @Override
    public Map getActivityStatistics(Map map) {
        Map result = new HashMap();
        //活动ID
        String id = map.get("id") + "";
        //查询活动数据
        IntentionPlaceForm intentionPlaceForm = intentionPlaceDao.getIntentionPlaceDetail(id);
        //待装户数量
        IntentionPlaceVO intentionPlaceVO = new IntentionPlaceVO();
        intentionPlaceVO.setId(intentionPlaceForm.getId());
        intentionPlaceVO.setCardType(intentionPlaceForm.getCardType());
        intentionPlaceVO.setCustomerType(intentionPlaceForm.getCustomerType());
        intentionPlaceVO.setActivityEndtime(intentionPlaceForm.getActivityEndtime());
        intentionPlaceVO.setProjectid(intentionPlaceForm.getProjectid());
        int sum = 0;
        //查询是否存在客户范围数据
        List<IntentionCst> intentionCstList1 = intentionPlaceDao.getIntentionPlaceCstList(map);
        if (intentionCstList1!=null && intentionCstList1.size()>0){
            sum = intentionCstList1.size();
        }else{
            sum = this.getNeedIntentionPlace(intentionPlaceVO);
        }
        result.put("needPlaceCount",sum);
        result.put("placeCount",intentionPlaceForm.getPlaceCount());
        if (sum>0){
            double placeCount = Double.valueOf(intentionPlaceForm.getPlaceCount())*100;
            String scale = decimalFormat.format(placeCount/sum);
            result.put("scale",scale);
        }else{
            result.put("scale",0.00);
        }
        return result;
    }

    @Override
    public List<IntentionCst> getActivityCstStatistics(Map map) {
        // 获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String companycode = request.getHeader("companycode");

        //获取是否开启自销系统
        boolean flag1 = false;
        if (redisUtil.get("ISZXOPEN_" + companycode) != null) {
            if ("1".equals(redisUtil.get("ISZXOPEN_" + companycode).toString())) {
                flag1= true;
            } else {
                flag1 = false;
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
            }
            if (companyMenuList != null && companyMenuList.size() > 0) {
                if (companyMenuList.contains("appmenu5-10")) {
                    //保存缓存是否开启自销为是
                    redisUtil.set("ISZXOPEN_" + companycode, "1", 3600);
                    flag1 = true;
                } else {
                    flag1 = false;
                    redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
                }
            } else {
                flag1 = false;
                //保存缓存是否开启自销为否
                redisUtil.set("ISZXOPEN_" + companycode, "2", 3600);
            }
        }
        List<IntentionCst> end = new ArrayList<>();
        //活动ID
        String id = map.get("id") + "";
        map.put("type","2");
        //查询活动数据
        IntentionPlaceForm intentionPlaceForm = intentionPlaceDao.getIntentionPlaceDetail(id);
        //意向房源个数
        int intentionCount = Integer.valueOf(intentionPlaceForm.getIntentionCount());
        //判断是否为数字（为数字匹配电话号码，不是匹配客户姓名）
        if (map.get("searchName") != null && !"".equals(map.get("searchName") + "")) {
            String searchName = map.get("searchName") + "";
            if (searchName.matches("[0-9]+")) {
                map.put("customerMobile", searchName);
            } else {
                map.put("userName", searchName);
            }
        }
        //查询已经装户客户数据
        map.put("activityId", id);
        map.put("edition", map.get("edition") + "");
        List<Map> cstResult = intentionPlaceDao.selectRoomResultCst(map);
        //查询是否存在客户范围数据
        List<IntentionCst> intentionCstList1 = intentionPlaceDao.getIntentionPlaceCstOne(map);
        if (intentionCstList1!=null && intentionCstList1.size()>0){
            for (int i = 0; i < intentionCstList1.size(); i++) {
                IntentionCst cst = intentionCstList1.get(i);
                cst.setNeedPlaceCount(String.valueOf(Integer.parseInt(cst.getNeedPlaceCount())*intentionCount));
                int placeCount = 0;
                if (cstResult!=null && cstResult.size()>0){
                    for (Map cstMapResult:cstResult) {
                        if (cstMapResult.get("opportunityClueId")!=null && String.valueOf(cstMapResult.get("opportunityClueId")).equalsIgnoreCase(cst.getOpportunityClueId())
                                && cstMapResult.get("roomguidOld")!=null && !"".equals(cstMapResult.get("roomguidOld")+"")){
                            placeCount++;
                        }
                    }
                }
                cst.setPlaceCount(placeCount+"");
            }
            end.addAll(intentionCstList1);
        }else{
            //查询机会数据
            map.put("activityEndTime", intentionPlaceForm.getActivityEndtime());
            map.put("projectId", intentionPlaceForm.getProjectid());
            List<IntentionCst> csts = intentionPlaceDao.getIntentionPlaceUserList(map);

            //查询活动排卡范围
            String cardType = "";
            if ("1".equals(intentionPlaceForm.getCustomerType())) {
            } else {
                cardType = intentionPlaceForm.getCardType();
            }
            //查询排卡数据
            List<Map<String, Object>> rowcards = this.getRowCardLists(intentionPlaceForm.getId(), intentionPlaceForm.getProjectid(), cardType, intentionPlaceForm.getActivityEndtime(),null,flag1);

            //拼接装户客户数据
            if (csts!=null && csts.size()>0){
                for (int i = 0;i< csts.size();i++) {
                    IntentionCst cst = csts.get(i);
                    if (flag1){
                        if (!"".equals(cardType)){
                            boolean flag = false;
                            for (Map mapCard : rowcards) {
                                if (mapCard.get("OppGUID") != null) {
                                    if (String.valueOf(mapCard.get("OppGUID")).equalsIgnoreCase(cst.getOpportunityClueId())) {
                                        flag = false;
                                        break;
                                    } else {
                                        flag = true;
                                    }
                                } else {
                                    flag = true;
                                }
                            }
                            if (flag) {
                                csts.remove(cst);
                                i--;
                            }
                        }
                    }else{
                        if (!"".equals(cardType)){
                            if (!StringUtils.isBlank(cst.getIntentionId())) {
                                boolean flag = false;
                                for (Map mapCard : rowcards) {
                                    if (mapCard.get("OppGUID") != null) {
                                        if (String.valueOf(mapCard.get("OppGUID")).equalsIgnoreCase(cst.getIntentionId())) {
                                            flag = false;
                                            break;
                                        } else {
                                            flag = true;
                                        }
                                    } else {
                                        flag = true;
                                    }
                                }
                                if (flag) {
                                    csts.remove(cst);
                                    i--;
                                }
                            } else {
                                csts.remove(cst);
                                i--;
                            }
                        }
                    }

                }
                if (csts!=null && csts.size()>0){
                    for (IntentionCst cst:csts) {
                        int placeCount = 0;
                        int needPlaceCount = 0;
                        if (rowcards!=null && rowcards.size()>0){
                            for (Map mapCard : rowcards) {
                                if (flag1){
                                    if (mapCard.get("OppGUID") != null && String.valueOf(mapCard.get("OppGUID")).equalsIgnoreCase(cst.getOpportunityClueId())) {
                                        needPlaceCount+=intentionCount;
                                    }
                                }else{
                                    if (mapCard.get("OppGUID") != null && String.valueOf(mapCard.get("OppGUID")).equalsIgnoreCase(cst.getIntentionId())) {
                                        needPlaceCount+=intentionCount;
                                    }
                                }
                            }
                            cst.setNeedPlaceCount(needPlaceCount+"");
                        }else{
                            cst.setNeedPlaceCount(intentionCount+"");
                        }
                        if (cstResult!=null && cstResult.size()>0){
                            for (Map cstMapResult:cstResult) {
                                if (cstMapResult.get("opportunityClueId")!=null && String.valueOf(cstMapResult.get("opportunityClueId")).equalsIgnoreCase(cst.getOpportunityClueId())
                                        && cstMapResult.get("roomguidOld")!=null && !"".equals(cstMapResult.get("roomguidOld")+"")){
                                    placeCount++;
                                }
                            }
                        }
                        cst.setPlaceCount(placeCount+"");
                    }
                }
                end.addAll(csts);
            }
        }
        if (end.size()>0){
            end = end.stream().sorted(Comparator.comparing(IntentionCst::getNeedPlaceCount).reversed()).collect(Collectors.toList());
        }
        return end;
    }

    @Override
    public IntentionCst getActivityCstDetail(Map map) {
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
        //活动ID
        String id = map.get("id") + "";
        //机会ID
        String opportunityClueId = map.get("opportunityClueId") + "";
        //查询机会数据
        IntentionCst intentionCst = intentionPlaceDao.getIntentionPlaceUser(map);
        //版本
        String edition = map.get("edition") + "";
        //查询活动数据
        IntentionPlaceForm intentionPlaceForm = intentionPlaceDao.getIntentionPlaceDetail(id);
        intentionCst.setActivityId(id);
        intentionCst.setActivityName(intentionPlaceForm.getActivityName());
        //查询已经装户客户数据
        map.put("activityId", id);
        map.put("opportunityClueId", opportunityClueId);
        map.put("edition", edition);
        List<Map> cstResult = intentionPlaceDao.selectRoomResultCst(map);
        //查询活动排卡范围
        String cardType = "";
        if ("1".equals(intentionPlaceForm.getCustomerType())) {
        } else {
            cardType = intentionPlaceForm.getCardType();
        }
        //意向房源个数
        int intentionCount = Integer.valueOf(intentionPlaceForm.getIntentionCount());
        List<IntentionCstCard> cards = new ArrayList<>();
        if ("".equals(cardType)){
            //查询是否存在装户范围卡片
            List<IntentionCst> intentionCstList1 = intentionPlaceDao.getCstCardList(map);
            //查询是否存在装户范围卡片
            if (intentionCstList1!=null && intentionCstList1.size()>0){
                for (IntentionCst csst:intentionCstList1) {
                    IntentionCstCard intentionCstCard = new IntentionCstCard();
                    intentionCstCard.setCardName(csst.getCardName());
                    intentionCstCard.setCardId(csst.getCardId());
                    List<IntentionCst> csts = new ArrayList<>();
                    for (int i = 0; i < intentionCount; i++) {
                        IntentionCst cst = new IntentionCst();
                        cst.setProjectid(intentionCst.getProjectid());
                        cst.setOpportunityClueId(intentionCst.getOpportunityClueId());
                        cst.setProjectClueId(intentionCst.getProjectClueId());
                        cst.setIntentionId(intentionCst.getIntentionId());
                        cst.setCustomerName(intentionCst.getCustomerName());
                        cst.setCustomerMobile(intentionCst.getCustomerMobile());
                        cst.setProjectname(intentionCst.getProjectname());
                        cst.setSalesId(intentionCst.getSalesId());
                        cst.setSalesName(intentionCst.getSalesName());
                        cst.setIntentionLevelEdit((i+1)+"");
                        if (i==0){
                            cst.setIntentionLevelEditDesc("首选");
                        }else if (i==1){
                            cst.setIntentionLevelEditDesc("二选");
                        }else if (i==2){
                            cst.setIntentionLevelEditDesc("三选");
                        }else if (i==3){
                            cst.setIntentionLevelEditDesc("四选");
                        }else if (i==4){
                            cst.setIntentionLevelEditDesc("五选");
                        }
                        cst.setCardId(csst.getCardId());
                        cst.setCardName(csst.getCardName());
                        cst.setProjectidFq(csst.getProjectidFq());
                        if (csst.getBatchNo()!=null){
                            cst.setBatchNo(csst.getBatchNo());
                            cst.setBatchNoName(csst.getBatchNoName());
                            cst.setCardGrouping(csst.getCardGrouping());
                            cst.setCardGroupingName(csst.getCardGroupingName());
                        }
                        cst.setCardType(csst.getCardType());
                        csts.add(cst);
                    }
                    intentionCstCard.setIntentionCsts(csts);
                    cards.add(intentionCstCard);
                }
            }else{
                String oppId = "";
                //判断是否自销系统
                if (flag){
                    oppId = intentionCst.getOpportunityClueId();
                }else{
                    oppId = intentionCst.getIntentionId();
                }
                //查询排卡数据
                List<Map<String, Object>> rowcards = this.getRowCardLists(intentionPlaceForm.getId(), intentionPlaceForm.getProjectid(), cardType, intentionPlaceForm.getActivityEndtime(),oppId,flag);
                if (rowcards!=null && rowcards.size()>0){
                    int count = 1;
                    for (Map cardMap:rowcards) {
                        IntentionCstCard intentionCstCard = new IntentionCstCard();
                        intentionCstCard.setCardName("卡"+ NumberUtil.int2chineseNum(count));
                        intentionCstCard.setCardId(cardMap.get("BookingGUID")+"");
                        List<IntentionCst> csts = new ArrayList<>();
                        for (int i = 0; i < intentionCount; i++) {
                            IntentionCst cst = new IntentionCst();
                            cst.setProjectid(intentionCst.getProjectid());
                            cst.setOpportunityClueId(intentionCst.getOpportunityClueId());
                            cst.setProjectClueId(intentionCst.getProjectClueId());
                            cst.setIntentionId(intentionCst.getIntentionId());
                            cst.setCustomerName(intentionCst.getCustomerName());
                            cst.setCustomerMobile(intentionCst.getCustomerMobile());
                            cst.setProjectname(intentionCst.getProjectname());
                            cst.setSalesId(intentionCst.getSalesId());
                            cst.setSalesName(intentionCst.getSalesName());
                            cst.setIntentionLevelEdit((i+1)+"");
                            if (i==0){
                                cst.setIntentionLevelEditDesc("首选");
                            }else if (i==1){
                                cst.setIntentionLevelEditDesc("二选");
                            }else if (i==2){
                                cst.setIntentionLevelEditDesc("三选");
                            }else if (i==3){
                                cst.setIntentionLevelEditDesc("四选");
                            }else if (i==4){
                                cst.setIntentionLevelEditDesc("五选");
                            }
                            cst.setCardId(cardMap.get("BookingGUID")+"");
                            cst.setCardName("卡"+NumberUtil.int2chineseNum(count));
                            cst.setProjectidFq(cardMap.get("p_projectfId")+"");
                            if (cardMap.get("x_OpenRoomBatchGUID")!=null){
                                cst.setBatchNo(cardMap.get("x_OpenRoomBatchGUID")+"");
                                cst.setBatchNoName(cardMap.get("x_OpenRoomBatchName")+"");
                                cst.setCardGrouping(cardMap.get("ProjPrefixCode")+"");
                                cst.setCardGroupingName(cardMap.get("ProjPrefix")+"");
                            }
                            if ("小卡".equals(cardMap.get("kpstate")+"")){
                                cst.setCardType("1");
                            }else{
                                cst.setCardType("2");
                            }
                            csts.add(cst);
                        }
                        intentionCstCard.setIntentionCsts(csts);
                        cards.add(intentionCstCard);
                        count++;
                    }
                }else{
                    IntentionCstCard intentionCstCard = new IntentionCstCard();
                    List<IntentionCst> csts = new ArrayList<>();
                    for (int i = 0; i < intentionCount; i++) {
                        IntentionCst cst = new IntentionCst();
                        cst.setProjectid(intentionCst.getProjectid());
                        cst.setOpportunityClueId(intentionCst.getOpportunityClueId());
                        cst.setProjectClueId(intentionCst.getProjectClueId());
                        cst.setIntentionId(intentionCst.getIntentionId());
                        cst.setCustomerName(intentionCst.getCustomerName());
                        cst.setCustomerMobile(intentionCst.getCustomerMobile());
                        cst.setProjectname(intentionCst.getProjectname());
                        cst.setSalesId(intentionCst.getSalesId());
                        cst.setSalesName(intentionCst.getSalesName());
                        cst.setIntentionLevelEdit((i+1)+"");
                        if (i==0){
                            cst.setIntentionLevelEditDesc("首选");
                        }else if (i==1){
                            cst.setIntentionLevelEditDesc("二选");
                        }else if (i==2){
                            cst.setIntentionLevelEditDesc("三选");
                        }else if (i==3){
                            cst.setIntentionLevelEditDesc("四选");
                        }else if (i==4){
                            cst.setIntentionLevelEditDesc("五选");
                        }
                        csts.add(cst);
                    }
                    intentionCstCard.setIntentionCsts(csts);
                    cards.add(intentionCstCard);
                }
            }
        }else{

            //查询是否存在装户范围卡片
            List<IntentionCst> intentionCstList1 = intentionPlaceDao.getCstCardList(map);
            //查询是否存在装户范围卡片
            if (intentionCstList1!=null && intentionCstList1.size()>0){
                for (IntentionCst csst:intentionCstList1) {
                    IntentionCstCard intentionCstCard = new IntentionCstCard();
                    intentionCstCard.setCardName(csst.getCardName());
                    intentionCstCard.setCardId(csst.getCardId());
                    List<IntentionCst> csts = new ArrayList<>();
                    for (int i = 0; i < intentionCount; i++) {
                        IntentionCst cst = new IntentionCst();
                        cst.setProjectid(intentionCst.getProjectid());
                        cst.setOpportunityClueId(intentionCst.getOpportunityClueId());
                        cst.setProjectClueId(intentionCst.getProjectClueId());
                        cst.setIntentionId(intentionCst.getIntentionId());
                        cst.setCustomerName(intentionCst.getCustomerName());
                        cst.setCustomerMobile(intentionCst.getCustomerMobile());
                        cst.setProjectname(intentionCst.getProjectname());
                        cst.setSalesId(intentionCst.getSalesId());
                        cst.setSalesName(intentionCst.getSalesName());
                        cst.setIntentionLevelEdit((i+1)+"");
                        if (i==0){
                            cst.setIntentionLevelEditDesc("首选");
                        }else if (i==1){
                            cst.setIntentionLevelEditDesc("二选");
                        }else if (i==2){
                            cst.setIntentionLevelEditDesc("三选");
                        }else if (i==3){
                            cst.setIntentionLevelEditDesc("四选");
                        }else if (i==4){
                            cst.setIntentionLevelEditDesc("五选");
                        }
                        cst.setCardId(csst.getCardId());
                        cst.setCardName(csst.getCardName());
                        cst.setProjectidFq(csst.getProjectidFq());
                        if (csst.getBatchNo()!=null){
                            cst.setBatchNo(csst.getBatchNo());
                            cst.setBatchNoName(csst.getBatchNoName());
                            cst.setCardGrouping(csst.getCardGrouping());
                            cst.setCardGroupingName(csst.getCardGroupingName());
                        }
                        cst.setCardType(csst.getCardType());
                        csts.add(cst);
                    }
                    intentionCstCard.setIntentionCsts(csts);
                    cards.add(intentionCstCard);
                }
            }else{
                String oppId = "";
                //判断是否自销系统
                if (flag){
                    oppId = intentionCst.getOpportunityClueId();
                }else{
                    oppId = intentionCst.getIntentionId();
                }
                //查询排卡数据
                List<Map<String, Object>> rowcards = this.getRowCardLists(intentionPlaceForm.getId(), intentionPlaceForm.getProjectid(), cardType, intentionPlaceForm.getActivityEndtime(),oppId,flag);
                if (rowcards!=null && rowcards.size()>0){
                    int count = 1;
                    for (Map cardMap:rowcards) {
                        IntentionCstCard intentionCstCard = new IntentionCstCard();
                        intentionCstCard.setCardName("卡"+ NumberUtil.int2chineseNum(count));
                        intentionCstCard.setCardId(cardMap.get("BookingGUID")+"");
                        List<IntentionCst> csts = new ArrayList<>();
                        for (int i = 0; i < intentionCount; i++) {
                            IntentionCst cst = new IntentionCst();
                            cst.setProjectid(intentionCst.getProjectid());
                            cst.setOpportunityClueId(intentionCst.getOpportunityClueId());
                            cst.setProjectClueId(intentionCst.getProjectClueId());
                            cst.setIntentionId(intentionCst.getIntentionId());
                            cst.setCustomerName(intentionCst.getCustomerName());
                            cst.setCustomerMobile(intentionCst.getCustomerMobile());
                            cst.setProjectname(intentionCst.getProjectname());
                            cst.setSalesId(intentionCst.getSalesId());
                            cst.setSalesName(intentionCst.getSalesName());
                            cst.setIntentionLevelEdit((i+1)+"");
                            if (i==0){
                                cst.setIntentionLevelEditDesc("首选");
                            }else if (i==1){
                                cst.setIntentionLevelEditDesc("二选");
                            }else if (i==2){
                                cst.setIntentionLevelEditDesc("三选");
                            }else if (i==3){
                                cst.setIntentionLevelEditDesc("四选");
                            }else if (i==4){
                                cst.setIntentionLevelEditDesc("五选");
                            }
                            cst.setCardId(cardMap.get("BookingGUID")+"");
                            cst.setCardName("卡"+NumberUtil.int2chineseNum(count));
                            cst.setProjectidFq(cardMap.get("p_projectfId")+"");
                            if (cardMap.get("x_OpenRoomBatchGUID")!=null){
                                cst.setBatchNo(cardMap.get("x_OpenRoomBatchGUID")+"");
                                cst.setBatchNoName(cardMap.get("x_OpenRoomBatchName")+"");
                                cst.setCardGrouping(cardMap.get("ProjPrefixCode")+"");
                                cst.setCardGroupingName(cardMap.get("ProjPrefix")+"");
                            }
                            if ("小卡".equals(cardMap.get("kpstate")+"")){
                                cst.setCardType("1");
                            }else{
                                cst.setCardType("2");
                            }
                            csts.add(cst);
                        }
                        intentionCstCard.setIntentionCsts(csts);
                        cards.add(intentionCstCard);
                        count++;
                    }
                }
            }
        }
        //将已装户的信息保存
        if (cstResult!=null && cstResult.size()>0){
            for (IntentionCstCard intentionCstCard:cards) {
                List<IntentionCst> cstss= intentionCstCard.getIntentionCsts();
                for (IntentionCst cst:cstss) {
                    for (Map resultMap:cstResult) {
                        if (resultMap.get("cardId")!=null && !"".equals(resultMap.get("cardId")+"")){
                            if (cst.getCardId()!=null && !"".equals(cst.getCardId())
                                    && cst.getIntentionLevelEdit().equals(resultMap.get("intentionLevelOld")+"")
                                    && cst.getCardId().equals(resultMap.get("cardId")+"") && resultMap.get("roomguidOld")!=null && !"".equals(resultMap.get("roomguidOld")+"")){
                                cst.setId(resultMap.get("id")+"");
                                cst.setRoomguidOld(resultMap.get("roomguidOld")+"");
                                cst.setRoomnameOld(resultMap.get("roomnameOld")+"");
                                cst.setIntentionLevelOld(resultMap.get("intentionLevelOld")+"");
                            }
                        }else {
                            if ((cst.getCardId()==null || "".equals(cst.getCardId()))
                                    && cst.getOpportunityClueId().equalsIgnoreCase(resultMap.get("opportunityClueId")+"")
                                    && cst.getIntentionLevelEdit().equals(resultMap.get("intentionLevelOld")+"")
                                    && resultMap.get("roomguidOld")!=null && !"".equals(resultMap.get("roomguidOld")+"")){
                                cst.setId(resultMap.get("id")+"");
                                cst.setRoomguidOld(resultMap.get("roomguidOld")+"");
                                cst.setRoomnameOld(resultMap.get("roomnameOld")+"");
                                cst.setIntentionLevelOld(resultMap.get("intentionLevelOld")+"");
                            }
                        }
                    }
                }
            }
        }
        intentionCst.setCards(cards);
        return intentionCst;
    }

    @Override
    public List<Map> getProSales(Map map) {
        return intentionPlaceDao.getProSales(map);
    }

    @Override
    public ResultBody addSalesMessage(Map map) {
        String activityId = map.get("activityId")+"";
        try{
            //查询活动数据
            IntentionPlaceForm intentionPlaceForm = intentionPlaceDao.getIntentionPlaceDetail(activityId);
            //查询项目下配置了调整通知的置业顾问
            List<String> salesIds = intentionPlaceDao.getEditMessageSales(intentionPlaceForm.getProjectid());
            //查询发生修改的装户记录
            if (salesIds!=null && salesIds.size()>0){
                Map param = new HashMap();
                param.put("projectId",intentionPlaceForm.getProjectid());
                param.put("activityId",activityId);
                param.put("salesIds",salesIds);
                List<String> messages = intentionPlaceDao.getEditRecord(param);
                if (messages!=null && messages.size()>0){
                    List<MessageForm> messageList = new ArrayList<>();
                    //添加置业顾问消息
                    for (String SaleID:messages) {
                        //查询已配置消息
                        Map messageTypeInfo = messageMapper.getUserMessageTypeInfo(SaleID,
                                intentionPlaceForm.getProjectid(),"zygw","31111");
                        String isRead = "1";
                        String isPush = "2";
                        if(messageTypeInfo != null) {
                            isRead = "0";
                            isPush = "1";
                        }
                        String content = "【装户调整通知】销售经理已调整你的"+intentionPlaceForm.getActivityName()+"(装户活动）的装户数据，请尽快查看处理";
                        MessageForm messageForm = new MessageForm();
                        messageForm.setSubject("装户调整通知");
                        messageForm.setContent(content);
                        messageForm.setProjectId(intentionPlaceForm.getProjectid());
                        messageForm.setProjectClueId(null);
                        messageForm.setOpportunityClueId(null);
                        messageForm.setReceiver(SaleID);
                        messageForm.setSender("admin");
                        messageForm.setMessageType(31111);
                        messageForm.setExt2("1");
                        messageForm.setIsRead(isRead);
                        messageForm.setIsPush(isPush);
                        JSONObject obj = new JSONObject();
                        obj.put("activityId",activityId);
                        obj.put("projectId",intentionPlaceForm.getProjectid());
                        obj.put("salesId",SaleID);
                        messageForm.setMessageData(JSONObject.toJSONString(obj));
                        messageList.add(messageForm);
                    }
                    messageMapper.insertMessageList(messageList);
                }
            }
            return ResultBody.success("通知成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-120005,"发生异常！！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateIntentionPlaceStatus(Map map) {
        try {
            String editor = SecurityUtils.getUserId();
            map.put("editor", editor);
            intentionPlaceDao.updateIntentionPlaceStatus(map);
            return ResultBody.success("更新成功！！");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003, "更新异常！！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addIntentionPlace(IntentionPlaceForm param) {
        String projectId = param.getProjectid();
        String projectName = param.getProjectname();
        String activityName = param.getActivityName();
        String creator = SecurityUtils.getUserId();
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            //查询城市
            Map cityMap = intentionPlaceDao.getCityByPro(projectId);
            String cityId = "";
            String cityName = "";
            if (cityMap != null && cityMap.get("ID") != null) {
                cityId = cityMap.get("ID") + "";
                cityName = cityMap.get("CityName") + "";
            }
            param.setCityId(cityId);
            param.setCityName(cityName);
            //保存装户活动
            String activityId = UUID.randomUUID().toString();
            param.setId(activityId);
            param.setCreator(creator);
            intentionPlaceDao.addIntentionPlace(param);
            List<IntentionPlaceMaterial> materials = param.getMaterialList();
            String materialid = "";
            if (materials != null && materials.size() > 0) {
                for (IntentionPlaceMaterial mm : materials) {
                    String mid = UUID.randomUUID().toString();
                    if (mm.getMaterialType().equals("2")) {
                        materialid = mid;
                    }
                    mm.setId(mid);
                    mm.setActivityId(activityId);
                    mm.setCreator(creator);
                }
                //保存素材
                intentionPlaceDao.addIntentionPlaceMaterial(materials);
            }

            //保存装户楼栋
            List<IntentionPlaceBuild> buildList = param.getBuildList();
            if (buildList != null && buildList.size() > 0) {
                for (IntentionPlaceBuild intentionBuild : buildList) {
                    String buildId = UUID.randomUUID().toString();
                    intentionBuild.setId(buildId);
                    intentionBuild.setActivityId(activityId);
                    intentionBuild.setProjectid(projectId);
                    intentionBuild.setProjectname(projectName);
                    List<BldUnit> units = intentionBuild.getUnitList();
                    double price = 0.00;
                    if (intentionBuild.getBuildprice() != null) {
                        price = Double.valueOf(intentionBuild.getBuildprice());
                    }
                    //保存装户单元
                    if (units != null && units.size() > 0) {
                        for (BldUnit unit : units) {
                            unit.setProjectid(projectId);
                            unit.setActivityId(activityId);
                            unit.setCreator(creator);
                            //保存装户房间
                            List<IntentionPlaceRoom> rooms = unit.getRoomList();
                            if (rooms != null && rooms.size() > 0) {
                                for (IntentionPlaceRoom room : rooms) {
                                    //如果选择预估价
                                    if ("2".equals(param.getShowpriceType()) && room.getFloorArea() != null && !"".equals(room.getFloorArea())) {
                                        room.setConfigureTotalPrice(df.format(price * (Double.valueOf(room.getFloorArea()))));
                                    } else {
                                        room.setConfigureTotalPrice(0.00 + "");
                                    }
                                    room.setProjectname(projectName);
                                    room.setProjectid(projectId);
                                    room.setCreator(creator);
                                    room.setActivityId(activityId);
                                    room.setActivityName(activityName);
                                    room.setPlacebuildId(buildId);
                                }
                                intentionPlaceDao.addIntentionPlaceRoom(rooms);
                                intentionPlaceDao.addResultEdit(rooms);
                            }
                        }
                        intentionPlaceDao.addIntentionPlaceUnit(units);
                    }
                }
                intentionPlaceDao.addIntentionPlaceBuild(buildList);
            }
            //保存排卡分组（如果选择了排大卡）
//                        if (param.getBatchEndList()!=null && param.getBatchEndList().size()>0){
//                                List<IntentionPlaceCardGroup> cardGroups = new ArrayList<>();
//                                //前端传回的排卡批次和分组
//                                List<ResultProjectVO> old = param.getBatchList();
//                                for (List<String> list:param.getBatchEndList()) {
//                                        //判断是否选择开盘批次（等于一则只选择了分期）
//                                     if (list.size()>1){
//                                             String projectFname = "";
//                                             for (ResultProjectVO vo:old) {
//                                                     if (list.get(0).equals(vo.getValue())){
//                                                             projectFname =  vo.getLabel();
//                                                     }
//                                                     for (ResultProjectVO bb:vo.getChildren()) {
//
//                                                     }
//                                             }
//                                     } else{
//                                             String projectFname = "";
//                                             for (ResultProjectVO vo:old) {
//                                                if (list.get(0).equals(vo.getValue())){
//                                                        projectFname =  vo.getLabel();
//                                                }
//                                             }
//                                             IntentionPlaceCardGroup intentionPlaceCardGroup = new IntentionPlaceCardGroup();
//                                             intentionPlaceCardGroup.setActivityId(activityId);
//                                             intentionPlaceCardGroup.setCreator(creator);
//                                             intentionPlaceCardGroup.setProjectid(projectId);
//                                             intentionPlaceCardGroup.setProjectFid(list.get(0));
//                                             intentionPlaceCardGroup.setProjectFname(projectFname);
//                                             intentionPlaceCardGroup.setCardType("2");
//                                             cardGroups.add(intentionPlaceCardGroup);
//                                     }
//                                }
//                        }
            List<Map> buildSites = param.getBuildSites();
            if (buildSites != null && buildSites.size() > 0) {
                for (Map sit : buildSites) {
                    sit.put("activityId", activityId);
                    sit.put("materialid", materialid);
                }
                intentionPlaceDao.addBuildSite(buildSites);
            }
            if (param.getBatchList() != null && param.getBatchList().size() > 0) {
                List<IntentionPlaceCardGroup> cardGroups = new ArrayList<>();
                for (ProBatchVO pro : param.getBatchList()) {
                    if (pro.getChildren() == null || pro.getChildren().size() == 0) {
                        if (pro.getIsChecked().equals("1")) {
                            IntentionPlaceCardGroup intentionPlaceCardGroup = new IntentionPlaceCardGroup();
                            intentionPlaceCardGroup.setActivityId(activityId);
                            intentionPlaceCardGroup.setCreator(creator);
                            intentionPlaceCardGroup.setProjectid(projectId);
                            intentionPlaceCardGroup.setProjectFid(pro.getValue());
                            intentionPlaceCardGroup.setProjectFname(pro.getLabel());
                            intentionPlaceCardGroup.setCardType("2");
                            cardGroups.add(intentionPlaceCardGroup);
                        }
                    } else {
                        for (ProBatchVO open : pro.getChildren()) {
                            if (open.getIsChecked().equals("1")) {
                                for (ProBatchVO cardGroup : open.getChildren()) {
                                    if (cardGroup.getIsChecked().equals("1")) {
                                        IntentionPlaceCardGroup intentionPlaceCardGroup = new IntentionPlaceCardGroup();
                                        intentionPlaceCardGroup.setActivityId(activityId);
                                        intentionPlaceCardGroup.setCreator(creator);
                                        intentionPlaceCardGroup.setProjectid(projectId);
                                        intentionPlaceCardGroup.setProjectFid(pro.getValue());
                                        intentionPlaceCardGroup.setProjectFname(pro.getLabel());
                                        intentionPlaceCardGroup.setOpeningBatch(open.getValue());
                                        intentionPlaceCardGroup.setOpeningBatchName(open.getLabel());
                                        intentionPlaceCardGroup.setCardGrouping(cardGroup.getValue());
                                        intentionPlaceCardGroup.setCardGroupingName(cardGroup.getLabel());
                                        intentionPlaceCardGroup.setCardType("2");
                                        cardGroups.add(intentionPlaceCardGroup);
                                    }

                                }
                            }

                        }
                    }

                }
                intentionPlaceDao.addIntentionPlaceCardGroup(cardGroups);
            }
            //如果选择了排小卡
            if (param.getFprojectList() != null && param.getFprojectList().size() > 0) {
                List<IntentionPlaceCardGroup> cardGroups = new ArrayList<>();
                for (String pro : param.getFprojectList()) {
                    IntentionPlaceCardGroup intentionPlaceCardGroup = new IntentionPlaceCardGroup();
                    intentionPlaceCardGroup.setActivityId(activityId);
                    intentionPlaceCardGroup.setCreator(creator);
                    intentionPlaceCardGroup.setProjectid(projectId);
                    String[] proIdsss = pro.split(",");
                    intentionPlaceCardGroup.setProjectFid(proIdsss[0]);
                    intentionPlaceCardGroup.setProjectFname(proIdsss[1]);
                    intentionPlaceCardGroup.setCardType("1");
                    cardGroups.add(intentionPlaceCardGroup);
                }
                intentionPlaceDao.addIntentionPlaceCardGroup(cardGroups);
            }
            return ResultBody.success("保存装户活动成功！！");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003, "保存装户活动异常！！");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateIntentionPlace(IntentionPlaceForm param) {
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            String creator = SecurityUtils.getUserId();
            String projectId = param.getProjectid();
            String projectName = param.getProjectname();
            String activityId = param.getId();
            String activityName = param.getActivityName();
            //更新活动
            param.setEditor(creator);

            //查询城市
            Map cityMap = intentionPlaceDao.getCityByPro(projectId);
            String cityId = "";
            String cityName = "";
            if (cityMap != null && cityMap.get("ID") != null) {
                cityId = cityMap.get("ID") + "";
                cityName = cityMap.get("CityName") + "";
            }
            param.setCityId(cityId);
            param.setCityName(cityName);
            intentionPlaceDao.updateIntentionPlace(param);
            //更新活动素材
            String materialid = "";
            List<IntentionPlaceMaterial> materials = param.getMaterialList();
            for (IntentionPlaceMaterial mater : materials) {
                if (mater.getMaterialType().equals("2")) {
                    materialid = mater.getId();
                    //判断是否上传了概览图
                    if (mater.getEndPhotoUrl() != null && !"".equals(mater.getEndPhotoUrl())) {
                        //存在即删除文件
                        String fileName = this.getFileName(mater.getEndPhotoUrl(), "1");
                        String filePath = path + fileName;
                        File file = new File(filePath);
                        if (file != null) {
                            file.delete();
                        }
                    }
                }
                mater.setEditor(creator);
            }
            if (materials != null && materials.size() > 0) {
                intentionPlaceDao.updateIntentionPlaceMaterial(materials);
            }
            intentionPlaceDao.delBuildSite(activityId);
            List<Map> buildSites = param.getBuildSites();
            if (buildSites != null && buildSites.size() > 0) {
                for (Map sit : buildSites) {
                    sit.put("activityId", activityId);
                    sit.put("materialid", materialid);
                }
                intentionPlaceDao.addBuildSite(buildSites);
            }
            //更新活动排卡分组
            intentionPlaceDao.deleteIntentionPlaceCardGroup(param.getId());
            //保存排卡分组（如果选择了排大卡）
            if (param.getBatchList() != null && param.getBatchList().size() > 0) {
                List<IntentionPlaceCardGroup> cardGroups = new ArrayList<>();
                for (ProBatchVO pro : param.getBatchList()) {
                    if (pro.getChildren() == null || pro.getChildren().size() == 0) {
                        if (pro.getIsChecked().equals("1")) {
                            IntentionPlaceCardGroup intentionPlaceCardGroup = new IntentionPlaceCardGroup();
                            intentionPlaceCardGroup.setActivityId(activityId);
                            intentionPlaceCardGroup.setCreator(creator);
                            intentionPlaceCardGroup.setProjectid(projectId);
                            intentionPlaceCardGroup.setProjectFid(pro.getValue());
                            intentionPlaceCardGroup.setProjectFname(pro.getLabel());
                            intentionPlaceCardGroup.setCardType("2");
                            cardGroups.add(intentionPlaceCardGroup);
                        }
                    } else {
                        for (ProBatchVO open : pro.getChildren()) {
                            if (open.getIsChecked().equals("1")) {
                                for (ProBatchVO cardGroup : open.getChildren()) {
                                    if (cardGroup.getIsChecked().equals("1")) {
                                        IntentionPlaceCardGroup intentionPlaceCardGroup = new IntentionPlaceCardGroup();
                                        intentionPlaceCardGroup.setActivityId(activityId);
                                        intentionPlaceCardGroup.setCreator(creator);
                                        intentionPlaceCardGroup.setProjectid(projectId);
                                        intentionPlaceCardGroup.setProjectFid(pro.getValue());
                                        intentionPlaceCardGroup.setProjectFname(pro.getLabel());
                                        intentionPlaceCardGroup.setOpeningBatch(open.getValue());
                                        intentionPlaceCardGroup.setOpeningBatchName(open.getLabel());
                                        intentionPlaceCardGroup.setCardGrouping(cardGroup.getValue());
                                        intentionPlaceCardGroup.setCardGroupingName(cardGroup.getLabel());
                                        intentionPlaceCardGroup.setCardType("2");
                                        cardGroups.add(intentionPlaceCardGroup);
                                    }

                                }
                            }

                        }
                    }

                }
                intentionPlaceDao.addIntentionPlaceCardGroup(cardGroups);
            }
            //如果选择了排小卡
            if (param.getFprojectList() != null && param.getFprojectList().size() > 0) {
                List<IntentionPlaceCardGroup> cardGroups = new ArrayList<>();
                for (String pro : param.getFprojectList()) {
                    IntentionPlaceCardGroup intentionPlaceCardGroup = new IntentionPlaceCardGroup();
                    intentionPlaceCardGroup.setActivityId(activityId);
                    intentionPlaceCardGroup.setCreator(creator);
                    intentionPlaceCardGroup.setProjectid(projectId);
                    String[] proIdsss = pro.split(",");
                    intentionPlaceCardGroup.setProjectFid(proIdsss[0]);
                    intentionPlaceCardGroup.setProjectFname(proIdsss[1]);
                    intentionPlaceCardGroup.setCardType("1");
                    cardGroups.add(intentionPlaceCardGroup);
                }
                intentionPlaceDao.addIntentionPlaceCardGroup(cardGroups);
            }
            //现在需要绑定的楼栋
            List<IntentionPlaceBuild> newBuilds = param.getBuildList();
            List<String> newBuildId = new ArrayList<>();
            for (IntentionPlaceBuild inten : newBuilds) {
                newBuildId.add(inten.getBuildguid());
            }
            //获取原有楼栋
            List<String> buildEditResult = intentionPlaceDao.getOldIntentionPlaceBuildID(activityId);
            for (int i = 0; i < newBuildId.size(); i++) {
                //删除再次选中的楼栋
                if (buildEditResult.contains(newBuildId.get(i))) {
                    buildEditResult.remove(newBuildId.get(i));
                    newBuildId.remove(i);
                    i--;
                }
            }
            //如果还存在原楼栋ID，则删除（现选楼栋不包含的）
            if (buildEditResult.size() > 0) {
                for (String id : buildEditResult) {
                    intentionPlaceDao.deleteEditResult(activityId, id);
                }
            }
            //删除原有楼栋房间
            intentionPlaceDao.deleteIntentionPlaceBuildAll(activityId);
            //保存最新房间数据
            if (newBuilds != null && newBuilds.size() > 0) {
                for (IntentionPlaceBuild intentionBuild : newBuilds) {
                    String buildId = UUID.randomUUID().toString();
                    intentionBuild.setId(buildId);
                    intentionBuild.setProjectid(projectId);
                    intentionBuild.setActivityId(activityId);
                    intentionBuild.setProjectname(projectName);
                    List<BldUnit> units = intentionBuild.getUnitList();
                    double price = 0.00;
                    if (intentionBuild.getBuildprice() != null) {
                        price = Double.valueOf(intentionBuild.getBuildprice());
                    }
                    //保存装户单元
                    if (units != null && units.size() > 0) {
                        for (BldUnit unit : units) {
                            unit.setProjectid(projectId);
                            unit.setActivityId(activityId);
                            unit.setCreator(creator);
                            //保存装户房间
                            List<IntentionPlaceRoom> rooms = unit.getRoomList();
                            //装户调整结果房间
                            List<IntentionPlaceRoom> roomsEditNew = new ArrayList<>();
                            if (rooms != null && rooms.size() > 0) {
                                for (IntentionPlaceRoom room : rooms) {
                                    //如果选择预估价
                                    if ("2".equals(param.getShowpriceType()) && room.getFloorArea() != null && !"".equals(room.getFloorArea())) {
                                        room.setConfigureTotalPrice(df.format(price * (Double.valueOf(room.getFloorArea()))));
                                    } else {
                                        room.setConfigureTotalPrice(0.00 + "");
                                    }
                                    room.setProjectname(projectName);
                                    room.setProjectid(projectId);
                                    room.setCreator(creator);
                                    room.setActivityId(activityId);
                                    room.setActivityName(activityName);
                                    room.setPlacebuildId(buildId);
                                    if (newBuildId.size() > 0 && newBuildId.contains(room.getBuildguid())) {
                                        roomsEditNew.add(room);
                                    }
                                }
                                intentionPlaceDao.addIntentionPlaceRoom(rooms);
                                if (roomsEditNew.size() > 0) {
                                    intentionPlaceDao.addResultEdit(roomsEditNew);
                                }

                            }
                        }
                        intentionPlaceDao.addIntentionPlaceUnit(units);
                    }
                }
                intentionPlaceDao.addIntentionPlaceBuild(newBuilds);
            }

            return ResultBody.success("更新成功！！");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(-21_0003, "更新异常！！");
        }
    }

    @Override
    public List<Map<String, Object>> getBldingByPro(String projectId) {
        List<Map<String, Object>> mapList = new ArrayList<>();
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
                return mapList;
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

        List<Map<String, Object>> bldMaps = new ArrayList<>();
        List<Map> pro = intentionPlaceDao.getFProject(projectId);
        if (pro.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Map map : pro) {
                sb.append("'" + map.get("id") + "',");
            }
            String ids = sb.toString().substring(0, sb.toString().length() - 1);

            //根据项目查询所有楼栋
            String sql = "select DISTINCT BldGUID,BldName,OrderCode from VS_XK_S_BUILDING  where PrjectFGUID in (" + ids + ") and UnitNo is not null ORDER BY OrderCode";
            // 判断是查询自销系统还是明源系统 true: 自销系统 false: 明源系统
            if (flag) {
                bldMaps = housingManagementDao.getDataList(sql);
            }
            else {
                bldMaps = DbTest.getObjects(sql);
            }

        }
        return bldMaps;
    }

    public List<IntentionPlaceBuild> getOldRoomList(String activityId, String buildguid) {
        Map param = new HashMap();
        param.put("id", activityId);
        param.put("buildguid", buildguid);
        //查询是否移动装户
        IntentionPlaceForm intentionPlaceForm = intentionPlaceDao.getIntentionPlaceDetail(activityId);
        List<IntentionPlaceBuild> builds = new ArrayList<>();
        builds = intentionPlaceDao.getIntentionPlaceBuild(param);
        List<BldUnit> units = new ArrayList<>();
        units = intentionPlaceDao.getIntentionPlaceUnit(param);
        List<IntentionPlaceRoom> roomList = new ArrayList<>();
        roomList = intentionPlaceDao.getIntentionPlaceRoom(activityId, buildguid);
        if (builds.size() > 0) {
            if (units.size() > 0) {
                for (IntentionPlaceBuild build : builds) {
                    List<BldUnit> units1 = new ArrayList<>();
                    for (BldUnit unit : units) {
                        if (unit.getBuildguid().equals(build.getBuildguid())) {
                            List<IntentionPlaceRoom> roomList1 = new ArrayList<>();
                            for (IntentionPlaceRoom room : roomList) {
                                if (room.getUnitno().equals(unit.getUnitno()) && room.getBuildguid().equals(unit.getBuildguid())) {
                                    roomList1.add(room);
                                }
                            }
                            unit.setRoomList(roomList1);
                            units1.add(unit);
                        }
                    }
                    build.setUnitList(units1);
                }
            }

        }
        return builds;
    }

    public List<IntentionPlaceBuild> getNewRoom(String bldIds) {
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
                return new ArrayList<>();
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

        List<IntentionPlaceBuild> builds = new ArrayList<>();
        //新增时查询明源
        String[] ids = bldIds.split(",");
        String buildIds = "'" + StringUtils.join(ids, "','") + "'";
        //获取楼栋数据
        List<Map<String, Object>> bldMaps = null;
        String sql = "select DISTINCT BldGUID,BldName,PrjectGUID,PrjectFGUID,Concat(KindeeProjectNAME,KindeeProjectFNAME) ProjectFNAME,BldType,OrderCode from VS_XK_S_BUILDING  where BldGUID in (" + buildIds + ") ORDER BY OrderCode";
        //判断是否自销调用
        if (flag){
            bldMaps = housingManagementDao.getDataList(sql);
        }else{
            bldMaps = DbTest.getObjects(sql);
        }

        //获取楼栋数据
//        String sql = "select DISTINCT BldGUID,BldName,PrjectGUID,PrjectFGUID,Concat(KindeeProjectNAME,KindeeProjectFNAME) ProjectFNAME,BldType,OrderCode from VS_XK_S_BUILDING  where BldGUID in (" + buildIds + ") ORDER BY OrderCode";
//        List<Map<String, Object>> bldMaps = DbTest.getObjects(sql);
        for (Map<String, Object> map : bldMaps) {
            IntentionPlaceBuild intentionPlaceBuild = new IntentionPlaceBuild();
            intentionPlaceBuild.setBuildguid(map.get("BldGUID") + "");
            intentionPlaceBuild.setBuildname(map.get("BldName") + "");
            intentionPlaceBuild.setOrderby(map.get("OrderCode") + "");
            intentionPlaceBuild.setProjectidFq(map.get("PrjectFGUID") + "");
            intentionPlaceBuild.setProjectnameFq(map.get("ProjectFNAME") + "");
            builds.add(intentionPlaceBuild);
        }
        if (builds.size() > 0) {
            //根据楼栋获取单元信息
            List<Map<String, Object>> result = null;
            String sql1 = "select DISTINCT BldGUID,UnitNo,UnitName,BldUnitGUID,PrjectGUID from VS_XK_S_BUILDING  where BldGUID in (" + buildIds + ") and UnitNo is not null order by UnitNo";
            //判断是否自销调用
            if (flag){
                result = housingManagementDao.getDataList(sql1);
            }else{
                result = DbTest.getObjects(sql1);
            }
//            String sql1 = "select DISTINCT BldGUID,UnitNo,UnitName,BldUnitGUID,PrjectGUID from VS_XK_S_BUILDING  where BldGUID in (" + buildIds + ") and UnitNo is not null order by cast(UnitNo as int)";
//            List<Map<String, Object>> result = DbTest.getObjects(
//                    sql1);

            List<Map<String, Object>> roomMaps = null;
            String roomSql = "";
            //判断是否自销调用
            if (flag){
                roomSql = "SELECT room.FloorNo FloorNo,room.FloorName,room.Room,room.RoomInfo,room.RoomNo as No,room.RoomGUID,room.RoomStru,room.StatusEnum,room.UnitNo,room.BldGUID,room.BldName,room.HxName,room.HxGUID,room.BldArea,room.Price,room.TnArea,room.TnPrice,room.Total,room.PrjectGUID,room.PrjectFGUID,Concat(room.KindeeProjectNAME,room.KindeeProjectFNAME) ProjectFNAME FROM VS_XK_S_ROOM room  WHERE room.BldGUID in (" + buildIds + ") order by room.UnitNo,room.FloorNo desc,room.RoomNo";
                roomMaps = housingManagementDao.getDataList(roomSql);
            }else{
                roomSql = "SELECT CAST ( room.FloorNo AS INT ) FloorNo,room.FloorName,room.Room,room.RoomInfo,room.RoomNo as No,room.RoomGUID,room.RoomStru,room.StatusEnum,room.UnitNo,room.BldGUID,room.BldName,room.HxName,room.HxGUID,room.BldArea,room.Price,room.TnArea,room.TnPrice,room.Total,room.PrjectGUID,room.PrjectFGUID,Concat(room.KindeeProjectNAME,room.KindeeProjectFNAME) ProjectFNAME FROM VS_XK_S_ROOM room  WHERE room.BldGUID in (" + buildIds + ") order by room.UnitNo,CAST ( room.FloorNo AS INT ) desc,room.RoomNo";
                roomMaps = DbTest.getObjects(roomSql);
            }
            //根据楼栋单元获取房间SQL语句
//            String roomSql = "SELECT CAST ( room.FloorNo AS INT ) FloorNo,room.FloorName,room.Room,room.RoomInfo,room.RoomNo as No,room.RoomGUID,room.RoomStru,room.StatusEnum,room.UnitNo,room.BldGUID,room.BldName,room.HxName,room.HxGUID,room.BldArea,room.Price,room.TnArea,room.TnPrice,room.Total,room.PrjectGUID,room.PrjectFGUID,Concat(room.KindeeProjectNAME,room.KindeeProjectFNAME) ProjectFNAME FROM VS_XK_S_ROOM room  WHERE room.BldGUID in (" + buildIds + ") order by room.UnitNo,CAST ( room.FloorNo AS INT ) desc,room.RoomNo";
//
//            List<Map<String, Object>> roomMaps = DbTest.getObjects(roomSql);
            for (IntentionPlaceBuild intention : builds) {
                List<BldUnit> units = new ArrayList<>();
                for (Map<String, Object> map : result) {
                    if (intention.getBuildguid().equals(map.get("BldGUID") + "")) {
                        //获取楼层房间最大数
                        int maxCount = 0;
                        List<Map<String, Object>> countMaps = null;
                        String floorSql = "";
                        //判断是否自销调用
                        if (flag){
                            floorSql = "select FloorNo,max(RoomNo) count from VS_XK_S_ROOM where BldGUID = '" + intention.getBuildguid() + "' AND UnitNo = '" + map.get("UnitNo") + "' group by FloorNo order by count desc limit 1";
                            countMaps = housingManagementDao.getDataList(floorSql);
                        }else{
                            floorSql = "select top 1 FloorNo,max(cast(RoomNo as int)) count from VS_XK_S_ROOM where BldGUID = '" + intention.getBuildguid() + "' AND UnitNo = '" + map.get("UnitNo") + "' group by FloorNo order by count desc";
                            countMaps = DbTest.getObjects(floorSql);
                        }
//                        String floorSql = "select top 1 FloorNo,max(cast(RoomNo as int)) count from VS_XK_S_ROOM where BldGUID = '" + intention.getBuildguid() + "' AND UnitNo = '" + map.get("UnitNo") + "' group by FloorNo order by count desc";
//                        Map<String, Object> countMap = DbTest.getObject(floorSql);
                        if (null != countMaps && countMaps.size()>0) {
                            Map<String, Object> countMap = countMaps.get(0);
                            if (null != countMap.get("count") && !"".equals(countMap.get("count"))) {
                                if (flag){
                                    maxCount = Integer.valueOf(countMap.get("count") + "");
                                }else {
                                    maxCount = Integer.valueOf(countMap.get("count") + "") + 1;
                                }
                            }
                        }
                        BldUnit bldUnit = new BldUnit();
                        bldUnit.setBuildguid(map.get("BldGUID") + "");
                        bldUnit.setBldunitguid(map.get("BldUnitGUID") + "");
                        bldUnit.setUnitname(map.get("UnitName") + "");
                        bldUnit.setUnitno(map.get("UnitNo") + "");
                        bldUnit.setMaxroomcount(maxCount);
                        List<IntentionPlaceRoom> roomList = new ArrayList<>();
                        for (Map<String, Object> map2 : roomMaps) {
                            if (intention.getBuildguid().equals(map2.get("BldGUID") + "") && bldUnit.getUnitno().equals(map2.get("UnitNo") + "")) {
                                IntentionPlaceRoom intentionPlaceRoom = new IntentionPlaceRoom();
                                intentionPlaceRoom.setBuildguid(map2.get("BldGUID") + "");
                                intentionPlaceRoom.setBuildname(map2.get("BldName") + "");
                                intentionPlaceRoom.setFloor(map2.get("FloorName") + "");
                                String floorNoS = map2.get("FloorNo") + "";
                                int floorNo = 0;
                                String noS = map2.get("No") + "";
                                int no = 0;
                                if (null != floorNoS && !"".equals(floorNoS)) {
                                    if ("0".equals(floorNoS)) {
                                        floorNo = 1;
                                    } else {
                                        floorNo = Integer.parseInt(floorNoS);
                                    }

                                }
                                if (null != noS && !"".equals(noS)) {
                                    if (flag){
                                        no = Integer.parseInt(noS);
                                    }else {
                                        no = Integer.parseInt(noS) + 1;
                                    }
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
                                if (map2.get("RoomStru") != null) {
                                    roomType = map2.get("RoomStru") + "";
                                }
                                intentionPlaceRoom.setRoomType(roomType);
                                intentionPlaceRoom.setUnitno(map2.get("UnitNo") + "");
                                intentionPlaceRoom.setHouseType(map2.get("HxName") + "");
                                intentionPlaceRoom.setHouseTypeId(map2.get("HxGUID") + "");
                                String salesStatus = "";
                                //(1:待售2:认购3:销控4:签约5：预留)-->(1:待售 2:已售 3:销控)
                                if ("1".equals(map2.get("StatusEnum") + "")) {
                                    salesStatus = "1";
                                } else if ("2".equals(map2.get("StatusEnum") + "") || "4".equals(map2.get("StatusEnum") + "")) {
                                    salesStatus = "2";
                                } else if ("3".equals(map2.get("StatusEnum") + "") || "5".equals(map2.get("StatusEnum") + "")) {
                                    salesStatus = "3";
                                }
                                intentionPlaceRoom.setSaleStatus(salesStatus);
                                intentionPlaceRoom.setStatus("1");//默认可装户
                                intentionPlaceRoom.setTotalPrice(map2.get("Total") + "");
                                roomList.add(intentionPlaceRoom);
                            }
                        }
                        bldUnit.setRoomList(roomList);
                        units.add(bldUnit);
                    }
                }
                intention.setUnitList(units);
            }

        }

        return builds;
    }





    public String getFileName(String url, String type) {
        String fileName = "";
        if (StringUtils.isNotBlank(url)) {
            //获得第一个点的位置
            int index = url.lastIndexOf("/");
            //根据第一个点的位置 获得第二个点的位置
            index = url.lastIndexOf("/", index - 1);
            //根据第二个点的位置，截取 字符串。得到结果 result
            if ("1".equals(type)) {
                fileName = url.substring(index + 1);
            } else {
                fileName = url.substring(index);
            }
        }
        return fileName;
    }



}
