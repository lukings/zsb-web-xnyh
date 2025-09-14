package cn.visolink.system.excel.mapper;

import cn.visolink.message.model.SysLog;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.parameter.model.vo.DictionaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.26
 */
@Repository
@Mapper
public interface ExcelImportMapper {

    /**
     * 校验项目是否存在
     */
    int checkProjectName(@Param("projectName") String projectName);

    /**
     * 校验业务员是否存在
     */
    int checkUserName(@Param("userName") String userName);

    /**
     * 校验中介公司是否存在
     */
    int checkCompany(@Param("companyName") String companyName);

    /**
     * 保存数据
     */
    int saveImportData(Map map);

    /**
     * 获取导入数据
     */
    List<Map> getAllImportData();

    /**
     * 查询地图导入客户历史记录
     */
    List<Map> queryMapImportCustomerHistory(Map<String, Object> paramMap);

    /**
     * 根据用户ID删除跟进记录
     */
    int deleteFollowupRecordByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID删除项目线索
     */
    int deleteProjectCluesByUserId(@Param("userId") String userId);

    /**
     * 检查项目线索状态
     */
    List<Map> checkProjectCluesStatus(@Param("userId") String userId);

    /**
     * 检查任务客户关联
     */
    List<Map> checkTaskCustomerRelation(@Param("userId") String userId);

    /**
     * 更新定时任务日志状态
     */
    int updateTimeTaskLogStatus(@Param("historyId") String historyId, @Param("resultStatus") int resultStatus);

    /**
     * 清空临时表
     */
    int delImportData();

    /**
     * 保存错误信息
     */
    int saveError(Map map);

    /**
     * 校验在线索表是否存在数据
     */

    Map checkProjectClue(@Param("mobile") String mobile, @Param("projectName") String projectName);

    /**
     * 获取项目
     */
    String getProjectId(@Param("projectName") String projectName);


    /**
     * 新增客户
     */
    int insertCustomerBasic(Map map);

    /**
     * 获取用户信息
     */
    Map getUserInfo(@Param("userName") String userName);

    /**
     * 获取用户信息
     */
    Map getForUserInfo(@Param("userName") String userName);

    /**
     * 新增线索
     */
    int insertClue(Map map);

    /**
     * 覆盖线索
     */
    int overiedClues(Map map);

    /**
     * 覆盖机会
     */
    int overiedOpp(Map map);

    /**
     * 新增机会
     */
    int insertOpp(Map map);

    /**
     * 新增首访信息
     */
    int saveInformation(Map map);


    /**
     * 获取中介项目保护规则
     *
     * @return 获取项目保护规则
     */

    Map getZjProjectProtect(@Param("projectId") String projectId, @Param("companyId") String companyId);


    /**
     * 获取自渠项目规则
     */
    Map getZqProjectProtect(@Param("projectId") String projectId);

    /**
     * 获取案场项目规则
     */

    Map getAcProjectProtect(@Param("projectId") String projectId);

    /**
     * 删除临时表数据
     *
     */
    int delTempData(@Param("id") String id);


    /**
     * 导入组织数据
     *
     */
    int saveOrgData(List<Map> list);

    /**
     * 保存岗位数据
     *
     */
    int saveJobData(List<Map> list);

    /**
     * 保存项目数据
     *
     */
    int saveProjectData(List<Map> list);

    /**
     * @return void
     * @Author wanggang
     * @Description //添加导出记录
     * @Date 18:23 2020/8/21
     * @Param [excelExportLog]
     **/
    void addExcelExportLog(ExcelExportLog excelExportLog);

    /***
     * 更新字段使用次数
     * */
    int updateDiyCode(String codeId);

    /**
     * @return java.util.List<cn.visolink.system.excel.model.ExcelExportLog>
     * @Author wanggang
     * @Description //查询导出记录
     * @Date 18:24 2020/8/21
     * @Param [excelExportLog]
     **/
    List<ExcelExportLog> getExcelExportLog(ExcelExportLog excelExportLog);

    /**
     * @return void
     * @Author wanggang
     * @Description //更新导出记录
     * @Date 18:26 2020/8/21
     * @Param [excelExportLog]
     **/
    void updateExcelExportLog(ExcelExportLog excelExportLog);

