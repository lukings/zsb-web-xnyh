package cn.visolink.system.invalidvisit.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.BadRequestException;
import cn.visolink.system.allpeople.contentManagement.model.Feedback;
import cn.visolink.system.invalidvisit.dao.InvalidVisitDao;
import cn.visolink.system.invalidvisit.model.InvalidVisit;
import cn.visolink.system.invalidvisit.service.InvalidVisitService;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.system.visitandcallexcel.model.ExcelModelVisitAndCall;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName InvalidVisitServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/10 10:37
 **/
@Service
public class InvalidVisitServiceImpl implements InvalidVisitService {

    @Autowired
    private InvalidVisitDao invalidVisitDao;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private AuthMapper authMapper;

    @Override
    public Map findInvalidVisitList(Map map) {

        Map hashMap = new HashMap<>();
        int pageIndex = 1;
        int pageSize = 10;
        if (map.get("index")!=null){
            pageIndex = Integer.parseInt(map.get("index")+"");
        }
        if (map.get("size")!=null){
            pageSize = Integer.parseInt(map.get("size")+"");
        }
        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            try{
                map.put("beginTime", DateUtil.beginOfDay(DateUtil.parse(String.valueOf(map.get("beginTime")))));
                map.put("endTime", DateUtil.endOfDay(DateUtil.parse(String.valueOf(map.get("endTime")))));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //项目id
        String projectId = String.valueOf(map.get("projectId"));
        //来访查询新访，复访，全部
        if (StrUtil.isBlank(projectId)) {
            throw new BadRequestException(-11_0002, "请选择项目!");
        }
        Map mapUser=new HashMap();
        mapUser.put("UserName", SecurityUtils.getUsername());
        Map userInfoMap = authMapper.mGetUserInfo(mapUser);
        List<String> fullpath = projectMapper.findFullPath(mapUser);
        StringBuffer sbs = new StringBuffer();
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sbs.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sbs.append("or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        //获取有权限的项目
        List<Map> mapLists = projectMapper.findProjectListByUserName( SecurityUtils.getUsername(),"",userInfoMap.get("JobCode").toString(),sbs.toString());
        List<String> proIdList = new ArrayList<>();
        if (mapLists!=null && mapLists.size()>0){
            for (Map proMap:mapLists) {
                proIdList.add(proMap.get("projectId")+"");
            }
        }

        String[] split = projectId.split(",");
        //判断是否有权限查询这些项目
        List<String> arrayToList= Arrays.asList(split);
        List<String> arrayList=new ArrayList<String>(arrayToList);//转换为ArrayLsit调用相关的remove方法
        //将无权限的删除
        for (int f = 0;f <arrayList.size();f++) {
            if (!proIdList.contains(arrayList.get(f))){
                arrayList.remove(f);
                f--;
            }
        }
        String[] strings = new String[arrayList.size()];
        arrayList.toArray(strings);
        String proIds = "'"+ StringUtils.join(strings,"','")+"'";
        if (map.get("visitReasonCodes")!=null && !"".equals(map.get("visitReasonCodes")+"")){
            String[] codes = String.valueOf(map.get("visitReasonCodes")).split(",");
            List<String> list = new ArrayList<>();
            for (String code:codes) {
                list.add(code);
            }
            map.put("visitReasonCodeList", list);
        }
        map.put("projectId", proIds);
        map.put("index", (pageIndex - 1) * pageSize);
        map.put("size", pageSize);
        Map countMap = invalidVisitDao.getCount(map);
        List<InvalidVisit> InvalidVisits = invalidVisitDao.findInvalidVisitList(map);
        hashMap.put("mapList", InvalidVisits);
        hashMap.put("count", countMap.get("visitCount"));
        hashMap.put("cstCount", countMap.get("cstCount"));
        return hashMap;
    }

    @Override
    public void invalidVisitExport(HttpServletRequest request, HttpServletResponse response, Map map) {

        if (map.get("beginTime")!=null && !"".equals(map.get("beginTime")+"") && map.get("endTime")!=null && !"".equals(map.get("endTime")+"")){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                map.put("beginTime", DateUtil.beginOfDay(DateUtil.parse(String.valueOf(map.get("beginTime")))));
                map.put("endTime", DateUtil.endOfDay(DateUtil.parse(String.valueOf(map.get("endTime")))));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //项目id
        String projectId = String.valueOf(map.get("projectId"));
        if (StrUtil.isBlank(projectId)) {
            throw new BadRequestException(-11_0002, "请选择项目!");
        }
        Map mapUser=new HashMap();
        mapUser.put("UserName", map.get("userName"));
        Map userInfoMap = authMapper.mGetUserInfo(mapUser);
        List<String> fullpath = projectMapper.findFullPath(mapUser);
        StringBuffer sbs = new StringBuffer();
        for (int i = 0; i < fullpath.size(); i++) {
            if (i==0){
                sbs.append("org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }else{
                sbs.append("or org.FullPath LIKE '"+fullpath.get(i)+"%'");
            }
        }
        //获取有权限的项目
        List<Map> mapLists = projectMapper.findProjectListByUserName( map.get("userName")+"","",userInfoMap.get("JobCode").toString(),sbs.toString());
        List<String> proIdList = new ArrayList<>();
        if (mapLists!=null && mapLists.size()>0){
            for (Map proMap:mapLists) {
                proIdList.add(proMap.get("projectId")+"");
            }
        }

        String[] split = projectId.split(",");
        //判断是否有权限查询这些项目
        List<String> arrayToList= Arrays.asList(split);
        List<String> arrayList=new ArrayList<String>(arrayToList);//转换为ArrayLsit调用相关的remove方法
        //将无权限的删除
        for (int f = 0;f <arrayList.size();f++) {
            if (!proIdList.contains(arrayList.get(f))){
                arrayList.remove(f);
                f--;
            }
        }
        String[] strings = new String[arrayList.size()];
        arrayList.toArray(strings);
        String proIds = "'"+ StringUtils.join(strings,"','")+"'";
        map.put("projectId", proIds);
        if (map.get("visitReasonCodes")!=null && !"".equals(map.get("visitReasonCodes")+"")){
            String[] codes = String.valueOf(map.get("visitReasonCodes")).split(",");
            List<String> list = new ArrayList<>();
            for (String code:codes) {
                list.add(code);
            }
            map.put("visitReasonCodeList", list);
        }
        List<InvalidVisit> InvalidVisits = invalidVisitDao.findInvalidVisitList(map);
        ArrayList<Object[]> dataset = new ArrayList<>();
        try{
            //导出的文档下面的名字
            String excelName = "无效客户登记明细";
            if (InvalidVisits!=null && InvalidVisits.size()>0){
                //循环遍历所有数据
                for (int z = 0; z < InvalidVisits.size(); z++) {
                    InvalidVisit map1 = InvalidVisits.get(z);
                    Object[] oArray = map1.toInvalidVisitData();
                    dataset.add(oArray);
                }
                String[] headers = InvalidVisits.get(0).toInvalidVisitTitle;
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel(excelName,headers,dataset,excelName,response,null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Map> getVisitReason(String authCompanyId) {
        return invalidVisitDao.getVisitReason(authCompanyId);
    }

    private void setDataToProjectCell(Workbook targetWorkBook, Row row2, int i, XSSFRow positionRow, InvalidVisit jobrow, CellStyle style) {
        CellStyle cs = targetWorkBook.createCellStyle();
        cs.cloneStyleFrom(style);
        cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        CellStyle cc = cs;

        XSSFCell cell1 = positionRow.createCell(0);
        cell1.setCellStyle(cc);
        cell1.setCellValue(Double.valueOf(jobrow.getNum()));
        XSSFCell cell2 = positionRow.createCell(1);
        cell2.setCellStyle(cc);
        cell2.setCellValue(jobrow.getProjectName());
        XSSFCell cell3 = positionRow.createCell(2);
        cell3.setCellStyle(cc);
        cell3.setCellValue(jobrow.getVisitTime());
        XSSFCell cell4 = positionRow.createCell(3);
        cell4.setCellStyle(cc);
        cell4.setCellValue(jobrow.getVisitNum());
        XSSFCell cell5 = positionRow.createCell(4);
        cell5.setCellStyle(cc);
        cell5.setCellValue(jobrow.getVisitReason());
        XSSFCell cell6 = positionRow.createCell(5);
        cell6.setCellStyle(cc);
        cell6.setCellValue(jobrow.getCreateTime());
        XSSFCell cell7 = positionRow.createCell(6);
        cell7.setCellStyle(cc);
        cell7.setCellValue(jobrow.getCreateUserName());
    }
}
