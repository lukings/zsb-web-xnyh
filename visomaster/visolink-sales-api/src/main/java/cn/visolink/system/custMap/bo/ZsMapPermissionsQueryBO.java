package cn.visolink.system.custMap.bo;

import cn.visolink.common.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
public class ZsMapPermissionsQueryBO extends PageQuery {

  private List<String> projectIds;

  private String userName;

  private String fullPaths;

  @ApiModelProperty("主键")
  private String permissionsId;
}
