package cn.visolink.system.allpeople.contentManagement.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.allpeople.contentManagement.model.*;
import cn.visolink.system.parameter.model.Dictionary;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/1/17 14:09
 **/
public interface ContentService {

    PageInfo<BuildingPhoto> getBuildingPhotos(Map map);

    ResultBody addBuildingPhoto(Map map);

    PageInfo<News> getNewsList(Map map);

    ResultBody addOrEidtNews(Map map);

    PageInfo<Feedback> getFeedbackList(Map map);

    PageInfo<Feedback> getFeedback(Map map);

    Feedback getFeedbackDetail(Map map);

    ResultBody handleFeedback(Map map);

    PageInfo<BuildingBook> getAllBuilding(Map map);

    ResultBody addOrEditBuilding(Map map);

    BuildingBook getBuildingDetail(Map map);

    List<Dictionary> getBuildingHXD();

    List<Dictionary> getBuildingHXZ(Map map);

    List<Map> getUserProjects(Map map);

    Map getCityAndBelongArea(Map map);

    List<Extension> getExtensionList(Map map);

    ResultBody addOrEditExtension(Map map);

    void feedbackExport(HttpServletRequest request, HttpServletResponse response, Map map);

    void feedbackHuiExport(HttpServletRequest request, HttpServletResponse response, Map map);


    List<Map> getCitysByJobId(Map map);

    void getWinCode(HttpServletResponse response, Extension extension);

    News getNewsDetail(Map map);

    List<Map> getZXBZ();

    String getBuildingByProId(Map map);

    List<BuildingPoster> getBuildingPosterList(Map map);

    void addBuildingPoster(BuildingPoster buildingPoster);

    void updateBuildingPoster(BuildingPoster buildingPoster);

    void delBuildingPoster(String id);

    ResultBody getPeripheralMatching(Map map);

    ResultBody getBuildingPhotoTO(Map map);

    List<BuildingPhoto> getBuildingPhotosOrder(Map map);

    ResultBody updateBuildingPhotoOrder(Map map);

    List<BuildingBook> getBuildingOrder(Map map);

    ResultBody updateBuildingOrder(Map map);

//    List<String> getBelongAreaByJobId(Map map);
//
//    List<Map> getCitysByBelongArea(Map map);

    /**
     * 查询楼盘问题列表
     * @param map
     * @return
     */
    ResultBody getBuildBookProblemList(Map map);

    /**
     * 保存问题
     * @param map
     * @return
     */
    ResultBody saveBuildBookProblem(Map map);

    /**
     * 更新问题
     * @param map
     * @return
     */
    ResultBody updateBuildBookProblem(Map map);

    /**
     * 更新排序
     * @param map
     * @return
     */
    ResultBody updateBuildBookProblemListIndex(Map map);

    /**
     * 查询楼盘
     * @param map
     * @return
     */
    ResultBody getBuildBookList(Map map);

    /**
     * 查询问题(排序使用)
     * @param buildBookId
     * @return
     */
    ResultBody getBuildBookProblemListByProjectId(String buildBookId);

    /**
     * 判断城市是否可配置弹窗图
     * @param map
     * @return
     */
    ResultBody getIsOkCity(Map map);

    /**
     * 查询问题数量
     * @param buildBookID
     * @return
     */
    ResultBody getBookProblemNum(String buildBookID);

    /**
     * 楼盘常见问题导出
     * @param request
     * @param response
     * @param param
     */
    void bookProblemNumExport(HttpServletRequest request, HttpServletResponse response, String param);
    /**
     * 更新轮播图/弹窗状态
     * @param map
     * @return
     */
    ResultBody updateBuildingPhotoStatus(Map map);

    /***
    *
     * @param
    *@return {}
    *@throws
    *@Description: 获取所有城市
    *@author FuYong
    *@date 2020/8/24 10:04
    */
    ResultBody getAllCityList(String cityId);

