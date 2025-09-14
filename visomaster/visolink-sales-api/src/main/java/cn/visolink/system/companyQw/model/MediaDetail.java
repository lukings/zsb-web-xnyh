package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MediaDetail
 * @Author wanggang
 * @Description //素材详情
 * @Date 2021/12/31 13:45
 **/
@Data
@ApiModel(value = "素材详情", description = "素材详情")
public class MediaDetail implements Serializable {

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

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "客户联系ID")
    private String contactId;

    @ApiModelProperty(value = "任务ID")
    private String taskId;

    @ApiModelProperty(value = "h5链接封面图路径")
    private String picUrl;
}
