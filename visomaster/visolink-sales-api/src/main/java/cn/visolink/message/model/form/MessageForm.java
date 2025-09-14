package cn.visolink.message.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Data;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>
 *
 * </p>MessageForm对象
 *
 * @author 吴要光
 * @since 2019-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Message对象", description = "")
public class MessageForm extends Page {

    private static final long serialVersionUID = 1L;

    private String id;

    private String subject;

    private String content;

    private String sender;

    private String senderName;

    private String sendTime;

    @ApiModelProperty(value = "")
    private Integer messageType;

    @ApiModelProperty(value = "0�� 1��")
    private Integer isDel;

    private String businessId;

    private String receiver;

    @ApiModelProperty(value = "0 �� 1��")
    private String isRead;

    private String readTime;

    private String msgUrl;

    private String isPush;

    private Integer isNeedPush;

    @ApiModelProperty(value = "案场机会表id")
    private String opportunityClueId;

    @ApiModelProperty(value = "线索ID")
    private String projectClueId;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    private String tradeGuId;

    private String ext2;

    private String ext3;

    private String ext4;

    private String messageData;

    private String cstType;


}
