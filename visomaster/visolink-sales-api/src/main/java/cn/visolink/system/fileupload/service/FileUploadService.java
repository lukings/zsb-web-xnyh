package cn.visolink.system.fileupload.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: wanggang
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/10/22
 */
public interface FileUploadService {

    /**
     * 公共上传接口
     * @param file
     * @param odImgName
     * @return
     */
    String uploadFileWebAll(MultipartFile file, String odImgName, String type) throws IOException;

    /**
     * 公共上传接口
     * @param file
     * @param odImgName
     * @return
     */
    String uploadFileBookWebAll(MultipartFile file, String odImgName,String[] odImgNames,String type) throws IOException;

    /**
     * 公共上传接口(qw)
     * @param file
     * @param odImgName
     * @param odImgNames
     * @param type
     * @return
     */
    String uploadQwFiles(MultipartFile file, String odImgName, String[] odImgNames, String type) throws IOException;
}
