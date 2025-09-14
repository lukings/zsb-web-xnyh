package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;
import java.io.Serializable;
import java.util.List;

/**
 * 群发任务表(BQwMassTexting)实体类
 *
 * @author makejava
 * @since 2022-01-05 16:59:05
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "群发任务", description = "群发任务")
public class BQwMassTexting implements Serializable {
    private static final long serialVersionUID = 557403378314848639L;
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "群发时间")
    private String sendTime;

    @ApiModelProperty(value = "成员ids")
    private List<String>  senderList;

    @ApiModelProperty(value = "成员ids")
    private String senders;

    @ApiModelProperty(value = "成员姓名")
    private String senderNames;

    @ApiModelProperty(value = "0：群发到客户；1：群发到客户群")
    private String sendType;

    @ApiModelProperty(value = "1：全部客户；2：按条件筛选")
    private String cstType;

    @ApiModelProperty(value = "客户标签")
    private List<String> flagIdList;

    @ApiModelProperty(value = "客户标签")
    private String flagType;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "创建人员")
    private String creater;

    @ApiModelProperty(value = "修改人员")
    private String updater;

    @ApiModelProperty(value = "是否发起群发，0未发起，1发起")
    private String isStart;

    @ApiModelProperty(value = "是否启用（1：启用 2：禁用（撤回））")
    private String status;

    @ApiModelProperty(value = "客户id")
    private String colClientId;

    @ApiModelProperty(value = "客户姓名")
    private String colClientNames;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "客户状态（0：未关联 1：报备 2：到访 3：排卡 7：认购 8：签约 ）")
    private List<String> cstStatus;

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

}
