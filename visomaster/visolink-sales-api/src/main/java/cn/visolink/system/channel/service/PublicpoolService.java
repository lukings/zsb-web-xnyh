package cn.visolink.system.channel.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.channel.model.Publicpool;
import cn.visolink.system.channel.model.form.CustomerDistributionRecordsForm;
import cn.visolink.system.channel.model.form.PublicpoolForm;
import cn.visolink.system.channel.model.form.RedistributionBatchForm;
import cn.visolink.system.channel.model.vo.CustomerDistributionRecordsVO;
import cn.visolink.system.channel.model.vo.PublicpoolVO;
import cn.visolink.system.channel.model.vo.RedistributionBatchVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Publicpool服务类
 * </p>
 *
 * @author autoJob
 * @since 2019-09-02
 */
public interface PublicpoolService extends IService<Publicpool> {


        /**
         * 查询公共池数据
         * @param publicpoolForm
         * @return
         */
        PageInfo<PublicpoolVO> selectAllPublic(PublicpoolForm publicpoolForm);

        /**
         * 重新分配原因
         * */
        List<Map> getClueResetCause(Map map);

    /**
     * @author wmy
     * @param request
     * @param response
     * @param publicpoolForm
     * 导出数据
     */
    void publicExport(HttpServletRequest request, HttpServletResponse response, PublicpoolForm publicpoolForm);
    String publicExportNew(HttpServletRequest request, HttpServletResponse response, PublicpoolForm publicpoolForm);

    /***
    *
     * @param publicpoolForm
    *@return {}
    *@throws
    *@Description: 查询全部公共池的数据
    *@author FuYong
    *@date 2020/9/9 17:25
    */
    List<PublicpoolVO> getAllPublicList(PublicpoolForm publicpoolForm);

    /**
     * @Author luqianqian
     * @Description //获取总监公海池授权
     * @Date 15:24 2023/09/13
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getFunctionObtainZs(Map map);
    /**
     * @Author luqianqian
     * @Description //总监公海池授权
     * @Date 15:24 2023/09/13
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody saveFunctionObtainZs(Map map);

    /**
     * @Author luqianqian
     * @Description //公客池捞取获取可选择的项目
     * @Date 15:24 2023/09/13
     * @Param [map]
     * @return cn.visolink.exception.ResultBody
     **/
    ResultBody getProjectListHasObtainCst(Map map);
}
