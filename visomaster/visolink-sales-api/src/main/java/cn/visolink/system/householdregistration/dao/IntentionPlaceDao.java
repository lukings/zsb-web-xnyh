package cn.visolink.system.householdregistration.dao;

import cn.visolink.system.householdregistration.model.*;
import cn.visolink.system.householdregistration.model.form.CardOppID;
import cn.visolink.system.householdregistration.model.form.IntentionPlaceForm;
import cn.visolink.system.householdregistration.model.vo.IntentionPlaceVO;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 装户活动表 Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2020-07-29
 */
public interface IntentionPlaceDao {

    /**
     * @param projectId
     * @return
     * 获取分期项目
     */
    List<Map> getFProject(String projectId);
    /**
     * @Author wanggang
     * @Description //保存装户活动
     * @Date 11:05 2020/7/30
     * @Param [intentionPlaceForm]
     * @return void
     **/
    void addIntentionPlace(IntentionPlaceForm intentionPlaceForm);

    /**
     * @Author wanggang
     * @Description //更新装户活动
     * @Date 11:05 2020/7/30
     * @Param [intentionPlaceForm]
     * @return void
     **/
    void updateIntentionPlace(IntentionPlaceForm intentionPlaceForm);


    /**
     * @Author wanggang
     * @Description //保存素材
     * @Date 11:05 2020/7/30
     * @Param [list]
     * @return void
     **/
    void addIntentionPlaceMaterial(@Param("list") List<IntentionPlaceMaterial> list);

    /**
     * @Author wanggang
     * @Description //更新素材
     * @Date 11:05 2020/7/30
     * @Param [list]
     * @return void
     **/
    void updateIntentionPlaceMaterial(@Param("list") List<IntentionPlaceMaterial> list);
    /**
     * @Author wanggang
     * @Description //保存排卡分组
     * @Date 11:05 2020/7/30
     * @Param [list]
     * @return void
     **/
    void addIntentionPlaceCardGroup(@Param("list") List<IntentionPlaceCardGroup> list);

    /**
     * @Author wanggang
     * @Description //删除排卡分组
     * @Date 11:05 2020/7/30
     * @Param [活动id]
     * @return void
     **/
    void deleteIntentionPlaceCardGroup(String id);

    /**
     * @Author wanggang
     * @Description //查询排小卡选择分期项目
     * @Date 11:05 2020/7/30
     * @Param [活动id]
     * @return void
     **/
    List<String> getIntentionPlaceCardGroupX(String id);
    /**
     * @Author wanggang
     * @Description //查询排大卡选择分期
     * @Date 11:05 2020/7/30
     * @Param [活动id]
     * @return void
     **/
    List<ProBatchVO> getIntentionPlaceCardGroupDF(String id);
    /**
     * @Author wanggang
     * @Description //查询排大卡选择分期中的开盘批次
     * @Date 11:05 2020/7/30
     * @Param [活动id]
     * @return void
     **/
    List<ProBatchVO> getIntentionPlaceCardGroupDP(@Param("id") String id, @Param("projectFid") String projectFid);
    /**
     * @Author wanggang
     * @Description //查询排大卡选择分期中的开盘批次下的排卡分组
     * @Date 11:05 2020/7/30
     * @Param [活动id]
     * @return void
     **/
    List<ProBatchVO> getIntentionPlaceCardGroupDZ(@Param("id") String id, @Param("projectFid") String projectFid, @Param("openingBatch") String openingBatch);
    /**
     * @Author wanggang
     * @Description //删除楼栋单元房间
     * @Date 11:05 2020/7/30
     * @Param [map]
     * @return void
     **/
    void deleteIntentionPlaceBuild(Map map);

    /**
     * @Author wanggang
     * @Description //删除楼栋单元房间(全部)
     * @Date 11:05 2020/7/30
     * @Param [map]
     * @return void
     **/
    void deleteIntentionPlaceBuildAll(String id);

    /**
     * @Author wanggang
     * @Description //查询装户楼栋
     * @Date 11:05 2020/7/30
     * @Param [活动id]
     * @return List
     **/
    List<IntentionPlaceBuild> getIntentionPlaceBuild(Map map);

