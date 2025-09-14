package cn.visolink.system.openQuotation.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderNode
 * @Author wanggang
 * @Description //订单节点
 * @Date 2021/1/13 14:02
 **/
@Data
public class OrderNode implements Serializable {

    private String id;

    private String orderNo;//订单编号

    private String nodeType;//节点类型

    private String nodeTypeName;//节点类型描述

    private String reason;//关闭原因

    private String recordTime;//操作时间

    private String bindPerson;

    private String unbindPerson;

    private String unbindReason;

    private String mobile;
}
