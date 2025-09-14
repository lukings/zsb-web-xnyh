package cn.visolink.system.channel.dao;

import cn.visolink.system.channel.model.*;
import cn.visolink.system.channel.model.vo.CustomerAddLogVo;
import cn.visolink.system.channel.model.vo.CustomerFodLogVo;
import cn.visolink.system.channel.model.vo.FollowUpRecordVO;
import cn.visolink.system.channel.model.vo.ProjectCluesNew;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2022/4/19 13:55
 **/
public interface DataStatisticDao {
    /**
     * @Author wanggang
     * @Description //获取项目统计
     * @Date 15:23 2022/4/19
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<ProDataStatistics> getProDataStatistics(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取打卡统计明细
     * @Date 2025/1/27
     * @Param [paramMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getPunchInStatisticsDetail(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取打卡统计数据汇总
     * @Date 2025/1/27
     * @Param [paramMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getPunchInStatisticsSummary(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取打卡统计项目明细（按团队）
     * @Date 2025/1/27
     * @Param [paramMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getPunchInProjectDetail(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取打卡统计客户明细
     * @Date 2025/1/27
     * @Param [paramMap]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getPunchInCustomerDetail(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取项目统计汇总-跟进记录
     * @Date 09:56 2024/3/12
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getProDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取项目统计汇总-报备客户
     * @Date 09:56 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> getProDataStatisticsGatherCc(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取统计汇总-新增客户/成交客户
     * @Date 09:56 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> getDataStatisticsGatherAt(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取人员统计
     * @Date 15:24 2022/4/19
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<ProDataStatistics> getUserDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取人员统计汇总-跟进记录
     * @Date 09:56 2024/3/12
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getUserDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取人员统计汇总-报备客户
     * @Date 09:56 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> getUserDataStatisticsGatherCc(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取专员统计
     * @Date 15:23 2022/4/19
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<ProDataStatistics> getZyUserDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取专员统计汇总-跟进记录
     * @Date 09:56 2024/3/12
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getZyUserDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取专员统计汇总-报备客户
     * @Date 09:56 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> getZyUserDataStatisticsGatherCc(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取个人统计
     * @Date 15:24 2022/4/19
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<ProDataStatistics> getGrUserDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取个人统计汇总-跟进记录
     * @Date 09:56 2024/3/12
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getGrUserDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取个人统计汇总-报备客户
     * @Date 09:56 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> getGrUserDataStatisticsGatherCc(Map paramMap);

    List<Map> getTeamListByProId(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取所有区域
     * @Date 20:07 2022/4/19
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAllRegion();
    /**
     * @Author wanggang
     * @Description //获取区域数据
     * @Date 21:11 2022/4/19
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<ProDataStatistics> getRegionDataStatistics(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取项目成交统计
     * @Date 11:21 2022/4/20
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.DealStatistics>
     **/
    List<DealStatistics> getProDealStatistics(Map paramMap);
    List<DealStatistics> getProDealStatisticsNew(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取人员成交统计
     * @Date 11:21 2022/4/20
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.DealStatistics>
     **/
    List<DealStatistics> getUserDealStatistics(Map paramMap);
    List<DealStatistics> getUserDealStatisticsNew(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取区域成交数据
     * @Date 11:21 2022/4/20
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.DealStatistics>
     **/
    List<DealStatistics> getRegionDealStatistics(Map paramMap);
    List<DealStatistics> getRegionDealStatisticsNew(Map paramMap);

    /**
     * @Author wanggang
     * @Description //获取跟进统计
     * @Date 20:55 2022/4/20
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.FollowUpStatistics>
     **/
    List<FollowUpStatistics> getFollowUpStatistics(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取项目转化率
     * @Date 16:24 2022/9/1
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProConversionRate>
     **/
    List<ProConversionRate> proConversionRateStatistics(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取区域转化率
     * @Date 16:26 2022/9/1
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProConversionRate>
     **/
    List<ProConversionRate> regionConversionRateStatistics(Map paramMap);
    /**
     * @Author wanggang
     * @Description //获取项目下人员
     * @Date 19:23 2022/9/13
     * @Param [projectId, qyzygw]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getProUsers(@Param("projectId") String projectId,@Param("jobCode") String qyzygw);

    /**
     * @Author wanggang
     * @Description //获取区域项目ID
     * @Date 19:28 2022/9/13
     * @Param [projectId]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getPros(String projectId);
    /**
     * @Author wanggang
     * @Description //获取所有区域
     * @Date 14:03 2022/9/15
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAllRegionNew(Map map);
    /**
     * @Author wanggang
     * @Description //获取区域下项目
     * @Date 15:02 2022/9/19
     * @Param [regionList]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getProList(@Param("list") List<String> regionList);
    /**
     * @Author wanggang
     * @Description //区域人员统计
     * @Date 15:09 2022/9/19
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<ProDataStatistics> getUserQyDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //区域人员统计汇总-跟进记录
     * @Date 16:38 2024/3/12
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getUserQyDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //区域人员统计汇总-报备客户
     * @Date 16:38 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> getUserQyDataStatisticsGatherCc(Map paramMap);

    /**
     * @Author wanggang
     * @Description //区域统计
     * @Date 15:50 2022/9/19
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<ProDataStatistics> getQyDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //区域统计汇总-跟进记录
     * @Date 16:35 2024/3/12
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getQyDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //区域统计汇总-报备客户
     * @Date 16:35 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> getQyDataStatisticsGatherCc(Map paramMap);

    List<String> getUserAscInsPro(@Param("proIds") List<String> proIds);
    List<String> getUserAscInsProHasLz(@Param("proIds") List<String> proIds);
    List<String> getUserAscInsGroup();

    /**
     * @Author luqianqian
     * @Description //获取客户来源统计
     * @Date 16:19 2024/4/19
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<SourceModeDataStatistics> getSourceModeDataStatistics(Map paramMap);
    List<SourceModeDataStatistics> getSourceModeDataStatisticsXt(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取集团统计
     * @Date 15:23 2024/11/10
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<ProDataStatistics> getGroupDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取集团统计汇总-跟进记录
     * @Date 15:23 2024/11/10
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getGroupDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取集团统计汇总-报备客户
     * @Date 15:23 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> getGroupDataStatisticsGatherCc(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取集团人员统计
     * @Date 15:23 2024/11/10
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.ProDataStatistics>
     **/
    List<ProDataStatistics> getGroupUserDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取集团人员统计汇总-跟进记录
     * @Date 15:23 2024/11/10
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.FollowUpRecordVO>
     **/
    List<FollowUpRecordVO> getGroupUserDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //获取集团人员统计汇总-报备客户
     * @Date 15:23 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProjectCluesNew> getGroupUserDataStatisticsGatherCc(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //查询成交客户明细
     * @Date 15:23 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProDataStatistics> customerTradeCycleDataStatisticsGather(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //查询成交周期统计
     * @Date 15:23 2024/11/23
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<ProDataStatistics> customerTradeCycleDataStatistics(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //查询客户报备日志记录
     * @Date 15:23 2024/11/24
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<CustomerAddLogVo> getAllCustomerAddLogList(Map paramMap);

    /**
     * @Author luqianqian
     * @Description //查询客户跟进交易日志记录
     * @Date 15:23 2024/11/24
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<CustomerFodLogVo> getAllCustomerFodLogList(@Param("ids") List<String> ids);

    /**
     * @Author luqianqian
     * @Description //根据项目id集合查询项目集合
     * @Date 15:23 2024/11/24
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.channel.model.vo.ProjectCluesNew>
     **/
    List<Map> getProjectIds(Map paramMap);

    List<Map> getProjectAreaIds(Map paramMap);
    List<Map> getProjectIdsCs(Map paramMap);
    List<Map> getRegionIds(Map paramMap);

    String getCustomerFollowVisitCount(@Param("opportunityClueId") String opportunityClueId, @Param("type") String type, @Param("reportCreateTime") String reportCreateTime, @Param("contractDate") String contractDate, @Param("typeId") String typeId);
    List<Map> getAllCustomerAddLogListPL(Map paramMap);

    List<Map> getAllCustomervisit(Map paramMap);
    List<Map> getAllCustomerThreeOne(Map paramMap);

    List<Map> getAllCustomerDaoFang(Map paramMap);

    List<Map> getAllCustomershouFang(Map paramMap);

    List<Map> getAllCustomerfuFang(Map paramMap);
    List<Map> getAllCustomerchengjiao(Map paramMap);



    List<Map> getAllCustomervisitMolecule(Map paramMap);
    List<Map> getAllCustomerThreeOneMolecule(Map paramMap);

    List<Map> getAllCustomerDaoFangMolecule(Map paramMap);

    List<Map> getAllCustomershouFangMolecule(Map paramMap);

    List<Map> getAllCustomerfuFangMolecule(Map paramMap);

    List<Map> getAllCustomerchengjiaoMolecule(Map paramMap);

    List<Map> getAllCustomerFugouMolecule(Map paramMap);
}
