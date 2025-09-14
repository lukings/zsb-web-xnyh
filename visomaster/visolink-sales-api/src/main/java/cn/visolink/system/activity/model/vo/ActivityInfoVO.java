package cn.visolink.system.activity.model.vo;

import cn.visolink.system.activity.model.ActivityAddress;
import cn.visolink.system.activity.model.ActivityMaterial;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ActivityInfoVO对象
 * </p>
 *
 * @author autoJob
 * @since 2020-05-25
 */
@Data
@ApiModel(value = "ActivityInfo对象", description = "营销活动表")
public class ActivityInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String rownum;

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

    @ApiModelProperty(value = "领券数")
    private String couponCollected;

    @ApiModelProperty(value = "活动素材集合")
    private List<ActivityMaterial> materials;

    @ApiModelProperty(value = "项目集合")
    private List<String> projectList;

    @ApiModelProperty(value = "地址集合")
    private List<ActivityAddress> addressList;

    @ApiModelProperty(value = "活动类型")
    private String activityType;

    private String signUpTitle;

    private String holdAddress;

    private String helpType;

    private String helpBeginTime;

    private String helpEndTime;

    private String giftName;

    @ApiModelProperty(value = "报名人数")
    private String signUpCount;

    @ApiModelProperty(value = "签到人数")
    private String signInCount;

    @ApiModelProperty(value = "发起助力人数")
    private String needHelpCount;

    @ApiModelProperty(value = "助力人数")
    private String helpCount;

    @ApiModelProperty(value = "助力成功人数")
    private String helpOkCount;

    private String collectCouponsTitle;

    @ApiModelProperty(value = "禁用时间")
    private String disabletime;

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

    @ApiModelProperty("开奖状态")
    private String openAwardStatus;

    @ApiModelProperty("许愿人数")
    private String vowCount;

    @ApiModelProperty("许愿标题")
    private String vow_titile;

    @ApiModelProperty("许愿开始时间")
    private String vow_begintime;

    @ApiModelProperty("许愿结束时间")
    private String vow_endtime;

    @ApiModelProperty("许愿开奖时间")
    private String vow_open_time;

    @ApiModelProperty("是否分区")
    private Integer is_support_partition_vow;

    @ApiModelProperty("是否展示名额")
    private Integer is_show_award_num;

    @ApiModelProperty("奖项信息")
    private List<Map> awardInfoList;

    @ApiModelProperty(value = "")
    private String open_award_img;

    @ApiModelProperty(value = "自定义字段")
    private String signup_diy_code;

    @ApiModelProperty("活动说明")
    private String activity_desc;

    private String help_allow_customer_status;

    private String signup_allow_customer_status;

    private String vow_allow_customer_status;

    @ApiModelProperty("助力优惠券")
    private List<HelpCoupon> helpCoupons;

    private String helpTitle;

    @ApiModelProperty("助力状态(0表示未有人助力,1代表有人开始已经助力了)")
    private Integer helpStatus;
    @ApiModelProperty("一级助力上线")
    private Integer level1No;
    @ApiModelProperty("二级助力上线")
    private Integer level2No;
    @ApiModelProperty("三级助力上线")
    private Integer level3No;

    @ApiModelProperty("一级助力人数")
    private Integer level1User;
    @ApiModelProperty("二级助力人数")
    private Integer level2User;
    @ApiModelProperty("三级助力人数")
    private Integer level3User;

    @ApiModelProperty(value = "楼盘名称")
    private String bookName;

    @ApiModelProperty(value = "累计乘车人数")
    private Integer ridSum;

    @ApiModelProperty(value = "累计乘车到访人数")
    private Integer ridArriveSum;

    @ApiModelProperty(value = "累计乘车认购人数")
    private Integer ridBuySum;

    @ApiModelProperty(value = "新增乘车报备人数")
    private Integer newRidReportSum;

    @ApiModelProperty(value = "新增乘车到访人数")
    private Integer newRidArriveSum;

    @ApiModelProperty(value = "新增乘车认购人数")
    private Integer newRidBuySum;

    @ApiModelProperty(value = "乘车活动项目名称")
    private String projectName;

    @ApiModelProperty(value = "首页热推集合")
    private Map map;

    @ApiModelProperty(value = "活动热推状态")
    private String isHomePageHot;


    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toActivityData() {
        return new Object[]{
                getRownum(),getActivityNo(),getActivityName(),getActivityProjectnames(),getActivityBegintime(),
                getActivityEndtime(),getCreatetime(),getCreator(),
                getReleaseTime(),getActStatus(),getActivityType(),getCouponCollected(),
                getSignUpCount(),getSignupMax(),getSignInCount(),getNeedHelpCount(),getHelpCount(),
                getLevel1No(),getLevel1User(),getLevel2No(),getLevel2User(),getLevel3No(),getLevel3User(),
                getOpenAwardStatus(),getVowCount(),getBookName(),getRidSum(),getRidArriveSum(),getRidBuySum(),
                getNewRidReportSum(),getNewRidArriveSum(),getNewRidBuySum(),getDisabletime()
        };
    }

    public String[] courtCaseTitle = new String[]{
            "序号","活动编号","活动名称","项目","活动开始时间","活动截止时间",
            "活动创建时间","创建人","活动发布时间","活动状态","活动类型",
            "领券数","报名数","报名上限","签到数","发起助力人数","助力人数","一级助力成功上限",
            "一级助力成功人数","二级助力成功上限",
            "二级助力成功人数","三级助力成功上限",
            "三级助力成功人数","开奖状态","许愿人数","楼盘名称","累计乘车人数","累计乘车到访人数","累计乘车认购人数",
            "新增乘车报备人数","新增乘车到访人数","新增乘车认购人数","禁用时间"};

}
