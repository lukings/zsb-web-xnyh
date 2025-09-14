package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 户型
 * @Auther: wang gang
 * @Date: 2020/2/1 19:04
 * @Description: Pointing to the breeze, the procedure is self-contained
 */
@Data
public class Apartment {

    private String id;//户型ID
    private String buildBookID;//楼书ID
    private String projectID;//项目ID
    private String creator;//创建人--编辑人
    private String status;//是否启用 0 未启用  1 启用
    private String isDel;//是否删除 0 未删除  1 删除
//    private String HouseBigType;//户型名称(原户型大类)
//    private String HouseType;//户型配置(户型分类)
    private String houseArea;//户型面积
    private String houseImg;//户型预览图
    private String firstFloorHeight;//首层高度
    private String productId;//产品ID
    private String productName;//产品
//    private String HouseDetail;//户型详情
//    private String IsHaveVR;//是否启用VR(0:否 1：是)
//    private String VRLookRoom;//VR看房
//    private String VRImgUrl;//VR缩略图路径
//    private String ListIndex;//排序号
//    private String PropertyTypeCode;//物业类型Code
//    private String PropertyTypeName;//物业类型
//    private String FloorAreaMin;//建筑面积最小值
//    private String FloorAreaMax;//建筑面积最大值
//    private String ReferencePriceMin;//参考价格最小值
//    private String ReferencePriceMax;//参考价格最大值
//    private String OnlookersNum;//围观人数
//    private String InventedOnlookersNum;//虚拟围观人数
//    private String Rooms;//室
//    private String Hall;//厅
//    private String Toilet;//卫
//    private String OrientationCode;//户型朝向Code
//    private String OrientationName;//户型朝向
//    private String PriceDisplayMode;//价格展示方式（1：价格待定 2：自定义）
//    private String PriceUnit;//价格单位（1：万元/套  2：元/㎡）
//    private String HouseDetailImgUrl;//户型详情图
//    private List<Map> HouseImgr;//户型轮播图响应集合
//    private List<HouseImg> HouseImgq;//户型轮播图请求集合
//    private List<Map> HouseImgVr;//户型视频响应集合
//    private List<HouseImg> HouseImgVq;//户型视频请求集合
//    private List<Map> HouseTagr;//户型标签响应集合
//    private List<BuildBookTag> HouseTagq;//户型标签请求集合
//    private List<Map> HouseTagDescr;//户型标签解析响应集合
//    private List<HouseAnalysis> HouseTagDescq;//户型标签解析请求集合
}
