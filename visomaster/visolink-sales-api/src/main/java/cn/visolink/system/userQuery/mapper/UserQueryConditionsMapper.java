package cn.visolink.system.userQuery.mapper;

import cn.visolink.system.userQuery.entity.UserQueryConditions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户查询条件Mapper接口
 * @author system
 * @date 2025/1/27
 */
@Mapper
public interface UserQueryConditionsMapper {
    
    /**
     * 保存或更新用户查询条件
     */
    int saveOrUpdate(@Param("id") String id,
                     @Param("userId") String userId, 
                     @Param("interfaceName") String interfaceName, 
                     @Param("queryParams") String queryParams);
    
    /**
     * 查询用户指定接口的最近一次查询条件
     */
    UserQueryConditions getLatestByUserAndInterface(@Param("userId") String userId, 
                                                   @Param("interfaceName") String interfaceName);
    
    /**
     * 删除用户指定接口的查询条件
     */
    int deleteByUserAndInterface(@Param("userId") String userId, 
                                @Param("interfaceName") String interfaceName);
    
    /**
     * 根据ID查询记录
     */
    UserQueryConditions getById(@Param("id") String id);

    /**
     * 根据dataRange和mapType过滤查询用户指定接口的最近一次查询条件
     */
    UserQueryConditions getLatestByUserAndInterfaceWithFilter(@Param("userId") String userId, 
                                                             @Param("interfaceName") String interfaceName,
                                                             @Param("dataRange") String dataRange,
                                                             @Param("mapType") String mapType);
}
