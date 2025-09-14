package cn.visolink.system.visitandcallexcel.dao;

import cn.visolink.system.visitandcallexcel.model.ExcelModelVisitAndCall;

import java.util.List;
import java.util.Map;

/**
 * 客户来访，来电，去电台账导出
 *
 * @author ligengying
 * @date 20191119
 */
public interface VisitAndCallExcelDao {

    /**
     * 查询项目名称，区域名称
     *
     * @param projectId
     * @return
     */
    List<Map> getProjectNameList(String projectId);

    /**
     * 新访统计台账
     *
     * @param temMap
     * @return
     */
    List<ExcelModelVisitAndCall> getExcelNewVisitList(Map temMap);

    /**
     * 复访统计台账
     *
     * @param temMap
     * @return
     */
    List<ExcelModelVisitAndCall> getExcelOldVisitList(Map temMap);

    /**
     * 来访访统计台账
     *
     * @param temMap
     * @return
     */
    List<ExcelModelVisitAndCall> getExcelAllVisitList(Map temMap);

    /**
     * 来电统计台账
     *
     * @param temMap
     * @return
     */
    List<ExcelModelVisitAndCall> getExcelComeCallList(Map temMap);

    /**
     * 去电统计台账
     *
     * @param temMap
     * @return
     */
    List<ExcelModelVisitAndCall> getExcelGoCallList(Map temMap);

    /**
     * 查询新访台账
     *
     * @param map
     * @return
     */
    List<Map> getNewVisitList(Map map);

    /**
     * 查询复访台账
     *
     * @param map
     * @return
     */
    List<Map> getOldVisitList(Map map);

    /**
     * 查询来访台账
     *
     * @param map
     * @return
     */
    List<Map> getAllVisitList(Map map);

    /**
     * 查询来电台账
     *
     * @param map
     * @return
     */
    List<Map> getComeCallList(Map map);

    /**
     * 查询去电台账
     *
     * @param map
     * @return
     */
    List<Map> getGoCallList(Map map);

    /**
     * 查询新访总数
     *
     * @param map
     * @return
     */
    int getNewVisitCount(Map map);

    /**
     * 查询复访总数
     *
     * @param map
     * @return
     */
    int getOldVisitCount(Map map);

    /**
     * 查询来访总数
     *
     * @param map
     * @return
     */
    int getAllVisitCount(Map map);

    /**
     * 查询来电总数
     *
     * @param map
     * @return
     */
    int getComeCallCount(Map map);

    /**
     * 查询去电总数
     *
     * @param map
     * @return
     */
    int getGoCallCount(Map map);

    /**
     * 查询判客结果
     * @param map
     * @return
     */
    List<ExcelModelVisitAndCall> getReceptionCustomerInfoList(Map map);

    /**
     * 查询判客台账总数
     * @param map
     * @return
     */
    int getVisitCount(Map map);
}
