package cn.visolink.system.visitandcallexcel.service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 客户来访，来电，去电台账导出
 *
 * @author ligengying
 * @date 20191119
 */
public interface VisitAndCallExcelService {

    /**
     * 导出来访，来电，去电台账
     *
     * @param request
     * @param response
     * @param params
     */
    void getDownloadVisitAndCallInfo(HttpServletRequest request, HttpServletResponse response, String params);

    /**
     * 查询来访，来单，去电台账
     *
     * @param map
     * @return
     */
    Map getVisitAndCallInfo(Map map);

    /**
     * 查询接待大使判客台账
     *
     * @param map
     * @return
     */
    Map getReceptionCustomerInfo(Map map);

    /**
     * 导出接待大使判客台账
     *
     * @param request
     * @param response
     * @param param
     */
    void getDownloadReceptionCustomerExcel(HttpServletRequest request, HttpServletResponse response, String param);
}
