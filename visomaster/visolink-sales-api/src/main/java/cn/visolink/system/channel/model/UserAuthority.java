package cn.visolink.system.channel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 亮
 * @Description: 用户权限导出实体类
 * @date 2023/10/18 10:01
 */
@Data
public class UserAuthority {

    @ApiModelProperty(value = "用户ID")
    private String id;

    @ApiModelProperty(value = "账号")
    private String UserName;

    @ApiModelProperty(value = "用户姓名")
    private String EmployeeName;

    @ApiModelProperty(value = "性别")
    private String Gender;

    @ApiModelProperty(value = "电话号")
    private String Mobile;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "账号类型")
    private String AccountType  ;

    @ApiModelProperty(value = "权限集合名称")
    private String OrgNameList;

    @ApiModelProperty(value = "有效期类型（0 有效期 0 不限时间 ）")
    private String isNoTime;

    @ApiModelProperty(value = "有效期开始时间")
    private String startTime;

    @ApiModelProperty(value = "有效期结束时间")
    private String endTime;

    @ApiModelProperty(value = "名称全号隐号")
    private String isNameShow;

    @ApiModelProperty(value = "联系方式全号隐号")
    private String isMobileShow;

    //设置导出列的数据
    public Object[] toData(){
        return new Object[]{
                getEmployeeName(),
                getUserName(),
                getGender(),
                getMobile(),
                getProjectName(),
                getOrgNameList(),
                getIsNoTime(),
                getStartTime(),
                getEndTime(),
                getIsNameShow(),
                getIsMobileShow()
        };
    }


    public String[]  excelTitle =  new String[]{
            "用户名称",
            "登录账号",
            "性别",
            "手机号",
            "项目名称",
            "权限集合名称",
            "有效期类型",
            "有效期开始时间",
            "有效期结束时间",
            "名称全号隐号",
            "联系方式全号隐号"

    };
}
