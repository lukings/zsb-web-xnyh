package cn.visolink.system.channel.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.channel.model.*;
import cn.visolink.system.channel.model.form.*;
import cn.visolink.system.channel.model.vo.*;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ProjectClues服务类
 * </p>
 *
 * @author 吴要光
 * @since 2019-08-24
 */
public interface ProjectCluesService extends IService<ProjectClues> {
    /**
     *线索
     * @param projectCluesForm 查询请求条件
     * @return 信息列表
     */
    PageInfo<ProjectCluesNew> channelProjectClues(Map projectCluesForm);

    /**
     *线索-个人
     * @param projectCluesForm 查询请求条件
     * @return 信息列表
     */
    PageInfo<ProjectCluesNew> channelProjectCluesByUser(Map projectCluesForm);

    /**
     * @Author wmy
     * @Description //渠道信息导出
     * @Date 16:41 2022/9/9
     * @Param [request, response, excelForm]
     * @return void
     **/
    void channelProjectCluesExport(HttpServletRequest request, HttpServletResponse response, String excelForm);
    String channelProjectCluesExportNew(HttpServletRequest request, HttpServletResponse response, String excelForm);

    /**
     * @Author wmy
     * @Description //渠道信息导出-个人
     * @Date 16:41 2022/9/9
     * @Param [request, response, excelForm]
     * @return void
     **/
    void channelProjectCluesByUserExport(HttpServletRequest request, HttpServletResponse response, String excelForm);

    /**
     *机会
     * @param projectCluesForm 查询请求条件
     * @return 信息列表
     */
    PageInfo<ProjectCluesNew> courtCase(ExcelForm projectCluesForm) throws ParseException;

    /**
     *机会-个人
     * @param projectCluesForm 查询请求条件
     * @return 信息列表
     */
    PageInfo<ProjectCluesNew> courtCaseByUser(ExcelForm projectCluesForm) throws ParseException;

    /**
     * @Author luqianqian
     * @Description //报备客户台账导出方式检定
     * @Date 16:41 2024/5/9
     * @Param [request, response, excelForm]
     * @return void
     **/
    ResultBody getCourtCaseIsExport(ExcelForm projectCluesForm) throws ParseException;
    /**
     * @Author wmy
     * @Description //案场信息导出
     * @Date 16:41 2022/9/9
     * @Param [request, response, excelForm]
     * @return void
     **/
    void courtCaseExport(HttpServletRequest request, HttpServletResponse response, ExcelForm projectCluesForm) throws ParseException;
    
    /**
     * @Author wmy
     * @Description //案场信息导出-不导出链路数据
     * @Date 16:41 2022/9/9
     * @Param [request, response, excelForm]
     * @return void
     **/
    void courtCaseExportNoLink(HttpServletRequest request, HttpServletResponse response, ExcelForm projectCluesForm) throws ParseException;
    String courtCaseExportNew(HttpServletRequest request, HttpServletResponse response, ExcelForm projectCluesForm) throws ParseException;

    /**
     * @Author wmy
     * @Description //案场信息导出-个人
     * @Date 16:41 2022/9/9
     * @Param [request, response, excelForm]
     * @return void
     **/
    void courtCaseByUserExport(HttpServletRequest request, HttpServletResponse response, ExcelForm projectCluesForm) throws ParseException;

    /**
     * @Author wmy
     * @Description //获取放弃记录
     * @Date 21:05 2022/9/5
     * @Param [map]
     * @return cn.visolink.system.channel.model.vo.CustomerDistributionRecordsVO
     **/
    PageInfo<CustomerDistributionRecordsVO> getAbandonRecord(CustomerDistributionRecords customerDistributionRecords);

    /**
     * @Author wmy
     * @Description //放弃记录导出
     * @Date 16:41 2022/9/9
     * @Param [request, response, customerDistributionRecords]
     * @return void
     **/
    void abandonRecordExport(HttpServletRequest request, HttpServletResponse response,
                             CustomerDistributionRecords customerDistributionRecords);
    String abandonRecordExportNew(HttpServletRequest request, HttpServletResponse response,
                             CustomerDistributionRecords customerDistributionRecords);

