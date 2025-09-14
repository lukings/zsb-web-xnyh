package cn.visolink.utils;

import cn.hutool.db.DaoTemplate;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.system.wechattoken.mapper.WeChatMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/7/3
 */
@Component
public class WeChatAccessTokenUtils {

    @Value("${appid}")
    private String appid;

    @Value("${appKey}")
    private String appKey;

    @Value("${WeChatType}")
    private String weChatType;

    @Value("${WeChatTypeUrl}")
    private String weChatTypeUrl;

    private static String WE_CHAT_ACCESS_TOKEN = "WE_CHAT_ACCESS_TOKEN";

    private static int WE_CHAT_REDIS = 300;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WeChatMapper weChatMapper;

    /***
    *
     * @param
    *@return {}
    *@throws
    *@Description: 获取token
    *@author FuYong
    *@date 2020/7/3 16:48
    */
    public String getToken(){
        Map logMap = new HashMap();
        logMap.put("StartTime",new Date());
        logMap.put("TaskName","生成微信token");
        String accessToken = null;
        String details = "";
        if("prod".equals(weChatType)){
            details = details + "1. 生产 进入";
            if (redisUtil.hasKey(WE_CHAT_ACCESS_TOKEN)) {
                details = details + "2. redis有值";
                accessToken = String.valueOf(redisUtil.get(WE_CHAT_ACCESS_TOKEN));
//                JSONObject jsonObject = JSONObject.parseObject(
//                        HttpRequestUtil.httpGet("https://api.weixin.qq.com/wxaapi/log/get_client_version?access_token=" + accessToken,false));
//                details = details + "2.2.accessToken:" + accessToken;
//                details = details + "2.2.accessToken" + accessToken;
//                details = details + "3.测试接口接口返回" + jsonObject.toJSONString();
//                if(jsonObject != null){
//                    details = details + "4. 测试接口返回有值："+ jsonObject.toJSONString();
//                    if(!"0".equals(jsonObject.getString("errcode"))){
//                        details = details + "5. 测试接口返回失败"+ jsonObject.toJSONString();
//                        accessToken = this.getWeChatAccessToken();
//                    }
//                }
            }else{
                details = details + "6. redis中无值";
                accessToken = this.getWeChatAccessToken();
            }
        }else{
            details = details + "7. 测试 进入";
            JSONObject jsonObject = JSONObject.parseObject(
                    HttpClientUtil.sendGet(weChatTypeUrl,new HashMap<>()));
            if("200".equals(jsonObject.getString("code"))){
                accessToken = jsonObject.getString("data");
            }
        }
        logMap.put("Note",details);
        logMap.put("ResultStatus","0");
        logMap.put("ExecutTime",new Date());
        weChatMapper.insertLogs(logMap);
        return accessToken;
    }

    /***
     *
     * @param
     *@return {}
     *@throws
     *@Description: 生成微信小程序token
     *@author FuYong
     *@date 2020/7/2 17:01
     */
    public String getWeChatAccessToken(){
        Map logMap = new HashMap();
        logMap.put("StartTime",new Date());
        logMap.put("TaskName","生成微信token");
        try {
            System.out.println("生成微信小程序token开始");
            Map<String, String> map = new LinkedHashMap<>();
            map.put("grant_type", "client_credential");
            map.put("appid", appid);
            map.put("secret", appKey);

            String rt = UrlUtil.sendPost("https://api.weixin.qq.com/cgi-bin/token", map);
            System.out.println("what is:" + rt);
            JSONObject json = JSON.parseObject(rt);
            if (json.getString("access_token") != null || json.getString("access_token") != "") {
                String note = "生成微信小程序token-------成功";
                System.out.println("生成微信小程序token-------成功:" + json.getString("access_token"));
                redisUtil.del(WE_CHAT_ACCESS_TOKEN);
                note = note + ";删除redis-------成功";
                redisUtil.set(WE_CHAT_ACCESS_TOKEN,json.getString("access_token"),WE_CHAT_REDIS);
                note = note + ";保存redis-------成功";
                System.out.println("返回--------生成微信小程序token");
                logMap.put("Note",note + "返回token" + json.getString("access_token"));
                logMap.put("ResultStatus",json.get("errcode"));
                return json.getString("access_token");
            } else {
                logMap.put("Note","生成微信小程序token-------失败:" + json.getString("errmsg"));
                logMap.put("ResultStatus",json.get("errcode"));
                System.out.println("生成微信小程序token-------失败");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            logMap.put("ExecutTime",new Date());
            weChatMapper.insertLogs(logMap);
        }
    }

}
