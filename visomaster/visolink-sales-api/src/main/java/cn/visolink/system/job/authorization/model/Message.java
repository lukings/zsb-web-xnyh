package cn.visolink.system.job.authorization.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/9/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_message")
@ApiModel(value = "Message对象", description = "消息表")
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "主题")
    @TableId(value = "Subject")
    private String subject;

    @ApiModelProperty(value = "内容")
    @TableId(value = "Content")
    private String content;

    @ApiModelProperty(value = "发送人")
    @TableId(value = "Sender")
    private String sender;

    @ApiModelProperty(value = "发送时间")
    @TableId(value = "MessageType")
    private String sendTime;

    @ApiModelProperty(value = "消息类型")
    @TableId(value = "MessageType")
    private Integer messageType;

    @ApiModelProperty(value = "是否删除(0 否 1 是)")
    @TableId(value = "IsDel")
    private Integer isDel;

    @ApiModelProperty(value= "消息接收者")
    @TableId(value = "Receiver")
    private String receiver;

    @ApiModelProperty(value = "是否阅读(0 否 1 是)")
    @TableId(value = "IsRead")
    private Integer isRead;

    @ApiModelProperty(value = "阅读时间")
    @TableId(value = "ReadTime")
    private String readTime;

    @ApiModelProperty(value = "项目ID")
    @TableId(value = "ProjectID")
    private String projectId;

    @ApiModelProperty(value = "任务")
    @TableId(value = "BusinessId")
    private String businessId;

    @ApiModelProperty(value = "机会id")
    @TableId(value = "OpportunityClueId")
    private String opportunityClueId;

    @ApiModelProperty(value = "线索id")
    @TableId(value = "ProjectClueId")
    private String projectClueId;

    @ApiModelProperty(value = "是否推送")
    @TableId(value = "IsPush")
    private Integer isPush;

    @ApiModelProperty(value = "是否新推送")
    @TableId(value = "IsNeedPush")
    private Integer isNeedPush;

    @ApiModelProperty(value = "扩展字段")
    @TableId(value = "Ext2")
    private String ext2;

    @ApiModelProperty(value = "扩展字段")
    @TableId(value = "ext3")
    private String ext3;

}
