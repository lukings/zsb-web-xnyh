package cn.visolink.message.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.task.MultiDataSourceTimedTaskHandle;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.message.model.OverdueUnconsumedProjectRecord;
import cn.visolink.message.model.SysLog;
import cn.visolink.message.model.form.MessageForm;
import cn.visolink.message.model.form.UpdateCluesMessage;
import cn.visolink.message.service.MessageService;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.model.ClueOpportunityExtend;
import cn.visolink.system.channel.model.form.ExcelForm;
import cn.visolink.system.channel.model.vo.CustomerFodLogVo;
import cn.visolink.system.channel.model.vo.ProjectCluesNew;
import cn.visolink.system.channel.model.vo.ReferralVo;
import cn.visolink.system.channel.service.impl.ProjectCluesServiceImpl;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.openQuotation.model.OppTradeVo;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.HttpRequestUtil;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <p>
 * Message服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2019-09-03
 */
@EnableScheduling
@Service
@Slf4j
public class MessageServiceImpl implements MessageService, ApplicationContextAware {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ProjectCluesDao projectCluesDao;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private RedisUtil redisUtil;

//    @Autowired
//    private RestHighLevelClient client;//连接elasticsearch的客户端，在配置文件中已经注入
//    private static final String indexName = "test-wy-customer_add_log"; //索引名称
//    private static final String documentId = "customer_add_log_01"; //文档ID

    @Autowired
    private MultiDataSourceTimedTaskHandle multiDataSourceTimedTaskHandle;

    @Value("${RedistributioinURL}")
    private String RedisbuURL;
    @Value("${ISTOMYURL}")
    private String istomyurl;
    @Value("${SENDMYPOOLURL}")
    private String sendMyPoolUrl;
    @Value("${spring.profiles.active}")
    private String active;

    @Value("${isSendOAMessage}")
    private int isSendOAMessage;

    @Value("${YDACSENDOAMESSAGEURL}")
    private String sendOAMessageUrl;
    @Value("${YDACSENDOAMESSAGEAPPCODE}")
    private String sendOAMessageAppCode;

    @Value("${YDACFOLLOWUPPENDINGURL}")
    private String follUpPendingUrl;

    @Value("${YDACREFERRALPENDINGURL}")
    private String referralPendingUrl;

    @Value("${YDACOBTAINCSTPENDINGURL}")
    private String obtainCstPendingUrl;

    @Value("${ExcelUrl}")
    private String excelUrl;
    @Value("${DownLoadUrl}")
    private String excelDownLoadUrl;

    @Value("${outbound.uid}")
    private String uid;
    @Value("${outbound.did}")
    private String did;
    @Value("${outbound.sid}")
    private String sid;

    @Value("${outbound.channelId}")
    private String channelId;

    @Value("${outbound.secret}")
    private String secret;

    @Value("${outbound.urlOp}")
    private String urlOp;

    @Value("${outbound.deleteHD}")
    private String deleteHD;

    @Value("${outbound.getTimeoutWarning}")
    private String getTimeoutWarning;

    private static ApplicationContext context;


    private static String CACHE_LOCK2 = "MESSAGE_PUSH_LOCK_TWO";
    //过期时间
    private static int EXPIRE_PERIOD2 = (int) DateUtils.MILLIS_PER_MINUTE * 8 / 1000;

    private static String START_UP_RULE_EXPIRED_LOCK = "START_UP_RULE_EXPIRED_LOCK";
    private static int RULE_EXPIRED_PERIOD = (int) DateUtils.MILLIS_PER_MINUTE * 15 / 1000;
    private static String START_UP_RULE_ENABLE_LOCK = "START_UP_RULE_ENABLE_LOCK";
    private static int RULE_ENABLE_PERIOD = (int) DateUtils.MILLIS_PER_MINUTE * 15 / 1000;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimpleDateFormat sfDa = new SimpleDateFormat("yyyy-MM-dd");

    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private ProjectCluesServiceImpl projectCluesService;

    /**
     * 报备---签约的消息
     */
//    @Scheduled(cron = "0 0/16 5-23 * * ?")
    public void selectMessageCron() {
        try {
            Thread.sleep(new Random().nextInt(100));
            if (redisUtil.setIfNull(CACHE_LOCK2,true)) {
                //redisUtil.set(CACHE_LOCK2, true, EXPIRE_PERIOD2);
                System.out.println("报备---签约的消息 : "+ EXPIRE_PERIOD2);
                selectMessage();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }finally {
            redisUtil.del(CACHE_LOCK2);
        }

    }

    /**
     * 启动规则过期任务
     */
//    @Scheduled(cron = "0 30 4 * * ?")
    public void startUpRuleExpiredTask() {
        multiDataSourceTimedTaskHandle.taskHandle(() -> startUpRuleExpiredTaskFun());
    }

    /**
     * 启动规则过期任务功能代码
     */
    public void startUpRuleExpiredTaskFun() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(START_UP_RULE_EXPIRED_LOCK) == null) {
            redisUtil.set(START_UP_RULE_EXPIRED_LOCK, true, RULE_EXPIRED_PERIOD);
            startUpRuleExpired();
        }
    }

    /**
     * 启动规则启用任务
     */
