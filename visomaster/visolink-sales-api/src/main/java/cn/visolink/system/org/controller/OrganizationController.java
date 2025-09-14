package cn.visolink.system.org.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.org.model.form.OrganizationForm;
import cn.visolink.system.org.model.vo.OrganizationVO;
import cn.visolink.system.org.service.OrganizationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * Organization前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
@RestController
@Api(tags = "组织机构管理")
@RequestMapping("/org")
public class OrganizationController {

    @Autowired
    public OrganizationService organizationService;


    /**
     * 保存单条
     *
     * @param param 保存参数
     * @return 是否添加成功
     */
    @Log("保存数据到Organization")
    @CessBody
    @ApiOperation(value = "保存", notes = "保存数据到Organization")
    @PostMapping(value = "/add")
    public OrganizationForm addOrganization(@RequestBody(required = false)  OrganizationForm param,HttpServletRequest request) {
        String authCompanyId = request.getHeader("AuthCompanyID");
        param.setAuthCompanyId(authCompanyId);
        param.setUserName(request.getHeader("username"));
        OrganizationForm result = organizationService.save(param);
        return result;
    }

      /**
     * 更新(根据主键id更新)
     *
     * @param param 修改参数
     * @return 是否更改成功
     */
    @Log("更新(根据主键id更新)Organization")
    @CessBody
    @ApiOperation(value = "更新数据", notes = "根据主键id更新Organization数据")
    @PostMapping(value = "/updateById")
    public Integer updateOrganizationById(@RequestBody(required = false) OrganizationForm param,HttpServletRequest request) {
        param.setUserName(request.getHeader("username"));
        Integer result = organizationService.updateById(param);
        return result;
    }

    /**
     * 删除(根据主键id伪删除)
     *
     * @param id 主键id
     * @return 是否删除成功
     */

    @Log("删除(根据主键id伪删除)Organization")
    @CessBody
    @ApiOperation(value = "删除数据", notes = "根据主键id伪删除Organization数据")
    @PostMapping(value = "/deleteById")
    public Integer deleteOrganizationById(String id) {
        Integer result = organizationService.deleteById(id);
        return result;
    }

    /**
     * 根据主键id查询单条
     *
     * @param id 主键id
     * @return 查询结果
     */
    @Log("根据主键id查询单条Organization")
    @CessBody
    @ApiOperation(value = "获取单条数据", notes = "根据主键id获取Organization数据")
    @RequestMapping(value = "/getById", method = RequestMethod.POST)
    public OrganizationVO getOrganizationById(String id) {
        OrganizationVO result = organizationService.selectById(id);
        return result;
    }

    /**
     * 查询全部
     *
     * @param param 查询条件
     * @return 查询结果
     */

    @Log("查询全部Organization s")
    @CessBody
    @ApiOperation(value = "全部查询", notes = "查询Organization全部数据")
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST)
    public Object getOrganizationAll(@RequestBody  OrganizationForm param, HttpServletRequest request) {
        return organizationService.selectAll(param);
    }

    @Log("根据pid查询该组织下的树数据")
    @CessBody
    @ApiOperation(value = "全部查询", notes = "根据pid查询该组织下的树数据")
    @GetMapping(value = "/treeDataList")
    public Map treeDataList(@RequestParam("pid") String pid) {
        return organizationService.treeDataList(pid);
    }

    /**
     * 查询全部
     *
     * @param param 查询条件
     * @return 查询结果
     */

    @Log("查询全部Organization s")
    @CessBody
    @ApiOperation(value = "全部查询", notes = "查询Organization全部数据")
    @RequestMapping(value = "/queryAll2", method = RequestMethod.POST)
    public Object getOrganizationAll2(@RequestBody  OrganizationForm param, HttpServletRequest request) {
        return organizationService.selectAll2(param);
    }


    /**
     * 分页查询
     *
     * @param param 查询条件
     * @return 查询结果
     */

    @Log("分页查询Organization")
    @CessBody
    @ApiOperation(value = "分页查询", notes = "分页查询Organization全部数据")
    @RequestMapping(value = "/queryPage", method = RequestMethod.POST)
    public IPage<OrganizationVO> getOrganizationPage(@RequestBody(required = false) OrganizationForm param) {
        IPage<OrganizationVO> result = organizationService.selectPage(param);
        return result;
    }

    /**
     * 组织机构启用/禁用
     * @return 是否更改成功
     */
    @Log("组织机构启用/禁用")
    @CessBody
    @ApiOperation(value = "组织机构启用/禁用", notes = "启用:1,禁用:0")
    @PostMapping(value = "/updateStatusById")
    public Integer updateStatusById(@RequestBody OrganizationForm organizationForm) {
        Integer result = organizationService.updateStatusById(organizationForm);
        return 0;
    }

    /**
     * 查询子组织（根据上级ID）
     */
    @Log("查询子组织")
    @CessBody
    @ApiOperation(value = "根据父id查询子组织集合")
    @RequestMapping(value = "/queryChildsOrg",method = RequestMethod.POST)
    public Map queryChildsOrg(@RequestBody Map map) {
        Map resultMap = organizationService.queryChildOrgs(map);
        return resultMap;
    }

    @PostMapping("/getUserDataRole")
    public Map getUserDataRole(@RequestBody Map map){
        return organizationService.getUserDataRole(map);
    }

}

