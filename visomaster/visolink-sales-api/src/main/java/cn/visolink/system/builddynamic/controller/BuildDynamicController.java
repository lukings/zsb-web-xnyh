package cn.visolink.system.builddynamic.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.builddynamic.model.BuildBookDynamic;
import cn.visolink.system.builddynamic.service.BuildDynamicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/12/18
 */
@RestController
@Api(tags = "楼盘动态管理")
@RequestMapping("/buildDynamic")
public class BuildDynamicController {

    @Autowired
    private BuildDynamicService buildDynamicService;



    @Log("查询楼盘动态列表")
    @ApiOperation(value = "查询楼盘动态列表", notes = "查询楼盘动态列表")
    @RequestMapping(value = "/getBuildBookDynamicList", method = RequestMethod.POST)
    public ResultBody getBuildBookDynamicList(@ApiParam(name = "map", value = "{\"projectIdList\":\"项目列表\",\"buildBookName\":\"楼盘名称\"," +
            "\"buildDynamicName\":\"楼盘动态名称\",\"creatorName\":\"创建人名称\",\"dynamicType\":\"楼盘动态类型\"," +
            "\"dateType\":\"时间类型（1 创建时间 2 发布时间）\",\"startDate\":\"开始时间\",\"endDate\":\"结束时间\"," +
            "\"pageIndex\":\"分页参数\",\"pageSize\":\"分页参数\",\"statusList\":\"状态\"}")
                                             @RequestBody Map map){
        return buildDynamicService.getBuildBookDynamicList(map);
    }

    @Log("根据id查询楼盘动态详情")
    @ApiOperation(value = "根据id查询楼盘动态详情", notes = "根据id查询楼盘动态详情")
    @RequestMapping(value = "/getBuildDynamicById", method = RequestMethod.GET)
    public ResultBody getBuildDynamicById(@ApiParam(name = "id", value = "id")String id){
        return buildDynamicService.getBuildDynamicById(id);
    }

    @Log("保存楼盘动态")
    @ApiOperation(value = "保存楼盘动态", notes = "保存楼盘动态")
    @RequestMapping(value = "/saveBuildBookDynamic", method = RequestMethod.POST)
    public ResultBody saveBuildBookDynamic(@RequestBody BuildBookDynamic buildBookDynamic){
        return buildDynamicService.saveBuildBookDynamic(buildBookDynamic);
    }

    @Log("修改楼盘动态")
    @ApiOperation(value = "修改楼盘动态", notes = "修改楼盘动态")
    @RequestMapping(value = "/editBuildBookDynamic", method = RequestMethod.POST)
    public ResultBody editBuildBookDynamic(@RequestBody BuildBookDynamic buildBookDynamic){
        return buildDynamicService.editBuildBookDynamic(buildBookDynamic);
    }

    @Log("根据项目id查询楼盘")
    @ApiOperation(value = "根据项目id查询楼盘", notes = "根据项目id查询楼盘")
    @RequestMapping(value = "/getBuildListByPojId", method = RequestMethod.GET)
    public ResultBody getBuildListByPojId(@ApiParam(name = "projectId", value = "项目id")String projectId){
        return buildDynamicService.getBuildListByPojId(projectId);
    }

    @Log("查询通用字典")
    @ApiOperation(value = "查询通用字典", notes = "查询通用字典")
    @RequestMapping(value = "/getBuildDynamicDictList", method = RequestMethod.POST)
    public ResultBody getBuildDynamicDictList(@ApiParam(name = "map", value = "{\"parentCode\":\"字典code\",\"childCodeStr\":\"字典子类凑得\",\"projectId\":\"项目id\"}")
                                              @RequestBody Map map){
        return buildDynamicService.getBuildDynamicDictList(map);
    }

    @Log("查询跳转选择数据")
    @ApiOperation(value = "查询跳转选择数据", notes = "查询跳转选择数据")
    @RequestMapping(value = "/getJumpTypeDataList", method = RequestMethod.POST)
    public ResultBody getJumpTypeDataList(@ApiParam(name = "map", value = "{\"type\":\"类型\",\"jumpId\":\"类型查询id\"}")
                                              @RequestBody Map map){
        return buildDynamicService.getJumpTypeDataList(map);
    }

    @Log("楼盘动态导出")
    @ApiOperation(value = "楼盘动态导出", notes = "楼盘动态导出")
    @RequestMapping(value = "/getBuildDynamicExcel", method = RequestMethod.POST)
    public void getBuildDynamicExcel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> param) {
        buildDynamicService.getBuildDynamicExcel(request,response, param);
    }
}
