package cn.visolink.system.enterprisedatabase.dao;

import cn.visolink.message.model.OverdueUnconsumedProjectRecord;
import cn.visolink.system.enterprisedatabase.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author luqianqian
 * @Description: 企业数据库接口类
 * @date 2025/2/10 15:55
 */
@Mapper
@Repository
public interface EnterpriseDatabaseDao {
    /**
     * 查询通用字段配置列表
     * @param map
     * @return
     */
    List<EdCommonFieldConfigurationVo> selectEdCommonFieldConfigurationList(Map map);

    /**
     * 查询招商宝数据库字段查询
     * @param tableName
     * @return
     */
    List<Map> selectZsbTableFieldList(@Param("tableName") String tableName);

    /**
     * 查询通用字段配置详情
     * @param map
     * @return
     */
    EdCommonFieldConfigurationVo selectEdCommonFieldConfigurationDetail(EdCommonFieldConfigurationVo map);
    List<EdCommonFieldConfigurationVo> selectEdCommonFieldConfigurationDefaultValueList(EdCommonFieldConfigurationVo map);

    /**
     * 新增通用字段配置
     * @param map
     * @return
     */
    int insertEdCommonFieldConfiguration(EdCommonFieldConfigurationVo map);

    /**
     * 编辑通用字段配置
     * @param map
     * @return
     */
    int updateEdCommonFieldConfiguration(EdCommonFieldConfigurationVo map);

    /**
     * 删除通用字段配置
     * @param id
     * @return
     */
    int deleteEdCommonFieldConfigurationDefaultValueInfo(@Param("id") String id);

    /**
     * 查询导入模板列表头
     * @param map
     * @return
     * */
    List<EdCommonFieldConfigurationVo> selectEdImportTemplateConfigurationTableHeadList(Map map);

    /**
     * 查询导入模板列表
     * @param map
     * @return
     * */
    List<EdImportTemplateConfigurationVo> selectEdImportTemplateConfigurationList(EdImportTemplateConfigurationVo map);

    /**
     * 查询导入模板信息
     * @param id
     * @return
     * */
    List<EdImportTemplateConfigurationVo> selectEdImportTemplateConfigurationInfo(@Param("id") String id);

    /**
     * 查询导入模板详情
     * @param map
     * @return
     * */
    EdImportTemplateConfigurationVo selectEdImportTemplateConfigurationDetail(EdImportTemplateConfigurationVo map);
    Map selectEdImportTemplateConfigurationDetail2(EdImportTemplateConfigurationVo map);

    /**
     * 新增导入模板
     * @param map
     * @return
     * */
    int insertEdImportTemplateConfiguration(EdImportTemplateConfigurationVo map);

    /**
     * 编辑导入模板
     * @param map
     * @return
     * */
    int updateEdImportTemplateConfiguration(EdImportTemplateConfigurationVo map);

    /**
     * 编辑导入模板启用状态
     * @param map
     * @return
     * */
    int updateEdImportTemplateConfigurationStatus(EdImportTemplateConfigurationVo map);

    /**
     * 查询企业数据库列表ID集合
     * @param map
     * @return
     * */
    List<String> selectEdCustomerIdList(EdCustomerInfoVo map);

    /**
     * 查询企业数据库列表
     * @param ids
     * @return
     * */
    List<EdCustomerInfoVo> selectEdCustomerList(@Param("ids") List<String> ids);

    /**
     * 查询通用字段配置展示字段列表
     */
    List<String> selectEdCommonFieldConfigurationShowList();

    /**
     * 查询企业数据库详情
     * @param map
     * @return
     * */
    List<EdCustomerInfoVo> selectEdCustomerDetail(EdCustomerInfoVo map);

    /**
     * 查询企业数据库导入模板列表
     * @param map
     * @return
     * */
    List<Map> selectEdImportTemplateList(Map map);

    /**
     * 查询企业数据库历史导入记录
     * @param map
     * @return
     * */
    List<EdImportCustomerLogVo> selectEdImportCustomerLogList(EdImportCustomerLogVo map);

    /**
     * 查询企业数据库导入批次数据
     * @param map
     * @return
     * */
    List<EdCustomerInfoVo> selectEdImportCustomerHistorySuccessList(EdImportCustomerLogVo map);

