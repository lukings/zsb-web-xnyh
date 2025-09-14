package cn.visolink.system.commission.service;

import java.util.Map;

/**
 * <p>
 * CommissionDetail服务类
 * </p>
 *
 * @author autoJob
 * @since 2020-06-22
 */
public interface CommissionService {

    /**
     * 获取佣金列表
     *
     * @param param
     * @return
     */
    Map<String, Object> getCommissionList(Map<String, Object> param);

    /**
     * 获取无效佣金列表
     *
     * @param param
     * @return
     */
    Map<String, Object> getInvalidCommissionList(Map<String, Object> param);
}
