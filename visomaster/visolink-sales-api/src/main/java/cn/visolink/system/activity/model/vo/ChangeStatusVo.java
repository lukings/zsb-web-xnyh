package cn.visolink.system.activity.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "修改(所有)活动参数类")
public class ChangeStatusVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "状态")
    private String status;
}
