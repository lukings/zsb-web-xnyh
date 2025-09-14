package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName QwServiceAutoReplyMedia
 * @Author wanggang
 * @Description //自动回复素材
 * @Date 2022/2/8 9:24
 **/
@Data
@ApiModel(value = "自动回复素材", description = "自动回复素材")
public class QwServiceAutoReplyMedia {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "media_id")
    private String mediaId;

    @ApiModelProperty(value = "媒体附件路径")
    private String mediaUrl;

    @ApiModelProperty(value = "小程序appid")
    private String appid;

    @ApiModelProperty(value = "小程序page路径")
    private String page;

    @ApiModelProperty(value = "素材类型(1：图片 2：视频 3：小程序 4：h5  5：文件 6:文本)")
    private String mediaType;

    @ApiModelProperty(value = "规则ID")
    private String ruleId;

    @ApiModelProperty(value = "h5链接封面图路径")
    private String picUrl;

}