    /**
     * 查询规则
     * @param projectProtectRuleForm 查询请求条件
     * @return 信息列表
     */
    ProjectProtectRuleVO selectRuleInfo(ProjectProtectRuleForm projectProtectRuleForm);

    /**
    * 查询渠道的代理公司的规则
    *
    */
    List<ProjectProtectRuleVO> selectRuleCompany(ProjectProtectRuleForm projectProtectRuleForm);

    /**
     * 规则操作
     * ProjectProtectRuleForm projectProtectRuleForm
     * @param
     * @return
     */
    Map updateChannelRule(RuleList te);

    /**
     * 基本信息
     * */
    ProjectCluesNew essentialInformation(Map map);

    List<Map> associatedCustomers(Map map);

    List<Map> nodeRecord(Map map);

    List<Map> dealRecord(Map map);

    List<Map> clueNodeRecord(Map map);

    /**
     * 跟进记录
     * */
    List<Map> followUpRecord(Map map);

    /**
     * 转介记录
     * */
    List<Map> toMoveRecord(Map map);

    /**
     * 首访问卷
     * */
    Map firstInterviewQuestionnaire(Map map);

    /**
     *查询重分配的项目可以分配的状态
     * */
    Map currProjectInfoSelect(String projectId);

    /**
     *查询项目符合不符合重分配线索
     * */
    Map SelectClueConditionClue(Map map);

    /**
     * 查询项目符合不符合重分配案场
     * */
    Map SelectClueConditionAC(Map map);

    /**
     * 交易信息
     * */
    List<Map> transactionInformation(Map map);

    /**
     * 渠道数据导出
     * */
    void channelExport(HttpServletRequest request, HttpServletResponse response, String excelForm);

    /**
     *客户明细台账数据
     * @param projectCluesForm 查询请求条件
     * @return 信息列表
     */
    PageInfo<ProjectCluesVO> queryAllCustmerDetails(ProjectCluesForm projectCluesForm);

    /**
     * 查询认知渠道
     * @param map
     * @return
     */
    List<ResultProjectVO> getMainMediaList(Map map);

    /**
     * 判断是否有无报备保护期内客户
     * @param map
     * @return
     */
    Map getIsReport(Map map);

    /**
     * 判断是否有无报备保护期内客户(多个客户)
     * @param map
     * @return
     */
    Map getIsReportList(Map map);

    /**
     * 查询认知渠道
     * @return
     */
    List<Map> getMainList(Map map);

    /**
     * 置业顾问名片台账
     * @param map
     * @return
     */
    ResultBody  getCardStandingBookList(Map map);

    /**
     * 导出置业顾问名片台账
     * @param request
     * @param response
     * @param param
     */
    void cardStandingBookExport(HttpServletRequest request, HttpServletResponse response, String param);
    /**
     * @Author wanggang
     * @Description //异步导出
     * @Date 14:45 2020/9/1
     * @Param [excelForm]
     * @return java.lang.String
     **/
    String channelExportNew(String excelForm);

    /***
    *
     * @param projectCluesForm
    *@return {}
    *@throws
    *@Description: 查询全部渠道信息
    *@author FuYong
    *@date 2020/9/9 15:58
    */
    List<ProjectCluesVO> getAllChannel(ProjectCluesForm projectCluesForm);

