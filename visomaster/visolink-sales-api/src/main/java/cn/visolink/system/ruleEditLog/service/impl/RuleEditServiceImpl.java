package cn.visolink.system.ruleEditLog.service.impl;

import cn.visolink.system.ruleEditLog.dao.RuleEditDao;
import cn.visolink.system.ruleEditLog.model.RuleEditLogBatch;
import cn.visolink.system.ruleEditLog.model.RuleEditLogDetail;
import cn.visolink.system.ruleEditLog.service.RuleEditService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName RuleEditServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/12/18 19:06
 **/
@Service
public class RuleEditServiceImpl implements RuleEditService {

    @Autowired
    private RuleEditDao ruleEditDao;
    @Override
    public Map batchParamQuery(Map<String, Object> map) {
        Map result = new HashMap();
        List<Map> types = new ArrayList<>();
        List<Map> params = new ArrayList<>();
        Map all = new HashMap();
        all.put("code","");
        all.put("name","全部");
        params.add(all);
        types.add(all);
        List<String> pList = new ArrayList<>();
        map.put("editType","2");
        if ("2".equals(map.get("editType")+"")){
            List<String> param = ruleEditDao.getSearchParams(map);
            if (param!=null && param.size()>0){
                for (String p:param) {
                    String[] datas = p.split(",");
                    for (String data:datas) {
                        if (!pList.contains(data)){
                            Map da = new HashMap();
                            da.put("code",data);
                            da.put("name",data);
                            params.add(da);
                            pList.add(data);
                        }
                    }
                }
            }
        }else{
            List<Map> typeList = ruleEditDao.getSearchTypeParam(map);
            if (typeList!=null && typeList.size()>0){
                types.addAll(typeList);
            }
            List<String> param = ruleEditDao.getSearchParams(map);
            if (param!=null && param.size()>0){
                for (String p:param) {
                    String[] datas = p.split(",");
                    for (String data:datas) {
                        if (!pList.contains(data)){
                            Map da = new HashMap();
                            da.put("code",data);
                            da.put("name",data);
                            params.add(da);
                            pList.add(data);
                        }
                    }
                }
            }
        }
        result.put("types",types);
        result.put("params",params);
        return result;
    }

    @Override
    public PageInfo<RuleEditLogBatch> batchQuery(Map<String, Object> map) {
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("pageIndex")!=null && !"".equals(map.get("pageIndex")+"")){
            pageIndex = Integer.parseInt(map.get("pageIndex")+"");
        }

        if (map.get("pageSize")!=null && !"".equals(map.get("pageSize")+"")){
            pageSize = Integer.parseInt(map.get("pageSize")+"");
        }
        PageHelper.startPage(pageIndex, pageSize);
        List<RuleEditLogBatch> result = ruleEditDao.getRuleEditBatch(map);
        if (result!=null && result.size()>0){
            for (RuleEditLogBatch rr:result) {
                if(rr.getEnclosureList()!=null){
                    String[] ee = rr.getEnclosureList().split(",");
                    rr.setEnclosures(Arrays.asList(ee));
                }
            }
        }
        return new PageInfo<>(result);
    }

    @Override
    public List<RuleEditLogDetail> batchDetailQuery(Map<String, Object> map) {
        return ruleEditDao.getRuleEditLogDetails(map);
    }
}
