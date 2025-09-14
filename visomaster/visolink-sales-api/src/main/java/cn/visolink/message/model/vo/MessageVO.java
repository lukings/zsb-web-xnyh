package cn.visolink.message.model.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import lombok.Data;
import java.io.Serializable;
/**
 * <p>
 * MessageVO对象
 * </p>
 *
 * @author 吴要光
 * @since 2019-09-03
 */
@Data
@ApiModel(value="Message对象", description="")
public class MessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

                                            private String id;

                private String Subject;

                private String Content;

                private String Sender;

                private Date SendTime;

        @ApiModelProperty(value = "")
                private Integer MessageType;

        @ApiModelProperty(value = "0�� 1��")
                private Boolean IsDel;

                private String BusinessId;

                private String Receiver;

        @ApiModelProperty(value = "0 �� 1��")
                private Boolean IsRead;

                private Date ReadTime;

                private String MsgUrl;

                private Boolean IsPush;

                private Boolean IsNeedPush;

        @ApiModelProperty(value = "案场机会表id")
                private String OpportunityClueId;

        @ApiModelProperty(value = "线索ID")
                private String ProjectClueId;

        @ApiModelProperty(value = "项目ID")
                private String ProjectID;

                private String Ext1;

                private String Ext2;

                private String Ext3;

                private String Ext4;


}