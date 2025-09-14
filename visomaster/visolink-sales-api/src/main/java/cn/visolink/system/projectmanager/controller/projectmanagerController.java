package cn.visolink.system.projectmanager.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.projectmanager.model.BuildHx;
import cn.visolink.system.projectmanager.model.BuildRoom;
import cn.visolink.system.projectmanager.model.HousingResourceModel;
import cn.visolink.system.projectmanager.model.requestmodel.*;
import cn.visolink.system.projectmanager.service.projectmanagerService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author 孙林
 * @date:2019-9-10
 * */

@RestController
@Api(tags = "基础数据-项目管理")
@RequestMapping("/manager")
public class projectmanagerController {
    @Autowired
   public projectmanagerService managerservice;

    @Log("项目管理查询")
    @CessBody
    @ApiOperation(value = "项目管理查询")
    @PostMapping("/projectListSelect")
    public ResultBody projectListSelect(@RequestBody ProjectQueryRequest projectQueryRequest, HttpServletRequest request) {
        return managerservice.projectListSelect(projectQueryRequest, request);
    }

    @Log("新建项目与分期共用接口")
    @CessBody
    @ApiOperation(value = "新建项目与分期共用接口")
    @PostMapping("/createProject")
    public ResultBody createProject(@RequestBody ProjectModel project, HttpServletRequest request) {
        return managerservice.createProject(project, request);
    }

    @Log("查询项目通过主键id")
    @CessBody
    @ApiOperation(value = "查询项目通过主键id")
    @GetMapping("/getProject")
    public ResultBody getProject(@RequestParam("id") String id) {
        return managerservice.getProject(id);
    }

    @Log("分期分页查询")
    @CessBody
    @ApiOperation(value = "分期分页查询")
    @PostMapping("/selectStagePlusPageList")
    public ResultBody selectStagePlusPageList(@RequestBody StageModel stageModel) {
        return managerservice.selectStagePlusPageList(stageModel);
    }

    @Log("组团分页查询")
    @CessBody
    @ApiOperation(value = "组团分页查询")
    @PostMapping("/selectGroupPlusPageList")
    public ResultBody selectGroupPlusPageList(@RequestBody GroupModel groupModel) {
        return managerservice.selectGroupPlusPageList(groupModel);
    }

    @Log("楼栋分页查询")
    @CessBody
    @ApiOperation(value = "楼栋分页查询")
    @PostMapping("/selectDesignBuildPlusPageList")
    public ResultBody selectDesignBuildPlusPageList(@RequestBody DesignBuildModel designBuildModel) {
        return managerservice.selectDesignBuildPlusPageList(designBuildModel);
    }

    @Log("组团的楼栋查询通过分期id")
    @CessBody
    @ApiOperation(value = "组团的楼栋查询通过分期id")
    @GetMapping("/selectDesignBuildByStageId")
    public ResultBody selectDesignBuild(@RequestParam("stageId") String stageId) {
        return managerservice.selectDesignBuildByStageId(stageId);
    }

    @Log("查询分期通过通过项目id")
    @CessBody
    @ApiOperation(value = "查询分期通过通过项目id")
    @GetMapping("/selectStageListByProjectId")
    public ResultBody selectStageListByProjectId(@RequestParam("projectId") String projectId) {
        return managerservice.selectStageListByProjectId(projectId);
    }

    @Log("通过项目id查询组团")
    @CessBody
    @ApiOperation(value = "通过项目id查询组团")
    @GetMapping("/selectGroupListByProjectId")
    public ResultBody selectGroupListByProjectId(@RequestParam("projectId") String projectId) {
        return managerservice.selectGroupListByProjectId(projectId);
    }

    @Log("分期的编辑保存")
    @CessBody
    @ApiOperation(value = "分期的编辑保存")
    @PostMapping("/updateStage")
    public ResultBody updateStage(@RequestBody StageModel stageModel, HttpServletRequest request) {
        return managerservice.updateStage(stageModel, request);
    }

