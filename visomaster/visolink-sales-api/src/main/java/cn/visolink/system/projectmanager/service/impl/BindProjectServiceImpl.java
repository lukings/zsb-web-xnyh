package cn.visolink.system.projectmanager.service.impl;

import cn.visolink.system.projectmanager.dao.BindProject;
import cn.visolink.system.projectmanager.dao.BindProjectMapper;
import cn.visolink.system.projectmanager.service.BindProjectService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Mr.Yu
 * @Date: 2021/10/12 10:48
 * @description
 * @Version 1.0
 */
@Service
public class BindProjectServiceImpl implements BindProjectService {

    @Autowired
    private BindProjectMapper bindProjectMapper;

//    protected static List<CustomerCompany> knowDataBaseList;
//
//    static {
//        knowDataBaseList = getDataBase();
//        CustomerCompany com = new CustomerCompany("10000001", "zhyx");
//        if (knowDataBaseList != null && !knowDataBaseList.isEmpty()) {
//            //CustomerCompany com = new CustomerCompany("10000001", "zhyx");
//            if (!knowDataBaseList.contains(com)) {
//                knowDataBaseList.add(com);
//            }
//        }
//        System.out.println("添加的数据库为： " + com);
//        System.out.println("已经知道的数据库有： " + knowDataBaseList );
//    }

    /**
     * 实时查询失效的项目列表并修改失效项目改为禁止
     *
     * @param companyCode
     * @return
     */
    @Override
    public void selectInvalidProject(String companyCode) {
        try {
            // 获取数据库列表
//            List<CustomerCompany> dataBaseList = knowDataBaseList;
//            List<CustomerCompany> cus = new ArrayList<>();
//            if (dataBaseList != null && !dataBaseList.isEmpty()) {
//                CustomerCompany com = new CustomerCompany("10000001", "zhyx");
//                if (!dataBaseList.contains(com)) {
//                    dataBaseList.add(com);
//                }
//            }
//            // 查询所有公司的失效的项目
//            if (StringUtils.isEmpty(companyCode)) {
//                updateInvalidProject(null, dataBaseList);
//            }
//            // 根据公司编码查询失效的项目
//            else {
//                updateInvalidProject(companyCode, dataBaseList);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateInvalidProject(String companyCode, List<CustomerCompany> dataBaseList) {
        Connection conn = null;
        PreparedStatement ps = null;
        List<BindProject> bindProjects = new ArrayList<>();
        ResultSet rs = null;

        // 获取失效的项目列表
        try {
            String urlAuthcompany = "jdbc:mysql://118.190.56.178:3306/authcompany" + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(urlAuthcompany, "root", "root");
            String invalidProjectSql = " select \n" +
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
                    " where is_del = 0 and is_bind = 1 \n";
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(companyCode)) {
                        invalidProjectSql = invalidProjectSql + "and company_code = '" + companyCode + "'";
                    }
            invalidProjectSql = invalidProjectSql + " and NOW() > end_time";
            ps = conn.prepareStatement(invalidProjectSql);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int is_bind = rs.getInt("isBind");
                String project_id = rs.getString("projectId");
                String projectName = rs.getString("projectName");
                int status = rs.getInt("status");
                java.util.Date createTime = rs.getDate("createTime");
                java.util.Date edit_time = rs.getDate("editTime");
                java.util.Date start_time = rs.getDate("startTime");
                Date end_time = rs.getDate("endTime");
                String company_code = rs.getString("companyCode");
                String company_name = rs.getString("companyName");
                String creator = rs.getString("creator");
                String editer = rs.getString("editer");
                String generateName = rs.getString("generateName");
                // 封装 BindProject 实体
                BindProject bean = BindProject.builder()
                        .id(id).isBind(is_bind).projectId(project_id)
                        .projectName(projectName).status(status)
                        .createTime(createTime).editTime(edit_time)
                        .startTime(start_time).endTime(end_time)
                        .companyCode(company_code).companyName(company_name)
                        .creator(creator).editer(editer).generateName(generateName).build();
                bindProjects.add(bean);
            }
            System.out.println("查询到的数据为： " + bindProjects);

            if (null != bindProjects && !bindProjects.isEmpty()) {
                // 遍历失效的项目列表
                for (BindProject bindProject : bindProjects) {
                    // 遍历数据库
                    for (CustomerCompany company : dataBaseList) {
                        if (company.getCompanyCode().equals(bindProject.getCompanyCode())) {
                            String url = "jdbc:mysql://118.190.56.178:3306/" + company.getDataBaseUrl() + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
                            System.out.println("拼接的数据库url: " + url);
                            conn = DriverManager.getConnection(url, "root", "root");

                            String sql = " update b_project \n" +
                                    " set Status = 0 \n" +
                                    " WHERE id = '" + bindProject.getProjectId() + "' and Status = 1 ";
                            ps = conn.prepareStatement(sql);
                            ps.executeUpdate();
                            ps.close();
                            conn.close();
                        }
                        continue;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  获取智慧营销数据库
     *
     */
    public static List<CustomerCompany> getDataBase() {
        List<CustomerCompany> dataBaseList = new ArrayList<>();
        Connection conn = null; PreparedStatement statement = null; ResultSet rs = null;
        try {
            String url = "jdbc:mysql://118.190.56.178:3306/authcompany" + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(url, "root", "root");
            String sql = "SELECT \n" +
                    " DataBaseUrl as dataBaseUrl, CompanyCode as companyCode \n" +
                    " FROM \n" +
                    " s_productcompanyrel \n" +
                    " GROUP BY DataBaseUrl";
            statement = conn.prepareStatement(sql);
            //执行脚本
            rs = statement.executeQuery();
            while (rs.next()) {
                String dataBase = rs.getString("dataBaseUrl");
                String companyCode = rs.getString("companyCode");
                CustomerCompany customerCompany = new CustomerCompany(dataBase, companyCode);
                dataBaseList.add(customerCompany);
            }
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            try {
                rs.close();
                statement.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return dataBaseList;
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    static class CustomerCompany {

        /**
         *   公司code
         */
        private String companyCode;

        /**
         *   数据库名称
         */
        private String dataBaseUrl;
    }

    /**
     * 注册数据库
     *
     * @param dataBaseName
     * @param companyCode
     */
    @Override
    public void registroyDatabase(String dataBaseName, String companyCode) {
//        CustomerCompany company = new CustomerCompany(dataBaseName, companyCode);
//        if (!knowDataBaseList.contains(company)) {
//            knowDataBaseList.add(company);
//        }
    }

    public static void main(String[] args) {
        List<CustomerCompany> dataBase = BindProjectServiceImpl.getDataBase();
        System.out.println(dataBase);
    }
}