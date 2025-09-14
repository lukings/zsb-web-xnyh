package cn.visolink.system.print.dao;

import cn.visolink.system.print.model.PrintField;
import cn.visolink.system.print.model.PrintTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //打印mapper
 * @Date 2020/3/9
 * @Param
 * @return
 **/
public interface PrintDao {


    /**
     * @Author wanggang
     * @Description //根据项目ID查询模板
     * @Date 2020/3/9
     * @Param projectId printType
     * @return
     **/
    PrintTemplate findTemplateByProId(@Param("projectId") String projectId,@Param("printType") String printType);
    /**
     * @Author wanggang
     * @Description //查询默认模板
     * @Date 2020/3/9
     * @Param printType
     * @return
     **/
    PrintTemplate findTemplateNoProId(@Param("printType") String printType);

    /**
     * @Author wanggang
     * @Description //获取打印字段
     * @Date 2020/3/9
     * @Param
     * @return
     **/
    List<PrintField> getAllField();
    /**
     * @Author wanggang
     * @Description //获取打印方式字典
     * @Date 2020/3/9
     * @Param
     * @return
     **/
    List<Map> getPrintCountDist();
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
