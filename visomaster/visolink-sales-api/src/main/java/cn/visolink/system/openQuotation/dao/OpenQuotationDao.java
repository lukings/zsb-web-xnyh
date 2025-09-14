package cn.visolink.system.openQuotation.dao;

import cn.visolink.system.openQuotation.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/12/31 13:53
 **/
public interface OpenQuotationDao {

    /**
     * @Author wanggang
     * @Description //获取项目下楼盘信息
     * @Date 14:33 2020/12/31
     * @Param [projectId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProBuild(String projectId);

    /**
     * @Author wanggang
     * @Description //获取开盘活动列表
     * @Date 14:33 2020/12/31
     * @Param
     * @return
     **/
    List<OpenActivity> getOpenActivityList(Map map);
    /**
     * @Author wanggang
     * @Description //获取开盘活动列表(导出)
     * @Date 15:07 2021/1/6
     * @Param [map]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OpenActivity>
     **/
    List<OpenActivity> getOpenActivityExport(Map map);

    /**
     * @Author wanggang
     * @Description //保存开盘活动楼栋信息
     * @Date 14:33 2020/12/31
     * @Param
     * @return
     **/
    void addOpenBuilds(@Param("list") List<OpenActivityBuild> list);

    /**
     * @Author wanggang
     * @Description //保存开盘活动楼盘信息
     * @Date 14:33 2020/12/31
     * @Param
     * @return
     **/
    void addOpenBooks(@Param("list") List<OpenBuildBook> list);

    /**
     * @Author wanggang
     * @Description //获取活动关联楼盘
     * @Date 15:01 2021/1/5
     * @Param [activityId]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OpenBuildBook>
     **/
    List<OpenBuildBook> getOpenBooks(String activityId);
    /**
     * @Author wanggang
     * @Description //保存开盘活动概览图坐标信息
     * @Date 14:33 2020/12/31
     * @Param
     * @return
     **/
    void addOpenBuildSite(@Param("list") List<OpenBuildSite> list);

    /**
     * @Author wanggang
     * @Description //获取活动概览图坐标
     * @Date 15:03 2021/1/5
     * @Param [activityId]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OpenBuildSite>
     **/
    List<OpenBuildSite> getOpenBuildSite(String activityId);
    /**
     * @Author wanggang
     * @Description //保存开盘活动不可设置房间
     * @Date 14:33 2020/12/31
     * @Param
     * @return
     **/
    void addOpenNotRoom(@Param("list") List<OpenNotRoom> list);

    /**
     * @Author wanggang
     * @Description //保存开盘活动折扣信息
     * @Date 14:33 2020/12/31
     * @Param
     * @return
     **/
    void addOpenDiscount(@Param("list") List<OpenDiscount> list);

    /**
     * @Author wanggang
     * @Description //获取活动折扣信息
     * @Date 15:02 2021/1/5
     * @Param [activityId]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OpenDiscount>
     **/
    List<OpenDiscount> getOpenDiscount(String activityId);
    /**
     * @Author wanggang
     * @Description //删除开盘活动折扣信息
     * @Date 14:10 2021/1/4
     * @Param [id]
     * @return void
     **/
    void delOpenDiscount(String id);

    /**
     * @Author wanggang
     * @Description //删除开盘活动设置房间
     * @Date 14:10 2021/1/4
     * @Param [id]
     * @return void
     **/
    void delOpenNotRoom(Map map);

    /**
     * @Author wanggang
     * @Description //删除开盘活动概览图坐标
     * @Date 14:10 2021/1/4
     * @Param [id]
     * @return void
     **/
    void delOpenBuildSite(String id);

    /**
     * @Author wanggang
     * @Description //删除开盘活动楼盘信息
     * @Date 14:10 2021/1/4
     * @Param [id]
     * @return void
     **/
    void delOpenBooks(String id);

    /**
     * @Author wanggang
     * @Description //删除开盘活动楼栋
     * @Date 14:10 2021/1/4
     * @Param [id]
     * @return void
     **/
    void delOpenBuilds(Map map);
    /**
     * @Author wanggang
     * @Description //保存开盘基本信息
     * @Date 14:13 2021/1/4
     * @Param [openActivity]
     * @return void
     **/
    void addOpenActivity(OpenActivity openActivity);
    /**
     * @Author wanggang
     * @Description //获取活动详情
     * @Date 14:56 2021/1/5
     * @Param [activityId]
     * @return cn.visolink.system.openQuotation.model.OpenActivity
     **/
    OpenActivity getOpenActivity(String activityId);
    /**
     * @Author wanggang
     * @Description //更新开盘基本信息
     * @Date 14:14 2021/1/4
     * @Param [openActivity]
     * @return void
     **/
    void updateOpenActivity(OpenActivity openActivity);
    /**
     * @Author wanggang
     * @Description //获取活动楼栋
     * @Date 14:32 2021/1/4
     * @Param [map]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OpenActivityBuild>
     **/
    List<OpenActivityBuild> getActivityBuilds(Map map);
    /**
     * @Author wanggang
     * @Description //获取楼栋是否存在订单
     * @Date 14:32 2021/1/4
     * @Param [map]
     * @return int
     **/
    int getBuildOrderCount(Map map);
    /**
     * @Author wanggang
     * @Description //获取销控房间ID
     * @Date 17:26 2021/1/4
     * @Param [bldGUID]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getControl(String bldGUID);
    /**
     * @Author wanggang
     * @Description //获取不显示的房间ID
     * @Date 11:33 2021/1/5
     * @Param [activityId, bldId]
     * @return java.util.List
     **/
    List<OpenNotRoom> getNotShowRoom(@Param("activityId") String activityId);

