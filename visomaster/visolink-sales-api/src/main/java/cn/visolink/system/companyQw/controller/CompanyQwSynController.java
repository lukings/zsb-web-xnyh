package cn.visolink.system.companyQw.controller;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.utils.DynamicDataSource;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.companyQw.dao.CompanyQwDao;
import cn.visolink.system.companyQw.dao.CompanyQwSynDao;
import cn.visolink.system.companyQw.model.ChannelCode;
import cn.visolink.system.companyQw.model.MediaDetail;
import cn.visolink.system.companyQw.model.QwUserVo;
import cn.visolink.system.companyQw.service.CompanyQwSynService;
import cn.visolink.system.companyQw.util.WXBizMsgCrypt;
import cn.visolink.system.companyQw.util.XmlUtil;
import cn.visolink.utils.HttpRequestUtil;
import cn.visolink.utils.StringUtils;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CompanyQwSynController
 * @Author wanggang
 * @Description //同步企微数据
 * @Date 2022/1/4 9:28
 **/
@RestController
@Api(tags = "企微组织同步")
@RequestMapping("qwSyn")
public class CompanyQwSynController {

    @Autowired
    private CompanyQwSynService companyQwSynService;
    @Autowired
    private CompanyQwSynDao companyQwSynDao;
    @Autowired
    private CompanyQwDao companyQwDao;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${WX_TOKEN_PATH}")
    private String wxTokenPath;

    @Log("保存组织企微创建部门")
    @CessBody
    @ApiOperation(value = "保存组织企微创建部门", notes = "保存组织企微创建部门")
    @RequestMapping(value = "/addDept", method = RequestMethod.POST)
    public String addDept(@RequestBody Map map) {
        if (map==null || map.get("orgName")==null || map.get("orgId")==null
                || map.get("orgPid")==null){
            return "-1";
        }
        String orgId = map.get("orgId").toString();
        String orgName = map.get("orgName").toString();
        String orgPid = map.get("orgPid").toString();
        String proId = null;
        if (map.get("proId")!=null && !"".equals(map.get("proId").toString())){
            proId = map.get("proId").toString();
        }
        return companyQwSynService.addDept(orgName,orgPid,orgId,proId);
    }

    @Log("保存人员企微添加成员")
    @CessBody
    @ApiOperation(value = "保存人员企微添加成员", notes = "保存人员企微添加成员")
    @RequestMapping(value = "/pushUser", method = RequestMethod.POST)
    public String pushUser(@RequestBody QwUserVo qwUserVo) {
        return companyQwSynService.pushUser(qwUserVo);
    }




    @Log("拉取企微组织数据")
    @CessBody
    @ApiOperation(value = "拉取企微组织数据", notes = "拉取企微组织数据")
    @RequestMapping(value = "/getQwOrgs", method = RequestMethod.GET)
    public ResultBody getQwOrgs(HttpServletRequest request) {
        return companyQwSynService.getQwOrgs(request);
    }


    @Log("初始化客户同步")
    @ApiOperation(value = "初始化客户同步", httpMethod = "GET")
    @GetMapping("/synCustomer")
    public void synCustomer(HttpServletRequest request, HttpServletResponse res) throws Exception {
        String token = "vMTGmzHZidybvee0RM77";
        String corpId = "wwf6f09735486f4925";
        String encodingAESKey = "oK6lnDlvQCttmu3RsSkafwOdmwV245S3w5VsSdHqY04";

        String companycode = request.getParameter("companycode");
        // 微信加密签名
        String msg_signature = request.getParameter("msg_signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        System.out.println("request=" + request.getRequestURL());

        PrintWriter out = res.getWriter();
        // 通过检验msg_signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        String result = null;
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAESKey, corpId);
            result = wxcpt.verifyUrl(msg_signature, timestamp, nonce, echostr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            result = token;
        }
        System.out.println("result=" + result);
        out.print(result);
        out.close();
        out = null;

    }

