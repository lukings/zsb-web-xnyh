package cn.visolink.system.channel.dao;

import cn.visolink.message.model.SysLog;
import cn.visolink.system.channel.model.*;
import cn.visolink.system.channel.model.form.*;
import cn.visolink.system.channel.model.vo.*;
import cn.visolink.system.excel.model.FollowUpRecordForm;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.org.model.form.OrganizationForm;
import cn.visolink.system.org.model.vo.OrganizationVO;
import cn.visolink.system.seniorbroker.vo.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2019-08-24
 */
@Mapper
@Repository
public interface ProjectCluesDao extends BaseMapper<ProjectClues> {

    /**
     * 查询渠道台账信息
     * @param projectCluesForm
     * @return
     */
    Page<ProjectCluesVO> queryAllCustmerByOther(ProjectCluesForm projectCluesForm);

    /**
     * 查询案场台账信息
     * @param projectCluesForm
     * @return
     */
    Page<ProjectCluesVO> queryAllAnChangByOther(ProjectCluesForm projectCluesForm);

    /**
     * 查询规则
     */
    ProjectProtectRuleVO ProjectProtectRuleInfo_Select(ProjectProtectRuleForm ProjectProtectRuleForm);

    /*以下是重分配选择人员所用接口*/
    /*有orgid时候查询方式*/
    Map orgRootIDByProjectId(Map map);

    /*无orgid的时候查询方式*/
    Map orgInfoByIdSelect(OrgRootForm orgRootForm);

    /*获取登陆人的信息*/
    List<Map<String, Object>> childOrgListByPid(Map map);

    List<Map<String, Object>> orgUserListByOrgIDOrEmplayName(Map map);

    /**
     * 用户详情中的用户基本信息
     * */
    ProjectCluesNew essentialInformation(Map map);

    /**
     * 客户详情中的关联客户
     * */
    List<Map> associatedCustomers(Map map);

    /**
     * 获取用户管理员权限
     * */
    int getUserJobHsxt(@Param("userId") String userId);
    /**
     * 获取用户集团权限
     * */
    int getUserJobHsjt(@Param("userId") String userId);

    /**
     * 客户详情里面的节点记录
     * */
    List<Map> nodeRecord(Map map);
    List<Map> clueNodeRecord(Map map);

    /**
     * 客户详情里面的交易记录
     * */
    List<Map> dealRecord(List<String> oppoList);

    /**
     * 查询客户是否有转介客户
     */
    List<String> getReferralOrderCustomerByOppId(String oppId);

    /**
     *客户详情里面的跟进记录
     * */
    List<Map> followUpRecord(Map map);

    /**
     * 客户详情里面的转介记录
     * */
    List<Map> toMoveRecord(Map map);

    /**
     * 首访问卷
     * */
    Map firstInterviewQuestionnaire(Map map);

    /**
     *查询重分配的项目可以分配的状态
     * */
    Map currProjectInfoSelect(@Param("projectId") String projectId);

    /**
     * 查询项目符合不符合重分配
     * */
    List<String> SelectClueConditionClue(Map map);

    /**
     * 查询项目符合不符合重分配
     * */
    List<String> SelectClueConditionAC(Map map);

    /**
     * 交易信息
     * */
    List<Map> transactionInformation(Map map);


    /**
     * 案场需要到处的文件
     * */
    List<ExcelModel> excelAc(Map map);

    /**
     * 渠道需要到处文件的sql
     * */
    List<ExcelModel> excelChannel(Map map);

    /**
     * 案场排卡导出数据需要的sql
     * */
    List<ExcelModel> excelCard(Map map);

    /**
     * 查询案场项目名称
     * */
    Map selectProjectName(Map map);

    /**
     * 客户明细
     */
    Page<ProjectCluesVO> queryAllCustmerDetails(ProjectCluesForm projectCluesForm);

    /**
     * 查询认知渠道
     * @param map
     * @return
     */
    List<Map> getMainMediaList(Map map);

    /**
     * 判断是否报备过
     * @param map
     * @return
     */
    List<Map> getCustomerMobile(Map map);

    /**
     * 根据线索ID查询机会ID
     * @param ProjectClueId
     * @return
     */
    String getOppIdBYCluesId(String ProjectClueId);
    /**
     * @Author wanggang
     * @Description //查询所有待分配线索
     * @Date 9:26 2020/3/14
     * @Param [ids]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getCluesByIds(@Param("ids") String ids);

    /**
     * @Author wanggang
     * @Description //查询项目打印配置
     * @Date 9:26 2020/3/14
     * @Param [projectId]
     * @return
     **/
    String getIsPrintStatus(@Param("projectId") String projectId);

    /**
     * @Author wanggang
     * @Description //项目打印配置
     * @Date 9:26 2020/3/14
     * @Param [java.util.Map]
     * @return
     **/
    void updateIsPrintStatus(Map map);

    /**
     * 置业顾问名片台账
     * @param map
     * @return
     */
    Page<CardStandingBook> getCardStandingBookList(Map map);

    /**
     * 查询置业顾问
     * @param map
     * @return
     */
    List<Map> getSalesAttributionList(Map map);

    /**
     * 更新扩展表数据
     * @param list
     * @return
     */
    void updateClueOpportunityExtend(List<ClueOpportunityExtend> list);

