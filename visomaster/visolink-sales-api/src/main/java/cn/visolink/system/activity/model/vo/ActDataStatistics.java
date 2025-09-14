package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "活动数据分析类")
public class ActDataStatistics {

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "累计乘车人数")
    private Integer ridSum;

    @ApiModelProperty(value = "累计乘车到访人数")
    private Integer ridArriveSum;

    @ApiModelProperty(value = "累计乘车认购人数")
    private Integer ridBuySum;

    @ApiModelProperty(value = "新增乘车报备人数")
    private Integer newRidReportSum;

    @ApiModelProperty(value = "新增乘车到访人数")
    private Integer newRidArriveSum;

    @ApiModelProperty(value = "新增乘车认购人数")
    private Integer newRidBuySum;

    @ApiModelProperty(value = "点击次数")
    private Integer clickCount;
}
