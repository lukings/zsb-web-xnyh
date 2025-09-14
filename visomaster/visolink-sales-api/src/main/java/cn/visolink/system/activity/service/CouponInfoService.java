package cn.visolink.system.activity.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.activity.model.CouponInfo;
import cn.visolink.system.activity.model.form.CouponDetailForm;
import cn.visolink.system.activity.model.form.CouponInfoForm;
import cn.visolink.system.activity.model.vo.CouponDetailVO;
import cn.visolink.system.activity.model.vo.CouponInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface CouponInfoService extends IService<CouponInfo> {

    /**
     * 保存优惠券
     * @param param
     * @return
     */
    ResultBody addCouponInfo(CouponInfoForm param);

    /**
     * 更新优惠券
     * @param param
     * @return
     */
    ResultBody updateCouponInfoById(CouponInfoForm param);

    /**
     * 查询优惠券
     * @param id
     * @return
     */
    CouponInfoVO getCouponInfoById(String id);

    /**
     * 更新优惠券
     * @param param
     * @return
     */
    ResultBody updateCouponStatusById(Map param);

    /**
     * 分页查询优惠券
     * @param param
     * @return
     */
    PageInfo<CouponInfoVO> getCouponInfoPage(CouponInfoForm param);

    /**
     * 优惠券导出
     * @param param
     * @return
     */
    void couponInfoExport(HttpServletRequest request, HttpServletResponse response, String param);

    /**
     * 分页查询优惠券领取情况
     * @param param
     * @return
     */
    PageInfo<CouponDetailVO> getCouponDetail(CouponDetailForm param);

    /**
     * 优惠券领取情况导出
     * @param param
     * @return
     */
    void couponDetailExport(HttpServletRequest request, HttpServletResponse response, CouponDetailForm param);
    /**
     * @Author wanggang
     * @Description //异步导出优惠券
     * @Date 17:19 2020/9/7
     * @Param [param]
     * @return java.lang.String
     **/
    String couponInfoExportNew(CouponInfoForm param);
    /**
     * @Author wanggang
     * @Description //获取优惠券状态
     * @Date 16:09 2020/11/11
     * @Param [id]
     * @return cn.visolink.system.activity.model.vo.CouponInfoVO
     **/
    String getCouponInfoStatus(String id);

    void updateCouNo();

}