    /***
    *
     * @param projectCluesForm
    *@return {}
    *@throws
    *@Description: 查询全部案场信息
    *@author FuYong
    *@date 2020/9/9 15:58
    */
    List<ProjectCluesVO> getAllCase(ProjectCluesForm projectCluesForm);
    /**
     * @Author wanggang
     * @Description //查询交易详情
     * @Date 16:35 2021/5/10
     * @Param [map]
     * @return java.util.Map
     **/
    Map getDealRecord(HttpServletRequest request,Map map);
    /**
     * @Author wanggang
     * @Description //获取中介公司
     * @Date 14:28 2021/5/17
     * @Param []
     * @return java.util.List<cn.visolink.system.channel.model.form.Supplier>
     **/
    List<Supplier> getSupplierList();
    /**
     * @Author wanggang
     * @Description //保存/引入中介门店
     * @Date 15:34 2021/5/17
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addIntermediaryStores(IntermediaryStores map);
    /**
     * @Author wanggang
     * @Description //更新中介规则状态
     * @Date 15:34 2021/5/17
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateIntermediaryRule(IntermediaryStores map);
    /**
     * @Author wanggang
     * @Description //获取中介门店
     * @Date 9:18 2021/5/18
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getIntermediaryList();
    /**
     * @Author wanggang
     * @Description //获取线索转机会记录
     * @Date 9:15 2022/3/30
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getClueReferral(ClueReferralForm map);
    /**
     * @Author wanggang
     * @Description //查询规则
     * @Date 8:56 2022/4/14
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectProjectRule(Map map);

    /**
     * @Author wanggang
     * @Description //查询规则
     * @Date 8:56 2022/4/14
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectProjectRuleByProjectId(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 9:33 2022/4/14
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addOrEditProjectRule(ProjectRuleDetail map);
    /**
     * @Author wanggang
     * @Description //删除规则
     * @Date 9:33 2022/4/14
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody deleteProjectRule(ProjectRuleDetail map);
    /**
     * @Author wmy
     * @Description //机会转介记录
     * @Date 14:04 2022/9/7
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody toOppMoveRecord(Map map);

    /**
     * @Author wanggang
     * @Description //线索转介记录
     * @Date 14:04 2022/4/14
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectReferralClue(Map map);
    /**
     * @Author wmy
     * @Description //机会转介导出
     * @Date 15:32 2022/4/14
     * @Param [request, response, map]
     * @return void
     **/
    void toOppMoveRecordExport(HttpServletRequest request, HttpServletResponse response, Map map);
    String toOppMoveRecordExportNew(HttpServletRequest request, HttpServletResponse response, Map map);
    /**
     * @Author wanggang
     * @Description //线索转介导出
     * @Date 15:32 2022/4/14
     * @Param [request, response, map]
     * @return void
     **/
    void selectReferralClueExport(HttpServletRequest request, HttpServletResponse response, Map map);
    /**
     * @Author wanggang
     * @Description //机会跟进记录
     * @Date 17:25 2022/4/16
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> followUpOppRecord(Map map);
    /**
     * @Author wanggang
     * @Description //获取机会信息
     * @Date 21:05 2022/4/16
     * @Param [map]
     * @return cn.visolink.system.channel.model.vo.OppInformation
     **/
    OppInformation oppInformation(Map map);

    InformationVO cluesInformation(Map map);

    /**
     * @Author wmy
     * @Description //编辑客户等级
     * @Date 21:05 2022/9/5
     * @Param [map]
     * @return RequestBody
     **/
    ResultBody updateCustomerGrade(ExcelForm projectCluesForm);

    /**
     * @Author wmy
     * @Description //申诉记录
     * @Date 21:05 2022/9/6
     * @Param [Appeal]
     * @return RequestBody
     **/
    ResultBody getAppealRecord(Appeal appeal);
    /**
     * @Author wmy
     * @Description //申诉记录导出
     * @Date 21:05 2022/9/9
     * @Param [appeal]
     * @return void
     **/
    void AppealRecordExport(HttpServletRequest request, HttpServletResponse response, Appeal appeal);

    /**
     * @Author wmy
     * @Description //任务管理台账
     * @Date 21:05 2022/9/6
     * @Param [Task]
     * @return RequestBody
     **/
    List<TaskVo> getTaskAccount(Task task);

    /**
     * @Author wmy
     * @Description //任务管理台账导出
     * @Date 21:05 2022/9/9
     * @Param [task]
     * @return void
     **/
    void taskAccountExport(HttpServletRequest request, HttpServletResponse response, Task task) throws ParseException;
    String taskAccountExportNew(HttpServletRequest request, HttpServletResponse response, Task task) throws ParseException;

    /**
     * @Author wmy
     * @Description //报备失败台账
     * @Date 21:05 2022/9/6
     * @Param [ReportFail]
     * @return RequestBody
     **/
    ResultBody getReportFailAccount(ReportFail reportFail);

    /**
     * @Author wmy
     * @Description //报备失败台账导出
     * @Date 21:05 2022/9/9
     * @Param [task]
     * @return void
     **/
    void reportFailAccountExport(HttpServletRequest request, HttpServletResponse response, ReportFail reportFail);
    String reportFailAccountExportNew(HttpServletRequest request, HttpServletResponse response, ReportFail reportFail);

