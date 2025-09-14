package cn.visolink.system.channel.dao;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.channel.model.CustomerDistributionRecords;
import cn.visolink.system.channel.model.Publicpool;
import cn.visolink.system.channel.model.form.PublicpoolForm;
import cn.visolink.system.channel.model.form.RedistributionBatchForm;
import cn.visolink.system.channel.model.vo.CustomerDistributionRecordsVO;
import cn.visolink.system.channel.model.vo.ProjectProtectRuleVO;
import cn.visolink.system.channel.model.vo.PublicpoolVO;
import cn.visolink.system.channel.model.vo.RedistributionBatchVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2019-09-02
 */
public interface PublicpoolDao extends BaseMapper<Publicpool> {

    /**
     * 查询案场重分配记录信息
     */
    List<CustomerDistributionRecordsVO> queryRedistributionRecord(Map map);
    /**
     * 查询案场重分配记录信息(导出)
     */
    List<CustomerDistributionRecords> getCustomerDistributionOpp(Map map);

    /**
    * 通过coder获取重分配原因
    * */
    String getRelson(@Param("dictCode") String dictCode);

    /**
     * 查询渠道重分配记录信息
     */
    List<Map> ProjectClueDeriveMustAcByToker(Map map);

    /**
     * 查询渠道重分配记录信息(导出)
     */
    List<CustomerDistributionRecords> getCustomerDistributionClues(Map map);

    /**
     * 重分配添加批次表
     * */
    Integer insertNewBatch(Map map);

    /**
     * 查询单个人的案场机会信息
     */
    List<Map> QueryOnePeople(Map map);

    /**
     * 查询单个人的渠道信息
     */
    List<Map> QueryOnePeopleClue(Map map);

    /**
     *修改批次详细信息表
     * */
    void UpdatePiCiDetail(Map map);

    /**
     *添加批次详细信息表
     * */
    void InsertPICIDetail(@Param("mapList") List<Map> map);

    /**
     *添加批次详细信息表
     * */
    void InsertPICIDetailTao(@Param("mapList") List<Map> map);

    /**
     * 查询登录人的信息
     * */
    Map selectUserJob(Map map);

    /**
     * 修改客户线索表和机会表渠道相关字段
     * @param map
     * @return
     */
    void UpdateXianSuoChance(@Param("mapList") List<Map> map);

    /**
     *更新线索
     * */
    void updateXianSuo(@Param("mapList") List<Map> map);

    /**
     * 修改客户线索表和机会表案场相关字段
     * */
    void UpdateChanceInformation(@Param("mapList") List<Map> map);

    /**
     *开始添加客户历史人员表
     * */
    Integer insertCustomerHistory(@Param("mapList") List<Map> map);

    /**
     * 删除客户公共的信息
     * */
    void deletePool(@Param("clueIdList") List<String> clueIdList);

    /**
     * 查询人员接口
     * */
    List<Map> selectMan(Map map);

    /**
     * 查询公共池中的顾客信息
     * */
    Page<PublicpoolVO> queryPublicPool(PublicpoolForm publicpoolForm);

    List<PublicpoolVO> queryPublicPools(PublicpoolForm publicpoolForm);

    /**
     * 查询内渠或者案场对应的规则
     * */
    List<ProjectProtectRuleVO> selectRuleByType(Map map);

    /**
     * 重新分配原因
     * */
    List<Map> getClueResetCause(Map map);

    /**
     * 业绩变更记录
     * */
    void insertDistributionRecords(@Param("mapList") List<Map> map);

    /**
     * 查询详细信息
     * */
    Map selectDetailedInformation(Map map);

    /**
     * 修改线索和机会表的报备人信息
     * */
    void updateDetaileReport(Map map);

    /**
     * 修改报备人日志
     * */
    void update_DetaileReport_log(Map map);

    /**
     * 修改报备人日志
     * */
    void insertModificationDetails(Map map);

    /**
     * getModification
     * @param projectClueId
     * @return
     */
    List<Map> getModification(@Param("projectClueId") String projectClueId);

    /**
     * 查询修改详情
     * @param updateLogId
     * @return
     */
    Map getModificationDetails(@Param("updateLogId") String updateLogId);

    /**
     * 查询中介公司
     * @param userId
     * @return
     */
    Map getCompanyInfo(@Param("userId") String userId);

    /***
    *
     * @param clueIdList
    *@return {}
    *@throws
    *@Description: 逾期消息删除
    *@author FuYong
    *@date 2020/8/20 18:03
    */
    int updateMessageByClueId(@Param("clueIdList") List<String> clueIdList,@Param("messageTypeList") List<String> messageTypeList);

    /***
    *
     * @param projectId
     * @param userId
     * @param tuokeOrAc
    *@return {}
    *@throws
    *@Description: 查询分配人员信息
    *@author FuYong
    *@date 2020/9/9 14:45
    */
    Map getDistributionUserInfo(@Param("projectId") String projectId,@Param("userId") String userId,@Param("tuokeOrAc") String tuokeOrAc);

    /***
    *
     * @param projectClueId
    *@return {}
    *@throws
    *@Description: 删除大客户经理业绩
    *@author FuYong
    *@date 2020/11/12 12:02
    */
    int delAccountPerformance(String projectClueId);

    /**
     * @Author luqianqian
     * @Description //获取登录人总监权限
     * @Date 21:24 2023/09/13
     * @Param [queryConditionForm]
     * @return void
     **/
    List<Map> getUserZjJobOrgInfo(Map map);

    /**
     * @Author luqianqian
     * @Description //获取app权限
     * @Date 21:24 2023/09/13
     * @Param [queryConditionForm]
     * @return void
     **/
    List<Map> getJobsAppFunctionRel(Map map);

    /**
     * @Author luqianqian
     * @Description //保存app权限
     * @Date 21:24 2023/09/13
     * @Param [queryConditionForm]
     * @return void
     **/
    int saveJobsAppFunctionRel(Map map);

    /**
     * @Author luqianqian
     * @Description //保存app权限
     * @Date 21:24 2023/09/13
     * @Param [queryConditionForm]
     * @return void
     **/
    int delJobsAppFunctionRel(Map map);

    List<Map> getProjectListHasObtainCst(Map map);

    List<String> getProListAll(String projectId);

    List<String> getProListAllRegions(@Param("areaList") List<String> areaList);
}
