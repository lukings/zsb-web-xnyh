package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ZsMapRulesBO {

  @ApiModelProperty("客户姓名")
  private String customerName;

  @ApiModelProperty("联系方式")
  private String customerMobile;

  @ApiModelProperty("直线距离")
  private String lineDistance;
}
