package cn.visolink.system.companyQw.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.companyQw.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CompanyQwService
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/12/31 9:35
 **/
public interface CompanyQwService {

    /**
     * @Author wanggang
     * @Description //查询小程序
     * @Date 10:49 2021/12/31
     * @Param [request]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getComApplet(HttpServletRequest request);
    /**
     * @Author wanggang
     * @Description //查询小程序页面
     * @Date 14:24 2021/12/31
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getComAppletPage();
    /**
     * @Author wanggang
     * @Description //查询小程序页面路径
     * @Date 14:25 2021/12/31
     * @Param [request, map]
     * @return java.util.List<java.util.Map>
     **/
    ResultBody getComAppletPagePath(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //保存项目素材
     * @Date 14:25 2021/12/31
     * @Param [request, proMediaVo]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addProMedia(HttpServletRequest request, ProMediaVo proMediaVo);
    /**
     * @Author wanggang
     * @Description //项目素材列表
     * @Date 14:25 2021/12/31
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProMediaList(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //获取项目素材详情
     * @Date 14:25 2021/12/31
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProMediaDetail(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //获取项目是否已配置素材
     * @Date 15:19 2021/12/31
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProMediaIsOk(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //保存敏感词分类
     * @Date 17:09 2022/1/3
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addSensitiveWordsType(HttpServletRequest request, String sensitiveWordsType);
    /**
     * @Author wanggang
     * @Description //获取敏感词列表
     * @Date 17:11 2022/1/3
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getSensitiveWords(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //删除敏感词
     * @Date 17:12 2022/1/3
     * @Param [request, id]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody delSensitiveWord(HttpServletRequest request, String id);
    /**
     * @Author wanggang
     * @Description //新增、编辑敏感关键词
     * @Date 17:21 2022/1/3
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addOrEditSensitiveWord(HttpServletRequest request, SensitiveWordVo sensitiveWordVo);
    /**
     * @Author wanggang
     * @Description //查询敏感关键词分类
     * @Date 17:21 2022/1/3
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getSensitiveWordsType();
    /**
     * @Author wanggang
     * @Description //查询项目渠道码列表
     * @Date 10:57 2022/1/7
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getChannelCodeList(Map map);
    /**
     * @Author wanggang
     * @Description //新增或编辑渠道码
     * @Date 10:58 2022/1/7
     * @Param [channelCode]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addOrEditChannelCode(HttpServletRequest request,ChannelCode channelCode);

    /**
     * @Author wanggang
     * @Description //删除渠道码
     * @Date 10:58 2022/1/7
     * @Param [id]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody delChannelCode(HttpServletRequest request,String id);
    /**
     * @Author wanggang
     * @Description //分发员工
     * @Date 10:59 2022/1/7
     * @Param [id]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody distributeEmployees(HttpServletRequest request,String id);
    /**
     * @Author wanggang
     * @Description //查询员工
     * @Date 16:36 2022/1/7
     * @Param [projectId]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getEmployees(String projectId);
    /**
     * @Author wanggang
     * @Description //查询渠道码详情
     * @Date 18:12 2022/1/7
     * @Param [id]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getChannelCode(String id);
    /**
     * @Author wanggang
     * @Description //查询部门列表
     * @Date 14:53 2022/1/17
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getDeptList(Map map);
    /**
     * @Author wanggang
     * @Description //删除部门
     * @Date 14:54 2022/1/17
     * @Param [request, id]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody delDept(HttpServletRequest request, String id);
    /**
     * @Author wanggang
     * @Description //绑定、解绑组织
     * @Date 14:54 2022/1/17
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody deptBinding(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //获取父部门下组织
     * @Date 17:11 2022/1/17
     * @Param [request, id]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getPDeptOrg(HttpServletRequest request, String id);
    /**
     * @Author wanggang
     * @Description //获取离职成员列表
     * @Date 9:11 2022/1/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getQuitUserList(Map map);
    /**
     * @Author wanggang
     * @Description //重分配客户
     * @Date 9:12 2022/1/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody quitUserRedistribution(HttpServletRequest request,Map map);
    /**
     * @Author wanggang
     * @Description //分配明细查询
     * @Date 11:13 2022/1/18
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody quitUserRedistDetail(Map map);
    /**
     * @Author wanggang
     * @Description //查询项目员工
     * @Date 13:50 2022/1/19
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProEmployees(Map map);
    /**
     * @Author wanggang
     * @Description //保存分组
     * @Date 9:54 2022/1/20
     * @Param [request, groupName]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addQwCstTagGroup(HttpServletRequest request, String groupName);
    /**
     * @Author wanggang
     * @Description //获取企业标签分组
     * @Date 9:55 2022/1/20
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getQwCstTagGroup();
    /**
     * @Author wanggang
     * @Description //获取企业微信客户标签列表
     * @Date 9:56 2022/1/20
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getQwCstTags(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //删除客户标签配置
     * @Date 9:56 2022/1/20
     * @Param [request, tagId]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody delQwCstTag(HttpServletRequest request, String tagId);
    /**
     * @Author wanggang
     * @Description //新增或编辑客户标签配置
     * @Date 9:57 2022/1/20
     * @Param [request, qwCstTag]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addOrEditTag(HttpServletRequest request, QwCstTag qwCstTag);
    /**
     * @Author wanggang
     * @Description //查询客服列表
     * @Date 17:27 2022/1/26
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getQwCstService();
    /**
     * @Author wanggang
     * @Description //查询客服人员列表
     * @Date 17:28 2022/1/26
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getQwCstServiceUser(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //添加或编辑客服
     * @Date 17:59 2022/1/26
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addOrEditCstService(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //删除客服人员
     * @Date 8:25 2022/2/4
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody delServiceUser(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //添加客服人员
     * @Date 9:26 2022/2/4
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addServiceUser(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //查询客服自动回复列表
     * @Date 10:52 2022/2/4
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getQwServiceAutoReply(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //删除客服自动回复
     * @Date 10:53 2022/2/4
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody delAutoReply(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //新增/编辑客服自动回复
     * @Date 8:50 2022/2/8
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody addOrEditAutoReply(HttpServletRequest request, QwServiceAutoReply qwServiceAutoReply);
    /**
     * @Author wanggang
     * @Description //获取客服自动回复详情
     * @Date 17:17 2022/2/8
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getAutoReply(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //获取渠道码统计数据
     * @Date 18:07 2022/2/8
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getChannelCodeStatistics(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //获取渠道码成员明细
     * @Date 18:07 2022/2/8
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getChannelCodeUserDetail(HttpServletRequest request, Map map);
    /**
     * @Author wanggang
     * @Description //渠道码成员明细导出
     * @Date 18:08 2022/2/8
     * @Param [request, response, param]
     * @return void
     **/
    void channelCodeUserDetailExport(HttpServletRequest request, HttpServletResponse response, String param);
    /**
     * @Author wanggang
     * @Description //获取渠道码折线图数据
     * @Date 18:08 2022/2/8
     * @Param [request, map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getChannelCodeLineChart(HttpServletRequest request, Map map);
}
