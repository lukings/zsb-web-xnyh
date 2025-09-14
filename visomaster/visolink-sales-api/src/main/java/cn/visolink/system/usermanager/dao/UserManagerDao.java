package cn.visolink.system.usermanager.dao;

import cn.visolink.system.companyQw.model.QuitUserCst;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wjc
 * @date 2019/09/11
 */
@Repository
public interface UserManagerDao {

    /**
     * 获取用户信息总数
     * @return
     */
    Integer findMessageCount(Map map);
    /**
     * 获取用户信息
     *
     * @param map
     * @return
     */
    List<Map> findMessage(Map map);
    /**
     * 禁用/启用  用户的账号
     *
     * @param map
     * @return
     */
    int updateUserStatus(Map map);
    /**
     * 移除用户
     *
     * @param map
     * @return
     */
    int deleteUser(Map map);

    int updateUser(Map map);
    int updateUserJob(Map map);

    Map getUserJob(String id);

    /**
     * 查询用户是否存在
     *
     * @param map
     * @return
     */
    Map<String,Object> userNameExists(Map map);

    /**
     * 从C_User表查询用户数据
     *
     * @param map
     * @return
     */
    Map<String,Object> getUserFromCuser(Map map);
    /**
     *岗位人员新增
     *
     * @param map
     * @return
     */
    int insertSystemJobUser(Map map);
    /**
     *岗位人员新增
     *
     * @param map
     * @return
     */
    int insertSystemJobUsersRel(Map map);
    /**
     *excel导出测试
     *
     * @param map
     * @return
     */
   Map<String,Object> userComomJobCodeByJobId(Map map);
    /**
     *excel导出测试
     *
     * @param map
     * @return
     */
   Map<String,Object> userProxyRegisterByUserId(Map map);
    /**
     *excel导出测试
     *
     * @param map
     * @return
     */
    List<Map> userProxyRegisterInvitationCode(Map map);

    /**
     *excel导出测试
     *
     * @param map
     * @return
     */
    List<Map> saleAccountLogInsert(Map map);
    /**
     *人员信息更新
     *
     * @param map
     * @return
     */
    int systemUserUpdate(Map map);

    /**
     *人员信息更新
     *
     * @param map
     * @return
     */
    int systemUserUpdateTwo(Map map);
    /**
     *添加操作日志列表
     *
     * @param map
     * @return
     */
    int systemLogInsert(Map map);

    /**
     * 更新用户信息
     * @param map
     * @return
     */
    int modifySystemUser(Map map);

    /**
     * 更新用户名片信息
     * @param map
     * @return
     */
    int updateCardByAccountId(Map map);

    /**
     * 获取同步开始时间
     * @param TaskName
     * @return
     */
    Date getSynStartTime(String TaskName);

    /**
     * 获取同步结束时间
     * @param s
     * @return
     */
    Date getSynExecutTime(String s);

    /**
     * 查询全民经纪人账号
     * @param id
     * @return
     */
    String getBrokerId(@Param("id") String id);

    /**
     * 解除全民经纪人绑定
     * @param id
     * @return
     */
    void updateBroker(@Param("id") String id);

    /**
     * 新增消息
     * @param map
     * @return
     */
    int insertMessage(Map map);
    /**
     * @Author wanggang
     * @Description //查询岗位是否对接了第三方
     * @Date 10:54 2021/3/24
     * @Param [relId]
     * @return java.util.Map
     **/
    Map getJobSales(@Param("relId") String relId,@Param("thirdParty") String thirdParty);
    /**
     * @Author wanggang
     * @Description //查询账号是否对接了第三方
     * @Date 11:09 2021/3/24
     * @Param [userId, thirdParty]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getSalesThirdPro(@Param("userId") String userId,@Param("thirdParty") String thirdParty);
    /**
     * @Author wanggang
     * @Description //更新第三方账号信息
     * @Date 11:21 2021/3/24
     * @Param [map]
     * @return void
     **/
    void updateSalesThird(Map map);
    /**
     * @Author wanggang
     * @Description //获取成员ID
     * @Date 16:49 2022/1/18
     * @Param [id]
     * @return java.lang.String
     **/
    String getQwUserId(String id);
    /**
     * @Author wanggang
     * @Description //获取成员客户
     * @Date 17:13 2022/1/18
     * @Param [userid]
     * @return java.util.List<cn.visolink.system.companyQw.model.QuitUserCst>
     **/
    List<QuitUserCst> getQwUserCst(String userid);
    /**
     * @Author wanggang
     * @Description //保存离职客户
     * @Date 17:14 2022/1/18
     * @Param [cstList]
     * @return void
     **/
    void addQuitUserCst(@Param("list") List<QuitUserCst> cstList);
    /**
     * @Author wanggang
     * @Description //更新成员状态，解除案场客户绑定
     * @Date 17:15 2022/1/18
     * @Param [userid]
     * @return void
     **/
    void delQwUser(String userid);
}
