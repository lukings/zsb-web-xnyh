package cn.visolink.system.channel.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName CluesForm
 * @Author wanggang
 * @Description //线索查询辅助
 * @Date 2022/4/6 17:13
 **/
@Data
@ApiModel(value = "线索查询辅助", description = "线索查询辅助")
public class CluesForm {

    @ApiModelProperty(value = "客户姓名/客户手机号")
    private String search;
    @ApiModelProperty(value = "录入人")
    private String reportUserName;
    @ApiModelProperty(value = "报备人角色（1：业务员 2：四强业务员 3：渠道商）")
    private String reportUserRole;
    @ApiModelProperty(value = "当前页")
    private String pageNum;
    @ApiModelProperty(value = "每页行数")
    private String pageSize;
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
