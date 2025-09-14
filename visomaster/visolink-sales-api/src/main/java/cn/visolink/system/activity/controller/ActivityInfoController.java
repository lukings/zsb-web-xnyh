package cn.visolink.system.activity.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.activity.model.form.ActivityInfoForm;
import cn.visolink.system.activity.model.form.RidActivityFrom;
import cn.visolink.system.activity.model.vo.*;
import cn.visolink.system.activity.service.ActivityInfoService;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import cn.visolink.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ActivityInfo前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2020-05-25
 */
@RestController
@Api(tags = "ActivityInfo")
@RequestMapping("/activity/activityInfo")
public class ActivityInfoController {

@Autowired
public ActivityInfoService activityInfoService;

    /**
    * 根据主键id查询单条
    * @param id 主键id
    * @return 查询结果
    */

    @Log("根据主键id查询单条ActivityInfo")
    @CessBody
    @ApiOperation(value = "获取单条数据", notes = "根据主键id获取ActivityInfo数据")
    @RequestMapping(value = "/getById", method = RequestMethod.POST)
    public ActivityInfoVO getActivityInfoById(@RequestBody(required = false) String id){
        ActivityInfoVO result= activityInfoService.selectById(id);
            return result;
            }

    /**
    * 查询全部
    * @param param 查询条件
    * @return 查询结果
    */

    @Log("查询全部ActivityInfo")
    @CessBody
    @ApiOperation(value = "全部查询", notes = "查询ActivityInfo全部数据")
    @RequestMapping(value = "/queryActivityAll", method = RequestMethod.POST)
    public List<ActivityInfoVO> getActivityInfoAll(@RequestBody(required = false) ActivityInfoForm param){
            List<ActivityInfoVO> result= activityInfoService.selectAll(param);
            return result;
            }

    /**
    * 分页查询
    * @param param 查询条件
    * @return 查询结果
    */
    @Log("分页查询ActivityInfo统计")
    @CessBody
    @ApiOperation(value = "分页查询", notes = "分页查询ActivityInfo统计")
    @RequestMapping(value = "/queryActivityPage", method = RequestMethod.POST)
    public PageInfo<ActivityInfoVO> getActivityInfoPage(@RequestBody(required = false) ActivityInfoForm param){
        PageInfo<ActivityInfoVO> result= activityInfoService.selectPage(param);
        return result;

    }

    /**
     * 分页查询
     * @param param 查询条件
     * @return 查询结果
     */
    @Log("分页查询ActivityInfo信息")
    @CessBody
    @ApiOperation(value = "分页查询", notes = "分页查询ActivityInfo信息")
    @RequestMapping(value = "/queryActivityListPage", method = RequestMethod.POST)
    public PageInfo<ActivityInfoVO> queryActivityListPage(@RequestBody(required = false) ActivityInfoForm param){
        PageInfo<ActivityInfoVO> result= activityInfoService.queryActivityListPage(param);
        return result;

    }

    @Log("根据主键id查询ActivityInfo报名数及助力数量等")
    @CessBody
    @ApiOperation(value = "获取单条数据", notes = "根据主键id查询ActivityInfo报名数及助力数量等")
    @RequestMapping(value = "/getActivitySumCountById", method = RequestMethod.POST)
    public ActivityInfoVO getActivitySumCountById(@RequestBody(required = false) Map map){
        ActivityInfoVO result= activityInfoService.getActivitySumCountById(map.get("id")+"");
        return result;
    }

    @Log("活动信息导出")
    @CessBody
    @ApiOperation(value = "活动信息导出", notes = "活动信息导出")
    @PostMapping(value = "/activityExport")
    public void activityExport(HttpServletRequest request, HttpServletResponse response,@RequestBody String param) {
        activityInfoService.activityExport(request,response, param);
    }

    @Log("活动信息导出(使用异步方式)")
    @CessBody
    @ApiOperation(value = "活动信息导出", notes = "活动信息导出")
    @PostMapping(value = "/activityExportNew")
    public String activityExportNew(@RequestBody String param) {
        return activityInfoService.activityExportNew(param);
    }

    /**
     * 更新活动状态
     * @param param
     * @return
     */

