package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ZsMapPermissionsRecordBO extends ZsMapRecordBO{

  @ApiModelProperty("地区权限有效期")
  private String areaExpirationDate;

  @ApiModelProperty("项目权限有效期")
  private String projExpirationDate;

  @ApiModelProperty("操作人")
  private String operator;

  @ApiModelProperty("操作时间")
  private String operatorTime;
}
