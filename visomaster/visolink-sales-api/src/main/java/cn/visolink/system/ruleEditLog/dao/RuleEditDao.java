package cn.visolink.system.ruleEditLog.dao;

import cn.visolink.system.ruleEditLog.model.RuleEditLogBatch;
import cn.visolink.system.ruleEditLog.model.RuleEditLogDetail;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/12/23 14:17
 **/
public interface RuleEditDao {

    void addRuleEditBatch(RuleEditLogBatch ruleEditLogBatch);

    void addRuleEditLogBetails(List<RuleEditLogDetail> list);

    List<String> getSearchParams(Map<String, Object> map);

    List<Map> getSearchTypeParam(Map<String, Object> map);

    List<RuleEditLogBatch> getRuleEditBatch(Map<String, Object> map);

    List<RuleEditLogDetail> getRuleEditLogDetails(Map<String, Object> map);
}
