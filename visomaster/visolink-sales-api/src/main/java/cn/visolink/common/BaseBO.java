package cn.visolink.common;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class BaseBO implements Serializable {

  @ApiModelProperty("创建人")
  private String createBy;

  @ApiModelProperty("创建时间")
  private Date createTime;

  @ApiModelProperty("修改人")
  private String updateBy;

  @ApiModelProperty("修改时间")
  private Date updateTime;

  @ApiModelProperty("修改人")
  private String modifyBy;

  @ApiModelProperty("修改时间")
  private Date modifyTime;

  @ApiModelProperty("是否删除")
  private Integer isDel;

}
