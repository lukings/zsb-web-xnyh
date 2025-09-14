package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ZsMapPermissionsVO extends ZsMapPermissionsBO {

  @ApiModelProperty("区域权限")
  private List<List<String>> areaPermissionsList;

  @ApiModelProperty("项目权限")
  private List<List<String>> projPermissionsList;

  @ApiModelProperty("项目权限")
  private List<String> areaDateTime;

  @ApiModelProperty("项目权限")
  private List<String> projDateTime;

  @ApiModelProperty("用户姓名")
  private String userName;

  @ApiModelProperty("权限类型")
  private String permissionsType;

  @ApiModelProperty("权限类型")
  private List<String> permissionsTypeList;
}
