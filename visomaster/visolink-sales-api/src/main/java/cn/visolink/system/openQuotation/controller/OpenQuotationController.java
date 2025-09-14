package cn.visolink.system.openQuotation.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.householdregistration.service.IntentionPlaceService;
import cn.visolink.system.openQuotation.model.OpenActivity;
import cn.visolink.system.openQuotation.model.OpenBuild;
import cn.visolink.system.openQuotation.service.OpenQuotationService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OpenQuotationController
 * @Author wanggang
 * @Description //开盘控制器
 * @Date 2020/12/31 13:51
 **/
@RestController
@RequestMapping("/openQuotation")
public class OpenQuotationController {

    @Autowired
    private OpenQuotationService openQuotationService;

    @Autowired
    private IntentionPlaceService intentionPlaceService;

    /**
     * 查询开盘活动列表
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询开盘活动列表")
    @CessBody
    @ApiOperation(value = "查询开盘活动列表", notes = "查询开盘活动列表")
    @RequestMapping(value = "/getOpenActivitys", method = RequestMethod.POST)
    public ResultBody getOpenActivitys(@ApiParam(name = "map", value = "{\"projectIdss\":\"项目id集合字符串\",\"search\":\"开盘活动名称/编号\",\"actStatus\":\"开盘活动状态\",\"isOnlinePay\":\"是否线上支付\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                 @RequestBody Map map){
        return openQuotationService.getOpenActivitys(map);
    }

    @Log("导出活动数据")
    @CessBody
    @ApiOperation(value = "导出活动数据", notes = "导出活动数据")
    @RequestMapping(value = "/openActivityExport", method = RequestMethod.POST)
    public void openActivityExport(HttpServletRequest request, HttpServletResponse response, @ApiParam(name = "map", value = "{\"projectIdss\":\"项目id集合字符串\",\"search\":\"开盘活动名称/编号\",\"actStatus\":\"开盘活动状态\",\"isOnlinePay\":\"是否线上支付\",\"userName\":\"登录人账号\",\"userId\":\"登录人ID\"}")
    @RequestBody Map map){
        openQuotationService.openActivityExport(request,response, map);
    }

    /**
     * 查询开盘活动明细列表
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询开盘活动明细列表")
    @CessBody
    @ApiOperation(value = "查询开盘活动明细列表", notes = "查询开盘活动明细列表")
    @RequestMapping(value = "/getOpenActivityResult", method = RequestMethod.POST)
    public ResultBody getOpenActivityResult(@ApiParam(name = "map", value = "{\"projectIdss\":\"项目id集合字符串\",\"search\":\"开盘活动名称/编号\",\"actStatus\":\"开盘活动状态\",\"isOnlinePay\":\"是否线上支付\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                       @RequestBody Map map){
        return openQuotationService.getOpenActivityResult(map);
    }

    @Log("导出活动明细数据")
    @CessBody
    @ApiOperation(value = "导出活动明细数据", notes = "导出活动明细数据")
    @RequestMapping(value = "/openActivityResultExport", method = RequestMethod.POST)
    public void openActivityResultExport(HttpServletRequest request, HttpServletResponse response, @ApiParam(name = "map", value = "{\"projectIdss\":\"项目id集合字符串\",\"search\":\"开盘活动名称/编号\",\"actStatus\":\"开盘活动状态\",\"isOnlinePay\":\"是否线上支付\",\"userName\":\"登录人账号\",\"userId\":\"登录人ID\"}")
                                                        @RequestBody Map map){
        openQuotationService.openActivityResultExport(request,response, map);
    }

    /**
     * 查询开盘订单列表
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询开盘订单列表")
    @CessBody
    @ApiOperation(value = "查询开盘订单列表", notes = "查询开盘订单列表")
    @RequestMapping(value = "/getOpenOrderList", method = RequestMethod.POST)
    public ResultBody getOpenOrderList(@ApiParam(name = "map", value = "{\"projectIdss\":\"项目id集合字符串\",\"orderNo\":\"订单编号\",\"roomName\":\"房间名称\",\"search\":\"客户姓名/手机号\",\"tradeCloseReason\":\"关闭原因\",\"payStatus\":\"支付状态（1：未支付 2：已支付）\",\"actualPayType\":\"支付方式（1：线上支付 2：线下支付）\",\"tradeStatus\":\"交易状态（1：激活 2：关闭）\",\"reportTime\":\"时间类型（1：创建时间 2：支付时间 3：关闭时间）\",\"date1\":\"开始时间\",\"date2\":\"结束时间\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                            @RequestBody Map map){
        return openQuotationService.getOpenOrderList(map);
    }

    @Log("导出开盘订单明细数据")
    @CessBody
    @ApiOperation(value = "导出开盘订单明细数据", notes = "导出开盘订单明细数据")
    @RequestMapping(value = "/openOrderListExport", method = RequestMethod.POST)
    public void openOrderListExport(HttpServletRequest request, HttpServletResponse response, @ApiParam(name = "map", value = "{\"projectIdss\":\"项目id集合字符串\",\"orderNo\":\"订单编号\",\"roomName\":\"房间名称\",\"search\":\"客户姓名/手机号\",\"tradeCloseReason\":\"关闭原因\",\"payStatus\":\"支付状态（1：未支付 2：已支付）\",\"actualPayType\":\"支付方式（1：线上支付 2：线下支付）\",\"tradeStatus\":\"交易状态（1：激活 2：关闭）\",\"reportTime\":\"时间类型（1：创建时间 2：支付时间 3：关闭时间）\",\"date1\":\"开始时间\",\"date2\":\"结束时间\",\"userName\":\"登录人账号\",\"userId\":\"登录人ID\"}")
                                                    @RequestBody Map map){
        openQuotationService.openOrderListExport(request,response, map);
    }

    @Log("获取房间订单")
    @CessBody
    @ApiOperation(value = "获取房间订单", notes = "获取房间订单")
    @RequestMapping(value = "/getRoomOrder", method = RequestMethod.POST)
    public String getRoomOrder(@ApiParam(name = "map", value = "{\"roomId\":\"房间ID\"}")
                                                @RequestBody Map map){
       return openQuotationService.getRoomOrder(map);
    }

    /**
     * 查询订单详情
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询订单详情")
    @CessBody
    @ApiOperation(value = "查询订单详情", notes = "查询订单详情")
    @RequestMapping(value = "/getOrderDetail", method = RequestMethod.POST)
    public ResultBody getOrderDetail(@ApiParam(name = "map", value = "{\"orderNo\":\"订单编号\"}")
                                           @RequestBody Map map){
        return openQuotationService.getOrderDetail(map);
    }

    /**
     * 查询开盘活动楼栋
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询开盘活动列表")
    @CessBody
    @ApiOperation(value = "查询开盘活动楼栋", notes = "查询开盘活动楼栋")
    @RequestMapping(value = "/getOpenActivityBuild", method = RequestMethod.POST)
    public ResultBody getOpenActivityBuild(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\"}")
                                       @RequestBody Map map){
        return openQuotationService.getOpenActivityBuild(map);
    }

    /**
     * 查询开盘活动概览图
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询开盘活动概览图")
    @CessBody
    @ApiOperation(value = "查询开盘活动概览图", notes = "查询开盘活动概览图")
    @RequestMapping(value = "/getOpenActivityPhoto", method = RequestMethod.POST)
    public ResultBody getOpenActivityPhoto(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\"}")
                                           @RequestBody Map map){
        return openQuotationService.getOpenActivityPhoto(map);
    }

    /**
     * 发布活动
     * @param map 条件
     * @return 结果
     */
    @Log("发布活动")
    @CessBody
    @ApiOperation(value = "发布活动", notes = "发布活动")
    @RequestMapping(value = "/releaseActivity", method = RequestMethod.POST)
    public ResultBody releaseActivity(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\"}")
                                 @RequestBody Map map){
        return openQuotationService.releaseActivity(map);
    }

    /**
     * 禁用活动
     * @param map 条件
     * @return 结果
     */
    @Log("禁用活动")
    @CessBody
    @ApiOperation(value = "禁用活动", notes = "禁用活动")
    @RequestMapping(value = "/disableActivity", method = RequestMethod.POST)
    public ResultBody disableActivity(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\"}")
                                      @RequestBody Map map){
        return openQuotationService.disableActivity(map);
    }

    /**
     * 删除活动
     * @param map 条件
     * @return 结果
     */
    @Log("删除活动")
    @CessBody
    @ApiOperation(value = "删除活动", notes = "删除活动")
    @RequestMapping(value = "/delActivity", method = RequestMethod.POST)
    public ResultBody delActivity(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\"}")
                                      @RequestBody Map map){
        return openQuotationService.delActivity(map);
    }

    /**
     * 查询项目下楼盘
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询项目下楼盘")
    @CessBody
    @ApiOperation(value = "查询项目下楼盘", notes = "查询项目下楼盘")
    @RequestMapping(value = "/getProBook", method = RequestMethod.POST)
    public ResultBody getProBook(@ApiParam(name = "map", value = "{\"projectId\":\"项目id\"}")
                                          @RequestBody Map map){
        return openQuotationService.getProBook(map);
    }

    /**
     * 查询开盘活动详情
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询开盘活动详情")
    @CessBody
    @ApiOperation(value = "查询开盘活动详情", notes = "查询开盘活动详情")
    @RequestMapping(value = "/getOpenActivityDetail", method = RequestMethod.POST)
    public OpenActivity getOpenActivityDetail(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\"}")
                                 @RequestBody Map map){
        return openQuotationService.getOpenActivityDetail(map);
    }


    /**
     * 查询项目下楼盘
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询项目收款银行信息")
    @CessBody
    @ApiOperation(value = "查询项目收款银行信息", notes = "查询项目收款银行信息")
    @RequestMapping(value = "/getProBank", method = RequestMethod.POST)
    public ResultBody getProBank(@ApiParam(name = "map", value = "{\"projectId\":\"项目id\"}")
                                 @RequestBody Map map){
        return openQuotationService.getProBank(map);
    }

    /**
     * 查询项目优惠方案
     * @param map 查询条件
     * @return 查询结果
     */
    @Log("查询项目优惠方案")
    @CessBody
    @ApiOperation(value = "查询项目优惠方案", notes = "查询项目优惠方案")
    @RequestMapping(value = "/getProDiscount", method = RequestMethod.POST)
    public ResultBody getProDiscount(@ApiParam(name = "map", value = "{\"projectId\":\"项目id\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\",\"discountName\":\"优惠方案名称\"}")
                                 @RequestBody Map map){
        return openQuotationService.getProDiscount(map);
    }

    @Log("保存活动")
    @CessBody
    @ApiOperation(value = "保存活动", notes = "保存活动")
    @RequestMapping(value = "/addOpenActivity", method = RequestMethod.POST)
    public ResultBody addOpenActivity(@RequestBody(required = false) OpenActivity param){
        return openQuotationService.addOpenActivity(param);
    }

    @Log("更新活动")
    @CessBody
    @ApiOperation(value = "更新活动", notes = "更新活动")
    @RequestMapping(value = "/updateOpenActivity", method = RequestMethod.POST)
    public ResultBody updateOpenActivity(@RequestBody(required = false) OpenActivity param){
        return openQuotationService.updateOpenActivity(param);
    }

    @Log("活动信息导出(使用异步方式)")
    @CessBody
    @ApiOperation(value = "活动信息导出", notes = "活动信息导出")
    @PostMapping(value = "/openActivityExportNew")
    public String openActivityExportNew(@ApiParam(name = "map", value = "{\"projectIdss\":\"项目id集合字符串\",\"search\":\"开盘活动名称/编号\",\"actStatus\":\"开盘活动状态\",\"isOnlinePay\":\"是否线上支付\",\"userName\":\"登录人账号\",\"userId\":\"登录人ID\"}")
            @RequestBody Map map) {
        return openQuotationService.openActivityExportNew(map);
    }

    /**
     * 查询项目下楼栋
     * @param projectId 查询条件
     * @return 查询结果
     */
    @Log("查询项目下楼栋")
    @CessBody
    @ApiOperation(value = "查询项目下楼栋", notes = "查询项目下楼栋")
    @RequestMapping(value = "/getProBlding", method = RequestMethod.POST)
    public ResultBody getProBlding(@ApiParam(name = "projectId", value = "项目ID")String projectId){
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
    public ResultBody getBldingRoomList(@ApiParam(name = "map", value = "{\"activityId\":\"活动ID\",\"bldId\":\"楼栋ID\",\"isResult\":\"是否选房结果（1：是）\"}")
                                        @RequestBody Map map){
        String bldId = null;
        String activityId = null;
        String isResult = null;
        if (map.get("activityId")!=null){
            activityId = map.get("activityId")+"";
        }
        if (map.get("bldId")!=null){
            bldId = map.get("bldId")+"";
        }
        if (map.get("isResult")!=null){
            isResult = map.get("isResult")+"";
        }
        OpenBuild result= openQuotationService.getBldingRoomList(bldId,activityId,isResult);
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
    @RequestMapping(value = "/getBldingRoomsList", method = RequestMethod.POST)
    public ResultBody getBldingRoomsList(@ApiParam(name = "map", value = "{\"bldIds\":\"楼栋ID逗号分割\",\"room\":\"房间名称\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                        @RequestBody Map map){
        if (map.get("bldIds")==null || "".equals(map.get("bldIds")+"")){
            return ResultBody.error(-20006,"楼栋ID未传！");
        }
        return openQuotationService.getBldingRoomsList(map);
    }

    /**
     * 查询楼栋是否可删除
     * @param
     * @return 查询结果
     */
    @Log("查询楼栋是否可删除")
    @CessBody
    @ApiOperation(value = "查询楼栋是否可删除", notes = "查询楼栋是否可删除")
    @RequestMapping(value = "/queryBuildDelOk", method = RequestMethod.POST)
    public ResultBody queryBuildDelOk(@ApiParam(name = "map", value = "{\"activityId\":\"活动ID\",\"bldId\":\"楼栋ID\"}")
                                       @RequestBody Map map){
        int result= openQuotationService.queryBuildDelOk(map);
        return ResultBody.success(result);
    }

    /**
     * 查询房间是否可删除
     * @param
     * @return 查询结果
     */
    @Log("查询房间是否可删除")
    @CessBody
    @ApiOperation(value = "查询房间是否可删除", notes = "查询房间是否可删除")
    @RequestMapping(value = "/queryRoomDelOk", method = RequestMethod.POST)
    public ResultBody queryRoomDelOk(@ApiParam(name = "map", value = "{\"roomId\":\"房间ID\"}")
                                      @RequestBody Map map){
        if (map==null || map.get("roomId")==null || "".equals(map.get("roomId")+"")){
            return ResultBody.error(-20006,"房间ID未传！");
        }
        int result= openQuotationService.queryRoomDelOk(map);
        return ResultBody.success(result);
    }

    /**
     * 查询开盘活动当前状态
     * @param
     * @return 查询结果
     */
    @Log("查询开盘活动当前状态")
    @CessBody
    @ApiOperation(value = "查询开盘活动当前状态", notes = "查询开盘活动当前状态")
    @RequestMapping(value = "/queryOpenActivityStatus", method = RequestMethod.POST)
    public ResultBody queryOpenActivityStatus(@ApiParam(name = "map", value = "{\"activityId\":\"活动ID\"}")
                                      @RequestBody Map map){
        return ResultBody.success(openQuotationService.queryOpenActivityStatus(map));
    }

    /**
     * 查询商户信息
     * @param
     * @return 查询结果
     */
    @Log("查询商户信息")
    @CessBody
    @ApiOperation(value = "查询商户信息", notes = "查询商户信息")
    @RequestMapping(value = "/queryOpenActivityBanks", method = RequestMethod.POST)
    public ResultBody queryOpenActivityBanks(@ApiParam(name = "map", value = "{\"activityId\":\"活动ID\",\"buildIds\":\"楼栋ID逗号隔开\"}")
                                              @RequestBody Map map){
        return ResultBody.success(openQuotationService.queryOpenActivityBanks(map));
    }

    /**
     * 查询楼栋是否存在认购定金
     * @param
     * @return 查询结果
     */
    @Log("查询楼栋是否存在认购定金")
    @CessBody
    @ApiOperation(value = "查询楼栋是否存在认购定金", notes = "查询楼栋是否存在认购定金")
    @RequestMapping(value = "/queryBuildAccount", method = RequestMethod.POST)
    public ResultBody queryBuildAccount(@ApiParam(name = "map", value = "{\"buildId\":\"楼栋ID\"}")
                                             @RequestBody Map map){
        if(map.get("buildId")==null || "".equals(map.get("buildId")+"")){
            return ResultBody.error(-21_0006,"必传参数未传");
        }
        return ResultBody.success(openQuotationService.queryBuildAccount(map));
    }

    /**
     * 查询活动是否可发布
     * @param
     * @return 查询结果
     */
    @Log("查询活动是否可发布")
    @CessBody
    @ApiOperation(value = "查询活动是否可发布", notes = "查询活动是否可发布")
    @RequestMapping(value = "/queryActivityIsOkPublish", method = RequestMethod.POST)
    public ResultBody queryActivityIsOkPublish(@ApiParam(name = "map", value = "{\"activityId\":\"活动ID\"}")
                                        @RequestBody Map map){
        if(map.get("activityId")==null || "".equals(map.get("activityId")+"")){
            return ResultBody.error(-21_0006,"必传参数未传");
        }
        return ResultBody.success(openQuotationService.queryActivityIsOkPublish(map));
    }

    @Log("查询旭客汇认购规则")
    @ApiOperation(value = "查询旭客汇认购规则", notes = "查询旭客汇认购规则")
    @RequestMapping(value = "/getProjectRule", method = RequestMethod.GET)
    public ResultBody getProjectRule(@ApiParam(name = "projectId", value = "项目ID")String projectId){
        return openQuotationService.getProjectRule(projectId);
    }

    @Log("保存旭客汇认购规则")
    @ApiOperation(value = "保存旭客汇认购规则", notes = "保存旭客汇认购规则")
    @RequestMapping(value = "/saveProjectRule", method = RequestMethod.POST)
    public ResultBody saveProjectRule(@ApiParam(name = "map", value = "{\"projectId\":\"项目ID\",\"subscribeStatus\":\"认购状态（1 启用 0 禁用）\",\"isOnlinePay\":\"是否线上支付（1 是 0 否）\",\"failMinute\":\"支付过期时间\",\"houseAreaShowType\":\"房源面积展示方式 1建筑面积2套内面积\"}")
                                      @RequestBody Map map){
        return openQuotationService.saveProjectRule(map);
    }

    @Log("判断项目是否配置商户号")
    @ApiOperation(value = "判断项目是否配置商户号", notes = "判断项目是否配置商户号")
    @RequestMapping(value = "/getIsCollBank", method = RequestMethod.GET)
    public ResultBody getIsCollBank(@ApiParam(name = "projectId", value = "项目ID")String projectId){
        return openQuotationService.getIsCollBank(projectId);
    }

    @Log("解除绑定")
    @ApiOperation(value = "解除绑定", notes = "解除绑定")
    @RequestMapping(value = "/editOrder", method = RequestMethod.POST)
    public ResultBody getIsCollBank(@ApiParam(name = "map", value = "{\"unbindReason\":\"解绑原因\",\"userId\":\"用户id\",\"orderNo\":\"订单号\"}")
                                        @RequestBody Map map){
        return openQuotationService.editOrder(map);
    }

    @Log("线下支付凭证审核列表")
    @ApiOperation(value = "线下支付凭证审核列表")
    @PostMapping(value = "offlinePayCheckList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "{\n" +
                    "    \"pageIndex\": 1,\n" +
                    "    \"pageSize\": 10,\n" +
                    "    \"project_id\": \"0003DCA4-B01F-EA11-80BB-005056A37AFA\",\n" +
                    "    \"room_name\": \"房间名称\",\n" +
                    "    \"client_name\": \"认购人姓名\"\n" +
                    "}")
    })
    public ResultBody offlinePayCheckList(@RequestBody Map map) {
        return openQuotationService.offlinePayCheckList(map);
    }

    @Log("审批凭证")
    @ApiOperation(value = "审批凭证")
    @PostMapping(value = "checkCertificate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "{\n" +
                    "\t\"checkFlag\": \"此值不为空表示通过，否则表示驳回\",\n" +
                    "\t\"rejection_reason\": \"驳回原因\",\n" +
                    "\t\"id\": \"主键id\"\n" +
                    "}")
    })
    public ResultBody checkCertificate(@RequestBody Map map) {
        return openQuotationService.checkCertificate(map);
    }

    @Log("查看驳回记录通过主键id")
    @ApiOperation(value = "查看驳回记录通过主键id")
    @PostMapping(value = "getRecordById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "{\n" +
                    "\t\"id\": \"defe0ab7-5e47-11ec-9e8e-00163e082b08\"\n" +
                    "}")
    })
    public ResultBody getRecordById(@RequestBody Map map) {
        return openQuotationService.getRecordById(map);
    }

    @Log("导出审核列表数据通过项目id")
    @CessBody
    @ApiOperation(value = "导出审核列表数据通过项目id")
    @RequestMapping(value = "/exportCheckListData", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "{\n" +
                    "\t\"project_id\": \"01802085-5D65-4348-8088-550A4EFDC2EB\"\n" +
                    "}")
    })
    public void exportCheckListData(@RequestBody Map map, HttpServletRequest request, HttpServletResponse response) {
        openQuotationService.exportCheckListData(map, request, response);
    }
}
