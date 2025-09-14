package cn.visolink.system.excel.controller;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.message.model.SysLog;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.excel.service.ExcelImportService;
import cn.visolink.system.excel.util.AMapUtils;
import cn.visolink.system.job.authorization.mapper.JobMapper;
import cn.visolink.system.timetask.SyncDataTask;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
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
public class MapExcelImportController {


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

    //招商地图客户导入
    @ResponseBody
    @RequestMapping(value = "/imporMapCustomerOld",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody imporMapCustomerOld(MultipartFile file,String jobName) throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sUserId = SecurityUtils.getUserId();
        String sUserName = SecurityUtils.getUsername();
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("批量导入地图客户任务开始");
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

        // 导入统计信息
        int totalCount = 0;
        int successCount = 0;
        int tempCount = 0;  // 临时表数据计数
        int errorCount = 0;

        String jobCode = null;

        List<Map<String, Object>> errorRecords = new ArrayList<>();
        List<ReportCustomerForm> tempList = new ArrayList<>(); // 临时表数据（只有地址/经纬度问题）
        List<ReportCustomerForm> formalList = new ArrayList<>(); // 正式表数据（完全正确）

        List<String> noProName = new ArrayList<>();
        List<String> noUser = new ArrayList<>();

        // 判重Map，记录已导入的客户名称
        Map<String, Boolean> duplicateCheck = new HashMap<>();

        try {
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                totalCount++;

                // 错误信息收集
                List<String> rowErrors = new ArrayList<>();
                List<String> addressErrors = new ArrayList<>(); // 专门收集地址/经纬度相关错误
                Map<String, Object> errorRecord = new HashMap<>();

                try {
                    // 将所有单元格转为字符串类型
                    for (Cell cell : row) {
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);
                        }
                    }

                    ReportCustomerForm reportCustomerForm = new ReportCustomerForm();
                    reportCustomerForm.setSourceMode("招商地图导入");
                    Map queryMap = new HashMap<>();

                    // 获取客户姓名（必填）
                    String customerName = "";
                    String apiAddress="";
                    if (row.getCell(1) != null) {
                        customerName = row.getCell(1).getStringCellValue().trim();
                        if (StringUtils.isEmpty(customerName)) {
                            rowErrors.add("客户名称不能为空");
                        }else{
                            //apiAddress = AMapUtils.nameToAddress(customerName);
                            apiAddress = "";
                        }

                    } else {
                        rowErrors.add("客户名称不能为空");
                    }
                    reportCustomerForm.setCustomerName(customerName);


                    // 获取所属项目（必填）
                    String projectName = "";
                    if (row.getCell(10) != null) {
                        projectName = row.getCell(10).getStringCellValue().trim();
                        if (StringUtils.isEmpty(projectName)) {
                            rowErrors.add("所属项目不能为空");
                        }
                    } else {
                        rowErrors.add("所属项目不能为空");
                    }

                    String userName = "";
                    if (row.getCell(17) != null) {
                        userName = row.getCell(17).getStringCellValue().trim();
                    }
                    // 判重检查
                    String duplicateKey = userName + "_" + customerName;
                    if (duplicateCheck.containsKey(duplicateKey)) {
                        rowErrors.add("当前上传文件中存在重复客户: " + customerName);
                    } else if (StringUtils.isNotEmpty(customerName) && StringUtils.isNotEmpty(projectName)) {
                        duplicateCheck.put(duplicateKey, true);

                        // 检查数据库中是否已存在该客户
                        Map<String, Object> checkParams = new HashMap<>();
                        checkParams.put("reportUserName", userName);
                        checkParams.put("customerName", customerName);
                        checkParams.put("projectName", projectName);
                        checkParams.put("sourceMode", "招商地图导入");

                        int count = excelImportMapper.checkZsdtdrCustomerExists(checkParams);
                        if (count > 0) {
                            rowErrors.add("系统中已存在该客户: " + customerName);
                        }
                    }

                    // 获取联系人
                    String contacts = "";
                    if (row.getCell(2) != null) {
                        contacts = row.getCell(2).getStringCellValue().trim();
                    }
                    reportCustomerForm.setContacts(contacts);

                    // 获取联系方式
                    String customerMobile = "";
                    if (row.getCell(3) != null) {
                        customerMobile = row.getCell(3).getStringCellValue().trim();
                        // 增加手机号格式校验
                        if (StringUtils.isNotEmpty(customerMobile)) {
                            // 中国大陆手机号格式：1开头的11位数字
                            String mobileRegex = "^1[3-9]\\d{9}$";
                            if (!customerMobile.matches(mobileRegex)) {
                                rowErrors.add("联系方式格式不正确，请输入正确的手机号码");
                            }
                        }
                    }
                    reportCustomerForm.setCustomerMobile(customerMobile);

                    // 获取企业地址、经纬度
                    String customerAddress = "";
                    String longitude = "";
                    String latitude = "";
                    boolean hasAddressData = false;

                    // 获取企业地址
                    if (row.getCell(4) != null) {
                        customerAddress = row.getCell(4).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(customerAddress)) {
                            hasAddressData = true;
                            if (StringUtils.isNotEmpty(apiAddress) && !apiAddress.equals(customerAddress)) {
                                rowErrors.add("客户地址与API返回不一致：Excel中为[" + customerAddress + "]，API返回为[" + apiAddress + "]");
                            }
                        }
                    }
                    reportCustomerForm.setCustomerAddress(customerAddress);

