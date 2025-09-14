package cn.visolink.system.seniorbroker.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/10/16
 */
@Data
public class AccountPerformance implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "活动ID")
    private String activeId;

    @ApiModelProperty(value = "活动所属项目ID")
    private String projectId;

    @ApiModelProperty(value = "大客户ID")
    private String accountId;

    @ApiModelProperty(value = "经纪人ID")
    private String brokerId;

    @ApiModelProperty(value = "线索ID")
    private String projectClueId;

    @ApiModelProperty(value = "业绩归属时间")
    private String performanceDate;

    @ApiModelProperty(value = "是否删除")
    private String isDel;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "修改时间")
    private String editTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    private String clueStatus;

}
