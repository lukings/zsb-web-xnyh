package cn.visolink.system.projectmanager.dao;

import cn.visolink.system.openQuotation.model.OppTradeVo;
import cn.visolink.system.openQuotation.model.OrderNodeRecord;
import cn.visolink.system.projectmanager.model.*;
import cn.visolink.system.projectmanager.model.Vo.FeeVo;
import cn.visolink.system.projectmanager.model.Vo.ProCollAccountVo;
import cn.visolink.system.projectmanager.model.Vo.RefundApprovalVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/11/24 14:57
 **/
@Mapper
@Repository
public interface HousingManagementDao {
    /**
     * @Author wanggang
     * @Description //查询交易客户
     * @Date 14:58 2021/11/24
     * @Param [tradeCstForm]
     * @return java.util.List<cn.visolink.system.projectmanager.model.TradeCstData>
     **/
    List<TradeCstData> getTradeCstList(TradeCstForm tradeCstForm);
    /**
     * @Author wanggang
     * @Description //获取置业顾问名称
     * @Date 10:22 2021/11/30
     * @Param [UserCode]
     * @return java.lang.String
     **/
    String getUserNameByUserCode(String UserCode);

    /**
     * @Author wanggang
     * @Description //获取数据
     * @Date 14:16 2021/7/15
     * @Param sqlValue
     * @return java.lang.String
     **/
    List<Map<String, Object>> getDataList(@Param("sqlValue") String sqlValue);

    /**
     * 获取销控的房间ID
     * @param BldGUID
     * @param
     * @return
     */
    List<String> getHousingSalesRoom(String BldGUID);
    /**
     * @Author wanggang
     * @Description //保存项目收款账号
     * @Date 15:20 2021/12/6
     * @Param [param]
     * @return void
     **/
    void addProColl(ProCollAccountVo param);
    /**
     * @Author wanggang
     * @Description //更新项目收款账号
     * @Date 15:21 2021/12/6
     * @Param [param]
     * @return void
     **/
    void updateProColl(ProCollAccountVo param);
    /**
     * @Author wanggang
     * @Description //获取项目收款账号
     * @Date 15:21 2021/12/6
     * @Param [id]
     * @return cn.visolink.system.projectmanager.model.Vo.ProCollAccountVo
     **/
    ProCollAccountVo getProColl(String id);
    /**
     * @Author wanggang
     * @Description //保存修改记录
     * @Date 15:22 2021/12/6
     * @Param [param]
     * @return void
     **/
    void addProCollEditLog(ProCollAccountVo param);
    /**
     * @Author wanggang
     * @Description //获取修改日志
     * @Date 16:27 2021/12/6
     * @Param [id]
     * @return java.util.List<cn.visolink.system.projectmanager.model.Vo.ProCollAccountVo>
     **/
    List<ProCollAccountVo> getProCollEdit(String id);
    /**
     * @Author wanggang
     * @Description //获取收款账号信息
     * @Date 17:17 2021/12/6
     * @Param [param]
     * @return java.util.List<cn.visolink.system.projectmanager.model.Vo.ProCollAccountVo>
     **/
    List<ProCollAccountVo> getXcollection(Map param);
    /**
     * @Author wanggang
     * @Description //获取退款信息
     * @Date 10:08 2021/12/7
     * @Param [param]
     * @return java.util.List<cn.visolink.system.projectmanager.model.Vo.RefundApprovalVo>
     **/
    List<RefundApprovalVo> getRefundApproval(Map param);
    /**
     * @Author wanggang
     * @Description //获取回款信息
     * @Date 10:09 2021/12/7
     * @Param [param]
     * @return java.util.List<cn.visolink.system.projectmanager.model.Vo.FeeVo>
     **/
    List<FeeVo> getReturnedMoney(Map param);
    /**
     * @Author wanggang
     * @Description //更新退款审批记录
     * @Date 11:20 2021/12/7
     * @Param [param]
     * @return void
     **/
    void updateRefundApproval(Map param);

    /**
     * 签约协议模板列表查询
     *
     * @param orderTemplate
     * @return
     */
    List<OrderTemplate> selectSignProtocolTemplates(OrderTemplate orderTemplate);

    /**
     * 签约协议模板列表启用或者禁用
     *
     * @param id
     * @param status
     * @return
     */
    Integer updateSignProtocolTemplatesStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 保存签约模板
     *
     * @param orderTemplate
     * @return
     */
    Integer saveOrderTemplate(OrderTemplate orderTemplate);

    /**
     * 查询签约模板详情通过id
     *
     * @param id
     * @return
     */
    OrderTemplate getOrderTemplate(Integer id);

    /**
     * 更新签约模板通过id
     *
     * @param orderTemplate
     * @return
     */
    Integer updateOrderTemplate(OrderTemplate orderTemplate);

