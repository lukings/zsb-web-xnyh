package cn.visolink.system.phone.util;

import org.mybatis.logging.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/12/21 16:05
 */
public class UrlFilesToZip {
  //  private static final Logger logger = LoggerFactory.getLogger(UrlFilesToZip.class);

    //根据文件链接把文件下载下来并且转成字节码
    public byte[] getImageFromURL(String urlPath) {
        byte[] data = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            is = conn.getInputStream();
            if (conn.getResponseCode() == 200) {
                data = readInputStream(is);
            } else {
                data = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return data;
    }


    public byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
        try {
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            baos.flush();
        } catch (IOException e) {
        }
        byte[] data = baos.toByteArray();
        try {
            is.close();
            baos.close();
        } catch (IOException e) {
        }
        return data;
    }
}
