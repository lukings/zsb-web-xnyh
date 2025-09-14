package cn.visolink.system.projectmanager.dao;


import cn.visolink.system.projectmanager.model.*;
import cn.visolink.system.projectmanager.model.requestmodel.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 孙林
 * @date:2019-9-10
 */
@Repository
public interface projectmanagerDao {

    /**
     * 通过父id查询项目
     *
     * @param pid
     * @return
     */
    List<Map> selectProjectByOrgId(String pid);

    /**
     * 通过org查询项目列表
     *
     * @param orgId
     * @return
     */
    List<Map> selectProjectPlusPageByOrgId(String orgId);

    /**
     * 根据项目id查询是否已经存在该项目
     *
     * @param projectId
     * @return
     */
    int isExistProject(String projectId);

    /**
     * 根据组织id查询组织信息
     *
     * @param orgId
     * @return
     */
    Map selectFullPath(String orgId);

    /**
     * 查询全部的项目
     *
     * @param list
     * @param projectName
     * @param areaName
     * @return
     */
    List<ProjectDto> selectAllProject(@Param("list") List<Map> list, @Param("projectName") String projectName, @Param("areaName") String areaName);

    /**
     * 查询项目通过全路径
     *
     * @param projectQueryRequest
     * @return
     */
    List<ProjectDto> selectProjectListByFullPath(ProjectQueryRequest projectQueryRequest);

    /**
     * 新建项目
     *
     * @param projectModel
     * @return
     */
    Integer createProject(ProjectModel projectModel);

    /**
     * 项目列表的编辑
     *
     * @param id
     * @return
     */
    ProjectModel getProjectByProjectId(String id);

    /**
     * 通过分期id查询分期信息
     *
     * @param stageId
     * @return
     */
    StageModel queryStage(String stageId);

    /**
     * 通过组团id查询楼栋列表
     *
     * @param groupId
     * @return
     */
    List<DesignBuildModel> selectDesignBuildByGroupId(String groupId);

    /**
     * 通过组团id查询组团信息
     *
     * @param groupId
     * @return
     */
    GroupModel queryGroup(String groupId);

    /**
     * 通过楼栋id查询楼栋信息
     *
     * @param buildId
     * @return
     */
    DesignBuildModel queryDesignBuild(String buildId);

    /**
     * 编辑时的项目的查询通过项目id
     *
     * @param projectModel
     * @return
     */
    Integer saveProjectFromEdit(ProjectModel projectModel);

    /**
     * 通过项目id查询组团
     *
     * @param projectId
     * @return
     */
    List<GroupModel> selectGroupListByProjectId(String projectId);

    /**
     * 通过组团id查询
     *
     * @param groupId
     * @return
     */
    List<GroupModel> selectGroupList(String groupId);

    /**
     * 新建项目的新建组团的楼栋全部查询
     *
     * @param stageId
     * @return
     */
    List<DesignBuildModel> selectDesignBuildByStageId(String stageId);

    /**
     * 项目编辑页面里面的新建分期
     *
     * @param stageModel
     * @return
     */
    Integer createStage(List<StageModel> stageModel);

    /**
     * 项目编辑页面里面的新建组团
     *
     * @param groupModel
     * @return
     */
    Integer createGroup(List<GroupModel> groupModel);

    /**
     * 项目编辑页面里面的新建楼栋
     *
     * @param designBuildModel
     * @return
     */
    Integer createDesignBuild(List<DesignBuildModel> designBuildModel);

    /**
     * 项目编辑页面里面的分期分页查询
     *
     * @param stageModel
     * @return
     */
    List<StageModel> selectStagePlusPageList(StageModel stageModel);

    /**
     * 项目编辑页面里面的组团分页查询
     *
     * @param groupModel
     * @return
     */
    List<GroupModel> selectGroupPlusPageList(GroupModel groupModel);

    /**
     * 项目编辑页面里面的楼栋分页查询
     *
     * @param designBuildModel
     * @return
     */
    List<DesignBuildModel> selectDesignBuildPlusPageList(DesignBuildModel designBuildModel);

    /**
     * 项目编辑页面里面的分期的编辑
     *
     * @param stageModel
     * @return
     */
    Integer updateStage(StageModel stageModel);

    /**
     * 项目编辑页面里面的组团的编辑
     *
     * @param groupModel
     * @return
     */
    Integer updateGroup(GroupModel groupModel);

    /**
     * 项目编辑页面里面的楼栋的编辑
     *
     * @param designBuildModel
     * @return
     */
    Integer updateDesignBuild(DesignBuildModel designBuildModel);

