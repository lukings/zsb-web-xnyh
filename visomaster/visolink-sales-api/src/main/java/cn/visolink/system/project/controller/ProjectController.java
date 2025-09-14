package cn.visolink.system.project.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import cn.visolink.system.project.model.vo.TranslateProjectVo;
import cn.visolink.system.project.service.ProjectService;
import cn.visolink.utils.SecurityUtils;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/9/17 10:08 上午
 */
@RestController
@Api(tags = "项目管理相关")
@RequestMapping("/pro")
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @Log("根据当前登录人查询对应所拥有的项目列表")
    @CessBody
    @ApiOperation(value = "查询当前登录人所拥有的项目列表")
    @PostMapping(value = "/getProjectList")
    public List<Map> findProjectListByUserId(String projectName, HttpServletRequest request){
        String authCompanyID = request.getHeader("AuthCompanyID");
        String username = request.getHeader("username");
        List<Map> projectList = projectService.findProjectListByUserId(username,projectName,authCompanyID);
        return projectList;
    }

    @Log("根据当前登录人查询对应所拥有的项目列表")
    @CessBody
    @ApiOperation(value = "查询当前登录人所拥有的项目列表")
    @PostMapping(value = "/getProjectListByUserName")
    public List<ResultProjectVO> getProjectListByUserId(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        List<ResultProjectVO> projectList = projectService.getProjectListByUserId(map);
        return projectList;
    }

    @Log("根据当前登录人查询对应所拥有的项目列表")
    @CessBody
    @ApiOperation(value = "查询当前登录人所拥有的项目列表")
    @PostMapping(value = "/getProjectListByUserNameSmds")
    public List<ResultProjectVO> getProjectListByUserNameSmds(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        List<ResultProjectVO> projectList = projectService.getProjectListByUserIdSmds(map);
        return projectList;
    }

    @Log("查询所有的项目列表")
    @CessBody
    @ApiOperation(value = "查询所有的项目列表")
    @PostMapping(value = "/getProjectAllList")
    public List<ResultProjectVO> getProjectAllList(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        List<ResultProjectVO> projectList = projectService.getProjectAllList(map);
        return projectList;
    }

    @Log("根据当前登录人查询对应所拥有的项目列表")
    @CessBody
    @ApiOperation(value = "查询当前登录人所拥有的项目列表")
    @PostMapping(value = "/getProjectAllListByUserName")
    public List<ResultProjectVO> getProjectAllListByUserName(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        List<ResultProjectVO> projectList = projectService.getProjectAllListByUserName(map);
        return projectList;
    }

    @Log("根据当前登录人查询对应所拥有的申请数据权限项目列表")
    @CessBody
    @ApiOperation(value = "根据当前登录人查询对应所拥有的申请数据权限项目列表")
    @PostMapping(value = "/getProjectListByUserNameAndSqx")
    public List<ResultProjectVO> getProjectListByUserNameAndSqx(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        String userId = request.getHeader("userid");
        if(StringUtils.isEmpty(userId)){
            userId = SecurityUtils.getUserId();
        }
        map.put("userId", userId);
        map.put("UserId", userId);
        List<ResultProjectVO> projectList = projectService.getProjectListByUserNameAndSqx(map);
        return projectList;
    }

    @Log("根据当前登录人查询对应所拥有的个人权限项目列表")
    @CessBody
    @ApiOperation(value = "根据当前登录人查询对应所拥有的个人权限项目列表")
    @PostMapping(value = "/getProjectListByOwnerUser")
    public List<ResultProjectVO> getProjectListByOwnerUser(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        String userId = request.getHeader("userid");
        if(StringUtils.isEmpty(userId)){
            userId = SecurityUtils.getUserId();
        }
        map.put("userId", userId);
        List<ResultProjectVO> projectList = projectService.getProjectListByOwnerUser(map);
        return projectList;
    }

    @Log("根据当前登录人查询对应所拥有的个人权限项目列表及申请权限")
    @CessBody
    @ApiOperation(value = "根据当前登录人查询对应所拥有的个人权限项目列表及申请权限")
    @PostMapping(value = "/getProjectListByOwnerUserAndSqx")
    public List<ResultProjectVO> getProjectListByOwnerUserAndSqx(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        String userId = request.getHeader("userid");
        if(StringUtils.isEmpty(userId)){
            userId = SecurityUtils.getUserId();
        }
        map.put("userId", userId);
        List<ResultProjectVO> projectList = projectService.getProjectListByOwnerUserAndSqx(map);
        return projectList;
    }

    @Log("根据当前登录人查询对应所拥有的区域申请权限")
    @CessBody
    @ApiOperation(value = "根据当前登录人查询对应所拥有的区域申请权限")
    @PostMapping(value = "/getRegionListByUserNameAndSqx")
    public List<Map> getRegionListByUserNameAndSqx(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String userId = request.getHeader("userid");
        if(StringUtils.isEmpty(userId)){
            userId = SecurityUtils.getUserId();
        }
        map.put("userId", userId);
        List<Map> projectList = projectService.getRegionListByUserNameAndSqx(map);
        return projectList;
    }

    @Log("根据当前登录人查询对应所拥有的区域列表")
    @CessBody
    @ApiOperation(value = "根据当前登录人查询对应所拥有的区域列表")
    @PostMapping(value = "/getRegionByUserName")
    public List<Map> getRegionByUserName(){
        List<Map> projectList = projectService.getRegionByUserName();
        return projectList;
    }

    @Log("根据当前登录人查询对应所拥有的项目列表")
    @CessBody
    @ApiOperation(value = "查询当前登录人所拥有的项目列表")
    @PostMapping(value = "/getCityListByUser")
    public ResultBody getCityListByUser(HttpServletRequest request){
        String username = request.getHeader("username");
        return projectService.getCityListByUser(username);
    }

    @Log("查询项目列表")
    @ApiOperation(value = "查询项目列表")
    @PostMapping(value = "/getProList")
    public ResultBody getProList(@RequestBody Map map){
        return projectService.getProList(map);
    }

    @Log("查询项目详情")
    @ApiOperation(value = "查询项目详情")
    @PostMapping(value = "/getProDetail")
    public ResultBody getProDetail(@RequestBody Map map){
        return projectService.getProDetail(map);
    }

    @Log("更新项目")
    @ApiOperation(value = "更新项目")
    @PostMapping(value = "/editPro")
    public ResultBody editPro(@RequestBody Map map){
        return projectService.editPro(map);
    }

