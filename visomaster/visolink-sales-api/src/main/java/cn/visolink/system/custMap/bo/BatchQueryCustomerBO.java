package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BatchQueryCustomerBO {

  @ApiModelProperty("类型")
  private String type;

  @ApiModelProperty("业务id")
  private String businessId;

  @ApiModelProperty("公客池id")
  private String poolId;

  @ApiModelProperty("公客池类型")
  private String poolType;

  @ApiModelProperty("线索还是报备")
  private String dataSource;

}
