package cn.visolink.system.activity.controller;

import cn.visolink.common.redis.RedisUtil;
import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.activity.dao.VowMapper;
import cn.visolink.system.activity.model.vo.ActivityVowDetailVo;
import cn.visolink.system.excel.mapper.ExcelImportMapper;
import cn.visolink.system.excel.model.ExcelExportLog;
import cn.visolink.utils.excel.ExcelExportUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/10/21 17:49
 */
@RestController
@Api(tags = "Vow")
@RequestMapping("/activity/vow")
public class VowController {

    @Autowired(required = false)
    private VowMapper vowMapper;

    @Autowired
    private RedisUtil redisUtil;

    private final static String VOWAWARDINFO="vow_award_info_";


    @Autowired
    private ExcelImportMapper excelImportMapper;

    private DecimalFormat df = new DecimalFormat("#0.00");
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /*
    * 查询许愿明细列表
    * */
    @Log("查询许愿明细列表")
    @ApiOperation(value = "查询许愿明细列表", notes = "查询许愿明细列表")
    @PostMapping("/getVowDetail")
    public ResultBody getVowDetail(@RequestBody Map map){
        PageHelper.startPage((int)map.get("pageNum"),(int)map.get("pageSize"));
        List<Map> list = vowMapper.getVowDetail(map);
        PageInfo pageInfo = new PageInfo(list);
        return ResultBody.success(pageInfo);
    }

    /*
    *设置弹幕
    * */

    @Log("设置弹幕")
    @ApiOperation(value = "设置弹幕", notes = "设置弹幕")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/updateBarrageStatus")
    public ResultBody updateBarrageStatus(@RequestBody Map map){
        vowMapper.updateBarrageStatus(map);
        Object object = redisUtil.get(VOWAWARDINFO+map.get("activity_id")+"_"+map.get("openId"));
        JSONObject jsonObject = JSONObject.parseObject(object.toString());
        jsonObject.put("barrage_status",map.get("type"));
        redisUtil.set(VOWAWARDINFO+map.get("activity_id")+"_"+map.get("openId"),jsonObject);
     return ResultBody.success("修改成功！");
    }

    /*
    * 校验是否已经开奖
    * */
    @Log("校验是否已经开奖")
    @ApiOperation(value = "校验是否已经开奖", notes = "校验是否已经开奖")
    @GetMapping("/checkIsOpenAward")
    public ResultBody checkIsOpenAward(String id){
        return ResultBody.success(vowMapper.checkIsOpenAward(id));
    }

    /*
    * 查询奖品中奖信息
    * */
    @Log("查询奖品中奖信息")
    @ApiOperation(value = "查询奖品中奖信息", notes = "查询奖品中奖信息")
    @GetMapping("/getAwardInfo")
    public ResultBody getAwardInfo(String activity_id){

        return ResultBody.success(vowMapper.getAwardInfo(activity_id));
    }



    /*
    * 设置中奖不中奖
    * */
    @Log("设置中奖不中奖")
    @ApiOperation(value = "设置中奖不中奖", notes = "设置中奖不中奖")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/updateAwardStatus")
    public ResultBody updateAwardStatus(@RequestBody Map map){
/*        int res = vowMapper.checkIsOpenAward(map.get("id")+"");
        if(res==1){
            return ResultBody.error(400,"用户已开奖，无法设置！");
        }*/
        vowMapper.updateAwardStatus(map);
        vowMapper.saveOperateRecord(map);
        Object object = redisUtil.get(VOWAWARDINFO+map.get("activity_id")+"_"+map.get("openId"));
        JSONObject jsonObject = JSONObject.parseObject(object.toString());
        jsonObject.put("award_status",map.get("type"));
        if("1".equals(map.get("type")) || map.get("type").equals(1)){
            Map couponMap = vowMapper.getCouponImageUrl(map.get("award_id").toString());
            jsonObject.put("win_award_id", map.get("award_id"));
            jsonObject.put("win_award_name", map.get("award_name"));
            if(couponMap!=null) {
                jsonObject.put("coupon_image_url", couponMap.get("coupon_image_url"));
                jsonObject.put("coupon_id", couponMap.get("id"));
            }
        }else {
            jsonObject.put("win_award_id",null);
            jsonObject.put("win_award_name",null);
            jsonObject.put("coupon_image_url",null);
            jsonObject.put("coupon_id",null);

        }
        redisUtil.set(VOWAWARDINFO+map.get("activity_id")+"_"+map.get("openId"),jsonObject);
        return ResultBody.success("设置成功！");
    }

