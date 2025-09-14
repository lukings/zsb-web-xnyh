package cn.visolink.system.job.authorization.mapper;

import cn.visolink.system.job.authorization.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.09
 */
@Repository
@Mapper
public interface JobMapper {


    /**
     * 获取所有岗位
     */
    List<Map> getJobByAuthId(Map map);

    /**
     * 查询所有组织架构
     */

    List<Map> getAllOrg(Map map);

    /**
     * 获取通用岗位列表
     *
     * @param map
     * @return
     */
    List<Map> getAllCommonJob(Map map);

    /**
     * 获取岗位下的人员
     *
     * @param map
     * @return
     */
    List<Map> getSystemJobUserList(Map map);
    Integer getSystemJobUserListCount(Map map);


    /**
     * @author liang
     * @date 2023/10/17 14:47
     * @description: 导出用户及岗位内容
     */
    List<UsersAndPositions> getUsersAndPositions();

    /**
     * 获取当前和下属所有组织岗位
     *
     * @param map
     * @return
     */
    List<Map> getSystemJobAllList(Map map);

    /**
     * 新增岗位-插入Jobs信息
     *
     * @param map
     * @return
     */
    int saveSystemJobForManagement(Map map);
    int saveSystemJobForManagement2(Map map);

    /**
     * 登录人有权限的菜单
     *
     * @param map
     * @return
     */
    List<Map> userMenusByUserId(Map map);

    /**
     * 登录人有权限的功能
     *
     * @param map
     * @return
     */
    List<Map> userFunctionsByUserId(Map map);

    /**
     * 该岗位已有的菜单和功能
     *
     * @param map
     * @return
     */
    List<Map> jobFunctionsByUserId(Map map);

    /**
     * 查询组织岗位功能授权
     *
     * @param map
     * @return
     */
    List<Map> getSystemJobMenusID(Map map);

    /**
     * 删除组织岗位功能授权
     *
     * @param map
     * @return
     */
    int removeSystemJobAuth(Map map);

    /**
     * 保存组织岗位功能授权
     *
     * @param map
     * @return
     */
    int saveSystemJobAuthManagement(Map map);

    /**
     * 更新Jobs信息
     *
     * @param map
     * @return
     */
    int modifySystemJobByUserId(Map map);

    /**
     * 管理端删除岗位
     *
     * @param map
     * @return
     */
    int removeSystemJobByUserId(Map map);

    /**
     * 查询用户名是否存在
     *
     * @param map
     * @return
     */
    Map getSystemUserNameExists(Map map);
    Map getSystemUserNameAndMobileExists(Map map);

    /**
     * 新增用户
     *
     * @param map
     * @return
     */
    int saveSystemUser(Map map);

    /**
     * 获取用户在岗位与平台账号关系表中是否有数据
     * @param map
     * @return
     */
    List<Map> getJobSuserrel(Map map);

    /**
     * 更新岗位为当前岗
     * @param ID
     */
    void updateJobUserRelCurrentJob(String ID);

    /**
     * 保存岗位与平台账号关系表
     * @param
     * @return
     */
    int saveJobSuserrel(Map map);

    /**
     * 删除用户人员信息
     * @param map
     * @return
     */
    int removeSystemJobUserRel(Map map);

    /**
     * 查询用户信息
     * @param map
     * @return
     */
    List<Map> findUserDesc(Map map);

    /**
     * 根据id删除用户岗位
     * @param ids
     * @return
     */
    void delJobUserRelById(@Param("ids") String ids);

    /**
     * @Author wanggang
     * @Description //查询需删除的组织ID
     * @Date 15:47 2022/1/12
     * @Param [ids, accountId]
     * @return java.lang.String
     **/
    List<Map> selectJobUserRel(@Param("ids") String ids);
    /**
     * 从C_User表查询用户数据
     *
     * @param map
     * @return
     */
    Map getUserFromCuser(Map map);

    /**
     * 根据岗位ID查询组织信息
     *
     * @param map
     * @return
     */
    Map getOrgInfoByJobID(Map map);

