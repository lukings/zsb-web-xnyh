package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ChannelCode
 * @Author wanggang
 * @Description //渠道码
 * @Date 2022/1/7 10:16
 **/
@Data
@ApiModel(value = "渠道码", description = "渠道码")
public class ChannelCode {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "1：新增 2：修改")
    private String addOrEdit;

    @ApiModelProperty(value = "场景，1-在小程序中联系，2-通过二维码联系")
    private String scene;

    @ApiModelProperty(value = "联系方式类型 1-单人, 2-多人")
    private String type;

    @ApiModelProperty(value = "二维码备注")
    private String remark;

    @ApiModelProperty(value = "外部客户添加时是否无需验证（1：是 0：否）")
    private String skipVerify;

    @ApiModelProperty(value = "渠道")
    private String state;

    @ApiModelProperty(value = "部门ID")
    private String party;

    @ApiModelProperty(value = "配置ID")
    private String configId;

    @ApiModelProperty(value = "二维码路径")
    private String qrCode;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "修改时间")
    private String editTime;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "是否自动添加昵称（1：是 0：否）")
    private String isAutoEditname;

    @ApiModelProperty(value = "是否自动添加客户描述（1：是 0：否）")
    private String isAutoAddRemark;

    @ApiModelProperty(value = "客户描述")
    private String description;

    @ApiModelProperty(value = "是否自动添加客户标签（1：是 0：否）")
    private String isAutoAddTag;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "标签ID")
    private String tagId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "是否添加欢迎语（1：是 0：否）")
    private String isAddWelcomeWords;

    @ApiModelProperty(value = "图片集合")
    private List<MediaDetail> photoList;

    @ApiModelProperty(value = "视频集合")
    private List<MediaDetail> videoList;

    @ApiModelProperty(value = "h5链接集合")
    private List<MediaDetail> h5List;

    @ApiModelProperty(value = "小程序集合")
    private List<MediaDetail> appletList;

    @ApiModelProperty(value = "文件集合")
    private List<MediaDetail> fileList;

    @ApiModelProperty(value = "文本集合")
    private List<MediaDetail> textList;

    @ApiModelProperty(value = "成员ID集合")
    private List<String> userList;
}