                    // 获取经度
                    if (row.getCell(5) != null) {
                        longitude = row.getCell(5).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(longitude)) {
                            hasAddressData = true;
                        }
                    }
                    reportCustomerForm.setLongitude(longitude);

                    // 获取纬度
                    if (row.getCell(6) != null) {
                        latitude = row.getCell(6).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(latitude)) {
                            hasAddressData = true;
                        }
                    }
                    reportCustomerForm.setLatitude(latitude);

                    // 检查地址和经纬度
                    if (!hasAddressData) {
                        addressErrors.add("企业地址和经纬度不能同时为空");
                    } else if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
                        // 如果有地址但没有经纬度，尝试获取经纬度
                        if (StringUtils.isNotEmpty(customerAddress)) {
                            try {
                                float[] gps = AMapUtils.AddressTolongitudea(customerAddress);
                                longitude = String.valueOf(gps[0]);
                                latitude = String.valueOf(gps[1]);
                                reportCustomerForm.setLongitude(longitude);
                                reportCustomerForm.setLatitude(latitude);
                                // 成功获取经纬度，清除地址错误
                                addressErrors.clear();
                            } catch (Exception e) {
                                addressErrors.add("根据企业地址获取经纬度失败: " + e.getMessage());
                            }
                        }
                    } else if (StringUtils.isEmpty(customerAddress)) {
                        // 如果有经纬度但没有地址，尝试获取地址
                        try {
                            customerAddress = AMapUtils.longitudeToAddress(Float.parseFloat(latitude), Float.parseFloat(longitude));
                            reportCustomerForm.setCustomerAddress(customerAddress);
                            // 成功获取地址，清除地址错误
                            addressErrors.clear();
                        } catch (Exception e) {
                            addressErrors.add("根据经纬度获取企业地址失败: " + e.getMessage());
                        }
                    }

                    // 获取行业分类（必填）
                    String belongIndustriseDesc = "";
                    if (row.getCell(7) != null) {
                        belongIndustriseDesc = row.getCell(7).getStringCellValue().trim();
                        if (StringUtils.isEmpty(belongIndustriseDesc)) {
                            rowErrors.add("行业分类不能为空");
                        }
                    } else {
                        rowErrors.add("行业分类不能为空");
                    }
                    reportCustomerForm.setBelongIndustriseDesc(belongIndustriseDesc);

                    // 获取行业分类编码
                    if (StringUtils.isNotEmpty(belongIndustriseDesc)) {
                        queryMap.clear();
                        queryMap.put("dictName", belongIndustriseDesc);
                        Map belongIndustriseMap = excelImportMapper.getDictParentHyfl(queryMap);
                        if (belongIndustriseMap != null && !belongIndustriseMap.isEmpty()) {
                            reportCustomerForm.setBelongIndustrise(belongIndustriseMap.get("code") + "");
                        } else {
                            //rowErrors.add("行业分类不存在: " + belongIndustriseDesc);
                            reportCustomerForm.setBelongIndustrise("");
                        }
                    }

                    // 获取二级分类（必填）
                    String belongIndustriseChildDesc = "";
                    if (row.getCell(8) != null) {
                        belongIndustriseChildDesc = row.getCell(8).getStringCellValue().trim();
                        if (StringUtils.isEmpty(belongIndustriseChildDesc)) {
                            rowErrors.add("二级分类不能为空");
                        }
                    } else {
                        rowErrors.add("二级分类不能为空");
                    }

                    // 处理二级分类
                    if (StringUtils.isNotEmpty(belongIndustriseChildDesc)) {
                        String[] cHyzl = belongIndustriseChildDesc.split("/");
                        for (int i = 0; i < cHyzl.length; i++) {
                            if (i == 0) {
                                // 设置一级子类
                                reportCustomerForm.setBelongIndustriseTwoDesc(cHyzl[0]);
                                if (StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustrise())) {
                                    queryMap.clear();
                                    queryMap.put("dictCode", reportCustomerForm.getBelongIndustrise());
                                    queryMap.put("dictName", cHyzl[0]);
                                    Map belongIndustriseTwoMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if (belongIndustriseTwoMap != null && !belongIndustriseTwoMap.isEmpty()) {
                                        reportCustomerForm.setBelongIndustriseTwo(belongIndustriseTwoMap.get("code") + "");
                                    } else {
                                        //rowErrors.add("一级子类不存在: " + cHyzl[0]);
                                        reportCustomerForm.setBelongIndustriseTwo("");
                                    }
                                }
                            } else if (i == 1) {
                                // 设置二级子类
                                reportCustomerForm.setBelongIndustriseThreeDesc(cHyzl[1]);
                                if (StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseTwo())) {
                                    queryMap.clear();
                                    queryMap.put("dictCode", reportCustomerForm.getBelongIndustriseTwo());
                                    queryMap.put("dictName", cHyzl[1]);
                                    Map belongIndustriseThreeMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if (belongIndustriseThreeMap != null && !belongIndustriseThreeMap.isEmpty()) {
                                        reportCustomerForm.setBelongIndustriseThree(belongIndustriseThreeMap.get("code") + "");
                                    } else {
                                        //rowErrors.add("二级子类不存在: " + cHyzl[1]);
                                        reportCustomerForm.setBelongIndustriseThree("");

                                    }
                                }
                            } else if (i == 2) {
                                // 设置三级子类
                                reportCustomerForm.setBelongIndustriseFourDesc(cHyzl[2]);
                                if (StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseThree())) {
                                    queryMap.clear();
                                    queryMap.put("dictCode", reportCustomerForm.getBelongIndustriseThree());
                                    queryMap.put("dictName", cHyzl[2]);
                                    Map belongIndustriseFourMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if (belongIndustriseFourMap != null && !belongIndustriseFourMap.isEmpty()) {
                                        reportCustomerForm.setBelongIndustriseFour(belongIndustriseFourMap.get("code") + "");
                                    } else {
                                        //rowErrors.add("三级子类不存在: " + cHyzl[2]);
                                        reportCustomerForm.setBelongIndustriseFour("");
                                    }
                                }
                            }
                        }
                    }

                    // 获取主营产品（必填）
                    String mainProducts = "";
                    if (row.getCell(9) != null) {
                        mainProducts = row.getCell(9).getStringCellValue().trim();
                        if (StringUtils.isEmpty(mainProducts)) {
                            rowErrors.add("主营产品不能为空");
                        }
                    } else {
                        rowErrors.add("主营产品不能为空");
                    }
                    reportCustomerForm.setMainProducts(mainProducts);

                    // 获取所属项目（必填）--提前处理客户判重用
                    if (row.getCell(10) != null) {
                        projectName = row.getCell(10).getStringCellValue().trim();
                        if (StringUtils.isEmpty(projectName)) {
                            rowErrors.add("所属项目不能为空");
                        }
                    } else {
                        rowErrors.add("所属项目不能为空");
                    }

                    // 设置所属项目
                    if (StringUtils.isNotEmpty(projectName)) {
                        queryMap.clear();
                        queryMap.put("projectName", projectName);
                        Map pMap = excelImportMapper.getProInfo(queryMap);
                        if (pMap != null && !pMap.isEmpty()) {
                            reportCustomerForm.setProjectName(pMap.get("projectName") + "");
                            reportCustomerForm.setProjectId(pMap.get("projectId") + "");
                        } else {
                            rowErrors.add("所属项目不存在: " + projectName);
                            noProName.add(projectName);
                        }
                    }

                    // 获取是否园区
                    String isPark = "";
                    if (row.getCell(11) != null) {
                        isPark = row.getCell(11).getStringCellValue().trim();
                    }

                    if ("是".equals(isPark)) {
                        reportCustomerForm.setIsPark("1");

                        // 获取园区信息
                        String parkAddress = "";
                        if (row.getCell(12) != null) {
                            parkAddress = row.getCell(12).getStringCellValue().trim();
                            if (StringUtils.isEmpty(parkAddress)) {
                                rowErrors.add("园区地址不能为空");
                            }
                        } else {
                            rowErrors.add("园区地址不能为空");
                        }
                        reportCustomerForm.setParkAddress(parkAddress);

                        if (StringUtils.isNotEmpty(parkAddress)) {
                            String[] parkAddressStr = addressCutting(parkAddress).split("-");
                            // 根据园区地址获取行政区信息
                            if (parkAddressStr.length > 0) {
                                reportCustomerForm.setParkProvince(parkAddressStr[0]);
                            }
                            if (parkAddressStr.length > 1) {
                                reportCustomerForm.setParkCity(parkAddressStr[1]);
                            }
                            if (parkAddressStr.length > 2) {
                                reportCustomerForm.setParkCounty(parkAddressStr[2]);
                            }
                            if (parkAddressStr.length > 3) {
                                reportCustomerForm.setParkStreet(parkAddressStr[3]);
                            }
                        }

                        // 获取园区层数
                        String parkFloor = "";
                        if (row.getCell(13) != null) {
                            parkFloor = row.getCell(13).getStringCellValue().trim();
                        }
                        reportCustomerForm.setParkFloor(parkFloor);

                        // 获取园区名称
                        String parkName = "";
                        if (row.getCell(14) != null) {
                            parkName = row.getCell(14).getStringCellValue().trim();
                            if (StringUtils.isEmpty(parkName)) {
                                rowErrors.add("园区名称不能为空");
                            }
                        } else {
                            rowErrors.add("园区名称不能为空");
                        }
                        reportCustomerForm.setParkName(parkName);
                    } else {
                        reportCustomerForm.setIsPark("0");
                    }

                    // 获取标签
                    String label = "";
                    if (row.getCell(15) != null) {
                        label = row.getCell(15).getStringCellValue().trim();
                    }
                    reportCustomerForm.setLabel(label);

                    // 获取录入时间
                    String createDate = "";
                    // 获取录入时间
                    Cell createDateCell = row.getCell(16);

                    if (createDateCell != null) {
                        // 处理单元格类型
                        switch (createDateCell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                String cellValue = createDateCell.getStringCellValue().trim();
                                if (!StringUtils.isEmpty(cellValue)) {
                                    try {
                                        // 尝试将字符串解析为数字（Excel日期数值）
                                        double excelDateNum = Double.parseDouble(cellValue);
                                        createDate = convertExcelDateToStr(excelDateNum, sf);
                                    } catch (NumberFormatException e) {
                                        // 如果不是数字格式，直接使用字符串作为日期（可能是用户手动输入的日期字符串）
                                        createDate = cellValue;
                                    }
                                } else {
                                    createDate = sf.format(new Date()); // 空字符串，使用当前日期
                                }
                                break;

                            case Cell.CELL_TYPE_NUMERIC:
                                // 直接处理数值类型的日期
                                double excelDateNum = createDateCell.getNumericCellValue();
                                createDate = convertExcelDateToStr(excelDateNum, sf);
                                break;

                            default:
                                // 其他类型的单元格，使用当前日期
                                createDate = sf.format(new Date());
                        }
                    } else {
                        // 单元格为空，使用当前日期
                        createDate = sf.format(new Date());
                    }

                    reportCustomerForm.setCreateDate(createDate);

                    // 获取录入人
                    if (row.getCell(17) != null) {
                        userName = row.getCell(17).getStringCellValue().trim();
                        if (StringUtils.isEmpty(userName)) {
                            rowErrors.add("录入人不能为空");
                        }
                    } else {
                        rowErrors.add("录入人不能为空");
                    }

                    // 获取录入人身份
                    String reportUserRole = "";
                    if (row.getCell(18) != null) {
                        reportUserRole = row.getCell(18).getStringCellValue().trim();
                        if (StringUtils.isEmpty(reportUserRole)) {
                            rowErrors.add("录入人身份不能为空");
                        }
                    } else {
                        rowErrors.add("录入人身份不能为空");
                    }
                    // 设置录入人身份
                    //项目招商经理、项目招商总监、项目营销经理、区域招商经理、区域招商总监、区域营销经理
                    if (StringUtils.isNotEmpty(reportUserRole)) {
                        if ("项目招商经理".equals(reportUserRole)) {
                            jobCode = "xsjl";
                            reportCustomerForm.setReportUserRole("5");
                        } else if ("项目招商总监".equals(reportUserRole)) {
                            jobCode = "zszj";
                            reportCustomerForm.setReportUserRole("6");
                        } else if ("项目营销经理".equals(reportUserRole)) {
                            jobCode = "yxjl";
                            reportCustomerForm.setReportUserRole("7");
                        }else if ("区域招商经理".equals(reportUserRole)) {
                            jobCode = "qyxsjl";
                            reportCustomerForm.setReportUserRole("8");
                        }else if ("区域招商总监".equals(reportUserRole)) {
                            jobCode = "qyzszj";
                            reportCustomerForm.setReportUserRole("9");
                        }else if ("区域营销经理".equals(reportUserRole)) {
                            jobCode = "qyyxjl";
                            reportCustomerForm.setReportUserRole("10");
                        }else {
                            rowErrors.add("录入人身份不正确，只能是'项目招商经理'、'项目招商总监'、'项目营销经理'、'区域招商经理'、'区域招商总监'、'区域营销经理'");
                        }
                    }

                    // 设置报备人和案场归属人
                    if (StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(reportCustomerForm.getProjectId()) && StringUtils.isNotEmpty(jobCode)) {
                        queryMap.clear();
                        queryMap.put("projectId", reportCustomerForm.getProjectId());
                        queryMap.put("jobCode", jobCode);
                        queryMap.put("userName", userName);
                        Map uMap = excelImportMapper.getUserOrgInfo(queryMap);
                        if (uMap != null && !uMap.isEmpty()) {
                            reportCustomerForm.setEmployeeName(uMap.get("userName") + "");
                            reportCustomerForm.setUserId(uMap.get("userId") + "");
                            reportCustomerForm.setOrgId(uMap.get("orgId") + "");
                            reportCustomerForm.setOrgName(uMap.get("orgName") + "");
                        } else {
                            rowErrors.add("未找到录入人信息: " + userName);
                            noUser.add(userName);
                        }
                    }

                  // 以下是需要新增的导入列
                  //  备注	客户来源佐证	租售类型	现有办公面积	现有租金元/㎡	厂房类型	所处楼层	年产值	污染物排放	意向类型	意向面积㎡	客户难点及疑虑
                    // 如果没有错误，添加到导入列表
                    if (rowErrors.isEmpty() && addressErrors.isEmpty()){
                        // 设置线索信息
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        reportCustomerForm.setFollowUpWay("导入");

                        reportCustomerForm.setFollowUpDetail(jobName+"-批量导入地图客户");
                        // 设置标注信息
                        reportCustomerForm.setDimensionType("3");
                        // 设置备注信息 记录导入人员
                        reportCustomerForm.setFlag(sUserName + "-批量导入地图客户" + "-账号记录：" + sUserId);

                        formalList.add(reportCustomerForm);
                        successCount++;
                    } else if (rowErrors.isEmpty() && !addressErrors.isEmpty()) {
                        // 只有地址/经纬度问题的数据，加入临时表列表
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        reportCustomerForm.setFollowUpWay("导入");


                        reportCustomerForm.setFollowUpDetail(jobName+"-批量导入地图客户");
                        reportCustomerForm.setDimensionType("3");
                        reportCustomerForm.setFlag(sUserName + "-批量导入地图客户(待完善地址)" + "-账号记录：" + sUserId);

                        tempList.add(reportCustomerForm);
                        tempCount++;

                        // 记录错误信息
                        errorRecord.put("rowNum", r);
                        errorRecord.put("projectClueId", ProjectClueId);
                        errorRecord.put("customerName", customerName);
                        errorRecord.put("errors", String.join("; ", addressErrors));
                        errorRecord.put("type", "address"); // 标记为地址错误
                        errorRecords.add(errorRecord);
                    } else {
                        // 其他错误，只记录错误信息
                        errorCount++;
                        errorRecord.put("rowNum", r);
                        errorRecord.put("projectClueId", "");
                        errorRecord.put("customerName", customerName);
                        // 合并所有错误信息
                        List<String> allErrors = new ArrayList<>(rowErrors);
                        allErrors.addAll(addressErrors);
                        errorRecord.put("errors", String.join("; ", allErrors));
                        errorRecord.put("type", "other"); // 标记为其他错误
                        errorRecords.add(errorRecord);
                    }
                } catch (Exception e) {
                    // 处理行处理过程中的异常
                    errorCount++;
                    errorRecord.put("rowNum", r);
                    errorRecord.put("customerName", row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "未知");
                    errorRecord.put("errors", "处理数据时发生异常: " + e.getMessage());
                    errorRecord.put("type", "exception"); // 标记为异常错误
                    errorRecords.add(errorRecord);
                }
            }

            // 保存数据
            if (!formalList.isEmpty()) {

                // 先清空临时表
                excelImportMapper.delProhectClueMarkTemp(sUserId);

                // 重新保存更新后的数据到临时表
                excelImportMapper.saveProhectClueMarkTemp(formalList, sUserId);

                // 将临时表数据导入正式表
                excelImportMapper.saveMarkTempToProjectClueA(sUserId);
                excelImportMapper.saveMarkTempToProjectClueB(sUserId);
                excelImportMapper.saveMarkTempToProjectClueC(sUserId);
                excelImportMapper.saveMarkTempToProjectClueD(sUserId);

                // 清空临时表
                excelImportMapper.delProhectClueMarkTemp(sUserId);

            }

            if (!tempList.isEmpty()) {
                // 清空临时表
                excelImportMapper.delProhectClueMarkTemp(sUserId);
                // 保存到临时表
                excelImportMapper.saveProhectClueMarkTemp(tempList, sUserId);
            }



            // 构建导入结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("totalCount", totalCount);
            resultMap.put("successCount", successCount);
            resultMap.put("tempCount", tempCount);
            resultMap.put("errorCount", errorCount);
            resultMap.put("errorRecords", errorRecords);

            // 记录导入结果日志
            String result = String.format("导入完成，总数: %d, 成功导入正式表: %d, 导入临时表: %d, 失败: %d",
                    totalCount, successCount, tempCount, errorCount);

            if (noProName.size() > 0) {
                String pro = ", 以下项目未查询到：" + StringUtils.join(noProName, ",");
                result = result + pro;
            }
            if (noUser.size() > 0) {
                String pro = ", 以下人员未查询到：" + StringUtils.join(noUser, ",");
                result = result + pro;
            }

            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("批量导入地图客户任务结束");
            sysLog0.setNote(result + "-人员记录-操作人：" + sUserName + "-操作人账号：" + sUserId);
            excelImportMapper.insertLogs(sysLog0);

            return ResultBody.success(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            // 记录导入失败日志
            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("批量导入地图客户任务失败");
            sysLog0.setNote("导入失败-操作人：" + sUserName + "-操作人账号：" + sUserId + "-错误信息：" + e.getMessage());
            excelImportMapper.insertLogs(sysLog0);
            return ResultBody.error(-1001, "数据有误 导入失败 请检查！");
        }
    }


    //招商地图客户导入历史数据
    @ResponseBody
    @RequestMapping(value = "/imporMapCustomerHistoryOld",method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody imporMapCustomerHistoryOld(MultipartFile file,String jobName) throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sUserId = SecurityUtils.getUserId();
        String sUserName = SecurityUtils.getUsername();
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("批量导入地图客户历史数据任务开始");
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

        // 导入统计信息
        int totalCount = 0;
        int successCount = 0;
        int tempCount = 0;  // 临时表数据计数
        int errorCount = 0;

        String jobCode = null;

        List<Map<String, Object>> errorRecords = new ArrayList<>();
        List<ReportCustomerForm> tempList = new ArrayList<>(); // 临时表数据（只有地址/经纬度问题）
        List<ReportCustomerForm> formalList = new ArrayList<>(); // 正式表数据（完全正确）

        List<String> noProName = new ArrayList<>();
        List<String> noUser = new ArrayList<>();

        // 判重Map，记录已导入的客户名称
        Map<String, Boolean> duplicateCheck = new HashMap<>();

        try {
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                totalCount++;

                // 错误信息收集
                List<String> rowErrors = new ArrayList<>();
                List<String> addressErrors = new ArrayList<>(); // 专门收集地址/经纬度相关错误
                Map<String, Object> errorRecord = new HashMap<>();

                try {
                    // 将所有单元格转为字符串类型
                    for (Cell cell : row) {
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);
                        }
                    }

                    ReportCustomerForm reportCustomerForm = new ReportCustomerForm();
                    reportCustomerForm.setSourceMode("招商地图导入-历史数据");
                    Map queryMap = new HashMap<>();

                    // 获取客户姓名（必填）
                    String customerName = "";
                    String apiAddress = "";
                    if (row.getCell(1) != null) {
                        customerName = row.getCell(1).getStringCellValue().trim();
                        if (StringUtils.isEmpty(customerName)) {
                            rowErrors.add("客户名称不能为空");
                        }else{
                             //apiAddress = AMapUtils.nameToAddress(customerName);
                               apiAddress ="";
                        }

                    } else {
                        rowErrors.add("客户名称不能为空");
                    }
                    reportCustomerForm.setCustomerName(customerName);



                    // 获取所属项目（必填）
                    String projectName = "";
                    if (row.getCell(10) != null) {
                        projectName = row.getCell(10).getStringCellValue().trim();
                        if (StringUtils.isEmpty(projectName)) {
                            rowErrors.add("所属项目不能为空");
                        }
                    } else {
                        rowErrors.add("所属项目不能为空");
                    }

                    String userName = "";
                    if (row.getCell(17) != null) {
                        userName = row.getCell(17).getStringCellValue().trim();
                    }
                    // 判重检查
                    String duplicateKey = userName + "_" + customerName;
                    if (duplicateCheck.containsKey(duplicateKey)) {
                        rowErrors.add("当前上传文件中存在重复客户: " + customerName);
                    } else if (StringUtils.isNotEmpty(customerName) && StringUtils.isNotEmpty(projectName)) {
                        duplicateCheck.put(duplicateKey, true);

                        // 检查数据库中是否已存在该客户
                        Map<String, Object> checkParams = new HashMap<>();
                        checkParams.put("reportUserName", userName);
                        checkParams.put("customerName", customerName);
                        checkParams.put("projectName", projectName);
                        checkParams.put("sourceMode", "招商地图导入-历史数据");

                        int count = excelImportMapper.checkZsdtdrCustomerExists(checkParams);
                        if (count > 0) {
                            rowErrors.add("系统中已存在该客户: " + customerName);
                        }
                    }

                    // 获取联系人
                    String contacts = "";
                    if (row.getCell(2) != null) {
                        contacts = row.getCell(2).getStringCellValue().trim();
                    }
                    reportCustomerForm.setContacts(contacts);

                    // 获取联系方式
                    String customerMobile = "";
                    if (row.getCell(3) != null) {
                        customerMobile = row.getCell(3).getStringCellValue().trim();
                        // 增加手机号格式校验
                        if (StringUtils.isNotEmpty(customerMobile)) {
                            // 中国大陆手机号格式：1开头的11位数字
                            String mobileRegex = "^1[3-9]\\d{9}$";
                            if (!customerMobile.matches(mobileRegex)) {
                                rowErrors.add("联系方式格式不正确，请输入正确的手机号码");
                            }
                        }
                    }
                    reportCustomerForm.setCustomerMobile(customerMobile);

                    // 获取企业地址、经纬度
                    String customerAddress = "";
                    String longitude = "";
                    String latitude = "";
                    boolean hasAddressData = false;

                    // 获取企业地址
                    if (row.getCell(4) != null) {
                        customerAddress = row.getCell(4).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(customerAddress)) {
                            hasAddressData = true;
                            if (StringUtils.isNotEmpty(apiAddress) && !apiAddress.equals(customerAddress)) {
                                rowErrors.add("客户地址与API返回不一致：Excel中为[" + customerAddress + "]，API返回为[" + apiAddress + "]");
                            }
                        }
                    }
                    reportCustomerForm.setCustomerAddress(customerAddress);

                    // 获取经度
                    if (row.getCell(5) != null) {
                        longitude = row.getCell(5).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(longitude)) {
                            hasAddressData = true;
                        }
                    }
                    reportCustomerForm.setLongitude(longitude);

                    // 获取纬度
                    if (row.getCell(6) != null) {
                        latitude = row.getCell(6).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(latitude)) {
                            hasAddressData = true;
                        }
                    }
                    reportCustomerForm.setLatitude(latitude);

                    // 检查地址和经纬度
                    if (!hasAddressData) {
                        addressErrors.add("企业地址和经纬度不能同时为空");
                    } else if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
                        // 如果有地址但没有经纬度，尝试获取经纬度
                        if (StringUtils.isNotEmpty(customerAddress)) {
                            try {
                                float[] gps = AMapUtils.AddressTolongitudea(customerAddress);
                                longitude = String.valueOf(gps[0]);
                                latitude = String.valueOf(gps[1]);
                                reportCustomerForm.setLongitude(longitude);
                                reportCustomerForm.setLatitude(latitude);
                                // 成功获取经纬度，清除地址错误
                                addressErrors.clear();
                            } catch (Exception e) {
                                addressErrors.add("根据企业地址获取经纬度失败: " + e.getMessage());
                            }
                        }
                    } else if (StringUtils.isEmpty(customerAddress)) {
                        // 如果有经纬度但没有地址，尝试获取地址
                        try {
                            customerAddress = AMapUtils.longitudeToAddress(Float.parseFloat(latitude), Float.parseFloat(longitude));
                            reportCustomerForm.setCustomerAddress(customerAddress);
                            // 成功获取地址，清除地址错误
                            addressErrors.clear();
                        } catch (Exception e) {
                            addressErrors.add("根据经纬度获取企业地址失败: " + e.getMessage());
                        }
                    }

                    // 获取行业分类（必填）
                    String belongIndustriseDesc = "";
                    if (row.getCell(7) != null) {
                        belongIndustriseDesc = row.getCell(7).getStringCellValue().trim();
                        if (StringUtils.isEmpty(belongIndustriseDesc)) {
                            rowErrors.add("行业分类不能为空");
                        }
                    } else {
                        rowErrors.add("行业分类不能为空");
                    }
                    reportCustomerForm.setBelongIndustriseDesc(belongIndustriseDesc);

                    // 获取行业分类编码
                    if (StringUtils.isNotEmpty(belongIndustriseDesc)) {
                        queryMap.clear();
                        queryMap.put("dictName", belongIndustriseDesc);
                        Map belongIndustriseMap = excelImportMapper.getDictParentHyfl(queryMap);
                        if (belongIndustriseMap != null && !belongIndustriseMap.isEmpty()) {
                            reportCustomerForm.setBelongIndustrise(belongIndustriseMap.get("code") + "");
                        } else {
                            //rowErrors.add("行业分类不存在: " + belongIndustriseDesc);
                            reportCustomerForm.setBelongIndustrise("");
                        }
                    }

                    // 获取二级分类（必填）
                    String belongIndustriseChildDesc = "";
                    if (row.getCell(8) != null) {
                        belongIndustriseChildDesc = row.getCell(8).getStringCellValue().trim();
                        if (StringUtils.isEmpty(belongIndustriseChildDesc)) {
                            rowErrors.add("二级分类不能为空");
                        }
                    } else {
                        rowErrors.add("二级分类不能为空");
                    }

                    // 处理二级分类
                    if (StringUtils.isNotEmpty(belongIndustriseChildDesc)) {
                        String[] cHyzl = belongIndustriseChildDesc.split("/");
                        for (int i = 0; i < cHyzl.length; i++) {
                            if (i == 0) {
                                // 设置一级子类
                                reportCustomerForm.setBelongIndustriseTwoDesc(cHyzl[0]);
                                if (StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustrise())) {
                                    queryMap.clear();
                                    queryMap.put("dictCode", reportCustomerForm.getBelongIndustrise());
                                    queryMap.put("dictName", cHyzl[0]);
                                    Map belongIndustriseTwoMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if (belongIndustriseTwoMap != null && !belongIndustriseTwoMap.isEmpty()) {
                                        reportCustomerForm.setBelongIndustriseTwo(belongIndustriseTwoMap.get("code") + "");
                                    } else {
                                        //rowErrors.add("一级子类不存在: " + cHyzl[0]);
                                        reportCustomerForm.setBelongIndustriseTwo("");
                                    }
                                }
                            } else if (i == 1) {
                                // 设置二级子类
                                reportCustomerForm.setBelongIndustriseThreeDesc(cHyzl[1]);
                                if (StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseTwo())) {
                                    queryMap.clear();
                                    queryMap.put("dictCode", reportCustomerForm.getBelongIndustriseTwo());
                                    queryMap.put("dictName", cHyzl[1]);
                                    Map belongIndustriseThreeMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if (belongIndustriseThreeMap != null && !belongIndustriseThreeMap.isEmpty()) {
                                        reportCustomerForm.setBelongIndustriseThree(belongIndustriseThreeMap.get("code") + "");
                                    } else {
                                        //rowErrors.add("二级子类不存在: " + cHyzl[1]);
                                        reportCustomerForm.setBelongIndustriseThree("");

                                    }
                                }
                            } else if (i == 2) {
                                // 设置三级子类
                                reportCustomerForm.setBelongIndustriseFourDesc(cHyzl[2]);
                                if (StringUtils.isNotEmpty(reportCustomerForm.getBelongIndustriseThree())) {
                                    queryMap.clear();
                                    queryMap.put("dictCode", reportCustomerForm.getBelongIndustriseThree());
                                    queryMap.put("dictName", cHyzl[2]);
                                    Map belongIndustriseFourMap = excelImportMapper.getDictChildHtzl(queryMap);
                                    if (belongIndustriseFourMap != null && !belongIndustriseFourMap.isEmpty()) {
                                        reportCustomerForm.setBelongIndustriseFour(belongIndustriseFourMap.get("code") + "");
                                    } else {
                                        //rowErrors.add("三级子类不存在: " + cHyzl[2]);
                                        reportCustomerForm.setBelongIndustriseFour("");
                                    }
                                }
                            }
                        }
                    }

                    // 获取主营产品（必填）
                    String mainProducts = "";
                    if (row.getCell(9) != null) {
                        mainProducts = row.getCell(9).getStringCellValue().trim();
                        if (StringUtils.isEmpty(mainProducts)) {
                            rowErrors.add("主营产品不能为空");
                        }
                    } else {
                        rowErrors.add("主营产品不能为空");
                    }
                    reportCustomerForm.setMainProducts(mainProducts);

                    // 获取所属项目（必填）--提前处理客户判重用
                    if (row.getCell(10) != null) {
                        projectName = row.getCell(10).getStringCellValue().trim();
                        if (StringUtils.isEmpty(projectName)) {
                            rowErrors.add("所属项目不能为空");
                        }
                    } else {
                        rowErrors.add("所属项目不能为空");
                    }

                    // 设置所属项目
                    if (StringUtils.isNotEmpty(projectName)) {
                        queryMap.clear();
                        queryMap.put("projectName", projectName);
                        Map pMap = excelImportMapper.getProInfo(queryMap);
                        if (pMap != null && !pMap.isEmpty()) {
                            reportCustomerForm.setProjectName(pMap.get("projectName") + "");
                            reportCustomerForm.setProjectId(pMap.get("projectId") + "");
                        } else {
                            rowErrors.add("所属项目不存在: " + projectName);
                            noProName.add(projectName);
                        }
                    }

                    // 获取是否园区
                    String isPark = "";
                    if (row.getCell(11) != null) {
                        isPark = row.getCell(11).getStringCellValue().trim();
                    }

                    if ("是".equals(isPark)) {
                        reportCustomerForm.setIsPark("1");

                        // 获取园区信息
                        String parkAddress = "";
                        if (row.getCell(12) != null) {
                            parkAddress = row.getCell(12).getStringCellValue().trim();
                            if (StringUtils.isEmpty(parkAddress)) {
                                rowErrors.add("园区地址不能为空");
                            }
                        } else {
                            rowErrors.add("园区地址不能为空");
                        }
                        reportCustomerForm.setParkAddress(parkAddress);

                        if (StringUtils.isNotEmpty(parkAddress)) {
                            String[] parkAddressStr = addressCutting(parkAddress).split("-");
                            // 根据园区地址获取行政区信息
                            if (parkAddressStr.length > 0) {
                                reportCustomerForm.setParkProvince(parkAddressStr[0]);
                            }
                            if (parkAddressStr.length > 1) {
                                reportCustomerForm.setParkCity(parkAddressStr[1]);
                            }
                            if (parkAddressStr.length > 2) {
                                reportCustomerForm.setParkCounty(parkAddressStr[2]);
                            }
                            if (parkAddressStr.length > 3) {
                                reportCustomerForm.setParkStreet(parkAddressStr[3]);
                            }
                        }

                        // 获取园区层数
                        String parkFloor = "";
                        if (row.getCell(13) != null) {
                            parkFloor = row.getCell(13).getStringCellValue().trim();
                        }
                        reportCustomerForm.setParkFloor(parkFloor);

                        // 获取园区名称
                        String parkName = "";
                        if (row.getCell(14) != null) {
                            parkName = row.getCell(14).getStringCellValue().trim();
                            if (StringUtils.isEmpty(parkName)) {
                                rowErrors.add("园区名称不能为空");
                            }
                        } else {
                            rowErrors.add("园区名称不能为空");
                        }
                        reportCustomerForm.setParkName(parkName);
                    } else {
                        reportCustomerForm.setIsPark("0");
                    }

                    // 获取标签
                    String label = "";
                    if (row.getCell(15) != null) {
                        label = row.getCell(15).getStringCellValue().trim();
                    }
                    reportCustomerForm.setLabel(label);

                    // 获取录入时间
                    String createDate = "";
                    // 获取录入时间
                    Cell createDateCell = row.getCell(16);

                    if (createDateCell != null) {
                        // 处理单元格类型
                        switch (createDateCell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                String cellValue = createDateCell.getStringCellValue().trim();
                                if (!StringUtils.isEmpty(cellValue)) {
                                    try {
                                        // 尝试将字符串解析为数字（Excel日期数值）
                                        double excelDateNum = Double.parseDouble(cellValue);
                                        createDate = convertExcelDateToStr(excelDateNum, sf);
                                    } catch (NumberFormatException e) {
                                        // 如果不是数字格式，直接使用字符串作为日期（可能是用户手动输入的日期字符串）
                                        createDate = cellValue;
                                    }
                                } else {
                                    createDate = sf.format(new Date()); // 空字符串，使用当前日期
                                }
                                break;

                            case Cell.CELL_TYPE_NUMERIC:
                                // 直接处理数值类型的日期
                                double excelDateNum = createDateCell.getNumericCellValue();
                                createDate = convertExcelDateToStr(excelDateNum, sf);
                                break;

                            default:
                                // 其他类型的单元格，使用当前日期
                                createDate = sf.format(new Date());
                        }
                    } else {
                        // 单元格为空，使用当前日期
                        createDate = sf.format(new Date());
                    }

                    reportCustomerForm.setCreateDate(createDate);

                    // 获取录入人
                    if (row.getCell(17) != null) {
                        userName = row.getCell(17).getStringCellValue().trim();
                        if (StringUtils.isEmpty(userName)) {
                            rowErrors.add("录入人不能为空");
                        }
                    } else {
                        rowErrors.add("录入人不能为空");
                    }

                    // 获取录入人身份
                    String reportUserRole = "";
                    if (row.getCell(18) != null) {
                        reportUserRole = row.getCell(18).getStringCellValue().trim();
                        if (StringUtils.isEmpty(reportUserRole)) {
                            rowErrors.add("录入人身份不能为空");
                        }
                    } else {
                        rowErrors.add("录入人身份不能为空");
                    }

                    // 处理新增的导入列（从第19列开始）
                    // 定义列索引变量，从19开始（0-based索引）
                    int columnIndex = 19;

                    // 处理备注字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        reportCustomerForm.setRemarks(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理客户来源佐证字段（文件路径，多条路径用逗号分隔）
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        String sourceEvidencePaths = row.getCell(columnIndex).getStringCellValue().trim();
                        // 保存到reportCustomerForm的enclosures属性中
                        List<String> enclosuresList = Arrays.asList(sourceEvidencePaths.split(","));
                        reportCustomerForm.setEnclosures(enclosuresList);
                    }
                    columnIndex++;

                    // 处理租售类型字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        //reportCustomerForm.setRentAndSaleType(row.getCell(columnIndex).getStringCellValue().trim());
                        reportCustomerForm.setRentAndSaleTypeDesc(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理现有办公面积字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        reportCustomerForm.setNowOfficeSpace(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理现有租金元/㎡字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        reportCustomerForm.setNowRent(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理厂房类型字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        //reportCustomerForm.setPlantType(row.getCell(columnIndex).getStringCellValue().trim());
                        reportCustomerForm.setPlantTypeDesc(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理所处楼层字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        reportCustomerForm.setFloor(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理年产值字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        reportCustomerForm.setAnnualOutputValue(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理污染物排放字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        //reportCustomerForm.setPollutantDischarge(row.getCell(columnIndex).getStringCellValue().trim());
                        reportCustomerForm.setPollutantDischargeDesc(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理意向类型字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        //reportCustomerForm.setIntentionType(row.getCell(columnIndex).getStringCellValue().trim());
                        reportCustomerForm.setIntentionTypeDesc(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理意向面积㎡字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        reportCustomerForm.setIntentionalAreaDesc(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理客户难点及疑虑字段
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        reportCustomerForm.setResistanceDesc(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 处理客户阶段-历史数据有
                    if (row.getCell(columnIndex) != null && StringUtils.isNotBlank(row.getCell(columnIndex).getStringCellValue())) {
                        //客户阶段保存进跟进详情中
                        reportCustomerForm.setFollowUpDetail(row.getCell(columnIndex).getStringCellValue().trim());
                    }
                    columnIndex++;

                    // 设置录入人身份
                    //项目招商经理、项目招商总监、项目营销经理、区域招商经理、区域招商总监、区域营销经理
                    if (StringUtils.isNotEmpty(reportUserRole)) {
                        if ("项目招商经理".equals(reportUserRole)) {
                            jobCode = "xsjl";
                            reportCustomerForm.setReportUserRole("5");
                        } else if ("项目招商总监".equals(reportUserRole)) {
                            jobCode = "zszj";
                            reportCustomerForm.setReportUserRole("6");
                        } else if ("项目营销经理".equals(reportUserRole)) {
                            jobCode = "yxjl";
                            reportCustomerForm.setReportUserRole("7");
                        }else if ("区域招商经理".equals(reportUserRole)) {
                            jobCode = "qyxsjl";
                            reportCustomerForm.setReportUserRole("8");
                        }else if ("区域招商总监".equals(reportUserRole)) {
                            jobCode = "qyzszj";
                            reportCustomerForm.setReportUserRole("9");
                        }else if ("区域营销经理".equals(reportUserRole)) {
                            jobCode = "qyyxjl";
                            reportCustomerForm.setReportUserRole("10");
                        }else {
                            rowErrors.add("录入人身份不正确，只能是'项目招商经理'、'项目招商总监'、'项目招商总监'、'项目招商总监'、'项目招商总监'、'项目招商总监'");
                        }
                    }

                    // 设置报备人和案场归属人
                    if (StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(reportCustomerForm.getProjectId()) && StringUtils.isNotEmpty(jobCode)) {
                        queryMap.clear();
                        queryMap.put("projectId", reportCustomerForm.getProjectId());
                        queryMap.put("jobCode", jobCode);
                        queryMap.put("userName", userName);
                        Map uMap = excelImportMapper.getUserOrgInfo(queryMap);
                        if (uMap != null && !uMap.isEmpty()) {
                            reportCustomerForm.setEmployeeName(uMap.get("userName") + "");
                            reportCustomerForm.setUserId(uMap.get("userId") + "");
                            reportCustomerForm.setOrgId(uMap.get("orgId") + "");
                            reportCustomerForm.setOrgName(uMap.get("orgName") + "");
                        } else {
                            rowErrors.add("未找到录入人信息: " + userName);
                            noUser.add(userName);
                        }
                    }

                    // 以下是需要新增的导入列
                    //  备注	客户来源佐证	租售类型	现有办公面积	现有租金元/㎡	厂房类型	所处楼层	年产值	污染物排放	意向类型	意向面积㎡	客户难点及疑虑
                    // 如果没有错误，添加到导入列表
                    if (rowErrors.isEmpty() && addressErrors.isEmpty()){
                        // 设置线索信息
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        reportCustomerForm.setFollowUpWay("导入历史数据");

                        reportCustomerForm.setFollowUpDetail(jobName+"-批量导入地图客户历史数据-客户阶段-"+reportCustomerForm.getFollowUpDetail());
                        // 设置标注信息
                        reportCustomerForm.setDimensionType("3");
                        // 设置备注信息 记录导入人员
                        reportCustomerForm.setFlag(sUserName + "-批量导入地图客户" + "-账号记录：" + sUserId);

                        formalList.add(reportCustomerForm);
                        successCount++;
                    } else if (rowErrors.isEmpty() && !addressErrors.isEmpty()) {
                        // 只有地址/经纬度问题的数据，加入临时表列表
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        reportCustomerForm.setFollowUpWay("导入");


                        reportCustomerForm.setFollowUpDetail(jobName+"-批量导入地图客户-客户阶段-"+reportCustomerForm.getFollowUpDetail());
                        reportCustomerForm.setDimensionType("3");
                        reportCustomerForm.setFlag(sUserName + "-批量导入地图客户(待完善地址)" + "-账号记录：" + sUserId);

                        tempList.add(reportCustomerForm);
                        tempCount++;

                        // 记录错误信息
                        errorRecord.put("rowNum", r);
                        errorRecord.put("projectClueId", ProjectClueId);
                        errorRecord.put("customerName", customerName);
                        errorRecord.put("errors", String.join("; ", addressErrors));
                        errorRecord.put("type", "address"); // 标记为地址错误
                        errorRecords.add(errorRecord);
                    } else {
                        // 其他错误，只记录错误信息
                        errorCount++;
                        errorRecord.put("rowNum", r);
                        errorRecord.put("projectClueId", "");
                        errorRecord.put("customerName", customerName);
                        // 合并所有错误信息
                        List<String> allErrors = new ArrayList<>(rowErrors);
                        allErrors.addAll(addressErrors);
                        errorRecord.put("errors", String.join("; ", allErrors));
                        errorRecord.put("type", "other"); // 标记为其他错误
                        errorRecords.add(errorRecord);
                    }

                    // 保存客户信息后，处理附件信息
                    if (reportCustomerForm.getEnclosures() != null && !reportCustomerForm.getEnclosures().isEmpty()) {
                        Map<String, Object> enclosureParams = new HashMap<>();
                        enclosureParams.put("ProjectClueId", reportCustomerForm.getProjectClueId());
                        enclosureParams.put("OpportunityClueId", reportCustomerForm.getOpportunityClueId());
                        enclosureParams.put("FollowRecordId", null);
                        enclosureParams.put("enclosureType", 1); // 附件类型为线索
                        enclosureParams.put("followType", 1); // 普通附件
                        enclosureParams.put("obtainCstApproveId", null);
                        enclosureParams.put("list", reportCustomerForm.getEnclosures());

                        // 调用addCluesEnclosures方法保存附件信息
                        projectCluesDao.addCluesEnclosures(enclosureParams);
                    }

                } catch (Exception e) {
                    // 处理行处理过程中的异常
                    errorCount++;
                    errorRecord.put("rowNum", r);
                    errorRecord.put("customerName", row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "未知");
                    errorRecord.put("errors", "处理数据时发生异常: " + e.getMessage());
                    errorRecord.put("type", "exception"); // 标记为异常错误
                    errorRecords.add(errorRecord);
                }
            }

            // 保存数据
            if (!formalList.isEmpty()) {

                // 先清空临时表
                excelImportMapper.delProhectClueMarkTemp(sUserId);

                // 重新保存更新后的数据到临时表
                excelImportMapper.saveProhectClueMarkTemp(formalList, sUserId);

                // 将临时表数据导入正式表
                excelImportMapper.saveMarkTempToProjectClueA(sUserId);
                excelImportMapper.saveMarkTempToProjectClueB(sUserId);
                excelImportMapper.saveMarkTempToProjectClueC(sUserId);
                excelImportMapper.saveMarkTempToProjectClueD(sUserId);
                // 清空临时表
                excelImportMapper.delProhectClueMarkTemp(sUserId);

            }

            if (!tempList.isEmpty()) {
                // 清空临时表
                excelImportMapper.delProhectClueMarkTemp(sUserId);
                // 保存到临时表
                excelImportMapper.saveProhectClueMarkTemp(tempList, sUserId);
            }



            // 构建导入结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("totalCount", totalCount);
            resultMap.put("successCount", successCount);
            resultMap.put("tempCount", tempCount);
            resultMap.put("errorCount", errorCount);
            resultMap.put("errorRecords", errorRecords);

            // 记录导入结果日志
            String result = String.format("导入完成，总数: %d, 成功导入正式表: %d, 导入临时表: %d, 失败: %d",
                    totalCount, successCount, tempCount, errorCount);

            if (noProName.size() > 0) {
                String pro = ", 以下项目未查询到：" + StringUtils.join(noProName, ",");
                result = result + pro;
            }
            if (noUser.size() > 0) {
                String pro = ", 以下人员未查询到：" + StringUtils.join(noUser, ",");
                result = result + pro;
            }

            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("批量导入地图客户任务结束");
            sysLog0.setNote(result + "-人员记录-操作人：" + sUserName + "-操作人账号：" + sUserId);
            excelImportMapper.insertLogs(sysLog0);

            return ResultBody.success(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            // 记录导入失败日志
            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("批量导入地图客户任务失败");
            sysLog0.setNote("导入失败-操作人：" + sUserName + "-操作人账号：" + sUserId + "-错误信息：" + e.getMessage());
            excelImportMapper.insertLogs(sysLog0);
            return ResultBody.error(-1001, "数据有误 导入失败 请检查！");
        }
    }



    /**
     * 更新临时表中客户地址信息并导入正式表
     * @param customerAddressList 客户地址信息列表
     * @return 处理结果
     */
    @ResponseBody
    @RequestMapping(value = "/updateCustomerAddressOld", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "更新客户地址信息并导入正式表", notes = "更新临时表中的客户地址、经纬度信息，并将数据导入正式表")
    public ResultBody updateCustomerAddressOld(@RequestBody List<Map<String, Object>> customerAddressList) {
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
                excelImportMapper.saveMarkTempToProjectClueA(sUserId);
                excelImportMapper.saveMarkTempToProjectClueB(sUserId);
                excelImportMapper.saveMarkTempToProjectClueC(sUserId);
                excelImportMapper.saveMarkTempToProjectClueD(sUserId);

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

    private String convertExcelDateToStr(double excelDateNum, SimpleDateFormat sdf) {
        try {
            // Excel日期从1900年1月1日开始计算（实际有2天误差，需要减去2）
            Calendar calendar = Calendar.getInstance();
            calendar.set(1900, Calendar.JANUARY, 1);
            calendar.add(Calendar.DAY_OF_YEAR, (int) excelDateNum - 2);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            // 转换失败，使用当前日期
            return sdf.format(new Date());
        }
    }

}
