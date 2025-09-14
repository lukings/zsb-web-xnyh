package cn.visolink.system.excel.controller;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.model.SysLog;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.channel.model.ProjectRuleDetail;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.excel.service.ExcelImportService;
import cn.visolink.system.excel.util.AMapUtils;
import cn.visolink.system.job.authorization.mapper.JobMapper;
import cn.visolink.system.timetask.SyncDataTask;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.system.excel.service.ExcelImportService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.26
 */
@RestController
@Api(tags = "导出日志")
@RequestMapping("/excel")
public class ExcelImportController {


    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private ExcelImportMapper excelImportMapper;

    @Autowired
    private ProjectCluesDao projectCluesDao;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SyncDataTask syncDataTask;
    @ResponseBody
    @RequestMapping(value = "/import",method = RequestMethod.POST)
    public ResultBody excelImport(MultipartFile file) {

        String fileName = file.getOriginalFilename();
        try {
            return ResultBody.success(excelImportService.batchImport(fileName,file));
           // a = studentService.batchImport(fileName, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    //导入组织
    @ResponseBody
    @RequestMapping(value = "/importOrg",method = RequestMethod.POST)
    public ResultBody excelImportOrg(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<Map> paramsList =new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }

            String orgId = row.getCell(0).getStringCellValue();
            String pid = row.getCell(1).getStringCellValue();
            String orgName = row.getCell(2).getStringCellValue();
            String OrgCategory = row.getCell(3).getStringCellValue();
            String fullPath = row.getCell(4).getStringCellValue();
            Map paramMap = new HashMap();
            paramMap.put("orgId",orgId);
            paramMap.put("pid",pid);
            paramMap.put("orgName",orgName);
            paramMap.put("OrgCategory",OrgCategory);
            paramMap.put("fullPath",fullPath);
            paramsList.add(paramMap);
            System.out.println(orgName);
        }
        excelImportMapper.saveOrgData(paramsList);
        // 组织全路径数据清洗
        syncDataTask.organizationFullPathDataCleaning();
        return  ResultBody.success("导入成功！");
    }
    //导入岗位
    @ResponseBody
    @RequestMapping(value = "/importJob",method = RequestMethod.POST)
    public ResultBody excelImportJob(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<Map> paramsList =new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }

