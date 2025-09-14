package cn.visolink.system.enterprisedatabase.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 企业数据库导入日志表
 * @TableName ed_import_customer_log
 */
@Data
public class EdImportCustomerLogVo implements Serializable {
    /**
     * ID
     */
    private String id;

    /**
     * 操作人ID
     */
    private String confirmId;

    /**
     * 操作人名称
     */
    private String confirmName;

    /**
     * 操作时间
     */
    private String confirmTime;

    /**
     * 导入批次
     */
    private String importBatch;

    /**
     * 导入结果
     */
    private String importResult;

    /**
     * 失效时间
     */
    private String invalidTime;

    /**
     * 是否同步
     */
    private String isSynTo;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否删除
     */
    private Integer isdel;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 修改人
     */
    private String updator;

    private String pageNum;

    private String pageSize;

    private static final long serialVersionUID = 1L;
}
