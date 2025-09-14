package cn.visolink.system.wechattoken.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.wechattoken.service.WeChatAccessTokenService;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.WeChatAccessTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/7/3
 */
@Service
public class WeChatAccessTokenServiceImpl implements WeChatAccessTokenService {

    @Autowired
    private WeChatAccessTokenUtils weChatAccessTokenUtils;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResultBody getWeChatAccessToken() {
        String accessToken = weChatAccessTokenUtils.getToken();
        if (StringUtils.isBlank(accessToken)) {
            return ResultBody.error(40002,"获取微信token失败！");
        }else{
            return ResultBody.success(accessToken);
        }
    }

    @Override
    public ResultBody getRedisByKey(String key) {
        return ResultBody.success(redisUtil.get(key));
    }
}
