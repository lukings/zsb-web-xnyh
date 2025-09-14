package cn.visolink.system.householdregistration.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName IntentionPlaceEditResult
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/8/3 10:18
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="IntentionPlaceEditResult", description="装户结果调整记录表")
public class IntentionPlaceEditResult implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "活动id")
    private String activityId;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "装户项目ID")
    private String projectid;

    @ApiModelProperty(value = "项目名称")
    private String projectname;

    @ApiModelProperty(value = "所属楼盘ID")
    private String buildbookId;

    @ApiModelProperty(value = "所属楼盘")
    private String buildbookName;

    @ApiModelProperty(value = "分期项目ID")
    private String projectidFq;

    @ApiModelProperty(value = "分期项目名称")
    private String projectnameFq;

    @ApiModelProperty(value = "楼栋ID")
    private String buildguid;

    @ApiModelProperty(value = "楼栋名称")
    private String buildname;

    @ApiModelProperty(value = "房间ID")
    private String roomguid;

    @ApiModelProperty(value = "房间编号")
    private String roomno;

    @ApiModelProperty(value = "房间名称")
    private String roomname;

    @ApiModelProperty(value = "销售状态")
    private String saleStatus;

    @ApiModelProperty(value = "房间类型")
    private String roomType;

    @ApiModelProperty(value = "户型")
    private String houseType;

    @ApiModelProperty(value = "户型ID")
    private String houseTypeId;

    @ApiModelProperty(value = "建筑面积")
    private String floorArea;

    @ApiModelProperty(value = "套内面积")
    private String insideArea;

    @ApiModelProperty(value = "建筑单价")
    private String floorPrice;

    @ApiModelProperty(value = "套内单价")
    private String insidePrice;

    @ApiModelProperty(value = "总价")
    private String totalPrice;

    @ApiModelProperty(value = "意向客户总数")
    private String totalCount;

    @ApiModelProperty(value = "一选客户数")
    private String oneCount;

    @ApiModelProperty(value = "二选客户数")
    private String twoCount;

    @ApiModelProperty(value = "三选客户数")
    private String threeCount;

    @ApiModelProperty(value = "四选客户数")
    private String fourCount;

    @ApiModelProperty(value = "五选客户数")
    private String fiveCount;

    @ApiModelProperty(value = "调整时间")
    private String edit_time;

    @ApiModelProperty(value = "操作人ID")
    private String editor;

    @ApiModelProperty(value = "是否发布旭客家 1已发布,0未发布")
    private String ispublish2cst;

    @ApiModelProperty(value = "是否发布置业顾问 1已发布,0未发布")
    private String ispublish2gw;

}