    /**
     * 更新扩展表数据
     * @param list
     * @return
     */
    void updateClueExtendValidity(List<ClueOpportunityExtend> list);

    /**
     * 更新扩展表数据
     * @param list
     * @return
     */
    void updateAcClueExtendValidity(List<ClueOpportunityExtend> list);

    /**
     * @Author wanggang
     * @Description //获取户型名称
     * @Date 14:22 2020/12/3
     * @Param [id]
     * @return java.lang.String
     **/
    String getHXnameById(String id);


    /**
     * 根据置业顾问查询乘车表主键
     * @author zhaohongen
     * @date 2021/04/08
     **/
    List<String> getTaxiId(String projectClueId);


    /***
     *
     * @param id
     *@return {}
     *@throws
     *@Description: 根据id查询置业顾问信息
     *@author zhaohongen
     */
    DistributionInfo getAccountUserById(@Param("id") String id);


    /**
     * 修改用户行程的置业顾问
     * @author zhaohongen
     * @date 2021/04/12
     **/
    int updateSalesAttribution(DistributionInfo distributionInfo);

    List<String> getDictLabels();

    List<String> geCustomLabels(String mark);

    String getChannelLabel(String projectClueId);

    /**
     * 交易房间分组
     * @param projectClueId
     * @return
     */
    List<Map> getDealRecordGroup(@Param("projectClueId") String projectClueId);

    /**
     * 根据线索ID获取交易记录
     * @return 交易记录
     * */
    List<Map> getDealRecord(@Param("projectClueId") String projectClueId,@Param("roomID") String roomID);
    /**
     * @Author wanggang
     * @Description //获取明源机会ID
     * @Date 16:39 2021/5/10
     * @Param [projectClueId]
     * @return java.lang.String
     **/
    String getIntensionId(String projectClueId);
    /**
     * @Author wanggang
     * @Description //获取中介公司
     * @Date 15:39 2021/5/17
     * @Param []
     * @return java.util.List<cn.visolink.system.channel.model.form.Supplier>
     **/
    List<Supplier> getSupplierList();

    /**
     * @Author wanggang
     * @Description //获取项目中介
     * @Date 17:38 2021/5/17
     * @Param [projectId]
     * @return cn.visolink.system.org.model.form.OrganizationForm
     **/
    OrganizationForm getZJBM(String projectId);
    /**
     * @Author wanggang
     * @Description //获取项目中介ID
     * @Date 18:00 2021/5/17
     * @Param [code]
     * @return java.lang.String
     **/
    String getCompnayID(String companyCode);
    /**
     * @Author wanggang
     * @Description //获取中介组织ID
     * @Date 18:51 2021/5/17
     * @Param [id, companyId]
     * @return java.lang.String
     **/
    String getOrgId(@Param("pid") String pid,@Param("OrgCompanyID") String companyId);
    /**
     * @Author wanggang
     * @Description //获取中介门店
     * @Date 9:21 2021/5/18
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getIntermediaryList();
    /**
     * @Author wanggang
     * @Description //更新门店组织状态
     * @Date 9:43 2021/5/18
     * @Param [map]
     * @return void
     **/
    void updateIntermediaryRule(IntermediaryStores map);
    /**
     * @Author wanggang
     * @Description //更新中介门店
     * @Date 15:10 2021/5/18
     * @Param [map]
     * @return void
     **/
    void updateCompany(IntermediaryStores map);
    /**
     * @Author wanggang
     * @Description //更新中介门店信息
     * @Date 13:58 2021/5/19
     * @Param [rule]
     * @return void
     **/
    void updateCompDesc(ProjectProtectRuleForm rule);
    /**
     * @Author wanggang
     * @Description //更新淘客池公池，线索扩展表数据
     * @Date 11:00 2021/6/7
     * @Param [cluesList]
     * @return void
     **/
    void updateTaoPublic(@Param("cluesList") List<String> cluesList);

    /**
     * @Author mays
     * @Description 查询当前客户联名来源信息
     * @Date 10:40 2021/6/3
     * @Param map
     * @return Map
     **/
    Map getSourceCustByClueId(String projectClueId);

    /***
     *
     *@return {}
     *@throws
     *@Description: 删除公共池数据（逻辑）
     *@author FuYong
     *@date 2021/7/6 17:19
     */
    int updateCustomerPublicPool(@Param(value="projectClueList") List<String> projectClueId);

    /***
     *
     *@return {}
     *@throws
     *@Description: 删除公共池数据（逻辑）
     *@author FuYong
     *@date 2021/7/6 17:19
     */
    int editCustomerPublicPoolByOppId(@Param(value="oppClueList") List<String> oppClueList);

