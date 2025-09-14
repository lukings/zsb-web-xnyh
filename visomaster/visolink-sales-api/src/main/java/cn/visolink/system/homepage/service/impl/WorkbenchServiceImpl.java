package cn.visolink.system.homepage.service.impl;

import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.message.model.form.MessageForm;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.model.form.UserOrgRelForm;
import cn.visolink.system.channel.service.ProjectCluesService;
import cn.visolink.system.custMap.dao.CustMapDao;
import cn.visolink.system.homepage.dao.WorkbenchMapper;
import cn.visolink.system.homepage.service.WorkbenchService;
import cn.visolink.system.org.model.vo.OrganizationVO;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkbenchServiceImpl implements WorkbenchService {

    @Autowired
    private WorkbenchMapper workbenchMapper;

    @Autowired
    private CustMapDao custMapDao;

    @Autowired
    private ProjectCluesDao projectCluesDao;

    @Autowired
    private ProjectCluesService projectCluesService;
    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public ResultBody getPendingList(Map map) {
        String type = map.get("type")+"";
        int pageNum = 1;
        int pageSize = 10;
        if (map.get("pageNum") != null && !"".equals(map.get("pageNum")+"")) {
            pageNum = Integer.parseInt(map.get("pageNum")+"");
        }
        if (map.get("pageSize") != null && !"".equals(map.get("pageSize")+"")) {
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        map.put("pageNum","");
        map.put("pageSize","");
        map.put("userId", SecurityUtils.getUserId());
        map.put("userName", SecurityUtils.getUsername());
        //获取登录人的权限
        List<Map> fullPath = workbenchMapper.findUserJobAllInfo(map);
        if(CollectionUtils.isEmpty(fullPath)){
            throw new BadRequestException(-10_0000,"用户无权限！");
        }
        List<String> userIds = new ArrayList<>();//项目专员
        List<String> qyUserIds = new ArrayList<>();//区域专员
        List<String> zyProList = new ArrayList<>();//专员
        List<String> qyProList = new ArrayList<>();//区域
        List<String> jlProList = new ArrayList<>();//经理
        List<String> qyjlProList = new ArrayList<>();//区域经理
        List<String> yxProList = new ArrayList<>();//营销
        List<String> zjProList = new ArrayList<>();//总监
        List<String> qyZjProList = new ArrayList<>();//总监
        fullPath.stream().forEach(x->{
            if("zygw".equals(x.get("jobCode")+"")){
                //转介审批 专员
                zyProList.add(x.get("projectId")+"");
            }else if("qyzygw".equals(x.get("jobCode")+"")){
                //转介审批 区域专员
                qyProList.add(x.get("projectId")+"");
            }else if("xsjl".equals(x.get("jobCode")+"")){
                //跟进审批 项目经理 获取团队下专员
                userIds.addAll(custMapDao.getTeamUser(x.get("orgId")+"","zygw"));
                jlProList.add(x.get("projectId")+"");
            }else if("qyxsjl".equals(x.get("jobCode")+"")){
                //跟进审批 区域经理 获取团队下专员
                qyUserIds.addAll(custMapDao.getTeamUser(x.get("orgId")+"","qyzygw"));
                qyjlProList.add(x.get("projectId")+"");
            }else if("yxjl".equals(x.get("jobCode")+"")){
                //跟进审批 转介审批 营销
                yxProList.add(x.get("projectId")+"");
            }else if("zszj".equals(x.get("jobCode")+"")){
                //跟进审批 转介审批 公客池捞取审批 总监
                zjProList.add(x.get("projectId")+"");
            }else if("qyzszj".equals(x.get("jobCode")+"")){
                //公客池捞取审批 区域总监
                qyZjProList.add(x.get("projectId")+"");
            }
        });
        map.put("userIds",userIds);
        map.put("qyUserIds",qyUserIds);
        map.put("zyProList",zyProList);
        map.put("qyProList",qyProList);
        map.put("jlProList",jlProList);
        map.put("qyjlProList",qyjlProList);
        map.put("yxProList",yxProList);
        map.put("zjProList",zjProList);
        map.put("qyZjProList",qyZjProList);
        List<Map> list = new ArrayList<>();
        List<Map> listclue = new ArrayList<>();

        List<String> orgIds = getOrgIds(SecurityUtils.getUserId(), null);
        if (CollectionUtils.isEmpty(orgIds)) {
            return ResultBody.success(new PageInfo<>());
        }
        map.put("orgIds", orgIds);
        PageHelper.startPage(pageNum, pageSize);
        if("1".equals(type)){
            list = workbenchMapper.getPendingList(map);
            listclue = workbenchMapper.getPendingListClue(map);
            list.addAll(listclue);
        }else {
            list = workbenchMapper.getPendingOkList(map);
            listclue = workbenchMapper.getPendingOkListClue(map);
            list.addAll(listclue);
        }
        return ResultBody.success(new PageInfo<>(list));
    }

    /**
     * 查询申请权限的项目但未申请权限但招商组
     * @param userId
     * @param proList
     * @return
     */
    @Override
    public List<String> linkOrgIds(String userId, List<String> proList) {
        UserOrgRelForm userOrgRelForm = new UserOrgRelForm();
        userOrgRelForm.setUserId(userId);
        userOrgRelForm.setProList(proList);
        List<UserOrgRelForm> userOrgRelFormList = projectCluesDao.getAdminDataViewPremissionApprove(userOrgRelForm);
        if (CollectionUtils.isEmpty(userOrgRelFormList)) {
            return null;
        }
        List<String> projectList = userOrgRelFormList.stream().map(UserOrgRelForm::getProjectId).distinct().collect(
            Collectors.toList());
        List<OrganizationVO> OrganizationVOs = projectCluesDao.getProOrgIds(projectList);
        //按项目分组，排除已申请权限，剩余的即为无权限的项目
        List<String> ids = OrganizationVOs.stream().map(OrganizationVO::getId).collect(Collectors.toList());
        //已配权限的ID
        List<String> orgIds = new ArrayList<>();
        for (UserOrgRelForm orgRelForm : userOrgRelFormList) {
            if (org.springframework.util.StringUtils.isEmpty(orgRelForm.getOrgId())) {
                continue;
            }
            List<String> orgs = Arrays.asList(orgRelForm.getOrgId().split(","));
            orgIds.addAll(orgs);
            orgIds = orgIds.stream().distinct().collect(Collectors.toList());
        }
        Iterator<String> iterator = ids.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (orgIds.contains(next)) {
                iterator.remove();
            }
        }
        return ids;
    }

    /**
     * 获取申请的权限
     * @param userId
     * @param proList
     * @return
     */
    private List<String> getOrgIds(String userId, List<String> proList) {
        UserOrgRelForm userOrgRelForm = new UserOrgRelForm();
        userOrgRelForm.setUserId(userId);
        userOrgRelForm.setProList(proList);
        List<UserOrgRelForm> userOrgRelFormList = projectCluesDao.getAdminDataViewPremissionApprove(userOrgRelForm);
        if (CollectionUtils.isEmpty(userOrgRelFormList)) {
            return null;
        }
        List<String> orgIds = new ArrayList<>();
        for (UserOrgRelForm orgRelForm : userOrgRelFormList) {
            if (org.springframework.util.StringUtils.isEmpty(orgRelForm.getOrgId())) {
                continue;
            }
            List<String> orgs = Arrays.asList(orgRelForm.getOrgId().split(","));
            orgIds.addAll(orgs);
            orgIds = orgIds.stream().distinct().collect(Collectors.toList());
        }
        return orgIds;
    }
    @Override
    public ResultBody getSendToMessage(Map map) {
        int pageNum = 1;
        int pageSize = 10;
        System.out.println(map.get("pageSize"));
        if (map.get("pageNum") != null){
            pageNum = Integer.parseInt(map.get("pageNum")+"");
        }
        if (map.get("pageSize") != null){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        map.put("userId",SecurityUtils.getUserId());
        try {
            PageHelper.startPage(pageNum,pageSize);
            List<MessageForm> messageList = workbenchMapper.getMessageList(map);
            return ResultBody.success(new PageInfo<>(messageList));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(-11_0002, e);
        }
    }

    @Override
    public ResultBody getMessageSize(Map map) {
        String userId = SecurityUtils.getUserId();
        return ResultUtil.success(workbenchMapper.getUserMessageNum(userId));
    }

    @Override
    public ResultBody updMessIsRead(Map map) {
        String userId = SecurityUtils.getUserId();
        map.put("userId", userId);
        return ResultUtil.success(workbenchMapper.updMessIsRead(map));
    }

    @Override
    public ResultBody updateMessage(Map map) {
        return ResultBody.success(workbenchMapper.updateMessage(map));
    }
}
