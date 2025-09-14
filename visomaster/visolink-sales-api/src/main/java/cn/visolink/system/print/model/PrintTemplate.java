package cn.visolink.system.print.model;

import lombok.Data;

/**
 * @ClassName PrintTemplate
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/9 10:47
 **/
@Data
public class PrintTemplate {

    /**
     * ID
     */
    private String ID;
    /**
     * 打印方式
     */
    private String PrintManner;
    /**
     * 模板类型
     */
    private String PrintType;
    /**
     * 项目ID
     */
    private String ProjectId;
    /**
     * 打印模板
     */
    private String PrintTemplate;
    /**
     * 是否启用
     */
    private String Status;
    /**
     * 是否删除
     */
    private String IsDel;
    /**
     * 是否默认模板（1：是 0：否）
     */
    private String IsDefault;
    /**
     * 创建时间
     */
    private String CreateTime;
    /**
     * 创建人/修改人
     */
    private String CreateUser;
}
