package cn.visolink.system.activity.dao;

import cn.visolink.system.activity.model.ActivityAddress;
import cn.visolink.system.activity.model.ActivityInfo;
import cn.visolink.system.activity.model.ActivityMaterial;
import cn.visolink.system.activity.model.form.ActivityInfoForm;
import cn.visolink.system.activity.model.form.RidActivityFrom;
import cn.visolink.system.activity.model.vo.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 营销活动表 Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2020-05-25
 */
public interface ActivityInfoDao extends BaseMapper<ActivityInfo> {

    /**
     * @param record
     * 查询所有活动
     * @return
     */
    List<ActivityInfoVO> getAllActivityInfoVO(ActivityInfoForm record);

    /**
     * @param map
     * 更新活动
     * @return
     */
    void updateActivityInfo(Map map);

    /**
     * @Author wanggang
     * @Description //保存活动素材
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    void addActivityPhoto(Map map);

    /**
     * @Author wanggang
     * @Description //更新活动素材
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    void updateActivityPhoto(ActivityMaterial map);

    /**
     * @Author wanggang
     * @Description //查询活动素材
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    List<ActivityMaterial> selectActivityPhoto(String id);

    /**
     * @Author wanggang
     * @Description //保存活动基本信息
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    void addActivityInfo(ActivityInfoForm activityInfoForm);

    /**
     * @Author wanggang
     * @Description //查询项目关联城市，楼盘
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    List<Map> getProBookCity(String projectId);

    /**
     * @Author wanggang
     * @Description //保存活动关联项目
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    void addActivityPro(Map map);

    /**
     * @Author wanggang
     * @Description //添加优惠券
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    void updateCoupon(Map map);
    /**
     * @Author 杨航行
     * @Description //添加奖项
     * @Date 11：54 2020/10/21
     * @Param [map]
     * @return void
     **/
    void saveAwardInfo(@Param("list") List<ActivityAwardInfo> list);

    /**
     * @Author 杨航行
     * @Description //删除奖项
     * @Date 11：54 2020/10/21
     * @Param [map]
     * @return void
     **/
     void delAwardInfoByActiviId(String id);

     void delCouponRel(String id);

     void saveCouponRel(@Param("id") String id,@Param("list") List<ActivityAwardInfo> list);
    /**
     * @Author wanggang
     * @Description //查询城市
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    List<String> selectCityByPro(String ids);

    /**
     * @Author wanggang
     * @Description //查询优惠券
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    List<Map> selectCoupon(@Param("activityId") String activityId,
                           @Param("proIds") String proIds,
                           @Param("couponName") String couponName);

    /**
     * @Author wanggang
     * @Description //查询优惠券项目
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    List<String> getCouponPro(String id);

    /**
     * @Author wanggang
     * @Description //查询奖项优惠券
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    List<Map> selectAwardCoupon(Map map);

    /**
     * @Author wanggang
     * @Description //查询活动关联项目
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    List<String> getActivityPro(String id);

    List<Map> getAwardInfoList(String activity_id);

    /**
     * @Author wanggang
     * @Description //查询活动详情
     * @Date 20:31 2020/5/25
     * @Param [map]
     * @return void
     **/
    ActivityInfoVO getActivityById(String id);
    /**
     * @Author wanggang
     * @Description //获取活动报名及助力成功人数
     * @Date 11:29 2020/9/15
     * @Param [id]
     * @return java.util.Map
     **/
    Map getHelpAndSingUpCount(String id);

    /**
     * @Author wanggang
     * @Description //更新活动详情
     * @Date 20:00 2020/5/26
     * @Param [activityInfoForm]
     * @return void
     **/
    void updateActivityById(ActivityInfoForm activityInfoForm);

    /**
     * @Author wanggang
     * @Description //更新活动关联项目
     * @Date 20:02 2020/5/26
     * @Param [map]
     * @return void
     **/
    void updateActivityProById(Map map);

