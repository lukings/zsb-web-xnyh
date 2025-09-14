package cn.visolink.system.invalidvisit.dao;

import cn.visolink.system.invalidvisit.model.InvalidVisit;

import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/10 10:25
 **/
public interface InvalidVisitDao {

    /**
     * @Author wanggang
     * @Description //获取无效客户列表
     * @Date 10:27 2020/3/10
     * @Param
     * @return
     **/
    List<InvalidVisit> findInvalidVisitList(Map map);
    /**
     * @Author wanggang
     * @Description //获取组数及到访人数
     * @Date 11:11 2020/3/10
     * @Param [map]
     * @return java.util.Map
     **/
    Map getCount(Map map);

    /**
     * @Author wanggang
     * @Description //获取来访原因
     * @Date 11:11 2020/3/10
     * @Param [map]
     * @return java.util.Map
     **/
    List<Map> getVisitReason(String authCompanyId);
}