    /**
     * @Author wanggang
     * @Description //查询装户楼栋
     * @Date 11:05 2020/7/30
     * @Param [活动id]
     * @return List
     **/
    List<IntentionPlaceBuild> getIntentionPlaceBuilds(Map map);

    /**
     * @Author wanggang
     * @Description //查询装户活动楼栋单元
     * @Date 11:05 2020/7/30
     * @Param [活动id]
     * @return List
     **/
    List<BldUnit> getIntentionPlaceUnit(Map map);

    /**
     * @Author wanggang
     * @Description //查询装户活动楼栋房间
     * @Date 11:05 2020/7/30
     * @Param [活动id]
     * @return List
     **/
    List<IntentionPlaceRoom> getIntentionPlaceRoom(@Param("id") String id,@Param("buildguid") String buildguid);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 17:37 2021/2/22
     * @Param [map]
     * @return java.util.List<cn.visolink.system.householdregistration.model.IntentionPlaceRoom>
     **/
    List<IntentionPlaceRoom> selectMoveRoomResult(Map map);
    /**
     * @Author wanggang
     * @Description //保存楼栋
     * @Date 11:05 2020/7/30
     * @Param [list]
     * @return void
     **/
    void addIntentionPlaceBuild(@Param("list") List<IntentionPlaceBuild> list);

    /**
     * @Author wanggang
     * @Description //保存单元
     * @Date 11:05 2020/7/30
     * @Param [list]
     * @return void
     **/
    void addIntentionPlaceUnit(@Param("list") List<BldUnit> list);

    /**
     * @Author wanggang
     * @Description //保存房间
     * @Date 11:05 2020/7/30
     * @Param [list]
     * @return void
     **/
    void addIntentionPlaceRoom(@Param("list") List<IntentionPlaceRoom> list);
    /**
     * @Author wanggang
     * @Description //初始化房间数据
     * @Date 15:27 2020/8/3
     * @Param [list]
     * @return void
     **/
    void addResultEdit(@Param("list") List<IntentionPlaceRoom> list);
    /**
     * @Author wanggang
     * @Description //更新房间
     * @Date 11:05 2020/7/30
     * @Param [list]
     * @return void
     **/
    void updateIntentionPlaceRoom(@Param("list") List<IntentionPlaceRoom> list);
    /**
     * @Author wanggang
     * @Description //查询关联城市
     * @Date 11:05 2020/7/30
     * @Param [projectId]
     * @return void
     **/
    Map getCityByPro(String projectId);

    /**
     * @Author wanggang
     * @Description //更新活动状态
     * @Date 11:05 2020/7/30
     * @Param [projectId]
     * @return void
     **/
    void updateIntentionPlaceStatus(Map map);

    /**
     * @Author wanggang
     * @Description //查询活动列表
     * @Date 11:05 2020/7/30
     * @Param [param]
     * @return void
     **/
    List<IntentionPlaceVO> getIntentionPlacePage(IntentionPlaceForm param);

    /**
     * @Author wanggang
     * @Description //查询活动楼栋
     * @Date 11:05 2020/7/30
     * @Param [activityId]
     * @return void
     **/
    List<Map> getBldList(String activityId);

    /**
     * @Author wanggang
     * @Description //查询活动详情
     * @Date 11:05 2020/7/30
     * @Param [activityId]
     * @return void
     **/
    IntentionPlaceForm getIntentionPlaceDetail(String id);

    /**
     * @Author wanggang
     * @Description //查询活动素材
     * @Date 11:05 2020/7/30
     * @Param [activityId]
     * @return void
     **/
    List<IntentionPlaceMaterial> getIntentionPlaceMaterial(String id);

    /**
     * @Author wanggang
     * @Description //查询装户结果列表
     * @Date 11:11 2020/8/3
     * @Param [param]
     * @return java.util.List<cn.visolink.system.householdregistration.model.IntentionPlaceResult>
     **/
    List<IntentionPlaceResult> getIntentionPlaceResultPage(IntentionPlaceResult param);

    /**
     * @Author wanggang
     * @Description //更新房间发布状态
     * @Date 14:49 2020/8/3
     * @Param [map]
     * @return void
     **/
    void updateIntentionPlaceResult(Map map);

