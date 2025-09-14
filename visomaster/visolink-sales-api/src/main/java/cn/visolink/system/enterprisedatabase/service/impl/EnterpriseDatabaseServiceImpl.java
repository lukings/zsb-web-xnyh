package cn.visolink.system.enterprisedatabase.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.message.model.OverdueUnconsumedProjectRecord;
import cn.visolink.message.model.SysLog;
import cn.visolink.message.model.form.MessageForm;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.model.ProjectRuleDetail;
import cn.visolink.system.channel.model.vo.InformationVO;
import cn.visolink.system.enterprisedatabase.dao.EnterpriseDatabaseDao;
import cn.visolink.system.enterprisedatabase.model.*;
import cn.visolink.system.enterprisedatabase.service.EnterpriseDatabaseService;
import cn.visolink.utils.AverageDataUtil;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpHost;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.ScriptQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luqianqian
 * @Description: 企业数据库服务实现类
 * @date 2025/2/10 15:53
 */
@Service
@Slf4j
public class EnterpriseDatabaseServiceImpl implements EnterpriseDatabaseService {
    @Autowired
    private EnterpriseDatabaseDao enterpriseDatabaseDao;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ProjectCluesDao projectCluesDao;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat sfr = new SimpleDateFormat("yyyyMMddHHmmss");

    @Value("${ed.commonFieldId}")
    private String commonFieldId;
    @Value("${ed.industryFieldId}")
    private String industryFieldId;
    @Value("${ed.industryTwoFieldId}")
    private String industryTwoFieldId;
    @Value("${ed.industryThreeFieldId}")
    private String industryThreeFieldId;
    @Value("${ed.industryFourFieldId}")
    private String industryFourFieldId;
    @Value("${ed.provinceFieldId}")
    private String provinceFieldId;
    @Value("${ed.companyScaleFieldId}")
    private String companyScaleFieldId;
    @Value("${ed.establishYearsFieldId}")
    private String establishYearsFieldId;
    @Value("${ed.registrationStatusFieldId}")
    private String registrationStatusFieldId;
    @Value("${ed.registeredCapitalFieldId}")
    private String registeredCapitalFieldId;
    @Value("${ed.legalPersonFieldId}")
    private String legalPersonFieldId;
    @Value("${ed.companyTypeFieldId}")
    private String companyTypeFieldId;
    @Value("${ed.phoneFieldId}")
    private String phoneFieldId;
    @Value("${ed.phoneFieldId2}")
    private String phoneFieldId2;
    @Value("${ed.webAddressFieldId}")
    private String webAddressFieldId;
    @Value("${ed.emailFieldId}")
    private String emailFieldId;
    @Value("${ed.postCodeFieldId}")
    private String postCodeFieldId;
    @Value("${ed.insuredPersonFieldId}")
    private String insuredPersonFieldId;
    @Value("${ed.annualRevenueFieldId}")
    private String annualRevenueFieldId;
    @Value("${ed.staffSizeFieldId}")
    private String staffSizeFieldId;

    @Value("${outbound.addRobot}")
    private String addRobot;

    @Value("${outbound.addTheCall}")
    private String addTheCall;

    // ES 连接信息
    @Value("${elasticsearch.host}")
    private String ES_HOST;
    @Value("${elasticsearch.port}")
    private int ES_PORT;
    private static final String ES_SCHEME = "http";

    @Autowired
    private RedisService redisService;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResultBody selectEdCommonFieldConfigurationList(Map map) {
        //查询企业数据库通用字段配置表所有未删除的模板配置字段
        List<EdCommonFieldConfigurationVo> EdCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationList(map);
        //返回结果为集合 内容包含ID 字段名称 显示类型 对应招商宝字段 数据重复时处理方式 是否展示该字段 是否必填 是否为判重字段 是否为通讯字段 字段说明 状态
        return ResultBody.success(new PageInfo<>(EdCommonFieldConfigurationList));
    }

    @Override
    public ResultBody selectZsbTableFieldList(String tableName) {
        if (StringUtils.isEmpty(tableName)){
            return ResultBody.error(-10_000, "参数不能为空");
        }
        //通过数据库内置sql 查询数据库对应表的全部字段信息
        List<Map> list = enterpriseDatabaseDao.selectZsbTableFieldList(tableName);
        return ResultBody.success(list);
    }

