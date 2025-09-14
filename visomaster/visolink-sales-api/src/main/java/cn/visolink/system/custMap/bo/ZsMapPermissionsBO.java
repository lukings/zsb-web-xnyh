package cn.visolink.system.custMap.bo;

import cn.visolink.common.BaseBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ZsMapPermissionsBO extends BaseBO {

  @ApiModelProperty("主键")
  private String permissionsId;

  @ApiModelProperty("用户ID")
  private String accountId;

  @ApiModelProperty("区域权限")
  private String areaPermissions;

  @ApiModelProperty("区域权限起期")
  private Date areaStartDate;

  @ApiModelProperty("区域权限止期")
  private Date areaEndDate;

  @ApiModelProperty("项目权限")
  private String projPermissions;

  @ApiModelProperty("项目权限起期")
  private Date projStartDate;

  @ApiModelProperty("项目权限止期")
  private Date projEndDate;

  @ApiModelProperty("权限类型")
    private String permissionsType;
}
