package cn.visolink.system.builddynamic.mapper;

import cn.visolink.system.builddynamic.model.BuildBookDynamic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/12/18
 */
@Mapper
public interface BuildDynamicMapper {

    /***
    *
     * @param buildBookDynamic
    *@return {}
    *@throws
    *@Description: 保存楼盘动态
    *@author FuYong
    *@date 2020/12/21 11:52
    */
    int saveBuildBookDynamic(BuildBookDynamic buildBookDynamic);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 查询楼盘动态
    *@author FuYong
    *@date 2020/12/21 11:53
    */
    List<BuildBookDynamic> getBuildBookDynamicList(Map map);

    /***
    *
     * @param id
    *@return {}
    *@throws
    *@Description: 查询楼盘动态详情
    *@author FuYong
    *@date 2020/12/22 11:13
    */
    BuildBookDynamic getBuildBookDynamicById(String id);

    /***
    *
     * @param buildBookDynamic
    *@return {}
    *@throws
    *@Description: 修改楼盘动态
    *@author FuYong
    *@date 2020/12/21 11:53
    */
    int editBuildBookDynamic(BuildBookDynamic buildBookDynamic);

    /***
    *
     * @param projectId
    *@return {}
    *@throws
    *@Description: 根据项目id查楼盘
    *@author FuYong
    *@date 2020/12/21 17:01
    */
    List<Map> getBuildListByPojId(@Param("projectId") List<String> projectId);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 查询通用字典值
    *@author FuYong
    *@date 2020/12/21 16:34
    */
    List<Map> getBuildDynamicDictList(Map map);

    /***
     *
     * @param cityId
     *@return {}
     *@throws
     *@Description: 查询新闻
     *@author FuYong
     *@date 2020/12/21 17:01
     */
    List<Map> getNewsListByCityId(@Param("cityId") String cityId);

    /***
     *
     * @param bookId
     *@return {}
     *@throws
     *@Description: 查询户型
     *@author FuYong
     *@date 2020/12/21 17:01
     */
    List<Map> getHouseListByBookId(@Param("bookId") String bookId);

    /***
     *
     * @param projectId
     *@return {}
     *@throws
     *@Description: 查询活动
     *@author FuYong
     *@date 2020/12/21 17:01
     */
    List<Map> getActivityListByPojId(@Param("projectId") String projectId);
}
