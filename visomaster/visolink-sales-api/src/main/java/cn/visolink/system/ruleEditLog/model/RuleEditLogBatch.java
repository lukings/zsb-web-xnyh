package cn.visolink.system.ruleEditLog.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "参数修改批次对象", description = "")
public class RuleEditLogBatch extends Page {

    private static final long serialVersionUID = 1L;

    private String id;

    private String ruleType;//规则类型

    private String ruleTypeCode;//规则类型Code

    private String projectId;//项目ID

    private String brokerRuleId;//经纪人规则ID

    private String editParams;//修改参数

    private String editType;//修改分类（1：渠道参数 2：案场参数 3：经纪人参数）

    private String creator;//修改人

    private String createTime;//修改时间

    private List<String> enclosures;//附件

    private String enclosureList;//附件逗号分隔
}
