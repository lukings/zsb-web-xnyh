package cn.visolink.system.company.dao;

import cn.visolink.system.company.model.vo.CompanyExport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.18
 */
@Mapper
public interface CompanyMapper {

    List<Map> getAllList(Map map);
    Integer getAllListCount(Map map);

    int insertCompany(Map map);

    List<Map> getAllProject();

    String isValidByOrgCode(@Param("orgCode") String orgCode);

    List<Map> getAssInforData(Map paramMap);

    int updateCompanyById(Map paramMap);

    void updateCompanyOrgById(Map paramMap);

    int deleteCompanyById(String id);

    List<CompanyExport> getAllListExport(Map map);
}