    /***
     *
     *@return {}
     *@throws
     *@Description: 更新线索机会表扩展字段
     *@author FuYong
     *@date 2021/7/6 17:19
     */
    int editClueExtendValidity(@Param("clueStatus") String clueStatus,@Param(value="clueList") List<String> clueList);
    /**
     * @Author wanggang
     * @Description //获取交易信息
     * @Date 10:21 2021/12/18
     * @Param [projectClueId, roomID]
     * @return java.util.Map
     **/
    Map getTradeByOppId(@Param("projectClueId") String projectClueId,@Param("roomId") String roomId);
    /**
     * @Author wanggang
     * @Description //获取线索转介记录
     * @Date 10:26 2022/3/30
     * @Param [map]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ClueReferralVo>
     **/
    List<ClueReferralVo> getClueReferral(ClueReferralForm map);
    /**
     * @Author wanggang
     * @Description //查询规则新
     * @Date 9:06 2022/4/14
     * @Param [map]
     * @return cn.visolink.system.channel.model.ProjectRuleDetail
     **/
    List<ProjectRuleDetail> selectProjectRule(Map map);
    /**
     * @author liang
     * @date 2023/10/17 8:56
     * @description: 导出规则配置所需查询
     */
    List<ProjectRuleDetail> selectProjectRulexport();
    /**
     * @Author wanggang
     * @Description //保存规则
     * @Date 9:56 2022/4/14
     * @Param [addList]
     * @return void
     **/
    void addProjectRule(@Param("list") List<ProjectRuleDetail> addList);
    /**
     * @Author wanggang
     * @Description //更新规则
     * @Date 9:56 2022/4/14
     * @Param [updateList]
     * @return void
     **/
    void updateProjectRule(@Param("list") List<ProjectRuleDetail> updateList);
    /**
     * @Author wanggang
     * @Description //转介列表
     * @Date 14:25 2022/4/14
     * @Param [map]
     * @return java.util.List<cn.visolink.system.channel.model.ReferralRecord>
     **/
    List<ReferralRecord> selectReferralClue(Map map);
    /**
     * @Author wmy
     * @Description //获取线索数据
     * @Date 20:47 2022/9/2
     * @Param [projectCluesForm]
     * @return java.util.List<cn.visolink.system.channel.model.ProjectClues>
     **/
    List<ProjectCluesNew> channelProjectClues(Map projectCluesForm);

    /**
     * @Author wanggang
     * @Description //获取机会数据
     * @Date 9:42 2022/4/15
     * @Param [projectCluesForm]
     * @return com.github.pagehelper.Page<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> courtCase(ExcelForm projectCluesForm);
    String courtCaseCount(ExcelForm projectCluesForm);

    /**
     * @Author wmy
     * @Description //获取放弃记录
     * @Date 21:05 2022/9/5
     * @Param [map]
     * @return cn.visolink.system.channel.model.vo.CustomerDistributionRecordsVO
     **/
    List<CustomerDistributionRecordsVO> getAbandonRecord(CustomerDistributionRecords customerDistributionRecords);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 17:26 2022/4/16
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> followUpOppRecord(Map map);
    /**
     * @Author wanggang
     * @Description //获取机会信息
     * @Date 21:06 2022/4/16
     * @Param [map]
     * @return cn.visolink.system.channel.model.vo.OppInformation
     **/
    OppInformation oppInformation(Map map);
    /**
     * @Author wanggang
     * @Description //获取机会附件
     * @Date 21:25 2022/4/16
     * @Param [map]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getOppEnclosures(Map map);

    /**
     * @Author wanggang
     * @Description //获取线索附件
     * @Date 21:25 2022/4/16
     * @Param [map]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getClueEnclosures(Map map);


    String selectRelationCompany(String opportunityClueId);

    InformationVO selectProjectClues(Map map);

    /**
     * @Author wmy
     * @Description //编辑客户等级
     * @Date 21:05 2022/9/5
     * @Param [map]
     * @return int
     **/
    int updateCustomerGrade(ExcelForm projectCluesForm);

    /**
     * @Author wmy
     * @Description //申诉记录
     * @Date 21:05 2022/9/6
     * @Param [appeal]
     * @return AppealVo
     **/
    List<AppealVo> getAppealRecord(Appeal appeal);

    /**
     * @Author wmy
     * @Description //任务管理台账
     * @Date 21:05 2022/9/6
     * @Param [Task]
     * @return TaskVo
     **/
    List<TaskVo> getTaskAccount(Task task);

    /**
     * @Author wmy
     * @Description //任务管理台账-标记查询
     * @Date 21:05 2022/9/6
     * @Param [Task]
     * @return TaskVo
     **/
    List<TaskVo> getTaskAccountForDraw(Task task);

    /**
     * @Author wmy
     * @Description //报备失败台账
     * @Date 21:05 2022/9/6
     * @Param [ReportFail]
     * @return ReportFailVo
     **/
    List<ReportFailVo> getReportFailAccount(ReportFail reportFail);

    /**
     * @Author wanggang
     * @Description //获取申诉信息
     * @Date 14:45 2022/9/4
     * @Param [map]
     * @return cn.visolink.system.channel.model.form.AppealForm
     **/
    AppealForm getAppealDetail(Map map);
    /**
     * @Author wanggang
     * @Description //获取申诉附件
     * @Date 14:46 2022/9/4
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAppealFj(Map map);
    /**
     * @Author wanggang
     * @Description //保存项目规则
     * @Date 9:42 2022/8/31
     * @Param [map]
     * @return void
     **/
    void addProRuleEnclosures(ProjectRuleDetail map);
    /**
     * @Author wanggang
     * @Description //删除附件
     * @Date 10:08 2022/8/31
     * @Param [map]
     * @return void
     **/
    void delProRuleEnclosures(ProjectRuleDetail map);
    /**
     * @Author wanggang
     * @Description //获取规则附件
     * @Date 10:17 2022/8/31
     * @Param [id]
     * @return java.util.List<java.lang.String>
     **/
    List<Map> getProRuleEnclosures(String id);

