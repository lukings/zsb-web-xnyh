package cn.visolink.system.enterprisedatabase.service;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.enterprisedatabase.model.EdCommonFieldConfigurationVo;
import cn.visolink.system.enterprisedatabase.model.EdCustomerInfoVo;
import cn.visolink.system.enterprisedatabase.model.EdImportCustomerLogVo;
import cn.visolink.system.enterprisedatabase.model.EdImportTemplateConfigurationVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author luqianqian
 * @Description: 企业数据库服务类
 * @date 2025/2/10 15:52
 */
public interface EnterpriseDatabaseService {
    /**
     * @Author luqianqian
     * @Description //企业数据库-通用字段配置列表查询
     * @Date 16:41 2025/2/10
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdCommonFieldConfigurationList(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-招商宝数据库字段查询
     * @Date 16:50 2025/2/10
     * @Param tableName
     * @return ResultBody
     **/
    ResultBody selectZsbTableFieldList(String tableName);

    /**
     * @Author luqianqian
     * @Description //企业数据库-通用字段配置详情查询
     * @Date 16:51 2025/2/10
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdCommonFieldConfigurationDetail(EdCommonFieldConfigurationVo map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-通用字段配置编辑
     * @Date 16:51 2025/2/10
     * @Param map
     * @return ResultBody
     **/
    ResultBody addOrEditEdCommonFieldConfiguration(EdCommonFieldConfigurationVo map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-导入模板列表头查询
     * @Date 16:51 2025/2/10
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdImportTemplateConfigurationTableHeadList(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-导入模板列表查询
     * @Date 16:59 2025/2/10
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdImportTemplateConfigurationList(EdImportTemplateConfigurationVo map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-导入模板详情查询
     * @Date 17:01 2025/2/10
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdImportTemplateConfigurationDetail(EdImportTemplateConfigurationVo map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-导入模板编辑
     * @Date 17:01 2025/2/10
     * @Param map
     * @return ResultBody
     **/
    ResultBody addOrEditEdImportTemplateConfiguration(EdImportTemplateConfigurationVo map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-导入模板启用/禁用
     * @Date 17:01 2025/2/10
     * @Param map
     * @return ResultBody
     **/
    ResultBody updateEdImportTemplateConfigurationStatus(EdImportTemplateConfigurationVo map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-列表查询
     * @Date 10:21 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdCustomerList(EdCustomerInfoVo map,String type);

    /**
     * @Author luqianqian
     * @Description //企业数据库-企业数据详情查看字段配置
     * @Date 10:21 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdCustomerDetailFiledConfiguration(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-企业数据详情查看
     * @Date 10:21 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdCustomerDetail(EdCustomerInfoVo map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-批量导入模板查询
     * @Date 10:21 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdImportTemplateList(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-批量导入客户
     * @Date 10:21 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    ResultBody importEdCustomer(MultipartFile file, Map map) throws IOException;

    /**
     * @Author luqianqian
     * @Description //企业数据库-批量导入客户查询
     * @Date 10:21 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdImportCustomerList(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-批量导入客户确认
     * @Date 10:22 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    ResultBody importEdCustomerConfirm(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-批量导入客户定时
     * @Date 10:22 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    void importEdCustomerTiming();

    /**
     * @Author luqianqian
     * @Description //企业数据库-历史导入记录台账
     * @Date 10:22 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdImportTemplateHistoryList(EdImportCustomerLogVo map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-查询导入批次成功记录
     * @Date 10:22 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    ResultBody selectEdImportCustomerHistorySuccessList(EdImportCustomerLogVo map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-客户过保
     * @Date 10:22 2025/2/11
     * @Param map
     * @return ResultBody
     **/
    void edCustomerOverProtectTiming();

    /**
     * @Author luqianqian
     * @Description //企业数据库-生成导入模板
     * @Date 17:07 2025/03/05
     * @Param map
     * @return ResultBody
     **/
    void downloadEdImportTemplate(HttpServletRequest request, HttpServletResponse response, Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-分配客户(加入话单)
     * @Date 17:07 2025/03/08
     * @Param map
     * @return ResultBody
     **/
    ResultBody edCustomerJoinTheCallDistribution(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-导入行业字段
     * @Date 17:07 2025/03/08
     * @Param map
     * @return ResultBody
     **/
    ResultBody addDict(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-查询所属行业
     * @Date 17:07 2025/03/08
     * @Param map
     * @return ResultBody
     **/
    ResultBody getEdIndustryOne();

    /**
     * @Author luqianqian
     * @Description //企业数据库-企业数据库数据智能匹配接口
     * @Date 17:07 2025/03/08
     * @Param map
     * @return ResultBody
     **/
    ResultBody edCustomerDataMatch(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-企业数据库数据对接招商宝
     * @Date 17:07 2025/03/08
     * @Param map
     * @return ResultBody
     **/
    ResultBody edCustomerDataToZsb(Map map);

    /**
     * 获取登录人权限内可分配客户的项目
     * */
    ResultBody getGlAllocationPropertyConsultantPro(Map map);

    /**
     * @Author luqianqian
     * @Description //企业数据库-查询企业数据库信息
     * @Date 18:32 2025/03/25
     * @Param customerId
     * @return ResultBody
     **/
    ResultBody selectEdCustomerInfo(String customerId);

    ResultBody getTimeoutWarning();

    void updateTimeoutWarning(String id);
}
