package cn.visolink.system.enterprisedatabase.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 企业数据库客户表
 * @TableName ed_customer_info
 */
@Data
public class EdCustomerInfoVo implements Serializable {
    /**
     * ID
     */
    private String id;

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

    /**
     * 导入人
     */
    private String importUser;

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

    /**
     * esid
     */
    private String docId;

    /**
     * 排序号
     */
    private Integer listIndex;

    /**
     * 关键词
     */
    private Map keyWord;
    private List<String> fieldList;

    private String pageNum;

    private String pageSize;

    private static final long serialVersionUID = 1L;
}
