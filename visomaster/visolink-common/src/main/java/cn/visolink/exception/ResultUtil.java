package cn.visolink.exception;

import cn.visolink.exception.conifg.PropertiesListenerConfig;

import java.util.Map;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/8/23 9:19 下午
 */
public class ResultUtil {

    /**
     * 成功返回
     * @param object
     * @return
     */
    public static ResultBody success(Object object) {
        ResultBody result = new ResultBody();
        result.setCode(200);
        result.setMessages("ok");
        result.setData(object);
        return result;
    }

    /**
     * 异常返回
     * @param code
     * @param msg
     * @return
     */
    public static ResultBody error(long code, String msg) {

        ResultBody result = new ResultBody();
        result.setCode(code);
        result.setMessages(msg);
        return result;
    }

}
