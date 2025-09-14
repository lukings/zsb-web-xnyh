package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "活动统计信息类")
public class ActDataStatisticsVo {

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "统计数量")
    private Integer dataCount;
}
