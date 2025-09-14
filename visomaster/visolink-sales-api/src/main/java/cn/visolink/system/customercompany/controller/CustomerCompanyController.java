package cn.visolink.system.customercompany.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.customercompany.dao.CustomerCompanyMapper;
import cn.visolink.utils.CommUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@Api("客户公司")
@RequestMapping("/customer")
public class CustomerCompanyController {

    @Autowired(required = false)
    private CustomerCompanyMapper customerCompanyMapper;


    @Log(" 获取客户公司列表")
    @ApiOperation(value = "获取客户公司列表",httpMethod = "POST")
    @PostMapping("/getCompanyList")
    public ResultBody getCustomerCompanyList(@RequestBody Map map){
        PageHelper.startPage(Integer.parseInt(map.get("pageNum")+""),Integer.parseInt(map.get("pageSize")+""));
        List<Map> list = customerCompanyMapper.getCustomerCompanyList(map);
        PageInfo pageInfo = new PageInfo(list);
        return ResultBody.success(pageInfo);
    }


    @Log("新增客户公司")
    @ApiOperation(value = "新增客户公司",httpMethod = "POST")
    @PostMapping("/saveCustomerCompany")
    public ResultBody saveCustomerCompany(@RequestBody Map map){
        String accountId =UUID.randomUUID().toString();
        String commonJobId =UUID.randomUUID().toString();
        String jobId =UUID.randomUUID().toString();
        String authCompanyId =UUID.randomUUID().toString();
        String jobName =map.get("companyName").toString()+"管理员";
        String jobCode =ToFirstChar(jobName).toUpperCase();
        String username =ToFirstChar(map.get("companyName").toString()).toUpperCase()+"-admin";
        map.put("accountId",accountId);
        map.put("commonJobId",commonJobId);
        map.put("jobId",jobId);
        map.put("authCompanyId",authCompanyId);
        map.put("username",username);
        map.put("jobName",jobName);
        map.put("jobCode",jobCode);
        map.put("companyCode",ToFirstChar(map.get("companyName").toString()).toUpperCase());
          int res = customerCompanyMapper.saveCustomerCompany(map);
          customerCompanyMapper.initUserAdmin(map);
          Map user = new HashMap();
          user.put("username",username);
          user.put("password",1);
          if(1==1){
              return ResultBody.success(user);
          }else{
              return ResultBody.success("保存失败！");
          }
    }

    @Log("修改客户公司")
    @ApiOperation(value = "修改客户公司",httpMethod = "POST")
    @PostMapping("/updateCustomerCompanyById")
    public ResultBody updateCustomerCompany(@RequestBody Map map){
        customerCompanyMapper.updateCustomerCompanyById(map);
        return ResultBody.success("修改成功！");
    }

    @Log("删除客户公司")
    @ApiOperation(value = "删除客户公司",httpMethod = "POST")
    @PostMapping("/deleteCustomerCompanyById")
    public ResultBody delCustomerCompany(@RequestBody Map map){
        return ResultBody.success(customerCompanyMapper.deleteCustomerCompanyById(map));
    }

    @Log("查询所有产品")
    @ApiOperation(value = "获取所有产品",httpMethod = "POST")
    @PostMapping("/getAllProduct")
    public ResultBody getAllProduct(@RequestBody Map map){
        List<Map> list = customerCompanyMapper.getAllProduct();
        Map menusMap = CommUtils.buildTree(list);
        List<Map> productCompanyRelList = customerCompanyMapper.getAllProductByCompanyID(map.get("companyId")+"");
        menusMap.put("productRel",productCompanyRelList);
        return ResultBody.success(menusMap);
    }

    @Log("保存公司授权信息")
    @ApiOperation(value = "保存公司授权信息",httpMethod = "POST")
    @PostMapping("/saveProductCompanys")
    public ResultBody saveProductCompanys(@RequestBody Map map){
        String args = map.get("productList")+"";
        args=args.replace(" ","");
        System.out.println(args);
        String[] strs = args.substring(1,args.length()-1).split(",");
        List list = Arrays.asList(strs);

        customerCompanyMapper.saveProductCompanys(map.get("companyId")+"",list);
        return ResultBody.success("添加成功！");
    }

    public static String ToFirstChar(String chinese){
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();  //转为单个字符
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }
}
