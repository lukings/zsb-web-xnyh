package cn.visolink.system.enterprisedatabase.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 企业数据库通用字段配置表
 * @TableName ed_common_field_configuration
 */
@Data
public class EdCommonFieldConfigurationVo implements Serializable {
    /**
     * ID
     */
    private String id;

    /**
     * 父ID
     */
    private String pid;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 对应招商宝字段编码
     */
    private String fieldToZsbCode;

    /**
     * 对应招商宝字段名称
     */
    private String fieldToZsbName;

    /**
     * 排序号
     */
    private Integer listIndex;

    /**
     * 数据重复时处理方式
     */
    private String processingMethodForDuplicateData;
    private String processingMethodForDuplicateDataValue;

    /**
     * 是否展示
     */
    private String isShow;
    private String isShowValue;

    /**
     * 是否必填
     */
    private String isRequired;
    private String isRequiredValue;

    /**
     * 是否为判重字段
     */
    private String isCriticalField;
    private String isCriticalFieldValue;

    /**
     * 是否为通讯字段
     */
    private String isCommunicationField;
    private String isCommunicationFieldValue;

    /**
     * 是否可以操作
     */
    private String isReadOnly;

    /**
     * 字段说明
     */
    private String fieldDescription;

    /**
     * 显示类型（1文本 2数值 3时间 4下拉单选）
     */
    private String showType;
    private String showTypeValue;

    /**
     * 参数默认值
     */
    private String paramDefaults;

    /**
     * 小数位
     */
    private Integer decimalPlaces;

    /**
     * 单位
     */
    private String unit;

    /**
     * 最小值
     */
    private String minVal;

    /**
     * 最大值
     */
    private String maxVal;

    /**
     * 存储模式（1：仅文本2：文本+值）
     */
    private Integer storageMode;

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

    private Integer index;

    /**
     * 字段类型
     */
    private String fieldTypeCode;

    /**
     * 字段类型名称
     */
    private String fieldTypeName;


    /**
     * 字段配置的默认值选项
     */
    private List<EdCommonFieldConfigurationVo> defaultValueList;
    private List<Map> defaultValueListMap;

    private static final long serialVersionUID = 1L;

}
