package cn.visolink.system.companyQw.dao;

import cn.visolink.system.companyQw.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/12/31 9:34
 **/
public interface CompanyQwDao {

    /**
     * @Author wanggang
     * @Description //查询小程序页面信息
     * @Date 14:42 2021/12/31
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getComAppletPage();

    /**
     * @Author wanggang
     * @Description //获取项目是否配置素材
     * @Date 15:22 2021/12/31
     * @Param [projectId]
     * @return int
     **/
    int getProMediaIsOk(String projectId);

    /**
     * @Author wanggang
     * @Description //获取项目素材
     * @Date 16:35 2021/12/31
     * @Param [projectId]
     * @return java.util.List<cn.visolink.system.companyQw.model.MediaDetail>
     **/
    List<MediaDetail> getMediaDetails(String projectId);

    /**
     * @Author wanggang
     * @Description //素材IDs
     * @Date 14:56 2022/1/3
     * @Param [projectId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getMediaIds(String projectId);

    /**
     * @Author wanggang
     * @Description //获取项目素材列表
     * @Date 18:12 2021/12/31
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProMediaList(Map map);
    /**
     * @Author wanggang
     * @Description //获取活动
     * @Date 18:29 2021/12/31
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getActivityByProId(Map map);
    /**
     * @Author wanggang
     * @Description //查询新闻
     * @Date 18:39 2021/12/31
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getNewsByCityId(Map map);
    /**
     * @Author wanggang
     * @Description //查询楼盘
     * @Date 18:39 2021/12/31
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getBuildingByProId(Map map);

    /**
     * @Author wanggang
     * @Description //查询楼盘户型
     * @Date 18:39 2021/12/31
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getBuildHxByProId(Map map);
    /**
     * @Author wanggang
     * @Description //保存素材
     * @Date 15:22 2022/1/3
     * @Param [mediaDetails]
     * @return void
     **/
    void addProMediaList(@Param("list") List<MediaDetail> mediaDetails);

    /**
     * @Author wanggang
     * @Description //删除素材
     * @Date 15:41 2022/1/3
     * @Param [delIds]
     * @return void
     **/
    void delProMedia(@Param("list") List<String> delIds);

    /**
     * @Author wanggang
     * @Description //获取需删除的素材信息
     * @Date 15:41 2022/1/3
     * @Param [delIds]
     * @return void
     **/
    List<String> getDelProMedia(@Param("list") List<String> delIds);
    /**
     * @Author wanggang
     * @Description //查询分类是否存在
     * @Date 9:35 2022/1/4
     * @Param [sensitiveWordsType]
     * @return int
     **/
    int getSensitiveWordsTypeIsOk(String sensitiveWordsType);
    /**
     * @Author wanggang
     * @Description //保存敏感词分类
     * @Date 9:51 2022/1/4
     * @Param [map]
     * @return void
     **/
    void addSensitiveWordsType(Map map);
    /**
     * @Author wanggang
     * @Description //获取敏感词分类
     * @Date 9:58 2022/1/4
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getSensitiveWordsTypes();
    /**
     * @Author wanggang
     * @Description //删除敏感词
     * @Date 10:20 2022/1/4
     * @Param [id]
     * @return void
     **/
    void delSensitiveWord(String id);

