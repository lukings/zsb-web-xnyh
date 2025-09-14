package cn.visolink.system.customerlabel.controller;

import cn.visolink.common.permission.RequiresPermission;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.customerlabel.bo.BatchCustomerLabelBO;
import cn.visolink.system.customerlabel.bo.CustomerLabelBO;
import cn.visolink.system.customerlabel.entity.CustomerLabel;
import cn.visolink.system.customerlabel.service.ICustomerLabelService;
import cn.visolink.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 个人标签信息表 前端控制器
 *
 * @author system
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/customerLabel")
@Api(tags = "个人标签管理")
@RequiresPermission("个人标签管理")
public class CustomerLabelController {

    @Autowired
    private ICustomerLabelService customerLabelService;

    /**
     * 新增标签
     */
    @PostMapping("/add")
    @ApiOperation("新增个人标签")
    @RequiresPermission("新增标签")
    public ResultBody<Boolean> addCustomerLabel(@Validated @RequestBody CustomerLabelBO customerLabelBO, 
                                               HttpServletRequest request) {
        try {
            // 获取当前登录用户
            String currentUser = request.getHeader("userid");
            if(StringUtils.isEmpty(currentUser)){
                currentUser = SecurityUtils.getUserId();
            }

            if (currentUser == null) {
                return ResultBody.error(401, "用户未登录");
            }

            // 创建标签实体
            CustomerLabel customerLabel = new CustomerLabel();
            customerLabel.setCustomerLabel(customerLabelBO.getCustomerLabel());

            // 调用服务新增标签
            boolean result = customerLabelService.addCustomerLabel(customerLabel, currentUser);
            
            if (result) {
                return ResultBody.success(true);
            } else {
                return ResultBody.error(500, "新增标签失败");
            }
        } catch (Exception e) {
            log.error("新增标签异常", e);
            return ResultBody.error(500, "新增标签异常：" + e.getMessage());
        }
    }

    /**
     * 批量新增标签
     */
    @PostMapping("/batchAdd")
    @ApiOperation("批量新增个人标签")
    @RequiresPermission("新增标签")
    public ResultBody<Integer> batchAddCustomerLabels(@Validated @RequestBody BatchCustomerLabelBO batchCustomerLabelBO, 
                                                     HttpServletRequest request) {
        try {
            // 获取当前登录用户
            String currentUser = request.getHeader("userid");
            if(StringUtils.isEmpty(currentUser)){
                currentUser = SecurityUtils.getUserId();
            }

            if (currentUser == null) {
                return ResultBody.error(401, "用户未登录");
            }

            // 转换为标签实体列表
            List<CustomerLabel> customerLabels = batchCustomerLabelBO.getCustomerLabels().stream()
                    .map(labelName -> {
                        CustomerLabel customerLabel = new CustomerLabel();
                        customerLabel.setCustomerLabel(labelName);
                        return customerLabel;
                    })
                    .collect(Collectors.toList());

            // 调用服务批量新增标签
            int successCount = customerLabelService.batchAddCustomerLabels(customerLabels, currentUser);
            
            return ResultBody.success(successCount);
        } catch (Exception e) {
            log.error("批量新增标签异常", e);
            return ResultBody.error(500, "批量新增标签异常：" + e.getMessage());
        }
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除个人标签")
    @RequiresPermission("删除标签")
    public ResultBody<Boolean> deleteCustomerLabel(@PathVariable String id, 
                                                  HttpServletRequest request) {
        try {
            // 获取当前登录用户
            String currentUser = request.getHeader("userid");
            if(StringUtils.isEmpty(currentUser)){
                currentUser = SecurityUtils.getUserId();
            }

            if (currentUser == null) {
                return ResultBody.error(401, "用户未登录");
            }

            // 调用服务删除标签
            boolean result = customerLabelService.deleteCustomerLabel(id, currentUser);
            
            if (result) {
                return ResultBody.success(true);
            } else {
                return ResultBody.error(500, "删除标签失败");
            }
        } catch (Exception e) {
            log.error("删除标签异常", e);
            return ResultBody.error(500, "删除标签异常：" + e.getMessage());
        }
    }

    /**
     * 根据创建人查询自己创建的标签
     */
    @GetMapping("/listByCreator")
    @ApiOperation("查询当前用户创建的标签列表")
    @RequiresPermission("查询标签")
    public ResultBody<List<CustomerLabel>> getCustomerLabelsByCreator(HttpServletRequest request) {
        try {
            // 获取当前登录用户
            String currentUser = request.getHeader("userid");
            if(StringUtils.isEmpty(currentUser)){
                currentUser = SecurityUtils.getUserId();
            }

            if (currentUser == null) {
                return ResultBody.error(401, "用户未登录");
            }

            // 调用服务查询标签列表
            List<CustomerLabel> customerLabels = customerLabelService.getCustomerLabelsByCreator(currentUser);
            
            return ResultBody.success(customerLabels);
        } catch (Exception e) {
            log.error("查询标签列表异常", e);
            return ResultBody.error(500, "查询标签列表异常：" + e.getMessage());
        }
    }
}
