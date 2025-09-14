package cn.visolink.common.example.controller;

import cn.visolink.common.example.model.DataStatus;
import cn.visolink.common.example.service.CommonApiService;
import cn.visolink.exception.ResultBody;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author YHX
 * @date 2021年11月23日 15:21
 */
@RestController
@RequestMapping("/common")
public class CommonApiController {

    @Autowired
    private CommonApiService commonApiService;


    /**
     * dictCode 字典编码
     * projectId 项目ID
     * 查询字典列表
     *
     * @param dictCode  字典Code
     * @param projectId 项目id
     * @return list
     * */
    @GetMapping("/getCommonDictList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictCode", value = "字典Code"),
            @ApiImplicitParam(name = "projectId", value = "项目id"),
            @ApiImplicitParam(name = "cityId", value = "城市id")
    })
    public ResultBody getCommonDictList(String dictCode,String projectId,String cityId){
        return ResultBody.success(commonApiService.getCommonDictList(dictCode,projectId,cityId));
    }

    /**
     * 变更数据状态
     *
     * @param dataStatus 数据状态
     * @return 条数
     * */
    @PostMapping("/updateDataStatus")
    public ResultBody updateDataStatus(@RequestBody DataStatus dataStatus){
        commonApiService.updateDataStatus(dataStatus);
        return ResultBody.success("更新成功");
    }

    /**
     * 查询登录人所属组织权限
     *
     * @param userid
     * @param type
     * @return
     */
    @GetMapping("/getUserOrgList")
    public ResultBody getUserOrgList(@RequestHeader("userid")String userid,Integer type){
        return ResultBody.success(commonApiService.getUserOrgList(userid,type));
    }

}