    /**
     * @Author wanggang
     * @Description //获取敏感词规则ID
     * @Date 10:20 2022/1/4
     * @Param [id]
     * @return void
     **/
    String getSensitiveWordById(String id);
    /**
     * @Author wanggang
     * @Description //获取关键词列表
     * @Date 10:55 2022/1/4
     * @Param [map]
     * @return java.util.List<cn.visolink.system.companyQw.model.SensitiveWordVo>
     **/
    List<SensitiveWordVo> getSensitiveWords(Map map);
    /**
     * @Author wanggang
     * @Description //获取项目部门
     * @Date 13:54 2022/1/4
     * @Param [paramIds]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getDepartmentByPro(String paramIds);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 14:09 2022/1/4
     * @Param [wordId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getDepartmentById(String wordId);
    /**
     * @Author wanggang
     * @Description //保存关键词
     * @Date 14:44 2022/1/4
     * @Param [sensitiveWordVo]
     * @return void
     **/
    void addSensitiveWord(SensitiveWordVo sensitiveWordVo);
    /**
     * @Author wanggang
     * @Description //保存关键词关联项目部门
     * @Date 14:55 2022/1/4
     * @Param [wordPros]
     * @return void
     **/
    void addWordPros(@Param("list") List<Map> wordPros);
    /**
     * @Author wanggang
     * @Description //删除关键词关联项目部门
     * @Date 15:06 2022/1/4
     * @Param [map]
     * @return void
     **/
    void delWordPros(Map map);
    /**
     * @Author wanggang
     * @Description //获取渠道码信息
     * @Date 11:02 2022/1/7
     * @Param [id]
     * @return cn.visolink.system.companyQw.model.ChannelCode
     **/
    ChannelCode getChannelCodeById(String id);
    /**
     * @Author wanggang
     * @Description //删除本地渠道码
     * @Date 11:14 2022/1/7
     * @Param [id]
     * @return void
     **/
    void delChannelCodeById(String id);
    /**
     * @Author wanggang
     * @Description //获取渠道码列表
     * @Date 14:52 2022/1/7
     * @Param [map]
     * @return java.util.List<cn.visolink.system.companyQw.model.ChannelCode>
     **/
    List<ChannelCode> getChannelCodes(Map map);
    /**
     * @Author wanggang
     * @Description //获取渠道码成员
     * @Date 15:12 2022/1/7
     * @Param [id]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getChannelCodeUser(String id);
    /**
     * @Author wanggang
     * @Description //获取项目员工
     * @Date 16:38 2022/1/7
     * @Param [projectId]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getEmployees(String projectId);
    /**
     * @Author wanggang
     * @Description //保存渠道码
     * @Date 17:17 2022/1/7
     * @Param [channelCode]
     * @return void
     **/
    void addChannelCode(ChannelCode channelCode);
    /**
     * @Author wanggang
     * @Description //更新渠道码
     * @Date 17:37 2022/1/7
     * @Param [channelCode]
     * @return void
     **/
    void updateChannelCode(ChannelCode channelCode);

    /**
     * @Author wanggang
     * @Description //保存渠道码素材
     * @Date 15:22 2022/1/3
     * @Param [mediaDetails]
     * @return void
     **/
    void addChannelCodeMediaList(@Param("list") List<MediaDetail> mediaDetails);

    /**
     * @Author wanggang
     * @Description //删除渠道码素材
     * @Date 15:41 2022/1/3
     * @Param [delIds]
     * @return void
     **/
    void delChannelCodeMedia(@Param("list") List<String> delIds);

    /**
     * @Author wanggang
     * @Description //获取需删除的渠道码素材信息
     * @Date 15:41 2022/1/3
     * @Param [delIds]
     * @return void
     **/
    List<String> getDelChannelCodeMedia(@Param("list") List<String> delIds);

    /**
     * @Author wanggang
     * @Description //获取渠道码素材
     * @Date 16:35 2021/12/31
     * @Param [id]
     * @return java.util.List<cn.visolink.system.companyQw.model.MediaDetail>
     **/
    List<MediaDetail> getChannelCodeMediaDetails(String id);

    /**
     * @Author wanggang
     * @Description //渠道码素材IDs
     * @Date 14:56 2022/1/3
     * @Param [id]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getChannelCodeMediaIds(String id);
    /**
     * @Author wanggang
     * @Description //删除渠道码人员
     * @Date 22:06 2022/1/7
     * @Param [id]
     * @return void
     **/
    void delChannelCodeUser(String id);
    /**
     * @Author wanggang
     * @Description //保存渠道码成员
     * @Date 22:15 2022/1/7
     * @Param [list]
     * @return void
     **/
    void addChannelCodeUser(@Param("list") List<Map> list);
    /**
     * @Author wanggang
     * @Description //获取渠道码和人员关系是否存在
     * @Date 22:36 2022/1/7
     * @Param [codeMap]
     * @return int
     **/
    int getChannelCodeIsOk(Map codeMap);
    /**
     * @Author wanggang
     * @Description //查询标签是否存在
     * @Date 22:27 2022/1/8
     * @Param [tagName]
     * @return java.lang.String
     **/
    String getTagIsOk(String tagName);
    /**
     * @Author wanggang
     * @Description //查询客户标签配置是否存在
     * @Date 10:33 2022/1/20
     * @Param [tagName, groupName]
     * @return java.lang.String
     **/
    String getTagIsExist(@Param("tagName") String tagName,@Param("groupName") String groupName);

