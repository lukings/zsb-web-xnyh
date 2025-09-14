package cn.visolink.system.timetask;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yangjie
 * @date 2020-9-17
 */
@Mapper
@Repository
public interface SyncDataTaskMapper {

    /**
     * 获取外部项目同步数据
     *
     * @return return
     */
    List<Map<String, Object>> getExternalProjectList();

    /**
     * 获取内部项目数据
     *
     * @return
     */
    List<Map<String, Object>> getInsideProjectList();

    /**
     *  获取内部未删除的项目数据
     *
     * @return
     */
    Long getNotDeletedInsideProjectList();

    /**
     * 插入项目
     *
     * @param list list
     * @return return
     */
    int insertBatchProject(List<Map> list);

    /**
     * 更新项目
     *
     * @param list list
     * @return return
     */
    int updateBatchProject(List<Map> list);

    /**
     * 获取外部组织同步数据
     *
     * @return return
     */
    List<Map<String, Object>> getExternalOrganizationList();

    /**
     * 获取内部组织数据
     *
     * @return
     */
    List<Map<String, Object>> getInsideOrganizationList();

    /**
     * 插入组织
     *
     * @param list list
     * @return return
     */
    int insertBatchOrganization(List<Map> list);

    /**
     * 更新组织
     *
     * @param list list
     * @return return
     */
    int updateBatchOrganization(List<Map> list);

    /**
     * 获取外部用户同步数据
     *
     * @return return
     */
    List<Map<String, Object>> getExternalUserList();

    /**
     * 获取内部用户数据
     *
     * @return
     */
    List<Map<String, Object>> getInsideUserList();

    /**
     * 插入用户
     *
     * @param list list
     * @return return
     */
    int insertBatchUser(List<Map> list);

    /**
     * 更新用户
     *
     * @param list list
     * @return return
     */
    int updateBatchUser(List<Map> list);

    /**
     * 获取外部字典同步数据
     *
     * @return return
     */
    List<Map<String, Object>> getExternalDictionaryList();

    /**
     * 获取内部字典数据
     *
     * @return
     */
    List<Map<String, Object>> getInsideDictionaryList();

    /**
     * 插入字典
     *
     * @param list list
     * @return return
     */
    int insertBatchDictionary(List<Map> list);

    /**
     * 更新字典
     *
     * @param list list
     * @return return
     */
    int updateBatchDictionary(List<Map> list);

    /**
     * 获取组织全路径
     *
     * @return return
     */
    List<Map<String, Object>> getOrganizationFullPath();

    /**
     * 批量更新组织 全路径
     *
     * @param newOrganizationList newOrganizationList
     * @return return
     */
    int updateBatchOrganizationFullPath(List<Map<String, Object>> newOrganizationList);
}
