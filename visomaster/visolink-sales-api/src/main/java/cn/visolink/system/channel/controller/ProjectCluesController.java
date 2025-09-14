package cn.visolink.system.channel.controller;

import cn.hutool.core.date.DateUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.channel.model.*;
import cn.visolink.system.channel.model.form.*;
import cn.visolink.system.channel.model.vo.*;
import cn.visolink.system.channel.service.ProjectCluesService;
import cn.visolink.system.channel.service.RedistributionService;
import cn.visolink.system.excel.model.ReportCustomerForm;
import cn.visolink.system.fileupload.fileUtil.UploadUtils;
import cn.visolink.system.project.model.vo.ResultProjectVO;
import cn.visolink.utils.CommUtils;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import com.github.pagehelper.PageInfo;
import io.cess.core.Cess;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.ParseException;
import java.util.*;

/**
 * <p>
 * ProjectClues前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2019-08-30
 */
@RestController
@Api(tags = "拓客台账")
@RequestMapping("/system/projectClues")
public class ProjectCluesController {

    @Autowired
    public ProjectCluesService projectCluesService;
    @Autowired
    public RedistributionService redistributionService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 存放图片的根目录
     */
    @Value("${FTP_BASEPATH}")
    private String rootPath;

    @Log("走访客户台账")
    //@CessBody
    @ApiOperation(value = "走访客户台账", notes = "走访客户台账")
    @RequestMapping(value = "/selectAllQudao", method = RequestMethod.POST)
    public PageInfo<ProjectCluesNew> selectAllQudao(@RequestBody Map projectCluesForm) {
        PageInfo<ProjectCluesNew> result = projectCluesService.channelProjectClues(projectCluesForm);
        return result;
    }

    @Log("个人走访客户台账")
    @CessBody
    @ApiOperation(value = "个人走访客户台账", notes = "个人走访客户台账")
    @RequestMapping(value = "/channelProjectCluesByUser", method = RequestMethod.POST)
    public PageInfo<ProjectCluesNew> channelProjectCluesByUser(@RequestBody Map projectCluesForm) {
        PageInfo<ProjectCluesNew> result = projectCluesService.channelProjectCluesByUser(projectCluesForm);
        return result;
    }