    /**
     * @Author wmy
     * @Description //根据任务id,获取成员
     * @Date 21:05 2022/9/21
     * @Param [ReportFail]
     * @return ReportFailVo
     **/
    List<TaskMember> getUserByTaskId(String taskIds);

//    获取任务完成情况
    Map getTaskComplete(Map quMap);

    List<Map> getCstSource();

    List<Map> getCstIndustryOneNew();

    List<Map> getCstIndustryTwo(Map map);

    /**
     * @Author luqianqian
     * @Description //获取业务员
     * @Date 21:28 2023/2/13
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProZsSales(Map map);
    /**
     * @Author wanggang
     * @Description //获取业务员组织
     * @Date 19:21 2022/8/23
     * @Param [param]
     * @return java.util.Map
     **/
    Map getUserOrg(Map param);
    /**
     * 获取项目对应公司GUID
     * */
    String getComGUIDByProject(String projectId);
    /**
     * @Author wanggang
     * @Description //获取项目规则
     * @Date 11:06 2022/4/27
     * @Param [projectId, sourceType]
     * @return cn.visolink.business.propertyconsultant.model.ProjectRuleDetail
     **/
    ProjectRuleDetail selectProjectRuleZs(@Param("projectId") String projectId,@Param("sourceType") String sourceType);
    /**
     * @Author wanggang
     * @Description //获取机会信息
     * @Date 15:21 2022/4/28
     * @Param [opportunityClueId]
     * @return java.util.Map
     **/
    Map getOpportunityById(String opportunityClueId);
    /**
     * @Author wanggang
     * @Description //保存分配记录
     * @Date 14:37 2022/6/30
     * @Param [oldMap]
     * @return void
     **/
    void addRelCustomerRecord(Map oldMap);
    /**
     * @Author wanggang
     * @Description //获取团队人员（临时表）
     * @Date 9:12 2022/6/17
     * @Param [userName, projectId]
     * @return java.util.List<java.lang.String>
     **/
    ReferralVo getOldOpportunityClueInfo(@Param("opportunityClueId") String opportunityClueId);
    /**
     * @Author wanggang
     * @Description //更新机会信息
     * @Date 17:06 2022/5/3
     * @Param [oppMap]
     * @return void
     **/
    void updateOppCst(Map oppMap);
    /**
     * 新增单条消息
     * @param message
     * @return
     */
    int insertOneMessage(Message message);

    /**
     * @Author wanggang
     * @Description //保存数据权限查看审批信息
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    void addDataViewPremission(UserOrgRelForm map);
    void addDataViewPremissionRecord(UserOrgRelForm map);
    /**
     * @Author wanggang
     * @Description //管理员保存数据权限查看审批信息
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    void addAdminDataViewPremission(UserOrgRelForm map);

    /**
     * @Author wanggang
     * @Description //保存数据权限查看审批信息
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    void updateDataViewPremission(UserOrgRelForm map);
    /**
     * @Author wanggang
     * @Description //管理员保存数据权限查看审批信息
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    void updateAdminDataViewPremission(UserOrgRelForm map);
    /**
     * @Author wanggang
     * @Description //获取数据权限查看审批列表
     * @Date 11:27 2022/8/26
     * @Param [map]
     * @return java.util.List<cn.visolink.system.channel.model.form.UserOrgRelForm>
     **/
    List<UserOrgRelForm> getDataViewPremissionList(Map map);
    /**
     * @Author wanggang
     * @Description //获取数据权限查看审批详情
     * @Date 11:53 2022/8/26
     * @Param [map]
     * @return cn.visolink.system.channel.model.form.UserOrgRelForm
     **/
    UserOrgRelForm getDataViewPremissionDetail(Map map);
    /**
     * @Author wanggang
     * @Description //获取数据权限查看审批是否存在
     * @Date 11:27 2022/8/26
     * @Param [map]
     * @return java.util.List<cn.visolink.system.channel.model.form.UserOrgRelForm>
     **/
    List<UserOrgRelForm> getDataViewPremissionApprove(UserOrgRelForm map);
    UserOrgRelForm getDataViewPremissionApproveStatus(UserOrgRelForm map);
    int updateDateViewPremissionStatus(@Param("uf") List<UserOrgRelForm> uf);

    /**
     * @Author wanggang
     * @Description //管理员获取人员所有生效的权限信息
     * @Date 11:27 2022/8/26
     * @Param [map]
     * @return java.util.List<cn.visolink.system.channel.model.form.UserOrgRelForm>
     **/
    List<UserOrgRelForm> getAdminDataViewPremissionApprove(UserOrgRelForm map);
    /**
     * @Author wanggang
     * @Description //管理员设置权限 禁用全部人员权限
     * @Date 11:27 2022/8/26
     * @Param [map]
     * @return java.util.List<cn.visolink.system.channel.model.form.UserOrgRelForm>
     **/
    int updateAdminDateViewPremissionStatus(@Param("userId") String userId);

    /**
     * @Author wanggang
     * @Description //获取人员权限
     * @Date 11:27 2022/8/26
     * @Param [map]
     * @return java.util.List<cn.visolink.business.propertyconsultant.model.form.UserOrgRelForm>
     **/
    List<Map> getDataViewPremissionOrg(UserOrgRelForm map);
    List<Map> getDataViewPremissionNoOrg(UserOrgRelForm map);