    /**
     * 批量更新组团id
     *
     * @param designBuildModels
     * @return
     */
    Integer updateBatchDesignBuild(List<DesignBuildModel> designBuildModels);

    /**
     * 项目编辑页面里面的分期的删除
     *
     * @param stageModel
     * @return
     */
    Integer delStage(StageModel stageModel);

    /**
     * 项目编辑页面里面的组团的删除
     *
     * @param groupModel
     * @return
     */
    Integer delGroup(GroupModel groupModel);

    /**
     * 项目编辑页面里面的楼栋的删除
     *
     * @param designBuildModel
     * @return
     */
    Integer delDesignBuild(DesignBuildModel designBuildModel);

    /**
     * 重置组团的id(其实就是删除t_mm_designBuild的group_id)
     *
     * @param designBuildModels
     * @return
     */
    Integer resetGroup(List<DesignBuildModel> designBuildModels);

    /**
     * 校验是否已经存在项目
     *
     * @param orgId
     * @return
     */
    Integer verifyIsExistProject(String orgId);

    /**
     * 批量保存楼层
     *
     * @param buildingFloorList
     * @return
     */
    Integer saveBatchBuildingFloor(List<BuildingFloor> buildingFloorList);

    /**
     * 批量添加单元
     *
     * @param buildUnitList
     * @return
     */
    Integer saveBatchUnit(List<BuildUnit> buildUnitList);

    /**
     * 批量添加房间
     *
     * @param buildRoomList
     * @return
     */
    Integer saveBatchRoom(List<BuildRoom> buildRoomList);

    /**
     * 通过户型id查询户型面积
     *
     * @param hxId
     * @return
     */
    BuildHx getHXArea(String hxId);




    Integer projectListSelectCount(Map<String, Object> map);

    /**
     * 启用禁用项目
     *
     * @param map
     * @return
     */
    Integer projectIsEnableUpdate(Map<String, Object> map);

    /**
     * 删除项目
     *
     * @param map
     * @return
     */
    Integer projectDeleteUpdate(Map<String, Object> map);

    /**
     * 判断项目编号是否已存在
     *
     * @param dataMap
     * @return
     */
    Map<String, Object> projectNumIsExsit(Map<String, String> dataMap);

    /**
     * 与售前项目相关联,和AddNewProjectSaleRelInsert为一条事务
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdate(Map<String, String> projectMap);

    /**
     * 与售前项目相关联,和AddNewProjectSaleRelUpdate为一条事务
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelInsert(Map<String, String> projectMap);

    /**
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是查找项目ID
     *
     * @param projectMap
     * @return
     */
    Map<String, String> newProjectOrgSelect(Map<String, String> projectMap);

    /**
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是跟新项目
     *
     * @param projectMap
     * @return
     */
    Integer newProjectOrgUpdate(Map<String, String> projectMap);

    /**
     * 修改项目信息
     *
     * @param projectMap
     * @return
     */
    Integer projectInfoModify(Map<String, String> projectMap);

    /**
     * 判断当前是否已经存在过关联关系
     *
     * @param projectMap
     * @return
     */
    Map<String, Object> projectSaleRelCountBySaleProjectIdSelect(Map<String, String> projectMap);

    /**
     * 若产生关联关系（>0）且则走这一条方法
     *
     * @param projectMap
     * @return
     */
    Integer newProjectSaleRelNoDel(Map<String, String> projectMap);

    /**
     * 增加一条新的项目
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectInfoInsert(Map<String, String> projectMap);

    /**
     * 该接口和增加一条新的项目一起执行
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdateProjectorgrel(Map<String, String> projectMap);

    /**
     * 该接口和增加一条新的项目一起执行
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdateOne(Map<String, String> projectMap);

    /**
     * 该接口和增加一条新的项目一起执行
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdateTwo(Map<String, String> projectMap);

    /**
     * 该接口和增加一条新的项目一起执行
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdateThree(Map<String, String> projectMap);

    /**
     * 该接口和增加一条新的项目一起执行
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdateFour(Map<String, String> projectMap);

    /**
     * 该接口和增加一条新的项目一起执行
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdateFive(Map<String, String> projectMap);

    /**
     * 该接口和增加一条新的项目一起执行
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdateSix(Map<String, String> projectMap);

    /**
     * 该接口和增加一条新的项目一起执行
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdateSeven(Map<String, String> projectMap);

    /**
     * 该接口和增加一条新的项目一起执行
     *
     * @param projectMap
     * @return
     */
    Integer addNewProjectSaleRelUpdateEight(Map<String, String> projectMap);

