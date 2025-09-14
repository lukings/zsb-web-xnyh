package cn.visolink.system.projectmanager.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName TradeCstForm
 * @Author wanggang
 * @Description //交易查询条件
 * @Date 2021/11/24 14:32
 **/
@Data
@ApiModel(value = "交易查询条件对象", description = "")
public class TradeCstForm extends Page {

    @ApiModelProperty(name = "projectIds", value = "房间ID")
    private List<String> projectIds;

    @ApiModelProperty(name = "search", value = "客户姓名/手机号")
    private String search;

    @ApiModelProperty(name = "roomInfo", value = "房间号")
    private String roomInfo;

    @ApiModelProperty(name = "clueStatus", value = "交易状态")
    private List<String> clueStatus;

    @ApiModelProperty(name = "salesName", value = "置业顾问姓名")
    private String salesName;

    @ApiModelProperty(name = "isAll", value = "是否全号")
    private String isAll;

}
