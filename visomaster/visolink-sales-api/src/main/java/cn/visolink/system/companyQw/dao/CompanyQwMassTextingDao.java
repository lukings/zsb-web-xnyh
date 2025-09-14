package cn.visolink.system.companyQw.dao;

import cn.visolink.system.companyQw.model.BQwMassTexting;
import cn.visolink.system.companyQw.model.MediaDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author: Mr.Yu
 * @Date: 2022/1/5 16:43
 * @description
 * @Version 1.0
 */
@Repository
public interface CompanyQwMassTextingDao {

    /**
     * 创建群发任务
     *
     * @param bQwMassTexting
     * @return
     */
    Integer createMassTextingTasks(BQwMassTexting bQwMassTexting);

    /**
     * 批量保存任务
     *
     * @param bQwMassTextingList
     * @return
     */
    Integer insertBatch(List<BQwMassTexting> bQwMassTextingList);

    /**
     * 通过实体作为筛选条件查询
     *
     * @return
     */
    List<BQwMassTexting> queryAll();

    /**
     * 通过主键修改数据
     *
     * @param qwMassTexting
     * @return
     */
    Integer updateById(BQwMassTexting qwMassTexting);

    /**
     * 通过主键进行删除
     *
     * @param id
     * @return
     */
    Integer deleteById(String id);

    /**
     * 查看群发任务列表
     *
     * @param map
     * @return
     */
    List<BQwMassTexting> selectMassTextingTasks(Map map);

    /**
     * @Author wanggang
     * @Description //查询未关联客户
     * @Date 15:54 2022/1/10
     * @Param [param]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getUserCstNoAc(Map param);

    /**
     * @Author wanggang
     * @Description //查询客户
     * @Date 15:55 2022/1/10
     * @Param [param]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getUserCstByType(Map param);

    /**
     * @Author wanggang
     * @Description //查询用户姓名
     * @Date 16:44 2022/1/10
     * @Param [senderList]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getUserNames(@Param("list") List<String> senderList);
    /**
     * @Author wanggang
     * @Description //查询客户姓名
     * @Date 16:45 2022/1/10
     * @Param [cstIds]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getCstNames(@Param("list") List<String> cstIds);
    /**
     * @Author wanggang
     * @Description //获取成员客户标签
     * @Date 16:57 2022/1/10
     * @Param [senderList]
     * @return java.util.List<java.util.Map>
     **/
    List<Map> getUserCstTags(@Param("list") List<String> senderList);
    /**
     * @Author wanggang
     * @Description //保存任务素材
     * @Date 17:31 2022/1/10
     * @Param [mediaDetails]
     * @return void
     **/
    void addTaskMediaList(@Param("list") List<MediaDetail> mediaDetails);
    /**
     * @Author wanggang
     * @Description //撤回任务
     * @Date 17:55 2022/1/10
     * @Param [id]
     * @return void
     **/
    void reMassTextingTasks(String id);

    /**
     * @Author wanggang
     * @Description //重发任务
     * @Date 18:16 2022/1/10
     * @Param [id]
     * @return void
     **/
    void reAddMassTextingTasks(Map map);
}