    /**
     * 集团签署认购协议文件列表
     *
     * @return
     */
    List<OrderTemplate> selectGroupOrderTemplate();
    /**
     * @Author wanggang
     * @Description //保存订单节点
     * @Date 15:37 2021/12/7
     * @Param [node]
     * @return void
     **/
    void addOrderNode(Map node);
    /**
     * @Author wanggang
     * @Description //更新订单状态
     * @Date 15:40 2021/12/7
     * @Param [orderNo]
     * @return void
     **/
    void updateXorder(String orderNo);
    /**
     * @Author wanggang
     * @Description //更新机会状态
     * @Date 15:44 2021/12/7
     * @Param [projectClueId]
     * @return void
     **/
    void updateOppStatus(String projectClueId);
    /**
     * @Author wanggang
     * @Description //更新回款信息
     * @Date 16:02 2021/12/7
     * @Param [id]
     * @return void
     **/
    void updateReturnedMoney(String id);
    /**
     * @Author wanggang
     * @Description //获取交易信息
     * @Date 16:03 2021/12/7
     * @Param [id]
     * @return java.util.Map
     **/
    Map getTradeMapByRemId(String id);
    /**
     * @Author wanggang
     * @Description //更新房间状态
     * @Date 16:44 2021/12/7
     * @Param [roomID, s]
     * @return void
     **/
    void updateRoomStatus(@Param("roomId") String roomID, @Param("statusEnum") String s, @Param("isTradeLock") Integer isTradeLock);
    /**
     * @Author wanggang
     * @Description //删除销控房间
     * @Date 16:44 2021/12/7
     * @Param [roomID]
     * @return void
     **/
    void updateHousingSalesControl(String roomId);
    /**
     * @Author wanggang
     * @Description //保存消息
     * @Date 16:52 2021/12/7
     * @Param [messageList]
     * @return void
     **/
    void insertMessage(@Param("list") List<Map> messageList);

    /**
     * 查询认购，签约消息
     * @param
     */
    List<Map> selectMessageALL(Map map);
    /**
     * 根据项目id查询销售经理
     * @param projectId
     * @return
     */
    List<Map> findXSJL(@Param("projectId") String projectId);
    /**
     * @Author wanggang
     * @Description //保存节点记录
     * @Date 16:53 2021/12/7
     * @Param [nodeMap]
     * @return void
     **/
    void savaFollowupRecord(Map nodeMap);

    List<Map> getAllProjectList();

    /**
     * 根据退款审批表的id查询交易id，再根据交易id查询订单信息
     *
     * @param id
     * @return
     */
    Map getOrderInfoById(String id);

    /**
     * 根据项目id查询银行信息
     *
     * @param projectId
     * @return
     */
    Map getBankInfoByProjectId(String projectId);

    /**
     * 更新订单
     *
     * @param map
     * @return
     */
    int updateOrder(Map map);

    /**
     * 更新交易
     *
     * @param dataMap
     */
    void updateOppTradeVo(Map dataMap);

    /**
     * 更新银行卡信息 b_returned_money
     *
     * @param map
     * @return
     */
    Integer updateBankInfo(Map map);

    /**
     * @Author wanggang
     * @Description //获取交易表数据
     * @Date 15:24 2021/11/20
     * @Param [tradeGUID]
     * @return cn.visolink.business.housingSales.model.vo.OppTradeVo
     **/
    OppTradeVo getOppTradeVo(String tradeGUID);

    /**
     * @Author wanggang
     * @Description //查询机会信息
     * @Date 16:51 2021/11/20
     * @Param [opportunityClueId]
     * @return java.util.Map
     **/
    Map getOppData(String opportunityClueId);

    /**
     * @Author wanggang
     * @Description //获取是否存在其他交易
     * @Date 17:22 2021/11/20
     * @Param [opportunityClueId, tradeGUID]
     * @return java.util.List<cn.visolink.business.housingSales.model.vo.OppTradeVo>
     **/
    List<OppTradeVo> getOldOppTradeVoList(@Param("opportunityClueId") String opportunityClueId,@Param("tradeGUID") String tradeGUID);

    /**
     * @Author wanggang
     * @Description //查询排卡信息
     * @Date 17:48 2021/11/20
     * @Param [opportunityClueId]
     * @return String
     **/
    String getCardType(String opportunityClueId);

    /**
     * @Author wanggang
     * @Description //更新机会状态
     * @Date 18:02 2021/11/20
     * @Param [projectClueId, clueStatus]
     * @return void
     **/
    void updateOppTStatus(@Param("projectClueId") String projectClueId,@Param("clueStatus") String clueStatus);

    /***
     *
     * @param orderNodeRecord
     *@return {}
     *@throws
     *@Description: 保存订单节点记录
     *@author FuYong
     *@date 2021/1/4 10:06
     */
    int saveOrderNodeRecord(OrderNodeRecord orderNodeRecord);

    /**
     *  跟新实收时间，金额
     *
     * @param map
     * @return
     */
    Integer updateReturnedMoneyByTradeGUID(Map map);

    /**
     * 通过userId查询用户名称
     *
     * @param userId
     * @return
     */
    String getNameByUserId(String userId);

    /**
     *  查询 x_order_template 主键id的最大值(因为这个表是自增主键)
     *
     * @return
     */
    Integer getMaxId();

    /**
     * 通过项目id查询是否已经存在项目
     *
     * @param projectId
     * @return
     */
    Integer getCountByProjectId(String projectId);
    /**
     * @Author wanggang
     * @Description //获取退款审核信息
     * @Date 10:13 2021/12/29
     * @Param [id]
     * @return cn.visolink.system.projectmanager.model.Vo.RefundApprovalVo
     **/
    RefundApprovalVo getRefundApprovalById(String id);
    /**
     * @Author wanggang
     * @Description //获取成交客户
     * @Date 8:52 2022/1/25
     * @Param [dealCstForm]
     * @return java.util.List<cn.visolink.system.projectmanager.model.DealCstData>
     **/
    List<DealCstData> getDealCstList(DealCstForm dealCstForm);
    /**
     * @Author wanggang
     * @Description //获取主客户ID
     * @Date 10:34 2022/1/25
     * @Param [dealCstForm]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getDealCstIds(DealCstForm dealCstForm);
    /**
     * @Author wanggang
     * @Description //获取关联客户信息
     * @Date 10:34 2022/1/25
     * @Param [mainIds]
     * @return java.util.List<cn.visolink.system.projectmanager.model.DealCstData>
     **/
    List<DealCstData> getDealRelationCst(@Param("list") List<String> mainIds);
}