    /**
     * @return java.util.List<cn.visolink.system.excel.model.ExcelExportLog>
     * @Author wanggang
     * @Description //查询登录人任务
     * @Date 9:54 2020/8/25
     * @Param [creator]
     **/
    List<ExcelExportLog> getExcelExportDownList(String creator);

    /**
     * @return java.util.Map
     * @Author wanggang
     * @Description //获取项目事业部，项目名称
     * @Date 17:29 2020/8/25
     * @Param [list]
     **/
    Map getAreaNameAndProNames(@Param("list") List<String> list);

    /**
     * @return java.util.List<cn.visolink.system.parameter.model.vo.DictionaryVO>
     * @Author wanggang
     * @Description //TODO
     * @Date 11:18 2020/9/2
     * @Param [map]
     **/
    List<DictionaryVO> getDictionaryList(String pid);

    /**
     * @return int
     * @Author wanggang
     * @Description //查询任务是否存在
     * @Date 11:39 2020/9/7
     * @Param [excelExportLog]
     **/
    int getExcelExportDownIsExist(ExcelExportLog excelExportLog);

    /**
     * @return void
     * @Author wanggang
     * @Description //插入线索扩展表
     * @Date 10:51 2021/6/22
     * @Param [map]
     **/
    void insertCluesExc(Map map);

    /**
     * @return void
     * @Author wanggang
     * @Description //导入人员
     * @Date 17:50 2021/10/13
     * @Param [paramsList]
     **/
    void saveUserData(List<Map> list);

    /**
     * @return int
     * @Author wanggang
     * @Description //查询是否存在此账号
     * @Date 13:57 2021/10/14
     * @Param [alias]
     **/
    int getAlias(String alias);

    /**
     * @return void
     * @Author wanggang
     * @Description //保存户型
     * @Date 18:34 2021/11/22
     * @Param [paramsList]
     **/
    void saveHxData(@Param("list") List<Map> paramsList);

    /**
     * @return void
     * @Author wanggang
     * @Description //保存房间
     * @Date 18:35 2021/11/22
     * @Param [paramsList]
     **/
    void saveRoomData(@Param("list") List<Map> paramsList);

    /**
     * @return void
     * @Author wanggang
     * @Description //保存楼栋
     * @Date 18:36 2021/11/22
     * @Param [paramsList]
     **/
    void saveBuildData(@Param("list") List<Map> paramsList);

    /**
     * @return void
     * @Author wanggang
     * @Description //保存单元
     * @Date 18:36 2021/11/22
     * @Param [paramsList]
     **/
    void saveUnitData(@Param("list") List<Map> paramsList);

    /**
     * @return int
     * @Author wanggang
     * @Description //查询项目是否已上线
     * @Date 11:08 2022/9/7
     * @Param [projectId]
     **/
    int getProIsUp(String projectId);

    /**
     * @return String
     * @Author wanggang
     * @Description //获取团队组织是否创建
     * @Date 11:30 2022/9/7
     * @Param [projectId, teamName]
     **/
    String getProTeam(@Param("projectId") String projectId, @Param("teamName") String teamName);

    /**
     * @return void
     * @Author wanggang
     * @Description //保存项目团队组织
     * @Date 13:33 2022/9/7
     * @Param [orgMap]
     **/
    void addProTeamOrg(Map orgMap);

    /**
     * @return java.util.List<java.util.Map>
     * @Author wanggang
     * @Description //获取招商专员-经理通用岗ID
     * @Date 13:43 2022/9/7
     * @Param []
     **/
    List<Map> getComJobId();

    /**
     * @return void
     * @Author wanggang
     * @Description //添加团队岗位
     * @Date 13:54 2022/9/7
     * @Param [jobMap]
     **/
    void saveProTeamJob(Map jobMap);

    /**
     * @return java.util.List<java.util.Map>
     * @Author wanggang
     * @Description //获取组织下岗位
     * @Date 13:59 2022/9/7
     * @Param [teamOrgId]
     **/
    List<Map> getTeamJobs(String teamOrgId);

    /**
     * @return java.lang.String
     * @Author wanggang
     * @Description //获取用户ID
     * @Date 14:07 2022/9/7
     * @Param [alias]
     **/
    String getUserId(String alias);

