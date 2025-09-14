package cn.visolink.system.custMap.bo;

import java.util.List;
import lombok.Data;

@Data
public class ZsMapDrawFolderResBO extends ZsMapDrawFolderBO{

  private List<ZsMapDrawFolderResBO> children;

}