//    @Scheduled(cron = "0 0 5 * * ?")
    public void startUpRuleEnableTask() {
        multiDataSourceTimedTaskHandle.taskHandle(() -> startUpRuleEnableTaskFun());
    }

    /**
     * 启动规则启用任务功能代码
     */
    public void startUpRuleEnableTaskFun() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (redisUtil.get(START_UP_RULE_ENABLE_LOCK) == null) {
            redisUtil.set(START_UP_RULE_ENABLE_LOCK, true, RULE_ENABLE_PERIOD);
            startUpRuleEnable();
        }
    }

    //全局list，存放需要发消息的数据
    List<MessageForm> listMessage = new ArrayList<>();

    @Override
    /*************************内渠**************************/
    @Scheduled(cron="0 0 9 * * ?")
    public void selectMessage() {
        if ("dev".equalsIgnoreCase(active) || "pre".equalsIgnoreCase(active))
            return;
        List<MessageForm> messages = new ArrayList<>();
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("过保预警信息定时任务开始");
        messageMapper.insertLogs(sysLog);
        log.info("<====================信息定时任务开始======================>");
        String date = sfDa.format(new Date());
        //查询案场满足可以发送---跟进预警----的消息
        List<Map> mapListAcFollowWorning = messageMapper.selectOpportunityFollowWarning(date);
        if (mapListAcFollowWorning!=null && mapListAcFollowWorning.size()>0){
            for (int i = 0; i < mapListAcFollowWorning.size(); i++) {
                //该条数据的机会id
                String OpportunityClueId = mapListAcFollowWorning.get(i).get("OpportunityClueId") + "";
                //该条数据的线索id
                String ProjectClueId = mapListAcFollowWorning.get(i).get("ProjectClueId")+"";
                String SalesAttributionId = mapListAcFollowWorning.get(i).get("SalesAttributionId")+"";
                String projectId = mapListAcFollowWorning.get(i).get("projectId")+"";
                String CustomerName = mapListAcFollowWorning.get(i).get("CustomerName")+"";
                String SalesFollowExpireDate = mapListAcFollowWorning.get(i).get("SalesFollowExpireDate")+"";
                MessageForm messageForm = new MessageForm();
                messageForm.setSubject("过保预警");
                messageForm.setContent("您的客户:"+CustomerName+",即将过保，请及时跟进");
                messageForm.setProjectId(projectId);
                messageForm.setProjectClueId(ProjectClueId);
                messageForm.setOpportunityClueId(OpportunityClueId);
                messageForm.setReceiver(SalesAttributionId);
                messageForm.setMessageType(2002);
                messageForm.setIsPush("2");
                messageForm.setIsRead("0");
                messages.add(messageForm);
//                //查询手机号
                Map map = messageMapper.getUserMobile(SalesAttributionId);
                if (map != null && !StringUtils.isEmpty(map.get("Mobile")+"")){
                    //发送短信
                    try {
                        Date date1 = sf.parse(SalesFollowExpireDate);
                        Date data2 = new Date();
                        int days = differentDaysByMillisecond(data2,date1);
                        String time = sfDa.format(date1);
                        String sss = URLEncoder.encode(CustomerName+"|"+days+"|"+time,"UTF-8");
                        String content = "您的客户:"+CustomerName+",即将过保，请及时跟进";
                        String userName = map.get("UserName")+"";
                        //发送短信改为发送OA
                        content = content.replaceAll(" ","_");
                        if(isSendOAMessage==1){
                            HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url=&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (messages.size() > 0) {
            int size = messages.size();
            //每次插入一千条
            int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<MessageForm> fformList = messages.subList(s * 1000, size);
                    messageMapper.insertMessage(fformList);
                } else {
                    List<MessageForm> fformList = messages.subList(s * 1000, (s + 1) * 1000);
                    messageMapper.insertMessage(fformList);
                }
            }
        }
        SysLog sysLog0 = new SysLog();
        sysLog0.setExecutTime(sf.format(new Date()));
        sysLog0.setTaskName("过保预警信息定时任务结束");
        sysLog0.setNote("需要发送的消息有" + messages.size() + "条");
        messageMapper.insertLogs(sysLog0);
        log.info("<====================信息定时任务结束======================>");
    }

    @Override
    public void deleteHD() {
        List<String> idHd = messageMapper.getIdHd();
        Map<String, Object> stringListHashMap = new HashMap<>();
        stringListHashMap.put("cid",idHd);
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU0NjI4NjQsIm5iZiI6MTczNTQ2Mjg2NCwiZXhwIjoxNzM1NTA2MDY0LCJkYXRhIjp7InVzZXJfaWQiOjE2NjYsImNvbXBhbnlfaWQiOjU0NSwiZnJvbV9pZCI6MH19.7VPo4-peKQQAH8y7w7ayAftg-nIu6FY78z75i4IPUl0";
            String s = HttpClientUtil.postHttpOutbound("https://ydacuat.vanyang.com.cn/call/api/crm.Leads/dele", token, stringListHashMap);
            System.out.println(s);

    }

    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

    /**
     * 启动规则过期任务
     */
    @Override
    public void startUpRuleExpired() {
        Map map = new HashMap();
        List<Map> ruleExpiredList = messageMapper.getExpiredRule(map);
        if (ruleExpiredList.size() > 0) {
            for (int i = 0; i < ruleExpiredList.size(); i++) {
                Map map1 = new HashMap();
                map1.put("isEnable", "0");
                map1.put("activityId", ruleExpiredList.get(i).get("ActivityId"));
                map1.put("isReport", "0");
                map1.put("projectID", ruleExpiredList.get(i).get("ProjectID"));
                messageMapper.updateBrokerRule(map1);
                messageMapper.updateBuildBook(map1);
            }
        }
    }

    /**
     * 启动规则启用任务
     */
    @Override
    public void startUpRuleEnable() {
        Map map = new HashMap();
        String startDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00");
        String endDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        List<Map> enableRuleList = messageMapper.getEnableRule(map);
        if (enableRuleList.size() > 0) {
            for (int i = 0; i < enableRuleList.size(); i++) {
                Map map1 = new HashMap();
                map1.put("isEnable", "1");
                map1.put("activityId", enableRuleList.get(i).get("ActivityId"));
                map1.put("isReport", "1");
                map1.put("projectID", enableRuleList.get(i).get("ProjectID"));
                map1.put("enableDate", enableRuleList.get(i).get("EnableDate"));
                map1.put("endDate", enableRuleList.get(i).get("EndDate"));
                messageMapper.updateBrokerRule(map1);
                messageMapper.updateBuildBook(map1);
            }
        }
    }
    @Override
    @Scheduled(cron = "0 0 5 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void getTimeoutWarning() {

        //获取外呼token
//        Map<String, Object> map1 = new HashMap<>();
//        map1.put("uid", uid);
//        map1.put("did", did);
//        map1.put("sid", sid);
//        map1.put("secret", secret);
//        map1.put("channel_id", channelId);
//        String s1 = HttpClientUtil.postHttpOutbound(urlOp, null, map1);
//        JSONObject jsonObject = JSONObject.parseObject(s1);
//        JSONObject data = jsonObject.getJSONObject("data");
//        String token1 = data.get("token") + "";

        //查询外呼接口超时未消费的项目

        String getTproject = HttpClientUtil.postHttpOutbound(getTimeoutWarning, null, null);
        // 解析接口返回的JSON
        JSONObject getTprojectData = JSONObject.parseObject(getTproject);

// 注意：这里将getJSONObject改为getJSONArray，因为data是数组类型
        JSONArray list = getTprojectData.getJSONArray("data");
        List<OverdueUnconsumedProjectRecord> records = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            JSONObject item = list.getJSONObject(i);
            OverdueUnconsumedProjectRecord record = new OverdueUnconsumedProjectRecord();

            // 注意：根据实际 JSON 字段类型，可能需要调整转换方式
            record.setAccountId(item.getInteger("account_id"));
            record.setCompanyId(item.getInteger("company_id"));
            record.setProjectName(item.getString("project_name"));
            record.setProjectId(item.getString("project_id"));
            record.setMoney(item.getBigDecimal("money"));
            record.setRemark(item.getString("remark"));
            record.setIsDisable(item.getInteger("is_disable"));
            record.setIsDelete(item.getInteger("is_delete"));
            record.setCreateUid(item.getInteger("create_uid"));
            record.setUpdateUid(item.getInteger("update_uid"));
            record.setDeleteUid(item.getInteger("delete_uid"));
            record.setCreateTime(item.getString("create_time")); // 假设 DateUtil 是自定义的日期解析工具类
            record.setUpdateTime(item.getString("update_time"));
            record.setDeleteTime(item.getString("delete_time"));
            record.setNotCallDay(item.getInteger("not_call_day"));
            record.setOverNotCallDay(item.getInteger("over_not_call_day"));
            record.setIsRead(0);
            records.add(record);
        }

        //清空超时未消费信息
        messageMapper.deleteTimeoutWarning();
        if (!records.isEmpty()) {
            messageMapper.batchInsert(records);
        }



    }
    @Override
    @Scheduled(cron = "0 0/30 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void addProPool() {

        if ("dev".equalsIgnoreCase(active) || "pre".equalsIgnoreCase(active))
            return;
        boolean flag = false;
        if (redisUtil.setIfNull("addProPool","1",21600)){
            flag = true;
        }
            if (flag) {
                List<MessageForm> messages = new ArrayList<>();
                SysLog sysLog = new SysLog();
                sysLog.setStartTime(sf.format(new Date()));
                sysLog.setTaskName("过保定时任务开始");
                messageMapper.insertLogs(sysLog);
                log.info("<====================过保定时任务开始======================>");

                //需要作废的机会
                List<String> oppIds = new ArrayList<>();
                List<String> clueIds = new ArrayList<>();
                List<Map> addProList = new ArrayList<>();
                List<Map> clueList = new ArrayList<>();
                //查询已经过保的机会
                List<Map> mapListAcFollowExpire = messageMapper.selectOpportunityFollowExpire();
                if (mapListAcFollowExpire != null && mapListAcFollowExpire.size() > 0) {
                    for (int i = 0; i < mapListAcFollowExpire.size(); i++) {
                        Map ac = mapListAcFollowExpire.get(i);
                        //判断客户类型
                        String cstType = ac.get("cstType") + "";
                        //判断是否需要作废
                        String isToMy = ac.get("IsReferralOk") + "";
                        //该条数据的机会id
                        String OpportunityClueId = ac.get("OpportunityClueId") + "";
                        if ("1".equals(isToMy)) {
                            oppIds.add(OpportunityClueId);
                            clueIds.add(ac.get("ProjectClueId") + "");
                        } else {
                            String SalesFollowExpireDate = ac.get("SalesFollowExpireDate") + "";
                            //该条数据的线索id
                            String ProjectClueId = ac.get("ProjectClueId") + "";
                            String SalesAttributionId = ac.get("SalesAttributionId") + "";
                            String projectId = ac.get("projectId") + "";
                            //查询项目是否区域项目
                            String isRegion = messageMapper.getIsRegionByPro(projectId);
                            if ("1".equals(isRegion)) {
                                ac.put("PoolType", "2");
                                ac.put("add_region_time", new Date());
                            } else {
                                ac.put("PoolType", "1");
                                ac.put("add_pro_time", new Date());
                            }
                            ac.put("AddType", "1");
                            MessageForm messageForm = new MessageForm();
                            messageForm.setSubject("跟进逾期");
                            messageForm.setContent("您的客户:" + ac.get("CustomerName") + ",已经过保，请知悉");
                            messageForm.setProjectId(projectId);
                            messageForm.setProjectClueId(ProjectClueId);
                            messageForm.setOpportunityClueId(OpportunityClueId);
                            messageForm.setReceiver(SalesAttributionId);
                            messageForm.setMessageType(2102);
                            messageForm.setIsPush("2");
                            messageForm.setIsRead("0");
                            messages.add(messageForm);
                            if ("clue".equals(cstType)) {
                                clueList.add(ac);
                            } else {
                                addProList.add(ac);
                            }
//                    //查询手机号
                            Map map = messageMapper.getUserMobile(SalesAttributionId);
                            if (map != null && !StringUtils.isEmpty(map.get("Mobile") + "")) {
                                //发送短信
                                try {
                                    Date date1 = sf.parse(SalesFollowExpireDate);
                                    String time = sfDa.format(date1);
                                    String sss = URLEncoder.encode(ac.get("CustomerName") + "|" + time, "UTF-8");
                                    String content = "您的客户:" + ac.get("CustomerName") + ",已经过保，请知悉";
                                    String userName = map.get("UserName") + "";
                                    //发送短信改为发送OA
                                    content = content.replaceAll(" ", "_");
                                    if (isSendOAMessage == 1) {
                                        HttpRequestUtil.httpGet(sendOAMessageUrl + "?method=unmsg&content=" + content + "&url=&h5url=&noneBindingReceiver=" + userName + "&appcode=" + sendOAMessageAppCode + "&sysName=移动案场系统", false);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                List<Map> mapListAcFollowExpire1 = new ArrayList<>();
                //插入公共池
                if (addProList.size() > 0) {
                    if (addProList.size() > 0) {
                        mapListAcFollowExpire1.addAll(addProList);
                        int size = addProList.size();
                        //每次插入一千条
                        int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                        for (int s = 0; s < count; s++) {
                            if (s == count - 1) {
                                List<Map> fformList = addProList.subList(s * 1000, size);
                                messageMapper.addProList(fformList);
                            } else {
                                List<Map> fformList = addProList.subList(s * 1000, (s + 1) * 1000);
                                messageMapper.addProList(fformList);
                            }
                        }
                    }
                }
                //分配线索进入公客池
                if (clueList.size() > 0) {
                    if (clueList.size() > 0) {
                        int size = clueList.size();
                        //每次插入一千条
                        int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                        for (int s = 0; s < count; s++) {
                            if (s == count - 1) {
                                List<Map> fformList = clueList.subList(s * 1000, size);
                                messageMapper.addProList(fformList);
                            } else {
                                List<Map> fformList = clueList.subList(s * 1000, (s + 1) * 1000);
                                messageMapper.addProList(fformList);
                            }
                        }
                    }
                }
                Map<String, Object> map1 = new HashMap<>();
                map1.put("uid", uid);
                map1.put("did", did);
                map1.put("sid", sid);
                map1.put("secret", secret);
                map1.put("channel_id", channelId);
                String s1 = HttpClientUtil.postHttpOutbound(urlOp, null, map1);
                JSONObject jsonObject = JSONObject.parseObject(s1);
                JSONObject data = jsonObject.getJSONObject("data");
                String token1 = data.get("token") + "";
                //如果有需要作废的机会
                if (oppIds.size() > 0) {
                    messageMapper.updateZFopps(oppIds);
                    //处理关联客户
                    messageMapper.delCusRelateZFopps(oppIds);

                    //删除话单--转介客户
                    Map<String, Object> mapId = new HashMap<>();
                    mapId.put("cid", clueIds);
                    HttpClientUtil.postHttpOutbound(deleteHD, token1, mapId);
                }

                //更新机会业务员为空
                if (mapListAcFollowExpire1 != null && mapListAcFollowExpire1.size() > 0) {
                    messageMapper.delSalesOpp(mapListAcFollowExpire1);
                    //处理关联客户
                    messageMapper.delCusRelateOpps(mapListAcFollowExpire1);

                    //删除话单--报备客户
                    List<String> projectClueIds = mapListAcFollowExpire1.stream()
                            .map(map -> map.get("ProjectClueId")) // 获取 key 为 "ProjectClueId" 的值
                            .filter(Objects::nonNull) // 过滤空值
                            .map(Object::toString)  // 显式地将 Object 转换为 String
                            .collect(Collectors.toList());
                    Map<String, Object> mapId = new HashMap<>();
                    mapId.put("cid", projectClueIds);
                    HttpClientUtil.postHttpOutbound(deleteHD, token1, mapId);
                }
                //更新分配线索业务员为空
                if (clueList != null && clueList.size() > 0) {
                    messageMapper.delSalesClue(clueList);
                    //删除话单--线索客户
                    List<String> projectClueIds = clueList.stream()
                            .map(map -> map.get("ProjectClueId")) // 获取 key 为 "ProjectClueId" 的值
                            .filter(Objects::nonNull) // 过滤空值
                            .map(Object::toString)  // 显式地将 Object 转换为 String
                            .collect(Collectors.toList());
                    Map<String, Object> mapId = new HashMap<>();
                    mapId.put("cid", projectClueIds);
                    HttpClientUtil.postHttpOutbound(deleteHD, token1, mapId);
                }

                if (messages.size() > 0) {
                    int size = messages.size();
                    //每次插入一千条
                    int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                    for (int s = 0; s < count; s++) {
                        if (s == count - 1) {
                            List<MessageForm> fformList = messages.subList(s * 1000, size);
                            messageMapper.insertMessage(fformList);
                        } else {
                            List<MessageForm> fformList = messages.subList(s * 1000, (s + 1) * 1000);
                            messageMapper.insertMessage(fformList);
                        }
                    }
                }
                SysLog sysLog0 = new SysLog();
                sysLog0.setExecutTime(sf.format(new Date()));
                sysLog0.setTaskName("跟进逾期信息定时任务结束");
                sysLog0.setNote("需要发送的消息有" + messages.size() + "条");
                messageMapper.insertLogs(sysLog0);
                log.info("<====================信息定时任务结束======================>");
                redisUtil.del("addProPool");
            }

    }
    //短信发送参考
    public static void main(String[] args){
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sss = URLEncoder.encode("张三|2|2022-8-20","UTF-8");
            System.out.println(sss);
//            HttpRequestUtil.httpGet("http://esb.vanyang.com.cn/sms/sendSMSMessageV1?sys_code=ydac&login_name=ydac&password=123456&modeId=000012&vars="+sss+"&mobile=18201285195&sendTime&notifyUrl=http://www.baidu.com&sysName=招商宝",false);
//            Date date1 = sf.parse("2022-9-19 9:07:00");
//            Date data2 = new Date();
//            int days = differentDaysByMillisecond(data2,date1);
//            System.out.println(days);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Scheduled(cron="0 0 1 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public String addAreaPool() {
        if ("dev".equalsIgnoreCase(active) || "pre".equalsIgnoreCase(active))
            return "";
        //使用ApplicationContextAware,它实现了这个接口的bean，当spring容器初始化的时候，会自动的将ApplicationContext注入进来
        MessageMapper messageMapper = (MessageMapper) this.getBean("messageMapper");
        //获取集团参数
        List<Map> addAreaParam = messageMapper.getAddAreaParam();
        Date today = new Date();
        if (addAreaParam==null){
            return "参数未配置！！";
        }else{
            Map ProParam = new HashMap();
            for (Map all:addAreaParam) {
                //判断规则类型 （2：项目业务员）
               String type = all.get("type")+"";
                if ("2".equals(type)){
                    ProParam = all;
                }
            }
            //项目参数集合
            List<Map> proAreaParams = new ArrayList<>();
            //区域参数集合
            List<Map> areaParams = new ArrayList<>();

            //项目所属区域ID
            Map comMap = new HashMap();
            //没配置规则的项目
            List<String> proIds = new ArrayList<>();
            //没配置规则的区域
            List<String> areaIds = new ArrayList<>();
            //获取所有项目池客户
            List<Map> proPoolCsts = messageMapper.getProPoolCst();
            //过期需要掉入区域池的ID
            List<String> updatePros = new ArrayList<>();
            //项目客户池处理
            if (proPoolCsts!=null && proPoolCsts.size()>0){
                for (Map cst:proPoolCsts) {
                    String projectId = cst.get("projectId")+"";
                    String ComGUID = "";
                    if (comMap.get(projectId)==null){
                        ComGUID = messageMapper.getComGUIDByProject(projectId);// 区域ID
                        comMap.put(projectId,ComGUID);
                    }else{
                        ComGUID = comMap.get(projectId)+"";
                    }
                    String id = cst.get("id")+"";
                    String time = cst.get("time")+"";//入池时间
                    String paramTime = "";//过期天数
                    //根据项目查询规则
                    if (proIds.contains(projectId) && areaIds.contains(ComGUID)){
                        //项目没配置规则,区域也没有配置 则取集团规则
                        paramTime = ProParam.get("time")+"";
                    }else{
                        if (!proIds.contains(projectId)){
                            //获取项目参数 是否已查询过
                            for (Map pro:proAreaParams) {
                                String projectIdParam = pro.get("projectId")+"";
                                String typeParam = pro.get("type")+"";
                                String timeParam = pro.get("time")+"";
                                if (projectId.equals(projectIdParam) && "2".equals(typeParam)){
                                    paramTime = timeParam;
                                    break;
                                }
                            }
                            //如果未查询到参数
                            if (StringUtils.isEmpty(paramTime)){
                                //查询项目参数
                                List<Map> proAreaParam = messageMapper.getProAddAreaParam(projectId);
                                if (proAreaParam!=null && proAreaParam.size()>0){
                                    for (Map pro:proAreaParam) {
                                        String typeParam = pro.get("type")+"";
                                        String timeParam = pro.get("time")+"";
                                        if ("2".equals(typeParam)){
                                            paramTime = timeParam;
                                            break;
                                        }
                                    }
                                    proAreaParams.addAll(proAreaParam);
                                }else{
                                    proIds.add(projectId);

                                }
                            }
                        }
                        //判断是否存在区域
                        if("null".equals(ComGUID) || StringUtils.isEmpty(ComGUID)){
                            //未获取到区域 则取集团规则
                            paramTime = ProParam.get("time")+"";
                        }
                        //如果项目参数未查询到 获取区域参数
                        if (StringUtils.isEmpty(paramTime)){
                            //判断区域参数是否已获取
                            for (Map pro:areaParams) {
                                String projectIdParam = pro.get("projectId")+"";
                                String typeParam = pro.get("type")+"";
                                String timeParam = pro.get("time")+"";
                                if (ComGUID.equals(projectIdParam) && "2".equals(typeParam)){
                                    paramTime = timeParam;
                                    break;
                                }
                            }
                            if (StringUtils.isEmpty(paramTime)){
                                //查询区域参数
                                List<Map> areaParam = messageMapper.getProAddAreaParam(ComGUID);
                                if (areaParam!=null && areaParam.size()>0){
                                    for (Map pro:areaParam) {
                                        String typeParam = pro.get("type")+"";
                                        String timeParam = pro.get("time")+"";
                                        if ("2".equals(typeParam)){
                                            paramTime = timeParam;
                                            break;
                                        }
                                    }
                                    areaParams.addAll(areaParam);
                                }else{
                                    areaIds.add(ComGUID);
                                    paramTime = ProParam.get("time")+"";
                                }
                            }
                        }
                    }
                    try {
                        if (StringUtils.isNotEmpty(paramTime)){
                            //判断是否过期
                            Date addDate = sf.parse(time);
                            Calendar calendar=Calendar.getInstance();
                            calendar.setTime(addDate);
                            calendar.add(Calendar.DAY_OF_MONTH, +Integer.parseInt(paramTime));
                            Date gqDate = calendar.getTime();
                            if (today.getTime()>=gqDate.getTime()){
                                updatePros.add(id);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //如果有过期的掉入区域池
                if (updatePros.size()>0){
                    int size = updatePros.size();
                    //每次插入一千条
                    int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                    for (int s = 0; s < count; s++) {
                        if (s == count - 1) {
                            List<String> fformList = updatePros.subList(s * 1000, size);
                            messageMapper.addAreaPool(fformList);
                        } else {
                            List<String> fformList = updatePros.subList(s * 1000, (s + 1) * 1000);
                            messageMapper.addAreaPool(fformList);
                        }
                    }
                }
            }
            return "处理完成！！";
        }
    }

    @Override
    @Scheduled(cron="0 0 1 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public String addNationalPool() {
        if ("dev".equalsIgnoreCase(active) || "pre".equalsIgnoreCase(active))
            return "";
        //使用ApplicationContextAware,它实现了这个接口的bean，当spring容器初始化的时候，会自动的将ApplicationContext注入进来
        MessageMapper messageMapper = (MessageMapper) this.getBean("messageMapper");
        //获取集团参数
        List<Map> addNationalParam = messageMapper.getAddNationalParam();
        Date today = new Date();
        if (addNationalParam==null){
            return "参数未配置！！";
        }else{
            Map ProParam = new HashMap();
            for (Map all:addNationalParam) {
                //判断规则类型 （2：项目业务员）
                String type = all.get("type")+"";
                if ("2".equals(type)){
                    ProParam = all;
                }
            }
            //项目参数集合
            List<Map> proAreaParams = new ArrayList<>();
            //区域参数集合
            List<Map> areaParams = new ArrayList<>();

            //项目所属区域ID
            Map comMap = new HashMap();
            //没配置规则的项目
            List<String> proIds = new ArrayList<>();
            //没配置规则的区域
            List<String> areaIds = new ArrayList<>();

            //获取所有区域池客户
            List<Map> proPoolCsts = messageMapper.getAreaPoolCst();
            //过期需要掉入全国池的ID
            List<String> updatePros = new ArrayList<>();
            //项目客户池处理
            if (proPoolCsts!=null && proPoolCsts.size()>0){
                for (Map cst:proPoolCsts) {
                    String projectId = cst.get("projectId")+"";
                    String ComGUID = "";
                    if (comMap.get(projectId)==null){
                        ComGUID = messageMapper.getComGUIDByProject(projectId);// 区域ID
                        comMap.put(projectId,ComGUID);
                    }else{
                        ComGUID = comMap.get(projectId)+"";
                    }
                    String id = cst.get("id")+"";
                    String time = cst.get("time")+"";//入池时间
                    String paramTime = "";//过期天数

                    //根据项目查询规则
                    if (proIds.contains(projectId) && areaIds.contains(ComGUID)){
                        //项目没配置规则,区域也没有配置 则取集团规则
                        paramTime = ProParam.get("time")+"";
                    }else{
                        if (!proIds.contains(projectId)){
                            //获取项目参数 是否已查询过
                            for (Map pro:proAreaParams) {
                                String projectIdParam = pro.get("projectId")+"";
                                String typeParam = pro.get("type")+"";
                                String timeParam = pro.get("time")+"";
                                if (projectId.equals(projectIdParam) && "2".equals(typeParam)){
                                    paramTime = timeParam;
                                    break;
                                }
                            }
                            //如果未查询到参数
                            if (StringUtils.isEmpty(paramTime)){
                                //查询项目参数
                                List<Map> proAreaParam = messageMapper.getProAddNationalParam(projectId);
                                if (proAreaParam!=null && proAreaParam.size()>0){
                                    for (Map pro:proAreaParam) {
                                        String typeParam = pro.get("type")+"";
                                        String timeParam = pro.get("time")+"";
                                        if ("2".equals(typeParam)){
                                            paramTime = timeParam;
                                            break;
                                        }
                                    }
                                    proAreaParams.addAll(proAreaParam);
                                }else{
                                    proIds.add(projectId);

                                }
                            }
                        }
                        //判断是否存在区域
                        if("null".equals(ComGUID) || StringUtils.isEmpty(ComGUID)){
                            //未获取到区域 则取集团规则
                            paramTime = ProParam.get("time")+"";
                        }
                        //如果项目参数未查询到 获取区域参数
                        if (StringUtils.isEmpty(paramTime)){
                            //判断区域参数是否已获取
                            for (Map pro:areaParams) {
                                String projectIdParam = pro.get("projectId")+"";
                                String typeParam = pro.get("type")+"";
                                String timeParam = pro.get("time")+"";
                                if (ComGUID.equals(projectIdParam) && "2".equals(typeParam)){
                                    paramTime = timeParam;
                                    break;
                                }
                            }
                            if (StringUtils.isEmpty(paramTime)){
                                //查询区域参数
                                List<Map> areaParam = messageMapper.getProAddNationalParam(ComGUID);
                                if (areaParam!=null && areaParam.size()>0){
                                    for (Map pro:areaParam) {
                                        String typeParam = pro.get("type")+"";
                                        String timeParam = pro.get("time")+"";
                                        if ("2".equals(typeParam)){
                                            paramTime = timeParam;
                                            break;
                                        }
                                    }
                                    areaParams.addAll(areaParam);
                                }else{
                                    areaIds.add(ComGUID);
                                    paramTime = ProParam.get("time")+"";
                                }
                            }
                        }
                    }
                    try {
                        if (StringUtils.isNotEmpty(paramTime)){
                            //判断是否过期
                            Date addDate = sf.parse(time);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(addDate);
                            calendar.add(Calendar.DAY_OF_MONTH, +Integer.parseInt(paramTime));
                            Date gqDate = calendar.getTime();
                            if (today.getTime() >= gqDate.getTime()) {
                                updatePros.add(id);
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //如果有过期的掉入全国池
                if (updatePros.size()>0){
                    int size = updatePros.size();
                    //每次插入一千条
                    int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                    for (int s = 0; s < count; s++) {
                        if (s == count - 1) {
                            List<String> fformList = updatePros.subList(s * 1000, size);
                            messageMapper.addNationalPool(fformList);
                        } else {
                            List<String> fformList = updatePros.subList(s * 1000, (s + 1) * 1000);
                            messageMapper.addNationalPool(fformList);
                        }
                    }

                }
            }
            return "处理完成！！";
        }
    }

    @Override
//    @Scheduled(cron = "0 0 23 28-31 * ?")
    public void monthlyDelCst() {
        final Calendar c = Calendar.getInstance();
        //如果是最后一天 则执行定时任务
        if (c.get(Calendar.DATE) == c.getActualMaximum(Calendar.DATE)) {
            //获取集团参数
            List<Map> addParam = messageMapper.getAllCstParam("-1");
            Map proAllParam = new HashMap();
            Map SHZsParam = new HashMap();
            Map BJZsParam = new HashMap();
            Map GZZsParam = new HashMap();
            Map SZZsParam = new HashMap();
            for (Map all:addParam) {
                //判断规则类型 （2：项目业务员 3上海招商 4北京招商 5广州招商 6深圳招商）
                String type = all.get("type")+"";
                if ("3".equals(type)){
                    SHZsParam = all;
                }else if ("4".equals(type)){
                    BJZsParam = all;
                }else if ("5".equals(type)){
                    GZZsParam = all;
                }else if ("6".equals(type)){
                    SZZsParam = all;
                }else if ("2".equals(type)){
                    proAllParam = all;
                }
            }

            //获取所有上线项目
            List<String> proIds = messageMapper.getAllProIds();
            //没配置规则的项目
            List<String> notIds = new ArrayList<>();
            //项目参数集合
            List<Map> proAreaParams = new ArrayList<>();
            if (proIds!=null && proIds.size()>0){
                for (String id:proIds) {
                    List<Map> proParam = messageMapper.getAllCstParam(id);
                    if (proParam!=null && proParam.size()>0){
                        proAreaParams.addAll(proParam);
                    }else{
                        notIds.add(id);
                    }
                }

                //处理项目数据
                for (String id:proIds) {
                    List<Map> addProList = new ArrayList<>();
                    List<Map> addZsList = new ArrayList<>();
                    //判断是否未配置规则
                    if (!notIds.contains(id)){
                        for (Map m:proAreaParams) {
                            String projectIdp = m.get("projectId")+"";
                            if (projectIdp.equals(id)){
                                String type = m.get("type")+"";
                                if ("3".equals(type)){
                                    SHZsParam = m;
                                }else if ("4".equals(type)){
                                    BJZsParam = m;
                                }else if ("5".equals(type)){
                                    GZZsParam = m;
                                }else if ("6".equals(type)){
                                    SZZsParam = m;
                                }else if ("2".equals(type)){
                                    proAllParam = m;
                                }
                            }
                        }
                    }
                    //查询个人客户数量，身份
                    List<Map> cstList = messageMapper.getProCstlist(id);
                    if(cstList!=null && cstList.size()>0){
                        for (Map cst:cstList) {
                            String SalesAttributionId = cst.get("SalesAttributionId")+"";
                            int count = Integer.parseInt(cst.get("count")+"");
                            String JobCode = cst.get("JobCode")+"";
                            //'zygw', 'BJZS', 'SHZS', 'GZZS', 'SZZS'
                            if ("zygw".equals(JobCode)){
                                String maxCount = proAllParam.get("monthReportMax")+"";
                                if (!"0".equals(maxCount)){
                                    int maxCountI = Integer.parseInt(maxCount);
                                    //如果现有客户数大于月最大数 丢失客户
                                    if (count>maxCountI){
                                        //需要丢失的客户数
                                        int limit = count-maxCountI;
                                        Map params = new HashMap();
                                        params.put("SalesAttributionId",SalesAttributionId);
                                        params.put("limit",limit);
                                        params.put("projectId",id);
                                        //查询需要丢失的客户信息
                                        List<Map> delCstList = messageMapper.getDelCstList(params);
                                        addProList.addAll(delCstList);
                                    }
                                }
                            }else if ("BJZS".equals(JobCode)){
                                String maxCount = BJZsParam.get("monthReportMax")+"";
                                String poolType = BJZsParam.get("poolType")+"";
                                if (!"0".equals(maxCount)){
                                    int maxCountI = Integer.parseInt(maxCount);
                                    //如果现有客户数大于月最大数 丢失客户
                                    if (count>maxCountI){
                                        //需要丢失的客户数
                                        int limit = count-maxCountI;
                                        Map params = new HashMap();
                                        params.put("SalesAttributionId",SalesAttributionId);
                                        params.put("limit",limit);
                                        params.put("projectId",id);
                                        //查询需要丢失的客户信息
                                        List<Map> delCstList = messageMapper.getDelCstList(params);
                                        if ("1".equals(poolType)){
                                            addProList.addAll(delCstList);
                                        }else{
                                            addZsList.addAll(delCstList);
                                        }
                                    }
                                }
                            }else if ("SHZS".equals(JobCode)){
                                String maxCount = SHZsParam.get("monthReportMax")+"";
                                String poolType = SHZsParam.get("poolType")+"";
                                if (!"0".equals(maxCount)){
                                    int maxCountI = Integer.parseInt(maxCount);
                                    //如果现有客户数大于月最大数 丢失客户
                                    if (count>maxCountI){
                                        //需要丢失的客户数
                                        int limit = count-maxCountI;
                                        Map params = new HashMap();
                                        params.put("SalesAttributionId",SalesAttributionId);
                                        params.put("limit",limit);
                                        params.put("projectId",id);
                                        //查询需要丢失的客户信息
                                        List<Map> delCstList = messageMapper.getDelCstList(params);
                                        if ("1".equals(poolType)){
                                            addProList.addAll(delCstList);
                                        }else{
                                            addZsList.addAll(delCstList);
                                        }
                                    }
                                }
                            }else if ("GZZS".equals(JobCode)){
                                String maxCount = GZZsParam.get("monthReportMax")+"";
                                String poolType = GZZsParam.get("poolType")+"";
                                if (!"0".equals(maxCount)){
                                    int maxCountI = Integer.parseInt(maxCount);
                                    //如果现有客户数大于月最大数 丢失客户
                                    if (count>maxCountI){
                                        //需要丢失的客户数
                                        int limit = count-maxCountI;
                                        Map params = new HashMap();
                                        params.put("SalesAttributionId",SalesAttributionId);
                                        params.put("limit",limit);
                                        params.put("projectId",id);
                                        //查询需要丢失的客户信息
                                        List<Map> delCstList = messageMapper.getDelCstList(params);
                                        if ("1".equals(poolType)){
                                            addProList.addAll(delCstList);
                                        }else{
                                            addZsList.addAll(delCstList);
                                        }
                                    }
                                }
                            }else if ("SZZS".equals(JobCode)){
                                String maxCount = SZZsParam.get("monthReportMax")+"";
                                String poolType = SZZsParam.get("poolType")+"";
                                if (!"0".equals(maxCount)){
                                    int maxCountI = Integer.parseInt(maxCount);
                                    //如果现有客户数大于月最大数 丢失客户
                                    if (count>maxCountI){
                                        //需要丢失的客户数
                                        int limit = count-maxCountI;
                                        Map params = new HashMap();
                                        params.put("SalesAttributionId",SalesAttributionId);
                                        params.put("limit",limit);
                                        params.put("projectId",id);
                                        //查询需要丢失的客户信息
                                        List<Map> delCstList = messageMapper.getDelCstList(params);
                                        if ("1".equals(poolType)){
                                            addProList.addAll(delCstList);
                                        }else{
                                            addZsList.addAll(delCstList);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    List<Map> mapListAcFollowExpire = new ArrayList<>();
                    //插入公共池
                    if (addProList.size()>0){
                        for (Map mmm:addProList) {
                            mmm.put("AddType","3");
                            //判断是否需要推送明源
                            String isToMy = mmm.get("IsToMy")+"";
                            //该条数据的机会id
                            String OpportunityClueId = mmm.get("OpportunityClueId") + "";
                            if ("1".equals(isToMy)){
                                try{
                                    //调用接口推送明源
                                    HttpRequestUtil.httpGet(istomyurl+"?opportunityClueId="+OpportunityClueId+"&type=3",false);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        }

                        //剔除招商转介的客户
                        for (int i = 0; i < addProList.size(); i++) {
                            Map map = addProList.get(i);
                            String ReportUserRole = map.get("ReportUserRole")+"";
                            if ("2".equals(ReportUserRole)
                                    ||"5".equals(ReportUserRole)
                                    ||"6".equals(ReportUserRole)
                                    ||"7".equals(ReportUserRole)){
                                if ("2".equals(ReportUserRole)){
                                    //上海招商的只作废
                                    mapListAcFollowExpire.add(map);
                                }else{
                                    String projectIdOld = "";
                                    String projectNameOld = "";
                                    Map pzj = messageMapper.getOppZSZJ(map);
                                    if (pzj==null){
                                        pzj = messageMapper.getOppZSZJQZ(map);
                                    }
                                    projectIdOld = pzj.get("projectId")+"";
                                    projectNameOld = pzj.get("projectName")+"";
                                    map.put("projectId",projectIdOld);
                                    map.put("projectName",projectNameOld);
                                    //更新业务员为招商业务员
                                    messageMapper.updateOppSales(map);
                                }
                                addProList.remove(i);
                                i--;
                            }
                        }
                        if (addProList.size()>0){
                            mapListAcFollowExpire.addAll(addProList);
                            int size = addProList.size();
                            //每次插入一千条
                            int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                            for (int s = 0; s < count; s++) {
                                if (s == count - 1) {
                                    List<Map> fformList = addProList.subList(s * 1000, size);
                                    messageMapper.addProList(fformList);
                                } else {
                                    List<Map> fformList = addProList.subList(s * 1000, (s + 1) * 1000);
                                    messageMapper.addProList(fformList);
                                }
                            }
                        }
                    }

                    //插入招商池
                    if (addZsList.size()>0){
                        for (Map mmm:addZsList) {
                            mmm.put("AddType","3");
                        }
                        mapListAcFollowExpire.addAll(addZsList);
                        int size = addZsList.size();
                        //每次插入一千条
                        int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                        for (int s = 0; s < count; s++) {
                            if (s == count - 1) {
                                List<Map> fformList = addZsList.subList(s * 1000, size);
                                messageMapper.addZsList(fformList);
                            } else {
                                List<Map> fformList = addZsList.subList(s * 1000, (s + 1) * 1000);
                                messageMapper.addZsList(fformList);
                            }
                        }
                    }
                    //更新机会业务员为空
                    if (mapListAcFollowExpire!=null && mapListAcFollowExpire.size()>0){
                        messageMapper.delSalesOpp(mapListAcFollowExpire);
                    }
                }
            }
        }
    }

    @Scheduled(cron="0 */30 * * * ?")
    public void automaticRejectionMain() {
        if ("dev".equalsIgnoreCase(active) || "pre".equalsIgnoreCase(active))
            return;
        this.automaticRejection();
        this.automaticFollowUpRejection();
        this.automaticObtainApprove();
        this.automaticSimilarCustomerReportRejection();
    }

    @Scheduled(cron="0 0 4 * * ?")
    public void automaticTomomorrowMain() {
        this.synCustomerReportRecord();
    }

    @Override
//    @Scheduled(cron="0 */30 * * * ?")
    public void automaticRejection() {
//        if ("uat".equalsIgnoreCase(active) || "dev".equalsIgnoreCase(active))
//            return;
//        messageMapper.automaticRejection();
        List<ReferralVo> list = messageMapper.getReferCusRejection();
        List<String> ids = new ArrayList<>();
        List<MessageForm> messages = new ArrayList<>();
        list.stream().forEach(referralVo->{
            //发送消息处理
            String customerName = referralVo.getCustomerName();
            MessageForm messageForm = new MessageForm();
            messageForm.setSubject("【驳回通知】");
            messageForm.setContent("【驳回通知】【" + customerName + "】转介已被驳回,驳回原因：超时自动驳回,请知悉。");
            messageForm.setSender("");
            messageForm.setMessageType(500103);
            messageForm.setIsDel(0);
            messageForm.setReceiver(referralVo.getReferralUserID());
            messageForm.setIsRead("0");
            messageForm.setIsPush("2");
            messageForm.setIsNeedPush(2);
            messageForm.setProjectClueId(referralVo.getProjectClueId());
            messageForm.setOpportunityClueId(referralVo.getOpportunityClueId());
            messageForm.setProjectId(referralVo.getReceiverProjectId());
            messages.add(messageForm);
            ids.add(referralVo.getId());

            //推送已办 OA消息推送 专员 区域专员 营销 总监
            List<String> jobList = messageMapper.getReferralPendingJobCode(referralVo.getReceiverProjectId(),referralVo.getReceiverUserID());
            String content = messageMapper.getPendingTitle("2",referralVo.getId());
            //获取审批人的登录名
            String userName = messageMapper.getUserNameById(referralVo.getReceiverUserID());
            if(StringUtils.isNotEmpty(userName)){
                jobList.stream().forEach(x->{
                    String url = "";
                    try {
                        url = URLEncoder.encode(referralPendingUrl+"?id="+referralVo.getId()+"&type=2&jobCode="+x, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    if(isSendOAMessage==1) {
                        HttpRequestUtil.httpGet(sendOAMessageUrl + "?method=unmsg&content=" + content + "&url=" + url + "&h5url=&noneBindingReceiver=" + userName + "&appcode=" + sendOAMessageAppCode + "&sysName=移动案场系统", false);
                    }
               });
            }
        });
        //自动驳回
        if (ids.size()>0){
            if (ids.size() > 0){
                int size = ids.size();
                //每次操作一千条
                int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                for (int s = 0; s < count; s++) {
                    if (s == count - 1) {
                        List<String> fformList = ids.subList(s * 1000, size);
                        messageMapper.automaticRejectionByIds(fformList);
                    } else {
                        List<String> fformList = ids.subList(s * 1000, (s + 1) * 1000);
                        messageMapper.automaticRejectionByIds(fformList);
                    }
                }
            }
        }
        //发送消息
        if (messages.size() > 0) {
            int size = messages.size();
            //每次插入一千条
            int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<MessageForm> fformList = messages.subList(s * 1000, size);
                    messageMapper.insertMessage(fformList);
                } else {
                    List<MessageForm> fformList = messages.subList(s * 1000, (s + 1) * 1000);
                    messageMapper.insertMessage(fformList);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron="0 0 2 * * ?")
    public void saveCustomerPoolDateLabel() {
//        if ("uat".equalsIgnoreCase(active) || "dev".equalsIgnoreCase(active))
//            return;
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("设置公客池客户逾期标签定时开始");
        messageMapper.insertLogs(sysLog);
        //获取公客池客户
        List<Map> poolCsts = messageMapper.getAllPoolCst();
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        //创建三个集合存储需要设置各自标签的数据
        List<String> dateFPoolCstList = new ArrayList<>();//逾期半年内
        List<String> dateSPoolCstList = new ArrayList<>();//逾期半年至一年
        List<String> dateTPoolCstList = new ArrayList<>();//逾期一年以上
        //遍历公客池 根据公客池类型 判断入池时间 设置标签 逾期半年内 逾期半年至一年 逾期一年以上
        poolCsts.stream().forEach(x->{
            if(StringUtils.isNotEmpty(String.valueOf(x.get("time")))){
                // 将输入的日期解析为 LocalDate
                LocalDate compareDate = LocalDate.parse(String.valueOf(x.get("time")));
                // 计算两个日期之间的差距
                Period period = Period.between(compareDate, currentDate);
                // 判断时间距离
                if (period.getYears() > 1) {
                    System.out.println("时间距离现在一年以上。");
                    dateTPoolCstList.add(String.valueOf(x.get("id")));
                } else if (period.getYears() == 1 || (period.getYears() == 0 && period.getMonths() >= 6)) {
                    System.out.println("时间距离现在半年到一年。");
                    dateSPoolCstList.add(String.valueOf(x.get("id")));
                } else {
                    System.out.println("时间距离现在半年内。");
                    dateFPoolCstList.add(String.valueOf(x.get("id")));
                }
            }
        });
        //批量更新标签
        if(!CollectionUtils.isEmpty(dateFPoolCstList)){
            messageMapper.updateCustomerPoolDateLabel(dateFPoolCstList,"1");
        }
        if(!CollectionUtils.isEmpty(dateSPoolCstList)){
            messageMapper.updateCustomerPoolDateLabel(dateSPoolCstList,"2");
        }
        if(!CollectionUtils.isEmpty(dateTPoolCstList)){
            messageMapper.updateCustomerPoolDateLabel(dateTPoolCstList,"3");
        }
        SysLog sysLog1 = new SysLog();
        sysLog1.setExecutTime(sf.format(new Date()));
        sysLog1.setTaskName("设置公客池客户逾期标签定时结束");
        messageMapper.insertLogs(sysLog1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(cron="0 */30 * * * ?")
    public void automaticObtainApprove() {
//        if ("uat".equalsIgnoreCase(active) || "dev".equalsIgnoreCase(active))
//            return;
//        messageMapper.automaticObtainApprove();
        List<ReferralVo> list = messageMapper.getObtainApproveRejection();
        List<String> ids = new ArrayList<>();
        List<MessageForm> messages = new ArrayList<>();
        list.stream().forEach(referralVo->{
            //发送消息处理
            MessageForm messageForm = new MessageForm();
            messageForm.setSubject("【审核驳回通知】");
            messageForm.setContent("【公客池获取客户审核驳回通知】【" + referralVo.getCustomerName() + "】公客池客户的获取审核已被驳回，驳回原因：超时自动驳回，请知悉。");
            messageForm.setSender("");
            messageForm.setMessageType(500103);
            messageForm.setIsDel(0);
            messageForm.setReceiver(referralVo.getSalesAttributionId());
            messageForm.setIsRead("0");
            messageForm.setIsPush("2");
            messageForm.setIsNeedPush(2);
            messageForm.setProjectClueId(referralVo.getProjectClueId());
            messageForm.setOpportunityClueId(referralVo.getOpportunityClueId());
            messageForm.setProjectId(referralVo.getProjectId());
            messages.add(messageForm);
            ids.add(referralVo.getId());

            //推送已办 OA消息推送
            List<String> jobList =Stream.of("zszj").collect(Collectors.toList());
            List<String> nextApprovalUser = messageMapper.getNextApprovalUserList(referralVo.getProjectId(),jobList,null);
            String content = messageMapper.getPendingTitle("3",referralVo.getId());
            String url = "";
            try {
                url = URLEncoder.encode(obtainCstPendingUrl+"?id="+referralVo.getId()+"&type=2&jobCode="+jobList.get(0), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            String finalUrl = url;
            nextApprovalUser.stream().forEach(x->{
                //获取审批人的登录名
                String userName = messageMapper.getUserNameById(x);
                if(StringUtils.isNotEmpty(userName) && isSendOAMessage==1){
                    HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url="+ finalUrl +"&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                }
            });
        });
        //自动驳回
        if (ids.size()>0){
            if (ids.size() > 0){
                int size = ids.size();
                //每次操作一千条
                int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                for (int s = 0; s < count; s++) {
                    if (s == count - 1) {
                        List<String> fformList = ids.subList(s * 1000, size);
                        messageMapper.automaticObtainApproveByIds(fformList);
                    } else {
                        List<String> fformList = ids.subList(s * 1000, (s + 1) * 1000);
                        messageMapper.automaticObtainApproveByIds(fformList);
                    }
                }
            }
        }
        //发送消息
        if (messages.size() > 0) {
            int size = messages.size();
            //每次插入一千条
            int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<MessageForm> fformList = messages.subList(s * 1000, size);
                    messageMapper.insertMessage(fformList);
                } else {
                    List<MessageForm> fformList = messages.subList(s * 1000, (s + 1) * 1000);
                    messageMapper.insertMessage(fformList);
                }
            }
        }
    }

    @Override
//    @Scheduled(cron="0 */30 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void automaticFollowUpRejection() {
        List<ReferralVo> list = messageMapper.getFollowUpPendingRejection();
        List<String> ids = new ArrayList<>();
        List<MessageForm> messages = new ArrayList<>();
        list.stream().forEach(referralVo->{
            //发送消息处理
            MessageForm messageForm = new MessageForm();
            messageForm.setSubject("【审核驳回通知】");
            messageForm.setContent("【跟进审核驳回通知】【" + referralVo.getCustomerName() + "】客户的跟进审核已被驳回，驳回原因：超时自动驳回，请知悉。");
            messageForm.setSender("");
            messageForm.setMessageType(500103);
            messageForm.setIsDel(0);
            messageForm.setReceiver(referralVo.getSalesAttributionId());
            messageForm.setIsRead("0");
            messageForm.setIsPush("2");
            messageForm.setIsNeedPush(2);
            messageForm.setProjectClueId(referralVo.getProjectClueId());
            messageForm.setOpportunityClueId(referralVo.getOpportunityClueId());
            messageForm.setProjectId(referralVo.getProjectId());
            messages.add(messageForm);
            ids.add(referralVo.getId());

            //推送已办 OA消息推送
            List<String> nextApprovalUser = new ArrayList<>();
            List<String> jobList = new ArrayList<>();
            if("1".equals(referralVo.getStatus())){
                if("1".equals(referralVo.getFollowUpUserRole())){
                    jobList = Stream.of("xsjl").collect(Collectors.toList());
                    nextApprovalUser = messageMapper.getNextApprovalUserList(referralVo.getProjectId(),jobList,referralVo.getOpportunityClueId());
                }else if("2".equals(referralVo.getFollowUpUserRole())){
                    jobList = Stream.of("qyxsjl").collect(Collectors.toList());
                    nextApprovalUser = messageMapper.getNextApprovalUserList(referralVo.getProjectId(),jobList,referralVo.getOpportunityClueId());
                }else {
                    jobList = Stream.of("zszj").collect(Collectors.toList());
                    nextApprovalUser = messageMapper.getNextApprovalUserList(referralVo.getProjectId(),jobList,null);
                }
            }else if("4".equals(referralVo.getStatus())){
                jobList = Stream.of("yxjl").collect(Collectors.toList());
                nextApprovalUser = messageMapper.getNextApprovalUserList(referralVo.getChildProjectId(),jobList,null);
            }else if("5".equals(referralVo.getStatus())){
                jobList = Stream.of("yxjl","zszj").collect(Collectors.toList());
                nextApprovalUser = messageMapper.getNextApprovalUserList(referralVo.getChildProjectId(),jobList,null);
            }
            String content = messageMapper.getPendingTitle("1",referralVo.getId());
            String url = "";
            try {
                url = URLEncoder.encode(follUpPendingUrl+"?id="+referralVo.getId()+"&type=2&jobCode="+jobList.get(0), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            String finalUrl = url;
            nextApprovalUser.stream().forEach(x->{
                //获取审批人的登录名
                String userName = messageMapper.getUserNameById(x);
                if(StringUtils.isNotEmpty(userName) && isSendOAMessage==1){
                    HttpRequestUtil.httpGet(sendOAMessageUrl+"?method=unmsg&content="+content+"&url="+ finalUrl +"&h5url=&noneBindingReceiver="+userName+"&appcode="+sendOAMessageAppCode+"&sysName=移动案场系统",false);
                }
            });
        });
        //自动驳回
        if (ids.size()>0){
            if (ids.size() > 0){
                int size = ids.size();
                //每次操作一千条
                int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                for (int s = 0; s < count; s++) {
                    if (s == count - 1) {
                        List<String> fformList = ids.subList(s * 1000, size);
                        messageMapper.automaticFollowUpPendingByIds(fformList);
                    } else {
                        List<String> fformList = ids.subList(s * 1000, (s + 1) * 1000);
                        messageMapper.automaticFollowUpPendingByIds(fformList);
                    }
                }
            }
        }
        //发送消息
        if (messages.size() > 0) {
            int size = messages.size();
            //每次插入一千条
            int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<MessageForm> fformList = messages.subList(s * 1000, size);
                    messageMapper.insertMessage(fformList);
                } else {
                    List<MessageForm> fformList = messages.subList(s * 1000, (s + 1) * 1000);
                    messageMapper.insertMessage(fformList);
                }
            }
        }
    }

    @Override
//    @Scheduled(cron="0 */30 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void automaticSimilarCustomerReportRejection() {
        List<ReferralVo> list = messageMapper.getSimilarCustomerReportPendingRejection();
        List<String> ids = new ArrayList<>();
        List<String> oppIds = new ArrayList<>();
        List<MessageForm> messages = new ArrayList<>();
        list.stream().forEach(referralVo->{
            //发送消息处理
            MessageForm messageForm = new MessageForm();
            messageForm.setSubject("【报备客户通知】");
            messageForm.setContent("【报备客户通知】您的客户:【" + referralVo.getCustomerName() + "】被驳回，审批意见为：超时自动驳回，请知悉！");
            messageForm.setSender("");
            messageForm.setMessageType(500103);
            messageForm.setIsDel(0);
            messageForm.setReceiver(referralVo.getSalesAttributionId());
            messageForm.setIsRead("0");
            messageForm.setIsPush("2");
            messageForm.setIsNeedPush(2);
            messageForm.setProjectClueId(referralVo.getProjectClueId());
            messageForm.setOpportunityClueId(referralVo.getOpportunityClueId());
            messageForm.setProjectId(referralVo.getProjectId());
            messages.add(messageForm);
            ids.add(referralVo.getId());
            oppIds.add(referralVo.getOpportunityClueId());
        });
        //自动驳回
        if (ids.size()>0){
            if (ids.size() > 0){
                int size = ids.size();
                //每次操作一千条
                int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                for (int s = 0; s < count; s++) {
                    if (s == count - 1) {
                        List<String> fformList = ids.subList(s * 1000, size);
                        messageMapper.automaticSimilarCustomerReportPendingByIds1(fformList);
                    } else {
                        List<String> fformList = ids.subList(s * 1000, (s + 1) * 1000);
                        messageMapper.automaticSimilarCustomerReportPendingByIds1(fformList);
                    }
                }
            }
        }
        if (oppIds.size()>0){
            if (oppIds.size() > 0){
                int size = oppIds.size();
                //每次操作一千条
                int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
                for (int s = 0; s < count; s++) {
                    if (s == count - 1) {
                        List<String> fformList = oppIds.subList(s * 1000, size);
                        messageMapper.automaticSimilarCustomerReportPendingByIds2(fformList);
                    } else {
                        List<String> fformList = oppIds.subList(s * 1000, (s + 1) * 1000);
                        messageMapper.automaticSimilarCustomerReportPendingByIds2(fformList);
                    }
                }
            }
        }
        //发送消息
        if (messages.size() > 0) {
            int size = messages.size();
            //每次插入一千条
            int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<MessageForm> fformList = messages.subList(s * 1000, size);
                    messageMapper.insertMessage(fformList);
                } else {
                    List<MessageForm> fformList = messages.subList(s * 1000, (s + 1) * 1000);
                    messageMapper.insertMessage(fformList);
                }
            }
        }
    }

    @Override
    @Scheduled(cron="0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void saveCustomerOppStatistics() {
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("报备客户客户统计定时开始");
        messageMapper.insertLogs(sysLog);
        List<Map> udpList = new ArrayList<>();
        //获取所有在保的报备客户
        List<Map> oppList = messageMapper.getAllOppCst();
        oppList.stream().forEach(x->{
            //获取客户id
            String opportunityClueId = x.get("opportunityClueId")+"";
            //获取客户的跟进信息
            Map followUpInfo = messageMapper.getFollowUpStatistics(opportunityClueId);
            udpList.add(followUpInfo);
        });
        if(!CollectionUtils.isEmpty(udpList)){
            int size = udpList.size();
            //每次插入一千条
            int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<Map> fformList = udpList.subList(s * 1000, size);
                    messageMapper.updateCustomerOppStatistics(fformList);
                } else {
                    List<Map> fformList = udpList.subList(s * 1000, (s + 1) * 1000);
                    messageMapper.updateCustomerOppStatistics(fformList);
                }
            }
        }
        SysLog sysLog1 = new SysLog();
        sysLog1.setExecutTime(sf.format(new Date()));
        sysLog1.setTaskName("报备客户客户统计定时结束");
        messageMapper.insertLogs(sysLog1);
    }

    @Override
    @Scheduled(cron="0 0 3 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void saveCustomerPoolStatistics() {
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("公客池客户统计定时开始");
        messageMapper.insertLogs(sysLog);
        List<Map> udpList = new ArrayList<>();
        //获取公客池未捞取的客户
        List<Map> poolList = messageMapper.getAllPoolCst();
        poolList.stream().forEach(x->{
            //获取客户id
            String opportunityClueId = x.get("opportunityClueId")+"";
            String id = x.get("id")+"";
            //获取客户的跟进信息
            Map followUpInfo = messageMapper.getFollowUpStatistics(opportunityClueId);
            followUpInfo.put("id",id);
            udpList.add(followUpInfo);
        });
        if(!CollectionUtils.isEmpty(udpList)){
            int size = udpList.size();
            //每次插入一千条
            int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
            for (int s = 0; s < count; s++) {
                if (s == count - 1) {
                    List<Map> fformList = udpList.subList(s * 1000, size);
                    messageMapper.updateCustomerPoolStatistics(fformList);
                } else {
                    List<Map> fformList = udpList.subList(s * 1000, (s + 1) * 1000);
                    messageMapper.updateCustomerPoolStatistics(fformList);
                }
            }
        }
        SysLog sysLog1 = new SysLog();
        sysLog1.setExecutTime(sf.format(new Date()));
        sysLog1.setTaskName("公客池客户统计定时结束");
        messageMapper.insertLogs(sysLog1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synCustomerReportRecord() {
        messageMapper.truncateCustomerReportRecord();
        messageMapper.saveCustomerReportRecord();
    }

    @Override
//    @Scheduled(cron="0 1 * * * ?")
    public String createExcelDownLoad() {
        if(redisUtil.setIfNull("CREATE_EXCEL_LOCK",true)){
            try {
                //查询任务数据
                List<ExcelExportLog> excelExportLogList = excelImportMapper.getExcelLog();
                if (!CollectionUtils.isEmpty(excelExportLogList)){
                    excelExportLogList.stream().forEach(map->{
                        if(map.getSubType().equals("CE1")){
                            ExcelForm projectCluesForm = new ExcelForm();
                            projectCluesForm = JSONObject.parseObject(map.getDoSql(),ExcelForm.class);
//                            BeanUtils.copyProperties(params,projectCluesForm);
                            ExcelExportLog excelExportLog = new ExcelExportLog();
                            BeanUtils.copyProperties(map,excelExportLog);

                            List<Map> fileds = projectCluesForm.getFileds();
                            fileds = fileds.stream().sorted((o1, o2) -> ((Integer) o1.get("sortNum")).compareTo((Integer) o2.get("sortNum"))).collect(Collectors.toList());

                            List<String> filedCodes = new ArrayList<>();
                            List<String> filedNames = new ArrayList<>();
                            for (Map filed : fileds) {
                                filedCodes.add(filed.get("filedCode")+"");
                                filedNames.add(filed.get("filedName")+"");
                            }

                            String userId = map.getCreator();
                            List<ProjectCluesNew> projectCluesNewList = new ArrayList<>();
                            if (projectCluesForm.getSearch() != null && !"".equals(projectCluesForm.getSearch())){
                                String search = projectCluesForm.getSearch();
                                //判断是否为数字（为数字匹配电话号码/身份证，不是匹配姓名）
                                if (search.matches("[0-9]+")) {
                                    projectCluesForm.setCustomerMobile(search);
                                } else {
                                    projectCluesForm.setCustomerName(search);
                                }
                            }
                            //导出的文档下面的名字
                            String excelName = map.getSubTypeDesc();
                            ArrayList<Object[]> dataset = new ArrayList<>();
                            String[] headers = null;
                            projectCluesNewList = projectCluesDao.courtCase(projectCluesForm);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String time1 = format.format(new Date());
                            if (!CollectionUtils.isEmpty(projectCluesNewList)) {
                                //报备链路
                                List<Map> rpList = new ArrayList<>();
                                if(StringUtils.isEmpty(projectCluesForm.getOwnerUserId())){
                                    rpList = projectCluesDao.getReportList(projectCluesNewList);
                                }
                                for (ProjectCluesNew projectCluesNew : projectCluesNewList) {
                                    //剩余天数需要计算
                                    if(StringUtils.isEmpty(projectCluesNew.getSalesFollowExpireDate()) || "签约".equals(projectCluesNew.getClueStatus())){
                                        projectCluesNew.setSalesFollowExpireDate("无");
                                        projectCluesNew.setRemainingDays("永久");
                                    }else {
                                        String time2 = projectCluesNew.getSalesFollowExpireDate();
                                        Integer remainingDays = null;
                                        try {
                                            remainingDays = projectCluesService.dateDifference(time1, time2);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        projectCluesNew.setRemainingDays(String.valueOf(remainingDays));
                                    }
                                    //信息完善度
                                    projectCluesNew.setPerfectionProgress(projectCluesService.getPerfectionProgress(projectCluesNew));
                                    //报备链路
                                    if(!CollectionUtils.isEmpty(rpList)){
                                        List<Map> cList = rpList.stream().filter(x->x.get("opportunityClueId").equals(projectCluesNew.getOpportunityClueId())).collect(Collectors.toList());
                                        List<Map> reList = new ArrayList<>();
                                        final Boolean[] ff = {false};
                                        if(!CollectionUtils.isEmpty(cList)){
                                            cList.stream().forEach(x->{
                                                if(ff[0]){
                                                    Map reMap = new HashMap<>();
                                                    reMap.put("salesAttributionName",x.get("reportUserName"));
                                                    reMap.put("reportCreateTime",x.get("reportCreateTime"));
                                                    reMap.put("opportunityClueId",UUID.randomUUID().toString());
                                                    reMap.put("rowKey",x.get("opportunityClueId"));
                                                    reMap.put("type","ret");
                                                    reList.add(reMap);
                                                }
                                                ff[0] = true;
                                            });
                                        }
                                        projectCluesNew.setChildren(reList);
                                    }
                                }
                            }
                            if (projectCluesNewList != null && projectCluesNewList.size() > 0) {
                                String isAllStr = projectCluesForm.getIsAll();
                                boolean isAll = true;
                                boolean isHasChild = true;
                                if ("1".equals(isAllStr)) isAll = false;
//                            headers = projectCluesNewList.get(0).courtCaseTitle2;
                                headers = filedNames.toArray(new String[0]);
                                int rowNum = 1;
                                for (ProjectCluesNew model : projectCluesNewList) {
                                    model.setRownum(rowNum);
                                    if (CollectionUtils.isEmpty(model.getChildren())) {
                                        isHasChild = false;
                                    } else {
                                        isHasChild = true;
                                    }
                                    Object[] oArray = model.toData2(isAll, filedCodes, isHasChild);
                                    dataset.add(oArray);
                                    rowNum++;
                                }
                                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                                String fileName = null;
                                try {
                                    fileName = excelExportUtil.exportExcelCreate(excelName, headers, dataset, null, excelUrl);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                excelExportLog.setFileName(fileName);
                                excelExportLog.setDownLoadUrl(excelDownLoadUrl + fileName);
                                excelExportLog.setExportStatus("2");
                                excelImportMapper.updateExcelStatus(excelExportLog);
                            }
                        }else {
                            SysLog sysLog1 = new SysLog();
                            sysLog1.setExecutTime(sf.format(new Date()));
                            sysLog1.setTaskName("导出excel异常");
                            sysLog1.setNote("任务ID未查询到：");
                            messageMapper.insertLogs(sysLog1);
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
                return "导出excel异常";
            }finally {
                redisUtil.del("CREATE_EXCEL_LOCK");
            }
        }else {
            return "正在执行任务！！";
        }
        return "调用成功！！";
    }

    @Override
    @Scheduled(cron="59 59 23 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void delExcelFile() {
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("删除excel定时开始");
        messageMapper.insertLogs(sysLog);
        try{
            String time = sf.format(new Date());
            List<String> names = excelImportMapper.selectNeedDelLog(time);
            if (names!=null && names.size()>0){
                for (String name:names) {
                    File file = new File("/app/netdata/excel/"+name);
                    if (file!=null){
                        file.delete();
                    }
                }
            }
            excelImportMapper.delLogs(time);
            SysLog sysLog1 = new SysLog();
            sysLog1.setExecutTime(sf.format(new Date()));
            sysLog1.setTaskName("删除excel成功！");
            messageMapper.insertLogs(sysLog1);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            SysLog sysLog1 = new SysLog();
            sysLog1.setExecutTime(sf.format(new Date()));
            sysLog1.setTaskName("删除异常");
            sysLog1.setNote(e.getMessage());
            messageMapper.insertLogs(sysLog1);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String initHistoryDate1130() {
        //路乾乾 初始化项目数据上线 内容包含客户等级变化表 客户新增日志记录表 客户跟进交易日志记录表
        String res = "初始化成功";
        //初始化三个表数据
//        messageMapper.truncateCustomerHistoryDate1130();
        //客户等级变化表 初始化数据获取客户主表的客户等级和客户更新时间
//        messageMapper.initCustomerLevelRecordHistoryDate1130();
        //客户新增日志记录表 初始化数据获取获取客户主表的数据 出班数据 是否新增设置为新增
//        messageMapper.initCustomerAddLogHistoryDate1130();
        //客户跟进交易日志记录表 客户跟进成交表的数据 同步跟进表的审核通过的普通跟进信息 保存审核通过的跟进记录 包含跟进方式 跟进时间 项目id 主项目id 是否三个一跟进 是否首拜 是否首访 是否签约后到访
//        messageMapper.initCustomerFollowLogHistoryDate1130();
        //客户跟进交易日志记录表 客户跟进成交表的数据 同步客户签约记录表 查询机会客户的签约记录 周期类型和周期时间 是否成交 判断客户是否存在激活状态的交易记录 按照客户ID 和 签约时间分组 最早的交易记录保存签约时间 并且用于和客户的创建时间比对 获取成交周期 再有签约记录 记作复购(复购周期是否可以按照第二次签约到最新一次签约时间的天数 除去签约的次数计算)
        //获取交易表所有客户ID
        List<String> tradeOppIds = messageMapper.getTradeCustomerIds();
        //循环客户ID 获取每个客户的交易信息 按照时间正序排列
        tradeOppIds.stream().forEach(oppId -> {
            List<OppTradeVo> oppTradeList = messageMapper.getTradeCustomerListByOppId(oppId);
            oppTradeList.stream().forEach(oppTradeVo -> {
                //首先判断 当前交易之前是否存在其他交易记录
                OppTradeVo beforeTrade = messageMapper.getCustomerBeforeTrade(oppId,oppTradeVo.getContractDate());
                //如果没有 计入成交周期 判断客户报备时间到签约时间 计算成交周期
                if(beforeTrade==null){
                    try {
                        LocalDate date2 = LocalDate.parse(sfDa.format(sfDa.parse(oppTradeVo.getContractDate())), df);
                        //集团成交周期 获取客户最早的报备时间计算
                        String reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(oppId,"1",null);
                        LocalDate date1 = LocalDate.parse(sfDa.format(sfDa.parse(reportCreateTime)), df);
                        long groupCycle =  ChronoUnit.DAYS.between(date1, date2);
                        //判断项目是否存在
                        String projectName = projectMapper.getProjectNameByProjectId(oppTradeVo.getProjectId());
                        long areaCycle = 0L;
                        long projectCycle = 0L;
                        if(StringUtils.isNotEmpty(projectName)){
                            //区域成交周期 获取客户在区域下所有项目的最早报备时间计算
                            reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(oppId,"2",oppTradeVo.getProjectId());
                            date1 = LocalDate.parse(sfDa.format(sfDa.parse(reportCreateTime)), df);
                            areaCycle =  ChronoUnit.DAYS.between(date1, date2);
                            //项目获取客户在项目下最早的报备时间计算
                            reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(oppId,"3",oppTradeVo.getProjectId());
                            date1 = LocalDate.parse(sfDa.format(sfDa.parse(reportCreateTime)), df);
                            projectCycle =  ChronoUnit.DAYS.between(date1, date2);
                        }
                        //招商组获取客户在招商团队最早的报备时间计算
                        long teamCycle = 0L;
                        if(StringUtils.isNotEmpty(oppTradeVo.getSalesAttributionTeamId())){
                            reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(oppId,"4",oppTradeVo.getSalesAttributionTeamId());
                            date1 = LocalDate.parse(sfDa.format(sfDa.parse(reportCreateTime)), df);
                            teamCycle =  ChronoUnit.DAYS.between(date1, date2);
                        }
                        //专员周期获取最早的报备时间计算
                        long userCycle = 0L;
                        if(StringUtils.isNotEmpty(oppTradeVo.getSalesAttributionId())){
                            reportCreateTime = messageMapper.getCustomerFirstReportCreateTime(oppId,"5",oppTradeVo.getSalesAttributionId());
                            date1 = LocalDate.parse(sfDa.format(sfDa.parse(reportCreateTime)), df);
                            userCycle =  ChronoUnit.DAYS.between(date1, date2);
                        }
                        //保存记录进入客户跟进交易日志记录表
                        CustomerFodLogVo customerFodLogVo = new CustomerFodLogVo();
                        customerFodLogVo.setAddLogId(projectCluesDao.getCustomerAddLogIdByOpportunityClueId(oppId));
                        customerFodLogVo.setOpportunityClueId(oppId);
                        customerFodLogVo.setProjectClueId(oppTradeVo.getProjectClueId());
                        customerFodLogVo.setBusinessId(oppTradeVo.getTradeGUID());
                        customerFodLogVo.setBusinessType(Objects.equals(oppTradeVo.getTradeStatus(), "激活") ? "6" : "7");
                        customerFodLogVo.setBusinessTime(oppTradeVo.getContractDate());
                        customerFodLogVo.setBusinessProjectId(oppTradeVo.getPrjectGUID());
                        customerFodLogVo.setCycleType("1");
                        customerFodLogVo.setGroupCycle(Double.parseDouble(String.valueOf(groupCycle)));
                        customerFodLogVo.setAreaCycle(Double.parseDouble(String.valueOf(areaCycle)));
                        customerFodLogVo.setProjectCycle(Double.parseDouble(String.valueOf(projectCycle)));
                        customerFodLogVo.setTeamCycle(Double.parseDouble(String.valueOf(teamCycle)));
                        customerFodLogVo.setUserCycle(Double.parseDouble(String.valueOf(userCycle)));
                        customerFodLogVo.setCreator("系统自动");
                        projectCluesDao.saveCustomerFodLog(customerFodLogVo);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else {//如果有判断上一次的成交时间和本次成交时间 时间周期是否差2天
                    try {
                        LocalDate date1 = LocalDate.parse(sfDa.format(sfDa.parse(beforeTrade.getContractDate())), df);
                        LocalDate date2 = LocalDate.parse(sfDa.format(sfDa.parse(oppTradeVo.getContractDate())), df);
                        long daysDiff =  ChronoUnit.DAYS.between(date1, date2);
                        //如果差值不到2天 算作同一次交易 不计入复购
                        if(daysDiff > 2){//如果差值大于2天 计入复购周期
                            //集团复购周期 获取上一次的交易记录 计算复购周期
                            String contractDate = messageMapper.getCustomerLastContractDate(oppId,oppTradeVo.getContractDate(),"1",null);
                            date1 = LocalDate.parse(sfDa.format(sfDa.parse(contractDate)), df);
                            long groupCycle =  ChronoUnit.DAYS.between(date1, date2);
                            //判断项目是否存在
                            String projectName = projectMapper.getProjectNameByProjectId(oppTradeVo.getProjectId());
                            long areaCycle = 0L;
                            long projectCycle = 0L;
                            if(StringUtils.isNotEmpty(projectName)){
                                //区域复购周期 获取客户在区域的上一次成交记录 计算复购周期
                                contractDate = messageMapper.getCustomerLastContractDate(oppId,oppTradeVo.getContractDate(),"2",oppTradeVo.getProjectId());
                                date1 = LocalDate.parse(sfDa.format(sfDa.parse(contractDate)), df);
                                areaCycle =  ChronoUnit.DAYS.between(date1, date2);
                                //项目获取客户在项目下上一次的成交记录计算
                                contractDate = messageMapper.getCustomerLastContractDate(oppId,oppTradeVo.getContractDate(),"3",oppTradeVo.getProjectId());
                                date1 = LocalDate.parse(sfDa.format(sfDa.parse(contractDate)), df);
                                projectCycle =  ChronoUnit.DAYS.between(date1, date2);
                            }
                            //招商组获取招商团队上一次的成交记录 计算复购周期
                            long teamCycle = 0L;
                            if (StringUtils.isNotEmpty(oppTradeVo.getSalesAttributionTeamId())) {
                                contractDate = messageMapper.getCustomerLastContractDate(oppId,oppTradeVo.getContractDate(),"4",oppTradeVo.getSalesAttributionTeamId());
                                date1 = LocalDate.parse(sfDa.format(sfDa.parse(contractDate)), df);
                                teamCycle =  ChronoUnit.DAYS.between(date1, date2);
                            }
                            //专员周期获取上一次的成交记录 计算复购周期
                            long userCycle = 0L;
                            if (StringUtils.isNotEmpty(oppTradeVo.getSalesAttributionId())) {
                                contractDate = messageMapper.getCustomerLastContractDate(oppId,oppTradeVo.getContractDate(),"5",oppTradeVo.getSalesAttributionId());
                                date1 = LocalDate.parse(sfDa.format(sfDa.parse(contractDate)), df);
                                userCycle =  ChronoUnit.DAYS.between(date1, date2);
                            }
                            //保存记录进入客户跟进交易日志记录表
                            CustomerFodLogVo customerFodLogVo = new CustomerFodLogVo();
                            customerFodLogVo.setAddLogId(projectCluesDao.getCustomerAddLogIdByOpportunityClueId(oppId));
                            customerFodLogVo.setOpportunityClueId(oppId);
                            customerFodLogVo.setProjectClueId(oppTradeVo.getProjectClueId());
                            customerFodLogVo.setBusinessId(oppTradeVo.getTradeGUID());
                            customerFodLogVo.setBusinessType(Objects.equals(oppTradeVo.getTradeStatus(), "激活") ? "6" : "7");
                            customerFodLogVo.setBusinessTime(oppTradeVo.getContractDate());
                            customerFodLogVo.setBusinessProjectId(oppTradeVo.getPrjectGUID());
                            customerFodLogVo.setCycleType("2");
                            customerFodLogVo.setGroupCycle(Double.parseDouble(String.valueOf(groupCycle)));
                            customerFodLogVo.setAreaCycle(Double.parseDouble(String.valueOf(areaCycle)));
                            customerFodLogVo.setProjectCycle(Double.parseDouble(String.valueOf(projectCycle)));
                            customerFodLogVo.setTeamCycle(Double.parseDouble(String.valueOf(teamCycle)));
                            customerFodLogVo.setUserCycle(Double.parseDouble(String.valueOf(userCycle)));
                            customerFodLogVo.setCreator("系统自动");
                            projectCluesDao.saveCustomerFodLog(customerFodLogVo);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        return res;
    }

//    @Override
////    @Scheduled(cron="59 59 23 * * ?")
//    public String initJtproConversionRateDate() {
//        Map paramMap = new HashMap();
//        List<CustomerAddLogVo> customerAddLogList = messageMapper.getAllCustomerAddLogList(paramMap);
//        //获取客户集合ID
//        List<String> ids = customerAddLogList.stream().map(CustomerAddLogVo::getOpportunityClueId).distinct().collect(Collectors.toList());
//        int size = ids.size();
//        List<CustomerFodLogVo> customerFodLogList = new ArrayList<>();
//        //每次查询一千条
//        int count = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
//        for (int s = 0; s < count; s++) {
//            if (s == count - 1) {
//                List<String> fformList = ids.subList(s * 1000, size);
//                List<CustomerFodLogVo> list = messageMapper.getAllCustomerFodLogList(fformList);
//                customerFodLogList.addAll(list);
//            } else {
//                List<String> fformList = ids.subList(s * 1000, (s + 1) * 1000);
//                List<CustomerFodLogVo> list = messageMapper.getAllCustomerFodLogList(fformList);
//                customerFodLogList.addAll(list);
//            }
//        }
//        //将跟进交易记录按客户维度分组
//        Map<String, List<CustomerFodLogVo>> recordMap = customerFodLogList.stream().collect(Collectors.groupingBy(CustomerFodLogVo::getOpportunityClueId));
//        //循环客户集合 封装客户跟进交易数据
//        List<CustomerFodLogVo> cc = new ArrayList<>();
//        CustomerFodLogVo customerFodLogVo = new CustomerFodLogVo();
//        customerFodLogVo.setBusinessId("visolink123");
//        customerFodLogVo.setBusinessType("visolink123");
//        customerFodLogVo.setBusinessTime(sf.format(DateUtil.date()));
//        customerFodLogVo.setBusinessProjectId("visolink123");
//        customerFodLogVo.setMainVisitProjectId("visolink123");
//        customerFodLogVo.setIsThreeOnesStatus("visolink123");
//        customerFodLogVo.setIsFirstComeVisitStatus("visolink123");
//        customerFodLogVo.setIsFirstVisitStatus("visolink123");
//        customerFodLogVo.setIsSignAfterVisitStatus("visolink123");
//        customerFodLogVo.setIsStatistics("visolink123");
//        cc.add(customerFodLogVo);
//        customerAddLogList.stream().forEach(x->{
//            x.setCustomerFodLogList(recordMap.get(x.getOpportunityClueId()));
//            //塞一个不满足条件的空集合数据 防止无数据异常
//            if(CollectionUtils.isEmpty(x.getCustomerFodLogList())){
//                cc.get(0).setOpportunityClueId(x.getOpportunityClueId());
//                x.setCustomerFodLogList(cc);
//            }
//        });
//        try {
//            // 判断索引是否存在
//            GetRequest getRequest = new GetRequest(indexName, "_doc", documentId); // 指定 "_doc" 类型
//            boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
//            System.out.println("判断索引=是否存在：" + exists);
//            if (!exists) {
//                System.out.println("索引不存在");
//                // 创建索引
//                String json = JSON.toJSONString(customerAddLogList);
//                System.out.println("看下数据："+json);
//                IndexRequest indexRequest = new IndexRequest(indexName, "_doc", documentId).source(json, XContentType.JSON);
//                client.index(indexRequest, RequestOptions.DEFAULT);
//            }else {
//                System.out.println("索引存在");
//                //删除索引
//                DeleteRequest deleteRequest = new DeleteRequest(indexName, "_doc", documentId); // 指定 "_doc" 类型
//                client.delete(deleteRequest, RequestOptions.DEFAULT);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return "初始化成功";
//    }

    public void brokerchinnel(List<Map> mapList, Integer in) {
        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //循环所有的数据
            for (int i = 0; i < mapList.size(); i++) {
                //当前日期
                Date dateToday = new Date();
                //该条数据的线索id
                String ProjectClueId = mapList.get(i).get("ProjectClueId") + "";
                //线索状态
                String ClueStatus = mapList.get(i).get("ClueStatus") + "";
                //报备人
                String reportNameId = mapList.get(i).get("ReportUserID") + "";
                //客户姓名
                String CustomerName = mapList.get(i).get("CustomerName") + "";
                //报备时间
                String ReportCreateTime = mapList.get(i).get("ReportCreateTime") + "";
                //客户属于的项目
                String ProjectID = mapList.get(i).get("projectId") + "";
                //电话号码
                String customerMobile = mapList.get(i).get("CustomerMobile") + "";
                //经纪人推荐的客户失效时间
                String brokerCustomerExpiryDate = null;
                if (mapList.get(i).get("BrokerCustomerExpiryDate") != null) {
                    Date dates = sf.parse(String.valueOf(mapList.get(i).get("BrokerCustomerExpiryDate")));
                    brokerCustomerExpiryDate = sf.format(dates);
                }
                String reportExpireDate = null;
                if (mapList.get(i).get("ReportExpireDate") != null) {
                    Date dates = sf.parse(String.valueOf(mapList.get(i).get("ReportExpireDate")));
                    reportExpireDate = sf.format(dates);
                }
                String endDate = null;
                if (StringUtils.isBlank(brokerCustomerExpiryDate)) {
                    endDate = reportExpireDate;
                } else {
                    endDate = brokerCustomerExpiryDate;
                }
                /*********************全民经纪人报备失效相关操作****************************/
                //当数据库数据不是明源同步、并且当前时间大于报备失效时间，失效
                UpdateCluesMessage updateCluesMessage = new UpdateCluesMessage();
                MessageForm messageForm = new MessageForm();
                if ("1".equals(ClueStatus)) {
                    updateCluesMessage.setProjectClueId(ProjectClueId);
                    updateCluesMessage.setCustomerName(CustomerName);
                    updateCluesMessage.setReportCreateTime(ReportCreateTime);
                    updateCluesMessage.setProjectId(ProjectID);
                    updateCluesMessage.setReportUserId(reportNameId);
                    String content = "";
                    if (in == 1) {
                        updateCluesMessage.setMessage("未在有效时间内到访");
                        messageForm.setMessageType(1101);
                        if (mapList.get(i).get("ReportExpireDate") != null) {
                            Date date1 = sf.parse(String.valueOf(mapList.get(i).get("ReportExpireDate")));
                            if (!dateToday.before(date1)) {
                                updateCluesMessage.setIsReportExpire(1);
                            }
                        }
                        content = "【客户失效提醒】您推荐的客户[" + updateCluesMessage.getCustomerName() + customerMobile + "]由于未能在" + endDate + "前到访成功，已失效，您可以再次推荐该客户";
                    } else if (in == 2) { //预警
                        messageForm.setMessageType(1001);
                        content = "【确访时间提醒】您推荐的客户[" + updateCluesMessage.getCustomerName() + customerMobile + "]到访有效期截至" + endDate + ",请确保客户尽早到访";
                    }
                    messageForm.setSubject("报备失效");
                    messageForm.setContent(content);
                    messageForm.setProjectId(updateCluesMessage.getProjectId());
                    messageForm.setProjectClueId(updateCluesMessage.getProjectClueId());
                    if (updateCluesMessage.getOpportunityClueId() != null) {
                        messageForm.setOpportunityClueId(updateCluesMessage.getOpportunityClueId());
                    }
                    messageForm.setReceiver(updateCluesMessage.getReportUserId());
                } else {
                    updateCluesMessage.setProjectClueId(ProjectClueId);
                    updateCluesMessage.setProjectId(ProjectID);
                    updateCluesMessage.setCustomerName(CustomerName);
                    updateCluesMessage.setReportCreateTime(ReportCreateTime);
                    updateCluesMessage.setReportUserId(reportNameId);
                    String content = "";
                    if (in == 3) {
                        updateCluesMessage.setMessage("未在有效时间内认购");
                        messageForm.setMessageType(1102);
                        if (mapList.get(i).get("TokerVisitExpireDate") != null) {
                            Date date1 = sf.parse(String.valueOf(mapList.get(i).get("TokerVisitExpireDate")));
                            if (!dateToday.before(date1)) {
                                updateCluesMessage.setIsTokerVisitExpire(1);
                            }
                        }
                        content = "【客户失效提醒】您推荐的客户[" + updateCluesMessage.getCustomerName() + customerMobile + "]由于未能在" + endDate + "前认购成功，已失效。";
                    } else if (in == 4) { //预警
                        messageForm.setMessageType(1002);
                        content = "【认购时间提醒】您推荐的客户[" + updateCluesMessage.getCustomerName() + customerMobile + "]认购有效期截至" + endDate + ",请尽快促成客户认购";
                    }
                    messageForm.setSubject("渠道保护期逾期");
                    messageForm.setContent(content);
                    messageForm.setProjectId(updateCluesMessage.getProjectId());
                    messageForm.setProjectClueId(updateCluesMessage.getProjectClueId());
                    if (updateCluesMessage.getOpportunityClueId() != null) {
                        messageForm.setOpportunityClueId(updateCluesMessage.getOpportunityClueId());
                    }
                    messageForm.setReceiver(updateCluesMessage.getReportUserId());

                }
                //添加消息
                listMessage.add(messageForm);
                if (in == 1 || in == 3) {
                    //改变线索中的数据
                    messageMapper.updateBprojectClues(updateCluesMessage);
                    //改变机会表中的数据
                    messageMapper.updateBprojectopportunity(updateCluesMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void timerchinnel(List<Map> mapList, Integer in) {
        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //循环所有的数据
        for (int i = 0; i < mapList.size(); i++) {
            //当前日期
            Date dateToday = new Date();
            //该条数据的线索id
            String ProjectClueId = mapList.get(i).get("ProjectClueId") + "";
            //报备失效逾期时间
            Date dateReport = (Date) mapList.get(i).get("ReportExpireDate");
            //报备失效时间预警
            Date dateReportWaring = (Date) mapList.get(i).get("ReportExpireWarningDate");
            Calendar ca = Calendar.getInstance();
            //最终计算出来的时间
            //线索状态
            String ClueStatus = mapList.get(i).get("ClueStatus") + "";
            //报备人
            String reportNameId = mapList.get(i).get("ReportUserID") + "";
            //客户姓名
            String CustomerName = mapList.get(i).get("CustomerName") + "";
            //报备时间
            String ReportCreateTime = mapList.get(i).get("ReportCreateTime") + "";
            //报备逾期时间
            String TheFirstVisitDate = mapList.get(i).get("TheFirstVisitDate") + "";
            //报备逾期时间
            String ReportExpireDate = mapList.get(i).get("ReportExpireDate") + "";
            //报备逾期时间
            String reportExpireWarningDate = mapList.get(i).get("ReportExpireWarningDate") + "";
            //报备逾期时间
            String tokerVisitExpireDate = mapList.get(i).get("TokerVisitExpireDate") + "";

            String TokerVisitExpireWarningDate = mapList.get(i).get("TokerVisitExpireWarningDate") + "";

            String SalesAttributionId = mapList.get(i).get("SalesAttributionId") + "";
            //客户属于的项目
            String ProjectID = mapList.get(i).get("projectId") + "";
            String ProjectName = mapList.get(i).get("ProjectName") + "";
            //渠道失效时间
            Date TokerVisitExpireDate = (Date) mapList.get(i).get("TokerVisitExpireDate");
            //成交渠道
            String SourceType = mapList.get(i).get("SourceType") + "";

            /*********************内渠报备失效相关操作****************************/
            //&& (mapList.get(i).get("SalesAttributionId")==null || mapList.get(i).get("SalesAttributionId")+""=="")
            if ("1".equals(ClueStatus)) {
                //当数据库数据不是明源同步、并且当前时间大于报备失效时间，失效
                Integer messageType = null;
                String jobCode = "";
                if (in == 2) {
                    messageType = 1101;
                    jobCode = "nqgw";
                } else if (in == 1) { //预警
                    messageType = 1001;
                    jobCode = "nqgw";
                } else if (in == 6) {
                    messageType = 2101;
                    jobCode = "zygw";
                } else if (in == 5) {
                    messageType = 2001;
                    jobCode = "zygw";
                }

                UpdateCluesMessage updateCluesMessage = new UpdateCluesMessage();
                updateCluesMessage.setProjectClueId(ProjectClueId);
                updateCluesMessage.setIsReportExpire(1);
                updateCluesMessage.setCustomerName(CustomerName);
                updateCluesMessage.setReportCreateTime(ReportCreateTime);
                updateCluesMessage.setProjectId(ProjectID);
                updateCluesMessage.setReportUserId(reportNameId);
                updateCluesMessage.setReportExpireDate(ReportExpireDate);
                updateCluesMessage.setReportExpireWarningDate(reportExpireWarningDate);
                updateCluesMessage.setTokerVisitExpireWarningDate(TokerVisitExpireWarningDate);
                updateCluesMessage.setTokerVisitExpireDate(tokerVisitExpireDate);
                updateCluesMessage.setSalesAttributionId(SalesAttributionId);
                getTime(updateCluesMessage, dateToday, dateReport, messageType, SourceType, in,jobCode);

            }
            //渠道保护期相关的操作
            if (!"1".equals(ClueStatus)) {
                boolean isSend = false;
                //当前时间大于等于渠道保护期时间，则逾期
                if (in == 4) {
                    //改变线索中的数据
                    UpdateCluesMessage updateCluesMessage = new UpdateCluesMessage();
                    updateCluesMessage.setProjectClueId(ProjectClueId);
                    updateCluesMessage.setProjectId(ProjectID);
                    updateCluesMessage.setCustomerName(CustomerName);
                    updateCluesMessage.setReportCreateTime(ReportCreateTime);
                    updateCluesMessage.setReportUserId(reportNameId);
                    updateCluesMessage.setIsTokerVisitExpire(1);
                    messageMapper.updateBprojectClues(updateCluesMessage);
                    //机会
                    messageMapper.updateBprojectopportunity(updateCluesMessage);

                    if (!"1".equals(SourceType)) {
                        //查询已配置消息
                        Map messageTypeInfo = messageMapper.getUserMessageTypeInfo(reportNameId,
                                ProjectID,"nqgw","1102");
                        String isRead = "1";
                        String isPush = "2";
                        if(messageTypeInfo != null) {
                            isRead = "0";
                            isPush = "1";
                        }
//                            String content = "【渠道保护期通知】您于" +
//                                    ReportCreateTime + "报备的客户[" + CustomerName + "]，由于未能及时认购，已于" + TokerVisitExpireDate + "失效，请知悉";
                        MessageForm messageForm = new MessageForm();
                        messageForm.setSubject("渠道保护期逾期");
                        messageForm.setContent("您的客户已渠道逾期");
                        messageForm.setProjectId(ProjectID);
                        messageForm.setProjectClueId(ProjectClueId);
                        messageForm.setReceiver(reportNameId);
                        messageForm.setMessageType(1102);
                        messageForm.setIsPush(isPush);
                        messageForm.setIsRead(isRead);
                        //添加额外参数
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("customerName",CustomerName);
                        jsonObject.put("theFirstVisitDate",TheFirstVisitDate);
                        jsonObject.put("tokerVisitExpireDate",tokerVisitExpireDate);
                        jsonObject.put("projectName",ProjectName);
                        messageForm.setMessageData(JSON.toJSONString(jsonObject));
                        listMessage.add(messageForm);
                            //添加消息
                            // messageMapper.insertMessage(messageForm);
                    }
                    //当前时间等于渠道保护期时间规定预警时间，则预警
                } else if (in == 3) {
                    if (!"1".equals(SourceType)) {
                        //查询已配置消息
                        Map messageTypeInfo = messageMapper.getUserMessageTypeInfo(reportNameId,
                                ProjectID,"nqgw","1002");
                        String isRead = "1";
                        String isPush = "2";
                        if(messageTypeInfo != null) {
                            isRead = "0";
                            isPush = "1";
                        }
//                            String content = "【渠道保护期预警】您于" +
//                                    ReportCreateTime + "报备的客户[" + CustomerName + "]即将于" + TokerVisitExpireDate + "失效，请及时促成认购";
                        MessageForm messageForm = new MessageForm();
                        messageForm.setSubject("渠道保护期预警");
                        messageForm.setContent("您的客户即将渠道逾期，请及时跟进");
                        messageForm.setProjectId(ProjectID);
                        messageForm.setProjectClueId(ProjectClueId);
                        messageForm.setReceiver(reportNameId);
                        messageForm.setMessageType(1002);
                        messageForm.setIsPush(isPush);
                        messageForm.setIsRead(isRead);
                        //添加额外参数
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("customerName",CustomerName);
                        jsonObject.put("theFirstVisitDate",TheFirstVisitDate);
                        jsonObject.put("tokerVisitExpireDate",tokerVisitExpireDate);
                        jsonObject.put("projectName",ProjectName);
                        messageForm.setMessageData(JSON.toJSONString(jsonObject));
                        listMessage.add(messageForm);
                            //添加消息
                            // messageMapper.insertMessage(messageForm);
                    }

                }
            }

        }


    }


    /*************************置业顾问***********************/
    public void timerOther(List<Map> mapList, Integer in) {
        StringBuffer sb = new StringBuffer();
        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //查看数据是否是数据报备未到到访的数据
        for (int i = 0; i < mapList.size(); i++) {
            //该条数据的机会id
            String OpportunityClueId = mapList.get(i).get("OpportunityClueId") + "";
            //该条数据的线索id
            String ProjectClueId = mapList.get(i).get("ProjectClueId") + "";
            //到访失效逾期时间（认购逾期）
            String SalesVisitExpireDate =  mapList.get(i).get("SalesVisitExpireDate") +"";
            String SalesVisitExpireWarningDate = mapList.get(i).get("SalesVisitExpireWarningDate") +"";
            //线索状态
            String ClueStatus = mapList.get(i).get("ClueStatus") + "";
            //来源渠道 1.中介 2.自渠 3.置地顾问
            String SourceType = mapList.get(i).get("SourceType") + "";
            //客户姓名
            String CustomerName = mapList.get(i).get("CustomerName") + "";
            //报备时间
            String ReportCreateTime = mapList.get(i).get("ReportCreateTime") + "";
            //报备人
            String ReportUserName = mapList.get(i).get("ReportUserName") + "";
            //客户属于的项目
            String ProjectID = mapList.get(i).get("projectId") + "";
            String ProjectName = mapList.get(i).get("ProjectName") + "";
            //下次跟进时间（失效时间）
            String SalesFollowExpireDate = mapList.get(i).get("SalesFollowExpireDate") +"";
            //案场最近跟进时间
            String SalesTheLatestFollowDate = mapList.get(i).get("SalesTheLatestFollowDate") + "";
            //客户第一次到访的时间
            String TheFirstVisitDate = mapList.get(i).get("TheFirstVisitDate") + "";
            //关键字段
            String flag = mapList.get(i).get("flag") + "";
            //案场归属人id
            String SalesAttributionId = mapList.get(i).get("SalesAttributionId") + "";
            //案场归属人姓名
            String SalesAttributionName = mapList.get(i).get("SalesAttributionName") + "";
            //跟进时间
            String VisitDate = mapList.get(i).get("VisitDate") + "";
            String SalesFollowExpireWarningDate = mapList.get(i).get("SalesFollowExpireWarningDate") +"";
            //跟进和认购相关的通知操作
            boolean isSend = false;
            if (!ClueStatus.equals("1") && Integer.parseInt(ClueStatus) < 7) {
                Integer messageType = null;
                String dataDate =
                        SalesTheLatestFollowDate == null ||
                                "null".equals(SalesTheLatestFollowDate) ||
                                "".equals(SalesTheLatestFollowDate) ? TheFirstVisitDate : SalesTheLatestFollowDate;
                //如果当前时间大于下次跟进时间,逾期（跟进）
                if (in == 4) {
                    //改变线索和机会表的数据
                    UpdateCluesMessage updateCluesMessage = new UpdateCluesMessage();
                    updateCluesMessage.setIsSalesFollowExpire(1);
                    updateCluesMessage.setProjectClueId(ProjectClueId);
                    updateCluesMessage.setOpportunityClueId(OpportunityClueId);
                    messageMapper.updateBprojectClues(updateCluesMessage);
                    messageMapper.updateBprojectopportunity(updateCluesMessage);
                    //查询已配置消息
                    Map messageTypeInfo = messageMapper.getUserMessageTypeInfo(SalesAttributionId,
                            ProjectID,"zygw","2102");
                    String isRead = "1";
                    String isPush = "2";
                    if(messageTypeInfo != null) {
                        isRead = "0";
                        isPush = "1";
                    }
//                        String content = "【跟进逾期通知】您于" +
//                                dataDate + "跟进的客户[" + CustomerName + "]，已于" + SalesFollowExpireDate + "逾期，请知悉。";
                    MessageForm messageForm = new MessageForm();
                    messageForm.setSubject("跟进逾期");
                    messageForm.setContent("您的客户跟进逾期，请知悉");
                    messageForm.setProjectId(ProjectID);
                    messageForm.setProjectClueId(ProjectClueId);
                    messageForm.setOpportunityClueId(OpportunityClueId);
                    messageForm.setReceiver(SalesAttributionId);
                    messageForm.setMessageType(2102);
                    messageForm.setIsPush(isPush);
                    messageForm.setIsRead(isRead);
                    //添加额外参数
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("customerName",CustomerName);
                    jsonObject.put("theFirstVisitDate",TheFirstVisitDate);
                    jsonObject.put("salesFollowExpireDate",SalesFollowExpireDate);
                    jsonObject.put("projectName",ProjectName);
                    messageForm.setMessageData(JSON.toJSONString(jsonObject));
                    listMessage.add(messageForm);
                    if (mapList.get(i).get("IsEnterPublicPool") != null && "1".equals(String.valueOf(mapList.get(i).get("IsEnterPublicPool")))) {
                        if (!"3".equals(ClueStatus) && !"4".equals(ClueStatus)){
                            addPublicPool(mapList.get(i), in);
                            //该条数据的机会id
                            String oppId = mapList.get(i).get("OpportunityClueId") + "";
                            //放入同步明源机会集合字符串
                            sb.append(oppId + ",");
                        }
                    }

                } else if (in == 3) {
                    //查询已配置消息
                    Map messageTypeInfo = messageMapper.getUserMessageTypeInfo(SalesAttributionId,
                            ProjectID,"zygw","2002");
                    String isRead = "1";
                    String isPush = "2";
                    if(messageTypeInfo != null) {
                        isRead = "0";
                        isPush = "1";
                    }
//                        String content = "【跟进预警】您于" +
//                                dataDate + "跟进的客户[" + CustomerName + "]，即将于" + SalesFollowExpireDate + "逾期，请尽快跟进";
                    MessageForm messageForm = new MessageForm();
                    messageForm.setSubject("跟进预警");
                    messageForm.setContent("您的客户即将跟进逾期，请及时跟进");
                    messageForm.setProjectId(ProjectID);
                    messageForm.setProjectClueId(ProjectClueId);
                    messageForm.setOpportunityClueId(OpportunityClueId);
                    messageForm.setReceiver(SalesAttributionId);
                    messageForm.setMessageType(2002);
                    messageForm.setIsPush(isPush);
                    messageForm.setIsRead(isRead);
                        //添加额外参数
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("customerName",CustomerName);
                    jsonObject.put("theFirstVisitDate",TheFirstVisitDate);
                    jsonObject.put("salesFollowExpireDate",SalesFollowExpireDate);
                    jsonObject.put("projectName",ProjectName);
                    messageForm.setMessageData(JSON.toJSONString(jsonObject));
                    listMessage.add(messageForm);
                }
                //认购逾期
                if (in == 6) {
                    //改变机会表的数据
                    UpdateCluesMessage updateCluesMessage = new UpdateCluesMessage();
                    updateCluesMessage.setIsSalesVisitExpire(1);
                    updateCluesMessage.setProjectClueId(ProjectClueId);
                    updateCluesMessage.setOpportunityClueId(OpportunityClueId);
                    messageMapper.updateBprojectClues(updateCluesMessage);
                    messageMapper.updateBprojectopportunity(updateCluesMessage);
                    //查询已配置消息
                    Map messageTypeInfo = messageMapper.getUserMessageTypeInfo(SalesAttributionId,
                            ProjectID,"zygw","2103");
                    String isRead = "1";
                    String isPush = "2";
                    if(messageTypeInfo != null) {
                        isRead = "0";
                        isPush = "1";
                    }
//                        String content = "【认购逾期通知】您于" +
//                                VisitDate + "做接访的客户[" + CustomerName + "]，由于未及时认购，已于" + SalesVisitExpireDate + "逾期，请知悉";
                    MessageForm messageForm = new MessageForm();
                    messageForm.setSubject("认购逾期");
                    messageForm.setContent("您的客户认购逾期，请知悉");
                    messageForm.setProjectId(ProjectID);
                    messageForm.setProjectClueId(ProjectClueId);
                    messageForm.setOpportunityClueId(OpportunityClueId);
                    messageForm.setReceiver(SalesAttributionId);
                    messageForm.setMessageType(2103);
                    messageForm.setIsPush(isPush);
                    messageForm.setIsRead(isRead);
                    //添加额外参数
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("customerName",CustomerName);
                    jsonObject.put("theFirstVisitDate",TheFirstVisitDate);
                    jsonObject.put("salesVisitExpireDate",SalesVisitExpireDate);
                    jsonObject.put("projectName",ProjectName);
                    messageForm.setMessageData(JSON.toJSONString(jsonObject));
                    listMessage.add(messageForm);
                    addPublicPool(mapList.get(i), in);

                } else if (in == 5) { //认购预警
                    //查询已配置消息
                    Map messageTypeInfo = messageMapper.getUserMessageTypeInfo(SalesAttributionId,
                            ProjectID,"zygw","2003");
                    String isRead = "1";
                    String isPush = "2";
                    if(messageTypeInfo != null) {
                        isRead = "0";
                        isPush = "1";
                    }
//                        String content = "【认购预警】您于" +
//                                VisitDate + "做接访的客户[" + CustomerName + "]，即将于" + SalesVisitExpireDate + "失效，请及时跟进促成认购";
                    MessageForm messageForm = new MessageForm();
                    messageForm.setSubject("认购预警");
                    messageForm.setContent("您的客户即将认购逾期，请及时跟进");
                    messageForm.setProjectId(ProjectID);
                    messageForm.setProjectClueId(ProjectClueId);
                    messageForm.setOpportunityClueId(OpportunityClueId);
                    messageForm.setReceiver(SalesAttributionId);
                    messageForm.setMessageType(2003);
                    messageForm.setIsPush(isPush);
                    messageForm.setIsRead(isRead);
                    //添加额外参数
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("customerName",CustomerName);
                    jsonObject.put("theFirstVisitDate",TheFirstVisitDate);
                    jsonObject.put("salesVisitExpireDate",SalesVisitExpireDate);
                    jsonObject.put("projectName",ProjectName);
                    messageForm.setMessageData(JSON.toJSONString(jsonObject));
                    listMessage.add(messageForm);
                }
            }

        }
        if (sb.toString().length() > 0) {
            String oppIds = sb.toString().substring(0, sb.toString().length() - 1);
            Map jsonMap = new HashMap();
            jsonMap.put("projectClueIds", oppIds);
            jsonMap.put("userId", null);
            jsonMap.put("userName", null);
            HttpRequestUtil.httpPost(RedisbuURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
        }
    }

    /*************************置业顾问签约***********************/
    public void timerTrade(List<Map> mapList, Integer in) {

        for (int i = 0; i < mapList.size(); i++) {
            //机会id
            String OpportunityClueId = mapList.get(i).get("OpportunityClueId") + "";
            //线索ID
            String ProjectClueId = mapList.get(i).get("ProjectClueId") + "";
            //认购日期
            String OrderDate = mapList.get(i).get("OrderDate") + "";
            //客户姓名
            String CstName = mapList.get(i).get("CstName") + "";
            //应签约日期
            String YqyDate = mapList.get(i).get("YqyDate") + "";
            //签约所属项目
            String ProjectID = mapList.get(i).get("ProjectID") + "";
            //案场归属人id（消息接收人id）
            String SalesAttributionId = mapList.get(i).get("SalesAttributionId") + "";
            //系统交易id
            String tradeGuId = mapList.get(i).get("TradeGUID") + "";
            /*************签约逾期********/
            if (in == 2) {
                String content = "「签约逾期通知」您于" + OrderDate + "认购的客户【" + CstName + "】，由于未及时签约，已于" + YqyDate + "逾期，请知悉。";
                MessageForm messageForm = new MessageForm();
                messageForm.setSubject("签约逾期");
                messageForm.setContent(content);
                messageForm.setProjectId(ProjectID);
                messageForm.setProjectClueId(ProjectClueId);
                messageForm.setOpportunityClueId(OpportunityClueId);
                messageForm.setReceiver(SalesAttributionId);
                messageForm.setMessageType(2104);
                messageForm.setTradeGuId(tradeGuId);
                listMessage.add(messageForm);
            } else if (in == 1) {
                String content = "「签约预警通知」您于" + OrderDate + "认购的客户【" + CstName + "】，即将于" + YqyDate + "逾期，请及时促成跟进签约。";
                MessageForm messageForm = new MessageForm();
                messageForm.setSubject("签约预警");
                messageForm.setContent(content);
                messageForm.setProjectId(ProjectID);
                messageForm.setProjectClueId(ProjectClueId);
                messageForm.setOpportunityClueId(OpportunityClueId);
                messageForm.setReceiver(SalesAttributionId);
                messageForm.setMessageType(2004);
                messageForm.setTradeGuId(tradeGuId);
                listMessage.add(messageForm);
            }
        }

    }

    /*************************置业顾问回款***********************/
    public void timerReturn(List<Map> mapList, Integer in) {

        for (int i = 0; i < mapList.size(); i++) {
            //机会id
            String OpportunityClueId = mapList.get(i).get("OpportunityClueId") + "";
            //线索ID
            String ProjectClueId = mapList.get(i).get("ProjectClueId") + "";
            //回款唯一标识
            String FeeGUID = mapList.get(i).get("FeeGUID") + "";
            //项目id
            String ProjectID = mapList.get(i).get("ProjectID") + "";
            //案场归属人
            String SalesAttributionId = mapList.get(i).get("SalesAttributionId") + "";
            //客户姓名
            String CustomerName = mapList.get(i).get("CustomerName") + "";
            //应收日期
            String YsDate = mapList.get(i).get("YsDate") + "";
            /*************回款逾期********/
            if (in == 2) {
                String content = "「回款逾期通知」您的客户【" + CustomerName + "】回款日期" + YsDate + ",由于未及时回款，已逾期，请知悉";
                MessageForm messageForm = new MessageForm();
                messageForm.setSubject("回款逾期");
                messageForm.setContent(content);
                messageForm.setProjectId(ProjectID);
                messageForm.setProjectClueId(ProjectClueId);
                messageForm.setOpportunityClueId(OpportunityClueId);
                messageForm.setReceiver(SalesAttributionId);
                messageForm.setMessageType(2105);
                messageForm.setTradeGuId(FeeGUID);
                listMessage.add(messageForm);
            } else if (in == 1) {
                String content = "「回款预警通知」您的客户【" + CustomerName + "】回款日期" + YsDate + ",请及时跟进促成回款";
                MessageForm messageForm = new MessageForm();
                messageForm.setSubject("回款预警");
                messageForm.setContent(content);
                messageForm.setProjectId(ProjectID);
                messageForm.setProjectClueId(ProjectClueId);
                messageForm.setOpportunityClueId(OpportunityClueId);
                messageForm.setReceiver(SalesAttributionId);
                messageForm.setMessageType(2005);
                messageForm.setTradeGuId(FeeGUID);
                listMessage.add(messageForm);

            }
        }

    }

    //报备失效的方法
    public void getTime(UpdateCluesMessage updateCluesMessage, Date dateToday,
                        Date dateReport, Integer messageType, String sourceType, Integer in,String jobCode) {
        boolean isSend = false;
        String messageTypes = messageType + "";
        //置业顾问消息
        //查询已配置消息
        Map messageTypeInfo = null;
        String isRead = "1";
        String isPush = "2";
        if(("1101".equals(messageTypes) || "1001".equals(messageTypes)) && "nqgw".equals(jobCode)){
            messageTypeInfo = messageMapper.getUserMessageTypeInfo(updateCluesMessage.getReportUserId(),
                    updateCluesMessage.getProjectId(),jobCode,messageTypes);
            if(messageTypeInfo != null) {
                isRead = "0";
                isPush = "1";
            }
        }else if( ("2101".equals(messageTypes) || "2001".equals(messageTypes)) && "zygw".equals(jobCode)){
            messageTypeInfo = messageMapper.getUserMessageTypeInfo(updateCluesMessage.getSalesAttributionId(),
                    updateCluesMessage.getProjectId(),jobCode,messageTypes);
            if(messageTypeInfo != null) {
                isRead = "0";
                isPush = "1";
            }
        }
        if(in == 2 || in == 6){
            //改变线索中的数据
            messageMapper.updateBprojectClues(updateCluesMessage);
            //改变机会表中的数据
            messageMapper.updateBprojectopportunity(updateCluesMessage);
        }
        //如果报备时间小于当前时间，则逾期
        if (in == 2 || in == 6) {
            if (!"1".equals(sourceType)) {
//                    String content = "【报备失效通知】您于" +
//                            updateCluesMessage.getReportCreateTime() + "报备的客户[" + updateCluesMessage.getCustomerName() + "]已于" + dateReport + "失效，请知悉";
                MessageForm messageForm = new MessageForm();
                messageForm.setSubject("报备失效");
                messageForm.setContent("您的客户报备逾期，请知悉");
                messageForm.setProjectId(updateCluesMessage.getProjectId());
                messageForm.setProjectClueId(updateCluesMessage.getProjectClueId());
                if (updateCluesMessage.getOpportunityClueId() != null) {
                    messageForm.setOpportunityClueId(updateCluesMessage.getOpportunityClueId());
                }
                messageForm.setMessageType(messageType);
                messageForm.setReceiver(updateCluesMessage.getReportUserId());
                messageForm.setIsPush(isPush);
                messageForm.setIsRead(isRead);
                //添加额外参数
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("customerName",updateCluesMessage.getCustomerName());
                jsonObject.put("projectName",updateCluesMessage.getProjectName());
                jsonObject.put("reportCreateTime",updateCluesMessage.getReportCreateTime());
                jsonObject.put("reportExpireDate",updateCluesMessage.getReportExpireDate());
                messageForm.setMessageData(JSON.toJSONString(jsonObject));
                //添加消息
                listMessage.add(messageForm);
//              messageMapper.insertMessage(messageForm);
            }

        }
        if (in == 1 || in == 5) {
            if (!"1".equals(sourceType)) {
//                    String content = "【报备失效预警】您于" +
//                            updateCluesMessage.getReportCreateTime() + "报备的客户[" + updateCluesMessage.getCustomerName() + "]将于" + dateReport + "失效，请及时促成到访";
                MessageForm messageForm = new MessageForm();
                messageForm.setSubject("报备失效预警");
                messageForm.setContent("您的客户即将报备逾期，请及时跟进");
                messageForm.setProjectId(updateCluesMessage.getProjectId());
                messageForm.setProjectClueId(updateCluesMessage.getProjectClueId());
                if (updateCluesMessage.getOpportunityClueId() != null) {
                    messageForm.setOpportunityClueId(updateCluesMessage.getOpportunityClueId());
                }
                messageForm.setReceiver(updateCluesMessage.getReportUserId());
                messageForm.setMessageType(messageType);
                messageForm.setIsPush(isPush);
                messageForm.setIsRead(isRead);
                //添加额外参数
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("customerName",updateCluesMessage.getCustomerName());
                jsonObject.put("projectName",updateCluesMessage.getProjectName());
                jsonObject.put("reportCreateTime",updateCluesMessage.getReportCreateTime());
                jsonObject.put("reportExpireDate",updateCluesMessage.getReportExpireDate());
                messageForm.setMessageData(JSON.toJSONString(jsonObject));
                //添加消息
                listMessage.add(messageForm);
//                        messageMapper.insertMessage(messageForm);
            }
        }
    }

    public void addPublicPool(Map maps, Integer in) {
        //初始化扩展表对象
        ClueOpportunityExtend clueOpportunityExtend = new ClueOpportunityExtend();

        //该条数据的机会id
        String OpportunityClueId = maps.get("OpportunityClueId") + "";
        //该条数据的线索id
        String ProjectClueId = maps.get("ProjectClueId") + "";
        clueOpportunityExtend.setProjectClueId(ProjectClueId);
        //线索状态
        String ClueStatus = maps.get("ClueStatus") + "";
        //来源渠道 1.中介 2.自渠 3.置地顾问
        String SourceType = maps.get("SourceType") + "";
        //客户姓名
        String CustomerName = maps.get("CustomerName") + "";
        //报备时间
        String ReportCreateTime = maps.get("ReportCreateTime") + "";
        //报备人
        String ReportUserName = maps.get("ReportUserName") + "";
        //客户属于的项目
        String ProjectID = maps.get("projectId") + "";
        //渠道失效时间
        Date TokerVisitExpireDate = (Date) maps.get("TokerVisitExpireDate");
        Object TokerVisitExpireDateEnd = null;
        Calendar ca1 = Calendar.getInstance();
        if (TokerVisitExpireDate != null) {
            ca1.setTime(TokerVisitExpireDate);
            ca1.add(Calendar.DATE, -2);
            TokerVisitExpireDateEnd = ca1.getTime();
        }
        //最终计算出来的失效时间
        Date n = (Date) TokerVisitExpireDateEnd;

        //案场最近跟进时间
        String SalesTheLatestFollowDate = maps.get("SalesTheLatestFollowDate") + "";
        //客户第一次到访的时间
        String TheFirstVisitDate = maps.get("TheFirstVisitDate") + "";
        //案场归属人id
        String SalesAttributionId = maps.get("SalesAttributionId") + "";
        //案场归属人姓名
        String SalesAttributionName = maps.get("SalesAttributionName") + "";
        String TokerAttributionName = maps.get("TokerAttributionName") + "";
        String TokerAttributionId = maps.get("TokerAttributionId") + "";
        String customerMobile = maps.get("CustomerMobile") + "";
        String CustomerGender = maps.get("CustomerGender") + "";
        String CustomerAge = maps.get("CustomerAge") + "";
        String CustomerCardType = maps.get("CustomerCardType") + "";
        String CustomerCardNum = maps.get("CustomerCardNum") + "";
        String TokerAttributionTeamId = maps.get("TokerAttributionTeamId") + "";
        String TokerAttributionTeamName = maps.get("TokerAttributionTeamName") + "";
        String SalesAttributionTeamId = maps.get("SalesAttributionTeamId") + "";
        String salesAttributionTeamName = maps.get("SalesAttributionTeamName") + "";
        String SourceTypeDesc = maps.get("SourceTypeDesc") + "";
        String Level = maps.get("Level") + "";
        String Label = maps.get("Label") + "";
        String SalesAttributionTime = maps.get("SalesAttributionTime") + "";
        String TradeLevel = maps.get("TradeLevel") + "";
        String DataCompleteRate = maps.get("DataCompleteRate") + "";
        String DataCompleteAttachRate = maps.get("DataCompleteAttachRate") + "";
        //数据进公共池
        Map map = new HashMap();
        if (TheFirstVisitDate != null && !TheFirstVisitDate.equals("null")) {
            map.put("theFirstVisitDate", TheFirstVisitDate);
        }
        if (ReportUserName != null && !ReportUserName.equals("null")) {
            map.put("reportUserName", ReportUserName);
        }
        if (ReportCreateTime != null && !ReportCreateTime.equals("null")) {
            map.put("reportTime", ReportCreateTime);
        }
        if (SalesAttributionId != null && !SalesAttributionId.equals("null")) {
            map.put("saleId", SalesAttributionId);
            clueOpportunityExtend.setOldSalesId(SalesAttributionId);
        }
        if (SalesAttributionName != null && !SalesAttributionName.equals("null")) {
            map.put("saleName", SalesAttributionName);
            clueOpportunityExtend.setOldSalesName(SalesAttributionName);
        }
        if (ClueStatus != null && !ClueStatus.equals("null")) {
            map.put("clueStatus", ClueStatus);
        }
        if (ProjectID != null && !ProjectID.equals("null")) {
            map.put("projectId", ProjectID);
        }
        if (SourceType != null && !SourceType.equals("null")) {
            map.put("sourceType", SourceType);
        }
        if (ProjectClueId != null && !ProjectClueId.equals("null")) {
            map.put("projectClueId", ProjectClueId);
        }
        if (OpportunityClueId != null && !OpportunityClueId.equals("null")) {
            map.put("opportunityClueId", OpportunityClueId);
        }
        if (TokerAttributionName != null && !TokerAttributionName.equals("null")) {
            map.put("tokerAttributionName", TokerAttributionName);
        }
        if (TokerAttributionId != null && !TokerAttributionId.equals("null")) {
            map.put("TokerAttributionId", TokerAttributionId);
        }
        if (SalesAttributionName != null && !SalesAttributionName.equals("null")) {
            map.put("salesAttributionName", SalesAttributionName);
        }
        if (SalesAttributionId != null && !SalesAttributionId.equals("null")) {
            map.put("salesAttributionId", SalesAttributionId);
        }
        if (CustomerName != null && !CustomerName.equals("null")) {
            map.put("customerName", CustomerName);
        }
        if (customerMobile != null && !customerMobile.equals("null")) {
            map.put("customerMobile", customerMobile);
        }
        if (CustomerGender != null && !CustomerGender.equals("null")) {
            map.put("customerGender", CustomerGender);
        }
        if (CustomerAge != null && !CustomerAge.equals("null")) {
            map.put("customerAge", CustomerAge);
        }
        if (CustomerCardType != null && !CustomerCardType.equals("null")) {
            map.put("customerCardType", CustomerCardType);
        }
        if (CustomerCardNum != null && !CustomerCardNum.equals("null")) {
            map.put("customerCardNum", CustomerCardNum);
        }
        if (TokerAttributionTeamId != null && !TokerAttributionTeamId.equals("null")) {
            map.put("tokerAttributionTeamId", TokerAttributionTeamId);
        }
        if (TokerAttributionTeamName != null && !TokerAttributionTeamName.equals("null")) {
            map.put("tokerAttributionTeamName", TokerAttributionTeamName);
        }
        if (SalesAttributionTeamId != null && !SalesAttributionTeamId.equals("null")) {
            map.put("salesAttributionTeamId", SalesAttributionTeamId);
            clueOpportunityExtend.setOldTeamId(SalesAttributionTeamId);
        }
        if (salesAttributionTeamName != null && !salesAttributionTeamName.equals("null")) {
            map.put("salesAttributionTeamName", salesAttributionTeamName);
            clueOpportunityExtend.setOldTeamName(salesAttributionTeamName);
        }
        if (SourceTypeDesc != null && !SourceTypeDesc.equals("null")) {
            map.put("sourceTypeDesc", SourceTypeDesc);
        }
        if (Level != null && !Level.equals("null")) {
            map.put("level", Level);
        }
        if (Label != null && !Label.equals("null")) {
            map.put("label", Label);
        }
        if (SalesAttributionTime != null && !SalesAttributionTime.equals("null")) {
            map.put("salesAttributionTime", SalesAttributionTime);
        }
        if (SalesTheLatestFollowDate != null && !SalesTheLatestFollowDate.equals("null")) {
            map.put("salesTheLatestFollowDate", SalesTheLatestFollowDate);
        }
        if (TradeLevel != null && !TradeLevel.equals("null")) {
            map.put("tradeLevel", TradeLevel);
        }
        if (DataCompleteRate != null && !DataCompleteRate.equals("null")) {
            map.put("dataCompleteRate", DataCompleteRate);
        }
        if (DataCompleteAttachRate != null && !DataCompleteAttachRate.equals("null")) {
            map.put("dataCompleteAttachRate", DataCompleteAttachRate);
        }

        //查询公共池是否存在此机会
        String OppClueId = messageMapper.selectIsPubilc(OpportunityClueId);
        if (in == 4) {
            map.put("expireTag", "跟进逾期");
            map.put("addType", 2);
            map.put("Reason", "跟进逾期进入公共池");

            map.put("childType", 1);
            map.put("salesName", null);
            map.put("salesId", null);
            map.put("salesTeamName", null);
            map.put("salesTeamId", null);
            messageMapper.updateSalesAttribution(ProjectClueId);


        } else {
            map.put("expireTag", "认购逾期");
            map.put("addType", 3);
            map.put("Reason", "认购逾期进入公共池");
            map.put("childType", 3);
            if (SalesAttributionId == null || SalesAttributionId.equals("null")) {
                Map saleMap = messageMapper.getOldSaleInfo(ProjectClueId);
                if (saleMap != null) {
                    map.put("salesAttributionId", saleMap.get("OldSalesId"));
                    map.put("salesAttributionName", saleMap.get("OldSalesName"));
                    map.put("salesAttributionTeamId", saleMap.get("OldSalesAttributionTeamId"));
                    map.put("salesAttributionTeamName", saleMap.get("OldSalesAttributionTeamName"));
                } else {
                    map.put("salesAttributionId", null);
                    map.put("salesAttributionName", null);
                    map.put("salesAttributionTeamId", null);
                    map.put("salesAttributionTeamName", null);
                }
            }
            map.put("salesName", SalesAttributionName);
            map.put("salesId", SalesAttributionId);
            map.put("salesTeamName", salesAttributionTeamName);
            map.put("salesTeamId", SalesAttributionTeamId);
        }
        //如果存在置业顾问更新扩展表上一次置业顾问信息
        if (clueOpportunityExtend.getOldSalesId()!=null){
            List<ClueOpportunityExtend> clus = new ArrayList<>();
            clus.add(clueOpportunityExtend);
            projectCluesDao.updateClueOpportunityExtend(clus);
        }
        if (OppClueId==null){
            messageMapper.insertPublic(map);
        }
        messageMapper.insertCustomerDistributionRecords(map);
    }

    /**
     *
     * */
    public String getDateLabel(String inputDate){
        // 将输入的日期解析为 LocalDate
        LocalDate compareDate = LocalDate.parse(inputDate);

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 计算两个日期之间的差距
        Period period = Period.between(compareDate, currentDate);

        // 判断时间距离
        if (period.getYears() > 1) {
            System.out.println("时间距离现在一年以上。");
        } else if (period.getYears() == 1 || (period.getYears() == 0 && period.getMonths() >= 6)) {
            System.out.println("时间距离现在半年到一年。");
        } else {
            System.out.println("时间距离现在半年内。");
        }

        return null;
    }

    /**
     * 日期格式化
     * @param dateStr      需要格式化的日期
     * @param pattern   时间格式，如：yyyy-MM-dd HH:mm:ss
     * @return          返回格式化后的时间字符串
     */
    public static Date formatDate(String dateStr,String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Date();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }
}
