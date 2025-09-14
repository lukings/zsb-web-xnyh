package cn.visolink.system.excel.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AMapUtils {

    // 高德地图key
    public static final String KEY = "6dc239658cd94e2007b4f3c933106693";
    private static Pattern pattern = Pattern.compile("\"location\":\"(\\d+\\.\\d+),(\\d+\\.\\d+)\"");

    /**
     * 通过具体位置，获取对应地图上的坐标: 经度、纬度
     *
     * @param address
     * @return
     */
    public static double[] addressToGPS(String address) {
        try {
            String url = String .format("http://restapi.amap.com/v3/geocode/geo?&s=rsv3&address=%s&key=%s", address, KEY);
            URL myURL = null;
            URLConnection httpsConn = null;
            try {
                myURL = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            System.out.println(url);
            InputStreamReader insr = null;
            BufferedReader br = null;
            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理
            if (httpsConn != null) {
                insr = new InputStreamReader( httpsConn.getInputStream(), "UTF-8");
                br = new BufferedReader(insr);
                String data = "";
                String line = null;
                while((line= br.readLine())!=null){
                    data+=line;
                }
                Matcher matcher = pattern.matcher(data);
                if (matcher.find() && matcher.groupCount() == 2) {
                    double[] gps = new double[2];
                    gps[0] = Double.valueOf(matcher.group(1));
                    gps[1] = Double.valueOf(matcher.group(2));
                    return gps;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * @Description:  <百度开发者>在百度地图开发文档申请的key
     */
    final static String AK = "krHOOUhCZ23jxq5GdKKYuDTve6EaFy52";

    /**
     * @Description: 地理位置编码
     * @Author: xy丶
     */
    final static String ADDRESS_TO_LONGITUDEA_URL = "http://api.map.baidu.com/geocoding/v3/?output=json&location=showLocation";

    /**
     * @Description: 地理位置编码
     * @Author: xy丶
     * @retur: 返回地理位置信息
     */
    public static float[] AddressTolongitudea(String address) {
        String httpUrl = ADDRESS_TO_LONGITUDEA_URL + "&ak=" + AK + "&address="+ address;
        System.out.println("请求url:" + httpUrl);
        // 创建默认http连接
        HttpClient client = HttpClients.createDefault();
        // 创建一个post请求
        HttpPost post = new HttpPost(httpUrl);
        try {
            // 用http连接去执行get请求并且获得http响应
            HttpResponse response = client.execute(post);
            // 从response中取到响实体
            HttpEntity entity = response.getEntity();
            System.out.println("响应实体:"+entity);
            // 把响应实体转成文本
            String html = EntityUtils.toString(entity);
            JSONObject htmlJson = JSONObject.parseObject(html);
            JSONObject result = JSONObject.parseObject(String.valueOf(htmlJson.get("result")));
            JSONObject location = JSONObject.parseObject(String.valueOf(result.get("location")));
            float[] gps = new float[2];
            gps[0] = Float.valueOf(String.valueOf(location.get("lng")));
            gps[1] = Float.valueOf(String.valueOf(location.get("lat")));
            return gps;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 逆地理编码 URL
     */
    final static String LONGITUDE_TO_ADDRESS_URL = "http://api.map.baidu.com/reverse_geocoding/v3/?output=json&coordtype=BD09";
    private static final String CUSTNAME_TO_ADDRESS_URL = "http://api.map.baidu.com/place/v2/search";
    /**
     * 逆地理编码
     * @param lat
     *        纬度 23.1067,
     * @param lng
     *        经度 113.325
     * @return
     */
    public static String longitudeToAddress(float lat, float lng) {
        //拼接请求路径
        String url = LONGITUDE_TO_ADDRESS_URL + "&ak=" + AK + "&location=" + lat + "," + lng;
        System.out.println("请求url:" + url);
        // 创建默认http连接
        HttpClient client = HttpClients.createDefault();
        // 创建一个post请求
        HttpPost post = new HttpPost(url);
        try {
            // 用http连接去执行get请求并且获得http响应
            HttpResponse response = client.execute(post);
            // 从response中取到响实体
            HttpEntity entity = response.getEntity();
            System.out.println("响应结果：" + entity);
            // 把响应实体转成文本
            String html = EntityUtils.toString(entity);
            JSONObject htmlJson = JSONObject.parseObject(html);
            JSONObject result = JSONObject.parseObject(String.valueOf(htmlJson.get("result")));
            return String.valueOf(result.get("formatted_address"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String nameToAddress(String custName) {
        // 拼接请求路径
        String params = String.format("output=json&region=全国&ak=%s&query=%s", AK, custName);
        String url = CUSTNAME_TO_ADDRESS_URL + "?" + params;
        System.out.println("请求URL: " + url);

        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url); // 改用GET请求

        try (CloseableHttpClient closeableClient = (CloseableHttpClient) client) { // 使用try-with-resources关闭连接
            HttpResponse response = closeableClient.execute(get);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String html = EntityUtils.toString(entity);
                JSONObject htmlJson = JSONObject.parseObject(html);

                // 检查API状态码（0表示成功）
                if (htmlJson.getInteger("status") == 0) {
                    JSONArray results = htmlJson.getJSONArray("results");
                    if (results != null && !results.isEmpty()) {
                        for (int i = 0; i < results.size(); i++) {
                            JSONObject result = results.getJSONObject(i);
                            String name = result.getString("name");
                            if (name.equals(custName)) { // 精确匹配名称
                                return result.getString("address")+"_"+result.getString("location");
                            }
                        }
                    }
                } else {
                    System.out.println("API错误码：" + htmlJson.getInteger("status") + ", 信息：" + htmlJson.getString("message"));
                }
            }
        } catch (Exception e) {
            System.out.println("请求异常：" + e.getMessage());
            e.printStackTrace();
        }
        return ""; // 未匹配或异常时返回空字符串
    }
    public static void main(String[] args) {
//        System.out.println("-------------以下为高德");
//        String address = "清远市清新区太平镇龙湾工业区湾保二街2号";
//        double[] doubles = addressToGPS(address);
//        System.out.println(address+"、经度: "+doubles[0]);
//        System.out.println(address+"、纬度: "+doubles[1]);
//        System.out.println("-------------以下为百度");
//        float[] longitudea = AddressTolongitudea("清远市清新区太平镇龙湾工业区湾保二街2号");
//        System.out.println("地址转换坐标 经度："+longitudea[0]);
//        System.out.println("地址转换坐标 纬度："+longitudea[1]);
//        System.out.println("-------------上面是坐标 下面是地址");
//        String address2 = longitudeToAddress(Float.parseFloat("39.94235163008703"),Float.parseFloat("116.45183521532495"));
//        System.out.println("坐标转换地址："+address2);

        // 测试nameToAddress方法
        System.out.println("-------------测试通过名称查询地址");
        String custName = "温州市圣帕电子科技有限公司";
        String address3 = nameToAddress(custName);
        System.out.println("客户名称：" + custName + "，查询到的地址：" + address3);
    }
}