    /**
     * @Author wanggang
     * @Description //查询标签组是否存在
     * @Date 22:27 2022/1/8
     * @Param [tagName]
     * @return java.lang.String
     **/
    String getTagGroupIsOk();
    /**
     * @Author wanggang
     * @Description //标签组是否存在
     * @Date 10:31 2022/1/20
     * @Param [groupName]
     * @return java.lang.String
     **/
    String getTagGroupIsExist(String groupName);
    /**
     * @Author wanggang
     * @Description //保存标签组
     * @Date 22:31 2022/1/8
     * @Param [map]
     * @return void
     **/
    void addTagGroup(Map map);
    /**
     * @Author wanggang
     * @Description //保存标签
     * @Date 22:31 2022/1/8
     * @Param [map]
     * @return void
     **/
    void addCstTag(Map map);
    /**
     * @Author wanggang
     * @Description //获取子部门
     * @Date 15:10 2022/1/17
     * @Param [id]
     * @return int
     **/
    int getDeptChild(String id);

    /**
     * @Author wanggang
     * @Description //获取子部门是否绑定
     * @Date 15:10 2022/1/17
     * @Param [id]
     * @return int
     **/
    int getDeptChildIsBind(String id);

    /**
     * @Author wanggang
     * @Description //获取部门成员
     * @Date 15:16 2022/1/17
     * @Param [id]
     * @return int
     **/
    int getDeptUser(String id);
    /**
     * @Author wanggang
     * @Description //删除本地部门
     * @Date 15:19 2022/1/17
     * @Param [id]
     * @return void
     **/
    void delDeptById(String id);
    /**
     * @Author wanggang
     * @Description //获取部门列表
     * @Date 15:33 2022/1/17
     * @Param [map]
     * @return java.util.List<cn.visolink.system.companyQw.model.CompanyQwDept>
     **/
    List<CompanyQwDept> getCompanyQwDepts(Map map);
    /**
     * @Author wanggang
     * @Description //获取部门成员客户
     * @Date 16:32 2022/1/17
     * @Param [id]
     * @return int
     **/
    int getDeptUserCst(String id);
    /**
     * @Author wanggang
     * @Description //解除部门与组织绑定
     * @Date 16:42 2022/1/17
     * @Param [id]
     * @return void
     **/
    void delDeptOrg(@Param("id") String id,@Param("userId") String userId);
    /**
     * @Author wanggang
     * @Description //绑定部门组织
     * @Date 16:50 2022/1/17
     * @Param [id, orgId, projectId]
     * @return void
     **/
    void bindOrg(@Param("id") String id,@Param("orgId") String orgId,@Param("projectId") String projectId,@Param("userId") String userId);
    /**
     * @Author wanggang
     * @Description //查询父级部门是否绑定
     * @Date 16:53 2022/1/17
     * @Param [id]
     * @return int
     **/
    int getDeptParentIsBind(String id);
    /**
     * @Author wanggang
     * @Description //查询父级部门下组织
     * @Date 17:13 2022/1/17
     * @Param [id]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getPDeptOrg(String id);
    /**
     * @Author wanggang
     * @Description //查询离职成员信息
     * @Date 10:42 2022/1/18
     * @Param [map]
     * @return java.util.List<cn.visolink.system.companyQw.model.QuitUser>
     **/
    List<QuitUser> getQuitUserList(Map map);
    /**
     * @Author wanggang
     * @Description //查询客户分配信息
     * @Date 11:25 2022/1/18
     * @Param [map]
     * @return java.util.List<cn.visolink.system.companyQw.model.QuitUserCst>
     **/
    List<QuitUserCst> quitUserRedistDetail(Map map);
    /**
     * @Author wanggang
     * @Description //更新离职成员客户状态为等待继承
     * @Date 14:52 2022/1/18
     * @Param [map]
     * @return void
     **/
    void updateQuitUserCst(Map map);
    /**
     * @Author wanggang
     * @Description //保存分配批次表
     * @Date 17:47 2022/1/18
     * @Param [batch]
     * @return void
     **/
    void addRedistributionBatch(Map batch);
    /**
     * @Author wanggang
     * @Description //获取项目成员
     * @Date 13:54 2022/1/19
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getProEmployees(Map map);
    /**
     * @Author wanggang
     * @Description //获取客户标签分组
     * @Date 10:14 2022/1/20
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getQwCstTagGroup();

    /**
     * @Author wanggang
     * @Description //删除分组
     * @Date 11:11 2022/1/20
     * @Param [delMap]
     * @return void
     **/
    void delTagGroup(Map delMap);
    /**
     * @Author wanggang
     * @Description //删除标签
     * @Date 11:11 2022/1/20
     * @Param [delMap]
     * @return void
     **/
    void delCstTag(Map delMap);

