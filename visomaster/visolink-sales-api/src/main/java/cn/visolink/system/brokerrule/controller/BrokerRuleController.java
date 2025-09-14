package cn.visolink.system.brokerrule.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.brokerrule.model.form.BrokerRuleForm;
import cn.visolink.system.brokerrule.model.form.BrokerRuleList;
import cn.visolink.system.brokerrule.model.vo.BrokerRuleVO;
import cn.visolink.system.brokerrule.service.BrokerRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/2/3
 */
@RestController
@Api(tags = "全民经纪人规则")
@RequestMapping("/brokerRule")
public class BrokerRuleController {

    @Autowired
    public BrokerRuleService brokerRuleService;

    @Log("查询全民经纪人规则列表")
    @ApiOperation(value = "查询全民经纪人规则", notes = "规则数据")
    @RequestMapping(value = "/getBrokerRuleList", method = RequestMethod.POST)
    public ResultBody getBrokerRule(@RequestBody BrokerRuleForm brokerRuleForm) {
        return brokerRuleService.getBrokerRuleList(brokerRuleForm);
    }

    @Log("查询全民经纪人规则详情")
    @ApiOperation(value = "查询全民经纪人规则详情", notes = "规则数据")
    @RequestMapping(value = "/getBrokerRuleDetails", method = RequestMethod.POST)
    public ResultBody getBrokerRuleDetails(@RequestBody Map map) {
        List<BrokerRuleVO> result = brokerRuleService.getBrokerRuleDetails(map);
        return ResultBody.success(result);
    }

    @Log("根据项目查询全民经纪人规则详情")
    @ApiOperation(value = "根据项目查询全民经纪人规则详情", notes = "规则数据")
    @RequestMapping(value = "/getBrokerRuleDetailsByProId", method = RequestMethod.POST)
    public ResultBody getBrokerRuleDetailsByProId(@RequestBody Map map) {
        return ResultBody.success(brokerRuleService.getBrokerRuleDetailsByProId(map));
    }

    @Log("全民经纪人规则相关的操作")
    @ApiOperation(value = "全民经纪人规则相关的操作", notes = "规则数据")
    @RequestMapping(value = "/saveBrokerRule", method = RequestMethod.POST)
    public ResultBody saveBrokerRule(@RequestBody BrokerRuleList brokerRuleList) {
        Map map = brokerRuleService.saveBrokerRule(brokerRuleList);
        return ResultBody.success(map);
    }

    @Log("删除全民经纪人规则")
    @ApiOperation(value = "删除全民经纪人规则", notes = "删除规则数据")
    @RequestMapping(value = "/delBrokerRule", method = RequestMethod.POST)
    public ResultBody delBrokerRule(@RequestBody Map map) {
        if (map.get("activityId")==null || "".equals(map.get("activityId"))){
            return ResultBody.error(-21_0006,"活动ID未传！！");
        }else{
            try{
                brokerRuleService.delBrokerRule(map.get("activityId")+"");
                return ResultBody.success("删除成功！！");
            }catch (Exception e){
                e.printStackTrace();
                return ResultBody.error(-21_0006,"删除全民经纪人规则异常！！");
            }
        }
    }

    @Log("禁用全民经纪人规则")
    @ApiOperation(value = "禁用全民经纪人规则", notes = "禁用规则数据")
    @RequestMapping(value = "/disabledBrokerRule", method = RequestMethod.POST)
    public ResultBody disabledBrokerRule(@RequestBody Map map) {
        if (map.get("activityId")==null || "".equals(map.get("activityId"))){
            return ResultBody.error(-21_0006,"活动ID未传！！");
        }else{
            try{
                brokerRuleService.disabledBrokerRule(map);
                return ResultBody.success("禁用成功！！");
            }catch (Exception e){
                e.printStackTrace();
                return ResultBody.error(-21_0006,"禁用全民经纪人规则异常！！");
            }
        }
    }

    @Log("启用全民经纪人规则")
    @ApiOperation(value = "启用全民经纪人规则", notes = "启用全民经纪人规则")
    @RequestMapping(value = "/enableBrokerRule", method = RequestMethod.POST)
    public ResultBody enableBrokerRule(@RequestBody Map map) {
        if (map.get("activityId")==null || "".equals(map.get("activityId"))){
            return ResultBody.error(-21_0006,"活动ID未传！！");
        }else{
            try{
                return brokerRuleService.enableBrokerRule(map);
            }catch (Exception e){
                e.printStackTrace();
                return ResultBody.error(-21_0006,"启用全民经纪人规则异常！！");
            }
        }
    }

    @Log("查询项目是否有启动的活动")
    @ApiOperation(value = "查询项目是否有启动的活动", notes = "规则数据")
    @RequestMapping(value = "/getBrokerRuleIsEnable", method = RequestMethod.POST)
    public ResultBody getBrokerRuleIsEnable(@RequestBody Map map) {
        Map returnMap = brokerRuleService.getBrokerRuleIsEnable(map);
        return ResultBody.success(returnMap);
    }


}
