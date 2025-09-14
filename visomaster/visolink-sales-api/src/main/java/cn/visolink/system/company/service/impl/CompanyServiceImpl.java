package cn.visolink.system.company.service.impl;

import cn.visolink.system.activity.model.form.ActivityInfoForm;
import cn.visolink.system.company.dao.CompanyMapper;
import cn.visolink.system.company.model.vo.CompanyExport;
import cn.visolink.system.company.service.CompanyService;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.18
 */
@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private ExcelImportMapper excelImportMapper;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public List<Map> getAllList(Map map) {
        return companyMapper.getAllList(map);
    }   @Override
    public Integer getAllListCount(Map map) {
        return companyMapper.getAllListCount(map);
    }

    @Override
    public int insertCompany(Map map) {
        return companyMapper.insertCompany(map);
    }

    @Override
    public String isValidByOrgCode(String orgCode) {
        return companyMapper.isValidByOrgCode(orgCode);
    }

    @Override
    public List<Map> getAllProject() {
        return companyMapper.getAllProject();
    }

    @Override
    public PageInfo getAssInforData(Map paramMap) {
        // form -> do 转换
        int pageIndex = Integer.parseInt(paramMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(paramMap.get("pageSize").toString());
        PageHelper.startPage(pageIndex, pageSize);
        List list = companyMapper.getAssInforData(paramMap);
        PageInfo<Object> pageInfo = new PageInfo<>(list);

        return pageInfo;
    }

    @Override
    public int updateCompanyById(Map map) {
        //同时更新组织表中介状态
        companyMapper.updateCompanyOrgById(map);
        return companyMapper.updateCompanyById(map);
    }

    @Override
    public int updateCompanyStatus(Map paramMap) {
        String status = paramMap.get("Status")+"";
        String companyId = paramMap.get("ID")+"";
        String orgStatus = "";
        paramMap.put("companyId",companyId);
        //启用
        if (status.equals("1")){
        }else{
            //禁用
            orgStatus = "0";
            paramMap.put("Status",orgStatus);
            //同时更新组织表中介状态
            companyMapper.updateCompanyOrgById(paramMap);
        }
        paramMap.put("Status",status);
        return companyMapper.updateCompanyById(paramMap);
    }
    @Override
    public int deleteCompanyById(String id){
        return companyMapper.deleteCompanyById(id);
    }

    @Override
    public void companyExport(HttpServletRequest request, HttpServletResponse response, String param) {
        Map map = JSONObject.parseObject(param,Map.class);
        if (map.get("date") != null && !map.get("date").equals("")) {
            map.put("startTime", map.get("date").toString().substring(1, 11));
            map.put("endTime", map.get("date").toString().substring(13, 23));
        }
        ArrayList<Object[]> dataset = new ArrayList<>();
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("10");
        excelExportLog.setMainTypeDesc("中介管理");
        excelExportLog.setSubType("ZJ1");
        excelExportLog.setSubTypeDesc("中介明细");
        excelExportLog.setExportType("3");
        excelExportLog.setIsAsyn("0");
        try {
            excelExportLog.setCreator(map.get("userId")+"");
            excelExportLog.setAreaName("/");
            excelExportLog.setProjectId("/");
            excelExportLog.setProjectName("/");
            //导出的文档下面的名字
            List<CompanyExport> list = companyMapper.getAllListExport(map);
            if (list.size()>0){
                String[] headers = list.get(0).getCourtCaseTitle();
                for (int i = 0; i < list.size(); i++) {
                    CompanyExport companyExport = list.get(i);
                    companyExport.setRowNum((i + 1) + "");
                    Object[] oArray = companyExport.toData();
                    dataset.add(oArray);
                }
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("中介明细", headers,dataset, "中介明细", response,null);
            }
            excelExportLog.setExportStatus("2");
            excelExportLog.setDownLoadTime(sf.format(new Date()));
            excelExportLog.setIsDown("1");
            Long export = new Date().getTime();
            Long exporttime = export-nowtime;
            String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
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


}
