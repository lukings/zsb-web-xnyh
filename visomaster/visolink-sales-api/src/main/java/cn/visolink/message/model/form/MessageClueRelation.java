package cn.visolink.message.model.form;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/9/23
 */
@Data
public class MessageClueRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String messageId;

    private String projectClueId;

    private String opportunityClueId;

    private String brokerId;

    private String remarks;
}
