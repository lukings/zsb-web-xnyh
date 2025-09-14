package cn.visolink.system.projectmanager.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.projectmanager.model.BuildHx;
import cn.visolink.system.projectmanager.model.BuildRoom;
import cn.visolink.system.projectmanager.model.HousingResourceModel;
import cn.visolink.system.projectmanager.model.requestmodel.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface projectmanagerService {

    /**
     * 项目管理查询
     *
     * @param projectQueryRequest
     * @param request
     * @return
     */
    ResultBody projectListSelect(ProjectQueryRequest projectQueryRequest, HttpServletRequest request);

    /**
     * 新建项目与分期共用接口
     *
     * @param project
     * @param request
     * @return
     */
    ResultBody createProject(ProjectModel project, HttpServletRequest request);

    /**
     * 通过id查询项目信息
     *
     * @param id
     * @return
     */
    ResultBody getProject(String id);

    /**
     * 组团的楼栋查询通过分期id
     *
     * @param stageId
     * @return
     */
    ResultBody selectDesignBuildByStageId(String stageId);

    /**
     * 查询分期通过通过项目id
     *
     * @param projectId
     * @return
     */
    ResultBody selectStageListByProjectId(String projectId);

    /**
     * 通过项目id查询组团
     *
     * @param projectId
     * @return
     */
    ResultBody selectGroupListByProjectId(String projectId);

    /**
     * 项目编辑页面里面的新建组团
     *
     * @param groupModel
     * @param request
     * @return
     */
    ResultBody createGroup(GroupModel groupModel, HttpServletRequest request);

    /**
     * 项目编辑页面里面的新建楼栋
     *
     * @param designBuildModel
     * @param request
     * @return
     */
    ResultBody createDesignBuild(DesignBuildModel designBuildModel, HttpServletRequest request);

    /**
     * 项目编辑页面里面的分期分页查询
     *
     * @param stageModel
     * @return
     */
    ResultBody selectStagePlusPageList(StageModel stageModel);

    /**
     * 项目编辑页面里面的组团分页查询
     *
     * @param groupModel
     * @return
     */
    ResultBody selectGroupPlusPageList(GroupModel groupModel);

    /**
     * 项目编辑页面里面的楼栋分页查询
     *
     * @param designBuildModel
     * @return
     */
    ResultBody selectDesignBuildPlusPageList(DesignBuildModel designBuildModel);

    /**
     * 项目编辑页面里面的分期的编辑
     *
     * @param stageModel
     * @param request
     * @return
     */
    ResultBody updateStage(StageModel stageModel, HttpServletRequest request);

    /**
     * 项目编辑页面里面的组团的编辑
     *
     * @param groupModel
     * @param request
     * @return
     */
    ResultBody updateGroup(GroupModel groupModel, HttpServletRequest request);

    /**
     * 项目编辑页面里面的楼栋的编辑
     *
     * @param designBuildModel
     * @param request
     * @return
     */
    ResultBody updateDesignBuild(DesignBuildModel designBuildModel, HttpServletRequest request);

    /**
     * 项目编辑页面里面的分期的删除
     *
     * @param stageModel
     * @return
     */
    ResultBody delStage(StageModel stageModel);

    /**
     * 项目编辑页面里面的组团的删除
     *
     * @param groupModel
     * @return
     */
    ResultBody delGroup(GroupModel groupModel);

    /**
     * 项目编辑页面里面的楼栋的删除
     *
     * @param designBuildModel
     * @return
     */
    ResultBody delDesignBuild(DesignBuildModel designBuildModel);

    /**
     * 通过分期id查询分期信息
     *
     * @param stageId
     * @return
     */
    ResultBody queryStage(String stageId);

    /**
     * 通过组团id查询组团信息
     *
     * @param groupId
     * @return
     */
    ResultBody queryGroup(String groupId);

    /**
     * 通过楼栋id查询楼栋信息
     *
     * @param buildId
     * @return
     */
    ResultBody queryDesignBuild(String buildId);

    /**
     * 校验是否已经存在项目
     *
     * @param orgId
     * @return
     */
    ResultBody verifyIsExistProject(String orgId);


    /**
     * 启用禁用项目
     * */
    public  Integer  projectIsEnableUpdate(Map<String, Object> map);
    /**
     * 删除项目
     * */
    public  Integer  projectDeleteUpdate(Map<String, Object> map);
    /*
     * 判断项目编号是否已存在
     * */
    public  Map <String, Object> projectNumIsExsit(Map<String, String> dataMap);
    /*
     * 增加一条新的项目
     * */
    public Integer addNewProjectInfoInsert(Map<String, String> projectMap);
    /*
     * 与售前项目相关联,和AddNewProjectSaleRelInsert为一条事务
     * */
    public Integer addNewProjectSaleRelUpdate(Map<String, String> projectMap);
    /*
     * 与售前项目相关联,和AddNewProjectSaleRelUpdate为一条事务
     * */
    public Integer addNewProjectSaleRelInsert(Map<String, String> projectMap);

    /*
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是查找项目ID
     * */
    public  Map <String, String> newProjectOrgSelect(Map<String, String> projectMap);
    /*
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是跟新项目
     * */
    public  Integer newProjectOrgUpdate(Map<String, String> projectMap);
    /*
     * 修改项目信息
     * */
    public  Integer projectInfoModify(Map<String, String> projectMap);

    /*
     * 判断当前是否已经存在过关联关系
     * */
    public Map <String, Object>  projectSaleRelCountBySaleProjectIdSelect(Map<String, String> projectMap);

    /*
     * 若产生关联关系（>0）且则走这一条方法
     * */
    public Integer newProjectSaleRelNoDel(Map<String, String> projectMap);

    /*
     * 增加项目和修改项目的调用方法
     * */
    public String projectexecute(Map<String, String> dataMap, HttpServletRequest request, HttpServletResponse response);

    /*
     * 增加项目
     * */
    public Map <String, Object> addNewProjectInfo(@RequestBody Map<String, String> projectMap);

    /*
     * 添加完成修改对应组织的项目ID,查询当前组织的项目ID
     * */
    public void updateOrgProject(Map<String, String> projectMap);

    /*
     * 修改项目信息,该方法要被controller层调用，集合需要修改项目信息方法的接口和逻辑
     * */
    public  Map <String, Object> updateProjectInfo(@RequestBody Map<String, String> projectMap);

    /*
     * 查询单条项目的数据
     * */
    public  List<Map<String,Object>> selectOneProject(Map<String, Object> map);

    /*
    * 修改参数的状态
    * */
    public  Integer  updateMenuStatus(Map<String, Object> map);

    /*
    * 修改systemmenus信息
    * */
    public Map systemmenus(Map map);

    List<Map> getCityList();

    /**
     * 查询失效的项目列表
     *
     * @return
     */
    ResultBody selectInvalidProject();

    /**
     * 查询未绑定的项目
     *
     * @param request
     * @return
     */
    ResultBody selectNotBindProject(HttpServletRequest request);


    /**
     * 生成房源
     *
     * @param housingResourceModel
     * @param request
     * @return
     */
    ResultBody generationHousingResource(HousingResourceModel housingResourceModel, HttpServletRequest request);

    /**
     * 查询户型列表通过项目id
     *
     * @param projectId
     * @return
     */
    ResultBody selectHXListPlusPageByProjectId(String projectId);


    /**
     * 通过项目id查询房间列表
     *
     * @param projectId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResultBody selectRoomPlusPageByProjectId(String projectId, String pageNum, String pageSize);

    /**
     * 通过房间id查询房间详情
     *
     * @param roomId
     * @return
     */
    ResultBody getRoomByProjectId(String roomId);

    /**
     * 保存编辑后的房间
     *
     * @param buildRoom
     * @return
     */
    ResultBody saveRoomByRoomId(BuildRoom buildRoom);

    /**
     * 批量更改户型
     *
     * @param buildRoom
     * @param request
     * @return
     */
    ResultBody saveBatchRoomByRoomId(List<BuildRoom> buildRoom, HttpServletRequest request);

    /**
     * 导出楼栋列表通过项目id
     *
     * @param projectId
     * @return
     */
    ResultBody exportDesignBuildListByProjectId(String projectId);

    /**
     * 房间资料导出通过项目id
     *
     * @param projectId
     * @return
     */
    ResultBody exportRoomListByProjectId(String projectId);

    /**
     * 房间面积查询列表通过项目id
     *
     * @param buildRoom
     * @return
     */
    ResultBody selectRoomAreaListPlusPageByProjectId(BuildRoom buildRoom);

    /**
     * 房间面积录入通过房间id
     *
     * @param buildRoom
     * @return
     */
    ResultBody importRoomAreaByRoomId(BuildRoom buildRoom);

    /**
     * 复制楼栋通过楼栋id
     *
     * @param buildId
     * @return
     */
    ResultBody copyDesignBuildByBuildId(String buildId);

    /**
     * 粘贴楼栋
     *
     * @param designBuildModel
     * @return
     */
    ResultBody pasteDesignBuild(DesignBuildModel designBuildModel);

    /**
     * 上下移动
     *
     * @param currentId
     * @param targetId
     * @param flag
     * @return
     */
    ResultBody move(String currentId, String targetId, Integer flag);

    /**
     * @Author wanggang
     * @Description //获取项目是否已初始化
     * @Date 15:08 2021/11/29
     * @Param [projectId]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectProjectIsSyn(String projectId);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 20:41 2021/11/29
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectProject();

    /**
     * 根据楼栋id查询所有的房间
     *
     * @param buildId
     * @return
     */
    ResultBody selectRoomListByBuildId(String buildId);

    /**
     * 根据房间id删除房间和删除整列工用接口
     *
     * @param buildRoom
     * @return
     */
    ResultBody delRoomByRoomId(List<BuildRoom> buildRoom);

    /**
     * 插入整列房间
     *
     * @param unitId
     * @param no
     * @param request
     * @return
     */
    ResultBody insertColumnRoom(String unitId, Integer no, HttpServletRequest request);

    /**
     * 合并房间
     *
     * @param currentRoomId
     * @param targetRoomId
     * @param moveFlag
     * @return
     */
    ResultBody mergeRoom(String currentRoomId, String targetRoomId, Integer moveFlag);

    /**
     * 房间拆分
     *
     * @param map
     * @return
     */
    ResultBody roomSplit(Map map);

    /**
     * 新增户型字典表
     *
     * @param buildHx
     * @param request
     * @return
     */
    ResultBody saveBuildHx(BuildHx buildHx, HttpServletRequest request);

    /**
     * 查询户型通过户型id
     *
     * @param id
     * @return
     */
    ResultBody getBuildHxById(String id);

    /**
     * 保存编辑后的户型
     *
     * @param buildHx
     * @param request
     * @return
     */
    ResultBody updateBuildHx(BuildHx buildHx, HttpServletRequest request);

    /**
     * 通过主键删除户型
     *
     * @param id 主键
     * @return 是否成功
     */
    ResultBody deleteById(String id);

    /**
     * 保存单独的房间信息
     *
     * @param buildRoom
     * @return
     */
    ResultBody updateBuildRoomByRoomId(BuildRoom buildRoom);

    /**
     * 新增单独房间
     *
     * @param buildRoom
     * @param request
     * @return
     */
    ResultBody saveBuildRoom(BuildRoom buildRoom, HttpServletRequest request);

    /**
     * 放盘---保存放盘数据
     *
     * @param releaseDishRequest
     * @return
     */
    ResultBody updateReleaseDish(ReleaseDishRequest releaseDishRequest);

    /**
     * 房间面积列表查询
     *
     * @param roomTaskDto
     * @return
     */
    ResultBody selectRoomAreaTaskPlusPageList(RoomTaskDto roomTaskDto);

    /**
     * 面积模板---项目下拉框查询
     *
     * @return
     */
    ResultBody selectProjectList();

    /**
     * 通过项目查询分期，楼栋列表
     *
     * @param projectId
     * @return
     */
    ResultBody selectRoomAreaTemplates(String projectId);

    /**
     * 模板导出---导出面积模板
     *
     * @param buildId
     * @param request
     * @param response
     * @return
     */
    void exportAreaTemplate(String buildId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 模板导出---选择楼栋模板
     *
     * @param buildId
     * @param request
     * @param response
     * @return
     */
    void exportStandardTemplate(String buildId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 面积导入
     *
     * @param file
     * @param applyName
     * @param applyTime
     * @param remark
     * @param request
     * @return
     */
    ResultBody importArea(MultipartFile file, String applyName, String applyTime, String remark, HttpServletRequest request);

    /**
     * 标准价导入
     *
     * @param file
     * @param applyName
     * @param valuationType
     * @param priceStandard
     * @param remark
     * @param request
     * @return
     */
    ResultBody importStandard(MultipartFile file, String applyName, Integer valuationType, Integer priceStandard, String remark, HttpServletRequest request);

    /**
     * 面积任务列表删除通过任务id
     *
     * @param taskId
     * @return
     */
    ResultBody delAreaByTaskId(String taskId);

    /**
     * 房间面积任务导出
     *
     * @param request
     * @param response
     * @return
     */
    void exportRoomTask(HttpServletRequest request, HttpServletResponse response);

    /**
     * 标准价任务导出
     *
     * @param request
     * @param response
     * @return
     */
    void exportStandardTask(HttpServletRequest request, HttpServletResponse response);

    /**
     * 房间面积任务执行通过任务id
     *
     * @param taskId
     * @return
     */
    ResultBody executeRoomAreaTask(String taskId);

    /**
     * 价格列表
     *
     * @param priceDto
     * @return
     */
    ResultBody selectPriceList(PriceDto priceDto);

    /**
     * 标准价任务列表删除通过任务id
     *
     * @param taskId
     * @return
     */
    ResultBody delStandardPriceByTaskId(String taskId);

    /**
     * 标准价任务执行通过任务id
     *
     * @param taskId
     * @return
     */
    ResultBody executeStandardPriceTask(String taskId);

    /**
     * 模板导出---低价录入
     *
     * @param buildId
     * @param request
     * @param response
     * @return
     */
    void exportDjTemplate(String buildId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 低价导入
     *
     * @param file
     * @param applyName
     * @param valuationType
     * @param priceStandard
     * @param remark
     * @param request
     * @return
     */
    ResultBody importDj(MultipartFile file, String applyName, Integer valuationType, Integer priceStandard, String remark, HttpServletRequest request);

    /**
     * 低价任务导出
     *
     * @param request
     * @param response
     * @return
     */
    void exportDjTask(HttpServletRequest request, HttpServletResponse response);

    /**
     * 低价任务列表删除通过任务id
     *
     * @param taskId
     * @return
     */
    ResultBody delDjPriceByTaskId(String taskId);

    /**
     * 低价任务执行通过任务id
     *
     * @param taskId
     * @return
     */
    ResultBody executeDjPriceTask(String taskId);
    /**
     * @Author wanggang
     * @Description //获取城市
     * @Date 8:38 2021/12/18
     * @Param [request]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectCityList(HttpServletRequest request);
}
