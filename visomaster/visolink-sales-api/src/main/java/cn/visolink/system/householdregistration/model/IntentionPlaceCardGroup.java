package cn.visolink.system.householdregistration.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName IntentionPlaceCardGroup
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/7/29 20:28
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value="排卡分组", description="排卡分组")
public class IntentionPlaceCardGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "装户活动id")
    private String activityId;

    @ApiModelProperty(value = "项目id")
    private String projectid;

    @ApiModelProperty(value = "分期id")
    private String projectFid;

    @ApiModelProperty(value = "分期名称")
    private String projectFname;

    @ApiModelProperty(value = "排卡类型（1：小卡 2：大卡）")
    private String cardType;

    @ApiModelProperty(value = "开盘批次Code")
    private String openingBatch;

    @ApiModelProperty(value = "开盘批次")
    private String openingBatchName;

    @ApiModelProperty(value = "排卡分组Code")
    private String cardGrouping;

    @ApiModelProperty(value = "排卡分组")
    private String cardGroupingName;

    @ApiModelProperty(value = "是否删除")
    private String isdel;

    @ApiModelProperty(value = "创建时间")
    private String createtime;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "修改时间")
    private String edittime;

    @ApiModelProperty(value = "修改人")
    private String editor;

}
