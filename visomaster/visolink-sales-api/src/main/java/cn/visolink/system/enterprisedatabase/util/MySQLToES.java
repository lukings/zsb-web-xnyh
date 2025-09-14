package cn.visolink.system.enterprisedatabase.util;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luqianqian
 * @Description: mysql操作es工具类
 * @date 2025/2/27 22:38
 */

public class MySQLToES {

    @Value("${spring.datasource.druid.url}")
    private static String MYSQL_URL;
    @Value("${spring.datasource.druid.username}")
    private static String MYSQL_USER;
    @Value("${spring.datasource.druid.password}")
    private static String MYSQL_PASSWORD;
    // ES 连接信息
    @Value("${elasticsearch.host}")
    private static String ES_HOST;
    @Value("${elasticsearch.port}")
    private static int ES_PORT;
    private static final String ES_SCHEME = "http";
    private static final String ES_INDEX = "ed_customer_info";

    public static void main(String[] args) {
        //本地调试
        ES_HOST = "192.168.103.62";
        ES_PORT = 9200;
        MYSQL_URL = "jdbc:mysql://122.228.177.99:23306/test-wy?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false&useCursorFetch=true&defaultFetchSize=500&allowMultiQueries=true";
        MYSQL_USER = "root";
        MYSQL_PASSWORD = "wy@123";

        // 1. 将 MySQL 数据导入 ES
//        importDataFromMySQLToES();

        // 2. 分页查询示例
//        paginatedSearch(2, 1);

        // 3. 条件查询示例
        conditionalSearch("column_name", "your_value");

        // 4. 新增数据示例
//        addNewData();

        // 5. 更新数据示例
//        updateData("your_document_id");
    }

    /**
     * 将 MySQL 数据导入 ES
     */
    public static void importDataFromMySQLToES() {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)));
             Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select ID,customer_id,common_field_configuration_id, field_code,field_name,field_value,data_sources_code,data_sources_name, examine_pre_type,DATE_FORMAT(expire_date,'%Y-%m-%d %H:%i:%s') expire_date,import_user,status,isdel,DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%s') create_time,creator, DATE_FORMAT(update_time,'%Y-%m-%d %H:%i:%s') update_time,updator from  ed_customer_info")) {

            while (resultSet.next()) {
                Map<String, Object> data = new HashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    data.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                IndexRequest indexRequest = new IndexRequest(ES_INDEX).source(data);
                IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分页查询
     * @param pageSize 每页数量
     * @param pageNumber 页码
     */
    public static void paginatedSearch(int pageSize, int pageNumber) {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            SearchRequest searchRequest = new SearchRequest(ES_INDEX);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.from((pageNumber - 1) * pageSize);
            sourceBuilder.size(pageSize);
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 条件查询
     * @param field 查询字段
     * @param value 查询值
     */
    public static void conditionalSearch(String field, String value) {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            SearchRequest searchRequest = new SearchRequest(ES_INDEX);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            MatchQueryBuilder matchQuery = QueryBuilders.matchQuery(field, value);
            sourceBuilder.query(matchQuery);
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增数据
     */
    public static void addNewData() {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            Map<String, Object> data = new HashMap<>();
            data.put("column1", "value1");
            data.put("column2", "value2");
            IndexRequest indexRequest = new IndexRequest(ES_INDEX).source(data);
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新数据
     * @param docId 文档 ID
     */
    public static void updateData(String docId) {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ES_HOST, ES_PORT, ES_SCHEME)))) {
            UpdateRequest updateRequest = new UpdateRequest(ES_INDEX, docId);
            Map<String, Object> updateDoc = new HashMap<>();
            updateDoc.put("column1", "new_value1");
            updateRequest.doc(updateDoc);
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
