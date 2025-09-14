package cn.visolink.system.custMap.bo;

import cn.visolink.common.BaseBO;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

@Data
public class ZsMapRecordBO extends BaseBO {

  @ApiModelProperty("主键")
  private String recordId;

  @ApiModelProperty("授权主键")
  private String permissionsId;

  @ApiModelProperty("授权类型")
  private Integer type;

  @ApiModelProperty("授权用户")
  private String userNames;

  @ApiModelProperty("地区权限")
  private String areaPermissions;

  @ApiModelProperty("地区权限起期")
  private Date areaStartDate;

  @ApiModelProperty("地区权限止期")
  private Date areaEndDate;

  @ApiModelProperty("项目权限")
  private String projPermissions;

  @ApiModelProperty("项目权限起期")
  private Date projStartDate;

  @ApiModelProperty("项目权限止期")
  private Date projEndDate;
}