    /**
     * @Author wanggang
     * @Description //查询装户结果
     * @Date 18:17 2020/8/3
     * @Param [id]
     * @return java.util.List<cn.visolink.system.householdregistration.model.IntentionPlaceRoom>
     **/
    List<IntentionPlaceRoom> selectRoomResult(@Param("id") String id, @Param("buildguid") String buildguid);
    /**
     * @Author wanggang
     * @Description //批量更新
     * @Date 18:49 2020/8/3
     * @Param [list]
     * @return void
     **/
    void updateEditResultSome(@Param("list") List<IntentionPlaceRoom> list);
    /**
     * @Author wanggang
     * @Description //更新单个
     * @Date 18:49 2020/8/3
     * @Param [map]
     * @return void
     **/
    void updateEditResult(Map map);
    /**
     * @Author wanggang
     * @Description //获取装户详情（按房间）
     * @Date 9:03 2020/8/4
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getRoomResultDetail(Map map);

    /**
     * @Author wanggang
     * @Description //获取到访后的机会数
     * @Date 19:57 2020/8/11
     * @Param [intentionPlaceVO]
     * @return int
     **/
    int getAllCount(IntentionPlaceVO intentionPlaceVO);

    /**
     * @Author wanggang
     * @Description //获取排卡机会数
     * @Date 20:05 2020/8/11
     * @Param [cardOppID]
     * @return int
     **/
    int getCardOpp(CardOppID cardOppID);