    @Log("更新活动状态")
    @ApiOperation(value = "更新活动状态", notes = "更新活动状态")
    @RequestMapping(value = "/updateActivityInfo", method = RequestMethod.POST)
    public ResultBody updateActivityInfo(@ApiParam(name = "param", value = "{\"id\":\"活动id\",\"isDel\":\"是否删除（1：删除 0：未删除）\",\"actStatus\":\"活动状态:1草稿，2已发布\",\"status\":\"是否启用:1已启用，0禁用\"}")
            @RequestBody(required = false) Map param){
        return activityInfoService.updateActivityInfo(param);
    }

    /**
     * 添加活动
     * @param param
     * @return
     */

    @Log("添加活动")
    @ApiOperation(value = "添加活动", notes = "添加活动")
    @RequestMapping(value = "/addActivityInfo", method = RequestMethod.POST)
    public ResultBody addActivityInfo(@RequestBody(required = false) ActivityInfoForm param){
        return activityInfoService.addActivityInfo(param);
    }

    /**
     * 查询城市
     * @param param
     * @return
     */
    @Log("查询城市")
    @ApiOperation(value = "查询城市", notes = "查询城市")
    @RequestMapping(value = "/selectCityByPro", method = RequestMethod.POST)
    public ResultBody selectCityByPro(@ApiParam(name = "param", value = "{\"ids\":\"项目ID集合\"}")
                                         @RequestBody(required = false) Map param){
        return activityInfoService.selectCityByPro(param);
    }

    /**
     * 查询优惠券
     * @param param
     * @return
     */
    @Log("查询优惠券")
    @ApiOperation(value = "查询优惠券", notes = "查询优惠券")
    @RequestMapping(value = "/selectCoupon", method = RequestMethod.POST)
    public ResultBody selectCoupon(@ApiParam(name = "param", value = "{\"ids\":\"项目ID集合\",\"id\":\"活动ID\"}")
                                      @RequestBody(required = false) Map param){
        return activityInfoService.selectCoupon(param);
    }

    /*
    *奖项查询优惠券
    * */
    @Log("奖项查询优惠券")
    @ApiOperation(value = "奖项查询优惠券", notes = "奖项查询优惠券")
    @RequestMapping(value = "/selectAwardCoupon", method = RequestMethod.POST)
    public ResultBody selectAwardCoupon(@ApiParam(name = "param", value = "{\"projectIds\":\"项目ID集合\",\"id\":\"活动ID\",\"openAwardTime\":\"开奖时间\"}")
                                            @RequestBody(required = false) Map param){
        return activityInfoService.selectAwardCoupon(param);
    }


    @Log("奖项查询优惠券")
    @ApiOperation(value = "奖项查询优惠券", notes = "奖项查询优惠券")
    @RequestMapping(value = "/selectCouponByProjectId", method = RequestMethod.POST)
    public ResultBody selectCouponByProjectId(@ApiParam(name = "param", value = "{\"projectIds\":\"项目ID集合\",\"id\":\"活动ID\",\"openAwardTime\":\"开奖时间\"}")
                                            @RequestBody(required = false) Map param){
        return activityInfoService.selectCouponByProjectId(param);
    }

    /**
     * 查询活动详情
     * @param param
     * @return
     */
    @Log("查询活动详情")
    @ApiOperation(value = "查询活动详情", notes = "查询活动详情")
    @RequestMapping(value = "/selectActivityById", method = RequestMethod.POST)
    public ResultBody selectActivityById(@ApiParam(name = "param", value = "{\"id\":\"活动ID\"}")
                                   @RequestBody(required = false) Map param){
        return activityInfoService.selectActivityById(param);
    }

    /**
     * 获取活动报名及助力成功人数
     * @param param
     * @return
     */
    @Log("获取活动报名及助力成功人数")
    @ApiOperation(value = "获取活动报名及助力成功人数", notes = "获取活动报名及助力成功人数")
    @RequestMapping(value = "/getHelpAndSingUpCount", method = RequestMethod.POST)
    public ResultBody getHelpAndSingUpCount(@ApiParam(name = "param", value = "{\"id\":\"活动ID\"}")
                                         @RequestBody(required = false) Map param){
        return activityInfoService.getHelpAndSingUpCount(param);
    }