    /**
     * @Author wanggang
     * @Description //查询标签的分组是否只有一个标签
     * @Date 11:12 2022/1/20
     * @Param [tagId]
     * @return java.lang.String
     **/
    String getTagGroupByTagId(String tagId);
    /**
     * @Author wanggang
     * @Description //更新标签信息
     * @Date 14:09 2022/1/20
     * @Param [qwCstTag]
     * @return void
     **/
    void updateCstTag(QwCstTag qwCstTag);
    /**
     * @Author wanggang
     * @Description //获取标签列表
     * @Date 14:17 2022/1/20
     * @Param [map]
     * @return java.util.List<cn.visolink.system.companyQw.model.QwCstTag>
     **/
    List<QwCstTag> getQwCstTagList(Map map);
    /**
     * @Author wanggang
     * @Description //查询客服信息
     * @Date 17:35 2022/1/26
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getQwCstService();
    /**
     * @Author wanggang
     * @Description //查询客服人员列表
     * @Date 17:36 2022/1/26
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getQwCstServiceUser(Map map);
    /**
     * @Author wanggang
     * @Description //更新客服信息
     * @Date 8:04 2022/2/4
     * @Param [map]
     * @return void
     **/
    void updateQwService(Map map);
    /**
     * @Author wanggang
     * @Description //添加客服信息
     * @Date 8:06 2022/2/4
     * @Param [map]
     * @return void
     **/
    void addQwService(Map map);
    /**
     * @Author wanggang
     * @Description //删除客服人员
     * @Date 8:31 2022/2/4
     * @Param [map]
     * @return void
     **/
    void delQwServiceUser(Map map);
    /**
     * @Author wanggang
     * @Description //获取原客服人员
     * @Date 9:36 2022/2/4
     * @Param [map]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getOldUsers(Map map);
    /**
     * @Author wanggang
     * @Description //添加客服人员
     * @Date 9:55 2022/2/4
     * @Param [maps]
     * @return void
     **/
    void addQwServiceUser(@Param("list") List<Map> maps);
    /**
     * @Author wanggang
     * @Description //删除自动回复规则
     * @Date 13:48 2022/2/8
     * @Param [map]
     * @return void
     **/
    void delAutoReply(Map map);
    /**
     * @Author wanggang
     * @Description //获取自动回复列表
     * @Date 14:26 2022/2/8
     * @Param [map]
     * @return java.util.List<cn.visolink.system.companyQw.model.QwServiceAutoReply>
     **/
    List<QwServiceAutoReply> getQwServiceAutoReply(Map map);
    /**
     * @Author wanggang
     * @Description //保存自动回复规则
     * @Date 15:10 2022/2/8
     * @Param [qwServiceAutoReply]
     * @return void
     **/
    void addAutoReply(QwServiceAutoReply qwServiceAutoReply);

