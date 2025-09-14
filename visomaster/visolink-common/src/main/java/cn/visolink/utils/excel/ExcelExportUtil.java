package cn.visolink.utils.excel;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.visolink.utils.PathUtil;
import cn.visolink.utils.SizeUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 利用开源组件POI3.0.2动态导出EXCEL文档
 *
 * @author yangyz01
 * @version v1.0
 */
public class ExcelExportUtil {

    public void exportExcelXls(String title, String[] headers, ArrayList<Object[]> dataset, HttpServletResponse response) throws IOException{
        String filename = title + "_" + DateUtil.formatDateTime(new Date());
        exportExcelXls(title, headers, dataset, filename,response,"yyyy-MM-dd hh:mm:ss");
    }

    public void exportExcelXls(String title, String[] headers, ArrayList<Object[]> dataset, String filename, HttpServletResponse response) throws IOException{
        exportExcelXls(title, headers, dataset, filename,response,"yyyy-MM-dd hh:mm:ss");
    }


    public void exportExcelXls(String title, String[] headers, ArrayList<Object[]> dataset, String filename, HttpServletResponse response, String pattern) throws IOException{
        exportExcel(-1,0, ExcelDefined.EXCEL_TYPE_2003_XLS,title, headers, dataset, filename,response, pattern,null);
    }

    public void exportExcel(String title, String[] headers, ArrayList<Object[]> dataset, HttpServletResponse response) throws IOException{
        String filename = title + "_" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        exportExcel(title, headers, dataset, filename,response,"yyyy-MM-dd hh:mm:ss");
    }

    public void exportExcel(String title, String[] headers, ArrayList<Object[]> dataset, String filename, HttpServletResponse response) throws IOException{
        exportExcel(title, headers, dataset, filename,response,"yyyy-MM-dd hh:mm:ss");
    }


    public void exportExcel(String title, String[] headers, ArrayList<Object[]> dataset, String filename, HttpServletResponse response, String pattern) throws IOException{
        int line = 1;
        if (pattern!=null && !"".equals(pattern)){
            line = 2;
        }
        exportExcel(line,0, ExcelDefined.EXCEL_TYPE_2007_XLSX,title, headers, dataset, filename,response, pattern,null);
    }

    public void exportExcel2(String title, String[] headers, ArrayList<Object[]> dataset, String filename, HttpServletResponse response, String pattern) throws IOException{
        int line = 1;
        if (pattern!=null && !"".equals(pattern)){
            line = 2;
        }
        exportExcel2(line,0, ExcelDefined.EXCEL_TYPE_2007_XLSX,title, headers, dataset, filename,response, pattern,null);
    }

    public void exportExcel3(int line,String title, String[] headers, ArrayList<Object[]> dataset, String filename, HttpServletResponse response, String pattern) throws IOException{
        exportExcel(line,0, ExcelDefined.EXCEL_TYPE_2007_XLSX,title, headers, dataset, filename,response, pattern,null);
    }

    public String exportExcelCreate(String title, String[] headers, ArrayList<Object[]> dataset,String pattern,String url) throws IOException {
        ExcelExportParam param = new ExcelExportParam();
        int line = 1;
        if (pattern!=null && !"".equals(pattern)){
            line = 2;
        }
        param.setLine(line);
        param.setWidth(0);
        param.setExcelType(ExcelDefined.EXCEL_TYPE_2007_XLSX);
        param.setSheetName(title);
        param.setFileName(UUID.randomUUID().toString());
        param.setDataPattern(pattern);
        param.setTemplate(null);
        String fileName = param.getFileName() + "." +  param.getExcelTypeDesc();
        FileOutputStream outputStream = new FileOutputStream(url+"/"+fileName);
        exportExcel2(param, headers, dataset, outputStream);
        outputStream.flush();
        outputStream.close();
        return fileName;
    }

    public void exportExcelTemplate(String template,  ArrayList<Object[]> dataset, String filename, HttpServletResponse response) throws IOException{
        exportExcelTemplate(template, dataset, filename,response,"yyyy-MM-dd hh:mm:ss");
    }


    public void exportExcelTemplate(String template,  ArrayList<Object[]> dataset, String filename, HttpServletResponse response, String pattern) throws IOException{
        exportExcel(1,0, ExcelDefined.EXCEL_TYPE_2007_XLSX,null, null, dataset, filename,response, pattern,template);
    }

    public void exportExcelTemplate(String template,  ArrayList<Object[]> dataset, String filename, HttpServletResponse response, String pattern,int line,int width) throws IOException{
        exportExcel(line,width, ExcelDefined.EXCEL_TYPE_2007_XLSX,null, null, dataset, filename,response, pattern,template);
    }

