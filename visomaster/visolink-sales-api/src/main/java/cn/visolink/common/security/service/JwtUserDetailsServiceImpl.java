package cn.visolink.common.security.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.constant.BizConstant;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.exception.BadRequestException;
import cn.visolink.utils.CommUtils;
import com.google.gson.Gson;
import org.apache.wicket.core.dbhelper.api.FrameServiceApi;
import org.apache.wicket.core.dbhelper.sql.DBSQLServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WCL
 * @date 2018-11-22
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class JwtUserDetailsServiceImpl implements UserDetailsService {

//    @Autowired
//    private UserService userService;

    @Autowired
    private JwtPermissionService permissionService;


    @Autowired
    private DBSQLServiceImpl dbsqlService;

    protected Gson gson = new Gson();
    @Autowired
    private FrameServiceApi frameServiceApi;

    @Autowired
    RedisService redisService;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;


    @Autowired
    private AuthMapper authMapper;


    @Override
    public UserDetails loadUserByUsername(String userName) {
//      String[] str = userName.split("!");
//      userName = str[0];
//      String companyCode = str[1];
        /**
         * redis键名命名规则：
         * 系统(来源).对象.id.属性
         * 例如：用户的工作经验，就是hr.employee.12345.experience
         */
        UserDetails object = (UserDetails) redisUtil.get(VisolinkConstant.REDIS_KEY+".User"+".info"+".web"+"."+userName);
        if (null != object) {
            return object;
        }
        Map<String, Object> stringMap = MapUtil.newHashMap();
        stringMap.put("UserName", userName);
        stringMap.put("JobID", null);
        //获取用户信息
        Map userInfoMap = authMapper.mGetUserInfo(stringMap);

        System.out.print("查库获得用户信息："+userInfoMap);

        if (MapUtil.isEmpty(userInfoMap)) {
            throw new BadRequestException(-10_0002, "账号不存在,请与管理员联系");
        }
        String userId = userInfoMap.get("id").toString();
        //加载用户岗位信息
        Map currentJob = authMapper.mJobsListByUserId(userId);
        if (CollUtil.isEmpty(currentJob)) {
            throw new BadRequestException(-10_0004, "您的账号当前无任何岗位,暂时无法登录,请与管理员联系!");
        }

        //加载用户菜单信息
        String jobId = currentJob.get("ID").toString();
        List<Map> menusListByUserIdSelect = authMapper.mMenusListByUserIdAndJobId(userId, jobId, 1);
        Map menusMap = CommUtils.buildTree(menusListByUserIdSelect);
        if (CollUtil.isEmpty(menusListByUserIdSelect)) {
            throw new BadRequestException(-10_0005, "您的账号当前无任何菜单,暂时无法登录,请与管理员联系!");
        }
        /**
         * 根据Job里查询出来的OrgId,获取对应的组织
         */
        String orgId = currentJob.get("JobOrgID") + "";
        Map orgMap = authMapper.mFindByProjectIdByOrgId(orgId);
        String orgLevel = authMapper.getUserOrgLevel(userId);
        if(orgMap==null){
            orgMap = new HashMap();
        }
        orgMap.put("orgLevel",orgLevel);
        /*currentJob.put("JobCode", userInfoMap.get("JobCode"));*/
        //组装用户信息
        UserDetails jwtUser = createJwtUser(userInfoMap, currentJob, orgMap, menusMap);
        redisUtil.set(VisolinkConstant.REDIS_KEY+".User"+".info"+".web"+"."+userName,jwtUser,86400);
        return jwtUser;
    }



    public UserDetails createJwtUser(Map<String, Object> planMap, Map jobs, Map orgMap, Map menus) {
        JwtUser jwtUser = new JwtUser(
                planMap.get("id").toString(),
                planMap.get("username").toString(),
                planMap.get("password").toString(),
                planMap.get("AccountType").toString(),
                planMap.get("EmployeeCode") + "",
                planMap.get("EmployeeName").toString(),
                planMap.get("Gender") != null ? ((Integer) planMap.get("Gender")).intValue():1,
                ObjectUtil.isNull(planMap.get("Mobile")) ? "" : planMap.get("Mobile") + "",
                ObjectUtil.isNull(planMap.get("Address")) ? "" : planMap.get("Address") + "",
                ObjectUtil.isNull(planMap.get("ProjectID")) ?"" :orgMap.get("ProjectID") + "",
                ObjectUtil.isNull(planMap.get("ProjectName"))?"":orgMap.get("ProjectName") + "",
                planMap.get("AuthCompanyID").toString(),
                planMap.get("ProductID").toString(),
                planMap.get("Creator").toString(),
                planMap.get("CreateTime").toString(),
                planMap.get("Status").toString(),
                planMap.get("IsDel").toString(),
                ObjectUtil.isNotNull(planMap.get("JobID")) ? "" : planMap.get("JobID").toString(),
                jobs,
                orgMap,
                menus,
                null,
                BizConstant.status_disable.equals(planMap.get("Status")) ? false : true,
                null
        );
        return jwtUser;
    }

}
