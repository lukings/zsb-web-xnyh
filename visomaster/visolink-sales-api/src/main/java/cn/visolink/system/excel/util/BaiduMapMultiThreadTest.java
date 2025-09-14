package cn.visolink.system.excel.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BaiduMapMultiThreadTest {
    private static final String API_KEY = "krHOOUhCZ23jxq5GdKKYuDTve6EaFy52";
    private static final String API_URL = "https://api.map.baidu.com/geocoding/v3";

    // 线程池配置
    private static final int CORE_THREADS = 200;
    private static final int MAX_THREADS = 300;
    private static final int QUEUE_SIZE = 1000;

    // 连接池配置
    private static final int MAX_CONNECTIONS = 500;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 300;

    // 限流器配置（与API的QPS限制匹配）
    private static final RateLimiter rateLimiter = RateLimiter.create(1000.0);

    // 创建高性能HTTP客户端
    private static final HttpClient httpClient = createHttpClient();

    // 创建线程池
    private static final ExecutorService executor = new ThreadPoolExecutor(
            CORE_THREADS,
            MAX_THREADS,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(QUEUE_SIZE),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void main(String[] args) throws InterruptedException {
        List<String> addresses = loadAddresses(); // 加载地址数据
        int totalRequests = addresses.size();
        CountDownLatch latch = new CountDownLatch(totalRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // 提交任务
        for (String address : addresses) {
            executor.submit(() -> {
                try {
                    // 获取令牌（控制QPS）
                    rateLimiter.acquire();

                    float[] result = getCoordinatesWithRetry(address);
                    if (result != null) {
                        successCount.incrementAndGet();
                    } else {
                        failedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failedCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有任务完成
        latch.await();
        executor.shutdown();

        long totalTime = System.currentTimeMillis() - startTime;

        // 输出统计结果
        System.out.println("===== 测试结果 =====");
        System.out.println("总请求数: " + totalRequests);
        System.out.println("成功数: " + successCount.get());
        System.out.println("失败数: " + failedCount.get());
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均耗时: " + (totalTime / totalRequests) + "ms");
        System.out.println("实际QPS: " + (totalRequests * 1000 / totalTime));
    }

    // 带重试的坐标获取方法
    private static float[] getCoordinatesWithRetry(String address) {
        for (int i = 0; i < 3; i++) {
            try {
                float[] result = getCoordinates(address);
                if (result != null) return result;
            } catch (Exception e) {
                System.err.println("请求失败，重试 " + (i + 1) + "/3: " + e.getMessage());
                try {
                    Thread.sleep(500 * (i + 1)); // 指数退避
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return null;
    }

    // 优化后的坐标获取方法
    private static float[] getCoordinates(String address) throws IOException {
        try {
            String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
            String url = API_URL + "?ak=" + API_KEY + "&address=" + encodedAddress + "&output=json";

            HttpGet request = new HttpGet(url);

            // 设置超时配置
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(10000)
                    .build();
            request.setConfig(config);

            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                EntityUtils.consume(entity); // 确保释放资源

                JSONObject json = JSONObject.parseObject(result);

                // 百度API: status=0表示成功
                if (json.getIntValue("status") == 0) {
                    JSONObject location = json.getJSONObject("result").getJSONObject("location");
                    return new float[]{
                            location.getFloatValue("lng"),
                            location.getFloatValue("lat")
                    };
                }

                System.err.println("API错误: " + result + ", 地址: " + address);
            } else {
                System.err.println("HTTP错误: " + statusCode + ", 地址: " + address);
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("编码错误: " + e.getMessage());
        }
        return null;
    }

    // 创建优化的HTTP客户端
    private static HttpClient createHttpClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", new SSLConnectionSocketFactory(sslContext))
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            cm.setMaxTotal(MAX_CONNECTIONS);
            cm.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

            return HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException("创建HTTP客户端失败", e);
        }
    }

    // 加载地址数据（生成1000条相同地址）
    private static List<String> loadAddresses() {
        List<String> addresses = new ArrayList<>(1000);
        String testAddress = "清远市清新区太平镇龙湾工业区湾保二街2号";

        // 循环添加1000次相同地址
        for (int i = 0; i < 10000; i++) {
            addresses.add(testAddress);
        }

        return addresses;
    }
}