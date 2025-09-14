package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * CouponInfoVO对象
 * </p>
 *
 * @author autoJob
 * @since 2020-05-27
 */
@Data
@ApiModel(value = "CouponInfo对象", description = "优惠券表")
public class CouponInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rownum;

    private String id;

    @ApiModelProperty(value = "关联活动ID")
    private String activityId;

    @ApiModelProperty(value = "优惠券名称")
    private String couponName;

    @ApiModelProperty(value = "优惠券编号")
    private String couponNo;

    @ApiModelProperty(value = "优惠券类型:1折扣券，2代金券，3礼品券")
    private String couponType;

    @ApiModelProperty(value = "优惠券面值")
    private String couponValue;

    @ApiModelProperty(value = "发布日期")
    private String publishTime;

    @ApiModelProperty(value = "领取开始日期")
    private String begintime;

    @ApiModelProperty(value = "领取结束日期")
    private String endtime;

    @ApiModelProperty(value = "是否可重复领取:1是，0否")
    private String isRepeatedCollection;

    @ApiModelProperty(value = "领取上限")
    private String collectionUp;

    @ApiModelProperty(value = "是否线上支付:1是，0否")
    private String ispay;

    @ApiModelProperty(value = "有效期类型:1固定有效期，2动态有效期")
    private String validType;

    @ApiModelProperty(value = "动态有效期小时数")
    private String validHours;

    @ApiModelProperty(value = "有效期开始时间")
    private String validBegintime;

    @ApiModelProperty(value = "有效期结束时间")
    private String validEndtime;

    @ApiModelProperty(value = "规则说明")
    private String roleDesc;

    @ApiModelProperty(value = "是否显示库存数")
    private String isShowstock;

    @ApiModelProperty(value = "总库存")
    private String stockNo;

    @ApiModelProperty(value = "剩余库存")
    private String stockSurplus;

    @ApiModelProperty(value = "已领券数")
    private String couponCollected;

    @ApiModelProperty(value = "核销数")
    private String couponClosure;

    @ApiModelProperty(value = "优惠券状态：1草稿，2已发布")
    private String couponStatus;

    @ApiModelProperty(value = "状态：1已启用，0未启用")
    private String status;

    @ApiModelProperty(value = "领取人数")
    private String collectionCstCount;

    @ApiModelProperty(value = "核销人数")
    private String closureCstCount;

    @ApiModelProperty(value = "领取张数")
    private String collectionCount;

    @ApiModelProperty(value = "核销张数")
    private String closureCount;

    @ApiModelProperty(value = "是否删除：1已删除，0未删除")
    private String isdel;

    @ApiModelProperty(value = "创建时间")
    private String createtime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "修改时间")
    private String edittime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "更新库存")
    private String updateStock;

    @ApiModelProperty(value = "项目集合")
    private List<String> projectList;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "活动编号")
    private String activityNo;

    @ApiModelProperty(value = "区域名称")
    private String areaNameNames;

    @ApiModelProperty(value = "项目名称")
    private String projectNames;

    @ApiModelProperty(value = "禁用时间")
    private String disabletime;

    @ApiModelProperty(value = "图片")
    private String coupon_image_url;

    @ApiModelProperty(value = "是否作为许愿礼品")
    private String is_vow_award;

    @ApiModelProperty(value = "是否展示在微楼书 1是 0否")
    private Integer isWeiLou;

    @ApiModelProperty("助力状态(0表示未有人助力,1代表有人开始已经助力了)")
    private Integer helpStatus;

    @ApiModelProperty("锁住人数")
    private Integer lockNo;


    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toActivityData() {
        return new Object[]{
                getRownum(),getCouponNo(),getCouponName(), getActivityNo(), getActivityName(), getAreaNameNames(),
                getProjectNames(), getPublishTime(), getBegintime(), getEndtime(), getCreatetime(), getCreator(),
                getCouponType(), getCouponValue(), getStockNo(), getStockSurplus(),
                getCollectionCstCount(), getCollectionCount(), getClosureCstCount(),
                getClosureCount(), getCouponStatus(), getDisabletime()
        };
    }

    public String[] courtCaseTitle = new String[]{
            "序号", "优惠券编号","优惠券展示名称","活动编号","活动名称", "区域", "项目",
            "优惠券发布时间", "优惠券领取开始时间", "优惠券领取截止时间",
            "优惠券创建时间", "创建人", "优惠券类型", "优惠券面值", "总库存",
            "剩余库存", "领取人数", "领取张数", "核销人数", "核销张数", "优惠券状态", "禁用时间"};

}
