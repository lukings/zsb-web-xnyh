package cn.visolink.system.allpeople.contentManagement.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.allpeople.contentManagement.model.*;
import cn.visolink.system.allpeople.contentManagement.service.ContentService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ContentController
 * @Author wanggang
 * @Description //轮播管理
 * @Date 2020/1/17 14:08
 **/
@RestController
@RequestMapping("/content")
public class ContentController {
    @Autowired
    private ContentService contentService;
    @Value("${mapAppKey}")
    private String mapAppKey;
    @Value("${mapUrl}")
    private String mapUrl;

    @Log("获取轮播图片")
    @ApiOperation(value = "获取轮播图片")
    @PostMapping("/getBuildingPhotos")
    public ResultBody getBuildingPhotos(@RequestBody Map map){

        return ResultBody.success(contentService.getBuildingPhotos(map));
    }

    @Log("更新轮播/弹窗图片状态")
    @ApiOperation(value = "更新轮播/弹窗图片状态")
    @PostMapping("/updateBuildingPhotoStatus")
    public ResultBody updateBuildingPhotoStatus(@RequestBody Map map){
        return ResultBody.success(contentService.updateBuildingPhotoStatus(map));
    }

    @Log("判断城市是否可配置弹窗图")
    @ApiOperation(value = "判断城市是否可配置弹窗图")
    @PostMapping("/getIsOkCity")
    public ResultBody getIsOkCity(@RequestBody Map map){
        return ResultBody.success(contentService.getIsOkCity(map));
    }

    @Log("获取轮播图片(排序使用)")
    @ApiOperation(value = "获取轮播图片(排序使用)")
    @PostMapping("/getBuildingPhotosOrder")
    public ResultBody getBuildingPhotosOrder(@RequestBody Map map){

        return ResultBody.success(contentService.getBuildingPhotosOrder(map));
    }

    @Log("更新轮播图片顺序")
    @ApiOperation(value = "更新轮播图片顺序")
    @PostMapping("/updateBuildingPhotoOrder")
    public ResultBody updateBuildingPhotoOrder(@RequestBody Map map){
        return contentService.updateBuildingPhotoOrder(map);
    }

    @Log("获取楼盘列表(排序使用)")
    @ApiOperation(value = "获取楼盘列表(排序使用)")
    @PostMapping("/getBuildingOrder")
    public ResultBody getBuildingOrder(@RequestBody Map map){

        return ResultBody.success(contentService.getBuildingOrder(map));
    }

    @Log("更新楼盘顺序")
    @ApiOperation(value = "更新楼盘顺序")
    @PostMapping("/updateBuildingOrder")
    public ResultBody updateBuildingOrder(@RequestBody Map map){
        return contentService.updateBuildingOrder(map);
    }

    @Log("添加/编辑轮播图片")
    @ApiOperation(value = "添加/编辑轮播图片")
    @PostMapping("/addBuildingPhoto")
    public ResultBody addBuildingPhoto(@ApiParam("添加新轮播图参数 newImgUrl")@RequestBody Map map){
        return contentService.addBuildingPhoto(map);
    }

    @Log("获取轮播图片跳转目的地")
    @ApiOperation(value = "获取轮播图片跳转目的地")
    @PostMapping("/getBuildingPhotoTO")
    public ResultBody getBuildingPhotoTO(@RequestBody Map map){
        return contentService.getBuildingPhotoTO(map);
    }

