package cn.visolink.system.custMap.dao;

import cn.visolink.system.custMap.bo.ZsMapDrawBO;
import cn.visolink.system.custMap.bo.ZsMapDrawQueryBO;
import cn.visolink.system.custMap.bo.ZsMapDrawResBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author lishumao
 * @Description 客户地图权限记录dao
 * @Date 2024/8/20 10:49
 **/
public interface ZsMapDrawDao {

    void save(ZsMapDrawBO zsMapDrawBO);

    List<ZsMapDrawResBO> select(ZsMapDrawQueryBO zsMapDrawQueryBO);

    int update(@Param("drawId") String drawId);

    int updateZsmapDraw(ZsMapDrawBO zsMapDrawBO);
    
    /**
     * 更新绘制区域的地址信息
     * @param drawId 绘制ID
     * @param address 地址信息
     * @return 更新行数
     */
    int updateAddress(@Param("drawId") String drawId, @Param("address") String address);

}
