package cn.visolink.system.wechattoken.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.wechattoken.service.WeChatAccessTokenService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/7/3
 */
@RestController
@RequestMapping("system/weChatToken")
public class WeChatAccessTokenController {

    @Autowired
    private WeChatAccessTokenService weChatAccessTokenService;

    @Log("查询微信小程序token")
    @GetMapping("getWeChatAccessToken")
    @ApiOperation(value = "查询微信小程序token")
    public ResultBody getReceptionCustomerInfo() {
        return weChatAccessTokenService.getWeChatAccessToken();
    }

    @Log("查询key值")
    @GetMapping("getRedisByKey")
    @ApiOperation(value = "查询key值")
    public ResultBody getRedisByKey(String key) {
        return weChatAccessTokenService.getRedisByKey(key);
    }
}
