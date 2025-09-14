package cn.visolink.system.job.authorization.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.job.authorization.service.JobService;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.09
 */
@RestController
@Api(tags = "岗位授权")
@RequestMapping("system/job")
public class JobController {

    @Autowired
    private JobService jobService;

    /**
     * 获取指定岗位
     */

    @Log("获取指定岗位")
    @CessBody
    @ApiOperation(value = "获取指定岗位", notes = "获取指定岗位")
    @RequestMapping(value = "/getJobByAuthId", method = RequestMethod.GET)
    public PageInfo<Map> getJobByAuthId(@ApiParam(name = "AuthCompanyID", value = "认证公司Id") String AuthCompanyID,
                                        @ApiParam(name = "ProductID", value = "产品ID") String ProductID,
                                        @ApiParam(name = "OrgID", value = "组织ID") String OrgID,String pageSize,String pageNum,String isIdm) {
        Map map = new HashMap();
        map.put("AuthCompanyID", AuthCompanyID);
        map.put("ProductID", ProductID);
        map.put("OrgID", OrgID);
        map.put("pageSize",pageSize);
        map.put("pageNum",pageNum);
        map.put("isIdm",isIdm);
        System.out.println("======="+map);
        return jobService.getJobByAuthId(map);
    }

    /**
     * 获取是否系统管理员
     */

    @Log("获取是否系统管理员")
    @CessBody
    @ApiOperation(value = "获取是否系统管理员", notes = "获取是否系统管理员")
    @RequestMapping(value = "/getIsSys", method = RequestMethod.GET)
    public String getIsSys() {
        return jobService.getIsSys();
    }

    /**
     * 查询所有的组织结构
     */

    @Log("查询所有的组织结构")
    @CessBody
    @ApiOperation(value = "查询所有的组织结构", notes = "查询所有的组织结构")
    @RequestMapping(value = "/getAllOrg", method = RequestMethod.GET)
    public List<Map> getAllOrg(@ApiParam(name = "AuthCompanyID", value = "认证公司Id") String AuthCompanyID,
                               @ApiParam(name = "ProductID", value = "产品ID") String ProductID,
                               @ApiParam(name = "OrgID", value = "组织ID") String OrgID,
                               @ApiParam(name = "PID", value = "父ID") String PID) {
        Map map = new HashMap();
        map.put("AuthCompanyID", AuthCompanyID);
        map.put("ProductID", ProductID);
        map.put("OrgID", OrgID);
        map.put("PID", PID);
        return jobService.getAllOrg(map);
    }

    /**
     * 获取通用岗位列表
     */
    @Log("获取通用岗位列表")
    @CessBody
    @ApiOperation(value = "获取通用岗位列表", notes = "获取通用岗位列表")
    @GetMapping("/getAllCommonJob")
    public List<Map> getAllCommonJob(@ApiParam(name = "AuthCompanyID", value = "认证公司Id") String AuthCompanyID,
                                     @ApiParam(name = "ProductID", value = "产品ID") String ProductID,
                                     @ApiParam(name = "JobName", value = "岗位名称") String JobName
    ) {
        Map map = new HashMap();
        map.put("AuthCompanyID", AuthCompanyID);
        map.put("ProductID", ProductID);
        map.put("JobName", JobName);
        return jobService.getAllCommonJob(map);
    }

    /**
     * 查询岗位下的人员列表，或根据姓名查询人员
     *
     * @param reqMap
     */
    @Log("查询岗位下的人员列表，或根据姓名查询人员")
    @CessBody
    @ApiOperation(value = "查询岗位下的人员列表，或根据姓名查询人员", notes = "查询岗位下的人员列表，或根据姓名查询人员")
    @PostMapping("getSystemUserList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "UserName", value = "用户名称"),
            @ApiImplicitParam(name = "pageIndex", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量")
    })
    public Map getSystemUserList(@RequestBody Map reqMap) {
        //PageHelper.startPage();
        return jobService.getSystemUserList(reqMap);
    }

