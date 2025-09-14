package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 楼书
 * @Auther: wang gang
 * @Date: 2020/2/1 14:09
 * @Description: Pointing to the breeze, the procedure is self-contained
 */
@Data
public class BuildingBook {

    private int num;
    private String ID;//主键ID
    private String ProjectID;//项目ID
    private String ProjectName;//项目名称
    private String ProjectShowName;//项目展示名称
    private String CityID;//城市ID
    private String CityName;//所在城市
    private String BelongArea;//所属区域
    private String BuildBookName;//楼书名称
    private String BuildLabel;//楼书标签
    private String UnitPrice;//单价
    private String CommissionRate;//佣金比率
    private String Address;//地址描述
    private String Longitude;//经度
    private String Latitude;//纬度
    private String ProjectDesc;//项目简介
    private String BuildMobile;//售楼热线
    private String BasicConfig;//基本配置
    private String RoundMating;//周边配套
    private String PeriodInt;//产权年限
    private String PropertyType;//物业类型
    private String FloorSpace;//占地面积
    private String BuildSapce;//建筑面积
    private String DecorationLevel;//装修程度
    private String PlotRatio;//容积率
    private String Developers;//开发商
    private String LandName;//地块名称
    private String OpenTime;//开盘时间
    private String Delivery;//交付时间
    private String PropertyCom;//物业公司
    private String PropertyFee;//物业费
    private String GreenRate;//绿化率
    private String BuildSpaceType;//楼盘包含面积
    private String HouseTypeDesc;//户型
    private String IsHaveVideo;//是否上传了视频 1是 0否
    private String IsHaveVR;//是否上传了vr 1是 0 否
    private String HeadImgUrl;//封面图
    private String IsShowHeadImg;//是否展示封面图 1展示 0 不展示
    private String IsReport;//是否允许报备 1允许 0不允许
    private String Creator;//创建人
    private String CreateTime;//创建时间
    private String Editor;//编辑人
    private String EditTime;//编辑时间
    private String Status;//状态 0禁用 1启用
    private String IsDel;//是否删除 0未删除 1删除
    private String IsPublish;//是否发布 0：不发布 1已发布
    private String IsAllPerfect;//楼盘信息是否完善 0：否 1：是
    private String StartTime;//全民经纪人开启时间
    private String EndTime;//全民经纪人关闭时间
    private String ShareDesc;//楼盘小程序分享描述
    private String ShareDescImgUrl;//楼盘小程序分享描述图片
    private String TrafficMating;//交通配置
    private String Education;//教育
    private String Hospital;//医院
    private String Bank;//银行
    private String Business;//商业
    private String ElseRound;//其他周边
    private String DeliveryDesc;//交付时间说明
    private String InventedOnlookersNum;//虚拟围观人数
    private String OnlookersNum;//围观人数
    private String ListIndex;//排序字段
//    private List<Map> Apartments;//户型集合
    private List<BuildBookPhoto> BuildingPhotos;//轮播图集合
//    private List<BuildingPhoto> BuildingVRs;//VR集合
//    private List<Map> BuildingPropertys;//楼栋业态集合
    private List<Map> BuildingTags;//楼栋标签集合
//    private List<BuildingPhoto> BuildingVideo;//楼盘视频
//    private List<PreSalePermit> PreSalePermits;//预售证号
//    private List<String> zbCity;
    private String cityType;
    private String peripheryCity;
    private List<BuildBookProduct> buildBookProducts;//产品
    private String isHot;//是否热推
    //城市级行政区
    private String District;
    //街道
    private String Street;

    //城市级行政区集合
    private List<String> DistrictList;
//    private String sumPrice;
    private String saleStatus;
}
