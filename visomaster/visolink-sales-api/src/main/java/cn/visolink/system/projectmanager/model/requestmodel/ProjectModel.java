package cn.visolink.system.projectmanager.model.requestmodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/10 11:05
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class ProjectModel extends BaseModel {

    @ApiModelProperty(name = "id", value = "主键id")
    private String id;

    @ApiModelProperty(name = "orgId", value = "组织id")
    private String orgId;

    @ApiModelProperty(name = "pid", value = "父级项目ID")
    private String pid;

    @ApiModelProperty(name = "isstages", value = "是否分期")
    private Integer isstages;

    @ApiModelProperty(name = "projectnum", value = "项目编号")
    private String projectnum;

    @ApiModelProperty(name = "projectcategory", value = "项目分类")
    private String projectcategory;

    @ApiModelProperty(name = "projectstatus", value = "项目状态")
    private String projectstatus;

    @ApiModelProperty(name = "contacts", value = "联系人")
    private String contacts;

    @ApiModelProperty(name = "contactphone", value = "联系电话")
    private String contactphone;

    @ApiModelProperty(name = "longitude", value = "经度")
    private Double longitude;

    @ApiModelProperty(name = "latitude", value = "纬度")
    private Double latitude;

    @ApiModelProperty(name = "openingtime", value = "开盘时间")
    private String openingtime;

    @ApiModelProperty(name = "submittedtime", value = "最早交房")
    private String submittedtime;

    @ApiModelProperty(name = "roomtype", value = "在售房型")
    private String roomtype;

    @ApiModelProperty(name = "averageprice", value = "均价参考")
    private String averageprice;

    @ApiModelProperty(name = "regionallocation", value = "区域位置")
    private String regionallocation;

    @ApiModelProperty(name = "accountstatus", value = "账号状态 1： 正常")
    private Integer accountstatus;

    @ApiModelProperty(name = "loginname", value = "登录名")
    private String loginname;

    @ApiModelProperty(name = "password", value = "登录密码")
    private String password;

    @ApiModelProperty(name = "authcompanyid", value = "认证公司")
    private String authcompanyid;

    @ApiModelProperty(name = "productid", value = "产品id")
    private String productid;

    @ApiModelProperty(name = "creator", value = "创建人")
    private String creator;

    @ApiModelProperty(name = "createtime", value = "创建时间")
    private String createtime;

    @ApiModelProperty(name = "editor", value = "编辑人")
    private String editor;

    @ApiModelProperty(name = "edittime", value = "编辑时间")
    private String edittime;

    @ApiModelProperty(name = "status", value = "状态 0： 禁用 1： 启用")
    private Integer status;

    @ApiModelProperty(name = "isdel", value = "是否删除 0： 不删除 1： 删除")
    private Integer isdel;

    @ApiModelProperty(name = "merchantarea", value = "地区")
    private String merchantarea;

    @ApiModelProperty(name = "projectareamask", value = "地区掩码")
    private String projectareamask;

    @ApiModelProperty(name = "projectcategorymask", value = "分类掩码")
    private String projectcategorymask;

    @ApiModelProperty(name = "province", value = "省")
    private String province;

    @ApiModelProperty(name = "provincecode", value = "省编码")
    private Integer provincecode;

    @ApiModelProperty(name = "city", value = "市")
    private String city;

    @ApiModelProperty(name = "citycode", value = "市编码")
    private Integer citycode;

    @ApiModelProperty(name = "county", value = "区")
    private String county;

    @ApiModelProperty(name = "countycode", value = "区编码")
    private Integer countycode;

    private String projectinfourl;

    private String peripheralmatchingurl;

    private String housetypeurl;

    private Integer sort;

    private String recommendedcommission;

    private String pricedesc;

    private String coverpictureaddress;

    @ApiModelProperty(name = "updateorinsertflag", value = "新增或更新标识  0 新增 1更新")
    private Integer updateorinsertflag;

    @ApiModelProperty(name = "addressdetails", value = "项目详细地址-贝壳树")
    private String addressdetails;

    @ApiModelProperty(name = "bksStatus", value = "贝壳树项目状态 1 启用 0 禁用")
    private Integer bksStatus;

    private String tokerresettype;

    private String anchangresettype;

    @ApiModelProperty(name = "release", value = "0：默认值 1：不同步项目PID标识 2：到访推送默认分期标识")
    private Integer release;

    private String tag;

    private String areaid;

    private String areaname;

    private String tagname;

    private Integer tokerreportstatus;

    private Integer isprintstatus;

    private String keycommission;

    private Integer ischosesaler;

    @ApiModelProperty(name = "trackfrequency", value = "获取轨迹频率")
    private Integer trackfrequency;

    @ApiModelProperty(name = "offlinetime", value = "离线时间")
    private Integer offlinetime;

    @ApiModelProperty(name = "version", value = "同步版本号")
    private Integer version;

    @ApiModelProperty(name = "hkbprojectid", value = "主数据项目ID")
    private String hkbprojectid;

    @ApiModelProperty(name = "hkbprojectfqid", value = "主数据分期ID")
    private String hkbprojectfqid;

    @ApiModelProperty(name = "kindeeprojectid", value = "金蝶项目ID")
    private String kindeeprojectid;

    @ApiModelProperty(name = "kindeeprojectname", value = "金蝶项目名称")
    private String kindeeprojectname;

    @ApiModelProperty(name = "kindeeprojectfid", value = "金蝶分期ID")
    private String kindeeprojectfid;

    @ApiModelProperty(name = "kindeeprojectfname", value = "金蝶分期名称")
    private String kindeeprojectfname;

    @ApiModelProperty(name = "buguid", value = "明源组织ID")
    private String buguid;

    @ApiModelProperty(name = "issyn", value = "是否同步数据")
    private Integer issyn;

    @ApiModelProperty(name = "hotline", value = "项目咨询电话")
    private String hotline;

    @ApiModelProperty(name = "bindProjectName", value = "绑定的项目名称")
    private String bindProjectName;

    @ApiModelProperty(name = "startTime", value = "项目的开始时间")
    private String startTime;

    @ApiModelProperty(name = "endTime", value = "项目的结束时间")
    private String endTime;

    @ApiModelProperty(name = "bindProjectId", value = "绑定的项目id(b_bind_project)")
    private Integer bindProjectId;

    @ApiModelProperty(name = "stageId", value = "分期id")
    private String stageId;

    @ApiModelProperty(name = "buildId", value = "楼栋id")
    private String buildId;

    @ApiModelProperty(name = "groupId", value = "组团id")
    private String groupId;

    @ApiModelProperty(name = "address", value = "地址")
    private String address;

    private String latestnews;

    private StageModel stageModelList;

    private List<GroupModel> groupModelList;

    private List<DesignBuildModel> designBuildModelList;

    @ApiModelProperty(name = "projectPic", value = "项目图片")
    private String projectPic;

    @ApiModelProperty(name = "buildlandArea", value = "建筑用地面积")
    private BigDecimal buildlandArea;

    @ApiModelProperty(name = "totallandArea", value = "总用地面积")
    private BigDecimal totallandArea;

    @ApiModelProperty(name = "upBuildArea", value = "地下建筑面积")
    private BigDecimal upBuildArea;

    @ApiModelProperty(name = "totalbuildArea", value = "总建筑面积")
    private BigDecimal totalbuildArea;

    @ApiModelProperty(name = "onBuildArea", value = "地上建筑面积")
    private BigDecimal onBuildArea;

    @ApiModelProperty(name = "getTime", value = "项目获取日期")
    private String getTime;

    @ApiModelProperty(name = "buildVolumeArea", value = "计容建筑面积")
    private BigDecimal buildVolumeArea;

    @ApiModelProperty(name = "plotRatio", value = "容积率")
    private BigDecimal plotRatio;

    @ApiModelProperty(name = "totalSaleArea", value = "总可售面积")
    private BigDecimal totalSaleArea;

    @ApiModelProperty(name = "projectId", value = "项目id")
    private String projectId;

    @ApiModelProperty(name = "projectName", value = "项目名称")
    private String projectName;

    @ApiModelProperty(name = "fullPath", value = "全路径")
    private String fullPath;

    /**
     *   1: 是新建  2: 是编辑
     */
    private String projectType;
}

