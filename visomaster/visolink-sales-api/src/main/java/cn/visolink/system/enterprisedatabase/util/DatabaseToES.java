package cn.visolink.system.enterprisedatabase.util;

import cn.visolink.system.enterprisedatabase.model.EdCustomerInfoVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luqianqian
 * @Description: 数据库连接ES
 * @date 2025/2/27 11:26
 */
public class DatabaseToES {
    // ES 连接信息
    @Value("${elasticsearch.host}")
    private static String ES_HOST;
    @Value("${elasticsearch.port}")
    private static int ES_PORT;
    private static final String ES_SCHEME = "http";

    // MySQL 连接信息
    @Value("${spring.datasource.druid.url}")
    private static String DB_URL;
    @Value("${spring.datasource.druid.username}")
    private static String DB_USER;
    @Value("${spring.datasource.druid.password}")
    private static String DB_PASSWORD;

    public static void main(String[] args) {
        //本地调试
        ES_HOST = "192.168.103.62";
//        ES_HOST = "192.168.46.66";
        ES_PORT = 9200;
        DB_URL = "jdbc:mysql://122.228.177.99:23306/test-wy?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true";
        DB_USER = "root";
        DB_PASSWORD = "wy@123";
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {

            // 从 MySQL 读取数据并添加到 ES
//            String tableName = "ed_customer_info";
//            List<Map<String, Object>> dataFromDB = readDataFromMySQL(tableName);
//            addDataToES(client, "ed_customer_info", dataFromDB);

            // 查询示例
//            searchES(client, "ed_customer_info");
//            searchES(client, "ed_customer_info_search");
//            searchESWithPagination(client, "ed_customer_info");//分页条件查询
//            searchESDistinct(client, "ed_customer_info");//去重分页查询

            // 更新示例，假设已知文档 ID
//            String docId = "VnmGW5UBayFeJReOTlNq";
//            updateES(client, "ed_customer_info", docId);

            //删除示例，假设已知文档 ID
//            deleteES(client, "ed_customer_info", "_Hk0mZUBayFeJReOHVMv");
//            deleteES(client, "ed_customer_info", "_Xk0mZUBayFeJReOHVNa");
//            deleteES(client, "ed_customer_info", "_nk0mZUBayFeJReOHVOF");
//            deleteES(client, "ed_customer_info", "_3k0mZUBayFeJReOHVOv");
//            deleteES(client, "ed_customer_info", "AHk0mZUBayFeJReOHVTZ");
//            deleteES(client, "ed_customer_info", "AXk0mZUBayFeJReOHlQC");
//            deleteES(client, "ed_customer_info", "Ank0mZUBayFeJReOHlQu");
//            deleteES(client, "ed_customer_info", "A3k0mZUBayFeJReOHlRY");
//            deleteES(client, "ed_customer_info", "BHk0mZUBayFeJReOHlSC");
//            deleteES(client, "ed_customer_info", "BXk0mZUBayFeJReOHlSt");
//            deleteES(client, "ed_customer_info", "Bnk0mZUBayFeJReOHlTX");
//            searchES(client, "ed_customer_info");

//            deleteAllES(client, "ed_customer_info");
//            deleteAllES(client, "ed_customer_info_search");

//            deleteErrorES(client, "ed_customer_info_search");
//            deleteErrorES(client, "ed_customer_info");

            //添加查询条件字段后设置值
//            updateESAfterQuery(client);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateESAfterQuery(RestHighLevelClient client) {
        try {
            List<String> ids = new ArrayList<>();
            ids.add("8d6608a8-a8fd-465e-ad03-660b9475a18c");
            ids.add("fd872e54-e344-418d-abe7-44924d8509d3");
            ids.add("d29784ba-282b-4716-8b3a-2d8937cf8141");
            // 创建搜索请求并指定索引
            SearchRequest searchRequest = new SearchRequest("ed_customer_info_search");
            searchRequest.source().query(QueryBuilders.matchAllQuery()).size(10000);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                System.out.println("查询到ed_customer_info_search文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
                String oldCustomerId = hit.getSourceAsMap().get("customer_id").toString();
                String docId = hit.getId();
                for (String id : ids){
                    SearchRequest searchRequest1 = new SearchRequest("ed_customer_info");
                    SearchSourceBuilder sourceBuilder1 = new SearchSourceBuilder();
                    BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
                    boolQuery1.must(QueryBuilders.matchPhraseQuery("customer_id", oldCustomerId));
                    boolQuery1.must(QueryBuilders.matchPhraseQuery("common_field_configuration_id", id));
                    sourceBuilder1.query(boolQuery1);
                    searchRequest1.source(sourceBuilder1);
                    SearchResponse searchResponse1 = client.search(searchRequest1, RequestOptions.DEFAULT);
                    SearchHits hits1 = searchResponse1.getHits();
                    for (SearchHit hit1 : hits1) {
                        System.out.println("查询到ed_customer_info文档，ID: " + hit1.getId() + ", 内容: " + hit1.getSourceAsString());
                        UpdateRequest request2 = new UpdateRequest("ed_customer_info_search" ,docId);
                        Map<String, Object> updateMap2 = new HashMap<>();
                        updateMap2.put(id, hit1.getSourceAsMap().get("field_value"));
                        request2.doc(updateMap2);
                        UpdateResponse response2 = client.update(request2, RequestOptions.DEFAULT);
                        System.out.println("文档已更新，版本: " + response2.getVersion());
//                        System.out.println("看看更新的数据 id"+id+"值："+updateMap2.get(id));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void deleteErrorES(RestHighLevelClient client, String indexName) {
        try {
            List<String> customerIds = new ArrayList<>();
//            customerIds.add("484c7bdc-7bf0-4b2a-9744-7b66fa5f26a6");
//            customerIds.add("d1d4d504-3b1d-4d4d-9a00-3ff759bcc9f2");
//            customerIds.add("5f0e7a66-b96f-4efc-b7cf-7a6a16967e55");
//            customerIds.add("778dadf5-5516-42e9-b7f5-1eac5e7c30d3");
            customerIds.add("f9802d34-643b-4c1f-9604-4aab2197cc50");

            // 创建搜索请求并指定索引
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
//                System.out.println("查询到文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
                String docId = hit.getId();
                DeleteRequest deleteRequest = new DeleteRequest(indexName, docId);
                DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
                System.out.println("Document deleted: " + deleteResponse.getResult());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void deleteAllES(RestHighLevelClient client, String edCustomerInfo) {
        try {
            SearchRequest searchRequest = new SearchRequest(edCustomerInfo);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.from(0);
            sourceBuilder.size(1000);
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                String docId = hit.getId();
                DeleteRequest deleteRequest = new DeleteRequest(edCustomerInfo, docId);
                DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
                System.out.println("Document deleted: " + deleteResponse.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从 MySQL 读取数据
    private static List<Map<String, Object>> readDataFromMySQL(String tableName) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select ID,customer_id,common_field_configuration_id, field_code,field_name,field_value,data_sources_code,data_sources_name, examine_pre_type,DATE_FORMAT(expire_date,'%Y-%m-%d %H:%i:%s') expire_date,import_user,status,isdel,DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%s') create_time,creator, DATE_FORMAT(update_time,'%Y-%m-%d %H:%i:%s') update_time,updator from " + tableName)) {
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
                }
                data.add(row);
            }
        }
        return data;
    }

    // 添加数据到 ES
    private static void addDataToES(RestHighLevelClient client, String index, List<Map<String, Object>> data) throws Exception {
        for (Map<String, Object> doc : data) {
            IndexRequest request = new IndexRequest(index);
            request.source(doc, XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            System.out.println("文档已添加，ID: " + response.getId());
        }
    }

    // 查询 ES 数据
    private static void searchES(RestHighLevelClient client, String index) throws Exception {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source().query(QueryBuilders.matchAllQuery()).size(10000);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            System.out.println("查询到文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
        }
    }
    private static void searchESDistinct(RestHighLevelClient client, String index) throws Exception {
        int page = 1;
        int size = 10;
        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 构建布尔查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 添加 examine_pre_type 和 import_user 条件
//        BoolQueryBuilder preTypeQuery = QueryBuilders.boolQuery();
//        preTypeQuery.should(QueryBuilders.termQuery("examine_pre_type", 1));
//        preTypeQuery.should(QueryBuilders.boolQuery()
//                .must(QueryBuilders.termQuery("examine_pre_type", 0))
//                .must(QueryBuilders.termQuery("import_user", "62011e0e-be4a-4d32-b73c-3fb0172643b6")));
//        boolQuery.filter(preTypeQuery);

        // 设置查询条件
        searchSourceBuilder.query(boolQuery);

        // 添加去重聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("customer_ids").field("customer_id.keyword"));

        // 执行搜索请求
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            System.out.println("查询到文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
        }

        // 处理聚合结果
        List<String> customerIds = new ArrayList<>();
        Terms terms = searchResponse.getAggregations().get("customer_ids");
        for (Terms.Bucket bucket : terms.getBuckets()) {
            customerIds.add(bucket.getKeyAsString());
        }

        // 实现分页逻辑
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, customerIds.size());
        customerIds = customerIds.subList(startIndex, endIndex);
        for (String customerId : customerIds){
            System.out.println("数据ID: " + customerId);
        }
    }

    //分页条件查询 ES 数据
    private static void searchESWithPagination(RestHighLevelClient client, String index) throws Exception {
        // 创建搜索请求并指定索引
        SearchRequest searchRequest = new SearchRequest(index);
        // 创建搜索源构建器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分页
        sourceBuilder.from((1 - 1) * 50);
        sourceBuilder.size(50);

        //条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //区分大小写
//        boolQuery.must(QueryBuilders.termQuery("field_value", "企查查"));
//        boolQuery.must(QueryBuilders.termQuery("field_name", "数据来源"));
        //不区分大小写
//        boolQuery.must(QueryBuilders.matchQuery("field_value", "企查查"));
//        boolQuery.must(QueryBuilders.matchQuery("field_name", "数据来源"));
        //不区分大小写 且作为一个整体进行匹配
//        boolQuery.must(QueryBuilders.matchPhraseQuery("field_value", "企查查"));
//        boolQuery.must(QueryBuilders.matchPhraseQuery("field_name", "数据来源"));
        sourceBuilder.query(boolQuery);

        // 将搜索源构建器设置到搜索请求中
        searchRequest.source(sourceBuilder);

        // 执行搜索请求
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 获取搜索结果
        SearchHits hits = searchResponse.getHits();

        // 遍历搜索结果并打印
        for (SearchHit hit : hits) {
            System.out.println("查询到文档，ID: " + hit.getId() + ", 内容: " + hit.getSourceAsString());
            deleteES(client, "ed_customer_info", hit.getId());
        }
    }

    // 更新 ES 数据
    private static void updateES(RestHighLevelClient client, String index, String docId) throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("updator", "62011e0e-be4a-4d32-b73c-3fb0172643b6");
        updateMap.put("update_time", "2025-03-03 14:32:25");

        UpdateRequest request = new UpdateRequest(index ,docId);
        request.doc(updateMap);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println("文档已更新，版本: " + response.getVersion());
    }

    private static void deleteES(RestHighLevelClient client, String index, String docId) throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest(index,docId);
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("删除文档，ID: " + deleteResponse.getId());
    }
}
