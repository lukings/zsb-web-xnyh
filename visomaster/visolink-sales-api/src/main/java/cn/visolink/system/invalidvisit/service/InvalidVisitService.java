package cn.visolink.system.invalidvisit.service;

import cn.visolink.system.invalidvisit.model.InvalidVisit;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/10 10:29
 **/
public interface InvalidVisitService {

    /**
     * @Author wanggang
     * @Description //查询无效客户列表
     * @Date 10:36 2020/3/10
     * @Param
     * @return
     **/
    Map findInvalidVisitList(Map map);

    /**
     * @Author wanggang
     * @Description //无效客户导出
     * @Date 10:36 2020/3/10
     * @Param
     * @return
     **/
    void invalidVisitExport(HttpServletRequest request, HttpServletResponse response, Map map);

    /**
     * @Author wanggang
     * @Description //获取来访原因
     * @Date 10:36 2020/3/10
     * @Param
     * @return
     **/
    List<Map> getVisitReason(String authCompanyId);
}
