package cn.visolink.system.activity.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * CouponInfoForm对象
 * </p>
 *
 * @author autoJob
 * @since 2020-05-27
 */
@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value="CouponDetail对象", description="优惠券领取详情表")
public class CouponDetailForm extends Page {

    private static final long serialVersionUID = 1L;

        private String id;

        @ApiModelProperty(value = "关联活动ID")
        private String activityId;

        @ApiModelProperty(value = "关联活动编号")
        private String activityNo;

        @ApiModelProperty(value = "关联活动")
        private String activityName;

        @ApiModelProperty(value = "优惠券ID")
        private String couponId;

        @ApiModelProperty(value = "优惠券名称")
        private String couponName;

        @ApiModelProperty(value = "优惠券编号")
        private String couponNo;

        @ApiModelProperty(value = "用户手机号/昵称")
        private String search;

        @ApiModelProperty(value = "核销项目")
        private String closureProName;

        @ApiModelProperty(value = "优惠券状态：1待使用，2已使用，3已过期")
        private String status;

        @ApiModelProperty(value = "优惠券类型:1折扣券，2代金券，3礼品券")
        private String couponType;

        @ApiModelProperty(value = "领取途径（1：活动 2：微楼书）")
        private List<String> giveType;

        @ApiModelProperty(value = "优惠券状态：1待使用，2已使用，3已过期")
        private String statuss;

        @ApiModelProperty(value = "领取途径（1：活动 2：微楼书）")
        private String giveTypes;

        @ApiModelProperty(value = "查询项目ID")
        private String projectIds;

        @ApiModelProperty(value = "关联项目ID")
        private List<String> projectList;

        @ApiModelProperty(value = "优惠券类型集合")
        private List<String> couponTypeList;

        @ApiModelProperty(value = "时间查询类型（1：领取日期 2：核销日期）")
        private String reportTime;

        @ApiModelProperty(value = "查询开始时间")
        private String date1;

        @ApiModelProperty(value = "查询结束时间")
        private String date2;

        @ApiModelProperty(value = "是否全号")
        private String isAll;

        @ApiModelProperty(value = "当前登录人账号")
        private String userName;

}