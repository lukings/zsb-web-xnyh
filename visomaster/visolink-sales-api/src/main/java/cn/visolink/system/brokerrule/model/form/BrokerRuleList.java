package cn.visolink.system.brokerrule.model.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/2/3
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "ProjectProtectRule对象", description = "")
public class BrokerRuleList {

    List<BrokerRuleForm> brokerRuleFormList;
    private String userId;
    private String userName;
    private String operateType;
    private String projectId;
    private String projectArea;
    private String projectName;
    /**
     * 项目咨询电话
     */
    private String hotLine;
}