    /**
     * 查询企业数据库过保数据
     * @param map
     * @return
     * */
    List<String> selectEdCustomerIdExpireList(Map map);

    /**
     * 查询企业数据库过保数据
     * @param mapListEdExpire
     * @return
     * */
    int updateEdCustomerExaminePreTypeInfo(@Param("list") List<String> mapListEdExpire);

    /**
     * 保存企业数据库临时数据表
     * @param edCustomerInfoTeList
     * @return
     * */
    int saveBacthEdImportCustomerTe(@Param("list") List<EdCustomerInfoTeVo> edCustomerInfoTeList);

    /**
     * 保存企业数据库导入日志表
     * @param edImportCustomerLogVo
     * @return
     * */
    int insertEdImportCustomerLog(EdImportCustomerLogVo edImportCustomerLogVo);

    /**
     * 企业数据库导入确认
     * @param map
     * @return
     * */
    int updateEdImportCustomerTeConfirm(Map map);

    /**
     * 查询企业数据库所有已确认的数据
     * @param map
     * @return
     * */
    List<EdCustomerInfoTeVo> getEdImportCustomerTeConfirm(Map map);

    /**
     * 查询企业数据库是否存在名称相同的数据
     * @param customerName
     * @return
     * */
    String selectEdCustomerIsRepeat(@Param("customerName") String customerName,@Param("commonFieldId") String commonFieldId);

    /**
     * 查询企业数据库数据的字段信息
     * @param customerId commonFieldConfigurationId
     * @return
     * */
    List<EdCustomerInfoVo> selectEdCustomerFieldInfo(@Param("customerId") String customerId, @Param("commonFieldConfigurationId") String commonFieldConfigurationId);

    /**
     * 删除企业数据库数据的字段信息
     * @param customerId commonFieldConfigurationId
     * @return
     * */
    int deleteEdCustomerIsRepeatField(@Param("customerId") String customerId, @Param("commonFieldConfigurationId") String commonFieldConfigurationId);

    /**
     * 保存企业数据库数据字段信息
     * @param edCustomerInfoTeVo
     * @return
     * */
    int saveEdCustomerInfo(EdCustomerInfoTeVo edCustomerInfoTeVo);

    /**
     * 保存企业数据库数据信息
     * @param value
     * @return
     * */
    int saveBatchEdCustomerInfo(@Param("list") List<EdCustomerInfoTeVo> value);

    /**
     * 更新企业数据库数据信息
     * @param edCustomerInfoTeVo
     * @return
     * */
    int updateEdCustomerInfo(EdCustomerInfoTeVo edCustomerInfoTeVo);

    /**
     * 保存企业数据库数据关联信息
     * @param map
     * @return
     * */
    int saveEdCustomerRelate(Map map);

    /**
     * 清空企业数据库临时数据
     * @param map
     * @return
     * */
    int deleteEdImportCustomerTeConfirm(Map map);

    /**
     * 删除企业数据库导入模板子信息
     * @param id
     * @return
     * */
    int deleteEdImportTemplateConfigurationCInfo(@Param("id") String id);

    /**
     * 企业数据库字典值导入
     * @param addList
     * @return
     * */
    int addDict(@Param("list") List<Map> addList);

    /**
     * 查询企业数据库所属行业
     * */
    List<Map> getEdIndustryOne();

    List<Map> getEdIndustryTwo(@Param("ids") List<String> ids);

    /**
     * 查询企业数据库临时数据表数据
     * */
    int getEdCustomerTeDistinctCustomerIdCount(Map map);
    List<String> getEdCustomerTeDistinctCustomerIdList(Map map);
    List<EdCustomerInfoTeVo> getEdCustomerTeList(@Param("ids") List<String> cid);
    List<EdCustomerInfoVo> getBatchEdCustomerTeList(@Param("batchCode") String batchCode);

    List<String> selectEdCustomerRelateIds(@Param("batchCode") String batchCode);

    List<EdImportTemplateConfigurationVo> selectEdCommonTemplateConfigurationInfo();

    List<Map> getAllocationAllCustomerUserOrgInfo(String userId);

    List<OverdueUnconsumedProjectRecord> getTimeoutWarning();

    void updateTimeoutWarning(String id);
}