    /**
     * @Author wanggang
     * @Description //获取人员数据查看权限下的小组
     * @Date 11:27 2022/8/26
     * @Param [map]
     * @return java.util.List<cn.visolink.business.propertyconsultant.model.form.UserOrgRelForm>
     **/
    List<Map> getDataViewPremissionOrgTeam(UserOrgRelForm map);

    /**
     * @Author wanggang
     * @Description //获取区域下项目
     * @Date 11:23 2022/8/18
     * @Param [comGUID]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getComProList(String comGUID);
    /**
     * 保存错误日志
     * @param sysLog1
     * @return
     */
    void insertLogs(SysLog sysLog1);
    /**
     * @Author luqianqian
     * @Description //获取业务员OA岗位组织信息
     * @Date 10:20 2022/9/15
     * @Param [projectId]
     * @return java.lang.String
     **/
    Map getUserOAInfo(@Param("userId") String userId);
    /**
     * @Author wanggang
     * @Description //获取区域ID
     * @Date 10:20 2022/9/15
     * @Param [projectId]
     * @return java.lang.String
     **/
    String getProComId(String projectId);
    /**
     * @Author wanggang
     * @Description //获取用户手机号
     * @Date 10:37 2022/9/3
     * @Param [userId]
     * @return java.lang.String
     **/
    String getUserMobile(String userId);

    /**
     * @Author luqianqian
     * @Description //获取关联客户列表
     * @Date 16:08 2023/02/09
     * @Param [map]
     * @return int
     **/
    List<Map> getCusRelateList(Map map);

    /**
     * @Author luqianqian
     * @Description //获取跟进数据
     * @Date 9:42 2022/4/15
     * @Param [projectCluesForm]
     * @return com.github.pagehelper.Page<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getFollowUpRecordList(FollowUpRecordVO followUpRecordVO);
    /**
     * @Author luqianqian
     * @Description //获取跟进数据-线索
     * @Date 9:42 2022/4/15
     * @Param [projectCluesForm]
     * @return com.github.pagehelper.Page<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getFollowUpRecordListClue(FollowUpRecordVO followUpRecordVO);

    int getFollowUpRecordCount(FollowUpRecordVO followUpRecordVO);

    int getFollowUpRecordCountClue(FollowUpRecordVO followUpRecordVO);

    List<Map> getCommonDict(@Param("parentCode") String parentCode);

    /**
     * @Author luqianqian
     * @Description //获取数据权限查看的全部权限信息
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    List<UserOrgRelForm> getAllDataViewPremissionInfo(UserOrgRelForm map);

    /**
     * @Author luqianqian
     * @Description //获取关联客户的主企业
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    List<String> getRelaCustomerMainId(ExcelForm projectCluesForm);

    /**
     * @Author luqianqian
     * @Description //获取登录人可分配客户的权限集合
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    List<Map> getAllocationAllCustomerUserOrgInfo(@Param("userId") String userId);

    List<Map> getAllocationAllCustomerUserOrgInfoCall(@Param("userId") String userId);

    /**
     * @Author wanggang
     * @Description //获取区域下项目
     * @Date 11:23 2022/8/18
     * @Param [comGUID]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getProList(String comGUID);

    List<String> getProListAll(String comGUID);

    List<String> getProListD(String comGUID);

    /**
     * @Author wanggang
     * @Description //获取可分配的专员新
     * @Date 11:23 2022/8/18
     * @Param [comGUID]
     * @return java.util.List<java.lang.String>
     **/
    List<Map> getGlAllocationPropertyConsultantZygw(Map map);
    List<Map> getGlAllocationPropertyConsultantZygwCall(Map map);


    /**
     * @Author luqianqian
     * @Description //获取项目联动
     * @Date 20:35 2023/06/13
     * @Param [projectId]
     * @return java.util.List<java.util.Map>
     **/
    String getTranslateProIds(String projectId);

    /**
     * @Author wanggang
     * @Description //获取机会客户是否存在
     * @Date 2022/4/26
     * @Param [map]
     * @return int
     **/
    List<Map> getCstIsOkReferral(Map map);
    List<Map> getCstIsOkComeVisit(Map map);
    List<Map> getCstIsOkTrade(Map map);

    /**
     * @Author wanggang
     * @Description //删除公池客户
     * @Date 16:18 2022/8/24
     * @Param [okOppIds]
     * @return void
     **/
    void delPublicOpps(@Param("list") List<String> okOppIds);

    /**
     * @Author luqianqian
     * @Description //判断当前登录人是否存在管理员调整客户状态权限
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    List<Map> getGlBatchUpdateCustomerStatusQx(@Param("userId") String userId);

    /**
     * @Author luqianqian
     * @Description //获取客户信息 以及项目区域所属
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    Map getCustomerProAndAreaAllInfo(@Param("opportunityClueId") String opportunityClueId);

    /**
     * @Author luqianqian
     * @Description //批量更新客户状态
     * @Date 10:53 2022/8/26
     * @Param [map]
     * @return void
     **/
    int updateBatchCustomerStatus(@Param("list") List<Map> list);

    /**
     * @author liang
     * @date 2023/10/18 10:11
     * @description: 导出用户以及权限查询
     */
    List<UserAuthority> getUserAuthorityAll();

