package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/9/9
 */
@Data
public class DistributionInfo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "线索id")
    private String projectClueId;

    @ApiModelProperty(value = "分配人员id")
    private String distributionUserId;

    @ApiModelProperty(value = "分配人员父级岗位")
    private String selectUserParent;

    @ApiModelProperty(value = "分配人员名称")
    private String distributionUserName;

    @ApiModelProperty(value = "分配人员父级岗位名称")
    private String selectUserParentName;


    private String salesAttributionId;


    private  String salesAttributionName;


    private String salesAttributionPhone;

    private List<String> list;

    private String userId;
}