    @Log("组团的编辑保存")
    @CessBody
    @ApiOperation(value = "组团的编辑保存")
    @PostMapping("/updateGroup")
    public ResultBody updateGroup(@RequestBody GroupModel groupModel, HttpServletRequest request) {
        return managerservice.updateGroup(groupModel, request);
    }

    @Log("楼栋的编辑保存")
    @CessBody
    @ApiOperation(value = "楼栋的编辑保存")
    @PostMapping("/updateDesignBuild")
    public ResultBody updateDesignBuild(@RequestBody DesignBuildModel designBuildModel, HttpServletRequest request) {
        return managerservice.updateDesignBuild(designBuildModel, request);
    }

    @Log("新建组团")
    @CessBody
    @ApiOperation(value = "新建组团")
    @PostMapping("/createGroup")
    public ResultBody createGroup(@RequestBody GroupModel groupModel, HttpServletRequest request) {
        return managerservice.createGroup(groupModel, request);
    }

    @Log("新建楼栋")
    @CessBody
    @ApiOperation(value = "新建楼栋")
    @PostMapping("/createDesignBuild")
    public ResultBody createDesignBuild(@RequestBody DesignBuildModel designBuildModel, HttpServletRequest request) {
        return ((Integer) managerservice.createDesignBuild(designBuildModel, request).getData()) > 0
                ? ResultBody.success("楼栋保存成功") : ResultBody.success("楼栋保存失败");
    }

    @Log("项目编辑页面里面的分期的删除")
    @CessBody
    @ApiOperation(value = "项目编辑页面里面的分期的删除")
    @PostMapping("/delStage")
    public ResultBody delStage(@RequestBody StageModel stageModel) {
        return managerservice.delStage(stageModel);
    }

    @Log("项目编辑页面里面的组团的删除")
    @CessBody
    @ApiOperation(value = "项目编辑页面里面的组团的删除")
    @PostMapping("/delGroup")
    public ResultBody delGroup(@RequestBody GroupModel groupModel) {
        return managerservice.delGroup(groupModel);
    }

    @Log("项目编辑页面里面的楼栋的删除")
    @CessBody
    @ApiOperation(value = "项目编辑页面里面的楼栋的删除")
    @PostMapping("/delDesignBuild")
    public ResultBody delDesignBuild(@RequestBody DesignBuildModel designBuildModel) {
        return managerservice.delDesignBuild(designBuildModel);
    }

    @Log("点击分期的编辑按钮查询")
    @CessBody
    @ApiOperation(value = "点击分期的编辑按钮查询")
    @GetMapping("/queryStage")
    public ResultBody queryStage(@RequestParam("stageId") String stageId) {
        return managerservice.queryStage(stageId);
    }

    @Log("点击组团的编辑按钮查询")
    @CessBody
    @ApiOperation(value = "点击分期的编辑按钮查询")
    @GetMapping("/queryGroup")
    public ResultBody queryGroup(@RequestParam("groupId") String groupId) {
        return managerservice.queryGroup(groupId);
    }

    @Log("点击楼栋的编辑按钮查询")
    @CessBody
    @ApiOperation(value = "点击楼栋的编辑按钮查询")
    @GetMapping("/queryDesignBuild")
    public ResultBody queryDesignBuild(@RequestParam("buildId") String buildId) {
        return managerservice.queryDesignBuild(buildId);
    }

    @Log("校验是否已经存在项目")
    @CessBody
    @ApiOperation(value = "校验是否已经存在项目")
    @GetMapping("/verifyIsExistProject")
    public ResultBody verifyIsExistProject(@RequestParam("orgId") String orgId) {
        return managerservice.verifyIsExistProject(orgId);
    }


    /**
     * 启用禁用项目
     * */
    @Log("启用禁用项目")
    @CessBody
    @ApiOperation(value = "启用禁用项目")
    @PostMapping("/projectIsEnableUpdate")
    public Integer projectIsEnableUpdate(@RequestBody Map<String,Object> map){
        Integer result=managerservice.projectIsEnableUpdate(map);
        Integer number=null;
        if(result==1){
            number = 0;
        }else {
            number =1;
        }
                return number;
    }

