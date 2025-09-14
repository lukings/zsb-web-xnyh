package cn.visolink.system.activity.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 营销活动表
 * </p>
 *
 * @author autoJob
 * @since 2020-05-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("a_activity_info")
@ApiModel(value="ActivityInfo对象", description="营销活动表")
public class ActivityInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "活动编号")
    private String activityNo;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "活动层级:1全国，2区域，3城市")
    private String activityLevel;

    @ApiModelProperty(value = "活动范围:范围内的项目名称")
    private String activityProjectnames;

    @ApiModelProperty(value = "分享文案")
    private String activitySharecont;

    @ApiModelProperty(value = "发布时间")
    private String releaseTime;

    @ApiModelProperty(value = "报名开始时间")
    private String signupBegintime;

    @ApiModelProperty(value = "报名结束时间")
    private String signupEndtime;

    @ApiModelProperty(value = "活动开始时间")
    private String activityBegintime;

    @ApiModelProperty(value = "活动结束时间")
    private String activityEndtime;

    @ApiModelProperty(value = "活动规则说明")
    private String activityNote;

    @ApiModelProperty(value = "是否需要报名")
    private String isSignup;

    @ApiModelProperty(value = "是否需要签到")
    private String isSignin;

    @ApiModelProperty(value = "是否邀请好友注册")
    private String isSharefriend;

    @ApiModelProperty(value = "是否有优惠券")
    private String havecoupon;

    @ApiModelProperty(value = "签到形式:1不签到，2线下签到，3线上签到")
    private String signinType;

    @ApiModelProperty(value = "邀请几人奖励")
    private String sharefriendNo;

    @ApiModelProperty(value = "邀请好友礼品:1线下礼品，2优惠券")
    private String sharefriendGift;

    @ApiModelProperty(value = "礼品领取方式:1报名领取，2签到领取，3直接领取")
    private String collectionMethod;

    @ApiModelProperty(value = "活动状态:1草稿，2已发布")
    private String actStatus;

    @ApiModelProperty(value = "是否启用:1已启用，0未启用")
    @TableLogic
    private String status;

    @ApiModelProperty(value = "是否删除:1已删除，0未删除")
    private String isDel;

    @ApiModelProperty(value = "创建时间")
    private String createtime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "修改时间")
    private String edittime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "禁用时间")
    private String disabletime;


}