    /**
     * 引入普通用户插入关联关系数据
     *
     * @param map
     * @return
     */
    int saveAccountToJobUserURl(Map map);

    Map getOrgJobId(String jobId);

    Map getUserId(String userId);

    /**
     * 查询引入OA账户时是否有重复
     *
     * @param map
     * @return
     */
    Map getCuserToAccount(Map map);

    /**
     * 组织岗位引入人员插入人员表
     *
     * @param map
     * @return
     */
    Map saveCuserToAccount(Map map);

    /**
     * 组织岗位引入人员插入人员表
     *
     * @param map
     * @return
     */
    int insertCuserToAccount(Map map);

    /**
     * 更新线索中的拓客信息
     *
     * @param map
     * @return
     */
    int modifyProjectClueTokerAttribution(Map map);

    /**
     * 更新机会中的拓客信息
     *
     * @param map
     * @return
     */
    int modifyProjectOppoTokerAttribution(Map map);

    /**
     * 更新线索中的案场信息
     *
     * @param map
     * @return
     */
    int modifyProjectClueSalesAttribution(Map map);

    /**
     * 更新机会中的案场信息
     *
     * @param map
     * @return
     */
    int modifyProjectOppoSalesAttribution(Map map);

    Map getUserProxyregisterByUserID(Map map);

    int saveUserProxyregisterInvitationCode(Map map);

    /**
     * 引入用户信息
     * @param map
     * @return
     */
    List<Map> getIntroducingUsers(Map map);

    List<Map> getJobIdCall(@Param("ids") String ids);

    /**
     * 统计引入用户的信息数量
     * @return
     */
    Integer getIntroducingUsersCount(Map map);

    /**
     * 保存引入用户
     */
    int saveIntroducingUsers(Map map);

    /**
     * 修改用户信息
     * @param reqMap
     * @return
     */
    int modifySystemJobUserRel(Map reqMap);


    /**
     * 判断岗位是否存在
     * @param
     * @return
     */
    Map isRepeat(@Param("accountId") String accountId,@Param("jobId") String jobId);
    int updateUserIdm(@Param("accountId") String accountId);

    /**
     * 判断是否存在当前岗位
     * @param
     * @return
     */
    Map isCurrentJob(@Param("accountId") String accountId);
    /**
     * 获取所有菜单
     * @return
     */
    List<Map> getAllMenu();

    /***
     * 获取指定岗位菜单
     * */

    List<Map> getJobMenu(@Param("jobId") String jobId);

    /***
     * 获取通用岗位指定菜单
     * */
    List<Map> getCommonMenu(@Param("jobId") String jobId);
    /***
     * 删除指定岗位菜单
     * */
    int delJobMRelMenu(@Param("jobId") String jobId);
    /***
     * 删除指定岗位菜单
     * */
    int delCommonJobMRelMenu(@Param("jobId") String jobId);
    /***
     * 添加指定岗位菜单
     * */
    int saveJobMenu(@Param("jobId") String jobId,@Param("menuId") String menuId);
    /***
     * 添加指定岗位菜单
     * */
    int saveCommonJobMenu(@Param("jobId") String jobId,@Param("menuId") String menuId);

    /**
     * 中介公司
     * @retu通用rn
     */
    List<Map> getAllCompanyInfo(@Param("ids") String ids);

    /**
     * 获取存在的中介ID
     * @param orgId
     * @return
     */
    List<String> getAllCompanyInfoByOrgId(@Param("orgId") String orgId);
    /**
     * 项目组织
     *
     */
    List<Map> getAllOrgProject();
    List<Map> getAllOrgProject2();

    int updateProjectId(Map map);

    int updateProjectIdNew(Map map);

    String getFullPath(@Param("orgId") String orgId);

    int updateOrg(@Param("projectId")String projectId,@Param("fullPath")String fullPath);

    Map getCurrentJobs(Map map);

    int insertCityJob(@Param("cityJobList") List<Map> cityJobList);

