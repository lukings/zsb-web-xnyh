package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SensitiveWordVo
 * @Author wanggang
 * @Description //关键词
 * @Date 2022/1/4 10:38
 **/
@Data
@ApiModel(value = "关键词", description = "关键词")
public class SensitiveWordVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "项目ID逗号隔开")
    private String projectIds;

    @ApiModelProperty(value = "新增或修改（1：新增 2：修改）")
    private String addOrEdit;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "敏感词逗号隔开")
    private String wordList;

    @ApiModelProperty(value = "拦截方式 1:警告并拦截发送；2:仅发警告")
    private String interceptType;

    @ApiModelProperty(value = "敏感词分类ID")
    private String typeId;

    @ApiModelProperty(value = "敏感词分类名称")
    private String typeName;

    @ApiModelProperty(value = "规则ID")
    private String ruleId;

    @ApiModelProperty(value = "项目名称")
    private String projectNames;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "修改时间")
    private String editTime;
}
