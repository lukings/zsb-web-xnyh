//package cn.visolink.system.phone.controller;
//
//import cn.visolink.config.MongoDBHelper;
//import cn.visolink.exception.BadRequestException;
//import cn.visolink.exception.ResultBody;
//import cn.visolink.system.excel.mapper.ExcelImportMapper;
//import cn.visolink.system.excel.model.ExcelExportLog;
//import cn.visolink.system.phone.mapper.PhoneMapper;
//import cn.visolink.system.phone.model.CustomerCallRecordVo;
//import cn.visolink.system.phone.model.CustomerCallRecordVoCount;
//import cn.visolink.system.phone.model.PhoneVo;
//import cn.visolink.utils.excel.ExcelExportUtil;
//import com.alibaba.fastjson.JSONObject;
//import java.net.URLEncoder;
//
//import com.mongodb.BasicDBObject;
//import io.swagger.annotations.ApiOperation;
//import org.bson.conversions.Bson;
//import org.bson.types.ObjectId;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.aggregation.Aggregation;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.web.bind.annotation.*;
//import com.mongodb.client.MongoCollection;
//import org.bson.Document;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
///**
// * @Author: 杨航行
// * @Description:
// * @Date: create in 2020/12/14 19:37
// */
//@RestController
//public class PhoneController {
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    @Autowired
//    private MongoDBHelper mongoDBHelper;
//
//    @Autowired
//    private ExcelImportMapper excelImportMapper;
//
//    @Autowired
//    private PhoneMapper phoneMapper;
//
//
//    private DecimalFormat df = new DecimalFormat("#0.00");
//    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    @Value("${WXBUrl}")
//    private String WXBUrl;
//
//    private MongoCollection<Document> getCollection(String collectionName)
//    {
//        return mongoTemplate.getCollection(collectionName);
//    }
//
//
//    @ApiOperation("通话记录统计")
//    @PostMapping("/getPhoneRecord")
//    public ResultBody getPhoneRecord(@RequestBody Map map) throws ParseException {
//        Map resMap = new HashMap();
//        Query query=new Query();
//        Integer pageSize =Integer.parseInt(map.get("pageSize").toString());
//        Integer pageNum =(Integer.parseInt(map.get("pageNum").toString())-1)*pageSize;
//        //项目ID
//        if(map.get("projectList")!=null) {
//            String projectList = map.get("projectList").toString().replace("[","").replace("]","").replace(" ","");
//            String[] str = projectList.split(",");
//            Query query1 = new Query();
//            query1.addCriteria(Criteria.where("projId").in(str));
//            List<Map> projects = mongoTemplate.find(query1, Map.class,"projectMerchantMapping");
//            List paramProject = new ArrayList();
//            for (Map project : projects) {
//                paramProject.add(project.get("projectId"));
//            }
//            query.addCriteria(Criteria.where("projectId").in(paramProject));
//        }
//        //手机号和姓名模糊检索
//        if(map.get("phoneAndName")!=null) {
//            query.addCriteria(new Criteria().orOperator(Criteria.where("name").regex(map.get("phoneAndName") + ""),
//                            Criteria.where("phone").regex(map.get("phoneAndName") + "")));
//        }
//        // 置业顾问名字
//        if(map.get("salerName")!=null){
//            query.addCriteria(Criteria.where("salerName").regex(map.get("salerName").toString()));
//        }
//
//        //意向等级
//        if(map.get("tradeLevel")!=null){
//            query.addCriteria(Criteria.where("tradeLevel").is(map.get("tradeLevel")));
//        }
//        //通话方式 (1代表去电 2代表 来电 来电包括0 2)
//        if(map.get("callStatus")!=null){
//            if(map.get("callStatus").equals("2")){
//                List list = new ArrayList();
//                list.add(2);
//                list.add(0);
//                query.addCriteria(Criteria.where("callStatus").in(list));
//            }else if(map.get("callStatus").equals("1")){
//                query.addCriteria(Criteria.where("callStatus").is(1));
//            }
//        }
//
//        // 电商名称
//        if(map.get("channel")!=null){
//            query.addCriteria(Criteria.where("channel").regex(map.get("channel")+""));
//        }else{
//            //是否电商 0 否 1是
//            if(map.get("isChannel")!=null){
//                // 电商名称
//                if(map.get("isChannel").equals("1")){
//                        query.addCriteria(Criteria.where("channel").ne(null));
//                }else if(map.get("isChannel").equals("0")){
//                        query.addCriteria(Criteria.where("channel").is(null));
//                }
//            }
//        }
//
//        // 是否接通 0否 1是
//        if(map.get("duration")!=null){
//            if(map.get("duration").equals("1")){
//                query.addCriteria(Criteria.where("duration").gte(1));
//            }else if(map.get("duration").equals("0")){
//                query.addCriteria(Criteria.where("duration").is(null));
//            }
//        }
//        //通话时间
//        if(map.get("startTime")!=null && map.get("endTime")!=null){
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date starDate = sdf.parse(map.get("startTime").toString());
//            Date endDate = sdf.parse(map.get("endTime").toString());
//            query.addCriteria(Criteria.where("callTime").gte(starDate).lte(endDate));
//        }
//        //认知渠道
//        if(map.get("parentChannelId")!=null){
//            query.addCriteria(Criteria.where("parentChannelId").is(map.get("parentChannelId")));
//        }
//        // 认知通道
//        if(map.get("childChannelId")!=null){
//            query.addCriteria(Criteria.where("childChannelId").is(map.get("childChannelId")));
//        }
////        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
////        query.with(sort);
//        List<Map> count = mongoTemplate.find(query,Map.class,"customerCallRecord");
//        //分页
//        query.limit(pageSize);
//        query.skip(pageNum);
//        List<Map> resList = mongoTemplate.find(query,Map.class,"customerCallRecord");
//        resMap.put("info",resList);
//        resMap.put("pageSize",map.get("pageSize"));
//        resMap.put("pageNum",map.get("pageNum"));
//        resMap.put("totalCount",count.size());
//        return ResultBody.success(resMap);
//    }
//
//    @ApiOperation("电商统计")
//    @PostMapping("/getPhoneCount")
//    public ResultBody getPhoneCount(@RequestBody Map map) throws ParseException {
//        Integer pageSize =Integer.parseInt(map.get("pageSize").toString());
//        Integer pageNum =(Integer.parseInt(map.get("pageNum").toString())-1)*pageSize;
//        Map resMap = new HashMap();
//        // 统计次数 默认1
//        Criteria criteria =new Criteria();
//        if(map.get("count")!=null) {
//            if (Integer.parseInt(map.get("count").toString()) == 1) {
//                criteria = Criteria.where("phoneCount").is(1);
//            } else if (Integer.parseInt(map.get("count").toString()) == 2) {
//                criteria = Criteria.where("phoneCount").is(2);
//            } else if (Integer.parseInt(map.get("count").toString()) == 3) {
//                criteria = Criteria.where("phoneCount").is(3);
//            } else if (Integer.parseInt(map.get("count").toString()) == 4) {
//                criteria = Criteria.where("phoneCount").is(4);
//            } else {
//                criteria = Criteria.where("phoneCount").gte(5);
//            }
//        }
//       // 项目ID
//        if(map.get("projectList")!=null) {
//            String projectList = map.get("projectList").toString().replace("[","").replace("]","").replace(" ","");
//            String[] str = projectList.split(",");
//            Query query1 = new Query();
//            query1.addCriteria(Criteria.where("projId").in(str));
//            List<Map> projects = mongoTemplate.find(query1, Map.class,"projectMerchantMapping");
//            List paramProject = new ArrayList();
//            for (Map project : projects) {
//                paramProject.add(project.get("projectId"));
//            }
//            if(map.get("count")!=null){
//                criteria.and("projectId").in(paramProject);
//            }else{
//                criteria = Criteria.where("projectId").in(paramProject);
//            }
//
//        }
//
//
//
//        //电商号码
//        if(map.get("channelPhone")!=null){
//            criteria.and("originalPhone").regex(map.get("channelPhone")+"");
//        }else{
//            criteria.and("originalPhone").ne(null);
//        }
//        // 电商名称
//        if(map.get("channel")!=null){
//            criteria.and("channel").regex(map.get("channel")+"");
//        }else{
//            criteria.and("channel").ne(null);
//        }
//        // 真实号码
//        if(map.get("phone")!=null){
//            criteria.and("phone").regex(map.get("phone").toString());
//        }else{
//            criteria.and("phone").ne(null);
//        }
//        //通话时间
//        if(map.get("startTime")!=null && map.get("endTime")!=null){
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date starDate = sdf.parse(map.get("startTime").toString());
//            Date endDate = sdf.parse(map.get("endTime").toString());
//            criteria.and("callTime").gte(starDate).lte(endDate);
//        }
////分页
//        Aggregation aggregation1 = Aggregation.newAggregation(
//                Aggregation.project("phone","originalPhone","channel","callTime","projectName","projectId"),
//                Aggregation.group("phone","originalPhone")
//                        .first("phone").as("phone")
//                        .first("originalPhone").as("originalPhone")
//                        .first("channel").as("channel")
//                        .first("callTime").as("callTime")
//                        .first("projectName").as("projectName")
//                        .first("projectId").as("projectId")
//                        .count().as("phoneCount"),
//                Aggregation.match(criteria),
//                Aggregation.skip(pageNum),
//                Aggregation.limit(pageSize)
//                );
//        Aggregation aggregation2 = Aggregation.newAggregation(
//                Aggregation.project("phone","originalPhone","channel","callTime","projectName","projectId"),
//                Aggregation.group("phone","originalPhone")
//                        .first("phone").as("phone")
//                        .first("originalPhone").as("originalPhone")
//                        .first("channel").as("channel")
//                        .first("callTime").as("callTime")
//                        .first("projectName").as("projectName")
//                        .first("projectId").as("projectId")
//                        .count().as("phoneCount"),
//                Aggregation.match(criteria));
//        List<Map> reslist = mongoTemplate.aggregate(aggregation1,"customerCallRecord",Map.class).getMappedResults();
//        List<Map> count =   mongoTemplate.aggregate(aggregation2,"customerCallRecord",Map.class).getMappedResults();
//        resMap.put("info",reslist);
//        resMap.put("pageSize",map.get("pageSize"));
//        resMap.put("pageNum",map.get("pageNum"));
//        resMap.put("totalCount",count.size());
//        return ResultBody.success(resMap);
//    }
//
//    @ApiOperation("保存接电说辞")
//    @PostMapping("/saveSaleTalk")
//    public ResultBody saveSaleTalk(@RequestBody Map map){
//        if(map.get("text")!=null) {
//            if(map.get("text").equals("")){
//                map.put("text",null);
//            }
//        if(map.get("projectId")!=null){
//            Query query = new Query();
//            query.limit(1);
//            query.addCriteria(Criteria.where("projId").is(map.get("projectId")));
//            List<Map> list = mongoTemplate.find(query,Map.class,"projectMerchantMapping");
//            if(list.size()<=0){
//                return ResultBody.error(10020,"该项目未开通接电说辞功能!");
//            }
//            for (Map map1 : list) {
//                map.put("projectId",map1.get("projectId").toString());
//            }
//        }
//
//            Query query1 = new Query();
//            query1.limit(1);
//            query1.addCriteria(Criteria.where("projectId").is(map.get("projectId").toString()));
//            List list = mongoTemplate.find(query1, Map.class, "saleTalk");
//            if (list.size() > 0) {
//                String projectId = map.get("projectId").toString();
//                map.remove("projectId");
//                mongoDBHelper.updateById(projectId, "saleTalk", map);
//            } else {
//                map.put("_class", "com.wangxiaobao.hermes.entity.mongo.SaleTalk");
//                mongoDBHelper.insert(map, "saleTalk");
//            }
//        }
//        return ResultBody.success("保存成功！");
//    }
//
//    @ApiOperation("获取接电说辞")
//    @GetMapping("/getSaleTalk")
//    public ResultBody getSaleTalk(String projectId){
//        if(projectId!=null){
//            Query query = new Query();
//            query.limit(1);
//            query.addCriteria(Criteria.where("projId").is(projectId));
//            List<Map> list = mongoTemplate.find(query,Map.class,"projectMerchantMapping");
//            if(list.size()<=0){
//                return  ResultBody.success(null);
//            }
//            for (Map map1 : list) {
//                if(map1.get("projectId")!=null) {
//                    projectId = map1.get("projectId") + "";
//                }else{
//                    return  ResultBody.success(null);
//                }
//            }
//            Query query1 = new Query();
//            query1.addCriteria(Criteria.where("projectId").is(projectId));
//            return ResultBody.success(mongoTemplate.find(query1,Map.class,"saleTalk"));
//        }
//        return ResultBody.success(null);
//    }
//
//    @ApiOperation("获取认知渠道")
//    @GetMapping("/getParentChannel")
//    public ResultBody getParentChannel(){
//        return ResultBody.success(phoneMapper.getParentChannel());
//    }
//
//    @ApiOperation("获取认知通道")
//    @PostMapping("/getChildChannel")
//    public ResultBody getChildChannel(@RequestBody PhoneVo phoneVo){
//        if(phoneVo.getProjectList()!=null&&phoneVo.getProjectList().size()>0) {
//            return ResultBody.success(phoneMapper.getChildChannel(phoneVo.getProjectList()));
//        }else {
//            return ResultBody.success(null);
//        }
//    }
//
//
//
//    @RequestMapping(value = "/downLoadZipFile")
//    public ResultBody toHmFileSaveAs(HttpServletResponse response, @RequestBody Map map, String tid) throws IOException, ParseException {
//        Query query=new Query();
//        //项目ID
//        if(map.get("projectList")!=null) {
//            String projectList = map.get("projectList").toString().replace("[","").replace("]","").replace(" ","");
//            String[] str = projectList.split(",");
//            Query query1 = new Query();
//            query1.addCriteria(Criteria.where("projId").in(str));
//            List<Map> projects = mongoTemplate.find(query1, Map.class,"projectMerchantMapping");
//            List paramProject = new ArrayList();
//            for (Map project : projects) {
//                paramProject.add(project.get("projectId"));
//            }
//            query.addCriteria(Criteria.where("projectId").in(paramProject));
//        }
//        //手机号和姓名模糊检索
//        if(map.get("phoneAndName")!=null) {
//            query.addCriteria(new Criteria().orOperator(Criteria.where("name").regex(map.get("phoneAndName") + ""),
//                    Criteria.where("phone").regex(map.get("phoneAndName") + "")));
//        }
//        // 置业顾问名字
//        if(map.get("salerName")!=null){
//            query.addCriteria(Criteria.where("salerName").regex(map.get("salerName").toString()));
//        }
//        // 电商名称
//        if(map.get("channel")!=null){
//            query.addCriteria(Criteria.where("channel").regex(map.get("channel")+""));
//        }
//        //意向等级
//        if(map.get("tradeLevel")!=null){
//            query.addCriteria(Criteria.where("tradeLevel").is(map.get("tradeLevel")));
//        }
//        //通话方式 (1代表去电 2代表 来电 来电包括0 2)
//        if(map.get("callStatus")!=null){
//            if(map.get("callStatus").equals("2")){
//                List list = new ArrayList();
//                list.add(2);
//                list.add(0);
//                query.addCriteria(Criteria.where("callStatus").in(list));
//            }else if(map.get("callStatus").equals("1")){
//                query.addCriteria(Criteria.where("callStatus").is(1));
//            }
//        }
//        //是否电商 0 否 1是
//        if(map.get("isChannel")!=null){
//            if(map.get("isChannel").equals("1")){
//                query.addCriteria(Criteria.where("channel").ne(null));
//            }else if(map.get("isChannel").equals("0")){
//                query.addCriteria(Criteria.where("channel").is(null));
//            }
//        }
//        // 是否接通 0否 1是
//        if(map.get("duration")!=null){
//            if(map.get("duration").equals("1")){
//                query.addCriteria(Criteria.where("duration").ne(null));
//            }else if(map.get("duration").equals("0")){
//                query.addCriteria(Criteria.where("duration").is(null));
//            }
//        }
//        //通话时间
//        if(map.get("startTime")!=null && map.get("endTime")!=null){
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date starDate = sdf.parse(map.get("startTime").toString());
//            Date endDate = sdf.parse(map.get("endTime").toString());
//            query.addCriteria(Criteria.where("callTime").gte(starDate).lte(endDate));
//        }
//        //认知渠道
//        if(map.get("parentChannelId")!=null){
//            query.addCriteria(Criteria.where("parentChannelId").is(map.get("parentChannelId")));
//        }
//        // 认知通道
//        if(map.get("childChannelId")!=null){
//            query.addCriteria(Criteria.where("childChannelId").is(map.get("childChannelId")));
//        }
//        query.addCriteria(Criteria.where("fileName").ne(null));
//        List<Map> reslist = mongoTemplate.find(query,Map.class,"customerCallRecord");
//       if(reslist.size()>100){
//           return ResultBody.error(13773,"超过下载数量下载！");
//       }
//        ZipOutputStream zos = null;
////下载方法
//        try {
//            //文件的名称
//            String downloadFilename = new String("录音文件.zip".getBytes("UTF-8"), "ISO8859-1");//控制文件名编码
//           // String downloadFilename = "录音文件.zip";
//            response.reset();
//            //设置格式
//            response.setContentType("application/x-msdownload");
//            response.setHeader("Access-Control-Allow-Origin","*");
//            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(downloadFilename, "UTF-8"));
//            //ZipOutputStream来对文件压缩操作
//            zos = new ZipOutputStream(response.getOutputStream());
//            //循环下载文件，并将之放到ZipOutputStream中
//            for (Map resMap : reslist) {
//                //filePath是下载路径集合
//                //fileName是文件名称
//                String fileName = resMap.get("fileName").toString();
//                System.out.println(fileName);
//                if(fileName==null || fileName.equals("")){
//                    continue;
//                }
//              String filePath = WXBUrl+fileName.replace(".3gpp",".mp3");
//                System.out.println(filePath);
//               // zos.putNextEntry(new ZipEntry((int)(1+Math.random()*1000)+".mp3"));
//                zos.putNextEntry(new ZipEntry(fileName.replace(".3gpp",".mp3")));
//                URL url = new URL(filePath);
//                InputStream is = null;
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                is = conn.getInputStream();
//                if (conn.getResponseCode() == 200) {
//                    zos.write(readInputStream(is));
//                }
//
//            }
//            zos.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//
//                zos.close();
//            } catch (IOException e) {
//            }
//        }
//        return ResultBody.success(null);
//    }
//
//
//    @ApiOperation(value = "通话详情导出", notes = "")
//    @RequestMapping(value = "/exportPhoneRecord")
//    public void exportPhoneRecord(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) throws ParseException {
//        ExcelExportLog excelExportLog = new ExcelExportLog();
//        String id = UUID.randomUUID().toString();
//        Long nowtime = new Date().getTime();
//        excelExportLog.setId(id);
//        excelExportLog.setMainType("8");
//        excelExportLog.setMainTypeDesc("智能话机管理");
//        excelExportLog.setSubType("ZN1");
//        excelExportLog.setSubTypeDesc("通话详情");
//
//        excelExportLog.setIsAsyn("0");
///*        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
//
//        excelExportLog.setAreaName(proMap.get("areaName")+"");
//        excelExportLog.setProjectId(proMap.get("projectId")+"");
//        excelExportLog.setProjectName(proMap.get("projectName")+"");*/
//
//
//
//        Map map = new HashMap();
//        map = JSONObject.parseObject(param,Map.class);
//        excelExportLog.setCreator(map.get("userId")+"");
//        String ids = map.get("projectStr")+"";
//        if ("".equals(ids)){
//            excelExportLog.setAreaName("/");
//            excelExportLog.setProjectId("/");
//            excelExportLog.setProjectName("/");
//        }else{
//            String[] arr = ids.split(",");
//            List<String> proIdList= new ArrayList<>(Arrays.asList(arr));
//            //获取项目集合数据（事业部，项目Id,项目名称）
//            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
//            excelExportLog.setAreaName(proMap.get("areaName")+"");
//            excelExportLog.setProjectId(proMap.get("projectId")+"");
//            excelExportLog.setProjectName(proMap.get("projectName")+"");
//        }
//
//
//        Query query=new Query();
//        //项目ID
//        if(map.get("projectList")!=null) {
//            String projectList = map.get("projectList").toString().replace("[","").replace("]","").replace(" ","");
//            String[] str = projectList.split(",");
//            List strlist = new ArrayList();
//            for (String s : str) {
//                strlist.add(s.substring(1,s.length()-1));
//            }
//            Query query1 = new Query();
//            query1.addCriteria(Criteria.where("projId").in(strlist));
//            List<Map> projects = mongoTemplate.find(query1, Map.class,"projectMerchantMapping");
//            List paramProject = new ArrayList();
//            for (Map project : projects) {
//                paramProject.add(project.get("projectId"));
//            }
//            query.addCriteria(Criteria.where("projectId").in(paramProject));
//        }
//
//
//        //手机号和姓名模糊检索
//        if(map.get("phoneAndName")!=null) {
//            query.addCriteria(new Criteria().orOperator(Criteria.where("name").regex(map.get("phoneAndName") + ""),
//                    Criteria.where("phone").regex(map.get("phoneAndName") + "")));
//        }
//        // 置业顾问名字
//        if(map.get("salerName")!=null){
//            query.addCriteria(Criteria.where("salerName").regex(map.get("salerName").toString()));
//        }
//        // 电商名称
//        if(map.get("channel")!=null){
//            query.addCriteria(Criteria.where("channel").regex(map.get("channel")+""));
//        }
//        //意向等级
//        if(map.get("tradeLevel")!=null){
//            query.addCriteria(Criteria.where("tradeLevel").is(map.get("tradeLevel")));
//        }
//        //通话方式 (1代表去电 2代表 来电 来电包括0 2)
//        if(map.get("callStatus")!=null){
//            if(map.get("callStatus").equals("2")){
//                List list = new ArrayList();
//                list.add(2);
//                list.add(0);
//                query.addCriteria(Criteria.where("callStatus").in(list));
//            }else if(map.get("callStatus").equals("1")){
//                query.addCriteria(Criteria.where("callStatus").is(1));
//            }
//        }
//        //是否电商 0 否 1是
//        if(map.get("isChannel")!=null){
//            if(map.get("isChannel").equals("1")){
//                query.addCriteria(Criteria.where("channel").ne(null));
//            }else if(map.get("isChannel").equals("0")){
//                query.addCriteria(Criteria.where("channel").is(null));
//            }
//        }
//        // 是否接通 0否 1是
//        if(map.get("duration")!=null){
//            if(map.get("duration").equals("1")){
//                query.addCriteria(Criteria.where("duration").ne(null));
//            }else if(map.get("duration").equals("0")){
//                query.addCriteria(Criteria.where("duration").is(null));
//            }
//        }
//        //通话时间
//        if(map.get("startTime")!=null && map.get("endTime")!=null){
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date starDate = sdf.parse(map.get("startTime").toString());
//            Date endDate = sdf.parse(map.get("endTime").toString());
//            query.addCriteria(Criteria.where("callTime").gte(starDate).lte(endDate));
//        }
//        //认知渠道
//        if(map.get("parentChannelId")!=null){
//            query.addCriteria(Criteria.where("parentChannelId").is(map.get("parentChannelId")));
//        }
//        // 认知通道
//        if(map.get("childChannelId")!=null){
//            query.addCriteria(Criteria.where("childChannelId").is(map.get("childChannelId")));
//        }
////        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
////        query.with(sort);
//        List<CustomerCallRecordVo> list = mongoTemplate.find(query,CustomerCallRecordVo.class,"customerCallRecord");
//       // List<ActivityVowDetailVo> list = vowMapper.getVowDetailExport(map);
//        if (list!=null && list.size()>0){
//            String[] headers = list.get(0).getActivityHelpTitle();
//            ArrayList<Object[]> dataset = new ArrayList<>();
//            String isAll = map.get("isAll")+"";
//            if("1".equals(isAll)){
//                excelExportLog.setExportType("2");
//            }else{
//                excelExportLog.setExportType("1");
//            }
//
//            for (CustomerCallRecordVo ac:list) {
//                Object[] oArray = ac.toActivityHelpData(isAll);
//                dataset.add(oArray);
//            }
//            try{
//                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
//                excelExportUtil.exportExcel("活动许愿明细",headers,dataset,"活动许愿明细",response,null);
//                excelExportLog.setExportStatus("2");
//            }catch (Exception e){
//                e.printStackTrace();
//
//                excelExportLog.setExportStatus("3");
//                excelExportLog.setExceptionMessage(e.getMessage());
//            }
//        }
//        Long export = new Date().getTime();
//        Long exporttime = export-nowtime;
//        String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
//        excelExportLog.setWaitTime("0");
//        excelExportLog.setExportTime(exportTime);
//        excelExportLog.setDownLoadTime(sf.format(new Date()));
//        excelExportLog.setIsDown("1");
//        //保存任务表
//        excelImportMapper.addExcelExportLog(excelExportLog);
//    }
//
//
//
//    @ApiOperation(value = "电商统计", notes = "")
//    @RequestMapping(value = "/exportPhoneCount")
//    public void exportPhoneCount(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) throws ParseException {
//        ExcelExportLog excelExportLog = new ExcelExportLog();
//        String id = UUID.randomUUID().toString();
//        Long nowtime = new Date().getTime();
//        excelExportLog.setId(id);
//        excelExportLog.setMainType("8");
//        excelExportLog.setMainTypeDesc("智能话机管理");
//        excelExportLog.setSubType("ZN2");
//        excelExportLog.setSubTypeDesc("电商统计");
//
//        excelExportLog.setIsAsyn("0");
///*        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
//
//        excelExportLog.setAreaName(proMap.get("areaName")+"");
//        excelExportLog.setProjectId(proMap.get("projectId")+"");
//        excelExportLog.setProjectName(proMap.get("projectName")+"");*/
//
//
//
//        Map map = new HashMap();
//        map = JSONObject.parseObject(param,Map.class);
//        excelExportLog.setCreator(map.get("userId")+"");
//        String ids = map.get("projectStr")+"";
//        if ("".equals(ids)){
//            excelExportLog.setAreaName("/");
//            excelExportLog.setProjectId("/");
//            excelExportLog.setProjectName("/");
//        }else{
//            String[] arr = ids.split(",");
//            List<String> proIdList= new ArrayList<>(Arrays.asList(arr));
//            //获取项目集合数据（事业部，项目Id,项目名称）
//            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
//            excelExportLog.setAreaName(proMap.get("areaName")+"");
//            excelExportLog.setProjectId(proMap.get("projectId")+"");
//            excelExportLog.setProjectName(proMap.get("projectName")+"");
//        }
//
//
//        // 统计次数 默认1
//        Criteria criteria =new Criteria();
//        if(map.get("count")!=null) {
//            if (Integer.parseInt(map.get("count").toString()) == 1) {
//                criteria = Criteria.where("phoneCount").is(1);
//            } else if (Integer.parseInt(map.get("count").toString()) == 2) {
//                criteria = Criteria.where("phoneCount").is(2);
//            } else if (Integer.parseInt(map.get("count").toString()) == 3) {
//                criteria = Criteria.where("phoneCount").is(3);
//            } else if (Integer.parseInt(map.get("count").toString()) == 4) {
//                criteria = Criteria.where("phoneCount").is(4);
//            } else {
//                criteria = Criteria.where("phoneCount").gte(5);
//            }
//        }
//        // 项目ID
//        if(map.get("projectList")!=null) {
//            String projectList = map.get("projectList").toString().replace("[","").replace("]","").replace(" ","");
//            String[] str = projectList.split(",");
//            List strlist = new ArrayList();
//            for (String s : str) {
//                strlist.add(s.substring(1,s.length()-1));
//            }
//            Query query1 = new Query();
//            query1.addCriteria(Criteria.where("projId").in(strlist));
//            List<Map> projects = mongoTemplate.find(query1, Map.class,"projectMerchantMapping");
//            List paramProject = new ArrayList();
//            for (Map project : projects) {
//                paramProject.add(project.get("projectId"));
//            }
//            if(map.get("count")!=null){
//                criteria.and("projectId").in(paramProject);
//            }else{
//                criteria = Criteria.where("projectId").in(paramProject);
//            }
//
//        }
//
//
//        //电商号码
//        if(map.get("channelPhone")!=null){
//            criteria.and("originalPhone").regex(map.get("channelPhone")+"");
//        }else{
//            criteria.and("originalPhone").ne(null);
//        }
//        // 电商名称
//        if(map.get("channel")!=null){
//            criteria.and("channel").regex(map.get("channel")+"");
//        }else{
//            criteria.and("channel").ne(null);
//        }
//        // 真实号码
//        if(map.get("phone")!=null){
//            criteria.and("phone").regex(map.get("phone").toString());
//        }else{
//            criteria.and("phone").ne(null);
//        }
//        //通话时间
//        if(map.get("startTime")!=null && map.get("endTime")!=null){
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date starDate = sdf.parse(map.get("startTime").toString());
//            Date endDate = sdf.parse(map.get("endTime").toString());
//            criteria.and("callTime").gte(starDate).lte(endDate);
//        }
////分页
//        Aggregation aggregation1 = Aggregation.newAggregation(
//                Aggregation.project("phone","originalPhone","channel","callTime","projectName","projectId"),
//                Aggregation.group("phone","originalPhone")
//                        .first("phone").as("phone")
//                        .first("originalPhone").as("originalPhone")
//                        .first("channel").as("channel")
//                        .first("callTime").as("callTime")
//                        .first("projectName").as("projectName")
//                        .first("projectId").as("projectId")
//                        .count().as("phoneCount"),
//                Aggregation.match(criteria)
//        );
//        List<CustomerCallRecordVoCount> list = mongoTemplate.aggregate(aggregation1,"customerCallRecord",CustomerCallRecordVoCount.class).getMappedResults();
//
//
//        // List<ActivityVowDetailVo> list = vowMapper.getVowDetailExport(map);
//        if (list!=null && list.size()>0){
//            String[] headers = list.get(0).getActivityHelpTitle();
//            ArrayList<Object[]> dataset = new ArrayList<>();
//            String isAll = map.get("isAll")+"";
//            if("1".equals(isAll)){
//                excelExportLog.setExportType("2");
//            }else{
//                excelExportLog.setExportType("1");
//            }
//
//            for (CustomerCallRecordVoCount ac:list) {
//                Object[] oArray = ac.toActivityHelpData(isAll);
//                dataset.add(oArray);
//            }
//            try{
//                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
//                excelExportUtil.exportExcel("活动许愿明细",headers,dataset,"活动许愿明细",response,null);
//                excelExportLog.setExportStatus("2");
//            }catch (Exception e){
//                e.printStackTrace();
//
//                excelExportLog.setExportStatus("3");
//                excelExportLog.setExceptionMessage(e.getMessage());
//            }
//        }
//        Long export = new Date().getTime();
//        Long exporttime = export-nowtime;
//        String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
//        excelExportLog.setWaitTime("0");
//        excelExportLog.setExportTime(exportTime);
//        excelExportLog.setDownLoadTime(sf.format(new Date()));
//        excelExportLog.setIsDown("1");
//        //保存任务表
//        excelImportMapper.addExcelExportLog(excelExportLog);
//    }
//
//
//
//    public byte[] readInputStream(InputStream is) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        int length = -1;
//        try {
//            while ((length = is.read(buffer)) != -1) {
//                baos.write(buffer, 0, length);
//            }
//            baos.flush();
//        } catch (IOException e) {
//        }
//        byte[] data = baos.toByteArray();
//        try {
//            is.close();
//            baos.close();
//        } catch (IOException e) {
//        }
//        return data;
//    }
//
//
//    }
