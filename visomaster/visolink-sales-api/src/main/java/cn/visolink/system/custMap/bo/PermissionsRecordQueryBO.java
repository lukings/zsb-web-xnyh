package cn.visolink.system.custMap.bo;

import cn.visolink.common.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class PermissionsRecordQueryBO extends PageQuery {

  @ApiModelProperty("授权主键")
  private String permissionsId;

  @ApiModelProperty("授权类型")
  private Integer type;

  @ApiModelProperty("被修改人")
  private String userName;

  @ApiModelProperty("起期")
  private Date startDate;

  @ApiModelProperty("止期")
  private Date endDate;

  @ApiModelProperty("修改人")
  private String operator;

  @ApiModelProperty("操作人ids")
  List<String> accountIds;
}