    /**
     * @Author wanggang
     * @Description //获取不显示的房间ID
     * @Date 11:33 2021/1/5
     * @Param [activityId, bldId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getNotShowRoomID(@Param("activityId") String activityId,@Param("bldId") String bldId);
    /**
     * @Author wanggang
     * @Description //更新状态
     * @Date 10:31 2021/1/6
     * @Param [map]
     * @return void
     **/
    void updateOpenActivityStatus(Map map);
    /**
     * @Author wanggang
     * @Description //获取活动当前状态
     * @Date 17:45 2021/1/6
     * @Param [map]
     * @return java.lang.String
     **/
    String queryOpenActivityStatus(Map map);
    /**
     * @Author wanggang
     * @Description //获取已认购房间ID
     * @Date 19:06 2021/1/6
     * @Param [bldId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getOrderOkRoomIds(String bldId);
    /**
     * @Author wanggang
     * @Description //获取未完成认购的房间ID
     * @Date 19:07 2021/1/6
     * @Param [bldId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getOrderRoomIds(String bldId);
    /**
     * @Author wanggang
     * @Description //获取开盘明细
     * @Date 16:44 2021/1/7
     * @Param [map]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OpenActivityResult>
     **/
    List<OpenActivityResult> getOpenActivityResult(Map map);
    /**
     * @Author wanggang
     * @Description //获取订单列表
     * @Date 10:55 2021/1/8
     * @Param [map]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OrderList>
     **/
    List<OrderList> getOrderList(Map map);
    /**
     * @Author wanggang
     * @Description //获取订单详情
     * @Date 19:22 2021/1/8
     * @Param [orderNo]
     * @return cn.visolink.system.openQuotation.model.OrderDetail
     **/
    OrderDetail getOrderDetail(String orderNo);
    /**
     * @Author wanggang
     * @Description //获取订单关联购房人
     * @Date 19:22 2021/1/8
     * @Param [orderNo]
     * @return java.util.List<cn.visolink.system.openQuotation.model.Buyers>
     **/
    List<Buyers> getRelationBuyers(String orderNo);
    /**
     * @Author wanggang
     * @Description //获取房间订单
     * @Date 18:16 2021/1/11
     * @Param [map]
     * @return java.lang.String
     **/
    String getRoomOrder(Map map);
    /**
     * @Author wanggang
     * @Description //获取订单节点
     * @Date 14:16 2021/1/13
     * @Param [orderNo]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OrderNode>
     **/
    List<OrderNode> getOrderNodes(String orderNo);
    /**
     * @Author wanggang
     * @Description //获取活动商户信息
     * @Date 15:06 2021/1/14
     * @Param [activityId]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OpenActivityBank>
     **/
    List<OpenActivityBank> getActivityBank(String activityId);
    /**
     * @Author wanggang
     * @Description //获取商户信息
     * @Date 15:06 2021/1/14
     * @Param [proIds]
     * @return java.util.List<cn.visolink.system.openQuotation.model.ProBank>
     **/
    List<ProBank> getBankByPros(String proFid);
    /**
     * @Author wanggang
     * @Description //删除商户信息
     * @Date 16:27 2021/1/14
     * @Param [activityId]
     * @return void
     **/
    void delActivityBank(String activityId);
    /**
     * @Author wanggang
     * @Description //添加商户信息
     * @Date 16:27 2021/1/14
     * @Param [list]
     * @return void
     **/
    void addActivityBank(@Param("list") List<OpenActivityBank> list);
    /**
     * @Author wanggang
     * @Description //校验时间配置是否有效
     * @Date 10:39 2021/1/24
     * @Param [map]
     * @return int
     **/
    int getIsTimeOk(Map map);
    /**
     * @Author wanggang
     * @Description //获取销控房间ID
     * @Date 15:47 2021/1/29
     * @Param [bldIds]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getControls(String bldIds);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 17:15 2021/1/29
     * @Param [list]
     * @return void
     **/
    void addOpenActivityRoom(@Param("list") List<OpenActivityRoom> list);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 17:15 2021/1/29
     * @Param [activityId]
     * @return java.util.List<cn.visolink.system.openQuotation.model.OpenActivityRoom>
     **/
    List<OpenActivityRoom> getOpenActivityRoom(String activityId);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 17:16 2021/1/29
     * @Param [activityId]
     * @return void
     **/
    void delOpenActivityRoom(String activityId);
    /**
     * @Author wanggang
     * @Description //查询房间是否可删除
     * @Date 13:39 2021/2/1
     * @Param [roomId]
     * @return int
     **/
    int queryRoomIsDel(String roomId);

