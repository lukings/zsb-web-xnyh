package cn.visolink.system.projectmanager.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Mr.Yu
 * @Date: 2021/10/12 15:52
 * @description
 * @Version 1.0
 */
@Repository
public interface BindProjectMapper {

    /**
     * 实时查询失效的项目列表并修改失效项目改为禁止
     *
     * @param companyCode
     * @return
     */
    @Select(" <script> select \n" +
            " id, is_bind AS isBind, \n" +
            " project_id AS projectId, \n" +
            " generate_name AS generateName, \n" +
            " project_name AS projectName, \n" +
            " `status`, \n" +
            " create_time AS createTime, \n" +
            " edit_time AS editTime, \n" +
            " start_time AS startTime, \n" +
            " end_time AS endTime, \n" +
            " company_code AS companyCode, \n" +
            " company_name AS companyName, \n" +
            " creator, editer \n" +
            " from b_bind_project \n" +
            " where is_del = 0 and is_bind = 1 \n" +
            " <if test = \" companyCode != null and companyCode != '' \">and company_code = #{companyCode} </if> \n" +
            " and NOW() > end_time </script>")
    List<BindProject> selectInvalidProject(@Param("companyCode") String companyCode);

}