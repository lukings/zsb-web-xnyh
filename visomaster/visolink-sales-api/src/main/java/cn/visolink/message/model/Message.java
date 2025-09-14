package cn.visolink.message.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 吴要光
 * @since 2019-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_message")
@ApiModel(value="Message对象", description="")
public class Message implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @TableField("Subject")
    private String Subject;

    @TableField("Content")
    private String Content;

    @TableField("Sender")
    private String Sender;

    @TableField("SendTime")
    private Date SendTime;

    @ApiModelProperty(value = "")
    @TableField("MessageType")
    private Integer MessageType;

    @ApiModelProperty(value = "0�� 1��")
    @TableField("IsDel")
    private Boolean IsDel;

    @TableField("BusinessId")
    private String BusinessId;

    @TableField("Receiver")
    private String Receiver;

    @ApiModelProperty(value = "0 �� 1��")
    @TableField("IsRead")
    private Boolean IsRead;

    @TableField("ReadTime")
    private Date ReadTime;

    @TableField("MsgUrl")
    private String MsgUrl;

    @TableField("IsPush")
    private Boolean IsPush;

    @TableField("IsNeedPush")
    private Boolean IsNeedPush;

    @ApiModelProperty(value = "案场机会表id")
    @TableField("OpportunityClueId")
    private String OpportunityClueId;

    @ApiModelProperty(value = "线索ID")
    @TableField("ProjectClueId")
    private String ProjectClueId;

    @ApiModelProperty(value = "项目ID")
    @TableField("ProjectID")
    private String ProjectID;

    @TableField("Ext1")
    private String Ext1;

    @TableField("Ext2")
    private String Ext2;

    @TableField("Ext3")
    private String Ext3;

    @TableField("Ext4")
    private String Ext4;


}
