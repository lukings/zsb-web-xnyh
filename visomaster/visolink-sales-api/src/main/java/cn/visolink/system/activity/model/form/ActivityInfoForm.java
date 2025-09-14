package cn.visolink.system.activity.model.form;

import cn.visolink.system.activity.model.ActivityAddress;
import cn.visolink.system.activity.model.ActivityMaterial;
import cn.visolink.system.activity.model.vo.ActivityAwardInfo;
import cn.visolink.system.activity.model.vo.HelpCoupon;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * ActivityInfoForm对象
 * </p>
 *
 * @author autoJob
 * @since 2020-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "ActivityInfo对象", description = "营销活动表")
public class ActivityInfoForm extends Page {

    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "查询项目ID")
    private String projectIds;

    @ApiModelProperty(value = "项目集合")
    private List<String> projectList;

    @ApiModelProperty(value = "时间查询类型（1：发布时间 2：开启时间 3：结束时间）")
    private String reportTime;

    @ApiModelProperty(value = "查询开始时间")
    private String date1;

    @ApiModelProperty(value = "查询结束时间")
    private String date2;

    @ApiModelProperty(value = "活动素材集合")
    private List<ActivityMaterial> materials;

    @ApiModelProperty(value = "优惠卷ID集合")
    private List<String> couponIdList;

    @ApiModelProperty(value = "当前登录人账号")
    private String userName;

    @ApiModelProperty(value = "当前登录人Id")
    private String userId;

    @ApiModelProperty(value = "活动类型")
    private String activityType;

    @ApiModelProperty(value = "报名标题")
    private String sinUpTitle;

    @ApiModelProperty(value = "活动举办地址 1 案场 2 非案场")
    private Integer holdAddress;

    @ApiModelProperty(value = "助力方式")
    private String helpType;

    @ApiModelProperty(value = "助力开始时间")
    private String helpBeginTime;

    @ApiModelProperty(value = "助力结束时间")
    private String helpEndTime;

    @ApiModelProperty(value = "礼品名称")
    private String giftName;

    @ApiModelProperty(value = "地址集合")
    private List<ActivityAddress> addressList;

    @ApiModelProperty(value = "领券展示标题")
    private String collectCouponsTitle;

    @ApiModelProperty(value = "报名上限")
    private String signupMax;

    @ApiModelProperty(value = "助力成功上限")
    private String helpMax;

    @ApiModelProperty(value = "是否限制报名数（0：否 1：是）")
    private String isSignupLimit;

    @ApiModelProperty(value = "是否限制助力成功数（0：否 1：是）")
    private String isHelpLimit;

    @ApiModelProperty(value = "是否展示报名成功数（0：否 1：是）")
    private String isSignupShow;

    @ApiModelProperty(value = "是否展示助力成功数（0：否 1：是）")
    private String isHelpShow;

    @ApiModelProperty(value = "许愿标题")
    private String vow_titile;

    @ApiModelProperty(value = "许愿开始时间")
    private String vow_begintime;

    @ApiModelProperty(value = "许愿标题")
    private String vow_endtime;

    @ApiModelProperty(value = "许愿开奖时间")
    private String vow_open_time;

    @ApiModelProperty(value = "是否分区")
    private int is_support_partition_vow;

    @ApiModelProperty(value = "是否展示奖品名额")
    private int is_show_award_num;

    @ApiModelProperty(value = "开奖状态")
    private String openAwardtStatus;

    @ApiModelProperty(value = "奖项列表")
    private List<ActivityAwardInfo> activityAwardInfo;
    private List<ActivityAwardInfo> awardInfo;

    @ApiModelProperty(value = "虚拟人数")
    private int virtual_num;

    @ApiModelProperty(value = "")
    private String open_award_img;

    @ApiModelProperty("自定义字段")
    private String signup_diy_code;

    @ApiModelProperty("活动说明")
    private String activity_desc;

    private String help_allow_customer_status;

    private String signup_allow_customer_status;

    private String vow_allow_customer_status;

    @ApiModelProperty("助力优惠券")
    private List<HelpCoupon> helpCoupons;

    @ApiModelProperty(value = "许愿标题")
    private String helpTitle;

    @ApiModelProperty(value = "楼盘id集合")
    private List<String> bookIds;

    private String queryType;//判断查询条件（是否按项目或楼盘查询）

    private String books;//楼盘ids字符串

    @ApiModelProperty(value = "热推id")
    private String hotId;

    @ApiModelProperty(value = "是否首页推荐")
    private String isHomePageHot;

    @ApiModelProperty(value = "首页热推图片")
    private String hotImageUrl;

    @ApiModelProperty(value = "热推开始时间")
    private String hotStartTime;

    @ApiModelProperty(value = "热推结束时间")
    private String hotEndTime;

    @ApiModelProperty(value = "热推城市id")
    private List hotCityId;

    @ApiModelProperty(value = "热推状态")
    private String hotStatus;

    @ApiModelProperty(value = "热推城市名称")
    private String hotCityName;

    private String companycode;//公司编码
}
