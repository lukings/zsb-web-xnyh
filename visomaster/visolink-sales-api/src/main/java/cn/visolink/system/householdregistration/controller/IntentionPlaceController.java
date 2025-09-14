package cn.visolink.system.householdregistration.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.householdregistration.model.IntentionPlaceBuild;
import cn.visolink.system.householdregistration.model.IntentionPlaceResult;
import cn.visolink.system.householdregistration.model.form.IntentionPlaceForm;
import cn.visolink.system.householdregistration.model.vo.IntentionPlaceVO;
import cn.visolink.system.householdregistration.service.IntentionPlaceService;
import cn.visolink.system.householdregistration.model.*;
import cn.visolink.utils.StringUtils;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * IntentionPlace前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2020-07-29
 */
@RestController
@Api(tags = "IntentionPlace")
@RequestMapping("/householdregistration/intentionPlace")
public class IntentionPlaceController {

    @Autowired
    private IntentionPlaceService intentionPlaceService;
    /**
    * 分页查询
    * @param param 查询条件
    * @return 查询结果
    */
    @Log("分页查询IntentionPlace")
    @CessBody
    @ApiOperation(value = "分页查询", notes = "分页查询IntentionPlace全部数据")
    @RequestMapping(value = "/getIntentionPlacePage", method = RequestMethod.POST)
    public ResultBody getIntentionPlacePage(@RequestBody(required = false) IntentionPlaceForm param){
        PageInfo<IntentionPlaceVO> result= intentionPlaceService.selectPage(param);
            return ResultBody.success(result);
    }

    @Log("保存活动")
    @CessBody
    @ApiOperation(value = "保存活动", notes = "保存活动")
    @RequestMapping(value = "/addIntentionPlace", method = RequestMethod.POST)
    public ResultBody addIntentionPlace(@RequestBody(required = false) IntentionPlaceForm param){
        return intentionPlaceService.addIntentionPlace(param);
    }

    @Log("更新活动")
    @CessBody
    @ApiOperation(value = "更新活动", notes = "更新活动")
    @RequestMapping(value = "/updateIntentionPlace", method = RequestMethod.POST)
    public ResultBody updateIntentionPlace(@RequestBody(required = false) IntentionPlaceForm param){
        return intentionPlaceService.updateIntentionPlace(param);
    }

