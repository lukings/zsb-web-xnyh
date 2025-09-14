package cn.visolink.system.channel.service;


import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface RedistributionService {

    /**
     * 查询渠道的重分配信息
     * */
    PageInfo ProjectClueDeriveMustAcByToker(Map map);

    /**
     * 查询渠道的重分配信息
     * */
    PageInfo queryRedistributionRecord(Map map);

    /**
     * 重分配接口
     */
    Map redistribution(Map map);

    /**
     * 重分配筛选人员接口
     * */
    List<Map> selectMan(Map map);

    /**
     *查询单个人的详细信息
     * */
    Map selectDetailedInformation(Map map);

    /**
     * 修改线索和机会表的报备人信息
     * */
    Map updateDetaileReport(Map map);

    /**
     * 查询修改日志
     * @param projectClueId
     * @return
     */
    List<Map> getModification(String projectClueId);

    /**
     * 查询修改详情
     * @param updateLogId
     * @return
     */
    List<Map> getModificationDetails(String updateLogId);

    void cluesRedistributionRecordExport(HttpServletRequest request, HttpServletResponse response, Map map);

    String cluesRedistributionRecordExportNew(Map map);

    void oppRedistributionRecordExport(HttpServletRequest request, HttpServletResponse response, Map map);

    String oppRedistributionRecordExportNew(Map map);
}
