package cn.visolink.system.product.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

    /**
     * 获取产品列表
     * @return 产品列表
    * */
    List<Map> getAllProductList();


    /**
    * 获取以授权产品列表
    * */
    List<Map> getAuthedProductList(String companyCode);
}
