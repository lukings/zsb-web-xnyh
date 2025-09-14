package cn.visolink.message.dao;



import cn.visolink.message.model.OverdueUnconsumedProjectRecord;
import cn.visolink.message.model.SysLog;
import cn.visolink.message.model.form.MessageClueRelation;
import cn.visolink.message.model.form.MessageForm;
import cn.visolink.message.model.form.UpdateCluesMessage;
import cn.visolink.system.channel.model.form.ProjectProtectRuleForm;
import cn.visolink.system.channel.model.vo.CustomerAddLogVo;
import cn.visolink.system.channel.model.vo.CustomerFodLogVo;
import cn.visolink.system.channel.model.vo.ProjectProtectRuleVO;
import cn.visolink.system.channel.model.vo.ReferralVo;
import cn.visolink.system.openQuotation.model.OppTradeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 吴要光
 * @since 2019-09-03
 */
@Mapper
@Repository
public interface MessageMapper {

    /**
     *查询客户线索中的报备
     * @return 客户详情
     * */
    List<Map> selectMessage();

    /**
     *查询机会表中的报备
     * @return 客户详情
     * */
    List<Map> selectOpportunity();

    /**
     * 查询所有的规则
     * */
    List<Map> selectAllRule();

    /**
     * 查询所有内渠可发消息的数据
     * @return
     */
    List<Map> selectWarningMessage(Map map);

    /**
     *查询所有置业顾问可发消息的机会数据
     * */
    List<Map> selectOpportunityWarningMessage(Map map);

    /**
     * 查询所有已经认购未签约的消息
     * */
    List<Map> selectTradeWarningMessage(Map map);

    /**
     * 预警
     * */
    List<Map> selectTradeWarningMessageInfo(Map map);

    /**
     * 回款逾期的消息
     * */
    List<Map> returnMoney(Map map);

    /**
     * 回款逾期预警消息
     * */
    List<Map> returnMoneyWarning(Map map);

    /**
     *更新线索表
     *
     */
    void updateBprojectClues(UpdateCluesMessage updateCluesMessage);
    /**
     *更新机会表
     *
     */
    void updateBprojectopportunity(UpdateCluesMessage updateCluesMessage);
    /**
     * 插入消息
     */
    void insertMessage(@Param("messageList") List<MessageForm> list);

    /**
     * 插入消息
     */
    void insertMessageList(@Param("messageList") List<MessageForm> list);

    /**
     * 插入新规则
     * */
    void ProjectProtectRuleInfo_Inset(ProjectProtectRuleForm projectProtectRuleForm);

    /**
     * 修改规则
     */
    void ProjectProtectRule_Update(ProjectProtectRuleForm projectProtectRuleForm);

    /**
     * 查询代理公司规则是否存在
     */
    ProjectProtectRuleVO ProjectProtectProxyRuleIsExist_Select(ProjectProtectRuleForm projectProtectRuleForm);

    /**
     * 添加规则记录表
     */
    void ProjectProtectRuleLogs_Inset(Map map);

    /**
     * 添加规则记录详细表
     */
    void ProjectProtectRuleLogsDetails_Inset(ProjectProtectRuleVO projectProtectRuleVO);

    /**
     *查询外渠的公司与其对应的规则
     */
    List<ProjectProtectRuleVO> ProjectProtectProxyRule_Select(Map map);

    /**
     * 添加公共池数据
     * */
    void insertPublic(Map map);

    /**
     * 删除规则
     * */
    void deleteRule(Map map);

    /**
     * 新增转介记录
     * @param map
     * @return
     */
    int insertCustomerDistributionRecords(Map map);

    /**
     * 置业顾问制空
     * @param projectClueId
     * @return
     */
    int updateSalesAttribution(@Param("projectClueId") String projectClueId);

    /**
     * 获得过期的全民经纪人规则
     * @param map
     * @return
     */
    List<Map> getExpiredRule(Map map);

    /**
     * 获得需要启动的全民经纪人规则
     * @param map
     * @return
     */
    List<Map> getEnableRule(Map map);

    /**
     * 获得过期的全民经纪人规则
     * @param map
     * @return
     */
    int updateBrokerRule(Map map);

    /**
     * 更新楼盘是否可报备
     * @param map
     * @return
     */
    int updateBuildBook(Map map);

    /**
     * 保存日志
     * @param sysLog
     * @return
     */
    void insertLogs(SysLog sysLog);

