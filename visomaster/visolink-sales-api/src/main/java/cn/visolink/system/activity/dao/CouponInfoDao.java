package cn.visolink.system.activity.dao;

import cn.visolink.system.activity.model.CouponInfo;
import cn.visolink.system.activity.model.form.CouponDetailForm;
import cn.visolink.system.activity.model.form.CouponInfoForm;
import cn.visolink.system.activity.model.vo.CouponDetailVO;
import cn.visolink.system.activity.model.vo.CouponInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券表 Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2020-05-27
 */
public interface CouponInfoDao extends BaseMapper<CouponInfo> {
    /**
     * @Author wanggang
     * @Description //保存优惠券
     * @Date 13:41 2020/5/27
     * @Param [couponInfoForm]
     * @return void
     **/
    void insertCouponInfo(CouponInfoForm couponInfoForm);

    /**
     * @Author wanggang
     * @Description //更新优惠券
     * @Date 13:41 2020/5/27
     * @Param [couponInfoForm]
     * @return void
     **/
    void updateCouponInfo(CouponInfoForm couponInfoForm);

    /**
     * @Author wanggang
     * @Description //查询优惠券
     * @Date 13:41 2020/5/27
     * @Param [id]
     * @return void
     **/
    CouponInfoVO getCouponInfoById(String id);

    /**
     * @Author wanggang
     * @Description //保存优惠券关联项目
     * @Date 13:41 2020/5/27
     * @Param [map]
     * @return void
     **/
    void insertCouponPro(Map map);

    /**
     * @Author wanggang
     * @Description //获取优惠券关联项目ID
     * @Date 13:41 2020/5/27
     * @Param [id]
     * @return void
     **/
    List<String> getCouponPro(String id);

    /**
     * @Author wanggang
     * @Description //删除优惠券关联项目
     * @Date 13:41 2020/5/27
     * @Param [map]
     * @return void
     **/
    void deleteCouponPro(Map map);

    /**
     * @Author wanggang
     * @Description //查询项目名称
     * @Date 13:41 2020/5/27
     * @Param [projectId]
     * @return void
     **/
    String getProNameById(String projectId);

    /**
     * @Author wanggang
     * @Description //更新优惠券状态
     * @Date 13:41 2020/5/27
     * @Param [projectId]
     * @return void
     **/
    void updateCouponInfoStatus(Map map);

    /**
     * @Author wanggang
     * @Description //分页查询优惠券
     * @Date 13:41 2020/5/27
     * @Param [projectId]
     * @return void
     **/
    List<CouponInfoVO> getAllCouponInfoVO(CouponInfoForm param);

    /**
     * @Author wanggang
     * @Description //查询优惠券关联项目及区域集团
     * @Date 13:41 2020/5/27
     * @Param [projectId]
     * @return void
     **/
    List<Map> getProNameAndAreaName(String id);

    /**
     * @Author wanggang
     * @Description //查询优惠券领取明细
     * @Date 13:41 2020/5/27
     * @Param [projectId]
     * @return void
     **/
    List<CouponDetailVO> getCouponDetailList(CouponDetailForm map);
    /**
     * @Author wanggang
     * @Description //获取优惠券状态
     * @Date 16:12 2020/11/11
     * @Param [id]
     * @return String
     **/
    String getCouponInfoStatus(String id);

    @Select("SELECT count(1) from a_coupon_detail where coupon_id = #{couponId} and is_lock = #{lock}")
    Integer queryLockNo(@Param("couponId") String couponId, Integer lock);

    List<String> getActCount();

    void updateCouNo(String couno, String s);
}
