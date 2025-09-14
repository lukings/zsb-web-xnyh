package cn.visolink.system.brokerrule.mapper;

import cn.visolink.system.brokerrule.model.form.BrokerRuleForm;
import cn.visolink.system.brokerrule.model.vo.BrokerRuleVO;
import cn.visolink.system.channel.model.form.ProjectProtectRuleForm;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/2/3
 */
@Mapper
public interface BrokerRuleMapper {

    /**
     * 查询全民经纪人保护规则
     *
     * @param brokerRuleForm
     * @return
     */
    Page<BrokerRuleVO> getBrokerRuleList(BrokerRuleForm brokerRuleForm);

    /**
     * 查询全民经纪人规则详情
     *
     * @param activityId
     * @return
     */
    List<BrokerRuleVO> getBrokerRuleDetails(@Param("activityId") String activityId);

    /**
     * 根据项目查询全民经纪人规则详情
     *
     * @param projectId
     * @return
     */
    List<BrokerRuleVO> getBrokerRuleDetailsByProId(@Param("projectId") String projectId);


    /**
     * 新增全民经纪人规则
     *
     * @param brokerRuleForm
     * @return
     */
    int insertBrokerRule(@Param("brokerRuleList") List<BrokerRuleForm> brokerRuleForm);

    /**
     * 修改全民经纪人规则
     *
     * @param brokerRuleForm
     * @return
     */
    int updateBrokerRule(@Param("brokerRuleList") List<BrokerRuleForm> brokerRuleForm);

    /**
     * 查询项目是否有启动的活动
     *
     * @param projectId
     * @return
     */
    String getBrokerRuleIsEnableD(@Param("projectId") String projectId, @Param("enableDate") String enableDate, @Param("endDate") String endDate);

    /**
     * 查询项目是否有启动的活动
     *
     * @param projectId
     * @return
     */
    String getBrokerRuleIsEnable(@Param("projectId") String projectId);
    /**
     * 更新楼盘信息
     *
     * @param map
     * @return
     */
    int updateBuildBookIsReport(Map map);

    /**
     * 查询规则表中是否有数据
     *
     * @param projectId
     * @return
     */
    String getProjectRuleId(@Param("projectId") String projectId);

    /**
     * 新增规则表数据
     *
     * @param projectProtectRuleForm
     * @return
     */
    int insertProtectRule(ProjectProtectRuleForm projectProtectRuleForm);

    /**
     * 是否规则启动
     * @param id
     * @return
     */
    Integer getIsProjectRule(@Param("id") String id);

    /**
     * @param activityId
     */
    void delBrokerRule(@Param("activityId") String activityId);

    /**
     * @Author wanggang
     * @Description //查询是否存在楼盘
     * @Date 17:07 2020/3/4
     * @Param
     * @return
     **/
    int getBuildBookbyPro(@Param("projectId") String projectId);

    /**
     * @param map
     * 禁用规则
     */
    void disabledBrokerRule(Map map);

    /**
     * 更新项目咨询电话
     * @param map
     * @return
     */
    int updateProjectHotLine(Map<String, Object> map);
    /**
     * @Author wanggang
     * @Description //启用规则
     * @Date 14:13 2021/6/2
     * @Param [map]
     * @return void
     **/
    void enableBrokerRule(Map map);
}
