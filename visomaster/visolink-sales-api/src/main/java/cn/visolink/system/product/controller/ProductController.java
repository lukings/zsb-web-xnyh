package cn.visolink.system.product.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.product.dao.ProductMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api("产品管理")
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @Log(" 获取产品列表")
    @ApiOperation(value = "获取产品列表",httpMethod = "GET")
    @GetMapping("/getAllProductList")
    public ResultBody getAllProductList(){
        return ResultBody.success(productMapper.getAllProductList());
    }


/*
    @Log(" 获取已授权产品列表")
    @ApiOperation(value = "获取产品列表",httpMethod = "GET")
    @GetMapping("/getAuthedProductList")
    public ResultBody getAuthedProductList(HttpServletRequest request){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://118.190.56.178:3306/authcompany?characterEncoding=utf-8&useSSL=false&serverTimezone=UTC&useAffectedRows=true");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        DynamicDataSource.dataSourcesMap.put("authcompany", druidDataSource);
        DynamicDataSource.setDataSource("authcompany");
        String companyCode = request.getHeader("companycode");
        return ResultBody.success(productMapper.getAuthedProductList(companyCode));
    }*/


}
