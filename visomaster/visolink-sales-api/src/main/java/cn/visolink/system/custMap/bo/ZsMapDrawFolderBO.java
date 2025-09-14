package cn.visolink.system.custMap.bo;

import cn.visolink.common.BaseBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ZsMapDrawFolderBO extends BaseBO {

  @ApiModelProperty("主键")
  private String folderId;

  @ApiModelProperty("层级")
  private Integer level;

  @ApiModelProperty("父节点ID")
  private String parentId;

  @ApiModelProperty("文件名")
  private String title;

}