    /**
     * 查询默认岗位下的人员列表，或根据姓名/账号查询人员
     *
     * @param reqMap
     */
    @Log("查询默认岗位下的人员列表，或根据姓名/账号查询人员")
    @CessBody
    @ApiOperation(value = "查询默认岗位下的人员列表，或根据姓名/账号查询人员", notes = "查询默认岗位下的人员列表，或根据姓名/账号查询人员")
    @PostMapping("getDeSystemUserList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ProjectId", value = "项目ID"),
            @ApiImplicitParam(name = "JobCodes", value = "通用岗位Code集合"),
            @ApiImplicitParam(name = "UserName", value = "用户名称/账号"),
            @ApiImplicitParam(name = "pageIndex", value = "第几页"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量")
    })
    public Map getDeSystemUserList(@RequestBody Map reqMap) {
        return jobService.getDeSystemUserList(reqMap);
    }

    /**
     * 查询人员信息
     *
     * @param reqMap
     */
    @Log("查询人员信息")
    @CessBody
    @ApiOperation(value = "查询人员信息", notes = "查询人员信息")
    @PostMapping("getUserDesc")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ID", value = "人员ID")
    })
    public Map getUserDesc(@RequestBody Map reqMap) {
        return jobService.getUserDesc(reqMap);
    }


    /**
     * 删除岗位人员
     *
     * @param reqMap
     */
    @Log("删除岗位人员")
    @CessBody
    @ApiOperation(value = "删除岗位人员", notes = "删除岗位人员")
    @PostMapping("removeUserRel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ID", value = "岗位关联表ID")
    })
    public String removeUserRel(@RequestBody Map reqMap) {
        return jobService.removeUserRel(reqMap);
    }

    /**
     * 获取当前和下属所有组织岗位
     *
     * @param reqMap
     */
    @Log("获取当前和下属所有组织岗位")
    @CessBody
    @ApiOperation(value = "获取当前和下属所有组织岗位", notes = "获取当前和下属所有组织岗位")
    @PostMapping("getSystemJobAllList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "OrgID", value = "组织ID")

    })
    public List<Map> getSystemJobAllList(@RequestBody Map reqMap) {

        return jobService.getSystemJobAllList(reqMap);
    }

    /**
     * 新增岗位-插入Jobs信息
     *
     * @param reqMap
     */
    @Log("新增岗位-插入Jobs信息")
    @CessBody
    @ApiOperation(value = "新增岗位-插入Jobs信息", notes = "新增岗位-插入Jobs信息")
    @PostMapping("saveSystemJobForManagement")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "JobCode", value = "岗位代码"),
            @ApiImplicitParam(name = "JobName", value = "岗位名称"),
            @ApiImplicitParam(name = "JobDesc", value = "岗位描述"),
            @ApiImplicitParam(name = "JobPID", value = "上级岗位ID"),
            @ApiImplicitParam(name = "CommonJobID", value = "通用岗位ID"),
            @ApiImplicitParam(name = "JobOrgID", value = "所属组织ID"),
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "Creator", value = "创建人"),
            @ApiImplicitParam(name = "Editor", value = "编辑人"),
            @ApiImplicitParam(name = "Status", value = "状态"),

    })
    public int saveSystemJobForManagement(@RequestBody Map reqMap) {

        return jobService.saveSystemJobForManagement(reqMap);
    }

    /**
     * 组织岗位功能列表查询(前端后端功能授权)
     *
     * @param reqMap
     */
    @Log("组织岗位功能列表查询(前端后端功能授权)")
    @CessBody
    @ApiOperation(value = "组织岗位功能列表查询(前端后端功能授权)", notes = "组织岗位功能列表查询(前端后端功能授权)")
    @PostMapping("getSystemJobAuthByUserId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "UserID", value = "用户ID"),
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "menusType", value = "菜单类型"),
            @ApiImplicitParam(name = "CommomJobID", value = "通用岗位ID"),

    })
    public Map getSystemJobAuthByUserId(@RequestBody Map reqMap) {

        return jobService.getSystemJobAuthByUserId(reqMap);
    }

    /**
     * 前后端功能授权保存
     *
     * @param reqMap
     */
    @Log("前后端功能授权保存")
    @CessBody
    @ApiOperation(value = "前后端功能授权保存", notes = "前后端功能授权保存")
    @PostMapping("saveSystemJobAuthByManagement")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "OldMenus", value = "旧菜单"),
            @ApiImplicitParam(name = "OldFunctions", value = "旧功能"),
            @ApiImplicitParam(name = "Menus", value = "菜单"),
            @ApiImplicitParam(name = "Functions", value = "功能"),
            @ApiImplicitParam(name = "MenusType", value = "菜单类型"),

    })
    public String saveSystemJobAuthByManagement(@RequestBody Map reqMap) {

        return jobService.saveSystemJobAuthByManagement(reqMap);
    }

    /**
     * 更新岗位信息
     *
     * @param reqMap
     */
    @Log("更新岗位信息")
    @CessBody
    @ApiOperation(value = "更新岗位信息", notes = "更新岗位信息")
    @PostMapping("modifySystemJobByUserId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ID", value = "ID"),
            @ApiImplicitParam(name = "JobCode", value = "岗位代码"),
            @ApiImplicitParam(name = "JobName", value = "岗位名称"),
            @ApiImplicitParam(name = "JobDesc", value = "岗位描述"),
            @ApiImplicitParam(name = "JobPID", value = "上级岗位ID"),
            @ApiImplicitParam(name = "CommonJobID", value = "通用岗位ID"),
            @ApiImplicitParam(name = "JobOrgID", value = "所属组织ID"),
            @ApiImplicitParam(name = "AuthCompanyID", value = "认证公司ID"),
            @ApiImplicitParam(name = "ProductID", value = "产品ID"),
            @ApiImplicitParam(name = "Creator", value = "创建人"),
            @ApiImplicitParam(name = "Editor", value = "编辑人"),
            @ApiImplicitParam(name = "Status", value = "状态"),

    })
    public int modifySystemJobByUserId(@RequestBody Map reqMap) {

        return jobService.modifySystemJobByUserId(reqMap);
    }

    /**
     * 查询项目默认岗位信息
     *
     * @param
     */
    @Log("查询项目默认岗位信息")
    @CessBody
    @ApiOperation(value = "查询项目默认岗位信息", notes = "查询项目默认岗位信息")
    @PostMapping("getDeJobsList")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "projectId", value = "项目ID")
    })
    public List<Map> getDeJobsList(@RequestBody Map reqMap) {
        return jobService.getDeJobsList(reqMap);
    }

    /**
     * 查询项目默认岗位信息
     *
     * @param
     */
    @Log("查询默认岗位信息")
    @CessBody
    @ApiOperation(value = "查询默认岗位信息", notes = "查询默认岗位信息")
    @PostMapping("getDeComJobsList")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "jobCode", value = "当前登录人最高岗位code")
    })
    public List<Map> getDeComJobsList(@RequestBody(required = false) Map reqMap) {
        return jobService.getDeComJobsList(reqMap);
    }

    /**
     * 管理端删除岗位信息
     *
     * @param reqMap
     */
    @Log("删除岗位信息")
    @CessBody
    @ApiOperation(value = "删除岗位信息", notes = "删除岗位信息")
    @PostMapping("removeSystemJobByUserId")
    @ApiModelProperty(name = "ID", value = "用户ID")
    public int removeSystemJobByUserId(@RequestBody Map reqMap) {

        return jobService.removeSystemJobByUserId(reqMap);
    }

    /**
     * 管理端查询引入用户
     *
     * @param reqMap
     */
    @Log("查询引入用户")
    @CessBody
    @ApiOperation(value = "查询引入用户", notes = "查询引入用户")
    @PostMapping("pullinUser")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "alias", value = "账号"),
            @ApiImplicitParam(name = "usercn", value = "用户姓名"),
            @ApiImplicitParam(name = "pageIndex", value = "页码"),
            @ApiImplicitParam(name = "pageSize", value = "数量"),
            @ApiImplicitParam(name = "jobCode", value = "岗位编码")
    })
    public Map pullinUser(@RequestBody Map reqMap,HttpServletRequest request) {
        String authCompanyId = request.getHeader("AuthCompanyID");
        reqMap.put("AuthCompanyID",authCompanyId);
        return jobService.pullinUser(reqMap);
    }

    /**
     * 管理端保存用户
     *
     * @param reqMap
     */
    @Log("保存用户")
    @CessBody
    @ApiOperation(value = "保存用户", notes = "保存引入用户")
    @PostMapping("saveSystemUser")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "UserIDS", value = "用户名称"),
            @ApiImplicitParam(name = "types", value = "员工姓名")
    })
    public Map saveSystemUser(@RequestBody Map reqMap,HttpServletRequest request) {
        reqMap.put("username",request.getHeader("username"));
        return jobService.saveSystemUser(reqMap);
    }

    /**
     * 管理端保存用户(默认岗位)
     *
     * @param reqMap
     */
    @Log("保存用户(默认岗位)")
    @CessBody
    @ApiOperation(value = "保存用户(默认岗位)", notes = "保存引入用户(默认岗位)")
    @PostMapping("saveDeSystemUser")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "jobId", value = "岗位ID"),
            @ApiImplicitParam(name = "orgId", value = "组织ID"),
            @ApiImplicitParam(name = "data", value = "人员集合"),
            @ApiImplicitParam(name = "userId", value = "用户ID"),
            @ApiImplicitParam(name = "projectId", value = "项目ID"),
            @ApiImplicitParam(name = "jobCode", value = "通用岗位编码")
    })
    public int saveDeSystemUser(@RequestBody Map reqMap) {
        return jobService.saveDeSystemUser(reqMap);
    }

    /**
     * 查询用户在本项目是否引入
     *
     * @param reqMap
     */
    @Log("查询用户信息")
    @CessBody
    @ApiOperation(value = "查询用户信息", notes = "查询用户信息")
    @PostMapping("findUserDesc")
    public Map findUserDesc(@RequestBody Map reqMap){
        return jobService.findUserDesc(reqMap);
    }

    /**
     * 查询用户账号是否存在
     *
     * @param reqMap
     */
    @Log("查询用户账号是否存在")
    @CessBody
    @ApiOperation(value = "查询用户账号是否存在", notes = "查询用户账号是否存在")
    @PostMapping("selectSystemUserCode")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "alias", value = "用户账号")
    })
    public int selectSystemUserCode(@RequestBody Map reqMap){
        return jobService.selectSystemUserCode(reqMap);
    }

    /**
     * 删除岗位下的用户信息
     *
     * @param reqMap
     * @return
     */
    @Log("删除用户")
    @CessBody
    @ApiOperation(value = "删除用户", notes = "删除用户")
    @PostMapping("removeSystemJobUserRel")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "JobID", value = "岗位ID"),
            @ApiImplicitParam(name = "UserID", value = "用户ID")
    })
    public int removeSystemJobUserRel(@RequestBody Map reqMap,HttpServletRequest request) {
        reqMap.put("username",request.getHeader("username"));
        return jobService.removeSystemJobUserRel(reqMap);
    }

    /**
     * 编辑岗位下的用户信息
     *
     * @param reqMap
     * @return
     */
    @Log("编辑用户")
    @CessBody
    @ApiOperation(value = "编辑用户", notes = "编辑用户")
    @PostMapping("modifySystemJobUserRel")
    @ApiModelProperty(name = "reqMap", value ="请求参数")
    public int modifySystemJobUserRel(@RequestBody Map reqMap) {

        return jobService.modifySystemJobUserRel(reqMap);
    }

    /**
     *保存岗位下的用户信息
     *
     * @param reqMap
     * @return
     */
    @Log("保存用户")
    @CessBody
    @ApiOperation(value = "保存用户", notes = "保存用户")
    @PostMapping("saveSystemJobUserRel")
    @ApiModelProperty(name = "reqMap", value ="请求参数")
    public Map saveSystemJobUserRel(@RequestBody Map reqMap) {
        return jobService.saveSystemJobUserRel(reqMap);
    }

    /**
     *新增默认岗账号
     *
     * @param reqMap
     * @return
     */
    @Log("新增默认岗账号")
    @CessBody
    @ApiOperation(value = "新增默认岗账号", notes = "新增默认岗账号")
    @PostMapping("saveDeSystemJobUserRel")
    @ApiModelProperty(name = "reqMap", value ="请求参数")
    public int saveDeSystemJobUserRel(@RequestBody Map reqMap) {
        return jobService.saveDeSystemJobUserRel(reqMap);
    }


    /**
    * 获取所有菜单
    * */

    @Log("获取功能菜单")
    @ApiOperation(value = "获取功能菜单", notes = "获取功能菜单")
    @PostMapping("/menu/list")
    @ApiModelProperty(name = "jobId", value ="角色ID")
    public ResultBody getAllMenus(@RequestBody Map map,HttpServletRequest request) {
        return jobService.getAllMenu(map.get("jobId").toString());
    }
    /**
     * 保存所有菜单
     * */

    @Log("保存所有菜单")
    @ApiOperation(value = "保存所有菜单", notes = "保存所有菜单")
    @PostMapping("/menu/save")
    @ApiModelProperty(name = "jobId", value ="角色ID")
    public ResultBody saveJobMenus(@RequestBody Map map) {
        return jobService.saveJobMenus(map,map.get("jobId").toString());
    }

    /**
     * 保存通用菜单
     * */

    @Log("保存通用菜单")
    @ApiOperation(value = "保存通用菜单", notes = "保存通用菜单")
    @PostMapping("common/menu/save")
    @ApiModelProperty(name = "jobId", value ="角色ID")
    public ResultBody saveCommonJobMenus(@RequestBody Map map) {
        return jobService.saveCommonJobMenus(map,map.get("jobId").toString());
    }

    /***
     * 获取通用岗位菜单
     * **/
    @Log("获取通用功能菜单")
    @ApiOperation(value = "获取通用功能菜单", notes = "获取通用功能菜单")
    @PostMapping("/common/menu")
    @ApiModelProperty(name = "jobId", value ="角色ID")
    public ResultBody getCommonAllMenus(@RequestBody Map map,HttpServletRequest request) {
        //String productId = request.getHeader("ProductID");
        String type = "";
        if (map.get("type")!=null && !"".equals(map.get("type")+"")){
            type = map.get("type")+"";
        }
        return jobService.getCommonAllMenu(map.get("jobId").toString(),type);
    }

    /***
     * 获取所有中介公司
     * **/
    @Log("获取所有中介公司")
    @ApiOperation(value = "获取所有中介公司", notes = "获取所有中介公司")
    @GetMapping("/company/all")
    @ApiModelProperty(name = "orgId", value ="组织ID")
    public ResultBody getAllCompanyInfo(String orgId,String id) {
        return  jobService.getAllCompanyInfo(orgId,id);
    }


    /***
     * 项目组织
     * **/
    @Log("项目组织")
    @ApiOperation(value = "项目组织", notes = "项目组织")
    @GetMapping("/org/project")
    public ResultBody getAllOrgProject(HttpServletRequest request) {
        return  jobService.getAllOrgProject();
    }
    /***
     * 项目组织
     * **/
    @Log("区域集团/事业部")
    @ApiOperation(value = "区域集团/事业部", notes = "区域集团/事业部")
    @GetMapping("/org/project2")
    public ResultBody getAllOrgProject2(HttpServletRequest request) {
        return  jobService.getAllOrgProject2();
    }

    /***
     * 更新项目
     * **/
    @Log("更新项目")
    @ApiOperation(value = "更新项目", notes = "更新项目")
    @PostMapping("/project/update")
    public ResultBody updateProject(@RequestBody Map map){
        System.out.println(map);
        String toker="";
        String anchang="";
        if(map.get("checkList").toString().indexOf("1")!=-1){
            toker+="1,";
        }
        if(map.get("checkList").toString().indexOf("2")!=-1){
            toker+="2,";
        }
        if(map.get("checkList").toString().indexOf("3")!=-1){
            toker+="3,";
        }
        if(map.get("checkList").toString().indexOf("4")!=-1){
            toker+="4,";
        }
        if(map.get("checkList").toString().indexOf("5")!=-1){
            toker+="5,";
        }
        if(map.get("checkList").toString().indexOf("6")!=-1){
            toker+="6,";
        }
        if(map.get("checkList").toString().indexOf("7")!=-1){
            toker+="7,";
        }
        if(map.get("checkList").toString().indexOf("8")!=-1){
            toker+="8,";
        }
        if(map.get("checkListwo").toString().indexOf("1")!=-1){
            anchang+="1,";
        }
        if(map.get("checkListwo").toString().indexOf("2")!=-1){
            anchang+="2,";
        }
        if(map.get("checkListwo").toString().indexOf("3")!=-1){
            anchang+="3,";
        }
        if(map.get("checkListwo").toString().indexOf("4")!=-1){
            anchang+="4,";
        }
        if(map.get("checkListwo").toString().indexOf("5")!=-1){
            anchang+="5,";
        }
        if(map.get("checkListwo").toString().indexOf("6")!=-1){
            anchang+="6,";
        }
        if(map.get("checkListwo").toString().indexOf("7")!=-1){
            anchang+="7,";
        }
        if(map.get("checkListwo").toString().indexOf("8")!=-1){
            anchang+="8,";
        }
        map.put("tokerResetType",toker);
        map.put("anChangResetType",anchang);
        if(map.get("projectStatus").toString().equals("在售")){
           map.put("projectStatus",5001);
        }
        if(map.get("projectStatus").toString().equals("待售")){
           map.put("projectStatus",5002);
        }
        if(map.get("projectStatus").toString().equals("热销")){
           map.put("projectStatus",5003);
        }
        if(map.get("projectStatus").toString().equals("售罄")){
           map.put("projectStatus",5004);
        }
        if(map.get("status").toString().equals("启用")){
           map.put("status",1);
        }
        if(map.get("status").toString().equals("禁用")){
           map.put("status",2);
        }
//        if(map.get("IsPrintStatus").toString().equals("启用") || map.get("IsPrintStatus").toString().equals("1")){
//            map.put("IsPrintStatus",1);
//        }else{
//            map.put("IsPrintStatus",0);
//        }
        System.out.println(toker);
        System.out.println(anchang);
        ResultBody resultBody = jobService.updateProject(map);
        //如果启用项目同步城市信息
        if ("1".equals(map.get("status")+"")){

        }
      return resultBody;
       // return null;
    }

    /***
     * 初始化项目
     * **/
    @Log("初始化项目")
    @ApiOperation(value = "初始化项目", notes = "初始化项目")
    @PostMapping("/project/updateNew")
    public ResultBody updateProjectNew(@RequestBody Map map){
        System.out.println(map);
        String toker="";
        String anchang="";
        if(map.get("checkList")!=null){
            toker = map.get("checkList").toString();
        }
        if(map.get("checkListwo")!=null){
            anchang = map.get("checkListwo").toString();
        }
        map.put("tokerResetType",toker);
        map.put("anChangResetType",anchang);
        map.put("status",1);
        ResultBody resultBody = jobService.updateProjectNew(map);
        return resultBody;
    }

    /***
     * 获取对应项目岗位数据
     *
     * **/
    @Log("获取对应项目岗位数据")
    @ApiOperation(value = "获取对应项目岗位数据", notes = "获取对应项目岗位数据")
    @PostMapping("/common/currentJobs")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "projectId", value = "项目ID"),
            @ApiImplicitParam(name = "userId", value = "用户ID")
    })
    public ResultBody getCurrentJobs(@RequestBody Map map) {
        return jobService.getCurrentJobs(map);
    }


    /***
     * 获取岗位的城市数据
     *
     * **/
    @Log("获取岗位的城市数据")
    @ApiOperation(value = "获取岗位的城市数据", notes = "获取岗位的城市数据")
    @PostMapping("/getCityJobList")
    public ResultBody getCityJobList(@RequestBody Map map) {
        return jobService.getCityJobList(map);
    }

    /***
     * 保存岗位与城市的关系
     *
     * **/
    @Log("保存岗位与城市的关系")
    @ApiOperation(value = "保存岗位与城市的关系", notes = "保存岗位与城市的关系")
    @PostMapping("/saveCityJob")
    public ResultBody saveCityJob(@RequestBody Map map) {
        return jobService.saveCityJob(map);
    }

    /***
     * 保存岗位与城市的关系
     *
     * **/
    @Log("大客户经理二级经纪人移动")
    @ApiOperation(value = "大客户经理二级经纪人移动", notes = "大客户经理二级经纪人移动")
    @PostMapping("/moveBrokerAccount")
    public String saveBrokerAccountRecords(@ApiParam(name = "userId", value = "用户id")String userId,
                                           @ApiParam(name = "projectId", value = "主项目ID")String projectId) {
        return jobService.saveBrokerAccountRecords(userId,projectId);
    }

    /**
     * 管理端保存用户
     *
     * @param reqMap {"jobsList": [{"jobCode": "20001", "jobId": "928e38d2-fd70-11e9-8e3f-005056a3c9de"}], "AccountType": 2, "Status": 1, "Gender": 1, "EmployeeCode": "f都", "UserName": "f第三方的", "EmployeeName": "ft环境", "authCompanyId": "ede1b679-3546-11e7-a3f8-5254007b6f02", "productId": "ee3b2466-3546-11e7-a3f8-5254007b6f02", "projectId": "F365AEFD-D0F7-405E-9220-4BA768C65CD3", "orgId": "55114361317", "userId": "e7cf453b-214e-4500-bf99-58ea0cc9b4e5", "Mobile": "439569", "OfficeTel": "439569", "OfficeMail": "fd", "PostCode": "fd", "Password": "", "Address": "上海"}
     */
    @Log("保存用户-岗位")
    @CessBody
    @ApiOperation(value = "保存用户-岗位")
    @PostMapping("saveUserJob")
    public Map saveUserJob(@RequestBody Map reqMap,HttpServletRequest request) {
        reqMap.put("username",request.getHeader("username"));
        return jobService.saveUserJob(reqMap);
    }

    /**
     * 查询所有的岗位
     *
     * @param
     */
    @Log("查询所有的岗位")
    @CessBody
    @ApiOperation(value = "查询所有的岗位")
    @PostMapping("selectJobsList")
    public ResultBody selectJobsList(@RequestBody Map map) {
        return jobService.selectJobsList(map);
    }

    /**
     * 导出用户与岗位信息
     */
    @Log("导出用户与岗位信息")
    @ApiOperation(value = "导出用户与岗位信息", notes = "导出用户与岗位信息")
    @PostMapping("/ExportUsersAnd")
    public void ExportUsersAnd(HttpServletResponse response) {
        jobService.ExportUsersAnd(response);
    }
}
