package cn.visolink.system.companyQw.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.companyQw.model.*;
import cn.visolink.system.companyQw.service.CompanyQwService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * @ClassName CompanyController
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/12/31 9:33
 **/
@RestController
@Api(tags = "企微数据配置")
@RequestMapping("/qw")
public class CompanyQwController {

    @Autowired
    private CompanyQwService companyQwService;

    @Log("查询小程序")
    @CessBody
    @ApiOperation(value = "查询小程序", notes = "查询小程序")
    @RequestMapping(value = "/getComApplet", method = RequestMethod.GET)
    public List<Map> getComApplet(HttpServletRequest request) {
        return companyQwService.getComApplet(request);
    }

    @Log("查询小程序页面")
    @CessBody
    @ApiOperation(value = "查询小程序页面", notes = "查询小程序页面")
    @RequestMapping(value = "/getComAppletPage", method = RequestMethod.GET)
    public List<Map> getComAppletPage() {
        return companyQwService.getComAppletPage();
    }

    @Log("查询小程序页面路径")
    @CessBody
    @ApiOperation(value = "查询小程序页面路径", notes = "查询小程序页面路径")
    @RequestMapping(value = "/getComAppletPagePath", method = RequestMethod.POST)
    public ResultBody getComAppletPagePath(HttpServletRequest request,
                                  @ApiParam(name = "map", value = "{\"projectId\":\"项目id\",\"pageType\":\"页面类型（1：活动 2：新闻 3：楼盘 4：户型）\"}")
                                  @RequestBody Map map) {
        return companyQwService.getComAppletPagePath(request,map);
    }

    @Log("保存项目素材")
    @CessBody
    @ApiOperation(value = "保存项目素材", notes = "保存项目素材")
    @RequestMapping(value = "/addProMedia", method = RequestMethod.POST)
    public ResultBody addProMedia(HttpServletRequest request,@RequestBody ProMediaVo proMediaVo) {
        return companyQwService.addProMedia(request,proMediaVo);
    }

