package cn.visolink.system.allpeople.examine.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.allpeople.contentManagement.model.*;
import cn.visolink.system.allpeople.examine.service.ExamineService;
import cn.visolink.system.channel.model.form.CluesForm;
import cn.visolink.system.channel.model.vo.CustomerDistributionRecordsVO;
import cn.visolink.system.channel.model.vo.OppInformation;
import cn.visolink.system.channel.model.vo.ProjectCluesNew;
import cn.visolink.system.channel.model.vo.PublicpoolVO;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ExamineController
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/1/14 19:01
 **/
@RestController
@RequestMapping("/examine")
public class ExamineController {
    @Autowired
    private ExamineService examineService;

    @Log("获取列表")
    @ApiOperation(value = "获取列表")
    @GetMapping("/getDataList")
    public ResultBody getDataList(){
//        ProjectRuleForm projectRuleForm = new ProjectRuleForm();
        List<Map> list = new ArrayList<>();
        Map examine = new HashMap();
        examine.put("code","111");
        examine.put("name","111");
//        ChannelContract channelContract = new ChannelContract();
//        List<ChannelContract> channelContracts = new ArrayList<>();
//        channelContracts.add(channelContract);
//        examine.setChannelContracts(channelContracts);
        list.add(examine);
//        projectRuleForm.setProjectID("-1");
//        projectRuleForm.setProjectRuleDetails(list);
//        return ResultBody.success(new PageInfo<>(list));
        return ResultBody.success(list);
    }

    @Log("获取详情")
    @ApiOperation(value = "获取详情")
    @GetMapping("/getDataDetail")
    public ResultBody getDataDetail(){
        BuildingBasic examine = new BuildingBasic();
        List<BuildBookPhoto> BuildingPhotos = new ArrayList<>();
        BuildBookPhoto buildBookPhoto = new BuildBookPhoto();
        BuildingPhotos.add(buildBookPhoto);
        List<BuildBookProduct> buildBookProducts = new ArrayList<>();
        BuildBookProduct buildBookProduct = new BuildBookProduct();
        List<Apartment> apartmentList = new ArrayList<>();
        Apartment apartment = new Apartment();
        apartmentList.add(apartment);
        buildBookProduct.setApartmentList(apartmentList);
        buildBookProducts.add(buildBookProduct);
        examine.setBuildBookProducts(buildBookProducts);
        examine.setBuildingPhotos(BuildingPhotos);
//        ChannelContract channelContract = new ChannelContract();
//        List<ChannelContract> channelContracts = new ArrayList<>();
//        channelContracts.add(channelContract);
//        examine.setChannelContracts(channelContracts);
        return ResultBody.success(examine);
    }

    /**
     * @Author wanggang
     * @Description //获取待审核列表
     * @Date 19:03 2020/1/14
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    @Log("获取待审核列表")
    @ApiOperation(value = "获取待审核列表")
    @PostMapping("/getExamineList")
    public ResultBody getExamineList(@RequestBody Map paramMap){

        return ResultBody.success(examineService.getExamineList(paramMap));
    }
    /**
     * @Author wanggang
     * @Description //更新经纪人状态
     * @Date 19:06 2020/1/14
     * @Param []
     * @return cn.visolink.exception.ResultBody
     **/
    @Log("更新经纪人状态")
    @ApiOperation(value = "更新经纪人状态")
    @PostMapping("/updatePeople")
    public ResultBody updatePeople(@RequestBody Map paramMap){
        try {
            examineService.updatePeople(paramMap);
        }catch (Exception e){
            e.printStackTrace();
            return ResultBody.error(-1,"更新经纪人异常！！");
        }
        return ResultBody.success("更新成功");
    }
    @Log("获取所有城市")
    @ApiOperation(value = "获取所有城市")
    @RequestMapping("/getCitys")
    public ResultBody getCitys(){
        return ResultBody.success(examineService.getCitys());
    }

    @Log("获取所有注册项目")
    @ApiOperation(value = "获取所有注册项目")
    @RequestMapping("/getAllProject")
    public ResultBody getAllProject(){
        return ResultBody.success(examineService.getAllProject());
    }