    @Log("导出活动数据")
    @CessBody
    @ApiOperation(value = "导出活动数据", notes = "导出活动数据")
    @RequestMapping(value = "/intentionPlaceExport", method = RequestMethod.POST)
    public void intentionPlaceExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param){
        intentionPlaceService.intentionPlaceExport(request,response, param);
    }

    /**
     * 查询项目下楼栋
     * @param projectId 查询条件
     * @return 查询结果
     */
    @Log("查询项目下楼栋")
    @CessBody
    @ApiOperation(value = "查询项目下楼栋", notes = "查询项目下楼栋")
    @RequestMapping(value = "/getBldingByPro", method = RequestMethod.POST)
    public ResultBody getBldingByPro(@ApiParam(name = "projectId", value = "项目ID")String projectId){
//        List<Map> result= new ArrayList<>();
//        Map map1 = new HashMap();
//        map1.put("BldGUID","8C11A25C-F89A-E811-80BB-005056A21B76");
//        map1.put("BldName","1#住宅楼");
//        map1.put("OrderCode","00000001");
//        Map map2 = new HashMap();
//        map1.put("BldGUID","8E11A25C-F89A-E811-80BB-005056A21B76");
//        map1.put("BldName","1#住宅楼地下");
//        map1.put("OrderCode","00000001");
//        Map map3 = new HashMap();
//        map1.put("BldGUID","9A11A25C-F89A-E811-80BB-005056A21B76");
//        map1.put("BldName","2#住宅楼");
//        map1.put("OrderCode","00000002");
//        result.add(map1);
//        result.add(map2);
//        result.add(map3);
        List<Map<String,Object>> result= intentionPlaceService.getBldingByPro(projectId);
        return ResultBody.success(result);
    }

    /**
     * 查询楼栋下的房间
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询楼栋下的房间")
    @CessBody
    @ApiOperation(value = "查询楼栋下的房间", notes = "查询楼栋下的房间")
    @RequestMapping(value = "/getBldingRoomList", method = RequestMethod.POST)
    public ResultBody getBldingRoomList(@ApiParam(name = "map", value = "{\"activityId\":\"活动ID(查看时只传活动ID，编辑楼栋时楼栋ID也传，新增时只传楼栋ID)\",\"bldIds\":\"楼栋ID逗号隔开\"}")
                                            @RequestBody Map map){
        String bldIds = null;
        String activityId = null;
        if (map.get("activityId")!=null){
            activityId = map.get("activityId")+"";
        }
        if (map.get("bldIds")!=null){
            bldIds = map.get("bldIds")+"";
        }
        List<IntentionPlaceBuild> result= intentionPlaceService.getBldingRoomList(bldIds,activityId);
        return ResultBody.success(result);
    }

    /**
     * 查询分期项目
     * @param projectId 查询条件
     * @return 查询结果
     */
    @Log("查询分期项目")
    @CessBody
    @ApiOperation(value = "查询分期项目", notes = "查询分期项目")
    @RequestMapping(value = "/getFProject", method = RequestMethod.POST)
    public ResultBody getFProject(@ApiParam(name = "projectId", value = "主项目ID")
                                                String projectId){
        List<Map> result= intentionPlaceService.getFProject(projectId);
        return ResultBody.success(result);
    }

    /**
     * 查询分期项目下排卡分组
     * @param projectId 查询条件
     * @return 查询结果
     */
    @Log("查询分期项目")
    @CessBody
    @ApiOperation(value = "查询分期项目下排卡分组", notes = "查询分期项目下排卡分组")
    @RequestMapping(value = "/getFProjectCardGroup", method = RequestMethod.POST)
    public ResultBody getFProjectCardGroup(@ApiParam(name = "projectId", value = "项目ID")
                                          String projectId){
        List<Map> result= intentionPlaceService.getFProjectCardGroup(projectId);
        return ResultBody.success(result);
    }

    /**
     * 更新装户活动状态
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("更新装户活动状态")
    @CessBody
    @ApiOperation(value = "更新装户活动状态", notes = "更新装户活动状态")
    @RequestMapping(value = "/updateIntentionPlaceStatus", method = RequestMethod.POST)
    public ResultBody updateIntentionPlaceStatus(@ApiParam(name = "map", value = "{\"id\":\"活动id\",\"status\":\"状态（0禁用，1启用）\",\"isdel\":\"是否删除:1已删除，0未删除\",\"actStatus\":\"活动状态:1草稿，2已发布\"}")
                                                     @RequestBody Map map){
        return intentionPlaceService.updateIntentionPlaceStatus(map);
    }

    /**
     * 查询装户详情
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询装户详情")
    @CessBody
    @ApiOperation(value = "查询装户详情", notes = "查询装户详情")
    @RequestMapping(value = "/getIntentionPlaceDetail", method = RequestMethod.POST)
    public ResultBody getIntentionPlaceDetail(@ApiParam(name = "map", value = "{\"id\":\"活动id\"}")
                                                 @RequestBody Map map){
        return intentionPlaceService.getIntentionPlaceDetail(map);
    }

    /**
     * 分页查询装户明细
     * @param param 查询条件
     * @return 查询结果
     */
    @Log("分页查询IntentionPlaceResult")
    @CessBody
    @ApiOperation(value = "分页查询", notes = "分页查询IntentionPlaceResult")
    @RequestMapping(value = "/getIntentionPlaceResultPage", method = RequestMethod.POST)
    public ResultBody getIntentionPlaceResultPage(@RequestBody(required = false) IntentionPlaceResult param){
        PageInfo<IntentionPlaceResult> result= intentionPlaceService.getIntentionPlaceResultPage(param);
        return ResultBody.success(result);
    }

    @Log("导出装户明细数据")
    @CessBody
    @ApiOperation(value = "导出装户明细数据", notes = "导出装户明细数据")
    @RequestMapping(value = "/intentionPlaceResultExport", method = RequestMethod.POST)
    public void intentionPlaceResultExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param){
        intentionPlaceService.intentionPlaceResultExport(request,response, param);
    }

    /**
     * 更新装户房间展示状态
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("更新装户房间展示状态")
    @CessBody
    @ApiOperation(value = "更新装户房间展示状态", notes = "更新装户房间展示状态")
    @RequestMapping(value = "/updateIntentionPlaceIsShow", method = RequestMethod.POST)
    public ResultBody updateIntentionPlaceIsShow(@ApiParam(name = "map", value = "{\"id\":\"活动id\",\"isPublish\":\"状态（0未发布，1发布）\",\"type\":\"发布类型:1发布旭客家，2发布置业顾问\"}")
                                                 @RequestBody Map map){
        return intentionPlaceService.updateIntentionPlaceIsShow(map);
    }

    /**
     * 调整装户结果
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("调整装户结果")
    @CessBody
    @ApiOperation(value = "调整装户结果", notes = "调整装户结果")
    @RequestMapping(value = "/updateEditResult", method = RequestMethod.POST)
    public ResultBody updateEditResult(@ApiParam(name = "map", value = "{\"roomList\":[{\"activityId\":\"活动id\",\"totalDiffCount\":\"总数调整量\",\"oneDiffCount\":\"一选调整量\"" +
            ",\"twoDiffCount\":\"二选调整量\",\"threeDiffCount\":\"三选调整量\",\"fourDiffCount\":\"四选调整量\",\"fiveDiffCount\":\"五选调整量\",\"roomguid\":\"房间ID\"}]}")
                                                 @RequestBody Map map){
        return intentionPlaceService.updateEditResult(map);
    }

    /**
     * 查询装户结果
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询装户结果")
    @CessBody
    @ApiOperation(value = "查询装户结果", notes = "查询装户结果")
    @RequestMapping(value = "/selectRoomResult", method = RequestMethod.POST)
    public ResultBody selectRoomResult(@ApiParam(name = "map", value = "{\"id\":\"活动id\",\"buildguid\":\"楼栋id\"}")
                                                 @RequestBody Map map){
        return intentionPlaceService.selectRoomResult(map);
    }

    /**
     * 查询移动装户结果
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询移动装户结果")
    @CessBody
    @ApiOperation(value = "查询移动装户结果", notes = "查询移动装户结果")
    @RequestMapping(value = "/selectMoveRoomResult", method = RequestMethod.POST)
    public ResultBody selectMoveRoomResult(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\",\"buildIds\":\"楼栋id逗号隔开\",\"edition(1: 初始版 2：调整版 3：最终版)\":\"版本\"}")
                                       @RequestBody Map map){
        return intentionPlaceService.selectMoveRoomResult(map);
    }

    /**
     * 查询房间装户客户信息
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询房间装户客户信息")
    @CessBody
    @ApiOperation(value = "查询房间装户客户信息", notes = "查询房间装户客户信息")
    @RequestMapping(value = "/selectRoomResultCstList", method = RequestMethod.POST)
    public ResultBody selectRoomResultCstList(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\",\"roomId\":\"房间ID\",\"edition(1: 初始版 2：调整版 3：最终版)\":\"版本\"}")
                                           @RequestBody Map map){
        return intentionPlaceService.selectRoomResultCstList(map);
    }

    /**
     * 更新房间装户客户信息
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("更新房间装户客户信息")
    @CessBody
    @ApiOperation(value = "更新房间装户客户信息", notes = "更新房间装户客户信息")
    @RequestMapping(value = "/updateRoomResult", method = RequestMethod.POST)
    public ResultBody updateRoomResult(@ApiParam(name = "map", value = "{\"list\":\"需要更新的集合\"}")
                                              @RequestBody Map map){
        return intentionPlaceService.updateRoomResult(map);
    }

    /**
     * 删除房间装户客户信息
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("删除房间装户客户信息")
    @CessBody
    @ApiOperation(value = "删除房间装户客户信息", notes = "删除房间装户客户信息")
    @RequestMapping(value = "/delRoomResult", method = RequestMethod.POST)
    public ResultBody delRoomResult(@ApiParam(name = "map", value = "{\"id\":\"装户信息ID\",\"activityId\":\"活动ID\"}")
                                       @RequestBody Map map){
        return intentionPlaceService.delRoomResult(map);
    }

    /**
     * 查询装户楼栋
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询装户楼栋")
    @CessBody
    @ApiOperation(value = "查询装户楼栋", notes = "查询装户楼栋")
    @RequestMapping(value = "/selectActivityBuild", method = RequestMethod.POST)
    public ResultBody selectActivityBuild(@ApiParam(name = "map", value = "{\"id\":\"活动id\"}")
                                       @RequestBody Map map){
        return intentionPlaceService.selectActivityBuild(map);
    }

    /**
     * 查询装户楼栋（装户结果）
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询装户楼栋（装户结果）")
    @CessBody
    @ApiOperation(value = "查询装户楼栋（装户结果）", notes = "查询装户楼栋（装户结果）")
    @RequestMapping(value = "/selectActivityBuildResult", method = RequestMethod.POST)
    public ResultBody getActivityBuildResult(@ApiParam(name = "map", value = "{\"id\":\"活动id\"}")
                                          @RequestBody Map map){
        return intentionPlaceService.getActivityBuildResult(map);
    }

    /**
     * 查询装户活动概览图
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询装户活动概览图")
    @CessBody
    @ApiOperation(value = "查询装户活动概览图", notes = "查询装户活动概览图")
    @RequestMapping(value = "/selectActivityPhoto", method = RequestMethod.POST)
    public ResultBody selectActivityPhoto(@ApiParam(name = "map", value = "{\"id\":\"活动id\"}")
                                          @RequestBody Map map){
        return intentionPlaceService.selectActivityPhoto(map);
    }

    /**
     * 查询装户明细（按房间）
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询装户明细（按房间）")
    @CessBody
    @ApiOperation(value = "查询装户明细（按房间）", notes = "查询装户明细（按房间）")
    @RequestMapping(value = "/getRoomResultDetail", method = RequestMethod.POST)
    public ResultBody getRoomResultDetail(@ApiParam(name = "map", value = "{\"id\":\"活动id\",\"roomguid\":\"房间id\",\"intentionLevel\":\"意向级别\"}")
                                       @RequestBody Map map){
        return intentionPlaceService.getRoomResultDetail(map);
    }

    /**
     * 获取服务器当前时间
     * @return 查询结果
     */
    @Log("获取服务器当前时间")
    @CessBody
    @ApiOperation(value = "获取服务器当前时间", notes = "获取服务器当前时间")
    @RequestMapping(value = "/getTimeNow", method = RequestMethod.GET)
    public ResultBody getTimeNow(){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ResultBody.success(sd.format(new Date()));
    }

    /**
     * 分页查询调整列表
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("分页查询EditRecord")
    @CessBody
    @ApiOperation(value = "分页查询", notes = "分页查询EditRecord")
    @RequestMapping(value = "/getEditRecordList", method = RequestMethod.POST)
    public ResultBody getEditRecordList(@RequestBody(required = false) Map map){
        PageInfo<EditRecord> result= intentionPlaceService.getEditRecordList(map);
        return ResultBody.success(result);
    }

    @Log("导出调整列表")
    @CessBody
    @ApiOperation(value = "导出调整列表", notes = "导出调整列表")
    @RequestMapping(value = "/editRecordExport", method = RequestMethod.POST)
    public void editRecordExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param){
        intentionPlaceService.editRecordExport(request,response, param);
    }

    /**
     * 根据项目查询装户活动
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("根据项目查询装户活动")
    @CessBody
    @ApiOperation(value = "根据项目查询装户活动", notes = "根据项目查询装户活动")
    @RequestMapping(value = "/getProActivitys", method = RequestMethod.POST)
    public List<Map> getProActivitys(@RequestBody(required = false) Map map){
        List<Map> result= intentionPlaceService.getProActivitys(map);
        return result;
    }

    /**
     * 根据装户活动查询客户
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("根据装户活动查询客户")
    @CessBody
    @ApiOperation(value = "根据装户活动查询客户", notes = "根据装户活动查询客户")
    @RequestMapping(value = "/getProActivityCsts", method = RequestMethod.POST)
    public ResultBody getProActivityCsts(@ApiParam(name = "map", value = "{\"id\":\"活动id\",\"searchName\":\"客户姓名/手机号\",\"salesId\":\"置业顾问ID\"}")
            @RequestBody(required = false) Map map){
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")){
            return ResultBody.error(-100025,"活动id未传！！");
        }
        if (map==null || map.get("salesId")==null || "".equals(map.get("salesId")+"")){
            return ResultBody.error(-100025,"置业顾问id未传！！");
        }
        List<IntentionCst> result= intentionPlaceService.getProActivityCsts(map);
        return ResultBody.success(result);
    }

    /**
     * 查询装户活动统计数据
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询装户活动统计数据")
    @CessBody
    @ApiOperation(value = "查询装户活动统计数据", notes = "查询装户活动统计数据")
    @RequestMapping(value = "/getActivityStatistics", method = RequestMethod.POST)
    public ResultBody getActivityStatistics(@ApiParam(name = "map", value = "{\"id\":\"活动id\",\"edition\":\"版本\"}")
                                         @RequestBody(required = false) Map map){
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")){
            return ResultBody.error(-100025,"活动id未传！！");
        }
        if (map==null || map.get("edition")==null || "".equals(map.get("edition")+"")){
            return ResultBody.error(-100025,"版本未传！！");
        }
        Map result= intentionPlaceService.getActivityStatistics(map);
        return ResultBody.success(result);
    }

    /**
     * 查询装户活动客户数据
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询装户活动客户数据")
    @CessBody
    @ApiOperation(value = "查询装户活动客户数据", notes = "查询装户活动客户数据")
    @RequestMapping(value = "/getActivityCstStatistics", method = RequestMethod.POST)
    public ResultBody getActivityCstStatistics(@ApiParam(name = "map", value = "{\"id\":\"活动id\",\"searchName\":\"客户姓名/手机号/置业顾问名称\",\"edition\":\"版本\"}")
                                            @RequestBody(required = false) Map map){
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")){
            return ResultBody.error(-100025,"活动id未传！！");
        }
        if (map==null || map.get("edition")==null || "".equals(map.get("edition")+"")){
            return ResultBody.error(-100025,"版本未传！！");
        }
        List<String> strs = new ArrayList<>();
        List<IntentionSales> sales = new ArrayList<>();
        List<IntentionCst> result= intentionPlaceService.getActivityCstStatistics(map);
        if (result!=null && result.size()>0){
            for (IntentionCst cst:result) {
                String salesId = cst.getSalesId();
                String salesName = cst.getSalesName();
                if (!StringUtils.isEmpty(salesId)){
                    if (!strs.contains(salesId)){
                        IntentionSales sal = new IntentionSales();
                        List<IntentionCst> cst1 = new ArrayList<>();
                        sal.setCsts(cst1);
                        sal.setSalesId(salesId);
                        sal.setSalesName(salesName);
                        sales.add(sal);
                    }
                }
            }

            for (int i = 0;i < result.size();i++) {
                IntentionCst cst = result.get(i);
                String salesId = cst.getSalesId();
                if (!StringUtils.isEmpty(salesId)){
                    for (IntentionSales sa:sales) {
                        if (sa.getSalesId().equals(salesId)){
                            sa.getCsts().add(cst);
                            result.remove(cst);
                            i--;
                            break;
                        }
                    }
                }
            }
            //没有置业顾问的放到无归属
            if (result.size()>0){
                IntentionSales sal = new IntentionSales();
                List<IntentionCst> cst1 = new ArrayList<>();
                sal.setSalesId("1");
                sal.setSalesName("无归属");
                cst1.addAll(result);
                sal.setCsts(cst1);
                sales.add(sal);
            }

        }
        return ResultBody.success(sales);
    }

    /**
     * 查询装户活动客户详情
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询装户活动客户详情")
    @CessBody
    @ApiOperation(value = "查询装户活动客户详情", notes = "查询装户活动客户详情")
    @RequestMapping(value = "/getActivityCstDetail", method = RequestMethod.POST)
    public ResultBody getActivityCstDetail(@ApiParam(name = "map", value = "{\"id\":\"活动id\",\"opportunityClueId\":\"机会ID\",\"edition\":\"版本\"}")
                                            @RequestBody(required = false) Map map){
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")){
            return ResultBody.error(-100025,"活动id未传！！");
        }
        if (map==null || map.get("opportunityClueId")==null || "".equals(map.get("opportunityClueId")+"")){
            return ResultBody.error(-100025,"机会id未传！！");
        }
        if (map==null || map.get("edition")==null || "".equals(map.get("edition")+"")){
            return ResultBody.error(-100025,"版本未传！！");
        }
        IntentionCst result= intentionPlaceService.getActivityCstDetail(map);
        return ResultBody.success(result);
    }


    /**
     * 销售经理装户
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("销售经理装户")
    @CessBody
    @ApiOperation(value = "销售经理装户", notes = "销售经理装户")
    @RequestMapping(value = "/addRoomResultList", method = RequestMethod.POST)
    public ResultBody addRoomResultList(@ApiParam(name = "map", value = "{\"list\":[{" +
            "\"activityId\":\"活动ID\",\"activityName\":\"活动名称\",\"projectid\":\"项目ID\",\"projectname\":\"项目名称\",\"projectidFq\":\"分期项目ID\",\"projectnameFq\":\"分期项目名称\",\"buildguid\":\"楼栋ID\",\"buildname\":\"楼栋名称\",\"opportunityClueId\":\"机会ID\",\"projectClueId\":\"线索ID\",\"customerName\":\"客户名称\",\"customerMobile\":\"客户手机号\",\"salesId\":\"置业顾问ID\",\"salesName\":\"置业顾问\",\"batchNo\":\"开盘批次Code\",\"batchNoName\":\"开盘批次\",\"cardGrouping\":\"排卡分组code\",\"cardGroupingName\":\"排卡分组\",\"cardId\":\"排卡ID\",\"cardName\":\"卡名\",\"cardType\":\"排卡类型\",\"placeNo\":\"排卡顺序\",\"intentionLevelEdit\":\"意向级别\",\"roomguidEdit\":\"房间ID\",\"roomnameEdit\":\"房间名\",\"creator\":\"创建人ID\"" +
            "}]}")
                                         @RequestBody(required = false) Map map){
        if (map==null || map.get("list")==null || "".equals(map.get("list")+"")){
            return ResultBody.error(-100025,"必传参数未传！！");
        }
        intentionPlaceService.addRoomResultList(map);
        return ResultBody.success("添加意向成功！！");
    }

    /**
     * 查询项目下置业顾问
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询项目下置业顾问")
    @CessBody
    @ApiOperation(value = "查询项目下置业顾问", notes = "查询项目下置业顾问")
    @RequestMapping(value = "/getProSales", method = RequestMethod.POST)
    public ResultBody getProSales(@ApiParam(name = "map", value = "{\"projectId\":\"项目id\"}")
                                               @RequestBody(required = false) Map map){
        if (map==null || map.get("projectId")==null || "".equals(map.get("projectId")+"")){
            return ResultBody.error(-100025,"项目id未传！！");
        }
        List<Map> result= intentionPlaceService.getProSales(map);
        return ResultBody.success(result);
    }

    /**
     * 置业顾问通知
     * @param map
     * @return
     */
    @Log("置业顾问通知")
    @CessBody
    @ApiOperation(value = "置业顾问通知", notes = "置业顾问通知")
    @RequestMapping(value = "/addSalesMessage", method = RequestMethod.POST)
    public ResultBody addSalesMessage(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\"}")
                                  @RequestBody(required = false) Map map){
        if (map==null || map.get("activityId")==null || "".equals(map.get("activityId")+"")){
            return ResultBody.error(-100025,"活动id未传！！");
        }
        return intentionPlaceService.addSalesMessage(map);
    }

}




