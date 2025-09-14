package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/6/30
 */
@Data
public class ActivityHelpVO implements Serializable {

    private static final long serialVersionUID = 1L;



    private String rownum;

    private String id;

    private String activityNo;//活动名称

    private String activityName;//活动名称

    private String helpType;//助力方式(新)

    private String helpBeginTime;//助力开始时间(新)

    private String helpEndTime;//助力结束时间(新)

    private String giftName;//礼品名称(新)

    private String shareId;//发起人id

    private String shareName;//发起人

    private String shareMobile;//发起人手机号

    private String shareMobileAll;//发起人手机号

    private String shareTime;//发起助力时间

    private String closureId;//核销人id

    private String closureName;//核销人

    private String closureTime;//核销时间

    private String helpNum;//助力个数

    private String shareFriendNo;//助力目标人数

    private String relationProject;//活动关联项目

    private Integer level;//活动关联项目

    private String activityId;

    @ApiModelProperty(value = "一级助力目标人数")
    private Integer leve1No;

    @ApiModelProperty(value = "二级助力目标人数")
    private Integer leve2No;

    @ApiModelProperty(value = "三级助力目标人数")
    private Integer leve3No;

    @ApiModelProperty(value = "锁定的礼品名称")
    private String lockCouponNo;

    @ApiModelProperty(value = "锁定的礼品名称")
    private String lockCouponName;

    @ApiModelProperty(value = "锁定的优惠券编码")
    private String lockCouponId;

    private String shareOpenId;

    @ApiModelProperty(value = "领取的优惠券编号")
    private String receiveCouponNo;

    @ApiModelProperty(value = "领取的优惠券名称")
    private String receiveCouponName;

    @ApiModelProperty(value = "领取的优惠券编码")
    private String receiveCouponId;

    @ApiModelProperty(value = "核销的优惠券名称")
    private String heXiaoCouponName;
    @ApiModelProperty(value = "核销的优惠券编码")
    private String heXiaoCouponId;

    private Integer helpStatus;

    private String helpStatusName;

    public String getRownum(){
        return StringUtils.isBlank(rownum) ? "" : rownum.replace(".0","");
    }
    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toActivityHelpData(String isAll){
        String customerMobile = "";
        if ("1".equals(isAll)){
            customerMobile = getShareMobileAll();
        }else{
            customerMobile = getShareMobile();
        }
        return new Object[]{
                getRownum(),getActivityNo(),getActivityName(),getRelationProject(),getShareTime(),
                customerMobile,getShareName(),getHelpType(),
                getHelpBeginTime(),getHelpEndTime(),getHelpStatusName(),getLeve1No(),getLeve2No(),getLeve3No(),getHelpNum(),
                getLockCouponNo(),getLockCouponName(),getReceiveCouponNo(),getReceiveCouponName(),getHeXiaoCouponName(),getClosureName(),getClosureTime()
        };
    }

    public String[]  activityHelpTitle =  new String[]{
            "序号","活动编号","活动名称","关联项目","发起助力时间","用户手机号",
            "用户昵称","助力方式","助力开始时间","助力截至时间","助力状态",
            "一级助力目标人数","二级助力目标人数","三级助力目标人数","已助力人数","锁定的优惠券编号",
            "锁定的优惠券名称","领取的优惠券编号","领取的优惠券名称","核销的优惠券名称","核销人","核销时间"};

}
