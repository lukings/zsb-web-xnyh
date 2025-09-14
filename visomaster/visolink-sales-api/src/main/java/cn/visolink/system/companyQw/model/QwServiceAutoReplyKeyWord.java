package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName QwServiceAutoReplyKeyWord
 * @Author wanggang
 * @Description //客服自动回复关键字
 * @Date 2022/2/8 9:16
 **/
@Data
@ApiModel(value = "客服自动回复关键字", description = "客服自动回复关键字")
public class QwServiceAutoReplyKeyWord {

    @ApiModelProperty(value = "规则ID")
    private String ruleId;

    @ApiModelProperty(value = "关键字")
    private String keyWord;

    @ApiModelProperty(value = "匹配类型（1：全匹配 2：半匹配）")
    private String matchingType;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "修改时间")
    private String editTime;
}
