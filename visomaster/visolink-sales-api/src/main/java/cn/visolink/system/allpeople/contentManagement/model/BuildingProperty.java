package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * @ClassName BuildingProperty
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/11/19 18:09
 **/
@Data
public class BuildingProperty {

    private String id;

    private String buildBookId;//楼栋ID

    private String propertyTypeCode;//物业类型Code

    private String propertyTypeName;//物业类型

    private String renovationCode;//装修标准Code

    private String renovationName;//装修标准

    private String buildBookYears;//产权年限

    private String floorAreaMin;//建筑面积最小值

    private String floorAreaMax;//建筑面积最大值

    private String referencePriceMin;//参考价格最小值

    private String referencePriceMax;//参考价格最大值

    private String priceDisplayMode;//价格展示方式（1：价格待定 2：自定义）

    private int listIndex = 0;//排序

    private String isDel;//是否删除

    private String creator;//创建人

    private String createTime;//创建时间

}
