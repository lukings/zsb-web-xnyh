package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ZsMapDrawFolderQueryBO {

  @ApiModelProperty("主键")
  private String folderId;

  @ApiModelProperty("层级")
  private Integer level;

  @ApiModelProperty("父节点ID")
  private String parentId;

  @ApiModelProperty("文件名称")
  private String title;

  @ApiModelProperty("创建人")
  private String createBy;

}
