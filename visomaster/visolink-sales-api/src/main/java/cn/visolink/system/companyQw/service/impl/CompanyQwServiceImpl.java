package cn.visolink.system.companyQw.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.companyQw.dao.CompanyQwDao;
import cn.visolink.system.companyQw.model.*;
import cn.visolink.system.companyQw.service.CompanyQwService;
import cn.visolink.system.fileupload.service.FileUploadService;
import cn.visolink.system.projectmanager.dao.BindProject;
import cn.visolink.utils.HttpRequestUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.WeiXinUtil;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.generator.config.IFileCreate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @ClassName CompanyQwServiceImpl
 * @Author wanggang
 * @Description //企微
 * @Date 2021/12/31 9:35
 **/
@Service
public class CompanyQwServiceImpl implements CompanyQwService {

    @Autowired
    private CompanyQwDao companyQwDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FileUploadService fileUploadService;

    @Value("${WX_TOKEN_PATH}")
    private String wxTokenPath;

    @Override
    public List<Map> getComApplet(HttpServletRequest request) {
        List<Map> appList = new ArrayList<>();
        String companycode = request.getHeader("companycode");
        Connection conn = null; PreparedStatement stat = null;
        try {
            String url = "jdbc:mysql://118.190.56.178:3306/authcompany" + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(url, "root", "root");
            // 拼接的sql,用于查询未绑定的项目
            String sql = "select  authorizer_appid appid, nick_name as appName from cltm_wechat_info where tenant_id = '" + companycode + "' limit 1";
            System.out.println("拼接的sql： " + sql);
            stat = conn.prepareStatement(sql);
            //执行脚本
            ResultSet resultSet = stat.executeQuery();
            System.out.println("返回的对象为： " + resultSet);
            // 遍历列表字段
            while (resultSet.next()){
                String appid = resultSet.getString("appid");
                String appName = resultSet.getString("appName");
                Map map = new HashMap();
                map.put("appid",appid);
                map.put("appName",appName);
                appList.add(map);
            }
            return appList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stat.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return appList;
    }

    @Override
    public List<Map> getComAppletPage() {
        return companyQwDao.getComAppletPage();
    }

    @Override
    public ResultBody getComAppletPagePath(HttpServletRequest request, Map map) {

        if (map==null || map.get("projectId")==null || map.get("pageType")==null
        || "".equals(map.get("projectId")+"") || "".equals(map.get("pageType")+"")){
            return ResultBody.error(-1200002,"必传参数为空！！");
        }
        List<Map> result = new ArrayList<>();
        String projectId = map.get("projectId")+"";
        //页面类型 （1：活动 2：新闻 3：楼盘 4：户型）
        String pageType = map.get("pageType")+"";
        if ("1".equals(pageType)){
            //查询活动
            result = companyQwDao.getActivityByProId(map);
        }else if ("2".equals(pageType)){
            //查询新闻
            result = companyQwDao.getNewsByCityId(map);
        }else if ("3".equals(pageType)){
            //查询楼盘
            result = companyQwDao.getBuildingByProId(map);
            if (result!=null && result.size()>0){
                for (Map mm:result) {
                    String path = "";
                    String id =mm.get("id")+"";
                    String proid = mm.get("ProjectID")+"";
                    String proName = mm.get("ProjectName")+"";
                    path = "?ProjectIDs="+proid+"&ProjectNames="+proName+"&buildBookId="+id;
                    mm.put("path",path);
                }
            }


        }else if ("4".equals(pageType)){
            //查询户型
            result = companyQwDao.getBuildHxByProId(map);
            if (result!=null && result.size()>0){
                for (Map mm:result) {
                    String path = "";
                    String buildBookId = mm.get("buildBookId")+"";
                    String id = mm.get("id")+"";
                    path = "?buildBookId="+buildBookId+"&houseTypeId="+id;
                    mm.put("path",path);
                }
            }

        }

        return ResultBody.success(result);
    }

    @Override
    public ResultBody addProMedia(HttpServletRequest request, ProMediaVo proMediaVo) {
        List<MediaDetail> mediaDetails = new ArrayList<>();
        String projectId = proMediaVo.getProjectId();
        List<MediaDetail> photoList = proMediaVo.getPhotoList();
        List<MediaDetail> videoList = proMediaVo.getVideoList();
        List<MediaDetail> h5List = proMediaVo.getH5List();
        List<MediaDetail> appletList = proMediaVo.getAppletList();
        List<MediaDetail> fileList = proMediaVo.getFileList();
        //判断是新增还是修改
        if ("1".equals(proMediaVo.getAddOrEdit())){
            if (photoList!=null && photoList.size()>0){
                for (MediaDetail me:photoList) {
                    me.setProjectId(projectId);
                    me.setMediaType("1");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(photoList);
            }
            if (videoList!=null && videoList.size()>0){
                for (MediaDetail me:videoList) {
                    me.setProjectId(projectId);
                    me.setMediaType("2");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(videoList);
            }
            if (h5List!=null && h5List.size()>0){
                for (MediaDetail me:h5List) {
                    me.setProjectId(projectId);
                    me.setMediaType("4");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(h5List);
            }
            if (appletList!=null && appletList.size()>0){
                for (MediaDetail me:appletList) {
                    me.setProjectId(projectId);
                    me.setMediaType("3");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(appletList);
            }
            if (fileList!=null && fileList.size()>0){
                for (MediaDetail me:fileList) {
                    me.setProjectId(projectId);
                    me.setMediaType("5");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(fileList);
            }
        }else{
            //需要删除的素材
            //查询原素材ID
            List<String> ids = companyQwDao.getMediaIds(projectId);
            if (photoList!=null && photoList.size()>0){
                for (MediaDetail me:photoList) {
                    //判断是否原素材
                    if (StringUtils.isEmpty(me.getId())){
                        me.setProjectId(projectId);
                        me.setMediaType("1");
                        me.setCreator(SecurityUtils.getUserId());
                        mediaDetails.add(me);
                    }else{
                        ids.remove(me.getId());
                    }
                }
            }
            if (videoList!=null && videoList.size()>0){
                for (MediaDetail me:videoList) {
                    //判断是否原素材
                    if (StringUtils.isEmpty(me.getId())){
                        me.setProjectId(projectId);
                        me.setMediaType("2");
                        me.setCreator(SecurityUtils.getUserId());
                        mediaDetails.add(me);
                    }else{
                        ids.remove(me.getId());
                    }
                }
            }
            if (h5List!=null && h5List.size()>0){
                for (MediaDetail me:h5List) {
                    //判断是否原素材
                    if (StringUtils.isEmpty(me.getId())){
                        me.setProjectId(projectId);
                        me.setMediaType("4");
                        me.setCreator(SecurityUtils.getUserId());
                        mediaDetails.add(me);
                    }else{
                        ids.remove(me.getId());
                    }
                }
            }
            if (appletList!=null && appletList.size()>0){
                for (MediaDetail me:appletList) {
                    //判断是否原素材
                    if (StringUtils.isEmpty(me.getId())){
                        me.setProjectId(projectId);
                        me.setMediaType("3");
                        me.setCreator(SecurityUtils.getUserId());
                        mediaDetails.add(me);
                    }else{
                        ids.remove(me.getId());
                    }
                }
            }
            if (fileList!=null && fileList.size()>0){
                for (MediaDetail me:fileList) {
                    //判断是否原素材
                    if (StringUtils.isEmpty(me.getId())){
                        me.setProjectId(projectId);
                        me.setMediaType("5");
                        me.setCreator(SecurityUtils.getUserId());
                        mediaDetails.add(me);
                    }else{
                        ids.remove(me.getId());
                    }
                }
            }
            if (ids.size()>0){
                //删除服务器文件
                List<String> names = companyQwDao.getDelProMedia(ids);
                //删除素材
                companyQwDao.delProMedia(ids);
                if (names!=null && names.size()>0){
                    List<String> delNames = new ArrayList<>();
                    for (String id:names) {
                        int index = id.lastIndexOf("/");
                        int i = index - 6;
                        String name = id.substring(i);
                        delNames.add(name);
                    }
                    try {
                        fileUploadService.uploadQwFiles(null,null,delNames.toArray(new String[delNames.size()]),"1");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //保存新数据
        if (mediaDetails.size()>0){
            companyQwDao.addProMediaList(mediaDetails);
        }
        return ResultBody.success("保存成功！！");
    }

    @Override
    public ResultBody getProMediaList(HttpServletRequest request, Map map) {
        if (map==null || map.get("projectIds")==null
                || "".equals(map.get("projectIds")+"")){
            return ResultBody.error(-1200002,"必传参数为空！！");
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        String[] ids = map.get("projectIds").toString().split(",");
        map.put("ids",ids);
        PageHelper.startPage(pageIndex,pageSize);
        List<Map> proMediaList = companyQwDao.getProMediaList(map);
        return ResultBody.success(new PageInfo<>(proMediaList));
    }

    @Override
    public ResultBody getProMediaDetail(HttpServletRequest request, Map map) {
        if (map==null || map.get("projectId")==null
                || "".equals(map.get("projectId")+"")){
            return ResultBody.error(-1200002,"必传参数为空！！");
        }
        String projectId = map.get("projectId")+"";
        ProMediaVo proMediaVo = new ProMediaVo();
        proMediaVo.setProjectId(projectId);
        List<MediaDetail> mediaDetails = companyQwDao.getMediaDetails(projectId);
        List<MediaDetail> photoList = new ArrayList<>();
        List<MediaDetail> videoList = new ArrayList<>();
        List<MediaDetail> h5List = new ArrayList<>();
        List<MediaDetail> appletList = new ArrayList<>();
        List<MediaDetail> fileList = new ArrayList<>();
        if (mediaDetails!=null && mediaDetails.size()>0){
            for (MediaDetail media:mediaDetails) {
                if ("1".equals(media.getMediaType())){
                    photoList.add(media);
                }else if ("2".equals(media.getMediaType())){
                    videoList.add(media);
                }else if ("3".equals(media.getMediaType())){
                    appletList.add(media);
                }else if ("4".equals(media.getMediaType())){
                    h5List.add(media);
                }else if ("5".equals(media.getMediaType())){
                    fileList.add(media);
                }
            }
        }
        proMediaVo.setPhotoList(photoList);
        proMediaVo.setAppletList(appletList);
        proMediaVo.setVideoList(videoList);
        proMediaVo.setH5List(h5List);
        proMediaVo.setFileList(fileList);
        return ResultBody.success(proMediaVo);
    }

    @Override
    public ResultBody getProMediaIsOk(HttpServletRequest request, Map map) {
        if (map==null || map.get("projectId")==null
                || "".equals(map.get("projectId")+"")){
            return ResultBody.error(-1200002,"必传参数为空！！");
        }
        String projectId = map.get("projectId")+"";
        int count = companyQwDao.getProMediaIsOk(projectId);
        return ResultBody.success(count);
    }

    @Override
    public ResultBody addSensitiveWordsType(HttpServletRequest request, String sensitiveWordsType) {
        //查询分类是否存在
        int count = companyQwDao.getSensitiveWordsTypeIsOk(sensitiveWordsType);
        if (count>0){
            return ResultBody.error(-1200002,"敏感词分类已存在！");
        }
        Map map = new HashMap();
        map.put("creator",SecurityUtils.getUserId());
        map.put("type_name",sensitiveWordsType);
        companyQwDao.addSensitiveWordsType(map);
        return ResultBody.success("保存成功！！");
    }

    @Override
    public ResultBody getSensitiveWords(HttpServletRequest request, Map map) {
        if (map==null || map.get("projectIds")==null
                || "".equals(map.get("projectIds")+"")){
            return ResultBody.error(-1200002,"必传参数为空！！");
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        String[] ids = map.get("projectIds").toString().split(",");
        map.put("ids",Arrays.asList(ids));
        PageHelper.startPage(pageIndex,pageSize);
        List<SensitiveWordVo> list = companyQwDao.getSensitiveWords(map);
        return ResultBody.success(new PageInfo<>(list));
    }

    @Override
    public ResultBody delSensitiveWord(HttpServletRequest request, String id) {
        String companycode = request.getHeader("companycode");
        //逻辑删除本地数据
        companyQwDao.delSensitiveWord(id);
        //调用企微接口删除企微信息
        String rule_id = companyQwDao.getSensitiveWordById(id);
        String token = redisUtil.get("QW_CSTTOKEN_"+companycode).toString();
        Map jsonMap = new HashMap();
        jsonMap.put("rule_id",rule_id);
        HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/del_intercept_rule?access_token="+token,
                JSONObject.parseObject(JSONObject.toJSONString(jsonMap)),false);
        return ResultBody.success("删除成功！！");
    }

    @Override
    public ResultBody addOrEditSensitiveWord(HttpServletRequest request, SensitiveWordVo sensitiveWordVo) {
        Map wxMap = new HashMap();
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"2",false);
        String projectIds = sensitiveWordVo.getProjectIds();
        String ruleId = "";
        //根据项目获取部门ID
        String[] proIds = projectIds.split(",");
        String paramIds = "'"+StringUtils.join(proIds,"','")+"'";
        List<Map> departments = companyQwDao.getDepartmentByPro(paramIds);
        if (departments==null || departments.size()==0){
            return ResultBody.error(-1200002,"所选项目未绑定企微组织！");
        }
        //判断新增还是修改
        if ("1".equals(sensitiveWordVo.getAddOrEdit())){
            List<String> deptList = new ArrayList<>();
            for (Map dept:departments) {
                deptList.add(dept.get("deptId")+"");
            }
            //新增
            wxMap.put("rule_name",sensitiveWordVo.getRuleName());
            String[] words = sensitiveWordVo.getWordList().split(",");
            wxMap.put("word_list",Arrays.asList(words));
            wxMap.put("intercept_type",sensitiveWordVo.getInterceptType());
            Map rangMap = new HashMap();
            rangMap.put("department_list",deptList);
            wxMap.put("applicable_range",rangMap);
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_intercept_rule?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
            //如果成功
            if (re!=null && "0".equals(re.getString("errcode"))){
                ruleId = re.getString("rule_id");
                sensitiveWordVo.setRuleId(ruleId);
                sensitiveWordVo.setCreator(SecurityUtils.getUserId());
                //保存关键词
                companyQwDao.addSensitiveWord(sensitiveWordVo);
                List<Map> wordPros = new ArrayList<>();
                String wordId = sensitiveWordVo.getId();
                for (Map dept:departments) {
                    dept.put("ruleId",ruleId);
                    dept.put("wordId",wordId);
                    dept.put("creator",SecurityUtils.getUserId());
                    wordPros.add(dept);
                }
                //保存关键词关联项目部门
                companyQwDao.addWordPros(wordPros);
            }

        }else{
            //修改
            ruleId = sensitiveWordVo.getRuleId();
            //获取原规则绑定项目部门
            List<Map> deptOld = companyQwDao.getDepartmentById(sensitiveWordVo.getId());
            for (int i = 0;i<deptOld.size();i++) {
                Map old = deptOld.get(i);
                boolean ff = false;
                for (int j = 0; j < departments.size(); j++) {
                    Map ne = departments.get(j);
                    if (old.get("deptId").toString().equals(ne.get("deptId").toString())){
                        departments.remove(ne);
                        deptOld.remove(old);
                        ff = true;
                        break;
                    }
                }
                if (ff){
                    i--;
                }
            }
            wxMap.put("rule_name",sensitiveWordVo.getRuleName());
            wxMap.put("rule_id",ruleId);
            wxMap.put("word_list",sensitiveWordVo.getWordList().split(","));
            wxMap.put("intercept_type",sensitiveWordVo.getInterceptType());

            //如果有删除的部门
            if (deptOld.size()>0){
                List<String> deptList = new ArrayList<>();
                for (Map dept:deptOld) {
                    deptList.add(dept.get("deptId")+"");
                }
                Map rangDelMap = new HashMap();
                rangDelMap.put("department_list",deptList);
                wxMap.put("remove_applicable_range",rangDelMap);
                String deptIds = "'"+StringUtils.join(deptList,"','")+"'";
                Map delMap = new HashMap();
                delMap.put("wordId",sensitiveWordVo.getId());
                delMap.put("deptIds",deptIds);
                //逻辑删除关联项目部门
                companyQwDao.delWordPros(delMap);
            }
            //如果有新增的部门
            if (departments.size()>0){
                List<Map> wordPros = new ArrayList<>();
                String wordId = sensitiveWordVo.getId();
                List<String> deptList = new ArrayList<>();
                for (Map dept:departments) {
                    deptList.add(dept.get("deptId")+"");
                    dept.put("ruleId",ruleId);
                    dept.put("wordId",wordId);
                    dept.put("creator",SecurityUtils.getUserId());
                    wordPros.add(dept);
                }
                Map rangAddMap = new HashMap();
                rangAddMap.put("department_list",deptList);
                wxMap.put("add_applicable_range",rangAddMap);
                //保存新增的关键词关联项目部门
                companyQwDao.addWordPros(wordPros);
            }
            //调用企微接口编辑规则
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/update_intercept_rule?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
            //如果成功
            if (re!=null && "0".equals(re.getString("errcode"))){

            }
        }
        return ResultBody.success("保存成功！！");
    }

    @Override
    public ResultBody getSensitiveWordsType() {
        List<Map> sensitiveWordsTypes = companyQwDao.getSensitiveWordsTypes();
        return ResultBody.success(sensitiveWordsTypes);
    }

    @Override
    public ResultBody getChannelCodeList(Map map) {
        if (map==null || map.get("projectIds")==null
                || "".equals(map.get("projectIds")+"")){
            return ResultBody.error(-1200002,"必传参数为空！！");
        }
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        String[] ids = map.get("projectIds").toString().split(",");
        map.put("ids",ids);
        PageHelper.startPage(pageIndex,pageSize);
        List<ChannelCode> list = companyQwDao.getChannelCodes(map);
        return ResultBody.success(new PageInfo<>(list));
    }

    @Override
    public ResultBody addOrEditChannelCode(HttpServletRequest request,ChannelCode channelCode) {
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"2",false);
        //删除企微渠道码
        Map qwMap = new HashMap();
        List<MediaDetail> mediaDetails = new ArrayList<>();
        List<String> userIds = channelCode.getUserList();
        Map codeMap = new HashMap();
        //查询是否可配置二维码 （渠道和人员重复 不可再次创建）
        codeMap.put("state",channelCode.getState());
        codeMap.put("contactId",channelCode.getId());
        codeMap.put("userIds",userIds);
        int count = companyQwDao.getChannelCodeIsOk(codeMap);
        if (count>0){
            return ResultBody.error(-120002,"渠道和人员已存在，请核对后重试！");
        }
        //判断是否添加标签
        if ("1".equals(channelCode.getIsAutoAddTag())){
            String tagName = channelCode.getTagName();
            //判断标签是否存在
            String tagId = companyQwDao.getTagIsOk(tagName);
            if (tagId!=null){
                channelCode.setTagId(tagId);
            }else{
                String tagGroup = companyQwDao.getTagGroupIsOk();
                String tagGroupName = "渠道码标签";
                //添加企微标签
                Map tagMap = new HashMap();
                tagMap.put("group_id",tagGroup);
                tagMap.put("group_name",tagGroupName);
                List<Map> tagList = new ArrayList<>();
                Map tag = new HashMap();
                tag.put("name",tagName);
                tagList.add(tag);
                tagMap.put("tag",tagList);
                //调用企微接口保存企微二维码
                JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_corp_tag?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(tagMap)),false);
                //如果成功
                if (re!=null && "0".equals(re.getString("errcode"))){
                    JSONObject jsonObject = re.getJSONObject("tag_group");
                    if (tagGroup==null){
                        tagGroup = jsonObject.getString("group_id");
                        Map tagGroupMap = new HashMap();
                        tagGroupMap.put("groupId",tagGroup);
                        tagGroupMap.put("groupName",tagGroupName);
                        companyQwDao.addTagGroup(tagGroupMap);
                    }
                    JSONArray jsonArray = jsonObject.getJSONArray("tag");
                    JSONObject tagObject = jsonArray.getJSONObject(0);
                    Map tt = new HashMap();
                    tt.put("tagId",tagObject.getString("id"));
                    tt.put("tagName",tagObject.getString("name"));
                    tt.put("groupId",jsonObject.getString("group_id"));
                    tt.put("groupName",tagGroupName);
                    companyQwDao.addCstTag(tt);
                    channelCode.setTagId(tagObject.getString("id"));
                }
            }
        }

        //判断新增还是编辑
        if ("1".equals(channelCode.getAddOrEdit())){
            //保存企微二维码
            qwMap.put("type",channelCode.getType());
            qwMap.put("scene","2");
            qwMap.put("skip_verify",channelCode.getSkipVerify().equals("1")?true:false);
            qwMap.put("state",channelCode.getState());
            qwMap.put("user",channelCode.getUserList());
            //调用企微接口保存企微二维码
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_contact_way?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(qwMap)),false);
            //如果成功
            if (re!=null && "0".equals(re.getString("errcode"))){
                String configId = re.getString("config_id");
                String qrCode = re.getString("qr_code");
                //保存渠道码
                channelCode.setConfigId(configId);
                channelCode.setQrCode(qrCode);
                channelCode.setCreator(SecurityUtils.getUserId());
                companyQwDao.addChannelCode(channelCode);
                String channelId = channelCode.getId();
                //保存渠道码人员
                List<Map> userMaps = new ArrayList<>();
                for (String userId:userIds) {
                    Map uu = new HashMap();
                    uu.put("userId",userId);
                    uu.put("type",channelCode.getType());
                    uu.put("contactId",channelId);
                    uu.put("state",channelCode.getState());
                    uu.put("configId",channelCode.getConfigId());
                    uu.put("qrCode",channelCode.getQrCode());
                    uu.put("creator",SecurityUtils.getUserId());
                    userMaps.add(uu);
                }
                companyQwDao.addChannelCodeUser(userMaps);
                //判断是否配置欢迎语
                if ("1".equals(channelCode.getIsAddWelcomeWords())){
                    List<MediaDetail> photoList = channelCode.getPhotoList();
                    List<MediaDetail> videoList = channelCode.getVideoList();
                    List<MediaDetail> h5List = channelCode.getH5List();
                    List<MediaDetail> appletList = channelCode.getAppletList();
                    List<MediaDetail> fileList = channelCode.getFileList();
                    List<MediaDetail> textList = channelCode.getTextList();
                    if (photoList!=null && photoList.size()>0){
                        for (MediaDetail me:photoList) {
                            me.setContactId(channelId);
                            me.setMediaType("1");
                            me.setCreator(SecurityUtils.getUserId());
                        }
                        mediaDetails.addAll(photoList);
                    }
                    if (videoList!=null && videoList.size()>0){
                        for (MediaDetail me:videoList) {
                            me.setContactId(channelId);
                            me.setMediaType("2");
                            me.setCreator(SecurityUtils.getUserId());
                        }
                        mediaDetails.addAll(videoList);
                    }
                    if (h5List!=null && h5List.size()>0){
                        for (MediaDetail me:h5List) {
                            me.setContactId(channelId);
                            me.setMediaType("4");
                            me.setCreator(SecurityUtils.getUserId());
                        }
                        mediaDetails.addAll(h5List);
                    }
                    if (appletList!=null && appletList.size()>0){
                        for (MediaDetail me:appletList) {
                            me.setContactId(channelId);
                            me.setMediaType("3");
                            me.setCreator(SecurityUtils.getUserId());
                        }
                        mediaDetails.addAll(appletList);
                    }
                    if (fileList!=null && fileList.size()>0){
                        for (MediaDetail me:fileList) {
                            me.setContactId(channelId);
                            me.setMediaType("5");
                            me.setCreator(SecurityUtils.getUserId());
                        }
                        mediaDetails.addAll(fileList);
                    }
                    if (textList!=null && textList.size()>0){
                        for (MediaDetail me:textList) {
                            me.setContactId(channelId);
                            me.setMediaType("6");
                            me.setCreator(SecurityUtils.getUserId());
                        }
                        mediaDetails.addAll(textList);
                    }
                }
            }else{
                return ResultBody.error(-120002,"调用企微新增渠道码失败！");
            }

        }else{
            qwMap.put("type",channelCode.getType());
            qwMap.put("config_id",channelCode.getConfigId());
            qwMap.put("skip_verify",channelCode.getSkipVerify().equals("1")?true:false);
            qwMap.put("state",channelCode.getState());
            qwMap.put("user",channelCode.getUserList());
            //调用企微接口更新企微二维码
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/update_contact_way?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(qwMap)),false);
            if (re!=null && "0".equals(re.getString("errcode"))){
                //更新渠道码
                companyQwDao.updateChannelCode(channelCode);
                //先删除渠道码人员
                companyQwDao.delChannelCodeUser(channelCode.getId());
                String contactId = channelCode.getId();
                List<Map> userMaps = new ArrayList<>();
                for (String userId:userIds) {
                    Map uu = new HashMap();
                    uu.put("userId",userId);
                    uu.put("type",channelCode.getType());
                    uu.put("contactId",contactId);
                    uu.put("state",channelCode.getState());
                    uu.put("configId",channelCode.getConfigId());
                    uu.put("qrCode",channelCode.getQrCode());
                    uu.put("creator",SecurityUtils.getUserId());
                    userMaps.add(uu);
                }
                companyQwDao.addChannelCodeUser(userMaps);
                //判断是否配置欢迎语
                if ("1".equals(channelCode.getIsAddWelcomeWords())){
                    List<MediaDetail> photoList = channelCode.getPhotoList();
                    List<MediaDetail> videoList = channelCode.getVideoList();
                    List<MediaDetail> h5List = channelCode.getH5List();
                    List<MediaDetail> appletList = channelCode.getAppletList();
                    List<MediaDetail> fileList = channelCode.getFileList();
                    List<MediaDetail> textList = channelCode.getTextList();
                    //查询原素材ID
                    List<String> ids = companyQwDao.getChannelCodeMediaIds(contactId);
                    if (photoList!=null && photoList.size()>0){
                        for (MediaDetail me:photoList) {
                            //判断是否原素材
                            if (StringUtils.isEmpty(me.getId())){
                                me.setContactId(contactId);
                                me.setMediaType("1");
                                me.setCreator(SecurityUtils.getUserId());
                                mediaDetails.add(me);
                            }else{
                                ids.remove(me.getId());
                            }
                        }
                    }
                    if (videoList!=null && videoList.size()>0){
                        for (MediaDetail me:videoList) {
                            //判断是否原素材
                            if (StringUtils.isEmpty(me.getId())){
                                me.setContactId(contactId);
                                me.setMediaType("2");
                                me.setCreator(SecurityUtils.getUserId());
                                mediaDetails.add(me);
                            }else{
                                ids.remove(me.getId());
                            }
                        }
                    }
                    if (h5List!=null && h5List.size()>0){
                        for (MediaDetail me:h5List) {
                            //判断是否原素材
                            if (StringUtils.isEmpty(me.getId())){
                                me.setContactId(contactId);
                                me.setMediaType("4");
                                me.setCreator(SecurityUtils.getUserId());
                                mediaDetails.add(me);
                            }else{
                                ids.remove(me.getId());
                            }
                        }
                    }
                    if (appletList!=null && appletList.size()>0){
                        for (MediaDetail me:appletList) {
                            //判断是否原素材
                            if (StringUtils.isEmpty(me.getId())){
                                me.setContactId(contactId);
                                me.setMediaType("3");
                                me.setCreator(SecurityUtils.getUserId());
                                mediaDetails.add(me);
                            }else{
                                ids.remove(me.getId());
                            }
                        }
                    }
                    if (fileList!=null && fileList.size()>0){
                        for (MediaDetail me:fileList) {
                            //判断是否原素材
                            if (StringUtils.isEmpty(me.getId())){
                                me.setContactId(contactId);
                                me.setMediaType("5");
                                me.setCreator(SecurityUtils.getUserId());
                                mediaDetails.add(me);
                            }else{
                                ids.remove(me.getId());
                            }
                        }
                    }
                    if (textList!=null && textList.size()>0){
                        for (MediaDetail me:textList) {
                            //判断是否原素材
                            if (StringUtils.isEmpty(me.getId())){
                                me.setContactId(contactId);
                                me.setMediaType("6");
                                me.setCreator(SecurityUtils.getUserId());
                                mediaDetails.add(me);
                            }else{
                                ids.remove(me.getId());
                            }
                        }
                    }
                    if (ids.size()>0){
                        //删除服务器文件
                        List<String> names = companyQwDao.getDelChannelCodeMedia(ids);
                        //删除素材
                        companyQwDao.delChannelCodeMedia(ids);
                        if (names!=null && names.size()>0){
                            List<String> delNames = new ArrayList<>();
                            for (String id:names) {
                                int index = id.lastIndexOf("/");
                                int i = index - 6;
                                String name = id.substring(i);
                                delNames.add(name);
                            }
                            try {
                                fileUploadService.uploadQwFiles(null,null,delNames.toArray(new String[delNames.size()]),"1");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }else{
                return ResultBody.error(-120002,"调用企微更新渠道码失败！");
            }
        }


        if (mediaDetails.size()>0){
            companyQwDao.addChannelCodeMediaList(mediaDetails);
        }
        return ResultBody.success("保存成功！！");
    }

    @Override
    public ResultBody delChannelCode(HttpServletRequest request,String id) {
        //查询渠道码信息
        ChannelCode channelCode = companyQwDao.getChannelCodeById(id);
        if (channelCode==null){
            return ResultBody.error(-120002,"渠道码已删除，请刷新！");
        }
        String configId = channelCode.getConfigId();
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"2",false);
        //删除企微渠道码
        Map qwMap = new HashMap();
        qwMap.put("config_id",configId);
        //调用企微接口删除企微渠道码
        JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/del_contact_way?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(qwMap)),false);
        //如果成功
        if (re!=null && "0".equals(re.getString("errcode"))){
            //删除本地渠道码
            companyQwDao.delChannelCodeById(id);
            return ResultBody.success("删除成功！！");
        }else{
            return ResultBody.error(-120002,"调用企微删除渠道码失败！");
        }
    }

    @Override
    public ResultBody distributeEmployees(HttpServletRequest request,String id) {
        //查询渠道码信息
        ChannelCode channelCode = companyQwDao.getChannelCodeById(id);
        if (channelCode==null){
            return ResultBody.error(-120002,"渠道码已删除，请刷新！");
        }
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"3",false);
        String agentid = "1000002";
        List<String> userIds = companyQwDao.getChannelCodeUser(id);
        if (userIds!=null && userIds.size()>0){
            Map wxMap = new HashMap();
            wxMap.put("agentid",agentid);
            //给成员发送消息进行推广
            Map text = new HashMap();
            String touser = StringUtils.join(userIds,"|");
            String msgtype = "text";
            String content = channelCode.getState()+"的渠道码已发放，点击链接保存渠道码即可去推广："+channelCode.getQrCode();
            text.put("content",content);
            wxMap.put("touser",touser);
            wxMap.put("msgtype",msgtype);
            wxMap.put("text",text);
            //调用企微接口发消息
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
            //如果成功
            if (re!=null && "0".equals(re.getString("errcode"))){

            }else{
                return ResultBody.error(-120002,"调用企微删除渠道码失败！");
            }
        }
        return ResultBody.success("发送成功！！");
    }

    @Override
    public ResultBody getEmployees(String projectId) {
        List<Map> users = companyQwDao.getEmployees(projectId);
        return ResultBody.success(users);
    }

    @Override
    public ResultBody getChannelCode(String id) {
        ChannelCode channelCode = companyQwDao.getChannelCodeById(id);
        List<String> userIds = companyQwDao.getChannelCodeUser(id);
        channelCode.setUserList(userIds);
        if ("1".equals(channelCode.getIsAddWelcomeWords())){
            List<MediaDetail> mediaDetails = companyQwDao.getChannelCodeMediaDetails(id);
            List<MediaDetail> photoList = new ArrayList<>();
            List<MediaDetail> videoList = new ArrayList<>();
            List<MediaDetail> h5List = new ArrayList<>();
            List<MediaDetail> appletList = new ArrayList<>();
            List<MediaDetail> fileList = new ArrayList<>();
            List<MediaDetail> textList = new ArrayList<>();
            if (mediaDetails!=null && mediaDetails.size()>0){
                for (MediaDetail media:mediaDetails) {
                    if ("1".equals(media.getMediaType())){
                        photoList.add(media);
                    }else if ("2".equals(media.getMediaType())){
                        videoList.add(media);
                    }else if ("3".equals(media.getMediaType())){
                        appletList.add(media);
                    }else if ("4".equals(media.getMediaType())){
                        h5List.add(media);
                    }else if ("5".equals(media.getMediaType())){
                        fileList.add(media);
                    }else if ("6".equals(media.getMediaType())){
                        textList.add(media);
                    }
                }
            }
            channelCode.setPhotoList(photoList);
            channelCode.setAppletList(appletList);
            channelCode.setVideoList(videoList);
            channelCode.setH5List(h5List);
            channelCode.setFileList(fileList);
            channelCode.setTextList(textList);
        }
        return ResultBody.success(channelCode);
    }

    @Override
    public ResultBody getDeptList(Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<CompanyQwDept> companyQwDepts = companyQwDao.getCompanyQwDepts(map);
        return ResultBody.success(new PageInfo<>(companyQwDepts));
    }

    @Override
    public ResultBody delDept(HttpServletRequest request, String id) {
        //查询部门是否可以删除
        //查询是否存在子部门
        int childCount = companyQwDao.getDeptChild(id);
        if (childCount>0){
            return ResultBody.error(-120002,"此部门存在子部门无法删除！！");
        }
        //查询是否存在成员
        int userCount = companyQwDao.getDeptUser(id);
        if (userCount>0){
            return ResultBody.error(-120002,"此部门下存在成员无法删除！！");
        }
        //调用企微接口删除部门
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"1",false);
        String re = HttpRequestUtil.httpGet("https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token="+token+"&id="+id,false);
        if (re!=null){
            JSONObject jsonObject = JSONObject.parseObject(re);
            //删除成功
            if ("0".equals(jsonObject.getString("errcode"))){
                //删除本地部门
                companyQwDao.delDeptById(id);
            }else{
                return ResultBody.error(-120002,"调用企微接口失败！！");
            }
        }else{
            return ResultBody.error(-120002,"调用企微接口失败！！");
        }
        return ResultBody.success("删除成功！！");
    }

    @Override
    public ResultBody deptBinding(HttpServletRequest request, Map map) {
        //获取参数
        String type = map.get("type")+"";//类型
        String id = map.get("id")+"";//部门ID
        String orgId = map.get("orgId")+"";//组织ID
        String projectId = null;//项目ID
        if (map.get("projectId")!=null && !"".equals(map.get("projectId"))){
            projectId = map.get("projectId")+"";
        }
        //判断类型（1：绑定 2：解绑）
        if ("2".equals(type)){
            //判断是否可以解除绑定 子级部门必须已解除绑定而且部门下成员不能关联案场客户
            //判断子部门是否已解除绑定
            int deptChild = companyQwDao.getDeptChildIsBind(id);
            if (deptChild>0){
                return ResultBody.error(-120002,"请先将子部门解除绑定！！");
            }
            //判断部门下成员是否关联案场客户
            int cstCount = companyQwDao.getDeptUserCst(id);
            if (cstCount>0){
                return ResultBody.error(-120002,"部门下成员已存在案场客户无法解除绑定！！");
            }
            //解除绑定
            companyQwDao.delDeptOrg(id,SecurityUtils.getUserId());
        } else {
            //判断是否可以绑定 父级部门必须已绑定
            int deptParent = companyQwDao.getDeptParentIsBind(id);
            if (deptParent==0){
                return ResultBody.error(-120002,"父级部门未绑定或此部门已绑定组织无法操作！！");
            }
            //绑定组织
            companyQwDao.bindOrg(id,orgId,projectId,SecurityUtils.getUserId());
        }
        return ResultBody.success("操作成功！！");
    }

    @Override
    public ResultBody getPDeptOrg(HttpServletRequest request, String id) {
        List<Map> orgList = companyQwDao.getPDeptOrg(id);
        return ResultBody.success(orgList);
    }

    @Override
    public ResultBody getQuitUserList(Map map) {
        String[] projectIds = map.get("projectIds").toString().split(",");
        List<String> ids = Arrays.asList(projectIds);
        map.put("ids",ids);
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<QuitUser> users = companyQwDao.getQuitUserList(map);
        return ResultBody.success(new PageInfo<>(users));
    }

    @Override
    public ResultBody quitUserRedistribution(HttpServletRequest request,Map map) {
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"2",false);
        String useridOld = map.get("useridOld")+"";
        String useridNew = map.get("useridNew")+"";
        String externalUserid = map.get("externalUserid")+"";
        //保存分配批次表
        Map batch = new HashMap();
        batch.put("userid_old",useridOld);
        batch.put("external_userids",externalUserid);
        batch.put("userid_new",useridNew);
        batch.put("creator",SecurityUtils.getUserId());
        companyQwDao.addRedistributionBatch(batch);
        List<String> cstIds = Arrays.asList(externalUserid.split(","));
        //调用企微接口进行离职重分配
        Map wxMap = new HashMap();
        wxMap.put("handover_userid",useridOld);
        wxMap.put("takeover_userid",useridNew);
        wxMap.put("external_userid",cstIds);
        //调用企微接口发消息
        JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/resigned/transfer_customer?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
        //如果成功
        if (re!=null && "0".equals(re.getString("errcode"))){
            List<String> cstOkIds = new ArrayList<>();
            List<String> error = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            JSONArray jsonArray = re.getJSONArray("customer");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if ("0".equals(jsonObject.getString("errcode"))){
                    cstOkIds.add(jsonObject.getString("external_userid"));
                }else{
                    String msg = "";
                    if ("40097".equals(jsonObject.getString("errcode"))){
                        msg = "该成员尚未离职";
                        if (!error.contains(msg)){
                            error.add(msg);
                        }
                    }else if ("40098".equals(jsonObject.getString("errcode"))){
                        msg = "成员尚未实名认证";
                        if (!error.contains(msg)){
                            error.add(msg);
                        }
                    }else if ("40099".equals(jsonObject.getString("errcode"))){
                        msg = "外部联系人的数量已达上限";
                        if (!error.contains(msg)){
                            error.add(msg);
                        }
                    }else if ("40100".equals(jsonObject.getString("errcode"))){
                        msg = "此用户的外部联系人已经在转移流程中";
                        if (!error.contains(msg)){
                            error.add(msg);
                        }
                    }else{
                        msg = "错误码："+jsonObject.getString("errcode");
                        if (!error.contains(msg)){
                            error.add(msg);
                        }
                    }
                }
            }
            String mm = "";
            if (error.size()>0){
                if (cstOkIds.size()>0){
                    mm = "部分客户重分配失败，失败原因：";
                }else{
                    mm = "客户重分配失败，失败原因：";
                }
                sb.append(mm);
                for (String e:error) {
                    sb.append(e+",");
                }
            }else{
                mm = "客户重分配发起成功！";
                sb.append(mm);
            }
            if (cstOkIds.size()>0){
                //更新离职成员客户状态为等待继承
                Map cstMap = new HashMap();
                cstMap.put("cstList",cstOkIds);
                cstMap.put("distUser",SecurityUtils.getUserId());
                cstMap.put("useridNew",useridNew);
                companyQwDao.updateQuitUserCst(cstMap);
                return ResultBody.success(sb.toString());
            }else{
                return ResultBody.error(-120002,sb.toString());
            }
        }else{
            return ResultBody.error(-120002,"调用企微离职重分配失败！");
        }
    }

