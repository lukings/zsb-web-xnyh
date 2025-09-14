package cn.visolink.system.builddynamic.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.builddynamic.model.BuildBookDynamic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/12/18
 */
public interface BuildDynamicService {

    /***
    *
     * @param buildBookDynamic
    *@return {}
    *@throws
    *@Description: 保存楼盘动态
    *@author FuYong
    *@date 2020/12/21 13:37
    */
    ResultBody saveBuildBookDynamic(BuildBookDynamic buildBookDynamic);

    /***
     *
     * @param map
     *@return {}
     *@throws
     *@Description: 保存楼盘动态
     *@author FuYong
     *@date 2020/12/21 13:37
     */
    ResultBody getBuildBookDynamicList(Map map);

    /***
     *
     * @param buildBookDynamic
     *@return {}
     *@throws
     *@Description: 修改楼盘动态
     *@author FuYong
     *@date 2020/12/21 13:37
     */
    ResultBody editBuildBookDynamic(BuildBookDynamic buildBookDynamic);

    /***
    *
     * @param projectId
    *@return {}
    *@throws
    *@Description: 根据项目id查楼盘
    *@author FuYong
    *@date 2020/12/21 17:01
    */
    ResultBody getBuildListByPojId(String projectId);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 查询通用字典值
    *@author FuYong
    *@date 2020/12/21 16:34
    */
    ResultBody getBuildDynamicDictList(Map map);

    /***
     *
     * @param map
     *@return {}
     *@throws
     *@Description: 查询跳转选择数据
     *@author FuYong
     *@date 2020/12/21 16:34
     */
    ResultBody getJumpTypeDataList(Map map);

    /***
     *
     * @param id
     *@return {}
     *@throws
     *@Description: 查询跳转选择数据
     *@author FuYong
     *@date 2020/12/21 16:34
     */
    ResultBody getBuildDynamicById(String id);

    /**
     * 楼盘动态导出
     *
     * @param request request
     * @param response response
     * @param param 楼盘动态导出
     * */
    void getBuildDynamicExcel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> param);

}
