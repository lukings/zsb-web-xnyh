package cn.visolink.system.projectmanager.model;

import cn.visolink.system.projectmanager.model.requestmodel.BaseModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/23 11:26
 * @description
 * @Version 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class PriceTask extends BaseModel implements Serializable {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 申请名称
     */
    private String applyName;

    /**
     * 申请日期
     */
    private String applyTime;

    /**
     * 状态 0：草稿 1：已执行
     */
    private Integer status;

    /**
     * 调整套数
     */
    private Integer adjustNum;

    /**
     * 调整前标准总价
     */
    private BigDecimal beforeBzTotalPrice;

    /**
     * 调整后标准总价
     */
    private BigDecimal afterBzTotalPrice;

    /**
     * 调整前建筑标准单价
     */
    private BigDecimal beforeBzBldPrice;

    /**
     * 调整前套内标准单价
     */
    private BigDecimal beforeBzTnPrice;

    /**
     * 调整后建筑标准单价
     */
    private BigDecimal afterBzBldPrice;

    /**
     * 调整后套内标准单价
     */
    private BigDecimal afterBzTnPrice;

    /**
     * 调整前低价总价
     */
    private BigDecimal beforeDjTotal;

    /**
     * 调整后低价总价
     */
    private BigDecimal afterDjTotal;

    /**
     * 调整前建筑低价单价
     */
    private BigDecimal beforeBldDjPrice;

    /**
     * 调整前套内低价单价
     */
    private BigDecimal beforeTnDjPrice;

    /**
     * 调整后套内低价单价
     */
    private BigDecimal afterDjPrice;

    /**
     * 调整后建筑低价单价
     */
    private BigDecimal afterBldDjPrice;

    /**
     * 计价方式 1:建筑面积 2:套内面积 3:套
     */
    private Integer valuationType;

    /**
     * 价格标准 1:以面价总价为准 2:以建筑单价为准 3:以套内单价为准 4:以底价总价为准
     */
    private Integer priceStandard;

    /**
     * 经办人
     */
    private String agent;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 创建人账号
     */
    private String createUser;

    /**
     * 修改人账号
     */
    private String updateUser;

    /**
     * 是否删除 0：正常；1：删除
     */
    private Integer isDelete;

    /**
     * 备注
     */
    private String remark;

    /**
     * 0:标准价录入 1:低价录入
     */
    private Integer type;

}

