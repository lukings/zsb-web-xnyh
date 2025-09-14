package cn.visolink.system.project.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import cn.visolink.system.project.model.vo.TranslateProjectVo;

import java.util.List;
import java.util.Map;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/9/17 10:10 上午
 */
public interface ProjectService {
    /**
     * 根据当前登录人查询对应所拥有的项目列表
     * @param UserName
     * @param projectName
     * @return
     */
    List<Map> findProjectListByUserId(String UserName,String projectName,String authCompanyID);

    /**
     * 根据当前登录人查询对应所拥有的项目列表
     * @param map
     * @return
     */
    List<ResultProjectVO> getProjectListByUserId(Map<String,Object> map);
    List<ResultProjectVO> getProjectAllListByUserName(Map<String,Object> map);
    List<ResultProjectVO> getProjectListByUserIdSmds(Map<String,Object> map);

    /**
     * 查询所有的项目列表
     * @param map
     * @return
     */
    List<ResultProjectVO> getProjectAllList(Map<String,Object> map);

    /**
     * 根据当前登录人查询对应所拥有数据权限项目列表
     * @param map
     * @return
     */
    List<ResultProjectVO> getProjectListByUserNameAndSqx(Map<String,Object> map);

    /**
     * 根据当前登录人查询对应所拥有的个人权限项目列表
     * @param map
     * @return
     */
    List<ResultProjectVO> getProjectListByOwnerUser(Map<String,Object> map);


    /**
     * 根据当前登录人查询对应所拥有的个人权限项目列表及申请权限
     * @param map
     * @return
     */
    List<ResultProjectVO> getProjectListByOwnerUserAndSqx(Map<String,Object> map);

    ResultBody getCityListByUser(String username);
    /**
     * @Author wanggang
     * @Description //查询项目列表
     * @Date 11:03 2022/4/19
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProList(Map map);
    /**
     * @Author wanggang
     * @Description //查询项目
     * @Date 11:29 2022/4/19
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProDetail(Map map);
    /**
     * @Author wanggang
     * @Description //更新项目信息
     * @Date 11:49 2022/4/19
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody editPro(Map map);
    /**
     * @Author wanggang
     * @Description //获取区域列表
     * @Date 16:31 2022/8/31
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getRegionByUserName();

    /**
     * @Author luqianqian
     * @Description //联动项目-新增联动项目
     * @Date 16:31 2023/02/10
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    ResultBody saveTranslatePro(Map map);

    /**
     * @Author luqianqian
     * @Description //联动项目-联动项目查询
     * @Date 16:31 2023/02/10
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    ResultBody getTranslatePro(Map map);

    /**
     * @Author luqianqian
     * @Description //获取总监名下项目
     * @Date 16:31 2023/02/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<ResultProjectVO> getZyProject(Map map);

    /**
     * @Author luqianqian
     * @Description //获取总监名下项目
     * @Date 16:31 2023/02/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<ResultProjectVO> getZygwProject(Map<String, Object> map);

    /**
     * @Author luqianqian
     * @Description //获取联动项目
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    ResultBody getTranslateProList(Map<String, Object> map);

    /**
     * @Author luqianqian
     * @Description //联动项目
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    ResultBody saveTranslateProject(TranslateProjectVo translateProjectVo);

    /**
     * @Author luqianqian
     * @Description //获取联动项目详情
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    ResultBody getTranslateProjectInfo(TranslateProjectVo translateProjectVo);

    /**
     * @Author luqianqian
     * @Description //获取项目是否区域
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    ResultBody getProIsRegion(Map map);

    /**
     * @Author luqianqian
     * @Description //获取全部区域
     * @Date 16:31 2023/10/18
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    ResultBody getAllRegionList(Map map);

    ResultBody findFullPathAllInsZs();

    List<Map> getRegionListByUserNameAndSqx(Map<String, Object> map);
}
