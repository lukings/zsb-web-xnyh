package cn.visolink.system.custMap.bo;

import cn.visolink.system.channel.model.vo.TaskVo;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;


@Data
public class ZsMapDrawResBO extends ZsMapDrawBO {

  @ApiModelProperty(value = "类型名称")
  private String typeName;

  @ApiModelProperty(value = "附件")
  private List<String> enclosures;

  @ApiModelProperty(value = "任务内容")
  private List<TaskVo> taskList;
}