    @Log("走访客户台账导出")
    @CessBody
    @ApiOperation(value = "走访客户台账导出", notes = "走访客户台账导出")
    @RequestMapping(value = "/channelProjectCluesExport")
    public void channelProjectCluesExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        projectCluesService.channelProjectCluesExport(request,response, excelForm);
    }

    @Log("走访客户台账导出")
    @CessBody
    @ApiOperation(value = "走访客户台账导出", notes = "走访客户台账导出")
    @RequestMapping(value = "/channelProjectCluesExportNew")
    public String channelProjectCluesExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        return projectCluesService.channelProjectCluesExportNew(request,response, excelForm);
    }

    @Log("个人走访客户台账导出")
    @CessBody
    @ApiOperation(value = "个人走访客户台账导出", notes = "个人走访客户台账导出")
    @RequestMapping(value = "/channelProjectCluesByUserExport")
    public void channelProjectCluesByUserExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String excelForm) {
        projectCluesService.channelProjectCluesByUserExport(request,response, excelForm);
    }

    @Log("报备客户台账")
    //@CessBody
    @ApiOperation(value = "报备客户台账", notes = "报备客户台账")
    @PostMapping("/courtCase")
    public PageInfo<ProjectCluesNew> courtCase(@RequestBody ExcelForm projectCluesForm) throws ParseException {
        PageInfo<ProjectCluesNew> result = projectCluesService.courtCase(projectCluesForm);
        return result;
    }

    @Log("个人报备客户台账")
    @CessBody
    @ApiOperation(value = "个人报备客户台账", notes = "个人报备客户台账")
    @PostMapping("/courtCaseByUser")
    public PageInfo<ProjectCluesNew> courtCaseByUser(@RequestBody ExcelForm projectCluesForm) throws ParseException {
        PageInfo<ProjectCluesNew> result = projectCluesService.courtCaseByUser(projectCluesForm);
        return result;
    }

    @Log("报备客户台账导出方式检定")
    @CessBody
    @ApiOperation(value = "报备客户台账导出方式检定", notes = "报备客户台账导出方式检定")
    @RequestMapping(value = "/getCourtCaseIsExport")
    public ResultBody getCourtCaseIsExport(@RequestBody ExcelForm projectCluesForm) throws ParseException {
        return projectCluesService.getCourtCaseIsExport(projectCluesForm);
    }

    @Log("报备客户台账导出")
    @CessBody
    @ApiOperation(value = "报备客户台账导出", notes = "报备客户台账导出")
    @RequestMapping(value = "/courtCaseExport")
    public void courtCaseExport(HttpServletRequest request, HttpServletResponse response,
                                @RequestBody ExcelForm projectCluesForm) throws ParseException {
        projectCluesService.courtCaseExport(request,response, projectCluesForm);
    }
    @Log("报备客户台账导出-无链路数据")
    @CessBody
    @ApiOperation(value = "报备客户台账导出-无链路数据", notes = "报备客户台账导出-无链路数据")
    @RequestMapping(value = "/courtCaseExportNoLink")
    public void courtCaseExportNoLink(HttpServletRequest request, HttpServletResponse response,
                                @RequestBody ExcelForm projectCluesForm) throws ParseException {
        projectCluesService.courtCaseExportNoLink(request,response, projectCluesForm);
    }

    @Log("报备客户台账导出")
    @CessBody
    @ApiOperation(value = "报备客户台账导出", notes = "报备客户台账导出")
    @RequestMapping(value = "/courtCaseExportNew")
    public String courtCaseExportNew(HttpServletRequest request, HttpServletResponse response,
                                @RequestBody ExcelForm projectCluesForm) throws ParseException {
        return projectCluesService.courtCaseExportNew(request,response, projectCluesForm);
    }

    @Log("个人报备客户台账导出")
    @CessBody
    @ApiOperation(value = "个人报备客户台账导出", notes = "个人报备客户台账导出")
    @RequestMapping(value = "/courtCaseByUserExport")
    public void courtCaseByUserExport(HttpServletRequest request, HttpServletResponse response,
                                @RequestBody ExcelForm projectCluesForm) throws ParseException {
        projectCluesService.courtCaseByUserExport(request,response, projectCluesForm);
    }

    @Log("编辑客户等级")
    @CessBody
    @ApiOperation(value = "编辑客户等级")
    @PostMapping("/updateCustomerGrade")
    public ResultBody updateCustomerGrade(@RequestBody ExcelForm projectCluesForm) {
        return projectCluesService.updateCustomerGrade(projectCluesForm);
    }

    @Log("查询放弃记录")
    //@CessBody
    @ApiOperation(value = "查询放弃记录")
    @PostMapping(value = "/getAbandonRecord")
    public PageInfo<CustomerDistributionRecordsVO> getAbandonRecord(@RequestBody CustomerDistributionRecords customerDistributionRecords) {
        PageInfo<CustomerDistributionRecordsVO> result = projectCluesService.getAbandonRecord(customerDistributionRecords);
        return result;
    }

    @Log("放弃记录导出")
    @CessBody
    @ApiOperation(value = "放弃记录导出", notes = "放弃记录导出")
    @RequestMapping(value = "/abandonRecordExport")
    public void abandonRecordExport(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody CustomerDistributionRecords customerDistributionRecords) {
        projectCluesService.abandonRecordExport(request,response, customerDistributionRecords);
    }

    @Log("放弃记录导出")
    @CessBody
    @ApiOperation(value = "放弃记录导出", notes = "放弃记录导出")
    @RequestMapping(value = "/abandonRecordExportNew")
    public String abandonRecordExportNew(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody CustomerDistributionRecords customerDistributionRecords) {
        return projectCluesService.abandonRecordExportNew(request,response, customerDistributionRecords);
    }

    @Log("查询规则")
    @CessBody
    @ApiOperation(value = "查询规则", notes = "渠道数据")
    @RequestMapping(value = "/selectRuleInfo", method = RequestMethod.POST)
    public ProjectProtectRuleVO selectRuleInfo(@RequestBody ProjectProtectRuleForm projectProtectRuleForm,HttpServletRequest request) {
        projectProtectRuleForm.setAuthCompanyID(request.getHeader("AuthCompanyID"));
        ProjectProtectRuleVO projectProtectRuleVO = projectCluesService.selectRuleInfo(projectProtectRuleForm);
        return projectProtectRuleVO;
    }

    @Log("查询ProtectRule")
    @CessBody
    @ApiOperation(value = "查询渠道的外销规则", notes = "渠道数据")
    @RequestMapping(value = "/selectRuleCompany", method = RequestMethod.POST)
    public List<ProjectProtectRuleVO> selectRuleCompany(@RequestBody ProjectProtectRuleForm projectProtectRuleForm) {
        List<ProjectProtectRuleVO> result = projectCluesService.selectRuleCompany(projectProtectRuleForm);
        return result;
    }

    @Log("规则相关的操作")
    @CessBody
    @ApiOperation(value = "规则相关的操作", notes = "规则数据")
    @RequestMapping(value = "/updateChannelRule", method = RequestMethod.POST)
    public Map updateChannelRule(@RequestBody RuleList te,HttpServletRequest request) {
        te.setAuthCompanyID(request.getHeader("AuthCompanyID"));
        Map map = projectCluesService.updateChannelRule(te);
        return map;
    }


    @Log("重分配接口")
    @CessBody
    @ApiOperation(value = "重分配接口", notes = "")
    @RequestMapping(value = "/redistribution", method = RequestMethod.POST)
    public Map redistribution(@RequestBody Map map) {
        Map resultMap = redistributionService.redistribution(map);
        return resultMap;
    }

    @Log("重分配历史接口")
    @CessBody
    @ApiOperation(value = "重分配历史接口", notes = "")
    @RequestMapping(value = "/ProjectClueDeriveMustAcByToker", method = RequestMethod.POST)
    public PageInfo ProjectClueDeriveMustAcByToker(@RequestBody Map map) {
        PageInfo pageList = redistributionService.ProjectClueDeriveMustAcByToker(map);
        return pageList;
    }

    @Log("渠道重分配信息导出")
    @CessBody
    @ApiOperation(value = "渠道重分配信息导出", notes = "渠道重分配信息导出")
    @PostMapping(value = "/cluesRedistributionRecordExport")
    public void cluesRedistributionRecordExport(HttpServletRequest request, HttpServletResponse response,@RequestBody Map map) {
        redistributionService.cluesRedistributionRecordExport(request,response, map);
    }

    @Log("渠道重分配信息导出(使用异步方式)")
    @CessBody
    @ApiOperation(value = "渠道重分配信息导出", notes = "渠道重分配信息导出")
    @PostMapping(value = "/cluesRedistributionRecordExportNew")
    public String cluesRedistributionRecordExportNew(@RequestBody Map map) {
        return redistributionService.cluesRedistributionRecordExportNew(map);
    }


    @Log("案场重分配历史接口")
    @CessBody
    @ApiOperation(value = "案场重分配历史接口", notes = "")
    @RequestMapping(value = "/queryRedistributionRecord", method = RequestMethod.POST)
    public PageInfo queryRedistributionRecord(@RequestBody Map map) {
        PageInfo pageList = redistributionService.queryRedistributionRecord(map);
        return pageList;
    }

    @Log("案场重分配信息导出")
    @CessBody
    @ApiOperation(value = "案场重分配信息导出", notes = "案场重分配信息导出")
    @PostMapping(value = "/oppRedistributionRecordExport")
    public void oppRedistributionRecordExport(HttpServletRequest request, HttpServletResponse response,@RequestBody Map map) {
        redistributionService.oppRedistributionRecordExport(request,response, map);
    }

    @Log("案场重分配信息导出(使用异步方式)")
    @CessBody
    @ApiOperation(value = "案场重分配信息导出", notes = "案场重分配信息导出")
    @PostMapping(value = "/oppRedistributionRecordExportNew")
    public String oppRedistributionRecordExportNew(@RequestBody Map map) {
        return redistributionService.oppRedistributionRecordExportNew(map);
    }



    @Log("重分配人员选择")
    @CessBody
    @ApiOperation(value = "重分配人员选择", notes = "")
    @RequestMapping(value = "/selectMan", method = RequestMethod.POST)
    public Map selectMan(@RequestBody Map map) {
        List<Map> pageList = redistributionService.selectMan(map);
        Map menusMap = CommUtils.buildTree(pageList);
        return menusMap;
    }

    @Log("详细信息中的基本信息")
    @CessBody
    @ApiOperation(value = "基本信息", notes = "")
    @RequestMapping(value = "/essentialInformation", method = RequestMethod.POST)
    public ProjectCluesNew essentialInformation(@RequestBody Map map) {
        ProjectCluesNew projectCluesVO = projectCluesService.essentialInformation(map);
        return projectCluesVO;
    }

    @Log("详细信息中的基本信息")
    @CessBody
    @ApiOperation(value = "基本信息", notes = "")
    @PostMapping("/oppInformation")
    public OppInformation oppInformation(@RequestBody Map map) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String userid=request.getHeader("userid");
        if(org.apache.commons.lang3.StringUtils.isEmpty(userid)){
            userid= SecurityUtils.getUserId();
        }
        map.put("userId", userid);
        OppInformation projectCluesVO = projectCluesService.oppInformation(map);
        projectCluesVO.setPoolType(map.get("poolType") + "");
        projectCluesVO.setPoolId(map.get("poolId") + "");
        return projectCluesVO;
    }

    @Log("走访客户基本信息")
    @CessBody
    @ApiOperation(value = "走访客户基本信息", notes = "")
    @PostMapping("/cluesInformation")
    public InformationVO cluesInformation(@RequestBody Map map) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String userid=request.getHeader("userid");
        if(org.apache.commons.lang3.StringUtils.isEmpty(userid)){
            userid= SecurityUtils.getUserId();
        }
        map.put("userId", userid);
        InformationVO informationVO = projectCluesService.cluesInformation(map);
        return informationVO;
    }

    @Log("详细信息中的联名客户")
    @CessBody
    @ApiOperation(value = "联名客户", notes = "")
    @RequestMapping(value = "/associatedCustomers", method = RequestMethod.POST)
    public List<Map> associatedCustomers(@RequestBody Map map) {
        List<Map> assMap =  projectCluesService.associatedCustomers(map);
        return assMap;
    }

    @Log("详细信息中的节点信息")
    @CessBody
    @ApiOperation(value = "节点信息", notes = "")
    @RequestMapping(value = "/nodeRecord", method = RequestMethod.POST)
    public List<Map> nodeRecord(@RequestBody Map map) {
        List<Map> assMap =  projectCluesService.nodeRecord(map);
        return assMap;
    }

    @Log("详细信息中的交易信息")
    @CessBody
    @ApiOperation(value = "交易信息", notes = "")
    @RequestMapping(value = "/dealRecord", method = RequestMethod.POST)
    public List<Map> dealRecord(@RequestBody Map map) {
        List<Map> assMap =  projectCluesService.dealRecord(map);
        return assMap;
    }

    @Log("线索详细信息中的节点信息")
    @CessBody
    @ApiOperation(value = "节点信息", notes = "")
    @RequestMapping(value = "/clueNodeRecord", method = RequestMethod.POST)
    public List<Map> clueNodeRecord(@RequestBody Map map) {
        List<Map> assMap =  projectCluesService.clueNodeRecord(map);
        return assMap;
    }

    @Log("详细信息中的跟进记录")
    @CessBody
    @ApiOperation(value = "跟进记录", notes = "")
    @RequestMapping(value = "/followUpRecord", method = RequestMethod.POST)
    public List<Map> followUpRecord(@RequestBody Map map) {
        List<Map> assMap =  projectCluesService.followUpRecord(map);
        return assMap;
    }

    @Log("机会跟进记录")
    @CessBody
    @ApiOperation(value = "机会跟进记录", notes = "")
    @RequestMapping(value = "/followUpOppRecord", method = RequestMethod.POST)
    public List<Map> followUpOppRecord(@RequestBody Map map) {
        List<Map> assMap =  projectCluesService.followUpOppRecord(map);
        return assMap;
    }

    @Log("详细信息中的转介记录")
    @CessBody
    @ApiOperation(value = "转介记录", notes = "")
    @RequestMapping(value = "/toMoveRecord", method = RequestMethod.POST)
    public List<Map> toMoveRecord(@RequestBody Map map) {
        List<Map> assMap =  projectCluesService.toMoveRecord(map);
        return assMap;
    }

    @Log("详细信息中的首访问卷")
    @CessBody
    @ApiOperation(value = "首访问卷", notes = "")
    @RequestMapping(value = "/firstInterviewQuestionnaire", method = RequestMethod.POST)
    public Map firstInterviewQuestionnaire(@RequestBody Map map) {
        Map assMap =  projectCluesService.firstInterviewQuestionnaire(map);
        return assMap;
    }

    @Log("分配项目的时候，项目的规则")
    @CessBody
    @ApiOperation(value = "项目重分配限制", notes = "")
    @RequestMapping(value = "/currProjectInfoSelect", method = RequestMethod.POST)
    public Map currProjectInfoSelect(String projectId) {
        Map assMap =  projectCluesService.currProjectInfoSelect(projectId);
        return assMap;
    }

    @Log("该线索是否可以重分配")
    @CessBody
    @ApiOperation(value = "该线索是否可以重分配", notes = "")
    @RequestMapping(value = "/SelectClueConditionClue", method = RequestMethod.POST)
    public Map SelectClueConditionClue(@RequestBody Map map) {
        Map assMap =  projectCluesService.SelectClueConditionClue(map);
        return assMap;
    }

    @Log("该线索是否可以重分配")
    @CessBody
    @ApiOperation(value = "该线索是否可以重分配", notes = "")
    @RequestMapping(value = "/SelectClueConditionAC", method = RequestMethod.POST)
    public Map SelectClueConditionAC(@RequestBody Map map) {
        Map assMap =  projectCluesService.SelectClueConditionAC(map);
        return assMap;
    }

    @Log("交易信息分组")
    @CessBody
    @ApiOperation(value = "交易信息分组", notes = "")
    @RequestMapping(value = "/transactionInformation", method = RequestMethod.POST)
    public List<Map> transactionInformation(@RequestBody Map map) {
        List<Map> assMap =  projectCluesService.transactionInformation(map);
        return assMap;
    }

    /**
     * 交易信息记录
     * */
    @Log("置业顾问-交易记录")
    @CessBody
    @ApiOperation(value="置业顾问-交易记录",notes = "置业顾问-交易记录")
    @RequestMapping(value = "/getDealRecord",method = RequestMethod.POST)
    public Map getDealRecord(HttpServletRequest request,@RequestBody Map map){
        return projectCluesService.getDealRecord(request,map);
    }

    @Log("渠道信息导出")
    @CessBody
    @ApiOperation(value = "渠道导出", notes = "")
    @RequestMapping(value = "/channelExport")
    public void channelExport(HttpServletRequest request, HttpServletResponse response,@RequestBody String excelForm) {
        projectCluesService.channelExport(request,response, excelForm);
    }

    @Log("渠道信息导出(使用异步方式)")
    @CessBody
    @ApiOperation(value = "渠道导出(使用异步方式)", notes = "")
    @RequestMapping(value = "/channelExportNew")
    public String channelExportNew(@RequestBody String excelForm) {
        return projectCluesService.channelExportNew(excelForm);
    }


    @Log("明细台账的数据")
    @CessBody
    @ApiOperation(value = "明细台账", notes = "")
    @RequestMapping(value = "/queryAllCustmerDetails", method = RequestMethod.POST)
    public PageInfo<ProjectCluesVO> queryAllCustmerDetails(@RequestBody ProjectCluesForm projectCluesForm) {
        PageInfo<ProjectCluesVO> result = projectCluesService.queryAllCustmerDetails(projectCluesForm);
        return result;
    }

    @Log("查询单个人的详细信息")
    @CessBody
    @ApiOperation(value = "审计台账详情", notes = "")
    @RequestMapping(value = "/selectDetailedInformation", method = RequestMethod.POST)
    public Map selectDetailedInformation(@RequestBody Map map) {
        Map mapRedis = redistributionService.selectDetailedInformation(map);
        return mapRedis;
    }

    @Log("修改客户明细")
    @CessBody
    @ApiOperation(value = "修改客户明细", notes = "")
    @RequestMapping(value = "/updateDetaileReport", method = RequestMethod.POST)
    public Map updateDetaileReport(@RequestBody Map map) {
        Map resultMap = redistributionService.updateDetaileReport(map);
        return resultMap;
    }

    @Log("查询认知途径")
    @CessBody
    @ApiOperation(value = "查询认知途径", notes = "")
    @RequestMapping(value = "/getMainMediaList", method = RequestMethod.POST)
    public List<ResultProjectVO> getMainMediaList(@RequestBody Map map) {
        List<ResultProjectVO> resultMap = projectCluesService.getMainMediaList(map);
        return resultMap;
    }

    @Log("查询认知渠道")
    @CessBody
    @ApiOperation(value = "查询认知渠道", notes = "")
    @RequestMapping(value = "/getMainList", method = RequestMethod.POST)
    public List<Map> getMainList(@RequestBody Map map,HttpServletRequest request) {
        String authCompanyId = request.getHeader("AuthCompanyID");
        map.put("AuthCompanyID",authCompanyId);
        List<Map> resultMap = projectCluesService.getMainList(map);
        return resultMap;
    }

    @Log("查询修改日志")
    @CessBody
    @ApiOperation(value = "查询修改日志", notes = "")
    @RequestMapping(value = "/getModificationList", method = RequestMethod.POST)
    public List<Map> getModificationList(@ApiParam(name = "projectClueId", value = "线索id")String projectClueId) {
        List<Map> resultMap = redistributionService.getModification(projectClueId);
        return resultMap;
    }

    @Log("查询修改日志明细")
    @CessBody
    @ApiOperation(value = "查询修改日志明细", notes = "")
    @RequestMapping(value = "/getModificationDetails", method = RequestMethod.POST)
    public List<Map> getModificationDetails(@ApiParam(name = "updateLogId", value = "修改日志id") String updateLogId) {
        List<Map> resultMap = redistributionService.getModificationDetails(updateLogId);
        return resultMap;
    }

    @Log("判断是否有无报备保护期内客户")
    @CessBody
    @ApiOperation(value = "判断是否有无报备保护期内客户", notes = "")
    @RequestMapping(value = "/getIsReport", method = RequestMethod.POST)
    public Map getIsReport(@RequestBody Map map) {
        Map resultMap = projectCluesService.getIsReport(map);
        return resultMap;
    }

    @Log("判断是否有无报备保护期内客户(台账重分配)")
    @CessBody
    @ApiOperation(value = "判断是否有无报备保护期内客户(台账重分配)", notes = "")
    @RequestMapping(value = "/getIsReportList", method = RequestMethod.POST)
    public Map getIsReportList(@RequestBody Map map) {
        Map resultMap = projectCluesService.getIsReportList(map);
        return resultMap;
    }

    @Log("上传文件")
    @CessBody
    @ApiOperation(value = "上传文件", notes = "")
    @RequestMapping(value = "/upLoadFile", method = RequestMethod.POST)
    public Map upLoadFile(@RequestParam("files")MultipartFile[] files){
        Map returnMap = new HashMap();
        try {
            if (files.length == 0) {
                returnMap.put("errmsg", "上传文件失败，没有文件！");
                return returnMap;
            }
            List<String> pathList = new ArrayList<>();
            String dateM = DateUtil.format(new Date(),"yyyyMM");
            for (int i = 0; i < files.length; i++) {
                long size = files[i].getSize();
                //控制台打印文件信息
                System.out.println(files[i].getOriginalFilename()+"-->"+size);
                String picNewName = UploadUtils.generateRandonFileName(files[i].getOriginalFilename());// 通过工具类产生新图片名称，防止重名
                //指明文件上传位置
                File dest = new File(rootPath + "/" + dateM, picNewName);
                //判断文件父目录是否存在
                if(!dest.getParentFile().exists()){
                    dest.getParentFile().mkdir();
                }
                //写入文件
                files[i].transferTo(dest);
                pathList.add(dateM + "/" + picNewName);
            }
            returnMap.put("code", 0);
            returnMap.put("errmsg", "上传文件成功");
            returnMap.put("data", pathList);
            return returnMap;
        } catch (Exception e) {
            throw new BadRequestException(-11_1063, "上传图像失败！", e);
        }
    }

    @Log("置业顾问名片台账")
    @ApiOperation(value = "置业顾问名片台账", notes = "")
    @RequestMapping(value = "/getCardStandingBookList", method = RequestMethod.POST)
    public ResultBody getCardStandingBookList(@RequestBody Map map) {
        return ResultBody.success(projectCluesService.getCardStandingBookList(map));
    }

    @Log("置业顾问名片台账导出")
    @ApiOperation(value = "置业顾问名片台账导出", notes = "")
    @RequestMapping(value = "/cardStandingBookExport")
    public void cardStandingBookExport(HttpServletRequest request, HttpServletResponse response,@RequestBody String param) {
        projectCluesService.cardStandingBookExport(request,response, param);
    }

    @Log("查询全部渠道信息")
    @ApiOperation(value = "查询全部渠道信息", notes = "查询全部渠道信息")
    @RequestMapping(value = "/getAllChannelList", method = RequestMethod.POST)
    public ResultBody getAllChannel(@RequestBody ProjectCluesForm projectCluesForm) {
        List<ProjectCluesVO> result = projectCluesService.getAllChannel(projectCluesForm);
        return ResultBody.success(result);
    }

    @Log("查询全部案场信息")
    @ApiOperation(value = "查询全部案场信息", notes = "查询全部案场信息")
    @RequestMapping(value = "/getAllCaseList", method = RequestMethod.POST)
    public ResultBody getAllCase(@RequestBody ProjectCluesForm projectCluesForm) {
        List<ProjectCluesVO> result = projectCluesService.getAllCase(projectCluesForm);
        return ResultBody.success(result);
    }

    @Log("查询中介公司信息")
    @ApiOperation(value = "查询中介公司信息", notes = "查询中介公司信息")
    @RequestMapping(value = "/getSupplierList", method = RequestMethod.GET)
    public ResultBody getSupplierList() {
        List<Supplier> result = projectCluesService.getSupplierList();
        return ResultBody.success(result);
    }

    @Log("保存/引入中介门店")
    @ApiOperation(value = "保存/引入中介门店", notes = "保存/引入中介门店")
    @RequestMapping(value = "/addIntermediaryStores", method = RequestMethod.POST)
    public ResultBody addIntermediaryStores(@RequestBody IntermediaryStores map) {
        return projectCluesService.addIntermediaryStores(map);
    }

    @Log("更新中介组织状态")
    @ApiOperation(value = "更新中介组织状态", notes = "更新中介组织状态")
    @RequestMapping(value = "/updateIntermediaryRule", method = RequestMethod.POST)
    public ResultBody updateIntermediaryRule(@RequestBody IntermediaryStores map) {
        return projectCluesService.updateIntermediaryRule(map);
    }

    @Log("查询所有中介门店")
    @ApiOperation(value = "查询所有中介门店", notes = "查询所有中介门店")
    @RequestMapping(value = "/getIntermediaryList", method = RequestMethod.GET)
    public ResultBody getIntermediaryList() {
        return projectCluesService.getIntermediaryList();
    }

    @Log("查询线索转机会记录")
    @ApiOperation(value = "查询线索转机会记录", notes = "查询线索转机会记录")
    @RequestMapping(value = "/getClueReferral", method = RequestMethod.POST)
    public ResultBody getClueReferral(@RequestBody ClueReferralForm map) {
        return projectCluesService.getClueReferral(map);
    }

    @Log("查询规则（新）")
    @ApiOperation(value = "查询规则（新）", notes = "查询规则（新）")
    @RequestMapping(value = "/selectProjectRule", method = RequestMethod.POST)
    public ResultBody selectProjectRule(@RequestBody Map map) {
        return projectCluesService.selectProjectRule(map);
    }
    @Log("查询单项目配置规则")
    @ApiOperation(value = "查询单项目配置规则", notes = "查询单项目配置规则")
    @RequestMapping(value = "/selectProjectRuleByProjectId", method = RequestMethod.POST)
    public ResultBody selectProjectRuleByProjectId(@RequestBody Map map) {
        return projectCluesService.selectProjectRuleByProjectId(map);
    }

    @Log("编辑规则（新）")
    @ApiOperation(value = "编辑规则（新）", notes = "编辑规则（新）")
    @RequestMapping(value = "/addOrEditProjectRule", method = RequestMethod.POST)
    public ResultBody addOrEditProjectRule(@RequestBody ProjectRuleDetail map) {
        return projectCluesService.addOrEditProjectRule(map);
    }

    /**
     * 删除规则（新）
     * */
    @Log("删除规则（新）")
    @ApiOperation(value = "删除规则（新）", notes = "删除规则（新）")
    @RequestMapping(value = "/deleteProjectRule", method = RequestMethod.POST)
    public ResultBody deleteProjectRule(@RequestBody ProjectRuleDetail map) {
        return projectCluesService.deleteProjectRule(map);
    }

    @Log("机会转介记录")
    @ApiOperation(value = "机会转介记录", notes = "机会转介记录")
    @RequestMapping(value = "/toOppMoveRecord", method = RequestMethod.POST)
    public ResultBody toOppMoveRecord(@RequestBody Map map) {
        return projectCluesService.toOppMoveRecord(map);
    }

    @Log("线索流转记录")
    @ApiOperation(value = "线索流转记录", notes = "线索流转记录")
    @RequestMapping(value = "/selectReferralClue", method = RequestMethod.POST)
    public ResultBody selectReferralClue(@RequestBody Map map) {
        return projectCluesService.selectReferralClue(map);
    }

    @Log("机会转介记录导出")
    @CessBody
    @ApiOperation(value = "机会转介记录导出", notes = "机会转介记录导出")
    @PostMapping(value = "/toOppMoveRecordExport")
    public void toOppMoveRecordExport(HttpServletRequest request, HttpServletResponse response,@RequestBody Map map) {
        projectCluesService.toOppMoveRecordExport(request,response, map);
    }

    @Log("机会转介记录导出")
    @CessBody
    @ApiOperation(value = "机会转介记录导出", notes = "机会转介记录导出")
    @PostMapping(value = "/toOppMoveRecordExportNew")
    public String toOppMoveRecordExportNew(HttpServletRequest request, HttpServletResponse response,@RequestBody Map map) {
        return projectCluesService.toOppMoveRecordExportNew(request,response, map);
    }

    @Log("线索流转导出")
    @CessBody
    @ApiOperation(value = "线索流转导出", notes = "线索流转导出")
    @PostMapping(value = "/selectReferralClueExport")
    public void selectReferralClueExport(HttpServletRequest request, HttpServletResponse response,@RequestBody Map map) {
        projectCluesService.selectReferralClueExport(request,response, map);
    }

    @Log("申诉记录")
    //@CessBody
    @ApiOperation(value = "申诉记录")
    @PostMapping("/getAppealRecord")
    public ResultBody getAppealRecord(@RequestBody Appeal appeal) {
        return projectCluesService.getAppealRecord(appeal);
    }

    @Log("申诉记录导出")
    @CessBody
    @ApiOperation(value = "申诉记录导出", notes = "申诉记录导出")
    @PostMapping(value = "/AppealRecordExport")
    public void AppealRecordExport(HttpServletRequest request, HttpServletResponse response,@RequestBody Appeal appeal) {
        projectCluesService.AppealRecordExport(request,response, appeal);
    }

    @Log("任务管理台账")
    @CessBody
    @ApiOperation(value = "任务管理台账")
    @PostMapping("/getTaskAccount")
    public ResultBody getTaskAccount(@RequestBody Task task) throws ParseException {
        List<TaskVo>  taskVoList=projectCluesService.getTaskAccount(task);

        return ResultBody.success(new PageInfo<>(taskVoList));
    }

    @Log("任务管理台账导出")
    @CessBody
    @ApiOperation(value = "任务管理台账导出")
    @PostMapping("/taskAccountExport")
    public void taskAccountExport(HttpServletRequest request, HttpServletResponse response, @RequestBody Task task) throws ParseException {
        projectCluesService.taskAccountExport(request, response, task);
    }

    @Log("任务管理台账导出")
    @CessBody
    @ApiOperation(value = "任务管理台账导出")
    @PostMapping("/taskAccountExportNew")
    public String taskAccountExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody Task task) throws ParseException {
        return projectCluesService.taskAccountExportNew(request, response, task);
    }

    @Log("报备失败台账")
    //@CessBody
    @ApiOperation(value = "报备失败台账")
    @PostMapping("/getReportFailAccount")
    public ResultBody getReportFailAccount(@RequestBody ReportFail reportFail) {
        return projectCluesService.getReportFailAccount(reportFail);
    }

    @Log("报备失败台账导出")
    @CessBody
    @ApiOperation(value = "报备失败台账导出")
    @PostMapping("/reportFailAccountExport")
    public void reportFailAccountExport(HttpServletRequest request, HttpServletResponse response, @RequestBody ReportFail reportFail) throws ParseException {
        projectCluesService.reportFailAccountExport(request, response, reportFail);
    }

    @Log("报备失败台账导出")
    @CessBody
    @ApiOperation(value = "报备失败台账导出")
    @PostMapping("/reportFailAccountExportNew")
    public String reportFailAccountExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody ReportFail reportFail) throws ParseException {
        return projectCluesService.reportFailAccountExportNew(request, response, reportFail);
    }

    @ApiOperation(value = "查询申诉详情")
    @PostMapping("/getAppealDetail")
    public ResultBody getAppealDetail(@RequestBody Map map) {
        return projectCluesService.getAppealDetail(map);
    }

    @ApiOperation(value = "查询客户来源")
    @GetMapping("/getCstSource")
    public ResultBody getCstSource() {
        return projectCluesService.getCstSource();
    }

    @ApiOperation(value = "获取客户行业新")
    @GetMapping("/getCstIndustryOneNew")
    public ResultBody getCstIndustryOneNew() {
        return projectCluesService.getCstIndustryOneNew();
    }


    @Log("获取项目下业务员（zs/pro）")
    @ApiOperation(value = "获取项目下业务员（zs/pro）", notes = "获取项目下业务员（zs/pro）")
    @PostMapping(value = "/getProZsSales")
    public ResultBody getProZsSales(@RequestBody Map map) {
        try{
            ResultBody resultBody = projectCluesService.getProZsSales(map);
            return resultBody;
        }catch (Exception e){
            throw new BadRequestException(-11_1065,"获取项目下业务员失败！",e);
        }
    }

    @Log("分配置业顾问")
    @ApiOperation(value = "分配置业顾问", notes = "分配置业顾问")
    @PostMapping(value = "/allocationPropertyConsultant")
    public ResultBody allocationPropertyConsultant(@RequestBody SalesAttributionForm salesAttributionForm) {
        return projectCluesService.allocationPropertyConsultant(salesAttributionForm);
    }

    /**
     * 获取登录人权限内可分配客户的置业顾问
     * */
    @Log("获取登录人权限内可分配客户的置业顾问")
    @ApiOperation(value = "获取登录人权限内可分配客户的置业顾问", notes = "获取登录人权限内可分配客户的置业顾问")
    @PostMapping(value = "/getGlAllocationPropertyConsultantZygw")
    public ResultBody getGlAllocationPropertyConsultantZygw(@RequestBody Map map) {
        return projectCluesService.getGlAllocationPropertyConsultantZygw(map);
    }

    /**
     * 分配置业顾问新
     * */
    @Log("分配置业顾问新")
    @ApiOperation(value = "分配置业顾问新", notes = "分配置业顾问新")
    @PostMapping(value = "/allocationPropertyConsultantNew")
    public ResultBody allocationPropertyConsultantNew(@RequestBody SalesAttributionForm salesAttributionForm) {
        return projectCluesService.allocationPropertyConsultantNew(salesAttributionForm);
    }

    /**
     * 客户转移
     * */
    @Log("客户转移")
    @ApiOperation(value = "客户转移", notes = "客户转移")
    @PostMapping(value = "/transferPropertyConsultant")
    public ResultBody transferPropertyConsultant(@RequestBody SalesAttributionForm salesAttributionForm) {
        return projectCluesService.transferPropertyConsultant(salesAttributionForm);
    }

    /**
     * 公池重分配新
     * */
    @Log("公池重分配新")
    @ApiOperation(value = "公池重分配新", notes = "公池重分配新")
    @PostMapping(value = "/publicPoolDistributionNew")
    public ResultBody publicPoolDistributionNew(@RequestBody SalesAttributionForm salesAttributionForm) {
        return projectCluesService.publicPoolDistributionNew(salesAttributionForm);
    }

    @ApiOperation(value = "获取人员数据查看权限下的小组")
    @PostMapping("/getDataViewPremissionOrgTeam")
    public ResultBody getDataViewPremissionOrgTeam(@RequestBody UserOrgRelForm map) {
        return projectCluesService.getDataViewPremissionOrgTeam(map);
    }

    @ApiOperation(value = "获取项目下的小组")
    @PostMapping("/getOrgTeam")
    public ResultBody getOrgTeam(@RequestBody UserOrgRelForm map) {
        return projectCluesService.getOrgTeam(map);
    }

    @ApiOperation(value = "发起数据权限查看审批")
    @PostMapping("/startDataViewPremission")
    public ResultBody startDataViewPremission(@RequestBody List<UserOrgRelForm> list) {
        return projectCluesService.startDataViewPremission(list);
    }

    @ApiOperation(value = "查询数据权限查看列表")
    @PostMapping("/getDataViewPremissionList")
    public ResultBody getDataViewPremissionList(@RequestBody Map map) {
        return projectCluesService.getDataViewPremissionList(map);
    }

    @ApiOperation(value = "查询数据权限查看详情")
    @PostMapping("/getDataViewPremissionDetail")
    public ResultBody getDataViewPremissionDetail(@RequestBody Map map) {
        return projectCluesService.getDataViewPremissionDetail(map);
    }

    @ApiOperation(value = "获取人员数据查看权限")
    @PostMapping("/getDataViewPremission")
    public ResultBody getDataViewPremission(@RequestBody UserOrgRelForm map) {
        return projectCluesService.getDataViewPremission(map);
    }

    @ApiOperation(value = "获取人员数据查看权限-招商地体包含专员")
    @PostMapping("/getDataViewPremissionZs")
    public ResultBody getDataViewPremissionZs(@RequestBody UserOrgRelForm map) {
        return projectCluesService.getDataViewPremissionZs(map);
    }

    @ApiOperation(value = "管理员数据权限查看设置")
    @PostMapping("/adminSetDataViewPremission")
    public ResultBody adminSetDataViewPremission(@RequestBody List<UserOrgRelForm> list) {
        return projectCluesService.adminSetDataViewPremission(list);
    }

    @ApiOperation(value = "获取数据权限查看的全部权限信息")
    @PostMapping("/getAllDataViewPremissionInfo")
    public ResultBody getAllDataViewPremissionInfo(@RequestBody UserOrgRelForm map) {
        return projectCluesService.getAllDataViewPremissionInfo(map);
    }

    @Log("跟进记录台账")
    @ApiOperation(value = "跟进记录台账")
    @PostMapping("/getFollowUpRecordList")
    public ResultBody getFollowUpRecordList(@RequestBody FollowUpRecordVO followUpRecordVO) throws ParseException {
        return projectCluesService.getFollowUpRecordList(followUpRecordVO);
    }

    @Log("跟进记录台账导出")
    @ApiOperation(value = "跟进记录台账导出")
    @PostMapping("/getFollowUpRecordListExport")
    public void getFollowUpRecordListExport(HttpServletRequest request, HttpServletResponse response, @RequestBody FollowUpRecordVO followUpRecordVO) throws ParseException {
        projectCluesService.getFollowUpRecordListExport(request, response, followUpRecordVO);
    }

    @Log("跟进记录台账导出")
    @ApiOperation(value = "跟进记录台账导出")
    @PostMapping("/getFollowUpRecordListExportNew")
    public String getFollowUpRecordListExportNew(HttpServletRequest request, HttpServletResponse response, @RequestBody FollowUpRecordVO followUpRecordVO) throws ParseException {
        return projectCluesService.getFollowUpRecordListExportNew(request, response, followUpRecordVO);
    }

    @ApiOperation(value = "查询字典")
    @GetMapping("/getCommonDict")
    public ResultBody getCommonDict(@RequestParam("parentCode") String parentCode) {
        return projectCluesService.getCommonDict(parentCode);
    }

    @Log("发起跟进获取当前操作人项目下可跟进最高岗位")
    @ApiOperation(value = "发起跟进获取当前操作人项目下可跟进最高岗位", notes = "发起跟进获取当前操作人项目下可跟进最高岗位")
    @RequestMapping(value = "/getMaxProJobInsFollowUper", method = RequestMethod.POST)
    public ResultBody getMaxProJobInsFollowUper(@RequestBody Map map) {
        return projectCluesService.getMaxProJobInsFollowUper(map);
    }

    /**
     * 批量调整客户状态
     * */
    @Log("批量调整客户状态")
    @ApiOperation(value = "批量调整客户状态")
    @PostMapping("/updateBatchCustomerStatus")
    public ResultBody updateBatchCustomerStatus(@RequestBody ExcelForm projectCluesForm) {
        return projectCluesService.updateBatchCustomerStatus(projectCluesForm);
    }

    /**
     * 导出规则配置
     * */
    @Log("导出规则配置")
    @ApiOperation(value = "导出规则配置", notes = "导出规则配置")
    @PostMapping("/ruleConfigurationExport")
    public void ruleConfigurationExport(HttpServletResponse response) {
        projectCluesService.ruleConfigurationExport(response);
    }

    /**
     * 导出用户权限信息
     * */
    @Log("导出用户权限信息")
    @ApiOperation(value = "导出用户权限信息", notes = "导出用户权限信息")
    @PostMapping("/ExportUserAuthorityAll")
    public void ExportUserAuthorityAll(HttpServletResponse response) {
        projectCluesService.ExportUserAuthorityAll(response);
    }

    /**
     * 常见问题台账
     * */
    @Log("常见问题台账")
    @ApiOperation(value = "常见问题台账", notes = "常见问题台账")
    @PostMapping("/getFeedAskCjList")
    public ResultBody getFeedAskCjList(@RequestBody FeedBackEc feedBackEc){
        return projectCluesService.getFeedAskCjList(feedBackEc);
    }

    /**
     * 常见问题台账
     * */
    @Log("问题反馈台账导出")
    @ApiOperation(value = "问题反馈台账导出", notes = "问题反馈台账导出")
    @PostMapping("/feedBackEdExcel")
    public void feedBackEdExcel(HttpServletRequest request, HttpServletResponse response,
                                      @RequestBody FeedBackEc feedBackEc){
        projectCluesService.feedBackEdExcel(request,response,feedBackEc);
    }

    /**
     * 常见问题详情
     * */
    @Log("常见问题详情")
    @ApiOperation(value = "常见问题详情", notes = "常见问题详情")
    @PostMapping("/getFeedAskCjDetail")
    public ResultBody getFeedAskCjDetail(@RequestBody FeedBackEc feedBackEc){
        return projectCluesService.getFeedAskCjDetail(feedBackEc);
    }

    /**
     * 新增编辑常见问题
     * */
    @Log("新增编辑常见问题")
    @ApiOperation(value = "新增编辑常见问题", notes = "新增编辑常见问题")
    @PostMapping("/addOrEditFeedAskCj")
    public ResultBody addOrEditFeedAskCj(@RequestBody FeedBackEc feedBackEc){
        return projectCluesService.addOrEditFeedAskCj(feedBackEc);
    }

    @Log("任务管理台账详情")
    @CessBody
    @ApiOperation(value = "任务管理台账详情")
    @PostMapping("/getTaskAccountDetail")
    public ResultBody getTaskAccountDetail(@RequestBody TaskQueryVO taskQueryVO) {
        return ResultBody.success(projectCluesService.getTaskAccountDetail(taskQueryVO));
    }
    @Log("外呼系统转线索客户")
    @ApiOperation(value = "外呼系统转线索客户", notes = "外呼系统转线索客户")
    @PostMapping("/callTurnTheClue")
    public ResultBody callTurnTheClue(@RequestBody ReportCustomerForm reportCustomerForm){
        return projectCluesService.callTurnTheClue(reportCustomerForm);
    }



    @Log("外呼系统机器人分配坐席查询接口")
    @ApiOperation(value = "外呼系统机器人分配坐席查询接口", notes = "外呼系统机器人分配坐席查询接口")
    @PostMapping("/getGlAllocationPropertyConsultantZygwCall")
    public ResultBody getGlAllocationPropertyConsultantZygwCall(@RequestBody Map map){
        return projectCluesService.getGlAllocationPropertyConsultantZygwCall(map);
    }


    @Log("外呼系统查询用户是否有机器人权限")
    @ApiOperation(value = "外呼系统查询用户是否有机器人权限", notes = "外呼系统查询用户是否有机器人权限")
    @PostMapping("/isRobotPermissions")
    public ResultBody isRobotPermissions(@RequestBody Map map){
        return projectCluesService.isRobotPermissions(map);
    }


    @Log("机器人添加线索客户")
    @ApiOperation(value = "机器人添加线索客户", notes = "机器人添加线索客户")
    @PostMapping("/callTurnTheClueRobot")
    public ResultBody callTurnTheClueRobot(@RequestBody List<Map> reportCustomerForm){
        return projectCluesService.callTurnTheClueRobot(reportCustomerForm);
    }

    @Log("保存跟进核验记录")
    @ApiOperation(value = "保存跟进核验记录", notes = "保存跟进核验记录")
    @PostMapping("/saveFollowupVerificationRecord")
    public ResultBody saveFollowupVerificationRecord(@RequestBody FollowupVerificationRecordVo verificationVo){
       try {
           if(StringUtils.isEmpty(verificationVo.getFollowRecordId())){
               return ResultBody.error(500,"跟进记录ID不能为空!");
           }
           ResultBody resultBody =  projectCluesService.saveFollowupVerificationRecord(verificationVo);
           return resultBody;
       }catch (Exception e){
           throw new BadRequestException(-11_1065,"发起跟进核验失败！",e);
       }finally {
           redisUtil.del("followupVerification"+verificationVo.getFollowRecordId());
       }
    }

    @Log("核验记录台账")
    @ApiOperation(value = "核验记录台账")
    @PostMapping("/getFollowupVerificationRecordList")
    public ResultBody getFollowupVerificationRecordList(@RequestBody FollowUpRecordVO followUpRecordVO) throws ParseException {
        return projectCluesService.getFollowupVerificationRecordList(followUpRecordVO);
    }

    @Log("核验记录台账导出")
    @ApiOperation(value = "核验记录台账导出")
    @PostMapping("/getFollowupVerificationRecordListExport")
    public void getFollowupVerificationRecordListExport(HttpServletRequest request, HttpServletResponse response, @RequestBody FollowUpRecordVO followUpRecordVO) throws ParseException {
        projectCluesService.getFollowupVerificationRecordListExport(request, response, followUpRecordVO);
    }

    @Log("查询跟进核验记录")
    @ApiOperation(value = "查询跟进核验记录",notes = "查询跟进核验记录")
    @RequestMapping(value = "/getFollowupVerificationRecordOnTab",method = RequestMethod.GET)
    public ResultBody getFollowupVerificationRecordOnTab(@ApiParam(name = "followRecordId",value = "跟进id") String followRecordId){
        return projectCluesService.getFollowupVerificationRecordOnTab(followRecordId);
    }

    @Log("查询跟进核验整改记录")
    @ApiOperation(value = "查询跟进核验整改记录",notes = "查询跟进核验整改记录")
    @RequestMapping(value = "/getFollowupRectificationRecordOnTab",method = RequestMethod.GET)
    public ResultBody getFollowupRectificationRecordOnTab(@ApiParam(name = "followVerificationRecordId",value = "跟进核验id") String followVerificationRecordId){
        return projectCluesService.getFollowupRectificationRecordOnTab(followVerificationRecordId);
    }

    /**
     * 批量调整客户过保及预警时间
     * */
    @Log("批量调整客户过保及预警时间")
    @ApiOperation(value = "批量调整客户过保及预警时间")
    @PostMapping("/updateBatchCustomerExpireDate")
    public ResultBody updateBatchCustomerExpireDate(@RequestBody ExcelForm projectCluesForm) {
        return projectCluesService.updateBatchCustomerExpireDate(projectCluesForm);
    }

    /**
     * 批量调整客户过保及预警时间（支持每个客户单独设置）
     * */
    @Log("批量调整客户过保及预警时间-根据天数算时间")
    @ApiOperation(value = "批量调整客户过保及预警时间-根据天数算时间", notes = "支持每个客户单独设置过保时间和预警时间")
    @PostMapping("/updateBatchCustomerExpireDateByDays")
    public ResultBody updateBatchCustomerExpireDateByDays(@RequestBody BatchUpdateCustomerExpireForm batchUpdateCustomerExpireForm) {
        return projectCluesService.updateBatchCustomerExpireDateByDays(batchUpdateCustomerExpireForm);
    }

    /**
     * 批量设置客户最大跟进次数
     * */
    @Log("批量设置客户最大跟进次数")
    @ApiOperation(value = "批量设置客户最大跟进次数")
    @PostMapping("/updateBatchCustomerMaxFollowUp")
    public ResultBody updateBatchCustomerMaxFollowUp(@RequestBody ExcelForm projectCluesForm) {
        return projectCluesService.updateBatchCustomerMaxFollowUp(projectCluesForm);
    }

    @Log("管理员-解锁客户每日访问上线")
    @ApiOperation(value = "管理员-解锁客户每日访问上线",notes = "管理员-解锁客户每日访问上线")
    @RequestMapping(value = "/unlockInterfaceLimit",method = RequestMethod.GET)
    public ResultBody unlockInterfaceLimit(@ApiParam(name = "userName",value = "用户登录名") String userName){
        return projectCluesService.unlockInterfaceLimit(userName);
    }

    /**
     * 公池客户平均重分配
     * */
    @Log("公池客户平均重分配")
    @ApiOperation(value = "公池客户平均重分配", notes = "公池客户平均重分配")
    @PostMapping(value = "/publicPoolAverageDistribution")
    public ResultBody publicPoolAverageDistribution(@RequestBody SalesAttributionForm salesAttributionForm) {
        return projectCluesService.publicPoolAverageDistribution(salesAttributionForm);
    }

    @Log("公客池平均分配记录")
    @CessBody
    @ApiOperation(value = "公客池平均分配记录", notes = "")
    @RequestMapping(value = "/queryPublicPoolAverageRedistributionRecord", method = RequestMethod.POST)
    public PageInfo queryPublicPoolAverageRedistributionRecord(@RequestBody Map map) {
        PageInfo pageList = projectCluesService.queryPublicPoolAverageRedistributionRecord(map);
        return pageList;
    }

    /**
     * 获取登录人权限内可分配客户的项目
     * */
    @Log("获取登录人权限内可分配客户的项目")
    @ApiOperation(value = "获取登录人权限内可分配客户的项目", notes = "获取登录人权限内可分配客户的项目")
    @PostMapping(value = "/getGlAllocationPropertyConsultantPro")
    public ResultBody getGlAllocationPropertyConsultantPro(@RequestBody Map map) {
        return projectCluesService.getGlAllocationPropertyConsultantPro(map);
    }

    /**
     * 获取登录人权限内可分配客户的项目下的置业顾问
     * */
    @Log("获取登录人权限内可分配客户的项目下的置业顾问")
    @ApiOperation(value = "获取登录人权限内可分配客户的项目下的置业顾问", notes = "获取登录人权限内可分配客户的项目下的置业顾问")
    @PostMapping(value = "/getGlAllocationPropertyConsultantProZygw")
    public ResultBody getGlAllocationPropertyConsultantProZygw(@RequestBody Map map) {
        return projectCluesService.getGlAllocationPropertyConsultantProZygw(map);
    }

    @Log("外呼系统获取客户保护期")
    @ApiOperation(value = "外呼系统获取客户保护期", notes = "外呼系统获取客户保护期")
    @PostMapping("/getCallProjectRule")
    public ResultBody getCallProjectRule(@RequestBody Map map){
        return projectCluesService.getCallProjectRule(map);
    }
}

