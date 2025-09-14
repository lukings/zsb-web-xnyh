package cn.visolink.system.allpeople.operationAnalysis.service;

import cn.visolink.system.allpeople.operationAnalysis.model.*;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/4/13 20:22
 **/
public interface OperationAnalysisService {

    /**
     * @param map
     * 获取分析数据
     * @return
     */
    PageInfo<OperationAnalysis> getOperationAnalysisList(Map map);
    /**
     * @param request
     * 导出分析数据
     * @return
     */
    void operationAnalysisExport(HttpServletRequest request, HttpServletResponse response, String paramMap);

    /**
     * @param paramMap
     * 获取推荐明细
     * @return
     */
    PageInfo<OperationAnalysisDetail> getOperationAnalysisDetailList(Map paramMap);

    /**
     * @param request
     * 导出推荐明细
     * @return
     */
    void operationAnalysisDetailExport(HttpServletRequest request, HttpServletResponse response, String paramMap);

    /**
     * @param paramMap
     * 获取城市运维明细
     * @return
     */
    PageInfo<CityStatement> getCityStatement(Map paramMap);

    /**
     * @param request
     * 导出城市运维明细
     * @return
     */
    void cityStatementExport(HttpServletRequest request, HttpServletResponse response, String param);

    /**
     * @param paramMap
     * 获取活动运维明细
     * @return
     */
    PageInfo<ActivityStatement> getActivityStatement(Map paramMap);

    /**
     * @param request
     * 导出活动运维明细
     * @return
     */
    void activityStatementExport(HttpServletRequest request, HttpServletResponse response, String param);

    /**
     * @param paramMap
     * 获取项目运维明细
     * @return
     */
    PageInfo<ProjectStatement> getProStatement(Map paramMap);

    /**
     * @param request
     * 导出项目运维明细
     * @return
     */
    void proStatementExport(HttpServletRequest request, HttpServletResponse response, String param);

    /**
     * @Author wanggang
     * @Description //异步导出运营分析表
     * @Date 15:20 2020/9/10
     * @Param [param]
     * @return java.lang.String
     **/
    String operationAnalysisExportNew(ExportVo param);
    /**
     * @Author wanggang
     * @Description //异步导出活动运营表
     * @Date 15:20 2020/9/10
     * @Param [param]
     * @return java.lang.String
     **/
    String activityStatementExportNew(ExportVo param);
    /**
     * @Author wanggang
     * @Description //异步导出项目内容运维明细表
     * @Date 15:20 2020/9/10
     * @Param [param]
     * @return java.lang.String
     **/
    String proStatementExportNew(ExportVo param);
}
