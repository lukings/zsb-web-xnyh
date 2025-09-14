package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

@Data
public class ZsMapPermissionsResBO {

  @ApiModelProperty("主键")
  private String permissionsId;

  @ApiModelProperty("区域名称")
  private String areaName;

  @ApiModelProperty("项目名称")
  private String projectName;

  @ApiModelProperty("用户名")
  private String userName;

  @ApiModelProperty("用户ID")
  private String accountId;

  @ApiModelProperty("岗位")
  private String jobName;

  @ApiModelProperty("项目权限")
  private String projPermissions;

  @ApiModelProperty("项目有效期")
  private String projExpirationDate;

  @ApiModelProperty("项目权限起期")
  private Date projStartDate;

  @ApiModelProperty("项目权限止期")
  private Date projEndDate;

  @ApiModelProperty("区域权限")
  private String areaPermissions;

  @ApiModelProperty("区域有效期")
  private String areaExpirationDate;

  @ApiModelProperty("区域权限起期")
  private Date areaStartDate;

  @ApiModelProperty("区域权限止期")
  private Date areaEndDate;

  @ApiModelProperty("权限类型")
  private String permissionsType;
}
