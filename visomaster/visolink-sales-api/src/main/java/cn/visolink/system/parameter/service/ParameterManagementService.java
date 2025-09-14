package cn.visolink.system.parameter.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.parameter.model.vo.ProjectBank;
import cn.visolink.system.parameter.model.vo.ProjectDiyCode;
import com.github.pagehelper.PageInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数管理接口
 *
 * @author ligengying
 * @date 20190909
 */

public interface ParameterManagementService {

    /**
     * 获取所有的参数
     *
     * @param reqMap
     */
    List<Map> getSystemAllparams(HashMap<String, String> reqMap);

    /**
     * 系统新增参数
     *
     * @param reqMap
     * @return
     */
    int saveSystemParam(Map reqMap);

    /**
     * 系统新增参数xin
     *
     * @param reqMap
     * @return
     */
    int saveSystemParamNew(Map reqMap);

    /**
     * 新增系统二级参数
     * @param reqMap
     * @return
     */
    int saveSystemParamSecond(Map reqMap);

    /**
     * 系统排序参数
     *
     * @param reqMap
     * @return
     */
    int orderParam(Map reqMap);
    /**
     * 系统修改参数
     *
     * @param reqMap
     * @return
     */
    int modifySystemParam(Map reqMap);

    int modifyParamTertiary(Map reqMap);

    Map getInfoById(Map reqMap);

    /**
     * 删除系统参数
     *
     * @param reqMap
     * @return
     */
    int removeSystemParam(Map reqMap);

    /**
     * 查询子集参数（树形）
     *
     * @param reqMap
     * @return
     */
    List<Map> getSystemTreeChildParams(Map reqMap);

    /**
     * 查询子集参数（非树形）
     *
     * @param reqMap
     * @return
     */
    Map getSystemChildParams(Map reqMap);

    /**
     * 启用/禁用参数
     *
     * @param reqMap
     * @return
     */
    int modifySystemParamStatus(Map reqMap);

    /**
     * 新增项目自定义参数
     * */
    int saveProjectDiyCode(Map map);

    /**
     * 获取自定义参数
     * */
    List<Map> getProjectDiyCode(ProjectDiyCode projectDiyCode);

    /**
     * 修改自定义参数
     * */
    int updateProjectDiyCode(Map map);

    /**
     * 修改状态
     * */
    int updateCodeStatus(Map map);

    /**
     * 获取项目分期
     * */
    List<Map> getProjectStages(String projectId);

    /**
     * 获取项目分期
     * */
    List<Map<String,Object>> getBankInfo(String stageId,String bankText);

    /**
     * 保存项目商户信息
     * */
    int saveProjectBankInfo(ProjectBank projectBank);
    /**
     * 保存项目商户信息
     * */
    int updateProjectBankInfo(ProjectBank projectBank);

    /**
     * 校验商户信息是否重复
     * */
    ResultBody getProjectBankInfo(ProjectBank projectBank);

    boolean checkActivityBank(ProjectBank projectBank);

    /**
     * 获取银行列表
     * */
    PageInfo getBankList(ProjectBank projectBank);
}
