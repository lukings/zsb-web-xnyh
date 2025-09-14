package cn.visolink.system.print.service.impl;

import cn.visolink.system.print.dao.PrintDao;
import cn.visolink.system.print.model.PrintField;
import cn.visolink.system.print.model.PrintInstall;
import cn.visolink.system.print.model.PrintTemplate;
import cn.visolink.system.print.service.PrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PrintServiceImpl
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/3/9 11:09
 **/
@Service
public class PrintServiceImpl implements PrintService {

    @Autowired
    private PrintDao printDao;

    @Override
    public PrintInstall findTemplateField() {
        //基本信息字段
        List<PrintField> basicField = new ArrayList<>();
        //客户信息字段
        List<PrintField> custField = new ArrayList<>();
        //全民经纪人字段
        List<PrintField> brokerField = new ArrayList<>();
        //查询所有字段
        List<PrintField> allField = printDao.getAllField();
        if (allField!=null && allField.size()>0){
            for (PrintField p:allField) {
                //基本
                if ("1".equals(p.getFieldType())){
                    basicField.add(p);
                }else if ("2".equals(p.getFieldType())){
                    custField.add(p);
                }else if ("3".equals(p.getFieldType())){
                    brokerField.add(p);
                }
            }
        }
        //查询打印方式
        List<Map> printCountDist = printDao.getPrintCountDist();
        PrintInstall printInstall = new PrintInstall();
        printInstall.setBasicField(basicField);
        printInstall.setCustField(custField);
        printInstall.setBrokerField(brokerField);
        printInstall.setPrintCount(printCountDist);
        return printInstall;
    }

    @Override
    public PrintTemplate findTemplateByProId(String projectId,String printType) {
        PrintTemplate printTemplate = printDao.findTemplateByProId(projectId,printType);
        //判断是否有模板，没有的话查询默认模板
        if (printTemplate==null || printTemplate.getID()==null){
            printTemplate = printDao.findTemplateNoProId(printType);
        }
        return printTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTemplate(PrintTemplate printTemplate) {
        printDao.addTemplate(printTemplate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(PrintTemplate printTemplate) {
        printDao.updateTemplate(printTemplate);
    }
}