    /**
     * 删除项目
     * */
    @Log("删除项目")
    @CessBody
    @ApiOperation(value = "删除项目")
   @PostMapping("/projectDeleteUpdate")
    public Integer projectDeleteUpdate(@RequestBody Map<String,Object> map){
        Integer result=managerservice.projectDeleteUpdate(map);
                return result;
    }


    @Log("增加项目和修改项目的调用方法")
    @CessBody
    @ApiOperation(value = "增加项目和修改项目的调用方法")
    @PostMapping("/projectexecute")
    @Transactional(rollbackFor = Exception.class)
    public String projectexecute(@RequestBody Map<String,String> dataMap, HttpServletRequest request, HttpServletResponse response)
    {
        String result=managerservice.projectexecute(dataMap,request,response);
        return result;
    }



    /**
     * 增加项目
     * */
    @Log("增加项目")
    @CessBody
    @ApiOperation(value = "增加项目")
    @PostMapping("/addNewProjectInfo")
    public Map <String, Object> addNewProjectInfo( @RequestBody Map <String, String> projectMap){
        Map <String, Object> result=managerservice.addNewProjectInfo(projectMap);
             return result;
        }


    @Log("添加完成修改对应组织的项目ID,查询当前组织的项目ID")
    @CessBody
    @ApiOperation(value = "添加完成修改对应组织的项目ID,查询当前组织的项目ID")
    @PostMapping("/updateOrgProject")
    public void updateOrgProject(@RequestBody Map <String, String> projectMap) {
        managerservice.updateOrgProject(projectMap);
    }


    @Log("修改项目信息")
    @CessBody
    @ApiOperation(value = "修改项目信息")
    @PostMapping("/updateProjectInfo")
  public  Map <String, Object> updateProjectInfo(@RequestBody Map <String, String> projectMap) {
        Map <String, Object> result=managerservice.updateProjectInfo(projectMap);
        return result;
    }

    @Log("查询单条项目的数据")
    @CessBody
    @ApiOperation(value = "修改项目信息")
    @PostMapping("/selectOneProject")
  public  List selectOneProject(@RequestBody Map <String, Object> projectMap) {
        List result=managerservice.selectOneProject(projectMap);
        System.out.println(result);
        return result;
    }

    @Log("修改参数的状态")
    @CessBody
    @ApiOperation(value = "修改项目信息")
    @PostMapping("/updateMenuStatus")
  public  Integer updateMenuStatus(@RequestBody Map <String, Object> projectMap) {
        Integer in = managerservice.updateMenuStatus(projectMap);
        System.out.println(in);
        return 0;
    }

    @Log("查询城市信息")
    @ApiOperation(value = "查询城市信息")
    @PostMapping("/getCityList")
    public ResultBody getCityList() {
        List<Map>  mapList = managerservice.getCityList();
        return ResultBody.success(mapList);
    }

    @Log("查询未绑定的项目")
    @ApiOperation(value = "查询未绑定的项目")
    @GetMapping("/selectNotBindProject")
    public ResultBody selectNotBindProject(HttpServletRequest request) {
        return managerservice.selectNotBindProject(request);
    }

    @Log("查询城市")
    @ApiOperation(value = "查询城市")
    @GetMapping("/selectCityList")
    public ResultBody selectCityList(HttpServletRequest request) {
        return managerservice.selectCityList(request);
    }

    @Log("查询项目是否已初始化")
    @ApiOperation(value = "查询项目是否已初始化")
    @GetMapping("/selectProjectIsSyn")
    public ResultBody selectProjectIsSyn(String projectId) {
        return managerservice.selectProjectIsSyn(projectId);
    }


    @Log("查询未初始化项目")
    @ApiOperation(value = "查询未初始化项目")
    @GetMapping("/selectProject")
    public ResultBody selectProject() {
        return managerservice.selectProject();
    }