    /**
     * @return int
     * @Author wanggang
     * @Description //获取用户岗位是否已存在
     * @Date 14:14 2022/9/7
     * @Param [accountId, jobId]
     **/
    int getUserJobIsOk(@Param("accountId") String accountId, @Param("jobId") String jobId);

    /**
     * @return void
     * @Author wanggang
     * @Description //保存用户岗位
     * @Date 14:28 2022/9/7
     * @Param [paramMap]
     **/
    void addUserJob(Map paramMap);

    /**
     * @return java.util.Map
     * @Author wanggang
     * @Description //获取组织信息
     * @Date 14:43 2022/9/8
     * @Param [areaId]
     **/
    Map getAreaOrg(String areaId);

    /**
     * @return void
     * @Author wanggang
     * @Description //保存项目组织
     * @Date 15:16 2022/9/8
     * @Param [newProOrgMap]
     **/
    void addProOrg(Map newProOrgMap);

    /**
     * @return void
     * @Author wanggang
     * @Description //保存区域项目
     * @Date 15:16 2022/9/8
     * @Param [newProMap]
     **/
    void addPronew(Map newProMap);

    /**
     * @return java.lang.String
     * @Author wanggang
     * @Description //获取区域名称
     * @Date 8:42 2022/9/14
     * @Param [regionList]
     **/
    String getAreaNames(@Param("list") List<String> regionList);

    /**
     * 新增线索表
     *
     * @return 新增线索表
     *
     */
    int insertProjectClues(@Param("list") List<ReportCustomerForm> reportCustomerFormList);

    /**
     * 新增跟进记录（节点记录）
     *
     * @return 新增跟进记录（节点记录）
     *
     */
    int saveFollowNodeUpRecord(@Param("list") List<ReportCustomerForm> reportCustomerFormList);

    /**
     * @return void
     * @Author wanggang
     * @Description //新增标注信息
     * @Date 18:22 2022/8/18
     * @Param [reportCustomerForm]
     **/
    void addDimension(@Param("list") List<ReportCustomerForm> reportCustomerFormList);

    /**
     * @return void
     * @Author wanggang
     * @Description //TODO
     * @Date 17:46 2022/8/18
     * @Param [reportCustomerForm]
     **/
    void saveInformationZ(@Param("list") List<ReportCustomerForm> reportCustomerFormList);

    /**
     * 获取行业分类字典
     *
     */
    Map getDictParentHyfl(Map map);

    Map getDictChildHtzl(Map map);

    /**
     * @Description //查询项目
     **/
    Map getProInfo(Map map);

    /**
     * @Description //查询人员权限信息
     **/
    Map getUserOrgInfo(Map map);

    /**
     * @Description //清空线索客户标注临时表信息
     **/
    int delProhectClueMarkTemp(@Param("userId") String userId);

    /**
     * @Description //保存线索客户标注临时表信息
     **/
    int saveProhectClueMarkTemp(@Param("list") List<ReportCustomerForm> reportCustomerFormList, @Param("userId") String userId);

    /**
     * @Description //查询线索客户标注临时表信息
     **/
    List<ReportCustomerForm> getProhectClueMarkTempList(ReportCustomerForm reportCustomerForm);

    /**
     * @Description //保存临时标注信息到正式表
     **/
    int saveMarkTempToProjectClue(@Param("userId") String userId);

    /**
     * @Description //保存临时标注信息到正式表
     **/
    int saveMarkTempToProjectClueA(@Param("userId") String userId);

    /**
     * @Description //保存临时标注信息到正式表
     **/
    int saveMarkTempToProjectClueB(@Param("userId") String userId);

    /**
     * @Description //保存临时标注信息到正式表
     **/
    int saveMarkTempToProjectClueC(@Param("userId") String userId);

    /**
     * @Description //保存临时标注信息到正式表
     **/
    int saveMarkTempToProjectClueD(@Param("userId") String userId);

    /**
     * @Description //保存临时标注信息到正式表
     **/
    int saveMarkTempToProjectClueE(@Param("userId") String userId);

    /**
     * /**
     * /**
     * /**
     * 保存日志
     */
    void insertLogs(SysLog sysLog);

