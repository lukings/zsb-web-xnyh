package cn.visolink.system.projectmanager.service;

/**
 * @Author: Mr.Yu
 * @Date: 2021/10/12 10:48
 * @description
 * @Version 1.0
 */
public interface BindProjectService {

    /**
     * 查询失效的项目列表
     *
     * @param companyCode
     * @return
     */
    void selectInvalidProject(String companyCode);

    /**
     * 注册数据库
     *
     * @param dataBaseName
     * @param companyCode
     */
    void registroyDatabase(String dataBaseName, String companyCode);

}