//    @Log("联动项目-新增联动项目")
//    @ApiOperation(value = "新增联动项目")
//    @PostMapping(value = "/saveTranslatePro")
//    public ResultBody saveTranslatePro(@RequestBody Map map){
//        return projectService.saveTranslatePro(map);
//    }
//
//
//    @Log("联动项目-联动项目查询")
//    @ApiOperation(value = "联动项目查询")
//    @PostMapping(value = "/getTranslatePro")
//    public ResultBody getTranslatePro(@RequestBody Map map){
//        return projectService.getTranslatePro(map);
//    }

    /**
     * 获取总监名下项目
     * @return 查询结果
     */
    @Log("获取总监名下项目")
    @CessBody
    @ApiOperation(value = "获取总监名下项目")
    @PostMapping(value = "/getZyProject")
    public List<ResultProjectVO> getZyProject(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        List<ResultProjectVO> projectList = projectService.getZyProject(map);
        return projectList;
    }

    /**
     * 获取专员名下项目
     * @return 查询结果
     */
    @Log("获取专员名下项目")
    @CessBody
    @ApiOperation(value = "获取专员名下项目")
    @PostMapping(value = "/getZygwProject")
    public List<ResultProjectVO> getZygwProject(@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        List<ResultProjectVO> projectList = projectService.getZygwProject(map);
        return projectList;
    }

    /**
     * 获取联动项目
     * @return 查询结果
     */
    @Log("获取联动项目")
    @ApiOperation(value = "获取联动项目")
    @PostMapping(value = "/getTranslateProList")
    public ResultBody getTranslateProList (@RequestBody Map<String,Object> map,HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        return projectService.getTranslateProList(map);
    }

    /**
     * 联动项目
     * @return 查询结果
     */
    @Log("联动项目")
    @ApiOperation(value = "联动项目")
    @PostMapping(value = "/saveTranslateProject")
    public ResultBody saveTranslateProject (@RequestBody TranslateProjectVo translateProjectVo, HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        return projectService.saveTranslateProject(translateProjectVo);
    }

    /**
     * 获取联动项目详情
     * @return 查询结果
     */
    @Log("联动项目")
    @ApiOperation(value = "获取联动项目详情")
    @PostMapping(value = "/getTranslateProjectInfo")
    public ResultBody getTranslateProjectInfo (@RequestBody TranslateProjectVo translateProjectVo, HttpServletRequest request){
        String authCompanyId = request.getHeader("AuthCompanyID");
        return projectService.getTranslateProjectInfo(translateProjectVo);
    }

    /**
     * 获取项目是否区域
     * @return 查询结果
     */
    @Log("联动项目")
    @ApiOperation(value = "获取项目是否区域")
    @PostMapping(value = "/getProIsRegion")
    public ResultBody getProIsRegion (@RequestBody Map map){
        return projectService.getProIsRegion(map);
    }

    /**
     * 获取全部区域
     * @return 查询结果
     */
    @Log("获取全部区域")
    @ApiOperation(value = "获取全部区域")
    @PostMapping(value = "/getAllRegionList")
    public ResultBody getAllRegionList (@RequestBody Map map){
        return projectService.getAllRegionList(map);
    }

    @Log("获取全部权限")
    @ApiOperation(value = "获取全部权限")
    @PostMapping(value = "/findFullPathAllByUser")
    public ResultBody findFullPathAllInsZs (){
        return projectService.findFullPathAllInsZs();
    }
}
