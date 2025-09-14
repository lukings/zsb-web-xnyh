package cn.visolink.system.enterprisedatabase.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.enterprisedatabase.model.EdCommonFieldConfigurationVo;
import cn.visolink.system.enterprisedatabase.model.EdCustomerInfoVo;
import cn.visolink.system.enterprisedatabase.model.EdImportCustomerLogVo;
import cn.visolink.system.enterprisedatabase.model.EdImportTemplateConfigurationVo;
import cn.visolink.system.enterprisedatabase.service.EnterpriseDatabaseService;
import cn.visolink.utils.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luqianqian
 * @Description: 企业数据库控制类
 * @date 2025/2/10 15:50
 */
@RestController
@Api(tags = "企业数据库")
@RequestMapping("/system/enterpriseDatabase")
public class EnterpriseDatabaseController {
    @Autowired
    private EnterpriseDatabaseService enterpriseDatabaseService;

    @Log("企业数据库-通用字段配置列表查询")
    @ApiOperation(value = "企业数据库-通用字段配置列表查询", notes = "企业数据库-通用字段配置列表查询")
    @RequestMapping(value = "/selectEdCommonFieldConfigurationList", method = RequestMethod.POST)
    public ResultBody selectProjectRule(@RequestBody Map map) {
        return enterpriseDatabaseService.selectEdCommonFieldConfigurationList(map);
    }

    @Log("企业数据库-招商宝数据库字段查询")
    @ApiOperation(value = "企业数据库-招商宝数据库字段查询", notes = "企业数据库-招商宝数据库字段查询")
    @RequestMapping(value = "/selectZsbTableFieldList", method = RequestMethod.GET)
    public ResultBody selectZsbTableFieldList(@Param("tableName") String tableName) {
        return enterpriseDatabaseService.selectZsbTableFieldList(tableName);
    }

    @Log("企业数据库-通用字段配置详情查询")
    @ApiOperation(value = "企业数据库-通用字段配置详情查询", notes = "企业数据库-通用字段配置详情查询")
    @RequestMapping(value = "/selectEdCommonFieldConfigurationDetail", method = RequestMethod.POST)
    public ResultBody selectEdCommonFieldConfigurationDetail(@RequestBody EdCommonFieldConfigurationVo map) {
        return enterpriseDatabaseService.selectEdCommonFieldConfigurationDetail(map);
    }

    @Log("企业数据库-通用字段配置编辑")
    @ApiOperation(value = "企业数据库-通用字段配置编辑", notes = "企业数据库-通用字段配置编辑")
    @RequestMapping(value = "/addOrEditEdCommonFieldConfiguration", method = RequestMethod.POST)
    public ResultBody addOrEditEdCommonFieldConfiguration(@RequestBody EdCommonFieldConfigurationVo map) {
        return enterpriseDatabaseService.addOrEditEdCommonFieldConfiguration(map);
    }

    @Log("企业数据库-导入模板列表头查询")
    @ApiOperation(value = "企业数据库-导入模板列表头查询", notes = "企业数据库-导入模板列表头查询")
    @RequestMapping(value = "/selectEdImportTemplateConfigurationTableHeadList", method = RequestMethod.POST)
    public ResultBody selectEdImportTemplateConfigurationTableHeadList(@RequestBody Map map) {
        return enterpriseDatabaseService.selectEdImportTemplateConfigurationTableHeadList(map);
    }

    @Log("企业数据库-导入模板列表查询")
    @ApiOperation(value = "企业数据库-导入模板列表查询", notes = "企业数据库-导入模板列表查询")
    @RequestMapping(value = "/selectEdImportTemplateConfigurationList", method = RequestMethod.POST)
    public ResultBody selectEdImportTemplateConfigurationList(@RequestBody EdImportTemplateConfigurationVo map) {
        return enterpriseDatabaseService.selectEdImportTemplateConfigurationList(map);
    }

