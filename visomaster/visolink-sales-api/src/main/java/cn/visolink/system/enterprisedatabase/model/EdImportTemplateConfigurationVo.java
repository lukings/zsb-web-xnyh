package cn.visolink.system.enterprisedatabase.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 企业数据库导入模板配置表
 * @TableName ed_import_template_configuration
 */
@Data
public class EdImportTemplateConfigurationVo implements Serializable {
    /**
     * ID
     */
    private String id;

    /**
     * 父ID
     */
    private String pid;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 数据来源编码
     */
    private String dataSourcesCode;

    /**
     * 数据来源名称
     */
    private String dataSourcesName;

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
     * 对应列内容
     */
    private String fieldValue;

    /**
     * 模板起始行
     */
    private Integer templateStartingLine;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否删除
     */
    private Integer isdel;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updator;

    /**
     * 子级数据
     */
    private List<EdImportTemplateConfigurationVo> children;

    /**
     * 是否为通讯字段
     */
    private String isCommunicationField;

    /**
     * 关键词
     */
    private String keyWord;

    private String pageNum;

    private String pageSize;

    private static final long serialVersionUID = 1L;
}
