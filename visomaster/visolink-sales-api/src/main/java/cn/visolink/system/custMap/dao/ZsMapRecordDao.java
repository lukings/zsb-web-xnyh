package cn.visolink.system.custMap.dao;

import cn.visolink.system.custMap.bo.PermissionsRecordQueryBO;
import cn.visolink.system.custMap.bo.ZsMapPermissionsRecordBO;
import cn.visolink.system.custMap.bo.ZsMapRecordBO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @Author lishumao
 * @Description 客户地图权限记录dao
 * @Date 2024/8/15 10:49
 **/
public interface ZsMapRecordDao {

    void save(ZsMapRecordBO zsMapRecordBO);

    List<ZsMapPermissionsRecordBO> getZsMapPermissionsRecord(
        PermissionsRecordQueryBO permissionsRecordQueryBO);

    List<String> getAccountIds(@Param("operator") String operator);
}
