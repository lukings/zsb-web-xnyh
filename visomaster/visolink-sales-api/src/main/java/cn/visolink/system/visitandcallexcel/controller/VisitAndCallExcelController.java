package cn.visolink.system.visitandcallexcel.controller;


import cn.hutool.core.util.StrUtil;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.visitandcallexcel.service.VisitAndCallExcelService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户来访，来电，去电台账导出
 *
 * @author ligengying
 * @date 20191119
 */
@Controller
@RequestMapping("system/excelMessageInfo")
public class VisitAndCallExcelController {

    @Autowired
    private VisitAndCallExcelService excelService;

    @Log("导出来电，去电，来访台账")
    @PostMapping("getDownloadVisitAndCallInfo")
    @CessBody
    @ApiOperation(value = "导出来电，去电，来访台账")
    public void getDownloadVisitAndCallInfo(@RequestBody String param,HttpServletRequest request, HttpServletResponse response) throws IOException {
       excelService.getDownloadVisitAndCallInfo(request, response, param);
    }

    @Log("查询来电，去电，来访台账")
    @PostMapping("getVisitAndCallInfo")
    @CessBody
    @ApiOperation(value = "查询来电，去电，来访台账")
    public Map getVisitAndCallInfo(@RequestBody Map map,HttpServletRequest request) {
        map.put("AuthCompanyID",request.getHeader("AuthCompanyID"));
        return excelService.getVisitAndCallInfo(map);
    }

    @Log("导出接待大使判客台账")
    @PostMapping("getDownloadReceptionCustomerExcel")
    @CessBody
    @ApiOperation(value = "导出接待大使判客台账")
    public void getDownloadReceptionCustomerExcel(@RequestBody String param,HttpServletRequest request, HttpServletResponse response) throws IOException {
        excelService.getDownloadReceptionCustomerExcel(request, response, param);
    }

    @Log("查询接待大使判客台账")
    @PostMapping("getReceptionCustomerInfo")
    @CessBody
    @ApiOperation(value = "查询接待大使判客台账")
    public Map getReceptionCustomerInfo(@RequestBody Map map,HttpServletRequest request) {
        String authCompanyId = request.getHeader("AuthCompanyID");
        map.put("AuthCompanyID",authCompanyId);
        return excelService.getReceptionCustomerInfo(map);
    }
}