    /**
     * 查询单条项目的数据
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> selectOneProject(Map<String, Object> map);

    /**
     * 修改菜单状态
     *
     * @param map
     * @return
     */
    Integer updateMenuStatus(Map<String, Object> map);

    /**
     * 修改Menus信息
     *
     * @return
     */
    Map systemMenuOldPathSelect();

    Map systemMenuNewPathSelect();

    Integer systemMenuInfoUpdate();

    List<Map> getCityList();

    int insertCityProject(Map map);

    int deleteCityProject(@Param("projectID") String projectId);

    /**
     * 查询失效的项目列表
     *
     * @return
     */
    List<BindProject> selectInvalidProject();

    /**
     * 更新失效的项目的状态为 禁止: 0
     *
     * @param bindProject
     * @return
     */
    Integer updateProjectStatus(List<BindProject> bindProject);

    /**
     * 查询未绑定的项目
     *
     * @param companyCode
     * @return
     */
    List<BindProject> selectNotBindProject(String companyCode);

    /**
     * 绑定项目
     *
     * @param bindProjects
     * @return
     */
    Integer updateProjectForBind(List<BindProject> bindProjects);

    /**
     * 禁止项目 1: 启动
     *
     * @param bindProjects
     * @return
     */
    Integer updateStatus(List<BindProject> bindProjects);


    /**
     * 通过项目id查询房间列表
     *
     * @param projectId
     * @return
     */
    List<Map> selectRoomPlusPageByProjectId(String projectId);

    /**
     * 通过房间id查询房间详情
     *
     * @param roomId
     * @return
     */
    BuildRoom getRoomByProjectId(String roomId);

    /**
     * 保存编辑后的房间
     *
     * @param map
     * @return
     */
    Integer saveRoomByRoomId(List<BuildRoom> map);

    /**
     * 房间面积查询列表通过项目id
     *
     * @param buildRoom
     * @return
     */
    List<BuildRoom> selectRoomAreaListPlusPageByProjectId(BuildRoom buildRoom);

    /**
     * 查询户型列表通过项目id
     *
     * @param projectId
     * @return
     */
    List<BuildHx> selectHXListPlusPageByProjectId(String projectId);

    /**
     * 通过项目id查询最大的楼栋列表的最大的序号值
     *
     * @param projectId
     * @return
     */
    Integer selectDesignBuildMaxOrderCode(String projectId);

    /**
     * 查询最大的序号
     *
     * @param projectId
     * @return
     */
    Integer getMaxOrderCode(String projectId);

    /**
     * 分期上下移动
     *
     * @param mapList
     * @return
     */
    Integer move(List<Map> mapList);

    /**
     * @Author wanggang
     * @Description //获取项目是否初始化
     * @Date 15:09 2021/11/29
     * @Param [projectId]
     * @return int
     **/
    int selectProjectIsSyn(String projectId);

    /**
     * @Author wanggang
     * @Description //获取未初始化项目
     * @Date 20:42 2021/11/29
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> selectProject();

    /**
     * 根据楼栋id查询所有的房间
     *
     * @param buildId
     * @return
     */
    List<BuildRoom> selectRoomListByBuildId(String buildId);

    /**
     * 批量删除房间
     *
     * @param delDataList
     * @return
     */
    Integer delRoomByRoomId(List<BuildRoom> delDataList);

    /**
     * 批量更新房间
     *
     * @param updateDataList
     * @return
     */
    Integer updateRoomByRoomId(List<BuildRoom> updateDataList);

    /**
     * 修改单元表的户数
     *
     * @param buildRoom
     * @return
     */
    Integer updateBuildUnit(List<BuildRoom> buildRoom);

    /**
     * 删除房间通过房间id
     *
     * @param roomId
     * @return
     */
    Integer delRoom(String roomId);

    /**
     * 右合并
     *
     * @param roomId
     * @return
     */
    Integer rightMoveRoom(String roomId);

    /**
     * 下合并
     *
     * @param roomId
     * @return
     */
    Integer downMoveRoom(String roomId);

    /**
     * 通过楼栋id查询楼层
     *
     * @param buildId
     * @return
     */
    Integer getMaxFloorNo(String buildId);

    /**
     * 更新单元户数
     *
     * @param unitId
     * @return
     */
    Integer updateUnit(String unitId);

    /**
     * 查询户数通过楼栋id
     *
     * @param buildId
     * @return
     */
    List<Integer> getHouseholdsNumByBuildId(String buildId);

    /**
     * 查询单元通过楼栋id
     *
     * @param buildId
     * @return
     */
    List<BuildUnit> selectUnitByBuildId(String buildId);