    @Log("客户同步")
    @ApiOperation(value = "客户同步", httpMethod = "POST")
    @PostMapping("/synCustomer")
    public void synCustomer(@RequestBody(required = false) String postdata, HttpServletRequest request) throws Exception {
        System.out.println("接收到请求: " + postdata);
        String token = "vMTGmzHZidybvee0RM77";
        String corpId = "wwf6f09735486f4925";
        String encodingAESKey = "oK6lnDlvQCttmu3RsSkafwOdmwV245S3w5VsSdHqY04";
        // 公司编码
        String companycode = request.getParameter("companycode");
        // 微信加密签名
        String msg_signature = request.getParameter("msg_signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        Map<String, String> authorizedMap = new HashMap<>();
        System.out.println("request=" + request.getRequestURL());
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAESKey, corpId);
            String sMsg = wxcpt.decryptMsg(msg_signature, timestamp, nonce, postdata);
            System.out.println("after decrypt msg: " + sMsg);
            authorizedMap = XmlUtil.xmlToMap(sMsg);

            System.out.println("resmsg: " + JSONObject.toJSONString(authorizedMap));
            // TODO: 解析出明文json标签的内容进行处理
            // For example:
            JSONObject json = JSON.parseObject(JSONObject.toJSONString(authorizedMap));
            String Content = json.getString("ChangeType");

            System.out.println("ChangeType：" + Content);

            String companyCode = companycode;
            Object obj = redisUtil.get(companyCode+"zhyx");
            if(obj!=null) {
                System.out.println("数据源信息:"+obj.toString());
                JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                DynamicDataSource.dataSourcesMap.get(jsonObject.get("database"));
                if (DynamicDataSource.dataSourcesMap.get(jsonObject.get("database")) == null) {
                    System.out.println("创建数据源");
                    DruidDataSource druidDataSource = new DruidDataSource();
                    druidDataSource.setUrl("jdbc:mysql://118.190.56.178:3306/" + jsonObject.get("database") + "?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true");
                    druidDataSource.setUsername("root");
                    druidDataSource.setPassword("root");
                    druidDataSource.setRemoveAbandoned(true);
                    druidDataSource.setRemoveAbandonedTimeout(1800);
                    druidDataSource.setTimeBetweenEvictionRunsMillis(90000);
                    druidDataSource.setMinEvictableIdleTimeMillis(1800000);
                    druidDataSource.setTestWhileIdle(true);
                    druidDataSource.setTestOnBorrow(true);
                    druidDataSource.setTestOnReturn(true);
                    druidDataSource.setValidationQuery("select 1");
                    druidDataSource.setFilters("stat");
                    druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    DynamicDataSource.dataSourcesMap.put(jsonObject.get("database"), druidDataSource);
                    DynamicDataSource.setDataSource(jsonObject.get("database").toString());
                } else {
                    System.out.println("获取数据源");
                    DynamicDataSource.setDataSource(jsonObject.get("database").toString());
                }
            }
            if("add_external_contact".equals(Content) || "edit_external_contact".equals(Content)
                    || "add_half_external_contact".equals(Content)
                    || "del_external_contact".equals(Content)
                    || "del_follow_user".equals(Content)){
                if ("add_external_contact".equals(Content) || "add_half_external_contact".equals(Content)){
                    //判断是否可以发送欢迎语
                    if (authorizedMap.get("WelcomeCode")!=null && !"".equals(authorizedMap.get("WelcomeCode")+"")){
                        String WelcomeCode = authorizedMap.get("WelcomeCode")+"";
                        //判断是否渠道码添加客户
                        if (authorizedMap.get("State")!=null && !"".equals(authorizedMap.get("State")+"")){
                            String State = authorizedMap.get("State")+"";
                            String UserID = authorizedMap.get("UserID")+"";
                            //判断渠道码是否设置了欢迎语
                            ChannelCode channelCode = companyQwSynDao.getChannelCodeIsWelcome(UserID,State);
                            if (channelCode.getIsAddWelcomeWords()!=null && "1".equals(channelCode.getIsAddWelcomeWords())){
                                //查询欢迎语素材
                                List<MediaDetail> mediaDetails = companyQwDao.getChannelCodeMediaDetails(channelCode.getId());
                                if (mediaDetails!=null && mediaDetails.size()>0){
                                    Map tagMap = new HashMap();
                                    tagMap.put("welcome_code",WelcomeCode);
                                    String accessToken = this.getWxToken(companycode,"2",false);
                                    List<Map> attachments = new ArrayList<>();
                                    for (MediaDetail media:mediaDetails) {
                                        if ("6".equals(media.getMediaType())){
                                            Map text = new HashMap();
                                            text.put("content",media.getTitle());
                                            tagMap.put("text",text);
                                        }else if ("1".equals(media.getMediaType())){
                                            Map text = new HashMap();
                                            Map image = new HashMap();
                                            image.put("media_id",media.getMediaId());
                                            text.put("image",image);
                                            text.put("msgtype","image");
                                            attachments.add(text);
                                        }else if ("2".equals(media.getMediaType())){
                                            Map text = new HashMap();
                                            Map video = new HashMap();
                                            video.put("media_id",media.getMediaId());
                                            text.put("video",video);
                                            text.put("msgtype","video");
                                            attachments.add(text);
                                        }else if ("3".equals(media.getMediaType())){
                                            Map text = new HashMap();
                                            Map miniprogram = new HashMap();
                                            miniprogram.put("title",media.getTitle());
                                            miniprogram.put("pic_media_id",media.getMediaId());
                                            miniprogram.put("appid",media.getAppid());
                                            miniprogram.put("page",media.getPage());
                                            text.put("miniprogram",miniprogram);
                                            text.put("msgtype","miniprogram");
                                            attachments.add(text);
                                        }else if ("4".equals(media.getMediaType())){
                                            Map text = new HashMap();
                                            Map link = new HashMap();
                                            link.put("title",media.getTitle());
                                            link.put("url",media.getMediaUrl());
                                            text.put("link",link);
                                            text.put("msgtype","link");
                                            attachments.add(text);
                                        }else if ("5".equals(media.getMediaType())){
                                            Map text = new HashMap();
                                            Map file = new HashMap();
                                            file.put("media_id",media.getMediaId());
                                            text.put("file",file);
                                            text.put("msgtype","file");
                                            attachments.add(text);
                                        }
                                    }
                                    if (attachments.size()>0){
                                        tagMap.put("attachments",attachments);
                                    }
                                    //调用企微接口发送欢迎语
                                    JSONObject res = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/send_welcome_msg?access_token="+accessToken,JSONObject.parseObject(JSONObject.toJSONString(tagMap)),false);
                                }
                            }
                        }
                    }
                }
                //保存临时表
                Integer date = Integer.valueOf(authorizedMap.get("CreateTime"));
                String CreateTime = this.transForDate1(date);
                authorizedMap.put("CreateTime",CreateTime);
                companyQwSynDao.addQwCstTemp(authorizedMap);
            }
        } catch (Exception e) {
            // TODO
            // 解密失败，失败原因请查看异常
            e.printStackTrace();
        }finally {
            DynamicDataSource.clear();
        }
    }

    @Log("初始化通讯录同步")
    @ApiOperation(value = "初始化通讯录同步", httpMethod = "GET")
    @GetMapping("/synUser")
    public void synCst(HttpServletRequest request, HttpServletResponse res) throws Exception {
        String token = "5wQbe9DufLziyuRBb7Gu";
        String corpId = "wwf6f09735486f4925";
        String encodingAESKey = "ICBegIp8vORktRSsbQNJqsKnxTR26WaKuhgfU2XIsKo";
        // 公司编码
        String companycode = request.getParameter("companycode");
        // 微信加密签名
        String msg_signature = request.getParameter("msg_signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        System.out.println("request=" + request.getRequestURL());

        PrintWriter out = res.getWriter();
        // 通过检验msg_signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        String result = null;
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAESKey, corpId);
            result = wxcpt.verifyUrl(msg_signature, timestamp, nonce, echostr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            result = token;
        }
        System.out.println("result=" + result);
        out.print(result);
        out.close();
        out = null;

    }

    @Log("通讯录同步")
    @ApiOperation(value = "通讯录同步", httpMethod = "POST")
    @PostMapping("/synUser")
    public void synCst(@RequestBody(required = false) String postdata, HttpServletRequest request) throws Exception {
        System.out.println("接收到请求: " + postdata);
        String token = "5wQbe9DufLziyuRBb7Gu";
        String corpId = "wwf6f09735486f4925";
        String encodingAESKey = "ICBegIp8vORktRSsbQNJqsKnxTR26WaKuhgfU2XIsKo";
        // 公司编码
        String companycode = request.getParameter("companycode");
        // 微信加密签名
        String msg_signature = request.getParameter("msg_signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        Map<String, String> authorizedMap = new HashMap<>();
        System.out.println("request=" + request.getRequestURL());
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAESKey, corpId);
            String sMsg = wxcpt.decryptMsg(msg_signature, timestamp, nonce, postdata);
            System.out.println("after decrypt msg: " + sMsg);
            authorizedMap = XmlUtil.xmlToMap(sMsg);
            // TODO: 解析出明文json标签的内容进行处理
            // For example:
            JSONObject json = JSON.parseObject(JSONObject.toJSONString(authorizedMap));
            String Content = json.getString("ChangeType");

            String companyCode = companycode;
            Object obj = redisUtil.get(companyCode+"zhyx");
            if(obj!=null) {
                System.out.println("数据源信息:"+obj.toString());
                JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                DynamicDataSource.dataSourcesMap.get(jsonObject.get("database"));
                if (DynamicDataSource.dataSourcesMap.get(jsonObject.get("database")) == null) {
                    System.out.println("创建数据源");
                    DruidDataSource druidDataSource = new DruidDataSource();
                    druidDataSource.setUrl("jdbc:mysql://118.190.56.178:3306/" + jsonObject.get("database") + "?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true");
                    druidDataSource.setUsername("root");
                    druidDataSource.setPassword("root");
                    druidDataSource.setRemoveAbandoned(true);
                    druidDataSource.setRemoveAbandonedTimeout(1800);
                    druidDataSource.setTimeBetweenEvictionRunsMillis(90000);
                    druidDataSource.setMinEvictableIdleTimeMillis(1800000);
                    druidDataSource.setTestWhileIdle(true);
                    druidDataSource.setTestOnBorrow(true);
                    druidDataSource.setTestOnReturn(true);
                    druidDataSource.setValidationQuery("select 1");
                    druidDataSource.setFilters("stat");
                    druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    DynamicDataSource.dataSourcesMap.put(jsonObject.get("database"), druidDataSource);
                    DynamicDataSource.setDataSource(jsonObject.get("database").toString());
                } else {
                    System.out.println("获取数据源");
                    DynamicDataSource.setDataSource(jsonObject.get("database").toString());
                }
            }
            if("create_user".equals(Content)){
                //保存临时表
                Integer date = Integer.valueOf(authorizedMap.get("CreateTime"));
                String CreateTime = this.transForDate1(date);
                authorizedMap.put("CreateTime",CreateTime);
                //获取部门信息
                String Department = json.getString("Department");
                String IsLeaderInDept = json.getString("IsLeaderInDept");
                String UserID = json.getString("UserID");
                String[] depts = Department.split(",");
                String[] isLeaders = IsLeaderInDept.split(",");
                List<Map> deptMaps = new ArrayList<>();
                for (int k = 0;k < depts.length; k++) {
                    String dept = depts[k];
                    String isLeader = isLeaders[k];
                    Map deptMap = new HashMap();
                    deptMap.put("dept_id",dept);
                    deptMap.put("user_id",UserID);
                    deptMap.put("direct_leader",isLeader);
                    deptMap.put("create_time",CreateTime);
                    deptMaps.add(deptMap);
                }
                //判断成员存在手机号
                String Mobile = json.getString("Mobile");
                if (!StringUtils.isEmpty(Mobile) && deptMaps.size()>0){
                    //查询此手机号的用户信息
                    Map userMap = new HashMap();
                    userMap.put("depts",depts);
                    userMap.put("mobile",Mobile);
                    String accountId = companyQwSynDao.getAcUserId(userMap);
                    if (!StringUtils.isEmpty(accountId)){
                        authorizedMap.put("accountId",accountId);
                    }
                }
                //保存成员
                companyQwSynDao.addQwUserNew(authorizedMap);
                //保存成员部门信息
                if (deptMaps.size()>0){
                    companyQwSynDao.addUserDepts(deptMaps);
                }
            }

            if("update_user".equals(Content)){
                //保存临时表
                Integer date = Integer.valueOf(authorizedMap.get("CreateTime"));
                String CreateTime = this.transForDate1(date);
                authorizedMap.put("CreateTime",CreateTime);
                String userid = json.getString("UserID");
                //查询原成员信息
                QwUserVo olduser = companyQwSynDao.getOldUser(userid);
                if (olduser!=null){

                    //获取部门信息
                    String Department = json.getString("Department");
                    String IsLeaderInDept = json.getString("IsLeaderInDept");
                    String NewUserID = json.getString("NewUserID");
                    List<Map> deptMaps = new ArrayList<>();
                    if (!StringUtils.isEmpty(Department)){
                        String[] depts = Department.split(",");
                        String[] isLeaders = IsLeaderInDept.split(",");
                        for (int k = 0;k < depts.length; k++) {
                            String dept = depts[k];
                            String isLeader = isLeaders[k];
                            Map deptMap = new HashMap();
                            deptMap.put("dept_id",dept);
                            if (StringUtils.isEmpty(NewUserID)){
                                deptMap.put("user_id",userid);
                            }else{
                                deptMap.put("user_id",NewUserID);
                            }
                            deptMap.put("direct_leader",isLeader);
                            deptMap.put("create_time",CreateTime);
                            deptMaps.add(deptMap);
                        }
                        //查询是否已绑定案场账号
                        if (StringUtils.isEmpty(olduser.getAccountId())){
                            //判断成员存在手机号
                            String Mobile = json.getString("Mobile");
                            if (!StringUtils.isEmpty(Mobile) && deptMaps.size()>0){
                                //查询此手机号的用户信息
                                Map userMap = new HashMap();
                                userMap.put("depts",depts);
                                userMap.put("mobile",Mobile);
                                String accountId = companyQwSynDao.getAcUserId(userMap);
                                if (!StringUtils.isEmpty(accountId)){
                                    authorizedMap.put("accountId",accountId);
                                }
                            }
                        }
                    }else{
                        List<String> depts = companyQwSynDao.getOldOrg(userid);
                        //查询是否已绑定案场账号
                        if (StringUtils.isEmpty(olduser.getAccountId())){
                            //判断成员存在手机号
                            String Mobile = json.getString("Mobile");
                            if (!StringUtils.isEmpty(Mobile) && deptMaps.size()>0){
                                //查询此手机号的用户信息
                                Map userMap = new HashMap();
                                userMap.put("depts",depts);
                                userMap.put("mobile",Mobile);
                                String accountId = companyQwSynDao.getAcUserId(userMap);
                                if (!StringUtils.isEmpty(accountId)){
                                    authorizedMap.put("accountId",accountId);
                                }
                            }
                        }
                    }
                    companyQwSynDao.updateQwUser(authorizedMap);
                    if (!StringUtils.isEmpty(NewUserID)){
                        //更新所有相关表userId
                        companyQwSynDao.updateAllUser(authorizedMap);
                    }
                    if (deptMaps.size()>0){
                        companyQwSynDao.delQwUserOrg(userid);
                        companyQwSynDao.addUserDepts(deptMaps);
                    }
                }
            }
            //删除成员 考虑离职
            if("delete_user".equals(Content)){
                Map delMap = new HashMap();
                //更新成员状态为退出企业
                Integer date = Integer.valueOf(authorizedMap.get("CreateTime"));
                String CreateTime = this.transForDate1(date);
                delMap.put("edit_time",CreateTime);
                delMap.put("userid",json.getString("UserID"));
                companyQwSynDao.delQwUser(delMap);
            }

        } catch (Exception e) {
            // TODO
            // 解密失败，失败原因请查看异常
            e.printStackTrace();
        } finally {
            DynamicDataSource.clear();
        }
    }

    @Log("初始化客服同步")
    @ApiOperation(value = "初始化客服同步", httpMethod = "GET")
    @GetMapping("/synService")
    public void synService(HttpServletRequest request, HttpServletResponse res) throws Exception {
        String token = "UNwU9Xaagu4FyX2AhHmOFtoom9";
        String corpId = "wwf6f09735486f4925";
        String encodingAESKey = "QRxbdV8y1SvakRCtb7gVjFUwPnLbEieIVlYiKfETEIL";

        String companycode = request.getParameter("companycode");
        // 微信加密签名
        String msg_signature = request.getParameter("msg_signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        System.out.println("request=" + request.getRequestURL());

        PrintWriter out = res.getWriter();
        // 通过检验msg_signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        String result = null;
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAESKey, corpId);
            result = wxcpt.verifyUrl(msg_signature, timestamp, nonce, echostr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            result = token;
        }
        System.out.println("result=" + result);
        out.print(result);
        out.close();
        out = null;

    }

    @Log("客服同步")
    @ApiOperation(value = "客服同步", httpMethod = "POST")
    @PostMapping("/synService")
    public void synService(@RequestBody(required = false) String postdata, HttpServletRequest request) throws Exception {
        System.out.println("接收到请求: " + postdata);
        String token = "UNwU9Xaagu4FyX2AhHmOFtoom9";
        String corpId = "wwf6f09735486f4925";
        String encodingAESKey = "QRxbdV8y1SvakRCtb7gVjFUwPnLbEieIVlYiKfETEIL";
        // 公司编码
        String companycode = request.getParameter("companycode");
        // 微信加密签名
        String msg_signature = request.getParameter("msg_signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        Map<String, String> authorizedMap = new HashMap<>();
        System.out.println("request=" + request.getRequestURL());
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAESKey, corpId);
            String sMsg = wxcpt.decryptMsg(msg_signature, timestamp, nonce, postdata);
            System.out.println("after decrypt msg: " + sMsg);
            authorizedMap = XmlUtil.xmlToMap(sMsg);

            System.out.println("客服回调: " + JSONObject.toJSONString(authorizedMap));
            // TODO: 解析出明文json标签的内容进行处理
            JSONObject json = JSON.parseObject(JSONObject.toJSONString(authorizedMap));

            String companyCode = companycode;
            Object obj = redisUtil.get(companyCode+"zhyx");
            if(obj!=null) {
                System.out.println("数据源信息:"+obj.toString());
                JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                DynamicDataSource.dataSourcesMap.get(jsonObject.get("database"));
                if (DynamicDataSource.dataSourcesMap.get(jsonObject.get("database")) == null) {
                    System.out.println("创建数据源");
                    DruidDataSource druidDataSource = new DruidDataSource();
                    druidDataSource.setUrl("jdbc:mysql://118.190.56.178:3306/" + jsonObject.get("database") + "?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true");
                    druidDataSource.setUsername("root");
                    druidDataSource.setPassword("root");
                    druidDataSource.setRemoveAbandoned(true);
                    druidDataSource.setRemoveAbandonedTimeout(1800);
                    druidDataSource.setTimeBetweenEvictionRunsMillis(90000);
                    druidDataSource.setMinEvictableIdleTimeMillis(1800000);
                    druidDataSource.setTestWhileIdle(true);
                    druidDataSource.setTestOnBorrow(true);
                    druidDataSource.setTestOnReturn(true);
                    druidDataSource.setValidationQuery("select 1");
                    druidDataSource.setFilters("stat");
                    druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    DynamicDataSource.dataSourcesMap.put(jsonObject.get("database"), druidDataSource);
                    DynamicDataSource.setDataSource(jsonObject.get("database").toString());
                } else {
                    System.out.println("获取数据源");
                    DynamicDataSource.setDataSource(jsonObject.get("database").toString());
                }
            }



        } catch (Exception e) {
            // TODO
            // 解密失败，失败原因请查看异常
            e.printStackTrace();
        }finally {
            DynamicDataSource.clear();
        }
    }


    public String transForDate1(Integer ms){
        String str = "";
        if(ms!=null){
            long msl=(long)ms*1000;
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if(ms!=null){
                try {
                    str=sdf.format(msl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    public String getWxToken(String companycode,String type,boolean flag){
        //type  分类 ：1通讯录token  2客户联系token 3应用 token
        //flag  true: 获取实时token  false: 读取缓存（默认）
        String token = "";
        if (flag){
            HttpRequestUtil.httpGet(wxTokenPath,false);
        }
        if ("1".equals(type)){
            token = redisUtil.get("QW_DATATOKEN_"+companycode).toString();
        }else if ("2".equals(type)){
            token = redisUtil.get("QW_CSTTOKEN_"+companycode).toString();
        }else if ("3".equals(type)){
            token = redisUtil.get("QW_APPTOKEN_"+companycode).toString();
        }else if ("4".equals(type)){
            token = redisUtil.get("QW_SERVICETOKEN_"+companycode).toString();
        }
        return token;
    }
//
//    @Log("拉取企微成员数据")
//    @CessBody
//    @ApiOperation(value = "拉取企微成员数据", notes = "拉取企微成员数据")
//    @RequestMapping(value = "/getQwUsers", method = RequestMethod.GET)
//    public ResultBody getQwUsers(HttpServletRequest request) {
//        return companyQwSynService.getQwUsers(request);
//    }
//
//    @Log("拉取企微成员客户数据")
//    @CessBody
//    @ApiOperation(value = "拉取企微成员客户数据", notes = "拉取企微成员客户数据")
//    @RequestMapping(value = "/getQwUserCsts", method = RequestMethod.GET)
//    public ResultBody getQwUserCsts(HttpServletRequest request) {
//        return companyQwSynService.getQwUserCsts(request);
//    }



}
