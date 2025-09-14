package cn.visolink.system.usermanager.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.exception.BadRequestException;
import cn.visolink.system.companyQw.model.QuitUserCst;
import cn.visolink.system.job.authorization.mapper.JobMapper;
import cn.visolink.system.usermanager.dao.UserManagerDao;
import cn.visolink.system.usermanager.service.UserManagerService;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.HttpRequestUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author wjc
 * @date 2019/09/11
 */
@Service
public class UserManagerServiceImpl implements UserManagerService {
    @Autowired
    private UserManagerDao userMessageDao;

    @Value("${ConsultantSyncURL}")
    private String consultantSyncURL;

    @Value("${outbound.updateUserOp}")
    private String updateUserOp;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JobMapper jobMapper;

    @Value("${outbound.deleteUserOp}")
    private String deleteUserOp;

    /**
     * 获取用户信息
     *
     * @param map
     * @return
     */
    @Override
    public Map findMessage(Map map) {
        Map resultMap=new HashMap();
        int pageIndex = Integer.parseInt(map.get("pageIndex").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        map.put("pageIndex", i);
        List<Map> message = userMessageDao.findMessage(map);
        Integer messageCount = userMessageDao.findMessageCount(map);
        resultMap.put("list", message);
        resultMap.put("total", messageCount);
        return resultMap;
    }

    /**
     * 人员信息更新
     *
     * @param map
     * @return
     */
    @Override
    public int systemUserUpdate(Map map) {
        Map data = (Map) map.get("data");
        if("启用".equals(data.get("Status"))){
            data.put("Status",1);
        }
        if("0".equals(data.get("Status"))){
            String brokerId = userMessageDao.getBrokerId(String.valueOf(data.get("ID")));
            if(!StringUtils.isBlank(brokerId)){
                //解除绑定
                userMessageDao.updateBroker(brokerId);
                Map map1 = new HashMap();
                map1.put("subject","旭客汇账号被禁用");
                map1.put("content","由于您的旭客汇账号被禁用，您的旭客汇账号绑定关系已失效，请知悉。");
                map1.put("sender", SecurityUtils.getUserId());
                map1.put("messageType","1000");
                map1.put("receiver",brokerId);
                userMessageDao.insertMessage(map1);
            }


            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String companycode = request.getHeader("companycode");
            if (redisUtil.get("QW_DATATOKEN_"+companycode)!=null){
                //查询是否存在成员
                String userid = userMessageDao.getQwUserId(String.valueOf(data.get("ID")));
                if (!StringUtils.isEmpty(userid)){
                    //调用企微删除成员（离职处理）
                    String token = redisUtil.get("QW_DATATOKEN_"+companycode).toString();
                    String re = HttpRequestUtil.httpGet("https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token="+token+"&userid="+userid,false);
                    if (re!=null){
                        JSONObject jsonObject = JSONObject.parseObject(re);
                        //删除成功
                        if ("0".equals(jsonObject.getString("errcode"))){
                            //查询成员客户
                            List<QuitUserCst> cstList = userMessageDao.getQwUserCst(userid);
                            //保存离职客户数据
                            userMessageDao.addQuitUserCst(cstList);
                            //成功后更新本地成员状态为离职  解除绑定案场客户
                            userMessageDao.delQwUser(userid);
                        }
                    }
                }
            }

        }
        if("普通账号".equals(data.get("AccountType"))){
            data.put("AccountType",2);
        }
        if("Saas账号".equals(data.get("AccountType"))){
            data.put("AccountType",1);
        }
        if("男".equals(data.get("Gender"))){
            data.put("Gender",1);
        }
        if("女".equals(data.get("Gender"))){
            data.put("Gender",2);
        }
        //查询是否对接了第三方
//        List<Map> csts = userMessageDao.getSalesThirdPro(data.get("ID")+"","1");
        int i = userMessageDao.modifySystemUser(data);
        // 同步更新用户名片信息
        userMessageDao.updateCardByAccountId(data);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("user_id",map.get("ID"));
        map1.put("username",map.get("UserName"));
        map1.put("password",map.get("password"));
        map1.put("phone",map.get("Mobile"));
        map1.put("Status",map.get("Status"));
        HttpClientUtil.postHttpOutbound(updateUserOp,redisService.getVal("outbound."+map.get("username"))+"",map1);
//        if (csts!=null && csts.size()>0){
//            String salesMobile = csts.get(0).get("salesMobile")+"";
//            String status = csts.get(0).get("status")+"";
//            String salesName = csts.get(0).get("salesName")+"";
//            //判断如果有修改
//            if (!status.equals(data.get("Status")+"") || (data.get("Mobile")!=null && !"".equals(data.get("Mobile")+"") && !salesMobile.equals(data.get("Mobile")+""))
//                    || (data.get("EmployeeName")!=null && !"".equals(data.get("EmployeeName")+"") && !salesName.equals(data.get("EmployeeName")+""))){
//                //同步置业顾问信息
//                String projectId = "";
//                for (Map cst:csts) {
//                    projectId = projectId + cst.get("projectId")+",";
//                }
//                projectId = projectId.substring(0,projectId.length()-1);
//                Map jsonMap = new HashMap();
//                // 1：在职：2：离职
//                if ("0".equals(data.get("Status"))){
//                    jsonMap.put("leave", 2);
//                }else{
//                    jsonMap.put("leave", 1);
//                }
//                jsonMap.put("wlkAccount", csts.get(0).get("wlkAccount"));
//                jsonMap.put("projectId", projectId);
//                jsonMap.put("salesId", data.get("ID"));
//                jsonMap.put("salesName", data.get("EmployeeName"));
//                jsonMap.put("salesMobile", data.get("Mobile"));
//                //1：正常 -1：冻结
//                jsonMap.put("status", 1);
//                JSONObject re = HttpRequestUtil.httpPost(consultantSyncURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
//                if ("200".equals(re.getString("code")+"") && !salesMobile.equals(data.get("Mobile")+"")){
//                    //更新第三方账号信息
//                    Map up = new HashMap();
//                    up.put("salesId",data.get("ID"));
//                    up.put("thirdParty","1");
//                    up.put("salesMobile",data.get("Mobile"));
//                    userMessageDao.updateSalesThird(up);
//                }
//            }
//        }
        return i;
    }

    /**
     * 禁用/启用 用户账号
     *
     * @param map
     * @return
     */
    @Override
    public int updateUserStatus(Map map) {
        try {
            if (!map.isEmpty()) {
                return userMessageDao.updateUserStatus(map);
            }
        } catch (Exception e) {
            throw new BadRequestException(-20_0001, e);
        }
        return 0;
    }

    /**
     * 移除用户
     *
     * @param map
     * @return
     */
    @Override
    public int deleteUser(Map map) {
//        Map userMessageMap = userMessageDao.getJobSales(map.get("ID").toString(), "1");
//        if (userMessageMap != null) {
//            //不为空则调同步
//            Map jsonMap = new HashMap();
//            // 1：在职：2：离职
//            jsonMap.put("leave", 1);
//            //1：正常 -1：冻结
//            jsonMap.put("status", -1);
//            // 同步置业顾问
//            jsonMap.put("projectId", userMessageMap.get("projectId"));
//            jsonMap.put("salesId", userMessageMap.get("salesId"));
//            jsonMap.put("salesName", userMessageMap.get("salesName"));
//            jsonMap.put("salesMobile", userMessageMap.get("salesMobile"));
//            jsonMap.put("wlkAccount", userMessageMap.get("wlkAccount"));
//            HttpRequestUtil.httpPost(consultantSyncURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
//        }
        //删除外呼系统坐席
        Map id = userMessageDao.getUserJob(map.get("ID") + "");
        Map jobId1 = jobMapper.getOrgJobId(id.get("JobID")+"");
        if("1".equals(map.get("type"))){


//            String jobCodeCom = jobId1.get("JobCode") + "";
//            Set<String> validStrings = new HashSet<>();
//            validStrings.add("qfsj");
//            validStrings.add("qyqfsj");
//            validStrings.add("qyfz");
//            validStrings.add("qyz");
//            validStrings.add("qyzygw");
//            validStrings.add("qyzszj");
//            validStrings.add("qyxsjl");
//            validStrings.add("qyyxjl");
//            validStrings.add("qycsss");
//            validStrings.add("10001");
//            validStrings.add("xmz");
//            validStrings.add("yxjl");
//            validStrings.add("zszj");
//            validStrings.add("xsjl");
//            validStrings.add("zygw");
//            if (validStrings.contains(jobCodeCom)){
                Map<String, Object> map1 = new HashMap<>();
                map1.put("uid",id.get("AccountID"));
                map1.put("did",jobId1.get("CommonJobID"));
                map1.put("sid",jobId1.get("JobOrgID"));
                HttpClientUtil.postHttpOutbound(deleteUserOp,redisService.getVal("outbound."+map.get("username"))+"",map1);
//            }

            return userMessageDao.deleteUser(map);

        }else{
            Map<String, Object> map1 = new HashMap<>();
            map1.put("uid",id.get("AccountID"));
            map1.put("did",jobId1.get("CommonJobID"));
            map1.put("sid",jobId1.get("JobOrgID"));
            map1.put("Status",map.get("status"));
            HttpClientUtil.postHttpOutbound(updateUserOp,redisService.getVal("outbound."+map.get("username"))+"",map1);
            return userMessageDao.updateUser(map);
        }

    }

    /**
     * 查询用户是否存在
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> userNameExists(Map map) {
        return userMessageDao.userNameExists(map);
    }

    /**
     * 从C_User表查询用户数据
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> getUserFromCuser(Map map) {
        return userMessageDao.getUserFromCuser(map);
    }

    /**
     * 岗位人员新增
     *
     * @param map
     * @return
     */
    @Override
    public int insertSystemJobUser(Map map) {
        return userMessageDao.insertSystemJobUser(map);
    }

    @Override
    public int insertSystemJobUsersRel(Map map) {
        return userMessageDao.insertSystemJobUsersRel(map);
    }

    /**
     * excel导出测试
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> userComomJobCodeByJobId(Map map) {
        return userMessageDao.userComomJobCodeByJobId(map);
    }

    /**
     * excel导出测试
     *
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> userProxyRegisterByUserId(Map map) {
        return userMessageDao.userProxyRegisterByUserId(map);
    }

    /**
     * excel导出测试
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> userProxyRegisterInvitationCode(Map map) {
        return userMessageDao.userProxyRegisterInvitationCode(map);
    }

    /**
     * excel导出测试
     *
     * @param map
     * @return
     */
    @Override
    public List<Map> saleAccountLogInsert(Map map) {
        return userMessageDao.saleAccountLogInsert(map);
    }

    /**
     * 人员信息更新
     *
     * @param map
     * @return
     */
    @Override
    public int systemUserUpdateTwo(Map map) {
        return userMessageDao.systemUserUpdateTwo(map);
    }

    @Override
    public int systemLogInsert(Map map) {
        return userMessageDao.systemLogInsert(map);
    }

    /**
     * 获取同步状态
     * @return
     */
    @Override
    public Map getSynStatus() {
        Map resultMap = new HashMap();
        //查询人员同步开始时间
        Date userStartTime = userMessageDao.getSynStartTime("人员开始同步");
        //查询交易同步开始时间
        Date tradeStartTime = userMessageDao.getSynStartTime("交易信息开始同步");
        //查询回款同步开始时间
//        Date recStartTime = userMessageDao.getSynStartTime("应收实收开始同步");
        //查询人员同步结束时间
        Date userExecutTime = userMessageDao.getSynExecutTime("(TaskName = '人员同步完成' or TaskName = '人员同步异常！')");
        //查询交易同步结束时间
        Date tradeExecutTime = userMessageDao.getSynExecutTime("(TaskName = '交易同步成功' or TaskName = '交易同步发生异常')");
        //查询回款同步结束时间
//        Date recExecutTime = userMessageDao.getSynExecutTime("(TaskName = '应收实收同步成功' or TaskName = '应收实收数据同步异常')");
        //根据时间判断人员同步状态（0：同步完成 1：未完成）
        if (userStartTime==null){
            resultMap.put("user",0);
        }else{
            if (userExecutTime==null){
                resultMap.put("user",1);
            }else{
                if (userExecutTime.compareTo(userStartTime)>0){
                    resultMap.put("user",0);
                }else{
                    resultMap.put("user",1);
                }
            }
        }
        //根据时间判断交易同步状态
        if (tradeStartTime==null){
            resultMap.put("trade",0);
        }else{
            if (tradeExecutTime==null){
                resultMap.put("trade",1);
            }else{
                if (tradeExecutTime.compareTo(tradeStartTime)>0){
                    resultMap.put("trade",0);
                }else{
                    resultMap.put("trade",1);
                }
            }
        }
        //根据时间判断回款同步状态
//        if (recStartTime==null){
//            resultMap.put("rec",0);
//        }else{
//            if (recExecutTime==null){
//                resultMap.put("rec",1);
//            }else{
//                if (recExecutTime.compareTo(recStartTime)>0){
//                    resultMap.put("rec",0);
//                }else{
//                    resultMap.put("rec",1);
//                }
//            }
//        }
        return resultMap;
    }
}