    /**
     * @Author wanggang
     * @Description //查询项目名称
     * @Date 20:02 2020/5/26
     * @Param [map]
     * @return void
     **/
    List<String> getProNames(String pids);

    /**
     * @Author wanggang
     * @Description //查询活动优惠卷ID
     * @Date 20:02 2020/5/26
     * @Param [map]
     * @return void
     **/
    List<String> selectActivityCoupon(String id);

    /**
     * @Author wanggang
     * @Description //更新redis错误日志保存
     * @Date 20:02 2020/5/26
     * @Param [map]
     * @return void
     **/
    void addErrorEdit(Map map);

    /**
     * @Author wanggang
     * @Description //更新优惠券库存变更字段为0
     * @Date 20:02 2020/5/26
     * @Param [map]
     * @return void
     **/
    void updateCouponUCount(String id);

    /**
     * @Author wanggang
     * @Description //禁用活动时更新轮播图及弹窗
     * @Date 20:02 2020/5/26
     * @Param [map]
     * @return void
     **/
    void updatePhotoAndPopup(String id);

    /**
     * @Author wanggang
     * @Description //删除原来的素材
     * @Date 20:02 2020/5/26
     * @Param [map]
     * @return void
     **/
    void delActivityPhoto(String id);

    /***
    *
    *@param activityAddressList
    *@return {}
    *@throws
    *@Description： 新增活动地址
    *@author FuYong
    *@date 2020/6/28 17:54
    */
    void saveActivityAddress(@Param("activityAddressList") List<ActivityAddress> activityAddressList);

    /***
    *
    * @param activityId
    *@return {}
    *@throws
    *@Description： 删除活动地址
    *@author FuYong
    *@date 2020/6/28 17:55
    */
    void delActivityAddressByActivityId(String activityId);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 查询楼盘经纬度
    *@author FuYong
    *@date 2020/6/28 17:58
    */
    List<Map> getLatitudeAndLongitudeList(Map map);

    /***
    *
     * @param activityId
    *@return {}
    *@throws
    *@Description:查詢活動地址
    *@author FuYong
    *@date 2020/6/28 18:19
    */
    List<ActivityAddress> getActivityAddressList(String activityId);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 查询活动报名明细
    *@author FuYong
    *@date 2020/6/30 10:17
    */
    Page<ActivitySignUpVO> getActivitySignUpDetailedList(Map map);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 查询活动助力明细
    *@author FuYong
    *@date 2020/6/30 10:18
    */
    Page<ActivityHelpVO> getActivityHelpDetailedList(Map map);

    /**
     * @param map
     * @Description: 活动关联项目查询
     */
    List<Map> getActivityProName(Map map);

    /**
     * @param map
     * @Description: 查询助力详情
     */
    List<Map> getHelpDetail(Map map);

    /***
    *
     * @param activityId
    *@return {}
    *@throws
    *@Description: 查询活动报名详情
    *@author FuYong
    *@date 2020/7/20 17:40
    */
    Page<Map> getActivitySignUpListByActId(String activityId);

    /***
    *
     * @param activityId
    *@return {}
    *@throws
    *@Description:查询活动签到详情
    *@author FuYong
    *@date 2020/7/20 17:40
    */
    Page<Map> getActivitySignInListByActId(String activityId);

    /***
    *
     * @param activityId
    *@return {}
    *@throws
    *@Description: 查询活动发起助力详情
    *@author FuYong
    *@date 2020/7/20 17:40
    */
    Page<Map> getActivityHelpListByActId(String activityId);

    /***
    *
     * @param activityId
    *@return {}
    *@throws
    *@Description:查询活动助力详情
    *@author FuYong
    *@date 2020/7/20 17:41
    */
    Page<Map> getActivityHelpDetailListByActId(String activityId);

