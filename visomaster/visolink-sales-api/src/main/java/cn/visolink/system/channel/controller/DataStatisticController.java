package cn.visolink.system.channel.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.channel.service.DataStatisticService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName DataStatisticController
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/19 13:51
 **/
@RestController
@Api(tags = "数据统计")
@RequestMapping("/dataStatistic")
public class DataStatisticController {

    @Autowired
    private DataStatisticService dataStatisticService;

    @Log("项目数据统计")
    @ApiOperation(value = "项目数据统计")
    @PostMapping("/projectDataStatistics")
    public ResultBody projectDataStatistics(@RequestBody Map paramMap){
        return dataStatisticService.projectDataStatistics(paramMap);
    }

    @Log("个人数据统计")
    @ApiOperation(value = "个人数据统计")
    @PostMapping("/userDataStatistics")
    public ResultBody userDataStatistics(@RequestBody Map paramMap){
        return dataStatisticService.userDataStatistics(paramMap);
    }

    @Log("根据项目ID获取小组")
    @ApiOperation(value = "根据项目ID获取小组")
    @PostMapping("/getTeamListByProId")
    public ResultBody getTeamListByProId(@RequestBody Map paramMap){
        return dataStatisticService.getTeamListByProId(paramMap);
    }

    @Log("区域数据统计")
    @ApiOperation(value = "区域数据统计")
    @PostMapping("/regionDataStatistics")
    public ResultBody regionDataStatistics(@RequestBody Map paramMap){
        return dataStatisticService.regionDataStatistics(paramMap);
    }

    @Log("区域项目查询")
    @ApiOperation(value = "区域项目查询")
    @GetMapping("/getAllRegion")
    public ResultBody getAllRegion(){
        return dataStatisticService.getAllRegion();
    }

    @Log("区域查询")
    @ApiOperation(value = "区域查询")
    @GetMapping("/getAllRegionNew")
    public ResultBody getAllRegionNew(){
        return dataStatisticService.getAllRegionNew();
    }