    private void exportExcel(int line,int width,int excelType, String title, String[] headers, ArrayList<Object[]> dataset, String filename, HttpServletResponse response, String pattern,String template) throws IOException {
        ExcelExportParam param = new ExcelExportParam();
        param.setLine(line);
        param.setWidth(width);
        if((dataset.size() + line) >= ExcelDefined.EXCEL2003_MAX_LINE){
            excelType = ExcelDefined.EXCEL_TYPE_2007_XLSX;
        }
        param.setExcelType(excelType);
        param.setSheetName(title);
        param.setFileName(filename);
        param.setDataPattern(pattern);
        param.setTemplate(template);
        exportExcel(param,headers,dataset,response);
    }

    private void exportExcel2(int line,int width,int excelType, String title, String[] headers, ArrayList<Object[]> dataset, String filename, HttpServletResponse response, String pattern,String template) throws IOException {
        ExcelExportParam param = new ExcelExportParam();
        param.setLine(line);
        param.setWidth(width);
        if((dataset.size() + line) >= ExcelDefined.EXCEL2003_MAX_LINE){
            excelType = ExcelDefined.EXCEL_TYPE_2007_XLSX;
        }
        param.setExcelType(excelType);
        param.setSheetName(title);
        param.setFileName(filename);
        param.setDataPattern(pattern);
        param.setTemplate(template);
        exportExcel2(param,headers,dataset,response);
    }

