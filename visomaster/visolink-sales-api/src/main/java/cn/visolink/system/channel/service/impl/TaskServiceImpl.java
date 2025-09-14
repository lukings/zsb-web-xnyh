package cn.visolink.system.channel.service.impl;

import cn.visolink.common.TaskTypeEnum;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.dao.TaskDao;
import cn.visolink.system.channel.model.ProjectRuleDetail;
import cn.visolink.system.channel.model.TaskCustomer;
import cn.visolink.system.channel.model.TaskMember;
import cn.visolink.system.channel.model.vo.BatchDeleteDuplicateCustomerRequest;
import cn.visolink.system.channel.model.vo.BatchDeleteDuplicateCustomerResponse;
import cn.visolink.system.channel.model.vo.CustomerDuplicateCheckRequest;
import cn.visolink.system.channel.model.vo.CustomerDuplicateCheckResponse;
import cn.visolink.system.channel.model.vo.ProjectCluesNew;
import cn.visolink.system.channel.model.vo.TaskVo;
import cn.visolink.system.channel.service.TaskService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.parameter.dao.ParameterManagementDao;
import cn.visolink.system.seniorbroker.vo.Message;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.HttpRequestUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {


  @Value("${YDACSENDOAMESSAGEURL}")
  private String sendOAMessageUrl;
  @Value("${YDACSENDOAMESSAGEAPPCODE}")
  private String sendOAMessageAppCode;
  @Value("${isSendOAMessage}")
  private int isSendOAMessage;

  @Autowired
  private MessageMapper messageMapper;
  @Autowired
  private ProjectCluesDao projectCluesDao;
  @Autowired
  private TaskDao taskDao;
  @Autowired
  private ParameterManagementDao parameterManagementDao;
  @Autowired
  private ExcelImportMapper excelImportMapper;

  @Override
  @Transactional
  public ResultBody createTask(TaskVo taskVo) {
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
              .getRequest();
              String userId=request.getHeader("userid");

              if(StringUtils.isEmpty(userId)){
                  userId=SecurityUtils.getUserId();
              }

    checkParam(taskVo);
//    if (!CollectionUtils.isEmpty(taskVo.getMemberIdList())){
//      //判断时间段内是否存在成员任务
//      Map userMap = new HashMap();
//      String memberInfo = taskVo.getMemberIdList().get(0); // 获取第一个成员信息
//      String[] memberParts = memberInfo.split("!");
//      userMap.put("memberIds",Collections.singletonList(memberParts[0]));
//      userMap.put("teamId",taskVo.getTeamId());
//      userMap.put("startTime",taskVo.getStartTime());
//      userMap.put("endTime",taskVo.getEndTime());
//      userMap.put("taskTypeId", taskVo.getTaskTypeId());
//      int count = taskDao.getUserIsOk(userMap);
//      if (count>0){
//        return ResultUtil.error(500, "创建任务失败,成员在此时间段内存在其他任务！");
//      }
//    }else{
//      //判断时间段内是否存在团队任务
//      Map userMap = new HashMap();
//      userMap.put("teamId",taskVo.getTeamId());
//      userMap.put("startTime",taskVo.getStartTime());
//      userMap.put("endTime",taskVo.getEndTime());
//      userMap.put("taskTypeId", taskVo.getTaskTypeId());
//      int count = taskDao.getTeamIsOk(userMap);
//      if (count>0){
//        return ResultUtil.error(500, "创建任务失败,团队在此时间段内存在其他任务！");
//      }
//    }

      if (StringUtils.isNotBlank(taskVo.getTaskArea())){
      //判断时间段内是否存在网格任务
      Map userMap = new HashMap();
      userMap.put("taskArea",taskVo.getTaskArea());
      userMap.put("startTime",taskVo.getStartTime());
      userMap.put("endTime",taskVo.getEndTime());
      userMap.put("taskTypeId", taskVo.getTaskTypeId());
      int count = taskDao.getTaskAreaIsOk(userMap);
      if (count>0){
        return ResultUtil.error(500, "创建任务失败,一个网格同一时期只能有一个任务！");
      }
    }
    List<Map> projectStages = parameterManagementDao.getProject(taskVo.getProjectId());
    if (!CollectionUtils.isEmpty(projectStages)) {
      taskVo.setOrgId(projectStages.get(0).get("AreaID").toString());
    }
    taskVo.setTaskTypeName(TaskTypeEnum.getNameByType(taskVo.getTaskTypeId()));
    taskVo.setId(UUID.randomUUID().toString());
    taskVo.setCreateBy(userId);
    //taskVo.setModifyBy(userId);
    taskVo.setIsDel(0);
    List<TaskMember> taskMemberList = initTaskMembers(taskVo);
    taskDao.saveZsMapTask(taskVo);
    if (CollectionUtils.isNotEmpty(taskMemberList)) {
      taskDao.saveTaskMembers(taskMemberList);
    }
      int totalCustomers=0;
      int successCount = 0;
      int failCount = 0;
    // 保存任务客户关系数据
    if (CollectionUtils.isNotEmpty(taskVo.getCustomerList())) {
    	List<TaskCustomer> taskCustomerList = initTaskCustomers(taskVo);
    	totalCustomers = taskCustomerList.size();

    	if(taskVo.getIsDupChecked()==1) {
            List<TaskCustomer> taskCustomerList1 = taskCustomerList.stream()
                    .filter(a -> "0".equals(a.getIsRepeat().toString())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(taskCustomerList1) && taskCustomerList1.size() > 0) {
                	 taskDao.saveTaskCustomers(taskCustomerList1);
                }
                successCount = taskCustomerList1 != null ? taskCustomerList1.size() : 0;
                failCount = totalCustomers - successCount;
    	}else {
    		   taskDao.saveTaskCustomers(taskCustomerList);
    		   successCount = totalCustomers;
    	}


        taskVo.setCustomerList(taskCustomerList);
        //保存任务下发节点记录
        List<ReportCustomerForm> reList = this.getReportCustomerFormList(taskVo);
        excelImportMapper.saveFollowNodeUpRecord(reList);
        // 更新b_project_rule表中的相关字段
        if (!CollectionUtils.isEmpty(taskVo.getMemberIdList())) {
            String memberInfo = taskVo.getMemberIdList().get(0); // 获取第一个成员信息
            String[] memberParts = memberInfo.split("!");
            if (memberParts.length == 4) {
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("customerIds", taskCustomerList.stream()
                        .map(TaskCustomer::getCustomerId)
                        .collect(Collectors.toList()));
//                updateMap.put("reportUserRole", "1");//需要根绝用户角色修改
//                updateMap.put("reportUserId", memberParts[0]);
//                updateMap.put("reportUserName", memberParts[1]);
//                updateMap.put("reportTeamID", memberParts[2]);
//                updateMap.put("reportTeamName", memberParts[3]);
//                updateMap.put("reportCreateTime",new Date());
                updateMap.put("salesAttributionId", memberParts[0]);
                updateMap.put("salesAttributionName", memberParts[1]);
                updateMap.put("salesAttributionGroupId", memberParts[2]);
                updateMap.put("salesAttributionGroupName", memberParts[3]);
                updateMap.put("salesAttributionTeamId", memberParts[2]);
                updateMap.put("salesAttributionTeamName", memberParts[3]);
                updateMap.put("salesAttributionTime", new Date());
                
                taskDao.updateProjectCule(updateMap);

                    Map mobileMap = messageMapper.getUserMobile(userId);
                    if (mobileMap != null) {
                        String userName = mobileMap.get("UserName")+"";
                        //发送系统消息
                        Message message = new Message();
                        message.setSubject("【地图拓客任务创建通知】");
                        message.setContent("【地图拓客任务创建通知】您的任务【"+taskVo.getTaskName()+"】已创建，请知晓。");
                        message.setSender("");
                        message.setMessageType(8001);
                        message.setIsDel(0);
                        message.setReceiver(userId);
                        message.setIsRead(0);
                        message.setIsPush(2);
                        message.setIsNeedPush(2);
                        message.setProjectClueId("");
                        message.setOpportunityClueId("");
                        message.setProjectId(taskVo.getProjectId());
                        projectCluesDao.insertOneMessage(message);

                        String content = "【地图拓客任务调整通知】您的任务【"+taskVo.getTaskName()+"】已创建，请知晓。";
                        //发送OA
                        content = content.replaceAll(" ","_");
                        if(isSendOAMessage==1){
                            HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url=&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                        }
                        //发送钉钉
                        String flowTitle="【地图拓客任务调整通知】您的任务【"+taskVo.getTaskName()+"】已创建，请知晓。";
                        try {
                            flowTitle = URLEncoder.encode(flowTitle,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        String resultD = HttpClientUtil.doPostSd("http://esb.vanyang.com.cn/dingding/corpconversation?content="+flowTitle+"&loginName="+userName+"&agentId=1942757632&title=短信验证码&sender=数字案场系统&sysName=数字案场系统&billUrl=", null);
                        System.out.println("钉钉发送="+resultD);
                    }

            }
        }else{
            List<Map> xsjls=taskDao.getTeamXsjl(taskVo);
            
            // 循环销售经理列表，给每个人发送消息
            if (!CollectionUtils.isEmpty(xsjls)) {
                for (Map xsjl : xsjls) {
                    String userIdm = String.valueOf(xsjl.get("userId"));
                    String userName = String.valueOf(xsjl.get("userName"));
                    
                    if (StringUtils.isNotEmpty(userIdm) && !"null".equals(userIdm)) {
                        //发送系统消息
                        Message message = new Message();
                        message.setSubject("【地图拓客任务通知】");
                        message.setContent("【地图拓客任务通知】您有新的地图拓客任务【"+taskVo.getTaskName()+"】，请及时处理。");
                        message.setSender("");
                        message.setMessageType(8001);
                        message.setIsDel(0);
                        message.setReceiver(userIdm);
                        message.setIsRead(0);
                        message.setIsPush(2);
                        message.setIsNeedPush(2);
                        message.setProjectClueId("");
                        message.setOpportunityClueId("");
                        message.setProjectId(taskVo.getProjectId());
                        projectCluesDao.insertOneMessage(message);
                        
                        //发送OA消息
                        String content = "【地图拓客任务通知】您有新的地图拓客任务【"+taskVo.getTaskName()+"】，请及时处理。";
                        content = content.replaceAll(" ","_");
                        if(isSendOAMessage==1){
                            HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url=&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                        }
                        
                        //发送钉钉消息
                        String flowTitle="【地图拓客任务通知】您有新的地图拓客任务【"+taskVo.getTaskName()+"】，请及时处理。";
                        try {
                            flowTitle = URLEncoder.encode(flowTitle,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        String resultD = HttpClientUtil.doPostSd("http://esb.vanyang.com.cn/dingding/corpconversation?content="+flowTitle+"&loginName="+userName+"&agentId=1942757632&title=短信验证码&sender=数字案场系统&sysName=数字案场系统&billUrl=", null);
                        System.out.println("钉钉发送="+resultD);
                    }
                }
            }
        }
        
        //taskDao.updateMapClueStatus(taskCustomerList);
    }
    
    // 构建返回信息
    Map<String, Object> resultData = new HashMap<>();
    resultData.put("taskInfo", taskVo);
    resultData.put("totalCustomers", totalCustomers);
    resultData.put("successCount", successCount);
    resultData.put("failCount", failCount);
    
    String messages="";
    if (failCount > 0) {
        messages = String.format("创建任务成功，已下发%d组客户，失败%d组（项目已存在）", successCount, failCount);
    } else {
        messages = String.format("创建任务成功，已下发%d组客户", successCount);
    }
    
    ResultBody result = new ResultBody();
    result.setCode(200);
    result.setMessages(messages);
    result.setData(resultData);
    return result;
  }

private  List<ReportCustomerForm> getReportCustomerFormList(TaskVo  taskVo){
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String employeeName="";
    String orgName="";
    if (!CollectionUtils.isEmpty(taskVo.getMemberIdList())) {
        String memberInfo = taskVo.getMemberIdList().get(0); // 获取第一个成员信息
        String[] memberParts = memberInfo.split("!");
        if (memberParts.length == 4) {
            employeeName=memberParts[1];
            orgName=memberParts[3];
        }
    }
    List<ReportCustomerForm>  lr= new ArrayList<>();
    for (TaskCustomer customer : taskVo.getCustomerList()) {
        ReportCustomerForm  reportCustomerForm = new ReportCustomerForm();
        reportCustomerForm.setProjectClueId(customer.getCustomerId());
        //设置新增节点记录
        reportCustomerForm.setFollowUpWay("任务下发");
        reportCustomerForm.setFollowUpDetail("任务下发客户");
        reportCustomerForm.setCreateDate(sf.format(new Date()));
        reportCustomerForm.setUserId(taskVo.getCreateBy());
        reportCustomerForm.setProjectId(taskVo.getProjectId());
        reportCustomerForm.setProjectName(taskVo.getProjectName());
        reportCustomerForm.setEmployeeName(employeeName);
        reportCustomerForm.setOrgName(orgName);
        lr.add(reportCustomerForm);
    }
    return lr;
}
  
  @Override
  @Transactional
  public ResultBody updateTask(TaskVo taskVo) {

      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
              .getRequest();
      String userid=request.getHeader("userid");
      if(StringUtils.isEmpty(userid)){
          userid=SecurityUtils.getUserId();
      }
      taskVo.setModifyBy(userid);
//	    if (!CollectionUtils.isEmpty(taskVo.getMemberIdList())){
//	      //判断时间段内是否存在成员任务
//	      Map userMap = new HashMap();
//	      userMap.put("id",taskVo.getId());
//	      userMap.put("memberIds",taskVo.getMemberIdList());
//	      userMap.put("teamId",taskVo.getTeamId());
//	      userMap.put("startTime",taskVo.getStartTime());
//	      userMap.put("endTime",taskVo.getEndTime());
//	      userMap.put("taskTypeId", taskVo.getTaskTypeId());
//	      int count = taskDao.getUserIsOk(userMap);
//	      if (count>0){
//	        return ResultUtil.error(500, "创建任务失败,成员在此时间段内存在其他任务！");
//	      }
//	    }else{
//	      //判断时间段内是否存在团队任务
//	      Map userMap = new HashMap();
//	      userMap.put("id",taskVo.getId());
//	      userMap.put("teamId",taskVo.getTeamId());
//	      userMap.put("startTime",taskVo.getStartTime());
//	      userMap.put("endTime",taskVo.getEndTime());
//	      userMap.put("taskTypeId", taskVo.getTaskTypeId());
//	      int count = taskDao.getTeamIsOk(userMap);
//	      if (count>0){
//	        return ResultUtil.error(500, "创建任务失败,团队在此时间段内存在其他任务！");
//	      }
//	    }
    // 执行更新操作
    taskDao.updateTask(taskVo);
    
    //发送系统消息+OA消息+钉钉消息 提示任务成员，任务信息发生变化
    if (!CollectionUtils.isEmpty(taskVo.getMemberIdList())){
    	
    	 for (String userId : taskVo.getMemberIdList()) {
    		  Map mobileMap = messageMapper.getUserMobile(userId);
    		  if (mobileMap != null) {
    			  String userName = mobileMap.get("UserName")+"";
		        //发送系统消息
		        Message message = new Message();
		        message.setSubject("【地图拓客任务调整通知】");
		        message.setContent("【地图拓客任务调整通知】您的任务【"+taskVo.getTaskName()+"】任务信息已发生变化，请知晓。");
		        message.setSender("");
		        message.setMessageType(8001);
		        message.setIsDel(0);
		        message.setReceiver(userId);
		        message.setIsRead(0);
		        message.setIsPush(2);
		        message.setIsNeedPush(2);
		        message.setProjectClueId("");
		        message.setOpportunityClueId("");
		        message.setProjectId(taskVo.getProjectId());
		        projectCluesDao.insertOneMessage(message);
        
		        String content = "【地图拓客任务调整通知】您的任务【"+taskVo.getTaskName()+"】任务信息已发生变化，请知晓。";
		        //发送OA
		        content = content.replaceAll(" ","_");
                if(isSendOAMessage==1){
                    HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url=&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                }
		        //发送钉钉
		        String flowTitle="【地图拓客任务调整通知】您的任务【"+taskVo.getTaskName()+"】任务信息已发生变化，请知晓。";
		        try {
		            flowTitle = URLEncoder.encode(flowTitle,"UTF-8");
		        } catch (UnsupportedEncodingException e) {
		            throw new RuntimeException(e);
		        }
		        String resultD = HttpClientUtil.doPostSd("http://esb.vanyang.com.cn/dingding/corpconversation?content="+flowTitle+"&loginName="+userName+"&agentId=1942757632&title=短信验证码&sender=数字案场系统&sysName=数字案场系统&billUrl=", null);
		        System.out.println("钉钉发送="+resultD);
    		  }
    	 }
    }
    
    return ResultUtil.success(taskVo);
  }
  
  @Override
  @Transactional
  public ResultBody stopTask(TaskVo taskVo) {


      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
              .getRequest();
      String userid=request.getHeader("userid");
      String userNameS=request.getHeader("username");
      if(StringUtils.isEmpty(userid)){
          userid=SecurityUtils.getUserId();
      }
      if(StringUtils.isEmpty(userNameS)){
          userNameS=SecurityUtils.getUsername();
      }
      taskVo.setModifyBy(userid);
    // 执行更新操作
    taskDao.stopTask(taskVo);

      //发送系统消息+OA消息+钉钉消息 提示任务成员，任务信息发生变化
      if (!CollectionUtils.isEmpty(taskVo.getMemberIdList())){
          for (String userId : taskVo.getMemberIdList()) {
              Map mobileMap = messageMapper.getUserMobile(userId);
              if (mobileMap != null) {
                  String userName = mobileMap.get("UserName")+"";
                  //发送系统消息
                  Message message = new Message();
                  message.setSubject("【地图拓客任务调整通知】");
                  message.setContent("【地图拓客任务终止通知】您的任务【"+taskVo.getTaskName()+"】已被【"+userNameS+"】请知晓。");
                  message.setSender("");
                  message.setMessageType(8001);
                  message.setIsDel(0);
                  message.setReceiver(userId);
                  message.setIsRead(0);
                  message.setIsPush(2);
                  message.setIsNeedPush(2);
                  message.setProjectClueId("");
                  message.setOpportunityClueId("");
                  message.setProjectId(taskVo.getProjectId());
                  projectCluesDao.insertOneMessage(message);

                  String content = "【地图拓客任务终止通知】您的任务【"+taskVo.getTaskName()+"】已被【"+userNameS+"】请知晓。";
                  //发送OA
                  content = content.replaceAll(" ","_");
                  if(isSendOAMessage==1){
                      HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url=&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                  }
                  //发送钉钉
                  String flowTitle="【地图拓客任务终止通知】您的任务【"+taskVo.getTaskName()+"】已被【"+userNameS+"】请知晓。";
                  try {
                      flowTitle = URLEncoder.encode(flowTitle,"UTF-8");
                  } catch (UnsupportedEncodingException e) {
                      throw new RuntimeException(e);
                  }
                  String resultD = HttpClientUtil.doPostSd("http://esb.vanyang.com.cn/dingding/corpconversation?content="+flowTitle+"&loginName="+userName+"&agentId=1942757632&title=短信验证码&sender=数字案场系统&sysName=数字案场系统&billUrl=", null);
                  System.out.println("钉钉发送="+resultD);
              }
          }
      }
    
    return ResultUtil.success(taskVo);
  }

  /**
   * 初始化任务客户关系数据
   * @param taskVo 任务信息
   * @return 任务客户关系列表
   */
  private List<TaskCustomer> initTaskCustomers(TaskVo taskVo) {
    List<TaskCustomer> taskCustomerList = new ArrayList<>();
    for (TaskCustomer customer : taskVo.getCustomerList()) {
      TaskCustomer taskCustomer = new TaskCustomer();
      taskCustomer.setId(UUID.randomUUID().toString());
      taskCustomer.setTaskId(taskVo.getId());
      taskCustomer.setCustomerId(customer.getCustomerId());  
      taskCustomer.setCustomerType("1");//1、任务下发2、手动录入
      taskCustomer.setCustomerName(customer.getCustomerName());
      taskCustomer.setCustomerMobile(customer.getCustomerMobile());
      taskCustomer.setCreateBy(taskVo.getCreateBy());
      taskCustomer.setModifyBy(taskVo.getModifyBy());
      taskCustomer.setIsDel(0);
      //是否与报备客户进行判重处理
      if(taskVo.getIsDupChecked()==1) {
    	  Map pMap = new HashMap();
    	  pMap.put("projectId",taskVo.getProjectId());
    	  pMap.put("customerMobile",customer.getCustomerMobile());
    	  pMap.put("customerName",customer.getCustomerName());
          int isRepeat = this.getCustomerIsRepeat(pMap);
    	  taskCustomer.setIsRepeat(isRepeat);
      }
      taskCustomerList.add(taskCustomer);
    }
    return taskCustomerList;
  }
  @Override
  public List<TaskVo> getListByIds(List<String> drawIds) {
    return taskDao.getListByIds(drawIds);
  }

  private List<TaskMember> initTaskMembers(TaskVo taskVo) {
    List<TaskMember> taskMemberList = new ArrayList<>();
      if (!CollectionUtils.isEmpty(taskVo.getMemberIdList())) {
          for (String userId : taskVo.getMemberIdList()) {
              TaskMember taskMember = new TaskMember();
              taskMember.setId(UUID.randomUUID().toString());
              taskMember.setCreateBy(taskVo.getCreateBy());
              taskMember.setModifyBy(taskVo.getModifyBy());
              taskVo.setIsDel(0);
              taskMember.setTaskId(taskVo.getId());
              taskMember.setMemberId(userId.split("!")[0]);
              taskMemberList.add(taskMember);
          }
      }
    return taskMemberList;
  }

  private void checkParam(TaskVo taskVo) {
    if (StringUtils.isEmpty(taskVo.getTaskTypeId()) || StringUtils.isEmpty(TaskTypeEnum.getNameByType(taskVo.getTaskTypeId()))) {
      throw new RuntimeException("任务类型不能为空");
    }
    if (StringUtils.isEmpty(taskVo.getTaskName())) {
      throw new RuntimeException("任务名称不能为空");
    }
    if (taskVo.getStartTime() == null) {
      throw new RuntimeException("任务开始时间不能为空");
    }
    if (taskVo.getEndTime() == null) {
      throw new RuntimeException("任务结束时间不能为空");
    }
  }
  
   private int getCustomerIsRepeat(Map map) {
    //处理项目联动
    String projectId = String.valueOf(map.get("projectId"));
    List<String> proList = new ArrayList<>();
    String proIds = projectCluesDao.getTranslateProIds(projectId);
    if(StringUtils.isNotEmpty(proIds)){
      proList = new ArrayList(Arrays.asList(proIds.split(",")));
    }
    //不管有无联动项目 保证原项目存在
    proList.add(projectId);
    map.put("proList",proList);
    //判断系统配置规则
    ProjectRuleDetail projectRuleDetailXt = projectCluesDao.selectProjectRuleZs("-1","2");
    map.put("judgeNoPool",projectRuleDetailXt.getJudgeNoPool());
    map.put("judgeNoRegion",projectRuleDetailXt.getJudgeNoRegion());
    //查询是否存在机会
    List<Map> opps = new ArrayList<>();
    opps = projectCluesDao.getCstIsOkReferral(map);

    boolean flag = false;
    for (Map m:opps) {
      int cout = Integer.parseInt(m.get("count")+"");
      if (cout>0){
        //获取重复客户 判断客户对应类型的规则 万企通走万企通配置 转介走转介保护期
        map.put("type",m.get("type")+"");
        List<Map> cusOpps = projectCluesDao.getCstIsOkRepeat(map);
        for (Map cusOpp: cusOpps) {
          Map queryMap = new HashMap();
          List<String> proList1 = new ArrayList<>();
          proList1.add(cusOpp.get("projectId")+"");
          queryMap.put("proList",proList1);
          queryMap.put("customerMobile",cusOpp.get("customerMobile")+"");
          queryMap.put("customerName",cusOpp.get("customerName")+"");
          String sourceMode = cusOpp.get("sourceMode")+"";
          if("1".equals(sourceMode)){//万企通客户
            queryMap.put("judgeNoPool",projectRuleDetailXt.getWqtJudgeNoPool());
            queryMap.put("judgeNoRegion",projectRuleDetailXt.getWqtJudgeNoRegion());
            //查询是否存在机会
            List<Map> opps1 = new ArrayList<>();
            if("0".equals(projectRuleDetailXt.getWqtJudgeStage())){
              opps1 = projectCluesDao.getCstIsOkReferral(queryMap);
            }else if("1".equals(projectRuleDetailXt.getWqtJudgeStage())){
              opps1 = projectCluesDao.getCstIsOkComeVisit(queryMap);
            }else if("2".equals(projectRuleDetailXt.getWqtJudgeStage())){
              opps1 = projectCluesDao.getCstIsOkTrade(queryMap);
            }
            for (Map m1:opps1) {
              int cout1 = Integer.parseInt(m1.get("count")+"");
              if (cout1>0){
                flag = true;
                break;
              }
            }
          }else if("2".equals(sourceMode)){//转介客户
            queryMap.put("judgeNoPool",projectRuleDetailXt.getReferralJudgeNoPool());
            queryMap.put("judgeNoRegion",projectRuleDetailXt.getReferralJudgeNoRegion());
            //查询是否存在机会
            List<Map> opps2 = new ArrayList<>();
            if("0".equals(projectRuleDetailXt.getReferralJudgeStage())){
              opps2 = projectCluesDao.getCstIsOkReferral(queryMap);
            }else if("1".equals(projectRuleDetailXt.getReferralJudgeStage())){
              opps2 = projectCluesDao.getCstIsOkComeVisit(queryMap);
            }else if("2".equals(projectRuleDetailXt.getReferralJudgeStage())){
              opps2 = projectCluesDao.getCstIsOkTrade(queryMap);
            }
            for (Map m2:opps2) {
              int cout2 = Integer.parseInt(m2.get("count")+"");
              if (cout2>0){
                flag = true;
                break;
              }
            }
          }else if("3".equals(sourceMode)){//案场客户
            flag = true;
            break;
          }
        }
      }
    }
   
    return flag?1:0;
  }

    public ResultBody getTeamXsjl(TaskVo vo) {
            return ResultUtil.success(taskDao.getTeamXsjl(vo));
    }

    @Override
    public ResultBody<CustomerDuplicateCheckResponse> checkCustomerDuplicate(CustomerDuplicateCheckRequest request) {
        try {
            // 参数校验
            if (request == null || StringUtils.isEmpty(request.getProjectId()) || 
                CollectionUtils.isEmpty(request.getCustomerList())) {
                return ResultBody.error(-1000_01, "参数不能为空");
            }

            CustomerDuplicateCheckResponse response = new CustomerDuplicateCheckResponse();
            List<CustomerDuplicateCheckResponse.CustomerDuplicateResult> customerResults = new ArrayList<>();
            
            int totalCount = request.getCustomerList().size();
            int duplicateCount = 0;
            int nonDuplicateCount = 0;

            // 遍历每个客户进行判重检查
            for (CustomerDuplicateCheckRequest.CustomerInfo customerInfo : request.getCustomerList()) {
                CustomerDuplicateCheckResponse.CustomerDuplicateResult result = 
                    new CustomerDuplicateCheckResponse.CustomerDuplicateResult();
                
                result.setCustomerId(customerInfo.getCustomerId());
                result.setCustomerName(customerInfo.getCustomerName());
                result.setCustomerMobile(customerInfo.getCustomerMobile());
                result.setCustomerNameIns(customerInfo.getCustomerNameIns()); // 透传客户名称隐号
                result.setCustomerMobileIns(customerInfo.getCustomerMobileIns()); // 透传客户手机号隐号
                result.setFlagType(customerInfo.getFlagType()); // 透传flagType参数

                // 调用原有的判重逻辑
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("projectId", request.getProjectId());
                paramMap.put("customerMobile", customerInfo.getCustomerMobile());
                paramMap.put("customerName", customerInfo.getCustomerName());
                
                int isRepeat = getCustomerIsRepeat(paramMap);
                result.setIsDuplicate(isRepeat);
                
                if (isRepeat == 1) {
                    duplicateCount++;
                    result.setDuplicateReason("该客户与现有报备客户重复");
                    // 这里可以进一步查询重复客户的详细信息
                    result.setDuplicateCustomers(getDuplicateCustomerDetails(paramMap));
                } else {
                    nonDuplicateCount++;
                    result.setDuplicateReason("该客户不重复");
                }
                
                customerResults.add(result);
            }

            response.setTotalCount(totalCount);
            response.setDuplicateCount(duplicateCount);
            response.setNonDuplicateCount(nonDuplicateCount);
            response.setCustomerResults(customerResults);

            return ResultUtil.success(response);
            
        } catch (Exception e) {
            log.error("客户判重检查失败", e);
            return ResultBody.error(-1000_01, "客户判重检查失败：" + e.getMessage());
        }
    }

    /**
     * 获取重复客户的详细信息
     * @param paramMap 查询参数
     * @return 重复客户信息列表
     */
    private List<CustomerDuplicateCheckResponse.CustomerDuplicateResult.DuplicateCustomerInfo> getDuplicateCustomerDetails(Map<String, Object> paramMap) {
        List<CustomerDuplicateCheckResponse.CustomerDuplicateResult.DuplicateCustomerInfo> duplicateCustomers = new ArrayList<>();
        
        try {
            // 处理项目联动
            String projectId = String.valueOf(paramMap.get("projectId"));
            List<String> proList = new ArrayList<>();
            String proIds = projectCluesDao.getTranslateProIds(projectId);
            if(StringUtils.isNotEmpty(proIds)){
                proList = new ArrayList(Arrays.asList(proIds.split(",")));
            }
            // 不管有无联动项目 保证原项目存在
            proList.add(projectId);
            paramMap.put("proList", proList);
            
            // 判断系统配置规则
            ProjectRuleDetail projectRuleDetailXt = projectCluesDao.selectProjectRuleZs("-1","2");
            paramMap.put("judgeNoPool", projectRuleDetailXt.getJudgeNoPool());
            paramMap.put("judgeNoRegion", projectRuleDetailXt.getJudgeNoRegion());
            
            // 查询重复客户信息
            List<Map> duplicateCustomerMaps = projectCluesDao.getCstIsOkRepeat(paramMap);
            
            for (Map customerMap : duplicateCustomerMaps) {
                CustomerDuplicateCheckResponse.CustomerDuplicateResult.DuplicateCustomerInfo duplicateInfo = 
                    new CustomerDuplicateCheckResponse.CustomerDuplicateResult.DuplicateCustomerInfo();
                
                duplicateInfo.setCustomerId(String.valueOf(customerMap.get("customerId")));
                duplicateInfo.setCustomerName(String.valueOf(customerMap.get("customerName")));
                duplicateInfo.setCustomerMobile(String.valueOf(customerMap.get("customerMobile")));
                duplicateInfo.setSourceMode(String.valueOf(customerMap.get("sourceMode")));
                duplicateInfo.setProjectId(String.valueOf(customerMap.get("projectId")));
                duplicateInfo.setProjectName(String.valueOf(customerMap.get("projectName")));
                duplicateInfo.setCreateTime(String.valueOf(customerMap.get("createTime")));
                
                // 设置来源描述
                String sourceMode = String.valueOf(customerMap.get("sourceMode"));
                switch (sourceMode) {
                    case "1":
                        duplicateInfo.setSourceModeDesc("万企通");
                        break;
                    case "2":
                        duplicateInfo.setSourceModeDesc("转介");
                        break;
                    case "3":
                        duplicateInfo.setSourceModeDesc("案场");
                        break;
                    default:
                        duplicateInfo.setSourceModeDesc("未知");
                        break;
                }
                
                duplicateCustomers.add(duplicateInfo);
            }
            
        } catch (Exception e) {
            log.error("获取重复客户详细信息失败", e);
        }
        
        return duplicateCustomers;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody<BatchDeleteDuplicateCustomerResponse> batchDeleteDuplicateCustomer(BatchDeleteDuplicateCustomerRequest request) {
        try {
            // 参数校验
            if (StringUtils.isEmpty(request.getProjectId())) {
                return ResultBody.error(-1000_01, "项目ID不能为空");
            }
            if (CollectionUtils.isEmpty(request.getCustomerIds())) {
                return ResultBody.error(-1000_01, "客户ID列表不能为空");
            }
            if (StringUtils.isEmpty(request.getOperatorId())) {
                return ResultBody.error(-1000_01, "操作人ID不能为空");
            }
            if (StringUtils.isEmpty(request.getOperatorName())) {
                return ResultBody.error(-1000_01, "操作人姓名不能为空");
            }

            BatchDeleteDuplicateCustomerResponse response = new BatchDeleteDuplicateCustomerResponse();
            List<BatchDeleteDuplicateCustomerResponse.DeleteResult> deleteResults = new ArrayList<>();
            
            int totalCount = request.getCustomerIds().size();
            int successCount = 0;
            int failCount = 0;

            // 遍历每个客户ID进行删除
            for (String customerId : request.getCustomerIds()) {
                BatchDeleteDuplicateCustomerResponse.DeleteResult result = 
                    new BatchDeleteDuplicateCustomerResponse.DeleteResult();
                result.setCustomerId(customerId);
                
                try {
                    // 删除项目线索
                    int deletedCluesCount = taskDao.batchDeleteProjectClues(
                        Arrays.asList(customerId), 
                        request.getOperatorId(), 
                        request.getOperatorName()
                    );
                    
                    // 删除跟进记录
                    int deletedFollowupCount = taskDao.batchDeleteFollowupRecords(
                        Arrays.asList(customerId), 
                        request.getOperatorId(), 
                        request.getOperatorName()
                    );
                    
                    result.setSuccess(true);
                    result.setMessage("删除成功");
                    result.setDeletedCluesCount(deletedCluesCount);
                    result.setDeletedFollowupCount(deletedFollowupCount);
                    successCount++;
                    
                } catch (Exception e) {
                    log.error("删除客户失败，客户ID: {}", customerId, e);
                    result.setSuccess(false);
                    result.setMessage("删除失败：" + e.getMessage());
                    result.setDeletedCluesCount(0);
                    result.setDeletedFollowupCount(0);
                    failCount++;
                }
                
                deleteResults.add(result);
            }

            response.setTotalCount(totalCount);
            response.setSuccessCount(successCount);
            response.setFailCount(failCount);
            response.setDeleteResults(deleteResults);

            return ResultUtil.success(response);
            
        } catch (Exception e) {
            log.error("批量删除重复客户失败", e);
            return ResultBody.error(-1000_01, "批量删除重复客户失败：" + e.getMessage());
        }
    }
}
