package cn.visolink.system.job.authorization.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 亮
 * @Description: 导出用户与岗位信息
 * @date 2023/10/17 14:33
 */
@Data
public class UsersAndPositions {
    @ApiModelProperty(value = "用户id")
    private String id;
    @ApiModelProperty(value = "岗位id")
    private String postId;
    @ApiModelProperty(value = "组织id")
    private String organizationId;
    @ApiModelProperty(value = "组织名称")
    private String OrgName;
    @ApiModelProperty(value = "组织路径")
    private String FullPath;
    @ApiModelProperty(value = "用户名称")
    private String EmployeeName;
    @ApiModelProperty(value = "岗位名称")
    private String JobName;
    @ApiModelProperty(value = "状态")
    private String Status;
    @ApiModelProperty(value = "登录账号")
    private String UserName;
    @ApiModelProperty(value = "性别")
    private String Gender;
    @ApiModelProperty(value = "手机号")
    private String Mobile;
    @ApiModelProperty(value = "区域")
    private String AreaName;
    @ApiModelProperty(value = "项目")
    private String ProjectName;

    //设置导出列的数据
    public Object[] toData(){
        return new Object[]{
                getId(),
                getPostId(),
//                getOrganizationId(),
                getEmployeeName(),
                getUserName(),
                getGender(),
                getMobile(),
                getStatus(),
                getJobName(),
                getOrgName(),
                getAreaName(),
                getProjectName()
//                getFullPath()
        };
    }


    public String[]  excelTitle =  new String[]{
            "用户编号",
            "岗位编号",
//            "组织编号",
            "用户名称",
            "登录账号",
            "性别",
            "手机号",
            "状态",
            "岗位名称",
            "组织名称",
            "区域名称",
            "项目名称",
//            "组织路径"

    };



}
