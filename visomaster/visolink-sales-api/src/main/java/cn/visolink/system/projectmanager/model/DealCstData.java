package cn.visolink.system.projectmanager.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName DealCstData
 * @Author wanggang
 * @Description //成交客户信息
 * @Date 2022/1/24 15:11
 **/
@Data
@ApiModel(value = "成交客户对象", description = "")
public class DealCstData {

    @ApiModelProperty(name = "roomId", value = "房间ID")
    private String roomId;

    @ApiModelProperty(name = "roomInfo", value = "房间全称")
    private String roomInfo;

    @ApiModelProperty(name = "cstName", value = "客户姓名")
    private String cstName;

    @ApiModelProperty(name = "cstMobile", value = "客户手机号")
    private String cstMobile;

    @ApiModelProperty(name = "projectName", value = "项目")
    private String projectName;

    @ApiModelProperty(name = "clueStatus", value = "客户状态")
    private String clueStatus;

    @ApiModelProperty(name = "sourceType", value = "成交类型")
    private String sourceType;

    @ApiModelProperty(name = "zygw", value = "置业顾问")
    private String zygw;

    @ApiModelProperty(name = "projectId", value = "项目ID")
    private String projectId;

    @ApiModelProperty(name = "htTotal", value = "交易金额")
    private String htTotal;

    @ApiModelProperty(name = "customerCardNum", value = "客户证件号码")
    private String customerCardNum;

    @ApiModelProperty(name = "reportUserName", value = "报备人")
    private String reportUserName;

    @ApiModelProperty(name = "performanceAttributorOld", value = "业绩归属人")
    private String performanceAttributorOld;

    @ApiModelProperty(name = "reportCreateTime", value = "报备时间")
    private String reportCreateTime;

    @ApiModelProperty(name = "clueValidity", value = "线索有效性( 1 正常 2 放弃 3 无效 4 作废 5 渠道逾期)")
    private String clueValidity;

    @ApiModelProperty(name = "reportExpireDate", value = "报备逾期时间")
    private String reportExpireDate;

    @ApiModelProperty(name = "theFirstVisitDate", value = "首访时间")
    private String theFirstVisitDate;

    @ApiModelProperty(name = "orderDate", value = "认购时间")
    private String orderDate;

    @ApiModelProperty(name = "contractDate", value = "签约时间")
    private String contractDate;

    @ApiModelProperty(name = "remarks", value = "备注")
    private String remarks;

    /**
     * 获取数据
     *
     * @param
     * @return
     */
    public Object[] toDealCstData() {
        return new Object[]{
                getCstName(),getCustomerCardNum(),getClueStatus(),getZygw(),
                getReportUserName(),getPerformanceAttributorOld(),getReportCreateTime(),
                getClueValidity(),getReportExpireDate(),getCstMobile(),getTheFirstVisitDate(),
                null,getOrderDate(),getContractDate(),getRoomInfo(),null,getRemarks()
        };
    }

    public String[] dealCstTitle = new String[]{
            "客户姓名*","客户证件号码*","客户状态","置业顾问*","报备人*","业绩归属人*",
            "报备时间*","报备有效性","报备失效时间","客户手机号","到访时间",
            "认筹时间","认购时间","签约时间","签约房号","订单佣金","备注"};


}
