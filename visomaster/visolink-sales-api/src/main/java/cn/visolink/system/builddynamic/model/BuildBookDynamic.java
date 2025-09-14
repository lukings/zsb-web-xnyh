package cn.visolink.system.builddynamic.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/12/18
 */
@Data
public class BuildBookDynamic {

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "楼盘动态名称")
    private String buildDynamicName;

    @ApiModelProperty(value = "楼盘动态描述")
    private String buildDynamicDesc;

    @ApiModelProperty(value = "项目id")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目区域名称")
    private String projectAreaId;

    @ApiModelProperty(value = "项目区域名称")
    private String projectAreaName;

    @ApiModelProperty(value = "楼盘id")
    private String buildBookId;

    @ApiModelProperty(value = "楼盘名称")
    private String buildBookName;

    @ApiModelProperty(value = "动态类型")
    private String dynamicCode;

    @ApiModelProperty(value = "动态类型名称")
    private String dynamicCodeName;

    @ApiModelProperty(value = "发布时间")
    private String releaseTime;

    @ApiModelProperty(value = "发布状态：1草稿，2已发布")
    private String releaseStatus;

    @ApiModelProperty(value = "是否启用:1已启用，0未启用")
    private String status;

    @ApiModelProperty(value = "是否删除:1已删除，0未删除")
    private String isDel;

    @ApiModelProperty(value = "是否跳转:1 是，0 否")
    private String isJump;

    @ApiModelProperty(value = "跳转类型")
    private String jumpType;

    @ApiModelProperty(value = "跳转类型名称")
    private String jumpTypeName;

    @ApiModelProperty(value = "跳转页面id")
    private String jumpPageId;

    @ApiModelProperty(value = "跳转页面名称")
    private String jumpPageName;

    @ApiModelProperty(value = "跳转参数")
    private String jumpParam;

    @ApiModelProperty(value = "微信订阅描述")
    private String weChatSubscribeDesc;

    @ApiModelProperty(value = "小程序分享描述")
    private String appletsShareDesc;

    @ApiModelProperty(value = "小程序分享图片地址")
    private String appletsShareImageUrl;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建人名称")
    private String creatorName;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "修改人名称")
    private String editorName;

    @ApiModelProperty(value = "修改时间")
    private String editTime;

    @ApiModelProperty(value = "禁用人")
    private String disableUserId;

    @ApiModelProperty(value = "禁用人名称")
    private String disableUserName;

    @ApiModelProperty(value = "禁用时间")
    private String disableTime;

    @ApiModelProperty(value = "状态名称")
    private String statusName;

    private List<String> projectIdList;

    public Object[] toBuildBookDynamicData(){
        return new Object[]{
            getBuildDynamicName(),getBuildBookName(),getProjectAreaName(),
            getProjectName(),getJumpTypeName(),getJumpPageName(),
            getReleaseTime(),getCreateTime(),getCreatorName(),getDisableTime(),
            getDisableUserName(),getStatusName()
        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "楼盘动态名称","楼盘","区域","项目","动态跳转页",
            "落地页名称","发布时间","创建时间","创建人",
            "禁用时间","禁用人","状态"};

}