    /**
     * @Author wanggang
     * @Description //获取申诉详情
     * @Date 14:43 2022/9/4
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getAppealDetail(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 10:20 2022/10/25
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getCstSource();

    ResultBody getCstIndustryOneNew();
    /**
     * @Author wanggang
     * @Description //获取业务员
     * @Date 21:21 2022/5/6
     * @Param [queryConditionForm]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProZsSales(Map map);
    /**
     * 分配置业顾问
     * @param salesAttributionForm
     * @return
     */
    ResultBody allocationPropertyConsultant(SalesAttributionForm salesAttributionForm);

    /**
     * @Author luqianqian
     * @Description //获取人员数据查看权限下的小组
     * @Date 11:50 2022/5/19
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getDataViewPremissionOrgTeam(UserOrgRelForm map);
    ResultBody getOrgTeam(UserOrgRelForm map);
    /**
     * @Author luqianqian
     * @Description //新增数据权限查看审批
     * @Date 10:30 2022/5/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody startDataViewPremission(List<UserOrgRelForm> list);
    /**
     * @Author luqianqian
     * @Description //获取数据权限查看列表
     * @Date 10:35 2022/5/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getDataViewPremissionList(Map map);
    /**
     * @Author luqianqian
     * @Description //获取数据权限查看详情
     * @Date 11:50 2022/5/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getDataViewPremissionDetail(Map map);

    /**
     * @Author luqianqian
     * @Description //获取数据权限查看详情
     * @Date 11:50 2022/5/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getDataViewPremission(UserOrgRelForm map);
    /**
     * @Author luqianqian
     * @Description //获取数据权限查看详情-招商地体包含专员
     * @Date 11:50 2022/5/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getDataViewPremissionZs(UserOrgRelForm map);
    /**
     * @Author luqianqian
     * @Description //新增数据权限查看审批
     * @Date 10:30 2022/5/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody adminSetDataViewPremission(List<UserOrgRelForm> list);
    /**
     * @Author luqianqian
     * @Description //获取数据权限查看列表
     * @Date 10:35 2022/5/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getAllDataViewPremissionInfo(UserOrgRelForm map);

    /**
     * @Author wmy
     * @Description //任务管理台账
     * @Date 21:05 2022/9/6
     * @Param [Task]
     * @return RequestBody
     **/
    ResultBody getFollowUpRecordList(FollowUpRecordVO followUpRecordVO) throws ParseException;

    /**
     * @Author wmy
     * @Description //任务管理台账导出
     * @Date 21:05 2022/9/9
     * @Param [task]
     * @return void
     **/
    void getFollowUpRecordListExport(HttpServletRequest request, HttpServletResponse response, FollowUpRecordVO followUpRecordVO) throws ParseException;
    String getFollowUpRecordListExportNew(HttpServletRequest request, HttpServletResponse response, FollowUpRecordVO followUpRecordVO) throws ParseException;

    /**
     * @Author luqianqian
     * @Description //TODO
     * @Date 10:20 2022/10/25
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getCommonDict(String parentCode);

    /**
     * 发起跟进获取当前操作人项目下可跟进最高岗位
     * */
    ResultBody getMaxProJobInsFollowUper(Map map);

    /**
     * 获取登录人权限内可分配客户的置业顾问
     * */
    ResultBody getGlAllocationPropertyConsultantZygw(Map map);

    /**
     * @Author luqianqian
     * @Description //分配置业顾问新
     * @Date 15:00 2023/10/16
     * @Param [salesAttributionForm]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody allocationPropertyConsultantNew(SalesAttributionForm salesAttributionForm);

    /**
     * @Author luqianqian
     * @Description //客户转移
     * @Date 12:21 2025/03/24
     * @Param [salesAttributionForm]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody transferPropertyConsultant(SalesAttributionForm salesAttributionForm);

    /**
     * @Author luqianqian
     * @Description //公池重分配新
     * @Date 15:00 2023/10/16
     * @Param [salesAttributionForm]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody publicPoolDistributionNew(SalesAttributionForm salesAttributionForm);

    /**
     * @Author luqianqian
     * @Description //批量调整客户状态
     * @Date 15:00 2023/10/11
     * @Param [salesAttributionForm]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateBatchCustomerStatus(ExcelForm projectCluesForm);

    /**
     * @author liang
     * @date 2023/10/16 14:25
     * @description: 导出规则配置
     */
    void ruleConfigurationExport(HttpServletResponse response);

