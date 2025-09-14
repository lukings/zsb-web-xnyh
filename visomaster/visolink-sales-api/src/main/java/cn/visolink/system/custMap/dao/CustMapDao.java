package cn.visolink.system.custMap.dao;

import cn.visolink.system.channel.model.vo.ProjectCluesNew;
import cn.visolink.system.custMap.bo.ZsMapPermissionsQueryBO;
import cn.visolink.system.custMap.bo.ZsMapPermissionsResBO;
import cn.visolink.system.custMap.bo.ZsMapResBO;
import cn.visolink.system.project.model.vo.ProjectVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //客户地图dao
 * @Date 2022/9/1 17:49
 **/
public interface CustMapDao {
    /**
     * 获取园区
     * */
    List<Map> getAllPark(Map map);

    /**
     * 获取标签
     * */
    List<Map> getAllLabel(Map map);


    /**
     * 获取走访坐标
     * */
    List<Map> tokerNumNew(Map paramMap);
    /**
     * 获取报备坐标
     * */
    List<Map> reportNum(Map paramMap);
    /**
     * 获取到访坐标
     * */
    List<Map> visitNum(Map paramMap);
    /**
     * 获取成交坐标
     * */
    List<Map> signNumNew(Map paramMap);
    /**
     * 获取客户行业
     * */
    List<Map> getCstIndustryOne();
    /**
     * @Author wanggang
     * @Description //走访热力图
     * @Date 14:58 2022/9/3
     * @Param [paramMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> tokerNumNewHeat(Map paramMap);
    /**
     * @Author wanggang
     * @Description //报备热力图
     * @Date 14:58 2022/9/3
     * @Param [paramMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> reportNumHeat(Map paramMap);
    /**
     * @Author wanggang
     * @Description //到访热力图
     * @Date 14:58 2022/9/3
     * @Param [paramMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> visitNumHeat(Map paramMap);
    /**
     * @Author wanggang
     * @Description //成交热力图
     * @Date 14:58 2022/9/3
     * @Param [paramMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> signNumNewHeat(Map paramMap);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 11:49 2022/9/30
     * @Param [orgId, userJobCode]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getTeamUser(@Param("orgId") String orgId,@Param("jobCode") String userJobCode);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 11:49 2022/9/30
     * @Param [orgId, userJobCode]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getTeamUserNew(@Param("orgPath") String orgPath);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 11:49 2022/9/30
     * @Param [orgId, userJobCode]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getProjectUserXsjl(@Param("orgPath") String orgPath);
    
    /**
     * @Author wanggang
     * @Description //根据组织路径查询团队用户信息（包含ID、姓名、账号）
     * @Date 2025/1/27
     * @Param [orgPath] 组织路径
     * @return java.util.List<java.util.Map> 团队用户信息列表
     **/
    List<Map> getTeamUserInfo(@Param("orgPath") String orgPath);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 11:49 2022/9/30
     * @Param [orgId, userJobCode]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getProjectUserGlc(@Param("projectID") String projectID);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 11:49 2022/9/30
     * @Param [orgId, userJobCode]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getProjectUserGlz(@Param("orgPath") String orgPath);

    /**
     * @Author wanggang
     * @Description //获取岗位组织ID
     * @Date 14:02 2022/9/30
     * @Param [userId, jobCode]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getUserOrgs(@Param("userId") String userId,@Param("jobCode") String jobCode);
    /**
     * @Author wanggang
     * @Description //获取岗位组织ID
     * @Date 14:02 2022/9/30
     * @Param [userId, jobCode]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getUserOrgsNew(@Param("userId") String userId,@Param("jobCode") String jobCode);

    /**
     * 获取线索客户
     * */
    List<ProjectCluesNew> getCluesCustomer(Map paramMap);
    /**
     * 获取机会客户
     * */
    List<ProjectCluesNew> getOpportunityCustomer(Map paramMap);
    /**
     * 获取公客池客户
     * */
    List<ProjectCluesNew> getPublicCustomer(Map paramMap);
    /**
     * 获取走访坐标客户
     * */
    List<ProjectCluesNew> getChannelTokerCustomer(Map paramMap);
    /**
     * 获取报备坐标客户
     * */
    List<ProjectCluesNew> getSalesReportCustomer(Map paramMap);
    /**
     * 获取到访坐标客户
     * */
    List<ProjectCluesNew> getSalesVisitCustomer(Map paramMap);
    /**
     * 获取成交坐标客户
     * */
    List<ProjectCluesNew> getSalesSignCustomer(Map paramMap);
    /**
     * @Description //获取区域下项目
     **/
    List<ProjectVO> getProList(Map map);
    /**
     * @Author wanggang
     * @Description //获取项目事业部，项目名称
     * @Date 17:29 2020/8/25
     * @Param [list]
     * @return java.util.Map
     **/
    Map getAreaNameAndProNames(@Param("list") List<ProjectVO> list);