    /**
     * 更新中介公司组织名称
     * @param map
     * @return
     */
    void updateCompName(Map map);

    /**
     * 查询跟进逾期记录
     * @param projectClueId
     * @return
     */
    Map getOldSaleInfo(String projectClueId);

    /***
     *
     * @param messageForm
     *@return {}
     *@throws
     *@Description:新增消息
     *@author FuYong
     *@date 2020/9/23 15:10
     */
    int saveMessageInfo(MessageForm messageForm);

    /***
     *
     * @param messageClueList
     *@return {}
     *@throws
     *@Description: 新增消息线索关联关系
     *@author FuYong
     *@date 2020/9/23 15:11
     */
    int saveMessageClueRelation(@Param(value = "messageClueList") List<MessageClueRelation> messageClueList);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:19 2020/10/19
     * @Param [OpportunityClueId]
     * @return java.lang.String
     **/
    String selectIsPubilc(String OpportunityClueId);

    /***
     *
     * @param userId
     * @param projectId
     * @param jobCode
     *@return {}
     *@throws
     *@Description: 查询用户配置消息
     *@author FuYong
     *@date 2020/11/19 14:59
     */
    List<Map> getUserMessageTypeList(@Param("userId") String userId,
                                        @Param("projectId") String projectId,
                                        @Param("jobCode") String jobCode);

