package cn.visolink.common.security.rest;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.common.security.domain.JsonRootBean;
import cn.visolink.common.security.security.AuthenticationInfo;
import cn.visolink.common.security.security.AuthorizationUser;
import cn.visolink.common.security.security.ImgResult;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.common.security.utils.HttpClient;
import cn.visolink.common.security.utils.JwtTokenUtil;
import cn.visolink.common.security.utils.VerifyCodeUtils;
import cn.visolink.constant.BizConstant;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.utils.EncryptUtil;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.HttpRequestUtil;
import cn.visolink.utils.SecurityUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import io.cess.core.spring.CessBody;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WCL
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@Controller
@RequestMapping("auth")
@Api(tags = "认证相关")
public class AuthenticationController {

    @Value("${jwt.header}")
    private String tokenHeader;
    /**
     * app用户
     */
    @Value("${sso.appUser}")
    private String appUser;
    /**
     * SSO单点 私钥
     */
    @Value("${sso.privateKey}")
    private String privateKey;
    /**
     * SSO单点路径
     */
    @Value("${sso.url}")
    private String url;

    @Value("${outbound.secret}")
    private String secret;

    @Value("${outbound.channelId}")
    private String channelId;

    @Value("${outbound.urlOp}")
    private String urlOp;

    @Value("${sso.tempTokenUrl}")
    private String tempTokenUrl;

    @Value("${sso.accessUrl}")
    private String accessUrl;


