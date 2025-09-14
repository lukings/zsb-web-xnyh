package cn.visolink.message.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 多数据源定时任务处理mapper
 *
 * @author yangjie
 * @date 2020-9-8
 */
@Mapper
@Repository
public interface TimedTaskHandleMapper {

    /**
     * 获取 数据源名称
     *
     * @return return
     */
    @Select("SELECT DISTINCT DataBaseUrl FROM `s_productcompanyrel` WHERE ProductType = '1' AND `status` = 1")
    List<String> getDataBaseUrl();

    /**
     * 获取 外部数据源
     *
     * @return return
     */
    @Select("SELECT DISTINCT t1.DataBaseUrl,t1.CompanyCode,t2.url,t2.username,t2.`password` " +
            "FROM `s_productcompanyrel` AS t1 INNER JOIN `s_syn_database_info` AS t2 " +
            "ON t1.CompanyID = t2.companyId WHERE t1.ProductType = 1 AND t1.`status` = 1 " +
            "AND t2.IsDel = 0 AND t2.`Status` = 1")
    List<Map<String, Object>> getExternalDataBaseUrl();

    /*@Select("SELECT * FROM `t_sys_user`")
    List<Map> getUser();*/
}