    /**
     * /**
     * /**
     * /**
     * 保存日志
     */
    void insertLogsWithID(SysLog sysLog);

    /**
     * 根据组织名称获取对应项目信息
     *
     */
    Map getProInfoByorgName(String orgName);

    /**
     * 根据岗位编码获取岗位信息
     *
     */
    Map getComJobInfoByJobCode(String qycsss);

    /**
     * 获取项目下是否存在岗位
     *
     */
    Map getJobInfoInsProject(Map paramsMap);

    /**
     * 根据用户名称 获取用户信息
     *
     */
    Map getUserInfoByUserName(String userName);


    /**
     * 根据用户名称 获取用户信息
     *
     */
    List<Map<String, Object>> getUserInfoSByUserName(String userName);

    /**
     * 获取人员是否引入岗位
     *
     */
    Map getUserJobrelInfo(String userId, String jobId);

    /**
     * 创建岗位
     *
     */
    int saveSystemJobForManagement(Map map);

    /**
     * 保存岗位人员关系
     *
     */
    int saveJobSuserrel(Map map);

    /**
     * @return cn.visolink.domain.master.excel.ExcelExportLog
     * @Author wanggang
     * @Description //查询任务详情
     * @Date 16:23 2020/8/26
     * @Param [id]
     **/
    List<ExcelExportLog> getExcelLog();

    /**
     * @return void
     * @Author wanggang
     * @Description //更新状态
     * @Date 16:44 2020/8/26
     * @Param [excelExportLog]
     **/
    void updateExcelStatus(ExcelExportLog excelExportLog);

    /**
     * @return java.util.List<java.lang.String>
     * @Author wanggang
     * @Description //查询需要删除的文件名称
     * @Date 16:23 2020/9/3
     * @Param [time]
     **/
    List<String> selectNeedDelLog(String time);

    /**
     * @return void
     * @Author wanggang
     * @Description //更新逻辑删除
     * @Date 16:23 2020/9/3
     * @Param [time]
     **/
    void delLogs(String time);

    Map getProInfoByProName(String proName);


    Map getOrgInfoByProAndUser(String projectId, String userId);

    void addCstToPool(ReportCustomerForm reportCustomerForm);

    void saveInformationZs(ReportCustomerForm reportCustomerForm);

    void insertProjectOpp(ReportCustomerForm reportCustomerForm);

    String getBasicCustomerId(ReportCustomerForm reportCustomerForm);

    void insertCustomerBasicZs(ReportCustomerForm reportCustomerForm);

    List<Map> getCstIsOkReferralClue(Map map);


    /**
     * 检查客户是否已存在
     *
     * @param params 包含userId、customerName、sourceMode和projectName的参数Map
     * @return 存在的记录数
     */
    int checkZsdtdrCustomerExists(Map<String, Object> params);


    /**
     * 检查客户是否已存在
     *
     * @param params 包含customerName、projectName的参数Map
     * @return 存在的记录数
     */
    int checkZsdtdrCustomerExistsHis(Map<String, Object> params);


    /**
     * 检查客的成交信息
     *
     * @return 存在的记录数
     * @para 查询客户成交信息
     */
    Map getCustDealInfo(String custName);

    /**
     * 将客户数据保存到正式表
     *
     * @param list 客户数据列表
     * @return 保存的记录数
     */
    int saveMapCustomerToProjectClue(List<ReportCustomerForm> list);

    /**
     *
     * @param reportCustomerForm
     * @return 保存的地址有问题的招商地图客户记录数
     */
    List<ReportCustomerForm> getZsdtdrClueTempList(ReportCustomerForm reportCustomerForm);

    // 查询需要更新的附件记录
    List<Map<String, Object>> getHistoryCustomerEnclosures();

    // 更新附件URL
    void updateEnclosureUrl(Map<String, Object> params);

    //将导入的零时表数据写入缓存表
    void saveMarkTempToProjectClueCache(String sUserId);

    /**
     * 根据线索ID查询联系方式
     *
     * @param projectClueId 线索ID
     * @return 联系方式列表
     */
    List<Map<String, Object>> queryCluesContacts(@Param("projectClueId") String projectClueId);
}
