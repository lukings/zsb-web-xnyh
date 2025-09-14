package cn.visolink.system.activity.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.activity.model.form.CouponDetailForm;
import cn.visolink.system.activity.model.form.CouponInfoForm;
import cn.visolink.system.activity.model.vo.CouponDetailVO;
import cn.visolink.system.activity.model.vo.CouponInfoVO;
import cn.visolink.system.activity.service.CouponInfoService;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <p>
 * CouponInfo前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2020-05-27
 */
@RestController
@Api(tags = "CouponInfo")
@RequestMapping("/coupon/couponInfo")
public class CouponInfoController {

@Autowired
public CouponInfoService couponInfoService;

    /**
    * 保存单条
    * @param param 保存参数
    * @return 是否添加成功
    */
    @Log("保存数据到CouponInfo")
    @ApiOperation(value = "保存", notes = "保存数据到CouponInfo")
    @PostMapping(value = "/addCouponInfo")
    public ResultBody addCouponInfo(@RequestBody(required = false) CouponInfoForm param){
            return couponInfoService.addCouponInfo(param);
    }

    /**
    * 更新(根据主键id更新)
    * @param param 修改参数
    * @return 是否更改成功
    */

    @Log("更新(根据主键id更新)CouponInfo")
    @ApiOperation(value = "更新数据", notes = "根据主键id更新CouponInfo数据")
    @PostMapping(value = "/updateCouponInfoById")
    public ResultBody updateCouponInfoById(@RequestBody(required = false) CouponInfoForm param){
            if("1".equals(param.getIs_vow_award())){
                param.setPublishTime(null);
                param.setIsRepeatedCollection("0");
                param.setCollectionUp("1");
                param.setBegintime(null);
                param.setEndtime(null);
            }
            return couponInfoService.updateCouponInfoById(param);
    }

    /**
    * 更新(根据主键id更新状态)
    * @param param
    * @return
    */

    @Log("更新(根据主键id更新状态)")
    @ApiOperation(value = "更新(根据主键id更新状态)", notes = "更新(根据主键id更新状态)")
    @PostMapping(value = "/updateCouponStatusById")
    public ResultBody updateCouponStatusById(@ApiParam(name = "param", value = "{\"id\":\"优惠券id\",\"isDel\":\"是否删除（1：删除 0：未删除）\",\"couponStatus\":\"活动状态:1草稿，2已发布\",\"status\":\"是否启用:1已启用，0禁用\"}")
                                                 @RequestBody(required = false) Map param){
            return couponInfoService.updateCouponStatusById(param);
    }

    /**
    * 根据主键id查询单条
    * @param id 主键id
    * @return 查询结果
    */

    @Log("根据主键id查询单条CouponInfo")
    @ApiOperation(value = "获取单条数据", notes = "根据主键id获取CouponInfo数据")
    @RequestMapping(value = "/getCouponInfoById", method = RequestMethod.POST)
    public ResultBody<CouponInfoVO> getCouponInfoById(@RequestBody(required = false) Map id){
        if (id ==null || "".equals(id.get("id")+"")){
            return ResultBody.error(-21_0006,"优惠券ID未传！！");
        }
        CouponInfoVO result= couponInfoService.getCouponInfoById(id.get("id")+"");
        return ResultBody.success(result);
    }

    /**
     * 根据主键id查询优惠券状态
     * @param id 主键id
     * @return 查询结果
     */

    @Log("根据主键id查询优惠券状态")
    @ApiOperation(value = "根据主键id查询优惠券状态", notes = "根据主键id获取CouponInfo数据")
    @RequestMapping(value = "/getCouponInfoStatus", method = RequestMethod.POST)
    public ResultBody getCouponInfoStatus(@RequestBody(required = false) Map id){
        if (id ==null || "".equals(id.get("id")+"")){
            return ResultBody.error(-21_0006,"优惠券ID未传！！");
        }
        String result= couponInfoService.getCouponInfoStatus(id.get("id")+"");
        return ResultBody.success(result);
    }

    /**
    * 分页查询
    * @param param 查询条件
    * @return 查询结果
    */

    @Log("分页查询CouponInfo")
    @CessBody
    @ApiOperation(value = "分页查询", notes = "分页查询CouponInfo全部数据")
    @RequestMapping(value = "/getCouponInfoPage", method = RequestMethod.POST)
    public PageInfo<CouponInfoVO> getCouponInfoPage(@RequestBody(required = false) CouponInfoForm param){
        PageInfo<CouponInfoVO> result= couponInfoService.getCouponInfoPage(param);
            return result;
    }

    @Log("优惠券信息导出")
    @CessBody
    @ApiOperation(value = "优惠券信息导出", notes = "优惠券信息导出")
    @PostMapping(value = "/couponInfoExport")
    public void couponInfoExport(HttpServletRequest request, HttpServletResponse response,@RequestBody String param) {
        couponInfoService.couponInfoExport(request,response, param);
    }

    @Log("优惠券信息导出（异步）")
    @CessBody
    @ApiOperation(value = "优惠券信息导出（异步）", notes = "优惠券信息导出（异步）")
    @RequestMapping(value = "/couponInfoExportNew", method = RequestMethod.POST)
    public String couponInfoExportNew(@RequestBody(required = false) CouponInfoForm param) {
        return couponInfoService.couponInfoExportNew(param);
    }

    @Log("优惠券领券明细导出")
    @CessBody
    @ApiOperation(value = "优惠券领券明细导出", notes = "优惠券领券明细导出")
    @GetMapping(value = "/couponDetailExport")
    public void couponDetailExport(HttpServletRequest request, HttpServletResponse response, CouponDetailForm param) {
        couponInfoService.couponDetailExport(request,response, param);
    }

    /**
     * 分页查询领券明细
     * @param param 查询条件
     * @return 查询结果
     */

    @Log("分页查询CouponDetail")
    @CessBody
    @ApiOperation(value = "分页查询领券明细", notes = "分页查询领券明细")
    @RequestMapping(value = "/getCouponDetail", method = RequestMethod.POST)
    public PageInfo<CouponDetailVO> getCouponDetail(@RequestBody(required = false) CouponDetailForm param){
        PageInfo<CouponDetailVO> result= couponInfoService.getCouponDetail(param);
        return result;
    }

    @ApiOperation(value = "更新全部优惠券数据编号")
    @PostMapping("updateCouNo")
    public void updateCouNo(){
        couponInfoService.updateCouNo();
    }
}

