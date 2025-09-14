//package cn.visolink.config;
//
//import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author wcl
// * @version 1.0
// * @date 2019/8/24 2:33 下午
// */
//@Configuration
//@MapperScan(value = {"cn.visolink.business.**.mapper","cn.visolink.business.**.dao"})
//public class MybatisPlusConfig {
//
//    /**
//     * mybatis-plus 分页插件
//     */
//
//    @Bean
//    public PaginationInterceptor paginationInterceptor(){
//        PaginationInterceptor page = new PaginationInterceptor();
//        page.setDialectType("mysql");
//        return page;
//    }
//
//}
