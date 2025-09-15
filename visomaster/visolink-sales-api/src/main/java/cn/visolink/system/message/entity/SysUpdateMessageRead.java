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
 * 系统更新消息已读记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("b_sysupdate_message_read")
@ApiModel(value = "系统更新消息已读记录", description = "系统更新消息已读记录实体")
public class SysUpdateMessageRead implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "消息ID")
    @TableField("message_id")
    private String messageId;

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty(value = "用户姓名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "是否已读：0-未读，1-已读")
    @TableField("is_read")
    private Integer isRead;

    @ApiModelProperty(value = "阅读时间")
    @TableField("read_time")
    private Date readTime;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private Date updateTime;
}
