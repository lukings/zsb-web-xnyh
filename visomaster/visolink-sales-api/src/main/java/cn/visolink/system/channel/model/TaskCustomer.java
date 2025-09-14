package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务客户关系表
 * @TableName b_task_customer
 */
@Data
public class TaskCustomer implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 客户id
     */
    private String customerId;
    /**
     * 客户名称
     */
    private String customerName;
    
    /**
     * 客户手机号
     */
    private String customerMobile;

    /**
     * 客户类型（任务下发、手动录入）
     */
    private String customerType;

    /**
     * 是否删除：0 未删除、1 已删除
     */
    private Integer isDel;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String modifyBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date modifyTime;
    
    /**
     * 是否删除：0 不重复、1 重复
     */
    private Integer isRepeat;

    private static final long serialVersionUID = 1L;
}