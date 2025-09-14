package cn.visolink.system.commission.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 佣金明细表
 * </p>
 *
 * @author autoJob
 * @since 2020-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("a_commission_detail")
@ApiModel(value="CommissionDetail对象", description="佣金明细表")
public class CommissionDetail implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "销管数据id")
    private String xgId;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "房间名称")
    private String roomName;

    @ApiModelProperty(value = "房间id")
    private String roomId;

    @ApiModelProperty(value = "机会id")
    private String intentionId;

    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "客户手机号")
    private String customerMobile;

    @ApiModelProperty(value = "客户性别(1、男2、女)")
    private Boolean customerGender;

    @ApiModelProperty(value = "签约时间")
    private Date contractDate;

    @ApiModelProperty(value = "经纪人id")
    private String brokerId;

    @ApiModelProperty(value = "经纪人姓名")
    private String brokerName;

    @ApiModelProperty(value = "经纪人手机号")
    private String brokerMobile;

    @ApiModelProperty(value = "置业顾问id")
    private String counselorId;

    @ApiModelProperty(value = "置业顾问姓名")
    private String counselorName;

    @ApiModelProperty(value = "奖励类型编码（1、佣金奖励2、非现金奖励）")
    private String rewardTypeCode;

    @ApiModelProperty(value = "奖励类型名称（1、佣金奖励2、非现金奖励）")
    private String rewardTypeName;

    @ApiModelProperty(value = "是否结佣(0、否1、是)")
    private Boolean isCommission;

    @ApiModelProperty(value = "不结佣原因")
    private String noCommissionCause;

    @ApiModelProperty(value = "立项编号")
    private String projectCode;

    @ApiModelProperty(value = "立项时间")
    private Date projectTime;

    @ApiModelProperty(value = "付款申请编号")
    private String paymentCode;

    @ApiModelProperty(value = "佣金金额")
    private BigDecimal commissionAmount;

    @ApiModelProperty(value = "已发佣金金额")
    private BigDecimal paidAmount;

    @ApiModelProperty(value = "已发佣金比例")
    private String paidRadio;

    @ApiModelProperty(value = "1、待审核2、待发放3、发放中4、已发放5、已失效")
    private Boolean status;

    @ApiModelProperty(value = "失效原因")
    private String failureCause;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
