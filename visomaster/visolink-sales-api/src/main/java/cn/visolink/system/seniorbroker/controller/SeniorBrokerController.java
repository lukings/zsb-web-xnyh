package cn.visolink.system.seniorbroker.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.seniorbroker.service.SeniorBrokerService;
import cn.visolink.system.seniorbroker.vo.BrokerAccountForm;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: BaoQiangLer
 * @ProjectName: visolink
 * @Description:  地图选房接口
 * @Date: Created in 2020/10/12
 */


@RestController
@Api(tags = "二级经纪人")
@RequestMapping("/seniorBroker")
public class SeniorBrokerController {

    private final SeniorBrokerService seniorBrokerService;

    public SeniorBrokerController(SeniorBrokerService seniorBrokerService) {
        this.seniorBrokerService = seniorBrokerService;
    }

    @Log("获取大客户活动数据")
    @ApiOperation(value = "获取大客户活动数据", notes = "获取大客户活动数据")
    @PostMapping(value = "getAccountActiveList")
    @ApiParam(name = "param", value = "{\"pageSize\":\"条数\",\"pageNum\":\"页数\"}")
    public ResultBody getAccountActiveList(@RequestBody Map<String, Object> param) {
        return ResultBody.success(seniorBrokerService.getAccountActiveList(param));
    }


    @Log("获取大客户活动详情")
    @ApiOperation(value = "获取大客户活动详情", notes = "获取大客户活动详情")
    @PostMapping(value = "getAccountActiveById")
    @ApiParam(name = "param", value = "{\"pageSize\":\"条数\",\"pageNum\":\"页数\"}")
    public ResultBody getAccountActiveById(@RequestBody Map<String, Object> param) {
        return ResultBody.success(seniorBrokerService.getAccountActiveById(param));
    }

    @Log("导出大客户活动数据")
    @CessBody
    @ApiOperation(value = "导出大客户活动数据", notes = "")
    @PostMapping(value = "/getAccountActiveExport")
    public void getAccountActiveExport(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> param) {
        seniorBrokerService.getAccountActiveExport(request,response, param);
    }

    @Log("获取活动下的二级经纪人")
    @ApiOperation(value = "获取活动下的二级经纪人", notes = "获取活动下的二级经纪人")
    @PostMapping(value = "getBrokerByActiveId")
    @ApiParam(name = "param", value = "{\"pageSize\":\"条数\",\"pageNum\":\"页数\"}")
    public ResultBody getBrokerByActiveId(@RequestBody Map<String, Object> param) {
        return ResultBody.success(seniorBrokerService.getBrokerByActiveId(param));
    }


    @Log("活动下的二级经纪人导出")
    @CessBody
    @ApiOperation(value = "活动下的二级经纪人导出", notes = "")
    @PostMapping(value = "/getBrokerByActiveIdExcel")
    public void getBrokerByActiveIdExcel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> param) {
        seniorBrokerService.getBrokerByActiveIdExcel(request,response, param);
    }

    @Log("获取活动下的客户")
    @ApiOperation(value = "获取活动下的客户", notes = "获取活动下的客户")
    @PostMapping(value = "getCustomerByActiveId")
    @ApiParam(name = "param", value = "{\"pageSize\":\"条数\",\"pageNum\":\"页数\"}")
    public ResultBody getCustomerByActiveId(@RequestBody Map<String, Object> param) {
        return ResultBody.success(seniorBrokerService.getCustomerByActiveId(param));
    }

    @Log("获取活动下的客户导出")
    @CessBody
    @ApiOperation(value = "获取活动下的客户导出", notes = "")
    @PostMapping(value = "/getCustomerByActiveIdExcel")
    public void getCustomerByActiveIdExcel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> param) {
        seniorBrokerService.getCustomerByActiveIdExcel(request,response, param);
    }

    @Log("获取二级经纪人数据")
    @ApiOperation(value = "获取二级经纪人数据", notes = "获取二级经纪人数据")
    @PostMapping(value = "getSeniorBroker")
    @ApiParam(name = "param", value = "{\"pageSize\":\"条数\",\"pageNum\":\"页数\"}")
    public ResultBody getSeniorBroker(@RequestBody Map<String, Object> param) {
        return ResultBody.success(seniorBrokerService.getSeniorBroker(param));
    }

    @Log("二级经纪人数据导出")
    @CessBody
    @ApiOperation(value = "二级经纪人数据导出", notes = "二级经纪人数据导出")
    @PostMapping(value = "/getSeniorBrokerExcel")
    public void getSeniorBrokerExcel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> param) {
        seniorBrokerService.getSeniorBrokerExcel(request,response, param);
    }

    @Log("二级经纪人数据异步导出")
    @CessBody
    @ApiOperation(value = "二级经纪人数据异步导出", notes = "")
    @RequestMapping(value = "/getSeniorBrokerExcelNew")
    public String getSeniorBrokerExcelNew(@RequestBody Map param) {
        return seniorBrokerService.getSeniorBrokerExcelNew(param);
    }

    @Log("获取项目的大客户")
    @ApiOperation(value = "获取项目的大客户", notes = "获取项目的大客户")
    @PostMapping(value = "getAccountProject")
    @ApiParam(name = "param", value = "{\"projectId\":\"项目id\"}")
    public ResultBody getAccountProject(@RequestBody Map<String, Object> param) {
        return seniorBrokerService.getAccountProject(param);
    }

    @Log("获取二级经纪人活动数据")
    @ApiOperation(value = "获取二级经纪人活动数据", notes = "获取二级经纪人活动数据")
    @PostMapping(value = "getSeniorBrokerActive")
    @ApiParam(name = "param", value = "{\"BrokerId\":\"经纪人id\",\"pageSize\":\"条数\",\"pageNum\":\"页数\"}")
    public ResultBody getSeniorBrokerActive(@RequestBody Map<String, Object> param) {
        return ResultBody.success(seniorBrokerService.getSeniorBrokerActive(param));
    }


    @Log("获取二级经纪人分配历史")
    @ApiOperation(value = "获取二级经纪人分配历史", notes = "获取二级经纪人分配历史")
    @PostMapping(value = "getBrokerAccountRecords")
    @ApiParam(name = "param", value = "{\"BrokerId\":\"经纪人id\",\"pageSize\":\"条数\",\"pageNum\":\"页数\"}")
    public ResultBody getBrokerAccountRecords(@RequestBody Map<String, Object> param) {
        return ResultBody.success(seniorBrokerService.getBrokerAccountRecords(param));
    }

    @Log("获取二级经纪人分配历史")
    @ApiOperation(value = "获取二级经纪人分配历史", notes = "获取二级经纪人分配历史")
    @PostMapping(value = "getDkhCfpType")
    public ResultBody getDkhCfpType() {
        return ResultBody.success(seniorBrokerService.getDkhCfpType());
    }



    @Log("二级经纪人分配")
    @ApiOperation(value = "二级经纪人分配", notes = "二级经纪人分配")
    @PostMapping(value = "redistributionAccountManager")
    @ApiParam(name = "brokerAccountForm", value = "{\"BrokerId\":\"经纪人id\",\"pageSize\":\"条数\",\"pageNum\":\"页数\"}")
    public ResultBody redistributionAccountManager(@RequestBody BrokerAccountForm brokerAccountForm) {
        return seniorBrokerService.redistributionAccountManager(brokerAccountForm);
    }



}