    /***
    *
     * @param
    *@return {}
    *@throws
    *@Description: 获取所有项目
    *@author FuYong
    *@date 2020/9/3 17:47
    */
    ResultBody getAllProjectList();

    /**
     * @Author wanggang
     * @Description //删除素材调用
     * @Date 9:23 2020/9/14
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody deleteBuildBookPhotos(Map map);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 10:18 2020/10/29
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getNewsOrder(Map map);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 10:18 2020/10/29
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateNewsOrder(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 19:31 2020/11/17
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    List<BuildBookPeriphery> getOldPeripheralMatching(Map map);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 18:23 2020/11/19
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getBuildBookProperty(Map map);

    /**
     * @Author wanggang
     * @Description //初始化周边配套
     * @Date 15:36 2020/11/24
     * @Param []
     * @return java.lang.String
     **/
    String addAllBuildBookPeripheralMatching();

    /**
     * @Author wanggang
     * @Description //获取户型标签
     * @Date 15:36 2020/11/24
     * @Param []
     * @return java.lang.String
     **/
    List<Map> getHxTag();

    /**
     * @Author wanggang
     * @Description //获取楼盘标签
     * @Date 15:36 2020/11/24
     * @Param []
     * @return java.lang.String
     **/
    List<Map> getLpTag();

    /**
     * @Author wanggang
     * @Description //获取户型朝向
     * @Date 15:36 2020/11/24
     * @Param []
     * @return java.lang.String
     **/
    List<Map> getHXCX();

    /**
     * @Author wanggang
     * @Description //修改户型状态
     * @Date 15:36 2020/11/24
     * @Param []
     * @return java.lang.String
     **/
    String updateBuildingApartmentStatus(Map map);

