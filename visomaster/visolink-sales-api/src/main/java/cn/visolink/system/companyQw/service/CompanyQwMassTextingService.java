package cn.visolink.system.companyQw.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.companyQw.model.BQwMassTexting;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author: Mr.Yu
 * @Date: 2022/1/5 16:41
 * @description
 * @Version 1.0
 */
public interface CompanyQwMassTextingService {

    /**
     * 创建群发任务
     *
     * @param bQwMassTexting
     * @param request
     * @return
     */
    ResultBody createMassTextingTasks(BQwMassTexting bQwMassTexting, HttpServletRequest request);

    /**
     * 查看群发任务列表
     *
     * @param map
     * @return
     */
    ResultBody selectMassTextingTasks(Map map);

    /**
     * @Author wanggang
     * @Description //获取成员客户数量
     * @Date 16:52 2022/1/10
     * @Param [bQwMassTexting, request]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getUserCstCount(BQwMassTexting bQwMassTexting, HttpServletRequest request);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 16:54 2022/1/10
     * @Param [bQwMassTexting, request]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getUserCstTag(BQwMassTexting bQwMassTexting, HttpServletRequest request);
    /**
     * @Author wanggang
     * @Description //删除任务
     * @Date 17:52 2022/1/10
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody delMassTextingTasks(Map map);
    /**
     * @Author wanggang
     * @Description //撤回群发任务
     * @Date 17:54 2022/1/10
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody reMassTextingTasks(Map map);
    /**
     * @Author wanggang
     * @Description //重发任务
     * @Date 18:23 2022/1/10
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody reAddMassTextingTasks(Map map);
}
