package cn.visolink.system.pubilcPool.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.pubilcPool.model.PublicPoolHisVO;
import cn.visolink.system.pubilcPool.model.PublicPoolVO;
import cn.visolink.system.pubilcPool.model.form.PublicPoolListSearch;
import cn.visolink.system.pubilcPool.model.form.RecoveryEdit;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/5/26 16:05
 **/
public interface PublicPoolService {

    PageInfo<PublicPoolVO> getPublicPoolList(PublicPoolListSearch paramMap);

    PageInfo<PublicPoolHisVO> getPublicPoolHisList(PublicPoolListSearch paramMap);

    void publicPoolExport(HttpServletRequest request, HttpServletResponse response, String param);

    void publicPoolHisExport(HttpServletRequest request, HttpServletResponse response, String param);

    ResultBody taoRecovery(RecoveryEdit params);

    ResultBody channelPoolRedistribution(RecoveryEdit params);

    ResultBody addTao(RecoveryEdit params);
}
