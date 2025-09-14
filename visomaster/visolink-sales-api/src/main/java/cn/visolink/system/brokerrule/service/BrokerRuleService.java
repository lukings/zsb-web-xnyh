package cn.visolink.system.brokerrule.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.brokerrule.model.form.BrokerRuleForm;
import cn.visolink.system.brokerrule.model.form.BrokerRuleList;
import cn.visolink.system.brokerrule.model.vo.BrokerRuleVO;

import java.util.List;
import java.util.Map;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/2/3
 */
public interface BrokerRuleService {

    /**
     * 查询全民经纪人规则
     * @param brokerRuleForm
     * @return
     */
    ResultBody getBrokerRuleList(BrokerRuleForm brokerRuleForm);

    /**
     * 查询全民经纪人规则
     * @param map
     * @return
     */
    List<BrokerRuleVO> getBrokerRuleDetails(Map map);

    /**
     * 根据项目查询全民经纪人规则
     * @param map
     * @return
     */
    List<BrokerRuleVO> getBrokerRuleDetailsByProId(Map map);

    /**
     * 保存全民经纪人规则
     * @param brokerRuleList
     * @return
     */
    Map saveBrokerRule(BrokerRuleList brokerRuleList);

    /**
     * 查询项目是否有启动的活动
     * @param map
     * @return
     */
    Map getBrokerRuleIsEnable(Map map);

    /**
     * 删除活动规则
     * @param activityId
     */
    void delBrokerRule(String activityId);

    /**
     * @param map
     * 禁用规则
     */
    void disabledBrokerRule(Map map);
    /**
     * @param map
     * 启用规则
     */
    ResultBody enableBrokerRule(Map map);
}