    @Log("生成房源")
    @ApiOperation(value = "生成房源")
    @PostMapping("/generationHousingResource")
    public ResultBody generationHousingResource(@RequestBody HousingResourceModel housingResourceModel, HttpServletRequest request) {
        return managerservice.generationHousingResource(housingResourceModel, request);
    }

    @Log("根据楼栋id查询所有的房间")
    @ApiOperation(value = "根据楼栋id查询所有的房间")
    @GetMapping("/selectRoomListByBuildId")
    public ResultBody selectRoomListByBuildId(@RequestParam("buildId") String buildId) {
        return managerservice.selectRoomListByBuildId(buildId);
    }

    @Log("查询户型列表通过项目id")
    @ApiOperation(value = "查询户型列表通过项目id")
    @PostMapping("/selectHXListPlusPageByProjectId")
    public ResultBody selectHXListPlusPageByProjectId(@RequestParam("projectId") String projectId) {
        return managerservice.selectHXListPlusPageByProjectId(projectId);
    }

    @Log("复制楼栋通过楼栋id")
    @ApiOperation(value = "复制楼栋通过楼栋id")
    @GetMapping("/copyDesignBuildByBuildId")
    public ResultBody copyDesignBuildByBuildId(@RequestParam("buildId") String buildId) {
        return managerservice.copyDesignBuildByBuildId(buildId);
    }

    @Log("粘贴楼栋")
    @ApiOperation(value = "粘贴楼栋")
    @PostMapping("/pasteDesignBuild")
    public ResultBody pasteDesignBuild(@RequestBody DesignBuildModel designBuildModel) {
        return managerservice.pasteDesignBuild(designBuildModel);
    }

    @Log("上下移动")
    @ApiOperation(value = "上下移动")
    @PostMapping("/move")
    public ResultBody move(@RequestParam("currentId") String currentId, @RequestParam("targetId") String targetId, @RequestParam("flag") Integer flag) {
        return managerservice.move(currentId, targetId, flag);
    }

    @Log("房间面积列表查询")
    @ApiOperation(value = "房间面积列表查询")
    @PostMapping("/selectRoomAreaTaskPlusPageList")
    public ResultBody selectRoomAreaTaskPlusPageList(@RequestBody RoomTaskDto roomTaskDto) {
        return managerservice.selectRoomAreaTaskPlusPageList(roomTaskDto);
    }

    @Log("面积模板---项目下拉框查询")
    @ApiOperation(value = "面积模板---项目下拉框查询")
    @GetMapping("/selectProjectList")
    public ResultBody selectProjectList() {
        return managerservice.selectProjectList();
    }

    @Log("楼栋模板查询")
    @ApiOperation(value = "楼栋模板查询")
    @GetMapping("/selectRoomAreaTemplates")
    public ResultBody selectRoomAreaTemplates(@RequestParam("projectId") String projectId) {
        return managerservice.selectRoomAreaTemplates(projectId);
    }

    @Log("模板导出---导出面积模板")
    @ApiOperation(value = "模板导出---导出面积模板")
    @GetMapping("/exportAreaTemplate")
    public void exportAreaTemplate(@RequestParam("buildId") String buildId, HttpServletRequest request, HttpServletResponse response) {
        managerservice.exportAreaTemplate(buildId, request, response);
    }

    @Log("面积导入")
    @ApiOperation(value = "面积导入")
    @PostMapping("/importArea")
    public ResultBody importArea(MultipartFile file, String applyName, String applyTime, String remark, HttpServletRequest request) {
        return managerservice.importArea(file, applyName, applyTime, remark, request);
    }

    @Log("面积任务列表删除通过任务id")
    @ApiOperation(value = "面积任务列表删除通过任务id")
    @GetMapping("/delAreaByTaskId")
    public ResultBody delAreaByTaskId(@RequestParam("taskId") String taskId) {
        return managerservice.delAreaByTaskId(taskId);
    }

