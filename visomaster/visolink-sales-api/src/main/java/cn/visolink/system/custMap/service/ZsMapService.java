package cn.visolink.system.custMap.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.custMap.bo.*;
import cn.visolink.system.project.model.vo.ProjectVO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ZsMapService {

  PageInfo<ZsMapPermissionsResBO> getZsMapPermissions(ZsMapPermissionsQueryBO zsMapPermissionsQueryBO);

  void saveZsMapPermissions(List<ZsMapPermissionsVO> zsMapPermissionsVO);

  void exportZsMapPermissions(HttpServletResponse response, ZsMapPermissionsQueryBO zsMapPermissionsQueryBO);

  ResultBody deleteZsMapPermissions(ZsMapPermissionsVO zsMapPermissionsVO);

  ZsMapPermissionsVO getZsMapPermissionsDetail(ZsMapPermissionsQueryBO zsMapPermissionsQueryBO);

  List<ZsMapResBO> zsMapDistict(String customerName, String customerMobile, String lineDistance, List<ZsMapResBO> zsMapResBOList);

  void locatiomError(String businessId);

  void zsMapDraw(ZsMapDrawBO zsMapDrawBO);
  ResultBody saveZsMapDraw(ZsMapDrawBO zsMapDrawBO);

  List<ZsMapDrawResBO> getZsMapDraw(ZsMapDrawQueryBO zsMapDrawQueryBO);

  ResultBody deleteZsMapDraw(ZsMapDrawQueryBO zsMapDrawQueryBO);

  ResultBody getCustomerIsRepeat(Map map);

  PageInfo<ZsMapPermissionsRecordBO> getZsMapPermissionsRecord(
      PermissionsRecordQueryBO permissionsRecordQueryBO);

  ResultBody makeFolder(ZsMapDrawFolderBO zsMapDrawFolderBO);

  ResultBody getFolderList(ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO);

  ResultBody updateFolder(ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO);

  List<ProjectVO> getProjectNames(List<String> projPermissionsList);

  ResultBody getCustomerList(List<BatchQueryCustomerBO> batchQueryCustomerBOList);

  ResultBody updateZsmapDraw(ZsMapDrawBO zsMapDrawBO);

  GridDistributionResBO getGridDistribution(GridDistributionQueryBO gridDistributionQueryBO);

  void exportGridDistributionWord(HttpServletResponse response, GridDistributionQueryBO gridDistributionQueryBO);
}