    /***
    *
     * @param activityId
    *@return {}
    *@throws
    *@Description: 查询活动领卷详情
    *@author FuYong
    *@date 2020/7/20 17:41
    */
    Page<Map> getActivityCouponDetailListByActId(String activityId);
    /**
     * @Author wanggang
     * @Description //查询排序活动
     * @Date 19:26 2020/10/29
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getActivityOrder(Map map);
    /**
     * @Author wanggang
     * @Description //删除排序
     * @Date 19:26 2020/10/29
     * @Param [map]
     * @return void
     **/
    void delActivityOrder(Map map);

    /**
     * @Author wanggang
     * @Description //保存排序
     * @Date 19:26 2020/10/29
     * @Param [map]
     * @return void
     **/
    void addActivityOrder(Map map);

    /**
     * @Author wanggang
     * @Description //获取活动当前状态
     * @Date 9:53 2020/11/13
     * @Param [map]
     * @return java.lang.String
     **/
    String getActivityStatus(Map map);

    /**
     * 报名 活动列表
    * */
    List<Map> getSignUpActivityList(Map map);

    /**
    * 报名明细
    * */
    List<Map> getSignUpDetailByActivityId(String activity_id);

    /**
    * 查询活动自定义字段
    * */
    String getActivityDiyCode(String activity_id);
    /**
     * @Author wanggang
     * @Description //查询活动发起助力人数
     * @Date 15:45 2020/12/17
     * @Param [map]
     * @return int
     **/
    int getActivityHelpCount(Map map);
    /**
     * @Author wanggang
     * @Description //获取活动信息
     * @Date 11:43 2020/12/30
     * @Param [param]
     * @return java.util.List<cn.visolink.system.activity.model.vo.ActivityInfoVO>
     **/
    List<ActivityInfoVO> getActivityInfoVOList(ActivityInfoForm param);
    /**
     * @Author wanggang
     * @Description //获取活动报名数量和助力数量等
     * @Date 14:58 2020/12/30
     * @Param [id]
     * @return cn.visolink.system.activity.model.vo.ActivityInfoVO
     **/
    ActivityInfoVO getActivitySumCountById(String id);

    /**
     * 更新助力优惠券信息
     * @param id
     * @param couponId
     * @param level
     * @param targetNumber
     * @param giftImageUrl
     * @param helpStatus
     */
    @Update("update a_coupon_info set activity_id = #{activityId},level = #{level},target_number = #{targetNumber},gift_image_url = #{giftImageUrl},help_status = #{helpStatus} " +
            "where id = #{couponId} and is_vow_award = 0")
    void updateHelpCoupons(@Param("activityId") String id,@Param("couponId") String couponId,@Param("level") Integer level,@Param("targetNumber") Integer targetNumber,@Param("giftImageUrl") String giftImageUrl,@Param("helpStatus") Integer helpStatus);

    @Select("select id as couponId, coupon_no as couponNo,level as level, target_number as targetNumber, gift_image_url as  giftImageUrl ,activity_id activityId, coupon_name couponName,(case when coupon_type = 1 then '折扣券'\n" +
            "        when coupon_type = 2 then '代金券'\n" +
            "        when coupon_type = 3 then '礼品券'\n" +
            "        else '' end) couponType, coupon_value couponValue,\n" +
            "        CONCAT(DATE_FORMAT(begintime,'%Y.%m.%d'),'-',DATE_FORMAT(endtime,'%Y.%m.%d')) time,\n" +
            "        (case when valid_type = 1 then\n" +
            "        CONCAT(DATE_FORMAT(valid_begintime,'%Y.%m.%d'),'-',DATE_FORMAT(valid_endtime,'%Y.%m.%d'))\n" +
            "        when valid_type = 2 and valid_hours is not null then concat('领取后',valid_hours,'天内有效')\n" +
            "        else '' end\n" +
            "        ) validityTime,stock_no as stockNo,stock_surplus as stockSurplus " +
            " from  a_coupon_info where activity_id = #{activityId} and help_status = 1 order by level asc")
    List<HelpCoupon> getHelpCoupons(@Param("activityId") String id);

    List<Map> selectCouponByProjectId(Map param);

