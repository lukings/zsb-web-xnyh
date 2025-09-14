package cn.visolink.system.wechattoken.service;

import cn.visolink.exception.ResultBody;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/7/3
 */
public interface WeChatAccessTokenService {

    ResultBody getWeChatAccessToken();

    ResultBody getRedisByKey(String key);
}