    @Log("获取经纪人列表")
    @ApiOperation(value = "获取经纪人列表")
    @PostMapping("/getBrokerUserList")
    public ResultBody getBrokerUserList(@RequestBody Map paramMap){
        return ResultBody.success(examineService.getBrokerUserList(paramMap));
    }

    @Log("经纪人导出")
    @CessBody
    @ApiOperation(value = "经纪人导出", notes = "")
    @PostMapping(value = "/brokerUserExport")
    public void brokerUserExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String exportVo) {
        examineService.brokerUserExport(request,response, exportVo);
    }

    @Log("经纪人导出(使用异步方式)")
    @CessBody
    @ApiOperation(value = "经纪人导出(使用异步方式)", notes = "")
    @PostMapping(value = "/brokerUserExportNew")
    public String brokerUserExportNew(@RequestBody String exportVo) {
        return examineService.brokerUserExportNew(exportVo);
    }
    @Log("获取经纪人信息")
    @ApiOperation(value = "获取经纪人信息")
    @PostMapping("/getBrokerUser")
    public ResultBody getBrokerUser(@RequestBody Map paramMap){
        return ResultBody.success(examineService.getBrokerUser(paramMap));
    }

    @Log("获取经纪人推荐客户信息")
    @ApiOperation(value = "获取经纪人推荐客户信息")
    @PostMapping("/getBrokerUserCustomer")
    public ResultBody getBrokerUserCustomer(@RequestBody Map paramMap){
        return ResultBody.success(examineService.getBrokerUserCustomer(paramMap));
    }
    @Log("获取经纪人变更日志信息")
    @ApiOperation(value = "获取经纪人变更日志信息")
    @PostMapping("/getBrokerUserEditLog")
    public ResultBody getBrokerUserEditLog(@RequestBody Map paramMap){
        return ResultBody.success(examineService.getBrokerUserEditLog(paramMap));
    }

    @Log("获取所有项目")
    @ApiOperation(value = "获取所有项目")
    @RequestMapping("/getProjectList")
    public ResultBody getProjectList(@RequestBody Map paramMap){
        return ResultBody.success(examineService.getProjectList(paramMap));
    }

    @Log("获取申请渠道商列表")
    @ApiOperation(value = "获取申请渠道商列表")
    @PostMapping("/channelRegistration")
    public ResultBody channelRegistration(@RequestBody Map paramMap){
        return ResultBody.success(examineService.channelRegistration(paramMap));
    }

    @Log("申请渠道商列表导出")
    @CessBody
    @ApiOperation(value = "申请渠道商列表导出", notes = "")
    @PostMapping(value = "/channelRegistrationExport")
    public void channelRegistrationExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String exportVo) {
        examineService.channelRegistrationExport(request,response, exportVo);
    }

    @Log("获取渠道商详情")
    @ApiOperation(value = "获取渠道商详情")
    @PostMapping("/channelDetail")
    public ResultBody channelDetail(@RequestBody Map paramMap){
        return ResultBody.success(examineService.channelDetail(paramMap));
    }

    @Log("获取渠道商列表")
    @ApiOperation(value = "获取渠道商列表")
    @PostMapping("/channelManagement")
    public ResultBody channelManagement(@RequestBody Map paramMap){
        return ResultBody.success(examineService.channelManagement(paramMap));
    }

    @Log("渠道商列表导出")
    @CessBody
    @ApiOperation(value = "渠道商列表导出", notes = "")
    @PostMapping(value = "/channelManagementExport")
    public void channelManagementExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String exportVo) {
        examineService.channelManagementExport(request,response, exportVo);
    }

    @Log("加入黑名单")
    @ApiOperation(value = "加入黑名单")
    @GetMapping("/addblacklist")
    public ResultBody addblacklist(@RequestParam(value ="type",required=false) String type,
                                   @RequestParam(value ="id",required=false) String id){
        return ResultBody.success(examineService.addblacklist(id,type));
    }

    @Log("渠道商审核")
    @ApiOperation(value = "渠道商审核")
    @PostMapping("/channelAudit")
    public ResultBody channelAudit(@RequestBody Map paramMap){
        return ResultBody.success(examineService.channelAudit(paramMap));
    }
}
