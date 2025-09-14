package cn.visolink.system.print.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.print.model.PrintInstall;
import cn.visolink.system.print.model.PrintTemplate;
import cn.visolink.system.print.service.PrintService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName PrintController
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/9 14:08
 **/
@RestController
@RequestMapping("/print")
public class PrintController {
    @Autowired
    private PrintService printService;

    @Log("查询模板字段及打印方式")
    @ApiOperation(value = "查询模板字段及打印方式", notes = "模板字段数据")
    @RequestMapping(value = "/getField")
    public ResultBody saveBrokerRule() {
        PrintInstall map = printService.findTemplateField();
        return ResultBody.success(map);
    }

    @Log("查询模板")
    @ApiOperation(value = "查询模板", notes = "模板数据")
    @RequestMapping(value = "/findTemplate", method = RequestMethod.POST)
    public ResultBody findTemplateByProId(@RequestBody Map map) {
        if (map.get("projectId")==null || "".equals(map.get("projectId"))
                || map.get("printType")==null || "".equals(map.get("printType"))){
            return ResultBody.error(-12_0005,"项目ID或模板类型未传");
        }
        PrintTemplate Template = printService.findTemplateByProId(map.get("projectId")+"",map.get("printType")+"");
        return ResultBody.success(Template);
    }

    @Log("新增模板")
    @ApiOperation(value = "新增模板", notes = "新增模板")
    @RequestMapping(value = "/addTemplate", method = RequestMethod.POST)
    public ResultBody addTemplate(@RequestBody PrintTemplate printTemplate) {
        try{
            printService.addTemplate(printTemplate);
            return ResultBody.success("保存模板成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-12_0005,"保存模板异常！");
        }
    }

    @Log("修改模板")
    @ApiOperation(value = "修改模板", notes = "修改模板")
    @RequestMapping(value = "/updateTemplate", method = RequestMethod.POST)
    public ResultBody updateTemplate(@RequestBody PrintTemplate printTemplate) {
        try{
            printService.updateTemplate(printTemplate);
            return ResultBody.success("修改模板成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-12_0005,"修改模板异常！");
        }
    }

}
