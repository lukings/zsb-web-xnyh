package cn.visolink.system.customercompany.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerCompanyMapper {


    /**
    * 获取所有客户公司
     * @param map @Param("companyCode") String companyCode, @Param("companyName") String companyName
     * @return 客户公司列表
    * */
    List<Map> getCustomerCompanyList(Map map);

    /*
    * 初始化账号
    *
    * */
    int initUserAdmin(Map map);

    /**
    * 新增公司
     * @param map  请求参数
     * @return 执行结果
    * */
    int saveCustomerCompany(Map map);

    /**
    * 修改公司
     * @param map  请求参数
     * @return 执行结果
    * */
    int updateCustomerCompanyById(Map map);

    /**
    * 删除公司
     * @param map  请求参数
     * @return 执行结果
    * */
    int deleteCustomerCompanyById(Map map);

    /**
     * 获取所有产品
     * @return 产品集合
     * */
    List<Map> getAllProduct();
    /**
     * 获取公司所有产品
     * @return 产品集合
     * */
    List<Map> getAllProductByCompanyID(String companyId);

    /**
    * 添加公司产品授权
     * @return 添加结果
    * */
    int saveProductCompanys(@Param("companyId") String companyId,@Param("list") List list);


}
