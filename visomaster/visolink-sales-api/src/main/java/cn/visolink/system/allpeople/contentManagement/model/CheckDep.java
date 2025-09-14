package cn.visolink.system.allpeople.contentManagement.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liming
 * <p>
 * created at 2021/3/15 16:47
 */
@Data
public class CheckDep {

    @ApiModelProperty("城市id 1")
    private String cityID;

    @ApiModelProperty("开始时间 2")
    private String showBeginTime;
    @ApiModelProperty("数据id")
    private String id;

    @ApiModelProperty("图片链接 3")
    private String imgUrl;

    @ApiModelProperty("首页轮播跳转类型（1：项目微楼书 2：新闻 3：不跳转4：活动 5:户型 6:小程序） 4")
    private String jumpType;

    @ApiModelProperty("首页轮播跳转类型（1：项目微楼书 2：新闻 3：不跳转4：活动 5:户型 6:小程序） 5")
    private String imgJumpUrl;

    @ApiModelProperty("是否启用 6")
    private String status;

    @ApiModelProperty("结束时间")
    private String showEndTime;



}