    /**
     * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
     *
     * @param param 导出表格参数
     * @param headers 表格属性列名数组
     * @param dataset 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param response     与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     */
    private void exportExcel(ExcelExportParam param, String[] headers, ArrayList<Object[]> dataset, HttpServletResponse response) throws IOException {
        String fileName = param.getFileName() + "." +  param.getExcelTypeDesc();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName , "utf-8"));
        OutputStream outputStream = response.getOutputStream();
        exportExcel(param, headers, dataset, outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 修改支持导出子类和折叠
     *
     * @param param 导出表格参数
     * @param headers 表格属性列名数组
     * @param dataset 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param response     与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     */
    private void exportExcel2(ExcelExportParam param, String[] headers, ArrayList<Object[]> dataset, HttpServletResponse response) throws IOException {
        String fileName = param.getFileName() + "." +  param.getExcelTypeDesc();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName , "utf-8"));
        OutputStream outputStream = response.getOutputStream();
        exportExcel2(param, headers, dataset, outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 根据参数不同，返回不同的Workbook
     * @param param 导出参数
     * @param size 导出文件大小
     * @return
     */
    private Workbook getWorkbook(ExcelExportParam param, int size){
        Workbook webBook = null;
        InputStream templateis=null;
        if (!param.isUserTemplate()){
            SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
            sxssfWorkbook.setCompressTempFiles(true);
            webBook = sxssfWorkbook;
        }else{
            String template = PathUtil.getAppClassPath() + param.getTemplate();
//        String template = param.getTemplate();
            File templateFile = new File(template);
            if (!templateFile.exists()) {
                templateFile.mkdirs();
            }
            try {
                templateis = new FileInputStream(templateFile);

                if(param.isExcel2003()){
                    if(!param.isUserTemplate()){
                        webBook = new HSSFWorkbook();
                    }else{
                        webBook = new HSSFWorkbook(templateis);
                    }
                } else if (size < ExcelDefined.EXCEL_2007_OPTIMIZE_LINE) {
                    if(!param.isUserTemplate()){
                        webBook = new XSSFWorkbook();
                    }else{
                        webBook = new XSSFWorkbook(templateis);
                    }
                } else {
                    SXSSFWorkbook sxssfWorkbook = null;
                    if(!param.isUserTemplate()){
                        sxssfWorkbook = new SXSSFWorkbook();
                        sxssfWorkbook.setCompressTempFiles(true);
                    }else{
                        sxssfWorkbook = new SXSSFWorkbook(new XSSFWorkbook(templateis));
                        sxssfWorkbook.setCompressTempFiles(true);
                    }

                    webBook = sxssfWorkbook;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(templateis!=null){
                    try {
                        templateis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return webBook;
    }
    /**
     * 得到默认的标题格式
     *
     * @param workbook
     * @return
     */
    private CellStyle getCellStyle(Workbook workbook, boolean isTitle) {
        // 生成一个样式
        CellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        if (isTitle) {
            // 背景颜色
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        }
        // 生成一个字体
        Font font = workbook.createFont();
        //设置字体格式
        font.setColor(HSSFColor.BLACK.index);
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        if (isTitle) {
            font.setBold(true);
        }

        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 得到默认的标题格式
     *
     * @param workbook
     * @return
     */
    private CellStyle getCellStyle2(Workbook workbook, boolean isTitle) {
        // 生成一个样式
        CellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER); // 水平居中对齐
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中对齐

        if (isTitle) {
            // 背景颜色
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        }
        // 生成一个字体
        Font font = workbook.createFont();
        //设置字体格式
        font.setColor(HSSFColor.BLACK.index);
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        if (isTitle) {
            font.setBold(true);
        }

        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 得到文本字段的对象，如果是Excel 2003，那么返回HSSFRichTextString，否则返回 XSSFRichTextString
     * @param param 导出Excel参数
     * @param value 内容值
     * @return
     */
    private RichTextString getRichTextString(ExcelExportParam param,String value){
        if(param.isExcel2003()){
            return new HSSFRichTextString(value);
        } else {
            return new XSSFRichTextString(value);
        }
    }

    /**
     * HSSFClientAnchor对象
     * @param param
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     * @param col1
     * @param row1
     * @param col2
     * @param row2
     * @return
     */
    private ClientAnchor getClientAnchor(ExcelExportParam param,int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2){
        if(param.isExcel2003()){
            return new HSSFClientAnchor(dx1, dy1, dx2, dy2, (short)col1, row1, (short)col2, row2);
        } else {
            return new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1, col2, row2);
        }
    }
    /**
     * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
     *
     * @param param 导出表格参数
     * @param headers 表格属性列名数组
     * @param dataset 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param out     与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     */
    private void exportExcel(ExcelExportParam param, String[] headers, ArrayList<Object[]> dataset, OutputStream out) {
        Workbook workbook = this.getWorkbook(param,dataset.size());

        if(workbook==null){
            workbook=new HSSFWorkbook();
        }
        Sheet sheet = null;
        if(param.isUserTemplate()){
            sheet = workbook.getSheetAt(0);
        }else{
            sheet = createSheetAndWriteTitle(param, headers, workbook);
        }

        //得到内容列的样式
        CellStyle contentCellStyle = this.getCellStyle(workbook, false);

        Font textFont = workbook.createFont();
        textFont.setColor(HSSFColor.BLACK.index);
        textFont.setFontName("宋体");
//        System.out.println("workbook.memory1 = " + SizeUtils.getObjectSize(workbook));
        long startTime = System.currentTimeMillis();

        int rowLength = 0;
        if(param.isUserTemplate()){
            rowLength = dataset.get(0).length;
        }else{
            rowLength = headers.length;
        }

        Row row  = null;
        Cell cell = null;
        int index = param.getLine();

        for (int i = 0; i < dataset.size(); i++, index++) {
            row = sheet.createRow(index);
            Object[] data = dataset.get(i);
            int actualRowLength = data.length; // 使用当前行的实际长度

            for (int j = 0; j < actualRowLength; j++) {
                cell = row.createCell(j);
                cell.setCellStyle(contentCellStyle);
                Object value = data[j];
                String textValue = (value != null ? value.toString() : "");
                String text = textValue.replaceAll("\r|\n", "");
                cell.setCellValue(text);
            }
        }
//        if (param.getDataPattern() != null && !"".equals(param.getDataPattern())){
//            row = sheet.createRow(dataset.size()+1);
//            cell = row.createCell(0);
//            String textValue = (param.getDataPattern() != null ? param.getDataPattern() : "");
//            String text=textValue.replaceAll("\r|\n", "");
//            cell.setCellValue(text);
//            CellRangeAddress region = new CellRangeAddress(dataset.size()+1, dataset.size()+1, 0, rowLength);
//            sheet.addMergedRegion(region);
//        }
        long endTime = System.currentTimeMillis();
        System.out.println("耗费时间：" + (endTime - startTime));
        try {
//            System.out.println("workbook.memory = " + SizeUtils.getObjectSize(workbook));
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改支持导出子类和折叠
     *
     * @param param 导出表格参数
     * @param headers 表格属性列名数组
     * @param dataset 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *                javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param out     与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     */
    private void exportExcel2(ExcelExportParam param, String[] headers, ArrayList<Object[]> dataset, OutputStream out) {
        Workbook workbook = this.getWorkbook(param,dataset.size());

        if(workbook==null){
            workbook=new HSSFWorkbook();
        }
        Sheet sheet = null;
        if(param.isUserTemplate()){
            sheet = workbook.getSheetAt(0);
        }else{
            sheet = createSheetAndWriteTitle(param, headers, workbook);
        }

        //得到内容列的样式
        CellStyle contentCellStyle = this.getCellStyle2(workbook, false);

        Font textFont = workbook.createFont();
        textFont.setColor(HSSFColor.BLACK.index);
        textFont.setFontName("宋体");
//        System.out.println("workbook.memory1 = " + SizeUtils.getObjectSize(workbook));
        long startTime = System.currentTimeMillis();

        int rowLength = 0;
        if(param.isUserTemplate()){
            rowLength = dataset.get(0).length;
        }else{
            rowLength = headers.length;
        }

        Row row  = null;
        Cell cell = null;
        int index = param.getLine();
        List<Map> collapseList = new ArrayList<>();
        for (int i = 0; i < dataset.size(); i++) {
            row = sheet.createRow(index);
            Object[] data = dataset.get(i);
            for (int j = 0; j < rowLength; j++) {
                cell = row.createCell(j);
                cell.setCellStyle(contentCellStyle);
                Object value = data[j];
                String textValue = (value != null ? value.toString() : "");
                String text=textValue.replaceAll("\r|\n", "");
                cell.setCellValue(text);
            }
            index++;//父级视为新的一行
            Object childData = data[data.length-1];
            List<Map> childList = new ArrayList<>();
            if (childData instanceof List) {
                List<Map> child = (List<Map>) childData;
                if("ret".equals(child.get(0).get("type")+"")){
                    childList = child;
                }
            }
            if(CollectionUtils.isNotEmpty(childList)){
                int startRow = index;
                for (int ic = 0; ic < childList.size(); ic++,index++) {//子级视为新的一行
                    row = sheet.createRow(index);
                    Map datac = childList.get(ic);
                    for (int j = 0; j < rowLength; j++) {
                        cell = row.createCell(j);
                        cell.setCellStyle(contentCellStyle);
                        Object value = "";
                        if(j == 22){//报备时间 22
                            value = datac.get("reportCreateTime");
                        }else if (j == 23){//报备时间 23
                            value = datac.get("salesAttributionName");
                        }
                        String textValue = (value != null ? value.toString() : "");
                        String text=textValue.replaceAll("\r|\n", "");
                        cell.setCellValue(text);
                    }

//                    cell = row.createCell(22);
//                    cell.setCellStyle(contentCellStyle);
//                    Object value1 = datac.get("reportCreateTime");
//                    String textValue1 = (value1 != null ? value1.toString() : "");
//                    String text1 = textValue1.replaceAll("\r|\n", "");
//                    cell.setCellValue(text1);

//                    cell = row.createCell(23);
//                    cell.setCellStyle(contentCellStyle);
//                    Object value2 = datac.get("salesAttributionName");
//                    String textValue2 = (value2 != null ? value2.toString() : "");
//                    String text2 = textValue2.replaceAll("\r|\n", "");
//                    cell.setCellValue(text2);
                }
                int endRow = startRow + childList.size() - 1;
                //从list中取出第一个和最后一个叶子节点的下标作为开始分组和结束分组的下标
                sheet.groupRow(startRow, endRow); // 分组
                sheet.setRowGroupCollapsed(startRow, true); // 折叠

                for (int j = 0; j < rowLength; j++) {
                    if(j != 22 && j!= 23){
                        sheet.addMergedRegion(new CellRangeAddress(startRow-1, endRow, j, j));
                    }
                }
            }
        }
//        if (param.getDataPattern() != null && !"".equals(param.getDataPattern())){
//            row = sheet.createRow(dataset.size()+1);
//            cell = row.createCell(0);
//            String textValue = (param.getDataPattern() != null ? param.getDataPattern() : "");
//            String text=textValue.replaceAll("\r|\n", "");
//            cell.setCellValue(text);
//            CellRangeAddress region = new CellRangeAddress(dataset.size()+1, dataset.size()+1, 0, rowLength);
//            sheet.addMergedRegion(region);
//        }
        long endTime = System.currentTimeMillis();
        System.out.println("耗费时间：" + (endTime - startTime));
        try {
//            System.out.println("workbook.memory = " + SizeUtils.getObjectSize(workbook));
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Sheet createSheetAndWriteTitle(ExcelExportParam param, String[] headers, Workbook workbook) {
        Sheet sheet = workbook.createSheet(param.getSheetName());
        if(!param.isUserTemplate()){
            sheet.setDefaultColumnWidth(20);
        }
        //得到标题列的样式
        CellStyle titleStyle = this.getCellStyle(workbook, true);

        Drawing patriarch = sheet.createDrawingPatriarch();
        Comment comment = patriarch.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
        comment.setString(this.getRichTextString(param,"注释:" + param.getSheetName()));
        comment.setAuthor("cifi");

        int index = 0;
        if (param.getDataPattern() != null && !"".equals(param.getDataPattern())){
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            String textValue = (param.getDataPattern() != null ? param.getDataPattern() : "");
            String text=textValue.replaceAll("\r|\n", "");
            cell.setCellValue(text);
            CellRangeAddress region = new CellRangeAddress(0, 0, 0, headers.length);
            sheet.addMergedRegion(region);
            index = 1;
        }
        Row titleRow = sheet.createRow(index);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellStyle(titleStyle);
            RichTextString text = this.getRichTextString(param,headers[i]);
            cell.setCellValue(text);
        }
        return sheet;
    }



}




