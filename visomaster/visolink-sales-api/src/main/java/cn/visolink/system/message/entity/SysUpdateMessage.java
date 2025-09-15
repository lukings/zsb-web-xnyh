package cn.visolink.system.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统更新消息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("b_sysupdate_message")
@ApiModel(value = "系统更新消息", description = "系统更新消息实体")
public class SysUpdateMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "消息标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "消息内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "消息类型：1-系统更新，2-维护通知，3-功能公告")
    @TableField("message_type")
    private Integer messageType;

    @ApiModelProperty(value = "目标用户类型：1-全部用户，2-指定用户")
    @TableField("target_type")
    private Integer targetType;

    @ApiModelProperty(value = "目标用户ID列表，逗号分隔")
    @TableField("target_users")
    private String targetUsers;

    @ApiModelProperty(value = "是否弹框提醒：0-否，1-是")
    @TableField("is_popup")
    private Integer isPopup;

    @ApiModelProperty(value = "失效时间")
    @TableField("expire_time")
    private Date expireTime;

    @ApiModelProperty(value = "状态：0-禁用，1-启用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "创建人ID")
    @TableField("creator")
    private String creator;

    @ApiModelProperty(value = "创建人姓名")
    @TableField("creator_name")
    private String creatorName;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新人ID")
    @TableField("updator")
    private String updator;

    @ApiModelProperty(value = "更新人姓名")
    @TableField("updator_name")
    private String updatorName;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;

    @ApiModelProperty(value = "是否删除：0-否，1-是")
    @TableField("is_deleted")
    private Integer isDeleted;
}