    List<ZsMapPermissionsResBO> getMapPermissions(ZsMapPermissionsQueryBO zsMapPermissionsQueryBO);

    List<ZsMapResBO> getOpportunity(Map paramMap);

    List<ZsMapResBO> getClues(Map paramMap);

    List<ZsMapResBO> getPublicPoolPage(Map paramMap);

    /**
     * 清空客户状态缓存表
     */
    void clearOpportunityCache();

    /**
     * 清空客户状态缓存表零时表
     */
    void clearOpportunityCacheStaging();

    /**
     * 复制CacheTmp到Cache
     */
    void insertOpportunityCache();
    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateOpportunityCacheStagingXM();

    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateOpportunityCacheStagingQY();

    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateOpportunityCacheStagingJT();


    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateOpportunityCacheStagingXM_Tkdt();

    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateOpportunityCacheStagingQY_Tkdt();

    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateOpportunityCacheStagingJT_Tkdt();


    /**
     * 清空客户状态缓存表
     */
    void clearCluesCache();

    /**
     * 清空客户状态缓存表零时表
     */
    void clearCluesCacheStaging();

    /**
     * 复制CacheTmp到Cache
     */
    void insertCluesCache();
    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateCluesCacheStagingXM_Khdt();

    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateCluesCacheStagingQY_Khdt();

    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateCluesCacheStagingJT_Khdt();


    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateCluesCacheStagingXM_Tkdt();

    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateCluesCacheStagingQY_Tkdt();

    /**
     * 更新客户状态缓存
     * @return 更新的记录数
     */
    int updateCluesCacheStagingJT_Tkdt();

    int updateCluesCacheStagingXM_KhdtMapHis();

    int updateCluesCacheStagingXM_TkdtMapHis();
    /**
     * 从缓存表中查询客户状态
     * @param paramMap 查询参数
     * @return 客户状态列表
     */
    List<ZsMapResBO> getOpportunityFromCache(Map<String, Object> paramMap);

    // 修改getPublicPool方法，添加分页参数
    List<ZsMapResBO> getPublicPool(Map paramMap);

    List<ZsMapResBO> getCluesKhdtHis(Map paramMap);

    List<ZsMapResBO> getCluesTkdt(Map paramMap);

    List<ZsMapResBO> getCluesTkdtHis(Map paramMap);

    List<ZsMapResBO> getCluesFromCache(Map paramMap);

    List<String> selectActiveTaskCustomerIds(String projectId);

    /**
     * 查询 flag 以 '_1' 结尾的数据
     */
    List<Map> selectFlagEndWith1();

    /**
     * 查询 flag 以 '_1' 结尾的数据
     */
    List<Map> selectFlagEndWith1AndUserId(Map param);
    
    /**
     * 通过CustomerName和projectId查b_project_opportunity_cache
     */
    List<Map> selectOpportunityCacheByNameAndProjectId(Map param);
    /**
     * 通过CustomerName和projectId查b_project_clues_cache
     */
    List<Map> selectCluesCacheByNameAndProjectId(Map param);

    /**
     * 通过OpportunityClueId和mapType更新b_project_opportunity_cache表StatusType
     */
    void updateOpportunityCacheStatusTypeByIdAndMapType(@Param("opportunityClueId") String opportunityClueId, @Param("statusType") String statusType, @Param("mapType") String mapType);
    /**
     * 通过ProjectClueId和mapType更新b_project_clues_cache表StatusType
     */
    void updateCluesCacheStatusTypeByIdAndMapType(@Param("projectClueId") String projectClueId, @Param("statusType") String statusType, @Param("mapType") String mapType);

    /**
     * 通过ProjectClueId更新b_project_clues_cache表StatusType
     */
    void updateCluesCacheStatusTypeById(@Param("projectClueId") String projectClueId, @Param("statusType") String statusType);
}
