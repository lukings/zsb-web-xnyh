package cn.visolink.system.companyQw.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.companyQw.dao.CompanyQwMassTextingDao;
import cn.visolink.system.companyQw.model.BQwMassTexting;
import cn.visolink.system.companyQw.model.MediaDetail;
import cn.visolink.system.companyQw.service.CompanyQwMassTextingService;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: Mr.Yu
 * @Date: 2022/1/5 16:41
 * @description
 * @Version 1.0
 */
@Service
public class CompanyQwMassTextingServiceImpl implements CompanyQwMassTextingService {

    @Autowired
    private CompanyQwMassTextingDao companyQwMassTextingDao;

    /**
     * 创建群发任务
     *
     * @param bQwMassTexting
     * @param request
     * @return
     */
    @Override
    public ResultBody createMassTextingTasks(BQwMassTexting bQwMassTexting, HttpServletRequest request) {
        Map<String, Object> retrunMap = new HashMap<>();
        // 主键id
        String id = UUID.randomUUID().toString().replace("-", "");
        bQwMassTexting.setId(id);
        bQwMassTexting.setCreater(SecurityUtils.getUserId());
        if (bQwMassTexting.getSenderList()==null || bQwMassTexting.getSenderList().size()==0){
            return ResultBody.error(-120002,"成员未选择！");
        }
        // 判断是群发到客户群还是群发到客户(0：群发到客户；1：群发到客户群 2：群发到朋友圈)
        if ("0".equals(bQwMassTexting.getSendType())) {
            //插询成员客户  (1:全部客户  2：按条件查询)
            Map param = new HashMap();
            param.put("userIds",bQwMassTexting.getSenderList());
            List<String> cstIds = new ArrayList<>();
            if ("2".equals(bQwMassTexting.getCstType())){
                //是否查询未关联的客户
                boolean falg = false;
                //按条件查询
                if (bQwMassTexting.getFlagIdList()!=null && bQwMassTexting.getFlagIdList().size()>0){
                    param.put("tagIds",bQwMassTexting.getFlagIdList());
                }
                if (bQwMassTexting.getCstStatus()!=null && bQwMassTexting.getCstStatus().size()>0){
                    param.put("projectId",bQwMassTexting.getProjectId());
                    List<String> status = new ArrayList<>();
                    for (String ss:bQwMassTexting.getCstStatus()) {
                        if (ss.equals("0")){
                            falg = true;
                        }else{
                            if (ss.equals("3")){
                                status.add("3");
                                status.add("4");
                            }else{
                                status.add(ss);
                            }
                        }
                    }
                    param.put("status",status);
                    List<String> acIds = new ArrayList<>();
                    //查询关联案场的客户
                    acIds = companyQwMassTextingDao.getUserCstByType(param);
                    List<String> idss = new ArrayList<>();
                    if (falg){
                        idss = companyQwMassTextingDao.getUserCstNoAc(param);
                    }
                    if (acIds!=null && acIds.size()>0){
                        if (idss!=null && idss.size()>0){
                            acIds.addAll(idss);
                        }
                    }else{
                        if (idss!=null && idss.size()>0){
                            acIds = idss;
                        }else{
                            return ResultBody.error(-120002,"没有客户符合要求！");
                        }
                    }
                    cstIds = acIds;
                }else{
                    //查询标签下客户
                    cstIds = companyQwMassTextingDao.getUserCstNoAc(param);
                }
            }else{
                cstIds = companyQwMassTextingDao.getUserCstNoAc(param);
            }

            bQwMassTexting.setSenders(StringUtils.join(bQwMassTexting.getSenderList(),","));
            //查询所有成员姓名
            List<String> senderNames = companyQwMassTextingDao.getUserNames(bQwMassTexting.getSenderList());
            //查询所有客户姓名
            List<String> colClientNames = companyQwMassTextingDao.getCstNames(cstIds);
            bQwMassTexting.setColClientId(StringUtils.join(cstIds,","));
            bQwMassTexting.setSenderNames(StringUtils.join(senderNames,","));
            bQwMassTexting.setColClientNames(StringUtils.join(colClientNames,","));
            companyQwMassTextingDao.createMassTextingTasks(bQwMassTexting);

            List<MediaDetail> mediaDetails = new ArrayList<>();
            List<MediaDetail> photoList = bQwMassTexting.getPhotoList();
            List<MediaDetail> h5List = bQwMassTexting.getH5List();
            List<MediaDetail> appletList = bQwMassTexting.getAppletList();
            List<MediaDetail> textList = bQwMassTexting.getTextList();

            if (photoList!=null && photoList.size()>0){
                for (MediaDetail me:photoList) {
                    me.setTaskId(id);
                    me.setMediaType("1");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(photoList);
            }

            if (h5List!=null && h5List.size()>0){
                for (MediaDetail me:h5List) {
                    me.setTaskId(id);
                    me.setMediaType("4");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(h5List);
            }
            if (appletList!=null && appletList.size()>0){
                for (MediaDetail me:appletList) {
                    me.setTaskId(id);
                    me.setMediaType("3");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(appletList);
            }

            if (textList!=null && textList.size()>0){
                for (MediaDetail me:textList) {
                    me.setTaskId(id);
                    me.setMediaType("6");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(textList);
            }
            //保存任务素材
            if (mediaDetails.size()>0){
                companyQwMassTextingDao.addTaskMediaList(mediaDetails);
            }
            retrunMap.put("code", 200);
            retrunMap.put("message", "保存成功");
        }
        // 1：群发到客户群
        else if ("1".equals(bQwMassTexting.getSendType())){
            bQwMassTexting.setSenders(StringUtils.join(bQwMassTexting.getSenderList(),","));
            //查询所有成员姓名
            List<String> senderNames = companyQwMassTextingDao.getUserNames(bQwMassTexting.getSenderList());
            bQwMassTexting.setSenderNames(StringUtils.join(senderNames,","));

            companyQwMassTextingDao.createMassTextingTasks(bQwMassTexting);
            List<MediaDetail> mediaDetails = new ArrayList<>();
            List<MediaDetail> photoList = bQwMassTexting.getPhotoList();
            List<MediaDetail> h5List = bQwMassTexting.getH5List();
            List<MediaDetail> appletList = bQwMassTexting.getAppletList();
            List<MediaDetail> textList = bQwMassTexting.getTextList();
            if (photoList!=null && photoList.size()>0){
                for (MediaDetail me:photoList) {
                    me.setTaskId(id);
                    me.setMediaType("1");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(photoList);
            }

            if (h5List!=null && h5List.size()>0){
                for (MediaDetail me:h5List) {
                    me.setTaskId(id);
                    me.setMediaType("4");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(h5List);
            }
            if (appletList!=null && appletList.size()>0){
                for (MediaDetail me:appletList) {
                    me.setTaskId(id);
                    me.setMediaType("3");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(appletList);
            }

            if (textList!=null && textList.size()>0){
                for (MediaDetail me:textList) {
                    me.setTaskId(id);
                    me.setMediaType("6");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(textList);
            }
            //保存任务素材
            if (mediaDetails.size()>0){
                companyQwMassTextingDao.addTaskMediaList(mediaDetails);
            }
            retrunMap.put("code", 200);
            retrunMap.put("message", "保存成功");
        }else{
            //群发到朋友圈
            bQwMassTexting.setSenders(StringUtils.join(bQwMassTexting.getSenderList(),","));
            //查询所有成员姓名
            List<String> senderNames = companyQwMassTextingDao.getUserNames(bQwMassTexting.getSenderList());
            bQwMassTexting.setSenderNames(StringUtils.join(senderNames,","));
            //插询成员客户  (1:全部客户  2：按条件查询)
            Map param = new HashMap();
            param.put("userIds",bQwMassTexting.getSenderList());
            List<String> cstIds = new ArrayList<>();
            cstIds = companyQwMassTextingDao.getUserCstNoAc(param);
            //查询所有客户姓名
            List<String> colClientNames = companyQwMassTextingDao.getCstNames(cstIds);
            bQwMassTexting.setColClientId(StringUtils.join(cstIds,","));
            bQwMassTexting.setColClientNames(StringUtils.join(colClientNames,","));
            companyQwMassTextingDao.createMassTextingTasks(bQwMassTexting);

            List<MediaDetail> mediaDetails = new ArrayList<>();
            List<MediaDetail> videoList = bQwMassTexting.getVideoList();
            List<MediaDetail> photoList = bQwMassTexting.getPhotoList();
            List<MediaDetail> h5List = bQwMassTexting.getH5List();
            List<MediaDetail> appletList = bQwMassTexting.getAppletList();
            List<MediaDetail> textList = bQwMassTexting.getTextList();
            if (photoList!=null && photoList.size()>0){
                for (MediaDetail me:photoList) {
                    me.setTaskId(id);
                    me.setMediaType("1");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(photoList);
            }
            if (videoList!=null && videoList.size()>0){
                for (MediaDetail me:videoList) {
                    me.setTaskId(id);
                    me.setMediaType("2");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(videoList);
            }

            if (h5List!=null && h5List.size()>0){
                for (MediaDetail me:h5List) {
                    me.setTaskId(id);
                    me.setMediaType("4");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(h5List);
            }
            if (appletList!=null && appletList.size()>0){
                for (MediaDetail me:appletList) {
                    me.setTaskId(id);
                    me.setMediaType("3");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(appletList);
            }

            if (textList!=null && textList.size()>0){
                for (MediaDetail me:textList) {
                    me.setTaskId(id);
                    me.setMediaType("6");
                    me.setCreator(SecurityUtils.getUserId());
                }
                mediaDetails.addAll(textList);
            }
            //保存任务素材
            if (mediaDetails.size()>0){
                companyQwMassTextingDao.addTaskMediaList(mediaDetails);
            }
            retrunMap.put("code", 200);
            retrunMap.put("message", "保存成功");

        }
        return ResultBody.success(retrunMap);
    }

    /**
     * 查看群发任务列表
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody selectMassTextingTasks(Map map) {
        if (MapUtil.isEmpty(map)) {
            return ResultBody.error(-20001, "请求参数为空！");
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
        PageHelper.startPage(pageIndex, pageSize);

        // 获取列表
        List<BQwMassTexting> qwMassTextingList = companyQwMassTextingDao.selectMassTextingTasks(map);
        return ResultBody.success(new PageInfo<>(qwMassTextingList));

    }

    @Override
    public ResultBody getUserCstCount(BQwMassTexting bQwMassTexting, HttpServletRequest request) {
        if (bQwMassTexting.getSenderList()==null || bQwMassTexting.getSenderList().size()==0){
            return ResultBody.error(-120002,"成员未选择！");
        }
        //插询成员客户  (1:全部客户  2：按条件查询)
        Map param = new HashMap();
        param.put("userIds",bQwMassTexting.getSenderList());
        List<String> cstIds = new ArrayList<>();
        if ("2".equals(bQwMassTexting.getCstType())){
            //是否查询未关联的客户
            boolean falg = false;
            //按条件查询
            if (bQwMassTexting.getFlagIdList()!=null && bQwMassTexting.getFlagIdList().size()>0){
                param.put("tagIds",bQwMassTexting.getFlagIdList());
            }
            if (bQwMassTexting.getCstStatus()!=null && bQwMassTexting.getCstStatus().size()>0){
                param.put("projectId",bQwMassTexting.getProjectId());
                List<String> status = new ArrayList<>();
                for (String ss:bQwMassTexting.getCstStatus()) {
                    if (ss.equals("0")){
                        falg = true;
                    }else{
                        if (ss.equals("3")){
                            status.add("3");
                            status.add("4");
                        }else{
                            status.add(ss);
                        }
                    }
                }
                param.put("status",status);
                List<String> acIds = new ArrayList<>();
                //查询关联案场的客户
                acIds = companyQwMassTextingDao.getUserCstByType(param);
                List<String> idss = new ArrayList<>();
                if (falg){
                    idss = companyQwMassTextingDao.getUserCstNoAc(param);
                }
                if (acIds!=null && acIds.size()>0){
                    if (idss!=null && idss.size()>0){
                        acIds.addAll(idss);
                    }
                }else{
                    if (idss!=null && idss.size()>0){
                        acIds = idss;
                    }
                }
                cstIds = acIds;
            }else{
                //查询标签下客户
                cstIds = companyQwMassTextingDao.getUserCstNoAc(param);
            }
        }else{
            cstIds = companyQwMassTextingDao.getUserCstNoAc(param);
        }
        return ResultBody.success(cstIds==null?0:cstIds.size());
    }

    @Override
    public ResultBody getUserCstTag(BQwMassTexting bQwMassTexting, HttpServletRequest request) {
        if (bQwMassTexting.getSenderList()==null || bQwMassTexting.getSenderList().size()==0){
            return ResultBody.error(-120002,"成员未选择！");
        }
        List<Map> tags = companyQwMassTextingDao.getUserCstTags(bQwMassTexting.getSenderList());
        return ResultBody.success(tags);
    }

    @Override
    public ResultBody delMassTextingTasks(Map map) {
        companyQwMassTextingDao.deleteById(map.get("id")+"");
        return ResultBody.success("删除成功！！");
    }

    @Override
    public ResultBody reMassTextingTasks(Map map) {
        companyQwMassTextingDao.reMassTextingTasks(map.get("id")+"");
        return ResultBody.success("撤回成功！！");
    }

    @Override
    public ResultBody reAddMassTextingTasks(Map map) {
        //获取当前时间后5分钟
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, 5);
        String sendTime = sdf.format(nowTime.getTime());
        map.put("sendTime",sendTime);
        companyQwMassTextingDao.reAddMassTextingTasks(map);
        return ResultBody.success("重发设置成功，稍等几分钟即自动发送！！");
    }


}

