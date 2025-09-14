package cn.visolink.system.projectmanager.service.impl;

import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.projectmanager.dao.BindProject;
import cn.visolink.system.projectmanager.dao.projectmanagerDao;
import cn.visolink.system.projectmanager.model.*;
import cn.visolink.system.projectmanager.model.requestmodel.*;
import cn.visolink.system.projectmanager.service.projectmanagerService;
import cn.visolink.utils.FileUtils;
import cn.visolink.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.core.exception.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

@Service
public class projectmanagerServiceImpl implements projectmanagerService {
    @Autowired
    projectmanagerDao managerDao;

    /**
     *   警告编码
     */
    private static final Integer ERROR_CODE = 500;

    /**
     * 项目管理查询
     * @param projectQueryRequest
     * @param request
     * @return
     */
    @Override
    public ResultBody projectListSelect(ProjectQueryRequest projectQueryRequest, HttpServletRequest request) {
        try {
            if (org.apache.commons.lang3.StringUtils.isEmpty(projectQueryRequest.getOrgId())) {
                return ResultBody.error(500, "组织id为空");
            }
            Map fullPath = managerDao.selectFullPath(projectQueryRequest.getOrgId());
            if (MapUtils.isEmpty(fullPath)) {
                return ResultBody.error(500, "组织新增为空");
            }

            if (org.apache.commons.lang3.StringUtils.isEmpty((String) fullPath.get("FullPath"))) {
                return ResultBody.error(500, "组织新增为空");
            }

            projectQueryRequest.setFullPath((String) fullPath.get("FullPath"));

            PageHelper.startPage(projectQueryRequest.getPageIndex(), projectQueryRequest.getPageSize());
            return ResultBody.success(new PageInfo<>(managerDao.selectProjectListByFullPath(projectQueryRequest)));
        } catch (Exception e) {
            throw new BadRequestException(-13_0001, e);
        }
    }
   /* @Override
    public ResultBody projectListSelect(ProjectQueryRequest projectQueryRequest, HttpServletRequest request) {
        try {
            if (org.apache.commons.lang3.StringUtils.isEmpty(projectQueryRequest.getOrgId())) {
                return ResultBody.error(500, "组织id为空");
            }
            // 防止存在缓存而出现脏数据
            finallyMap.clear();
            List<Map> projectByOrgId = selectProjectByOrgId(projectQueryRequest.getOrgId());
            List<Map> mapList = projectByOrgId.stream().distinct().collect(Collectors.toList());
            // 为了防止projectByOrgId为空，导致报错
            if (CollectionUtils.isEmpty(mapList)) {
                Map map = new HashMap();
                map.put("ID", "---");
                mapList = Arrays.asList(map);
            }
            System.out.println("最终的项目为： " + projectByOrgId);
            PageHelper.startPage(projectQueryRequest.getPageIndex(), projectQueryRequest.getPageSize());
            return ResultBody.success(new PageInfo<>(managerDao.selectAllProject(mapList, projectQueryRequest.getProjectName(), projectQueryRequest.getAreaName())));
        } catch (Exception e) {
            throw new BadRequestException(-13_0001, e);
        }
    }*/