    @Log("项目数据导出")
    @CessBody
    @ApiOperation(value = "项目数据导出", notes = "")
    @RequestMapping(value = "/projectDataExport")
    public void projectDataExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        dataStatisticService.projectDataExport(request,response, excelForm);
    }

    @Log("项目数据导出")
    @CessBody
    @ApiOperation(value = "项目数据导出", notes = "")
    @RequestMapping(value = "/projectDataExportNew")
    public String projectDataExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        return dataStatisticService.projectDataExportNew(request,response, excelForm);
    }

    @Log("个人数据导出")
    @CessBody
    @ApiOperation(value = "个人数据导出", notes = "")
    @RequestMapping(value = "/userDataExport")
    public void userDataExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        dataStatisticService.userDataExport(request,response, excelForm);
    }

    @Log("个人数据导出")
    @CessBody
    @ApiOperation(value = "个人数据导出", notes = "")
    @RequestMapping(value = "/userDataExportNew")
    public String userDataExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        return dataStatisticService.userDataExportNew(request,response, excelForm);
    }


    @Log("区域数据导出")
    @CessBody
    @ApiOperation(value = "区域数据导出", notes = "")
    @RequestMapping(value = "/regionDataExport")
    public void regionDataExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        dataStatisticService.regionDataExport(request,response, excelForm);
    }

    @Log("区域数据导出")
    @CessBody
    @ApiOperation(value = "区域数据导出", notes = "")
    @RequestMapping(value = "/regionDataExportNew")
    public String regionDataExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        return dataStatisticService.regionDataExportNew(request,response, excelForm);
    }

    @Log("项目成交数据统计")
    @ApiOperation(value = "项目成交数据统计")
    @PostMapping("/dealStatistics")
    public ResultBody dealStatistics(@RequestBody Map paramMap){
        return dataStatisticService.dealStatistics(paramMap);
    }

    @Log("区域成交数据统计")
    @ApiOperation(value = "区域成交数据统计")
    @PostMapping("/regionDealStatistics")
    public ResultBody regionDealStatistics(@RequestBody Map paramMap){
        return dataStatisticService.regionDealStatistics(paramMap);
    }

    @Log("项目成交数据导出")
    @CessBody
    @ApiOperation(value = "项目成交数据导出", notes = "")
    @RequestMapping(value = "/dealStatisticsExport")
    public void dealStatisticsExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        dataStatisticService.dealStatisticsExport(request,response, excelForm);
    }

    @Log("区域成交数据导出")
    @CessBody
    @ApiOperation(value = "区域成交数据导出", notes = "")
    @RequestMapping(value = "/regionDealExport")
    public void regionDealExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        dataStatisticService.regionDealExport(request,response, excelForm);
    }

    @Log("跟进数据统计")
    @ApiOperation(value = "跟进数据统计")
    @PostMapping("/followUpStatistics")
    public ResultBody followUpStatistics(@RequestBody Map paramMap){
        return dataStatisticService.followUpStatistics(paramMap);
    }

    @Log("跟进数据导出")
    @CessBody
    @ApiOperation(value = "跟进数据导出", notes = "")
    @RequestMapping(value = "/followUpExport")
    public void followUpExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        dataStatisticService.followUpExport(request,response, excelForm);
    }

    @Log("项目转化率统计")
    @ApiOperation(value = "项目转化率统计")
    @PostMapping("/proConversionRateStatistics")
    public ResultBody proConversionRateStatistics(@RequestBody Map paramMap){
        return dataStatisticService.proConversionRateStatistics(paramMap);
    }

    @Log("项目转化率统计新")
    @ApiOperation(value = "项目转化率统计新")
    @PostMapping("/proConversionRateStatisticsNew")
    public ResultBody proConversionRateStatisticsNew(@RequestBody Map paramMap){
        return dataStatisticService.proConversionRateStatisticsNewPL(paramMap);
    }

    @Log("项目转化率导出新")
    @ApiOperation(value = "项目转化率导出新")
    @PostMapping("/proConversionRateStatisticsExportNew")
    public void proConversionRateStatisticsExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm){
        dataStatisticService.proConversionRateStatisticsExportNewPL(request,response, excelForm);
    }

    @Log("区域转化率统计")
    @ApiOperation(value = "区域转化率统计")
    @PostMapping("/regionConversionRateStatistics")
    public ResultBody regionConversionRateStatistics(@RequestBody Map paramMap){
        return dataStatisticService.regionConversionRateStatistics(paramMap);
    }

    @Log("项目转化率导出")
    @CessBody
    @ApiOperation(value = "项目转化率导出", notes = "")
    @RequestMapping(value = "/proConversionRateExport")
    public void proConversionRateExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        dataStatisticService.proConversionRateExport(request,response, excelForm);
    }

    @Log("区域转化率导出")
    @CessBody
    @ApiOperation(value = "区域转化率导出", notes = "")
    @RequestMapping(value = "/regionConversionRateExport")
    public void regionConversionRateExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        dataStatisticService.regionConversionRateExport(request,response, excelForm);
    }

    @Log("客户来源数据统计")
    @ApiOperation(value = "客户来源数据统计")
    @PostMapping("/sourceModeDataStatistics")
    public ResultBody sourceModeDataStatistics(@RequestBody Map paramMap){
        return dataStatisticService.sourceModeDataStatistics(paramMap);
    }

    @Log("客户来源数据统计导出")
    @ApiOperation(value = "客户来源数据统计导出")
    @PostMapping("/sourceModeDataStatisticsExport")
    public void sourceModeDataStatisticsExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm){
        dataStatisticService.sourceModeDataStatisticsExport(request,response, excelForm);
    }

    @Log("客户来源数据统计导出")
    @ApiOperation(value = "客户来源数据统计导出")
    @PostMapping("/sourceModeDataStatisticsExportNew")
    public String sourceModeDataStatisticsExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm){
        return dataStatisticService.sourceModeDataStatisticsExportNew(request,response, excelForm);
    }

    @Log("集团数据统计")
    @ApiOperation(value = "集团数据统计")
    @PostMapping("/groupDataStatistics")
    public ResultBody groupDataStatistics(@RequestBody Map paramMap){
        return dataStatisticService.groupDataStatistics(paramMap);
    }

    @Log("集团数据导出")
    @CessBody
    @ApiOperation(value = "集团数据导出", notes = "")
    @RequestMapping(value = "/groupDataExport")
    public void groupDataExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        dataStatisticService.groupDataExport(request,response, excelForm);
    }

    @Log("客户成交周期数据统计")
    @ApiOperation(value = "客户成交周期数据统计")
    @PostMapping("/customerTradeCycleDataStatistics")
    public ResultBody customerTradeCycleDataStatistics(@RequestBody Map paramMap){
        return dataStatisticService.customerTradeCycleDataStatistics(paramMap);
    }

    @Log("客户成交周期数据统计详情")
    @ApiOperation(value = "客户成交周期数据统计详情")
    @PostMapping("/customerTradeCycleDataStatisticsGather")
    public ResultBody customerTradeCycleDataStatisticsGather(@RequestBody Map paramMap){
        return dataStatisticService.customerTradeCycleDataStatisticsGather(paramMap);
    }

    @Log("客户成交周期数据统计详情导出")
    @ApiOperation(value = "客户成交周期数据统计详情导出")
    @PostMapping("/customerTradeCycleDataStatisticsGatherExport")
    public void customerTradeCycleDataStatisticsGatherExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm){
        dataStatisticService.customerTradeCycleDataStatisticsGatherExport(request,response, excelForm);
    }

    @Log("打卡统计")
    @ApiOperation(value = "打卡统计")
    @PostMapping("/punchInStatistics")
    public ResultBody punchInStatistics(@RequestBody Map paramMap){
        return dataStatisticService.punchInStatistics(paramMap);
    }

    @Log("打卡统计项目明细")
    @ApiOperation(value = "打卡统计项目明细")
    @PostMapping("/punchInProjectDetail")
    public ResultBody punchInProjectDetail(@RequestBody Map paramMap){
        return dataStatisticService.punchInProjectDetail(paramMap);
    }

    @Log("打卡统计客户明细")
    @ApiOperation(value = "打卡统计客户明细")
    @PostMapping("/punchInCustomerDetail")
    public ResultBody punchInCustomerDetail(@RequestBody Map paramMap){
        return dataStatisticService.punchInCustomerDetail(paramMap);
    }
}