    /**
     * 更新楼栋表的生成房源为1(已经生成房源)
     *
     * @param buildId
     * @return
     */
    Integer updateDesignBuildByBuildId(String buildId);

    /**
     * 拼接楼栋名称和单元名称
     *
     * @param roomId
     * @return
     */
    String getDesignBuildAndUnitByRoomId(String roomId);

    /**
     * 批量更新面积数据到 t_mm_build_room
     *
     * @param buildRoomList
     * @return
     */
    Integer updateBatchRoom(List<BuildRoom> buildRoomList);

    /**
     * 批量更新标准价数据到 t_mm_build_room
     *
     * @param buildRoomList
     * @return
     */
    Integer updateBatchStandardPrice(List<BuildRoom> buildRoomList);

    /**
     * 新增户型
     *
     * @param buildHx
     * @return
     */
    Integer saveBuildHx(BuildHx buildHx);

    /**
     * 查询户型通过户型id
     *
     * @param id
     * @return
     */
    BuildHx getBuildHxById(String id);

    /**
     * 保存编辑后的户型
     *
     * @param buildHx
     * @return
     */
    Integer updateBuildHx(BuildHx buildHx);

    /**
     * 通过主键删除户型
     *
     * @param id 主键
     * @return 影响行数
     */
    Integer deleteById(String id);

    /**
     * 保存单独的房间信息
     *
     * @param buildRoom
     * @return
     */
    Integer updateBuildRoomByRoomId(BuildRoom buildRoom);

    /**
     * 根据单元id和序号查询序号后的房间信息(不包含当前列的数据)
     *
     * @param unitId
     * @param no
     * @return
     */
    List<BuildRoom> selectRoomByUnitIdAndNo(@Param("unitId") String unitId, @Param("no") Integer no);

    /**
     * 跟新 t_mm_build_room 的房间序号
     *
     * @param buildRoomList
     * @return
     */
    Integer updateRoomNo(List<BuildRoom> buildRoomList);

    /**
     * 放盘---保存放盘数据
     *
     * @param buildRoomList
     * @return
     */
    Integer updateReleaseDish(List<BuildRoom> buildRoomList);

    /**
     * 根据单元id查询房间信息
     *
     * @param unitId
     * @return
     */
    List<BuildRoom> selectRoomListByUnitId(String unitId);

    /**
     * 根据单元id和房间序号查询列数据
     *
     * @param unitId
     * @param no
     * @return
     */
    List<BuildRoom> selectRoomColumnList(@Param("unitId") String unitId, @Param("no") Integer no);

    /**
     * 根据楼栋id删除单元的数据
     *
     * @param buildId
     * @return
     */
    Integer delUnitByBuildId(String buildId);

    /**
     * 根据楼栋id删除房间的数据
     *
     * @param buildId
     * @return
     */
    Integer delRoomByBuildId(String buildId);

    /**
     * 根据房间id查询楼栋id
     *
     * @param roomId
     * @return
     */
    String getBuildIdByRoomId(String roomId);

    /**
     * 通过房间id查询房间详情
     *
     * @param roomId
     * @return
     */
    BuildRoom getRoomByRoomId(String roomId);

    /**
     * 房间面积列表查询
     *
     * @param roomTaskDto
     * @return
     */
    List<RoomTask> selectRoomAreaTaskPlusPageList(RoomTaskDto roomTaskDto);

    /**
     * 面积模板---项目下拉框查询
     *
     * @return
     */
    List<ProjectModel> selectProjectList();

    /**
     * 模板导出---选择楼栋模板
     *
     * @param buildId
     * @return
     */
    List<BuildRoom> buildRoomListByBuildId(String buildId);

    /**
     * 获取分期名称
     *
     * @param buildId
     * @return
     */
    DesignBuildModel getDesignBuildByBuildId(String buildId);

    /**
     * 通过项目id查询项目名称
     *
     * @param projectId
     * @return
     */
    String getProjectName(String projectId);

    /**
     * 添加房间的数据到 t_mm_build_room_plus
     *
     * @param buildRoomList
     * @return
     */
    Integer saveBatchRoomToPlusTable(List<BuildRoom> buildRoomList);

    /**
     * 根据build查询房间信息
     *
     * @param buildId
     * @return
     */
    List<BuildRoom> getAreaDataByBuildId(String buildId);

    /**
     * 保存房间面积任务
     *
     * @param roomTask
     * @return
     */
    Integer saveRoomAreaTask(RoomTask roomTask);

    /**
     * 面积任务列表删除通过任务id
     *
     * @param taskId
     * @return
     */
    Integer delAreaByTaskId(String taskId);