    static final List<Map> finallyMap = new ArrayList<>();
    private List<Map> selectProjectByOrgId(String orgId) {

        List<Map> mapList = managerDao.selectProjectByOrgId(orgId);
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map m : mapList) {
                Integer orgCategory = (Integer) mapList.get(0).get("OrgCategory");
                if (orgCategory != 4) {
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty((String) m.get("ID"))) {
                        selectProjectByOrgId((String) m.get("ID"));
                    }
                }
                if (orgCategory == 4) {
                    finallyMap.add(m);
                }

            }
        }
        else {
            List<Map> mapList1 = managerDao.selectProjectPlusPageByOrgId(orgId);
            if (CollectionUtils.isNotEmpty(mapList1)) {
                for (Map map : mapList1) {
                    finallyMap.add(map);
                }
            }
        }
        System.out.println(finallyMap + "----------------------------------------------------------------");
        return finallyMap;
    }


    /**
     * 新建项目与分期共用接口
     *
     * @param project
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody createProject(ProjectModel project, HttpServletRequest request) {
        // 生成项目id
        String _project_id = project.getProjectId();
        if (org.apache.commons.lang3.StringUtils.isEmpty(_project_id)) {
            _project_id = UUID.randomUUID().toString().replace("-", "");
        }
        project.setId(_project_id);

        Map fullPathMap = managerDao.selectFullPath(project.getOrgId());
        if (MapUtils.isEmpty(fullPathMap)) {
            return ResultBody.error(ERROR_CODE, "组织信息为空！");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty((String) fullPathMap.get("FullPath"))) {
            return ResultBody.error(ERROR_CODE, "fullpath为空，请先维护！！");
        }

        // 截取fullpath到区域
        String fullPath = (String) fullPathMap.get("FullPath");
        String[] strings = fullPath.split("/");
        fullPath = strings[0] + "/" + strings[1];

        // 通过full path查询组织数据
        Map fullPathMap2 = managerDao.selectDateByFullPath(fullPath);

        project.setFullPath(fullPathMap.get("FullPath") + "/" + project.getProjectName());
        String username = request.getHeader("username");
        System.out.println("获取到的用户名称   " + username);
        int countProject = 0;
        project.setCreator(username);
        project.setAreaid((String) fullPathMap2.get("ID"));
        project.setAreaname((String) fullPathMap2.get("OrgName"));

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(_project_id)) {
            countProject = managerDao.isExistProject(_project_id);
        }
        Map map = new HashMap();
        System.out.println("是否存在项目：  " + countProject);
        if (countProject == 0) {
            // 根据项目名称判断是否已经存在项目
            Integer isExist = managerDao.isExistProjectName(project.getProjectName());
            if (isExist > 0) {
                return ResultBody.error(ERROR_CODE, "项目名称已经存在！");
            }

            // 新增项目时把字典表全局的与字典与该项目绑定
            Map ha = new HashMap();
            ha.put("Creator", username);
            ha.put("ProjectID", _project_id);
            managerDao.saveGlobalDictionary(ha);

            // 保存项目数据
            managerDao.createProject(project);
            // 创建项目给组织表绑定项目id
            managerDao.updateOrganizationByOrgId(_project_id, project.getOrgId());
            if (null != project.getStageModelList()) {
                project.getStageModelList().setCreateUser(username);
                project.getStageModelList().setProjectId(_project_id);
                project.getStageModelList().setOrderCode(1);
                project.getStageModelList().setStartTime(org.apache.commons.lang3.StringUtils.isEmpty(project.getStageModelList().getStartTime()) == true ? null : project.getStageModelList().getStartTime());
                project.getStageModelList().setEndTime(org.apache.commons.lang3.StringUtils.isEmpty(project.getStageModelList().getEndTime()) == true ? null : project.getStageModelList().getEndTime());
                // 分期id
                String stageId = UUID.randomUUID().toString();
                project.getStageModelList().setStageId(stageId);
                System.out.println("项目id为：   " + _project_id);
                List<StageModel> stageModelList = beanToList(project.getStageModelList());
                managerDao.createStage(stageModelList);
                map.put("projectId", _project_id);
                map.put("message", "新建分期成功");

                // 保存分期数据到项目表
                ProjectModel pm = new ProjectModel()
                        .setId(stageId).setPid(_project_id)
                        .setProjectName(project.getStageModelList().getStageName())
                        .setCreator(username).setProjectnum(project.getStageModelList().getStageCode());
                managerDao.createProject(pm);

                return ResultBody.success(map);
            }
            map.put("projectId", _project_id);
            if ("1".equals(project.getProjectType())) {
                map.put("message", "项目创建成功");
            }
            return ResultBody.success(map);
        }
        // 已经存在项目
        if (countProject > 0) {

            if ("2".equals(project.getProjectType())) {
                // 根据项目名称判断是否已经存在项目
                Map map1 = managerDao.isExistProjectName2(project.getProjectName());
                if (org.apache.commons.lang3.StringUtils.isEmpty(project.getProjectName())) {
                    project.setProjectName("");
                }
                if (org.apache.commons.lang3.StringUtils.isEmpty(project.getProjectId())) {
                    project.setProjectId("");
                }

                if (null != map1
                        && project.getProjectName().equals(map1.get("projectName"))
                        && !project.getProjectId().equals(map1.get("projectId"))) {
                    return ResultBody.error(ERROR_CODE, "项目名称已经存在！");
                }
                // 修改项目
                managerDao.saveProjectFromEdit(project);
            }

            if (null != project.getStageModelList()) {
                project.getStageModelList().setCreateUser(username);
                Integer orderCode = managerDao.getMaxOrderCode(_project_id);
                project.getStageModelList().setOrderCode(orderCode == null ? 1 : orderCode + 1);
                project.getStageModelList().setStartTime(org.apache.commons.lang3.StringUtils.isEmpty(project.getStageModelList().getStartTime()) == true ? null : project.getStageModelList().getStartTime());
                project.getStageModelList().setEndTime(org.apache.commons.lang3.StringUtils.isEmpty(project.getStageModelList().getEndTime()) == true ? null : project.getStageModelList().getEndTime());
                // 根据分期名称判断是否已经存在分期
                Integer isExistStageName = managerDao.isExistStageName(project.getStageModelList().getStageName(), project.getProjectId());
                if (isExistStageName > 0) {
                    throw new RuntimeException("此项目该分期名称已经存在，请重新输入分期名称");
                }
                // 分期id
                String stageId = UUID.randomUUID().toString();
                project.getStageModelList().setStageId(stageId);
                // 添加分期
                List<StageModel> stageModels = beanToList(project.getStageModelList());
                managerDao.createStage(stageModels);

                map.put("projectId", _project_id);
                map.put("message", "新建分期成功");

                // 保存分期数据到项目表
                ProjectModel pm = new ProjectModel()
                        .setId(stageId).setPid(_project_id)
                        .setProjectName(project.getStageModelList().getStageName())
                        .setCreator(username).setProjectnum(project.getStageModelList().getStageCode());
                managerDao.createProject(pm);

                return ResultBody.success(map);
            }
            map.put("projectId", _project_id);
            map.put("message", "项目编辑成功");
        }

        return ResultBody.success(map);
    }

    private <T> List<T> beanToList(T t) {
        List<T> list = new ArrayList<>();
        if (null != t) {
            list.add(t);
            return list;
        }
        return null;
    }

    /**
     * 通过id查询项目信息
     *
     * @param id
     * @return
     */
    @Override
    public ResultBody getProject(String id) {
        return ResultBody.success(managerDao.getProjectByProjectId(id));
    }

    /**
     * 新建项目的新建组团的楼栋全部查询
     *
     * @param stageId
     * @return
     */
    @Override
    public ResultBody selectDesignBuildByStageId(String stageId) {
        return ResultBody.success(managerDao.selectDesignBuildByStageId(stageId));
    }

    /**
     * 查询分期通过通过项目id
     *
     * @param projectId
     * @return
     */
    @Override
    public ResultBody selectStageListByProjectId(String projectId) {
        StageModel stageModel = new StageModel();
        stageModel.setProjectId(projectId);
        return ResultBody.success(managerDao.selectStagePlusPageList(stageModel));
    }

    /**
     * 通过项目id查询组团
     *
     * @param projectId
     * @return
     */
    @Override
    public ResultBody selectGroupListByProjectId(String projectId) {
        return ResultBody.success(managerDao.selectGroupListByProjectId(projectId));
    }


    /**
     * 项目编辑页面里面的新建组团
     *
     * @param groupModel
     * @return
     */
    @Override
    public ResultBody createGroup(GroupModel groupModel, HttpServletRequest request) {
        // 根据组团名称判断是否已经存在组团
        Map existGroupName = managerDao.isExistGroupName(groupModel.getGroupName(), groupModel.getStageId());
        if (existGroupName != null) {
            return ResultBody.error(ERROR_CODE, "组团名称已经存在！");
        }
        String username = request.getHeader("username");
        String group_id = UUID.randomUUID().toString().replace("-", "");
        List<GroupModel> groupModelList = new ArrayList<>();
        groupModel.setCreateUser(username);
        groupModel.setGroupId(group_id);
        groupModelList.add(groupModel);

        System.out.println("组团保存的数据： " + groupModel);
        managerDao.createGroup(groupModelList);

        if (CollectionUtils.isNotEmpty(groupModel.getDesignBuildModelList())) {
            for (DesignBuildModel gm : groupModel.getDesignBuildModelList()) {
                gm.setGroupId(group_id);
                gm.setStageId(groupModel.getStageId());
            }
            managerDao.updateBatchDesignBuild(groupModel.getDesignBuildModelList());
        }

        return ResultBody.success("创建组团成功");
    }

    /**
     * 项目编辑页面里面的新建楼栋
     *
     * @param designBuildModel
     * @return
     */
    @Override
    public ResultBody createDesignBuild(DesignBuildModel designBuildModel, HttpServletRequest request) {
        String username = request.getHeader("username");
        if (org.apache.commons.lang3.StringUtils.isEmpty(designBuildModel.getProjectId())) {
            return ResultBody.error(ERROR_CODE, "项目为空");
        }

        // 根据楼栋名称判断是否已经存在楼栋
        Map existBuildName = managerDao.isExistBuildName(designBuildModel.getProductBuildName(), designBuildModel.getProjectId());
        if (existBuildName != null) {
            return ResultBody.error(ERROR_CODE, "楼栋名称已经存在！");
        }

        // 竣备证取证时间
        if (org.apache.commons.lang3.StringUtils.isEmpty(designBuildModel.getCompletioncerDate())) {
            designBuildModel.setCompletioncerDate(null);
        }

        // 施工证取证时间
        if (org.apache.commons.lang3.StringUtils.isEmpty(designBuildModel.getConstructionQzDate())) {
            designBuildModel.setConstructionQzDate(null);
        }

        // 土地证取证时间
        if (org.apache.commons.lang3.StringUtils.isEmpty(designBuildModel.getLandQzDate())) {
            designBuildModel.setLandQzDate(null);
        }

        // 规划证取证时间
        if (org.apache.commons.lang3.StringUtils.isEmpty(designBuildModel.getPlanningQzDate())) {
            designBuildModel.setPlanningQzDate(null);
        }

        // 预售证取证时间
        if (org.apache.commons.lang3.StringUtils.isEmpty(designBuildModel.getPresaleQzDate())) {
            designBuildModel.setPresaleQzDate(null);
        }


        List<DesignBuildModel> designBuildModelList = new ArrayList<>();
        designBuildModel.setCreateUser(username);
        designBuildModel.setBuildId(UUID.randomUUID().toString());
        designBuildModel.setOrderCode(this.maxOrderCode(designBuildModel.getProjectId()));
        designBuildModelList.add(designBuildModel);
        System.out.println("楼栋保存的数据： " + designBuildModel);
        return ResultBody.success(managerDao.createDesignBuild(designBuildModelList));
    }

    /**
     * 项目编辑页面里面的分期分页查询
     *
     * @param stageModel
     * @return
     */
    @Override
    public ResultBody selectStagePlusPageList(StageModel stageModel) {
        PageHelper.startPage(stageModel.getPageIndex(), stageModel.getPageSize());
        return ResultBody.success(new PageInfo<>(managerDao.selectStagePlusPageList(stageModel)));
    }

    /**
     * 项目编辑页面里面的组团分页查询
     *
     * @param groupModel
     * @return
     */
    @Override
    public ResultBody selectGroupPlusPageList(GroupModel groupModel) {
        PageHelper.startPage(groupModel.getPageIndex(), groupModel.getPageSize());
        List<GroupModel> groupModelList = managerDao.selectGroupPlusPageList(groupModel);
        if (CollectionUtils.isNotEmpty(groupModelList)) {
            for (GroupModel gm : groupModelList) {
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(gm.getStageId())) {
                    gm.setDesignBuildModelList(managerDao.selectDesignBuildByStageId(gm.getStageId()));
                }
            }
        }
        return ResultBody.success(new PageInfo<>(groupModelList));
    }

    /**
     * 项目编辑页面里面的楼栋分页查询
     *
     * @param designBuildModel
     * @return
     */
    @Override
    public ResultBody selectDesignBuildPlusPageList(DesignBuildModel designBuildModel) {
        PageHelper.startPage(designBuildModel.getPageIndex(), designBuildModel.getPageSize());
        return ResultBody.success(new PageInfo<>(managerDao.selectDesignBuildPlusPageList(designBuildModel)));
    }

    /**
     * 项目编辑页面里面的分期的编辑
     *
     * @param stageModel
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody updateStage(StageModel stageModel, HttpServletRequest request) {
        String username = request.getHeader("username");
        stageModel.setUpdateUser(username);

        // 根据分期id查询分期信息
        Map existStageName = managerDao.isExistStageName2(stageModel.getStageName(), stageModel.getProjectId());
        if (null != existStageName
                && stageModel.getStageName().equals(existStageName.get("stageName"))
                && !stageModel.getStageId().equals(existStageName.get("stageId"))) {
            return ResultBody.error(ERROR_CODE, "分期名称已经存在");
        }

        // 保存分期数据到项目表
        ProjectModel pm = new ProjectModel()
                // 因为 saveProjectFromEdit的分期id是使用项目id更新的(id使用projectId代替, where ID = #{projectId})
                .setProjectId(stageModel.getStageId())
                .setProjectName(stageModel.getStageName())
                .setEditor(username).setProjectnum(stageModel.getStageCode());
        managerDao.saveProjectFromEdit(pm);

        // 保存分期数据
        return  managerDao.updateStage(stageModel) > 0
                ? ResultBody.success("分期修改成功") : ResultBody.success("分期修改失败");
    }

    /**
     * 项目编辑页面里面的组团的编辑
     *
     * @param groupModel
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody updateGroup(GroupModel groupModel, HttpServletRequest request) {
        String username = request.getHeader("username");
        groupModel.setUpdateUser(username);

        // 根据组团名称查询组团信息
        Map existGroupName = managerDao.isExistGroupName(groupModel.getGroupName(), groupModel.getStageId());
        if (null != existGroupName
                && groupModel.getGroupName().equals(existGroupName.get("groupName"))
                && !groupModel.getGroupId().equals(existGroupName.get("groupId"))) {
            return ResultBody.error(ERROR_CODE, "组团名称已经存在");
        }

        Integer integer = managerDao.updateGroup(groupModel);
        if (CollectionUtils.isNotEmpty(groupModel.getDesignBuildModelList())) {

            List<DesignBuildModel> designBuildModelList = new ArrayList<>();
            List<DesignBuildModel> resetGroupList = new ArrayList<>();
            DesignBuildModel designBuildModel = new DesignBuildModel();
            designBuildModel.setGroupId(groupModel.getGroupId());
            resetGroupList.add(designBuildModel);
            managerDao.resetGroup(resetGroupList);

            for (DesignBuildModel dbm : groupModel.getDesignBuildModelList()) {
                dbm.setGroupId(groupModel.getGroupId());
                dbm.setStageId(groupModel.getStageId());
                designBuildModelList.add(dbm);
            }

            managerDao.updateBatchDesignBuild(designBuildModelList);
        }
        return integer > 0
                ? ResultBody.success("组团修改成功") : ResultBody.success("组团修改失败");
    }

    /**
     * 项目编辑页面里面的楼栋的编辑
     *
     * @param designBuildModel
     * @param request
     * @return
     */
    @Override
    public ResultBody updateDesignBuild(DesignBuildModel designBuildModel, HttpServletRequest request) {
        String username = request.getHeader("username");
        designBuildModel.setUpdateUser(username);

        // 根据楼栋名称查询楼栋信息
        Map existProductBuildName = managerDao.isExistBuildName(designBuildModel.getProductBuildName(), designBuildModel.getProjectId());
        if (null != existProductBuildName
                && designBuildModel.getProductBuildName().equals(existProductBuildName.get("productBuildName"))
                && !designBuildModel.getBuildId().equals(existProductBuildName.get("buildId"))) {
            return ResultBody.error(ERROR_CODE, "楼栋名称已经存在");
        }

        return managerDao.updateDesignBuild(designBuildModel) > 0
                ? ResultBody.success("楼栋修改成功") : ResultBody.success("楼栋修改失败");
    }

    /**
     * 项目编辑页面里面的分期的删除
     *
     * @param stageModel
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody delStage(StageModel stageModel) {
        // 1. 删除项目表中的分期数据通过id
        managerDao.deleteProject(stageModel.getStageId());
        // 2. 删除分期数据
        return managerDao.delStage(stageModel) > 0
                ? ResultBody.success("分期删除成功") : ResultBody.success("分期删除失败");
    }

    /**
     * 项目编辑页面里面的组团的删除
     *
     * @param groupModel
     * @return
     */
    @Override
    public ResultBody delGroup(GroupModel groupModel) {
        return managerDao.delGroup(groupModel) > 0
                ? ResultBody.success("组团删除成功") : ResultBody.success("组团删除失败");
    }

    /**
     * 项目编辑页面里面的楼栋的删除
     *
     * @param designBuildModel
     * @return
     */
    @Override
    public ResultBody delDesignBuild(DesignBuildModel designBuildModel) {
        // 1. 根据楼栋id删除单元的数据
        managerDao.delUnitByBuildId(designBuildModel.getBuildId());

        // 2. 根据楼栋id删除房间的数据
        managerDao.delRoomByBuildId(designBuildModel.getBuildId());

        // 3. 根据楼栋id删除楼栋的数据
        return managerDao.delDesignBuild(designBuildModel) > 0
                ? ResultBody.success("楼栋删除成功") : ResultBody.success("楼栋删除失败");
    }

    /**
     * 通过分期id查询分期信息
     *
     * @param stageId
     * @return
     */
    @Override
    public ResultBody queryStage(String stageId) {
        return ResultBody.success(managerDao.queryStage(stageId));
    }

    /**
     * 通过组团id查询组团信息
     *
     * @param groupId
     * @return
     */
    @Override
    public ResultBody queryGroup(String groupId) {
        GroupModel groupModel = managerDao.queryGroup(groupId);
        if (null != groupModel) {
            List<DesignBuildModel> designBuildModelList = managerDao.selectDesignBuildByGroupId(groupId);
            groupModel.setDesignBuildModelList(designBuildModelList);
        }
        return ResultBody.success(groupModel);
    }

    /**
     * 通过楼栋id查询楼栋信息
     *
     * @param buildId
     * @return
     */
    @Override
    public ResultBody queryDesignBuild(String buildId) {
        return ResultBody.success(managerDao.queryDesignBuild(buildId));
    }

    /**
     * 校验是否已经存在项目
     *
     * @param orgId
     * @return
     */
    @Override
    public ResultBody verifyIsExistProject(String orgId) {
        return managerDao.verifyIsExistProject(orgId) > 0
                ? ResultBody.success("已经存在项目，不能重复建项目")
                : ResultBody.success("可以新建项目");
    }














    /**
     * 启用禁用项目
     */
    @Override
    public Integer projectIsEnableUpdate(Map<String, Object> map) {
        try {
            if (!map.isEmpty()) {
                if ("1".equals(map.get("Status")+"")){
                    map.put("isSyn",1);
                }else{
                    map.put("isSyn",0);
                }
                return managerDao.projectIsEnableUpdate(map);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0002, e);
        }
        return null;

    }

    /**
     * 删除项目
     */
    @Override
    public Integer projectDeleteUpdate(Map<String, Object> map) {
        try {
            if (!map.isEmpty()) {
                return managerDao.projectDeleteUpdate(map);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0003, e);
        }
        return null;
    }

    /*
     * 判断项目编号是否已存在
     * */
    @Override
    public Map<String, Object> projectNumIsExsit(Map<String, String> dataMap) {
        try {
            if (!dataMap.isEmpty()) {
                return managerDao.projectNumIsExsit(dataMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0011, e);
        }
        return null;
    }


    /*新增销售系统与售前系统的关联,先跟新，然後插入，这里是跟新
     * 与售前项目相关联,和AddNewProjectSaleRelInsert一起被调用
     * */
    @Override
    public Integer addNewProjectSaleRelUpdate(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.addNewProjectSaleRelUpdate(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0015, e);
        }
        return null;
    }

    /*
     * 新增销售系统与售前系统的关联,先跟新，然後插入，这里是插入数据
     * 与售前项目相关联,和addNewProjectSaleRelUpdate一起被调用
     * */
    @Override
    public Integer addNewProjectSaleRelInsert(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.addNewProjectSaleRelInsert(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0016, e);
        }
        return null;

    }


    /*
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是查找项目ID
     * */
    @Override
    public Map<String, String> newProjectOrgSelect(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.newProjectOrgSelect(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0013, e);
        }
        return null;

    }

    /*
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是跟新项目
     * */
    @Override
    public Integer newProjectOrgUpdate(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.newProjectOrgUpdate(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0014, e);
        }
        return null;
    }

    /*
     * 修改项目信息
     * */
    @Override
    public Integer projectInfoModify(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.projectInfoModify(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0005, e);
        }
        return null;
    }

    /*
     * 判断当前是否已经存在过关联关系
     * */
    @Override
    public Map<String, Object> projectSaleRelCountBySaleProjectIdSelect(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.projectSaleRelCountBySaleProjectIdSelect(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0012, e);
        }
        return null;
    }

    /*
     * 若产生关联关系（>0）且则走这一条方法
     * */
    @Override
    public Integer newProjectSaleRelNoDel(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.newProjectSaleRelNoDel(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0012, e);
        }
        return null;
    }

    /*
     * 增加一条新的项目
     * */
    @Override
    public Integer addNewProjectInfoInsert(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.addNewProjectInfoInsert(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0011, e);
        }
        return null;
    }

    /*
     * 增加项目和修改项目的调用方法
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String projectexecute(Map<String, String> dataMap, HttpServletRequest request, HttpServletResponse response) {
        String r = "";
        Map<String, Object> returnMap = new LinkedHashMap<>();
        String res = "";
        try {
            String reqType = dataMap.get("reqType");
            String oldProjectNum = dataMap.get("OldProjectNum");
            String ProjectNum = dataMap.get("ProjectNum");
            if (!oldProjectNum.equals(ProjectNum)) {

                /*
                 * 判断项目编号是否已存在
                 * */

                Map<String, Object> projectInfo = projectNumIsExsit(dataMap);
                if (projectInfo != null) {
                    returnMap.put("errcode", "-1");
                    returnMap.put("errmsg", "项目编号已存在");
                    return JSON.toJSONString(returnMap);

                }

            }
            /*
             * 查看被调用的是新增还是修改
             * */
            switch (reqType) {
                case "addNewProject":

                    returnMap = addNewProjectInfo(dataMap);
                    break;


                case "modifyProjectInfo":
                    returnMap = updateProjectInfo(dataMap);
                    break;
            }
        } catch (Exception e) {

            returnMap.put("errcode", "-1");
            returnMap.put("errmsg", "系统错误，请联系管理员!");
            e.printStackTrace();
            throw new BadRequestException(-13_0010, e);

        }

        return JSON.toJSONString(returnMap);
    }


    /*
     * 增加一条新的项目(联合所有需要增加项目的方法，在控制台被调用)
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addNewProjectInfo(Map<String, String> projectMap) {

        Map<String, Object> returnMap = new HashMap<>();

        try {

            String ProjectID = UUID.randomUUID().toString();
            projectMap.put("ID", ProjectID);

            /*
             * 以下方法为插入一条项目信息时需要调用的接口
             * */
            addNewProjectInfoInsert(projectMap);
            managerDao.addNewProjectSaleRelUpdateProjectorgrel(projectMap);
            managerDao.addNewProjectSaleRelUpdateOne(projectMap);
            managerDao.addNewProjectSaleRelUpdateTwo(projectMap);
            managerDao.addNewProjectSaleRelUpdateThree(projectMap);
            managerDao.addNewProjectSaleRelUpdateFour(projectMap);
            managerDao.addNewProjectSaleRelUpdateFive(projectMap);
            managerDao.addNewProjectSaleRelUpdateSix(projectMap);
            managerDao.addNewProjectSaleRelUpdateSeven(projectMap);
            managerDao.addNewProjectSaleRelUpdateEight(projectMap);


            String SaleProjectID = projectMap.get("SaleProjectID");
            if (!StringUtil.isNullOrEmpty(SaleProjectID)) {
                    /*
                       是否关联销售系统项目
                    *
                    * */
                addNewProjectSaleRelUpdate(projectMap);
                addNewProjectSaleRelInsert(projectMap);
            }

            /*
             * 添加完成修改对应组织的项目ID,查询当前组织的项目ID
             * */
            updateOrgProject(projectMap);
            returnMap.put("errcode", 0);
            returnMap.put("errmsg", "项目新增成功!");

        } catch (Exception e) {
            returnMap.put("errcode", "-1");
            returnMap.put("errmsg", "系统内部出错，请联系管理员");

            throw new BadRequestException(-13_0004, e);

        }

        return returnMap;
    }

    /*
     * 添加完成修改对应组织的项目ID,查询当前组织的项目ID
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrgProject(Map<String, String> projectMap) {
        System.out.println("哈哈哈" + projectMap.get("ID"));
        try {
            Map<String, String> orgMap = newProjectOrgSelect(projectMap);
            orgMap.put("NewProjectID", projectMap.get("ID") + "");
            newProjectOrgUpdate(orgMap);

        } catch (Exception e) {
            throw new BadRequestException(-13_0006, e);

        }
    }

    /*
     * 修改项目信息,该方法要被controller层调用，集合需要修改项目信息方法的接口和逻辑
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateProjectInfo(@RequestBody Map<String, String> projectMap) {

        Map<String, Object> returnMap = new HashMap<>();

        try {

            /*
             * 修改项目信息
             * */
            projectInfoModify(projectMap);
            projectMap.put("SaleIsDel", "1");
            /*
             * 把原来的关联项目删除，不管有没有新增关联
             * */
            addNewProjectSaleRelUpdate(projectMap);
            String SaleProjectID = projectMap.get("SaleProjectID");
            if (!StringUtil.isNullOrEmpty(SaleProjectID)) {
                /*
                 * 是否关联销售系统项目，判断当前内容不为空时，判断当前是否已经存在过关联关系
                 * */
                Map<String, Object> isSaleMap = projectSaleRelCountBySaleProjectIdSelect(projectMap);
                if (isSaleMap.size() > 0) {
                    long count = (long) isSaleMap.get("A");
                    if (count > 0) {
                        /*
                         * 当前已存在关联关系，执行此方法
                         * */
                        newProjectSaleRelNoDel(projectMap);
                    } else {
                        addNewProjectSaleRelInsert(projectMap);
                    }
                } else {
                    addNewProjectSaleRelInsert(projectMap);
                }


            }
            returnMap.put("errcode", 0);
            returnMap.put("errmsg", "项目修改成功!");

        } catch (Exception e) {

            e.printStackTrace();

            returnMap.put("errcode", "-1");
            returnMap.put("errmsg", "系统内部出错，请联系管理员");
            throw new BadRequestException(-13_0009, e);

        }

        return returnMap;
    }

    @Override
    public List<Map<String, Object>> selectOneProject(Map<String, Object> map) {
        return managerDao.selectOneProject(map);
    }

    @Override
    public Integer updateMenuStatus(Map<String, Object> map) {
        return managerDao.updateMenuStatus(map);
    }

    @Override
    public Map systemmenus(Map map) {

        return null;
    }

    @Override
    public List<Map> getCityList() {
        return managerDao.getCityList();
    }

    /**
     * 查询失效的项目列表
     *
     * @return
     */
    @Override
    @Transactional
    public ResultBody selectInvalidProject() {
        Integer invalidProjectCount = 0;
        // 查询失效的项目列表
        List<BindProject> invalidProjectList = managerDao.selectInvalidProject();
        // 存在失效的项目，则更新项目的状态为 禁止: 0
        if (invalidProjectList != null && !invalidProjectList.isEmpty()) {
            // b_bind_project 表的启动修改未禁用(项目失效)
            invalidProjectCount = managerDao.updateProjectStatus(invalidProjectList);
            // b_project 表的启动修改未禁用(项目失效)
            managerDao.updateStatus(invalidProjectList);
        }

        return ResultBody.success(invalidProjectCount);
    }

    /**
     * 查询未绑定的项目
     *
     * @param request
     * @return
     */
    @Override
    public ResultBody selectNotBindProject(HttpServletRequest request) {
        String companycode = request.getHeader("companycode");
        if (StringUtils.isEmpty(companycode)) {
            ResultBody.error(500, "Header中的companycode没有，请先维护");
        }
        Connection conn = null; PreparedStatement stat = null;
        try {
            String url = "jdbc:mysql://118.190.56.178:3306/authcompany" + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8";
            conn = DriverManager.getConnection(url, "root", "root");
            // 拼接的sql,用于查询未绑定的项目
            String sql = "select \n" +
                    "         id, is_bind as isBind, project_id as projectId, generate_name as generateName, \n" +
                    "         project_name as projectName, `status`, create_time as createTime, \n" +
                    "         edit_time as editTime, start_time as startTime, end_time as endTime, \n" +
                    "         company_code as companyCode, company_name as companyName, creator, editer \n" +
                    "         from b_bind_project \n" +
                    "         where is_del = 0 \n" +
                    "         AND `status` = 1 \n" +
                    "         AND `is_bind` = 0 \n" +
                    "         AND company_code = '" + companycode + "'";
            System.out.println("拼接的sql： " + sql);
            stat = conn.prepareStatement(sql);
            //执行脚本
            ResultSet resultSet = stat.executeQuery();
            System.out.println("返回的对象为： " + resultSet);
            List<BindProject> bindProjectsList = new ArrayList<>();
            // 遍历列表字段
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                int is_bind = resultSet.getInt("isBind");
                String project_id = resultSet.getString("projectId");
                String projectName = resultSet.getString("projectName");
                int status = resultSet.getInt("status");
                Date createTime = resultSet.getDate("createTime");
                Date edit_time = resultSet.getDate("editTime");
                Date start_time = resultSet.getDate("startTime");
                Date end_time = resultSet.getDate("endTime");
                String company_code = resultSet.getString("companyCode");
                String company_name = resultSet.getString("companyName");
                String creator = resultSet.getString("creator");
                String editer = resultSet.getString("editer");
                String generateName = resultSet.getString("generateName");
                // 封装 BindProject 实体
                BindProject bean = BindProject.builder()
                        .id(id).isBind(is_bind).projectId(project_id)
                        .projectName(projectName).status(status)
                        .createTime(createTime).editTime(edit_time)
                        .startTime(start_time).endTime(end_time)
                        .companyCode(company_code).companyName(company_name)
                        .creator(creator).editer(editer).generateName(generateName).build();
                bindProjectsList.add(bean);
            }
            stat.close();
            conn.close();
            return ResultBody.success(bindProjectsList);
        } catch (SQLException e) {
            try {
                stat.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return ResultBody.success(null);
    }




    /**
     * 生成房源
     *
     * @param housingResourceModel
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public ResultBody generationHousingResource(HousingResourceModel housingResourceModel, HttpServletRequest request) {
        String username = request.getHeader("username");

        //更新楼栋
        DesignBuildModel designBuildModel = new DesignBuildModel();
        BeanUtils.copyProperties(housingResourceModel, designBuildModel);
        managerDao.updateDesignBuild(designBuildModel);

        // 批量添加楼层
        if (CollectionUtils.isNotEmpty(housingResourceModel.getBuildingFloorList())) {
            housingResourceModel.getBuildingFloorList().forEach(buildingFloor -> buildingFloor.setCreator(username));
            managerDao.saveBatchBuildingFloor(housingResourceModel.getBuildingFloorList());
        }
        BigDecimal bigDecimal = new BigDecimal(0.00);
        if (CollectionUtils.isNotEmpty(housingResourceModel.getBuildUnitList())) {
            for (BuildUnit bu : housingResourceModel.getBuildUnitList()) {
                String _unitId = UUID.randomUUID().toString();
                bu.setCreateUser(username);
                bu.setUnitId(_unitId);
                if (CollectionUtils.isNotEmpty(bu.getBuildRoomList())) {
                    for (BuildRoom br : bu.getBuildRoomList()) {
                        br.setCreateUser(username);
                        br.setUnitId(_unitId);
                        if (org.apache.commons.lang3.StringUtils.isNotEmpty(br.getHxId())) {
                            BuildHx buildHx = managerDao.getHXArea(br.getHxId());
                            br.setYsBldArea(null != buildHx ? buildHx.getBldArea() : bigDecimal);
                            br.setYsTnArea(null != buildHx ? buildHx.getTnArea() : bigDecimal);
                        }
                    }
                    // 批量添加房间
                    managerDao.saveBatchRoom(bu.getBuildRoomList());
                }
            }
            // 更新楼栋表的生成房源为1(已经生成房源)
            managerDao.updateDesignBuildByBuildId(housingResourceModel.getBuildId());
            // 批量添加单元
            managerDao.saveBatchUnit(housingResourceModel.getBuildUnitList());
        }
        return ResultBody.success("添加成功");
    }

    /**
     * 从浏览器取值填充到bean对象中
     *
     * @param t
     * @param request
     * @param prop
     * @param <T>
     * @return
     */
    private <T> T pushBeanFromBrowser(T t, HttpServletRequest request, String... prop) {
        try {
            if (CollectionUtils.isNotEmpty(Arrays.asList(prop))) {
                for (String s : prop) {
                    String[] split = s.split("-");
                    String params = request.getHeader(split[0]);
                    Field declaredFields = t.getClass().getDeclaredField(split[1]);
                    declaredFields.setAccessible(true);
                    declaredFields.set(t, params);
                }
                System.out.println("转换为的对象： " + t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 查询户型列表通过项目id
     *
     * @param projectId
     * @return
     */
    @Override
    public ResultBody selectHXListPlusPageByProjectId(String projectId) {
        String message = "";
        try {
            if (org.apache.commons.lang3.StringUtils.isEmpty(projectId)) {
                return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "项目id为空！");
            }
            return ResultBody.success(managerDao.selectHXListPlusPageByProjectId(projectId));

        }catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
        }
        return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 通过项目id查询房间列表
     *
     * @param projectId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResultBody selectRoomPlusPageByProjectId(String projectId, String pageNum, String pageSize) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(projectId)) {return ResultBody.error(ERROR_CODE, "项目id为空");}
        PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
        return ResultBody.success(new PageInfo<>(managerDao.selectRoomPlusPageByProjectId(projectId)));
    }

    /**
     * 通过房间id查询房间详情
     *
     * @param roomId
     * @return
     */
    @Override
    public ResultBody getRoomByProjectId(String roomId) {
        return ResultBody.success(managerDao.getRoomByProjectId(roomId));
    }

    /**
     * 保存编辑后的房间
     *
     * @param buildRoom
     * @return
     */
    @Override
    public ResultBody saveRoomByRoomId(BuildRoom buildRoom) {
        List<BuildRoom> mapList = beanToList(buildRoom);
        return ResultBody.success(managerDao.saveRoomByRoomId(mapList));
    }

    /**
     * 批量更改户型
     *
     * @param buildRoom
     * @param request
     * @return
     */
    @Override
    public ResultBody saveBatchRoomByRoomId(List<BuildRoom> buildRoom, HttpServletRequest request) {
        return ResultBody.success(managerDao.saveRoomByRoomId(buildRoom));
    }

    /**
     * 导出楼栋列表通过项目id
     *
     * @param projectId
     * @return
     */
    @Override
    public ResultBody exportDesignBuildListByProjectId(String projectId) {
        return null;
    }

    /**
     * 房间资料导出通过项目id
     *
     * @param projectId
     * @return
     */
    @Override
    public ResultBody exportRoomListByProjectId(String projectId) {
        return null;
    }

    /**
     * 房间面积查询列表通过项目id
     *
     * @param buildRoom
     * @return
     */
    @Override
    public ResultBody selectRoomAreaListPlusPageByProjectId(BuildRoom buildRoom) {
        PageHelper.startPage(buildRoom.getPageIndex(), buildRoom.getPageSize());
        return ResultBody.success(new PageInfo<>(managerDao.selectRoomAreaListPlusPageByProjectId(buildRoom)));
    }

    /**
     * 房间面积录入通过房间id
     *
     * @param buildRoom
     * @return
     */
    @Override
    public ResultBody importRoomAreaByRoomId(BuildRoom buildRoom) {
        return null;
    }

    /**
     * 复制楼栋通过楼栋id
     *
     * @param buildId
     * @return
     */
    @Override
    public ResultBody copyDesignBuildByBuildId(String buildId) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(buildId)) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "楼栋id为空");
        }
        return ResultBody.success(managerDao.queryDesignBuild(buildId));
    }

    /**
     * 通过项目id查询最大的楼栋列表的最大的序号值
     *
     * @param projectId
     * @return
     */
    public Integer maxOrderCode(String projectId) {
        Integer integer = managerDao.selectDesignBuildMaxOrderCode(projectId);
        return null == integer ? 1 : integer + 1;
    }

    /**
     * 粘贴楼栋
     *
     * @param designBuildModel
     * @return
     */
    @Override
    public ResultBody pasteDesignBuild(DesignBuildModel designBuildModel) {
        if (null == designBuildModel) {
            return ResultBody.error(-2001, "请求参数不能为空");
        }
        // 判断粘贴楼栋是楼栋名称不能重复(通过楼栋id和)
        Map existBuildName = managerDao.isExistBuildName(designBuildModel.getProductBuildName(), designBuildModel.getProjectId());
        if (existBuildName != null) {
            return ResultBody.error(ERROR_CODE, "楼栋名称已经存在, 请重新选择！");
        }

        designBuildModel.setOrderCode(this.maxOrderCode(designBuildModel.getProjectId()));
        // 原始楼栋id
        String old_buildId = designBuildModel.getBuildId();
        // 生成楼栋id
        String buildId = UUID.randomUUID().toString();
        designBuildModel.setBuildId(buildId);
        List<DesignBuildModel> designBuildModelList = beanToList(designBuildModel);
        // 创建楼栋
        Integer designBuildCount = managerDao.createDesignBuild(designBuildModelList);
        if (designBuildCount > 0) {
            // 根据旧楼栋id查询单元列表
            List<BuildUnit> buildUnitList = managerDao.selectUnitByBuildId(old_buildId);
            if (CollectionUtils.isNotEmpty(buildUnitList)) {
                for (BuildUnit bd : buildUnitList) {
                    bd.setBuildId(buildId);
                    System.out.println("生成的楼栋id： " + buildId);
                    bd.setUnitId(UUID.randomUUID().toString());
                }
                managerDao.saveBatchUnit(buildUnitList);
            }

            // 根据旧楼栋id查询房间列表
            List<BuildRoom> buildRoomList = managerDao.selectRoomListByBuildId(old_buildId);
            // 根据新楼栋id查询单元列表
            List<BuildUnit> newBuildUnitList = managerDao.selectUnitByBuildId(buildId);
            if (CollectionUtils.isNotEmpty(buildRoomList)) {
                if (CollectionUtils.isNotEmpty(newBuildUnitList)) {
                    for (BuildRoom bd : buildRoomList) {
                        bd.setBuildId(buildId);
                        bd.setProductBuildName(designBuildModel.getProductBuildName());
                        for (BuildUnit bu : newBuildUnitList) {
                            if (org.apache.commons.lang3.StringUtils.isNotEmpty(bd.getUnitName())) {
                                if (bd.getUnitName().equals(bu.getUnitName())) {
                                    bd.setUnitName(bu.getUnitName());
                                    bd.setUnitId(bu.getUnitId());
                                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(bd.getRoomInfo())) {
                                        String[] strings = bd.getRoomInfo().split("-");
                                        bd.setRoomInfo(designBuildModel.getProductBuildName() + "-" + strings[1] + "-" + strings[2]);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    // 进行房间的复制
                    designBuildCount = managerDao.saveBatchRoom(buildRoomList);
                }
            }
        }
        return designBuildCount > 0
                ? ResultBody.success("复制成功") : ResultBody.success("复制失败");
    }


    /**
     * 上下移动
     *
     * @param currentId
     * @param targetId
     * @param flag 0:分期 1:楼栋
     * @return
     */
    @Override
    public ResultBody move(String currentId, String targetId, Integer flag) {
        int isSucceed = 0;
        List<Map> stageBuilderList = new ArrayList<>();
        // 分期
        if (flag == 0) {
            // 查询当前序号
            Integer currentOrderCode = managerDao.getCurrentOrderCode(currentId);
            // 查询目标序号
            Integer targetOrderCode = managerDao.getCurrentOrderCode(targetId);
            Map h = new HashMap(4);
            h.put("stageId", currentId);
            h.put("orderCode", targetOrderCode);
            stageBuilderList.add(h);

            Map map = new HashMap(4);
            map.put("stageId", targetId);
            map.put("orderCode", currentOrderCode);
            stageBuilderList.add(map);
            isSucceed = managerDao.move(stageBuilderList);
        }
        // 楼栋
        else {
            // 查询当前序号
            Integer currentOrderCode = managerDao.getDesignBuildCurrentOrderCode(currentId);
            // 查询目标序号
            Integer targetOrderCode = managerDao.getDesignBuildCurrentOrderCode(targetId);

            Map h = new HashMap(4);
            h.put("buildId", currentId);
            h.put("orderCode", targetOrderCode);
            stageBuilderList.add(h);

            Map map = new HashMap(4);
            map.put("buildId", targetId);
            map.put("orderCode", currentOrderCode);
            stageBuilderList.add(map);
            isSucceed = managerDao.moveDesignBuild(stageBuilderList);
        }
        return isSucceed > 0 ? ResultBody.success("移动成功") : ResultBody.success("移动失败");
    }

    @Override
    public ResultBody selectProjectIsSyn(String projectId) {
        int count = managerDao.selectProjectIsSyn(projectId);
        return ResultBody.success(count);
    }

    @Override
    public ResultBody selectProject() {
        return ResultBody.success(managerDao.selectProject());
    }

    /**
     * 根据楼栋id查询所有的房间
     *
     * @param buildId
     * @return
     */
    @Override
    public ResultBody selectRoomListByBuildId(String buildId) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(buildId)) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "楼栋id为空");
        }
        // 查询出所有的房间信息
        List<BuildRoom> buildRoomList = managerDao.selectRoomListByBuildId(buildId);
        if (CollectionUtils.isEmpty(buildRoomList)) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "未查询到房间信息！");
        }
        // 获取楼层(序号最大值就是楼层数)
        Integer floorNo = buildRoomList.stream().max(Comparator.comparing(BuildRoom::getFloorNo)).get().getFloorNo();
        // 楼层列表
        List<RoomBuilder.FloorBuilder> floorBuilderList = new ArrayList<>();
        RoomBuilder roomBuilder = new RoomBuilder();
        // 遍历数据进行楼层分组
        for (int[] i = {1}; i[0] <= floorNo; i[0]++) {
            // 楼层名称
            String[] floorName = {""};
            RoomBuilder.FloorBuilder floorBuilder = new RoomBuilder.FloorBuilder();
            List<BuildRoom> buildRooms = new ArrayList<>();
            buildRoomList.forEach(buildRoom -> {
                if (i[0] == buildRoom.getFloorNo()) {
                    buildRooms.add(buildRoom);
                    if (org.apache.commons.lang3.StringUtils.isEmpty(floorName[0])) {
                        floorName[0] = buildRoom.getFloorName();
                    }
                }
            });
            floorBuilder.setFloorBuilder(buildRooms).setFloorName(floorName[0]);
            floorBuilderList.add(floorBuilder);
        }
        roomBuilder.setDataList(floorBuilderList);
        // 查询单元通过楼栋id
        List<BuildUnit> unitList = managerDao.selectUnitByBuildId(buildId);
        if (CollectionUtils.isNotEmpty(unitList)) {
            // 查询户数通过楼栋id
            roomBuilder.setUnitList(unitList);
        }

        return ResultBody.success(roomBuilder);
    }

    @Data
    static class RoomBuilder {
        /**
         *   楼层列表
         */
        private List<FloorBuilder> dataList;

        /**
         *   单元
         */
        private List<BuildUnit> unitList;

        @Data
        @Accessors(chain = true)
        static class FloorBuilder {
            /**
             *   房间列表
             */
            private List<BuildRoom> floorBuilder;

            /**
             *   房间名称
             */
            private String floorName;
        }
    }

    /**
     * 根据房间id删除房间和删除整列工用接口
     *
     * @param buildRoomList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody delRoomByRoomId(List<BuildRoom> buildRoomList) {
        if (CollectionUtils.isEmpty(buildRoomList)) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "请求参数为空！");
        }

        // 根据房间id查询楼栋id
        String buildId = managerDao.getBuildIdByRoomId(buildRoomList.get(0).getRoomId());
        // 查询房间信息根据楼栋id
        BuildRoom roomByRoomId = managerDao.getRoomByRoomId(buildRoomList.get(0).getRoomId());
        if (null == roomByRoomId) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "间信息为空！");
        }
        // 根据楼栋id查询楼层编号最大值判断有几层
        int flooesCount = 0;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildId)) {
            flooesCount = managerDao.getMaxFloorNo(buildId);
        }
        // 批量删除房间
        Integer count = managerDao.delRoomByRoomId(buildRoomList);

        // 如果删除整列则修改户数


        if ((flooesCount == buildRoomList.size()) && flooesCount != 0) {
            count = managerDao.updateUnit(buildRoomList.get(0).getUnitId());

            // 修改房间序号
            // 根据单元id和序号查询序号后的房间信息(不包含当前列的数据)
            /*List<BuildRoom> roomList = managerDao.selectRoomByUnitIdAndNo(roomByRoomId.getUnitId(), roomByRoomId.getNo());
            if (CollectionUtils.isNotEmpty(roomList)) {
                for (BuildRoom br : roomList) {
                    br.setNo(br.getNo() - 1);
                }
                // 更新房间序号
                count = managerDao.updateRoomNo(roomList);
                return count > 0 ? ResultBody.success("删除整列成功") : ResultBody.success("删除整列失败");
            }*/
            return count > 0 ? ResultBody.success("删除整列成功") : ResultBody.success("删除整列失败");
        }
        else {
            if (buildRoomList.size() > 1) {
                count = managerDao.updateUnit(buildRoomList.get(0).getUnitId());
                return count > 0 ? ResultBody.success("删除整列成功") : ResultBody.success("删除整列失败");
                // 修改房间序号
                // 根据单元id和序号查询序号后的房间信息(不包含当前列的数据)
                /*List<BuildRoom> roomList = managerDao.selectRoomByUnitIdAndNo(roomByRoomId.getUnitId(), roomByRoomId.getNo());
                if (CollectionUtils.isNotEmpty(roomList)) {
                    for (BuildRoom br : roomList) {
                        br.setNo(br.getNo() - 1);
                    }
                    // 更新房间序号
                    count = managerDao.updateRoomNo(roomList);
                    return count > 0 ? ResultBody.success("删除整列成功") : ResultBody.success("删除整列失败");
                }*/
            }
            else {
                // 根据单元id和房间序号查询列数据
                List<BuildRoom> buildRoomList1 = managerDao.selectRoomColumnList(roomByRoomId.getUnitId(), roomByRoomId.getNo());
                // 判断不为空
                if (CollectionUtils.isNotEmpty(buildRoomList1)) {
                    // 如果查询到的数据大于1，则说明只是删除单个房间，户数不用改变
                    if (buildRoomList1.size() > 1) {
                        // 不做任务操作，直接返回成功
                        return ResultBody.success("删除成功");
                    }
                    // 如果查询到的数据等于1，则说明改列只剩下一个房间进行删除后，户数需要改变
                    else {
                        count = managerDao.updateUnit(buildRoomList.get(0).getUnitId());
                        return count > 0 ? ResultBody.success("删除整列成功") : ResultBody.success("删除整列失败");
                        // 修改房间序号
                        // 根据单元id和序号查询序号后的房间信息(不包含当前列的数据)
                        /*List<BuildRoom> roomList = managerDao.selectRoomByUnitIdAndNo(roomByRoomId.getUnitId(), roomByRoomId.getNo());
                        if (CollectionUtils.isNotEmpty(roomList)) {
                            for (BuildRoom br : roomList) {
                                br.setNo(br.getNo() - 1);
                            }
                            // 更新房间序号
                            count = managerDao.updateRoomNo(roomList);
                            return count > 0 ? ResultBody.success("删除整列成功") : ResultBody.success("删除整列失败");
                        }*/
                    }
                }
            }
        }


        return count > 0 ? ResultBody.success("删除成功") : ResultBody.success("删除失败");
    }

    /**
     * 插入整列房间
     *
     * @param unitId
     * @param no
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody insertColumnRoom(String unitId, Integer no, HttpServletRequest request) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(unitId) && null == no) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "请求参数为空！");
        }
        String username = request.getHeader("username");
        Integer updateCount = 0;
        // 需要插入的列数据
        //List<BuildRoom> buildRoomList = new ArrayList<>();
        // 需要更新 right_cell 的数据
        // List<BuildRoom> rightCellList = new ArrayList<>();
        // 更新老数据的序号
        List<BuildRoom> updateRoomList = new ArrayList<>();
        // 根据单元id和序号查询序号后的房间信息(不包含当前列的数据)
        List<BuildRoom> roomList = managerDao.selectRoomByUnitIdAndNo(unitId, no);

        if (CollectionUtils.isNotEmpty(roomList)) {
            for (BuildRoom bd : roomList) {
                BuildRoom buildRoom = new BuildRoom().setNo(bd.getNo() + 1).setRoomId(bd.getRoomId());
                updateRoomList.add(buildRoom);

            }
            // 跟新 t_mm_build_room 的房间序号
            managerDao.updateRoomNo(updateRoomList);
            // 跟新 t_mm_build_unit 的户数
            updateCount = managerDao.updateBuildUnit(beanToList(new BuildRoom().setUnitId(unitId)));
        }
        else {
            // 跟新 t_mm_build_unit 的户数
            updateCount = managerDao.updateBuildUnit(beanToList(new BuildRoom().setUnitId(unitId)));
        }




        // 判断不为空
        /*if (CollectionUtils.isNotEmpty(roomList)) {
            for (BuildRoom bd : roomList) {
                BuildRoom buildRoom = new BuildRoom()
                .setNo(bd.getNo() + 1).setRoomId(bd.getRoomId());
                updateRoomList.add(buildRoom);


                buildRoom.setUnitId(roomList.get(0).getUnitId())
                        .setUnitName(roomList.get(0).getUnitName())
                        .setUnitNo(bd.getUnitNo())
                        .setNo(no)
                        .setFloorName(bd.getFloorName())
                        .setFloorNo(bd.getFloorNo())
                        .setCreateUser(username)
                        .setYsBldArea(BigDecimal.ZERO)
                        .setYsTnArea(BigDecimal.ZERO)
                        .setBuildId(roomList.get(0).getBuildId())
                        .setProductBuildName(roomList.get(0).getProductBuildName())
                        .setProjectId(roomList.get(0).getProjectId());
                buildRoomList.add(buildRoom);
            }

            if (managerDao.saveBatchRoom(buildRoomList) > 0) {
                // 跟新 t_mm_build_unit 的户数
                managerDao.updateBuildUnit(beanToList(new BuildRoom().setUnitId(unitId)));
                // 跟新 t_mm_build_room 的房间序号
                updateCount = managerDao.updateRoomNo(updateRoomList);
            }
        }*/
        // 为空
        /*else {
            // 根据单元id查询房间信息
            List<BuildRoom> roomListByUnitId = managerDao.selectRoomListByUnitId(unitId);
            if (CollectionUtils.isNotEmpty(roomListByUnitId)) {
                BuildRoom room = roomListByUnitId.get(roomListByUnitId.size() - 1);
                BuildRoom buildRoom = new BuildRoom()
                        .setNo(room.getNo() + 1)
                        .setUnitId(room.getUnitId())
                        .setUnitName(room.getUnitName())
                        .setUnitNo(room.getUnitNo())
                        .setFloorName(room.getFloorName())
                        .setFloorNo(room.getFloorNo())
                        .setCreateUser(username)
                        .setYsBldArea(BigDecimal.ZERO)
                        .setYsTnArea(BigDecimal.ZERO)
                        .setBuildId(room.getBuildId())
                        .setProductBuildName(room.getProductBuildName())
                        .setProjectId(room.getProjectId());
                buildRoomList.add(buildRoom);

                if (managerDao.saveBatchRoom(buildRoomList) > 0) {
                    // 跟新 t_mm_build_unit 的户数
                    updateCount = managerDao.updateBuildUnit(beanToList(new BuildRoom().setUnitId(unitId)));
                }
            }
        }*/

        return updateCount > 0
                ? ResultBody.success("插入成功") : ResultBody.success("插入失败");
    }

    /**
     * 合并房间
     *
     * @param currentRoomId
     * @param targetRoomId
     * @param moveFlag 0: 右合并 1: 下合并
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody mergeRoom(String currentRoomId, String targetRoomId, Integer moveFlag) {
        int count = 0;
        managerDao.delRoom(targetRoomId);
        if (0 == moveFlag) {
            count = managerDao.rightMoveRoom(currentRoomId);
        }
        else if (1 == moveFlag) {
            count = managerDao.downMoveRoom(currentRoomId);
        }
        return count > 0 ? ResultBody.success("移动成功") : ResultBody.success("移动失败");
    }

    /**
     * 房间拆分
     *
     * @param map
     * @return
     */
    @Override
    public ResultBody roomSplit(Map map) {
        if (null == map && org.apache.commons.lang3.StringUtils.isEmpty((String) map.get("currentRoomId"))) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "请求参数为空！");
        }
        // 根据房间id查询房间信息
        BuildRoom buildRoomInfo = managerDao.getRoomByRoomId((String) map.get("currentRoomId"));
        if (null == buildRoomInfo) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "房间信息为空！");
        }
        int currentRightCell = 0, currentDownCell = 0;
        // 左右拆分
        if (buildRoomInfo.getRightCell() > 0) {
            // 合并几个房间
            int rightCell = buildRoomInfo.getRightCell();
            currentRightCell = rightCell;
            buildRoomInfo.setRightCell(0);

            // 房间简称加上合并几个就是最后合并的房间简称3
            if (null != buildRoomInfo.getNo()) {
                Integer no = buildRoomInfo.getNo() + rightCell;
                buildRoomInfo.setNo(no);
                // Integer roomCode = Integer.valueOf(buildRoomInfo.getRoomNo()) + rightCell;
                if (no > 9) {
                    buildRoomInfo.setRoomCode(no.toString());
                    buildRoomInfo.setRoomNo(no.toString());
                    buildRoomInfo.setRoomName(buildRoomInfo.getFloorNo().toString() + no.toString());
                }
                else {
                    buildRoomInfo.setRoomCode("0" + no);
                    buildRoomInfo.setRoomNo("0" + no.toString());
                    buildRoomInfo.setRoomName(buildRoomInfo.getFloorNo().toString() + "0" + no.toString());
                }

            }

            // 拼接楼栋名称和单元名称
            String name = managerDao.getDesignBuildAndUnitByRoomId((String) map.get("currentRoomId"));
            buildRoomInfo.setRoomInfo(name + buildRoomInfo.getRoomName());

            // 更新当前房间的right_cell - 1
            BuildRoom buildRoom = new BuildRoom().setRoomId((String) map.get("currentRoomId")).setRightCell(currentRightCell - 1);
            managerDao.updateBuildRoomByRoomId(buildRoom);
        }
        // 上下拆分
        else {
            // 合并几个房间
            int downCell = buildRoomInfo.getDownCell();
            currentDownCell = downCell;
            buildRoomInfo.setDownCell(0);

            // 房间简称加上合并几个就是最后合并的房间简称3
            if (null != buildRoomInfo.getFloorNo()) {
                Integer floorNo = buildRoomInfo.getFloorNo() - downCell;
                buildRoomInfo.setFloorNo(floorNo);
                if (floorNo > 9) {
                    buildRoomInfo.setRoomCode(floorNo.toString() + buildRoomInfo.getNo());
                    buildRoomInfo.setRoomNo(floorNo.toString());
                    buildRoomInfo.setRoomName(floorNo.toString() + buildRoomInfo.getNo());
                }
                else {
                    buildRoomInfo.setRoomCode("0" + buildRoomInfo.getNo());
                    buildRoomInfo.setRoomNo("0" + buildRoomInfo.getNo());
                    buildRoomInfo.setRoomName(floorNo.toString() + "0" + buildRoomInfo.getNo());
                }

            }

            // 拼接楼栋名称和单元名称
            String[] strings = buildRoomInfo.getRoomInfo().split("-");
            buildRoomInfo.setRoomInfo(strings[0] + "-" + strings[1] + "-" + buildRoomInfo.getRoomName());

            // 更新当前房间的right_cell - 1
            BuildRoom buildRoom = new BuildRoom().setRoomId((String) map.get("currentRoomId")).setDownCell(currentDownCell - 1);
            managerDao.updateBuildRoomByRoomId(buildRoom);
        }

        return managerDao.saveBatchRoom(beanToList(buildRoomInfo)) > 0
                ? ResultBody.success("拆分成功") : ResultBody.success("拆分失败");
    }

    /**
     * 新增户型字典表
     *
     * @param buildHx
     * @param request
     * @return
     */
    @Override
    public ResultBody saveBuildHx(BuildHx buildHx, HttpServletRequest request) {
        buildHx.setId(UUID.randomUUID().toString());
        buildHx.setCreator(request.getHeader("username"));
        return managerDao.saveBuildHx(buildHx) > 0
                ? ResultBody.success("新增户型成功") : ResultBody.success("新增户型失败");
    }

    /**
     * 查询户型通过户型id
     *
     * @param id
     * @return
     */
    @Override
    public ResultBody getBuildHxById(String id) {
        return ResultBody.success(managerDao.getBuildHxById(id));
    }

    /**
     * 保存编辑后的户型
     *
     * @param buildHx
     * @param request
     * @return
     */
    @Override
    public ResultBody updateBuildHx(BuildHx buildHx, HttpServletRequest request) {
        buildHx.setModified(request.getHeader("username"));
        return managerDao.updateBuildHx(buildHx) > 0
                ? ResultBody.success("修改户型成功") : ResultBody.success("修改户型失败");
    }

    /**
     * 通过主键删除户型
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public ResultBody deleteById(String id) {
        return managerDao.deleteById(id) > 0 ? ResultBody.success("删除成功")
                : ResultBody.success("删除失败");
    }

    /**
     * 保存单独的房间信息
     *
     * @param buildRoom
     * @return
     */
    @Override
    public ResultBody updateBuildRoomByRoomId(BuildRoom buildRoom) {
        return managerDao.updateBuildRoomByRoomId(buildRoom) > 0 ? ResultBody.success("保存成功")
                : ResultBody.success("保存失败");
    }

    /**
     * 放盘---保存放盘数据
     *
     * @param releaseDishRequest
     * @return
     */
    @Override
    public ResultBody updateReleaseDish(ReleaseDishRequest releaseDishRequest) {
        Integer updateStatus = 0;
        // 请求的数据不为空
        if (CollectionUtils.isNotEmpty(releaseDishRequest.getBuildRoomList())) {
            // 保存数据
            updateStatus = managerDao.updateReleaseDish(releaseDishRequest.getBuildRoomList());
        }
        return updateStatus > 0 ? ResultBody.success("保存成功") : ResultBody.success("保存失败");
    }

    /**
     * 新增单独房间
     *
     * @param buildRoom
     * @param request
     * @return
     */
    @Override
    public ResultBody saveBuildRoom(BuildRoom buildRoom, HttpServletRequest request) {
        buildRoom.setCreateUser(request.getHeader("username"));
        return ResultBody.success(managerDao.saveBatchRoom(beanToList(buildRoom)));
    }

    /**
     * 房间面积列表查询
     *
     * @param roomTaskDto
     * @return
     */
    @Override
    public ResultBody selectRoomAreaTaskPlusPageList(RoomTaskDto roomTaskDto) {
        if (null == roomTaskDto) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "请求参数为空");
        }
        PageHelper.startPage(roomTaskDto.getPageIndex(), roomTaskDto.getPageSize());
        return ResultBody.success(new PageInfo<>(managerDao.selectRoomAreaTaskPlusPageList(roomTaskDto)));
    }

    /**
     * 面积模板---项目下拉框查询
     *
     * @return
     */
    @Override
    public ResultBody selectProjectList() {
        return ResultBody.success(managerDao.selectProjectList());
    }

    /**
     * 通过项目查询分期，楼栋列表
     *
     * @param projectId
     * @return
     */
    @Override
    public ResultBody selectRoomAreaTemplates(String projectId) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(projectId)) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "项目id为空");
        }
        /*StageModel stageModel = new StageModel().setProjectId(projectId);
        List<StageModel> stageModelList = managerDao.selectStagePlusPageList(stageModel);
        if (CollectionUtils.isNotEmpty(stageModelList)) {
            for (StageModel sm : stageModelList) {
                List<DesignBuildModel> designBuildModelList = managerDao.selectDesignBuildByStageId(sm.getStageId());
                if (CollectionUtils.isNotEmpty(designBuildModelList)) {
                    sm.setDesignBuildModelList(designBuildModelList);
                }
            }
        }
        return ResultBody.success(stageModelList);*/
        return ResultBody.success(managerDao.selectDesignBuildByProjectId(projectId));
    }

    /**
     * 模板导出---导出面积模板
     *
     * @param buildId
     * @param request
     * @param response
     * @return
     */
    @Override
    public void exportAreaTemplate(String buildId, HttpServletRequest request, HttpServletResponse response) {
        FileInputStream templateInputStream = null;
        Workbook targetWorkBook = null;
        XSSFSheet targetSheet = null;
        FileOutputStream fileOutputStream = null;
        File templateFile;
        XSSFRow positionRow;
        try {
            List<BuildRoom> buildRoomList = managerDao.buildRoomListByBuildId(buildId);
            DesignBuildModel designBuildModel = managerDao.getDesignBuildByBuildId(buildId);
            // 查询项目id
            String _projectName = managerDao.getProjectName(designBuildModel.getProjectId());
            System.out.println("数据为： " + designBuildModel);
            // 路径
            // String path = request.getServletContext().getRealPath("/");
            String path = "/app/netdata/hourse/area.xlsx";
            //String path = "C:\\Users\\yuzhiyong\\AppData\\Local\\Temp\\tomcat-docbase.7849858583715565332.8001\\";
            System.out.println("路径为 " + path);
            // String templatePath =  "TemplateExcel" + File.separator + "area.xlsx";
            //path = path + templatePath;
            templateFile = new File(path);
            // 检验是否存在文件 不存在创建抛异常
            if (!templateFile.exists()) {
                throw new BadRequestException(1001, "未读取到配置的导出模版，请先配置导出模版!");
            }

            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "楼栋面积模板");
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();
            //清空原模板剩余数据
            for (int i = 7; i <= maxTemplateRows; i++) {
                Row removeRow = targetSheet.getRow(i);
                if (removeRow != null) {
                    targetSheet.removeRow(removeRow);
                }
            }
            String planName = "楼栋模板" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            //导出临时文件文件夹。
            String targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            String targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = returnResponseInfo(response, planName);
            // 验证目标文件夹是否存在
            verityTargetFile(targetFileDir);
            int startRow = 7, startRows = startRow;
            // 遍历数据
            for (BuildRoom br : buildRoomList) {
                br.setRow(startRow++);
                br.setYsBldArea(null);
                br.setYsTnArea(null);
                br.setScBldArea(null);
                br.setScTnArea(null);
            }
            Map<String, Object> cellStyle = cellStyle(targetWorkBook);
            CellStyle style1 = (CellStyle) cellStyle.get("style1");
            CellStyle style2 = (CellStyle) cellStyle.get("style2");

            //System.out.println("json格式： " + JSON.toJSONString(buildRoomList));

            positionRow = targetSheet.createRow(4);
            positionRow.setHeightInPoints(20);
            XSSFCell cell = positionRow.createCell(0);
            cell.setCellValue("项目名称");
            cell = positionRow.createCell(1);
            cell.setCellValue(_projectName);

            positionRow = targetSheet.createRow(5);
            positionRow.setHeightInPoints(20);
            XSSFCell cell1 = positionRow.createCell(0);
            cell1.setCellValue("项目标识");
            cell1 = positionRow.createCell(1);
            cell1.setCellValue(designBuildModel.getProjectId());

            for (BuildRoom br : buildRoomList) {
                positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                setDatTargetDataToCell(positionRow, br, style2, style1, 1);
                startRows = startRows + 1;
            }
            targetSheet.setRowSumsBelow(false);
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            // targetWorkBook.write(fileOutputStream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (templateInputStream != null) {
                    templateInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 模板导出---选择楼栋模板
     *
     * @param buildId
     * @param request
     * @param response
     * @return
     */
    @Override
    public void exportStandardTemplate(String buildId, HttpServletRequest request, HttpServletResponse response) {
        FileInputStream templateInputStream = null;
        Workbook targetWorkBook = null;
        XSSFSheet targetSheet = null;
        FileOutputStream fileOutputStream = null;
        File templateFile;
        XSSFRow positionRow;
        try {
            List<BuildRoom> buildRoomList = managerDao.buildRoomListByBuildId(buildId);
            DesignBuildModel designBuildModel = managerDao.getDesignBuildByBuildId(buildId);

            // 路径
            // String path = "D:\\yu\\mianjia.xlsx";
            //String path = request.getServletContext().getRealPath("/");
            String path = "/app/netdata/hourse/mianjia.xlsx";
            System.out.println("路径为 " + path);
            //String templatePath = "TemplateExcel" + File.separator + "mianjia.xlsx";
            templateFile = new File(path);
            // 检验是否存在文件 不存在创建抛异常
            verityFile(templateFile);

            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "楼栋面积模板");
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();
            //清空原模板剩余数据
            for (int i = 1; i <= maxTemplateRows; i++) {
                Row removeRow = targetSheet.getRow(i);
                if (removeRow != null) {
                    targetSheet.removeRow(removeRow);
                }
            }
            String planName = "楼栋模板" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            //导出临时文件文件夹。
            String targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            String targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = returnResponseInfo(response, planName);
            // 验证目标文件夹是否存在
            verityTargetFile(targetFileDir);
            int startRow = 1, startRows = startRow;
            // 遍历数据
            for (BuildRoom br : buildRoomList) {
                br.setRow(startRow++);
                br.setMjBldPrice(null);
                br.setMjTnPrice(null);
                br.setMjTotal(null);
                br.setStageName(designBuildModel.getStageName());
                br.setBldType(designBuildModel.getBldType());
                br.setProductName(designBuildModel.getProductName());
            }
            Map<String, Object> cellStyle = cellStyle(targetWorkBook);
            CellStyle style1 = (CellStyle) cellStyle.get("style1");
            CellStyle style2 = (CellStyle) cellStyle.get("style2");

            String s = JSON.toJSONString(buildRoomList);
            System.out.println("json格式： " + s);
            for (BuildRoom br : buildRoomList) {
                positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                setDatTargetDataToCell(positionRow, br, style2, style1, 2);
                startRows = startRows + 1;
            }
            targetSheet.setColumnHidden(19, true);
            targetSheet.setColumnHidden(0, true);
            targetSheet.setColumnHidden(20, true);
            targetSheet.setRowSumsBelow(false);
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            //targetWorkBook.write(fileOutputStream);
        } catch (ServiceException se) {
            se.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (templateInputStream != null) {
                    templateInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 面积导入
     *
     * @param file
     * @param applyName
     * @param applyTime
     * @param remark
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody importArea(MultipartFile file, String applyName, String applyTime, String remark, HttpServletRequest request) {
        try {
            String username = request.getHeader("username");
            // 第二个参数为空会默认创建一个后缀名 ".tmp"
            File f = File.createTempFile("tmp", null);
            file.transferTo(f);
            // 在JVM退出时删除文件
            f.deleteOnExit();
            FileInputStream fileInputStream;
            XSSFWorkbook workBook;
            Sheet planSheet;
            fileInputStream = new FileInputStream(f);
            workBook = new XSSFWorkbook(fileInputStream);
            planSheet = workBook.getSheetAt(0);
            //导入数据总行数 如果sheet中一行数据都没有则返回-1，只有第一行有数据则返回0，最后有数据的行是第n行则返回 n-1
            int planSheetTotalRows = planSheet.getLastRowNum();
            int startRow = 7;
            // 项目级别单独存储
            List<BuildRoom> buildRoomList = new ArrayList<>();
            // 获取项目名称
            Row row2 = planSheet.getRow(4);
            Cell cell = row2.getCell(1);
            String projectName = FileUtils.getCellValue(cell, null);
            System.out.println("项目名称： " + projectName);

            // 获取项目id
            Row row1 = planSheet.getRow(5);
            Cell row1Cell = row1.getCell(1);
            String projectId = FileUtils.getCellValue(row1Cell, null);

            // 用于判断是预售录入还是实测录入
            Row row = planSheet.getRow(7);
            // 预售建筑面积
            Cell cell0 = row.getCell(2);
            String cellValue1 = FileUtils.getCellValue(cell0, null);

            // 预售套内面积
            cell0 = row.getCell(3);
            String cellValue2 = FileUtils.getCellValue(cell0, null);

            BigDecimal num = new BigDecimal(cellValue1);
            BigDecimal num1 = new BigDecimal(cellValue2);
            // _flag: {0: 录入预售 1: 录入实测}
            int _flag = 0;
            if (num.compareTo(BigDecimal.ZERO) != 1 && num1.compareTo(BigDecimal.ZERO) != 1) {
                _flag = 1;
            }
            for (int i = startRow; i <= planSheetTotalRows; i++) {
                row = planSheet.getRow(i);
                getCellValue(row, buildRoomList, 1, _flag, projectId);
            }

            // 任务id
            String[] _task_id = {UUID.randomUUID().toString()};

            // 添加数据到 t_mm_build_room_plus
            buildRoomList.forEach(buildRoom -> buildRoom.setTaskId(_task_id[0]));
            managerDao.saveBatchRoomToPlusTable(buildRoomList);

            // 获取房间id
            String roomId = buildRoomList.get(0).getRoomId();
            if (org.apache.commons.lang3.StringUtils.isEmpty(roomId)) {
                roomId = buildRoomList.get(1).getRoomId();
            }
            // 保存数据到房间任务表
            RoomTask roomTask = new RoomTask()
                    .setTaskId(_task_id[0]);
            // 根据房间id查询楼栋id
            String builid = managerDao.getBuildIdByRoomId(roomId);
            // 原预售建筑面积, 原预售套内面积, 原实测建筑面积, 原实测套内面积
            BigDecimal[] beforeAreaData = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
            // 根据build查询房间信息(t_mm_build_room)
            List<BuildRoom> buildRooms = managerDao.getAreaDataByBuildId(builid);


            // 修改的房间数据
            List<BuildRoom> newAreaData = new ArrayList<>();
            // 原来的房间数据
            List<BuildRoom> oldAreaData = new ArrayList<>();
            // 调整的套数
            Integer account = 0;
            // beforeYsBldArea: { 原预售建筑面积, 原预售套内面积, 原实测建筑面积, 原实测套内面积}
            // 录入预售
            if (buildRoomList.get(0).getType() == 0) {
                roomTask.setBeforeYsBldArea(beforeAreaData[0])
                        .setBeforeYsTnArea(beforeAreaData[1]);

                // 导入的数据和原来的数据进行比较选出调整的套数
                if (CollectionUtils.isNotEmpty(buildRoomList) && CollectionUtils.isNotEmpty(buildRooms)) {
                    for (BuildRoom rm : buildRoomList) {
                        for (BuildRoom br : buildRooms) {
                            if (rm.getRoomId().equals(br.getRoomId())) {
                                if (rm.getYsBldArea().compareTo(br.getYsBldArea()) != 0 || rm.getYsTnArea().compareTo(br.getYsTnArea()) != 0) {
                                    account = account + 1;
                                    newAreaData.add(rm);
                                    oldAreaData.add(br);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            // 录入实测
            else if (buildRoomList.get(0).getType() == 1) {
                roomTask.setBeforeScBldArea(beforeAreaData[2])
                        .setBeforeScTnArea(beforeAreaData[3]);

                // 导入的数据和原来的数据进行比较选出调整的套数
                if (CollectionUtils.isNotEmpty(buildRoomList) && CollectionUtils.isNotEmpty(buildRooms)) {
                    for (BuildRoom rm : buildRoomList) {
                        for (BuildRoom br : buildRooms) {
                            if (rm.getRoomId().equals(br.getRoomId())) {
                                if (rm.getScBldArea().compareTo(br.getScBldArea()) != 0 || rm.getScTnArea().compareTo(br.getScTnArea()) != 0) {
                                    account = account + 1;
                                    newAreaData.add(rm);
                                    oldAreaData.add(br);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // 取原来的面积数据
            if (CollectionUtils.isNotEmpty(oldAreaData)) {
                oldAreaData.forEach(buildRoom -> {
                    if (buildRoom.getYsBldArea() != null) {
                        beforeAreaData[0] = beforeAreaData[0].add(buildRoom.getYsBldArea());
                    }
                    if (buildRoom.getYsTnArea() != null) {
                        beforeAreaData[1] = beforeAreaData[1].add(buildRoom.getYsTnArea());
                    }
                    if (buildRoom.getScBldArea() != null) {
                        beforeAreaData[2] = beforeAreaData[2].add(buildRoom.getScBldArea());
                    }
                    if (buildRoom.getScTnArea() != null) {
                        beforeAreaData[3] = beforeAreaData[3].add(buildRoom.getScTnArea());
                    }
                });
            }

            // 调整后的面积: {调整后的预售建筑面积,调整后的预售套内面积,调整后的实测建筑面积,调整后的实测套内面积,}
            BigDecimal[] afterAreaData = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
            // 录入的数据
            for (BuildRoom br : newAreaData) {
                br.setCreateUser(username);
                br.setTaskId(_task_id[0]);
                if (br.getYsBldArea() != null) {
                    afterAreaData[0] = afterAreaData[0].add(br.getYsBldArea());
                }
                if (br.getYsTnArea() != null) {
                    afterAreaData[1] = afterAreaData[1].add(br.getYsTnArea());
                }
                if (br.getScBldArea() != null) {
                    afterAreaData[2] = afterAreaData[2].add(br.getScBldArea());
                }
                if (br.getScTnArea() != null) {
                    afterAreaData[3] = afterAreaData[3].add(br.getScTnArea());
                }
            }

            // 任务表的数据填充完毕
            roomTask.setAgent(username)
                    // 状态 0：草稿 1：已执行
                    .setStatus(0).setProjectName(projectName)
                    .setApplyName(applyName).setApplyTime(applyTime)
                    .setRemark(remark).setAdjustNum(account).setProjectId(projectId);

            // 录入的是预售面积
            if (buildRoomList.get(0).getType() == 0) {
                roomTask.setBeforeYsBldArea(beforeAreaData[0])
                        .setBeforeYsTnArea(beforeAreaData[1]);
                roomTask.setAfterYsBldArea(afterAreaData[0])
                        .setAfterYsTnArea(afterAreaData[1]);
                managerDao.saveRoomAreaTask(roomTask);
            }
            // 录入的是实测面积
            else if (buildRoomList.get(0).getType() == 1) {
                roomTask.setBeforeScBldArea(beforeAreaData[2])
                        .setBeforeScTnArea(beforeAreaData[3]);
                roomTask.setAfterScBldArea(afterAreaData[2])
                        .setAfterScTnArea(afterAreaData[3]);
                managerDao.saveRoomAreaTask(roomTask);
            }
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResultBody.success("导入成功");
    }

    private void getCellValue(Row row, List<BuildRoom> buildRoomList, int flag, int type, String projectId) {
        // flag 1:面积录入 2:   type: {0: 录入预售 1: 录入实测}
        BuildRoom buildRoom = new BuildRoom();
        if (flag == 1) {
            // 房间id
            Cell cell0 = row.getCell(0);
            String cellValue0 = FileUtils.getCellValue(cell0, null);
            buildRoom.setRoomId(cellValue0).setProjectId(projectId);
            if (type == 0) {
                // 预售建筑面积
                cell0 = row.getCell(2);
                String cellValue1 = FileUtils.getCellValue(cell0, null);

                // 预售套内面积
                cell0 = row.getCell(3);
                String cellValue2 = FileUtils.getCellValue(cell0, null);
                buildRoom.setYsBldArea(new BigDecimal(cellValue1)).setYsTnArea(new BigDecimal(cellValue2));
                buildRoom.setType(0);
                buildRoomList.add(buildRoom);

            }
            else if (type == 1) {
                // 实测建筑面积
                cell0 = row.getCell(4);
                String cellValue1 = FileUtils.getCellValue(cell0, null);

                // 实测套内面积
                cell0 = row.getCell(5);
                String cellValue2 = FileUtils.getCellValue(cell0, null);
                buildRoom.setScBldArea(new BigDecimal(cellValue1)).setScTnArea(new BigDecimal(cellValue2));

                buildRoom.setType(1);
                buildRoomList.add(buildRoom);
            }
        }
        else if (flag == 2) {
            // 房间id
            Cell cell = row.getCell(0);
            String cellValue0 = FileUtils.getCellValue(cell, null);

            // 面价建筑单价
            cell = row.getCell(16);
            String cellValue1 = FileUtils.getCellValue(cell, null);

            // 面价套内单价
            cell = row.getCell(17);
            String cellValue2 = FileUtils.getCellValue(cell, null);

            // 面价总价
            cell = row.getCell(18);
            String cellValue3 = FileUtils.getCellValue(cell, null);

            // 项目id
            cell = row.getCell(19);
            String cellValue4 = FileUtils.getCellValue(cell, null);

            // 楼栋id
            cell = row.getCell(20);
            String cellValue5 = FileUtils.getCellValue(cell, null);

            buildRoom.setRoomId(cellValue0)
                    .setMjBldPrice(new BigDecimal(cellValue1))
                    .setMjTnPrice(new BigDecimal(cellValue2))
                    .setMjTotal(new BigDecimal(cellValue3))
                    .setProjectId(cellValue4).setBuildId(cellValue5);
            buildRoomList.add(buildRoom);
        }
    }

    private void getCellValueForStandard(Row row, List<BuildRoom> buildRoomList, Integer valuationType, Integer priceStandard) {
        // 计价方式 1:建筑面积 2:套内面积 3:套 价格标准 1:以面价总价为准 2:以建筑单价为准 3:以套内单价为准 4:以底价总价为准
        BuildRoom buildRoom = new BuildRoom();

        // 房间标识
        Cell cell00 = row.getCell(0);
        String cellValue0 = FileUtils.getCellValue(cell00, null);
        buildRoom.setRoomId(cellValue0);

        // 项目标识
        Cell cell11 = row.getCell(19);
        String cellValue11 = FileUtils.getCellValue(cell11, null);
        buildRoom.setProjectId(cellValue11);

        // 楼栋标识
        Cell cell22 = row.getCell(20);
        String cellValue22 = FileUtils.getCellValue(cell22, null);
        buildRoom.setBuildId(cellValue22);

        // 计价方式 1:建筑面积
        if (valuationType == 1) {
            buildRoom.setValuationType(1);
            // 价格标准 1:以面价总价为准
            if (priceStandard == 1) {
                buildRoom.setPriceStandard(1);
                // 面价总价
                Cell cell0 = row.getCell(18);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setMjTotal(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
            // 价格标准 2:以建筑单价为准
            else if (priceStandard == 2) {
                buildRoom.setPriceStandard(2);
                // 面价建筑单价
                Cell cell0 = row.getCell(16);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setMjBldPrice(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
        }
        // 计价方式 2:套内面积
        else if (valuationType == 2) {
            buildRoom.setValuationType(2);
            // 价格标准 1:以面价总价为准
            if (priceStandard == 1) {
                buildRoom.setPriceStandard(1);
                // 面价总价
                Cell cell0 = row.getCell(18);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setMjTotal(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
            // 价格标准 3:以套内单价为准
            else if (priceStandard == 3) {
                buildRoom.setPriceStandard(3);
                // 面价套内单价
                Cell cell0 = row.getCell(17);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setMjTnPrice(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
        }
        // 计价方式 3:套
        else if (valuationType == 3) {
            buildRoom.setValuationType(3);
            // 价格标准 1:以面价总价为准
            if (priceStandard == 1) {
                buildRoom.setPriceStandard(1);
                // 面价总价
                Cell cell0 = row.getCell(18);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setMjTotal(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
        }
    }

    /**
     * 验证目标文件夹是否存在
     *
     * @param targetFileDir
     */
    private void verityTargetFile(String targetFileDir) {
        File targetFileDirFile = new File(targetFileDir);
        if (!targetFileDirFile.exists()) {
            targetFileDirFile.mkdirs();
        }
    }

    private void close(FileInputStream inputStream, FileOutputStream outputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDatTargetDataToCell(XSSFRow positionRow, BuildRoom buildRoom, CellStyle style1, CellStyle style2, int flag) {
        /**
         * 单元格样色共三种：
         * 1.白底 居中加粗 style2
         * 2.灰底 居中 加粗 style1
         */
        int startColumn = 0;

        if (flag == 1) {
            // 房间标识
            XSSFCell cell_1 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_1.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getRoomId())) {
                cell_1.setCellValue(buildRoom.getRoomId());
            } else {
                cell_1.setCellValue("");
            }

            // 房间标识
            XSSFCell cell_2 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_2.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getRoomInfo())) {
                cell_2.setCellValue(buildRoom.getRoomInfo());
            } else {
                cell_2.setCellValue("");
            }

            // 预售建筑面积
            XSSFCell cell_3 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_3.setCellStyle(style2);
            if (null != buildRoom.getYsBldArea()) {
                cell_3.setCellValue(buildRoom.getYsBldArea().doubleValue());
            } else {
                cell_3.setCellValue(0.00);
            }

            // 预售套内面积
            XSSFCell cell_4 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_4.setCellStyle(style2);
            if (null != buildRoom.getYsTnArea()) {
                cell_4.setCellValue(buildRoom.getYsTnArea().doubleValue());
            } else {
                cell_4.setCellValue(0.00);
            }

            // 实测建筑面积
            XSSFCell cell_5 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_5.setCellStyle(style2);
            if (null != buildRoom.getScBldArea()) {
                cell_5.setCellValue(buildRoom.getScBldArea().doubleValue());
            } else {
                cell_5.setCellValue(0.00);
            }

            // 实测套内面积
            XSSFCell cell_6 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_6.setCellStyle(style2);
            if (null != buildRoom.getScTnArea()) {
                cell_6.setCellValue(buildRoom.getScTnArea().doubleValue());
            } else {
                cell_6.setCellValue(0.00);
            }
        }
        else if (flag == 2) {
            // 房间id
            XSSFCell cell_1 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_1.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getRoomId())) {
                cell_1.setCellValue(buildRoom.getRoomId());
            } else {
                cell_1.setCellValue("");
            }

            // 分期名称
            XSSFCell cell0 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell0.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getStageName())) {
                cell0.setCellValue(buildRoom.getStageName());
            } else {
                cell0.setCellValue("");
            }

            // 产品类型
            XSSFCell cell1 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell1.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getProductName())) {
                cell1.setCellValue(buildRoom.getProductName());
            } else {
                cell1.setCellValue("");
            }

            // 房间全称
            XSSFCell cell2 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell2.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getRoomInfo())) {
                cell2.setCellValue(buildRoom.getRoomInfo());
            } else {
                cell2.setCellValue("");
            }

            // 楼栋名称
            XSSFCell cell3 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell3.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getProductBuildName())) {
                cell3.setCellValue(buildRoom.getProductBuildName());
            } else {
                cell3.setCellValue("");
            }

            // 楼层
            XSSFCell cell4 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell4.setCellStyle(style1);
            if (null != buildRoom.getFloorNo()) {
                cell4.setCellValue(buildRoom.getFloorNo());
            }

            // 房号
            XSSFCell cell5 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell5.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getRoomName())) {
                cell5.setCellValue(buildRoom.getRoomName());
            } else {
                cell5.setCellValue("");
            }

            // 户型
            XSSFCell cell6 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell6.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getHxName())) {
                cell6.setCellValue(buildRoom.getHxName());
            } else {
                cell6.setCellValue("");
            }


            // 建筑面积
            XSSFCell cell7 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell7.setCellStyle(style1);
            if (null != buildRoom.getBldArea()) {
                cell7.setCellValue(buildRoom.getBldArea().doubleValue());
            } else {
                cell7.setCellValue(0.00);
            }

            // 套内面积
            XSSFCell cell8 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell8.setCellStyle(style1);
            if (null != buildRoom.getTnArea()) {
                cell8.setCellValue(buildRoom.getTnArea().doubleValue());
            } else {
                cell8.setCellValue(0.00);
            }

            // 低价建筑单价
            XSSFCell cell9 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell9.setCellStyle(style1);
            if (null != buildRoom.getDjBldPrice()) {
                cell9.setCellValue(buildRoom.getDjBldPrice().doubleValue());
            } else {
                cell9.setCellValue(0.00);
            }

            // 底价套内单价
            XSSFCell cell10 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell10.setCellStyle(style1);
            if (null != buildRoom.getDjTnPrice()) {
                cell10.setCellValue(buildRoom.getDjTnPrice().doubleValue());
            } else {
                cell10.setCellValue(0.00);
            }

            // 底价总价
            XSSFCell cell11 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell11.setCellStyle(style1);
            if (null != buildRoom.getDjTotal()) {
                cell11.setCellValue(buildRoom.getDjTotal().doubleValue());
            } else {
                cell11.setCellValue(0.00);
            }

            // 原面价建筑单价
            XSSFCell cell12 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell12.setCellStyle(style1);
            if (null != buildRoom.getDjTotal()) {
                cell12.setCellValue(buildRoom.getDjTotal().doubleValue());
            } else {
                cell12.setCellValue(0.00);
            }

            // 原面价套内单价
            XSSFCell cell13 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell13.setCellStyle(style1);
            if (null != buildRoom.getDjTotal()) {
                cell13.setCellValue(buildRoom.getDjTotal().doubleValue());
            } else {
                cell13.setCellValue(0.00);
            }

            // 原面价总价
            XSSFCell cell14 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell14.setCellStyle(style1);
            if (null != buildRoom.getDjTotal()) {
                cell14.setCellValue(buildRoom.getDjTotal().doubleValue());
            } else {
                cell14.setCellValue(0.00);
            }

            // 面价建筑单价
            XSSFCell cell15 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell15.setCellStyle(style2);
            if (null != buildRoom.getMjBldPrice()) {
                cell15.setCellValue(buildRoom.getMjBldPrice().doubleValue());
            } else {
                cell15.setCellValue(0.00);
            }

            // 面价套内单价
            XSSFCell cell16 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell16.setCellStyle(style2);
            if (null != buildRoom.getMjTnPrice()) {
                cell16.setCellValue(buildRoom.getMjTnPrice().doubleValue());
            } else {
                cell16.setCellValue(0.00);
            }

            // 面价总价
            XSSFCell cell17 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell17.setCellStyle(style2);
            if (null != buildRoom.getMjTotal()) {
                cell17.setCellValue(buildRoom.getMjTotal().doubleValue());
            } else {
                cell17.setCellValue(0.00);
            }

            // 项目标识
            XSSFCell cell18 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell18.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getProjectId())) {
                cell18.setCellValue(buildRoom.getProjectId());
            } else {
                cell18.setCellValue("");
            }

            // 楼栋标识
            XSSFCell cell19 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell19.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getBuildId())) {
                cell19.setCellValue(buildRoom.getBuildId());
            } else {
                cell19.setCellValue("");
            }

        }
        else if (flag == 3) {
            // 房间id
            XSSFCell cell_1 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_1.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getRoomId())) {
                cell_1.setCellValue(buildRoom.getRoomId());
            } else {
                cell_1.setCellValue("");
            }

            // 分期名称
            XSSFCell cell0 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell0.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getStageName())) {
                cell0.setCellValue(buildRoom.getStageName());
            } else {
                cell0.setCellValue("");
            }

            // 产品类型
            XSSFCell cell1 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell1.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getProductName())) {
                cell1.setCellValue(buildRoom.getProductName());
            } else {
                cell1.setCellValue("");
            }

            // 房间全称
            XSSFCell cell2 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell2.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getRoomInfo())) {
                cell2.setCellValue(buildRoom.getRoomInfo());
            } else {
                cell2.setCellValue("");
            }

            // 楼栋名称
            XSSFCell cell3 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell3.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getProductBuildName())) {
                cell3.setCellValue(buildRoom.getProductBuildName());
            } else {
                cell3.setCellValue("");
            }

            // 楼层
            XSSFCell cell4 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell4.setCellStyle(style1);
            if (null != buildRoom.getFloorName()) {
                cell4.setCellValue(buildRoom.getFloorName());
            }else {
                cell4.setCellValue("");
            }

            // 房号
            XSSFCell cell5 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell5.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getRoomName())) {
                cell5.setCellValue(buildRoom.getRoomName());
            } else {
                cell5.setCellValue("");
            }

            // 户型
            XSSFCell cell6 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell6.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getHxName())) {
                cell6.setCellValue(buildRoom.getHxName());
            } else {
                cell6.setCellValue("");
            }


            // 建筑面积
            XSSFCell cell7 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell7.setCellStyle(style1);
            if (null != buildRoom.getBldArea()) {
                cell7.setCellValue(buildRoom.getBldArea().doubleValue());
            } else {
                cell7.setCellValue(0.00);
            }

            // 套内面积
            XSSFCell cell8 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell8.setCellStyle(style1);
            if (null != buildRoom.getTnArea()) {
                cell8.setCellValue(buildRoom.getTnArea().doubleValue());
            } else {
                cell8.setCellValue(0.00);
            }

            // 原低价建筑单价
            XSSFCell cell9 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell9.setCellStyle(style1);
            if (null != buildRoom.getDjBldPrice()) {
                cell9.setCellValue(buildRoom.getDjBldPrice().doubleValue());
            } else {
                cell9.setCellValue(0.00);
            }

            // 原底价套内单价
            XSSFCell cell10 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell10.setCellStyle(style1);
            if (null != buildRoom.getDjTnPrice()) {
                cell10.setCellValue(buildRoom.getDjTnPrice().doubleValue());
            } else {
                cell10.setCellValue(0.00);
            }

            // 原底价总价
            XSSFCell cell11 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell11.setCellStyle(style1);
            if (null != buildRoom.getDjTotal()) {
                cell11.setCellValue(buildRoom.getDjTotal().doubleValue());
            } else {
                cell11.setCellValue(0.00);
            }

            // 低价建筑单价
            XSSFCell cell12 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell12.setCellStyle(style2);
            if (null != buildRoom.getOldDjBldPrice()) {
                cell12.setCellValue(buildRoom.getOldDjBldPrice().doubleValue());
            } else {
                cell12.setCellValue(0.00);
            }

            // 低价套内单价
            XSSFCell cell13 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell13.setCellStyle(style2);
            if (null != buildRoom.getOldDjTnPrice()) {
                cell13.setCellValue(buildRoom.getOldDjTnPrice().doubleValue());
            } else {
                cell13.setCellValue(0.00);
            }

            // 低价总价
            XSSFCell cell14 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell14.setCellStyle(style2);
            if (null != buildRoom.getOldDjTotal()) {
                cell14.setCellValue(buildRoom.getOldDjTotal().doubleValue());
            } else {
                cell14.setCellValue(0.00);
            }

            // 项目标识
            XSSFCell cell18 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell18.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getProjectId())) {
                cell18.setCellValue(buildRoom.getProjectId());
            } else {
                cell18.setCellValue("");
            }

            // 楼栋标识
            XSSFCell cell19 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell19.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(buildRoom.getBuildId())) {
                cell19.setCellValue(buildRoom.getBuildId());
            } else {
                cell19.setCellValue("");
            }
        }
    }

    /**
     * 检验是否存在文件
     *
     * @param templateFile
     * @throws ServiceException
     */
    private void verityFile(File templateFile) throws ServiceException {
        if (!templateFile.exists()) {
            templateFile.mkdirs();
            throw new ServiceException("-15_1003", "认购确认导出失败。模板文件不存在");
        }
    }

    /**
     *
     *
     * @param response
     * @param planName
     * @return
     * @throws UnsupportedEncodingException
     */
    private String returnResponseInfo(HttpServletResponse response, String planName) throws UnsupportedEncodingException {
        planName = URLEncoder.encode(planName + ".xlsx", "utf-8").replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        //设置content-disposition响应头控制浏览器以下载的形式打开文件
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(planName.getBytes(), "utf-8"));
        return planName;
    }

    /**
     *
     * @param targetWorkBook
     * @return
     */
    private Map<String, Object> cellStyle(Workbook targetWorkBook) {
        Map<String, Object> style = new HashMap<>();
        /**
         * 单元格样色共三种：
         * 1.白底 居中加粗 style1
         * 2.灰底 居中 加粗
         * 3.灰底 正常
         * 4.动态判断
         */
        XSSFDataFormat format = (XSSFDataFormat) targetWorkBook.createDataFormat();
        CellStyle style1 = targetWorkBook.createCellStyle();
        CellStyle style2 = targetWorkBook.createCellStyle();
        style2.cloneStyleFrom(style1);
        style2.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style1.setLocked(false);
        style1.setBorderBottom(BorderStyle.THIN);
        style1.setBorderTop(BorderStyle.THIN);
        style1.setBorderLeft(BorderStyle.THIN);
        style1.setBorderRight(BorderStyle.THIN);

        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setDataFormat(format.getFormat("0.00"));
        style.put("style1", style1);
        style.put("style2", style2);
        return style;
    }
    /**
     * 面积任务列表删除通过任务id
     *
     * @param taskId
     * @return
     */
    @Override
    public ResultBody delAreaByTaskId(String taskId) {
        return managerDao.delAreaByTaskId(taskId) > 0 ? ResultBody.success("删除成功"): ResultBody.success("删除失败");
    }

    /**
     * 房间面积任务导出
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public void exportRoomTask(HttpServletRequest request, HttpServletResponse response) {
        FileInputStream templateInputStream = null;
        Workbook targetWorkBook = null;
        XSSFSheet targetSheet = null;
        FileOutputStream fileOutputStream = null;
        File templateFile;
        XSSFRow positionRow;
        try {
            List<RoomTask> roomTaskList = managerDao.selectRoomAreaTaskPlusPageList(new RoomTaskDto());

            // 路径
            // String path = "D:\\yu\\areatask.xlsx";
            //String path = request.getServletContext().getRealPath("/");
            //System.out.println("路径为 " + path);
            //String templatePath = "TemplateExcel" + File.separator + "areatask.xlsx";
            String path = "/app/netdata/hourse/areatask.xlsx";
            templateFile = new File(path);
            System.out.println("路径为 " + path);
            // 检验是否存在文件 不存在创建抛异常
            verityFile(templateFile);

            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "面积任务");
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();
            //清空原模板剩余数据
            for (int i = 1; i <= maxTemplateRows; i++) {
                Row removeRow = targetSheet.getRow(i);
                if (removeRow != null) {
                    targetSheet.removeRow(removeRow);
                }
            }
            String planName = "楼栋模板" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            //导出临时文件文件夹。
            String targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            String targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = returnResponseInfo(response, planName);
            // 验证目标文件夹是否存在
            verityTargetFile(targetFileDir);
            int startRow = 1, startRows = startRow;
            Map<String, Object> cellStyle = cellStyle(targetWorkBook);
            CellStyle style1 = (CellStyle) cellStyle.get("style1");
            System.out.println("json格式： " + JSON.toJSONString(roomTaskList));


            for (RoomTask br1 : roomTaskList) {
                positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                setDatTargetDataToCell(positionRow, br1, style1);
                startRows = startRows + 1;
            }
            targetSheet.setRowSumsBelow(false);
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            // targetWorkBook.write(fileOutputStream);
        } catch (ServiceException se) {
            se.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (templateInputStream != null) {
                    templateInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setDatTargetDataToCell(XSSFRow positionRow, RoomTask roomTask, CellStyle style1) {
        /**
         * 单元格样色共三种：
         * 1.白底 居中加粗 style2
         * 2.灰底 居中 加粗 style1
         */
        int startColumn = 0;
        // 项目名称
        XSSFCell cell_1 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_1.setCellStyle(style1);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(roomTask.getProjectName())) {
            cell_1.setCellValue(roomTask.getProjectName());
        } else {
            cell_1.setCellValue("");
        }

        // 申请名称
        XSSFCell cell_2 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_2.setCellStyle(style1);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(roomTask.getApplyName())) {
            cell_2.setCellValue(roomTask.getApplyName());
        } else {
            cell_2.setCellValue("");
        }

        // 申请日期
        XSSFCell cell_3 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_3.setCellStyle(style1);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(roomTask.getApplyTime())) {
            cell_3.setCellValue(roomTask.getApplyTime());
        } else {
            cell_3.setCellValue("");
        }

        // 状态  0：草稿 1：已执行
        XSSFCell cell_4 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_4.setCellStyle(style1);
        if (null != roomTask.getStatus()) {
            if (roomTask.getStatus() == 0) {
                cell_4.setCellValue("草稿");
            }
            else if (roomTask.getStatus() == 1){
                cell_4.setCellValue("已执行");
            }
        } else {
            cell_4.setCellValue("");
        }

        // 调整套数
        XSSFCell cell_51 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_51.setCellStyle(style1);
        if (null != roomTask.getAdjustNum()) {
            cell_51.setCellValue(roomTask.getAdjustNum());
        } else {
            cell_51.setCellValue(0.00);
        }

        // 调整前预售建筑面积
        XSSFCell cell_5 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_5.setCellStyle(style1);
        if (null != roomTask.getBeforeYsBldArea()) {
            cell_5.setCellValue(roomTask.getBeforeYsBldArea().doubleValue());
        } else {
            cell_5.setCellValue(0.00);
        }

        // 调整前预售套内面积
        XSSFCell cell_6 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_6.setCellStyle(style1);
        if (null != roomTask.getBeforeYsTnArea()) {
            cell_6.setCellValue(roomTask.getBeforeYsTnArea().doubleValue());
        } else {
            cell_6.setCellValue(0.00);
        }

        // 调整前实测建筑面积
        XSSFCell cell_7 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_7.setCellStyle(style1);
        if (null != roomTask.getBeforeScBldArea()) {
            cell_7.setCellValue(roomTask.getBeforeScBldArea().doubleValue());
        } else {
            cell_7.setCellValue(0.00);
        }

        // 调整前实测套内面积
        XSSFCell cell_8 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_8.setCellStyle(style1);
        if (null != roomTask.getBeforeScTnArea()) {
            cell_8.setCellValue(roomTask.getBeforeScTnArea().doubleValue());
        } else {
            cell_8.setCellValue(0.00);
        }

        // 调整后预售建筑面积
        XSSFCell cell_9 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_9.setCellStyle(style1);
        if (null != roomTask.getAfterYsBldArea()) {
            cell_9.setCellValue(roomTask.getAfterYsBldArea().doubleValue());
        } else {
            cell_9.setCellValue(0.00);
        }

        // 调整后预售套内面积
        XSSFCell cell_10 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_10.setCellStyle(style1);
        if (null != roomTask.getAfterYsTnArea()) {
            cell_10.setCellValue(roomTask.getAfterYsTnArea().doubleValue());
        } else {
            cell_10.setCellValue(0.00);
        }

        // 调整后实测建筑面积
        XSSFCell cell_11 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_11.setCellStyle(style1);
        if (null != roomTask.getAfterScBldArea()) {
            cell_11.setCellValue(roomTask.getAfterScBldArea().doubleValue());
        } else {
            cell_11.setCellValue(0.00);
        }

        // 调整后实测套内面积
        XSSFCell cell_12 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_12.setCellStyle(style1);
        if (null != roomTask.getAfterScTnArea()) {
            cell_12.setCellValue(roomTask.getAfterScTnArea().doubleValue());
        } else {
            cell_12.setCellValue(0.00);
        }

        // 经办人
        XSSFCell cell_13 = positionRow.createCell(startColumn);
        startColumn = ++startColumn;
        cell_13.setCellStyle(style1);
        if (null != roomTask.getAgent()) {
            cell_13.setCellValue(roomTask.getAgent());
        } else {
            cell_13.setCellValue("");
        }
    }

    /**
     * 房间面积任务执行通过任务id
     *
     * @param taskId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody executeRoomAreaTask(String taskId) {

        // 需要更新数据到 t_mm_build_room 的数据，并且还要更新相应的数据(比如预售建筑面积更新到建筑面积字段，预售套内面积更新到套内面积字段等等。。。。。。)
        List<BuildRoom> updateData = new ArrayList<>();
        // 单纯的更新到 t_mm_build_room 的数据，不做其他的操作(比如预售建筑面积更新到建筑面积字段，预售套内面积更新到套内面积字段等等。。。。。。)
        List<BuildRoom> onlyUpdateData = new ArrayList<>();
        int returnValue = 0;
        if (org.apache.commons.lang3.StringUtils.isEmpty(taskId)) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "任务id为空");
        }

        // 通过任务id查询房间辅助表的数据, 如果不为空则把数据更新到t_mm_build_room表中
        List<BuildRoom> buildRoomList = managerDao.selectRoomAreaTaskListByTaskId(taskId);
        if (CollectionUtils.isNotEmpty(buildRoomList)) {
            // 辅助表的数据更新到t_mm_build_room
            managerDao.updateBatchToBuildRoom(buildRoomList);
            BuildRoom buildRoom = buildRoomList.get(0);
            if (buildRoom.getYsBldArea() != null || buildRoom.getYsTnArea() != null) {
                // 预售
                managerDao.updateBatchRoom(buildRoomList);
            }
            else {
                // 实测
                managerDao.updateBatchRoom(buildRoomList);
            }

            // 更新 t_mm_room_task 的状态为已执行
            returnValue = managerDao.updateStatusByTaskId(taskId);
        }
        return returnValue > 0 ? ResultBody.success("执行成功") : ResultBody.success("执行失败");
    }

    /**
     * 价格列表
     *
     * @param priceDto
     * @return
     */
    @Override
    public ResultBody selectPriceList(PriceDto priceDto) {
        PageHelper.startPage(priceDto.getPageIndex(), priceDto.getPageSize());
        return ResultBody.success(new PageInfo<>(managerDao.selectPriceList(priceDto)));
    }

    /**
     * 标准价导入
     *
     * @param file
     * @param applyName
     * @param valuationType
     * @param priceStandard
     * @param remark
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody importStandard(MultipartFile file, String applyName, Integer valuationType, Integer priceStandard, String remark, HttpServletRequest request) {
        try {
            String username = request.getHeader("username");
            // 第二个参数为空会默认创建一个后缀名 ".tmp"
            File f = File.createTempFile("tmp", null);
            file.transferTo(f);
            // 在JVM退出时删除文件
            f.deleteOnExit();
            FileInputStream fileInputStream;
            XSSFWorkbook workBook;
            Sheet planSheet;
            fileInputStream = new FileInputStream(f);
            workBook = new XSSFWorkbook(fileInputStream);
            planSheet = workBook.getSheetAt(0);
            //导入数据总行数 如果sheet中一行数据都没有则返回-1，只有第一行有数据则返回0，最后有数据的行是第n行则返回 n-1
            int planSheetTotalRows = planSheet.getLastRowNum(), startRow = 1;

            // 项目级别单独存储
            List<BuildRoom> buildRoomList = new ArrayList<>();

            for (int i = startRow; i <= planSheetTotalRows; i++) {
                Row row = planSheet.getRow(i);
                getCellValueForStandard(row, buildRoomList, valuationType, priceStandard);
            }

            // 分期id
            String[] taskId = {UUID.randomUUID().toString()};
            // 添加分期id和经办人
            buildRoomList.forEach(buildRoom -> {
                buildRoom.setCreateUser(username);
                buildRoom.setTaskId(taskId[0]);
            });
            // 添加数据到 t_mm_build_room_plus
            managerDao.saveBatchRoomToPlusTable(buildRoomList);

            // 下面是往任务表中添加的数据
            // 调整套数
            int adjustmentCount = 0;
            // 录入的数据
            List<BuildRoom> newDataList = new ArrayList<>();
            // 原来的数据
            List<BuildRoom> oldDataList = new ArrayList<>();
            // 面价老数据
            BigDecimal oldMjTotal = BigDecimal.ZERO;
            // 面价新数据
            BigDecimal newMjTotal = BigDecimal.ZERO;
            // 根据楼栋id查询出所有房间信息(元数据)
            List<BuildRoom> buildRooms = managerDao.getAreaDataByBuildId(buildRoomList.get(0).getBuildId());
            // 判断录入的数据和元数据不为空
            if (CollectionUtils.isNotEmpty(buildRoomList) && CollectionUtils.isNotEmpty(buildRooms)) {
                for (BuildRoom newData : buildRoomList) {
                    for (BuildRoom oldData : buildRooms) {
                        // 计价方式 1:建筑面积   价格标准 1:以面价总价为准 2:以建筑单价为准 3:以套内单价为准 4:以底价总价为准
                        if (buildRoomList.get(0).getValuationType() == 1) {
                            // 价格标准 1:以面价总价为准
                            if (buildRoomList.get(0).getPriceStandard() == 1) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        if (newData.getMjTotal().compareTo(oldData.getMjTotal() == null ? BigDecimal.ZERO : oldData.getMjTotal()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getMjTotal() == null ? BigDecimal.ZERO : oldData.getMjTotal());
                                            newMjTotal = newMjTotal.add(newData.getMjTotal() == null ? BigDecimal.ZERO : newData.getMjTotal());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 价格标准  2:以建筑单价为准
                            else if (buildRoomList.get(0).getPriceStandard() == 2) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        System.out.println("面价为： " + newData.getMjBldPrice());
                                        System.out.println("面价为： " + oldData.getMjBldPrice());
                                        if (newData.getMjBldPrice().compareTo(oldData.getMjBldPrice() == null ? BigDecimal.ZERO : oldData.getMjBldPrice()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getMjBldPrice() == null ? BigDecimal.ZERO : oldData.getMjBldPrice());
                                            newMjTotal = newMjTotal.add(newData.getMjBldPrice() == null ? BigDecimal.ZERO : newData.getMjBldPrice());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // 计价方式  2:套内面积
                        else if (buildRoomList.get(0).getValuationType() == 2) {
                            // 价格标准 1:以面价总价为准
                            if (buildRoomList.get(0).getPriceStandard() == 1) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        if (newData.getMjTotal().compareTo(oldData.getMjTotal() == null ? BigDecimal.ZERO : oldData.getMjTotal()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getMjTotal() == null ? BigDecimal.ZERO : oldData.getMjTotal());
                                            newMjTotal = newMjTotal.add(newData.getMjTotal() == null ? BigDecimal.ZERO : newData.getMjTotal());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 价格标准  3:以套内单价为准
                            else if (buildRoomList.get(0).getPriceStandard() == 3) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        if (newData.getMjTnPrice().compareTo(oldData.getMjTnPrice() == null ? BigDecimal.ZERO : oldData.getMjTnPrice()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getMjTnPrice() == null ? BigDecimal.ZERO : oldData.getMjTnPrice());
                                            newMjTotal = newMjTotal.add(newData.getMjTnPrice() == null ? BigDecimal.ZERO : newData.getMjTnPrice());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // 计价方式  3:套
                        else if (buildRoomList.get(0).getValuationType() == 3) {
                            // 价格标准 1:以面价总价为准
                            if (buildRoomList.get(0).getPriceStandard() == 1) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        if (newData.getMjTotal().compareTo(oldData.getMjTotal() == null ? BigDecimal.ZERO : oldData.getMjTotal()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getMjTotal() == null ? BigDecimal.ZERO : oldData.getMjTotal());
                                            newMjTotal = newMjTotal.add(newData.getMjTotal() == null ? BigDecimal.ZERO : newData.getMjTotal());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 获取项目名称
            String projectName = managerDao.getProjectName(buildRoomList.get(0).getProjectId());
            System.out.println("项目名称" + projectName);
            // 封装价格任务表的数据
            PriceTask priceTask = new PriceTask()
                    .setTaskId(taskId[0])
                    .setProjectId(buildRoomList.get(0).getProjectId())
                    .setProjectName(buildRoomList.get(0).getProjectName())
                    .setAdjustNum(adjustmentCount)
                    .setApplyName(applyName)
                    .setRemark(remark)
                    .setAgent(username)
                    .setCreateUser(username)
                    .setUpdateUser(username)
                    .setProjectName(projectName);
            // 计价方式 1:建筑面积   价格标准 1:以面价总价为准 2:以建筑单价为准 3:以套内单价为准 4:以底价总价为准
            if (buildRoomList.get(0).getValuationType() == 1) {
                // 价格标准 1:以面价总价为准
                if (buildRoomList.get(0).getPriceStandard() == 1) {
                    priceTask.setBeforeBzTotalPrice(oldMjTotal);
                    priceTask.setAfterBzTotalPrice(newMjTotal);
                }
                // 价格标准  2:以建筑单价为准
                else if (buildRoomList.get(0).getPriceStandard() == 2) {
                    System.out.println("标准价格： " + oldMjTotal);
                    System.out.println("标准价格： " + newMjTotal);
                    priceTask.setBeforeBzBldPrice(oldMjTotal);
                    priceTask.setAfterBzBldPrice(newMjTotal);
                }
            }
            // 计价方式  2:套内面积
            else if (buildRoomList.get(0).getValuationType() == 2) {
                // 价格标准 1:以面价总价为准
                if (buildRoomList.get(0).getPriceStandard() == 1) {
                    priceTask.setBeforeBzTotalPrice(oldMjTotal);
                    priceTask.setAfterBzTotalPrice(newMjTotal);
                }
                // 价格标准  3:以套内单价为准
                else if (buildRoomList.get(0).getPriceStandard() == 3) {
                    priceTask.setBeforeBzTnPrice(oldMjTotal);
                    priceTask.setBeforeBzTnPrice(newMjTotal);
                }
            }
            // 计价方式  3:套
            else if (buildRoomList.get(0).getValuationType() == 3) {
                // 价格标准 1:以面价总价为准
                if (buildRoomList.get(0).getPriceStandard() == 1) {
                    priceTask.setBeforeBzTotalPrice(oldMjTotal);
                    priceTask.setAfterBzTotalPrice(newMjTotal);
                }
            }

            priceTask.setType(0);
            managerDao.savePriceTask(priceTask);

        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResultBody.success("导入成功");
    }

    /**
     * 房间面积任务导出
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public void exportStandardTask(HttpServletRequest request, HttpServletResponse response) {
        FileInputStream templateInputStream = null;
        Workbook targetWorkBook = null;
        XSSFSheet targetSheet = null;
        FileOutputStream fileOutputStream = null;
        File templateFile;
        XSSFRow positionRow;
        try {
            // 查询所有的标准价的任务数据
            List<PriceTask> priceTaskList = managerDao.selectStandardPriceTask(0);

            int rows = 1;
            for (PriceTask pt : priceTaskList) {
                pt.setRow(rows);
                rows = rows + 1;
            }

            // 路径
            // String path = "D:\\yu\\pricetask.xlsx";
            //String path = request.getServletContext().getRealPath("/");
            String path = "/app/netdata/hourse/pricetask.xlsx";
            System.out.println("路径为 " + path);
            //String templatePath = "TemplateExcel" + File.separator + "pricetask.xlsx";
            templateFile = new File(path);
            // 检验是否存在文件 不存在创建抛异常
            verityFile(templateFile);

            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "标准价任务");
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();
            //清空原模板剩余数据
            for (int i = 1; i <= maxTemplateRows; i++) {
                Row removeRow = targetSheet.getRow(i);
                if (removeRow != null) {
                    targetSheet.removeRow(removeRow);
                }
            }
            String planName = "标准价任务" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            //导出临时文件文件夹。
            String targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            String targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = returnResponseInfo(response, planName);
            // 验证目标文件夹是否存在
            verityTargetFile(targetFileDir);
            int startRow = 1, startRows = startRow;
            Map<String, Object> cellStyle = cellStyle(targetWorkBook);
            CellStyle style1 = (CellStyle) cellStyle.get("style1");
            System.out.println("json格式： " + JSON.toJSONString(priceTaskList));


            for (PriceTask pr : priceTaskList) {
                positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                setPriceDataToCell(positionRow, pr, style1, 1);
                startRows = startRows + 1;
            }
            targetSheet.setRowSumsBelow(false);
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            // targetWorkBook.write(fileOutputStream);
        } catch (ServiceException se) {
            se.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (templateInputStream != null) {
                    templateInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPriceDataToCell(XSSFRow positionRow, PriceTask priceTask, CellStyle style1, int flag) {
        /**
         * 单元格样色共三种：
         * 1.白底 居中加粗 style2
         * 2.灰底 居中 加粗 style1
         */
        int startColumn = 0;
        if (flag == 1) {

            // 项目名称
            XSSFCell cell_1 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_1.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(priceTask.getProjectName())) {
                cell_1.setCellValue(priceTask.getProjectName());
            } else {
                cell_1.setCellValue("");
            }

            // 申请名称
            XSSFCell cell_2 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_2.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(priceTask.getApplyName())) {
                cell_2.setCellValue(priceTask.getApplyName());
            } else {
                cell_2.setCellValue("");
            }

            // 申请日期
            XSSFCell cell_3 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_3.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(priceTask.getApplyTime())) {
                cell_3.setCellValue(priceTask.getApplyTime());
            } else {
                cell_3.setCellValue("");
            }

            // 状态  0：草稿 1：已执行
            XSSFCell cell_4 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_4.setCellStyle(style1);
            if (null != priceTask.getStatus()) {
                if (priceTask.getStatus() == 0) {
                    cell_4.setCellValue("草稿");
                }
                else if (priceTask.getStatus() == 1){
                    cell_4.setCellValue("已执行");
                }
            } else {
                cell_4.setCellValue("");
            }

            // 调整套数
            XSSFCell cell_51 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_51.setCellStyle(style1);
            if (null != priceTask.getAdjustNum()) {
                cell_51.setCellValue(priceTask.getAdjustNum());
            } else {
                cell_51.setCellValue(0.00);
            }

            // 调整前标准总价
            XSSFCell cell_14 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_14.setCellStyle(style1);
            if (null != priceTask.getBeforeBzTotalPrice()) {
                cell_14.setCellValue(priceTask.getBeforeBzTotalPrice().doubleValue());
            } else {
                cell_14.setCellValue(0.00);
            }

            // 调整后标准总价
            XSSFCell cell_15 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_15.setCellStyle(style1);
            if (null != priceTask.getAfterBzTotalPrice()) {
                cell_15.setCellValue(priceTask.getAfterBzTotalPrice().doubleValue());
            } else {
                cell_15.setCellValue(0.00);
            }

            // 调整前建筑标准单价
            XSSFCell cell_16 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_16.setCellStyle(style1);
            if (null != priceTask.getBeforeBzBldPrice()) {
                cell_16.setCellValue(priceTask.getBeforeBzBldPrice().doubleValue());
            } else {
                cell_16.setCellValue(0.00);
            }

            // 调整后建筑标准单价
            XSSFCell cell_17 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_17.setCellStyle(style1);
            if (null != priceTask.getAfterBzBldPrice()) {
                cell_17.setCellValue(priceTask.getAfterBzBldPrice().doubleValue());
            } else {
                cell_17.setCellValue(0.00);
            }

            // 调整前套内标准单价
            XSSFCell cell_18 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_18.setCellStyle(style1);
            if (null != priceTask.getBeforeBzTnPrice()) {
                cell_18.setCellValue(priceTask.getBeforeBzTnPrice().doubleValue());
            } else {
                cell_18.setCellValue(0.00);
            }

            // 调整后套内标准单价
            XSSFCell cell_19 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_19.setCellStyle(style1);
            if (null != priceTask.getAfterBzTnPrice()) {
                cell_19.setCellValue(priceTask.getAfterBzTnPrice().doubleValue());
            } else {
                cell_19.setCellValue(0.00);
            }

            // 经办人
            XSSFCell cell_13 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_13.setCellStyle(style1);
            if (null != priceTask.getAgent()) {
                cell_13.setCellValue(priceTask.getAgent());
            } else {
                cell_13.setCellValue("");
            }
        }
        else if (flag == 2) {
            // 项目名称
            XSSFCell cell_1 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_1.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(priceTask.getProjectName())) {
                cell_1.setCellValue(priceTask.getProjectName());
            } else {
                cell_1.setCellValue("");
            }

            // 申请名称
            XSSFCell cell_2 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_2.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(priceTask.getApplyName())) {
                cell_2.setCellValue(priceTask.getApplyName());
            } else {
                cell_2.setCellValue("");
            }

            // 申请日期
            XSSFCell cell_3 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_3.setCellStyle(style1);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(priceTask.getApplyTime())) {
                cell_3.setCellValue(priceTask.getApplyTime());
            } else {
                cell_3.setCellValue("");
            }

            // 状态  0：草稿 1：已执行
            XSSFCell cell_4 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_4.setCellStyle(style1);
            if (null != priceTask.getStatus()) {
                if (priceTask.getStatus() == 0) {
                    cell_4.setCellValue("草稿");
                }
                else if (priceTask.getStatus() == 1){
                    cell_4.setCellValue("已执行");
                }
            } else {
                cell_4.setCellValue("");
            }

            // 调整套数
            XSSFCell cell_51 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_51.setCellStyle(style1);
            if (null != priceTask.getAdjustNum()) {
                cell_51.setCellValue(priceTask.getAdjustNum());
            } else {
                cell_51.setCellValue(0.00);
            }

            // 调整前低价总价
            XSSFCell cell_14 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_14.setCellStyle(style1);
            if (null != priceTask.getBeforeDjTotal()) {
                cell_14.setCellValue(priceTask.getBeforeDjTotal().doubleValue());
            } else {
                cell_14.setCellValue(0.00);
            }

            // 调整后低价总价
            XSSFCell cell_15 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_15.setCellStyle(style1);
            if (null != priceTask.getAfterDjTotal()) {
                cell_15.setCellValue(priceTask.getAfterDjTotal().doubleValue());
            } else {
                cell_15.setCellValue(0.00);
            }

            // 调整前建筑低价单价
            XSSFCell cell_16 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_16.setCellStyle(style1);
            if (null != priceTask.getBeforeBldDjPrice()) {
                cell_16.setCellValue(priceTask.getBeforeBldDjPrice().doubleValue());
            } else {
                cell_16.setCellValue(0.00);
            }

            // 调整后建筑低价单价
            XSSFCell cell_17 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_17.setCellStyle(style1);
            if (null != priceTask.getAfterBldDjPrice()) {
                cell_17.setCellValue(priceTask.getAfterBldDjPrice().doubleValue());
            } else {
                cell_17.setCellValue(0.00);
            }

            // 调整前套内低价单价
            XSSFCell cell_18 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_18.setCellStyle(style1);
            if (null != priceTask.getBeforeTnDjPrice()) {
                cell_18.setCellValue(priceTask.getBeforeTnDjPrice().doubleValue());
            } else {
                cell_18.setCellValue(0.00);
            }

            // 调整后套内低价单价
            XSSFCell cell_19 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_19.setCellStyle(style1);
            if (null != priceTask.getAfterDjPrice()) {
                cell_19.setCellValue(priceTask.getAfterDjPrice().doubleValue());
            } else {
                cell_19.setCellValue(0.00);
            }

            // 经办人
            XSSFCell cell_13 = positionRow.createCell(startColumn);
            startColumn = ++startColumn;
            cell_13.setCellStyle(style1);
            if (null != priceTask.getAgent()) {
                cell_13.setCellValue(priceTask.getAgent());
            } else {
                cell_13.setCellValue("");
            }
        }
    }

    /**
     * 标准价任务列表删除通过任务id
     *
     * @param taskId
     * @return
     */
    @Override
    public ResultBody delStandardPriceByTaskId(String taskId) {
        return managerDao.delStandardPriceByTaskId(taskId) > 0
                ? ResultBody.success("删除成功"): ResultBody.success("删除失败");
    }

    /**
     * 标准价任务执行通过任务id
     *
     * @param taskId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody executeStandardPriceTask(String taskId) {
        int returnValue = 0;
        if (org.apache.commons.lang3.StringUtils.isEmpty(taskId)) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "任务id为空");
        }
        // 通过任务id查询房间辅助表的数据, 如果不为空则把数据更新到t_mm_build_room表中
        List<BuildRoom> buildRoomList = managerDao.selectRoomAreaTaskListByTaskId(taskId);
        if (CollectionUtils.isNotEmpty(buildRoomList)) {
            // 辅助表的数据更新到t_mm_build_room
            managerDao.updateBatchStandardPriceToBuildRoom(buildRoomList);

            BuildRoom buildRoom = buildRoomList.get(0);
            if (buildRoom.getMjBldPrice() != null) {
                // 面价建筑单价
                managerDao.updateBatchStandardPrice(buildRoomList);
            }
            else if (buildRoom.getMjTnPrice() != null) {
                // 面价套内单价
                managerDao.updateBatchStandardPrice(buildRoomList);
            }
            else {
                // 面价总价
                managerDao.updateBatchStandardPrice(buildRoomList);
            }

            // 更新 t_mm_price_task 的状态为已执行
            returnValue = managerDao.updateStandardPriceStatusByTaskId(taskId);
        }
        return returnValue > 0 ? ResultBody.success("执行成功") : ResultBody.success("执行失败");
    }

    /**
     * 模板导出---低价录入
     *
     * @param buildId
     * @param request
     * @param response
     * @return
     */
    @Override
    public void exportDjTemplate(String buildId, HttpServletRequest request, HttpServletResponse response) {
        FileInputStream templateInputStream = null;
        Workbook targetWorkBook = null;
        XSSFSheet targetSheet = null;
        FileOutputStream fileOutputStream = null;
        File templateFile;
        XSSFRow positionRow;
        try {
            List<BuildRoom> buildRoomList = managerDao.buildRoomListByBuildId(buildId);
            DesignBuildModel designBuildModel = managerDao.getDesignBuildByBuildId(buildId);

            // 路径
            // String path = "D:\\yu\\djj.xlsx";
            // String path = request.getServletContext().getRealPath("/");
            String path = "/app/netdata/hourse/dj.xlsx";
            System.out.println("路径为 " + path);
            //String templatePath = "TemplateExcel" + File.separator + "dj.xlsx";
            templateFile = new File(path);
            // 检验是否存在文件 不存在创建抛异常
            verityFile(templateFile);

            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "低价录入模板");
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();
            //清空原模板剩余数据
            for (int i = 1; i <= maxTemplateRows; i++) {
                Row removeRow = targetSheet.getRow(i);
                if (removeRow != null) {
                    targetSheet.removeRow(removeRow);
                }
            }
            String planName = "低价模板" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            //导出临时文件文件夹。
            String targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            String targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = returnResponseInfo(response, planName);
            // 验证目标文件夹是否存在
            verityTargetFile(targetFileDir);
            int startRow = 1, startRows = startRow;
            // 遍历数据
            for (BuildRoom br : buildRoomList) {
                br.setRow(startRow++);
                br.setStageName(designBuildModel.getStageName());
                br.setBldType(designBuildModel.getBldType());
                br.setProductName(designBuildModel.getProductName());
            }
            Map<String, Object> cellStyle = cellStyle(targetWorkBook);
            CellStyle style1 = (CellStyle) cellStyle.get("style1");
            CellStyle style2 = (CellStyle) cellStyle.get("style2");

            String s = JSON.toJSONString(buildRoomList);
            System.out.println("json格式： " + s);
            for (BuildRoom br : buildRoomList) {
                positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                setDatTargetDataToCell(positionRow, br, style2, style1, 3);
                startRows = startRows + 1;
            }
            targetSheet.setColumnHidden(16, true);
            targetSheet.setColumnHidden(0, true);
            targetSheet.setColumnHidden(17, true);
            targetSheet.setRowSumsBelow(false);
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            //targetWorkBook.write(fileOutputStream);
        } catch (ServiceException se) {
            se.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (templateInputStream != null) {
                    templateInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 标准价导入
     *
     * @param file
     * @param applyName
     * @param valuationType
     * @param priceStandard
     * @param remark
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody importDj(MultipartFile file, String applyName, Integer valuationType, Integer priceStandard, String remark, HttpServletRequest request) {
        try {
            String username = request.getHeader("username");
            // 第二个参数为空会默认创建一个后缀名 ".tmp"
            File f = File.createTempFile("tmp", null);
            file.transferTo(f);
            // 在JVM退出时删除文件
            f.deleteOnExit();
            FileInputStream fileInputStream;
            XSSFWorkbook workBook;
            Sheet planSheet;
            fileInputStream = new FileInputStream(f);
            workBook = new XSSFWorkbook(fileInputStream);
            planSheet = workBook.getSheetAt(0);
            //导入数据总行数 如果sheet中一行数据都没有则返回-1，只有第一行有数据则返回0，最后有数据的行是第n行则返回 n-1
            int planSheetTotalRows = planSheet.getLastRowNum(), startRow = 1;

            // 项目级别单独存储
            List<BuildRoom> buildRoomList = new ArrayList<>();

            for (int i = startRow; i <= planSheetTotalRows; i++) {
                Row row = planSheet.getRow(i);
                getCellValueForDj(row, buildRoomList, valuationType, priceStandard);
            }

            // 分期id
            String[] taskId = {UUID.randomUUID().toString()};
            // 添加分期id和经办人
            buildRoomList.forEach(buildRoom -> {
                buildRoom.setCreateUser(username);
                buildRoom.setTaskId(taskId[0]);
            });
            // 添加低价数据到 t_mm_build_room_plus
            managerDao.saveBatchRoomToPlusTable(buildRoomList);

            // 下面是往任务表中添加的数据
            // 调整套数
            int adjustmentCount = 0;
            // 录入的数据
            List<BuildRoom> newDataList = new ArrayList<>();
            // 原来的数据
            List<BuildRoom> oldDataList = new ArrayList<>();
            // 面价老数据
            BigDecimal oldMjTotal = BigDecimal.ZERO;
            // 面价新数据
            BigDecimal newMjTotal = BigDecimal.ZERO;
            // 根据楼栋id查询出所有房间信息(元数据)
            List<BuildRoom> buildRooms = managerDao.getAreaDataByBuildId(buildRoomList.get(0).getBuildId());
            // 判断录入的数据和元数据不为空
            if (CollectionUtils.isNotEmpty(buildRoomList) && CollectionUtils.isNotEmpty(buildRooms)) {
                for (BuildRoom newData : buildRoomList) {
                    for (BuildRoom oldData : buildRooms) {
                        // 计价方式 1:建筑面积   价格标准 1:以面价总价为准 2:以建筑单价为准 3:以套内单价为准 4:以底价总价为准
                        if (buildRoomList.get(0).getValuationType() == 1) {
                            // 价格标准 4:以底价总价为准
                            if (buildRoomList.get(0).getPriceStandard() == 4) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        if (newData.getDjTotal().compareTo(oldData.getDjTotal() == null ? BigDecimal.ZERO : oldData.getDjTotal()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getDjTotal() == null ? BigDecimal.ZERO : oldData.getDjTotal());
                                            newMjTotal = newMjTotal.add(newData.getDjTotal() == null ? BigDecimal.ZERO : newData.getDjTotal());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                        }
                                    }
                                }
                            }
                            // 价格标准  2:以建筑单价为准
                            else if (buildRoomList.get(0).getPriceStandard() == 2) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        if (newData.getDjBldPrice().compareTo(oldData.getDjBldPrice() == null ? BigDecimal.ZERO : oldData.getDjBldPrice()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getDjBldPrice() == null ? BigDecimal.ZERO : oldData.getDjBldPrice());
                                            newMjTotal = newMjTotal.add(newData.getDjBldPrice() == null ? BigDecimal.ZERO : newData.getDjBldPrice());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                        }
                                    }
                                }
                            }
                        }
                        // 计价方式  2:套内面积
                        else if (buildRoomList.get(0).getValuationType() == 2) {
                            // 价格标准 4:以底价总价为准
                            if (buildRoomList.get(0).getPriceStandard() == 4) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        if (newData.getDjTotal().compareTo(oldData.getDjTotal() == null ? BigDecimal.ZERO : oldData.getDjTotal()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getDjTotal() == null ? BigDecimal.ZERO : oldData.getDjTotal());
                                            newMjTotal = newMjTotal.add(newData.getDjTotal() == null ? BigDecimal.ZERO : newData.getDjTotal());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                        }
                                    }
                                }
                            }
                            // 价格标准  3:以套内单价为准
                            else if (buildRoomList.get(0).getPriceStandard() == 3) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        if (newData.getDjTnPrice().compareTo(oldData.getDjTnPrice() == null ? BigDecimal.ZERO : oldData.getDjTnPrice()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getDjTnPrice() == null ? BigDecimal.ZERO : oldData.getDjTnPrice());
                                            newMjTotal = newMjTotal.add(newData.getDjTnPrice() == null ? BigDecimal.ZERO : newData.getDjTnPrice());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                        }
                                    }
                                }
                            }
                        }
                        // 计价方式  3:套
                        else if (buildRoomList.get(0).getValuationType() == 3) {
                            // 价格标准 4:以底价总价为准
                            if (buildRoomList.get(0).getPriceStandard() == 4) {
                                if (org.apache.commons.lang3.StringUtils.isNotEmpty(newData.getRoomId())) {
                                    if (newData.getRoomId().equals(oldData.getRoomId())) {
                                        if (newData.getDjTotal().compareTo(oldData.getDjTotal() == null ? BigDecimal.ZERO : oldData.getDjTotal()) != 0) {
                                            adjustmentCount = adjustmentCount + 1;
                                            oldMjTotal = oldMjTotal.add(oldData.getDjTotal() == null ? BigDecimal.ZERO : oldData.getDjTotal());
                                            newMjTotal = newMjTotal.add(newData.getDjTotal() == null ? BigDecimal.ZERO : newData.getDjTotal());
                                            newDataList.add(newData);
                                            oldDataList.add(oldData);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // 获取项目名称
            String projectName = managerDao.getProjectName(buildRoomList.get(0).getProjectId());
            // 封装价格任务表的数据
            PriceTask priceTask = new PriceTask()
                    .setTaskId(taskId[0])
                    .setProjectId(buildRoomList.get(0).getProjectId())
                    .setProjectName(buildRoomList.get(0).getProjectName())
                    .setAdjustNum(adjustmentCount)
                    .setApplyName(applyName)
                    .setRemark(remark)
                    .setAgent(username)
                    .setCreateUser(username)
                    .setUpdateUser(username)
                    .setProjectName(projectName);
            // 计价方式 1:建筑面积   价格标准 1:以面价总价为准 2:以建筑单价为准 3:以套内单价为准 4:以底价总价为准
            if (buildRoomList.get(0).getValuationType() == 1) {
                // 价格标准 4:以低价总价为准
                if (buildRoomList.get(0).getPriceStandard() == 4) {
                    priceTask.setBeforeDjTotal(oldMjTotal);
                    priceTask.setAfterDjTotal(newMjTotal);
                }
                // 价格标准  2:以建筑单价为准
                else if (buildRoomList.get(0).getPriceStandard() == 2) {
                    priceTask.setBeforeBldDjPrice(oldMjTotal);
                    priceTask.setAfterBldDjPrice(newMjTotal);
                }
            }
            // 计价方式  2:套内面积
            else if (buildRoomList.get(0).getValuationType() == 2) {
                // 价格标准 4:以低价总价为准
                if (buildRoomList.get(0).getPriceStandard() == 4) {
                    priceTask.setBeforeDjTotal(oldMjTotal);
                    priceTask.setAfterDjTotal(newMjTotal);
                }
                // 价格标准  3:以套内单价为准
                else if (buildRoomList.get(0).getPriceStandard() == 3) {
                    priceTask.setBeforeTnDjPrice(oldMjTotal);
                    priceTask.setAfterDjPrice(newMjTotal);
                }
            }
            // 计价方式  3:套
            else if (buildRoomList.get(0).getValuationType() == 3) {
                // 价格标准 4:以低价总价为准
                if (buildRoomList.get(0).getPriceStandard() == 4) {
                    priceTask.setBeforeDjTotal(oldMjTotal);
                    priceTask.setAfterDjTotal(newMjTotal);
                }
            }
            //  0:标准价录入 1:低价录入
            priceTask.setType(1);
            managerDao.savePriceTask(priceTask);
        }catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResultBody.success("导入成功");
    }

    private void getCellValueForDj(Row row, List<BuildRoom> buildRoomList, Integer valuationType, Integer priceStandard) {
        // 计价方式 1:建筑面积 2:套内面积 3:套 价格标准 1:以面价总价为准 2:以建筑单价为准 3:以套内单价为准 4:以底价总价为准
        BuildRoom buildRoom = new BuildRoom();

        // 房间标识
        Cell cell00 = row.getCell(0);
        String cellValue0 = FileUtils.getCellValue(cell00, null);
        buildRoom.setRoomId(cellValue0);

        // 项目标识
        Cell cell11 = row.getCell(16);
        String cellValue11 = FileUtils.getCellValue(cell11, null);
        buildRoom.setProjectId(cellValue11);

        // 楼栋标识
        Cell cell22 = row.getCell(17);
        String cellValue22 = FileUtils.getCellValue(cell22, null);
        buildRoom.setBuildId(cellValue22);

        // 计价方式 1:建筑面积
        if (valuationType == 1) {
            buildRoom.setValuationType(1);
            // 价格标准 4:以底价总价为准
            if (priceStandard == 4) {
                buildRoom.setPriceStandard(4);
                // 低价总价
                Cell cell0 = row.getCell(15);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setDjTotal(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
            // 价格标准 2:以建筑单价为准
            else if (priceStandard == 2) {
                buildRoom.setPriceStandard(2);
                // 低价建筑单价
                Cell cell0 = row.getCell(13);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setDjBldPrice(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
        }
        // 计价方式 2:套内面积
        else if (valuationType == 2) {
            buildRoom.setValuationType(2);
            // 价格标准 4:以底价总价为准
            if (priceStandard == 4) {
                buildRoom.setPriceStandard(4);
                // 低价总价
                Cell cell0 = row.getCell(15);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setDjTotal(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
            // 价格标准 3:以套内单价为准
            else if (priceStandard == 3) {
                buildRoom.setPriceStandard(3);
                // 低价套内单价
                Cell cell0 = row.getCell(14);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setDjTnPrice(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
        }
        // 计价方式 3:套
        else if (valuationType == 3) {
            buildRoom.setValuationType(3);
            // 价格标准 4:以底价总价为准
            if (priceStandard == 4) {
                buildRoom.setPriceStandard(4);
                // 低价总价
                Cell cell0 = row.getCell(15);
                String cellValue1 = FileUtils.getCellValue(cell0, null);
                buildRoom.setDjTotal(new BigDecimal(cellValue1));
                buildRoomList.add(buildRoom);
            }
        }
    }

    /**
     * 低价任务导出
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public void exportDjTask(HttpServletRequest request, HttpServletResponse response) {
        FileInputStream templateInputStream = null;
        Workbook targetWorkBook = null;
        XSSFSheet targetSheet = null;
        FileOutputStream fileOutputStream = null;
        File templateFile;
        XSSFRow positionRow;
        try {
            // 查询所有的低价的任务数据
            List<PriceTask> priceTaskList = managerDao.selectStandardPriceTask(1);

            int rows = 1;
            for (PriceTask pt : priceTaskList) {
                pt.setRow(rows);
                rows = rows + 1;
            }

            // 路径
            //  String path = "D:\\yu\\djpricetask.xlsx";
            //String path = request.getServletContext().getRealPath("/");
            String path = "/app/netdata/hourse/djpricetask.xlsx";
            System.out.println("路径为 " + path);
            //String templatePath = "TemplateExcel" + File.separator + "djpricetask.xlsx";
            templateFile = new File(path);
            // 检验是否存在文件 不存在创建抛异常
            verityFile(templateFile);

            templateInputStream = new FileInputStream(templateFile);
            targetWorkBook = new XSSFWorkbook(templateInputStream);
            targetSheet = (XSSFSheet) targetWorkBook.getSheetAt(0);
            targetWorkBook.setSheetName(0, "低价任务");
            //模板文件中最大行
            int maxTemplateRows = targetSheet.getLastRowNum();
            //清空原模板剩余数据
            for (int i = 1; i <= maxTemplateRows; i++) {
                Row removeRow = targetSheet.getRow(i);
                if (removeRow != null) {
                    targetSheet.removeRow(removeRow);
                }
            }
            String planName = "低价任务模板" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            //导出临时文件文件夹。
            String targetFileDir = "Uploads" + File.separator + "DownLoadTemporaryFiles";
            // 目标文件路径。
            String targetFilePath = targetFileDir + File.separator + planName + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            planName = returnResponseInfo(response, planName);
            // 验证目标文件夹是否存在
            verityTargetFile(targetFileDir);
            int startRow = 1, startRows = startRow;
            Map<String, Object> cellStyle = cellStyle(targetWorkBook);
            CellStyle style1 = (CellStyle) cellStyle.get("style1");
            System.out.println("json格式： " + JSON.toJSONString(priceTaskList));


            for (PriceTask pr : priceTaskList) {
                positionRow = targetSheet.createRow(startRows);
                positionRow.setHeightInPoints(20);
                setPriceDataToCell(positionRow, pr, style1, 2);
                startRows = startRows + 1;
            }
            targetSheet.setRowSumsBelow(false);
            fileOutputStream = new FileOutputStream(targetFilePath);
            //页面输出
            targetWorkBook.write(response.getOutputStream());
            // targetWorkBook.write(fileOutputStream);
        } catch (ServiceException se) {
            se.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (templateInputStream != null) {
                    templateInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 低价任务列表删除通过任务id
     *
     * @param taskId
     * @return
     */
    @Override
    public ResultBody delDjPriceByTaskId(String taskId) {
        return managerDao.delStandardPriceByTaskId(taskId) > 0
                ? ResultBody.success("删除成功"): ResultBody.success("删除失败");
    }

    /**
     * 低价任务执行通过任务id
     *
     * @param taskId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody executeDjPriceTask(String taskId) {
        int returnValue = 0;
        if (org.apache.commons.lang3.StringUtils.isEmpty(taskId)) {
            return ResultBody.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "任务id为空");
        }
        // 通过任务id查询房间辅助表的数据, 如果不为空则把数据更新到t_mm_build_room表中
        List<BuildRoom> buildRoomList = managerDao.selectRoomAreaTaskListByTaskId(taskId);
        if (CollectionUtils.isNotEmpty(buildRoomList)) {
            // 辅助表的数据更新到t_mm_build_room
            managerDao.updateBatchDjPriceToBuildRoom(buildRoomList);
            // 更新 t_mm_price_task 的状态为已执行
            returnValue = managerDao.updateStandardPriceStatusByTaskId(taskId);
        }
        return returnValue > 0 ? ResultBody.success("执行成功") : ResultBody.success("执行失败");
    }

    @Override
    public ResultBody selectCityList(HttpServletRequest request) {
        return ResultBody.success(managerDao.getCitys());
    }

}
