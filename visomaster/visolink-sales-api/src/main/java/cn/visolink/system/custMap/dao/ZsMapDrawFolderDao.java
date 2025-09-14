package cn.visolink.system.custMap.dao;

import cn.visolink.system.custMap.bo.ZsMapDrawFolderBO;
import cn.visolink.system.custMap.bo.ZsMapDrawFolderQueryBO;
import cn.visolink.system.custMap.bo.ZsMapDrawFolderResBO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @Author lishumao
 * @Description 客户地图权限记录dao
 * @Date 2024/8/20 10:49
 **/
public interface ZsMapDrawFolderDao {

    int save(ZsMapDrawFolderBO zsMapDrawFolderBO);

    List<ZsMapDrawFolderResBO> select(ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO);

    int update(ZsMapDrawFolderQueryBO zsMapDrawFolderQueryBO);
}
