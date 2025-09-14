package cn.visolink.system.allpeople.contentManagement.dao;

import cn.visolink.system.allpeople.contentManagement.model.*;
import cn.visolink.system.parameter.model.Dictionary;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/1/17 14:10
 **/
public interface ContentDao {

    /**
     * @Author wanggang
     * @Description //获取图片
     * @Date 16:30 2020/1/17
     * @Param [map]
     * @return java.util.List<cn.visolink.system.allpeople.contentManagement.model.BuildingPhoto>
     **/
    List<BuildingPhoto> getBuildingPhotos(Map map);

    /**
     * @Author wanggang
     * @Description //获取图片(排序用)
     * @Date 16:30 2020/1/17
     * @Param [map]
     * @return java.util.List<cn.visolink.system.allpeople.contentManagement.model.BuildingPhoto>
     **/
    List<BuildingPhoto> getBuildingPhotosOrder(Map map);
    /**
     * @Author wanggang
     * @Description //保存图片
     * @Date 16:31 2020/1/17
     * @Param [buildingPhoto]
     * @return void
     **/
    void addBuildingPhoto(BuildingPhoto buildingPhoto);

    /**
     * 获取原图片路径
     * @param ID
     * @return
     */
    String getOldPhoto(@Param("ID") String ID);

    /**
     * 更新轮播图片
     * @param buildingPhoto
     */
    void updateBuildingPhoto(BuildingPhoto buildingPhoto);

    /**
     * 获取新闻列表
     * @param map
     * @return
     */
    List<News> getNewsList(Map map);

    /**
     * 新增新闻
     * @param news
     */
    void addNews(News news);

    /**
     * 修改新闻
     * @param news
     */
    void updateNews(News news);

    /**
     * 获取原始图片路径
     * @param ID
     * @return
     */
    String getOldPhotoNews(@Param("ID") String ID);

    /**
     * 获取旭客家反馈列表
     * @param map
     * @return
     */
    List<Feedback> getFeedbackList(Map map);


    /**
     * 获取旭客汇反馈列表
     * @param map
     * @return
     */
    List<Feedback> getAllFeedbackList(Map map);
    /**
     * 获取反馈详情
     * @param map
     * @return
     */
    Feedback getFeedbackDetail(Map map);
    /**
     * 反馈处理
     * @param dataMap
     * @return
     */
    void updateFeedback(Map dataMap);
    /**
     * 获取楼书列表
     * @param map
     * @return
     */
    List<BuildingBook> getAllBuilding(Map map);

    /**
     * 根据楼盘ID查询轮播图片
     * @return
     */
    List<BuildingPhoto> getBuildingPhotoList(@Param("BuildBookID") String BuildBookID);

    /**
     * 根据楼盘ID查询楼盘视频
     * @return
     */
    List<BuildingPhoto> getBuildingPhotoVo(@Param("BuildBookID") String BuildBookID);
    /**
     * 根据楼盘ID查询楼盘数据
     * @return
     */
    BuildingBook getBuildingBook(@Param("BuildBookID") String BuildBookID);
    /**
     * 根据楼盘ID查询户型
     * @return
     */
    List<Apartment> getBuildingApartmentList(Map map);

    /**
     * 根据楼盘ID查询户型
     * @return
     */
    List<Map> getBuildingApartmentAll(Map map);
    /**
     * 保存楼盘信息
     * @param buildingBook
     */
    void addBuildingBook(BuildingBook buildingBook);

    /**
     * 更新楼盘信息
     * @param buildingBook
     */
    void updateBuildingBook(BuildingBook buildingBook);

    /**
     * 保存楼盘轮播图片、视频
     * @param buildingPhoto
     */
    void addBuildingPhAndVi(BuildingPhoto buildingPhoto);

    /**
     * 更新楼盘轮播图片、视频
     * @param buildingPhoto
     */
    void updateBuildingPhAndVi(BuildingPhoto buildingPhoto);

