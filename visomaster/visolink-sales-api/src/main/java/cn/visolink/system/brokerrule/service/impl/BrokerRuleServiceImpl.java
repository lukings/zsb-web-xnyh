package cn.visolink.system.brokerrule.service.impl;

import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.dao.MessageMapper;
import cn.visolink.system.brokerrule.mapper.BrokerRuleMapper;
import cn.visolink.system.brokerrule.model.form.BrokerRuleForm;
import cn.visolink.system.brokerrule.model.form.BrokerRuleList;
import cn.visolink.system.brokerrule.model.vo.BrokerRuleVO;
import cn.visolink.system.brokerrule.service.BrokerRuleService;
import cn.visolink.system.channel.model.form.ProjectProtectRuleForm;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/2/3
 */
@Service
public class BrokerRuleServiceImpl implements BrokerRuleService {

    @Autowired
    private BrokerRuleMapper brokerRuleMapper;

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 查询全民经纪人规则
     * @param brokerRuleForm
     * @return
     */
    @Override
    public ResultBody getBrokerRuleList(BrokerRuleForm brokerRuleForm) {
        PageHelper.startPage((int) brokerRuleForm.getCurrent(), (int) brokerRuleForm.getSize());
        Page<BrokerRuleVO> brokerRuleVOS =brokerRuleMapper.getBrokerRuleList(brokerRuleForm);
        return ResultBody.success(new PageInfo<BrokerRuleVO>(brokerRuleVOS));
    }

    /**
     * 查询全民经纪人规则详情
     * @param map
     * @return
     */
    @Override
    public List<BrokerRuleVO> getBrokerRuleDetails(Map map) {
        if(map.get("activityId") == null){
            throw new BadRequestException(-10_0000, "参数不能为空");
        }
        return brokerRuleMapper.getBrokerRuleDetails(String.valueOf(map.get("activityId")));
    }

    @Override
    public List<BrokerRuleVO> getBrokerRuleDetailsByProId(Map map) {
        if(map.get("projectId") == null || "".equals(map.get("projectId"))){
            throw new BadRequestException(-10_0000, "参数不能为空");
        }
        return brokerRuleMapper.getBrokerRuleDetailsByProId(map.get("projectId")+"");
    }

