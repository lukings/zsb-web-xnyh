package cn.visolink.system.excel.service.impl;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.model.SysLog;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.DistVO;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.excel.service.ExcelImportService;
import cn.visolink.system.excel.util.DataCastUtil;
import cn.visolink.system.excel.util.StringToDate;
import cn.visolink.system.parameter.model.vo.DictionaryVO;
import cn.visolink.system.project.dao.ProjectMapper;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.26
 */
@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    //private static final Logger logger = LoggerFactory.getLogger(ExcelImportServiceImpl.class);
    
    DataCastUtil dataCastUtil = new DataCastUtil();
    @Autowired
    private ExcelImportMapper excelImportMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private AuthMapper authMapper;

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public Map batchImport(String fileName, MultipartFile file) throws Exception {


        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        InputStream is = file.getInputStream();
        Workbook wb = null;
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        Sheet sheet = wb.getSheetAt(0);
        //导入之前清空临时表
        Map resultMap = new HashMap();
        excelImportMapper.delImportData();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            //r = 2 表示从第三行开始循环 如果你的第三行开始是数据

            Row row = sheet.getRow(r);

            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }
            //通过sheet表单对象得到 行对象
            if (row == null) {
                resultMap.put(r, "错误信息：第" + r + "行【数据为空！】");
                continue;
            }
            if (row.getCell(0)==null){
                break;
            }
            //交易渠道
            String num = row.getCell(0).getStringCellValue();
            String sourceTypeDesc = row.getCell(1).getStringCellValue();
            String customerName = row.getCell(2).getStringCellValue();
            String customerGender = row.getCell(3).getStringCellValue();
            String customerMobile = row.getCell(4).getStringCellValue();
            String projectName = row.getCell(5).getStringCellValue();
            String userName = "";
            if (row.getCell(6)!=null){
                userName = row.getCell(6).getStringCellValue();
            }
            String reportCreateTime;

            if(row.getCell(7)==null || row.getCell(7).getStringCellValue().equals("")){
                 reportCreateTime=null;
            }else{
               reportCreateTime = StringToDate.toDate(Double.parseDouble(row.getCell(7).getStringCellValue()),"yyyy-MM-dd HH:mm:ss");
            }
            String visitDate;
            if(row.getCell(8)==null || row.getCell(8).getStringCellValue().equals("")){
                 visitDate =null;
            }else{
                 visitDate = StringToDate.toDate(Double.parseDouble(row.getCell(8).getStringCellValue()),"yyyy-MM-dd HH:mm:ss");
            }
            String reportUserName = "";
            if (row.getCell(9)!=null){
                reportUserName = row.getCell(9).getStringCellValue();
            }
            String companyName = "";
            if (row.getCell(10)!=null){
                companyName = row.getCell(10).getStringCellValue();
            }
            String mainMediaName = "";
            if (row.getCell(11)!=null){
                mainMediaName = row.getCell(11).getStringCellValue();
            }
            String subMediaName = "";
            if (row.getCell(12)!=null){
                subMediaName = row.getCell(12).getStringCellValue();
            }
            String ageGroup = "";
            if (row.getCell(13)!=null){
                ageGroup = row.getCell(13).getStringCellValue();
            }
            String workAreaDesc = "";
            if (row.getCell(14)!=null){
                workAreaDesc = row.getCell(14).getStringCellValue();
            }
            String belongIndustriseDesc = "";
            if (row.getCell(15)!=null){
                belongIndustriseDesc = row.getCell(15).getStringCellValue();
            }
            String lifeAreaDesc = "";
            if (row.getCell(16)!=null){
                lifeAreaDesc = row.getCell(16).getStringCellValue();
            }
            String familyStructureDesc = "";
            if (row.getCell(17)!=null){
                familyStructureDesc = row.getCell(17).getStringCellValue();
            }
            String familyIncomeDesc = "";
            if (row.getCell(18)!=null){
                familyIncomeDesc = row.getCell(18).getStringCellValue();
            }
            String purchasePurposeDesc = "";
            if (row.getCell(19)!=null){
                purchasePurposeDesc = row.getCell(19).getStringCellValue();
            }
            String homeNumDesc = "";
            if (row.getCell(20)!=null){
                homeNumDesc = row.getCell(20).getStringCellValue();
            }
            String currentHouseTypeDesc = "";
            if (row.getCell(21)!=null){
                currentHouseTypeDesc = row.getCell(21).getStringCellValue();
            }
            String intentionalAreaDesc = "";
            if (row.getCell(22)!=null){
                intentionalAreaDesc = row.getCell(22).getStringCellValue();
            }
            String intentionalFloorDesc = "";
            if (row.getCell(23)!=null){
                intentionalFloorDesc = row.getCell(23).getStringCellValue();
            }
            String acceptPriceDesc = "";
            if (row.getCell(24)!=null){
                acceptPriceDesc = row.getCell(24).getStringCellValue();
            }
            String acceptTotalPriceDesc = "";
            if (row.getCell(25)!=null){
                acceptTotalPriceDesc = row.getCell(25).getStringCellValue();
            }
            String qualifications = "";
            if (row.getCell(26)!=null){
                qualifications = row.getCell(26).getStringCellValue();
            }
            String cardTypeDesc = "";
            if (row.getCell(27)!=null){
                cardTypeDesc = row.getCell(27).getStringCellValue();
            }
            String cardNum = "";
            if (row.getCell(28)!=null){
                cardNum = row.getCell(28).getStringCellValue();
            }
            String buyPointDesc = "";
            if (row.getCell(29)!=null){
                buyPointDesc = row.getCell(29).getStringCellValue();
            }
            String resistanceDesc = "";
            if (row.getCell(30)!=null){
                resistanceDesc = row.getCell(30).getStringCellValue();
            }
            String remarks = "";
            if (row.getCell(31)!=null){
                remarks = row.getCell(31).getStringCellValue();
            }
            if (customerName.equals("")){
                customerName=null;
            }if (customerGender.equals("")){
                customerGender=null;
            }if (customerMobile.equals("")){
                customerMobile=null;
            }
            if (userName.equals("")){
                userName=null;
            }if (reportUserName.equals("")){
                reportUserName=null;
            }if (companyName.equals("")){
                companyName=null;
            }if (mainMediaName.equals("")){
                mainMediaName=null;
            }if (subMediaName.equals("")){
                subMediaName=null;
            }if (ageGroup.equals("")){
                ageGroup=null;
            }if (workAreaDesc.equals("")){
                workAreaDesc=null;
            }if (belongIndustriseDesc.equals("")){
                belongIndustriseDesc=null;
            }if (lifeAreaDesc.equals("")){
                lifeAreaDesc=null;
            }if (familyStructureDesc.equals("")){
                familyStructureDesc=null;
            }if (familyIncomeDesc.equals("")){
                familyIncomeDesc=null;
            }if (purchasePurposeDesc.equals("")){
                purchasePurposeDesc=null;
            }if (homeNumDesc.equals("")){
                homeNumDesc=null;
            }if (currentHouseTypeDesc.equals("")){
                currentHouseTypeDesc=null;
            }if (intentionalAreaDesc.equals("")){
                intentionalAreaDesc=null;
            }if (intentionalFloorDesc.equals("")){
                intentionalFloorDesc=null;
            }
            if (acceptPriceDesc.equals("")){
                acceptPriceDesc=null;
            }
            if (acceptTotalPriceDesc.equals("")){
                acceptTotalPriceDesc=null;
            }
            if (qualifications.equals("")){
                qualifications=null;
            }
            if (cardTypeDesc.equals("")){
                cardTypeDesc=null;
            }
            if (cardNum.equals("")){
                cardNum=null;
            }
            if (buyPointDesc.equals("")){
                buyPointDesc=null;
            }
            if (resistanceDesc.equals("")){
                resistanceDesc=null;
            }
            if (remarks.equals("")){
                remarks=null;
            }
            Map saveMap = new HashMap();
            saveMap.put("sourceTypeDesc", sourceTypeDesc);
            saveMap.put("customerName", customerName);
            saveMap.put("customerGender", customerGender);
            saveMap.put("customerMobile", customerMobile);
            saveMap.put("projectName", projectName);
            saveMap.put("userName", userName);
            saveMap.put("reportCreateTime", reportCreateTime);
            saveMap.put("visitDate", visitDate);
            saveMap.put("reportUserName", reportUserName);
            saveMap.put("companyName", companyName);
            if (mainMediaName == null) {
//                saveMap.put("mainMediaName", "阵地包装");
                saveMap.put("mainMediaName", "其他");
            } else {
                saveMap.put("mainMediaName", mainMediaName);
            }
            if (subMediaName == null) {
//                saveMap.put("subMediaName", "路过");
                saveMap.put("subMediaName", "其他");
            } else {
                saveMap.put("subMediaName", subMediaName);
            }
            saveMap.put("ageGroup", ageGroup);
            saveMap.put("workAreaDesc", workAreaDesc);
            saveMap.put("belongIndustriseDesc", belongIndustriseDesc);
            saveMap.put("lifeAreaDesc", lifeAreaDesc);
            saveMap.put("familyStructureDesc", familyStructureDesc);
            saveMap.put("familyIncomeDesc", familyIncomeDesc);
            saveMap.put("purchasePurposeDesc", purchasePurposeDesc);
            saveMap.put("homeNumDesc", homeNumDesc);
            saveMap.put("currentHouseTypeDesc", currentHouseTypeDesc);
            saveMap.put("intentionalAreaDesc", intentionalAreaDesc);
            saveMap.put("intentionalFloorDesc", intentionalFloorDesc);
            saveMap.put("acceptPriceDesc", acceptPriceDesc);
            saveMap.put("acceptTotalPriceDesc", acceptTotalPriceDesc);
            saveMap.put("qualifications", qualifications);
            saveMap.put("cardTypeDesc", cardTypeDesc);
            saveMap.put("cardNum", cardNum);
            saveMap.put("buyPointDesc", buyPointDesc);
            saveMap.put("resistanceDesc", resistanceDesc);
            saveMap.put("remarks", remarks);
            saveMap.put("fileName", fileName);
            //首先校验项目是否为空
            if (projectName == "" || projectName == null) {
                saveMap.put("error", "错误信息：第" + r + "行数据【项目为空！】");
                resultMap.put(r, "错误信息：第" + r + "行数据【项目为空！】");
                saveMap.put("row", r);
                excelImportMapper.saveError(saveMap);
                continue;
            }
            //校验项目是否存在
            int checkProject = excelImportMapper.checkProjectName(projectName);
            if (checkProject == 0) {
                saveMap.put("error", "错误信息：第" + r + "行数据【项目不存在！】");
                resultMap.put(r, "错误信息：第" + r + "行数据【项目不存在！】");
                saveMap.put("row", r);
                excelImportMapper.saveError(saveMap);
                continue;
            }
            if(customerMobile==null){
                customerMobile="";
            }
            //校验手机号是否11位
            if (customerMobile.length() != 11) {
                saveMap.put("error", "错误信息：第" + r + "行数据【手机号" + customerMobile + "格式错误！】");
                resultMap.put(r, "错误信息：第" + r + "行数据【手机号" + customerMobile + "格式错误！】");
                saveMap.put("row", r);
                excelImportMapper.saveError(saveMap);
                continue;
            }
            //校验业务员
           /* if (userName == "" || userName == null) {
                saveMap.put("error", "错误信息：第" +r+ "行数据【业务员为空！】");
                resultMap.put(r, "错误信息：第" +r+"行数据【业务员为空！】");
                saveMap.put("row",r);
                excelImportMapper.saveError(saveMap);
                continue;
            }*/
            //校验业务员是否存在
            if (userName != "" && userName != null) {
                int checkUserName = excelImportMapper.checkUserName(userName);
                if (checkUserName == 0) {
                    saveMap.put("error", "错误信息：第" + r + "行数据【业务员" + userName + "不存在！】");
                    resultMap.put(r, "错误信息：第" + r + "行数据【业务员" + userName + "不存在！】");
                    saveMap.put("row", r);
                    excelImportMapper.saveError(saveMap);
                    continue;
                }
            }
            //校验报备人OA账号
            if (reportUserName == "" || reportUserName == null) {
                saveMap.put("reportUserName", userName);
                reportUserName = userName;
           /*     saveMap.put("error", "错误信息：第" +r+ "行数据【报备人OA账号为空！】");
                resultMap.put(r, "错误信息：第" +r+ "行数据【报备人OA账号为空！】");
                excelImportMapper.saveError(saveMap);
                continue;*/
            }

            //校验报备人OA账号是否存在
            int checkReportUserName = excelImportMapper.checkUserName(reportUserName);
            if (checkReportUserName == 0) {
                saveMap.put("error", "错误信息：第" + r + "行数据【报备人" + reportUserName + "OA账号不存在！】");
                resultMap.put(r, "错误信息：第" + r + "行数据【报备人" + reportUserName + "OA账号不存在！】");
                saveMap.put("row", r);
                excelImportMapper.saveError(saveMap);
                continue;
            }
            //校验中介公司
            if (sourceTypeDesc.equals("中介成交")) {
                if (companyName == "" || companyName == null) {
                    saveMap.put("error", "错误信息：第" + r + "行数据【中介公司为空！】");
                    resultMap.put(r, "错误信息：第" + r + "行数据【中介公司为空！】");
                    saveMap.put("row", r);
                    excelImportMapper.saveError(saveMap);
                    continue;
                }
                int checkCompany = excelImportMapper.checkCompany(companyName);

                if (checkCompany == 0) {
                    saveMap.put("error", "错误信息：第" + r + "行数据【中介公司" + companyName + "不存在！】");
                    resultMap.put(r, "错误信息：第" + r + "行数据【中介公司" + companyName + "不存在！】");
                    saveMap.put("row", r);
                    excelImportMapper.saveError(saveMap);
                    continue;
                }
            }

            excelImportMapper.saveImportData(saveMap);

        }
        return resultMap;
    }

    @Override
    public List<Map> getAllImportData() {
        return excelImportMapper.getAllImportData();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map importBizTable() throws ParseException {
        // 获取临时表所有数据
        List<Map> list = excelImportMapper.getAllImportData();
        // 取第一条数据的项目名称，获取项目id
        String projectId = excelImportMapper.getProjectId(list.get(0).get("projectName") + "");
        // 获取自渠项目规则
        Map protectZqMap = excelImportMapper.getZqProjectProtect(projectId);
        // 获取案场项目规则
        Map protectAcMap = excelImportMapper.getAcProjectProtect(projectId);
        Map resultMap = new HashMap();
        //先导入已到访的
        for (Map map : list) {
            // 校验在线索表是否存在数据
            Map checkClue = excelImportMapper.checkProjectClue(map.get("customerMobile") + "", String.valueOf(map.get("projectName")));
            if (map.get("visitDate") != null && !map.get("visitDate").equals("")) {
                //通过手机号 项目  判断是否存在 如果存在  跳过
                if (checkClue != null) {
                    commonExcel(map, "2", checkClue.get("ProjectClueId").toString(), Integer.parseInt(checkClue.get("ClueStatus").toString()), projectId, protectZqMap, protectAcMap);
                } else {
                    commonExcel(map, "2", null, null, projectId, protectZqMap, protectAcMap);
                }
            } else {
                //未到访
                if (checkClue != null) {
                    if (!checkClue.get("ClueStatus").equals(1) && !checkClue.get("ClueStatus").equals(9)) {
                        commonExcel(map, "1", checkClue.get("ProjectClueId").toString(), Integer.parseInt(checkClue.get("ClueStatus").toString()), projectId, protectZqMap, protectAcMap);
                    } else {
                        //表示存在未到访客户  要判断是否报备逾期   如果未逾期  不允许导入  如果逾期 允许导入
                        if (checkClue.get("IsReportExpire").equals(0)) {
                            commonExcel(map, "1", checkClue.get("ProjectClueId").toString(), Integer.parseInt(checkClue.get("ClueStatus").toString()), projectId, protectZqMap, protectAcMap);
                        } else {
                            commonExcel(map, "1", null, null, projectId, protectZqMap, protectAcMap);
                        }
                    }
                } else {
                    commonExcel(map, "1", null, null, projectId, protectZqMap, protectAcMap);
                }
            }
        }

        return resultMap;
    }

    @Override
    public List<ExcelExportLog> getExcelExportDownList() {
        return excelImportMapper.getExcelExportDownList(SecurityUtils.getUserId());
    }

    @Override
    public PageInfo<ExcelExportLog> getExcelExportList(ExcelExportLog excelExportLog) {
        List<String> proIdList = new ArrayList<>();
        if (excelExportLog.getProjectIds() != null && !"".equals(excelExportLog.getProjectIds())) {

        } else {
            Map map = new HashMap();
            map.put("UserName", SecurityUtils.getUsername());
            Map userInfoMap = authMapper.mGetUserInfo(map);
            //判断是否系统管理员
            if("10001".equals(userInfoMap.get("JobCode").toString()) || "1999".equals(userInfoMap.get("JobCode").toString())){
                excelExportLog.setProjectIds("");
            }else{
                List<String> fullpath = projectMapper.findFullPath(map);
                StringBuffer sbs = new StringBuffer();
                for (int i = 0; i < fullpath.size(); i++) {
                    if (i == 0) {
                        sbs.append("org.FullPath LIKE '" + fullpath.get(i) + "%'");
                    } else {
                        sbs.append("or org.FullPath LIKE '" + fullpath.get(i) + "%'");
                    }
                }
                List<Map> mapList = projectMapper.findProjectListByUserName(SecurityUtils.getUsername() + "", "", userInfoMap.get("JobCode").toString(), sbs.toString());
                if (mapList != null && mapList.size() > 0) {
                    for (Map proMap : mapList) {
                        proIdList.add(proMap.get("projectId") + "");
                    }
                }
                excelExportLog.setProjectIds(StringUtils.join(proIdList.toArray(),","));
            }

        }
        PageHelper.startPage((int)excelExportLog.getCurrent(),(int)excelExportLog.getSize());
        return new PageInfo<>(excelImportMapper.getExcelExportLog(excelExportLog));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExcelExport(HttpServletRequest request, HttpServletResponse response, String param) {
        ExcelExportLog excelExportLog = JSONObject.parseObject(param,ExcelExportLog.class);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        excelExportLog.setEditor(excelExportLog.getCreator());
        excelExportLog.setIsDown("1");
        excelExportLog.setDownLoadTime(sf.format(new Date()));
        try{
            excelImportMapper.updateExcelExportLog(excelExportLog);
            String fileName = excelExportLog.getSubTypeDesc() + ".xlsx";
            InputStream fis = new BufferedInputStream(new FileInputStream("/app/netdata/excel/"+excelExportLog.getFileName()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName , "utf-8"));
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody reLoadExcel(ExcelExportLog excelExportLog) {
//        excelExportLog.setEditor(SecurityUtils.getUserId());
//        excelExportLog.setExportStatus("1");
//        excelImportMapper.updateExcelExportLog(excelExportLog);
//        //放入redis
//        redisUtil.lPush("downLoad",excelExportLog.getId());
        return ResultBody.success("重启任务失败！！");
    }

    @Override
    public int getExcelExportDownIsExist(ExcelExportLog excelExportLog) {
        excelExportLog.setCreator(SecurityUtils.getUserId());
        return excelImportMapper.getExcelExportDownIsExist(excelExportLog);
    }

    @Override
    public ResultBody getButchtMark(ReportCustomerForm reportCustomerForm) {
        int pageIndex = 1;
        int pageSize = 10;
        if (reportCustomerForm.getPageIndex()!=null){
            pageIndex = Integer.parseInt(reportCustomerForm.getPageIndex());
        }
        if (reportCustomerForm.getPageSize()!=null){
            pageSize = Integer.parseInt(reportCustomerForm.getPageSize());
        }
        PageHelper.startPage(pageIndex,pageSize);
        List<ReportCustomerForm> list = excelImportMapper.getProhectClueMarkTempList(reportCustomerForm);
        List reList = new ArrayList();
        reList.add(0,"<tr><th>序号</th><th>客户姓名</th><th>联系人方式</th><th>企业地址</th><th>经度</th><th>纬度</th><th>行业分类</th><th>二级分类</th><th>主营产品</th><th>意向项目</th>" +
                "<th>录入时间</th><th>录入人</th><th>录入人身份</th><th>是否园区</th><th>园区地址</th><th>园区层数</th><th>园区名称</th><th>标签</th></tr>");
        final int[] i = {1};
        list.stream().forEach(x->{
            String table = "<tr><td>";
            table = table.concat(i[0] +"").concat("</td><td>");//序号
            table = table.concat(x.getCustomerName()).concat("</td><td>");//客户姓名
            table = table.concat(x.getCustomerMobile()).concat("</td><td>");//联系人方式
            table = table.concat(x.getCustomerAddress()).concat("</td><td>");//企业地址
            table = table.concat(x.getLongitude()).concat("</td><td>");//经度
            table = table.concat(x.getLatitude()).concat("</td><td>");//纬度
            table = table.concat(x.getBelongIndustriseDesc()).concat("</td><td>");//行业分类
            table = table.concat(x.getBelongIndustriseTwoDesc()).concat("</td><td>");//二级分类
            table = table.concat(x.getMainProducts()).concat("</td><td>");//主营产品
            table = table.concat(x.getProjectName()).concat("</td><td>");//意向项目
            table = table.concat(x.getCreateDate()).concat("</td><td>");//录入时间
            table = table.concat(x.getEmployeeName()).concat("</td><td>");//录入人
            table = table.concat(x.getReportUserRole()).concat("</td><td>");//录入人身份
            table = table.concat(x.getIsPark()).concat("</td><td>");//是否园区
            table = table.concat(x.getParkAddress()).concat("</td><td>");//园区地址
            table = table.concat(x.getParkFloor()).concat("</td><td>");//园区层数
            table = table.concat(x.getParkName()).concat("</td><td>");//园区名称
            table = table.concat(x.getLabel()).concat("</td>");//标签
            reList.add(i[0],table.concat("</tr>"));
            i[0] = i[0] +1;
        });
        return ResultBody.success(new PageInfo(reList));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveButchtMark() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        //日志记录
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("批量导入标注任务开始");
        sysLog.setNote("人员记录-操作人："+userName+"-操作人id："+userId);
        excelImportMapper.insertLogs(sysLog);
        //保存临时表数据进入正式表
        excelImportMapper.saveMarkTempToProjectClue(userId);
        //清空临时表
        excelImportMapper.delProhectClueMarkTemp(userId);
        SysLog sysLog0 = new SysLog();
        sysLog0.setExecutTime(sf.format(new Date()));
        sysLog0.setTaskName("批量导入标注任务结束");
        sysLog0.setNote("人员记录-操作人："+userName+"-操作人账号："+userId);
        excelImportMapper.insertLogs(sysLog0);
        return ResultBody.success("导入成功");
    }

    @Override
    public List<DistVO> getExcelExportDist() {
        List<DistVO> list = new ArrayList<>();
        //获取导出类型大类
        List<DictionaryVO> main = excelImportMapper.getDictionaryList(null);
        if (main!=null && main.size()>0){
            for (DictionaryVO dict:main) {
                DistVO dist = new DistVO();
                dist.setLabel(dict.getDictName());
                dist.setValue(dict.getDictCode());
                //获取导出类型子类
                List<DictionaryVO> sub = excelImportMapper.getDictionaryList(dict.getId());
                if (sub!=null && sub.size()>0){
                    List<DistVO> children = new ArrayList<>();
                    for (DictionaryVO dict1:sub) {
                        DistVO dist1 = new DistVO();
                        dist1.setLabel(dict1.getDictName());
                        dist1.setValue(dict1.getDictCode());
                        children.add(dist1);
                    }
                    dist.setChildren(children);
                }
                list.add(dist);
            }
        }
        return list;
    }



    //封装代码
    public void commonExcel(Map map, String clueStatus, String projectClueId, Integer oldClueStatus, String projectId, Map protectZqMap, Map protectAcMap) throws ParseException {
        //通过项目名获取项目ID
        if (map.get("userName") == null) {
            map.put("salesAttributionId", null);
            map.put("salesAttributionName", null);
            map.put("salesAttributionGroupId", null);
            map.put("salesAttributionGroupName", null);
        } else {
            Map gwInfo = excelImportMapper.getUserInfo(map.get("userName") + "");
            map.put("salesAttributionId", gwInfo.get("ID"));
            map.put("salesAttributionName", gwInfo.get("EmployeeName"));
            map.put("salesAttributionGroupId", gwInfo.get("OrgId"));
            map.put("salesAttributionGroupName", gwInfo.get("OrgName"));
        }

        if (map.get("reportUserName") == null) {
            map.put("reportUserId", null);
            map.put("reportUserName", null);
            map.put("reportTeamId", null);
            map.put("reportTeamName", null);
        } else {
            Map reportInfo = excelImportMapper.getUserInfo(map.get("reportUserName") + "");
            map.put("reportUserId", reportInfo.get("ID"));
            map.put("reportUserName", reportInfo.get("EmployeeName"));
            map.put("reportTeamId", reportInfo.get("OrgId"));
            map.put("reportTeamName", reportInfo.get("OrgName"));
        }

        if (map.get("customerGender") != null) {
            if (map.get("customerGender").equals("男")) {
                map.put("customerGender", 1);
            } else if (map.get("customerGender").equals("女")) {
                map.put("customerGender", 2);
            } else {
                map.put("customerGender", 1);
            }
        } else {
            map.put("customerGender", 1);
        }

        if (clueStatus.equals("2")) {
            map.put("customerLevel", "2");
            map.put("tradeLevel", "2");
            map.put("level", "B");
        } else {
            map.put("customerLevel", "1");
            map.put("tradeLevel", "3");
            map.put("level", "C");
        }


        String customerUuid = UUID.randomUUID().toString();
        String projectClueUuid = UUID.randomUUID().toString();
        String projectOppUuid = UUID.randomUUID().toString();
        if (map.get("sourceTypeDesc").equals("中介成交")) {
            // 获取用户信息
            Map zjMap = excelImportMapper.getForUserInfo(map.get("reportUserName") + "");
            if (zjMap != null) {
                // 获取中介项目保护规则
                Map protectZjMap = excelImportMapper.getZjProjectProtect(projectId, zjMap.get("teamId") + "");
                //报备逾期
                if (protectZjMap.get("ReportExpireDays") == null || Integer.parseInt(protectZjMap.get("ReportExpireDays").toString()) == 0 || protectZjMap.get("ReportExpireDays") == "0" || protectZjMap.get("ReportExpireDays") == "") {
                    map.put("reportExpireDate", null);
                } else {
                    map.put("reportExpireDate", dataCastUtil.excelAddHour(Integer.parseInt(protectZjMap.get("ReportExpireDays").toString()), map.get("reportCreateTime").toString()));
                }
                //报备预警
                if (protectZjMap.get("ReportDaysWarning") == null || Integer.parseInt(protectZjMap.get("ReportDaysWarning").toString()) == 0 || protectZjMap.get("ReportDaysWarning") == "0" || protectZjMap.get("ReportDaysWarning") == "") {
                    map.put("reportDaysWarning", null);
                } else {
                    map.put("reportDaysWarning", dataCastUtil.excelAddHour(Integer.parseInt(protectZjMap.get("ReportExpireDays").toString()) - Integer.parseInt(protectZjMap.get("ReportDaysWarning").toString()), map.get("reportCreateTime").toString()));
                }
                //渠道有效期
                if (protectZjMap.get("ChannelProtectionPeriod") == null || Integer.parseInt(protectZjMap.get("ChannelProtectionPeriod").toString()) == 0 || protectZjMap.get("ChannelProtectionPeriod") == "0" || protectZjMap.get("ChannelProtectionPeriod") == "") {
                    map.put("tokerVisitExpireDate", null);
                } else {
                    map.put("tokerVisitExpireDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZjMap.get("ChannelProtectionPeriod").toString()), map.get("reportCreateTime").toString()));
                }
                //渠道有效期预警
                if (protectZjMap.get("ChannelProtectionPeriodWarning") == null || Integer.parseInt(protectZjMap.get("ChannelProtectionPeriodWarning").toString()) == 0 || protectZjMap.get("ChannelProtectionPeriodWarning") == "0" || protectZjMap.get("ChannelProtectionPeriodWarning") == "") {
                    map.put("tokerVisitExpireWarningDate", null);
                } else {
                    map.put("tokerVisitExpireWarningDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZjMap.get("ChannelProtectionPeriod").toString()) - Integer.parseInt(protectZjMap.get("ChannelProtectionPeriodWarning").toString()), map.get("reportCreateTime").toString()));
                }
                //案场到访逾期
                if (!clueStatus.equals(1) && !clueStatus.equals(9)) {
                    if (protectZjMap.get("VisitExpireDays") == null || Integer.parseInt(protectZjMap.get("VisitExpireDays").toString()) == 0 || protectZjMap.get("VisitExpireDays") == "0" || protectZjMap.get("VisitExpireDays") == "") {
                        map.put("salesVisitExpireDate", null);
                    } else {
                        map.put("salesVisitExpireDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZjMap.get("VisitExpireDays").toString()), map.get("visitDate").toString()));
                    }
                    //案场到访逾期预警
                    if (protectZjMap.get("VisitingWarning") == null || Integer.parseInt(protectZjMap.get("VisitingWarning").toString()) == 0 || protectZjMap.get("VisitingWarning") == "0" || protectZjMap.get("VisitingWarning") == "") {
                        map.put("salesVisitExpireWarningDate", null);
                    } else {
                        map.put("salesVisitExpireWarningDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZjMap.get("VisitExpireDays").toString()) - Integer.parseInt(protectZjMap.get("VisitingWarning").toString()), map.get("visitDate").toString()));
                    }
                } else {
                    map.put("salesVisitExpireDate", null);
                    map.put("salesVisitExpireWarningDate", null);
                }
                map.put("reportTeamId", zjMap.get("teamId"));
                map.put("reportTeamName", zjMap.get("teamName"));
                map.put("performanceAttributor", map.get("companyName"));
                map.put("performanceAttributorID", zjMap.get("companyID"));
                map.put("performanceAttributorOld", map.get("companyName"));
                map.put("performanceAttributorOldID", zjMap.get("companyID"));
            } else {
                map.put("reportExpireDate", null);
                map.put("reportDaysWarning", null);
                map.put("tokerVisitExpireDate", null);
                map.put("tokerVisitExpireWarningDate", null);
                map.put("salesVisitExpireDate", null);
                map.put("salesVisitExpireWarningDate", null);
                map.put("reportTeamId", null);
                map.put("reportTeamName", null);
                map.put("performanceAttributor", null);
            }

            map.put("sourceType", 1);
        } else if (map.get("sourceTypeDesc").equals("自渠成交")) {

            if (protectZqMap != null) {
                //报备逾期
                if (protectZqMap.get("ReportExpireDays") == null || Integer.parseInt(protectZqMap.get("ReportExpireDays").toString()) == 0 || protectZqMap.get("ReportExpireDays") == "0" || protectZqMap.get("ReportExpireDays") == "") {
                    map.put("reportExpireDate", null);
                } else {
                    map.put("reportExpireDate", dataCastUtil.excelAddHour(Integer.parseInt(protectZqMap.get("ReportExpireDays").toString()), map.get("reportCreateTime").toString()));
                }
                //报备预警
                if (protectZqMap.get("ReportDaysWarning") == null || Integer.parseInt(protectZqMap.get("ReportDaysWarning").toString()) == 0 || protectZqMap.get("ReportDaysWarning") == "0" || protectZqMap.get("ReportDaysWarning") == "") {
                    map.put("reportDaysWarning", null);
                } else {
                    map.put("reportDaysWarning", dataCastUtil.excelAddHour(Integer.parseInt(protectZqMap.get("ReportExpireDays").toString()) - Integer.parseInt(protectZqMap.get("ReportDaysWarning").toString()), map.get("reportCreateTime").toString()));
                }
                //渠道有效期
                if (protectZqMap.get("ChannelProtectionPeriod") == null || Integer.parseInt(protectZqMap.get("ChannelProtectionPeriod").toString()) == 0 || protectZqMap.get("ChannelProtectionPeriod") == "0" || protectZqMap.get("ChannelProtectionPeriod") == "") {
                    map.put("tokerVisitExpireDate", null);
                } else {
                    map.put("tokerVisitExpireDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZqMap.get("ChannelProtectionPeriod").toString()), map.get("reportCreateTime").toString()));
                }
                //渠道有效期预警
                if (protectZqMap.get("ChannelProtectionPeriodWarning") == null || Integer.parseInt(protectZqMap.get("ChannelProtectionPeriodWarning").toString()) == 0 || protectZqMap.get("ChannelProtectionPeriodWarning") == "0" || protectZqMap.get("ChannelProtectionPeriodWarning") == "") {
                    map.put("tokerVisitExpireWarningDate", null);
                } else {
                    map.put("tokerVisitExpireWarningDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZqMap.get("ChannelProtectionPeriod").toString()) - Integer.parseInt(protectZqMap.get("ChannelProtectionPeriodWarning").toString()), map.get("reportCreateTime").toString()));
                }
                //案场到访逾期
                if (!clueStatus.equals("1") && !clueStatus.equals("9")) {
                    if (protectZqMap.get("VisitExpireDays") == null || Integer.parseInt(protectZqMap.get("VisitExpireDays").toString()) == 0 || protectZqMap.get("VisitExpireDays") == "0" || protectZqMap.get("VisitExpireDays") == "") {
                        map.put("salesVisitExpireDate", null);
                    } else {
                        map.put("salesVisitExpireDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZqMap.get("VisitExpireDays").toString()), map.get("visitDate").toString()));
                    }
                    //案场到访逾期预警
                    if (protectZqMap.get("VisitingWarning") == null || Integer.parseInt(protectZqMap.get("VisitingWarning").toString()) == 0 || protectZqMap.get("VisitingWarning") == "0" || protectZqMap.get("VisitingWarning") == "") {
                        map.put("salesVisitExpireWarningDate", null);
                    } else {
                        map.put("salesVisitExpireWarningDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZqMap.get("VisitExpireDays").toString()) - Integer.parseInt(protectZqMap.get("VisitingWarning").toString()), map.get("visitDate").toString()));
                    }
                } else {
                    map.put("salesVisitExpireDate", null);
                    map.put("salesVisitExpireWarningDate", null);
                }
                map.put("performanceAttributor", map.get("reportUserName"));
                map.put("performanceAttributorID", map.get("reportUserId"));
                map.put("performanceAttributorOld", map.get("reportUserName"));
                map.put("performanceAttributorOldID", map.get("reportUserId"));
            } else {
                map.put("reportExpireDate", null);
                map.put("reportDaysWarning", null);
                map.put("tokerVisitExpireDate", null);
                map.put("tokerVisitExpireWarningDate", null);
                map.put("salesVisitExpireDate", null);
                map.put("salesVisitExpireWarningDate", null);
            }
            map.put("sourceType", 2);
            map.put("performanceAttributor", map.get("reportUserName"));
        } else if (map.get("sourceTypeDesc").equals("案场成交")) {
            if (protectAcMap != null) {
                //报备逾期
                if (protectAcMap.get("ReportExpireDays") == null || Integer.parseInt(protectAcMap.get("ReportExpireDays").toString()) == 0 || protectAcMap.get("ReportExpireDays") == "0" || protectAcMap.get("ReportExpireDays") == "") {
                    map.put("reportExpireDate", null);
                } else {
                    map.put("reportExpireDate", dataCastUtil.excelAddHour(Integer.parseInt(protectAcMap.get("ReportExpireDays").toString()), map.get("reportCreateTime").toString()));
                }
                //报备预警
                if (protectAcMap.get("ReportDaysWarning") == null || Integer.parseInt(protectAcMap.get("ReportDaysWarning").toString()) == 0 || protectAcMap.get("ReportDaysWarning") == "0" || protectAcMap.get("ReportDaysWarning") == "") {
                    map.put("reportDaysWarning", null);
                } else {
                    map.put("reportDaysWarning", dataCastUtil.excelAddHour(Integer.parseInt(protectAcMap.get("ReportExpireDays").toString()) - Integer.parseInt(protectAcMap.get("ReportDaysWarning").toString()), map.get("reportCreateTime").toString()));
                }
                //渠道有效期
                if (protectAcMap.get("ChannelProtectionPeriod") == null || Integer.parseInt(protectAcMap.get("ChannelProtectionPeriod").toString()) == 0 || protectAcMap.get("ChannelProtectionPeriod") == "0" || protectAcMap.get("ChannelProtectionPeriod") == "") {
                    map.put("tokerVisitExpireDate", null);
                } else {
                    map.put("tokerVisitExpireDate", dataCastUtil.excelAddDay(Integer.parseInt(protectAcMap.get("ChannelProtectionPeriod").toString()), map.get("reportCreateTime").toString()));
                }
                //渠道有效期预警
                if (protectAcMap.get("ChannelProtectionPeriodWarning") == null || Integer.parseInt(protectAcMap.get("ChannelProtectionPeriodWarning").toString()) == 0 || protectAcMap.get("ChannelProtectionPeriodWarning") == "0" || protectAcMap.get("ChannelProtectionPeriodWarning") == "") {
                    map.put("tokerVisitExpireWarningDate", null);
                } else {
                    map.put("tokerVisitExpireWarningDate", dataCastUtil.excelAddDay(Integer.parseInt(protectAcMap.get("ChannelProtectionPeriod").toString()) - Integer.parseInt(protectAcMap.get("ChannelProtectionPeriodWarning").toString()), map.get("reportCreateTime").toString()));
                }
                //案场到访逾期
                if (!clueStatus.equals("1") && !clueStatus.equals("9")) {
                    if (protectAcMap.get("VisitExpireDays") == null || Integer.parseInt(protectAcMap.get("VisitExpireDays").toString()) == 0 || protectAcMap.get("VisitExpireDays") == "0" || protectAcMap.get("VisitExpireDays") == "") {
                        map.put("salesVisitExpireDate", null);
                    } else {
                        map.put("salesVisitExpireDate", dataCastUtil.excelAddDay(Integer.parseInt(protectAcMap.get("VisitExpireDays").toString()), map.get("visitDate").toString()));
                    }
                    //案场到访逾期预警
                    if (protectAcMap.get("VisitingWarning") == null || Integer.parseInt(protectAcMap.get("VisitingWarning").toString()) == 0 || protectAcMap.get("VisitingWarning") == "0" || protectAcMap.get("VisitingWarning") == "") {
                        map.put("salesVisitExpireWarningDate", null);
                    } else {
                        map.put("salesVisitExpireWarningDate", dataCastUtil.excelAddDay(Integer.parseInt(protectAcMap.get("VisitExpireDays").toString()) - Integer.parseInt(protectAcMap.get("VisitingWarning").toString()), map.get("visitDate").toString()));
                    }
                } else {
                    map.put("salesVisitExpireDate", null);
                    map.put("salesVisitExpireWarningDate", null);
                }
            } else {
                map.put("reportExpireDate", null);
                map.put("reportDaysWarning", null);
                map.put("tokerVisitExpireDate", null);
                map.put("tokerVisitExpireWarningDate", null);
                map.put("salesVisitExpireDate", null);
                map.put("salesVisitExpireWarningDate", null);
            }

            map.put("sourceType", 3);
        } /*else if (map.get("sourceTypeDesc").equals("私营媒介")) {
            Map protectZqMap = excelImportMapper.getZqProjectProtect(projectId);
            if (protectZqMap != null) {
                //报备逾期
                if (protectZqMap.get("ReportExpireDays") == null) {
                    map.put("reportExpireDate", null);
                } else {
                    map.put("reportExpireDate", dataCastUtil.excelAddHour(Integer.parseInt(protectZqMap.get("ReportExpireDays").toString()),map.get("reportCreateTime").toString()));
                }
                //报备预警
                if (protectZqMap.get("ReportDaysWarning") == null) {
                    map.put("reportDaysWarning", null);
                } else {
                    map.put("reportDaysWarning", dataCastUtil.excelAddHour(Integer.parseInt(protectZqMap.get("ReportExpireDays").toString())-Integer.parseInt(protectZqMap.get("ReportDaysWarning").toString()),map.get("reportCreateTime").toString()));
                }
                //渠道有效期
                if (protectZqMap.get("ChannelProtectionPeriod") == null) {
                    map.put("tokerVisitExpireDate", null);
                } else {
                    map.put("tokerVisitExpireDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZqMap.get("ChannelProtectionPeriod").toString()),map.get("reportCreateTime").toString()));
                }
                //渠道有效期预警
                if (protectZqMap.get("ChannelProtectionPeriodWarning") == null) {
                    map.put("tokerVisitExpireWarningDate", null);
                } else {
                    map.put("tokerVisitExpireWarningDate", dataCastUtil.excelAddDay(Integer.parseInt(protectZqMap.get("ChannelProtectionPeriod").toString())-Integer.parseInt(protectZqMap.get("ChannelProtectionPeriodWarning").toString()),map.get("reportCreateTime").toString()));
                }
                //案场到访逾期
                if(!clueStatus.equals(1)&&!clueStatus.equals(9)){
                    if(protectZqMap.get("VisitExpireDays")==null){
                        map.put("salesVisitExpireDate",null);
                    }else{
                        map.put("salesVisitExpireDate",dataCastUtil.excelAddDay(Integer.parseInt(protectZqMap.get("VisitExpireDays").toString()),map.get("visitDate").toString()));
                    }
                    //案场到访逾期预警
                    if(protectZqMap.get("VisitingWarning")==null){
                        map.put("salesVisitExpireWarningDate",null);
                    }else{
                        map.put("salesVisitExpireWarningDate",dataCastUtil.excelAddDay(Integer.parseInt(protectZqMap.get("VisitExpireDays").toString())-Integer.parseInt(protectZqMap.get("VisitingWarning").toString()),map.get("visitDate").toString()));
                    }
                }else{
                    map.put("salesVisitExpireDate", null);
                    map.put("salesVisitExpireWarningDate", null);
                }
            } else {
                map.put("reportExpireDate", null);
                map.put("reportDaysWarning", null);
                map.put("tokerVisitExpireDate", null);
                map.put("tokerVisitExpireWarningDate", null);
                map.put("salesVisitExpireDate", null);
                map.put("salesVisitExpireWarningDate", null);
            }
            map.put("sourceType", 4);
            //map.put("performanceAttributor", reportInfo.get("ID"));
        }*/
        map.put("projectId", projectId);
        map.put("customerUuid", customerUuid);
        map.put("projectClueUuid", projectClueUuid);
        map.put("projectOppUuid", projectOppUuid);
        map.put("clueStatus", clueStatus);
        if (projectClueId == null && oldClueStatus == null) {
            //生成线索表   客户表  机会表   首访信息表
            excelImportMapper.insertCustomerBasic(map);
            excelImportMapper.insertClue(map);
            if (clueStatus.equals("2")) {
                excelImportMapper.insertOpp(map);
                excelImportMapper.saveInformation(map);
            }
        } else {
            //通过线索ID更新数据
            map.put("projectClueId", projectClueId);
            excelImportMapper.overiedClues(map);
            excelImportMapper.overiedOpp(map);
        }

        excelImportMapper.delTempData(map.get("id").toString());
    }
    
    @Override
    public ResultBody queryMapImportCustomerHistory(Map<String, Object> paramMap) {
        try {
            // 查询地图导入客户历史记录
            List<Map> historyList = excelImportMapper.queryMapImportCustomerHistory(paramMap);
            
            // 处理每条记录，解析Note字段中的用户ID
            List<Map> processedList = new ArrayList<>();
            for (Map history : historyList) {
                Map processedHistory = new HashMap<>(history);
                
                // 解析Note字段中的用户ID
                String note = (String) history.get("Note");
                if (note != null && !note.isEmpty()) {
                    String userId = extractUserIdFromNote(note);
                    processedHistory.put("userId", userId);
                    processedHistory.put("userName", extractUserNameFromNote(note));
                }
                
                processedList.add(processedHistory);
            }

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("mapImportCustomerHistoryList", processedList);
            resultMap.put("pageNum", paramMap.get("pageNum"));
            resultMap.put("pageSize", paramMap.get("pageSize"));
            resultMap.put("total", processedList != null ? processedList.size() : 0);
            return ResultBody.success(resultMap);
        } catch (Exception e) {
            //logger.error("查询地图导入客户历史记录失败", e);
            return ResultBody.error(-10001,"查询地图导入客户历史记录失败：" + e.getMessage());
        }
    }
    
    @Override
    public ResultBody deleteMapImportData(Map<String, Object> paramMap) {
        try {
            String historyId = (String) paramMap.get("historyId");
            if (historyId == null || historyId.isEmpty()) {
                return ResultBody.error(-10001,"历史记录ID不能为空");
            }
            
            // 根据历史记录ID查询Note字段
            Map<String, Object> queryParam = new HashMap<>();
            queryParam.put("historyId", historyId);
            List<Map> historyList = excelImportMapper.queryMapImportCustomerHistory(queryParam);
            
            if (historyList == null || historyList.isEmpty()) {
                return ResultBody.error(-10002,"未找到对应的历史记录");
            }
            
            Map history = historyList.get(0);
            String note = (String) history.get("Note");
            if (note == null || note.isEmpty()) {
                return ResultBody.error(-10003,"历史记录Note字段为空，无法解析用户ID");
            }
            
            // 解析用户ID
            String userId = extractUserIdFromNote(note);
            if (userId == null || userId.isEmpty()) {
                return ResultBody.error(-10004,"无法从Note字段解析出用户ID");
            }
            
            // 校验删除条件
            //ResultBody validationResult = validateDeleteConditions(userId);
            ResultBody validationResult = validateDeleteConditions(historyId);
            if (validationResult.getCode() != 200) {
                return validationResult;
            }
            
            // 执行删除操作-改为倒入时生成的主键。
            int followupDeleted = excelImportMapper.deleteFollowupRecordByUserId(historyId);
            int cluesDeleted = excelImportMapper.deleteProjectCluesByUserId(historyId);
            
            // 删除成功后，更新定时任务日志状态为1
            int statusUpdated = excelImportMapper.updateTimeTaskLogStatus(historyId, 1);
            
            Map<String, Object> result = new HashMap<>();
            result.put("followupDeleted", followupDeleted);
            result.put("cluesDeleted", cluesDeleted);
            result.put("userId", userId);
            result.put("statusUpdated", statusUpdated);
            
            return ResultBody.success(result);
        } catch (Exception e) {
            //logger.error("删除地图导入数据失败", e);
            return ResultBody.error(-10005,"删除地图导入数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 从Note字段中提取用户ID
     */
    private String extractUserIdFromNote(String note) {
        if (note == null || note.isEmpty()) {
            return null;
        }
        
        // 查找"操作人账号："后面的内容
        int index = note.indexOf("操作人账号：");
        if (index != -1) {
            String afterAccount = note.substring(index + "操作人账号：".length());
            // 提取账号（可能是负数，以-开头）
            if (afterAccount.startsWith("-")) {
                // 处理负数账号
                int endIndex = afterAccount.indexOf("-", 1);
                if (endIndex != -1) {
                    return afterAccount.substring(0, endIndex);
                } else {
                    return afterAccount;
                }
            } else {
                // 处理正数账号
                int endIndex = afterAccount.indexOf("-");
                if (endIndex != -1) {
                    return afterAccount.substring(0, endIndex);
                } else {
                    return afterAccount;
                }
            }
        }
        
        return null;
    }
    
    /**
     * 从Note字段中提取用户名
     */
    private String extractUserNameFromNote(String note) {
        if (note == null || note.isEmpty()) {
            return null;
        }
        // 查找"操作人："后面的内容
        int index = note.indexOf("操作人：");
        if (index != -1) {
            String afterOperator = note.substring(index + "操作人：".length());
            int endIndex = afterOperator.indexOf("-");
            if (endIndex != -1) {
                return afterOperator.substring(0, endIndex);
            } else {
                return afterOperator;
            }
        }
        
        return null;
    }
    
    /**
     * 校验删除条件
     */
    private ResultBody validateDeleteConditions(String userId) {
        try {
            // 检查项目线索状态
            List<Map> statusCheck = excelImportMapper.checkProjectCluesStatus(userId);
            if (statusCheck != null && !statusCheck.isEmpty()) {
                return ResultBody.error(-10001,"存在状态不为0的项目线索，无法删除");
            }
            
            // 检查任务客户关联
            List<Map> relationCheck = excelImportMapper.checkTaskCustomerRelation(userId);
            if (relationCheck != null && !relationCheck.isEmpty()) {
                return ResultBody.error(-10002,"存在任务客户关联，无法删除");
            }
            
            return ResultBody.success("可以删除");
        } catch (Exception e) {
            //logger.error("校验删除条件失败", e);
            return ResultBody.error(-10009,"校验删除条件失败：" + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> queryCluesContacts(String projectClueId) {
        return excelImportMapper.queryCluesContacts(projectClueId);
    }

}