    /**
     * 删除楼盘轮播图片、视频
     * @param IDs
     */
    void delBuildingPhAndVi(@Param("IDs") String IDs);
    /**
     * 保存户型
     * @param apartment
     */
    void addBuildingApartment(Apartment apartment);

    /**
     * 更新户型
     * @param apartment
     */
    void updateBuildingApartment(Apartment apartment);

    /**
     * 获取户型大类
     * @return
     */
    List<Dictionary> getBuildingHXD();

    /**
     * 获取户型子类
     * @param map
     * @return
     */
    List<Dictionary> getBuildingHXZ(Map map);

    /**
     * 获取当前登陆人拥有的项目
     * @param map
     * @return
     */
    List<Map> getUserProjects(Map map);

    /**
     * 获取区域和城市
     * @param projectId
     * @return
     */
    Map getCityAndBelongArea(@Param("projectId") String projectId);

    /**
     * 获取推广活动
     * @param map
     * @return
     */
    List<Extension> getExtensionList(Map map);

    /**
     * 添加推广活动
     * @param extension
     */
    void addExtension(Extension extension);

    /**
     * 更新推广活动
     * @param extension
     */
    void updateExtension(Extension extension);

    /**
     * 获取项目名称
     * @param ID
     * @return
     */
    String getProNameByID(@Param("ID") String ID);

    /**
     * 根据岗位查询城市
     * @param JobIDs
     * @return
     */
    List<Map> getCityByJobId(@Param("JobIDs") String JobIDs);

    /**
     * 根据用户ID查询岗位
     * @param userId
     * @return
     */
    List<Map> getAllJobs(@Param("userId") String userId);

    /**
     * 查询所有城市
     * @return
     */
    List<Map> getAllCitys();

    /**
     * 查询是否是系统管理员
     * @param JobID
     * @return
     */
    String isAdmin(@Param("JobID") String JobID);

    /**
     * 获取新闻详情
     * @param map
     * @return
     */
    News getNewsDetail(Map map);

    /**
     * 获取装修标准
     * @return
     */
    List<Map> getZXBZ();

    /**
     * 删除户型
     * @param ids
     */
    void delBuildingApartment(@Param("ids") String ids);

    /**
     * 获取项目佣金比例
     * @param projectID
     * @return
     */
    Map getCommissionRate(@Param("projectID") String projectID);

    /**
     * 获取项目楼盘
     * @param map
     * @return
     */
    String getBuildingByProId(Map map);

    /**
     * 获取项目编号和活动ID
     * @param ProjectID
     * @return
     */
    String getProNumId(@Param("ProjectID") String ProjectID);

    /**
     * @Author wanggang
     * @Description //查询海报图片列表
     * @Date 14:53 2020/3/12
     * @Param
     * @return
     **/
    List<BuildingPoster> getBuildingPosterList(Map map);
    /**
     * @Author wanggang
     * @Description //添加海报图片
     * @Date 15:08 2020/3/12
     * @Param [buildingPoster]
     * @return void
     **/
    void addBuildingPoster(BuildingPoster buildingPoster);
    /**
     * @Author wanggang
     * @Description //修改海报图片
     * @Date 15:08 2020/3/12
     * @Param [buildingPoster]
     * @return void
     **/
    void updateBuildingPoster(BuildingPoster buildingPoster);
    /**
     * @Author wanggang
     * @Description //删除海报图片
     * @Date 15:10 2020/3/12
     * @Param [id]
     * @return void
     **/
    void delBuildingPoster(@Param("ID") String id);

    /**
     * @param map
     * 保存二维码数据
     */
    void addBookPoster(Map map);

    /**
     * @param CityId
     * 根据城市ID查询楼盘
     * @return
     */
    List<Map> getBuildingByCityId(@Param("CityId") String CityId);

    /**
     * @param CityId
     * 根据城市ID查询新闻
     * @return
     */
    List<Map> getNewsByCityId(@Param("CityId") String CityId);

    /**
     * @param map
     * 根据城市ID查询楼盘
     * @return
     */
    List<BuildingBook> getBuildingOrder(Map map);