    /**
     * @author liang
     * @date 2023/10/18 10:11
     * @description: 导出用户以及权限
     */
    void ExportUserAuthorityAll(HttpServletResponse response);

    /**
     * 常见问题台账
     * */
    ResultBody getFeedAskCjList(FeedBackEc feedBackEc);

    /**
     * 常见问题详情
     * */
    ResultBody getFeedAskCjDetail(FeedBackEc feedBackEc);

    /**
     * 新增编辑常见问题
     * */
    ResultBody addOrEditFeedAskCj(FeedBackEc feedBackEc);
    /**
     * 问题反馈台账导出
     * */
    void feedBackEdExcel(HttpServletRequest request, HttpServletResponse response,FeedBackEc feedBackEc);
    /**
     * 校验当前用户是否需要限制权限查询
     * @param jobs
     * @return
     */
    Boolean checkJobs(List<String> jobs);

    void operation();

    ResultBody callTurnTheClue(ReportCustomerForm reportCustomerForm);

    ResultBody callTurnTheClueRobot(List<Map> reportCustomerForm);


    PageInfo<TaskDetailVO> getTaskAccountDetail(TaskQueryVO taskQueryVO);


    ResultBody getGlAllocationPropertyConsultantZygwCall(Map map);

    ResultBody isRobotPermissions(Map map);

    /**
     * 保存跟进核验记录
     * @param verificationVo
     * @return
     */
    ResultBody saveFollowupVerificationRecord(FollowupVerificationRecordVo verificationVo);

    /**
     * 核验记录台账
     * @param followUpRecordVO
     * @return
     */
    ResultBody getFollowupVerificationRecordList(FollowUpRecordVO followUpRecordVO);

    /**
     * 核验记录台账导出
     * @param followUpRecordVO
     * @return
     */
    void getFollowupVerificationRecordListExport(HttpServletRequest request, HttpServletResponse response, FollowUpRecordVO followUpRecordVO);

    /**
     * 查询跟进核验记录
     * @param followRecordId
     * @return
     */
    ResultBody getFollowupVerificationRecordOnTab(String followRecordId);

    /**
     * 查询跟进核验整改记录
     * @param followVerificationRecordId
     * @return
     */
    ResultBody getFollowupRectificationRecordOnTab(String followVerificationRecordId);

    /**
     * @Author luqianqian
     * @Description //批量调整客户过保及预警时间
     * @Date 17:00 2024/12/25
     * @Param [salesAttributionForm]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateBatchCustomerExpireDate(ExcelForm projectCluesForm);

    /**
     * @Author luqianqian
     * @Description //批量调整客户过保及预警时间（支持每个客户单独设置）
     * @Date 17:00 2024/12/25
     * @Param [batchUpdateCustomerExpireForm]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateBatchCustomerExpireDateByDays(BatchUpdateCustomerExpireForm batchUpdateCustomerExpireForm);

    /**
     * @Author luqianqian
     * @Description //批量设置客户最大跟进次数
     * @Date 17:00 2025/01/23
     * @Param [projectCluesForm]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateBatchCustomerMaxFollowUp(ExcelForm projectCluesForm);

    /**
     * 管理员-解锁客户每日访问上线
     * */
    ResultBody unlockInterfaceLimit(String userName);

    /**
     * @Author luqianqian
     * @Description //公池客户平均重分配
     * @Date 14:20 2025/01/23
     * @Param [salesAttributionForm]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody publicPoolAverageDistribution(SalesAttributionForm salesAttributionForm);


    /**
     * 公客池平均分配记录
     * */
    PageInfo queryPublicPoolAverageRedistributionRecord(Map map);

    /**
     * 获取登录人权限内可分配客户的项目
     * */
    ResultBody getGlAllocationPropertyConsultantPro(Map map);

    /**
     * 获取登录人权限内可分配客户的项目下的置业顾问
     * */
    ResultBody getGlAllocationPropertyConsultantProZygw(Map map);

    /**
     * 外呼系统获取客户保护期
     * */
    ResultBody getCallProjectRule(Map map);
}
