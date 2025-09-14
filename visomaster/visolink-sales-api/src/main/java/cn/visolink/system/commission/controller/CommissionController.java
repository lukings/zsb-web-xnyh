package cn.visolink.system.commission.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.commission.service.CommissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * CommissionDetail前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2020-06-22
 */
@RestController
@Api(tags = "佣金")
@RequestMapping("/commission")
public class CommissionController {

    @Autowired
    public CommissionService commissionService;

    @Log("获取佣金列表")
    @ApiOperation(value = "获取佣金列表", notes = "获取佣金列表")
    @PostMapping(value = "getCommissionList")
    public ResultBody getCommissionList(@ApiParam(name = "map", value = "{\"brokerId\":\"经纪人id\",\"pageNum\":\"当前页数\",\"pageSize\":\"每页条数\"}")
                                        @RequestBody Map<String, Object> param) {
        return ResultBody.success(commissionService.getCommissionList(param));
    }

    @Log("获取无效佣金列表")
    @ApiOperation(value = "获取无效佣金列表", notes = "获取无效佣金列表")
    @PostMapping(value = "getInvalidCommissionList")
    public ResultBody getInvalidCommissionList(@ApiParam(name = "map", value = "{\"brokerId\":\"经纪人id\",\"pageNum\":\"当前页数\",\"pageSize\":\"每页条数\"}")
                                        @RequestBody Map<String, Object> param) {
        return ResultBody.success(commissionService.getInvalidCommissionList(param));
    }

}