    @Log("房间面积任务导出")
    @ApiOperation(value = "房间面积任务导出")
    @GetMapping("/exportRoomTask")
    public void exportRoomTask(HttpServletRequest request, HttpServletResponse response) {
        managerservice.exportRoomTask(request, response);
    }

    @Log("房间面积任务执行通过任务id")
    @ApiOperation(value = "房间面积任务执行通过任务id")
    @GetMapping("/executeRoomAreaTask")
    public ResultBody executeRoomAreaTask(@RequestParam("taskId") String taskId) {
        return managerservice.executeRoomAreaTask(taskId);
    }


    // ******************* 价格管理 ********************

    @Log("价格列表")
    @ApiOperation(value = "价格列表")
    @PostMapping("/selectPriceList")
    public ResultBody selectPriceList(@RequestBody PriceDto priceDto) {
        return managerservice.selectPriceList(priceDto);
    }

    @Log("模板导出---标准价模板")
    @ApiOperation(value = "模板导出---标准价模板")
    @GetMapping("/exportStandardTemplate")
    public void exportStandardTemplate(@RequestParam("buildId") String buildId, HttpServletRequest request, HttpServletResponse response) {
        managerservice.exportStandardTemplate(buildId, request, response);
    }

    @Log("标准价导入")
    @ApiOperation(value = "标准价导入")
    @PostMapping("/importStandard")
    public ResultBody importStandard(MultipartFile file, String applyName, Integer valuationType, Integer priceStandard, String remark, HttpServletRequest request) {
        return managerservice.importStandard(file, applyName, valuationType, priceStandard, remark, request);
    }

    @Log("标准价任务导出")
    @ApiOperation(value = "标准价任务导出")
    @GetMapping("/exportStandardTask")
    public void exportStandardTask(HttpServletRequest request, HttpServletResponse response) {
        managerservice.exportStandardTask(request, response);
    }

    @Log("标准价任务列表删除通过任务id")
    @ApiOperation(value = "标准价任务列表删除通过任务id")
    @GetMapping("/delStandardPriceByTaskId")
    public ResultBody delStandardPriceByTaskId(@RequestParam("taskId") String taskId) {
        return managerservice.delStandardPriceByTaskId(taskId);
    }

    @Log("标准价任务执行通过任务id")
    @ApiOperation(value = "标准价任务执行通过任务id")
    @GetMapping("/executeStandardPriceTask")
    public ResultBody executeStandardPriceTask(@RequestParam("taskId") String taskId) {
        return managerservice.executeStandardPriceTask(taskId);
    }

    // ************************** 低价录入 ***********************************

    @Log("模板导出---低价导出")
    @ApiOperation(value = "模板导出---低价导出")
    @GetMapping("/exportDjTemplate")
    public void exportDjTemplate(@RequestParam("buildId") String buildId, HttpServletRequest request, HttpServletResponse response) {
        managerservice.exportDjTemplate(buildId, request, response);
    }

    @Log("低价导入")
    @ApiOperation(value = "低价导入")
    @PostMapping("/importDj")
    public ResultBody importDj(MultipartFile file, String applyName, Integer valuationType, Integer priceStandard, String remark, HttpServletRequest request) {
        return managerservice.importDj(file, applyName, valuationType, priceStandard, remark, request);
    }

    @Log("低价任务导出")
    @ApiOperation(value = "低价任务导出")
    @GetMapping("/exportDjTask")
    public void exportDjTask(HttpServletRequest request, HttpServletResponse response) {
        managerservice.exportDjTask(request, response);
    }

    @Log("低价任务列表删除通过任务id")
    @ApiOperation(value = "低价任务列表删除通过任务id")
    @GetMapping("/delDjPriceByTaskId")
    public ResultBody delDjPriceByTaskId(@RequestParam("taskId") String taskId) {
        return managerservice.delDjPriceByTaskId(taskId);
    }