    /**
     * @Author luqianqian
     * @Description //获取项目公司层级
     * @Date 10:20 2023/11/29
     * @Param [projectId]
     * @return java.lang.String
     **/
    String getProOrgParentLevel(String projectId);


    /**
     * 获取详细的重复客户
     * */
    /**
     * @Author wanggang
     * @Description //获取机会客户是否存在
     * @Date 2022/4/26
     * @Param [map]
     * @return int
     **/
    List<Map> getCstIsOkRepeat(Map map);

    /**
     * @Author luqianqian
     * @Description //获取数据权限细腻些
     * @Date 10:20 2023/11/29
     * @Param [projectId]
     * @return java.lang.String
     **/
    UserOrgRelForm getDataViewPremissionMainInfo(UserOrgRelForm userOrgRelForm);

    /**
     * 删除规则
     * */
    int delProjectRule(ProjectRuleDetail map);

    List<Map> getCstIsOkDisClues(Map map);

    /**
     * @Author wanggang
     * @Description //获取线索信息
     * @Date 22:29 2022/4/26
     * @Param [projectClueId]
     * @return cn.visolink.business.propertyconsultant.model.form.ReportCustomerForm
     **/
    ReportCustomerForm getProjectClue(String projectClueId);

    /**
     * 查询外展客户详情
     * @param projectClueId
     * @return
     */
    InformationVO getInformationInfo(@Param("projectClueId") String projectClueId,@Param("jobCode") String jobCode,@Param(value="isNameShow") String isNameShow,@Param(value="isMobileShow") String isMobileShow,@Param(value="proIsShowNameStr") String proIsShowNameStr,@Param(value="proIsShowMobileStr") String proIsShowMobileStr);

