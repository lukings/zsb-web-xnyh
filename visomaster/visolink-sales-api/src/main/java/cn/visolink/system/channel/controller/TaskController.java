package cn.visolink.system.channel.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.channel.model.form.ExcelForm;
import cn.visolink.system.channel.model.vo.BatchDeleteDuplicateCustomerRequest;
import cn.visolink.system.channel.model.vo.BatchDeleteDuplicateCustomerResponse;
import cn.visolink.system.channel.model.vo.CustomerDuplicateCheckRequest;
import cn.visolink.system.channel.model.vo.CustomerDuplicateCheckResponse;
import cn.visolink.system.channel.model.vo.TaskVo;
import cn.visolink.system.channel.service.ProjectCluesService;
import cn.visolink.system.channel.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Publicpool前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2019-09-02
 */
@RestController
@Api(tags = "task")
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectCluesService projectCluesService;

    @Log("创建任务")
    @ApiOperation(value = "创建任务", notes = "创建任务")
    @PostMapping(value = "/createTask")
    public ResultBody createTask(@RequestBody TaskVo taskVo){
        try {
            return taskService.createTask(taskVo);
        } catch (Exception e) {
            return ResultBody.error(-1000_01, e.getMessage());
        }
    }
    
    @Log("更新任务")
    @ApiOperation(value = "更新任务的结束时间及指标")
    @PostMapping("/updateTask")
    public ResultBody updateTask(@RequestBody TaskVo taskVo) {
        return taskService.updateTask(taskVo);
    }
    
    @Log("终止任务")
    @ApiOperation(value = "更新任务的任务状态")
    @PostMapping("/stopTask")
    public ResultBody stopTask(@RequestBody TaskVo taskVo) {
        return taskService.stopTask(taskVo);
    }
    


    @Log("维护月度任务历史数据")
    @ApiOperation(value = "维护月度任务历史数据", notes = "维护月度任务历史数据")
    @GetMapping(value = "/operation")
    public void operation(){
        projectCluesService.operation();
    }


    @Log("获取任务团队销售经理")
    @ApiOperation(value = "获取任务团队销售经理")
    @PostMapping("/getTeamXsjl")
    public ResultBody  getTeamXsjl(@RequestBody TaskVo taskVo) {
        return  taskService.getTeamXsjl(taskVo);
    }

    @Log("客户判重检查")
    @ApiOperation(value = "客户判重检查", notes = "在创建任务前检查客户是否与现有报备客户重复")
    @PostMapping("/checkCustomerDuplicate")
    public ResultBody<CustomerDuplicateCheckResponse> checkCustomerDuplicate(@RequestBody CustomerDuplicateCheckRequest request) {
        try {
            return taskService.checkCustomerDuplicate(request);
        } catch (Exception e) {
            return ResultBody.error(-1000_01, e.getMessage());
        }
    }

    @Log("批量删除重复客户")
    @ApiOperation(value = "批量删除重复客户", notes = "批量逻辑删除重复客户的项目线索和跟进记录")
    @PostMapping("/batchDeleteDuplicateCustomer")
    public ResultBody<BatchDeleteDuplicateCustomerResponse> batchDeleteDuplicateCustomer(@RequestBody BatchDeleteDuplicateCustomerRequest request) {
        try {
            return taskService.batchDeleteDuplicateCustomer(request);
        } catch (Exception e) {
            return ResultBody.error(-1000_01, e.getMessage());
        }
    }

}

