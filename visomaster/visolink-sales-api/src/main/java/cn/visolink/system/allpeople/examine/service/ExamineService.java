package cn.visolink.system.allpeople.examine.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.allpeople.examine.model.ChannelRegistration;
import cn.visolink.system.allpeople.examine.model.Customer;
import cn.visolink.system.allpeople.examine.model.Examine;
import cn.visolink.system.allpeople.examine.model.UserEdit;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/1/14 19:08
 **/
public interface ExamineService {

    PageInfo<Examine> getExamineList(Map map);

    void updatePeople(Map map);

    List<Map> getCitys();

    Map getBrokerUserList(Map paramMap);

    Examine getBrokerUser(Map paramMap);

    PageInfo<Customer> getBrokerUserCustomer(Map paramMap);

    PageInfo<UserEdit> getBrokerUserEditLog(Map paramMap);

    List<Map> getProjectList(Map paramMap);

    List<Map> getAllProject();

    void brokerUserExport(HttpServletRequest request, HttpServletResponse response, String exportVo);

    String brokerUserExportNew(String exportVo);

    ResultBody channelRegistration(Map paramMap);

    ChannelRegistration channelDetail(Map paramMap);

    ResultBody channelManagement(Map paramMap);

    String addblacklist(String id,String type);

    String channelAudit(Map paramMap);

    void channelRegistrationExport(HttpServletRequest request, HttpServletResponse response, String exportVo);

    void channelManagementExport(HttpServletRequest request, HttpServletResponse response, String exportVo);
}
