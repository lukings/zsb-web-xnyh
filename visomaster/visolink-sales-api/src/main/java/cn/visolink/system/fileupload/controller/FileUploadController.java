package cn.visolink.system.fileupload.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.fileupload.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;

/**
 * @Author: wanggang
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2019/10/22
 */
@RestController
@Api(tags = "上传文件")
@RequestMapping("/FileUpload")
public class FileUploadController {
    @Value("${IMAGE_ACTIVITY_URL}")
    private String webPath;
    @Value("${IMAGE_BASE_URL_W}")
    private String webBookPath;
    @Autowired
    private FileUploadService fileUploadService;

    @Log("公共上传接口(楼书)")
    @ApiOperation(value = "上传", notes = "公共上传接口")
    @PostMapping(value = "/uploadBookFiles")
    public String uploadBookFiles(@RequestParam(value ="odImgName",required=false) String odImgName,
                                  @RequestParam(value ="odImgNames",required=false) String[] odImgNames,
                                   @RequestParam(value = "file",required=false) MultipartFile file,
                                   @RequestParam(value = "type",required=false) String type){
        String result = "";
        try{
            result = fileUploadService.uploadFileBookWebAll(file,odImgName,odImgNames,type);
        }catch (Exception e){
            e.printStackTrace();
            result = "E";
        }
        return result;
    }
    @Log("公共上传接口(活动)")
    @ApiOperation(value = "上传", notes = "公共上传接口")
    @PostMapping(value = "/uploadFiles")
    public String uploadFileWebAll(@RequestParam(value ="odImgName",required=false) String odImgName,
                                   @RequestParam(value = "file",required=false) MultipartFile file,
                                   @RequestParam(value = "type",required=false) String type){
        String result = "";
        try{
            result = fileUploadService.uploadFileWebAll(file,odImgName,type);
        }catch (Exception e){
            e.printStackTrace();
            result = "E";
        }
        return result;
    }

    @Log("公共上传接口(企微)")
    @ApiOperation(value = "上传", notes = "公共上传接口")
    @PostMapping(value = "/uploadQwFiles")
    public String uploadQwFiles(@RequestParam(value ="odImgName",required=false) String odImgName,
                                  @RequestParam(value ="odImgNames",required=false) String[] odImgNames,
                                  @RequestParam(value = "file",required=false) MultipartFile file,
                                  @RequestParam(value = "type",required=false) String type){
        String result = "";
        try{
            result = fileUploadService.uploadQwFiles(file,odImgName,odImgNames,type);
        }catch (Exception e){
            e.printStackTrace();
            result = "E";
        }
        return result;
    }


    @Log("公共下载图片接口")
    @ApiOperation(value = "下载", notes = "公共下载图片接口")
    @RequestMapping(value = "/downPhoto")
    public void downPhoto(HttpServletResponse response,
                                   @RequestParam(value ="imgName",required=false) String imgName,
                                   @RequestParam(value = "rdmImgName",required=false) String rdmImgName){
        response.setContentType("application/octet-stream");
        try{
            String fileName = URLEncoder.encode(imgName,"utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename="+fileName+";filename*='utf-8'"+fileName);
            File file = new File(webPath,rdmImgName);
            if (file!=null){
                response.setContentLength((int)file.length());
                FileUtils.copyFile(file,response.getOutputStream());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Log("公共下载图片接口")
    @ApiOperation(value = "下载", notes = "公共下载图片接口")
    @RequestMapping(value = "/downBookPhoto")
    public void downBookPhoto(HttpServletResponse response,
                          @RequestParam(value ="imgName",required=false) String imgName,
                          @RequestParam(value = "rdmImgName",required=false) String rdmImgName){
        response.setContentType("application/octet-stream");
        try{
            String fileName = URLEncoder.encode(imgName,"utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename="+fileName+";filename*='utf-8'"+fileName);
            File file = new File(webBookPath,rdmImgName);
            if (file!=null){
                response.setContentLength((int)file.length());
                FileUtils.copyFile(file,response.getOutputStream());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
