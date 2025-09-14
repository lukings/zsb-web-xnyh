package cn.visolink.system.companyQw.service.impl;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.companyQw.dao.CompanyQwSynDao;
import cn.visolink.system.companyQw.model.QwUserVo;
import cn.visolink.system.companyQw.service.CompanyQwSynService;
import cn.visolink.utils.HttpRequestUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @ClassName CompanyQwSynServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/1/4 9:30
 **/
@Service
public class CompanyQwSynServiceImpl implements CompanyQwSynService {

    @Autowired
    private CompanyQwSynDao companyQwSynDao;
    @Autowired
    private CompanyQwServiceImpl companyQwService;

    @Override
    public String addDept(String orgName, String orgPid, String orgId, String proId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String companycode = request.getHeader("companycode");
        //根组织
        if ("-1".equals(orgPid) || StringUtils.isEmpty(orgPid)){
            //更新企微根部门
            companyQwSynDao.updateQwDept(orgId);
            return "1";
        }else{
            //根据父级组织ID查询 父级部门ID
            String deptPid = companyQwSynDao.getDeptPid(orgPid);
            if (StringUtils.isEmpty(deptPid)){
                return "-1";
            }
            String id = "";
            Map wxMap = new HashMap();
            wxMap.put("parentid",deptPid);
            wxMap.put("name",orgName);
            String token = companyQwService.getWxToken(companycode,"1",false);
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
            //如果成功
            if (re!=null && "0".equals(re.getString("errcode"))){
                id = re.getString("id");
            }else{

            }
            if (!StringUtils.isEmpty(id)){
                Map resultMap = new HashMap();
                resultMap.put("dept_id",id);
                resultMap.put("parentid",deptPid);
                resultMap.put("dept_name",orgName);
//                resultMap.put("creator", SecurityUtils.getUserId());
                resultMap.put("jk_org_id",orgId);
                resultMap.put("jk_pro_id",proId);
                //保存部门到本地
                companyQwSynDao.addQwDept(resultMap);
                return "1";
            }else{
                return "-2";
            }
        }
    }

    @Override
    public ResultBody delDept(HttpServletRequest request, String id) {
        return null;
    }

    @Override
    public String addUser(QwUserVo qwUserVo) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String companycode = request.getHeader("companycode");
        String token = companyQwService.getWxToken(companycode,"1",false);
        //获取部门ID
        String deptId = companyQwSynDao.getDeptPid(qwUserVo.getMainDepartment());
        qwUserVo.setMainDepartment(deptId);
        List<String> department = new ArrayList<>();
        department.add(deptId);
        qwUserVo.setDepartment(department);
        qwUserVo.setUserid(qwUserVo.getMobile());
        //调用接口保存成员
        JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(qwUserVo)),false);
        //如果成功
        if (re!=null && "0".equals(re.getString("errcode"))){

        }else{

        }
        //保存成员及成员部门到本地
        companyQwSynDao.addQwUser(qwUserVo);
        return "1";
    }

    @Override
    public String pushUser(QwUserVo qwUserVo) {
        //查询是否存在成员
        Map userMap = companyQwSynDao.getQwUserIsOk(qwUserVo.getMobile());
        if (userMap == null){
            this.addUser(qwUserVo);
        }else{
            String userId = userMap.get("userid")+"";
            String[] depts = null;
            if (userMap.get("depts")!=null && !"".equals(userMap.get("depts").toString())){
                depts = userMap.get("depts").toString().split(",");
            }
            //获取部门ID
            String deptId = companyQwSynDao.getDeptPid(qwUserVo.getMainDepartment());
            if (deptId!=null){
                boolean flag = false;
                if (depts!=null){
                    for (String dept:depts) {
                        if (deptId.equals(dept)){
                            flag = true;
                            break;
                        }
                    }
                }

                //如果存在此部门不做任何操作，否则更新企微成员信息
                if (!flag){
                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                    String companycode = request.getHeader("companycode");
                    List<String> deptNews = new ArrayList<>();
                    if (depts!=null){
                        List<String> deptNew = Arrays.asList(depts);
                        deptNews.addAll(deptNew);
                    }
                    deptNews.add(deptId);
                    Map wxMap = new HashMap();
                    wxMap.put("userid",userId);
                    wxMap.put("department",deptNews);
                    String token = companyQwService.getWxToken(companycode,"1",false);
                    JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
                    //如果成功
                    if (re!=null && "0".equals(re.getString("errcode"))){
                    }else{
                    }
                    qwUserVo.setMainDepartment(deptId);
                    qwUserVo.setUserid(userId);
                    //新增部门
                    companyQwSynDao.addQwUserRel(qwUserVo);
                }
            }
        }
        return "1";
    }

    @Override
    public ResultBody getQwOrgs(HttpServletRequest request) {
        String companycode = request.getHeader("companycode");
        String token = companyQwService.getWxToken(companycode,"1",false);
        String re = HttpRequestUtil.httpGet("https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token="+token,false);
        //查询原部门信息
        List<String> deptIds = companyQwSynDao.getDeptAll();
        if (deptIds!=null && deptIds.size()>0){
            return ResultBody.error(-120003,"公司企微组织已初始化无需再次配置！");
        }
        if (re!=null){
            JSONObject object = JSONObject.parseObject(re);
            JSONArray array = object.getJSONArray("department");
            for (int i = 0;i<array.size();i++) {
                JSONObject dept = array.getJSONObject(i);
                Map resultMap = new HashMap();
                resultMap.put("dept_id",dept.getIntValue("id"));
                resultMap.put("parentid",dept.getIntValue("parentid"));
                resultMap.put("dept_name",dept.getString("name"));
                //保存部门到本地
                companyQwSynDao.addQwDept(resultMap);
            }
        }

        return ResultBody.success("同步完成！！");
    }
}
