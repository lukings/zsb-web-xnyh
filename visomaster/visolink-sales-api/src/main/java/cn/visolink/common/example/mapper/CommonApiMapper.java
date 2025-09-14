package cn.visolink.common.example.mapper;

import cn.visolink.common.example.model.DataStatus;
import cn.visolink.common.example.model.Dict;
import cn.visolink.common.example.model.UserOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommonApiMapper {

    /**
     * dictCode 字典编码
     * projectId 项目ID
     * 查询字典列表
     *
     * @param dictCode  字典Code
     * @param projectId 项目id
     * @return list
     * */
    List<Dict> getCommonDictList(@Param("dictCode") String dictCode, @Param("projectId") String projectId, @Param("cityId") String cityId);


    /**
     * 变更数据状态
     *
     * @param dataStatus 数据状态
     * @return 条数
     * */
    int updateDataStatus(DataStatus dataStatus);

    /**
     * 获取所属组织
     *
     * @param userId
     * @param type
     * @return
     */
    List<UserOrg> getUserOrgList(String userId, Integer type);
}
