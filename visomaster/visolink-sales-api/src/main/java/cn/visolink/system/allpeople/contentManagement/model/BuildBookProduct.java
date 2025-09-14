package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

import java.util.List;

/**
 * @ClassName BuildBookProduct
 * @Author wanggang
 * @Description //楼盘产品
 * @Date 2022/4/18 21:34
 **/
@Data
public class BuildBookProduct {
    private String id;//ID
    private String buildBookId;//楼盘ID
    private String productType;//产品分类
    private String productTypeDesc;//产品分类描述
    private String productDetail;//产品说明
    private String productImageUrl;//产品展示图路径
    private String productArea;//产品面积
    private String creator;
    private List<Apartment> apartmentList;//户型
}