    @Override
    public ResultBody quitUserRedistDetail(Map map) {
        List<String> statusList = new ArrayList<>();
        if (map.get("status")!=null && !"".equals(map.get("status"))){
            String[] ss = map.get("status").toString().split(",");
            if (ss!=null && ss.length>0){
                statusList = Arrays.asList(ss);
            }
        }
        map.put("statusList",statusList);
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<QuitUserCst> cstList = companyQwDao.quitUserRedistDetail(map);
        return ResultBody.success(new PageInfo<>(cstList));
    }

    @Override
    public ResultBody getProEmployees(Map map) {
        List<Map> users = companyQwDao.getProEmployees(map);
        return ResultBody.success(users);
    }

    @Override
    public ResultBody addQwCstTagGroup(HttpServletRequest request, String groupName) {
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"2",false);
        //查询分组名称是否存在
        String groupId = companyQwDao.getTagGroupIsExist(groupName);
        if (!StringUtils.isEmpty(groupId)){
            return ResultBody.error(-120002,"分组已存在无需创建！！");
        }
        //调用企微接口保存分组 分组下创建一个默认标签（企微接口只有创建标签的）
        Map wxMap = new HashMap();
        wxMap.put("group_name",groupName);
        List<Map> tags = new ArrayList<>();
        Map tag = new HashMap();
        tag.put("name","默认标签");
        tags.add(tag);
        wxMap.put("tag",tags);
        //调用企微接口保存分组
        JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_corp_tag?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
        //如果成功
        if (re!=null && "0".equals(re.getString("errcode"))){
            JSONObject tag_group = re.getJSONObject("tag_group");
            Map groupMap = new HashMap();
            groupMap.put("groupId",tag_group.getString("group_id"));
            groupMap.put("groupName",tag_group.getString("group_name"));
            groupMap.put("creator",SecurityUtils.getUserId());
            //保存标签分组
            companyQwDao.addTagGroup(groupMap);
            //保存默认客户标签
            JSONArray array = tag_group.getJSONArray("tag");
            if (array!=null && array.size()>0){
                for (int i = 0; i < array.size(); i++) {
                    JSONObject tagObject = array.getJSONObject(i);
                    Map tagAddMap = new HashMap();
                    tagAddMap.put("tagId",tagObject.getString("id"));
                    tagAddMap.put("tagName",tagObject.getString("name"));
                    tagAddMap.put("groupId",tag_group.getString("group_id"));
                    tagAddMap.put("groupName",tag_group.getString("group_name"));
                    companyQwDao.addCstTag(tagAddMap);
                }
            }
            return ResultBody.success("分组创建成功！！");
        }else{
            return ResultBody.error(-120002,"调用企微接口创建标签分组失败！！");
        }
    }

    @Override
    public ResultBody getQwCstTagGroup() {
        List<Map> groups = companyQwDao.getQwCstTagGroup();
        return ResultBody.success(groups);
    }

    @Override
    public ResultBody getQwCstTags(HttpServletRequest request, Map map) {

        List<String> groups = new ArrayList<>();
        if (map.get("groupIds")!=null && !"".equals(map.get("groupIds"))){
            String[] ss = map.get("groupIds").toString().split(",");
            if (ss!=null && ss.length>0){
                groups = Arrays.asList(ss);
            }
        }
        map.put("groups",groups);
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<QwCstTag> tagList = companyQwDao.getQwCstTagList(map);
        return ResultBody.success(new PageInfo<>(tagList));
    }

    @Override
    public ResultBody delQwCstTag(HttpServletRequest request, String tagId) {
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"2",false);
        Map wxMap = new HashMap();
        List<String> ids = new ArrayList<>();
        ids.add(tagId);
        wxMap.put("tag_id",ids);
        //调用企微接口删除标签
        JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/del_corp_tag?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
        //如果成功
        if (re!=null && "0".equals(re.getString("errcode"))){
            Map delMap = new HashMap();
            delMap.put("editor",SecurityUtils.getUserId());
            //查询此标签的分组下是否只有一个标签 一个的话标签分组同时删除
            String groupId = companyQwDao.getTagGroupByTagId(tagId);
            if (!StringUtils.isEmpty(groupId)){
                delMap.put("groupId",groupId);
                //删除分组
                companyQwDao.delTagGroup(delMap);
            }
            delMap.put("tagId",tagId);
            //删除标签
            companyQwDao.delCstTag(delMap);
            return ResultBody.success("删除标签成功！");
        }else{
            return ResultBody.error(-120002,"调用企微接口删除标签失败！！");
        }
    }

    @Override
    public ResultBody addOrEditTag(HttpServletRequest request, QwCstTag qwCstTag) {

        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"2",false);
        //判断是新增还是修改
        if ("1".equals(qwCstTag.getAddOrEdit())){
            //新增
            //查询标签名称是否存在
            String tagId = companyQwDao.getTagIsExist(qwCstTag.getTagName(),qwCstTag.getGroupName());
            if (!StringUtils.isEmpty(tagId)){
                return ResultBody.error(-120002,"标签已存在无需创建！！");
            }
            //调用企微接口保存标签
            Map wxMap = new HashMap();
            wxMap.put("group_name",qwCstTag.getGroupName());
            List<Map> tags = new ArrayList<>();
            Map tag = new HashMap();
            tag.put("name",qwCstTag.getTagName());
            tags.add(tag);
            wxMap.put("tag",tags);
            //调用企微接口保存标签
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/add_corp_tag?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
            //如果成功
            if (re!=null && "0".equals(re.getString("errcode"))){
                JSONObject tag_group = re.getJSONObject("tag_group");
                JSONArray array = tag_group.getJSONArray("tag");
                if (array!=null && array.size()>0){
                    JSONObject tago = array.getJSONObject(0);
                    String tagNewId = tago.getString("id");
                    //保存本地标签
                    Map tagMap = new HashMap();
                    tagMap.put("tagId",tagNewId);
                    tagMap.put("tagName",qwCstTag.getTagName());
                    tagMap.put("groupId",qwCstTag.getGroupId());
                    tagMap.put("groupName",qwCstTag.getGroupName());
                    tagMap.put("creator",SecurityUtils.getUserId());
                    companyQwDao.addCstTag(tagMap);
                }
                return ResultBody.success("添加标签成功！");
            }else{
                return ResultBody.error(-120002,"调用企微接口创建标签失败！！");
            }

        }else{
            //修改
            //查询标签名称是否存在
            String tagId = companyQwDao.getTagIsExist(qwCstTag.getTagName(),qwCstTag.getGroupName());
            if (!StringUtils.isEmpty(tagId)){
                return ResultBody.error(-120002,"标签已存在不能修改！！");
            }
            //调用企微接口编辑标签
            Map wxMap = new HashMap();
            wxMap.put("id",qwCstTag.getTagId());
            wxMap.put("name",qwCstTag.getTagName());
            //调用企微接口编辑标签
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/externalcontact/edit_corp_tag?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
            //如果成功
            if (re!=null && "0".equals(re.getString("errcode"))){
                //更新本地标签
                qwCstTag.setEditor(SecurityUtils.getUserId());
                companyQwDao.updateCstTag(qwCstTag);
                return ResultBody.success("编辑标签成功！");
            }else{
                return ResultBody.error(-120002,"调用企微接口编辑标签失败！！");
            }
        }
    }

    @Override
    public ResultBody getQwCstService() {
        List<Map> cstServices = companyQwDao.getQwCstService();
        return ResultBody.success(cstServices);
    }

    @Override
    public ResultBody getQwCstServiceUser(HttpServletRequest request, Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<Map> cstServiceUser = companyQwDao.getQwCstServiceUser(map);
        return ResultBody.success(new PageInfo<>(cstServiceUser));
    }

    @Override
    public ResultBody addOrEditCstService(HttpServletRequest request, Map map) {
        String custServiceName = map.get("custServiceName")+"";
        String avatar = map.get("avatar")+"";
        String openKfid = null;
        if (map.get("openKfid")!=null && !"".equals(map.get("openKfid")+"")){
            openKfid = map.get("openKfid")+"";
        }
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"4",false);
        //调用企微上传临时素材
        // 调用接口获取 media_id
        String url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=" + token + "&type=image";
        String media_id = "";
        String defult = "https://sales.visolink.com/image/";
        String dMap = WeiXinUtil.sendRequest(url, avatar.replaceAll(defult,"/app/netdata/"));
        System.out.println("上传临时素材返回值： " + dMap);

        Map mediaDataMap = JSON.parseObject(dMap, Map.class);
        if (MapUtil.isNotEmpty(mediaDataMap)) {
            if ((Integer) mediaDataMap.get("errcode") == 0) {
                media_id = (String) mediaDataMap.get("media_id");
            }
            else {
                throw new RuntimeException(mediaDataMap.toString());
            }
        }
        //判断是否新增
        if (StringUtils.isEmpty(openKfid)){
            //新增客服
            //调用企微接口新增客服信息
            Map wxMap = new HashMap();
            wxMap.put("name",custServiceName);
            wxMap.put("media_id",media_id);
            //调用企微接口更新客服信息
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/kf/account/add?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
            //如果成功
            if (re!=null && "0".equals(re.getString("errcode"))){
                String openKfidNew = re.getString("open_kfid");
                //新增客服信息
                map.put("creator",SecurityUtils.getUserId());
                map.put("openKfid",openKfidNew);
                companyQwDao.addQwService(map);
                return ResultBody.success("新增客服信息成功！");
            }else{
                return ResultBody.error(-120002,"调用企微接口新增客服信息失败！！");
            }

        }else{
            //更新客服
            //调用企微接口更新客服信息
            Map wxMap = new HashMap();
            wxMap.put("open_kfid",openKfid);
            wxMap.put("name",custServiceName);
            wxMap.put("media_id",media_id);
            //调用企微接口更新客服信息
            JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/kf/account/update?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
            //如果成功
            if (re!=null && "0".equals(re.getString("errcode"))){
                //更新客服信息
                map.put("editor",SecurityUtils.getUserId());
                companyQwDao.updateQwService(map);
                return ResultBody.success("更新客服信息成功！");
            }else{
                return ResultBody.error(-120002,"调用企微接口更新客服信息失败！！");
            }
        }
    }

    @Override
    public ResultBody delServiceUser(HttpServletRequest request, Map map) {
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"4",false);
        List<String> userIds = new ArrayList<>();
        String userid = map.get("userid")+"";
        String openKfid = map.get("openKfid")+"";
        userIds.add(userid);
        //调用企微接口删除客服人员
        Map wxMap = new HashMap();
        wxMap.put("open_kfid",map.get("openKfid")+"");
        wxMap.put("userid_list",userIds);
        //调用企微接口删除客服信息
        JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/kf/servicer/del?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
        //如果成功
        if (re!=null && "0".equals(re.getString("errcode"))){
            //删除本地人员信息
            map.put("editor",SecurityUtils.getUserId());
            map.put("openKfid",openKfid);
            map.put("userid",userid);
            companyQwDao.delQwServiceUser(map);
            return ResultBody.success("删除客服人员信息成功！");
        }else{
            return ResultBody.error(-120002,"调用企微接口删除客服人员信息失败！！");
        }
    }

    @Override
    public ResultBody addServiceUser(HttpServletRequest request, Map map) {
        String companycode = request.getHeader("companycode");
        String token = this.getWxToken(companycode,"4",false);
        String openKfid = map.get("openKfid")+"";
        String projectId = map.get("projectId")+"";
        List<String> ids = new ArrayList<>();
        List<String> userIds = Arrays.asList(map.get("userids").toString().split(","));
        map.put("userIdList",userIds);
        ids.addAll(userIds);
        //查询原客服人员信息
        List<String> userOlds = companyQwDao.getOldUsers(map);
        if (userOlds!=null && userOlds.size()>0){
            for (int i = 0;i<ids.size();i++) {
                String id = ids.get(i);
                if (userOlds.contains(id)){
                    ids.remove(id);
                    i--;
                }
            }
        }
        if (ids.size()>100){
            return ResultBody.error(-120002,"新增客服人员最多添加100人！！");
        }
        //调用企微接口新增客服人员
        Map wxMap = new HashMap();
        wxMap.put("open_kfid",map.get("openKfid")+"");
        wxMap.put("userid_list",ids);
        //调用企微接口新增客服信息
        JSONObject re = HttpRequestUtil.httpPost("https://qyapi.weixin.qq.com/cgi-bin/kf/servicer/add?access_token="+token,JSONObject.parseObject(JSONObject.toJSONString(wxMap)),false);
        //如果成功
        if (re!=null && "0".equals(re.getString("errcode"))){
            //新增本地人员信息
            List<Map> maps = new ArrayList<>();
            for (String dd:ids) {
                Map addMap = new HashMap();
                addMap.put("creator",SecurityUtils.getUserId());
                addMap.put("openKfid",openKfid);
                addMap.put("userid",dd);
                addMap.put("projectId",projectId);
                maps.add(addMap);
            }
            companyQwDao.addQwServiceUser(maps);
            return ResultBody.success("新增客服人员信息成功！");
        }else{
            return ResultBody.error(-120002,"调用企微接口新增客服人员信息失败！！");
        }
    }

    @Override
    public ResultBody getQwServiceAutoReply(HttpServletRequest request, Map map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        if (map.get("projectIds")!=null && !"".equals(map.get("projectIds")+"")){
            String[] ids = map.get("projectIds").toString().split(",");
            map.put("ids",Arrays.asList(ids));
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<QwServiceAutoReply> qwServiceAutoReplys = companyQwDao.getQwServiceAutoReply(map);
        return ResultBody.success(new PageInfo<>(qwServiceAutoReplys));
    }

    @Override
    public ResultBody delAutoReply(HttpServletRequest request, Map map) {
        //逻辑删除自动回复规则
        map.put("editor",SecurityUtils.getUserId());
        companyQwDao.delAutoReply(map);
        return ResultBody.success("删除成功！");
    }

    @Override
    public ResultBody addOrEditAutoReply(HttpServletRequest request, QwServiceAutoReply qwServiceAutoReply) {
        List<QwServiceAutoReplyKeyWord> keyWordList = qwServiceAutoReply.getKeyWordList();
        if (keyWordList==null || keyWordList.size()==0){
            return ResultBody.error(-120002,"关键词未配置！！");
        }
        List<String> proids = qwServiceAutoReply.getProjectIdList();
        if (proids==null || proids.size()==0){
            return ResultBody.error(-120002,"项目未选择！！");
        }
        List<String> relList = qwServiceAutoReply.getRelList();
        if (relList==null || relList.size()==0){
            return ResultBody.error(-120002,"客服未选择！！");
        }
        List<String> errorKey = new ArrayList<>();
        //判断关键字是否可配置
        for (int i = 0;i< keyWordList.size();i++) {
            String key = keyWordList.get(i).getKeyWord();
            //查询关键字是否存在
            Map keyMap = new HashMap();
            keyMap.put("key",key);
            keyMap.put("relList",relList);
            keyMap.put("ruleId",qwServiceAutoReply.getId());
            int count = companyQwDao.getAutoReplyKeyWordIsOk(keyMap);
            if (count>0){
                errorKey.add(key);
                keyWordList.remove(keyWordList.get(i));
                i--;
            }
        }
        if (keyWordList.size()==0){
            return ResultBody.error(-120002,"此次配置的关键字都已配置过请确认后重试！！");
        }else{
            if (errorKey.size()>0){
                String error = StringUtils.join(errorKey,",");
                return ResultBody.error(-120002,"此次配置的关键字中:"+error+"已配置过请确认后重试！！");
            }
        }

        //判断新增还是修改
        if ("1".equals(qwServiceAutoReply.getAddOrEdit())){
            //新增
            //保存规则表
            qwServiceAutoReply.setCreator(SecurityUtils.getUserId());
            companyQwDao.addAutoReply(qwServiceAutoReply);
            String ruleId = qwServiceAutoReply.getId();
            //保存关联项目
            List<Map> proMaps = new ArrayList<>();
            for (String proId:proids) {
                Map map = new HashMap();
                map.put("rule_id",ruleId);
                map.put("project_id",proId);
                map.put("creator",SecurityUtils.getUserId());
                proMaps.add(map);
            }
            companyQwDao.addAutoReplyPro(proMaps);
            //保存关联客服
            List<Map> relMaps = new ArrayList<>();
            for (String relId:relList) {
                Map map = new HashMap();
                map.put("rule_id",ruleId);
                map.put("open_kfid",relId);
                map.put("creator",SecurityUtils.getUserId());
                relMaps.add(map);
            }
            companyQwDao.addAutoReplyRel(relMaps);
            //保存关键词
            for (QwServiceAutoReplyKeyWord keyWord:keyWordList) {
                keyWord.setCreator(SecurityUtils.getUserId());
                keyWord.setRuleId(ruleId);
            }
            companyQwDao.addAutoReplyKeyWord(keyWordList);
            //保存素材
            List<QwServiceAutoReplyMedia> allList = new ArrayList<>();
            List<QwServiceAutoReplyMedia> photoList = qwServiceAutoReply.getPhotoList();
            List<QwServiceAutoReplyMedia> videoList = qwServiceAutoReply.getVideoList();
            List<QwServiceAutoReplyMedia> h5List = qwServiceAutoReply.getH5List();
            List<QwServiceAutoReplyMedia> appletList = qwServiceAutoReply.getAppletList();
            List<QwServiceAutoReplyMedia> fileList = qwServiceAutoReply.getFileList();
            List<QwServiceAutoReplyMedia> textList = qwServiceAutoReply.getTextList();
            if (photoList!=null && photoList.size()>0){
                for (QwServiceAutoReplyMedia me:photoList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("1");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(photoList);
            }
            if (videoList!=null && videoList.size()>0){
                for (QwServiceAutoReplyMedia me:videoList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("2");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(videoList);
            }
            if (h5List!=null && h5List.size()>0){
                for (QwServiceAutoReplyMedia me:h5List) {
                    me.setRuleId(ruleId);
                    me.setMediaType("4");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(h5List);
            }
            if (appletList!=null && appletList.size()>0){
                for (QwServiceAutoReplyMedia me:appletList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("3");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(appletList);
            }
            if (fileList!=null && fileList.size()>0){
                for (QwServiceAutoReplyMedia me:fileList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("5");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(fileList);
            }
            if (textList!=null && textList.size()>0){
                for (QwServiceAutoReplyMedia me:textList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("6");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(textList);
            }
            if (allList.size()>0){
                companyQwDao.addAutoReplyMedia(allList);
            }

        }else{
            //修改
            //更新规则表
            qwServiceAutoReply.setEditor(SecurityUtils.getUserId());
            companyQwDao.updateAutoReply(qwServiceAutoReply);
            String ruleId = qwServiceAutoReply.getId();
            //删除原来关联的项目数据
            companyQwDao.delAutoReplyPro(ruleId);
            //保存关联项目
            List<Map> proMaps = new ArrayList<>();
            for (String proId:proids) {
                Map map = new HashMap();
                map.put("rule_id",ruleId);
                map.put("project_id",proId);
                map.put("creator",SecurityUtils.getUserId());
                proMaps.add(map);
            }
            companyQwDao.addAutoReplyPro(proMaps);
            //删除原来关联的客服
            companyQwDao.delAutoReplyRel(ruleId);
            //保存关联客服
            List<Map> relMaps = new ArrayList<>();
            for (String relId:relList) {
                Map map = new HashMap();
                map.put("rule_id",ruleId);
                map.put("open_kfid",relId);
                map.put("creator",SecurityUtils.getUserId());
                relMaps.add(map);
            }
            companyQwDao.addAutoReplyRel(relMaps);
            //删除原来关键词
            companyQwDao.delAutoReplyKeyWord(ruleId);
            //保存关键词
            for (QwServiceAutoReplyKeyWord keyWord:keyWordList) {
                keyWord.setCreator(SecurityUtils.getUserId());
                keyWord.setRuleId(ruleId);
            }
            companyQwDao.addAutoReplyKeyWord(keyWordList);
            //删除原素材
            companyQwDao.delOldAutoReplyMedia(ruleId);
            //保存素材
            List<QwServiceAutoReplyMedia> allList = new ArrayList<>();
            List<QwServiceAutoReplyMedia> photoList = qwServiceAutoReply.getPhotoList();
            List<QwServiceAutoReplyMedia> videoList = qwServiceAutoReply.getVideoList();
            List<QwServiceAutoReplyMedia> h5List = qwServiceAutoReply.getH5List();
            List<QwServiceAutoReplyMedia> appletList = qwServiceAutoReply.getAppletList();
            List<QwServiceAutoReplyMedia> fileList = qwServiceAutoReply.getFileList();
            List<QwServiceAutoReplyMedia> textList = qwServiceAutoReply.getTextList();
            if (photoList!=null && photoList.size()>0){
                for (QwServiceAutoReplyMedia me:photoList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("1");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(photoList);
            }
            if (videoList!=null && videoList.size()>0){
                for (QwServiceAutoReplyMedia me:videoList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("2");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(videoList);
            }
            if (h5List!=null && h5List.size()>0){
                for (QwServiceAutoReplyMedia me:h5List) {
                    me.setRuleId(ruleId);
                    me.setMediaType("4");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(h5List);
            }
            if (appletList!=null && appletList.size()>0){
                for (QwServiceAutoReplyMedia me:appletList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("3");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(appletList);
            }
            if (fileList!=null && fileList.size()>0){
                for (QwServiceAutoReplyMedia me:fileList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("5");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(fileList);
            }
            if (textList!=null && textList.size()>0){
                for (QwServiceAutoReplyMedia me:textList) {
                    me.setRuleId(ruleId);
                    me.setMediaType("6");
                    me.setCreator(SecurityUtils.getUserId());
                }
                allList.addAll(textList);
            }
            if (allList.size()>0){
                companyQwDao.addAutoReplyMedia(allList);
            }
        }
        return ResultBody.success("编辑成功！！");
    }

    @Override
    public ResultBody getAutoReply(HttpServletRequest request, Map map) {
        String ruleId = map.get("ruleId")+"";
        //获取规则信息
        QwServiceAutoReply qwServiceAutoReply = companyQwDao.getAutoReply(ruleId);
        //获取规则项目
        List<String> proIds = companyQwDao.getAutoReplyPro(ruleId);
        qwServiceAutoReply.setProjectIdList(proIds);
        //获取规则关键字
        List<QwServiceAutoReplyKeyWord> keywords = companyQwDao.getAutoReplyKeyWord(ruleId);
        qwServiceAutoReply.setKeyWordList(keywords);
        //获取规则客服
        List<String> openKfids = companyQwDao.getAutoReplyRel(ruleId);
        qwServiceAutoReply.setRelList(openKfids);
        //获取规则素材
        List<QwServiceAutoReplyMedia> allList = companyQwDao.getOldAutoReplyMedia(ruleId);
        if (allList!=null && allList.size()>0){
            List<QwServiceAutoReplyMedia> photoList = new ArrayList<>();
            List<QwServiceAutoReplyMedia> videoList = new ArrayList<>();
            List<QwServiceAutoReplyMedia> h5List = new ArrayList<>();
            List<QwServiceAutoReplyMedia> appletList = new ArrayList<>();
            List<QwServiceAutoReplyMedia> fileList = new ArrayList<>();
            List<QwServiceAutoReplyMedia> textList = new ArrayList<>();
            for (QwServiceAutoReplyMedia qwServiceAutoReplyMedia:allList) {
                if ("1".equals(qwServiceAutoReplyMedia.getMediaType())){
                    photoList.add(qwServiceAutoReplyMedia);
                }else if ("2".equals(qwServiceAutoReplyMedia.getMediaType())){
                    videoList.add(qwServiceAutoReplyMedia);
                }else if ("3".equals(qwServiceAutoReplyMedia.getMediaType())){
                    appletList.add(qwServiceAutoReplyMedia);
                }else if ("4".equals(qwServiceAutoReplyMedia.getMediaType())){
                    h5List.add(qwServiceAutoReplyMedia);
                }else if ("5".equals(qwServiceAutoReplyMedia.getMediaType())){
                    fileList.add(qwServiceAutoReplyMedia);
                }else if ("6".equals(qwServiceAutoReplyMedia.getMediaType())){
                    textList.add(qwServiceAutoReplyMedia);
                }
            }
            qwServiceAutoReply.setPhotoList(photoList);
            qwServiceAutoReply.setVideoList(videoList);
            qwServiceAutoReply.setAppletList(appletList);
            qwServiceAutoReply.setH5List(h5List);
            qwServiceAutoReply.setFileList(fileList);
            qwServiceAutoReply.setTextList(textList);
        }
        return ResultBody.success(qwServiceAutoReply);
    }

    @Override
    public ResultBody getChannelCodeStatistics(HttpServletRequest request, Map map) {
        String[] proids = map.get("projectIds").toString().split(",");
        map.put("ids",proids);
        Map result = new HashMap();
        //获取统计数据
        //获取客户总数
        int sum = companyQwDao.getChannelCodeCstSum(map);
        //获取新增客户数
        int newCount = companyQwDao.getChannelCodeCstNewCount(map);
        //获取流失客户数
        int delCount = companyQwDao.getChannelCodeCstDelCount(map);
        result.put("sum",sum);
        result.put("newCount",newCount);
        result.put("delCount",delCount);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody getChannelCodeUserDetail(HttpServletRequest request, Map map) {
        String[] proids = map.get("projectIds").toString().split(",");
        map.put("ids",proids);
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }
        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<ChannelCodeUserDetail> channelCodeUserDetails = companyQwDao.getChannelCodeUserDetail(map);
        return ResultBody.success(new PageInfo<>(channelCodeUserDetails));
    }

    @Override
    public void channelCodeUserDetailExport(HttpServletRequest request, HttpServletResponse response, String param) {
        ArrayList<Object[]> dataset = new ArrayList<>();
        Map map = JSONObject.parseObject(param,Map.class);
        String[] proids = map.get("projectIds").toString().split(",");
        map.put("ids",proids);
        try {
            //查询数据
            List<ChannelCodeUserDetail> channelCodeUserDetails = companyQwDao.getChannelCodeUserDetail(map);
            if (channelCodeUserDetails.size()>0){
                String[] headers = channelCodeUserDetails.get(0).getChannelCodeTitle();
                for (int i = 0; i < channelCodeUserDetails.size(); i++) {
                    ChannelCodeUserDetail activityInfoVO = channelCodeUserDetails.get(i);
                    activityInfoVO.setRownum((i+1)+"");
                    Object[] oArray = activityInfoVO.toChannelCodeData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("渠道码成员客户", headers,dataset, "渠道码成员客户", response,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultBody getChannelCodeLineChart(HttpServletRequest request, Map map) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] proids = map.get("projectIds").toString().split(",");
        map.put("ids",proids);
        String type = map.get("type")+"";
        //时间集合
        List timeList = new LinkedList();
        //数据集合
        List numberList = new LinkedList();
        String times = null;
        try{
            if (map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
                times = sf.format(sf.parse(map.get("endTime")+""));
            }else{
                times = sf.format(new Date());
            }
            //获取时间
            Date time = DateUtil.parse(times);
            for (int i = 0; i < 7; i++) {
                //开始时间
                Date beginTime = DateUtil.beginOfDay(time);
                //结束时间
                Date endTime = DateUtil.endOfDay(time);
                map.put("startTime", sf1.format(beginTime));
                map.put("endTime", sf1.format(endTime));
                String format = DateUtil.format(time, "yyyy/MM/dd");
                timeList.add(i, format);
                int count = 0;
                //判断查询类型（1：总数（默认） 2：新增数 3：流失数）
                if ("1".equals(type)){
                    count = companyQwDao.getSumChannelCodeLine(map);
                }else if("2".equals(type)){
                    count = companyQwDao.getChannelCodeCstNewCount(map);
                }else{
                    count = companyQwDao.getChannelCodeCstDelCount(map);
                }
                numberList.add(i, count);
                time = DateUtil.offsetDay(time, -1);
            }
            Collections.reverse(timeList);
            Collections.reverse(numberList);
            Map hashMap = MapUtil.newHashMap();
            hashMap.put("timeList", timeList);
            hashMap.put("numberList", numberList);
            return ResultBody.success(hashMap);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-122002,"异常！！");
        }
    }

    public String getWxToken(String companycode,String type,boolean flag){
        //type  分类 ：1通讯录token  2客户联系token 3应用 token
        //flag  true: 获取实时token  false: 读取缓存（默认）
        String token = "";
        if (flag){
            HttpRequestUtil.httpGet(wxTokenPath,false);
        }
        if ("1".equals(type)){
            token = redisUtil.get("QW_DATATOKEN_"+companycode).toString();
        }else if ("2".equals(type)){
            token = redisUtil.get("QW_CSTTOKEN_"+companycode).toString();
        }else if ("3".equals(type)){
            token = redisUtil.get("QW_APPTOKEN_"+companycode).toString();
        }else if ("4".equals(type)){
            token = redisUtil.get("QW_SERVICETOKEN_"+companycode).toString();
        }
        return token;
    }

}
