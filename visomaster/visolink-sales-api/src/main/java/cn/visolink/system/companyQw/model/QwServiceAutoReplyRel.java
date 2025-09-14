package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName QwServiceAutoReplyRel
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/2/8 9:20
 **/
@Data
@ApiModel(value = "自动回复关联客服", description = "自动回复关联客服")
public class QwServiceAutoReplyRel {

    @ApiModelProperty(value = "规则ID")
    private String ruleId;

    @ApiModelProperty(value = "客服ID")
    private String openKfid;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "修改时间")
    private String editTime;
}