    /**
     * 更新活动详情
     * @param param
     * @return
     */
    @Log("更新活动详情")
    @ApiOperation(value = "更新活动详情", notes = "更新活动详情")
    @RequestMapping(value = "/updateActivityById", method = RequestMethod.POST)
    public ResultBody updateActivityById(@RequestBody(required = false) ActivityInfoForm param){
        return activityInfoService.updateActivityById(param);
    }

    /**
     * 更新redis
     * @param param
     * @return
     */
    @Log("更新redis")
    @ApiOperation(value = "更新redis", notes = "更新redis")
    @RequestMapping(value = "/updateRedisById", method = RequestMethod.POST)
    public ResultBody updateRedisById(@RequestBody(required = false) Map param){
        return activityInfoService.updateRedisById(param);
    }

    /**
     * 更新redis许愿图片
     * @param
     * @return
     */
    @Log("更新redis许愿图片")
    @ApiOperation(value = "更新redis许愿图片", notes = "更新redis许愿图片")
    @RequestMapping(value = "/updateRedisXy", method = RequestMethod.GET)
    public ResultBody updateRedisXy(){
        return activityInfoService.updateRedisXy();
    }

    @Log("根据当前登录人查询对应所拥有的项目列表")
    @CessBody
    @ApiOperation(value = "查询当前登录人所拥有的项目列表")
    @PostMapping(value = "/getBookProjectListByUserName")
    public List<ResultProjectVO> getProjectListByUserId(@RequestBody Map<String,Object> map){
        List<ResultProjectVO> projectList = activityInfoService.getProjectListByUserId(map);
        return projectList;
    };

    @Log("查询楼盘经纬度")
    @ApiOperation(value = "查询楼盘经纬度")
    @PostMapping(value = "/getLatitudeAndLongitudeList")
    public ResultBody getLatitudeAndLongitudeList(@RequestBody Map map){
        List<Map> resultMapList = activityInfoService.getLatitudeAndLongitudeList(map);
        return ResultBody.success(resultMapList);
    };

    @Log("查询活动报名明细")
    @ApiOperation(value = "查询活动报名明细")
    @PostMapping(value = "/getActivitySignUpDetailed")
    public ResultBody getActivitySignUpDetailedList(@RequestBody Map map){
        return ResultBody.success(activityInfoService.getActivitySignUpDetailedList(map));
    };

    @Log("查询活动助力明细")
    @ApiOperation(value = "查询活动助力明细")
    @PostMapping(value = "/getActivityHelpDetailed")
    public ResultBody getActivityHelpDetailedList(@RequestBody Map map){
        return ResultBody.success(activityInfoService.getActivityHelpDetailedList(map));
    };

