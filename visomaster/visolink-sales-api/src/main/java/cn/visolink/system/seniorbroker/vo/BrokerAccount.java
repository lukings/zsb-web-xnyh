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
public class BrokerAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "二级经纪人ID")
    private String brokerId;

    @ApiModelProperty(value = "大客户经理ID")
    private String accountId;

    @ApiModelProperty(value = "归属项目ID")
    private String projectId;

    @ApiModelProperty(value = "归属项目名称")
    private String projectName;

    @ApiModelProperty(value = "是否删除1删除，0未删除")
    private String isDel;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "编辑时间")
    private String editTime;

}
