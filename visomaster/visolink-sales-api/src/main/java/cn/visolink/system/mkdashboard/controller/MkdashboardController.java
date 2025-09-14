package cn.visolink.system.mkdashboard.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.HttpRequestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName MkdashboardController
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/3/18 22:22
 **/
@RestController
@RequestMapping("/mkdashboard")
public class MkdashboardController {
    @Log("中转中台请求")
    @ApiOperation(value = "中转中台请求")
    @RequestMapping(value = "/getMkdashboard")
    public Map getMkdashboard(@RequestParam("url") String url){
        String result = HttpClientUtil.sendGet2(url);
        System.out.println(result);
        return JSONObject.toJavaObject(JSON.parseObject(result),Map.class);
    };

    @Log("中转中台请求Post")
    @ApiOperation(value = "中转中台请求Post")
    @RequestMapping(value = "/getMkdashboardPost")
    public Map getMkdashboardPost(@RequestParam("url") String url, @RequestBody String param){
        JSONObject result = HttpRequestUtil.httpHeaderPost(url,JSONObject.parseObject(param),false);
        System.out.println(result);
        return JSONObject.toJavaObject(result,Map.class);
    };
}
