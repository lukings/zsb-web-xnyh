package cn.visolink.system.project.dao;

import cn.visolink.system.allpeople.examine.model.ProjectList;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.system.project.model.vo.TranslateProjectVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/9/17 10:11 上午
 */
//@Repository
@Mapper
public interface ProjectMapper {
    /**
     * 根据当前登录人查询对应所拥有的项目列表
     *
     * @param UserName
     * @param projectName
     * @return
     */
    public List<Map> findProjectListByUserName(@Param("UserName")String UserName,@Param("projectName") String projectName,@Param("jobCode") String jobCode,@Param("where") String where);

    /**
     * 查询项目
     * @param map
     * @return
     */
    public List<ProjectVO> getProjectListByUserName(Map map);
    public List<ProjectVO> getProjectListByUserNameSmds(Map map);
    /**
     * 查询申请权限的项目 公司层级权限是区域id 无法判断名下项目 单独获取
     * @param map
     * @return
     */
    public List<ProjectVO> getProjectListByUserIdAndQy(Map map);

    /**
     * 查询已有楼书的项目
     * @param map
     * @return
     */
    public List<ProjectVO> getBookProjectListByUserName(Map map);

    /**
     * 查询项目层级
     * @param map
     * @return
     */
    List<String> findFullPath(Map map);

    /**
     * 查询项目权限
     * @param map
     * @return
     */
    List<String> findOrgProjectId(Map map);
    /**
     * 查询数据授权的项目层级
     * @param map
     * @return
     */
    List<String> findFullPathSQX(Map map);
    List<String> findFullPathHasUser(Map map);

    /**
     * 查询个人的权限项目层级
     * @param map
     * @return
     */
    List<String> findFullPathByOwnerUser(Map map);

    List<String> getOrgListByUser(String userName);

    List<Map> getCityList(String orgStr);

    List<Map> getCityListByOrgId(String orgStr);
    /**
     * @Author wanggang
     * @Description //查询所有项目
     * @Date 11:08 2022/4/19
     * @Param [map]
     * @return java.util.List<cn.visolink.system.allpeople.examine.model.ProjectList>
     **/
    List<ProjectList> getProList(Map map);
    /**
     * @Author wanggang
     * @Description //获取项目详情
     * @Date 11:35 2022/4/19
     * @Param [map]
     * @return cn.visolink.system.allpeople.examine.model.ProjectList
     **/
    ProjectList getProDetail(Map map);
    /**
     * @Author wanggang
     * @Description //更新项目组织
     * @Date 11:53 2022/4/19
     * @Param [map]
     * @return void
     **/
    void updateProject(Map map);
    /**
     * @Author wanggang
     * @Description //根据岗位获取组织路径
     * @Date 14:36 2022/6/10
     * @Param [map1]
     * @return java.util.List<java.lang.String>
     **/
    List<String> findFullPathByJobs(Map map1);
    /**
     * @Author wanggang
     * @Description //查询
     * @Date 16:35 2022/8/31
     * @Param [map]
     * @return java.util.List<java.lang.String>
     **/
    List<String> findFullPathAll(Map map);
    List<String> findFullPathAllHasUser(Map map);

    /**
     * @Author wanggang
     * @Description //查询无需申请的权限
     * @Date 16:35 2022/8/31
     * @Param [map]
     * @return java.util.List<java.lang.String>
     **/
    List<String> findFullPathNotApply(Map map);

    List<Map> findFullPathAllInsZs(Map map);
    /**
     * @Author wanggang
     * @Description //获取区域
     * @Date 16:45 2022/8/31
     * @Param [map]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getRegionByUserName(Map map);
    /**
     * @Author luqianqian
     * @Description //更新项目关联
     * @Date 11:53 2023/2/10
     * @Param [map]
     * @return void
     **/
    void updateTranslatePro(Map map);
    /**
     * @Author luqianqian
     * @Description //保存项目关联
     * @Date 11:53 2023/2/10
     * @Param [map]
     * @return void
     **/
    void saveTranslatePro(List<Map> list);
    /**
     * @Author luqianqian
     * @Description //查询项目关联
     * @Date 11:53 2023/2/10
     * @Param [map]
     * @return void
     **/
    Map getTranslatePro(Map map);

    /**
     * @Author luqianqian
     * @Description //获取总监名下项目
     * @Date 16:31 2023/02/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<String> getZyProject(String userId);
    /**
     * @Author luqianqian
     * @Description //获取专员名下项目
     * @Date 16:31 2023/02/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<String> getZygwProject(String userId);
    String getZyProjectGw(Map map);

    /**
     * @Author luqianqian
     * @Description //获取联动项目
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<ProjectVO> getTranslateProListByAreaId(Map map);

    /**
     * @Author luqianqian
     * @Description //获取项目联动
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    TranslateProjectVo getTranslateProjectByAreaId(@Param("areaId") String areaId);
    List<TranslateProjectVo> getTranslateProjectByAreaIdInsStatus(@Param("id") String id);

    /**
     * @Author luqianqian
     * @Description //新增项目联动
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    int saveTranslateProject(TranslateProjectVo translateProjectVo);
    /**
     * @Author luqianqian
     * @Description //更新项目联动
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    int updateTranslateProject(TranslateProjectVo translateProjectVo);

    /**
     * @Author luqianqian
     * @Description //获取项目是否区域
     * @Date 16:31 2023/06/13
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    String getProIsRegion(@Param("projectId") String projectId);

    /**
     * @Author luqianqian
     * @Description //获取所有区域
     * @Date 14:03 2022/9/15
     * @Param []
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getAllRegionNew2(Map map);

    /**
     * 获取项目名称
     * */
    String getProjectNameByProjectId(@Param("projectId") String projectId);
    List<ProjectVO> getProjectListByIds(@Param("projectIds") List<String> projectIds);

    /**
     * 获取项目下的招商团队
     * */
    List<String> getTeamIdsByProject(Map paramMap);

    /**
     * 获取招商团队下专员
     * */
    String getTeamIdsByQxProject(Map paramMap);

    /**
     * 获取区域下所有项目
     * */
    List<String> getAllProInsRegion(@Param("qyIds") List<String> qyIds);
    List<String> getProInsRegion(@Param("id") String id);
}
