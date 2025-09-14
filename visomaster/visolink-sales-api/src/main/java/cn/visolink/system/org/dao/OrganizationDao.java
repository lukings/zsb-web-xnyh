package cn.visolink.system.org.dao;

import cn.visolink.system.org.model.Organization;
import cn.visolink.system.org.model.form.OrganizationForm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
@Repository
public interface OrganizationDao extends BaseMapper<Organization> {
    /**
     * 根据产品Id和父级Id查找组织树
     * @return
     */
    public List<Organization> findOrgListByOrgIdAndProIdAndCompanyId(Map map);

    /**
     * 根据产品Id和父级Id查找组织树
     *
     * @param map
     * @return
     */
    List<Organization> findOrgListByOrgIdAndProIdAndCompanyId2(Map map);

    /**
     * 根据产品Id和父级Id查找组织树
     * @param page
     * @param authCompanyId
     * @param productId
     * @param orgId
     * @param pId
     * @return
     */
    public Page<Organization> findOrgListByOrgIdAndProIdAndCompanyId(Page page, @Param("authCompanyId")String authCompanyId, @Param("productId")String productId,
                                                                     @Param("orgId")String orgId, @Param("pId") String pId );


    /**
     * 根据组织Id修改组织状态，禁用/启用
     * @return
     */
    public Integer updateStatusById(OrganizationForm organizationForm);

    /**
     * 根据上级组织ID查询子组织
     * @return
     */
    public List<OrganizationForm> queryChildOrgs(Map map);
    public String queryChildOrgsCount(Map map);
    public Map getParentProject(String id);
    public void updateChildFullPath(Map paramMap);

    /**
     * 根据UserId查找组织列表
     * @param UserId
     * @return
     */
    List<Map> getOrgList(@Param("UserId") String UserId);


    /**
     * 查询三级
     * */
    List<Map> getThreeOrgList(Map map);

    /**
     * 通过pid查询该组织下的所有组织
     *
     * @param pid
     * @return
     */
    List<Organization> selectThreeOrgByPid(String pid);

    /**
     * 判断是否存在下一级
     *
     * @param pid
     * @return
     */
    Integer isExistNextOrg(String pid);
    /**
     * @Author wanggang
     * @Description //TODO
     * @Date 11:31 2022/10/12
     * @Param [paramMap]
     * @return java.util.List<cn.visolink.system.org.model.Organization>
     **/
    List<Organization> findOrgListByOrgIdAndProIdAndCompanyIdNew(Map paramMap);
}
