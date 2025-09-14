package cn.visolink.system.org.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.BaseResultCodeEnum;
import cn.visolink.system.companyQw.service.impl.CompanyQwSynServiceImpl;
import cn.visolink.system.job.authorization.mapper.JobMapper;
import cn.visolink.system.org.dao.OrganizationDao;
import cn.visolink.system.org.model.Organization;
import cn.visolink.system.org.model.form.OrganizationForm;
import cn.visolink.system.org.model.vo.OrganizationVO;
import cn.visolink.system.org.service.OrganizationService;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cess.CessException;
import io.cess.util.PropertyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * <p>
 * Organization服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationDao, Organization> implements OrganizationService {

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CompanyQwSynServiceImpl companyQwSynService;

    @Value("${outbound.addOrgOp}")
    private String addOrgOp;

    @Autowired
    private RedisService redisService;

    @Value("${outbound.updateOrgOp}")
    private String updateOrgOp;

    @Autowired
    private JobMapper jobMapper;

    @Value("${outbound.secret}")
    private String secret;

    @Value("${outbound.channelId}")
    private String channelId;

    @Value("${outbound.urlOp}")
    private String urlOp;

    @Override
    public OrganizationForm save(OrganizationForm record) {
        if (ObjectUtil.isNull(record.getLevels())) {
            throw new BadRequestException(-10_0012, "参数格式不正确！");
        }
        if (StrUtil.isEmpty(record.getFullPath())) {
            throw new BadRequestException(-10_0012, "参数格式不正确！");
        }
        if (StrUtil.isEmpty(record.getOrgName())) {
            throw new BadRequestException(-10_0012, "参数格式不正确！");
        }
        Organization data = this.convertDO(record);
        data.setCreateTime(new Date());
        if (data.getOrgType()!=null){

        }else{
            data.setOrgType(2);
        }
        data.setLevels(data.getLevels() + 1);
        data.setFullPath(data.getFullPath() + "/" + data.getOrgName());
        //查询上级组织是否关联项目
        Map project = organizationMapper.getParentProject(record.getPid());
        if (project != null && project.size() > 0) {
            data.setProjectId(project.get("ProjectId") + "");
        }
        String id = UUID.randomUUID().toString();
        data.setId(id);
        baseMapper.insert(data);

        Map<String, Object> map = new HashMap<>();
        map.put("name",data.getOrgName());
        map.put("p_id",data.getPid());
        map.put("id",data.getId());
        map.put("type",data.getOrgCategory());
        if (data.getOrgCategory() == 4){
            map.put("project_id",data.getProjectId());
        }
        Map idTwo = jobMapper.getZgQx(SecurityUtils.getUserId());
        String  token1 = "";
        if(idTwo != null && null!=idTwo.get("sid") && !"".equals(idTwo.get("sid"))){
            Map<String, Object> loginMap = new HashMap<>();
            loginMap.put("secret",secret);
            loginMap.put("channel_id",channelId);
            loginMap.put("sid",idTwo.get("sid"));
            loginMap.put("uid",idTwo.get("uid"));
            loginMap.put("did",idTwo.get("did"));
            String s = HttpClientUtil.postHttpOutbound(urlOp, null, loginMap);
            JSONObject jsonObject = JSONObject.parseObject(s);
            JSONObject data1 = jsonObject.getJSONObject("data");
            token1 = data1.get("token").toString();
        }else{
            token1 = redisService.getVal("outbound."+record.getUserName())+"";
        }
        HttpClientUtil.postHttpOutbound(addOrgOp,token1,map);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String companycode = request.getHeader("companycode");
        if (redisUtil.get("QW_DATATOKEN_"+companycode)!=null) {
            //添加企微组织信息
            companyQwSynService.addDept(data.getOrgName(), data.getPid(), id, data.getProjectId());
        }

        return record;
    }

    @Override
    public Integer updateById(OrganizationForm record) {
        Organization data = this.convertDO(record);
        if (StrUtil.isNotBlank(record.getCompanyName())) {
            data.setOrgCompanyId(record.getCompanyName());
        }
        data.setEditTime(new Date());
        //查询出所有的子组织
        //获取父级Id
        String id = data.getId();
        Map<Object, Object> map = new HashMap<>();
        List<OrganizationForm> list = new ArrayList<>();
        Map<Object, Object> hashMap = new HashMap<>();

        map.put("id", id);
        //获取父级的原组织名称
        Map parentOrg = organizationMapper.getParentProject(id);
        String oldOrgName = parentOrg.get("OrgName") + "";
        map.put("oldName", oldOrgName);
        map.put("newName", data.getOrgName());
        findAllChildOrg(map, list);
        if(list!=null&&list.size()>0){
            for (OrganizationForm org : list) {
                hashMap.put("id", org.getId());
                hashMap.put("fullPath", org.getFullPath().replaceAll(oldOrgName, data.getOrgName()));
                organizationMapper.updateChildFullPath(hashMap);
            }
        }
        data.setFullPath(data.getFullPath().replaceAll(oldOrgName, data.getOrgName()));
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name",data.getOrgName());
        map1.put("p_id",data.getPid());
        map1.put("id",data.getId());
        HttpClientUtil.postHttpOutbound(updateOrgOp,redisService.getVal("outbound."+record.getUserName())+"",map1);
        //修改父级信息
        return baseMapper.updateById(data);
    }

    public void findAllChildOrg(Map map, List bigList) {
        Map<Object, Object> hashMap = new HashMap<>();
        //查询当前组织下的所有子集
        List<OrganizationForm> childOrgsList = organizationMapper.queryChildOrgs(map);
        if (childOrgsList != null & childOrgsList.size() > 0) {
            for (OrganizationForm org : childOrgsList) {
                bigList.add(org);
                hashMap.put("id", org.getId());
                findAllChildOrg(hashMap, bigList);
            }
        }

    }

    @Override
    public Integer deleteById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        return baseMapper.deleteById(id);
    }

    @Override
    public OrganizationVO selectById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        Organization data = baseMapper.selectById(id);
        OrganizationVO result = PropertyUtil.copy(data, OrganizationVO.class);
        return result;
    }

    @Autowired
    private OrganizationDao organizationMapper;

    @Override
    public Map selectAll(OrganizationForm record) {
        Map map = new HashMap();
        map.put("UserName", SecurityUtils.getUsername());
        Map userInfoMap = authMapper.mGetUserInfo(map);
        String jobCode = userInfoMap.get("JobCode") + "";
        String sql = "";
        if (!jobCode.equals("10001")){
            List<String> fullss = authMapper.userPath(map);
            StringBuffer sb = new StringBuffer();
            if (fullss==null || fullss.size()==0){
                throw new BadRequestException(-10_0000,"用户无项目权限！");
            }
            sql = " and (";
            for (int i = 0; i < fullss.size(); i++) {
                if (i==fullss.size()-1){
                    sb.append("FullPath LIKE '"+fullss.get(i)+"%')");
                }else{
                    sb.append(" FullPath LIKE '"+fullss.get(i)+"%' or ");
                }
            }
            sql = sql + sb.toString();
        }

        Map paramMap = new HashMap<>();
        paramMap.put("userName", SecurityUtils.getUsername());
        paramMap.put("jobCode", userInfoMap.get("JobCode") + "");
        paramMap.put("isNeedShow", record.getIsNeedShow());
        paramMap.put("projectId", record.getProjectId());
        paramMap.put("AuthCompanyID", record.getAuthCompanyId());
        paramMap.put("sql",sql);
        List<Organization> list = organizationMapper.findOrgListByOrgIdAndProIdAndCompanyIdNew(paramMap);
        if (!jobCode.equals("10001")) {
            for (Organization org:list) {
                if (org.getOrgCategory()==4){
                    org.setPid("-1");
                }
            }
        }
        return this.convert(list);
    }


    public Map treeData(String pid, List<Organization> organization, List<Organization> organizationOne, boolean flag) {

        // 查询到的组织数据集合
        List<Organization> organizationList1 = null;
        // 判断集合 organization 是否为空
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(organization)) {
            organizationList1 = new ArrayList<>(100);
            organizationOne = new ArrayList<>(10);
        }
        else {
            organizationList1 = organization;
        }

        // 拼接pid格式为为 ('id1', 'id2', 'id3'), 组装sql语句
        String pids = "";
        // 根据是否有 "(" 和 ")" 判断是否需要组装sql语句
        if (!pid.contains("(") && !pid.contains(")")) {
            // 进行组装sql语句
            pids = "('" + pid + "')";
        }
        else {
            // 已经组装好sql语句，直接赋值就可以
            pids = pid;
        }

        // 根据pid查询该组织下的树数据
        List<Organization> organizationList = organizationMapper.selectThreeOrgByPid(pids);
        if (!organizationList.isEmpty()) {
            if(!flag) {
                organizationOne.addAll(organizationList);
                System.out.println("第一次的集合： " + organizationOne);
                flag = true;
            }
            // 该组织下有数据, 添加进 organizations 中
            organizationList1.addAll(organizationList);
            List<String> pidList = new ArrayList<>(organizationList.size());
            // 遍历数据，获取id
            for (Organization o : organizationList) {
                pidList.add("'" + o.getId() + "'");
            }

            // 进行递归调用
            this.treeData("(" + Arrays.toString(pidList.toArray(new String[organizationList.size()])).replace("[", "").replace("]", "") + ")"
            , organizationList1, organizationOne, flag);
        }

        return this.convertStructure(organizationList1, organizationOne);
    }

    /**
     * @author  Mr.Yu
     * @message 不在云端起舞，紧贴地面行走
     * @description
     *     组织树结构
     * @date 2021/12/13 11:46
     * @return
     */
    private Map convertStructure(List<Organization> list, List<Organization> organizationOne) {

        // 以下的逻辑粘贴的是 convert 方法 和 buildTree 方法的代码
        List<OrganizationVO> organizationList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return MapUtil.newHashMap();
        }

        // 把对象 Organization 转换为 OrganizationVO 对象
        for (Organization source : list) {
            OrganizationVO target = new OrganizationVO();
            BeanUtils.copyProperties(source, target);
            organizationList.add(target);
        }
        List<OrganizationVO> trees = CollUtil.newArrayList();

        // 遍历转换后的对象集合
        for (OrganizationVO vo : organizationList) {

            // 把第一次根据pid查询到的数据放进 trees 中
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(organizationOne)) {
                for (Organization o : organizationOne) {
                    if (vo.getId().equals(o.getId())) {
                        trees.add(vo);
                    }
                }
            }

            // 遍历所有的数据，开始构建树结构
            for (OrganizationVO organizationVO : organizationList) {
                if (organizationVO.getPid() != null && vo.getId() != null) {
                    if (organizationVO.getPid().equals(vo.getId())) {
                        if (org.apache.commons.collections4.CollectionUtils.isEmpty(vo.getChildren())) {
                            vo.setChildren(new ArrayList<>(4));
                        }
                        vo.getChildren().add(organizationVO);
                    }
                }
            }
        }
        Map map = MapUtil.newHashMap();
        map.put("content", trees.isEmpty() ? organizationList : trees);
        map.put("totalElements", organizationList != null ? organizationList.size() : 0);
        return map;
    }

    /**
     * 根据pid查询该组织下的树数据
     *
     * @param pid
     * @return
     */
    @Override
    public Map treeDataList(String pid) {
        return this.treeData(pid, null, null, false);
    }

    /**
     * 只查询到项目
     *
     * @param record 查询请求条件
     * @return
     */
    @Override
    public Map selectAll2(OrganizationForm record) {
        Map map = new HashMap();
        map.put("UserName", SecurityUtils.getUsername());
        Map userInfoMap = authMapper.mGetUserInfo(map);
        Map paramMap = new HashMap<>();
        paramMap.put("userName", SecurityUtils.getUsername());
        paramMap.put("jobCode", userInfoMap.get("JobCode") + "");
        paramMap.put("isNeedShow", record.getIsNeedShow());
        paramMap.put("projectId", record.getProjectId());
        paramMap.put("AuthCompanyID", record.getAuthCompanyId());
        List<Organization> list = organizationMapper.findOrgListByOrgIdAndProIdAndCompanyId(paramMap);
        String jobCode = userInfoMap.get("JobCode") + "";
        if (jobCode.equals("20001")) {
            list.get(0).setPid("-1");
        }
        return this.convert(list);
    }

    @Override
    public IPage<OrganizationVO> selectPage(OrganizationForm record) {
        // form -> do 转换
        Organization data = PropertyUtil.copy(record, Organization.class);

        // 分页数据设置
        Page<Organization> page = new Page<>(record.getCurrent(), record.getSize());
        // 查询条件
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(data);
        IPage<Organization> list = baseMapper.findOrgListByOrgIdAndProIdAndCompanyId(page, record.getAuthCompanyId(),
                record.getProductId(), record.getId(), record.getPid());
        IPage<OrganizationVO> iPage = new Page<>();
        iPage.setRecords(PropertyUtil.copy(list.getRecords(), List.class));
        iPage.setCurrent(list.getCurrent());
        iPage.setSize(list.getSize());
        iPage.setTotal(list.getTotal());
        iPage.setPages(list.getPages());
        return iPage;
    }

    @Override
    public Map queryChildOrgs(Map paramMap) {
        // form -> do 转换
        Map resultMap = new HashMap<>();
        int pageIndex = Integer.parseInt(paramMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(paramMap.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        paramMap.put("pageIndex", i);
        List list = organizationMapper.queryChildOrgs(paramMap);
        String total = organizationMapper.queryChildOrgsCount(paramMap);
        resultMap.put("list", list);
        resultMap.put("total",Integer.parseInt(total));
        return resultMap;
    }


    /**
     * Form -> Do
     *
     * @param form 对象
     * @return Do对象
     */
    private Organization convertDO(OrganizationForm form) {
        Organization data = new Organization();
        data.setId(form.getId());
        data.setPid(form.getPid());
        data.setOrgCode(form.getOrgCode());
        data.setOrgName(form.getOrgName());
        data.setOrgShortName(form.getOrgShortName());
        data.setOrgCategory(form.getOrgCategory());
        data.setListIndex(form.getListIndex());
        data.setLevels(form.getLevels());
        data.setFullPath(form.getFullPath());
        data.setAuthCompanyID(form.getAuthCompanyId());
        data.setProductId(form.getProductId());
        data.setCreator(form.getCreator());
//        data.setCreateTime(DateUtil.parseTime(form.getCreateTime()));
        data.setEditor(form.getEditor());
//        data.setEditTime(DateUtil.parseTime(form.getEditTime()));
        data.setStatus(form.getStatus());
        data.setIsDel(form.getIsDel());
        data.setCurrentPoint(form.getCurrentPoint());
        data.setProjectId(form.getProjectId());
        data.setOrgCompanyId(form.getOrgCompanyId());
        data.setOrgType(form.getOrgType());
        return data;
    }

    /**
     * Do -> VO
     *
     * @param list 对象
     * @return VO对象
     */
    private Map convert(List<Organization> list) {
        List<OrganizationVO> organizationList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return MapUtil.newHashMap();
        }
        OrganizationVO target = null;
        for (Organization source : list) {
            target = new OrganizationVO();
            BeanUtils.copyProperties(source, target);
            organizationList.add(target);
        }
        Map map = this.buildTree(organizationList);
        return map;
    }


    @Override
    public Integer updateStatusById(OrganizationForm organizationForm) {
        try {
            String username = SecurityUtils.getUsername();
            organizationForm.setUserName(username);
            return this.baseMapper.updateStatusById(organizationForm);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(-20_0001, e);
        }
    }

    /**
     * 构建 orgTree
     *
     * @param orgList
     * @return
     */
    public Map buildTree(List<OrganizationVO> orgList) {
        List<OrganizationVO> trees = CollUtil.newArrayList();
        for (OrganizationVO vo : orgList) {
            System.out.println(vo.getOrgName());
            if ("-1".equals(vo.getPid())) {
                trees.add(vo);
            }

            for (OrganizationVO organizationVO : orgList) {
                if (organizationVO.getPid() != null && vo.getId() != null) {
                    if (organizationVO.getPid().equals(vo.getId())) {
                        if (organizationDao.isExistNextOrg(organizationVO.getId()) > 0) {

                        }
                        else {
                            System.out.println(vo);
                            organizationVO.setChildren(null);
                        }
                        if (vo.getChildren()!=null){
                            vo.getChildren().add(organizationVO);
                        }
                    }
                }
            }
        }
        Map map = MapUtil.newHashMap();
        map.put("content", trees.size() == 0 ? orgList : trees);
        map.put("totalElements", orgList != null ? orgList.size() : 0);
        return map;
    }



    @Override
    public Map getUserDataRole(Map map) {
        Map<Object, Object> resultMap = new HashMap<>();
        String username=map.get("userName")+"";
   /*     Map resmenu = (Map) redisUtil.get("orgmenu"+username);
        if (resmenu!=null&&map.get("name").equals("")&&map.get("name")==""){
            System.out.println("走redis");
            return resmenu;
        }*/
        System.out.println("走数据库");
        String orgid = "";
        //查询出登录人的所有组织
        List<Map> orgList = organizationDao.getOrgList(map.get("userid")+"");
        if(orgList==null||orgList.size()<=0){
            resultMap.put("result",null);
            resultMap.put("totalElements",null);
            resultMap.put("code",200);
            return resultMap;
        }
        for (Map map1 : orgList) {
            orgid += "'" + map1.get("orgId") + "',";
        }

        String resOrgId = orgid.substring(0, orgid.length() - 1);
        map.put("resOrgId",resOrgId);
        List<Map> mapRegion = organizationDao.getThreeOrgList(map);
        //System.out.println("组织ID"+orgId);
        Map menusMap = buildTreeFour(mapRegion,orgList);
     /*   if(map.get("name").equals("")&&map.get("name")=="") {
            //redisUtil.set("orgmenu" + username, menusMap);
            redisUtil.set("orgmenu"+username,menusMap,36000);
        }*/
        return menusMap;
    }





    public static Map buildTreeFour(List<Map> menuList,List<Map> orgId) {
        List<Map> trees = CollUtil.newArrayList();
        List<Map> childrenMaps =null;
        List<Map> child =null;
        for (Map menu : menuList) {

            if ("-1".equals(menu.get("PID").toString())) {
                trees.add(menu);
            }
            if(orgId.size()>0){
                for (int i = 0; i < orgId.size(); i++) {
                    if(orgId.get(i).get("orgId").equals(menu.get("ID"))){
                        menu.put("isClick","true");
                    }
                }
            }

            childrenMaps= new ArrayList<>();
            for (Map it : menuList) {
                if (it.get("PID").equals(menu.get("ID"))) {
                    if(menu.get("isClick")!=null&&menu.get("isClick").equals("true")){
                        it.put("isClick","true");
                    }else{
                        if(orgId.size()>0){
                            for (int i = 0; i < orgId.size(); i++) {
                                if(orgId.get(i).get("orgId").equals(it.get("ID"))){
                                    it.put("isClick","true");
                                }
                            }
                        }
                    }

                    childrenMaps.add(it);

                    child= new ArrayList<>();
                    for (Map it2 : menuList) {
                        if (it2.get("PPID").equals(it.get("ID"))) {
                            if(it.get("isClick")!=null&&it.get("isClick").equals("true")){
                                it2.put("isClick","true");
                            }else{
                                if(orgId.size()>0){
                                    for (int i = 0; i < orgId.size(); i++) {
                                        if(orgId.get(i).get("orgId").equals(it2.get("ID"))){
                                            it2.put("isClick","true");
                                        }
                                    }
                                }
                            }
                            child.add(it2);
                            /*  childFour= new ArrayList<>();
                          for (Map it3 : menuList) {
                                if (it3.get("PPPID").equals(it2.get("ID"))) {
                                    it3.put("isClick","true");
                                    childFour.add(it3);
                                }
                            }
                            it2.put("child",childFour);*/
                        }
                    }
                    it.put("child",child);
                }
            }

            menu.put("children",childrenMaps);

        }
        Map map = MapUtil.newHashMap();
        map.put("result",trees.size() == 0?menuList:trees);
        map.put("totalElements",menuList!=null?menuList.size():0);
        map.put("code",200);
        return map;
    }
}
