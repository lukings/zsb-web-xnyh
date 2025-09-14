package cn.visolink.system.householdregistration.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.householdregistration.model.EditRecord;
import cn.visolink.system.householdregistration.model.IntentionCst;
import cn.visolink.system.householdregistration.model.IntentionPlaceBuild;
import cn.visolink.system.householdregistration.model.IntentionPlaceResult;
import cn.visolink.system.householdregistration.model.form.IntentionPlaceForm;
import cn.visolink.system.householdregistration.model.vo.IntentionPlaceVO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * IntentionPlace服务类
 * </p>
 *
 * @author autoJob
 * @since 2020-07-29
 */
public interface IntentionPlaceService{
    /**
     * @Author wanggang
     * @Description //分页获取装户活动列表
     * @Date 11:06 2020/7/29
     * @Param [param]
     * @return com.github.pagehelper.PageInfo<cn.visolink.system.householdregistration.model.vo.IntentionPlaceVO>
     **/
    PageInfo<IntentionPlaceVO> selectPage(IntentionPlaceForm param);
    /**
     * @Author wanggang
     * @Description //更新装户活动状态
     * @Date 11:09 2020/7/29
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateIntentionPlaceStatus(Map map);
    /**
     * @Author wanggang
     * @Description //保存装户活动
     * @Date 11:12 2020/7/29
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addIntentionPlace(IntentionPlaceForm param);
    /**
     * @Author wanggang
     * @Description //更新装户活动
     * @Date 11:12 2020/7/29
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateIntentionPlace(IntentionPlaceForm param);

    /**
     * @Author wanggang
     * @Description //查询项目下楼栋
     * @Date 11:12 2020/7/29
     * @Param [param]
     * @return cn.visolink.exception.ResultBody
     **/
    List<Map<String, Object>> getBldingByPro(String projectId);

    /**
     * @Author wanggang
     * @Description //查询楼栋下房间
     * @Date 11:12 2020/7/29
     * @Param [bldIds]
     * @return cn.visolink.exception.ResultBody
     **/
    List<IntentionPlaceBuild> getBldingRoomList(String bldIds, String activityId);
    /**
     * @Author wanggang
     * @Description //查询分期项目
     * @Date 11:12 2020/7/29
     * @Param [projectId]
     * @return cn.visolink.exception.ResultBody
     **/
    List<Map> getFProject(String projectId);

    /**
     * @Author wanggang
     * @Description //查询排卡分组
     * @Date 11:12 2020/7/29
     * @Param [projectId]
     * @return cn.visolink.exception.ResultBody
     **/
    List<Map> getFProjectCardGroup(String projectId);

    /**
     * @Author wanggang
     * @Description //查询排卡分组
     * @Date 11:12 2020/7/29
     * @Param [request,response,param]
     * @return cn.visolink.exception.ResultBody
     **/
    void intentionPlaceExport(HttpServletRequest request, HttpServletResponse response, String param);

    /**
     * @Author wanggang
     * @Description //查询活动详情
     * @Date 11:12 2020/7/29
     * @Param [request,response,param]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getIntentionPlaceDetail(Map map);
    /**
     * @Author wanggang
     * @Description //分页查询装户结果
     * @Date 11:07 2020/8/3
     * @Param [param]
     * @return com.github.pagehelper.PageInfo<cn.visolink.system.householdregistration.model.IntentionPlaceResult>
     **/
    PageInfo<IntentionPlaceResult> getIntentionPlaceResultPage(IntentionPlaceResult param);
    /**
     * @Author wanggang
     * @Description //导出装户结果
     * @Date 11:07 2020/8/3
     * @Param [request, response, param]
     * @return void
     **/
    void intentionPlaceResultExport(HttpServletRequest request, HttpServletResponse response, String param);
    /**
     * @Author wanggang
     * @Description //更新装户房间展示状态
     * @Date 14:41 2020/8/3
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateIntentionPlaceIsShow(Map map);
    /**
     * @Author wanggang
     * @Description //调整装户结果
     * @Date 19:15 2020/8/3
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateEditResult(Map map);
    /**
     * @Author wanggang
     * @Description //查询装户结果
     * @Date 19:16 2020/8/3
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectRoomResult(Map map);
    /**
     * @Author wanggang
     * @Description //查询装户明细（按房间）
     * @Date 19:16 2020/8/3
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getRoomResultDetail(Map map);
    /**
     * @Author wanggang
     * @Description //获取活动楼栋
     * @Date 15:52 2020/8/19
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectActivityBuild(Map map);
    /**
     * @Author wanggang
     * @Description //获取概览图
     * @Date 9:51 2020/8/20
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectActivityPhoto(Map map);
    /**
     * @Author wanggang
     * @Description //查询装户楼栋（装户结果）
     * @Date 20:06 2020/8/25
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getActivityBuildResult(Map map);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 17:01 2021/2/19
     * @Param [map]
     * @return com.github.pagehelper.PageInfo<cn.visolink.system.householdregistration.model.EditRecord>
     **/
    PageInfo<EditRecord> getEditRecordList(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 17:02 2021/2/19
     * @Param [request, response, param]
     * @return void
     **/
    void editRecordExport(HttpServletRequest request, HttpServletResponse response, String param);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 18:02 2021/2/19
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProActivitys(Map map);

    /**
     * @Author wanggang
     * @Description //查询移动装户结果
     * @Date 16:03 2021/2/22
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectMoveRoomResult(Map map);

    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 18:36 2021/2/22
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody selectRoomResultCstList(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 16:54 2021/2/23
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody updateRoomResult(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 17:04 2021/2/23
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody delRoomResult(Map map);
    /**
     * @Author wanggang
     * @Description //获取装户客户
     * @Date 20:27 2021/2/23
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<IntentionCst> getProActivityCsts(Map map);
    /**
     * @Author wanggang
     * @Description //装户
     * @Date 20:45 2021/2/23
     * @Param [map]
     * @return void
     **/
    void addRoomResultList(Map map);
    /**
     * @Author wanggang
     * @Description //装户统计
     * @Date 10:35 2021/2/24
     * @Param [map]
     * @return java.util.Map
     **/
    Map getActivityStatistics(Map map);
    /**
     * @Author wanggang
     * @Description //客户统计
     * @Date 10:35 2021/2/24
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<IntentionCst> getActivityCstStatistics(Map map);
    /**
     * @Author wanggang
     * @Description //客户详情
     * @Date 10:35 2021/2/24
     * @Param [map]
     * @return
     **/
    IntentionCst getActivityCstDetail(Map map);
    /**
     * @Author wanggang
     * @Description //查询项目下置业顾问
     * @Date 20:37 2021/2/24
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProSales(Map map);
    /**
     * @Author wanggang
     * @Description //添加置业顾问通知
     * @Date 11:12 2021/3/3
     * @Param [map]
     * @return java.lang.String
     **/
    ResultBody addSalesMessage(Map map);
}
