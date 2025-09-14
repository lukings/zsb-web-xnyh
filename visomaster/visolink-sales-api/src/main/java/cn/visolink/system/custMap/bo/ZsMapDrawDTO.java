package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
public class ZsMapDrawDTO extends ZsMapDrawBO {

  @ApiModelProperty(value = "附件")
  private List<String> enclosures;

}
