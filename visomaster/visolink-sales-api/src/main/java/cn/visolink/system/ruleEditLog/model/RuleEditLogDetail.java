package cn.visolink.system.ruleEditLog.model;

import lombok.Data;

/**
 * @ClassName RuleEditLogDetail
 * @Author wanggang
 * @Description //参数修改详情
 * @Date 2020/12/18 15:28
 **/
@Data
public class RuleEditLogDetail {

    private String id;

    private String batchId;//修改参数批次ID

    private String param;//修改参数

    private String beforeEdit;//修改参数前值

    private String afterEdit;//修改参数后值

    private String creator;//修改人

    private String createTime;//修改时间

    private String projectId;//项目ID

}
