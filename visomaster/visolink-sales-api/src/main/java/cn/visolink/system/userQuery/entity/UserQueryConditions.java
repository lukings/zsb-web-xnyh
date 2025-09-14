package cn.visolink.system.userQuery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户查询条件实体类
 * @author system
 * @date 2025/1/27
 */
@Data
@TableName("b_user_query_conditions")
public class UserQueryConditions {
    
    /**
     * 主键UUID
     */
    @TableId(type = IdType.INPUT)
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 接口名称
     */
    private String interfaceName;
    
    /**
     * 查询参数JSON
     */
    private String queryParams;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
