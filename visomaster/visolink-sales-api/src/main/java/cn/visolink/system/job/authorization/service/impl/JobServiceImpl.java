package cn.visolink.system.job.authorization.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.card.service.UserCardService;
import cn.visolink.system.channel.model.ProjectRuleDetail;
import cn.visolink.system.companyQw.model.QuitUserCst;
import cn.visolink.system.companyQw.model.QwUserVo;
import cn.visolink.system.companyQw.service.impl.CompanyQwSynServiceImpl;
import cn.visolink.system.job.authorization.mapper.JobMapper;
import cn.visolink.system.job.authorization.model.*;
import cn.visolink.system.job.authorization.service.JobService;
import cn.visolink.system.projectmanager.dao.projectmanagerDao;
import cn.visolink.system.usermanager.dao.UserManagerDao;
import cn.visolink.utils.*;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.10
 */

@Service
public class JobServiceImpl implements JobService {

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jt;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    projectmanagerDao managerDao;

    @Autowired
    private UserManagerDao userManagerDao;

    @Autowired
    private UserCardService userCardService;

    @Autowired
    private CompanyQwSynServiceImpl companyQwSynService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserManagerDao userMessageDao;



    @Value("${ConsultantSyncURL}")
    private String consultantSyncURL;

    @Value("${outbound.addUserOp}")
    private String addUserOp;

    @Value("${outbound.deleteUserOp}")
    private String deleteUserOp;

    @Value("${outbound.updateUserOp}")
    private String updateUserOp;


    @Autowired
    private RedisService redisService;

    @Value("${outbound.secret}")
    private String secret;

    @Value("${outbound.channelId}")
    private String channelId;