    @Log("活动报名明细表导出")
    @CessBody
    @ApiOperation(value = "活动报名明细表导出", notes = "")
    @RequestMapping(value = "/activitySignUpDetailedExport")
    public void activitySignUpDetailedExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        activityInfoService.activitySignUpDetailedExport(request,response, param);
    }

    @Log("活动助力明细表导出")
    @CessBody
    @ApiOperation(value = "活动助力明细表导出", notes = "")
    @RequestMapping(value = "/activityHelpDetailedExport")
    public void activityHelpDetailedExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        activityInfoService.activityHelpDetailedExport(request,response, param);
    }

    @Log("活动报名明细表异步导出")
    @CessBody
    @ApiOperation(value = "活动报名明细表异步导出", notes = "")
    @RequestMapping(value = "/activitySignUpDetailedExportNew")
    public String activitySignUpDetailedExportNew(@RequestBody Map param) {
        return activityInfoService.activitySignUpDetailedExportNew(param);
    }

    @Log("活动助力明细表异步导出")
    @CessBody
    @ApiOperation(value = "活动助力明细表异步导出", notes = "")
    @RequestMapping(value = "/activityHelpDetailedExportNew")
    public String activityHelpDetailedExportNew(@RequestBody Map param) {
        return activityInfoService.activityHelpDetailedExportNew(param);
    }

    @Log("查询活动关联项目")
    @ApiOperation(value = "查询活动关联项目")
    @PostMapping(value = "/getActivityPro")
    public ResultBody getActivityPro(@RequestBody Map map){
        return ResultBody.success(activityInfoService.getActivityPro(map));
    };

    @Log("生成签到二维码")
    @ApiOperation(value = "生成签到二维码")
    @PostMapping(value = "/createQr")
    public ResultBody createQr(@RequestBody Map map){
        return activityInfoService.createQr(map);
    };

    @Log("查询助力详情")
    @ApiOperation(value = "查询助力详情")
    @PostMapping(value = "/getHelpDetail")
    public ResultBody getHelpDetail(@RequestBody Map map){
        if (map == null || map.get("id")==null || "".equals(map.get("id"))){
            return ResultBody.error(-10_0000, "参数不能为空");
        }
        return ResultBody.success(activityInfoService.getHelpDetail(map));
    };

    @Log("查询活动统计详情")
    @ApiOperation(value = "查询活动统计详情")
    @PostMapping (value = "/getActivityCensusDetail")
    public ResultBody getActivityCensusDetail(@ApiParam(name = "map", value = "{\"activityId\":\"活动id\",\"type\":\"类型\",\"pageNum\":\"分页\",\"pageSize\":\"分页\"}")
                                              @RequestBody Map map){
        return activityInfoService.getActivityCensusDetail(map);
    };

    @Log("查询排序的活动")
    @ApiOperation(value = "查询排序的活动")
    @PostMapping (value = "/getActivityOrder")
    public ResultBody getActivityOrder(@ApiParam(name = "map", value = "{\"CityID\":\"城市id\"}")
                                              @RequestBody Map map){
        return activityInfoService.getActivityOrder(map);
    };

    @Log("保存活动排序")
    @ApiOperation(value = "保存活动排序")
    @PostMapping (value = "/addActivityOrder")
    public ResultBody addActivityOrder(@ApiParam(name = "map", value = "{\"CityID\":\"城市id\",\"list\":[{\"activityId\":\"活动id\",\"imgUrl\":\"活动图片路径\",\"listIndex\":\"排序\"}]}")
                                              @RequestBody Map map){
        map.put("userId", SecurityUtils.getUserId());
        return activityInfoService.addActivityOrder(map);
    };

    @Log("查询活动当前状态")
    @ApiOperation(value = "查询活动当前状态")
    @PostMapping (value = "/getActivityStatus")
    public ResultBody getActivityStatus(@ApiParam(name = "map", value = "{\"id\":\"活动id\"}")
                                       @RequestBody Map map){
        if (map == null || map.get("id")==null || "".equals(map.get("id"))){
            return ResultBody.error(-10_0000, "参数不能为空");
        }
        return activityInfoService.getActivityStatus(map);
    };

    @Log("查询活动发起助力人数")
    @ApiOperation(value = "查询活动发起助力人数")
    @PostMapping (value = "/getActivityHelpCount")
    public ResultBody getActivityHelpCount(@ApiParam(name = "map", value = "{\"id\":\"活动id\"}")
                                        @RequestBody Map map){
        if (map == null || map.get("id")==null || "".equals(map.get("id"))){
            return ResultBody.error(-10_0000, "参数不能为空");
        }
        return activityInfoService.getActivityHelpCount(map);
    };

    @ApiOperation(value = "查询已参加报名活动列表")
    @PostMapping("/getSignUpActivityList")
    public ResultBody getSignUpActivityList(@RequestBody Map map){
        return ResultBody.success(activityInfoService.getSignUpActivityList(map));
    }

    @RequestMapping(value="/exportDiyCodeDetail",method = RequestMethod.GET)
    public ResultBody exportExcel(HttpServletRequest request, HttpServletResponse response ){
        String activity_id = request.getParameter("activity_id");
        String exportType = request.getParameter("exportType");
        return activityInfoService.getExcel(request,response,activity_id,exportType);
    }

    @ApiOperation(value = "更新全部活动数据编号")
    @PostMapping("updateCouNo")
    public void updateCouNo(){
        activityInfoService.updateCouNo();
    }

    /**
     * mays 新增乘车活动接口
     * @param ridActivityFrom 乘车活动主体
     * @return 返回状态码
     */
    @ApiOperation(value = "新增或更新乘车活动接口")
    @PostMapping("addOrUpdateRidActivity")
    public ResultBody addRidActivity(@RequestBody RidActivityFrom ridActivityFrom) {
        if (StringUtils.isNotBlank(ridActivityFrom.getId())) {
            // 更新
            return activityInfoService.updateRidActivity(ridActivityFrom);
        } else {
            // 添加
            return activityInfoService.addRidActivity(ridActivityFrom);
        }
    }

    /**
     * mays 查询当前乘车活动详情
     * @param ridActivityId 乘车活动id
     * @return
     */
    @ApiOperation(value = "根据id查询当前乘车活动")
    @PostMapping("getRidActivityInfo/{ridActivityId}")
    public ResultBody getRidActivityInfo(@PathVariable String ridActivityId) {
        return activityInfoService.getRidActivityInfo(ridActivityId);
    }

    /**
     * mays 修改当前活动状态
     * @param changeStatusVo 封装的参数类
     * @return
     */
    @ApiOperation(value = "修改当前乘车活动状态")
    @PostMapping("changeRidActStatus")
    public ResultBody changeRidActStatus(@RequestBody ChangeStatusVo changeStatusVo) {
        return activityInfoService.changeRidActStatus(changeStatusVo);
    }

    /**
     *  mays 查询全部乘车活动
     * @return
     */
    @ApiOperation(value = "查询全部乘车活动")
    @PostMapping("queryAllRidActivity")
    public List<RidActivityInfoVo> queryAllRidActivity() {
        return activityInfoService.queryAllRidActivity();
    }

    /**
     *  mays 条件查询乘车活动
     * @param ridActConditionsVo 查询条件和分页条件
     * @return
     */
    @ApiOperation(value = "条件查询乘车活动")
    @PostMapping("getRidActsByConditions")
    public ResultBody<PageInfo<RidActivityInfoVo>> getRidActsByConditions(@RequestBody RidActConditionsVo ridActConditionsVo) {
        return activityInfoService.getRidActsByConditions(ridActConditionsVo);
    }

    /**
     *  mays 删除乘车活动
     * @param ridActId 活动id
     * @return
     */
    @ApiOperation(value = "删除乘车活动")
    @PostMapping("delRidActById/{ridActId}")
    public ResultBody delRidActById(@PathVariable String ridActId) {
        return activityInfoService.delRidActById(ridActId);
    }

    /**
     *  mays 乘车活动导出
     * @param request ..
     * @param response ..
     * @param ridActConditionsVo 条件查询主体
     * @throws IOException
     */
    @ApiOperation(value = "乘车活动导出")
    @PostMapping("ridActExport")
    public void ridActExport(HttpServletRequest request,HttpServletResponse response,@RequestBody RidActConditionsVo ridActConditionsVo) throws IOException {
        activityInfoService.ridActExport(request,response,ridActConditionsVo);
    }

    /**
     *  mays 查询乘车行程明细
     * @param ridActConditionsVo 明细查询条件 包含分页条件
     * @return
     */
    @ApiOperation(value = "查询乘车行程明细")
    @PostMapping("getRidActTripDetails")
    public ResultBody<PageInfo<RidActDetailsVo>> getRidActTripDetails(@RequestBody RidActConditionsVo ridActConditionsVo) {
        return activityInfoService.getRidActTripDetails(ridActConditionsVo);
    }

    /**
     *  乘车行程明细导出
     * @param request
     * @param response
     * @param ridActConditionsVo 查询条件主体
     * @throws IOException
     */
    @ApiOperation(value = "乘车行程明细导出")
    @PostMapping(value = "ridActDetailsExport")
    public void ridActDetailsExport(HttpServletRequest request,HttpServletResponse response,@RequestBody RidActConditionsVo ridActConditionsVo) throws IOException {
        activityInfoService.ridActDetailsExport(request,response,ridActConditionsVo);
    }
}



