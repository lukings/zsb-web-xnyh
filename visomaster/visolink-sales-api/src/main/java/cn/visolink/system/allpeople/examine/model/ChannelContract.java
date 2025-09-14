package cn.visolink.system.allpeople.examine.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ChannelContract
 * @Author wanggang
 * @Description //渠道商合同
 * @Date 2022/4/1 15:16
 **/
@ApiModel(value="渠道商合同", description="渠道商合同")
@Data
public class ChannelContract {

    @ApiModelProperty(value = "渠道商id")
    private String companyId;
    @ApiModelProperty(value = "合同ID")
    private String contractId;
    @ApiModelProperty(value = "合同路径")
    private String contractUrl;
    @ApiModelProperty(value = "合同编号")
    private String contractNo;
    @ApiModelProperty(value = "合同开始时间")
    private String startTime;
    @ApiModelProperty(value = "合同结束时间")
    private String endTime;
    @ApiModelProperty(value = "合同项目")
    private String proNames;
    @ApiModelProperty(value = "合同状态")
    private String contractStatus;
}
