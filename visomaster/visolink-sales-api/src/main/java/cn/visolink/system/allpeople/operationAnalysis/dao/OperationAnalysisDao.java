package cn.visolink.system.allpeople.operationAnalysis.dao;

import cn.visolink.system.allpeople.operationAnalysis.model.*;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/4/13 20:22
 **/
public interface OperationAnalysisDao {

    /**
     * @param map
     * 获取分析数据
     * @return
     */
    List<OperationAnalysis> getOperationAnalysisList(Map map);

    /**
     * @param map
     * 获取分析数据
     * @return
     */
    List<OperationAnalysis> getAdminOperationAnalysisList(Map map);

    /**
     * 根据项目ID查询合并项目
     * @param projectId
     * @return
     */
    List<Map> getMergeProjectID(String projectId);

    /**
     * @param map
     * 获取推荐明细
     * @return
     */
    List<OperationAnalysisDetail> getOperationAnalysisDetailList(Map map);

    /**
     * @param map
     * 获取城市数据明细
     * @return
     */
    List<CityStatement> getCityStatement(Map map);

    /**
     * @param map
     * 获取活动数据明细
     * @return
     */
    List<ActivityStatement> getActivityStatement(Map map);

    /**
     * @param map
     * 获取项目数据明细
     * @return
     */
    List<ProjectStatement> getProjectStatement(Map map);

    /**
     * @Author wanggang
     * @Description //获取置业顾问
     * @Date 19:30 2020/7/9
     * @Param
     * @return
     **/
    List<Map> getSales();

    /**
     * @Author wanggang
     * @Description //获取置业顾问关注信息
     * @Date 19:30 2020/7/9
     * @Param
     * @return
     **/
    Map getGZdesc(Map map);
    /**
     * @Author wanggang
     * @Description //获取项目自动报备到访数量
     * @Date 19:30 2020/7/9
     * @Param
     * @return
     **/
    int getConsultingVisitCount(Map map);

    /**
     * @Author wanggang
     * @Description //获取项目金蝶ID
     * @Date 19:30 2020/7/9
     * @Param
     * @return
     **/
    String getJDproId(String proId);

    /**
     * @Author wanggang
     * @Description //获取活动期间新访复访数
     * @Date 19:30 2020/7/9
     * @Param
     * @return
     **/
    List<String> getVisitCount(Map map);

    /**
     * @Author wanggang
     * @Description //获取城市新闻维护频率
     * @Date 19:30 2020/7/9
     * @Param
     * @return
     **/
    List<String> getNewsAddTimes(Map cityMap);

    /**
     * @Author wanggang
     * @Description //获取城市活动维护频率
     * @Date 19:30 2020/7/9
     * @Param
     * @return
     **/
    List<String> getActivityAddTimes(Map cityMap);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 获取楼盘动态维护频率
    *@author FuYong
    *@date 2020/12/22 11:43
    */
    List<String> getDynamicAddTimes(Map map);
}
