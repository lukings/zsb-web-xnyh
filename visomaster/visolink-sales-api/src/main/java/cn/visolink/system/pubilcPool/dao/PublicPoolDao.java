package cn.visolink.system.pubilcPool.dao;

import cn.visolink.system.pubilcPool.model.CasePublicPoolVO;
import cn.visolink.system.pubilcPool.model.CluesModel;
import cn.visolink.system.pubilcPool.model.PublicPoolHisVO;
import cn.visolink.system.pubilcPool.model.PublicPoolVO;
import cn.visolink.system.pubilcPool.model.form.CustomerDistributionRecords;
import cn.visolink.system.pubilcPool.model.form.PublicPoolListSearch;
import cn.visolink.system.pubilcPool.model.form.RecoveryEdit;
import cn.visolink.system.pubilcPool.model.form.RedistributionBatch;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/26 16:03
 **/
public interface PublicPoolDao {
    /**
     * @Author wanggang
     * @Description //获取公共池数据
     * @Date 17:39 2021/5/27
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.pubilcPool.model.PublicPoolVO>
     **/
    List<PublicPoolVO> getPublicPoolList(PublicPoolListSearch paramMap);
    /**
     * @Author wanggang
     * @Description //查询历史记录
     * @Date 17:39 2021/5/27
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.pubilcPool.model.PublicPoolHisVO>
     **/
    List<PublicPoolHisVO> getPublicPoolHisList(PublicPoolListSearch paramMap);
    /**
     * @Author wanggang
     * @Description //更新公共池数据（回收）
     * @Date 9:51 2021/5/31
     * @Param [params]
     * @return void
     **/
    void updatePublicPoolVO(RecoveryEdit params);
    /**
     * @Author wanggang
     * @Description //保存回收记录
     * @Date 10:00 2021/5/31
     * @Param [list]
     * @return void
     **/
    void addPublicPoolHis(@Param("list") List<PublicPoolVO> list);
    /**
     * @Author wanggang
     * @Description //保存记录批次
     * @Date 10:43 2021/5/31
     * @Param [batch]
     * @return void
     **/
    void addPublicPoolBatch(Map batch);
    /**
     * @Author wanggang
     * @Description //获取操作人信息
     * @Date 11:56 2021/5/31
     * @Param [userId]
     * @return java.lang.String
     **/
    String getUserName(String userId);
    /**
     * @Author wanggang
     * @Description //新增分配批次表
     * @Date 15:37 2021/5/31
     * @Param [redistributionBatch]
     * @return void
     **/
    void insertRedistributionBatch(RedistributionBatch redistributionBatch);
    /**
     * @Author wanggang
     * @Description //新增分配记录表
     * @Date 15:38 2021/5/31
     * @Param [customerDistributionRecordsList]
     * @return void
     **/
    void insertCustomerDistributionRecords(@Param("customerDistributionRecordsList") List<CustomerDistributionRecords> customerDistributionRecordsList);
    /**
     * @Author wanggang
     * @Description //新增淘客池
     * @Date 10:01 2021/6/1
     * @Param [list]
     * @return void
     **/
    void addPublicPoolList(@Param("publicPoolVOList") List<PublicPoolVO> list);
    /**
     * @Author wanggang
     * @Description //更新公共池（淘客）
     * @Date 10:48 2021/6/1
     * @Param [params]
     * @return void
     **/
    void updatePublicPool(RecoveryEdit params);
    /**
     * @Author wanggang
     * @Description //获取有效线索
     * @Date 14:10 2021/6/1
     * @Param [params]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getCluesMobile(RecoveryEdit params);
    /**
     * @Author wanggang
     * @Description //获取所有线索
     * @Date 14:10 2021/6/1
     * @Param [params]
     * @return java.util.List<java.lang.String>
     **/
    List<CluesModel> getAllClues(RecoveryEdit params);
    /**
     * @Author wanggang
     * @Description //更新线索状态
     * @Date 16:55 2021/6/1
     * @Param [delClues]
     * @return void
     **/
    void updateCluesStatusDel(@Param("delClues") List<CluesModel> delClues);
    /**
     * @Author wanggang
     * @Description //获取公共池数据
     * @Date 18:03 2021/6/2
     * @Param [publicPoolListSearch]
     * @return java.util.List<cn.visolink.system.pubilcPool.model.PublicPoolVO>
     **/
    List<PublicPoolVO> getPublicPoolListByMobile(PublicPoolListSearch publicPoolListSearch);

    /***
    *
     * @param projectClueIdList
    *@return {}
    *@throws
    *@Description: 案场公共池客户数据
    *@author FuYong
    *@date 2021/6/21 16:43
    */
    List<CasePublicPoolVO> getCasePublicPoolList(@Param("projectClueIdList") List<String> projectClueIdList);

    /***
    *
     * @param projectId
     * @param poolType
     * @param customerBasicIdList
    *@return {}
    *@throws
    *@Description: 删除公共池数据
    *@author FuYong
    *@date 2021/6/21 16:43
    */
    int delPublicPool(@Param("projectId") String projectId,@Param("poolType") String poolType,@Param("customerBasicIdList") List<String> customerBasicIdList);

    /***
    *
     * @param projectClueIdList
    *@return {}
    *@throws
    *@Description: 删除案场公共池数据
    *@author FuYong
    *@date 2021/6/21 16:43
    */
    int delCasePublicPool(@Param("projectClueIdList") List<String> projectClueIdList);

    /***
    *
     * @param casePublicPoolVOList
    *@return {}
    *@throws
    *@Description: 新增案场公共池表数据
    *@author FuYong
    *@date 2021/6/21 17:06
    */
    int insertPublic(@Param("list") List<CasePublicPoolVO> casePublicPoolVOList);

    void updateAcPublicPoolVO(RecoveryEdit params);

    /***
     *
     * @param mapList
     *@return {}
     *@throws
     *@Description: 修改案场公共池表数据
     *@author FuYong
     *@date 2021/6/21 17:06
     */
    int editPublic(@Param("list") List<CasePublicPoolVO> mapList);

    /***
     *
     * @param projectClueIdList
     *@return {}
     *@throws
     *@Description: 查询案场公共池是否有数据
     *@author FuYong
     *@date 2021/7/6 17:07
     */
    List<String> getClueIdList(@Param("list") List<String> projectClueIdList);
}
