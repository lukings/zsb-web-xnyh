package cn.visolink.system.visitandcallexcel.dao;

import cn.visolink.system.visitandcallexcel.model.ProjectProtectRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
public interface ProjectProtectRuleDao extends BaseMapper<ProjectProtectRule> {

    /**
     * 获取组织ID
     *
     * @return
     */
    String getOrgId(Map map);
}