    @Override
    public ResultBody selectEdCommonFieldConfigurationDetail(EdCommonFieldConfigurationVo map) {
        //进入接口判断ID是否为空 如果为空 提示ID不能为空
        if (StringUtils.isEmpty(map.getId())){
            return ResultBody.error(-10_000, "参数不能为空");
        }
        //如果不为空 根据ID查询企业数据库通用字段配置信息 内容包含ID 字段名称 字段编码 排序号 显示类型 对应招商宝字段 数据重复时处理方式 是否展示该字段 是否必填 是否为判重字段 是否为通讯字段 字段说明
        EdCommonFieldConfigurationVo EdCommonFieldConfigurationDetail = enterpriseDatabaseDao.selectEdCommonFieldConfigurationDetail(map);
        //根据ID查询PID等于ID的数据
        List<EdCommonFieldConfigurationVo> defaultValueList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationDefaultValueList(map);
        //封装进入每个数据内 对应的参数为默认值集合 用于前端展示默认值
        EdCommonFieldConfigurationDetail.setDefaultValueList(defaultValueList);
        return ResultBody.success(EdCommonFieldConfigurationDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addOrEditEdCommonFieldConfiguration(EdCommonFieldConfigurationVo map) {
        //进入接口判断参数是否有ID字段
        if (StringUtils.isEmpty(map.getId())){
            //若无 为新增逻辑
            String id = UUID.randomUUID().toString();
            map.setId(id);
            //保存参数进入企业数据库通用字段配置表 PID为-1 表示为父级数据
            map.setPid("-1");
            map.setCreator(SecurityUtils.getUserId());
            map.setCreateTime(DateUtil.date());
            enterpriseDatabaseDao.insertEdCommonFieldConfiguration(map);
            //判断是否有默认值集合 如果有保存默认值信息进入企业数据库通用字段配置表 PID为上面新增数据的ID
            if (map.getDefaultValueList() != null && map.getDefaultValueList().size() > 0){
                for (EdCommonFieldConfigurationVo edCommonFieldConfigurationVo : map.getDefaultValueList()) {
                    edCommonFieldConfigurationVo.setId(UUID.randomUUID().toString());
                    edCommonFieldConfigurationVo.setPid(id);
                    edCommonFieldConfigurationVo.setCreator(SecurityUtils.getUserId());
                    edCommonFieldConfigurationVo.setCreateTime(DateUtil.date());
                    enterpriseDatabaseDao.insertEdCommonFieldConfiguration(edCommonFieldConfigurationVo);
                }
            }
        }else {
            //若有 为编辑逻辑
            map.setUpdator(SecurityUtils.getUserId());
            map.setUpdateTime(DateUtil.date());
            //根据ID更新企业数据库通用字段配置表的字段信息
            enterpriseDatabaseDao.updateEdCommonFieldConfiguration(map);
            //判断是否有默认值集合 如果有 删除根据PID删除历史的默认值信息 保存新的默认值信息进入企业数据库通用字段配置表
            if (map.getDefaultValueList() != null && map.getDefaultValueList().size() > 0){
                //根据PID删除历史的默认值信息
                enterpriseDatabaseDao.deleteEdCommonFieldConfigurationDefaultValueInfo(map.getId());
                for (EdCommonFieldConfigurationVo edCommonFieldConfigurationVo : map.getDefaultValueList()) {
                    //保存新的默认值信息进入企业数据库通用字段配置表
                    edCommonFieldConfigurationVo.setId(UUID.randomUUID().toString());
                    edCommonFieldConfigurationVo.setPid(map.getId());
                    edCommonFieldConfigurationVo.setCreator(SecurityUtils.getUserId());
                    edCommonFieldConfigurationVo.setCreateTime(DateUtil.date());
                    enterpriseDatabaseDao.insertEdCommonFieldConfiguration(edCommonFieldConfigurationVo);
                }
            }
        }
        return ResultBody.success("保存成功");
    }

    @Override
    public ResultBody selectEdImportTemplateConfigurationTableHeadList(Map map) {
        //查询企业数据库通用字段配置表所有未删除 状态为启用的模板配置字段 内容包含字段名称 字段编码 前端代码配置
        List<EdCommonFieldConfigurationVo> EdCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdImportTemplateConfigurationTableHeadList(map);
        EdCommonFieldConfigurationList.stream().forEach(x->{
            x.setFieldCode(StringUtils.toCamelCase(x.getFieldCode()));
            //判断字段类型 如果是下拉 需要查询出来默认值
            if (x.getShowType().equals("4")){
                List<EdCommonFieldConfigurationVo> defaultValueList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationDefaultValueList(x);
                List<Map> defaultValueListMap = new ArrayList<>();
                defaultValueList.stream().forEach(y->{
                    Map map1 = new HashMap<>();
                    map1.put("value",y.getFieldCode());
                    map1.put("label",y.getFieldName());
                    defaultValueListMap.add(map1);
                });
                x.setDefaultValueListMap(defaultValueListMap);
            }
        });
        return ResultBody.success(EdCommonFieldConfigurationList);
    }

    @Override
    public ResultBody selectEdImportTemplateConfigurationList(EdImportTemplateConfigurationVo map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (StringUtils.isNotEmpty(map.getPageNum())){
            pageIndex = Integer.parseInt(map.getPageNum());
        }
        if (StringUtils.isNotEmpty(map.getPageSize())){
            pageSize = Integer.parseInt(map.getPageSize());
        }
        PageHelper.startPage(pageIndex,pageSize);
        //查询导入模板配置表的所有未删除的配置信息 内容包含ID PID 模板名称 数据来源 字段名称 字段编码
        List<EdImportTemplateConfigurationVo> edImportTemplateConfigurationList = enterpriseDatabaseDao.selectEdImportTemplateConfigurationList(map);
        //根据查询结果获取PID为-1的 这些数据为主信息 然后循环全部数据 根据主数据的ID和其他数据的PID 分别获取每个主数据下的子数据 然后将每一个子数据按照字段编码 字段名称一一对应放入到主数据集合中
        List<Map> reList = new ArrayList<>();
        for (EdImportTemplateConfigurationVo edImportTemplateConfigurationVo : edImportTemplateConfigurationList) {
            List<EdImportTemplateConfigurationVo> child = enterpriseDatabaseDao.selectEdImportTemplateConfigurationInfo(edImportTemplateConfigurationVo.getId());
            Map reMap = new HashMap();
            reMap.put("id",edImportTemplateConfigurationVo.getId());
            reMap.put("status",edImportTemplateConfigurationVo.getStatus());
            reMap.put("templateName",edImportTemplateConfigurationVo.getTemplateName());
            reMap.put("dataSourcesCode",edImportTemplateConfigurationVo.getDataSourcesCode());
            reMap.put("dataSourcesName",edImportTemplateConfigurationVo.getDataSourcesName());
            reMap.put("templateStartingLine",edImportTemplateConfigurationVo.getTemplateStartingLine());
            for (EdImportTemplateConfigurationVo c : child){
                reMap.put(c.getFieldCode(),c.getFieldValue());
            }
            reList.add(reMap);
        }
        //将处理完成的主数据 放到返回集合list中 响应给前端
        return ResultBody.success(new PageInfo<>(reList));
    }

    @Override
    public ResultBody selectEdImportTemplateConfigurationDetail(EdImportTemplateConfigurationVo map) {
        //根据列表数据的ID查询导入模板配置表的全部字段配置信息 响应给前端
        Map reMap = enterpriseDatabaseDao.selectEdImportTemplateConfigurationDetail2(map);
        //根据主信息ID 查询子信息 进行拼装
        List<EdImportTemplateConfigurationVo> child = enterpriseDatabaseDao.selectEdImportTemplateConfigurationInfo(map.getId());
        child.stream().forEach(x->{
            reMap.put(x.getCommonFieldConfigurationId(),x.getFieldValue());
        });
        return ResultBody.success(reMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addOrEditEdImportTemplateConfiguration(EdImportTemplateConfigurationVo map) {
        //进入接口判断ID是否为空 如果为空 为新增逻辑
        if (StringUtils.isEmpty(map.getId())){
            //进入接口判断数据来源和模板名称是否为空 如果为空 提示数据来源或者模板名称不能为空
            if (StringUtils.isEmpty(map.getDataSourcesCode()) || StringUtils.isEmpty(map.getTemplateName())){
                return ResultBody.error(400,"数据来源或者模板名称不能为空");
            }
            //保存数据来源 模板名称 进入导入模板配置表 这一条为主数据 PID为-1
            String id = UUID.randomUUID().toString();
            map.setId(id);
            map.setPid("-1");
            map.setCreator(SecurityUtils.getUserId());
            map.setCreateTime(DateUtil.date());
            enterpriseDatabaseDao.insertEdImportTemplateConfiguration(map);
            //保存数据来源 模板名称 字段名称 字段编码 字段名称对应配置ID 进入导入模板配置表 对应PID为主数据的ID
            for (EdImportTemplateConfigurationVo edImportTemplateConfigurationVo : map.getChildren()) {
                edImportTemplateConfigurationVo.setId(UUID.randomUUID().toString());
                edImportTemplateConfigurationVo.setPid(map.getId());
                edImportTemplateConfigurationVo.setTemplateName(map.getTemplateName());
                edImportTemplateConfigurationVo.setDataSourcesCode(map.getDataSourcesCode());
                edImportTemplateConfigurationVo.setDataSourcesName(map.getDataSourcesName());
                edImportTemplateConfigurationVo.setTemplateStartingLine(map.getTemplateStartingLine());
                edImportTemplateConfigurationVo.setCreator(SecurityUtils.getUserId());
                edImportTemplateConfigurationVo.setCreateTime(DateUtil.date());
                enterpriseDatabaseDao.insertEdImportTemplateConfiguration(edImportTemplateConfigurationVo);
            }
        }else {//如果不为空 为编辑逻辑
            //进入接口判断数据来源和模板名称是否为空 如果为空 提示数据来源或者模板名称不能为空
            if (StringUtils.isEmpty(map.getDataSourcesCode()) || StringUtils.isEmpty(map.getTemplateName())){
                return ResultBody.error(400,"数据来源或者模板名称不能为空");
            }
            //如果不为空 根据ID更新 主数据的数据来源 模板名称信息
            map.setUpdator(SecurityUtils.getUserId());
            map.setUpdateTime(DateUtil.date());
            enterpriseDatabaseDao.updateEdImportTemplateConfiguration(map);
            //删除历史的数据 重新保存新的子信息
            enterpriseDatabaseDao.deleteEdImportTemplateConfigurationCInfo(map.getId());
            //根据PID等于传入的参数ID和字段编码 字段名称对应配置ID 判断改模板下字段是否存在 如果存在 更新字段名称 如果不存在新增进入导入模板配置表
            for (EdImportTemplateConfigurationVo edImportTemplateConfigurationVo : map.getChildren()) {
                edImportTemplateConfigurationVo.setId(UUID.randomUUID().toString());
                edImportTemplateConfigurationVo.setPid(map.getId());
                edImportTemplateConfigurationVo.setTemplateName(map.getTemplateName());
                edImportTemplateConfigurationVo.setDataSourcesCode(map.getDataSourcesCode());
                edImportTemplateConfigurationVo.setDataSourcesName(map.getDataSourcesName());
                edImportTemplateConfigurationVo.setTemplateStartingLine(map.getTemplateStartingLine());
                edImportTemplateConfigurationVo.setCreator(SecurityUtils.getUserId());
                edImportTemplateConfigurationVo.setCreateTime(DateUtil.date());
                enterpriseDatabaseDao.insertEdImportTemplateConfiguration(edImportTemplateConfigurationVo);
//                if (enterpriseDatabaseDao.selectEdImportTemplateConfigurationDetail(edImportTemplateConfigurationVo) != null){
//                    //如果存在 更新字段名称
//                    edImportTemplateConfigurationVo.setTemplateName(map.getTemplateName());
//                    edImportTemplateConfigurationVo.setDataSourcesCode(map.getDataSourcesCode());
//                    edImportTemplateConfigurationVo.setDataSourcesName(map.getDataSourcesName());
//                    edImportTemplateConfigurationVo.setTemplateStartingLine(map.getTemplateStartingLine());
//                    edImportTemplateConfigurationVo.setUpdator(SecurityUtils.getUserId());
//                    edImportTemplateConfigurationVo.setUpdateTime(DateUtil.date());
//                    enterpriseDatabaseDao.updateEdImportTemplateConfiguration(edImportTemplateConfigurationVo);
//                }else {
//                    //如果不存在新增进入导入模板配置表
//                    edImportTemplateConfigurationVo.setId(UUID.randomUUID().toString());
//                    edImportTemplateConfigurationVo.setPid(map.getId());
//                    edImportTemplateConfigurationVo.setTemplateName(map.getTemplateName());
//                    edImportTemplateConfigurationVo.setDataSourcesCode(map.getDataSourcesCode());
//                    edImportTemplateConfigurationVo.setDataSourcesName(map.getDataSourcesName());
//                    edImportTemplateConfigurationVo.setTemplateStartingLine(map.getTemplateStartingLine());
//                    edImportTemplateConfigurationVo.setCreator(SecurityUtils.getUserId());
//                    edImportTemplateConfigurationVo.setCreateTime(DateUtil.date());
//                    enterpriseDatabaseDao.insertEdImportTemplateConfiguration(edImportTemplateConfigurationVo);
//                }
            }
        }
        return ResultBody.success("保存成功");
    }

    @Override
    public ResultBody updateEdImportTemplateConfigurationStatus(EdImportTemplateConfigurationVo map) {
        //进入接口判断ID是否为空 如果为空 提示ID不能为空
        if (StringUtils.isEmpty(map.getId())){
            return ResultBody.error(500,"ID不能为空");
        }
        //如果不为空 根据ID更新主数据的状态和PID等于参数ID的子数据的状态
        enterpriseDatabaseDao.updateEdImportTemplateConfigurationStatus(map);
        return ResultBody.success("保存成功");
    }

    @Override
    public ResultBody selectEdCustomerList(EdCustomerInfoVo map,String type) {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            int pageIndex = 1;
            int pageSize = 10;
            if (StringUtils.isNotEmpty(map.getPageNum())){
                pageIndex = Integer.parseInt(map.getPageNum());
            }
            if (StringUtils.isNotEmpty(map.getPageSize())){
                pageSize = Integer.parseInt(map.getPageSize());
            }
//            PageHelper.startPage(pageIndex,pageSize);
            String searchIndexName = "ed_customer_info_search";
            //查询企业数据库数据查看权限为私密 且当前登录人包含在导入人集合中的数据和企业数据库数据查看权限为公开的数据 按照查询条件筛选符合条件的数据 然后把数据ID 去重 按照这个查询结果分页获取符合条件的数据
//            List<String> customerIds = enterpriseDatabaseDao.selectEdCustomerIdList(map);
            //ES 条件查询
            long total = 0;
            List<String> customerIds = new ArrayList<>();
            SearchRequest searchRequest = new SearchRequest(searchIndexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            //分页
            sourceBuilder.from((pageIndex - 1) * pageSize);
            sourceBuilder.size(pageSize);
            //查询条件
            BoolQueryBuilder boolQuery = this.searchToEsQuery(map.getKeyWord(),type);
            // 处理 examine_pre_type 条件
            BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
            orQuery.should(QueryBuilders.termQuery("examine_pre_type", 1));
            BoolQueryBuilder andSubQuery = QueryBuilders.boolQuery();
            andSubQuery.must(QueryBuilders.termQuery("examine_pre_type", 0));
            // 模拟 FIND_IN_SET
            andSubQuery.must(QueryBuilders.wildcardQuery("import_user.keyword",map.getImportUser()));
            orQuery.should(andSubQuery);
            boolQuery.must(orQuery);
            sourceBuilder.query(boolQuery);
            // 按 create_time 降序排序
            sourceBuilder.sort("create_time", SortOrder.DESC);
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            // 获取搜索结果
            SearchHits hits = searchResponse.getHits();
            total = hits.getTotalHits().value;
            for (SearchHit hit : hits) {
                String customerId = hit.getSourceAsMap().get("customer_id").toString();
                customerIds.add(customerId);
            }
            if (CollectionUtils.isEmpty(customerIds)){
                return ResultBody.success(new PageInfo<>());
            }
            //根据上述数据ID的集合 查询企业数据库表 获取符合条件的数据
//            List<EdCustomerInfoVo> edCustomerInfoList = enterpriseDatabaseDao.selectEdCustomerList(customerIds);
            //ES 条件查询
            List<EdCustomerInfoVo> edCustomerInfoList = new ArrayList<>();
            // 创建搜索请求并指定索引
            String indexName = "ed_customer_info";
            SearchRequest searchRequest1 = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder1 = new SearchSourceBuilder();
            sourceBuilder1.size(10000);

            // 构建布尔查询
            BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();

            // 添加 customer_id 在指定列表中的条件
            if (!customerIds.isEmpty()) {
                // 统一大小写
                List<String> lowerCaseCustomerIds = customerIds.stream().map(String::toLowerCase).collect(Collectors.toList());
                // 使用 keyword 子字段进行查询
                boolQuery1.must(QueryBuilders.termsQuery("customer_id.keyword", lowerCaseCustomerIds));
            }

            // 设置查询条件
            sourceBuilder1.query(boolQuery1);

            // 设置搜索源构建器到搜索请求
            searchRequest1.source(sourceBuilder1);

            // 执行搜索请求
            SearchResponse searchResponse1 = client.search(searchRequest1, RequestOptions.DEFAULT);
            SearchHits hits1 = searchResponse1.getHits();

            // 遍历搜索结果并打印所需字段
            for (SearchHit hit : hits1) {
                System.out.println("查询到文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
                EdCustomerInfoVo edCustomerInfoVo = JSONObject.parseObject(hit.getSourceAsString(), EdCustomerInfoVo.class);
                edCustomerInfoList.add(edCustomerInfoVo);
            }

            //获取通用字段的配置 并按照通用字段配置ID分组获取通用字段配置信息
            List<EdCommonFieldConfigurationVo> edCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationList(new HashMap<>());
            Map<String, EdCommonFieldConfigurationVo> edCommonFieldConfigurationMap = edCommonFieldConfigurationList.stream().collect(Collectors.toMap(EdCommonFieldConfigurationVo::getId, edCommonFieldConfigurationVo -> edCommonFieldConfigurationVo));
            //先按照数据ID分组 获取每个数据的完整数据集合
            Map<String, List<EdCustomerInfoVo>> edCustomerInfoMap = edCustomerInfoList.stream().collect(Collectors.groupingBy(EdCustomerInfoVo::getCustomerId));
            //循环数据集合拼接每一个展示数据
            List<Map> reList = new ArrayList<>();
            edCustomerInfoMap.forEach((key, value) -> {
                List<EdCustomerInfoVo> edCustomerInfoVoList = edCustomerInfoMap.get(key);
                final String[] companyName = {""};
                final String[] phone = {""};
                final String[] phoneToCall = {""};
                final String[] legalPerson = {""};
                final String[] registeredCapital = {""};
                final String[] establishmentDate = {""};
                final String[] registrationStatus = {""};
                List<String> tag = new ArrayList<>();
                List<Map> details = new ArrayList<>();
                Map reMap = new HashMap();
                //将数据集合的数据按照通用字段ID 进行分组 如果ID相同 则将fieldValue值拼接起来 作为一个数据
                Map<String, List<EdCustomerInfoVo>> edCustomerInfoVoMap = edCustomerInfoVoList.stream().collect(Collectors.groupingBy(EdCustomerInfoVo::getCommonFieldConfigurationId));
                edCustomerInfoVoMap.forEach((k, v) -> {
                    List<EdCustomerInfoVo> fieldValueList = edCustomerInfoVoMap.get(k);
                    String fieldValue = "";
                    String fieldValue2 = "";
                    if (CollectionUtils.isNotEmpty(fieldValueList)){
                        if (k.equals(phoneFieldId) || k.equals(phoneFieldId2)){
                            //电话 手机 需要在每一个字段值后拼接自己的数据来源信息 数据来源使用（）包起来
                            fieldValue = fieldValueList.stream().map(edCustomerInfoVo -> edCustomerInfoVo.getFieldValue() + "(" + edCustomerInfoVo.getDataSourcesName() + ")").collect(Collectors.joining(";"));
                            fieldValue2 = fieldValueList.stream().map(EdCustomerInfoVo::getFieldValue).collect(Collectors.joining(";"));
                        }else {
                            fieldValue = fieldValueList.stream().map(EdCustomerInfoVo::getFieldValue).collect(Collectors.joining(";"));
                        }
                    }
                    if (k.equals(commonFieldId)){
                        companyName[0] = fieldValueList.get(0).getFieldValue();
                    }else if (k.equals(registrationStatusFieldId)){
                        registrationStatus[0] = fieldValue;
                    }else if (k.equals(companyScaleFieldId) || k.equals(companyTypeFieldId)){
                        tag.add(fieldValue);
                    }else if (k.equals(legalPersonFieldId) || k.equals(registeredCapitalFieldId)
                    || k.equals(establishYearsFieldId) || k.equals(phoneFieldId) || k.equals(phoneFieldId2)
                    || k.equals(emailFieldId) || k.equals(webAddressFieldId) || k.equals(provinceFieldId)){
                        if (k.equals(establishYearsFieldId)){
                            if (fieldValue.contains("-")){
                                try {
                                    establishmentDate[0] = sd.format(sd.parse(fieldValue));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                establishmentDate[0] = sd.format(convertExcelDateToJavaDate(Long.parseLong(fieldValue)));;
                            }
                            Map dMap = new HashMap();
                            dMap.put("i",edCommonFieldConfigurationMap.get(k).getListIndex());
                            dMap.put("fieldValue",fieldValueList.get(0).getFieldName()+"："+establishmentDate[0]);
                            details.add(dMap);
                        }else {
                            Map dMap = new HashMap();
                            dMap.put("i",edCommonFieldConfigurationMap.get(k).getListIndex());
                            dMap.put("fieldValue",fieldValueList.get(0).getFieldName()+"："+fieldValue);
                            details.add(dMap);
                        }

                        if ("1".equals(edCommonFieldConfigurationMap.get(k).getIsCommunicationField())){
                            if (StringUtils.isEmpty(phone[0])){
                                phone[0] = fieldValue;
                                phoneToCall[0] = fieldValue2;
                            }else {
                                phone[0] = phone[0]+";"+fieldValue;
                                phoneToCall[0] = phoneToCall[0]+";"+fieldValue2;
                            }
                        }
                    }
                    if (k.equals(registeredCapitalFieldId)){
                        registeredCapital[0] = fieldValue;
                    }
                    if (k.equals(legalPersonFieldId)){
                        legalPerson[0] = fieldValue;
                    }

                });
                reMap.put("checked", false);
                reMap.put("customerId", key);
                reMap.put("companyName", companyName[0]);
                reMap.put("registrationStatus",registrationStatus[0]);
                reMap.put("tag",tag);
                reMap.put("phone",phone[0]);
                reMap.put("phoneToCall",phoneToCall[0]);
                //details 增加排序 按照listIndex 排序展示 同时转化fieldValue到新集合内
                List<String> detailss = details.stream().sorted(Comparator.comparing(maps -> (Integer) maps.get("i"))).map(maps -> String.valueOf(maps.get("fieldValue"))).collect(Collectors.toList());
                reMap.put("details",detailss);
                reMap.put("legalPerson",legalPerson[0]);
                reMap.put("registeredCapital",registeredCapital[0]);
                reMap.put("establishmentDate",establishmentDate[0]);
                reList.add(reMap);
            });
            Map reMap = new HashMap();
            reMap.put("list",reList);
            reMap.put("total",total);
            return ResultBody.success(reMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBody.success(new PageInfo<>());
    }

    // 搜索条件转ES查询条件
    private BoolQueryBuilder searchToEsQuery(Map keyWord,String type) {
        //termQuery 精确匹配查询 适用于精确匹配的场景
        //matchQuery 全文本查询，会对查询词进行分词处理 适用于模糊匹配的场景
        //matchPhraseQuery 全文本查询，要求查询完整的查询短语 适用于需要精确匹配短语的场景
        //matchPhrasePrefixQuery 全文本查询，要求查询完整的查询短语 但允许最后一个分词以查询词的前缀形式出现 适用于需要模糊匹配短语前缀的场景
        //matchBoolPrefixQuery 全文本查询，会对查询词进行分词处理 对于除最后一个分词之外的其他分词，使用 matchQuery 进行匹配；对于最后一个分词，使用前缀匹配 适用于需要对部分分词进行精确匹配，对最后一个分词进行前缀匹配的场景
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //将查询条件转换成es代码
        String keyword = String.valueOf(keyWord.get("keyword"));//关键词 公司名称
        String keywordScope = String.valueOf(keyWord.get("keywordScope"));//关键词搜索范围 0 模糊匹配 1 精准匹配
        if (StringUtils.isNotEmpty(keyword)){
            if ("0".equals(keywordScope)){
                boolQuery.must(QueryBuilders.matchPhraseQuery(commonFieldId, keyword));
            }else {
                boolQuery.must(QueryBuilders.matchQuery(commonFieldId+".keyword", keyword));
            }
        }
        String industry = String.valueOf(keyWord.get("industry"));//所属行业 行业大类/行业中类/行业小类/行业微类。。。
        if (StringUtils.isNotEmpty(industry)){
            if ("PC".equals(type)){
                List<List<String>> industryList = (List<List<String>>) keyWord.get("industry");
                if (CollectionUtils.isNotEmpty(industryList)){
                    BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
                    BoolQueryBuilder orOneQuery = QueryBuilders.boolQuery();
                    Boolean flag = false;
                    BoolQueryBuilder orTwoQuery = QueryBuilders.boolQuery();
                    Boolean flagTwo = false;
                    BoolQueryBuilder orThreeQuery = QueryBuilders.boolQuery();
                    Boolean flagThree = false;
                    BoolQueryBuilder orFourQuery = QueryBuilders.boolQuery();
                    Boolean flagFour = false;
                    for (List<String> list : industryList) {
                        for (int i = 0; i < list.size(); i++){
                            if (i == 0){
                                orOneQuery.should(QueryBuilders.matchPhraseQuery(industryFieldId, list.get(i)));
                                if (!flag){
                                    flag = true;
                                }
                            }else if (i == 1){
                                orTwoQuery.should(QueryBuilders.matchPhraseQuery(industryTwoFieldId, list.get(i)));
                                if (!flagTwo){
                                    flagTwo = true;
                                }
                            }else if (i == 2){
                                orThreeQuery.should(QueryBuilders.matchPhraseQuery(industryThreeFieldId, list.get(i)));
                                if (!flagThree){
                                    flagThree = true;
                                }
                            }else if (i == 3){
                                orFourQuery.should(QueryBuilders.matchPhraseQuery(industryFourFieldId, list.get(i)));
                                if (!flagFour){
                                    flagFour = true;
                                }
                            }
                        }
                    }
                    if (flag){
                        orQuery.should(orOneQuery);
                    }
                    if (flagTwo){
                        orQuery.should(orTwoQuery);
                    }
                    if (flagThree){
                        orQuery.should(orThreeQuery);
                    }
                    if (flagFour){
                        orQuery.should(orFourQuery);
                    }
                    boolQuery.must(orQuery);
                }
            }else {
                List<String> industryList = Arrays.asList(industry.split("/"));
                if (CollectionUtils.isNotEmpty(industryList)){
                    BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
                    BoolQueryBuilder orOneQuery = QueryBuilders.boolQuery();
                    Boolean flag = false;
                    BoolQueryBuilder orTwoQuery = QueryBuilders.boolQuery();
                    Boolean flagTwo = false;
                    BoolQueryBuilder orThreeQuery = QueryBuilders.boolQuery();
                    Boolean flagThree = false;
                    BoolQueryBuilder orFourQuery = QueryBuilders.boolQuery();
                    Boolean flagFour = false;
                    for (int i = 0; i < industryList.size(); i++){
                        if (i == 0){
                            orOneQuery.should(QueryBuilders.matchPhraseQuery(industryFieldId, industryList.get(i)));
                            if (!flag){
                                flag = true;
                            }
                        }else if (i == 1){
                            orTwoQuery.should(QueryBuilders.matchPhraseQuery(industryTwoFieldId, industryList.get(i)));
                            if (!flagTwo){
                                flagTwo = true;
                            }
                        }else if (i == 2){
                            orThreeQuery.should(QueryBuilders.matchPhraseQuery(industryThreeFieldId, industryList.get(i)));
                            if (!flagThree){
                                flagThree = true;
                            }
                        }else if (i == 3){
                            orFourQuery.should(QueryBuilders.matchPhraseQuery(industryFourFieldId, industryList.get(i)));
                            if (!flagFour){
                                flagFour = true;
                            }
                        }
                    }
                    if (flag){
                        orQuery.should(orOneQuery);
                    }
                    if (flagTwo){
                        orQuery.should(orTwoQuery);
                    }
                    if (flagThree){
                        orQuery.should(orThreeQuery);
                    }
                    if (flagFour){
                        orQuery.should(orFourQuery);
                    }
                    boolQuery.must(orQuery);
                }
            }
        }
        String province = String.valueOf(keyWord.get("province"));//省份地区 上海市宝山区。。。
        if (StringUtils.isNotEmpty(province)){
            if ("PC".equals(type)){
//                List<List<String>> provinceList = (List<List<String>>) keyWord.get("province");
//                if (CollectionUtils.isNotEmpty(provinceList)){
//                    BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
//                    for (List<String> list : provinceList) {
//                        //将map里的值拼接到一起 作为一个地址 例如 map里 第一个值是上海市 第二个值市宝山区 那么地址就是 上海市宝山区
//                        String value = list.stream().collect(Collectors.joining());
//                        orQuery.should(QueryBuilders.matchPhrasePrefixQuery(provinceFieldId, value));
//                    }
//                    boolQuery.must(orQuery);
//                }
                List<String> areaList = new ArrayList<>();
                List<String> provinceList = (List<String>) keyWord.get("provinceArray");
                List<String> cityList = (List<String>) keyWord.get("cityArray");
                List<String> countList = (List<String>) keyWord.get("countArray");
                if (!org.springframework.util.CollectionUtils.isEmpty(provinceList)) {
                    if (cityList == null)
                        cityList = new ArrayList<>();
                    if (provinceList.contains("北京市"))
                        cityList.add("北京市");
                    if (provinceList.contains("天津市"))
                        cityList.add("天津市");
                    if (provinceList.contains("上海市"))
                        cityList.add("上海市");
                    if (provinceList.contains("重庆市"))
                        cityList.add("重庆市");
                    areaList = provinceList;
                }
                if (!org.springframework.util.CollectionUtils.isEmpty(cityList)) {
                    areaList = cityList;
                }
                if (!org.springframework.util.CollectionUtils.isEmpty(countList)) {
                    areaList = countList;
                }
                // 循环areaList 循环每个值 按照每个值匹配地址 多个使用or
                if (CollectionUtils.isNotEmpty(areaList)){
                    BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
                    for (String area : areaList) {
                        orQuery.should(QueryBuilders.matchPhrasePrefixQuery(provinceFieldId, area));
                    }
                    boolQuery.must(orQuery);
                }
            }else {
                boolQuery.must(QueryBuilders.matchPhrasePrefixQuery(provinceFieldId, province.replace("/","")));
            }
        }
        if ("PC".equals(type)){
            String companyScale = String.valueOf(keyWord.get("companyScale"));//企业规模 large 大型 medium 中等 small 小型 micro 微型
            if (StringUtils.isNotEmpty(companyScale)){
                String companyScaleValue = "";
                if ("large".equals(companyScale)){
                    companyScaleValue = "大型";
                }else if ("medium".equals(companyScale)){
                    companyScaleValue = "中型";
                }else if ("small".equals(companyScale)){
                    companyScaleValue = "小型";
                }else if ("micro".equals(companyScale)){
                    companyScaleValue = "微型";
                }
                boolQuery.must(QueryBuilders.matchPhraseQuery(companyScaleFieldId, companyScaleValue));
            }
        }else {
            List<String> companyScaleList = (List<String>) keyWord.get("companyScale");//企业规模 large 大型 medium 中等 small 小型 micro 微型
            if (CollectionUtils.isNotEmpty(companyScaleList)){
                BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
                for (String companyScale : companyScaleList){
                    if ("large".equals(companyScale)){
                        orQuery.should(QueryBuilders.matchPhraseQuery(companyScaleFieldId, "大型"));
                    }else if ("medium".equals(companyScale)){
                        orQuery.should(QueryBuilders.matchPhraseQuery(companyScaleFieldId, "中型"));
                    }else if ("small".equals(companyScale)){
                        orQuery.should(QueryBuilders.matchPhraseQuery(companyScaleFieldId, "小型"));
                    }else if ("micro".equals(companyScale)){
                        orQuery.should(QueryBuilders.matchPhraseQuery(companyScaleFieldId, "微型"));
                    }
                }
                boolQuery.must(orQuery);
            }
        }
        List<String> establishYearsList = (List<String>) keyWord.get("establishYears");//成立年限 within3Months 3个月内 within6Months 半年内 within1Year 1年内 1To3Years 1-3年 3To5Years 3-5年 5To10Years 5-10年 moreThan10Years 10年以上
        if (CollectionUtils.isNotEmpty(establishYearsList)){
            BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
            for (String establishYear : establishYearsList){
                //多选条件 需要使用es 进行查询条件拼接
                //根据字段值 转换日期 进行范围比较
                Date startDate = null;//开始时间
                Date endDate = null;//结束
                if ("within3Months".equals(establishYear)){
                    Date date = new Date();
                    startDate = DateUtils.addMonths(date, -3);
                    endDate = date;
                }else if ("within6Months".equals(establishYear)){
                    Date date = new Date();
                    startDate = DateUtils.addMonths(date, -6);
                    endDate = date;
                }else if ("within1Year".equals(establishYear)){
                    Date date = new Date();
                    startDate = DateUtils.addMonths(date, -12);
                    endDate = date;
                }else if ("1To3Years".equals(establishYear)){
                    Date date = new Date();
                    startDate = DateUtils.addMonths(date, -36);
                    endDate = DateUtils.addMonths(date, -12);
                }else if ("3To5Years".equals(establishYear)){
                    Date date = new Date();
                    startDate = DateUtils.addMonths(date, -60);
                    endDate = DateUtils.addMonths(date, -36);
                }else if ("5To10Years".equals(establishYear)){
                    Date date = new Date();
                    startDate = DateUtils.addMonths(date, -120);
                    endDate = DateUtils.addMonths(date, -60);
                }else if ("moreThan10Years".equals(establishYear)){
                    Date date = new Date();
                    endDate = DateUtils.addMonths(date, -120);
                }else {
                    //自定义 自定义时间区间 将前端传过来的日期进行转换用于es查询
                    String[] establishYearsArr = establishYear.split(",");
                    try {
                        if (StringUtils.isNotEmpty(establishYearsArr[0])){
                            startDate = DateUtils.parseDate(establishYearsArr[0], "yyyy-MM-dd");
                        }
                        if (StringUtils.isNotEmpty(establishYearsArr[1])){
                            endDate = DateUtils.parseDate(establishYearsArr[1], "yyyy-MM-dd");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if (startDate != null && endDate != null){
                    orQuery.should(QueryBuilders.rangeQuery(establishYearsFieldId).gte(sf.format(startDate)).lte(sf.format(endDate)));
                }else if (startDate == null && endDate != null){
                    orQuery.should(QueryBuilders.rangeQuery(establishYearsFieldId).lte(sf.format(endDate)));
                }else if (startDate != null && endDate == null){
                    orQuery.should(QueryBuilders.rangeQuery(establishYearsFieldId).gte(sf.format(startDate)));
                }
            }
            boolQuery.must(orQuery);
        }
        List<String> registrationStatusList = (List<String>) keyWord.get("registrationStatus");//登记状态 active 存续/在业 movedIn 迁入 movedOut 迁出 canceled 注销 revoked 吊销 withdrawn 撤销 liquidating 清算 suspended 停业 closed 已歇业 orderedToClose 责令关闭 dissolved 解散
        if (CollectionUtils.isNotEmpty(registrationStatusList)){
            BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
            for (String registrationStatus : registrationStatusList){
                String registrationStatusValue = "";
                if ("active".equals(registrationStatus)){
                    registrationStatusValue = "存续/在业";
                }else if ("movedIn".equals(registrationStatus)){
                    registrationStatusValue = "迁入";
                }else if ("movedOut".equals(registrationStatus)){
                    registrationStatusValue = "迁出";
                }else if ("canceled".equals(registrationStatus)){
                    registrationStatusValue = "注销";
                }else if ("revoked".equals(registrationStatus)){
                    registrationStatusValue = "吊销";
                }else if ("withdrawn".equals(registrationStatus)){
                    registrationStatusValue = "撤销";
                }else if ("liquidating".equals(registrationStatus)){
                    registrationStatusValue = "清算";
                }else if ("suspended".equals(registrationStatus)){
                    registrationStatusValue = "停业";
                }else if ("closed".equals(registrationStatus)){
                    registrationStatusValue = "已歇业";
                }else if ("orderedToClose".equals(registrationStatus)){
                    registrationStatusValue = "责令关闭";
                }else if ("dissolved".equals(registrationStatus)){
                    registrationStatusValue = "解散";
                }
                orQuery.should(QueryBuilders.matchQuery(registrationStatusFieldId, registrationStatusValue));
            }
            boolQuery.must(orQuery);
        }
        List<String> registeredCapitalList = (List<String>) keyWord.get("registeredCapital");//注册资本 0To100 0-100万 100To200 100-200万 200To500 200-500万 500To1000 500-1000万 1000To5000 1000-5000万 moreThan5000 5000万以上
        if (CollectionUtils.isNotEmpty(registeredCapitalList)){
            BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
            for (String registeredCapital : registeredCapitalList){
                String beginRegisteredCapitalValue = "";
                String endRegisteredCapitalValue = "";
                if ("0To100".equals(registeredCapital)){
                    beginRegisteredCapitalValue = "0";
                    endRegisteredCapitalValue = "100";
                }else if ("100To200".equals(registeredCapital)){
                    beginRegisteredCapitalValue = "100";
                    endRegisteredCapitalValue = "200";
                }else if ("200To500".equals(registeredCapital)){
                    beginRegisteredCapitalValue = "200";
                    endRegisteredCapitalValue= "500";
                }else if ("500To1000".equals(registeredCapital)){
                    beginRegisteredCapitalValue = "500";
                    endRegisteredCapitalValue = "1000";
                }else if ("1000To5000".equals(registeredCapital)){
                    beginRegisteredCapitalValue = "1000";
                    endRegisteredCapitalValue = "5000";
                }else if ("moreThan5000".equals(registeredCapital)){
                    beginRegisteredCapitalValue = "5000";
                }else {
                    //自定义 自定义金额范围 将前端传过来的金额用于es查询
                    String[] registeredCapitalArr = registeredCapital.split(",");
                    try {
                        if (StringUtils.isNotEmpty(registeredCapitalArr[0])){
                            beginRegisteredCapitalValue = registeredCapitalArr[0];
                        }
                        if (StringUtils.isNotEmpty(registeredCapitalArr[1])){
                            endRegisteredCapitalValue = registeredCapitalArr[1];
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                // 构建脚本查询
                String scriptBase = "if (doc['"+registeredCapitalFieldId+".keyword"+"'].size() == 0) { return false; } " +
                        "def amountStr = doc['"+registeredCapitalFieldId+".keyword"+"'].value; " +
                        "def pattern = /(\\d+(?:\\.\\d+)?)(万)?/; " +
                        "def matcher = pattern.matcher(amountStr); " +
                        "if (matcher.find()) { " +
                        "    def num = Double.parseDouble(matcher.group(1)); " +
                        "    if (matcher.group(2) != null) { " +
                        "        num *= 10000; " +
                        "    } ";
                if (StringUtils.isNotEmpty(beginRegisteredCapitalValue) && StringUtils.isNotEmpty(endRegisteredCapitalValue)) {
                    // 转换为以万为单位进行比较
                    String scriptSource = String.format(scriptBase + "    def numInWan = num / 10000; return numInWan >= %s && numInWan <= %s; } return false;",
                            beginRegisteredCapitalValue, endRegisteredCapitalValue);
                    Map<String, Object> params = new HashMap<>();
                    Script script = new Script(ScriptType.INLINE, "painless", scriptSource, params);
                    ScriptQueryBuilder scriptQuery = new ScriptQueryBuilder(script);
                    orQuery.should(scriptQuery);
                } else if (StringUtils.isNotEmpty(beginRegisteredCapitalValue) && StringUtils.isEmpty(endRegisteredCapitalValue)) {
                    // 转换为以万为单位进行比较
                    String scriptSource = String.format(scriptBase + "    def numInWan = num / 10000; return numInWan >= %s; } return false;", beginRegisteredCapitalValue);
                    Map<String, Object> params = new HashMap<>();
                    Script script = new Script(ScriptType.INLINE, "painless", scriptSource, params);
                    ScriptQueryBuilder scriptQuery = new ScriptQueryBuilder(script);
                    orQuery.should(scriptQuery);
                } else if (StringUtils.isEmpty(beginRegisteredCapitalValue) && StringUtils.isNotEmpty(endRegisteredCapitalValue)) {
                    // 转换为以万为单位进行比较
                    String scriptSource = String.format(scriptBase + "    def numInWan = num / 10000; return numInWan <= %s; } return false;", endRegisteredCapitalValue);
                    Map<String, Object> params = new HashMap<>();
                    Script script = new Script(ScriptType.INLINE, "painless", scriptSource, params);
                    ScriptQueryBuilder scriptQuery = new ScriptQueryBuilder(script);
                    orQuery.should(scriptQuery);
                }
            }
            boolQuery.must(orQuery);
        }
        return boolQuery;
    }

    @Override
    public ResultBody selectEdCustomerDetailFiledConfiguration(Map map) {
        //查询通用字段配置表所有被是否显示该字段为否的字段Id
        List<String> fieldShowList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationShowList();
        return ResultBody.success(fieldShowList);
    }

    @Override
    public ResultBody selectEdCustomerDetail(EdCustomerInfoVo map) {
        //获取企业数据库通用字段配置表所有被是否显示该字段为否的字段Id
        List<String> fieldShowList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationShowList();
        //根据企业数据库ID和通用配置是否展示获取企业数据信息
        map.setFieldList(fieldShowList);
//        List<EdCustomerInfoVo> edCustomerInfoVo = enterpriseDatabaseDao.selectEdCustomerDetail(map);
        //ES 条件查询
        List<EdCustomerInfoVo> edCustomerInfoVo = new ArrayList<>();
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            String indexName = "ed_customer_info";
            // 创建搜索请求并指定索引
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.size(10000);

            // 构建布尔查询
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 添加 customer_id 相等的条件
            boolQuery.must(QueryBuilders.matchPhraseQuery("customer_id", map.getCustomerId()));

            // 添加 common_field_configuration_id 在指定列表中的条件
            if (!map.getFieldList().isEmpty()) {
                // 统一大小写
                List<String> lowerCaseCustomerIds = map.getFieldList().stream().map(String::toLowerCase).collect(Collectors.toList());
                // 使用 keyword 子字段进行查询
                boolQuery.must(QueryBuilders.termsQuery("common_field_configuration_id.keyword", lowerCaseCustomerIds));
            }

            // 设置查询条件
            sourceBuilder.query(boolQuery);

            // 指定要返回的字段
            String[] includes = {"ID", "customer_id", "common_field_configuration_id", "field_code", "field_name", "field_value", "data_sources_code", "data_sources_name"};
            String[] excludes = {};
            sourceBuilder.fetchSource(includes, excludes);

            // 设置搜索源构建器到搜索请求
            searchRequest.source(sourceBuilder);

            // 执行搜索请求
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            // 遍历搜索结果并打印所需字段
            for (SearchHit hit : hits) {
                EdCustomerInfoVo edCustomerInfoVo1 = JSONObject.parseObject(hit.getSourceAsString(), EdCustomerInfoVo.class);
                edCustomerInfoVo.add(edCustomerInfoVo1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //将数据集合的数据按照通用字段ID 进行分组 如果ID相同 则将fieldValue值拼接起来 作为一个数据
        List<EdCustomerInfoVo> edCustomerInfoVoList = new ArrayList<>();
        Map<String, List<EdCustomerInfoVo>> edCustomerInfoVoMap = edCustomerInfoVo.stream().collect(Collectors.groupingBy(EdCustomerInfoVo::getCommonFieldConfigurationId));
        //查询企业数据库通用字段配置表所有未删除 状态为启用的模板配置字段 内容包含字段名称 字段编码 前端代码配置
        List<EdCommonFieldConfigurationVo> EdCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdImportTemplateConfigurationTableHeadList(new HashMap<>());
        for (EdCommonFieldConfigurationVo edCommonFieldConfigurationVo : EdCommonFieldConfigurationList) {
            EdCustomerInfoVo edCustomerInfo = new EdCustomerInfoVo();
            edCustomerInfo.setFieldName(edCommonFieldConfigurationVo.getFieldName());
            String k = edCommonFieldConfigurationVo.getId();
            List<EdCustomerInfoVo> fieldValueList = edCustomerInfoVoMap.get(k);
            if (CollectionUtils.isEmpty(fieldValueList)){
                edCustomerInfo.setFieldValue("暂无数据！");
//                edCustomerInfo.setFieldValue("");
            }else {
                String fieldValue = "";
                if (CollectionUtils.isNotEmpty(fieldValueList)){
                    if (k.equals(phoneFieldId) || k.equals(phoneFieldId2)){
                        //电话 手机 需要在每一个字段值后拼接自己的数据来源信息 数据来源使用（）包起来
                        fieldValue = fieldValueList.stream().map(s -> s.getFieldValue() + "(" + s.getDataSourcesName() + ")").collect(Collectors.joining(";"));
                    }else {
                        fieldValue = fieldValueList.stream().map(EdCustomerInfoVo::getFieldValue).collect(Collectors.joining(","));
                    }
                }
//                EdCustomerInfoVo edCustomerInfo = fieldValueList.get(0);
                if (k.equals(establishYearsFieldId)){
                    if (fieldValue.contains("-")){//包含特殊字符- 格式为 2020-01-01
                        try {
                            edCustomerInfo.setFieldValue(sd.format(sd.parse(fieldValue)));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }else {//不包含特殊字符 格式为 2020/1/1
                        edCustomerInfo.setFieldValue(sd.format(convertExcelDateToJavaDate(Long.parseLong(fieldValue))));
                    }
                }else {
                    edCustomerInfo.setFieldValue(fieldValue);
                }
            }
            edCustomerInfoVoList.add(edCustomerInfo);
        }
        return ResultBody.success(edCustomerInfoVoList);
    }

    @Override
    public ResultBody selectEdImportTemplateList(Map map) {
        //查询导入模板配置表所有未删除 启用状态 且PID为-1的模板 返回模板名称和模板ID
        List<Map> edImportTemplateConfigurationList = enterpriseDatabaseDao.selectEdImportTemplateList(map);
        return ResultBody.success(edImportTemplateConfigurationList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody importEdCustomer(MultipartFile file, Map map) throws IOException {
//        List reList = new ArrayList();
        int count = 0;
        int sCount = 0;
        int fCount = 0;
        List<EdImportTemplateConfigurationVo> edImportTemplateFieldList = new ArrayList<>();
        String dataSourcesCode = "";
        String dataSourcesName = "";
        if ("-1".equals(String.valueOf(map.get("templateId")))){//通用模板
            edImportTemplateFieldList = enterpriseDatabaseDao.selectEdCommonTemplateConfigurationInfo();
            //从模板配置信息获取数据来源信息
            dataSourcesCode = String.valueOf(map.get("dataSourcesCode"));
            dataSourcesName = String.valueOf(map.get("dataSourcesName"));
        }else {//自定义模板
            edImportTemplateFieldList = enterpriseDatabaseDao.selectEdImportTemplateConfigurationInfo(String.valueOf(map.get("templateId")));
            //从模板配置信息获取数据来源信息
            dataSourcesCode = edImportTemplateFieldList.get(0).getDataSourcesCode();
            dataSourcesName = edImportTemplateFieldList.get(0).getDataSourcesName();
        }
        //生成导入批次编号 设置导入批次编号（年月日时分秒+UUID） 计算保护失效 保存是否同步到机器人呼叫名单状态 保存数据进入企业数据库导入临时数据表 数据是否确认状态为待确认
        Date date = new Date();
        EdImportCustomerLogVo edImportCustomerLogVo = new EdImportCustomerLogVo();
        String importBatch = sfr.format(date)+ RandomUtil.randomNumbers(4);
        edImportCustomerLogVo.setImportBatch(importBatch);
        edImportCustomerLogVo.setConfirmId(SecurityUtils.getUserId());
        edImportCustomerLogVo.setConfirmName(SecurityUtils.getEmployeeName());
        edImportCustomerLogVo.setConfirmTime(sf.format(DateUtil.date()));
        edImportCustomerLogVo.setIsSynTo(map.get("isSynTo")+"");
        edImportCustomerLogVo.setCreateTime(sf.format(DateUtil.date()));
        edImportCustomerLogVo.setCreator(SecurityUtils.getUserId());
        //获取系统规则计算保护期
        String expireDate = "";
        String projectId = map.get("projectId")+"";
        String ComGUID = projectCluesDao.getComGUIDByProject(projectId);// 区域ID
        //查询规则计算报备逾期及预警时间
        ProjectRuleDetail projectRuleDetail = null;
        projectRuleDetail = projectCluesDao.selectProjectRuleZs(projectId,"2");
        //项目没有配置规则 查询区域的
        if (projectRuleDetail==null){
            projectRuleDetail = projectCluesDao.selectProjectRuleZs(ComGUID,"2");
        }
        //没有区域的查询集团的
        if (projectRuleDetail==null){
            projectRuleDetail = projectCluesDao.selectProjectRuleZs("-1","2");
        }
        String enterpriseDatabaseCustomerProtectionDaysStr = "";
        int enterpriseDatabaseCustomerProtectionDays = 0;
        if (projectRuleDetail!=null) {
            enterpriseDatabaseCustomerProtectionDaysStr = projectRuleDetail.getEnterpriseDatabaseCustomerProtectionDays();
            if (StringUtils.isNotEmpty(enterpriseDatabaseCustomerProtectionDaysStr)) {
                enterpriseDatabaseCustomerProtectionDays = Integer.parseInt(enterpriseDatabaseCustomerProtectionDaysStr);
            }
        }
        //查询规则计算逾期时间
        if (StringUtils.isNotEmpty(enterpriseDatabaseCustomerProtectionDaysStr)){
            Date dBefore = new Date();
            Calendar calendar = Calendar.getInstance(); //得到日历
            calendar.setTime(dBefore);//把当前时间赋给日历
            calendar.add(Calendar.DAY_OF_MONTH, enterpriseDatabaseCustomerProtectionDays);
            dBefore = calendar.getTime();
            expireDate = sf.format(dBefore);
        }else{
            expireDate = null;
        }

        //根据导入模板配置信息的起始行 获取模板数据
        int startLine = edImportTemplateFieldList.get(0).getTemplateStartingLine();
        String fileName = file.getOriginalFilename();

        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        InputStream is = file.getInputStream();
        Workbook wb = null;
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        Sheet sheet = wb.getSheetAt(0);
        System.out.println(sheet.getLastRowNum());
        if(sheet.getLastRowNum() == 0){
            return ResultBody.error(-1001,"暂无数据导入");
        }
        List<Map> phoneList = new ArrayList<>();
        //根据模板配置信息 对应模板列和数据库字段对应关系 处理数据保存进入数据集合
        Row rowTitle = sheet.getRow(startLine-1);
        Map<Integer,Map> titleMap = new HashMap<>();
//        String table = "<tr>";
        for (Cell cell : rowTitle) {
            String title = cell.getStringCellValue();
//            table = table.concat("<td>").concat(title).concat("</td>");
            for (EdImportTemplateConfigurationVo edImportTemplateConfigurationVo : edImportTemplateFieldList) {
                if (title.equals(edImportTemplateConfigurationVo.getFieldValue())){
                    //根据模板配置信息 获取通用字段配置信息
                    Map fieldMap = new HashMap();
                    fieldMap.put("fieldId",edImportTemplateConfigurationVo.getCommonFieldConfigurationId());
                    fieldMap.put("fieldCode",edImportTemplateConfigurationVo.getFieldCode());
                    fieldMap.put("fieldName",edImportTemplateConfigurationVo.getFieldName());
                    fieldMap.put("isCommunicationField",edImportTemplateConfigurationVo.getIsCommunicationField());
                    titleMap.put(cell.getColumnIndex(),fieldMap);
                }
            }
        }
//        table = table.concat("</tr>");
//        reList.add(0,table);

        List<EdCustomerInfoTeVo> edCustomerInfoTeList = new ArrayList<>();
//        int i = 1;
        long startTime = System.currentTimeMillis();
        for (int r = startLine; r <= sheet.getLastRowNum(); r++) {
            try {
                Row row = sheet.getRow(r);
//                table = "<tr>";
                String customerId = UUID.randomUUID().toString();
                Map phoneMap = new HashMap();
                phoneMap.put("customerId",customerId);
                String phone = "";
                List<EdCustomerInfoTeVo> rowTeList = new ArrayList<>();
                Boolean isMain = false;
                for (Cell cell : row) {
                    cell.setCellType(CellType.STRING);
                    if (cell.getStringCellValue().equals("-")){//空数据 不保存该数据
                        continue;
                    }
                    EdCustomerInfoTeVo edCustomerInfoTeVo = new EdCustomerInfoTeVo();
                    if (titleMap.containsKey(cell.getColumnIndex())){
                        Map fieldMap = titleMap.get(cell.getColumnIndex());
                        //保存通讯字段
                        if (commonFieldId.equals(fieldMap.get("fieldId")+"")){
                            isMain = true;
                            if (StringUtils.isNotEmpty(cell.getStringCellValue())){
                                phoneMap.put("name",cell.getStringCellValue());
                            }
                        }
                        if ("1".equals(String.valueOf(fieldMap.get("isCommunicationField")))){
                            //如果通讯字段有； 中文分号 显示数据异常 该条数据导入失败
                            if (cell.getStringCellValue().contains("；")){
                               //手动异常 不导入该数据 计入导入失败
                                int i = 1/0;
                            }
                            if (StringUtils.isNotEmpty(cell.getStringCellValue())){
                                if (StringUtils.isEmpty(phone)){
                                    phone = phone.concat(cell.getStringCellValue());
                                }else {
                                    phone = phone.concat(";").concat(cell.getStringCellValue());
                                }
                            }
                        }
//                        if (establishYearsFieldId.equals(String.valueOf(fieldMap.get("fieldId")))){//日期格式化
//                            table = table.concat("<td>").concat(sd.format(convertExcelDateToJavaDate(Long.parseLong(cell.getStringCellValue())))).concat("</td>");
//                        }else {
//                            table = table.concat("<td>").concat(cell.getStringCellValue()).concat("</td>");
//                        }

                        edCustomerInfoTeVo.setCustomerId(customerId);
                        edCustomerInfoTeVo.setCommonFieldConfigurationId(String.valueOf(fieldMap.get("fieldId")));
                        edCustomerInfoTeVo.setFieldCode(fieldMap.get("fieldCode")+"");
                        edCustomerInfoTeVo.setFieldName(fieldMap.get("fieldName")+"");
                        edCustomerInfoTeVo.setFieldValue(cell.getStringCellValue());
                        edCustomerInfoTeVo.setImportBatch(importBatch);
                        edCustomerInfoTeVo.setImportUserId(SecurityUtils.getUserId());
                        edCustomerInfoTeVo.setImportUserName(SecurityUtils.getUsername());
                        edCustomerInfoTeVo.setImportTime(DateUtil.date());
                        edCustomerInfoTeVo.setIsConfirm(0);
                        edCustomerInfoTeVo.setDataSourcesCode(dataSourcesCode);
                        edCustomerInfoTeVo.setDataSourcesName(dataSourcesName);
                        edCustomerInfoTeVo.setExaminePreType(0);
                        edCustomerInfoTeVo.setExpireDate(expireDate);
                        rowTeList.add(edCustomerInfoTeVo);
                    }
                }
                phoneMap.put("phone",phone);
                if (isMain){
                    phoneList.add(phoneMap);
                    edCustomerInfoTeList.addAll(rowTeList);
                    sCount = sCount + 1;
                }else {
                    count = count - 1;
                }
//                table = table.concat("</tr>");
//                reList.add(i,table);
//                i++;
            }catch (Exception e){
                fCount = fCount + 1;
            }finally {
                count = count + 1;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("耗费时间：" + (endTime - startTime));
        //保存企业数据库导入日志表 状态为导入中 导入批次编号为生成的编号 并保存是否同步到机器人呼叫名单状态和保护时间
        int size = edCustomerInfoTeList.size();
        //每次插入一千条
        int nc = size % 1000 == 0 ? size / 1000 : size / 1000 + 1;
        for (int s = 0; s < nc; s++) {
            if (s == nc - 1) {
                List<EdCustomerInfoTeVo> fformList = edCustomerInfoTeList.subList(s * 1000, size);
                enterpriseDatabaseDao.saveBacthEdImportCustomerTe(fformList);
            } else {
                List<EdCustomerInfoTeVo> fformList = edCustomerInfoTeList.subList(s * 1000, (s + 1) * 1000);
                enterpriseDatabaseDao.saveBacthEdImportCustomerTe(fformList);
            }
        }
        if (map.get("isSynTo").equals("1")){
            //根据是否同步到机器人呼叫名单 保存手机号到号码组
            //根据是否存在机器人呼叫名单 调用外呼接口 生成机器人号码组
            String token1 = String.valueOf(redisService.getVal("outbound."+SecurityUtils.getUsername()));
            Map map2 = new HashMap<>();
            map2.put("name",sf.format(date)+"招商宝企业数据库导入名单");
            map2.put("remarks",sf.format(date)+"招商宝企业数据库导入名单");
            List<Map> data2 = new ArrayList<>();
            Map currentJob = authMapper.mJobsListByUserId(SecurityUtils.getUserId());
            phoneList.stream().forEach(x->{
                Map map3 = new HashMap();
                map3.put("company_name", String.valueOf(x.get("name")));
                map3.put("nickname", String.valueOf(x.get("name")));
                map3.put("phone", String.valueOf(x.get("phone")));
                map3.put("cid", String.valueOf(x.get("customerId")));
                map3.put("uid",SecurityUtils.getUserId());
                map3.put("sid", String.valueOf(currentJob.get("JobOrgID")));
                map3.put("did", String.valueOf(currentJob.get("JobCode")));
                map3.put("wy_type",2);

                data2.add(map3);
            });
            map2.put("data",data2);
            HttpClientUtil.postHttpOutbound(addRobot, token1, map2);
        }
        if (sCount > 0){
            //保存导入日志
            String importResult = "";
            if (fCount > 0){
                importResult = "导入总数据"+count+"条，导入成功"+sCount+"条，导入失败"+fCount+"条，失败原因是：数据格式不正确。";
            }else {
                importResult = "导入总数据"+count+"条，导入成功"+sCount+"条，导入失败"+fCount+"条。";
            }
            edImportCustomerLogVo.setImportResult(importResult);
            edImportCustomerLogVo.setInvalidTime(expireDate);
            enterpriseDatabaseDao.insertEdImportCustomerLog(edImportCustomerLogVo);
            //接口最终返回本批次导入临时表待确认的数据 返回参考批量导入线索客户的回显
            Map reMap = new HashMap<>();
            reMap.put("importBatch",importBatch);
            reMap.put("importResult",importResult+"请注意：导入结果确认后次日可查看！");
//        reMap.put("list",reList);
            return ResultBody.success(reMap);
        }else {
            return ResultBody.error(400,"导入失败！");
        }
    }

    @Override
    public ResultBody selectEdImportCustomerList(Map map) {
        //判断导入批次是否为空
        String importBatch = String.valueOf(map.get("importBatch"));
        if (StringUtils.isEmpty(importBatch)){
            return ResultBody.error(400,"导入批次不能为空！");
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        //临时数据表 数据ID去重查询 实现分页
        int total = enterpriseDatabaseDao.getEdCustomerTeDistinctCustomerIdCount(map);
        int i = (pageIndex - 1) * pageSize;
        map.put("pageIndex",i);
        map.put("pageSize",pageSize);
        List<String> cid = enterpriseDatabaseDao.getEdCustomerTeDistinctCustomerIdList(map);
        if (CollectionUtils.isEmpty(cid)){
            return ResultBody.error(400,"暂无数据！");
        }
        //查询临时表数据
        List<EdCustomerInfoTeVo> edCustomerInfoTeList = enterpriseDatabaseDao.getEdCustomerTeList(cid);
        //将临时表按数据ID进行分组
        Map<String, List<EdCustomerInfoTeVo>> mapList = edCustomerInfoTeList.stream().collect(Collectors.groupingBy(EdCustomerInfoTeVo::getCustomerId));
        List reList = new ArrayList();
        if (CollectionUtils.isNotEmpty(edCustomerInfoTeList)){
            //查询企业数据库通用字段配置表所有未删除 状态为启用的模板配置字段 内容包含字段名称 字段编码 前端代码配置
            List<EdCommonFieldConfigurationVo> EdCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdImportTemplateConfigurationTableHeadList(map);
            //拼接前端信息
            String table = "<tr>";
            for (EdCommonFieldConfigurationVo fieldMap : EdCommonFieldConfigurationList){
                table = table.concat("<td>").concat(fieldMap.getFieldName()).concat("</td>");
            }
            table = table.concat("</tr>");
            reList.add(0,table);
            //表头拼接完成 开始拼接数据信息 循环分组后的临时表数据 根据临时表的common_field_configuration_id 字段对应通用模板配置的ID 对应数据位置 没有的数据为空
            for (Map.Entry<String, List<EdCustomerInfoTeVo>> entry : mapList.entrySet()) {
                table = "<tr>";
                for (EdCommonFieldConfigurationVo fieldMap : EdCommonFieldConfigurationList){
                    String value = "";
                    //从值里根据fieldMap的id和值的common_field_configuration_id 匹配获取对应字段值 给value
                    for (EdCustomerInfoTeVo valueMap : entry.getValue()){
                        if (fieldMap.getId().equals(valueMap.getCommonFieldConfigurationId())){
                            if (establishYearsFieldId.equals(fieldMap.getId())){//日期格式化
                                if (valueMap.getFieldValue().contains("-")){//包含特殊字符- 格式为 2020-01-01
                                    try {
                                        value = sd.format(sd.parse(valueMap.getFieldValue()));
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }else {//不包含特殊字符 格式为 2020/1/1
                                    value = sd.format(convertExcelDateToJavaDate(Long.parseLong(valueMap.getFieldValue())));
                                }
                            }else {
                                value = valueMap.getFieldValue();
                            }
                        }
                    }
                    table = table.concat("<td>").concat(value).concat("</td>");
                }
                table = table.concat("</tr>");
                reList.add(table);
            }
        }
        Map reMap = new HashMap<>();
        reMap.put("list",reList);
        reMap.put("total",total);
        return ResultBody.success(reMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody importEdCustomerConfirm(Map map) {
        //根据前端返回的确认状态和导入客户ID集合更新导入临时表待确认状态为已确认 只有已确认的数据晚上会同步到企业数据库 未确认数据会定时清除
        enterpriseDatabaseDao.updateEdImportCustomerTeConfirm(map);
        return ResultBody.success("确认成功！");
    }

    @Override
    @Scheduled(cron="0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void importEdCustomerTiming() {
        //获取企业数据库导入数据临时表所有已确认的客户
        List<EdCustomerInfoTeVo> edCustomerInfoTeList = enterpriseDatabaseDao.getEdImportCustomerTeConfirm(new HashMap<>());
        if (CollectionUtils.isNotEmpty(edCustomerInfoTeList)){
            //获取通用字段的配置 并按照通用字段配置ID分组获取通用字段配置信息
            List<EdCommonFieldConfigurationVo> edCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationList(new HashMap<>());
            Map<String, EdCommonFieldConfigurationVo> edCommonFieldConfigurationMap = edCommonFieldConfigurationList.stream().collect(Collectors.toMap(EdCommonFieldConfigurationVo::getId, edCommonFieldConfigurationVo -> edCommonFieldConfigurationVo));
            //将查询到的客户按照数据ID分组获取每条数据的信息
            Map<String, List<EdCustomerInfoTeVo>> edCustomerInfoTeMap =edCustomerInfoTeList.stream().collect(Collectors.groupingBy(EdCustomerInfoTeVo::getCustomerId));
            try (RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
                String index = "ed_customer_info";//es 索引设置为表名
                String index_search = "ed_customer_info_search";//es 索引设置为表名+搜索标识
                //循环数据
                for (Map.Entry<String, List<EdCustomerInfoTeVo>> entry : edCustomerInfoTeMap.entrySet()) {
                    String customerId = entry.getKey();
                    //根据客户名称获取是否存在重复客户数据
                    String customerName = entry.getValue().stream().filter(x-> x.getCommonFieldConfigurationId().equals(commonFieldId)).findFirst().get().getFieldValue();
//                    String oldCustomerId = enterpriseDatabaseDao.selectEdCustomerIsRepeat(customerName,commonFieldId);
                    //使用ES 条件查询
                    String oldCustomerId = "";
                    SearchRequest searchRequest = new SearchRequest(index);
                    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    boolQuery.must(QueryBuilders.matchPhraseQuery("field_value", customerName));
                    boolQuery.must(QueryBuilders.matchPhraseQuery("common_field_configuration_id", commonFieldId));
                    sourceBuilder.query(boolQuery);
                    searchRequest.source(sourceBuilder);
                    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
                    SearchHits hits = searchResponse.getHits();
                    for (SearchHit hit : hits) {
                        System.out.println("查询到文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
                        EdCustomerInfoVo edCustomerInfoVo = JSONObject.parseObject(hit.getSourceAsString(), EdCustomerInfoVo.class);
                        oldCustomerId = edCustomerInfoVo.getCustomerId();
                    }
                    //电话 更多电话 字段可能存在多个数据 是以;隔开的 需要分开处理 将原数据按;拆分 移除原数据 按拆分后的保存新数据
                    List<EdCustomerInfoTeVo> ndel = new ArrayList<>();
                    List<EdCustomerInfoTeVo> nadd = new ArrayList<>();
                    for (EdCustomerInfoTeVo edCustomerInfoTeVo : entry.getValue()){
                        if (edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(phoneFieldId) || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(phoneFieldId2)){
                            String value = edCustomerInfoTeVo.getFieldValue();
                            String[] valueArr = value.split(";");
                            ndel.add(edCustomerInfoTeVo);
                            for (String s : valueArr){
                                EdCustomerInfoTeVo edCustomerInfoTeVo1 = new EdCustomerInfoTeVo();
                                BeanUtils.copyProperties(edCustomerInfoTeVo,edCustomerInfoTeVo1);
                                edCustomerInfoTeVo1.setFieldValue(s);
                                nadd.add(edCustomerInfoTeVo1);
                            }
                        }
                    }
                    entry.getValue().removeAll(ndel);
                    entry.getValue().addAll(nadd);

                    if (StringUtils.isNotEmpty(oldCustomerId)){//存在重复数据
                        //如果存在重复数据 根据每个字段的判重处理方式 更新重复数据 重复数据如果是去重后合并 判断历史是否存在 如果不存在 新增新数据 如果存在 跳过 并保存不同的数据来源到数据来源字段 导入人也进行合并用于人员查询
                        for (EdCustomerInfoTeVo edCustomerInfoTeVo : entry.getValue()) {
                            edCustomerInfoTeVo.setCustomerId(oldCustomerId);
                            //获取数据信息
//                            List<EdCustomerInfoVo> edCustomerInfoList = enterpriseDatabaseDao.selectEdCustomerFieldInfo(oldCustomerId,edCustomerInfoTeVo.getCommonFieldConfigurationId());
                            //使用ES 条件查询
                            List<EdCustomerInfoVo> edCustomerInfoList = new ArrayList<>();
                            SearchRequest searchRequest1 = new SearchRequest(index);
                            SearchSourceBuilder sourceBuilder1 = new SearchSourceBuilder();
                            BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
                            boolQuery1.must(QueryBuilders.matchPhraseQuery("customer_id", oldCustomerId));
                            boolQuery1.must(QueryBuilders.matchPhraseQuery("common_field_configuration_id", edCustomerInfoTeVo.getCommonFieldConfigurationId()));
                            sourceBuilder1.query(boolQuery1);
                            searchRequest1.source(sourceBuilder1);
                            SearchResponse searchResponse1 = client.search(searchRequest1, RequestOptions.DEFAULT);
                            SearchHits hits1 = searchResponse1.getHits();
                            for (SearchHit hit1 : hits1) {
                                System.out.println("查询到文档，ID: " + hit1.getId() + ", 内容: " + hit1.getSourceAsString());
                                EdCustomerInfoVo edCustomerInfoVo1 = JSONObject.parseObject(hit1.getSourceAsString(), EdCustomerInfoVo.class);
                                edCustomerInfoVo1.setDocId(hit1.getId());
                                edCustomerInfoList.add(edCustomerInfoVo1);
                            }

                            //根据该数据字段ID获取对应字段配置信息 获取所有判重字段 和判重处理逻辑
                            EdCommonFieldConfigurationVo edCommonFieldConfigurationVo = edCommonFieldConfigurationMap.get(edCustomerInfoTeVo.getCommonFieldConfigurationId());
                            if (edCommonFieldConfigurationVo.getProcessingMethodForDuplicateData().equals("1")){//跳过该字段

                            }else if (edCommonFieldConfigurationVo.getProcessingMethodForDuplicateData().equals("2")){//覆盖原字段内容
                                //判断历史是否存在多条该字段内容 如果存在删除其他的 新增一条新的数据 如果不存在其他的 更新该字段内容 如果数据不存在 新增该字段内容
                                if (edCustomerInfoList.size() > 1){
                                    //删除其他数据
                                    enterpriseDatabaseDao.deleteEdCustomerIsRepeatField(oldCustomerId,edCustomerInfoTeVo.getCommonFieldConfigurationId());
                                    //ES 删除操作
                                    for (EdCustomerInfoVo edCustomerInfoVo : edCustomerInfoList) {
                                        DeleteRequest deleteRequest = new DeleteRequest(index,edCustomerInfoVo.getDocId());
                                        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
                                        System.out.println("删除文档，ID: " + deleteResponse.getId());
                                    }
                                    //新增该字段内容
                                    enterpriseDatabaseDao.saveEdCustomerInfo(edCustomerInfoTeVo);
                                    //ES 新增操作
                                    IndexRequest request = new IndexRequest(index);
                                    //数据转换
                                    Map<String, Object> addMap = this.dataToESMap(edCustomerInfoTeVo);
                                    request.source(addMap, XContentType.JSON);
                                    IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                                    System.out.println("文档已添加，ID: " + response.getId());
                                }else if (edCustomerInfoList.size() == 1){
                                    enterpriseDatabaseDao.updateEdCustomerInfo(edCustomerInfoTeVo);
                                    //ES 更新操作
                                    Map<String, Object> updateMap = new HashMap<>();
                                    updateMap.put("field_value", edCustomerInfoTeVo.getFieldValue());
                                    updateMap.put("data_sources_code", edCustomerInfoTeVo.getDataSourcesCode());
                                    updateMap.put("data_sources_name", edCustomerInfoTeVo.getDataSourcesName());
                                    updateMap.put("import_user", edCustomerInfoTeVo.getImportUserId());
                                    updateMap.put("updator", edCustomerInfoTeVo.getImportUserId());
                                    updateMap.put("update_time", sf.format(edCustomerInfoTeVo.getImportTime()));

                                    UpdateRequest request = new UpdateRequest(index ,edCustomerInfoList.get(0).getDocId());
                                    request.doc(updateMap);
                                    UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
                                    System.out.println("文档已更新，版本: " + response.getVersion());
                                }else if (CollectionUtils.isEmpty(edCustomerInfoList)){
                                    //新增该字段内容
                                    enterpriseDatabaseDao.saveEdCustomerInfo(edCustomerInfoTeVo);
                                    //ES 新增操作
                                    IndexRequest request = new IndexRequest(index);
                                    //数据转换
                                    Map<String, Object> addMap = this.dataToESMap(edCustomerInfoTeVo);
                                    request.source(addMap, XContentType.JSON);
                                    IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                                    System.out.println("文档已添加，ID: " + response.getId());
                                }
                                //ES 更新操作
                                if (edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(commonFieldId)
                                        || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(industryFieldId) || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(industryTwoFieldId)
                                        || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(industryThreeFieldId) || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(industryFourFieldId)
                                        || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(provinceFieldId) || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(companyScaleFieldId)
                                        || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(establishYearsFieldId) || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(registrationStatusFieldId)
                                        || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(registeredCapitalFieldId)){
                                    Map<String, Object> updateMap2 = new HashMap<>();
                                    if (edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(establishYearsFieldId)){
                                        if (edCustomerInfoTeVo.getFieldValue().contains("-")){//包含特殊字符- 格式为 2020-01-01
                                            updateMap2.put(edCustomerInfoTeVo.getCommonFieldConfigurationId(), sf.format(sf.parse(edCustomerInfoTeVo.getFieldValue().concat(" 00:00:00"))));
                                        }else {//不包含特殊字符 格式为 2020/1/1
                                            updateMap2.put(edCustomerInfoTeVo.getCommonFieldConfigurationId(), sf.format(convertExcelDateToJavaDate(Long.parseLong(edCustomerInfoTeVo.getFieldValue()))));
                                        }
                                    }else {
                                        updateMap2.put(edCustomerInfoTeVo.getCommonFieldConfigurationId(), edCustomerInfoTeVo.getFieldValue());
                                    }
                                    //通过数据ID 搜索到该条数据的es ID
                                    String docId = "";
                                    String importUser = "";
                                    SearchRequest searchRequest2 = new SearchRequest(index_search);
                                    SearchSourceBuilder sourceBuilder2 = new SearchSourceBuilder();
                                    BoolQueryBuilder boolQuery2 = QueryBuilders.boolQuery();
                                    boolQuery2.must(QueryBuilders.matchPhraseQuery("customer_id", oldCustomerId));
                                    sourceBuilder2.query(boolQuery2);
                                    searchRequest2.source(sourceBuilder2);
                                    SearchResponse searchResponse2 = client.search(searchRequest2, RequestOptions.DEFAULT);
                                    SearchHits hits2 = searchResponse2.getHits();
                                    for (SearchHit hit2 : hits2) {
                                        System.out.println("查询到文档，ID: " + hit2.getId() + ", 内容: " + hit2.getSourceAsString());
                                        docId = hit2.getId();
                                        importUser = String.valueOf(hit2.getSourceAsMap().get("import_user"));
                                    }
                                    //通过es ID 更新该数据的字段值
                                    if (StringUtils.isNotEmpty(docId)){
                                        UpdateRequest request = new UpdateRequest(index_search ,docId);
                                        if (StringUtils.isNotEmpty(importUser)){
                                            String[] importUserArr = importUser.split(",");
                                            //如果集合内有当前导入人 不操作 如果没有 进行拼接
                                            if (Arrays.asList(importUserArr).contains(edCustomerInfoTeVo.getImportUserId())){
                                                //如果集合内有当前导入人 不操作 如果没有 进行拼接
                                            }else {
                                                importUser = importUser + "," + edCustomerInfoTeVo.getImportUserId();
                                            }
                                            updateMap2.put("import_user", importUser);
                                        }
                                        request.doc(updateMap2);
                                        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
                                        System.out.println("文档已更新，版本: " + response.getVersion());
                                    }
                                }
                            }else if (edCommonFieldConfigurationVo.getProcessingMethodForDuplicateData().equals("3")){//去重并合并该字段内容
                                //如果数据集合存在该数据 不保存 如果不存在 保存新数据
                                Boolean flag = true;
                                for (EdCustomerInfoVo edCustomerInfoVo : edCustomerInfoList) {
                                    if (flag){
                                        if (edCustomerInfoVo.getFieldValue().equals(edCustomerInfoTeVo.getFieldValue())){
                                            //跳过
                                            flag = false;
                                        }else {
                                            flag = true;
                                        }
                                    }
                                }
                                if (flag){
                                    //保存该字段内容
                                    enterpriseDatabaseDao.saveEdCustomerInfo(edCustomerInfoTeVo);
                                    //ES 新增操作
                                    IndexRequest request = new IndexRequest(index);
                                    //数据转换
                                    Map<String, Object> addMap = this.dataToESMap(edCustomerInfoTeVo);
                                    request.source(addMap, XContentType.JSON);
                                    IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                                    System.out.println("文档已添加，ID: " + response.getId());
                                    //ES 更新操作
                                    if (edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(commonFieldId)
                                            || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(industryFieldId) || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(industryTwoFieldId)
                                            || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(industryThreeFieldId) || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(industryFourFieldId)
                                            || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(provinceFieldId) || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(companyScaleFieldId)
                                            || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(establishYearsFieldId) || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(registrationStatusFieldId)
                                            || edCustomerInfoTeVo.getCommonFieldConfigurationId().equals(registeredCapitalFieldId)){
                                        //通过数据ID 搜索到该条数据的es ID
                                        String docId = "";
                                        String oldVlaue  = "";
                                        String importUser = "";
                                        SearchRequest searchRequest2 = new SearchRequest(index_search);
                                        SearchSourceBuilder sourceBuilder2 = new SearchSourceBuilder();
                                        BoolQueryBuilder boolQuery2 = QueryBuilders.boolQuery();
                                        boolQuery2.must(QueryBuilders.matchPhraseQuery("customer_id", oldCustomerId));
                                        sourceBuilder2.query(boolQuery2);
                                        searchRequest2.source(sourceBuilder2);
                                        SearchResponse searchResponse2 = client.search(searchRequest2, RequestOptions.DEFAULT);
                                        SearchHits hits2 = searchResponse2.getHits();
                                        for (SearchHit hit2 : hits2) {
                                            System.out.println("查询到文档，ID: " + hit2.getId() + ", 内容: " + hit2.getSourceAsString());
                                            docId = hit2.getId();
                                            //从hit2.getSourceAsString 取对应字段的值
                                            Map<String, Object> sourceAsMap = hit2.getSourceAsMap();
                                            oldVlaue = sourceAsMap.get(edCustomerInfoTeVo.getCommonFieldConfigurationId()).toString();
                                            importUser = String.valueOf(sourceAsMap.get("import_user"));
                                        }
                                        //通过es ID 更新该数据的字段值
                                        if (StringUtils.isNotEmpty(docId)){
                                            Map<String, Object> updateMap2 = new HashMap<>();
                                            updateMap2.put(edCustomerInfoTeVo.getCommonFieldConfigurationId(), oldVlaue + "," + edCustomerInfoTeVo.getFieldValue());
                                            if (StringUtils.isNotEmpty(importUser)){
                                                String[] importUserArr = importUser.split(",");
                                                //如果集合内有当前导入人 不操作 如果没有 进行拼接
                                                if (Arrays.asList(importUserArr).contains(edCustomerInfoTeVo.getImportUserId())){
                                                    //如果集合内有当前导入人 不操作 如果没有 进行拼接
                                                }else {
                                                    importUser = importUser + "," + edCustomerInfoTeVo.getImportUserId();
                                                }
                                                updateMap2.put("import_user", importUser);
                                            }
                                            UpdateRequest request2 = new UpdateRequest(index_search ,docId);
                                            request2.doc(updateMap2);
                                            UpdateResponse response2 = client.update(request2, RequestOptions.DEFAULT);
                                            System.out.println("文档已更新，版本: " + response2.getVersion());
                                        }
                                    }
                                }
                            }
                        }
                    }else {
                        //如果不存在重复数据 保存新的客户进入企业数据库表
                        enterpriseDatabaseDao.saveBatchEdCustomerInfo(entry.getValue());
                        //ES 批量新增操作
                        Map<String, Object> asMap = new HashMap<>();
                        //初始化所有查询字段进入asMap
                        asMap.put(commonFieldId, "");
                        asMap.put(industryFieldId, "");
                        asMap.put(industryTwoFieldId, "");
                        asMap.put(industryThreeFieldId, "");
                        asMap.put(industryFourFieldId, "");
                        asMap.put(provinceFieldId, "");
                        asMap.put(companyScaleFieldId, "");
                        asMap.put(establishYearsFieldId, sf.format(sf.parse("1900-01-01 00:00:00")));//给一个超出时间的默认时间 没有的话查询不到 有的话会更新成正确的
                        asMap.put(registrationStatusFieldId, "");
                        asMap.put(registeredCapitalFieldId, "");
                        for (EdCustomerInfoTeVo doc : entry.getValue()) {
                            if (doc.getCommonFieldConfigurationId().equals(commonFieldId)
                                    || doc.getCommonFieldConfigurationId().equals(industryFieldId) || doc.getCommonFieldConfigurationId().equals(industryTwoFieldId)
                                    || doc.getCommonFieldConfigurationId().equals(industryThreeFieldId) || doc.getCommonFieldConfigurationId().equals(industryFourFieldId)
                                    || doc.getCommonFieldConfigurationId().equals(provinceFieldId) || doc.getCommonFieldConfigurationId().equals(companyScaleFieldId)
                                    || doc.getCommonFieldConfigurationId().equals(establishYearsFieldId) || doc.getCommonFieldConfigurationId().equals(registrationStatusFieldId)
                                    || doc.getCommonFieldConfigurationId().equals(registeredCapitalFieldId)){
                                if (doc.getCommonFieldConfigurationId().equals(establishYearsFieldId)){
                                    if (doc.getFieldValue().contains("-")){//包含特殊字符- 格式为 2020-01-01
                                        asMap.put(doc.getCommonFieldConfigurationId(), sf.format(sf.parse(doc.getFieldValue().concat(" 00:00:00"))));
                                    }else {//不包含特殊字符 格式为 2020/1/1
                                        asMap.put(doc.getCommonFieldConfigurationId(), sf.format(convertExcelDateToJavaDate(Long.parseLong(doc.getFieldValue()))));
                                    }
                                }else {
                                    asMap.put(doc.getCommonFieldConfigurationId(), doc.getFieldValue());
                                }
                            }
                            IndexRequest request = new IndexRequest(index);
                            //数据转换
                            Map<String, Object> addMap = this.dataToESMap(doc);
                            request.source(addMap, XContentType.JSON);
                            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                            System.out.println("文档已添加，ID: " + response.getId());
                        }
                        //ES 新增操作
                        IndexRequest request = new IndexRequest(index_search);
                        asMap.put("customer_id", customerId);
                        asMap.put("import_user", entry.getValue().get(0).getImportUserId());
                        asMap.put("examine_pre_type", entry.getValue().get(0).getExaminePreType());
                        asMap.put("create_time", sf.format(entry.getValue().get(0).getImportTime()));
                        request.source(asMap, XContentType.JSON);
                        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                        System.out.println("文档已添加，ID: " + response.getId());
                        //保存导入批次数据关联表
                        Map relate = new HashMap<>();
                        relate.put("customerId",customerId);
                        relate.put("importBatch",entry.getValue().get(0).getImportBatch());
                        enterpriseDatabaseDao.saveEdCustomerRelate(relate);
                    }
                }
                //全部批次导入完成 删除临时表的数据
                enterpriseDatabaseDao.deleteEdImportCustomerTeConfirm(new HashMap<>());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ResultBody selectEdImportTemplateHistoryList(EdImportCustomerLogVo map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (StringUtils.isNotEmpty(map.getPageNum())){
            pageIndex = Integer.parseInt(map.getPageNum());
        }
        if (StringUtils.isNotEmpty(map.getPageSize())){
            pageSize = Integer.parseInt(map.getPageSize());
        }
        PageHelper.startPage(pageIndex,pageSize);
        //查询企业数据库导入日志表里操作人为当前登录人的记录 返回字段为导入时间 导入批次 操作人 导入结果 保护时效(按操作时间和系统配置计算保护失效)
        map.setConfirmId(SecurityUtils.getUserId());
        List<EdImportCustomerLogVo> edImportCustomerLogList = enterpriseDatabaseDao.selectEdImportCustomerLogList(map);
        return ResultBody.success(new PageInfo<>(edImportCustomerLogList));
    }

    @Override
    public ResultBody selectEdImportCustomerHistorySuccessList(EdImportCustomerLogVo map) {
        //根据历史导入批次ID获取本批次导入成功的数据
        List<EdCustomerInfoVo> edCustomerInfoList = enterpriseDatabaseDao.selectEdImportCustomerHistorySuccessList(map);
        return ResultBody.success(edCustomerInfoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron="0 0 1 * * ?")
    public void edCustomerOverProtectTiming() {
        //查询企业数据库表 企业数据库数据查看权限为私密 且过保时间在当前时间之前的客户 更新企业数据库类型为 集团
        List<MessageForm> messages = new ArrayList<>();
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("企业数据库过保定时任务开始");
        messageMapper.insertLogs(sysLog);
        log.info("<====================企业数据库过保定时任务开始======================>");
        String note = "";
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            String indexName = "ed_customer_info";
            String searchIndexName = "ed_customer_info_search";

            //查询已经过保的私密企业数据库客户
//            List<String> mapListEdExpire = enterpriseDatabaseDao.selectEdCustomerIdExpireList(map);
            //ES 条件查询
            List<String> mapListEdExpire = new ArrayList<>();
            List<String> mapListEdExpireSCId = new ArrayList<>();
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.size(10000);
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must(QueryBuilders.termQuery("examine_pre_type", 0));
            boolQuery.must(QueryBuilders.rangeQuery("expire_date").lt("now"));
            sourceBuilder.query(boolQuery);
//            sourceBuilder.fetchSource(false);
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : searchResponse.getHits()) {
                mapListEdExpire.add(hit.getId());
                //获取ES返回结果里面customer_id 如果mapListEdExpireSCId不包含该id 就保存
                try {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    if (!mapListEdExpireSCId.contains(sourceAsMap.get("customer_id"))){
                        mapListEdExpireSCId.add(sourceAsMap.get("customer_id").toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            BulkRequest bulkRequest = new BulkRequest();
            if (mapListEdExpire!=null && mapListEdExpire.size()>0){
                //批量更新企业数据库客户数据查看权限为公开
                enterpriseDatabaseDao.updateEdCustomerExaminePreTypeInfo(mapListEdExpire);
                //ES 批量更新
                for (String edCustomerId : mapListEdExpire) {
                    Map<String, Object> updateDoc = new HashMap<>();
                    updateDoc.put("examine_pre_type", "1");
                    UpdateRequest updateRequest = new UpdateRequest(indexName,edCustomerId)
                            .doc(updateDoc, XContentType.JSON);
                    bulkRequest.add(updateRequest);
                }
                BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (bulkResponse.hasFailures()) {
                    System.out.println("批量更新存在失败操作: " + bulkResponse.buildFailureMessage());
                    note = "批量更新存在失败操作: " + bulkResponse.buildFailureMessage();
                } else {
                    System.out.println("批量更新成功");
                    note = "批量更新成功";
                }
            }
            if (mapListEdExpireSCId!=null && mapListEdExpireSCId.size()>0){
                //通过ID 查询searchIndexName 获取es ID 进行更新examine_pre_type 为1
                SearchRequest searchRequest1 = new SearchRequest(searchIndexName);
                SearchSourceBuilder sourceBuilder1 = new SearchSourceBuilder();
                sourceBuilder1.size(10000);
                BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
                // 统一大小写
                List<String> lowerCaseCustomerIds = mapListEdExpireSCId.stream().map(String::toLowerCase).collect(Collectors.toList());
                // 使用 keyword 子字段进行查询
                boolQuery1.must(QueryBuilders.termsQuery("customer_id.keyword", lowerCaseCustomerIds));
                // 设置查询条件
                sourceBuilder1.query(boolQuery1);
                // 设置搜索源构建器到搜索请求
                searchRequest1.source(sourceBuilder1);
                // 执行搜索请求
                SearchResponse searchResponse1 = client.search(searchRequest1, RequestOptions.DEFAULT);
                SearchHits hits1 = searchResponse1.getHits();
                // 遍历搜索结果并打印所需字段
                BulkRequest bulkRequest1 = new BulkRequest();
                for (SearchHit hit : hits1) {
                    System.out.println("查询到文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
                    Map<String, Object> updateDoc1 = new HashMap<>();
                    updateDoc1.put("examine_pre_type", "1");
                    UpdateRequest updateRequest1 = new UpdateRequest(searchIndexName,hit.getId())
                            .doc(updateDoc1, XContentType.JSON);
                    bulkRequest1.add(updateRequest1);
                }
                BulkResponse bulkResponse1 = client.bulk(bulkRequest1, RequestOptions.DEFAULT);
                if (bulkResponse1.hasFailures()) {
                    System.out.println("批量更新存在失败操作: " + bulkResponse1.buildFailureMessage());
                    note = "批量更新存在失败操作: " + bulkResponse1.buildFailureMessage();
                } else {
                    System.out.println("批量更新成功");
                    note = "批量更新成功";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SysLog sysLog0 = new SysLog();
        sysLog0.setExecutTime(sf.format(new Date()));
        sysLog0.setTaskName("企业数据库过保定时任务结束");
        sysLog0.setNote(note);
        messageMapper.insertLogs(sysLog0);
        log.info("<====================企业数据库过保定时任务结束======================>");
    }

    @Override
    public void downloadEdImportTemplate(HttpServletRequest request, HttpServletResponse response, Map map) {
        //判断模板ID是否存在 如果不存在 提示
//        if (ObjectUtils.isNotEmpty(map.get("templateId"))){
            //模板信息
            String excelName = "";//模板名称
            List<String> filedNames = new ArrayList<>();
            String[] headers = null;//表头
            ArrayList<Object[]> dataset = new ArrayList<>();//数据集合
            int line = 1;//起始行
//            //根据模板ID获取模板信息
//            String templateId = String.valueOf(map.get("templateId"));
//            List<EdImportTemplateConfigurationVo> edImportTemplateConfigurationInfo = enterpriseDatabaseDao.selectEdImportTemplateConfigurationInfo(templateId);
//            if (CollectionUtils.isNotEmpty(edImportTemplateConfigurationInfo)){
//                for (EdImportTemplateConfigurationVo edImportTemplateConfigurationVo : edImportTemplateConfigurationInfo) {
//                    filedNames.add(edImportTemplateConfigurationVo.getFieldValue());
//                }
//                line = edImportTemplateConfigurationInfo.get(0).getTemplateStartingLine();
//                excelName = edImportTemplateConfigurationInfo.get(0).getTemplateName();
//                headers = filedNames.toArray(new String[0]);
//            }
            //查询企业数据库通用字段配置表所有未删除 状态为启用的模板配置字段 内容包含字段名称 字段编码 前端代码配置
            List<EdCommonFieldConfigurationVo> EdCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdImportTemplateConfigurationTableHeadList(map);
            if (CollectionUtils.isNotEmpty(EdCommonFieldConfigurationList)){
                for (EdCommonFieldConfigurationVo edCommonFieldConfigurationVo : EdCommonFieldConfigurationList) {
                    filedNames.add(edCommonFieldConfigurationVo.getFieldName());
                }
                line = 1;
                excelName = "通用导入模板";
                headers = filedNames.toArray(new String[0]);
            }

            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            try {
                excelExportUtil.exportExcel3(line,excelName, headers, dataset, excelName, response,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
    }

    @Override
    public ResultBody edCustomerJoinTheCallDistribution(Map map) {
        String type = String.valueOf(map.get("type"));
        List<Map> customerIds = new ArrayList<>();
        if ("1".equals(type)){
            if (ObjectUtils.isEmpty(map.get("customerIds"))){
                return ResultBody.error(-10002, "请选择要勾选话单客户！");
            }
            customerIds = (List<Map>) map.get("customerIds");
        }else {
            String batchCode = String.valueOf(map.get("batchCode"));
            //根据批次代码从关联表查询数据ID
            List<String> ids = enterpriseDatabaseDao.selectEdCustomerRelateIds(batchCode);
            List<EdCustomerInfoVo> edCustomerInfoList = new ArrayList<>();
            //ES 条件查询
            if (CollectionUtils.isNotEmpty(ids)){//数据已导入es 走es查询
                try (RestHighLevelClient client = new RestHighLevelClient(
                        RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
                    // 创建搜索请求并指定索引
                    String indexName = "ed_customer_info";
                    SearchRequest searchRequest1 = new SearchRequest(indexName);
                    SearchSourceBuilder sourceBuilder1 = new SearchSourceBuilder();
                    sourceBuilder1.size(10000);

                    // 构建布尔查询
                    BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();

                    // 添加 customer_id 在指定列表中的条件
                    if (!ids.isEmpty()) {
                        // 统一大小写
                        List<String> lowerCaseCustomerIds = ids.stream().map(String::toLowerCase).collect(Collectors.toList());
                        // 使用 keyword 子字段进行查询
                        boolQuery1.must(QueryBuilders.termsQuery("customer_id.keyword", lowerCaseCustomerIds));
                    }

                    // 设置查询条件
                    sourceBuilder1.query(boolQuery1);

                    // 设置搜索源构建器到搜索请求
                    searchRequest1.source(sourceBuilder1);

                    // 执行搜索请求
                    SearchResponse searchResponse1 = client.search(searchRequest1, RequestOptions.DEFAULT);
                    SearchHits hits1 = searchResponse1.getHits();

                    // 遍历搜索结果并打印所需字段
                    for (SearchHit hit : hits1) {
                        System.out.println("查询到文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
                        EdCustomerInfoVo edCustomerInfoVo = JSONObject.parseObject(hit.getSourceAsString(), EdCustomerInfoVo.class);
                        edCustomerInfoList.add(edCustomerInfoVo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {//数据暂时没有导入es 走数据库临时表查询
                edCustomerInfoList = enterpriseDatabaseDao.getBatchEdCustomerTeList(batchCode);
            }
            //获取通用字段的配置 并按照通用字段配置ID分组获取通用字段配置信息
            List<EdCommonFieldConfigurationVo> edCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationList(new HashMap<>());
            Map<String, EdCommonFieldConfigurationVo> edCommonFieldConfigurationMap = edCommonFieldConfigurationList.stream().collect(Collectors.toMap(EdCommonFieldConfigurationVo::getId, edCommonFieldConfigurationVo -> edCommonFieldConfigurationVo));
            //先按照数据ID分组 获取每个数据的完整数据集合
            Map<String, List<EdCustomerInfoVo>> edCustomerInfoMap = edCustomerInfoList.stream().collect(Collectors.groupingBy(EdCustomerInfoVo::getCustomerId));
            //循环数据集合拼接每一个展示数据
            List<Map> reList = new ArrayList<>();
            edCustomerInfoMap.forEach((key, value) -> {
                List<EdCustomerInfoVo> edCustomerInfoVoList = edCustomerInfoMap.get(key);
                final String[] companyName = {""};
                final String[] phoneToCall = {""};
                List<String> details = new ArrayList<>();
                Map reMap = new HashMap();
                //将数据集合的数据按照通用字段ID 进行分组 如果ID相同 则将fieldValue值拼接起来 作为一个数据
                Map<String, List<EdCustomerInfoVo>> edCustomerInfoVoMap = edCustomerInfoVoList.stream().collect(Collectors.groupingBy(EdCustomerInfoVo::getCommonFieldConfigurationId));
                edCustomerInfoVoMap.forEach((k, v) -> {
                    List<EdCustomerInfoVo> fieldValueList = edCustomerInfoVoMap.get(k);
                    String fieldValue = "";
                    String fieldValue2 = "";
                    if (CollectionUtils.isNotEmpty(fieldValueList)){
                        if (k.equals(phoneFieldId) || k.equals(phoneFieldId2)){
                            //电话 手机 需要在每一个字段值后拼接自己的数据来源信息 数据来源使用（）包起来
                            fieldValue = fieldValueList.stream().map(edCustomerInfoVo -> edCustomerInfoVo.getFieldValue() + "(" + edCustomerInfoVo.getDataSourcesName() + ")").collect(Collectors.joining(";"));
                            fieldValue2 = fieldValueList.stream().map(EdCustomerInfoVo::getFieldValue).collect(Collectors.joining(";"));
                        }else {
                            fieldValue = fieldValueList.stream().map(EdCustomerInfoVo::getFieldValue).collect(Collectors.joining(","));
                        }
                    }
                    if (k.equals(commonFieldId)){
                        companyName[0] = fieldValueList.get(0).getFieldValue();
                    }else {
                        details.add(fieldValueList.get(0).getFieldName()+"："+fieldValue);
                        if ("1".equals(edCommonFieldConfigurationMap.get(k).getIsCommunicationField())){
                            if (StringUtils.isEmpty(phoneToCall[0])){
                                phoneToCall[0] = fieldValue2;
                            }else {
                                phoneToCall[0] = phoneToCall[0]+";"+fieldValue2;
                            }
                        }
                    }
                });
                reMap.put("customerId", key);
                reMap.put("companyName", companyName[0]);
                reMap.put("phoneToCall",phoneToCall[0]);
                reMap.put("details",details);
                reList.add(reMap);
            });
            customerIds.addAll(reList);
        }
        if (CollectionUtils.isEmpty(customerIds)){
            return ResultBody.error(-10002, "没有可以分配的话单客户！");
        }
        //将客户集合按照客户ID分组
        Map<String, List<Map>> customerMaps = customerIds.stream().collect(Collectors.groupingBy(map1 -> map1.get("customerId").toString()));
        if (ObjectUtils.isEmpty(map.get("customerDistributionList"))){
            return ResultBody.error(-10002, "请选择要分配的通话人员！");
        }
        List<Map> customerDistributionList = (List<Map>) map.get("customerDistributionList");
        //将通话人员集合按照人员ID分组
        Map<String, List<Map>> customerDistributionMaps = customerDistributionList.stream().collect(Collectors.groupingBy(map1 -> map1.get("salesAttributionId").toString()));
        //调用平均分配算法获取每个人员的话单数据
        List<String> projectClueIdList = customerIds.stream().map(map1 -> map1.get("customerId").toString()).collect(Collectors.toList());
        List<String> allocationUserList = customerDistributionList.stream().map(map1 -> map1.get("salesAttributionId").toString()).collect(Collectors.toList());
        List<Map> mapList = AverageDataUtil.averageData(projectClueIdList, allocationUserList);
        List<Map> dataList = new ArrayList<>();
        for (Map maps : mapList) {
            //查询置业顾问信息
            Iterator<String> iter = maps.keySet().iterator();
            while (iter.hasNext()) {
                //获取key
                String key = iter.next();//专员
                Map u = customerDistributionMaps.get(key).get(0);
                List<String> value = (List) maps.get(key);//客户
                value.forEach(s -> {
                    System.out.println("********* => " + key + " " + s);
                    Map c = customerMaps.get(s).get(0);

                    if (ObjectUtils.isNotEmpty(c.get("phoneToCall"))){
                        Map data = new HashMap<>();
                        data.put("name", String.valueOf(c.get("companyName")));
                        data.put("source", 1);
                        data.put("mobile", String.valueOf(c.get("phoneToCall")));
//                        data.put("level", String.valueOf(c.get("level")));
                        data.put("cid", s);
                        data.put("uid", String.valueOf(u.get("salesAttributionId")));
                        data.put("sid", String.valueOf(u.get("jobOrgID")));
                        data.put("did", String.valueOf(u.get("commonJobID")));
                        data.put("wy_type", 2);
                        dataList.add(data);
                    }
                });
            }
        }
        return ResultBody.success(dataList);
    }

    @Override
    public ResultBody addDict(Map map) {
        List<Map> addList = new ArrayList<>();
        Map addMap = new HashMap();
        String id = UUID.randomUUID().toString();
        addMap.put("id", id);
        addMap.put("pid", -1);
        addMap.put("listIndex", 1);
        addMap.put("type", 0);
        addMap.put("levels", 0);
        addMap.put("value", "edsshy");
        addMap.put("name", "企业数据库所属行业");
        addList.add(addMap);
        List<Map> list = (List<Map>) map.get("data");
        if (CollectionUtils.isNotEmpty(list)) {
            flattenList(list, id, addList);
        }
        int a = enterpriseDatabaseDao.addDict(addList);
        return ResultBody.success("导入成功："+a+"条");
    }

    private static void flattenList(List<Map> list, String parentId, List<Map> addList) {
        int i = 1;
        for (Map map : list) {
            Map addMap = new HashMap();
            String id = UUID.randomUUID().toString();
            String value = String.valueOf(map.get("value"));
            String name = String.valueOf(map.get("name"));
            addMap.put("id", id);
            addMap.put("pid", parentId);
            addMap.put("listIndex", i);
            addMap.put("type", 1);
            addMap.put("levels", 1);
            addMap.put("value", value);
            addMap.put("name", name);
            addList.add(addMap);
            i = i + 1;

            List<Map> childList = (List<Map>) map.get("childList");
            if (CollectionUtils.isNotEmpty(childList)) {
                flattenList(childList, id, addList);
            }
        }
    }

    @Override
    public ResultBody getEdIndustryOne() {
        List<Map> dictList = enterpriseDatabaseDao.getEdIndustryOne();
        //获取集合的value值 查询子级数据
        List<String> idsTwo = dictList.stream().map(map -> map.get("id").toString()).collect(Collectors.toList());
        List<Map> dictTwoList = enterpriseDatabaseDao.getEdIndustryTwo(idsTwo);
        //将数据按照父级ID分组
        Map<String, List<Map>> dictTwoListMap = dictTwoList.stream().collect(Collectors.groupingBy(map1 -> map1.get("pid").toString()));

        List<String> idsThree = dictTwoList.stream().map(map -> map.get("id").toString()).collect(Collectors.toList());
        List<Map> dictThreeList = enterpriseDatabaseDao.getEdIndustryTwo(idsThree);
        //将数据按照父级ID分组
        Map<String, List<Map>> dictThreeListMap = dictThreeList.stream().collect(Collectors.groupingBy(map1 -> map1.get("pid").toString()));

        List<String> idsFour = dictThreeList.stream().map(map -> map.get("id").toString()).collect(Collectors.toList());
        List<Map> dictFourList = enterpriseDatabaseDao.getEdIndustryTwo(idsFour);
        //将数据按照父级ID分组
        Map<String, List<Map>> dictFourListMap = dictFourList.stream().collect(Collectors.groupingBy(map1 -> map1.get("pid").toString()));

        for (Map d:dictList) {
            List<Map> c = dictTwoListMap.get(d.get("id").toString());
            if (c!=null && c.size()>0){
                for (Map mm:c) {
                    List<Map> children = dictThreeListMap.get(mm.get("id").toString());
                    if (children!=null && children.size()>0){
                        for (Map ss:children) {
                            List<Map> children1 = dictFourListMap.get(ss.get("id").toString());
                            if (children1!=null && children1.size()>0){
                                ss.put("children",children1);
                            }
                        }
                        mm.put("children",children);
                    }
                }
            }
            if(c!=null && c.size()>0){
                d.put("children",c);
            }
        }
        return ResultBody.success(dictList);
    }

    @Override
    public ResultBody edCustomerDataMatch(Map map) {
        String customerName = String.valueOf(map.get("customerName"));
        List<Map> reList = new ArrayList<>();
        if (StringUtils.isEmpty(customerName)){
            return ResultBody.success(reList);
        }
        //根据名称从es查询数据 获取名称和文档id
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            String indexName = "ed_customer_info";
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must(QueryBuilders.matchPhraseQuery("field_value", customerName));
            boolQuery.must(QueryBuilders.matchPhraseQuery("common_field_configuration_id", commonFieldId));
            sourceBuilder.query(boolQuery);
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                System.out.println("查询到文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
                EdCustomerInfoVo edCustomerInfoVo = JSONObject.parseObject(hit.getSourceAsString(), EdCustomerInfoVo.class);
                Map reMap = new HashMap();
                reMap.put("id", edCustomerInfoVo.getCustomerId());
                reMap.put("value", edCustomerInfoVo.getFieldValue());
                reMap.put("dataSourcesName", edCustomerInfoVo.getDataSourcesName());
                reList.add(reMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBody.success(reList);
    }

    @Override
    public ResultBody edCustomerDataToZsb(Map map) {
        String customerId = String.valueOf(map.get("id"));
        if (StringUtils.isEmpty(customerId)){
            return ResultBody.error(400,"数据ID不能为空");
        }
        //获取通用字段的配置 并按照通用字段配置ID分组获取通用字段配置信息
        List<EdCommonFieldConfigurationVo> edCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationList(new HashMap<>());
        Map<String, EdCommonFieldConfigurationVo> edCommonFieldConfigurationMap = edCommonFieldConfigurationList.stream().collect(Collectors.toMap(EdCommonFieldConfigurationVo::getId, edCommonFieldConfigurationVo -> edCommonFieldConfigurationVo));
        Map reMap = new HashMap();
        List<EdCustomerInfoVo> edCustomerInfoList = new ArrayList<>();
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            String indexName = "ed_customer_info";
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must(QueryBuilders.matchPhraseQuery("customer_id", customerId));
            sourceBuilder.size(10000);
            sourceBuilder.query(boolQuery);
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse1 = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits1 = searchResponse1.getHits();
            for (SearchHit hit1 : hits1) {
                System.out.println("查询到文档，ID: " + hit1.getId() + ", 内容: " + hit1.getSourceAsString());
                EdCustomerInfoVo edCustomerInfoVo = JSONObject.parseObject(hit1.getSourceAsString(), EdCustomerInfoVo.class);
                edCustomerInfoList.add(edCustomerInfoVo);
//                String commonFieldConfigurationId = edCustomerInfoVo.getCommonFieldConfigurationId();
//                String zsbFieldCode = edCommonFieldConfigurationMap.get(commonFieldConfigurationId).getFieldToZsbCode();
//                informationVO = this.edCustomerInfoToZsb(informationVO,zsbFieldCode,edCustomerInfoVo);
            }
            reMap = this.esDataToZsb(edCommonFieldConfigurationMap,edCustomerInfoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBody.success(reMap);
    }

    /**
     * 获取登录人权限内可分配客户的项目
     * */
    @Override
    public ResultBody getGlAllocationPropertyConsultantPro(Map map) {
        map.put("isManager", "0");
        //获取当前登录人是否存在可分配客户的权限
        List<Map> list = enterpriseDatabaseDao.getAllocationAllCustomerUserOrgInfo(SecurityUtils.getUserId());
        if(org.springframework.util.CollectionUtils.isEmpty(list)){
            return ResultBody.error(-1000_01,"暂无分配客户权限！");
        }
        List<String> proList = new ArrayList<>();//项目总监
        List<String> qyProList = new ArrayList<>();//区域总监
        //判断是否存在管理员权限
        final boolean[] isManager = {false};
        list.stream().forEach(x->{
            if("10001".equals(x.get("jobCode"))){
                isManager[0] = true;
            }else if("zszj".equals(x.get("jobCode")) || "yxjl".equals(x.get("jobCode"))){
                proList.add(x.get("projectId")+"");
            }else if("qyzszj".equals(x.get("jobCode"))){
                qyProList.add(x.get("projectId")+"");
            }else if ("qyfz".equals(x.get("jobCode")) || "qyyxjl".equals(x.get("jobCode"))){
                String comGUID = x.get("areaId")+"";
                proList.addAll(projectCluesDao.getProListD(comGUID));
                qyProList.addAll(projectCluesDao.getProList(comGUID));
            }
        });
        //获取权限内的权限专员 按区域 项目 团队 专员 分组
        if(isManager[0]){//管理员 可分配全系统人员
            map.put("isManager", "1");
        }else {//按权限查询
            List<String> pList = new ArrayList<>();
            pList.addAll(proList);
            pList.addAll(qyProList);
            map.put("pList", pList);
        }
        List<Map> reList = projectCluesDao.getGlAllocationPropertyConsultantPro(map);
        return ResultBody.success(new PageInfo<>(reList));
    }

    @Override
    public ResultBody selectEdCustomerInfo(String customerId) {
        EdCustomerInfoVo map = new EdCustomerInfoVo();
        map.setCustomerId(customerId);
        //获取企业数据库通用字段配置表所有被是否显示该字段为否的字段Id
        List<String> fieldShowList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationShowList();
        //根据企业数据库ID和通用配置是否展示获取企业数据信息
        map.setFieldList(fieldShowList);
        //ES 条件查询
        List<EdCustomerInfoVo> edCustomerInfoVo = new ArrayList<>();
        String updateTime = "";
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            String indexName = "ed_customer_info";
            // 创建搜索请求并指定索引
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.size(10000);

            // 构建布尔查询
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 添加 customer_id 相等的条件
            boolQuery.must(QueryBuilders.matchPhraseQuery("customer_id", map.getCustomerId()));

            // 添加 common_field_configuration_id 在指定列表中的条件
            if (!map.getFieldList().isEmpty()) {
                // 统一大小写
                List<String> lowerCaseCustomerIds = map.getFieldList().stream().map(String::toLowerCase).collect(Collectors.toList());
                // 使用 keyword 子字段进行查询
                boolQuery.must(QueryBuilders.termsQuery("common_field_configuration_id.keyword", lowerCaseCustomerIds));
            }

            // 设置查询条件
            sourceBuilder.query(boolQuery);

            // 设置搜索源构建器到搜索请求
            searchRequest.source(sourceBuilder);

            // 执行搜索请求
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            // 遍历搜索结果并打印所需字段
            for (SearchHit hit : hits) {
                EdCustomerInfoVo edCustomerInfoVo1 = JSONObject.parseObject(hit.getSourceAsString(), EdCustomerInfoVo.class);
                //判断当前数据的创建时间和编辑时间是否都存在
                if (StringUtils.isEmpty(edCustomerInfoVo1.getUpdateTime())){
                    if (StringUtils.isEmpty(updateTime)){
                        updateTime = edCustomerInfoVo1.getCreateTime();
                    }else {
                        //判断当前数据的更新时间是否更晚 如果更晚 取这个时间
                        if (edCustomerInfoVo1.getCreateTime().compareTo(updateTime)>0){
                            updateTime = edCustomerInfoVo1.getCreateTime();
                        }
                    }
                }else {
                    if (StringUtils.isEmpty(updateTime)){
                        updateTime = edCustomerInfoVo1.getUpdateTime();
                    }else {
                        //判断当前数据的更新时间是否更晚 如果更晚 取这个时间
                        if (edCustomerInfoVo1.getUpdateTime().compareTo(updateTime)>0){
                            updateTime = edCustomerInfoVo1.getUpdateTime();
                        }
                    }
                }
                edCustomerInfoVo.add(edCustomerInfoVo1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将查询结果edCustomerInfoVo 按通用字段id分组
        Map<String, List<EdCustomerInfoVo>> edCustomerInfoMap = edCustomerInfoVo.stream().collect(Collectors.groupingBy(EdCustomerInfoVo::getCommonFieldConfigurationId));

        //获取通用字段的配置 并按照通用字段配置ID分组获取通用字段配置信息
        List<EdCommonFieldConfigurationVo> edCommonFieldConfigurationList = enterpriseDatabaseDao.selectEdCommonFieldConfigurationList(new HashMap<>());
        Map<String, EdCommonFieldConfigurationVo> edCommonFieldConfigurationMap = edCommonFieldConfigurationList.stream().collect(Collectors.toMap(EdCommonFieldConfigurationVo::getId, edCommonFieldConfigurationVo -> edCommonFieldConfigurationVo));
        //主信息
        Map reMap = new HashMap<>();
        List<String> tag = new ArrayList<>();
        //通讯信息
        Map concatInfo = new HashMap<>();
        final String[] phoneToCall = {""};
        List<Map> concatInfoList = new ArrayList<>();
        edCustomerInfoMap.forEach((k,v)->{
            EdCommonFieldConfigurationVo pz = edCommonFieldConfigurationMap.get(k);
            //存在去重合并的数据 将fieldValue 拼接
            String fieldValue = v.stream().map(EdCustomerInfoVo::getFieldValue).collect(Collectors.joining("，|"));
            String firstFieldValue = "";
            if (CollectionUtils.isNotEmpty(v)){
                firstFieldValue = v.get(0).getFieldValue();
            }
            if (k.equals(commonFieldId)){//公司名称
                reMap.put("customerName",firstFieldValue);
            }
            if (k.equals(registrationStatusFieldId)){//营业状态
                reMap.put("registrationStatus",firstFieldValue);
            }
            if (k.equals(companyTypeFieldId) || k.equals(companyScaleFieldId)){// 公司规模 企业类型
                tag.add(firstFieldValue);
            }
            if (k.equals(companyScaleFieldId)){
                reMap.put("companyScale",firstFieldValue);
            }
            if (k.equals(legalPersonFieldId)){//法定代表人
                reMap.put("legalPerson",firstFieldValue);
            }
            if (k.equals(registeredCapitalFieldId)){// 注册资本
                reMap.put("registeredCapital",firstFieldValue);
            }
            if (k.equals(establishYearsFieldId)){// 成立日期
                String value = "";
                if (fieldValue.contains("-")){//包含特殊字符- 格式为 2020-01-01
                    try {
                        value = sd.format(sd.parse(fieldValue));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }else {//不包含特殊字符 格式为 2020/1/1
                    value = sd.format(convertExcelDateToJavaDate(Long.parseLong(fieldValue)));
                }
                reMap.put("establishmentDate",value);
            }
            if (k.equals(insuredPersonFieldId)){//参保人数
                reMap.put("insuredPerson",firstFieldValue);
            }
            if (k.equals(annualRevenueFieldId)){//年营业额
                reMap.put("annualRevenue",firstFieldValue);
            }
            if (k.equals(staffSizeFieldId)){//人员规模
                reMap.put("staffSize",firstFieldValue);
            }
            if (k.equals(provinceFieldId)){//地址
                reMap.put("address",firstFieldValue);
            }
            if (k.equals(webAddressFieldId)){//网址
                reMap.put("webAddress",firstFieldValue);
            }
            if (k.equals(emailFieldId)){//邮箱
                reMap.put("email",firstFieldValue);
            }
            if (k.equals(postCodeFieldId)){//邮编
                reMap.put("postCode",firstFieldValue);
            }
            if (pz.getIsCommunicationField().equals("1")){//通讯字段
                v.stream().forEach(vo->{
                    Map c = new HashMap();
                    c.put("number",vo.getFieldValue());
                    c.put("source",vo.getDataSourcesName());
                    concatInfoList.add(c);
                    if (StringUtils.isEmpty(phoneToCall[0])){
                        phoneToCall[0] = vo.getFieldValue();
                    }else {
                        phoneToCall[0] = phoneToCall[0]+";"+vo.getFieldValue();
                    }
                });
            }
        });
        //主要信息
        Map companyInfo = new HashMap();
        //从配置获取主要信息 将配置按照fieldTypeCode+','+fieldTypeName 分组 组装主要信息
        Map<String, List<EdCommonFieldConfigurationVo>> fieldTypeMap = edCommonFieldConfigurationList.stream().filter(x->StringUtils.isNotEmpty(x.getFieldTypeCode())).collect(Collectors.groupingBy(x->x.getFieldTypeCode()+","+x.getFieldTypeName()));
        fieldTypeMap.forEach((k,v)->{
            String[] info = k.split(",");
            String title = info[1];
            String name = info[0];
            List<Map> information = new ArrayList<>();
            v.stream().forEach(x->{
                Map map1 = new HashMap();
                map1.put("label",x.getFieldName());
                //从edCustomerInfoMap 获取对应数据
                List<EdCustomerInfoVo> vo = edCustomerInfoMap.get(x.getId());
                if (CollectionUtils.isEmpty(vo)){
                    map1.put("value"," -- ");
                }else {
                    map1.put("value",vo.stream().map(EdCustomerInfoVo::getFieldValue).collect(Collectors.joining(",")));
                }
                information.add(map1);
            });
            Map map1 = new HashMap();
            map1.put("title",title);
            map1.put("name",name);
            map1.put("information",information);
            companyInfo.put(name,map1);
        });

        reMap.put("updateTime",updateTime);
        reMap.put("tag",tag);
        reMap.put("companyInfo",companyInfo);
        concatInfo.put("total",concatInfoList.size());
        concatInfo.put("phoneList",concatInfoList);
        reMap.put("concatInfo",concatInfo);
        reMap.put("phoneToCall",phoneToCall[0]);
        return ResultBody.success(reMap);
    }
    @Override
    public ResultBody getTimeoutWarning() {
        List<OverdueUnconsumedProjectRecord> timeoutWarning = enterpriseDatabaseDao.getTimeoutWarning();

        return ResultBody.success(timeoutWarning);
    }

    @Override
    public void updateTimeoutWarning(String id) {
        enterpriseDatabaseDao.updateTimeoutWarning(id);
    }
    private Map esDataToZsb(Map<String, EdCommonFieldConfigurationVo> edCommonFieldConfigurationMap,List<EdCustomerInfoVo> edCustomerInfoList){
        Map map = new HashMap();
        for (EdCustomerInfoVo edCustomerInfoVo:edCustomerInfoList) {
            String zsbFieldCode = edCommonFieldConfigurationMap.get(edCustomerInfoVo.getCommonFieldConfigurationId()).getFieldToZsbCode();
            if (StringUtils.isNotEmpty(zsbFieldCode)){
                if (zsbFieldCode.contains("_")){
                    //格式化转换驼峰命名
                    map.put(StringUtils.toCamelCase(zsbFieldCode),edCustomerInfoVo.getFieldValue());
                }else {
                    //格式化首字母转换小写
                    map.put(StringUtils.uncapitalize(zsbFieldCode),edCustomerInfoVo.getFieldValue());
                }
            }
        }
        return map;
    }

    private InformationVO edCustomerInfoToZsb(InformationVO informationVO,String zsbFieldCode,EdCustomerInfoVo edCustomerInfoVo){
        if ("CustomerName".equals(zsbFieldCode)){//公司名称
            informationVO.setCustomerName(edCustomerInfoVo.getFieldValue());
        }
        if ("CustomerMobile".equals(zsbFieldCode)){//联系人手机号
            informationVO.setCustomerMobile(edCustomerInfoVo.getFieldValue());
        }
        if ("Contacts".equals(zsbFieldCode)){//联系人
            informationVO.setContacts(edCustomerInfoVo.getFieldValue());
        }
        if ("SourceMode".equals(zsbFieldCode)){//客户来源
            informationVO.setSourceMode(edCustomerInfoVo.getFieldValue());
        }
        return informationVO;
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) throws Exception {
        T bean = beanClass.getDeclaredConstructor().newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (map.containsKey(key)) {
                Object value = map.get(key);
                Method setter = property.getWriteMethod();
                if (setter != null) {
                    setter.invoke(bean, value);
                }
            }
        }
        return bean;
    }

    private Map<String, Object> dataToESMap(EdCustomerInfoTeVo edCustomerInfoTeVo){
        Map<String, Object> doc = new HashMap<>();
        //将上面的字段映射到ES
        doc.put("creator", edCustomerInfoTeVo.getImportUserId());
        doc.put("create_time", sf.format(edCustomerInfoTeVo.getImportTime()));
        doc.put("field_code", edCustomerInfoTeVo.getFieldCode());
        doc.put("common_field_configuration_id", edCustomerInfoTeVo.getCommonFieldConfigurationId());
        doc.put("import_user", edCustomerInfoTeVo.getImportUserId());
        doc.put("data_sources_name", edCustomerInfoTeVo.getDataSourcesName());
        doc.put("field_name", edCustomerInfoTeVo.getFieldName());
        doc.put("data_sources_code", edCustomerInfoTeVo.getDataSourcesCode());
        doc.put("update_time", null);
        doc.put("expire_date", edCustomerInfoTeVo.getExpireDate());
        doc.put("field_value", edCustomerInfoTeVo.getFieldValue());
        doc.put("updator", null);
        doc.put("examine_pre_type", edCustomerInfoTeVo.getExaminePreType());
        doc.put("ID", UUID.randomUUID().toString());
        doc.put("customer_id", edCustomerInfoTeVo.getCustomerId());
        doc.put("isdel", 0);
        doc.put("status", 1);
        return doc;
    }

    public static Date convertExcelDateToJavaDate(long excelDate) {
        // Excel 的日期起始于 1899 年 12 月 30 日
        Calendar calendar = Calendar.getInstance();
        calendar.set(1899, Calendar.DECEMBER, 30);

        // 加上 Excel 日期序列值所代表的天数
        calendar.add(Calendar.DAY_OF_YEAR, (int) excelDate);

        return calendar.getTime();
    }
}
