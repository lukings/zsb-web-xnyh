package cn.visolink.system.projectmanager.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DealCstForm
 * @Author wanggang
 * @Description //成交客户请求对象
 * @Date 2022/1/24 17:05
 **/
@Data
@ApiModel(value = "成交客户请求对象", description = "")
public class DealCstForm extends Page{

    @ApiModelProperty(name = "roomInfo", value = "房间全称")
    private String roomInfo;

    @ApiModelProperty(name = "search", value = "客户姓名/手机号")
    private String search;

    @ApiModelProperty(name = "projectIds", value = "项目Id集合")
    private List<String> projectIds;

    @ApiModelProperty(name = "clueStatus", value = "客户状态(认购,签约)")
    private String clueStatus;

    @ApiModelProperty(name = "sourceType", value = "成交类型(1:中介 4：全民经纪人)")
    private String sourceType;

    @ApiModelProperty(name = "zygw", value = "置业顾问")
    private String zygw;

    @ApiModelProperty(name = "reportUserName", value = "报备人")
    private String reportUserName;

    @ApiModelProperty(name = "performanceAttributorOld", value = "业绩归属人")
    private String performanceAttributorOld;

    @ApiModelProperty(name = "timeType", value = "时间类型 1：报备时间 2：到访时间 3：认购时间 4：签约时间")
    private String timeType;

    @ApiModelProperty(name = "startTime", value = "开始时间")
    private String startTime;

    @ApiModelProperty(name = "endTime", value = "结束时间")
    private String endTime;

}
