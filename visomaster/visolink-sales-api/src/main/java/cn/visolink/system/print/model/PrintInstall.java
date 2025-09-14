package cn.visolink.system.print.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName PrintInstall
 * @Author wanggang
 * @Description //打印设置
 * @Date 2020/3/9 10:56
 **/
@Data
public class PrintInstall {

    /**
     * 基本信息字段
     */
    private List<PrintField> basicField;
    /**
     * 客户信息字段
     */
    private List<PrintField> custField;
    /**
     * 全民经纪人字段
     */
    private List<PrintField> brokerField;
    /**
     * 打印方式
     */
    private List<Map> printCount;
}
