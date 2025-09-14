package cn.visolink.system.channel.controller;

import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.channel.model.form.*;
import cn.visolink.system.channel.model.vo.CustomerDistributionRecordsVO;
import cn.visolink.system.channel.model.vo.ProjectCluesVO;
import cn.visolink.system.channel.model.vo.PublicpoolVO;
import cn.visolink.system.channel.model.vo.RedistributionBatchVO;
import cn.visolink.system.channel.service.PublicpoolService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Publicpool前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2019-09-02
 */
@RestController
@Api(tags = "publicPool")
@RequestMapping("/publicPool")
public class PublicpoolController {

@Autowired
public PublicpoolService publicpoolService;

    @Log("查询projectClues")
    @CessBody
    @ApiOperation(value = "查询公共池的数据", notes = "公共池数据")
    @RequestMapping(value = "/selectAllPublic", method = RequestMethod.POST)
    public PageInfo<PublicpoolVO> selectAllPublic(@RequestBody PublicpoolForm publicpoolForm){

        PageInfo<PublicpoolVO> result = publicpoolService.selectAllPublic(publicpoolForm);
        return result;
    }

    @Log("查询重分配原因")
    @CessBody
    @ApiOperation(value = "查询重分配原因", notes = "")
    @RequestMapping(value = "/getClueResetCause", method = RequestMethod.POST)
    public List<Map> getClueResetCause(@RequestBody Map map){
        List<Map> result = publicpoolService.getClueResetCause(map);
        return result;
    }

    @Log("公共池导出")
    @CessBody
    @ApiOperation(value = "公共池导出", notes = "")
    @RequestMapping(value = "/publicExport")
    public void publicExport(HttpServletRequest request, HttpServletResponse response,@RequestBody PublicpoolForm publicpoolForm) {
        publicpoolService.publicExport(request,response, publicpoolForm);
    }

    @Log("公共池导出")
    @CessBody
    @ApiOperation(value = "公共池导出", notes = "")
    @RequestMapping(value = "/publicExportNew")
    public String publicExportNew(HttpServletRequest request, HttpServletResponse response,@RequestBody PublicpoolForm publicpoolForm) {
        return publicpoolService.publicExportNew(request,response, publicpoolForm);
    }

    @Log("查询全部公共池的数据")
    @ApiOperation(value = "查询全部公共池的数据", notes = "查询全部公共池的数据")
    @RequestMapping(value = "/getAllPublicList", method = RequestMethod.POST)
    public ResultBody getAllPublicList(@RequestBody PublicpoolForm publicpoolForm) {
        List<PublicpoolVO> result = publicpoolService.getAllPublicList(publicpoolForm);
        return ResultBody.success(new PageInfo<>(result));
    }

    @Log("获取总监公海池授权")
    @ApiOperation(value = "获取总监公海池授权", notes = "获取总监公海池授权")
    @PostMapping(value = "/getFunctionObtainZs")
    public ResultBody getFunctionObtainZs(@RequestBody Map map) {
        try{
            ResultBody resultBody = publicpoolService.getFunctionObtainZs(map);
            return resultBody;
        }catch (Exception e){
            throw new BadRequestException(-11_0002,"获取总监公海池授权数据失败！",e);
        }
    }

    @Log("总监公海池授权")
    @ApiOperation(value = "总监公海池授权", notes = "总监公海池授权")
    @PostMapping(value = "/saveFunctionObtainZs")
    public ResultBody saveFunctionObtainZs(@RequestBody Map map) {
        try{
            ResultBody resultBody = publicpoolService.saveFunctionObtainZs(map);
            return resultBody;
        }catch (Exception e){
            throw new BadRequestException(-11_0002,"公海池授权失败！",e);
        }
    }

    @Log("公客池捞取获取可选择的项目")
    @ApiOperation(value = "公客池捞取获取可选择的项目", notes = "公客池捞取获取可选择的项目")
    @PostMapping(value = "/getProjectListHasObtainCst")
    public ResultBody getProjectListHasObtainCst(@RequestBody Map map) {
        return publicpoolService.getProjectListHasObtainCst(map);
    }
}

