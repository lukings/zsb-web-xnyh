package cn.visolink.system.openQuotation.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.openQuotation.model.OpenActivity;
import cn.visolink.system.openQuotation.model.OpenBuild;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/12/31 13:52
 **/
public interface OpenQuotationService {

    ResultBody getProBook(Map map);

    ResultBody getProBank(Map map);

    ResultBody getProDiscount(Map map);

    OpenBuild getBldingRoomList(String bldId, String activityId, String isResult);

    ResultBody addOpenActivity(OpenActivity param);

    ResultBody updateOpenActivity(OpenActivity param);

    void openActivityExport(HttpServletRequest request, HttpServletResponse response, Map map);

    void openActivityResultExport(HttpServletRequest request, HttpServletResponse response, Map map);

    int queryBuildDelOk(Map map);

    OpenActivity getOpenActivityDetail(Map map);

    ResultBody getOpenActivitys(Map map);

    ResultBody releaseActivity(Map map);

    ResultBody disableActivity(Map map);

    ResultBody delActivity(Map map);

    ResultBody getOpenActivityBuild(Map map);

    ResultBody getOpenActivityPhoto(Map map);

    String queryOpenActivityStatus(Map map);

    String openActivityExportNew(Map map);

    ResultBody getOpenActivityResult(Map map);

    ResultBody getOpenOrderList(Map map);

    void openOrderListExport(HttpServletRequest request, HttpServletResponse response, Map map);

    ResultBody getOrderDetail(Map map);

    String getRoomOrder(Map map);

    ResultBody queryOpenActivityBanks(Map map);

    String queryBuildAccount(Map map);

    int queryActivityIsOkPublish(Map map);

    ResultBody getBldingRoomsList(Map map);

    int queryRoomDelOk(Map map);

    ResultBody getProjectRule(String projectId);

    ResultBody saveProjectRule(Map map);

    ResultBody getIsCollBank(String projectId);

    ResultBody editOrder(Map map);

    /**
     * 线下支付凭证审核列表
     *
     * @param map
     * @return
     */
    ResultBody offlinePayCheckList(Map map);

    /**
     * 审批凭证
     *
     * @param map
     * @return
     */
    ResultBody checkCertificate(Map map);

    /**
     * 查看驳回记录通过主键id
     *
     * @param map
     * @return
     */
    ResultBody getRecordById(Map map);

    /**
     * 导出审核列表数据通过项目id
     *
     * @param map
     * @param request
     * @param response
     * @return
     */
    void exportCheckListData(Map map, HttpServletRequest request, HttpServletResponse response);


}