    /**
     * @Author wanggang
     * @Description //查询排小卡范围
     * @Date 11:22 2020/8/11
     * @Param [activityId, projectId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getSmallCard(@Param("activityId") String activityId, @Param("projectId") String projectId);

    /**
     * @Author wanggang
     * @Description //查询排大卡范围
     * @Date 11:22 2020/8/11
     * @Param [activityId, projectId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getBigCard(@Param("activityId") String activityId, @Param("projectId") String projectId);
    /**
     * @Author wanggang
     * @Description //获取活动原楼栋ID
     * @Date 16:02 2020/8/18
     * @Param [id]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getOldIntentionPlaceBuildID(String id);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 16:04 2020/8/18
     * @Param [activityId, buildguid]
     * @return void
     **/
    void deleteEditResult(@Param("activityId") String activityId, @Param("buildguid") String buildguid);
    /**
     * @Author wanggang
     * @Description //获取活动楼栋
     * @Date 16:29 2020/8/19
     * @Param [id]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getActivityBuild(String id);

    /**
     * @Author wanggang
     * @Description //获取活动楼栋（装户结果）
     * @Date 16:29 2020/8/19
     * @Param [id]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getActivityBuildResult(String id);

    /**
     * @Author wanggang
     * @Description //获取概览图
     * @Date 9:52 2020/8/20
     * @Param [id]
     * @return java.util.Map
     **/
    String selectActivityPhoto(String id);
    /**
     * @Author wanggang
     * @Description //删除坐标
     * @Date 10:28 2020/8/20
     * @Param [id]
     * @return void
     **/
    void delBuildSite(String id);
    /**
     * @Author wanggang
     * @Description //保存楼栋坐标
     * @Date 10:28 2020/8/20
     * @Param [list]
     * @return void
     **/
    void addBuildSite(@Param("list") List<Map> list);
    /**
     * @Author wanggang
     * @Description //获取楼栋坐标
     * @Date 10:28 2020/8/20
     * @Param [id]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getBuildSite(String id);

    /***
     *
     * @param projectId
     *@return {}
     *@throws
     *@Description: 查询所有分期
     *@author FuYong
     *@date 2020/9/18 11:08
     */
    List<String> getFqIdByProId(@Param("projectId") String projectId);
    /**
     * @Author wanggang
     * @Description //查询调整记录
     * @Date 11:02 2021/2/19
     * @Param [map]
     * @return java.util.List<cn.visolink.system.householdregistration.model.EditRecord>
     **/
    List<EditRecord> getEditList(Map map);
    /**
     * @Author wanggang
     * @Description //获取项目下装户活动
     * @Date 11:02 2021/2/19
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProActivitys(Map map);
    /**
     * @Author wanggang
     * @Description //获取装户明细（移动装户）
     * @Date 16:56 2021/2/20
     * @Param [param]
     * @return java.util.List<cn.visolink.system.householdregistration.model.IntentionPlaceResult>
     **/
    List<IntentionPlaceResult> getIntentionPlaceResultPage2(IntentionPlaceResult param);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 15:01 2021/2/23
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> selectRoomResultCst(Map map);
    /**
     * @Author wanggang
     * @Description //删除装户结果
     * @Date 11:31 2021/3/3
     * @Param [id, editId]
     * @return void
     **/
    void delRoomResult(@Param("id") String id,@Param("editId") String editId);
    /**
     * @Author wanggang
     * @Description //更新装户结果
     * @Date 11:31 2021/3/3
     * @Param [map]
     * @return void
     **/
    void updateRoomResult(Map map);
    /**
     * @Author wanggang
     * @Description //添加调整记录
     * @Date 11:32 2021/3/3
     * @Param [map]
     * @return void
     **/
    void addEditRecords(Map map);
    /**
     * @Author wanggang
     * @Description //销售经理装户
     * @Date 11:32 2021/3/3
     * @Param [map]
     * @return void
     **/
    void addRoomResultList(Map map);
    /**
     * @Author wanggang
     * @Description //查询装户客户集合（到访客户）
     * @Date 11:32 2021/3/3
     * @Param [map]
     * @return java.util.List<cn.visolink.system.householdregistration.model.IntentionCst>
     **/
    List<IntentionCst> getIntentionPlaceUserList(Map map);
    /**
     * @Author wanggang
     * @Description //查询装户范围客户
     * @Date 11:32 2021/3/3
     * @Param [map]
     * @return java.util.List<cn.visolink.system.householdregistration.model.IntentionCst>
     **/
    List<IntentionCst> getIntentionPlaceCstList(Map map);
    /**
     * @Author wanggang
     * @Description //查询装户客户详情
     * @Date 11:32 2021/3/3
     * @Param [map]
     * @return cn.visolink.system.householdregistration.model.IntentionCst
     **/
    IntentionCst getIntentionPlaceUser(Map map);
    /**
     * @Author wanggang
     * @Description //查询项目下置业顾问
     * @Date 11:32 2021/3/3
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProSales(Map map);
    /**
     * @Author wanggang
     * @Description //查询装户范围（待装户数量）
     * @Date 11:33 2021/3/3
     * @Param [map]
     * @return java.util.List<cn.visolink.system.householdregistration.model.IntentionCst>
     **/
    List<IntentionCst> getIntentionPlaceCstOne(Map map);
    /**
     * @Author wanggang
     * @Description //查询装户范围（排卡客户）
     * @Date 11:33 2021/3/3
     * @Param [map]
     * @return java.util.List<cn.visolink.system.householdregistration.model.IntentionCst>
     **/
    List<IntentionCst> getCstCardList(Map map);
    /**
     * @Author wanggang
     * @Description //查询待删除装户记录
     * @Date 11:33 2021/3/3
     * @Param [map]
     * @return java.util.Map
     **/
    Map getDelIntentionDesc(Map map);
    /**
     * @Author wanggang
     * @Description //查询配置了修改通知的置业顾问
     * @Date 11:33 2021/3/3
     * @Param [projectid]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getEditMessageSales(String projectId);
    /**
     * @Author wanggang
     * @Description //查询需要发送消息的置业顾问
     * @Date 11:41 2021/3/3
     * @Param [param]
     * @return java.util.List<java.util.Map>
     **/
    List<String> getEditRecord(Map param);
    /**
     * @Author wanggang
     * @Description //更新为已调整
     * @Date 10:10 2021/3/4
     * @Param [activityId]
     * @return void
     **/
    void updateActivityEdit(String activityId);
    /**
     * @Author wanggang
     * @Description //查询装户结果是否存在
     * @Date 17:00 2021/3/18
     * @Param [param]
     * @return int
     **/
    int queryIsResultExit(Map param);
}
