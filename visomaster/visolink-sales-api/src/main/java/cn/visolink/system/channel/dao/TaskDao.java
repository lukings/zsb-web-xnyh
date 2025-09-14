package cn.visolink.system.channel.dao;

import cn.visolink.system.channel.model.TaskCustomer;
import cn.visolink.system.channel.model.TaskMember;
import cn.visolink.system.channel.model.vo.TaskVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lishumao
 * @since 2024-08-21
 */
public interface TaskDao {


  void saveZsMapTask(TaskVo taskVo);

  void saveTaskMembers(@Param("taskMemberList") List<TaskMember> taskMemberList);

  /**
   * 维护月度历史数据   * @return
   */
  List<TaskVo> selectMothTask();

  Integer updateTaskMember(TaskMember taskMember);

  List<TaskVo> getListByIds(@Param("drawIds") List<String> drawIds);

  int getUserIsOk(Map userMap);

  int getTeamIsOk(Map userMap);

  int getTaskAreaIsOk(Map userMap);
  
  /**
   * 保存任务客户关系
   * @param taskCustomerList 任务客户关系列表
   */
  void saveTaskCustomers(@Param("taskCustomerList") List<TaskCustomer> taskCustomerList);
  
  /**
   * 保存单个任务客户关系
   * @param taskCustomer 任务客户关系
   */
  void saveTaskCustomer(Map<String, Object> taskCustomer);
  
  /**
   * 根据ID删除任务客户关系
   * @param id 任务客户关系ID
   * @param modifyBy 修改人
   */
  void deleteTaskCustomer(@Param("id") String id, @Param("modifyBy") String modifyBy);
  
  /**
   * 根据任务ID删除所有关联的客户关系
   * @param taskId 任务ID
   * @param modifyBy 修改人
   */
  void deleteTaskCustomersByTaskId(@Param("taskId") String taskId, @Param("modifyBy") String modifyBy);
  
  /**
   * 根据客户ID删除关联关系
   * @param customerId 客户ID
   * @param modifyBy 修改人
   */
  void deleteTaskCustomersByCustomerId(@Param("customerId") String customerId, @Param("modifyBy") String modifyBy);
  
  
  Integer updateTask(TaskVo taskVo);

   void stopTask(TaskVo taskVo);

   void deleteTask(TaskVo taskVo);

  /**
   * 保存任务客户关系
   * @param taskCustomerList 任务客户关系列表
   */
  void updateMapClueStatus(@Param("taskCustomerList") List<TaskCustomer> taskCustomerList);


  /**
   * 获取团队(xiaoshou经理)
   *
   * @param
   * @return return
   */
  List<Map> getTeamXsjl(TaskVo taskVo);

  /**
   * 获取项目的营销经理和招商总监
   *
   * @param
   * @return return
   */
  List<String> getProjectYxjlAndZszj(@Param("projectIds") List<String> projectIds);

  void updateProjectCule(Map<String, Object> params);

  void updateTaskStatusByTime();

  List<Map<String, Object>> selectTaskCustomerToRecycle();

  void recycleTaskCustomerByTaskStatusAndNotReported();

  void batchUpdateByProjectClueIds(@Param("projectClueIds") List<String> projectClueIds, @Param("updateMap") Map<String, Object> updateMap);

  List<Map<String, Object>> selectTaskCustomerByTaskId(@Param("taskId") String taskId);


  List<Map<String, Object>> selectTaskByTaskId(@Param("taskId") String taskId);
  /**
   * 更新任务团员的到访指标（arriveCount）
   * @param taskId 任务ID
   * @param arriveCount 到访数量
   * @return 更新条数
   */
  int updateTaskBjl(@Param("taskId") String taskId, @Param("arriveCount") int arriveCount);

  /**
   * 批量逻辑删除项目线索
   * @param customerIds 客户ID列表
   * @param operatorId 操作人ID
   * @param operatorName 操作人姓名
   * @return 删除数量
   */
  int batchDeleteProjectClues(@Param("customerIds") List<String> customerIds, 
                             @Param("operatorId") String operatorId, 
                             @Param("operatorName") String operatorName);

  /**
   * 批量逻辑删除跟进记录
   * @param customerIds 客户ID列表
   * @param operatorId 操作人ID
   * @param operatorName 操作人姓名
   * @return 删除数量
   */
  int batchDeleteFollowupRecords(@Param("customerIds") List<String> customerIds, 
                                @Param("operatorId") String operatorId, 
                                @Param("operatorName") String operatorName);
}
