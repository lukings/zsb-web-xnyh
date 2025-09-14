package cn.visolink.system.projectmanager.model.Vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UnitVo
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/11/30 10:52
 **/
@Data
@ApiModel(value="单元信息")
public class UnitVo {

    @ApiModelProperty(value = "楼栋名称")
    private String unitName;

    @ApiModelProperty(value = "单元编号")
    private String unitNo;

    @ApiModelProperty(value = "单元最大房间数")
    private String roomMax;
}
