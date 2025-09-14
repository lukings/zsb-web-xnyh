package cn.visolink.utils;

//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luqianqian
 * @Description: elasticsearch工具类
 * @date 2025/1/5 18:41
 */
@Configuration
public class ElasticsearchUtil {
//    @Value("${elasticsearch.host}")
    private String host;
//    @Value("${elasticsearch.port}")
    private Integer port;

//    @Bean
//    public RestHighLevelClient client() {
//        return new RestHighLevelClient(RestClient.builder(
//                new HttpHost(host, port, "http")
//        ));
//    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
