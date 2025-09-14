package cn.visolink.system.parameter.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.parameter.dao.ParameterManagementDao;
import cn.visolink.system.parameter.model.vo.ProjectBank;
import cn.visolink.system.parameter.model.vo.ProjectDiyCode;
import cn.visolink.system.parameter.service.ParameterManagementService;
import cn.visolink.utils.ChineseCharacterUtil;
import cn.visolink.utils.DbTest;
import cn.visolink.utils.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Transactional(rollbackFor = Exception.class)
public class ParameterManagementServiceImpl implements ParameterManagementService {

    private List<Map> childMenu = new ArrayList<>();

    @Autowired
    private ParameterManagementDao parameterDao;

    @Autowired
    private ChineseCharacterUtil chineseCharacterUtil;

    /**
     * 查询系统所有的参数
     *
     * @param reqMap
     */
    @Override
    public List<Map> getSystemAllparams(HashMap<String, String> reqMap) {
        //查询所有的参数
        List<Map> resMap = parameterDao.getSystemAllParams(reqMap);
        //结果集合
        ArrayList<Object> resultList = new ArrayList<>();
        //一级菜单
        ArrayList<Map> oneList = new ArrayList<>();
        //二级菜单
        CopyOnWriteArrayList<Map> twoList = new CopyOnWriteArrayList<>();
        String type = reqMap.get("type")+"";
        //遍历查询参数
        for (Map map : resMap) {

            String levels = String.valueOf(map.get("Levels"));
            String dictionaryLevel = String.valueOf(map.get("DictionaryLevel"));
            if ("1".equals(type)){
                //一级菜单
                if ("0".equals(levels) || "1".equals(dictionaryLevel)) {
                    oneList.add(map);
                    continue;
                }else{
                    twoList.add(map);
                    continue;
                }
            }else{
                //一级菜单
                if ("0".equals(levels)) {
                    oneList.add(map);
                    continue;
                }else{
                    twoList.add(map);
                    continue;
                }
            }

            //二级菜单
           /* if ("1".equals(levels)) {

            }*/
        }

        //遍历一级菜单
        for (Map oneMap : oneList) {
            String id = String.valueOf(oneMap.get("ID"));
            //遍历二级菜单
            ArrayList<Object> list = new ArrayList<>();
            for (Map twoMap : twoList) {
                String pid = String.valueOf(twoMap.get("PID"));
                if (id.equals(pid)) {
                    List<Map> ll = parameterDao.getParamsByPid(String.valueOf(twoMap.get("ID")));
                    if (ll!=null && ll.size()>0){
                        twoMap.put("Children",ll);//三级
                        for (Map tt:ll) {
                            String tId = String.valueOf(tt.get("ID"));
                            List<Map> ff = parameterDao.getParamsByPid(tId);
                            if (ff!=null && ff.size()>0){
                                tt.put("Children",ff);//四级
                            }
                        }
                    }
                    list.add(twoMap);
                    twoList.remove(twoMap);
                }
            }
                oneMap.put("Children", list);
        }
        return oneList;
    }