    @Log("项目素材列表")
    @CessBody
    @ApiOperation(value = "项目素材列表", notes = "项目素材列表")
    @RequestMapping(value = "/getProMediaList", method = RequestMethod.POST)
    public ResultBody getProMediaList(HttpServletRequest request,
                                      @ApiParam(name = "map", value = "{\"projectIds\":\"项目id逗号隔开\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                      @RequestBody Map map) {
        return companyQwService.getProMediaList(request,map);
    }

    @Log("获取项目素材详情")
    @CessBody
    @ApiOperation(value = "获取项目素材详情", notes = "获取项目素材详情")
    @RequestMapping(value = "/getProMediaDetail", method = RequestMethod.POST)
    public ResultBody getProMediaDetail(HttpServletRequest request,
                                      @ApiParam(name = "map", value = "{\"projectId\":\"项目id\"}")
                                      @RequestBody Map map) {
        return companyQwService.getProMediaDetail(request,map);
    }

    @Log("获取项目是否已设置素材")
    @CessBody
    @ApiOperation(value = "获取项目是否已设置素材", notes = "获取项目是否已设置素材")
    @RequestMapping(value = "/getProMediaIsOk", method = RequestMethod.POST)
    public ResultBody getProMediaIsOk(HttpServletRequest request,
                                        @ApiParam(name = "map", value = "{\"projectId\":\"项目id\"}")
                                        @RequestBody Map map) {
        return companyQwService.getProMediaIsOk(request,map);
    }

    @Log("保存敏感词分类")
    @CessBody
    @ApiOperation(value = "保存敏感词分类", notes = "保存敏感词分类")
    @RequestMapping(value = "/addSensitiveWordsType", method = RequestMethod.POST)
    public ResultBody addSensitiveWordsType(HttpServletRequest request,
                                        @ApiParam(name = "map", value = "{\"sensitiveWordsType\":\"分类名称\"}")
                                        @RequestBody Map map) {
        if (map==null || map.get("sensitiveWordsType") ==null || "".equals(map.get("sensitiveWordsType")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        String sensitiveWordsType = map.get("sensitiveWordsType")+"";
        return companyQwService.addSensitiveWordsType(request,sensitiveWordsType);
    }

    @Log("新增、编辑敏感关键词")
    @CessBody
    @ApiOperation(value = "新增、编辑敏感关键词", notes = "新增、编辑敏感关键词")
    @RequestMapping(value = "/addOrEditSensitiveWord", method = RequestMethod.POST)
    public ResultBody addOrEditSensitiveWord(HttpServletRequest request,
                                        @RequestBody SensitiveWordVo sensitiveWordVo) {
        return companyQwService.addOrEditSensitiveWord(request,sensitiveWordVo);
    }

    @Log("删除敏感关键词")
    @CessBody
    @ApiOperation(value = "删除敏感关键词", notes = "删除敏感关键词")
    @RequestMapping(value = "/delSensitiveWord", method = RequestMethod.POST)
    public ResultBody delSensitiveWord(HttpServletRequest request,
                                        @ApiParam(name = "map", value = "{\"id\":\"敏感关键词id\"}")
                                        @RequestBody Map map) {
        if (map==null || map.get("id") ==null || "".equals(map.get("id")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        String id = map.get("id")+"";
        return companyQwService.delSensitiveWord(request,id);
    }

    @Log("查询敏感关键词列表")
    @CessBody
    @ApiOperation(value = "查询敏感关键词列表", notes = "查询敏感关键词列表")
    @RequestMapping(value = "/getSensitiveWords", method = RequestMethod.POST)
    public ResultBody getSensitiveWords(HttpServletRequest request,
                                        @ApiParam(name = "map", value = "{\"projectIds\":\"项目id逗号隔开\",\"ruleName\":\"规则名称\",\"wordList\":\"关键词\",\"typeId\":\"分类Id\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                        @RequestBody Map map) {
        return companyQwService.getSensitiveWords(request,map);
    }

    @Log("查询敏感关键词分类")
    @CessBody
    @ApiOperation(value = "查询敏感关键词分类", notes = "查询敏感关键词分类")
    @RequestMapping(value = "/getSensitiveWordsType", method = RequestMethod.GET)
    public ResultBody getSensitiveWordsType() {
        return companyQwService.getSensitiveWordsType();
    }

    @Log("查询项目渠道码列表")
    @CessBody
    @ApiOperation(value = "查询项目渠道码列表", notes = "查询项目渠道码列表")
    @RequestMapping(value = "/getChannelCodeList", method = RequestMethod.POST)
    public ResultBody getChannelCodeList(@ApiParam(name = "map", value = "{\"projectIds\":\"项目id逗号隔开\",\"state\":\"渠道\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                             @RequestBody Map map) {
        return companyQwService.getChannelCodeList(map);
    }

    @Log("新增或编辑渠道码")
    @CessBody
    @ApiOperation(value = "新增或编辑渠道码", notes = "新增或编辑渠道码")
    @RequestMapping(value = "/addOrEditChannelCode", method = RequestMethod.POST)
    public ResultBody addOrEditChannelCode(HttpServletRequest request,@RequestBody ChannelCode channelCode) {
        return companyQwService.addOrEditChannelCode(request,channelCode);
    }

    @Log("删除渠道码")
    @CessBody
    @ApiOperation(value = "删除渠道码", notes = "删除渠道码")
    @RequestMapping(value = "/delChannelCode", method = RequestMethod.POST)
    public ResultBody delChannelCode(HttpServletRequest request,@ApiParam(name = "map", value = "{\"id\":\"渠道码ID\"}")
                                         @RequestBody Map map) {
        String id = "";
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")){
            return ResultBody.error(-1200002,"渠道码ID未传！！");
        }
        id = map.get("id")+"";
        return companyQwService.delChannelCode(request,id);
    }

    @Log("分发员工")
    @CessBody
    @ApiOperation(value = "分发员工", notes = "分发员工")
    @RequestMapping(value = "/distributeEmployees", method = RequestMethod.POST)
    public ResultBody distributeEmployees(HttpServletRequest request,
            @ApiParam(name = "map", value = "{\"id\":\"渠道码ID\"}")
            @RequestBody Map map) {
        String id = "";
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")){
            return ResultBody.error(-1200002,"渠道码ID未传！！");
        }
        id = map.get("id")+"";
        return companyQwService.distributeEmployees(request,id);
    }

    @Log("查询员工")
    @CessBody
    @ApiOperation(value = "查询员工", notes = "查询员工")
    @RequestMapping(value = "/getEmployees", method = RequestMethod.POST)
    public ResultBody getEmployees(HttpServletRequest request,
                                          @ApiParam(name = "map", value = "{\"projectId\":\"项目ID\"}")
                                          @RequestBody Map map) {
        String projectId = "";
        if (map==null || map.get("projectId")==null || "".equals(map.get("projectId")+"")){
            return ResultBody.error(-1200002,"项目ID未传！！");
        }
        projectId = map.get("projectId")+"";
        return companyQwService.getEmployees(projectId);
    }

    @Log("查询渠道码详情")
    @CessBody
    @ApiOperation(value = "查询渠道码详情", notes = "查询渠道码详情")
    @RequestMapping(value = "/getChannelCode", method = RequestMethod.POST)
    public ResultBody getChannelCode(HttpServletRequest request,
                                   @ApiParam(name = "map", value = "{\"id\":\"渠道码ID\"}")
                                   @RequestBody Map map) {
        String id = "";
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")){
            return ResultBody.error(-1200002,"渠道码ID未传！！");
        }
        id = map.get("id")+"";
        return companyQwService.getChannelCode(id);
    }

    @Log("查询部门列表")
    @CessBody
    @ApiOperation(value = "查询部门列表", notes = "查询部门列表")
    @RequestMapping(value = "/getDeptList", method = RequestMethod.POST)
    public ResultBody getDeptList(@ApiParam(name = "map", value = "{\"deptName\":\"部门名称\",\"orgName\":\"组织名称\",\"status\":\"是否绑定（1：绑定 2：未绑定）\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                         @RequestBody Map map) {
        return companyQwService.getDeptList(map);
    }

    @Log("删除部门")
    @CessBody
    @ApiOperation(value = "删除部门", notes = "删除部门")
    @RequestMapping(value = "/delDept", method = RequestMethod.POST)
    public ResultBody delDept(HttpServletRequest request,@ApiParam(name = "map", value = "{\"id\":\"部门ID\"}")
    @RequestBody Map map) {
        String id = "";
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")){
            return ResultBody.error(-1200002,"部门ID未传！！");
        }
        id = map.get("id")+"";
        return companyQwService.delDept(request,id);
    }

    @Log("查询父部门下组织信息")
    @CessBody
    @ApiOperation(value = "查询父部门下组织信息", notes = "查询父部门下组织信息")
    @RequestMapping(value = "/getPDeptOrg", method = RequestMethod.POST)
    public ResultBody getPDeptOrg(HttpServletRequest request,@ApiParam(name = "map", value = "{\"parentid\":\"父部门ID\"}")
    @RequestBody Map map) {
        String id = "";
        if (map==null || map.get("parentid")==null || "".equals(map.get("parentid")+"")){
            return ResultBody.error(-1200002,"父部门ID未传！！");
        }
        id = map.get("parentid")+"";
        return companyQwService.getPDeptOrg(request,id);
    }



    @Log("部门绑定、解绑")
    @CessBody
    @ApiOperation(value = "部门绑定、解绑", notes = "部门绑定、解绑")
    @RequestMapping(value = "/deptBinding", method = RequestMethod.POST)
    public ResultBody deptBinding(HttpServletRequest request,@ApiParam(name = "map", value = "{\"id\":\"部门ID\",\"orgId\":\"组织ID\",\"projectId\":\"项目ID\",\"type\":\"类型（1：绑定 2：解绑）\"}")
    @RequestBody Map map) {
        String id = "";
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")
                || map.get("orgId")==null || "".equals(map.get("orgId")+"")
                || map.get("type")==null || "".equals(map.get("type")+"")){
            return ResultBody.error(-1200002,"部门ID或组织ID或类型未传！！");
        }
        return companyQwService.deptBinding(request,map);
    }


    @Log("查询离职成员列表")
    @CessBody
    @ApiOperation(value = "查询离职成员列表", notes = "查询离职成员列表")
    @RequestMapping(value = "/getQuitUserList", method = RequestMethod.POST)
    public ResultBody getQuitUserList(@ApiParam(name = "map", value = "{\"projectIds\":\"项目ID逗号分隔\",\"startTime\":\"离职开始时间\",\"endTime\":\"离职结束时间\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                  @RequestBody Map map) {
        if (map==null || map.get("projectIds")==null){
            return ResultBody.error(-120002,"项目ID未传！！");
        }
        return companyQwService.getQuitUserList(map);
    }

    @Log("分配明细查询")
    @CessBody
    @ApiOperation(value = "分配明细查询", notes = "分配明细查询")
    @RequestMapping(value = "/quitUserRedistDetail", method = RequestMethod.POST)
    public ResultBody quitUserRedistDetail(@ApiParam(name = "map", value = "{\"useridOld\":\"原成员ID\",\"cstName\":\"客户名称\",\"userNameNew\":\"继承成员名称\",\"status\":\"客户状态逗号分隔\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                             @RequestBody Map map) {
        if (map==null || map.get("useridOld")==null){
            return ResultBody.error(-120002,"原成员ID未传！！");
        }
        return companyQwService.quitUserRedistDetail(map);
    }

    @Log("离职重分配")
    @CessBody
    @ApiOperation(value = "离职重分配", notes = "离职重分配")
    @RequestMapping(value = "/quitUserRedistribution", method = RequestMethod.POST)
    public ResultBody quitUserRedistribution(HttpServletRequest request,
            @ApiParam(name = "map", value = "{\"useridOld\":\"原成员ID\",\"useridNew\":\"继承成员ID\",\"externalUserid\":\"客户ID逗号隔开\"}")
                                  @RequestBody Map map) {
        if (map==null || map.get("useridOld")==null || "".equals(map.get("useridOld")+"")){
            return ResultBody.error(-120002,"原成员ID未传！！");
        }
        if (map.get("useridNew")==null || "".equals(map.get("useridNew")+"")){
            return ResultBody.error(-120002,"继承成员ID未传！！");
        }
        if (map.get("externalUserid")==null || "".equals(map.get("externalUserid")+"")){
            return ResultBody.error(-120002,"客户ID未传！！");
        }else{
            String externalUserid = map.get("externalUserid")+"";
            String[] ids = externalUserid.split(",");
            if (ids.length>100){
                return ResultBody.error(-120002,"客户最大分配数100人，请重新选择！！");
            }
        }
        return companyQwService.quitUserRedistribution(request,map);
    }

    @Log("查询项目员工")
    @CessBody
    @ApiOperation(value = "查询项目员工", notes = "查询项目员工")
    @RequestMapping(value = "/getProEmployees", method = RequestMethod.POST)
    public ResultBody getProEmployees(HttpServletRequest request,
                                   @ApiParam(name = "map", value = "{\"projectId\":\"项目ID\",\"userName\":\"成员姓名\"}")
                                   @RequestBody Map map) {
        if (map==null || map.get("projectId")==null || "".equals(map.get("projectId")+"")){
            return ResultBody.error(-1200002,"项目ID未传！！");
        }
        return companyQwService.getProEmployees(map);
    }

    @Log("新增、编辑企业客户标签")
    @CessBody
    @ApiOperation(value = "新增、编辑企业客户标签", notes = "新增、编辑企业客户标签")
    @RequestMapping(value = "/addOrEditTag", method = RequestMethod.POST)
    public ResultBody addOrEditTag(HttpServletRequest request,
                                             @RequestBody QwCstTag qwCstTag) {
        return companyQwService.addOrEditTag(request,qwCstTag);
    }

    @Log("删除企业客户标签")
    @CessBody
    @ApiOperation(value = "删除企业客户标签", notes = "删除企业客户标签")
    @RequestMapping(value = "/delQwCstTag", method = RequestMethod.POST)
    public ResultBody delQwCstTag(HttpServletRequest request,
                                       @ApiParam(name = "map", value = "{\"tagId\":\"企业客户标签id\"}")
                                       @RequestBody Map map) {
        if (map==null || map.get("tagId") ==null || "".equals(map.get("tagId")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        String tagId = map.get("tagId")+"";
        return companyQwService.delQwCstTag(request,tagId);
    }

    @Log("查询企业客户标签列表")
    @CessBody
    @ApiOperation(value = "查询企业客户标签列表", notes = "查询企业客户标签列表")
    @RequestMapping(value = "/getQwCstTags", method = RequestMethod.POST)
    public ResultBody getQwCstTags(HttpServletRequest request,
                                        @ApiParam(name = "map", value = "{\"tagName\":\"标签名称\",\"groupIds\":\"分组Id逗号隔开\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                        @RequestBody Map map) {
        return companyQwService.getQwCstTags(request,map);
    }

    @Log("查询企业客户标签分组")
    @CessBody
    @ApiOperation(value = "查询企业客户标签分组", notes = "查询企业客户标签分组")
    @RequestMapping(value = "/getQwCstTagGroup", method = RequestMethod.GET)
    public ResultBody getQwCstTagGroup() {
        return companyQwService.getQwCstTagGroup();
    }

    @Log("保存企业客户标签分组")
    @CessBody
    @ApiOperation(value = "保存企业客户标签分组", notes = "保存企业客户标签分组")
    @RequestMapping(value = "/addQwCstTagGroup", method = RequestMethod.POST)
    public ResultBody addQwCstTagGroup(HttpServletRequest request,
                                            @ApiParam(name = "map", value = "{\"groupName\":\"分组名称\"}")
                                            @RequestBody Map map) {
        if (map==null || map.get("groupName") ==null || "".equals(map.get("groupName")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        String groupName = map.get("groupName")+"";
        return companyQwService.addQwCstTagGroup(request,groupName);
    }

    @Log("查询企业客服列表")
    @CessBody
    @ApiOperation(value = "查询企业客服列表", notes = "查询企业客服列表")
    @RequestMapping(value = "/getQwCstService", method = RequestMethod.POST)
    public ResultBody getQwCstService() {
        return companyQwService.getQwCstService();
    }

    @Log("查询企业客服人员列表")
    @CessBody
    @ApiOperation(value = "查询企业客服人员列表", notes = "查询企业客服人员列表")
    @RequestMapping(value = "/getQwCstServiceUser", method = RequestMethod.POST)
    public ResultBody getQwCstServiceUser(HttpServletRequest request,
                                   @ApiParam(name = "map", value = "{\"userName\":\"成员名称\",\"openKfid\":\"客服Id\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                   @RequestBody Map map) {
        return companyQwService.getQwCstServiceUser(request,map);
    }

    @Log("保存/编辑企业客服")
    @CessBody
    @ApiOperation(value = "保存/编辑企业客服", notes = "保存/编辑企业客服")
    @RequestMapping(value = "/addOrEditCstService", method = RequestMethod.POST)
    public ResultBody addOrEditCstService(HttpServletRequest request,
                                       @ApiParam(name = "map", value = "{\"custServiceName\":\"客服名称\",\"openKfid\":\"客服ID\",\"avatar\":\"客服头像路径\"}")
                                       @RequestBody Map map) {
        if (map==null || map.get("custServiceName") ==null || "".equals(map.get("custServiceName")+"")
                || map.get("avatar") ==null || "".equals(map.get("avatar")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        return companyQwService.addOrEditCstService(request,map);
    }

    @Log("删除客服人员")
    @CessBody
    @ApiOperation(value = "删除客服人员", notes = "删除客服人员")
    @RequestMapping(value = "/delServiceUser", method = RequestMethod.POST)
    public ResultBody delServiceUser(HttpServletRequest request,
                                          @ApiParam(name = "map", value = "{\"userid\":\"客服人员ID\",\"openKfid\":\"客服ID\"}")
                                          @RequestBody Map map) {
        if (map==null || map.get("openKfid") ==null || "".equals(map.get("openKfid")+"")
                || map.get("userid") ==null || "".equals(map.get("userid")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        return companyQwService.delServiceUser(request,map);
    }

    @Log("新增客服人员")
    @CessBody
    @ApiOperation(value = "新增客服人员", notes = "新增客服人员")
    @RequestMapping(value = "/addServiceUser", method = RequestMethod.POST)
    public ResultBody addServiceUser(HttpServletRequest request,
                                          @ApiParam(name = "map", value = "{\"userids\":\"成员ID逗号隔开\",\"openKfid\":\"客服ID\",\"projectId\":\"项目ID\"}")
                                          @RequestBody Map map) {
        if (map==null || map.get("openKfid") ==null || "".equals(map.get("openKfid")+"")
                || map.get("projectId") ==null || "".equals(map.get("projectId")+"")
                || map.get("userids") ==null || "".equals(map.get("userids")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        return companyQwService.addServiceUser(request,map);
    }

    @Log("查询客服自动回复列表")
    @CessBody
    @ApiOperation(value = "查询客服自动回复列表", notes = "查询客服自动回复列表")
    @RequestMapping(value = "/getQwServiceAutoReply", method = RequestMethod.POST)
    public ResultBody getQwServiceAutoReply(HttpServletRequest request,
                                          @ApiParam(name = "map", value = "{\"ruleName\":\"规则名称\",\"projectIds\":\"项目ID逗号隔开\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                          @RequestBody Map map) {
        return companyQwService.getQwServiceAutoReply(request,map);
    }

    @Log("新增/编辑客服自动回复")
    @CessBody
    @ApiOperation(value = "新增/编辑客服自动回复", notes = "新增/编辑客服自动回复")
    @RequestMapping(value = "/addOrEditAutoReply", method = RequestMethod.POST)
    public ResultBody addOrEditAutoReply(HttpServletRequest request,
                                            @RequestBody QwServiceAutoReply qwServiceAutoReply) {
        return companyQwService.addOrEditAutoReply(request,qwServiceAutoReply);
    }

    @Log("删除客服自动回复")
    @CessBody
    @ApiOperation(value = "删除客服自动回复", notes = "删除客服自动回复")
    @RequestMapping(value = "/delAutoReply", method = RequestMethod.POST)
    public ResultBody delAutoReply(HttpServletRequest request,
                                         @ApiParam(name = "map", value = "{\"ruleId\":\"规则ID\"}")
                                         @RequestBody Map map) {
        if (map==null || map.get("ruleId") ==null || "".equals(map.get("ruleId")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        return companyQwService.delAutoReply(request,map);
    }

    @Log("获取客服自动回复详情")
    @CessBody
    @ApiOperation(value = "获取客服自动回复详情", notes = "获取客服自动回复详情")
    @RequestMapping(value = "/getAutoReply", method = RequestMethod.POST)
    public ResultBody getAutoReply(HttpServletRequest request,
                                   @ApiParam(name = "map", value = "{\"ruleId\":\"规则ID\"}")
                                   @RequestBody Map map) {
        if (map==null || map.get("ruleId") ==null || "".equals(map.get("ruleId")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        return companyQwService.getAutoReply(request,map);
    }

    @Log("获取渠道码统计数据")
    @CessBody
    @ApiOperation(value = "获取渠道码统计数据", notes = "获取渠道码统计数据")
    @RequestMapping(value = "/getChannelCodeStatistics", method = RequestMethod.POST)
    public ResultBody getChannelCodeStatistics(HttpServletRequest request,
                                   @ApiParam(name = "map", value = "{\"projectIds\":\"项目ID逗号隔开\",\"state\":\"渠道码名称\",\"startTime\":\"开始时间\",\"endTime\":\"结束时间\"}")
                                   @RequestBody Map map) {
        if (map==null || map.get("projectIds") ==null || "".equals(map.get("projectIds")+"")
                || map.get("state") ==null || "".equals(map.get("state")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        return companyQwService.getChannelCodeStatistics(request,map);
    }

    @Log("获取渠道码成员明细")
    @CessBody
    @ApiOperation(value = "获取渠道码成员明细", notes = "获取渠道码成员明细")
    @RequestMapping(value = "/getChannelCodeUserDetail", method = RequestMethod.POST)
    public ResultBody getChannelCodeUserDetail(HttpServletRequest request,
                                   @ApiParam(name = "map", value = "{\"projectIds\":\"项目ID逗号隔开\",\"state\":\"渠道码名称\",\"startTime\":\"开始时间\",\"endTime\":\"结束时间\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                   @RequestBody Map map) {
        if (map==null || map.get("projectIds") ==null || "".equals(map.get("projectIds")+"")
                || map.get("state") ==null || "".equals(map.get("state")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        return companyQwService.getChannelCodeUserDetail(request,map);
    }

    @Log("渠道码成员明细导出")
    @CessBody
    @ApiOperation(value = "渠道码成员明细导出", notes = "渠道码成员明细导出")
    @PostMapping(value = "/channelCodeUserDetailExport")
    public void channelCodeUserDetailExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        companyQwService.channelCodeUserDetailExport(request,response, param);
    }

    @Log("获取渠道码折线图数据")
    @CessBody
    @ApiOperation(value = "获取渠道码折线图数据", notes = "获取渠道码折线图数据")
    @RequestMapping(value = "/getChannelCodeLineChart", method = RequestMethod.POST)
    public ResultBody getChannelCodeLineChart(HttpServletRequest request,
                                   @ApiParam(name = "map", value = "{\"projectIds\":\"项目ID逗号隔开\",\"state\":\"渠道码名称\",\"startTime\":\"开始时间\",\"endTime\":\"结束时间\",\"type\":\"查询类型（1：总数（默认） 2：新增数 3：流失数）\"}")
                                   @RequestBody Map map) {
        if (map==null || map.get("projectIds") ==null || "".equals(map.get("projectIds")+"")
                || map.get("state") ==null || "".equals(map.get("state")+"")){
            ResultBody.error(-1200002,"必传参数为空！！");
        }
        return companyQwService.getChannelCodeLineChart(request,map);
    }


}