    @Value("${outbound.urlOp}")
    private String urlOp;
    /**
     * 获取所有岗位
     */
    @Override
    public PageInfo<Map> getJobByAuthId(Map map) {
        PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        List<Map> list = jobMapper.getJobByAuthId(map);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 查询所有组织架构
     */
    @Override
    public List<Map> getAllOrg(Map map) {
        return jobMapper.getAllOrg(map);
    }

    @Override
    public List<Map> getAllCommonJob(Map map) {
        return jobMapper.getAllCommonJob(map);
    }

    /**
     * 查询岗位下的人员列表，或根据姓名查询人员
     *
     * @param reqMap
     * @return
     */
    @Override
    public Map getSystemUserList(Map reqMap) {
        // form -> do 转换
        Map resultMap = new HashMap<>();
        int pageIndex = Integer.parseInt(reqMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        reqMap.put("pageIndex", i);
        List<Map> userList = jobMapper.getSystemJobUserList(reqMap);
        Integer count = jobMapper.getSystemJobUserListCount(reqMap);
        resultMap.put("list", userList);
        resultMap.put("total", count);
        return resultMap;
    }

    @Override
    public Map getDeSystemUserList(Map reqMap) {
        Map resultMap = new HashMap<>();
        int pageIndex = Integer.parseInt(reqMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        reqMap.put("pageIndex", i);
        List<Map> userList = jobMapper.getDeSystemJobUserList(reqMap);
        Integer count = jobMapper.getDeSystemJobUserListCount(reqMap);
        resultMap.put("list", userList);
        resultMap.put("total", count);
        return resultMap;
    }

    @Override
    public String removeUserRel(Map reqMap) {
        String result = "删除成功！";
        try{
            jobMapper.removeUserRel(reqMap.get("ID")+"");
        }catch (Exception e){
            e.printStackTrace();
            result = "删除异常！";
        }
        return result;
    }

    @Override
    public Map getUserDesc(Map reqMap) {
        return jobMapper.getUserDesc(reqMap.get("ID")+"");
    }

    @Override
    public List<Map> getDeJobsList(Map reqMap) {
        //默认岗位暂时固定
        reqMap.put("param","'xsjl','楼盘&轮播&新闻管理','qdjl','khds','20001'");
        return jobMapper.getDeJobsList(reqMap);
    }

    @Override
    public List<Map> getDeComJobsList(Map reqMap) {
        return jobMapper.getDeComJobsList(reqMap);
    }




    /**
     * 获取当前和下属所有组织岗位
     *
     * @param reqMap
     * @return
     */
    @Override
    public List<Map> getSystemJobAllList(Map reqMap) {
        return jobMapper.getSystemJobAllList(reqMap);
    }

    /**
     * 新增岗位-插入Jobs信息
     *
     * @param reqMap
     * @return
     */
    @Override
    public int saveSystemJobForManagement(Map reqMap) {

//        reqMap.put("ID", UUID.randomUUID().toString());
        int i = jobMapper.saveSystemJobForManagement(reqMap);
//        int i1 = jobMapper.saveSystemJobForManagement2(reqMap);
        return i;
    }

    /**
     * 组织岗位功能列表查询(前端功能授权)
     *
     * @param reqMap
     * @return
     */
    @Override
    public Map getSystemJobAuthByUserId(Map reqMap) {

        //登录人有权限的菜单
        List<Map> userMenus = jobMapper.userMenusByUserId(reqMap);

        //登录人有权限的功能
        List<Map> userFunctions = jobMapper.userFunctionsByUserId(reqMap);

        //该岗位已有的菜单和功能
        ArrayList<Map> jobFunction1 = (ArrayList<Map>) jobMapper.jobFunctionsByUserId(reqMap);
        CopyOnWriteArrayList<Map> jobFunctions = new CopyOnWriteArrayList<>(jobFunction1);

        List<Map> oneList = new CopyOnWriteArrayList<>();
        List<Map> twoList = new CopyOnWriteArrayList<>();
        List<Map> threeList = new CopyOnWriteArrayList<>();
        List<Map> fourList = new CopyOnWriteArrayList<>();

        for (Map userMenu : userMenus) {
            String id = userMenu.get("ID") + "";
            String levels = userMenu.get("Levels").toString();
            switch (levels) {
                case "1":
                    oneList.add(userMenu);
                    break;
                case "2":
                    twoList.add(userMenu);
                    break;
                case "3":
                    threeList.add(userMenu);
                    break;
                case "4":
                    fourList.add(userMenu);
                    break;
            }
            for (Map jobFunction : jobFunctions) {
                String jobFunId = jobFunction.get("ID") + "";
                if (id.equals(jobFunId)) {
                    userMenu.put("flag", true);
                    jobFunctions.remove(jobFunction);
                }
            }
        }

        List<Map> child = new ArrayList<>(8);
        for (Map threeMap : threeList) {
            String id = threeMap.get("ID").toString();
            for (Map fourMap : fourList) {
                String pid = fourMap.get("PID").toString();
                if (id.equals(pid)) {
                    child.add(fourMap);
                    fourList.remove(fourMap);
                }
            }
            threeMap.put("child", child);
            child.clear();
        }

        for (Map twoMap : twoList) {
            String id = twoMap.get("ID").toString();
            for (Map threeMap : threeList) {
                String pid = threeMap.get("PID").toString();
                if (id.equals(pid)) {
                    child.add(threeMap);
                    threeList.remove(threeMap);
                }
            }
            twoMap.put("child", child);
            child.clear();
        }

        for (Map oneMap : oneList) {
            String id = oneMap.get("ID").toString();
            for (Map twoMap : twoList) {
                String pid = twoMap.get("PID").toString();
                if (id.equals(pid)) {
                    child.add(twoMap);
                    twoList.remove(twoMap);
                }
            }
            oneMap.put("child", child);
            child.clear();
        }


        HashMap<String, Object> map = MapUtil.newHashMap();
        map.put("userMenus", oneList);
//        map.put("userFunctions", userFunctions);
//        map.put("jobFunctions", jobFunctions);
        return map;
    }

    /**
     * 前后端功能授权保存
     *
     * @param paramMap
     * @return
     */
    @Override
    public String saveSystemJobAuthByManagement(Map paramMap) {
        String OldMenus = String.valueOf(paramMap.get("OldMenus"));

        OldMenus = OldMenus.replaceAll("\\|", "','");
        Map<String, String> pa = new HashMap<>();
        pa.put("JobID", String.valueOf(paramMap.get("JobID")));
        pa.put("OldeMenuID", OldMenus);
        pa.put("MenusType", String.valueOf(paramMap.get("MenusType")));

        List<Map> resList = jobMapper.getSystemJobMenusID(paramMap);

        if (resList != null) {
            for (Map map : resList) {
                jobMapper.removeSystemJobAuth(map);
            }
        }

        // //删除原来的菜单
        String newMenus = String.valueOf(paramMap.get("Menus"));
        if (StringUtil.isEmpty(newMenus)) {
            return "保存失败！";
        }

        String[] menusArray = newMenus.split("\\|");

        int i = 0;
        if (menusArray.length > 0) {
            for (i = 0; i < menusArray.length; i++) {
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("JobID", String.valueOf(paramMap.get("JobID")));
                dataMap.put("MenuID", menusArray[i]);
                jobMapper.saveSystemJobAuthManagement(dataMap);
            }
        }
        if (i > 0) {
            return "保存成功";
        }
        return "保存失败";
    }

    /**
     * 修改岗位信息
     *
     * @param reqMap
     * @return
     */
    @Override
    public int modifySystemJobByUserId(Map reqMap) {
        int i = jobMapper.modifySystemJobByUserId(reqMap);
//        int i1 = jobMapper.saveSystemJobForManagement2(reqMap);
        return i;
    }

    /**
     * 删除岗位信息
     *
     * @param reqMap
     * @return
     */
    @Override
    public int removeSystemJobByUserId(Map reqMap) {
        //判断岗位下是否存在人员
        List<Map> userList = jobMapper.getJobsInsUserList(reqMap);
        if(CollectionUtils.isNotEmpty(userList)){
            return -100231023;
        }
        return jobMapper.removeSystemJobByUserId(reqMap);
    }

    /**
     * 查询引入用户
     *
     * @param reqMap
     * @return
     */
    @Override
    public Map pullinUser(Map reqMap) {
        Map resultMap = new HashMap<>();
        int pageIndex = Integer.parseInt(reqMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        reqMap.put("pageIndex", i);
        List<Map> list = jobMapper.getIntroducingUsers(reqMap);
        Integer count = jobMapper.getIntroducingUsersCount(reqMap);
        resultMap.put("list", list);
        resultMap.put("total", count);
        return resultMap;
    }

    /**
     * 保存用户（默认岗）
     *
     * @param map
     * @return
     */
    @Override
    public int saveDeSystemUser(Map map) {

        //岗位ID
        String jobId = map.get("jobId").toString();
        String authCompanyId = "ede1b679-3546-11e7-a3f8-5254007b6f02";
        String orgId = map.get("orgId").toString();
        List<Map> data = (List) map.get("data");
        String productId = "ee3b2466-3546-11e7-a3f8-5254007b6f02";
        String userId = map.get("userId").toString();

        if (data==null || data.size()==0){
            return -1;
        }
        List<String> userNames = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            userNames.add(data.get(i).get("alias")+"");
        }
        //判断账号是否在此岗位已存在
        Map searchMap = new HashMap();
        searchMap.put("userNames",userNames);
        searchMap.put("jobId",jobId);
        List<String> users = jobMapper.jobUserIsExit(searchMap);
        if (users!=null && users.size()>0){
            for (int i = 0; i < data.size(); i++) {
                if (users.contains(data.get(i).get("alias")+"")){
                    data.remove(i);
                    i--;
                }
            }
        }
        if (data.size()>0){
            for (Map map1:data) {
                String ID = UUID.randomUUID().toString();
                map1.put("authCompanyId", authCompanyId);
                map1.put("orgId", orgId);
                map1.put("productId", productId);
                map1.put("creator", userId);
                map1.put("jobId", jobId);
                map1.put("ID", ID);
                Map userISHaving = jobMapper.getSystemUserNameExists(map1);
                //用户存在
                if (userISHaving!=null) {
                    map1.put("ID", userISHaving.get("ID"));
                    //判断是否存在当前岗位
                    Map currenJob=jobMapper.isCurrentJob(userISHaving.get("ID").toString());
                    if(currenJob==null){
                        map1.put("CurrentJob",1);
                    }else{
                        map1.put("CurrentJob",0);
                    }
                    jobMapper.saveJobSuserrel(map1);
                }else{
                    //保存账号表
                    map1.put("CurrentJob",1);
                    jobMapper.saveIntroducingUsers(map1);
                    jobMapper.saveJobSuserrel(map1);
                }
                String mobile = "";
                if (map1.get("mobile")!=null && !"".equals(map1.get("mobile")+"")){
                    mobile = map1.get("mobile")+"";
                }
                //判断是否维护了手机号 维护了推送企微
//                if (!StringUtils.isEmpty(mobile)){
//
//                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//                    String companycode = request.getHeader("companycode");
//                    if (redisUtil.get("QW_DATATOKEN_"+companycode)!=null){
//                        QwUserVo qwUserVo = new QwUserVo();
//                        qwUserVo.setAccountId(ID);
//                        qwUserVo.setMainDepartment(orgId);
//                        qwUserVo.setEnable("1");
//                        qwUserVo.setEmail(map1.get("mail")==null?"":map1.get("mail").toString());
//                        qwUserVo.setName(map1.get("usercn").toString());
//                        qwUserVo.setMobile(mobile);
//                        qwUserVo.setGender(map1.get("genderId")==null?"1":map1.get("genderId").toString());
//                        //企微引入用户
//                        companyQwSynService.pushUser(qwUserVo);
//                    }
//                }
                //初始化消息配置
//                String user_Id = MapUtils.getString(map1,"ID","");
//                String project_Id = MapUtils.getString(map,"projectId","");
//                String job_Code = MapUtils.getString(map,"jobCode","");
//                if(StringUtils.isNoneBlank(user_Id) && StringUtils.isNoneBlank(project_Id) && StringUtils.isNoneBlank(job_Code)) {
//                    //查询消息模块
//                    List<String> tempIdList = jobMapper.getCommonTemplateListByJobCode(job_Code);
//                    if (tempIdList != null && tempIdList.size() > 0) {
//                        for (String str : tempIdList) {
//                            UserConfigForm userConfigForm = new UserConfigForm();
//                            userConfigForm.setJobCode(job_Code);
//                            userConfigForm.setProjectId(project_Id);
//                            userConfigForm.setUserId(user_Id);
//                            userConfigForm.setTemplateId(str);
//                            this.saveUserTemplateConfig(userConfigForm);
//                        }
//                    }
//                }
            }
        }
        return 0;
    }

    /**
     * 保存用户
     *
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map saveSystemUser(Map map) {
        Map resultMap = new HashMap();
        //岗位ID
        String jobId = map.get("jobId").toString();
        String authCompanyId = map.get("authCompanyId").toString();
        String orgId = map.get("orgId").toString();
        List<Map> data = (List) map.get("data");
        String productId1 = map.get("productId").toString();
        String userId = map.get("userId").toString();
        String projectId = "";
        if (map.get("projectId")!=null){
            projectId = map.get("projectId").toString();
        }
        String jobCode = "";
        if (map.get("jobCode")!=null){
            jobCode = map.get("jobCode").toString();
        }
        String ids = "";
        List<Map> delUsers = null;
        //判断是否存在原来的岗位，存在即删除岗位
        if (map.get("ids")!=null && !"".equals(map.get("ids"))){
            ids = map.get("ids").toString();
            delUsers = jobMapper.selectJobUserRel(ids);
            //删除外呼系统坐席
            List<Map> jobIdCall = jobMapper.getJobIdCall(ids);
            String sToken = redisService.getVal("outbound." + map.get("username")) + "";
            for (Map map1 : jobIdCall) {
                Map<String, Object> ma = new HashMap<>();
                ma.put("uid",map.get("userId"));
                ma.put("did",map1.get("CommonJobID"));
                ma.put("sid",map1.get("JobOrgID"));
                ma.put("Status",0);
                HttpClientUtil.postHttpOutbound(deleteUserOp,sToken,ma);
            }



            jobMapper.delJobUserRelById(ids);

        }

        //判断插入的用户是否存在
        CopyOnWriteArrayList<Map> maps = new CopyOnWriteArrayList<>(data);
        for (Map map1 : maps) {
            Map userISHaving = jobMapper.getSystemUserNameExists(map1);
            if(userISHaving == null){//适配万洋 若未根据登录账号获取到用户 可能是因为登录账号为空 增加判断登录账号为空手机号存在
                userISHaving = jobMapper.getSystemUserNameAndMobileExists(map1);
            }

            //用户存在
            if (userISHaving!=null) {
                map1.put("accountId",userISHaving.get("ID"));
                //判断用户是否在本项目下有客户
                Map selectMap = new HashMap();
                selectMap.put("userId", userISHaving.get("ID"));
                selectMap.put("projectId", projectId);
                int cstCut = 0;
                if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)) {
                    cstCut = jobMapper.getOppCstBySalesId(selectMap);
                }
//                else if ("nqgw".equals(jobCode)) {
//                    cstCut = jobMapper.getCstByReId(selectMap);
//                }
                if (cstCut > 0) {
                    String orgName = jobMapper.getOrgName(orgId);
                    Map userMap = new HashMap();
                    userMap.put("userId", userISHaving.get("ID"));
                    userMap.put("orgId", orgId);
                    userMap.put("orgName", orgName);
                    userMap.put("projectId", projectId);
                    //判断是渠道还是内场岗位调整
                    if ("zygw".equals(jobCode) || "qyzygw".equals(jobCode)) {
                        jobMapper.updateSaleTeamID(userMap);
                    }
//                    else if ("nqgw".equals(jobCode)) {
//                        jobMapper.updateReportTeamID(userMap);
//                    }
                }
            }

        }
        List<Map> maps1 = new ArrayList<>(maps);
        List<String> messageList = new ArrayList<>();
        for (Map datum : maps1) {
            String ID = UUID.randomUUID().toString();
            datum.put("authCompanyId", authCompanyId);
            datum.put("orgId", orgId);
            datum.put("productId", productId1);
            datum.put("creator", userId);
            datum.put("jobId", jobId);
            datum.put("ID", ID);
            if(datum.get("accountId")!=null){
                ID = datum.get("accountId")+"";
                Map jobMap=jobMapper.isRepeat(datum.get("accountId").toString(),jobId);
                jobMapper.updateUserIdm(datum.get("accountId")+"");
                Map currenJob=jobMapper.isCurrentJob(datum.get("accountId").toString());
                //保存平台与岗位的关系
                datum.put("ID",datum.get("accountId"));
                if(currenJob==null){
                    List<String> jobList = Stream.of("nqgw", "zygw", "xsjl","xszz","tdjl","qdjl","qdzz","dkhjl","qyptgl","qypt","GZZS","SZZS","BJZS","SHZS","BJZSJL","GZZSJL","SZZSJL","SHZSJL","qygl").collect(Collectors.toList());
                    if(jobList.contains(jobCode)) {
                        datum.put("CurrentJob",1);
                    }else{
                        datum.put("CurrentJob",0);
                    }
                }else{
                    datum.put("CurrentJob",0);
                }
                if(jobMap==null){
                    Map jobId1 = jobMapper.getOrgJobId(datum.get("jobId")+"");
                    Map userId1 = jobMapper.getUserId(datum.get("ID") + "");


                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("nickname",userId1.get("EmployeeName"));
                        map1.put("username",userId1.get("UserName"));
                        map1.put("password",userId1.get("password"));
                        map1.put("uid",userId1.get("ID"));
                        map1.put("did",jobId1.get("CommonJobID"));
                        map1.put("sid",jobId1.get("JobOrgID"));
                        map1.put("phone",userId1.get("Mobile"));
                        Map id = jobMapper.getZgQx(SecurityUtils.getUserId());
                        String  token1 = "";
                        if(id != null && null!=id.get("sid") && !"".equals(id.get("sid"))){
                            Map<String, Object> loginMap = new HashMap<>();
                            loginMap.put("secret",secret);
                            loginMap.put("channel_id",channelId);
                            loginMap.put("sid",id.get("sid"));
                            loginMap.put("uid",id.get("uid"));
                            loginMap.put("did",id.get("did"));
                            String s = HttpClientUtil.postHttpOutbound(urlOp, null, loginMap);
                            JSONObject jsonObject = JSONObject.parseObject(s);
                            JSONObject data1 = jsonObject.getJSONObject("data");
                            token1 = data1.get("token").toString();
                        }else{
                            token1 = redisService.getVal("outbound."+map.get("username"))+"";
                        }

                        //判断岗位来赋值不同的外呼权限
                        String jobCodeCom = jobId1.get("JobCode") + "";
                        // 获取角色类型并放入map
                        RoleType roleType = JobPermissionUtil.getRoleType(jobCodeCom);
                        map1.put("role_type", roleType.getCode());


                        String s = HttpClientUtil.postHttpOutbound(addUserOp,token1,map1);

                    jobMapper.saveJobSuserrel(datum);
                }else{
                    //表示岗位已存在
                    resultMap.put("code","1001");
                    return resultMap;
                }
            }else{
                //保存账号表
                List<String> jobList = Stream.of("nqgw", "zygw", "xsjl","xszz","tdjl","qdjl","qdzz","dkhjl","qyptgl","qypt","GZZS","SZZS","BJZS","SHZS","BJZSJL","GZZSJL","SZZSJL","SHZSJL","qygl").collect(Collectors.toList());
                if(jobList.contains(jobCode)) {
                    datum.put("CurrentJob", 1);
                }else{
                    datum.put("CurrentJob",0);
                }
                Map jobId1 = jobMapper.getOrgJobId(datum.get("jobId")+"");
                Map userId1 = jobMapper.getUserId(datum.get("ID")+"");
                String jobCodeCom = jobId1.get("JobCode") + "";
//                Set<String> validStrings = new HashSet<>();
//                validStrings.add("qfsj");
//                validStrings.add("qyqfsj");
//                validStrings.add("qyfz");
//                validStrings.add("qyz");
//                validStrings.add("qyzygw");
//                validStrings.add("qyzszj");
//                validStrings.add("qyxsjl");
//                validStrings.add("qyyxjl");
//                validStrings.add("qycsss");
//                validStrings.add("10001");
//                validStrings.add("xmz");
//                validStrings.add("yxjl");
//                validStrings.add("zszj");
//                validStrings.add("xsjl");
//                validStrings.add("zygw");
//
//                if (validStrings.contains(jobCodeCom)){
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("nickname",userId1.get("EmployeeName"));
                    map1.put("username",userId1.get("UserName"));
                    map1.put("password",userId1.get("password"));
                    map1.put("uid",userId1.get("ID"));
                    map1.put("did",jobId1.get("CommonJobID"));
                    map1.put("sid",jobId1.get("JobOrgID"));
                    map1.put("phone",userId1.get("Mobile"));
                    Map id = jobMapper.getZgQx(SecurityUtils.getUserId());
                    String  token1 = "";
                    if(id != null  && null!=id.get("sid") && !"".equals(id.get("sid"))){
                        Map<String, Object> loginMap = new HashMap<>();
                        loginMap.put("secret",secret);
                        loginMap.put("channel_id",channelId);
                        loginMap.put("sid",id.get("sid"));
                        loginMap.put("uid",id.get("uid"));
                        loginMap.put("did",id.get("did"));
                        String s = HttpClientUtil.postHttpOutbound(urlOp, null, map);
                        JSONObject jsonObject = JSONObject.parseObject(s);
                        JSONObject data1 = jsonObject.getJSONObject("data");
                        token1 = data1.get("token").toString();
                    }else{
                        token1 = redisService.getVal("outbound."+map.get("username"))+"";
                    }
                // 获取角色类型并放入map
                RoleType roleType = JobPermissionUtil.getRoleType(jobCodeCom);
                map1.put("role_type", roleType.getCode());
                    HttpClientUtil.postHttpOutbound(addUserOp,token1,map1);
//                }
                jobMapper.saveIntroducingUsers(datum);
                jobMapper.saveJobSuserrel(datum);
            }
            String mobile = "";
            if (datum.get("mobile")!=null && !"".equals(datum.get("mobile")+"")){
                mobile = datum.get("mobile")+"";
            }
            //判断是否维护了手机号 维护了推送企微
//            if (!StringUtils.isEmpty(mobile)){
//
//                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//                String companycode = request.getHeader("companycode");
//                if (redisUtil.get("QW_DATATOKEN_"+companycode)!=null){
//                    if (delUsers!=null && delUsers.size()>0){
//                        for (Map mm:delUsers) {
//                            if (ID.equals(mm.get("accountId")+"")){
//                                String orgIdOld = mm.get("orgId")+"";
//                                //删除企微表中对应的部门
//                                jobMapper.delUserOrg(orgIdOld,ID);
//                            }
//                        }
//                    }
//                    QwUserVo qwUserVo = new QwUserVo();
//                    qwUserVo.setAccountId(ID);
//                    qwUserVo.setMainDepartment(orgId);
//                    qwUserVo.setEnable("1");
//                    qwUserVo.setEmail(datum.get("mail")==null?"":datum.get("mail").toString());
//                    qwUserVo.setName(datum.get("usercn").toString());
//                    qwUserVo.setMobile(mobile);
//                    qwUserVo.setGender(datum.get("genderId")==null?"1":datum.get("genderId").toString());
//                    //企微引入用户
//                    companyQwSynService.pushUser(qwUserVo);
//                }
//            }

            // 引入置业岗位 同步维护名片数据
//            if ("zygw".equals(jobCode)) {
//                userCardService.updateCardData(datum);
//
//                // 同步置业顾问
//            }
//            if("dkhjl".equals(jobCode)){
//                String message = this.saveBrokerAccountRecords(String.valueOf(datum.get("ID")),projectId);
//                if(StringUtils.isNotBlank(message)){
//                    messageList.add(message);
//                }
//            }
            //初始化消息配置
//            String user_Id = MapUtils.getString(datum,"ID","");
//            String project_Id = MapUtils.getString(map,"projectId","");
//            String job_Code = MapUtils.getString(map,"jobCode","");
//            if(StringUtils.isNoneBlank(user_Id) && StringUtils.isNoneBlank(project_Id) && StringUtils.isNoneBlank(job_Code)) {
//                //查询消息模块
//                List<String> tempIdList = jobMapper.getCommonTemplateListByJobCode(job_Code);
//                if (tempIdList != null && tempIdList.size() > 0) {
//                    for (String str : tempIdList) {
//                        UserConfigForm userConfigForm = new UserConfigForm();
//                        userConfigForm.setJobCode(job_Code);
//                        userConfigForm.setProjectId(project_Id);
//                        userConfigForm.setUserId(user_Id);
//                        userConfigForm.setTemplateId(str);
//                        this.saveUserTemplateConfig(userConfigForm);
//                    }
//                }
//            }
        }
        resultMap.put("code","200");
        resultMap.put("message",StringUtils.join(messageList,","));
        return resultMap;
    }

    /**
     * 查询账号是否存在
     * @param reqMap
     * @return
     */
    @Override
    public int selectSystemUserCode(Map reqMap) {
        Map userISHaving = jobMapper.getSystemUserNameExists(reqMap);
        if (userISHaving!=null && userISHaving.get("UserName")!=null){
            return 1;
        }else{
            return 0;
        }
    }

    /**
     * 删除用户信息
     *
     * @param reqMap
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeSystemJobUserRel(Map reqMap) {
        //删除外呼系统坐席
        Map jobId1 = jobMapper.getOrgJobId(reqMap.get("JobID")+"");
        if("1".equals(reqMap.get("type"))){
            try{
                //获取所有岗位
                List<Map> maps = jobMapper.getJobSuserrel(reqMap);
                //判断是否存在多个岗位
                if (maps.size()>1){
                    String CurrentJob = "0";
                    //判断删除的岗位是否为当前岗
                    for (Map map:maps) {
                        if (map.get("JobID").equals(reqMap.get("JobID"))){
                            if (map.get("CurrentJob")!=null && !"".equals(map.get("CurrentJob")+"")){
                                CurrentJob = map.get("CurrentJob")+"";
                            }
                        }
                    }
                    //如果删除的岗位为当前岗
                    if ("1".equals(CurrentJob)){
                        for (Map map:maps) {
                            //将其它岗位更新为当前岗
                            if (!map.get("JobID").equals(reqMap.get("JobID"))){
                                jobMapper.updateJobUserRelCurrentJob(map.get("ID")+"");
                                break;
                            }
                        }
                    }
                }

//                String jobCodeCom = jobId1.get("JobCode") + "";
//                Set<String> validStrings = new HashSet<>();
//                validStrings.add("qfsj");
//                validStrings.add("qyqfsj");
//                validStrings.add("qyfz");
//                validStrings.add("qyz");
//                validStrings.add("qyzygw");
//                validStrings.add("qyzszj");
//                validStrings.add("qyxsjl");
//                validStrings.add("qyyxjl");
//                validStrings.add("qycsss");
//                validStrings.add("10001");
//                validStrings.add("xmz");
//                validStrings.add("yxjl");
//                validStrings.add("zszj");
//                validStrings.add("xsjl");
//                validStrings.add("zygw");
//                if (validStrings.contains(jobCodeCom)){
                //删除坐席
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("uid",reqMap.get("UserID"));
                    map1.put("did",jobId1.get("CommonJobID"));
                    map1.put("sid",jobId1.get("JobOrgID"));
                    HttpClientUtil.postHttpOutbound(deleteUserOp,redisService.getVal("outbound."+reqMap.get("username"))+"",map1);
//                }
                //删除岗位
                int i = jobMapper.removeSystemJobUserRel(reqMap);
                //根据岗位ID查询岗位信息
                Map jobMap = jobMapper.selectOrgByJobId(reqMap.get("JobID")+"");
                if (jobMap!=null && "zygw".equals(jobMap.get("JobCode")+"")){
                    String projectId = jobMap.get("ProjectID")+"";
                    Map resultMap = new HashMap();
                    resultMap.put("projectId",projectId);
                    resultMap.put("orgId",null);
                    resultMap.put("orgName",null);
                    resultMap.put("userId",reqMap.get("UserID"));
                    jobMapper.updateSaleTeamID(resultMap);
//                Map accountMap=new HashMap();
//                accountMap.put("ID",resultMap.get("userId"));
//                Map accountThirdPartyMap = jobMapper.getBaccountThirdParty(accountMap);
//                if (accountThirdPartyMap != null && accountThirdPartyMap.get("xk_sales_id")
//                        != null && accountThirdPartyMap.get("xk_sales_id").equals(reqMap.get("UserID"))) {
//                    // 删除时同步
//                    Map jsonMap = new HashMap();
//                    // 1：在职：2：离职
//                    jsonMap.put("leave", 1);
//                    // 同步置业顾问
//                    //项目ID
//                    jsonMap.put("projectId", projectId);
//                    jsonMap.put("salesId", reqMap.get("UserID"));
//                    jsonMap.put("wlkAccount", accountThirdPartyMap.get("third_party_account"));
//                    jsonMap.put("salesName", reqMap.get("EmployeeName"));
//                    jsonMap.put("salesMobile", reqMap.get("Mobile"));
//                    //1：正常 -1：冻结
//                    //删除传冻结
//                    jsonMap.put("status", -1);
//                    HttpRequestUtil.httpPost(consultantSyncURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
//                }
                    // 移除原岗 同步删除名片楼盘排序数据
                    userCardService.delCardBuildBook(resultMap);

                }else if(jobMap!=null && "nqgw".equals(jobMap.get("JobCode")+"")){
                    String projectId = jobMap.get("ProjectID")+"";
                    Map resultMap = new HashMap();
                    resultMap.put("projectId",projectId);
                    resultMap.put("orgId",null);
                    resultMap.put("orgName",null);
                    resultMap.put("userId",reqMap.get("UserID"));
                    jobMapper.updateReportTeamID(resultMap);
                }else if (jobMap!=null && "dkhjl".equals(jobMap.get("JobCode")+"")){
                    //大客户经理岗位删除
                    Map resultMap = new HashMap();
                    String projectId = jobMap.get("ProjectID")+"";
                    resultMap.put("projectId",projectId);
                    resultMap.put("userId",reqMap.get("UserID"));
                    resultMap.put("editor",SecurityUtils.getUserId());
                    //查询经理下的二级经纪人 发送消息
                    List<String> cstList = jobMapper.getSecCstList(resultMap);
                    //原大客户经理及二级经纪人数据
                    List<Map> accontMap = jobMapper.getOldAccountManagerL(resultMap);
                    if(cstList!=null && cstList.size() > 0){
                        String name = accontMap.get(0).get("accountName")+"";
                        List<Message> messageList = new ArrayList<>();
                        //查询所有楼盘
                        List<String> buildNameList = jobMapper.getAllBuildBook(resultMap);
                        String allBuildName = "";
                        if(buildNameList.size() > 0){
                            allBuildName = String.join("、", buildNameList);
                        }
                        for (String str : cstList){
                            Message messageTwo = new Message();
                            messageTwo.setSubject("变更大客户经理提醒");
                            messageTwo.setContent("您在" + allBuildName + "的大客户经理"+name+"已离职，我们会尽快安排新的大客户经理,点击查看详情。");
                            messageTwo.setSender( SecurityUtils.getUserId());
                            messageTwo.setMessageType(2203);
                            messageTwo.setIsDel(0);
                            messageTwo.setReceiver(str);
                            messageTwo.setIsRead(0);
                            messageTwo.setIsPush(2);
                            messageTwo.setIsNeedPush(2);
                            messageTwo.setProjectId(projectId);
                            messageTwo.setProjectClueId(null);
                            messageTwo.setId(UUID.randomUUID().toString());
                            messageTwo.setExt3(str);
                            messageList.add(messageTwo);
                        }
                        if(messageList.size() > 0){
                            jobMapper.insertMessageList(messageList);
                        }
                    }
                    //保存分配记录
                    String  batchId = UUID.randomUUID().toString();
                    BrokerAccountRecordsBatch brokerAccountRecordsBatch = new BrokerAccountRecordsBatch();
                    brokerAccountRecordsBatch.setId(batchId);
                    brokerAccountRecordsBatch.setAccountId(null);
                    brokerAccountRecordsBatch.setCountNumber(accontMap.size() +"");
                    brokerAccountRecordsBatch.setCreateUser(SecurityUtils.getUserId());
                    brokerAccountRecordsBatch.setEntrance("PC端删除大客户经理岗位");
                    brokerAccountRecordsBatch.setReason("PC端删除大客户经理岗位");
                    brokerAccountRecordsBatch.setProjectId(null);
                    jobMapper.saveBrokerAccountRecordsBatch(brokerAccountRecordsBatch);
                    //保存更新记录
                    if (accontMap!=null && accontMap.size()>0){
                        List<BrokerAccountRecords> brokerAccountRecordsList = new ArrayList<>();
                        for (Map map : accontMap) {
                            BrokerAccountRecords brokerAccountRecords = new BrokerAccountRecords();
                            brokerAccountRecords.setAccountId(null);
                            brokerAccountRecords.setAccountIdOld(MapUtils.getString(map,"accountId",null));
                            brokerAccountRecords.setAccountMobile(null);
                            brokerAccountRecords.setAccountMobileOld(MapUtils.getString(map,"accountMobile",null));
                            brokerAccountRecords.setAccountName(null);
                            brokerAccountRecords.setCreator(SecurityUtils.getUserId());
                            brokerAccountRecords.setAccountNameOld(MapUtils.getString(map,"accountName",null));
                            brokerAccountRecords.setBrokerId(MapUtils.getString(map,"brokerId",null));
                            brokerAccountRecords.setBrokerMobile(MapUtils.getString(map,"brokerMobile",null));
                            brokerAccountRecords.setBrokerName(MapUtils.getString(map,"brokerName",null));
                            brokerAccountRecords.setBrokerOpenId(MapUtils.getString(map,"openId",null));
                            brokerAccountRecords.setProjectId(MapUtils.getString(map,"projectid",null));
                            brokerAccountRecords.setProjectIdOld(MapUtils.getString(map,"projectid",null));
                            brokerAccountRecords.setProjectName(MapUtils.getString(map,"projectname",null));
                            brokerAccountRecords.setProjectNameOld(MapUtils.getString(map,"projectname",null));
                            brokerAccountRecords.setRemarks("PC端删除大客户经理岗位");
                            brokerAccountRecords.setEntrance("PC端引入大客户经理角色");
                            brokerAccountRecords.setBatchId(batchId);
                            brokerAccountRecordsList.add(brokerAccountRecords);
                        }
                        jobMapper.saveBrokerAccountRecords(brokerAccountRecordsList);
                    }
                    //更新业绩归属人表
                    jobMapper.updatePerDkh(resultMap);
                    //更新二级经纪人关联表
                    jobMapper.updateSecDkh(resultMap);


                }
                return i;
            }catch (Exception e){
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
        }else{
            Map<String, Object> map1 = new HashMap<>();
            map1.put("uid",reqMap.get("UserID"));
            map1.put("did",jobId1.get("CommonJobID"));
            map1.put("sid",jobId1.get("JobOrgID"));
            map1.put("Status",reqMap.get("status"));
            HttpClientUtil.postHttpOutbound(updateUserOp,redisService.getVal("outbound."+reqMap.get("username"))+"",map1);
            return userMessageDao.updateUserJob(reqMap);
        }


    }

    /**
     * 修改用户
     *
     * @param reqMap
     * @return
     */
    @Override
    public int modifySystemJobUserRel(Map reqMap) {
        Map map = (Map) reqMap.get("data");
        if("男".equals(map.get("Gender"))){
            map.put("Gender",1);
        }
        if("女".equals(map.get("Gender"))){
            map.put("Gender",2);
        }
        if("启用".equals(map.get("Status"))){
            map.put("Status",1);
        }
        if("禁用".equals(map.get("Status"))){
            map.put("Status",0);
        }
        if("Saas账号".equals(map.get("AccountType"))){
            map.put("AccountType",1);
        }
        if("普通账号".equals(map.get("AccountType"))){
            map.put("AccountType",2);
        }
        //禁用时发送消息
        if ("0".equals(map.get("Status")+"")){
            //大客户经理禁用
            //查询是否有大客户经理权限
            List<String> projectIdList = jobMapper.getProjectIdList(String.valueOf(map.get("ID")));
            if(projectIdList != null && projectIdList.size() > 0){
                Map resultMap = new HashMap();
                resultMap.put("projectIdList",projectIdList);
                resultMap.put("userId",map.get("ID"));
                resultMap.put("editor",SecurityUtils.getUserId());
                //查询经理下的二级经纪人 发送消息
                List<String> cstList = jobMapper.getSecCstList(resultMap);
                //原大客户经理及二级经纪人数据
                List<Map> accontMap = jobMapper.getOldAccountManagerL(resultMap);
                if(cstList!=null && cstList.size() > 0){
                    String name = accontMap.get(0).get("accountName")+"";
                    List<Message> messageList = new ArrayList<>();
                    //查询所有楼盘
                    List<String> buildNameList = jobMapper.getAllBuildBook(resultMap);
                    String allBuildName = "";
                    if(buildNameList.size() > 0){
                        allBuildName = String.join("、", buildNameList);
                    }
                    for (String str : cstList){
                        Message messageTwo = new Message();
                        messageTwo.setSubject("变更大客户经理提醒");
                        messageTwo.setContent("您在" + allBuildName + "的大客户经理"+name+"已禁用，我们会尽快安排新的大客户经理,点击查看详情。");
                        messageTwo.setSender( SecurityUtils.getUserId());
                        messageTwo.setMessageType(2203);
                        messageTwo.setIsDel(0);
                        messageTwo.setReceiver(str);
                        messageTwo.setIsRead(0);
                        messageTwo.setIsPush(2);
                        messageTwo.setIsNeedPush(2);
                        messageTwo.setProjectId(null);
                        messageTwo.setProjectClueId(null);
                        messageTwo.setId(UUID.randomUUID().toString());
                        messageTwo.setExt3(str);
                        messageList.add(messageTwo);
                    }
                    if(messageList.size() > 0){
                        jobMapper.insertMessageList(messageList);
                    }
                }
                //保存分配记录
                String  batchId = UUID.randomUUID().toString();
                BrokerAccountRecordsBatch brokerAccountRecordsBatch = new BrokerAccountRecordsBatch();
                brokerAccountRecordsBatch.setId(batchId);
                brokerAccountRecordsBatch.setAccountId(null);
                brokerAccountRecordsBatch.setCountNumber(accontMap.size() +"");
                brokerAccountRecordsBatch.setCreateUser(SecurityUtils.getUserId());
                brokerAccountRecordsBatch.setEntrance("PC端删除大客户经理岗位");
                brokerAccountRecordsBatch.setReason("PC端删除大客户经理岗位");
                brokerAccountRecordsBatch.setProjectId(null);
                jobMapper.saveBrokerAccountRecordsBatch(brokerAccountRecordsBatch);
                //保存更新记录
                if (accontMap!=null && accontMap.size()>0){
                    List<BrokerAccountRecords> brokerAccountRecordsList = new ArrayList<>();
                    for (Map maps : accontMap) {
                        BrokerAccountRecords brokerAccountRecords = new BrokerAccountRecords();
                        brokerAccountRecords.setAccountId(null);
                        brokerAccountRecords.setAccountIdOld(MapUtils.getString(maps,"accountId",null));
                        brokerAccountRecords.setAccountMobile(null);
                        brokerAccountRecords.setAccountMobileOld(MapUtils.getString(maps,"accountMobile",null));
                        brokerAccountRecords.setAccountName(null);
                        brokerAccountRecords.setCreator(SecurityUtils.getUserId());
                        brokerAccountRecords.setAccountNameOld(MapUtils.getString(maps,"accountName",null));
                        brokerAccountRecords.setBrokerId(MapUtils.getString(maps,"brokerId",null));
                        brokerAccountRecords.setBrokerMobile(MapUtils.getString(maps,"brokerMobile",null));
                        brokerAccountRecords.setBrokerName(MapUtils.getString(maps,"brokerName",null));
                        brokerAccountRecords.setBrokerOpenId(MapUtils.getString(maps,"openId",null));
                        brokerAccountRecords.setProjectId(MapUtils.getString(maps,"projectid",null));
                        brokerAccountRecords.setProjectIdOld(MapUtils.getString(maps,"projectid",null));
                        brokerAccountRecords.setProjectName(MapUtils.getString(maps,"projectname",null));
                        brokerAccountRecords.setProjectNameOld(MapUtils.getString(maps,"projectname",null));
                        brokerAccountRecords.setRemarks("PC端删除大客户经理岗位");
                        brokerAccountRecords.setEntrance("PC端引入大客户经理角色");
                        brokerAccountRecords.setBatchId(batchId);
                        brokerAccountRecordsList.add(brokerAccountRecords);
                    }
                    jobMapper.saveBrokerAccountRecords(brokerAccountRecordsList);
                }
                //更新业绩归属人表
                jobMapper.updatePerDkh(resultMap);
                //更新二级经纪人关联表
                jobMapper.updateSecDkh(resultMap);


            }

//            String brokerId = jobMapper.getBrokerId(String.valueOf(map.get("ID")));
//            if(!StringUtils.isBlank(brokerId)){
//                Map map1 = new HashMap();
//                map1.put("subject","旭客汇账号被禁用");
//                map1.put("content","由于您的旭客汇账号被禁用，您的旭客汇账号绑定关系已失效，请知悉。");
//                map1.put("sender",map.get("Editor"));
//                map1.put("messageType","1000");
//                map1.put("receiver",brokerId);
//                jobMapper.insertMessage(map1);
//            }

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String companycode = request.getHeader("companycode");
            if (redisUtil.get("QW_DATATOKEN_"+companycode)!=null){
                //查询是否存在成员
                String userid = userMessageDao.getQwUserId(String.valueOf(map.get("ID")));
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
        }else{
            String jobID = jobMapper.getComIdByJobId(map.get("JobID") + "");
            Map<String, Object> map1 = new HashMap<>();
            map1.put("nickname",map.get("EmployeeName"));
            map1.put("username",map.get("UserName"));
            map1.put("uid",map.get("ID"));
            map1.put("did",jobID);
            map1.put("sid",map.get("UserOrgID"));
            map1.put("phone",map.get("Mobile"));
            Map id = jobMapper.getZgQx(SecurityUtils.getUserId());
            String  token1 = "";
            if(id != null && null!=id.get("sid") && !"".equals(id.get("sid"))){
                Map<String, Object> loginMap = new HashMap<>();
                loginMap.put("secret",secret);
                loginMap.put("channel_id",channelId);
                loginMap.put("sid",id.get("sid"));
                loginMap.put("uid",id.get("uid"));
                loginMap.put("did",id.get("did"));
                String s = HttpClientUtil.postHttpOutbound(urlOp, null, loginMap);
                JSONObject jsonObject = JSONObject.parseObject(s);
                JSONObject data1 = jsonObject.getJSONObject("data");
                token1 = data1.get("token").toString();
            }else{
                token1 = redisService.getVal("outbound."+map.get("username"))+"";
            }
            //判断岗位来赋值不同的外呼权限
            String jobCodeCom = id.get("JobCode") + "";
            // 获取角色类型并放入map
            RoleType roleType = JobPermissionUtil.getRoleType(jobCodeCom);
            map1.put("role_type", roleType.getCode());
            String s = HttpClientUtil.postHttpOutbound(addUserOp,token1,map1);
        }
        map.put("Editor", SecurityUtils.getUserId());
        // 同步
        //查询是否对接了第三方
//        List<Map> csts = userMessageDao.getSalesThirdPro(map.get("ID")+"","1");
        int i = jobMapper.modifySystemJobUserRel(map);
        // 岗位岗位关联表是否调岗
        jobMapper.updateUserJobRelIsPost(map);
        // 同步更新用户名片信息
        userManagerDao.updateCardByAccountId(map);
//        if (csts!=null && csts.size()>0){
//            String salesMobile = csts.get(0).get("salesMobile")+"";
//            String status = csts.get(0).get("status")+"";
//            String salesName = csts.get(0).get("salesName")+"";
//            //判断如果有修改
//            if (!status.equals(map.get("Status")+"") || (map.get("Mobile")!=null && !"".equals(map.get("Mobile")+"") && !salesMobile.equals(map.get("Mobile")+""))
//                    || (map.get("EmployeeName")!=null && !"".equals(map.get("EmployeeName")+"") && !salesName.equals(map.get("EmployeeName")+""))){
//                //同步置业顾问信息
//                String projectId = "";
//                for (Map cst:csts) {
//                    projectId = projectId + cst.get("projectId")+",";
//                }
//                projectId = projectId.substring(0,projectId.length()-1);
//                Map jsonMap = new HashMap();
//                // 1：在职：2：离职
//                if ("0".equals(map.get("Status"))){
//                    jsonMap.put("leave", 2);
//                }else{
//                    jsonMap.put("leave", 1);
//                }
//                jsonMap.put("wlkAccount", csts.get(0).get("wlkAccount"));
//                jsonMap.put("projectId", projectId);
//                jsonMap.put("salesId", map.get("ID"));
//                jsonMap.put("salesName", map.get("EmployeeName"));
//                jsonMap.put("salesMobile", map.get("Mobile"));
//                //1：正常 -1：冻结
//                jsonMap.put("status", 1);
//                JSONObject re = HttpRequestUtil.httpPost(consultantSyncURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
//                if ("200".equals(re.getString("code")+"") && !salesMobile.equals(map.get("Mobile")+"")){
//                    //更新第三方账号信息
//                    Map up = new HashMap();
//                    up.put("salesId",map.get("ID"));
//                    up.put("thirdParty","1");
//                    up.put("salesMobile",map.get("Mobile"));
//                    userMessageDao.updateSalesThird(up);
//                }
//            }
//        }
        return i;
    }
    @Override
    public int saveDeSystemJobUserRel(Map map) {
        //岗位ID
        String jobId = map.get("jobId").toString();
        String authCompanyId = "ede1b679-3546-11e7-a3f8-5254007b6f02";
        String productId = "ee3b2466-3546-11e7-a3f8-5254007b6f02";
        String orgId = map.get("orgId").toString();
        Map data = (Map) map.get("data");
        String userId = map.get("userId").toString();
        String password = data.get("Password") + "";
        if(data.get("Password")==null||"".equals(password)||"null".equalsIgnoreCase(password)){
            data.put("Password",EncryptUtils.encryptPassword("1"));
        }else{
            data.put("Password",EncryptUtils.encryptPassword(data.get("Password").toString()));
        }
        data.put("alias",data.get("UserName"));
        Map userISHaving = jobMapper.getSystemUserNameExists(data);
        if (userISHaving!=null){
            return 1001;//账号已存在
        }
        String ID = UUID.randomUUID().toString();
        data.put("AuthCompanyID", authCompanyId);
        data.put("EmployeeCode", data.get("EmployeeName"));
        data.put("UserOrgID", orgId);
        data.put("ProductID", productId);
        data.put("Creator", userId);
        data.put("JobID", jobId);
        data.put("ID", ID);
        data.put("CurrentJob",1);
        //保存账号表
        jobMapper.saveSystemUser(data);
        //保存平台与岗位的关系
        jobMapper.saveAccountToJobUserURl(data);
        return 0;
    }
    @Override
    public Map saveSystemJobUserRel(Map map) {
        Map resultMap = new HashMap();
        //岗位ID
        String jobId = map.get("jobId").toString();
        String authCompanyId = map.get("authCompanyId").toString();
        String orgId = map.get("orgId").toString();
        Map data = (Map) map.get("data");
        String productId = map.get("productId").toString();
        String userId = map.get("userId").toString();
        String password = data.get("Password") + "";
        String projectId = "";
        if (map.get("projectId")!=null){
            projectId = map.get("projectId").toString();
        }
        String jobCode = "";
        if (map.get("jobCode")!=null){
            jobCode = map.get("jobCode").toString();
        }
        if(data.get("Password")==null||"".equals(password)||"null".equalsIgnoreCase(password)){
            data.put("Password",EncryptUtils.encryptPassword("1"));
        }else{
            data.put("Password",EncryptUtils.encryptPassword(data.get("Password").toString()));
        }
        data.put("alias",data.get("UserName"));
        Map userISHaving = jobMapper.getSystemUserNameExists(data);
        String ID = UUID.randomUUID().toString();
        data.put("AuthCompanyID", authCompanyId);
        data.put("UserOrgID", orgId);
        data.put("ProductID", productId);
        data.put("Creator", userId);
        data.put("JobID", jobId);
        data.put("ID", ID);
        data.put("accountId",ID);
        data.put("EmployeeCode",data.get("EmployeeName"));
        if (userISHaving!=null) {
            //用户存在
            data.put("ID", userISHaving.get("ID"));
            data.put("accountId", userISHaving.get("ID"));
            //判断用户是否在本项目下有客户
            Map selectMap = new HashMap();
            selectMap.put("userId", userISHaving.get("ID"));
            selectMap.put("projectId", projectId);
            int cstCut = 0;
            if ("zygw".equals(jobCode)) {
                cstCut = jobMapper.getCstBySalesId(selectMap);
            } else if ("nqgw".equals(jobCode)) {
                cstCut = jobMapper.getCstByReId(selectMap);
            }
            if (cstCut > 0) {
                String orgName = jobMapper.getOrgName(orgId);
                Map userMap = new HashMap();
                userMap.put("userId", userISHaving.get("ID"));
                userMap.put("orgId", orgId);
                userMap.put("orgName", orgName);
                userMap.put("projectId", projectId);
                //判断是渠道还是内场岗位调整
                if ("zygw".equals(jobCode)) {
                    jobMapper.updateSaleTeamID(userMap);
                } else if ("nqgw".equals(jobCode)) {
                    jobMapper.updateReportTeamID(userMap);
                }
            }
            //判断是否存在相同岗位
            Map jobMap=jobMapper.isRepeat(userISHaving.get("ID")+"",jobId);
            Map currenJob=jobMapper.isCurrentJob(userISHaving.get("ID")+"");
            if(currenJob==null){
                List<String> jobList = Stream.of("nqgw", "zygw", "xsjl","xszz","tdjl","qdjl","qdzz","dkhjl","qyptgl","qypt").collect(Collectors.toList());
                if(jobList.contains(jobCode)) {
                    data.put("CurrentJob", 1);
                }else{
                    data.put("CurrentJob",0);
                }
            }else{
                data.put("CurrentJob",0);
            }
            if(jobMap==null){
                jobMapper.saveAccountToJobUserURl(data);
            }else{
                //表示岗位已存在
                resultMap.put("code","1001");
                return resultMap;
            }
        }else{
            List<String> jobList = Stream.of("nqgw", "zygw", "xsjl","xszz","tdjl","qdjl","qdzz","dkhjl","qyptgl","qypt").collect(Collectors.toList());
            if(jobList.contains(jobCode)) {
                data.put("CurrentJob", 1);
            }else{
                data.put("CurrentJob",0);
            }
            //保存账号表
            jobMapper.saveSystemUser(data);
            //保存平台与岗位的关系
            jobMapper.saveAccountToJobUserURl(data);
        }

        String mobile = "";
        if (data.get("Mobile")!=null && !"".equals(data.get("Mobile")+"")){
            mobile = data.get("Mobile")+"";
        }
        //判断是否维护了手机号 维护了推送企微
        if (!StringUtils.isEmpty(mobile)){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String companycode = request.getHeader("companycode");
            if (redisUtil.get("QW_DATATOKEN_"+companycode)!=null) {
                QwUserVo qwUserVo = new QwUserVo();
                qwUserVo.setAccountId(ID);
                qwUserVo.setMainDepartment(orgId);
                qwUserVo.setEnable("1");
                qwUserVo.setEmail(data.get("OfficeMail") == null ? "" : data.get("OfficeMail").toString());
                qwUserVo.setName(data.get("EmployeeName").toString());
                qwUserVo.setMobile(mobile);
                qwUserVo.setGender(data.get("Gender") == null ? "1" : data.get("Gender").toString());
                //企微引入用户
                companyQwSynService.pushUser(qwUserVo);
            }
        }
        // 引入置业岗位 同步维护名片数据
        if ("zygw".equals(jobCode)) {
            userCardService.updateCardData(data);
            Map jsonMap = new HashMap();
            //只有启用的时候才调
            if(data.get("Status").toString().equals("1")){
                // 1：在职：2：离职
                jsonMap.put("leave",1);
                // 同步置业顾问
                jsonMap.put("projectId",projectId);
                jsonMap.put("salesId",data.get("accountId"));
                jsonMap.put("salesName",data.get("EmployeeName"));
                jsonMap.put("salesMobile",data.get("Mobile"));
                //1：正常 -1：冻结
                jsonMap.put("status",1);
                HttpRequestUtil.httpPost(consultantSyncURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
            }
        }
        if("dkhjl".equals(jobCode)){
            String message = this.saveBrokerAccountRecords(String.valueOf(data.get("ID")),projectId);
            resultMap.put("message",message);
        }
        //表示岗位已存在
        resultMap.put("code","200");
        return resultMap;

    }

    /**
     * 获取所有菜单
     * @return
     */
    @Override
    public ResultBody getAllMenu(String jobId) {
        System.out.println("-----"+jobId);
        List<Map> list=jobMapper.getAllMenu();
        Map menusMap = CommUtils.buildTree(list);
        List<Map> jobMenu=jobMapper.getJobMenu(jobId);
        menusMap.put("jobRelMenu",jobMenu);
        return ResultBody.success(menusMap);
    }

    /**
     * 获取通用岗位所有菜单
     * @return
     */
    @Override
    public ResultBody getCommonAllMenu(String jobId,String type) {
        List<Map> list=jobMapper.getAllMenu();
        Map menusMap = null;
        if ("".equals(type)){
            menusMap = CommUtils.buildTree1(list);
        }else{
            menusMap = CommUtils.buildTree(list);
        }
        List<Map> jobMenu=jobMapper.getCommonMenu(jobId);
        menusMap.put("jobRelMenu",jobMenu);
        return ResultBody.success(menusMap);
    }

    /**
     * 获取所有菜单
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveJobMenus(Map map, String jobId) {
        try {
            jobMapper.delJobMRelMenu(jobId);
            String str=map.get("jobList").toString().replace("[","").replace("]","");
            String isLast=map.get("isLast").toString().replace("[","").replace("]","");
            String[] strArray = str.split(", ");
            String[] isLastArray = isLast.split(", ");
            for (int i = 0; i < strArray.length; i++) {
                System.out.println("jobId："+jobId);
                System.out.println("menuId："+strArray[i]);
                if(Integer.parseInt(isLastArray[i].toString())==0){
                    System.out.println(strArray[i]+"父节点不添加");
                }else{
                    jobMapper.saveJobMenu(jobId,strArray[i]);
                }
            }
            return ResultBody.error(200,"保存成功！");
        }catch (Exception e){
            throw new BadRequestException(-14_0001,e);
        }
    }
    /**
     * 保存菜单
     * @retu通用rn
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveCommonJobMenus(Map map, String jobId) {
        try {
            jobMapper.delCommonJobMRelMenu(jobId);
            String str=map.get("jobList").toString().replace("[","").replace("]","");
            String isLast=map.get("isLast").toString().replace("[","").replace("]","");
            if (!"".equals(str)){
                String[] strArray = str.split(", ");
                String[] isLastArray = isLast.split(", ");
                for (int i = 0; i < strArray.length; i++) {
                    System.out.println("jobId："+jobId);
                    System.out.println("menuId："+strArray[i]);
                    if(Integer.parseInt(isLastArray[i].toString())==0){
                        System.out.println(strArray[i]+"父节点不添加");
                    }else{
                        jobMapper.saveCommonJobMenu(jobId,strArray[i]);
                    }
                }
            }
            return ResultBody.error(200,"保存成功！");
        }catch (Exception e){
            throw new BadRequestException(-14_0001,e);
        }
    }

    /**
     * 中介公司
     * @retu通用rn
     */
    @Override
    public ResultBody getAllCompanyInfo(String orgId,String cid) {
        String ids = null;
        StringBuffer idss = new StringBuffer();
        if (orgId!=null && !"".equals(orgId)){
            List<String> idList = jobMapper.getAllCompanyInfoByOrgId(orgId);
            if (idList!=null && idList.size()>0){
                for (String id:idList) {
                    if(!id.equals(cid)){
                        idss.append("'"+id+"',");
                    }

                }
                ids = idss.toString().substring(0,idss.toString().length()-1);
            }
        }
        return ResultBody.success(jobMapper.getAllCompanyInfo(ids));
    }

    /**
     * 所属组织
     *
     */
    @Override
    public ResultBody getAllOrgProject() {
        List<Map> list=jobMapper.getAllOrgProject();
        Map menusMap = CommUtils.buildTree(list);
        return ResultBody.success(menusMap);
    }
    /**
     * 所属区域集团
     *
     */
    @Override
    public ResultBody getAllOrgProject2() {
        List<Map> list=jobMapper.getAllOrgProject2();
        Map menusMap = CommUtils.buildTree(list);
        return ResultBody.success(menusMap);
    }

    /**
     * 更新项目
     * @retu通用rn
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateProject(Map map) {
        if(map!=null){
            //启用时默认开启同步
            if ("1".equals(map.get("status")+"")){
                map.put("isSyn",1);
            }else{
                map.put("isSyn",0);
            }
            try{
                jobMapper.updateProjectId(map);
                String fullPath=jobMapper.getFullPath(map.get("orgId").toString());
                jobMapper.updateOrg(map.get("projectId").toString(),fullPath);
                //启用时自动创建组织及岗位
                if ("1".equals(map.get("status")+"")){
                    //查询组织中部门数据
                    List<Map> depts = jobMapper.getProOrg(map.get("projectId").toString());
                    boolean wq = false;//中介
                    boolean nq = false;//渠道
                    boolean ac = false;//案场
                    String nqId = "";//渠道组织ID
                    //如果缺少部门数据添加部门
                    if (depts!=null && depts.size()>0){
                        for (Map map1:depts){
                            if ("1".equals(map1.get("OrgType")+"")){
                                nq = true;
                                nqId = map1.get("id")+"";
                            }else if ("2".equals(map1.get("OrgType")+"")){
                                wq = true;
                            }else if ("3".equals(map1.get("OrgType")+"")){
                                ac = true;
                            }
                        }
                    }
                    List<Map> deptAdd = new ArrayList<>();
                    if (!wq){
                        Map map1 = new HashMap();
                        map1.put("ID",UUID.randomUUID().toString());
                        map1.put("PID",map.get("orgId").toString());
                        map1.put("OrgName","中介");
                        map1.put("OrgShortName","中介");
                        map1.put("OrgCategory",5);
                        map1.put("ListIndex",1);
                        map1.put("Levels",3);
                        map1.put("FullPath",fullPath+"/中介");
                        map1.put("AuthCompanyID","ede1b679-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProductID","ee3b2466-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProjectID",map.get("projectId").toString());
                        map1.put("OrgType",2);
                        deptAdd.add(map1);
                    }
                    if (!nq){
                        Map map1 = new HashMap();
                        nqId = UUID.randomUUID().toString();
                        map1.put("ID",nqId);
                        map1.put("PID",map.get("orgId").toString());
                        map1.put("OrgName","渠道");
                        map1.put("OrgShortName","渠道");
                        map1.put("OrgCategory",5);
                        map1.put("ListIndex",1);
                        map1.put("Levels",3);
                        map1.put("FullPath",fullPath+"/渠道");
                        map1.put("AuthCompanyID","ede1b679-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProductID","ee3b2466-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProjectID",map.get("projectId").toString());
                        map1.put("OrgType",1);
                        deptAdd.add(map1);
                    }
                    if (!ac){
                        Map map1 = new HashMap();
                        map1.put("ID",UUID.randomUUID().toString());
                        map1.put("PID",map.get("orgId").toString());
                        map1.put("OrgName","案场");
                        map1.put("OrgShortName","案场");
                        map1.put("OrgCategory",5);
                        map1.put("ListIndex",1);
                        map1.put("Levels",3);
                        map1.put("FullPath",fullPath+"/案场");
                        map1.put("AuthCompanyID","ede1b679-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProductID","ee3b2466-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProjectID",map.get("projectId").toString());
                        map1.put("OrgType",3);
                        deptAdd.add(map1);
                    }
                    if (deptAdd.size()>0){
                        //保存部门
                        jobMapper.addDepts(deptAdd);
                    }
                    //查询默认通用岗信息
                    List<Map> comJobs = jobMapper.getComJobs("'xsjl','楼盘&轮播&新闻管理','qdjl','khds','20001'");
                    String xsjlComId = "";//销售经理通用岗ID
                    String chjlComId = "";//项目策划经理通用岗ID
                    String qdjlComId = "";//渠道经理通用岗ID
                    String khdsComId = "";//客户大使通用岗ID
                    String gljlComId = "";//项目管理经理通用岗ID
                    for (Map comJob: comJobs){
                        if ("xsjl".equals(comJob.get("JobCode")+"")){
                            xsjlComId = comJob.get("ID")+"";
                        }else if("楼盘&轮播&新闻管理".equals(comJob.get("JobCode")+"")){
                            chjlComId = comJob.get("ID")+"";
                        }else if("qdjl".equals(comJob.get("JobCode")+"")){
                            qdjlComId = comJob.get("ID")+"";
                        }else if("khds".equals(comJob.get("JobCode")+"")){
                            khdsComId = comJob.get("ID")+"";
                        }else if("20001".equals(comJob.get("JobCode")+"")){
                            gljlComId = comJob.get("ID")+"";
                        }
                    }


                    String orgIds = "'"+map.get("orgId")+"','"+nqId+"'";
                    //查询岗位数据
                    List<String> jobCodes = jobMapper.getProJobs(orgIds);

                    //如果默认岗位缺失则直接添加
                    List<Map> jobAdd = new ArrayList<>();
                    boolean xsjl = true;//销售经理
                    boolean qdjl = true;//渠道经理
                    boolean chjl = true;//项目策划经理
                    boolean khds = true;//客户大使
                    boolean gljl = true;//项目管理经理
                    if (jobCodes!=null && jobCodes.size()>0){
                        if (jobCodes.contains("xsjl")){
                            xsjl = false;
                        }
                        if (jobCodes.contains("楼盘&轮播&新闻管理")){
                            chjl = false;
                        }
                        if (jobCodes.contains("qdjl")){
                            qdjl = false;
                        }
                        if (jobCodes.contains("khds")){
                            khds = false;
                        }
                        if (jobCodes.contains("20001")){
                            gljl = false;
                        }
                    }
                    if (xsjl){
                        Map map2 = new HashMap();
                        map2.put("ID",UUID.randomUUID().toString());
                        map2.put("JobCode","xsjl");
                        map2.put("JobName","销售经理");
                        map2.put("JobDesc","销售经理");
                        map2.put("CommonJobID",xsjlComId);
                        map2.put("JobOrgID",map.get("orgId"));
                        jobAdd.add(map2);
                    }
                    //策划经理岗位ID
                    String chjlJobsId = "";
                    if (chjl){
                        Map map2 = new HashMap();
                        chjlJobsId = UUID.randomUUID().toString();
                        map2.put("ID",chjlJobsId);
                        map2.put("JobCode","chjl");
                        map2.put("JobName","项目策划经理");
                        map2.put("JobDesc","项目策划经理");
                        map2.put("CommonJobID",chjlComId);
                        map2.put("JobOrgID",map.get("orgId"));
                        jobAdd.add(map2);
                    }
                    if (qdjl){
                        Map map2 = new HashMap();
                        map2.put("ID",UUID.randomUUID().toString());
                        map2.put("JobCode","qdjl");
                        map2.put("JobName","渠道经理");
                        map2.put("JobDesc","渠道经理");
                        map2.put("CommonJobID",qdjlComId);
                        map2.put("JobOrgID",nqId);
                        jobAdd.add(map2);
                    }
                    if (khds){
                        Map map2 = new HashMap();
                        map2.put("ID",UUID.randomUUID().toString());
                        map2.put("JobCode","khds");
                        map2.put("JobName","客户大使");
                        map2.put("JobDesc","客户大使");
                        map2.put("CommonJobID",khdsComId);
                        map2.put("JobOrgID",map.get("orgId"));
                        jobAdd.add(map2);
                    }
                    if (gljl){
                        Map map2 = new HashMap();
                        map2.put("ID",UUID.randomUUID().toString());
                        map2.put("JobCode","gljl");
                        map2.put("JobName","项目管理经理");
                        map2.put("JobDesc","项目管理经理");
                        map2.put("CommonJobID",gljlComId);
                        map2.put("JobOrgID",map.get("orgId"));
                        jobAdd.add(map2);
                    }
                    //保存岗位
                    if (jobAdd.size()>0){
                        jobMapper.saveSystemJobForManagementList(jobAdd);
                    }
                    //判断项目是否关联城市
                    String cityId =  map.get("cityId")+"";
                    if (cityId!=null && !"".equals(cityId) && chjl){
                        //项目策划岗位添加城市权限
                        List<Map> cityMaps = new ArrayList<>();
                        Map cityMap = new HashMap();
                        cityMap.put("cityID",cityId);
                        cityMap.put("jobId",chjlJobsId);
                        cityMaps.add(cityMap);
                        jobMapper.insertCityJob(cityMaps);
                    }
                }

                BindProject bindProject = BindProject
                        .builder()
                        // b_bind_project 表的id
                        .id((Integer) map.get("id"))
                        .projectId(map.get("projectId").toString())
                        .projectName(map.get("projectName").toString())
                        .generateName(map.get("generateName").toString())
                        .startTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(map.get("startTime").toString()))
                        .endTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(map.get("endTime").toString()))
                        .build();
                // 绑定项目 TODO 绑定项目
                updateBindProject(bindProject);

            }catch (Exception e){
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultBody.error(-1002008,"更新异常！");
            }
//            managerDao.deleteCityProject(String.valueOf(map.get("projectId")));
//            Map paramMap = new HashMap();
//            paramMap.put("projectID",map.get("projectId"));
//            paramMap.put("cityID",map.get("cityId"));
//            managerDao.insertCityProject(paramMap);
        }
        return ResultBody.success("成功！");
    }

    /**
     * 初始化项目
     * @retu通用rn
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateProjectNew(Map map) {
        if(map!=null){
            //启用时默认开启同步
            if ("1".equals(map.get("status")+"")){
                map.put("isSyn",1);
            }else{
                map.put("isSyn",0);
            }
            try{
                jobMapper.addProCity(map);
                Map orgMap =jobMapper.getProOrgData(map.get("projectId").toString());
                if(orgMap==null){
                    return ResultBody.error(-1200003,"请先维护项目组织数据！");
                }
                String fullPath = orgMap.get("FullPath").toString();
                map.put("orgId",orgMap.get("id"));
                jobMapper.updateProjectIdNew(map);
                jobMapper.updateOrg(map.get("projectId").toString(),fullPath);
                //启用时自动创建组织及岗位
                if ("1".equals(map.get("status")+"")){
                    //查询组织中部门数据
                    List<Map> depts = jobMapper.getProOrg(map.get("projectId").toString());
                    boolean wq = false;//中介
                    boolean nq = false;//渠道
                    boolean ac = false;//案场
                    String nqId = "";//渠道组织ID
                    //如果缺少部门数据添加部门
                    if (depts!=null && depts.size()>0){
                        for (Map map1:depts){
                            if ("1".equals(map1.get("OrgType")+"")){
                                nq = true;
                                nqId = map1.get("id")+"";
                            }else if ("2".equals(map1.get("OrgType")+"")){
                                wq = true;
                            }else if ("3".equals(map1.get("OrgType")+"")){
                                ac = true;
                            }
                        }
                    }
                    List<Map> deptAdd = new ArrayList<>();
                    if (!wq){
                        Map map1 = new HashMap();
                        map1.put("ID",UUID.randomUUID().toString());
                        map1.put("PID",map.get("orgId").toString());
                        map1.put("OrgName","中介");
                        map1.put("OrgShortName","中介");
                        map1.put("OrgCategory",5);
                        map1.put("ListIndex",1);
                        map1.put("Levels",3);
                        map1.put("FullPath",fullPath+"/中介");
                        map1.put("AuthCompanyID","ede1b679-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProductID","ee3b2466-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProjectID",map.get("projectId").toString());
                        map1.put("OrgType",2);
                        deptAdd.add(map1);
                    }
                    if (!nq){
                        Map map1 = new HashMap();
                        nqId = UUID.randomUUID().toString();
                        map1.put("ID",nqId);
                        map1.put("PID",map.get("orgId").toString());
                        map1.put("OrgName","渠道");
                        map1.put("OrgShortName","渠道");
                        map1.put("OrgCategory",5);
                        map1.put("ListIndex",1);
                        map1.put("Levels",3);
                        map1.put("FullPath",fullPath+"/渠道");
                        map1.put("AuthCompanyID","ede1b679-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProductID","ee3b2466-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProjectID",map.get("projectId").toString());
                        map1.put("OrgType",1);
                        deptAdd.add(map1);
                    }
                    if (!ac){
                        Map map1 = new HashMap();
                        map1.put("ID",UUID.randomUUID().toString());
                        map1.put("PID",map.get("orgId").toString());
                        map1.put("OrgName","案场");
                        map1.put("OrgShortName","案场");
                        map1.put("OrgCategory",5);
                        map1.put("ListIndex",1);
                        map1.put("Levels",3);
                        map1.put("FullPath",fullPath+"/案场");
                        map1.put("AuthCompanyID","ede1b679-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProductID","ee3b2466-3546-11e7-a3f8-5254007b6f02");
                        map1.put("ProjectID",map.get("projectId").toString());
                        map1.put("OrgType",3);
                        deptAdd.add(map1);
                    }
                    if (deptAdd.size()>0){
                        //保存部门
                        jobMapper.addDepts(deptAdd);
                        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                        String companycode = request.getHeader("companycode");
                        if (redisUtil.get("QW_DATATOKEN_"+companycode)!=null) {
                            for (Map deptMap:deptAdd) {
                                //添加企微组织信息
                                companyQwSynService.addDept(deptMap.get("OrgName").toString(), deptMap.get("PID").toString(), deptMap.get("ID").toString(), deptMap.get("ProjectID").toString());
                            }
                        }
                    }
                    //查询默认通用岗信息
                    List<Map> comJobs = jobMapper.getComJobs("'xsjl','楼盘&轮播&新闻管理','qdjl','khds','20001'");
                    String xsjlComId = "";//销售经理通用岗ID
                    String chjlComId = "";//项目策划经理通用岗ID
                    String qdjlComId = "";//渠道经理通用岗ID
                    String khdsComId = "";//客户大使通用岗ID
                    String gljlComId = "";//项目管理经理通用岗ID
                    for (Map comJob: comJobs){
                        if ("xsjl".equals(comJob.get("JobCode")+"")){
                            xsjlComId = comJob.get("ID")+"";
                        }else if("楼盘&轮播&新闻管理".equals(comJob.get("JobCode")+"")){
                            chjlComId = comJob.get("ID")+"";
                        }else if("qdjl".equals(comJob.get("JobCode")+"")){
                            qdjlComId = comJob.get("ID")+"";
                        }else if("khds".equals(comJob.get("JobCode")+"")){
                            khdsComId = comJob.get("ID")+"";
                        }else if("20001".equals(comJob.get("JobCode")+"")){
                            gljlComId = comJob.get("ID")+"";
                        }
                    }


                    String orgIds = "'"+map.get("orgId")+"','"+nqId+"'";
                    //查询岗位数据
                    List<String> jobCodes = jobMapper.getProJobs(orgIds);

                    //如果默认岗位缺失则直接添加
                    List<Map> jobAdd = new ArrayList<>();
                    boolean xsjl = true;//销售经理
                    boolean qdjl = true;//渠道经理
                    boolean chjl = true;//项目策划经理
                    boolean khds = true;//客户大使
                    boolean gljl = true;//项目管理经理
                    if (jobCodes!=null && jobCodes.size()>0){
                        if (jobCodes.contains("xsjl")){
                            xsjl = false;
                        }
                        if (jobCodes.contains("楼盘&轮播&新闻管理")){
                            chjl = false;
                        }
                        if (jobCodes.contains("qdjl")){
                            qdjl = false;
                        }
                        if (jobCodes.contains("khds")){
                            khds = false;
                        }
                        if (jobCodes.contains("20001")){
                            gljl = false;
                        }
                    }
                    if (xsjl){
                        Map map2 = new HashMap();
                        map2.put("ID",UUID.randomUUID().toString());
                        map2.put("JobCode","xsjl");
                        map2.put("JobName","销售经理");
                        map2.put("JobDesc","销售经理");
                        map2.put("CommonJobID",xsjlComId);
                        map2.put("JobOrgID",map.get("orgId"));
                        jobAdd.add(map2);
                    }
                    //策划经理岗位ID
                    String chjlJobsId = "";
                    if (chjl){
                        Map map2 = new HashMap();
                        chjlJobsId = UUID.randomUUID().toString();
                        map2.put("ID",chjlJobsId);
                        map2.put("JobCode","chjl");
                        map2.put("JobName","项目策划经理");
                        map2.put("JobDesc","项目策划经理");
                        map2.put("CommonJobID",chjlComId);
                        map2.put("JobOrgID",map.get("orgId"));
                        jobAdd.add(map2);
                    }
                    if (qdjl){
                        Map map2 = new HashMap();
                        map2.put("ID",UUID.randomUUID().toString());
                        map2.put("JobCode","qdjl");
                        map2.put("JobName","渠道经理");
                        map2.put("JobDesc","渠道经理");
                        map2.put("CommonJobID",qdjlComId);
                        map2.put("JobOrgID",nqId);
                        jobAdd.add(map2);
                    }
                    if (khds){
                        Map map2 = new HashMap();
                        map2.put("ID",UUID.randomUUID().toString());
                        map2.put("JobCode","khds");
                        map2.put("JobName","客户大使");
                        map2.put("JobDesc","客户大使");
                        map2.put("CommonJobID",khdsComId);
                        map2.put("JobOrgID",map.get("orgId"));
                        jobAdd.add(map2);
                    }
                    if (gljl){
                        Map map2 = new HashMap();
                        map2.put("ID",UUID.randomUUID().toString());
                        map2.put("JobCode","gljl");
                        map2.put("JobName","项目管理经理");
                        map2.put("JobDesc","项目管理经理");
                        map2.put("CommonJobID",gljlComId);
                        map2.put("JobOrgID",map.get("orgId"));
                        jobAdd.add(map2);
                    }
                    //保存岗位
                    if (jobAdd.size()>0){
                        jobMapper.saveSystemJobForManagementList(jobAdd);
                    }
                    //判断项目是否关联城市
                    String cityId =  map.get("cityId")+"";
                    if (map.get("cityId")!=null && cityId!=null && !"".equals(cityId) && chjl){
                        //项目策划岗位添加城市权限
                        List<Map> cityMaps = new ArrayList<>();
                        Map cityMap = new HashMap();
                        cityMap.put("cityID",cityId);
                        cityMap.put("jobId",chjlJobsId);
                        cityMaps.add(cityMap);
                        jobMapper.insertCityJob(cityMaps);
                    }
                }

                BindProject bindProject = BindProject
                        .builder()
                        // b_bind_project 表的id
                        .id((Integer) map.get("bindProId"))
                        .projectId(map.get("projectId").toString())
                        .projectName(orgMap.get("ProjectName").toString())
                        .generateName(map.get("bindProjectName").toString())
                        .startTime(new Date(Long.parseLong(map.get("startTime").toString())))
                        .endTime(new Date(Long.parseLong(map.get("endTime").toString())))
                        .build();
                // 绑定项目 TODO 绑定项目
                updateBindProject(bindProject);

            }catch (Exception e){
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultBody.error(-1002008,"更新异常！");
            }
        }
        return ResultBody.success("成功！");
    }

    @Override
    public String getIsSys() {

        String result = "-1";
        int count = jobMapper.getIsSys(SecurityUtils.getUserId());
        if (count>0){
            result = "1";
        }
        return result;
    }

    @Override
    public ResultBody getCurrentJobs(Map map) {
        ResultBody result = new ResultBody();
        if (map!=null){
            if (map.get("JobCode")==null || "".equals(map.get("JobCode")) || map.get("projectId")==null || null==map.get("userId")
                    || "".equals(map.get("projectId")) || "".equals(map.get("userId"))){
                result.setCode(201);
                result.setMessages("必要参数未传！！");
                result.setData(null);
            }else{
                try{
                    String projectId = map.get("projectId")+"";
                    String userId = map.get("userId")+"";
                    String JobCode = map.get("JobCode")+"";
                    Map paramMap = new HashMap();
                    paramMap.put("projectId",projectId);
                    paramMap.put("userId",userId);
                    paramMap.put("JobCode",JobCode);
                    jobMapper.getCurrentJobs(paramMap);
                    result.setCode(200);
                    result.setMessages("ok");
                    result.setData(jobMapper.getCurrentJobs(paramMap));
                }catch (Exception e){
                    e.printStackTrace();
                    result.setCode(203);
                    result.setMessages("获取岗位数据异常！！");
                    result.setData(null);
                }

            }
        }else{
            result.setCode(201);
            result.setMessages("参数错误！！");
            result.setData(null);
        }
        return result;
    }

    @Override
    public ResultBody getCityJobList(Map map) {
        ResultBody result = new ResultBody();
        if (map!=null){
            try{
                String jobId = String.valueOf(map.get("jobId"));
                List<Map> mapList = jobMapper.getCityJobList(jobId);
                result.setCode(200);
                result.setMessages("ok");
                result.setData(mapList);
            }catch (Exception e){
                e.printStackTrace();
                result.setCode(203);
                result.setMessages("获取岗位城市授权数据异常！！");
                result.setData(null);
            }
        }else{
            result.setCode(201);
            result.setMessages("参数错误！！");
            result.setData(null);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveCityJob(Map map) {
        ResultBody result = new ResultBody();
        if (map!=null){
            try{
                List<String> stringList = (List<String>) map.get("cityIDList");
                if(stringList.size() == 0){
                    result.setCode(201);
                    result.setMessages("授权城市未选择！！");
                    result.setData(null);
                }
                List<Map> list = new ArrayList<>();
                for (int i = 0; i < stringList.size(); i++) {
                    Map maps = new HashMap();
                    maps.put("cityID",stringList.get(i));
                    maps.put("jobId",String.valueOf(map.get("jobId")));
                    list.add(maps);
                }
                jobMapper.deleteCityJob(String.valueOf(map.get("jobId")));
                jobMapper.insertCityJob(list);
                result.setCode(200);
                result.setMessages("ok");
                result.setData(null);
            }catch (Exception e){
                e.printStackTrace();
                result.setCode(203);
                result.setMessages("保存岗位城市授权数据异常！！");
                result.setData(null);
            }
        }else{
            result.setCode(201);
            result.setMessages("参数错误！！");
            result.setData(null);
        }
        return result;
    }

    @Override
    public Map findUserDesc(Map reqMap) {
        Map resultMap = new HashMap();
        String code = "0";//代表没有查询到
        String message = "OK";//如果存在岗位时的提示语
        String ids = "";//存在岗位的ID
        List<Map> data = (List) reqMap.get("data");
        String userNames = "";
        StringBuffer userSb = new StringBuffer();
        for (Map map:data){
            userSb.append("'"+map.get("alias")+"',");
        }
        userNames = userSb.toString().substring(0,userSb.toString().length()-1);
        reqMap.put("userNames",userNames);
        List<Map> userJobs = jobMapper.findUserDesc(reqMap);
        if (userJobs!=null && userJobs.size()>0){
            StringBuffer sb = new StringBuffer();
            StringBuffer idSb = new StringBuffer();
            sb.append("以下用户在本项目下存在岗位：");
            for (Map map:userJobs) {
                sb.append(map.get("EmployeeName")+"-"+map.get("OrgName")+"-"+map.get("JobName")+",");
                idSb.append("'"+map.get("id")+"',");
            }
            code = "1";
            message = sb.toString()+"是否进行岗位调整？";
            ids = idSb.toString().substring(0,idSb.toString().length()-1);
        }
        resultMap.put("code",code);
        resultMap.put("message",message);
        resultMap.put("ids",ids);
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveUserTemplateConfig(UserConfigForm userConfigForm) {
        if(StringUtils.isBlank(userConfigForm.getUserId()) ||
                StringUtils.isBlank(userConfigForm.getJobCode()) ||
                StringUtils.isBlank(userConfigForm.getProjectId()) ||
                StringUtils.isBlank(userConfigForm.getTemplateId())){
            return;
        }
        //查询模板信息
        CommonTemplate commonTemplate = jobMapper.getTemplateInfo(userConfigForm.getTemplateId());
        //定义id
        String templateId = UUID.randomUUID().toString();
        String templateTwoId = UUID.randomUUID().toString();
        String templateThreeId = UUID.randomUUID().toString();
        if(commonTemplate != null && commonTemplate.getLevel() != 1){
            //查询是否配置
            String id = jobMapper.getIsConfigTemplate(userConfigForm.getUserId(),
                    userConfigForm.getJobCode(),commonTemplate.getId(),userConfigForm.getProjectId());
            if(StringUtils.isBlank(id)){
                //实际删除
                jobMapper.delUserTemplateConfigTwo(userConfigForm.getUserId(), userConfigForm.getJobCode(),commonTemplate.getId(), userConfigForm.getProjectId());
                //查询父级信息
                CommonTemplate commonTemplateTwo =  jobMapper.getTemplateInfo(commonTemplate.getPId());
                if(commonTemplateTwo != null && commonTemplateTwo.getLevel() != 1){
                    //查询是否配置
                    String idTwo = jobMapper.getIsConfigTemplate(userConfigForm.getUserId(),
                            userConfigForm.getJobCode(),commonTemplateTwo.getId(),userConfigForm.getProjectId());
                    if(StringUtils.isBlank(idTwo)) {
                        //查询父级信息
                        CommonTemplate commonTemplateThree = jobMapper.getTemplateInfo(commonTemplateTwo.getPId());
                        if (commonTemplateThree != null) {
                            //查询是否配置
                            String idThree = jobMapper.getIsConfigTemplate(userConfigForm.getUserId(),
                                    userConfigForm.getJobCode(),commonTemplateThree.getId(),userConfigForm.getProjectId());
                            if(StringUtils.isBlank(idThree)) {
                                //新增配置
                                UserTemplateConfig userTemplateConfig = this.getUserTemplateConfig(commonTemplateThree, userConfigForm.getUserId(),
                                        userConfigForm.getJobCode(), userConfigForm.getProjectId());
                                userTemplateConfig.setId(templateThreeId);
                                jobMapper.saveUserTemplateConfig(userTemplateConfig);
                                //保存模板信息
                                UserTemplateConfig userTemplateConfigTwo = this.getUserTemplateConfig(commonTemplateTwo,userConfigForm.getUserId(),
                                        userConfigForm.getJobCode(),userConfigForm.getProjectId());
                                userTemplateConfigTwo.setId(templateTwoId);
                                userTemplateConfigTwo.setPid(templateThreeId);
                                jobMapper.saveUserTemplateConfig(userTemplateConfigTwo);
                            }else{
                                //保存模板信息
                                UserTemplateConfig userTemplateConfigTwo = this.getUserTemplateConfig(commonTemplateTwo,userConfigForm.getUserId(),
                                        userConfigForm.getJobCode(),userConfigForm.getProjectId());
                                userTemplateConfigTwo.setId(templateTwoId);
                                userTemplateConfigTwo.setPid(idThree);
                                jobMapper.saveUserTemplateConfig(userTemplateConfigTwo);
                            }
                        }
                        UserTemplateConfig userTemplateConfigOne = this.getUserTemplateConfig(commonTemplate,userConfigForm.getUserId(),
                                userConfigForm.getJobCode(),userConfigForm.getProjectId());
                        userTemplateConfigOne.setId(templateId);
                        userTemplateConfigOne.setPid(templateTwoId);
                        jobMapper.saveUserTemplateConfig(userTemplateConfigOne);
                    }else{
                        UserTemplateConfig userTemplateConfigOne = this.getUserTemplateConfig(commonTemplate,userConfigForm.getUserId(),
                                userConfigForm.getJobCode(),userConfigForm.getProjectId());
                        userTemplateConfigOne.setId(templateId);
                        userTemplateConfigOne.setPid(idTwo);
                        jobMapper.saveUserTemplateConfig(userTemplateConfigOne);
                    }
                }else{
                    UserTemplateConfig userTemplateConfigOne = this.getUserTemplateConfig(commonTemplate,userConfigForm.getUserId(),
                            userConfigForm.getJobCode(),userConfigForm.getProjectId());
                    userTemplateConfigOne.setId(templateId);
                    jobMapper.saveUserTemplateConfig(userTemplateConfigOne);
                }
            }
        }
    }

    /***
     *
     * @param commonTemplate
     * @param userId
     * @param jobCode
     * @param projectId
     *@return {}
     *@throws
     *@Description: 配置信息
     *@author FuYong
     *@date 2020/11/16 19:13
     */
    private UserTemplateConfig getUserTemplateConfig(CommonTemplate commonTemplate,String userId,String jobCode,String projectId){
        UserTemplateConfig userTemplateConfig = new UserTemplateConfig();
        userTemplateConfig.setGroupCode(commonTemplate.getGroupCode());
        userTemplateConfig.setGroupDesc(commonTemplate.getGroupDesc());
        userTemplateConfig.setGroupName(commonTemplate.getGroupName());
        userTemplateConfig.setGroupValue(commonTemplate.getGroupValue());
        userTemplateConfig.setJobCode(jobCode);
        userTemplateConfig.setProjectId(projectId);
        userTemplateConfig.setTemplateId(commonTemplate.getId());
        userTemplateConfig.setType(commonTemplate.getType());
        userTemplateConfig.setUserId(userId);
        userTemplateConfig.setLevel(commonTemplate.getLevel());
        userTemplateConfig.setListIndex(0);
        return userTemplateConfig;
    }

    /***
     *
     * @param userId
     * @param projectId
     *@return {}
     *@throws
     *@Description: 大客户经理二级经纪人移动
     *@author FuYong
     *@date 2021/4/25 11:35
     */
    @Override
    public String saveBrokerAccountRecords(String userId,String projectId){
        //查询大客户经理项目
        String message = null;
        List<String> projectIdList = jobMapper.getProjectIdList(userId);
        if(projectIdList != null && projectIdList.size() > 0){
            //查询项目名称
            String projectName = jobMapper.getProjectName(projectId);
            //查询用户信息
            Map userMap = jobMapper.getUserDesc(userId);
            //查询大客户经理下的二级经纪人
            Map paramMap = new HashMap();
            paramMap.put("projectIdList",projectIdList);
            paramMap.put("userId",userId);
            List<Map> brokerMapList = jobMapper.getBrokerList(paramMap);
            if(brokerMapList != null && brokerMapList.size() > 0){
                //获取二级经纪人id
                List<String> brokerList = new ArrayList<>();
                for (Map map : brokerMapList){
                    brokerList.add(MapUtils.getString(map,"brokerId"));
                }
                //查询当前项目下有大客户经理的二级经纪人
                List<String> cstTwoList = jobMapper.getSecCstListByPojId(projectId,userId);
                //查询当前项目下无大客户经理的二级经纪人
                List<Map> oldBrokerMapList = jobMapper.getSecCstTwoListByPojId(projectId, brokerList);
                int total = brokerMapList.size();
                if(cstTwoList != null && cstTwoList.size() > 0){
                    for (int i = 0; i < brokerMapList.size(); i++) {
                        String brokerIds = String.valueOf(brokerMapList.get(i).get("brokerId"));
                        if(cstTwoList.contains(brokerIds)){
                            brokerMapList.remove(brokerMapList.get(i));
                            i--;
                        }
                    }
                }
                int brokerSize = brokerMapList.size();
                int surplus = total - brokerSize;
                //批次id
                String batchId = UUID.randomUUID().toString();
                //处理数据
                List<BrokerAccount> brokerAccountList = new ArrayList<>();
                List<BrokerAccount> editBrokerAccountList = new ArrayList<>();
                List<BrokerAccountRecords> brokerAccountRecordsList = new ArrayList<>();
                if(oldBrokerMapList != null && oldBrokerMapList.size() > 0){
                    for (Map brokerMap : brokerMapList) {
                        String brokerId = String.valueOf(brokerMap.get("brokerId"));
                        String oldId = "";
                        boolean flag = false;
                        for (int i = 0; i < oldBrokerMapList.size(); i++) {
                            String oldBrokerId = String.valueOf(oldBrokerMapList.get(i).get("brokerId"));
                            if(brokerId.equals(oldBrokerId)){
                                oldId = String.valueOf(oldBrokerMapList.get(i).get("id"));
                                flag = true;
                                oldBrokerMapList.remove(oldBrokerMapList.get(i));
                                i--;
                                break;
                            }
                        }
                        if(flag){
                            //处理大客户经理二级经纪人关联表
                            BrokerAccount brokerAccount = new BrokerAccount();
                            brokerAccount.setAccountId(userId);
                            brokerAccount.setId(oldId);
                            editBrokerAccountList.add(brokerAccount);
                        }else{
                            //处理大客户经理二级经纪人关联表
                            BrokerAccount brokerAccount = new BrokerAccount();
                            brokerAccount.setAccountId(userId);
                            brokerAccount.setBrokerId(String.valueOf(brokerMap.get("brokerId")));
                            brokerAccount.setProjectId(projectId);
                            brokerAccount.setProjectName(projectName);
                            brokerAccountList.add(brokerAccount);
                        }
                        //处理级经纪人重分配记录
                        BrokerAccountRecords brokerAccountRecords = new BrokerAccountRecords();
                        brokerAccountRecords.setAccountId(userId);
                        brokerAccountRecords.setAccountMobile(String.valueOf(userMap.get("Mobile")));
                        brokerAccountRecords.setAccountName(String.valueOf(userMap.get("EmployeeName")));
                        brokerAccountRecords.setAccountIdOld(null);
                        brokerAccountRecords.setAccountMobileOld(null);
                        brokerAccountRecords.setAccountNameOld(null);
                        brokerAccountRecords.setCreator(SecurityUtils.getUserId());
                        brokerAccountRecords.setBrokerId(String.valueOf(brokerMap.get("brokerId")));
                        brokerAccountRecords.setBrokerMobile(String.valueOf(brokerMap.get("Mobile")));
                        brokerAccountRecords.setBrokerName(String.valueOf(brokerMap.get("Name")));
                        brokerAccountRecords.setBrokerOpenId(String.valueOf(brokerMap.get("OpenId")));
                        brokerAccountRecords.setProjectId(projectId);
                        brokerAccountRecords.setProjectName(projectName);
                        brokerAccountRecords.setProjectIdOld(null);
                        brokerAccountRecords.setProjectNameOld(null);
                        brokerAccountRecords.setRemarks("PC端引入大客户经理角色");
                        brokerAccountRecords.setEntrance("PC端引入大客户经理角色");
                        brokerAccountRecords.setBatchId(batchId);
                        brokerAccountRecordsList.add(brokerAccountRecords);
                    }
                    //当前项目下无大客户经理的二级经纪人 分配
                    if(oldBrokerMapList != null && oldBrokerMapList.size() > 0){
                        for (Map maps : oldBrokerMapList){
                            //处理大客户经理二级经纪人关联表
                            BrokerAccount brokerAccount = new BrokerAccount();
                            brokerAccount.setAccountId(userId);
                            brokerAccount.setId(String.valueOf(maps.get("id")));
                            editBrokerAccountList.add(brokerAccount);

                            //处理级经纪人重分配记录
                            BrokerAccountRecords brokerAccountRecords = new BrokerAccountRecords();
                            brokerAccountRecords.setAccountId(userId);
                            brokerAccountRecords.setAccountMobile(String.valueOf(userMap.get("Mobile")));
                            brokerAccountRecords.setAccountName(String.valueOf(userMap.get("EmployeeName")));
                            brokerAccountRecords.setAccountIdOld(null);
                            brokerAccountRecords.setAccountMobileOld(null);
                            brokerAccountRecords.setAccountNameOld(null);
                            brokerAccountRecords.setCreator(SecurityUtils.getUserId());
                            brokerAccountRecords.setBrokerId(String.valueOf(maps.get("brokerId")));
                            brokerAccountRecords.setBrokerMobile(String.valueOf(maps.get("Mobile")));
                            brokerAccountRecords.setBrokerName(String.valueOf(maps.get("Name")));
                            brokerAccountRecords.setBrokerOpenId(String.valueOf(maps.get("OpenId")));
                            brokerAccountRecords.setProjectId(projectId);
                            brokerAccountRecords.setProjectName(projectName);
                            brokerAccountRecords.setProjectIdOld(null);
                            brokerAccountRecords.setProjectNameOld(null);
                            brokerAccountRecords.setRemarks("PC端引入大客户经理角色");
                            brokerAccountRecords.setEntrance("PC端引入大客户经理角色");
                            brokerAccountRecords.setBatchId(batchId);
                            brokerAccountRecordsList.add(brokerAccountRecords);
                        }
                    }
                }else {
                    for (Map brokerMap : brokerMapList) {
                        //处理大客户经理二级经纪人关联表
                        BrokerAccount brokerAccount = new BrokerAccount();
                        brokerAccount.setAccountId(userId);
                        brokerAccount.setBrokerId(String.valueOf(brokerMap.get("brokerId")));
                        brokerAccount.setProjectId(projectId);
                        brokerAccount.setProjectName(projectName);
                        brokerAccountList.add(brokerAccount);
                        //处理级经纪人重分配记录
                        BrokerAccountRecords brokerAccountRecords = new BrokerAccountRecords();
                        brokerAccountRecords.setAccountId(userId);
                        brokerAccountRecords.setAccountMobile(String.valueOf(userMap.get("Mobile")));
                        brokerAccountRecords.setAccountName(String.valueOf(userMap.get("EmployeeName")));
                        brokerAccountRecords.setAccountIdOld(null);
                        brokerAccountRecords.setAccountMobileOld(null);
                        brokerAccountRecords.setAccountNameOld(null);
                        brokerAccountRecords.setCreator(SecurityUtils.getUserId());
                        brokerAccountRecords.setBrokerId(String.valueOf(brokerMap.get("brokerId")));
                        brokerAccountRecords.setBrokerMobile(String.valueOf(brokerMap.get("Mobile")));
                        brokerAccountRecords.setBrokerName(String.valueOf(brokerMap.get("Name")));
                        brokerAccountRecords.setBrokerOpenId(String.valueOf(brokerMap.get("OpenId")));
                        brokerAccountRecords.setProjectId(projectId);
                        brokerAccountRecords.setProjectName(projectName);
                        brokerAccountRecords.setProjectIdOld(null);
                        brokerAccountRecords.setProjectNameOld(null);
                        brokerAccountRecords.setRemarks("PC端引入大客户经理角色");
                        brokerAccountRecords.setEntrance("PC端引入大客户经理角色");
                        brokerAccountRecords.setBatchId(batchId);
                        brokerAccountRecordsList.add(brokerAccountRecords);
                    }
                }
                if(editBrokerAccountList != null && editBrokerAccountList.size() > 0) {
                    List<String> brokerActIdList = new ArrayList<>();
                    for (BrokerAccount brokerAccount : editBrokerAccountList){
                        brokerActIdList.add(brokerAccount.getId());
                    }
                    //查询分配经纪人客户业绩
                    List<AccountPerformance> accountPerformanceList = jobMapper.getAccountPerformanceList(brokerActIdList);
                    if (accountPerformanceList.size() > 0) {
                        //查询大客户经理活动id
                        List<String> activityIdList = jobMapper.getActivityByAccountId(userId, projectId);
                        List<String> idList = new ArrayList<>();
                        List<String> idListTwo = new ArrayList<>();
                        for (AccountPerformance accountPerformance : accountPerformanceList) {
                            if (StringUtils.isNotBlank(accountPerformance.getActiveId())
                                    && !activityIdList.contains(accountPerformance.getActiveId())
                                    && "1".equals(accountPerformance.getClueStatus())) {
                                idList.add(accountPerformance.getId());
                            } else {
                                if ("1".equals(accountPerformance.getClueStatus())) {
                                    idListTwo.add(accountPerformance.getId());
                                }
                            }
                        }
                        //更新业绩归属
                        if (idList.size() > 0) {
                            jobMapper.updateAccountPerformance(idList, userId);
                        }
                        if (idListTwo.size() > 0) {
                            jobMapper.updateAccountPerformanceTwo(idListTwo, userId);
                        }
                    }
                }
                //保存分配记录
                BrokerAccountRecordsBatch brokerAccountRecordsBatch = new BrokerAccountRecordsBatch();
                brokerAccountRecordsBatch.setId(batchId);
                brokerAccountRecordsBatch.setAccountId(userId);
                brokerAccountRecordsBatch.setCountNumber(brokerMapList.size() +"");
                brokerAccountRecordsBatch.setCreateUser(SecurityUtils.getUserId());
                brokerAccountRecordsBatch.setEntrance("PC端引入大客户经理角色");
                brokerAccountRecordsBatch.setReason("PC端引入大客户经理角色");
                brokerAccountRecordsBatch.setProjectId(projectId);
                message = "该大客户经理在其他项目的"+total+"位合伙人，有"+brokerSize+"位已为您自动引入到了"+projectName+"，其余"+surplus+"位因已在"+projectName+"存在了大客户经理归属不允许引入。";
                brokerAccountRecordsBatch.setMessage(message);
                jobMapper.saveBrokerAccountRecordsBatch(brokerAccountRecordsBatch);
                //保存二级经纪人
                if(brokerAccountList != null && brokerAccountList.size() > 0) {
                    jobMapper.saveBrokerAccount(brokerAccountList);
                }
                //修改二级经纪人
                if(editBrokerAccountList != null && editBrokerAccountList.size() > 0) {
                    jobMapper.editBrokerAccount(editBrokerAccountList);
                }
                //保存分配批次
                if(brokerAccountRecordsList != null && brokerAccountRecordsList.size() > 0) {
                    jobMapper.saveBrokerAccountRecords(brokerAccountRecordsList);
                }
            }
        }
        return message;
    }

    private Map returnMap = new HashMap(4);

    /**
     * 保存用户-岗位
     *
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map saveUserJob(Map map) {
        Map resultMap = new HashMap(4);
        if (null == map) {
            return returnMap;
        }
        // 组织id
        String orgId = map.get("orgId").toString();
        // 获取密码
        String password = map.get("Password") + "";
        String authCompanyId = map.get("authCompanyId").toString();
        String productId = map.get("productId").toString();
        String userId = map.get("userId").toString();
        // 设置密码
        if (null == map.get("Password") || "".equals(password) || "null".equalsIgnoreCase(password)) {
            map.put("Password", EncryptUtils.encryptPassword("1"));
        } else {
            map.put("Password", EncryptUtils.encryptPassword(map.get("Password").toString()));
        }

        String projectId = "";
        if (map.get("projectId") != null) {
            projectId = map.get("projectId").toString();
        }
        // 获取岗位列表
        List<Map> jobsList = (List<Map>) map.get("jobsList");
        // 获取用户名
        map.put("alias", map.get("UserName"));
        // 查询用户名是否存在
        Map userISHaving = jobMapper.getSystemUserNameExists(map);
        String ID = UUID.randomUUID().toString();
        map.put("AuthCompanyID", authCompanyId);
        map.put("UserOrgID", orgId);
        map.put("ProductID", productId);
        map.put("Creator", userId);
        map.put("ID", ID);
        map.put("accountId", ID);
        map.put("EmployeeCode",map.get("EmployeeName"));

        // 添加的新用户已经存在
        if (userISHaving != null) {
            //用户存在
            map.put("ID", userISHaving.get("ID"));
            map.put("accountId", userISHaving.get("ID"));
            //判断用户是否在本项目下有客户
            Map selectMap = new HashMap();
            selectMap.put("userId", userISHaving.get("ID"));
            selectMap.put("projectId", projectId);
            int cstCut = 0;
            // 遍历岗位
            if (jobsList != null && jobsList.size() > 0) {
                for (Map job : jobsList) {
                    // 获取jobCode
                    String jobCode = job.get("jobCode") + "";
                    if ("zygw".equals(jobCode)) {
                        cstCut = jobMapper.getCstBySalesId(selectMap);
                    } else if ("nqgw".equals(jobCode)) {
                        cstCut = jobMapper.getCstByReId(selectMap);
                    }

                    // 存在客户
                    if (cstCut > 0) {
                        String orgName = jobMapper.getOrgName(orgId);
                        Map userMap = new HashMap();
                        userMap.put("userId", userISHaving.get("ID"));
                        userMap.put("orgId", orgId);
                        userMap.put("orgName", orgName);
                        userMap.put("projectId", projectId);
                        //判断是渠道还是内场岗位调整
                        if ("zygw".equals(jobCode)) {
                            jobMapper.updateSaleTeamID(userMap);
                        } else if ("nqgw".equals(jobCode)) {
                            jobMapper.updateReportTeamID(userMap);
                        }
                    }

                    // 获取jobId
                    String jobId = job.get("jobId") + "";
                    //判断是否存在相同岗位
                    Map jobMap = jobMapper.isRepeat(userISHaving.get("ID") + "", jobId);
                    // 判断是否存在默认岗位
                    Map currenJob = jobMapper.isCurrentJob(userISHaving.get("ID") + "");
                    // 不存在默认岗位
                    if (currenJob == null) {
                        List<String> jobList = Stream.of("nqgw", "zygw", "xsjl", "xszz", "tdjl", "qdjl", "qdzz", "dkhjl", "qyptgl", "qypt").collect(Collectors.toList());
                        if (jobList.contains(jobCode)) {
                            map.put("CurrentJob", 1);
                        } else {
                            map.put("CurrentJob", 0);
                        }
                    }
                    // 存在默认岗位
                    else {
                        map.put("CurrentJob", 0);
                    }
                    // 不存在相同岗位， 进行添加关联关系到 s_jobsuserrel 表中
                    if (jobMap == null) {

                        map.put("JobID", jobId);
                        Map jobId1 = jobMapper.getOrgJobId(jobId);
                        String jobCodeCom = jobId1.get("JobCode") + "";
//                        Set<String> validStrings = new HashSet<>();
//                        validStrings.add("qfsj");
//                        validStrings.add("qyqfsj");
//                        validStrings.add("qyfz");
//                        validStrings.add("qyz");
//                        validStrings.add("qyzygw");
//                        validStrings.add("qyzszj");
//                        validStrings.add("qyxsjl");
//                        validStrings.add("qyyxjl");
//                        validStrings.add("qycsss");
//                        validStrings.add("10001");
//                        validStrings.add("xmz");
//                        validStrings.add("yxjl");
//                        validStrings.add("zszj");
//                        validStrings.add("xsjl");
//                        validStrings.add("zygw");
//
//                        if (validStrings.contains(jobCodeCom)){
                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("nickname",map.get("EmployeeName"));
                            map1.put("username",map.get("UserName"));
                            map1.put("password",map.get("password"));
                            map1.put("uid",map.get("ID"));
                            map1.put("did",jobId1.get("CommonJobID"));
                            map1.put("sid",jobId1.get("JobOrgID"));
                            map1.put("phone",map.get("Mobile"));
                            Map id = jobMapper.getZgQx(SecurityUtils.getUserId());
                            String  token1 = "";
                            if(id != null && null!=id.get("sid") && !"".equals(id.get("sid"))){
                                Map<String, Object> loginMap = new HashMap<>();
                                loginMap.put("secret",secret);
                                loginMap.put("channel_id",channelId);
                                loginMap.put("sid",id.get("sid"));
                                loginMap.put("uid",id.get("uid"));
                                loginMap.put("did",id.get("did"));
                                String s = HttpClientUtil.postHttpOutbound(urlOp, null, loginMap);
                                JSONObject jsonObject = JSONObject.parseObject(s);
                                JSONObject data1 = jsonObject.getJSONObject("data");
                                token1 = data1.get("token").toString();
                            }else{
                                token1 = redisService.getVal("outbound."+map.get("username"))+"";
                            }
                        // 获取角色类型并放入map
                        RoleType roleType = JobPermissionUtil.getRoleType(jobCodeCom);
                        map1.put("role_type", roleType.getCode());
                            HttpClientUtil.postHttpOutbound(addUserOp,token1,map1);
//                        }
                        jobMapper.saveAccountToJobUserURl(map);
                    }
                    // 存在相同岗位，之间返回
                    else {
                        //表示岗位已存在
                        resultMap.put("code", "1001");
                        // return resultMap;
                    }
                }
            }
        }
        // 添加的用户不存在
        else {
            List<String> jobList = Stream.of("nqgw", "zygw", "xsjl", "xszz", "tdjl", "qdjl", "qdzz", "dkhjl", "qyptgl", "qypt").collect(Collectors.toList());

            //保存账号表
            jobMapper.saveSystemUser(map);

            if (null != jobsList && jobsList.size() > 0) {
                // 进行遍历岗位
                for (Map job : jobsList) {
                    // 获取jobCode
                    String jobCode = job.get("jobCode") + "";
                    if (jobList.contains(jobCode)) {
                        map.put("CurrentJob", 1);
                    } else {
                        map.put("CurrentJob", 0);
                    }
                    map.put("JobID", job.get("jobId") + "");
                    //保存平台与岗位的关系
                    jobMapper.saveAccountToJobUserURl(map);
                    Map jobId = jobMapper.getOrgJobId(job.get("jobId") + "");
                    String jobCodeCom = jobId.get("JobCode") + "";
//                    Set<String> validStrings = new HashSet<>();
//                    validStrings.add("qfsj");
//                    validStrings.add("qyqfsj");
//                    validStrings.add("qyfz");
//                    validStrings.add("qyz");
//                    validStrings.add("qyzygw");
//                    validStrings.add("qyzszj");
//                    validStrings.add("qyxsjl");
//                    validStrings.add("qyyxjl");
//                    validStrings.add("qycsss");
//                    validStrings.add("10001");
//                    validStrings.add("xmz");
//                    validStrings.add("yxjl");
//                    validStrings.add("zszj");
//                    validStrings.add("xsjl");
//                    validStrings.add("zygw");
//
//                    if (validStrings.contains(jobCodeCom)) {
                        Map<String, Object> map1 = new HashMap<>();
                        map1.put("nickname", map.get("EmployeeName"));
                        map1.put("username", map.get("UserName"));
                        map1.put("password", map.get("password"));
                        map1.put("uid", map.get("ID"));
                        map1.put("did", jobId.get("CommonJobID"));
                        map1.put("sid", jobId.get("JobOrgID"));
                        map1.put("phone", map.get("Mobile"));
                        Map id = jobMapper.getZgQx(SecurityUtils.getUserId());
                        String  token1 = "";
                        if(id != null  && null!=id.get("sid") && !"".equals(id.get("sid"))){
                            Map<String, Object> loginMap = new HashMap<>();
                            loginMap.put("secret",secret);
                            loginMap.put("channel_id",channelId);
                            loginMap.put("sid",id.get("sid"));
                            loginMap.put("uid",id.get("uid"));
                            loginMap.put("did",id.get("did"));
                            String s = HttpClientUtil.postHttpOutbound(urlOp, null, loginMap);
                            JSONObject jsonObject = JSONObject.parseObject(s);
                            JSONObject data1 = jsonObject.getJSONObject("data");
                            token1 = data1.get("token").toString();
                        }else{
                            token1 = redisService.getVal("outbound."+map.get("username"))+"";
                        }
                    // 获取角色类型并放入map
                    RoleType roleType = JobPermissionUtil.getRoleType(jobCodeCom);
                    map1.put("role_type", roleType.getCode());
                        HttpClientUtil.postHttpOutbound(addUserOp, token1, map1);
//                    }
                }
            }
        }

        // 遍历岗位
        if (jobsList != null && jobsList.size() > 0) {
            for (Map job : jobsList) {
                // 获取jobCode
                String jobCode = job.get("jobCode") + "";
                // 引入置业岗位 同步维护名片数据
                if ("zygw".equals(jobCode)) {
                    userCardService.updateCardData(map);
                    Map jsonMap = new HashMap();
                    //只有启用的时候才调
                    if (map.get("Status").toString().equals("1")) {
                        // 1：在职：2：离职
                        jsonMap.put("leave", 1);
                        // 同步置业顾问
                        jsonMap.put("projectId", projectId);
                        jsonMap.put("salesId", map.get("accountId"));
                        jsonMap.put("salesName", map.get("EmployeeName"));
                        jsonMap.put("salesMobile", map.get("Mobile"));
                        //1：正常 -1：冻结
                        jsonMap.put("status", 1);
                        HttpRequestUtil.httpPost(consultantSyncURL, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)), false);
                    }
                }
                if ("dkhjl".equals(jobCode)) {
                    String message = this.saveBrokerAccountRecords(String.valueOf(job.get("ID")), projectId);
                    resultMap.put("message", message);
                }
                //表示岗位已存在
                resultMap.put("code", "200");
            }
        }

        return resultMap;
    }

    /**
     * 查询所有的岗位
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody selectJobsList(Map map) {
        PageHelper.startPage((Integer) map.get("current"), (Integer) map.get("size"));
        List<Map> mapList = jobMapper.selectJobsList(map);
        System.out.println("selectJobsList: 方法结果---" + mapList);
        return ResultBody.success(new PageInfo<>(mapList));
    }

    /**
     * 绑定项目
     *
     * @param bindProject
     * @return
     */
    @Override
    @Transactional
    public ResultBody updateBindProject(BindProject bindProject) {

        Connection conn = null; PreparedStatement ps = null; int executeUpdate = 0;
        try {
            Map map = new HashMap(4);
            map.put("projectId", bindProject.getProjectId());
            map.put("bindProjectName", bindProject.getGenerateName());
            map.put("startTime", bindProject.getStartTime());
            map.put("endTime", bindProject.getEndTime());
            map.put("bindProjectId", bindProject.getId());
            // 更新 b_project 表的绑定项目名称
            jobMapper.updateBindProjectName(map);
            String url = "jdbc:mysql://118.190.56.178:3306/authcompany" + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(url, "root", "root");
            String sql = " update b_bind_project \n" +
                    " SET project_id = '" + bindProject.getProjectId() + "', \n" +
                    " project_name = '" + bindProject.getProjectName() + "', \n" +
                    " is_bind = 1 \n" +
                    " where id = " + bindProject.getId();
            System.out.println("拼接的sql为： "  + sql);
            ps = conn.prepareStatement(sql);
            //执行脚本
            executeUpdate = ps.executeUpdate();
            System.out.println("返回的结果： " + executeUpdate);
            ps.close();
            conn.close();
        } catch (SQLException e) {
            try {
                ps.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return ResultBody.success(executeUpdate);
    }



    @Override
    public void   ExportUsersAnd   (HttpServletResponse response) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<UsersAndPositions> usersAndPositions = jobMapper.getUsersAndPositions();

        if(usersAndPositions!=null && usersAndPositions.size()>0){
            UsersAndPositions projectRuleDetail = usersAndPositions.get(0);
            String[] excelTitle = projectRuleDetail.getExcelTitle();
            for (UsersAndPositions u : usersAndPositions){

                Object[] oArray = u.toData();
                dataset.add(oArray);
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel("用户岗位", excelTitle, dataset, "用户岗位", response, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