    /**
     * 保存全民经纪人规则
     * @param brokerRuleList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map saveBrokerRule(BrokerRuleList brokerRuleList) {
        Map<String, Object> returnMap = new LinkedHashMap<>();
        try {
            //判断操作类类型
            if ("1".equals(brokerRuleList.getOperateType())) {
                //新增
                String projectRuleId = brokerRuleMapper.getProjectRuleId(brokerRuleList.getProjectId());
                if (StringUtils.isBlank(projectRuleId)) {
                    projectRuleId = UUID.randomUUID().toString();
                    ProjectProtectRuleForm projectProtectRuleForm = new ProjectProtectRuleForm();
                    projectProtectRuleForm.setId(projectRuleId);
                    projectProtectRuleForm.setProjectId(brokerRuleList.getProjectId());
                    projectProtectRuleForm.setProjectOrgCategory(1);
                    projectProtectRuleForm.setSourceType(4);
                    projectProtectRuleForm.setEditor(brokerRuleList.getUserId());
                    projectProtectRuleForm.setIsEnterPublicPool(0);
                    brokerRuleMapper.insertProtectRule(projectProtectRuleForm);
                }
                String activityId = UUID.randomUUID().toString();
                List<BrokerRuleForm> brokerRuleFormList = brokerRuleList.getBrokerRuleFormList();
                for (int i = 0; i < brokerRuleFormList.size(); i++) {
                    brokerRuleFormList.get(i).setRuleID(projectRuleId);
                    brokerRuleFormList.get(i).setActivityId(activityId);
                    brokerRuleFormList.get(i).setProjectArea(brokerRuleList.getProjectArea());
                    brokerRuleFormList.get(i).setProjectName(brokerRuleList.getProjectName());
                    brokerRuleFormList.get(i).setCreateUserName(brokerRuleList.getUserName());
                    brokerRuleFormList.get(i).setCreateUser(brokerRuleList.getUserId());
                }
                brokerRuleMapper.insertBrokerRule(brokerRuleFormList);
                //判断新增规则为启用时则更新楼盘数据
                if ("1".equals(brokerRuleFormList.get(0).getIsEnable())){
                    Map maps = new HashMap();
                    maps.put("brokerageRule",brokerRuleFormList.get(0).getBrokerageRule());
                    maps.put("projectId",brokerRuleList.getProjectId());
                    maps.put("startTime",brokerRuleFormList.get(0).getEnableDate());
                    maps.put("endTime",brokerRuleFormList.get(0).getEndDate());
                    maps.put("isReport","1");
                    brokerRuleMapper.updateBuildBookIsReport(maps);
                }
            } else {
                //查询修改的规则原来是否是开启状态 是-更新楼盘信息 否-则不更新
                int isNum = brokerRuleMapper.getIsProjectRule(brokerRuleList.getBrokerRuleFormList().get(0).getId());
                //修改
                List<BrokerRuleForm> brokerRuleFormList = brokerRuleList.getBrokerRuleFormList();
//                boolean flag = false;
//                for (int i = 0; i < brokerRuleFormList.size(); i++) {
//                    brokerRuleFormList.get(i).setEditUser(brokerRuleList.getUserId());
//                    if ("0".equals(brokerRuleFormList.get(i).getIsEnable())) {
//                        flag = true;
//                    }
//                }
                brokerRuleMapper.updateBrokerRule(brokerRuleFormList);
                //如果修改的规则状态为开启或原状态为开启则更新楼盘数据
                if (isNum == 1 || "1".equals(brokerRuleFormList.get(0).getIsEnable())){
                    Map maps = new HashMap();
                    maps.put("brokerageRule",brokerRuleFormList.get(0).getBrokerageRule());
                    maps.put("projectId",brokerRuleList.getProjectId());
                    maps.put("startTime",brokerRuleFormList.get(0).getEnableDate());
                    maps.put("endTime",brokerRuleFormList.get(0).getEndDate());
                    maps.put("isReport",brokerRuleFormList.get(0).getIsEnable());
                    brokerRuleMapper.updateBuildBookIsReport(maps);
                }
//                Map maps = new HashMap();
//                maps.put("brokerageRule",brokerRuleFormList.get(0).getBrokerageRule());
//                maps.put("projectId",brokerRuleList.getProjectId());
//                if (flag && isNum == 0) {
//                    maps.put("isReport",0);
//                    brokerRuleMapper.updateBuildBookIsReport(maps);
//                }else{
//                    if(isNum == 0){
//                        maps.put("startTime",brokerRuleFormList.get(0).getEnableDate());
//                        maps.put("endTime",brokerRuleFormList.get(0).getEndDate());
//                    }
//                    maps.put("isReport",1);
//                    brokerRuleMapper.updateBuildBookIsReport(maps);
//                }
            }
            returnMap.put("code", "0");
            returnMap.put("errmsg", "保存规则成功");
        }catch (Exception e){
            //回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            returnMap.put("code", "-1");
            returnMap.put("errmsg", "保存规则失败");
        }
        return returnMap;
    }

    /**
     * 查询项目是否有启动的活动
     * @param map
     * @return
     */
    @Override
    public Map getBrokerRuleIsEnable(Map map) {
        if(map.get("projectId") == null ||
            map.get("enableDate") == null ||
            map.get("endDate") == null ||
            map.get("isEnable") == null){
            throw new BadRequestException(-10_0000, "参数不能为空");
        }
        Map returnMap = new  HashMap();
        String activityId = brokerRuleMapper.getBrokerRuleIsEnableD(String.valueOf(map.get("projectId")),
                String.valueOf(map.get("enableDate")),String.valueOf(map.get("endDate")));
        if(!StringUtils.isEmpty(activityId) && !activityId.equals(String.valueOf(map.get("activityId")))){
            returnMap.put("code", "1");
            returnMap.put("data", "当前活动日期已有活动,请重新选择日期");
            return returnMap;
        }else{
            activityId = brokerRuleMapper.getBrokerRuleIsEnable(String.valueOf(map.get("projectId")));
            if(!StringUtils.isEmpty(activityId) && "1".equals(String.valueOf(map.get("isEnable"))) && !activityId.equals(String.valueOf(map.get("activityId")))){
                returnMap.put("code", "1");
                returnMap.put("data", "已有活动启用,当前活动不能启用");
                return returnMap;
            }
        }
        returnMap.put("code", "0");
        returnMap.put("data", 0);
        return returnMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBrokerRule(String activityId) {
        brokerRuleMapper.delBrokerRule(activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disabledBrokerRule(Map map) {
        //如果禁用的规则是可报备状态则更新此项目楼盘为不可推荐状态
        if ("1".equals(map.get("isEnable"))){
            Map maps = new HashMap();
            maps.put("projectId",map.get("projectId"));
            maps.put("isReport","0");
            brokerRuleMapper.updateBuildBookIsReport(maps);
        }
        brokerRuleMapper.disabledBrokerRule(map);
    }
    @Override
    public ResultBody enableBrokerRule(Map map) {
        //判断时间是否冲突
        String acID = brokerRuleMapper.getBrokerRuleIsEnableD(map.get("projectId")+"",map.get("enableDate")+"",map.get("endDate")+"");
        if (StringUtils.isEmpty(acID)){
            map.put("editUser", SecurityUtils.getUserId());
            brokerRuleMapper.enableBrokerRule(map);
            return ResultBody.success("启用成功！");
        }else{
            return ResultBody.error(-120002,"规则时间有冲突请查看");
        }

    }
}