    /**
     * @Author wanggang
     * @Description //保存自动回复素材
     * @Date 15:19 2022/2/8
     * @Param [allList]
     * @return void
     **/
    void addAutoReplyMedia(@Param("list") List<QwServiceAutoReplyMedia> allList);
    /**
     * @Author wanggang
     * @Description //保存自动回复关联项目
     * @Date 15:35 2022/2/8
     * @Param [proMaps]
     * @return void
     **/
    void addAutoReplyPro(@Param("list") List<Map> proMaps);
    /**
     * @Author wanggang
     * @Description //保存自动回复关联客服
     * @Date 15:35 2022/2/8
     * @Param [proMaps]
     * @return void
     **/
    void addAutoReplyRel(@Param("list") List<Map> relMaps);
    /**
     * @Author wanggang
     * @Description //保存自动回复关键词
     * @Date 15:36 2022/2/8
     * @Param [keyWordList]
     * @return void
     **/
    void addAutoReplyKeyWord(@Param("list") List<QwServiceAutoReplyKeyWord> keyWordList);
    /**
     * @Author wanggang
     * @Description //更新规则信息
     * @Date 15:46 2022/2/8
     * @Param [qwServiceAutoReply]
     * @return void
     **/
    void updateAutoReply(QwServiceAutoReply qwServiceAutoReply);
    /**
     * @Author wanggang
     * @Description //删除原关联项目
     * @Date 15:52 2022/2/8
     * @Param [ruleId]
     * @return void
     **/
    void delAutoReplyPro(String ruleId);
    /**
     * @Author wanggang
     * @Description //删除原关联客服
     * @Date 15:52 2022/2/8
     * @Param [ruleId]
     * @return void
     **/
    void delAutoReplyRel(String ruleId);
    /**
     * @Author wanggang
     * @Description //删除原关键词
     * @Date 15:53 2022/2/8
     * @Param [ruleId]
     * @return void
     **/
    void delAutoReplyKeyWord(String ruleId);
    /**
     * @Author wanggang
     * @Description //获取原素材
     * @Date 15:59 2022/2/8
     * @Param [ruleId]
     * @return java.util.List<cn.visolink.system.companyQw.model.QwServiceAutoReplyMedia>
     **/
    List<QwServiceAutoReplyMedia> getOldAutoReplyMedia(String ruleId);
    /**
     * @Author wanggang
     * @Description //删除原素材
     * @Date 17:02 2022/2/8
     * @Param [ruleId]
     * @return void
     **/
    void delOldAutoReplyMedia(String ruleId);
    /**
     * @Author wanggang
     * @Description //获取规则
     * @Date 17:21 2022/2/8
     * @Param [ruleId]
     * @return cn.visolink.system.companyQw.model.QwServiceAutoReply
     **/
    QwServiceAutoReply getAutoReply(String ruleId);
    /**
     * @Author wanggang
     * @Description //获取关联项目
     * @Date 17:27 2022/2/8
     * @Param [ruleId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getAutoReplyPro(String ruleId);
    /**
     * @Author wanggang
     * @Description //获取关联客服
     * @Date 17:27 2022/2/8
     * @Param [ruleId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getAutoReplyRel(String ruleId);
    /**
     * @Author wanggang
     * @Description //获取关键字
     * @Date 17:28 2022/2/8
     * @Param [ruleId]
     * @return java.util.List<cn.visolink.system.companyQw.model.QwServiceAutoReplyKeyWord>
     **/
    List<QwServiceAutoReplyKeyWord> getAutoReplyKeyWord(String ruleId);
    /**
     * @Author wanggang
     * @Description //获取客户总数
     * @Date 18:34 2022/2/8
     * @Param [map]
     * @return int
     **/
    int getChannelCodeCstSum(Map map);

    /**
     * @Author wanggang
     * @Description //获取新增客户数
     * @Date 18:34 2022/2/8
     * @Param [map]
     * @return java.util.Map
     **/
    int getChannelCodeCstNewCount(Map map);

    /**
     * @Author wanggang
     * @Description //获取流失客户数
     * @Date 18:34 2022/2/8
     * @Param [map]
     * @return java.util.Map
     **/
    int getChannelCodeCstDelCount(Map map);
    /**
     * @Author wanggang
     * @Description //获取成员客户明细
     * @Date 22:15 2022/2/8
     * @Param [map]
     * @return java.util.List<cn.visolink.system.companyQw.model.ChannelCodeUserDetail>
     **/
    List<ChannelCodeUserDetail> getChannelCodeUserDetail(Map map);
    /**
     * @Author wanggang
     * @Description //获取折线图总数
     * @Date 23:19 2022/2/8
     * @Param [map]
     * @return int
     **/
    int getSumChannelCodeLine(Map map);
    /**
     * @Author wanggang
     * @Description //获取关键字是否存在
     * @Date 11:37 2022/2/11
     * @Param [keyMap]
     * @return int
     **/
    int getAutoReplyKeyWordIsOk(Map keyMap);
}
