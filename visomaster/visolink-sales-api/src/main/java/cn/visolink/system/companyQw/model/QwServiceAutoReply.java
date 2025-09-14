package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName QwServiceAutoReply
 * @Author wanggang
 * @Description //客服自动回复规则
 * @Date 2022/2/8 9:01
 **/
@Data
@ApiModel(value = "客服自动回复规则", description = "客服自动回复规则")
public class QwServiceAutoReply {

    @ApiModelProperty(value = "规则ID")
    private String id;

    @ApiModelProperty(value = "项目ID集合")
    private List<String> projectIdList;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "新增或修改（1：新增 2：修改）")
    private String addOrEdit;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "修改时间")
    private String editTime;

    @ApiModelProperty(value = "关键词逗号隔开")
    private String keyWords;

    @ApiModelProperty(value = "关联客服ID集合")
    private List<String> relList;

    @ApiModelProperty(value = "关键词集合")
    private List<QwServiceAutoReplyKeyWord> keyWordList;

    @ApiModelProperty(value = "图片集合")
    private List<QwServiceAutoReplyMedia> photoList;

    @ApiModelProperty(value = "视频集合")
    private List<QwServiceAutoReplyMedia> videoList;

    @ApiModelProperty(value = "h5链接集合")
    private List<QwServiceAutoReplyMedia> h5List;

    @ApiModelProperty(value = "小程序集合")
    private List<QwServiceAutoReplyMedia> appletList;

    @ApiModelProperty(value = "文件集合")
    private List<QwServiceAutoReplyMedia> fileList;

    @ApiModelProperty(value = "文本集合")
    private List<QwServiceAutoReplyMedia> textList;
}
