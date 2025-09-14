package cn.visolink.system.allpeople.contentManagement.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BuildingPhoto
 * @Author wanggang
 * @Description //楼栋图片
 * @Date 2020/1/17 16:12
 **/
@Data
public class BuildingPhoto {

    private String ID;//图片ID
    private String PID;//图片父级ID
    private String CityID;//城市ID
    private String CityName;//城市名称
    private String BuildBookID;//楼书ID
    private String ImgUrl;//图片链接
    private String newImgUrl;//新图片链接
    private String OldVideoName;//原视频名称
    private String ImgName;//图片名称
    private String ImgDesc;//图片描述
    private String ListIndex;//序号
    private String Status;//状态 0 禁用 1 启用
    private String IsDel;//是否删除
    private String TypeName;//类别名称 1.楼书vr 2 楼书图片 3.楼书视频 4 首页图片 5 首页弹窗
    private String PicturesName;//图集名称
    private String ImgJumpUrl;//图片跳转链接
    private String JumpType;//跳转类型
    private String JumpToName;//首页轮播跳转落地页名称
    private String IsShow;//是否展示
//    private MultipartFile file;//图片文件
    private String Creator;//创建人
    private String addOrEdit;//是否修改  1：添加 2：修改
    private String ShowBeginTime = null;//开始展示时间
    private String ShowEndTime = null;//结束展示时间
    private String CreateTime;//创建时间
    private String BeginAndEndTimeDesc;//展示时间描述
    private String ClickNum;//点击次数
    private String MiNiImgUrl;
    private String isSupernatant;
    private String VRUrl;//楼书VR链接
    private String JumpBuildId;//户型跳转对应楼盘ID

    private String MaterialType;//楼盘图片类别code
    private String MaterialTypeDesc;//楼盘图片类别

    /**
     * 用于app或pad端 1 app 2 pad
     */
    @ApiModelProperty(value = "用于app或pad端 1 app 2 pad")
    private Integer port;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String editor;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private String editTime;



}
