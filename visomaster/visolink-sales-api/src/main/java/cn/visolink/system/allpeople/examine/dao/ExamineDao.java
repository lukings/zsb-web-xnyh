package cn.visolink.system.allpeople.examine.dao;

import cn.visolink.system.allpeople.examine.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //审核
 * @Date 2020/1/14 19:19
 **/
public interface ExamineDao {

    List<Examine> getExamineList(Map map);

    void updatePeople(Map map);

    List<Map> getCitys();

    List<Examine> getBrokerUserList(Map map);

    List<Examine> getBrokerUserListExport(Map map);

    int getBrokerUserListCount(Map map);

    int getBrokerUserListByAllCount(Map map);

    int getBrokerUserListByProCount(Map map);

    int getBrokerUserListByRegCount(Map map);

    int getBrokerUserListByNRegCount(Map map);

    List<Examine> getBrokerUserListByAll(Map map);

    List<Examine> getBrokerUserListByPro(Map map);

    List<Examine> getBrokerUserListByReg(Map map);

    List<Examine> getBrokerUserListByNReg(Map map);

    List<Examine> getBrokerUserListByAllExcel(Map map);

    List<Examine> getBrokerUserListByProExcel(Map map);

    List<Examine> getBrokerUserListByRegExcel(Map map);

    List<Examine> getBrokerUserListByNRegExcel(Map map);

    Examine getBrokerUser(Map paramMap);

    List<Customer> getBrokerUserCustomer(Map paramMap);

    List<UserEdit> getBrokerUserEditLog(Map paramMap);

    List<Map> getProjectList(Map paramMap);

    List<Map> getAllProject();

    Map getExamineByOpenId(@Param("OpenId") String OpenId);

    Map getExamineByHBID(@Param("OpenId") String OpenId);

    String getNewsById(String id);

    String getBuildBookById(String id);

    String getCityById(String id);

    String getActivityById(String id);

    String getActivityPosterById(String id);

    String getBuildBookPosterById(String id);
    /**
     * @Author wanggang
     * @Description //获取落地页(户型)
     * @Date 20:21 2020/12/16
     * @Param [id]
     * @return java.lang.String
     **/
    String getBuildHouseById(String id);

    String getCardPosterById(String id);

    String getUserNameByCardId(String id);

    List<Map> getExamineListByOpenId(@Param("openIds") List<String> openIds);

    List<Map> getUserCardList(String brokerId);

    String getDKHActivityById(String id);

    String getLPDTById(String id);
    /**
     * @Author wanggang
     * @Description //查询项目对应区域
     * @Date 17:51 2020/12/16
     * @Param [proIds]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getOrgIdsByPros(@Param("proIds") List<String> proIds);
    /**
     * @Author wanggang
     * @Description //获取落地页名称
     * @Date 19:34 2020/12/16
     * @Param [id]
     * @return java.lang.String
     **/
    String getExtenJumpTo(String id);
    /**
     * @Author wanggang
     * @Description //加入黑名单
     * @Date 16:21 2022/4/13
     * @Param [id]
     * @return void
     **/
    void addblacklist(@Param("id") String id,@Param("type") String type);
    /**
     * @Author wanggang
     * @Description //审批渠道商
     * @Date 16:32 2022/4/13
     * @Param [paramMap]
     * @return void
     **/
    void channelAudit(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取审批渠道商列表
     * @Date 16:46 2022/4/13
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.allpeople.examine.model.ChannelRegistration>
     **/
    List<ChannelRegistration> channelRegistration(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取签约渠道商列表
     * @Date 16:47 2022/4/13
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.allpeople.examine.model.ChannelRegistration>
     **/
    List<ChannelRegistration> channelManagement(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取签约渠道商
     * @Date 16:57 2022/4/13
     * @Param [paramMap]
     * @return cn.visolink.system.allpeople.examine.model.ChannelRegistration
     **/
    ChannelRegistration channelDetail1(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取审批渠道商
     * @Date 16:57 2022/4/13
     * @Param [paramMap]
     * @return cn.visolink.system.allpeople.examine.model.ChannelRegistration
     **/
    ChannelRegistration channelDetail2(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取渠道商合同
     * @Date 16:58 2022/4/13
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.allpeople.examine.model.ChannelContract>
     **/
    List<ChannelContract> getChannelContracts(Map paramMap);
}
