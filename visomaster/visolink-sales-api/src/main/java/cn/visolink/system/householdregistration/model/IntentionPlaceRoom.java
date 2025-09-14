package cn.visolink.system.householdregistration.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName IntentionPlaceRoom
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/7/29 11:27
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("a_intention_place_build")
@ApiModel(value="装户房间", description="装户房间表")
public class IntentionPlaceRoom implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "装户活动id")
    private String activityId;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "项目id")
    private String projectid;

    @ApiModelProperty(value = "项目名称")
    private String projectname;

    @ApiModelProperty(value = "分期id")
    private String projectidFq;

    @ApiModelProperty(value = "分期名称")
    private String projectnameFq;

    @ApiModelProperty(value = "装户楼栋ID")
    private String placebuildId;

    @ApiModelProperty(value = "明源楼栋ID")
    private String buildguid;

    @ApiModelProperty(value = "楼栋名称")
    private String buildname;

    @ApiModelProperty(value = "房间ID")
    private String roomguid;

    @ApiModelProperty(value = "房间名称")
    private String roomname;

    @ApiModelProperty(value = "房间全称")
    private String roominfo;

    @ApiModelProperty(value = "房间编号")
    private int roomno;

    @ApiModelProperty(value = "单元")
    private String unitno;

    @ApiModelProperty(value = "楼层")
    private String floor;

    @ApiModelProperty(value = "楼层号")
    private int floorno;

    @ApiModelProperty(value = "销售状态(1:待售 2:已售 3:销控)")
    private String saleStatus;

    @ApiModelProperty(value = "房间类型")
    private String roomType;

    @ApiModelProperty(value = "朝向")
    private String exposure;

    @ApiModelProperty(value = "户型图")
    private String exposureImg;

    @ApiModelProperty(value = "户型")
    private String houseType;

    @ApiModelProperty(value = "户型Id")
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

    @ApiModelProperty(value = "配置总价")
    private String configureTotalPrice;

    @ApiModelProperty(value = "装户总数")
    private String totalCount;

    @ApiModelProperty(value = "装户总数调整值")
    private String totalDiffCount;

    @ApiModelProperty(value = "装户调整后总数")
    private String totalEditCount;

    @ApiModelProperty(value = "一选数量")
    private String oneCount;

    @ApiModelProperty(value = "一选数量调整值")
    private String oneDiffCount;

    @ApiModelProperty(value = "一选调整后数量")
    private String oneEditCount;

    @ApiModelProperty(value = "二选数量")
    private String twoCount;

    @ApiModelProperty(value = "二选数量调整值")
    private String twoDiffCount;

    @ApiModelProperty(value = "二选调整后数量")
    private String twoEditCount;

    @ApiModelProperty(value = "三选数量")
    private String threeCount;

    @ApiModelProperty(value = "三选数量调整值")
    private String threeDiffCount;

    @ApiModelProperty(value = "三选调整后数量")
    private String threeEditCount;

    @ApiModelProperty(value = "四选数量")
    private String fourCount;

    @ApiModelProperty(value = "四选数量调整值")
    private String fourDiffCount;

    @ApiModelProperty(value = "四选调整后数量")
    private String fourEditCount;

    @ApiModelProperty(value = "五选数量")
    private String fiveCount;

    @ApiModelProperty(value = "五选数量调整值")
    private String fiveDiffCount;

    @ApiModelProperty(value = "五选调整后数量")
    private String fiveEditCount;

    @ApiModelProperty(value = "排序")
    private String orderby;

    @ApiModelProperty(value = "房间状态（1可装户，0不可装户）")
    private String status;

    @ApiModelProperty(value = "是否删除（1已删除，0未删除）")
    private String isdel;

    @ApiModelProperty(value = "创建时间")
    private String createtime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "修改时间")
    private String edittime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    private Boolean isChecked;

    private String isEdit;//是否调整（1:调整 2：未调整）
}