    /**
     * 系统新增参数
     *
     * @param reqMap
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveSystemParam(Map reqMap) {
        /*reqMap.put("ID", UUID.randomUUID().toString());
        //判断是否项目级参数
        if (reqMap.get("Type")!=null && "pro".equals(reqMap.get("Type")+"")){
            reqMap.put("DictCode",UUID.randomUUID().toString());
        }else{
            Map paramCodeExists = parameterDao.getSystemParamCodeExists(reqMap);
            Integer isExists = Integer.valueOf(paramCodeExists.get("A").toString());
            if (isExists != null && isExists > 0) {
                throw new BadRequestException("参数编码已存在！");
            }
        }
        try {
            String IsReadOnly = reqMap.get("IsReadOnly") + "";
            if (reqMap.get("IsReadOnly") == null || "".equals(IsReadOnly) || "null".equalsIgnoreCase(IsReadOnly)) {
                reqMap.put("IsReadOnly", "1");
            }
            int i = parameterDao.insertSystemParam(reqMap);
            return i;

        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BadRequestException("添加参数异常！");
        }*/
        reqMap.put("ID", UUID.randomUUID().toString());
        //判断是否项目级参数
        reqMap.put("DictCode",UUID.randomUUID().toString());
        reqMap.put("ProjectID",null);
        Integer isExists = parameterDao.getSystemParamName(reqMap);
        if (isExists != null && isExists > 0) {
            throw new BadRequestException("目录已存在！");
        }
        try {
            String IsReadOnly = reqMap.get("IsReadOnly") + "";
            if (reqMap.get("IsReadOnly") == null || "".equals(IsReadOnly) || "null".equalsIgnoreCase(IsReadOnly)) {
                reqMap.put("IsReadOnly", "1");
            }
            int i = parameterDao.insertSystemParam(reqMap);
            return i;

        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BadRequestException("添加目录异常！");
        }
    }

    @Override
    public int saveSystemParamNew(Map reqMap) {
        reqMap.put("ID", UUID.randomUUID().toString());
        int paramCodeExists = parameterDao.getSystemParamCodeExists1(reqMap);
        if (paramCodeExists > 0) {
            throw new BadRequestException("参数编码已存在！");
        }
        try {
            int i = parameterDao.insertSystemParam(reqMap);
            return i;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("添加参数异常！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveSystemParamSecond(Map reqMap) {

        Map paramCodeExists = parameterDao.getSystemParamCodeExists(reqMap);
        Integer isExists = Integer.valueOf(paramCodeExists.get("A").toString());
        if (isExists != null && isExists > 0) {
            throw new BadRequestException("参数编码已存在！");
        }

        if (reqMap.get("Levels") == null || StringUtils.isEmpty(reqMap.get("Levels").toString())) {
            reqMap.put("Levels", 1);
        }
        if(reqMap.get("isALlProject")!=null&&reqMap.get("isALlProject").equals(1)){
            List<Map> list = parameterDao.getAllProjectList();
            for (Map map : list) {
                reqMap.put("ID", UUID.randomUUID().toString());
                reqMap.put("ProjectID",map.get("projectId"));
                reqMap.put("globalFlag", "1");
                parameterDao.insertSystemParamSecond(reqMap);
                if (reqMap.get("DefinitionDefaultsList") != null) {
                    List<Map> childList = (List<Map>) reqMap.get("DefinitionDefaultsList");
                    if (!CollectionUtils.isEmpty(childList)) {
                        int i = 0;
                        int Levels = 2;
                        for (Map childMap : childList) {
                            childMap.put("ListIndex", i);
                            childMap.put("Levels", Levels);
                            childMap.put("PID", reqMap.get("ID"));
                            childMap.put("ID", UUID.randomUUID().toString());
                            childMap.put("PublicPid", reqMap.get("ID"));
                            i++;
                            this.addParamChildren(childMap, Levels, reqMap, reqMap.get("ID").toString());
                        }

                        parameterDao.batchSecondParamChildren(childList, reqMap);
                    }
                }
            }
        }
        else{
            reqMap.put("ID", UUID.randomUUID().toString());
            reqMap.put("globalFlag", null);
            parameterDao.insertSystemParamSecond(reqMap);
            if (reqMap.get("DefinitionDefaultsList") != null) {
                List<Map> childList = (List<Map>) reqMap.get("DefinitionDefaultsList");
                if (!CollectionUtils.isEmpty(childList)) {
                    int i = 0;
                    int Levels = 2;
                    for (Map childMap : childList) {
                        childMap.put("ListIndex", i);
                        childMap.put("Levels", Levels);
                        childMap.put("PID", reqMap.get("ID"));
                        childMap.put("ID", UUID.randomUUID().toString());
                        childMap.put("PublicPid", reqMap.get("ID"));
                        i++;
                        this.addParamChildren(childMap, Levels, reqMap, reqMap.get("ID").toString());
                    }

                    parameterDao.batchSecondParamChildren(childList, reqMap);
                }
            }
        }
//        if (count == 0) {
//            throw new BadRequestException("添加失败！");
//        }

        return 0;
    }

    private Integer addParamChildren(Map childMap, int Levels, Map reqMap, String publicPid){
        if (childMap.get("DefinitionDefaultsList") == null) {
            return 0;
        }
        List<Map> childList = (List<Map>) childMap.get("DefinitionDefaultsList");
        if (CollectionUtils.isEmpty(childList)) {
            return 0;
        }
        int i = 0;
        Levels++;
        for (Map map : childList) {
            map.put("ListIndex", i);
            map.put("Levels", Levels);
            map.put("PID", childMap.get("ID"));
            map.put("ID", UUID.randomUUID().toString());
            map.put("PublicPid", publicPid);
            this.addParamChildren(map, Levels, reqMap, publicPid);
        }
        parameterDao.batchSecondParamChildren(childList, reqMap);
        return 0;
    }

    /**
     * 系统排序参数
     *
     * @param reqMap
     * @return
     */
    @Override
    public int orderParam(Map reqMap) {
        if(reqMap.get("type").equals(0)){
            System.out.println("减少");
            parameterDao.orderParamDel(reqMap);
        }else{
            System.out.println("增加");
            parameterDao.orderParamAdd(reqMap);
        }
        return 1;
    }

    /**
     * 系统修改参数
     *
     * @param reqMap
     * @return
     */
    @Override
    public int modifySystemParam(Map reqMap) {

        int i = parameterDao.modifySystemParam(reqMap);
        if (i < 1) {
            return i;
        }
        if (reqMap.get("DictTypeMode") != null) {
            if (!"1".equals(reqMap.get("DictTypeMode").toString()) && !"2".equals(reqMap.get("DictTypeMode").toString())
                    && !"3".equals(reqMap.get("DictTypeMode").toString())  && !"4".equals(reqMap.get("DictTypeMode").toString())) {
                List<Map> list = parameterDao.getParamsByPid(reqMap.get("ID").toString());
                if (!CollectionUtils.isEmpty(list)) {
                    parameterDao.batchRemoveSystemParam(list);
                }
                if (reqMap.get("DefinitionDefaultsList") != null) {
                    List<Map> childList = (List<Map>) reqMap.get("DefinitionDefaultsList");
                    if (!CollectionUtils.isEmpty(childList)) {
                        int index = 0;
                        int Levels = 2;
                        for (Map childMap : childList) {
                            childMap.put("ListIndex", index);
                            childMap.put("Levels", Levels);
                            childMap.put("PID", reqMap.get("ID"));
                            childMap.put("ID", UUID.randomUUID().toString());
                            childMap.put("PublicPid", reqMap.get("ID"));
                            index++;
                            this.addParamChildren(childMap, Levels, reqMap, reqMap.get("ID").toString());
                        }
                        parameterDao.batchSecondParamChildren(childList, reqMap);
                    }
                }
            }
        }
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int modifyParamTertiary(Map reqMap) {
        parameterDao.modifyParamTertiary(reqMap);

        // 根据主键ID查询项目id
        String project_id = parameterDao.getProjectIdById((String) reqMap.get("ID"));
        if (StringUtils.isNotEmpty(project_id)) {
            // 跟新渠道团队和案场团队到项目表中
            Map map = new HashMap();
            map.put("projectId", project_id);
            String customArrayOnes = ((String) reqMap.get("customArrayOne")).replace("[", "").replace("]", "").replace("\"", "");
            String customArrayTwo = ((String) reqMap.get("customArrayTwo")).replace("[", "").replace("]", "").replace("\"", "");

            map.put("customArrayOne", customArrayOnes);
            map.put("customArrayTwo", customArrayTwo);

            // 跟新数据到项目表中
            parameterDao.updateProjectByProjectId(map);
        }

        if (reqMap.get("DictTypeMode") != null) {
            if ("8".equals(reqMap.get("DictTypeMode").toString()) || "9".equals(reqMap.get("DictTypeMode").toString())
                    || "10".equals(reqMap.get("DictTypeMode").toString())  || "11".equals(reqMap.get("DictTypeMode").toString())) {
                if (reqMap.get("DefinitionDefaultsList") != null) {
                    List<Map> childList = (List<Map>) reqMap.get("DefinitionDefaultsList");
                    if (!CollectionUtils.isEmpty(childList)) {
                        for (Map map : childList) {
                            map.put("Editor", reqMap.get("Editor"));
                            parameterDao.modifyParamTertiary(map);
                        }

                    }
                }
            }
        }
        return 0;
    }



    @Override
    public Map getInfoById(Map reqMap) {
        Map map = parameterDao.getInfoById(reqMap);
        if (map != null) {
            if (map.get("customArrayOne") == null) {
                map.put("customArrayOne", null);
            }
            if (map.get("customArrayTwo") == null) {
                map.put("customArrayTwo", null);
            }
        }

        return map;
    }

    /**
     * 删除系统参数
     *
     * @param reqMap
     * @return
     */
    @Override
    public int removeSystemParam(Map reqMap) {

        return parameterDao.removeSystemParam(reqMap);
    }

    /**
     * 查询子集参数（树形）
     *
     * @param reqMap
     * @return
     */
    @Override
    public List<Map> getSystemTreeChildParams(Map reqMap) {

        return parameterDao.getSystemTreeChildParams(reqMap);
    }

    @Override
    public Map getSystemChildParams(Map reqMap) {
        Map<String, Object> map = MapUtil.newHashMap();
        int pageIndex = Integer.parseInt(reqMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        reqMap.put("pageIndex", i);
        String pid = reqMap.get("pid").toString();
        //判断当前查询的为几级菜单下的
        Integer levels = this.isMYStyle(pid);
        map.put("paramLevls", levels);
        reqMap.put("ID", reqMap.get("pid"));
        Map CurrentPidParams = parameterDao.getInfoById(reqMap);
        if (levels == 3 && CurrentPidParams != null && CurrentPidParams.get("DictTypeMode") != null) {
            String DictTypeMode = CurrentPidParams.get("DictTypeMode").toString();
            //对子类只有单个文本框的参数进行返会结果处理
            if ("1".equals(DictTypeMode) || "2".equals(DictTypeMode)
                    || "3".equals(DictTypeMode) || "4".equals(DictTypeMode)) {
                List<Map>  resultList = new ArrayList<>();
                resultList.add(CurrentPidParams);
                map.put("systemParams", resultList);
                map.put("count", 1);
                return map;
            }
            if ("5".equals(DictTypeMode) || "6".equals(DictTypeMode)
                    || "7".equals(DictTypeMode) || "8".equals(DictTypeMode) || "9".equals(DictTypeMode)) {
                List<Map> projectChildParams = parameterDao.getChildParams(reqMap);
                Map systemChildParamsCount = parameterDao.getChildParamsCount(reqMap);
                map.put("systemParams", projectChildParams);
                map.put("count", systemChildParamsCount.get("count"));
                return map;
            }
            //对有多层级子类的参数进行返会结果处理
            if ("10".equals(DictTypeMode) || "11".equals(DictTypeMode)) {
                List<Map> allChildParamsList = parameterDao.getAllChildParamsById(pid);
                if (CollectionUtils.isEmpty(allChildParamsList)) {
                    map.put("systemParams", new ArrayList<>());
                    map.put("count", 0);
                    return map;
                }
                List<Map> resultList = new ArrayList<>();
                //首先查找pid下面的一级参数
                for (Map childMap : allChildParamsList) {
                    if (pid.equals(childMap.get("PID").toString())) {
                        resultList.add(childMap);
                    }
                }
                //然后查找一级参数下面的所有子参数
                for (Map childMap : resultList) {
                    List<Map> childList = this.treeMenuList(allChildParamsList, childMap.get("ID").toString(), new ArrayList<>());
                    childMap.put("childList", childList);
                }
                map.put("systemParams", resultList);
                map.put("count", resultList.size());
                return map;
            }
        }

        List<Map> projectChildParams = parameterDao.getSystemChildParams(reqMap);
        Map systemChildParamsCount = parameterDao.getSystemChildParamsCount(reqMap);
        map.put("systemParams", projectChildParams);
        map.put("count", systemChildParamsCount.get("count"));
        return map;
    }

    /**
     * 启用/禁用参数
     *
     * @param reqMap
     * @return
     */
    @Override
    public int modifySystemParamStatus(Map reqMap) {
        return parameterDao.modifySystemParamStatus(reqMap);
    }

    /**
     * 新增项目自定义字段
    * */
    @Override
    public int saveProjectDiyCode(Map map) {
        return parameterDao.saveProjectDiyCode(map);
    }


    /**
     * 获取自定义参数
     * */

    @Override
    public List<Map> getProjectDiyCode(ProjectDiyCode projectDiyCode) {
        return parameterDao.getProjectDiyCode(projectDiyCode);
    }


    /**
     * 修改自定义参数
     * */
    @Override
    public int updateProjectDiyCode(Map map) {
        return parameterDao.updateProjectDiyCode(map);
    }


    /**
     * 修改项目状态
    * */
    @Override
    public int updateCodeStatus(Map map) {
        return parameterDao.updateCodeStatus(map
        );
    }

    @Override
    public List<Map> getProjectStages(String projectId) {
        return parameterDao.getProjectStages(projectId);
    }

    /**
     * 获取项目分期
     * */
    @Override
    public List<Map<String,Object>> getBankInfo(String stageId, String bankText) {
        String sql = "select projectFid,projectFname,text,value from VS_XK_RzBank where 1=1 ";
        if(stageId!=null && !"".equals(stageId)){
            sql+=" and projectFid="+stageId;
        }
        if(bankText!=null &&!"".equals(bankText)&&!"''".equals(bankText)){
            sql+=" and text="+bankText;
        }
        List<Map<String,Object>> list = DbTest.getObjects(sql);
        return list;
    }

    @Override
    public int saveProjectBankInfo(ProjectBank projectBank) {
        return parameterDao.saveProjectBankInfo(projectBank);
    }
    @Override
    public int updateProjectBankInfo(ProjectBank projectBank) {
        return parameterDao.updateProjectBankInfo(projectBank);
    }

    @Override
    public ResultBody getProjectBankInfo(ProjectBank projectBank) {
         String bakId = parameterDao.getProjectBankInfo(projectBank);
         if(bakId!=null){
             return ResultBody.error(-13_0017,"该分期下已存在此银行或商户信息，请重新选择！");
         }
         return ResultBody.success(true);
    }

    @Override
    public boolean checkActivityBank(ProjectBank projectBank) {
        List<Map>  list = parameterDao.getBuildId(projectBank.getProjectFid());
        int orderRes = parameterDao.getXkhOrderStatus(projectBank.getProjectId());
        if(orderRes>0){
            return false;
        }else if(list.size()>0){
            String param="";
            for (Map map : list) {
                param+="'"+map.get("build_id")+"',";
            }
            String sql = "SELECT count(1) as res FROM VS_XK_S_BUILDING WHERE  PrjectFGUID='"+projectBank.getProjectFid()+"' and BldGUID IN ("+param.substring(0,param.length()-1)+")";
            Map result = DbTest.getObject(sql);
            if(result!=null&&Integer.parseInt(result.get("res")+"")>0){
                return false;
            }
        }
        return true;
    }

    @Override
    public PageInfo getBankList(ProjectBank projectBank) {
        PageHelper.startPage(projectBank.getPageNum(),projectBank.getPageSize());
        List<Map> list = parameterDao.getBankList(projectBank.getProjectId(),projectBank.getProjectFid(),projectBank.getProCollAccount());
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    private Integer isMYStyle (String pid) {
        if (!"-1".equals(pid)) {
            Map paramsByIdMap = parameterDao.getParamsById(pid);
            if (paramsByIdMap != null ) {
                if ("-1".equals(paramsByIdMap.get("PID").toString()) || "8384E489-BE94-0B8E-FD4D-6667914920CB".equals(paramsByIdMap.get("PID").toString())) {
                    return 2;
                }else {
                    return 3;
                }
            }
        }
        return 1;
    }

    private List<Map> treeMenuList(List<Map> menuList, String pid, List<Map> resultList){
        for(Map map: menuList){
            //遍历出父id等于参数的id，add进子节点集合
            if(pid.equals(map.get("PID").toString())){
                //递归遍历下一级
                map.put("childList", treeMenuList(menuList, map.get("ID").toString(), new ArrayList<>()));
                resultList.add(map);
            }
        }
        return resultList;
    }

}
