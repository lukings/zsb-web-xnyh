package cn.visolink.system.companyQw.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.companyQw.model.ProMediaVo;
import cn.visolink.system.companyQw.model.QwUserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CompanyQwService
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/12/31 9:35
 **/
public interface CompanyQwSynService {

    /**
     * @Author wanggang
     * @Description //保存组织创建部门
     * @Date 10:49 2021/12/31
     * @Param [request]
     * @return java.util.List<java.util.Map>
     **/
    String addDept(String orgName,String orgPid,String orgId,String proId);

    /**
     * @Author wanggang
     * @Description //删除部门
     * @Date 10:49 2021/12/31
     * @Param [request]
     * @return java.util.List<java.util.Map>
     **/
    ResultBody delDept(HttpServletRequest request,String id);

    /**
     * @Author wanggang
     * @Description //新增人员
     * @Date 14:25 2021/12/31
     * @Param [QwUserVo]
     * @return
     **/
    String addUser(QwUserVo qwUserVo);

    /**
     * @Author wanggang
     * @Description //引入人员
     * @Date 14:25 2021/12/31
     * @Param [qwUserVo]
     * @return
     **/
    String pushUser(QwUserVo qwUserVo);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 10:29 2022/1/6
     * @Param [request]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getQwOrgs(HttpServletRequest request);
}
