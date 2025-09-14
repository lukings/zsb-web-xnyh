package cn.visolink.system.enterprisedatabase.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业数据库临时数据表
 * @TableName ed_customer_info_te
 */
@Data
public class EdCustomerInfoTeVo implements Serializable {
    /**
     * ID
     */
    private String id;

    /**
     * 导入人ID
     */
    private String importUserId;

    /**
     * 导入人名称
     */
    private String importUserName;

    /**
     * 导入时间
     */
    private Date importTime;

    /**
     * 导入批次
     */
    private String importBatch;

    /**
     * 是否确认
     */
    private Integer isConfirm;

    /**
     * 数据ID
     */
    private String customerId;

    /**
     * 通用字段配置ID
     */
    private String commonFieldConfigurationId;

    /**
     * 通用字段编码
     */
    private String fieldCode;

    /**
     * 通用字段名称
     */
    private String fieldName;

    /**
     * 字段值
     */
    private String fieldValue;

    /**
     * 数据来源编码
     */
    private String dataSourcesCode;

    /**
     * 数据来源名称
     */
    private String dataSourcesName;

    /**
     * 数据查看权限(0 私密 1 公开)
     */
    private Integer examinePreType;

    /**
     * 过保时间
     */
    private String expireDate;

    private static final long serialVersionUID = 1L;
}
