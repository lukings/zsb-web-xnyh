package cn.visolink.system.print.model;

import lombok.Data;

/**
 * @ClassName PrintField
 * @Author wanggang
 * @Description //打印模板字段
 * @Date 2020/3/9 10:51
 **/
@Data
public class PrintField {

    private String ID;
    /**
     *字段名称
     */
    private String FieldName;
    /**
     *字段替换标识
     */
    private String FieldValue;
    /**
     *字段类型
     */
    private String FieldType;
    /**
     *是否删除
     */
    private String IsDel;

    /**
     * 创建时间
     */
    private String CreateTime;

}
