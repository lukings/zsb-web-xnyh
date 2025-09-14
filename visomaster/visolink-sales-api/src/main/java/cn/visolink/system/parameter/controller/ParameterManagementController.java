package cn.visolink.system.parameter.controller;
import cn.hutool.core.map.MapUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.parameter.model.vo.ProjectBank;
import cn.visolink.system.parameter.model.vo.ProjectDiyCode;
import cn.visolink.system.parameter.service.ParameterManagementService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理参数管理
 *
 * @author ligengying
 * @date 20190906
 */

@RestController
@Api(tags = "系统管理-参数管理")
@RequestMapping("param")
public class ParameterManagementController {

    @Autowired
    private ParameterManagementService parameterService;

    /**
     * 查询系统所有的参数
     *
     * @param
     * @return
     */
    @Log("查询所有的参数")
    @CessBody
    @ApiOperation(value = "查询系统所有的参数")
    @PostMapping("getSystemAllParams")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authCompanyId", value = "认证公司ID"),
            @ApiImplicitParam(name = "productId", value = "产品ID"),
            @ApiImplicitParam(name = "projectId", value = "项目ID")
    })
    public List<Map> getSystemAllParams(@RequestBody HashMap<String,String> reqMap) {

        List<Map> dictionaryList = parameterService.getSystemAllparams(reqMap);
        return dictionaryList;
    }

    /**
     * 系统新增参数
     *
     * @param reqMap
     * @return
     */
    @Log("系统新增参数")
    @CessBody
    @ApiOperation(value = "系统新增参数")
    @PostMapping("saveSystemParam")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父ID"),
            @ApiImplicitParam(name = "name", value = "参数名称"),
            @ApiImplicitParam(name = "code", value = "参数Code")
    })
    public int saveSystemParam(@RequestBody Map reqMap) {

        int number = parameterService.saveSystemParam(reqMap);
        return number;
    }

    @Log("新增参数")
    @CessBody
    @ApiOperation(value = "新增参数")
    @PostMapping("saveSystemParamNew")
    public int saveSystemParamNew(@RequestBody Map reqMap) {
        int number = parameterService.saveSystemParamNew(reqMap);
        return number;
    }

    @Log("系统新增二级参数")
    @CessBody
    @ApiOperation(value = "系统新增二级参数")
    @PostMapping("saveSystemParamSecond")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父ID"),
            @ApiImplicitParam(name = "name", value = "参数名称"),
            @ApiImplicitParam(name = "code", value = "参数Code")
    })
    public int saveSystemParamSecond(@RequestBody Map reqMap) {

        int number = parameterService.saveSystemParamSecond(reqMap);
        return number;
    }

    @ApiOperation("项目自定义字段")
    @PostMapping("/saveProjectDiyCode")
    public ResultBody saveProjectDiyCode(@RequestBody Map map){
        return ResultBody.success(parameterService.saveProjectDiyCode(map));
    }


    @ApiOperation("获取自定义字段")
    @PostMapping("/getProjectDiyCode")
    public ResultBody getProjectDiyCode(@RequestBody ProjectDiyCode projectDiyCode){
        List<Map> list = parameterService.getProjectDiyCode(projectDiyCode);
      if(projectDiyCode.getPageNum() !=null){
          PageHelper.startPage(projectDiyCode.getPageNum(),projectDiyCode.getPageSize());
          PageInfo pageInfo = new PageInfo(list);
          return ResultBody.success(pageInfo);
      }else{
          return ResultBody.success(list);
      }
    }

    @ApiOperation("编辑自定义字段")
    @PostMapping("/updateProjectDiyCode")
    public ResultBody updateProjectDiyCode(@RequestBody Map map){
        return ResultBody.success(parameterService.updateProjectDiyCode(map));
    }

    @ApiOperation("/修改状态")
    @PostMapping("/updateStatus")
    public ResultBody updateStatus(@RequestBody Map map){
        return ResultBody.success(parameterService.updateCodeStatus(map));
    }


    /**
     * 上移下移参数
     * */
    @Log("调整参数位置")
    @ApiOperation(value = "调整参数位置")
    @PostMapping("orderParam")
    public ResultBody orderParam(@RequestBody Map reqMap){
        parameterService.orderParam(reqMap);
        return ResultBody.success("成功");
    }

    /**
     * 系统修改参数
     *
     * @param reqMap
     * @return
     */
    @Log("系统修改参数")
    @CessBody
    @ApiOperation(value = "系统修改参数")
    @PostMapping("modifySystemParam")
    @ApiModelProperty(name = "reqMap", value = "请求参数")
    public int modifySystemParam(@RequestBody Map reqMap) {
        System.out.println(reqMap.get("ID"));
        int number = parameterService.modifySystemParam(reqMap);
        return number;
    }

    @Log("系统修改三级参数")
    @CessBody
    @ApiOperation(value = "系统修改三级参数")
    @PostMapping("modifyParamTertiary")
    @ApiModelProperty(name = "reqMap", value = "请求参数")
    public int modifyParamTertiary(@RequestBody Map reqMap) {
        System.out.println(reqMap.get("ID"));
        return parameterService.modifyParamTertiary(reqMap);
    }

    @Log("根据ID获取数据信息")
    @CessBody
    @ApiOperation(value = "根据ID获取数据信息")
    @PostMapping("getInfoById")
    @ApiModelProperty(name = "reqMap", value = "请求参数")
    public Map getInfoById(@RequestBody Map reqMap) {
        return parameterService.getInfoById(reqMap);
    }

    /**
     * 删除系统参数
     *
     * @param reqMap
     * @return
     */
    @Log("删除系统参数")
    @CessBody
    @ApiOperation(value = "删除系统参数")
    @PostMapping("removeSystemParam")
    @ApiModelProperty(name = "id", value = "参数ID")
    public int removeSystemParam(@RequestBody Map reqMap) {
        int number = parameterService.removeSystemParam(reqMap);
        return number;
    }

    /**
     * 查询子集参数（树形）
     *
     * @param id
     * @return
     */
    @Log("查询子集参数（树形）")
    @CessBody
    @ApiOperation(value = "查询子集参数（树形）")
    @GetMapping("getSystemTreeChildParams")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "参数ID"),
            @ApiImplicitParam(name = "projectId", value = "项目ID")
    })
    public List<Map> getSystemTreeChildParams(@RequestParam String id, @RequestParam String projectId) {

        Map reqMap = MapUtil.newHashMap();
        reqMap.put("id", id);
        reqMap.put("projectId", projectId);
        return parameterService.getSystemTreeChildParams(reqMap);
    }

    /**
     * 查询子集参数（非树形）
     *
     * @param pid
     * @param projectId
     * @return
     */
    @Log("查询子集参数（非树形）")
    @CessBody
    @ApiOperation(value = "查询子集参数（非树形）")
    @GetMapping("getSystemChildParams")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "父ID"),
            @ApiImplicitParam(name = "projectId", value = "项目ID"),
            @ApiImplicitParam(name = "pageSize",value ="每页显示数量"),
            @ApiImplicitParam(name="pageIndex",value ="当前页")
    })
    public Map getSystemChildParams(String pid, String projectId, String CityId, String type, String pageSize, String pageIndex) {
        Map reqMap = MapUtil.newHashMap();
        reqMap.put("pid", pid);
        reqMap.put("projectId", projectId);
        reqMap.put("CityId", CityId);
        reqMap.put("type", type);
        reqMap.put("pageSize", pageSize);
        reqMap.put("pageIndex", pageIndex);
        return parameterService.getSystemChildParams(reqMap);
    }

    /**
     * 启用/禁用参数
     *
     * @param id
     * @param status
     * @return
     */
    @Log("启用/禁用参数")
    @CessBody
    @ApiOperation(value = "启用/禁用参数")
    @GetMapping("modifySystemParamStatus")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "参数ID"),
            @ApiImplicitParam(name = "status", value = "参数状态")
    })
    public int modifySystemParamStatus(@RequestParam String id, @RequestParam String status) {

        Map reqMap = MapUtil.newHashMap();
        reqMap.put("id", id);
        reqMap.put("status", status);
        return parameterService.modifySystemParamStatus(reqMap);
    }

    @ApiOperation(value = "启用禁用")
    @PostMapping("/updateBankStatus")
    public ResultBody updateBankStatus(@RequestBody ProjectBank projectBank){
        // 检验是否有活动使用

         if(projectBank.getStatus()==1&&!parameterService.checkActivityBank(projectBank)){
             return ResultBody.error(-13_0017,"此收款账号已被进行中的认购活动引用，不能进行禁用操作。");
         }else{
             parameterService.updateProjectBankInfo(projectBank);
             return ResultBody.success("更新成功！");
         }

    }

    @ApiOperation(value = "保存商户信息")
    @PostMapping("/saveProjectBankInfo")
    public ResultBody saveProjectBankInfo(@RequestBody ProjectBank projectBank){
        //首先判断是否保存过
        ResultBody resultBody = parameterService.getProjectBankInfo(projectBank);
        if(resultBody.getData()!=null&&resultBody.getData().equals(true)){
            parameterService.saveProjectBankInfo(projectBank);
        }else{
            return resultBody;
        }
        return ResultBody.success("保存成功");
    }
    @ApiOperation(value = "修改商户信息")
    @PostMapping("/updateProjectBankInfo")
    public ResultBody updateProjectBankInfo(@RequestBody ProjectBank projectBank){
        //首先判断是否保存过
       //  parameterService.getProjectBankInfo(projectBank);
        ResultBody resultBody = parameterService.getProjectBankInfo(projectBank);
        if(resultBody.getData()!=null&&resultBody.getData().equals(true)){
            parameterService.updateProjectBankInfo(projectBank);
        }else{
            return resultBody;
        }
        return ResultBody.success("修改成功！");
    }

    @ApiOperation(value = "获取项目分期")
    @GetMapping("/getProjectStages")
    public ResultBody getProjectStages(String projectId){
        return ResultBody.success(parameterService.getProjectStages(projectId));
    }

    @ApiOperation(value = "获取银行信息")
    @GetMapping("/getBankInfo")
    public ResultBody getBankInfo(String stageId,String bankText){
        return ResultBody.success(parameterService.getBankInfo(stageId,bankText));
    }


    @ApiOperation(value = "获取银行列表")
    @PostMapping("/getBankList")
    public ResultBody getBankList(@RequestBody ProjectBank projectBank){

        return ResultBody.success(parameterService.getBankList(projectBank));
    }


}
