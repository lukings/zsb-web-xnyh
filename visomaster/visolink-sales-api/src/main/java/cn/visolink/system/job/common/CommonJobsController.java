package cn.visolink.system.job.common;

import cn.visolink.common.redis.service.RedisService;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.job.common.dao.CommonJobsDao;
import cn.visolink.system.job.common.model.form.CommonJobsForm;
import cn.visolink.system.job.common.model.vo.CommonJobsVO;
import cn.visolink.system.job.common.service.CommonJobsService;
import cn.visolink.utils.HttpClientUtil;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通用岗位前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2019-08-29
 */
@RestController
@Api(tags = "通用岗位")
@RequestMapping("/job/common")
public class CommonJobsController {

    @Autowired
    public CommonJobsService commonjobsService;

    @Autowired
    private CommonJobsDao  commonJobsDao;

    @Autowired
    private RedisService redisService;


    @Value("${outbound.updateRoleOp}")
    private String updateRoleOp;

    /**
     * 保存单条
     * @param param 保存参数
     * @return 是否添加成功
     */
   /* @Log("保存数据到Commonjobs")
    @CessBody
    @ApiOperation(value = "保存", notes = "保存数据到Commonjobs")
    @PostMapping(value = "/add")
    public Integer addCommonjobs(@RequestBody(required = false) CommonjobsForm param){
            Integer result= commonjobsService.save(param);
            return result;
            }*/

    /**
     * 更新(根据主键id更新)
     * @param param 修改参数
     * @return 是否更改成功
     */

    /*@Log("更新(根据主键id更新)Commonjobs")
    @CessBody
    @ApiOperation(value = "更新数据", notes = "根据主键id更新Commonjobs数据")
    @PostMapping(value = "/updateById")
    public Integer updateCommonjobsById(@RequestBody(required = false) CommonjobsForm param){
            Integer result= commonjobsService.updateById(param);
            return result;
            }*/

    /**
     * 删除(根据主键id伪删除)
     * @param id 主键id
     * @return 是否删除成功
     */

    /*@Log("删除(根据主键id伪删除)Commonjobs")
    @CessBody
    @ApiOperation(value = "删除数据", notes = "根据主键id伪删除Commonjobs数据")
    @PostMapping(value = "/deleteById")
    public Integer deleteCommonjobsById(String id){
            Integer result= commonjobsService.deleteById(id);
            return result;
            }*/

    /**
     * 根据主键id查询单条
     * @param 主键id
     * @return 查询结果
     */

    /*@Log("根据主键id查询单条Commonjobs")
    @CessBody
    @ApiOperation(value = "获取单条数据", notes = "根据主键id获取Commonjobs数据")
    @RequestMapping(value = "/getById", method = RequestMethod.POST)
    public CommonjobsVO getCommonjobsById(@RequestBody(required = false) String id){
        CommonjobsVO result= commonjobsService.selectById(id);
            return result;
            }*/


    @Log("查询通用岗位")
    @CessBody
    @ApiOperation(value = "全部查询", notes = "全部岗位数据")
    @RequestMapping(value = "/commonJobsSelectAll", method = RequestMethod.POST)
    public PageInfo<CommonJobsVO> commonJobsSelectAll(@RequestBody CommonJobsForm commonjobsForm) {
        PageInfo<CommonJobsVO> result = commonjobsService.commonJobsSelectAll(commonjobsForm);
        return result;
    }

    @Log("添加通用岗位")
    @CessBody
    @ApiOperation(value = "添加通用岗位", notes = "岗位数据")
    @RequestMapping(value = "/systemCommonJob_Insert", method = RequestMethod.POST)
    public Integer systemCommonJob_Insert(@RequestBody CommonJobsForm commonjobsForm, HttpServletRequest request) {
        commonjobsForm.setEditor(request.getHeader("username"));
        Integer in = commonjobsService.systemCommonJob_Insert(commonjobsForm);
        Integer res = null;
        if(in==1){
            res = 0;
        }else {
            res = 1;
        }
        return res;
    }

    @Log("删除通用岗位")
    @CessBody
    @ApiOperation(value = "删除通用岗位", notes = "岗位数据")
    @RequestMapping(value = "/systemCommonJobDelete", method = RequestMethod.POST)
    public Integer systemCommonJobDelete(@RequestBody CommonJobsForm commonjobsForm) {
        Integer in = commonJobsDao.systemCommonJobDelete(commonjobsForm);
        return 0;
    }

    @Log("启用禁用通用岗位")
    @CessBody
    @ApiOperation(value = "启用禁用通用岗位", notes = "岗位数据")
    @RequestMapping(value = "/systemCommonJobStatusUpdate", method = RequestMethod.POST)
    public Integer systemCommonJobStatusUpdate(@RequestBody CommonJobsForm commonjobsForm) {
        Integer in = commonJobsDao.systemCommonJobStatusUpdate(commonjobsForm);
        return 0;
    }

    @Log("更新通用岗位")
    @CessBody
    @ApiOperation(value = "更新通用岗位", notes = "岗位数据")
    @RequestMapping(value = "/systemCommonJobUpdate", method = RequestMethod.POST)
    public Integer systemCommonJobUpdate(@RequestBody CommonJobsForm commonjobsForm, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("title",commonjobsForm.getJobName());
        map.put("three_id",commonjobsForm.getId());
        map.put("type",4);

        HttpClientUtil.postHttpOutbound(updateRoleOp,redisService.getVal("outbound."+request.getHeader("username"))+"",map);
        Integer in = commonJobsDao.systemCommonJobUpdate(commonjobsForm);
        System.out.println(in);
        return 0;
    }




}

