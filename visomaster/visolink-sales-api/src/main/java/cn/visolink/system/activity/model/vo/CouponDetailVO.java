package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName CouponDetailVO
 * @Author wanggang
 * @Description //领券详情
 * @Date 2020/6/2 9:24
 **/
@Data
@ApiModel(value="CouponInfo对象", description="优惠券表")
public class CouponDetailVO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rownum;

    private String id;
    @ApiModelProperty(value = "优惠券ID")
    private String couponId;
    @ApiModelProperty(value = "优惠券编号")
    private String couponNo;
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;
    @ApiModelProperty(value = "关联活动ID")
    private String activityId;
    @ApiModelProperty(value = "活动编号")
    private String activityNo;
    @ApiModelProperty(value = "活动名称")
    private String activityName;
    @ApiModelProperty(value = "关联项目名称")
    private String couponProjectnames;
    @ApiModelProperty(value = "优惠券类型:1折扣券，2代金券，3礼品券，4秒杀券")
    private String couponType;
    @ApiModelProperty(value = "优惠券面值")
    private String couponValue;
    @ApiModelProperty(value = "有效期开始时间")
    private String validBegintime;
    @ApiModelProperty(value = "有效期结束时间")
    private String validEndtime;
    @ApiModelProperty(value = "规则说明")
    private String roleDesc;
    @ApiModelProperty(value = "领取人ID")
    private String collectionId;
    @ApiModelProperty(value = "领取人手机号")
    private String mobile;
    @ApiModelProperty(value = "领取人昵称")
    private String weChatUserName;
    @ApiModelProperty(value = "领取人openid")
    private String collectionOpenid;
    @ApiModelProperty(value = "领取人")
    private String collection;
    @ApiModelProperty(value = "领取时间")
    private String collectionTime;
    @ApiModelProperty(value = "核销人ID")
    private String closureId;
    @ApiModelProperty(value = "核销人")
    private String closure;
    @ApiModelProperty(value = "核销时间")
    private String closureTime;
    @ApiModelProperty(value = "核销项目ID")
    private String closureProId;
    @ApiModelProperty(value = "核销项目")
    private String closureProName;
    @ApiModelProperty(value = "优惠券状态：1待使用，2已使用，3已过期")
    private String status;
    @ApiModelProperty(value = "领取途径（1：活动 2：微楼书）")
    private String giveType;
    @ApiModelProperty(value = "是否删除：1已删除，0未删除")
    private String isdel;
    @ApiModelProperty(value = "创建时间")
    private String createtime;
    @ApiModelProperty(value = "创建人")
    private String creator;

    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toActivityData(){
        return new Object[]{
                getRownum(),getCouponNo(),getCouponName(),getActivityNo(),getActivityName(),getCouponProjectnames(),
                getCollectionTime(),getMobile(),getWeChatUserName(),getStatus(),getClosureTime(),
                getClosureProName(),getClosure(),getGiveType(),getCouponType(),getCouponValue(),
                getValidBegintime(),getValidEndtime()
        };
    }

    public String[]  courtCaseTitle =  new String[]{
            "序号","优惠券编号","优惠券名称","关联活动编号","关联活动","关联项目","领取时间",
            "用户手机号","用户昵称","优惠券使用状态","核销时间",
            "核销项目","核销操作人","领取来源","优惠券类型","优惠券面值",
            "优惠券有效期开始时间","优惠券有效期截止时间"};

}
