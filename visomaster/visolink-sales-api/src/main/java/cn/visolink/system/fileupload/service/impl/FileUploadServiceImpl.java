package cn.visolink.system.fileupload.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.visolink.system.fileupload.fileUtil.UploadUtils;
import cn.visolink.system.fileupload.service.FileUploadService;
import cn.visolink.utils.StringUtils;
import com.jcraft.jsch.*;
import net.coobird.thumbnailator.makers.ScaledThumbnailMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

/**
 * @Author: wanggang
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/10/22
 */
@Service
@Component
public class FileUploadServiceImpl implements FileUploadService {

    /**
     * 存放图片的根目录
     */
    @Value("${IMAGE_ACTIVITY_URL}")
    private String webPath;
    @Value("${IMAGE_BASE_URL_W}")
    private String webBookPath;

    @Value("${IMAGE_BASE_URL_QW}")
    private String webQwPath;

    @Value("${spring.profiles.active}")
    private String active;

    @Override
    public String uploadFileWebAll(MultipartFile file, String odImgName,String type) throws IOException {
        //删除原有图片
        if(!StringUtils.isBlank(odImgName)){
            new File(webPath + odImgName).delete();
        }
        String dateM = DateUtil.format(new Date(),"yyyyMM");
        if (file!=null){
            String picNewName = "";
            if ("5".equals(type)){
                picNewName = file.getOriginalFilename();// 获取图片原来的名字
            }else{
                String fileName = file.getOriginalFilename();// 获取图片原来的名字
                picNewName = UploadUtils.generateRandonFileName(fileName);// 通过工具类产生新图片名称，防止重名
            }
            //判断文件不大于200Kb,不需要压缩
            //判断上传的是图片还是视频（1：图片）
//            if ((!checkFileSize(file.getSize(),200,"K")) && ("1".equals(type) || "5".equals(type))){
//                //临时文件名称
//                String tempName = UploadUtils.generateRandonFileName(picNewName);// 通过工具类产生新图片名称，防止重名
//                //指明文件上传位置
//                File destOld = new File(webPath, tempName);
//                File dest = new File(webPath, picNewName);
//                //判断文件父目录是否存在
//                if(!dest.getParentFile().exists()){
//                    dest.getParentFile().mkdir();
//                }
//                //写入文件
//                file.transferTo(destOld);
//                //读取文件
//                BufferedImage img = ImageIO.read(destOld);
//                //按固定比例0.15压缩图片
//                BufferedImage bi = new ScaledThumbnailMaker().scale(0.8).imageType(BufferedImage.TYPE_3BYTE_BGR).make(img);
//                //写压缩后图片            
//                ImageIO.write(bi,"jpg",dest);
//                destOld.delete();
//
//            }else{
//                //指明文件上传位置
//                File dest = new File(webPath, picNewName);
//                //写入文件
//                file.transferTo(dest);
//            }
            if ("prod".equalsIgnoreCase(active) || "pre".equalsIgnoreCase(active)){
                //走sftp 跨服务器上传
                JSch jsch = new JSch();
                try {
                    Session session = jsch.getSession("root", "192.168.47.64", 54320);
                    session.setPassword("WYjtSZAC2025");
                    session.setConfig("StrictHostKeyChecking", "no");

                    session.connect();

                    ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
                    channelSftp.connect();

                    // 检查并创建目录
                    String targetDir = webPath + dateM;
                    try {
                        // 尝试获取目录信息
                        channelSftp.stat(targetDir);
                    } catch (SftpException e) {
                        // 如果目录不存在，捕获异常并创建目录
                        if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                            channelSftp.mkdir(targetDir);
                        } else {
                            throw e;
                        }
                    }

                    InputStream inputStream = file.getInputStream();
                    String remoteFilePath = webPath + dateM+"/"+picNewName;
                    channelSftp.put(inputStream,remoteFilePath );

                    inputStream.close();
                    channelSftp.disconnect();
                    session.disconnect();

                    System.out.println("File uploaded successfully.");
                } catch (JSchException | SftpException e) {
                    throw new RuntimeException(e);
                }

            }else {
                //指明文件上传位置
                File dest = new File(webPath + dateM , picNewName);
                //判断文件父目录是否存在
                if(!dest.getParentFile().exists()){
                    dest.getParentFile().mkdir();
                }
                //写入文件
                file.transferTo(dest);
            }
            return dateM + "/" + picNewName;
        }else{
            return "删除成功！！";
        }
    }

    @Override
    public String uploadFileBookWebAll(MultipartFile file, String odImgName,String[] odImgNames,String type) throws IOException {
        //删除原有图片
        if(!StringUtils.isBlank(odImgName)){
            File oldfile = new File(webBookPath+odImgName);
            if (oldfile!=null){
                oldfile.delete();
            }
        }
        String dateM = DateUtil.format(new Date(),"yyyyMM");
        if (odImgNames!=null && odImgNames.length>0){
            for (String name:odImgNames) {
                File oldfile = new File(webBookPath + name);
                if (oldfile!=null){
                    oldfile.delete();
                }
            }
        }
        if (file!=null){
            String picNewName = "";
            if ("5".equals(type)){
                picNewName = file.getOriginalFilename();// 获取图片原来的名字
            }else{
                String fileName = file.getOriginalFilename();// 获取图片原来的名字
                picNewName = UploadUtils.generateRandonFileName(fileName);// 通过工具类产生新图片名称，防止重名
            }
            //判断文件不大于200Kb,不需要压缩
            //判断上传的是图片还是视频（1：图片）
            if ((!checkFileSize(file.getSize(),200,"K")) && ("1".equals(type) || "5".equals(type))){
                //临时文件名称
                String tempName = UploadUtils.generateRandonFileName(picNewName);// 通过工具类产生新图片名称，防止重名
                //指明文件上传位置
                File destOld = new File(webPath, tempName);
                File dest = new File(webBookPath + dateM, picNewName);
                //判断文件父目录是否存在
                if(!dest.getParentFile().exists()){
                    dest.getParentFile().mkdir();
                }
                //写入文件
                file.transferTo(destOld);
                //读取文件
                BufferedImage img = ImageIO.read(destOld);
                //按固定比例0.15压缩图片
                BufferedImage bi = new ScaledThumbnailMaker().scale(0.8).imageType(BufferedImage.TYPE_3BYTE_BGR).make(img);
                //写压缩后图片            
                ImageIO.write(bi,"jpg",dest);
                destOld.delete();

            }else{
                //指明文件上传位置
                File dest = new File(webBookPath + dateM, picNewName);
                //判断文件父目录是否存在
                if(!dest.getParentFile().exists()){
                    dest.getParentFile().mkdir();
                }
                //写入文件
                file.transferTo(dest);
            }

            return dateM + "/" + picNewName;
        }else{
            return "删除成功！！";
        }
    }

    @Override
    public String uploadQwFiles(MultipartFile file, String odImgName, String[] odImgNames, String type) throws IOException {
        //删除原有图片
        if(!StringUtils.isBlank(odImgName)){
            File oldfile = new File(webQwPath+odImgName);
            if (oldfile!=null){
                oldfile.delete();
            }
        }
        String dateM = DateUtil.format(new Date(),"yyyyMM");
        if (odImgNames!=null && odImgNames.length>0){
            for (String name:odImgNames) {
                File oldfile = new File(webQwPath + name);
                if (oldfile!=null){
                    oldfile.delete();
                }
            }
        }
        if (file!=null){
            String picNewName = "";
            if ("5".equals(type)){
                picNewName = file.getOriginalFilename();// 获取图片原来的名字
            }else{
                String fileName = file.getOriginalFilename();// 获取图片原来的名字
                picNewName = UploadUtils.generateRandonFileName(fileName);// 通过工具类产生新图片名称，防止重名
            }
            //指明文件上传位置
            File dest = new File(webQwPath + dateM, picNewName);
            //判断文件父目录是否存在
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdir();
            }
            //写入文件
            file.transferTo(dest);
            return dateM + "/" + picNewName;
        }else{
            return "删除成功！！";
        }
    }

    /**
     * 判断文件大小
     *
     * @param len
     *            文件长度
     * @param size
     *            限制大小
     * @param unit
     *            限制单位（B,K,M,G）
     * @return
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }

    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     * @throws SocketException
     */
    private static String getLinuxLocalIp() throws SocketException {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress().toString();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                                System.out.println(ipaddress);
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            System.out.println("获取ip地址异常");
            ip = "127.0.0.1";
            ex.printStackTrace();
        }
        System.out.println("IP:" + ip);
        return ip;
    }


}
