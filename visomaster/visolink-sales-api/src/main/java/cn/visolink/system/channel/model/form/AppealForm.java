package cn.visolink.system.channel.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName AppealForm
 * @Author wanggang
 * @Description //申诉
 * @Date 2022/8/26 9:36
 **/
@Data
@ApiModel(value="申诉", description="申诉")
public class AppealForm {

    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "用户ID")
    private String userId;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "问题描述")
    private String problemDesc;
    @ApiModelProperty(value = "处理人ID")
    private String handleUserId;
    @ApiModelProperty(value = "处理时间")
    private String handleTime;
    @ApiModelProperty(value = "处理意见")
    private String handleDesc;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "修改时间")
    private String editTime;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "项目ID")
    private String projectId;
    @ApiModelProperty(value = "项目")
    private String projectName;
    @ApiModelProperty(value = "流程ID")
    private String processId;
    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "附件集合")
    private List<Map> imgUrls;

}
