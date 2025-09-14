package cn.visolink.system.activity.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "乘车活动条件查询类")
public class RidActConditionsVo extends Page {

    @ApiModelProperty(value = "区域项目复选id集合")
    private List<String> projectIds;

    @ApiModelProperty(value = "区域项目复选名称集合")
    private List<String> projectNames;

    @ApiModelProperty(value = "楼盘id集合")
    private List<String> bookIds;

    @ApiModelProperty(value = "楼盘名称集合")
    private List<String> bookNames;

    @ApiModelProperty(value = "城市id集合")
    private String cityId;

    @ApiModelProperty(value = "城市名称集合")
    private String cityName;

    @ApiModelProperty(value = "活动编号")
    private String ridActivityNo;

    @ApiModelProperty(value = "活动名称")
    private String ridActivityName;

//    @ApiModelProperty(value = "参与活动的用户身份")
//    private List<String> ridAllowCustomerStatusList;

    @ApiModelProperty(value = "创建人姓名")
    private String creatorName;

    @ApiModelProperty(value = "活动状态")
    private String status;

    @ApiModelProperty(value = "乘车活动时间类型  1 活动开始时间 2 活动结束时间 3 活动禁用时间  乘车明细时间类型 1 发起时间 2 出发时间")
    private String timeType;

    @ApiModelProperty(value = "活动开始时间")
    private String activityBegintime;

    @ApiModelProperty(value = "活动结束时间")
    private String activityEndtime;

    @ApiModelProperty(value = "客户姓名")
    private String brokerName;

    @ApiModelProperty(value = "客户手机号")
    private String brokerMobile;

    @ApiModelProperty(value = "行程状态")
    private String tripStatus;

    @ApiModelProperty(value = "导出设置 1 全号导出 2 引号导出")
    private Integer exportType;

    @ApiModelProperty(value = "置业顾问姓名")
    private String salesAttributionName;

}
