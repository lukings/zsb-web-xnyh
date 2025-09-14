package cn.visolink.system.projectmanager.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.projectmanager.model.*;
import cn.visolink.system.projectmanager.model.Vo.ProCollAccountVo;
import cn.visolink.system.projectmanager.service.HousingManagementService;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName HousingManagementController
 * @Author wanggang
 * @Description //房源管理
 * @Date 2021/11/24 13:45
 **/
@RestController
@RequestMapping("/house")
public class HousingManagementController {

    @Autowired
    private HousingManagementService housingManagementService;

    @Log("查询房间交易信息")
    @CessBody
    @ApiOperation(value = "查询房间交易信息")
    @PostMapping("/tradeCstList")
    public PageInfo<TradeCstData> getTradeCstList(@RequestBody TradeCstForm tradeCstForm){
        return housingManagementService.getTradeCstList(tradeCstForm);
    }

    @Log("房间交易信息导出")
    @CessBody
    @ApiOperation(value = "房间交易信息导出", notes = "房间交易信息导出")
    @PostMapping(value = "/tradeCstExport")
    public void tradeCstExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        housingManagementService.tradeCstExport(request,response, param);
    }

    @Log("查询成交信息")
    @CessBody
    @ApiOperation(value = "查询成交信息")
    @PostMapping("/dealCstList")
    public PageInfo<DealCstData> getTradeCstList(@RequestBody DealCstForm dealCstForm){
        return housingManagementService.getDealCstData(dealCstForm);
    }

    @Log("成交信息导出")
    @CessBody
    @ApiOperation(value = "成交信息导出", notes = "成交信息导出")
    @PostMapping(value = "/dealCstExport")
    public void dealCstExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        housingManagementService.dealCstExport(request,response, param);
    }

    @Log("成交关联客户导出")
    @CessBody
    @ApiOperation(value = "成交关联客户导出", notes = "成交关联客户导出")
    @PostMapping(value = "/dealRelationCstExport")
    public void dealRelationCstExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        housingManagementService.dealRelationCstExport(request,response, param);
    }

    @Log("房间信息查询")
    @CessBody
    @ApiOperation(value = "房间信息查询", notes = "房间信息查询")
    @PostMapping(value = "/getRoomList")
    public ResultBody getRoomList(HttpServletRequest request, @ApiParam(name = "param", value = "{\"bldguidList\":\"楼栋id集合逗号隔开\"}")
    @RequestBody Map param) {
        return housingManagementService.getRoomList(request,param);
    }

    @Log("房间详情查询")
    @CessBody
    @ApiOperation(value = "房间详情查询", notes = "房间详情查询")
    @PostMapping(value = "/getRoomById")
    public ResultBody getRoomById(HttpServletRequest request,@ApiParam(name = "param", value = "{\"RoomGUID\":\"房间ID\"}")
    @RequestBody Map param) {
        String RoomGUID = "";
        if(param == null || param.get("RoomGUID") ==null || "".equals(param.get("RoomGUID")+"")){
            return ResultBody.error(-1200002,"房间ID未传！");
        }else{
            RoomGUID = param.get("RoomGUID")+"";
        }
        return housingManagementService.getRoomById(request,RoomGUID);
    }

    @Log("楼栋查询")
    @CessBody
    @ApiOperation(value = "楼栋查询", notes = "楼栋查询")
    @PostMapping(value = "/getBldByProId")
    public ResultBody getBldByProId(HttpServletRequest request,@ApiParam(name = "param", value = "{\"projectId\":\"项目ID\"}")
    @RequestBody Map param) {
        String projectId = "";
        if(param == null || param.get("projectId") ==null || "".equals(param.get("projectId")+"")){
            return ResultBody.error(-1200002,"项目ID未传！");
        }else{
            projectId = param.get("projectId")+"";
        }
        return housingManagementService.getBldByProId(request,projectId);
    }

    @Log("收款账户查询")
    @CessBody
    @ApiOperation(value = "收款账户查询", notes = "收款账户查询")
    @PostMapping(value = "/getXcollection")
    public ResultBody getXcollection(@ApiParam(name = "param", value = "{\"projectIds\":\"项目ID集合逗号隔开\",\"proCollAccount\":\"商户号\",\"bankNo\":\"银行卡号\",\"mobile\":\"客户手机号\",\"accountType\":\"收款类型\",\"isOk\":\"近一次结算是否正常(1:正常 2：异常)\",\"pageNum\":\"当前页\",\"pageSize\":\"每页行数\"}")
    @RequestBody Map param) {
        if(param == null || param.get("projectIds") ==null || "".equals(param.get("projectIds")+"")){
            return ResultBody.error(-1200002,"项目ID未传！");
        }
        return housingManagementService.getXcollection(param);
    }

    @Log("收款账户导出")
    @CessBody
    @ApiOperation(value = "收款账户导出", notes = "收款账户导出")
    @PostMapping(value = "/xcollectionExport")
    public void xcollectionExport(HttpServletRequest request, HttpServletResponse response,@ApiParam(name = "param", value = "{\"projectIds\":\"项目ID集合逗号隔开\",\"proCollAccount\":\"商户号\",\"bankNo\":\"银行卡号\",\"mobile\":\"客户手机号\",\"accountType\":\"收款类型\",\"isOk\":\"近一次结算是否正常(1:正常 2：异常)\"}")
                                     @RequestBody Map param) {
         housingManagementService.xcollectionExport(request,response,param);
    }

    @Log("收款账户保存")
    @CessBody
    @ApiOperation(value = "收款账户保存", notes = "收款账户保存")
    @PostMapping(value = "/addOrEditColl")
    public ResultBody addOrEditColl(@RequestBody ProCollAccountVo param) {
        return housingManagementService.addOrEditColl(param);
    }

    @Log("获取手机验证码")
    @CessBody
    @ApiOperation(value = "获取手机验证码", notes = "获取手机验证码")
    @PostMapping(value = "/getXcode")
    public ResultBody getXcode(@RequestBody Map param) {
        return housingManagementService.getXcode(param);
    }

    @Log("收款账户修改日志")
    @CessBody
    @ApiOperation(value = "收款账户修改日志", notes = "收款账户修改日志")
    @PostMapping(value = "/getProCollEditLog")
    public ResultBody getProCollEditLog(@ApiParam(name = "param", value = "{\"id\":\"账户ID\",\"pageNum\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                        @RequestBody Map param) {
        if(param == null || param.get("id") ==null || "".equals(param.get("id")+"")){
            return ResultBody.error(-1200002,"账户ID未传！");
        }
        return housingManagementService.getProCollEditLog(param);
    }

    @Log("退款台账")
    @CessBody
    @ApiOperation(value = "退款台账", notes = "退款台账")
    @PostMapping(value = "/getRefundApproval")
    public ResultBody getRefundApproval(@ApiParam(name = "param", value = "{\"pageNum\":\"当前页\",\"pageSize\":\"每页行数\",\"projectIds\":\"项目ID集合逗号隔开\",\"search\":\"客户姓名、手机号\"}")
                                        @RequestBody Map param) {
        if(param == null || param.get("projectIds") ==null || "".equals(param.get("projectIds")+"")){
            return ResultBody.error(-1200002,"项目ID未传！");
        }
        return housingManagementService.getRefundApproval(param);
    }

    @Log("退款台账导出")
    @CessBody
    @ApiOperation(value = "退款台账导出", notes = "退款台账导出")
    @PostMapping(value = "/refundApprovalExport")
    public void refundApprovalExport(HttpServletRequest request, HttpServletResponse response,@ApiParam(name = "param", value = "{\"projectIds\":\"项目ID集合逗号隔开\",\"search\":\"客户姓名、手机号\"}")
    @RequestBody Map param) {
        housingManagementService.refundApprovalExport(request,response,param);
    }

    @Log("收款明细台账")
    @CessBody
    @ApiOperation(value = "收款明细台账", notes = "收款明细台账")
    @PostMapping(value = "/getReturnedMoney")
    public ResultBody getTradeRecPai(@ApiParam(name = "param", value = "{\"projectIds\":\"项目ID集合逗号隔开\",\"search\":\"客户姓名、手机号\",\"pageNum\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                        @RequestBody Map param) {
        if(param == null || param.get("projectIds") ==null || "".equals(param.get("projectIds")+"")){
            return ResultBody.error(-1200002,"项目ID未传！");
        }
        return housingManagementService.getReturnedMoney(param);
    }

    @Log("收款明细台账导出")
    @CessBody
    @ApiOperation(value = "收款明细台账导出", notes = "收款明细台账导出")
    @PostMapping(value = "/returnedMoneyExport")
    public void returnedMoneyExport(HttpServletRequest request, HttpServletResponse response,@ApiParam(name = "param", value = "{\"projectIds\":\"项目ID集合逗号隔开\",\"search\":\"客户姓名、手机号\"}")
    @RequestBody Map param) {
        housingManagementService.returnedMoneyExport(request,response,param);
    }

    @Log("退款审批")
    @CessBody
    @ApiOperation(value = "退款审批", notes = "退款审批")
    @PostMapping(value = "/refundApproval")
    public ResultBody refundApproval(@ApiParam(name = "param", value = "{\"id\":\"id\",\"status\":\"(2: 通过 3：驳回)\",\"rejectionReasons\":\"驳回原因\"}")
                                        @RequestBody Map param) {
        if(param == null || param.get("id") ==null || "".equals(param.get("id")+"")){
            return ResultBody.error(-1200002,"ID未传！");
        }
        return housingManagementService.refundApproval(param);
    }

    @Log("收款确认")
    @CessBody
    @ApiOperation(value = "收款确认", notes = "收款确认")
    @PostMapping(value = "/returnedMoney")
    public ResultBody returnedMoney(@ApiParam(name = "param", value = "{\"id\":\"id\"}")
                                     @RequestBody Map param) {
        if(param == null || param.get("id") ==null || "".equals(param.get("id")+"")){
            return ResultBody.error(-1200002,"ID未传！");
        }
        return housingManagementService.returnedMoney(param);
    }

    @Log("签约协议模板列表查询")
    @CessBody
    @ApiOperation(value = "签约协议模板列表查询")
    @PostMapping(value = "/selectSignProtocolTemplates")
    public ResultBody selectSignProtocolTemplates(@RequestBody OrderTemplate orderTemplate) {
        return housingManagementService.selectSignProtocolTemplates(orderTemplate);
    }

    @Log("签约协议模板列表启用或者禁用")
    @CessBody
    @ApiOperation(value = "签约协议模板列表启用或者禁用")
    @GetMapping(value = "/updateSignProtocolTemplatesStatus")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键id"),
            @ApiImplicitParam(name = "statusFlag", value = "0禁用 1启用")
    })
    public ResultBody updateSignProtocolTemplatesStatus(@RequestParam("id") Integer id, @RequestParam("statusFlag") Integer statusFlag) {
        return housingManagementService.updateSignProtocolTemplatesStatus(id, statusFlag);
    }

    @Log("保存签约模板")
    @CessBody
    @ApiOperation(value = "保存签约模板")
    @PostMapping(value = "/saveOrderTemplate")
    public ResultBody saveOrderTemplate(@RequestBody OrderTemplate orderTemplate, HttpServletRequest request) {
        return housingManagementService.saveOrderTemplate(orderTemplate, request);
    }

    @Log("查询签约模板详情通过id")
    @CessBody
    @ApiOperation(value = "查询签约模板详情通过id")
    @GetMapping(value = "/getOrderTemplate")
    @ApiImplicitParam(name = "id", value = "主键id")
    public ResultBody getOrderTemplate(@RequestParam("id") Integer id) {
        return housingManagementService.getOrderTemplate(id);
    }

    @Log("更新签约模板通过id")
    @CessBody
    @ApiOperation(value = "更新签约模板通过id")
    @PostMapping(value = "/updateOrderTemplate")
    public ResultBody updateOrderTemplate(@RequestBody OrderTemplate orderTemplate, HttpServletRequest request) {
        return housingManagementService.updateOrderTemplate(orderTemplate, request);
    }

    @Log("集团签署认购协议文件列表")
    @CessBody
    @ApiOperation(value = "集团签署认购协议文件列表")
    @GetMapping(value = "/selectGroupOrderTemplate")
    public ResultBody selectGroupOrderTemplate() {
        return housingManagementService.selectGroupOrderTemplate();
    }

}