    /**
     * @Author wanggang
     * @Description //获取户型详情
     * @Date 15:36 2020/11/24
     * @Param []
     * @return java.lang.String
     **/
    Map getBuildingApartmentDetail(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 16:56 2020/11/30
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getBuildingHX(Map map);
    /**
     * @Author wanggang
     * @Description //获取用户级别
     * @Date 15:20 2020/12/9
     * @Param []
     * @return java.lang.String
     **/
    String getUserOrgLevel();

    /**
     * @Author wanggang
     * @Description //获取用户项目/区域
     * @Date 15:23 2020/12/9
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getExtensionTypeDesc(Map map);
    /**
     * @Author wanggang
     * @Description //获取区域项目选项
     * @Date 9:17 2020/12/10
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getOrgAndPro(Map map);
    /**
     * @Author wanggang
     * @Description //获取推广码新
     * @Date 11:51 2020/12/10
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    PageInfo getExtenListNew(Map map);
    /**
     * @Author wanggang
     * @Description //删除推广码
     * @Date 14:48 2020/12/10
     * @Param [map]
     * @return java.lang.String
     **/
    String delExten(Map map);
    /**
     * @Author wanggang
     * @Description //根据权限获取城市
     * @Date 20:05 2020/12/10
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getCityByOrgId(Map map);

    /**
     * @Author wanggang
     * @Description //添加或修改推广码
     * @Date 10:23 2020/12/11
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addOrEditExten(Map map);
    /**
     * @Author wanggang
     * @Description //查询推广码跳转地
     * @Date 16:21 2020/12/12
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getBuildingExtenTO(Map map);

    /**
     * @Author wanggang
     * @Description //获取活动所属项目
     * @Date 17:59 2020/12/12
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getActivityPros(Map map);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 18:36 2020/12/12
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProOrg(Map map);

    /**
     * @Author wanggang
     * @Description //推广码导出
     * @Date 9:48 2020/12/14
     * @Param [request, response, excelForm]
     * @return void
     **/
    void extenExport(HttpServletRequest request, HttpServletResponse response, String excelForm);

    /**
     * @Author wanggang
     * @Description //获取楼盘图片类型
     * @Date 14:46 2020/12/23
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getLpPhotoType();

    ResultBody checkDep(CheckDep req);

    /**
     * @Author zhaohongen
     * @Description //上传app pad端启动图片
     * @Date 2021/04/20
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody startupPictureDeploy(StartupPagePicture startupPagePicture) throws IOException;

    /**
     * @Author zhaohongen
     * @Description //删除app pad端启动图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody delStartupPicture(StartupPagePicture startupPagePicture);

    /**
     * @Author zhaohongen
     * @Description //修改状态app pad端启动图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody statusStartupPicture(StartupPagePicture startupPagePicture);

    /**
     * @Author zhaohongen
     * @Description //修改app pad端启动图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateStartupPicture(StartupPagePicture startupPagePicture);


    /**
     * @Author zhaohongen
     * @Description //根据主键id查询app pad端启动图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody startupPictureById(String id);

    /**
     * @Author zht
     * @param map
     * @Description 根据城市ID查询楼盘
     * @Date 2021/06/09
     * @return cn.visolink.exception.ResultBody
     */

    List<ResultProjectVO> getNewsBuilding(Map map);

    /**
     * @Author zht
     * @Description 获取新闻类型
     * @Date 2021/06/09
     * @return cn.visolink.exception.ResultBody
     */
    List<Dictionary> getNewsType();

    /**
     * @Author zht
     * @Description 导出新闻列表
     * @Date 2021/06/11
     * @return cn.visolink.exception.ResultBody
     */
    void newsExport(HttpServletRequest request, HttpServletResponse response, Map map);


    /**
     * @Author zht
     * @Description 查询楼盘问题列表
     * @Date  2021/6/16
     * @Param
     * @return
     **/
    List<BuildingProblem> getBuildingProblemList(Map map);
    /**
     * @Author zht
     * @Description 添加楼盘问题
     * @Date 2021/6/16
     * @Param [buildingProblem]
     * @return void
     **/
    void addBuildingProblem(BuildingProblem buildingProblem);
    /**
     * @Author zht
     * @Description 修改楼盘问题
     * @Date 2021/6/16
     * @Param [buildingProblem]
     * @return void
     **/
    void updateBuildingProblem(BuildingProblem buildingProblem);
    /**
     * @Author zht
     * @Description 删除楼盘问题
     * @Date 2021/6/16
     * @Param [id]
     * @return void
     **/
    void delBuildingProblem(String id);


    /**
     *  添加首页热推图
     * @param map 参数
     * @return
     */
    ResultBody addActivityHotHomePage(Map map);

    /**
     *  更新首页热推图
     * @param map 参数
     * @return
     */
    ResultBody updateActivityHotHomePage(Map map);

    /**
     * 更新首页热推状态
     * @param map 更新参数
     * @return
     */
    ResultBody updateHotStatus(Map map);

    /**
     * 删除首页热推图
     * @param id 主键id
     * @return
     */
    ResultBody delHotHomePageImg(String id);

    /**
     * 查询首页热推图列表
     * @param map
     * @return
     */
    ResultBody getHotImgList(Map map);

    /**
     *  查询字典值（楼盘状态）
     * @return
     */
    ResultBody getBuildBookDic();

    /**
     *  根据id查询当前热推图信息
     * @param id 主键id
     * @return
     */
    ResultBody getHotHomePae(String id);

    /**
     *  根据多个城市查询活动
     * @param cityId
     * @return
     */
    ResultBody getActivityByCityId(List<String> cityId);

    /**
     * 根据项目查询涉及城市
     * @param map
     * @return
     */
    ResultBody getCityByPro(Map map);

    /**
     *  校验是否可以配置热推
     * @param map 配置热推参数
     * @return
     */
    ResultBody checkHotConfig(Map map);

    ResultBody getCityListByAciId(Map map);

    List<Map> getProductsDict(String projectId);
}
