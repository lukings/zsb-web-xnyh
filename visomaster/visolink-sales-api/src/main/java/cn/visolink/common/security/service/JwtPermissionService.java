package cn.visolink.common.security.service;
import com.google.common.collect.ImmutableMap;
import org.apache.wicket.core.dbhelper.sql.DBSQLServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author WCL
 */
@Service
@CacheConfig(cacheNames = "role")
public class JwtPermissionService {

    @Autowired(required = false)
    private DBSQLServiceImpl dbsqlService;
    
    /**
     * key的名称如有修改，请同步修改 UserServiceImpl 中的 update 方法
     * @param user
     * @return
     */
    @Cacheable(key = "'loadPermissionByUser:' + #p0")
    public Collection<GrantedAuthority> mapToGrantedAuthorities(String UserId) {
        // 如果dbsqlService不可用（sharding模式下），返回空集合
        if (dbsqlService == null) {
            return Collections.emptyList();
        }
        
        List<GrantedAuthority> authorities = new ArrayList<>();

        ImmutableMap<String, String> immutableMap = ImmutableMap.<String, String>builder().put("UserId", UserId).build();
        List<Map> jobsList = dbsqlService.getListBySqlID("mJobsListByUserId_Select", immutableMap);
        List<Map> menusList = dbsqlService.getListBySqlID("mMenusListByUserId_Select",immutableMap);
        
        //遍历所有岗位
        for (Map map : jobsList) {
            //遍历岗位对应菜单的逻辑已被注释掉，所以直接返回空集合
        }
        return authorities;
    }
    
    /**
     * 检查是否在sharding模式下
     */
    public boolean isInShardingMode() {
        return dbsqlService == null;
    }
}
