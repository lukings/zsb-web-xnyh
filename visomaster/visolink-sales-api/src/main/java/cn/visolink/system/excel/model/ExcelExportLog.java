package cn.visolink.system.excel.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ExcelExportLog
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/8/21 18:02
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "导出任务记录对象", description = "导出任务记录表")
public class ExcelExportLog extends Page implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "集团事业部")
    private String areaName;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "任务大类")
    private String mainType;

    @ApiModelProperty(value = "任务大类描述")
    private String mainTypeDesc;

    @ApiModelProperty(value = "任务子类")
    private String subType;

    @ApiModelProperty(value = "任务子类描述")
    private String subTypeDesc;

    @ApiModelProperty(value = "导出类型（1：隐号 2：全号 3：无限制）")
    private String exportType;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "创建人ID")
    private String creator;

    @ApiModelProperty(value = "下载路径")
    private String downLoadUrl;

    @ApiModelProperty(value = "执行sql")
    private String doSql;

    @ApiModelProperty(value = "导出状态（1：未导出 2：导出成功 3：导出失败）")
    private String exportStatus;

    @ApiModelProperty(value = "导出状态描述")
    private String exportStatusDesc;

    @ApiModelProperty(value = "下载时间")
    private String downLoadTime;

    @ApiModelProperty(value = "是否下载")
    private String isDown = "0";

    @ApiModelProperty(value = "修改时间")
    private String editTime;

    @ApiModelProperty(value = "修改人ID")
    private String editor;

    @ApiModelProperty(value = "是否删除（0：否 1：是）")
    private String isDel;

    @ApiModelProperty(value = "关联项目ID集合")
    private List<String> projectList;

    @ApiModelProperty(value = "关联项目ID")
    private String projectIds;

    @ApiModelProperty(value = "任务子类ID集合")
    private List<String> subTypeList;

    @ApiModelProperty(value = "导出状态")
    private List<String> exportStatusList;

    @ApiModelProperty(value = "导出类型")
    private List<String> exportTypeList;

    @ApiModelProperty(value = "时间查询类型（1：导出时间 2：下载时间）")
    private String reportTime;

    @ApiModelProperty(value = "查询开始时间")
    private String date1;

    @ApiModelProperty(value = "查询结束时间")
    private String date2;

    @ApiModelProperty(value = "当前登录人账号/用户名")
    private String userName;

    @ApiModelProperty(value = "是否异步导出")
    private String isAsyn;

    @ApiModelProperty(value = "异步导出等待时长")
    private String waitTime;

    @ApiModelProperty(value = "导出时长")
    private String exportTime;

    @ApiModelProperty(value = "错误信息")
    private String exceptionMessage;

}
