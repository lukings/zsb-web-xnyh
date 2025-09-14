package cn.visolink.system.projectmanager.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.projectmanager.model.*;
import cn.visolink.system.projectmanager.model.Vo.ProCollAccountVo;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/11/24 13:48
 **/
public interface HousingManagementService {

    /**
     * @Author wanggang
     * @Description //获取交易客户数据
     * @Date 14:47 2021/11/24
     * @Param [tradeCstForm]
     * @return java.util.List<cn.visolink.system.projectmanager.model.TradeCstData>
     **/
    PageInfo<TradeCstData> getTradeCstList(TradeCstForm tradeCstForm);
    /**
     * @Author wanggang
     * @Description //交易信息导出
     * @Date 16:57 2021/11/24
     * @Param [request, response, param]
     * @return void
     **/
    void tradeCstExport(HttpServletRequest request, HttpServletResponse response, String param);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 9:43 2021/11/30
     * @Param [request, param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getRoomList(HttpServletRequest request, Map param);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 9:43 2021/11/30
     * @Param [request, roomGUID]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getRoomById(HttpServletRequest request, String roomGUID);
    /**
     * @Author wanggang
     * @Description //获取楼栋
     * @Date 10:49 2021/12/1
     * @Param [request, projectId]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getBldByProId(HttpServletRequest request, String projectId);
    /**
     * @Author wanggang
     * @Description //收款账户保存
     * @Date 14:53 2021/12/6
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addOrEditColl(ProCollAccountVo param);
    /**
     * @Author wanggang
     * @Description //获取收款账户列表
     * @Date 14:54 2021/12/6
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getXcollection(Map param);
    /**
     * @Author wanggang
     * @Description //获取修改日志
     * @Date 15:36 2021/12/6
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProCollEditLog(Map param);
    /**
     * @Author wanggang
     * @Description //收款账号导出
     * @Date 17:39 2021/12/6
     * @Param [request, response, param]
     * @return void
     **/
    void xcollectionExport(HttpServletRequest request, HttpServletResponse response, Map param);
    /**
     * @Author wanggang
     * @Description //获取退款台账
     * @Date 9:55 2021/12/7
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getRefundApproval(Map param);
    /**
     * @Author wanggang
     * @Description //退款台账导出
     * @Date 9:58 2021/12/7
     * @Param [request, response, param]
     * @return void
     **/
    void refundApprovalExport(HttpServletRequest request, HttpServletResponse response, Map param);
    /**
     * @Author wanggang
     * @Description //获取回款信息
     * @Date 10:03 2021/12/7
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getReturnedMoney(Map param);
    /**
     * @Author wanggang
     * @Description //导出回款信息
     * @Date 10:03 2021/12/7
     * @Param [request, response, param]
     * @return void
     **/
    void returnedMoneyExport(HttpServletRequest request, HttpServletResponse response, Map param);
    /**
     * @Author wanggang
     * @Description //退款审批
     * @Date 10:56 2021/12/7
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody refundApproval(Map param);
    /**
     * @Author wanggang
     * @Description //确认收款
     * @Date 10:56 2021/12/7
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody returnedMoney(Map param);

    /**
     * 签约协议模板列表查询
     *
     * @param orderTemplate
     * @return
     */
    ResultBody selectSignProtocolTemplates(OrderTemplate orderTemplate);

    /**
     * 签约协议模板列表启用或者禁用
     *
     * @param id
     * @param statusFlag 0禁用 1启用
     * @return
     */
    ResultBody updateSignProtocolTemplatesStatus(Integer id, Integer statusFlag);

    /**
     * 保存签约模板
     *
     * @param orderTemplate
     * @param request
     * @return
     */
    ResultBody saveOrderTemplate(OrderTemplate orderTemplate, HttpServletRequest request);

    /**
     * 查询签约模板详情通过id
     *
     * @param id
     * @return
     */
    ResultBody getOrderTemplate(Integer id);

    /**
     * 更新签约模板通过id
     *
     * @param orderTemplate
     * @param request
     * @return
     */
    ResultBody updateOrderTemplate(OrderTemplate orderTemplate, HttpServletRequest request);

    /**
     * 集团签署认购协议文件列表
     *
     * @return
     */
    ResultBody selectGroupOrderTemplate();
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:45 2021/12/20
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getXcode(Map param);
    /**
     * @Author wanggang
     * @Description //获取成交信息
     * @Date 17:30 2022/1/24
     * @Param [dealCstForm]
     * @return com.github.pagehelper.PageInfo<cn.visolink.system.projectmanager.model.DealCstData>
     **/
    PageInfo<DealCstData> getDealCstData(DealCstForm dealCstForm);
    /**
     * @Author wanggang
     * @Description //成交信息导出
     * @Date 17:31 2022/1/24
     * @Param [request, response, dealCstForm]
     * @return void
     **/
    void dealCstExport(HttpServletRequest request, HttpServletResponse response, String param);
    /**
     * @Author wanggang
     * @Description //成交关联客户导出
     * @Date 10:30 2022/1/25
     * @Param [request, response, param]
     * @return void
     **/
    void dealRelationCstExport(HttpServletRequest request, HttpServletResponse response, String param);
}
