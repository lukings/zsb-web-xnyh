package cn.visolink.system.channel.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "ProjectProtectRule对象", description = "")
public class RuleList extends Page {

    /*用来接收规则的实体类*/
    private ProjectProtectRuleForm one;
    private List<ProjectProtectRuleForm> two;
    private String entrance;
    private String modifyType;
    private String projectId;
    private String userId;
    private Integer standbyModeStandbyMode;
    private Integer isRep;
    private Integer idyVerification;
    private Integer isEnterPublicPool;
    private String isPrintStatus;
    private String AuthCompanyID;

}