    @Log("获取新闻列表")
    @ApiOperation(value = "获取新闻列表")
    @PostMapping("/getAllNews")
    public ResultBody getAllNews(@RequestBody Map map){
        if (map.get("JobID")==null || "".equals(map.get("JobID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getNewsList(map));
        }
    }

    @Log("获取需排序新闻列表")
    @ApiOperation(value = "获取需排序新闻列表")
    @PostMapping("/getNewsOrder")
    public ResultBody getNewsOrder(@RequestBody Map map){
        if (map.get("CityID")==null || "".equals(map.get("CityID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getNewsOrder(map));
        }
    }

    @Log("更新新闻顺序")
    @ApiOperation(value = "更新新闻顺序")
    @PostMapping("/updateNewsOrder")
    public ResultBody updateNewsOrder(@RequestBody Map map){
        return contentService.updateNewsOrder(map);
    }

    @Log("获取新闻详情")
    @ApiOperation(value = "获取新闻详情")
    @PostMapping("/getNewsDetail")
    public ResultBody getNewsDetail(@RequestBody Map map){
        if (map.get("ID")==null || "".equals(map.get("ID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getNewsDetail(map));
        }
    }
    @Log("添加/修改新闻")
    @ApiOperation(value = "添加/修改新闻")
    @PostMapping("/addOrEditNews")
    public ResultBody addOrEditNews(@RequestBody Map map){
//        news.setFile(file);
        return contentService.addOrEidtNews(map);
    }

    @Log("获取旭客家反馈列表")
    @ApiOperation(value = "获取反馈列表")
    @PostMapping("/getAllFeedback")
    public ResultBody getAllFeedback(@RequestBody Map map){
        return ResultBody.success(contentService.getFeedbackList(map));
    }

    @Log("获取旭客汇反馈列表")
    @ApiOperation(value = "获取旭客汇反馈列表")
    @PostMapping("/getFeedback")
    public ResultBody getFeedback(@RequestBody Map map){
        return ResultBody.success(contentService.getFeedback(map));
    }


    @Log("旭客家反馈信息导出")
    @CessBody
    @ApiOperation(value = "反馈导出", notes = "")
    @RequestMapping(value = "/feedbackExport")
    public void feedbackExport(HttpServletRequest request, HttpServletResponse response,@RequestBody Map map) {
        contentService.feedbackExport(request,response, map);
    }


    @Log("旭客汇反馈信息导出")
    @CessBody
    @ApiOperation(value = "旭客汇反馈信息导出", notes = "旭客汇反馈信息导出")
    @RequestMapping(value = "/feedbackHuiExport")
    public void feedbackHuiExport(HttpServletRequest request, HttpServletResponse response,@RequestBody Map map) {
        contentService.feedbackHuiExport(request,response,  map);
    }

    @Log("反馈详情")
    @ApiOperation(value = "反馈详情")
    @PostMapping("/getFeedbackDetail")
    public ResultBody getFeedbackDetail(@RequestBody Map map){
        return ResultBody.success(contentService.getFeedbackDetail(map));
    }

    @Log("反馈处理")
    @ApiOperation(value = "反馈处理")
    @PostMapping("/handleFeedback")
    public ResultBody handleFeedback(@RequestBody Map map){
        return contentService.handleFeedback(map);
    }

    @Log("获取楼盘列表")
    @ApiOperation(value = "获取楼盘列表")
    @PostMapping("/getAllBuilding")
    public ResultBody getAllBuilding(@RequestBody Map map){
        return ResultBody.success(contentService.getAllBuilding(map));
    }

    @Log("获取项目楼盘")
    @ApiOperation(value = "获取项目楼盘")
    @PostMapping("/getBuildingByProId")
    public ResultBody getBuildingByProId(@RequestBody Map map){
        return ResultBody.success(contentService.getBuildingByProId(map));
    }

    @Log("新增/编辑楼盘")
    @ApiOperation(value = "新增/编辑楼盘")
    @PostMapping("/addOrEditBuilding")
    public ResultBody addOrEditBuilding(@RequestBody Map map){
//        buildingBasic.setBuildingFile(BuildingFile);
//        buildingBasic.setBuildingFiles(BuildingFiles);
//        buildingBasic.setBuildingVideoFile(BuildingVideoFile);
        return contentService.addOrEditBuilding(map);
    }


    @Log("初始化楼盘周边配套")
    @ApiOperation(value = "初始化楼盘周边配套")
    @PostMapping("/addAllBuildBookPeripheralMatching")
    public String addAllBuildBookPeripheralMatching(){
        return contentService.addAllBuildBookPeripheralMatching();
    }

    @Log("获取楼盘详情")
    @ApiOperation(value = "获取楼盘详情")
    @PostMapping("/getBuildingDetail")
    public ResultBody getBuildingDetail(@RequestBody Map map){
        if (map.get("BuildBookID")==null || "".equals(map.get("BuildBookID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getBuildingDetail(map));
        }
    }

    @Log("获取楼盘户型")
    @ApiOperation(value = "获取楼盘户型")
    @PostMapping("/getBuildingHX")
    public ResultBody getBuildingHX(@RequestBody Map map){
        if (map.get("BuildBookID")==null || "".equals(map.get("BuildBookID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getBuildingHX(map));
        }
    }

    @Log("获取户型大类")
    @ApiOperation(value = "获取户型大类")
    @PostMapping("/getBuildingHXD")
    public ResultBody getBuildingHXD(){
        return ResultBody.success(contentService.getBuildingHXD());
    }

    @Log("获取户型子类")
    @ApiOperation(value = "获取户型子类")
    @PostMapping("/getBuildingHXZ")
    public ResultBody getBuildingHXZ(@RequestBody Map map){
        if (map.get("ID")==null || "".equals(map.get("ID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getBuildingHXZ(map));
        }
    }
    @Log("获取用户项目")
    @ApiOperation(value = "获取用户项目")
    @PostMapping("/getUserProjects")
    public ResultBody getUserProjects(@RequestBody Map map){
        if (map.get("userName")==null || "".equals(map.get("userName"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getUserProjects(map));
        }

    }

    @Log("获取项目城市、区域")
    @ApiOperation(value = "获取项目城市、区域")
    @PostMapping("/getCityAndBelongArea")
    public ResultBody getCityAndBelongArea(@RequestBody Map map){
        if (map.get("projectId")==null || "".equals(map.get("projectId"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getCityAndBelongArea(map));
        }

    }
    @Log("获取活动列表")
    @ApiOperation(value = "获取活动列表")
    @PostMapping("/getExtensionList")
    public ResultBody getExtensionList(@RequestBody Map map){
        if (map.get("projectId")==null || "".equals(map.get("projectId"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getExtensionList(map));
        }

    }

    @Log("添加/编辑活动")
    @ApiOperation(value = "添加/编辑活动")
    @PostMapping("/addOrEditExtension")
    public ResultBody addOrEditExtension(@RequestBody Map map){
        return contentService.addOrEditExtension(map);
    }


    @Log("根据岗位获取城市")
    @ApiOperation(value = "根据岗位获取城市")
    @RequestMapping("/getCitysByJobId")
    public ResultBody getCitysByJobId(@RequestBody Map map){
        //修改为按照账号取所有岗位的城市
//        if (map.get("JobID")==null || "".equals(map.get("JobID"))){
//            return ResultBody.error(-21_0006,"必传参数未传！");
//        }else{
//            return ResultBody.success(contentService.getCitysByJobId(map));
//        }
        return ResultBody.success(contentService.getCitysByJobId(map));
    }

    @Log("根据项目，楼盘获取海报")
    @ApiOperation(value = "根据项目，楼盘获取海报")
    @RequestMapping("/getBuildingPosterList")
    public ResultBody getBuildingPosterList(@RequestBody Map map){
        if (map.get("ProjectId")==null || "".equals(map.get("ProjectId")) || map.get("BuildBookID")==null || "".equals(map.get("BuildBookID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getBuildingPosterList(map));
        }
    }

    @Log("添加海报")
    @ApiOperation(value = "添加海报")
    @PostMapping("/addBuildingPoster")
    public ResultBody addBuildingPoster(@RequestBody Map map){
        try{
            String json = JSONObject.toJSONString(map);
            //转化为实体类
            BuildingPoster buildingPoster = JSONObject.toJavaObject(JSON.parseObject(json),BuildingPoster.class);
            contentService.addBuildingPoster(buildingPoster);
            return ResultBody.success("添加海报成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0006,"添加海报异常！");
        }
    }

    @Log("编辑海报")
    @ApiOperation(value = "编辑海报")
    @PostMapping("/updateBuildingPoster")
    public ResultBody updateBuildingPoster(@RequestBody Map map){
        try{
            String json = JSONObject.toJSONString(map);
            //转化为实体类
            BuildingPoster buildingPoster = JSONObject.toJavaObject(JSON.parseObject(json),BuildingPoster.class);
            contentService.updateBuildingPoster(buildingPoster);
            return ResultBody.success("编辑海报成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0006,"编辑海报异常！");
        }
    }

    @Log("删除海报")
    @ApiOperation(value = "删除海报")
    @PostMapping("/delBuildingPoster")
    public ResultBody delBuildingPoster(@RequestBody Map map){
        if (map.get("ID")==null || "".equals(map.get("ID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            contentService.delBuildingPoster(map.get("ID")+"");
            return ResultBody.success("删除海报成功！");
        }
    }

    @Log("获取装修标准")
    @ApiOperation(value = "获取装修标准")
    @RequestMapping("/getDecorationStandard")
    public ResultBody getZXBZ(){
        return ResultBody.success(contentService.getZXBZ());
    }

    @Log("获取楼栋业态")
    @ApiOperation(value = "获取楼栋业态")
    @RequestMapping("/getBuildBookProperty")
    public ResultBody getBuildBookProperty(@RequestBody Map map){
//        if (map.get("projectId")==null || "".equals(map.get("projectId")+"") ){
//            return ResultBody.error(-21_0006,"必传参数未传!");
//        }

        return ResultBody.success(contentService.getBuildBookProperty(map));
    }
    //暂时未作
//    @Log("根据岗位获取区域")
//    @ApiOperation(value = "根据岗位获取区域")
//    @RequestMapping("/getBelongAreaByJobId")
//    public ResultBody getBelongAreaByJobId(@RequestBody Map map){
//        if (map.get("JobID")==null || "".equals(map.get("JobID"))){
//            return ResultBody.error(-21_0006,"必传参数未传！");
//        }else{
//            return ResultBody.success(contentService.getBelongAreaByJobId(map));
//        }
//    }
//    //暂时未作
//    @Log("根据区域获取城市")
//    @ApiOperation(value = "根据区域获取城市")
//    @RequestMapping("/getCitysByBelongArea")
//    public ResultBody getCitysByBelongArea(@RequestBody Map map){
//        if (map.get("JobID")==null || "".equals(map.get("JobID"))){
//            return ResultBody.error(-21_0006,"必传参数未传！");
//        }else{
//            return ResultBody.success(contentService.getBelongAreaByJobId(map));
//        }
//    }

    @Log("下载小程序二维码")
    @ApiOperation(value = "下载小程序二维码")
    @RequestMapping("/getWinCode")
    public ResultBody getWinCode( HttpServletResponse response,Extension extension ){
        if (extension.getIconUrl()==null || "".equals(extension.getIconUrl()) || extension.getIconName()==null || "".equals(extension.getIconName())){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            contentService.getWinCode(response,extension);
            return ResultBody.success("小程序二维码获取成功！");
        }
    }

    @Log("获取周边配套")
    @ApiOperation(value = "获取周边配套")
    @PostMapping("/getPeripheralMatching")
    public ResultBody getPeripheralMatching(@RequestBody Map map){
        if (map.get("lat")==null || "".equals(map.get("lat"))
        || map.get("lng")==null || "".equals(map.get("lng"))
        || map.get("periphery")==null || "".equals(map.get("periphery"))
        || map.get("adType")==null || "".equals(map.get("adType"))
        || map.get("projectId")==null || "".equals(map.get("projectId"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return contentService.getPeripheralMatching(map);
        }
    }

    @Log("获取原周边配套")
    @ApiOperation(value = "获取原周边配套")
    @PostMapping("/getOldPeripheralMatching")
    public ResultBody getOldPeripheralMatching(@RequestBody Map map){
        if (map.get("buildBookId")==null || "".equals(map.get("buildBookId"))
                || map.get("adType")==null || "".equals(map.get("adType"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            Map resultMap = new HashMap();
            resultMap.put("data",contentService.getOldPeripheralMatching(map));
            return ResultBody.success(resultMap);
        }
    }

    @Log("查询楼盘问题列表")
    @ApiOperation(value = "查询楼盘问题列表")
    @PostMapping("/getBuildBookProblemList")
    public ResultBody getBuildBookProblemList(@RequestBody Map map){
        return contentService.getBuildBookProblemList(map);
    }

    @Log("保存楼盘常见问题")
    @ApiOperation(value = "保存楼盘常见问题")
    @PostMapping("/saveBuildBookProblem")
    public ResultBody saveBuildBookProblem(@RequestBody Map map){
        return contentService.saveBuildBookProblem(map);
    }

    @Log("更新楼盘常见问题")
    @ApiOperation(value = "更新楼盘常见问题")
    @PostMapping("/updateBuildBookProblem")
    public ResultBody updateBuildBookProblem(@RequestBody Map map){
        return contentService.updateBuildBookProblem(map);
    }

    @Log("更新楼盘常见问题排序")
    @ApiOperation(value = "更新楼盘常见问题排序")
    @PostMapping("/updateBuildBookProblemListIndex")
    public ResultBody updateBuildBookProblemListIndex(@RequestBody Map map){
        return contentService.updateBuildBookProblemListIndex(map);
    }

    @Log("楼盘常见问题新增查询楼盘")
    @ApiOperation(value = "楼盘常见问题新增查询楼盘")
    @PostMapping("/getBuildBookList")
    public ResultBody getBuildBookList(@RequestBody Map map){
        return contentService.getBuildBookList(map);
    }

    @Log("查询问题(排序使用)")
    @ApiOperation(value = "查询问题(排序使用)")
    @GetMapping("/getBuildBookProblemListByProjectId")
    public ResultBody getBuildBookProblemListByProjectId(String buildBookId){
        return contentService.getBuildBookProblemListByProjectId(buildBookId);
    }

    @Log("查询问题数量")
    @ApiOperation(value = "查询问题数量")
    @GetMapping("/getBookProblemNum")
    public ResultBody getBookProblemNum(String buildBookID){
        return contentService.getBookProblemNum(buildBookID);
    }

    @Log("楼盘常见问题导出")
    @ApiOperation(value = "楼盘常见问题导出", notes = "")
    @RequestMapping(value = "/bookProblemNumExport")
    public void bookProblemNumExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        contentService.bookProblemNumExport(request,response, param);
    }

    @Log("获取所有城市")
    @ApiOperation(value = "获取所有城市", notes = "")
    @GetMapping(value = "/getAllCityList")
    public ResultBody getAllCityList(String cityId) {
       return contentService.getAllCityList(cityId);
    }

    @Log("获取所有项目")
    @ApiOperation(value = "获取所有项目", notes = "")
    @GetMapping(value = "/getAllProjectList")
    public ResultBody getAllProjectList(){
        return contentService.getAllProjectList();
    }


    @Log("更新楼盘素材（删除时调用）")
    @ApiOperation(value = "更新楼盘素材（删除时调用）")
    @PostMapping("/deleteBuildBookPhotos")
    public ResultBody deleteBuildBookPhotos(@RequestBody Map map){
        return contentService.deleteBuildBookPhotos(map);
    }

    @Log("获取户型标签")
    @ApiOperation(value = "获取户型标签")
    @RequestMapping("/getHxTag")
    public ResultBody getHxTag(){
        return ResultBody.success(contentService.getHxTag());
    }
    @Log("获取楼盘标签")
    @ApiOperation(value = "获取楼盘标签")
    @RequestMapping("/getLpTag")
    public ResultBody getLpTag(){
        return ResultBody.success(contentService.getLpTag());
    }

    @Log("获取楼盘产品分类")
    @ApiOperation(value = "获取楼盘产品分类")
    @GetMapping("/getProductsDict")
    public ResultBody getProductsDict(@RequestParam(value ="projectId",required=false) String projectId){
        return ResultBody.success(contentService.getProductsDict(projectId));
    }

    @Log("获取户型朝向")
    @ApiOperation(value = "获取户型朝向")
    @RequestMapping("/getHXCX")
    public ResultBody getHXCX(){
        return ResultBody.success(contentService.getHXCX());
    }

    @Log("修改户型状态")
    @ApiOperation(value = "修改户型状态")
    @RequestMapping("/updateBuildingApartmentStatus")
    public ResultBody updateBuildingApartmentStatus(@RequestBody Map map){
        return ResultBody.success(contentService.updateBuildingApartmentStatus(map));
    }


    @Log("获取户型详情")
    @ApiOperation(value = "获取户型详情")
    @PostMapping("/getBuildingApartmentDetail")
    public ResultBody getBuildingApartmentDetail(@RequestBody Map map){
        if (map.get("id")==null || "".equals(map.get("id"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            Map resultMap = new HashMap();
            resultMap.put("data",contentService.getBuildingApartmentDetail(map));
            return ResultBody.success(resultMap);
        }
    }

    @Log("获取用户项目/区域")
    @ApiOperation(value = "获取用户项目/区域")
    @PostMapping("/getExtensionTypeDesc")
    public ResultBody getExtensionTypeDesc(@ApiParam(name = "map", value = "{\"ExtenType\":\"配置层级\",\"orgLevel\":\"权限层级\"}")
            @RequestBody Map map){
        if (map.get("ExtenType")==null || "".equals(map.get("ExtenType"))
        ||map.get("orgLevel")==null || "".equals(map.get("orgLevel"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getExtensionTypeDesc(map));
        }
    }

    @Log("获取用户权限级别")
    @ApiOperation(value = "获取用户权限级别")
    @PostMapping("/getUserOrgLevel")
    public ResultBody getUserOrgLevel(){
        return ResultBody.success(contentService.getUserOrgLevel());
    }

    @Log("获取推广码新")
    @ApiOperation(value = "获取推广码新")
    @PostMapping("/getExtenListNew")
    public ResultBody getExtenListNew(@ApiParam(name = "map", value = "{\"orgLevel\":\"权限层级\",\"proIds\":[项目ID集合],\"orgIds\":[区域ID集合],\"toUrls\":[跳转地],\"extenTypes\":[码属性],\"extenActivityName\":\"推广码名称\",\"jumpToName\":\"落地页\",\"creator\":\"创建人\",\"pageIndex\":\"当前页\",\"pageSize\":\"每页行数\"}")
                                          @RequestBody Map map){
        if (map.get("orgLevel")==null || "".equals(map.get("orgLevel"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getExtenListNew(map));
        }
    }

    @Log("获取区域项目选项")
    @ApiOperation(value = "获取区域项目选项")
    @PostMapping("/getOrgAndPro")
    public ResultBody getOrgAndPro(@ApiParam(name = "map", value = "{\"orgLevel\":\"权限层级\"}")
                                      @RequestBody Map map){
        if (map.get("orgLevel")==null || "".equals(map.get("orgLevel"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getOrgAndPro(map));
        }

    }

    @Log("删除推广码")
    @ApiOperation(value = "删除推广码")
    @PostMapping("/delExten")
    public ResultBody delExten(@ApiParam(name = "map", value = "{\"ID\":\"推广码ID\"}")
                                   @RequestBody Map map){
        if (map.get("ID")==null || "".equals(map.get("ID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.delExten(map));
        }
    }

    @Log("配置推广码获取城市")
    @ApiOperation(value = "配置推广码获取城市")
    @PostMapping("/getCityByOrgId")
    public ResultBody getCityByOrgId(@ApiParam(name = "map", value = "{\"orgLevel\":\"权限层级\"}")
                               @RequestBody Map map){
        if (map.get("orgLevel")==null || "".equals(map.get("orgLevel"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getCityByOrgId(map));
        }
    }

    @Log("新增/编辑推广码新")
    @ApiOperation(value = "新增/编辑推广码新")
    @PostMapping("/addOrEditExten")
    public ResultBody addOrEditExten(@RequestBody Map map){
        return contentService.addOrEditExten(map);
    }

    @Log("获取推广码跳转目的地")
    @ApiOperation(value = "获取推广码跳转目的地")
    @PostMapping("/getBuildingExtenTO")
    public ResultBody getBuildingExtenTO(@RequestBody Map map){
        return contentService.getBuildingExtenTO(map);
    }

    @Log("获取活动所属项目")
    @ApiOperation(value = "获取活动所属项目")
    @PostMapping("/getActivityPros")
    public ResultBody getActivityPros(@RequestBody Map map){
        if (map.get("activityId")==null || "".equals(map.get("activityId"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return contentService.getActivityPros(map);
        }

    }

    @Log("获取项目所属区域")
    @ApiOperation(value = "获取项目所属区域")
    @PostMapping("/getProOrg")
    public ResultBody getProOrg(@RequestBody Map map){
        if (map.get("proIds")==null){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return contentService.getProOrg(map);
        }

    }

    @Log("推广码导出")
    @CessBody
    @ApiOperation(value = "推广码导出", notes = "")
    @RequestMapping(value = "/extenExport")
    public void extenExport(HttpServletRequest request, HttpServletResponse response,@RequestBody String excelForm) {
        contentService.extenExport(request,response, excelForm);
    }

    @Log("获取楼盘图片类型")
    @ApiOperation(value = "获取楼盘图片类型")
    @PostMapping("/getLpPhotoType")
    public ResultBody getLpPhotoType(){
        return contentService.getLpPhotoType();
    }

    @Log("判断城市是否可配置首页品牌广告")
    @ApiOperation(value = "判断城市是否可配置首页品牌广告")
    @PostMapping("/checkDep")
    public ResultBody checkDep(@RequestBody CheckDep req){
        return ResultBody.success(contentService.checkDep(req));
    }


    @Log("新增app，pad 端启动页图片")
    @ApiOperation(value = "新增app，pad端启动页图片")
    @PostMapping("/startupPictureDeploy")
    public ResultBody startupPictureDeploy(@RequestBody StartupPagePicture startupPagePicture) throws IOException {
        return contentService.startupPictureDeploy(startupPagePicture);
    }

    @Log("删除app，pad 端启动页图片")
    @ApiOperation(value = "删除app，pad端启动页图片")
    @PostMapping("/delStartupPicture")
    public ResultBody delStartupPicture(@RequestBody StartupPagePicture startupPagePicture){
        return contentService.delStartupPicture(startupPagePicture);
    }

    @Log("修改状态app，pad 端启动页图片 状态")
    @ApiOperation(value = "修改状态app，pad 端启动页图片 状态")
    @PostMapping("/statusStartupPicture")
    public ResultBody statusStartupPicture(@RequestBody StartupPagePicture startupPagePicture){

        return contentService.statusStartupPicture(startupPagePicture);
    }

    @Log("修改app，pad 端启动页图片")
    @ApiOperation(value = "修改app，pad 端启动页图片")
    @PostMapping("/updateStartupPicture")
    public ResultBody updateStartupPicture(@RequestBody StartupPagePicture startupPagePicture){

        return contentService.updateStartupPicture(startupPagePicture);
    }

    @Log("根据主键id查询app pad端启动图片")
    @ApiOperation(value = "根据主键id查询app pad端启动图片")
    @PostMapping("/startupPictureById")
    public ResultBody startupPictureById(@RequestBody String id){

        return contentService.startupPictureById(id);
    }

    @Log("根据新闻城市ID查询楼盘")
    @ApiOperation(value = "根据新闻城市ID查询楼盘")
    @PostMapping("/getNewsBuilding")
    public ResultBody getNewsBuilding(@RequestBody Map map){

        return ResultBody.success(contentService.getNewsBuilding(map));
    }

    @Log("获取新闻类型")
    @ApiOperation(value = "获取新闻类型")
    @PostMapping("/getNewsType")
    public ResultBody getNewsType(){

        return ResultBody.success(contentService.getNewsType());
    }


    @Log("旭客后台新闻列表导出")
    @CessBody
    @ApiOperation(value = "旭客后台新闻列表导出", notes = "")
    @RequestMapping(value = "/newsExport")
    public void newsExport(HttpServletRequest request, HttpServletResponse response,@RequestBody Map map) {
        contentService.newsExport(request,response, map);
    }


    @Log("根据项目，获取楼盘问答")
    @ApiOperation(value = "根据项目，获取楼盘问答")
    @RequestMapping("/getBuildingProblemList")
    public ResultBody getBuildingProblemList(@RequestBody Map map){
        if (map.get("ProjectId")==null || "".equals(map.get("ProjectId")) || map.get("BuildBookID")==null || "".equals(map.get("BuildBookID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            return ResultBody.success(contentService.getBuildingProblemList(map));
        }
    }

    @Log("添加楼盘问答")
    @ApiOperation(value = "添加楼盘问答")
    @PostMapping("/addBuildingProblem")
    public ResultBody addBuildingProblem(@RequestBody Map map){
        try{
            String json = JSONObject.toJSONString(map);
            //转化为实体类
            BuildingProblem buildingProblem = JSONObject.toJavaObject(JSON.parseObject(json),BuildingProblem.class);
            contentService.addBuildingProblem(buildingProblem);
            return ResultBody.success("添加问答成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0006,"添加问答异常！");
        }
    }

    @Log("编辑楼盘问答")
    @ApiOperation(value = "编辑楼盘问答")
    @PostMapping("/updateBuildingProblem")
    public ResultBody updateBuildingProblem(@RequestBody Map map){
        try{
            String json = JSONObject.toJSONString(map);
            //转化为实体类
            BuildingProblem buildingProblem = JSONObject.toJavaObject(JSON.parseObject(json),BuildingProblem.class);
            contentService.updateBuildingProblem(buildingProblem);
            return ResultBody.success("编辑问答成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0006,"编辑问答异常！");
        }
    }

    @Log("删除楼盘问答")
    @ApiOperation(value = "删除楼盘问答")
    @PostMapping("/delBuildingProblem")
    public ResultBody delBuildingProblem(@RequestBody Map map){
        if (map.get("ID")==null || "".equals(map.get("ID"))){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }else{
            contentService.delBuildingProblem(map.get("ID")+"");
            return ResultBody.success("删除问答成功！");
        }
    }

    @Log("活动添加或更新首页热推图")
    @ApiOperation(value = "活动添加或更新首页热推图")
    @PostMapping("/addOrUpdateActivityHotHomePage")
    public ResultBody addActivityHotHomePage(
            @ApiParam("id:主键id 更新时传id 新增为空即可 " +
            "activityId:活动id " +
            "hotCityId:城市id " +
            "hotCityName:城市id " +
            "hotImageUrl:热推图片路径 " +
            "hotStartTime:热推开始时间 " +
            "hotEndTime:热推结束时间 " +
            "status:状态 0 未禁用 1 已禁用 " +
            "creator:创建人id")@RequestBody Map map) {
        String id = map.get("id") + "";
        if (StringUtils.isBlank(id)) {
            return contentService.addActivityHotHomePage(map);
        } else {
            return contentService.updateActivityHotHomePage(map);
        }
    }

    @Log("更新热推图状态")
    @ApiOperation(value = "更新热推图状态")
    @PostMapping("updateHotStatus")
    public ResultBody updateHotStatus(@ApiParam("activityId:活动id " +
            "hotCityId:城市id " +
            "hotStartTime:热推开始时间 " +
            "hotEndTime:热推结束时间 " +
            "status:修改的状态 0 未禁用 1 已禁用 " +
            "editor:修改人id")@RequestBody Map map) {
        return contentService.updateHotStatus(map);
    }

    @Log("删除首页热推图")
    @ApiOperation(value = "删除首页热推图")
    @PostMapping("delHotHomePageImg")
    public ResultBody delHotHomePageImg(@ApiParam("热推图主键id")@RequestBody Map map) {
        String id = map.get("id") + "";
        return contentService.delHotHomePageImg(id);
    }

    @Log("查询首页热推图列表")
    @ApiOperation(value = "查询首页热推图列表")
    @PostMapping("getHotImgList")
    public ResultBody getHotImgList(
            @ApiParam("activityName:活动名称, " +
            "activityNo:活动编号," +
            "cityId:城市id," +
            "status:状态") @RequestBody Map map) {
        return contentService.getHotImgList(map);
    }

    @Log("查询楼盘热卖字典值")
    @ApiOperation("查询楼盘热卖字典值")
    @PostMapping("/getBuildBookDic")
    public ResultBody getBuildBookDic() {
        return contentService.getBuildBookDic();
    }

    @Log("获取当前热推图详细信息")
    @ApiOperation("获取当前热推图详细信息")
    @PostMapping("getHotHomePae")
    public ResultBody getHotHomePae(@ApiParam("热推图主键id") @RequestBody Map map) {
        String id = map.get("id") + "";
        return contentService.getHotHomePae(id);
    }

    @Log("根据城市id 查询城市下所有活动")
    @ApiOperation("根据城市id 查询城市下所有活动")
    @PostMapping("getActivityByCityId")
    public ResultBody getActivityByCityId(@ApiParam("城市id") @RequestBody Map map) {
        if (null == map.get("cityId")) {
            return ResultBody.error(100089,"必传参数未传！");
        }
        List<String> cityId = (List<String>) map.get("cityId");
        return contentService.getActivityByCityId(cityId);
    }

    @Log("根据项目查询涉及城市")
    @ApiOperation("根据项目查询涉及城市")
    @PostMapping("getCityByPro")
    public ResultBody getCityByPro(@RequestBody Map map) {
        if (map == null || null == map.get("ids")  || "".equals(map.get("ids"))) {
            return ResultBody.error(-21_0006, "必传参数未传！！");
        }
        return contentService.getCityByPro(map);
    }

    @Log("校验当前城市是否可以配置热推位")
    @ApiOperation("校验当前城市是否可以配置热推位")
    @PostMapping("checkHotConfig")
    public ResultBody checkHotConfig(@RequestBody Map map) {
        return contentService.checkHotConfig(map);
    }

    @Log("根据活动获取关联城市列表")
    @ApiOperation("根据活动获取关联城市列表")
    @PostMapping("getCityListByAciId")
    public ResultBody getCityListByAciId(@RequestBody Map map) {
        return contentService.getCityListByAciId(map);
    }


}
