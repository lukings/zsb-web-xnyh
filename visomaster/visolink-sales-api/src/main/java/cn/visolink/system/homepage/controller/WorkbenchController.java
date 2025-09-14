package cn.visolink.system.homepage.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.homepage.service.WorkbenchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(tags = "工作台")
@RequestMapping("/homePageWorkbench")
public class WorkbenchController {
    @Autowired
    private WorkbenchService workbenchService;

    @Log("查询我的待办")
    @PostMapping("/getPendingList")
    public ResultBody getPendingList(@RequestBody Map map){
        return workbenchService.getPendingList(map);
    }

    @Log("查询我的消息")
    @PostMapping("/getSendToMessage")
    public ResultBody getSendToMessage(@RequestBody Map map){
        return workbenchService.getSendToMessage(map);
    }

    @Log("查询未读消息条数")
    @PostMapping("/getMessageSize")
    public ResultBody getMessageSize(HttpServletRequest request){
        Map map = new HashMap();
        return workbenchService.getMessageSize(map);
    }

    @Log("消息全部已读")
    @PostMapping("/updMessIsRead")
    public ResultBody updMessIsRead(@RequestBody Map map, HttpServletRequest request){
        return workbenchService.updMessIsRead(map);
    }

    @Log("修改消息已读")
    @ApiOperation(value = "修改消息已读", notes = "修改消息已读")
    @PostMapping(value = "/updateMessage")
    public ResultBody updateMessage(
            @ApiParam(name = "map", value = "{\"id\":\"消息id\"}")
            @RequestBody Map map) {
        return workbenchService.updateMessage(map);
    }
}
