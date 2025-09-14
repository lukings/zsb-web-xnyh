package cn.visolink.system.custMap.dao;

import cn.visolink.system.custMap.bo.ZsMapPermissionsBO;
import cn.visolink.system.custMap.bo.ZsMapPermissionsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author lishumao
 * @Description 客户地图权限dao
 * @Date 2024/8/15 10:49
 **/
public interface ZsMapPermissionsDao {

    List<ZsMapPermissionsBO> getMapPermissions(@Param("accountIds") List<String> accountId);

    void save(ZsMapPermissionsBO zsMapPermissionsBO);

    Integer update(ZsMapPermissionsBO zsMapPermissionsBO);


    Integer deletePermissionsById(ZsMapPermissionsVO permissionsVO);

    ZsMapPermissionsBO query(@Param("permissionsId") String permissionsId);
}
