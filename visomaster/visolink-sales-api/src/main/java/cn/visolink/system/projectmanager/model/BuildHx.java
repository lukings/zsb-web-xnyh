package cn.visolink.system.projectmanager.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 孙林
 * @date:2019-9-10
 * */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BuildHx implements Serializable {

    @ApiModelProperty(name = "id", value = "户型主键id")
    private String id;

    @ApiModelProperty(name = "hxName", value = "户型名称")
    private String hxName;

    @ApiModelProperty(name = "alias", value = "户型别名")
    private String alias;

    @ApiModelProperty(name = "roomStruCode", value = "房间类型代码")
    private String roomStruCode;

    @ApiModelProperty(name = "roomStru", value = "房间类型")
    private String roomStru;

    @ApiModelProperty(name = "projectId", value = "项目id")
    private String projectId;

    @ApiModelProperty(name = "projectFid", value = "分期项目ID")
    private String projectFid;

    @ApiModelProperty(name = "xHxImg", value = "户型图")
    private String xHxImg;

    @ApiModelProperty(name = "xHxImgUrl", value = "户型图地址")
    private String xHxImgUrl;

    @ApiModelProperty(name = "xHxImgName", value = "户型图名称")
    private String xHxImgName;

    @ApiModelProperty(name = "planUrl", value = "平面图地址")
    private String planUrl;

    @ApiModelProperty(name = "remark", value = "户型描述")
    private String remark;

    @ApiModelProperty(name = "plan", value = "平面图")
    private String plan;

    @ApiModelProperty(name = "tnArea", value = "套内面积")
    private BigDecimal tnArea;

    @ApiModelProperty(name = "bldArea", value = "建筑面积")
    private BigDecimal bldArea;

    @ApiModelProperty(name = "xHxOtherName", value = "户型别名")
    private String xHxOtherName;

    @ApiModelProperty(name = "xArea", value = "区域")
    private String xArea;

    @ApiModelProperty(name = "xHxProductType", value = "户型产品类型")
    private String xHxProductType;

    @ApiModelProperty(name = "xAreaSection", value = "面积段")
    private String xAreaSection;

    @ApiModelProperty(name = "xStaircaseProportion", value = "梯户比")
    private String xStaircaseProportion;

    @ApiModelProperty(name = "xWideNumber", value = "面宽数")
    private String xWideNumber;

    @ApiModelProperty(name = "xScopeenum", value = "使用范围code")
    private Integer xScopeenum;

    @ApiModelProperty(name = "xScope", value = "使用范围")
    private String xScope;

    @ApiModelProperty(name = "xRowid", value = "设计户型Id")
    private String xRowid;

    @ApiModelProperty(name = "createdTime", value = "创建时间")
    private String createdTime;

    @ApiModelProperty(name = "creator", value = "创建人")
    private String creator;

    @ApiModelProperty(name = "modifiedTime", value = "修改时间")
    private String modifiedTime;

    @ApiModelProperty(name = "modified", value = "修改人")
    private String modified;

    @ApiModelProperty(name = "isDel", value = "是否删除 0：正常；1：删除")
    private Integer isDel;

}