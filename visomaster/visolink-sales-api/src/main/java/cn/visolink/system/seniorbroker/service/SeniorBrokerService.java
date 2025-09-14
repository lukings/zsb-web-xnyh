package cn.visolink.system.seniorbroker.service;


import cn.visolink.exception.ResultBody;
import cn.visolink.system.channel.model.form.PublicpoolForm;
import cn.visolink.system.seniorbroker.vo.AccountActiveExcel;
import cn.visolink.system.seniorbroker.vo.BrokerAccountForm;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author: BaoQiangLer
 * @ProjectName: visolink
 * @Description:  地图选房业务接口
 * @Date: Created in 2020/10/12
 */
public interface SeniorBrokerService {


    /**
     * 获取大客户活动数据
     *
     * @param param 筛选条件
     * @return result
     * */
    PageInfo<AccountActiveExcel> getAccountActiveList(Map<String, Object> param);

    /**
     * 获取大客户活动数据
     *
     * @param param 筛选条件
     * @return result
     * */
    Map<String,Object> getAccountActiveById(Map<String, Object> param);

    /**
     * 导出大客户活动数据
     *
     * @param request request
     * @param response response
     * @param param 筛选条件
     * */
    void getAccountActiveExport(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param);

    /**
     * 活动下的二级经纪人导出
     *
     * @param request request
     * @param response response
     * @param param 筛选条件
     * */
    void getBrokerByActiveIdExcel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param);

    /**
     * 获取活动下的二级经纪人
     *
     * @param param 筛选条件
     * @return result
     * */
    PageInfo<Map<String,Object>> getBrokerByActiveId(Map<String, Object> param);

    /**
     * 获取活动下的客户
     *
     * @param param 筛选条件
     * @return result
     * */
    PageInfo<Map<String,Object>> getCustomerByActiveId(Map<String, Object> param);

    /**
     * 获取活动下的客户导出
     *
     * @param request request
     * @param response response
     * @param param 大客户经理人账号
     * */
    void getCustomerByActiveIdExcel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param);

    /**
     * 获取二级经纪人数据
     *
     * @param param 筛选条件
     * @return result
     * */
    PageInfo<Map<String,Object>> getSeniorBroker(Map<String, Object> param);

    /**
     * 二级经纪人数据导出
     *
     * @param request request
     * @param response response
     * @param param 大客户经理人账号
     * */
    void getSeniorBrokerExcel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param);


    /**
     * 获取项目的大客户
     *
     * @param param 筛选条件
     * @return result
     * */
    ResultBody getAccountProject(Map<String, Object> param);


    /**
     * 获取二级经纪人活动数据
     *
     * @param param 筛选条件
     * @return result
     * */
    PageInfo<Map<String,Object>> getSeniorBrokerActive(Map<String, Object> param);

    /**
     * 获取二级经纪人分配历史
     *
     * @param param 筛选条件
     * @return result
     * */
    PageInfo<Map<String,Object>> getBrokerAccountRecords(Map<String, Object> param);

    /**
     * 获取二级经纪人分配历史
     *
     * @return result
     * */
    List<Map<String,Object>> getDkhCfpType();

    /**
     * 获取二级经纪人活动数据
     *
     * @param brokerAccountForm 筛选条件
     * @return ResultBody
     * */
    ResultBody redistributionAccountManager(BrokerAccountForm brokerAccountForm);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 16:12 2020/12/17
     * @Param [param]
     * @return java.lang.String
     **/
    String getSeniorBrokerExcelNew(Map param);
}
