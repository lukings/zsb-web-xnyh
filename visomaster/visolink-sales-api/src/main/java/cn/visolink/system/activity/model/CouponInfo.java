package cn.visolink.system.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 优惠券表
 * </p>
 *
 * @author autoJob
 * @since 2020-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("a_coupon_info")
@ApiModel(value="CouponInfo对象", description="优惠券表")
public class CouponInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
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
    @TableLogic
    private String status;

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

}
