package cn.visolink.system.activity.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.activity.model.ActivityInfo;
import cn.visolink.system.activity.model.form.ActivityInfoForm;
import cn.visolink.system.activity.model.form.RidActivityFrom;
import cn.visolink.system.activity.model.vo.*;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ActivityInfo服务类
 * </p>
 *
 * @author autoJob
 * @since 2020-05-25
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    /**
     * 根据主键查询信息对象
     *
     * @param id 主键
     * @return 信息对象
     */
    ActivityInfoVO selectById(String id);

    /**
     * 根据主键查询信息对象
     *
     * @param record 查询请求条件
     * @return 信息列表
     */
    List<ActivityInfoVO> selectAll(ActivityInfoForm record);

    /**
     * 分页查询信息对象
     *
     * @param record 查询请求条件
     * @return 列表
     */
    PageInfo<ActivityInfoVO> selectPage(ActivityInfoForm record);

    /**
     * 更新活动
     *
     * @param map 查询请求条件
     * @return
     */
    ResultBody updateActivityInfo(Map map);

    /**
     * 活动导出
     *
     * @param
     * @return
     */
    void activityExport(HttpServletRequest request, HttpServletResponse response, String param);

    /**
     * 活动保存
     *
     * @param
     * @return
     */
    ResultBody addActivityInfo(ActivityInfoForm param);

    /**
     * 查询城市
     *
     * @param
     * @return
     */
    ResultBody selectCityByPro(Map param);

    /**
     * 查询优惠券
     *
     * @param
     * @return
     */
    ResultBody selectCoupon(Map param);


    /**
     * 查询奖项优惠券
     *
     * @param
     * @return
     */
    ResultBody selectAwardCoupon(Map param);
    /**
     * 查询活动详情
     *
     * @param
     * @return
     */
    ResultBody selectActivityById(Map param);

    /**
     * 更新活动详情
     *
     * @param
     * @return
     */
    ResultBody updateActivityById(ActivityInfoForm param);

    /**
     * 更新redis
     *
     * @param
     * @return
     */
    ResultBody updateRedisById(Map param);

    ResultBody updateRedisXy();

    /**
     * 查询项目
     *
     * @param
     * @return
     */
    List<ResultProjectVO> getProjectListByUserId(Map<String, Object> map);

    /***
     *
     * @param map
     *@return {}
     *@throws
     *@Description:查询楼盘经纬度
     *@author FuYong
     *@date 2020/6/28 18:10
     */
    List<Map> getLatitudeAndLongitudeList(Map map);

    /***
     *
     * @param map
     *@return {}
     *@throws
     *@Description: 查询活动报名明细
     *@author FuYong
     *@date 2020/6/30 10:19
     */
    ResultBody getActivitySignUpDetailedList(Map map);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 查询活动助力明细
    *@author FuYong
    *@date 2020/6/30 10:19
    */
    ResultBody getActivityHelpDetailedList(Map map);

    /**
     * @param request
     * @param response
     * @param param
     * @Description: 活动报名明细导出
     */
    void activitySignUpDetailedExport(HttpServletRequest request, HttpServletResponse response, String param);

    /**
     * @param map
     * @Description: 活动关联项目查询
     */
    List<Map> getActivityPro(Map map);

    /**
     * @param map
     * @Description: 生成签到二维码
     */
    ResultBody createQr(Map map);

    /**
     * @param request
     * @param response
     * @param param
     * @Description: 活动助力明细导出
     */
    void activityHelpDetailedExport(HttpServletRequest request, HttpServletResponse response, String param);

    /**
     * @param map
     * @Description: 查询助力详情
     */
    List<Map> getHelpDetail(Map map);

    /***
    *
     * @param map
    *@return {}
    *@throws
    *@Description: 查询活动统计详情
    *@author FuYong
    *@date 2020/7/20 17:44
    */
    ResultBody getActivityCensusDetail(Map map);
    /**
     * @Author wanggang
     * @Description //活动信息导出(使用异步方式)
     * @Date 15:26 2020/8/25
     * @Param [param]
     * @return java.lang.String
     **/
    String activityExportNew(String param);
    /**
     * @Author wanggang
     * @Description //获取活动报名及助力成功人数
     * @Date 11:32 2020/9/15
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getHelpAndSingUpCount(Map param);
    /**
     * @Author wanggang
     * @Description //查询排序活动
     * @Date 19:47 2020/10/29
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getActivityOrder(Map map);
    /**
     * @Author wanggang
     * @Description //保存排序
     * @Date 19:47 2020/10/29
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addActivityOrder(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 16:22 2020/11/2
     * @Param [param]
     * @return void
     **/
    String activitySignUpDetailedExportNew(Map param);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 16:22 2020/11/2
     * @Param [param]
     * @return void
     **/
    String activityHelpDetailedExportNew(Map param);

    /**
     * @Author wanggang
     * @Description //获取当前活动状态
     * @Date 9:51 2020/11/13
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getActivityStatus(Map map);


    /**
    * 活动报名明细活动列表
    * */
    List<Map> getSignUpActivityList(Map map);

    /**
     * 导出
    * */
    ResultBody getExcel(HttpServletRequest request, HttpServletResponse response,String activity_id,String exportType);
    /**
     * @Author wanggang
     * @Description //查询活动发起助力人数
     * @Date 15:42 2020/12/17
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getActivityHelpCount(Map map);
    /**
     * @Author wanggang
     * @Description //分页查询活动数据
     * @Date 11:38 2020/12/30
     * @Param [param]
     * @return com.github.pagehelper.PageInfo<cn.visolink.system.activity.model.vo.ActivityInfoVO>
     **/
    PageInfo<ActivityInfoVO> queryActivityListPage(ActivityInfoForm param);
    /**
     * @Author wanggang
     * @Description //获取报名及助力数量
     * @Date 15:02 2020/12/30
     * @Param [id]
     * @return cn.visolink.system.activity.model.vo.ActivityInfoVO
     **/
    ActivityInfoVO getActivitySumCountById(String id);

    ResultBody selectCouponByProjectId(Map param);

    void updateCouNo();

    ResultBody addRidActivity(RidActivityFrom ridActivityFrom);

    ResultBody updateRidActivity(RidActivityFrom ridActivityFrom);

    ResultBody getRidActivityInfo(String ridActivityId);

    ResultBody changeRidActStatus(ChangeStatusVo changeStatusVo);

    List<RidActivityInfoVo> queryAllRidActivity();

    ResultBody<PageInfo<RidActivityInfoVo>> getRidActsByConditions(RidActConditionsVo ridActConditionsVo);

    ResultBody delRidActById(String ridActId);

    void ridActExport(HttpServletRequest request,HttpServletResponse response,RidActConditionsVo ridActConditionsVo) throws IOException;

    ResultBody<PageInfo<RidActDetailsVo>> getRidActTripDetails(RidActConditionsVo ridActConditionsVo);

    void ridActDetailsExport(HttpServletRequest request, HttpServletResponse response, RidActConditionsVo ridActConditionsVo);


}
