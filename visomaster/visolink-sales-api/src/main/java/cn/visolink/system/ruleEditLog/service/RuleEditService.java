package cn.visolink.system.ruleEditLog.service;

import cn.visolink.system.ruleEditLog.model.RuleEditLogBatch;
import cn.visolink.system.ruleEditLog.model.RuleEditLogDetail;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface RuleEditService {

    Map batchParamQuery(Map<String, Object> map);

    PageInfo<RuleEditLogBatch> batchQuery(Map<String, Object> map);

    List<RuleEditLogDetail> batchDetailQuery(Map<String, Object> map);

}