    int deleteCityJob(@Param("jobId") String jobId);

    List<Map> getCityJobList(@Param("jobId") String jobId);

    /**
     * 查询全民经纪人账号
     * @param id
     * @return
     */
    String getBrokerId(String id);

    /**
     * 新增消息
     * @param map
     * @return
     */
    int insertMessage(Map map);

    /**
     * 查询岗位信息
     * @param JobID
     * @return
     */
    Map selectOrgByJobId(@Param("JobID") String JobID);
    /**
     * 更新置业顾问团队数据
     * @param
     * @return
     */
    void updateSaleTeamID(Map map);

    /**
     * 更新渠道团队数据
     * @param
     * @return
     */
    void updateReportTeamID(Map map);

    /**
     * 更新渠道团队数据
     * @param
     * @return
     */
    int getCstBySalesId(Map selectMap);

    /**
     * 更新渠道团队数据
     * @param
     * @return
     */
    int getCstByReId(Map selectMap);

    /**
     * 更新渠道团队数据
     * @param
     * @return
     */
    String getOrgName(String orgId);
    /**
     * @Author wanggang
     * @Description //查询项目下部门组织
     * @Date 15:16 2020/10/10
     * @Param [projectId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProOrg(String projectId);
    /**
     * @Author wanggang
     * @Description //保存部门
     * @Date 16:07 2020/10/10
     * @Param [list]
     * @return void
     **/
    void addDepts(@Param("list") List<Map> list);
    /**
     * @Author wanggang
     * @Description //获取项目组织下岗位（默认岗位）
     * @Date 16:09 2020/10/10
     * @Param [orgId]
     * @return java.util.List<java.util.Map>
     **/
    List<String> getProJobs(String orgIds);
    /**
     * @Author wanggang
     * @Description //添加岗位
     * @Date 9:01 2020/10/12
     * @Param [jobAdd]
     * @return void
     **/
    void saveSystemJobForManagementList(@Param("list") List<Map> list);
    /**
     * @Author wanggang
     * @Description //获取默认通用岗数据
     * @Date 9:05 2020/10/12
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getComJobs(String param);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 10:34 2020/10/12
     * @Param [reqMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getDeSystemJobUserList(Map reqMap);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 10:35 2020/10/12
     * @Param [reqMap]
     * @return java.lang.Integer
     **/
    Integer getDeSystemJobUserListCount(Map reqMap);
    /**
     * @Author wanggang
     * @Description //移除岗位人员
     * @Date 11:12 2020/10/12
     * @Param [ID]
     * @return void
     **/
    void removeUserRel(String ID);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 13:59 2020/10/12
     * @Param [id]
     * @return java.util.Map
     **/
    Map getUserDesc(String id);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:13 2020/10/12
     * @Param [reqMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getDeJobsList(Map reqMap);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:58 2020/10/13
     * @Param [reqMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getDeComJobsList(Map reqMap);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 14:42 2020/10/13
     * @Param [reqMap]
     * @return java.util.List<java.lang.String>
     **/
    List<String> jobUserIsExit(Map reqMap);
    /**
     * @Author wanggang
     * @Description //更新经纪人业绩归属信息
     * @Date 17:57 2020/11/4
     * @Param [resultMap]
     * @return void
     **/
    void updatePerDkh(Map resultMap);
    /**
     * @Author wanggang
     * @Description //更新二级经纪人关系表
     * @Date 17:57 2020/11/4
     * @Param [resultMap]
     * @return void
     **/
    void updateSecDkh(Map resultMap);

    /**
     * @Author wanggang
     * @Description //查询经理下的二级经纪人
     * @Date 9:43 2020/11/5
     * @Param [resultMap]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getSecCstList(Map resultMap);
    /**
     * @Author wanggang
     * @Description //baocu
     * @Date 11:31 2020/11/5
     * @Param [list]
     * @return void
     **/
    void insertMessageList(@Param("messageList") List<Message> messageList);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 14:53 2020/11/5
     * @Param [brokerAccountRecordsList]
     * @return void
     **/
    void saveBrokerAccountRecords(@Param("brokerAccountRecordsList") List<BrokerAccountRecords> brokerAccountRecordsList);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 14:53 2020/11/5
     * @Param [accountId, projectId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getAllBuildBook(Map resultMap);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:02 2020/11/5
     * @Param [accountId, projectId]
     * @return java.util.Map
     **/
    List<Map> getOldAccountManagerL(Map resultMap);

    /***
     *
     * @param userId
     *@return {}
     *@throws
     *@Description: 查询大客户经理所有项目
     *@author FuYong
     *@date 2020/11/12 16:18
     */
    List<String> getProjectIdList(String userId);

    /***
     *
     * @param id
     *@return {}
     *@throws
     *@Description: 查询模板信息
     *@author FuYong
     *@date 2020/11/16 19:02
     */
    CommonTemplate getTemplateInfo(String id);

    /***
     *
     *@return {}
     *@throws
     *@Description: 删除用户模板配置
     *@author FuYong
     *@date 2020/11/11 17:01
     */
    int delUserTemplateConfig(@Param("userId") String userId,
                              @Param("jobCode") String jobCode,
                              @Param("templateId") String templateId,
                              @Param("projectId") String projectId);

    /***
     *
     * @param userId
     * @param jobCode
     * @param templateId
     * @param projectId
     *@return {}
     *@throws
     *@Description: 实际删除配置
     *@author FuYong
     *@date 2020/11/27 17:43
     */
    int delUserTemplateConfigTwo(@Param("userId") String userId,
                                 @Param("jobCode") String jobCode,
                                 @Param("templateId") String templateId,
                                 @Param("projectId") String projectId);

    /***
     *
     * @param userId
     * @param jobCode
     * @param templateId
     * @param projectId
     *@return {}
     *@throws
     *@Description: 查询是否配置模板
     *@author FuYong
     *@date 2020/11/11 17:02
     */
    String getIsConfigTemplate(@Param("userId") String userId,
                               @Param("jobCode") String jobCode,
                               @Param("templateId") String templateId,
                               @Param("projectId") String projectId);

    /***
     *
     * @param UserTemplateConfig
     *@return {}
     *@throws
     *@Description: 保存模板配置信息
     *@author FuYong
     *@date 2020/11/11 16:59
     */
    int saveUserTemplateConfig(UserTemplateConfig UserTemplateConfig);

    /***
     *
     * @param jobCode
     *@return {}
     *@throws
     *@Description: 查询通用岗位模板
     *@author FuYong
     *@date 2020/11/26 19:22
     */
    List<String> getCommonTemplateListByJobCode(String jobCode);

    /**
     　　* @description: 查询第三方账户表
     　　* @param ${tags}
     　　* @return ${return_type}
     　　* @throws
     　　* @author ${lilei}
     　　* @date 2021/3/23 下午3:14
     　　*/
    Map getBaccountThirdParty(Map map);

    /***
     *
     * @param map
     *@return {}
     *@throws
     *@Description: 查询二级经纪人
     *@author FuYong
     *@date 2021/4/25 15:50
     */
    List<Map> getBrokerList(Map map);

    /***
     *
     * @param projectId
     * @param userId
     *@return {}
     *@throws
     *@Description: 查询当前项目下有大客户经理的二级经纪人
     *@author FuYong
     *@date 2021/4/25 15:17
     */
    List<String> getSecCstListByPojId(@Param("projectId") String projectId,@Param("userId") String userId);

    /***
     *
     * @param projectId
     *@return {}
     *@throws
     *@Description: 查询当前项目下无大客户经理的二级经纪人
     *@author FuYong
     *@date 2021/4/28 16:30
     */
    List<Map> getSecCstTwoListByPojId(@Param("projectId") String projectId,@Param("brokerList")List<String> brokerList);

    /***
     *
     * @param brokerAccountList
     *@return {}
     *@throws
     *@Description: 保存二级经纪人
     *@author FuYong
     *@date 2021/4/25 15:18
     */
    int saveBrokerAccount(@Param("brokerAccountList") List<BrokerAccount> brokerAccountList);

    /***
     *
     * @param brokerAccountRecordsBatch
     *@return {}
     *@throws
     *@Description: 保存大客户经理变更批次
     *@author FuYong
     *@date 2021/4/25 15:19
     */
    int saveBrokerAccountRecordsBatch(BrokerAccountRecordsBatch brokerAccountRecordsBatch);

    /***
     *
     * @param projectId
     *@return {}
     *@throws
     *@Description: 查询项目名称
     *@author FuYong
     *@date 2021/4/25 15:34
     */
    String getProjectName(@Param("projectId") String projectId);

    /***
     *
     *@return {}
     *@throws
     *@Description: 查询业绩归属表数据
     *@author FuYong
     *@date 2020/10/22 20:13
     */
    List<AccountPerformance> getAccountPerformanceList(@Param("brokerActIdList") List<String> brokerActIdList);

    /***
     *
     * @param accountId
     * @param projectId
     *@return {}
     *@throws
     *@Description: 查询大客户经理活动
     *@author FuYong
     *@date 2020/10/22 21:07
     */
    List<String> getActivityByAccountId(@Param("accountId") String accountId,
                                        @Param("projectId") String projectId);


    /***
     *
     * @param idList
     *@return {}
     *@throws
     *@Description: 修改业绩归属数据
     *@author FuYong
     *@date 2020/10/22 20:57
     */
    int updateAccountPerformance(@Param("idList") List<String> idList,@Param("userId") String userId);

    /***
     *
     * @param idList
     *@return {}
     *@throws
     *@Description: 修改业绩归属数据
     *@author FuYong
     *@date 2020/10/22 20:57
     */
    int updateAccountPerformanceTwo(@Param("idList") List<String> idList,@Param("userId") String userId);

    /***
     *
     * @param brokerAccountList
     *@return {}
     *@throws
     *@Description: 保存二级经纪人
     *@author FuYong
     *@date 2021/4/25 15:18
     */
    int editBrokerAccount(@Param("brokerAccountList") List<BrokerAccount> brokerAccountList);

    /**
     * 查询所有的岗位
     *
     * @param map
     * @return
     */
    List<Map> selectJobsList(Map map);

    /**
     * 绑定项目
     *
     * @param bindProject
     * @return
     */
    Integer updateBindProject(BindProject bindProject);

    /**
     * 更新绑定的项目名称
     *
     * @param map
     * @return
     */
    Integer updateBindProjectName(Map map);
    /**
     * @Author wanggang
     * @Description //获取项目组织数据
     * @Date 15:41 2021/11/29
     * @Param [projectId]
     * @return java.util.Map
     **/
    Map getProOrgData(String projectId);
    /**
     * @Author wanggang
     * @Description //添加项目城市
     * @Date 8:50 2021/12/18
     * @Param [map]
     * @return void
     **/
    void addProCity(Map map);
    /**
     * @Author wanggang
     * @Description //删除对应的部门
     * @Date 15:57 2022/1/12
     * @Param [orgIdOld, id]
     * @return void
     **/
    void delUserOrg(@Param("orgId") String orgIdOld,@Param("accountId") String id);
    /**
     * @Author wanggang
     * @Description //添加项目岗位
     * @Date 15:28 2022/5/22
     * @Param [proMaps]
     * @return void
     **/
    void addProJobs(@Param("list") List<Map> proMaps);

    int getOppCstBySalesId(Map selectMap);
    /**
     * @Author wanggang
     * @Description //是否系统管理员
     * @Date 14:46 2022/10/29
     * @Param [userId]
     * @return int
     **/
    int getIsSys(String userId);

    /**
     * 获取岗位下人员
     * */
    List<Map> getJobsInsUserList(Map map);

    /**
     * 更新用户岗位是否调岗
     * */
    int updateUserJobRelIsPost(Map map);

    String getComIdByJobId(String jobId);

    Map getZgQx(String userId);
}