    /**
     * 新增线索表
     * @return 新增线索表
     * */
    int insertProjectClues(ReportCustomerForm reportCustomerForm);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 17:45 2022/5/13
     * @Param [opportunityClueId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getOppEnclosures2(String opportunityClueId);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 20:52 2022/4/26
     * @Param [param]
     * @return void
     **/
    void addCluesEnclosures(Map param);

    /**
     * @Author wanggang
     * @Description //更新线索信息
     * @Date 17:06 2022/5/3
     * @Param [oppMap]
     * @return void
     **/
    void updateClueCst(Map oppMap);

    /**
     * 常见问题台账
     * */
    List<FeedBackEc> getFeedAskCjList(FeedBackEc feedBackEc);

    /**
     * 常见问题详情
     * */
    FeedBackEc getFeedAskCjDetail(FeedBackEc feedBackEc);

    /**
     * 新增问题反馈
     * */
    int addFeedAskCj(FeedBackEc feedBackEc);

    /**
     * 编辑问题反馈
     * */
    int updateFeedAskCj(FeedBackEc feedBackEc);

    /**
     * 问题反馈台账
     * */
    List<FeedBackEc> getFeedBackEdList(FeedBackEc feedBackEc);

    /**
     * 报备链路
     * */
    List<Map> getReportList(@Param("list") List<ProjectCluesNew> list);

    /**
     * 获取节点移交
     * */
    List<Map> getFirstNodeMoveInfo(@Param("id") String id);
    List<Map> getMainNodeMoveInfo(@Param("id") String id);

    int getAllowedCount(@Param("userId") String userId);
    int getAllowedClueCount(@Param("userId") String userId,@Param("projectId") String projectId);

    List<String> getJobsByUserId(@Param("userId") String userId);

    List<OrganizationVO> getProOrgIds(@Param("projectIds") List<String> projectIds);

    void updateLocationError(@Param("businessId") String businessId);


    int insertProjectCluesCall(ReportCustomerForm reportCustomerForm);
    int insertProjectCluesCallTwo(Map map);

    int saveFollowUpRecordCall(FollowUpRecordForm followUpRecordForm);

    void saveInformationZCall(ReportCustomerForm reportCustomerForm);

    void addDimensionCall(ReportCustomerForm reportCustomerForm);


    List<Map> getCustIsOkInfo(Map map);

    int getKsReportCount(List<Map> custList);

    int getKsVisitCount(List<Map> custList);

    int getKsOrderCount(List<Map> custList);

    List<TaskDetailVO> getTaskAccountDetail(TaskQueryVO taskQueryVO);
    Map getProjectByOrgId(String id);

    Map getUserNameMobile(String id);

    List<Map> isRobotPermissions(Map map);

    /**
     * @Author luqianqian
     * @Description //获取跟进记录信息
     * @Date 15:00 2024/11/11
     * @Param [id]
     * @return cn.visolink.business.propertyconsultant.model.vo.FollowUpRecordVO
     **/
    FollowUpRecordVO getFollowUpRecordById(String followRecordId);

    /**
     * @Author luqianqian
     * @Description //获取跟进核验记录
     * @Date 15:00 2024/11/11
     * @Param [id]
     * @return cn.visolink.business.propertyconsultant.model.vo.FollowUpRecordVO
     **/
    FollowupVerificationRecordVo getFollowupVerificationRecord(FollowupVerificationRecordVo verificationVo);
    int getFollowupVerificationVersionNum(FollowupVerificationRecordVo verificationVo);

    /**
     * @Author luqianqian
     * @Description //保存核验记录
     * @Date 15:00 2024/11/11
     * @Param [id]
     * @return cn.visolink.business.propertyconsultant.model.vo.FollowUpRecordVO
     **/
    int saveFollowupVerificationRecord(FollowupVerificationRecordVo verificationVo);

    /**
     * @Author luqianqian
     * @Description //核验整改通过 修改跟进记录
     * @Date 15:00 2024/11/11
     * @Param [id]
     * @return cn.visolink.business.propertyconsultant.model.vo.FollowUpRecordVO
     **/
    int updateFollowUpRecordInsVrf(FollowupVerificationRecordVo verificationVo);

    /**
     * @Author luqianqian
     * @Description //跟进核验记录台账
     * @Date 15:00 2024/11/11
     * @Param [id]
     * @return cn.visolink.business.propertyconsultant.model.vo.FollowUpRecordVO
     **/
    List<FollowUpRecordVO> getFollowUpVerificationRecordList(FollowUpRecordVO followUpRecordVO);
    int getFollowUpRecordVerificationCount(FollowUpRecordVO followUpRecordVO);

    /**
     * @Author luqianqian
     * @Description //查询跟进核验整改完整记录
     * @Date 15:00 2024/11/11
     * @Param [id]
     * @return cn.visolink.business.propertyconsultant.model.vo.FollowUpRecordVO
     **/
    List<FollowupRectificationRecordVo> getFollowupVerificationAllRecordList(@Param("followVerificationRecordId") String followVerificationRecordId);

    /**
     * 根据跟进ID获取跟您核验记录
     */
    List<FollowupVerificationRecordVo> getFollowupVerificationRecordByFollowRecordId(@Param("followRecordId") String followRecordId);

    /**
     * 根据核验ID集合获取跟进核验整改记录
     */
    List<FollowupRectificationRecordVo> getFollowupRectificationRecordByFollowupVerificationRecordIds(@Param("ids")List<String> ids);

    /**
     * 获取客户信息用于客户等级变化记录使用
     * */
    Map getCustomerInfoInsCl(@Param("opportunityClueId") String opportunityClueId);

    /**
     * 保存客户等级变化记录
     * */
    int insertCustomerLevelChangeLog(CustomerLevelRecordVo customerLevelRecordVo);

    /**
     * 保存客户新增报备日志
     * */
    int saveCustomerAddLog(CustomerAddLogVo customerAddLogVo);

    /**
     * 保存客户新增跟进交易日志
     * */
    int saveCustomerFodLog(CustomerFodLogVo customerFodLogVo);

    /**
     * 设置历史客户的有效状态为无效
     * */
    int disableCutomerAddLog(CustomerAddLogVo customerAddLogVo);

    /**
     * 判断该客户历史是否报备过该项目
     * */
    String getCustomerAddLogToIsAdd(CustomerAddLogVo customerAddLogVo);

    /**
     * 修改客户跟进交易记录是否计入统计状态
     * */
    int updateCustomerFodRecordIsStatistics(@Param("followRecordId") String followRecordId,@Param("isStatistics") String isStatistics);

    /**
     * 获取客户的客户新增报备日志记录ID
     * */
    String getCustomerAddLogIdByOpportunityClueId(@Param("opportunityClueId") String opportunityClueId);

    /**
     * 获取招商团队下人员
     * */
    List<String> getTeamUser(@Param("orgId") String orgId);

    Integer getProjectProportion(TaskVo task1);

    /**
     * @Author luqianqian
     * @Description //批量调整客户过保及预警时间
     * @Date 17:44 2024/12/25
     * @Param [map]
     * @return void
     **/
    int updateBatchCustomerExpireDate(@Param("list") List<Map> list);

    /**
     * @Author luqianqian
     * @Description //保存客户变更节点记录
     * @Date 17:44 2024/12/25
     * @Param [oldMap]
     * @return void
     **/
    void addCustomerChangeNodeRecord(Map oldMap);

    /**
     * @Author luqianqian
     * @Description //批量更新客户最大跟进次数
     * @Date 17:44 2025/01/23
     * @Param [list]
     * @return int
     **/
    int updateBatchCustomerMaxFollowUp(@Param("list") List<Map> list);

    /**
     * @Author luqianqian
     * @Description //获取客户上一次分配人员
     * @Date 17:44 2025/01/23
     * @Param [oldMap]
     * @return void
     **/
    Map getCstLastDisClues(@Param("opportunityClueId") String opportunityClueId);

    /**
     * @Author luqianqian
     * @Description //保存重分配批次
     * @Date 18:19 2025/01/23
     * @Param [oldMap]
     * @return void
     **/
    int addRelCustomerBatch(Map customerDistributionBatch);

    /**
     * @Author luqianqian
     * @Description //批量保存分配记录
     * @Date 18:19 2025/01/23
     * @Param [oldMap]
     * @return void
     **/
    void addRelCustomerRecordBatch(@Param("list") List<Map> list);

    /**
     * 新增线索表
     * @return 新增线索表
     * */
    int insertProjectCluesBatch(@Param("list") List<ReportCustomerForm> list);

    /**
     * 保存客户新增报备日志
     * */
    int saveCustomerAddLogBatch(@Param("list") List<CustomerAddLogVo> list);

    /**
     * 设置历史客户的有效状态为无效
     * */
    int disableCutomerAddLogBatch(@Param("list") List<CustomerAddLogVo> list);

    /**
     * 保存客户等级变化记录
     * */
    int insertCustomerLevelChangeLogBatch(@Param("list") List<CustomerLevelRecordVo> list);

    /**
     * @Author luqianqian
     * @Description //更新线索信息
     * @Date 18:19 2025/01/23
     * @Param [list]
     * @return void
     **/
    void updateClueCstBatch(@Param("list") List<Map> list);

    /**
     * @Author luqianqian
     * @Description //TODO
     * @Date 18:19 2025/01/23
     * @Param [list]
     * @return void
     **/
    void addCluesEnclosuresBatch(@Param("list") List<Map> list);

    /**
     * @Author luqianqian
     * @Description //更新机会信息
     * @Date 18:19 2025/01/23
     * @Param [list]
     * @return void
     **/
    void updateOppCstBatch(@Param("list") List<Map> list);

    /**
     * @Author luqianqian
     * @Description //获取专员线索客户名称和联系方式
     * @Date 15:56 2025/01/26
     * @Param [oppMap]
     * @return void
     **/
    List<Map> getClueCstNMInfo(Map map);

    /**
     * @Author luqianqian
     * @Description //获取机会信息
     * @Date 15:56 2025/01/26
     * @Param [opportunityClueId]
     * @return java.util.Map
     **/
    List<Map> getOpportunityByIds(@Param("ids") List<String> ids);

    /**
     * @Author luqianqian
     * @Description //获取公客池机会信息
     * @Date 15:56 2025/01/26
     * @Param [opportunityClueId]
     * @return java.util.Map
     **/
    List<Map> getPublicPoolOpportunityByIds(@Param("ids") List<String> ids);

    /**
     * @Author luqianqian
     * @Description //查询专员的分配客户数量
     * @Date 15:56 2025/01/26
     * @Param [opportunityClueId]
     * @return java.util.Map
     **/
    List<Map> getUserAllowedClueCount(@Param("userIds") List<CustomerDistributionInfo> userId,@Param("projectId") String projectId);

    /**
     * @Author luqianqian
     * @Description //获取线索信息
     * @Date 15:56 2025/01/26
     * @Param [ids]
     * @return cn.visolink.business.propertyconsultant.model.form.ReportCustomerForm
     **/
    List<ReportCustomerForm> getProjectClueByIds(@Param("ids") List<String> ids);

    /**
     * 查询外展客户详情
     * @param ids
     * @return
     */
    List<InformationVO> getInformationInfoByIds(@Param("ids") List<String> ids,@Param("jobCode") String jobCode,@Param(value="isNameShow") String isNameShow,@Param(value="isMobileShow") String isMobileShow,@Param(value="proIsShowNameStr") String proIsShowNameStr,@Param(value="proIsShowMobileStr") String proIsShowMobileStr);

    /**
     * @Author luqianqian
     * @Description //TODO
     * @Date 15:56 2025/01/26
     * @Param [opportunityClueId]
     * @return java.util.List<java.lang.String>
     **/
    List<Map> getOppEnclosuresByIds(@Param("ids") List<String> ids);

    /**
     * @Author luqianqian
     * @Description //获取业务员组织
     * @Date 15:56 2025/01/26
     * @Param [param]
     * @return java.util.Map
     **/
    List<Map> getUserOrgByIds(Map param);

    /**
     * 公客池平均分配批次记录信息
     */
    List<CustomerDistributionRecordsVO> queryPublicPoolAverageRedistributionRecord(Map map);

    /**
     * 公客池平均分配详情记录信息
     */
    List<CustomerDistributionRecordsVO> queryPublicPoolAverageRedistributionDetailRecord(Map map);

    /**
     * @Author luqianqian
     * @Description //获取可分配的项目
     * @Date 10:07 2025/01/27
     * @Param [comGUID]
     * @return java.util.List<java.lang.String>
     **/
    List<Map> getGlAllocationPropertyConsultantPro(Map map);

    String queryPublicPoolAverageRedistributionInfo(Map map);

	List<TaskCustomer> getCustomerByTaskId(String taskIds);

    /**
     * 根据父任务ID列表获取子任务
     * @param parentIds 父任务ID列表，格式为 '1','2','3'
     * @return 子任务列表
     */
    List<TaskVo> getChildTasksByParentIds(String parentIds);

    /**
     * 获取客户来访信息
     * @param paramMap 参数
     * @return 来访信息
     */
    Map<String, String> getCustomerVisitInfo(Map<String, Object> paramMap);

    /**
     * 获取客户成交信息
     * @param paramMap 参数
     * @return 成交信息
     */
    Map<String, String> getCustomerDealInfo(Map<String, Object> paramMap);

    /**
     * 获取客户三个一信息
     * @param paramMap 参数
     * @return 三个一信息
     */
    Map<String, String> getCustomerThreeOneInfo(Map<String, Object> paramMap);

    /**
     * 获取线索拜访信息
     * @param paramMap 参数
     * @return 拜访信息
     */
    Map<String, String> getClueVisitInfo(Map<String, Object> paramMap);

    /**
     * 批量更新线索表地址信息
     * @param updateList 更新列表
     * @return 更新数量
     */
    int batchUpdateCluesAddress(@Param("list") List<Map<String, Object>> updateList);

    /**
     * 批量更新机会表地址信息
     * @param updateList 更新列表
     * @return 更新数量
     */
    int batchUpdateOpportunityAddress(@Param("list") List<Map<String, Object>> updateList);
}
