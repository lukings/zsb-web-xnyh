package cn.visolink.system.allpeople.contentManagement.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.common.security.utils.HttpClient;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.message.model.SysLog;
import cn.visolink.message.model.form.MessageForm;
import cn.visolink.system.activity.dao.ActivityInfoDao;
import cn.visolink.system.allpeople.contentManagement.dao.ContentDao;
import cn.visolink.system.allpeople.contentManagement.model.*;
import cn.visolink.system.allpeople.contentManagement.service.ContentService;
import cn.visolink.system.allpeople.examine.dao.ExamineDao;
import cn.visolink.system.channel.model.form.ExcelForm;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.fileupload.service.FileUploadService;
import cn.visolink.system.parameter.model.Dictionary;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.project.model.vo.ProjectVO;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import cn.visolink.utils.*;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.coobird.thumbnailator.makers.ScaledThumbnailMaker;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ContentServiceImpl
 * @Author wanggang
 * @Description //内容
 * @Date 2020/1/17 14:10
 **/
@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentDao contentDao;
    @Value("${IMAGE_BASE_URL_W}")
    private String imgUrl;
    @Value("${IMAGE_END_URL}")
    private String imgEndUrl;
    @Value("${UPLOAD_URL}")
    private String uploadUrl;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ExamineDao examineDao;
    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private FileUploadService fileUploadService;

    @Value("${appid}")
    private String appid;
    @Value("${appKey}")
    private String appKey;
    @Value("${mapAppKey}")
    private String mapAppKey;
    @Value("${mapUrl}")
    private String mapUrl;
    @Value("${IMAGE_TAG}")
    private String imageTag;

    private static String WE_CHAT_ACCESS_TOKEN = "WE_CHAT_ACCESS_TOKEN";

    //楼盘key值
    private static String BUILDING_ID = "buildingID";
    //新闻类型Key值
    private static String NEWS_TYPE = "NewsType";

    @Autowired
    private WeChatAccessTokenUtils weChatAccessTokenUtils;

    /**
     * 获取轮播图片
     * @param map
     * @return
     */
    @Override
    public PageInfo<BuildingPhoto> getBuildingPhotos(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        if (map.get("TypeName") == null || "".equals(map.get("TypeName"))){
            StartupPagePicture startupPagePicture = new StartupPagePicture();
            if(map.get("port") != null && !"".equals(map.get("port"))){
                startupPagePicture.setPort(Integer.parseInt(map.get("port").toString()));
            }
            PageHelper.startPage(pageIndex,pageSize);
            List list =  contentDao.startupPictureList(startupPagePicture);
            PageInfo<BuildingPhoto> pageInfo = new PageInfo<>(list);
            return pageInfo;
        }
            if (map.get("CityIDs")!=null && !"".equals(map.get("CityIDs"))){
            String[] ids = map.get("CityIDs").toString().split(",");
            if (ids!=null && ids.length>0){
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < ids.length; i++) {
                    if (i==ids.length-1){
                        sb.append("'"+ids[i]+"','00000000-0000-0000-0000-000000000000'");
                    }else{
                        sb.append("'"+ids[i]+"',");
                    }
                }
                map.put("CityIDs",sb.toString());
            }
        }else{
            if (map.get("JobID")!=null && !"".equals(map.get("JobID"))){
                List<Map> ids = this.getCitysByJobId(null);
                //查询岗位是否是系统管理员
//                String jobCode = contentDao.isAdmin(map.get("JobID")+"");
//                if (jobCode!=null && ("10001".equals(jobCode) || "系统管理员".equals(jobCode))){
//                    //是系统管理员查询所有城市
//                    ids = contentDao.getAllCitys();
//                }else{
//                    ids = contentDao.getCityByJobId(map.get("JobID")+"");
//                }
                if (ids!=null && ids.size()>0){
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < ids.size(); i++) {
                        if (i==ids.size()-1){
                            sb.append("'"+ids.get(i).get("CityID")+"','00000000-0000-0000-0000-000000000000'");
                        }else{
                            sb.append("'"+ids.get(i).get("CityID")+"',");
                        }
                    }
                    map.put("ids",sb.toString());
                }else{
                    map.put("ids","00000000-0000-0000-0000-000000000000");
                }
            }
        }
        PageHelper.startPage(pageIndex,pageSize);
        List list = contentDao.getBuildingPhotos(map);
        PageInfo<BuildingPhoto> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    /**
     * 添加/修改轮播图片
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addBuildingPhoto(Map map) {
//        MultipartFile file = buildingPhoto.getFile();
        try{
            String json = JSONObject.toJSONString(map);
            //转化为实体类
            BuildingPhoto buildingPhoto = JSONObject.toJavaObject(JSON.parseObject(json),BuildingPhoto.class);
            List<BuildingPhoto> buildingPhotos = new ArrayList<>();
            //获取原文件路径
//        String ImgUrl = buildingPhoto.getImgUrl();
//        if (file!=null){
//            if(!checkFileSize(file.getSize(),10,"M")){
//                return ResultBody.error(2001, "文件过大");
//            }
//            Map jsonMap = new HashMap();
//            jsonMap.put("type","1");
//            jsonMap.put("file",file);
//            JSONObject json = HttpRequestUtil.httpPost(uploadUrl, JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
//            String picNewName = JSON.toJSONString(json);
////            String picNewName = uploadFile(file);
//            if ("E".equals(picNewName)){
//                return ResultBody.error(2001, "上传图片异常！");
//            }
//            buildingPhoto.setImgUrl(imgUrl+picNewName);
//            buildingPhoto.setImgName(picNewName);
//        }
            //判断是新增图片还是修改（1：新增 2：修改）
            if ("1".equals(buildingPhoto.getAddOrEdit())){
                String[] cityIds = buildingPhoto.getCityID().split(",");
                String[] cityNames = buildingPhoto.getCityName().split(",");
                if (cityIds.length==1){
                    String ID = UUID.randomUUID().toString().replaceAll("-","");
                    buildingPhoto.setID(ID);
                    contentDao.addBuildingPhoto(buildingPhoto);
                    buildingPhotos.add(buildingPhoto);
                }else{
                    for (int i = 0; i < cityIds.length; i++) {
                        String ID = UUID.randomUUID().toString().replaceAll("-","");
                        buildingPhoto.setID(ID);
                        buildingPhoto.setCityID(cityIds[i]);
                        buildingPhoto.setCityName(cityNames[i]);
                        contentDao.addBuildingPhoto(buildingPhoto);
                        buildingPhotos.add(buildingPhoto);
                    }
                }
            }else{
                contentDao.updateBuildingPhoto(buildingPhoto);
                buildingPhotos.add(buildingPhoto);
            }
            return ResultBody.success(buildingPhotos);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0008,"轮播管理异常！");
        }
    }


    public String formUpload(String urlStr,String inputValue) {
        String res = "";
        String contentType = "";
        HttpURLConnection conn = null;
        File file1 = null;
        DataInputStream in=null;
        // boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "---------------------------123821742118716";
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            StringBuffer strBufs = new StringBuffer();
            strBufs.append("\r\n").append("--").append(BOUNDARY)
                    .append("\r\n");
            strBufs.append("Content-Disposition: form-data; name=type"+"\r\n\r\n");
            strBufs.append("5");
            out.write(strBufs.toString().getBytes());
            // file
            file1 = new File(inputValue);
            String filename = file1.getName();

            //没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
            contentType = new MimetypesFileTypeMap().getContentType(file1);
            //contentType非空采用filename匹配默认的图片类型
            if(!"".equals(contentType)){
                if (filename.endsWith(".png")) {
                    contentType = "image/png";
                }else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
                    contentType = "image/jpeg";
                }else if (filename.endsWith(".gif")) {
                    contentType = "image/gif";
                }else if (filename.endsWith(".ico")) {
                    contentType = "image/image/x-icon";
                }
            }
            if (contentType == null || "".equals(contentType)) {
                contentType = "application/octet-stream";
            }
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(BOUNDARY)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=file; filename=" + filename+ "\r\n");
            System.out.println("file,"+filename);

            strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            in = new DataInputStream(
                    new FileInputStream(file1));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            StringBuffer strBuf1 = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf1.append(line).append("\n");
            }
            res = strBuf1.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
            //读取本地图片后删除
            if (file1!=null){
                file1.delete();
            }
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return res;
    }


    /**
     * 获取新闻列表
     * @param map
     * @return
     */
    @Override
    public PageInfo<News> getNewsList(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }

        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime")+"")));
                map.put("endTime",sf.format(sf.parse(String.valueOf(map.get("endTime")).replace("00:00:00","23:59:59"))));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (map.get("CityIDs")!=null && !"".equals(map.get("CityIDs"))){
            String[] ids = map.get("CityIDs").toString().split(",");
            if (ids!=null && ids.length>0){
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < ids.length; i++) {
                    if (i==ids.length-1){
                        sb.append("'"+ids[i]+"','00000000-0000-0000-0000-000000000000'");
                    }else{
                        sb.append("'"+ids[i]+"',");
                    }
                }
                map.put("CityIDs",sb.toString());
            }
        }else{
            if (map.get("JobID")!=null && !"".equals(map.get("JobID"))){
                List<Map> ids = this.getCitysByJobId(null);
//                //查询岗位是否是系统管理员
//                String jobCode = contentDao.isAdmin(map.get("JobID")+"");
//                if (jobCode!=null && ("10001".equals(jobCode) || "系统管理员".equals(jobCode))){
//                    //是系统管理员查询所有城市
//                    ids = contentDao.getAllCitys();
//                }else{
//                    ids = contentDao.getCityByJobId(map.get("JobID")+"");
//                }
                if (ids!=null && ids.size()>0){
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < ids.size(); i++) {
                        if (i==ids.size()-1){
                            sb.append("'"+ids.get(i).get("CityID")+"','00000000-0000-0000-0000-000000000000'");
                        }else{
                            sb.append("'"+ids.get(i).get("CityID")+"',");
                        }
                    }
                    map.put("ids",sb.toString());
                }else{
                    map.put("ids","00000000-0000-0000-0000-000000000000");
                }
            }
        }

        if(map.containsKey(BUILDING_ID)){

            Object lo = map.get(BUILDING_ID);
            map.put(BUILDING_ID,null);
            StringBuffer sb = new StringBuffer();
            if(lo != null){
                List<Object>  buildList = (List) lo;
                for(Object o : buildList){
                    if(o != null){
                        List<String>  buildingBookList = (List) o;
                        if(buildingBookList.size()>1){
                            sb.append("'"+buildingBookList.get(1)+"',");

                        }
                    }
                }
            }
            if(sb.length()>0){
                sb.append("''");
                map.put("BuildingID",sb.toString());
            }
        }

        if(map.containsKey(NEWS_TYPE)){
            Object lo = map.get(NEWS_TYPE);
            map.put(NEWS_TYPE,null);
            StringBuffer sb = new StringBuffer();
            if(lo != null){
                List<String>  newsTypeList = (List) lo;
                for(String newsType : newsTypeList){
                    sb.append("'"+newsType+"',");
                }
            }
            if(sb.length()>0){
                sb.append("''");
                map.put(NEWS_TYPE,sb.toString());
            }
        }

        PageHelper.startPage(pageIndex, pageSize);
        List<News> list = contentDao.getNewsList(map);
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    /**
     * 新增/修改新闻
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addOrEidtNews(Map map) {
        try{
            String json = JSONObject.toJSONString(map);

            //转化为实体类
            News news = JSONObject.toJavaObject(JSON.parseObject(json),News.class);
//            MultipartFile file = news.getFile();
            //获取原文件路径
//            String ImgUrl = news.getHeadImgUrl();
//            if (file!=null){
//                if(!checkFileSize(file.getSize(),10,"M")){
//                    return ResultBody.error(2001, "文件过大");
//                }
//                String picNewName = uploadFile(file);
//                if ("E".equals(picNewName)){
//                    return ResultBody.error(-21_0003, "上传图片异常！");
//                }
//                news.setHeadImgUrl(imgUrl+picNewName);
//            }
            //判断是否首次更改发布时间
            if ("1".equals(news.getIsPublish()) && news.getPublishTime()==null){
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                news.setPublishTime(sf.format(new Date()));
            }

            if (news.getContent()!=null){
                BASE64Decoder decoder = new BASE64Decoder();
//                news.setContent(news.getContent().replaceAll("image/brokerWeb",this.imageTag+"image/brokerWeb"));
                news.setContent(new String(decoder.decodeBuffer(news.getContent()), "UTF-8"));
            }


            //判断是新增图片还是修改（1：新增 2：修改）
            if ("1".equals(news.getAddOrEdit())){
                news.setID(UUID.randomUUID().toString());
                contentDao.addNews(news);
            }else{
                contentDao.updateNews(news);
//                if (file!=null || "1".equals(news.getIsDel())){
//                    if (ImgUrl!=null){
//                        //删除原来的图片
//                        File old = new File(ImgUrl);
//                        if (old!=null){
//                            old.delete();
//                        }
//                    }
//                }
            }

            //保存新闻楼盘

            if(map.containsKey(BUILDING_ID)){
                List<NewsBuildBook> newsBuildBookList = new ArrayList<>();
                contentDao.deleteNewsBuildBook(news.getID());
                Object lo = map.get(BUILDING_ID);
                if(lo != null){
                    List<Object>  buildList = (List) lo;
                    for(Object o : buildList){
                        if(o != null){
                            List<String>  buildindBookList = (List) o;
                            if(buildindBookList.size()>1){
                                NewsBuildBook  newsBuildBook =new NewsBuildBook();
                                newsBuildBook.setBuildBookId(buildindBookList.get(1));
                                newsBuildBook.setNewsId(news.getID());
                                newsBuildBook.setCreator(news.getCreator());
                                newsBuildBookList.add(newsBuildBook);
                            }
                        }
                    }
                }
                if(!newsBuildBookList.isEmpty()){
                    contentDao.saveNewsBuildBook(newsBuildBookList);
                }
            }

            return ResultBody.success("新闻成功编辑！！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0002,"新闻编辑异常！！");
        }


    }

    @Override
    public PageInfo<Feedback> getFeedbackList(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex") != null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize") != null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex, pageSize);
        if (map.get("beginTime") != null && !"".equals(map.get("beginTime") + "") && map.get("endTime") != null && !"".equals(map.get("endTime") + "")){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime") + "")));
                map.put("endTime",sf.format(sf.parse(map.get("endTime") + "")));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        List<Feedback> list = contentDao.getFeedbackList(map);
        for (Feedback feedback : list){
            List<String> imgList = contentDao.getFeedBackImgList(feedback.getID());
            feedback.setImgList(imgList);
        }
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }





    @Override
    public PageInfo<Feedback> getFeedback(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex, pageSize);
        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime")+"")));
                map.put("endTime",sf.format(sf.parse(map.get("endTime")+"")));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        List<Feedback> list = contentDao.getAllFeedbackList(map);
        for (Feedback feedback : list){
            List<String> imgList = contentDao.getFeedBackImgList(feedback.getID());
            feedback.setImgList(imgList);
        }
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public Feedback getFeedbackDetail(Map map) {
        return contentDao.getFeedbackDetail(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody handleFeedback(Map map) {
        try{
            //插入消息表
            List<MessageForm> listMessage = new ArrayList<>();
            //content 反馈内容
            String content = "【反馈通知】您的反馈已处理："+map.get("handleDesc");
            MessageForm messageForm = new MessageForm();
            messageForm.setSubject("反馈通知");
            messageForm.setContent(content);
            messageForm.setReceiver(map.get("feedBackUserId")+"");//反馈人ID
            messageForm.setSender(map.get("userId")+"");//当前登录人ID
            messageForm.setMessageType(2108);
            listMessage.add(messageForm);
            messageMapper.insertMessage(listMessage);
            //将消息放入缓存
//            Map userMap = new HashMap();
//            userMap.put("ID",map.get("feedBackUserId")+"");
//            //获取全民经纪人账号
//            Examine examine = examineDao.getBrokerUser(userMap);
//            List list = new ArrayList();
//            String content1 = examine.getMobile() + "-" + "您的反馈已处理：" + map.get("handleDesc");
//            list.add(content1);
//            redisUtil.lPush("pushMessage", list);
            //更新反馈信息
            Map dataMap = new HashMap();
            dataMap.put("ID",map.get("id"));
            dataMap.put("HandleUserId",map.get("userId")+"");
            dataMap.put("Status",1);
            dataMap.put("HandleDesc",map.get("handleDesc"));
            dataMap.put("Editor",map.get("userId")+"");
            contentDao.updateFeedback(dataMap);
            return ResultBody.success("反馈处理成功！！！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0001,"反馈处理异常！");
        }

    }

    @Override
    public PageInfo<BuildingBook> getAllBuilding(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex, pageSize);
        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime")+"")));
                map.put("endTime",sf.format(sf.parse(map.get("endTime")+"")));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (!"".equals(map.get("ProjectIDs"))){
            StringBuffer sb = new StringBuffer();
            String[] pids = map.get("ProjectIDs").toString().split(",");
            for (int i = 0; i < pids.length; i++) {
                if (i==pids.length-1){
                    sb.append("'"+pids[i]+"'");
                }else{
                    sb.append("'"+pids[i]+"',");
                }
            }
            map.put("ProjectIDs",sb.toString());
        }
        if (!"".equals(map.get("BelongAreas"))){
            StringBuffer sb = new StringBuffer();
            String[] pids = map.get("BelongAreas").toString().split(",");
            for (int i = 0; i < pids.length; i++) {
                if (i==pids.length-1){
                    sb.append("'"+pids[i]+"'");
                }else{
                    sb.append("'"+pids[i]+"',");
                }
            }
            map.put("BelongAreas",sb.toString());
        }
        if (map.get("order")==null || "".equals(map.get("order"))){
            map.put("order","CreateTime");
        }
        if (map.get("desc")==null || "".equals(map.get("desc"))){
            map.put("desc","desc");
        }
        List<BuildingBook> list = contentDao.getAllBuilding(map);
        PageInfo pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody addOrEditBuilding(Map map) {
        //楼书列表展示图片
//        MultipartFile BuildingFile = buildingBasic.getBuildingFile();
//        //楼书轮播图片数组
//        MultipartFile[] BuildingFiles = buildingBasic.getBuildingFiles();
//        //楼书视频文件
//        MultipartFile BuildingVideoFile = buildingBasic.getBuildingVideoFile();
        try{
            boolean all = true;
            String saleStatus = map.get("saleStatus") + "";
            if (org.apache.commons.lang3.StringUtils.isBlank(saleStatus)) {
                return ResultBody.error(120000,"楼盘状态不能为空");
            }
            String json = JSONObject.toJSONString(map);
            //转化为实体类
            BuildingBasic buildingBasic = JSONObject.toJavaObject(JSON.parseObject(json),BuildingBasic.class);
            //封面图路径
//            String HeadImgUrl = null;
            String addOrEdit = buildingBasic.getAddOrEdit();
            String creator = buildingBasic.getCreator();
            BuildingBook buildingBook = new BuildingBook();
            buildingBook.setAddress(buildingBasic.getAddress());
            buildingBook.setBelongArea(buildingBasic.getBelongArea());
            buildingBook.setCityID(buildingBasic.getCityID());
            buildingBook.setCityName(buildingBasic.getCityName());
            buildingBook.setDistrict(buildingBasic.getDistrict());
            buildingBook.setStreet(buildingBasic.getStreet());
            buildingBook.setProjectID(buildingBasic.getProjectID());
            buildingBook.setProjectName(buildingBasic.getProjectName());
            buildingBook.setProjectShowName(buildingBasic.getProjectShowName());
            buildingBook.setDeliveryDesc(buildingBasic.getDeliveryDesc());
            buildingBook.setInventedOnlookersNum(buildingBasic.getInventedOnlookersNum());
            buildingBook.setSaleStatus(buildingBasic.getSaleStatus());
            if (buildingBook.getProjectShowName()==null || "".equals(buildingBook.getProjectShowName())){
                all = false;
            }
            buildingBook.setBuildBookName(buildingBasic.getProjectShowName());
            // 楼盘单价
            if (buildingBasic.getUnitPrice()==null || "".equals(buildingBasic.getUnitPrice())){
                buildingBook.setUnitPrice("价格未定");
            }else{
                buildingBook.setUnitPrice(buildingBasic.getUnitPrice());
            }
            buildingBook.setBuildMobile(buildingBasic.getBuildMobile());
            buildingBook.setLatitude(buildingBasic.getLatitude());
            buildingBook.setLongitude(buildingBasic.getLongitude());
            buildingBook.setIsShowHeadImg(buildingBasic.getIsShowHeadImg());
            buildingBook.setStatus(buildingBasic.getIsShowHeadImg());
            buildingBook.setBuildLabel(buildingBasic.getBuildLabel());
            //转换日期格式
            if (buildingBasic.getOpenTime()!=null && !"".equals(buildingBasic.getOpenTime())){
                buildingBook.setOpenTime(this.dealDateFormat(buildingBasic.getOpenTime()));
            }
            if (buildingBasic.getDelivery()!=null && !"".equals(buildingBasic.getDelivery())){
                buildingBook.setDelivery(this.dealDateFormat(buildingBasic.getDelivery()));
            }
            buildingBook.setPeriodInt(buildingBasic.getPeriodInt());
            buildingBook.setDecorationLevel(buildingBasic.getDecorationLevel());
            buildingBook.setPropertyCom(buildingBasic.getPropertyCom());
            buildingBook.setPropertyFee(buildingBasic.getPropertyFee());
            buildingBook.setFloorSpace(buildingBasic.getFloorSpace());
            buildingBook.setBuildSapce(buildingBasic.getBuildSapce());
            buildingBook.setGreenRate(buildingBasic.getGreenRate());
            buildingBook.setPlotRatio(buildingBasic.getPlotRatio());
            buildingBook.setProjectDesc(buildingBasic.getProjectDesc());
            buildingBook.setBasicConfig(buildingBasic.getInfrastructure());
            buildingBook.setCreator(creator);
            buildingBook.setHeadImgUrl(buildingBasic.getHeadImgUrl());
            buildingBook.setShareDesc(buildingBasic.getShareDesc());
            buildingBook.setShareDescImgUrl(buildingBasic.getShareDescImgUrl());
            buildingBook.setDevelopers(buildingBasic.getDevelopers());
            //周边配套---日期格式转换
            buildingBook.setTrafficMating(buildingBasic.getTrafficMating());
            buildingBook.setEducation(buildingBasic.getEducation());
            buildingBook.setHospital(buildingBasic.getHospital());
            buildingBook.setBank(buildingBasic.getBank());
            buildingBook.setBusiness(buildingBasic.getBusiness());
            buildingBook.setElseRound(buildingBasic.getElseRound());
            buildingBook.setIsHot(buildingBasic.getIsHot());
            buildingBook.setIsPublish("1");//默认发布
            //保存楼盘周边城市
            List<BuildBookCity> buildBookCityList = new ArrayList<>();
            //判断是否新增楼盘
            if ("1".equals(addOrEdit)){
                //获取楼盘排序号
                String listIndex = contentDao.getBuildListIndexByCityId(buildingBasic.getCityID());
                if (listIndex!=null){
                    buildingBook.setListIndex(Integer.valueOf(listIndex)+1+"");
                }else{
                    buildingBook.setListIndex("1");
                }
                //生成楼盘ID
                String bid = UUID.randomUUID().toString().replaceAll("-","");
                buildingBook.setID(bid);
                buildingBook.setIsReport("1");

                //保存楼栋标签
                if (buildingBasic.getBuildingTags()!=null && buildingBasic.getBuildingTags().size()>0){
                    int num = 0;
                    List<BuildBookTag> tags = new ArrayList<>();
                    for (BuildBookTag buildingTag:buildingBasic.getBuildingTags()) {
                        num++;
                        buildingTag.setBuildBookId(bid);
                        buildingTag.setCreator(creator);
                        buildingTag.setTagType("1");
                        buildingTag.setListIndex(num+"");
                        tags.add(buildingTag);
                    }
                    contentDao.addHouseTag(tags);
                }

                //保存轮播图片
                if (buildingBasic.getBuildingPhotos()!=null && buildingBasic.getBuildingPhotos().size()>0){
                    int num = 1;
                    for (BuildBookPhoto buildingPhoto:buildingBasic.getBuildingPhotos()) {
                        //判断如果是需要删除的轮播图删除图片
                        if ("1".equals(buildingPhoto.getIsClear())){
                            List<BuildingPhoto> photos = buildingPhoto.getBuildingPhoto();
                            for (BuildingPhoto b:photos) {
                                //删除图片
                                File file = new File(imgUrl+b.getImgName());
                                if (file!=null){
                                    file.delete();
                                }
                            }
                        }else{
                            List<BuildingPhoto> photos = buildingPhoto.getBuildingPhoto();
                            if (photos!=null && photos.size()>0){
                                for (BuildingPhoto ph:photos) {
                                    ph.setBuildBookID(bid);
                                    ph.setCreator(creator);
                                    ph.setTypeName("2");
                                    ph.setMaterialType(buildingPhoto.getCode());
                                    ph.setMaterialTypeDesc(buildingPhoto.getName());
                                    ph.setListIndex(num+"");
                                    contentDao.addBuildingPhAndVi(ph);
                                    num++;
                                }
                            }
                        }
                    }
                }else{
                    all = false;
                }
                List<BuildBookProduct> buildBookProducts = buildingBasic.getBuildBookProducts();
                if (buildBookProducts!=null && buildBookProducts.size()>0){
                    for (BuildBookProduct build:buildBookProducts) {
                        build.setBuildBookId(bid);
                        build.setCreator(creator);
                        contentDao.addBuildBookProduct(build);
                        String ProductId = build.getId();
                        List<Apartment> apartments = build.getApartmentList();
                        if (apartments!=null && apartments.size()>0){
                            for (Apartment apartment:apartments) {
                                apartment.setId(UUID.randomUUID().toString().replaceAll("-",""));
                                apartment.setBuildBookID(bid);
                                apartment.setCreator(creator);
                                apartment.setProjectID(buildingBasic.getProjectID());
                                apartment.setProductId(ProductId);
                                contentDao.addBuildingApartment(apartment);
                            }
                        }
                    }
                }else{
                    all = false;
                }
                //保存主城市信息
                BuildBookCity buildBookCity = new BuildBookCity();
                buildBookCity.setBuildBookId(bid);
                buildBookCity.setBuildBookName(buildingBasic.getProjectShowName());
                buildBookCity.setCityId(buildingBasic.getCityID());
                buildBookCity.setCityName(buildingBasic.getCityName());
                buildBookCity.setCityType("1");
                buildBookCity.setCreator(buildingBasic.getCreator());
                buildBookCity.setListIndex(buildingBook.getListIndex());
                buildBookCityList.add(buildBookCity);
                if(buildBookCityList.size() > 0){
                    contentDao.saveBuildBookCity(buildBookCityList);
                }
                if (all){
                    buildingBook.setIsAllPerfect("1");
                }else{
                    buildingBook.setIsAllPerfect("0");
                }
                //楼盘周边配套
                List<BuildBookPeriphery> allList = new ArrayList<>();
                //查看前端是否配置了学校信息
                List<BuildBookPeriphery> schools = buildingBasic.getSchools();
                Map perMap = new HashMap();
                perMap.put("lat",buildingBasic.getLatitude());
                perMap.put("lng",buildingBasic.getLongitude());
                perMap.put("buildBookId",buildingBook.getID());
                perMap.put("projectId",buildingBook.getProjectID());
                if (schools==null || schools.size()==0){
                    perMap.put("periphery","学校");
                    perMap.put("adType","1");
                    schools = this.getNewPeripheralMatching(perMap);
                }else{
                    for (BuildBookPeriphery b:schools) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                }
                if(schools!=null && schools.size()>0){
                    allList.addAll(schools);
                }
                //查看前端是否配置了交通信息
                List<BuildBookPeriphery> traffics = buildingBasic.getTraffics();
                if (traffics==null || traffics.size()==0){
                    perMap.put("periphery","交通");
                    perMap.put("adType","2");
                    traffics = this.getNewPeripheralMatching(perMap);
                }else{
                    for (BuildBookPeriphery b:traffics) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                }
                if(traffics!=null && traffics.size()>0){
                    allList.addAll(traffics);
                }
                //查看前端是否配置了购物信息
                List<BuildBookPeriphery> shoppings = buildingBasic.getShoppings();
                if (shoppings==null || shoppings.size()==0){
                    perMap.put("periphery","购物");
                    perMap.put("adType","3");
                    shoppings = this.getNewPeripheralMatching(perMap);
                }else{
                    for (BuildBookPeriphery b:shoppings) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                }
                if(shoppings!=null && shoppings.size()>0){
                    allList.addAll(shoppings);
                }
                //查看前端是否配置了餐饮信息
                List<BuildBookPeriphery> foods = buildingBasic.getFoods();
                if (foods==null || foods.size()==0){
                    perMap.put("periphery","美食");
                    perMap.put("adType","4");
                    foods = this.getNewPeripheralMatching(perMap);
                }else{
                    for (BuildBookPeriphery b:foods) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                }
                if(foods!=null && foods.size()>0){
                    allList.addAll(foods);
                }
                //查看前端是否配置了医院信息
                List<BuildBookPeriphery> hospitals = buildingBasic.getHospitals();
                if (hospitals==null || hospitals.size()==0){
                    perMap.put("periphery","医院");
                    perMap.put("adType","5");
                    hospitals = this.getNewPeripheralMatching(perMap);
                }else{
                    for (BuildBookPeriphery b:hospitals) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                }
                if(hospitals!=null && hospitals.size()>0){
                    allList.addAll(hospitals);
                }
                //保存周边信息
                if (allList.size()>0){
                    contentDao.addPeriphery(allList);
                }
                //保存楼盘信息
                contentDao.addBuildingBook(buildingBook);
                return ResultBody.success("楼盘新增成功！");
                //编辑楼盘
            }else{
                String BuildBookID = buildingBasic.getId();
                buildingBook.setID(BuildBookID);
                //删除原标签
                contentDao.delBuildTag(BuildBookID);
                //保存楼栋标签
                if (buildingBasic.getBuildingTags()!=null && buildingBasic.getBuildingTags().size()>0){
                    int num = 0;
                    List<BuildBookTag> tags = new ArrayList<>();
                    for (BuildBookTag buildingTag:buildingBasic.getBuildingTags()) {
                        num++;
                        buildingTag.setBuildBookId(BuildBookID);
                        buildingTag.setCreator(creator);
                        buildingTag.setTagType("1");
                        buildingTag.setListIndex(num+"");
                        tags.add(buildingTag);
                    }
                    contentDao.addHouseTag(tags);
                }
                //删除原产品及户型
                contentDao.delBuildProducts(BuildBookID);
                List<BuildBookProduct> buildBookProducts = buildingBasic.getBuildBookProducts();
                if (buildBookProducts!=null && buildBookProducts.size()>0){
                    for (BuildBookProduct build:buildBookProducts) {
                        build.setBuildBookId(BuildBookID);
                        build.setCreator(creator);
                        contentDao.addBuildBookProduct(build);
                        String ProductId = build.getId();
                        List<Apartment> apartments = build.getApartmentList();
                        if (apartments!=null && apartments.size()>0){
                            for (Apartment apartment:apartments) {
                                apartment.setId(UUID.randomUUID().toString().replaceAll("-",""));
                                apartment.setBuildBookID(BuildBookID);
                                apartment.setCreator(creator);
                                apartment.setProjectID(buildingBasic.getProjectID());
                                apartment.setProductId(ProductId);
                                contentDao.addBuildingApartment(apartment);
                            }
                        }
                    }
                }else{
                    all = false;
                }
                //保存轮播图片
                if (buildingBasic.getBuildingPhotos()!=null && buildingBasic.getBuildingPhotos().size()>0){
                    int num = 1;
                    for (BuildBookPhoto buildingPhoto:buildingBasic.getBuildingPhotos()) {
                        //判断是否删除了图片
                        if ("1".equals(buildingPhoto.getIsClear())){
                            //需要删除的图片ID
                            List<String> ids = new ArrayList<>();
                            List<BuildingPhoto> photos = buildingPhoto.getBuildingPhoto();
                            for (BuildingPhoto b:photos) {
                                //删除图片
                                File file = new File(imgUrl+b.getImgName());
                                if (file!=null){
                                    file.delete();
                                }
                                if (b.getID()!=null && !"".equals(b.getID())){
                                    ids.add(b.getID());
                                }
                            }
                            if (ids.size()>0){
                                String[] ss = ids.toArray(new String[ids.size()]);
                                String idss = "'"+StringUtils.join(ss,"','")+"'";
                                contentDao.delBuildingPhAndVi(idss);
                            }
                        }else{
                            List<BuildingPhoto> photos = buildingPhoto.getBuildingPhoto();
                            String MaterialType = buildingPhoto.getCode();
                            String MaterialTypeDesc = buildingPhoto.getName();
                            if (photos!=null && photos.size()>0){
                                for (BuildingPhoto ph:photos) {
                                    ph.setCreator(creator);
                                    //编辑
                                    if (ph.getID()!=null && !"".equals(ph.getID())){
                                        ph.setListIndex(num+"");
                                        contentDao.updateBuildingPhAndVi(ph);
                                        //新增
                                    }else{
                                        ph.setListIndex(num+"");
                                        ph.setBuildBookID(BuildBookID);
                                        ph.setTypeName("2");
                                        ph.setMaterialType(MaterialType);
                                        ph.setMaterialTypeDesc(MaterialTypeDesc);
                                        contentDao.addBuildingPhAndVi(ph);
                                    }
                                    num++;
                                }
                            }
                        }
                    }
                }
                if(buildBookCityList.size() > 0){
                    contentDao.saveBuildBookCity(buildBookCityList);
                }
                //楼盘周边配套
                List<BuildBookPeriphery> allList = new ArrayList<>();
                //查看前端是否配置了学校信息
                List<BuildBookPeriphery> schools = buildingBasic.getSchools();
                Map perMap = new HashMap();
                perMap.put("lat",buildingBasic.getLatitude());
                perMap.put("lng",buildingBasic.getLongitude());
                perMap.put("buildBookId",buildingBook.getID());
                perMap.put("projectId",buildingBook.getProjectID());
                if (schools==null || schools.size()==0){
                    perMap.put("periphery","学校");
                    perMap.put("adType","1");
                    //判断是否存在学校数据
                    schools = this.getOldPeripheralMatching(perMap);
                    if (schools==null || schools.size()==0){
                        schools = this.getNewPeripheralMatching(perMap);
                        if(schools!=null && schools.size()>0){
                            allList.addAll(schools);
                        }
                    }
                }else{
                    //删除原数据
                    contentDao.delPeriphery(buildingBook.getID(),"1");
                    for (BuildBookPeriphery b:schools) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                    allList.addAll(schools);
                }

                //查看前端是否配置了交通信息
                List<BuildBookPeriphery> traffics = buildingBasic.getTraffics();
                if (traffics==null || traffics.size()==0){
                    perMap.put("periphery","交通");
                    perMap.put("adType","2");
                    //判断是否存在交通数据
                    traffics = this.getOldPeripheralMatching(perMap);
                    if (traffics==null || traffics.size()==0){
                        traffics = this.getNewPeripheralMatching(perMap);
                        if(traffics!=null && traffics.size()>0){
                            allList.addAll(traffics);
                        }
                    }
                }else{
                    //删除原数据
                    contentDao.delPeriphery(buildingBook.getID(),"2");
                    for (BuildBookPeriphery b:traffics) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                    allList.addAll(traffics);
                }
                //查看前端是否配置了购物信息
                List<BuildBookPeriphery> shoppings = buildingBasic.getShoppings();
                if (shoppings==null || shoppings.size()==0){
                    perMap.put("periphery","购物");
                    perMap.put("adType","3");
                    //判断是否存在购物数据
                    shoppings = this.getOldPeripheralMatching(perMap);
                    if (shoppings==null || shoppings.size()==0){
                        shoppings = this.getNewPeripheralMatching(perMap);
                        if(shoppings!=null && shoppings.size()>0){
                            allList.addAll(shoppings);
                        }
                    }
                }else{
                    //删除原数据
                    contentDao.delPeriphery(buildingBook.getID(),"3");
                    for (BuildBookPeriphery b:shoppings) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                    allList.addAll(shoppings);
                }
                //查看前端是否配置了餐饮信息
                List<BuildBookPeriphery> foods = buildingBasic.getFoods();
                if (foods==null || foods.size()==0){
                    perMap.put("periphery","美食");
                    perMap.put("adType","4");
                    //判断是否存在美食数据
                    foods = this.getOldPeripheralMatching(perMap);
                    if (foods==null || foods.size()==0){
                        foods = this.getNewPeripheralMatching(perMap);
                        if(foods!=null && foods.size()>0){
                            allList.addAll(foods);
                        }
                    }
                }else{
                    //删除原数据
                    contentDao.delPeriphery(buildingBook.getID(),"4");
                    for (BuildBookPeriphery b:foods) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                    allList.addAll(foods);
                }
                //查看前端是否配置了医院信息
                List<BuildBookPeriphery> hospitals = buildingBasic.getHospitals();
                if (hospitals==null || hospitals.size()==0){
                    perMap.put("periphery","医院");
                    perMap.put("adType","5");
                    //判断是否存在医院数据
                    hospitals = this.getOldPeripheralMatching(perMap);
                    if (hospitals==null || hospitals.size()==0){
                        hospitals = this.getNewPeripheralMatching(perMap);
                        if(hospitals!=null && hospitals.size()>0){
                            allList.addAll(hospitals);
                        }
                    }
                }else{
                    //删除原数据
                    contentDao.delPeriphery(buildingBook.getID(),"5");
                    for (BuildBookPeriphery b:hospitals) {
                        b.setBuildBookId(buildingBook.getID());
                    }
                    allList.addAll(hospitals);
                }
                //保存周边信息
                if (allList.size()>0){
                    contentDao.addPeriphery(allList);
                }
                contentDao.updateBuildingBook(buildingBook);
                return ResultBody.success("楼盘编辑成功！");
            }

        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if ("1".equals(map.get("addOrEdit")+"")){
                return ResultBody.error(-21_0004,"楼盘新增异常！");
            }else{
                return ResultBody.error(-21_0004,"楼盘编辑异常！");
            }
        }
    }
    public String dealDateFormat(String oldDate) {
        Date date1 = null;
        DateFormat df2 = null;
        try {
            df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (oldDate.contains("T")){
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = df.parse(oldDate);
                SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
                date1 = df1.parse(date.toString());
            }else{
                date1 = df2.parse(oldDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df2.format(date1);
    }


    @Override
    public BuildingBook getBuildingDetail(Map map) {
        String BuildBookID = map.get("BuildBookID")+"";
        BuildingBook buildingBook = contentDao.getBuildingBook(BuildBookID);
        if (buildingBook!=null){
            //获取楼盘产品
            List<BuildBookProduct> buildBookProducts = contentDao.getBuildBookProduct(BuildBookID);
            if (buildBookProducts!=null && buildBookProducts.size()>0){
                for (BuildBookProduct buildBookProduct:buildBookProducts) {
                    Map param = new HashMap();
                    param.put("BuildBookID",BuildBookID);
                    param.put("productId",buildBookProduct.getId());
                    List<Apartment> Apartments = contentDao.getBuildingApartmentList(param);
                    if (Apartments!=null){
                        buildBookProduct.setApartmentList(Apartments);
                    }
                }
                buildingBook.setBuildBookProducts(buildBookProducts);
            }
            //获取轮播图类别
            List<Map> types = contentDao.getBuildingPhotoTypes(BuildBookID);
            List<BuildBookPhoto> BuildingPhotos = new ArrayList<>();
            if (types!=null && types.size()>0){
                List<BuildingPhoto> buildingPhotoList = contentDao.getBuildingPhotoList(BuildBookID);
                for (Map m:types) {
                    String MaterialType = m.get("MaterialType")+"";
                    String MaterialTypeDesc = m.get("MaterialTypeDesc")+"";
                    BuildBookPhoto buildBookPhoto = new BuildBookPhoto();
                    buildBookPhoto.setCode(MaterialType);
                    buildBookPhoto.setName(MaterialTypeDesc);
                    List<BuildingPhoto> photos = new ArrayList<>();
                    if (buildingPhotoList!=null && buildingPhotoList.size()>0){
                        for (int i = 0; i < buildingPhotoList.size(); i++) {
                            BuildingPhoto photo = buildingPhotoList.get(i);
                            if (MaterialType.equals(photo.getMaterialType())){
                                photos.add(photo);
                                buildingPhotoList.remove(i);
                                i--;
                            }
                        }
                    }
                    buildBookPhoto.setBuildingPhoto(photos);
                    BuildingPhotos.add(buildBookPhoto);
                }
            }
            Map param = new HashMap();
            param.put("buildBookId",BuildBookID);
            param.put("tagType","1");
            List<Map> BuildingTags = contentDao.getHouseTag(param);
            List<String> districtList = new ArrayList<>();
            if(buildingBook.getCityName() !=null){
                districtList = contentDao.getDistrict(buildingBook.getCityName());
            }
            buildingBook.setDistrictList(districtList);
            buildingBook.setBuildingPhotos(BuildingPhotos);
            buildingBook.setBuildingTags(BuildingTags);
        }
        return buildingBook;
    }

    @Override
    public List<Dictionary> getBuildingHXD() {
        return contentDao.getBuildingHXD();
    }

    @Override
    public List<Dictionary> getBuildingHXZ(Map map) {
        return contentDao.getBuildingHXZ(map);
    }

    @Override
    public List<Map> getUserProjects(Map map) {
        Map map1=new HashMap();
        map1.put("UserName", map.get("userName")+"");
        map1.put("JobCode", "qygl");
        List<String> fullpath = projectMapper.findFullPath(map1);
        StringBuffer sb = new StringBuffer();
        if (fullpath==null || fullpath.size()==0){
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        map1.put("where", sb.toString());
        List<Map> proMaps = new ArrayList<>();
        List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map1);
        if (projectList!=null && projectList.size()>0){
            for (ProjectVO p:projectList) {
                Map map2 = new HashMap();
                map2.put("projectId",p.getProjectId());
                map2.put("projectName",p.getProjectName());
                proMaps.add(map2);
            }
            return proMaps;
        }else{
            throw new BadRequestException(-10_0000,"用户无项目权限！");
        }

//        Map userInfoMap = authMapper.mGetUserInfo(map1);
//        map.put("jobCode",userInfoMap.get("JobCode")+"");
//        return contentDao.getUserProjects(map);
//        return null;
    }

    @Override
    public Map getCityAndBelongArea(Map map) {
        Map<String,Object> resultMap = contentDao.getCityAndBelongArea(map.get("projectId")+"");
        List<String> districtList = new ArrayList<>();
        if(!resultMap.isEmpty()&&resultMap.containsKey("CityName")){
             districtList = contentDao.getDistrict(String.valueOf(resultMap.get("CityName")));

        }
        resultMap.put("districtList",districtList);
        return resultMap;
    }

    @Override
    public List<Extension> getExtensionList(Map map) {
        return contentDao.getExtensionList(map);
    }

    @Override
    public ResultBody addOrEditExtension(Map map) {
        try{
            String json = JSONObject.toJSONString(map);
            //转化为实体类
            Extension extension = JSONObject.toJavaObject(JSON.parseObject(json),Extension.class);
            extension.setIsNewAdd("0");
            extension.setExtenType("1");
            if (extension.getPhotoWidth()==null || "".equals(extension.getPhotoWidth())){
                extension.setPhotoWidth("430");
            }
            //判断是否编辑
            if ("2".equals(extension.getAddOrEdit())){
                String ID = extension.getID();
                if (ID==null || "".equals(ID)){
                    return ResultBody.error(-21_0006,"必传参数未传！");
                }
                //判断是否删除活动
                if (extension.getIsDel()!=null && "1".equals(extension.getIsDel())){
                    contentDao.updateExtension(extension);
                }else{
                    String projectName = extension.getProjectName();
                    String extenActivityName = extension.getExtenActivityName();
                    String IconName = "策划推广-"+extenActivityName+"-"+projectName+".png";
                    extension.setIconName(IconName);
                    contentDao.updateExtension(extension);
                }
                contentDao.updateExtension(extension);
                return ResultBody.success("策划推广编辑成功！");
            }else {
                String projectId = extension.getProjectID();
                String projectName = extension.getProjectName();
                String dateM =  this.getDateM();
                String IconName = "策划推广-"+extension.getExtenActivityName()+"-"+projectName+".png";
                extension.setIconName(IconName);
                String picNewName = UploadUtils.generateRandonFileName(IconName);// 通过工具类产生新图片名称，防止重名
                extension.setIconUrl(imgEndUrl + dateM + "/" + picNewName);
                extension.setType("1");
                extension.setPhotoName(picNewName);
                contentDao.addExtension(extension);
                String id = extension.getID();
                //查询活动ID，项目编码
                String proNum = contentDao.getProNumId(projectId);
                String paramId = id;
                if ("2".equals(extension.getToUrl())){
                    paramId = IdUtils.getPrimaryKey();
                }
                //生成小程序二维码
                String param = "C_"+paramId+"_"+proNum;
                getminiqrQr(param,picNewName,extension.getToUrl(),extension.getPhotoWidth(),dateM);
                if ("2".equals(extension.getToUrl())){
                    Map erMap = new HashMap();
                    erMap.put("BookId",extension.getBuildBookID());
                    erMap.put("PosterId",id);
                    erMap.put("BookPosterNum", paramId);
                    contentDao.addBookPoster(erMap);
                }
                return ResultBody.success("策划推广新增成功！");
            }
        }catch (Exception e){
            e.printStackTrace();
            if ("1".equals(map.get("addOrEdit"))){
                return ResultBody.error(-21_0007,"策划推广新增异常！");
            }else{
                return ResultBody.error(-21_0007,"策划推广编辑异常！");
            }
        }
    }

    /*
     * 获取 二维码图片
     *
     */
    public void getminiqrQr(String params,String IconName,String ToUrl,String photoWidth,String dateM) {

        //暂时不需要返回，以后改为七牛，或百度云使用
        String codeUrl= imgUrl + dateM + "/" + IconName;
        InputStream inputStream = null;
        RestTemplate rest = new RestTemplate();
        OutputStream outputStream = null;
        int width = 430;
        if (photoWidth!=null && !"".equals(photoWidth)){
            width = Integer.parseInt(photoWidth);
        }
        try
        {
            //首页
            String toUrl = "pages/mainMarketing/index/index";
            if ("2".equals(ToUrl)){
                //楼盘
                toUrl = "pages/mainMarketing/buildingDetails/index";
            }else if ("3".equals(ToUrl)){
                //楼书户型
                toUrl = "building/pages/houseDetails/index";
            }else if ("4".equals(ToUrl)){
                //活动
                toUrl = "activity/pages/activityDetails/index";
            }else if ("5".equals(ToUrl)){
                //新闻
                toUrl = "pages/mainMarketing/newsLsit/details";
            }
//            String accessToken = contentDao.getWXToken();
            String accessToken = weChatAccessTokenUtils.getToken();
            String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken;
            Map<String, Object> param = new HashMap<>();
            param.put("scene", params);
            param.put("page", toUrl);
            param.put("width", width);
            param.put("auto_color", false);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            HttpEntity requestEntity = new HttpEntity(param, headers);
            ResponseEntity<byte[]> entity = rest.exchange(url, HttpMethod.POST, requestEntity, byte[].class,
                    new Object[0]);
            System.out.println("调用小程序生成微信永久小程序码URL接口返回结果:" + entity.getBody());
            byte[] result = entity.getBody();
            inputStream = new ByteArrayInputStream(result);

            File file = new File(codeUrl);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outputStream = new FileOutputStream(file);
            int len = 0;
            byte[] in_b  = null;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally{
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                //让系统回收资源，但不一定是回收刚才设成null的资源，可能是回收其他没用的资源。
                System.gc();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        this.formUpload(uploadUrl,codeUrl);
    }
    @Override
    public void feedbackExport(HttpServletRequest request, HttpServletResponse response,Map map) {
        String json = JSONObject.toJSONString(map);
        //转化为实体类
        ExcelForm excelForm = JSONObject.toJavaObject(JSON.parseObject(json),ExcelForm.class);
//        Map paramMap = new HashMap();
        if (excelForm.getBeginTime()!=null && excelForm.getEndTime()!=null && !"".equals(excelForm.getBeginTime()) && !"".equals(excelForm.getEndTime())){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                map.put("beginTime",sf.format(sf.parse(excelForm.getBeginTime())));
                map.put("endTime",sf.format(sf.parse(excelForm.getEndTime())));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(map.get("projectLists") != null && !"".equals(String.valueOf(map.get("projectLists"))) && !"null".equals(String.valueOf(map.get("projectLists")))){
            String [] ids = String.valueOf(map.get("projectLists")).split(",");
            map.put("projectList",Arrays.asList(ids));
        }
        if(map.get("buildBookIds") != null && !"".equals(String.valueOf(map.get("buildBookIds"))) && !"null".equals(String.valueOf(map.get("buildBookIds")))){
            String [] ids = String.valueOf(map.get("buildBookIds")).split(",");
            map.put("buildBookIdList",Arrays.asList(ids));
        }
        if(map.get("statusLists") != null && !"".equals(String.valueOf(map.get("statusLists"))) && !"null".equals(String.valueOf(map.get("statusLists")))){
            String [] ids = String.valueOf(map.get("statusLists")).split(",");
            map.put("statusList",Arrays.asList(ids));
        }
        if(map.get("problemTypeLists") != null && !"".equals(String.valueOf(map.get("problemTypeLists"))) && !"null".equals(String.valueOf(map.get("problemTypeLists")))){
            String [] ids = String.valueOf(map.get("problemTypeLists")).split(",");
            map.put("problemTypeList",Arrays.asList(ids));
        }
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<Feedback> list = contentDao.getFeedbackList(map);
        try{
            //导出的文档下面的名字
            String excelName = "反馈明细";
            if (list!=null && list.size()>0){
                //循环遍历所有数据
                for (int z = 0; z < list.size(); z++) {
                    Feedback map1 = list.get(z);
                    if ("1".equals(map1.getStatus())){
                        map1.setStatus("已处理");
                    }else{
                        map1.setStatus("未处理");
                    }
                    Object[] oArray = map1.toFeedbackData();
                    dataset.add(oArray);

                }
                String[] headers = list.get(0).toFeedbackTitle;
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(excelName,headers,dataset,excelName,response,null);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void feedbackHuiExport(HttpServletRequest request, HttpServletResponse response,Map map) {
        String json = JSONObject.toJSONString(map);
        //转化为实体类
        ExcelForm excelForm = JSONObject.toJavaObject(JSON.parseObject(json),ExcelForm.class);
//        Map paramMap = new HashMap();
        if (excelForm.getBeginTime()!=null && excelForm.getEndTime()!=null && !"".equals(excelForm.getBeginTime()) && !"".equals(excelForm.getEndTime())){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                map.put("beginTime",sf.format(sf.parse(excelForm.getBeginTime())));
                map.put("endTime",sf.format(sf.parse(excelForm.getEndTime())));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(map.get("projectLists") != null && !"".equals(String.valueOf(map.get("projectLists"))) && !"null".equals(String.valueOf(map.get("projectLists")))){
            String [] ids = String.valueOf(map.get("projectLists")).split(",");
            map.put("projectList",Arrays.asList(ids));
        }
        if(map.get("FeedbackModuleList") != null && !"".equals(String.valueOf(map.get("FeedbackModuleList"))) && !"null".equals(String.valueOf(map.get("buildBookIds")))){
            String [] ids = String.valueOf(map.get("FeedbackModuleList")).split(",");
            map.put("FeedbackModuleList",Arrays.asList(ids));
        }
        if(map.get("statusLists") != null && !"".equals(String.valueOf(map.get("statusLists"))) && !"null".equals(String.valueOf(map.get("statusLists")))){
            String [] ids = String.valueOf(map.get("statusLists")).split(",");
            map.put("statusList",Arrays.asList(ids));
        }
        if(map.get("problemTypeLists") != null && !"".equals(String.valueOf(map.get("problemTypeLists"))) && !"null".equals(String.valueOf(map.get("problemTypeLists")))){
            String [] ids = String.valueOf(map.get("problemTypeLists")).split(",");
            map.put("problemTypeList",Arrays.asList(ids));
        }
        ArrayList<Object[]> dataset = new ArrayList<>();
        List<Feedback> list = contentDao.getAllFeedbackList(map);
        try{
            //导出的文档下面的名字
            String excelName = "旭客汇反馈明细";
            if (list!=null && list.size()>0){
                //循环遍历所有数据
                for (int z = 0; z < list.size(); z++) {
                    Feedback map1 = list.get(z);
                    if ("1".equals(map1.getStatus())){
                        map1.setStatus("已处理");
                    }else{
                        map1.setStatus("未处理");
                    }
                    Object[] oArray = map1.toAllFeedbackData();
                    dataset.add(oArray);

                }
                String[] headers = list.get(0).toAllFeedbackTitle;
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(excelName,headers,dataset,excelName,response,null);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public List<Map> getCitysByJobId(Map map) {
        //查询岗位是否是系统管理员
//        String jobCode = contentDao.isAdmin(map.get("JobID")+"");
//        if (jobCode!=null && ("10001".equals(jobCode) || "系统管理员".equals(jobCode))){
//            return contentDao.getAllCitys();
//        }else{
//            return contentDao.getCityByJobId(map.get("JobID")+"");
//        }
        String userId = null;
        if(map !=null &&map.containsKey("userId")){
            userId = String.valueOf(map.get("userId"));
        }else{
            userId = SecurityUtils.getUserId();
        }

        //查询所有岗位
        List<Map> jobMap = contentDao.getAllJobs(userId);
        if (jobMap!=null && jobMap.size()>0){
            //查询岗位是否是系统管理员
            boolean isAdmin = false;
            StringBuffer sb = new StringBuffer();
            for (Map map1:jobMap) {
                if ("10001".equals(map1.get("JobCode")) || "系统管理员".equals(map1.get("JobCode"))){
                    isAdmin = true;
                }
                sb.append("'"+map1.get("id")+"',");
            }
            if (isAdmin){
                return contentDao.getAllCitys();
            }else{
                String JobIDs = sb.toString().substring(0,sb.toString().length()-1);
               Map map1 = new HashMap();
                map1.put("JobIDs",JobIDs);
                List<String> fullpath = projectMapper.findFullPathByJobs(map1);
                StringBuffer sb1 = new StringBuffer();
                if (fullpath==null || fullpath.size()==0){
                    throw new BadRequestException(-10_0000,"用户无项目权限！");
                }
                for (int i = 0; i < fullpath.size(); i++) {
                    if (i==0){
                        sb1.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                    }else{
                        sb1.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                    }
                }
                map1.put("where", sb1.toString());
                List<String> proMaps = new ArrayList<>();
                List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map1);
                if (projectList!=null && projectList.size()>0){
                    for (ProjectVO p:projectList) {
                        proMaps.add(p.getProjectId());
                    }
                    List<Map> citys = contentDao.getCityByProS(proMaps);
                    return citys;
                }else{
                    throw new BadRequestException(-10_0000,"用户无项目权限！");
                }
//                return contentDao.getCityByJobId(JobIDs);
            }
        }else{
            return null;
        }
    }

    @Override
    public void getWinCode(HttpServletResponse response, Extension extension) {
        response.setContentType("application/octet-stream");
        try{
            String fileName = URLEncoder.encode(extension.getIconName(),"utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename="+fileName+";filename*='utf-8'"+fileName);
            File file = new File(extension.getIconUrl());
            if (file!=null){
                response.setContentLength((int)file.length());
                FileUtils.copyFile(file,response.getOutputStream());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public News getNewsDetail(Map map) {

        return contentDao.getNewsDetail(map);

    }

    @Override
    public List<Map> getZXBZ() {
        return contentDao.getZXBZ();
    }

    @Override
    public String getBuildingByProId(Map map) {
        return contentDao.getBuildingByProId(map);
    }

    @Override
    public List<BuildingPoster> getBuildingPosterList(Map map) {
        return contentDao.getBuildingPosterList(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBuildingPoster(BuildingPoster buildingPoster) {
        contentDao.addBuildingPoster(buildingPoster);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBuildingPoster(BuildingPoster buildingPoster) {
        contentDao.updateBuildingPoster(buildingPoster);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBuildingPoster(String id) {
        contentDao.delBuildingPoster(id);
    }


    @Override
    public List<BuildBookPeriphery> getOldPeripheralMatching(Map map) {
        String buildBookId = map.get("buildBookId")+"";
        String adType = map.get("adType")+"";
        //查询原来配置的周边配套
        List<BuildBookPeriphery> data1 = contentDao.getPeriphery(buildBookId,adType);
        return data1;
    }

    @Override
    public List<Map> getBuildBookProperty(Map map) {
        List<Map> result = new ArrayList<>();
//        String projectId = map.get("projectId")+"";
        //所有项目ID
//        StringBuffer idsb = new StringBuffer();
//        //根据主项目ID查询所有分期项目ID
//        List<String> ids = contentDao.getProjects(projectId);
//        if (null != ids && ids.size() > 0) {
//            for (int i = 0; i < ids.size(); i++) {
//                if (i==ids.size()-1){
//                    idsb.append("'"+ids.get(i)+"'");
//                }else{
//                    idsb.append("'"+ids.get(i)+"',");
//                }
//            }
//        }
        //查询楼栋业态大类及子类
        //查询大类
        List<DictDesc> mainList = contentDao.getBuildingMainDict();
        for (DictDesc d:mainList) {
            Map main = new HashMap();
            main.put("label",d.getDictName());
            main.put("value",d.getDictCode());
            List<Map> children = new ArrayList<>();
            List<String> exits = new ArrayList<>();
            //查询子类
            List<DictDesc> subList = contentDao.getBuildingSubDict(d.getID());
            for (DictDesc dict:subList) {
                if (!exits.contains(dict.getDictName())){
                    Map sub = new HashMap();
                    sub.put("label",dict.getDictName());
                    sub.put("value",dict.getDictCode());
                    exits.add(dict.getDictName());
                    children.add(sub);
                }
            }
            main.put("children",children);
            result.add(main);
        }

//        String sql = "select s.x_productCode from VS_XK_S_BUILDING s INNER JOIN VS_XK_S_ROOM room on room.BldGUID = s.BldGUID" +
//                " where s.PrjectFGUID in ("+idsb.toString()+")  GROUP BY s.x_productCode";
//        List<Map<String, Object>> bldMaps = DbTest.getObjects(sql);
//        if (bldMaps!=null && bldMaps.size()>0){
//            for (Map<String, Object> mapb:bldMaps) {
//                for (DictDesc d:subAllList) {
//                    if (d.getDictCode().equals(mapb.get("x_productCode")+"")){
//                        if (!exits.contains(d.getDictName())){
//                            exits.add(d.getDictName());
//                            Map mapf = new HashMap();
//                            mapf.put("DictCode",d.getDictCode());
//                            mapf.put("DictName",d.getDictName());
//                            result.add(mapf);
//                        }
//                    }
//                }
//            }
//        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addAllBuildBookPeripheralMatching() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SysLog sysLog = new SysLog();
        sysLog.setExecutTime(sf.format(new Date()));
        sysLog.setTaskName("初始化周边配套开始");
        sysLog.setNote("初始化周边配套开始");
        messageMapper.insertLogs(sysLog);
        try{
            //查询未维护周边配套的楼盘信息
            List<Map> builds = contentDao.getBuildBookNotPeriphery();
            //保存学校信息
            List<BuildBookPeriphery> schools = new ArrayList<>();
            for (Map map:builds) {
                map.put("periphery","学校");
                map.put("adType","1");
                List<BuildBookPeriphery> school = this.getNewPeripheralMatching(map);
                if (school!=null && school.size()>0){
                    schools.addAll(school);
                }
            }
            contentDao.addPeriphery(schools);
            //保存医院信息
            List<BuildBookPeriphery> hospitals = new ArrayList<>();
            for (Map map:builds) {
                map.put("periphery","医院");
                map.put("adType","5");
                List<BuildBookPeriphery> hospital = this.getNewPeripheralMatching(map);
                if (hospital!=null && hospital.size()>0){
                    hospitals.addAll(hospital);
                }
            }
            contentDao.addPeriphery(hospitals);
            //保存交通信息
            List<BuildBookPeriphery> traffics = new ArrayList<>();
            for (Map map:builds) {
                map.put("periphery","交通");
                map.put("adType","2");
                List<BuildBookPeriphery> traffic = this.getNewPeripheralMatching(map);
                if (traffic!=null && traffic.size()>0){
                    traffics.addAll(traffic);
                }
            }
            contentDao.addPeriphery(traffics);
            //保存购物信息
            List<BuildBookPeriphery> shoppings = new ArrayList<>();
            for (Map map:builds) {
                map.put("periphery","购物");
                map.put("adType","3");
                List<BuildBookPeriphery> shopping = this.getNewPeripheralMatching(map);
                if (shopping!=null && shopping.size()>0){
                    shoppings.addAll(shopping);
                }
            }
            contentDao.addPeriphery(shoppings);
            //保存美食信息
            List<BuildBookPeriphery> foods = new ArrayList<>();
            for (Map map:builds) {
                map.put("periphery","美食");
                map.put("adType","4");
                List<BuildBookPeriphery> food = this.getNewPeripheralMatching(map);
                if (food!=null && food.size()>0){
                    foods.addAll(food);
                }
            }
            contentDao.addPeriphery(foods);
            SysLog sysLog1 = new SysLog();
            sysLog1.setExecutTime(sf.format(new Date()));
            sysLog1.setTaskName("初始化周边配套完成");
            sysLog1.setNote("初始化周边配套开始");
            messageMapper.insertLogs(sysLog1);
            return "成功！！";
        }catch (Exception e){
            SysLog sysLog1 = new SysLog();
            sysLog1.setExecutTime(sf.format(new Date()));
            sysLog1.setTaskName("初始化周边配套异常");
            sysLog1.setNote("初始化周边配套异常");
            messageMapper.insertLogs(sysLog1);
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "异常！！";
        }


    }

    @Override
    public List<Map> getHxTag() {
        return contentDao.getHxTag();
    }

    @Override
    public List<Map> getLpTag() {
        return contentDao.getLpTag();
    }

    @Override
    public List<Map> getHXCX() {
        return contentDao.getHXCX();
    }

    @Override
    public String updateBuildingApartmentStatus(Map map) {
        contentDao.updateBuildingApartmentStatus(map);
        return "成功！";
    }

    @Override
    public Map getBuildingApartmentDetail(Map map) {

        String id = map.get("id")+"";
        //获取户型基本信息
        Map detail = contentDao.getBuildingApartmentDetail(id);
        if (detail!=null){
            //获取户型轮播图
            map.put("HouseTypeID",id);
            map.put("TypeName","1");
            List<Map> HouseImgr = contentDao.getHouseImg(map);
            //获取户型视频
            map.put("TypeName","2");
            List<Map> HouseImgVr = contentDao.getHouseImg(map);
            //获取户型解析
            List<Map> HouseTagDescr = contentDao.getHouseAnalysis(map);
            //获取户型标签
            map.put("houseTypeId",id);
            map.put("tagType","2");
            List<Map> HouseTagr = contentDao.getHouseTag(map);
            detail.put("HouseImgr",HouseImgr);
            detail.put("HouseImgVr",HouseImgVr);
            detail.put("HouseTagDescr",HouseTagDescr);
            detail.put("HouseTagr",HouseTagr);
        }
        return detail;
    }

    @Override
    public List<Map> getBuildingHX(Map map) {
        List<Map> Apartments = contentDao.getBuildingApartmentAll(map);
        //获取户型基本信息
        if (Apartments!=null && Apartments.size()>0){
            map.put("BuildBookID",null);
            for (Map aMap:Apartments) {
                //获取户型轮播图
                map.put("HouseTypeID",aMap.get("ID"));
                map.put("TypeName","1");
                List<Map> HouseImgr = contentDao.getHouseImg(map);
                //获取户型视频
                map.put("TypeName","2");
                List<Map> HouseImgVr = contentDao.getHouseImg(map);
                //获取户型解析
                List<Map> HouseTagDescr = contentDao.getHouseAnalysis(map);
                //获取户型标签
                map.put("houseTypeId",aMap.get("ID"));
                map.put("tagType","2");
                List<Map> HouseTagr = contentDao.getHouseTag(map);
                aMap.put("HouseImgr",HouseImgr);
                aMap.put("HouseImgVr",HouseImgVr);
                aMap.put("HouseTagDescr",HouseTagDescr);
                aMap.put("HouseTagr",HouseTagr);
            }
        }
        return Apartments;
    }

    public List<BuildBookPeriphery> getNewPeripheralMatching(Map map){
        int pageIndex = 1;
        int pageSize = 20;
        try{
            String range = "1000";
            if(map.get("range")!=null && !"".equals(map.get("range"))){
                range = map.get("range")+"";
            }
            //拼接参数
            String keyword = URLEncoder.encode(map.get("periphery")+"","utf-8");
            String nearby = map.get("lat")+","+map.get("lng")+","+range;
            String param = "keyword="+keyword+"&boundary=nearby("+nearby+")&key="+mapAppKey+"&orderby=_distance&page_size="+pageSize+"&page_index="+pageIndex;
            String buildBookId = map.get("buildBookId")+"";
            String adType = map.get("adType")+"";
            String projectId = map.get("projectId")+"";
            if ("学校".equals(map.get("periphery"))){
                String filter = URLEncoder.encode("大学,中学,小学,幼儿园","utf-8");
                param = param+"&filter=category="+filter;
            }
            if ("医院".equals(map.get("periphery"))){
                String filter = URLEncoder.encode("<>肿瘤,传染病,精神病","utf-8");
                param = param+"&filter=category"+filter;
            }

            //按照关键字搜索
            String result = HttpClient.sendGetRequest(mapUrl,param);
            if (result!=null && !"".equals(result)){
                JSONObject json = JSON.parseObject(result);
                if ("0".equals(json.getString("status"))){
                    List<BuildBookPeriphery> perList = new ArrayList<>();
                    JSONArray list = json.getJSONArray("data");
                    for (Object json1:list) {
                        JSONObject json2 = (JSONObject)json1;
                        BuildBookPeriphery buildBookPeriphery = new BuildBookPeriphery();
                        buildBookPeriphery.setTitle(json2.getString("title"));
                        buildBookPeriphery.setDistance(json2.getString("_distance"));
                        buildBookPeriphery.setAddress(json2.getString("address"));
                        buildBookPeriphery.setTel(json2.getString("tel"));
                        buildBookPeriphery.setLat(json2.getJSONObject("location").getString("lat"));
                        buildBookPeriphery.setLng(json2.getJSONObject("location").getString("lng"));
                        buildBookPeriphery.setAdType(adType);
                        buildBookPeriphery.setBuildBookId(buildBookId);
                        try{
                            buildBookPeriphery.setCreator(SecurityUtils.getUserId());
                        }catch (Exception e){
                            buildBookPeriphery.setCreator("admin");
                        }
                        buildBookPeriphery.setProjectId(projectId);
                        perList.add(buildBookPeriphery);
                    }
                    return perList;
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public ResultBody getPeripheralMatching(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        try{
            String range = "1000";
            if(map.get("range")!=null && !"".equals(map.get("range"))){
                range = map.get("range")+"";
            }

            String adType = map.get("adType")+"";
            String projectId = map.get("projectId")+"";

            //拼接参数
            String keyword = URLEncoder.encode(map.get("periphery")+"","utf-8");
            String nearby = map.get("lat")+","+map.get("lng")+","+range;
            String param = "keyword="+keyword+"&boundary=nearby("+nearby+")&key="+mapAppKey+"&orderby=_distance&page_size="+pageSize+"&page_index="+pageIndex;

            if ("1".equals(adType)){
                String filter = URLEncoder.encode("教育学校","utf-8");
                param = param+"&filter=category="+filter;
            }else if ("2".equals(adType)){
                String filter = URLEncoder.encode("基础设施","utf-8");
                param = param+"&filter=category="+filter;
            }else if ("3".equals(adType)){
                String filter = URLEncoder.encode("购物","utf-8");
                param = param+"&filter=category="+filter;
            }else if ("4".equals(adType)){
                String filter = URLEncoder.encode("美食","utf-8");
                param = param+"&filter=category="+filter;
            }else if ("5".equals(adType)){
                String filter = URLEncoder.encode("医疗保健","utf-8");
                param = param+"&filter=category="+filter;
            }
            //按照关键字搜索
            String result = HttpClient.sendGetRequest(mapUrl,param);
            if (result!=null && !"".equals(result)){
                JSONObject json = JSON.parseObject(result);
                if ("0".equals(json.getString("status"))){
                    Map resultMap = new HashMap();
                    List<Map> dataList = new ArrayList<>();
                    JSONArray list = json.getJSONArray("data");
                    for (Object json1:list) {
                        JSONObject json2 = (JSONObject)json1;
                        Map result1 = new HashMap();
                        result1.put("title",json2.getString("title"));
                        result1.put("distance",json2.getString("_distance"));
                        result1.put("address",json2.getString("address"));
                        result1.put("tel",json2.getString("tel"));
                        result1.put("lat",json2.getJSONObject("location").getString("lat"));
                        result1.put("lng",json2.getJSONObject("location").getString("lng"));
                        result1.put("adType",adType);
                        result1.put("creator",SecurityUtils.getUserId());
                        result1.put("projectId",projectId);
                        dataList.add(result1);
                    }
                    String count = json.getString("count");
                    resultMap.put("total",Integer.parseInt(count));
                    resultMap.put("data",dataList);
                    return ResultBody.success(resultMap);
                }else{
                    Map resultMap = new HashMap();
                    List<Map> dataList = new ArrayList<>();
                    String message = "";
                    if ("310".equals(json.getString("status"))){
                        message = "请求参数信息有误";
                    }else if("311".equals(json.getString("status"))){
                        message = "Key格式错误";
                    }else if("306".equals(json.getString("status"))){
                        message = "请求有护持信息请检查字符串";
                    }else if("110".equals(json.getString("status"))){
                        message = "请求来源未被授权";
                    }
                    resultMap.put("total",0);
                    resultMap.put("data",dataList);
                    return ResultBody.success(resultMap);
                }
            }else{
                return ResultBody.error(-21_0006,"获取周边配套未获取到数据！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0006,"获取周边配套异常！");
        }

    }

    @Override
    public ResultBody getBuildingPhotoTO(Map map) {
        if (map.get("JumpType")==null || "".equals(map.get("JumpType"))){
            return ResultBody.error(-21000,"必传参数未传！");
        }
        List<Map> list = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        //查询有权限的项目
        if ("1".equals(map.get("JumpType")+"") || "4".equals(map.get("JumpType")+"")){
            Map mapss=new HashMap();
            mapss.put("UserName", SecurityUtils.getUsername());
            Map userInfoMap = authMapper.mGetUserInfo(mapss);
            List<String> fullpath = projectMapper.findFullPath(mapss);
            StringBuffer sbs = new StringBuffer();
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sbs.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sbs.append("or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            //获取有权限的项目
            List<Map> mapList = projectMapper.findProjectListByUserName( SecurityUtils.getUsername(),"",userInfoMap.get("JobCode").toString(),sbs.toString());
            if (mapList!=null && mapList.size()>0){
                for (Map proMap:mapList) {
                    ids.add(proMap.get("projectId")+"");
                }
            }else{
                return ResultBody.success(list);
            }

        } else if ("5".equals(map.get("JumpType")+"")){
            //跳转户型
            if (map.get("BuildBookId")==null || "".equals(map.get("BuildBookId"))){
                return ResultBody.error(-21000,"必传参数未传！");
            }
            String buildBookId = map.get("BuildBookId")+"";
            //根据楼盘获取户型
            list = contentDao.getBuildHouse(buildBookId);
        } else{
            //查询有权限的城市
            //修改为按照账号查询所有岗位城市
            String userId = SecurityUtils.getUserId();
            //查询所有岗位
            List<Map> jobMap = contentDao.getAllJobs(userId);
            if (jobMap!=null && jobMap.size()>0){
                //查询岗位是否是系统管理员
                boolean isAdmin = false;
                StringBuffer sb = new StringBuffer();
                for (Map map1:jobMap) {
                    if ("10001".equals(map1.get("JobCode")) || "系统管理员".equals(map1.get("JobCode"))){
                        isAdmin = true;
                        break;
                    }
                    sb.append("'"+map1.get("id")+"',");
                }
                if (isAdmin){

                }else{
                    String JobIDs = sb.toString().substring(0,sb.toString().length()-1);
                    List<Map> citys = contentDao.getCityByJobId(JobIDs);
                    if (citys!=null && citys.size()>0){
                        for (Map city:citys) {
                            ids.add(city.get("CityID")+"");
                        }
                    }else{
                        return ResultBody.success(list);
                    }
                }
            }else{
                return ResultBody.success(list);
            }
        }
        //判断跳转类型 1：项目微楼书 2：新闻 3：不跳转 4：活动 5:户型
        if ("1".equals(map.get("JumpType")+"")){
            list = contentDao.getBuildingByProIds(ids);
        }else if ("2".equals(map.get("JumpType")+"")){
            list = contentDao.getNewsByCityIds(ids);
        }else if ("4".equals(map.get("JumpType")+"")){
            list = contentDao.getActivityByProIds(ids);
        }
        return ResultBody.success(list);
    }

    @Override
    public List<BuildingPhoto> getBuildingPhotosOrder(Map map) {
        return contentDao.getBuildingPhotosOrder(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateBuildingPhotoOrder(Map map) {
        //获取排序图片集合
        JSONArray array = JSON.parseArray(JSON.toJSONString(map.get("list")));
        try{
            for (Object o:array) {
                //转化为实体类
                BuildingPhoto buildingPhoto = JSONObject.toJavaObject(JSON.parseObject(JSON.toJSONString(o)),BuildingPhoto.class);
                contentDao.updateBuildingPhoto(buildingPhoto);
            }
            return ResultBody.success("更新成功!");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-200011,"更新异常！");
        }
    }

    @Override
    public List<BuildingBook> getBuildingOrder(Map map) {

        return contentDao.getBuildingOrder(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateBuildingOrder(Map map) {
        //获取楼盘集合
        JSONArray array = JSON.parseArray(JSON.toJSONString(map.get("list")));
        try{
            for (Object o:array) {
                //转化为实体类
                BuildingBook buildingBook = JSONObject.toJavaObject(JSON.parseObject(JSON.toJSONString(o)),BuildingBook.class);
                contentDao.updateBuildingOrder(buildingBook);
                contentDao.updateBuildCityOrder(buildingBook);
            }
            return ResultBody.success("更新成功!");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-200011,"更新异常！");
        }
    }

    /**
     *查询楼盘问题列表
     * @param map
     * @return
     */
    @Override
    public ResultBody getBuildBookProblemList(Map map) {
        PageHelper.startPage((int) map.get("current"), (int) map.get("size"));
        Page<BuildBookProblem> list = contentDao.getBuildBookProblemList(map);
        return ResultBody.success(new PageInfo<BuildBookProblem>(list));
    }

    /**
     *保存问题
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveBuildBookProblem(Map map) {
        Integer listIndex = contentDao.getBuildBookProblemListIndex(String.valueOf(map.get("buildBookID")));
        if(listIndex == null){
            map.put("listIndex",0);
        }else{
            map.put("listIndex",listIndex + 1);
        }
        return ResultBody.success(contentDao.saveBuildBookProblem(map));
    }

    /**
     *更新问题
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateBuildBookProblem(Map map) {
        return ResultBody.success(contentDao.updateBuildBookProblem(map));
    }

    /**
     *更新排序
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateBuildBookProblemListIndex(Map map) {
        List<Map> mapList = (List<Map>) map.get("list");
        return ResultBody.success(contentDao.updateBuildBookProblemListIndex(mapList));
    }

    /**
     * 查询楼盘
     * @param map
     * @return
     */
    @Override
    public ResultBody getBuildBookList(Map map) {
        return ResultBody.success(contentDao.getBuildBookList(map));
    }

    /**
     * 查询问题(排序使用)
     * @param buildBookId
     * @return
     */
    @Override
    public ResultBody getBuildBookProblemListByProjectId(String buildBookId) {
        return ResultBody.success(contentDao.getBuildBookProblemListByProjectId(buildBookId));
    }

    @Override
    public ResultBody getIsOkCity(Map map) {
        if (map==null || map.get("CityID")==null || "".equals(map.get("CityID")+"")){
            return ResultBody.error(-21_0006,"必传参数未传！！");
        }
        String id = contentDao.getIsOkCity(map);
        if (id==null){
            return ResultBody.success(1);
        }else{
            if (map.get("id")!=null && id.equals(map.get("id")+"")){
                return ResultBody.success(1);
            }
            return ResultBody.success(0);
        }
    }

    @Override
    public ResultBody getBookProblemNum(String buildBookID) {
        return ResultBody.success(contentDao.getBookProblemNum(buildBookID));
    }

    /***
    *
     * @param request
     * @param response
     * @param
    *@return {}
    *@throws
    *@Description: 楼盘常见问题导出
    *@author FuYong
    *@date 2020/7/6 17:16
    */
    @Override
    public void bookProblemNumExport(HttpServletRequest request, HttpServletResponse response, String param) {
        cn.visolink.system.allpeople.contentManagement.model.ExcelForm paramMap = JSONObject.parseObject(param,cn.visolink.system.allpeople.contentManagement.model.ExcelForm.class);
        //声明变量
        String excelName = null;
        String basePath = "templates";
        String templatePath = "";
        ArrayList<Object[]> dataset = new ArrayList<>();
        //转化参数
        Map map = new HashMap();
        if (!StringUtils.isBlank(paramMap.getProjectIds())) {
            map.put("projectList", Arrays.asList(paramMap.getProjectIds().split(",")));
        } else {
            map.put("projectList", null);
        }
        if (!StringUtils.isBlank(paramMap.getBuildBookId())) {
            map.put("buildBookId", paramMap.getBuildBookId());
        } else {
            map.put("buildBookId", null);
        }
        if (!StringUtils.isBlank(paramMap.getStatusStr())) {
            map.put("statusList", paramMap.getStatusStr().split(","));
        } else {
            map.put("statusList", null);
        }
        if (!StringUtils.isBlank(paramMap.getCreateUserName())) {
            map.put("createUserName", paramMap.getCreateUserName());
        } else {
            map.put("createUserName", null);
        }
        if (!StringUtils.isBlank(paramMap.getProblemDescribe())) {
            map.put("problemDescribe", paramMap.getProblemDescribe());
        } else {
            map.put("problemDescribe", null);
        }

        List<BuildBookProblem> buildBookProblemList = contentDao.getBuildBookProblemList(map);
        try {
            excelName = "楼盘常见问题台账";
            templatePath = basePath + File.separator + "problem.xlsx";
            int num = 0;
            for (BuildBookProblem model : buildBookProblemList) {
                num++;
                model.setRownum(num);
                Object[] oArray = model.toExproData();
                dataset.add(oArray);
            }
            ExcelExportUtil excelExportUtil = new ExcelExportUtil();
            excelExportUtil.exportExcelTemplate(templatePath, dataset, excelName, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateBuildingPhotoStatus(Map map) {
        if (map==null || map.get("id")==null || "".equals(map.get("id")+"")
                || map.get("Status")==null || "".equals(map.get("Status")+"")){
            return ResultBody.error(-21_0006,"必传参数未传！！");
        }
        try{
            contentDao.updateBuildingPhotoStatus(map);
            return ResultBody.success("修改成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0006,"修改状态异常！！");
        }

    }

    @Override
    public ResultBody getAllCityList(String cityId) {
        List<Map> cityList = contentDao.getAllCitys();
        if(cityList.size() > 0){
            cityList = cityList.stream()
                    .filter(f -> !"00000000-0000-0000-0000-000000000000".equals(String.valueOf(f.get("CityID")))
                                && !cityId.equals(String.valueOf(f.get("CityID"))))
                    .collect(Collectors.toList());
        }
        return ResultBody.success(cityList);
    }

    @Override
    public ResultBody getAllProjectList() {
        List<Map>  mapList = contentDao.getAllProject();
        return ResultBody.success(mapList);
    }


    private void setDataToProjectCell(Workbook targetWorkBook, Row row2, int i, XSSFRow positionRow, Feedback jobrow, CellStyle style) {
        CellStyle cs = targetWorkBook.createCellStyle();
        cs.cloneStyleFrom(style);
        cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        CellStyle cc = cs;

        XSSFCell cell1 = positionRow.createCell(0);
        cell1.setCellStyle(cc);
        cell1.setCellValue(Double.valueOf(jobrow.getNum()));
        XSSFCell cell2 = positionRow.createCell(1);
        cell2.setCellStyle(cc);
        cell2.setCellValue(jobrow.getFeedBackUserName());
        XSSFCell cell3 = positionRow.createCell(2);
        cell3.setCellStyle(cc);
        cell3.setCellValue(jobrow.getFeedBackUserMobile());
        XSSFCell cell4 = positionRow.createCell(3);
        cell4.setCellStyle(cc);
        cell4.setCellValue(jobrow.getFeedBackUserRole());
        XSSFCell cell5 = positionRow.createCell(4);
        cell5.setCellStyle(cc);
        cell5.setCellValue(jobrow.getCreateTime());
        XSSFCell cell6 = positionRow.createCell(5);
        cell6.setCellStyle(cc);
        cell6.setCellValue(jobrow.getProblemDesc());
        XSSFCell cell7 = positionRow.createCell(6);
        cell7.setCellStyle(cc);
        if ("1".equals(jobrow.getStatus())){
            cell7.setCellValue("已处理");
        }else{
            cell7.setCellValue("待处理");
        }
        XSSFCell cell8 = positionRow.createCell(7);
        cell8.setCellStyle(cc);
        cell8.setCellValue(jobrow.getHandleUserName());
        XSSFCell cell9 = positionRow.createCell(8);
        cell9.setCellStyle(cc);
        cell9.setCellValue(jobrow.getHandleTime());
        XSSFCell cell10 = positionRow.createCell(9);
        cell10.setCellStyle(cc);
        cell10.setCellValue(jobrow.getHandleDesc());
    }

    //上传视频公共方法
    private String uploadVideo(MultipartFile file){
        String fileName = file.getOriginalFilename();// 获取原来视频的名字
        String vdieoNewName = UploadUtils.generateRandonFileName(fileName);// 通过工具类产生新视频名称，防止重名
        File dest = new File(imgUrl+vdieoNewName);
        try{
            //判断文件父目录是否存在
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdir();
            }
            //写入文件
            file.transferTo(dest);
            return vdieoNewName;
        }catch (Exception e){
            e.printStackTrace();
            return "E";
        }
    }
    //上传图片公共方法
    private String uploadFile(MultipartFile file){
        String fileName = file.getOriginalFilename();// 获取图片原来的名字
        String picNewName = UploadUtils.generateRandonFileName(fileName);// 通过工具类产生新图片名称，防止重名
        //临时文件名称
        String tempName = UploadUtils.generateRandonFileName(fileName);// 通过工具类产生新图片名称，防止重名

        //指明文件上传位置
        File destOld = new File(imgUrl+tempName);
        File dest = new File(imgUrl+picNewName);
        try{
            //判断文件父目录是否存在
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdir();
            }
            //写入文件
            file.transferTo(destOld);
            //读取文件
            BufferedImage img = ImageIO.read(destOld);
            //按固定比例0.15压缩图片
            BufferedImage bi = new ScaledThumbnailMaker().scale(0.5).imageType(BufferedImage.TYPE_3BYTE_BGR).make(img);
            //写压缩后图片            
            ImageIO.write(bi,"jpg",dest);
            destOld.delete();
            return picNewName;
        }catch (Exception e){
            e.printStackTrace();
            return "E";
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

    @Override
    public ResultBody deleteBuildBookPhotos(Map map) {
        if (map==null || map.get("id")==null || map.get("type")==null){
            return ResultBody.error(-21_0006,"必传参数未传！");
        }
        //type(1:楼盘轮播图，视频  2：户型图 3: 楼栋产品 4：户型轮播图/视频 5：户型标签 楼盘标签 6：户型标签分析 7：户型详情图 )
        if ("1".equals(map.get("type")+"")){
            String[] ids = map.get("id").toString().split(",");
            String param = "'"+StringUtils.join(ids,"','")+"'";
            //删除轮播图，视频
            contentDao.delBuildingPhAndVi(param);
        }else if ("2".equals(map.get("type")+"")){
            //查询户型围观人数或报备人数
            String hxId = contentDao.isOkDelHX(map.get("id")+"");
            if (hxId==null){
                return ResultBody.error(-21_0006,"此户型已有相关数据不可删除！");
            }
            //获取户型信息
            Map HXDetail = contentDao.getHXDetail(map.get("id")+"");
            if (HXDetail!=null){
                if (HXDetail.get("HouseImg")!=null && !"".equals(HXDetail.get("HouseImg")+"")){
                    String[] names = HXDetail.get("HouseImg").toString().split("/");
                    String oldName = names[names.length-1];
                    this.delPhotos(oldName);
                }
                if (HXDetail.get("VRImgUrl")!=null && !"".equals(HXDetail.get("VRImgUrl")+"")){
                    String[] names = HXDetail.get("VRImgUrl").toString().split("/");
                    String oldName = names[names.length-1];
                    this.delPhotos(oldName);
                }
                if (HXDetail.get("HouseDetailImgUrl")!=null && !"".equals(HXDetail.get("HouseDetailImgUrl")+"")){
                    String[] names = HXDetail.get("HouseDetailImgUrl").toString().split("/");
                    String oldName = names[names.length-1];
                    this.delPhotos(oldName);
                }
            }

            //删除户型图
            contentDao.delBuildingApartment("'"+map.get("id")+"'");
            //同时删除户型相关数据
//            contentDao.delHouseAnalysisByHouseId(map.get("id")+"");
//            contentDao.delHouseImgByHouseId(map.get("id")+"");
//            contentDao.delHouseTagByHouseId(map.get("id")+"");
        }else if ("3".equals(map.get("type")+"")){
            //删除楼栋产品
//            contentDao.delBuildingProperty("'"+map.get("id")+"'");
            //获取户型信息
            List<Map> HXDetails = contentDao.getHXDetailByProduct(map.get("id")+"");
            if (HXDetails!=null && HXDetails.size()>0){
                for (Map HXDetail:HXDetails) {
                    if (HXDetail.get("HouseImg")!=null && !"".equals(HXDetail.get("HouseImg")+"")){
                        String[] names = HXDetail.get("HouseImg").toString().split("/");
                        String oldName = names[names.length-1];
                        this.delPhotos(oldName);
                    }
                    if (HXDetail.get("HouseDetailImgUrl")!=null && !"".equals(HXDetail.get("HouseDetailImgUrl")+"")){
                        String[] names = HXDetail.get("HouseDetailImgUrl").toString().split("/");
                        String oldName = names[names.length-1];
                        this.delPhotos(oldName);
                    }
                }
            }
            contentDao.delBuildBookProduct("'"+map.get("id")+"'");
        }else if ("4".equals(map.get("type")+"")){
            //删除户型轮播图/视频
            contentDao.delHouseImg("'"+map.get("id")+"'");
        }else if ("5".equals(map.get("type")+"") || "8".equals(map.get("type")+"")){
            //删除户型标签  || 删除楼盘标签
            contentDao.delHouseTag("'"+map.get("id")+"'");
        }else if ("6".equals(map.get("type")+"")){
            //删除户型标签分析
            contentDao.delHouseAnalysis("'"+map.get("id")+"'");
        }else if ("7".equals(map.get("type")+"")){
            //删除户型详情图
            contentDao.delHouseDetailPhoto(map.get("id")+"");
        }

        return ResultBody.success("删除成功！！");
    }
    //删除图片
    public void delPhotos(String oldName){
        try{
            if(!StringUtils.isBlank(oldName)){
                new File(imgUrl+oldName).delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public List<Map> getNewsOrder(Map map) {
        return contentDao.getNewsOrder(map);
    }

    @Override
    public ResultBody updateNewsOrder(Map map) {
        try{
            contentDao.updateNewsOrder(map);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-21_0007,"排序异常！");
        }
        return ResultBody.success("排序成功！！");
    }

    @Override
    public String getUserOrgLevel() {
        return contentDao.getOrgLevel(SecurityUtils.getUsername());
    }

    @Override
    public List<Map> getExtensionTypeDesc(Map map) {
        List<Map> result = new ArrayList<>();
        String orgLevel = map.get("orgLevel")+"";
        String level = map.get("ExtenType")+"";
        //判断权限层级（1：集团 2：区域 4：项目）
        if ("1".equals(orgLevel)){
            //判断查询的层级（2：区域 1：项目）
            if ("2".equals(level)){
                //获取所有区域
                result = contentDao.getAllQy();
            }else{
                //获取所有项目
                result = contentDao.getAllPro();
            }
        }else if ("2".equals(orgLevel)){
            //判断查询的层级（2：区域 1：项目）
            if ("2".equals(level)){
                //获取有权限区域
                result = contentDao.getQyByUserName(SecurityUtils.getUsername());
            }else{
                map.put("UserName",SecurityUtils.getUsername());
                List<String> fullpath = projectMapper.findFullPath(map);
                StringBuffer sb = new StringBuffer();
                if (fullpath==null || fullpath.size()==0){
                    throw new BadRequestException(-10_0000,"用户无项目权限！");
                }
                for (int i = 0; i < fullpath.size(); i++) {
                    if (i==0){
                        sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                    }else{
                        sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                    }
                }
                //获取有权限项目
                result = contentDao.getProByUserName(sb.toString());
            }
        }else{
            map.put("UserName",SecurityUtils.getUsername());
            List<String> fullpath = projectMapper.findFullPath(map);
            StringBuffer sb = new StringBuffer();
            if (fullpath==null || fullpath.size()==0){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            //获取有权限项目
            result = contentDao.getProByUserName(sb.toString());
        }
        return result;
    }

    @Override
    public List<Map> getOrgAndPro(Map map) {
        List<Map> result = new ArrayList<>();
        //获取权限层级(1:集团 2:区域 4:项目)
        String orgLevel = map.get("orgLevel")+"";
        if("1".equals(orgLevel)){
            //查询所有区域及项目
            List<Map> qys = contentDao.getAllQy();
            for (Map qyMap:qys) {
                Map resultMap = new HashMap();
                resultMap.put("label",qyMap.get("name"));
                resultMap.put("value",qyMap.get("id"));
                List<Map> children = new ArrayList<>();
                if (qyMap.get("FullPath")!=null && !"".equals(qyMap.get("FullPath")+"")){
                    List<Map> childrens = contentDao.getProByQyFullPath(qyMap.get("FullPath")+"");
                    if (childrens!=null && childrens.size()>0){
                        for (Map proMap:childrens) {
                            Map pMap = new HashMap();
                            pMap.put("label",proMap.get("name"));
                            pMap.put("value",proMap.get("id"));
                            children.add(pMap);
                        }
                    }
                }
                resultMap.put("children",children);
                result.add(resultMap);
            }
        }else if("2".equals(orgLevel)){
            //查询权限下区域及项目
            List<Map> qys = contentDao.getQyByUserName(SecurityUtils.getUsername());
            if (qys!=null && qys.size()>0){
                for (Map qyMap:qys) {
                    Map resultMap = new HashMap();
                    resultMap.put("label",qyMap.get("name"));
                    resultMap.put("value",qyMap.get("id"));
                    List<Map> children = new ArrayList<>();
                    if (qyMap.get("FullPath")!=null && !"".equals(qyMap.get("FullPath")+"")){
                        List<Map> childrens = contentDao.getProByQyFullPath(qyMap.get("FullPath")+"");
                        if (childrens!=null && childrens.size()>0){
                            for (Map proMap:childrens) {
                                Map pMap = new HashMap();
                                pMap.put("label",proMap.get("name"));
                                pMap.put("value",proMap.get("id"));
                                children.add(pMap);
                            }
                        }
                    }
                    resultMap.put("children",children);
                    result.add(resultMap);
                }
            }
        }else{
            map.put("UserName",SecurityUtils.getUsername());
            List<String> fullpath = projectMapper.findFullPath(map);
            StringBuffer sb = new StringBuffer();
            if (fullpath==null || fullpath.size()==0){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
            for (int i = 0; i < fullpath.size(); i++) {
                if (i==0){
                    sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }else{
                    sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                }
            }
            map.put("where", sb.toString());
            List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map);
            if(projectList.size() > 0){
                List<String> strings = projectList.stream().map(ProjectVO::getAreaId).filter(x -> x != null).distinct().collect(Collectors.toList());
                for (int i = 0; i < strings.size(); i++) {
                    String areaId = strings.get(i);
                    Map resultMap = new HashMap();
                    resultMap.put("value",areaId);
                    List<ProjectVO> projectVOList1 = projectList.stream().filter(pro -> areaId.equals(pro.getAreaId())).collect(Collectors.toList());
                    List<Map> children = new ArrayList<>();
                    if(projectVOList1.size() > 0){
                        for (int j = 0; j < projectVOList1.size(); j++) {
                            resultMap.put("label",projectVOList1.get(0).getAreaName());
                            Map pMap = new HashMap();
                            pMap.put("label",projectVOList1.get(j).getProjectName());
                            pMap.put("value",projectVOList1.get(j).getProjectId());
                            children.add(pMap);
                        }
                    }
                    resultMap.put("children",children);
                    result.add(resultMap);
                }
            }
        }
        return result;
    }

    @Override
    public PageInfo getExtenListNew(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }

        ParamDesc paramDesc = JSONObject.toJavaObject(JSON.parseObject(JSONObject.toJSONString(map)),ParamDesc.class);
        //如果项目ID未传判断人员级别(1:集团 2:区域 4:项目)
        if (paramDesc.getProIds()==null || paramDesc.getProIds().size()==0){
            if ("2".equals(paramDesc.getOrgLevel())){
                //查询权限下区域及项目
                List<Map> qys = contentDao.getQyByUserName(SecurityUtils.getUsername());
                if (qys!=null && qys.size()>0){
                    List<String> orgIds = new ArrayList<>();
                    List<String> proIds = new ArrayList<>();
                    for (Map qyMap:qys) {
                        orgIds.add(qyMap.get("id")+"");
                        if (qyMap.get("FullPath")!=null && !"".equals(qyMap.get("FullPath")+"")){
                            List<Map> childrens = contentDao.getProByQyFullPath(qyMap.get("FullPath")+"");
                            if (childrens!=null && childrens.size()>0){
                                for (Map proMap:childrens) {
                                    proIds.add(proMap.get("id")+"");
                                }
                            }
                        }
                    }
                    if (proIds.size()==0){
                        throw new BadRequestException(-10_0000,"用户无项目权限！");
                    }
                    map.put("proIds",proIds);
                    map.put("orgIds",orgIds);
                }else{
                    throw new BadRequestException(-10_0000,"用户无区域权限！");
                }
            }else if ("4".equals(paramDesc.getOrgLevel())){
                map.put("UserName",SecurityUtils.getUsername());
                List<String> fullpath = projectMapper.findFullPath(map);
                StringBuffer sb = new StringBuffer();
                if (fullpath==null || fullpath.size()==0){
                    throw new BadRequestException(-10_0000,"用户无项目权限！");
                }
                for (int i = 0; i < fullpath.size(); i++) {
                    if (i==0){
                        sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                    }else{
                        sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                    }
                }
                map.put("where", sb.toString());
                List<ProjectVO> projectList = projectMapper.getProjectListByUserName(map);
                if (projectList!=null && projectList.size()>0){
                    List<String> proIds = new ArrayList<>();
                    for (ProjectVO p:projectList) {
                        proIds.add(p.getProjectId());
                    }
                    map.put("proIds",proIds);
                }else{
                    throw new BadRequestException(-10_0000,"用户无项目权限！");
                }
            }
        }
        PageHelper.startPage(pageIndex, pageSize);
        List<Map> list = contentDao.getExtenListNew(map);
        return new PageInfo(list);
    }

    @Override
    public String delExten(Map map) {
        String ID = map.get("ID")+"";
        //查询推广码是否有注册人数
        int count = contentDao.getRegisterCnt(ID);
        if (count>0){
            return "-1";
        }else{
            //更新为删除状态
            map.put("Editor",SecurityUtils.getUserId());
            contentDao.updateExtenDel(map);
        }
        return "1";
    }

    @Override
    public List<Map> getCityByOrgId(Map map) {
        //获取权限层级(1:集团 2:区域 4:项目)
        String orgLevel = map.get("orgLevel")+"";
        List<Map> citys = new ArrayList<>();
        if ("1".equals(orgLevel)){
            //获取所有城市
            citys = contentDao.getAllCitys();
        }else{
            //获取权限下城市
            citys = contentDao.getCityByOrgId(SecurityUtils.getUsername());
        }
        return citys;
    }

    @Override
    public ResultBody addOrEditExten(Map map) {
        try{
            String json = JSONObject.toJSONString(map);
            String dateM =  this.getDateM();
            //转化为实体类
            Extension extension = JSONObject.toJavaObject(JSON.parseObject(json),Extension.class);
            extension.setCreator(SecurityUtils.getUserId());
            if (extension.getPhotoWidth()==null || "".equals(extension.getPhotoWidth())){
                extension.setPhotoWidth("430");
            }
            //判断是否编辑(未修改码尺寸)
            if ("2".equals(extension.getAddOrEdit())){
                String ID = extension.getID();
                if (ID==null || "".equals(ID)){
                    return ResultBody.error(-21_0006,"必传参数未传！");
                }
                String extenActivityName = extension.getExtenActivityName();
                String IconName = "策划推广-"+extenActivityName+".png";
                extension.setIconName(IconName);
                contentDao.updateExtension(extension);
                return ResultBody.success("推广码编辑成功！");
            }else {
                String ID = extension.getID();
                String IconName = "";
                //判断码级别(1：项目级 2：区域级 3：集团级)
                if ("1".equals(extension.getExtenType())){
                    //获取项目名称
                    String projectName = contentDao.getProNameByID(extension.getProjectID());
                    if (extension.getMainMediaName()!=null && extension.getSubMediaName()!=null){
                        IconName = projectName+"-"+extension.getJumpToName()+"-"+extension.getExtenActivityName()+"-"+extension.getMainMediaName()+"-"+extension.getSubMediaName()+".png";
                    }else{
                        IconName = projectName+"-"+extension.getJumpToName()+"-"+extension.getExtenActivityName()+".png";
                    }
                }else if ("2".equals(extension.getExtenType())){
                    //获取区域名称
                    String orgName = contentDao.getOrgNameByOrgId(extension.getExtenOrgId());
                    IconName = orgName+"-"+extension.getJumpToName()+"-"+extension.getExtenActivityName()+".png";
                }else if ("3".equals(extension.getExtenType())){
                    IconName = "旭辉集团-"+extension.getJumpToName()+"-"+extension.getExtenActivityName()+".png";
                }
                if (ID==null || "".equals(ID)){
                    extension.setIsNewAdd("1");
                    //新增推广码
                    extension.setIconName(IconName);
                    String picNewName = UploadUtils.generateRandonFileName(IconName);// 通过工具类产生新图片名称，防止重名
                    extension.setIconUrl(imgEndUrl + dateM + "/" + picNewName);
                    extension.setPhotoName(picNewName);
                    extension.setDays("0");
                    contentDao.addExtension(extension);
                    String newId = extension.getID();
                    //生成小程序二维码
                    String param = "C_"+newId;
                    getminiqrQr(param,picNewName,extension.getToUrl(),extension.getPhotoWidth(),dateM);
                    return ResultBody.success("推广码新增成功！");
                }else{
                    extension.setIsNewAdd("1");
                    //判断是否编辑(修改码尺寸重新生成)
                    //删除原图片
                    if (extension.getPhotoName()!=null && !"".equals(extension.getPhotoName())){
                        File file = new File(imgUrl+extension.getPhotoName());
                        if (file!=null){
                            file.delete();
                        }
                    }else{
                        if (extension.getIconUrl()!=null && !"".equals(extension.getIconUrl())){
                            String[] names = extension.getIconUrl().split("/");
                            String name = names[names.length-1];
                            File file = new File(imgUrl+name);
                            if (file!=null){
                                file.delete();
                            }
                        }

                    }
                    //重新生成
                    extension.setIconName(IconName);
                    String picNewName = UploadUtils.generateRandonFileName(IconName);// 通过工具类产生新图片名称，防止重名
                    extension.setIconUrl(imgEndUrl+ dateM + "/"  + picNewName);
                    extension.setPhotoName(picNewName);
                    contentDao.updateExtension(extension);
                    //生成小程序二维码
                    String param = "C_"+extension.getID();
                    getminiqrQr(param,picNewName,extension.getToUrl(),extension.getPhotoWidth(),dateM);
                    return ResultBody.success("推广码修改成功！");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            if ("1".equals(map.get("addOrEdit"))){
                return ResultBody.error(-21_0007,"推广码新增异常！");
            }else{
                return ResultBody.error(-21_0007,"推广码编辑异常！");
            }
        }
    }

    @Override
    public ResultBody getBuildingExtenTO(Map map) {
        if (map.get("ToUrl")==null || "".equals(map.get("ToUrl"))){
            return ResultBody.error(-21000,"必传参数未传！");
        }
        List<Map> list = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        //查询有权限的项目
        if ("2".equals(map.get("ToUrl")+"") || "4".equals(map.get("ToUrl")+"")){
            //判断查询权限是否集团（1：集团 2区域 4项目）
            if (map.get("orgLevel")!=null && "1".equals(map.get("orgLevel")+"")){

            }else{
                Map mapss=new HashMap();
                mapss.put("UserName", SecurityUtils.getUsername());
                Map userInfoMap = authMapper.mGetUserInfo(mapss);
                List<String> fullpath = projectMapper.findFullPath(mapss);
                StringBuffer sbs = new StringBuffer();
                for (int i = 0; i < fullpath.size(); i++) {
                    if (i==0){
                        sbs.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                    }else{
                        sbs.append("or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                    }
                }
                //获取有权限的项目
                List<Map> mapList = projectMapper.findProjectListByUserName( SecurityUtils.getUsername(),"",userInfoMap.get("JobCode").toString(),sbs.toString());
                if (mapList!=null && mapList.size()>0){
                    for (Map proMap:mapList) {
                        ids.add(proMap.get("projectId")+"");
                    }
                }else{
                    return ResultBody.success(list);
                }
            }

        } else if ("3".equals(map.get("ToUrl")+"")){
            //跳转户型
            if (map.get("BuildBookId")==null || "".equals(map.get("BuildBookId"))){
                return ResultBody.error(-21000,"必传参数未传！");
            }
            String buildBookId = map.get("BuildBookId")+"";
            //根据楼盘获取户型
            list = contentDao.getBuildHouse(buildBookId);
        } else if ("5".equals(map.get("ToUrl")+"")){
            //判断查询权限是否集团（1：集团 2区域 4项目）
            if (map.get("orgLevel")!=null && "1".equals(map.get("orgLevel")+"")){

            }else{
                //查询有权限的城市
                //修改为按照账号查询所有岗位城市
                String userId = SecurityUtils.getUserId();
                //查询所有岗位
                List<Map> jobMap = contentDao.getAllJobs(userId);
                if (jobMap!=null && jobMap.size()>0){
                    //查询岗位是否是系统管理员
                    boolean isAdmin = false;
                    StringBuffer sb = new StringBuffer();
                    for (Map map1:jobMap) {
                        if ("10001".equals(map1.get("JobCode")) || "系统管理员".equals(map1.get("JobCode"))){
                            isAdmin = true;
                            break;
                        }
                        sb.append("'"+map1.get("id")+"',");
                    }
                    if (isAdmin){

                    }else{
                        String JobIDs = sb.toString().substring(0,sb.toString().length()-1);
                        List<Map> citys = contentDao.getCityByJobId(JobIDs);
                        if (citys!=null && citys.size()>0){
                            for (Map city:citys) {
                                ids.add(city.get("CityID")+"");
                            }
                        }else{
                            return ResultBody.success(list);
                        }
                    }
                }else{
                    return ResultBody.success(list);
                }
            }
        }
        //判断跳转类型 1：首页 2：项目微楼书 3：户型 4：活动 5: 新闻
        if ("2".equals(map.get("ToUrl")+"")){
            list = contentDao.getBuildingByProIds(ids);
        }else if ("5".equals(map.get("ToUrl")+"")){
            list = contentDao.getNewsByCityIds(ids);
        }else if ("4".equals(map.get("ToUrl")+"")){
            list = contentDao.getActivityByProIds(ids);
        }
        return ResultBody.success(list);
    }

    @Override
    public ResultBody getActivityPros(Map map) {
        return ResultBody.success(contentDao.getActivityPros(map.get("activityId")+""));
    }

    @Override
    public ResultBody getProOrg(Map map) {
        List<Map> result = new ArrayList<>();
        List<String> orgIdList = new ArrayList<>();
        List<String> orgIds = contentDao.getProOrgIds(map);
        if (orgIds!=null && orgIds.size()>0){
            for (String id:orgIds) {
                Map orgMap = new HashMap();
                Map orgMap1 = this.getParentOrg(id);
                if (orgMap1!=null && orgMap1.get("ID")!=null){
                    orgMap.put("id",orgMap1.get("ID"));
                    orgMap.put("name",orgMap1.get("OrgName"));
                    if (!orgIdList.contains(orgMap1.get("ID")+"")){
                        result.add(orgMap);
                        orgIdList.add(orgMap1.get("ID")+"");
                    }
                }

            }
        }
        return ResultBody.success(result);
    }

    @Override
    public void extenExport(HttpServletRequest request, HttpServletResponse response, String excelForm) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParamDesc paramDesc = JSONObject.parseObject(excelForm,ParamDesc.class);
        ArrayList<Object[]> dataset = new ArrayList<>();
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("5");
        excelExportLog.setMainTypeDesc("内容管理");
        excelExportLog.setSubType("N3");
        excelExportLog.setSubTypeDesc("推广码列表");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        try {
            Map paramMap = new HashMap();
            paramMap.put("orgLevel",paramDesc.getOrgLevel());
            paramMap.put("extenActivityName",paramDesc.getExtenActivityName());
            paramMap.put("jumpToName",paramDesc.getJumpToName());
            paramMap.put("creator",paramDesc.getCreator());
            paramMap.put("toUrls",paramDesc.getToUrls());
            paramMap.put("proIds",paramDesc.getProIds());
            paramMap.put("orgIds",paramDesc.getOrgIds());
            List<String> proIdList = new ArrayList<>();
            //如果项目ID未传判断人员级别(1:集团 2:区域 4:项目)
            if (paramDesc.getProIds()==null || paramDesc.getProIds().size()==0){
                if ("2".equals(paramDesc.getOrgLevel())){
                    //查询权限下区域及项目
                    List<Map> qys = contentDao.getQyByUserName(SecurityUtils.getUsername());
                    if (qys!=null && qys.size()>0){
                        List<String> orgIds = new ArrayList<>();
                        List<String> proIds = new ArrayList<>();
                        for (Map qyMap:qys) {
                            orgIds.add(qyMap.get("id")+"");
                            if (qyMap.get("FullPath")!=null && !"".equals(qyMap.get("FullPath")+"")){
                                List<Map> childrens = contentDao.getProByQyFullPath(qyMap.get("FullPath")+"");
                                if (childrens!=null && childrens.size()>0){
                                    for (Map proMap:childrens) {
                                        proIds.add(proMap.get("id")+"");
                                        proIdList.add(proMap.get("id")+"");
                                    }
                                }
                            }
                        }
                        if (proIds.size()==0){
                            throw new BadRequestException(-10_0000,"用户无项目权限！");
                        }
                        paramMap.put("proIds",proIds);
                        paramMap.put("orgIds",orgIds);
                    }else{
                        throw new BadRequestException(-10_0000,"用户无区域权限！");
                    }
                }else if ("4".equals(paramDesc.getOrgLevel())){
                    paramMap.put("UserName",paramDesc.getUserName());
                    List<String> fullpath = projectMapper.findFullPath(paramMap);
                    StringBuffer sb = new StringBuffer();
                    if (fullpath==null || fullpath.size()==0){
                        throw new BadRequestException(-10_0000,"用户无项目权限！");
                    }
                    for (int i = 0; i < fullpath.size(); i++) {
                        if (i==0){
                            sb.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
                        }else{
                            sb.append(" or org.FullPath LIKE '"+fullpath.get(i)+"%'");
                        }
                    }
                    paramMap.put("where", sb.toString());
                    List<ProjectVO> projectList = projectMapper.getProjectListByUserName(paramMap);
                    if (projectList!=null && projectList.size()>0){
                        List<String> proIds = new ArrayList<>();
                        for (ProjectVO p:projectList) {
                            proIds.add(p.getProjectId());
                            proIdList.add(p.getProjectId());
                        }
                        paramMap.put("proIds",proIds);
                    }else{
                        throw new BadRequestException(-10_0000,"用户无项目权限！");
                    }
                }
            }
            excelExportLog.setCreator(paramDesc.getUserId());
            if (proIdList.size()>0){
                //获取项目集合数据（事业部，项目Id,项目名称）
                Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
                excelExportLog.setAreaName(proMap.get("areaName")+"");
                excelExportLog.setProjectId(proMap.get("projectId")+"");
                excelExportLog.setProjectName(proMap.get("projectName")+"");
            }else{
                excelExportLog.setAreaName("/");
                excelExportLog.setProjectId("/");
                excelExportLog.setProjectName("/");
            }

            List<Extension> list = contentDao.getExtenListNewExprot(paramMap);
            if (list.size()>0){
                String[] headers = list.get(0).getCourtCaseTitle();
                for (int i = 0; i < list.size(); i++) {
                    Extension extension = list.get(i);
                    extension.setNum((i + 1) + "");
                    Object[] oArray = extension.toPublicData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("推广码明细表", headers,dataset, "推广码明细表", response,null);
            }
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(sf.format(new Date()));
            excelExportLog.setIsDown("1");
            Long export = new Date().getTime();
            Long exporttime = export-nowtime;
            String exportTime =sf.format(Double.valueOf(exporttime+"")/1000);
            excelExportLog.setWaitTime("0");
            excelExportLog.setExportTime(exportTime);
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
        } catch (Exception e) {
            e.printStackTrace();
            excelExportLog.setExportStatus("3");
            excelExportLog.setExceptionMessage(e.getMessage());
            //保存任务表
            excelImportMapper.addExcelExportLog(excelExportLog);
        }

    }

    @Override
    public ResultBody getLpPhotoType() {
        return ResultBody.success(contentDao.getLpPhotoType());

    }

    @Override
    public ResultBody checkDep(CheckDep req) {
        if (StringUtils.isBlank(req.getCityID())) {
            //没有城市id
            return ResultBody.success(1);
        }
        if (StringUtils.isBlank(req.getShowBeginTime())) {
            //没有开始时间
            return ResultBody.success(2);
        }
//        if (StringUtils.isBlank(req.getImgUrl())) {
//            //没有图片链接
//            return ResultBody.success(3);
//        }
        if (StringUtils.isBlank(req.getJumpType())) {
            //没有首页轮播跳转类型
            return ResultBody.success(4);
        } else {
            if (!"3".equals(req.getJumpType()) && StringUtils.isBlank(req.getImgJumpUrl())) {
                //没有首页轮播跳转链接
                return ResultBody.success(5);
            }
        }
        if (StringUtils.isBlank(req.getStatus())) {
            //是否启用 6
            return ResultBody.success(6);
        }
        // 获取新增活动起始时间
        String beginTime = req.getShowBeginTime();
        String endTime = req.getShowEndTime();

        // 可以存在多个禁用状态的广告
        if (req.getStatus().equals("0")) {
            return ResultBody.success(0);
        }
        // 查询是否存在已启用城市的id集合
        Integer count = contentDao.getCondictionCount(req.getCityID(),beginTime,endTime,req.getId());
        if (count > 0) {
            //当前没有启用的城市
            return ResultBody.success(7);
        } else {
            return ResultBody.success(0);
        }
    }

    public Map getParentOrg(String id){
        Map result = contentDao.getParentOrg(id);
        if (result==null || result.get("PID") ==null || result.get("ID") ==null || "00000001".equals(result.get("PID")+"")){
            return result;
        }else{
            result = this.getParentOrg(result.get("ID")+"");
        }
        return result;
    }

    public String getDateM(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(new Date());
    }

    @Override
    public ResultBody startupPictureDeploy(StartupPagePicture startupPagePicture) throws IOException {
        if (startupPagePicture == null  || StringUtils.isBlank(startupPagePicture.getImgUrl())
                || StringUtils.isBlank(startupPagePicture.getUserName())
                || startupPagePicture.getStatus() == null
                || startupPagePicture.getPort() == null) {
            return ResultBody.error(11_1037,"参数异常！");
        }
        if (startupPagePicture.getStatus() == 1) {
         List<String> id = contentDao.startupPictureListByStatus(startupPagePicture);
            if (id != null && id.size() > 0) {
                for (String s : id) {
                    Map map = new HashMap();
                    map.put("status",0);
                    map.put("userName",startupPagePicture.getUserName());
                    map.put("id",s);
                    contentDao.statusStartupPicture(map);
                }
            }
        }
        String newId = UUID.randomUUID().toString();
        startupPagePicture.setId(newId);
        Integer row = contentDao.startupPictureDeploy(startupPagePicture);
        return ResultBody.success(row);
    }

    @Override
    public ResultBody statusStartupPicture(StartupPagePicture startupPagePicture) {
        if (startupPagePicture.getId() == null || "".equals(startupPagePicture.getId())) {
            return ResultBody.error(11_1037,"参数异常！");
        }else if(startupPagePicture.getUserName()== null || "".equals(startupPagePicture.getUserName())){
            return ResultBody.error(11_1037,"参数异常！");
        }else if(startupPagePicture.getStatus() == null ){
            return ResultBody.error(11_1037,"参数异常！");
        }
        BuildingPhoto buildingPhoto = contentDao.startupPictureById(startupPagePicture.getId());
        if (buildingPhoto == null || buildingPhoto.getID() == null || "".equals(buildingPhoto.getID())){
            return ResultBody.error(11_1037,"参数异常！");
        }
        startupPagePicture.setPort(buildingPhoto.getPort());
        Map map = new HashMap();
        map.put("id",startupPagePicture.getId());
        map.put("status",startupPagePicture.getStatus());
        map.put("userNmae",startupPagePicture.getUserName());

        if (startupPagePicture.getStatus() == 1) {
            Boolean falg = false;
            List<String> id = contentDao.startupPictureListByStatus(startupPagePicture);
            if (id != null && id.size() > 0) {
                for (String s : id) {
                    if(s == startupPagePicture.getId() || startupPagePicture.getId().equals(s)){
                        falg = true;
                    }else{
                        Map map1 = new HashMap();
                        map1.put("status",0);
                        map1.put("userName",startupPagePicture.getUserName());
                        map1.put("id",s);
                        contentDao.statusStartupPicture(map1);
                    }
                }
            }
            if(falg){
                return ResultBody.success(1);
            }
        }
        Integer  row =  contentDao.statusStartupPicture(map);
        return ResultBody.success(row);
    }

    @Override
    public ResultBody delStartupPicture(StartupPagePicture startupPagePicture) {
        if (startupPagePicture.getId() == null || "".equals(startupPagePicture.getId())) {
            return ResultBody.error(11_1037,"参数异常！");
        }else if(startupPagePicture.getUserName()== null || "".equals(startupPagePicture.getUserName())){
            return ResultBody.error(11_1037,"参数异常！");
        }
        Integer  row =  contentDao.delStartupPicture(startupPagePicture);
        return ResultBody.success(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateStartupPicture(StartupPagePicture startupPagePicture) {
        try {
            String id = startupPagePicture.getId();
            if (id == null || "".equals(id)) {
                return ResultBody.error(11_1037,"参数异常！");
            }else if(startupPagePicture.getUserName()== null || "".equals(startupPagePicture.getUserName())){
                return ResultBody.error(11_1037,"参数异常！");
            }
            Integer row = contentDao.updateStartupPicture(startupPagePicture);
            if (1 == startupPagePicture.getStatus()) {
                contentDao.updateStartupPictureToDisabled(id,startupPagePicture.getPort() + "");
            }
            return ResultBody.success(row);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return ResultBody.error(20009,"启动页更新异常");
        }
    }
    @Override
    public ResultBody startupPictureById(String id) {
        if (id == null || "".equals(id)) {
            return ResultBody.error(11_1037,"参数异常！");
        }
        BuildingPhoto buildingPhoto = contentDao.startupPictureById(id);
        return ResultBody.success(buildingPhoto);
    }


    @Override
    public List<ResultProjectVO> getNewsBuilding(Map map) {
        List <BuildingBook> projectList =contentDao.getNewsBuilding(map);
        if(!projectList.isEmpty()){
            List<String> strings = projectList.stream().map(BuildingBook::getProjectID).filter(x -> x != null).distinct().collect(Collectors.toList());
            List<ResultProjectVO> resultProjectVOList = new ArrayList<>();
            for (int i = 0; i < strings.size(); i++) {
                String areaId = strings.get(i);
                ResultProjectVO resultProjectVO = new ResultProjectVO();
                resultProjectVO.setValue(areaId);
                List<BuildingBook> projectVOList1 = projectList.stream().filter(pro -> areaId.equals(pro.getProjectID())).collect(Collectors.toList());
                if(projectVOList1.size() > 0){
                    List<ResultProjectVO> resultProjectVOList2 = new ArrayList<>();
                    for (int j = 0; j < projectVOList1.size(); j++) {
                        resultProjectVO.setLabel(projectVOList1.get(0).getProjectName());
                        ResultProjectVO resultProjectVO2 = new ResultProjectVO();
                        resultProjectVO2.setValue(projectVOList1.get(j).getID());
                        resultProjectVO2.setLabel(projectVOList1.get(j).getProjectShowName());
                        resultProjectVOList2.add(resultProjectVO2);
                    }
                    resultProjectVO.setChildren(resultProjectVOList2);
                }
                resultProjectVOList.add(resultProjectVO);
            }
            return resultProjectVOList;
        }
        return null;

    }

    @Override
    public List<Dictionary> getNewsType() {

        return contentDao.getNewsType();
    }

    @Override
    public void newsExport(HttpServletRequest request, HttpServletResponse response,Map map) {
        String json = JSONObject.toJSONString(map);
        //转化为实体类
        ExcelForm excelForm = JSONObject.toJavaObject(JSON.parseObject(json),ExcelForm.class);
//        Map paramMap = new HashMap();
        if (excelForm.getBeginTime()!=null && excelForm.getEndTime()!=null && !"".equals(excelForm.getBeginTime()) && !"".equals(excelForm.getEndTime())){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                map.put("beginTime",sf.format(sf.parse(map.get("beginTime")+"")));
                map.put("endTime",sf.format(sf.parse(String.valueOf(map.get("endTime")).replace("00:00:00","23:59:59"))));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if (map.get("CityIDs")!=null && !"".equals(map.get("CityIDs"))){
            String[] ids = map.get("CityIDs").toString().split(",");
            if (ids!=null && ids.length>0){
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < ids.length; i++) {
                    if (i==ids.length-1){
                        sb.append("'"+ids[i]+"','00000000-0000-0000-0000-000000000000'");
                    }else{
                        sb.append("'"+ids[i]+"',");
                    }
                }
                map.put("CityIDs",sb.toString());
            }
        }else{
            if (map.get("JobID")!=null && !"".equals(map.get("JobID"))){
                Map param = new HashMap<>();
                param.put("userId",map.get("UserID"));
                List<Map> ids = this.getCitysByJobId(param);

                if (ids!=null && ids.size()>0){
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < ids.size(); i++) {
                        if (i==ids.size()-1){
                            sb.append("'"+ids.get(i).get("CityID")+"','00000000-0000-0000-0000-000000000000'");
                        }else{
                            sb.append("'"+ids.get(i).get("CityID")+"',");
                        }
                    }
                    map.put("ids",sb.toString());
                }else{
                    map.put("ids","00000000-0000-0000-0000-000000000000");
                }
            }
        }

        if(map.containsKey(BUILDING_ID)){

            Object lo = map.get(BUILDING_ID);
            map.put(BUILDING_ID,null);
            StringBuffer sb = new StringBuffer();
            if(lo != null){
                List<Object>  buildList = (List) lo;
                for(Object o : buildList){
                    if(o != null){
                        List<String>  buildingBookList = (List) o;
                        if(buildingBookList.size()>1){
                            sb.append("'"+buildingBookList.get(1)+"',");

                        }
                    }
                }
            }
            if(sb.length()>0){
                sb.append("''");
                map.put("BuildingID",sb.toString());
            }
        }

        if(map.containsKey(NEWS_TYPE)){
            Object lo = map.get(NEWS_TYPE);
            map.put(NEWS_TYPE,null);
            StringBuffer sb = new StringBuffer();
            if(lo != null){
                List<String>  newsTypeList = (List) lo;
                for(String newsType : newsTypeList){
                    sb.append("'"+newsType+"',");
                }
            }
            if(sb.length()>0){
                sb.append("''");
                map.put(NEWS_TYPE,sb.toString());
            }
        }

        ArrayList<Object[]> dataset = new ArrayList<>();
        List<News> list = contentDao.getNewsList(map);
        try{
            //导出的文档下面的名字
            String excelName = "新闻明细";
            if (list!=null && list.size()>0){
                //循环遍历所有数据
                for (int z = 0; z < list.size(); z++) {
                    News map1 = list.get(z);

                    Object[] oArray = map1.toNewsData();
                    dataset.add(oArray);

                }
                String[] headers = list.get(0).toNewsTitle;
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(excelName,headers,dataset,excelName,response,null);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public List<BuildingProblem> getBuildingProblemList(Map map) {
        return contentDao.getBuildingProblemList(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBuildingProblem(BuildingProblem buildingProblem) {
        contentDao.addBuildingProblem(buildingProblem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBuildingProblem(BuildingProblem buildingProblem) {
        contentDao.updateBuildingProblem(buildingProblem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBuildingProblem(String id) {
        contentDao.delBuildingProblem(id);
    }

    @Autowired
    private ActivityInfoDao activityInfoDao;

    @Override
    public ResultBody addActivityHotHomePage(Map map) {
        if (map == null) {
            return ResultBody.error(109002,"参数存在错误，请重试！");
        }
        try {
            if (null == map.get("hotCityId")
                    || "".equals ((map.get("hotCityId") + ""))
                    || "null".equals ((map.get("hotCityId") + "")) ) {
                return ResultBody.error(109001,"请先选择展示的城市!");
            }
            List<String> hotCityIds = (List<String>) map.get("hotCityId");
            String activityId = map.get("activityId") + "";
            if (!hotCityIds.contains("00000000-0000-0000-0000-000000000000")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(activityId)) {
                    return ResultBody.error(109000,"请先选择绑定的活动！");
                }
            }
            // 新增校验 当前关联城市必须在活动关联的城市内
            Map hashMap = new HashMap();
            hashMap.put("activityId",activityId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String hotStartTime = map.get("hotStartTime") + "";
            String hotEndTime = map.get("hotEndTime") + "";
            if (org.apache.commons.lang3.StringUtils.isBlank(hotEndTime) ||
                    org.apache.commons.lang3.StringUtils.isBlank(hotStartTime)) {
                return ResultBody.error(12003,"展示起止时间不能为空！");
            }
            String status = map.get("status") + "";
            // 做添加验证
            if ("0".equals(status)) {
                // 每个城市做校验
                for (String hotCityId : hotCityIds) {
                    Integer count = activityInfoDao.checkHotHomePage(hotCityId, hotStartTime, hotEndTime, "", "");
                    if (count > 0) {
                        String cityName = activityInfoDao.getCityNameById(hotCityId);
                        return ResultBody.error(108888,"当前" + cityName + "已有活动热推,请重新选择时间或更改城市!");                    }
                }
            }
            map.put("createTime",dateFormat.format(new Date()));
            // 添加首页热推
            map.put("hotStartTime",dateFormat.parse(hotStartTime));
            map.put("hotEndTime",dateFormat.parse(hotEndTime));
            map.put("hotCityId",ListToString(hotCityIds));
            contentDao.addHotHomePage(map);
            String isHomePageHot = "0";
            // 根据绑定活动更新活动热推状态
            contentDao.updateActivityHotStatus(activityId,isHomePageHot);
            return ResultBody.success("保存首页热推成功");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(109002,"保存首页热推失败,请重试!");
        }
    }

    // 集合转字符串方法
    public static String ListToString(List list) {
        if (!CollectionUtils.isEmpty(list)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i)).append(',');
            }
            return list.isEmpty() ? "" : sb.toString().substring(0, sb.toString().length() - 1);
        }
        return null;
    }

    @Override
    public ResultBody updateActivityHotHomePage(Map map) {
        if (map == null) {
            return ResultBody.error(109002,"参数存在错误，请重试！");
        }
        try {
            if (null == map.get("hotCityId")
                    || "".equals ((map.get("hotCityId") + "")) ||
                    "null".equals ((map.get("hotCityId") + "")) ) {
                return ResultBody.error(109001,"请先选择展示的城市!");
            }
            List<String> hotCityIds = (List<String>) map.get("hotCityId");
            String activityId = map.get("activityId") + "";
            if (!hotCityIds.contains("00000000-0000-0000-0000-000000000000")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(activityId)) {
                    return ResultBody.error(109000,"请先选择绑定的活动！");
                }
            }
            String hotStartTime = map.get("hotStartTime") + "";
            String hotEndTime = map.get("hotEndTime") + "";
            if (org.apache.commons.lang3.StringUtils.isBlank(hotEndTime) ||
                    org.apache.commons.lang3.StringUtils.isBlank(hotStartTime)) {
                return ResultBody.error(12003,"展示起止时间不能为空！");
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String status = map.get("status") + "";
            // 启用状态时 做添加验证
            String id = map.get("id") + "";
            String isHomePageHot = "";
            String flag = "1";
            if ("0".equals(status)) {
                for (String hotCityId : hotCityIds) {
                    Integer count = activityInfoDao.checkHotHomePage(hotCityId, hotStartTime, hotEndTime,flag,id);
                    if (count > 0) {
                        String cityName = activityInfoDao.getCityNameById(hotCityId);
                        return ResultBody.error(108888,"当前" + cityName + "已有活动热推,请重新选择时间或更改城市!");                    }
                }
                isHomePageHot = "0";
                // 更新活动为热推活动
                contentDao.updateActivityHotStatus(activityId,isHomePageHot);
            } else {
                isHomePageHot = "1";
                // 更新活动为不热推
                contentDao.updateActivityHotStatus(activityId,isHomePageHot);
            }
            map.put("editTime",dateFormat.format(new Date()));
            map.put("hotStartTime",dateFormat.parse(hotStartTime));
            map.put("hotEndTime",dateFormat.parse(hotEndTime));
            map.put("hotCityId",ListToString(hotCityIds));
            contentDao.updateHotHomePage(map);
            return ResultBody.success("更新首页热推成功");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(109004,"更新首页热推失败,请重试!");
        }
    }

    @Override
    public ResultBody updateHotStatus(Map map) {
        if (map == null) {
            return ResultBody.error(109002,"参数存在错误，请重试！");
        }
        try {
            if (null == map.get("hotCityId")) {
                return ResultBody.error(109001,"请先选择展示的城市!");
            }
            List<String> hotCityIds = (List<String>) map.get("hotCityId");
            String activityId = map.get("activityId") + "";
            if (!hotCityIds.contains("00000000-0000-0000-0000-000000000000")) {
                if (org.apache.commons.lang3.StringUtils.isBlank(activityId)) {
                    return ResultBody.error(109000,"请先选择绑定的活动！");
                }
            }
            String hotStartTime = map.get("hotStartTime") + "";
            String hotEndTime = map.get("hotEndTime") + "";
            String status = map.get("status") + "";
            String isHomePageHot = "";
            // 更改状态做验证
            if ("0".equals(status)) {
                for (String hotCityId : hotCityIds) {
                    Integer count = activityInfoDao.checkHotHomePage(hotCityId, hotStartTime, hotEndTime, "", "");
                    if (count > 0) {
                        String cityName = activityInfoDao.getCityNameById(hotCityId);
                        return ResultBody.error(108888,"当前" + cityName + "已有活动热推,请重新选择时间或更改城市!");
                    }
                }
                isHomePageHot = "0";
                // 更新活动为热推活动
                contentDao.updateActivityHotStatus(activityId,isHomePageHot);
            } else {
                isHomePageHot = "1";
                // 更新活动为不热推
                contentDao.updateActivityHotStatus(activityId,isHomePageHot);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("editTime",dateFormat.format(new Date()));
            // 添加首页热推
            contentDao.updateHotHomePageStatus(map);
            return ResultBody.success("首页热推状态更新成功");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(109005,"首页热推状态更新失败");
        }
    }

    @Override
    public ResultBody delHotHomePageImg(String id) {
        try {
            // 查询当前活动下是有有绑定热推信息
            Integer count = contentDao.getHotCount(id);
                // 如果没有绑定热推信息 则将活动改变为非热推活动
            contentDao.updateActivityStatus(id);
            contentDao.delHotHomePageImg(id);
            return ResultBody.success("首页热播图删除成功");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultBody.error(109008,"首页热播图删除失败");
        }
    }

    @Override
    public ResultBody getHotImgList(Map map) {
        PageHelper.startPage((int) map.get("current"), (int) map.get("size"));
        List<Map> list = contentDao.getHotImgList(map);
        // 查询城市名称
        for (Map hotMap : list) {
            String cityId = hotMap.get("city_id") + "";
            String[] split = cityId.split(",");
            List<String> cityIds = Arrays.asList(split);
            String cityNames = contentDao.getConcatCityName(cityIds);
            hotMap.put("city_name",cityNames);
            hotMap.put("city_id",cityIds);
        }
        return ResultBody.success(new PageInfo(list));
    }

    @Override
    public ResultBody getBuildBookDic() {
        List<Map> list = contentDao.getBuildBookDic();
        return ResultBody.success(list);
    }

    @Override
    public ResultBody getHotHomePae(String id) {
        Map homePaeMap = contentDao.getHotHomePaeById(id);
        return ResultBody.success(homePaeMap);
    }

    @Override
    public ResultBody getActivityByCityId(List<String> cityId) {
        // 查询所有活动与城市的关联集合
        List<Map> actAllCityIds = contentDao.getActAllCityIds();
        List actIdList = new ArrayList();
        if (!CollectionUtils.isEmpty(actAllCityIds)) {
            for (Map map : actAllCityIds) {
                String cityIds = map.get("cityIds") + "";
                List<String> list = Arrays.asList(cityIds.split(","));
                if (list.containsAll(cityId)) {
                    actIdList.add(map.get("actId"));
                }
            }
        }
        List<Map> mapList = new ArrayList<>();
        // 查询所有活动
        if (!CollectionUtils.isEmpty(actIdList)) {
            mapList = contentDao.getActivityByIds(actIdList);
        }

        return ResultBody.success(mapList);
    }


    @Override
    public ResultBody getCityByPro(Map map) {
        StringBuffer sb = new StringBuffer();
        if (map.get("ids") instanceof ArrayList<?>) {
            for (Object o : (List<?>) map.get("ids")) {
                String id = String.class.cast(o);
                sb.append("'" + id + "',");
            }
        }
        String ids = sb.toString().substring(0, sb.toString().length() - 1);
        List<Map> citys = contentDao.getCityByPro(ids);
        return ResultBody.success(citys);
    }

    @Override
    public ResultBody checkHotConfig(Map map) {
        if (null != map.get("hotCityId")) {
            List<String> hotCityIds = (List<String>) map.get("hotCityId");
            String hotStartTime = map.get("hotStartTime") + "";
            String hotEndTime = map.get("hotEndTime") + "";
            if ( CollectionUtils.isEmpty(hotCityIds) ||
                    org.apache.commons.lang3.StringUtils.isBlank(hotStartTime) ||
                    org.apache.commons.lang3.StringUtils.isBlank(hotEndTime)) {
                return ResultBody.error(100089,"必传参数未传！");
            }
            for (String hotCityId : hotCityIds) {
                Integer integer = activityInfoDao.checkHotHomePage(hotCityId, hotStartTime, hotEndTime, "", "");
                while (integer > 0) {
                    String cityName = activityInfoDao.getCityNameById(hotCityId);
                    return ResultBody.error(108888,"当前"+cityName+"已有活动热推,请重新选择时间!");
                }
            }
        }
        return ResultBody.success("校验通过");
    }

    @Override
    public ResultBody getCityListByAciId(Map map) {
        List<Map> listByAciId =activityInfoDao.getCityListByAciId(map);
        return ResultBody.success(listByAciId);
    }

    @Override
    public List<Map> getProductsDict(String projectId) {
        List<Map> maps = contentDao.getProductsDict(projectId);
        return maps;
    }
}
