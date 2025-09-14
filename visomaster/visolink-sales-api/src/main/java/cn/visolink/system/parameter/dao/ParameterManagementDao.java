package cn.visolink.system.parameter.dao;


import cn.visolink.system.parameter.model.vo.ProjectBank;
import cn.visolink.system.parameter.model.vo.ProjectDiyCode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface ParameterManagementDao {

    /**
     * 查询系统的所有参数
     *
     * @param reqMap
     */
    List<Map> getSystemAllParams(HashMap<String, String> reqMap);

    /**
     * 新增系统参数
     *
     * @param map
     */
    int insertSystemParam(Map map);

    /**
     * 新增系统二级参数
     *
     * @param map
     */
    int insertSystemParamSecond(Map map);

    /**
     * 批量新增系统二级参数下面的子菜单
     *
     * @param map
     */
    int batchSecondParamChildren(@Param("list") List<Map> list, @Param("map") Map map);

    /**
     * 系统排序参数
     *
     * @param reqMap
     * @return
     */
    int orderParamAdd(Map reqMap);

    int orderParamDel(Map reqMap);
    /**
     * 修改系统参数
     *
     * @param map
     * @return
     */
    int modifySystemParam(Map map);

    int modifyParamTertiary(Map map);

    Map getInfoById(Map map);

    /**
     * 删除系统参数
     *
     * @param map
     * @return
     */
    int removeSystemParam(Map map);

    int batchRemoveSystemParam(List<Map> list);

    /**
     * 获取子集参数（树形）
     *
     * @param map
     * @return
     */
    List<Map> getSystemTreeChildParams(Map map);

    /**
     * 获取参数子级(非树形)
     *
     * @param map
     * @return
     */
    List<Map> getSystemChildParams(Map map);


    List<Map> getChildParams(Map map);

    List<Map> getAllChildParamsById(String publicPid);

    Map getParamsById(String id);

    List<Map> getParamsByPid(String pid);

    /**
     * 获取参数子级总记录数(非树形)
     *
     * @param map
     * @return
     */
    Map getSystemChildParamsCount(Map map);

    Map getChildParamsCount(Map map);

    /**
     * 查询参数Code是否已存在
     *
     * @param map
     * @return
     */
    Map getSystemParamCodeExists(Map map);


    /**
    * 保存自定义字段
    * */
    int saveProjectDiyCode(Map map);

    /**
    * 获取自定义参数
    * */
    List<Map> getProjectDiyCode(ProjectDiyCode projectDiyCode);

    /**
     * 启用/禁用参数
     *
     * @param map
     * @return
     */
    int modifySystemParamStatus(Map map);

    /**
     * 修改自定义参数
     * */
    int updateProjectDiyCode(Map map);

    /**
     * 修改项目状态
     * */
    int updateCodeStatus(Map map);

    /**
    * 获取项目分期
    * */
    /*List<ProjectStagesVO> getProjectStages(String projectId);*/
    List<Map> getProjectStages(String projectId);

    List<Map> getProject(String projectId);
    /**
    * 保存项目商户信息
    * */
    int saveProjectBankInfo(ProjectBank projectBank);
    /**
    * 保存项目商户信息
    * */
    int updateProjectBankInfo(ProjectBank projectBank);

    String getActivityBank(String id);
    /**
    * 校验商户信息是否重复
    * */
    String getProjectBankInfo(ProjectBank projectBank);

    List<Map> getBuildId(String projectFid);

    int getXkhOrderStatus(String projectId);

    List<Map> getBankList(@Param("projectId") String projectId,@Param("stageId") String stageId, @Param("proCollAccount") String proCollAccount);

    List<Map> getAllProjectList();

    /**
     * 查询参数Code是否已存在
     *
     * @param map
     * @return
     */
    Integer getSystemParamName(Map map);

    /**
     * 根据 s_dictionary表主键id查询项目id
     *
     * @param id
     * @return
     */
    String getProjectIdById(String id);

    /**
     * 跟新渠道团队和案场团队到项目表中
     *
     * @param map
     * @return
     */
    Integer updateProjectByProjectId(Map map);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 9:26 2022/9/2
     * @Param [reqMap]
     * @return int
     **/
    int getSystemParamCodeExists1(Map reqMap);
}