    /***
    *
     * @param projectId
    *@return {}
    *@throws
    *@Description: 查询旭客汇认购规则
    *@author FuYong
    *@date 2021/3/22 18:00
    */
    Map getProjectRuleByPojId(@Param("projectId") String projectId);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 新增旭客汇认购规则
    *@author FuYong
    *@date 2021/3/22 18:01
    */
    int saveProjectRule(Map map);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 修改旭客汇认购规则
    *@author FuYong
    *@date 2021/3/22 18:01
    */
    int editProjectRule(Map map);

    /***
    *
     * @param projectId
    *@return {}
    *@throws
    *@Description: 判断项目是否配置商户号
    *@author FuYong
    *@date 2021/3/26 11:54
    */
    int getIsCollBank(@Param("projectId") String projectId);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 解除绑定
    *@author FuYong
    *@date 2021/3/26 15:15
    */
    int editOrder(Map map);

    /***
    *
     * @param id
    *@return {}
    *@throws
    *@Description:
    *@author FuYong
    *@date 2021/3/26 16:36
    */
    String getAccountName(String id);

    /***
    *
     * @param openId
    *@return {}
    *@throws
    *@Description:
    *@author FuYong
    *@date 2021/3/26 16:36
    */
    String getBrokerName(String openId);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 保存节点记录
    *@author FuYong
    *@date 2021/3/26 16:56
    */
    int saveOrderNode(Map map);

    /**
     * 线下支付凭证审核列表
     *
     * @param map
     * @return
     */
    List<Map> offlinePayCheckList(Map map);

    /**
     * 审批凭证
     *
     * @param map
     * @return
     */
    Integer checkCertificate(Map map);

    /**
     * 查看驳回记录通过主键id
     *
     * @param map
     * @return
     */
    Map getRecordById(Map map);

    /**
     * 导出审核列表数据通过项目id
     * @param map
     * @return
     */
    List<OfflineCertificate> selectCertificateList(Map map);

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
     * @return
     */
    Integer updateOppTradeVo(Map dataMap);

    /**
     * 获取交易表数据
     *
     * @param tradeGUID
     * @return
     */
    OppTradeVo getOppTradeVo(String tradeGUID);

    /**
     * 查询机会信息
     *
     * @param opportunityClueId
     * @return
     */
    Map getOppData(String opportunityClueId);

    /**
     * 获取是否存在其他交易
     *
     * @param opportunityClueId
     * @param tradeGUID
     * @return
     */
    List<OppTradeVo> getOldOppTradeVoList(@Param("opportunityClueId") String opportunityClueId,@Param("tradeGUID") String tradeGUID);

    /**
     * 查询排卡信息
     *
     * @param opportunityClueId
     * @return
     */
    String getCardType(String opportunityClueId);

    /**
     * 更新机会状态
     *
     * @param projectClueId
     * @param clueStatus
     */
    void updateOppTStatus(@Param("projectClueId") String projectClueId,@Param("clueStatus") String clueStatus);

    /**
     * 根据交易id查询订单信息
     *
     * @param trade_guid
     * @return
     */
    Map getOrderInfoByTradeGuid(String trade_guid);

    /**
     * 保存订单节点记录
     *
     * @param orderNodeRecord
     * @return
     */
    Integer saveOrderNodeRecord(OrderNodeRecord orderNodeRecord);

    /**
     * 查询认购，签约消息
     *
     * @param map
     * @param
     */
    List<Map> selectMessageALL(Map map);

    /**
     * 根据项目id查询销售经理
     *
     * @param projectId
     * @return
     */
    List<Map> findXSJL(@Param("projectId") String projectId);

    /**
     * 认购，签约添加消息
     *
     * @param list
     * @param
     */
    void insertMessage(List<Map> list);

    /**
     *  跟新实收时间，金额
     *
     * @param map
     * @return
     */
    Integer updateReturnedMoneyByTradeGUID(Map map);

    /**
     * 根据username查询名字
     *
     * @param username
     * @return
     */
    String getNameByusername(String username);

    /**
     * @Author wanggang
     * @Description //删除销控数据
     * @Date 15:23 2021/11/30
     * @Param [roomID]
     * @return void
     **/
    void updateHousingSalesControl(@Param("roomId") String roomID);

    /**
     * 添加跟进记录表
     *
     * @param map
     */
    void savaFollowupRecord(HashMap<String, Object> map);
    /**
     * @Author wanggang
     * @Description //获取线下支付凭证审核信息
     * @Date 11:25 2021/12/29
     * @Param [map]
     * @return cn.visolink.system.openQuotation.model.OfflineCertificate
     **/
    OfflineCertificate getOfflinePay(Map map);
}
