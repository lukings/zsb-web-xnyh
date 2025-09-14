package cn.visolink.system.channel.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.channel.model.vo.BatchDeleteDuplicateCustomerRequest;
import cn.visolink.system.channel.model.vo.BatchDeleteDuplicateCustomerResponse;
import cn.visolink.system.channel.model.vo.CustomerDuplicateCheckRequest;
import cn.visolink.system.channel.model.vo.CustomerDuplicateCheckResponse;
import cn.visolink.system.channel.model.vo.TaskVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskService {

  ResultBody createTask(TaskVo taskVo);

  List<TaskVo> getListByIds(@Param("drawIds") List<String> drawIds);
  
  ResultBody updateTask(TaskVo taskVo);

  ResultBody stopTask(TaskVo taskVo);

  ResultBody getTeamXsjl(TaskVo taskVo);

  /**
   * 客户判重检查
   * @param request 客户判重检查请求
   * @return 判重结果
   */
  ResultBody<CustomerDuplicateCheckResponse> checkCustomerDuplicate(CustomerDuplicateCheckRequest request);

  /**
   * 批量删除重复客户
   * @param request 批量删除重复客户请求
   * @return 删除结果
   */
  ResultBody<BatchDeleteDuplicateCustomerResponse> batchDeleteDuplicateCustomer(BatchDeleteDuplicateCustomerRequest request);

}
