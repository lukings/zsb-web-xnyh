package cn.visolink.system.excel.util;

import cn.visolink.system.excel.model.ReportCustomerForm;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量地理编码工具类 - 支持多线程并发处理地址转经纬度和经纬度转地址
 */
public class BatchGeocodingUtil {

    private static final String API_KEY = "krHOOUhCZ23jxq5GdKKYuDTve6EaFy52";
    private static final String ADDRESS_TO_COORDINATES_URL = "http://api.map.baidu.com/geocoding/v3/?output=json&location=showLocation";
    private static final String COORDINATES_TO_ADDRESS_URL = "http://api.map.baidu.com/reverse_geocoding/v3/?output=json&coordtype=BD09";

    // 线程池配置
    private static final int CORE_THREADS = 200;
    private static final int MAX_THREADS = 300;
    private static final int QUEUE_SIZE = 1000;
    private static final int KEEP_ALIVE_TIME = 60;

    // 连接池配置
    private static final int MAX_CONNECTIONS = 500;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 300;

    // 限流器配置（与API的QPS限制匹配）
    private static final RateLimiter rateLimiter = RateLimiter.create(500.0);

    // 创建高性能HTTP客户端
    private static final HttpClient httpClient = createHttpClient();

    // 创建线程池
    private static final ExecutorService executor = new ThreadPoolExecutor(
            CORE_THREADS,
            MAX_THREADS,
            KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(QUEUE_SIZE),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 批量处理地址转经纬度
     * @param customerForms 客户表单列表
     * @param timeout 超时时间（秒）
     * @return 处理结果统计
     */
    public static Map<String, Integer> batchAddressToCoordinates(List<ReportCustomerForm> customerForms, int timeout) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        // 创建计数器，用于等待所有任务完成
        CountDownLatch latch = new CountDownLatch(customerForms.size());

        // 提交任务到线程池
        for (ReportCustomerForm form : customerForms) {
            if (StringUtils.isNotEmpty(form.getCustomerAddress()) &&
                    (StringUtils.isEmpty(form.getLongitude()) || StringUtils.isEmpty(form.getLatitude()))) {

                executor.submit(() -> {
                    try {
                        // 获取令牌（控制QPS）
                        rateLimiter.acquire();

                        // 调用API获取经纬度
                        float[] coordinates = getCoordinatesWithRetry(form.getCustomerAddress());

                        if (coordinates != null) {
                            form.setLongitude(String.valueOf(coordinates[0]));
                            form.setLatitude(String.valueOf(coordinates[1]));
                            successCount.incrementAndGet();
                        } else {
                            failedCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failedCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            } else {
                // 如果已有经纬度或没有地址，直接跳过
                latch.countDown();
            }
        }

        try {
            // 等待所有任务完成或超时
            latch.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, Integer> result = new ConcurrentHashMap<>();
        result.put("success", successCount.get());
        result.put("failed", failedCount.get());
        return result;
    }

    /**
     * 批量处理经纬度转地址
     * @param customerForms 客户表单列表
     * @param timeout 超时时间（秒）
     * @return 处理结果统计
     */
    public static Map<String, Integer> batchCoordinatesToAddress(List<ReportCustomerForm> customerForms, int timeout) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        // 创建计数器，用于等待所有任务完成
        CountDownLatch latch = new CountDownLatch(customerForms.size());

        // 提交任务到线程池
        for (ReportCustomerForm form : customerForms) {
            if (StringUtils.isNotEmpty(form.getLongitude()) && StringUtils.isNotEmpty(form.getLatitude()) &&
                    StringUtils.isEmpty(form.getCustomerAddress())) {

                executor.submit(() -> {
                    try {
                        // 获取令牌（控制QPS）
                        rateLimiter.acquire();

                        // 调用API获取地址
                        String address = getAddressWithRetry(Float.parseFloat(form.getLatitude()),
                                Float.parseFloat(form.getLongitude()));

                        if (StringUtils.isNotEmpty(address)) {
                            form.setCustomerAddress(address);
                            successCount.incrementAndGet();
                        } else {
                            failedCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failedCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            } else {
                // 如果已有地址或没有经纬度，直接跳过
                latch.countDown();
            }
        }

        try {
            // 等待所有任务完成或超时
            latch.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, Integer> result = new ConcurrentHashMap<>();
        result.put("success", successCount.get());
        result.put("failed", failedCount.get());
        return result;
    }

    /**
     * 批量通过客户名获取API地址和经纬度，并写入ReportCustomerForm
     * @param customerForms 客户表单列表
     * @param timeout 超时时间（秒）
     */
    public static void batchNameToAddress(List<ReportCustomerForm> customerForms, int timeout) {
        CountDownLatch latch = new CountDownLatch(customerForms.size());
        for (ReportCustomerForm form : customerForms) {
            executor.submit(() -> {
                try {
                    rateLimiter.acquire();
                    String result = cn.visolink.system.excel.util.AMapUtils.nameToAddress(form.getCustomerName());
                    if (result != null && !result.isEmpty()) {
                        // 期望格式：地址_{"lat":xx,"lng":xx}
                        String[] arr = result.split("_");
                        if (arr.length == 2) {
                            form.setApiCustomerAddress(arr[0]);
                            String location = arr[1];
                            // 解析{"lat":xx,"lng":xx}
                            location = location.replace("{", "").replace("}", "");
                            String[] kvs = location.split(",");
                            for (String kv : kvs) {
                                String[] pair = kv.split(":");
                                if (pair.length == 2) {
                                    String key = pair[0].replaceAll("\"", "").trim();
                                    String value = pair[1].replaceAll("\"", "").trim();
                                    if ("lat".equals(key)) {
                                        form.setApiLatitude(value);
                                    } else if ("lng".equals(key)) {
                                        form.setApiLongitude(value);
                                    }
                                }
                            }
                        } else {
                            form.setApiCustomerAddress(result);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 带重试的坐标获取方法
    private static float[] getCoordinatesWithRetry(String address) {
        for (int i = 0; i < 3; i++) {
            try {
                float[] result = getCoordinates(address);
                if (result != null) return result;
            } catch (Exception e) {
                try {
                    Thread.sleep(500 * (i + 1)); // 指数退避
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return null;
    }

    // 带重试的地址获取方法
    private static String getAddressWithRetry(float lat, float lng) {
        for (int i = 0; i < 3; i++) {
            try {
                String result = getAddress(lat, lng);
                if (result != null) return result;
            } catch (Exception e) {
                try {
                    Thread.sleep(500 * (i + 1)); // 指数退避
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return null;
    }

    // 地址转坐标实现
    private static float[] getCoordinates(String address) throws IOException {
        try {
            String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
            String url = ADDRESS_TO_COORDINATES_URL + "&ak=" + API_KEY + "&address=" + encodedAddress;

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
            }
        } catch (UnsupportedEncodingException e) {
            // 处理编码异常
        }
        return null;
    }

    // 坐标转地址实现
    private static String getAddress(float lat, float lng) throws IOException {
        try {
            String url = COORDINATES_TO_ADDRESS_URL + "&ak=" + API_KEY + "&location=" + lat + "," + lng;

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
                    JSONObject resultObj = json.getJSONObject("result");
                    return resultObj.getString("formatted_address");
                }
            }
        } catch (Exception e) {
            // 处理异常
        }
        return null;
    }

    // 创建优化的HTTP客户端
    private static HttpClient createHttpClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, (chain, authType) -> true);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();

            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(MAX_CONNECTIONS);
            cm.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

            return HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException("创建HTTP客户端失败", e);
        }
    }

    // 关闭线程池
    public static void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}