package cn.visolink.system.allpeople.operationAnalysis.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.allpeople.operationAnalysis.model.ExportVo;
import cn.visolink.system.allpeople.operationAnalysis.service.OperationAnalysisService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName OperationAnalysisController
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/4/13 20:23
 **/
@RestController
@RequestMapping("/operationAnalysis")
public class OperationAnalysisController {
    @Autowired
    private OperationAnalysisService operationAnalysisService;

    @Log("运营分析表")
    @ApiOperation(value = "获取运营分析表")
    @PostMapping("/getOperationAnalysisList")
    public ResultBody getOperationAnalysisList(@RequestBody Map paramMap){
        return ResultBody.success(operationAnalysisService.getOperationAnalysisList(paramMap));
    }

    @Log("运营分析表导出")
    @CessBody
    @ApiOperation(value = "运营分析表导出", notes = "")
    @RequestMapping(value = "/operationAnalysisExport")
    public void operationAnalysisExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        operationAnalysisService.operationAnalysisExport(request,response, param);
    }

    @Log("运营分析表导出（异步）")
    @CessBody
    @ApiOperation(value = "运营分析表导出（异步）", notes = "运营分析表导出（异步）")
    @RequestMapping(value = "/operationAnalysisExportNew", method = RequestMethod.POST)
    public String operationAnalysisExportNew(@RequestBody(required = false) ExportVo param) {
        return operationAnalysisService.operationAnalysisExportNew(param);
    }

    @Log("推荐明细表")
    @ApiOperation(value = "获取推荐明细")
    @PostMapping("/getOperationAnalysisDetailList")
    public ResultBody getOperationAnalysisDetailList(@RequestBody Map paramMap){
        return ResultBody.success(operationAnalysisService.getOperationAnalysisDetailList(paramMap));
    }

    @Log("推荐明细表导出")
    @CessBody
    @ApiOperation(value = "推荐明细表导出", notes = "")
    @RequestMapping(value = "/operationAnalysisDetailExport")
    public void operationAnalysisDetailExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        operationAnalysisService.operationAnalysisDetailExport(request,response, param);
    }

    @Log("城市运维查询")
    @ApiOperation(value = "城市运维查询")
    @PostMapping("/getCityStatement")
    public ResultBody getCityStatement(@RequestBody Map paramMap){
        return ResultBody.success(operationAnalysisService.getCityStatement(paramMap));
    }

    @Log("城市运维明细表导出")
    @CessBody
    @ApiOperation(value = "城市运维明细表导出", notes = "")
    @RequestMapping(value = "/cityStatementExport")
    public void cityStatementExport(HttpServletRequest request, HttpServletResponse response,@RequestBody String param) {
        operationAnalysisService.cityStatementExport(request,response, param);
    }

    @Log("活动运维查询")
    @ApiOperation(value = "活动运维查询")
    @PostMapping("/getActivityStatement")
    public ResultBody getActivityStatement(@RequestBody Map paramMap){
        return ResultBody.success(operationAnalysisService.getActivityStatement(paramMap));
    }

    @Log("活动运维明细表导出")
    @CessBody
    @ApiOperation(value = "活动运维明细表导出", notes = "")
    @RequestMapping(value = "/activityStatementExport")
    public void activityStatementExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        operationAnalysisService.activityStatementExport(request,response, param);
    }

    @Log("活动运维明细表导出(使用异步方式)")
    @CessBody
    @ApiOperation(value = "活动运维明细表导出(使用异步方式)", notes = "活动运维明细表导出(使用异步方式)")
    @PostMapping(value = "/activityStatementExportNew")
    public String activityStatementExportNew(@RequestBody ExportVo param) {
        return operationAnalysisService.activityStatementExportNew(param);
    }


    @Log("项目内容运维查询")
    @ApiOperation(value = "项目内容运维查询")
    @PostMapping("/getProStatement")
    public ResultBody getProStatement(@RequestBody Map paramMap){
        return ResultBody.success(operationAnalysisService.getProStatement(paramMap));
    }

    @Log("项目内容运维明细表导出")
    @CessBody
    @ApiOperation(value = "项目内容运维明细表导出", notes = "")
    @RequestMapping(value = "/proStatementExport")
    public void proStatementExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        operationAnalysisService.proStatementExport(request,response, param);
    }

    @Log("项目内容运维明细表导出(使用异步方式)")
    @CessBody
    @ApiOperation(value = "项目内容运维明细表导出(使用异步方式)", notes = "项目内容运维明细表导出(使用异步方式)")
    @PostMapping(value = "/proStatementExportNew")
    public String proStatementExportNew(@RequestBody ExportVo param) {
        return operationAnalysisService.proStatementExportNew(param);
    }

}
