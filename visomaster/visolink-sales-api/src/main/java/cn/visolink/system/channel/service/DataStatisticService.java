package cn.visolink.system.channel.service;

import cn.visolink.exception.ResultBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/19 13:53
 **/
public interface DataStatisticService {
    /**
     * @Author wanggang
     * @Description //获取项目统计
     * @Date 14:15 2022/4/19
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody projectDataStatistics(Map paramMap);

    /**
     * @Author wanggang
     * @Description //打卡统计
     * @Date 2025/1/27
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody punchInStatistics(Map paramMap);

    /**
     * @Author wanggang
     * @Description //打卡统计项目明细
     * @Date 2025/1/27
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody punchInProjectDetail(Map paramMap);

    /**
     * @Author wanggang
     * @Description //打卡统计客户明细
     * @Date 2025/1/27
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody punchInCustomerDetail(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取人员统计
     * @Date 14:15 2022/4/19
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody userDataStatistics(Map paramMap);


    ResultBody getTeamListByProId(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取区域
     * @Date 16:19 2022/4/19
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getAllRegion();
    /**
     * @Author wanggang
     * @Description //获取区域统计
     * @Date 16:19 2022/4/19
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody regionDataStatistics(Map paramMap);
    /**
     * @Author wanggang
     * @Description //项目数据导出
     * @Date 9:40 2022/4/20
     * @Param [request, response, excelForm]
     * @return void
     **/
    void projectDataExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    String projectDataExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm);
    /**
     * @Author wanggang
     * @Description //个人数据导出
     * @Date 9:40 2022/4/20
     * @Param [request, response, excelForm]
     * @return void
     **/
    void userDataExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    String userDataExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm);
    /**
     * @Author wanggang
     * @Description //区域数据导出
     * @Date 9:40 2022/4/20
     * @Param [request, response, excelForm]
     * @return void
     **/
    void regionDataExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    String regionDataExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm);
    /**
     * @Author wanggang
     * @Description //获取项目成交统计
     * @Date 14:15 2022/4/19
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody dealStatistics(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取区域成交统计
     * @Date 11:14 2022/4/20
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody regionDealStatistics(Map paramMap);
    /**
     * @Author wanggang
     * @Description //项目成交数据导出
     * @Date 9:40 2022/4/20
     * @Param [request, response, excelForm]
     * @return void
     **/
    void dealStatisticsExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    /**
     * @Author wanggang
     * @Description //区域成交数据导出
     * @Date 9:40 2022/4/20
     * @Param [request, response, excelForm]
     * @return void
     **/
    void regionDealExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    /**
     * @Author wanggang
     * @Description //跟进数据统计
     * @Date 14:03 2022/4/20
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody followUpStatistics(Map paramMap);
    /**
     * @Author wanggang
     * @Description //跟进数据导出
     * @Date 21:17 2022/4/20
     * @Param [request, response, excelForm]
     * @return void
     **/
    void followUpExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    /**
     * @Author wanggang
     * @Description //项目转化率统计
     * @Date 16:08 2022/9/1
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody proConversionRateStatistics(Map paramMap);
    /**
     * @Author luqianqian
     * @Description //项目转化率统计新
     * @Date 16:08 2024/11/10
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody proConversionRateStatisticsNew(Map paramMap);

    ResultBody proConversionRateStatisticsNewPL(Map paramMap);
    /**
     * @Author luqianqian
     * @Description //项目转化率导出新
     * @Date 16:08 2024/11/10
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    void proConversionRateStatisticsExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm);

    void proConversionRateStatisticsExportNewPL(HttpServletRequest request, HttpServletResponse response, String excelForm);
    /**
     * @Author wanggang
     * @Description //区域转化率统计
     * @Date 16:08 2022/9/1
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody regionConversionRateStatistics(Map paramMap);
    /**
     * @Author wanggang
     * @Description //项目转化率导出
     * @Date 16:41 2022/9/1
     * @Param [request, response, excelForm]
     * @return void
     **/
    void proConversionRateExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    /**
     * @Author wanggang
     * @Description //区域转化率导出
     * @Date 16:42 2022/9/1
     * @Param [request, response, excelForm]
     * @return void
     **/
    void regionConversionRateExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    /**
     * @Author wanggang
     * @Description //获取区域
     * @Date 14:01 2022/9/15
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getAllRegionNew();

    /**
     * @Author luqianqian
     * @Description //客户来源数据统计
     * @Date 15:50 2024/4/19
     * @Param [request, response, excelForm]
     * @return void
     **/
    ResultBody sourceModeDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //客户来源数据统计导出
     * @Date 15:50 2024/4/19
     * @Param [request, response, excelForm]
     * @return void
     **/
    void sourceModeDataStatisticsExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    String sourceModeDataStatisticsExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm);

    /**
     * @Author luqianqian
     * @Description //获取集团统计
     * @Date 14:15 2024/11/22
     * @Param [paramMap]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody groupDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //集团数据导出
     * @Date 9:40 2024/11/22
     * @Param [request, response, excelForm]
     * @return void
     **/
    void groupDataExport(HttpServletRequest request, HttpServletResponse response, String excelForm);

    /**
     * @Author luqianqian
     * @Description //客户成交周期数据统计
     * @Date 22:30 2024/11/21
     * @Param [request, response, excelForm]
     * @return void
     **/
    ResultBody customerTradeCycleDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //客户成交周期数据统计详情
     * @Date 22:30 2024/11/21
     * @Param [request, response, excelForm]
     * @return void
     **/
    ResultBody customerTradeCycleDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //客户成交周期数据统计详情导出
     * @Date 22:30 2024/11/21
     * @Param [request, response, excelForm]
     * @return void
     **/
    void customerTradeCycleDataStatisticsGatherExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
}
