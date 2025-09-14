package cn.visolink.system.ruleEditLog.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.ruleEditLog.service.RuleEditService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author
 * @date:2019-9-10
 * */

@RestController
@Api(tags = "参数修改")
@RequestMapping("/ruleEdit")
public class RuleEditController {
    @Autowired
    private RuleEditService ruleEditService;

    @Log("批次参数查询")
    @CessBody
    @ApiOperation(value = "批次参数查询")
    @PostMapping("/batchParamQuery")
    public ResultBody batchParamQuery(@RequestBody Map<String,Object> map){
//        if (map.get("editType")==null || "".equals(map.get("editType"))){
//            return ResultBody.error(-21_0006,"必传参数未传！");
//        }
        Map result = ruleEditService.batchParamQuery(map);
        return  ResultBody.success(result);
    }

    @Log("修改日志（批次）查询")
    @CessBody
    @ApiOperation(value = "修改日志（批次）查询")
    @PostMapping("/batchQuery")
    public ResultBody batchQuery(@RequestBody Map<String,Object> map){
        return  ResultBody.success(ruleEditService.batchQuery(map));
    }

    @Log("修改日志（详情）查询")
    @CessBody
    @ApiOperation(value = "修改日志（详情）查询")
    @PostMapping("/batchDetailQuery")
    public ResultBody batchDetailQuery(@RequestBody Map<String,Object> map){
        if (map.get("batchId")==null || "".equals(map.get("batchId"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }
        return  ResultBody.success(ruleEditService.batchDetailQuery(map));
    }


}