    /**
     * @param buildingBook
     * 更新楼盘数据
     * @return
     */
    void updateBuildingOrder(BuildingBook buildingBook);

    /**
     * 保存预售证号
     * @param preSalePermit
     * @return
     */
    void addPreSalePermit(PreSalePermit preSalePermit);

    /**
     * 查询预售证号
     * @param buildBookID
     * @return
     */
    List<PreSalePermit> getPreSalePermits(@Param("BuildBookID") String buildBookID);

    /**
     * 更新预售证号
     * @param preSalePermit
     * @return
     */
    void updatePreSalePermit(PreSalePermit preSalePermit);

    /**
     * 删除预售证号
     * @param ids
     * @return
     */
    void delPreSalePermits(@Param("ids") String ids);

    /**
     * 查询当前城市最后排序号
     * @param CityID
     * @return
     */
    String getBuildListIndexByCityId(@Param("CityID") String CityID);

    /**
     * 查询楼盘问题列表
     * @param map
     * @return
     */
    Page<BuildBookProblem> getBuildBookProblemList(Map map);

    /**
     * 保存问题
     * @param map
     * @return
     */
    int saveBuildBookProblem(Map map);

    /**
     * 查询排序数
     * @param buildBookID
     * @return
     */
    Integer getBuildBookProblemListIndex(String buildBookID);

    /**
     * 更新问题
     * @param map
     * @return
     */
    int updateBuildBookProblem(Map map);

    /**
     * 更新排序
     * @param mapList
     * @return
     */
    int updateBuildBookProblemListIndex(@Param("mapList") List<Map> mapList);

    /**
     * 查询楼盘
     * @param map
     * @return
     */
    List<Map> getBuildBookList(Map map);

    /**
     * 查询问题(排序使用)
     * @param buildBookId
     * @return
     */
    List<BuildBookProblem> getBuildBookProblemListByProjectId(String buildBookId);

    /**
     * 查询城市关联活动
     * @param cityId
     * @return
     */
    List<Map> getActivityByCityId(String cityId);

    /**
     * 查询弹窗是否可配置
     * @param map
     * @return
     */
    String getIsOkCity(Map map);

    /**
     * 查询问题数量
     * @param buildBookID
     * @return
     */
    Integer getBookProblemNum(String buildBookID);

    /**
     * 查询楼盘信息
     * @param proIds
     * @return
     */
    List<Map> getBuildingByProIds(@Param("proIds") List<String> proIds);

    /**
     * 查询新闻
     * @param cityIds
     * @return
     */
    List<Map> getNewsByCityIds(@Param("cityIds") List<String> cityIds);

    /**
     * 查询活动
     * @param proIds
     * @return
     */
    List<Map> getActivityByProIds(@Param("proIds") List<String> proIds);

    /**
     * 更新轮播图、弹窗状态
     * @param map
     * @return
     */
    void updateBuildingPhotoStatus(Map map);

    /***
    *
     * @param buildBookCityList
    *@return {}
    *@throws
    *@Description: 保存楼盘城市数据
    *@author FuYong
    *@date 2020/8/21 17:11
    */
    int saveBuildBookCity(@Param("buildBookCityList") List<BuildBookCity> buildBookCityList);

    /***
    *
     * @param buildBookId
    *@return {}
    *@throws
    *@Description: 删除楼盘城市数据
    *@author FuYong
    *@date 2020/8/21 17:12
    */
    int deleteBuildBookCity(@Param("buildBookId") String buildBookId);

    /***
    *
     * @param buildBookId
    *@return {}
    *@throws
    *@Description: 查询周边城市
    *@author FuYong
    *@date 2020/8/24 16:35
    */
    List<String> getBuildBookCity(@Param("buildBookId") String buildBookId);

    /***
    *
     * @param cityIdList
    *@return {}
    *@throws
    *@Description: 根据id 查询城市数据
    *@author FuYong
    *@date 2020/8/24 11:42
    */
    List<Map> getCityListById(@Param("cityIdList") List<String> cityIdList);

