package cn.visolink.system.excel.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.excel.model.DistVO;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.excel.model.ReportCustomerForm;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.26
 */
public interface ExcelImportService {

  Map batchImport(String fileName, MultipartFile file) throws Exception;

  List<Map> getAllImportData();

  Map importBizTable() throws ParseException;

  List<ExcelExportLog> getExcelExportDownList();

  PageInfo<ExcelExportLog> getExcelExportList(ExcelExportLog excelExportLog);

  void updateExcelExport(HttpServletRequest request, HttpServletResponse response, String param);

  List<DistVO> getExcelExportDist();

  ResultBody reLoadExcel(ExcelExportLog excelExportLog);

  int getExcelExportDownIsExist(ExcelExportLog excelExportLog);

  ResultBody getButchtMark(ReportCustomerForm reportCustomerForm);

  ResultBody saveButchtMark();
  
  /**
   * 查询地图导入客户历史记录
   */
  ResultBody queryMapImportCustomerHistory(Map<String, Object> paramMap);
  
  /**
   * 根据导入历史记录删除导入数据
   */
      ResultBody deleteMapImportData(Map<String, Object> paramMap);

    /**
     * 根据线索ID查询联系方式
     * @param projectClueId 线索ID
     * @return 联系方式列表
     */
    List<Map<String, Object>> queryCluesContacts(String projectClueId);
}