    @Log("低价任务执行通过任务id")
    @ApiOperation(value = "低价任务执行通过任务id")
    @GetMapping("/executeDjPriceTask")
    public ResultBody executeDjPriceTask(@RequestParam("taskId") String taskId) {
        return managerservice.executeDjPriceTask(taskId);
    }

    // ************************** 房间调整 ***********************************

    /**
     * 需要单元id，房间id
     */
    @Log("根据房间id删除房间和删除整列工用接口")
    @ApiOperation(value = "根据房间id删除房间和删除整列工用接口")
    @PostMapping("/delRoomByRoomId")
    public ResultBody delRoomByRoomId(@RequestBody List<BuildRoom> buildRoom) {
        return managerservice.delRoomByRoomId(buildRoom);
    }

    @Log("插入整列房间")
    @ApiOperation(value = "插入整列房间")
    @GetMapping("/insertColumnRoom")
    public ResultBody insertColumnRoom(@RequestParam("unitId") String unitId, @RequestParam("no") Integer no, HttpServletRequest request) {
        return managerservice.insertColumnRoom(unitId, no, request);
    }

    @Log("合并房间")
    @ApiOperation(value = "合并房间")
    @GetMapping("/mergeRoom")
    public ResultBody mergeRoom(@RequestParam("currentRoomId") String currentRoomId, @RequestParam("targetRoomId") String targetRoomId, Integer moveFlag) {
        return managerservice.mergeRoom(currentRoomId, targetRoomId, moveFlag);
    }

    @Log("房间拆分")
    @ApiOperation(value = "房间拆分")
    @PostMapping("/roomSplit")
    public ResultBody roomSplit(@RequestBody Map map) {
        return managerservice.roomSplit(map);
    }

    @Log("新增户型")
    @ApiOperation(value = "新增户型")
    @PostMapping("/saveBuildHx")
    public ResultBody saveBuildHx(@RequestBody Map map, HttpServletRequest request) {
        BuildHx buildHx = BeanUtil.mapToBean(map, BuildHx.class, false);
        return managerservice.saveBuildHx(buildHx, request);
    }

    @Log("查询户型通过户型id")
    @ApiOperation(value = "查询户型通过户型id")
    @GetMapping("/getBuildHxById")
    public ResultBody getBuildHxById(@RequestParam("id") String id) {
        return managerservice.getBuildHxById(id);
    }

    @Log("保存编辑后的户型")
    @ApiOperation(value = "保存编辑后的户型")
    @PostMapping("/updateBuildHx")
    public ResultBody updateBuildHx(@RequestBody Map map, HttpServletRequest request) {
        BuildHx buildHx = BeanUtil.mapToBean(map, BuildHx.class, false);
        return managerservice.updateBuildHx(buildHx, request);
    }

    @Log("通过主键删除户型")
    @ApiOperation(value = "通过主键删除户型")
    @GetMapping("/deleteById")
    public ResultBody deleteById(@RequestParam("id") String id) {
        return managerservice.deleteById(id);
    }

    /**
     *   废弃
     */
    @Log("保存单独的房间信息")
    @ApiOperation(value = "保存单独的房间信息")
    @PostMapping("/updateBuildRoomByRoomId")
    public ResultBody updateBuildRoomByRoomId(@RequestBody BuildRoom buildRoom) {
        return managerservice.updateBuildRoomByRoomId(buildRoom);
    }

    @Log("新增单独房间")
    @ApiOperation(value = "新增单独房间")
    @PostMapping("/saveBuildRoom")
    public ResultBody saveBuildRoom(@RequestBody BuildRoom buildRoom, HttpServletRequest request) {
        return managerservice.saveBuildRoom(buildRoom, request);
    }


    @Log("放盘---保存放盘数据")
    @ApiOperation(value = "放盘---保存放盘数据")
    @PostMapping("/updateReleaseDish")
    public ResultBody updateReleaseDish(@RequestBody ReleaseDishRequest releaseDishRequest) {
        return managerservice.updateReleaseDish(releaseDishRequest);
    }
}