    @Log("企业数据库-导入模板详情查询")
    @ApiOperation(value = "企业数据库-导入模板详情查询", notes = "企业数据库-导入模板详情查询")
    @RequestMapping(value = "/selectEdImportTemplateConfigurationDetail", method = RequestMethod.POST)
    public ResultBody selectEdImportTemplateConfigurationDetail(@RequestBody EdImportTemplateConfigurationVo map) {
        return enterpriseDatabaseService.selectEdImportTemplateConfigurationDetail(map);
    }

    @Log("企业数据库-导入模板编辑")
    @ApiOperation(value = "企业数据库-导入模板编辑", notes = "企业数据库-导入模板编辑")
    @RequestMapping(value = "/addOrEditEdImportTemplateConfiguration", method = RequestMethod.POST)
    public ResultBody addOrEditEdImportTemplateConfiguration(@RequestBody EdImportTemplateConfigurationVo map) {
        return enterpriseDatabaseService.addOrEditEdImportTemplateConfiguration(map);
    }

    @Log("企业数据库-导入模板启用/禁用")
    @ApiOperation(value = "企业数据库-导入模板启用/禁用", notes = "企业数据库-导入模板启用/禁用")
    @RequestMapping(value = "/updateEdImportTemplateConfigurationStatus", method = RequestMethod.POST)
    public ResultBody updateEdImportTemplateConfigurationStatus(@RequestBody EdImportTemplateConfigurationVo map) {
        return enterpriseDatabaseService.updateEdImportTemplateConfigurationStatus(map);
    }

    @Log("企业数据库-列表查询")
    @ApiOperation(value = "企业数据库-列表查询", notes = "企业数据库-列表查询")
    @RequestMapping(value = "/selectEdCustomerList", method = RequestMethod.POST)
    public ResultBody selectEdCustomerList(@RequestBody EdCustomerInfoVo map) {
        return enterpriseDatabaseService.selectEdCustomerList(map,"PC");
    }

    @Log("企业数据库-列表查询")
    @CessBody
    @ApiOperation(value = "企业数据库-列表查询", notes = "企业数据库-列表查询")
    @RequestMapping(value = "/selectEdCustomerListApp", method = RequestMethod.POST)
    public ResultBody selectEdCustomerListApp(@RequestBody EdCustomerInfoVo map) {
        return enterpriseDatabaseService.selectEdCustomerList(map,"APP");
    }

    @Log("企业数据库-企业数据详情查看字段配置")
    @ApiOperation(value = "企业数据库-企业数据详情查看字段配置", notes = "企业数据库-企业数据详情查看字段配置")
    @RequestMapping(value = "/selectEdCustomerDetailFiledConfiguration", method = RequestMethod.POST)
    public ResultBody selectEdCustomerDetailFiledConfiguration(@RequestBody Map map) {
        return enterpriseDatabaseService.selectEdCustomerDetailFiledConfiguration(map);
    }

    @Log("企业数据库-企业数据详情查看")
    @ApiOperation(value = "企业数据库-企业数据详情查看", notes = "企业数据库-企业数据详情查看")
    @RequestMapping(value = "/selectEdCustomerDetail", method = RequestMethod.POST)
    public ResultBody selectEdCustomerDetail(@RequestBody EdCustomerInfoVo map) {
        return enterpriseDatabaseService.selectEdCustomerDetail(map);
    }

    @Log("企业数据库-批量导入模板查询")
    @ApiOperation(value = "企业数据库-批量导入模板查询", notes = "企业数据库-批量导入模板查询")
    @RequestMapping(value = "/selectEdImportTemplateList", method = RequestMethod.POST)
    public ResultBody selectEdImportTemplateList(@RequestBody Map map) {
        return enterpriseDatabaseService.selectEdImportTemplateList(map);
    }

