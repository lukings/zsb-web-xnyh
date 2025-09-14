package cn.visolink.message.controller;

import cn.visolink.message.service.MessageService;
import cn.visolink.system.channel.service.ProjectCluesService;
import cn.visolink.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;
    import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.visolink.logs.aop.log.Log;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * Message前端控制器
 * </p>
 *
 * @author 吴要光
 * @since 2019-09-03
 */
@RestController
@Api(tags = "Message")
@RequestMapping("/message")
public class MessageController {

    @Autowired
    public MessageService messageService;
    @Autowired
    public ProjectCluesService projectCluesService;

    @Log("跟进预警")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/selectMessage", method = RequestMethod.POST)
    public List<Map> getMessagePage(){
        messageService.selectMessage();
        return null;
    }

    @Log("跟进预警")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/deleteHD", method = RequestMethod.POST)
    public List<Map> deleteHD(){
        messageService.deleteHD();

        return null;
    }

    @Log("跟进逾期掉客户池")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/addProPool", method = RequestMethod.GET)
    public String addProPool(){
        messageService.addProPool();
        return "调用成功！！";
    }

    @Log("新增超时未消费项目")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/getTimeoutWarning", method = RequestMethod.GET)
    public String getTimeoutWarning(){
        messageService.getTimeoutWarning();
        return "调用成功！！";
    }

    @Log("项目池掉入区域池")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/addAreaPool", method = RequestMethod.GET)
    public String addAreaPool(){
        return messageService.addAreaPool();
    }
    @Log("区域池掉入全国池")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/addNationalPool", method = RequestMethod.GET)
    public String addNationalPool(){
        return messageService.addNationalPool();
    }

    @Log("每月强制丢失客户")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/monthlyDelCst", method = RequestMethod.GET)
    public String monthlyDelCst(){
        messageService.monthlyDelCst();
        return "调用成功！！";
    }

    @Log("转介超时自动驳回")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/automaticRejection", method = RequestMethod.GET)
    public String automaticRejection(){
        messageService.automaticRejection();
        return "调用成功！！";
    }

    @Log("查询是否有需要插入信息表的数据")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/startUpRuleExpiredTask", method = RequestMethod.POST)
    public List<Map> startUpRuleExpiredTask(){
        messageService.startUpRuleExpired();
        return null;
    }

    @Log("查询是否有需要插入信息表的数据")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/startUpRuleEnableTask", method = RequestMethod.POST)
    public List<Map> startUpRuleEnableTask(){
        messageService.startUpRuleEnable();
        return null;
    }

    @Log("设置公客池客户逾期标签")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/saveCustomerPoolDateLabel", method = RequestMethod.POST)
    public String saveCustomerPoolDateLabel(){
        messageService.saveCustomerPoolDateLabel();
        return "调用成功！！";
    }

    @Log("公客池获取客户待办逾期")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/automaticObtainApprove", method = RequestMethod.POST)
    public String automaticObtainApprove(){
        messageService.automaticObtainApprove();
        return "调用成功！！";
    }

    @Log("客户跟进超时自动驳回")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/automaticFollowUpRejection", method = RequestMethod.POST)
    public String automaticFollowUpRejection(){
        messageService.automaticFollowUpRejection();
        return "调用成功！！";
    }

    @Log("相似客户审批超时自动驳回")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/automaticSimilarCustomerReportRejection", method = RequestMethod.POST)
    public String automaticSimilarCustomerReportRejection(){
        messageService.automaticSimilarCustomerReportRejection();
        return "调用成功！！";
    }

    @Log("报备客户客户统计")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/saveCustomerOppStatistics", method = RequestMethod.POST)
    public String saveCustomerOppStatistics(){
        messageService.saveCustomerOppStatistics();
        return "调用成功！！";
    }

    @Log("公客池客户统计")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/saveCustomerPoolStatistics", method = RequestMethod.POST)
    public String saveCustomerPoolStatistics(){
        messageService.saveCustomerPoolStatistics();
        return "调用成功！！";
    }

    @Log("同步报备链路")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/synCustomerReportRecord", method = RequestMethod.GET)
    public String synCustomerReportRecord(){
        messageService.synCustomerReportRecord();
        return "调用成功！！";
    }

    @Log("导出任务执行")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/createExcelDownLoad", method = RequestMethod.GET)
    public String createExcelDownLoad(){
        return messageService.createExcelDownLoad();
    }

    @Log("删除导出附件")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/delExcelFile", method = RequestMethod.GET)
    public String delExcelFile(){
        messageService.delExcelFile();
        return "调用成功！！";
    }

    @Log("初始化数据1130版本")
    @CessBody
    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/initHistoryDate1130", method = RequestMethod.GET)
    public String initHistoryDate1130(){
        return messageService.initHistoryDate1130();
    }

//    @Log("初始化集团转化率数据")
//    @CessBody
//    @ApiOperation(value = "", notes = "")
//    @RequestMapping(value = "/initJtproConversionRateDate", method = RequestMethod.GET)
//    public String initJtproConversionRateDate(){
//        return messageService.initJtproConversionRateDate();
//    }

}

