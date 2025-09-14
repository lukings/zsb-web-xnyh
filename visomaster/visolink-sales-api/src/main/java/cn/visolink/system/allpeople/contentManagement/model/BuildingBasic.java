package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

import java.util.List;

/**
 * 楼书基本信息
 * @Auther: wang gang
 * @Date: 2020/2/1 18:24
 * @Description: Pointing to the breeze, the procedure is self-contained
 */
@Data
public class BuildingBasic {
    private String id;//楼盘ID
    private String belongArea;//区域
    private String cityID;//城市ID
    private String cityName;//城市
    private String projectID;//项目ID
    private String projectName;//项目名称
    private String projectShowName;//项目展示名称
    private String unitPrice;//单价
    private String address;//项目地址
    private String buildMobile;//售楼热线
    private String longitude;//经度
    private String latitude;//纬度
    private String isShowHeadImg;//是否展示
    private String buildLabel;//标签逗号隔开
    private String openTime;//开盘时间
    private String delivery;//交付时间
    private String periodInt;//产权年限
    private String decorationLevel;//装修程度
    private String propertyCom;//物业公司
    private String propertyFee;//物业费
    private String floorSpace;//占地面积
    private String buildSapce;//建筑面积
    private String greenRate;//绿化率
    private String plotRatio;//容积率
    private String developers;//开发商
    private String projectDesc;//项目简介
    private String infrastructure;//基础设施
    private String shareDesc;//楼盘小程序分享描述
    private String ShareDescImgUrl;//楼盘小程序分享描述图片
    private String trafficMating;//交通配套
    private String education;//教育
    private String hospital;//医院
    private String bank;//银行
    private String business;//商业
    private String elseRound;//其他周边
    private String roundMating;//周边配套
    private String headImgUrl;//封面图
//    private String videoName;//视频名称
    private String deliveryDesc;//交付时间说明
    private String inventedOnlookersNum;//虚拟围观人数
//    private String isHaveVideo;//是否上传了视频 1是 0否
//    private List<BuildingPhoto> BuildingVideo;//楼盘视频
//    private List<Apartment> Apartments;//户型集合
    private List<BuildBookPhoto> BuildingPhotos;//轮播图集合
//    private List<BuildingPhoto> BuildingVRs;//VR集合
//    private List<BuildingProperty> BuildingPropertys;//楼栋业态集合
    private List<BuildBookTag> BuildingTags;//楼栋标签集合
//    private List<PreSalePermit> PreSalePermits;//预售证号
//    private MultipartFile BuildingFile;//楼书列表展示图片
//    private MultipartFile[] BuildingFiles;//楼书轮播图片数组
//    private MultipartFile BuildingVideoFile;//楼书视频文件
    private String addOrEdit;//新增或修改 （1：新增 2：修改）
    private String creator;//新增或修改人
//    private List<BuildBookCity> buildBookCity;//楼盘周边城市
//    private List<String> zbCity;
    private String ListIndex;//排序字段
    private List<BuildBookPeriphery> schools;//学校
    private List<BuildBookPeriphery> traffics;//交通
    private List<BuildBookPeriphery> shoppings;//购物
    private List<BuildBookPeriphery> foods;//餐饮
    private List<BuildBookPeriphery> hospitals;//医院
    private List<BuildBookProduct> buildBookProducts;//产品
    private String isHot;//是否热推
    //城市级行政区
    private String district;
    //街道
    private String street;
//    private String sumPrice; // 总价
    private String saleStatus; // 楼盘销售状态
}
