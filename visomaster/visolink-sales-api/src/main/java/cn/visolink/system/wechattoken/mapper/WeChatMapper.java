package cn.visolink.system.wechattoken.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/7/3
 */
@Mapper
public interface WeChatMapper {

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 保存日志
    *@author FuYong
    *@date 2020/7/3 18:18
    */
    void insertLogs(Map map);
}
