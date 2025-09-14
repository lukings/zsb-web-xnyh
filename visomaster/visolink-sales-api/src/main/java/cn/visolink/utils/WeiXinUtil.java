package cn.visolink.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @Author: Mr.Yu
 * @Date: 2022/1/6 11:29
 * @description
 * @Version 1.0
 */
public class WeiXinUtil {

    public static String sendRequest(String url, String filePath) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(url);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 把文件加到HTTP的post请求中
        File f = new File(filePath);
        String sResponse = "";
        try {
            builder.addBinaryBody("media", new FileInputStream(f), ContentType.APPLICATION_OCTET_STREAM, f.getName());
            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);
            CloseableHttpResponse response = httpClient.execute(uploadFile);
            HttpEntity responseEntity = response.getEntity();
            sResponse = EntityUtils.toString(responseEntity, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sResponse;
    }

}