    /**
     * @param buildingBook
     * 更新楼盘数据
     * @return
     */
    void updateBuildCityOrder(BuildingBook buildingBook);

    /***
    *
     * @param cityId
    *@return {}
    *@throws
    *@Description: 查询最大排序
    *@author FuYong
    *@date 2020/8/27 16:58
    */
    Integer getBuildBookCityListIndex(@Param("cityId") String cityId);

    List<Map> getAllProject();

    List<String> getFeedBackImgList(@Param("feedbackId") String feedbackId);
    /**
     * @Author wanggang
     * @Description //查询排序新闻
     * @Date 10:33 2020/10/29
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getNewsOrder(Map map);
    /**
     * @Author wanggang
     * @Description //更新新闻排序
     * @Date 10:34 2020/10/29
     * @Param [map]
     * @return void
     **/
    void updateNewsOrder(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 10:31 2020/11/16
     * @Param [BuildBookID]
     * @return cn.visolink.system.allpeople.contentManagement.model.BuildingPhoto
     **/
    List<BuildingPhoto> getBuildingVR(@Param("BuildBookID") String BuildBookID);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:43 2020/11/17
     * @Param [buildBookId, adType]
     * @return java.util.List<java.util.Map>
     **/
    List<BuildBookPeriphery> getPeriphery(@Param("buildBookId") String buildBookId,@Param("adType") String adType);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:46 2020/11/17
     * @Param [list]
     * @return void
     **/
    void addPeriphery(@Param("list") List<BuildBookPeriphery> list);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:47 2020/11/17
     * @Param [buildBookId, adType]
     * @return void
     **/
    void delPeriphery(@Param("buildBookId") String buildBookId,@Param("adType") String adType);

    /**
     * 获取楼栋业态信息
     * @return
     */
    List<Map> getBuildingProperty(@Param("buildBookId") String buildBookId);

    /**
     * 保存楼栋业态信息
     * @return
     */
    void addBuildingProperty(@Param("list") List<BuildingProperty> list);

    /**
     * 删除楼栋业态信息
     * @return
     */
    void delBuildingProperty(String ids);

    /**
     * 更新楼栋业态信息
     * @return
     */
    void updateBuildingProperty(BuildingProperty buildingProperty);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:54 2020/10/20
     * @Param []
     * @return java.util.List<cn.visolink.system.allpeople.contentManagement.model.DictDesc>
     **/
    List<DictDesc> getBuildingMainDict();

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:55 2020/10/20
     * @Param [pid]
     * @return java.util.List<cn.visolink.system.allpeople.contentManagement.model.DictDesc>
     **/
    List<DictDesc> getBuildingSubDict(String pid);

    /**
     * 根据主项目ID获取分期项目
     * @param projectId
     * @return
     */
    List<String> getProjects(String projectId);
    /**
     * 获取未维护周边的楼盘
     * @param
     * @return
     */
    List<Map> getBuildBookNotPeriphery();
    /**
     * 查询户型轮播图/视频
     * @param map
     * @return
     */
    List<Map> getHouseImg(Map map);
    /**
     * 添加户型轮播图/视频
     * @param list
     * @return
     */
    void addHouseImg(@Param("list") List<HouseImg> list);
    /**
     * 删除户型轮播图/视频
     * @param ids
     * @return
     */
    void delHouseImg(@Param("ids") String ids);
    /**
     * 根据户型ID删除户型轮播图/视频
     * @param id
     * @return
     */
    void delHouseImgByHouseId(@Param("id") String id);
    /**
     * 查询标签
     * @param map
     * @return
     */
    List<Map> getHouseTag(Map map);
    /**
     * 添加户型标签
     * @param list
     * @return
     */
    void addHouseTag(@Param("list") List<BuildBookTag> list);
    /**
     * 更新户型标签
     * @param list
     * @return
     */
    void updateHouseTag(@Param("list") List<BuildBookTag> list);
    /**
     * 删除户型标签
     * @param ids
     * @return
     */
    void delHouseTag(@Param("ids") String ids);
    /**
     * 根据户型ID删除户型标签
     * @param id
     * @return
     */
    void delHouseTagByHouseId(@Param("id") String id);
    /**
     * 查询标签分析信息
     * @param map
     * @return
     */
    List<Map> getHouseAnalysis(Map map);
    /**
     * 添加标签分析信息
     * @param list
     * @return
     */
    void addHouseAnalysis(@Param("list") List<HouseAnalysis> list);
    /**
     * 更新标签分析信息
     * @param ids
     * @return
     */
    void updateHouseAnalysis(@Param("list") List<HouseAnalysis> ids);
    /**
     * 删除标签分析信息
     * @param ids
     * @return
     */
    void delHouseAnalysis(@Param("ids") String ids);
    /**
     * 根据户型ID删除户型标签分析信息
     * @param id
     * @return
     */
    void delHouseAnalysisByHouseId(@Param("id") String id);
    /**
     * @Author wanggang
     * @Description //获取户型标签
     * @Date 16:44 2020/11/26
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getHxTag();

    /**
     * @Author wanggang
     * @Description //获取户型朝向
     * @Date 16:44 2020/11/26
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getHXCX();

    /**
     * @Author wanggang
     * @Description //获取楼盘标签
     * @Date 16:44 2020/11/26
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getLpTag();

    /**
     * @Author wanggang
     * @Description //删除户型详情
     * @Date 16:44 2020/11/26
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    void delHouseDetailPhoto(String id);

    /**
     * @Author wanggang
     * @Description //启用禁用户型
     * @Date 16:44 2020/11/26
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    void updateBuildingApartmentStatus(Map map);

    /**
     * @Author wanggang
     * @Description //获取户型详情
     * @Date 11:10 2020/11/27
     * @Param [ID]
     * @return java.util.Map
     **/
    Map getBuildingApartmentDetail(String ID);
    /**
     * @Author wanggang
     * @Description //获取楼盘户型
     * @Date 14:43 2020/11/30
     * @Param [buildBookId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getBuildHouse(String buildBookId);
    /**
     * @Author wanggang
     * @Description //是否可删除HX
     * @Date 21:01 2020/12/3
     * @Param [id]
     * @return java.lang.String
     **/
    String isOkDelHX(String id);
    /**
     * @Author wanggang
     * @Description //获取户型详情
     * @Date 21:32 2020/12/3
     * @Param [id]
     * @return java.util.Map
     **/
    Map getHXDetail(String id);
    /**
     * @Author wanggang
     * @Description //获取推广码新
     * @Date 15:38 2020/12/7
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getExtenListNew(Map map);
    /**
     * @Author wanggang
     * @Description //获取推广码导出
     * @Date 15:38 2020/12/7
     * @Param [map]
     * @return java.util.List
     **/
    List<Extension> getExtenListNewExprot(Map map);
    /**
     * @Author wanggang
     * @Description //查询所有项目
     * @Date 15:08 2020/12/9
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAllPro();
    /**
     * @Author wanggang
     * @Description //根据权限查询项目
     * @Date 15:08 2020/12/9
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProByUserName(String where);
    /**
     * @Author wanggang
     * @Description //查询所有区域
     * @Date 15:08 2020/12/9
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAllQy();
    /**
     * @Author wanggang
     * @Description //根据账号查询区域
     * @Date 15:08 2020/12/9
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getQyByUserName(String userName);
    /**
     * @Author wanggang
     * @Description //获取权限级别
     * @Date 15:08 2020/12/9
     * @Param [userName]
     * @return java.lang.String
     **/
    String getOrgLevel(String userName);
    /**
     * @Author wanggang
     * @Description //获取区域下项目
     * @Date 18:15 2020/12/9
     * @Param [FullPath]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProByQyFullPath(String FullPath);
    /**
     * @Author wanggang
     * @Description //获取推广码注册人数
     * @Date 17:56 2020/12/12
     * @Param [ID]
     * @return int
     **/
    int getRegisterCnt(String ID);
    /**
     * @Author wanggang
     * @Description //删除推广码
     * @Date 17:56 2020/12/12
     * @Param [map]
     * @return void
     **/
    void updateExtenDel(Map map);
    /**
     * @Author wanggang
     * @Description //获取城市
     * @Date 17:56 2020/12/12
     * @Param [userName]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getCityByOrgId(String userName);
    /**
     * @Author wanggang
     * @Description //获取活动所属项目
     * @Date 17:56 2020/12/12
     * @Param [activityId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getActivityPros(String activityId);
    /**
     * @Author wanggang
     * @Description //获取项目组织ID
     * @Date 19:23 2020/12/12
     * @Param [list]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getProOrgIds(Map map);
    /**
     * @Author wanggang
     * @Description //获取上级组织信息
     * @Date 19:25 2020/12/12
     * @Param [id]
     * @return java.util.Map
     **/
    Map getParentOrg(String id);
    /**
     * @Author wanggang
     * @Description //获取微信token
     * @Date 14:11 2020/12/14
     * @Param []
     * @return java.lang.String
     **/
    String getWXToken();
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 14:48 2020/12/23
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getLpPhotoType();
    /**
     * @Author wanggang
     * @Description //获取楼盘图片类别
     * @Date 15:25 2020/12/23
     * @Param [BuildBookID]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getBuildingPhotoTypes(String BuildBookID);
    /**
     * @Author wanggang
     * @Description //获取组织名称
     * @Date 17:56 2020/12/24
     * @Param [extenOrgId]
     * @return java.lang.String
     **/
    String getOrgNameByOrgId(String id);

    List<String> checkDep(@Param("cityID") String cityID,
                          @Param("beginTime")String beginTime, @Param("endTime")String endTime);

    Integer getCondictionCount(@Param("cityID")String cityID,
                               @Param("beginTime")String beginTime, @Param("endTime")String endTime,
                               @Param("id") String id);


    /**
     * @Author zhaohongen
     * @Description //上传app pad端启动图片
     * @Date 2021/04/20
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    Integer startupPictureDeploy(StartupPagePicture startupPagePicture);


    /**
     * @Author zhaohongen
     * @Description //修改app pad端启动图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    Integer updateStartupPicture(StartupPagePicture startupPagePicture);

    /**
     * @Author zhaohongen
     * @Description //删除app pad端启动图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    Integer delStartupPicture(StartupPagePicture startupPagePicture);

    /**
     * @Author zhaohongen
     * @Description //查询app pad端启动图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    List<BuildingPhoto> startupPictureList(StartupPagePicture startupPagePicture);

    /**
     * @Author zhaohongen
     * @Description //查询指定端是否有已启用图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    List<String> startupPictureListByStatus(StartupPagePicture startupPagePicture);

    /**
     * @Author zhaohongen
     * @Description //修改状态app pad端启动图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
    Integer statusStartupPicture(Map map);


    /**
     * @Author zhaohongen
     * @Description //根据id查询app pad端启动图片
     * @Date 2021/04/21
     * @Param StartupPagePicture
     * @return cn.visolink.exception.ResultBody
     **/
        BuildingPhoto startupPictureById(@Param("id") String id);


    /**
     * @Author zht
     * @param map
     * @Description 根据城市ID查询楼盘
     * @Date 2021/06/09
     * @return cn.visolink.exception.ResultBody
     */
    List<BuildingBook> getNewsBuilding(Map map);


    /**
     * @Author zht
     * @Description 获取新闻类型
     * @Date 2021/06/09
     * @return cn.visolink.exception.ResultBody
     */
    List<Dictionary> getNewsType();


    /***
     *
     * @param newsBuildBookList
     *@return {}
     *@throws
     *@Description: 保存楼盘城市数据
     *@author FuYong
     *@date 2020/8/21 17:11
     */
    int saveNewsBuildBook(@Param("newsBuildBookList") List<NewsBuildBook> newsBuildBookList);

    /***
     *
     * @param newsId
     *@return {}
     *@throws
     *@Description: 删除楼盘城市数据
     *@author FuYong
     *@date 2020/8/21 17:12
     */
    int deleteNewsBuildBook(@Param("newsId") String newsId);

    /**
     * @Author zht
     * @Description 获取城市行政区
     * @Date 2021/06/15
     * @return Map
     */
    List<String> getDistrict(@Param("cityName") String cityName);


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
    void delBuildingProblem(@Param("ID") String id);

    /**
     *  添加首页热推图片
     * @param map 热推图片参数
     */
    void addHotHomePage(Map map);

    /**
     *  更新首页热推图片
     * @param map 更新参数
     */
    void updateHotHomePage(Map map);

    /**
     *  更新首页热推状态
     * @param map 更新参数
     */
    void updateHotHomePageStatus(Map map);

    /**
     *  删除首页热推
     * @param id 主键id
     * @return
     */
    void delHotHomePageImg(String id);

    /**
     *  查询首页热推列表
     * @param map
     * @return
     */
    List<Map> getHotImgList(Map map);

    /**
     *  查询楼盘热卖标签字典值
     * @return
     */
    List<Map> getBuildBookDic();

    /**
     *  根据主键查询热播图信息
     * @param id 主键id
     * @return
     */
    Map getHotHomePaeById(String id);

    /**
     *  更新活动热推状态
     * @param activityId 活动id
     * @param isHomePageHot
     */
    void updateActivityHotStatus(@Param("activityId") String activityId, @Param("isHomePageHot") String isHomePageHot);

    /**
     *  根据项目id 查询到涉及的城市
     * @return
     */
    List<Map> getCityByPro(String ids1);

    void updateActivityStatus(String id);

    Integer getHotCount(String id);

    /**
     *  查询多个城市名称拼接
     * @param cityIds 城市id集合
     * @return
     */
    String getConcatCityName(@Param("cityIds") List<String> cityIds);

    List<Map> getActAllCityIds();

    List<Map> getActivityByIds(@Param("actIdList") List actIdList);

    void  updateActivityStatusById(@Param("activityId") String activityId);

    /**
     *  更新启动页其余状态为禁用
     * @param id 修改的启动页id
     * @param port
     */
    void updateStartupPictureToDisabled(@Param("id") String id, @Param("port") String port);
    /**
     * @Author wanggang
     * @Description //保存楼盘产品
     * @Date 22:43 2022/4/18
     * @Param [build]
     * @return void
     **/
    void addBuildBookProduct(BuildBookProduct build);
    /**
     * @Author wanggang
     * @Description //更新楼盘产品
     * @Date 23:02 2022/4/18
     * @Param [build]
     * @return void
     **/
    void updateBuildBookProduct(BuildBookProduct build);
    /**
     * @Author wanggang
     * @Description //获取产品户型
     * @Date 23:24 2022/4/18
     * @Param [id]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getHXDetailByProduct(String id);

    void delBuildBookProduct(String id);
    /**
     * @Author wanggang
     * @Description //获取楼盘产品
     * @Date 0:06 2022/4/19
     * @Param [buildBookID]
     * @return java.util.List<cn.visolink.system.allpeople.contentManagement.model.BuildBookProduct>
     **/
    List<BuildBookProduct> getBuildBookProduct(String buildBookID);
    /**
     * @Author wanggang
     * @Description //获取产品分类
     * @Date 10:51 2022/4/19
     * @Param [projectId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProductsDict(String projectId);
    /**
     * @Author wanggang
     * @Description //获取城市根据项目
     * @Date 11:34 2022/6/10
     * @Param [ProIDs]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getCityByProS(@Param("list") List<String> ProIDs);
    /**
     * @Author wanggang
     * @Description //删除楼盘标签
     * @Date 16:11 2022/7/14
     * @Param [buildBookID]
     * @return void
     **/
    void delBuildTag(String id);
    /**
     * @Author wanggang
     * @Description //删除楼盘产品及户型
     * @Date 16:25 2022/7/14
     * @Param [buildBookID]
     * @return void
     **/
    void delBuildProducts(String buildBookID);
}
