package cn.visolink.system.companyQw.dao;

import cn.visolink.system.companyQw.model.ChannelCode;
import cn.visolink.system.companyQw.model.MediaDetail;
import cn.visolink.system.companyQw.model.QwUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/12/31 9:34
 **/
public interface CompanyQwSynDao {

    /**
     * @Author wanggang
     * @Description //获取父部门ID
     * @Date 19:03 2022/1/4
     * @Param [orgPid]
     * @return java.lang.String
     **/
    String getDeptPid(String orgPid);

    /**
     * @Author wanggang
     * @Description //保存部门至本地
     * @Date 19:03 2022/1/4
     * @Param [resultMap]
     * @return void
     **/
    void addQwDept(Map resultMap);
    /**
     * @Author wanggang
     * @Description //保存用户
     * @Date 19:52 2022/1/4
     * @Param [qwUserVo]
     * @return void
     **/
    void addQwUser(QwUserVo qwUserVo);
    /**
     * @Author wanggang
     * @Description //保存用户部门
     * @Date 19:52 2022/1/4
     * @Param [qwUserVo]
     * @return void
     **/
    void addQwUserRel(QwUserVo qwUserVo);
    /**
     * @Author wanggang
     * @Description //查询用户是否存在
     * @Date 20:16 2022/1/4
     * @Param [mobile]
     * @return int
     **/
    Map getQwUserIsOk(String mobile);
    /**
     * @Author wanggang
     * @Description //更新根部门信息
     * @Date 16:45 2022/1/5
     * @Param [orgId]
     * @return void
     **/
    void updateQwDept(String orgId);
    /**
     * @Author wanggang
     * @Description //查询部门信息
     * @Date 10:40 2022/1/6
     * @Param []
     * @return java.util.List<java.lang.String>
     **/
    List<String> getDeptAll();
    /**
     * @Author wanggang
     * @Description //保存客户临时数据
     * @Date 14:33 2022/1/6
     * @Param [authorizedMap]
     * @return void
     **/
    void addQwCstTemp(Map<String, String> authorizedMap);
    /**
     * @Author wanggang
     * @Description //删除成员
     * @Date 18:25 2022/1/11
     * @Param [authorizedMap]
     * @return void
     **/
    void delQwUser(Map<String, String> authorizedMap);
    /**
     * @Author wanggang
     * @Description //保存成员
     * @Date 18:50 2022/1/11
     * @Param [authorizedMap]
     * @return void
     **/
    void addQwUserNew(Map<String, String> authorizedMap);
    /**
     * @Author wanggang
     * @Description //保存部门信息
     * @Date 18:55 2022/1/11
     * @Param [deptMaps]
     * @return void
     **/
    void addUserDepts(@Param("list") List<Map> deptMaps);
    /**
     * @Author wanggang
     * @Description //获取成员可关联账号ID
     * @Date 19:13 2022/1/11
     * @Param [userMap]
     * @return java.lang.String
     **/
    String getAcUserId(Map userMap);
    /**
     * @Author wanggang
     * @Description //获取原成员信息
     * @Date 19:39 2022/1/11
     * @Param [userid]
     * @return cn.visolink.system.companyQw.model.QwUserVo
     **/
    QwUserVo getOldUser(String userid);
    /**
     * @Author wanggang
     * @Description //删除部门
     * @Date 19:45 2022/1/11
     * @Param [userid]
     * @return void
     **/
    void delQwUserOrg(String userid);
    /**
     * @Author wanggang
     * @Description //更新成员信息
     * @Date 19:55 2022/1/11
     * @Param [authorizedMap]
     * @return void
     **/
    void updateQwUser(Map<String, String> authorizedMap);
    /**
     * @Author wanggang
     * @Description //获取原部门
     * @Date 20:08 2022/1/11
     * @Param [userid]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getOldOrg(String userid);
    /**
     * @Author wanggang
     * @Description //更新所有表信息
     * @Date 20:12 2022/1/11
     * @Param [authorizedMap]
     * @return void
     **/
    void updateAllUser(Map<String, String> authorizedMap);
    /**
     * @Author wanggang
     * @Description //获取渠道码信息
     * @Date 15:21 2022/1/21
     * @Param [userId, state]
     * @return cn.visolink.system.companyQw.model.ChannelCode
     **/
    ChannelCode getChannelCodeIsWelcome(@Param("userId") String userId, @Param("state") String state);
}
