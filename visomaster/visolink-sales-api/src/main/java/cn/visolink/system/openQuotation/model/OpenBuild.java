package cn.visolink.system.openQuotation.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName OpenBuild
 * @Author wanggang
 * @Description //楼栋
 * @Date 2020/7/29 11:16
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="楼栋", description="楼栋")
public class OpenBuild implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "项目id")
    private String projectid;

    @ApiModelProperty(value = "项目名称")
    private String projectname;

    @ApiModelProperty(value = "分期id")
    private String projectidFq;

    @ApiModelProperty(value = "分期名称")
    private String projectnameFq;

    @ApiModelProperty(value = "明源楼栋ID")
    private String buildguid;

    @ApiModelProperty(value = "楼栋名称")
    private String buildname;

    @ApiModelProperty(value = "楼栋均价")
    private String buildprice;

    @ApiModelProperty(value = "排序")
    private String orderby;

    @ApiModelProperty(value = "楼栋状态")
    private String status;

    @ApiModelProperty(value = "单元集合")
    private List<OpenUnit> unitList;
}