    /***
     *
     * @param userId
     * @param projectId
     * @param jobCode
     * @param groupValue
     *@return {}
     *@throws
     *@Description: 是否又配置消息
     *@author FuYong
     *@date 2020/11/25 11:07
     */
    Map getUserMessageTypeInfo(@Param("userId") String userId,
                               @Param("projectId") String projectId,
                               @Param("jobCode") String jobCode,
                               @Param("groupValue") String groupValue);
    /**
     * @Author wanggang
     * @Description //获取集团参数(掉入区域池)
     * @Date 11:15 2022/4/21
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAddAreaParam();
    /**
     * @Author wanggang
     * @Description //获取项目池客户
     * @Date 13:48 2022/4/21
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProPoolCst();

    /**
     * @Author wanggang
     * @Description //获取招商池客户
     * @Date 13:48 2022/4/21
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getZsPoolCst();
    /**
     * @Author wanggang
     * @Description //获取项目规则
     * @Date 14:46 2022/4/21
     * @Param [projectId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProAddAreaParam(String projectId);
    /**
     * @Author wanggang
     * @Description //掉入区域客户池
     * @Date 18:00 2022/4/21
     * @Param [updatePros]
     * @return void
     **/
    void addAreaPool(@Param("list") List<String> updatePros);
    /**
     * @Author luqianqian
     * @Description //掉入区域客户池推送明源
     * @Date 2022/5/23
     * @Param [poolId]
     * @return map
     **/
    Map getSendMyInfoByAreaPool(String poolId);
    /**
     * @Author wanggang
     * @Description //掉入总招商客户池
     * @Date 18:01 2022/4/21
     * @Param [updateZss]
     * @return void
     **/
    void addZZSPool(@Param("list") List<String> updateZss);
    /**
     * @Author wanggang
     * @Description //获取集团参数(掉入全国池)
     * @Date 18:09 2022/4/21
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAddNationalParam();
    /**
     * @Author wanggang
     * @Description //获取区域池客户
     * @Date 18:13 2022/4/21
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAreaPoolCst();
    /**
     * @Author wanggang
     * @Description //获取总招商池客户
     * @Date 18:13 2022/4/21
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getZZsPoolCst();
    /**
     * @Author wanggang
     * @Description //获取项目参数(掉入全国池)
     * @Date 18:14 2022/4/21
     * @Param [projectId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProAddNationalParam(String projectId);
    /**
     * @Author wanggang
     * @Description //掉入全国池
     * @Date 18:15 2022/4/21
     * @Param [updatePros]
     * @return void
     **/
    void addNationalPool(@Param("list") List<String> updatePros);
    /**
     * @Author luqianqian
     * @Description //掉入全国池推送明源
     * @Date 2022/5/23
     * @Param [poolId]
     * @return map
     **/
    Map getSendMyInfoByNationalPool(String poolId);
    /**
     * @Author wanggang
     * @Description //查询所有跟进预警客户
     * @Date 20:40 2022/4/21
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> selectOpportunityFollowWarning(String date);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 21:45 2022/4/21
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> selectOpportunityFollowExpire();
    /**
     * @Author wanggang
     * @Description //获取招商入池参数
     * @Date 14:16 2022/4/22
     * @Param [projectId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAllZsParam(String projectId);
    /**
     * @Author wanggang
     * @Description //掉入项目公共池
     * @Date 14:37 2022/4/22
     * @Param [fformList]
     * @return void
     **/
    void addProList(@Param("list") List<Map> fformList);
    /**
     * @Author wanggang
     * @Description //掉入招商池
     * @Date 14:37 2022/4/22
     * @Param [fformList]
     * @return void
     **/
    void addZsList(@Param("list") List<Map> fformList);
    /**
     * @Author wanggang
     * @Description //更新业务员为空
     * @Date 15:49 2022/4/22
     * @Param [mapListAcFollowExpire]
     * @return void
     **/
    void delSalesOpp(@Param("list") List<Map> mapListAcFollowExpire);
    void delSalesClue(@Param("list") List<Map> clueList);
    /**
     * @Author wanggang
     * @Description //获取上线项目
     * @Date 16:18 2022/4/22
     * @Param []
     * @return java.util.List<java.lang.String>
     **/
    List<String> getAllProIds();
    /**
     * @Author wanggang
     * @Description //获取客户每月最大保留数
     * @Date 16:46 2022/4/22
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAllCstParam(String projectId);
    /**
     * @Author wanggang
     * @Description //获取项目下人员客户数
     * @Date 17:36 2022/4/22
     * @Param [id]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProCstlist(String projectId);
    /**
     * @Author wanggang
     * @Description //获取需要丢失的客户
     * @Date 21:15 2022/4/22
     * @Param [params]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getDelCstList(Map params);
    /**
     * @Author wanggang
     * @Description //转介超时自动驳回
     * @Date 18:15 2022/4/23
     * @Param []
     * @return void
     **/
    void automaticRejection();
    List<ReferralVo> getReferCusRejection();
    int automaticRejectionByIds(@Param("ids") List<String> ids);
    /**
     * @Author wanggang
     * @Description //获取招商转介记录
     * @Date 19:34 2022/5/20
     * @Param [map]
     * @return java.util.Map
     **/
    Map getOppZSZJ(Map map);
    /**
     * @Author wanggang
     * @Description //获取招商强转记录
     * @Date 19:34 2022/5/20
     * @Param [map]
     * @return java.util.Map
     **/
    Map getOppZSZJQZ(Map map);
    /**
     * @Author wanggang
     * @Description //更新客户状态
     * @Date 19:36 2022/5/20
     * @Param [map]
     * @return void
     **/
    void updateOppSales(Map map);
    /**
     * @Author wanggang
     * @Description //获取区域ID
     * @Date 15:04 2022/9/6
     * @Param [projectId]
     * @return java.lang.String
     **/
    String getComGUIDByProject(String projectId);
    /**
     * @Author wanggang
     * @Description //获取是否区域项目
     * @Date 16:38 2022/9/6
     * @Param [projectId]
     * @return java.lang.String
     **/
    String getIsRegionByPro(String projectId);
    /**
     * @Author wanggang
     * @Description //作废机会
     * @Date 16:46 2022/9/6
     * @Param [oppIds]
     * @return void
     **/
    void updateZFopps(@Param("list") List<String> oppIds);
    /**
     * @Author wanggang
     * @Description //获取用户手机号
     * @Date 8:56 2022/9/15
     * @Param [salesAttributionId]
     * @return java.lang.String
     **/
    Map getUserMobile(@Param("userId") String salesAttributionId);
    /**
     * @Author luqianqian
     * @Description //删除关联客户
     * @Date 16:08 2023/02/09
     * @Param [map]
     * @return int
     **/
    int delCusRelateZFopps(@Param("list") List<String> oppIds);
    int delCusRelateOpps(@Param("list") List<Map> mapListAcFollowExpire);

