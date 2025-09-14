package cn.visolink.system.excel.controller;

import cn.visolink.common.permission.RequiresPermission;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.message.model.SysLog;
import cn.visolink.system.channel.dao.ProjectCluesDao;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.excel.service.ExcelImportService;
import cn.visolink.system.excel.util.BatchGeocodingUtil;
import cn.visolink.system.fileupload.service.FileUploadService;
import cn.visolink.system.job.authorization.mapper.JobMapper;
import cn.visolink.system.timetask.SyncDataTask;
import cn.visolink.utils.FileUtil;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.system.custMap.dao.CustMapDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.26
 */
@RestController
@Api(tags = "导出日志")
@RequestMapping("/excel")
public class MapExcelImportThreadController {
    private static final Logger logger = LoggerFactory.getLogger(MapExcelImportThreadController.class);

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
    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private CustMapDao custMapDao;
    private static final Map<String, Integer> STATUS_TYPE_WEIGHT_MAPTYPE2 = new HashMap<>();
    private static final Map<String, Integer> STATUS_TYPE_WEIGHT_MAPTYPE1 = new HashMap<>();

    static {
        // mapType=2
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("1", 9);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("2", 8);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("7", 7);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("6", 6);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("4", 5);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("3", 4);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("5", 3);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("9", 2);
        STATUS_TYPE_WEIGHT_MAPTYPE2.put("8", 1);
        // mapType=1
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("1", 9);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("2", 8);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("3", 7);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("4", 6);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("5", 5);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("9", 4);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("8", 3);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("7", 2);
        STATUS_TYPE_WEIGHT_MAPTYPE1.put("6", 1);
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
        while (m.find()) {
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

            town += m.group("town");
            if ((county == null || "".equals(county)) && town != null && !"".equals(town)) {
                town = town + "-";
            }
            village = m.group("village");

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
    @Log("招商地图导入")
    @RequestMapping(value = "/imporMapCustomer", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody imporMapCustomer(MultipartFile file, String jobName, String jobCode, String isMarkedAsOriginalStatus) throws IOException {
        long startTime = System.currentTimeMillis();
        String startTimeString=String.valueOf(startTime);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sUserId = SecurityUtils.getUserId();
        String sUserName = SecurityUtils.getUsername();
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("批量导入地图客户任务开始");
        sysLog.setNote("人员记录-操作人：" + sUserName + "-操作人id：" + sUserId);
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

        //String jobCode = null;

        List<Map<String, Object>> errorRecords = new ArrayList<>();
        List<ReportCustomerForm> tempList = new ArrayList<>(); // 临时表数据（只有地址/经纬度问题）
        List<ReportCustomerForm> formalList = new ArrayList<>(); // 正式表数据（完全正确）

        List<String> noProName = new ArrayList<>();
        List<String> noUser = new ArrayList<>();

        // 判重Map，记录已导入的客户名称
        Map<String, Boolean> duplicateCheck = new HashMap<>();

        try {
            // 存储需要批量处理的客户列表
            List<ReportCustomerForm> addressToCoordinatesList = new ArrayList<>();
            List<ReportCustomerForm> coordinatesToAddressList = new ArrayList<>();

            // 创建线索ID到错误记录的映射
            Map<String, Map<String, Object>> errorRecordMap = new HashMap<>();
            // 创建线索ID到行号的映射
            Map<String, Integer> clueIdToRowNum = new HashMap<>();
            // 创建批处理记录到行号的映射
            Map<String, Integer> batchRowNumMap = new HashMap<>();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                //通过有没有序号和客户名称来判断是否输入数据行
                Cell keyCell1 = row.getCell(0);
                Cell keyCell2 = row.getCell(1);
                if (keyCell1 == null && keyCell2 == null) {
                    continue; // 关键列单元格为空，视为空白行跳过
                }
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
                    reportCustomerForm.setSourceModeCode("30");
                    Map queryMap = new HashMap<>();

                    // 获取客户姓名（必填）
                    String customerName = "";
                    String apiAddress = "";
                    if (row.getCell(1) != null) {
                        customerName = row.getCell(1).getStringCellValue().trim();
                        if (StringUtils.isEmpty(customerName)) {
                            rowErrors.add("客户名称不能为空");
                        } else {
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
//                    if (row.getCell(17) != null) {
//                        userName = row.getCell(17).getStringCellValue().trim();
//                    }
                    userName = sUserName;
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
                    String customerMobileS = "";
                    if (row.getCell(3) != null) {
                        customerMobileS = row.getCell(3).getStringCellValue().trim();
                        String rawMobile = row.getCell(3).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(rawMobile)) {
                            // 支持多个联系方式以分号分割，取第一个作为主要联系方式
                            String[] mobileArray = rawMobile.split(";");
                            if (mobileArray.length > 0) {
                                customerMobile = mobileArray[0].trim();

                                // 对每一个联系方式都进行格式校验
                                for (int i = 0; i < mobileArray.length; i++) {
                                    String mobile = mobileArray[i].trim();
                                    if (StringUtils.isNotEmpty(mobile)) {
                                        // 中国大陆手机号格式：1开头的11位数字
                                        String mobileRegex = "^1[3-9]\\d{9}$";
                                        if (!mobile.matches(mobileRegex)) {
                                            rowErrors.add("第" + (i + 1) + "个联系方式格式不正确：" + mobile + "，请输入正确的手机号码");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    reportCustomerForm.setCustomerMobile(customerMobile);
                    reportCustomerForm.setCustomerMobileS(customerMobileS);

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
                        // 如果有地址但没有经纬度，添加到批量处理列表
                        if (StringUtils.isNotEmpty(customerAddress)) {
                            // 记录原始行号
                            reportCustomerForm.setRowNum(r);
                            addressToCoordinatesList.add(reportCustomerForm);
                        }
                    } else if (StringUtils.isEmpty(customerAddress)) {
                        // 如果有经纬度但没有地址，添加到批量处理列表
                        // 记录原始行号
                        reportCustomerForm.setRowNum(r);
                        coordinatesToAddressList.add(reportCustomerForm);
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
//                    Cell createDateCell = row.getCell(16);
//
//                    if (createDateCell != null) {
//                        // 处理单元格类型
//                        switch (createDateCell.getCellType()) {
//                            case Cell.CELL_TYPE_STRING:
//                                String cellValue = createDateCell.getStringCellValue().trim();
//                                if (!StringUtils.isEmpty(cellValue)) {
//                                    try {
//                                        // 尝试将字符串解析为数字（Excel日期数值）
//                                        double excelDateNum = Double.parseDouble(cellValue);
//                                        createDate = convertExcelDateToStr(excelDateNum, sf);
//                                    } catch (NumberFormatException e) {
//                                        // 如果不是数字格式，尝试作为日期字符串解析并验证
//                                        Date parsedDate = parseDateWithStrictValidation(cellValue);
//                                        if (parsedDate != null) {
//                                            createDate = sf.format(parsedDate);
//                                        } else {
//                                            // 非法日期格式，记录错误并使用当前日期
//                                            rowErrors.add("日期格式有问题: " + cellValue);
//                                            createDate = sf.format(new Date());
//                                        }
//                                    }
//                                } else {
//                                    createDate = sf.format(new Date()); // 空字符串，使用当前日期
//                                }
//                                break;
//
//                            case Cell.CELL_TYPE_NUMERIC:
//                                // 直接处理数值类型的日期
//                                double excelDateNum = createDateCell.getNumericCellValue();
//                                createDate = convertExcelDateToStr(excelDateNum, sf);
//                                break;
//
//                            default:
//                                // 其他类型的单元格，使用当前日期
//                                createDate = sf.format(new Date());
//                        }
//                    } else {
//                        // 单元格为空，使用当前日期
//                        createDate = sf.format(new Date());
//                    }

                    createDate = sf.format(new Date());
                    reportCustomerForm.setCreateDate(createDate);

                    // 获取录入人
//                    if (row.getCell(17) != null) {
//                        userName = row.getCell(17).getStringCellValue().trim();
//                        if (StringUtils.isEmpty(userName)) {
//                            rowErrors.add("录入人不能为空");
//                        }
//                    } else {
//                        rowErrors.add("录入人不能为空");
//                    }
                    userName = sUserName;

                    // 获取录入人身份
                    String reportUserRole = jobName;
//                    if (row.getCell(18) != null) {
//                        reportUserRole = row.getCell(18).getStringCellValue().trim();
//                        if (StringUtils.isEmpty(reportUserRole)) {
//                            rowErrors.add("录入人身份不能为空");
//                        }
//                    } else {
//                        rowErrors.add("录入人身份不能为空");
//                    }
                    // 设置录入人身份
                    //项目招商经理、项目招商总监、项目营销经理、区域招商经理、区域招商总监、区域营销经理
//                    if (StringUtils.isNotEmpty(reportUserRole)) {
//                        if ("项目招商经理".equals(reportUserRole)) {
//                            jobCode = "xsjl";
//                            reportCustomerForm.setReportUserRole("5");
//                        } else if ("项目招商总监".equals(reportUserRole)) {
//                            jobCode = "zszj";
//                            reportCustomerForm.setReportUserRole("6");
//                        } else if ("项目营销经理".equals(reportUserRole)) {
//                            jobCode = "yxjl";
//                            reportCustomerForm.setReportUserRole("7");
//                        }else if ("区域招商经理".equals(reportUserRole)) {
//                            jobCode = "qyxsjl";
//                            reportCustomerForm.setReportUserRole("8");
//                        }else if ("区域招商总监".equals(reportUserRole)) {
//                            jobCode = "qyzszj";
//                            reportCustomerForm.setReportUserRole("9");
//                        }else if ("区域营销经理".equals(reportUserRole)) {
//                            jobCode = "qyyxjl";
//                            reportCustomerForm.setReportUserRole("10");
//                        }else {
//                            rowErrors.add("录入人身份不正确，只能是'项目招商经理'、'项目招商总监'、'项目营销经理'、'区域招商经理'、'区域招商总监'、'区域营销经理'");
//                        }
//                    }
                    if (StringUtils.isNotEmpty(reportUserRole)) {
                        if ("xsjl".equals(jobCode)) {
                            jobCode = "xsjl";
                            reportCustomerForm.setReportUserRole("5");
                        } else if ("zszj".equals(jobCode)) {
                            jobCode = "zszj";
                            reportCustomerForm.setReportUserRole("6");
                        } else if ("yxjl".equals(jobCode)) {
                            jobCode = "yxjl";
                            reportCustomerForm.setReportUserRole("7");
                        } else if ("qyxsjl".equals(jobCode)) {
                            jobCode = "qyxsjl";
                            reportCustomerForm.setReportUserRole("8");
                        } else if ("qyzszj".equals(jobCode)) {
                            jobCode = "qyzszj";
                            reportCustomerForm.setReportUserRole("9");
                        } else if ("qyyxjl".equals(jobCode)) {
                            jobCode = "qyyxjl";
                            reportCustomerForm.setReportUserRole("10");
                        } else {
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
                    if (rowErrors.isEmpty() && addressErrors.isEmpty()) {
                        // 设置线索信息
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        reportCustomerForm.setFollowUpWay("导入");

                        reportCustomerForm.setFollowUpDetail("批量导入地图客户");
                        // 设置标注信息
                        reportCustomerForm.setDimensionType("3");
                        // 设置备注信息 记录导入人员
                        reportCustomerForm.setFlag("map_" + sUserName + "_" + sUserId +"_"+ startTimeString+ "_" + isMarkedAsOriginalStatus);
                        reportCustomerForm.setRowNum(r);
                        formalList.add(reportCustomerForm);
                        successCount++;
                    } else if (rowErrors.isEmpty() && !addressErrors.isEmpty()) {
                        // 只有地址/经纬度问题的数据，加入临时表列表
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        reportCustomerForm.setFollowUpWay("导入");

                        reportCustomerForm.setFollowUpDetail("批量导入地图客户");
                        reportCustomerForm.setDimensionType("3");
                        reportCustomerForm.setFlag("map_" + sUserName + "_" + sUserId+"_"+ startTimeString+ "_" + isMarkedAsOriginalStatus);
                        reportCustomerForm.setRowNum(r);
                        tempList.add(reportCustomerForm);
                        tempCount++;

                        // 记录错误信息
                        errorRecord.put("rowNum", r);
                        errorRecord.put("projectClueId", ProjectClueId);
                        errorRecord.put("customerName", customerName);
                        errorRecord.put("errors", String.join("; ", addressErrors));
                        errorRecord.put("type", "address"); // 标记为地址错误
                        errorRecords.add(errorRecord);

                        // 保存到映射中
                        errorRecordMap.put(ProjectClueId, errorRecord);
                        clueIdToRowNum.put(ProjectClueId, r);

                        // 记录批处理行号
                        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
                            batchRowNumMap.put(ProjectClueId, r);
                        }
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

            // 批量处理地址转经纬度
            if (!addressToCoordinatesList.isEmpty()) {
                Map<String, Integer> result = BatchGeocodingUtil.batchAddressToCoordinates(addressToCoordinatesList, 300);
                if (result.get("failed") > 0) {
                    // 处理失败的情况
                    for (ReportCustomerForm form : addressToCoordinatesList) {
                        if (StringUtils.isEmpty(form.getLongitude()) || StringUtils.isEmpty(form.getLatitude())) {
                            String clueId = form.getProjectClueId();
                            // 查找对应的错误记录
                            Map<String, Object> errorRecord = errorRecordMap.get(clueId);
                            if (errorRecord != null) {
                                // 合并错误信息
                                String existingErrors = (String) errorRecord.get("errors");
                                String newError = "根据企业地址获取经纬度失败";
                                errorRecord.put("errors", existingErrors + "; " + newError);
                            } else {
                                // 如果没有找到现有错误记录，创建新的
                                errorRecord = new HashMap<>();
                                errorRecord.put("rowNum", form.getRowNum()); // 使用原始行号
                                errorRecord.put("projectClueId", clueId);
                                errorRecord.put("customerName", form.getCustomerName());
                                errorRecord.put("errors", "根据企业地址获取经纬度失败");
                                errorRecord.put("type", "address");
                                errorRecords.add(errorRecord);
                                errorRecordMap.put(clueId, errorRecord);
                            }
                            // 从tempList中移除失败的记录
                            tempList.removeIf(item -> item.getRowNum() == form.getRowNum());
                            formalList.removeIf(item -> item.getRowNum() == form.getRowNum());
                        }
                    }
                }
                // 将处理后的结果重新分配回原始列表
                for (ReportCustomerForm form : addressToCoordinatesList) {
                    if (!StringUtils.isEmpty(form.getLongitude()) && !StringUtils.isEmpty(form.getLatitude())) {
                        // 找到对应的原始记录并更新经纬度
                        for (ReportCustomerForm originalForm : tempList) {
                            if (originalForm.getProjectClueId().equals(form.getProjectClueId())) {
                                originalForm.setLongitude(form.getLongitude());
                                originalForm.setLatitude(form.getLatitude());
                                break;
                            }
                        }
                        for (ReportCustomerForm originalForm : formalList) {
                            if (originalForm.getProjectClueId().equals(form.getProjectClueId())) {
                                originalForm.setLongitude(form.getLongitude());
                                originalForm.setLatitude(form.getLatitude());
                                break;
                            }
                        }
                    }
                }
            }

            // 批量处理经纬度转地址
            if (!coordinatesToAddressList.isEmpty()) {
                Map<String, Integer> result = BatchGeocodingUtil.batchCoordinatesToAddress(coordinatesToAddressList, 300);
                if (result.get("failed") > 0) {
                    // 处理失败的情况
                    for (ReportCustomerForm form : coordinatesToAddressList) {
                        if (StringUtils.isEmpty(form.getCustomerAddress())) {
                            String clueId = form.getProjectClueId();
                            // 查找对应的错误记录
                            Map<String, Object> errorRecord = errorRecordMap.get(clueId);
                            if (errorRecord != null) {
                                // 合并错误信息
                                String existingErrors = (String) errorRecord.get("errors");
                                String newError = "根据经纬度获取企业地址失败";
                                errorRecord.put("errors", existingErrors + "; " + newError);
                            } else {
                                // 如果没有找到现有错误记录，创建新的
                                errorRecord = new HashMap<>();
                                errorRecord.put("rowNum", form.getRowNum()); // 使用原始行号
                                errorRecord.put("projectClueId", clueId);
                                errorRecord.put("customerName", form.getCustomerName());
                                errorRecord.put("errors", "根据经纬度获取企业地址失败");
                                errorRecord.put("type", "address");
                                errorRecords.add(errorRecord);
                                errorRecordMap.put(clueId, errorRecord);
                            }
                            // 从tempList中移除失败的记录
                            tempList.removeIf(item -> item.getRowNum() == form.getRowNum());
                            formalList.removeIf(item -> item.getRowNum() == form.getRowNum());
                        }
                    }
                }
                // 将处理后的结果重新分配回原始列表
                for (ReportCustomerForm form : coordinatesToAddressList) {
                    if (!StringUtils.isEmpty(form.getCustomerAddress())) {
                        // 找到对应的原始记录并更新地址
                        for (ReportCustomerForm originalForm : tempList) {
                            if (originalForm.getProjectClueId().equals(form.getProjectClueId())) {
                                originalForm.setCustomerAddress(form.getCustomerAddress());
                                break;
                            }
                        }
                        for (ReportCustomerForm originalForm : formalList) {
                            if (originalForm.getProjectClueId().equals(form.getProjectClueId())) {
                                originalForm.setCustomerAddress(form.getCustomerAddress());
                                break;
                            }
                        }
                    }
                }
            }

            // 新增：批量通过客户名获取API地址和经纬度
            BatchGeocodingUtil.batchNameToAddress(formalList, 300);
            BatchGeocodingUtil.batchNameToAddress(tempList, 300);

            // 新增：对比API返回的地址与原地址，不一致则记录错误信息
            Iterator<ReportCustomerForm> iterator = formalList.iterator();
            while (iterator.hasNext()) {
                ReportCustomerForm form = iterator.next();
                if (form.getApiCustomerAddress() != null && !form.getApiCustomerAddress().isEmpty() && form.getCustomerAddress() != null && !form.getCustomerAddress().isEmpty()
                        && !form.getApiCustomerAddress().equals(form.getCustomerAddress())) {
                    // 记录冲突日志
                    Map<String, Object> errorRecord = new HashMap<>();
                    errorRecord.put("rowNum", form.getRowNum());
                    errorRecord.put("projectClueId", form.getProjectClueId());
                    errorRecord.put("customerName", form.getCustomerName());
                    errorRecord.put("errors", "API地址与Excel地址不一致，API地址：" + form.getApiCustomerAddress() + "，Excel地址：" + form.getCustomerAddress());
                    errorRecord.put("type", "addrNotMatch");
                    errorRecords.add(errorRecord);

                    // 拼接字段
                    if (!form.getCustomerAddress().contains("_")) {
                        form.setCustomerAddress(form.getCustomerAddress() + "_" + form.getApiCustomerAddress());
                        form.setLongitude(form.getLongitude() + "_" + form.getApiLongitude());
                        form.setLatitude(form.getLatitude() + "_" + form.getApiLatitude());
                    }

                    // 移除formalList，加入tempList
                    iterator.remove();
                    tempList.add(form);
                    successCount--;
                    tempCount++;
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
                excelImportMapper.saveMarkTempToProjectClueE(sUserId);

                //导入数据写入缓存表
                excelImportMapper.saveMarkTempToProjectClueCache(sUserId);
                //处理是否按该项目已有重叠客户标注逻辑
                if ("1".equals(isMarkedAsOriginalStatus)) {
                    this.updateCustomerStatusCacheRecent(sUserId);
                }

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
            // 对错误记录按行号排序
            errorRecords.sort(Comparator.comparingInt(e -> (Integer) e.get("rowNum")));
            resultMap.put("errorRecords", errorRecords);


            // 在构建 resultMap 后，返回结果前添加
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            // 将耗时转换为秒，保留两位小数
            double durationInSeconds = Math.round(duration / 10.0) / 100.0;
            resultMap.put("duration", durationInSeconds);


            // 记录导入结果日志
            String result = String.format("导入完成，总数: %d, 成功导入正式表: %d, 导入临时表: %d, 失败: %d, 耗时: %.2f秒",
                    totalCount, successCount, tempCount, errorCount, durationInSeconds);

            if (noProName.size() > 0) {
                String pro = ", 以下项目未查询到：" + StringUtils.join(noProName, ",");
                result = result + pro;
            }
            if (noUser.size() > 0) {
                String pro = ", 以下人员未查询到：" + StringUtils.join(noUser, ",");
                result = result + pro;
            }

            SysLog sysLog0 = new SysLog();
            sysLog0.setMyUUID(startTimeString);
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("批量导入地图客户任务结束");
            sysLog0.setNote(result + "-人员记录-操作人：" + sUserName + "-操作人账号：" + sUserId);
            excelImportMapper.insertLogsWithID(sysLog0);

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
    @Log("招商地图导入-历史数据")
    @RequestMapping(value = "/imporMapCustomerHistory", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public ResultBody imporMapCustomerHistory(MultipartFile file, String jobName) throws IOException {
        long startTime = System.currentTimeMillis();
        String startTimeString=String.valueOf(startTime);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sUserId = SecurityUtils.getUserId();
        String sUserName = SecurityUtils.getUsername();
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("批量导入地图客户历史数据任务开始");
        sysLog.setNote("人员记录-操作人：" + sUserName + "-操作人id：" + sUserId);
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
            // 存储需要批量处理的客户列表
            List<ReportCustomerForm> addressToCoordinatesList = new ArrayList<>();
            List<ReportCustomerForm> coordinatesToAddressList = new ArrayList<>();

            // 创建线索ID到错误记录的映射
            Map<String, Map<String, Object>> errorRecordMap = new HashMap<>();
            // 创建线索ID到行号的映射
            Map<String, Integer> clueIdToRowNum = new HashMap<>();
            // 创建批处理记录到行号的映射
            Map<String, Integer> batchRowNumMap = new HashMap<>();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                //通过有没有序号和客户名称来判断是否输入数据行
                Cell keyCell1 = row.getCell(0);
                Cell keyCell2 = row.getCell(1);
                if (keyCell1 == null && keyCell2 == null) {
                    continue; // 关键列单元格为空，视为空白行跳过
                }
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
                    reportCustomerForm.setSourceModeCode("31");
                    Map queryMap = new HashMap<>();

                    // 获取客户姓名（必填）
                    String customerName = "";
                    String apiAddress = "";
                    if (row.getCell(1) != null) {
                        customerName = row.getCell(1).getStringCellValue().trim();
                        if (StringUtils.isEmpty(customerName)) {
                            rowErrors.add("客户名称不能为空");
                        } else {
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
                    String khjd = "";
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
                        checkParams.put("customerName", customerName);
                        checkParams.put("projectName", projectName);
                        int count = excelImportMapper.checkZsdtdrCustomerExistsHis(checkParams);
                        if (count > 0) {
                            rowErrors.add("系统中该项目已存在该客户: " + customerName);
                        }

                        Map<String, Object> cMap = excelImportMapper.getCustDealInfo(customerName);

                        if (cMap != null && !cMap.isEmpty()) {
                            String proName = cMap.get("ProjectName").toString();
                            if (proName.equals(projectName)) {
                                khjd = "已成交（在本项目）";
                            } else {
                                khjd = "已成交（在其他项目）";
                            }
                        }

                    }

                    // 获取联系人
                    String contacts = "";
                    if (row.getCell(2) != null) {
                        contacts = row.getCell(2).getStringCellValue().trim();
                    }
                    reportCustomerForm.setContacts(contacts);

                    /// 获取联系方式
                    String customerMobile = "";
                    String customerMobileS = "";
                    if (row.getCell(3) != null) {
                        customerMobileS = row.getCell(3).getStringCellValue().trim();
                        String rawMobile = row.getCell(3).getStringCellValue().trim();
                        if (StringUtils.isNotEmpty(rawMobile)) {
                            // 支持多个联系方式以分号分割，取第一个作为主要联系方式
                            String[] mobileArray = rawMobile.split(";");
                            if (mobileArray.length > 0) {
                                customerMobile = mobileArray[0].trim();

                                // 对每一个联系方式都进行格式校验
                                for (int i = 0; i < mobileArray.length; i++) {
                                    String mobile = mobileArray[i].trim();
                                    if (StringUtils.isNotEmpty(mobile)) {
                                        // 中国大陆手机号格式：1开头的11位数字
                                        String mobileRegex = "^1[3-9]\\d{9}$";
                                        if (!mobile.matches(mobileRegex)) {
                                            rowErrors.add("第" + (i + 1) + "个联系方式格式不正确：" + mobile + "，请输入正确的手机号码");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    reportCustomerForm.setCustomerMobile(customerMobile);
                    reportCustomerForm.setCustomerMobileS(customerMobileS);

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
                        // 如果有地址但没有经纬度，添加到批量处理列表
                        if (StringUtils.isNotEmpty(customerAddress)) {
                            // 记录原始行号
                            reportCustomerForm.setRowNum(r);
                            addressToCoordinatesList.add(reportCustomerForm);
                        }
                    } else if (StringUtils.isEmpty(customerAddress)) {
                        // 如果有经纬度但没有地址，添加到批量处理列表
                        // 记录原始行号
                        reportCustomerForm.setRowNum(r);
                        coordinatesToAddressList.add(reportCustomerForm);
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
                                        // 如果不是数字格式，尝试作为日期字符串解析并验证
                                        Date parsedDate = parseDateWithStrictValidation(cellValue);
                                        if (parsedDate != null) {
                                            createDate = sf.format(parsedDate);
                                        } else {
                                            // 非法日期格式，记录错误并使用当前日期
                                            rowErrors.add("日期格式有问题: " + cellValue);
                                            createDate = sf.format(new Date());
                                        }
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
                        String hkjd_excel = row.getCell(columnIndex).getStringCellValue().trim();
                        //校验excel填写成交的真实性
                        if (StringUtils.isNotBlank(khjd)) {
                            reportCustomerForm.setFollowUpDetail(khjd);
                        } else {
                            if ("已成交（在本项目）".equals(hkjd_excel) || "已成交（在其他项目）".equals(hkjd_excel)) {
                                reportCustomerForm.setFollowUpDetail("目标企业");
                            } else {
                                reportCustomerForm.setFollowUpDetail(hkjd_excel);
                            }
                        }

                    }
                    columnIndex++;

                    // 设置录入人身份
                    //项目招商经理、项目招商总监、项目营销经理、区域招商经理、区域招商总监、区域营销经理
                    if (StringUtils.isNotEmpty(reportUserRole)) {
                        if ("项目招商专员".equals(reportUserRole)) {
                            jobCode = "zygw";
                            reportCustomerForm.setReportUserRole("1");
                        } else if ("区域招商专员".equals(reportUserRole)) {
                            jobCode = "qyzygw";
                            reportCustomerForm.setReportUserRole("2");
                        } else if ("项目招商经理".equals(reportUserRole)) {
                            jobCode = "xsjl";
                            reportCustomerForm.setReportUserRole("5");
                        } else if ("项目招商总监".equals(reportUserRole)) {
                            jobCode = "zszj";
                            reportCustomerForm.setReportUserRole("6");
                        } else if ("项目营销经理".equals(reportUserRole)) {
                            jobCode = "yxjl";
                            reportCustomerForm.setReportUserRole("7");
                        } else if ("区域招商经理".equals(reportUserRole)) {
                            jobCode = "qyxsjl";
                            reportCustomerForm.setReportUserRole("8");
                        } else if ("区域招商总监".equals(reportUserRole)) {
                            jobCode = "qyzszj";
                            reportCustomerForm.setReportUserRole("9");
                        } else if ("区域营销经理".equals(reportUserRole)) {
                            jobCode = "qyyxjl";
                            reportCustomerForm.setReportUserRole("10");
                        } else {
                            rowErrors.add("录入人身份不正确，只能是'项目招商专员'、'区域招商专员'、'项目招商经理'、'项目招商总监'、'项目营销经理'、'区域招商经理'、'区域招商总监'、'区域营销经理'");
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
                    if (rowErrors.isEmpty() && addressErrors.isEmpty()) {
                        // 设置线索信息
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        reportCustomerForm.setFollowUpWay("导入历史数据");

                        reportCustomerForm.setFollowUpDetail("批量导入地图客户历史数据-客户阶段-" + reportCustomerForm.getFollowUpDetail());
                        // 设置标注信息
                        reportCustomerForm.setDimensionType("3");
                        // 设置备注信息 记录导入人员
                        reportCustomerForm.setFlag("maphistory_" + sUserName + "_" + sUserId+"_"+startTimeString);
                        reportCustomerForm.setRowNum(r);
                        formalList.add(reportCustomerForm);
                        successCount++;
                    } else if (rowErrors.isEmpty() && !addressErrors.isEmpty()) {
                        // 只有地址/经纬度问题的数据，加入临时表列表
                        String ProjectClueId = UUID.randomUUID().toString();
                        reportCustomerForm.setProjectClueUuid(ProjectClueId);
                        reportCustomerForm.setProjectClueId(ProjectClueId);
                        reportCustomerForm.setFollowUpWay("导入历史数据");

                        reportCustomerForm.setFollowUpDetail("批量导入地图客户历史数据-客户阶段-" + reportCustomerForm.getFollowUpDetail());
                        reportCustomerForm.setDimensionType("3");
                        reportCustomerForm.setFlag("maphistory_" + sUserName + "_" + sUserId+"_"+startTimeString);
                        reportCustomerForm.setRowNum(r);
                        tempList.add(reportCustomerForm);
                        tempCount++;

                        // 记录错误信息
                        errorRecord.put("rowNum", r);
                        errorRecord.put("projectClueId", ProjectClueId);
                        errorRecord.put("customerName", customerName);
                        errorRecord.put("errors", String.join("; ", addressErrors));
                        errorRecord.put("type", "address"); // 标记为地址错误
                        errorRecords.add(errorRecord);

                        // 保存到映射中
                        errorRecordMap.put(ProjectClueId, errorRecord);
                        clueIdToRowNum.put(ProjectClueId, r);

                        // 记录批处理行号
                        if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
                            batchRowNumMap.put(ProjectClueId, r);
                        }
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

            // 批量处理地址转经纬度
            if (!addressToCoordinatesList.isEmpty()) {
                Map<String, Integer> result = BatchGeocodingUtil.batchAddressToCoordinates(addressToCoordinatesList, 300);
                if (result.get("failed") > 0) {
                    // 处理失败的情况
                    for (ReportCustomerForm form : addressToCoordinatesList) {
                        if (StringUtils.isEmpty(form.getLongitude()) || StringUtils.isEmpty(form.getLatitude())) {
                            String clueId = form.getProjectClueId();
                            // 查找对应的错误记录
                            Map<String, Object> errorRecord = errorRecordMap.get(clueId);
                            if (errorRecord != null) {
                                // 合并错误信息
                                String existingErrors = (String) errorRecord.get("errors");
                                String newError = "根据企业地址获取经纬度失败";
                                errorRecord.put("errors", existingErrors + "; " + newError);
                            } else {
                                // 如果没有找到现有错误记录，创建新的
                                errorRecord = new HashMap<>();
                                errorRecord.put("rowNum", form.getRowNum()); // 使用原始行号
                                errorRecord.put("projectClueId", clueId);
                                errorRecord.put("customerName", form.getCustomerName());
                                errorRecord.put("errors", "根据企业地址获取经纬度失败");
                                errorRecord.put("type", "address");
                                errorRecords.add(errorRecord);
                                errorRecordMap.put(clueId, errorRecord);
                            }
                            // 从tempList中移除失败的记录
                            tempList.removeIf(item -> item.getRowNum() == form.getRowNum());
                            formalList.removeIf(item -> item.getRowNum() == form.getRowNum());
                        }
                    }
                }
                // 将处理后的结果重新分配回原始列表
                for (ReportCustomerForm form : addressToCoordinatesList) {
                    if (!StringUtils.isEmpty(form.getLongitude()) && !StringUtils.isEmpty(form.getLatitude())) {
                        // 找到对应的原始记录并更新经纬度
                        for (ReportCustomerForm originalForm : tempList) {
                            if (originalForm.getProjectClueId().equals(form.getProjectClueId())) {
                                originalForm.setLongitude(form.getLongitude());
                                originalForm.setLatitude(form.getLatitude());
                                break;
                            }
                        }
                        for (ReportCustomerForm originalForm : formalList) {
                            if (originalForm.getProjectClueId().equals(form.getProjectClueId())) {
                                originalForm.setLongitude(form.getLongitude());
                                originalForm.setLatitude(form.getLatitude());
                                break;
                            }
                        }
                    }
                }
            }

            // 批量处理经纬度转地址
            if (!coordinatesToAddressList.isEmpty()) {
                Map<String, Integer> result = BatchGeocodingUtil.batchCoordinatesToAddress(coordinatesToAddressList, 300);
                if (result.get("failed") > 0) {
                    // 处理失败的情况
                    for (ReportCustomerForm form : coordinatesToAddressList) {
                        if (StringUtils.isEmpty(form.getCustomerAddress())) {
                            String clueId = form.getProjectClueId();
                            // 查找对应的错误记录
                            Map<String, Object> errorRecord = errorRecordMap.get(clueId);
                            if (errorRecord != null) {
                                // 合并错误信息
                                String existingErrors = (String) errorRecord.get("errors");
                                String newError = "根据经纬度获取企业地址失败";
                                errorRecord.put("errors", existingErrors + "; " + newError);
                            } else {
                                // 如果没有找到现有错误记录，创建新的
                                errorRecord = new HashMap<>();
                                errorRecord.put("rowNum", form.getRowNum()); // 使用原始行号
                                errorRecord.put("projectClueId", clueId);
                                errorRecord.put("customerName", form.getCustomerName());
                                errorRecord.put("errors", "根据经纬度获取企业地址失败");
                                errorRecord.put("type", "address");
                                errorRecords.add(errorRecord);
                                errorRecordMap.put(clueId, errorRecord);
                            }
                            // 从tempList中移除失败的记录
                            tempList.removeIf(item -> item.getRowNum() == form.getRowNum());
                            formalList.removeIf(item -> item.getRowNum() == form.getRowNum());
                        }
                    }
                }
                // 将处理后的结果重新分配回原始列表
                for (ReportCustomerForm form : coordinatesToAddressList) {
                    if (!StringUtils.isEmpty(form.getCustomerAddress())) {
                        // 找到对应的原始记录并更新地址
                        for (ReportCustomerForm originalForm : tempList) {
                            if (originalForm.getProjectClueId().equals(form.getProjectClueId())) {
                                originalForm.setCustomerAddress(form.getCustomerAddress());
                                break;
                            }
                        }
                        for (ReportCustomerForm originalForm : formalList) {
                            if (originalForm.getProjectClueId().equals(form.getProjectClueId())) {
                                originalForm.setCustomerAddress(form.getCustomerAddress());
                                break;
                            }
                        }
                    }
                }
            }

            // 新增：批量通过客户名获取API地址和经纬度
            BatchGeocodingUtil.batchNameToAddress(formalList, 300);
            BatchGeocodingUtil.batchNameToAddress(tempList, 300);

            // 新增：对比API返回的地址与原地址，不一致则记录错误信息
            Iterator<ReportCustomerForm> iterator = formalList.iterator();
            while (iterator.hasNext()) {
                ReportCustomerForm form = iterator.next();
                if (form.getApiCustomerAddress() != null && !form.getApiCustomerAddress().isEmpty()
                        && form.getCustomerAddress() != null && !form.getCustomerAddress().isEmpty()
                        && !form.getApiCustomerAddress().equals(form.getCustomerAddress())) {

                    // 记录冲突日志
                    Map<String, Object> errorRecord = new HashMap<>();
                    errorRecord.put("rowNum", form.getRowNum());
                    errorRecord.put("projectClueId", form.getProjectClueId());
                    errorRecord.put("customerName", form.getCustomerName());
                    errorRecord.put("errors", "API地址与Excel地址不一致，API地址：" + form.getApiCustomerAddress() + "，Excel地址：" + form.getCustomerAddress());
                    errorRecord.put("type", "addrNotMatch");
                    errorRecords.add(errorRecord);

                    // 拼接字段
                    if (!form.getCustomerAddress().contains("_")) {
                        form.setCustomerAddress(form.getCustomerAddress() + "_" + form.getApiCustomerAddress());
                        form.setLongitude(form.getLongitude() + "_" + form.getApiLongitude());
                        form.setLatitude(form.getLatitude() + "_" + form.getApiLatitude());
                    }

                    // 移除formalList，加入tempList
                    iterator.remove();
                    tempList.add(form);
                    successCount--;
                    tempCount++;
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
                excelImportMapper.saveMarkTempToProjectClueE(sUserId);

                //导入数据写入缓存表
                excelImportMapper.saveMarkTempToProjectClueCache(sUserId);


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
            // 对错误记录按行号排序
            errorRecords.sort(Comparator.comparingInt(e -> (Integer) e.get("rowNum")));
            resultMap.put("errorRecords", errorRecords);
            // 在构建 resultMap 后，返回结果前添加
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            // 将耗时转换为秒，保留两位小数
            double durationInSeconds = Math.round(duration / 10.0) / 100.0;
            resultMap.put("duration", durationInSeconds);
            // 记录导入结果日志
            String result = String.format("导入完成，总数: %d, 成功导入正式表: %d, 导入临时表: %d, 失败: %d, 耗时: %.2f秒",
                    totalCount, successCount, tempCount, errorCount, durationInSeconds);

            if (noProName.size() > 0) {
                String pro = ", 以下项目未查询到：" + StringUtils.join(noProName, ",");
                result = result + pro;
            }
            if (noUser.size() > 0) {
                String pro = ", 以下人员未查询到：" + StringUtils.join(noUser, ",");
                result = result + pro;
            }

            SysLog sysLog0 = new SysLog();
            sysLog0.setMyUUID(startTimeString);
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("批量导入地图客户历史数据任务结束");
            sysLog0.setNote(result + "-人员记录-操作人：" + sUserName + "-操作人账号：" + sUserId);
            excelImportMapper.insertLogsWithID(sysLog0);

            return ResultBody.success(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            // 记录导入失败日志
            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("批量导入地图客户历史数据任务失败");
            sysLog0.setNote("导入失败-操作人：" + sUserName + "-操作人账号：" + sUserId + "-错误信息：" + e.getMessage());
            excelImportMapper.insertLogs(sysLog0);
            return ResultBody.error(-1001, "数据有误 导入失败 请检查！");
        }
    }


    private void updateCustomerStatusCacheRecent(String userId) {
        logger.info("开始执行客户状态缓存更新任务");
        long startTime = System.currentTimeMillis();

        try {
            // 查询 flag 以 '_1' 结尾的数据
            Map<String, Object> p = new java.util.HashMap<>();
            p.put("userId", userId);
            List<Map> flagEndWith1List = custMapDao.selectFlagEndWith1AndUserId(p);

            logger.info("flag 以 '_1' 结尾的数据条数: {}", flagEndWith1List.size());

            for (Map item : flagEndWith1List) {
                Object customerName = item.get("CustomerName");
                Object projectId = item.get("projectId");
                if (customerName == null || projectId == null) continue;
                Map<String, Object> param = new java.util.HashMap<>();
                param.put("customerName", customerName);
                param.put("projectId", projectId);
                List<Map> listOpportunityCache = custMapDao.selectOpportunityCacheByNameAndProjectId(param);
                List<Map> listCluesCache = custMapDao.selectCluesCacheByNameAndProjectId(param);
                logger.info("CustomerName: {}, projectId: {}, opportunityCache size: {}, cluesCache size: {}", customerName, projectId, listOpportunityCache.size(), listCluesCache.size());

                Map<String, List<Map>> oppCacheByMapType = listOpportunityCache.stream()
                        .filter(e -> e.get("mapType") != null)
                        .collect(Collectors.groupingBy(e -> String.valueOf(e.get("mapType"))));
                Map<String, List<Map>> cluesCacheByMapType = listCluesCache.stream()
                        .filter(e -> e.get("mapType") != null)
                        .collect(Collectors.groupingBy(e -> String.valueOf(e.get("mapType"))));

                Object opportunityClueId = item.get("OpportunityClueId");
                Object projectClueId = item.get("ProjectClueId");

                for (String mapType : new String[]{"1", "2"}) {
                    List<Map> oppListForType = oppCacheByMapType.get(mapType);
                    List<Map> cluesListForType = cluesCacheByMapType.get(mapType);
                    Map<String, Integer> weightMap = "1".equals(mapType) ? STATUS_TYPE_WEIGHT_MAPTYPE1 : STATUS_TYPE_WEIGHT_MAPTYPE2;

                    // 机会缓存表
                    String maxStatusType = null;
                    int maxWeight = Integer.MIN_VALUE;
                    String currentStatusType = null;
                    if (opportunityClueId != null && oppListForType != null && !oppListForType.isEmpty()) {
                        for (Map cacheItem : oppListForType) {
                            String statusType = String.valueOf(cacheItem.get("StatusType"));
                            int weight = weightMap.getOrDefault(statusType, 0);
                            if (weight > maxWeight) {
                                maxWeight = weight;
                                maxStatusType = statusType;
                            }
                            if (opportunityClueId.equals(cacheItem.get("OpportunityClueId"))) {
                                currentStatusType = statusType;
                            }
                        }
                        if (maxStatusType != null && currentStatusType != null && !maxStatusType.equals(currentStatusType)) {
                            custMapDao.updateOpportunityCacheStatusTypeByIdAndMapType(String.valueOf(opportunityClueId), maxStatusType, mapType);
                            logger.info("已更新StatusType: {}，mapType: {}，OpportunityClueId: {}，ProjectClueId: {}", maxStatusType, mapType, opportunityClueId, projectClueId);
                        }
                    }

                    // 线索缓存表
                    maxStatusType = null;
                    maxWeight = Integer.MIN_VALUE;
                    currentStatusType = null;
                    if (projectClueId != null && cluesListForType != null && !cluesListForType.isEmpty()) {
                        for (Map cacheItem : cluesListForType) {
                            String statusType = String.valueOf(cacheItem.get("StatusType"));
                            int weight = weightMap.getOrDefault(statusType, 0);
                            if (weight > maxWeight) {
                                maxWeight = weight;
                                maxStatusType = statusType;
                            }
                            if (projectClueId.equals(cacheItem.get("ProjectClueId"))) {
                                currentStatusType = statusType;
                            }
                        }
                        if (maxStatusType != null && currentStatusType != null && !maxStatusType.equals(currentStatusType)) {
                            custMapDao.updateCluesCacheStatusTypeByIdAndMapType(String.valueOf(projectClueId), maxStatusType, mapType);
                            logger.info("已更新StatusType: {}，mapType: {}，OpportunityClueId: {}，ProjectClueId: {}", maxStatusType, mapType, opportunityClueId, projectClueId);
                        }
                    }
                }
            }

            long endTime2 = System.currentTimeMillis();
            logger.info("更新线索缓存表耗时：" + (endTime2 - endTime2) / 1000 + "s");


        } catch (Exception e) {
            logger.error("客户状态缓存更新失败", e);
            throw e;
        }
    }

    /**
     * 更新临时表中客户地址信息并导入正式表
     *
     * @param customerAddressList 客户地址信息列表
     * @return 处理结果
     */
    @Log("更新客户地址信息并导入正式表")
    @ResponseBody
    @RequestMapping(value = "/updateCustomerAddressNew", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "更新客户地址信息并导入正式表", notes = "更新临时表中的客户地址、经纬度信息，并将数据导入正式表")
    public ResultBody updateCustomerAddressNew(@RequestBody List<Map<String, Object>> customerAddressList) {
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
                excelImportMapper.saveMarkTempToProjectClueE(sUserId);

                //导入数据写入缓存表
                excelImportMapper.saveMarkTempToProjectClueCache(sUserId);


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

    @Log("更新地图导入历史数据的图片地址")
    @ResponseBody
    @RequestMapping(value = "/updateHistoryCustomerEnclosureUrl", method = RequestMethod.GET)
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "更新地图导入历史数据的图片地址", notes = "更新地图导入历史数据的图片地址")
    public ResultBody updateHistoryCustomerEnclosureUrl() {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sUserId = SecurityUtils.getUserId();
        String sUserName = SecurityUtils.getUsername();

        // 记录任务开始日志
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("更新地图导入历史数据图片地址任务开始");
        sysLog.setNote("人员记录-操作人：" + sUserName + "-操作人id：" + sUserId);
        excelImportMapper.insertLogs(sysLog);

        try {
            // 查询需要更新的附件记录
            List<Map<String, Object>> enclosures = excelImportMapper.getHistoryCustomerEnclosures();

            int totalCount = enclosures.size();
            int successCount = 0;
            int errorCount = 0;
            List<Map<String, Object>> errorRecords = new ArrayList<>();

            for (Map<String, Object> enclosure : enclosures) {
                try {
                    String oldUrl = (String) enclosure.get("enclosureUrl");
                    String enclosureName = (String) enclosure.get("enclosureName");
                    // 如果文件名为空，从URL中提取文件名
                    if (StringUtils.isEmpty(enclosureName)) {
                        // 从URL中提取文件名
                        String[] urlParts = oldUrl.split("/");
                        if (urlParts.length > 0) {
                            enclosureName = urlParts[urlParts.length - 1];
                            // 如果文件名仍然为空，使用时间戳作为文件名
                            if (StringUtils.isEmpty(enclosureName)) {
                                enclosureName = "file_" + System.currentTimeMillis() + ".jpg";
                            }
                        } else {
                            enclosureName = "file_" + System.currentTimeMillis() + ".jpg";
                        }
                    }
                    Integer id = (Integer) enclosure.get("id");

                    if (StringUtils.isEmpty(oldUrl)) {
                        continue;
                    }

                    // 下载原文件
                    byte[] fileBytes = downloadFile(oldUrl);
                    if (fileBytes == null) {
                        throw new Exception("下载文件失败: " + oldUrl);
                    }

                    // 创建临时文件
                    File tempFile = File.createTempFile("temp_", "_" + enclosureName);
                    try {
                        // 写入临时文件
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        fos.write(fileBytes);
                        fos.close();

                        // 创建DiskFileItem
                        DiskFileItemFactory factory = new DiskFileItemFactory();
                        DiskFileItem fileItem = (DiskFileItem) factory.createItem(
                                "file",
                                "application/octet-stream",
                                false,
                                enclosureName
                        );

                        // 写入文件内容
                        try (InputStream input = new ByteArrayInputStream(fileBytes)) {
                            IOUtils.copy(input, fileItem.getOutputStream());
                        }

                        // 创建CommonsMultipartFile
                        CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);

                        // 调用上传接口
                        String newUrl = fileUploadService.uploadFileWebAll(multipartFile, enclosureName, "activity");
                        if (StringUtils.isEmpty(newUrl) || "E".equals(newUrl)) {
                            throw new Exception("上传文件失败");
                        }

                        // 更新附件表
                        Map<String, Object> updateParams = new HashMap<>();
                        updateParams.put("id", id);
                        updateParams.put("enclosureUrl", "https://ydac.vanyang.com.cn/image/activity/" + newUrl);
                        excelImportMapper.updateEnclosureUrl(updateParams);

                        successCount++;
                    } finally {
                        // 删除临时文件
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                    }
                } catch (Exception e) {
                    errorCount++;
                    Map<String, Object> errorRecord = new HashMap<>();
                    errorRecord.put("id", enclosure.get("id"));
                    errorRecord.put("oldUrl", enclosure.get("enclosureUrl"));
                    errorRecord.put("error", e.getMessage());
                    errorRecords.add(errorRecord);
                }
            }
            // 在构建 resultMap 后，返回结果前添加
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            // 将耗时转换为秒，保留两位小数
            double durationInSeconds = Math.round(duration / 10.0) / 100.0;

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("duration", durationInSeconds);
            resultMap.put("totalCount", totalCount);
            resultMap.put("successCount", successCount);
            resultMap.put("errorCount", errorCount);
            resultMap.put("errorRecords", errorRecords);

            // 记录任务完成日志
            String result = String.format("更新完成，总数: %d, 成功: %d, 失败: %d,耗时: %.2f秒",
                    totalCount, successCount, errorCount, durationInSeconds);

            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("更新地图导入历史数据图片地址任务结束");
            sysLog0.setNote(result + "-人员记录-操作人：" + sUserName + "-操作人账号：" + sUserId);
            excelImportMapper.insertLogs(sysLog0);

            return ResultBody.success(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            // 记录任务失败日志
            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("更新地图导入历史数据图片地址任务失败");
            sysLog0.setNote("更新失败-操作人：" + sUserName + "-操作人账号：" + sUserId + "-错误信息：" + e.getMessage());
            excelImportMapper.insertLogs(sysLog0);
            return ResultBody.error(-1001, "更新失败，请检查！");
        }
    }

    @Log("更新地图导入历史数据的图片地址任务")
    @ResponseBody
    @RequestMapping(value = "/updateHistoryCustomerEnclosureUrlTask", method = RequestMethod.GET)
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "更新地图导入历史数据的图片地址", notes = "更新地图导入历史数据的图片地址")
    @Scheduled(cron = "0 0/5 22-23,0-8 * * ?")
    public ResultBody updateHistoryCustomerEnclosureUrlTask() {
        long startTime = System.currentTimeMillis();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sUserId = "task";
        String sUserName = "定时任务";

        // 记录任务开始日志
        SysLog sysLog = new SysLog();
        sysLog.setStartTime(sf.format(new Date()));
        sysLog.setTaskName("更新地图导入历史数据图片地址任务开始");
        sysLog.setNote("人员记录-操作人：" + sUserName + "-操作人id：" + sUserId);
        excelImportMapper.insertLogs(sysLog);

        try {
            // 查询需要更新的附件记录
            List<Map<String, Object>> enclosures = excelImportMapper.getHistoryCustomerEnclosures();

            int totalCount = enclosures.size();
            int successCount = 0;
            int errorCount = 0;
            List<Map<String, Object>> errorRecords = new ArrayList<>();

            for (Map<String, Object> enclosure : enclosures) {
                try {
                    String oldUrl = (String) enclosure.get("enclosureUrl");
                    String enclosureName = (String) enclosure.get("enclosureName");
                    // 如果文件名为空，从URL中提取文件名
                    if (StringUtils.isEmpty(enclosureName)) {
                        // 从URL中提取文件名
                        String[] urlParts = oldUrl.split("/");
                        if (urlParts.length > 0) {
                            enclosureName = urlParts[urlParts.length - 1];
                            // 如果文件名仍然为空，使用时间戳作为文件名
                            if (StringUtils.isEmpty(enclosureName)) {
                                enclosureName = "file_" + System.currentTimeMillis() + ".jpg";
                            }
                        } else {
                            enclosureName = "file_" + System.currentTimeMillis() + ".jpg";
                        }
                    }
                    Integer id = (Integer) enclosure.get("id");

                    if (StringUtils.isEmpty(oldUrl)) {
                        continue;
                    }

                    // 下载原文件
                    byte[] fileBytes = downloadFile(oldUrl);
                    if (fileBytes == null) {
                        throw new Exception("下载文件失败: " + oldUrl);
                    }

                    // 创建临时文件
                    File tempFile = File.createTempFile("temp_", "_" + enclosureName);
                    try {
                        // 写入临时文件
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        fos.write(fileBytes);
                        fos.close();

                        // 创建DiskFileItem
                        DiskFileItemFactory factory = new DiskFileItemFactory();
                        DiskFileItem fileItem = (DiskFileItem) factory.createItem(
                                "file",
                                "application/octet-stream",
                                false,
                                enclosureName
                        );

                        // 写入文件内容
                        try (InputStream input = new ByteArrayInputStream(fileBytes)) {
                            IOUtils.copy(input, fileItem.getOutputStream());
                        }

                        // 创建CommonsMultipartFile
                        CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);

                        // 调用上传接口
                        String newUrl = fileUploadService.uploadFileWebAll(multipartFile, enclosureName, "activity");
                        if (StringUtils.isEmpty(newUrl) || "E".equals(newUrl)) {
                            throw new Exception("上传文件失败");
                        }

                        // 更新附件表
                        Map<String, Object> updateParams = new HashMap<>();
                        updateParams.put("id", id);
                        updateParams.put("enclosureUrl", "https://ydac.vanyang.com.cn/image/activity/" + newUrl);
                        excelImportMapper.updateEnclosureUrl(updateParams);

                        successCount++;
                    } finally {
                        // 删除临时文件
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                    }
                } catch (Exception e) {
                    errorCount++;
                    Map<String, Object> errorRecord = new HashMap<>();
                    errorRecord.put("id", enclosure.get("id"));
                    errorRecord.put("oldUrl", enclosure.get("enclosureUrl"));
                    errorRecord.put("error", e.getMessage());
                    errorRecords.add(errorRecord);
                }
            }
            // 在构建 resultMap 后，返回结果前添加
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            // 将耗时转换为秒，保留两位小数
            double durationInSeconds = Math.round(duration / 10.0) / 100.0;
            // 构建结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("duration", durationInSeconds);
            resultMap.put("totalCount", totalCount);
            resultMap.put("successCount", successCount);
            resultMap.put("errorCount", errorCount);
            resultMap.put("errorRecords", errorRecords);

            // 记录任务完成日志
            String result = String.format("更新完成，总数: %d, 成功: %d, 失败: %d,耗时: %.2f秒",
                    totalCount, successCount, errorCount, durationInSeconds);

            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("更新地图导入历史数据图片地址任务结束");
            sysLog0.setNote(result + "-人员记录-操作人：" + sUserName + "-操作人账号：" + sUserId);
            excelImportMapper.insertLogs(sysLog0);

            return ResultBody.success(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            // 记录任务失败日志
            SysLog sysLog0 = new SysLog();
            sysLog0.setExecutTime(sf.format(new Date()));
            sysLog0.setTaskName("更新地图导入历史数据图片地址任务失败");
            sysLog0.setNote("更新失败-操作人：" + sUserName + "-操作人账号：" + sUserId + "-错误信息：" + e.getMessage());
            excelImportMapper.insertLogs(sysLog0);
            return ResultBody.error(-1001, "更新失败，请检查！");
        }
    }

    /**
     * 严格验证日期格式，防止类似"20205/6/4"这样的非法日期通过
     */
    private Date parseDateWithStrictValidation(String dateStr) {
        // 定义支持的日期格式（包含时分秒）
        String[] formats = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy.MM.dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy/MM/dd HH:mm",
                "yyyy.MM.dd HH:mm",
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "yyyy.MM.dd"
        };

        for (String format : formats) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false); // 严格解析，防止非法日期

            try {
                Date date = sdf.parse(dateStr);

                // 额外验证：确保解析后的日期格式与原始输入一致
                String formattedDate = sdf.format(date);

                // 额外检查：确保年份是合理的（例如：1900-2100之间）
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                if (year < 1900 || year > 2100) {
                    return null; // 年份不合理，返回验证失败
                }

                // 额外检查：确保解析后的字符串与原始输入匹配（处理可能的填充问题）
                if (isDateStringMatching(dateStr, formattedDate, format)) {
                    return date;
                }
            } catch (ParseException e) {
                // 继续尝试下一个格式
            }
        }
        return null; // 所有格式都无法解析
    }

    /**
     * 检查原始日期字符串与格式化后的字符串是否匹配
     */
    private boolean isDateStringMatching(String original, String formatted, String format) {
        // 移除所有非数字字符（处理不同分隔符的情况）
        String originalDigits = original.replaceAll("\\D+", "");
        String formattedDigits = formatted.replaceAll("\\D+", "");

        // 确保数字部分匹配
        return originalDigits.equals(formattedDigits);
    }

    /**
     * 下载文件
     *
     * @param url 文件URL
     * @return 文件字节数组
     */
    private byte[] downloadFile(String url) {
        try {
            URL fileUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析地址冲突并批量入库
     *
     * @param choiceList key: projectClueId, value: addressSource (1:api, 2:excel)
     */
    @Log("解析地址冲突并批量入库")
    @PostMapping("/resolveAddressConflict")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "解析地址冲突并批量入库", notes = "解析地址冲突并批量入库")
    public ResultBody resolveAddressConflict(@RequestBody List<Map<String, Object>> choiceList) {
        String sUserId = SecurityUtils.getUserId();
        // 转换为Map
        Map<String, Integer> choiceMap = new HashMap<>();
        for (Map<String, Object> choice : choiceList) {
            String projectClueId = (String) choice.get("projectClueId");
            Integer addressSource = Integer.valueOf((String) choice.get("addressSource"));
            choiceMap.put(projectClueId, addressSource);
        }

        // 后续逻辑同前
        ReportCustomerForm queryForm = new ReportCustomerForm();
        queryForm.setUserId(sUserId);
        List<ReportCustomerForm> tempList = excelImportMapper.getZsdtdrClueTempList(queryForm);
        for (ReportCustomerForm form : tempList) {
            Integer addressSource = choiceMap.get(form.getProjectClueId());
            if (addressSource != null) {
                form.setCustomerAddress(getAddressBySource(form.getCustomerAddress(), addressSource));
                form.setLongitude(getAddressBySource(form.getLongitude(), addressSource));
                form.setLatitude(getAddressBySource(form.getLatitude(), addressSource));
            }
        }
        // 批量入库
        // 先清空临时表
        excelImportMapper.delProhectClueMarkTemp(sUserId);
        excelImportMapper.saveProhectClueMarkTemp(tempList, sUserId);
        excelImportMapper.saveMarkTempToProjectClueA(sUserId);
        excelImportMapper.saveMarkTempToProjectClueB(sUserId);
        excelImportMapper.saveMarkTempToProjectClueC(sUserId);
        excelImportMapper.saveMarkTempToProjectClueD(sUserId);
        excelImportMapper.saveMarkTempToProjectClueE(sUserId);
        excelImportMapper.saveMarkTempToProjectClueCache(sUserId);
        excelImportMapper.delProhectClueMarkTemp(sUserId);
        return ResultBody.success("更新成功！");
    }

    /**
     * 工具方法：根据来源拆分字段
     */
    private String getAddressBySource(String value, int source) {
        if (value == null) return "";
        String[] arr = value.split("_", 2);
        if (arr.length == 2) {
            return source == 1 ? arr[1] : arr[0];
        } else {
            return value;
        }
    }

    /**
     * 检查客户地址与API地址是否一致
     *
     * @param customerList 客户信息列表
     * @return 地址不一致的客户列表
     */
    @Log("检查客户地址与API地址是否一致")
    @PostMapping("/checkCustomerAddress")
    @ResponseBody
    @ApiOperation(value = "检查客户地址与API地址是否一致", notes = "通过客户名称获取API地址，与传入的地址进行对比")
    public ResultBody checkCustomerAddress(@RequestBody List<Map<String, Object>> customerList) {
        try {
            if (customerList == null || customerList.isEmpty()) {
                return ResultBody.error(-1001, "客户列表不能为空");
            }

            // 将前端传入的数据转换为ReportCustomerForm对象列表
            List<ReportCustomerForm> reportCustomerForms = new ArrayList<>();
            for (Map<String, Object> customer : customerList) {
                ReportCustomerForm form = new ReportCustomerForm();
                form.setCustomerName((String) customer.get("customerName"));
                form.setCustomerAddress((String) customer.get("customerAddress"));
                form.setLongitude((String) customer.get("longitude"));
                form.setLatitude((String) customer.get("latitude"));
                // 设置其他必要字段
                form.setBusinessId((String) customer.get("businessId"));
                reportCustomerForms.add(form);
            }

            // 调用批量地址获取方法
            BatchGeocodingUtil.batchNameToAddress(reportCustomerForms, 300);

            // 对比地址，找出不一致的数据
            List<Map<String, Object>> inconsistentData = new ArrayList<>();
            for (ReportCustomerForm form : reportCustomerForms) {
                // 检查API是否返回了地址信息
                if (form.getApiCustomerAddress() != null && !form.getApiCustomerAddress().isEmpty()) {
                    // 对比API地址与传入地址
                    if (!form.getApiCustomerAddress().equals(form.getCustomerAddress())) {
                        Map<String, Object> inconsistentRecord = new HashMap<>();
                        inconsistentRecord.put("businessId", form.getBusinessId());
                        inconsistentRecord.put("customerName", form.getCustomerName());
                        inconsistentRecord.put("originalAddress", form.getCustomerAddress());
                        inconsistentRecord.put("apiAddress", form.getApiCustomerAddress());
                        inconsistentRecord.put("originalLongitude", form.getLongitude());
                        inconsistentRecord.put("apiLongitude", form.getApiLongitude());
                        inconsistentRecord.put("originalLatitude", form.getLatitude());
                        inconsistentRecord.put("apiLatitude", form.getApiLatitude());

                        // 添加对比结果说明
                        inconsistentRecord.put("description", "API地址与原始地址不一致");

                        inconsistentData.add(inconsistentRecord);
                    }
                } else {
                    // API没有返回地址信息的情况
                    Map<String, Object> noApiData = new HashMap<>();
                    noApiData.put("businessId", form.getBusinessId());
                    noApiData.put("customerName", form.getCustomerName());
                    noApiData.put("originalAddress", form.getCustomerAddress());
                    noApiData.put("apiAddress", "");
                    noApiData.put("originalLongitude", form.getLongitude());
                    noApiData.put("apiLongitude", "");
                    noApiData.put("originalLatitude", form.getLatitude());
                    noApiData.put("apiLatitude", "");
                    noApiData.put("description", "API未返回地址信息");

                    inconsistentData.add(noApiData);
                }
            }

            // 构建返回结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("totalCount", customerList.size());
            resultMap.put("inconsistentCount", inconsistentData.size());
            resultMap.put("inconsistentData", inconsistentData);
            resultMap.put("consistentCount", customerList.size() - inconsistentData.size());

            return ResultBody.success(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1001, "检查地址时发生错误: " + e.getMessage());
        }
    }

    /**
     * 更新客户地址信息
     *
     * @param addressUpdateList 地址更新列表
     * @return 更新结果
     */
    @Log("根据用户选择更新线索表或机会表的地址信息")
    @PostMapping("/updateCustomerAddressInfo")
    @ResponseBody
    @ApiOperation(value = "更新客户地址信息", notes = "根据用户选择更新线索表或机会表的地址信息")
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateCustomerAddressInfo(@RequestBody List<Map<String, Object>> addressUpdateList) {
        try {
            if (addressUpdateList == null || addressUpdateList.isEmpty()) {
                return ResultBody.error(-1001, "更新列表不能为空");
            }

            String sUserId = SecurityUtils.getUserId();
            String sUserName = SecurityUtils.getUsername();

            // 分类数据：线索表和机会表
            List<Map<String, Object>> cluesUpdateList = new ArrayList<>();
            List<Map<String, Object>> opportunityUpdateList = new ArrayList<>();

            // 统计信息
            int totalCount = addressUpdateList.size();
            int cluesCount = 0;
            int opportunityCount = 0;
            int errorCount = 0;
            List<String> errorMessages = new ArrayList<>();

            // 分类处理数据
            for (Map<String, Object> updateInfo : addressUpdateList) {
                try {
                    String dataType = (String) updateInfo.get("dataType"); // "clue" 或 "opportunity"
                    String businessId = (String) updateInfo.get("businessId"); // ProjectClueId 或 OpportunityClueId
                    String customerAddress = (String) updateInfo.get("customerAddress");
                    String longitude = (String) updateInfo.get("longitude");
                    String latitude = (String) updateInfo.get("latitude");

                    // 验证必要参数
                    if (StringUtils.isEmpty(businessId)) {
                        errorMessages.add("业务ID不能为空");
                        errorCount++;
                        continue;
                    }

                    if (StringUtils.isEmpty(customerAddress) && StringUtils.isEmpty(longitude) && StringUtils.isEmpty(latitude)) {
                        errorMessages.add("地址、经度、纬度至少需要提供一个");
                        errorCount++;
                        continue;
                    }

                    // 构建更新参数
                    Map<String, Object> updateParams = new HashMap<>();
                    updateParams.put("businessId", businessId);
                    updateParams.put("customerAddress", customerAddress);
                    updateParams.put("longitude", longitude);
                    updateParams.put("latitude", latitude);
                    updateParams.put("updateUserId", sUserId);
                    updateParams.put("updateUserName", sUserName);

                    // 根据数据类型分类
                    if ("clue".equals(dataType)) {
                        cluesUpdateList.add(updateParams);
                        cluesCount++;
                    } else if ("opportunity".equals(dataType)) {
                        opportunityUpdateList.add(updateParams);
                        opportunityCount++;
                    } else {
                        errorMessages.add("数据类型不正确，只能是 'clue' 或 'opportunity'");
                        errorCount++;
                    }

                } catch (Exception e) {
                    errorCount++;
                    errorMessages.add("处理数据时发生异常: " + e.getMessage());
                }
            }

            // 批量更新线索表
            int cluesUpdateCount = 0;
            if (!cluesUpdateList.isEmpty()) {
                try {
                    cluesUpdateCount = projectCluesDao.batchUpdateCluesAddress(cluesUpdateList);
                } catch (Exception e) {
                    errorMessages.add("更新线索表失败: " + e.getMessage());
                    errorCount += cluesUpdateList.size();
                }
            }

            // 批量更新机会表
            int opportunityUpdateCount = 0;
            if (!opportunityUpdateList.isEmpty()) {
                try {
                    opportunityUpdateCount = projectCluesDao.batchUpdateOpportunityAddress(opportunityUpdateList);
                } catch (Exception e) {
                    errorMessages.add("更新机会表失败: " + e.getMessage());
                    errorCount += opportunityUpdateList.size();
                }
            }

            // 构建返回结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("totalCount", totalCount);
            resultMap.put("cluesCount", cluesCount);
            resultMap.put("opportunityCount", opportunityCount);
            resultMap.put("cluesUpdateCount", cluesUpdateCount);
            resultMap.put("opportunityUpdateCount", opportunityUpdateCount);
            resultMap.put("errorCount", errorCount);
            resultMap.put("errorMessages", errorMessages);
            resultMap.put("successCount", cluesUpdateCount + opportunityUpdateCount);

            // 记录操作日志
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String logMessage = String.format("批量更新客户地址信息，总数: %d, 线索更新: %d, 机会更新: %d, 失败: %d",
                    totalCount, cluesUpdateCount, opportunityUpdateCount, errorCount);

            SysLog sysLog = new SysLog();
            sysLog.setExecutTime(sf.format(new Date()));
            sysLog.setTaskName("批量更新客户地址信息");
            sysLog.setNote(logMessage + "-操作人：" + sUserName + "-操作人账号：" + sUserId);
            excelImportMapper.insertLogs(sysLog);

            return ResultBody.success(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultBody.error(-1001, "更新地址信息时发生错误: " + e.getMessage());
        }
    }

    /**
     * 查询地图导入客户历史记录
     */
    @Log("查询地图导入客户历史记录")
    @PostMapping("/queryMapImportCustomerHistory")
    @ResponseBody
    @ApiOperation(value = "查询地图导入客户历史记录", notes = "查询地图导入客户历史记录")
    public ResultBody queryMapImportCustomerHistory(@RequestBody Map<String, Object> paramMap) {
        return excelImportService.queryMapImportCustomerHistory(paramMap);
    }

    /**
     * 根据导入历史记录删除导入数据
     */
    @Log("根据导入历史记录删除导入数据")
    @PostMapping("/deleteMapImportData")
    @ResponseBody
    @ApiOperation(value = "根据导入历史记录删除导入数据", notes = "根据导入历史记录删除导入数据")
    public ResultBody deleteMapImportData(@RequestBody Map<String, Object> paramMap) {
        return excelImportService.deleteMapImportData(paramMap);
    }


    @Log("查询客户联系方式")
    @PostMapping("/queryCluesContacts")
    @ApiOperation(value = "查询客户联系方式", notes = "查询客户联系方式")
    public ResultBody queryCluesContacts(@RequestBody Map<String, Object> paramMap) {
        // 1. 初始化基础参数
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userId = request.getHeader("userid");
        if (org.apache.commons.lang3.StringUtils.isEmpty(userId)) {
            userId = SecurityUtils.getUserId();
        }

        try {
            // 2. 参数验证
            String projectClueId = (String) paramMap.get("projectClueId");
            if (StringUtils.isEmpty(projectClueId)) {
                return ResultBody.error(-10001, "线索ID不能为空");
            }

            // 3. 调用service查询
            List<Map<String, Object>> contactsList = excelImportService.queryCluesContacts(projectClueId);

            return ResultBody.success(contactsList);
        } catch (Exception e) {
            //log.error("查询客户联系方式失败", e);
            return ResultBody.error(-10002, "查询客户联系方式失败" + e.getMessage());
        }
    }
}
