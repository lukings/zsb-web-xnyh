package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName QwCstTag
 * @Author wanggang
 * @Description //客户标签
 * @Date 2022/1/20 9:36
 **/
@Data
@ApiModel(value = "客户标签", description = "客户标签")
public class QwCstTag {

    @ApiModelProperty(value = "1：新增 2：修改")
    private String addOrEdit;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "标签ID")
    private String tagId;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "标签类型 1-企业设置，2-用户自定义，3-规则组标签")
    private String type;

    @ApiModelProperty(value = "标签组ID")
    private String groupId;

    @ApiModelProperty(value = "标签组名称")
    private String groupName;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "修改时间")
    private String editTime;

}
