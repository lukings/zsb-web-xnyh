package cn.visolink.system.invalidvisit.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.invalidvisit.service.InvalidVisitService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName InvalidVisitController
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/10 15:53
 **/
@RestController
@RequestMapping("/invalidVisit")
public class InvalidVisitController {
    @Autowired
    private InvalidVisitService invalidVisitService;

    @Log("导出无效客户台账")
    @PostMapping("/invalidVisitExport")
    @CessBody
    @ApiOperation(value = "导出无效客户台账")
    public void invalidVisitExport(@RequestBody Map map, HttpServletRequest request, HttpServletResponse response) throws IOException {
        invalidVisitService.invalidVisitExport(request, response, map);
    }

    @Log("查询无效客户台账")
    @PostMapping("/findInvalidVisitList")
    @CessBody
    @ApiOperation(value = "查询无效客户台账")
    public Map findInvalidVisitList(@RequestBody Map map) {

        return invalidVisitService.findInvalidVisitList(map);
    }

    @Log("查询来访原因")
    @PostMapping("/getVisitReason")
    @CessBody
    @ApiOperation(value = "查询来访原因")
    public List<Map> getVisitReason(HttpServletRequest request) {
        String authCompanyId = request.getHeader("AuthCompanyID");
        return invalidVisitService.getVisitReason(authCompanyId);
    }
}
