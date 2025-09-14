package cn.visolink.system.userQuery.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.userQuery.service.UserQueryConditionsService;
import cn.visolink.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户查询条件管理Controller
 * @author system
 * @date 2025/1/27
 */
@RestController
@RequestMapping("/userQuery")
@Api(tags = "用户查询条件管理")
@Slf4j
public class UserQueryConditionsController {
    
    @Autowired
    private UserQueryConditionsService userQueryConditionsService;
    

    /**
     * 获取当前用户指定接口的最近一次查询条件
     */
    @PostMapping("/getLatestConditions")
    @ResponseBody
    @ApiOperation(value = "获取最近查询条件", notes = "获取当前用户指定接口的最近一次查询条件")
    public ResultBody getLatestConditions(@RequestParam String interfaceName, 
                                        @RequestParam(required = false) String dataRange,
                                        @RequestParam(required = false) String mapType) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            // 获取当前登录用户ID
            String userId = request.getHeader("userid");
            String userName = request.getHeader("username");
            if(StringUtils.isEmpty(userId)){
                userId = SecurityUtils.getUserId();
            }
            
            if (StringUtils.isBlank(userId)) {
                return ResultBody.error(-10001, "用户未登录");
            }
            
            // 获取查询条件（带过滤）
            Map<String, Object> conditions = userQueryConditionsService.getUserQueryConditionsWithFilter(userId, interfaceName, dataRange, mapType);
            
            return ResultBody.success(conditions);
        } catch (Exception e) {
            log.error("获取查询条件失败", e);
            return ResultBody.error(-10002, "获取查询条件失败：" + e.getMessage());
        }
    }
    
    /**
     * 清除当前用户指定接口的查询条件
     */
    @PostMapping("/clearConditions")
    @ResponseBody
    @ApiOperation(value = "清除查询条件", notes = "清除当前用户指定接口的查询条件")
    public ResultBody clearConditions(@RequestParam String interfaceName) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            // 获取当前登录用户ID
            String userId = request.getHeader("userid");
            String userName = request.getHeader("username");
            if(StringUtils.isEmpty(userId)){
                userId = SecurityUtils.getUserId();
            }
            
            if (StringUtils.isBlank(userId)) {
                return ResultBody.error(-10001, "用户未登录");
            }
            
            userQueryConditionsService.clearUserQueryConditions(userId, interfaceName);
            
            return ResultBody.success("清除成功");
        } catch (Exception e) {
            log.error("清除查询条件失败", e);
            return ResultBody.error(-10002, "清除查询条件失败：" + e.getMessage());
        }
    }
}
