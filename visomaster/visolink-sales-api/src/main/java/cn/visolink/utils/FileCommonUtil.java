package cn.visolink.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/12/8 14:12
 */
public class FileCommonUtil {
    public static void createFile(HttpServletResponse response, HSSFWorkbook workbook) {
        // 设置文件名
        String fileName ="报名明细";
        try {
            // 捕获内存缓冲区的数据，转换成字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            // 获取内存缓冲中的数据
            byte[] content = out.toByteArray();
            // 将字节数组转化为输入流
            InputStream in = new ByteArrayInputStream(content);
            //通过调用reset（）方法可以重新定位。
            response.reset();
            // 如果文件名是英文名不需要加编码格式，如果是中文名需要添加"iso-8859-1"防止乱码
            // 告诉浏览器用什么软件可以打开此文件
            // 下载文件的默认名称
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"),"ISO-8859-1" )+".xls");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(in);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            bis.close();
            bos.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
