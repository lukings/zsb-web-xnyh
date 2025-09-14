package cn.visolink.system.phone.util;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/12/21 16:40
 */
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 文件操作工具类
 * @author wangsong
 * @mail 1720696548@qq.com
 * @date 2020/8/14 0014 15:14
 * @version 1.0.0
 */
public class FileUtil {


    /**
     * 需要 filePaths 下载路径集( 文件 url )
     *      zipNamex  压缩包名 + .zip
     *      res       HttpServletResponse
     **/
    public static void exceDownload(HttpServletResponse res, List<String> filePaths, String zipName) throws IOException { //String zipName = zipName;       // 压缩包名字
        File path = new File(ResourceUtils.getURL("classpath:").getPath());   //项目跟目录
        if (!path.exists()) path = new File("");
        File upload = new File(path.getAbsolutePath(), "static/images/uploaZip");      //压缩包根目录
        if (!upload.exists()) upload.mkdirs();
        String zipFilePath = upload + File.separator + zipName;                              //拼接目录
        //创建zip文件输出流 == 压缩文件========================================================
        File zip = new File(zipFilePath);
        if (!zip.exists()) {
            zip.createNewFile();
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
        // 打压缩包
        zipFile(filePaths, zos);
        zos.close();
        //设置下载的压缩文件名称 --> java.net.URLEncoder.encode(zipName, "UTF-8")
        res.setContentType("text/html; charset=UTF-8");        // 设置编码字符
        res.setContentType("application/octet-stream");        // 设置内容类型为下载类型
        OutputStream out = res.getOutputStream();              // 创建页面返回方式为输出流，会自动弹出下载框 new String(zipName.getBytes("utf-8"),"ISO8859-1")
        // res.setHeader("Content-disposition", "attachment;filename=123" + zipName);
        res.addHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(zipName, "UTF-8"));
        //创建zip文件输出流==========================================================
        //将打包后的文件写到客户端，输出的方法同上，使用缓冲流输出
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zipFilePath));
        byte[] buff = new byte[bis.available()];
        bis.read(buff);
        bis.close();
        out.write(buff);//输出数据文件
        out.flush();//释放缓存
        out.close();//关闭输出流
    }


    /**
     * 压缩文件
     *
     * @param filePaths   需要压缩的文件路径集合
     * @throws IOException
     */
    private static String zipFile(List<String> filePaths, ZipOutputStream zos) throws IOException {
        //循环读取文件路径集合，获取每一个文件的路径
        for (String filePath : filePaths) {
            File inputFile = new File(filePath);  //根据文件路径创建文件
            URL url = new URL(filePath);
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            //将文件写入zip内，即将文件进行打包
            zos.putNextEntry(new ZipEntry(inputFile.getName()));
            //写入文件的方法，同上
            int size = 0;
            byte[] buffer = new byte[1024];  //设置读取数据缓存大小
            while ((size = bis.read(buffer)) > 0) {
                zos.write(buffer, 0, size);
            }
            //关闭输入输出流
            zos.closeEntry();
            bis.close();
        }
        return null;
    }
}
