package cn.visolink.system.channel.model.form;

import cn.visolink.system.channel.model.ProjectRuleDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ProjectRuleForm
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/6 14:13
 **/
@ApiModel(value="项目规则辅助类", description="项目规则辅助类")
@Data
public class ProjectRuleForm {

    @ApiModelProperty(value = "项目ID")
    private String projectID;

    @ApiModelProperty(value = "项目规则")
    private List<ProjectRuleDetail> projectRuleDetails;
}
