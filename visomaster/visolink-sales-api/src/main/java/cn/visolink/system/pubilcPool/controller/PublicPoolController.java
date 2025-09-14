package cn.visolink.system.pubilcPool.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.pubilcPool.model.form.PublicPoolListSearch;
import cn.visolink.system.pubilcPool.model.form.RecoveryEdit;
import cn.visolink.system.pubilcPool.service.PublicPoolService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PublicPoolController
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/26 16:06
 **/
@RestController
@RequestMapping("/publicPool")
public class PublicPoolController {

    @Autowired
    private PublicPoolService publicPoolService;

    @Log("公池/淘客池列表")
    @ApiOperation(value = "公池/淘客池列表")
    @PostMapping("/getPublicPoolList")
    public ResultBody getPublicPoolList(@RequestBody PublicPoolListSearch paramMap){
        return ResultBody.success(publicPoolService.getPublicPoolList(paramMap));
    }

    @Log("公池/淘客池列表导出")
    @CessBody
    @ApiOperation(value = "公池/淘客池列表导出", notes = "公池/淘客池列表导出")
    @PostMapping(value = "/publicPoolExport")
    public void publicPoolExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        publicPoolService.publicPoolExport(request,response, param);
    }

    @Log("放弃/淘客记录")
    @ApiOperation(value = "放弃/淘客记录")
    @PostMapping("/getPublicPoolHisList")
    public ResultBody getPublicPoolHisList(@RequestBody PublicPoolListSearch paramMap){
        return ResultBody.success(publicPoolService.getPublicPoolHisList(paramMap));
    }

    @Log("放弃/淘客记录导出")
    @CessBody
    @ApiOperation(value = "放弃/淘客记录", notes = "放弃/淘客记录")
    @PostMapping(value = "/publicPoolHisExport")
    public void publicPoolHisExport(HttpServletRequest request, HttpServletResponse response,@RequestBody String param) {
        publicPoolService.publicPoolHisExport(request,response, param);
    }

    @Log("淘客池回收")
    @CessBody
    @ApiOperation(value = "淘客池回收", notes = "淘客池回收")
    @PostMapping(value = "/taoRecovery")
    public ResultBody taoRecovery(@RequestBody RecoveryEdit params) {
        return publicPoolService.taoRecovery(params);
    }


    @Log("入淘客池")
    @CessBody
    @ApiOperation(value = "入淘客池", notes = "入淘客池")
    @PostMapping(value = "/addTao")
    public ResultBody addTao(@RequestBody RecoveryEdit params) {
        return publicPoolService.addTao(params);
    }



    @Log("重分配")
    @CessBody
    @ApiOperation(value = "重分配", notes = "重分配")
    @PostMapping(value = "/channelPoolRedistribution")
    public ResultBody channelPoolRedistribution(@RequestBody RecoveryEdit params) {
        return publicPoolService.channelPoolRedistribution(params);
    }

}