    /*
    * 开奖接口
    * */
    @GetMapping("/openAward")
    public void openAward(String activity_id){
          // 首先获取本条许愿是否支持分区
          //int isPartition = vowMapper.getPartition(activity_id);
          //  获取所有奖项ID
          List<Map> awardInfo = vowMapper.getAwardInfo(activity_id);
          if(awardInfo != null) {
              for (Map map : awardInfo) {
                  // 目前不需要分区抽奖 先先写死 1
                  map.put("isPartition", 0);
                  List<Map> winners = vowMapper.getAwardWinners(map);
                  // 批量更新中奖信息
                  vowMapper.updateBrokerAwardStatus(map.get("award_id") + "", map.get("award_name") + "", winners);
                  // 循环更新redis
                  for (Map winner : winners) {
                      Object object = redisUtil.get(VOWAWARDINFO + map.get("activity_id") + "_" + winner.get("openId"));
                      JSONObject jsonObject = JSONObject.parseObject(object.toString());
                      jsonObject.put("award_status", 1);
                      jsonObject.put("win_award_id", map.get("award_id"));
                      jsonObject.put("win_award_name", map.get("award_name"));
                      redisUtil.set(VOWAWARDINFO + map.get("activity_id")+ "_"+ winner.get("openId")  , jsonObject);
                  }
              }
          }

    }



    @Log("许愿明细表导出")
    @CessBody
    @ApiOperation(value = "许愿明细表导出", notes = "")
    @RequestMapping(value = "/activityVowDetailedExport")
    public void activityVowDetailedExport(HttpServletRequest request, HttpServletResponse response, @RequestBody String param) {
        ExcelExportLog excelExportLog = new ExcelExportLog();
        String id = UUID.randomUUID().toString();
        Long nowtime = new Date().getTime();
        excelExportLog.setId(id);
        excelExportLog.setMainType("3");
        excelExportLog.setMainTypeDesc("活动管理");
        excelExportLog.setSubType("H6");
        excelExportLog.setSubTypeDesc("许愿抽奖明细");

        excelExportLog.setIsAsyn("0");
/*        Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);

        excelExportLog.setAreaName(proMap.get("areaName")+"");
        excelExportLog.setProjectId(proMap.get("projectId")+"");
        excelExportLog.setProjectName(proMap.get("projectName")+"");*/



        Map result = new HashMap();
        result = JSONObject.parseObject(param,Map.class);
        excelExportLog.setCreator(result.get("userId")+"");
        String ids = result.get("projectStr")+"";
        if ("".equals(ids)){
            excelExportLog.setAreaName("/");
            excelExportLog.setProjectId("/");
            excelExportLog.setProjectName("/");
        }else{
            String[] arr = ids.split(",");
            List<String> proIdList= new ArrayList<>(Arrays.asList(arr));
            //获取项目集合数据（事业部，项目Id,项目名称）
            Map proMap = excelImportMapper.getAreaNameAndProNames(proIdList);
            excelExportLog.setAreaName(proMap.get("areaName")+"");
            excelExportLog.setProjectId(proMap.get("projectId")+"");
            excelExportLog.setProjectName(proMap.get("projectName")+"");
        }
        List<ActivityVowDetailVo> list = vowMapper.getVowDetailExport(result);
        if (list!=null && list.size()>0){
            String[] headers = list.get(0).getActivityHelpTitle();
            ArrayList<Object[]> dataset = new ArrayList<>();
            String isAll = result.get("isAll")+"";
            if("1".equals(isAll)){
                excelExportLog.setExportType("2");
            }else{
                excelExportLog.setExportType("1");
            }
            int rowNo = 0;
            for (ActivityVowDetailVo ac:list) {
                rowNo++;
                ac.setRowNum(rowNo + "");
                Object[] oArray = ac.toActivityHelpData(isAll);
                dataset.add(oArray);
            }
            try{
                ExcelExportUtil excelExportUtil = new ExcelExportUtil();
                excelExportUtil.exportExcel("活动许愿明细",headers,dataset,"活动许愿明细",response,null);
                excelExportLog.setExportStatus("2");
            }catch (Exception e){
                e.printStackTrace();

                excelExportLog.setExportStatus("3");
                excelExportLog.setExceptionMessage(e.getMessage());
            }
        }
        Long export = new Date().getTime();
        Long exporttime = export-nowtime;
        String exportTime =df.format(Double.valueOf(exporttime+"")/1000);
        excelExportLog.setWaitTime("0");
        excelExportLog.setExportTime(exportTime);
        excelExportLog.setDownLoadTime(sf.format(new Date()));
        excelExportLog.setIsDown("1");
        //保存任务表
        excelImportMapper.addExcelExportLog(excelExportLog);
    }
}
