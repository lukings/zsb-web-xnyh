package cn.visolink.system.seniorbroker.mapper;

import cn.visolink.system.job.authorization.model.BrokerAccountRecordsBatch;
import cn.visolink.system.seniorbroker.vo.*;
import cn.visolink.message.model.form.MessageClueRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author: BaoQiangLer
 * @ProjectName: visolink
 * @Description:  地图选房Mapper
 * @Date: Created in 2020/10/12
 */

@Mapper
@Repository
public interface SeniorBrokerMapper {

    /**
     * 获取大客户活动数据
     *
     * @param map 信息
     * @return list
     */
    List<AccountActiveExcel> getAccountActiveList(Map<String, Object> map);

    /**
     * 获取大客户活动数据
     *
     * @param activeId 信息
     * @return list
     */
    Map<String,Object> getAccountActiveById(@Param("ActiveId") String activeId);

    /**
     * 获取活动下的二级经纪人
     *
     * @param map 信息
     * @return list
     */
    List<Map<String,Object>> getBrokerByActiveId(Map<String, Object> map);

    /**
     * 获取活动下的二级经纪人导出
     *
     * @param map 信息
     * @return list
     */
    List<BrokerActiveExcel> getBrokerByActiveIdExcel(Map<String, Object> map);


    /**
     * 获取活动下的客户
     *
     * @param map 信息
     * @return list
     */
    List<Map<String,Object>> getCustomerByActiveId(Map<String, Object> map);


    /**
     * 获取活动下的客户导出
     *
     * @param map 信息
     * @return list
     */
    List<CustomerActiveExcel> getCustomerByActiveIdExcel(Map<String, Object> map);


    /**
     * 获取二级经纪人数据
     *
     * @param map 信息
     * @return list
     */
    List<Map<String,Object>> getSeniorBroker(Map<String, Object> map);


    /**
     * 二级经纪人数据导出
     *
     * @param map 信息
     * @return list
     */
    List<SeniorBrokerActiveExcel> getSeniorBrokerExcel(Map<String, Object> map);


    /**
     * 获取项目的大客户
     *
     * @param projectId 经纪人id
     * @return list
     */
    List<Map<String,Object>> getAccountProject(@Param("projectId") String projectId);

    /**
     * 获取二级经纪人活动数据
     *
     * @param map 信息
     * @return list
     */
    List<Map<String,Object>> getSeniorBrokerActive(Map<String, Object> map);


    /**
     * 获取经纪人的大客户数据
     *
     * @param brokerId 经纪人id
     * @return list
     */
    List<Map> getAccountByBrokerId(@Param("BrokerId") String brokerId);


    /**
     * 获取经纪人的大客户数据
     *
     * @param map 经纪人id
     * @return list
     */
    int updateBrokerAccount(Map<String, Object> map);


    /***
     *
     * @param brokerActIdList
     *@return {}
     *@throws
     *@Description: 查询原大客户经理
     *@author FuYong
     *@date 2020/10/20 13:51
     */
    List<Map> getOldAccountManagerL(@Param("brokerActIdList") List<String> brokerActIdList);

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
    List<String> getActivityByAccountId(@Param("accountId") String accountId, @Param("projectId") String projectId);
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
     * @param brokerAccountRecordsList
     *@return {}
     *@throws
     *@Description: 保存重分配记录
     *@author FuYong
     *@date 2020/10/20 9:59
     */
    int saveBrokerAccountRecords(@Param("brokerAccountRecordsList") List<BrokerAccountRecords> brokerAccountRecordsList);

    /***
     *
     * @param brokerActIdList
     *@return {}
     *@throws
     *@Description: 更新大客户经理关联关系
     *@author FuYong
     *@date 2020/10/28 10:01
     */
    int updateBrokerAccountId(@Param("brokerActIdList") List<String> brokerActIdList,@Param("userId") String userId);

    /***
     *
     * @param mapList
     *@return {}
     *@throws
     *@Description: 保存修改信息记录日志
     *@author FuYong
     *@date 2020/10/23 15:31
     */
    int recordUpdateUserLog(@Param("mapList") List<Map> mapList);


    /***
     *
     * @param message
     *@return {}
     *@throws
     *@Description:新增消息
     *@author FuYong
     *@date 2020/9/23 15:10
     */
    int saveMessageInfo(Message message);

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
     * 获取二级经纪人分配历史
     *
     * @param map 信息
     * @return list
     */
    List<Map<String,Object>> getBrokerAccountRecords(Map<String, Object> map);

    /**
     * 获取大客户重分配原因
     *
     * @return list
     */
    List<Map<String,Object>> getDkhCfpType();

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 14:53 2020/11/5
     * @Param [accountId, projectId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getAllBuildBook(@Param("accountId") String accountId,@Param("projectId") String projectId);

    /**
     * @Author wanggang
     * @Description //baocu
     * @Date 11:31 2020/11/5
     * @Param [list]
     * @return void
     **/
    void insertMessageList(@Param("messageList") List<Message> messageList);

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


}
