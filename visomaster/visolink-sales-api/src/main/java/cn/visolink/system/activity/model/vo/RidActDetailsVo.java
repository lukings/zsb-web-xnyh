package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "行程信息展示VO类")
public class RidActDetailsVo {

    @ApiModelProperty(value = "行号")
    private String rowNo;

    @ApiModelProperty(value = "行程明细id")
    private String tripDetailsId;

    @ApiModelProperty(value = "乘车活动id")
    private String ridActivityId;

    @ApiModelProperty(value = "乘车活动编号")
    private String ridActivityNo;

    @ApiModelProperty("乘车活动名称")
    private String ridActivityName;

    @ApiModelProperty(value = "城市id")
    private String cityId;

    @ApiModelProperty(value = "城市名称")
    private String cityName;

    @ApiModelProperty(value = "乘车活动项目id")
    private String projectId;

    @ApiModelProperty(value = "乘车活动项目名称")
    private String projectName;

    @ApiModelProperty(value = "楼盘id")
    private String bookId;

    @ApiModelProperty(value = "楼盘名称")
    private String bookName;

    @ApiModelProperty(value = "客户id")
    private String brokerId;

    @ApiModelProperty(value = "客户姓名")
    private String brokerName;

    @ApiModelProperty(value = "客户手机号")
    private String brokerMobile;

    @ApiModelProperty(value = "客户手机号隐号导出")
    private String brokerSecMobile;

    @ApiModelProperty(value = "出发时间")
    private String departureTime;

    @ApiModelProperty(value = "发起时间 即创建时间")
    private String createTime;

    @ApiModelProperty(value = "出发地址")
    private String departureAddr;

    @ApiModelProperty(value = "置业顾问id")
    private  String salesAttributionId;

    @ApiModelProperty(value = "置业顾问姓名")
    private String salesAttributionName;

    @ApiModelProperty(value = "置业顾问手机号")
    private String phone;

    @ApiModelProperty(value = "当前行程状态")
    private String tripStatus;

    public String[] getRidActTitle = new String[] {
            "序号","活动编号","项目名称","楼盘名称","城市名称","客户姓名","客户手机号",
            "行程发起时间","行程出发时间","出发地址","置业顾问姓名","置业顾问手机号","当前行程状态"
    };

    public Object[] toRidActData() {
        return new Object[]{
                getRowNo(),getRidActivityNo(),getProjectName(),getBookName(),getCityName(),getBrokerName(),
                getBrokerMobile(),getCreateTime(),getDepartureTime(),getDepartureAddr(),getSalesAttributionName(),getPhone(),getTripStatus()
        };
    }

    public Object[] roRidActSecData() {
        return new Object[]{
                getRowNo(),getRidActivityNo(),getProjectName(),getBookName(),getCityName(),getBrokerName(),
                getBrokerSecMobile(),getCreateTime(),getDepartureTime(),getDepartureAddr(),getSalesAttributionName(),getPhone(),getTripStatus()
        };
    }
}