            String jobId = row.getCell(0).getStringCellValue();
            String jobName = row.getCell(1).getStringCellValue();
            String comjobId = row.getCell(2).getStringCellValue();
            String orgId = row.getCell(3).getStringCellValue();
            Map paramMap = new HashMap();
            paramMap.put("jobId",jobId);
            paramMap.put("jobName",jobName);
            paramMap.put("comjobId",comjobId);
            paramMap.put("orgId",orgId);
            paramsList.add(paramMap);
        }
        excelImportMapper.saveJobData(paramsList);
        return  ResultBody.success("导入成功！");
    }

    //导入项目
    @ResponseBody
    @RequestMapping(value = "/importProject",method = RequestMethod.POST)
    public ResultBody excelImportProject(MultipartFile file, HttpServletRequest request) throws IOException {
//        String companycode= request.getHeader("companycode");
//        Object obj = redisUtil.get(companycode+"zhyx");
//        JSONObject jsonObject = JSONObject.parseObject(obj.toString());
        //Integer projectNum = (Integer) jsonObject.get("projectNum");
//        Integer projectNum =5;
        String fileName = file.getOriginalFilename();
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
        System.out.println(sheet.getLastRowNum());
        List<Map> paramsList =new ArrayList<>();
//        if(sheet.getLastRowNum()-2>projectNum){
//            return ResultBody.error(400,"导入项目超过上限！");
//        }
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }

            String projectId = row.getCell(0).getStringCellValue();
            String pid =null;
            if(row.getCell(1)!=null){
                pid = row.getCell(1).getStringCellValue();
            }
            String projectName = row.getCell(2).getStringCellValue();
            Map paramMap = new HashMap();
            paramMap.put("projectId",projectId);
            paramMap.put("pid",pid);
            paramMap.put("projectName",projectName);
            paramsList.add(paramMap);
        }
        excelImportMapper.saveProjectData(paramsList);
        return  ResultBody.success("导入成功！");
    }

    //导入人员
    @ResponseBody
    @RequestMapping(value = "/importUser",method = RequestMethod.POST)
    public ResultBody importUser(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<Map> paramsList =new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }

            String alias = row.getCell(0).getStringCellValue();
            //判断是否已存在此账号
            int count = excelImportMapper.getAlias(alias);
            if (count>0){
                continue;
            }
            String mail = "";
            if (row.getCell(1)!=null){
                mail = row.getCell(1).getStringCellValue();
            }
            String usercn = row.getCell(2).getStringCellValue();
            String userstatus = row.getCell(3).getStringCellValue();
            String mobile = "";
            if (row.getCell(4)!=null){
                mobile = row.getCell(4).getStringCellValue();
            }
            String genderId = "1";
            if (row.getCell(5)!=null && !"".equals(row.getCell(5).getStringCellValue())){
                genderId = row.getCell(5).getStringCellValue();
            }
            Map paramMap = new HashMap();
            paramMap.put("alias",alias);
            paramMap.put("mail",mail);
            paramMap.put("usercn",usercn);
            paramMap.put("userstatus",userstatus);
            paramMap.put("mobile",mobile);
            paramMap.put("genderId",genderId);
            paramsList.add(paramMap);
        }
        if (paramsList.size()>0){
            excelImportMapper.saveUserData(paramsList);
        }
        return  ResultBody.success("导入成功！");
    }

    //导入楼栋
    @ResponseBody
    @RequestMapping(value = "/importBuild",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody importBuild(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<Map> paramsList =new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }

            String projectId = row.getCell(0).getStringCellValue();
            String projectName = null;
            if (row.getCell(1)!=null){
                projectName = row.getCell(1).getStringCellValue();
            }
            String stageId = null;
            if (row.getCell(2)!=null){
                stageId = row.getCell(2).getStringCellValue();
            }
            String stageName = null;
            if (row.getCell(3)!=null){
                stageName = row.getCell(3).getStringCellValue();
            }
            String buildId = null;
            if (row.getCell(4)!=null){
                buildId = row.getCell(4).getStringCellValue();
            }
            String productBuildName = null;
            if (row.getCell(5)!=null){
                productBuildName = row.getCell(5).getStringCellValue();
            }
            String productCode = null;
            if (row.getCell(6)!=null){
                productCode = row.getCell(6).getStringCellValue();
            }
            String productName = null;
            if (row.getCell(7)!=null){
                productName = row.getCell(7).getStringCellValue();
            }
            String floors = null;
            if (row.getCell(8)!=null){
                floors = row.getCell(8).getStringCellValue();
            }
            String unitNum = null;
            if (row.getCell(9)!=null){
                unitNum = row.getCell(9).getStringCellValue();
            }
            String roomNum = null;
            if (row.getCell(10)!=null){
                roomNum = row.getCell(10).getStringCellValue();
            }
            String startTime = null;
            if (row.getCell(11)!=null && !"".equals(row.getCell(11).getStringCellValue())){
                startTime = row.getCell(11).getStringCellValue();
            }
            String endTime = null;
            if (row.getCell(12)!=null && !"".equals(row.getCell(12).getStringCellValue())){
                endTime = row.getCell(12).getStringCellValue();
            }
            String orderCode = null;
            if (row.getCell(13)!=null){
                orderCode = row.getCell(13).getStringCellValue();
            }
            String bldType = null;
            if (row.getCell(14)!=null){
                bldType = row.getCell(14).getStringCellValue();
            }
            String permitNum = null;
            if (row.getCell(15)!=null && !"".equals(row.getCell(15).getStringCellValue())){
                permitNum = row.getCell(15).getStringCellValue();
            }

            Map paramMap = new HashMap();
            paramMap.put("build_id",buildId);
            paramMap.put("product_build_name",productBuildName);
            paramMap.put("product_code",productCode);
            paramMap.put("product_name",productName);
            paramMap.put("floors",floors);
            paramMap.put("unit_num",unitNum);
            paramMap.put("room_num",roomNum);
            paramMap.put("project_id",projectId);
            paramMap.put("project_name",projectName);
            paramMap.put("stage_id",stageId);
            paramMap.put("stage_name",stageName);
            paramMap.put("order_code",orderCode);
            paramMap.put("bld_type",bldType);
            paramMap.put("permit_num",permitNum);
            paramMap.put("start_time",startTime);
            paramMap.put("end_time",endTime);
            paramsList.add(paramMap);
        }
        if (paramsList.size()>0){
            excelImportMapper.saveBuildData(paramsList);
        }
        return  ResultBody.success("导入成功！");
    }

    //导入单元
    @ResponseBody
    @RequestMapping(value = "/importUnit",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody importUnit(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<Map> paramsList =new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }

            String unitId = row.getCell(0).getStringCellValue();

            String unitName = null;
            if (row.getCell(1)!=null){
                unitName = row.getCell(1).getStringCellValue();
            }
            String unitNo = row.getCell(2).getStringCellValue();
            String buildId = row.getCell(3).getStringCellValue();
            String stageId = null;
            if (row.getCell(4)!=null){
                stageId = row.getCell(4).getStringCellValue();
            }
            String stageName = null;
            if (row.getCell(5)!=null && !"".equals(row.getCell(5).getStringCellValue())){
                stageName = row.getCell(5).getStringCellValue();
            }
            String projectId = null;
            if (row.getCell(6)!=null && !"".equals(row.getCell(6).getStringCellValue())){
                projectId = row.getCell(6).getStringCellValue();
            }
            String projectName = null;
            if (row.getCell(7)!=null && !"".equals(row.getCell(7).getStringCellValue())){
                projectName = row.getCell(7).getStringCellValue();
            }
            Map paramMap = new HashMap();
            paramMap.put("unit_id",unitId);
            paramMap.put("unit_name",unitName);
            paramMap.put("unit_no",unitNo);
            paramMap.put("build_id",buildId);
            paramMap.put("project_id",projectId);
            paramMap.put("project_name",projectName);
            paramMap.put("stage_id",stageId);
            paramMap.put("stage_name",stageName);
            paramsList.add(paramMap);
        }
        if (paramsList.size()>0){
            excelImportMapper.saveUnitData(paramsList);
        }
        return  ResultBody.success("导入成功！");
    }

    //导入房间
    @ResponseBody
    @RequestMapping(value = "/importRoom",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody importRoom(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<Map> paramsList =new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }

            String projectId = row.getCell(0).getStringCellValue();
            String projectName = null;
            if (row.getCell(1)!=null){
                projectName = row.getCell(1).getStringCellValue();
            }
            String stageId = null;
            if (row.getCell(2)!=null && !"".equals(row.getCell(2).getStringCellValue())){
                stageId = row.getCell(2).getStringCellValue();
            }
            String stageName = null;
            if (row.getCell(3)!=null && !"".equals(row.getCell(3).getStringCellValue())){
                stageName = row.getCell(3).getStringCellValue();
            }
            String buildId = row.getCell(4).getStringCellValue();
            String productBuildName = row.getCell(5).getStringCellValue();
            String unitId = null;
            if (row.getCell(6)!=null){
                unitId = row.getCell(6).getStringCellValue();
            }
            String unitName = null;
            if (row.getCell(7)!=null && !"".equals(row.getCell(7).getStringCellValue())){
                unitName = row.getCell(7).getStringCellValue();
            }
            String unitNo = null;
            if (row.getCell(8)!=null && !"".equals(row.getCell(8).getStringCellValue())){
                unitNo = row.getCell(8).getStringCellValue();
            }
            String roomId = null;
            if (row.getCell(9)!=null && !"".equals(row.getCell(9).getStringCellValue())){
                roomId = row.getCell(9).getStringCellValue();
            }
            String roomInfo = null;
            if (row.getCell(10)!=null && !"".equals(row.getCell(10).getStringCellValue())){
                roomInfo = row.getCell(10).getStringCellValue();
            }
            String roomName = null;
            if (row.getCell(11)!=null && !"".equals(row.getCell(11).getStringCellValue())){
                roomName = row.getCell(11).getStringCellValue();
            }
            String roomNo = null;
            if (row.getCell(12)!=null && !"".equals(row.getCell(12).getStringCellValue())){
                roomNo = row.getCell(12).getStringCellValue();
            }
            String no = null;
            if (row.getCell(13)!=null && !"".equals(row.getCell(13).getStringCellValue())){
                no = row.getCell(13).getStringCellValue();
            }
            String floorNo = null;
            if (row.getCell(14)!=null && !"".equals(row.getCell(14).getStringCellValue())){
                floorNo = row.getCell(14).getStringCellValue();
            }
            String floorName = null;
            if (row.getCell(15)!=null && !"".equals(row.getCell(15).getStringCellValue())){
                floorName = row.getCell(15).getStringCellValue();
            }
            String statusEnum = null;
            if (row.getCell(16)!=null && !"".equals(row.getCell(16).getStringCellValue())){
                statusEnum = row.getCell(16).getStringCellValue();
            }
            String djBldPrice = null;
            if (row.getCell(17)!=null && !"".equals(row.getCell(17).getStringCellValue())){
                djBldPrice = row.getCell(17).getStringCellValue();
            }
            String djTnPrice = null;
            if (row.getCell(18)!=null && !"".equals(row.getCell(18).getStringCellValue())){
                djTnPrice = row.getCell(18).getStringCellValue();
            }
            String djTotal = null;
            if (row.getCell(19)!=null && !"".equals(row.getCell(19).getStringCellValue())){
                djTotal = row.getCell(19).getStringCellValue();
            }
            String bldPrice = null;
            if (row.getCell(20)!=null && !"".equals(row.getCell(20).getStringCellValue())){
                bldPrice = row.getCell(20).getStringCellValue();
            }
            String bldArea = null;
            if (row.getCell(21)!=null && !"".equals(row.getCell(21).getStringCellValue())){
                bldArea = row.getCell(21).getStringCellValue();
            }
            String hxId = null;
            if (row.getCell(22)!=null && !"".equals(row.getCell(22).getStringCellValue())){
                hxId = row.getCell(22).getStringCellValue();
            }
            String hxName = null;
            if (row.getCell(23)!=null && !"".equals(row.getCell(23).getStringCellValue())){
                hxName = row.getCell(23).getStringCellValue();
            }
            String tnPrice = null;
            if (row.getCell(24)!=null && !"".equals(row.getCell(24).getStringCellValue())){
                tnPrice = row.getCell(24).getStringCellValue();
            }
            String tnArea = null;
            if (row.getCell(25)!=null && !"".equals(row.getCell(25).getStringCellValue())){
                tnArea = row.getCell(25).getStringCellValue();
            }
            String total = null;
            if (row.getCell(26)!=null && !"".equals(row.getCell(26).getStringCellValue())){
                total = row.getCell(26).getStringCellValue();
            }
            String ysBldArea = null;
            if (row.getCell(27)!=null && !"".equals(row.getCell(27).getStringCellValue())){
                ysBldArea = row.getCell(27).getStringCellValue();
            }
            String ysTnArea = null;
            if (row.getCell(28)!=null && !"".equals(row.getCell(28).getStringCellValue())){
                ysTnArea = row.getCell(28).getStringCellValue();
            }
            String scBldArea = null;
            if (row.getCell(29)!=null && !"".equals(row.getCell(29).getStringCellValue())){
                scBldArea = row.getCell(29).getStringCellValue();
            }
            String scTnArea = null;
            if (row.getCell(30)!=null && !"".equals(row.getCell(30).getStringCellValue())){
                scTnArea = row.getCell(30).getStringCellValue();
            }
            String roomStru = null;
            if (row.getCell(31)!=null && !"".equals(row.getCell(31).getStringCellValue())){
                roomStru = row.getCell(31).getStringCellValue();
            }
            String calMode = null;
            if (row.getCell(32)!=null && !"".equals(row.getCell(32).getStringCellValue())){
                calMode = row.getCell(32).getStringCellValue();
            }
            String areaStatus = null;
            if (row.getCell(33)!=null && !"".equals(row.getCell(33).getStringCellValue())){
                areaStatus = row.getCell(33).getStringCellValue();
            }

            Map paramMap = new HashMap();
            paramMap.put("room_id",roomId);
            paramMap.put("room_info",roomInfo);
            paramMap.put("room_name",roomName);
            paramMap.put("room_no",roomNo);
            paramMap.put("no",no);
            paramMap.put("floor_name",floorName);
            paramMap.put("floor_no",floorNo);
            paramMap.put("unit_id",unitId);
            paramMap.put("unit_name",unitName);
            paramMap.put("unit_no",unitNo);
            paramMap.put("build_id",buildId);
            paramMap.put("product_build_name",productBuildName);
            paramMap.put("status_enum",statusEnum);
            paramMap.put("project_id",projectId);
            paramMap.put("project_name",projectName);
            paramMap.put("stage_id",stageId);
            paramMap.put("stage_name",stageName);
            paramMap.put("dj_bld_price",djBldPrice);
            paramMap.put("dj_tn_price",djTnPrice);
            paramMap.put("dj_total",djTotal);
            paramMap.put("bld_price",bldPrice);
            paramMap.put("bld_area",bldArea);
            paramMap.put("hx_id",hxId);
            paramMap.put("hx_name",hxName);
            paramMap.put("tn_price",tnPrice);
            paramMap.put("tn_area",tnArea);
            paramMap.put("total",total);
            paramMap.put("ys_bld_area",ysBldArea);
            paramMap.put("ys_tn_area",ysTnArea);
            paramMap.put("sc_bld_area",scBldArea);
            paramMap.put("sc_tn_area",scTnArea);
            paramMap.put("room_stru",roomStru);
            paramMap.put("cal_mode",calMode);
            paramMap.put("area_status",areaStatus);
            paramsList.add(paramMap);
        }
        if (paramsList.size()>0){
            excelImportMapper.saveRoomData(paramsList);
        }
        return  ResultBody.success("导入成功！");
    }

    //导入户型
    @ResponseBody
    @RequestMapping(value = "/importHx",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody importHx(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<Map> paramsList =new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }

            String id = row.getCell(0).getStringCellValue();
            String hxName = null;
            if (row.getCell(1)!=null){
                hxName = row.getCell(1).getStringCellValue();
            }
            String projectId = row.getCell(2).getStringCellValue();
            String projectFid = null;
            if (row.getCell(3)!=null){
                projectFid = row.getCell(3).getStringCellValue();
            }
            String xHxOtherName = null;
            if (row.getCell(4)!=null){
                xHxOtherName = row.getCell(4).getStringCellValue();
            }
            String tnArea = null;
            if (row.getCell(5)!=null && !"".equals(row.getCell(5).getStringCellValue())){
                tnArea = row.getCell(5).getStringCellValue();
            }
            String bldArea = null;
            if (row.getCell(6)!=null && !"".equals(row.getCell(6).getStringCellValue())){
                bldArea = row.getCell(6).getStringCellValue();
            }
            String remark = null;
            if (row.getCell(7)!=null && !"".equals(row.getCell(7).getStringCellValue())){
                remark = row.getCell(7).getStringCellValue();
            }
            String xAreaSection = null;
            if (row.getCell(8)!=null && !"".equals(row.getCell(8).getStringCellValue())){
                xAreaSection = row.getCell(8).getStringCellValue();
            }
            String xStaircaseProportion = null;
            if (row.getCell(9)!=null && !"".equals(row.getCell(9).getStringCellValue())){
                xStaircaseProportion = row.getCell(9).getStringCellValue();
            }
            String xWideNumber = null;
            if (row.getCell(10)!=null && !"".equals(row.getCell(10).getStringCellValue())){
                xWideNumber = row.getCell(10).getStringCellValue();
            }
            Map paramMap = new HashMap();
            paramMap.put("id",id);
            paramMap.put("hx_name",hxName);
            paramMap.put("project_id",projectId);
            paramMap.put("project_fid",projectFid);
            paramMap.put("remark",remark);
            paramMap.put("tn_area",tnArea);
            paramMap.put("bld_area",bldArea);
            paramMap.put("x_hx_other_name",xHxOtherName);
            paramMap.put("x_area_section",xAreaSection);
            paramMap.put("x_staircase_proportion",xStaircaseProportion);
            paramMap.put("x_wide_number",xWideNumber);
            paramsList.add(paramMap);
        }
        if (paramsList.size()>0){
            excelImportMapper.saveHxData(paramsList);
        }
        return  ResultBody.success("导入成功！");
    }

    @RequestMapping(value = "/getAllImportData",method = RequestMethod.GET)
    public ResultBody getAllImportData(){

        return ResultBody.success(excelImportService.getAllImportData());
    }

    @PostMapping("/importBizTable")
    public ResultBody importBizTable() throws ParseException {
        return ResultBody.success(excelImportService.importBizTable());
    }



    /**
     * 查询当前登录人导出任务
     * @return 查询结果
     */

    @Log("查询当前登录人导出任务")
    @CessBody
    @ApiOperation(value = "查询当前登录人导出任务", notes = "查询当前登录人导出任务")
    @RequestMapping(value = "/getExcelExportDownList", method = RequestMethod.POST)
    public ResultBody getExcelExportDownList() {
        return ResultBody.success(excelImportService.getExcelExportDownList());
    }

    /**
     * 查询当前类型任务是否已存在
     * @return 查询结果
     */

    @Log("查询当前类型任务是否已存在")
    @CessBody
    @ApiOperation(value = "查询当前类型任务是否已存在", notes = "查询当前类型任务是否已存在")
    @RequestMapping(value = "/getExcelExportDownIsExist", method = RequestMethod.POST)
    public ResultBody getExcelExportDownIsExist(@RequestBody ExcelExportLog excelExportLog) {
        return ResultBody.success(excelImportService.getExcelExportDownIsExist(excelExportLog));
    }

    /**
     * 查询导出任务
     * @return 查询结果
     */
    @Log("分页查询导出任务")
    @CessBody
    @ApiOperation(value = "分页查询导出任务", notes = "分页查询导出任务")
    @RequestMapping(value = "/getExcelExportList", method = RequestMethod.POST)
    public ResultBody getExcelExportList(@RequestBody ExcelExportLog excelExportLog) {
        return ResultBody.success(excelImportService.getExcelExportList(excelExportLog));
    }

    /**
     * 更新任务状态为已下载
     * @return
     */
    @Log("更新任务状态为已下载")
    @CessBody
    @ApiOperation(value = "更新任务状态为已下载", notes = "更新任务状态为已下载")
    @RequestMapping(value = "/updateExcelExport", method = RequestMethod.POST)
    public void updateExcelExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelExportLog) {
         excelImportService.updateExcelExport(request,response,excelExportLog);
    }

    /**
     * 重启任务
     * @return
     */
    @Log("重启任务")
    @CessBody
    @ApiOperation(value = "重启任务", notes = "重启任务")
    @RequestMapping(value = "/reLoadExcel", method = RequestMethod.POST)
    public ResultBody reLoadExcel(@RequestBody ExcelExportLog excelExportLog) {
        return excelImportService.reLoadExcel(excelExportLog);
    }


    @Log("查询导出文件类型字典")
    @CessBody
    @ApiOperation(value = "查询导出文件类型字典", notes = "查询导出文件类型字典")
    @RequestMapping(value = "/getExcelExportDist", method = RequestMethod.POST)
    public ResultBody getExcelExportDist() {
        return ResultBody.success(excelImportService.getExcelExportDist());
    }

    //导入人员岗位
    @ResponseBody
    @RequestMapping(value = "/importUserJob",method = RequestMethod.POST)
    public ResultBody importUserJob(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<String> noProName = new ArrayList<>();
        List<String> noUser = new ArrayList<>();
        //获取招商经理和招商专员通用岗ID
        List<Map> comJobs = excelImportMapper.getComJobId();
        String jlComJobId = "";
        String zyComJobId = "";
        String qyjlComJobId = "";
        String qyzyComJobId = "";
        String qyzjComJobId = "";//区域总监
//        String qyyxComJobId = "";//区域营销经理
        for (Map cj:comJobs) {
            if ("xsjl".equals(cj.get("JobCode")+"")){
                jlComJobId = cj.get("ID")+"";
            }else if ("zygw".equals(cj.get("JobCode")+"")){
                zyComJobId = cj.get("ID")+"";
            }else if ("qyxsjl".equals(cj.get("JobCode")+"")){
                qyjlComJobId = cj.get("ID")+"";
            }else if ("qyzygw".equals(cj.get("JobCode")+"")){
                qyzyComJobId = cj.get("ID")+"";
            }else if ("qyzszj".equals(cj.get("JobCode")+"")){
                qyzjComJobId = cj.get("ID")+"";
            }
        }
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row!=null){
                for (Cell cell : row) {
                    cell.setCellType(CellType.STRING);
                }
                if (row.getCell(0)!=null){
                    String alias = row.getCell(0).getStringCellValue();
                    if(row.getCell(2)!=null){
                        String jobCode = row.getCell(2).getStringCellValue();
                        String teamName = row.getCell(3).getStringCellValue();
                        String projectId = "";
                        if (row.getCell(4)!=null){
                            projectId = row.getCell(4).getStringCellValue();
                        }
                        String areaId = "";
                        if (row.getCell(5)!=null){
                            areaId = row.getCell(5).getStringCellValue();
                        }

                        if (noProName.size()>0 && noProName.contains(teamName)){
                            continue;
                        }
                        if (noUser.size()>0 && noUser.contains(alias)){
                            continue;
                        }
                        //获取用户ID
                        String accountId = excelImportMapper.getUserId(alias);
                        if (StringUtils.isEmpty(accountId)){
                            noUser.add(alias);
                            continue;
                        }
                        if (!teamName.contains("组")){
                            teamName = teamName+"一组";
                        }
                        if (!StringUtils.isEmpty(projectId)){
                            //查询项目是否已配置上线
                            int count = excelImportMapper.getProIsUp(projectId);
                            if (count == 0){
                                noProName.add(teamName);
                                continue;
                            }
                        }else{
                            //获取区域组织数据
                            Map orgMap = excelImportMapper.getAreaOrg(areaId);
                            if (orgMap.get("proOrgName")!=null){
                                projectId = orgMap.get("projectId")+"";
                            }else{
                                //项目ID
                                String projectIdNew = UUID.randomUUID().toString();
                                //项目组织ID
                                String orgNew = UUID.randomUUID().toString();
                                //创建区域项目组织
                                Map newProOrgMap = new HashMap();
                                newProOrgMap.put("ID",orgNew);
                                newProOrgMap.put("PID",areaId);
                                newProOrgMap.put("OrgName",orgMap.get("OrgName")+"项目");
                                newProOrgMap.put("FullPath",orgMap.get("FullPath")+"/"+orgMap.get("OrgName")+"项目");
                                newProOrgMap.put("ProjectID",projectIdNew);
                                newProOrgMap.put("OrgCategory","4");
                                newProOrgMap.put("Levels","1");
                                excelImportMapper.addProOrg(newProOrgMap);
                                //创建区域项目岗位
                                //创建团队下岗位
                                Map jobMap = new HashMap();
                                String jlJobId = UUID.randomUUID().toString();
                                jobMap.put("ID",jlJobId);
                                jobMap.put("JobCode","区域总监");
                                jobMap.put("JobName","区域总监");
                                jobMap.put("JobDesc","区域总监");
                                jobMap.put("JobPID","");
                                jobMap.put("CommonJobID",qyzjComJobId);
                                jobMap.put("JobOrgID",orgNew);
                                excelImportMapper.saveProTeamJob(jobMap);
                                //创建区域项目
                                Map newProMap = new HashMap();
                                newProMap.put("ID",projectIdNew);
                                newProMap.put("ProjectNum","QY01");
                                newProMap.put("ProjectName",orgMap.get("OrgName")+"项目");
                                newProMap.put("OrgID",orgNew);
                                newProMap.put("tag","区域项目添加");
                                newProMap.put("AreaID",areaId);
                                newProMap.put("AreaName",orgMap.get("OrgName"));
                                newProMap.put("ComGUID",areaId);
                                newProMap.put("isSyn","1");
                                newProMap.put("isRegion","1");
                                excelImportMapper.addPronew(newProMap);
                                projectId = projectIdNew;
                            }
                        }

                        //查询团队是否已创建
                        String teamOrgId = excelImportMapper.getProTeam(projectId,teamName);

                        String jlJobId = "";//经理岗位ID
                        String zyJobId = "";//专员岗位ID
                        String qyjlJobId = "";//区域经理岗位ID
                        String qyzyJobId = "";//区域专员岗位ID
                        if (StringUtils.isEmpty(teamOrgId)){
                            //创建团队组织
                            //获取项目组织ID
                            Map pidMap = jobMapper.getProOrgData(projectId);
                            Map orgMap = new HashMap();
                            teamOrgId = UUID.randomUUID().toString();
                            orgMap.put("ID",teamOrgId);
                            orgMap.put("PID",pidMap.get("id"));
                            orgMap.put("OrgCode","");
                            orgMap.put("OrgName",teamName);
                            orgMap.put("OrgShortName",teamName);
                            orgMap.put("ProjectID",projectId);
                            excelImportMapper.addProTeamOrg(orgMap);

                            //创建团队下岗位
                            Map jobMap = new HashMap();
                            jlJobId = UUID.randomUUID().toString();
                            jobMap.put("ID",jlJobId);
                            jobMap.put("JobCode","招商经理");
                            jobMap.put("JobName","招商经理");
                            jobMap.put("JobDesc","招商经理");
                            jobMap.put("JobPID","");
                            if (StringUtils.isEmpty(areaId)){
                                jobMap.put("CommonJobID",jlComJobId);
                            }else{
                                qyjlJobId = jlJobId;
                                jobMap.put("CommonJobID",qyjlComJobId);
                            }
                            jobMap.put("JobOrgID",teamOrgId);
                            excelImportMapper.saveProTeamJob(jobMap);
                            Map jobMap1 = new HashMap();
                            zyJobId = UUID.randomUUID().toString();
                            jobMap1.put("ID",zyJobId);
                            jobMap1.put("JobCode","招商专员");
                            jobMap1.put("JobName","招商专员");
                            jobMap1.put("JobDesc","招商专员");
                            jobMap1.put("JobPID","");
                            if (StringUtils.isEmpty(areaId)){
                                jobMap1.put("CommonJobID",zyComJobId);
                            }else{
                                qyzyJobId = zyJobId;
                                jobMap1.put("CommonJobID",qyzyComJobId);
                            }
                            jobMap1.put("JobOrgID",teamOrgId);
                            excelImportMapper.saveProTeamJob(jobMap1);
                        }else{
                            //获取团队下岗位
                            List<Map> jobs = excelImportMapper.getTeamJobs(teamOrgId);
                            for (Map jj:jobs) {
                                if ("xsjl".equals(jj.get("JobCode")+"")){
                                    jlJobId = jj.get("ID")+"";
                                }else if ("zygw".equals(jj.get("JobCode")+"")){
                                    zyJobId = jj.get("ID")+"";
                                }else if ("qyxsjl".equals(jj.get("JobCode")+"")){
                                    qyjlJobId = jj.get("ID")+"";
                                }else if ("qyzygw".equals(jj.get("JobCode")+"")){
                                    qyzyJobId = jj.get("ID")+"";
                                }
                            }
                        }

                        String jobId = "";
                        //判断需要创建的岗位 1:招商专员 2：招商经理
                        if ("1".equals(jobCode)){
                            if (StringUtils.isEmpty(areaId)){
                                jobId = zyJobId;
                            }else{
                                jobId = qyzyJobId;
                            }
                        }else{
                            if (StringUtils.isEmpty(areaId)){
                                jobId = jlJobId;
                            }else{
                                jobId = qyjlJobId;
                            }
                        }
                        //获取用户是否已存在岗位 不存在添加岗位
                        int countJob = excelImportMapper.getUserJobIsOk(accountId,jobId);
                        if (countJob>0){
                            continue;
                        }else{
                            Map paramMap = new HashMap();
                            paramMap.put("AccountID",accountId);
                            paramMap.put("JobID",jobId);
                            paramMap.put("Tag","add");
                            excelImportMapper.addUserJob(paramMap);
                        }
                    }
                }
            }
        }
        String result = "导入成功";
        if (noProName.size()>0){
            String pro =",以下项目未上线："+StringUtils.join(noProName,",");
            result = result+pro;
        }
        if (noUser.size()>0){
            String pro =",以下账号未查询到："+StringUtils.join(noUser,",");
            result = result+pro;
        }
        return  ResultBody.success(result);
    }

    //兰图绘数据迁移
    @ResponseBody
    @RequestMapping(value = "/importLantuhuiInfo",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody importLantuhuiInfo(MultipartFile file) throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sUserId = SecurityUtils.getUserId();
        String sUserName = SecurityUtils.getUsername();
        //日志记录
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("兰图绘数据迁移任务开始");
        sysLog.setNote("人员记录-操作人："+sUserName+"-操作人id："+sUserId);
        excelImportMapper.insertLogs(sysLog);

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<String> noProName = new ArrayList<>();
        List<String> noUser = new ArrayList<>();
        List<ReportCustomerForm> reList = new ArrayList<ReportCustomerForm>();
        String jobCode = null;
        String type = sheet.getRow(0).getCell(3).getStringCellValue();
        System.out.println(sheet.getLastRowNum());
        if(sheet.getLastRowNum() == 0){
            return ResultBody.error(-1001,"暂无数据导入");
        }
        if("企业地址".equals(type)){//企业地址
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row!=null){
                    for (Cell cell : row) {
                        cell.setCellType(CellType.STRING);
                    }
                    ReportCustomerForm reportCustomerForm = new ReportCustomerForm();
                    Map queryMap = new HashMap<>();
                    String customerName = row.getCell(1).getStringCellValue();//客户姓名
                    reportCustomerForm.setCustomerName(customerName);
                    String customerMobile = row.getCell(2).getStringCellValue();//联系人方式
                    reportCustomerForm.setCustomerMobile(customerMobile);
                    String customerAddress = row.getCell(3).getStringCellValue();//企业地址
                    reportCustomerForm.setCustomerAddress(customerAddress);
                    //根据地址获取坐标
                    float[] gps = AMapUtils.AddressTolongitudea(customerAddress);
                    reportCustomerForm.setLongitude(String.valueOf(gps[0]));
                    reportCustomerForm.setLatitude(String.valueOf(gps[1]));
                    String belongIndustriseDesc = row.getCell(4).getStringCellValue();//行业分类
                    reportCustomerForm.setBelongIndustriseDesc(belongIndustriseDesc);
                    //获取行业分类编码
                    queryMap.clear();
                    queryMap.put("dictName",belongIndustriseDesc);
                    Map belongIndustriseMap = excelImportMapper.getDictParentHyfl(queryMap);
                    if(belongIndustriseMap != null && !belongIndustriseMap.isEmpty()){
                        reportCustomerForm.setBelongIndustrise(belongIndustriseMap.get("code")+"");
                    }
                    String belongIndustriseChildDesc = row.getCell(5).getStringCellValue();//二级分类
                    //根据/拆分子级分类 获取行业分类编码
                    String[] cHyzl = belongIndustriseChildDesc.split("/");
                    for (int i = 0;i<cHyzl.length;i++){
                        if(i==0){
                            //设置一级子类
                            reportCustomerForm.setBelongIndustriseTwoDesc(cHyzl[0]);
                            if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustrise())){//大类存在 根据大类获取下一级子类
                                queryMap.clear();
                                queryMap.put("dictCode",reportCustomerForm.getBelongIndustrise());
                                queryMap.put("dictName",cHyzl[0]);
                                Map belongIndustriseTwoMap = excelImportMapper.getDictChildHtzl(queryMap);
                                if(belongIndustriseTwoMap != null && !belongIndustriseTwoMap.isEmpty()){
                                    reportCustomerForm.setBelongIndustriseTwo(belongIndustriseTwoMap.get("code")+"");
                                }
                            }
                        }else if(i==1){
                            //设置二级子类
                            reportCustomerForm.setBelongIndustriseThreeDesc(cHyzl[1]);
                            if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseTwo())){//一级子类存在 根据一级子类获取下一级子类
                                queryMap.clear();
                                queryMap.put("dictCode",reportCustomerForm.getBelongIndustriseTwo());
                                queryMap.put("dictName",cHyzl[1]);
                                Map belongIndustriseThreeMap = excelImportMapper.getDictChildHtzl(queryMap);
                                if(belongIndustriseThreeMap != null && !belongIndustriseThreeMap.isEmpty()){
                                    reportCustomerForm.setBelongIndustriseThree(belongIndustriseThreeMap.get("code")+"");
                                }
                            }
                        }else if(i==2){
                            //设置三级子类
                            reportCustomerForm.setBelongIndustriseFourDesc(cHyzl[2]);
                            if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseThree())){//二级子类存在 根据二级子类获取下一级子类
                                queryMap.clear();
                                queryMap.put("dictCode",reportCustomerForm.getBelongIndustriseThree());
                                queryMap.put("dictName",cHyzl[2]);
                                Map belongIndustriseFourMap = excelImportMapper.getDictChildHtzl(queryMap);
                                if(belongIndustriseFourMap != null && !belongIndustriseFourMap.isEmpty()){
                                    reportCustomerForm.setBelongIndustriseFour(belongIndustriseFourMap.get("code")+"");
                                }
                            }
                        }
                    }
                    String mainProducts = row.getCell(6).getStringCellValue();//主营产品
                    reportCustomerForm.setMainProducts(mainProducts);
                    String projectName = row.getCell(7).getStringCellValue();//意向项目
                    //设置意向项目
                    queryMap.clear();
                    queryMap.put("projectName",projectName);
                    Map pMap = excelImportMapper.getProInfo(queryMap);
                    if(pMap != null && !pMap.isEmpty()){
                        reportCustomerForm.setProjectName(pMap.get("projectName")+"");
                        reportCustomerForm.setProjectId(pMap.get("projectId")+"");
                    }else {
                        noProName.add(projectName);
                        continue;
                    }
                    String createDate = row.getCell(8).getStringCellValue();//录入时间
                    reportCustomerForm.setCreateDate(createDate);
                    String userName = row.getCell(9).getStringCellValue();//录入人
                    String reportUserRole = row.getCell(10).getStringCellValue();//录入人身份
                    //设置录入人身份
                    if("项目招商专员".equals(reportUserRole)){
                        jobCode = "zygw";
                        reportCustomerForm.setReportUserRole("1");
                    }else if("区域招商专员".equals(reportUserRole)){
                        jobCode = "qyzygw";
                        reportCustomerForm.setReportUserRole("2");
                    }
                    //设置报备人和案场归属人
                    queryMap.clear();
                    queryMap.put("projectId",reportCustomerForm.getProjectId());
                    queryMap.put("jobCode",jobCode);
                    queryMap.put("userName",userName);
                    Map uMap = excelImportMapper.getUserOrgInfo(queryMap);
                    if(uMap != null && !uMap.isEmpty()){
                        reportCustomerForm.setEmployeeName(uMap.get("userName")+"");
                        reportCustomerForm.setUserId(uMap.get("userId")+"");
                        reportCustomerForm.setOrgId(uMap.get("orgId")+"");
                        reportCustomerForm.setOrgName(uMap.get("orgName")+"");
                    }else {
                        noUser.add(userName);
                        continue;
                    }
                    String isPark = row.getCell(11).getStringCellValue();//是否园区
                    if("是".equals(isPark)){
                        reportCustomerForm.setIsPark("1");
                        //根据是否园区设置园区信息
                        String parkAddress = row.getCell(12).getStringCellValue();//园区地址
                        reportCustomerForm.setParkAddress(parkAddress);
                        String[] parkAddressStr = addressCutting(parkAddress).split("-");
                        //根据园区地址获取行政区信息
                        if(parkAddressStr.length > 0){
                            reportCustomerForm.setParkProvince(parkAddressStr[0]);
                        }
                        if(parkAddressStr.length > 1){
                            reportCustomerForm.setParkCity(parkAddressStr[1]);
                        }
                        if(parkAddressStr.length > 2){
                            reportCustomerForm.setParkCounty(parkAddressStr[2]);
                        }
                        if(parkAddressStr.length > 3){
                            reportCustomerForm.setParkStreet(parkAddressStr[3]);
                        }
                        String parkFloor = row.getCell(13).getStringCellValue();//园区层数
                        reportCustomerForm.setParkFloor(parkFloor);
                        String parkName = row.getCell(14).getStringCellValue();//园区名称
                        reportCustomerForm.setParkName(parkName);
                    }else {
                        reportCustomerForm.setIsPark("0");
                    }
                    //设置线索信息
                    String ProjectClueId = UUID.randomUUID().toString();
                    reportCustomerForm.setProjectClueUuid(ProjectClueId);
                    reportCustomerForm.setProjectClueId(ProjectClueId);
                    //设置新增节点记录
                    reportCustomerForm.setFollowUpWay("导入");
                    reportCustomerForm.setFollowUpDetail(""+"批量导入地图客户");
                    reportCustomerForm.setCreateDate(sf.format(new Date()));
                    //设置标注信息
                    reportCustomerForm.setDimensionType("1");
                    //设置备注信息 记录导入人员
                    reportCustomerForm.setFlag(sUserName+"-兰图绘数据迁移"+"-账号记录："+sUserId);
                    reList.add(reportCustomerForm);
                }
            }
        }else {//坐标
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row!=null){
                    for (Cell cell : row) {
                        cell.setCellType(CellType.STRING);
                    }
                    ReportCustomerForm reportCustomerForm = new ReportCustomerForm();
                    Map queryMap = new HashMap<>();
                    String customerName = row.getCell(1).getStringCellValue();//客户姓名
                    reportCustomerForm.setCustomerName(customerName);
                    String customerMobile = row.getCell(2).getStringCellValue();//联系人方式
                    reportCustomerForm.setCustomerMobile(customerMobile);
                    String longitude = row.getCell(3).getStringCellValue();//经度
                    reportCustomerForm.setLongitude(longitude);
                    String latitude = row.getCell(4).getStringCellValue();//纬度
                    reportCustomerForm.setLatitude(latitude);
                    //根据经纬度获取地址
                    String customerAddress = AMapUtils.longitudeToAddress(Float.parseFloat(latitude),Float.parseFloat(longitude));
                    reportCustomerForm.setCustomerAddress(customerAddress);
                    String belongIndustriseDesc = row.getCell(5).getStringCellValue();//行业分类
                    reportCustomerForm.setBelongIndustriseDesc(belongIndustriseDesc);
                    //获取行业分类编码
                    queryMap.clear();
                    queryMap.put("dictName",belongIndustriseDesc);
                    Map belongIndustriseMap = excelImportMapper.getDictParentHyfl(queryMap);
                    if(belongIndustriseMap != null && !belongIndustriseMap.isEmpty()){
                        reportCustomerForm.setBelongIndustrise(belongIndustriseMap.get("code")+"");
                    }
                    String belongIndustriseChildDesc = row.getCell(5).getStringCellValue();//二级分类
                    //根据/拆分子级分类 获取行业分类编码
                    String[] cHyzl = belongIndustriseChildDesc.split("/");
                    for (int i = 0;i<cHyzl.length;i++){
                        if(i==0){
                            //设置一级子类
                            reportCustomerForm.setBelongIndustriseTwoDesc(cHyzl[0]);
                            if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustrise())){//大类存在 根据大类获取下一级子类
                                queryMap.clear();
                                queryMap.put("dictCode",reportCustomerForm.getBelongIndustrise());
                                queryMap.put("dictName",cHyzl[0]);
                                Map belongIndustriseTwoMap = excelImportMapper.getDictChildHtzl(queryMap);
                                if(belongIndustriseTwoMap != null && !belongIndustriseTwoMap.isEmpty()){
                                    reportCustomerForm.setBelongIndustriseTwo(belongIndustriseTwoMap.get("code")+"");
                                }
                            }
                        }else if(i==1){
                            //设置二级子类
                            reportCustomerForm.setBelongIndustriseThreeDesc(cHyzl[1]);
                            if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseTwo())){//一级子类存在 根据一级子类获取下一级子类
                                queryMap.clear();
                                queryMap.put("dictCode",reportCustomerForm.getBelongIndustriseTwo());
                                queryMap.put("dictName",cHyzl[1]);
                                Map belongIndustriseThreeMap = excelImportMapper.getDictChildHtzl(queryMap);
                                if(belongIndustriseThreeMap != null && !belongIndustriseThreeMap.isEmpty()){
                                    reportCustomerForm.setBelongIndustriseThree(belongIndustriseThreeMap.get("code")+"");
                                }
                            }
                        }else if(i==2){
                            //设置三级子类
                            reportCustomerForm.setBelongIndustriseFourDesc(cHyzl[2]);
                            if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseThree())){//二级子类存在 根据二级子类获取下一级子类
                                queryMap.clear();
                                queryMap.put("dictCode",reportCustomerForm.getBelongIndustriseThree());
                                queryMap.put("dictName",cHyzl[2]);
                                Map belongIndustriseFourMap = excelImportMapper.getDictChildHtzl(queryMap);
                                if(belongIndustriseFourMap != null && !belongIndustriseFourMap.isEmpty()){
                                    reportCustomerForm.setBelongIndustriseFour(belongIndustriseFourMap.get("code")+"");
                                }
                            }
                        }
                    }
                    String mainProducts = row.getCell(7).getStringCellValue();//主营产品
                    reportCustomerForm.setMainProducts(mainProducts);
                    String projectName = row.getCell(8).getStringCellValue();//意向项目
                    //设置意向项目
                    queryMap.clear();
                    queryMap.put("projectName",projectName);
                    Map pMap = excelImportMapper.getProInfo(queryMap);
                    if(pMap != null && !pMap.isEmpty()){
                        reportCustomerForm.setProjectName(pMap.get("projectName")+"");
                        reportCustomerForm.setProjectId(pMap.get("projectId")+"");
                    }else {
                        noProName.add(projectName);
                        continue;
                    }
                    String createDate = row.getCell(9).getStringCellValue();//录入时间
                    reportCustomerForm.setCreateDate(createDate);
                    String userName = row.getCell(10).getStringCellValue();//录入人
                    String reportUserRole = row.getCell(11).getStringCellValue();//录入人身份
                    //设置录入人身份
                    if("项目招商专员".equals(reportUserRole)){
                        jobCode = "zygw";
                        reportCustomerForm.setReportUserRole("1");
                    }else if("区域招商专员".equals(reportUserRole)){
                        jobCode = "qyzygw";
                        reportCustomerForm.setReportUserRole("2");
                    }
                    //设置报备人和案场归属人
                    queryMap.clear();
                    queryMap.put("projectId",reportCustomerForm.getProjectId());
                    queryMap.put("jobCode",jobCode);
                    queryMap.put("userName",userName);
                    Map uMap = excelImportMapper.getUserOrgInfo(queryMap);
                    if(uMap != null && !uMap.isEmpty()){
                        reportCustomerForm.setEmployeeName(uMap.get("userName")+"");
                        reportCustomerForm.setUserId(uMap.get("userId")+"");
                        reportCustomerForm.setOrgId(uMap.get("orgId")+"");
                        reportCustomerForm.setOrgName(uMap.get("orgName")+"");
                    }else {
                        noUser.add(userName);
                        continue;
                    }
                    String isPark = row.getCell(12).getStringCellValue();//是否园区
                    if("是".equals(isPark)){
                        reportCustomerForm.setIsPark("1");
                        //根据是否园区设置园区信息
                        String parkAddress = row.getCell(13).getStringCellValue();//园区地址
                        reportCustomerForm.setParkAddress(parkAddress);
                        String[] parkAddressStr = addressCutting(parkAddress).split("-");
                        //根据园区地址获取行政区信息
                        if(parkAddressStr.length > 0){
                            reportCustomerForm.setParkProvince(parkAddressStr[0]);
                        }
                        if(parkAddressStr.length > 1){
                            reportCustomerForm.setParkCity(parkAddressStr[1]);
                        }
                        if(parkAddressStr.length > 2){
                            reportCustomerForm.setParkCounty(parkAddressStr[2]);
                        }
                        if(parkAddressStr.length > 3){
                            reportCustomerForm.setParkStreet(parkAddressStr[3]);
                        }
                        String parkFloor = row.getCell(14).getStringCellValue();//园区层数
                        reportCustomerForm.setParkFloor(parkFloor);
                        String parkName = row.getCell(15).getStringCellValue();//园区名称
                        reportCustomerForm.setParkName(parkName);
                    }else {
                        reportCustomerForm.setIsPark("0");
                    }
                    //设置线索信息
                    String ProjectClueId = UUID.randomUUID().toString();
                    reportCustomerForm.setProjectClueUuid(ProjectClueId);
                    reportCustomerForm.setProjectClueId(ProjectClueId);
                    //设置新增节点记录
                    reportCustomerForm.setFollowUpWay("走访");
                    reportCustomerForm.setFollowUpDetail("招商专员-新增走访");
                    reportCustomerForm.setCreateDate(sf.format(new Date()));
                    //设置标注信息
                    reportCustomerForm.setDimensionType("1");
                    //设置备注信息 记录导入人员
                    reportCustomerForm.setFlag(sUserName+"-兰图绘数据迁移"+"-账号记录："+sUserId);
                    reList.add(reportCustomerForm);
                }
            }
        }
        if(reList.size()>0){
            //线索表新增
            excelImportMapper.insertProjectClues(reList);
            //跟进记录新增节点记录
            excelImportMapper.saveFollowNodeUpRecord(reList);
            //新增标注信息
            excelImportMapper.addDimension(reList);
            //保存详细信息
            excelImportMapper.saveInformationZ(reList);
        }
        String result = "导入成功";
        if (noProName.size()>0){
            String pro =",以下项目未查询到："+StringUtils.join(noProName,",");
            result = result+pro;
        }
        if (noUser.size()>0){
            String pro =",以下人员未查询到："+StringUtils.join(noUser,",");
            result = result+pro;
        }
        SysLog sysLog0 = new SysLog();
        sysLog0.setExecutTime(sf.format(new Date()));
        sysLog0.setTaskName("兰图绘数据迁移任务结束");
        sysLog0.setNote(result+"-人员记录-操作人："+sUserName+"-操作人账号："+sUserId);
        excelImportMapper.insertLogs(sysLog0);
        return  ResultBody.success(result);
    }

    //批量导入标注
    @ResponseBody
    @RequestMapping(value = "/imporButchtMark",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody imporButchtMark(MultipartFile file) throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sUserId = SecurityUtils.getUserId();
        String sUserName = SecurityUtils.getUsername();
        //日志记录
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("批量导入临时标注任务开始");
        sysLog.setNote("人员记录-操作人："+sUserName+"-操作人id："+sUserId);
        excelImportMapper.insertLogs(sysLog);

        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        List<String> noProName = new ArrayList<>();
        List<String> noUser = new ArrayList<>();
        List<ReportCustomerForm> reList = new ArrayList<ReportCustomerForm>();
        String jobCode = null;
        String type = sheet.getRow(0).getCell(3).getStringCellValue();
        System.out.println(sheet.getLastRowNum());
        if(sheet.getLastRowNum() == 0){
            return ResultBody.error(-1001,"暂无数据导入");
        }
        try {
            if("企业地址".equals(type)){//企业地址
                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row!=null){
                        for (Cell cell : row) {
                            cell.setCellType(CellType.STRING);
                        }
                        ReportCustomerForm reportCustomerForm = new ReportCustomerForm();
                        Map queryMap = new HashMap<>();
                        String customerName = row.getCell(1).getStringCellValue();//客户姓名
                        reportCustomerForm.setCustomerName(customerName);
                        String customerMobile = row.getCell(2).getStringCellValue();//联系人方式
                        reportCustomerForm.setCustomerMobile(customerMobile);
                        String customerAddress = row.getCell(3).getStringCellValue();//企业地址
                        reportCustomerForm.setCustomerAddress(customerAddress);
                        //根据地址获取坐标
                        float[] gps = AMapUtils.AddressTolongitudea(customerAddress);
                        reportCustomerForm.setLongitude(String.valueOf(gps[0]));
                        reportCustomerForm.setLatitude(String.valueOf(gps[1]));
                        String belongIndustriseDesc = row.getCell(4).getStringCellValue();//行业分类
                        reportCustomerForm.setBelongIndustriseDesc(belongIndustriseDesc);
                        //获取行业分类编码
                        queryMap.clear();
                        queryMap.put("dictName",belongIndustriseDesc);
                        Map belongIndustriseMap = excelImportMapper.getDictParentHyfl(queryMap);
                        if(belongIndustriseMap != null && !belongIndustriseMap.isEmpty()){
                            reportCustomerForm.setBelongIndustrise(belongIndustriseMap.get("code")+"");
                        }
                        String belongIndustriseChildDesc = row.getCell(5).getStringCellValue();//二级分类
                        //根据/拆分子级分类 获取行业分类编码
                        String[] cHyzl = belongIndustriseChildDesc.split("/");
                        for (int i = 0;i<cHyzl.length;i++){
                            if(i==0){
                                //设置一级子类
                                reportCustomerForm.setBelongIndustriseTwoDesc(cHyzl[0]);
                                if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustrise())){//大类存在 根据大类获取下一级子类
                                    queryMap.clear();
                                    queryMap.put("dictCode",reportCustomerForm.getBelongIndustrise());
                                    queryMap.put("dictName",cHyzl[0]);
                                    Map belongIndustriseTwoMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if(belongIndustriseTwoMap != null && !belongIndustriseTwoMap.isEmpty()){
                                        reportCustomerForm.setBelongIndustriseTwo(belongIndustriseTwoMap.get("code")+"");
                                    }
                                }
                            }else if(i==1){
                                //设置二级子类
                                reportCustomerForm.setBelongIndustriseThreeDesc(cHyzl[1]);
                                if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseTwo())){//一级子类存在 根据一级子类获取下一级子类
                                    queryMap.clear();
                                    queryMap.put("dictCode",reportCustomerForm.getBelongIndustriseTwo());
                                    queryMap.put("dictName",cHyzl[1]);
                                    Map belongIndustriseThreeMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if(belongIndustriseThreeMap != null && !belongIndustriseThreeMap.isEmpty()){
                                        reportCustomerForm.setBelongIndustriseThree(belongIndustriseThreeMap.get("code")+"");
                                    }
                                }
                            }else if(i==2){
                                //设置三级子类
                                reportCustomerForm.setBelongIndustriseFourDesc(cHyzl[2]);
                                if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseThree())){//二级子类存在 根据二级子类获取下一级子类
                                    queryMap.clear();
                                    queryMap.put("dictCode",reportCustomerForm.getBelongIndustriseThree());
                                    queryMap.put("dictName",cHyzl[2]);
                                    Map belongIndustriseFourMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if(belongIndustriseFourMap != null && !belongIndustriseFourMap.isEmpty()){
                                        reportCustomerForm.setBelongIndustriseFour(belongIndustriseFourMap.get("code")+"");
                                    }
                                }
                            }
                        }
                        String mainProducts = row.getCell(6).getStringCellValue();//主营产品
                        reportCustomerForm.setMainProducts(mainProducts);
                        String projectName = row.getCell(7).getStringCellValue();//意向项目
                        //设置意向项目
                        queryMap.clear();
                        queryMap.put("projectName",projectName);
                        Map pMap = excelImportMapper.getProInfo(queryMap);
                        if(pMap != null && !pMap.isEmpty()){
                            reportCustomerForm.setProjectName(pMap.get("projectName")+"");
                            reportCustomerForm.setProjectId(pMap.get("projectId")+"");
                        }else {
                            noProName.add(projectName);
                            continue;
                        }
                        String createDate = row.getCell(8).getStringCellValue();//录入时间
                        reportCustomerForm.setCreateDate(createDate);
                        String userName = row.getCell(9).getStringCellValue();//录入人
                        String reportUserRole = row.getCell(10).getStringCellValue();//录入人身份
                        //设置录入人身份
                        if("项目招商专员".equals(reportUserRole)){
                            jobCode = "zygw";
                            reportCustomerForm.setReportUserRole("1");
                        }else if("区域招商专员".equals(reportUserRole)){
                            jobCode = "qyzygw";
                            reportCustomerForm.setReportUserRole("2");
                        }
                        //设置报备人和案场归属人
                        queryMap.clear();
                        queryMap.put("projectId",reportCustomerForm.getProjectId());
                        queryMap.put("jobCode",jobCode);
                        queryMap.put("userName",userName);
                        Map uMap = excelImportMapper.getUserOrgInfo(queryMap);
                        if(uMap != null && !uMap.isEmpty()){
                            reportCustomerForm.setEmployeeName(uMap.get("userName")+"");
                            reportCustomerForm.setUserId(uMap.get("userId")+"");
                            reportCustomerForm.setOrgId(uMap.get("orgId")+"");
                            reportCustomerForm.setOrgName(uMap.get("orgName")+"");
                        }else {
                            noUser.add(userName);
                            continue;
                        }
                        String isPark = row.getCell(11).getStringCellValue();//是否园区
                        if("是".equals(isPark)){
                            reportCustomerForm.setIsPark("1");
                            //根据是否园区设置园区信息
                            String parkAddress = row.getCell(12).getStringCellValue();//园区地址
                            reportCustomerForm.setParkAddress(parkAddress);
                            String[] parkAddressStr = addressCutting(parkAddress).split("-");
                            //根据园区地址获取行政区信息
                            if(parkAddressStr.length > 0){
                                reportCustomerForm.setParkProvince(parkAddressStr[0]);
                            }
                            if(parkAddressStr.length > 1){
                                reportCustomerForm.setParkCity(parkAddressStr[1]);
                            }
                            if(parkAddressStr.length > 2){
                                reportCustomerForm.setParkCounty(parkAddressStr[2]);
                            }
                            if(parkAddressStr.length > 3){
                                reportCustomerForm.setParkStreet(parkAddressStr[3]);
                            }
                            String parkFloor = row.getCell(13).getStringCellValue();//园区层数
                            reportCustomerForm.setParkFloor(parkFloor);
                            String parkName = row.getCell(14).getStringCellValue();//园区名称
                            reportCustomerForm.setParkName(parkName);
                        }else {
                            reportCustomerForm.setIsPark("0");
                        }
                        String label = row.getCell(15).getStringCellValue();//标签
                        reportCustomerForm.setLabel(label);
                        //设置线索信息
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        //设置新增节点记录
                        reportCustomerForm.setFollowUpWay("走访");
                        reportCustomerForm.setFollowUpDetail("招商专员-新增走访");
                        reportCustomerForm.setCreateDate(sf.format(new Date()));
                        //设置标注信息
                        reportCustomerForm.setDimensionType("1");
                        //设置备注信息 记录导入人员
                        reportCustomerForm.setFlag(sUserName+"-批量导入标注"+"-账号记录："+sUserId);
                        reList.add(reportCustomerForm);
                    }
                }
            }else {//坐标
                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row!=null){
                        for (Cell cell : row) {
                            cell.setCellType(CellType.STRING);
                        }
                        ReportCustomerForm reportCustomerForm = new ReportCustomerForm();
                        Map queryMap = new HashMap<>();
                        String customerName = row.getCell(1).getStringCellValue();//客户姓名
                        reportCustomerForm.setCustomerName(customerName);
                        String customerMobile = row.getCell(2).getStringCellValue();//联系人方式
                        reportCustomerForm.setCustomerMobile(customerMobile);
                        String longitude = row.getCell(3).getStringCellValue();//经度
                        reportCustomerForm.setLongitude(longitude);
                        String latitude = row.getCell(4).getStringCellValue();//纬度
                        reportCustomerForm.setLatitude(latitude);
                        //根据经纬度获取地址
                        String customerAddress = AMapUtils.longitudeToAddress(Float.parseFloat(latitude),Float.parseFloat(longitude));
                        reportCustomerForm.setCustomerAddress(customerAddress);
                        String belongIndustriseDesc = row.getCell(5).getStringCellValue();//行业分类
                        reportCustomerForm.setBelongIndustriseDesc(belongIndustriseDesc);
                        //获取行业分类编码
                        queryMap.clear();
                        queryMap.put("dictName",belongIndustriseDesc);
                        Map belongIndustriseMap = excelImportMapper.getDictParentHyfl(queryMap);
                        if(belongIndustriseMap != null && !belongIndustriseMap.isEmpty()){
                            reportCustomerForm.setBelongIndustrise(belongIndustriseMap.get("code")+"");
                        }
                        String belongIndustriseChildDesc = row.getCell(5).getStringCellValue();//二级分类
                        //根据/拆分子级分类 获取行业分类编码
                        String[] cHyzl = belongIndustriseChildDesc.split("/");
                        for (int i = 0;i<cHyzl.length;i++){
                            if(i==0){
                                //设置一级子类
                                reportCustomerForm.setBelongIndustriseTwoDesc(cHyzl[0]);
                                if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustrise())){//大类存在 根据大类获取下一级子类
                                    queryMap.clear();
                                    queryMap.put("dictCode",reportCustomerForm.getBelongIndustrise());
                                    queryMap.put("dictName",cHyzl[0]);
                                    Map belongIndustriseTwoMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if(belongIndustriseTwoMap != null && !belongIndustriseTwoMap.isEmpty()){
                                        reportCustomerForm.setBelongIndustriseTwo(belongIndustriseTwoMap.get("code")+"");
                                    }
                                }
                            }else if(i==1){
                                //设置二级子类
                                reportCustomerForm.setBelongIndustriseThreeDesc(cHyzl[1]);
                                if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseTwo())){//一级子类存在 根据一级子类获取下一级子类
                                    queryMap.clear();
                                    queryMap.put("dictCode",reportCustomerForm.getBelongIndustriseTwo());
                                    queryMap.put("dictName",cHyzl[1]);
                                    Map belongIndustriseThreeMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if(belongIndustriseThreeMap != null && !belongIndustriseThreeMap.isEmpty()){
                                        reportCustomerForm.setBelongIndustriseThree(belongIndustriseThreeMap.get("code")+"");
                                    }
                                }
                            }else if(i==2){
                                //设置三级子类
                                reportCustomerForm.setBelongIndustriseFourDesc(cHyzl[2]);
                                if(StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseThree())){//二级子类存在 根据二级子类获取下一级子类
                                    queryMap.clear();
                                    queryMap.put("dictCode",reportCustomerForm.getBelongIndustriseThree());
                                    queryMap.put("dictName",cHyzl[2]);
                                    Map belongIndustriseFourMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if(belongIndustriseFourMap != null && !belongIndustriseFourMap.isEmpty()){
                                        reportCustomerForm.setBelongIndustriseFour(belongIndustriseFourMap.get("code")+"");
                                    }
                                }
                            }
                        }
                        String mainProducts = row.getCell(7).getStringCellValue();//主营产品
                        reportCustomerForm.setMainProducts(mainProducts);
                        String projectName = row.getCell(8).getStringCellValue();//意向项目
                        //设置意向项目
                        queryMap.clear();
                        queryMap.put("projectName",projectName);
                        Map pMap = excelImportMapper.getProInfo(queryMap);
                        if(pMap != null && !pMap.isEmpty()){
                            reportCustomerForm.setProjectName(pMap.get("projectName")+"");
                            reportCustomerForm.setProjectId(pMap.get("projectId")+"");
                        }else {
                            noProName.add(projectName);
                            continue;
                        }
                        String createDate = row.getCell(9).getStringCellValue();//录入时间
                        reportCustomerForm.setCreateDate(createDate);
                        String userName = row.getCell(10).getStringCellValue();//录入人
                        String reportUserRole = row.getCell(11).getStringCellValue();//录入人身份
                        //设置录入人身份
                        if("项目招商专员".equals(reportUserRole)){
                            jobCode = "zygw";
                            reportCustomerForm.setReportUserRole("1");
                        }else if("区域招商专员".equals(reportUserRole)){
                            jobCode = "qyzygw";
                            reportCustomerForm.setReportUserRole("2");
                        }
                        //设置报备人和案场归属人
                        queryMap.clear();
                        queryMap.put("projectId",reportCustomerForm.getProjectId());
                        queryMap.put("jobCode",jobCode);
                        queryMap.put("userName",userName);
                        Map uMap = excelImportMapper.getUserOrgInfo(queryMap);
                        if(uMap != null && !uMap.isEmpty()){
                            reportCustomerForm.setEmployeeName(uMap.get("userName")+"");
                            reportCustomerForm.setUserId(uMap.get("userId")+"");
                            reportCustomerForm.setOrgId(uMap.get("orgId")+"");
                            reportCustomerForm.setOrgName(uMap.get("orgName")+"");
                        }else {
                            noUser.add(userName);
                            continue;
                        }
                        String isPark = row.getCell(12).getStringCellValue();//是否园区
                        if("是".equals(isPark)){
                            reportCustomerForm.setIsPark("1");
                            //根据是否园区设置园区信息
                            String parkAddress = row.getCell(13).getStringCellValue();//园区地址
                            reportCustomerForm.setParkAddress(parkAddress);
                            String[] parkAddressStr = addressCutting(parkAddress).split("-");
                            //根据园区地址获取行政区信息
                            if(parkAddressStr.length > 0){
                                reportCustomerForm.setParkProvince(parkAddressStr[0]);
                            }
                            if(parkAddressStr.length > 1){
                                reportCustomerForm.setParkCity(parkAddressStr[1]);
                            }
                            if(parkAddressStr.length > 2){
                                reportCustomerForm.setParkCounty(parkAddressStr[2]);
                            }
                            if(parkAddressStr.length > 3){
                                reportCustomerForm.setParkStreet(parkAddressStr[3]);
                            }
                            String parkFloor = row.getCell(14).getStringCellValue();//园区层数
                            reportCustomerForm.setParkFloor(parkFloor);
                            String parkName = row.getCell(15).getStringCellValue();//园区名称
                            reportCustomerForm.setParkName(parkName);String label = row.getCell(16).getStringCellValue();//标签
                            reportCustomerForm.setLabel(label);
                        }else {
                            reportCustomerForm.setIsPark("0");
                        }
                        //设置线索信息
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        //设置新增节点记录
                        reportCustomerForm.setFollowUpWay("走访");
                        reportCustomerForm.setFollowUpDetail("招商专员-新增走访");
                        reportCustomerForm.setCreateDate(sf.format(new Date()));
                        //设置标注信息
                        reportCustomerForm.setDimensionType("1");
                        reportCustomerForm.setFlag(sUserName+"-批量导入标注"+"-账号记录："+sUserId);
                        reList.add(reportCustomerForm);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1001,"数据有误 导入失败 请检查！");
        }
        if(reList.size() > 0){
            //清空临时表
            excelImportMapper.delProhectClueMarkTemp(sUserId);
            //保存进入临时表
            excelImportMapper.saveProhectClueMarkTemp(reList,sUserId);
        }
        String result = "导入成功";
        if (noProName.size()>0){
            String pro =",以下项目未查询到："+StringUtils.join(noProName,",");
            result = result+pro;
        }
        if (noUser.size()>0){
            String pro =",以下人员未查询到："+StringUtils.join(noUser,",");
            result = result+pro;
        }
        SysLog sysLog0 = new SysLog();
        sysLog0.setExecutTime(sf.format(new Date()));
        sysLog0.setTaskName("批量导入临时标注任务结束");
        sysLog0.setNote(result+"-人员记录-操作人："+sUserName+"-操作人账号："+sUserId+"-导入结果："+result);
        excelImportMapper.insertLogs(sysLog0);
        return  ResultBody.success(result);
    }

    //查看批量导入标注
    @ResponseBody
    @RequestMapping(value = "/getButchtMark",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody getButchtMark(@RequestBody ReportCustomerForm reportCustomerForm) {
        reportCustomerForm.setUserId(SecurityUtils.getUserId());
        return excelImportService.getButchtMark(reportCustomerForm);
    }

    //保存批量导入标注
    @ResponseBody
    @RequestMapping(value = "/saveButchtMark",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveButchtMark() {
        return excelImportService.saveButchtMark();
    }

    /*
    * 提取地址中的省市区，兼容XX区XX小区等地址中出现多个市和区的问题
     * */
    public static String addressCutting(String address) {
        if (address.startsWith("北京市") || address.startsWith("天津市") || address.startsWith("上海市") || address.startsWith("重庆市")) {
            address = address.substring(0, 3) + "市辖区" + address.substring(3);
        }
        String regex = "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
        Matcher m = Pattern.compile(regex).matcher(address);
        String province = null, city = null, county = null, town = null, village = null;
        while(m.find()){
            province = m.group("province");

            if (province.equals("北京市") || province.equals("天津市") || province.equals("上海市") || province.equals("重庆市")) {
                city = province;

                county = m.group("city");
                if (county.split("区").length > 1) {
                    town = county.substring(county.indexOf("区") + 1);
                    county = county.substring(0, county.indexOf("区") + 1);
                    if (town.contains("区")) {
                        town = town.substring(county.indexOf("区") + 1);
                    }
                } else {
                    county = m.group("county");
                    if (county.split("区").length > 1) {
                        town = county.substring(county.indexOf("区") + 1);
                        county = county.substring(0, county.indexOf("区") + 1);
                    }
                }
            } else {
                city = m.group("city");

                county = m.group("county");
                if (county != null && !"".equals(county)) {
                    if (county.split("市").length > 1 && county.indexOf("市") < 5) {
                        town = county;
                        county = county.substring(0, county.indexOf("市") + 1);
                        town = town.substring(county.indexOf("市") + 1);
                    }
                    if (county.split("旗").length > 1) {
                        town = county;
                        county = county.substring(0, county.indexOf("旗") + 1);
                        town = town.substring(county.indexOf("旗") + 1);
                    }
                    if (county.split("海域").length > 1) {
                        town = county;
                        county = county.substring(0, county.indexOf("海域") + 2);
                        town = town.substring(county.indexOf("海域") + 2);
                    }
                    if (county.split("区").length > 1) {
                        town = county;
                        county = county.substring(0, county.indexOf("区") + 1);
                        town = town.substring(county.indexOf("区") + 1);
                    }
                }

            }

            if (province != null && !"".equals(province)) {
                province = province + "-";
            }

            if (city != null && !"".equals(city)) {
                city = city + "-";
            }

            if (county != null && !"".equals(county)) {
                county = county + "-";
            }

            town+=m.group("town");
            if ((county == null || "".equals(county)) && town != null && !"".equals(town)) {
                town = town + "-";
            }
            village=m.group("village");

        }

        String newMachineAdress = province + city + county + town + village;
        if (newMachineAdress != null && !"".equals(newMachineAdress)) {
            newMachineAdress = newMachineAdress.replaceAll("null", "");
        }

        if (newMachineAdress == null || "".equals(newMachineAdress)) {
            newMachineAdress = address;
        }

        return newMachineAdress;
    }

    //导入人员成本速算
    @ResponseBody
    @RequestMapping(value = "/importUserCsss",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody excelImportUserCsss(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        Map comJobMap = excelImportMapper.getComJobInfoByJobCode("qycsss");
        List<Map> paramsList =new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }
            String projectName = row.getCell(0).getStringCellValue();
            String orgName = row.getCell(1).getStringCellValue();
            String userName = row.getCell(2).getStringCellValue();
//            String cost = row.getCell(3).getStringCellValue();
//            String remark = row.getCell(4).getStringCellValue();
            //通过组织名称 获取项目信息
            Map proMap = excelImportMapper.getProInfoByorgName(orgName);
            //通过人员名称 获取用户信息
            Map userMap = excelImportMapper.getUserInfoByUserName(userName);
            if(proMap!=null && !proMap.isEmpty() && userMap!=null && !userMap.isEmpty()){
                String orgId = String.valueOf(proMap.get("orgId"));
                //判断项目下是否存在区域项目成本速算岗位
                Map paramsMap = new HashMap();
                paramsMap.put("orgId",orgId);
                paramsMap.put("commonJobID", String.valueOf(comJobMap.get("ID")));
                Map jobMap = excelImportMapper.getJobInfoInsProject(paramsMap);
                //如果没有 创建岗位 引入人员 如果有 开始引入人员
                if(jobMap!=null && !jobMap.isEmpty()){
                    //判断当前人员是否已存在该岗位下 若没有 直接引入 若有 跳过
                    Map jobuserMap = excelImportMapper.getUserJobrelInfo(String.valueOf(userMap.get("userId")), String.valueOf(jobMap.get("ID")));
                    if (jobuserMap != null && !jobuserMap.isEmpty()) {

                    } else {
                        Map userJobMap = new HashMap();
                        userJobMap.put("ID",String.valueOf(userMap.get("userId")));
                        userJobMap.put("jobId",String.valueOf(jobMap.get("ID")));
                        userJobMap.put("CurrentJob","0");
                        userJobMap.put("Tag","导入区域项目成本速算");
                        excelImportMapper.saveJobSuserrel(userJobMap);
                    }
                }else {
                    Map reqMap = new HashMap();
                    String ID = UUID.randomUUID().toString();
                    reqMap.put("ID",ID);
                    reqMap.put("AuthCompanyID","ede1b679-3546-11e7-a3f8-5254007b6f02");
                    reqMap.put("CommonJobID", String.valueOf(comJobMap.get("ID")));
                    reqMap.put("Creator","导入区域项目成本速算");
                    reqMap.put("Editor","导入区域项目成本速算");
                    reqMap.put("JobCode","区域项目成本速算");
                    reqMap.put("JobDesc","区域项目成本速算");
                    reqMap.put("JobName","区域项目成本速算");
                    reqMap.put("JobOrgID",orgId);
                    reqMap.put("ProductID","ee3b2466-3546-11e7-a3f8-5254007b6f02");
                    reqMap.put("Status","1");
                    int i = excelImportMapper.saveSystemJobForManagement(reqMap);
                    Map datum = new HashMap();
                    datum.put("ID",userMap.get("userId"));
                    datum.put("jobId",ID);
                    datum.put("CurrentJob","0");
                    datum.put("Tag","导入区域项目成本速算");
                    excelImportMapper.saveJobSuserrel(datum);
                }

            }
        }
        return ResultBody.success("调用成功");
    }

    //导入项目公客池测试客户
    @ResponseBody
    @RequestMapping(value = "/importCustomerPoolsCs",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody excelImportCustomerPoolCs(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

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
        System.out.println(sheet.getLastRowNum());
        //判断系统配置规则
        ProjectRuleDetail projectRuleDetailXt = projectCluesDao.selectProjectRuleZs("-1","2");
        //不可分配的客户信息
        List<String> notOppIds = new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }
            String employeeName = row.getCell(0).getStringCellValue();//专员名称
            String projectName = row.getCell(1).getStringCellValue();//所属项目
            String customerType = row.getCell(2).getStringCellValue();//客户类型 0:企业 1:个人
            String sourceModeName = row.getCell(3).getStringCellValue();//客户来源
            String customerName = row.getCell(4).getStringCellValue();//公司名称
            String contacts = row.getCell(5).getStringCellValue();//联系人
            String position = row.getCell(6).getStringCellValue();//职位描述
            String customerMobile = row.getCell(7).getStringCellValue();//联系方式
            String mainProducts = row.getCell(8).getStringCellValue();//主营产品
            String demandRemarks = row.getCell(9).getStringCellValue();//特殊意向需求需备注
            //通过项目名称 获取项目信息
            Map proMap = excelImportMapper.getProInfoByProName(projectName);
            //通过人员名称 获取用户信息
            Map userMap = excelImportMapper.getUserInfoByUserName(employeeName);
            //通过项目和人员新获取组织信息
            Map orgMap= excelImportMapper.getOrgInfoByProAndUser(String.valueOf(proMap.get("projectId")),String.valueOf(userMap.get("userId")));
            //针对项目判重客户
            Map map = new HashMap();
            map.put("projectId",String.valueOf(proMap.get("projectId")));
            map.put("customerMobile",customerMobile);
            map.put("customerName",customerName);
            //原机会的项目ID
            String oldProjectId = String.valueOf(proMap.get("projectId"));
            //处理项目联动
            List<String> proList = new ArrayList<>();
            String proIds = projectCluesDao.getTranslateProIds(String.valueOf(proMap.get("projectId")));
            if(StringUtils.isNotEmpty(proIds)){
                proList = new ArrayList(Arrays.asList(proIds.split(",")));
            }
            //不管有无联动项目 保证原项目存在
            proList.add(String.valueOf(proMap.get("projectId")));
            map.put("proList",proList);
            map.put("judgeNoPool",projectRuleDetailXt.getJudgeNoPool());
            map.put("judgeNoRegion",projectRuleDetailXt.getJudgeNoRegion());
            //查询是否存在机会
            List<Map> opps = new ArrayList<>();
            if("0".equals(projectRuleDetailXt.getJudgeStage())){
                opps = projectCluesDao.getCstIsOkReferral(map);
            }else if("1".equals(projectRuleDetailXt.getJudgeStage())){
                opps = projectCluesDao.getCstIsOkComeVisit(map);
            }else if("2".equals(projectRuleDetailXt.getJudgeStage())){
                opps = projectCluesDao.getCstIsOkTrade(map);
            }else {
                return ResultBody.error(-10002,"系统配置异常");
            }
            boolean flag3 = false;
            List<Map> opps3 = excelImportMapper.getCstIsOkReferralClue(map);
            for (Map m1:opps3) {
                int cout1 = Integer.parseInt(m1.get("count")+"");
                if (cout1>0){
                    flag3 = true;
                    break;
                }
            }
            if(flag3){
                notOppIds.add(customerName);
            }else {
                String type = "";
                boolean flag1 = false;
                boolean isNotOK = false;
                for (Map m:opps) {
                    int cout = Integer.parseInt(m.get("count")+"");
                    if (cout>0){
                        type = m.get("type")+"";
                        //获取重复客户 判断客户对应类型的规则 万企通走万企通配置 转介走转介保护期
                        map.put("type",type);
                        List<Map> cusOpps = projectCluesDao.getCstIsOkRepeat(map);
                        for (Map cusOpp: cusOpps) {
                            Map queryMap = new HashMap();
                            queryMap.put("projectId",cusOpp.get("projectId")+"");
                            queryMap.put("customerMobile",cusOpp.get("customerMobile")+"");
                            queryMap.put("customerName",cusOpp.get("customerName")+"");
                            String sourceMode = cusOpp.get("sourceMode")+"";
                            if("1".equals(sourceMode)){//万企通客户
                                queryMap.put("judgeNoPool",projectRuleDetailXt.getWqtJudgeNoPool());
                                queryMap.put("judgeNoRegion",projectRuleDetailXt.getWqtJudgeNoRegion());
                                //查询是否存在机会
                                List<Map> opps1 = new ArrayList<>();
                                if("0".equals(projectRuleDetailXt.getWqtJudgeStage())){
                                    opps1 = projectCluesDao.getCstIsOkReferral(queryMap);
                                }else if("1".equals(projectRuleDetailXt.getWqtJudgeStage())){
                                    opps1 = projectCluesDao.getCstIsOkComeVisit(queryMap);
                                }else if("2".equals(projectRuleDetailXt.getWqtJudgeStage())){
                                    opps1 = projectCluesDao.getCstIsOkTrade(queryMap);
                                }else {
                                    return ResultBody.error(-10002,"系统配置异常");
                                }
                                for (Map m1:opps1) {
                                    int cout1 = Integer.parseInt(m1.get("count")+"");
                                    if (cout1>0){
                                        type = m1.get("type")+"";
                                        flag1 = true;
                                        break;
                                    }
                                }
                            }else if("2".equals(sourceMode)){//转介客户
                                queryMap.put("judgeNoPool",projectRuleDetailXt.getReferralJudgeNoPool());
                                queryMap.put("judgeNoRegion",projectRuleDetailXt.getReferralJudgeNoRegion());
                                //查询是否存在机会
                                List<Map> opps2 = new ArrayList<>();
                                if("0".equals(projectRuleDetailXt.getReferralJudgeStage())){
                                    opps2 = projectCluesDao.getCstIsOkReferral(queryMap);
                                }else if("1".equals(projectRuleDetailXt.getReferralJudgeStage())){
                                    opps2 = projectCluesDao.getCstIsOkComeVisit(queryMap);
                                }else if("2".equals(projectRuleDetailXt.getReferralJudgeStage())){
                                    opps2 = projectCluesDao.getCstIsOkTrade(queryMap);
                                }else {
                                    return ResultBody.error(-10002,"系统配置异常");
                                }
                                for (Map m2:opps2) {
                                    int cout2 = Integer.parseInt(m2.get("count")+"");
                                    if (cout2>0){
                                        type = m2.get("type")+"";
                                        flag1 = true;
                                        break;
                                    }
                                }
                            }else if("3".equals(sourceMode)){//案场客户
                                flag1 = true;
                                break;
                            }
                        }
                    }
                }
                if (flag1){
                    if ("1".equals(type)){
                        if (!oldProjectId.equals(String.valueOf(proMap.get("projectId")))){
                            isNotOK = true;
                        }
                    }else if ("pro".equals(type)){
                        isNotOK = true;
                    }else if ("region".equals(type)){
                        isNotOK = true;
                    }else if ("proRelate".equals(type)){
                        isNotOK = true;
                    }else if ("regionRelate".equals(type)){
                        isNotOK = true;
                    }
                }
                if (isNotOK && !customerName.equals("散客")){
                    notOppIds.add(customerName);
                }else {
                    //生成客户id
                    String ProjectClueId = UUID.randomUUID().toString();
                    String OpportunityClueId = UUID.randomUUID().toString();
                    ReportCustomerForm reportCustomerForm = new ReportCustomerForm();
                    reportCustomerForm.setProjectClueId(ProjectClueId);
                    reportCustomerForm.setOpportunityClueId(OpportunityClueId);
                    reportCustomerForm.setCustomerName(customerName);
                    reportCustomerForm.setContacts(contacts);
                    reportCustomerForm.setCustomerMobile(customerMobile);
                    reportCustomerForm.setPosition(position);
                    reportCustomerForm.setMainProducts(mainProducts);
                    reportCustomerForm.setUserId(userMap.get("userId")+"");
                    reportCustomerForm.setEmployeeName(userMap.get("employeeName")+"");
                    reportCustomerForm.setProjectId(proMap.get("projectId")+"");
                    reportCustomerForm.setProjectName(proMap.get("projectName")+"");
                    reportCustomerForm.setOrgId(orgMap.get("orgId")+"");
                    reportCustomerForm.setOrgName(orgMap.get("orgName")+"");
                    reportCustomerForm.setCustomerType(customerType.equals("个人") ? "1" : "0");
                    reportCustomerForm.setSourceMode(sourceModeName);
                    reportCustomerForm.setDemandRemarks(demandRemarks);
                    reportCustomerForm.setClueStatus("9");
                    reportCustomerForm.setFlag("24年11月塑料展项目 模拟插入数据");
                    reportCustomerForm.setRemarks("24年11月塑料展项目 模拟插入数据");
                    //查询客户是否存在
                    String basicCustomerId = excelImportMapper.getBasicCustomerId(reportCustomerForm);
                    //保存客户
                    if (basicCustomerId==null){
                        basicCustomerId = UUID.randomUUID().toString();
                        reportCustomerForm.setCustomerUuid(basicCustomerId);
                        reportCustomerForm.setCustomerName(customerName);
                        reportCustomerForm.setCustomerMobile(customerMobile);
                        reportCustomerForm.setUserId(userMap.get("userId")+"");
                        excelImportMapper.insertCustomerBasicZs(reportCustomerForm);
                    }
                    //保存进入公客池
                    excelImportMapper.addCstToPool(reportCustomerForm);
                    //保存客户主表信息
                    excelImportMapper.insertProjectOpp(reportCustomerForm);
                    //保存详细信息
                    excelImportMapper.saveInformationZs(reportCustomerForm);
                }
            }
        }
        System.out.println("保存成功："+notOppIds+"未保存成功");
        notOppIds.stream().forEach(x->{
            System.out.println(x);
        });
        return ResultBody.success("保存成功");
    }
    /**
     * 更新临时表中客户地址信息并导入正式表
     * @param customerAddressList 客户地址信息列表
     * @return 处理结果
     */
    @ResponseBody
    @RequestMapping(value = "/updateCustomerAddress", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "更新客户地址信息并导入正式表", notes = "更新临时表中的客户地址、经纬度信息，并将数据导入正式表")
    public ResultBody updateCustomerAddress(@RequestBody List<Map<String, Object>> customerAddressList) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sUserId = SecurityUtils.getUserId();
        String sUserName = SecurityUtils.getUsername();
        
        // 记录任务开始日志
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("更新客户地址信息并导入正式表任务开始");
        sysLog.setNote("人员记录-操作人：" + sUserName + "-操作人id：" + sUserId);
        excelImportMapper.insertLogs(sysLog);
        
        // 统计信息
        int totalCount = customerAddressList.size();
        int successCount = 0;
        int errorCount = 0;
        List<Map<String, Object>> errorRecords = new ArrayList<>();
        
        try {
            // 获取临时表中的数据
        	ReportCustomerForm queryForm = new ReportCustomerForm();
            queryForm.setUserId(sUserId);
            List<ReportCustomerForm> tempCustomers = excelImportMapper.getZsdtdrClueTempList(queryForm);
            
            // 创建ID到客户对象的映射，方便快速查找
            Map<String, ReportCustomerForm> customerMap = new HashMap<>();
            for (ReportCustomerForm customer : tempCustomers) {
                customerMap.put(customer.getProjectClueId(), customer);
            }
            
            // 存储需要更新的客户列表
            List<ReportCustomerForm> updateList = new ArrayList<>();
            
            // 处理每个客户地址更新
            for (Map<String, Object> addressInfo : customerAddressList) {
                try {
                    String projectClueId = (String) addressInfo.get("projectClueId");
                    String customerAddress = (String) addressInfo.get("customerAddress");
                    String longitude = (String) addressInfo.get("longitude");
                    String latitude = (String) addressInfo.get("latitude");
                    
                    // 验证必要参数
                    if (StringUtils.isEmpty(projectClueId)) {
                        throw new IllegalArgumentException("客户ID不能为空");
                    }
                    
                    // 查找对应的客户
                    ReportCustomerForm customer = customerMap.get(projectClueId);
                    if (customer == null) {
                        throw new IllegalArgumentException("未找到对应的客户记录");
                    }
                    
                    // 直接更新地址信息，不进行校验
                    if (StringUtils.isNotEmpty(customerAddress)) {
                        customer.setCustomerAddress(customerAddress);
                    }
                    
                    if (StringUtils.isNotEmpty(longitude)) {
                        customer.setLongitude(longitude);
                    }
                    
                    if (StringUtils.isNotEmpty(latitude)) {
                        customer.setLatitude(latitude);
                    }
                    // 添加到更新列表
                    updateList.add(customer);
                    
                    // 更新成功计数
                    successCount++;
                    
                } catch (Exception e) {
                    // 处理单个客户更新过程中的异常
                    errorCount++;
                    Map<String, Object> errorRecord = new HashMap<>();
                    errorRecord.put("projectClueId", addressInfo.get("projectClueId"));
                    errorRecord.put("customerName", addressInfo.get("customerName"));
                    errorRecord.put("errors", "处理数据时发生异常: " + e.getMessage());
                    errorRecords.add(errorRecord);
                }
            }
            
            // 将更新后的数据保存到临时表
            if (!updateList.isEmpty()) {
                // 先清空临时表
                excelImportMapper.delProhectClueMarkTemp(sUserId);
                
                // 重新保存更新后的数据到临时表
                excelImportMapper.saveProhectClueMarkTemp(updateList, sUserId);
                
                // 将临时表数据导入正式表
                excelImportMapper.saveMarkTempToProjectClue(sUserId);
                
                // 清空临时表
                excelImportMapper.delProhectClueMarkTemp(sUserId);
            }
            
            // 构建结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("totalCount", totalCount);
            resultMap.put("successCount", successCount);
            resultMap.put("errorCount", errorCount);
            resultMap.put("errorRecords", errorRecords);
            
            // 记录任务完成日志
            String result = String.format("更新完成，总数: %d, 成功: %d, 失败: %d", 
                    totalCount, successCount, errorCount);
            
            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("更新客户地址信息并导入正式表任务结束");
            sysLog0.setNote(result + "-人员记录-操作人：" + sUserName + "-操作人账号：" + sUserId);
            excelImportMapper.insertLogs(sysLog0);
            
            return ResultBody.success(resultMap);
            
        } catch (Exception e) {
            e.printStackTrace();
            // 记录任务失败日志
            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("更新客户地址信息并导入正式表任务失败");
            sysLog0.setNote("更新失败-操作人：" + sUserName + "-操作人账号：" + sUserId + "-错误信息：" + e.getMessage());
            excelImportMapper.insertLogs(sysLog0);
            return ResultBody.error(-1001, "数据有误 更新失败 请检查！");
        }
    }

}