    /**
     * 价格列表
     *
     * @param priceDto
     * @return
     */
    List<PriceTask> selectPriceList(PriceDto priceDto);

    /**
     * 更新 t_mm_room_task 的状态为已执行
     *
     * @param taskId
     * @return
     */
    Integer updateStatusByTaskId(String taskId);

    /**
     * 通过任务id查询房间辅助表的数据, 如果不为空则把数据更新到t_mm_build_room表中
     *
     * @param taskId
     * @return
     */
    List<BuildRoom> selectRoomAreaTaskListByTaskId(String taskId);

    /**
     * 辅助表的数据更新到t_mm_build_room
     * @param buildRoomList
     * @return
     */
    Integer updateBatchToBuildRoom(List<BuildRoom> buildRoomList);

    /**
     * 添加标准价任务
     *
     * @param priceTask
     * @return
     */
    Integer savePriceTask(PriceTask priceTask);

    /**
     * 查询所有的标准价的任务数据
     *
     * @param type
     * @return
     */
    List<PriceTask> selectStandardPriceTask(Integer type);

    /**
     * 更新状态通过任务id
     *
     * @param taskId
     * @return
     */
    Integer updateStandardPriceStatusByTaskId(String taskId);

    /**
     * 标准价任务列表删除通过任务id
     *
     * @param taskId
     * @return
     */
    Integer delStandardPriceByTaskId(String taskId);

    /**
     * 辅助表的数据更新到t_mm_build_room
     *
     * @param buildRoomList
     * @return
     */
    Integer updateBatchStandardPriceToBuildRoom(List<BuildRoom> buildRoomList);

    /**
     * // 辅助表的数据(低价)更新到t_mm_build_room
     * @param buildRoomList
     * @return
     */
    Integer updateBatchDjPriceToBuildRoom(List<BuildRoom> buildRoomList);

    /**
     * 通过分期id查询当前的排序号
     *
     * @param stageId
     * @return
     */
    Integer getCurrentOrderCode(String stageId);

    /**
     * 通过楼栋id查询当前的排序号
     *
     * @param buildId
     * @return
     */
    Integer getDesignBuildCurrentOrderCode(String buildId);

    /**
     * 楼栋上下移动
     *
     * @param mapList
     * @return
     */
    Integer moveDesignBuild(List<Map> mapList);

    /**
     * 创建项目给组织表绑定项目id
     *
     * @param projectId
     * @param orgId
     * @return
     */
    Integer updateOrganizationByOrgId(@Param("projectId") String projectId, @Param("orgId") String orgId);

    /**
     * 通过full path查询组织数据
     *
     * @param fullPath
     * @return
     */
    Map selectDateByFullPath(String fullPath);
    /**
     * 根据项目名称判断是否已经存在项目
     *
     * @param projectName
     * @return
     */
    Integer isExistProjectName(String projectName);

    /**
     * 新增项目时把字典表全局的与字典与该项目绑定
     * @param map
     * @return
     */
    Integer saveGlobalDictionary(Map map);

    /**
     * 根据分期名称判断是否已经存在分期
     *
     * @param stageName
     * @param projectId
     * @return
     */
    Integer isExistStageName(@Param("stageName") String stageName, @Param("projectId") String projectId);

    /**
     * 根据组团名称判断是否已经存在组团
     *
     * @param groupName
     * @param stageId
     * @return
     */
    Map isExistGroupName(@Param("groupName") String groupName, @Param("stageId") String stageId);

    /**
     * 根据项目名称判断是否已经存在项目
     *
     * @param projectName
     * @return
     */
    Map isExistProjectName2(String projectName);

    /**
     * 根据楼栋名称判断是否已经存在楼栋
     *
     * @param buildName
     * @param projectId
     * @return
     */
    Map isExistBuildName(@Param("buildName") String buildName, @Param("projectId") String projectId);

    /**
     * 根据分期名称查询分期信息
     *
     * @param stageName
     * @param projectId
     * @return
     */
    Map isExistStageName2(@Param("stageName") String stageName, @Param("projectId") String projectId);

    /**
     *  查询楼栋通过项目id
     *
     * @param projectId
     * @return
     */
    List<DesignBuildModel> selectDesignBuildByProjectId(String projectId);

    /**
     * 通过分期id删除在项目表中的分期数据
     *
     * @param stageId
     * @return
     */
    Integer deleteProject(String stageId);
    /**
     * @Author wanggang
     * @Description //获取城市
     * @Date 8:41 2021/12/18
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getCitys();
}