    @Select("SELECT count(1) from a_activity_help where activity_id = #{activityId}")
    Integer getCountHelp(@Param("activityId") String id);

    @Select("SELECT is_lock as isLock,coupon_id as couponId,is_system as  isSystem from a_coupon_detail where collection_openid = #{openId} and coupon_id = #{couponId}")
    List<CouponLock> queryIsLock(@Param("couponId") String couponId, @Param("openId") String openId);

    @Select("SELECT count(1) from ( SELECT t.activity_id,IFNULL(t2.num,0) as num from a_activity_help t left join\n" +
            "(SELECT count(1) as num,help_id from a_activity_helpdetail group by help_id) t2 on t.id=t2.help_id) a  where activity_id = #{activityId} and num >= #{targetNumber}")
    Integer getSuccessHelpNo(@Param("activityId") String activityId,@Param("targetNumber") Integer targetNumber);

    @Select("SELECT count(1) from a_coupon_detail where activity_id = #{activityId} and (is_lock =3 or is_lock is NULL)")
    Integer getCountReceive(@Param("activityId") String id);

    void updateCouNo(String actNo,String id);

    List<String> getActCount();

    void addRidActivity(RidActivityFrom ridActivityFrom);

    Map getBuildBookInfoById(String buildBookId);

    void addRidActivityBuildBookInfo(Map map);

    List<String> getAllRidActBuildBookIds(String projectId);

    void updateRidActivity(RidActivityFrom ridActivityFrom);

    void updateRidActivityBuildBookInfo(Map map);

    RidActivityInfoVo getRidActivityInfo(String ridActivityId);

    void changeStatus(ChangeStatusVo changeStatusVo);

    List<RidActivityInfoVo> queryAllRidActivity();

    List<RidActivityInfoVo> getRidActsByConditions(RidActConditionsVo ridActConditionsVo);

    void delRidActById(String ridActId);

    List<RidActDetailsVo> getRidActTripDetails(RidActConditionsVo ridActConditionsVo);

    List<ActDataStatisticsVo> getAllRidPeopleCount(@Param("ids")List<String> ids);

    List<ActDataStatisticsVo> getNewReportCount(@Param("ids")List<String> ids);

    List<ActDataStatisticsVo> getNewRidArrivedCount(@Param("ids")List<String> ids);

//    Integer getNewRidBuyCount(String ridActId);

    List<String> getAllRidActivityIds();

    void saveStatisticsData(ActDataStatistics actDataStatistics);

    List<String> geAllStatisticsIds();

//    void updateStatisticsById(ActDataStatistics actDataStatistics);

    List<ActDataStatisticsVo> getAllRidPeopleCountSum(@Param("ids") List<String> ids);

    // 此方法数据统计专用
    void flushAllData();

    ActDataStatistics getActDataStatistics(String id);

    String getBookNameByActId(String id);

    List<ActDataStatisticsVo> getAllClickCount();

    void delRidStatisticsById(String ridActId);

    String getProjectName(String projectId);

    /**
     *  添加首页热推信息
     * @param hotMap 参数集合
     */
    void addActivityHotHomePage(Map<String, Object> hotMap);

    /**
     *  验证当前活动热推时间是否冲突
     * @param hotCityId
     * @param hotStartTime
     * @param hotEndTime
     * @param flag
     * @param id
     * @return
     */
    Integer checkHotHomePage(@Param("hotCityId") String hotCityId,
                             @Param("hotStartTime") String hotStartTime,
                             @Param("hotEndTime") String hotEndTime,
                             @Param("flag") String flag,
                             @Param("id") String id);

    /**
     *  更新首页热推信息
     * @param hotMap
     */
    void updateActivityHotHomePage(Map<String, Object> hotMap);

    Map getHotPageInfo(String id);

    List<Map> getCityListByAciId(Map map);

    /**
     *  根据id查询城市名称做提示信息使用
     * @param hotCityId 城市id
     * @return
     */
    String getCityNameById(String hotCityId);

    void updateHotPageStatusById(String hotId);

}