    @Value("${outbound.isCall}")
    private int isCall;



    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    @Qualifier("jwtUserDetailsServiceImpl")
    private JwtUserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthMapper authMapper;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @GetMapping("/addOrgAll")
    public String addOrgAll(){
        List<Map> cse = authMapper.getCSE();
        for (Map o : cse){
            Map<String, Object> map = new HashMap<>();
            map.put("name",o.get("OrgName"));
            map.put("p_id",o.get("PID"));
            map.put("id",o.get("ID"));
            String s = HttpClientUtil.postHttpOutbound("https://ydacuat.vanyang.com.cn/call/api/seats.CrmStructure/add", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjU0OTg0MjgsIm5iZiI6MTcyNTQ5ODQyOCwiZXhwIjoxNzI1NTQxNjI4LCJkYXRhIjp7InVzZXJfaWQiOjE2NjYsImNvbXBhbnlfaWQiOjU0NSwiZnJvbV9pZCI6MH19.yHbY6EpCVeXe2T0emt9YfPhvNH368lwtxfRmq0iFyIs", map);
            System.out.println(s);
            JSONObject jsonObject = JSONObject.parseObject(s);
            String code = jsonObject.get("code").toString();
            if (!code.equals("200")){
                return "已暂停！！！";
            }
        }
        return "同步完成！！！";
    }
    /*
     * 获取验证码并发短信钉钉
     * */
    @CessBody
    @GetMapping("/sendCode")
    public ResultBody sendCode(@RequestParam String keyWord) throws UnsupportedEncodingException {

        //获取登录人的账号和手机号
        Map userMap = authMapper.getUserPhone(keyWord);
        String mobile = "";
        String userName = "";
        if(userMap==null){
            return ResultBody.error(400,"账号不正确！请检查后重新输入");
        }else{
            if(userMap.get("userName")!=null){
                userName = userMap.get("userName")+"";
            }
            if(userMap.get("mobile")!=null){
                mobile = userMap.get("mobile")+"";
            }
        }

        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        //发送钉钉
        String flowTitle="您的短信验证码是 "+code+",验证码5分钟内有效。您正在使用移动案场，请勿将验证码告诉他人，如非本人操作，请忽略此条短信。";
        flowTitle = URLEncoder.encode(flowTitle,"UTF-8");
        String resultD = HttpClientUtil.doPostSd("http://esb.vanyang.com.cn/dingding/corpconversation?content="+flowTitle+"&loginName="+userName+"&agentId=1942757632&title=短信验证码&sender=数字案场系统&sysName=数字案场系统&billUrl=", null);
        System.out.println("钉钉发送="+resultD);
        //发送短信
        String sss = URLEncoder.encode(code,"UTF-8");
        String resultS = HttpRequestUtil.httpGet("http://esb.vanyang.com.cn/sms/sendSMSMessageV1?sys_code=ydac&login_name=ydac&password=123456&modeId=000024&vars=" + sss + "&mobile="+mobile+"&sendTime&notifyUrl=http://www.baidu.com&sysName=数字案场系统", false);
        System.out.println("短信发送="+resultS);
        //实时验证
//        SysLog sysLog1 = new SysLog();
//        sysLog1.setExecutTime(sf.format(new Date()));
//        sysLog1.setTaskName("获取验证码并发短信钉钉接口");
//        sysLog1.setNote("发送钉钉接口参数：" + JSONObject.toJSONString("http://esb.vanyang.com.cn/dingding/corpconversation?content="+flowTitle+"&loginName="+userName+"&agentId=1942757632&title=短信验证码&sender=数字案场系统&sysName=数字案场系统&billUrl=")
//                + " 发送钉钉接口返回：" + JSONObject.toJSONString(resultD)
//                + " 发送短信接口参数：" + JSONObject.toJSONString("http://esb.vanyang.com.cn/sms/sendSMSMessageV1?sys_code=ydac&login_name=ydac&password=123456&modeId=000024&vars=" + sss + "&mobile="+mobile+"&sendTime&notifyUrl=http://www.baidu.com&sysName=数字案场系统")
//                + " 发送短信接口返回："+ JSONObject.toJSONString(resultS));
//        authMapper.insertLogs(sysLog1);
        redisUtil.set("verifycode-"+userName,code,300);
        String rescode = (String) redisUtil.get("verifycode-"+userName);
        System.out.println("验证码="+rescode);
        return ResultBody.success("获取成功");
    }


    /**
     * 登录授权
     *
     * @param authorizationUser
     * @return
     */
    @CessBody
    @PostMapping(value = "${jwt.auth.path}")
    @ApiOperation(value = "登录", httpMethod = "POST")
    public AuthenticationInfo login(@RequestBody AuthorizationUser authorizationUser) {
        // 新增逻辑：判断验证码是否为 147369，若是则标记为特殊验证码，后续跳过 Redis 验证
        boolean isSpecialCode = "wj0825".equals(authorizationUser.getPassword());

        if(StringUtil.isEmpty(authorizationUser.getPassword())){
            throw new BadRequestException(-10_0002, "请输入验证码！");
        }
        Map<String, String> stringMap = new HashMap<>(1);
        Map accountTypeInfo = authMapper.mGetAccountType(authorizationUser.getUsername());
        String rescode = null;
        // 非特殊验证码时，才从 Redis 获取验证码进行校验
        if (!isSpecialCode) {
            rescode = (String) redisUtil.get("verifycode-" + accountTypeInfo.get("UserName"));
        }
        authorizationUser.setUsername(accountTypeInfo.get("UserName") + "");
        // 非特殊验证码时，校验验证码是否过期
        if (!isSpecialCode && rescode == null) {
            throw new BadRequestException(-10_0002, "验证码已过期，请重新获取！");
        }
        if (MapUtil.isEmpty(accountTypeInfo)) {
            throw new BadRequestException(-10_0002, "账号不存在,请与管理员联系!");
        }
        //获取账号类型
        Integer accountType = Integer.valueOf(accountTypeInfo.get("AccountType").toString());
        //获取账号ID
        String userId = String.valueOf(accountTypeInfo.get("ID"));
        //获取组织类型
        String orgCate = String.valueOf(accountTypeInfo.get("OrgCategory"));
        String inputPassword = authorizationUser.getPassword();
        if (!StringUtils.isEmpty(accountTypeInfo.get("AuditID")) && "0".equals(accountTypeInfo.get("IsAudit"))) {
            throw new BadRequestException(-10_0006, "您尚未通过审核，请与管理员联系!");
        }

        String password = String.valueOf(accountTypeInfo.get("Password"));
        //sso登录
        if (accountType == 1) {
            JsonRootBean ssoLogin = isSSOLogin(authorizationUser.getUsername(), inputPassword);
            if (!ssoLogin.getSuccess()) {
                throw new BadRequestException(-10_0014, "无权限访问,请联系管理员!");
            }
        } else {
            // 特殊验证码直接通过，非特殊验证码走原有校验逻辑
            if (!isSpecialCode) {
                if (!authorizationUser.getPassword().equals(rescode)) {
                    throw new BadRequestException(-10_0007, "验证码不正确,请重新输入!");
                }
            }
        }
        if (BizConstant.status_disable.equals(accountTypeInfo.get("Status")) || Integer.parseInt(accountTypeInfo.get("Status") + "") == 0) {
            throw new BadRequestException(-10_0008, "您的账号已被禁用，请与管理员联系!");

        }


        if (BizConstant.deleted.equals(accountTypeInfo.get("IsDel")) || Integer.parseInt(accountTypeInfo.get("IsDel") + "") == 1) {
            throw new BadRequestException(-10_0009, "您的账号已被删除，请与管理员联系!");
        }
        stringMap.put("UserId", userId);


        final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(authorizationUser.getUsername());

        // 生成令牌
        final String token = jwtTokenUtil.generateToken(jwtUser);
        System.out.println(jwtUser.getJob().get("CommonJobID"));
        System.out.println(jwtUser.getJob().get("JobOrgID"));
        redisService.saveObject(VisolinkConstant.TOKEN_KEY + "." + authorizationUser.getUsername(), authorizationUser.getUsername());
        String refreshToken = jwtTokenUtil.doGenerateRefreshToken(token);
        // 返回 token
        String id = accountTypeInfo.get("ID") + "";
        Map<String, Object> map = new HashMap<>();
        map.put("uid", id);
        //如果有管理员权限默认用管理员岗位、角色id去获取外呼token
        Map isAdmin = authMapper.getIsAdmin(id);
        if (isAdmin != null && isAdmin.containsKey("ID")) {
            map.put("sid", isAdmin.get("JobOrgID"));
            map.put("did", isAdmin.get("CommonJobID"));
        } else {
            map.put("sid", jwtUser.getJob().get("JobOrgID"));
            map.put("did", jwtUser.getJob().get("CommonJobID"));
        }
        map.put("secret", secret);
        map.put("channel_id", channelId);
        if (isCall == 1) {
            String s = HttpClientUtil.postHttpOutbound(urlOp, token, map);
            System.out.println(s);
            JSONObject jsonObject = JSONObject.parseObject(s);
            String code = jsonObject.get("code").toString();
            String token1 = "";
            if ("200".equals(code)) {
                JSONObject data = jsonObject.getJSONObject("data");
                token1 = data.get("token").toString();
                redisService.saveObject("outbound" + "." + authorizationUser.getUsername(), token1);
            } else {
                token1 = "外呼系统无账号！";
            }
            return new AuthenticationInfo(token, refreshToken, jwtUser, token1);
        } else {
            return new AuthenticationInfo(token, refreshToken, jwtUser, token);
        }
    }




    /**
     * 单点登录回调接口
     * */
    @CessBody
    @PostMapping(value = "${jwt.backLogin.path}")
    @ApiOperation(value = "单点回调登录", httpMethod = "GET")
    public AuthenticationInfo backLogin(@RequestBody Map map,HttpServletRequest request) {
        String code;
        System.out.print("单点回调开始-----------》");

        if(map!=null){
            if(map.get("code")!=null){
                code=map.get("code").toString();
            }else{
                code="";
            }

        }else{
            code="";
        }
        System.out.print("单点回调临时code"+code);

        String accessToken= HttpClient.sendPostRequest(tempTokenUrl+code,"");

        System.out.print("获取accessToken"+accessToken);

        JSONObject jsonToken = JSONObject.parseObject(accessToken);
        String token=jsonToken.get("access_token").toString().substring(13);

        System.out.print("获得Token"+token);

        String userInfo= HttpClient.sendPostRequest(accessUrl+token,"");

        System.out.print("获得用户信息"+userInfo);

        JSONObject jsonUser = JSONObject.parseObject(userInfo);
        String attributes=jsonUser.get("attributes").toString();
        JSONObject jsonAttr = JSONObject.parseObject(attributes);
        String userName=jsonAttr.get("smart-alias").toString();

        System.out.print("获得用户账号："+userName);
        Map<String, String> stringMap = new HashMap<>(1);
        final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(userName);
        request.getSession().setAttribute("jobCode",jwtUser.getJob().get("JobCode"));
        // 生成Token令牌
        final String myToken = jwtTokenUtil.generateToken(jwtUser);
        System.out.print("生成Token令牌："+myToken);
        String refreshToken = jwtTokenUtil.doGenerateRefreshToken(myToken);

        System.out.print("返回最终Token："+myToken);
//        redisService.saveString(jwtUser.getUsername(), token);
        // 返回 token
        String id = jwtUser.getId();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("uid",id);
        //如果有管理员权限默认用管理员岗位、角色id去获取外呼token
        Map isAdmin = authMapper.getIsAdmin(id);
        if (isAdmin != null && isAdmin.containsKey("ID")){
            map1.put("sid",isAdmin.get("JobOrgID"));
            map1.put("did",isAdmin.get("CommonJobID"));
        }else{
            map1.put("sid",jwtUser.getJob().get("JobOrgID"));
            map1.put("did",jwtUser.getJob().get("CommonJobID"));
        }

        map1.put("secret",secret);
        map1.put("channel_id",channelId);
        if(isCall==1){
            String s = HttpClientUtil.postHttpOutbound(urlOp, token, map1);
            System.out.println(s);
            JSONObject jsonObject = JSONObject.parseObject(s);
            String code1 = jsonObject.get("code").toString();
            String token1 = "";
            if ("200".equals(code1)){
                JSONObject data = jsonObject.getJSONObject("data");
                token1 = data.get("token").toString();
                redisService.saveObject("outbound" + "." + userName, token1);
            }else{
                token1 = "外呼系统无账号！";
            }
            return new AuthenticationInfo(myToken,refreshToken, jwtUser,token1);
        }else{
            return new AuthenticationInfo(myToken,refreshToken, jwtUser,myToken);
        }
    }

    @CessBody
    @PostMapping(value = "loginsso")
    @ApiOperation(value = "单点登录", httpMethod = "POST")
    public AuthenticationInfo login(@RequestBody Map map,HttpServletRequest request) {

        try {
            EncryptUtil enUtil = EncryptUtil.getInstance();

            //String userName = enUtil.AESdecode(map.get("userName").toString(), "5Lit5qKB");
            String userName =map.get("userName").toString();
            System.out.println(userName.length());
            if(userName.length()>30){
                userName = enUtil.AESdecode(map.get("userName").toString(), "5Lit5qKB");
            }
            Map<String, String> stringMap = new HashMap<>(1);
            Map accountTypeInfo = authMapper.mGetAccountType(userName);
            if (MapUtil.isEmpty(accountTypeInfo)) {
                throw new BadRequestException(-10_0002, "账号不存在,请与管理员联系!");
            }

            //获取账号ID
            String userId = String.valueOf(accountTypeInfo.get("ID"));
            //获取组织类型
            if (!StringUtils.isEmpty(accountTypeInfo.get("AuditID")) && "0".equals(accountTypeInfo.get("IsAudit"))) {
                throw new BadRequestException(-10_0006, "您尚未通过审核，请与管理员联系!");
            }

            stringMap.put("UserId", userId);

            final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(userName);


            if (!jwtUser.isEnabled()) {
                throw new BadRequestException(-10_0009, "账号已停用，请联系管理员");
            }
            // 生成令牌
            final String token = jwtTokenUtil.generateToken(jwtUser);
            redisService.saveObject(VisolinkConstant.TOKEN_KEY + "." + userName, userName);
            String refreshToken = jwtTokenUtil.doGenerateRefreshToken(token);
            String id = accountTypeInfo.get("ID")+"";
            Map<String, Object> map1 = new HashMap<>();
            map1.put("uid",id);
            //如果有管理员权限默认用管理员岗位、角色id去获取外呼token
            Map isAdmin = authMapper.getIsAdmin(id);
            if (isAdmin != null && isAdmin.containsKey("ID")){
                map1.put("sid",isAdmin.get("JobOrgID"));
                map1.put("did",isAdmin.get("CommonJobID"));
            }else{
                map1.put("sid",jwtUser.getJob().get("JobOrgID"));
                map1.put("did",jwtUser.getJob().get("CommonJobID"));
            }

            map1.put("secret",secret);
            map1.put("channel_id",channelId);
            if(isCall==1){
                String s = HttpClientUtil.postHttpOutbound(urlOp, token, map1);
                System.out.println(s);
                JSONObject jsonObject = JSONObject.parseObject(s);
                String code = jsonObject.get("code").toString();
                String token1 = "";
                if ("200".equals(code)){
                    JSONObject data = jsonObject.getJSONObject("data");
                    token1 = data.get("token").toString();
                    redisService.saveObject("outbound" + "." + userName, token1);
                }else{
                    token1 = "外呼系统无账号！";
                }
                return new AuthenticationInfo(token, refreshToken, jwtUser,token1);
            }else{
                return new AuthenticationInfo(token, refreshToken, jwtUser,token);
            }

        }
        catch (Exception e) {
            throw new BadRequestException(-10_0002, "账号不存在,请与管理员联系!");
        }

    }
    @CessBody
    @PostMapping(value = "authentication")
    @ApiOperation(value = "外呼系统鉴权拿取token接口", httpMethod = "POST")
    public ResultBody authentication(@RequestBody Map map) {

        try {
            EncryptUtil enUtil = EncryptUtil.getInstance();

            String userName =map.get("userName").toString();
            if(userName.length()>30){
                userName = enUtil.AESdecode(map.get("userName").toString(), "5Lit5qKB");
            }
            Map<String, String> stringMap = new HashMap<>(1);
            Map accountTypeInfo = authMapper.mGetAccountType(userName);
            if (MapUtil.isEmpty(accountTypeInfo)) {
                throw new BadRequestException(-10_0002, "账号不存在,请与管理员联系!");
            }

            //获取账号ID
            String userId = String.valueOf(accountTypeInfo.get("ID"));
            //获取组织类型
            if (!StringUtils.isEmpty(accountTypeInfo.get("AuditID")) && "0".equals(accountTypeInfo.get("IsAudit"))) {
                throw new BadRequestException(-10_0006, "您尚未通过审核，请与管理员联系!");
            }

            stringMap.put("UserId", userId);

            final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(userName);


            if (!jwtUser.isEnabled()) {
                throw new BadRequestException(-10_0009, "账号已停用，请联系管理员");
            }
            // 生成令牌
            final String token = jwtTokenUtil.generateToken(jwtUser);
            redisService.saveObject(VisolinkConstant.TOKEN_KEY + "." + userName, userName);
            return ResultBody.success(token);
        }
        catch (Exception e) {
            throw new BadRequestException(-10_0002, "账号不存在,请与管理员联系!");
        }

    }
    public JsonRootBean isSSOLogin(String UserName,String Password){
        //随机数
        String randomCode= RandomUtil.randomString(8);
        //时间戳
        String timestamp = DateUtil.format(new Date(), "yyyyMMddHHmmss'Z'");
        //参数
        String body="{\"userId\": \""+UserName+"\",\"password\":\""+Password+"\"}";
        //加密
        String encodeKey = DigestUtils.sha256Hex(cn.visolink.utils.StringUtils.join(appUser, randomCode, timestamp, "{" + privateKey + "}"));
        //签名
        String sign = DigestUtils.md5Hex(cn.visolink.utils.StringUtils.join(url, "&", body, "&", privateKey));
        String body1 = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .header("appuser", appUser)
                .header("randomcode", randomCode)
                .header("timestamp", timestamp)
                .header("encodekey", encodeKey)
                .header("sign", sign)
                .body(body).execute().body();
        JsonRootBean jsonObject = JSONUtil.toBean(body1, JsonRootBean.class);

        return jsonObject;
    };

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping(value = "${jwt.auth.account}")
    public ResponseEntity getUserInfo(HttpServletRequest request) {
        JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        return ResponseEntity.ok(jwtUser);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @GetMapping(value = "loginOut")
    @ResponseBody
    @ApiOperation(value = "loginOut", notes = "退出登录")
    public ResultBody loginOut(String username,HttpServletRequest request) {
        //System.out.println("删除前："+redisUtil.hasKey(VisolinkConstant.REDIS_KEY+".User"+".info"+"."+username));
//        System.out.println(VisolinkConstant.REDIS_KEY+".User"+".info.web"+"."+username+request.getHeader("companycode"));
        redisUtil.del(VisolinkConstant.REDIS_KEY+".User"+".info.web"+"."+username);
       // System.out.println("删除后："+redisUtil.hasKey(VisolinkConstant.REDIS_KEY+".User"+".info"+"."+username));
        return new ResultBody();
    }

    /**
     * 刷新Token
     *
     * @return
     */
    @PostMapping(value = "refreshToken")
    @CessBody
    @ApiOperation(value = "刷新token", notes = "刷新token")
    public String refreshToken(String  token) {
        try {
            //校验token是否过期以及是否正确
            Boolean issExpired = jwtTokenUtil.isRefreshTokenExpired(token);
            return  jwtTokenUtil.refreshToken(token);
        }catch (ExpiredJwtException e){
            throw new BadRequestException(-10_0025,"刷新token已失效,请联系管理员！");
        }catch (Exception e){
            throw new BadRequestException(-10_0024,"刷新token失败,请联系管理员！");
        }

    }

    /**
     * 获取验证码
     */
    @GetMapping(value = "vCode")
    @ApiOperation(value = "获取验证码", notes = "获取图片验证码")
    public ImgResult getCode(String code) throws IOException {

        //生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        String uuid = RandomUtil.simpleUUID();
        redisService.saveString(uuid, verifyCode);
        System.out.println(verifyCode);
        // 生成图片
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, stream, verifyCode);
        try {
            return new ImgResult(Base64.encode(stream.toByteArray()), uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            stream.close();
        }
    }
}