    @Log("企业数据库-批量导入客户")
    @ApiOperation(value = "企业数据库-批量导入客户", notes = "企业数据库-批量导入客户")
    @RequestMapping(value = "/importEdCustomer", method = RequestMethod.POST)
    public ResultBody importEdCustomer(@RequestParam("file") MultipartFile file,
                                       @RequestParam("templateId") String templateId,
                                       @RequestParam("dataSourcesCode") String dataSourcesCode,
                                       @RequestParam("dataSourcesName") String dataSourcesName,
                                       @RequestParam("isSynTo") String isSynTo) throws IOException {
        //进入接口判断模板 根据模板ID查询模板配置信息
        if (StringUtils.isEmpty(templateId)){
            return ResultBody.error(500,"模板ID不能为空！！！");
        }
        if ("-1".equals(templateId) && StringUtils.isEmpty(dataSourcesCode) && StringUtils.isEmpty(dataSourcesName)){
            return ResultBody.error(500,"您选择了通用模板导入，请选择导入数据来源！！！");
        }
        Map map = new HashMap();
        map.put("templateId",templateId);
        map.put("isSynTo",isSynTo);
        map.put("dataSourcesCode",dataSourcesCode);
        map.put("dataSourcesName",dataSourcesName);
        return enterpriseDatabaseService.importEdCustomer(file,map);
    }

    @Log("企业数据库-批量导入客户查询")
    @ApiOperation(value = "企业数据库-批量导入客户查询", notes = "企业数据库-批量导入客户查询")
    @RequestMapping(value = "/selectEdImportCustomerList", method = RequestMethod.POST)
    public ResultBody selectEdImportCustomerList(@RequestBody Map map) {
        return enterpriseDatabaseService.selectEdImportCustomerList(map);
    }

    @Log("企业数据库-批量导入客户确认")
    @ApiOperation(value = "企业数据库-批量导入客户确认", notes = "企业数据库-批量导入客户确认")
    @RequestMapping(value = "/importEdCustomerConfirm", method = RequestMethod.POST)
    public ResultBody importEdCustomerConfirm(@RequestBody Map map) {
        return enterpriseDatabaseService.importEdCustomerConfirm(map);
    }

    @Log("企业数据库-批量导入客户定时")
    @ApiOperation(value = "企业数据库-批量导入客户定时", notes = "企业数据库-批量导入客户定时")
    @RequestMapping(value = "/importEdCustomerTiming", method = RequestMethod.POST)
    public void importEdCustomerTiming() {
        enterpriseDatabaseService.importEdCustomerTiming();
    }

    @Log("企业数据库-历史导入记录台账")
    @ApiOperation(value = "企业数据库-历史导入记录台账", notes = "企业数据库-历史导入记录台账")
    @RequestMapping(value = "/selectEdImportCustomerHistoryList", method = RequestMethod.POST)
    public ResultBody selectEdImportTemplateHistoryList(@RequestBody EdImportCustomerLogVo map) {
        return enterpriseDatabaseService.selectEdImportTemplateHistoryList(map);
    }

    @Log("企业数据库-查询导入批次成功记录")
    @ApiOperation(value = "企业数据库-查询导入批次成功记录", notes = "企业数据库-查询导入批次成功记录")
    @RequestMapping(value = "/selectEdImportCustomerHistorySuccessList", method = RequestMethod.POST)
    public ResultBody selectEdImportCustomerHistorySuccessList(@RequestBody EdImportCustomerLogVo map) {
        return enterpriseDatabaseService.selectEdImportCustomerHistorySuccessList(map);
    }

    @Log("企业数据库-客户过保")
    @ApiOperation(value = "企业数据库-客户过保", notes = "企业数据库-客户过保")
    @RequestMapping(value = "/edCustomerOverProtectTiming", method = RequestMethod.POST)
    public void edCustomerOverProtectTiming() {
        enterpriseDatabaseService.edCustomerOverProtectTiming();
    }

    @Log("企业数据库-生成导入模板")
    @ApiOperation(value = "企业数据库-生成导入模板", notes = "企业数据库-生成导入模板")
    @RequestMapping(value = "/downloadEdImportTemplate", method = RequestMethod.POST)
    public void downloadEdImportTemplate(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map) {
        enterpriseDatabaseService.downloadEdImportTemplate(request, response, map);
    }