    /**
     * @Author luqianqian
     * @Description //获取全部公客池客户
     * @Date 18:13 2023/9/12
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAllPoolCst();

    /**
     * @Author luqianqian
     * @Description //批量更新客户标签
     * @Date 18:13 2023/9/12
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    int updateCustomerPoolDateLabel(@Param("list") List<String> list,@Param("label") String label);

    /**
     * @Author wanggang
     * @Description //公客池获取客户待办逾期
     * @Date 18:15 2022/4/23
     * @Param []
     * @return void
     **/
    void automaticObtainApprove();
    List<ReferralVo> getObtainApproveRejection();
    int automaticObtainApproveByIds(@Param("ids") List<String> ids);

    /**
     * 获取登录人的登录名
     * */
    String getUserNameById(@Param("id") String id);
    String getEmployeeNameById(@Param("id") String id);

    /**
     * 获取转介接收人的岗位
     * */
    List<String> getReferralPendingJobCode(@Param("projectId") String projectId,@Param("userId") String userId);

    /**
     * 根据项目获取下一节点审批人
     * @return 跟进记录
     * */
    List<String> getNextApprovalUserList(@Param("projectId") String projectId,@Param("jobList") List<String> jobList,@Param("opportunityClueId") String opportunityClueId);

    /**
     * 获取待办标题
     * */
    String getPendingTitle(@Param("type") String type,@Param("id") String id);

    List<ReferralVo> getFollowUpPendingRejection();

    List<ReferralVo> getSimilarCustomerReportPendingRejection();
    int automaticFollowUpPendingByIds(@Param("ids") List<String> ids);
    int automaticSimilarCustomerReportPendingByIds1(@Param("ids") List<String> ids);
    int automaticSimilarCustomerReportPendingByIds2(@Param("ids") List<String> ids);

    /**
     * 获取客户的跟进数据
     * */
    Map getFollowUpStatistics(String opportunityClueId);

    /**
     * 更新公客池客户的跟进数据
     * */
    int updateCustomerPoolStatistics(@Param("udpList") List<Map> udpList);

    /**
     * 获取所有客户
     * */
    List<Map> getAllOppCst();

    /**
     * 更新贝贝客户的跟进数据
     * */
    int updateCustomerOppStatistics(@Param("udpList") List<Map> udpList);

    /**
     * 更新报备链路
     * */
    void truncateCustomerReportRecord();
    void saveCustomerReportRecord();

    /**
     * 初始化客户等级记录表 客户新增报备日志记录表 客户跟进交易日志记录表 1130
     * */
    int truncateCustomerHistoryDate1130();

    /**
     * 初始化客户等级日志记录表历史数据1130
     * */
    int initCustomerLevelRecordHistoryDate1130();

    /**
     * 初始化客户新增报备日志记录表历史数据1130
     * */
    int initCustomerAddLogHistoryDate1130();

    /**
     * 初始化客户跟进交易日志记录表历史数据1130
     * */
    int initCustomerFollowLogHistoryDate1130();

    /**
     * 获取客户交易记录表所有客户ID
     * */
    List<String> getTradeCustomerIds();

    /**
     * 根据客户ID获取客户所有交易记录
     * */
    List<OppTradeVo> getTradeCustomerListByOppId(@Param("opportunityClueId") String opportunityClueId);

    /**
     * 获取客户是否存在其他交易
     * */
    OppTradeVo getCustomerBeforeTrade(@Param("opportunityClueId") String opportunityClueId, @Param("contractDate") String contractDate);

    /**
     * 获取客户最早报备时间
     * */
    String getCustomerFirstReportCreateTime(@Param("opportunityClueId") String opportunityClueId, @Param("type") String type, @Param("typeId") String typeId);

    /**
     * 获取客户上一次签约时间
     * */
    String getCustomerLastContractDate(@Param("opportunityClueId") String opportunityClueId, @Param("contractDate") String contractDate, @Param("type") String type, @Param("typeId") String typeId);

    /**
     * 获取客户最早签约时间
     * */
    String getCustomerFirstContractDate(@Param("opportunityClueId") String opportunityClueId, @Param("type") String type, @Param("typeId") String typeId);

List<String> getIdHd();

    /**
     * @Author luqianqian
     * @Description //查询客户报备日志记录
     * @Date 15:23 2024/11/24
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<CustomerAddLogVo> getAllCustomerAddLogList(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //查询客户跟进交易日志记录
     * @Date 15:23 2024/11/24
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<CustomerFodLogVo> getAllCustomerFodLogList(@Param("ids") List<String> ids);

    void deleteTimeoutWarning();

    int batchInsert(List<OverdueUnconsumedProjectRecord> recordList);
}
