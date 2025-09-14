package cn.visolink.system.companyQw.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.companyQw.model.BQwMassTexting;
import cn.visolink.system.companyQw.service.CompanyQwMassTextingService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @ClassName CompanyController
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/12/31 9:33
 **/
@RestController
@Api(tags = "群发任务")
@RequestMapping("massTexting")
public class CompanyQwMassTextingController {

    @Autowired
    private CompanyQwMassTextingService companyQwMassTextingService;

    @Log("创建群发任务")
    @CessBody
    @ApiOperation(value = "创建群发任务", notes = "创建群发任务")
    @RequestMapping(value = "createMassTextingTasks", method = RequestMethod.POST)
    public ResultBody createMassTextingTasks(@RequestBody BQwMassTexting bQwMassTexting, HttpServletRequest request) {
        return companyQwMassTextingService.createMassTextingTasks(bQwMassTexting, request);
    }

    @Log("查询成员客户数量")
    @CessBody
    @ApiOperation(value = "查询成员客户数量", notes = "查询成员客户数量")
    @RequestMapping(value = "getUserCstCount", method = RequestMethod.POST)
    public ResultBody getUserCstCount(@RequestBody BQwMassTexting bQwMassTexting, HttpServletRequest request) {
        return companyQwMassTextingService.getUserCstCount(bQwMassTexting, request);
    }

    @Log("查询成员客户标签")
    @CessBody
    @ApiOperation(value = "查询成员客户标签", notes = "查询成员客户标签")
    @RequestMapping(value = "getUserCstTag", method = RequestMethod.POST)
    public ResultBody getUserCstTag(@RequestBody BQwMassTexting bQwMassTexting, HttpServletRequest request) {
        return companyQwMassTextingService.getUserCstTag(bQwMassTexting, request);
    }

    @Log("查看群发任务列表")
    @CessBody
    @ApiOperation(value = "查看群发任务列表", notes = "查看群发任务列表")
    @RequestMapping(value = "selectMassTextingTasks", method = RequestMethod.POST)
    public ResultBody selectMassTextingTasks(
            @ApiParam(name = "map", value = "{\"projectIds\":\"项目id逗号隔开\",\"taskName\":\"任务名称\",\"creater\":\"创建人\",\"status\":\"任务状态(0:已撤回 1：待群发 2：已群发)\",\"timeType\":\"时间类型(1:群发时间 2：创建时间)\",\"beginTime\":\"开始时间\",\"endTime\":\"结束时间\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
            @RequestBody Map map) {
        return companyQwMassTextingService.selectMassTextingTasks(map);
    }

    @Log("删除群发任务")
    @CessBody
    @ApiOperation(value = "删除群发任务", notes = "删除群发任务")
    @RequestMapping(value = "delMassTextingTasks", method = RequestMethod.POST)
    public ResultBody delMassTextingTasks(
            @ApiParam(name = "map", value = "{\"id\":\"id\"}")
            @RequestBody Map map) {
        return companyQwMassTextingService.delMassTextingTasks(map);
    }

    @Log("撤回群发任务")
    @CessBody
    @ApiOperation(value = "撤回群发任务", notes = "撤回群发任务")
    @RequestMapping(value = "reMassTextingTasks", method = RequestMethod.POST)
    public ResultBody reMassTextingTasks(
            @ApiParam(name = "map", value = "{\"id\":\"id\"}")
            @RequestBody Map map) {
        return companyQwMassTextingService.reMassTextingTasks(map);
    }

    @Log("重发任务")
    @CessBody
    @ApiOperation(value = "重发任务", notes = "重发任务")
    @RequestMapping(value = "reAddMassTextingTasks", method = RequestMethod.POST)
    public ResultBody reAddMassTextingTasks(
            @ApiParam(name = "map", value = "{\"id\":\"id\"}")
            @RequestBody Map map) {
        return companyQwMassTextingService.reAddMassTextingTasks(map);
    }


}
