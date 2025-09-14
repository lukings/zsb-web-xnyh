package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ZsMapPermissionsExcelBO {

  @ApiModelProperty("区域名称")
  private String areaName;

  @ApiModelProperty("项目名称")
  private String projectName;

  @ApiModelProperty("用户名")
  private String userName;

  @ApiModelProperty("岗位")
  private String jobName;

  @ApiModelProperty("项目权限")
  private String projPermissions;

  @ApiModelProperty("项目有效期")
  private String projExpirationDate;

  @ApiModelProperty("区域权限")
  private String areaPermissions;

  @ApiModelProperty("区域有效期")
  private String areaExpirationDate;

  @ApiModelProperty("权限类型")
  private String permissionsType;

  public Object[] toOldData(){

    return new Object[]{
        getAreaName(), getProjectName(), getUserName(), getJobName(), getProjPermissions(), getProjExpirationDate(),
        getAreaPermissions(), getAreaExpirationDate(), getPermissionsType()
    };
  }
  public String[]  courtCaseTitle =  new String[]{
      "区域名称", "项目名称", "用户名","岗位","项目权限","项目有效期","区域权限","区域有效期","权限类型"};

}
