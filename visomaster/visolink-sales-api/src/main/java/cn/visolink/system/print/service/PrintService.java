package cn.visolink.system.print.service;

import cn.visolink.system.print.model.PrintInstall;
import cn.visolink.system.print.model.PrintTemplate;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/9 11:08
 **/
public interface PrintService {
    /**
     * @Author wanggang
     * @Description //查询模板字段及打印方式
     * @Date 2020/3/9
     * @Param
     * @return
     **/
    PrintInstall findTemplateField();
    /**
     * @Author wanggang
     * @Description //根据项目ID及分类查询模板
     * @Date 2020/3/9
     * @Param
     * @return
     **/
    PrintTemplate findTemplateByProId(String projectId,String printType);
    /**
     * @Author wanggang
     * @Description //创建模板
     * @Date 11:07 2020/3/9
     * @Param [printTemplate]
     * @return void
     **/
    void addTemplate(PrintTemplate printTemplate);
    /**
     * @Author wanggang
     * @Description //更新模板
     * @Date 11:07 2020/3/9
     * @Param [printTemplate]
     * @return void
     **/
    void updateTemplate(PrintTemplate printTemplate);
}