    /**
     * 获取登录人权限内可分配客户的项目
     * */
    @Log("获取登录人权限内可分配客户的项目")
    @ApiOperation(value = "获取登录人权限内可分配客户的项目", notes = "获取登录人权限内可分配客户的项目")
    @PostMapping(value = "/getGlAllocationPropertyConsultantPro")
    public ResultBody getGlAllocationPropertyConsultantPro(@RequestBody Map map) {
        return enterpriseDatabaseService.getGlAllocationPropertyConsultantPro(map);
    }

    @Log("企业数据库-分配客户(加入话单)")
    @ApiOperation(value = "企业数据库-分配客户(加入话单)", notes = "企业数据库-分配客户(加入话单)")
    @RequestMapping(value = "/edCustomerJoinTheCallDistribution", method = RequestMethod.POST)
    public ResultBody edCustomerJoinTheCallDistribution(@RequestBody Map map) {
        return enterpriseDatabaseService.edCustomerJoinTheCallDistribution(map);
    }
//
//    @Log("企业数据库-加入机器人")
//    @ApiOperation(value = "企业数据库-加入机器人", notes = "企业数据库-加入机器人")
//    @RequestMapping(value = "/edCustomerJoinRobot", method = RequestMethod.POST)
//    public ResultBody edCustomerJoinRobot(@RequestBody Map map) {
//        return enterpriseDatabaseService.edCustomerJoinRobot(map);
//    }
//
    @Log("企业数据库-企业数据库数据智能匹配接口")
    @ApiOperation(value = "企业数据库-企业数据库数据智能匹配接口", notes = "企业数据库-企业数据库数据智能匹配接口")
    @RequestMapping(value = "/edCustomerDataMatch", method = RequestMethod.GET)
    public ResultBody edCustomerDataMatch(@Param("customerName") String customerName) {
        Map map = new HashMap();
        map.put("customerName",customerName);
        return enterpriseDatabaseService.edCustomerDataMatch(map);
    }

    @Log("企业数据库-企业数据库数据对接招商宝")
    @ApiOperation(value = "企业数据库-企业数据库数据对接招商宝", notes = "企业数据库-企业数据库数据对接招商宝")
    @RequestMapping(value = "/edCustomerDataToZsb", method = RequestMethod.POST)
    public ResultBody edCustomerDataToZsb(@RequestBody Map map) {
        return enterpriseDatabaseService.edCustomerDataToZsb(map);
    }

    @Log("企业数据库-添加行业字典")
    @ApiOperation(value = "企业数据库-添加行业字典", notes = "企业数据库-添加行业字典")
    @RequestMapping(value = "/addDict", method = RequestMethod.POST)
    public ResultBody addDict(@RequestBody Map map) {
        return enterpriseDatabaseService.addDict(map);
    }

    @Log("企业数据库-查询所属行业")
    @ApiOperation(value = "查询所属行业")
    @GetMapping("/getEdIndustryOne")
    public ResultBody getEdIndustryOne() {
        return enterpriseDatabaseService.getEdIndustryOne();
    }

    @CessBody
    @Log("企业数据库-查询企业数据库信息")
    @ApiOperation(value = "查询企业数据库信息")
    @GetMapping("/selectEdCustomerInfo")
    public ResultBody selectEdCustomerInfo(@Param("customerId") String customerId) {
        if (StringUtils.isEmpty(customerId)){
            return ResultBody.error(400,"数据ID不能为空");
        }
        return enterpriseDatabaseService.selectEdCustomerInfo(customerId);
    }

    @Log("查询外呼超时未消费项目")
    @ApiOperation(value = "查询外呼超时未消费项目")
    @GetMapping("/getTimeoutWarning")
    public ResultBody getTimeoutWarning() {

        return enterpriseDatabaseService.getTimeoutWarning();
    }
    @Log("修改外呼超时未消费项目")
    @ApiOperation(value = "修改外呼超时未消费项目")
    @GetMapping("/updateTimeoutWarning")
    public ResultBody updateTimeoutWarning(@Param("id")String id) {
        enterpriseDatabaseService.updateTimeoutWarning(id);
        return ResultBody.success("成功");
    }


}
