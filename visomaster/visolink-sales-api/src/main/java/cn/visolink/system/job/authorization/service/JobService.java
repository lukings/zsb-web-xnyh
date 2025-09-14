package cn.visolink.system.job.authorization.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.job.authorization.model.BindProject;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.10
 */

public interface JobService {

    /**
     * 获取所有岗位
     */
    PageInfo<Map> getJobByAuthId(Map map);

    /**
     * 查询所有组织架构
     */

    List<Map> getAllOrg(Map map);

    /**
     * 获取通用岗位列表
     *
     * @param map
     * @return
     */
    List<Map> getAllCommonJob(Map map);

    /**
     * 查询岗位下的人员列表，或根据姓名查询人员
     *
     * @param reqMap
     * @return
     */
    Map getSystemUserList(Map reqMap);

    /**
     * 获取当前和下属所有组织岗位
     *
     * @param reqMap
     * @return
     */
    List<Map> getSystemJobAllList(Map reqMap);

    /**
     * 新增岗位-插入Jobs信息
     *
     * @param reqMap
     * @return
     */
    int saveSystemJobForManagement(Map reqMap);

    /**
     * 组织岗位功能列表查询(前端功能授权)
     *
     * @param reqMap
     * @return
     */
    Map getSystemJobAuthByUserId(Map reqMap);

    /**
     * 前后端功能授权保存
     *
     * @param reqMap
     * @return
     */
    String saveSystemJobAuthByManagement(Map reqMap);

    /**
     * 更新岗位信息
     *
     * @param reqMap
     * @return
     */
    int modifySystemJobByUserId(Map reqMap);

    /**
     * 删除岗位信息
     *
     * @param reqMap
     * @return
     */
    int removeSystemJobByUserId(Map reqMap);

    /**
     * 查询引入用户
     *
     * @param reqMap
     * @return
     */
    Map pullinUser(Map reqMap);

    /**
     * 保存用户
     *
     * @param reqMap
     * @return
     */
    Map saveSystemUser(Map reqMap);

    /**
     * 查询用户账号是否存在
     *
     * @param reqMap
     * @return
     */
    int selectSystemUserCode(Map reqMap);


    /**
     * 删除用户
     *
     * @param reqMap
     * @return
     */
    int removeSystemJobUserRel(Map reqMap);

    /**
     *修改用户
     * @param reqMap
     * @return
     */
    int modifySystemJobUserRel(Map reqMap);

    /**
     * 保存用户信息
     * @param reqMap
     * @return
     */
    Map saveSystemJobUserRel(Map reqMap);


    /**
     * 获取所有菜单
     * @return
     */
    ResultBody getAllMenu(String jobId);

    /**
     * 获取通用岗位所有菜单
     * @return
     */

    ResultBody getCommonAllMenu(String jobId,String type);



    /**
     * 保存菜单
     * @return
     */
    ResultBody saveJobMenus(Map map,String jobId);
    /**
     * 保存菜单
     * @retu通用rn
     */
    ResultBody saveCommonJobMenus(Map map,String jobId);
    /**
     * 中介公司
     * @retu通用rn
     */
    ResultBody getAllCompanyInfo(String orgId,String cid);
    /**
     * 项目组织
     * @retu通用rn
     */
    ResultBody getAllOrgProject();
    ResultBody getAllOrgProject2();

    /**
     * 更新项目
     * @retu通用rn
     */
    ResultBody updateProject(Map map);

    /**
     * 查询对应项目上的岗位信息
     * @retu通用rn
     */
    ResultBody getCurrentJobs(Map map);

    /**
     * 获取对应项目岗位数据
     * @retu通用rn
     */
    ResultBody getCityJobList(Map map);

    /**
     * 保存岗位与城市的关系
     * @retu通用rn
     */
    ResultBody saveCityJob(Map map);

    /**
     * 查询用户信息
     * @return
     */
    Map findUserDesc(Map reqMap);

    /**
     * 查询默认岗用户信息
     * @return
     */
    Map getDeSystemUserList(Map reqMap);

    /**
     * 删除用户岗位
     * @return
     */
    String removeUserRel(Map reqMap);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 11:50 2020/10/12
     * @Param [reqMap]
     * @return java.util.Map
     **/
    Map getUserDesc(Map reqMap);
    /**
     * @Author wanggang
     * @Description //获取项目岗位信息
     * @Date 15:11 2020/10/12
     * @Param [reqMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getDeJobsList(Map reqMap);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:56 2020/10/12
     * @Param [reqMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getDeComJobsList(Map reqMap);
    /**
     * @Author wanggang
     * @Description //保存岗位
     * @Date 14:13 2020/10/13
     * @Param [reqMap]
     * @return int
     **/
    int saveDeSystemUser(Map reqMap);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:53 2020/10/13
     * @Param [reqMap]
     * @return int
     **/
    int saveDeSystemJobUserRel(Map reqMap);

    /***
    *
     * @param userId
     * @param projectId
    *@return {}
    *@throws
    *@Description: 大客户经理二级经纪人移动
    *@author FuYong
    *@date 2021/5/18 17:29
    */
    String saveBrokerAccountRecords(String userId,String projectId);

    /**
     * 保存用户-岗位
     *
     * @param reqMap
     * @return
     */
    Map saveUserJob(Map reqMap);

    /**
     * 查询所有的岗位
     *
     * @param map
     * @return
     */
    ResultBody selectJobsList(Map map);

    /**
     * 绑定项目
     *
     * @param bindProject
     * @return
     */
    ResultBody updateBindProject(BindProject bindProject);
    /**
     * @Author wanggang
     * @Description //初始化项目
     * @Date 15:29 2021/11/29
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateProjectNew(Map map);
    /**
     * @Author wanggang
     * @Description //判断是否系统管理员
     * @Date 14:44 2022/10/29
     * @Param []
     * @return java.lang.String
     **/
    String getIsSys();



    /**
     * @author liang
     * @date 2023/10/17 14:50
     * @description: 导出用户与岗位信息
     */
    public void   ExportUsersAnd   (HttpServletResponse response);
}
